use vamapi;

-- 用户调用接口关系表
create table if not exists `user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户ID',
    `interfaceInfoId` bigint not null comment '接口ID',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '0-正常，1-禁用',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDeleted` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口关系';


-- ----------------------------
-- Table structure for interface_log
-- ----------------------------
DROP TABLE IF EXISTS `interface_log`;
CREATE TABLE `interface_log`  (
                                  `id` bigint NOT NULL COMMENT 'id',
                                  `interfaceId` bigint NOT NULL COMMENT '接口id',
                                  `requestTime` datetime NULL DEFAULT NULL COMMENT '请求时间',
                                  `requestMethod` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求方式',
                                  `requestUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求地址',
                                  `requestContentLength` bigint NULL DEFAULT NULL COMMENT '请求长度（上传流量）',
                                  `responseStatusCode` int NULL DEFAULT NULL COMMENT '响应码',
                                  `responseContentLength` bigint NULL DEFAULT NULL COMMENT '响应长度（下载流量）',
                                  `requestDuration` bigint NULL DEFAULT NULL COMMENT '请求处理时间',
                                  `userId` bigint NULL DEFAULT NULL COMMENT '用户id',
                                  `clientIp` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户ip',
                                  `createTime` datetime default CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
                                  `updateTime` datetime default CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `isDelete` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- View structure for interface_info_proportion
-- ----------------------------
DROP VIEW IF EXISTS `interface_info_proportion`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `interface_info_proportion` AS select `ii`.`name` AS `name`,(`ii`.`totalInvokes` / (select sum(`interface_info`.`totalInvokes`) from `interface_info`)) AS `ratio` from `interface_info` `ii`;

-- ----------------------------
-- View structure for interface_log_week_count
-- ----------------------------
DROP VIEW IF EXISTS `interface_log_week_count`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `interface_log_week_count` AS select `d`.`day` AS `day`,count(`il`.`createTime`) AS `count` from ((select (curdate() - interval (`a`.`a` + (10 * `b`.`a`)) day) AS `day` from ((select 0 AS `a` union all select 1 AS `1` union all select 2 AS `2` union all select 3 AS `3` union all select 4 AS `4` union all select 5 AS `5` union all select 6 AS `6` union all select 7 AS `7`) `a` join (select 0 AS `a` union all select 1 AS `1` union all select 2 AS `2` union all select 3 AS `3` union all select 4 AS `4` union all select 5 AS `5` union all select 6 AS `6` union all select 7 AS `7`) `b`)) `d` left join `interface_log` `il` on((cast(`il`.`createTime` as date) = `d`.`day`))) where (`d`.`day` between (curdate() - interval 6 day) and curdate()) group by `d`.`day` order by `d`.`day`;
