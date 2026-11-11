<template>
  <div>
    <div class="page-head">
      <div>
        <h2 class="section-title">记录查询</h2>
      </div>
    </div>

    <div class="page-card search-card">
      <div class="card-title">检索条件</div>

      <div class="sub-tabs">
        <button type="button" class="sub-tab" :class="{ active: mode === 'basic' }" @click="switchMode('basic')">基础检索</button>
        <button type="button" class="sub-tab" :class="{ active: mode === 'advanced' }" @click="switchMode('advanced')">高级检索</button>
      </div>

      <el-form label-position="top" class="query-form" @submit.native.prevent>

        <!-- 基础检索 -->
        <el-row v-if="mode === 'basic'" :gutter="16">
          <el-col :xs="24" :sm="8">
            <el-form-item label="记录单号">
              <el-input v-model="filters.recordNo" placeholder="输入记录单号" clearable class="soft-input" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="品类">
              <el-select v-model="filters.category" clearable placeholder="全部品类" class="soft-input" style="width:100%">
                <el-option v-for="item in meta.categories" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="池号">
              <el-input v-model="filters.pondCode" placeholder="输入池号" clearable class="soft-input" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="状态">
              <el-select v-model="filters.status" clearable placeholder="全部状态" class="soft-input" style="width:100%">
                <el-option label="全部状态" value="" />
                <el-option label="已入池" value="entered" />
                <el-option label="优化中" value="optimizing" />
                <el-option label="已出池" value="exited" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="入池开始时间">
              <el-date-picker v-model="filters.startDate" type="date" value-format="yyyy-MM-dd" placeholder="年/月/日" class="soft-input" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="入池结束时间">
              <el-date-picker v-model="filters.endDate" type="date" value-format="yyyy-MM-dd" placeholder="年/月/日" class="soft-input" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 高级检索：仅死亡率 / 掉秤率区间 -->
        <el-row v-else :gutter="24" class="advanced-row">
          <el-col :xs="24" :md="12">
            <el-form-item label="死亡率范围 (%)">
              <div class="range-row">
                <el-input-number
                  v-model="filters.minMortality"
                  :min="0"
                  :max="100"
                  :step="0.01"
                  :precision="2"
                  :controls="true"
                  controls-position="right"
                  placeholder="最小值"
                  class="range-input"
                />
                <span class="range-sep">-</span>
                <el-input-number
                  v-model="filters.maxMortality"
                  :min="0"
                  :max="100"
                  :step="0.01"
                  :precision="2"
                  :controls="true"
                  controls-position="right"
                  placeholder="最大值"
                  class="range-input"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="掉秤率范围 (%)">
              <div class="range-row">
                <el-input-number
                  v-model="filters.minWeightLoss"
                  :min="0"
                  :max="100"
                  :step="0.01"
                  :precision="2"
                  :controls="true"
                  controls-position="right"
                  placeholder="最小值"
                  class="range-input"
                />
                <span class="range-sep">-</span>
                <el-input-number
                  v-model="filters.maxWeightLoss"
                  :min="0"
                  :max="100"
                  :step="0.01"
                  :precision="2"
                  :controls="true"
                  controls-position="right"
                  placeholder="最大值"
                  class="range-input"
                />
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="actions">
          <el-button class="btn-reset" @click="reset">重置</el-button>
          <el-button type="primary" @click="load">查询</el-button>
        </div>
      </el-form>
    </div>

    <div class="page-card" style="margin-top: 12px">
      <div class="result-head">
        <div>
          <div class="card-title">查询结果</div>
          <p class="card-sub">共 {{ list.length }} 条记录</p>
        </div>
        <div>
          <el-button icon="el-icon-document" @click="exportCsv">导出Excel</el-button>
          <el-button icon="el-icon-download" @click="exportPdfHint">导出PDF</el-button>
        </div>
      </div>
      <el-empty v-if="!loading && !list.length" description="未找到符合条件的记录" />
      <el-table v-else v-loading="loading" :data="list" border size="small">
        <el-table-column prop="recordNo" label="记录单号" min-width="140" />
        <el-table-column prop="category" label="品类" width="90" />
        <el-table-column prop="statusLabel" label="状态" width="90" />
        <el-table-column prop="source" label="来源" width="110" />
        <el-table-column prop="pondCode" label="池号" width="80" />
        <el-table-column prop="entryTime" label="入池时间" min-width="150" />
        <el-table-column prop="exitTime" label="出池时间" min-width="150" />
        <el-table-column prop="totalWeight" label="入池重量" width="100" />
        <el-table-column prop="exitWeight" label="出池重量" width="100" />
        <el-table-column prop="mortalityRate" label="死亡率%" width="90" />
        <el-table-column prop="weightLossRate" label="掉秤率%" width="90" />
      </el-table>
    </div>
  </div>
</template>

<script>
import { loadMergedRecords, fetchFlavorMetaSynced } from '@/utils/flavorPurifyBridge'

export default {
  name: 'RecordQuery',
  data() {
    return {
      mode: 'basic',
      loading: false,
      meta: { categories: [] },
      list: [],
      filters: this.emptyFilters()
    }
  },
  created() {
    this.init()
  },
  activated() {
    this.load()
  },
  methods: {
    emptyFilters() {
      return {
        recordNo: '',
        category: '',
        pondCode: '',
        status: '',
        startDate: '',
        endDate: '',
        minMortality: undefined,
        maxMortality: undefined,
        minWeightLoss: undefined,
        maxWeightLoss: undefined
      }
    },
    switchMode(mode) {
      if (this.mode === mode) return
      this.mode = mode
      this.filters = this.emptyFilters()
      this.load()
    },
    buildQuery() {
      if (this.mode === 'advanced') {
        return {
          minMortality: this.filters.minMortality,
          maxMortality: this.filters.maxMortality,
          minWeightLoss: this.filters.minWeightLoss,
          maxWeightLoss: this.filters.maxWeightLoss
        }
      }
      return {
        recordNo: this.filters.recordNo,
        category: this.filters.category,
        pondCode: this.filters.pondCode,
        status: this.filters.status,
        startDate: this.filters.startDate,
        endDate: this.filters.endDate
      }
    },
    async init() {
      const metaRes = await fetchFlavorMetaSynced()
      this.meta = metaRes.data
      await this.load()
    },
    async load() {
      this.loading = true
      try {
        const res = await loadMergedRecords(this.buildQuery())
        this.list = Array.isArray(res) ? res : (res && res.data) || []
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.filters = this.emptyFilters()
      this.load()
    },
    exportCsv() {
      if (!this.list.length) {
        this.$message.warning('暂无数据可导出')
        return
      }
      const header = ['记录单号', '品类', '状态', '来源', '池号', '入池时间', '出池时间', '入池重量', '出池重量', '死亡率', '掉秤率']
      const rows = this.list.map(i => [
        i.recordNo, i.category, i.statusLabel, i.source, i.pondCode,
        i.entryTime, i.exitTime || '', i.totalWeight, i.exitWeight, i.mortalityRate, i.weightLossRate
      ])
      const csv = [header, ...rows].map(r => r.map(c => `"${String(c == null ? '' : c).replace(/"/g, '""')}"`).join(',')).join('\n')
      const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `风味优化记录_${Date.now()}.csv`
      a.click()
      URL.revokeObjectURL(url)
      this.$message.success('已导出 CSV（可用 Excel 打开）')
    },
    escapeHtml(value) {
      return String(value == null ? '' : value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
    },
    exportPdfHint() {
      if (!this.list.length) {
        this.$message.warning('暂无数据可导出')
        return
      }
      const esc = this.escapeHtml
      // 生成可打印的简洁报表窗口，字段全部转义，避免 XSS
      const rows = this.list.map(i => `
        <tr>
          <td>${esc(i.recordNo)}</td>
          <td>${esc(i.category)}</td>
          <td>${esc(i.statusLabel)}</td>
          <td>${esc(i.pondCode)}</td>
          <td>${esc(i.entryTime)}</td>
          <td>${esc(i.exitTime)}</td>
          <td>${esc(i.totalWeight)}</td>
          <td>${esc(i.exitWeight)}</td>
        </tr>`).join('')
      const html = `<!DOCTYPE html><html><head><meta charset="utf-8"><title>风味优化记录</title>
        <style>body{font-family:sans-serif;padding:24px}table{border-collapse:collapse;width:100%;font-size:12px}
        th,td{border:1px solid #ccc;padding:6px 8px;text-align:left}th{background:#f5f5f5}
        h1{font-size:18px;margin:0 0 12px}</style></head><body>
        <h1>风味优化记录查询</h1>
        <table><thead><tr>
          <th>记录单号</th><th>品类</th><th>状态</th><th>池号</th>
          <th>入池时间</th><th>出池时间</th><th>入池重量</th><th>出池重量</th>
        </tr></thead><tbody>${rows}</tbody></table>
        <script>window.onload=function(){window.print()}<\/script>
        </body></html>`
      const w = window.open('', '_blank')
      if (!w) {
        this.$message.warning('浏览器拦截了弹窗，请允许后重试')
        return
      }
      w.document.write(html)
      w.document.close()
    }
  }
}
</script>

<style scoped>
.search-card {
  padding-bottom: 18px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #e8edf2;
  line-height: 1.4;
}

.card-sub {
  margin: 4px 0 14px;
  font-size: 13px;
  color: #b7c4d2;
  line-height: 1.4;
}

.sub-tabs {
  display: inline-flex;
  background: rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 18px;
}

.sub-tab {
  border: 1px solid transparent;
  background: transparent;
  padding: 8px 18px;
  border-radius: 8px;
  cursor: pointer;
  color: #b7c4d2;
  font-size: 14px;
  line-height: 1.2;
  transition: all 0.15s ease;
}

.sub-tab.active {
  background: #0f1a26;
  border-color: rgba(124, 143, 161, 0.25);
  color: #e8edf2;
  font-weight: 600;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.query-form >>> .el-form-item {
  margin-bottom: 16px;
}

.query-form >>> .el-form-item__label {
  padding: 0 0 8px;
  line-height: 1.2;
  color: #c5d0dc;
  font-weight: 500;
  font-size: 13px;
}

.advanced-row {
  margin-bottom: 4px;
}

.range-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.range-sep {
  color: #b7c4d2;
  flex-shrink: 0;
  font-size: 14px;
}

.range-input {
  flex: 1;
  width: 100%;
}

/* 浅灰圆角数字框，贴合稿面 */
.range-input >>> .el-input__inner,
.soft-input >>> .el-input__inner {
  background: #0f1a26;
  border-color: rgba(124, 143, 161, 0.22);
  border-radius: 8px;
  height: 40px;
  line-height: 40px;
  color: #e8edf2;
}

.range-input >>> .el-input__inner:focus,
.soft-input >>> .el-input__inner:focus {
  background: #0f1a26;
  border-color: #54907a;
}

.range-input >>> .el-input-number__increase,
.range-input >>> .el-input-number__decrease {
  background: #0f1a26;
  border-color: rgba(124, 143, 161, 0.22);
  width: 32px;
  color: #b7c4d2;
}

.range-input >>> .el-input-number__increase:hover,
.range-input >>> .el-input-number__decrease:hover {
  color: #54907a;
}

.range-input >>> .el-input-number.is-controls-right .el-input__inner {
  padding-right: 42px;
  padding-left: 12px;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 4px;
}

.btn-reset {
  border-radius: 8px;
  padding: 10px 20px;
  border-color: rgba(124, 143, 161, 0.25);
  color: #c5d0dc;
  background: #0f1a26;
}

.result-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}
</style>
