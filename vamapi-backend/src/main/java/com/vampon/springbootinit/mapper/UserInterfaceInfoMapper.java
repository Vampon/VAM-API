package com.vampon.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vampon.vamapicommon.model.entity.UserInterfaceInfo;
import java.util.List;
/**
* @author Fang Hao
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2024-03-04 16:16:13
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




