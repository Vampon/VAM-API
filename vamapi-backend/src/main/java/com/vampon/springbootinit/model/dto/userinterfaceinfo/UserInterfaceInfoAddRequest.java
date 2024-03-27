package com.vampon.springbootinit.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author vampon
 * 
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

    /**
     * 调用用户ID
     */
    private Long userId;

    /**
     * 接口ID
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;


}