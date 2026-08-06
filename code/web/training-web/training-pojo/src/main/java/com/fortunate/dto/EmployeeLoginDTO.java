package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel(description = "员工登录DTO")
public class EmployeeLoginDTO implements Serializable {
  
  @ApiModelProperty("用户名")
  private String username;
  
  @ApiModelProperty("密码")
  private String password;
}
