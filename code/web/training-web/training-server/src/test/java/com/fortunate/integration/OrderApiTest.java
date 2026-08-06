package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端订单模块接口测试
 */
@DisplayName("商家端 - 订单管理")
class OrderApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/order/conditionSearch - 条件分页查询订单")
    void conditionSearch() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/order/conditionSearch")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }

    @Test
    @DisplayName("GET /store/order/details - 查询订单详情")
    void details() throws Exception {
        // 先查一单
        MvcResult pageResult = mockMvc.perform(get("/store/order/conditionSearch")
                        .param("page", "1").param("pageSize", "1")
                        .header("token", storeToken()))
                .andReturn();
        JsonNode records = parseResult(pageResult).path("data").path("records");
        if (records.isEmpty()) {
            return; // 无订单时跳过
        }
        Long id = records.get(0).path("id").asLong();

        MvcResult result = mockMvc.perform(get("/store/order/details")
                        .param("id", String.valueOf(id))
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }
}
