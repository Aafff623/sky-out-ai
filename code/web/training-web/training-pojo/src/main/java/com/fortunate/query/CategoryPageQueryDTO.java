package com.fortunate.query;

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
@ApiModel(description = "分类分页查询参数DTO")
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPageQueryDTO extends BasePageQuery implements Serializable {

  @ApiModelProperty("分类名称")
  private String name;
  
  @ApiModelProperty("分类类型 1-菜品分类 2-套餐分类")
  private Integer type;
  
  @ApiModelProperty("分类状态 1-启用 0-停用")
  private Integer status;
}
