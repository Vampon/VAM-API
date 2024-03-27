package com.vampon.springbootinit.service;

import com.vampon.springbootinit.service.UserInterfaceInfoService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
@SpringBootTest
public class UserInterfaceInfoServiceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void invokeCount() {
       boolean res = userInterfaceInfoService.invokeCount(1L,1L);
        System.out.println(res);
    }
}