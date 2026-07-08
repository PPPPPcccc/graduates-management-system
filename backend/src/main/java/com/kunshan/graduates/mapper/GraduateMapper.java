// mapper/GraduateMapper.java
package com.kunshan.graduates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import com.kunshan.graduates.entity.Graduate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GraduateMapper extends BaseMapper<Graduate> {

    /**
     * 下拉字典:学历(去重)
     */
    @Select("SELECT DISTINCT education AS value FROM graduate WHERE education IS NOT NULL ORDER BY education")
    List<String> selectDistinctEducation();

    @Select("SELECT DISTINCT graduation_date AS value FROM graduate WHERE graduation_date IS NOT NULL ORDER BY graduation_date")
    List<String> selectDistinctGraduationDate();

    @Select("SELECT DISTINCT investigator AS value FROM graduate WHERE investigator IS NOT NULL ORDER BY investigator")
    List<String> selectDistinctInvestigator();

    @Select("SELECT DISTINCT school AS value FROM graduate WHERE school IS NOT NULL ORDER BY school")
    List<String> selectDistinctSchool();

    @Select("SELECT DISTINCT investigation_date AS value FROM graduate WHERE investigation_date IS NOT NULL ORDER BY investigation_date")
    List<String> selectDistinctInvestigationDate();

    @Select("SELECT DISTINCT investigation_method AS value FROM graduate WHERE investigation_method IS NOT NULL ORDER BY investigation_method")
    List<String> selectDistinctInvestigationMethod();

    int insertGraduate(Graduate g);

    int deleteById(Long id);

    /**
     * 分页 + 多条件筛选(MyBatis-Plus 动态 SQL)
     */
    IPage<Graduate> selectGraduatePage(IPage<Graduate> page,
                                       @Param("name") String name,
                                       @Param("idCard") String idCard,
                                       @Param("education") String education,
                                       @Param("school") String school,
                                       @Param("graduationDate") String graduationDate,
                                       @Param("major") String major,
                                       @Param("phone") String phone,
                                       @Param("householdAddress") String householdAddress,
                                       @Param("investigator") String investigator,
                                       @Param("investigationDate") String investigationDate,
                                       @Param("investigationMethod") String investigationMethod,
                                       @Param("employmentStatus") String employmentStatus,
                                       @Param("employmentType") String employmentType,
                                       @Param("otherEmployment") String otherEmployment,
                                       @Param("specialEmployment") String specialEmployment,
                                       @Param("otherSituation") String otherSituation);
}