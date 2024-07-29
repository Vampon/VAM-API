package com.vampon.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.mapper.UserInterfaceInfoMapper;
import com.vampon.springbootinit.service.InterfaceInfoService;
import com.vampon.springbootinit.service.UserService;
import com.vampon.vamapicommon.model.entity.InterfaceInfo;
import com.vampon.vamapicommon.model.entity.UserInterfaceInfo;
import com.vampon.springbootinit.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Fang Hao
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-03-04 16:16:13
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，参数不能为空
        if (add) {
            if(userInterfaceInfo.getInterfaceInfoId()<=0 || userInterfaceInfo.getUserId()<=0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR , "接口或用户不存在");
            }

        }
        if (userInterfaceInfo.getLeftNum()<0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "剩余次数不能小于0");
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if(interfaceInfoId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.gt("leftNum", 0);
        // 外部使用了分布式锁，解决并发安全问题
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        boolean update = this.update(updateWrapper);
        return update;
    }

    @Override
    public int getUserInterfaceInfoLeftNum(long interfaceInfoId, long userId) {
        if(interfaceInfoId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo query = this.getOne(queryWrapper);
        if(query == null){
            return 0;
        }
        return 1;
    }

    @Override
    public boolean invokeProcess(long interfaceInfoId, long userId) {
        if(interfaceInfoId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo:下面的这些逻辑是不是应该需要事务，不然万一哪一步出错了，更新的消息都需要回滚
        boolean invoked = invokeCount(interfaceInfoId, userId);
        if(!invoked) {
            return false;
        }
        // 查询接口信息中的扣减积分数
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceInfoId);
        int reduceScore = interfaceInfo.getReduceScore();
        // 接口表对应id的接口调用次数更新
        boolean added = interfaceInfoService.addInterfaceInvokeCount(interfaceInfoId);
        if(!added){
            return false;
        }
        // 扣除用户积分余额
        boolean reduced = userService.reduceWalletBalance(userId, reduceScore);
        if(!reduced){
            return false;
        }
        return true;

    }
}




