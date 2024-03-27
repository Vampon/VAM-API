package com.vampon.vamapicommon.service;

import com.vampon.vamapicommon.model.entity.InterfaceInfo;

/**
* @author Fang Hao
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-02-28 23:31:22
*/
public interface InnerInterfaceInfoService{


    /**
     * 从数据库中查询模拟接口是否存在
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
