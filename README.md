# 未就业大学生信息管理系统 - 搭建教程

> 本文档将一步一步教你搭建一个完整的前后端分离项目。
> 技术栈：Spring Boot 3 + Vue 3 + SQLite + Element Plus + ECharts
> 部署方式：本机部署（局域网访问）

---

## 目录

1. [环境准备](#一环境准备)
2. [后端项目搭建](#二后端项目搭建-spring-boot)
3. [前端项目搭建](#三前端项目搭建-vue)
4. [接口联调](#四接口联调)
5. [本机部署](#五本机部署)
6. [附录：常见问题](#六附录常见问题)

---

## 一、环境准备

### 1.1 需要安装的软件

| 软件 | 版本要求 | 下载地址 | 说明 |
|------|---------|---------|------|
| JDK | 17+ | https://adoptium.net/temurin/releases/?version=17 | Java 运行环境，必须装 |
| Maven | 3.6+ | https://maven.apache.org/download.cgi | Java 项目构建工具 |
| Node.js | 18+ LTS | https://nodejs.org/ | 前端运行环境，必须装 |
| VSCode | 最新版 | https://code.visualstudio.com/ | 前端开发编辑器 |
| IDEA | 社区版即可 | https://www.jetbrains.com/idea/download/ | 后端开发编辑器（更推荐），也可用 VSCode |

### 1.2 环境验证

打开终端（Windows 按 Win+R，输入 cmd），依次执行以下命令：

```bash
# 验证 Java
java -version

# 验证 Maven
mvn -version

# 验证 Node.js
node -v
npm -v
```

如果都能输出版本号，说明环境安装成功。

### 1.3 创建项目根目录

在你喜欢的任意位置创建一个文件夹，作为整个项目的根目录：

```
未就业大学生信息管理系统/
├── backend/          # 后端代码
└── frontend/         # 前端代码
```

---

## 二、后端项目搭建 (Spring Boot)

### 2.1 一键生成项目骨架（推荐方式）

有两种方式可以快速创建 Spring Boot 项目：

#### 方式一：网页在线生成（推荐）

1. 打开浏览器访问 **https://start.spring.io/**
2. 按以下配置填写：

| 配置项 | 值 |
|--------|-----|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 3.2.x (默认最新) |
| **Group** | com.kunshan |
| **Artifact** | graduates |
| **Package name** | com.kunshan.graduates |
| **Java version** | 17 |

3. 点击右侧「ADD DEPENDENCIES」按钮，添加依赖：
   - **Spring Web**
   - **Lombok**（简化代码）

4. 点击底部的「GENERATE」按钮，会下载一个 `graduates.zip` 文件
5. 解压后，把内容放到 `backend/` 目录下

#### 方式二：IDEA 内部直接创建（更方便）

1. 打开 IDEA
2. 选择 **File → New → Project**
3. 选择 **Spring Initializr**，点击 Next
4. 填写：
   - **Name**: graduates
   - **Group**: com.kunshan
   - **Artifact**: graduates
5. 点击 Next，选择依赖：
   - **Spring Web**
   - **Lombok**
6. 点击 Create

> 以上两种方式最终效果一样，都会自动生成标准的 Maven 项目结构，不需要手动创建任何文件夹。

### 2.2 添加必要依赖

用 Initializr 生成的项目只有基础依赖，需要手动添加几个依赖来支持 SQLite 和 MyBatis-Plus。

打开 `pom.xml`，找到 `<dependencies>` 标签，在其中添加以下依赖：

```xml
<!-- MyBatis Plus (增强 CRUD 能力) -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>

<!-- SQLite JDBC 驱动 -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.44.1.0</version>
</dependency>
```

> Lombok 已经在 Initializr 中添加过了，不需要重复添加。

### 2.3 创建文件夹（仅需这一步）

在 IDEA 中找到 `src/main/java/com/kunshan/graduates/` 包目录，右键 → New → Package，创建以下包：

```
controller
service
service.impl
mapper
entity
config
```

> 这一步只需要在 IDEA 里右键创建几个包文件夹，3分钟搞定。

### 2.4 编写配置文件 application.yml

在 `src/main/resources/` 目录下找到已有的 `application.yml`，替换为以下内容：

```yaml
server:
  port: 8080

spring:
  application:
    name: graduates
  
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./src/main/resources/graduates.db

mybatis-plus:
  type-aliases-package: com.kunshan.graduates.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

logging:
  level:
    com.kunshan.graduates: debug
```

### 2.5 主启动类

找到已有的 `GraduatesApplication.java`，修改为以下内容：

```java
package com.kunshan.graduates;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kunshan.graduates.mapper")  // 扫描 Mapper 接口
public class GraduatesApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraduatesApplication.class, args);
        System.out.println("========================================");
        System.out.println("未就业大学生信息管理系统启动成功！");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("========================================");
    }
}
```

### 2.6 实体类 (Entity)

在 `entity` 包下创建 `Graduate.java`：

```java
package com.kunshan.graduates.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("graduates")
public class Graduate {
    
    @TableId(type = IdType.INPUT)  // 手动输入主键（身份证号）
    private String idCard;         // 身份证号（主键）
    
    private String name;            // 姓名
    private String gender;          // 性别
    private String nation;          // 民族
    private String education;       // 学历（本科/专科/硕士/博士）
    private String graduateSchool;  // 毕业院校
    private String major;           // 专业
    private String graduateYear;   // 毕业年份
    private String phone;           // 联系电话
    private String householdAddress;// 户籍地址
    private String currentAddress;  // 现居地址
    private String employmentStatus;// 就业状态（未就业/已就业/就业困难）
    private String difficultyType; // 困难类型（无/技能不足/学历不足/其他）
    private String registeredDate;  // 登记日期
    private String remarks;         // 备注
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
```

### 2.7 Mapper 接口

在 `mapper` 包下创建 `GraduateMapper.java`：

```java
package com.kunshan.graduates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunshan.graduates.entity.Graduate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GraduateMapper extends BaseMapper<Graduate> {
    // 继承 BaseMapper 后，自动拥有了增删改查的基本方法
    // 如果需要自定义查询，可以在这里添加方法
}
```

### 2.8 Service 层

在 `service` 包下创建接口 `GraduateService.java`：

```java
package com.kunshan.graduates.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kunshan.graduates.entity.Graduate;

public interface GraduateService extends IService<Graduate> {
    
    // 检查身份证号是否已存在
    boolean isIdCardExist(String idCard);
}
```

创建实现类 `GraduateServiceImpl.java`：

```java
package com.kunshan.graduates.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kunshan.graduates.entity.Graduate;
import com.kunshan.graduates.mapper.GraduateMapper;
import com.kunshan.graduates.service.GraduateService;
import org.springframework.stereotype.Service;

@Service
public class GraduateServiceImpl extends ServiceImpl<GraduateMapper, Graduate> 
    implements GraduateService {
    
    @Override
    public boolean isIdCardExist(String idCard) {
        QueryWrapper<Graduate> wrapper = new QueryWrapper<>();
        wrapper.eq("id_card", idCard);
        return this.count(wrapper) > 0;
    }
}
```

### 2.9 Controller 层（核心！）

在 `controller` 包下创建 `GraduateController.java`：

```java
package com.kunshan.graduates.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunshan.graduates.entity.Graduate;
import com.kunshan.graduates.service.GraduateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graduates")
@CrossOrigin  // 允许跨域访问
public class GraduateController {
    
    @Autowired
    private GraduateService graduateService;
    
    // ========== 增 ==========
    @PostMapping
    public Map<String, Object> add(@RequestBody Graduate graduate) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查身份证号是否已存在
        if (graduateService.isIdCardExist(graduate.getIdCard())) {
            result.put("success", false);
            result.put("message", "该身份证号已存在，无法重复录入！");
            return result;
        }
        
        graduate.setCreateTime(LocalDateTime.now());
        graduate.setUpdateTime(LocalDateTime.now());
        boolean success = graduateService.save(graduate);
        
        result.put("success", success);
        result.put("message", success ? "新增成功" : "新增失败");
        return result;
    }
    
    // ========== 删 ==========
    @DeleteMapping("/{idCard}")
    public Map<String, Object> delete(@PathVariable String idCard) {
        Map<String, Object> result = new HashMap<>();
        boolean success = graduateService.removeById(idCard);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
    
    // ========== 改 ==========
    @PutMapping
    public Map<String, Object> update(@RequestBody Graduate graduate) {
        Map<String, Object> result = new HashMap<>();
        graduate.setUpdateTime(LocalDateTime.now());
        boolean success = graduateService.updateById(graduate);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }
    
    // ========== 查（列表 + 分页 + 搜索）==========
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String nation,
            @RequestParam(required = false) String employmentStatus) {
        
        Map<String, Object> result = new HashMap<>();
        
        QueryWrapper<Graduate> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");  // 按创建时间倒序
        
        // 模糊搜索
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (idCard != null && !idCard.isEmpty()) {
            wrapper.eq("id_card", idCard);
        }
        if (education != null && !education.isEmpty()) {
            wrapper.eq("education", education);
        }
        if (nation != null && !nation.isEmpty()) {
            wrapper.eq("nation", nation);
        }
        if (employmentStatus != null && !employmentStatus.isEmpty()) {
            wrapper.eq("employment_status", employmentStatus);
        }
        
        Page<Graduate> page = new Page<>(current, size);
        Page<Graduate> resultPage = graduateService.page(page, wrapper);
        
        result.put("success", true);
        result.put("data", resultPage.getRecords());
        result.put("total", resultPage.getTotal());
        result.put("pages", resultPage.getPages());
        result.put("current", resultPage.getCurrent());
        result.put("size", resultPage.getSize());
        
        return result;
    }
    
    // ========== 查（单个详情）==========
    @GetMapping("/{idCard}")
    public Map<String, Object> getById(@PathVariable String idCard) {
        Map<String, Object> result = new HashMap<>();
        Graduate graduate = graduateService.getById(idCard);
        result.put("success", graduate != null);
        result.put("data", graduate);
        return result;
    }
    
    // ========== 统计接口（给图表用）==========
    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 学历分布
        QueryWrapper<Graduate> educationWrapper = new QueryWrapper<>();
        List<Map<String, Object>> educationStats = graduateService.maps(educationWrapper.select("education, count(*) as count").groupBy("education"));
        
        // 民族分布
        QueryWrapper<Graduate> nationWrapper = new QueryWrapper<>();
        List<Map<String, Object>> nationStats = graduateService.maps(nationWrapper.select("nation, count(*) as count").groupBy("nation"));
        
        // 就业状态分布
        QueryWrapper<Graduate> statusWrapper = new QueryWrapper<>();
        List<Map<String, Object>> statusStats = graduateService.maps(statusWrapper.select("employment_status, count(*) as count").groupBy("employment_status"));
        
        // 困难类型分布
        QueryWrapper<Graduate> difficultyWrapper = new QueryWrapper<>();
        List<Map<String, Object>> difficultyStats = graduateService.maps(difficultyWrapper.select("difficulty_type, count(*) as count").groupBy("difficulty_type"));
        
        result.put("success", true);
        result.put("educationStats", educationStats);
        result.put("nationStats", nationStats);
        result.put("statusStats", statusStats);
        result.put("difficultyStats", difficultyStats);
        
        return result;
    }
    
    // ========== 导出 Excel（简化版，返回 CSV）==========
    @GetMapping("/export")
    public Map<String, Object> export() {
        Map<String, Object> result = new HashMap<>();
        List<Graduate> allGraduates = graduateService.list();
        
        // 生成 CSV 格式
        StringBuilder csv = new StringBuilder();
        csv.append("身份证号,姓名,性别,民族,学历,毕业院校,专业,毕业年份,联系电话,户籍地址,现居地址,就业状态,困难类型,登记日期,备注\n");
        
        for (Graduate g : allGraduates) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                nullSafe(g.getIdCard()),
                nullSafe(g.getName()),
                nullSafe(g.getGender()),
                nullSafe(g.getNation()),
                nullSafe(g.getEducation()),
                nullSafe(g.getGraduateSchool()),
                nullSafe(g.getMajor()),
                nullSafe(g.getGraduateYear()),
                nullSafe(g.getPhone()),
                nullSafe(g.getHouseholdAddress()),
                nullSafe(g.getCurrentAddress()),
                nullSafe(g.getEmploymentStatus()),
                nullSafe(g.getDifficultyType()),
                nullSafe(g.getRegisteredDate()),
                nullSafe(g.getRemarks())
            ));
        }
        
        result.put("success", true);
        result.put("data", csv.toString());
        result.put("total", allGraduates.size());
        
        return result;
    }
    
    // ========== 检查身份证号是否存在 ==========
    @GetMapping("/check/{idCard}")
    public Map<String, Object> checkIdCard(@PathVariable String idCard) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("exists", graduateService.isIdCardExist(idCard));
        return result;
    }
    
    // 工具方法：防止 null 导致 CSV 格式错乱
    private String nullSafe(Object obj) {
        return obj == null ? "" : obj.toString().replace(",", "，");
    }
}
```

### 2.10 解决 MyBatis-Plus 与 SQLite 的兼容问题

MyBatis-Plus 默认使用 MySQL 的分页插件，但 SQLite 需要特殊处理。创建 `MybatisPlusConfig.java`：

```java
package com.kunshan.graduates.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // SQLite 分页配置
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.SQLITE);
        paginationInterceptor.setMaxLimit(500L);  // 最大单页分页条数
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        return interceptor;
    }
}
```

### 2.11 初始化 SQLite 数据库

SQLite 会自动根据配置文件中的路径创建数据库文件，但表结构需要初始化。创建 `schema.sql`（放在 `src/main/resources/` 下，仅首次使用）：

```sql
-- 手动创建 graduates 表（如果数据库文件为空的话）
-- 这个文件通常不需要执行，MyBatis-Plus 会在启动时根据实体类自动创建表

CREATE TABLE IF NOT EXISTS graduates (
    id_card TEXT PRIMARY KEY,
    name TEXT,
    gender TEXT,
    nation TEXT,
    education TEXT,
    graduate_school TEXT,
    major TEXT,
    graduate_year TEXT,
    phone TEXT,
    household_address TEXT,
    current_address TEXT,
    employment_status TEXT,
    difficulty_type TEXT,
    registered_date TEXT,
    remarks TEXT,
    create_time TEXT,
    update_time TEXT
);
```

**重要**：在首次启动后端之前，创建一个空的数据库文件：

```bash
cd backend/src/main/resources
touch graduates.db
```

或者更简单的方式：后端启动时会自动创建空的 `.db` 文件，第一次启动后访问任意接口，MyBatis-Plus 会根据实体类自动创建表。

### 2.12 后端测试运行

在 IDEA 中打开 `backend` 项目，右键点击 `GraduatesApplication.java`，选择 "Run 'GraduatesApplication'"。

如果看到以下输出，说明后端启动成功：

```
========================================
未就业大学生信息管理系统启动成功！
访问地址：http://localhost:8080
========================================
```

打开浏览器访问 `http://localhost:8080/api/graduates/statistics`，应该能看到 JSON 返回。

---

## 三、前端项目搭建 (Vue)

### 3.1 创建 Vue 项目

打开终端，进入 `frontend` 目录，执行：

```bash
cd frontend
npm create vue@latest .
```

按提示选择：
- 是否使用 TypeScript：**否**（简化学习成本）
- 是否使用 Vue Router：**是**
- 是否使用 Pinia：**是**（状态管理）
- 是否使用 ESLint：**是**

等待安装完成。

### 3.2 安装必要依赖

进入 `frontend` 目录，安装额外依赖：

```bash
npm install axios element-plus @element-plus/icons-vue echarts vue-echarts
```

- `axios`：发送 HTTP 请求
- `element-plus`：UI 组件库
- `@element-plus/icons-vue`：图标库
- `echarts` + `vue-echarts`：图表库

### 3.3 项目目录结构

创建完成后，`frontend/src/` 目录结构如下：

```
frontend/src/
├── assets/              # 静态资源
├── components/         # 公共组件
│   └── ...
├── router/            # 路由配置
│   └── index.js
├── views/             # 页面组件（我们主要在这里写代码）
│   ├── GraduateList.vue    # 列表页
│   ├── GraduateForm.vue    # 新增/编辑表单
│   └── Dashboard.vue        # 统计图表页
├── App.vue
├── main.js
└── api/               # API 请求封装
    └── graduate.js
```

### 3.4 配置 main.js

修改 `src/main.js`：

```javascript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
```

### 3.5 配置 axios 请求

创建 `src/utils/request.js`（封装 axios）：

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080',  // 后端地址
    timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        return config
    },
    error => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        if (!res.success) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        return res
    },
    error => {
        console.error('响应错误:', error)
        ElMessage.error(error.message || '网络错误')
        return Promise.reject(error)
    }
)

export default request
```

### 3.6 API 封装

创建 `src/api/graduate.js`：

```javascript
import request from '@/utils/request'

export function getGraduateList(params) {
    return request({
        url: '/api/graduates/list',
        method: 'get',
        params
    })
}

export function getGraduate(idCard) {
    return request({
        url: `/api/graduates/${idCard}`,
        method: 'get'
    })
}

export function addGraduate(data) {
    return request({
        url: '/api/graduates',
        method: 'post',
        data
    })
}

export function updateGraduate(data) {
    return request({
        url: '/api/graduates',
        method: 'put',
        data
    })
}

export function deleteGraduate(idCard) {
    return request({
        url: `/api/graduates/${idCard}`,
        method: 'delete'
    })
}

export function checkIdCard(idCard) {
    return request({
        url: `/api/graduates/check/${idCard}`,
        method: 'get'
    })
}

export function getStatistics() {
    return request({
        url: '/api/graduates/statistics',
        method: 'get'
    })
}

export function exportData() {
    return request({
        url: '/api/graduates/export',
        method: 'get'
    })
}
```

### 3.7 路由配置

修改 `src/router/index.js`：

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/list'
    },
    {
        path: '/list',
        name: 'GraduateList',
        component: () => import('@/views/GraduateList.vue')
    },
    {
        path: '/add',
        name: 'GraduateAdd',
        component: () => import('@/views/GraduateForm.vue')
    },
    {
        path: '/edit/:idCard',
        name: 'GraduateEdit',
        component: () => import('@/views/GraduateForm.vue')
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
```

### 3.8 主页面布局

修改 `src/App.vue`：

```vue
<template>
    <el-container style="height: 100vh">
        <!-- 顶部导航 -->
        <el-header style="background: #545c64; color: white; display: flex; align-items: center">
            <span style="font-size: 20px; font-weight: bold">未就业大学生信息管理系统</span>
            <div style="flex: 1"></div>
            <el-menu
                :default-active="$route.path"
                mode="horizontal"
                background-color="#545c64"
                text-color="white"
                active-text-color="#ffd04b"
                router
            >
                <el-menu-item index="/list">信息管理</el-menu-item>
                <el-menu-item index="/dashboard">统计图表</el-menu-item>
            </el-menu>
        </el-header>

        <!-- 主内容区 -->
        <el-main style="background: #f0f2f5; padding: 20px">
            <router-view />
        </el-main>
    </el-container>
</template>

<script setup>
</script>
```

### 3.9 列表页面（核心页面）

创建 `src/views/GraduateList.vue`：

```vue
<template>
    <div>
        <!-- 搜索表单 -->
        <el-card style="margin-bottom: 20px">
            <el-form :inline="true" :model="searchForm">
                <el-form-item label="姓名">
                    <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable style="width: 150px" />
                </el-form-item>
                <el-form-item label="身份证号">
                    <el-input v-model="searchForm.idCard" placeholder="请输入身份证号" clearable style="width: 180px" />
                </el-form-item>
                <el-form-item label="学历">
                    <el-select v-model="searchForm.education" placeholder="请选择" clearable style="width: 120px">
                        <el-option label="本科" value="本科" />
                        <el-option label="专科" value="专科" />
                        <el-option label="硕士" value="硕士" />
                        <el-option label="博士" value="博士" />
                    </el-select>
                </el-form-item>
                <el-form-item label="民族">
                    <el-input v-model="searchForm.nation" placeholder="请输入民族" clearable style="width: 120px" />
                </el-form-item>
                <el-form-item label="就业状态">
                    <el-select v-model="searchForm.employmentStatus" placeholder="请选择" clearable style="width: 130px">
                        <el-option label="未就业" value="未就业" />
                        <el-option label="已就业" value="已就业" />
                        <el-option label="就业困难" value="就业困难" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon> 搜索
                    </el-button>
                    <el-button @click="handleReset">
                        <el-icon><Refresh /></el-icon> 重置
                    </el-button>
                    <el-button type="success" @click="handleAdd">
                        <el-icon><Plus /></el-icon> 新增
                    </el-button>
                    <el-button type="warning" @click="handleExport">
                        <el-icon><Download /></el-icon> 导出
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 数据表格 -->
        <el-card>
            <el-table :data="tableData" stripe border style="width: 100%" v-loading="loading">
                <el-table-column prop="idCard" label="身份证号" width="180" />
                <el-table-column prop="name" label="姓名" width="80" />
                <el-table-column prop="gender" label="性别" width="60" />
                <el-table-column prop="nation" label="民族" width="80" />
                <el-table-column prop="education" label="学历" width="80" />
                <el-table-column prop="graduateSchool" label="毕业院校" width="150" />
                <el-table-column prop="major" label="专业" width="120" />
                <el-table-column prop="graduateYear" label="毕业年份" width="100" />
                <el-table-column prop="phone" label="联系电话" width="120" />
                <el-table-column prop="employmentStatus" label="就业状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.employmentStatus)">
                            {{ row.employmentStatus }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="difficultyType" label="困难类型" width="100" />
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination
                v-model:current-page="pagination.current"
                v-model:page-size="pagination.size"
                :total="pagination.total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                style="margin-top: 20px; justify-content: flex-end"
                @size-change="loadData"
                @current-change="loadData"
            />
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGraduateList, deleteGraduate, exportData } from '@/api/graduate'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({
    name: '',
    idCard: '',
    education: '',
    nation: '',
    employmentStatus: ''
})
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0
})

const getStatusType = (status) => {
    const types = {
        '未就业': 'warning',
        '已就业': 'success',
        '就业困难': 'danger'
    }
    return types[status] || 'info'
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getGraduateList({
            current: pagination.current,
            size: pagination.size,
            ...searchForm
        })
        tableData.value = res.data
        pagination.total = res.total
    } catch (error) {
        console.error('加载数据失败:', error)
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    pagination.current = 1
    loadData()
}

const handleReset = () => {
    Object.assign(searchForm, {
        name: '',
        idCard: '',
        education: '',
        nation: '',
        employmentStatus: ''
    })
    handleSearch()
}

const handleAdd = () => {
    router.push('/add')
}

const handleEdit = (row) => {
    router.push(`/edit/${row.idCard}`)
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除 "${row.name}" 的信息吗？此操作不可恢复！`,
            '删除确认',
            { type: 'warning' }
        )
        await deleteGraduate(row.idCard)
        ElMessage.success('删除成功')
        loadData()
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除失败:', error)
        }
    }
}

const handleExport = async () => {
    try {
        const res = await exportData()
        // 创建下载
        const blob = new Blob(['\uFEFF' + res.data], { type: 'text/csv;charset=utf-8' })  // \uFEFF 是 BOM，解决 Excel 乱码
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = `毕业生信息_${new Date().toISOString().slice(0, 10)}.csv`
        link.click()
        ElMessage.success(`导出成功，共 ${res.total} 条数据`)
    } catch (error) {
        console.error('导出失败:', error)
    }
}

onMounted(() => {
    loadData()
})
</script>
```

### 3.10 表单页面（新增/编辑）

创建 `src/views/GraduateForm.vue`：

```vue
<template>
    <el-card>
        <template #header>
            <div style="display: flex; align-items: center">
                <el-button text @click="router.back()">
                    <el-icon><ArrowLeft /></el-icon> 返回
                </el-button>
                <span style="margin-left: 20px; font-size: 18px; font-weight: bold">
                    {{ isEdit ? '编辑信息' : '新增信息' }}
                </span>
            </div>
        </template>

        <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="120px"
            style="max-width: 800px"
        >
            <el-divider content-position="left">基本信息</el-divider>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="身份证号" prop="idCard">
                        <el-input
                            v-model="form.idCard"
                            placeholder="请输入18位身份证号"
                            :disabled="isEdit"
                            maxlength="18"
                            show-word-limit
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="姓名" prop="name">
                        <el-input v-model="form.name" placeholder="请输入姓名" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="form.gender">
                            <el-radio label="男">男</el-radio>
                            <el-radio label="女">女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="民族" prop="nation">
                        <el-input v-model="form.nation" placeholder="请输入民族" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-divider content-position="left">学历信息</el-divider>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="学历" prop="education">
                        <el-select v-model="form.education" placeholder="请选择学历" style="width: 100%">
                            <el-option label="本科" value="本科" />
                            <el-option label="专科" value="专科" />
                            <el-option label="硕士" value="硕士" />
                            <el-option label="博士" value="博士" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="毕业年份" prop="graduateYear">
                        <el-date-picker
                            v-model="form.graduateYear"
                            type="year"
                            placeholder="请选择毕业年份"
                            format="YYYY"
                            value-format="YYYY"
                            style="width: 100%"
                        />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="毕业院校" prop="graduateSchool">
                        <el-input v-model="form.graduateSchool" placeholder="请输入毕业院校" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="专业" prop="major">
                        <el-input v-model="form.major" placeholder="请输入专业" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-divider content-position="left">联系信息</el-divider>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="联系电话" prop="phone">
                        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="登记日期" prop="registeredDate">
                        <el-date-picker
                            v-model="form.registeredDate"
                            type="date"
                            placeholder="请选择登记日期"
                            format="YYYY-MM-DD"
                            value-format="YYYY-MM-DD"
                            style="width: 100%"
                        />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="户籍地址" prop="householdAddress">
                <el-input v-model="form.householdAddress" placeholder="请输入户籍地址" />
            </el-form-item>

            <el-form-item label="现居地址" prop="currentAddress">
                <el-input v-model="form.currentAddress" placeholder="请输入现居地址" />
            </el-form-item>

            <el-divider content-position="left">就业情况</el-divider>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="就业状态" prop="employmentStatus">
                        <el-select v-model="form.employmentStatus" placeholder="请选择就业状态" style="width: 100%">
                            <el-option label="未就业" value="未就业" />
                            <el-option label="已就业" value="已就业" />
                            <el-option label="就业困难" value="就业困难" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="困难类型" prop="difficultyType">
                        <el-select v-model="form.difficultyType" placeholder="请选择困难类型" style="width: 100%">
                            <el-option label="无" value="无" />
                            <el-option label="技能不足" value="技能不足" />
                            <el-option label="学历不足" value="学历不足" />
                            <el-option label="其他" value="其他" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="备注" prop="remarks">
                <el-input v-model="form.remarks" type="textarea" :rows="3" placeholder="请输入备注信息" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="handleSubmit" :loading="submitting">
                    {{ isEdit ? '保存修改' : '确认添加' }}
                </el-button>
                <el-button @click="router.back()">取消</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { getGraduate, addGraduate, updateGraduate, checkIdCard } from '@/api/graduate'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const submitting = ref(false)

const isEdit = computed(() => !!route.params.idCard)
const idCard = computed(() => route.params.idCard)

const form = reactive({
    idCard: '',
    name: '',
    gender: '男',
    nation: '',
    education: '',
    graduateYear: '',
    graduateSchool: '',
    major: '',
    phone: '',
    registeredDate: '',
    householdAddress: '',
    currentAddress: '',
    employmentStatus: '未就业',
    difficultyType: '无',
    remarks: ''
})

const rules = {
    idCard: [
        { required: true, message: '请输入身份证号', trigger: 'blur' },
        { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }
    ],
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
    education: [{ required: true, message: '请选择学历', trigger: 'change' }],
    phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ]
}

// 新增时检查身份证号是否已存在
const validateIdCard = async (rule, value, callback) => {
    if (!isEdit.value && value && value.length === 18) {
        try {
            const res = await checkIdCard(value)
            if (res.exists) {
                callback(new Error('该身份证号已存在'))
            } else {
                callback()
            }
        } catch (error) {
            callback()
        }
    } else {
        callback()
    }
}

const loadData = async () => {
    if (isEdit.value) {
        try {
            const res = await getGraduate(idCard.value)
            if (res.data) {
                Object.assign(form, res.data)
            }
        } catch (error) {
            ElMessage.error('加载数据失败')
            router.back()
        }
    }
}

const handleSubmit = async () => {
    try {
        await formRef.value.validate()
        submitting.value = true
        
        if (isEdit.value) {
            await updateGraduate(form)
            ElMessage.success('修改成功')
        } else {
            await addGraduate(form)
            ElMessage.success('添加成功')
        }
        
        router.push('/list')
    } catch (error) {
        console.error('提交失败:', error)
    } finally {
        submitting.value = false
    }
}

onMounted(() => {
    loadData()
})
</script>
```

### 3.11 统计图表页面

创建 `src/views/Dashboard.vue`：

```vue
<template>
    <div>
        <el-row :gutter="20" style="margin-bottom: 20px">
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center">
                        <div style="font-size: 36px; color: #409EFF; font-weight: bold">{{ statistics.total }}</div>
                        <div style="color: #666; margin-top: 10px">总人数</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center">
                        <div style="font-size: 36px; color: #E6A23C; font-weight: bold">{{ statistics.unemployed }}</div>
                        <div style="color: #666; margin-top: 10px">未就业</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center">
                        <div style="font-size: 36px; color: #67C23A; font-weight: bold">{{ statistics.employed }}</div>
                        <div style="color: #666; margin-top: 10px">已就业</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center">
                        <div style="font-size: 36px; color: #F56C6C; font-weight: bold">{{ statistics.difficult }}</div>
                        <div style="color: #666; margin-top: 10px">就业困难</div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <span>学历分布</span>
                    </template>
                    <div ref="educationChartRef" style="width: 100%; height: 300px"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <span>就业状态分布</span>
                    </template>
                    <div ref="statusChartRef" style="width: 100%; height: 300px"></div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <span>民族分布（前10）</span>
                    </template>
                    <div ref="nationChartRef" style="width: 100%; height: 300px"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <span>困难类型分布</span>
                    </template>
                    <div ref="difficultyChartRef" style="width: 100%; height: 300px"></div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStatistics } from '@/api/graduate'

const educationChartRef = ref()
const statusChartRef = ref()
const nationChartRef = ref()
const difficultyChartRef = ref()

let educationChart, statusChart, nationChart, difficultyChart

const statistics = reactive({
    total: 0,
    unemployed: 0,
    employed: 0,
    difficult: 0
})

const loadStatistics = async () => {
    try {
        const res = await getStatistics()
        
        // 处理统计数据
        const educationData = res.educationStats || []
        const statusData = res.statusStats || []
        const nationData = (res.nationStats || []).slice(0, 10)  // 取前10
        const difficultyData = res.difficultyStats || []
        
        // 计算总计
        statistics.total = educationData.reduce((sum, item) => sum + item.count, 0)
        statistics.unemployed = statusData.find(s => s.employment_status === '未就业')?.count || 0
        statistics.employed = statusData.find(s => s.employment_status === '已就业')?.count || 0
        statistics.difficult = statusData.find(s => s.employment_status === '就业困难')?.count || 0
        
        // 渲染图表
        await nextTick()
        
        renderEducationChart(educationData)
        renderStatusChart(statusData)
        renderNationChart(nationData)
        renderDifficultyChart(difficultyData)
        
    } catch (error) {
        console.error('加载统计失败:', error)
    }
}

const renderEducationChart = (data) => {
    educationChart = echarts.init(educationChartRef.value)
    educationChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
            label: { show: true, formatter: '{b}: {c}人 ({d}%)' },
            data: data.map(item => ({
                name: item.education || '未知',
                value: item.count
            })),
            color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
        }]
    })
}

const renderStatusChart = (data) => {
    statusChart = echarts.init(statusChartRef.value)
    statusChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
            type: 'pie',
            radius: '60%',
            label: { show: true, formatter: '{b}: {c}人' },
            data: data.map(item => ({
                name: item.employment_status || '未知',
                value: item.count
            })),
            color: ['#E6A23C', '#67C23A', '#F56C6C']
        }]
    })
}

const renderNationChart = (data) => {
    nationChart = echarts.init(nationChartRef.value)
    nationChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.map(item => item.nation || '未知') },
        yAxis: { type: 'value' },
        series: [{
            type: 'bar',
            data: data.map(item => item.count),
            itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#83bff6' },
                    { offset: 1, color: '#2378f7' }
                ])
            },
            barWidth: '50%'
        }]
    })
}

const renderDifficultyChart = (data) => {
    difficultyChart = echarts.init(difficultyChartRef.value)
    difficultyChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
            type: 'pie',
            radius: '50%',
            label: { show: true, formatter: '{b}: {c}人' },
            data: data.map(item => ({
                name: item.difficulty_type || '未知',
                value: item.count
            })),
            color: ['#67C23A', '#E6A23C', '#F56C6C', '#909399']
        }]
    })
}

onMounted(() => {
    loadStatistics()
    
    // 响应窗口大小变化
    window.addEventListener('resize', () => {
        educationChart?.resize()
        statusChart?.resize()
        nationChart?.resize()
        difficultyChart?.resize()
    })
})
</script>
```

### 3.12 修改 index.html

修改 `frontend/index.html`，添加中文标题：

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8">
    <link rel="icon" href="/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>未就业大学生信息管理系统</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

### 3.13 前端测试运行

在终端进入 `frontend` 目录，执行：

```bash
npm run dev
```

看到类似以下输出，说明启动成功：

```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://192.168.x.x:5173/
  ➜  press h + enter to show help
```

打开浏览器访问 `http://localhost:5173`，应该能看到系统界面。

---

## 四、接口联调

### 4.1 确保后端运行

1. 后端在 IDEA 中运行，确保看到 `GraduatesApplication` 启动成功
2. 访问 `http://localhost:8080/api/graduates/statistics`，确认返回 JSON

### 4.2 确保前端运行

1. 前端在终端运行 `npm run dev`
2. 访问 `http://localhost:5173`

### 4.3 测试完整流程

1. 点击「新增」按钮，填写一个毕业生的信息
2. 提交后，数据应该出现在列表中
3. 点击「编辑」，修改信息并保存
4. 点击「删除」，确认数据被删除
5. 在「统计图表」页面，查看各种图表是否正常显示

### 4.4 常见联调问题

**问题1：前端请求报 404**
- 确认后端是否启动
- 确认后端端口是 8080
- 确认前端 `request.js` 中的 `baseURL` 是 `http://localhost:8080`

**问题2：CORS 跨域错误**
- 确认后端 Controller 上有 `@CrossOrigin` 注解
- 或者在 `application.yml` 中配置跨域（后端已配置）

**问题3：SQLite 表不存在**
- 首次启动后端后，MyBatis-Plus 会自动根据实体类创建表
- 如果表没创建，重启后端试试

---

## 五、本机部署

### 5.1 打包后端

在终端进入 `backend` 目录，执行：

```bash
mvn clean package -DskipTests
```

等待编译完成，会在 `backend/target/` 目录下生成 `graduates-1.0.0.jar` 文件。

### 5.2 创建启动脚本

在 `backend/` 目录下创建 `start.bat`（Windows 双击启动脚本）：

```batch
@echo off
chcp 65001 > nul
echo ========================================
echo   未就业大学生信息管理系统启动中...
echo ========================================
java -jar target/graduates-1.0.0.jar
pause
```

### 5.3 一键启动

1. 双击 `start.bat`，等待启动完成
2. 后端运行在 `http://localhost:8080`
3. 前端在另一终端运行 `npm run dev`
4. 浏览器访问 `http://localhost:5173`

### 5.4 局域网访问（让其他同事访问）

如果要让同办公室的同事访问，需要：

1. **查看本机 IP**
   ```bash
   ipconfig
   ```
   找到类似 `192.168.1.xxx` 的 IPv4 地址

2. **确保防火墙允许**
   - Windows 防火墙需要开放 8080 和 5173 端口
   - 或者让 IT 帮忙开放

3. **同事访问**
   - 前端：`http://你的IP:5173`
   - 后端 API：`http://你的IP:8080`

### 5.5 设置开机自启（可选）

如果想让后端开机自动启动：

1. 创建快捷方式：将 `start.bat` 发送到桌面
2. 按 `Win + R`，输入 `shell:startup`
3. 将桌面上的快捷方式复制到打开的文件夹中

---

## 六、附录：常见问题

### Q1: 启动后端时报错 "Unable to create initial connections of pool"

**原因**：SQLite 数据库文件路径问题。

**解决**：
1. 确认 `application.yml` 中的路径正确
2. 确认 `src/main/resources/` 目录下有 `graduates.db` 文件（或空文件即可）
3. 如果路径使用 `./` 开头，确保在 `backend` 目录下执行 java 命令

### Q2: 前端 `npm install` 报错

**解决**：
```bash
# 清除缓存
npm cache clean --force

# 删除 node_modules 重新安装
rm -rf node_modules package-lock.json
npm install
```

### Q3: MyBatis-Plus 无法识别 SQLite 的表

**原因**：MyBatis-Plus 默认使用 MySQL 分页插件。

**解决**：确保 `MybatisPlusConfig.java` 中的 `DbType.SQLITE` 配置正确。

### Q4: 如何备份数据？

**方法**：
1. 关闭后端
2. 复制 `backend/src/main/resources/graduates.db` 文件
3. 粘贴到安全的位置即可

### Q5: 如何迁移到正式服务器？

1. 在正式服务器安装 JDK 17
2. 将 `backend/target/graduates-1.0.0.jar` 复制到服务器
3. 同样创建启动脚本
4. 前端执行 `npm run build`，生成 `dist` 文件夹
5. 将 `dist` 文件夹部署到 Nginx（或者让前端也打包成 jar 内嵌）

---

## 项目文件清单

以下是本教程创建的所有文件汇总：

```
backend/
├── pom.xml
├── start.bat                          # 启动脚本（需手动创建）
└── src/main/
    ├── java/com/kunshan/graduates/
    │   ├── GraduatesApplication.java
    │   ├── config/
    │   │   └── MybatisPlusConfig.java
    │   ├── controller/
    │   │   └── GraduateController.java
    │   ├── entity/
    │   │   └── Graduate.java
    │   ├── mapper/
    │   │   └── GraduateMapper.java
    │   └── service/
    │       ├── GraduateService.java
    │       └── impl/
    │           └── GraduateServiceImpl.java
    └── resources/
        ├── application.yml
        ├── graduates.db               # 自动生成
        └── schema.sql                # 可选

frontend/
├── package.json
├── index.html
└── src/
    ├── App.vue
    ├── main.js
    ├── api/
    │   └── graduate.js
    ├── router/
    │   └── index.js
    ├── utils/
    │   └── request.js
    └── views/
        ├── GraduateList.vue
        ├── GraduateForm.vue
        └── Dashboard.vue
```

---

*文档版本：v1.0*
*创建日期：2026年7月3日*
