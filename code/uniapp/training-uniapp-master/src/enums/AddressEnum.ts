/**
 * 是否为默认地址
 */
export const enum AddressTypeEnum {
  /** 默认收货地址 */
  DEFAULT = 1,

  /** 不是默认收货地址 */
  NOT_DEFAULT = 0,
}

/**
 * 地址标签
 */
export const enum AddressLabelEnum {
  /** 无选择 */
  NO_SELECT = "",
  /** 家 */
  HOME = "家",

  /** 公司 */
  COMPANY = "公司",

  /** 学校 */
  SCHOOL = "学校",

  /** 其他 */
  OTHER = "其他",
}

/**
 * 性别枚举
 */
export const enum SexEnum {
  /** 女 */
  FEMALE = "0",

  /** 男 */
  MALE = "1",
}