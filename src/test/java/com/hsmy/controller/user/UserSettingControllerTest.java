package com.hsmy.controller.user;

import com.hsmy.common.Result;
import com.hsmy.entity.UserSetting;
import com.hsmy.entity.Scripture;
import com.hsmy.interceptor.LoginInterceptor;
import com.hsmy.service.ScriptureService;
import com.hsmy.service.UserSettingService;
import com.hsmy.service.impl.DailyWishPopupSettingService;
import com.hsmy.service.impl.ScriptureRechargeGateService;
import com.hsmy.vo.UserSettingVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSettingControllerTest {

    private static final Long USER_ID = 100L;
    private static final Long SCRIPTURE_ID = 200L;

    @Mock
    private UserSettingService userSettingService;
    @Mock
    private ScriptureService scriptureService;
    @Mock
    private DailyWishPopupSettingService dailyWishPopupSettingService;
    @Mock
    private ScriptureRechargeGateService scriptureRechargeGateService;

    @InjectMocks
    private UserSettingController controller;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LoginInterceptor.USER_ID_ATTRIBUTE, USER_ID);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getUserSettings_includesDailyWishPopupEnabledFromRedis() {
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(USER_ID);
        userSetting.setScriptureId(SCRIPTURE_ID);
        userSetting.setSoundEnabled(1);

        Scripture scripture = new Scripture();
        scripture.setId(SCRIPTURE_ID);
        scripture.setScriptureName("心经");

        when(userSettingService.getUserSettingByUserId(USER_ID)).thenReturn(userSetting);
        when(scriptureService.getScriptureById(SCRIPTURE_ID)).thenReturn(scripture);
        when(dailyWishPopupSettingService.isDailyWishPopupEnabled()).thenReturn(false);
        when(scriptureRechargeGateService.isGateEnabled()).thenReturn(true);

        Result<UserSettingVO> result = controller.getUserSettings(new MockHttpServletRequest());

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(Boolean.FALSE, result.getData().getDailyWishPopupEnabled());
        assertEquals(Boolean.TRUE, result.getData().getScriptureGate());
    }
}
