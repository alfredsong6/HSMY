package com.hsmy.config;

import com.hsmy.enums.StorageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {
    
    /**
     * 存储类型，默认本地存储
     */
    private StorageType type = StorageType.LOCAL;
    
    /**
     * 允许的文件类型
     */
    private String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    /**
     * 最大文件大小（字节），默认5MB
     */
    private Long maxFileSize = 5 * 1024 * 1024L;
    
    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();
    
    /**
     * 腾讯云COS配置
     */
    private TencentCosConfig tencentCos = new TencentCosConfig();
    
    /**
     * 阿里云OSS配置
     */
    private AliyunOssConfig aliyunOss = new AliyunOssConfig();
    
    @Data
    public static class LocalConfig {
        /**
         * 本地存储根路径
         */
        private String rootPath = "./uploads";
        
        /**
         * 访问路径前缀
         */
        private String urlPrefix = "/uploads";
    }
    
    @Data
    public static class TencentCosConfig {
        /**
         * SecretId
         */
        private String secretId;
        
        /**
         * SecretKey
         */
        private String secretKey;
        
        /**
         * 存储桶名称
         */
        private String bucketName;
        
        /**
         * 地域
         */
        private String region;
        
        /**
         * CDN域名
         */
        private String cdnDomain;
    }
    
    @Data
    public static class AliyunOssConfig {
        /**
         * AccessKey ID
         */
        private String accessKeyId;
        
        /**
         * AccessKey Secret
         */
        private String accessKeySecret;
        
        /**
         * 存储桶名称
         */
        private String bucketName;
        
        /**
         * 地域节点
         */
        private String endpoint;
        
        /**
         * CDN域名
         */
        private String cdnDomain;
    }
}