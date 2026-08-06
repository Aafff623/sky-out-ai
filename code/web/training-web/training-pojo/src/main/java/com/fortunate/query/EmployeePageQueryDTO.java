package com.fortunate.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description
 */
@Data
@ApiModel("员工分页查询DTO")
public class EmployeePageQueryDTO extends BasePageQuery implements Serializable {
  
  @ApiModelProperty("员工姓名")
  private String name;
}
