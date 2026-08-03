import request from "@/utils/request";

const BASE_URL = "/client/ai-assistant/recommend"

class AiRecommendAPI {
  /**
   * AI智能推荐菜品
   */
  static recommend(data: RecommendRequestDTO) {
    return request.post<RecommendResponseVO[]>(BASE_URL, data);
  }
}

export default AiRecommendAPI;

/** AI推荐请求数据类型 */
export interface RecommendRequestDTO {
  /** 用户偏好描述 */
  preference: string;
  /** 店铺ID */
  shopId: number;
}

/** AI推荐响应数据类型 */
export interface RecommendResponseVO {
  /** 菜品ID */
  id: number;
  /** 菜品名称 */
  name: string;
  /** 菜品价格 */
  price: number;
  /** 菜品图片 */
  image: string;
  /** 推荐理由 */
  reason: string;
}
