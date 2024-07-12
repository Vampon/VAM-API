package com.vampon.springbootinit.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.exception.ThrowUtils;
import com.vampon.springbootinit.mapper.InterfaceLogMapper;
import com.vampon.springbootinit.service.InterfaceLogService;
import com.vampon.vamapicommon.model.entity.InterfaceLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Fang Hao
* @description 针对表【interface_log】的数据库操作Service实现
* @createDate 2024-07-06 21:12:03
*/
@Service
public class InterfaceLogServiceImpl extends ServiceImpl<InterfaceLogMapper, InterfaceLog>
    implements InterfaceLogService {

    @Autowired
    private InterfaceLogMapper interfaceLogMapper;

    /**
     * 非空判断
     *
     * @param InterfaceLog
     * @param add
     */
    @Override
    public void validInterfaceLog(InterfaceLog InterfaceLog, boolean add) {
        if (InterfaceLog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long interfaceId = InterfaceLog.getInterfaceId();
        Long userId = InterfaceLog.getUserId();
        String requestUrl = InterfaceLog.getRequestUrl();
        // 创建时，参数不能为空
        if (add) {
//            ThrowUtils.throwIf(ObjUtil.isEmpty(
//                            userId),
//                    ErrorCode.PARAMS_ERROR,"用户id必填！");
            ThrowUtils.throwIf(ObjUtil.isEmpty(
                            interfaceId),
                    ErrorCode.PARAMS_ERROR, "接口信息id必填！");
        }
    }

    @Override
    public Integer getInterfaceInfoAverageCost() {
        return interfaceLogMapper.getInterfaceInfoAverageCost();
    }

}




