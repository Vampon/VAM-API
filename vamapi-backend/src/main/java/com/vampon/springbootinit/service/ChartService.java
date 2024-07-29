package com.vampon.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vampon.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.vampon.springbootinit.model.entity.Chart;
import com.vampon.springbootinit.model.entity.UserStatistics;
import com.vampon.springbootinit.model.vo.BiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Fang Hao
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-07-14 10:52:24
*/
public interface ChartService extends IService<Chart> {

    String userDataTransform();

    String interfaceInfoDataTransform();

    List<Chart> getLatestChartInfo();

    BiResponse genChartByAi(GenChartByAiRequest genChartByAiRequest, String csvData, HttpServletRequest request);

    BiResponse genChartByAiAsync(GenChartByAiRequest genChartByAiRequest, String csvData, HttpServletRequest request);


}
