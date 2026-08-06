package com.fortunate.mapper;

import com.fortunate.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description
 */
@Mapper
public interface OrderDetailMapper {
  
  /**
   * 根据订单id查询订单明细数据
   * @param id 订单id
   * @return 订单明细数据列表
   */
  @Select("select * from `order_detail` where `order_id` = #{orderId}")
  List<OrderDetail> getByOrderId(@Param("orderId") Long id);
  
  /**
   * 批量插入订单明细数据
   * @param orderDetailList 订单明细数据列表
   */
  void insertBatch(List<OrderDetail> orderDetailList);
  
  /**
   * 根据订单id列表查询订单明细数据
   * @param ids 订单id列表
   * @return 订单明细数据列表
   */
  List<OrderDetail> getByOrderIds(@Param("orderIds") List<Long> ids);
  
}
