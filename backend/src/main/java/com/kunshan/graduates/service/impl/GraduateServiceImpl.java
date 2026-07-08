// service/impl/GraduateServiceImpl.java
package com.kunshan.graduates.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunshan.graduates.entity.AuxItem;
import com.kunshan.graduates.entity.Graduate;
import com.kunshan.graduates.mapper.GraduateAuxMapper;
import com.kunshan.graduates.mapper.GraduateFullMapper;
import com.kunshan.graduates.mapper.GraduateMapper;
import com.kunshan.graduates.service.GraduateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GraduateServiceImpl implements GraduateService {

    @Autowired private GraduateMapper graduateMapper;
    @Autowired private GraduateAuxMapper graduateAuxMapper;
    @Autowired private GraduateFullMapper graduateFullMapper;

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

        // 补全关联表数据
        fillAuxData(result.getRecords());

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
        return graduateFullMapper.selectByIdWithAux(id.intValue());
    }

    @Override
    public boolean add(Graduate g, String createdBy) {
        g.setCreatedBy(createdBy);
        deriveEmploymentFields(g);
        boolean ok = graduateMapper.insertGraduate(g) > 0;
        if (ok) {
            syncAuxFromEntity(g.getId(), g);
        }
        return ok;
    }

    @Override
    public boolean update(Long id, Graduate g, String updatedBy) {
        g.setId(id.intValue());
        deriveEmploymentFields(g);
        boolean ok = graduateMapper.updateById(g) >= 0;
        if (ok) {
            // 先删后插，保持关联数据同步
            graduateAuxMapper.deleteServiceDemands(id.intValue());
            graduateAuxMapper.deleteAcceptedServices(id.intValue());
            graduateAuxMapper.deleteServiceTimes(id.intValue());
            syncAuxFromEntity(id.intValue(), g);
        }
        return ok;
    }

    /**
     * 从 Graduate 实体（含 List 字段）写入关联表
     */
    private void syncAuxFromEntity(Integer graduateId, Graduate g) {
        if (g.getEmploymentServiceNeeds() != null) {
            for (String d : g.getEmploymentServiceNeeds()) {
                if (d != null && !d.trim().isEmpty()) {
                    graduateAuxMapper.insertServiceDemand(graduateId, d.trim());
                }
            }
        }
        if (g.getReceivedServices() != null) {
            for (String s : g.getReceivedServices()) {
                if (s != null && !s.trim().isEmpty()) {
                    graduateAuxMapper.insertAcceptedService(graduateId, s.trim());
                }
            }
        }
        if (g.getServiceTime() != null) {
            for (String t : g.getServiceTime()) {
                if (t != null && !t.trim().isEmpty()) {
                    graduateAuxMapper.insertServiceTime(graduateId, t.trim());
                }
            }
        }
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
                Integer gradId = g.getId();

                // 同步写入关联表
                syncAuxTables(gradId, row);

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

    /**
     * 将就业服务需求、已接受就业服务、服务时间写入关联表
     * 注意：key 必须与前端 HEADERS 数组中的列名保持一致
     */
    private void syncAuxTables(Integer graduateId, Map<String, String> row) {
        // 就业服务需求（可多选，逗号分隔）
        String demandStr = row.get("就业服务需求(可多选)");
        if (demandStr != null && !demandStr.trim().isEmpty()) {
            for (String d : demandStr.split(",")) {
                d = d.trim();
                if (!d.isEmpty()) {
                    graduateAuxMapper.insertServiceDemand(graduateId, d);
                }
            }
        }

        // 已接受就业服务（可多选，逗号分隔）
        String receivedStr = row.get("已接受就业服务(可多选)");
        if (receivedStr != null && !receivedStr.trim().isEmpty()) {
            for (String s : receivedStr.split(",")) {
                s = s.trim();
                if (!s.isEmpty()) {
                    graduateAuxMapper.insertAcceptedService(graduateId, s);
                }
            }
        }

        // 服务时间（可多选，逗号分隔）
        String timeStr = row.get("服务时间");
        if (timeStr != null && !timeStr.trim().isEmpty()) {
            for (String t : timeStr.split(",")) {
                t = t.trim();
                if (!t.isEmpty()) {
                    graduateAuxMapper.insertServiceTime(graduateId, t);
                }
            }
        }
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
        int gid = id.intValue();
        graduateAuxMapper.deleteServiceDemands(gid);
        graduateAuxMapper.deleteAcceptedServices(gid);
        graduateAuxMapper.deleteServiceTimes(gid);
        return graduateMapper.deleteById(id) > 0;
    }

    /**
     * 批量填充关联表数据到 graduate 列表
     */
    private void fillAuxData(List<Graduate> records) {
        if (records == null || records.isEmpty()) return;

        List<Integer> ids = records.stream().map(Graduate::getId).collect(Collectors.toList());
        String idsStr = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

        Map<Integer, List<String>> demandsMap  = auxToMap(graduateFullMapper.selectDemandsByGraduateIds(idsStr));
        Map<Integer, List<String>> servicesMap = auxToMap(graduateFullMapper.selectServicesByGraduateIds(idsStr));
        Map<Integer, List<String>> timesMap    = auxToMap(graduateFullMapper.selectServiceTimesByGraduateIds(idsStr));

        for (Graduate g : records) {
            g.setEmploymentServiceNeeds(demandsMap.get(g.getId()));
            g.setReceivedServices(servicesMap.get(g.getId()));
            g.setServiceTime(timesMap.get(g.getId()));
        }
    }

    private Map<Integer, List<String>> auxToMap(List<AuxItem> items) {
        Map<Integer, List<String>> map = new HashMap<>();
        if (items == null) return map;
        for (AuxItem item : items) {
            map.computeIfAbsent(item.getGraduateId(), k -> new ArrayList<>()).add(item.getValue());
        }
        return map;
    }
}