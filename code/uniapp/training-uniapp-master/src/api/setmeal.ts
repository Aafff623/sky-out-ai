import request from "@/utils/request";
import { StatusEnum } from "@/enums/StatusEnum";
import { CategoryTypeEnum } from "@/enums/CategoryEnum";

const SETMEAL_BASE_URL = "/client/setmeal";

// api地址
const api = {
  list: `${SETMEAL_BASE_URL}/list`,
  setmealOfDish: `${SETMEAL_BASE_URL}/dish`,
};

class SetmealAPI {
  /**
   * 获取套餐列表
   */
  static getSetmealCateId(categoryId?: number | string, tabIndex: number = 0) {
    const params = {};
    if (categoryId) {
      Object.assign(params, { categoryId });
    }
    Object.assign(params, { tabIndex });
    return request.get<SetmealVO[]>(api.list, params);
  }

  /**
   * 获取套餐中包含的菜品列表
   * @param id 套餐id
   */
  static getSetmealOfDishList(id?: number | string) {
    return request.get<DishItemVO[]>(api.setmealOfDish, { id });
  }
}

export default SetmealAPI;

/**
 * 获取菜品列表响应数据VO对象
 */
export interface SetmealVO extends ResponseVOCommonFields {
  /** 套餐id */
  id?: number;
  /** 分类id */
  categoryId?: number;
  /**
   * 套餐名称
   */
  categoryName?: string;
  /** 套餐名称 */
  name?: string;
  /** 套餐价格 */
  price?: number;
  /** 状态 0:停用 1:启用 */
  status?: StatusEnum;
  /** 描述信息 */
  description?: string;
  /** 图片 */
  image?: string;
  /** 套餐热度 */
  hotSpot?: number;
  /** 加入购物车的套餐数量 */
  number?: number;
  /** 菜品所属分类类型 */
  type?: CategoryTypeEnum;
}

export interface DishItemVO {
  /** 菜品名称 */
  name?: string;
  /** 份数 */
  copies?: number;
  /** 套餐中包含的菜品图片 */
  image?: string;
  /** 描述信息 */
  description?: string;
}
