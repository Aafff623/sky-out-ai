package com.fortunate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description
 */
@Data
@Builder
@ApiModel(description = "订单明细")
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("订单明细id")
  private Long id;
  
  @ApiModelProperty("商品名称")
  private String name;
  
  @ApiModelProperty("订单id")
  private Long orderId;
  
  @ApiModelProperty("菜品id")
  private Long dishId;

  @ApiModelProperty("套餐id")
  private Long setmealId;
  
  @ApiModelProperty("数量")
  private Integer number;
  
  @ApiModelProperty("金额")
  private BigDecimal amount;
  
  @ApiModelProperty("图片")
  private String image;
}
