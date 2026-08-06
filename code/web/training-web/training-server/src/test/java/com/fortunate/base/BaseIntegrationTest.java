package com.fortunate.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunate.constant.JwtClaimsConstant;
import com.fortunate.properties.JwtProperties;
import com.fortunate.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 集成测试基类：起真实 Spring 上下文 + 真实中间件（local profile）。
 * 提供商家端 / 用户端 token 获取与通用请求方法。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtProperties jwtProperties;

    /** Mock 掉 WebSocket ServerEndpointExporter，避免 MockMvc 无 Servlet 容器导致初始化失败 */
    @MockBean
    private ServerEndpointExporter serverEndpointExporter;

    protected String storeToken;
    protected String clientToken;

    /** 获取商家端 token（admin/123456） */
    protected String storeToken() throws Exception {
        if (storeToken == null) {
            MvcResult result = mockMvc.perform(post("/store/employee/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                    .andExpect(status().isOk())
                    .andReturn();
            JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
            storeToken = node.path("data").path("token").asText();
        }
        return storeToken;
    }

    /** 获取用户端 token：用 JwtUtil 生成真实 JWT（userId=1） */
    protected String clientToken() {
        if (clientToken == null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, 1L);
            clientToken = JwtUtil.createJWT(
                    jwtProperties.getClientSecretKey(),
                    jwtProperties.getClientTtl(),
                    claims);
        }
        return clientToken;
    }

    /** 解析 Result 响应 */
    protected JsonNode parseResult(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    /** 断言 Result.code == 200 */
    protected void assertSuccess(JsonNode node) {
        int code = node.path("code").asInt();
        org.junit.jupiter.api.Assertions.assertEquals(200, code,
                "Expected code=200 but got " + code + ", msg=" + node.path("msg").asText());
    }

    /** 断言 Result.code != 200（业务失败） */
    protected void assertError(JsonNode node) {
        int code = node.path("code").asInt();
        org.junit.jupiter.api.Assertions.assertNotEquals(200, code,
                "Expected error but got code=200");
    }
}
