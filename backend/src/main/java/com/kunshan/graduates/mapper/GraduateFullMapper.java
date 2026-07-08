// mapper/GraduateFullMapper.java
// 查询毕业生及其关联附表数据（支持单条和批量）
package com.kunshan.graduates.mapper;

import com.kunshan.graduates.entity.AuxItem;
import com.kunshan.graduates.entity.Graduate;
import com.kunshan.graduates.handler.StringListTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GraduateFullMapper {

    /**
     * 单条查询：JOIN 三张关联表，用 GROUP_CONCAT 拼成逗号分隔字符串
     */
    @Select("""
        SELECT g.*,
               GROUP_CONCAT(DISTINCT d.demand)         AS _demands,
               GROUP_CONCAT(DISTINCT s.service)         AS _services,
               GROUP_CONCAT(DISTINCT t.service_date)    AS _service_times
          FROM graduate g
     LEFT JOIN employment_service_demand    d ON d.graduate_id = g.id
     LEFT JOIN accepted_employment_service  s ON s.graduate_id = g.id
     LEFT JOIN service_time                t ON t.graduate_id = g.id
         WHERE g.id = #{id}
      GROUP BY g.id
        """)
    @Results({
        @Result(property = "employmentServiceNeeds", column = "_demands",
                typeHandler = StringListTypeHandler.class),
        @Result(property = "receivedServices",      column = "_services",
                typeHandler = StringListTypeHandler.class),
        @Result(property = "serviceTime",          column = "_service_times",
                typeHandler = StringListTypeHandler.class)
    })
    Graduate selectByIdWithAux(@Param("id") Integer id);

    /**
     * 批量查询就业服务需求（按 graduate_id 分组）
     */
    @Select("SELECT graduate_id AS graduateId, demand AS value FROM employment_service_demand WHERE graduate_id IN (${ids})")
    List<AuxItem> selectDemandsByGraduateIds(@Param("ids") String ids);

    /**
     * 批量查询已接受就业服务
     */
    @Select("SELECT graduate_id AS graduateId, service AS value FROM accepted_employment_service WHERE graduate_id IN (${ids})")
    List<AuxItem> selectServicesByGraduateIds(@Param("ids") String ids);

    /**
     * 批量查询服务时间
     */
    @Select("SELECT graduate_id AS graduateId, service_date AS value FROM service_time WHERE graduate_id IN (${ids})")
    List<AuxItem> selectServiceTimesByGraduateIds(@Param("ids") String ids);
}
