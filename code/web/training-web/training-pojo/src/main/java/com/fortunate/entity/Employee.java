package com.fortunate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApiModel(description = "员工实体类")
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("员工id")
  private Long id;
  
  @ApiModelProperty("员工用户名")
  private String username;
  
  @ApiModelProperty("员工姓名")
  private String name;
  
  @ApiModelProperty("员工密码")
  @JsonIgnore
  private String password;
  
  @ApiModelProperty("员工手机号码")
  private String phone;
  
  @ApiModelProperty("员工性别")
  private String sex;
  
  @ApiModelProperty("是否启动该员工账号，0-禁用，1-启用")
  private Integer status;
  
  @ApiModelProperty("员工创建时间")
  private LocalDateTime createTime;
  
  @ApiModelProperty("员工更新时间")
  private LocalDateTime updateTime;
  
  @ApiModelProperty("员工创建者id")
  private Long createUser;
  
  @ApiModelProperty("员工更新者id")
  private Long updateUser;
}
