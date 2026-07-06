package com.kunshan.graduates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunshan.graduates.entity.GraduaInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 毕业生信息数据库操作接口
 * 继承 BaseMapper 后，自动拥有 增删改查 的方法，不用写 SQL
 */
@Mapper
public interface GraduateInfoMapper extends BaseMapper<GraduaInfo> {
    // 空接口，MyBatis-Plus 会自动实现
}