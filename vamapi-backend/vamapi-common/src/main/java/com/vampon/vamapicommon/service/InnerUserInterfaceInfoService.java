package com.vampon.vamapicommon.service;

import com.vampon.vamapicommon.model.entity.UserInterfaceInfo;

/**
* @author Fang Hao
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-03-04 16:16:13
*/
public interface InnerUserInterfaceInfoService{
    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 获取接口剩余调用次数
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
