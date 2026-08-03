import request from "@/utils/request";

const SHOPPINGCART_BASE_URL = "/client/shoppingCart";

// api地址
const api = {
  add: `${SHOPPINGCART_BASE_URL}/add`,
  list: `${SHOPPINGCART_BASE_URL}/list`,
  clean: `${SHOPPINGCART_BASE_URL}/clean`,
  sub: `${SHOPPINGCART_BASE_URL}/sub`,
  update: `${SHOPPINGCART_BASE_URL}/update`,
};

class ShoppingCartAPI {
  /**
   * 添加购物车
   * @param 菜品id
   */
  static add(params: Pick<ShoppingCart, "dishId">) {
    return request.post<null>(api.add, params);
  }

  /**
   * 查看购物车
   */
  static list() {
    return request.get<ShoppingCart[]>(api.list, {}, { isPrompt: false });
  }

  /**
   * 更新购物车中商品的数量
   */
  static update(params: ShoppingCart) {
    return request.put<null>(api.update, params);
  }

  /**
   * 清空购物车
   */
  static clean() {
    return request.delete<null>(api.clean);
  }

  /**
   * 删除购物车中一个商品
   */
  static sub(
    params: Pick<ShoppingCart, "dishId" | "setmealId" | "number" | "dishFlavor">
  ) {
    return request.post<null>(api.sub, params);
  }
}

export default ShoppingCartAPI;

/**
 * 购物车实体对象
 */
export interface ShoppingCart extends ResponseVOCommonFields {
  /** 购物车id */
  id?: number;
  /** 商品名称 */
  name?: string;
  /** 用户id */
  userId?: number | string;
  /** 排序 */
  sort?: number;
  /** 菜品id */
  dishId?: number | string;
  /** 套餐id */
  setmealId?: number | string;
  /** 菜品数量 */
  number?: number;
  /** 菜品金额 */
  amount?: string;
  /** 菜品图片 */
  image?: string;
  /** 菜品口味 */
  dishFlavor?: string;
  /** 创建时间 */
  createTime?: Date;
}

/**
 * 购物车DTO
 */
export type ShoppingCartDTO = Pick<
  ShoppingCart,
  "dishId" | "setmealId" | "number" | "dishFlavor" | "name" | "image" | "amount"
>;

/**
 * 购物车VO
 */
export interface ShoppingCartVO extends ShoppingCart {}
