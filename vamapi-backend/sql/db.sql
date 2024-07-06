use vamapi;

-- 接口信息
create table if not exists `interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '接口名称',
    `description` varchar(256) null comment '接口描述',
    `url` varchar(512) not null comment '接口地址',
    `requestParams` text null comment '请求参数',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '接口状态（0-关闭，1-开启）',
    `method` varchar(256) not null comment '请求类型',
    `userId` bigint not null comment '创建人',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDeleted` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)',
    `totalInvokes` int default 0 not null comment '总调用次数',
    `reduceScore` tinyint default 0 not null comment '接口花费积分',
    `avatarUrl` varchar(512) not null comment '接口头像地址'
) comment '接口信息';

insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('孔思远', '廖泽洋', 'www.wanita-schiller.com', '史鸿煊', '林聪健', 0, '苏子涵', 1897);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('苏烨伟', '薛致远', 'www.malika-doyle.com', '刘楷瑞', '曾立诚', 0, '曹修洁', 7671594792);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('林懿轩', '钱皓轩', 'www.norman-prosacco.net', '钱绍辉', '韦雨泽', 0, '侯智宸', 5);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('张风华', '雷伟宸', 'www.fernande-kovacek.name', '黎烨华', '梁浩', 0, '雷风华', 8811);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('严瑾瑜', '任黎昕', 'www.astrid-hermann.info', '覃绍辉', '罗鹤轩', 0, '田楷瑞', 70809661);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('陆鹭洋', '邹雨泽', 'www.sima-wisoky.name', '曹立果', '曹立诚', 0, '武健柏', 7);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('侯修杰', '陆峻熙', 'www.jonah-boyle.org', '任志强', '崔楷瑞', 0, '邱博超', 615792284);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('邱伟宸', '邵烨磊', 'www.wm-waelchi.name', '石子默', '何立诚', 0, '廖鑫鹏', 6);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('陈弘文', '彭子骞', 'www.dewitt-thiel.com', '沈天翊', '朱鹏', 0, '朱昊天', 7318730);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('彭文', '张耀杰', 'www.johnie-mccullough.info', '薛皓轩', '蒋子骞', 0, '何金鑫', 31328897);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('董瑞霖', '吴昊天', 'www.donald-fisher.io', '杨雪松', '丁展鹏', 0, '钟昊焱', 56);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('顾瑾瑜', '方志泽', 'www.kelvin-boyer.net', '夏烨伟', '邓正豪', 0, '顾鸿涛', 6946797372);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('姚哲瀚', '黎振家', 'www.aron-wyman.co', '廖煜城', '孙晓博', 0, '陈致远', 494578561);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('夏俊驰', '许彬', 'www.linnea-hane.name', '朱博文', '谢浩轩', 0, '张昊焱', 197637683);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('赖黎昕', '钟懿轩', 'www.benedict-huels.co', '杨耀杰', '卢思聪', 0, '汪修杰', 76);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('许聪健', '沈哲瀚', 'www.mariette-lang.net', '贾博超', '袁昊然', 0, '唐远航', 10);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('余靖琪', '郭鹏煊', 'www.raymon-hessel.biz', '程鸿涛', '邓立诚', 0, '侯伟诚', 8013783);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('孔昊强', '刘煜祺', 'www.connie-hane.info', '谭峻熙', '沈靖琪', 0, '傅炎彬', 11006);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('宋智宸', '赵志强', 'www.merilyn-kris.biz', '蔡鸿煊', '洪思聪', 0, '罗俊驰', 555);
insert into `interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('黄哲瀚', '赵志泽', 'www.basil-walter.info', '苏越泽', '董鹤轩', 0, '陶志泽', 375006);