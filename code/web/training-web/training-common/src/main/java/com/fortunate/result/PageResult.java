package com.fortunate.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description 分页结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "分页查询结果")
public class PageResult implements Serializable {
  
  @ApiModelProperty("总记录数")
  private long total;
  
  @ApiModelProperty("当前页数据集合")
  private List<?> records;
}
