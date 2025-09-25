# 敲击数据链路接口评估

## 触发入口概览
- `KnockWebSocketConfig` 注册 `/ws/knock` 通道到 `KnockWebSocketHandler`。握手阶段通过 `KnockWebSocketHandshakeInterceptor` 校验登录态，连接建立后由 `KnockRealtimeNotifier` 向在线客户端推送敲击结果，确保页面展示与后端统计同步。参考：`src/main/java/com/hsmy/config/KnockWebSocketConfig.java:18`、`src/main/java/com/hsmy/websocket/KnockWebSocketHandler.java:20`、`src/main/java/com/hsmy/websocket/KnockRealtimeNotifier.java:13`。
- `MeritServiceConcurrencyTest` 使用多线程脚本验证 `UserLockManager` 的串行化能力，确保敲击入库时 `t_merit_record` 与 `t_user_stats` 的自增不会出现竞态。参考：`src/test/java/com/hsmy/service/impl/MeritServiceConcurrencyTest.java:19`、`src/main/java/com/hsmy/utils/UserLockManager.java:29`。
- `RankingController` 暴露日、周、总榜查询及用户排名接口，委托 `RankingService` 读取 `t_ranking` 快照，默认返回前 100 名。参考：`src/main/java/com/hsmy/controller/ranking/RankingController.java:24`。

## 接口详细说明

- 新增 `GET /knock/stats/periods` 提供指定日期的日/周/月/年敲击与功德统计，实时返回当日数据，周/月/年结果由凌晨定时任务聚合后供读取。参考：`src/main/java/com/hsmy/controller/knock/KnockController.java:142`、`src/main/java/com/hsmy/service/impl/KnockServiceImpl.java:169`。
### 1. KnockWebSocketConfig & WebSocket 实时入口
- 握手：`KnockWebSocketConfig.registerWebSocketHandlers` 为 `/ws/knock` 注册处理器，并设置 `KnockWebSocketHandshakeInterceptor`。拦截器从 `Authorization`、`X-Session-Id` 等来源提取 token，经 `SessionService.getUserSessionContext` 验证后，将 `userId` 写入 `WebSocketSession` 属性。参考：`src/main/java/com/hsmy/config/KnockWebSocketConfig.java:18`、`src/main/java/com/hsmy/websocket/KnockWebSocketHandshakeInterceptor.java:24`。
- 连接管理：`KnockWebSocketHandler.afterConnectionEstablished` 获取用户编号，调用 `KnockWebSocketSessionManager.register` 维护在线会话集合。关闭时对应地移除会话。参考：`src/main/java/com/hsmy/websocket/KnockWebSocketHandler.java:24`、`src/main/java/com/hsmy/websocket/KnockWebSocketSessionManager.java:18`。
- 消息处理：当前支持 `ping` 与 `clientUpdate`，用于维持心跳与客户端状态上报。业务敲击请求仍走 HTTP 接口，但每次 `KnockServiceImpl.manualKnock` 完成后会调用 `KnockRealtimeNotifier.notifyManualKnock` 将结果推送给在线用户，实现实时反馈。参考：`src/main/java/com/hsmy/websocket/KnockWebSocketHandler.java:40`、`src/main/java/com/hsmy/service/impl/KnockServiceImpl.java:105`、`src/main/java/com/hsmy/websocket/KnockRealtimeNotifier.java:17`。
- 数据落表：当用户敲击时，`KnockServiceImpl.manualKnock` 通过 `MeritService.manualKnock` 写入 `t_merit_record`（小时聚合），`UserStatsMapper.updateKnockStats` 追加总数，并调用 `UserPeriodStatsService.recordKnock` 更新 `t_user_period_stats` 的日数据，实现 WebSocket 反馈与三张表同步。参考：`src/main/java/com/hsmy/service/impl/MeritServiceImpl.java:61`、`src/main/java/com/hsmy/service/impl/UserPeriodStatsServiceImpl.java:33`。

### 2. MeritServiceConcurrencyTest 并发验证入口
- 测试目标：`MeritServiceConcurrencyTest` 通过 100 个线程同时针对单一用户累计计数，使用 `UserLockManager.executeWithUserLock` 保证串行，验证敲击时的加锁逻辑不会遗漏写入。参考：`src/test/java/com/hsmy/service/impl/MeritServiceConcurrencyTest.java:32`、`src/main/java/com/hsmy/utils/UserLockManager.java:41`。
- 单用户测试：`testUserLockManagerConcurrency` 每个线程执行 10 次加一操作，最终断言获得预期 1000 次结果，证明锁可避免并发写入丢失，从而保证 `t_merit_record` 与 `t_user_stats` 的累计字段可靠。参考：`src/test/java/com/hsmy/service/impl/MeritServiceConcurrencyTest.java:45`。
- 多用户测试：`testMultipleUsersNoInterference` 为不同用户创建独立锁实例，确保各用户敲击互不阻塞，也不相互影响。参考：`src/test/java/com/hsmy/service/impl/MeritServiceConcurrencyTest.java:74`。
- 结论：该入口虽非线上接口，但验证了敲击写入链路的线程安全性，是小时维度记录与总量统计按预期累加的前提。

### 3. RankingController 榜单查询入口
- 日榜接口：`/rankings/daily` 调用 `RankingService.getTodayRanking`，读取当天快照，默认 limit 100。数据由凌晨定时任务 `RankingScheduledTask.generateDailyRanking` 事先写入 `t_ranking`。参考：`src/main/java/com/hsmy/controller/ranking/RankingController.java:31`、`src/main/java/com/hsmy/task/RankingScheduledTask.java:23`。
- 周榜接口：`/rankings/weekly` 以当周周一为快照日期读取周榜，底层 `RankingCalculationService.generateWeeklyRanking` 聚合 `t_merit_record` 中指定周的功德值后写入 `t_ranking`。参考：`src/main/java/com/hsmy/controller/ranking/RankingController.java:43`、`src/main/java/com/hsmy/service/impl/RankingCalculationServiceImpl.java:82`。
- 总榜接口：`/rankings/total` 直接返回总榜快照，基于 `t_merit_record` 累计功德排序。参考：`src/main/java/com/hsmy/controller/ranking/RankingController.java:55`、`src/main/java/com/hsmy/service/impl/RankingCalculationServiceImpl.java:191`。
- 用户排名：`/rankings/user/{userId}` 汇总指定用户在日、周、总榜的排名，便于前端展示。`/rankings/my` 则使用当前登录用户 ID 调用同一逻辑。参考：`src/main/java/com/hsmy/controller/ranking/RankingController.java:67`。
- 生成策略：排名生成 SQL 通过 `t_merit_record` 聚合功德值并 `LIMIT 1000`。接口层默认取前 100 名，与产品需求基本一致，但若希望从源头限制写入，可将 SQL 上限改为 100。参考：`src/main/java/com/hsmy/service/impl/RankingCalculationServiceImpl.java:45`、`src/main/resources/mapper/RankingMapper.xml:37`。

## 数据沉淀要求对照
- **t_merit_record（按小时聚合）**：`MeritServiceImpl.executeKnockOperation` 将手动敲击归并到小时窗口，已存在记录时增量更新，并固定 `create_time` 在小时起点。自动敲击结算复用同一路径，满足小时粒度。参考：`src/main/java/com/hsmy/service/impl/MeritServiceImpl.java:61`。
- **t_dim_time（时间维度）**：`UserPeriodStatsServiceImpl.recordKnock` 通过 `TimeDimensionService.ensureDate` 确保日维度存在。夜间定时任务按日聚合生成周、月、年记录写入 `t_user_period_stats`。参考：`src/main/java/com/hsmy/service/impl/UserPeriodStatsServiceImpl.java:33`、`src/main/java/com/hsmy/task/UserPeriodStatsScheduledTask.java:25`。
- **t_user_stats（总量统计）**：敲击完成后 `UserStatsMapper.updateKnockStats` 累加总功德与总敲击，并更新最后敲击时间。所有写入都在用户锁内完成，避免并发覆盖。参考：`src/main/java/com/hsmy/service/impl/MeritServiceImpl.java:116`、`src/main/resources/mapper/UserStatsMapper.xml:60`。
- **t_ranking（榜单快照）**：`RankingCalculationServiceImpl` 聚合 `t_merit_record` 写入 `t_ranking`，由 `RankingController` 暴露查询接口。默认返回前 100 名，仍可视需求收紧 SQL 的 `LIMIT 1000`。参考：`src/main/java/com/hsmy/service/impl/RankingCalculationServiceImpl.java:37`、`src/main/resources/mapper/RankingMapper.xml:37`。

## 风险与建议
- 自动敲击跨小时仍以会话整体结算，若需要严格小时对齐，可在心跳阶段引入按小时切片的写库策略。
- 排行榜生成阶段的 `LIMIT 1000` 与产品“前 100 名”存在差异，建议下调或在批量写入前裁剪数据量。
- 周、月、年聚合依赖 00:02 定时任务，应监控调度线程池与 `asyncExecutor` 状态，并预留人工重跑能力以应对异常。
