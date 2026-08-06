package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel(description = "订单支付DTO")
public class OrdersPaymentDTO implements Serializable {
  
  @ApiModelProperty("订单号")
  private String orderNumber;
  
  @ApiModelProperty("付款方式")
  private Integer payMethod;
}
