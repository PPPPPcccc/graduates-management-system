<!-- components/GraduateDetailModal.vue -->
<template>
    <a-modal
      :open="open"
      :title="mode === 'edit' ? '编辑毕业生信息' : '查看毕业生信息'"
      width="800px"
      :footer="null"
      :mask-closable="false"
      @cancel="handleClose"
      :destroy-on-close="true"
    >
      <a-form
        :model="form"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
        :disabled="mode === 'view'"
        layout="horizontal"
      >
        <a-row :gutter="16">
          <a-col :span="12"><a-form-item label="姓名">
            <a-input v-model:value="form.name" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="身份证号">
            <a-input v-model:value="form.idCard" />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="学历">
            <a-select v-model:value="form.education" :options="EDU" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="毕业院校">
            <a-input v-model:value="form.school" />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="毕业日期">
            <a-input v-model:value="form.graduationDate" placeholder="如:2026年6月" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="专业">
            <a-input v-model:value="form.major" />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="联系电话">
            <a-input v-model:value="form.phone" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="调查人">
            <a-input v-model:value="form.investigator" />
          </a-form-item></a-col>
  
          <a-col :span="24"><a-form-item label="户籍地址">
            <a-input v-model:value="form.householdAddress" />
          </a-form-item></a-col>
          <a-col :span="24"><a-form-item label="常住详细地址">
            <a-input v-model:value="form.residenceAddress" />
          </a-form-item></a-col>
  
          <a-col :span="12"><a-form-item label="调查日期">
            <a-input v-model:value="form.investigationDate" placeholder="如:4月16日" />
          </a-form-item></a-col>
          <a-col :span="12"><a-form-item label="调查方式">
            <a-select v-model:value="form.investigationMethod" :options="METHOD" />
          </a-form-item></a-col>
  
          <!-- ========== 三联就业下拉 ========== -->
          <a-col :span="8"><a-form-item label="就业情况(一级)">
            <a-select v-model:value="form.employmentStatus" :options="EMP_STATUS"
              placeholder="请选择" @change="onStatusChange" />
          </a-form-item></a-col>
          <a-col :span="8"><a-form-item label="就业情况(二级)">
            <a-select v-model:value="form.employmentType" :options="empTypeOptions"
              placeholder="请先选择一级" :disabled="!form.employmentStatus || form.employmentStatus !== '已就业'"
              @change="onTypeChange" />
          </a-form-item></a-col>
          <a-col :span="8"><a-form-item label="就业情况(三级)">
            <a-select v-model:value="thirdLevel.value" :options="thirdLevel.options"
              placeholder="请先选择二级" :disabled="!thirdLevel.enabled" />
          </a-form-item></a-col>
        </a-row>
  
        <!-- 编辑模式才有保存按钮 -->
        <div v-if="mode === 'edit'" style="text-align: right; margin-top: 16px;">
          <a-space>
            <a-button @click="handleClose">取消</a-button>
            <a-button type="primary" :loading="saving" @click="handleSave">保存</a-button>
          </a-space>
        </div>
      </a-form>
    </a-modal>
  </template>
  
  <script setup>
  import { reactive, ref, watch } from 'vue'
  import { message } from 'ant-design-vue'
  import { updateGraduate } from '@/api/graduate'
  
  const props = defineProps({
    open: Boolean,
    mode: { type: String, default: 'view' },  // view / edit
    record: { type: Object, default: () => ({}) }
  })
  const emit = defineEmits(['update:open', 'saved'])
  
  const form = reactive({})
  const saving = ref(false)
  
  // 下拉字典
  const EDU = [
    { value: '专科生毕业', label: '专科生毕业' },
    { value: '本科生毕业', label: '本科生毕业' },
    { value: '硕士研究生', label: '硕士研究生' },
    { value: '博士研究生', label: '博士研究生' }
  ]
  const METHOD = [
    { value: '电话调查', label: '电话调查' },
    { value: '上门调查', label: '上门调查' }
  ]
  const EMP_STATUS = [
    { value: '已就业', label: '已就业' },
    { value: '未就业', label: '未就业' }
  ]
  const EMP_TYPE = [
    { value: '单位就业', label: '单位就业' },
    { value: '其他就业', label: '其他就业' },
    { value: '特殊就业', label: '特殊就业' },
    { value: '其他情况', label: '其他情况' }
  ]
  
  // 三联下拉的级联
  const empTypeOptions = ref([])
  const thirdLevel = reactive({ enabled: false, value: undefined, options: [] })
  
  const onStatusChange = (val) => {
    form.employmentType = undefined
    thirdLevel.value = undefined
    if (val === '已就业') {
      empTypeOptions.value = EMP_TYPE
    } else {
      empTypeOptions.value = []
      thirdLevel.enabled = false
      thirdLevel.options = []
    }
  }
  const onTypeChange = (val) => {
    thirdLevel.value = undefined
    if (val === '其他就业') {
      thirdLevel.enabled = true
      thirdLevel.options = [
        { value: '个体经营', label: '个体经营' },
        { value: '灵活就业', label: '灵活就业' },
        { value: '公益岗位', label: '公益岗位' },
        { value: '自主创业', label: '自主创业' }
      ]
    } else if (val === '特殊就业') {
      thirdLevel.enabled = true
      thirdLevel.options = [
        { value: '社区岗位', label: '社区岗位' },
        { value: '基层项目', label: '基层项目' },
        { value: '科研助理', label: '科研助理' }
      ]
    } else if (val === '其他情况') {
      thirdLevel.enabled = true
      thirdLevel.options = [
        { value: '参军', label: '参军' },
        { value: '出国', label: '出国' },
        { value: '升学', label: '升学' }
      ]
    } else {
      thirdLevel.enabled = false
      thirdLevel.options = []
    }
  }
  
  // 初始化表单(打开弹窗时填充 record)
  watch(() => props.open, (v) => {
    if (v) {
      Object.assign(form, JSON.parse(JSON.stringify(props.record || {})))
      // 回填三级级联
      if (form.employmentStatus) onStatusChange(form.employmentStatus)
      if (form.employmentType) {
        // 回填 thirdLevel
        const map = {
          '其他就业': 'otherEmployment',
          '特殊就业': 'specialEmployment',
          '其他情况': 'otherSituation'
        }
        const field = map[form.employmentType]
        if (field && form[field]) {
          onTypeChange(form.employmentType)
          thirdLevel.value = form[field]
        }
      }
    }
  })
  
  const handleClose = () => emit('update:open', false)
  const handleSave = async () => {
    saving.value = true
    try {
      // 把 thirdLevel 写回对应字段
      if (form.employmentType === '其他就业') form.otherEmployment = thirdLevel.value
      if (form.employmentType === '特殊就业') form.specialEmployment = thirdLevel.value
      if (form.employmentType === '其他情况') form.otherSituation = thirdLevel.value
  
      const res = await updateGraduate(form.id, form)
      if (res.data.success) {
        message.success('保存成功')
        emit('saved')
        handleClose()
      } else {
        message.error(res.data.message)
      }
    } finally {
      saving.value = false
    }
  }
  </script>