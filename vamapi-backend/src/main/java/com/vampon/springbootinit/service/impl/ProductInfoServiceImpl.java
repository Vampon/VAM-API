package com.vampon.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.mapper.ProductInfoMapper;
import com.vampon.springbootinit.service.ProductInfoService;
import com.vampon.vamapicommon.model.entity.ProductInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: QiMu
 * @Date: 2023/08/25 02:56:44
 * @Version: 1.0
 * @Description: 产品信息服务impl
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo>
        implements ProductInfoService {
    @Override
    public void validProductInfo(ProductInfo productInfo, boolean add) {
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = productInfo.getName();
        String description = productInfo.getDescription();
        Integer total = productInfo.getTotal();
        Date expirationTime = productInfo.getExpirationTime();
        String productType = productInfo.getProductType();
        Integer addPoints = productInfo.getAddPoints();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (addPoints < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "增加积分不能为负数");
        }
        if (total < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "售卖金额不能为负数");
        }
    }
}




