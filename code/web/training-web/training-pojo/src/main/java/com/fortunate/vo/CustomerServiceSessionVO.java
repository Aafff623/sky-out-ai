package com.fortunate.vo;

import com.fortunate.entity.CustomerServiceMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客服会话VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceSessionVO implements Serializable {

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
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

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
     * 客服人员名称
     */
    private String staffName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 最后一条消息
     */
    private String lastMessage;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数量
     */
    private Integer unreadCount;

    /**
     * 消息列表
     */
    private List<CustomerServiceMessage> messages;
}
