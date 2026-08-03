import request from "@/utils/request";
import { ShopStatusEnum } from "@/enums/StatusEnum";

const SHOP_BASE_URL = "/client/shop";
const SHOP_INFO_BASE_URL = "/client/shopInfo";

const api = {
  status: `${SHOP_BASE_URL}/status`,
  info: `${SHOP_INFO_BASE_URL}`,
};

class ShopAPI {
  /**
   * 获取店铺的营业状态
   */
  static getStatus() {
    return request.get<ShopStatusEnum>(api.status);
  }

  /**
   * 获取店铺信息
   */
  static getShopInfo() {
    return request.get<ShopInfoVO>(api.info);
  }
}

export default ShopAPI;

/** 商家信息VO */
export interface ShopInfoVO {
  /** 商家名称 */
  name?: string;
  /** 商家LOGO */
  logo?: string;
  /** 商家地址 */
  address?: string;
  /** 营业时间 */
  businessHours?: string;
  /** 联系电话 */
  contactPhone?: string;
  /** 经度 */
  longitude?: string; 
  /** 纬度 */
  latitude?: string;
  /** 配送范围（公里） */
  deliveryRange?: number;
  /** 起送价 */
  minimumOrder?: number;
  /** 配送费 */
  deliveryFee?: number;
}
