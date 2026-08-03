import request from "@/utils/request";
import { StatusEnum } from "@/enums/StatusEnum";

const SHOP_BASE_URL = "/store/shop";

class ShopAPI {
  /** 设置店铺营业状态 */
  static status(status: StatusEnum) {
    return request<any, ResponseData>({
      url: `${SHOP_BASE_URL}/status/${status}`,
      method: "put",
    });
  }

  /** 获取店铺营业状态*/
  static getStatus() {
    return request<any, ResponseData<StatusEnum>>({
      url: `${SHOP_BASE_URL}/status`,
      method: "get",
    });
  }
}

export default ShopAPI;
