import request from "@/utils/request";
import { StatusEnum } from "@/enums/StatusEnum";

const SETMEAL_BASE_URL = "/store/setmeal";
export const baseUrls = {
  add: `${SETMEAL_BASE_URL}`,
  getPage: `${SETMEAL_BASE_URL}/page`,
  getById: `${SETMEAL_BASE_URL}`,
  batchDelete: `${SETMEAL_BASE_URL}`,
  startOrStop: `${SETMEAL_BASE_URL}/status`,
  update: `${SETMEAL_BASE_URL}`,
};

class SetmealAPI {
  /**
   * 新增套餐
   * @param data 套餐表单数据
   */
  static add(data: SetmealDTO) {
    return request<any, ResponseData>({
      url: baseUrls.add,
      method: "post",
      data,
    });
  }

  /**
   * 获取套餐分页列表
   * @param queryParams 查询参数
   */
  static getPage(queryParams: SetmealPageQueryDTO) {
    return request<any, PageResult<SetmealVO[]>>({
      url: baseUrls.getPage,
      method: "get",
      params: queryParams,
    });
  }

  /**
   * 批量删除套餐
   * @param ids 套餐id字符串，例如："1,2,3" | [1, 2, 3]
   */
  static batchDelete(ids: SetmealDTO["id"][] | string) {
    return request<any, ReqResultType>({
      url: `${SETMEAL_BASE_URL}`,
      method: "delete",
      params: { ids },
    });
  }

  /**
   * 通过id获取套餐详情
   * @param id 套餐id
   */
  static getById(id: string | number) {
    return request<any, ReqResultType<SetmealVO>>({
      url: baseUrls.getById,
      method: "get",
      params: { id },
    });
  }

  /**
   * 修改套餐
   * @param data 套餐表单数据
   */
  static update(data: SetmealDTO) {
    return request<any, ResponseData>({
      url: baseUrls.update,
      method: "put",
      data,
    });
  }

  /**
   * 启用禁用套餐
   * @param 套餐ID
   * @param 启用状态，0：禁用；1：启用
   */
  static startOrStop(id: number, status: StatusEnum) {
    return request<any, ResponseData>({
      url: `${baseUrls.startOrStop}/${status}`,
      method: "post",
      params: { id },
    });
  }
}

export default SetmealAPI;

export interface SetmealDTO {
  /** 套餐id */
  id?: number;
  /** 该套餐所属分类id */
  categoryId?: number;
  /** 套餐名称 */
  name?: string;
  /** 套餐价格 */
  price?: number;
  /** 状态 0:停用 1:启用 */
  status?: StatusEnum;
  /** 描述信息 */
  description?: string;
  /** 上传到阿里云oss的图片路径 */
  image?: string;
  /** 套餐菜品关系 */
  setmealDishes?: SetmealDish[];
}

export interface SetmealPageQueryDTO extends PageQuery {
  /** 查询套餐名称 */
  name?: string;
  /** 分类id */
  categoryId?: number;
  /** 状态：0-禁用 1-启用 */
  status?: StatusEnum;
}

export interface SetmealVO {
  /** 套餐id */
  id?: number;
  /** 分类id */
  categoryId?: number;
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
  /** 更新时间 */
  updateTime?: string;
  /** 分类名称 */
  categoryName?: string;
  /** 套餐和菜品的关联关系 */
  setmealDishes?: SetmealDish[];
}

export interface SetmealDish {
  /** id */
  id?: number;
  /** 套餐id */
  setmealId?: number;
  /** 菜品id */
  dishId?: number;
  /** 菜品名称 */
  name?: string;
  /** 菜品原价 */
  price?: number;
  /** 套餐中包含的菜品图片 */
  image?: string;
  /** 份数 */
  copies?: number;
}
