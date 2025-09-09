package com.hsmy.service;

import com.hsmy.config.FileStorageProperties;
import com.hsmy.enums.StorageType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 文件存储服务工厂
 */
@Service
@RequiredArgsConstructor
public class FileStorageServiceFactory {
    
    private final ApplicationContext applicationContext;
    private final FileStorageProperties fileStorageProperties;
    
    /**
     * 获取当前配置的文件存储服务
     */
    public FileStorageService getFileStorageService() {
        StorageType storageType = fileStorageProperties.getType();
        
        switch (storageType) {
            case LOCAL:
                return applicationContext.getBean("localFileStorageService", FileStorageService.class);
            case TENCENT_COS:
                return applicationContext.getBean("tencentCosFileStorageService", FileStorageService.class);
            case ALIYUN_OSS:
                return applicationContext.getBean("aliyunOssFileStorageService", FileStorageService.class);
            default:
                throw new IllegalArgumentException("Unsupported storage type: " + storageType);
        }
    }
}