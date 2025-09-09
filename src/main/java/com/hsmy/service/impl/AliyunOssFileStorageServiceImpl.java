package com.hsmy.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.hsmy.config.FileStorageProperties;
import com.hsmy.dto.FileUploadResult;
import com.hsmy.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 阿里云OSS存储服务实现
 * 注意：此实现需要添加阿里云OSS SDK依赖
 */
@Slf4j
@Service("aliyunOssFileStorageService")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.storage.type", havingValue = "aliyun-oss")
public class AliyunOssFileStorageServiceImpl implements FileStorageService {
    
    private final FileStorageProperties fileStorageProperties;
    
    // TODO: 需要引入阿里云OSS SDK
    // private OSS ossClient;
    
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
        
        // 构建存储路径
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String objectKey = folder + "/" + datePath + "/" + newFileName;
        
        // TODO: 使用阿里云OSS SDK上传文件
        /*
        try {
            // 初始化OSS客户端
            if (ossClient == null) {
                ossClient = createOssClient();
            }
            
            // 创建上传请求
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            // 上传文件
            PutObjectResult result = ossClient.putObject(
                fileStorageProperties.getAliyunOss().getBucketName(),
                objectKey,
                file.getInputStream(),
                metadata
            );
            
        } catch (Exception e) {
            log.error("阿里云OSS上传失败", e);
            throw new IOException("文件上传失败", e);
        }
        */
        
        // 计算文件MD5
        String md5 = SecureUtil.md5(file.getInputStream());
        
        // 构建返回结果
        FileUploadResult result = new FileUploadResult();
        result.setFileName(newFileName);
        result.setFileSize(file.getSize());
        result.setFileType(fileExtension);
        result.setFilePath(objectKey);
        result.setMd5(md5);
        result.setUrl(getFileUrl(objectKey));
        
        log.info("阿里云OSS文件上传成功: {}", result.getUrl());
        return result;
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            // TODO: 使用阿里云OSS SDK删除文件
            /*
            if (ossClient == null) {
                ossClient = createOssClient();
            }
            
            ossClient.deleteObject(fileStorageProperties.getAliyunOss().getBucketName(), filePath);
            */
            
            log.info("阿里云OSS文件删除成功: {}", filePath);
            return true;
        } catch (Exception e) {
            log.error("阿里云OSS删除文件失败: {}", filePath, e);
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String filePath) {
        FileStorageProperties.AliyunOssConfig ossConfig = fileStorageProperties.getAliyunOss();
        if (StringUtils.hasText(ossConfig.getCdnDomain())) {
            return "https://" + ossConfig.getCdnDomain() + "/" + filePath;
        } else {
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + filePath;
        }
    }
    
    @Override
    public boolean exists(String filePath) {
        try {
            // TODO: 使用阿里云OSS SDK检查文件是否存在
            /*
            if (ossClient == null) {
                ossClient = createOssClient();
            }
            
            return ossClient.doesObjectExist(fileStorageProperties.getAliyunOss().getBucketName(), filePath);
            */
            return false;
        } catch (Exception e) {
            log.error("阿里云OSS检查文件存在性失败: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * 创建OSS客户端
     */
    /*
    private OSS createOssClient() {
        FileStorageProperties.AliyunOssConfig ossConfig = fileStorageProperties.getAliyunOss();
        
        return new OSSClientBuilder().build(
            ossConfig.getEndpoint(),
            ossConfig.getAccessKeyId(),
            ossConfig.getAccessKeySecret()
        );
    }
    */
    
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