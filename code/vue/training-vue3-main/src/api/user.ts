import request from "@/utils/request";

const USER_BASE_URL = "/store/user";
export const baseUrls = {
  getPage: `${USER_BASE_URL}/page`,
};

/** 小程序用户分页查询对象 */
export interface WxUserPageQuery extends PageQuery {
  /** 昵称 */
  name?: string;
  /** 手机号 */
  phone?: string;
}

/** 小程序用户分页对象 */
export interface WxUserPageVO {
  /** 用户ID */
  id?: number;
  /** 微信openid */
  openid?: string;
  /** 昵称 */
  name?: string;
  /** 手机号 */
  phone?: string;
  /** 头像URL */
  avatar?: string;
  /** 注册时间 */
  createTime?: string;
}

class UserAPI {
  /**
   * 获取小程序用户分页列表
   * @param queryParams 查询参数
   */
  static getPage(queryParams: WxUserPageQuery) {
    return request<any, PageResult<WxUserPageVO[]>>({
      url: baseUrls.getPage,
      method: "get",
      params: queryParams,
    });
  }
}

export default UserAPI;
