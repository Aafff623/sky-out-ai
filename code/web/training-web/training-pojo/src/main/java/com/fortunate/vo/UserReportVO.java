package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description 用户统计视图对象
 */
@Data
@Builder
@ApiModel(description = "用户统计视图对象")
@NoArgsConstructor
@AllArgsConstructor
public class UserReportVO implements Serializable {
  
  @ApiModelProperty(value = "日期，以逗号分隔", example = "2022-10-01,2022-10-02,2022-10-03")
  private String dateList;
  
  @ApiModelProperty(value = "用户总量，以逗号分隔", example = "200,210,220")
  private String totalUserList;
  
  @ApiModelProperty(value = "新增用户，以逗号分隔", example = "20,21,10")
  private String newUserList;
}
