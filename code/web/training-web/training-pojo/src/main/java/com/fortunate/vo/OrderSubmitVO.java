package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description
 */
@Data
@Builder
@ApiModel(description = "订单提交信息")
@NoArgsConstructor
@AllArgsConstructor
public class OrderSubmitVO implements Serializable {
  
  @ApiModelProperty("订单id")
  private Long id;
  
  @ApiModelProperty("订单号")
  private String orderNumber;
  
  @ApiModelProperty("取餐号（仅堂食订单返回）")
  private String pickupNumber;
  
  @ApiModelProperty("订单金额")
  private BigDecimal orderAmount;
  
  @ApiModelProperty("下单时间")
  private LocalDateTime orderTime;
}
