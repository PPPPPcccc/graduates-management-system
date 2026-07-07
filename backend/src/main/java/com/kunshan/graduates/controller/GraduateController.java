// controller/GraduateController.java
package com.kunshan.graduates.controller;

import com.kunshan.graduates.entity.Graduate;
import com.kunshan.graduates.service.GraduateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/graduate")
public class GraduateController {

    @Autowired private GraduateService graduateService;

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam Map<String, Object> params,
                                    HttpSession session) {
        Map<String, Object> out = new HashMap<>();
        try {
            Map<String, Object> data = graduateService.pageQuery(params);
            out.put("success", true);
            out.put("data", data);
        } catch (Exception e) {
            out.put("success", false);
            out.put("message", e.getMessage());
        }
        return out;
    }

    @GetMapping("/filter-options")
    public Map<String, Object> filterOptions() {
        Map<String, Object> out = new HashMap<>();
        out.put("success", true);
        out.put("data", graduateService.getFilterOptions());
        return out;
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        Map<String, Object> out = new HashMap<>();
        Graduate g = graduateService.getById(id);
        if (g != null) {
            out.put("success", true);
            out.put("data", g);
        } else {
            out.put("success", false);
            out.put("message", "记录不存在");
        }
        return out;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Graduate g,
                                       HttpSession session) {
        Map<String, Object> out = new HashMap<>();
        String updatedBy = (String) session.getAttribute("username");
        g.setUpdatedBy(updatedBy);
        boolean ok = graduateService.update(id, g, updatedBy);
        out.put("success", ok);
        out.put("message", ok ? "保存成功" : "保存失败");
        return out;
    }
}