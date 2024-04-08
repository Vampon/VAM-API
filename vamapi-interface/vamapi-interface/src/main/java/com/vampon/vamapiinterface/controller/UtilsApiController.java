package com.vampon.vamapiinterface.controller;

import com.vampon.vamapicommon.common.BaseResponse;
import com.vampon.vamapicommon.common.ResultUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class UtilsApiController {
    @PostMapping("/generate_crontab")
    public BaseResponse<String> generateCrontab(
            @RequestParam(required = false) String minutes,
            @RequestParam(required = false) String hours,
            @RequestParam(required = false) String dayOfMonth,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String dayOfWeek) {

        // 构造Crontab表达式
        StringBuilder crontabExpressionBuilder = new StringBuilder();

        appendIfNotNull(crontabExpressionBuilder, minutes, "0");
        appendIfNotNull(crontabExpressionBuilder, hours, "0");
        appendIfNotNull(crontabExpressionBuilder, dayOfMonth, "1");
        appendIfNotNull(crontabExpressionBuilder, month, "*");
        appendIfNotNull(crontabExpressionBuilder, dayOfWeek, "*");

        String crontabExpression = crontabExpressionBuilder.toString();
        return ResultUtils.success(crontabExpression);
    }

    private void appendIfNotNull(StringBuilder builder, String param, String defaultValue) {
        if (param != null && !param.isEmpty()) {
            builder.append(param);
        } else {
            builder.append(defaultValue);
        }
        builder.append(" ");
    }
}
