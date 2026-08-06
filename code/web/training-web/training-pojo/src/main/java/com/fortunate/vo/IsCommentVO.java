package com.fortunate.vo;

import lombok.Data;

@Data
public class IsCommentVO {

    /**
     * 订单id
     */
    private Long orderId;

//    /**
//     * 订单号
//     */
//    private String orderNumber;

    /**
     * 是否评论 0否 1是
     */
    private Short isComment;
}
