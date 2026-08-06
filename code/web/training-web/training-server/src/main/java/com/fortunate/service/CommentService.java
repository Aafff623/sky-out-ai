package com.fortunate.service;


import com.fortunate.dto.CommentDTO;
import com.fortunate.dto.CommentLikeDTO;
import com.fortunate.dto.CommentSaveDTO;
import com.fortunate.dto.IsCommentDTO;
import com.fortunate.query.CommentPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.vo.CommentVo;
import jakarta.annotation.PostConstruct;

import java.util.List;

public interface CommentService {

    /**
     * 发表评论
     * @param dto
     * @return
     */
    Result comment(CommentSaveDTO dto);


    /**
     * 加载评论
     * @param dto
     * @return
     */
    Result load(CommentDTO dto);

    /**
     * 评论点赞
     * @param dto
     * @return
     */
    Result like(CommentLikeDTO dto);

    /**
     * 判断是否评论
     * @param orderIds
     * @return
     */
    Result isComment(IsCommentDTO orderIds);

    /**
     * 管理端分页查询评论
     * @param dto
     * @return
     */
    PageResult page(CommentPageQueryDTO dto);

    /**
     * 批量删除评论
     * @param ids
     */
    Result deleteById(List<String> ids);

    /**
     * 根据id查询评论
     * @param id
     * @return
     */
    Result<CommentVo> getById(String id);

    /**
     * 初始化敏感词
     */
    public void initSensitive();
}
