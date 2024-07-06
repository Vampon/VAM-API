package com.vampon.springbootinit.model.dto.pay;

import lombok.Data;

import java.io.Serializable;

/**
 * 付款创建请求
 */
@Data
public class PayCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口id
     */
    private String productId;

    /**
     * 支付类型
     */
    private String payType;

}