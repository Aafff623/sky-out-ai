package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户端公开查询接口测试（无需 JWT）
 */
@DisplayName("用户端 - 公开查询")
class ClientPublicApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /client/category/list - 查询分类列表")
    void categoryList() throws Exception {
        MvcResult result = mockMvc.perform(get("/client/category/list")
                        .param("type", "1"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").isArray());
    }

    @Test
    @DisplayName("GET /client/dish/list - 按分类查询菜品")
    void dishList() throws Exception {
        // 先拿一个分类 id
        MvcResult catResult = mockMvc.perform(get("/client/category/list")
                        .param("type", "1"))
                .andReturn();
        JsonNode categories = parseResult(catResult).path("data");
        if (categories.isEmpty()) return;
        Long categoryId = categories.get(0).path("id").asLong();

        MvcResult result = mockMvc.perform(get("/client/dish/list")
                        .param("categoryId", String.valueOf(categoryId))
                        .param("tabIndex", "0"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }

    @Test
    @DisplayName("GET /client/dish/{id} - 查询菜品详情")
    void dishDetail() throws Exception {
        // 先拿一个菜品
        MvcResult catResult = mockMvc.perform(get("/client/category/list")
                        .param("type", "1"))
                .andReturn();
        JsonNode categories = parseResult(catResult).path("data");
        if (categories.isEmpty()) return;
        Long categoryId = categories.get(0).path("id").asLong();

        MvcResult listResult = mockMvc.perform(get("/client/dish/list")
                        .param("categoryId", String.valueOf(categoryId))
                        .param("tabIndex", "0"))
                .andReturn();
        JsonNode dishes = parseResult(listResult).path("data");
        if (dishes.isEmpty()) return;
        Long dishId = dishes.get(0).path("id").asLong();

        MvcResult result = mockMvc.perform(get("/client/dish/" + dishId))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertEquals(dishId, node.path("data").path("id").asLong());
    }

    @Test
    @DisplayName("GET /client/dish/recommend - 推荐菜品（前4条）")
    void dishRecommend() throws Exception {
        MvcResult result = mockMvc.perform(get("/client/dish/recommend"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").isArray());
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").size() <= 4);
    }

    @Test
    @DisplayName("GET /client/setmeal/list - 查询套餐列表")
    void setmealList() throws Exception {
        MvcResult result = mockMvc.perform(get("/client/setmeal/list")
                        .param("tabIndex", "0"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }

    @Test
    @DisplayName("GET /client/shop/status - 查询营业状态")
    void shopStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/client/shop/status"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        int status = node.path("data").asInt();
        org.junit.jupiter.api.Assertions.assertTrue(status == 0 || status == 1);
    }

    @Test
    @DisplayName("GET /client/shopInfo - 查询店铺信息")
    void shopInfo() throws Exception {
        MvcResult result = mockMvc.perform(get("/client/shopInfo"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }
}
