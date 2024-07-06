package com.vampon.vamapiclientsdk.client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.vampon.vamapiclientsdk.utils.SignUtils.getSign;


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
        // 密钥一般不能在服务器间进行传输，会被拦截重放，不安全
        // hashMap.put("secretKey",secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        // todo 此处因为涉及到中文参数的时候，就会出现不一致的现象，这里先把请求体去掉，后续再找解决办法
//        hashMap.put("body",body);
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


}
