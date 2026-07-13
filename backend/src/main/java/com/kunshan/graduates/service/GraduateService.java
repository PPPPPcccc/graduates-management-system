// service/GraduateService.java
package com.kunshan.graduates.service;

import com.kunshan.graduates.entity.Graduate;
import java.util.List;
import java.util.Map;

public interface GraduateService {
    Map<String, Object> pageQuery(Map<String, Object> params);
    Map<String, Object> getFilterOptions();
    Map<String, Object> getStatistics();
    Graduate getById(Long id);
    boolean update(Long id, Graduate g, String updatedBy);
    boolean add(Graduate g, String createdBy);
    Map<String, Object> batchAdd(List<Map<String, String>> rows, String createdBy);
    boolean delete(Long id);
    int batchDelete(List<Long> ids);
}