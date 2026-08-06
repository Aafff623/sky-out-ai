package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel("分类数据传输对象")
public class CategoryDTO implements Serializable {
  @ApiModelProperty("分类id")
  private Long id;
  
  @ApiModelProperty("类型，1-菜品分类 2-套餐分类")
  private Integer type;
  
  @ApiModelProperty("分类名称")
  private String name;
  
  @ApiModelProperty("排序")
  private Integer sort;
}
