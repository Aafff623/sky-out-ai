package com.fortunate.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AI 智能客服 SSE 聊天接口测试（真实调百炼）
 * 注意：SSE 流式接口用 MockMvc 只能验证连接建立和初始事件，完整流式消费需要真实 HTTP 客户端。
 */
@DisplayName("AI - 智能客服聊天")
class AiChatApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /client/ai-assistant/chat - 发送消息建立 SSE 连接")
    void chat_establishConnection() throws Exception {
        String sessionId = "test-" + UUID.randomUUID();
        String body = "{\"sessionId\":\"" + sessionId + "\",\"message\":\"你好\",\"messageType\":1}";

        // SSE 接口返回 Flux<ChatEventVO>，MockMvc 会等流完成或超时
        // 这里验证接口能正常响应（不抛异常、状态码 200）
        MvcResult result = mockMvc.perform(post("/client/ai-assistant/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("authentication", clientToken())
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andReturn();

        // SSE 响应可能是 200 或异步处理，只要不 4xx/5xx 就算通
        int statusCode = result.getResponse().getStatus();
        org.junit.jupiter.api.Assertions.assertTrue(statusCode < 400,
                "SSE chat should not return error, got " + statusCode);
    }
}
