package com.fortunate.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fortunate.constant.CachePrefixConstant;
import com.fortunate.constant.MessageConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.context.ToolContext;
import com.fortunate.dto.*;
import com.fortunate.entity.*;
import com.fortunate.exception.AddressBookBusinessException;
import com.fortunate.exception.OrderBusinessException;
import com.fortunate.exception.ShoppingCartBusinessException;
import com.fortunate.mapper.*;
import com.fortunate.query.OrdersPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.service.OrderService;
import com.fortunate.service.PickupCodeService;
import com.fortunate.vo.DishVO;
import com.fortunate.vo.OrderSubmitVO;
import com.fortunate.vo.OrderVO;
import com.fortunate.websocket.WebSocketServer;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fortunate.constant.CachePrefixConstant.USER_ORDER;

/**
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final Snowflake snowflake = IdUtil.getSnowflake(1, 1);
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WebSocketServer webSocketServer;
    private final PickupCodeService pickupCodeService;
    private final UserMapper usersMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 条件搜索订单
     *
     * @param ordersPageQueryDTO 订单搜索条件
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);


        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = this.getOrderVOList(page);

        orderVOList = orderVOList.stream().map(orderVO -> {
            //根据用户id获取用户详情
            User userDB = usersMapper.getUserById(orderVO.getUserId());
            orderVO.setUserName(userDB.getName());
            if (orderVO.getOrderType() == 1) {
                orderVO.setOrderTypeName("外卖");
            } else {
                orderVO.setOrderTypeName("堂食");
            }
            return orderVO;
        }).collect(Collectors.toList());
        return new PageResult(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (CollectionUtils.isEmpty(ordersList)) return orderVOList;

        for (Orders orders : ordersList) {
            // 将共同字段赋值到OrderVO对象中
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);

            // 返回字符串格式：宫保鸡丁*3;可口可乐300ml*1;
            String orderDishes = this.getOrderDishesStr(orders);

            // 将订单菜品信息封装到orderVO对象中
            orderVO.setOrderDishes(orderDishes);
            orderVOList.add(orderVO);
        }

        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders 订单对象
     * @return 菜品信息字符串
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接位字符串（返回格式：[宫保鸡丁*3;, 可口可乐300ml*1;]）
        List<String> orderDishList = orderDetailList.stream()
                .map(od -> od.getName() + "*" + od.getNumber() + ";")
                .collect(Collectors.toList());

        return String.join("", orderDishList);
    }

    /**
     * 订单详情
     *
     * @param id 订单id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        // 根据订单id查询订单
        Orders order = orderMapper.getOrderById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
        String orderDishesStr = this.getOrderDishesStr(order);

        // 将该订单及其详情封装到OrderVO对象中
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDishes(orderDishesStr);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 确认接单
     *
     * @param id 订单id
     */
    @Override
    public void confirm(Long id) {
        Orders orderById = orderMapper.getOrderById(id);
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.CONFIRMED) // 将订单状态修改为：已接单
                .build();
        orderMapper.update(orders);
        clearUserOrderCache(orderById.getUserId());
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO 订单拒单DTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 根据订单id查询订单
        Orders orderDB = orderMapper.getOrderById(ordersRejectionDTO.getId());

        // 商家可以拒单的情况：1、订单存在 2、订单状态为2（待接单）
        if (orderDB == null || !Orders.TO_BE_CONFIRMED.equals(orderDB.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_REJECTION_ERROR);
        }

        // 查看订单支付状态，如果用户已经支付了订单，商家拒单则需要进行退款操作
        Integer payStatus = orderDB.getStatus();
        if (Orders.PAID.equals(payStatus)) {
            log.info("取消订单，申请退款==> {}", "用户已经支付了订单，商家拒单则需要进行退款操作");
        }
        //清除缓存
        clearUserOrderCache(orderDB.getUserId());

        // 根据订单id更新订单状态、拒单原因、取消时间
        Orders order = Orders.builder()
                .id(orderDB.getId())
                .status(Orders.CANCELLED) // 订单状态：已取消
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();

        orderMapper.update(order);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO 订单取消DTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // 根据订单id查询订单
        Orders orderDB = orderMapper.getOrderById(ordersCancelDTO.getId());

        // 判断订单是否存在
        if (orderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 判断订单状态是否为已支付，已支付的订单取消的话需要进行退款操作
        Integer payStatus = orderDB.getStatus();
        if (Orders.PAID.equals(payStatus)) {
            log.info("取消订单，申请退款==> {}", "订单状态为已支付，已支付的订单取消的话需要进行退款操作");
        }
        //清除缓存
        clearUserOrderCache(orderDB.getUserId());

        // 根据订单id更新订单状态、取消原因、取消时间
        Orders order = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED) // 订单状态：已取消
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    /**
     * 派送订单
     *
     * @param id 订单id
     */
    @Override
    public void delivery(Long id) {
        // 根据订单id查询订单
        Orders orderDB = orderMapper.getOrderById(id);

        // 判断订单是否存在，并且状态为已接单（3）
        if (orderDB == null || !Orders.CONFIRMED.equals(orderDB.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_DELIVERY_ERROR);
        }

        // 更新订单状态为：派送中
        Orders order = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS) // 订单状态：派送中
                .build();
        orderMapper.update(order);
        clearUserOrderCache(orderDB.getUserId());
    }

    /**
     * 完成订单
     *
     * @param id 订单id
     */
    @Override
    public void complete(Long id) {
        // 根据订单id查询订单
        Orders orderDB = orderMapper.getOrderById(id);

        // 判断订单是否存在
        if (orderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_COMPLETE_ERROR);
        }

        boolean isValidStatus;
        // 根据订单类型和实际状态值进行判断
        if (Objects.equals(orderDB.getOrderType(), Orders.DINE_IN)) {
            // 堂食订单：允许在状态8
            isValidStatus = orderDB.getStatus().equals(Orders.DINE_IN_ORDER);
        } else {
            // 外卖订单：必须处于派送中状态（状态4）
            isValidStatus = Orders.DELIVERY_IN_PROGRESS.equals(orderDB.getStatus());
        }

        if (!isValidStatus) {
            throw new OrderBusinessException(MessageConstant.ORDER_COMPLETE_ERROR);
        }
        //清除缓存
        clearUserOrderCache(orderDB.getUserId());

        // 更新订单状态为已完成，并设置完成时间
        Orders order = Orders.builder()
                .id(orderDB.getId())
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();

        orderMapper.update(order);
    }

    /**
     * 订单支付
     *
     * @param ordersSubmitDTO 订单信息
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getUserId() != null ? BaseContext.getUserId() : (Long) ToolContext.get(ToolContext.USER_ID);

        /* 购物车为空 */
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (CollectionUtils.isEmpty(shoppingCartList)) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //删除redis热度菜品缓存
        Set<String> keys = redisTemplate.keys(CachePrefixConstant.DISH_KEY + "null" + "*");
        redisTemplate.delete(keys);
        //清理用户历史订单记录缓存
        clearUserOrderCache(userId);
        // 根据订单类型分支处理
        if (Objects.equals(ordersSubmitDTO.getOrderType(), Orders.DINE_IN)) {
            return handleDineInOrder(ordersSubmitDTO, userId, shoppingCartList);
        }

        return handleTakeawayOrder(ordersSubmitDTO, userId, shoppingCartList);
    }

    /**
     * 处理堂食订单
     *
     * @param dto
     * @param userId
     * @param cartList
     * @return
     */
    private OrderSubmitVO handleDineInOrder(OrdersSubmitDTO dto, Long userId, List<ShoppingCart> cartList) {
        // 生成取餐号
        String pickupNumber = pickupCodeService.generatePickupCode();

        // 创建订单
        Orders orders = createDineInOrder(dto, userId, pickupNumber);
        orderMapper.insert(orders);

        // 插入订单明细
        this.insertOrderDetails(orders, cartList);

        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        return buildSubmitResult(orders, pickupNumber);
    }

    private OrderSubmitVO handleTakeawayOrder(OrdersSubmitDTO dto, Long userId, List<ShoppingCart> cartList) {
        // 校验地址信息
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 创建带地址的订单
        Orders orders = createTakeawayOrder(dto, userId, addressBook);
        orderMapper.insert(orders);

        // 插入订单明细
        insertOrderDetails(orders, cartList);

        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        return buildSubmitResult(orders, null);
    }

    /**
     * 生成堂食订单
     *
     * @param dto          订单提交数据
     * @param userId       当前登录微信用户id
     * @param pickupNumber 取餐号
     * @return 订单对象
     */
    private Orders createDineInOrder(OrdersSubmitDTO dto, Long userId, String pickupNumber) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto, orders);

        // 设置堂食特有字段
        orders.setNumber(String.valueOf(snowflake.nextId()));
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPickupNumber(pickupNumber);

        // 清除非必要字段
        orders.setAddressBookId(null);
        orders.setAddress(null);
        orders.setConsignee(null);
        orders.setPhone(null);

        return orders;
    }

    /**
     * 生成外卖订单
     *
     * @param dto         订单提交数据
     * @param userId      当前登录微信用户id
     * @param addressBook 地址簿对象
     * @return 订单对象
     */
    private Orders createTakeawayOrder(OrdersSubmitDTO dto, Long userId, AddressBook addressBook) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto, orders);
        //拼接 地址
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        // 设置外卖配送信息
        orders.setNumber(String.valueOf(snowflake.nextId()));
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(address);
        orders.setConsignee(addressBook.getConsignee());

        return orders;
    }

    private OrderSubmitVO buildSubmitResult(Orders orders, String pickupNumber) {
        OrderSubmitVO vo = new OrderSubmitVO();
        vo.setId(orders.getId());
        vo.setOrderNumber(orders.getNumber());
        vo.setOrderAmount(orders.getAmount());
        vo.setOrderTime(orders.getOrderTime());

        // 仅堂食返回取餐号
        if (pickupNumber != null) {
            vo.setPickupNumber(pickupNumber);
        }
        return vo;
    }

    /**
     * 创建订单明细，并批量插入订单明细表
     *
     * @param orders           订单对象
     * @param shoppingCartList 购物车列表
     */
    private void insertOrderDetails(Orders orders, List<ShoppingCart> shoppingCartList) {
        List<OrderDetail> orderDetailList = new ArrayList<>();

        shoppingCartList.forEach(cart -> {
            //为对应的菜品增加热度
            if (cart.getDishId() != null) {
                dishMapper.updateHotDish(cart.getDishId());
            }

            //为对应的套餐增加热度
            if (cart.getSetmealId() != null) {
                setmealMapper.updateHotSetmeal(cart.getSetmealId());
            }
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        });
        log.info("orderDetail:{}", orderDetailList);
        orderDetailMapper.insertBatch(orderDetailList);
    }

    @Override
    public void reminder(Long id) {
        // 根据订单id查询需要催单的订单
        Orders orders = orderMapper.getOrderById(id);

        // 判断订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type", 2); // 催单消息类型
        map.put("orderId", id);
        map.put("content", "订单号" + orders.getNumber() + "，请您尽快接单！");

        // 通过WebSocket向客户端推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 用户订单分页查询
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return
     */
    @Override
    public PageResult pageQueryUser(int pageNum, int pageSize, Integer status) {
        // 构建缓存键
        Long userId = BaseContext.getUserId() != null ? BaseContext.getUserId() : (Long) ToolContext.get(ToolContext.USER_ID);
        String cacheKey = buildUserOrderCacheKey(userId, pageNum, pageSize, status);
        // 尝试从缓存获取结果
        PageResult cachedResult = getCachedPageResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        // 开始分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = OrdersPageQueryDTO.builder()
                .status(status)
                .userId(userId)
                .build();

        // 分页查询订单数据（根据用户id、订单状态查询）
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        // 使用Optional进行空检查
        long total = Optional.ofNullable(page)
                .map(Page::getTotal)
                .orElse(0L);

        /* 根据订单id列表批量查询订单详情数据 */
        if (total > 0) {
            // 获取当前页的订单id列表
            List<Long> orderIds = page.stream()
                    .map(Orders::getId)
                    .collect(Collectors.toList());

            // 批量查询订单详情
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderIds(orderIds);

            // 将对应的订单id和这个订单对应的订单详情数据放入map形成映射关系（按订单 ID 进行分组）
            Map<Long, List<OrderDetail>> orderDetailsMap = orderDetails.stream()
                    .collect(Collectors.groupingBy(OrderDetail::getOrderId));

            for (Orders orders : page) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);

                // 根据对应订单id获取对应的订单详情数据
                orderVO.setOrderDetailList(orderDetailsMap.get(orders.getId()));
                list.add(orderVO);
            }
        }

        PageResult result = new PageResult(total, list);

        // 将结果缓存
        cachePageResult(cacheKey, JSON.toJSONString(result));

        return result;
    }

    /**
     * 构建用户订单缓存键
     *
     * @param userId   用户id
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param status   订单状态
     * @return 缓存键
     */
    private String buildUserOrderCacheKey(Long userId, int pageNum, int pageSize, Integer status) {
        StringBuilder keyBuilder = new StringBuilder(USER_ORDER);
        keyBuilder.append(userId).append(":")
                .append(pageNum).append(":")
                .append(pageSize);

        if (status != null) {
            keyBuilder.append(":status:").append(status);
        }

        return keyBuilder.toString();
    }

    private PageResult getCachedPageResult(String cacheKey) {
        try {
            String cachedObject = String.valueOf(redisTemplate.opsForValue().get(cacheKey));
            if (cachedObject != null) {
                return JSON.parseObject(cachedObject, PageResult.class);
            }
        } catch (Exception e) {
            log.warn("Failed to get cached page result", e);
        }
        return null;
    }

    /**
     * 缓存页面结果
     *
     * @param cacheKey
     * @param pageResult
     */
    private void cachePageResult(String cacheKey, String pageResult) {
        try {
            // 设置缓存过期时间（例如5分钟）
            redisTemplate.opsForValue().set(cacheKey, pageResult, Duration.ofMinutes(5));
        } catch (Exception e) {
            log.warn("Failed to cache page result", e);
        }
    }

    // 在修改订单状态的方法中添加缓存清理逻辑
    private void clearUserOrderCache(Long userId) {
        // 清理指定用户的订单缓存
        Set<String> keys = redisTemplate.keys(USER_ORDER + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public void userCancelById(OrderCancelDTO orderCancelDTO) {
        // 根据订单id查询订单
        Orders orderDB = orderMapper.getOrderById(orderCancelDTO.getId());

        // 判断订单是否存在
        if (orderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 校验订单归属：用户（或 AI 工具）只能取消自己的订单
        Long currentUserId = BaseContext.getUserId() != null ? BaseContext.getUserId() : (Long) ToolContext.get(ToolContext.USER_ID);
        if (currentUserId == null || !orderDB.getUserId().equals(currentUserId)) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (orderDB.getStatus() > 2) {
            // 只有处于`待付款`和`待接单`状态的订单才能取消
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = Orders.builder()
                .id(orderDB.getId())
                .build();

        // 订单处于待接单状态下，需要进行退款操作
        if (orderDB.getStatus()
                .equals(Orders.TO_BE_CONFIRMED)) {
            log.error("取消订单，申请退款");
            // 退款成功后将支付状态修改为：退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(Optional.ofNullable(orderCancelDTO.getCancelReason())
                .orElse("用户取消")
                .trim());
        orders.setCancelTime(LocalDateTime.now());

        // 清理用户订单缓存（可能是 AI 调用，userId 已在上方归属校验处获取，必不为 null）
        clearUserOrderCache(currentUserId);

        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getUserId();

        // 校验订单归属：只能对自己的订单"再来一单"
        Orders order = orderMapper.getOrderById(id);
        if (order == null || userId == null || !order.getUserId().equals(userId)) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 根据订单id查询需要再来一单的订单它包含的商品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 遍历该订单包含的商品信息，重新生成购物车对象，并添加到数据库中
        List<ShoppingCart> shoppingCartList = orderDetailList.stream()
                .map(o -> {
                    ShoppingCart shoppingCart = new ShoppingCart();

                    // 将原订单详情里面的菜品信息重新复制到购物车对象中
                    BeanUtils.copyProperties(o, shoppingCart, "id");
                    shoppingCart.setUserId(userId);
                    shoppingCart.setCreateTime(LocalDateTime.now());

                    return shoppingCart;
                })
                .collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param orderNumber
     */
    @Override
    public void paySuccess(String orderNumber) {
        // 获取当前下单的微信用户id
        Long userId = BaseContext.getUserId();

        // 根据订单号查询当前用户下单的订单
        Orders ordersDB = orderMapper.getOrderByNumberAndUserId(orderNumber, userId);

        // 判断订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Integer targetStatus = ordersDB.getOrderType() == 2 ? Orders.DINE_IN_ORDER  // 食堂订单状态
                : Orders.TO_BE_CONFIRMED; // 外卖订单状态

        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(targetStatus) // 订单状态：待接单
                .payStatus(Orders.PAID) // 订单支付状态：已支付
                .checkoutTime(LocalDateTime.now()) // 订单结算时间
                .build();
        // 更新订单状态为已支付
        orderMapper.update(orders);

        /* 通过WebSocket向客户端推送消息，type（1：来单提醒，2：催单）、orderId（订单id）、content（消息内容） */
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号" + orderNumber);

        // 清理用户订单缓存
        clearUserOrderCache(userId);

        // 将map转为json字符串，发送给客户端
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    /**
     * 修改订单支付方式
     *
     * @param ordersPaymentDTO
     */
    @Override
    public void updatePayStatus(OrdersPaymentDTO ordersPaymentDTO) {
        if (ordersPaymentDTO == null){
            return;
        }
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        Integer payMethod = ordersPaymentDTO.getPayMethod();
        if (orderNumber == null || payMethod == null){
            return;
        }
        orderMapper.updateByNumber(ordersPaymentDTO);
    }
}
