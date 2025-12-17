package com.hsmy.service;

import com.hsmy.dto.UserFeedbackRequest;

/**
 * 用户反馈Service
 */
public interface UserFeedbackService {

    /**
     * 提交用户反馈
     *
     * @param userId  用户ID
     * @param request 反馈请求
     * @return 反馈记录ID
     */
    Long submitFeedback(Long userId, UserFeedbackRequest request);
}
