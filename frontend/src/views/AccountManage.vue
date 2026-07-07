<!-- views/AccountManage.vue -->
<template>
    <a-card title="账号管理">
      <a-table :columns="columns" :data-source="dataList" row-key="id" :loading="loading" bordered>
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <a-tag :color="record.role === 'admin' ? 'red' : 'blue'">
              {{ record.role === 'admin' ? '管理员' : '普通用户' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'enabled'">
            <a-tag :color="record.enabled ? 'green' : 'default'">
              {{ record.enabled ? '启用' : '已禁用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'password'">
            <code>{{ record.passwordPlain || '(加密存储,点击眼睛查看明文)' }}</code>
            <a-button v-if="!record.passwordPlain" type="link" size="small" @click="reveal(record)">
              <EyeOutlined /> 查看
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { message } from 'ant-design-vue'
  import { EyeOutlined } from '@ant-design/icons-vue'
  import { fetchAccountsWithPlain, getAccountPlain } from '@/api/account'
  
  const columns = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '账号', dataIndex: 'username', width: 150 },
    { title: '姓名', dataIndex: 'name', width: 150 },
    { title: '角色', dataIndex: 'role', key: 'role', width: 100 },
    { title: '状态', dataIndex: 'enabled', key: 'enabled', width: 100 },
    { title: '密码(明文)', key: 'password', width: 350 },
    { title: '创建时间', dataIndex: 'createdAt', width: 180 }
  ]
  
  const dataList = ref([])
  const loading = ref(false)
  
  const load = async () => {
    loading.value = true
    try {
      const res = await fetchAccountsWithPlain()
      if (res.data.success) dataList.value = res.data.data
    } finally { loading.value = false }
  }
  
  const reveal = async (record) => {
    const res = await getAccountPlain(record.id)
    if (res.data.success) {
      record.passwordPlain = res.data.data.password
      message.success('密码已显示(仅管理员可见)')
    }
  }
  
  onMounted(load)
  </script>