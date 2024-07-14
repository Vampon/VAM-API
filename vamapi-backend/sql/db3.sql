use vamapi;

# -- 用户凭证下载表
# create table if not exists user_voucherUrl_info
# (
#     id          bigint auto_increment comment 'id' primary key,
#     userId      bigint                             not null comment '用户id',
#     voucherUrl  varchar(256)                       not null comment '凭证下载链接',
#     downloadStatus     tinyint                           not null comment '凭证下载状态（0-未下载，1-已下载）',
#     downloadTime datetime                         null comment '下载时间',
#     createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
#     updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
#     isDelete   tinyint  default 0                 not null comment '是否删除'
# )
#     comment '用户凭证下载表';

-- 查询用户近一周内每天注册信息
SELECT DATE(createTime) AS create_date, COUNT(*) AS user_count
FROM user
WHERE createTime >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(createTime)
ORDER BY create_date;

-- 查询用户近一周内每天接口调用次数
SELECT DATE(createTime) AS create_date, COUNT(*) AS invoke_count, AVG(requestDuration) as duration
FROM interface_log
WHERE createTime >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(createTime)
ORDER BY create_date;