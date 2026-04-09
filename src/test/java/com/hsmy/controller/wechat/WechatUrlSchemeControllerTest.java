package com.hsmy.controller.wechat;

import com.hsmy.config.WechatMiniProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class WechatUrlSchemeControllerTest {

    @Mock
    private WechatMiniProperties properties;

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private WechatUrlSchemeController controller;

    @BeforeEach
    void setUp() {
        when(properties.getAppId()).thenReturn("wx-test-app");
        when(properties.getSecret()).thenReturn("wx-test-secret");

        controller = new WechatUrlSchemeController(properties);
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        ReflectionTestUtils.setField(controller, "restTemplate", restTemplate);
    }

    @Test
    void getAccessToken_usesStableTokenEndpoint() throws Exception {
        server.expect(once(), requestTo("https://api.weixin.qq.com/cgi-bin/stable_token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"grant_type\":\"client_credential\",\"appid\":\"wx-test-app\",\"secret\":\"wx-test-secret\",\"force_refresh\":false}"))
                .andRespond(withSuccess("{\"access_token\":\"stable-token\"}", MediaType.APPLICATION_JSON));

        String token = ReflectionTestUtils.invokeMethod(controller, "getAccessToken");

        assertEquals("stable-token", token);
        server.verify();
    }
}
