package com.hsmy.service.wechat.impl;

import com.hsmy.config.WechatMiniProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class WechatMiniAuthServiceImplTest {

    @Mock
    private WechatMiniProperties properties;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private WechatMiniAuthServiceImpl service;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();

        when(properties.getAppId()).thenReturn("wx-test-app");
        when(properties.getSecret()).thenReturn("wx-test-secret");
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(restTemplateBuilder.setConnectTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        service = new WechatMiniAuthServiceImpl(properties, stringRedisTemplate, restTemplateBuilder);
    }

    @Test
    void getAccessToken_usesStableTokenEndpointAndCachesPerApp() {
        when(valueOperations.get("hsmy:wechat:mini:access_token:wx-test-app")).thenReturn(null);

        server.expect(once(), requestTo("https://api.weixin.qq.com/cgi-bin/stable_token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"grant_type\":\"client_credential\",\"appid\":\"wx-test-app\",\"secret\":\"wx-test-secret\",\"force_refresh\":false}"))
                .andRespond(withSuccess("{\"access_token\":\"stable-token\",\"expires_in\":7200}", MediaType.APPLICATION_JSON));

        String token = service.getAccessToken();

        assertEquals("stable-token", token);
        verify(valueOperations).set("hsmy:wechat:mini:access_token:wx-test-app", "stable-token", 7000L, TimeUnit.SECONDS);
        server.verify();
    }
}
