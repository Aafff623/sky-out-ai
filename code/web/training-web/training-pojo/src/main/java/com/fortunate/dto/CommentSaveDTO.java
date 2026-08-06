package com.fortunate.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentSaveDTO {
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 菜品id
     */
    private List<Long> dishIds;

    /**
     * 套餐id
     */
    private List<Long> setmealIds;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 原始评论
     */
    private String originalComment;

    /**
     * 评论内容
     */
    private String content;

    /**
     * AI扩写类型
     * 1帮写 2续写 3润色 4精简
     */
    private Integer type;
}
