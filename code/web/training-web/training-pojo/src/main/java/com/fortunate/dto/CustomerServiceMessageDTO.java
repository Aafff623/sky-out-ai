package com.fortunate.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 客服消息DTO
 */
@Data
public class CustomerServiceMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息类型 1文本 2图片 3语音
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;
}
