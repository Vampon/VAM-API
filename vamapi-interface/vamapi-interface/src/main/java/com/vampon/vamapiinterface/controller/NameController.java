package com.vampon.vamapiinterface.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.vampon.vamapiclientsdk.model.User;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapiinterface.model.Result;
import com.vampon.vamapiinterface.utils.SignUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 查询名称API
 * @author vampon
 */
@RestController
//@RequestMapping
public class NameController {

//    @GetMapping("/name")
//    public Result getNameByGet(String name)
//    {
//
//        return "GET 返回名字" + name;
//    }
//
//    @PostMapping("/name")
//    public Result getnameByPost(@RequestParam String name)
//    {
//        return "POST 返回名字" + name;
//    }

    @PostMapping("/username")
    public BaseResponse<String> getUsernameByPost(@RequestBody User user, HttpServletRequest request)
    {
//        String accessKey = request.getHeader("accessKey");
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = request.getHeader("body");
//        //todo:这里实际上应该需要先去数据库中查询一下是否已分配给该用户
//        if(!accessKey.equals("vampon") )
//        {
//            throw new RuntimeException("无权限");
//        }
//        //todo:这里可以使用redis来存储随机数
//        if(Long.parseLong(nonce) > 10000)
//        {
//            throw new RuntimeException("无权限");
//        }
//        //todo:这里需要校验一下时间戳是否相距合理，和当前时间不能超过5分钟
//        //if(timestamp)
//
//        //构造和客户端相同的hashMap
//        Map<String, String> hashMap = new HashMap<>();
//        hashMap.put("accessKey",accessKey);
//        hashMap.put("nonce",nonce);
//        hashMap.put("timestamp",timestamp);
//        hashMap.put("body",body);
//        //todo:实际情况中从数据库中查出secretKey
//        String serverSign = SignUtils.getSign(hashMap, "qwertyuiop");
//        if(!serverSign.equals(sign))
//        {
//            throw new RuntimeException("无权限");
//        }
        String result = "POST 返回用户名字" + user.getUserName();
        // 调用成功后，次数+1

        return ResultUtils.success(result);
    }

}
