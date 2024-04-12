package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.vamapicommon.model.entity.UserInterfaceInfo;

public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 查询用户接口调用剩余次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    int getUserInterfaceInfoLeftNum(long interfaceInfoId, long userId);

}
