<template>
  <div class="monitor-page">
    <header class="page-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">AI报警监控</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">实时查看 AI 报警与病例状态，快速确认预警并进入协同诊断</p>
    </header>

    <div class="page-body">
      <div class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-label">活跃病例</div>
          <div class="kpi-val">{{ summary.active || 0 }}</div>
          <div class="kpi-sub">诊断中 + 治疗中</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">紧急病例</div>
          <div class="kpi-val danger">{{ summary.critical || 0 }}</div>
          <div class="kpi-sub">需立即干预</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">治疗中</div>
          <div class="kpi-val">{{ summary.treating || 0 }}</div>
          <div class="kpi-sub">执行治疗方案</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">已治愈</div>
          <div class="kpi-val success">{{ summary.resolved || 0 }}</div>
          <div class="kpi-sub">累计 {{ summary.total || 0 }} 条</div>
        </div>
      </div>

      <section class="module-card">
        <div class="module-head">
          <div>
            <h2 class="module-title">AI 报警</h2>
            <p class="module-desc">基于监测数据的智能预警（Mock）</p>
          </div>
          <button type="button" class="btn-ghost" :disabled="alarmLoading" @click="loadAlarms">
            <i class="el-icon-refresh" :class="{ spinning: alarmLoading }" />
            刷新
          </button>
        </div>
        <div v-loading="alarmLoading" class="table-wrap">
          <el-table :data="alarms" size="medium" empty-text="暂无 AI 报警">
            <el-table-column prop="alarmTitle" label="标题" min-width="180" />
            <el-table-column prop="poolCode" label="池号" width="80" />
            <el-table-column prop="priority" label="优先级" width="90">
              <template slot-scope="{ row }">
                <span class="prio" :class="row.priority">{{ priorityLabel(row.priority) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="alarmReason" label="原因" min-width="220" show-overflow-tooltip />
            <el-table-column prop="aiSuggestion" label="AI建议" min-width="180" show-overflow-tooltip />
            <el-table-column prop="alarmTime" label="时间" width="160" />
            <el-table-column prop="alarmStatus" label="状态" width="90">
              <template slot-scope="{ row }">
                <span :class="row.alarmStatus === 'open' ? 'status-open' : 'status-ack'">
                  {{ row.alarmStatus === 'open' ? '未确认' : '已确认' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template slot-scope="{ row }">
                <button type="button" class="link-btn" @click="goAlarmDiagnosis(row)">去诊断</button>
                <button
                  v-if="row.alarmStatus === 'open'"
                  type="button"
                  class="link-btn"
                  @click="ackAlarm(row)"
                >确认</button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </section>

      <section class="module-card">
        <div class="module-head">
          <div>
            <h2 class="module-title">病例监测列表</h2>
            <p class="module-desc">跟踪诊断与治疗中的病例进展</p>
          </div>
          <!-- 新建诊断暂不可用，按需求隐藏 -->
        </div>
        <div v-loading="loading" class="table-wrap">
          <el-table :data="pagedRecords" size="medium" empty-text="暂无病例">
            <el-table-column prop="suspectedDisease" label="病害名称" min-width="150">
              <template slot-scope="{ row }">{{ row.suspectedDisease || '待诊断' }}</template>
            </el-table-column>
            <el-table-column prop="pondCode" label="池号" width="80" />
            <el-table-column prop="category" label="品类" width="90" />
            <el-table-column prop="severityLabel" label="严重度" width="80" />
            <el-table-column prop="statusLabel" label="状态" width="100">
              <template slot-scope="{ row }">
                <span class="status-dot" :class="row.status">{{ row.statusLabel }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="updatedAt" label="更新时间" width="160" />
            <el-table-column label="操作" width="100" fixed="right">
              <template slot-scope="{ row }">
                <button type="button" class="link-btn" @click="goDiagnosis(row)">查看</button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pager">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="records.length"
              :page-size="pageSize"
              :current-page.sync="page"
            />
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { fetchSummary, fetchDiagnosisRecords, fetchAiAlarms, ackAiAlarm } from '@/api/fishDisease'
import { syncModuleAiAlarmsToMission } from '@/utils/alarmTaskBridge'
import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'

export default {
  name: 'Monitor',
  components: { MenuToggle, HeaderAuth },
  inject: {
    switchFishNav: { default: null }
  },
  data() {
    return {
      loading: false,
      alarmLoading: false,
      summary: {},
      records: [],
      alarms: [],
      page: 1,
      pageSize: 10
    }
  },
  computed: {
    pagedRecords() {
      const start = (this.page - 1) * this.pageSize
      return this.records.slice(start, start + this.pageSize)
    }
  },
  created() {
    this.init()
  },
  methods: {
    priorityLabel(p) {
      return ({ critical: '紧急', urgent: '紧急', high: '高', medium: '中', low: '低' }[p] || p)
    },
    async init() {
      this.loading = true
      const summaryP = fetchSummary()
        .then(res => { this.summary = (res && res.data) || {} })
        .catch(() => { this.summary = {} })
      const recordsP = fetchDiagnosisRecords({})
        .then(res => { this.records = (res && res.data) || [] })
        .catch(() => { this.records = [] })
      try {
        await Promise.all([summaryP, recordsP, this.loadAlarms()])
      } finally {
        this.loading = false
      }
    },
    async loadAlarms() {
      this.alarmLoading = true
      try {
        const res = await fetchAiAlarms({})
        this.alarms = res.data || []
        // 高危鱼病 AI 预警同步到任务与计划，供他人接收执行
        syncModuleAiAlarmsToMission(this.alarms, {
          prefix: 'FD-AI-',
          source: 'fish_disease',
          category: 'fish'
        }).catch(err => console.warn('fish disease → mission', err))
      } catch (e) {
        this.alarms = []
      } finally {
        this.alarmLoading = false
      }
    },
    async ackAlarm(row) {
      try {
        await ackAiAlarm(row.id)
        this.$message.success('已确认报警')
        await this.loadAlarms()
      } catch (e) {
        this.$message.error((e && e.message) || '确认失败')
      }
    },
    goNewDiagnosis() {
      if (typeof this.switchFishNav === 'function') {
        this.switchFishNav('diagnosis')
        return
      }
      this.$router.push('/diagnosis')
    },
    goAlarmDiagnosis(row) {
      const pondCode = row.poolCode || row.pondCode || row.pool_code || ''
      if (typeof this.switchFishNav === 'function') {
        this.switchFishNav('diagnosis', pondCode ? { pondCode } : {})
        return
      }
      this.$router.push({
        path: '/fish-disease',
        query: { tab: 'diagnosis', ...(pondCode ? { pondCode } : {}) }
      })
    },
    goDiagnosis(row) {
      if (typeof this.switchFishNav === 'function') {
        this.switchFishNav('diagnosis', { caseId: row.id })
        return
      }
      this.$router.push({ path: '/diagnosis', query: { caseId: row.id } })
    }
  }
}
</script>

<style scoped>
.monitor-page {
  min-height: 100vh;
  background: #0a1520;
  position: relative;
}

.page-header {
  background: #0f1a26;
  padding: 28px 40px 20px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.header-title-row {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}

.header-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #e8edf2;
  line-height: 1.25;
}

.header-desc {
  margin: 10px 0 0;
  font-size: 14px;
  color: #7c8fa1;
  line-height: 1.5;
}

.page-body {
  padding: 0 40px 48px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.kpi-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 16px;
  padding: 20px 24px;
}

.kpi-label {
  font-size: 14px;
  color: #7c8fa1;
  margin-bottom: 8px;
}

.kpi-val {
  font-size: 30px;
  font-weight: 600;
  color: #e8edf2;
  line-height: 1.2;
}

.kpi-val.danger { color: #ff7875; }
.kpi-val.success { color: #73d13d; }

.kpi-sub {
  margin-top: 8px;
  font-size: 13px;
  color: #5a6b7d;
}

.module-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 16px;
  padding: 24px 28px;
  overflow: hidden;
}

.module-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.module-title {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 600;
  color: #e8edf2;
}

.module-desc {
  margin: 0;
  font-size: 14px;
  color: #7c8fa1;
  line-height: 1.5;
}

.btn-primary {
  height: 36px;
  padding: 0 16px;
  border: none;
  border-radius: 10px;
  background: #409eff;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  flex-shrink: 0;
}

.btn-primary:hover {
  background: #66b1ff;
}

.btn-ghost {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 14px;
  border: 1px solid rgba(124, 143, 161, 0.28);
  border-radius: 10px;
  background: transparent;
  color: #e8edf2;
  font-size: 14px;
  cursor: pointer;
  flex-shrink: 0;
}

.btn-ghost:hover:not(:disabled) {
  background: rgba(64, 158, 255, 0.1);
  border-color: rgba(64, 158, 255, 0.45);
}

.btn-ghost:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinning {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.table-wrap >>> .el-table {
  border: none;
}

.link-btn {
  border: none;
  background: transparent;
  color: #409eff;
  font-size: 13px;
  cursor: pointer;
  padding: 0;
}

.link-btn:hover {
  color: #66b1ff;
}

.prio.critical,
.prio.urgent,
.prio.high { color: #ff7875; font-weight: 600; }
.prio.medium { color: #faad14; font-weight: 600; }
.prio.low { color: #73d13d; }

.status-open { color: #faad14; }
.status-ack { color: #5a6b7d; }
.muted { color: #5a6b7d; }

.pager {
  padding-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.status-dot {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #e8edf2;
}

.status-dot::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #409eff;
}

.status-dot.diagnosing::before,
.status-dot.treating::before { background: #409eff; }
.status-dot.resolved::before { background: #73d13d; }
.status-dot.pending::before { background: #faad14; }

@media (max-width: 1100px) {
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
  .page-header,
  .page-body { padding-left: 20px; padding-right: 20px; }
}

@media (max-width: 640px) {
  .kpi-grid { grid-template-columns: 1fr; }
  .header-title { font-size: 22px; }
  .module-head { flex-direction: column; }
}
</style>
