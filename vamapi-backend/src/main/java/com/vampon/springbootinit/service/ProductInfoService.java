package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.vamapicommon.model.entity.ProductInfo;

/**
 * 产品信息服务
 */
public interface ProductInfoService extends IService<ProductInfo> {
    /**
     * 有效产品信息
     * 校验
     */
    void validProductInfo(ProductInfo productInfo, boolean add);
}
