package com.vampon.vamapiclientsdk.client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

import static com.vampon.vamapiclientsdk.utils.SignUtils.getSign;


/**
 * 调用第三方接口的客户端
 * 一些校验尽可能放在客户端，不要等发送到网关再进行校验，减少压力
 */
@Slf4j
public class VamApiClient {
    private static final String DEFAULT_GATEWAY_HOST = "http://vamapi.cloud/gateway";
    private String gatewayHost;
    private final String accessKey;
    private final String secretKey;

    public VamApiClient(String accessKey, String secretKey) {
        this(accessKey, secretKey, DEFAULT_GATEWAY_HOST);
    }

    public VamApiClient(String accessKey, String secretKey, String gatewayHost) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.gatewayHost = gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    private Map<String, String> getHeaderMap(String body){
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey",accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign",getSign(hashMap, secretKey));
        return hashMap;
    }


    /**
     * 支持调用任意接口，把请求导向网关
     * @param params 接口参数
     * @param method 接口使用方法
     * @return 接口调用结果
     */
    public String invokeInterface(String params, String method, String path) {
        log.info("请求参数：" + params);
        log.info("请求方法：" + method);
        log.info("请求路径：" + path);
        if (ObjectUtils.isEmpty(method)) {
            throw new IllegalArgumentException("请填写请求方法");
        }
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("请填写请求路径");
        }
        // 匹配 /api/ 开头的路径
        if (!path.matches("^/api/.*")){
            throw new IllegalArgumentException("路径格式不正确: " + path);
        }
        method = method.trim().toUpperCase();
        log.info("SDK正在转发至GATEWAY_HOST:{}", gatewayHost);
        String result;
        HttpRequest request;
        if ("GET".equalsIgnoreCase(method)) {
            String fullUrl = gatewayHost + path + "?" + params;
            request = HttpRequest.get(fullUrl)
                    .header("Accept-Charset", CharsetUtil.UTF_8)
                    .addHeaders(getHeaderMap(params));
        } else if ("POST".equalsIgnoreCase(method)) {
            request = HttpRequest.post(gatewayHost + path)
                    .header("Accept-Charset", CharsetUtil.UTF_8)
                    .addHeaders(getHeaderMap(params))
                    .body(params);
        } else {
            throw new IllegalArgumentException("不支持的请求方法: " + method);
        }

        try (HttpResponse httpResponse = request.execute()) {
            String body = httpResponse.body();
            result = JSONUtil.formatJsonStr(body);
        } catch (Exception e) {
            log.error("SDK调用接口失败：{}", e.getMessage(), e);
            result = "{\"code\": 500, \"msg\": \"接口调用失败\", \"data\": \"null\"}";
        }

        log.info("SDK调用接口完成，响应数据：{}", result);
        return result;
    }


}
