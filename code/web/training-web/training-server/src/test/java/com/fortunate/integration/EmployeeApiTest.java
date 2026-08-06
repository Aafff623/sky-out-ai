package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端员工模块接口测试
 */
@DisplayName("商家端 - 员工管理")
class EmployeeApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /store/employee/login - 正确凭证登录成功")
    void login_success() throws Exception {
        MvcResult result = mockMvc.perform(post("/store/employee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertFalse(node.path("data").path("token").asText().isEmpty());
    }

    @Test
    @DisplayName("POST /store/employee/login - 错误密码登录失败")
    void login_wrongPassword() throws Exception {
        MvcResult result = mockMvc.perform(post("/store/employee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertError(node);
    }

    @Test
    @DisplayName("GET /store/employee/me - 带 token 获取当前员工")
    void me_withToken() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/employee/me")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertEquals("admin", node.path("data").path("username").asText());
    }

    @Test
    @DisplayName("GET /store/employee/me - 无 token 返回 401")
    void me_noToken() throws Exception {
        mockMvc.perform(get("/store/employee/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /store/employee/page - 分页查询员工")
    void page() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/employee/page")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").path("total").asLong() >= 1);
    }

    @Test
    @DisplayName("POST /store/employee/logout - 登出")
    void logout() throws Exception {
        MvcResult result = mockMvc.perform(post("/store/employee/logout")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }
}
