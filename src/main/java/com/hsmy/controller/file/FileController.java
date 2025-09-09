package com.hsmy.controller.file;

import com.hsmy.common.Result;
import com.hsmy.dto.FileUploadResult;
import com.hsmy.service.FileStorageService;
import com.hsmy.service.FileStorageServiceFactory;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * 文件上传下载Controller
 */
@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileStorageServiceFactory fileStorageServiceFactory;
    
    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public Result<FileUploadResult> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 检查用户是否登录
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
            FileUploadResult result = storageService.uploadFile(file, "avatar");
            
            log.info("用户头像上传成功，userId: {}, url: {}", userId, result.getUrl());
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("头像上传失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传通用文件
     */
    @PostMapping("/upload/{folder}")
    public Result<FileUploadResult> uploadFile(@RequestParam("file") MultipartFile file,
                                             @PathVariable String folder) {
        try {
            // 检查用户是否登录
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
            FileUploadResult result = storageService.uploadFile(file, folder);
            
            log.info("文件上传成功，userId: {}, folder: {}, url: {}", userId, folder, result.getUrl());
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("文件上传失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public Result<String> deleteFile(@RequestParam String filePath) {
        try {
            // 检查用户是否登录
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
            boolean success = storageService.deleteFile(filePath);
            
            if (success) {
                log.info("文件删除成功，userId: {}, filePath: {}", userId, filePath);
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 本地文件下载（仅本地存储模式有效）
     */
    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        try {
            String requestPath = request.getRequestURI();
            String filePath = requestPath.replace("/api/file/uploads/", "");
            
            // 构建本地文件路径
            File file = Paths.get("./uploads", filePath).toFile();
            
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 获取文件类型
            String contentType = getContentType(file.getName());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "inline; filename=\"" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()) + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("文件下载失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取文件访问URL
     */
    @GetMapping("/url")
    public Result<String> getFileUrl(@RequestParam String filePath) {
        try {
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
            String url = storageService.getFileUrl(filePath);
            return Result.success(url);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查文件是否存在
     */
    @GetMapping("/exists")
    public Result<Boolean> checkFileExists(@RequestParam String filePath) {
        try {
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
            boolean exists = storageService.exists(filePath);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查文件存在性失败", e);
            return Result.error("检查失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据文件名获取Content-Type
     */
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            default:
                return "application/octet-stream";
        }
    }
}