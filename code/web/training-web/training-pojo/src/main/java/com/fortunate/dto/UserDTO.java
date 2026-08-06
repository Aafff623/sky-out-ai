package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel(description = "微信小程序DTO")
public class UserDTO implements Serializable {
  
  @ApiModelProperty("微信用户id")
  private Long id;
  
  @ApiModelProperty("微信用户昵称")
  private String name;
  
  @ApiModelProperty("微信用户头像")
  private String avatar;
}
