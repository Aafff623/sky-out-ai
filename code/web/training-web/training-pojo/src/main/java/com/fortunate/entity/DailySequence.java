package com.fortunate.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySequence implements Serializable {
  
  private static final long serialVersionUID = 1L;
  @ApiModelProperty("日期")
  private LocalDate date;
  @ApiModelProperty("字母")
  private Character letter;
  @ApiModelProperty("数字")
  private Integer number;
  @ApiModelProperty("版本")
  private Integer version;
  
  public DailySequence(LocalDate date, Character letter, Integer number) {
    this(date, letter, number, 0);
  }
  
}
