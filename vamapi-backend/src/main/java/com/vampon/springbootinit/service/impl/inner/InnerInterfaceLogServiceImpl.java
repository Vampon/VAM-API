package com.vampon.springbootinit.service.impl.inner;

import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.service.InterfaceLogService;
import com.vampon.vamapicommon.model.entity.InterfaceLog;
import com.vampon.vamapicommon.service.InnerInterfaceLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * @author vampon
 */
@DubboService
@Slf4j
public class InnerInterfaceLogServiceImpl implements InnerInterfaceLogService {

    @Autowired
    InterfaceLogService interfaceLogService;

    /*
        todo: 这里本质上只统计了调用正确的接口日志信息，而且数据不全面
     */
    @Override
    public boolean save(Long interfaceInfoId, Long userId, Long startTime, String ipAddress, Long requestContentLength) {
        System.out.println(userId);
        InterfaceLog interfaceLog = new InterfaceLog();
        interfaceLog.setInterfaceId(interfaceInfoId);
        interfaceLog.setRequestContentLength(requestContentLength);
        interfaceLog.setUserId(userId);
        interfaceLog.setClientIp(ipAddress);
        Long endTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        Long responseTime = null;
        if (startTime != null && endTime != null) {
            //计算响应时间
            responseTime = endTime - startTime;
            log.info("用户id：{}，接口id：{}，请求开始时间：{}，请求结束时间：{}，请求耗时：{}ms", userId, interfaceInfoId, startTime, endTime, responseTime);
        }
        interfaceLog.setRequestDuration(responseTime);
        // 校验
        interfaceLogService.validInterfaceLog(interfaceLog, true);
        log.info("存储接口调用日志：",interfaceLog);
        return interfaceLogService.save(interfaceLog);
    }
}
