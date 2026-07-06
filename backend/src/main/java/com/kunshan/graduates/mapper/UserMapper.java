package com.kunshan.graduates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunshan.graduates.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 空接口，MyBatis-Plus 自动实现
}