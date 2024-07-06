# 数据库初始化

-- 创建库
create database if not exists vamapi;

-- 切换库
use vamapi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    accessKey    varchar(512)                           not null comment 'accessKey',
    secretKey    varchar(512)                           not null comment 'secretKey',
    balance      int                                    not null comment '钱包余额（分）',
    invitationCode   varchar(256)                           null comment '邀请码',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    userEmail    varchar(128)                           null comment '用户绑定邮箱',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';


-- 产品信息表（积分）
create table if not exists product_info
(
    id           bigint auto_increment comment 'id' primary key,
    name         varchar(256)                           not null comment '产品名称',
    status       int                                    not null comment '状态（0- 默认下线 1- 上线）',
    addPoints    int                                    not null comment '增加积分个数',
    description  varchar(1024)                          null comment '产品描述',
    userId       bigint                                 not null comment '创建用户 id',
    total        int                                    not null comment '金额',
    productType      varchar(256)                       null comment '产品类型（VIP-会员 RECHARGE-充值）',
    expirationTime   datetime                           null comment '过期时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '产品信息表（积分）';

-- 付款信息表
create table if not exists payment_info
(
    id            bigint auto_increment comment 'id' primary key,
    orderNo       varchar(256)                           not null comment '商户订单号',
    transactionId varchar(256)                           not null comment '微信支付订单号',
    tradeType     varchar(256)                           not null comment '交易类型',
    tradeState    varchar(256)                           not null comment '交易状态(SUCCESS：支付成功,REFUND：转入退款,NOTPAY：未支付,CLOSED：已关闭,REVOKED：已撤销（仅付款码支付会返回）,USERPAYING：用户支付中（仅付款码支付会返回）,PAYERROR：支付失败（仅付款码支付会返回）)',
    tradeStateDesc varchar(1024)                         not null comment '交易状态描述',
    openid        varchar(256)                           not null comment '用户标识',
    payerTotal    int                                    not null comment '用户支付金额',
    currency      varchar(256)                           not null comment '货币类型',
    payerCurrency varchar(256)                           not null comment '用户支付币种',
    content       varchar(1024)                          not null comment '接口返回内容',
    total         int                                    not null comment '总金额',
    createTime   datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    successTime  varchar(256)                            null comment '支付完成时间'
) comment '付款信息表';

-- 商品订单表
create table if not exists product_order
(
    id            bigint auto_increment comment 'id' primary key,
    orderNo       varchar(256)                           not null comment '微信订单号/支付宝订单id',
    payType       varchar(256)                           not null comment '支付方式（默认 WX- 微信 ZFB- 支付宝）',
    expirationTime   datetime                            not null comment '过期时间',
    orderName     varchar(256)                           not null comment '商品名称',
    codeUrl       varchar(1024)                          not null comment '二维码地址',
    productInfo   varchar(512)                           not null comment '商品信息',
    total         int                                    not null comment '金额',
    productId     bigint                                 not null comment '商品id',
    userId        bigint                                 not null comment '创建人',
    status        varchar(256)                           not null comment '接口订单状态(SUCCESS：支付成功,REFUND：转入退款,NOTPAY：未支付,CLOSED：已关闭,REVOKED：已撤销（仅付款码支付会返回）,USERPAYING：用户支付中（仅付款码支付会返回）,PAYERROR：支付失败（仅付款码支付会返回）)',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    addPoints     int                                    not null comment '增加积分个数',
    formData      varchar(1024)                          not null comment '支付宝订单体'
) comment '商品订单表';