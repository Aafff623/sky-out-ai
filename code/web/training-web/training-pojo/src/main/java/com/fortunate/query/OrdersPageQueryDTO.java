package com.fortunate.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "订单分页查询参数")
public class OrdersPageQueryDTO extends BasePageQuery implements Serializable {
  
  @ApiModelProperty("订单号")
  private String number;
  
  @ApiModelProperty("手机号")
  private String phone;
  
  @ApiModelProperty("订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消")
  private Integer status;
  
  @ApiModelProperty("开始时间")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime beginTime;
  
  @ApiModelProperty("结束时间")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endTime;
  
  @ApiModelProperty("用户id")
  private Long userId;
}
