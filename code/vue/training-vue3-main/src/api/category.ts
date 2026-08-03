import request from "@/utils/request";
import { StatusEnum } from "@/enums/StatusEnum";

const CATEGORY_BASE_URL = "/store/category";

class CategoryAPI {
  /**
   * 新增分类
   * @param data 分类表单数据
   */
  static add(data: CategoryForm) {
    return request<any, ResponseData>({
      url: `${CATEGORY_BASE_URL}`,
      method: "post",
      data: data,
    });
  }

  /**
   * 修改分类
   * @param data 分类表单数据
   */
  static update(data: CategoryForm) {
    return request<any, ResponseData>({
      url: `${CATEGORY_BASE_URL}`,
      method: "put",
      data,
    });
  }

  /**
   * 获取分类分页列表
   * @param queryParams 查询参数
   */
  static getPage(queryParams: CategoryPageQuery) {
    return request<any, PageResult<CategoryPageVO[]>>({
      url: `${CATEGORY_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  }

  /**
   * 启用禁用分类
   * @param 分类ID
   * @param 启用状态，0：禁用；1：启用
   */
  static startOrStop(categoryId: number, status: StatusEnum) {
    return request<any, ResponseData>({
      url: `${CATEGORY_BASE_URL}/status`,
      method: "post",
      params: {
        id: categoryId,
        status,
      },
    });
  }

  /**
   * 根据类型获取分类列表
   * @param type 分类类型
   */
  static getByType(type: number) {
    return request<any, ResponseData<CategoryPageVO[]>>({
      url: `${CATEGORY_BASE_URL}/list`,
      method: "get",
      params: {
        type,
      },
    });
  }

  /**
   * 根据id删除分类
   */
  static deleteById(id: string | number) {
    return request<any, ResponseData>({
      url: `${CATEGORY_BASE_URL}`,
      method: "delete",
      params: { id },
    });
  }
}

export default CategoryAPI;

/**
 * 分类分页查询对象
 */
export interface CategoryPageQuery extends PageQuery {
  /** 分类名称 */
  name?: string;
  /** 分类类型 */
  type?: number;
  /** 启用状态 */
  status?: StatusEnum;
}

/** 分类分页对象 */
export interface CategoryPageVO extends ResponseVOFields {
  /** 分类ID */
  id?: number;
  /** 分类名称 */
  name?: string;
  /** 分类类型 */
  type?: number;
  /** 排序 */
  sort?: number;
  /** 启用状态 */
  status?: StatusEnum;
}

/** 分类表单类型 */
export interface CategoryForm {
  /** 分类ID */
  id?: number;
  /** 分类名称 */
  name?: string;
  /** 分类类型 */
  type?: number;
  /** 分类排序 */
  sort?: number;
}
