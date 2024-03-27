package com.vampon.vamapiinterface.controller;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapiinterface.model.StableDiffusionPrompt;
import okhttp3.*;
import cn.hutool.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
public class StableDiffusionController {
    public static final String API_KEY = "ygL4fJcQyh2p45766VTbMlzt";
    public static final String SECRET_KEY = "tqXRoBjALN3LP1iEx35pLmKsONLfN4VR";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @PostMapping("/word2img")
    public static BaseResponse<String> word2img(@org.springframework.web.bind.annotation.RequestBody StableDiffusionPrompt stableDiffusionPrompt) throws IOException{
        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyString = "{\"prompt\":\"" + stableDiffusionPrompt.getPositive_prompt() + ",\"size\":\"1024x1024\",\"n\":1,\"steps\":10,\"sampler_index\":\"Euler a\"}";
        RequestBody body = RequestBody.create(mediaType, requestBodyString);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/text2image/sd_xl?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        // System.out.println(response.body().string());
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
