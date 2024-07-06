package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.vamapicommon.model.entity.PaymentInfo;
import com.vampon.vamapicommon.model.vo.PaymentInfoVo;
/**
 * 支付信息服务
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    /**
     * 创建付款信息
     */
    boolean createPaymentInfo(PaymentInfoVo paymentInfoVo);
}
