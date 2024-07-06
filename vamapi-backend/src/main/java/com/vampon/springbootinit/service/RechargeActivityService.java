package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.vamapicommon.model.entity.RechargeActivity;

import java.util.List;

/**
 * 充值活动服务
 */
public interface RechargeActivityService extends IService<RechargeActivity> {
    /**
     * 按订单号获取充值活动
     *
     * @param orderNo 订单号
     * @return
     */
    List<RechargeActivity> getRechargeActivityByOrderNo(String orderNo);
}
