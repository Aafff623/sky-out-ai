package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description 套餐总览
 */
@Data
@Builder
@ApiModel(description = "查询套餐总览")
@NoArgsConstructor
@AllArgsConstructor
public class SetmealOverViewVO implements Serializable {
  
  /** 已启售数量 */
  @ApiModelProperty("已启售数量")
  private long sold;
  
  /** 已停售数量 */
  @ApiModelProperty("已停售数量")
  private long discontinued;
}
