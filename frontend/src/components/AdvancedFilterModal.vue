<!-- components/AdvancedFilterModal.vue -->
<template>
    <a-modal
      :open="open"
      title="高级查询"
      width="720px"
      :footer="null"
      @cancel="emit('update:open', false)"
    >
      <a-form :model="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12"><a-form-item label="姓名">
            <a-input v-model:value="form.name" allow-clear />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="身份证号">
            <a-input v-model:value="form.idCard" allow-clear />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="学历">
            <a-select v-model:value="form.education" allow-clear :options="toOpts(options.education)" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="毕业院校">
            <a-select v-model:value="form.school" allow-clear show-search :options="toOpts(options.school)" />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="毕业日期">
            <a-select v-model:value="form.graduationDate" allow-clear :options="toOpts(options.graduationDate)" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="专业">
            <a-input v-model:value="form.major" allow-clear />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="联系电话">
            <a-input v-model:value="form.phone" allow-clear />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="户籍地址">
            <a-input v-model:value="form.householdAddress" allow-clear />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="调查人">
            <a-select v-model:value="form.investigator" allow-clear :options="toOpts(options.investigator)" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="调查日期">
            <a-select v-model:value="form.investigationDate" allow-clear :options="toOpts(options.investigationDate)" />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="调查方式">
            <a-select v-model:value="form.investigationMethod" allow-clear
              :options="[
                { value: '电话调查', label: '电话调查' },
                { value: '上门调查', label: '上门调查' }
              ]" />
          </a-form-item></a-col>
  
          <!-- ========== 三联就业下拉 ========== -->
          <a-col :span="8"><a-form-item label="就业情况(一)">
            <a-select v-model:value="form.employmentStatus" allow-clear
              :options="[{value:'已就业',label:'已就业'},{value:'未就业',label:'未就业'}]"
              placeholder="请选择"
              @change="onStatusChange" />
          </a-form-item></a-col>
  
          <a-col :span="8"><a-form-item label="就业情况(二)">
            <a-select v-model:value="form.employmentType" allow-clear
              :options="empTypeOptions" placeholder="请先选择一级"
              :disabled="!form.employmentStatus || form.employmentStatus !== '已就业'"
              @change="onTypeChange" />
          </a-form-item></a-col>
  
          <a-col :span="8"><a-form-item label="就业情况(三)">
            <a-select v-model:value="thirdLevel.value" allow-clear
              :options="thirdLevel.options" placeholder="请先选择二级"
              :disabled="!thirdLevel.enabled" />
          </a-form-item></a-col>
        </a-row>
  
        <div style="text-align: right; margin-top: 8px;">
          <a-space>
            <a-button @click="emit('reset')">重置</a-button>
            <a-button type="primary" @click="emit('search', form)">查询</a-button>
          </a-space>
        </div>
      </a-form>
    </a-modal>
  </template>
  
  <script setup>
  import { reactive, ref } from 'vue'
  
  const props = defineProps({
    open: Boolean,
    form: Object,
    options: Object
  })
  const emit = defineEmits(['update:open', 'search', 'reset'])
  
  const form = reactive({ ...props.form })
  
  const toOpts = (arr = []) => arr.map(v => ({ value: v, label: v }))
  
  // 三联级联
  const empTypeOptions = ref([])
  const thirdLevel = reactive({ enabled: false, value: undefined, options: [] })
  
  const onStatusChange = (v) => {
    form.employmentType = undefined
    thirdLevel.value = undefined
    empTypeOptions.value = v === '已就业' ? [
      { value: '单位就业', label: '单位就业' },
      { value: '其他就业', label: '其他就业' },
      { value: '特殊就业', label: '特殊就业' },
      { value: '其他情况', label: '其他情况' }
    ] : []
    thirdLevel.options = []
    thirdLevel.enabled = false
  }
  const onTypeChange = (v) => {
    thirdLevel.value = undefined
    const map = {
      '其他就业': [
        { value: '个体经营', label: '个体经营' },
        { value: '灵活就业', label: '灵活就业' },
        { value: '公益岗位', label: '公益岗位' },
        { value: '自主创业', label: '自主创业' }
      ],
      '特殊就业': [
        { value: '社区岗位', label: '社区岗位' },
        { value: '基层项目', label: '基层项目' },
        { value: '科研助理', label: '科研助理' }
      ],
      '其他情况': [
        { value: '参军', label: '参军' },
        { value: '出国', label: '出国' },
        { value: '升学', label: '升学' }
      ]
    }
    thirdLevel.options = map[v] || []
    thirdLevel.enabled = !!v
  }
  </script>