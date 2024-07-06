package com.vampon.vamapicommon.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 *
 **/
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户accessKey
     */
    private String accessKey;

    /**
     * 用户绑定邮箱
     */
    private String userEmail;

    /**
     * 钱包余额（分）
     */
    private Integer balance;

    /**
     * 邀请码
     */
    private String invitationCode;

    private static final long serialVersionUID = 1L;
}