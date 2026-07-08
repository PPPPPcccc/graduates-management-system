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
        deriveEmploymentFields(g);
        return graduateMapper.updateById(g) >= 0;
    }

    @Override
    public boolean add(Graduate g, String createdBy) {
        g.setCreatedBy(createdBy);
        deriveEmploymentFields(g);
        return graduateMapper.insertGraduate(g) > 0;
    }

    /**
     * 根据领导填写的实际字段，自动推导 employmentStatus 和 employmentType。
     * 规则（优先级递减）：
     *   1. 填写了"就业地/单位名称/单位性质/专业是否对口" → 已就业 + 单位就业
     *   2. 填写了"其他就业"                    → 已就业 + 其他就业
     *   3. 填写了"特殊就业"                    → 已就业 + 特殊就业
     *   4. 填写了"其他情况"                    → 已就业 + 其他情况
     *   5. 填写了"未就业原因/有无就业意愿/是否提供1151服务/就业服务需求" → 未就业
     */
    private void deriveEmploymentFields(Graduate g) {
        boolean hasUnit = hasValue(g.getEmploymentLocation())
                        || hasValue(g.getUnitName())
                        || hasValue(g.getUnitNature())
                        || hasValue(g.getMajorMatched());

        boolean hasOtherEmp    = hasValue(g.getOtherEmployment());
        boolean hasSpecialEmp  = hasValue(g.getSpecialEmployment());
        boolean hasOtherSit    = hasValue(g.getOtherSituation());

        boolean hasUnemployed  = hasValue(g.getUnemployedReason())
                               || hasValue(g.getEmploymentWillingness())
                               || hasValue(g.getProvide1151Service())
                               || hasValue(g.getRecommendUnitPosition());

        if (hasUnit) {
            g.setEmploymentStatus("已就业");
            g.setEmploymentType("单位就业");
        } else if (hasOtherEmp) {
            g.setEmploymentStatus("已就业");
            g.setEmploymentType("其他就业");
        } else if (hasSpecialEmp) {
            g.setEmploymentStatus("已就业");
            g.setEmploymentType("特殊就业");
        } else if (hasOtherSit) {
            g.setEmploymentStatus("已就业");
            g.setEmploymentType("其他情况");
        } else if (hasUnemployed) {
            g.setEmploymentStatus("未就业");
            g.setEmploymentType(null);
        }
    }

    private boolean hasValue(String v) {
        return v != null && !v.trim().isEmpty();
    }

    @Override
    public Map<String, Object> batchAdd(List<Map<String, String>> rows, String createdBy) {
        List<Map<String, String>> failed = new ArrayList<>();
        int successCount = 0;
        for (Map<String, String> row : rows) {
            try {
                Graduate g = mapToGraduate(row);
                g.setCreatedBy(createdBy);
                graduateMapper.insertGraduate(g);
                successCount++;
            } catch (Exception e) {
                Map<String, String> err = new HashMap<>();
                err.put("name", row.getOrDefault("姓名", "-"));
                err.put("idCard", row.getOrDefault("身份证号码", "-"));
                err.put("reason", e.getMessage() != null ? e.getMessage() : "数据格式错误或必填字段缺失");
                failed.add(err);
            }
        }
        Map<String, Object> out = new HashMap<>();
        out.put("successCount", successCount);
        out.put("failedCount", failed.size());
        out.put("failed", failed);
        return out;
    }

    private Graduate mapToGraduate(Map<String, String> row) {
        Graduate g = new Graduate();
        g.setName(row.get("姓名"));
        g.setIdCard(row.get("身份证号码"));
        g.setEducation(row.get("学历"));
        g.setSchool(row.get("毕业院校"));
        g.setGraduationDate(row.get("毕业日期"));
        g.setMajor(row.get("专业"));
        g.setPhone(row.get("联系电话"));
        g.setHouseholdAddress(row.get("户籍地址"));
        g.setResidenceAddress(row.get("常住详细地址"));
        g.setInvestigator(row.get("调查人"));
        g.setInvestigationDate(row.get("调查日期"));
        g.setInvestigationMethod(row.get("调查方式"));
        g.setEmploymentLocation(row.get("就业地"));
        g.setUnitName(row.get("单位名称"));
        g.setUnitNature(row.get("单位性质"));
        g.setMajorMatched(row.get("专业是否对口"));
        g.setOtherEmployment(row.get("其他就业"));
        g.setSpecialEmployment(row.get("特殊就业"));
        g.setOtherSituation(row.get("其他情况"));
        g.setUnemployedReason(row.get("未就业原因"));
        g.setEmploymentWillingness(row.get("有无就业意愿"));
        g.setProvide1151Service(row.get("是否提供\"1151\"服务"));
        g.setRecommendUnitPosition(row.get("推荐单位及岗位名称"));
        g.setRemarks(row.get("备注"));
        if (g.getName() == null || g.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("姓名为必填项");
        }
        deriveEmploymentFields(g);
        return g;
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

    @Override
    public boolean delete(Long id) {
        return graduateMapper.deleteById(id) > 0;
    }
}