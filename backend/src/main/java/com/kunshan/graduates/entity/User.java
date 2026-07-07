package com.kunshan.graduates.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表 user
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    /** 明文密码(仅管理员后台展示用,勿用于登录校验) */
    private String plainPassword;

    private String name;

    private String role;

    private Boolean enabled;

    private LocalDateTime createdAt;
}