import request from "@/utils/request";
import * as AddressEnum from "@/enums/AddressEnum";

const ADDRESS_BASE_URL = "/client/addressBook";

// api地址
const api = {
  list: `${ADDRESS_BASE_URL}/list`,
  add: `${ADDRESS_BASE_URL}`,
  update: `${ADDRESS_BASE_URL}`,
  delete: `${ADDRESS_BASE_URL}`,
  getById: `${ADDRESS_BASE_URL}`,
  default: `${ADDRESS_BASE_URL}/default`,
};

class AddressAPI {
  /**
   * 查询当前登录用户的所有地址信息
   */
  static list() {
    return request.get<Array<AddressBook>>(api.list);
  }

  /**
   * 新增地址
   */
  static add(address: AddressBook) {
    return request.post<null>(api.add, address);
  }

  /**
   * 根据id修改地址
   */
  static update(address: AddressBook) {
    return request.put<null>(api.update, address);
  }

  /**
   * 根据id删除地址
   * @param id 地址id
   */
  static delete(id: AddressBook["id"]) {
    return request.delete<null>(`${api.delete}/${id}`);
  }

  /**
   * 根据id查询地址
   * @param id 地址id
   */
  static getById(id: AddressBook["id"]) {
    return request.get<AddressBook>(api.getById, { id });
  }

  /**
   * 设置默认地址
   */
  static setDefault(address: AddressBook) {
    return request.put<null>(api.default, address);
  }

  /**
   * 查询默认地址
   */
  static getDefault() {
    return request.get<AddressBook>(api.default);
  }
}

export default AddressAPI;

/**
 * 地址簿实体类
 */
export interface AddressBook {
  /** 地址簿id */
  id?: number;
  /** 用户id */
  userId?: number;
  /** 收货人 */
  consignee?: string;
  /** 手机号 */
  phone?: string;
  /** 性别 0 女 1 男 */
  sex?: AddressEnum.SexEnum;
  /** 省级区划编号 */
  provinceCode?: string;
  /** 省级名称 */
  provinceName?: string;
  /** 市级区划编号 */
  cityCode?: string;
  /** 市级名称 */
  cityName?: string;
  /** 区级区划编号 */
  districtCode?: string;
  /** 区级名称 */
  districtName?: string;
  /** 详细地址 */
  detail?: string;
  /** 标签 */
  label?: AddressEnum.AddressLabelEnum;
  /** 是否为默认地址 0否 1是 */
  isDefault?: AddressEnum.AddressTypeEnum;
  /** 地区 code 数组 */ 
  regions?: string[];
}
