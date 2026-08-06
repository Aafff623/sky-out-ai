package com.fortunate.service;

import com.fortunate.dto.CommentRepayDTO;
import com.fortunate.dto.CommentRepayLikeDTO;
import com.fortunate.dto.CommentRepaySaveDTO;
import com.fortunate.entity.CommentRepay;
import com.fortunate.result.Result;

import java.util.List;

public interface CommentRepayService {
    /**
     * 评论回复
     *
     * @param dto
     * @param type 1 商家评论  2 用户评论
     * @return
     */
    Result commentRepay(CommentRepaySaveDTO dto,Integer type);

    /**
     * 菜品评价 对用户的评论信息进行ai自动回复
     */
    void aiCommentRepay(String commentId);

    /**
     * 评论回复 对用户的评论信息进行ai自动回复
     * @param commentRepayId
     * @param content
     */
    void aiCommentRepayTwo(String commentRepayId,String content);

    /**
     * 保存封装评论信息
     *
     * @param dto
     * @param userId
     * @return
     */
     CommentRepay uniformReply(CommentRepaySaveDTO dto, Long userId);

    /**
     * 评论回复点赞
     *
     * @param dto
     * @return
     */
    Result like(CommentRepayLikeDTO dto);

    /**
     * 评论回复列表
     *
     * @param dto
     * @return
     */
    Result load(CommentRepayDTO dto);

    /**
     * 管理端查询所有回复评论
     *
     * @param dto
     * @return
     */
    Result list(CommentRepayDTO dto);

    /**
     * 管理端批量删除回复评论
     *
     * @param ids
     * @return
     */
    Result deleteCommentRepay(List<String> ids);
}
