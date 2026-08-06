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
@ApiModel(description = "小程序端用户实体信息")
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("用户id")
  private Long id;
  
  @ApiModelProperty("微信用户唯一标识")
  private String openid;
  
  @ApiModelProperty("微信用户姓名")
  private String name;
  
  @ApiModelProperty("手机号")
  private String phone;
  
  @ApiModelProperty("性别 0 女 1 男")
  private String sex;
  
  @ApiModelProperty("头像")
  private String avatar;
  
  @ApiModelProperty("注册时间")
  private LocalDateTime createTime;
}
