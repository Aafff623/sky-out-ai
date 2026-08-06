package com.fortunate.dto;

import lombok.Data;

@Data
public class CommentRepayLikeDTO {
    /**
     * 评论回复id
     */
    private String commentRepayId;
    /**
     * 0点赞 1取消点赞
     */
    private Short operation;
}