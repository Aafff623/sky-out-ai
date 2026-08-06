package com.fortunate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话上下文实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiConversationContext implements Serializable {

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
     * 用户ID
     */
    private Long userId;

    /**
     * 上下文数据（JSON格式存储用户偏好、历史查询等）
     */
    private String contextData;

    /**
     * 最后一次意图识别结果
     */
    private String lastIntent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
