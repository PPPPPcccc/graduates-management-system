package com.kunshan.graduates.service.impl;

import com.kunshan.graduates.entity.GraduaInfo;
import com.kunshan.graduates.mapper.GraduateInfoMapper;
import com.kunshan.graduates.service.GraduateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraduateInfoServiceImpl implements GraduateInfoService {

    @Autowired
    private GraduateInfoMapper mapper;

    @Override
    public boolean save(GraduaInfo entity) {
        return mapper.insert(entity) > 0;
    }

    @Override
    public boolean deleteByIdCard(String idCard) {
        return mapper.deleteById(idCard) > 0;
    }

    @Override
    public boolean updateByIdCard(GraduaInfo entity) {
        return mapper.updateById(entity) > 0;
    }

    @Override
    public GraduaInfo getByIdCard(String idCard) {
        return mapper.selectById(idCard);
    }

    @Override
    public List<GraduaInfo> listAll() {
        return mapper.selectList(null);
    }

    @Override
    public long count() {
        return mapper.selectCount(null);
    }
}