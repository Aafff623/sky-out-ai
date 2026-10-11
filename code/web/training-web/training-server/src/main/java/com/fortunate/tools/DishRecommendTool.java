package com.fortunate.tools;

import com.fortunate.chatService.RecommenAssistantService;
import com.fortunate.context.ToolContext;
import com.fortunate.entity.Dish;
import com.fortunate.entity.DishFlavor;
import com.fortunate.mapper.CategoryMapper;
import com.fortunate.mapper.DishFlavorMapper;
import com.fortunate.mapper.DishMapper;
import com.fortunate.service.AiAssistantService;
import com.fortunate.service.DishService;
import com.fortunate.tools.result.ToolResultHolder;
import com.fortunate.vo.DishVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.MemoryId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.fortunate.constant.Constants.ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DishRecommendTool {
    private final DishMapper dishMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final DishService dishService;
    private final CategoryMapper categoryMapper;
    private AiAssistantService aiAssistantService;

    @Autowired
    public void setAiAssistantService(@Lazy AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }
//    private static final String FIELD_NAME_FORMAT = "{}";  // 提取格式字符串常量

    //获取前6个菜品
    @Tool("推荐菜品给用户")
    public List<Dish> getRecommendDishes(@MemoryId String memoryId) {
        //在每一次请求都会设置一个requestId（以会话 memoryId 为作用域）
        String requestId = ToolContext.get(memoryId, ToolContext.REQUEST_ID).toString();
        //获取所有启用的菜品
        List<Dish> allEnabledDishes = dishMapper.getAllEnabledDishes();
        //随机打乱
        Collections.shuffle(allEnabledDishes);
        //截取前6个
        allEnabledDishes = allEnabledDishes.subList(0, Math.min(allEnabledDishes.size(), 6));
        String field = Dish.class.getSimpleName();
        //转换为VO对象存储
        List<DishVO> dishVOS = listDishWithFlavors(allEnabledDishes);
        //设置返回结果
        ToolResultHolder.put(requestId, field, dishVOS);
        //返回数据
        return allEnabledDishes;
    }

    /**
     * \"id\": 菜品ID,\n" +
     * "\"name\": \"菜品名称\",\n" +
     * "\"price\": 价格,\n" +
     * "\"reason\": \"推荐理由（30字以内）\"\n" +
     *
     * @param preference
     * @return
     */
    @Tool("根据用户偏好推荐菜品")
    public List<DishVO> getDishesByPreference(@MemoryId String memoryId, @P(value = "用户偏好") String preference) {
        //在每一次请求都会设置一个requestId（以会话 memoryId 为作用域）
        String requestId = ToolContext.get(memoryId, ToolContext.REQUEST_ID).toString();
        //获取用户id
        Long userId = (Long) ToolContext.get(memoryId, ToolContext.USER_ID);
        List<Map<String, Object>> smartRecommendations = aiAssistantService.getSmartRecommendations(userId, preference, 1L);

        List<DishVO> dishVOS = parseListMapToDishVOs(smartRecommendations, 6);
        String field = Dish.class.getSimpleName();
        //设置返回结果
        ToolResultHolder.put(requestId, field, dishVOS);
        return dishVOS;
    }

    private List<DishVO> listDishWithFlavors(List<Dish> dishes) {
        List<DishVO> list = dishes.stream()
                .map(dish -> {
                    DishVO dishVO = new DishVO();
                    BeanUtils.copyProperties(dish, dishVO);
                    dishVO.setCategoryName(categoryMapper.getCategoryNameById(dish.getCategoryId()));
                    List<DishFlavor> flavorsByDishId = dishFlavorMapper.getFlavorsByDishId(dish.getId());
                    dishVO.setFlavors(flavorsByDishId);
                    return dishVO;
                }).toList();

        return list;
    }

    private List<DishVO> parseListMapToDishVOs(List<Map<String, Object>> recommendations, Integer limit) {
        return recommendations.stream()
                .map(r -> {
                    Long id = (Long) r.get(ID);
                    return dishService.getByIdWithFlavor(id);
                }).limit(limit).toList();
    }
}
