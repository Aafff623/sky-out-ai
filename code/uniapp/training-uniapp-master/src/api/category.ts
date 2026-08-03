import request from "@/utils/request";
import { StatusEnum } from "@/enums/StatusEnum";

const CATEGORY_BASE_URL = "/client/category";

// api地址
const api = {
  list: `${CATEGORY_BASE_URL}/list`,
};

class CategoryAPI {
  /**
   * 获取菜品分类列表
   */
  static list() {
    return request.get<CategoryVO[]>(api.list);
  }
}

export default CategoryAPI;

/**
 * 获取分类列表响应数据VO对象
 */
export interface CategoryVO extends ResponseVOCommonFields {
  /** 分类id */
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
