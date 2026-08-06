package com.fortunate.vo;

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
@ApiModel(description = "微信登录数据展示对象")
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO implements Serializable {
  
  @ApiModelProperty("新增用户id")
  private Long id;
  
  @ApiModelProperty("openid用户唯一标识")
  private String openid;
  
  @ApiModelProperty("jwt校验令牌token")
  private String token;
}
