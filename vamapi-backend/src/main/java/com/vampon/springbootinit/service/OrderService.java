package com.vampon.springbootinit.service;


import com.vampon.vamapicommon.model.entity.ProductOrder;
import com.vampon.vamapicommon.model.entity.User;
import com.vampon.vamapicommon.model.vo.ProductOrderVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单服务
 */
public interface OrderService {
    /**
     * 处理订单通知
     */
    String doOrderNotify(String notifyData, HttpServletRequest request);

    /**
     * 按付费类型获取产品订单服务
     */
    ProductOrderService getProductOrderServiceByPayType(String payType);

    /**
     * 按付款类型创建订单
     */
    ProductOrderVo createOrderByPayType(Long productId, String payType, User loginUser);

    /**
     * 按时间获得未支付订单
     */
    List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove, String payType);
}
