package com.fortunate.constant;

/**
 * 客服相关常量
 */
public class CustomerServiceConstant {

    /**
     * 服务类型
     */
    public static final Integer SERVICE_TYPE_HUMAN = 1; // 人工客服
    public static final Integer SERVICE_TYPE_AI = 2; // AI助手

    /**
     * 会话状态
     */
    public static final Integer SESSION_STATUS_ACTIVE = 1; // 进行中
    public static final Integer SESSION_STATUS_ENDED = 2; // 已结束

    /**
     * 发送者类型
     */
    public static final Integer SENDER_TYPE_USER = 1; // 用户
    public static final Integer SENDER_TYPE_STAFF = 2; // 客服
    public static final Integer SENDER_TYPE_AI = 3; // AI

    /**
     * 消息类型
     */
    public static final Integer MESSAGE_TYPE_TEXT = 1; // 文本
    public static final Integer MESSAGE_TYPE_IMAGE = 2; // 图片
    public static final Integer MESSAGE_TYPE_PARAM = 3; // 参数

    public static final String REDIS_KEY_SESSION_MESSAGES = "training:session:messages:";
}
