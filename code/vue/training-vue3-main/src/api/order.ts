import request from "@/utils/request";
import { OrderEnum } from "@/enums/OrderEnum";
import { PayMethodEnum, PayStatusEnum } from "@/enums/PayEnum";

const ORDER_BASE_URL = "/store/order";

export const baseUrls = {
  conditionSearch: `${ORDER_BASE_URL}/conditionSearch`,
  details: `${ORDER_BASE_URL}/details`,
  confirm: `${ORDER_BASE_URL}/confirm`,
  rejection: `${ORDER_BASE_URL}/rejection`,
  cancel: `${ORDER_BASE_URL}/cancel`,
  delivery: `${ORDER_BASE_URL}/delivery`,
  complete: `${ORDER_BASE_URL}/complete`,
};

class OrderAPI {
  /**
   * 订单搜索
   * @param params 订单搜索表单
   */
  static conditionSearch(params: OrderPageQuery) {
    return request<any, PageResult<OrderPageVO[]>>({
      url: baseUrls.conditionSearch,
      method: "get",
      params,
    });
  }

  /**
   * 根据id查询订单详情
   * @param id 订单id
   */
  static details(id: number | string) {
    return request<any, PageResult<OrderPageVO[]>>({
      url: baseUrls.details,
      method: "get",
      params: { id },
    });
  }

  /**
   * 商家接单
   * @param id 订单id
   */
  static confirm(id: number | string) {
    return request<any, ResponseData>({
      url: `${baseUrls.confirm}/${id}`,
      method: "put",
    });
  }

  /**
   * 商家拒单
   * @param data 拒单DTO
   */
  static rejection(data: OrdersRejectionDTO) {
    return request<any, ResponseData>({
      url: baseUrls.rejection,
      method: "put",
      data,
    });
  }

  /**
   * 商家取消订单
   * @param data 取消订单DTO
   */
  static cancel(data: OrdersCancelDTO) {
    return request<any, ResponseData>({
      url: baseUrls.cancel,
      method: "put",
      data,
    });
  }

  /**
   * 商家派送订单
   * @param id 订单id
   */
  static delivery(id: number | string) {
    return request<any, ResponseData>({
      url: `${baseUrls.delivery}/${id}`,
      method: "put",
    });
  }

  /**
   * 完成订单
   * @param id 订单id
   */
  static complete(id: number | string) {
    return request<any, ResponseData>({
      url: `${baseUrls.complete}/${id}`,
      method: "put",
    });
  }
}

export default OrderAPI;

/**
 * 订单分页查询对象
 */
export interface OrderPageQuery extends PageQuery {
  /** 用户id */
  userId?: number | string;
  /** 订单号 */
  number?: string;
  /** 手机号 */
  phone?: string;
  /** 订单状态 */
  status?: OrderEnum;
  /** 开始时间 */
  beginTime?: Date | string;
  /** 结束时间 */
  endTime?: Date | string;
}

/** 订单分页对象 */
export interface OrderPageVO {
  /** 订单ID */
  id?: number;
  /** 订单号 */
  number?: string;
  /** 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款 */
  status?: OrderEnum;
  /** 下单用户id */
  userId?: number | string;
  /** 地址id */
  addressBookId: number;
  /** 下单时间 */
  orderTime: Date | string;
  /** 结账时间 */
  checkoutTime: Date | string;
  /** 支付方式 1微信，2支付宝 */
  payMethod: PayMethodEnum;
  /** 支付状态 0未支付 1已支付 2退款 */
  payStatus: PayStatusEnum;
  /** 实收金额 */
  amount: number | string;
  /** 备注 */
  remark: string;
  /** 用户名 */
  userName: string;
  /** 手机号 */
  phone: string;
  /** 地址 */
  address: string;
  /** 收货人 */
  consignee: string;
  /** 订单取消原因 */
  cancelReason: string | null;
  /** 订单拒绝原因 */
  rejectionReason: string | null;
  /** 订单取消时间 */
  cancelTime: Date | string | null;
  /** 预计送达时间 */
  estimatedDeliveryTime: Date | string | null;
  /** 配送状态  1立即送出  0选择具体时间 */
  deliveryStatus: number;
  /** 送达时间 */
  deliveryTime: Date | string;
  /** 打包费 */
  packAmount: number | string;
  /** 餐具数量 */
  tablewareNumber: number;
  /** 餐具数量状态  1按餐量提供  0选择具体数量 */
  tablewareStatus: number;
  /** 订单菜品信息，例如：可口可乐*1;经典酸菜鮰鱼*1; */
  orderDishes: string;
  /** 订单菜品详情信息，例如：{id:1,name:'可口可乐',price:10,number:1} */
  orderDetailList: any[];
  /** 订单类型 */
  orderType?: 1 | 2;
  /** 取餐号 */
  pickupNumber?: string;
}

/** 商家拒单订单类型 */
export interface OrdersRejectionDTO {
  /** 订单ID */
  id?: number;
  /** 订单拒绝原因 */
  rejectionReason?: string;
}

/** 商家取消订单类型 */
export interface OrdersCancelDTO {
  /** 订单ID */
  id?: number;
  /** 订单取消原因 */
  cancelReason?: string;
}

/** 订单socket消息类型 */
export interface OrderSocketMessage {
  /** 订单ID */
  orderId?: number;
  /** 内容 */
  content?: string;
  /** 提示类型，1：新订单提醒，2：催单提醒 */
  type?: 1 | 2;
}

export type CombinedOrderDTO = {
  /** 订单ID */
  id?: number;
} & Omit<OrdersRejectionDTO, "id"> &
  Omit<OrdersCancelDTO, "id">;
