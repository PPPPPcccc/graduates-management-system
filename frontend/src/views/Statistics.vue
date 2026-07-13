<!-- views/Statistics.vue -->
<template>
  <div class="statistics-page">
    <!-- KPI 卡片 -->
    <a-row :gutter="16" class="kpi-row">
      <a-col :span="6">
        <div class="kpi-card">
          <div class="kpi-label">毕业生总数</div>
          <div class="kpi-value">{{ stats.total || 0 }}</div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="kpi-card">
          <div class="kpi-label">已就业人数</div>
          <div class="kpi-value employed">{{ employedCount }}</div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="kpi-card">
          <div class="kpi-label">未就业人数</div>
          <div class="kpi-value unemployed">{{ unemployedCount }}</div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="kpi-card">
          <div class="kpi-label">就业率</div>
          <div class="kpi-value rate">{{ stats.employmentRate || 0 }}%</div>
        </div>
      </a-col>
    </a-row>

    <!-- 第一行：就业情况 + 学历 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">就业情况分布</div>
          <div ref="employmentPieRef" class="chart-container"></div>
        </div>
      </a-col>
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">学历分布</div>
          <div ref="educationPieRef" class="chart-container"></div>
        </div>
      </a-col>
    </a-row>

    <!-- 第二行：学历×就业情况 + 调查方式 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">学历与就业情况交叉分析</div>
          <div ref="eduEmpBarRef" class="chart-container"></div>
        </div>
      </a-col>
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">调查方式分布</div>
          <div ref="investigationPieRef" class="chart-container"></div>
        </div>
      </a-col>
    </a-row>

    <!-- 第三行：就业意愿 + 就业类型 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">未就业群体就业意愿分布</div>
          <div ref="willingPieRef" class="chart-container"></div>
        </div>
      </a-col>
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">已就业群体就业类型分布</div>
          <div ref="empTypePieRef" class="chart-container"></div>
        </div>
      </a-col>
    </a-row>

    <!-- 第四行：专业对口 + 1151服务 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">专业是否对口（单位就业）</div>
          <div ref="majorMatchedPieRef" class="chart-container"></div>
        </div>
      </a-col>
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">未就业群体"1151"服务提供情况</div>
          <div ref="service1151PieRef" class="chart-container"></div>
        </div>
      </a-col>
    </a-row>

    <!-- 第五行：服务需求 + 已接受服务 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">就业服务需求分布</div>
          <div ref="demandBarRef" class="chart-container"></div>
        </div>
      </a-col>
      <a-col :span="12">
        <div class="chart-card">
          <div class="chart-title">已接受就业服务分布</div>
          <div ref="receivedBarRef" class="chart-container"></div>
        </div>
      </a-col>
    </a-row>

    <!-- 第六行：服务时间趋势 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :span="24">
        <div class="chart-card">
          <div class="chart-title">服务时间月度趋势</div>
          <div ref="timeLineRef" class="chart-container-wide"></div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { fetchStatistics } from '@/api/graduate'

const stats = ref({})
const loading = ref(true)

// KPI 辅助数据
const employedCount = computed(() => {
  const arr = stats.value.employmentStatus || []
  return arr.find(x => x.label === '已就业')?.value || 0
})
const unemployedCount = computed(() => {
  const arr = stats.value.employmentStatus || []
  return arr.find(x => x.label === '未就业')?.value || 0
})

// ECharts 实例
const charts = []

function makePie(refEl, data, colors) {
  const el = refEl.value
  if (!el) return null
  const instance = echarts.init(el)
  instance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: 16, top: 'center', textStyle: { fontSize: 12 } },
    color: colors || ['#2c5282', '#e88a4a', '#888'],
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      label: { show: true, formatter: '{b}\n{c}人' },
      data: data.map(d => ({ name: d.label, value: d.value }))
    }]
  })
  charts.push(instance)
  return instance
}

function makeBarH(refEl, data, colors) {
  const el = refEl.value
  if (!el) return null
  const instance = echarts.init(el)
  instance.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 8, right: 24, top: 8, bottom: 30 },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: data.map(d => d.label), axisLabel: { fontSize: 12 } },
    colorBy: 'series',
    series: [{
      type: 'bar',
      data: data.map((d, i) => ({
        value: d.value,
        itemStyle: { color: (colors && colors[i]) || `hsl(210, 60%, ${50 - i * 5}%)` }
      })),
      label: { show: true, position: 'right', formatter: '{c}' }
    }]
  })
  charts.push(instance)
  return instance
}

function makeStackedBar(refEl, rawData) {
  const el = refEl.value
  if (!el) return null
  const instance = echarts.init(el)

  const eduSet = [...new Set(rawData.map(d => d.edu))].sort()
  const empSet = [...new Set(rawData.map(d => d.emp))]

  const series = empSet.map(emp => ({
    name: emp,
    type: 'bar',
    stack: 'total',
    data: eduSet.map(edu => {
      const item = rawData.find(r => r.edu === edu && r.emp === emp)
      return item ? item.count : 0
    }),
    label: { show: false }
  }))

  instance.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: params => {
      let tip = `${params[0].name}<br/>`
      params.forEach(p => { tip += `${p.marker} ${p.seriesName}: ${p.value}<br/>` })
      return tip
    }},
    legend: { bottom: 0, textStyle: { fontSize: 12 } },
    grid: { left: 8, right: 16, top: 8, bottom: 40 },
    xAxis: { type: 'category', data: eduSet, axisLabel: { rotate: 15, fontSize: 11 } },
    yAxis: { type: 'value', name: '人数' },
    color: ['#2c5282', '#e88a4a', '#52b788', '#9b59b6', '#888'],
    series
  })
  charts.push(instance)
  return instance
}

function makeLine(refEl, data) {
  const el = refEl.value
  if (!el) return null
  const instance = echarts.init(el)
  const months = data.map(d => `${d.label}月`)
  instance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 8, right: 16, top: 8, bottom: 36 },
    xAxis: { type: 'category', data: months, axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', name: '服务次数', minInterval: 1 },
    series: [{
      type: 'line',
      data: data.map(d => d.value),
      smooth: true,
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(44,82,130,0.3)' },
        { offset: 1, color: 'rgba(44,82,130,0.05)' }
      ])},
      lineStyle: { color: '#2c5282', width: 2 },
      itemStyle: { color: '#2c5282' },
      label: { show: true, position: 'top', fontSize: 11 }
    }]
  })
  charts.push(instance)
  return instance
}

// Chart refs
const employmentPieRef = ref()
const educationPieRef = ref()
const eduEmpBarRef = ref()
const investigationPieRef = ref()
const willingPieRef = ref()
const empTypePieRef = ref()
const majorMatchedPieRef = ref()
const service1151PieRef = ref()
const demandBarRef = ref()
const receivedBarRef = ref()
const timeLineRef = ref()

const PIE_COLORS   = ['#2c5282', '#e88a4a', '#52b788', '#9b59b6', '#e74c3c', '#f39c12']
const BAR_COLORS_H = ['#2c5282','#3182ce','#63b3ed','#90cdf4','#bee3f8','#c0d9f0','#d0e0f0']

async function loadData() {
  loading.value = true
  try {
    const res = await fetchStatistics()
    if (res.data.success) {
      stats.value = res.data.data
      renderCharts()
    }
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  const s = stats.value

  if (employmentPieRef.value)
    makePie(employmentPieRef, s.employmentStatus || [], PIE_COLORS)

  if (educationPieRef.value)
    makeBarH(educationPieRef, s.education || [], BAR_COLORS_H)

  if (eduEmpBarRef.value && (s.educationEmployment || []).length > 0)
    makeStackedBar(eduEmpBarRef, s.educationEmployment)

  if (investigationPieRef.value)
    makePie(investigationPieRef, s.investigationMethod || [], ['#2c5282', '#52b788'])

  if (willingPieRef.value)
    makePie(willingPieRef, s.employmentWillingness || [], ['#e88a4a', '#52b788', '#888'])

  if (empTypePieRef.value)
    makePie(empTypePieRef, s.employmentType || [], PIE_COLORS)

  if (majorMatchedPieRef.value)
    makePie(majorMatchedPieRef, s.majorMatched || [], ['#52b788', '#e88a4a', '#888'])

  if (service1151PieRef.value)
    makePie(service1151PieRef, s.provide1151Service || [], ['#52b788', '#e88a4a', '#888'])

  if (demandBarRef.value)
    makeBarH(demandBarRef, s.serviceDemand || [], BAR_COLORS_H)

  if (receivedBarRef.value)
    makeBarH(receivedBarRef, s.acceptedService || [], BAR_COLORS_H)

  if (timeLineRef.value)
    makeLine(timeLineRef, s.serviceTimeMonth || [])
}

function handleResize() {
  charts.forEach(c => c?.resize())
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c?.dispose())
})
</script>

<style scoped>
.statistics-page {
  padding: 4px 0;
}

.kpi-row {
  margin-bottom: 16px;
}

.kpi-card {
  background: linear-gradient(135deg, #1e3a5f 0%, #2c5282 100%);
  border-radius: 8px;
  padding: 20px 16px;
  text-align: center;
  color: white;
}

.kpi-label {
  font-size: 13px;
  opacity: 0.85;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 28px;
  font-weight: bold;
}

.kpi-value.employed { color: #52b788; }
.kpi-value.unemployed { color: #f39c12; }
.kpi-value.rate { color: #90cdf4; }

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px;
  height: 320px;
  display: flex;
  flex-direction: column;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  border-bottom: 2px solid #2c5282;
  display: inline-block;
  padding-bottom: 4px;
}

.chart-container {
  flex: 1;
  min-height: 0;
}

.chart-container-wide {
  flex: 1;
  min-height: 200px;
}
</style>
