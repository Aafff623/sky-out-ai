package com.fortunate.service.impl;

import com.fortunate.chatService.SystemChatServer;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.CommentRepayDTO;
import com.fortunate.dto.CommentRepayLikeDTO;
import com.fortunate.dto.CommentRepaySaveDTO;
import com.fortunate.entity.Comment;
import com.fortunate.entity.CommentRepay;
import com.fortunate.entity.CommentRepayLike;
import com.fortunate.entity.User;
import com.fortunate.exception.CommentException;
import com.fortunate.mapper.UserMapper;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.CommentRepayService;
import com.fortunate.utils.GreenTextScan;
import com.fortunate.utils.SensitiveWordUtil;
import com.fortunate.vo.CommentRepayVo;
import com.fortunate.vo.CommentVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.fortunate.aliyun.RiskLabel.textRiskLabels;
import static com.fortunate.constant.CommentConstants.COMMENT_STATUS_SELLER;
import static com.fortunate.constant.CommentConstants.SELLER_ID;
import static com.fortunate.constant.MessageConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentRepayServiceImpl implements CommentRepayService {
    private final MongoTemplate mongoTemplate;
    private final UserMapper userMapper;
    private final GreenTextScan greenTextScan;
    private final SystemChatServer sysChatServer;
    private static final Long SYSTEM_USER_ID = 4L;
    @Autowired
    @Lazy
    private CommentRepayService commentRepayService;

    /**
     * 评论回复
     *
     * @param dto
     * @return
     */
    @Override
    public Result commentRepay(CommentRepaySaveDTO dto, Integer type) {
        //1.检查参数
        if (dto == null || StringUtils.isBlank(dto.getContent()) ||
                dto.getRootCommentId() == null) {
            throw new CommentException(INVALID_PARAM);
        }
        if (dto.getContent().length() > 500) {
            throw new CommentException(COMMENT_TOO_LONG);
        }

        //安全检查
        try {
            Map<String, String> map = greenTextScan.greenTextScan(dto.getContent());
            if (map != null) {
                for (String textRiskLabel : textRiskLabels) {
                    if (map.get("label") != null && map.get("label").equals(textRiskLabel)) {
                        //自动审核评论失败 存在违规内容
                        throw new CommentException(VIOLATION_CONTENT);
                    }
                }
            }
            //数据库敏感词审核
            Map<String, Integer> map1 = SensitiveWordUtil.matchWords(dto.getContent());
            //不为空说明存在违规内容
            if (!map1.isEmpty()) {
                throw new CommentException(VIOLATION_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommentException(VIOLATION_CONTENT);
        }

        //判断是否为商家评论
        Long userId = null;
        if (type.equals(COMMENT_STATUS_SELLER)) {
            //商家评论则使用固定ID
            userId = SELLER_ID;
        } else {
            userId = BaseContext.getUserId();
        }
        //2.判断是否登录
        if (userId == null) {
            throw new CommentException(UNAUTHORIZED);
        }

        CommentRepay commentRepay = uniformReply(dto, userId);
        //判断被回复的人是否为AI助手
        if (dto.getReplyAuthorId() != null && dto.getReplyAuthorId().equals(SYSTEM_USER_ID)) {
            //是的话ai助手要进行二级回复
            commentRepayService.aiCommentRepayTwo(commentRepay.getId(), dto.getContent());
        }
        return Result.success();
    }

    @Async
    @Override
    public CommentRepay uniformReply(CommentRepaySaveDTO dto, Long userId) {
        User user = userMapper.getUserById(userId);
        Comment comment = mongoTemplate.findById(dto.getRootCommentId(), Comment.class);
        //封装数据
        CommentRepay commentRepay = new CommentRepay();
        if (user == null) {
            throw new CommentException(USER_NOT_FOUND);
        }
        //根据评论id查询评论信息用于设置评论的作者id和名称
        if (dto.getReplyAuthorId() != null) {
            //说明是在回复列表中回复其他人的评论
            //根据被回复人的id查询信息
            User replyUser = userMapper.getUserById(dto.getReplyAuthorId());
            //判断被回复的人是否存在
            if (replyUser == null) {
                throw new CommentException(USER_NOT_FOUND);
            }
            //判断被回复人是否为AI助手或自己
            if (replyUser.getId().equals(SYSTEM_USER_ID) || replyUser.getId().equals(userId)) {
                //已读
                commentRepay.setIsRead((short) 1);
            } else {
                //未读
                commentRepay.setIsRead((short) 0);
            }
            commentRepay.setReplyAuthorId(replyUser.getId());
            commentRepay.setReplyAuthorName(replyUser.getName());

        } else {
            //说明是在评论列表中回复作者
            assert comment != null;
            //判断是否为评论作者自己回复自己
            if (comment.getAuthorId().equals(userId)) {
                //是自己回复自己 是否已读 设置为已读
                commentRepay.setIsRead((short) 1);
            } else {
                //不是自己回复自己 //未读
                commentRepay.setIsRead((short) 0);
            }
            //回复的作者id、名称
            commentRepay.setReplyAuthorId(Objects.requireNonNull(comment).getAuthorId());
            commentRepay.setReplyAuthorName(comment.getAuthorName());
        }

        //回复人id、内容、头像、昵称
        commentRepay.setAuthorId(userId);
        commentRepay.setContent(dto.getContent());
        commentRepay.setAuthorAvatar(user.getAvatar());
        commentRepay.setAuthorName(user.getName());
        //被回复的id
        commentRepay.setReplyCommentId(dto.getReplyCommentId());
        //根id
        commentRepay.setRootCommentId(dto.getRootCommentId());
        //创建修改时间
        commentRepay.setCreatedTime(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000));
        commentRepay.setUpdatedTime(new Date());
        //点赞数
        commentRepay.setLikes(0);
        mongoTemplate.save(commentRepay);

        //更新回复数量
        assert comment != null;
        comment.setReply(comment.getReply() + 1);
        mongoTemplate.save(comment);
        return commentRepay;
    }

    /**
     * 对用户的评论信息进行ai自动回复
     *
     * @param commentId
     */
    @Override
    @Async
    public void aiCommentRepay(String commentId) {
        //默认是一级评论
        try {
            Comment comment = mongoTemplate.findById(commentId, Comment.class);
            //一级评论需要拼接一下菜品名称
            String str = """
                    用户购买了菜品：%s,
                    评论了：%s,
                    请你对这个进行回复
                    """;
            String userPrompt = String.format(str, comment.getMoreDishName(), comment.getContent());

            String aiChat = sysChatServer.chat(userPrompt);
            CommentRepaySaveDTO repaySaveDTO = new CommentRepaySaveDTO();
            repaySaveDTO.setRootCommentId(comment.getId());
            repaySaveDTO.setContent(aiChat);
            repaySaveDTO.setReplyAuthorId(comment.getAuthorId());
            repaySaveDTO.setReplyCommentId(comment.getId());
            this.uniformReply(repaySaveDTO, SYSTEM_USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            //失败降级
            aiCommentRepayFallback(commentId);
        }
    }

    /**
     * 对用户的评论信息进行ai自动回复 二级评论回复 只有当其回复ai助手时才会调用
     *
     * @param commentRepayId
     */
    @Override
    @Async("aiReplyExecutor")
    public void aiCommentRepayTwo(String commentRepayId, String content) {
        //二级评论
        try {
//            Thread.sleep(5000);
            CommentRepay commentRepay = mongoTemplate.findById(commentRepayId, CommentRepay.class);
            String aiChat = sysChatServer.chat(content);
            CommentRepaySaveDTO repaySaveDTO = new CommentRepaySaveDTO();
            repaySaveDTO.setRootCommentId(commentRepay.getRootCommentId());
            repaySaveDTO.setContent(aiChat);
            repaySaveDTO.setReplyAuthorId(commentRepay.getAuthorId());
            log.info("commentRepayId:{}", commentRepay.getId());
            repaySaveDTO.setReplyCommentId(commentRepay.getId());
            this.uniformReply(repaySaveDTO, SYSTEM_USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            //失败降级
            aiCommentRepayTwoFallback(commentRepayId);
        }
    }

    /**
     * ai自动回复失败降级方法
     */
    public void aiCommentRepayFallback(String commentId) {
        log.info("ai自动回复失败降级方法");
        Comment comment = mongoTemplate.findById(commentId, Comment.class);
        CommentRepaySaveDTO repaySaveDTO = new CommentRepaySaveDTO();
        repaySaveDTO.setRootCommentId(commentId);
        repaySaveDTO.setContent("调用aigc服务出错了！");
        repaySaveDTO.setReplyAuthorId(comment.getAuthorId());
        repaySaveDTO.setReplyCommentId(comment.getId());
        this.uniformReply(repaySaveDTO, SYSTEM_USER_ID);
        log.info("降级成功");
    }

    /**
     * ai自动回复二级评论失败降级方法
     */
    public void aiCommentRepayTwoFallback(String commentId) {
        log.info("ai自动回复失败降级方法");
        CommentRepay commentRepay = mongoTemplate.findById(commentId, CommentRepay.class);
        CommentRepaySaveDTO repaySaveDTO = new CommentRepaySaveDTO();
        repaySaveDTO.setRootCommentId(commentRepay.getRootCommentId());
        repaySaveDTO.setContent("调用aigc服务出错了！");
        repaySaveDTO.setReplyAuthorId(commentRepay.getAuthorId());
        repaySaveDTO.setReplyCommentId(commentRepay.getId());
        this.uniformReply(repaySaveDTO, SYSTEM_USER_ID);
        log.info("降级成功");
    }

    /**
     * 评论回复点赞
     *
     * @param dto
     * @return
     */
    @Override
    public Result like(CommentRepayLikeDTO dto) {
        //1.检查参数
        if (dto == null || dto.getCommentRepayId() == null) {
            throw new CommentException(INVALID_PARAM);
        }
        //2.判断是否登录
        Long userId = BaseContext.getUserId();
        // 测试期间给定值
//        userId = userId == null ? 1L : userId;
        if (userId == null) {
            throw new CommentException(UNAUTHORIZED);
        }
        CommentRepay commentRepay = mongoTemplate.findById(dto.getCommentRepayId(), CommentRepay.class);

        if (commentRepay != null && dto.getOperation() == 0) {
            //点赞数量增加
            commentRepay.setLikes(commentRepay.getLikes() + 1);
            mongoTemplate.save(commentRepay);
            //保存点赞数据
            CommentRepayLike apCommentRepayLike = new CommentRepayLike();
            apCommentRepayLike.setCommentRepayId(commentRepay.getId());
            apCommentRepayLike.setAuthorId(userId);
            mongoTemplate.save(apCommentRepayLike);
        } else {
            //取消点赞
            int tmp = commentRepay.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;
            commentRepay.setLikes(tmp);
            mongoTemplate.save(commentRepay);
            //删除点赞数据
            Query query = Query.query(Criteria.where("commentRepayId").is(commentRepay.getId()).and("authorId").is(userId));
            mongoTemplate.remove(query, CommentRepayLike.class);
        }
        //返回点赞数给前端展示
        Map<String, Object> result = new HashMap<>();
        result.put("likes", commentRepay.getLikes());
        return Result.success(result);
    }

    /**
     * 评论回复列表
     *
     * @param dto
     * @return
     */
    @Override
    public Result load(CommentRepayDTO dto) {
        //1.检查参数
        if (dto == null || dto.getRootCommentId() == null) {
            throw new CommentException(INVALID_PARAM);
        }

        //2.加载数据
        Date adjustedMinDate = new Date(dto.getMinDate().getTime() + 8 * 60 * 60 * 1000);
        Query query = Query.query(Criteria.where("rootCommentId").is(dto.getRootCommentId()).and("createdTime").lt(adjustedMinDate));
        //先统计一下总回复数
        long total = mongoTemplate.count(query, CommentRepay.class);
        query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        //获取所有回复评论
        List<CommentRepay> commentRepays = mongoTemplate.find(query, CommentRepay.class);
        //判断用户是否已登录
        Long userId = BaseContext.getUserId();
        // 测试期间给定值
//        userId = userId == null ? 1L : userId;
        //构建分页结果对象
        PageResult pageResult = new PageResult();
        if (userId == null) {
            pageResult.setTotal(total);
            pageResult.setRecords(commentRepays);
            //直接返回所有评论回复
            return Result.success(pageResult);
        }

        //3.2 用户已登录
        //需要查询当前评论中哪些数据被登录用户点赞了
        //获取当前所有回复评论的id
        List<String> idList = commentRepays.stream().map(CommentRepay::getId).collect(Collectors.toList());
        //构建查询条件
        Query query1 = Query.query(Criteria.where("commentRepayId").in(idList).and("authorId").is(userId));
        //查询被点赞后的数据列表
        List<CommentRepayLike> commentRepayLikes = mongoTemplate.find(query1, CommentRepayLike.class);

        //如果为空说明当前评论内的其他回复评论登录用户都没有点赞过
        if (commentRepayLikes == null || commentRepayLikes.isEmpty()) {
            pageResult.setTotal(total);
            pageResult.setRecords(commentRepays);
            //直接返回回复评论即可
            return Result.success(pageResult);
        }

        //执行到这里说明登录用户点赞过一些回复评论
        //创建vo集合
        List<CommentRepayVo> resultList = getCommentRepayVos(commentRepays, commentRepayLikes);
        pageResult.setTotal(total);
        pageResult.setRecords(resultList);

        return Result.success(pageResult);
    }

    /**
     * 管理端查询所有回复评论
     *
     * @param dto
     * @return
     */
    @Override
    public Result list(CommentRepayDTO dto) {
        //参数校验
        if (dto.getRootCommentId() == null) {
            throw new CommentException(INVALID_PARAM);
        }
        //根据评论id查询主评论
        Query query = Query.query(Criteria.where("id").is(dto.getRootCommentId()));
        Comment comment = mongoTemplate.findOne(query, Comment.class);
        //根据评论id查询所有回复评论
        query = Query.query(Criteria.where("rootCommentId").is(dto.getRootCommentId()));
        List<CommentRepay> commentRepays = mongoTemplate.find(query, CommentRepay.class);
        //CommentVo
        CommentVo commentVo = new CommentVo();
        assert comment != null;
        BeanUtils.copyProperties(comment, commentVo);
        commentVo.setCommentRepayVos(commentRepays);
        return Result.success(commentVo);
    }

    /**
     * 管理端批量删除回复评论
     *
     * @param ids
     * @return
     */
    @Override
    public Result deleteCommentRepay(List<String> ids) {
        Query query = null;

        //先删除回复评论
        for (String id : ids) {
            //根据评论id查询回复评论
            query = Query.query(Criteria.where("id").is(id));
            CommentRepay one = mongoTemplate.findOne(query, CommentRepay.class);
            //再获取主评论id
            String rootCommentId = one.getRootCommentId();
            //查询主评论 回复数减1
            Query query2 = Query.query(Criteria.where("id").is(rootCommentId));
            Comment comment = mongoTemplate.findOne(query2, Comment.class);
            if (comment != null) {
                comment.setReply(comment.getReply() - 1);
            }
            //保存主评论
            mongoTemplate.save(comment);
            //再删除回复评论
            mongoTemplate.remove(query, CommentRepay.class);
        }
        //再删除回复评论点赞数据
        query = Query.query(Criteria.where("commentRepayId").in(ids));
        mongoTemplate.remove(query, CommentRepayLike.class);
        return Result.success();
    }

    @NotNull
    private static List<CommentRepayVo> getCommentRepayVos(List<CommentRepay> commentRepays, List<CommentRepayLike> commentRepayLikes) {
        List<CommentRepayVo> resultList = new ArrayList<>();
        commentRepays.forEach(x -> {
            //创建vo对象
            CommentRepayVo commentRepayVo = new CommentRepayVo();
            //属性拷贝
            BeanUtils.copyProperties(x, commentRepayVo);
            //遍历点赞列表
            for (CommentRepayLike apCommentRepayLike : commentRepayLikes) {
                //判断当前回复评论id是否和点赞列表中的回复评论id一致
                if (apCommentRepayLike.getCommentRepayId().equals(x.getId())) {
                    //一直则设置点赞状态 用于给前端高亮展示
                    commentRepayVo.setOperation((short) 0);
                    break;
                }
            }
            //添加到结果列表
            resultList.add(commentRepayVo);
        });
        return resultList;
    }
}
