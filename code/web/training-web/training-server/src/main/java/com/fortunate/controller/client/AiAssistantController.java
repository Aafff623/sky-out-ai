package com.fortunate.controller.client;

import com.fortunate.chatService.AiCommentService;
import com.fortunate.chatService.ConsultantService;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.AiChatDTO;
import com.fortunate.dto.CustomerServiceMessageDTO;
import com.fortunate.dto.RecommendRequestDTO;
import com.fortunate.exception.AiException;
import com.fortunate.result.Result;
import com.fortunate.service.AiAssistantService;
import com.fortunate.service.CommentService;
import com.fortunate.service.CustomerServiceService;
import com.fortunate.vo.AiChatResponseVO;
import com.fortunate.vo.ChatEventVO;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * AI智能助手控制器
 */
@RestController
@RequestMapping("/client/ai-assistant")
@Api(tags = "AI智能助手相关接口")
@RequiredArgsConstructor
@Slf4j
public class AiAssistantController {
    private final AiAssistantService aiAssistantService;

    /**
     * AI聊天接口
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation("AI聊天")
    public Flux<ChatEventVO> chat(@RequestBody AiChatDTO chatDTO) {
        log.info("AI聊天请求：{}", chatDTO);
        // 验证sessionId
        if (chatDTO.getSessionId() == null || chatDTO.getSessionId().trim().isEmpty()) {
            log.error("AI聊天请求缺少sessionId");
            throw new AiException("会话ID不能为空");
        }

        // 从JWT中获取真实用户ID
        Long userId = BaseContext.getUserId() ;
//        Long userId = 2L;

        // 处理AI聊天
        return aiAssistantService.processChat(chatDTO, userId);
    }

    /**
     * AI智能推荐菜品
     */
    @PostMapping("/recommend")
    @ApiOperation("AI智能推荐菜品")
    public Result<List<Map<String, Object>>> recommendDishes(@RequestBody RecommendRequestDTO requestDTO) {
        log.info("AI智能推荐菜品请求：{}", requestDTO);

        // 这里需要从JWT中获取用户ID，暂时使用固定值
        Long userId = BaseContext.getUserId();
        try {
            // 调用AI推荐服务
            List<Map<String, Object>> recommendations = aiAssistantService.getSmartRecommendations(userId, requestDTO.getPreference(), requestDTO.getShopId());
            return Result.success(recommendations);
        } catch (Exception e) {
            log.error("AI推荐菜品失败", e);
            return Result.error("推荐失败，请稍后重试");
        }
    }
}
