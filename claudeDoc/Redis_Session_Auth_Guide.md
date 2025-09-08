# Redis + Session 登录认证系统使用说明

## 系统架构概述

本系统实现了基于Redis的Session管理机制，不使用JWT，而是通过传统的SessionId + Redis存储的方式实现用户登录认证。

## 主要组件

### 1. Redis配置
- **RedisConfig.java**: Redis序列化配置
- **application.yml**: Redis连接配置

### 2. Session管理
- **SessionService**: Session管理接口
- **SessionServiceImpl**: Session管理实现类，负责创建、验证、刷新、删除Session

### 3. 登录拦截器
- **LoginInterceptor**: 拦截所有需要认证的请求
- **WebConfig**: 配置拦截器规则和跨域设置

### 4. 认证控制器
- **AuthController**: 提供登录、登出、获取用户信息接口

## API接口说明

### 登录接口 (不需要认证)
```
POST /api/auth/login
Content-Type: application/json

请求体：
{
    "loginAccount": "用户名/手机号/邮箱",
    "password": "密码"
}

响应：
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "sessionId": "abc123...",
        "userId": 1,
        "username": "testuser",
        "nickname": "测试用户",
        "phone": "13800138000",
        "email": "test@example.com"
    }
}
```

### 登出接口 (需要认证)
```
POST /api/auth/logout
X-Session-Id: [sessionId]

响应：
{
    "code": 200,
    "message": "操作成功",
    "data": "登出成功"
}
```

### 获取用户信息接口 (需要认证)
```
GET /api/auth/user-info
X-Session-Id: [sessionId]

响应：
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "nickname": "测试用户",
        // 其他用户字段...
    }
}
```

### 健康检查接口 (不需要认证)
```
GET /api/auth/health

响应：
{
    "code": 200,
    "message": "操作成功",
    "data": "服务正常"
}
```

## 认证机制

### Session传递方式
1. **请求头方式 (推荐)**：`X-Session-Id: [sessionId]`
2. **URL参数方式**：`?sessionId=[sessionId]`

### 拦截器配置
- **拦截所有路径**: `/**`
- **排除路径**:
  - `/api/auth/login` - 登录接口
  - `/api/auth/register` - 注册接口  
  - `/api/auth/logout` - 登出接口
  - `/api/auth/code` - 验证码接口
  - `/api/health` - 健康检查
  - `/error` - 错误页面
  - `/favicon.ico` - 图标文件

### Session配置
- **过期时间**: 7天
- **Redis Key格式**: `session:[sessionId]`
- **自动刷新**: 每次访问时自动刷新过期时间

## 使用示例

### 前端JavaScript调用示例

```javascript
// 1. 用户登录
const login = async (loginAccount, password) => {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            loginAccount,
            password
        })
    });
    
    const result = await response.json();
    if (result.code === 200) {
        // 存储sessionId到localStorage
        localStorage.setItem('sessionId', result.data.sessionId);
        return result.data;
    }
    throw new Error(result.message);
};

// 2. 发起需要认证的请求
const callAuthenticatedAPI = async (url, options = {}) => {
    const sessionId = localStorage.getItem('sessionId');
    
    const response = await fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            'X-Session-Id': sessionId
        }
    });
    
    const result = await response.json();
    
    // 检查是否未认证
    if (result.code === 401) {
        // 清除sessionId并跳转到登录页
        localStorage.removeItem('sessionId');
        window.location.href = '/login';
        return;
    }
    
    return result;
};

// 3. 获取当前用户信息
const getCurrentUser = () => callAuthenticatedAPI('/api/auth/user-info');

// 4. 用户登出
const logout = async () => {
    await callAuthenticatedAPI('/api/auth/logout', {
        method: 'POST'
    });
    localStorage.removeItem('sessionId');
    window.location.href = '/login';
};
```

## 后端使用工具类

```java
// 在Controller中获取当前用户ID
@RestController
public class SomeController {
    
    // 方法1：使用工具类
    @GetMapping("/some-api")
    public Result<String> someAPI() {
        Long currentUserId = UserContextUtil.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("用户未登录");
        }
        // 业务逻辑...
        return Result.success("成功");
    }
    
    // 方法2：使用工具类（强制要求登录）
    @GetMapping("/another-api")
    public Result<String> anotherAPI() {
        Long currentUserId = UserContextUtil.requireCurrentUserId(); // 未登录会抛异常
        // 业务逻辑...
        return Result.success("成功");
    }
    
    // 方法3：从Request中获取
    @GetMapping("/third-api")
    public Result<String> thirdAPI(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(LoginInterceptor.USER_ID_ATTRIBUTE);
        // 业务逻辑...
        return Result.success("成功");
    }
}
```

## 优势

1. **简单易懂**: 传统Session机制，概念清晰
2. **安全性高**: SessionId存储在Redis中，服务端可控
3. **易于管理**: 可以方便地查看、删除、统计在线用户
4. **支持集群**: 基于Redis的分布式Session
5. **自动过期**: Redis原生过期机制
6. **跨域支持**: 配置了完善的CORS设置

## 注意事项

1. **Redis依赖**: 系统依赖Redis服务，需要确保Redis正常运行
2. **Session清理**: Redis会自动清理过期Session
3. **并发访问**: 使用Redis保证了Session的线程安全
4. **前端存储**: 建议使用localStorage存储sessionId
5. **HTTPS**: 生产环境建议使用HTTPS传输sessionId

## 部署要求

1. **Redis服务**: 需要部署Redis服务器
2. **数据库**: 需要用户相关数据表
3. **Java 8+**: 项目基于Java 8开发
4. **SpringBoot 2.7.14**: 使用指定版本SpringBoot