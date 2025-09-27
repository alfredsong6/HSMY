package com.hsmy.test.integration;

import com.hsmy.dto.SendCodeRequest;
import com.hsmy.dto.RegisterByCodeRequest;
import com.hsmy.dto.LoginRequestV2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证功能集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String authToken;

    @Test
    @Order(1)
    void testCompleteRegistrationFlow() throws Exception {
        // 1. 发送验证码
        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setAccount("13800138999");
        sendCodeRequest.setAccountType("phone");
        sendCodeRequest.setBusinessType("register");

        mockMvc.perform(post("/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 注册用户
        RegisterByCodeRequest registerRequest = new RegisterByCodeRequest();
        registerRequest.setAccount("13800138999");
        registerRequest.setCode("TEST_CODE"); // 测试环境使用固定验证码
        registerRequest.setNickname("集成测试用户");

        MvcResult registerResult = mockMvc.perform(post("/auth/register-by-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13800138999"))
                .andReturn();

        // 提取token
        String response = registerResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        authToken = jsonNode.get("data").get("token").asText();

        assertNotNull(authToken);
    }

    @Test
    @Order(2)
    void testLoginFlow() throws Exception {
        // 测试密码登录
        LoginRequestV2 loginRequest = new LoginRequestV2();
        loginRequest.setLoginAccount("13800138999");
        loginRequest.setLoginType("password");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    @Order(3)
    void testAuthenticatedAccess() throws Exception {
        // 使用token访问需要认证的接口
        mockMvc.perform(get("/user/self/info")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13800138999"));
    }

    @Test
    @Order(4)
    void testSessionManagement() throws Exception {
        // 获取用户会话列表
        mockMvc.perform(get("/auth/sessions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 踢出其他会话
        mockMvc.perform(post("/auth/kick-other-sessions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(5)
    void testLogout() throws Exception {
        // 登出
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));

        // 验证token已失效
        mockMvc.perform(get("/user/self/info")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testErrorHandling() throws Exception {
        // 测试无效验证码
        RegisterByCodeRequest invalidRequest = new RegisterByCodeRequest();
        invalidRequest.setAccount("13800138998");
        invalidRequest.setCode("INVALID_CODE");

        mockMvc.perform(post("/auth/register-by-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("验证码无效或已过期"));

        // 测试错误密码
        LoginRequestV2 wrongPasswordRequest = new LoginRequestV2();
        wrongPasswordRequest.setLoginAccount("test_user_1");
        wrongPasswordRequest.setLoginType("password");
        wrongPasswordRequest.setPassword("wrong_password");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }
}