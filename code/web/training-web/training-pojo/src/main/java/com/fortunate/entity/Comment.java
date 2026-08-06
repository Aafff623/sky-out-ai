package com.fortunate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * APP评论信息
 */
@Data
@Document("comment")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    /**
     * id
     */
    private String id;

    /**
     * 用户ID  发表评论的用户id
     */
    private Long authorId;

    /**
     * 订单编号
     */
    private String orderNumber;

    /**
     * 用户昵称
     */
    private String authorName;

    /**
     * 菜品id
     */
    private Long dishId;

    /**
     * 套餐id
     */
    private Long setmealId;

    /**
     * 多个菜品名称
     */
    private String moreDishName;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 作者头像
     */
    private String image;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 回复数
     */
    private Integer reply;

    /**
     * 文章标记
     * 0 普通评论
     * 1 热点评论
     * 2 推荐评论
     * 3 置顶评论
     * 4 精品评论
     * 5 大V 评论
     */
    private Short flag;

    /**
     * 评论排列序号
     */
    private Integer ord;

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

    /**
     * 评论状态  0 关闭状态   1 正常状态
     */
    private Boolean status;

}