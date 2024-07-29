package com.vampon.springbootinit.job.cycle;

import com.vampon.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.vampon.springbootinit.model.vo.BiResponse;
import com.vampon.springbootinit.service.ChartService;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

// 定时任务取消任务即可开启
//@Component
public class BiJob {

    @Resource
    private ChartService chartService;

    /**
     * 每天凌晨12点执行一次BI数据分析
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void getDataStatistics() throws InterruptedException {
        String userData = chartService.userDataTransform();
        String interfaceData = chartService.interfaceInfoDataTransform();
        GenChartByAiRequest userChartByAiRequest = new GenChartByAiRequest();
        userChartByAiRequest.setName("用户数据统计");
        userChartByAiRequest.setGoal("展示用户数据统计");
        userChartByAiRequest.setChartType("折线图");

        GenChartByAiRequest interfaceChartByAiRequest = new GenChartByAiRequest();
        interfaceChartByAiRequest.setName("接口调用数据统计");
        interfaceChartByAiRequest.setGoal("展示接口调用数据统计");
        interfaceChartByAiRequest.setChartType("折线图");
        // 异步调用
        BiResponse userBiResponse = chartService.genChartByAiAsync(userChartByAiRequest, userData, null);
        Thread.sleep(5000);
        BiResponse interfaceBiResponse = chartService.genChartByAiAsync(interfaceChartByAiRequest, interfaceData, null);
    }
}
