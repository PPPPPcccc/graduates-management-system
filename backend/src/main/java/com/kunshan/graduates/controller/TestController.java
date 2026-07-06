package com.kunshan.graduates.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 用来验证数据库能不能连通的测试接口
 * 访问 http://localhost:8080/api/test/db 看到「数据库连接成功」就算成功
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db")
    public String testDb() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 执行一句最简单的 SQL：查 SQLite 的版本号
            ResultSet rs = stmt.executeQuery("SELECT sqlite_version() AS version");

            if (rs.next()) {
                String version = rs.getString("version");
                return "数据库连接成功！SQLite 版本：" + version;
            }
            return "数据库连接失败：未获取到版本号";

        } catch (Exception e) {
            return "数据库连接失败：" + e.getMessage();
        }
    }
}