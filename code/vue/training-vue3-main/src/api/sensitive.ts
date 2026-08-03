import request from "@/utils/request";
const SENSITIVE_BASE_URL = '/sensitive';
// 敏感词分页查询参数
export interface SensitivePageQueryDTO {
  page: number;
  pageSize: number;
  sensitive?: string;
}

// 敏感词实体
export interface Sensitive {
  id?: number;
  sensitives: string;
  createdTime?: Date;
  updatedTime?: Date;
}

class SensitiveAPI {
  /**
   * 新增敏感词
   * @param sensitive 敏感词
   */
  static addSensitive(sensitive: string) {
    return request<any, ResponseData>({
      url: `${SENSITIVE_BASE_URL}`,
      method: 'post',
      params: { sensitive }
    });
  }

  /**
   * 修改敏感词
   * @param id 敏感词ID
   * @param sensitive 敏感词内容
   */
  static updateSensitive(form: SensitiveForm) {
    return request<any, ResponseData>({
      url: `${SENSITIVE_BASE_URL}/${form.id}`,
      method: 'put',
      params: { sensitive: form.sensitives }
    });
  }

  /**
   * 分页查询敏感词
   * @param queryParams 分页查询参数
   */
  static page(queryParams: SensitivePageQueryDTO) {
    return request<any, ResponseData<Sensitive[]>>({
      url: `${SENSITIVE_BASE_URL}`,
      method: 'get',
      params: queryParams
    });
  }

  /**
   * 删除敏感词
   * @param ids 敏感词ID列表
   */
  static delete(ids: number[]) {
    return request<any, ResponseData>({
      url: `${SENSITIVE_BASE_URL}`,
      method: 'delete',
      params: { ids }
    });
  }
}

export default SensitiveAPI;

export interface PageResult<T = any> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

/** 敏感词表单类型 */
export interface SensitiveForm {
  /** ID */
  id?: number;
  /** 名称 */
  sensitives?: string;
}
