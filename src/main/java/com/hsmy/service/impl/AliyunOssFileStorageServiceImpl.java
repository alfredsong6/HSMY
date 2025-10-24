package com.hsmy.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
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
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 阿里云OSS存储服务实现
 */
@Slf4j
@Service("aliyunOssFileStorageService")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.storage.type", havingValue = "aliyun-oss")
public class AliyunOssFileStorageServiceImpl implements FileStorageService {
    
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
        
        // 构建存储路径
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String objectKey = folder + "/" + datePath + "/" + newFileName;
        
        FileStorageProperties.AliyunOssConfig ossConfig = fileStorageProperties.getAliyunOss();
        OSS ossClient = null;
        try {
            ossClient = createOssClient();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            if (StringUtils.hasText(file.getContentType())) {
                metadata.setContentType(file.getContentType());
            }
            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        ossConfig.getBucketName(),
                        objectKey,
                        inputStream);
                putObjectRequest.setMetadata(metadata);
                ossClient.putObject(putObjectRequest);
            }
        } catch (OSSException oe) {
            log.error("阿里云OSS上传失败，返回错误: code={}, message={}, requestId={}, hostId={}",
                    oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId(), oe.getHostId());
            throw new IOException("文件上传失败: " + oe.getErrorMessage(), oe);
        } catch (ClientException ce) {
            log.error("阿里云OSS上传失败，客户端异常: {}", ce.getMessage(), ce);
            throw new IOException("文件上传失败: 网络或客户端异常", ce);
        } catch (Exception e) {
            log.error("阿里云OSS上传失败", e);
            throw new IOException("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        
        // 计算文件MD5
        String md5;
        try (InputStream md5Stream = file.getInputStream()) {
            md5 = SecureUtil.md5(md5Stream);
        }
        
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
        FileStorageProperties.AliyunOssConfig ossConfig = fileStorageProperties.getAliyunOss();
        OSS ossClient = null;
        try {
            ossClient = createOssClient();
            ossClient.deleteObject(ossConfig.getBucketName(), filePath);
            log.info("阿里云OSS文件删除成功: {}", filePath);
            return true;
        } catch (OSSException oe) {
            log.error("阿里云OSS删除文件失败: {}, code={}, message={}", filePath, oe.getErrorCode(), oe.getErrorMessage(), oe);
            return false;
        } catch (ClientException ce) {
            log.error("阿里云OSS删除文件失败，客户端异常: {}", filePath, ce);
            return false;
        } catch (Exception e) {
            log.error("阿里云OSS删除文件失败: {}", filePath, e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
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
        FileStorageProperties.AliyunOssConfig ossConfig = fileStorageProperties.getAliyunOss();
        OSS ossClient = null;
        try {
            ossClient = createOssClient();
            return ossClient.doesObjectExist(ossConfig.getBucketName(), filePath);
        } catch (OSSException oe) {
            log.error("阿里云OSS检查文件存在性失败: {}, code={}, message={}", filePath, oe.getErrorCode(), oe.getErrorMessage(), oe);
            return false;
        } catch (ClientException ce) {
            log.error("阿里云OSS检查文件存在性失败: {}", filePath, ce);
            return false;
        } catch (Exception e) {
            log.error("阿里云OSS检查文件存在性失败: {}", filePath, e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * 创建OSS客户端
     */
    private OSS createOssClient() {
        FileStorageProperties.AliyunOssConfig ossConfig = fileStorageProperties.getAliyunOss();
        return new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
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
