package com.hsmy.test.controller;

import com.hsmy.controller.auth.AuthController;
import com.hsmy.dto.SendCodeRequest;
import com.hsmy.dto.RegisterByCodeRequest;
import com.hsmy.dto.LoginRequestV2;
import com.hsmy.dto.LoginResponse;
import com.hsmy.service.UserService;
import com.hsmy.service.SessionService;
import com.hsmy.service.VerificationCodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 认证控制器单元测试
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    
    @MockBean
    private SessionService sessionService;
    
    @MockBean
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 初始化Mock行为
    }

    @Test
    void testSendCode_Success() throws Exception {
        // 准备测试数据
        SendCodeRequest request = new SendCodeRequest();
        request.setAccount("13800138888");
        request.setAccountType("phone");
        request.setBusinessType("register");

        // 模拟服务层返回
        when(verificationCodeService.sendCode(any(), any(), any(), any()))
            .thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("验证码已发送，请查收"));

        // 验证服务层方法被调用
        verify(verificationCodeService).sendCode(eq("13800138888"), eq("phone"), eq("register"), any());
    }

    @Test
    void testSendCode_InvalidPhone() throws Exception {
        SendCodeRequest request = new SendCodeRequest();
        request.setAccount("12345");
        request.setAccountType("phone");
        request.setBusinessType("register");

        mockMvc.perform(post("/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("手机号格式不正确"));

        // 验证服务层方法未被调用
        verify(verificationCodeService, never()).sendCode(any(), any(), any(), any());
    }

    @Test
    void testSendCode_SendFailed() throws Exception {
        SendCodeRequest request = new SendCodeRequest();
        request.setAccount("13800138888");
        request.setAccountType("phone");
        request.setBusinessType("register");

        // 模拟发送失败
        when(verificationCodeService.sendCode(any(), any(), any(), any()))
            .thenReturn(false);

        mockMvc.perform(post("/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("验证码发送失败，请稍后重试"));
    }

    @Test
    void testRegisterByCode_Success() throws Exception {
        RegisterByCodeRequest request = new RegisterByCodeRequest();
        request.setAccount("13800138888");
        request.setCode("123456");
        request.setNickname("测试用户");

        // 模拟验证码验证成功
        when(verificationCodeService.verifyCode("13800138888", "123456", "register"))
            .thenReturn(true);
        
        // 模拟注册成功
        when(userService.registerByCode(any())).thenReturn(1L);
        
        // 模拟获取用户信息
        User mockUser = createMockUser(1L);
        when(userService.getUserById(1L)).thenReturn(mockUser);
        
        // 模拟创建会话
        when(sessionService.createSession(any(), any())).thenReturn("sess_123456");

        mockMvc.perform(post("/auth/register-by-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.token").value("sess_123456"));
    }

    @Test
    void testLogin_PasswordSuccess() throws Exception {
        LoginRequestV2 request = new LoginRequestV2();
        request.setLoginAccount("test_user");
        request.setLoginType("password");
        request.setPassword("123456");

        User mockUser = createMockUser(1L);
        when(userService.getUserByLoginAccount("test_user")).thenReturn(mockUser);
        when(sessionService.createSession(any(), any())).thenReturn("sess_123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("sess_123456"));
    }

    @Test
    void testLogin_WrongPassword() throws Exception {
        LoginRequestV2 request = new LoginRequestV2();
        request.setLoginAccount("test_user");
        request.setLoginType("password");
        request.setPassword("wrong_password");

        User mockUser = createMockUser(1L);
        when(userService.getUserByLoginAccount("test_user")).thenReturn(mockUser);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer sess_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));

        verify(sessionService).removeSession("sess_123456");
    }

    private User createMockUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("test_user");
        user.setNickname("测试用户");
        user.setPhone("13800138888");
        user.setEmail("test@example.com");
        user.setPassword("e10adc3949ba59abbe56e057f20f883e"); // 123456的MD5
        user.setStatus(1);
        return user;
    }
}