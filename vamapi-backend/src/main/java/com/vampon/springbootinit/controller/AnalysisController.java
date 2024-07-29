package com.vampon.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vampon.springbootinit.annotation.AuthCheck;
import com.vampon.springbootinit.common.BaseResponse;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.common.ResultUtils;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.mapper.UserInterfaceInfoMapper;
import com.vampon.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.vampon.springbootinit.model.entity.Chart;
import com.vampon.springbootinit.model.entity.UserInterfaceInfoLog;
import com.vampon.springbootinit.model.vo.BiResponse;
import com.vampon.springbootinit.model.vo.InterfaceInfoVO;
import com.vampon.springbootinit.service.ChartService;
import com.vampon.springbootinit.service.InterfaceInfoService;
import com.vampon.springbootinit.service.InterfaceLogService;
import com.vampon.vamapicommon.model.entity.InterfaceInfo;
import com.vampon.vamapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分析控制器
 *
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private InterfaceLogService interfaceLogService;

    @Resource
    private ChartService chartService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() { //tips:再好好看看
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(5);
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVOList);
    }

    @GetMapping("/interface/statistics_info")
    public BaseResponse<UserInterfaceInfoLog> getStatisticsInfo() {

        UserInterfaceInfoLog userInterfaceInfoLog = new UserInterfaceInfoLog();
        // 获取接口调用次数
        // 获取最近1000个接口调用记录平均时间
        Integer cost = interfaceLogService.getInterfaceInfoAverageCost();
        userInterfaceInfoLog.setInterfaceInfoAverageRequestDuration(cost);
        return ResultUtils.success(userInterfaceInfoLog);
    }

    @GetMapping("/get/statistics")
    public BaseResponse<List<BiResponse>> getDataStatistics(HttpServletRequest request) throws InterruptedException {
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
        BiResponse userBiResponse = chartService.genChartByAiAsync(userChartByAiRequest, userData, request);
        Thread.sleep(5000);
        BiResponse interfaceBiResponse = chartService.genChartByAiAsync(interfaceChartByAiRequest, interfaceData, request);
        // 因为是异步调用，此时返回的是null，需要等待异步调用完成
        return ResultUtils.success(Arrays.asList(userBiResponse, interfaceBiResponse));
    }

    @GetMapping("/get/latest_chart")
    public BaseResponse<List<BiResponse>> getLatestChart(HttpServletRequest request) {
        List<Chart> charts = chartService.getLatestChartInfo();
        List<BiResponse> biResponses = new ArrayList<>();
        for (Chart chart : charts) {
            BiResponse biResponse = new BiResponse();
            biResponse.setGenChart(chart.getGenChart());
            biResponse.setGenResult(chart.getGenResult());
            biResponse.setChartId(chart.getId());
            biResponse.setName(chart.getName());
            biResponses.add(biResponse);
        }
        return ResultUtils.success(biResponses);
    }


}
