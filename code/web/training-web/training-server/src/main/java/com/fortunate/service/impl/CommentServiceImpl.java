package com.fortunate.service.impl;

import com.fortunate.chatService.SystemChatServer;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.*;
import com.fortunate.entity.*;
import com.fortunate.exception.CommentException;
import com.fortunate.mapper.DishMapper;
import com.fortunate.mapper.OrderMapper;
import com.fortunate.mapper.SetmealMapper;
import com.fortunate.mapper.UserMapper;
import com.fortunate.query.CommentPageQueryDTO;
import com.fortunate.query.SensitivePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.CommentRepayService;
import com.fortunate.service.CommentService;
import com.fortunate.service.SensitiveService;
import com.fortunate.utils.GreenTextScan;
import com.fortunate.utils.SensitiveWordUtil;
import com.fortunate.vo.CommentVo;
import com.fortunate.vo.IsCommentVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.fortunate.aliyun.RiskLabel.textRiskLabels;
import static com.fortunate.constant.MessageConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserMapper userMapper;
    private final MongoTemplate mongoTemplate;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;
    private final GreenTextScan greenTextScan;
    public static final String USER_ORDER_CACHE_KEY = "user:orders:";
    private final RedisTemplate redisTemplate;
    private final OrderMapper orderMapper;
    private final CommentRepayService commentRepayService;
    private final SensitiveService sensitiveService;

    /**
     * 完成订单发表评论
     *
     * @param dto
     * @return
     */
    @Override
    public Result comment(CommentSaveDTO dto) {
        //参数校验
        if (dto == null || StringUtils.isBlank(dto.getContent()) || dto.getDishIds() == null) {
            throw new CommentException(INVALID_PARAM);
        }

        //评论内容不能超过500字
        if (dto.getContent().length() > 500) {
            throw new RuntimeException(COMMENT_TOO_LONG);
        }

        //安全检查
        try {
            //自动审核评论
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

        //获取当前登录用户信息
        Long userId = BaseContext.getUserId();

        //查询用户信息
        User userById = userMapper.getUserById(userId);
        if (userById == null) {
            throw new CommentException(ACCOUNT_NOT_FOUND);
        }

        //从数据库中分别查询出对应的菜品名称并进行拼接
        StringBuilder moreDishName = new StringBuilder();
        List<Long> dishIds = dto.getDishIds();
        if (dishIds != null && !dishIds.isEmpty()) {
            //去除集合重复元素
            dishIds = dishIds.stream().distinct().toList();
            for (Long dishId : dishIds) {
                String name = dishMapper.getDishById(dishId).getName();
                moreDishName.append(name);
                //如果是最后一个则不添加、
                if (!dishId.equals(dto.getDishIds().get(dto.getDishIds().size() - 1))) {
                    moreDishName.append("、").toString();
                }
            }
        }

        //从数据库中分别查询出对应的套餐名称并进行拼接
        List<Long> setmealIds = dto.getSetmealIds();
        if (setmealIds != null && !setmealIds.isEmpty()) {
            setmealIds = setmealIds.stream().distinct().toList();
            //因为菜品最后一个不添加、 需要判断当前套餐是否不是第一个 不是则要添加、否则不添加、
            if (!moreDishName.isEmpty()) {
                //说明已经添加过菜品了 需要添加一个、
                moreDishName.append("、").toString();
            }
            //从数据库中分别查询出对应的套餐名称并进行拼接
            for (Long setmealId : setmealIds) {
                String name = setmealMapper.getSetmealById(setmealId).getName();
                moreDishName.append(name);
                //如果是最后一个则不添加、
                if (!setmealId.equals(dto.getSetmealIds().get(dto.getSetmealIds().size() - 1))) {
                    moreDishName.append("、").toString();
                }
            }
        }

        //封装评论信息
        Comment comment = null;
        Long finalUserId = userId;
        //如果是菜品
        if (dishIds != null) {
            for (Long dishId : dishIds) {
                comment = Comment.builder()
                        //评论用户id
                        .authorId(finalUserId)
                        //评论用户名称
                        .authorName(userById.getName())
                        //评论菜品id
                        .dishId(dishId)
                        //关联菜品
                        .moreDishName(moreDishName.toString())
                        //评论内容
                        .content(dto.getContent())
                        //评论用户头像
                        .image(userById.getAvatar())
                        //普通评论
                        .flag((short) 0)
                        //订单编号
                        .orderNumber(dto.getOrderNumber())
                        //TODO 时间差八小时不清楚原因
                        .createdTime(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000))
                        //点赞数默认0
                        .likes(0)
                        //回复数默认0
                        .reply(0)
                        .build();
                //保存评论信息
                mongoTemplate.save(comment);
                //更新订单信息为已评论
                Orders orders = new Orders();
                orders.setId(dto.getOrderId());
                orders.setNumber(dto.getOrderNumber());
                orders.setIsComment((short) 1);
                orders.setUserId(userId);
                orderMapper.update(orders);
                // 对用户的评论信息进行ai自动回复
                commentRepayService.aiCommentRepay(comment.getId());
            }
        }
        //如果是套餐
        if (setmealIds != null) {
            for (Long setmealId : setmealIds) {
                comment = Comment.builder()
                        .authorId(finalUserId)
                        .authorName(userById.getName())
                        .setmealId(setmealId)
                        .moreDishName(moreDishName.toString())
                        .content(dto.getContent())
                        .image(userById.getAvatar())
                        .flag((short) 1)
                        .orderNumber(dto.getOrderNumber())
                        //TODO 时间差八小时不清楚原因
                        .createdTime(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000))
                        .likes(0)
                        .reply(0)
                        .build();
                //保存评论信息
                mongoTemplate.save(comment);
                Orders orders = new Orders();
                orders.setId(dto.getOrderId());
                orders.setIsComment((short) 1);
                orderMapper.update(orders);
                // 对用户的评论信息进行ai自动回复
                commentRepayService.aiCommentRepay(comment.getId());
            }
        }

        //清除缓存
        String cacheKey = USER_ORDER_CACHE_KEY + userId;
        redisTemplate.delete(cacheKey);

        return Result.success();
    }

    /**
     * 加载评论
     *
     * @param dto
     * @return
     */
    @Override
    public Result load(CommentDTO dto) {
        //1.检查参数
        if (dto == null || dto.getDishId() == null && dto.getSetmealId() == null) {
            throw new CommentException(INVALID_PARAM);
        }
        //2.设置分页参数
        int size = dto.getSize() != null ? dto.getSize() : 10;
        // 设置最大返回数量限制，防止性能问题
//        size = Math.min(size, 50);

        //加载数据
        Date adjustedMinDate = new Date(dto.getMinDate().getTime() + 8 * 60 * 60 * 1000);
        Query query = null;
        if (dto.getDishId() != null) {
            query = Query.query(
                    Criteria.where("dishId")
                            .is(dto.getDishId())
                            .and("createdTime")
                            .lt(adjustedMinDate));
        } else {
            query = Query.query(
                    Criteria.where("setmealId")
                            .is(dto.getSetmealId())
                            .and("createdTime")
                            .lt(adjustedMinDate));
        }

        // 设置排序和分页
        query.with(Sort.by(Sort.Direction.DESC, "createdTime")).limit(size);
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        //因为需要额外获取回复数 所以使用map构建分页结果
        Map<String, Object> map = new HashMap<>();

        // 获取当前菜品的总评论数（不含回复数）
        long total = mongoTemplate.count(query, Comment.class);
        map.put("total", total);
        //遍历所有评论 根据评论id去统计回复评论数量
        long countCommentRepay = 0;
        List<Comment> processedComments = new ArrayList<>();
        for (Comment comment : comments) {
            //对评论的内容进行安全检查并赋值给新数组
            Comment processedComment = processCommentContent(comment);
            processedComments.add(processedComment);
            //获取当前评论的回复数
            countCommentRepay += mongoTemplate.count(Query.query(Criteria.where("rootCommentId").is(comment.getId())), CommentRepay.class);
        }
        //加上评论数 就是当前商品的所有评论数量
        countCommentRepay += total;
        map.put("countCommentRepay", countCommentRepay);

        //3.1 用户未登录
        Long userId = BaseContext.getUserId();
        // 测试期间给定值
//        userId = userId == null ? 1L : userId;
        if (userId == null) {
            //评论结果
            map.put("records", comments);
            //返回所有评论
            return Result.success(map);
        }

        //3.2 用户已登录
        //需要查询当前评论中哪些数据被点赞了
        List<String> idList = processedComments.stream().map(Comment::getId).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentId").in(idList).and("authorId").is(userId));
        List<CommentLike> commentLikes = mongoTemplate.find(query1, CommentLike.class);
        //说明当前评论没有被点赞过
        if (commentLikes.isEmpty()) {
            map.put("records", processedComments);
            return Result.success(map);
        }


        //遍历所有评论 根据评论id去查询当前用户是否点赞过
        List<CommentVo> resultList = new ArrayList<>();
        processedComments.forEach(x -> {
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(x, vo);
            for (CommentLike apCommentLike : commentLikes) {
                if (x.getId().equals(apCommentLike.getCommentId())) {
                    vo.setOperation((short) 0);
                    break;
                }
            }
            resultList.add(vo);
        });
        //登录后的评论结果
        map.put("records", resultList);
        return Result.success(map);
    }

    /**
     * 评论点赞
     *
     * @param dto
     * @return
     */
    @Override
    public Result like(CommentLikeDTO dto) {
        //1.检查参数
        if (dto == null || dto.getCommentId() == null) {
            throw new CommentException(INVALID_PARAM);
        }
        //2.判断是否登录
        Long userId = BaseContext.getUserId();
        // 测试期间给定值
//        userId = userId == null ? 1L : userId;
        if (userId == null) {
            throw new CommentException(UNAUTHORIZED);
        }

        Comment comment = mongoTemplate.findById(dto.getCommentId(), Comment.class);
        //判断当前用户是否点赞
        if (comment != null && dto.getOperation() == 0) {
            //点赞
            comment.setLikes(comment.getLikes() + 1);
            mongoTemplate.save(comment);

            //保存评论点赞数据
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(comment.getId());
            commentLike.setAuthorId(userId);
            mongoTemplate.save(commentLike);
        } else {
            //取消点赞
            int tmp = comment.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;
            comment.setLikes(tmp);
            mongoTemplate.save(comment);

            //删除评论点赞
            Query query = Query.query(Criteria.where("commentId").is(comment.getId()).and("authorId").is(userId));
            mongoTemplate.remove(query, CommentLike.class);
        }
        //返回点赞数给前端展示
        Map<String, Object> result = new HashMap<>();
        result.put("likes", comment.getLikes());
        return Result.success(result);
    }

    /**
     * 判断是否评论
     *
     * @param orderIds
     * @return
     */
    @Override
    public Result isComment(IsCommentDTO orderIds) {
        //获取当前用户id
        Long userId = BaseContext.getUserId();
        // 临时使用默认用户ID，实际应该从JWT中获取
//        userId = userId == null ? 1L : userId;
        if (userId == null) {
            throw new CommentException(UNAUTHORIZED);
        }

        List<IsCommentVO> result = orderIds.getOrderIds().stream().map(orderId -> {
            IsCommentVO vo = new IsCommentVO();
            vo.setOrderId(orderId);
            //查询该订单是否被评论过
            Orders orderById = orderMapper.getOrderById(orderId);
            vo.setIsComment((orderById.getIsComment()));
            return vo;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 管理端分页查询评论
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult page(CommentPageQueryDTO dto) {
        Query query = new Query();
        //总条数
        long total = mongoTemplate.count(query, Comment.class);
        //定义查询条件
        List<Criteria> orCriteria = new ArrayList<>();
        if (dto.getDishId() != null) {
            orCriteria.add(Criteria.where("dishId").is(dto.getDishId()));
        }
        if (dto.getSetmealId() != null) {
            orCriteria.add(Criteria.where("setmealId").is(dto.getSetmealId()));
        }
        if (!orCriteria.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(orCriteria));
        }
        query.with(Sort.by(Sort.Direction.DESC, "createdTime"))
                .skip((long) (dto.getPage() - 1) * dto.getPageSize())
                .limit(dto.getPageSize());
        //查询
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVo commentVo = new CommentVo();
            //检查评论内容是否违规
            Comment processComment = processCommentContent(comment);
            BeanUtils.copyProperties(processComment, commentVo);
            if (comment.getDishId() != null) {
                commentVo.setDishName(dishMapper.getDishById(comment.getDishId()).getName());
            }
            if (comment.getSetmealId() != null) {
                commentVo.setSetmealName(setmealMapper.getSetmealById(comment.getSetmealId()).getName());
            }
            commentVos.add(commentVo);
        }

        PageResult pageResult = new PageResult(total, commentVos);
        return pageResult;
    }

    /**
     * 批量删除评论
     *
     * @param ids
     */
    @Override
    public Result deleteById(List<String> ids) {
        ids.forEach(this::deleteComment);
        return Result.success();
    }

    /**
     * 根据id查询评论
     *
     * @param id
     * @return
     */
    @Override
    public Result<CommentVo> getById(String id) {
        //先查询评论
        Query query = Query.query(Criteria.where("id").is(id));
        Comment comment = mongoTemplate.findOne(query, Comment.class);
        //根据主评论id查询该评论下的回复
        query = Query.query(Criteria.where("rootCommentId").is(id));
        List<CommentRepay> commentRepays = mongoTemplate.find(query, CommentRepay.class);
        //封装评论
        CommentVo commentVo = new CommentVo();
        //属性拷贝
        if (comment != null) {
            BeanUtils.copyProperties(comment, commentVo);
        }
        //按照时间倒序
        commentRepays.sort(Comparator.comparing(CommentRepay::getCreatedTime).reversed());
        //封装回复
        commentVo.setCommentRepayVos(commentRepays);
        return Result.success(commentVo);
    }

    private void deleteComment(String id) {
        //构建查询条件
        Query query = null;
        query = Query.query(Criteria.where("id").is(id));
        //根据id删除评论
        mongoTemplate.remove(query, Comment.class);
        //构建查询条件
        query = Query.query(Criteria.where("commentId").is(id));
        //删除该评论下的点赞
        mongoTemplate.remove(query, CommentLike.class);
        //先查询出所有回复评论
        query = Query.query(Criteria.where("rootCommentId").is(id));
        List<CommentRepay> commentRepays = mongoTemplate.find(query, CommentRepay.class);
        //再删除该评论下的回复
        mongoTemplate.remove(query, CommentRepay.class);

        //删除回复评论下的点赞
        for (CommentRepay commentRepay : commentRepays) {
            query = Query.query(Criteria.where("commentRepayId").is(commentRepay.getId()));
            mongoTemplate.remove(query, CommentRepayLike.class);
        }
    }

    /**
     * 初始化敏感词
     */
    @PostConstruct
    public void initSensitive() {
        SensitivePageQueryDTO dto = new SensitivePageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(1000);
        //查询所有敏感词
        PageResult page = sensitiveService.page(dto);
        List<?> records = page.getRecords();
        Collection<String> words = records.stream()
                .map(record -> ((Sensitive) record).getSensitives())
                .toList();
        SensitiveWordUtil.initMap(words);
        log.info("初始化敏感词完成：{}", words);
    }

    /**
     * 处理评论内容
     *
     * @param comment
     * @return
     */
    private Comment processCommentContent(Comment comment) {
        try {
            //自动审核过慢所以不在加载评论时使用 只在评论时使用
            // 自动审核评论
//            Map<String, String> scanResult = greenTextScan.greenTextScan(comment.getContent());
//            if (scanResult != null) {
//                for (String textRiskLabel : textRiskLabels) {
//                    // 自动审核评论失败 存在违规内容
//                    if (scanResult.get("label").equals(textRiskLabel)) {
//                        Comment newComment = new Comment();
//                        BeanUtils.copyProperties(newComment, comment);
//                        newComment.setContent("该评论存在违规内容");
//                        return newComment;
//                    }
//                }
//            }
            // 数据库敏感词审核
            Map<String, Integer> sensitiveWords = SensitiveWordUtil.matchWords(comment.getContent());
            // 不为空说明存在违规内容
            if (!sensitiveWords.isEmpty()) {
                Comment newComment = new Comment();
                newComment.setAuthorName(comment.getAuthorName());
                newComment.setImage(comment.getImage());
                newComment.setDishId(comment.getDishId());
                newComment.setSetmealId(comment.getSetmealId());
                BeanUtils.copyProperties(newComment, comment);
                newComment.setContent(VIOLATION_CONTENT);
                return newComment;
            }
            return comment;
        } catch (Exception e) {
            e.printStackTrace();
            Comment newComment = new Comment();
            newComment.setAuthorName(comment.getAuthorName());
            newComment.setImage(comment.getImage());
            newComment.setDishId(comment.getDishId());
            newComment.setSetmealId(comment.getSetmealId());
            BeanUtils.copyProperties(newComment, comment);
            newComment.setContent(VIOLATION_CONTENT);
            return newComment;
        }
    }
}
