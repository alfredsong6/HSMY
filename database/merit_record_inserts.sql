-- 功德记录测试数据插入语句
-- userid: 1968213494687993856
-- knock_type: 1-普通敲击, 2-连击敲击
-- source: 1-木鱼, 2-钟声, 3-鼓声, 4-念经, 5-供香

-- 2024年11月数据（第1周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1001, 1968213494687993856, 100, 1, 1, 'session_001', 1, 1.0, '普通敲击木鱼', 'system', '2024-11-01 08:30:00', 'system', '2024-11-01 08:30:00', 0),
(1002, 1968213494687993856, 150, 2, 1, 'session_001', 5, 1.5, '连击敲击木鱼x5', 'system', '2024-11-01 09:15:00', 'system', '2024-11-01 09:15:00', 0),
(1003, 1968213494687993856, 120, 1, 2, 'session_002', 1, 1.0, '普通敲击钟声', 'system', '2024-11-02 10:00:00', 'system', '2024-11-02 10:00:00', 0),
(1004, 1968213494687993856, 200, 2, 3, 'session_002', 10, 2.0, '连击敲击鼓声x10', 'system', '2024-11-03 14:20:00', 'system', '2024-11-03 14:20:00', 0);

-- 2024年11月数据（第2周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1005, 1968213494687993856, 80, 1, 4, 'session_003', 1, 1.0, '普通念经', 'system', '2024-11-05 07:00:00', 'system', '2024-11-05 07:00:00', 0),
(1006, 1968213494687993856, 300, 2, 1, 'session_003', 15, 3.0, '连击敲击木鱼x15', 'system', '2024-11-06 18:30:00', 'system', '2024-11-06 18:30:00', 0),
(1007, 1968213494687993856, 90, 1, 5, 'session_004', 1, 1.0, '普通供香', 'system', '2024-11-07 11:45:00', 'system', '2024-11-07 11:45:00', 0),
(1008, 1968213494687993856, 250, 2, 2, 'session_004', 8, 2.5, '连击敲击钟声x8', 'system', '2024-11-08 16:00:00', 'system', '2024-11-08 16:00:00', 0);

-- 2024年11月数据（第3周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1009, 1968213494687993856, 110, 1, 3, 'session_005', 1, 1.0, '普通敲击鼓声', 'system', '2024-11-12 09:30:00', 'system', '2024-11-12 09:30:00', 0),
(1010, 1968213494687993856, 180, 2, 4, 'session_005', 6, 1.8, '连击念经x6', 'system', '2024-11-13 13:15:00', 'system', '2024-11-13 13:15:00', 0),
(1011, 1968213494687993856, 130, 1, 1, 'session_006', 1, 1.0, '普通敲击木鱼', 'system', '2024-11-14 08:00:00', 'system', '2024-11-14 08:00:00', 0),
(1012, 1968213494687993856, 400, 2, 5, 'session_006', 20, 4.0, '连击供香x20', 'system', '2024-11-15 20:30:00', 'system', '2024-11-15 20:30:00', 0);

-- 2024年11月数据（第4周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1013, 1968213494687993856, 95, 1, 2, 'session_007', 1, 1.0, '普通敲击钟声', 'system', '2024-11-20 06:45:00', 'system', '2024-11-20 06:45:00', 0),
(1014, 1968213494687993856, 220, 2, 3, 'session_007', 11, 2.2, '连击敲击鼓声x11', 'system', '2024-11-21 15:20:00', 'system', '2024-11-21 15:20:00', 0),
(1015, 1968213494687993856, 105, 1, 4, 'session_008', 1, 1.0, '普通念经', 'system', '2024-11-22 12:00:00', 'system', '2024-11-22 12:00:00', 0),
(1016, 1968213494687993856, 350, 2, 1, 'session_008', 18, 3.5, '连击敲击木鱼x18', 'system', '2024-11-23 19:45:00', 'system', '2024-11-23 19:45:00', 0);

-- 2024年12月数据（第1周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1017, 1968213494687993856, 125, 1, 5, 'session_009', 1, 1.0, '普通供香', 'system', '2024-12-01 07:30:00', 'system', '2024-12-01 07:30:00', 0),
(1018, 1968213494687993856, 160, 2, 2, 'session_009', 7, 1.6, '连击敲击钟声x7', 'system', '2024-12-02 10:15:00', 'system', '2024-12-02 10:15:00', 0),
(1019, 1968213494687993856, 115, 1, 1, 'session_010', 1, 1.0, '普通敲击木鱼', 'system', '2024-12-03 14:00:00', 'system', '2024-12-03 14:00:00', 0),
(1020, 1968213494687993856, 280, 2, 3, 'session_010', 14, 2.8, '连击敲击鼓声x14', 'system', '2024-12-04 17:30:00', 'system', '2024-12-04 17:30:00', 0);

-- 2024年12月数据（第2周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1021, 1968213494687993856, 85, 1, 4, 'session_011', 1, 1.0, '普通念经', 'system', '2024-12-08 08:00:00', 'system', '2024-12-08 08:00:00', 0),
(1022, 1968213494687993856, 190, 2, 5, 'session_011', 9, 1.9, '连击供香x9', 'system', '2024-12-09 11:30:00', 'system', '2024-12-09 11:30:00', 0),
(1023, 1968213494687993856, 140, 1, 2, 'session_012', 1, 1.0, '普通敲击钟声', 'system', '2024-12-10 13:45:00', 'system', '2024-12-10 13:45:00', 0),
(1024, 1968213494687993856, 320, 2, 1, 'session_012', 16, 3.2, '连击敲击木鱼x16', 'system', '2024-12-11 16:15:00', 'system', '2024-12-11 16:15:00', 0);

-- 2024年12月数据（第3周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1025, 1968213494687993856, 98, 1, 3, 'session_013', 1, 1.0, '普通敲击鼓声', 'system', '2024-12-15 09:00:00', 'system', '2024-12-15 09:00:00', 0),
(1026, 1968213494687993856, 240, 2, 4, 'session_013', 12, 2.4, '连击念经x12', 'system', '2024-12-16 14:30:00', 'system', '2024-12-16 14:30:00', 0),
(1027, 1968213494687993856, 135, 1, 5, 'session_014', 1, 1.0, '普通供香', 'system', '2024-12-17 10:00:00', 'system', '2024-12-17 10:00:00', 0),
(1028, 1968213494687993856, 380, 2, 2, 'session_014', 19, 3.8, '连击敲击钟声x19', 'system', '2024-12-18 18:45:00', 'system', '2024-12-18 18:45:00', 0);

-- 2025年1月数据（第1周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1029, 1968213494687993856, 145, 1, 1, 'session_015', 1, 1.0, '普通敲击木鱼', 'system', '2025-01-01 00:30:00', 'system', '2025-01-01 00:30:00', 0),
(1030, 1968213494687993856, 260, 2, 3, 'session_015', 13, 2.6, '连击敲击鼓声x13', 'system', '2025-01-02 08:15:00', 'system', '2025-01-02 08:15:00', 0),
(1031, 1968213494687993856, 108, 1, 4, 'session_016', 1, 1.0, '普通念经', 'system', '2025-01-03 12:30:00', 'system', '2025-01-03 12:30:00', 0),
(1032, 1968213494687993856, 420, 2, 5, 'session_016', 21, 4.2, '连击供香x21', 'system', '2025-01-04 19:00:00', 'system', '2025-01-04 19:00:00', 0);

-- 2025年1月数据（第2周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1033, 1968213494687993856, 118, 1, 2, 'session_017', 1, 1.0, '普通敲击钟声', 'system', '2025-01-06 07:45:00', 'system', '2025-01-06 07:45:00', 0),
(1034, 1968213494687993856, 170, 2, 1, 'session_017', 8, 1.7, '连击敲击木鱼x8', 'system', '2025-01-07 15:00:00', 'system', '2025-01-07 15:00:00', 0),
(1035, 1968213494687993856, 155, 1, 3, 'session_018', 1, 1.0, '普通敲击鼓声', 'system', '2025-01-08 11:20:00', 'system', '2025-01-08 11:20:00', 0),
(1036, 1968213494687993856, 340, 2, 4, 'session_018', 17, 3.4, '连击念经x17', 'system', '2025-01-09 20:15:00', 'system', '2025-01-09 20:15:00', 0);

-- 2025年1月数据（第3周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1037, 1968213494687993856, 128, 1, 5, 'session_019', 1, 1.0, '普通供香', 'system', '2025-01-13 09:30:00', 'system', '2025-01-13 09:30:00', 0),
(1038, 1968213494687993856, 210, 2, 2, 'session_019', 10, 2.1, '连击敲击钟声x10', 'system', '2025-01-14 13:00:00', 'system', '2025-01-14 13:00:00', 0),
(1039, 1968213494687993856, 102, 1, 1, 'session_020', 1, 1.0, '普通敲击木鱼', 'system', '2025-01-15 16:45:00', 'system', '2025-01-15 16:45:00', 0),
(1040, 1968213494687993856, 500, 2, 3, 'session_020', 25, 5.0, '连击敲击鼓声x25(最高连击)', 'system', '2025-01-16 21:30:00', 'system', '2025-01-16 21:30:00', 0);

-- 2025年1月数据（第4周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1041, 1968213494687993856, 112, 1, 4, 'session_021', 1, 1.0, '普通念经', 'system', '2025-01-20 08:00:00', 'system', '2025-01-20 08:00:00', 0),
(1042, 1968213494687993856, 270, 2, 5, 'session_021', 12, 2.7, '连击供香x12', 'system', '2025-01-21 14:30:00', 'system', '2025-01-21 14:30:00', 0),
(1043, 1968213494687993856, 122, 1, 2, 'session_022', 1, 1.0, '普通敲击钟声', 'system', '2025-01-22 10:15:00', 'system', '2025-01-22 10:15:00', 0),
(1044, 1968213494687993856, 360, 2, 1, 'session_022', 18, 3.6, '连击敲击木鱼x18', 'system', '2025-01-23 17:45:00', 'system', '2025-01-23 17:45:00', 0);

-- 2025年1月数据（第5周）
INSERT INTO t_merit_record (id, user_id, merit_gained, knock_type, source, session_id, combo_count, bonus_rate, description, create_by, create_time, update_by, update_time, is_deleted) VALUES
(1045, 1968213494687993856, 138, 1, 3, 'session_023', 1, 1.0, '普通敲击鼓声', 'system', '2025-01-27 09:00:00', 'system', '2025-01-27 09:00:00', 0),
(1046, 1968213494687993856, 230, 2, 4, 'session_023', 11, 2.3, '连击念经x11', 'system', '2025-01-28 13:20:00', 'system', '2025-01-28 13:20:00', 0),
(1047, 1968213494687993856, 150, 1, 5, 'session_024', 1, 1.0, '普通供香', 'system', '2025-01-29 11:00:00', 'system', '2025-01-29 11:00:00', 0),
(1048, 1968213494687993856, 450, 2, 2, 'session_024', 22, 4.5, '连击敲击钟声x22', 'system', '2025-01-30 19:30:00', 'system', '2025-01-30 19:30:00', 0);

-- 统计信息（注释）
-- 总记录数：48条
-- ID范围：1001-1048
-- knock_type分布：1(普通敲击)24条，2(连击敲击)24条
-- source分布：1(木鱼)10条，2(钟声)10条，3(鼓声)10条，4(念经)9条，5(供香)9条
-- 时间跨度：2024年11月 - 2025年1月
-- 包含多个周和月的数据，便于测试周报和月报功能