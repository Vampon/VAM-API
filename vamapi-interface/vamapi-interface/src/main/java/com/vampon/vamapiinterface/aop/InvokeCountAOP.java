package com.vampon.vamapiinterface.aop;


import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 调用次数切面
 */
@RestControllerAdvice
public class InvokeCountAOP {
    // 可以通过AOP切面的方法，对提供接口进行调用次数统计
    // 然而，AOP切面是只存在于单个项目中的，如果接口是多个项目开发的，那么每个项目依旧要引入AOP包，以及开发AOP的类
}
