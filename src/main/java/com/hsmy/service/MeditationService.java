package com.hsmy.service;

import com.hsmy.vo.meditation.*;

import java.util.List;

public interface MeditationService {

    MeditationSubscriptionStatusVO purchaseSubscription(Long userId, MeditationSubscriptionPurchaseVO purchaseVO);

    MeditationSubscriptionStatusVO getSubscriptionStatus(Long userId);

    MeditationSessionStartResponse startSession(Long userId, MeditationSessionStartVO startVO);

    MeditationSessionFinishResponse finishSession(Long userId, MeditationSessionFinishVO finishVO);

    void addReflection(Long userId, MeditationSessionReflectionVO reflectionVO);

    void updateShare(Long userId, MeditationSessionShareVO shareVO);

    void pingSession(Long userId, MeditationSessionPingVO pingVO);

    Integer settleAbnormalSessions(Long userId);

    void discardSession(Long userId, MeditationSessionDiscardVO discardVO);

    MeditationStatsSummaryVO getStatsSummary(Long userId);

    List<MeditationMonthViewVO> getMonthStats(Long userId, String month);

    MeditationUserPrefVO getUserPreference(Long userId);

    MeditationUserPrefVO updateUserPreference(Long userId, MeditationUserPrefVO prefVO);
}
