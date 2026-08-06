package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description 订单统计
 */
@Data
@Builder
@ApiModel(description = "订单统计视图对象")
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportVO implements Serializable {
  
  /** 日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03 */
  @ApiModelProperty(value = "日期，以逗号分隔", example = "2022-10-01,2022-10-02,2022-10-03")
  private String dateList;
  
  /** 每日订单数，以逗号分隔，例如：260,210,215 */
  @ApiModelProperty(value = "每日订单数，以逗号分隔", example = "260,210,215")
  private String orderCountList;
  
  /** 每日有效订单数，以逗号分隔，例如：20,21,10 */
  @ApiModelProperty(value = "每日有效订单数，以逗号分隔", example = "20,21,10")
  private String validOrderCountList;
  
  /** 订单总数 */
  @ApiModelProperty("订单总数")
  private Integer totalOrderCount;
  
  /** 有效订单数 */
  @ApiModelProperty("有效订单数")
  private Integer validOrderCount;
  
  /** 订单完成率 */
  @ApiModelProperty("订单完成率")
  private Double orderCompletionRate;
}
