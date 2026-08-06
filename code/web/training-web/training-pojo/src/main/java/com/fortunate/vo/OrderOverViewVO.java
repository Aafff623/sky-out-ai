package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @description 订单概览数据
 */
@Data
@Builder
@ApiModel(description = "查询订单管理数据")
@NoArgsConstructor
@AllArgsConstructor
public class OrderOverViewVO implements Serializable {
  
  /** 待接单数量 */
  @ApiModelProperty("待接单数量")
  private long waitingOrders;
  
  /** 待派送数量 */
  @ApiModelProperty("待派送数量")
  private long deliveredOrders;
  
  /** 已完成数量 */
  @ApiModelProperty("已完成数量")
  private long completedOrders;
  
  /** 已取消数量 */
  @ApiModelProperty("已取消数量")
  private long cancelledOrders;
  
  /** 全部订单 */
  @ApiModelProperty("全部订单")
  private long allOrders;
}
