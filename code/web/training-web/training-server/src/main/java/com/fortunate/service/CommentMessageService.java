package com.fortunate.service;

import com.fortunate.query.CommentMessagePageQueryDTO;
import com.fortunate.vo.CommentMessageVO;

import java.util.List;

public interface CommentMessageService {

    /**
     * 根据用户id查询用户的被回复评论消息
     * @param userId
     * @return
     */
    List<CommentMessageVO> queryCommentMessage(Long userId, CommentMessagePageQueryDTO dto);

    /**
     * 查询用户未读消息数量
     * @param userId
     * @return
     */
    Integer count(Long userId);
}
