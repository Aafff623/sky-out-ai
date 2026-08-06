package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel(description = "微信小程序用户登录DTO")
public class UserLoginDTO implements Serializable {
  
  @ApiModelProperty("微信授权码")
  private String code;
}
