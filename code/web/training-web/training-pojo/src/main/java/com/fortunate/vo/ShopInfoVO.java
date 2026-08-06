package com.fortunate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description
 */
@Data
@Builder
@ApiModel(description = "商家信息VO")
@NoArgsConstructor
@AllArgsConstructor
public class ShopInfoVO implements Serializable {
  
  @ApiModelProperty("商家名称")
  private String name;
  @ApiModelProperty("商家LOGO")
  private String logo;
  @ApiModelProperty("商家地址")
  private String address;
  @ApiModelProperty("营业时间")
  private String businessHours;
  @ApiModelProperty("联系电话")
  private String contactPhone;
  @ApiModelProperty("营业状态 1营业 0休息")
  private Integer status;
  
  @ApiModelProperty("经度")
  private String longitude;
  
  @ApiModelProperty("纬度")
  private String latitude;
  
  @ApiModelProperty("配送范围（公里）")
  private Integer deliveryRange;
  
  @ApiModelProperty("起送价")
  private BigDecimal minimumOrder;
  
  @ApiModelProperty("配送费")
  private BigDecimal deliveryFee;
}
