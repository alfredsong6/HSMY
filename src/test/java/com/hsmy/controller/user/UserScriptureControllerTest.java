package com.hsmy.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.common.Result;
import com.hsmy.dto.SaveSectionProgressRequest;
import com.hsmy.entity.Scripture;
import com.hsmy.entity.ScriptureSection;
import com.hsmy.entity.UserScriptureProgress;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.interceptor.LoginInterceptor;
import com.hsmy.service.ScriptureSectionService;
import com.hsmy.service.ScriptureService;
import com.hsmy.service.UserScriptureProgressService;
import com.hsmy.service.UserScripturePurchaseService;
import com.hsmy.service.impl.ScriptureRechargeGateService;
import com.hsmy.vo.LatestScriptureProgressVO;
import com.hsmy.vo.StartReadingStatusVO;
import com.hsmy.vo.UserScripturePurchaseVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserScriptureControllerTest {

    private static final Long USER_ID = 100L;
    private static final Long SCRIPTURE_ID = 200L;
    private static final Long OLD_SECTION_ID = 301L;
    private static final Long LATEST_SECTION_ID = 302L;

    @Mock
    private UserScripturePurchaseService userScripturePurchaseService;
    @Mock
    private ScriptureService scriptureService;
    @Mock
    private ScriptureSectionService scriptureSectionService;
    @Mock
    private UserScriptureProgressService userScriptureProgressService;
    @Mock
    private ScriptureRechargeGateService scriptureRechargeGateService;

    @InjectMocks
    private UserScriptureController controller;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LoginInterceptor.USER_ID_ATTRIBUTE, USER_ID);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        lenient().when(scriptureRechargeGateService.canReturnScriptureLists(USER_ID)).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getLatestProgress_returnsLatestSectionProgress_whenPurchaseSnapshotIsStale() {
        Scripture scripture = buildScripture();
        UserScripturePurchase purchase = buildPurchase();
        ScriptureSection latestSection = buildSection(LATEST_SECTION_ID, 3, "第三卷");
        UserScriptureProgress latestProgress = buildProgress(LATEST_SECTION_ID, 7613, 0, 0D, new Date());

        when(scriptureService.getScriptureById(SCRIPTURE_ID)).thenReturn(scripture);
        when(userScripturePurchaseService.getUserPurchaseDetail(USER_ID, SCRIPTURE_ID)).thenReturn(purchase);
        when(scriptureSectionService.getById(LATEST_SECTION_ID)).thenReturn(latestSection);
        when(userScriptureProgressService.getLatestByUserAndScripture(USER_ID, SCRIPTURE_ID)).thenReturn(latestProgress);

        Result<LatestScriptureProgressVO> result = controller.getLatestProgress(String.valueOf(SCRIPTURE_ID));

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(String.valueOf(LATEST_SECTION_ID), result.getData().getCurrentSection().getSectionId());
        assertEquals(Integer.valueOf(7613), result.getData().getLastPosition());
        assertEquals(latestProgress.getLastReadTime(), result.getData().getLastReadTime());
    }

    @Test
    void startReading_returnsLatestSectionAndPosition() {
        Scripture scripture = buildScripture();
        scripture.setPrice(9);
        UserScripturePurchase purchase = buildPurchase();
        UserScriptureProgress latestProgress = buildProgress(LATEST_SECTION_ID, 7613, 0, 0D, new Date());
        ScriptureSection latestSection = buildSection(LATEST_SECTION_ID, 3, "第三卷");

        when(scriptureService.getScriptureById(SCRIPTURE_ID)).thenReturn(scripture);
        when(userScripturePurchaseService.getUserPurchaseDetail(USER_ID, SCRIPTURE_ID)).thenReturn(purchase);
        when(userScripturePurchaseService.isUserPurchaseValid(USER_ID, SCRIPTURE_ID)).thenReturn(true);
        when(userScriptureProgressService.getLatestByUserAndScripture(USER_ID, SCRIPTURE_ID)).thenReturn(latestProgress);
        when(scriptureSectionService.getById(LATEST_SECTION_ID)).thenReturn(latestSection);

        Result<StartReadingStatusVO> result = controller.startReading(String.valueOf(SCRIPTURE_ID));

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("valid", result.getData().getStatus());
        assertEquals(String.valueOf(LATEST_SECTION_ID), result.getData().getLastSectionId());
        assertEquals(Integer.valueOf(7613), result.getData().getLastPosition());
    }

    @Test
    void startReading_createsTrial_whenFreePermanentScriptureHasNotBeenPurchased() {
        Scripture scripture = buildScripture();
        scripture.setPrice(0);
        scripture.setPermanentPrice(0);
        UserScripturePurchase trialPurchase = new UserScripturePurchase();
        trialPurchase.setPurchaseType("trial");
        trialPurchase.setCompletedSections(0);

        when(scriptureService.getScriptureById(SCRIPTURE_ID)).thenReturn(scripture);
        when(userScripturePurchaseService.getUserPurchaseDetail(USER_ID, SCRIPTURE_ID)).thenReturn(null);
        when(userScripturePurchaseService.ensureTrialPurchase(USER_ID, SCRIPTURE_ID)).thenReturn(trialPurchase);
        when(userScripturePurchaseService.isUserPurchaseValid(USER_ID, SCRIPTURE_ID)).thenReturn(false);

        Result<StartReadingStatusVO> result = controller.startReading(String.valueOf(SCRIPTURE_ID));

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("trial", result.getData().getPurchaseType());
        verify(userScripturePurchaseService).ensureTrialPurchase(USER_ID, SCRIPTURE_ID);
        verify(userScripturePurchaseService, never()).ensureFreePermanentPurchase(USER_ID, scripture);
    }

    @Test
    void getAvailableScriptures_doesNotMarkFreePermanentScriptureAsPurchasedWithoutRecord() {
        Scripture scripture = buildScripture();
        scripture.setPermanentPrice(0);

        when(scriptureService.getUsableScriptures(USER_ID)).thenReturn(Collections.singletonList(scripture));
        when(userScripturePurchaseService.getValidPurchasesByUserId(USER_ID)).thenReturn(Collections.emptyList());

        Result<List<com.hsmy.vo.ScriptureVO>> result = controller.getAvailableScriptures();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals(Boolean.FALSE, result.getData().get(0).getIsPurchased());
        assertEquals(Boolean.FALSE, result.getData().get(0).getIsPurchaseValid());
        assertEquals(null, result.getData().get(0).getPurchaseType());
    }

    @Test
    void getAvailableScriptures_returnsReviewList_whenRechargeGateBlocksUser() {
        Scripture scripture = buildScripture();
        scripture.setStatus(3);
        when(scriptureRechargeGateService.canReturnScriptureLists(USER_ID)).thenReturn(false);
        when(scriptureService.getReviewScriptures()).thenReturn(Collections.singletonList(scripture));

        Result<List<com.hsmy.vo.ScriptureVO>> result = controller.getAvailableScriptures();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals(Integer.valueOf(3), result.getData().get(0).getStatus());
        assertEquals(Boolean.FALSE, result.getData().get(0).getIsPurchased());
        verify(scriptureService, never()).getUsableScriptures(USER_ID);
        verify(userScripturePurchaseService, never()).getValidPurchasesByUserId(USER_ID);
    }

    @Test
    void getUserPurchases_returnsReviewPage_whenRechargeGateBlocksUser() {
        Scripture scripture = buildScripture();
        scripture.setStatus(3);
        when(scriptureRechargeGateService.canReturnScriptureLists(USER_ID)).thenReturn(false);
        when(scriptureService.getReviewScriptures()).thenReturn(Collections.singletonList(scripture));

        Result<Page<UserScripturePurchaseVO>> result = controller.getUserPurchases(1, 10, new MockHttpServletRequest());

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getCurrent());
        assertEquals(10, result.getData().getSize());
        assertEquals(1, result.getData().getTotal());
        assertEquals(1, result.getData().getRecords().size());
        assertEquals("200", result.getData().getRecords().get(0).getScriptureId());
        assertEquals(Integer.valueOf(3), result.getData().getRecords().get(0).getStatus());
        verify(userScripturePurchaseService, never()).getPurchasesByUserId(USER_ID, 1, 10);
    }

    @Test
    void saveSectionProgress_includesCurrentSectionPartialProgressInTotalProgress() {
        Scripture scripture = buildScripture();
        ScriptureSection section = buildSection(LATEST_SECTION_ID, 3, "第三卷");
        UserScriptureProgress existingProgress = buildProgress(LATEST_SECTION_ID, 3200, 0, 20D, new Date());
        SaveSectionProgressRequest request = new SaveSectionProgressRequest();
        request.setLastPosition(7613);
        request.setSectionReadingProgress(40D);
        request.setIsCompleted(0);
        request.setSpendSeconds(30);

        when(scriptureService.getScriptureById(SCRIPTURE_ID)).thenReturn(scripture);
        when(scriptureSectionService.getById(LATEST_SECTION_ID)).thenReturn(section);
        when(userScripturePurchaseService.isUserPurchaseValid(USER_ID, SCRIPTURE_ID)).thenReturn(true);
        when(userScriptureProgressService.getByUserAndSection(USER_ID, LATEST_SECTION_ID)).thenReturn(existingProgress);
        when(userScriptureProgressService.sumSectionReadingProgress(USER_ID, SCRIPTURE_ID)).thenReturn(220D);
        when(userScripturePurchaseService.updateSectionProgress(USER_ID, SCRIPTURE_ID, LATEST_SECTION_ID,
                7613, 40D, 24D, 30, false)).thenReturn(true);

        Result<Void> result = controller.saveSectionProgress(
                String.valueOf(SCRIPTURE_ID),
                String.valueOf(LATEST_SECTION_ID),
                request
        );

        assertEquals(200, result.getCode());

        ArgumentCaptor<Double> totalProgressCaptor = ArgumentCaptor.forClass(Double.class);
        verify(userScripturePurchaseService).updateSectionProgress(
                eq(USER_ID),
                eq(SCRIPTURE_ID),
                eq(LATEST_SECTION_ID),
                eq(7613),
                eq(40D),
                totalProgressCaptor.capture(),
                eq(30),
                eq(false)
        );
        assertEquals(24D, totalProgressCaptor.getValue());
    }

    private Scripture buildScripture() {
        Scripture scripture = new Scripture();
        scripture.setId(SCRIPTURE_ID);
        scripture.setScriptureName("楞严经");
        scripture.setSectionCount(10);
        scripture.setPreviewSectionCount(2);
        scripture.setPrice(0);
        return scripture;
    }

    private UserScripturePurchase buildPurchase() {
        UserScripturePurchase purchase = new UserScripturePurchase();
        purchase.setScriptureId(SCRIPTURE_ID);
        purchase.setPurchaseType("permanent");
        purchase.setLastSectionId(OLD_SECTION_ID);
        purchase.setCompletedSections(2);
        purchase.setReadingProgress(new BigDecimal("20.5"));
        purchase.setStatus(1);
        return purchase;
    }

    private ScriptureSection buildSection(Long id, Integer sectionNo, String title) {
        ScriptureSection section = new ScriptureSection();
        section.setId(id);
        section.setScriptureId(SCRIPTURE_ID);
        section.setSectionNo(sectionNo);
        section.setTitle(title);
        return section;
    }

    private UserScriptureProgress buildProgress(Long sectionId,
                                                Integer lastPosition,
                                                Integer isCompleted,
                                                Double readingProgress,
                                                Date lastReadTime) {
        UserScriptureProgress progress = new UserScriptureProgress();
        progress.setSectionId(sectionId);
        progress.setLastPosition(lastPosition);
        progress.setIsCompleted(isCompleted);
        progress.setReadingProgress(BigDecimal.valueOf(readingProgress));
        progress.setLastReadTime(lastReadTime);
        return progress;
    }
}
