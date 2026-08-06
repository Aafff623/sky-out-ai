package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @description 工作台今日数据查询
 */
@Data
@Builder
@ApiModel(description = "工作台今日数据查询")
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataVO implements Serializable {
  
  /** 营业额 */
  @ApiModelProperty("营业额")
  private Double turnover;
  
  /** 有效订单数 */
  @ApiModelProperty("有效订单数")
  private Integer validOrderCount;
  
  /** 订单完成率 */
  @ApiModelProperty("订单完成率")
  private Double orderCompletionRate;
  
  /** 平均客单价 */
  @ApiModelProperty("平均客单价")
  private Double unitPrice;
  
  /** 新增用户数 */
  @ApiModelProperty("新增用户数")
  private Integer newUsers;
}
