package com.vampon.vamapiinterface.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.vampon.vamapicommon.common.ErrorCode;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapiinterface.model.Enum.CacheEnum;
import com.vampon.vamapiinterface.model.Events;
import com.vampon.vamapiinterface.model.TiktokHot;
import com.vampon.vamapiinterface.model.WeatherInfo;
import com.vampon.vamapiinterface.model.WeiboHot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cache.CacheManager;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 有意思的接口
 *
 */
@RestController
public class FunApiController {

    @Autowired
    private CacheManager cacheManager;

    private static BaseResponse<String> invokeOuterApi(String url, String body) {
        System.out.println((url + " " + body));
        try(HttpResponse httpResponse = HttpRequest.get(url + "?" + body)
                .execute()){
            return ResultUtils.success(httpResponse.body());
        }
    }

    /**
     * 每日星座运势
     */
    @PostMapping("/horoscope")
    public BaseResponse<String> randImages(HttpServletRequest request) {
        String url = "https://api.vvhan.com/api/horoscope";
        String body = "type=scorpio&time=today";
        return invokeOuterApi(url, body);
    }
    /**
     * 毒鸡汤
     */
    @GetMapping("/poison")
    public BaseResponse<String> poisonChicken(HttpServletRequest request) {
        String url = "http://api.btstu.cn/yan/api.php";
        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
        return invokeOuterApi(url, body);
    }

    /**
     * 天气信息
     * @return
     */
    @GetMapping("/weather")
    public BaseResponse<String> getWeather(String adcode,HttpServletRequest request) {
        // 访问高德查询天气接口
        String url = "https://restapi.amap.com/v3/weather/weatherInfo?key=5bec0cbe2b14c4957857c197d5e0bc00&extensions=base&city=%s";
        // todo: 校验adcode是否正确
        url = String.format(url, adcode);
        HttpResponse httpResponse = HttpRequest.get(url).execute();
        String responseJson = httpResponse.body();
        System.out.println(responseJson);
        // 解析 JSON
        JSONObject jsonObject = JSONUtil.parseObj(responseJson);
        String status = jsonObject.getStr("status");
        if(!"1".equals(status)){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"请求接口失败，请稍后再试");
        }
        JSONArray weatherArray = jsonObject.getJSONArray("lives");
        if (weatherArray.size() > 0) {
            JSONObject weatherObject = weatherArray.getJSONObject(0); // 获取数组中的第一个对象
            JSONObject filteredObject = new JSONObject();
            filteredObject.put("province", weatherObject.getStr("province"));
            filteredObject.put("city", weatherObject.getStr("city"));
            filteredObject.put("weather", weatherObject.getStr("weather"));
            filteredObject.put("temperature", weatherObject.getStr("temperature"));
            filteredObject.put("winddirection", weatherObject.getStr("winddirection"));
            filteredObject.put("windpower", weatherObject.getStr("windpower"));
            WeatherInfo weather = filteredObject.toBean(WeatherInfo.class);
            String weatherResult = JSONUtil.toJsonStr(weather); // 注意这里可能需要一些处理来避免直接转换为JSON字符串
            return ResultUtils.success(weatherResult);
        } else {
            // 没有天气数据，返回适当的错误或空响应
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "没有找到天气数据");
        }

    }

    /**
     * 获取微博热搜
     * @return 返回微博热搜
     */
    @Cacheable(value = "WEIBO", key = "#root.methodName")
    @GetMapping("/weiboHotSearch")
    public BaseResponse<String> getWeiboHotSearch(HttpServletRequest request){
        System.out.println("Executing getWeiboHotSearch method");
        // 1. 访问微博热搜接口
        String url = "https://weibo.com/ajax/side/hotSearch";
        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.get(url + "?" + body).execute();
        String responseJson = httpResponse.body();
        // 解析 JSON
        JSONObject jsonObject = JSONUtil.parseObj(responseJson);

        // 获取微博的realtime数组
        JSONArray realtimeArray = jsonObject.getJSONObject("data").getJSONArray("realtime");
        // 遍历realtime数组并只保留note、label_name和num字段

        List<WeiboHot> weiboHotList = new ArrayList<>();
        for (int i = 0; i < realtimeArray.size(); i++) {
            JSONObject realtimeObject = realtimeArray.getJSONObject(i);
            JSONObject filteredObject = new JSONObject();
            String note = realtimeObject.getStr("note");
            filteredObject.put("index", i+1);
            filteredObject.put("title", note);
            filteredObject.put("hotType", realtimeObject.getStr("label_name"));
            filteredObject.put("hotNum", realtimeObject.getStr("num"));
            // filteredObject.put("url", "https://s.weibo.com/weibo?q=%23"+ URLUtil.encode(note) +"%23");
            WeiboHot weiboHot = filteredObject.toBean(WeiboHot.class);
            weiboHotList.add(weiboHot);
        }
//        WeiboHotSearchResponse weiboHotSearchResponse = new WeiboHotSearchResponse();
//        weiboHotSearchResponse.setWeibohotSearch(weiboHotList);
        String weiboHotResult = JSONUtil.toJsonStr(weiboHotList);
        // 3.返回
        return ResultUtils.success(weiboHotResult);
    }

    @Cacheable(value = "EVENTONHISTORY", key = "#month + '_' + #day")
    @GetMapping("/eventsOnHistory")
    public BaseResponse<String> getEventsOnHistory(String month, String day, HttpServletRequest request){
        // 校验
        List<String> monthMap = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        List<String> dayMap = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31");
        if (!monthMap.contains(month) || !dayMap.contains(day)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"日期格式错误，请输入正确的日期");
        }
        System.out.println("Executing getEventsOnHistory method");
        // 访问历史上的今天接口
        String url = "https://baike.baidu.com/cms/home/eventsOnHistory/%s.json";
        url = String.format(url, month);
        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.get(url + "?" + body).execute();
        String responseJson = httpResponse.body();
        // 解析 JSON
        JSONObject jsonObject = JSONUtil.parseObj(responseJson);
        JSONArray eventsArray = jsonObject.getJSONObject(month).getJSONArray(month+day);
        JSONObject eventsObject = eventsArray.getJSONObject(0);
        JSONObject filteredObject = new JSONObject();
        filteredObject.put("date", month+day);
        // 去除 HTML 标签
        String cleanText = eventsObject.getStr("desc").replaceAll("<[^>]*>", "");
        // 去除多余的转义字符
        cleanText = cleanText.replaceAll("\\\\\"", "\"");
        filteredObject.put("desc", cleanText);
        Events events = filteredObject.toBean(Events.class);
        String eventsOnHistoryResult = JSONUtil.toJsonStr(events);
        return ResultUtils.success(eventsOnHistoryResult);
    }

    /**
     * 获取抖音热榜
     * @return 返回抖音热榜
     */
    @Cacheable(value = "TIKTOK", key = "#root.methodName")
    @GetMapping("/tiktokHotSearch")
    public BaseResponse<String> getTiktokHotSearch(HttpServletRequest request){
        System.out.println("Executing getTiktokHotSearch method");
        // 访问抖音热榜接口
        String url = "https://apis.tianapi.com/douyinhot/index?key=0496aa3de7ffb29ef40a2b3d51319077";
        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.get(url).execute();
        String responseJson = httpResponse.body();
        // 解析 JSON
        JSONObject jsonObject = JSONUtil.parseObj(responseJson);
        JSONArray tiktokArray = jsonObject.getJSONObject("result").getJSONArray("list");
        List<TiktokHot> tiktokHotList = new ArrayList<>();
        for (int i = 0; i < tiktokArray.size(); i++) {
            JSONObject tiktokObject = tiktokArray.getJSONObject(i); // 获取数组中的第一个对象
            TiktokHot tiktokHot = tiktokObject.toBean(TiktokHot.class);
            tiktokHotList.add(tiktokHot);
        }
        String tiktokHotResult = JSONUtil.toJsonStr(tiktokHotList);
        // 返回
        return ResultUtils.success(tiktokHotResult);
    }
}