# 敲敲木鱼项目 - 完整模块接口设计文档

## 已完成模块

### 1. 用户系统模块 ✅
- Entity: User, UserStats
- Mapper: UserMapper, UserStatsMapper (含XML)
- Service: UserService, UserServiceImpl
- Controller: UserController
- VO: LoginVO, RegisterVO, UserVO, UserQueryVO, ChangePasswordVO

### 2. 功德系统模块 ✅
- Entity: MeritRecord, ExchangeRecord, MeritLevel
- Mapper: MeritRecordMapper, ExchangeRecordMapper (含XML)
- Service: MeritService, MeritServiceImpl
- Controller: MeritController
- VO: KnockVO, ExchangeVO

## 待完成模块清单

### 3. 捐赠系统模块
需要创建的文件：
```
entity/
  ├── DonationProject.java     # 捐赠项目实体
  └── Donation.java            # 捐赠记录实体

mapper/
  ├── DonationProjectMapper.java
  ├── DonationMapper.java
  └── xml/
      ├── DonationProjectMapper.xml
      └── DonationMapper.xml

service/
  ├── DonationService.java
  └── impl/
      └── DonationServiceImpl.java

controller/
  └── donation/
      └── DonationController.java

vo/
  ├── DonationVO.java          # 捐赠VO
  ├── DonationProjectVO.java   # 项目VO
  └── DonationQueryVO.java     # 查询VO
```

### 4. 道具商城模块
需要创建的文件：
```
entity/
  ├── Item.java                # 道具实体
  ├── UserItem.java           # 用户道具实体
  └── PurchaseRecord.java     # 购买记录实体

mapper/
  ├── ItemMapper.java
  ├── UserItemMapper.java
  ├── PurchaseRecordMapper.java
  └── xml/
      ├── ItemMapper.xml
      ├── UserItemMapper.xml
      └── PurchaseRecordMapper.xml

service/
  ├── ItemService.java
  └── impl/
      └── ItemServiceImpl.java

controller/
  └── shop/
      └── ShopController.java

vo/
  ├── ItemVO.java              # 道具VO
  ├── PurchaseVO.java          # 购买VO
  └── ItemQueryVO.java         # 查询VO
```

### 5. 排行榜系统模块
需要创建的文件：
```
entity/
  ├── Ranking.java             # 排行榜快照实体
  └── RankingReward.java       # 排行榜奖励实体

mapper/
  ├── RankingMapper.java
  ├── RankingRewardMapper.java
  └── xml/
      ├── RankingMapper.xml
      └── RankingRewardMapper.xml

service/
  ├── RankingService.java
  └── impl/
      └── RankingServiceImpl.java

controller/
  └── ranking/
      └── RankingController.java

vo/
  ├── RankingVO.java           # 排行榜VO
  └── RankingQueryVO.java      # 查询VO
```

### 6. 成就系统模块
需要创建的文件：
```
entity/
  ├── Achievement.java         # 成就定义实体
  └── UserAchievement.java     # 用户成就实体

mapper/
  ├── AchievementMapper.java
  ├── UserAchievementMapper.java
  └── xml/
      ├── AchievementMapper.xml
      └── UserAchievementMapper.xml

service/
  ├── AchievementService.java
  └── impl/
      └── AchievementServiceImpl.java

controller/
  └── achievement/
      └── AchievementController.java

vo/
  ├── AchievementVO.java       # 成就VO
  └── UserAchievementVO.java   # 用户成就VO
```

### 7. 任务系统模块
需要创建的文件：
```
entity/
  ├── Task.java                # 任务定义实体
  └── UserTask.java            # 用户任务实体

mapper/
  ├── TaskMapper.java
  ├── UserTaskMapper.java
  └── xml/
      ├── TaskMapper.xml
      └── UserTaskMapper.xml

service/
  ├── TaskService.java
  └── impl/
      └── TaskServiceImpl.java

controller/
  └── task/
      └── TaskController.java

vo/
  ├── TaskVO.java              # 任务VO
  └── UserTaskVO.java          # 用户任务VO
```

### 8. 活动系统模块
需要创建的文件：
```
entity/
  ├── Activity.java            # 活动定义实体
  └── UserActivity.java        # 用户活动参与记录

mapper/
  ├── ActivityMapper.java
  ├── UserActivityMapper.java
  └── xml/
      ├── ActivityMapper.xml
      └── UserActivityMapper.xml

service/
  ├── ActivityService.java
  └── impl/
      └── ActivityServiceImpl.java

controller/
  └── activity/
      └── ActivityController.java

vo/
  ├── ActivityVO.java          # 活动VO
  └── ActivityQueryVO.java     # 查询VO
```

### 9. 社交系统模块
需要创建的文件：
```
entity/
  ├── UserRelation.java        # 用户关系实体
  ├── ShareRecord.java         # 分享记录实体
  └── InviteRecord.java        # 邀请记录实体

mapper/
  ├── UserRelationMapper.java
  ├── ShareRecordMapper.java
  ├── InviteRecordMapper.java
  └── xml/
      ├── UserRelationMapper.xml
      ├── ShareRecordMapper.xml
      └── InviteRecordMapper.xml

service/
  ├── SocialService.java
  └── impl/
      └── SocialServiceImpl.java

controller/
  └── social/
      └── SocialController.java

vo/
  ├── RelationVO.java          # 关系VO
  ├── ShareVO.java             # 分享VO
  └── InviteVO.java            # 邀请VO
```

### 10. 消息通知模块
需要创建的文件：
```
entity/
  ├── SystemMessage.java       # 系统消息实体
  └── UserMessage.java         # 用户消息实体

mapper/
  ├── SystemMessageMapper.java
  ├── UserMessageMapper.java
  └── xml/
      ├── SystemMessageMapper.xml
      └── UserMessageMapper.xml

service/
  ├── MessageService.java
  └── impl/
      └── MessageServiceImpl.java

controller/
  └── message/
      └── MessageController.java

vo/
  ├── MessageVO.java           # 消息VO
  └── MessageQueryVO.java      # 查询VO
```

### 11. 系统日志模块
需要创建的文件：
```
entity/
  ├── OperationLog.java        # 操作日志实体
  └── KnockSession.java        # 敲击会话实体

mapper/
  ├── OperationLogMapper.java
  ├── KnockSessionMapper.java
  └── xml/
      ├── OperationLogMapper.xml
      └── KnockSessionMapper.xml

service/
  ├── LogService.java
  └── impl/
      └── LogServiceImpl.java
```

### 12. 充值支付模块
需要创建的文件：
```
entity/
  ├── RechargeOrder.java       # 充值订单实体
  ├── VipPackage.java          # VIP套餐实体
  └── VipPurchase.java         # VIP购买记录

mapper/
  ├── RechargeOrderMapper.java
  ├── VipPackageMapper.java
  ├── VipPurchaseMapper.java
  └── xml/
      ├── RechargeOrderMapper.xml
      ├── VipPackageMapper.xml
      └── VipPurchaseMapper.xml

service/
  ├── PaymentService.java
  └── impl/
      └── PaymentServiceImpl.java

controller/
  └── payment/
      └── PaymentController.java

vo/
  ├── RechargeVO.java          # 充值VO
  ├── VipPackageVO.java        # VIP套餐VO
  └── PaymentQueryVO.java      # 查询VO
```

## 接口设计规范

### 1. 通用接口模式
每个模块通常包含以下接口：
- **POST /create** - 创建
- **PUT /update** - 更新
- **DELETE /delete/{id}** - 删除
- **GET /get/{id}** - 根据ID查询
- **POST /page** - 分页查询
- **GET /list** - 列表查询

### 2. 响应格式
所有接口统一返回`Result<T>`格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 3. 分页查询
使用MyBatis-Plus的`Page`对象，统一分页参数：
- pageNum: 页码（默认1）
- pageSize: 每页大小（默认10）

## 技术栈确认
- SpringBoot: 2.7.14
- JDK: 1.8
- MyBatis-Plus: 3.5.3.1
- MySQL: 8.0
- 主键策略: 雪花算法
- 逻辑删除: is_deleted字段

## 下一步行动
请指定需要优先完成的模块，我将为您创建完整的代码实现：
1. 捐赠系统模块
2. 道具商城模块
3. 排行榜系统模块
4. 成就系统模块
5. 任务系统模块
6. 活动系统模块
7. 社交系统模块
8. 消息通知模块
9. 系统日志模块
10. 充值支付模块

每个模块包含完整的Entity、Mapper(含XML)、Service、ServiceImpl、Controller和VO类。