use vamapi;

-- 充值活动表
create table if not exists recharge_activity
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                             not null comment '用户id',
    productId  bigint                             not null comment '商品id',
    orderNo        varchar(256)                           null comment '商户订单号',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '充值活动表';

-- 产品信息
create table if not exists product_info
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                           not null comment '产品名称',
    description    varchar(256)                           null comment '产品描述',
    userId         bigint                                 null comment '创建人',
    total          bigint                                 null comment '金额(分)',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    productType    varchar(256) default 'RECHARGE'        not null comment '产品类型（VIP-会员 RECHARGE-充值,RECHARGEACTIVITY-充值活动）',
    status         tinyint      default 0                 not null comment '商品状态（0- 默认下线 1- 上线）',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除'
)
    comment '产品信息';

# 产品订单
create table if not exists product_order
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           not null comment '订单号',
    codeUrl        varchar(256)                           null comment '二维码地址',
    userId         bigint                                 not null comment '创建人',
    productId      bigint                                 not null comment '商品id',
    orderName      varchar(256)                           not null comment '商品名称',
    total          bigint                                 not null comment '金额(分)',
    status         varchar(256) default 'NOTPAY'          not null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    payType        varchar(256) default 'WX'              not null comment '支付方式（默认 WX- 微信 ZFB- 支付宝）',
    productInfo    text                                   null comment '商品信息',
    formData       text                                   null comment '支付宝formData',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '商品订单';

#付款信息
create table if not exists payment_info
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           null comment '商户订单号',
    transactionId  varchar(256)                           null comment '微信支付订单号',
    tradeType      varchar(256)                           null comment '交易类型',
    tradeState     varchar(256)                           null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    tradeStateDesc varchar(256)                           null comment '交易状态描述',
    successTime    varchar(256)                           null comment '支付完成时间',
    openid         varchar(256)                           null comment '用户标识',
    payerTotal     bigint                                 null comment '用户支付金额',
    currency       varchar(256) default 'CNY'             null comment '货币类型',
    payerCurrency  varchar(256) default 'CNY'             null comment '用户支付币种',
    content        text                                   null comment '接口返回内容',
    total          bigint                                 null comment '总金额(分)',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '付款信息';

INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695338876708544514, '100坤币', '增加100坤币到钱包', 1691069533871013889, 1, 100, 'RECHARGEACTIVITY', 1, null, '2023-08-26 15:34:20', '2023-08-28 12:58:30', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695773972037839073, '9999坤币', '增加9999坤币到钱包', 1691069533871013889, 699, 9999, 'RECHARGE', 1, '2023-08-28 13:01:34', '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695776766919888897, '1000坤币', '增加1000坤币到钱包', 1691069533871013889, 99, 1000, 'RECHARGE', 1, null, '2023-08-27 20:34:21', '2023-08-27 20:34:21', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695777072030339073, '3000坤币', '增加3000坤币到钱包', 1691069533871013889, 199, 3000, 'RECHARGE', 1, null, '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695777203236556802, '15999坤币', '增加15999坤币到钱包', 1691069533871013889, 888, 15999, 'RECHARGE', 1, null, '2023-08-27 20:36:05', '2023-08-28 13:02:25', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695778320091631617, '18999坤币', '增加18999坤币到钱包', 1691069533871013889, 999, 18999, 'RECHARGE', 1, null, '2023-08-27 20:40:32', '2023-08-28 13:02:42', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1697087470134259713, '10坤币', '签到获取', 1692848556158709762, 0, 10, 'RECHARGE', 0, null, '2023-08-31 11:22:37', '2023-08-31 11:22:37', 1);