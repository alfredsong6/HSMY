package com.hsmy.interceptor;

import com.hsmy.config.AuthWhiteListProperties;
import com.hsmy.service.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class LoginInterceptorTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private AuthWhiteListProperties authWhiteListProperties;

    @InjectMocks
    private LoginInterceptor loginInterceptor;

    @Test
    void preHandle_allowsAnonymousAccessToTotalRanking() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rankings/total");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = loginInterceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
        assertEquals(200, response.getStatus());
        verifyNoInteractions(sessionService);
    }
}
