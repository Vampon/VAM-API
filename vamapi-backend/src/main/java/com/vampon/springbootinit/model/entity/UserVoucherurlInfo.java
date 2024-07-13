package com.vampon.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户凭证下载表
 * @TableName user_voucherurl_info
 */
@TableName(value ="user_voucherurl_info")
@Data
public class UserVoucherurlInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 凭证下载链接
     */
    private String voucherUrl;

    /**
     * 凭证下载状态（0-未下载，1-已下载）
     */
    private Integer downloadStatus;

    /**
     * 下载时间
     */
    private Date downloadTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}