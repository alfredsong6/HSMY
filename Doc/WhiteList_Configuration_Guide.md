# 接口白名单配置说明

## 功能概述

本系统实现了基于AntMatch风格的接口白名单功能，支持灵活的路径匹配规则，可以动态配置哪些接口不需要登录认证。

## AntMatch支持的通配符

### 1. 单个字符匹配 `?`
- `?` 匹配任意单个字符（除了路径分隔符 `/`）
- 示例：`/api/user?` 匹配 `/api/user1`、`/api/userA` 但不匹配 `/api/user12`

### 2. 任意字符匹配 `*`
- `*` 匹配0个或多个字符（除了路径分隔符 `/`）
- 示例：`/api/user*` 匹配 `/api/user`、`/api/user123`、`/api/userABC`

### 3. 路径匹配 `**`
- `**` 匹配0个或多个路径段（包含路径分隔符 `/`）
- 示例：`/api/**` 匹配 `/api/user`、`/api/user/profile`、`/api/system/config`

### 4. 字符集匹配 `[...]`
- `[abc]` 匹配方括号内的任意一个字符
- `[a-z]` 匹配a到z范围内的任意字符
- 示例：`/api/user[0-9]` 匹配 `/api/user0` 到 `/api/user9`

## 配置方式

### 1. application.yml配置（推荐）

```yaml
auth:
  whitelist:
    enabled: true    # 是否启用白名单功能
    paths:
      # 基本路径匹配
      - /api/auth/login
      - /api/auth/register
      
      # 使用*通配符
      - /api/public/*          # 匹配 /api/public/info, /api/public/data
      - /static/*              # 匹配所有静态资源一级目录
      
      # 使用**通配符
      - /api/open/**           # 匹配 /api/open 下的所有路径
      - /static/**             # 匹配所有静态资源
      - /webjars/**            # 匹配所有webjars资源
      
      # 使用?通配符
      - /api/test?             # 匹配 /api/test1, /api/testA 等
      
      # 复合匹配
      - /api/*/public/**       # 匹配 /api/user/public/info, /api/admin/public/data
      - /api/v[1-9]/auth/*     # 匹配 /api/v1/auth/login, /api/v2/auth/register
```

### 2. 动态API配置

系统提供了RESTful接口来动态管理白名单：

```bash
# 获取白名单配置
GET /api/system/whitelist/config

# 获取所有白名单路径
GET /api/system/whitelist/paths

# 添加白名单路径
POST /api/system/whitelist/add?path=/api/new/**

# 移除白名单路径  
POST /api/system/whitelist/remove?path=/api/old/**

# 启用/禁用白名单功能
POST /api/system/whitelist/toggle?enabled=true

# 检查指定路径是否在白名单中
GET /api/system/whitelist/check?path=/api/test/hello

# 测试当前请求路径
GET /api/system/whitelist/test-current
```

## 常用配置示例

### 完整的生产环境配置

```yaml
auth:
  whitelist:
    enabled: true
    paths:
      # 认证相关
      - /api/auth/login
      - /api/auth/register
      - /api/auth/logout
      - /api/auth/forgot-password
      - /api/auth/reset-password
      - /api/auth/verify-code
      
      # 公共接口
      - /api/public/**
      - /api/open/**
      
      # 静态资源
      - /static/**
      - /public/**
      - /webjars/**
      - /css/**
      - /js/**
      - /images/**
      - /favicon.ico
      
      # 健康检查
      - /health/**
      - /actuator/health
      
      # API文档（生产环境可移除）
      - /doc.html
      - /swagger-ui/**
      - /swagger-resources/**
      - /v2/api-docs
      - /v3/api-docs/**
      
      # 系统错误页面
      - /error
      
      # 文件上传/下载（根据需求）
      - /api/file/download/**
      - /api/file/preview/**
```

### 开发环境配置

```yaml
auth:
  whitelist:
    enabled: true
    paths:
      # 继承生产环境配置
      - /api/auth/**           # 所有认证接口
      - /api/public/**
      - /static/**
      - /health/**
      - /error
      - /favicon.ico
      
      # 开发工具
      - /actuator/**
      - /h2-console/**
      - /druid/**
      
      # API文档
      - /doc.html
      - /swagger-ui/**
      - /swagger-resources/**
      - /v*/api-docs/**
      
      # 测试接口
      - /api/test/**
      - /api/dev/**
      - /api/mock/**
```

## 高级配置示例

### 版本化API白名单

```yaml
auth:
  whitelist:
    enabled: true
    paths:
      # 匹配所有版本的公共接口
      - /api/v*/public/**      # /api/v1/public/*, /api/v2/public/*
      - /api/v[1-9]/auth/**    # /api/v1/auth/*, /api/v2/auth/*
      
      # 特定版本接口
      - /api/v1/legacy/**      # 只有v1版本的遗留接口
      - /api/v2/features/**    # 只有v2版本的新功能
```

### 多租户白名单

```yaml
auth:
  whitelist:
    enabled: true
    paths:
      # 租户公共接口
      - /api/*/public/**       # /api/tenant1/public/*, /api/tenant2/public/*
      - /api/*/auth/**         # 各租户认证接口
      
      # 系统级接口
      - /api/system/health
      - /api/system/status
```

### 移动端API白名单

```yaml
auth:
  whitelist:
    enabled: true
    paths:
      # 移动端专用接口
      - /api/mobile/auth/**
      - /api/mobile/public/**
      - /api/mobile/app-config
      
      # 通用接口
      - /api/common/**
      - /api/file/download/**
```

## 使用注意事项

### 1. 路径标准化
- 系统会自动标准化请求路径，移除多余的`/`和查询参数
- Context Path会被自动处理

### 2. 性能考虑
- 白名单检查在拦截器中进行，性能开销很小
- 建议将最常用的路径放在配置列表前面

### 3. 安全建议
- **生产环境**应该移除所有测试和开发相关的白名单
- **审慎配置**`/**`这样的宽泛匹配
- **定期审查**白名单配置，移除不需要的路径

### 4. 调试技巧
- 开启debug日志：`logging.level.com.hsmy: debug`
- 使用测试接口检查路径匹配：`/api/system/whitelist/check?path=xxx`
- 查看当前请求信息：`/api/system/whitelist/test-current`

## 动态管理示例

### JavaScript前端调用

```javascript
// 获取白名单配置
const getWhiteListConfig = async () => {
    const response = await fetch('/api/system/whitelist/config');
    return await response.json();
};

// 添加白名单路径
const addWhiteListPath = async (path) => {
    const response = await fetch(`/api/system/whitelist/add?path=${encodeURIComponent(path)}`, {
        method: 'POST'
    });
    return await response.json();
};

// 检查路径是否在白名单中
const checkPath = async (path) => {
    const response = await fetch(`/api/system/whitelist/check?path=${encodeURIComponent(path)}`);
    return await response.json();
};

// 禁用白名单（紧急情况下使用）
const disableWhiteList = async () => {
    const response = await fetch('/api/system/whitelist/toggle?enabled=false', {
        method: 'POST'
    });
    return await response.json();
};
```

### Java代码中使用

```java
@RestController
public class MyController {
    
    @Autowired
    private AuthWhiteListProperties whiteListProperties;
    
    // 运行时添加白名单
    @PostMapping("/add-temp-whitelist")
    public Result<String> addTempWhiteList(@RequestParam String path) {
        whiteListProperties.addWhiteListPath(path);
        return Result.success("添加成功");
    }
    
    // 检查路径是否在白名单中
    @GetMapping("/check-whitelist")
    public Result<Boolean> checkWhiteList(@RequestParam String path) {
        boolean inWhiteList = WhiteListUtil.isInWhiteList(path, whiteListProperties.getPaths());
        return Result.success(inWhiteList);
    }
}
```

## 故障排除

### 1. 路径不匹配
- 检查路径是否包含Context Path
- 确认通配符使用是否正确
- 使用测试接口验证路径匹配

### 2. 配置不生效
- 确认`auth.whitelist.enabled=true`
- 检查YAML格式是否正确
- 重启应用程序

### 3. 性能问题
- 避免过多的复杂匹配规则
- 将常用路径放在列表前面
- 考虑合并相似的路径规则

## 最佳实践

1. **最小权限原则**：只将必要的接口加入白名单
2. **分类管理**：按功能模块对白名单进行分类
3. **环境隔离**：不同环境使用不同的白名单配置
4. **定期审查**：定期检查和清理白名单配置
5. **监控日志**：关注白名单相关的访问日志