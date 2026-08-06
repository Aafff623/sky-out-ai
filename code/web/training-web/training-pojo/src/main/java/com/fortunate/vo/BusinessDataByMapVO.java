package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * @description 运营数据报表-明细数据导出excel业务数据
 */
@Data
@Builder
@ApiModel(description = "运营数据报表-明细数据导出excel业务数据")
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataByMapVO {
  
  /** 营业额 */
  @ApiModelProperty("营业额")
  private Map<LocalDate, Double> turnoverMap;
  
  /** 有效订单数 */
  @ApiModelProperty("有效订单数")
  private Map<LocalDate, Long> validOrderCountMap;
  
  /** 订单完成率 */
  @ApiModelProperty("订单完成率")
  private Map<LocalDate, Double> orderCompletionRateMap;
  
  /** 平均客单价 */
  @ApiModelProperty("平均客单价")
  private Map<LocalDate, Double> unitPriceMap;
  
  /** 新增用户数 */
  @ApiModelProperty("新增用户数")
  private Map<LocalDate, Long> newUsersMap;
}
