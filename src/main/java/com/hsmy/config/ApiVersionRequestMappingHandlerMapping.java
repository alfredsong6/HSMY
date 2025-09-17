package com.hsmy.config;

import com.hsmy.annotation.ApiVersion;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * API版本处理的RequestMappingHandlerMapping
 * 
 * @author HSMY  
 * @date 2025/09/10
 */
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) {
            return null;
        }

        // 检查方法级别的版本注解
        ApiVersion methodVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        // 检查类级别的版本注解
        ApiVersion classVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);

        if (methodVersion != null || classVersion != null) {
            // 优先使用方法级别的版本，然后是类级别的版本
            String version = methodVersion != null ? methodVersion.value() : classVersion.value();
            
            // 获取原始路径模式
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            
            // 为每个路径模式添加版本前缀
            String[] versionedPatterns = patterns.stream()
                    .map(pattern -> "/api/" + version + pattern)
                    .toArray(String[]::new);
            
            // 创建新的RequestMappingInfo，包含版本前缀的路径
            return RequestMappingInfo.paths(versionedPatterns)
                    .methods(info.getMethodsCondition().getMethods().toArray(new RequestMethod[0]))
                    .params(info.getParamsCondition().getExpressions().toArray(new String[0]))
                    .headers(info.getHeadersCondition().getExpressions().toArray(new String[0]))
//                    .consumes(info.getConsumesCondition().getExpressions().toArray(new MediaType[0]))
//                    .produces(info.getProducesCondition().getExpressions().toArray(new MediaType[0]))
//                    .mappingName(info.getName())
                    .build();
        }

        return info;
    }
}