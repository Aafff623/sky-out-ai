import request from "@/utils/request";

const WORKSPACE_BASE_URL = "/store/workspace";
export const workspaceUrls = {
  businessData: `${WORKSPACE_BASE_URL}/businessData`,
  orderOverView: `${WORKSPACE_BASE_URL}/overviewOrders`,
  dishOverView: `${WORKSPACE_BASE_URL}/overviewDishes`,
  setmealOverView: `${WORKSPACE_BASE_URL}/overviewSetmeals`,
};

class WorkspaceAPI {
  /**
   * 获取今日运营数据
   */
  static businessData() {
    return request<any, ReqResultType<BusinessDataVO>>({
      url: workspaceUrls.businessData,
      method: "get",
    });
  }

  /**
   * 获取订单概览数据
   */
  static orderOverView() {
    return request<any, ReqResultType<OrderOverViewVO>>({
      url: workspaceUrls.orderOverView,
      method: "get",
    });
  }

  /**
   * 获取菜品概览数据
   */
  static dishOverView() {
    return request<any, ReqResultType<DishOverViewVO>>({
      url: workspaceUrls.dishOverView,
      method: "get",
    });
  }

  /**
   * 获取套餐概览数据
   */
  static setmealOverView() {
    return request<any, ReqResultType<SetmealOverViewVO>>({
      url: workspaceUrls.setmealOverView,
      method: "get",
    });
  }
}

export interface BusinessDataVO {
  /** 营业额 */
  turnover?: number;
  /** 有效订单数 */
  validOrderCount?: number;
  /** 订单完成率 */
  orderCompletionRate?: number;
  /** 平均客单价 */
  unitPrice?: number;
  /** 新增用户数 */
  newUsers?: number;
}

export interface OrderOverViewVO {
  /** 待接单数量 */
  waitingOrders?: number;
  /** 待派送数量 */
  deliveredOrders?: number;
  /** 已完成数量 */
  completedOrders?: number;
  /** 已取消数量 */
  cancelledOrders?: number;
  /** 全部订单 */
  allOrders?: number;
}

export interface DishOverViewVO {
  /** 已启售数量 */
  sold?: number;
  /** 已停售数量 */
  discontinued?: number;
}

export interface SetmealOverViewVO {
  /** 已启售数量 */
  sold?: number;
  /** 已停售数量 */
  discontinued?: number;
}

export default WorkspaceAPI;
