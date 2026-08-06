package com.fortunate.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentRepayDTO {

    /**
     * 评论id
     */
    private String rootCommentId;
    /**
     * 列表中的最小时间
     */
    private Date minDate;
    /**
     * 分页条数
     */
    private Integer size;
}