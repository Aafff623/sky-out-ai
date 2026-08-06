package com.fortunate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 客服消息记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 发送者类型 1用户 2客服 3AI
     */
    private Integer senderType;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 消息类型 1文本 2图片 3参数
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 参数消息
     */
    private Object params;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
