package com.hsmy.dto;

import lombok.Data;

/**
 * 文件上传结果DTO
 */
@Data
public class FileUploadResult {
    
    /**
     * 文件访问URL
     */
    private String url;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 存储路径
     */
    private String filePath;
    
    /**
     * 文件MD5
     */
    private String md5;
}