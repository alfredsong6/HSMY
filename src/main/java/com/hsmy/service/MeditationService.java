package com.hsmy.service;

import com.hsmy.vo.meditation.*;

import java.util.List;

public interface MeditationService {

    MeditationSubscriptionStatusVO purchaseSubscription(Long userId, MeditationSubscriptionPurchaseVO purchaseVO);

    MeditationSubscriptionStatusVO getSubscriptionStatus(Long userId);

    MeditationSessionStartResponse startSession(Long userId, MeditationSessionStartVO startVO);

    MeditationSessionFinishResponse finishSession(Long userId, MeditationSessionFinishVO finishVO);

    void discardSession(Long userId, MeditationSessionDiscardVO discardVO);

    MeditationStatsSummaryVO getStatsSummary(Long userId);

    List<MeditationMonthViewVO> getMonthStats(Long userId, String month);

    MeditationUserPrefVO getUserPreference(Long userId);

    MeditationUserPrefVO updateUserPreference(Long userId, MeditationUserPrefVO prefVO);
}
