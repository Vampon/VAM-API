package com.vampon.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vampon.springbootinit.model.entity.UserStatistics;
import com.vampon.vamapicommon.model.entity.User;

import java.util.List;

/**
 * 用户数据库操作
 *
 * @author vampon
 * 
 */
public interface UserMapper extends BaseMapper<User> {
    List<UserStatistics> userCountByDate();
}




