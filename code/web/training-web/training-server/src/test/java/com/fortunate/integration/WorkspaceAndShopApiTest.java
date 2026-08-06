package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端工作台 + 店铺模块接口测试
 */
@DisplayName("商家端 - 工作台与店铺")
class WorkspaceAndShopApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/workspace/businessData - 今日运营数据")
    void businessData() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/workspace/businessData")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").has("turnover"));
    }

    @Test
    @DisplayName("GET /store/workspace/overviewOrders - 订单概览")
    void overviewOrders() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/workspace/overviewOrders")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }

    @Test
    @DisplayName("GET /store/workspace/overviewDishes - 菜品概览")
    void overviewDishes() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/workspace/overviewDishes")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }

    @Test
    @DisplayName("GET /store/workspace/overviewSetmeals - 套餐概览")
    void overviewSetmeals() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/workspace/overviewSetmeals")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }

    @Test
    @DisplayName("GET /store/shop/status - 查询营业状态")
    void getShopStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/shop/status")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        int status = node.path("data").asInt();
        org.junit.jupiter.api.Assertions.assertTrue(status == 0 || status == 1);
    }

    @Test
    @DisplayName("PUT /store/shop/status/{status} - 设置营业状态")
    void setShopStatus() throws Exception {
        // 先查当前状态
        MvcResult getResult = mockMvc.perform(get("/store/shop/status")
                        .header("token", storeToken()))
                .andReturn();
        int current = parseResult(getResult).path("data").asInt();

        // 切换
        int newStatus = current == 1 ? 0 : 1;
        mockMvc.perform(put("/store/shop/status/" + newStatus)
                        .header("token", storeToken()))
                .andExpect(status().isOk());

        // 验证
        MvcResult verify = mockMvc.perform(get("/store/shop/status")
                        .header("token", storeToken()))
                .andReturn();
        org.junit.jupiter.api.Assertions.assertEquals(newStatus, parseResult(verify).path("data").asInt());

        // 恢复
        mockMvc.perform(put("/store/shop/status/" + current)
                        .header("token", storeToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /store/shopInfo - 查询店铺信息")
    void getShopInfo() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/shopInfo")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertFalse(node.path("data").path("name").asText().isEmpty());
    }
}
