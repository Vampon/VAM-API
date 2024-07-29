package com.vampon.springbootinit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserStatistics {
    private Date createDate;
    private Integer userCount;
}
