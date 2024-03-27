package com.vampon.vamapiinterface.controller;

import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ResultUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@RestController
public class RandomPasswordController {

    // 生成随机密码
    @PostMapping("/random_password")
    public BaseResponse<String> getRandomPassword(@RequestParam Integer length) {
        // 定义密码字符集
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        // 初始化随机数生成器
        SecureRandom random = new SecureRandom();

        // 生成随机密码
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charset.length());
            password.append(charset.charAt(randomIndex));
        }

        String result = "随机密码: " + password.toString();
        return ResultUtils.success(result);
    }
}
