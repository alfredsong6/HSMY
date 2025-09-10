# API版本管理使用说明

## 概述

项目已实现完整的API版本管理系统，支持不同版本APP的兼容性。通过URL路径方式进行版本控制，格式为：`/api/{version}/{resource}`

## 支持的版本

### V1.0 (v1.0)
- **描述**: 初始版本
- **发布日期**: 2025-09-07
- **状态**: 稳定版本
- **访问方式**: `/api/v1.0/*`

### V1.1 (v1.1)
- **描述**: 功德系统优化版本
- **发布日期**: 2025-09-10
- **状态**: 当前最新版本
- **访问方式**: `/api/v1.1/*`
- **新特性**:
  - 详细余额信息返回
  - 优化兑换接口响应格式
  - 新增功德统计汇总接口
  - 增强版余额查询
  - 兑换前后余额对比

### V2.0 (v2.0)
- **描述**: 重大更新版本
- **状态**: 计划中
- **访问方式**: `/api/v2.0/*`

## API访问示例

### V1.0版本接口
```
GET /api/v1.0/merit/balance     # 获取余额信息(简化版)
POST /api/v1.0/merit/exchange   # 功德兑换(基础版)
GET /api/v1.0/merit/history     # 功德历史记录
```

### V1.1版本接口
```
GET /api/v1.1/merit/balance     # 获取余额信息(详细版)
POST /api/v1.1/merit/exchange   # 功德兑换(增强版)
GET /api/v1.1/merit/history     # 功德历史记录
GET /api/v1.1/merit/summary     # 功德统计汇总(新增)
```

## 版本管理接口

### 获取版本列表
```http
GET /api/v1.0/version/list
```

**响应示例**:
```json
{
  "code": 200,
  "message": "版本信息查询成功",
  "data": {
    "supportedVersions": [...],
    "currentVersion": "v1.1",
    "defaultVersion": "v1.0",
    "latestVersion": "v1.1"
  }
}
```

### 检查版本兼容性
```http
GET /api/v1.0/version/compatibility?clientVersion=v1.0
```

### 获取版本更新信息
```http
GET /api/v1.0/version/updates?currentVersion=v1.0
```

## 客户端使用指南

### 1. 版本检测
客户端启动时应该调用版本兼容性检查接口，确认当前版本是否受支持：

```javascript
// 检查版本兼容性
const response = await fetch('/api/v1.0/version/compatibility?clientVersion=v1.0');
const compatibility = await response.json();

if (!compatibility.data.compatible) {
  // 提示用户升级
  showUpdatePrompt(compatibility.data.latestVersion);
}
```

### 2. 版本路由
根据客户端版本选择对应的API路径：

```javascript
const API_VERSION = 'v1.0';  // 客户端版本
const BASE_URL = `/api/${API_VERSION}`;

// 调用对应版本的接口
const balance = await fetch(`${BASE_URL}/merit/balance`);
```

### 3. 响应处理
不同版本的接口可能返回不同格式的数据，需要适配处理：

```javascript
// V1.0版本余额响应
{
  "userId": 1,
  "totalMerit": 5000,
  "meritCoins": 5
}

// V1.1版本余额响应
{
  "userId": 1,
  "totalMerit": 5000,
  "meritCoins": 5,
  "todayMerit": 100,
  "weeklyMerit": 500,
  "monthlyMerit": 2000,
  "userLevel": 3,
  "apiVersion": "v1.1"
}
```

## 服务端开发指南

### 1. 添加版本注解
为控制器添加版本支持：

```java
@RestController
@RequestMapping("/merit")
@ApiVersion(ApiVersionConstant.V1_0)
public class MeritController {
    // ...
}
```

### 2. 创建新版本控制器
当需要支持新版本时，创建新的控制器：

```java
@RestController
@RequestMapping("/merit")
@ApiVersion(ApiVersionConstant.V1_1)
public class MeritControllerV1_1 {
    // 新版本的实现
}
```

### 3. 版本特定的VO类
为不同版本创建专门的VO类：

```java
// V1.1版本的兑换VO
@Data
@EqualsAndHashCode(callSuper = true)
public class ExchangeVOV1_1 extends ExchangeVO {
    private Integer exchangeType;
    private Boolean useCoupon;
    // ...
}
```

### 4. 数据转换
使用ApiVersionConverter进行版本间数据转换：

```java
// 转换为对应版本的响应格式
Object response = ApiVersionConverter.convertBalanceResponse(balanceData, "v1.1");
```

## 兼容性策略

1. **向后兼容**: 新版本保持对老版本的兼容
2. **渐进式升级**: 支持多版本共存
3. **废弃通知**: 通过接口返回废弃信息
4. **强制升级**: 对于安全更新，可要求强制升级

## 注意事项

1. URL中的版本号必须严格按照格式：`v1.0`, `v1.1`, `v2.0`
2. 同一个资源可以有多个版本的实现
3. 版本废弃时需要提前通知客户端
4. 测试时需要覆盖所有支持的版本