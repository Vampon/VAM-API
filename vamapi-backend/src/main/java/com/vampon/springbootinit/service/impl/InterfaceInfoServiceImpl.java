package com.vampon.springbootinit.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.exception.ThrowUtils;
import com.vampon.springbootinit.mapper.InterfaceInfoMapper;
import com.vampon.springbootinit.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.vampon.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.vampon.springbootinit.service.InterfaceInfoService;
import com.vampon.springbootinit.service.UserService;
import com.vampon.springbootinit.utils.VamApiClient;
import com.vampon.vamapicommon.model.entity.InterfaceInfo;
import com.vampon.vamapicommon.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.vampon.springbootinit.constant.RedisConstant.*;

/**
* @author Fang Hao
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-02-28 23:31:22
*/
@Service
@Slf4j
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {
    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();

        // 创建时，参数不能为空
        if (add) {
            if(StringUtils.isAnyBlank(name)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "内容过长");
        }
    }

    @Override
    public Page<InterfaceInfo> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        String method = interfaceInfoQueryRequest.getMethod();
        String name = interfaceInfoQueryRequest.getName();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 从缓存中获取接口分页信息
        // 计算 offset 和 count
        long offset = (current - 1) * size;
        long count = size;
        // 获取redis中的数据
        Set<String> allInterfacesInRedis = stringRedisTemplate.opsForZSet().reverseRange(CACHE_INTERFACEINFO_ALL_KEY, 0, -1);
        Page<InterfaceInfo> page = new Page<>(current, size);
        if(ObjUtil.isNotEmpty(allInterfacesInRedis)){
            //缓存命中
            // 对元素进行分页处理
            List<InterfaceInfo> pagedInterfaces = allInterfacesInRedis.stream()
                    .skip(offset)
                    .limit(count)
                    .filter(interfaceInRedis ->{
                                InterfaceInfo interfaceInfo = this.convertJSONToInterfaceInfo(interfaceInRedis);
                                // 查询条件
                                if(StrUtil.isAllBlank(method,name,description)){
                                    return true;
                                }
                                if(StrUtil.isNotBlank(method)){
                                    if(interfaceInfo.getMethod().contains(method)){
                                        return true;
                                    }
                                }
                                if(StrUtil.isNotBlank(name)){
                                    if(interfaceInfo.getName().contains(name)){
                                        return true;
                                    }
                                }
                                if(StrUtil.isNotBlank(description)){
                                    if(interfaceInfo.getDescription().contains(description)){
                                        return true;
                                    }
                                }
                                return false;
                            }
                    )
                    .map(this::convertJSONToInterfaceInfo)
                    .collect(Collectors.toList());
            page.setRecords(pagedInterfaces);
            page.setTotal(allInterfacesInRedis.size());
            log.info("分页缓存命中");
            return page;
        }
        log.info("分页缓存未命中");
        // 未命中,查询数据库
        // 构建查询条件和排序条件
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("totalInvokes");  // 按照 createTime 字段升序排序
        List<InterfaceInfo> allInterfaceInfoList = list(queryWrapper);
        page.setRecords(allInterfaceInfoList);
        page.setTotal(allInterfaceInfoList.size());
        //存储数据到redis中
        for (InterfaceInfo interfaceInfo : page.getRecords()) {
            // 将 InterfaceInfo 转为 JSON 字符串
            String json = convertInterfaceInfoToJSON(interfaceInfo);

            // 添加到 Redis 的 ZSet 中，使用默认的 score（即调用总次数）
            stringRedisTemplate.opsForZSet().add(CACHE_INTERFACEINFO_ALL_KEY, json,interfaceInfo.getTotalInvokes());

        }
        // 设置过期时间，用来作为如果出现缓存不一致现象的兜底方案
        stringRedisTemplate.expire(CACHE_INTERFACEINFO_ALL_KEY,CACHE_INTERFACEINFO_ALL_TTL,TimeUnit.SECONDS);
        return page;

//        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
//        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
//        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
//                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
//        Page<InterfaceInfo> interfaceInfoPage = this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public String getInvokeResult(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request, InterfaceInfo oldInterfaceInfo) {
        // 接口请求地址
        Long id = oldInterfaceInfo.getId();
        String url = oldInterfaceInfo.getUrl();
        String method = oldInterfaceInfo.getMethod();
        // 接口请求路径
        // todo:正常数据库需要添加一下path字段，这里我先直接截取操作了
        // 找到/api/username部分的起始位置
        int startIndex = url.indexOf("/api/");
        String path = url.substring(startIndex);
        // String path = oldInterfaceInfo.getPath();
        String requestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 获取SDK客户端，并根据yml里配置的网关地址设置reApiClient的网关地址
        User loginUser = userService.getLoginUser(request);// tips:搞清楚request是做什么的
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        VamApiClient vamApiClient = new VamApiClient(accessKey, secretKey);
        // todo:设置网关地址，使用配置类，直接注入新网关地址，避免魔法值，方便上线
        // vamApiClient.setGateway_Host(gatewayConfig.getHost());
        // log.info("generate sdk {} done ",reApiClient);
        String invokeResult=null;
        try {
            // 执行方法
            invokeResult = vamApiClient.invokeInterface(requestParams, method, path);
        } catch (Exception e) {
            // 调用失败，开子线程使用默认参数确认接口是否可用
            //tryAgainUsingOriginalParam(oldInterfaceInfo, id, url, method, path, requestParams, vamApiClient);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用失败");
        }
        // 走到下面，接口肯定调用成功了
        // 如果调用出现了接口内部异常或者路径错误，需要下线接口（网关已经将异常结果统一处理了）
        if (StrUtil.isBlank(invokeResult)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口返回值为空");
        }
        else{
            JSONObject jsonObject;
            try {
                jsonObject = JSONUtil.parseObj(invokeResult);
            }catch (Exception e){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口响应参数不规范");//JSON转化失败，响应数据不是JSON格式
            }
            int code;
            String msg;
            try {
                code =(int) Optional.ofNullable(jsonObject.get("code")).orElse("-1");//要求接口返回必须是统一响应格式
                msg = (String) Optional.ofNullable(jsonObject.get("message")).orElse("无");
            }catch (Exception e){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口响应参数不规范");
            }
            ThrowUtils.throwIf(code==-1,ErrorCode.SYSTEM_ERROR,"接口响应参数不规范");//响应参数里不包含code

            if (code==ErrorCode.SYSTEM_ERROR.getCode()){
//                offlineInterface(id);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口异常，即将关闭接口");
            }
            else if (code==ErrorCode.NOT_FOUND_ERROR.getCode()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口路径不存在: %s".format(msg));
            }
            // 请求参数错误
            else if (code==ErrorCode.PARAMS_ERROR.getCode()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, String.format("请求参数错误: %s", msg));
            } else if (code==ErrorCode.OPERATION_ERROR.getCode()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, String.format("操作失败: %s", msg));
            } else if (code==ErrorCode.FORBIDDEN_ERROR.getCode()){
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, String.format("限制访问: %s", msg));
            }
            return invokeResult;
        }
    }

    private String convertInterfaceInfoToJSON(InterfaceInfo interfaceInfo) {
        return JSON.toJSONString(interfaceInfo);
    }
    private InterfaceInfo convertJSONToInterfaceInfo(String interfaceInfoJSON) {
        return JSON.parseObject(interfaceInfoJSON, InterfaceInfo.class);
    }

    @Override
    public void deleteRedisCache(Long id){
        stringRedisTemplate.delete(CACHE_INTERFACEINFO_ALL_KEY);
        stringRedisTemplate.delete(CACHE_INTERFACEINFO_KEY + id);
    }

    @Override
    public void deleteAllRedisCache(){
        stringRedisTemplate.delete(CACHE_INTERFACEINFO_ALL_KEY);
    }

    @Override
    public InterfaceInfo getInterfaceInfoById(Long id) {
        // 查询缓存
        String interfaceInfoJson = stringRedisTemplate.opsForValue().get(CACHE_INTERFACEINFO_KEY + id);
        if(StrUtil.isNotBlank(interfaceInfoJson)){
            log.info("id:" + id + "接口信息缓存命中");
            return convertJSONToInterfaceInfo(interfaceInfoJson);
        }
        // 判断是否是空值
        // 因为StrUtil.isNotBlank是false对应的是字符串里是""或者为null，因此如果过了上面的if，只需在这里再判断一下不是null，就可以确定是空值""了
        if(interfaceInfoJson!=null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR , "请求接口数据不存在");
        }

        // 缓存没有命中
        log.info("id:" + id + "接口信息缓存未命中");
        InterfaceInfo interfaceInfo = getById(id);
        //
        if(interfaceInfo==null){
            // 将空值也存入redis缓存，为了防止缓存穿透问题
            stringRedisTemplate.opsForValue().set(CACHE_INTERFACEINFO_KEY + id,"",CACHE_NULL_TTL);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR , "请求接口数据不存在");
        }
        // 将 InterfaceInfo 转为 JSON 字符串
        String json = convertInterfaceInfoToJSON(interfaceInfo);
        // CACHE_INTERFACEINFO_TTL是20到40的随机值，应对可能发生的缓存雪崩问题
        stringRedisTemplate.opsForValue().set(CACHE_INTERFACEINFO_KEY + id,json,CACHE_INTERFACEINFO_TTL);
        return interfaceInfo;
    }

//    @Override
//    public Long getInterfaceInfoTotalInvokesCount() {
//        return interfaceInfoMapper.getInterfaceInfoTotalInvokesCount().longValue();
//    }


    @Override
    public boolean addInterfaceInvokeCount(Long interfaceInfoId) {
        LambdaUpdateWrapper<InterfaceInfo> interfaceInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        interfaceInfoLambdaUpdateWrapper.eq(InterfaceInfo::getId, interfaceInfoId);
        interfaceInfoLambdaUpdateWrapper.setSql("totalInvokes = totalInvokes + " + 1);
        return this.update(interfaceInfoLambdaUpdateWrapper);
    }

}




