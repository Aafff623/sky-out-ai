package com.fortunate.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class CommentMessageVO {

    /**
     * 根评论ID 用于前端查询是哪个具体菜品或套餐的主评论
     */
    private String rootCommentId;

    /**
     * 回复的评论ID
     */
    private String repayCommentId;

    /**
     * 回复用户ID
     */
    private Long repayAuthorId;

    /**
     * 回复用户昵称
     */
    private String repayAuthorName;

    /**
     * 回复用户头像
     */
    private String repayAuthorAvatar;

    /**
     * 回复内容
     */
    private String repayContent;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 菜品ID 用于给前端跳转到菜品评论详情页
     */
    private Long dishId;

    /**
     * 套餐ID 用于给前端跳转到套餐评论详情页
     */
    private Long setmealId;

    /**
     * 创建时间
     */
    private Date createdTime;
}
