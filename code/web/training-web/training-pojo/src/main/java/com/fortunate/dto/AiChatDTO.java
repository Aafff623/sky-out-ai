package com.fortunate.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AI聊天请求DTO
 */
@Data
public class AiChatDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户消息内容
     */
    private String message;

    /**
     * 消息类型 1文本 2图片 3语音
     */
    private Integer messageType = 1;
}
