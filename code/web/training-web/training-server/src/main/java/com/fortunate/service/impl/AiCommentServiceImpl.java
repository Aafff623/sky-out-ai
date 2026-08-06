package com.fortunate.service.impl;

import com.alibaba.fastjson.JSON;
import com.fortunate.chatService.AiCommentExpandServiceAgent;
import com.fortunate.chatService.AiCommentService;
import com.fortunate.dto.CommentSaveDTO;
import com.fortunate.entity.Dish;
import com.fortunate.entity.Setmeal;
import com.fortunate.exception.CommentException;
import com.fortunate.mapper.DishMapper;
import com.fortunate.mapper.SetmealMapper;
import com.fortunate.service.AiCommentExpandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fortunate.constant.MessageConstant.INVALID_PARAM;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCommentServiceImpl implements AiCommentExpandService {
    private final AiCommentService aiCommentService;
    private final DishMapper dishMapper;
    private final AiCommentExpandServiceAgent aiCommentExpandServiceAgent;
    private final SetmealMapper setmealMapper;
    // 通用评价模板
    private static final String COMMENT_TEMPLATE = "这道菜%s，口感%s，%s。总体来说%s，值得推荐。";


    /**
     * ai评论帮写、续写、润色、精简
     *
     * @param dto
     * @return
     */
    @Override
    public Map<String, String> expand(CommentSaveDTO dto) {

        if (dto.getDishIds() != null || dto.getSetmealIds() != null) {
            //获取菜品id集合
            List<Long> dishIds = null;
            //菜品数据集合
            List<Dish> dishes = null;
            //获取套餐id集合
            List<Long> setmealIds = null;
            //套餐数据集合
            List<Setmeal> setmealByIds = null;
            //拼接菜品数据
            String str = null;
            //说明订单评论而非是普通评论
            //根据菜品id集合查询菜品
            dishIds = dto.getDishIds();

            if (dishIds != null && !dishIds.isEmpty()) {
                //去除集合中的重复元素
                dishIds = dishIds.stream().distinct().toList();
                dishes = dishMapper.getDishByIds(dishIds);
            }
            //根据套餐id集合查询套餐
            setmealIds = dto.getSetmealIds();

            if (setmealIds != null && !setmealIds.isEmpty()) {
                //去除集合中的重复元素
                setmealIds = setmealIds.stream().distinct().toList();
                setmealByIds = setmealMapper.getSetmealByIds(setmealIds);
            }
            if (dishes != null && !dishes.isEmpty()) {
                str += dishes.toString();
            }
            //拼接套餐数据
            if (setmealByIds != null && !setmealByIds.isEmpty()) {
                str += setmealByIds.toString();
            }
            //将菜品或套餐转为json字符串
            String jsonStr = null;
            if (str != null) {
                jsonStr = JSON.toJSONString(str);
            }
            String string = null;
            try {
                string = aiCommentService.chat(dto.getContent(), dto.getType(), jsonStr);
            } catch (Exception e) {
                //扩写失败使用传统降级
                string = getTraditionalRecommendations(dto);
            }
            log.info("Ai评论扩写结果：{}", string);
            Map<String, String> map = new HashMap<>();
            map.put("msg", string);
            return map;
        }
        //否则就是普通评论
        String string = aiCommentExpandServiceAgent.chat(dto.getContent(), dto.getType(),dto.getOriginalComment());
        log.info("Ai评论扩写结果：{}", string);
        Map<String, String> map = new HashMap<>();
        map.put("msg", string);
        return map;
    }

    //ai括写失败降级方法
    private String getTraditionalRecommendations(CommentSaveDTO dto) {
        List<Long> dishIds = dto.getDishIds();
        List<Long> setmealIds = dto.getSetmealIds();
        StringBuilder sb = new StringBuilder();
        if (dishIds != null && !dishIds.isEmpty()) {
            for (Long dishId : dishIds) {
                Dish dish = dishMapper.getDishById(dishId);
                if (dish != null) {
                    String dishName = dish.getName();
                    String comment = generateSimpleComment(dishName);
                    sb.append(comment).append("\n");
                }
            }
        }
        if (setmealIds != null && !setmealIds.isEmpty()) {
            for (Long setmealId : setmealIds) {
                Setmeal setmeal = setmealMapper.getSetmealById(setmealId);
                if (setmeal != null) {
                    String setmealName = setmeal.getName();
                    String comment = generateSimpleSetmealComment(setmealName);
                    sb.append(comment).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 生成简单的通用菜品评价文本
     *
     * @param dishName 菜品名称
     * @return 通用评价文本
     */
    private String generateSimpleComment(String dishName) {
        String[] comments = {
                "这道" + dishName + "味道很不错，值得一试。",
                dishName + "做得非常好，色香味俱全。",
                "非常喜欢这道" + dishName + "，推荐大家品尝。",
                dishName + "口感丰富，味道鲜美，很棒的选择。",
                "这道" + dishName + "制作精良，味道正宗。",
                dishName + "看起来很有食欲，吃起来也很美味。",
                "很棒的" + dishName + "，味道正宗，分量也足。",
                dishName + "是我很喜欢的一道菜，每次必点。",
                "这道" + dishName + "做得很用心，味道也很棒。",
                dishName + "色香味俱佳，是餐桌上的亮点。"
        };

        // 随机选择一个评价
        int index = (int) (Math.random() * comments.length);
        return comments[index];
    }

    /**
     * 生成通用套餐评价文本
     *
     * @param setmealName 套餐名称
     * @return 通用评价文本
     */
    private String generateSimpleSetmealComment(String setmealName) {
        String[] comments = {
                "这个" + setmealName + "搭配合理，性价比很高。",
                setmealName + "内容丰富，味道都很不错。",
                "套餐" + setmealName + "很超值，推荐大家尝试。",
                setmealName + "的组合很棒，每道菜都很好吃。",
                "这个" + setmealName + "真的很划算，菜品质量也很好。"
        };

        // 随机选择一个评价
        int index = (int) (Math.random() * comments.length);
        return comments[index];
    }

    /**
     * 生成基础通用评价文本（不区分具体菜品）
     *
     * @return 通用评价文本
     */
    private String generateBasicComment() {
        String[] comments = {
                "味道很不错，值得推荐。",
                "做得很棒，色香味俱全。",
                "非常喜欢，会再次购买。",
                "口感丰富，味道鲜美。",
                "制作精良，味道正宗。",
                "看起来很有食欲，吃起来也很棒。",
                "味道很好，分量也足。",
                "是我很喜欢的口味，推荐大家品尝。",
                "做得很用心，味道也很棒。",
                "色香味俱佳，非常满意。"
        };

        // 随机选择一个评价
        int index = (int) (Math.random() * comments.length);
        return comments[index];
    }

}
