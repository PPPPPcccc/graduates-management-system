package com.kunshan.graduates.service;

import com.kunshan.graduates.entity.GraduaInfo;

import java.util.List;

public interface GraduateInfoService {

    /** 新增一条毕业生信息 */
    boolean save(GraduaInfo entity);

    /** 按身份证号删除 */
    boolean deleteByIdCard(String idCard);

    /** 更新毕业生信息 */
    boolean updateByIdCard(GraduaInfo entity);

    /** 按身份证号查询 */
    GraduaInfo getByIdCard(String idCard);

    /** 查询所有 */
    List<GraduaInfo> listAll();

    /** 查询总数 */
    long count();
}