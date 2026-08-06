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
 * 商家端菜品模块接口测试
 */
@DisplayName("商家端 - 菜品管理")
class DishApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/dish/page - 分页查询菜品")
    void page() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/dish/page")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").path("total").asLong() >= 20);
    }

    @Test
    @DisplayName("GET /store/dish/page - 按名称模糊查询")
    void page_byName() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/dish/page")
                        .param("page", "1").param("pageSize", "10").param("name", "鱼")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }

    @Test
    @DisplayName("GET /store/dish - 按 id 查询菜品详情")
    void getById() throws Exception {
        // 先分页拿一个 id
        MvcResult pageResult = mockMvc.perform(get("/store/dish/page")
                        .param("page", "1").param("pageSize", "1")
                        .header("token", storeToken()))
                .andReturn();
        Long id = parseResult(pageResult).path("data").path("records").get(0).path("id").asLong();

        MvcResult result = mockMvc.perform(get("/store/dish")
                        .param("id", String.valueOf(id))
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertEquals(id, node.path("data").path("id").asLong());
    }

    @Test
    @DisplayName("POST /store/dish/status/{status} - 起售/停售切换")
    void startOrStop() throws Exception {
        // 先拿一个菜品 id
        MvcResult pageResult = mockMvc.perform(get("/store/dish/page")
                        .param("page", "1").param("pageSize", "1")
                        .header("token", storeToken()))
                .andReturn();
        Long id = parseResult(pageResult).path("data").path("records").get(0).path("id").asLong();
        Integer currentStatus = parseResult(pageResult).path("data").path("records").get(0).path("status").asInt();

        // 切换状态
        int newStatus = currentStatus == 1 ? 0 : 1;
        MvcResult result = mockMvc.perform(post("/store/dish/status/" + newStatus)
                        .param("id", String.valueOf(id))
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));

        // 切回来
        mockMvc.perform(post("/store/dish/status/" + currentStatus)
                        .param("id", String.valueOf(id))
                        .header("token", storeToken()))
                .andExpect(status().isOk());
    }
}
