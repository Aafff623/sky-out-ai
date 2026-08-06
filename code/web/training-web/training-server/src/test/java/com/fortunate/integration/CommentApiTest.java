package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端评论模块接口测试
 */
@DisplayName("商家端 - 评论管理")
class CommentApiTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /store/comment/page - 分页查询评论")
    void page() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/comment/page")
                        .param("page", "1").param("pageSize", "10")
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
    }

    @Test
    @DisplayName("GET /store/comment/{id} - 查询评论详情")
    void getById() throws Exception {
        // 先查一页拿 id
        MvcResult pageResult = mockMvc.perform(get("/store/comment/page")
                        .param("page", "1").param("pageSize", "1")
                        .header("token", storeToken()))
                .andReturn();
        JsonNode records = parseResult(pageResult).path("data").path("records");
        if (records.isEmpty()) {
            return; // 无评论数据时跳过
        }
        String id = records.get(0).path("id").asText();

        MvcResult result = mockMvc.perform(get("/store/comment/" + id)
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }
}
