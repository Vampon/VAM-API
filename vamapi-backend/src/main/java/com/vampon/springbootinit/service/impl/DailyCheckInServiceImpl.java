package com.vampon.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vampon.springbootinit.mapper.DailyCheckInMapper;

import com.vampon.springbootinit.model.entity.DailyCheckIn;
import com.vampon.springbootinit.service.DailyCheckInService;
import org.springframework.stereotype.Service;

/**
* @author Fang Hao
* @description 针对表【daily_check_in(每日签到表)】的数据库操作Service实现
* @createDate 2024-07-20 19:58:25
*/
@Service
public class DailyCheckInServiceImpl extends ServiceImpl<DailyCheckInMapper, DailyCheckIn>
    implements DailyCheckInService {

}




