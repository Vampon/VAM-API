package com.vampon.vamapiinterface.controller;
import cn.hutool.Hutool;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ErrorCode;
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
        if(chat.getPrompt().length()>100){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数过长");
        }
        // 构建消息体
        MediaType mediaType = MediaType.parse("application/json");
        String prompt = "你是一个无所不知的智能机器人，为我答疑解惑。我的问题是%s。请注意，你的所有回答字数都应控制在200字以内";
        prompt = String.format(prompt, chat.getPrompt());
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
        // 校验
        if (birthTime == null || gender == null || birthPlace == null || currentQuestion == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if ( birthTime.length() > 50 || birthPlace.length() > 50 || currentQuestion.length() > 50){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数过长");
        }
        // 构建消息体
        MediaType mediaType = MediaType.parse("application/json");
        String prompt = "你将扮演一个算命大师，为我答疑解惑。我的出生时间是%s,性别是%s,出生地点是%s,当前关注的问题是%s,请你告诉我预测的结果。请注意，你的所有回答字数都应控制在100字以内";
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

    @GetMapping("/joker_master")
    public static BaseResponse<String> fortuneTellers() throws IOException{
        // 构建消息体
        MediaType mediaType = MediaType.parse("application/json");
        String prompt = "你将扮演一位笑话大师，为我讲述各种令人捧腹的笑话。请你讲述一段笑话，请注意，你的所有回答字数都应控制在100字以内";
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
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "请求失败");

        }
    }

    @GetMapping("/talk_rubbish")
    public static BaseResponse<String> talkRubbish() throws IOException{
        // 构建消息体
        MediaType mediaType = MediaType.parse("application/json");
        String prompt = "你将扮演一位抽象大师，说出一段离谱至极的胡言乱语，可以针对任何事件任何人物任何观点，请注意，你的所有回答字数都应控制在100字以内";
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
