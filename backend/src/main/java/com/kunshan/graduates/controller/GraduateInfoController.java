package com.kunshan.graduates.controller;

import com.kunshan.graduates.entity.GraduaInfo;
import com.kunshan.graduates.service.GraduateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 毕业生信息 HTTP 接口
 *
 * 5 个接口：
 *   POST   /api/graduate           新增
 *   GET    /api/graduate           查询所有
 *   GET    /api/graduate/{idCard}  按身份证号查
 *   PUT    /api/graduate/{idCard}  更新
 *   DELETE /api/graduate/{idCard}  删除
 */
@RestController
@RequestMapping("/api/graduate")
public class GraduateInfoController {

    @Autowired
    private GraduateInfoService service;

    /** 新增 */
    @PostMapping
    public Map<String, Object> save(@RequestBody GraduaInfo entity) {
        Map<String, Object> result = new HashMap<>();

        // 1. 简单校验：身份证号不能为空
        if (entity.getIdCard() == null || entity.getIdCard().isEmpty()) {
            result.put("success", false);
            result.put("message", "身份证号不能为空");
            return result;
        }

        // 2. 简单校验：姓名不能为空
        if (entity.getName() == null || entity.getName().isEmpty()) {
            result.put("success", false);
            result.put("message", "姓名不能为空");
            return result;
        }

        // 3. 检查是否已存在
        GraduaInfo exist = service.getByIdCard(entity.getIdCard());
        if (exist != null) {
            result.put("success", false);
            result.put("message", "该身份证号已登记：" + exist.getName());
            return result;
        }

        // 4. 插入
        boolean ok = service.save(entity);
        result.put("success", ok);
        result.put("message", ok ? "新增成功" : "新增失败");
        if (ok) {
            result.put("data", service.getByIdCard(entity.getIdCard()));
        }
        return result;
    }

    /** 查询所有 */
    @GetMapping
    public Map<String, Object> listAll() {
        Map<String, Object> result = new HashMap<>();
        List<GraduaInfo> list = service.listAll();
        result.put("success", true);
        result.put("total", list.size());
        result.put("data", list);
        return result;
    }

    /** 按身份证号查 */
    @GetMapping("/{idCard}")
    public Map<String, Object> getByIdCard(@PathVariable String idCard) {
        Map<String, Object> result = new HashMap<>();
        GraduaInfo entity = service.getByIdCard(idCard);
        if (entity == null) {
            result.put("success", false);
            result.put("message", "未找到该身份证号的记录");
        } else {
            result.put("success", true);
            result.put("data", entity);
        }
        return result;
    }

    /** 更新 */
    @PutMapping("/{idCard}")
    public Map<String, Object> update(@PathVariable String idCard, @RequestBody GraduaInfo entity) {
        Map<String, Object> result = new HashMap<>();
        entity.setIdCard(idCard); // 防止 URL 里的和 body 里的不一致
        boolean ok = service.updateByIdCard(entity);
        result.put("success", ok);
        result.put("message", ok ? "更新成功" : "更新失败或记录不存在");
        return result;
    }

    /** 删除 */
    @DeleteMapping("/{idCard}")
    public Map<String, Object> delete(@PathVariable String idCard) {
        Map<String, Object> result = new HashMap<>();
        boolean ok = service.deleteByIdCard(idCard);
        result.put("success", ok);
        result.put("message", ok ? "删除成功" : "记录不存在");
        return result;
    }

    /** 查询总数（额外提供一个） */
    @GetMapping("/count")
    public Map<String, Object> count() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("total", service.count());
        return result;
    }
}