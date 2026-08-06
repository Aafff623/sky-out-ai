package com.fortunate.entity;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "商家信息实体类")
@NoArgsConstructor
@AllArgsConstructor
public class ShopInfo implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("商家信息实体id")
  private Long id;
  @ApiModelProperty("店家名称")
  private String name;
  @ApiModelProperty("logo")
  private String logo;
  @ApiModelProperty("店家地址")
  private String address;
  @ApiModelProperty("营业时间")
  private String businessHours;
  @ApiModelProperty("联系电话")
  private String contactPhone;
  @ApiModelProperty("1营业 0休息")
  private Integer status;
  @ApiModelProperty("创建时间")
  private LocalDateTime createTime;
  @ApiModelProperty("更新时间")
  private LocalDateTime updateTime;
}
