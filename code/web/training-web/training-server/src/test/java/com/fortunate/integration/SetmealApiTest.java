package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端套餐模块接口测试
 */
@DisplayName("商家端 - 套餐管理")
class SetmealApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/setmeal/page - 分页查询套餐")
    void page() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/setmeal/page")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }

    @Test
    @DisplayName("GET /store/setmeal - 按 id 查询套餐详情")
    void getById() throws Exception {
        MvcResult pageResult = mockMvc.perform(get("/store/setmeal/page")
                        .param("page", "1").param("pageSize", "1")
                        .header("token", storeToken()))
                .andReturn();
        JsonNode records = parseResult(pageResult).path("data").path("records");
        if (records.isEmpty()) {
            return; // 无套餐数据时跳过
        }
        Long id = records.get(0).path("id").asLong();

        MvcResult result = mockMvc.perform(get("/store/setmeal")
                        .param("id", String.valueOf(id))
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }
}
