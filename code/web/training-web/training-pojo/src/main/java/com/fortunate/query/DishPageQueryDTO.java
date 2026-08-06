package com.fortunate.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel(description = "菜品分页查询数据传输对象")
public class DishPageQueryDTO extends BasePageQuery implements Serializable {
  
  @ApiModelProperty("菜品名称")
  private String name;
  
  @ApiModelProperty("该菜品所属分类id")
  private Integer categoryId;
  
  @ApiModelProperty("菜品状态 0-表示禁用 1-表示启用")
  private Integer status;
}
