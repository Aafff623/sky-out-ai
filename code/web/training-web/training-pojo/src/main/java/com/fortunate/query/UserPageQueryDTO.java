package com.fortunate.query;

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
@ApiModel(description = "用户分页查询参数DTO")
@NoArgsConstructor
@AllArgsConstructor
public class UserPageQueryDTO extends BasePageQuery implements Serializable {

  @ApiModelProperty("用户昵称")
  private String name;

  @ApiModelProperty("用户手机号")
  private String phone;
}
