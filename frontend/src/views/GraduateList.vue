<!-- views/GraduateList.vue -->
<template>
    <div class="graduate-page">
  
      <!-- ========== 搜索区 ========== -->
      <a-form layout="inline" :model="filters" class="filter-bar">
        <a-form-item label="姓名">
          <a-input v-model:value="filters.name" placeholder="请输入姓名" allow-clear />
        </a-form-item>
        <a-form-item label="学历">
          <a-select
            v-model:value="filters.education"
            placeholder="请选择学历"
            allow-clear
            style="width: 180px"
            :options="educationOptions"
          />
        </a-form-item>
        <a-form-item label="毕业日期">
          <a-select
            v-model:value="filters.graduationDate"
            placeholder="请选择毕业日期"
            allow-clear
            style="width: 160px"
            :options="graduationDateOptions"
          />
        </a-form-item>
        <a-form-item label="调查人">
          <a-select
            v-model:value="filters.investigator"
            placeholder="请选择调查人"
            allow-clear
            style="width: 160px"
            :options="investigatorOptions"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleQuery">
              <SearchOutlined /> 查询
            </a-button>
            <a-button @click="handleReset">
              <ReloadOutlined /> 重置
            </a-button>
            <a-button @click="openAdvanced">
              <FilterOutlined /> 高级查询
            </a-button>
            <a-divider type="vertical" style="margin: 0 4px;" />
            <a-button type="primary" @click="openAdd">
              <PlusOutlined /> 添加
            </a-button>
            <a-button @click="openBatch">
              <UploadOutlined /> 批量添加
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
  
      <!-- ========== 表格 ========== -->
      <div class="table-scroll-wrapper">
        <a-table
          :columns="columns"
          :data-source="dataList"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          @change="handleTableChange"
          size="middle"
          bordered
          :scroll="{ x: 'max-content' }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" size="small" @click="openView(record)">
                  <EyeOutlined /> 查看
                </a-button>
                <a-button type="link" size="small" @click="openEdit(record)">
                  <EditOutlined /> 编辑
                </a-button>
              </a-space>
            </template>
            <template v-else-if="column.key === 'idCard'">
              {{ maskIdCard(record.idCard) }}
            </template>
          </template>
        </a-table>
      </div>
  
      <!-- ========== 查看/编辑 弹窗 ========== -->
      <GraduateDetailModal
        v-model:open="modal.open"
        :mode="modal.mode"
        :record="modal.record"
        @saved="handleSaved"
      />

      <!-- ========== 批量添加弹窗 ========== -->
      <BatchAddModal
        v-model:open="batch.open"
        @saved="handleSaved"
      />
  
      <!-- ========== 高级查询弹窗 ========== -->
      <AdvancedFilterModal
        v-model:open="advanced.open"
        :form="advanced.form"
        :options="options"
        @search="handleAdvancedSearch"
        @reset="handleAdvancedReset"
      />
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted, computed } from 'vue'
  import { message } from 'ant-design-vue'
  import {
    SearchOutlined, ReloadOutlined, FilterOutlined,
    EyeOutlined, EditOutlined, PlusOutlined, UploadOutlined
  } from '@ant-design/icons-vue'
  import { fetchGraduateList, fetchFilterOptions } from '@/api/graduate'
  import GraduateDetailModal from '@/components/GraduateDetailModal.vue'
  import AdvancedFilterModal from '@/components/AdvancedFilterModal.vue'
  import BatchAddModal from '@/components/BatchAddModal.vue'
  
  // ====== 顶部简易筛选 ======
  const filters = reactive({
    name: '',
    education: undefined,
    graduationDate: undefined,
    investigator: undefined
  })
  
  // ====== 弹窗状态 ======
  const modal = reactive({ open: false, mode: 'view', record: {} })
  const advanced = reactive({ open: false, form: {} })
  const batch = reactive({ open: false })

  const openAdd = () => {
    modal.mode = 'add'
    modal.record = {}
    modal.open = true
  }
  const openBatch = () => {
    batch.open = true
  }
  const openView = (record) => {
    modal.mode = 'view'
    modal.record = record
    modal.open = true
  }
  const openEdit = (record) => {
    modal.mode = 'edit'
    modal.record = record
    modal.open = true
  }
  const handleSaved = () => loadData()
  
  // ====== 选项(后端下拉字典) ======
  const options = reactive({
    education: [],
    graduationDate: [],
    investigator: [],
    school: [],
    investigationDate: [],
    investigationMethod: []
  })
  const educationOptions = computed(() => options.education.map(v => ({ label: v, value: v })))
  const graduationDateOptions = computed(() => options.graduationDate.map(v => ({ label: v, value: v })))
  const investigatorOptions = computed(() => options.investigator.map(v => ({ label: v, value: v })))
  
  // ====== 表格列 ======
  const columns = [
    { title: '序号', key: 'index', width: 70,
      customRender: ({ index }) => (pagination.current - 1) * pagination.pageSize + index + 1
    },
    { title: '姓名',     dataIndex: 'name', width: 100 },
    { title: '身份证号', dataIndex: 'idCard', key: 'idCard', width: 200 },
    { title: '学历',     dataIndex: 'education', width: 120 },
    { title: '毕业院校', dataIndex: 'school', width: 200 },
    { title: '毕业日期', dataIndex: 'graduationDate', width: 110 },
    { title: '专业',     dataIndex: 'major', width: 140 },
    { title: '联系电话', dataIndex: 'phone', width: 130 },
    { title: '户籍地址', dataIndex: 'householdAddress', width: 160, ellipsis: true },
    { title: '调查人',   dataIndex: 'investigator', width: 100 },
    { title: '调查日期', dataIndex: 'investigationDate', width: 100 },
    { title: '就业情况', key: 'employment', width: 200,
      customRender: ({ record }) => formatEmployment(record)
    },
    { title: '操作', key: 'action', width: 160, fixed: 'right' }
  ]
  
  const formatEmployment = (r) => {
    if (!r.employmentStatus) return '-'
    let s = r.employmentStatus
    if (r.employmentType) s += ' / ' + r.employmentType
    if (r.otherEmployment)     s += ' / ' + r.otherEmployment
    if (r.specialEmployment)   s += ' / ' + r.specialEmployment
    if (r.otherSituation)      s += ' / ' + r.otherSituation
    return s
  }
  
  // ====== 数据加载 ======
  const dataList = ref([])
  const loading = ref(false)
  const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true })
  
  const queryParams = computed(() => ({
    ...filters,
    page: pagination.current,
    pageSize: pagination.pageSize
  }))
  
  const loadData = async () => {
    loading.value = true
    try {
      const res = await fetchGraduateList(queryParams.value)
      if (res.data.success) {
        dataList.value = res.data.data.records
        pagination.total = res.data.data.total
      } else {
        message.error(res.data.message || '加载失败')
      }
    } finally {
      loading.value = false
    }
  }
  
  const loadOptions = async () => {
    try {
      const res = await fetchFilterOptions()
      if (res.data.success) Object.assign(options, res.data.data)
    } catch (e) {
      console.error('加载筛选选项失败', e)
    }
  }
  
  const openAdvanced = () => {
    advanced.open = true
  }
  const handleAdvancedSearch = (form) => {
    Object.assign(filters, form)
    advanced.open = false
    loadData()
  }
  const handleAdvancedReset = () => {
    Object.keys(filters).forEach(k => filters[k] = undefined)
    advanced.open = false
    loadData()
  }

  const handleQuery = () => {
    pagination.current = 1
    loadData()
  }
  const handleReset = () => {
    Object.keys(filters).forEach(k => filters[k] = undefined)
    handleQuery()
  }
  const handleTableChange = (pag) => {
    pagination.current = pag.current
    pagination.pageSize = pag.pageSize
    loadData()
  }

  const maskIdCard = (s) => {
    if (!s || s.length < 18) return s
    return s.slice(0, 6) + '*'.repeat(8) + s.slice(14)
  }
  
  onMounted(() => {
    loadOptions()
    loadData()
  })
  </script>
  
  <style scoped>
  .graduate-page { padding: 0; }
  .filter-bar { margin-bottom: 16px; }
  :deep(.ant-form-item) { margin-bottom: 12px; }
  .table-scroll-wrapper { overflow-x: auto; }
  </style>