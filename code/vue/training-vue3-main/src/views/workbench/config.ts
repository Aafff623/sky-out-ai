import { LocationQueryRaw } from "vue-router";
import {
  type BusinessDataVO,
  type OrderOverViewVO,
  type DishOverViewVO,
  type SetmealOverViewVO,
} from "@/api/workspace";
import { OrderEnum } from "@/enums/OrderEnum";

export type WorkspaceRequestMap = {
  businessData: BusinessDataVO;
  orderOverView: OrderOverViewVO;
  dishOverView: DishOverViewVO;
  setmealOverView: SetmealOverViewVO;
};

export type ModuleState<T extends Record<string, any>> = {
  [K in keyof T]: {
    data: T[K] | null;
    loading: boolean;
  };
};

export type ModuleStates = ModuleState<WorkspaceRequestMap>;

export interface DataItemConfig<T = any> {
  /** 索引key */
  key: keyof T;
  /** 显示名称 */
  label: string;
  /** 图标 */
  icon: string;
  /** 图标大小 */
  iconSize?: string;
  /** 路由参数 */
  query?: {
    [key: string]: any;
  } & LocationQueryRaw;
  /** 特殊符号 */
  symbol?: {
    /** 前缀 */
    prefix?: string;
    /** 后缀 */
    suffix?: string;
  };
  /** 格式化函数 */
  formatter?: (value?: number) => number;
  /** 描述 */
  description?: string;
}

export const businessDataItems = ref<DataItemConfig<BusinessDataVO>[]>([
  {
    key: "turnover",
    label: "营业额",
    icon: "workbench_turnover",
    symbol: { prefix: "￥" },
    description:
      "外卖商家今日营业额是当日通过外卖平台完成所有订单的总金额，由每笔订单的商品、餐盒等费用扣除退款得出，受菜品定价、促销活动、店铺评分等因素影响，是衡量当日经营状况的关键指标。",
  },
  {
    key: "validOrderCount",
    label: "有效订单",
    iconSize: "2em",
    icon: "workbench_order",
    description:
      "有效订单指已完成支付且未被取消的订单数量，反映实际成交的订单情况，排除未支付和取消订单，准确体现商家实际处理订单量。",
  },
  {
    key: "orderCompletionRate",
    label: "订单完成率",
    symbol: { suffix: "%" },
    icon: "workbench_completion",
    description:
      "订单完成率是已完成订单占总订单的比例，体现商家服务能力和运营效率，受备餐速度、骑手配送、客户沟通等因素影响。",
  },
  {
    key: "unitPrice",
    label: "平均客单价",
    symbol: { prefix: "￥" },
    icon: "workbench_price",
    description:
      "平均客单价是营业额与订单数的比值，反映顾客消费水平，受菜品组合、促销策略、推荐销售等因素影响，是优化菜单结构的重要指标。",
  },
  {
    key: "newUsers",
    label: "新增用户",
    icon: "workbench_user",
    description:
      "新增用户指当日首次下单的顾客数量，反映平台推广效果和店铺吸引力，受新客优惠、口碑传播、平台曝光等因素影响。",
  },
]);

export const orderDataItems = ref<DataItemConfig<OrderOverViewVO>[]>([
  {
    key: "allOrders",
    label: "全部订单",
    icon: "order_total",
    query: { status: OrderEnum.ALL },
  },
  {
    key: "waitingOrders",
    label: "待接单",
    iconSize: "1.95em",
    icon: "order_pending",
    query: { status: OrderEnum.PENDING_ORDER },
  },
  {
    key: "deliveredOrders",
    label: "待派送",
    iconSize: "2.5em",
    icon: "order_delivering",
    query: { status: OrderEnum.ORDERS_TAKEN },
  },
  {
    key: "completedOrders",
    label: "已完成",
    icon: "order_completed",
    query: { status: OrderEnum.COMPLETED },
  },
  {
    key: "cancelledOrders",
    label: "已取消",
    icon: "order_cancelled",
    query: { status: OrderEnum.CANCELED },
  },
]);

export const dishDataItems = ref<DataItemConfig<DishOverViewVO>[]>([
  {
    key: "sold",
    label: "已启售",
    icon: "dish_active",
    query: { status: 1 },
  },
  {
    key: "discontinued",
    label: "已停售",
    icon: "dish_inactive",
    query: { status: 0 },
  },
]);

export const setmealDataItems = ref<DataItemConfig<SetmealOverViewVO>[]>([
  {
    key: "sold",
    label: "已启售",
    iconSize: "2.2em",
    icon: "setmeal_active",
    query: { status: 1 },
  },
  {
    key: "discontinued",
    label: "已停售",
    iconSize: "2.2em",
    icon: "setmeal_inactive",
    query: { status: 0 },
  },
]);
