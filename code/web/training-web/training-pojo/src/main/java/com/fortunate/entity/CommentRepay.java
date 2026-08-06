package com.fortunate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("comment_repay")
public class CommentRepay {
    /**
     * id
     */
    @Id
    private String id;

    /**
     * 用户ID
     */
    private Long authorId;

    /**
     * 用户昵称
     */
    private String authorName;

    /**
     * 用户头像
     */
    private String authorAvatar;

     /**
     * 评论根id
     */
    private String rootCommentId;

    /**
     * 回复的用户ID
     */
    private Long replyAuthorId;

    /**
     * 回复的评论id
     */
    private String replyCommentId;

    /**
     * 回复的用户昵称
     */
    private String replyAuthorName;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 是否已读 0未读 1已读
     */
    private Short isRead;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

}