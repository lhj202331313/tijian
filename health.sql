# ============================================================
# health 健康体检管理系统 完整数据库脚本
# 包含：建库、建表（11张）、测试数据
# 说明：orders 表含 doctor_id（接待医生，可空）+ 索引
# 规则：定义先定义主表，删除先删除从表
# ============================================================

# 定义和删除之前都需要先判断
# 定义先定义主表
# 删除先删除从表

drop database if exists health;
create database if not exists health;

use health;

-- ==================== 删除顺序：从表先删，主表后删 ====================
drop table if exists cidetailedreport;
drop table if exists overallresult;
drop table if exists cireport;
drop table if exists setmealdetailed;
drop table if exists orders;
drop table if exists checkitemdetailed;
drop table if exists setmeal;
drop table if exists doctor;
drop table if exists checkitem;
drop table if exists hospital;
drop table if exists users;

-- ==================== 建表顺序：主表先建，从表后建 ====================

-- 1. 用户表 users
create table if not exists users(
    id bigint auto_increment not null comment '用户主键',
    username varchar(50) not null comment '登录账号',
    password varchar(100) not null comment '加密密码',
    real_name varchar(50) comment '真实姓名',
    id_card varchar(18) comment '身份证号',
    phone varchar(11) comment '手机号',
    sex tinyint default 0 comment '性别 0未知 1男 2女',
    create_time datetime default current_timestamp comment '注册时间',
    status tinyint default 1 comment '状态 0禁用 1正常',
    primary key (`id`)
);

-- 2. 医院表 hospital
create table if not exists hospital(
    id bigint auto_increment not null comment '医院主键',
    name varchar(100) not null comment '医院名称',
    address varchar(200) comment '医院地址',
    phone varchar(20) comment '医院电话',
    business_hours varchar(100) comment '营业时间',
    rule_desc varchar(255) comment '预约规则说明',
    status tinyint default 1 comment '状态 0停用 1启用',
    primary key (`id`)
);

-- 3. 医生表 doctor（归属医院）
create table if not exists doctor(
    id bigint auto_increment not null comment '医生主键',
    username varchar(50) not null comment '医生登录账号',
    password varchar(100) not null comment '加密密码',
    real_name varchar(50) comment '医生姓名',
    department varchar(50) comment '所属科室',
    hospital_id bigint not null comment '所属医院id',
    phone varchar(11) comment '联系电话',
    create_time datetime default current_timestamp comment '创建时间',
    status tinyint default 1 comment '状态 0禁用 1正常',
    primary key (`id`),
    constraint fk_doctor_hospital foreign key (`hospital_id`) references hospital(`id`)
);

-- 4. 检查项目表 checkitem
create table if not exists checkitem(
    id bigint auto_increment not null comment '检查项目主键',
    name varchar(100) not null comment '检查项目名称',
    type varchar(50) comment '项目类型 一般/检验/影像/功能',
    sort_no int default 0 comment '排序号',
    status tinyint default 1 comment '状态 0停用 1启用',
    primary key (`id`)
);

-- 5. 体检套餐表 setmeal
create table if not exists setmeal(
    id bigint auto_increment not null comment '套餐主键',
    hospital_id bigint not null comment '所属医院id',
    name varchar(100) not null comment '套餐名称',
    type varchar(50) comment '套餐类型 个人/团体',
    price decimal(10,2) comment '套餐价格',
    description text comment '套餐描述',
    status tinyint default 1 comment '状态 0下架 1上架',
    primary key (`id`),
    constraint fk_setmeal_hospital foreign key (`hospital_id`) references hospital(`id`)
);

-- 6. 套餐-检查项关联表 setmealdetailed
create table if not exists setmealdetailed(
    id bigint auto_increment not null comment '关联主键',
    setmeal_id bigint not null comment '套餐id',
    checkitem_id bigint not null comment '检查项目id',
    primary key (`id`),
    constraint uk_setmeal_checkitem unique (`setmeal_id`, `checkitem_id`),
    constraint fk_detailed_setmeal foreign key (`setmeal_id`) references setmeal(`id`),
    constraint fk_detailed_checkitem foreign key (`checkitem_id`) references checkitem(`id`)
);

-- 7. 指标表 checkitemdetailed
create table if not exists checkitemdetailed(
    id bigint auto_increment not null comment '指标主键',
    checkitem_id bigint not null comment '所属检查项目id',
    name varchar(100) not null comment '指标名称',
    unit varchar(20) comment '单位',
    normal_range varchar(100) comment '参考范围',
    status tinyint default 1 comment '状态 0停用 1启用',
    primary key (`id`),
    constraint fk_itemdetailed_checkitem foreign key (`checkitem_id`) references checkitem(`id`)
);

-- 8. 预约订单表 orders（新增 doctor_id 接待医生，可空+索引）
create table if not exists orders(
    id bigint auto_increment not null comment '订单主键',
    order_no varchar(32) comment '订单编号',
    user_id bigint not null comment '预约用户id',
    hospital_id bigint not null comment '医院id',
    setmeal_id bigint not null comment '体检套餐id',
    doctor_id bigint null comment '接待医生id',
    order_date date not null comment '体检日期',
    order_status tinyint default 0 comment '订单状态 0待体检 1已完成 2已取消',
    create_time datetime default current_timestamp comment '下单时间',
    cancel_time datetime null comment '取消时间',
    primary key (`id`),
    index idx_orders_doctor (`doctor_id`),
    constraint fk_orders_user foreign key (`user_id`) references users(`id`),
    constraint fk_orders_hospital foreign key (`hospital_id`) references hospital(`id`),
    constraint fk_orders_setmeal foreign key (`setmeal_id`) references setmeal(`id`)
);

-- 9. 报告检查项表 cireport
create table if not exists cireport(
    id bigint auto_increment not null comment '报告检查项主键',
    order_id bigint not null comment '订单id',
    checkitem_id bigint not null comment '检查项目id',
    result_status tinyint default 0 comment '结果状态 0正常 1异常',
    report_time datetime null comment '检查完成时间',
    primary key (`id`),
    constraint uk_ci_order_item unique (`order_id`, `checkitem_id`),
    constraint fk_cireport_order foreign key (`order_id`) references orders(`id`),
    constraint fk_cireport_checkitem foreign key (`checkitem_id`) references checkitem(`id`)
);

-- 10. 检查结果明细表 cidetailedreport
create table if not exists cidetailedreport(
    id bigint auto_increment not null comment '结果明细主键',
    cireport_id bigint not null comment '报告检查项id',
    checkitemdetailed_id bigint not null comment '指标id',
    result_value varchar(50) comment '检查结果值',
    is_abnormal tinyint default 0 comment '是否异常 0正常 1异常',
    create_time datetime default current_timestamp comment '录入时间',
    primary key (`id`),
    constraint fk_cidetail_cireport foreign key (`cireport_id`) references cireport(`id`),
    constraint fk_cidetail_item foreign key (`checkitemdetailed_id`) references checkitemdetailed(`id`)
);

-- 11. 总检结论表 overallresult
create table if not exists overallresult(
    id bigint auto_increment not null comment '总检结论主键',
    order_id bigint not null unique comment '订单id（一次体检一条总检）',
    doctor_id bigint not null comment '总检医生id',
    summary text comment '总检结论与建议',
    doctor_name varchar(50) comment '总检医生姓名（冗余，方便直接展示）',
    result_status tinyint default 0 comment '总检状态 0正常 1有异常',
    create_time datetime default current_timestamp comment '生成时间',
    publish_time datetime null comment '发布时间',
    status tinyint default 0 comment '状态 0草稿 1已发布',
    primary key (`id`),
    constraint fk_overall_order foreign key (`order_id`) references orders(`id`),
    constraint fk_overall_doctor foreign key (`doctor_id`) references doctor(`id`)
);

-- ==================== 插入数据：主表先插，从表后插 ====================

-- 1. 用户表 users（10人）
insert into users(id, username, password, real_name, id_card, phone, sex, create_time, status) values
(1,  'zhangsan',  '123456', '张三',   '410101199801011234', '13800001001', 1, '2026-09-15 14:21:20', 1),
(2,  'lisi',      '123456', '李四',   '410101199502022345', '13800001002', 1, '2026-09-15 14:21:20', 1),
(3,  'wangwu',    '123456', '王五',   '410101197203033456', '13800001003', 1, '2026-09-15 14:21:20', 1),
(4,  'zhaoliu',   '123456', '赵六',   '410101199011044567', '13800001004', 2, '2026-09-15 14:21:20', 1),
(5,  'qianqi',    '123456', '钱七',   '410101195803055678', '13800001005', 1, '2026-09-15 14:21:20', 1),
(6,  'sunba',     '123456', '孙八',   '410101199212066789', '13800001006', 2, '2026-09-15 14:21:20', 1),
(7,  'zhoujiu',   '123456', '周九',   '410101199405077890', '13800001007', 1, '2026-09-15 14:21:20', 1),
(8,  'wushi',     '123456', '吴十',   '410101199708088901', '13800001008', 2, '2026-09-15 14:21:20', 1),
(9,  'zhengshiyi','123456', '郑十一', '410101200103099012', '13800001009', 1, '2026-09-15 14:21:20', 1),
(10, 'fengshier', '123456', '冯十二', '410101199610101023', '13800001010', 2, '2026-09-15 14:21:20', 1);

-- 2. 医院表 hospital（3家）
insert into hospital(id, name, address, phone, business_hours, rule_desc, status) values
(1, '郑州市第一人民医院体检中心', '郑州市中原区建设路1号',   '0371-67000001', '周一至周六 7:30-11:30', '提前1天预约，需空腹', 1),
(2, '郑州美年大健康体检中心',     '郑州市金水区花园路56号',  '0371-67000002', '周一至周日 7:30-12:00', '提前3天预约，周末可检', 1),
(3, '河南省人民医院健康管理中心', '郑州市金水区纬五路7号',   '0371-67000003', '周一至周五 7:30-11:30', '提前7天预约，专家坐诊', 1);

-- 3. 医生表 doctor（6人，归属医院）
insert into doctor(id, username, password, real_name, department, hospital_id, phone, create_time, status) values
(1, 'sunyisheng',  '123456', '孙建国', '内科',   1, '13900002001', '2026-09-15 14:21:20', 1),
(2, 'zhouyisheng', '123456', '周明华', '外科',   1, '13900002002', '2026-09-15 14:21:20', 1),
(3, 'wuyisheng',   '123456', '吴丽娟', '放射科', 2, '13900002003', '2026-09-15 14:21:20', 1),
(4, 'zhengyisheng','123456', '郑国平', '检验科', 2, '13900002004', '2026-09-15 14:21:20', 1),
(5, 'wangdefang',  '123456', '王德芳', '内科',   3, '13900002005', '2026-09-15 14:21:20', 1),
(6, 'lihuimin',    '123456', '李慧敏', '超声科', 3, '13900002006', '2026-09-15 14:21:20', 1);

-- 4. 检查项目表 checkitem（10项）
insert into checkitem(id, name, type, sort_no, status) values
(1,  '一般检查',   '一般', 1, 1),
(2,  '血常规',     '检验', 2, 1),
(3,  '尿常规',     '检验', 3, 1),
(4,  '肝功能',     '检验', 4, 1),
(5,  '肾功能',     '检验', 5, 1),
(6,  '血脂',       '检验', 6, 1),
(7,  '胸透',       '影像', 7, 1),
(8,  '腹部B超',    '影像', 8, 1),
(9,  '心电图',     '功能', 9, 1),
(10, '甲状腺彩超', '影像', 10, 1);

-- 5. 体检套餐表 setmeal（8个）
insert into setmeal(id, hospital_id, name, type, price, description, status) values
(1, 1, '入职基础套餐',   '个人体检', 199.00,  '一般检查、血常规、尿常规、胸透', 1),
(2, 1, '白领健康套餐',   '个人体检', 399.00,  '一般检查、血常规、尿常规、肝功能、胸透、腹部B超', 1),
(3, 1, '中老年全面套餐', '个人体检', 899.00,  '一般检查、血常规、尿常规、肝功能、肾功能、血脂、胸透、腹部B超、心电图、甲状腺彩超', 1),
(4, 2, '基础体检套餐',   '个人体检', 159.00,  '一般检查、血常规、尿常规、胸透', 1),
(5, 2, '青年标准套餐',   '团体体检', 359.00,  '一般检查、血常规、尿常规、肝功能、腹部B超', 1),
(6, 2, '女性关爱套餐',   '个人体检', 599.00,  '一般检查、血常规、尿常规、肝功能、腹部B超、甲状腺彩超', 1),
(7, 3, '全面体检套餐',   '个人体检', 799.00,  '一般检查、血常规、尿常规、肝功能、肾功能、血脂、胸透、腹部B超、心电图', 1),
(8, 3, '高端深度套餐',   '个人体检', 1299.00, '一般检查、血常规、尿常规、肝功能、肾功能、血脂、胸透、腹部B超、心电图、甲状腺彩超', 1);

-- 6. 套餐-检查项关联表 setmealdetailed（54条）
insert into setmealdetailed(id, setmeal_id, checkitem_id) values
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 7),
(5, 2, 1), (6, 2, 2), (7, 2, 3), (8, 2, 4), (9, 2, 7), (10, 2, 8),
(11, 3, 1), (12, 3, 2), (13, 3, 3), (14, 3, 4), (15, 3, 5), (16, 3, 6),
(17, 3, 7), (18, 3, 8), (19, 3, 9), (20, 3, 10),
(21, 4, 1), (22, 4, 2), (23, 4, 3), (24, 4, 7),
(25, 5, 1), (26, 5, 2), (27, 5, 3), (28, 5, 4), (29, 5, 8),
(30, 6, 1), (31, 6, 2), (32, 6, 3), (33, 6, 4), (34, 6, 8), (35, 6, 10),
(36, 7, 1), (37, 7, 2), (38, 7, 3), (39, 7, 4), (40, 7, 5), (41, 7, 6),
(42, 7, 7), (43, 7, 8), (44, 7, 9),
(45, 8, 1), (46, 8, 2), (47, 8, 3), (48, 8, 4), (49, 8, 5), (50, 8, 6),
(51, 8, 7), (52, 8, 8), (53, 8, 9), (54, 8, 10);

-- 7. 指标表 checkitemdetailed（24个，含影像/功能类结论指标）
insert into checkitemdetailed(id, checkitem_id, name, unit, normal_range, status) values
(1,  1, '身高',       'cm',     '男155-190', 1),
(2,  1, '体重',       'kg',     '40-90',     1),
(3,  1, '血压',       'mmHg',   '90-140/60-90', 1),
(4,  2, '白细胞计数', '10^9/L', '4.0-10.0',  1),
(5,  2, '红细胞计数', '10^12/L','3.5-5.5',   1),
(6,  2, '血红蛋白',   'g/L',    '120-160',   1),
(7,  2, '血小板计数', '10^9/L', '100-300',   1),
(8,  4, '谷丙转氨酶', 'U/L',    '0-40',      1),
(9,  4, '谷草转氨酶', 'U/L',    '0-40',      1),
(10, 5, '肌酐',       'umol/L', '44-133',    1),
(11, 5, '尿素氮',     'mmol/L', '2.9-8.2',   1),
(12, 6, '总胆固醇',   'mmol/L', '2.8-5.2',   1),
(13, 6, '甘油三酯',   'mmol/L', '0.4-1.7',   1),
(14, 3, '尿蛋白',     '—',      '阴性',      1),
(15, 3, '尿糖',       '—',      '阴性',      1),
(16, 7, '检查结论',   null,     '心、肺、膈未见异常', 1),
(17, 8, '肝脏',       null,     '未见异常',   1),
(18, 8, '胆囊',       null,     '未见异常',   1),
(19, 8, '胰腺',       null,     '未见异常',   1),
(20, 8, '脾脏',       null,     '未见异常',   1),
(21, 8, '双肾',       null,     '未见异常',   1),
(22, 9, '心率',       '次/分',  '60-100',     1),
(23, 9, '心电图结论', null,     '窦性心律，正常心电图', 1),
(24, 10, '检查结论',  null,     '甲状腺未见异常', 1);

-- 8. 预约订单表 orders（14条，含接待医生doctor_id）
insert into orders(id, order_no, user_id, hospital_id, setmeal_id, doctor_id, order_date, order_status, create_time, cancel_time) values
(1,  'TJ20260820001', 1,  1, 2, 1,    '2026-08-20', 1, '2026-08-10 09:30:00', null),
(2,  'TJ20260910002', 2,  2, 5, 3,    '2026-09-10', 1, '2026-09-01 10:00:00', null),
(3,  'TJ20260918003', 3,  3, 7, 5,    '2026-09-18', 0, '2026-09-08 14:20:00', null),
(4,  'TJ20260905004', 4,  1, 1, 1,    '2026-09-05', 1, '2026-08-28 08:45:00', null),
(5,  'TJ20260710005', 1,  1, 1, null, '2026-07-10', 2, '2026-06-25 11:00:00', '2026-07-01 09:00:00'),
(6,  'TJ20260925006', 2,  2, 4, 3,    '2026-09-25', 0, '2026-09-12 16:10:00', null),
(7,  'TJ20260825007', 5,  1, 3, 1,    '2026-08-25', 1, '2026-08-15 09:00:00', null),
(8,  'TJ20260908008', 6,  2, 6, 3,    '2026-09-08', 1, '2026-08-30 10:30:00', null),
(9,  'TJ20260912009', 7,  3, 8, 5,    '2026-09-12', 1, '2026-09-02 15:00:00', null),
(10, 'TJ20260902010', 8,  1, 2, 1,    '2026-09-02', 1, '2026-08-25 08:20:00', null),
(11, 'TJ20260920011', 9,  2, 5, 3,    '2026-09-20', 0, '2026-09-10 13:40:00', null),
(12, 'TJ20260901012', 10, 3, 7, 5,    '2026-09-01', 1, '2026-08-22 11:10:00', null),
(13, 'TJ20260615013', 5,  1, 1, null, '2026-06-15', 2, '2026-06-05 09:00:00', '2026-06-10 10:00:00'),
(14, 'TJ20260830014', 3,  2, 4, 3,    '2026-08-30', 1, '2026-08-20 14:30:00', null);

-- 9. 报告检查项表 cireport（55条，对应8个已完成订单）
insert into cireport(id, order_id, checkitem_id, result_status, report_time) values
(1,  1, 1, 0, '2026-08-20 08:50:00'), (2,  1, 2, 0, '2026-08-20 09:10:00'),
(3,  1, 3, 0, '2026-08-20 09:20:00'), (4,  1, 4, 1, '2026-08-20 10:00:00'),
(5,  1, 7, 0, '2026-08-20 10:20:00'), (6,  1, 8, 0, '2026-08-20 10:40:00'),
(7,  4, 1, 0, '2026-09-05 08:45:00'), (8,  4, 2, 0, '2026-09-05 09:00:00'),
(9,  4, 3, 0, '2026-09-05 09:15:00'), (10, 4, 7, 0, '2026-09-05 09:30:00'),
(11, 7, 1, 1, '2026-08-25 08:40:00'), (12, 7, 2, 0, '2026-08-25 09:00:00'),
(13, 7, 3, 0, '2026-08-25 09:10:00'), (14, 7, 4, 0, '2026-08-25 09:30:00'),
(15, 7, 5, 0, '2026-08-25 09:50:00'), (16, 7, 6, 1, '2026-08-25 10:00:00'),
(17, 7, 7, 0, '2026-08-25 10:20:00'), (18, 7, 8, 0, '2026-08-25 10:40:00'),
(19, 7, 9, 0, '2026-08-25 11:00:00'), (20, 7, 10, 0, '2026-08-25 11:10:00'),
(21, 8, 1, 0, '2026-09-08 08:30:00'), (22, 8, 2, 0, '2026-09-08 08:50:00'),
(23, 8, 3, 0, '2026-09-08 09:00:00'), (24, 8, 4, 0, '2026-09-08 09:20:00'),
(25, 8, 8, 0, '2026-09-08 09:40:00'), (26, 8, 10, 0, '2026-09-08 10:00:00'),
(27, 9, 1, 0, '2026-09-12 08:30:00'), (28, 9, 2, 0, '2026-09-12 08:50:00'),
(29, 9, 3, 0, '2026-09-12 09:00:00'), (30, 9, 4, 0, '2026-09-12 09:20:00'),
(31, 9, 5, 0, '2026-09-12 09:40:00'), (32, 9, 6, 0, '2026-09-12 09:50:00'),
(33, 9, 7, 0, '2026-09-12 10:10:00'), (34, 9, 8, 0, '2026-09-12 10:30:00'),
(35, 9, 9, 0, '2026-09-12 10:50:00'), (36, 9, 10, 1, '2026-09-12 11:00:00'),
(37, 10, 1, 0, '2026-09-02 08:40:00'), (38, 10, 2, 0, '2026-09-02 09:00:00'),
(39, 10, 3, 0, '2026-09-02 09:10:00'), (40, 10, 4, 0, '2026-09-02 09:30:00'),
(41, 10, 7, 0, '2026-09-02 09:50:00'), (42, 10, 8, 0, '2026-09-02 10:10:00'),
(43, 12, 1, 0, '2026-09-01 08:30:00'), (44, 12, 2, 0, '2026-09-01 08:50:00'),
(45, 12, 3, 0, '2026-09-01 09:00:00'), (46, 12, 4, 0, '2026-09-01 09:20:00'),
(47, 12, 5, 0, '2026-09-01 09:40:00'), (48, 12, 6, 0, '2026-09-01 09:50:00'),
(49, 12, 7, 0, '2026-09-01 10:10:00'), (50, 12, 8, 0, '2026-09-01 10:30:00'),
(51, 12, 9, 0, '2026-09-01 10:50:00'),
(52, 14, 1, 0, '2026-08-30 08:40:00'), (53, 14, 2, 0, '2026-08-30 09:00:00'),
(54, 14, 3, 0, '2026-08-30 09:15:00'), (55, 14, 7, 0, '2026-08-30 09:30:00');

-- 10. 检查结果明细表 cidetailedreport（60条）
insert into cidetailedreport(id, cireport_id, checkitemdetailed_id, result_value, is_abnormal, create_time) values
(1,  1, 1, '172',    0, '2026-08-20 08:50:00'),
(2,  1, 2, '68',     0, '2026-08-20 08:50:00'),
(3,  1, 3, '118/76', 0, '2026-08-20 08:50:00'),
(4,  2, 4, '6.5',    0, '2026-08-20 09:10:00'),
(5,  2, 5, '4.8',    0, '2026-08-20 09:10:00'),
(6,  2, 6, '145',    0, '2026-08-20 09:10:00'),
(7,  2, 7, '220',    0, '2026-08-20 09:10:00'),
(8,  4, 8, '68',     1, '2026-08-20 10:00:00'),
(9,  4, 9, '35',     0, '2026-08-20 10:00:00'),
(10, 7, 1, '165',    0, '2026-09-05 08:45:00'),
(11, 7, 2, '52',     0, '2026-09-05 08:45:00'),
(12, 7, 3, '110/70', 0, '2026-09-05 08:45:00'),
(13, 8, 4, '7.2',    0, '2026-09-05 09:00:00'),
(14, 8, 5, '4.5',    0, '2026-09-05 09:00:00'),
(15, 8, 6, '138',    0, '2026-09-05 09:00:00'),
(16, 8, 7, '180',    0, '2026-09-05 09:00:00'),
(17, 11, 1, '170',    0, '2026-08-25 08:40:00'),
(18, 11, 2, '72',     0, '2026-08-25 08:40:00'),
(19, 11, 3, '152/95', 1, '2026-08-25 08:40:00'),
(20, 12, 4, '5.6',    0, '2026-08-25 09:00:00'),
(21, 12, 5, '4.2',    0, '2026-08-25 09:00:00'),
(22, 12, 6, '128',    0, '2026-08-25 09:00:00'),
(23, 12, 7, '195',    0, '2026-08-25 09:00:00'),
(24, 14, 8, '32',     0, '2026-08-25 09:30:00'),
(25, 14, 9, '28',     0, '2026-08-25 09:30:00'),
(26, 15, 10, '82',    0, '2026-08-25 09:50:00'),
(27, 15, 11, '5.6',   0, '2026-08-25 09:50:00'),
(28, 16, 12, '6.8',   1, '2026-08-25 10:00:00'),
(29, 16, 13, '2.3',   1, '2026-08-25 10:00:00'),
(30, 21, 1, '160',    0, '2026-09-08 08:30:00'),
(31, 21, 2, '50',     0, '2026-09-08 08:30:00'),
(32, 21, 3, '105/68', 0, '2026-09-08 08:30:00'),
(33, 22, 4, '5.0',    0, '2026-09-08 08:50:00'),
(34, 22, 5, '4.6',    0, '2026-09-08 08:50:00'),
(35, 22, 6, '126',    0, '2026-09-08 08:50:00'),
(36, 22, 7, '205',    0, '2026-09-08 08:50:00'),
(37, 24, 8, '25',     0, '2026-09-08 09:20:00'),
(38, 24, 9, '22',     0, '2026-09-08 09:20:00'),
(39, 28, 4, '5.9',    0, '2026-09-12 08:50:00'),
(40, 28, 5, '4.9',    0, '2026-09-12 08:50:00'),
(41, 28, 6, '152',    0, '2026-09-12 08:50:00'),
(42, 28, 7, '230',    0, '2026-09-12 08:50:00'),
(43, 31, 10, '76',    0, '2026-09-12 09:40:00'),
(44, 31, 11, '4.8',   0, '2026-09-12 09:40:00'),
(45, 32, 12, '4.6',   0, '2026-09-12 09:50:00'),
(46, 32, 13, '1.2',   0, '2026-09-12 09:50:00'),
(47, 38, 4, '6.1',    0, '2026-09-02 09:00:00'),
(48, 38, 5, '4.4',    0, '2026-09-02 09:00:00'),
(49, 38, 6, '132',    0, '2026-09-02 09:00:00'),
(50, 38, 7, '198',    0, '2026-09-02 09:00:00'),
(51, 44, 4, '5.4',    0, '2026-09-01 08:50:00'),
(52, 44, 5, '4.7',    0, '2026-09-01 08:50:00'),
(53, 44, 6, '141',    0, '2026-09-01 08:50:00'),
(54, 44, 7, '215',    0, '2026-09-01 08:50:00'),
(55, 46, 8, '28',     0, '2026-09-01 09:20:00'),
(56, 46, 9, '26',     0, '2026-09-01 09:20:00'),
(57, 53, 4, '6.8',    0, '2026-08-30 09:00:00'),
(58, 53, 5, '5.1',    0, '2026-08-30 09:00:00'),
(59, 53, 6, '148',    0, '2026-08-30 09:00:00'),
(60, 53, 7, '185',    0, '2026-08-30 09:00:00');

-- 11. 总检结论表 overallresult（8条）
insert into overallresult(id, order_id, doctor_id, summary, doctor_name, result_status, create_time, publish_time, status) values
(1, 1,  1, '谷丙转氨酶偏高（68 U/L），建议复查肝功能，清淡饮食、戒酒、规律作息；其余项目未见明显异常。', '孙建国', 1, '2026-08-21 10:30:00', '2026-08-21 15:00:00', 1),
(2, 4,  2, '各检查项目均未见明显异常，体检结论：健康。', '周明华', 0, '2026-09-06 11:00:00', '2026-09-06 16:00:00', 1),
(3, 7,  1, '血压偏高（152/95 mmHg）、总胆固醇与甘油三酯偏高，建议低盐低脂饮食、适当运动，一月后复查血压血脂。', '孙建国', 1, '2026-08-26 10:00:00', '2026-08-26 15:30:00', 1),
(4, 8,  4, '各检查项目均未见明显异常，体检结论：健康。', '郑国平', 0, '2026-09-09 09:00:00', '2026-09-09 14:00:00', 1),
(5, 9,  5, '甲状腺彩超提示：右侧甲状腺结节（TI-RADS 3类），建议定期随访，半年后复查甲状腺彩超及甲功。', '王德芳', 1, '2026-09-13 10:00:00', '2026-09-13 16:00:00', 1),
(6, 10, 2, '各检查项目均未见明显异常，体检结论：健康。', '周明华', 0, '2026-09-03 09:30:00', '2026-09-03 15:00:00', 1),
(7, 12, 6, '各检查项目均未见明显异常，体检结论：健康。', '李慧敏', 0, '2026-09-02 10:30:00', '2026-09-02 16:00:00', 1),
(8, 14, 3, '各检查项目均未见明显异常，体检结论：健康。', '吴丽娟', 0, '2026-08-31 09:00:00', '2026-08-31 14:30:00', 1);
-- （注：内容由AI生成）
