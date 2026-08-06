package com.fortunate.entity;

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
@ApiModel(description = "地址簿实体类")
@NoArgsConstructor
@AllArgsConstructor
public class AddressBook implements Serializable {
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("地址簿id")
  private Long id;
  
  @ApiModelProperty("用户id")
  private Long userId;
  
  @ApiModelProperty("收货人")
  private String consignee;
  
  @ApiModelProperty(value = "手机号", required = true)
  private String phone;
  
  @ApiModelProperty("性别 0 女 1 男")
  private String sex;
  
  @ApiModelProperty("省级区划编号")
  private String provinceCode;
  
  @ApiModelProperty("省级名称")
  private String provinceName;
  
  @ApiModelProperty("市级区划编号")
  private String cityCode;
  
  @ApiModelProperty("市级名称")
  private String cityName;
  
  @ApiModelProperty("区级区划编号")
  private String districtCode;
  
  @ApiModelProperty("区级名称")
  private String districtName;
  
  @ApiModelProperty(value = "详细地址", required = true)
  private String detail;
  
  @ApiModelProperty("标签")
  private String label;
  
  @ApiModelProperty("是否为默认地址 0否 1是")
  private Integer isDefault;
}
