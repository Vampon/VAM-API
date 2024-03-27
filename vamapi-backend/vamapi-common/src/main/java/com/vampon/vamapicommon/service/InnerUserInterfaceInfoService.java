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
}
