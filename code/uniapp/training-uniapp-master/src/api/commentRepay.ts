
import request from "@/utils/request";

const COMMENT_REPAY_BASE_URL = "/client/comment-repay";

class CommentRepayAPI {

  /**
   * 评论回复
   */
  static addCommentRepay(params: CommentRepaySaveDTO) {
    return request.post<CommentRepay>(COMMENT_REPAY_BASE_URL + "/save", params);
  }

  /**
   * 获取评论回复列表
   */
  static getCommentRepayLoad(commentId: CommentRepayDTO) {
    return request.post<PageResult<CommentRepayVO>>(COMMENT_REPAY_BASE_URL + "/load", commentId);
  }

  /**
   * 回复评论点赞
   */
  static like(params: CommentRepayLikeDTO) {
    return request.post<CommentRepay>(COMMENT_REPAY_BASE_URL + "/like", params);
  }
  /**
   * 批量删除评论回复
   * @param ids 评论回复ID数组
   */
  static deleteCommentRepay(ids: string | string[]) {
    const idsParam = Array.isArray(ids) ? ids.join(',') : ids;
    return request.delete<ResponseData>(`${COMMENT_REPAY_BASE_URL}?ids=${idsParam}`)
  }
}

export default CommentRepayAPI;


export interface CommentRepay extends ResponseVOCommonFields {
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
  * 根评论id
  */
  rootCommentId: string;

  /**
   * 回复内容
   */
  content: string;

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
   * 点赞数
   */
  likes: number;

  /**
   * 创建时间
   */
  createdTime: string;

  /**
   * 更新时间
   */
  updatedTime: string;

}
export interface CommentRepayVO extends CommentRepay {

  /**
   * 回复数
   */
  reply: number;

  /**
   * 评论点赞状态  0 未点赞   1 已点赞
   */
  operation?: number | null | undefined;
}
export interface CommentRepayLikeDTO extends ResponseVOCommonFields {

  /**
   * 评论回复id
   */
  commentRepayId: string;
  /**
   * 0 未点赞   1 已点赞

   */
  operation: number;
}

export interface CommentRepaySaveDTO extends ResponseVOCommonFields {
  /**
   * 评论id
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

export interface CommentRepayDTO extends ResponseVOCommonFields {

  /**
   * 评论id
   */
  rootCommentId: string

  /**
   * 回复最小时间
   */
  minDate: Date

  /**
   * 分页条数
   */
  size: number
}


