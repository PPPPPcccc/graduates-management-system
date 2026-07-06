package com.kunshan.graduates.controller;

import com.kunshan.graduates.entity.User;
import com.kunshan.graduates.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口（登录/退出/查当前用户）
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     * POST /api/auth/login
     * body: { "username": "admin", "password": "admin123" }
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            result.put("success", false);
            result.put("message", "账号或密码不能为空");
            return result;
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            result.put("success", false);
            result.put("message", "账号不存在");
            return result;
        }

        if (user.getEnabled() != null && !user.getEnabled()) {
            result.put("success", false);
            result.put("message", "账号已被禁用");
            return result;
        }

        if (!userService.checkPassword(password, user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }

        // 登录成功，把用户信息存进 session
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("name", user.getName());
        session.setAttribute("role", user.getRole());

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("data", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "name", user.getName(),
                "role", user.getRole()
        ));
        return result;
    }

    /**
     * 退出登录
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "已退出");
        return result;
    }

    /**
     * 获取当前登录用户信息
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public Map<String, Object> me(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Object username = session.getAttribute("username");
        if (username == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }
        result.put("success", true);
        result.put("data", Map.of(
                "id", session.getAttribute("userId"),
                "username", username,
                "name", session.getAttribute("name"),
                "role", session.getAttribute("role")
        ));
        return result;
    }
}