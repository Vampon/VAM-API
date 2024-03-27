package com.vampon.vamapiinterface.controller;
import cn.hutool.json.JSONObject;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapiinterface.model.Chat;
import okhttp3.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
@RestController
public class VamBotController {
    public static final String API_KEY = "ygL4fJcQyh2p45766VTbMlzt";
    public static final String SECRET_KEY = "tqXRoBjALN3LP1iEx35pLmKsONLfN4VR";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @PostMapping("/vam_bot")
    public static BaseResponse<String> chatWithBot(@org.springframework.web.bind.annotation.RequestBody Chat chat) throws IOException{
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
        // System.out.println(response.body().string());
        String botResponse = response.body().string();
        return ResultUtils.success(botResponse);
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
