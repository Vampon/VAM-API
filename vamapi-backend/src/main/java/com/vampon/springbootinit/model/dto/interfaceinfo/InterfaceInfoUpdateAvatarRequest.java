package com.vampon.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

/**
 *
 */
@Data
public class InterfaceInfoUpdateAvatarRequest {
    private static final long serialVersionUID = 1L;
    private long id;
    /**
     * 接口头像
     */
    private String avatarUrl;
}
