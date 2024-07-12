package com.vampon.vamapicommon.service;


import com.vampon.vamapicommon.model.entity.InterfaceLog;

public interface InnerInterfaceLogService {

    /**
     * 存储日志
     */
    boolean save(Long interfaceInfoId, Long userId, Long startTime, String ipAddress, Long requestContentLength);

}
