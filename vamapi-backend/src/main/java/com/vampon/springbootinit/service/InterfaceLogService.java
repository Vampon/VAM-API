package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.vamapicommon.model.entity.InterfaceLog;

/**
* @author Fang Hao
* @description 针对表【interface_log】的数据库操作Service
* @createDate 2024-07-06 21:12:03
*/
public interface InterfaceLogService extends IService<InterfaceLog> {

    /**
     * 非空判断
     * @param interfaceLog
     * @param add
     */
    void validInterfaceLog(InterfaceLog interfaceLog, boolean add);

    /**
     * 获取接口调用平均时间（最近一千条）
     * @return
     */
    Integer getInterfaceInfoAverageCost();

}
