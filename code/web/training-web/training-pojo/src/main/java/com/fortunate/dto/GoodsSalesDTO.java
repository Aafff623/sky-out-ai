package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description 商品销量top10DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "商品销量top10DTO")
public class GoodsSalesDTO implements Serializable {
  
  @ApiModelProperty("商品名称")
  private String name;
  
  @ApiModelProperty("销量")
  private Integer number;
}
