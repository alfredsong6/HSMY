package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.dto.SmsResult;
import com.hsmy.entity.VerificationCode;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.VerificationCodeMapper;
import com.hsmy.service.EmailService;
import com.hsmy.service.SmsServiceFactory;
import com.hsmy.service.VerificationCodeService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {
    
    private final VerificationCodeMapper verificationCodeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final SmsServiceFactory smsServiceFactory;
    private final EmailService emailService;
    
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final int MIN_SEND_INTERVAL_SECONDS = 60;
    private static final int MAX_DAILY_SEND_COUNT = 10;
    
    @Override
    public boolean sendCode(String account, String accountType, String businessType, String ipAddress) {
        // 检查发送间隔
        LocalDateTime lastSendTime = verificationCodeMapper.getLastSendTime(account, businessType);
        if (lastSendTime != null) {
            long seconds = ChronoUnit.SECONDS.between(lastSendTime, LocalDateTime.now());
            if (seconds < MIN_SEND_INTERVAL_SECONDS) {
                log.warn("验证码发送过于频繁，account: {}", account);
                throw new BusinessException("验证码发送过于频繁，请" + (MIN_SEND_INTERVAL_SECONDS - seconds) + "秒后再试");
            }
        }
        
        // 检查今日发送次数
        Integer todayCount = verificationCodeMapper.getTodaySendCount(account);
        if (todayCount != null && todayCount >= MAX_DAILY_SEND_COUNT) {
            log.warn("今日验证码发送次数已达上限，account: {}", account);
            throw new BusinessException("今日验证码发送次数已达上限");
        }
        
        // 生成验证码
        String code = generateCode();
        
        // 保存到数据库
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setId(IdGenerator.nextId());
        verificationCode.setAccount(account);
        verificationCode.setAccountType(accountType);
        verificationCode.setCode(code);
        verificationCode.setBusinessType(businessType);
        verificationCode.setUsed(false);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        verificationCode.setIpAddress(ipAddress);
        
        verificationCodeMapper.insert(verificationCode);
        
        // 保存到Redis，用于快速验证
        String redisKey = getRedisKey(account, businessType);
        stringRedisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        // 实际发送验证码（这里应该调用短信或邮件服务）
        boolean sendResult = sendToAccount(account, accountType, code);
        
        if (sendResult) {
            log.info("验证码发送成功，account: {}, code: {}", account, code);
        } else {
            log.error("验证码发送失败，account: {}", account);
            // 发送失败时删除记录
            verificationCodeMapper.deleteById(verificationCode.getId());
            stringRedisTemplate.delete(redisKey);
        }
        
        return sendResult;
    }
    
    @Override
    public boolean verifyCode(String account, String code, String businessType) {
        // 先从Redis验证
        String redisKey = getRedisKey(account, businessType);
        String cachedCode = stringRedisTemplate.opsForValue().get(redisKey);
        
        if (cachedCode != null && cachedCode.equals(code)) {
            return true;
        }
        
        // Redis中没有，从数据库查询
        LambdaQueryWrapper<VerificationCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VerificationCode::getAccount, account)
               .eq(VerificationCode::getCode, code)
               .eq(VerificationCode::getBusinessType, businessType)
               .eq(VerificationCode::getUsed, false)
               .gt(VerificationCode::getExpireTime, LocalDateTime.now())
               .orderByDesc(VerificationCode::getCreateTime)
               .last("LIMIT 1");
        
        VerificationCode verificationCode = verificationCodeMapper.selectOne(wrapper);
        return verificationCode != null;
    }
    
    @Override
    @Transactional
    public boolean verify(String account, String accountType, String code, String businessType) {
        // 验证验证码
        boolean isValid = verifyCode(account, code, businessType);
        
        if (isValid) {
            // 验证成功后标记为已使用
            markCodeAsUsed(account, code, businessType);
            log.info("验证码验证成功并已标记为已使用，account: {}, businessType: {}", account, businessType);
        } else {
            log.warn("验证码验证失败，account: {}, code: {}, businessType: {}", account, code, businessType);
        }
        
        return isValid;
    }
    
    @Override
    @Transactional
    public void markCodeAsUsed(String account, String code, String businessType) {
        // 标记数据库中的验证码为已使用
        LambdaQueryWrapper<VerificationCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VerificationCode::getAccount, account)
               .eq(VerificationCode::getCode, code)
               .eq(VerificationCode::getBusinessType, businessType)
               .eq(VerificationCode::getUsed, false);
        
        VerificationCode verificationCode = verificationCodeMapper.selectOne(wrapper);
        if (verificationCode != null) {
            verificationCode.setUsed(true);
            verificationCode.setUseTime(LocalDateTime.now());
            verificationCodeMapper.updateById(verificationCode);
        }
        
        // 从Redis中删除
        String redisKey = getRedisKey(account, businessType);
        stringRedisTemplate.delete(redisKey);
    }
    
    /**
     * 生成验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 实际发送验证码到手机或邮箱
     */
    private boolean sendToAccount(String account, String accountType, String code) {
        try {
            SmsResult result;
            
            if ("phone".equals(accountType)) {
                // 发送短信验证码
                result = smsServiceFactory.getSmsService().sendVerificationCode(account, code);
                log.info("短信验证码发送结果，手机号: {}, 成功: {}, 消息: {}", account, result.isSuccess(), result.getErrorMessage());
            } else if ("email".equals(accountType)) {
                // 发送邮件验证码
                result = emailService.sendVerificationCode(account, code);
                log.info("邮件验证码发送结果，邮箱: {}, 成功: {}, 消息: {}", account, result.isSuccess(), result.getErrorMessage());
            } else {
                log.error("不支持的账号类型: {}", accountType);
                return false;
            }
            
            return result.isSuccess();
            
        } catch (Exception e) {
            log.error("发送验证码异常，账号: {}, 类型: {}", account, accountType, e);
            // 如果短信服务未配置或出错，降级到日志输出（开发环境）
            log.info("【降级处理】验证码发送到{}({}): {}", account, accountType, code);
            return true; // 开发环境下返回true，生产环境可以根据需要调整
        }
    }
    
    /**
     * 获取Redis键
     */
    private String getRedisKey(String account, String businessType) {
        return "verification:code:" + businessType + ":" + account;
    }
}