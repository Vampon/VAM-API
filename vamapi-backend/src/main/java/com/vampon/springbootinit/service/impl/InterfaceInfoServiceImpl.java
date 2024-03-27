package com.vampon.springbootinit.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.exception.ThrowUtils;
import com.vampon.springbootinit.mapper.InterfaceInfoMapper;
import com.vampon.springbootinit.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.vampon.springbootinit.service.InterfaceInfoService;
import com.vampon.springbootinit.service.UserService;
import com.vampon.springbootinit.utils.VamApiClient;
import com.vampon.vamapicommon.model.entity.InterfaceInfo;
import com.vampon.vamapicommon.model.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
* @author Fang Hao
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-02-28 23:31:22
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {
    @Resource
    private UserService userService;


    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

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
        //log.info("generate sdk {} done ",reApiClient);
        System.out.println("请求参数：" + requestParams);
        String invokeResult=null;
        try {
            // 执行方法
            invokeResult = vamApiClient.invokeInterface(id,requestParams, url, method,path);
        } catch (Exception e) {
            // 调用失败，开子线程使用默认参数确认接口是否可用
            //tryAgainUsingOriginalParam(oldInterfaceInfo, id, url, method, path, requestParams, reApiClient);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口调用失败");
        }
        // 走到下面，接口肯定调用成功了
        // 如果调用出现了接口内部异常或者路径错误，需要下线接口（网关已经将异常结果统一处理了）
        if (StrUtil.isBlank(invokeResult)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口返回值为空");
        }
        else{
            JSONObject jsonObject;
            try {
                jsonObject = JSONUtil.parseObj(invokeResult);
            }catch (Exception e){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口响应参数不规范");//JSON转化失败，响应数据不是JSON格式
            }
            int code =(int) Optional.ofNullable(jsonObject.get("code")).orElse("-1");//要求接口返回必须是统一响应格式
            ThrowUtils.throwIf(code==-1,ErrorCode.SYSTEM_ERROR,"接口响应参数不规范");//响应参数里不包含code

            if(code==ErrorCode.SYSTEM_ERROR.getCode()){
//                offlineInterface(id);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口异常，即将关闭接口");
            }
            else if(code==ErrorCode.NOT_FOUND_ERROR.getCode()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口路径不存在");
            }
            // 请求参数错误
            else if(code==ErrorCode.PARAMS_ERROR.getCode()){
                throw new BusinessException((ErrorCode.PARAMS_ERROR));
            }
            return invokeResult;
        }
    }

}




