package com.fortunate.vo;

import com.fortunate.entity.CommentRepay;
import lombok.Data;

@Data
public class CommentRepayVo extends CommentRepay {

    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;
}