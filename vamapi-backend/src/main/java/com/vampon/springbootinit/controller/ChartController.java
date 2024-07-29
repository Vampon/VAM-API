package com.vampon.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.vampon.springbootinit.annotation.AuthCheck;
import com.vampon.springbootinit.common.BaseResponse;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.common.ResultUtils;
import com.vampon.springbootinit.constant.CommonConstant;
import com.vampon.springbootinit.constant.UserConstant;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.exception.ThrowUtils;
import com.vampon.springbootinit.manager.AiManager;
import com.vampon.springbootinit.model.dto.chart.ChartQueryRequest;
import com.vampon.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.vampon.springbootinit.model.entity.Chart;
import com.vampon.springbootinit.model.vo.BiResponse;
import com.vampon.springbootinit.service.ChartService;
import com.vampon.springbootinit.service.UserService;
import com.vampon.springbootinit.utils.SqlUtils;
import com.vampon.vamapicommon.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
/*
import java.util.concurrent.ThreadPoolExecutor;
*/

/**
 * 帖子接口
 *
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

/*    @Resource
    private ThreadPoolExecutor threadPoolExecutor;*/


    // region 增删改查



    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }




//    /**
//     * 智能分析（异步）
//     *
//     * @param multipartFile
//     * @param genChartByAiRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/gen/async")
//    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
//                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
//        String name = genChartByAiRequest.getName();
//        String goal = genChartByAiRequest.getGoal();
//        String chartType = genChartByAiRequest.getChartType();
//        // 校验
//        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
//        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
//        // 校验文件
//        long size = multipartFile.getSize();
//        String originalFilename = multipartFile.getOriginalFilename();
//        // 校验文件大小
//        final long ONE_MB = 1024 * 1024L;
//        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
//        // 校验文件后缀 aaa.png
//        String suffix = FileUtil.getSuffix(originalFilename);
//        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
//        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
//
//        User loginUser = userService.getLoginUser(request);
//        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
////        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
////                "分析需求：\n" +
////                "{数据分析的需求或者目标}\n" +
////                "原始数据：\n" +
////                "{csv格式的原始数据，用,作为分隔符}\n" +
////                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
////                "【【【【【\n" +
////                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
////                "【【【【【\n" +
////                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
//        long biModelId = 1659171950288818178L;
//        // 分析需求：
//        // 分析网站用户的增长情况
//        // 原始数据：
//        // 日期,用户数
//        // 1号,10
//        // 2号,20
//        // 3号,30
//
//        // 构造用户输入
//        StringBuilder userInput = new StringBuilder();
//        userInput.append("分析需求：").append("\n");
//
//        // 拼接分析目标
//        String userGoal = goal;
//        if (StringUtils.isNotBlank(chartType)) {
//            userGoal += "，请使用" + chartType;
//        }
//        userInput.append(userGoal).append("\n");
//        userInput.append("原始数据：").append("\n");
//        // 压缩后的数据
//        String csvData = "";
//        userInput.append(csvData).append("\n");
//
//        // 插入到数据库
//        Chart chart = new Chart();
//        chart.setName(name);
//        chart.setGoal(goal);
//        chart.setChartData(csvData);
//        chart.setChartType(chartType);
//        chart.setStatus("wait");
//        chart.setUserId(loginUser.getId());
//        boolean saveResult = chartService.save(chart);
//        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
//
//        // todo 建议处理任务队列满了后，抛异常的情况
//        CompletableFuture.runAsync(() -> {
//            // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
//            Chart updateChart = new Chart();
//            updateChart.setId(chart.getId());
//            updateChart.setStatus("running");
//            boolean b = chartService.updateById(updateChart);
//            if (!b) {
//                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
//                return;
//            }
//            // 调用 AI
//            String result = aiManager.doChat(biModelId, userInput.toString());
//            String[] splits = result.split("【【【【【");
//            if (splits.length < 3) {
//                handleChartUpdateError(chart.getId(), "AI 生成错误");
//                return;
//            }
//            String genChart = splits[1].trim();
//            String genResult = splits[2].trim();
//            Chart updateChartResult = new Chart();
//            updateChartResult.setId(chart.getId());
//            updateChartResult.setGenChart(genChart);
//            updateChartResult.setGenResult(genResult);
//            // todo 建议定义状态为枚举值
//            updateChartResult.setStatus("succeed");
//            boolean updateResult = chartService.updateById(updateChartResult);
//            if (!updateResult) {
//                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
//            }
//        }, threadPoolExecutor);
//
//        BiResponse biResponse = new BiResponse();
//        biResponse.setChartId(chart.getId());
//        return ResultUtils.success(biResponse);
//    }

//    /**
//     * 智能分析（异步消息队列）
//     *
//     * @param multipartFile
//     * @param genChartByAiRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/gen/async/mq")
//    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
//                                                      GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
//        String name = genChartByAiRequest.getName();
//        String goal = genChartByAiRequest.getGoal();
//        String chartType = genChartByAiRequest.getChartType();
//        // 校验
//        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
//        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
//        // 校验文件
//        long size = multipartFile.getSize();
//        String originalFilename = multipartFile.getOriginalFilename();
//        // 校验文件大小
//        final long ONE_MB = 1024 * 1024L;
//        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
//        // 校验文件后缀 aaa.png
//        String suffix = FileUtil.getSuffix(originalFilename);
//        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
//        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
//
//        User loginUser = userService.getLoginUser(request);
//        // 限流判断，每个用户一个限流器
//        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
//        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
////        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
////                "分析需求：\n" +
////                "{数据分析的需求或者目标}\n" +
////                "原始数据：\n" +
////                "{csv格式的原始数据，用,作为分隔符}\n" +
////                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
////                "【【【【【\n" +
////                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
////                "【【【【【\n" +
////                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
//        long biModelId = 1659171950288818178L;
//        // 分析需求：
//        // 分析网站用户的增长情况
//        // 原始数据：
//        // 日期,用户数
//        // 1号,10
//        // 2号,20
//        // 3号,30
//
//        // 构造用户输入
//        StringBuilder userInput = new StringBuilder();
//        userInput.append("分析需求：").append("\n");
//
//        // 拼接分析目标
//        String userGoal = goal;
//        if (StringUtils.isNotBlank(chartType)) {
//            userGoal += "，请使用" + chartType;
//        }
//        userInput.append(userGoal).append("\n");
//        userInput.append("原始数据：").append("\n");
//        // 压缩后的数据
//        String csvData = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append(csvData).append("\n");
//
//        // 插入到数据库
//        Chart chart = new Chart();
//        chart.setName(name);
//        chart.setGoal(goal);
//        chart.setChartData(csvData);
//        chart.setChartType(chartType);
//        chart.setStatus("wait");
//        chart.setUserId(loginUser.getId());
//        boolean saveResult = chartService.save(chart);
//        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
//        long newChartId = chart.getId();
//        biMessageProducer.sendMessage(String.valueOf(newChartId));
//        BiResponse biResponse = new BiResponse();
//        biResponse.setChartId(newChartId);
//        return ResultUtils.success(biResponse);
//    }
//
//
    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }


    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }



}
