package com.fortunate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客服会话实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 服务类型 1人工客服 2AI助手
     */
    private Integer serviceType;

    /**
     * 会话状态 1进行中 2已结束
     */
    private Integer status;

    /**
     * 客服人员ID
     */
    private Long staffId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
