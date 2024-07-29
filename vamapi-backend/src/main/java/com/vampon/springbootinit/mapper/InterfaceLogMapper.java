package com.vampon.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vampon.springbootinit.model.entity.InterfaceInfoStatistics;
import com.vampon.vamapicommon.model.entity.InterfaceLog;

import java.util.List;

/**
* @author Fang Hao
* @description 针对表【interface_log】的数据库操作Mapper
* @createDate 2024-07-06 21:12:03
* @Entity generator.domain.InterfaceLog
*/
public interface InterfaceLogMapper extends BaseMapper<InterfaceLog> {

    /**
     * @Description: 获取调用平均时长（最近1000条）
     * @param:
     * @return:
     * @auther: Vampon
     * @createDate: 2023-3-15
     */
    Integer getInterfaceInfoAverageCost();


    List<InterfaceInfoStatistics> interfaceInvokeCountByDate();

}




