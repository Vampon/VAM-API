package com.vampon.vamapiinterface.model;

import lombok.Data;

@Data
public class StableDiffusionPrompt {

    private String positive_prompt;
    /**
     * 生成风格
     */
    private int style;

    private String negative_prompt;
}
