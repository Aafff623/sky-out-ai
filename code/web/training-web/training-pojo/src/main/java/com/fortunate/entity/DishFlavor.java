package com.fortunate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @description 菜品口味
 */
@Data
@Builder
@ApiModel(description = "菜品口味")
@NoArgsConstructor
@AllArgsConstructor
public class DishFlavor implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("菜品口味id")
  private Long id;
  
  @ApiModelProperty("菜品id")
  private Long dishId;
  
  @ApiModelProperty("口味名称")
  private String name;
  
  @ApiModelProperty("口味值")
  private String value;
  
}
