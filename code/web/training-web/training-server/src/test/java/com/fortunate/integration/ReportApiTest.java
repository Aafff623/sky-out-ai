package com.fortunate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortunate.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端报表模块接口测试
 */
@DisplayName("商家端 - 运营报表")
class ReportApiTest extends BaseIntegrationTest {

    private static final String BEGIN = "2026-08-01";
    private static final String END = "2026-08-06";

    @Test
    @DisplayName("GET /store/report/turnoverStatistics - 营业额统计")
    void turnoverStatistics() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/report/turnoverStatistics")
                        .param("begin", BEGIN).param("end", END)
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = parseResult(result);
        assertSuccess(node);
        org.junit.jupiter.api.Assertions.assertTrue(node.path("data").has("dateList"));
    }

    @Test
    @DisplayName("GET /store/report/userStatistics - 用户统计")
    void userStatistics() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/report/userStatistics")
                        .param("begin", BEGIN).param("end", END)
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }

    @Test
    @DisplayName("GET /store/report/ordersStatistics - 订单统计")
    void ordersStatistics() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/report/ordersStatistics")
                        .param("begin", BEGIN).param("end", END)
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }

    @Test
    @DisplayName("GET /store/report/top10 - 销量 TOP10")
    void top10() throws Exception {
        MvcResult result = mockMvc.perform(get("/store/report/top10")
                        .param("begin", BEGIN).param("end", END)
                        .header("token", storeToken()))
                .andExpect(status().isOk())
                .andReturn();
        assertSuccess(parseResult(result));
    }
}
