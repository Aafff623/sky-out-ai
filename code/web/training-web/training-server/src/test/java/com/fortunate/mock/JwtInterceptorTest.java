package com.fortunate.mock;

import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JWT 拦截器边界测试：验证无 token / 错误 token 时返回 401
 */
@DisplayName("JWT 拦截器边界")
class JwtInterceptorTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/employee/page - 无 token 返回 401")
    void storeNoToken() throws Exception {
        mockMvc.perform(get("/store/employee/page")
                        .param("page", "1").param("pageSize", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /store/dish/page - 无 token 返回 401")
    void storeDishNoToken() throws Exception {
        mockMvc.perform(get("/store/dish/page")
                        .param("page", "1").param("pageSize", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /store/workspace/businessData - 无 token 返回 401")
    void storeWorkspaceNoToken() throws Exception {
        mockMvc.perform(get("/store/workspace/businessData"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /store/order/conditionSearch - 无 token 返回 401")
    void storeOrderNoToken() throws Exception {
        mockMvc.perform(get("/store/order/conditionSearch")
                        .param("page", "1").param("pageSize", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /store/employee/page - 错误 token 返回 401")
    void storeBadToken() throws Exception {
        mockMvc.perform(get("/store/employee/page")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", "bad-token-value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /client/shoppingCart/list - 无 authentication 返回 401")
    void clientCartNoToken() throws Exception {
        mockMvc.perform(get("/client/shoppingCart/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /client/order/historyOrders - 无 authentication 返回 401")
    void clientOrderNoToken() throws Exception {
        mockMvc.perform(get("/client/order/historyOrders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /client/addressBook/list - 无 authentication 返回 401")
    void clientAddressNoToken() throws Exception {
        mockMvc.perform(get("/client/addressBook/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /client/ai-assistant/recommend - 无 authentication 返回 401")
    void clientAiRecommendNoToken() throws Exception {
        mockMvc.perform(post("/client/ai-assistant/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"preference\":\"辣\",\"shopId\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /client/ai-comment/expand - 无 authentication 返回 401")
    void clientAiCommentNoToken() throws Exception {
        mockMvc.perform(post("/client/ai-comment/expand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalComment\":\"好吃\",\"type\":1}"))
                .andExpect(status().isUnauthorized());
    }
}
