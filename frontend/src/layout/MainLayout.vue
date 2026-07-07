<!-- layout/MainLayout.vue -->
<template>
    <a-layout class="main-layout">
      <!-- ========== 顶部 ========== -->
      <a-layout-header class="app-header">
        <div class="header-left">
          <span class="system-logo">★ 未就业大学生信息管理系统</span>
        </div>
        <div class="header-right">
          <a-dropdown>
            <span class="user-info">
              <a-avatar size="small" style="background:#2c5282">
                {{ userInitial }}
              </a-avatar>
              <span class="user-name">{{ userName }}</span>
              <CaretDownOutlined />
            </span>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="handleLogout">
                  <LogoutOutlined /> 退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
  
      <a-layout>
        <!-- ========== 左侧菜单 ========== -->
        <a-layout-sider width="220" theme="dark" class="app-sider">
          <a-menu
            theme="dark"
            mode="inline"
            :selected-keys="[route.path]"
            @click="handleMenuClick"
          >
            <a-menu-item v-for="m in menus" :key="m.path">
              <component :is="m.icon" />
              <span>{{ m.title }}</span>
            </a-menu-item>
          </a-menu>
        </a-layout-sider>
  
        <!-- ========== 内容区 ========== -->
        <a-layout-content class="app-content">
          <!-- 面包屑 -->
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item><HomeOutlined /> 首页</a-breadcrumb-item>
            <a-breadcrumb-item v-for="b in breadcrumbs" :key="b.path">
              {{ b.title }}
            </a-breadcrumb-item>
          </a-breadcrumb>
  
          <!-- 路由出口 -->
          <div class="content-wrapper">
            <router-view />
          </div>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </template>
  
  <script setup>
  import { computed, h } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { message, Modal } from 'ant-design-vue'
  import {
    TeamOutlined, UserOutlined, CaretDownOutlined,
    LogoutOutlined, HomeOutlined
  } from '@ant-design/icons-vue'
  import { logout } from '@/api/auth'
  
  const route = useRoute()
  const router = useRouter()
  
  const userName = computed(() => localStorage.getItem('name') || '用户')
  const role = computed(() => localStorage.getItem('role'))
  const userInitial = computed(() => (userName.value || 'U')[0])
  
  // 根据角色过滤菜单
  const allMenus = [
    { path: '/app/graduate', title: '高校毕业生信息管理', icon: TeamOutlined, adminOnly: false },
    { path: '/app/account',   title: '账号管理',           icon: UserOutlined,  adminOnly: true  }
  ]
  const menus = computed(() => allMenus.filter(m => !m.adminOnly || role.value === 'admin'))
  
  const breadcrumbs = computed(() => {
    const matched = route.matched.filter(r => r.meta && r.meta.title)
    return matched.map(r => ({ path: r.path, title: r.meta.title }))
  })
  
  const handleMenuClick = ({ key }) => router.push(key)
  
  const handleLogout = () => {
    Modal.confirm({
      title: '确定退出登录?',
      onOk: async () => {
        try { await logout() } catch (e) {}
        localStorage.clear()
        message.success('已退出登录')
        router.push('/login')
      }
    })
  }
  </script>
  
  <style scoped>
  .main-layout { min-height: 100vh; }
  
  .app-header {
    background: linear-gradient(90deg, #1e3a5f 0%, #2c5282 100%);
    display: flex; align-items: center; justify-content: space-between;
    padding: 0 24px; color: white;
  }
  .system-logo { font-size: 18px; font-weight: bold; letter-spacing: 2px; }
  .user-info { color: white; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; }
  .user-name { font-size: 14px; }
  
  .app-sider { background: #001529; }
  
  .app-content { padding: 16px 24px; background: #f0f2f5; }
  .breadcrumb { margin-bottom: 16px; }
  .content-wrapper {
    background: white; padding: 24px; border-radius: 4px;
    min-height: calc(100vh - 64px - 32px - 36px);
  }
  </style>