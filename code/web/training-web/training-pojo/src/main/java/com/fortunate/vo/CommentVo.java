package com.fortunate.vo;

import com.fortunate.entity.Comment;
import com.fortunate.entity.CommentRepay;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo extends Comment {

    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;

    /**
     * 菜品名称
     */
    private String dishName;

    /**
     * 套餐名称
     */
    private String setmealName;

    /**
     * 评论回复
     */
    List<CommentRepay> commentRepayVos;


}