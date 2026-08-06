package com.fortunate.dto;

import com.fortunate.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description
 */
@Data
@ApiModel(description = "菜品传输对象")
public class DishDTO implements Serializable {
  
  @ApiModelProperty("菜品id")
  private Long id;
  
  @ApiModelProperty("菜品名称")
  private String name;
  
  @ApiModelProperty("菜品分类id")
  private Long categoryId;
  
  @ApiModelProperty("菜品价格")
  private BigDecimal price;
  
  @ApiModelProperty("菜品图片url")
  private String image;
  
  @ApiModelProperty("菜品描述信息")
  private String description;
  
  @ApiModelProperty("菜品状态，0-停售 1-起售")
  private Integer status;
  
  @ApiModelProperty("菜品口味，可以有多个口味")
  private List<DishFlavor> flavors = new ArrayList<>();
}
