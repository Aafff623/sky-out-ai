import request from "@/utils/request";
import { PayMethodsEnum } from "@/enums/PayEnum";
import { TablewareStatusEnum, OrderStatus } from "@/enums/OrderEnum";

const ORDER_BASE_URL = "/client/order";

// api地址
const api = {
  submit: `${ORDER_BASE_URL}/submit`,
  reminder: `${ORDER_BASE_URL}/reminder`,
  historyOrders: `${ORDER_BASE_URL}/historyOrders`,
  orderDetail: `${ORDER_BASE_URL}/orderDetail`,
  cancel: `${ORDER_BASE_URL}/cancel`,
  repetition: `${ORDER_BASE_URL}/repetition/`,
  payment: `${ORDER_BASE_URL}/payment`,
  updatePayStatus: `${ORDER_BASE_URL}/updatePayStatus`,
};

class OrderAPI {
  /**
   * 用户下单
   * @param 订单提交DTO
   */
  static submit(params: OrdersSubmitDTO) {
    return request.post<OrderSubmitVO>(api.submit, params);
  }

  /**
   * 订单支付
   * @param 订单支付DTO
   */
  static payment(
    params: OrdersPaymentDTO,
    options: ReqOptionsConfig = { load: false }
  ) {
    return request.put<null>(api.payment, params, options);
  }

  /**
   * 催单
   * @param 订单id
   */
  static reminder(id: number | string) {
    return request.get<null>(api.reminder, { id });
  }

  /**
   * 修改支付方式
   */
  static changePaymentMethod(params: OrdersPaymentDTO) {
    return request.put<null>(api.updatePayStatus, params)
  }

  /**
   * 历史订单查询
   * @param 订单id
   */
  static historyOrders({ page, pageSize, status }: OrderQueryDTO) {
    return request.get<PageResult<OrderVO>>(
      api.historyOrders,
      {
        page,
        pageSize,
        status,
      },
      { load: true }
    );
  }

  /**
   * 根据id查询订单详情
   * @param 订单id
   */
  static details(id: number | string) {
    return request.get<OrderVO>(api.orderDetail, { id });
  }

  /**
   * 用户取消订单
   * @param orderCancelDTO 订单取消DTO
   */
  static cancel(
    orderCancelDTO: OrderCancelDTO,
    options: ReqOptionsConfig = {}
  ) {
    return request.put<null>(api.cancel, orderCancelDTO, options);
  }

  /**
   * 再来一单
   * @param 订单id
   */
  static repetition(id: number | string) {
    return request.put<null>(api.repetition + id);
  }
}

export default OrderAPI;

/**
 * 订单实体类
 */
export interface Order {
  /** 订单配送地址 */
  address?: string;
  /** 地址簿ID */
  addressBookId?: number;
  /** 实收金额 */
  amount?: string;
  /** 取消原因 */
  cancelReason?: string;
  /** 订单取消时间 */
  cancelTime?: Date | string;
  /** 订单结账时间 */
  checkoutTime?: Date | string;
  /** 收货人姓名 */
  consignee?: string;
  /** 配送状态，1：立即送出，0：选择具体时间 */
  deliveryStatus?: number;
  /** 实际配送时间 */
  deliveryTime?: Date | string;
  /** 预计送达时间 */
  estimatedDeliveryTime?: Date | string;
  /** 订单ID */
  id?: string | number;
  /** 订单编号 */
  number?: string;
  /** 下单时间 */
  orderTime?: string;
  /** 包装费用 */
  packAmount?: number;
  /** 支付方式 1微信，2支付宝 */
  payMethod?: number;
  /** 支付状态 0未支付 1已支付 2退款 */
  payStatus?: number;
  /** 联系电话 */
  phone?: string;
  /** 拒收原因 */
  rejectionReason?: string;
  /** 订单备注信息 */
  remark?: string;
  /** 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款 */
  status?: OrderStatus;
  /** 餐具数量 */
  tablewareNumber?: number;
  /** 餐具数量状态  1按餐量提供  0选择具体数量 */
  tablewareStatus?: number;
  /** 下单用户id */
  userId?: number;
  /** 用户名 */
  userName?: string;
  /** 订单类型 1：外卖，2：堂食 */
  orderType?: 1 | 2;
  /** 取餐号（堂食专用） */
  pickupNumber?: number | string;
}

/**
 * 订单支付DTO
 */
export interface OrdersPaymentDTO {
  /** 订单号 */
  orderNumber?: string | number;
  /** 付款方式，1：微信，2：支付宝 */
  payMethod?: PayMethodsEnum;
}

/**
 * 订单提交DTO
 */
export interface OrdersSubmitDTO {
  /** 地址簿id */
  addressBookId?: number | string;
  /** 付款方式，1：微信，2：支付宝 */
  payMethod?: PayMethodsEnum;
  /** 备注 */
  remark?: string;
  /** 预计送达时间 */
  estimatedDeliveryTime?: Date | string;
  /** 配送状态  1立即送出  0选择具体时间 */
  deliveryStatus?: number;
  /** 餐具数量 */
  tablewareNumber?: number;
  /** 餐具数量状态  1按餐量提供  0选择具体数量 */
  tablewareStatus?: TablewareStatusEnum;
  /** 打包费 */
  packAmount?: number;
  /** 总金额 */
  amount?: string;
  /** 订单类型, 1：外卖，2：堂食 */
  orderType?: 1 | 2;
}

/**
 * 订单提交VO
 */
export interface OrderSubmitVO {
  /** 订单id */
  id?: Order["id"];
  /** 订单号 */
  orderNumber?: string;
  /** 订单金额 */
  orderAmount?: string;
  /** 下单时间 */
  orderTime?: Date | string;
}

/**
 * 订单VO
 */
export interface OrderVO extends Order {
  /** 订单菜品信息 */
  orderDishes?: string;
  /** 订单详情 */
  orderDetailList?: Array<OrderDetail>;
  /** 是否已评论 0 未评论   1 已评论 */
  isComment?: number | boolean;
}

/**
 * 订单明细VO
 */
export interface OrderDetail {
  /** 订单明细id */
  id?: Order["id"];
  /** 商品名称 */
  name?: string;
  /** 订单id */
  orderId?: number | string;
  /** 菜品id */
  dishId?: number | string;
  /*** 套餐id*/
  setmealId: number | string
  /** 数量 */
  number?: number;
  /** 金额 */
  amount?: string;
  /** 图片 */
  image?: string;
}

/**
 * 订单取消DTO
 * @param id 订单id
 * @param cancelReason 取消原因
 */
export interface OrderCancelDTO {
  /** 订单id */
  id: Order["id"];
  /** 取消原因 */
  cancelReason: string;
}

/**
 * 订单分页查询参数
 */
export interface OrderQueryDTO extends PageQuery {
  /** 订单状态 */
  status: OrderVO["status"];
}
