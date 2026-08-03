import request from '@/utils/request'
const KNOWLEDGE_BASE_URL = "/store/knowledgeBase";

class KnowledgeBaseAPI {

  /**
   * 分页查询知识库
   */
  static pageQuery(params: KnowledgeBasePageQueryDTO) {
    return request.get<any, ResponseData<KnowledgeBase[]>>(`${KNOWLEDGE_BASE_URL}/page`, {
      params
    })
  }

  /**
   * 新增知识库
   * @param data 表单数据
   */
  static addKnowledgeBase(data: FormData) {
    return request.post<any, ReqResultType>(`${KNOWLEDGE_BASE_URL}/embed`, data, {
      headers: { "Content-Type": "multipart/form-data" }
    })
  }

  /**
   * 删除知识库
   * @param ids 知识库ID数组
   */
  static deleteByIds(ids: number) {
    return request.delete<any, ReqResultType>(`${KNOWLEDGE_BASE_URL}/delete`, {
      params: { ids }
    })
  }
}

export default KnowledgeBaseAPI

export interface KnowledgeBase {
  /**
   * 主键
   */
  id: number | null;

  /**
   * 文件名称
   */
  fileName: string | null;

  /**
   * 单文件关联的redis向量ids
   */
  redisIds: string | null;

  /**
   * 原始文件的url
   */
  url: string | null;

  /**
   * 创建时间
   */
  createdTime: Date | null;
}

export interface KnowledgeBasePageQueryDTO extends PageQuery {
  /**
   * 文件名称
   */
  fileName: string | null;
}

