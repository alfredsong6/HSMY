package com.hsmy.controller.scripture;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Scripture;
import com.hsmy.entity.ScriptureSection;
import com.hsmy.service.ScriptureService;
import com.hsmy.service.ScriptureSectionService;
import com.hsmy.service.UserScripturePurchaseService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.ScriptureQueryVO;
import com.hsmy.vo.ScriptureVO;
import com.hsmy.vo.ScriptureSectionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

                // 填充用户购买信息（如果用户已登录）
                try {
                    Long userId = UserContextUtil.getCurrentUserId();
                    if (userId != null) {
                        vo.setIsPurchased(userScripturePurchaseService.hasUserPurchased(userId, scripture.getId()));
                        vo.setIsPurchaseValid(userScripturePurchaseService.isUserPurchaseValid(userId, scripture.getId()));
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

            // 填充用户购买信息（如果用户已登录）
            try {
                Long userId = UserContextUtil.getCurrentUserId();
                if (userId != null) {
                    vo.setIsPurchased(userScripturePurchaseService.hasUserPurchased(userId, scriptureId));
                    vo.setIsPurchaseValid(userScripturePurchaseService.isUserPurchaseValid(userId, scriptureId));
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
    @GetMapping("/{scriptureId}/sections")
    public Result<List<ScriptureSectionVO>> getScriptureSections(@PathVariable Long scriptureId) {
        try {
            Scripture scripture = scriptureService.getScriptureById(scriptureId);
            if (scripture == null) {
                return Result.error("典籍不存在");
            }
            List<ScriptureSection> sections = scriptureSectionService.listByScriptureId(scriptureId);
            List<ScriptureSectionVO> sectionVOS = sections.stream().map(section -> {
                ScriptureSectionVO vo = new ScriptureSectionVO();
                vo.setId(section.getId());
                vo.setSectionNo(section.getSectionNo());
                vo.setTitle(section.getTitle());
                vo.setWordCount(section.getWordCount());
                vo.setDurationSeconds(section.getDurationSeconds());
                vo.setIsFree(section.getIsFree());
                return vo;
            }).collect(Collectors.toList());
            return Result.success(sectionVOS);
        } catch (Exception e) {
            return Result.error("获取分段列表失败：" + e.getMessage());
        }
    }

    /**
     * 记录阅读行为（增加阅读次数）
     *
     * @param scriptureId 典籍ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{scriptureId}/read")
    public Result<Void> recordReading(@PathVariable Long scriptureId, HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();

            // 检查用户是否有权限阅读该典籍
            if (!userScripturePurchaseService.isUserPurchaseValid(userId, scriptureId)) {
                return Result.error("您尚未购买该典籍或购买已过期");
            }

            Boolean success = userScripturePurchaseService.recordUserReading(userId, scriptureId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("记录阅读失败");
            }
        } catch (Exception e) {
            return Result.error("记录阅读失败：" + e.getMessage());
        }
    }
}
