package com.hsmy.service;

import com.hsmy.dto.MeritGainRequest;
import com.hsmy.dto.MeritGainResult;

/**
 * 统一处理功德增长与等级升级的服务。
 */
public interface MeritGainService {

    /**
     * 增加功德（可附带敲击数），并按照功德等级规则更新用户等级。
     *
     * @param request 增益请求
     * @return 处理结果
     */
    MeritGainResult gainMerit(MeritGainRequest request);

    /**
     * 带用户锁的功德增长入口，避免调用方重复写锁逻辑。
     *
     * @param request 增益请求
     * @return 处理结果
     */
    MeritGainResult gainMeritWithLock(MeritGainRequest request);
}
