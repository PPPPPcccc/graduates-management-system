package com.kunshan.graduates.service;

import com.kunshan.graduates.entity.AccountWithPlain;
import com.kunshan.graduates.entity.User;

import java.util.List;

public interface UserService {
    /** 根据用户名查用户 */
    User findByUsername(String username);

    /** 校验密码是否正确 */
    boolean checkPassword(String rawPassword, String hashedPassword);

    /** 查询所有账号(带明文密码,仅管理员用) */
    List<AccountWithPlain> listAllWithPlainPassword();

    /** 根据 ID 查询带明文密码的账号 */
    AccountWithPlain getPlainById(Integer id);
}