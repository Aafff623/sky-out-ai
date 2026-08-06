package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 健康检查与上传接口测试
 */
@DisplayName("基础设施接口")
class HealthAndUploadApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET / - 健康检查")
    void health() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }
}
