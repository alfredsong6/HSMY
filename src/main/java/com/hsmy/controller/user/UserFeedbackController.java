package com.hsmy.controller.user;

import com.hsmy.common.Result;
import com.hsmy.dto.UserFeedbackRequest;
import com.hsmy.service.UserFeedbackService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户反馈接口
 */
@Slf4j
@RestController
@RequestMapping("/user/feedback")
@RequiredArgsConstructor
public class UserFeedbackController {

    private final UserFeedbackService userFeedbackService;

    /**
     * 提交反馈（当前只收集content、contact、feedback_source）
     */
    @PostMapping
    public Result<Long> submitFeedback(@Validated @RequestBody UserFeedbackRequest request) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            Long feedbackId = userFeedbackService.submitFeedback(userId, request);
            return Result.success("反馈提交成功", feedbackId);
        } catch (Exception e) {
            log.error("提交反馈失败", e);
            return Result.error(e.getMessage());
        }
    }
}
