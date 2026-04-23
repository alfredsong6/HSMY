package com.hsmy.service.impl;

import com.hsmy.entity.Scripture;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.mapper.ScriptureMapper;
import com.hsmy.mapper.UserScripturePurchaseMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.ScriptureSectionService;
import com.hsmy.service.UserScriptureProgressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserScripturePurchaseServiceImplTest {

    private static final Long USER_ID = 100L;
    private static final Long SCRIPTURE_ID = 200L;

    @Mock
    private UserScripturePurchaseMapper userScripturePurchaseMapper;
    @Mock
    private ScriptureMapper scriptureMapper;
    @Mock
    private UserStatsMapper userStatsMapper;
    @Mock
    private MeritCoinTransactionMapper meritCoinTransactionMapper;
    @Mock
    private UserScriptureProgressService userScriptureProgressService;
    @Mock
    private ScriptureSectionService scriptureSectionService;

    @InjectMocks
    private UserScripturePurchaseServiceImpl service;

    @Test
    void ensureFreePermanentPurchase_usesPermanentPriceInsteadOfLeasePrice() {
        Scripture scripture = new Scripture();
        scripture.setId(SCRIPTURE_ID);
        scripture.setStatus(1);
        scripture.setPrice(9);
        scripture.setPermanentPrice(0);

        when(userScripturePurchaseMapper.selectByUserAndScripture(USER_ID, SCRIPTURE_ID)).thenReturn(null);

        UserScripturePurchase purchase = service.ensureFreePermanentPurchase(USER_ID, scripture);

        assertNotNull(purchase);
        assertEquals("permanent", purchase.getPurchaseType());
        assertEquals(Integer.valueOf(0), purchase.getMeritCoinsPaid());

        ArgumentCaptor<UserScripturePurchase> captor = ArgumentCaptor.forClass(UserScripturePurchase.class);
        verify(userScripturePurchaseMapper).insert(captor.capture());
        assertEquals("permanent", captor.getValue().getPurchaseType());
        assertEquals(Integer.valueOf(0), captor.getValue().getMeritCoinsPaid());
    }
}
