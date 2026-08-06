package com.fortunate.dto;

import lombok.Data;

@Data
public class CommentRepaySaveDTO {
    /**
     * 根评论id
     */
    private String rootCommentId;
    /**
     * 回复内容
     */
    private String content;
    /**
     * 被回复的作者id
     */
    private Long replyAuthorId;
    /**
     * 被回复的评论id
     * */
    private String replyCommentId;
}