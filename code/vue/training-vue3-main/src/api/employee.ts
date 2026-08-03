import request from "@/utils/request";
import { StatusEnum } from "@/enums/StatusEnum";

const EMPLOYEE_BASE_URL = "/store/employee";
export const baseUrls = {
  getInfo: `${EMPLOYEE_BASE_URL}/me`,
  login: `${EMPLOYEE_BASE_URL}/login`,
  add: `${EMPLOYEE_BASE_URL}`,
  getPage: `${EMPLOYEE_BASE_URL}/page`,
  startOrStop: `${EMPLOYEE_BASE_URL}/status`,
  update: `${EMPLOYEE_BASE_URL}`,
  deleteById: `${EMPLOYEE_BASE_URL}`,
  logout: `${EMPLOYEE_BASE_URL}/logout`,
}

class EmployeeAPI {
  /**
   * 获取当前登录用户信息
   */
  static getInfo() {
    return request<any, ReqResultType<UserInfo>>({
      url: baseUrls.getInfo,
      method: "get",
    });
  }

  /** 登录接口*/
  static login(data: LoginData) {
    return request<any, ResponseData<LoginResult>>({
      url: baseUrls.login,
      method: "post",
      data,
    });
  }

  /**
   * 添加用户
   * @param data 用户表单数据
   */
  static add(data: UserForm) {
    return request<any, ResponseData>({
      url: baseUrls.add,
      method: "post",
      data: data,
    });
  }

  /**
   * 获取用户分页列表
   * @param queryParams 查询参数
   */
  static getPage(queryParams: UserPageQuery) {
    return request<any, PageResult<UserPageVO[]>>({
      url: baseUrls.getPage,
      method: "get",
      params: queryParams,
    });
  }

  /**
   * 启用禁用员工账号
   * @param 用户ID
   * @param 启用状态，0：禁用；1：启用
   */
  static startOrStop(userId: number, status: number) {
    return request<any, ResponseData>({
      url: baseUrls.startOrStop,
      method: "post",
      params: {
        id: userId,
        status,
      },
    });
  }

  /**
   * 修改用户
   * @param data 用户表单数据
   */
  static update(data: UserForm) {
    return request<any, ResponseData>({
      url: baseUrls.update,
      method: "put",
      data,
    });
  }

  /**
   * 根据id删除用户
   */
  static deleteById(id: string | number) {
    return request<any, ResponseData>({
      url: baseUrls.deleteById,
      method: "delete",
      params: { id },
    });
  }

  /** 注销 接口*/
  static logout() {
    return request({
      url: baseUrls.logout,
      method: "post",
    });
  }
}

export default EmployeeAPI;

/** 登录请求参数 */
export interface LoginData {
  /** 用户名 */
  username?: string;
  /** 密码 */
  password?: string;
}

/** 登录响应 */
export interface LoginResult {
  /** 用户id */
  id?: number;
  /** 用户名 */
  username?: string;
  /** 姓名 */
  name?: string;
  /** 访问token */
  token?: string;
}

/** 登录用户信息 */
export interface UserInfo {
  /** 用户ID */
  id?: number;

  /** 用户名 */
  username?: string;

  /** 昵称 */
  name?: string;

  /** 头像URL */
  avatar?: string;

  /** 角色 */
  roles: string[];

  /** 权限 */
  perms: string[];
}

/**
 * 用户分页查询对象
 */
export interface UserPageQuery extends PageQuery {
  /** 用户名 */
  name?: string;
}

/** 用户分页对象 */
export interface UserPageVO extends ResponseVOFields {
  /** 用户ID */
  id?: number;
  /** 用户名 */
  username?: string;
  /** 姓名 */
  name?: string;
  /** 密码 */
  password?: string;
  /** 手机号 */
  phone?: string;
  /** 性别 */
  sex?: number;
  /** 员工启用状态 */
  status?: StatusEnum;
}

/** 用户表单类型 */
export interface UserForm {
  /** 用户ID */
  id?: number;
  /** 用户名 */
  username?: string;
  /** 姓名 */
  name?: string;
  /** 手机号 */
  phone?: string;
  /** 性别 */
  sex?: number;
  /**
   * 商家头像
   */
  avatar?: string;
}
