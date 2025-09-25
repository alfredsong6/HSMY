package com.hsmy.controller.user;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.PurchaseScriptureRequest;
import com.hsmy.dto.UpdateLastReadingPositionRequest;
import com.hsmy.dto.UpdateReadingProgressRequest;
import com.hsmy.entity.Scripture;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.service.ScriptureService;
import com.hsmy.service.UserScripturePurchaseService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.ScriptureReadingVO;
import com.hsmy.vo.UserPurchaseStatsVO;
import com.hsmy.vo.UserScripturePurchaseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户典籍购买Controller
 *
 * @author HSMY
 * @date 2025/09/25
 */
@RestController
@RequestMapping("/user/scripture")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class UserScriptureController {

    private final UserScripturePurchaseService userScripturePurchaseService;
    private final ScriptureService scriptureService;

    /**
     * 购买典籍
     *
     * @param request 购买请求
     * @param httpRequest HTTP请求
     * @return 购买结果
     */
    @PostMapping("/purchase")
    public Result<Void> purchaseScripture(@RequestBody @Valid PurchaseScriptureRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            // 检查典籍是否存在且可购买
            if (!scriptureService.checkScriptureAvailable(request.getScriptureId())) {
                return Result.error("该典籍不存在或已下架");
            }

            // 执行购买
            Boolean success = userScripturePurchaseService.purchaseScripture(
                    userId, request.getScriptureId(), request.getPurchaseMonths());

            if (success) {
                return Result.success();
            } else {
                return Result.error("购买失败，请检查您的福币余额或该典籍是否已购买");
            }
        } catch (Exception e) {
            return Result.error("购买失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户购买的典籍列表
     *
     * @param httpRequest HTTP请求
     * @return 购买记录列表
     */
    @GetMapping("/purchases")
    public Result<List<UserScripturePurchaseVO>> getUserPurchases(HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            List<UserScripturePurchase> purchases = userScripturePurchaseService.getPurchasesByUserId(userId);

            List<UserScripturePurchaseVO> purchaseVOs = purchases.stream().map(purchase -> {
                UserScripturePurchaseVO vo = new UserScripturePurchaseVO();
                BeanUtils.copyProperties(purchase, vo);

                // 获取典籍信息
                Scripture scripture = scriptureService.getScriptureById(purchase.getScriptureId());
                if (scripture != null) {
                    vo.setScriptureName(scripture.getScriptureName());
                    vo.setScriptureType(scripture.getScriptureType());
                    vo.setCoverUrl(scripture.getCoverUrl());
                }

                // 计算剩余天数
                if (purchase.getExpireTime() != null && purchase.getIsExpired() == 0) {
                    long remainingDays = ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            purchase.getExpireTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    );
                    vo.setRemainingDays(remainingDays);
                    vo.setIsExpiringSoon(remainingDays <= 7 && remainingDays > 0);
                } else {
                    vo.setRemainingDays(0L);
                    vo.setIsExpiringSoon(false);
                }

                return vo;
            }).collect(Collectors.toList());

            return Result.success(purchaseVOs);
        } catch (Exception e) {
            return Result.error("获取购买记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户有效的典籍购买记录
     *
     * @param httpRequest HTTP请求
     * @return 有效购买记录列表
     */
    @GetMapping("/valid-purchases")
    public Result<List<UserScripturePurchaseVO>> getValidPurchases(HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            List<UserScripturePurchase> purchases = userScripturePurchaseService.getValidPurchasesByUserId(userId);

            List<UserScripturePurchaseVO> purchaseVOs = purchases.stream().map(purchase -> {
                UserScripturePurchaseVO vo = new UserScripturePurchaseVO();
                BeanUtils.copyProperties(purchase, vo);

                // 获取典籍信息
                Scripture scripture = scriptureService.getScriptureById(purchase.getScriptureId());
                if (scripture != null) {
                    vo.setScriptureName(scripture.getScriptureName());
                    vo.setScriptureType(scripture.getScriptureType());
                    vo.setCoverUrl(scripture.getCoverUrl());
                }

                // 计算剩余天数
                if (purchase.getExpireTime() != null) {
                    long remainingDays = ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            purchase.getExpireTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    );
                    vo.setRemainingDays(remainingDays);
                    vo.setIsExpiringSoon(remainingDays <= 7 && remainingDays > 0);
                }

                return vo;
            }).collect(Collectors.toList());

            return Result.success(purchaseVOs);
        } catch (Exception e) {
            return Result.error("获取有效购买记录失败：" + e.getMessage());
        }
    }

    /**
     * 更新阅读进度
     *
     * @param request 更新请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("/reading-progress")
    public Result<Void> updateReadingProgress(@RequestBody @Valid UpdateReadingProgressRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            // 检查用户是否有权限阅读该典籍
            if (!userScripturePurchaseService.isUserPurchaseValid(userId, request.getScriptureId())) {
                return Result.error("您尚未购买该典籍或购买已过期");
            }

            Boolean success = userScripturePurchaseService.updateReadingProgress(
                    userId, request.getScriptureId(), request.getReadingProgress());

            if (success) {
                return Result.success();
            } else {
                return Result.error("更新阅读进度失败");
            }
        } catch (Exception e) {
            return Result.error("更新阅读进度失败：" + e.getMessage());
        }
    }

    /**
     * 更新最后阅读位置
     *
     * @param request 更新请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("/last-reading-position")
    public Result<Void> updateLastReadingPosition(@RequestBody @Valid UpdateLastReadingPositionRequest request,
                                                HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            // 检查用户是否有权限阅读该典籍
            if (!userScripturePurchaseService.isUserPurchaseValid(userId, request.getScriptureId())) {
                return Result.error("您尚未购买该典籍或购买已过期");
            }

            Boolean success = userScripturePurchaseService.updateLastReadingPosition(
                    userId, request.getScriptureId(), request.getLastReadingPosition());

            if (success) {
                return Result.success();
            } else {
                return Result.error("更新阅读位置失败");
            }
        } catch (Exception e) {
            return Result.error("更新阅读位置失败：" + e.getMessage());
        }
    }

    /**
     * 获取典籍购买详情
     *
     * @param scriptureId 典籍ID
     * @param httpRequest HTTP请求
     * @return 购买详情
     */
    @GetMapping("/purchase/{scriptureId}")
    public Result<UserScripturePurchaseVO> getPurchaseDetail(@PathVariable Long scriptureId,
                                                           HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            UserScripturePurchase purchase = userScripturePurchaseService.getUserPurchaseDetail(userId, scriptureId);

            if (purchase == null) {
                return Result.error("您尚未购买该典籍");
            }

            UserScripturePurchaseVO vo = new UserScripturePurchaseVO();
            BeanUtils.copyProperties(purchase, vo);

            // 获取典籍信息
            Scripture scripture = scriptureService.getScriptureById(scriptureId);
            if (scripture != null) {
                vo.setScriptureName(scripture.getScriptureName());
                vo.setScriptureType(scripture.getScriptureType());
                vo.setCoverUrl(scripture.getCoverUrl());
            }

            // 计算剩余天数
            if (purchase.getExpireTime() != null && purchase.getIsExpired() == 0) {
                long remainingDays = ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        purchase.getExpireTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                );
                vo.setRemainingDays(remainingDays);
                vo.setIsExpiringSoon(remainingDays <= 7 && remainingDays > 0);
            }

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("获取购买详情失败：" + e.getMessage());
        }
    }

    /**
     * 续费典籍
     *
     * @param scriptureId 典籍ID
     * @param extendMonths 续费月数
     * @param httpRequest HTTP请求
     * @return 续费结果
     */
    @PostMapping("/renew/{scriptureId}")
    public Result<Void> renewScripture(@PathVariable Long scriptureId,
                                     @RequestParam Integer extendMonths,
                                     HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            if (extendMonths == null || extendMonths < 1 || extendMonths > 12) {
                return Result.error("续费月数必须在1-12月之间");
            }

            Boolean success = userScripturePurchaseService.renewScripture(userId, scriptureId, extendMonths);

            if (success) {
                return Result.success();
            } else {
                return Result.error("续费失败，请检查您的福币余额");
            }
        } catch (Exception e) {
            return Result.error("续费失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否已购买指定典籍
     *
     * @param scriptureId 典籍ID
     * @param httpRequest HTTP请求
     * @return 购买状态
     */
    @GetMapping("/check-purchased/{scriptureId}")
    public Result<Boolean> checkUserPurchased(@PathVariable Long scriptureId,
                                            HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            Boolean isPurchased = userScripturePurchaseService.hasUserPurchased(userId, scriptureId);
            return Result.success(isPurchased);
        } catch (Exception e) {
            return Result.error("检查购买状态失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户典籍购买是否有效
     *
     * @param scriptureId 典籍ID
     * @param httpRequest HTTP请求
     * @return 有效状态
     */
    @GetMapping("/check-valid/{scriptureId}")
    public Result<Boolean> checkPurchaseValid(@PathVariable Long scriptureId,
                                            HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            Boolean isValid = userScripturePurchaseService.isUserPurchaseValid(userId, scriptureId);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.error("检查购买有效性失败：" + e.getMessage());
        }
    }

    /**
     * 继续阅读典籍（获取典籍内容和用户阅读进度）
     *
     * @param scriptureId 典籍ID
     * @param httpRequest HTTP请求
     * @return 典籍阅读内容和进度信息
     */
    @GetMapping("/read/{scriptureId}")
    public Result<ScriptureReadingVO> continueReading(@PathVariable Long scriptureId,
                                                    HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            // 检查用户是否有权限阅读该典籍
            if (!userScripturePurchaseService.isUserPurchaseValid(userId, scriptureId)) {
                return Result.error("您尚未购买该典籍或购买已过期，无法阅读");
            }

            // 获取典籍信息
            Scripture scripture = scriptureService.getScriptureById(scriptureId);
            if (scripture == null) {
                return Result.error("典籍不存在");
            }

            // 获取用户购买记录
            UserScripturePurchase purchase = userScripturePurchaseService.getUserPurchaseDetail(userId, scriptureId);
            if (purchase == null) {
                return Result.error("购买记录不存在");
            }

            // 构建阅读VO
            ScriptureReadingVO readingVO = new ScriptureReadingVO();

            // 复制典籍基本信息
            readingVO.setScriptureId(scripture.getId());
            readingVO.setScriptureName(scripture.getScriptureName());
            readingVO.setScriptureType(scripture.getScriptureType());
            readingVO.setAuthor(scripture.getAuthor());
            readingVO.setDescription(scripture.getDescription());
            readingVO.setContent(scripture.getContent());
            readingVO.setCoverUrl(scripture.getCoverUrl());
            readingVO.setAudioUrl(scripture.getAudioUrl());
            readingVO.setWordCount(scripture.getWordCount());
            readingVO.setCategoryTags(scripture.getCategoryTags());

            // 复制用户阅读信息
            readingVO.setReadingProgress(purchase.getReadingProgress());
            readingVO.setLastReadingPosition(purchase.getLastReadingPosition());
            readingVO.setReadCount(purchase.getReadCount());
            readingVO.setLastReadTime(purchase.getLastReadTime());
            readingVO.setExpireTime(purchase.getExpireTime());
            readingVO.setMeritCoinsPaid(purchase.getMeritCoinsPaid());
            readingVO.setPurchaseMonths(purchase.getPurchaseMonths());

            // 计算剩余天数
            if (purchase.getExpireTime() != null) {
                long remainingDays = ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        purchase.getExpireTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                );
                readingVO.setRemainingDays(Math.max(0, remainingDays));
                readingVO.setIsExpiringSoon(remainingDays <= 7 && remainingDays > 0);
            }

            // 设置阅读位置（使用最后阅读位置，而不是根据阅读进度计算）
            Integer lastPosition = purchase.getLastReadingPosition();
            if (lastPosition == null) {
                lastPosition = 0; // 如果没有记录，从头开始
            }
            readingVO.setCurrentPosition(lastPosition);

            // 寻找建议的开始位置（从最后阅读位置寻找段落或句子边界）
            if (scripture.getContent() != null) {
                int suggestedStart = findSuggestedStartPosition(scripture.getContent(), lastPosition);
                readingVO.setSuggestedStartPosition(suggestedStart);
            }

            // 记录本次阅读行为
            userScripturePurchaseService.recordUserReading(userId, scriptureId);

            return Result.success(readingVO);
        } catch (Exception e) {
            return Result.error("获取阅读内容失败：" + e.getMessage());
        }
    }

    /**
     * 寻找建议的阅读开始位置（考虑段落或句子边界）
     *
     * @param content 典籍内容
     * @param currentPosition 当前位置
     * @return 建议开始位置
     */
    private int findSuggestedStartPosition(String content, int currentPosition) {
        if (content == null || currentPosition <= 0) {
            return 0;
        }

        if (currentPosition >= content.length()) {
            return content.length();
        }

        // 向前寻找段落边界（换行符）
        int paragraphStart = content.lastIndexOf('\n', currentPosition);
        if (paragraphStart != -1 && currentPosition - paragraphStart <= 200) {
            return paragraphStart + 1;
        }

        // 向前寻找句子边界（句号、问号、感叹号）
        int sentenceStart = -1;
        for (int i = currentPosition - 1; i >= Math.max(0, currentPosition - 100); i--) {
            char c = content.charAt(i);
            if (c == '。' || c == '？' || c == '！' || c == '.' || c == '?' || c == '!') {
                sentenceStart = i + 1;
                break;
            }
        }

        if (sentenceStart != -1) {
            return sentenceStart;
        }

        // 如果找不到合适的边界，返回当前位置向前50个字符
        return Math.max(0, currentPosition - 50);
    }

    /**
     * 获取用户购买统计
     *
     * @param httpRequest HTTP请求
     * @return 购买统计
     */
    @GetMapping("/stats")
    public Result<UserPurchaseStatsVO> getUserPurchaseStats(HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            // 获取基础统计数据
            Integer totalPurchases = userScripturePurchaseService.countUserPurchases(userId);
            List<UserScripturePurchase> allPurchases = userScripturePurchaseService.getPurchasesByUserId(userId);
            List<UserScripturePurchase> validPurchases = userScripturePurchaseService.getValidPurchasesByUserId(userId);
            List<UserScripturePurchase> expiringSoon = userScripturePurchaseService.getExpiringSoonPurchases(7);

            // 过滤当前用户的即将过期记录
            List<UserScripturePurchase> userExpiringSoon = expiringSoon.stream()
                    .filter(purchase -> purchase.getUserId().equals(userId))
                    .collect(Collectors.toList());

            // 计算已过期数量
            Integer expired = totalPurchases - validPurchases.size();

            // 计算总阅读次数
            Long totalReadCount = allPurchases.stream()
                    .mapToLong(purchase -> purchase.getReadCount() != null ? purchase.getReadCount() : 0L)
                    .sum();

            // 计算平均阅读进度
            Double averageProgress = allPurchases.stream()
                    .filter(purchase -> purchase.getReadingProgress() != null)
                    .mapToDouble(purchase -> purchase.getReadingProgress().doubleValue())
                    .average()
                    .orElse(0.0);

            // 构建统计VO
            UserPurchaseStatsVO statsVO = new UserPurchaseStatsVO(
                    totalPurchases,
                    validPurchases.size(),
                    userExpiringSoon.size(),
                    expired,
                    totalReadCount,
                    Math.round(averageProgress * 100.0) / 100.0 // 保留两位小数
            );

            return Result.success(statsVO);
        } catch (Exception e) {
            return Result.error("获取购买统计失败：" + e.getMessage());
        }
    }
}