package com.fortunate.task;


import com.fortunate.constant.MessageConstant;
import com.fortunate.entity.Orders;
import com.fortunate.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * @description 订单定时任务，定时处理订单状态
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTask {
  
  private final OrderMapper orderMapper;
  
  /**
   * 处理超时订单
   * @description 对于用户未及时下单的订单，超过15分钟后自动取消订单
   */
  @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
  public void processTimeoutOrder() {
    log.info("开始处理超时订单==> {}", LocalDateTime.now());
    
    Orders order = Orders.builder()
            .status(Orders.CANCELLED) // 订单状态：已取消
            .cancelReason(MessageConstant.ORDER_CANCEL_REASON) // 取消原因：超时自动取消
            .cancelTime(LocalDateTime.now()) // 取消时间
            .build();
    
    // 更新这些订单状态为`超时自动取消`，并设置取消原因、并记录取消时间
    processOrders(Orders.PENDING_PAYMENT, -15, order);
  }
  
  /**
   * 每天凌晨1点触发，处理前一天0点一直处于派送中的订单
   */
  @Scheduled(cron = "0 0 1 * * ?") // 0秒0分1点 -> 每天凌晨1点执行一次（01:00:00）
  public void processDeliveryOrder() {
    log.info("开始处理处于派送中的订单==> {}", LocalDateTime.now());
    
    Orders order = Orders.builder()
            .status(Orders.COMPLETED) // 订单状态：已完成
            .build();
    
    // -60就是前一天0点，也就是说，处理前一天0点一直处于派送中的订单
    processOrders(Orders.DELIVERY_IN_PROGRESS, -60, order);
  }
  
  /**
   * 处理订单（一次性更新订单状态）
   * @param status 订单状态
   * @param timeoutTime 超时时间
   * @param order 订单实体类
   */
  private void processOrders(Integer status, int timeoutTime, Orders order) {
    LocalDateTime time = LocalDateTime.now()
            .plusMinutes(timeoutTime);
    List<Orders> orderList = orderMapper.getOrdersByStatusAndOrderTimeLT(status, time);
    
    if (orderList == null || orderList.isEmpty()) return;
    
    Integer orderStatus = order.getStatus();
    log.info(
            Orders.CANCELLED.equals(orderStatus) ? "超时自动取消的订单数量" : "前天一直处于派送中的订单数量" + "==> {}",
            orderList.size());
    
    // 更新订单状态（使用`in`关键字批量更新）
    orderMapper.updateOrdersStatus(orderList, order);
  }
  
  /**
   * 处理订单（逐一循环更新订单状态）
   * @param status 订单状态
   * @param timeoutTime 超时时间
   * @param updateOrder 更新订单状态的函数
   */
  private void processOrders(Integer status, int timeoutTime, Consumer<Orders> updateOrder) {
    /* 原型SQL：select * from `orders` where `status` = ? and `order_time` < (当前时间 - 15分钟) */
    // 当前时间减去15分钟，比如现在是`12:00`，则减去15分钟就是`11:45`之前的订单就是超时订单
    LocalDateTime time = LocalDateTime.now()
            .plusMinutes(timeoutTime);
    
    // 查询订单状态为`待付款`且订单创建时间早于`timeoutTime`的订单
    List<Orders> orderList = orderMapper.getOrdersByStatusAndOrderTimeLT(status, time);
    
    if (orderList == null || orderList.isEmpty()) return;
    
    orderList.forEach(updateOrder);
    
    // 暂时使用如下代码，逐个更新订单状态（效率低，性能低）
    orderList.forEach(orderMapper::update);
  }
  
}
