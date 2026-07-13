<!-- components/GraduateDetailModal.vue -->
<template>
    <a-modal
      :open="open"
      :title="mode === 'add' ? '添加毕业生信息' : mode === 'edit' ? '编辑毕业生信息' : '查看毕业生信息'"
      width="900px"
      :footer="null"
      :mask-closable="false"
      @cancel="handleClose"
      :destroy-on-close="true"
    >
      <a-form :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" :disabled="mode === 'view'" layout="horizontal">

        <!-- ========== 区域一：个人信息 ========== -->
        <div class="section-block">
          <div class="section-title">个人信息</div>
          <a-row :gutter="16">
            <a-col :span="12"><a-form-item label="姓名" name="name">
              <a-input v-model:value="form.name" placeholder="请输入姓名" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="身份证号" name="idCard">
              <a-input v-model:value="form.idCard" placeholder="请输入身份证号" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="学历" name="education">
              <a-select v-model:value="form.education" :options="EDU" placeholder="请选择学历" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="毕业院校" name="school">
              <a-input v-model:value="form.school" placeholder="请输入毕业院校" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="毕业日期" name="graduationDate">
              <a-input v-model:value="form.graduationDate" placeholder="如:2026年6月" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="专业" name="major">
              <a-input v-model:value="form.major" placeholder="请输入专业" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="联系电话" name="phone">
              <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
            </a-form-item></a-col>
            <a-col :span="24"><a-form-item label="户籍地址" name="householdAddress">
              <a-input v-model:value="form.householdAddress" />
            </a-form-item></a-col>
            <a-col :span="24"><a-form-item label="常住详细地址" name="residenceAddress">
              <a-input v-model:value="form.residenceAddress" />
            </a-form-item></a-col>
          </a-row>
        </div>

        <!-- ========== 区域二：调查记录 ========== -->
        <div class="section-block">
          <div class="section-title">调查记录</div>
          <a-row :gutter="16">
            <a-col :span="12"><a-form-item label="调查人" name="investigator">
              <a-input v-model:value="form.investigator" placeholder="请输入调查人" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="调查日期" name="investigationDate">
              <a-input v-model:value="form.investigationDate" placeholder="如:4月16日" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="调查方式" name="investigationMethod">
              <a-select v-model:value="form.investigationMethod" :options="METHOD" placeholder="请选择" />
            </a-form-item></a-col>
            <a-col :span="12"><a-form-item label="服务时间" name="serviceTime">
              <div class="tag-input-wrapper">
                <!-- 已有时间标签 -->
                <span
                  v-for="(t, idx) in form.serviceTime || []"
                  :key="idx"
                  class="time-tag"
                >
                  {{ t }}
                  <span v-if="mode !== 'view'" class="time-tag-close" @click="removeServiceTime(idx)">×</span>
                </span>
                <!-- 添加入口（编辑/新增模式显示） -->
                <span v-if="mode !== 'view'" class="time-add-row">
                  <a-input-number
                    v-model:value="addMonth"
                    :min="1" :max="12"
                    placeholder="月"
                    class="time-num-input"
                    :controls="false"
                  />
                  <span class="time-unit">月</span>
                  <a-input-number
                    v-model:value="addDay"
                    :min="1" :max="31"
                    placeholder="日"
                    class="time-num-input"
                    :controls="false"
                  />
                  <span class="time-unit">日</span>
                  <a-button type="link" size="small" @click="addServiceTime">添加</a-button>
                </span>
              </div>
            </a-form-item></a-col>
          </a-row>
        </div>

        <!-- ========== 区域三：就业情况 ========== -->
        <div class="section-block">
          <div class="section-title">就业情况</div>

          <!-- 一级：就业状态 -->
          <a-row :gutter="16" style="margin-bottom: 16px;">
            <a-col :span="12"><a-form-item label="就业情况" name="employmentStatus">
              <a-select v-model:value="form.employmentStatus" :options="EMP_STATUS"
                placeholder="请选择" @change="onStatusChange" />
            </a-form-item></a-col>
            <a-col :span="12" v-if="form.employmentStatus === '已就业'"><a-form-item label="就业类型" name="employmentType">
              <a-select v-model:value="form.employmentType" :options="empTypeOptions"
                placeholder="请先选择就业情况" @change="onTypeChange" />
            </a-form-item></a-col>
          </a-row>

          <!-- 已就业-单位就业 -->
          <div v-if="form.employmentStatus === '已就业' && form.employmentType === '单位就业'">
            <a-row :gutter="16">
              <a-col :span="12"><a-form-item label="就业地" name="employmentLocation">
                <a-input v-model:value="form.employmentLocation" />
              </a-form-item></a-col>
              <a-col :span="12"><a-form-item label="单位名称" name="unitName">
                <a-input v-model:value="form.unitName" />
              </a-form-item></a-col>
              <a-col :span="12"><a-form-item label="单位性质" name="unitNature">
                <a-input v-model:value="form.unitNature" placeholder="请输入单位性质" />
              </a-form-item></a-col>
              <a-col :span="12"><a-form-item label="专业是否对口" name="majorMatched">
                <a-select v-model:value="form.majorMatched" :options="YES_NO" placeholder="请选择" />
              </a-form-item></a-col>
            </a-row>
          </div>

          <!-- 已就业-其他就业 -->
          <div v-if="form.employmentStatus === '已就业' && form.employmentType === '其他就业'">
            <a-row :gutter="16">
              <a-col :span="12"><a-form-item label="其他就业">
                <a-select v-model:value="form.otherEmployment" :options="OTHER_EMPLOYMENT" placeholder="请选择" />
              </a-form-item></a-col>
            </a-row>
          </div>

          <!-- 已就业-特殊就业 -->
          <div v-if="form.employmentStatus === '已就业' && form.employmentType === '特殊就业'">
            <a-row :gutter="16">
              <a-col :span="12"><a-form-item label="特殊就业">
                <a-select v-model:value="form.specialEmployment" :options="SPECIAL_EMPLOYMENT" placeholder="请选择" />
              </a-form-item></a-col>
            </a-row>
          </div>

          <!-- 已就业-其他情况 -->
          <div v-if="form.employmentStatus === '已就业' && form.employmentType === '其他情况'">
            <a-row :gutter="16">
              <a-col :span="12"><a-form-item label="其他情况">
                <a-select v-model:value="form.otherSituation" :options="OTHER_SITUATION" placeholder="请选择" />
              </a-form-item></a-col>
            </a-row>
          </div>

          <!-- 未就业 -->
          <div v-if="form.employmentStatus === '未就业'">
            <a-row :gutter="16">
              <a-col :span="12"><a-form-item label="未就业原因" name="unemployedReason">
                <a-input v-model:value="form.unemployedReason" placeholder="请输入未就业原因" />
              </a-form-item></a-col>
              <a-col :span="12"><a-form-item label="有无就业意愿" name="employmentWillingness">
                <a-select v-model:value="form.employmentWillingness" :options="HAVE_NONE" placeholder="请选择" />
              </a-form-item></a-col>
              <a-col :span="12"><a-form-item label='是否提供"1151"服务' name="provide1151Service">
                <a-select v-model:value="form.provide1151Service" :options="YES_NO" placeholder="请选择" />
              </a-form-item></a-col>
              <a-col :span="24"><a-form-item label="就业服务需求">
                <a-select v-model:value="form.employmentServiceNeeds" :options="SERVICE_DEMANDS" mode="multiple" placeholder="可多选" allow-clear />
              </a-form-item></a-col>
              <a-col :span="24"><a-form-item label="已接受就业服务">
                <a-select v-model:value="form.receivedServices" :options="RECEIVED_SERVICES" mode="multiple" placeholder="可多选" allow-clear />
              </a-form-item></a-col>
              <a-col :span="24"><a-form-item label="推荐单位及岗位" name="recommendUnitPosition">
                <a-input v-model:value="form.recommendUnitPosition" />
              </a-form-item></a-col>
            </a-row>
          </div>
        </div>

        <!-- ========== 区域四：备注 ========== -->
        <div class="section-block">
          <div class="section-title">备注</div>
          <a-row :gutter="16">
            <a-col :span="24"><a-form-item :wrapper-col="{ span: 24 }">
              <a-textarea v-model:value="form.remarks" :rows="3" placeholder="请输入备注" />
            </a-form-item></a-col>
          </a-row>
        </div>

        <!-- 保存按钮 -->
        <div v-if="mode === 'add' || mode === 'edit'" style="text-align: right; margin-top: 8px;">
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
  import { updateGraduate, addGraduate } from '@/api/graduate'

  const props = defineProps({
    open: Boolean,
    mode: { type: String, default: 'view' },
    record: { type: Object, default: () => ({}) }
  })
  const emit = defineEmits(['update:open', 'saved'])

  const form = reactive({})
  const saving = ref(false)
  const addMonth = ref(null)
  const addDay = ref(null)

  const addServiceTime = () => {
    if (addMonth.value === null || addDay.value === null) return
    const v = `${addMonth.value}月${addDay.value}日`
    if (!form.serviceTime) form.serviceTime = []
    if (!form.serviceTime.includes(v)) {
      form.serviceTime.push(v)
    }
    addMonth.value = null
    addDay.value = null
  }

  const removeServiceTime = (idx) => {
    form.serviceTime.splice(idx, 1)
  }

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

  const YES_NO = [
    { value: '是', label: '是' },
    { value: '否', label: '否' }
  ]
  const HAVE_NONE = [
    { value: '有', label: '有' },
    { value: '无', label: '无' }
  ]

  const OTHER_EMPLOYMENT = [
    { value: '个体经营', label: '个体经营' },
    { value: '灵活就业', label: '灵活就业' },
    { value: '公益岗位', label: '公益岗位' },
    { value: '自主创业', label: '自主创业' }
  ]
  const SPECIAL_EMPLOYMENT = [
    { value: '社区岗位', label: '社区岗位' },
    { value: '基层项目', label: '基层项目' },
    { value: '科研助理', label: '科研助理' }
  ]
  const OTHER_SITUATION = [
    { value: '参军', label: '参军' },
    { value: '出国', label: '出国' },
    { value: '升学', label: '升学' }
  ]

  const SERVICE_DEMANDS = [
    { value: '失业登记需求',     label: '失业登记需求' },
    { value: '困难认定需求',     label: '困难认定需求' },
    { value: '职业指导需求',     label: '职业指导需求' },
    { value: '求职需求',         label: '求职需求' },
    { value: '技能培训需求',     label: '技能培训需求' },
    { value: '创业服务需求',     label: '创业服务需求' },
    { value: '就业见习需求',     label: '就业见习需求' },
    { value: '其他需求',         label: '其他需求' }
  ]
  const RECEIVED_SERVICES = [
    { value: '失业登记',         label: '失业登记' },
    { value: '重点帮扶',         label: '重点帮扶' },
    { value: '求职登记',         label: '求职登记' },
    { value: '职业指导',         label: '职业指导' },
    { value: '创业培训',         label: '创业培训' },
    { value: '就业见习',         label: '就业见习' },
    { value: '就业援助',         label: '就业援助' },
    { value: '岗位推荐',         label: '岗位推荐' },
    { value: '技能培训',         label: '技能培训' },
    { value: '创业指导服务',      label: '创业指导服务' }
  ]

  const empTypeOptions = ref([])

  const onStatusChange = (val) => {
    form.employmentType = undefined
    if (val === '已就业') {
      empTypeOptions.value = EMP_TYPE
    } else {
      empTypeOptions.value = []
      form.employmentServiceNeeds = undefined
      form.receivedServices = undefined
    }
  }
  const onTypeChange = (val) => {
    if (val !== '其他就业') form.otherEmployment = undefined
    if (val !== '特殊就业') form.specialEmployment = undefined
    if (val !== '其他情况') form.otherSituation = undefined
  }

  watch(() => props.open, (v) => {
    if (v) {
      Object.assign(form, JSON.parse(JSON.stringify(props.record || {})))
      if (form.employmentStatus === '已就业') {
        empTypeOptions.value = EMP_TYPE
      }
      if (!form.employmentServiceNeeds || form.employmentServiceNeeds.length === 0) form.employmentServiceNeeds = undefined
      if (!form.receivedServices || form.receivedServices.length === 0) form.receivedServices = undefined
    } else {
      Object.keys(form).forEach(k => delete form[k])
      empTypeOptions.value = []
    }
    addMonth.value = null
    addDay.value = null
  })

  const handleClose = () => emit('update:open', false)
  const handleSave = async () => {
    saving.value = true
    try {
      let res
      if (props.mode === 'add') {
        res = await addGraduate(form)
      } else {
        res = await updateGraduate(form.id, form)
      }
      if (res.data.success) {
        message.success(props.mode === 'add' ? '添加成功' : '保存成功')
        emit('saved')
        handleClose()
      } else {
        message.error(res.data.message || '操作失败')
      }
    } catch (e) {
      message.error(e?.response?.data?.message || e?.message || '网络错误，请稍后重试')
    } finally {
      saving.value = false
    }
  }
  </script>

  <style scoped>
  .section-block {
    border: 1px solid #f0f0f0;
    border-radius: 4px;
    padding: 16px 16px 8px;
    margin-bottom: 16px;
    background: #fafafa;
  }
  .section-title {
    font-weight: 600;
    font-size: 14px;
    color: #333;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 2px solid #1890ff;
    display: inline-block;
  }
  .tag-input-wrapper {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 6px;
    border: 1px solid #d9d9d9;
    border-radius: 6px;
    padding: 4px 8px;
    min-height: 38px;
    background: #fff;
    cursor: text;
  }
  .tag-input-wrapper:focus-within {
    border-color: #1890ff;
    box-shadow: 0 0 0 2px rgba(24,144,255,0.1);
  }
  .time-tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    background: #e6f7ff;
    color: #1890ff;
    border: 1px solid #91d5ff;
    border-radius: 4px;
    padding: 2px 8px;
    font-size: 13px;
  }
  .time-tag-close {
    cursor: pointer;
    font-size: 14px;
    color: #8cc8ff;
    line-height: 1;
  }
  .time-tag-close:hover {
    color: #1890ff;
  }
  .time-add-row {
    display: flex;
    align-items: center;
    gap: 4px;
  }
  .time-num-input {
    width: 48px;
    text-align: center;
  }
  .time-unit {
    color: #666;
    font-size: 13px;
  }
  </style>
