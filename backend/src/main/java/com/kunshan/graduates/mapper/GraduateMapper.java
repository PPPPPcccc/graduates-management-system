// mapper/GraduateMapper.java
package com.kunshan.graduates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import com.kunshan.graduates.entity.Graduate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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

    /** 统计：总人数 */
    @Select("SELECT COUNT(*) FROM graduate")
    long countTotal();

    /** 统计：就业情况分布 */
    @Select("SELECT COALESCE(employment_status, '未知') AS label, COUNT(*) AS value FROM graduate GROUP BY employment_status")
    List<Map<String, Object>> countByEmploymentStatus();

    /** 统计：学历分布 */
    @Select("SELECT COALESCE(education, '未知') AS label, COUNT(*) AS value FROM graduate GROUP BY education ORDER BY value DESC")
    List<Map<String, Object>> countByEducation();

    /** 统计：就业类型分布（仅已就业） */
    @Select("SELECT COALESCE(employment_type, '未知') AS label, COUNT(*) AS value FROM graduate WHERE employment_status = '已就业' GROUP BY employment_type")
    List<Map<String, Object>> countByEmploymentType();

    /** 统计：专业是否对口（仅单位就业） */
    @Select("SELECT COALESCE(major_matched, '未知') AS label, COUNT(*) AS value FROM graduate WHERE employment_status = '已就业' AND employment_type = '单位就业' GROUP BY major_matched")
    List<Map<String, Object>> countByMajorMatched();

    /** 统计：就业意愿分布（仅未就业） */
    @Select("SELECT COALESCE(employment_willingness, '未知') AS label, COUNT(*) AS value FROM graduate WHERE employment_status = '未就业' GROUP BY employment_willingness")
    List<Map<String, Object>> countByEmploymentWillingness();

    /** 统计：学历 × 就业情况 */
    @Select("SELECT COALESCE(education, '未知') AS edu, COALESCE(employment_status, '未知') AS emp, COUNT(*) AS count FROM graduate GROUP BY education, employment_status")
    List<Map<String, Object>> countByEducationAndEmployment();

    /** 统计：调查方式分布 */
    @Select("SELECT COALESCE(investigation_method, '未知') AS label, COUNT(*) AS value FROM graduate GROUP BY investigation_method")
    List<Map<String, Object>> countByInvestigationMethod();

    /** 统计：是否提供1151服务（仅未就业） */
    @Select("SELECT COALESCE(provide_1151_service, '未知') AS label, COUNT(*) AS value FROM graduate WHERE employment_status = '未就业' GROUP BY provide_1151_service")
    List<Map<String, Object>> countByProvide1151Service();

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