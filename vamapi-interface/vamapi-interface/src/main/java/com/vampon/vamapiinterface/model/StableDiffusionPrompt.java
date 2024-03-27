package com.vampon.vamapiinterface.model;

import lombok.Data;

@Data
public class StableDiffusionPrompt {
    private String positive_prompt;
    private String negative_prompt;
}
