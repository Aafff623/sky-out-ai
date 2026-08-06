package com.fortunate.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel("员工DTO")
public class EmployeeDTO implements Serializable {
  
  @ApiModelProperty("员工id")
  private Long id;
  
  @ApiModelProperty("用户名")
  private String username;
  
  @ApiModelProperty("姓名")
  private String name;
  
  @ApiModelProperty("手机号码")
  private String phone;
  
  @ApiModelProperty("性别")
  private String sex;

  @ApiModelProperty("头像")
  private String avatar;
}
