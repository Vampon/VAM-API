package com.vampon.springbootinit.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vampon.springbootinit.common.BaseResponse;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.common.ResultUtils;
import com.vampon.springbootinit.constant.CommonConstant;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.exception.ThrowUtils;
import com.vampon.springbootinit.manager.AiManager;
import com.vampon.springbootinit.mapper.InterfaceInfoMapper;
import com.vampon.springbootinit.mapper.InterfaceLogMapper;
import com.vampon.springbootinit.mapper.UserMapper;
import com.vampon.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.vampon.springbootinit.model.entity.Chart;
import com.vampon.springbootinit.model.entity.InterfaceInfoStatistics;
import com.vampon.springbootinit.model.entity.UserStatistics;
import com.vampon.springbootinit.model.vo.BiResponse;
import com.vampon.springbootinit.service.ChartService;
import com.vampon.springbootinit.mapper.ChartMapper;

import com.vampon.springbootinit.service.UserService;
import com.vampon.vamapicommon.model.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
* @author Fang Hao
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-07-14 10:52:24
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

    @Resource
    private UserMapper userMapper;

    @Resource ChartMapper chartMapper;

    @Resource
    private InterfaceLogMapper interfaceLogMapper;

    @Resource
    private AiManager aiManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * 用户数据获取与转换
     * @return
     */
    @Override
    public String userDataTransform() {
        // 数据库查询
        List<UserStatistics> userStatistics = userMapper.userCountByDate();
        // 获取填充后的结果
        List<UserStatistics> filledStatistics = fillMissingUserDates(userStatistics);
        // 转换为CSV格式
        return userDataConvertToCSV(filledStatistics);
    }

    /**
     * 接口信息数据获取与转换
     * @return
     */
    @Override
    public String interfaceInfoDataTransform() {
        // 数据库查询
        List<InterfaceInfoStatistics> interfaceInfoStatistics = interfaceLogMapper.interfaceInvokeCountByDate();
        // 获取填充后的结果
        List<InterfaceInfoStatistics> filledStatistics = fillMissingInterfaceDates(interfaceInfoStatistics);
        // 转换为CSV格式
        return interfaceDataConvertToCSV(filledStatistics);
    }

    /**
     * 从数据库中读取最新图表信息
     * @return
     */
    @Override
    public List<Chart> getLatestChartInfo(){
        List<Chart> latestChartInfo = chartMapper.getLatestChartInfo();
        return latestChartInfo;
    }

    /**
     * AI分析生成图表（同步获取）
     * @param genChartByAiRequest
     * @param csvData
     * @param request
     * @return
     */
    @Override
    public BiResponse genChartByAi(GenChartByAiRequest genChartByAiRequest, String csvData, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com
//        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//                "分析需求：\n" +
//                "{数据分析的需求或者目标}\n" +
//                "原始数据：\n" +
//                "{csv格式的原始数据，用,作为分隔符}\n" +
//                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//                "【【【【【\n" +
//                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
//                "【【【【【\n" +
//                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        long biModelId = CommonConstant.BI_MODEL_ID;
        // 分析需求：
        // 分析网站用户的增长情况
        // 原始数据：
        // 日期,用户数
        // 1号,10
        // 2号,20
        // 3号,30

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        userInput.append(csvData).append("\n");

        String result = aiManager.doChat(biModelId, userInput.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        boolean saveResult = this.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return biResponse;
    }

    /**
     * AI分析生成图表（异步获取）
     * @param genChartByAiRequest
     * @param csvData
     * @param request
     * @return
     */
    @Override
    public BiResponse genChartByAiAsync(GenChartByAiRequest genChartByAiRequest, String csvData, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
//        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//                "分析需求：\n" +
//                "{数据分析的需求或者目标}\n" +
//                "原始数据：\n" +
//                "{csv格式的原始数据，用,作为分隔符}\n" +
//                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//                "【【【【【\n" +
//                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
//                "【【【【【\n" +
//                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        long biModelId = CommonConstant.BI_MODEL_ID;
        // 分析需求：
        // 分析网站用户的增长情况
        // 原始数据：
        // 日期,用户数
        // 1号,10
        // 2号,20
        // 3号,30
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        userInput.append(csvData).append("\n");

        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        boolean saveResult = this.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // todo 建议处理任务队列满了后，抛异常的情况
        CompletableFuture.runAsync(() -> {
            // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus("running");
            boolean b = this.updateById(updateChart);
            if (!b) {
                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
                return;
            }
            // 调用 AI
            String result = aiManager.doChat(biModelId, userInput.toString());
            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
                handleChartUpdateError(chart.getId(), "AI 生成错误");
                return;
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            // todo 建议定义状态为枚举值
            updateChartResult.setStatus("succeed");
            boolean updateResult = this.updateById(updateChartResult);
            if (!updateResult) {
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }
        }, threadPoolExecutor);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return biResponse;
    }

    public static List<UserStatistics> fillMissingUserDates(List<UserStatistics> statistics) {
        // 计算前7天的所有日期
        List<Date> last7Days = getLast7Days();
        Map<String, UserStatistics> statisticsMap = new HashMap<>();

        // 使用SimpleDateFormat来格式化日期，以便比较
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 将查询结果放入Map中
        for (UserStatistics stat : statistics) {
            String formattedDate = sdf.format(stat.getCreateDate());
            statisticsMap.put(formattedDate, stat);
        }

        // 填充缺少的日期
        List<UserStatistics> filledStatistics = new ArrayList<>();
        for (Date date : last7Days) {
            String formattedDate = sdf.format(date);
            if (statisticsMap.containsKey(formattedDate)) {
                filledStatistics.add(statisticsMap.get(formattedDate));
            } else {
                filledStatistics.add(new UserStatistics(date, 0));
            }
        }
        return filledStatistics;
    }

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = this.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }


    public static List<InterfaceInfoStatistics> fillMissingInterfaceDates(List<InterfaceInfoStatistics> interfaceInfoStatistics) {
        // 计算前7天的所有日期
        List<Date> last7Days = getLast7Days();
        Map<String, InterfaceInfoStatistics> statisticsMap = new HashMap<>();

        // 使用SimpleDateFormat来格式化日期，以便比较
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 将查询结果放入Map中
        for (InterfaceInfoStatistics stat : interfaceInfoStatistics) {
            String formattedDate = sdf.format(stat.getCreateDate());
            statisticsMap.put(formattedDate, stat);
        }

        // 填充缺少的日期
        List<InterfaceInfoStatistics> filledStatistics = new ArrayList<>();
        for (Date date : last7Days) {
            String formattedDate = sdf.format(date);
            if (statisticsMap.containsKey(formattedDate)) {
                filledStatistics.add(statisticsMap.get(formattedDate));
            } else {
                filledStatistics.add(new InterfaceInfoStatistics(date, 0, 0));
            }
        }
        return filledStatistics;
    }


    public static List<Date> getLast7Days() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return dates;
    }

    public static String userDataConvertToCSV(List<UserStatistics> statistics) {
        StringBuilder csvBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 添加CSV头
        csvBuilder.append("时间,用户注册人数\n");

        // 添加CSV内容
        for (UserStatistics stat : statistics) {
            csvBuilder.append(sdf.format(stat.getCreateDate())).append(",");
            csvBuilder.append(stat.getUserCount()).append("\n");
        }
        return csvBuilder.toString();
    }

    public static String interfaceDataConvertToCSV(List<InterfaceInfoStatistics> statistics) {
        StringBuilder csvBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 添加CSV头
        csvBuilder.append("时间,接口调用次数,响应时延\n");

        // 添加CSV内容
        for (InterfaceInfoStatistics stat : statistics) {
            csvBuilder.append(sdf.format(stat.getCreateDate())).append(",");
            csvBuilder.append(stat.getInvokeCount()).append(",");
            csvBuilder.append(stat.getDuration()).append("\n");
        }
        return csvBuilder.toString();
    }
}




