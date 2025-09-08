# API接口测试指南

## 概述

本文档提供了敲敲木鱼项目的完整API接口测试方案，包含多种测试方式和工具，确保系统功能正常运行。

## 测试工具

### 1. Bash脚本测试 (`api_test.sh`)

**适用场景**：Linux/macOS系统，快速自动化测试

**使用方法**：
```bash
# 赋予执行权限
chmod +x api_test.sh

# 运行测试
./api_test.sh

# 或者指定服务器地址
BASE_URL="http://your-server:8080/api" ./api_test.sh
```

**依赖要求**：
- `curl` - HTTP请求工具
- `jq` - JSON解析工具（可选）

### 2. Python脚本测试 (`api_test.py`)

**适用场景**：跨平台，功能最完整的测试工具

**使用方法**：
```bash
# 安装依赖
pip install requests

# 运行测试
python api_test.py

# 指定服务器地址
python api_test.py --url http://your-server:8080/api

# 显示详细输出
python api_test.py --verbose
```

**特性**：
- ✅ 完整的测试覆盖
- ✅ 详细的测试报告
- ✅ 并发用户测试
- ✅ 异常处理测试
- ✅ 跨平台支持

### 3. Postman集合测试 (`HSMY_API_Tests.postman_collection.json`)

**适用场景**：手动测试，团队协作，API文档

**使用方法**：
1. 打开Postman应用
2. 点击Import导入集合文件
3. 设置环境变量`baseUrl`为您的服务器地址
4. 按顺序执行测试请求

**特性**：
- 🎯 图形化界面操作
- 📝 自动提取和保存sessionId
- 🔄 支持环境变量切换
- 📊 可视化测试结果

## 测试流程

### 阶段1：系统基础测试
1. **健康检查** - 验证服务器是否正常启动
2. **白名单功能** - 测试接口访问控制机制

### 阶段2：用户认证测试  
1. **用户注册** - 测试新用户创建
2. **用户登录** - 测试认证机制和Session创建
3. **获取用户信息** - 验证Session有效性和用户上下文

### 阶段3：会话管理测试
1. **多端登录** - 测试同一用户多设备登录
2. **会话查询** - 获取用户所有活跃会话
3. **会话管理** - 踢出其他设备登录

### 阶段4：业务功能测试
1. **敲木鱼功能** - 核心业务逻辑
2. **功德统计** - 用户数据展示
3. **排行榜** - 社交功能测试

### 阶段5：异常处理测试
1. **无效输入** - 测试参数验证
2. **认证失败** - 测试安全机制
3. **边界条件** - 极限情况处理

## 测试数据

### 测试用户数据模板
```json
{
    "username": "testuser_[timestamp]",
    "password": "123456", 
    "confirmPassword": "123456",
    "nickname": "测试用户[timestamp]",
    "phone": "138[8位随机数字]",
    "email": "testuser_[timestamp]@example.com"
}
```

### 敲木鱼测试数据
```json
{
    "knockCount": 10,
    "prayerText": "祈求平安健康"
}
```

## 预期结果

### 成功场景

#### 用户注册成功
```json
{
    "code": 200,
    "message": "操作成功", 
    "data": "注册成功"
}
```

#### 用户登录成功
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "sessionId": "abc123_1694156400000",
        "userId": 1,
        "username": "testuser_123",
        "nickname": "测试用户",
        "phone": "13800138000",
        "email": "test@example.com"
    }
}
```

#### 获取用户信息成功
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "userId": 1,
        "username": "testuser_123",
        "nickname": "测试用户",
        "currentLevel": 5,
        "meritCoins": 1500,
        "totalMerit": 10000,
        "loginTime": "2025-09-08T10:00:00",
        "lastAccessTime": "2025-09-08T12:30:00",
        "isAdmin": false
    }
}
```

### 失败场景

#### 认证失败
```json
{
    "code": 401,
    "message": "登录已过期，请重新登录"
}
```

#### 参数验证失败
```json
{
    "code": 400,
    "message": "用户名已存在"
}
```

## 测试环境要求

### 服务器要求
- ✅ 服务器运行在8080端口
- ✅ 数据库连接正常
- ✅ Redis服务运行正常
- ✅ 网络访问无限制

### 客户端要求
- **Bash测试**：Linux/macOS + curl + jq
- **Python测试**：Python 3.6+ + requests库
- **Postman测试**：Postman应用

## 故障排除

### 常见问题

#### 1. 连接失败
```
[ERROR] 请求失败: Connection refused
```
**解决方案**：
- 检查服务器是否启动：`curl http://localhost:8080/api/auth/health`
- 检查端口是否正确
- 检查防火墙设置

#### 2. 认证失败
```
[ERROR] GET /auth/user-info - 期望状态码: 200, 实际: 401
```
**解决方案**：
- 检查Redis服务是否运行
- 确认SessionId是否正确传递
- 检查Session是否过期

#### 3. 数据库错误
```
[ERROR] 用户注册失败：数据库连接异常
```
**解决方案**：
- 检查数据库服务状态
- 验证数据库连接配置
- 确认数据表是否存在

#### 4. JSON解析错误
```
[ERROR] 无效JSON格式
```
**解决方案**：
- 检查请求Content-Type
- 验证JSON格式正确性
- 确认字符编码为UTF-8

### 调试技巧

#### 1. 开启详细日志
```yaml
# application.yml
logging:
  level:
    com.hsmy: debug
    org.springframework.web: debug
```

#### 2. 使用浏览器开发工具
- F12打开开发者工具
- Network标签查看HTTP请求详情
- Console标签查看JavaScript错误

#### 3. 检查Redis数据
```bash
# 连接Redis
redis-cli

# 查看所有Session
keys session:*

# 查看用户Session列表  
keys user:sessions:*

# 查看Session详情
get session:your_session_id
```

## 性能测试

### 并发测试建议

#### 使用Apache Bench
```bash
# 测试登录接口并发性能
ab -n 100 -c 10 -T application/json -p login_data.json http://localhost:8080/api/auth/login
```

#### 使用Python脚本
```python
# 运行并发用户测试
python api_test.py --concurrent-users 10
```

### 性能指标
- **响应时间**：< 200ms（正常业务接口）
- **并发用户**：支持100+并发登录
- **Session容量**：Redis可存储10万+活跃Session

## 最佳实践

### 测试建议
1. **环境隔离**：使用独立的测试环境
2. **数据清理**：测试后清理测试数据
3. **定期执行**：集成到CI/CD流程
4. **结果记录**：保存测试报告和日志

### 安全注意事项
1. **密码安全**：测试环境使用简单密码
2. **数据脱敏**：避免使用真实用户数据
3. **访问控制**：限制测试环境网络访问
4. **清理策略**：及时清理测试用户和Session

## 扩展测试

### 自定义测试用例
可以基于提供的脚本模板，添加更多业务场景测试：

```python
def test_custom_business_logic(self):
    """自定义业务逻辑测试"""
    # 添加您的测试代码
    pass
```

### 集成测试
将API测试集成到您的CI/CD流程中：

```yaml
# .github/workflows/api-test.yml
name: API Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run API Tests
        run: python api_test.py --url http://test-server:8080/api
```

通过这套完整的测试工具和方案，您可以全面验证敲敲木鱼项目的API接口功能，确保系统稳定可靠运行！