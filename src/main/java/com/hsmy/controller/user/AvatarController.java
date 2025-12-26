package com.hsmy.controller.user;

import com.hsmy.common.Result;
import com.hsmy.config.FileStorageProperties;
import com.hsmy.dto.UpdateAvatarRequest;
import com.hsmy.service.FileStorageServiceFactory;
import com.hsmy.service.UserService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.AvatarResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 用户头像管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AvatarController {

    private final UserService userService;
    private final FileStorageServiceFactory fileStorageServiceFactory;
    private final FileStorageProperties fileStorageProperties;

    /**
     * 更新头像URL
     */
    @PostMapping("/avatar/update")
    public Result<String> updateAvatar(@RequestBody @Validated UpdateAvatarRequest request) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }

            Boolean success = userService.updateAvatar(userId, request.getAvatarUrl());

            if (success) {
                log.info("用户头像更新成功，userId: {}, avatarUrl: {}", userId, request.getAvatarUrl());
                return Result.success("头像更新成功");
            } else {
                return Result.error("头像更新失败");
            }
        } catch (Exception e) {
            log.error("头像更新失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除头像（设置为空）
     */
    @DeleteMapping("/avatar")
    public Result<String> deleteAvatar() {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }

            Boolean success = userService.updateAvatar(userId, null);

            if (success) {
                log.info("用户头像删除成功，userId: {}", userId);
                return Result.success("头像删除成功");
            } else {
                return Result.error("头像删除失败");
            }
        } catch (Exception e) {
            log.error("头像删除失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户头像
     */
    @GetMapping("/avatar")
    public Result<AvatarResponseVO> getCurrentUserAvatar() {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }

            com.hsmy.entity.User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            String avatarUrl = user.getAvatarUrl();
            AvatarResponseVO responseVO = new AvatarResponseVO();
            responseVO.setAvatarUrl(avatarUrl);

            if (!StringUtils.hasText(avatarUrl)) {
                return Result.success(responseVO);
            }

            if (avatarUrl.toLowerCase().startsWith("https")) {
                return Result.success(responseVO);
            }

            try {
                String relativePath = resolveRelativePath(avatarUrl);
                Path avatarPath = Paths.get(fileStorageProperties.getLocal().getRootPath(), relativePath);

                if (Files.exists(avatarPath)) {
                    byte[] fileBytes = Files.readAllBytes(avatarPath);
                    String mimeType = resolveMimeType(avatarPath);
                    String base64Data = Base64.getEncoder().encodeToString(fileBytes);
                    responseVO.setBase64Content("data:" + mimeType + ";base64," + base64Data);
                    responseVO.setBase64Encoded(true);
                } else {
                    log.warn("本地头像文件不存在，userId: {}, path: {}", userId, avatarPath.toAbsolutePath());
                }
            } catch (Exception ex) {
                log.error("本地头像转换Base64失败，userId: {}, avatarUrl: {}", userId, avatarUrl, ex);
            }

            return Result.success(responseVO);
        } catch (Exception e) {
            log.error("获取用户头像失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 将本地访问URL转换为存储路径
     */
    private String resolveRelativePath(String avatarUrl) {
        String relativePath = avatarUrl;
        try {
            if (avatarUrl.contains("://")) {
                relativePath = URI.create(avatarUrl).getPath();
            }
        } catch (Exception e) {
            log.warn("头像地址解析异常，avatarUrl: {}", avatarUrl, e);
        }
        String urlPrefix = fileStorageProperties.getLocal().getUrlPrefix();
        if (StringUtils.hasText(urlPrefix) && relativePath.startsWith(urlPrefix)) {
            relativePath = relativePath.substring(urlPrefix.length());
        }
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    private String resolveMimeType(Path path) {
        try {
            String detected = Files.probeContentType(path);
            if (StringUtils.hasText(detected)) {
                return detected;
            }
        } catch (Exception e) {
            log.warn("头像MimeType自动检测失败，path: {}", path, e);
        }
        String filename = path.getFileName().toString().toLowerCase();
        if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".bmp")) {
            return "image/bmp";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}
