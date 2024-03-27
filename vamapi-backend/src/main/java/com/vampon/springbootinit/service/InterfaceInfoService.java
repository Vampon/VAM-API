package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.springbootinit.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.vampon.vamapicommon.model.entity.InterfaceInfo;

import javax.servlet.http.HttpServletRequest;

/**
* @author Fang Hao
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-02-28 23:31:22
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);


    String getInvokeResult(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request, InterfaceInfo oldInterfaceInfo);
}
