package com.fortunate.service.impl;


import com.fortunate.constant.StatusConstant;
import com.fortunate.entity.Dish;
import com.fortunate.entity.Orders;
import com.fortunate.entity.Setmeal;
import com.fortunate.entity.User;
import com.fortunate.mapper.DishMapper;
import com.fortunate.mapper.OrderMapper;
import com.fortunate.mapper.SetmealMapper;
import com.fortunate.mapper.UserMapper;
import com.fortunate.service.WorkspaceService;
import com.fortunate.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description 工作台service实现类
 */
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
  
  private final OrderMapper orderMapper;
  private final UserMapper userMapper;
  private final DishMapper dishMapper;
  private final SetmealMapper setmealMapper;
  
  /**
   * 1、营业额：当日已完成订单的总金额
   * 2、有效订单：当日已完成订单的数量
   * 3、订单完成率：有效订单数 / 总订单数
   * 4、平均客单价：营业额 / 有效订单数
   * 5、新增用户：当日新增用户的数量
   */
  @Override
  public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
    Map<String, Object> map = new HashMap<>();
    map.put("begin", begin);
    map.put("end", end);
    
    // 查询当天全部的总订单数
    Integer totalOrderCount = orderMapper.countByMap(map);
    
    map.put("status", Orders.COMPLETED); // 订单状态：已完成
    /* 1、营业额：当日已完成订单的总金额 */
    Double turnover = orderMapper.sumByMap(map);
    turnover = turnover == null ? 0.0 : turnover;
    
    /* 2、有效订单：当日已完成订单的数量 */
    Integer validOrderCount = orderMapper.countByMap(map);
    
    double unitPrice = 0.0; // 平均客单价
    double orderCompleteRate = 0.0; // 订单完成率
    
    if (totalOrderCount != 0 && validOrderCount != 0) {
      /* 3、订单完成率 = 有效订单数 / 总订单数 */
      orderCompleteRate = validOrderCount.doubleValue() / totalOrderCount;
      
      /* 4、平均客单价 = 营业额 / 有效订单数 */
      unitPrice = turnover / validOrderCount;
    }
    
    /* 5、新增用户：当日新增用户的数量 */
    Integer newUsers = userMapper.countByMap(map);
    
    return BusinessDataVO.builder()
            .turnover(turnover)
            .validOrderCount(validOrderCount)
            .orderCompletionRate(orderCompleteRate)
            .unitPrice(unitPrice)
            .newUsers(newUsers)
            .build();
  }
  
  @Override
  public BusinessDataByMapVO findRangeBusinessData(LocalDateTime beginTime, LocalDateTime endTime) {
    Map<String, Object> map = new HashMap<>();
    map.put("begin", beginTime);
    map.put("end", endTime);
    
    /* 范围内的全部订单列表、新增用户列表（2条sql） */
    List<Orders> totalOrderList = orderMapper.findOrdersByMap(map);
    List<User> newUsers = userMapper.findUsersByMap(map);
    
    // 按日期分组统计总订单数和有效订单数、营业额
    Map<LocalDate, List<Orders>> ordersByDate = totalOrderList.stream()
            .collect(Collectors.groupingBy(order -> order.getOrderTime()
                    .toLocalDate()));
    
    Map<LocalDate, Long> totalOrderCountMap = new HashMap<>(); // 总订单数map
    Map<LocalDate, Long> validOrderCountMap = new HashMap<>(); // 有效订单数map
    Map<LocalDate, Double> turnoverMap = new HashMap<>(); // 营业额map
    
    ordersByDate.forEach((date, orders) -> {
      totalOrderCountMap.put(date, (long) orders.size());
      
      /* 1、营业额：按日期分组统计营业额 */
      double turnover = orders.stream()
              .filter(order -> Orders.COMPLETED.equals(order.getStatus()))
              .mapToDouble(order -> order.getAmount()
                      .doubleValue())
              .sum();
      turnoverMap.put(date, turnover);
      
      /* 2、有效订单：按日期分组统计有效订单数 */
      long validOrderCount = orders.stream()
              .filter(order -> Orders.COMPLETED.equals(order.getStatus()))
              .count();
      validOrderCountMap.put(date, validOrderCount);
    });
    
    /* 3、订单完成率 = 有效订单数 / 总订单数 */
    Map<LocalDate, Double> orderCompleteRateMap = totalOrderCountMap.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> validOrderCountMap.getOrDefault(entry.getKey(),
                                                                                                  0L) * 1.0 / entry.getValue()));
    
    /*  4、平均客单价 = 营业额 / 有效订单数 */
    Map<LocalDate, Double> unitPriceMap = turnoverMap.entrySet()
            .stream()
            .filter(entry -> validOrderCountMap.getOrDefault(entry.getKey(), 0L) != 0)
            .collect(Collectors.toMap(Map.Entry::getKey,
                                      entry -> entry.getValue() / validOrderCountMap.get(entry.getKey())));
    
    /* 5、新增用户：按日期分组统计新增用户数 */
    Map<LocalDate, Long> newUsersMap = newUsers.stream()
            .collect(Collectors.groupingBy(user -> user.getCreateTime()
                    .toLocalDate(), Collectors.counting()));
    
    return BusinessDataByMapVO.builder()
            .turnoverMap(turnoverMap)
            .validOrderCountMap(validOrderCountMap)
            .orderCompletionRateMap(orderCompleteRateMap)
            .unitPriceMap(unitPriceMap)
            .newUsersMap(newUsersMap)
            .build();
  }
  
  @Override
  public OrderOverViewVO getOrderOverView() {
    Map<String, Object> map = new HashMap<>();
    map.put("begin", LocalDateTime.now()
            .with(LocalTime.MIN)); // 今日开始时间
    
    // 查询从今天开始下单的所有订单
    List<Orders> allOrdersList = orderMapper.findOrdersByMap(map); /* 缺陷，当订单数据很庞大时，直接获取所有订单，然后在内存中进行处理时对内存压力很大 */
    
    long waitingOrders = countByStatus(allOrdersList, Orders.TO_BE_CONFIRMED, Orders::getStatus); /* 待接单 */
    long deliveredOrders = countByStatus(allOrdersList, Orders.CONFIRMED, Orders::getStatus); /* 待派送 */
    long completedOrders = countByStatus(allOrdersList, Orders.COMPLETED, Orders::getStatus); /* 已完成 */
    long cancelledOrders = countByStatus(allOrdersList, Orders.CANCELLED, Orders::getStatus); /* 已取消 */
    long allOrders = allOrdersList.size(); /* 全部订单 */
    
    return OrderOverViewVO.builder()
            .waitingOrders(waitingOrders)
            .deliveredOrders(deliveredOrders)
            .completedOrders(completedOrders)
            .cancelledOrders(cancelledOrders)
            .allOrders(allOrders)
            .build();
  }
  
  @Override
  public DishOverViewVO getDishOverView() {
    // 查询所有菜品
    List<Dish> allDishesList = dishMapper.getAllDishes(); /* 此处同理 */
    
    // 统计不同状态的菜品数量
    long sold = countByStatus(allDishesList, StatusConstant.ENABLE, Dish::getStatus); /* 菜品状态：起售 */
    long discontinued = countByStatus(allDishesList, StatusConstant.DISABLE, Dish::getStatus); /* 菜品状态：停售 */
    
    return DishOverViewVO.builder()
            .sold(sold)
            .discontinued(discontinued)
            .build();
  }
  
  @Override
  public SetmealOverViewVO getSetmealOverView() {
    // 查询所有套餐
    List<Setmeal> allSetmealsList = setmealMapper.list(null); /* 此处同理 */
    
    // 统计不同状态套餐数量
    long sold = countByStatus(allSetmealsList, StatusConstant.ENABLE, Setmeal::getStatus); /* 套餐状态：起售 */
    long discontinued = countByStatus(allSetmealsList, StatusConstant.DISABLE, Setmeal::getStatus); /* 套餐状态：停售 */
    
    return SetmealOverViewVO.builder()
            .sold(sold)
            .discontinued(discontinued)
            .build();
  }
  
  /**
   * 根据状态统计数量
   * @param list 列表
   * @param status 状态
   * @return 数量
   */
  private <T> long countByStatus(List<T> list, Integer status, Function<T, Integer> getStatusFunction) {
    return list.stream()
            .filter(item -> status.equals(getStatusFunction.apply(item)))
            .count();
  }
}
