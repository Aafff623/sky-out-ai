/**
 * 订单状态枚举
 */
export const enum OrderEnum {
  /** 全部订单 */
  ALL = "",

  /** 待付款 */
  PENDING_PAYMENT = 1,

  /** 待接单 */
  PENDING_ORDER = 2,

  /** 已接单（待派送） */
  ORDERS_TAKEN = 3,

  /** 派送中 */
  DELIVERING = 4,

  /** 已完成 */
  COMPLETED = 5,

  /** 已取消 */
  CANCELED = 6,

  /** 退款 */
  REFUND = 7,

  /** 待出餐 */
  DINE_IN_ORDER = 8,
}

/**
 * 订单取消原因枚举
 */
export const enum ReasonEnum {
  /** 商家拒单 */
  REJECTTION = "商家拒单",

  /** 商家取消 */
  CANCEL = "商家取消订单",
}
