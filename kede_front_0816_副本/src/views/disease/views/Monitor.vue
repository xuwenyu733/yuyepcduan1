<template>
  <div class="monitor-page">
    <div class="kpi-grid">
      <div class="kpi-card page-card">
        <div class="kpi-label">活跃病例</div>
        <div class="kpi-val">{{ summary.active || 0 }}</div>
        <div class="kpi-sub">诊断中 + 治疗中</div>
      </div>
      <div class="kpi-card page-card">
        <div class="kpi-label">紧急病例</div>
        <div class="kpi-val danger">{{ summary.critical || 0 }}</div>
        <div class="kpi-sub">需立即干预</div>
      </div>
      <div class="kpi-card page-card">
        <div class="kpi-label">治疗中</div>
        <div class="kpi-val">{{ summary.treating || 0 }}</div>
        <div class="kpi-sub">执行治疗方案</div>
      </div>
      <div class="kpi-card page-card">
        <div class="kpi-label">已治愈</div>
        <div class="kpi-val success">{{ summary.resolved || 0 }}</div>
        <div class="kpi-sub">累计 {{ summary.total || 0 }} 条</div>
      </div>
    </div>

    <div class="page-card table-card alarm-card">
      <div class="table-head">
        <span class="table-title">AI 报警（Mock）</span>
        <el-button size="small" :loading="alarmLoading" @click="loadAlarms">刷新</el-button>
      </div>
      <el-table v-loading="alarmLoading" :data="alarms" size="medium" empty-text="暂无 AI 报警">
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
            {{ row.alarmStatus === 'open' ? '未确认' : '已确认' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.alarmStatus === 'open'"
              type="text"
              @click="ackAlarm(row)"
            >确认</el-button>
            <span v-else class="muted">—</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="page-card table-card">
      <div class="table-head">
        <span class="table-title">病例监测列表</span>
        <el-button type="primary" size="small" @click="$router.push('/diagnosis')">新建诊断</el-button>
      </div>
      <el-table v-loading="loading" :data="pagedRecords" size="medium">
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
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="goDiagnosis(row)">查看</el-button>
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
  </div>
</template>

<script>
import { fetchSummary, fetchDiagnosisRecords, fetchAiAlarms, ackAiAlarm } from '../api'

export default {
  name: 'Monitor',
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
      try {
        const [summaryRes, recordsRes] = await Promise.all([
          fetchSummary(),
          fetchDiagnosisRecords({})
        ])
        this.summary = summaryRes.data
        this.records = recordsRes.data
        await this.loadAlarms()
      } finally {
        this.loading = false
      }
    },
    async loadAlarms() {
      this.alarmLoading = true
      try {
        const res = await fetchAiAlarms({})
        this.alarms = res.data || []
      } catch (e) {
        this.alarms = []
      } finally {
        this.alarmLoading = false
      }
    },
    async ackAlarm(row) {
      await ackAiAlarm(row.id)
      this.$message.success('已确认报警')
      await this.loadAlarms()
    },
    goDiagnosis(row) {
      this.$router.push({ path: '/diagnosis', query: { caseId: row.id } })
    }
  }
}
</script>

<style scoped>
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.kpi-card {
  padding: 20px 24px;
}

.kpi-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.kpi-val {
  font-size: 30px;
  font-weight: 600;
  color: var(--ink);
  line-height: 1.2;
}

.kpi-val.danger { color: #ff4d4f; }
.kpi-val.success { color: #52c41a; }

.kpi-sub {
  margin-top: 8px;
  font-size: 13px;
  color: var(--text-secondary);
}

.table-card {
  padding: 0;
  overflow: hidden;
}

.alarm-card {
  margin-bottom: 16px;
}

.prio.critical,
.prio.urgent,
.prio.high { color: #ff4d4f; }
.prio.medium { color: #faad14; }
.prio.low { color: #52c41a; }

.muted { color: var(--text-secondary); }

.table-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border);
}

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
}

.table-card >>> .el-table {
  border: none;
}

.pager {
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end;
}

.status-dot {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.status-dot::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary);
}

.status-dot.diagnosing::before,
.status-dot.treating::before { background: var(--primary); }
.status-dot.resolved::before { background: #52c41a; }
.status-dot.pending::before { background: #faad14; }

@media (max-width: 1100px) {
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
