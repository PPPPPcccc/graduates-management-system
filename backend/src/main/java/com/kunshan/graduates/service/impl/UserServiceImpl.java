package com.kunshan.graduates.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kunshan.graduates.entity.User;
import com.kunshan.graduates.mapper.UserMapper;
import com.kunshan.graduates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }

    /**
     * 把明文密码加密成 BCrypt 哈希值（启动时调用一次，给默认账号用）
     */
    public void encodeAndUpdatePassword(String username, String rawPassword) {
        User user = findByUsername(username);
        if (user != null) {
            user.setPassword(encoder.encode(rawPassword));
            userMapper.updateById(user);
        }
    }
}