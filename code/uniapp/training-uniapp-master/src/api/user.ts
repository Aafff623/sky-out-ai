import request from "@/utils/request";

const CATEGORY_BASE_URL = "/client/user";

const api = {
  login: `${CATEGORY_BASE_URL}/login`,
  info: `${CATEGORY_BASE_URL}/info`,
  update: `${CATEGORY_BASE_URL}`,
};

class UserAPI {
  /**
   * 微信授权登录
   */
  static login(code: string, options?: ReqOptionsConfig) {
    return request.post<UserLoginVO>(api.login, { code }, options);
  }

  /**
   * 获取用户信息
   */
  static info() {
    return request.get<UserVO>(api.info);
  }

  /**
   * 更新用户信息
   */
  static update(data: UserDTO) {
    return request.put<UserVO>(api.update, data);
  }
}

export default UserAPI;

/** 微信登录返回数据类型 */
export interface UserLoginVO {
  /** 新增用户id */
  id?: number;
  /** openid用户唯一标识 */
  openid?: string;
  /** jwt校验令牌token */
  token?: string;
}

/** 用户信息更新数据类型 */
export interface UserDTO {
  id?: number;
  phone?: string;
  sex?: number;
  name?: string;
  avatar?: string;
}

/** 用户信息返回数据类型 */
export interface UserVO {
  /** 新增用户id */
  id?: number;
  /** openid用户唯一标识 */
  openid?: string;
  /** 微信用户昵称 */
  name?: string;
  phone?: string;
  sex?: number;
  /** 微信用户头像 */
  avatar?: string;
  /** 注册时间 */
  createTime?: Date;
}
