package com.vampon.springbootinit.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.vampon.springbootinit.model.client.TestUser;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.vampon.springbootinit.utils.SignUtils.getSign;

/**
 * 调用第三方接口的客户端
 */
@Slf4j
public class VamApiClient {
    // todo:将魔法值修改了
    private static String GATEWAY_HOST = "http://localhost:8090";
    private String accessKey;
    private String secretKey;

    public VamApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public void setGateway_Host(String gatewayHost) {
        GATEWAY_HOST = gatewayHost;
    }

    private Map<String, String> getHeaderMap(String body){
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey",accessKey);
        //密钥一般不能在服务器间进行传输，会被拦截重放，不安全
        // hashMap.put("secretKey",secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body",body);
        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign",getSign(hashMap, secretKey));
        return hashMap;
    }

    /**
     * 支持调用任意接口，把请求导向网关
     * @param params 接口参数
     * @param url 接口地址
     * @param method 接口使用方法
     * @return 接口调用结果
     */
    public String invokeInterface(long id,String params, String url, String method,String path)  {
        String result;
        log.info("SDK正在转发至GATEWAY_HOST:{}",GATEWAY_HOST);
        try(
                HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + path)
                        // 处理中文编码
                        .header("Accept-Charset", CharsetUtil.UTF_8)
                        .addHeaders(getHeaderMap(params))
                        .body(params)
                        .execute())
        {
            String body = httpResponse.body();
            /*
            // 可以在SDK处理接口404的情况
            if(httpResponse.getStatus()==404){
                body = String.format("{\"code\": %d,\"msg\":\"%s\",\"data\":\"%s\"}",
                        httpResponse.getStatus(), "接口请求路径不存在", "null");
                log.info("响应结果：" + body);
            }
            */
            // 将返回的JSON结果格式化，其实就是加换行符
            result=JSONUtil.formatJsonStr(body);
        }
        log.info("SDK调用接口完成，响应数据：{}",result);
        return result;
    }

//    public String getNameByGet(String name)
//    {
//        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("name", name);
//
//        String result= HttpUtil.get("http://localhost:8090/api/name", paramMap);
//        System.out.println(result);
//        return result;
//    }
//
//
//    public String getnameByPost(String name)
//    {
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("name", name);
//
//        String result= HttpUtil.post("http://localhost:8090/api/name", paramMap);
//        System.out.println(result);
//        return result;
//    }



//    public String getUsernameByPost(TestUser user)
//    {
//        String json = JSONUtil.toJsonStr(user);
//        String url = "http://localhost:8090/api/username";
//        String result = HttpRequest.post(url)
//                .addHeaders(getHeaderMap(json))
//                .body(json)
//                .execute().body(); // tip:这个再仔细查查
//        System.out.println(result);
//        return result;
//    }

}
