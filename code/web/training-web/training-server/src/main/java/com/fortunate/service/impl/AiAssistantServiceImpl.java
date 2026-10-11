package com.fortunate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.fortunate.chatService.ConsultantService;
import com.fortunate.chatService.RecommenAssistantService;
import com.fortunate.constant.Constants;
import com.fortunate.context.ToolContext;
import com.fortunate.dto.AiChatDTO;
import com.fortunate.entity.*;
import com.fortunate.enumeration.ChatEventTypeEnum;
import com.fortunate.mapper.*;
import com.fortunate.repository.RedisChatMemoryStore;
import com.fortunate.service.AiAssistantService;
import com.fortunate.tools.result.ToolResultHolder;
import com.fortunate.vo.ChatEventVO;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.fortunate.constant.Constants.DISH;

/**
 * AI智能助手服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiAssistantServiceImpl implements AiAssistantService {

    private final OrderMapper orderMapper;
    private final DishMapper dishMapper;
    private final ConsultantService consultantService;
    private final RecommenAssistantService recommendationService;
    private final OrderDetailMapper orderDetailMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final ItemViewMapper itemViewMapper;
    private final ChatEventVO STOP_EVENT = new ChatEventVO(null, ChatEventTypeEnum.STOP.getValue());


    /**
     * 处理AI聊天请求
     *
     * @param chatDTO 聊天请求DTO
     * @param userId  用户ID
     * @return
     */
    @Override
    public Flux<ChatEventVO> processChat(AiChatDTO chatDTO, Long userId) {
        String message = chatDTO.getMessage();
        String sessionId = chatDTO.getSessionId();
        log.info("处理AI聊天请求，用户ID：{}，消息：{}", userId, message);
        return handleGeneralQuestion(sessionId, message, userId);
    }

    /**
     * 处理普通问题
     *
     * @param sessionId
     * @param message   用户消息
     * @param userId
     * @return
     */
    @Override
    public Flux<ChatEventVO> handleGeneralQuestion(String sessionId, String message, Long userId) {
        try {
            // 生成本次请求id
            var requestId = IdUtil.fastSimpleUUID();
//            TokenStream stream = consultantService.chat(sessionId, message);
            // 设置工具上下文（以会话 memoryId 为作用域，并发会话互不串扰）
            ToolContext.put(sessionId, ToolContext.REQUEST_ID, requestId);
            ToolContext.put(sessionId, ToolContext.USER_ID, userId);
            return Flux.create(sink -> {
                consultantService.chat(sessionId, message)
                        .onPartialResponse(content -> {
                            //ai正常返回的数据
                            ChatEventVO chatEventVO = new ChatEventVO(content, ChatEventTypeEnum.DATA.getValue());
                            sink.next(chatEventVO);
                        })
                        .onCompleteResponse(chatResponse -> {
                            //ai完成回答后从工具上下文获取数据
                            Map<String, Object> map = ToolResultHolder.get(requestId);
                            //存在数据则追加参数
                            if (CollUtil.isNotEmpty(map)) {
                                ToolResultHolder.remove(requestId);
                                ChatEventVO chatEventVO = ChatEventVO.builder()
                                        .eventData(map)
                                        .eventType(ChatEventTypeEnum.PARAM.getValue())
                                        .build();
                                //追加参数
                                sink.next(chatEventVO);
                                sink.next(STOP_EVENT);
                            } else {
                                sink.next(STOP_EVENT);
                            }
                            //清理工具上下文
                            ToolContext.clear(sessionId);
                        })
                        .onToolExecuted(toolExecution -> {
                            String name = toolExecution.request().name();
                            log.info("执行工具：{}", name);
                            String messageId = toolExecution.request().id();
                            //设置本次请求id
                            ToolResultHolder.put(messageId, ToolContext.REQUEST_ID, requestId);
                        })
                        .onRetrieved(c -> {
                            for (Content content : c) {
                                log.info("检索内容：{}", content);
                            }
                        })
                        .onError(error -> {
                            log.error("AI处理通用问题失败", error);
                            // 异常时也必须结束流，否则 SSE 连接悬挂不释放
                            sink.next(STOP_EVENT);
                            sink.complete();
                            //清理工具上下文与工具结果缓存，防止静态 Map 条目泄漏
                            ToolResultHolder.remove(requestId);
                            ToolContext.clear(sessionId);
                        })
                        .start();
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理通用问题失败");
            return Flux.just(STOP_EVENT);
        }
    }

    /**
     * 获取智能推荐菜品
     *
     * @param userId     用户ID
     * @param preference 用户偏好描述
     * @param shopId     店铺ID
     * @return
     */
    @Override
    public List<Map<String, Object>> getSmartRecommendations(Long userId, String preference, Long shopId) {
        log.info("智能推荐菜品，用户ID：{}，偏好：{}，店铺ID：{}", userId, preference, shopId);
        try {
            // 1. 获取所有可用菜品
            List<Dish> allDishes = dishMapper.list(null);
            if (allDishes == null || allDishes.isEmpty()) {
                return createEmptyRecommendation();
            }

            // 无偏好时不走百炼，传统推荐秒回（避免空提示也等十几秒）
            if (StrUtil.isBlank(preference)) {
                return getTraditionalRecommendations(allDishes, preference);
            }

            // 2. 使用通义 AI分析用户偏好并推荐菜品
            List<Map<String, Object>> recommendations = getAiRecommendations(allDishes, preference);
            return recommendations;
        } catch (Exception e) {
            log.error("智能推荐菜品失败", e);
            return createMockRecommendations(preference);
        }
    }


    /**
     * 使用通义 AI进行智能推荐
     */
    private List<Map<String, Object>> getAiRecommendations(List<Dish> allDishes, String preference) {
        try {
            // 构建菜品信息字符串（再压缩候选与字段，降低百炼延迟）
            StringBuilder dishesInfo = new StringBuilder();
            for (int i = 0; i < Math.min(allDishes.size(), 10); i++) {
                Dish dish = allDishes.get(i);
                dishesInfo.append(String.format("%d.%s ¥%.0f\n",
                        dish.getId(), dish.getName(), dish.getPrice()));
            }

            // 短提示：只要 id + 短理由，便于模型快回
            String userPrompt = String.format(
                    "偏好：%s\n菜品：\n%s请推荐3道，只返回JSON数组：" +
                            "[{\"id\":数字,\"name\":\"名\",\"price\":数,\"reason\":\"理由≤20字\",\"categoryId\":数,\"type\":1}]",
                    preference, dishesInfo);

            // 同步百炼超时：超时则传统推荐；12s 兼顾体感与偶发慢响应
            final int aiTimeoutSec = 12;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(
                    () -> recommendationService.chat(userPrompt));
            String aiResponse;
            try {
                aiResponse = future.get(aiTimeoutSec, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                future.cancel(true);
                log.warn("通义 AI推荐超时（{}s），使用传统推荐方法", aiTimeoutSec);
                return getTraditionalRecommendations(allDishes, preference);
            } catch (Exception ex) {
                future.cancel(true);
                throw ex;
            }
            log.info("通义 AI推荐响应：{}", aiResponse);

            // 解析AI响应
            List<Map<String, Object>> recommendations = parseAiRecommendations(aiResponse, allDishes);

            if (recommendations.isEmpty()) {
                // 如果AI解析失败，使用传统方法作为降级
                log.warn("AI推荐解析失败，使用传统推荐方法");
                return getTraditionalRecommendations(allDishes, preference);
            }

            return recommendations;

        } catch (Exception e) {
            log.error("通义 AI推荐失败，使用传统推荐方法", e);
            return getTraditionalRecommendations(allDishes, preference);
        }
    }

    /**
     * 解析AI推荐响应
     */
    public List<Map<String, Object>> parseAiRecommendations(String aiResponse, List<Dish> allDishes) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        try {
            // 清理响应文本，提取JSON部分
            String jsonStr = aiResponse.trim();
            jsonStr = jsonStr.trim();
            List<Map> recommendationList = JSON.parseArray(jsonStr, Map.class);

            log.info("AI推荐响应：{}", recommendationList);
            for (Map map1 : recommendationList) {
                Map<String, Object> recommendation1 = new HashMap<>();
                String idObj = map1.get("id").toString();
                Long dishId;
                dishId = Long.valueOf(idObj);
                // 查找对应的菜品
                Dish dish = allDishes.stream()
                        .filter(d -> d.getId().equals(dishId))
                        .findFirst()
                        .orElse(null);
                //查询对应的口味
                List<DishFlavor> flavors = dishFlavorMapper.getFlavorsByDishId(dishId);
                if (dish != null) {
                    recommendation1.put("id", dish.getId());
                    recommendation1.put("name", dish.getName());
                    recommendation1.put("price", dish.getPrice());
                    recommendation1.put("image", dish.getImage());
                    recommendation1.put("flavors", flavors);
                    // 提取推荐理由
                    recommendation1.put("reason", map1.get("reason"));
                }
                recommendations.add(recommendation1);
            }
            log.info("推荐结果：{}", recommendations);
        } catch (Exception e) {
            log.error("解析AI推荐响应失败", e);
        }

        return recommendations;
    }

    /**
     * 传统推荐方法（作为AI推荐的降级方案）
     */
    private List<Map<String, Object>> getTraditionalRecommendations(List<Dish> allDishes, String preference) {
        List<Dish> filteredDishes = filterDishesByPreference(allDishes, preference);
        List<Map<String, Object>> recommendations = new ArrayList<>();
        int maxRecommendations = Math.min(5, filteredDishes.size());

        for (int i = 0; i < maxRecommendations; i++) {
            Dish dish = filteredDishes.get(i);
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("id", dish.getId());
            recommendation.put("name", dish.getName());
            recommendation.put("price", dish.getPrice());
            recommendation.put("image", dish.getImage());
            recommendation.put("reason", generateRecommendReason(dish, preference));
            recommendations.add(recommendation);
        }

        return recommendations;
    }

    /**
     * 根据用户偏好筛选菜品
     */
    private List<Dish> filterDishesByPreference(List<Dish> dishes, String preference) {
        if (preference == null || preference.trim().isEmpty()) {
            return dishes.stream().limit(5).collect(Collectors.toList());
        }

        String lowerPreference = preference.toLowerCase();
        List<Dish> filtered = new ArrayList<>();

        // 根据关键词筛选
        for (Dish dish : dishes) {
            String dishName = dish.getName().toLowerCase();
            String dishDescription = dish.getDescription() != null ? dish.getDescription().toLowerCase() : "";

            // 辣味偏好
            if (lowerPreference.contains("辣") || lowerPreference.contains("麻")) {
                if (dishName.contains("辣") || dishName.contains("麻") ||
                        dishDescription.contains("辣") || dishDescription.contains("麻")) {
                    filtered.add(dish);
                }
            }
            // 清淡偏好
            else if (lowerPreference.contains("清淡") || lowerPreference.contains("不辣") || lowerPreference.contains("淡")) {
                if (!dishName.contains("辣") && !dishName.contains("麻") &&
                        !dishDescription.contains("辣") && !dishDescription.contains("麻")) {
                    filtered.add(dish);
                }
            }
            // 汤类偏好
            else if (lowerPreference.contains("汤") || lowerPreference.contains("粉")) {
                if (dishName.contains("汤") || dishName.contains("粉") ||
                        dishDescription.contains("汤") || dishDescription.contains("粉")) {
                    filtered.add(dish);
                }
            }
            // 默认推荐热门菜品
            else {
                filtered.add(dish);
            }
        }

        // 如果筛选结果为空，返回前几个菜品
        if (filtered.isEmpty()) {
            return dishes.stream().limit(5).collect(Collectors.toList());
        }

        return filtered.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * 生成推荐理由
     */
    private String generateRecommendReason(Dish dish, String preference) {
        if (preference == null || preference.trim().isEmpty()) {
            return "经典美味，值得一试";
        }

        String lowerPreference = preference.toLowerCase();
        String dishName = dish.getName().toLowerCase();

        if (lowerPreference.contains("辣") && (dishName.contains("辣") || dishName.contains("麻"))) {
            return "香辣过瘾，满足您对辣味的需求";
        } else if (lowerPreference.contains("清淡") && !dishName.contains("辣")) {
            return "口味清淡，营养健康";
        } else if (lowerPreference.contains("汤") && dishName.contains("汤")) {
            return "汤汁鲜美，暖胃又暖心";
        } else if (lowerPreference.contains("粉") && dishName.contains("粉")) {
            return "口感Q弹，汤汁浓郁";
        } else {
            return "根据您的喜好精心推荐";
        }
    }

    /**
     * 创建空推荐结果
     */
    private List<Map<String, Object>> createEmptyRecommendation() {
        return new ArrayList<>();
    }

    /**
     * 创建模拟推荐数据
     */
    private List<Map<String, Object>> createMockRecommendations(String preference) {
        List<Map<String, Object>> mockData = new ArrayList<>();
        //查询数据库获取所有菜品
        List<Dish> allDishes = dishMapper.list(new Dish());
        //根据热度进行排序
        allDishes.sort(Comparator.comparing(Dish::getHotSpot, Comparator.nullsLast(Comparator.reverseOrder())).reversed());

        //最多返回5道菜；菜品不足 5 条时按实际数量返回，避免数组越界
        int limit = Math.min(5, allDishes.size());
        for (int i = 0; i < limit; i++) {
            Map<String, Object> item = new HashMap<>();
            Dish dish = allDishes.get(i);
            item.put("id", dish.getId());
            item.put("name", dish.getName());
            item.put("price", dish.getPrice());
            item.put("image", dish.getImage());
            item.put("reason", dish.getName() + "，" + generateRecommendReason(dish, preference));
            mockData.add(item);
        }

        return mockData;
    }
}
