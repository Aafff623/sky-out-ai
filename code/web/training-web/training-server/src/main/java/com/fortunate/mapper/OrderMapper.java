package com.fortunate.mapper;

import com.fortunate.annotation.AutoFill;
import com.fortunate.dto.GoodsSalesDTO;
import com.fortunate.dto.OrdersPaymentDTO;
import com.fortunate.entity.Orders;
import com.fortunate.enumeration.OperationType;
import com.fortunate.query.OrdersPageQueryDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @description
 */
@Mapper
public interface OrderMapper {

    /**
     * 分页条件查询并按下单时间排序
     *
     * @param ordersPageQueryDTO 订单分页查询DTO
     * @return 分页结果
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     *
     * @param id 订单id
     * @return 订单实体类
     */
    @Select("select * from `orders` where `id` = #{orderId}")
    Orders getOrderById(@Param("orderId") Long id);

    /**
     * 更新订单数据
     *
     * @param orders 订单实体类
     */
    @AutoFill(OperationType.UPDATE)
    void update(Orders orders);

    /**
     * 插入订单数据
     *
     * @param orders 订单实体类
     */
    void insert(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     *
     * @param orderNumber 订单号
     * @param userId      用户id
     * @return 订单实体类
     */
    @Select("select * from `orders` where `number` = #{orderNumber} and `user_id`= #{userId}")
    Orders getOrderByNumberAndUserId(@Param("orderNumber") String orderNumber, @Param("userId") Long userId);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 订单列表
     */
    @Select("select * from `orders` where `user_id` = #{userId} order by `order_time` desc limit #{limit}")
    List<Orders> getOrdersByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    @Select("select * from `orders` where `user_id` = #{userId}  and `status` = #{status}")
    List<Orders> getOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据动态条件统计订单数量
     *
     * @param map 条件map
     * @return 订单数量
     */
    Integer countByMap(Map<String, Object> map);

    /**
     * 根据动态条件统计营业额数据
     *
     * @param map 条件map
     * @return 营业额数据
     */
    Double sumByMap(Map<String, Object> map);

    /**
     * 根据日期范围获取订单列表
     *
     * @param map 条件map
     * @return 订单列表
     */
    List<Orders> findOrdersByMap(Map<String, Object> map);

    /**
     * 统计指定时间区间内的销量排名前10
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 销量排名前10的商品列表
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询指定订单状态和订单创建时间小于timeoutTime的订单列表
     *
     * @param status      订单状态
     * @param timeoutTime 订单创建时间小于timeoutTime
     * @return 订单列表
     */
    @Select("select * from `orders` where `status` = #{status} and `order_time` < #{timeoutTime}")
    List<Orders> getOrdersByStatusAndOrderTimeLT(@Param("status") Integer status,
                                                 @Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 批量更新订单状态
     *
     * @param orderList 订单列表
     * @param order     订单实体类
     */
    void updateOrdersStatus(@Param("orderList") List<Orders> orderList, @Param("order") Orders order);

    /**
     * 根据订单号更新订单信息
     * @param orders
     */
    void updateByNumber(OrdersPaymentDTO orderDto);
}
