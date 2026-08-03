/**
 * 配送状态枚举
 */
export const enum DeliveryStatusEnum {
  /** 立即配送 */
  IMMEDIATELY = 1,

  /** 预约配送 */
  RESERVATION = 0,
}

/**
 * 配送日期类型枚举
 */
export const enum DeliveryDateTypeEnum {
  /** 今日配送 */
  TODAY = 0,

  /** 明日配送 */
  TOMORROW = 1,
}

/**
 * 餐具数量状态 1按餐量提供 0选择具体数量
 */
export const enum TablewareStatusEnum {
  /** 无选择 */
  NO_TABLEWARE = "",

  /** 按餐量提供 */
  BY_SIZE = 1,

  /** 选择具体数量 */
  BY_NUMBER = 0,
}

/**
 * 订单状态枚举类型
 */
export enum OrderStatus {
  /** 全部订单 */
  ALL = "",
  /** 待付款 */
  PENDING_PAYMENT = 1,

  /** 待接单 */
  PENDING_ACCEPTANCE = 2,

  /** 已接单 */
  ACCEPTED = 3,

  /** 派送中 */
  IN_DELIVERY = 4,

  /** 已完成 */
  COMPLETED = 5,

  /** 已取消 */
  CANCELED = 6,

  /** 退款 */
  REFUNDED = 7,

  /** 堂食 */
  DINE_IN_ORDER = 8,
}

/** 订单状态map */
export const orderStatusMap = [
  { name: "待付款", value: OrderStatus.PENDING_PAYMENT },
  { name: "待接单", value: OrderStatus.PENDING_ACCEPTANCE },
  { name: "已接单", value: OrderStatus.ACCEPTED },
  { name: "派送中", value: OrderStatus.IN_DELIVERY },
  { name: "已完成", value: OrderStatus.COMPLETED },
  { name: "已取消", value: OrderStatus.CANCELED },
  { name: "退款", value: OrderStatus.REFUNDED },
  { name: "待取餐", value: OrderStatus.DINE_IN_ORDER },
];
