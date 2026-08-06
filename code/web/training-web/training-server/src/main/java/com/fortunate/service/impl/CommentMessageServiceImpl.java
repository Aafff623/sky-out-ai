package com.fortunate.service.impl;

import com.fortunate.entity.Comment;
import com.fortunate.entity.CommentRepay;
import com.fortunate.entity.Dish;
import com.fortunate.entity.Setmeal;
import com.fortunate.mapper.DishMapper;
import com.fortunate.mapper.SetmealMapper;
import com.fortunate.query.CommentMessagePageQueryDTO;
import com.fortunate.service.CommentMessageService;
import com.fortunate.vo.CommentMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentMessageServiceImpl implements CommentMessageService {

    private final MongoTemplate mongoTemplate;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 根据用户id查询用户的被回复评论消息
     *
     * @param userId
     * @return
     */
    @Override
    public List<CommentMessageVO> queryCommentMessage(Long userId, CommentMessagePageQueryDTO dto) {
        //查询当前用户被xxx回复的评论 但不能是自己回复自己
        Query query = Query.query(
                        Criteria.where("replyAuthorId")
                                .is(userId)
                                .and("authorId")
                                .ne(userId))
                .with(Sort.by(Sort.Direction.DESC, "createdTime"))
                .skip((long) (dto.getPage() - 1) * dto.getPageSize())
                .limit(dto.getPageSize());
        List<CommentRepay> commentRepays = mongoTemplate.find(query, CommentRepay.class);
        //遍历查询结果 将未读设置为已读
        for (CommentRepay commentRepay : commentRepays) {
            if (commentRepay.getIsRead() == null || commentRepay.getIsRead() == 0) {
                //已读
                commentRepay.setIsRead((short) 1);
                mongoTemplate.save(commentRepay);
            }
        }

        //遍历查询结果 封装成VO对象进行返回
        List<CommentMessageVO> commentMessageVOList = new ArrayList<>(
                commentRepays.stream().map(commentRepay -> {
                    //跟据根评论id查询根评论信息
                    String rootCommentId = commentRepay.getRootCommentId();
                    Query query1 = Query.query(Criteria.where("id").is(rootCommentId));
                    //查询根评论
                    Comment comment = mongoTemplate.findOne(query1, Comment.class);
                    //封装返回数据对象
                    assert comment != null;
                    CommentMessageVO commentMessageVO = null;
                    if (comment.getDishId() != null) {
                        Long dishId = comment.getDishId();
                        //查询菜品信息设置回复的图片
                        Dish dishById = dishMapper.getDishById(dishId);
                        //说明当前评论的是菜品
                        commentMessageVO = CommentMessageVO.builder()
                                .rootCommentId(commentRepay.getRootCommentId())
                                .repayCommentId(commentRepay.getId())
                                .repayAuthorId(commentRepay.getAuthorId())
                                .repayAuthorName(commentRepay.getAuthorName())
                                .repayAuthorAvatar(commentRepay.getAuthorAvatar())
                                .repayContent(commentRepay.getContent())
                                .dishId(comment.getDishId())
                                .image(dishById.getImage())
                                .createdTime(commentRepay.getCreatedTime())
                                .build();
                    } else {
                        Long setmealId = comment.getSetmealId();
                        Setmeal setmealById = setmealMapper.getSetmealById(setmealId);
                        //说明评论的是套餐
                        commentMessageVO = CommentMessageVO.builder()
                                .rootCommentId(commentRepay.getRootCommentId())
                                .repayCommentId(commentRepay.getId())
                                .repayAuthorId(commentRepay.getAuthorId())
                                .repayAuthorName(commentRepay.getAuthorName())
                                .repayAuthorAvatar(commentRepay.getAuthorAvatar())
                                .repayContent(commentRepay.getContent())
                                .setmealId(comment.getSetmealId())
                                .image(setmealById.getImage())
                                .createdTime(commentRepay.getCreatedTime())
                                .build();
                    }
                    return commentMessageVO;
                }).toList());
        //再根据时间把里面的内容进行排序
        commentMessageVOList.sort((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime()));
        return commentMessageVOList;
    }

    /**
     * 查询用户未读消息数量
     *
     * @param userId
     * @return
     */
    @Override
    public Integer count(Long userId) {
        Query query = Query.query(Criteria.where("replyAuthorId").is(userId).and("isRead").is(0));
        return (int) mongoTemplate.count(query, CommentRepay.class);
    }
}
