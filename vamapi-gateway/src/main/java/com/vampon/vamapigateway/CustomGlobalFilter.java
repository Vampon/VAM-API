package com.vampon.vamapigateway;

import com.vampon.manager.RedisLimiterManager;
import com.vampon.vamapicommon.model.entity.InterfaceInfo;
import com.vampon.vamapicommon.model.entity.User;
import com.vampon.vamapicommon.service.InnerInterfaceInfoService;
import com.vampon.vamapicommon.service.InnerUserInterfaceInfoService;
import com.vampon.vamapicommon.service.InnerUserService;
import com.vampon.vamapigateway.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component

public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    private static final List<String> IP_WHERE_LIST = Arrays.asList("127.0.0.1");//测试白名单
    public static final String INTERFACE_HOST = "http://localhost:8123";
    /**
     * 全局过滤
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 用户发送请求到API网关（默认能达到这里就已经算完成）
        // 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识" + request.getId());
        log.info("请求路径" + path);
        log.info("请求方法" + method);
        log.info("请求参数" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址" + sourceAddress);
        log.info("请求来源地址" + request.getRemoteAddress());

        // 访问控制-黑白名单
        ServerHttpResponse response = exchange.getResponse();
        if(!IP_WHERE_LIST.contains(sourceAddress))
        {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 用户鉴权（ak,sk）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        //todo:这里实际上应该需要先去数据库中查询一下是否已分配给该用户（done）
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser error", e);
        }
        if(invokeUser == null){
            return handleNoAuth(response);
        }
        // 基于Redisson对单个用户每秒调用次数限流
        // redisLimiterManager.doRateLimit("invoke_" + invokeUser.getId());
        //todo:这里不应该使用魔法值vampon
        if(!"vampon".equals(accessKey) ) //tips:why flip?
        {
            return handleNoAuth(response);
        }
        //todo:这里可以使用redis来存储随机数
        if(Long.parseLong(nonce) > 10000)
        {
            return handleNoAuth(response);
        }
        // 需要校验一下时间戳是否相距合理，和当前时间不能超过5分钟
        Long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES)
        {
            return handleNoAuth(response);
        }

        // 构造和客户端相同的hashMap
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey",accessKey);
        hashMap.put("nonce",nonce);
        hashMap.put("timestamp",timestamp);
        hashMap.put("body",body);
        //todo:实际情况中从数据库中查出secretKey（done）
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(hashMap, secretKey);
        if(sign == null || !serverSign.equals(sign))
        {
            return handleNoAuth(response);
        }

        // 请求的模拟接口是否存在
        // todo: 从数据库中查询模拟接口是否存在，以及请求方法是否匹配（done）
        // todo: 因为网关项目没有引入mybatis等操作数据库的方法，最好由backend提供接口，直接调用（done）

        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        }catch (Exception e){
            log.error("getInterfaceInfo error", e);
        }
        if(interfaceInfo == null){
            return handleNoAuth(response);
        }
        //todo: 校验是否还有调用次数
        // 请求转发，调用模拟接口 + 响应日志（done）
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());

//        Mono<Void> filter = chain.filter(exchange);
//        // 响应日志
//        log.info("相应" + response.getStatusCode());
//        // todo: 调用成功，调用接口次数 + 1
//        if (response.getStatusCode() == HttpStatus.OK)
//        {
//
//        } else {
//            // 调用失败，返回一个规范的错误码
//            return handleInvokeError(response);
//        }
//        return filter;

    }

    /**
     * 处理响应（因为原始的是异步的，它是先记录日志，再调用原始接口，因此在这里需要自定义一下）
     * tips：再好好读读
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) { // , long interfaceInfoId, long userId
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = (HttpStatus) originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override//可以决定多个过滤器的顺序
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response)
    {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response)
    {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}