package com.vampon.vamapiinterface.model.Enum;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 缓存枚举
 *
 * @author vampon
 * 
 */
public enum CacheEnum {

    WEIBO("WEIBO", 1),
    TIKTOK("TIKTOK", 1),
    EVENTONHISTORY("EVENTONHISTORY", 12),
    TODAYPROTRAIT("TODAYPROTRAIT", 12);

    private final String text;

    private final Integer value;


    CacheEnum(String text, Integer value) {
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
    public static CacheEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (CacheEnum anEnum : CacheEnum.values()) {
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
