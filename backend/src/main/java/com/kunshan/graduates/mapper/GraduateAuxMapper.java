// mapper/GraduateAuxMapper.java
// 用于批量写入时同步维护 graduate 的关联附表
package com.kunshan.graduates.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GraduateAuxMapper {

    /**
     * 写入已接受就业服务
     * graduateId: graduate 主键
     * service:    服务名称，如 "职业指导"
     */
    @Insert("INSERT OR IGNORE INTO accepted_employment_service(graduate_id, service) VALUES(#{graduateId}, #{service})")
    int insertAcceptedService(@Param("graduateId") Integer graduateId,
                               @Param("service")    String service);

    /**
     * 写入就业服务需求
     */
    @Insert("INSERT OR IGNORE INTO employment_service_demand(graduate_id, demand) VALUES(#{graduateId}, #{demand})")
    int insertServiceDemand(@Param("graduateId") Integer graduateId,
                            @Param("demand")    String demand);

    /**
     * 写入服务时间
     */
    @Insert("INSERT INTO service_time(graduate_id, service_date) VALUES(#{graduateId}, #{serviceDate})")
    int insertServiceTime(@Param("graduateId") Integer graduateId,
                          @Param("serviceDate") String serviceDate);

    /**
     * 插入前先删除该 graduate 的旧记录（批量重写场景）
     */
    @Delete("DELETE FROM accepted_employment_service WHERE graduate_id = #{graduateId}")
    int deleteAcceptedServices(@Param("graduateId") Integer graduateId);

    @Delete("DELETE FROM employment_service_demand WHERE graduate_id = #{graduateId}")
    int deleteServiceDemands(@Param("graduateId") Integer graduateId);

    @Delete("DELETE FROM service_time WHERE graduate_id = #{graduateId}")
    int deleteServiceTimes(@Param("graduateId") Integer graduateId);

    /** 统计就业服务需求（按 demand 分组计数） */
    @Select("SELECT demand AS label, COUNT(*) AS value FROM employment_service_demand GROUP BY demand ORDER BY value DESC")
    List<Map<String, Object>> countByServiceDemand();

    /** 统计已接受就业服务（按 service 分组计数） */
    @Select("SELECT service AS label, COUNT(*) AS value FROM accepted_employment_service GROUP BY service ORDER BY value DESC")
    List<Map<String, Object>> countByAcceptedService();

    /** 统计服务时间按月分布（提取 X月XX日 的月份） */
    @Select("""
        SELECT SUBSTR(service_date, 1, INSTR(service_date, '月') - 1) AS label,
               COUNT(*) AS value
          FROM service_time
         WHERE service_date LIKE '%月%日'
         GROUP BY SUBSTR(service_date, 1, INSTR(service_date, '月') - 1)
         ORDER BY CAST(label AS INTEGER)
        """)
    List<Map<String, Object>> countByServiceTimeMonth();
}
