package com.fortunate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @description 套餐菜品关系
 */
@Data
@Builder
@ApiModel(description = "套餐菜品关系")
@NoArgsConstructor
@AllArgsConstructor
public class SetmealDish implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("套餐菜品id")
  private Long id;
  
  @ApiModelProperty("套餐id")
  private Long setmealId;
  
  @ApiModelProperty("菜品id")
  private Long dishId;
  
  @ApiModelProperty("菜品名称")
  private String name;
  
  @ApiModelProperty("菜品原价")
  private BigDecimal price;
  
  @ApiModelProperty("份数")
  private Integer copies;
  
  @ApiModelProperty("套餐中菜品图片")
  private String image;
}
