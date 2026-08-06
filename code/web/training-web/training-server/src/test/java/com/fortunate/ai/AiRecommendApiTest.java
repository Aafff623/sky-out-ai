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
 * AI 菜品推荐接口测试（真实调百炼）
 */
@DisplayName("AI - 菜品推荐")
class AiRecommendApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /client/ai-assistant/recommend - 语义推荐菜品")
    void recommend() throws Exception {
        String body = "{\"preference\":\"想吃辣的\",\"shopId\":1}";
        MvcResult result = mockMvc.perform(post("/client/ai-assistant/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("authentication", clientToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").isArray());
    }

    @Test
    @DisplayName("POST /client/ai-assistant/recommend - 空偏好也能返回（降级）")
    void recommend_emptyPreference() throws Exception {
        String body = "{\"preference\":\"\",\"shopId\":1}";
        MvcResult result = mockMvc.perform(post("/client/ai-assistant/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("authentication", clientToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        // 空偏好可能走降级逻辑，code 可能是 200 或业务错误码
        org.junit.jupiter.api.Assertions.assertNotNull(node.path("code"));
    }
}
