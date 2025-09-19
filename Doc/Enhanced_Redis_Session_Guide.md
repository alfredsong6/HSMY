# 优化后的Redis Session认证系统使用说明

## 更新内容概览

基于您的要求，我们对Redis Session认证系统进行了重大优化：

1. **登录成功后Redis存储完整用户信息**：不再仅存储用户ID，而是存储包含用户基础信息、统计信息、登录信息等的完整会话上下文
2. **校验SessionId时将用户信息添加到上下文**：拦截器从Redis取出完整用户信息后，将其添加到request属性中，方便后续使用

## 核心优化

### 1. 用户会话上下文对象 (`UserSessionContext`)

```java
public class UserSessionContext {
    // 基础用户信息
    private Long userId;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    
    // 用户统计信息  
    private Integer currentLevel;
    private Integer meritCoins;
    private Long totalMerit;
    
    // 会话信息
    private Date loginTime;
    private Date lastAccessTime;
    private String loginIp;
    private String userAgent;
    private String sessionId;
    private Boolean isAdmin;
    
    // 扩展字段
    private String roles;
    private String permissions;
}
```

### 2. SessionService优化

```java
// 创建Session时传入完整User对象和HttpServletRequest
String createSession(User user, HttpServletRequest request);

// 获取完整的用户会话上下文
UserSessionContext getUserSessionContext(String sessionId);

// 支持多端登录管理
List<String> getUserSessions(Long userId);
Integer kickOutUserSessions(Long userId);
```

### 3. LoginInterceptor增强

- 从Redis中获取完整的`UserSessionContext`
- 将用户ID和完整会话上下文都设置到request属性中
- 支持用户状态检查（账号是否被禁用）
- 自动更新最后访问时间

### 4. UserContextUtil工具类扩展

```java
// 获取完整会话上下文
UserSessionContext getCurrentUserSessionContext();

// 便捷方法获取具体用户信息
String getCurrentUsername();
String getCurrentNickname();
Integer getCurrentMeritCoins();
Long getCurrentTotalMerit();
Integer getCurrentLevel();
String getCurrentLoginIp();
Boolean isCurrentUserAdmin();

// 权限检查方法（可扩展）
Boolean hasPermission(String permission);
Boolean hasRole(String role);
```

## API接口更新

### 登录接口

```bash
POST /api/auth/login
Content-Type: application/json

# 请求体
{
    "loginAccount": "用户名/手机号/邮箱",
    "password": "password123"
}

# 响应（包含完整用户信息）
{
    "code": 200,
    "data": {
        "sessionId": "abc123_1694156400000",
        "userId": 1,
        "username": "testuser",
        "nickname": "测试用户",
        "phone": "13800138000",
        "email": "test@example.com"
    }
}
```

### 获取用户信息接口

```bash
GET /api/auth/user-info
X-Session-Id: abc123_1694156400000

# 响应（返回完整会话上下文）
{
    "code": 200,
    "data": {
        "userId": 1,
        "username": "testuser",
        "nickname": "测试用户",
        "phone": "13800138000",
        "email": "test@example.com",
        "currentLevel": 5,
        "meritCoins": 1500,
        "totalMerit": 10000,
        "loginTime": "2025-09-08T10:00:00",
        "lastAccessTime": "2025-09-08T12:30:00",
        "isAdmin": false
    }
}
```

### 新增会话管理接口

```bash
# 获取用户所有活跃Session
GET /api/auth/sessions
X-Session-Id: abc123_1694156400000

# 踢出其他登录会话（保留当前）
POST /api/auth/kick-other-sessions
X-Session-Id: abc123_1694156400000

# 管理员强制踢出用户所有会话
POST /api/auth/kick-user-sessions?userId=123
X-Session-Id: admin_session_id
```

## Redis存储结构优化

### Session存储

```
Key: session:abc123_1694156400000
Value: UserSessionContext对象（JSON序列化）
TTL: 7天
```

### 用户Session列表（支持多端登录）

```
Key: user:sessions:123
Value: Set集合 ["session1", "session2", "session3"]
TTL: 7天
```

## 使用示例

### 后端Controller中使用

```java
@RestController
public class BusinessController {
    
    // 方式1：获取完整会话上下文
    @GetMapping("/user-profile")
    public Result<Object> getUserProfile() {
        UserSessionContext context = UserContextUtil.getCurrentUserSessionContext();
        if (context == null) {
            return Result.error("用户未登录");
        }
        
        // 直接使用上下文中的信息，无需再次查询数据库
        Map<String, Object> profile = new HashMap<>();
        profile.put("username", context.getUsername());
        profile.put("level", context.getCurrentLevel());
        profile.put("meritCoins", context.getMeritCoins());
        profile.put("loginTime", context.getLoginTime());
        
        return Result.success(profile);
    }
    
    // 方式2：使用便捷方法
    @GetMapping("/merit-info")
    public Result<Object> getMeritInfo() {
        Long userId = UserContextUtil.getCurrentUserId();
        String username = UserContextUtil.getCurrentUsername();
        Integer meritCoins = UserContextUtil.getCurrentMeritCoins();
        Long totalMerit = UserContextUtil.getCurrentTotalMerit();
        Integer level = UserContextUtil.getCurrentLevel();
        
        // 这些信息都来自Redis缓存，无需数据库查询
        Map<String, Object> info = new HashMap<>();
        info.put("userId", userId);
        info.put("username", username);
        info.put("meritCoins", meritCoins);
        info.put("totalMerit", totalMerit);
        info.put("level", level);
        
        return Result.success(info);
    }
    
    // 方式3：权限检查
    @GetMapping("/admin-data")
    public Result<Object> getAdminData() {
        if (!UserContextUtil.isCurrentUserAdmin()) {
            return Result.error("无管理员权限");
        }
        
        // 管理员操作...
        return Result.success("管理员数据");
    }
}
```

### 前端JavaScript使用

```javascript
// 登录并获取完整用户信息
const login = async (loginAccount, password) => {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ loginAccount, password })
    });
    
    const result = await response.json();
    if (result.code === 200) {
        // 存储sessionId
        localStorage.setItem('sessionId', result.data.sessionId);
        
        // 存储基础用户信息（可选）
        localStorage.setItem('userInfo', JSON.stringify({
            userId: result.data.userId,
            username: result.data.username,
            nickname: result.data.nickname
        }));
        
        return result.data;
    }
    throw new Error(result.message);
};

// 获取完整用户信息（包含实时数据）
const getUserInfo = async () => {
    const sessionId = localStorage.getItem('sessionId');
    const response = await fetch('/api/auth/user-info', {
        headers: { 'X-Session-Id': sessionId }
    });
    
    const result = await response.json();
    if (result.code === 200) {
        // 更新本地缓存的用户信息
        localStorage.setItem('userInfo', JSON.stringify(result.data));
        return result.data;
    }
    
    if (result.code === 401) {
        // 登录过期，跳转到登录页
        localStorage.clear();
        window.location.href = '/login';
    }
    
    throw new Error(result.message);
};

// 踢出其他设备的登录
const kickOtherSessions = async () => {
    const sessionId = localStorage.getItem('sessionId');
    const response = await fetch('/api/auth/kick-other-sessions', {
        method: 'POST',
        headers: { 'X-Session-Id': sessionId }
    });
    
    const result = await response.json();
    if (result.code === 200) {
        alert(result.data);
    } else {
        throw new Error(result.message);
    }
};
```

## 性能优化优势

### 1. 减少数据库查询

- **优化前**：每次请求都需要根据userId查询用户基本信息和统计信息
- **优化后**：用户信息直接从Redis中获取，大大减少数据库压力

### 2. 提升响应速度

- **优化前**：需要多次数据库查询才能获取完整用户信息
- **优化后**：一次Redis查询即可获取所有需要的用户信息

### 3. 支持多端登录管理

- 用户可以在多个设备上同时登录
- 支持查看所有活跃会话
- 支持踢出指定设备的登录会话

### 4. 增强安全性

- 记录登录IP、User-Agent等信息，便于安全审计
- 支持管理员强制下线用户
- 自动检查用户状态，被禁用用户无法继续使用

## 扩展特性

### 1. 权限系统集成

```java
// 在UserSessionContext中可以存储用户角色和权限
context.setRoles("user,vip");
context.setPermissions("read,write,delete");

// 在UserContextUtil中检查权限
public static Boolean hasPermission(String permission) {
    UserSessionContext context = getCurrentUserSessionContext();
    if (context == null || context.getPermissions() == null) {
        return false;
    }
    
    return Arrays.asList(context.getPermissions().split(","))
                 .contains(permission);
}
```

### 2. 用户信息实时同步

```java
// 当用户信息更新时，同步更新所有活跃Session
@Service
public class UserSyncService {
    
    @Autowired
    private SessionService sessionService;
    
    public void syncUserInfo(Long userId, User updatedUser) {
        // 获取用户所有活跃Session
        List<String> sessions = sessionService.getUserSessions(userId);
        
        // 更新每个Session中的用户信息
        for (String sessionId : sessions) {
            sessionService.updateSessionUser(sessionId, updatedUser);
        }
    }
}
```

### 3. 会话监控统计

```java
// 可以轻松实现在线用户统计、用户行为分析等功能
@GetMapping("/online-users")
public Result<Integer> getOnlineUserCount() {
    // 通过Redis统计所有活跃Session数量
    Set<String> keys = redisTemplate.keys("session:*");
    return Result.success(keys.size());
}
```

## 注意事项

1. **Redis存储空间**：由于存储完整用户信息，Redis占用空间会增加，需要适当调整内存配置
2. **数据一致性**：用户信息更新后，需要同步更新Redis中的Session数据
3. **序列化性能**：使用Jackson序列化UserSessionContext，性能较好但需要注意字段的序列化配置
4. **Session清理**：Redis过期机制会自动清理过期Session，但建议定期清理无效的用户Session列表

## 总结

通过这次优化，我们实现了：

✅ **完整用户信息存储**：Redis中存储包含用户基础信息、统计信息、会话信息的完整上下文  
✅ **用户信息上下文注入**：拦截器自动将用户信息添加到request上下文中  
✅ **性能大幅提升**：减少了大量数据库查询，提升了接口响应速度  
✅ **功能更加完善**：支持多端登录管理、会话监控、权限检查等高级特性  
✅ **使用更加便捷**：提供了丰富的工具方法，简化了业务代码  

这套优化后的Session系统既保持了传统Session的简单易懂，又具备了现代应用所需的高性能和丰富功能。