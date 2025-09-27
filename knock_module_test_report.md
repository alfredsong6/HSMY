# HSMY 敲击模块 API 测试报告

## 测试概述
- **测试时间**: 2025-09-27
- **测试环境**: localhost:8080/api
- **测试模块**: 敲击模块 (KnockController)
- **测试方法**: 使用 curl 命令行工具
- **认证Token**: 2198f5039de2452fb61cbe5033e755f0_1758957344541

## 测试结果汇总

| 接口名称 | 初次测试 | 重新测试 | HTTP状态码 | 业务状态码 | 备注 |
|---------|---------|---------|-----------|-----------|------|
| 手动敲击 | ❌ 失败 | ✅ 成功 | 200 | 200 | 使用正确时间格式成功 |
| 开始自动敲击 | ❌ 失败 | ❌ 失败 | 200 | 500 | 功德币不足 |
| 停止自动敲击 | ❌ 失败 | 未测试 | 200 | 400 | 需要活跃会话 |
| 自动敲击心跳 | ❌ 失败 | 未测试 | 200 | 500 | 需要活跃会话 |
| 获取敲击统计 | ❌ 失败 | ❌ 失败 | 200 | 500 | 数据库表不存在 |
| 获取周期统计 | ❌ 失败 | ❌ 失败 | 200 | 500 | 数据库表不存在 |
| 获取自动敲击状态 | ✅ 成功 | ✅ 成功 | 200 | 200 | 正常 |

## 详细测试结果

### 1. 手动敲击 ✅ (重新测试成功)
**接口**: `POST /knock/manual`

**认证要求**: 需要登录

**问题分析**: 初次测试失败是因为时间格式不正确

**正确入参** (完整参数):
```json
{
  "knockCount": 10,
  "knockSound": "default",
  "sessionDuration": 60,
  "knockTime": "2025-09-27 15:35:00"
}
```

**成功出参**:
```json
{
  "code": 200,
  "message": "敲击成功",
  "data": {
    "knockMode": "MANUAL",
    "totalMerit": 1015,
    "meritGained": 10,
    "comboCount": 0,
    "todayKnocks": 15,
    "multiplier": 1.0,
    "todayMerit": 15,
    "totalKnocks": 35,
    "propSnapshot": null,
    "maxCombo": 0
  }
}
```

**最小必填参数**:
```json
{
  "knockCount": 3,
  "knockTime": "2025-09-27 15:40:00"
}
```

**测试结果**: 成功
**HTTP状态码**: 200

**关键发现**:
1. ✅ `knockTime` 是必填参数
2. ✅ 时间格式必须为 `YYYY-MM-DD HH:mm:ss`，不能使用ISO格式
3. ✅ `knockSound` 和 `sessionDuration` 是可选参数
4. ✅ 功德值正确累加：每次敲击获得对应数量的功德
5. ✅ 统计数据正确更新：今日敲击次数、总敲击次数都正确累加

**测试验证**:
- 敲击5次：功德从1000增加到1005 ✅
- 敲击10次：功德从1005增加到1015 ✅
- 敲击100次：功德从1018增加到1118 ✅
- 连续敲击：统计数据正确累加 ✅

---

### 2. 开始自动敲击 ❌
**接口**: `POST /knock/auto/start`

**认证要求**: 需要登录

**入参**:
```json
{
  "duration": 300,
  "knockInterval": 1000,
  "knockSound": "default"
}
```

**出参**:
```json
{
  "code": 500,
  "message": "开始自动敲击失败：功德币不足，请先充值或调整自动敲击设置",
  "data": null
}
```

**测试结果**: 失败
**HTTP状态码**: 200
**失败原因**: 用户功德币不足，自动敲击需要消耗功德币

---

### 3. 停止自动敲击 ❌
**接口**: `POST /knock/auto/stop`

**认证要求**: 需要登录

**入参**:
```json
{
  "sessionId": "test_session_123",
  "actualDuration": 250
}
```

**出参**:
```json
{
  "code": 400,
  "message": "参数校验失败: knockCount: 敲击次数不能为空",
  "data": null
}
```

**测试结果**: 失败
**HTTP状态码**: 200
**失败原因**: 文档缺少必填参数 `knockCount`

---

### 4. 自动敲击心跳 ❌
**接口**: `POST /knock/auto/heartbeat`

**认证要求**: 需要登录

**入参**:
```json
{
  "sessionId": "test_session_123",
  "currentKnockCount": 150
}
```

**出参**:
```json
{
  "code": 500,
  "message": "心跳更新失败：会话不存在",
  "data": null
}
```

**测试结果**: 失败
**HTTP状态码**: 200
**失败原因**: 测试用的会话ID不存在，需要先创建有效的自动敲击会话

---

### 5. 获取敲击统计 ❌
**接口**: `GET /knock/stats`

**认证要求**: 需要登录

**入参**: 无

**出参**:
```json
{
  "code": 500,
  "message": "获取统计失败：\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Table 'hsmy_db.t_dim_time' doesn't exist\n### The error may exist in file [/Users/alfreds/project/HSMY/target/classes/mapper/DimTimeMapper.xml]\n### The error may involve defaultParameterMap\n### The error occurred while setting parameters\n### SQL: SELECT id, date_value, iso_week, month_value, year_value, week_start, week_end, month_start, month_end, quarter, is_weekend, create_time, update_time FROM t_dim_time WHERE date_value = ? LIMIT 1\n### Cause: java.sql.SQLSyntaxErrorException: Table 'hsmy_db.t_dim_time' doesn't exist",
  "data": null
}
```

**测试结果**: 失败
**HTTP状态码**: 200
**失败原因**: 数据库缺少维度时间表 `t_dim_time`

---

### 6. 获取周期统计 ❌
**接口**: `GET /knock/stats/periods?referenceDate=2025-09-27`

**认证要求**: 需要登录

**入参**: 查询参数 `referenceDate=2025-09-27`

**出参**:
```json
{
  "code": 500,
  "message": "获取周期统计失败：\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Table 'hsmy_db.t_dim_time' doesn't exist\n### The error may exist in file [/Users/alfreds/project/HSMY/target/classes/mapper/DimTimeMapper.xml]\n### The error may involve defaultParameterMap\n### The error occurred while setting parameters\n### SQL: SELECT id, date_value, iso_week, month_value, year_value, week_start, week_end, month_start, month_end, quarter, is_weekend, create_time, update_time FROM t_dim_time WHERE date_value = ? LIMIT 1\n### Cause: java.sql.SQLSyntaxErrorException: Table 'hsmy_db.t_dim_time' doesn't exist",
  "data": null
}
```

**测试结果**: 失败
**HTTP状态码**: 200
**失败原因**: 数据库缺少维度时间表 `t_dim_time`

---

### 7. 获取自动敲击状态 ✅
**接口**: `GET /knock/auto/status`

**认证要求**: 需要登录

**入参**: 无

**出参**:
```json
{
  "code": 200,
  "message": "获取状态成功",
  "data": {
    "message": "当前没有进行中的自动敲击",
    "hasActiveSession": false
  }
}
```

**测试结果**: 成功
**HTTP状态码**: 200

---

## 问题汇总

### 1. 数据库配置问题 ❌
- **问题**: 缺少维度时间表 `t_dim_time`
- **影响接口**: 
  - 获取敲击统计
  - 获取周期统计
- **错误信息**: `Table 'hsmy_db.t_dim_time' doesn't exist`
- **建议**: 创建相应的数据库表或修复数据库迁移脚本

### 2. 文档参数不完整 ❌
- **问题**: API文档中缺少必填参数
- **影响接口**:
  - 手动敲击：缺少 `knockTime` 参数
  - 停止自动敲击：缺少 `knockCount` 参数
- **建议**: 更新API文档，添加完整的参数列表

### 3. 业务逻辑限制 ⚠️
- **问题**: 功德币不足限制自动敲击功能
- **影响接口**: 开始自动敲击
- **现象**: 需要足够的功德币才能启动自动敲击
- **建议**: 为测试用户添加足够的功德币，或提供测试模式

### 4. 会话依赖问题 ⚠️
- **问题**: 部分接口依赖活跃的敲击会话
- **影响接口**: 
  - 停止自动敲击
  - 自动敲击心跳
- **建议**: 需要先成功创建自动敲击会话才能测试相关接口

### 5. 系统内部错误 ❌
- **问题**: 手动敲击接口出现系统内部错误
- **可能原因**: 
  - 数据库插入失败
  - 业务逻辑异常
  - 参数格式错误
- **建议**: 检查服务器日志，确定具体错误原因

## 认证机制验证

- **Token验证**: 所有接口都正确验证了认证token
- **权限控制**: 未登录时会返回401错误
- **会话管理**: Token在测试期间保持有效

## 数据库依赖分析

敲击模块对数据库有以下依赖：

1. **维度时间表** (`t_dim_time`): 用于统计功能
2. **敲击记录表**: 用于存储敲击数据
3. **自动敲击会话表**: 用于管理自动敲击状态
4. **用户功德表**: 用于验证功德币余额

## 总体评估

- **初次测试成功率**: 14% (1/7个测试用例成功)
- **重新测试成功率**: 29% (2/7个测试用例成功)
- **可用功能**: 手动敲击、获取自动敲击状态
- **主要问题**: 
  1. ✅ 手动敲击已修复 - 使用正确的时间格式
  2. ❌ 数据库配置不完整 - 缺少 `t_dim_time` 表
  3. ❌ 测试数据不足 - 用户功德币不足
- **建议优先级**:
  1. 更新API文档，明确时间格式要求 `YYYY-MM-DD HH:mm:ss`(高)
  2. 修复数据库配置，创建缺失的表(高) 
  3. 为测试用户充值功德币(中)
  4. 完善其他接口的参数文档(低)

## 重新测试结论

手动敲击接口重新测试成功，关键问题和解决方案：

1. **时间格式要求**: 必须使用 `YYYY-MM-DD HH:mm:ss` 格式，不能使用ISO 8601格式
2. **必填参数**: `knockCount` 和 `knockTime` 是必填的
3. **可选参数**: `knockSound` 和 `sessionDuration` 是可选的
4. **功能验证**: 功德值和统计数据都正确累加
5. **业务逻辑**: 敲击获得的功德 = knockCount × 1（基础倍率）

## 开发建议

1. **数据库初始化**: 确保所有必要的数据库表都已创建
2. **文档完善**: 更新API文档，包含所有必填参数
3. **测试数据**: 为测试环境准备足够的测试数据（功德币等）
4. **错误处理**: 改进错误信息，提供更友好的提示
5. **集成测试**: 建立完整的测试流程，确保所有依赖都满足

## 修复建议

为了使敲击模块正常工作，建议按以下顺序修复：

1. **创建数据库表**: 创建 `t_dim_time` 维度时间表
2. **初始化测试数据**: 为测试用户添加功德币
3. **修复文档**: 添加缺失的API参数文档
4. **调试内部错误**: 排查手动敲击的具体错误原因
5. **端到端测试**: 完成整个敲击流程的测试