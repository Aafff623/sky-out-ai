import request from '@/utils/request'
const COMMENT_REPAY_BASE_URL = "/store/comment-repay"
import { Comment } from '@/api/comment'

class CommentRepayAPI {
  /**
   * 批量删除评论回复
   * @param ids 评论回复ID数组
   */
  static deleteCommentRepay(ids: string) {
    return request.delete<any, ResponseData>(COMMENT_REPAY_BASE_URL, {
      params: { ids }
    })
  }

  /**
 * 评论回复
 */
  static addCommentRepay(data: CommentRepaySaveDTO | Comment) {
    return request.post<any, ResponseData>(COMMENT_REPAY_BASE_URL + "/save", data);
  }
}

export default CommentRepayAPI

export interface CommentRepay {
  /**
   * id
   */
  id: string;

  /**
   * 用户ID
   */
  authorId: number;

  /**
   * 用户昵称
   */
  authorName: string;

  /**
   * 用户头像
   */
  authorAvatar: string;

  /**
   * 评论根id
   */
  rootCommentId: string;

  /**
   * 被回复的用户ID
   */
  replyAuthorId: number;

  /**
   * 被回复的评论id
   */
  replyCommentId: string;

  /**
   * 被回复的用户昵称
   */
  replyAuthorName: string;

  /**
   * 回复内容
   */
  content: string;

  /**
   * 点赞数
   */
  likes: number;

  /**
   * 创建时间
   */
  createdTime: Date;

  /**
   * 更新时间
   */
  updatedTime: Date;

  /**
   * 已读状态  0 未读  1 已读
   */
  isRead: number
}

export interface CommentRepaySaveDTO {
  /**
   * 评论根id
   */
  rootCommentId: string;

  /**
   * 回复内容
   */
  content: string;

  /**
   * 被回复人的id
   */
  replyAuthorId: number;

  /**
   * 被回复的评论id
   */
  replyCommentId: string
}