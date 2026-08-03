/**
 * 支付方式
 */
export const enum PayMethodEnum {
  /** 微信支付 */
  WECHAT = 1,

  /** 支付宝支付 */
  ALIPAY = 2,
}

/**
 * 支付状态
 */
export const enum PayStatusEnum {
  /** 未支付 */
  UNPAID = 0,

  /** 已支付 */
  PAID = 1,

  /** 退款 */
  REFUND = 2,
}
