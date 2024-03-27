package com.vampon.vamapicommon.service;

import com.vampon.vamapicommon.model.entity.User;

/**
 * 用户服务
 *
 */
public interface InnerUserService{

    /**
     * 从数据库中查询是否已经分配给用户秘钥
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
