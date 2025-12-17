package com.hsmy.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hsmy.dto.UserFeedbackRequest;
import com.hsmy.entity.UserFeedback;
import com.hsmy.mapper.UserFeedbackMapper;
import com.hsmy.service.UserFeedbackService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户反馈Service实现
 */
@Service
@RequiredArgsConstructor
public class UserFeedbackServiceImpl implements UserFeedbackService {

    private static final String DEFAULT_FEEDBACK_TYPE = "OTHER";
    private static final String DEFAULT_TITLE = "用户反馈";

    private final UserFeedbackMapper userFeedbackMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitFeedback(Long userId, UserFeedbackRequest request) {
        UserFeedback feedback = new UserFeedback();
        feedback.setId(IdGenerator.nextId());
        feedback.setUserId(userId);
        feedback.setFeedbackType(DEFAULT_FEEDBACK_TYPE);
        String feedbackSource = StrUtil.blankToDefault(request.getFeedbackSource(), "APP");
        feedback.setFeedbackSource(feedbackSource);
        feedback.setTitle(buildTitle(request.getContent()));
        feedback.setContent(request.getContent());
        feedback.setContact(request.getContact());
        feedback.setIsContactable(1);

        userFeedbackMapper.insert(feedback);
        return feedback.getId();
    }

    private String buildTitle(String content) {
        if (StrUtil.isBlank(content)) {
            return DEFAULT_TITLE;
        }
        // 取内容前100字符作为标题
        return StrUtil.sub(content, 0, 100);
    }
}
