import request from "@/utils/request";

const CUSTOMER_SERVICE_BASE_URL = "/client/customer-service";
const AI_ASSISTANT_BASE_URL = "/client/ai-assistant";

// API地址
const api = {
  createHumanSession: `${CUSTOMER_SERVICE_BASE_URL}/session/human`,
  createAISession: `${CUSTOMER_SERVICE_BASE_URL}/session/ai`,
  getMessages: `${CUSTOMER_SERVICE_BASE_URL}/messages`,
  getSessionDetail: `${CUSTOMER_SERVICE_BASE_URL}/session`,
  endSession: `${CUSTOMER_SERVICE_BASE_URL}/session`,
  // AI助手相关API
  aiChat: `${AI_ASSISTANT_BASE_URL}/chat`,
  quickDishRecommend: `${AI_ASSISTANT_BASE_URL}/quick-dish-recommend`,
};

class CustomerServiceAPI {
  /**
   * 创建或获取人工客服会话
   */
  static createHumanSession() {
    return request.post<CustomerServiceSessionVO>(api.createHumanSession);
  }

  /**
   * 创建或获取AI助手会话
   * @param options 请求配置选项
   */
  static createAISession(options?: any) {
    return request.post<CustomerServiceSessionVO>(api.createAISession, {}, options);
  }

  /**
   * 获取会话消息列表
   * @param sessionId 会话ID
   * @param options 请求配置选项
   */
  static getSessionMessages(sessionId: string, type: number, options?: any) {
    return request.get<CustomerServiceMessageVO[]>(`${api.getMessages}/${sessionId}?type=${type}`, {}, options);
  }

  /**
   * 获取会话详情
   * @param sessionId 会话ID
   */
  static getSessionDetail(sessionId: string) {
    return request.get<CustomerServiceSessionVO>(`${api.getSessionDetail}/${sessionId}`);
  }

  /**
   * 结束会话
   * @param sessionId 会话ID
   */
  static endSession(sessionId: string) {
    return request.put(`${api.endSession}/${sessionId}/end`);
  }

  /**
   * AI聊天
   * @param chatData 聊天数据
   * @param options 请求配置选项
   */
  static aiChat(chatData: AiChatDTO, options?: any) {
    // AI聊天接口需要更长的超时时间
    const defaultOptions = {
      timeout: 45000, // 45秒超时
      responseType: 'stream', // 流式响应
      // 禁用自动数据解析
      isFactory: false,
      //禁用加载动画
      load: false,
      ...options
    };
    return request.post<AiStreamChatResponseVO[]>(api.aiChat, chatData, defaultOptions);
    // return request.post<AiChatResponseVO>(api.aiChat, chatData, defaultOptions);
  }
}

export default CustomerServiceAPI;

/** 客服会话VO */
export interface CustomerServiceSessionVO {
  /** 主键 */
  id?: number;
  /** 用户ID */
  userId?: number;
  /** 用户名称 */
  userName?: string;
  /** 用户头像 */
  userAvatar?: string;
  /** 会话ID */
  sessionId?: string;
  /** 服务类型 1人工客服 2AI助手 */
  serviceType?: number;
  /** 会话状态 1进行中 2已结束 */
  status?: number;
  /** 客服人员ID */
  staffId?: number;
  /** 客服人员名称 */
  staffName?: string;
  /** 创建时间 */
  createTime?: string;
  /** 结束时间 */
  endTime?: string;
  /** 最后一条消息 */
  lastMessage?: string;
  /** 最后消息时间 */
  lastMessageTime?: string;
  /** 未读消息数量 */
  unreadCount?: number;
  /** 消息列表 */
  messages?: CustomerServiceMessageVO[];
}

/** 客服消息VO */
export interface CustomerServiceMessageVO {
  /** 主键 */
  id?: number;
  /** 会话ID */
  sessionId?: string;
  /** 发送者类型 1用户 2客服 3AI */
  senderType?: number;
  /** 发送者ID */
  senderId?: number;
  /** 消息类型 1文本 2图片 3语音 */
  messageType?: number;
  /** 消息内容 */
  content?: string;
  /** 创建时间 */
  createTime?: string;
}

/** 客服消息DTO */
export interface CustomerServiceMessageDTO {
  /** 会话ID */
  sessionId?: string;
  /** 消息类型 1文本 2图片 3语音 */
  messageType?: number;
  /** 消息内容 */
  content?: string;
}

/** AI聊天请求DTO */
export interface AiChatDTO {
  /** 会话ID */
  sessionId?: string;
  /** 用户消息内容 */
  message?: string;
  /** 消息类型 1文本 2图片 3语音 */
  messageType?: number;
}

/** AI聊天响应VO */
export interface AiChatResponseVO {
  /** AI回复内容 */
  reply?: string;
  /** 意图识别结果 */
  intent?: string;
  /** 是否包含订单信息 */
  hasOrderInfo?: boolean;
  /** 订单信息列表 */
  orderList?: OrderInfoVO[];
  /** 是否包含菜品推荐 */
  hasDishRecommend?: boolean;
  /** 推荐菜品列表 */
  dishList?: DishRecommendVO[];
}

/** AI流式聊天响应VO */
export interface AiStreamChatResponseVO {
  /**
  * 文本内容
  */
  eventData: string;
  /**
   * 事件类型，1001-数据事件，1002-停止事件，1003-参数事件
   */
  eventType: number;
}

/** 订单信息VO */
export interface OrderInfoVO {
  orderNumber?: string;
  status?: string;
  statusText?: string;
  orderTime?: string;
  amount?: number;
  dishNames?: string;
}

/** 菜品推荐VO */
export interface DishRecommendVO {
  dishId?: number;
  dishName?: string;
  description?: string;
  price?: number;
  image?: string;
  category?: string;
}
