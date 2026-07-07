// service/impl/GraduateServiceImpl.java
package com.kunshan.graduates.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunshan.graduates.entity.Graduate;
import com.kunshan.graduates.mapper.GraduateMapper;
import com.kunshan.graduates.service.GraduateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.*;

@Service
public class GraduateServiceImpl implements GraduateService {

    @Autowired private GraduateMapper graduateMapper;

    @Override
    public Map<String, Object> pageQuery(Map<String, Object> p) {
        long current = Long.parseLong(p.getOrDefault("page", 1).toString());
        long size = Long.parseLong(p.getOrDefault("pageSize", 10).toString());
        IPage<Graduate> page = new Page<>(current, size);

        IPage<Graduate> result = graduateMapper.selectGraduatePage(page,
                str(p.get("name")), str(p.get("idCard")), str(p.get("education")),
                str(p.get("school")), str(p.get("graduationDate")), str(p.get("major")),
                str(p.get("phone")), str(p.get("householdAddress")),
                str(p.get("investigator")), str(p.get("investigationDate")),
                str(p.get("investigationMethod")),
                str(p.get("employmentStatus")), str(p.get("employmentType")),
                str(p.get("otherEmployment")), str(p.get("specialEmployment")),
                str(p.get("otherSituation")));

        Map<String, Object> out = new HashMap<>();
        out.put("records", result.getRecords());
        out.put("total", result.getTotal());
        out.put("current", result.getCurrent());
        out.put("size", result.getSize());
        return out;
    }

    @Override
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> out = new HashMap<>();
        out.put("education",           sortByPinyin(graduateMapper.selectDistinctEducation()));
        out.put("graduationDate",      sortByPinyin(graduateMapper.selectDistinctGraduationDate()));
        out.put("investigator",        sortByPinyin(graduateMapper.selectDistinctInvestigator()));
        out.put("school",              sortByPinyin(graduateMapper.selectDistinctSchool()));
        out.put("investigationDate",   sortByPinyin(graduateMapper.selectDistinctInvestigationDate()));
        out.put("investigationMethod", sortByPinyin(graduateMapper.selectDistinctInvestigationMethod()));
        return out;
    }

    @Override
    public Graduate getById(Long id) {
        return graduateMapper.selectById(id);
    }

    @Override
    public boolean update(Long id, Graduate g, String updatedBy) {
        g.setId(id.intValue());
        return graduateMapper.updateById(g) >= 0;
    }

    /** 按中文拼音首字母排序 */
    private List<String> sortByPinyin(List<String> list) {
        if (list == null) return new ArrayList<>();
        Collator collator = Collator.getInstance(Locale.CHINA);
        List<String> sorted = new ArrayList<>(list);
        sorted.sort(collator::compare);
        return sorted;
    }

    private String str(Object o) {
        if (o == null) return null;
        String s = o.toString().trim();
        return s.isEmpty() ? null : s;
    }
}