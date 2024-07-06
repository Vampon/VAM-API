package com.vampon.vamapiinterface.controller;
import cn.hutool.Hutool;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapiinterface.model.Chat;
import com.vampon.vamapiinterface.model.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
@RestController
@Slf4j
public class VamBotController {
    public static final String API_KEY = "ygL4fJcQyh2p45766VTbMlzt";
    public static final String SECRET_KEY = "tqXRoBjALN3LP1iEx35pLmKsONLfN4VR";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @PostMapping("/vam_bot")
    public static BaseResponse<String> chatWithBot(@org.springframework.web.bind.annotation.RequestBody Chat chat) throws IOException{
        log.info("RequestBody:" + chat);
        // 构建消息体
        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyString = "{\"messages\":[{\"role\":\"user\",\"content\":\"" + chat.getPrompt() + "\"}],\"disable_search\":false,\"enable_citation\":false}";
        RequestBody body = RequestBody.create(mediaType, requestBodyString);
        // 构建请求
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        // 发送请求并处理响应
        Response response = HTTP_CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBodyString = response.body().string();
            System.out.println(responseBodyString);
            ChatResponse chatResponse = JSONUtil.toBean(responseBodyString, ChatResponse.class);
//            String chatResult = chatResponse.getResult();
//            Gson gson = new GsonBuilder().create();
//            ChatResponse chatResponse = gson.fromJson(responseBodyString, ChatResponse.class);
            String chatResult = chatResponse.getResult();
            return ResultUtils.success(chatResult);
        } else {
            // 处理请求失败的情况，例如重试请求或记录错误
            return ResultUtils.error(50000, "请求失败");
        }
//        System.out.println(response.body().string());
//        ChatResponse chatResponse = JSONUtil.toBean(response.body().string(), ChatResponse.class);
//        String chatResult = chatResponse.getResult();
//        return ResultUtils.success(chatResult);
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


    @GetMapping("/fortune_tellers")
    public static BaseResponse<String> fortuneTellers(String birthTime, String gender, String birthPlace,
                                                      String currentQuestion) throws IOException{
        // 构建消息体
        MediaType mediaType = MediaType.parse("application/json");
        String prompt = "你将扮演一个算命大师，为我答疑解惑。我的出生时间是%s,性别是%s,出生地点是%s,当前关注的问题是%s,请你告诉我预测的结果。";
        prompt = String.format(prompt, birthTime, gender, birthPlace, currentQuestion);
        String requestBodyString = "{\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt + "\"}],\"disable_search\":false,\"enable_citation\":false}";
        RequestBody body = RequestBody.create(mediaType, requestBodyString);
        // 构建请求
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        // 发送请求并处理响应
        Response response = HTTP_CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBodyString = response.body().string();
            System.out.println(responseBodyString);
            ChatResponse chatResponse = JSONUtil.toBean(responseBodyString, ChatResponse.class);
            String chatResult = chatResponse.getResult();
            return ResultUtils.success(chatResult);
        } else {
            // 处理请求失败的情况，例如重试请求或记录错误
            return ResultUtils.error(50000, "请求失败");
        }
    }


    public static void main(String[] args) {

        Chat chat = new Chat();
        chat.setPrompt("你好");
        try {
            String result = chatWithBot(chat).getData();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
