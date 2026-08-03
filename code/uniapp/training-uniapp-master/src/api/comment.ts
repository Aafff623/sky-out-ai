import request from "@/utils/request";

const COMMENT_BASE_URL = "/client/comment";

class CommentAPI {
  /**
   * 获取评论列表
   * @param params 筛选参数
   */
  static getCommentList(params: CommentDto) {

    return request.post<PageResult<CommentVO>>(`${COMMENT_BASE_URL}/load`, params);
  }

  /**
   * 添加评论
   * @param dishId 菜品id
   * @param content 评论内容
   */
  static addComment(params: CommentSaveVO) {
    return request.post<CommentVO>(`${COMMENT_BASE_URL}/save`, params);
  }
  /**
   * 点赞
   * @param commentId 评论id
   */
  static like(commentId: string, operation: number) {
    return request.post<CommentVO>(`${COMMENT_BASE_URL}/like`, { commentId, operation });
  }

  /**
   * 查询订单是否已评论
   * @param orderIds 订单ids
   */
  static isComment(orderIds: number[]) {
    return request.post<{ [key: string]: string }>(`${COMMENT_BASE_URL}/isComment`, { orderIds });
  }

  /**
   * 批量删除评论
   * @param ids 评论ID数组
   */
  static deleteByIds(ids: string[] | string) {
    // 如果传入的是数组，则转换为逗号分隔的字符串
    const idsParam = Array.isArray(ids) ? ids.join(',') : ids;
    // console.log(idsParam);

    return request.delete<ResponseData>(`${COMMENT_BASE_URL}?ids=${idsParam}`);
  }
}
export default CommentAPI;

export interface CommentVO extends ResponseVOCommonFields {
  /**
   * id
   */
  id: string;

  /**
   * 用户ID  发表评论的用户id
   */
  authorId: number;

  /**
   * 用户昵称
   */
  authorName: string;

  /**
   * 菜品id
   */
  dishId: number;

  /**
   * 订单id
   */
  orderId: number;

  /**
   * 评论内容
   */
  content: string;

  /**
   * 作者头像
   */
  image: string;

  /**
   * 点赞数
   */
  likes: number;

  /**
   * 回复数
   */
  reply: number;

  /**
   * 文章标记
   * 0 普通评论
   * 1 热点评论
   * 2 推荐评论
   * 3 置顶评论
   * 4 精品评论
   * 5 大V 评论
   */
  flag: number;

  /**
   * 评论排列序号
   */
  ord: number;

  /**
   * 创建时间
   */
  createdTime: string;

  /**
   * 更新时间
   */
  updatedTime: string;

  /**
   * 评论状态  0 关闭状态   1 正常状态
   */
  status: boolean;

  /**
   * 评论点赞状态  0 未点赞   1 已点赞

   */
  operation?: number | null | undefined;

  /**
   * 是否已评论 0 未评论   1 已评论
   */
  isComment?: number | null | undefined;

  /**
   * 关联的菜品名称
   */
  moreDishName: string
}

export interface CommentDto extends ResponseVOCommonFields {
  /**
   * 菜品id
   */
  dishId: number;
  /**
   * 套餐id

   */
  setmealId: number;

  /**
   * 查询最小时间
   */
  minDate: Date

  /**
   *  分页参数
   */
  size: number;
}

export interface CommentSaveVO extends ResponseVOCommonFields {

  /**
   * 评论id
   */
  orderId: number

  /**
   * 菜品id
   */
  dishIds: number[];

  /**
   * 套餐id
   */

  setmealIds: number[]

  /**
   * 订单编号
   */
  orderNumber?: string;

  /**
   * 评论内容
   */
  content: string;
}