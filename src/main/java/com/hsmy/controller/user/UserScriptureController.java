package com.hsmy.controller.user;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.*;
import com.hsmy.entity.Scripture;
import com.hsmy.entity.ScriptureSection;
import com.hsmy.entity.UserScriptureProgress;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.service.ScriptureSectionService;
import com.hsmy.service.ScriptureService;
import com.hsmy.service.UserScriptureProgressService;
import com.hsmy.service.UserScripturePurchaseService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
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
    private final ScriptureSectionService scriptureSectionService;
    private final UserScriptureProgressService userScriptureProgressService;

    /**
     * 购买典籍
     *
     * @param request 购买请求
     * @param httpRequest HTTP请求
     * @return 购买结果
     */
    //@PostMapping("/purchase")
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
                    userId, request.getScriptureId(), null);

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
     * 买断典籍
     *
     * @param request 买断请求
     * @param httpRequest HTTP请求
     * @return 购买结果
     */
    @PostMapping("/purchase-permanent")
    public Result<Void> purchaseScripturePermanent(@RequestBody @Valid PurchaseScripturePermanentRequest request,
                                                 HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            // 根据购买类型执行不同逻辑
            if ("permanent".equals(request.getPurchaseType())) {
                // 买断模式
                if (!scriptureService.checkScriptureAvailable(request.getScriptureId())) {
                    return Result.error("该典籍不存在或已下架");
                }

                Boolean success = userScripturePurchaseService.purchaseScripturePermanent(userId, request.getScriptureId());
                if (success) {
                    return Result.success();
                } else {
                    return Result.error("买断失败，请检查您的福币余额、该典籍是否支持买断或您是否已购买");
                }
            } else {
                return Result.error("不支持的购买类型");
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
    //@GetMapping("/purchases")
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

                boolean isPermanent = "permanent".equalsIgnoreCase(purchase.getPurchaseType()) || purchase.getExpireTime() == null;
                vo.setIsPermanent(isPermanent);

                // 计算剩余天数
                if (purchase.getStatus() != null && purchase.getStatus() != 1) {
                    vo.setRemainingDays(0L);
                    vo.setIsExpiringSoon(false);
                } else if (!isPermanent && purchase.getExpireTime() != null) {
                    long remainingDays = ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            purchase.getExpireTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    );
                    vo.setRemainingDays(remainingDays);
                    vo.setIsExpiringSoon(remainingDays <= 7 && remainingDays > 0);
                } else {
                    vo.setRemainingDays(-1L);
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
                    vo.setIsPermanent(false);
                } else {
                    vo.setRemainingDays(-1L);
                    vo.setIsExpiringSoon(false);
                    vo.setIsPermanent(true);
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
    //@PutMapping("/reading-progress")
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

//    /**
//     * 更新分段阅读进度
//     *
//     * @param request 更新请求
//     * @param httpRequest HTTP请求
//     * @return 更新结果
//     */
//    @PutMapping("/section/progress")
//    public Result<Void> updateSectionProgress(@RequestBody @Valid UpdateSectionProgressRequest request,
//                                              HttpServletRequest httpRequest) {
//        try {
//            Long userId = UserContextUtil.requireCurrentUserId();
//
//            if (!userScripturePurchaseService.isUserPurchaseValid(userId, request.getScriptureId())) {
//                return Result.error("您尚未购买该典籍或购买已过期");
//            }
//
//            Boolean success = userScripturePurchaseService.updateSectionProgress(
//                    userId,
//                    request.getScriptureId(),
//                    request.getSectionId(),
//                    request.getLastPosition(),
//                    request.getSectionProgress(),
//                    request.getTotalProgress(),
//                    request.getSpendSeconds());
//
//            if (success) {
//                return Result.success();
//            } else {
//                return Result.error("更新分段进度失败");
//            }
//        } catch (Exception e) {
//            return Result.error("更新分段进度失败：" + e.getMessage());
//        }
//    }

    /**
     * 更新最后阅读位置
     *
     * @param request 更新请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    //@PutMapping("/last-reading-position")
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

            boolean isPermanent = "permanent".equalsIgnoreCase(purchase.getPurchaseType()) || purchase.getExpireTime() == null;
            vo.setIsPermanent(isPermanent);

            // 计算剩余天数
            if (purchase.getStatus() != null && purchase.getStatus() != 1) {
                vo.setRemainingDays(0L);
                vo.setIsExpiringSoon(false);
            } else if (!isPermanent && purchase.getExpireTime() != null ) {
                long remainingDays = ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        purchase.getExpireTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                );
                vo.setRemainingDays(remainingDays);
                vo.setIsExpiringSoon(remainingDays <= 7 && remainingDays > 0);
            } else {
                vo.setRemainingDays(-1L);
                vo.setIsExpiringSoon(false);
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
    //@PostMapping("/renew/{scriptureId}")
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
    public Result<Boolean> checkPurchaseValid(@PathVariable String scriptureId,
                                            HttpServletRequest httpRequest) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            Boolean isValid = userScripturePurchaseService.isUserPurchaseValid(userId, Long.valueOf(scriptureId));
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.error("检查购买有效性失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户在某本经的最新阅读进度
     *
     * @param scriptureId 典籍ID
     * @return 最新阅读进度
     */
    @GetMapping("/{scriptureId}/latest-progress")
    public Result<LatestScriptureProgressVO> getLatestProgress(@PathVariable String scriptureId) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            Scripture scripture = scriptureService.getScriptureById(Long.valueOf(scriptureId));
            if (scripture == null) {
                return Result.error("典籍不存在");
            }

            UserScripturePurchase purchase = userScripturePurchaseService.getUserPurchaseDetail(userId, Long.valueOf(scriptureId));
            if (purchase == null) {
                //return Result.error("您尚未购买该典籍");
                return Result.success(null);
            }

            //userScripturePurchaseService.isUserPurchaseValid(userId, Long.valueOf(scriptureId)); // refresh status if needed

            // 确定当前分段
            ScriptureSection section = null;
            if (purchase.getLastSectionId() != null) {
                section = scriptureSectionService.getById(purchase.getLastSectionId());
            }
            if (section == null) {
                UserScriptureProgress latest = userScriptureProgressService.getLatestByUserAndScripture(userId, Long.valueOf(scriptureId));
                if (latest != null && latest.getSectionId() != null) {
                    section = scriptureSectionService.getById(latest.getSectionId());
                }
            }
            if (section == null) {
                section = scriptureSectionService.getFirstSection(Long.valueOf(scriptureId));
            }
            if (section == null) {
                return Result.error("典籍尚未配置分段内容");
            }

            UserScriptureProgress progress = userScriptureProgressService.getByUserAndSection(userId, section.getId());
            Integer lastPosition = progress != null && progress.getLastPosition() != null ? progress.getLastPosition() : 0;

            LatestScriptureProgressVO vo = new LatestScriptureProgressVO();
            vo.setScriptureId(scriptureId);
            vo.setScriptureName(scripture.getScriptureName());
            vo.setCoverUrl(scripture.getCoverUrl());
            vo.setLastPosition(lastPosition);
            vo.setReadingProgress(purchase.getReadingProgress());
            vo.setCompletedSections(purchase.getCompletedSections());
            vo.setTotalSections(scripture.getSectionCount());
            vo.setLastReadTime(purchase.getLastReadTime());
            vo.setStatus(purchase.getStatus());

            LatestScriptureProgressVO.Section sec = new LatestScriptureProgressVO.Section();
            sec.setSectionId(section.getId().toString());
            sec.setSectionNo(section.getSectionNo());
            sec.setTitle(section.getTitle());
            vo.setCurrentSection(sec);

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("获取最新阅读进度失败：" + e.getMessage());
        }
    }

    /**
     * 开始阅读：若无记录则创建 trial，并返回阅读状态
     */
    @PostMapping("/start/{scriptureId}")
    public Result<StartReadingStatusVO> startReading(@PathVariable String scriptureId) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            Scripture scripture = scriptureService.getScriptureById(Long.valueOf(scriptureId));
            if (scripture == null) {
                return Result.error("典籍不存在");
            }

            UserScripturePurchase purchase = userScripturePurchaseService.getUserPurchaseDetail(userId, Long.valueOf(scriptureId));
            if (purchase == null) {
                if (scripture.getPrice() != null && scripture.getPrice() == 0) {
                    purchase = userScripturePurchaseService.ensureFreePermanentPurchase(userId, scripture);
                } else {
                    purchase = userScripturePurchaseService.ensureTrialPurchase(userId, Long.valueOf(scriptureId));
                }
            } else if (!"permanent".equalsIgnoreCase(purchase.getPurchaseType())
                    && scripture.getPrice() != null && scripture.getPrice() == 0) {
                purchase = userScripturePurchaseService.ensureFreePermanentPurchase(userId, scripture);
            }

            boolean valid = userScripturePurchaseService.isUserPurchaseValid(userId, Long.valueOf(scriptureId));
            Integer totalSections = scripture.getSectionCount();
            Integer completedSections = purchase.getCompletedSections();
            int previewCount = scripture.getPreviewSectionCount() == null ? 0 : scripture.getPreviewSectionCount();
            int done = completedSections == null ? 0 : completedSections;

            String status;
            Boolean readFlag;
            if ("trial".equalsIgnoreCase(purchase.getPurchaseType())) {
                status = done < previewCount ? "trial_exceeded" : "trial_not_exceeded";
                readFlag = done < previewCount;
            } else if (valid) {
                status = "valid";
                readFlag = true;
            } else {
                status = "expired";
                readFlag = false;
            }

            StartReadingStatusVO vo = new StartReadingStatusVO();
            vo.setStatus(status);
            vo.setReadFlag(readFlag);
            vo.setPurchaseType(purchase.getPurchaseType());
            vo.setExpireTime(purchase.getExpireTime());
            vo.setCompletedSections(completedSections);
            vo.setTotalSections(totalSections);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("开始阅读失败：" + e.getMessage());
        }
    }

    /**
     * 获取某卷内容和当前卷进度
     *
     * @param scriptureId 典籍ID
     * @param sectionId   分段ID
     * @return 卷内容与进度
     */
    @GetMapping("/{scriptureId}/sections/{sectionId}")
    public Result<SectionContentResponseVO> getSectionContent(@PathVariable String scriptureId,
                                                              @PathVariable String sectionId) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            Scripture scripture = scriptureService.getScriptureById(Long.valueOf(scriptureId));
            if (scripture == null) {
                return Result.error("典籍不存在");
            }

            ScriptureSection section = scriptureSectionService.getById(Long.valueOf(sectionId));
            if (section == null || !scripture.getId().equals(section.getScriptureId())) {
                return Result.error("分段不存在或不属于该典籍");
            }

            boolean purchaseValid = userScripturePurchaseService.isUserPurchaseValid(userId, Long.valueOf(scriptureId));
            boolean preview = isPreviewSection(scripture, section);
            if (!purchaseValid && !preview) {
                return Result.error("您尚未购买该典籍或购买已过期");
            }

            UserScriptureProgress progress = userScriptureProgressService.getByUserAndSection(userId, Long.valueOf(sectionId));

            SectionContentResponseVO vo = new SectionContentResponseVO();

            SectionContentResponseVO.ScriptureInfo sInfo = new SectionContentResponseVO.ScriptureInfo();
            sInfo.setId(scripture.getId());
            sInfo.setName(scripture.getScriptureName());
            sInfo.setType(scripture.getScriptureType());
            vo.setScripture(sInfo);

            SectionContentResponseVO.SectionInfo secInfo = new SectionContentResponseVO.SectionInfo();
            secInfo.setId(section.getId().toString());
            secInfo.setSectionNo(section.getSectionNo());
            secInfo.setTitle(section.getTitle());
            secInfo.setContent(section.getContent());
            secInfo.setAudioUrl(section.getAudioUrl());
            secInfo.setWordCount(section.getWordCount());
            vo.setSection(secInfo);

            SectionContentResponseVO.ReadingState state = new SectionContentResponseVO.ReadingState();
            state.setLastPosition(progress != null ? progress.getLastPosition() : 0);
            state.setReadingProgress(progress != null && progress.getReadingProgress() != null ? progress.getReadingProgress().doubleValue() : 0D);
            state.setIsCompleted(progress != null && progress.getIsCompleted() != null ? progress.getIsCompleted() : 0);
            state.setLastReadTime(progress != null ? progress.getLastReadTime() : null);
            vo.setReadingState(state);

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("获取卷内容失败：" + e.getMessage());
        }
    }

    /**
     * 保存当前卷阅读进度（Upsert 分段进度并汇总整本）
     */
    @PostMapping("/{scriptureId}/sections/{sectionId}/progress")
    public Result<Void> saveSectionProgress(@PathVariable String scriptureId,
                                            @PathVariable String sectionId,
                                            @RequestBody @Valid SaveSectionProgressRequest request) {
        try {
            // 校验 userId 是否为当前登录用户
            Long currentUserId = UserContextUtil.requireCurrentUserId();

            Scripture scripture = scriptureService.getScriptureById(Long.valueOf(scriptureId));
            if (scripture == null) {
                return Result.error("典籍不存在");
            }

            //UserScripturePurchase purchase = userScripturePurchaseService.getUserPurchaseDetail(currentUserId, Long.valueOf(scriptureId));
            ScriptureSection section = scriptureSectionService.getById(Long.valueOf(sectionId));
            if (section == null || !scripture.getId().equals(section.getScriptureId())) {
                return Result.error("分段不存在或不属于该典籍");
            }

            boolean preview = isPreviewSection(scripture, section);
            boolean purchaseValid = userScripturePurchaseService.isUserPurchaseValid(currentUserId, Long.valueOf(scriptureId));
            if (!purchaseValid && !preview) {
                return Result.error("您尚未购买该典籍或购买已过期");
            }

            // 分段进度 upsert + 整本进度汇总
            userScripturePurchaseService.updateSectionProgress(
                    currentUserId,
                    Long.valueOf(scriptureId),
                    Long.valueOf(sectionId),
                    request.getLastPosition(),
                    request.getSectionReadingProgress(),
                    null,
                    request.getSpendSeconds(),
                    request.getIsCompleted() != null && request.getIsCompleted() == 1
            );

            // 按规格再汇总整本进度 (用 completedSections/sectionCount)
            Integer completedSections = userScriptureProgressService.countCompletedSections(currentUserId, Long.valueOf(scriptureId));
            int totalSections = scripture.getSectionCount() == null ? 0 : scripture.getSectionCount();
            double totalProgress = (totalSections > 0 && completedSections != null)
                    ? Math.min(100.0, (completedSections * 100.0) / totalSections)
                    : 0.0;

            userScripturePurchaseService.updateSectionProgress(
                    currentUserId,
                    Long.valueOf(scriptureId),
                    Long.valueOf(sectionId),
                    request.getLastPosition(),
                    request.getSectionReadingProgress(),
                    totalProgress,
                    request.getSpendSeconds(),
                    request.getIsCompleted() != null && request.getIsCompleted() == 1
            );

            return Result.success();
        } catch (Exception e) {
            return Result.error("保存分段进度失败：" + e.getMessage());
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

            // 定位当前分段
            ScriptureSection section = null;
            if (purchase.getLastSectionId() != null) {
                section = scriptureSectionService.getById(purchase.getLastSectionId());
            }
            if (section == null) {
                section = scriptureSectionService.getFirstSection(scriptureId);
            }
            if (section == null) {
                return Result.error("典籍尚未配置分段内容");
            }

            UserScriptureProgress progress = userScriptureProgressService.getByUserAndSection(userId, section.getId());
            Integer lastPosition = progress != null && progress.getLastPosition() != null ? progress.getLastPosition() : 0;

            // 构建阅读VO
            ScriptureReadingVO readingVO = new ScriptureReadingVO();
            readingVO.setScriptureId(scripture.getId());
            readingVO.setScriptureName(scripture.getScriptureName());
            readingVO.setScriptureType(scripture.getScriptureType());
            readingVO.setAuthor(scripture.getAuthor());
            readingVO.setDescription(scripture.getDescription());
            readingVO.setCoverUrl(scripture.getCoverUrl());
            readingVO.setAudioUrl(section.getAudioUrl());
            readingVO.setWordCount(scripture.getWordCount());
            readingVO.setCategoryTags(scripture.getCategoryTags());

            readingVO.setSectionId(section.getId());
            readingVO.setSectionNo(section.getSectionNo());
            readingVO.setSectionTitle(section.getTitle());
            readingVO.setSectionWordCount(section.getWordCount());
            readingVO.setContent(section.getContent());

            readingVO.setReadingProgress(purchase.getReadingProgress());
            readingVO.setSectionProgress(progress != null && progress.getReadingProgress() != null ? progress.getReadingProgress() : BigDecimal.ZERO);
            readingVO.setLastReadingPosition(lastPosition);
            readingVO.setCurrentPosition(lastPosition);
            readingVO.setReadCount(purchase.getReadCount());
            readingVO.setLastReadTime(purchase.getLastReadTime());
            readingVO.setExpireTime(purchase.getExpireTime());
            readingVO.setMeritCoinsPaid(purchase.getMeritCoinsPaid());
            readingVO.setPurchaseMonths(purchase.getPurchaseMonths());

            // 计算剩余天数与是否买断
            boolean isPermanent = "permanent".equalsIgnoreCase(purchase.getPurchaseType()) || purchase.getExpireTime() == null;
            readingVO.setIsPermanent(isPermanent);
            if (purchase.getExpireTime() != null) {
                long remainingDays = ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        purchase.getExpireTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                );
                readingVO.setRemainingDays(Math.max(0, remainingDays));
                readingVO.setIsExpiringSoon(remainingDays <= 7 && remainingDays > 0);
            } else {
                readingVO.setRemainingDays(-1L);
                readingVO.setIsExpiringSoon(false);
            }

            if (section.getContent() != null) {
                int suggestedStart = findSuggestedStartPosition(section.getContent(), lastPosition);
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

    private boolean isPreviewSection(Scripture scripture, ScriptureSection section) {
        if (section == null) {
            return false;
        }
        if (section.getIsFree() != null && section.getIsFree() == 1) {
            return true;
        }
        Integer previewCount = scripture != null ? scripture.getPreviewSectionCount() : null;
        return previewCount != null && previewCount > 0 && section.getSectionNo() != null && section.getSectionNo() <= previewCount;
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
