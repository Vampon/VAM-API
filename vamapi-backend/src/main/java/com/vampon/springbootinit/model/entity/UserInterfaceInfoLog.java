package com.vampon.springbootinit.model.entity;

import lombok.Data;

/**
 * * 用户接口信息统计日志
 */
@Data
public class UserInterfaceInfoLog {

    /**
     * 最近1000个接口请求日志的请求时延
     */
    private Integer InterfaceInfoAverageRequestDuration;
    /**
     * 接口调用总次数
     */
    private Long InterfaceTotalInvokeCount;
    /**
     * 用户数量
     */
    private Long UserTotalNum;
    /**
     * 接口接入数量
     */
    private Long InterfaceTotalNum;
}
