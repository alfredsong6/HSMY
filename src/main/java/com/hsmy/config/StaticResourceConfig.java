package com.hsmy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web静态资源配置
 */
@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {
    
    private final FileStorageProperties fileStorageProperties;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地文件访问路径
        if (fileStorageProperties.getType() == com.hsmy.enums.StorageType.LOCAL) {
            registry.addResourceHandler("/api/file/uploads/**")
                   .addResourceLocations("file:" + fileStorageProperties.getLocal().getRootPath() + "/");
        }
    }
}