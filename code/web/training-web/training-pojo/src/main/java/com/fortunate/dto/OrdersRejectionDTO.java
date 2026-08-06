package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description
 */
@Data
@Builder
@ApiModel(description = "订单拒绝原因DTO")
@NoArgsConstructor
@AllArgsConstructor
public class OrdersRejectionDTO implements Serializable {
  
  @ApiModelProperty("订单id")
  private Long id;
  
  @ApiModelProperty("订单拒绝原因")
  private String rejectionReason;
}
