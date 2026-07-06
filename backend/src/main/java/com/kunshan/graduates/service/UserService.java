package com.kunshan.graduates.service;

import com.kunshan.graduates.entity.User;

public interface UserService {
    /** 根据用户名查用户 */
    User findByUsername(String username);

    /** 校验密码是否正确 */
    boolean checkPassword(String rawPassword, String hashedPassword);
}