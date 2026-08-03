import request from "@/utils/request";

const COMMENT_MESSAGE_BASE_URL = "/client/comment-message";

class CommentMessageAPI {

  /**
   * 获取互动消息
   * @param params 分页参数
   * @returns 
   */
  static getCommentMessage(params: CommentMessageDTO) {
    return request.get<CommentMessageVO[]>(`${COMMENT_MESSAGE_BASE_URL}?page=${params.page}&pageSize=${params.pageSize}`);
  }

  /**
   * 获取未读互动消息数量
   */
  static getUnreadCount() {
    return request.get<number>(`${COMMENT_MESSAGE_BASE_URL}/unread/count`);
  }
}
export default CommentMessageAPI;

export interface CommentMessageVO {
  /**
   * 根评论ID 用于前端查询是哪个具体菜品或套餐的主评论
   */
  rootCommentId: string;

  /**
   * 回复的评论ID
   */
  repayCommentId: string;

  /**
   * 回复用户ID
   */
  repayAuthorId: number;

  /**
   * 回复用户昵称
   */
  repayAuthorName: string;

  /**
   * 回复用户头像
   */
  repayAuthorAvatar: string;

  /**
   * 回复内容
   */
  repayContent: string;

  /**
   * 商品图片
   */
  image: string;

  /**
   * 菜品ID 用于给前端跳转到菜品评论详情页
   */
  dishId: number;

  /**
   * 套餐ID 用于给前端跳转到套餐评论详情页
   */
  setmealId: number;

  /**
   * 创建时间
   */
  createdTime: Date;
}

export interface CommentMessageDTO {
  page: number;
  pageSize: number;
}

