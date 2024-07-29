package com.vampon.vamapigateway;

import com.alibaba.fastjson.JSON;
import com.vampon.vamapigateway.exception.BusinessException;
import com.vampon.vamapigateway.manager.RedisLimiterManager;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ErrorCode;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapicommon.model.entity.InterfaceInfo;
import com.vampon.vamapicommon.model.entity.User;
import com.vampon.vamapicommon.service.InnerInterfaceInfoService;
import com.vampon.vamapicommon.service.InnerInterfaceLogService;
import com.vampon.vamapicommon.service.InnerUserInterfaceInfoService;
import com.vampon.vamapicommon.service.InnerUserService;
import com.vampon.vamapigateway.utils.RedissonLockUtil;
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
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    @DubboReference
    private InnerInterfaceLogService interfaceLogService;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private RedissonLockUtil redissonLockUtil;


    // threadLocalVariable.set(threadLocalVariable.get() + 1);
    // 接口调用开始时间
//    private Long startTime = null;
    // 考虑到并发安全问题，弃用上述方式，选用ThreadLocal实现
    // private ThreadLocal<Long> startTime = ThreadLocal.withInitial(() -> 0L);


    private static final List<String> IP_WHERE_LIST = Arrays.asList("127.0.0.1");//测试白名单

    //todo:将魔法值去除，改成可通过配置文件配置
    public static final String INTERFACE_HOST = "http://localhost:8123";
    public static final String GATEWAY_HOST = "http://localhost:8090";
    /**
     * 全局过滤
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 用户发送请求到API网关（默认能达到这里就已经算完成）
        // startTime.set(startTime.get() + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        // startTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        exchange.getAttributes().put("startTime", LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        // 请求日志
        ServerHttpRequest request = exchange.getRequest();
        // 记录请求流量，获取请求内容长度
        // todo:这块数据大小记录的不对，一直为-1
        Long requestContentLength = request.getHeaders().getContentLength();
        String path = GATEWAY_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        String url = request.getURI().toString().trim();
        // 获取公网ip
        HttpHeaders headers = request.getHeaders();
        String ipAddress = headers.getFirst("X-Real-IP");
        String uri = headers.getFirst("X-Original-URI");
        String host = headers.getFirst("X-Original-Host");
        String scheme = headers.getFirst("X-Original-Scheme");

        log.info("请求唯一标识" + request.getId());
        log.info("请求路径" + path);
        log.info("请求方法" + method);
        log.info("请求参数" + request.getQueryParams());
        log.info("请求流量（bytes）：" + requestContentLength);
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址" + sourceAddress);
        log.info("请求来源地址" + request.getRemoteAddress());

        // 基于Redisson对全局用户每秒调用次数限流
        redisLimiterManager.doGlobalRateLimit("Global_Limit_Key");

        // 访问控制-黑白名单
        ServerHttpResponse response = exchange.getResponse();
        try {
            if(!IP_WHERE_LIST.contains(sourceAddress))
            {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
            // 用户鉴权（ak,sk）
            String accessKey = headers.getFirst("accessKey");
            String nonce = headers.getFirst("nonce");
            String timestamp = headers.getFirst("timestamp");
            String sign = headers.getFirst("sign");
            String body = headers.getFirst("body");
            // 先去数据库中查询一下是否已分配给该用户
            User invokeUser = null;
            try {
                invokeUser = innerUserService.getInvokeUser(accessKey);
            }catch (Exception e){
                log.error("getInvokeUser error", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取用户信息失败");
            }
            if(invokeUser == null){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户信息不存在");
            }
            // 如果用户被封号，直接拒绝
            if("suspend".equals(invokeUser.getUserRole())){
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被封禁，请联系管理员");
            }

            // 基于Redisson对单个用户每秒调用次数限流
            redisLimiterManager.doUserRateLimit("invoke_" + invokeUser.getId());
            // 这里不应该使用魔法值vampon(done)
            if(!invokeUser.getAccessKey().equals(accessKey) )
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "凭证信息校验错误");
            }
            //todo:这里可以使用redis来存储随机数
            if(Long.parseLong(nonce) > 10000)
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "凭证信息可能被重放，请确认凭证是否泄露，如泄露请尽快更新");
            }
            // 需要校验一下时间戳是否相距合理，和当前时间不能超过5分钟
            Long currentTime = System.currentTimeMillis() / 1000;
            final Long FIVE_MINUTES = 60 * 5L;
            if((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES)
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "凭证信息可能被重放，请确认凭证是否泄露，如泄露请尽快更新");
            }

            if (invokeUser.getBalance() <= 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "余额不足，请先充值");
            }

            // 构造和客户端相同的hashMap
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("accessKey",accessKey);
            hashMap.put("nonce",nonce);
            hashMap.put("timestamp",timestamp);
            // todo 此处因为涉及到中文参数的时候，就会出现不一致的现象，这里先把请求体去掉，后续再找解决办法
            // hashMap.put("body",body);
            // 从数据库中查出secretKey（done）
            String secretKey = invokeUser.getSecretKey();
            String serverSign = SignUtils.getSign(hashMap, secretKey);
            if(sign == null || !serverSign.equals(sign))
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "凭证信息校验错误");
            }

            // 请求的模拟接口是否存在
            // 从数据库中查询模拟接口是否存在，以及请求方法是否匹配（done）
            // 因为网关项目没有引入mybatis等操作数据库的方法，最好由backend提供接口，直接调用（done）
            // todo:这里是通过path和method来查询接口是否存在的，这造成个bug，也就是端口号8123和8090不匹配的错误
            InterfaceInfo interfaceInfo = null;
            try {
                interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
            }catch (Exception e){
                log.error("getInterfaceInfo error", e);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询接口信息失败");
            }
            if(interfaceInfo == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口信息不存在");
            }
            // 校验是否开通接口
            // todo:这个方法原本是为了校验有没有剩余调用次数的，但是现在改成积分调用了，这块暂时被用来校验是否开通了接口，后续需要调整
            try {
                int leftNum = innerUserInterfaceInfoService.getUserInterfaceInfoLeftNum(interfaceInfo.getId(), invokeUser.getId());
                if(leftNum <= 0){
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "请先开通接口");
                }
            } catch (Exception e) {
                log.error("getUserInterfaceInfoLeftNum error", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "请先开通接口");
            }

            // 请求转发，调用模拟接口 + 响应日志（done）
            return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId(), ipAddress, requestContentLength);
        } catch (BusinessException e){
            return handleErrorResponse(response, e.getCode(), e.getMessage());
        } catch (Exception e){
            return handleErrorResponse(response, ErrorCode.SYSTEM_ERROR.getCode(), "系统错误");
        }

    }

    /**
     * 处理响应（因为原始的是异步的，它是先记录日志，再调用原始接口，因此在这里需要自定义一下）
     * tips：再好好读读
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId, String ipAddress, long requestContentLength) { // , long interfaceInfoId, long userId
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
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body).cache();;
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 调用成功，接口调用次数 + 1 invokeCount（历史逻辑，后续移除，userInterface表以后可以改为用户接口开通关系表）
//                                        try {
//                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
//                                        } catch (Exception e) {
//                                            log.error("invokeCount error", e);
//                                        }
                                        // 扣除积分（分布式锁保证并发安全性）
                                        redissonLockUtil.redissonDistributedLocks(("gateway_" + userId).intern(), () -> {
                                            log.info("调用请求后扣款方法：interfaceId:{}  userId:{}", interfaceInfoId, userId);
                                            boolean invoke = innerUserInterfaceInfoService.invokeProcess(interfaceInfoId, userId);
                                            if (!invoke) {
                                                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用繁忙，请稍后再试");
                                            }
                                        }, "接口调用失败");
                                        log.info("网关调用接口调用次数+1: {}", interfaceInfoId);
                                        log.info("开始存储日志......");
                                        Long startTime = exchange.getAttribute("startTime");
                                        interfaceLogService.save(interfaceInfoId, userId, startTime, ipAddress, requestContentLength);
                                        log.info("日志存储完成");
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

//    public Mono<Void> handleNoAuth(ServerHttpResponse response)
//    {
//        response.setStatusCode(HttpStatus.FORBIDDEN);
//        return response.setComplete();
//    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response)
    {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    private Mono<Void> handleErrorResponse(ServerHttpResponse response, int errorCode, String errorMessage) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        DataBufferFactory bufferFactory = response.bufferFactory();
        BaseResponse<?> errorResponse = ResultUtils.error(errorCode, errorMessage);
        byte[] errorBytes = JSON.toJSONString(errorResponse).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = bufferFactory.wrap(errorBytes);
        return response.writeWith(Mono.just(buffer));
    }
}