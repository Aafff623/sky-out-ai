package com.fortunate.dto;

import lombok.Data;

@Data
public class CommentLikeDTO {
    /**
     * 评论id
     */
    private String commentId;

    /**
     * 0点赞 1取消点赞
     */
    private Short operation;
}