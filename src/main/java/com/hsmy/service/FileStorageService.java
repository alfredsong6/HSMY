package com.hsmy.service;

import com.hsmy.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {
    
    /**
     * 上传文件
     * 
     * @param file 文件
     * @param folder 文件夹路径
     * @return 上传结果
     * @throws IOException IO异常
     */
    FileUploadResult uploadFile(MultipartFile file, String folder) throws IOException;
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 获取文件访问URL
     * 
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getFileUrl(String filePath);
    
    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean exists(String filePath);
}