import { CategoryTypeEnum } from "@/enums/CategoryEnum";
import request from "@/utils/request";

const DISH_BASE_URL = "/client/dish";

// api地址
const api = {
  list: `${DISH_BASE_URL}/list`,
};

class DishAPI {
  /**
   * 获取菜品列表
   */
  static getDishByCateId(categoryId?: number | string, tabIndex: number = 0) {
    const params = {};
    if (categoryId) {
      Object.assign(params, { categoryId });
    }
    Object.assign(params, { tabIndex });
    return request.get<DishVO[]>(api.list, params);
  }

  /**
   * 获取菜品详情
   * @param id 菜品id
   */
  static getDishById(id: number | string) {
    return request.get<DishVO>(`${DISH_BASE_URL}/${id}`);
  }
  /**
   * 获取推荐菜品
   * @param 
   */
  static getRecommendDish() {
    return request.get<DishVO[]>(`${DISH_BASE_URL}/recommend`);
  }
}

export default DishAPI;

/**
 * 获取菜品列表响应数据VO对象
 */
export interface DishVO extends ResponseVOCommonFields {
  /** 菜品id */
  id?: number;
  /** 菜品名称 */
  name?: string;
  /** 菜品分类id */
  categoryId?: number;
  /** 菜品分类名称 */
  categoryName?: string;
  /** 菜品价格 */
  price?: string;
  /** 图片url */
  image?: string;
  /** 描述信息 */
  description?: string;
  /** 菜品状态：0-停售、1-起售 */
  status?: string;
  /** 菜品热度 */
  hotSpot?: number;
  /** 加入购物车的菜品数量 */
  number?: number;
  /** 菜品口味 */
  flavors?: DishFlavor[];
  /** 菜品所属分类类型 */
  type?: CategoryTypeEnum;
}

/** 菜品口味 */
export interface DishFlavor {
  /** 菜品口味id */
  id?: number;
  /** 菜品id */
  dishId?: number;
  /** 口味名称 */
  name?: string;
  /** 口味值 */
  value?: string | string[];
}
