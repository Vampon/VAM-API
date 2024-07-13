package com.vampon.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vampon.springbootinit.constant.CommonConstant;
import com.vampon.springbootinit.model.dto.post.PostQueryRequest;
import com.vampon.springbootinit.model.entity.Post;
import com.vampon.springbootinit.model.entity.UserVoucherurlInfo;
import com.vampon.springbootinit.service.UserVoucherurlInfoService;
import com.vampon.springbootinit.mapper.UserVoucherurlInfoMapper;
import com.vampon.springbootinit.utils.SqlUtils;
import com.vampon.vamapicommon.model.entity.User;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Fang Hao
* @description 针对表【user_voucherurl_info(用户凭证下载表)】的数据库操作Service实现
* @createDate 2024-07-07 15:30:14
*/
@Service
public class UserVoucherurlInfoServiceImpl extends ServiceImpl<UserVoucherurlInfoMapper, UserVoucherurlInfo>
    implements UserVoucherurlInfoService{


}




