package com.fortunate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description
 */
@Data
@Builder
@ApiModel(description = "订单提交DTO")
@NoArgsConstructor
@AllArgsConstructor
public class OrdersSubmitDTO implements Serializable {
  
  @ApiModelProperty("地址簿id")
  private Long addressBookId;
  
  @ApiModelProperty("付款方式，1：微信，2：支付宝")
  private int payMethod;
  
  @ApiModelProperty("备注")
  private String remark;
  
  @ApiModelProperty("预计送达时间")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime estimatedDeliveryTime;
  
  @ApiModelProperty("配送状态  1立即送出  0选择具体时间")
  private Integer deliveryStatus;
  
  @ApiModelProperty("餐具数量")
  private Integer tablewareNumber;
  
  @ApiModelProperty("餐具数量状态  1按餐量提供  0选择具体数量")
  private Integer tablewareStatus;
  
  @ApiModelProperty("打包费")
  private Integer packAmount;
  
  @ApiModelProperty("总金额")
  private BigDecimal amount;
  
  @ApiModelProperty("订单类型 1外卖 2堂食")
  private Integer orderType;
}
