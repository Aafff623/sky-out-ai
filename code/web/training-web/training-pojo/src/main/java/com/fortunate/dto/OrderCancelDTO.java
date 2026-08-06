package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description 订单取消dto
 */
@Data
@Builder
@ApiModel(description = "订单取消dto")
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelDTO implements Serializable {
  
  @ApiModelProperty("订单id")
  private Long id;
  
  @ApiModelProperty("订单取消原因")
  private String cancelReason;
}
