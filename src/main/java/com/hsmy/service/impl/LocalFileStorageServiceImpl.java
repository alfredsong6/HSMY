package com.hsmy.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.hsmy.config.FileStorageProperties;
import com.hsmy.dto.FileUploadResult;
import com.hsmy.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 本地文件存储服务实现
 */
@Slf4j
@Service("localFileStorageService")
@RequiredArgsConstructor
public class LocalFileStorageServiceImpl implements FileStorageService {
    
    private final FileStorageProperties fileStorageProperties;
    
    @Override
    public FileUploadResult uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String fileExtension = FileUtil.extName(originalFilename).toLowerCase();
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }
        
        // 检查文件大小
        if (file.getSize() > fileStorageProperties.getMaxFileSize()) {
            throw new IllegalArgumentException("文件大小超过限制: " + fileStorageProperties.getMaxFileSize() + " 字节");
        }
        
        // 生成新文件名
        String newFileName = generateFileName(fileExtension);
        
        // 构建存储路径：rootPath/folder/年月日/fileName
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativePath = folder + "/" + datePath + "/" + newFileName;
        
        // 创建绝对路径
        Path rootPath = Paths.get(fileStorageProperties.getLocal().getRootPath());
        Path filePath = rootPath.resolve(relativePath);
        
        // 创建目录
        Files.createDirectories(filePath.getParent());
        
        // 保存文件
        Files.copy(file.getInputStream(), filePath);
        
        // 计算文件MD5
        String md5 = SecureUtil.md5(file.getInputStream());
        
        // 构建返回结果
        FileUploadResult result = new FileUploadResult();
        result.setFileName(newFileName);
        result.setFileSize(file.getSize());
        result.setFileType(fileExtension);
        result.setFilePath(relativePath);
        result.setMd5(md5);
        result.setUrl(getFileUrl(relativePath));
        
        log.info("文件上传成功: {}", result.getUrl());
        return result;
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path rootPath = Paths.get(fileStorageProperties.getLocal().getRootPath());
            Path fileFullPath = rootPath.resolve(filePath);
            
            if (Files.exists(fileFullPath)) {
                Files.delete(fileFullPath);
                log.info("文件删除成功: {}", filePath);
                return true;
            } else {
                log.warn("文件不存在: {}", filePath);
                return false;
            }
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String filePath) {
        return fileStorageProperties.getLocal().getUrlPrefix() + "/" + filePath.replace("\\", "/");
    }
    
    @Override
    public boolean exists(String filePath) {
        Path rootPath = Paths.get(fileStorageProperties.getLocal().getRootPath());
        Path fileFullPath = rootPath.resolve(filePath);
        return Files.exists(fileFullPath);
    }
    
    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedFileType(String fileExtension) {
        String[] allowedTypes = fileStorageProperties.getAllowedTypes();
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 生成新文件名
     */
    private String generateFileName(String fileExtension) {
        return IdUtil.fastSimpleUUID() + "." + fileExtension;
    }
}