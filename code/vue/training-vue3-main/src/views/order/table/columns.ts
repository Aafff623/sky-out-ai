import { OrderPageVO } from "@/api/order";

/** 全部订单表字段（完成） */
export const ALL: Array<TableColumnData<OrderPageVO>> = [
  {
    label: "订单号",
    prop: "number",
    minWidth: "200px",
  },
  {
    label: "订单状态",
    text: "status",
  },
  {
    label: "用户名",
    text: "userName",
    minWidth: "140px",
  },
  {
    label: "手机号",
    prop: "phone",
    minWidth: "140px",
  },
  {
    label: "地址",
    text: "address",
    minWidth: "180px",
  },
  {
    label: "就餐方式",
    text: "orderTypeName",
    minWidth: "120px",
  },
  {
    label: "下单时间",
    prop: "orderTime",
    minWidth: "160px",
  },
  {
    label: "实收金额",
    text: "amount",
  },
  {
    label: "备注",
    text: "remark",
    minWidth: "200px",
  },
  {
    label: "操作",
    text: "action",
    minWidth: "140px",
    fixed: "right",
  },
];

/** 待接单表字段（完成） */
export const PENDING_ORDER: Array<TableColumnData<OrderPageVO>> = [
  {
    label: "订单号",
    prop: "number",
    minWidth: "200px",
  },
  {
    label: "订单菜品",
    prop: "orderDishes",
    minWidth: "160px",
  },
  {
    label: "用户名",
    text: "userName",
    minWidth: "140px",
  },
  {
    label: "手机号",
    prop: "phone",
    minWidth: "140px",
  },
  {
    label: "支付方式",
    text: "payMethod",
  },
  {
    label: "地址",
    text: "address",
    minWidth: "180px",
  },
  {
    label: "就餐方式",
    text: "orderTypeName",
    minWidth: "120px",
  },
  {
    label: "下单时间",
    prop: "orderTime",
    minWidth: "160px",
  },
  {
    label: "备注",
    text: "remark",
    minWidth: "160px",
  },
  {
    label: "操作",
    text: "action",
    minWidth: "140px",
    fixed: "right",
  },
];

/** 待派送（待接单）表字段（完成） */
export const ORDERS_TAKEN: Array<TableColumnData<OrderPageVO>> = [
  {
    label: "订单号",
    prop: "number",
    minWidth: "200px",
  },
  {
    label: "订单菜品",
    prop: "orderDishes",
    minWidth: "160px",
  },
  {
    label: "地址",
    text: "address",
    minWidth: "180px",
  },
  {
    label: "就餐方式",
    text: "orderTypeName",
    minWidth: "120px",
  },
  {
    label: "预计送达时间",
    prop: "estimatedDeliveryTime",
    minWidth: "160px",
  },
  {
    label: "备注",
    text: "remark",
    minWidth: "200px",
  },
  {
    label: "餐具数量",
    prop: "tablewareNumber",
  },
  {
    label: "操作",
    text: "action",
    minWidth: "140px",
    fixed: "right",
  },
];

/** 派送中表字段（完成） */
export const DELIVERING: Array<TableColumnData<OrderPageVO>> = [
  {
    label: "订单号",
    prop: "number",
    minWidth: "200px",
  },
  {
    label: "订单菜品",
    prop: "orderDishes",
    minWidth: "160px",
  },
  {
    label: "用户名",
    text: "userName",
    minWidth: "140px",
  },
  {
    label: "手机号",
    prop: "phone",
    minWidth: "140px",
  },
  {
    label: "地址",
    text: "address",
    minWidth: "180px",
  },
  {
    label: "就餐方式",
    text: "orderTypeName",
    minWidth: "120px",
  },
  {
    label: "支付方式",
    text: "payMethod",
  },
  {
    label: "预计送达时间",
    prop: "estimatedDeliveryTime",
    minWidth: "160px",
  },
  {
    label: "实收金额",
    text: "amount",
  },
  {
    label: "操作",
    text: "action",
    minWidth: "140px",
    fixed: "right",
  },
];

/** 已完成表字段（完成） */
export const COMPLETED: Array<TableColumnData<OrderPageVO>> = [
  {
    label: "订单号",
    prop: "number",
    minWidth: "200px",
  },
  {
    label: "用户名",
    text: "userName",
    minWidth: "160px",
  },
  {
    label: "手机号",
    prop: "phone",
    minWidth: "140px",
  },
  {
    label: "地址",
    text: "address",
    minWidth: "180px",
  },
  {
    label: "就餐方式",
    text: "orderTypeName",
    minWidth: "120px",
  },
  {
    label: "送达时间",
    prop: "deliveryTime",
    minWidth: "160px",
  },
  {
    label: "实收金额",
    text: "amount",
  },
  {
    label: "备注",
    text: "remark",
    minWidth: "200px",
  },
  {
    label: "操作",
    text: "action",
    minWidth: "140px",
    fixed: "right",
  },
];

/** 已取消表字段（完成） */
export const CANCELED: Array<TableColumnData<OrderPageVO>> = [
  {
    label: "订单号",
    prop: "number",
    minWidth: "200px",
  },
  {
    label: "订单菜品",
    prop: "orderDishes",
    minWidth: "220px",
  },
  {
    label: "用户名",
    prop: "userName",
    minWidth: "140px",
  },
  {
    label: "手机号",
    prop: "phone",
    minWidth: "140px",
  },
  {
    label: "地址",
    text: "address",
    minWidth: "180px",
  },
  {
    label: "就餐方式",
    text: "orderTypeName",
    minWidth: "120px",
  },
  {
    label: "取消原因",
    text: "status",
    minWidth: "160px",
  },
  {
    label: "取消订单时间",
    prop: "orderTime",
    minWidth: "160px",
  },
  {
    label: "操作",
    text: "action",
    minWidth: "140px",
    fixed: "right",
  },
];

/** 堂食订单（待出餐）表字段 */
export const DINE_IN_ORDER: Array<TableColumnData<OrderPageVO>> = [
  {
    label: "订单号",
    prop: "number",
    minWidth: "200px",
  },
  {
    label: "取餐码",
    prop: "pickupNumber",
    minWidth: "120px",
  },
  {
    label: "订单菜品",
    prop: "orderDishes",
    minWidth: "160px",
  },
  {
    label: "下单时间",
    prop: "orderTime",
    minWidth: "160px",
  },
  {
    label: "订单类型",
    prop: "orderType",
    minWidth: "120px",
    formatter: (row: OrderPageVO) =>
      row.orderType === 2 ? "堂食订单" : "--"
  },
  {
    label: "操作",
    text: "action",
    minWidth: "140px",
    fixed: "right",
  },
];
