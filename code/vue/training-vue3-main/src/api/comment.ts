import { CommentRepay } from '@/api/commentRepay';
import request from '@/utils/request'

const COMMENT_BASE_URL = "/store/comment";
class CommentAPI {
  /**
   * 分页查询评论
   */
  static pageQuery(queryParams: CommentPageQueryDTO) {
    return request<any, ResponseData<CommentVo[]>>({
      url: `${COMMENT_BASE_URL}/page`,
      params: queryParams,
      method: "get",
    });
  }
  /**
 * 根据ID获取评论详情
 * @param id 评论ID
 */
  static getById(id: string) {
    return request.get<CommentVo>(`${COMMENT_BASE_URL}/${id}`);
  }

  /**
   * 批量删除评论
   * @param ids 评论ID数组
   */
  static deleteByIds(ids: string) {
    return request.delete<any, ResponseData>(COMMENT_BASE_URL, {
      params: { ids }
    });
  }


}
export default CommentAPI

export interface CommentPageQueryDTO extends PageQuery {
  /**
   * 菜品id
   */
  dishId: string | undefined;

  /**
   * 套餐id
   */
  setmealId: string | undefined;
}

export interface Comment {
  /**
   * id
   */
  id: string;

  /**
   * 用户ID  发表评论的用户id
   */
  authorId: number;

  /**
   * 订单ID
   */
  orderId: string;

  /**
   * 用户昵称
   */
  authorName: string;

  /**
   * 菜品id
   */
  dishId: number;

  /**
   * 套餐id
   */
  setmealId: number;

  /**
   * 多个菜品名称
   */
  moreDishName: string;

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
  createdTime?: Date;

  /** 更新时间 */
  updateTime?: Date;

  /**
   * 评论状态  0 关闭状态   1 正常状态
   */
  status: boolean;

  /**
   * 是否已评论  0 未评论  1 已评论
   */
  isComment: number;
}

export interface CommentVo extends Comment {
  /**
   * 0：点赞
   * 1：取消点赞
   */
  operation: number;

  /**
   * 菜品名称
   */
  dishName?: string;

  /**
   * 套餐名称
   */
  setmealName?: string;

  /**
   * 评论回复
   */
  commentRepayVos: CommentRepay[];
}



