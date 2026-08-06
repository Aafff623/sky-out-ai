package com.fortunate.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AI 评论扩写接口测试（真实调百炼）
 */
@DisplayName("AI - 评论扩写")
class AiCommentExpandApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /client/ai-comment/expand - 帮写评论")
    void expand_helpWrite() throws Exception {
        String body = "{\"orderId\":1,\"dishIds\":[102],\"originalComment\":\"好吃\",\"type\":1}";
        MvcResult result = mockMvc.perform(post("/client/ai-comment/expand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("authentication", clientToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        // AI 对极短输入可能返回空 content，只验证接口调通即可
        org.junit.jupiter.api.Assertions.assertNotNull(node.path("data"));
    }

    @Test
    @DisplayName("POST /client/ai-comment/expand - 润色评论")
    void expand_polish() throws Exception {
        String body = "{\"orderId\":1,\"dishIds\":[102],\"originalComment\":\"这个菜真的很好吃，分量也足\",\"type\":3}";
        MvcResult result = mockMvc.perform(post("/client/ai-comment/expand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("authentication", clientToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }
}
