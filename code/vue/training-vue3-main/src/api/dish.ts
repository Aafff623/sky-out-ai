import request from "@/utils/request";
import { StatusEnum } from "@/enums/StatusEnum";

const DISH_BASE_URL = "/store/dish";

class DishAPI {
  /**
   * 新增菜品
   * @param data 菜品表单数据
   */
  static add(data: DishForm) {
    return request<any, ResponseData>({
      url: `${DISH_BASE_URL}`,
      method: "post",
      data: data,
    });
  }

  /**
   * 获取菜品分页列表
   * @param queryParams 查询参数
   */
  static getPage(queryParams: DishPageQuery) {
    return request<any, ResponseData<DishPageVO[]>>({
      url: `${DISH_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  }

  /**
   * 批量删除菜品
   * @param ids 菜品id字符串，例如："1,2,3"
   */
  static batchDelete(ids: string) {
    return request<any, ResponseData>({
      url: `${DISH_BASE_URL}`,
      method: "delete",
      params: { ids },
    });
  }

  /**
   * 通过id获取菜品详情
   * @param id 菜品id
   */
  static getByIdWithFlavor(id: string | number) {
    return request<any, ReqResultType<DishPageVO>>({
      url: `${DISH_BASE_URL}`,
      method: "get",
      params: { id },
    });
  }

  /**
   * 修改菜品
   * @param data 菜品表单数据
   */
  static update(data: DishForm) {
    return request<any, ResponseData>({
      url: `${DISH_BASE_URL}`,
      method: "put",
      data,
    });
  }

  /**
   * 启用禁用菜品
   * @param 菜品ID
   * @param 启用状态，0：禁用；1：启用
   */
  static startOrStop(id: number, status: StatusEnum) {
    return request<any, ResponseData>({
      url: `${DISH_BASE_URL}/status/${status}`,
      method: "post",
      params: { id },
    });
  }
}

export default DishAPI;

/**
 * 菜品分页查询对象
 */
export interface DishPageQuery extends PageQuery {
  /** 菜品名称 */
  name?: string;
  /** 菜品所属分类ID */
  categoryId?: number;
  /** 启用状态 */
  status?: StatusEnum;
}

/** 菜品分页对象 */
export interface DishPageVO {
  /** 菜品ID */
  id?: number;
  /** 菜品名称 */
  name?: string;
  /** 菜品所属分类ID */
  categoryId?: number;
  /** 菜品价格 */
  price?: number;
  /** 菜品图片 */
  image?: string;
  /** 菜品描述 */
  description?: string;
  /** 启用状态 */
  status?: StatusEnum;
  /** 更新时间 */
  updateTime?: Date;
  /** 分类名称 */
  categoryName?: string;
  /** 菜品口味 */
  flavors?: DishFlavor[];
}

/** 菜品表单类型 */
export interface DishForm {
  /** 菜品ID */
  id?: number;
  /** 菜品名称 */
  name?: string;
  /** 菜品所属分类ID */
  categoryId?: number;
  /** 菜品价格 */
  price?: string | number;
  /** 菜品图片 */
  image?: string;
  /** 菜品描述 */
  description?: string;
  /** 启用状态 */
  status?: StatusEnum;
  /** 菜品口味 */
  flavors?: DishFlavor[];
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
