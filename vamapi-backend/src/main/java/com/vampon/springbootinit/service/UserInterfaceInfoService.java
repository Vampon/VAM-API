package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.vamapicommon.model.entity.UserInterfaceInfo;

public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计(用户接口表)
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

    /**
     * 调用接口相关信息处理（接口表调用次数+1，用户接口表次数更新，用户积分扣减）
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeProcess(long interfaceInfoId, long userId);

}
