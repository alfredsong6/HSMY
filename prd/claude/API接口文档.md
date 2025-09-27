# HSMY 项目 API 接口文档

## 版本信息
- **API版本**: v1.0 / v1.1
- **文档更新日期**: 2025-09-27
- **项目名称**: HSMY（慧思木鱼）功德管理系统

## 系统概述

HSMY是一个集成了敲木鱼功德、典籍阅读、冥想练习、商城购物等功能的佛教修行管理系统。系统采用RESTful API设计，支持用户注册登录、功德积累兑换、典籍购买阅读、排行榜等核心功能。

## 通用说明

### 请求格式
- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **请求方法**: 支持GET、POST、PUT、DELETE

### 认证方式
系统采用Session认证机制：
- 登录成功后返回sessionId
- 后续请求需在Header中携带：`Authorization: Bearer {token}`

### 通用响应格式
所有接口都返回以下统一格式：

```json
{
  "code": 200,          // 状态码: 200-成功, 401-未登录, 403-无权限, 500-服务器错误
  "message": "操作成功",  // 响应消息
  "data": {}            // 响应数据，具体结构因接口而异
}
```

### 状态码说明
- **200**: 操作成功
- **401**: 未登录或token失效
- **403**: 无权限访问
- **404**: 资源不存在
- **500**: 服务器内部错误

---

## 1. 认证模块 (AuthController)

### 1.1 发送验证码
- **接口**: `POST /auth/send-code`
- **功能**: 发送短信或邮箱验证码
- **请求参数**:
```json
{
  "account": "13888888888",           // 手机号或邮箱
  "accountType": "phone",             // 账号类型: phone/email
  "businessType": "register"          // 业务类型: register/login
}
```
- **响应示例**:
```json
{
  "code": 200,
  "message": "验证码已发送，请查收",
  "data": "验证码已发送，请查收"
}
```

### 1.2 验证码注册
- **接口**: `POST /auth/register-by-code`
- **功能**: 通过验证码注册新用户
- **请求参数**:
```json
{
  "account": "13888888888",           // 手机号或邮箱
  "code": "123456",                   // 验证码
  "nickname": "用户昵称"               // 可选昵称
}
```
- **响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "sessionId": "sess_123456789",
    "token": "sess_123456789",
    "tokenType": "Bearer",
    "userId": 123,
    "username": "user_123",
    "nickname": "用户昵称",
    "phone": "13888888888",
    "email": null
  }
}
```

### 1.3 用户登录
- **接口**: `POST /auth/login`
- **功能**: 支持密码登录和验证码登录
- **请求参数**:
```json
{
  "loginAccount": "13888888888",      // 登录账号
  "loginType": "password",            // 登录方式: password/code
  "password": "123456",               // 密码（密码登录时必填）
  "code": "654321"                    // 验证码（验证码登录时必填）
}
```
- **响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "sessionId": "sess_123456789",
    "token": "sess_123456789",
    "tokenType": "Bearer",
    "userId": 123,
    "username": "user_123",
    "nickname": "用户昵称",
    "phone": "13888888888",
    "email": "user@example.com"
  }
}
```

### 1.4 用户登出
- **接口**: `POST /auth/logout`
- **功能**: 退出登录，销毁当前会话
- **认证**: 需要登录
- **响应示例**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": "登出成功"
}
```

### 1.5 获取用户会话列表
- **接口**: `GET /auth/sessions`
- **功能**: 获取用户所有活跃的登录会话
- **认证**: 需要登录
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": ["sess_123456789", "sess_987654321"]
}
```

### 1.6 踢出其他会话
- **接口**: `POST /auth/kick-other-sessions`
- **功能**: 保留当前会话，踢出其他所有登录会话
- **认证**: 需要登录
- **响应示例**:
```json
{
  "code": 200,
  "message": "成功踢出 2 个其他登录会话",
  "data": "成功踢出 2 个其他登录会话"
}
```

### 1.7 健康检查
- **接口**: `GET /auth/health`
- **功能**: 服务健康状态检查
- **认证**: 无需登录
- **响应示例**:
```json
{
  "code": 200,
  "message": "服务正常",
  "data": "服务正常"
}
```

---

## 2. 用户模块 (UserController)

### 2.1 获取用户信息
- **接口**: `GET /user/info/{userId}` 或 `GET /user/self/info`
- **功能**: 获取指定用户或当前用户信息
- **认证**: 需要登录
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123,
    "username": "user_123",
    "nickname": "用户昵称",
    "phone": "13888888888",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "status": 1,
    "createTime": "2025-09-01T10:00:00"
  }
}
```

### 2.2 更新用户信息
- **接口**: `PUT /user/update`
- **功能**: 更新用户基本信息
- **认证**: 需要登录
- **请求参数**:
```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/new_avatar.jpg"
}
```

### 2.3 修改密码
- **接口**: `POST /user/changePassword`
- **功能**: 修改用户密码，修改后会踢出所有旧会话并返回新token
- **认证**: 需要登录
- **请求参数**:
```json
{
  "oldPassword": "123456",
  "newPassword": "654321",
  "confirmPassword": "654321"
}
```

### 2.4 初始化密码
- **接口**: `POST /user/initializePassword`
- **功能**: 为无密码账户初始化密码
- **认证**: 需要登录
- **请求参数**:
```json
{
  "password": "123456",
  "confirmPassword": "123456"
}
```

### 2.5 通过短信重置密码
- **接口**: `POST /user/resetPasswordWithSms`
- **功能**: 通过短信验证码重置密码
- **请求参数**:
```json
{
  "phone": "13888888888",
  "code": "123456",
  "password": "newpass123",
  "confirmPassword": "newpass123"
}
```

### 2.6 检查用户名/手机号/邮箱是否存在
- **接口**: 
  - `GET /user/check/username?username={username}`
  - `GET /user/check/phone?phone={phone}`
  - `GET /user/check/email?email={email}`
- **功能**: 检查账号信息是否已被注册
- **认证**: 无需登录

---

## 3. 敲击模块 (KnockController)

### 3.1 手动敲击
- **接口**: `POST /knock/manual`
- **功能**: 手动敲击木鱼获得功德
- **认证**: 需要登录
- **请求参数**:
```json
{
  "knockCount": 10,                   // 敲击次数
  "knockSound": "default",            // 敲击音效
  "sessionDuration": 60               // 会话持续时间（秒）
}
```
- **响应示例**:
```json
{
  "code": 200,
  "message": "敲击成功",
  "data": {
    "knockCount": 10,
    "meritGained": 10,
    "totalMerit": 1000,
    "sessionId": "knock_sess_123"
  }
}
```

### 3.2 开始自动敲击
- **接口**: `POST /knock/auto/start`
- **功能**: 开始自动敲击会话
- **认证**: 需要登录
- **请求参数**:
```json
{
  "duration": 300,                    // 持续时间（秒）
  "knockInterval": 1000,              // 敲击间隔（毫秒）
  "knockSound": "default"             // 敲击音效
}
```

### 3.3 停止自动敲击
- **接口**: `POST /knock/auto/stop`
- **功能**: 停止自动敲击会话并结算功德
- **认证**: 需要登录
- **请求参数**:
```json
{
  "sessionId": "auto_knock_sess_123", // 会话ID
  "actualDuration": 250               // 实际持续时间
}
```

### 3.4 自动敲击心跳
- **接口**: `POST /knock/auto/heartbeat`
- **功能**: 维持自动敲击会话活跃状态
- **认证**: 需要登录
- **请求参数**:
```json
{
  "sessionId": "auto_knock_sess_123", // 会话ID
  "currentKnockCount": 150            // 当前敲击次数
}
```

### 3.5 获取敲击统计
- **接口**: `GET /knock/stats`
- **功能**: 获取用户敲击统计数据
- **认证**: 需要登录
- **响应示例**:
```json
{
  "code": 200,
  "message": "获取统计成功",
  "data": {
    "todayKnocks": 100,
    "totalKnocks": 5000,
    "todayMerit": 100,
    "totalMerit": 5000,
    "averageDaily": 167,
    "longestStreak": 15
  }
}
```

### 3.6 获取周期统计
- **接口**: `GET /knock/stats/periods?referenceDate={date}`
- **功能**: 获取按周期的敲击统计
- **认证**: 需要登录
- **参数**: `referenceDate` - 参考日期（可选）

### 3.7 获取自动敲击状态
- **接口**: `GET /knock/auto/status`
- **功能**: 获取当前自动敲击会话状态
- **认证**: 需要登录

---

## 4. 冥想模块 (MeditationController)

### 4.1 购买冥想订阅
- **接口**: `POST /meditation/subscription/purchase`
- **功能**: 购买冥想功能订阅
- **认证**: 需要登录
- **请求参数**:
```json
{
  "subscriptionType": "monthly",      // 订阅类型: monthly/yearly
  "paymentMethod": "merit_coins"      // 支付方式
}
```

### 4.2 获取订阅状态
- **接口**: `GET /meditation/subscription/status`
- **功能**: 获取用户冥想订阅状态
- **认证**: 需要登录

### 4.3 开始冥想会话
- **接口**: `POST /meditation/session/start`
- **功能**: 开始一次冥想会话
- **认证**: 需要登录
- **请求参数**:
```json
{
  "duration": 600,                    // 计划时长（秒）
  "meditationType": "mindfulness",    // 冥想类型
  "backgroundMusic": "nature_sounds"  // 背景音乐
}
```

### 4.4 完成冥想会话
- **接口**: `POST /meditation/session/finish`
- **功能**: 完成冥想会话并记录
- **认证**: 需要登录
- **请求参数**:
```json
{
  "sessionId": "meditation_sess_123", // 会话ID
  "actualDuration": 580,              // 实际时长
  "completionRate": 0.97              // 完成率
}
```

### 4.5 放弃冥想会话
- **接口**: `POST /meditation/session/discard`
- **功能**: 放弃当前冥想会话
- **认证**: 需要登录

### 4.6 获取冥想统计摘要
- **接口**: `GET /meditation/stats/summary`
- **功能**: 获取冥想统计摘要
- **认证**: 需要登录

### 4.7 获取月度冥想统计
- **接口**: `GET /meditation/stats/month?month={month}`
- **功能**: 获取指定月份的冥想统计
- **认证**: 需要登录

### 4.8 获取/更新冥想配置
- **接口**: 
  - `GET /meditation/config/default` - 获取默认配置
  - `PUT /meditation/config/default` - 更新默认配置
- **功能**: 管理用户冥想偏好设置
- **认证**: 需要登录

---

## 5. 典籍模块 (ScriptureController)

### 5.1 获取典籍列表
- **接口**: `GET /scripture/list`
- **功能**: 获取典籍列表，支持多种查询条件
- **认证**: 可选（登录后显示购买状态）
- **查询参数**:
  - `keyword` - 搜索关键词
  - `scriptureType` - 典籍类型
  - `isHot` - 是否热门（1-是）
  - `minPrice/maxPrice` - 价格范围
  - `difficultyLevel` - 难度等级
  - `tag` - 标签

### 5.2 获取典籍详情
- **接口**: `GET /scripture/{scriptureId}`
- **功能**: 获取指定典籍的详细信息
- **认证**: 可选
- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123,
    "scriptureTitle": "金刚经",
    "scriptureType": "经典",
    "description": "佛教重要经典...",
    "price": 100,
    "difficultyLevel": 3,
    "isPurchased": true,
    "isPurchaseValid": true
  }
}
```

### 5.3 获取热门典籍
- **接口**: `GET /scripture/hot`
- **功能**: 获取热门典籍列表
- **认证**: 无需登录

### 5.4 按类型获取典籍
- **接口**: `GET /scripture/type/{scriptureType}`
- **功能**: 根据类型获取典籍列表
- **认证**: 无需登录

### 5.5 搜索典籍
- **接口**: `GET /scripture/search?keyword={keyword}`
- **功能**: 根据关键词搜索典籍
- **认证**: 无需登录

### 5.6 记录阅读行为
- **接口**: `POST /scripture/{scriptureId}/read`
- **功能**: 记录用户阅读典籍的行为
- **认证**: 需要登录且已购买

---

## 6. 功德模块 (MeritController)

### 6.1 获取功德余额
- **接口**: `POST /merit/balance`
- **功能**: 获取用户详细功德余额信息（V1.1增强版）
- **认证**: 需要登录
- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "userId": 123,
    "totalMerit": 5000,
    "meritCoins": 1000,
    "todayMerit": 100,
    "weeklyMerit": 500,
    "monthlyMerit": 2000,
    "apiVersion": "v1.1"
  }
}
```

### 6.2 功德兑换功德币
- **接口**: `POST /merit/exchange`
- **功能**: 将功德兑换为功德币（V1.1增强版，含兑换前后对比）
- **认证**: 需要登录
- **请求参数**:
```json
{
  "exchangeAmount": 100,              // 兑换的功德数量
  "exchangeRate": 1                   // 兑换比率（功德:功德币）
}
```

### 6.3 获取功德历史记录
- **接口**: `GET /merit/history`
- **功能**: 获取功德获取历史记录
- **认证**: 需要登录
- **查询参数**:
  - `startDate` - 开始日期
  - `endDate` - 结束日期
  - `pageNum` - 页码（默认1）
  - `pageSize` - 每页大小（默认10）

### 6.4 获取功德统计汇总
- **接口**: `GET /merit/summary`
- **功能**: 获取功德统计汇总信息（V1.1新增）
- **认证**: 需要登录

### 6.5 获取日功德统计
- **接口**: `GET /merit/daily?date={date}`
- **功能**: 获取指定日期的功德统计
- **认证**: 需要登录

### 6.6 获取功德统计
- **接口**: `GET /merit/stats/{userId}`
- **功能**: 获取指定用户的功德统计
- **认证**: 需要登录

---

## 7. 商城模块 (ShopController)

### 7.1 获取道具列表
- **接口**: `GET /shop/items`
- **功能**: 获取商城道具列表
- **认证**: 无需登录
- **查询参数**:
  - `itemType` - 道具类型
  - `category` - 道具分类

### 7.2 获取限时道具
- **接口**: `GET /shop/items/limited`
- **功能**: 获取限时道具列表
- **认证**: 无需登录

### 7.3 获取道具详情
- **接口**: `GET /shop/items/{itemId}`
- **功能**: 获取指定道具的详细信息
- **认证**: 无需登录

### 7.4 购买道具
- **接口**: `POST /shop/purchase`
- **功能**: 使用功德币购买道具
- **认证**: 需要登录
- **请求参数**:
  - `itemId` - 道具ID
  - `quantity` - 购买数量（默认1）
- **响应示例**:
```json
{
  "code": 200,
  "message": "购买成功",
  "data": {
    "success": true,
    "orderNo": "ORD_20250927_123456",
    "itemId": 123,
    "itemName": "祈福香",
    "quantity": 1,
    "totalPrice": 100,
    "remainingCoins": 900
  }
}
```

---

## 8. 排行榜模块 (RankingController)

### 8.1 获取日榜
- **接口**: `GET /rankings/daily?limit={limit}`
- **功能**: 获取日榜排行数据
- **认证**: 无需登录
- **参数**: `limit` - 查询条数（默认100）

### 8.2 获取周榜
- **接口**: `GET /rankings/weekly?limit={limit}`
- **功能**: 获取周榜排行数据
- **认证**: 无需登录

### 8.3 获取总榜
- **接口**: `GET /rankings/total?limit={limit}`
- **功能**: 获取总榜排行数据
- **认证**: 无需登录

### 8.4 获取用户排名信息
- **接口**: `GET /rankings/user/{userId}` 或 `GET /rankings/my`
- **功能**: 获取指定用户或当前用户在各榜单中的排名
- **认证**: 需要登录（/my接口）
- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "userId": 123,
    "dailyRanking": {
      "rank": 15,
      "score": 100,
      "rankingType": "DAILY"
    },
    "weeklyRanking": {
      "rank": 8,
      "score": 500,
      "rankingType": "WEEKLY"
    },
    "totalRanking": {
      "rank": 25,
      "score": 5000,
      "rankingType": "TOTAL"
    }
  }
}
```

---

## 9. 其他模块

### 9.1 文件上传 (FileController)
- **接口**: `POST /file/upload`
- **功能**: 文件上传（头像、图片等）
- **认证**: 需要登录

### 9.2 头像管理 (AvatarController)
- **接口**: `PUT /avatar/update`
- **功能**: 更新用户头像
- **认证**: 需要登录

### 9.3 用户道具 (UserItemController)
- **功能**: 管理用户拥有的道具

### 9.4 任务系统 (TaskController)
- **功能**: 每日任务、成就系统

### 9.5 用户设置 (UserSettingController)
- **功能**: 用户个性化设置

### 9.6 捐赠功能 (DonationController)
- **功能**: 功德捐赠相关功能

### 9.7 成就系统 (AchievementController)
- **功能**: 用户成就管理

---

## 错误处理

### 常见错误码
- **400**: 请求参数错误
- **401**: 未登录或认证失效
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

### 错误响应示例
```json
{
  "code": 401,
  "message": "您尚未登录，请先登录",
  "data": null
}
```

---

## API版本说明

系统支持API版本控制，通过@ApiVersion注解标识：

- **V1_0**: 基础版本
- **V1_1**: 增强版本（功德模块增加了更详细的统计信息和兑换对比功能）

不同版本的接口可能在响应数据结构上有所差异，请注意接口文档中的版本标识。

---

## 安全说明

1. **认证机制**: 基于Session的认证，登录后获得sessionId作为后续请求的凭证
2. **权限控制**: 接口分为公开接口和需要登录的接口
3. **参数验证**: 所有输入参数都会进行格式验证和安全检查
4. **敏感信息**: 密码等敏感信息采用MD5加密存储
5. **会话管理**: 支持多端登录和会话管理功能

---

## 开发测试

### 测试环境
- **Base URL**: `http://localhost:8080/api/v1`
- **文档工具**: 建议使用Postman或类似工具进行接口测试

### 测试流程
1. 调用发送验证码接口获取验证码
2. 使用验证码注册账户
3. 登录获取认证token
4. 使用token调用需要认证的接口

---

*本文档会随着系统功能的更新而持续完善，最新版本请以实际代码为准。*