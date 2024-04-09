package com.vampon.vamapiinterface.controller;

import com.vampon.vamapiclientsdk.model.User;
import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ResultUtils;
import com.vampon.vamapiinterface.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Random;


/**
 * 查询名称API
 * @author vampon
 */
@RestController
//@RequestMapping
public class RandomNumberController {



    @GetMapping("/random_number")
    public BaseResponse<String> getRandomNumberByPost(@RequestParam Integer range)
    {
        Random random = new Random();
        int rtnNum = random.nextInt(range) + 1;
        String result = "POST 返回随机数字" + Integer.toString(rtnNum);
        return ResultUtils.success(result);
    }

}
