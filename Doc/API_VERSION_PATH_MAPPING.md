# API版本路径映射示例

## 概述

本项目实现了基于URL路径的API版本控制系统。通过自定义的`ApiVersionRequestMappingHandlerMapping`，所有带有`@ApiVersion`注解的控制器都会自动添加版本前缀。

## 路径映射机制

### 1. 原理
- 控制器使用`@ApiVersion(ApiVersionConstant.V1_0)`注解
- 原始路径：`@RequestMapping("/merit")`
- 映射后路径：`/api/v1.0/merit`

### 2. 版本前缀规则
```
原始控制器路径 + @ApiVersion = 最终API路径

例如：
@RequestMapping("/merit") + @ApiVersion("v1.0") = /api/v1.0/merit/*
@RequestMapping("/test") + @ApiVersion("v1.1") = /api/v1.1/test/*
```

## 实际路径映射示例

### V1.0版本接口
```
控制器: MeritController
注解: @ApiVersion(ApiVersionConstant.V1_0) // "v1.0"
原始路径: @RequestMapping("/merit")

映射结果:
GET /api/v1.0/merit/balance          -> 获取余额
POST /api/v1.0/merit/exchange        -> 功德兑换
GET /api/v1.0/merit/history          -> 功德历史
GET /api/v1.0/merit/today/{userId}   -> 今日功德
```

### V1.1版本接口
```
控制器: MeritControllerV1_1
注解: @ApiVersion(ApiVersionConstant.V1_1) // "v1.1"
原始路径: @RequestMapping("/merit")

映射结果:
GET /api/v1.1/merit/balance          -> 获取余额(增强版)
POST /api/v1.1/merit/exchange        -> 功德兑换(增强版)
GET /api/v1.1/merit/summary          -> 功德统计汇总(新增)
```

### 测试接口示例
```
V1.0测试接口:
GET /api/v1.0/test/version           -> 版本信息
GET /api/v1.0/test/user/{userId}     -> 用户信息

V1.1测试接口:  
GET /api/v1.1/test/version           -> 版本信息(增强版)
GET /api/v1.1/test/user/{userId}     -> 用户信息(增强版)
GET /api/v1.1/test/user/{userId}/details -> 用户详细信息(新增)
```

## 版本管理接口
```
GET /api/v1.0/version/list           -> 获取支持的版本列表
GET /api/v1.0/version/compatibility  -> 检查版本兼容性
GET /api/v1.0/version/updates        -> 获取版本更新信息
```

## 客户端调用示例

### JavaScript调用
```javascript
// V1.0版本调用
const baseUrlV10 = '/api/v1.0';
const balanceV10 = await fetch(`${baseUrlV10}/merit/balance`);

// V1.1版本调用  
const baseUrlV11 = '/api/v1.1';
const balanceV11 = await fetch(`${baseUrlV11}/merit/balance`);
const summaryV11 = await fetch(`${baseUrlV11}/merit/summary`); // 新功能
```

### cURL调用
```bash
# V1.0版本
curl -X GET "http://localhost:8080/api/v1.0/merit/balance"

# V1.1版本
curl -X GET "http://localhost:8080/api/v1.1/merit/balance"
curl -X GET "http://localhost:8080/api/v1.1/merit/summary"
```

## 版本特性对比

### V1.0 特性
- 基础功德查询
- 简单兑换功能
- 基本统计信息

### V1.1 特性
- 详细余额信息
- 兑换前后对比
- 统计汇总接口
- 增强版用户信息
- 更丰富的响应数据

## 兼容性处理

### 向后兼容
- V1.1版本保持对V1.0版本的兼容
- 旧客户端可以继续使用V1.0接口
- 新客户端可以使用V1.1的新特性

### 版本检测
```javascript
// 检查客户端版本兼容性
const checkCompatibility = async (clientVersion) => {
  const response = await fetch(`/api/v1.0/version/compatibility?clientVersion=${clientVersion}`);
  const result = await response.json();
  
  if (!result.data.compatible) {
    console.warn('客户端版本过旧，建议升级');
  }
  
  return result.data.serverVersion; // 返回推荐使用的服务器版本
};
```

## 技术实现细节

### 1. ApiVersionRequestMappingHandlerMapping
- 继承`RequestMappingHandlerMapping`
- 重写`getMappingForMethod`方法
- 自动为带版本注解的路径添加前缀

### 2. 路径处理流程
1. Spring扫描控制器方法
2. 检查`@ApiVersion`注解
3. 获取原始`@RequestMapping`路径
4. 添加`/api/{version}`前缀
5. 创建新的`RequestMappingInfo`

### 3. 版本优先级
- 方法级别`@ApiVersion` > 类级别`@ApiVersion`
- 支持细粒度的版本控制

这个版本控制系统使得API能够平滑升级，支持多版本并存，为不同版本的客户端提供相应的服务。