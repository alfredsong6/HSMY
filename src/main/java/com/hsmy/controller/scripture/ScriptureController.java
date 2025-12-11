package com.hsmy.controller.scripture;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Scripture;
import com.hsmy.entity.ScriptureBarrageProgress;
import com.hsmy.entity.ScriptureSection;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.service.ScriptureBarrageProgressService;
import com.hsmy.service.ScriptureSectionService;
import com.hsmy.service.ScriptureService;
import com.hsmy.service.UserScripturePurchaseService;
import com.hsmy.utils.TextSanitizerUtil;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.ScriptureQueryVO;
import com.hsmy.vo.ScriptureSectionVO;
import com.hsmy.vo.ScriptureSectionsResponseVO;
import com.hsmy.vo.ScriptureTextStreamVO;
import com.hsmy.vo.ScriptureVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 典籍Controller
 *
 * @author HSMY
 * @date 2025/09/25
 */
@RestController
@RequestMapping("/scripture")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class ScriptureController {

    private final ScriptureService scriptureService;
    private final ScriptureSectionService scriptureSectionService;
    private final UserScripturePurchaseService userScripturePurchaseService;
    private final ScriptureBarrageProgressService scriptureBarrageProgressService;

    /**
     * 获取典籍列表
     *
     * @param queryVO 查询条件
     * @param request HTTP请求
     * @return 典籍列表
     */
    @GetMapping("/list")
    public Result<List<ScriptureVO>> getScriptureList(ScriptureQueryVO queryVO, HttpServletRequest request) {
        try {
            List<Scripture> scriptures;

            if (StringUtils.hasText(queryVO.getKeyword())) {
                // 关键词搜索
                scriptures = scriptureService.searchScriptures(queryVO.getKeyword());
            } else if (StringUtils.hasText(queryVO.getScriptureType())) {
                // 按类型查询
                scriptures = scriptureService.getScripturesByType(queryVO.getScriptureType());
            } else if (queryVO.getIsHot() != null && queryVO.getIsHot() == 1) {
                // 热门典籍
                scriptures = scriptureService.getHotScriptures();
            } else if (queryVO.getMinPrice() != null || queryVO.getMaxPrice() != null) {
                // 价格范围查询
                scriptures = scriptureService.getScripturesByPriceRange(queryVO.getMinPrice(), queryVO.getMaxPrice());
            } else if (queryVO.getDifficultyLevel() != null) {
                // 难度等级查询
                scriptures = scriptureService.getScripturesByDifficultyLevel(queryVO.getDifficultyLevel());
            } else if (StringUtils.hasText(queryVO.getTag())) {
                // 标签查询
                scriptures = scriptureService.getScripturesByTag(queryVO.getTag());
            } else {
                // 获取所有上架典籍
                scriptures = scriptureService.getAllActiveScriptures();
            }

            // 转换为VO并填充用户购买信息
            List<ScriptureVO> scriptureVOs = scriptures.stream().map(scripture -> {
                ScriptureVO vo = new ScriptureVO();
                BeanUtils.copyProperties(scripture, vo);
                vo.setId(scripture.getId().toString());
                vo.setCanPreview(scripture.getPreviewSectionCount() != null && scripture.getPreviewSectionCount() > 0);

                // 填充用户购买信息（如果用户已登录）
            try {
                Long userId = UserContextUtil.getCurrentUserId();
                if (userId != null) {
                    UserScripturePurchase purchase = userScripturePurchaseService.getUserPurchaseDetail(userId, scripture.getId());
                    if (purchase == null) {
                        vo.setIsPurchased(false);
                    }else if (purchase.getPurchaseType().equals("trial")) {
                        vo.setIsPurchased(false);
                    } else if (purchase.getPurchaseType().equals("lease") && purchase.getIsExpired() == 1) {
                        vo.setIsPurchased(false);
                    } else {
                        vo.setIsPurchased(true);
                    }
                    //vo.setIsPurchaseValid(purchased && userScripturePurchaseService.isUserPurchaseValid(userId, scripture.getId()));
                    if (purchase != null) {
                        vo.setExpireTime(purchase.getExpireTime());
                        vo.setPurchaseType(purchase.getPurchaseType());
                    }
                }
            } catch (Exception e) {
                // 用户未登录，忽略
                vo.setIsPurchased(false);
                vo.setIsPurchaseValid(false);
            }

                return vo;
            }).collect(Collectors.toList());

            return Result.success(scriptureVOs);
        } catch (Exception e) {
            return Result.error("获取典籍列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取典籍详情
     *
     * @param scriptureId 典籍ID
     * @param request HTTP请求
     * @return 典籍详情
     */
    @GetMapping("/{scriptureId}")
    public Result<ScriptureVO> getScriptureDetail(@PathVariable Long scriptureId, HttpServletRequest request) {
        try {
            Scripture scripture = scriptureService.getScriptureById(scriptureId);
            if (scripture == null) {
                return Result.error("典籍不存在");
            }

            ScriptureVO vo = new ScriptureVO();
            BeanUtils.copyProperties(scripture, vo);
            vo.setId(scriptureId.toString());
            vo.setCanPreview(scripture.getPreviewSectionCount() != null && scripture.getPreviewSectionCount() > 0);

            // 填充用户购买信息（如果用户已登录）
            try {
                Long userId = UserContextUtil.getCurrentUserId();
                if (userId != null) {
                    UserScripturePurchase purchase = userScripturePurchaseService.getUserPurchaseDetail(userId, scriptureId);
                    boolean purchased = purchase != null;
                    vo.setIsPurchased(purchased);
                    vo.setIsPurchaseValid(purchased && userScripturePurchaseService.isUserPurchaseValid(userId, scriptureId));
                    if (purchase != null) {
                        vo.setExpireTime(purchase.getExpireTime());
                        vo.setPurchaseType(purchase.getPurchaseType());
                    }
                }
            } catch (Exception e) {
                // 用户未登录，忽略
                vo.setIsPurchased(false);
                vo.setIsPurchaseValid(false);
            }

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("获取典籍详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门典籍列表
     *
     * @param request HTTP请求
     * @return 热门典籍列表
     */
    @GetMapping("/hot")
    public Result<List<ScriptureVO>> getHotScriptures(HttpServletRequest request) {
        try {
            List<Scripture> scriptures = scriptureService.getHotScriptures();
            List<ScriptureVO> scriptureVOs = scriptures.stream().map(scripture -> {
                ScriptureVO vo = new ScriptureVO();
                BeanUtils.copyProperties(scripture, vo);
                vo.setCanPreview(scripture.getPreviewSectionCount() != null && scripture.getPreviewSectionCount() > 0);
                return vo;
            }).collect(Collectors.toList());

            return Result.success(scriptureVOs);
        } catch (Exception e) {
            return Result.error("获取热门典籍失败：" + e.getMessage());
        }
    }

    /**
     * 按类型获取典籍列表
     *
     * @param scriptureType 典籍类型
     * @param request HTTP请求
     * @return 典籍列表
     */
    @GetMapping("/type/{scriptureType}")
    public Result<List<ScriptureVO>> getScripturesByType(@PathVariable String scriptureType, HttpServletRequest request) {
        try {
            List<Scripture> scriptures = scriptureService.getScripturesByType(scriptureType);
            List<ScriptureVO> scriptureVOs = scriptures.stream().map(scripture -> {
                ScriptureVO vo = new ScriptureVO();
                BeanUtils.copyProperties(scripture, vo);
                vo.setCanPreview(scripture.getPreviewSectionCount() != null && scripture.getPreviewSectionCount() > 0);
                return vo;
            }).collect(Collectors.toList());

            return Result.success(scriptureVOs);
        } catch (Exception e) {
            return Result.error("获取典籍列表失败：" + e.getMessage());
        }
    }

    /**
     * 搜索典籍
     *
     * @param keyword 搜索关键词
     * @param request HTTP请求
     * @return 搜索结果
     */
    @GetMapping("/search")
    public Result<List<ScriptureVO>> searchScriptures(@RequestParam String keyword, HttpServletRequest request) {
        try {
            if (!StringUtils.hasText(keyword)) {
                return Result.error("搜索关键词不能为空");
            }

            List<Scripture> scriptures = scriptureService.searchScriptures(keyword);
            List<ScriptureVO> scriptureVOs = scriptures.stream().map(scripture -> {
                ScriptureVO vo = new ScriptureVO();
                BeanUtils.copyProperties(scripture, vo);
                vo.setCanPreview(scripture.getPreviewSectionCount() != null && scripture.getPreviewSectionCount() > 0);
                return vo;
            }).collect(Collectors.toList());

            return Result.success(scriptureVOs);
        } catch (Exception e) {
            return Result.error("搜索典籍失败：" + e.getMessage());
        }
    }

    /**
     * 获取典籍分段列表
     *
     * @param scriptureId 典籍ID
     * @return 分段列表
     */
    @GetMapping("/{scriptureId}/sections/catalog")
    public Result<ScriptureSectionsResponseVO> getScriptureSections(@PathVariable Long scriptureId) {
        try {
            Scripture scripture = scriptureService.getScriptureById(scriptureId);
            if (scripture == null) {
                return Result.error("典籍不存在");
            }
            List<ScriptureSection> sections = scriptureSectionService.listByScriptureId(scriptureId);
            List<ScriptureSectionVO> sectionVOS = sections.stream().map(section -> {
                ScriptureSectionVO vo = new ScriptureSectionVO();
                vo.setId(section.getId().toString());
                vo.setSectionId(section.getId().toString());
                vo.setSectionNo(section.getSectionNo());
                vo.setTitle(section.getTitle());
                vo.setWordCount(section.getWordCount());
                vo.setDurationSeconds(section.getDurationSeconds());
                vo.setIsFree(section.getIsFree());
                return vo;
            }).collect(Collectors.toList());
            ScriptureSectionsResponseVO resp = new ScriptureSectionsResponseVO();
            resp.setScriptureId(scriptureId);
            resp.setSections(sectionVOS);
            return Result.success(resp);
        } catch (Exception e) {
            return Result.error("获取分段列表失败：" + e.getMessage());
        }
    }

    /**
     * 按全局偏移获取纯文本流，用于跨卷连续阅读。
     *
     * @param scriptureId 典籍ID
     * @param offset      全局字符偏移（默认0）
     * @param limit       获取的字符数量（默认200，上限2000）
     * @param dailyReset  是否每日重置进度（可选）
     * @return 文本块、下一偏移量及是否结束
     */
    @GetMapping("/{scriptureId}/text-stream")
    public Result<ScriptureTextStreamVO> streamText(@PathVariable Long scriptureId,
                                                    @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                    @RequestParam(value = "limit", defaultValue = "200") Integer limit,
                                                    @RequestParam(value = "dailyReset", required = false) Boolean dailyReset) {
        try {
            Scripture scripture = scriptureService.getScriptureById(scriptureId);
            if (scripture == null) {
                return Result.error("典籍不存在");
            }
            List<ScriptureSection> sections = scriptureSectionService.listByScriptureId(scriptureId);
            if (sections == null || sections.isEmpty()) {
                return Result.error("典籍尚未配置分段内容");
            }

            int safeOffset = offset == null || offset < 0 ? 0 : offset;
            int safeLimit = limit == null || limit <= 0 ? 200 : Math.min(limit, 2000);

            Long userId = UserContextUtil.getCurrentUserId();
            if (userId != null) {
                ScriptureBarrageProgress progress = scriptureBarrageProgressService.getByUserAndScripture(userId, scriptureId);
                if (progress != null && progress.getIsDailyReset() != null && progress.getIsDailyReset() == 1 && progress.getLastFetchTime() != null) {
                    LocalDate lastDate = progress.getLastFetchTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    // 开启每日重置时，隔日重新从0开始
                    if (lastDate.isBefore(LocalDate.now())) {
                        safeOffset = 0;
                    }
                }
            }

            int totalLength = 0;
            int consumed = 0;
            StringBuilder builder = new StringBuilder();
            Long lastSectionId = null;
            int sectionOffset = 0;

            for (ScriptureSection section : sections) {
                String plain = TextSanitizerUtil.toPlainText(section.getContent());
                int len = plain.length();
                int sectionStart = totalLength;

                if (safeOffset < totalLength + len && consumed < safeLimit) {
                    int startInSection = Math.max(0, safeOffset - totalLength);
                    int take = Math.min(safeLimit - consumed, len - startInSection);
                    if (take > 0) {
                        builder.append(plain, startInSection, startInSection + take);
                        consumed += take;
                    }
                }

                int endGlobal = safeOffset + consumed;
                // 记录当前chunk落在的分段及分段内偏移，便于下次继续
                if (endGlobal > sectionStart && endGlobal <= sectionStart + len) {
                    lastSectionId = section.getId();
                    sectionOffset = endGlobal - sectionStart;
                }

                totalLength += len;
            }

            int nextOffset = Math.min(totalLength, safeOffset + consumed);
            boolean isEnd = nextOffset >= totalLength;

            ScriptureTextStreamVO vo = new ScriptureTextStreamVO();
            vo.setContent(builder.toString());
            vo.setNextOffset(nextOffset);
            vo.setIsEnd(isEnd);

            if (userId != null) {
                // 持久化弹幕阅读游标
                scriptureBarrageProgressService.saveProgress(
                        userId,
                        scriptureId,
                        lastSectionId,
                        sectionOffset,
                        nextOffset,
                        safeLimit,
                        dailyReset
                );
            }

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("获取文本流失败：" + e.getMessage());
        }
    }

//    /**
//     * 记录阅读行为（增加阅读次数）
//     *
//     * @param scriptureId 典籍ID
//     * @param request HTTP请求
//     * @return 操作结果
//     */
//    @PostMapping("/{scriptureId}/read")
//    public Result<Void> recordReading(@PathVariable Long scriptureId, HttpServletRequest request) {
//        try {
//            Long userId = UserContextUtil.requireCurrentUserId();
//
//            // 检查用户是否有权限阅读该典籍
//            if (!userScripturePurchaseService.isUserPurchaseValid(userId, scriptureId)) {
//                return Result.error("您尚未购买该典籍或购买已过期");
//            }
//
//            Boolean success = userScripturePurchaseService.recordUserReading(userId, scriptureId);
//            if (success) {
//                return Result.success();
//            } else {
//                return Result.error("记录阅读失败");
//            }
//        } catch (Exception e) {
//            return Result.error("记录阅读失败：" + e.getMessage());
//        }
//    }
}
