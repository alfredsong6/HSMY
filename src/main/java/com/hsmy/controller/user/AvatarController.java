package com.hsmy.controller.user;

import com.hsmy.common.Result;
import com.hsmy.dto.UpdateAvatarRequest;
import com.hsmy.service.FileStorageServiceFactory;
import com.hsmy.service.UserService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    
//    /**
//     * 上传头像并自动更新用户信息
//     */
//    @PostMapping("/avatar/upload")
//    public Result<String> uploadAndUpdateAvatar(@RequestParam("file") MultipartFile file) {
//        try {
//            // 检查用户是否登录
//            Long userId = UserContextUtil.getCurrentUserId();
//            if (userId == null) {
//                return Result.error("用户未登录");
//            }
//
//            // 上传文件
//            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
//            FileUploadResult uploadResult = storageService.uploadFile(file, "avatar");
//
//            // 更新用户头像
//            Boolean updateSuccess = userService.updateAvatar(userId, uploadResult.getUrl());
//
//            if (updateSuccess) {
//                log.info("用户头像上传并更新成功，userId: {}, avatarUrl: {}", userId, uploadResult.getUrl());
//                return Result.success(uploadResult.getUrl());
//            } else {
//                // 如果更新失败，删除已上传的文件
//                storageService.deleteFile(uploadResult.getFilePath());
//                return Result.error("头像更新失败");
//            }
//        } catch (IllegalArgumentException e) {
//            log.warn("头像上传失败：{}", e.getMessage());
//            return Result.error(e.getMessage());
//        } catch (Exception e) {
//            log.error("头像上传失败", e);
//            return Result.error("上传失败：" + e.getMessage());
//        }
//    }
    
    /**
     * 更新头像URL
     */
    @PostMapping("/avatar/update")
    public Result<String> updateAvatar(@RequestBody @Validated UpdateAvatarRequest request) {
        try {
            // 检查用户是否登录
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 更新用户头像
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
            // 检查用户是否登录
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 将头像设置为空
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
     * 获取当前用户头像URL
     */
    @GetMapping("/avatar")
    public Result<String> getCurrentUserAvatar() {
        try {
            // 检查用户是否登录
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 获取用户信息
            com.hsmy.entity.User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            return Result.success(user.getAvatarUrl());
        } catch (Exception e) {
            log.error("获取用户头像失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }
}