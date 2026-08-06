package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端分类模块接口测试
 */
@DisplayName("商家端 - 分类管理")
class CategoryApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/category/page - 分页查询分类")
    void page() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/category/page")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").path("total").asLong() >= 1);
    }

    @Test
    @DisplayName("GET /store/category/list - 按类型查询分类列表")
    void list_byType() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/category/list")
                        .param("type", "1")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").isArray());
    }
}
