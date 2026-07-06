<template>
    <div class="home-container">
      <div class="header">
        <div class="header-left">
          <span class="system-logo">★ 未就业大学生信息管理系统</span>
        </div>
        <div class="header-right">
          <span class="welcome-text">欢迎您，{{ userName }}（{{ roleText }}）</span>
          <a-button type="link" @click="handleLogout">退出登录</a-button>
        </div>
      </div>
  
      <div class="content">
        <a-card>
          <h2>登录成功！</h2>
          <p>这是首页占位页面。后续会在这里做：</p>
          <ul>
            <li>毕业生信息录入</li>
            <li>毕业生信息查询</li>
            <li>统计分析</li>
            <li>用户管理</li>
          </ul>
        </a-card>
      </div>
    </div>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { message } from 'ant-design-vue'
  import { logout } from '../api/auth'
  
  const router = useRouter()
  
  const userName = computed(() => localStorage.getItem('name') || '用户')
  const roleText = computed(() => {
    const role = localStorage.getItem('role')
    return role === 'admin' ? '管理员' : '普通用户'
  })
  
  const handleLogout = async () => {
    try { await logout() } catch (e) {}
    localStorage.clear()
    message.success('已退出登录')
    router.push('/login')
  }
  </script>
  
  <style scoped>
  .home-container { min-height: 100vh; background: #f5f5f5; }
  .header {
    height: 64px;
    background: linear-gradient(90deg, #1e3a5f 0%, #2c5282 100%);
    color: white;
    display: flex; align-items: center; justify-content: space-between;
    padding: 0 24px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
  .system-logo { font-size: 18px; font-weight: bold; letter-spacing: 2px; }
  .welcome-text { margin-right: 16px; font-size: 14px; }
  .content { padding: 24px; }
  </style>