// controller/AccountController.java
package com.kunshan.graduates.controller;

import com.kunshan.graduates.entity.AccountWithPlain;
import com.kunshan.graduates.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired private UserService userService;

    @GetMapping("/list")
    public Map<String, Object> list(HttpSession session) {
        Map<String, Object> out = new HashMap<>();
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            out.put("success", false);
            out.put("message", "无权限");
            return out;
        }
        out.put("success", true);
        out.put("data", userService.listAllWithPlainPassword());
        return out;
    }

    @GetMapping("/{id}/plain")
    public Map<String, Object> plain(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> out = new HashMap<>();
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            out.put("success", false);
            out.put("message", "无权限");
            return out;
        }
        AccountWithPlain a = userService.getPlainById(id);
        if (a != null) {
            out.put("success", true);
            out.put("data", Map.of("password", a.getPassword()));
        } else {
            out.put("success", false);
            out.put("message", "账号不存在");
        }
        return out;
    }
}