import request from "@/utils/request";

const BASE_URL = "/client/ai-comment";

class AiCommentAPI {
  /**
   * ai评论扩写
   * @param dishIds 菜品id
   * @returns 
   */
  static aiCommentExpandList(params: AiCommentDto) {
    // 修改为查询参数方式
    return request.post<AiCommentDto>(`${BASE_URL}/expand`, params)
  }
}

export interface AiCommentDto {
  /**
   * 菜品ids
   */
  dishIds: number[]

  /**
   * 套餐ids
   */
  setmealIds: number[]

  /**
   * 原始评论
   */
  originalComment: string

  /**
   * 原始输入内容
   */
  content: string

  /*AI扩写类型 1帮写 2续写 3润色 4精简*/
  type: number

}
export default AiCommentAPI;