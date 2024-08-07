package com.vampon.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vampon.springbootinit.model.entity.Chart;

import java.util.List;

/**
* @author Fang Hao
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2024-07-14 10:52:24
* @Entity generator.domain.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {

    List<Chart> getLatestChartInfo();

}




