// service/GraduateService.java
package com.kunshan.graduates.service;

import com.kunshan.graduates.entity.Graduate;
import java.util.Map;

public interface GraduateService {
    Map<String, Object> pageQuery(Map<String, Object> params);
    Map<String, Object> getFilterOptions();
    Graduate getById(Long id);
    boolean update(Long id, Graduate g, String updatedBy);
}