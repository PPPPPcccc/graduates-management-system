<template>
    <div class="login-container">
      <div class="login-left">
        <div class="logo-area">
          <div class="emblem">★</div>
          <h1 class="system-name">未就业大学生<br/>信息管理系统</h1>
          <p class="system-sub">昆山市 · 大学生就业服务</p>
        </div>
      </div>
  
      <div class="login-right">
        <a-card class="login-card" :bordered="false">
          <h2 class="login-title">用户登录</h2>
          <p class="login-subtitle">请输入您的账号信息</p>
  
          <a-form
            :model="form"
            :rules="rules"
            ref="formRef"
            @finish="handleLogin"
            layout="vertical"
          >
            <a-form-item label="账号" name="username">
              <a-input v-model:value="form.username" placeholder="请输入登录账号" size="large" allow-clear>
                <template #prefix><UserOutlined /></template>
              </a-input>
            </a-form-item>
  
            <a-form-item label="密码" name="password">
              <a-input-password v-model:value="form.password" placeholder="请输入登录密码" size="large">
                <template #prefix><LockOutlined /></template>
              </a-input-password>
            </a-form-item>
  
            <a-button type="primary" html-type="submit" size="large" block :loading="loading">
              登 录
            </a-button>
          </a-form>
  
          <a-alert v-if="errorMsg" :message="errorMsg" type="error" show-icon style="margin-top: 16px" />
  
          <div class="login-tip">
            <p>测试账号：</p>
            <p>管理员：admin / admin123</p>
            <p>普通用户：staff / staff123</p>
          </div>
        </a-card>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { message } from 'ant-design-vue'
  import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
  import { login } from '../api/auth'
  
  const router = useRouter()
  const formRef = ref()
  const loading = ref(false)
  const errorMsg = ref('')
  
  const form = reactive({ username: '', password: '' })
  
  const rules = {
    username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码至少 6 位', trigger: 'blur' }
    ]
  }
  
  const handleLogin = async () => {
    errorMsg.value = ''
    loading.value = true
    try {
      const res = await login(form.username, form.password)
      if (res.data.success) {
        localStorage.setItem('loggedIn', 'true')
        localStorage.setItem('username', res.data.data.username)
        localStorage.setItem('name', res.data.data.name)
        localStorage.setItem('role', res.data.data.role)
        message.success('登录成功')
        router.push('/home')
      } else {
        errorMsg.value = res.data.message
      }
    } catch (err) {
      errorMsg.value = '登录失败：' + (err.response?.data?.message || err.message)
    } finally {
      loading.value = false
    }
  }
  </script>
  
  <style scoped>
  .login-container { display: flex; height: 100vh; width: 100vw; }
  .login-left {
    flex: 1;
    background: linear-gradient(135deg, #1e3a5f 0%, #2c5282 100%);
    display: flex; align-items: center; justify-content: center; color: white;
  }
  .logo-area { text-align: center; }
  .emblem { font-size: 96px; margin-bottom: 24px; color: #d4af37; }
  .system-name { font-size: 36px; font-weight: bold; letter-spacing: 4px; margin-bottom: 16px; line-height: 1.5; }
  .system-sub { font-size: 16px; letter-spacing: 6px; color: rgba(255, 255, 255, 0.7); }
  
  .login-right {
    flex: 1; background: #f5f5f5;
    display: flex; align-items: center; justify-content: center;
  }
  .login-card { width: 400px; padding: 32px; border-radius: 8px; box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08); }
  .login-title { font-size: 28px; text-align: center; margin-bottom: 8px; color: #1e3a5f; }
  .login-subtitle { text-align: center; color: #888; margin-bottom: 32px; }
  .login-tip {
    margin-top: 24px; padding: 16px; background: #f0f5fa;
    border-left: 3px solid #2c5282; font-size: 13px; color: #555; line-height: 1.8;
  }
  .login-tip p { margin: 0; }
  </style>