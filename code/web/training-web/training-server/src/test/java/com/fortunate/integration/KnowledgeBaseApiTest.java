package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端知识库模块接口测试
 */
@DisplayName("商家端 - 知识库管理")
class KnowledgeBaseApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/knowledgeBase/page - 分页查询知识库")
    void page() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/knowledgeBase/page")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }
}
