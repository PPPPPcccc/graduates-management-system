<!-- components/BatchAddModal.vue -->
<template>
  <a-modal
    :open="open"
    title="批量导入毕业生"
    width="1200px"
    :footer="null"
    :mask-closable="false"
    @cancel="handleClose"
    :destroy-on-close="true"
  >
    <!-- 全局粘贴捕获层 -->
    <div ref="pasteLayer" tabindex="-1" style="outline:none;" @paste="onGlobalPaste">
      <div class="batch-tip">
        <span style="color:#faad14;font-weight:bold;">提示：</span>
        请先下载
        <a href="/空白表头.xlsx" download style="color:#1890ff;">空白表头.xlsx</a>
        对照格式，或直接将 Excel 中的数据
        <b>整块复制粘贴</b>到下方表格中（包含表头行），系统将自动识别列名并导入。
        如需手动输入，请直接点击单元格进行编辑。
      </div>

      <!-- 固定表头行 -->
      <div class="grid-wrapper">
        <table class="excel-grid">
          <colgroup>
            <col v-for="(_, i) in HEADERS" :key="i" :style="{ width: colWidths[i] + 'px', minWidth: colWidths[i] + 'px' }">
          </colgroup>
          <thead>
            <tr class="header-row">
              <th v-for="(h, i) in HEADERS" :key="i">
                <span class="header-text">{{ h }}</span>
                <span class="row-num">{{ i + 1 }}</span>
              </th>
            </tr>
          </thead>
          <tbody ref="gridBody">
            <tr v-for="(row, ri) in gridData" :key="ri">
              <td v-for="(cell, ci) in row" :key="ci">
                <input
                  :ref="el => setCellRef(el, ri, ci)"
                  :value="cell"
                  class="cell-input"
                  @input="onCellInput(ri, ci, $event)"
                  @keydown.enter.prevent="focusNextCell(ri, ci, $event)"
                  @keydown.tab.prevent="focusNextCell(ri, ci, $event)"
                  @keydown.up.prevent="focusPrevRow(ri, ci)"
                  @keydown.down.prevent="focusNextRow(ri, ci)"
                  @keydown.escape="handleClose"
                />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 粘贴统计 & 状态 -->
      <div class="paste-status" v-if="pasteStatus">
        <a-tag :color="pasteStatus.color">{{ pasteStatus.text }}</a-tag>
      </div>
    </div>

    <div style="text-align: right; margin-top: 12px;">
      <a-space>
        <a-button @click="handleClose">取消</a-button>
        <a-button @click="handleReset">清空表格</a-button>
        <a-button type="primary" :loading="submitting" :disabled="validRowCount === 0" @click="handleSubmit">
          提交导入 ({{ validRowCount }} 条有效数据)
        </a-button>
      </a-space>
    </div>

    <!-- 失败详情弹窗 -->
    <a-modal
      v-model:open="failModal.open"
      title="导入失败详情"
      width="700px"
      :footer="null"
      :mask-closable="false"
    >
      <a-alert
        type="warning"
        show-icon
        :message="`共 ${failModal.list.length} 条数据导入失败`"
        style="margin-bottom: 16px;"
      />
      <a-table
        :columns="failColumns"
        :data-source="failModal.list"
        :pagination="{ pageSize: 10 }"
        size="small"
        bordered
      />
    </a-modal>
  </a-modal>
</template>

<script setup>
import { reactive, ref, computed, nextTick, watch } from 'vue'
import { message } from 'ant-design-vue'
import { batchAddGraduates } from '@/api/graduate'

const props = defineProps({ open: Boolean })
const emit = defineEmits(['update:open', 'saved'])

const MAX_ROWS = 200

// 所有可能的表头别名映射（用户Excel列名 -> 标准列名）
const HEADER_ALIASES = {
  '姓名': '姓名',
  '身份证号码': '身份证号码',
  '身份证号': '身份证号码',
  '学历': '学历',
  '毕业院校': '毕业院校',
  '毕业学校': '毕业院校',
  '毕业日期': '毕业日期',
  '专业': '专业',
  '联系电话': '联系电话',
  '电话': '联系电话',
  '户籍地址': '户籍地址',
  '常住详细地址': '常住详细地址',
  '常住地址': '常住详细地址',
  '调查人': '调查人',
  '调查日期': '调查日期',
  '调查方式': '调查方式',
  '就业地': '就业地',
  '单位名称': '单位名称',
  '单位性质': '单位性质',
  '专业是否对口': '专业是否对口',
  '是否对口': '专业是否对口',
  '其他就业': '其他就业',
  '特殊就业': '特殊就业',
  '其他情况': '其他情况',
  '未就业原因': '未就业原因',
  '有无就业意愿': '有无就业意愿',
  '就业意愿': '有无就业意愿',
  '是否提供1151服务': '是否提供"1151"服务',
  '就业服务需求(可多选)': '就业服务需求(可多选)',
  '就业服务需求': '就业服务需求(可多选)',
  '服务时间': '服务时间',
  '已接受就业服务(可多选)': '已接受就业服务(可多选)',
  '已接受就业服务': '已接受就业服务(可多选)',
  '推荐单位及岗位名称': '推荐单位及岗位名称',
  '推荐单位': '推荐单位及岗位名称',
  '备注': '备注'
}

const HEADERS = [
  '姓名', '身份证号码', '学历', '毕业院校', '毕业日期', '专业', '联系电话',
  '户籍地址', '常住详细地址', '调查人', '调查日期', '调查方式', '就业地',
  '单位名称', '单位性质', '专业是否对口', '其他就业', '特殊就业', '其他情况',
  '未就业原因', '有无就业意愿', '是否提供"1151"服务', '就业服务需求(可多选)',
  '服务时间', '已接受就业服务(可多选)', '推荐单位及岗位名称', '备注'
]

const colWidths = [
  80, 160, 100, 150, 100, 120, 110,
  180, 180, 80, 100, 90, 100,
  140, 90, 100, 90, 90, 90,
  110, 100, 100, 110,
  100, 110, 140, 100
]

// 2D 网格数据 [row][col]
const gridData = ref([])
const cellRefs = ref([])
const gridBody = ref(null)
const pasteLayer = ref(null)
const pasteStatus = ref(null)
let pasteTimer = null

const setCellRef = (el, ri, ci) => {
  if (!cellRefs.value[ri]) cellRefs.value[ri] = []
  cellRefs.value[ri][ci] = el
}

// 初始化空行
const initGrid = () => {
  gridData.value = Array.from({ length: 20 }, () => Array(HEADERS.length).fill(''))
  cellRefs.value = []
  pasteStatus.value = null
}

// 自动扩展行数
const expandRows = (needed) => {
  while (gridData.value.length < needed) {
    gridData.value.push(Array(HEADERS.length).fill(''))
  }
}

// 全局粘贴拦截
const onGlobalPaste = (e) => {
  e.preventDefault()
  const clipboardData = e.clipboardData || window.clipboardData
  if (!clipboardData) return

  let text = clipboardData.getData('text/plain')
  if (!text) return

  // 兼容 Excel 复制出来的 Unicode 空格和制表符
  text = text.replace(/\u00A0/g, ' ').replace(/\u2003/g, ' ')

  const lines = text.split('\n').filter(l => l.trim().length > 0)
  if (lines.length === 0) return

  const splitLine = (line) => {
    if (line.includes('\t')) return line.split('\t')
    if (line.includes(',')) return line.split(',')
    return line.trim().split(/\s{2,}/)
  }

  // 尝试把第一行当作表头处理
  const firstLineCells = splitLine(lines[0])
  const firstIsHeader = HEADERS.some(h =>
    firstLineCells.some(c => HEADER_ALIASES[c.trim()] === h)
  )

  const startRow = 0
  let rowCount = firstIsHeader ? lines.length - 1 : lines.length
  expandRows(startRow + rowCount)

  let standardHeaders = null

  if (firstIsHeader) {
    standardHeaders = firstLineCells.map(c => {
      const t = c.trim().replace(/^["']|["']$/g, '')
      return HEADER_ALIASES[t] || null
    })
  }

  let pasted = 0
  let skipped = 0

  for (let i = 0; i < rowCount; i++) {
    const rawCells = splitLine(lines[firstIsHeader ? i + 1 : i])
    const targetRow = startRow + i

    if (standardHeaders) {
      standardHeaders.forEach((header, ci) => {
        if (header && rawCells[ci] !== undefined) {
          gridData.value[targetRow][ci] = rawCells[ci].trim().replace(/^["']|["']$/g, '')
          pasted++
        }
      })
    } else {
      rawCells.forEach((val, ci) => {
        if (ci < HEADERS.length) {
          gridData.value[targetRow][ci] = val.trim().replace(/^["']|["']$/g, '')
          pasted++
        } else {
          skipped++
        }
      })
    }
  }

  // 显示状态提示
  const dataRows = firstIsHeader ? rowCount : rowCount
  pasteStatus.value = {
    text: `已粘贴 ${dataRows} 行数据（${pasted} 个单元格）${skipped > 0 ? `，跳过 ${skipped} 个多余单元格` : ''}`,
    color: 'blue'
  }
  if (pasteTimer) clearTimeout(pasteTimer)
  pasteTimer = setTimeout(() => { pasteStatus.value = null }, 4000)
}

// 单元格输入
const onCellInput = (ri, ci, e) => {
  gridData.value[ri][ci] = e.target.value
}

// 回车/Tab 跳到下一个单元格
const focusNextCell = (ri, ci) => {
  const nextCi = ci + 1
  const nextRi = nextCi >= HEADERS.length ? ri + 1 : ri
  const targetCi = nextCi >= HEADERS.length ? 0 : nextCi
  expandRows(nextRi + 1)
  nextTick(() => {
    const el = cellRefs.value[nextRi]?.[targetCi]
    if (el) el.focus()
  })
}

const focusPrevRow = (ri, ci) => {
  if (ri <= 0) return
  nextTick(() => {
    const el = cellRefs.value[ri - 1]?.[ci]
    if (el) el.focus()
  })
}

const focusNextRow = (ri, ci) => {
  expandRows(ri + 2)
  nextTick(() => {
    const el = cellRefs.value[ri + 1]?.[ci]
    if (el) el.focus()
  })
}

// 弹窗打开时初始化
watch(() => props.open, (v) => {
  if (v) nextTick(() => {
    initGrid()
    pasteLayer.value?.focus()
  })
})

// 计算有效行数（至少姓名非空）
const validRowCount = computed(() => {
  return gridData.value.filter(row => row[0] && row[0].trim().length > 0).length
})

const handleReset = () => {
  initGrid()
  pasteStatus.value = null
}

const handleClose = () => emit('update:open', false)

const handleSubmit = async () => {
  if (validRowCount.value === 0) {
    message.warning('表格中没有有效数据行（姓名不能为空）')
    return
  }

  submitting.value = true
  try {
    const rows = gridData.value
      .filter(row => row[0] && row[0].trim().length > 0)
      .map(row => {
        const rowMap = {}
        row.forEach((val, ci) => {
          if (val && val.trim()) {
            rowMap[HEADERS[ci]] = val.trim()
          }
        })
        return rowMap
      })

    const res = await batchAddGraduates(rows)
    if (res.data.success) {
      const result = res.data.data
      const ok = result.successCount || 0
      const fail = result.failedCount || 0
      if (fail === 0) {
        message.success(`导入成功，共 ${ok} 条数据`)
        emit('saved')
        handleClose()
      } else {
        message.warning(`导入完成：${ok} 条成功，${fail} 条失败`)
        failModal.list = result.failed || []
        failModal.open = true
        emit('saved')
      }
    } else {
      message.error(res.data.message || '导入失败')
    }
  } finally {
    submitting.value = false
  }
}

const submitting = ref(false)
const failModal = reactive({ open: false, list: [] })
const failColumns = [
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '身份证号', dataIndex: 'idCard', key: 'idCard' },
  { title: '失败原因', dataIndex: 'reason', key: 'reason' }
]
</script>

<style scoped>
.batch-tip {
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 4px;
  padding: 10px 14px;
  margin-bottom: 12px;
  font-size: 13px;
  line-height: 1.6;
  color: #ad6800;
}
.grid-wrapper {
  overflow: auto;
  max-height: 480px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}
.excel-grid {
  border-collapse: collapse;
  table-layout: fixed;
  width: 100%;
}
.excel-grid thead .header-row th {
  background: #f0f5ff;
  border: 1px solid #d0d7de;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 2;
  text-align: left;
  user-select: none;
}
.header-text {
  display: block;
  padding: 6px 8px;
  font-size: 12px;
  font-weight: 600;
  color: #1f2328;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.row-num {
  display: block;
  padding: 0 8px 4px;
  font-size: 10px;
  color: #8b949e;
}
.excel-grid tbody tr:hover td {
  background: #f5f9ff;
}
.excel-grid tbody tr:last-child:hover td {
  background: #f0f5ff;
}
.excel-grid td {
  border: 1px solid #e0e6ed;
  padding: 0;
  height: 28px;
}
.cell-input {
  width: 100%;
  height: 28px;
  border: none;
  outline: none;
  background: transparent;
  padding: 0 6px;
  font-size: 12px;
  font-family: inherit;
  color: #24292f;
  box-sizing: border-box;
  display: block;
}
.cell-input:focus {
  background: #e6f4ff;
  outline: 2px solid #1677ff;
  outline-offset: -2px;
}
.paste-status {
  margin-top: 8px;
  min-height: 22px;
}
</style>
