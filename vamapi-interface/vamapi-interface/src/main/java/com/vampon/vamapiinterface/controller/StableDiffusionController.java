package com.vampon.vamapiinterface.controller;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ErrorCode;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapiinterface.model.Enum.StableDiffusionStyleEnum;
import com.vampon.vamapiinterface.model.StableDiffusionPrompt;
import com.vampon.vamapiinterface.model.TiktokHot;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.*;
import cn.hutool.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class StableDiffusionController {
    public static final String API_KEY = "ygL4fJcQyh2p45766VTbMlzt";
    public static final String SECRET_KEY = "tqXRoBjALN3LP1iEx35pLmKsONLfN4VR";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(40, TimeUnit.SECONDS) // 设置连接超时时间为10秒
            .readTimeout(40, TimeUnit.SECONDS) // 设置读超时时间为30秒
            .writeTimeout(30, TimeUnit.SECONDS) // 设置写超时时间为10秒
            .build();


    @PostMapping("/word2img")
    public static BaseResponse<String> word2img(@org.springframework.web.bind.annotation.RequestBody StableDiffusionPrompt stableDiffusionPrompt) throws IOException{
        if(stableDiffusionPrompt.getPositive_prompt().length()>100){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数过长");
        }
        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyString = "{\"prompt\":\"" + stableDiffusionPrompt.getPositive_prompt()
                + "\",\"style\":\"" + StableDiffusionStyleEnum.getEnumByValue(stableDiffusionPrompt.getStyle()).getText()
                + "\",\"size\":\"768x768\",\"n\":1,\"steps\":10,\"sampler_index\":\"Euler a\",\"seed\":56}";
        System.out.println(requestBodyString);
        RequestBody body = RequestBody.create(mediaType, requestBodyString);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/text2image/sd_xl?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String sdResponse = response.body().string();
        return ResultUtils.success(sdResponse);
    }

    @Cacheable(value = "TODAYPROTRAIT", key = "#root.methodName")
    @PostMapping("/today_portrait")
    public BaseResponse<String> todayPortrait() throws IOException{
        // 通过热榜提取今日关键字
        String url = "https://apis.tianapi.com/douyinhot/index?key=0496aa3de7ffb29ef40a2b3d51319077";
        HttpResponse httpResponse = HttpRequest.get(url).execute();
        String responseJson = httpResponse.body();
        // 解析 JSON
        JSONObject jsonObject = JSONUtil.parseObj(responseJson);
        JSONArray tiktokArray = jsonObject.getJSONObject("result").getJSONArray("list");
        StringBuilder hotWord = new StringBuilder();
        int useLength = Math.min(tiktokArray.size(), 5);
        for (int i = 0; i < useLength; i++) {
            JSONObject tiktokObject = tiktokArray.getJSONObject(i);
            TiktokHot tiktokHot = tiktokObject.toBean(TiktokHot.class);
            hotWord.append(tiktokHot.getWord());
            hotWord.append(",");
        }
        String positive_prompt = hotWord.toString();
        // 以文生图
        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyString = "{\"prompt\":\"" + positive_prompt
                + "\",\"style\":\"" + "Base"
                + "\",\"size\":\"768x768\",\"n\":1,\"steps\":30,\"sampler_index\":\"Euler a\",\"seed\":56}";
        System.out.println(requestBodyString);
        RequestBody body = RequestBody.create(mediaType, requestBodyString);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/text2image/sd_xl?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String sdResponse = response.body().string();
        return ResultUtils.success(sdResponse);
    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getStr("access_token");
    }
}
