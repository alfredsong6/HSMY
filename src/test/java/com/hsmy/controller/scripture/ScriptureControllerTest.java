package com.hsmy.controller.scripture;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.common.Result;
import com.hsmy.entity.Scripture;
import com.hsmy.interceptor.LoginInterceptor;
import com.hsmy.service.ScriptureBarrageProgressService;
import com.hsmy.service.ScriptureSectionService;
import com.hsmy.service.ScriptureService;
import com.hsmy.service.UserScripturePurchaseService;
import com.hsmy.service.impl.ScriptureRechargeGateService;
import com.hsmy.vo.ScriptureQueryVO;
import com.hsmy.vo.ScriptureVO;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptureControllerTest {

    private static final Long USER_ID = 100L;

    @Mock
    private ScriptureService scriptureService;
    @Mock
    private ScriptureSectionService scriptureSectionService;
    @Mock
    private UserScripturePurchaseService userScripturePurchaseService;
    @Mock
    private ScriptureBarrageProgressService scriptureBarrageProgressService;
    @Mock
    private ScriptureRechargeGateService scriptureRechargeGateService;

    @InjectMocks
    private ScriptureController controller;

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
    void getScriptureList_returnsReviewPage_whenRechargeGateBlocksUser() {
        ScriptureQueryVO queryVO = new ScriptureQueryVO();
        queryVO.setPageNum(2);
        queryVO.setPageSize(20);
        Scripture scripture = new Scripture();
        scripture.setId(200L);
        scripture.setScriptureName("审核典籍");
        scripture.setStatus(3);
        scripture.setPreviewSectionCount(1);
        when(scriptureRechargeGateService.canReturnScriptureLists(USER_ID)).thenReturn(false);
        when(scriptureService.getReviewScriptures()).thenReturn(Collections.singletonList(scripture));

        Result<Page<ScriptureVO>> result = controller.getScriptureList(queryVO, new MockHttpServletRequest());

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().getCurrent());
        assertEquals(20, result.getData().getSize());
        assertEquals(1, result.getData().getTotal());
        assertEquals(1, result.getData().getRecords().size());
        assertEquals("200", result.getData().getRecords().get(0).getId());
        assertEquals(Integer.valueOf(3), result.getData().getRecords().get(0).getStatus());
        verify(scriptureService, never()).pageScriptures(queryVO);
    }
}
