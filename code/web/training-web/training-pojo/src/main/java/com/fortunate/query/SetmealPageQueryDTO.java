package com.fortunate.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 套餐分页查询条件
 */
@Data
public class SetmealPageQueryDTO extends BasePageQuery implements Serializable {
  @ApiModelProperty("查询套餐名称")
  private String name;
  
  @ApiModelProperty("分类id")
  private Integer categoryId;
  
  @ApiModelProperty("状态：0-禁用 1-启用")
  private Integer status;
  
}
