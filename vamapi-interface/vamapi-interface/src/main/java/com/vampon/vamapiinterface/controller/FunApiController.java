package com.vampon.vamapiinterface.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapicommon.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

/**
 * 有意思的接口
 *
 */
@RestController
public class FunApiController {

    private static BaseResponse<String> invokeOuterApi(String url, String body) {
        System.out.println((url + " " + body));
        try(HttpResponse httpResponse = HttpRequest.get(url + "?" + body)
                .execute()){
            return ResultUtils.success(httpResponse.body());
        }
    }
    /**
     * 随机头像
     */
    @PostMapping("/rand.avatar")
    public BaseResponse<String> randAvatar(HttpServletRequest request) {
        String url = "https://api.uomg.com/api/rand.avatar";
        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
        return invokeOuterApi(url, body);
    }

    /**
     * 随机壁纸
     */
    @PostMapping("/sjbz")
    public BaseResponse<String> randImages(HttpServletRequest request) {
        String url = "http://api.btstu.cn/sjbz/api.php";
        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
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
    public BaseResponse<String> getWeather(HttpServletRequest request) {
        String url = "https://api.vvhan.com/api/weather";
        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
        return invokeOuterApi(url, body);
    }

    /**
     * 获取微博热搜
     * @return 返回微博热搜
     */
//    @GetMapping("/weiboHotSearch")
//    public BaseResponse<String> getWeiboHotSearch(HttpServletRequest request){
//        // 1. 访问微博热搜接口
//        String url = "https://weibo.com/ajax/side/hotSearch";
//        String body = URLUtil.decode(request.getHeader("body"), CharsetUtil.CHARSET_UTF_8);
//        HttpResponse httpResponse = HttpRequest.get(url + "?" + body).execute();
//        String responseJson = httpResponse.body();
//        // 解析 JSON
//        JSONObject jsonObject = JSONObject.parseObject(responseJson);
//
//        // 获取微博的realtime数组
//        JSONArray realtimeArray = jsonObject.getJSONObject("data").getJSONArray("realtime");
//        // 遍历realtime数组并只保留note、label_name和num字段
//
//        List<WeiboHot> weiboHotList = new ArrayList<>();
//        for (int i = 0; i < realtimeArray.size(); i++) {
//            JSONObject realtimeObject = realtimeArray.getJSONObject(i);
//            JSONObject filteredObject = new JSONObject();
//            String note = realtimeObject.getString("note");
//            filteredObject.put("index", i+1);
//            filteredObject.put("title", note);
//            filteredObject.put("hotType", realtimeObject.getString("label_name"));
//            filteredObject.put("hotNum", realtimeObject.getInteger("num"));
//            filteredObject.put("url", "https://s.weibo.com/weibo?q=%23"+ URLUtil.encode(note) +"%23");
//            WeiboHot weiboHot = filteredObject.toJavaObject(WeiboHot.class);
//            weiboHotList.add(weiboHot);
//        }
//        WeiboHotSearchResponse weiboHotSearchResponse = new WeiboHotSearchResponse();
//        weiboHotSearchResponse.setWeibohotSearch(weiboHotList);
//
//        // 3.返回
//        return weiboHotSearchResponse;
//    }
}