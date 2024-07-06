package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.vamapicommon.model.entity.ProductOrder;
import com.vampon.vamapicommon.model.entity.User;
import com.vampon.vamapicommon.model.vo.ProductOrderVo;


import javax.servlet.http.HttpServletRequest;

/**
 * 接口订单服务
 */
public interface ProductOrderService extends IService<ProductOrder> {

    /**
     * 保存产品订单
     */
    ProductOrderVo saveProductOrder(Long productId, User loginUser);

    /**
     * 更新产品订单
     */
    boolean updateProductOrder(ProductOrder productOrder);

    /**
     * 获取产品订单
     * 获取订单
     */
    ProductOrderVo getProductOrder(Long productId, User loginUser, String payType);


    /**
     * 按订单号更新订单状态
     */
    boolean updateOrderStatusByOrderNo(String outTradeNo, String orderStatus);

    /**
     * 按订单号关闭订单
     */
    void closedOrderByOrderNo(String outTradeNo) throws Exception;


    /**
     * 通过out trade no获得产品订单
     * 获取产品订单状态
     */
    ProductOrder getProductOrderByOutTradeNo(String outTradeNo);

    /**
     * 处理超时订单
     * 检查订单状态(微信查单接口)
     */
    void processingTimedOutOrders(ProductOrder productOrder);


    /**
     * 付款通知
     * 处理付款通知
     */
    String doPaymentNotify(String notifyData, HttpServletRequest request);
}
