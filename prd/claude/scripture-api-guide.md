# 典籍功能模块接口说明文档

## 概述

典籍功能模块是一个完整的佛教典籍在线阅读系统，支持用户购买、阅读、进度管理等核心功能。系统采用福币付费模式，按月订阅制，支持书本翻页式的阅读交互体验。

## 核心业务逻辑

### 1. 购买逻辑
- 用户使用福币购买典籍阅读权限（1-10福币/月）
- 支持1-12个月的购买时长选择
- 购买后生成带有过期时间的购买记录
- 系统自动更新典籍购买统计数据

### 2. 阅读权限管理
- 基于购买记录的有效性进行权限验证
- 过期时间到达后自动失效，需续费继续阅读
- 支持7天即将过期提醒机制

### 3. 阅读进度机制
**核心设计理念**：区分"阅读进度"和"最后阅读位置"
- **阅读进度(readingProgress)**：整体完成度指标（0-100%），只能向前推进
- **最后阅读位置(lastReadingPosition)**：用户最后停留的具体字符位置，支持前后移动

**应用场景**：
```
用户正常阅读到60%位置 → readingProgress=60%, lastReadingPosition=1500
用户翻回30%位置重读  → readingProgress=60%(保持), lastReadingPosition=800
继续阅读时从位置800开始，而非1500位置
```

## 接口详细说明

## 一、典籍查询接口组 (`/scripture`)

### 1.1 获取典籍列表
**接口路径**：`GET /scripture/list`

**功能描述**：根据多种条件查询典籍列表，支持类型筛选、价格范围、热门度、难度等级等

**查询参数**：
- `scriptureType`：典籍类型（sutra-佛经经典，mantra-咒语）
- `isHot`：是否热门（0-否，1-是）
- `minPrice/maxPrice`：价格范围（1-10福币）
- `difficultyLevel`：难度等级（1-初级，2-中级，3-高级）
- `keyword`：搜索关键词
- `tag`：分类标签
- `sortField/sortOrder`：排序字段和方向

**业务逻辑**：
1. 根据查询条件从数据库获取典籍列表
2. 如果用户已登录，填充购买状态信息
3. 返回包含购买状态的典籍VO列表

**响应示例**：
```json
{
  "code": 200,
  "data": [{
    "id": 1001,
    "scriptureName": "心经",
    "scriptureType": "sutra",
    "price": 2,
    "priceUnit": "部",
    "isHot": 1,
    "isPurchased": true,
    "isPurchaseValid": true
  }]
}
```

### 1.2 获取典籍详情
**接口路径**：`GET /scripture/{scriptureId}`

**功能描述**：获取单个典籍的详细信息

**业务逻辑**：
1. 根据ID查询典籍详情
2. 验证典籍存在性
3. 如果用户已登录，查询并填充用户购买状态
4. 返回完整的典籍信息

### 1.3 热门典籍列表
**接口路径**：`GET /scripture/hot`

**功能描述**：获取系统推荐的热门典籍列表

### 1.4 按类型查询
**接口路径**：`GET /scripture/type/{scriptureType}`

**参数说明**：
- `scriptureType`：sutra（佛经经典）或 mantra（咒语）

### 1.5 搜索典籍
**接口路径**：`GET /scripture/search?keyword={keyword}`

**功能描述**：根据关键词搜索典籍名称、作者、描述等字段

### 1.6 记录阅读行为
**接口路径**：`POST /scripture/{scriptureId}/read`

**功能描述**：记录用户阅读行为，增加阅读次数统计

**业务逻辑**：
1. 验证用户购买权限
2. 更新用户购买记录中的阅读次数
3. 更新典籍总阅读次数
4. 更新最后阅读时间

## 二、用户典籍管理接口组 (`/user/scripture`)

### 2.1 购买典籍
**接口路径**：`POST /user/scripture/purchase`

**请求体**：
```json
{
  "scriptureId": 1001,
  "purchaseMonths": 3
}
```

**业务逻辑**：
1. **权限验证**：检查典籍是否存在且可购买
2. **重复购买检查**：验证用户是否已购买且未过期
3. **创建购买记录**：
   - 计算支付金额：`price * purchaseMonths`
   - 设置过期时间：当前时间 + 购买月数
   - 初始化阅读数据：进度0%，位置0，阅读次数0
4. **更新统计**：增加典籍购买次数
5. **扣除福币**：（需要集成用户福币系统）

### 2.2 继续阅读接口 ⭐核心接口
**接口路径**：`GET /user/scripture/read/{scriptureId}`

**功能描述**：用户点击进入阅读时获取典籍内容和阅读位置信息

**业务逻辑**：
1. **权限验证**：检查购买记录是否存在且未过期
2. **内容获取**：返回完整典籍内容供前端分页
3. **位置定位**：
   ```java
   // 关键逻辑：使用最后阅读位置而非阅读进度
   Integer lastPosition = purchase.getLastReadingPosition();
   if (lastPosition == null) {
       lastPosition = 0; // 首次阅读从头开始
   }
   // 智能定位到合适的段落或句子边界
   int suggestedStart = findSuggestedStartPosition(content, lastPosition);
   ```
4. **记录阅读行为**：自动增加阅读次数和更新最后阅读时间

**响应数据**：
```json
{
  "code": 200,
  "data": {
    "scriptureId": 1001,
    "scriptureName": "心经",
    "content": "观自在菩萨，行深般若波罗蜜多时...",
    "readingProgress": 60.5,
    "lastReadingPosition": 800,
    "currentPosition": 800,
    "suggestedStartPosition": 795,
    "remainingDays": 25,
    "isExpiringSoon": false
  }
}
```

**前端使用方式**：
```javascript
// 前端根据返回数据进行分页处理
const pageSize = 500; // 每页字符数
const startPage = Math.floor(data.lastReadingPosition / pageSize) + 1;
// 从startPage开始显示内容
```

### 2.3 更新最后阅读位置 ⭐核心接口
**接口路径**：`PUT /user/scripture/last-reading-position`

**功能描述**：用户翻页时保存当前阅读位置

**请求体**：
```json
{
  "scriptureId": 1001,
  "lastReadingPosition": 1250
}
```

**业务逻辑**：
1. 验证用户购买权限
2. 更新数据库中的`last_reading_position`字段
3. 同时更新`last_read_time`为当前时间

**前端调用时机**：
- 用户翻页时（建议防抖处理，避免频繁调用）
- 用户退出阅读时
- 定期自动保存（如每30秒）

### 2.4 更新阅读进度
**接口路径**：`PUT /user/scripture/reading-progress`

**功能描述**：更新用户整体阅读完成度

**请求体**：
```json
{
  "scriptureId": 1001,
  "readingProgress": 75.5
}
```

**使用场景**：
- 用户阅读到新的最远位置时
- 完成整个典籍阅读时设置为100%

### 2.5 获取购买记录列表
**接口路径**：`GET /user/scripture/purchases`

**功能描述**：获取用户所有典籍购买记录

**业务逻辑**：
1. 查询用户所有购买记录
2. 关联查询典籍基本信息（名称、封面等）
3. 计算剩余天数和即将过期状态
4. 返回enriched的购买记录VO列表

### 2.6 获取有效购买记录
**接口路径**：`GET /user/scripture/valid-purchases`

**功能描述**：仅获取未过期的购买记录，用于"我的书架"展示

### 2.7 续费典籍
**接口路径**：`POST /user/scripture/renew/{scriptureId}?extendMonths={months}`

**业务逻辑**：
1. 查询现有购买记录
2. 计算续费金额：`典籍价格 * 续费月数`
3. 更新过期时间：
   ```java
   // 从现有过期时间延长，如果已过期则从当前时间开始
   Date baseTime = purchase.getExpireTime().after(new Date()) ?
                   purchase.getExpireTime() : new Date();
   calendar.setTime(baseTime);
   calendar.add(Calendar.MONTH, extendMonths);
   ```
4. 更新购买记录状态为有效

### 2.8 购买状态检查
**接口路径**：
- `GET /user/scripture/check-purchased/{scriptureId}` - 检查是否已购买
- `GET /user/scripture/check-valid/{scriptureId}` - 检查购买是否有效

**使用场景**：前端显示购买按钮前的状态判断

### 2.9 用户统计信息
**接口路径**：`GET /user/scripture/stats`

**响应数据**：
```json
{
  "code": 200,
  "data": {
    "totalPurchases": 5,
    "validPurchases": 3,
    "expiringSoon": 1,
    "expired": 2,
    "totalReadCount": 127,
    "averageProgress": 68.5
  }
}
```

## 三、数据表结构

### 3.1 典籍表 (t_scripture)
```sql
-- 核心字段
scripture_name VARCHAR(200)    -- 典籍名称
scripture_type VARCHAR(30)     -- 类型：sutra/mantra
price INT                      -- 价格（福币）
price_unit VARCHAR(10)         -- 计价单位
duration_months INT            -- 购买时长
is_hot TINYINT                 -- 是否热门
content LONGTEXT               -- 完整内容
word_count INT                 -- 字数统计
```

### 3.2 用户购买记录表 (t_user_scripture_purchase)
```sql
-- 购买信息
user_id BIGINT                 -- 用户ID
scripture_id BIGINT            -- 典籍ID
merit_coins_paid INT           -- 支付福币
purchase_months INT            -- 购买月数
purchase_time DATETIME         -- 购买时间
expire_time DATETIME           -- 过期时间
is_expired TINYINT             -- 是否过期

-- 阅读数据
reading_progress DECIMAL(5,2)  -- 阅读进度百分比
last_reading_position INT      -- 最后阅读位置（字符位置）⭐新增
read_count INT                 -- 阅读次数
last_read_time DATETIME        -- 最后阅读时间
```

## 四、前端集成指南

### 4.1 书本翻页交互实现

```javascript
class ScriptureReader {
    constructor(scriptureData) {
        this.content = scriptureData.content;
        this.pageSize = 500; // 每页字符数
        this.totalPages = Math.ceil(this.content.length / this.pageSize);
        // 根据最后阅读位置计算起始页
        this.currentPage = Math.floor(scriptureData.lastReadingPosition / this.pageSize) + 1;
    }

    // 翻页处理
    nextPage() {
        if (this.currentPage < this.totalPages) {
            this.currentPage++;
            this.updatePage();
            this.saveProgress(); // 防抖保存
        }
    }

    // 保存阅读位置（防抖处理）
    saveProgress: debounce(function() {
        const startPos = (this.currentPage - 1) * this.pageSize;
        fetch('/user/scripture/last-reading-position', {
            method: 'PUT',
            body: JSON.stringify({
                scriptureId: this.scriptureId,
                lastReadingPosition: startPos
            })
        });
    }, 2000)
}
```

### 4.2 阅读流程
1. **进入阅读**：调用`GET /user/scripture/read/{id}`获取内容和位置
2. **翻页阅读**：前端分页显示，翻页时调用位置更新接口
3. **退出保存**：确保最后位置已保存
4. **重新进入**：从上次位置继续阅读

## 五、系统特性

### 5.1 权限控制
- 所有阅读相关接口都需要登录验证
- 购买状态实时校验，过期自动失效
- 接口级别的权限拦截

### 5.2 数据一致性
- 使用`@Transactional`保证关键操作的原子性
- 购买和统计数据的同步更新
- 并发安全的阅读进度更新

### 5.3 性能优化
- 智能的阅读位置定位算法
- 防抖机制减少API调用频率
- 合理的数据库索引设计

### 5.4 用户体验
- 断点续读功能
- 智能边界定位（段落/句子边界）
- 即将过期提醒机制
- 详细的购买和阅读统计

## 六、扩展功能建议

### 6.1 已实现功能
- ✅ 基础购买和阅读功能
- ✅ 进度管理和位置记录
- ✅ 权限验证和过期处理
- ✅ 统计和分析功能

### 6.2 可扩展功能
- 🔄 阅读笔记和书签功能
- 🔄 阅读历史和轨迹分析
- 🔄 社交分享和推荐系统
- 🔄 离线下载和缓存机制
- 🔄 多终端同步阅读进度

---

**文档版本**：v1.0
**创建时间**：2025-09-25
**最后更新**：2025-09-25