package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 敏感词模块接口测试（无 JWT 拦截）
 */
@DisplayName("敏感词管理")
class SensitiveApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /sensitive - 分页查询敏感词")
    void page() throws Exception {
        MvcResult result = mockMvc.perform(get("/sensitive")
                        .param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }

    @Test
    @DisplayName("POST /sensitive - 新增敏感词")
    void add() throws Exception {
        MvcResult result = mockMvc.perform(post("/sensitive")
                        .param("sensitive", "测试敏感词" + System.currentTimeMillis()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }
}
