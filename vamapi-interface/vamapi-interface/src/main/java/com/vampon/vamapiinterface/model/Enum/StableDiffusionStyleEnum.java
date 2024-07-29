package com.vampon.vamapiinterface.model.Enum;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文生图风格枚举
 *
 * @author vampon
 * 
 */
public enum StableDiffusionStyleEnum {

    BASE("Base", 0),
    ANIME("Anime", 1),
    COMIC_BOOK("Comic Book", 2),
    PIXEL("Pixel Art", 3),
    CINEMATIC("Cinematic", 4),
    MODEL_3D("3D Model", 5),
    FANTASY_ART("Fantasy Art", 6);

    private final String text;

    private final Integer value;

    StableDiffusionStyleEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static StableDiffusionStyleEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (StableDiffusionStyleEnum anEnum : StableDiffusionStyleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
