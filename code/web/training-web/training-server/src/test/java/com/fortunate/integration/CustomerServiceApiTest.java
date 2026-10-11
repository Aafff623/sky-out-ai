package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 客服会话模块接口测试（#1 起 /store/customer-service/** 已纳入 JWT，需商家端 token）
 */
@DisplayName("客服会话")
class CustomerServiceApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/customer-service/sessions/active - 查询活跃会话")
    void activeSessions() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/customer-service/sessions/active")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").isArray());
    }

    @Test
    @DisplayName("GET /store/customer-service/sessions/staff/{staffId} - 查询员工会话")
    void staffSessions() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/customer-service/sessions/staff/1")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }
}
