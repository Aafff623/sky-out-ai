package com.fortunate.entity;

import com.alibaba.fastjson.annotation.JSONField;
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
@ApiModel(description = "订单实体类")
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  /** 订单状态：1待付款 */
  public static final Integer PENDING_PAYMENT = 1;
  /** 订单状态：2待接单 */
  public static final Integer TO_BE_CONFIRMED = 2;
  /** 订单状态：3已接单 */
  public static final Integer CONFIRMED = 3;
  /** 订单状态：4派送中 */
  public static final Integer DELIVERY_IN_PROGRESS = 4;
  /** 订单状态：5已完成 */
  public static final Integer COMPLETED = 5;
  /** 订单状态：6已取消 */
  public static final Integer CANCELLED = 6;
  /** 订单状态：7退款 */
  public static final Integer REFUNDING = 7;
  /** 订单状态：8堂食订单 */
  public static final Integer DINE_IN_ORDER = 8;
  
  
  /** 支付状态：0未支付 */
  public static final Integer UN_PAID = 0;
  /** 支付状态：1已支付 */
  public static final Integer PAID = 1;
  /** 支付状态：2退款 */
  public static final Integer REFUND = 2;
  /** 订单类型：1外卖 */
  public static final Integer TAKEAWAY = 1;
  /** 订单类型：2堂食 */
  public static final Integer DINE_IN = 2;
  
  
  @ApiModelProperty("订单id")
  private Long id;
  
  @ApiModelProperty("订单号")
  private String number;
  
  @ApiModelProperty("订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款")
  private Integer status;
  
  @ApiModelProperty("下单用户id")
  private Long userId;
  
  @ApiModelProperty("地址id")
  private Long addressBookId;
  
  @ApiModelProperty("下单时间")
  // @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 局部字段日期格式化
  private LocalDateTime orderTime;
  
  @ApiModelProperty("结账时间")
  private LocalDateTime checkoutTime;
  
  @ApiModelProperty("支付方式 1微信，2支付宝")
  private Integer payMethod;
  
  @ApiModelProperty("支付状态 0未支付 1已支付 2退款")
  private Integer payStatus;
  
  @ApiModelProperty("实收金额")
  private BigDecimal amount;
  
  @ApiModelProperty("备注")
  private String remark;
  
  @ApiModelProperty("用户名")
  private String userName;
  
  @ApiModelProperty("手机号")
  private String phone;
  
  @ApiModelProperty("地址")
  private String address;
  
  @ApiModelProperty("收货人")
  private String consignee;
  
  @ApiModelProperty("订单取消原因")
  private String cancelReason;
  
  @ApiModelProperty("订单拒绝原因")
  private String rejectionReason;
  
  @ApiModelProperty("订单取消时间")
  private LocalDateTime cancelTime;
  
  @ApiModelProperty("预计送达时间")
  private LocalDateTime estimatedDeliveryTime;
  
  @ApiModelProperty("配送状态  1立即送出  0选择具体时间")
  private Integer deliveryStatus;
  
  @ApiModelProperty("送达时间")
  private LocalDateTime deliveryTime;
  
  @ApiModelProperty("打包费")
  private int packAmount;
  
  @ApiModelProperty("餐具数量")
  private int tablewareNumber;
  
  @ApiModelProperty("餐具数量状态  1按餐量提供  0选择具体数量")
  private Integer tablewareStatus;
  
  @ApiModelProperty("订单类型 1外卖 2堂食")
  private Integer orderType;
  
  @ApiModelProperty("取餐号（堂食专用）")
  private String pickupNumber;

  @ApiModelProperty("是否评价 0否 1是")
  private Short isComment;
}
