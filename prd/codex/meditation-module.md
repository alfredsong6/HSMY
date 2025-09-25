# 冥想功能方案概览

## 我理解的需求
- 冥想与木鱼敲击同级，用户可配置冥想时长（预设 5~30 分钟或自定义）。
- 可选择是否伴随木鱼敲击，并在开启时设定敲击频率 60–100 次/分钟。
- 冥想结束后可保存记录：系统自动生成昵称和功德标签，同时记录日期时间、实际冥想时长。用户还需选择心情并填写一句领悟。保存行为还要记住本次设置的时间和敲击频率，方便下次默认。
- 功能需要付费使用：1 功德币/天、5 功德币/周、15 功德币/月，可视为订阅付费能力，需支持到期判断以及多端校验。
- 提供个人冥想统计：今日次数与总时长；累计次数与总时长；按月视图列出每天的冥想数据（时长、心情、领悟）。

## 表设计建议
### `t_meditation_session`
| 字段 | 说明 |
| --- | --- |
| id (PK) | 会话主键；雪花或自增 |
| user_id | 冥想的用户 ID |
| session_id | 前端可见的 UUID，方便日志查询 |
| planned_duration | 计划时长（秒）；对应预设或自定义 |
| actual_duration | 实际冥想时长（秒） |
| start_time / end_time | 开始与结束时间 |
| with_knock | 是否伴随木鱼敲击（0/1） |
| knock_frequency | 敲击频率（次/分钟），仅伴随敲击时存入 |
| mood_code | 冥想结束时选择的心情枚举 |
| insight_text | 一句领悟的话语（200 字内） |
| nickname_generated | 系统生成的冥想昵称 |
| merit_tag | 自动生成的功德标签 |
| config_snapshot | JSON，保存本次配置（时长、敲击频率等） |
| save_flag | 是否保存该会话（0-丢弃，1-保存） |
| coin_cost / coin_refunded | 本次会话扣除及退回的功德币，用于对账 |
| payment_status | 会话支付状态：RESERVED/SETTLED/REFUNDED |
| created_time / updated_time | 创建与更新时间 |

### `t_meditation_daily_stats`
| 字段 | 说明 |
| --- | --- |
| id (PK) | 统计记录主键 |
| user_id | 用户 ID |
| stat_date | 统计日期（yyyy-MM-dd） |
| session_count | 当日冥想次数 |
| total_minutes | 当日累计时长（分钟） |
| last_mood | 最近一次冥想的心情 |
| last_insight | 最近一次冥想的领悟文字 |
| created_time / updated_time | 统计行的时间戳 |

### `t_meditation_subscription`
| 字段 | 说明 |
| --- | --- |
| id (PK) | 订阅主键 |
| user_id | 用户 ID |
| plan_type | DAY/WEEK/MONTH，对应 1/5/15 功德币 |
| start_time / end_time | 生效与失效时间 |
| status | current/expired/cancelled |
| coin_cost | 支付功德币数量 |
| order_id | 对应功德币流水表 `t_merit_coin_transaction.id` |
| created_time / updated_time | 记录时间戳 |

### `t_meditation_user_pref`
| 字段 | 说明 |
| --- | --- |
| user_id (PK) | 用户 ID，同一用户唯一一行 |
| default_duration | 最近一次选择的计划时长（秒） |
| default_with_knock | 默认是否开启伴随敲击（0/1） |
| default_knock_frequency | 默认敲击频率（次/分钟），`default_with_knock=1` 时生效 |
| last_updated_time | 用户偏好更新时间 |

> 服务启动时可延迟加载偏好表；若与 `t_user_stats` 联动，保持在业务层聚合即可。

### `t_merit_coin_transaction`
| 字段 | 说明 |
| --- | --- |
| id (PK) | 流水主键 |
| user_id | 用户 ID |
| biz_type | 业务类型（如 `MEDITATION_SUBSCRIBE`、`MEDITATION_REFUND`） |
| biz_id | 业务主键（例如 `t_meditation_subscription.id`） |
| change_amount | 变动值，正数增加、负数扣减 |
| balance_after | 变动后的余额快照 |
| remark | 说明文案，便于运营查看 |
| created_time / updated_time | 记录时间戳 |

> 实时余额继续存放在 `t_user_stats.merit_coins`，所有扣减/退款均同步写入该字段及流水表，实现“余额 + 明细”双记录。

## 接口设计思路
- `POST /api/meditation/subscription/purchase`：提交订阅类型，扣除功德币并生成订阅记录；返回有效期。
- `GET /api/meditation/subscription/status`：返回当前套餐与剩余天数，供前端判断是否提示续费。
- `POST /api/meditation/session/start`：校验订阅有效性，生成会话草稿，返回 `sessionId`。请求体带时长、是否敲击与频率。
- `POST /api/meditation/session/finish`：写入实际时长、心情、领悟、自动标签，并落库；同时刷新 `t_meditation_daily_stats`。
- `POST /api/meditation/session/discard`：用户选择不保存时，删除/标记草稿。
- `GET /api/meditation/stats/summary`：返回今日、累计统计（次数、时长）。
- `GET /api/meditation/stats/month?month=2025-09`：输出月视图，每日包含时长、心情、领悟。
- 可选：`GET /api/meditation/config/default`，`PUT /api/meditation/config/default` 用于读取/更新默认时长与敲击频率。

实现关键点：
- 所有写操作需加用户锁，防止多端同时结束会话。
- 订阅校验可缓存（Redis），但以数据库为准；扣币逻辑复用现有钱包模块。
- 统计采用增量更新（触发器或服务层），并定时作废过期订阅。
