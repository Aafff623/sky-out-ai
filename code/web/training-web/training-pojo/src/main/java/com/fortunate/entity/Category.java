package com.fortunate.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("分类id")
  private Long id;
  
  @ApiModelProperty("类型: 1菜品分类 2 套餐分类")
  private Integer type;
  
  @ApiModelProperty("分类名称")
  private String name;
  
  @ApiModelProperty("排序")
  private Integer sort;
  
  @ApiModelProperty("分类状态 0标识禁用 1表示启用")
  private Integer status;
  
  @ApiModelProperty("创建时间")
  private LocalDateTime createTime;
  
  @ApiModelProperty("更新时间")
  private LocalDateTime updateTime;
  
  @ApiModelProperty("创建人")
  private Long createUser;
  
  @ApiModelProperty("修改人")
  private Long updateUser;
}
