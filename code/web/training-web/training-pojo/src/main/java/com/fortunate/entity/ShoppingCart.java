package com.fortunate.entity;

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
@ApiModel(description = "购物车实体")
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("购物车id")
  private Long id;
  
  @ApiModelProperty("商品名称")
  private String name;
  
  @ApiModelProperty("用户id")
  private Long userId;
  
  @ApiModelProperty("菜品id")
  private Long dishId;
  
  @ApiModelProperty("套餐id")
  private Long setmealId;
  
  @ApiModelProperty("菜品口味")
  private String dishFlavor;
  
  @ApiModelProperty("数量")
  private Integer number;
  
  @ApiModelProperty("金额")
  private BigDecimal amount;
  
  @ApiModelProperty("图片")
  private String image;
  
  @ApiModelProperty("创建时间")
  private LocalDateTime createTime;
}
