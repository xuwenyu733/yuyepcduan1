<template>
  <div class="app-container task-plan">
    <div class="page-head">
      <div>
        <h2 class="title">任务计划录入 <el-tag size="mini" type="warning" effect="dark">人工录入</el-tag></h2>
        <p class="sub">日 / 周 / 月 / 年计划 · 浏览器 localStorage 本地保存 · 用于手工补录任务</p>
      </div>
      <div class="head-actions">
        <el-button size="small" icon="el-icon-bell" @click="$router.push('/alert_monitor')">AI 报警</el-button>
        <el-button size="small" icon="el-icon-document" @click="$router.push('/keyshow/InspectionList')">智能巡检</el-button>
        <el-button size="small" icon="el-icon-s-order" @click="$router.push('/mission_planning')">协同计划</el-button>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="openCreate">新建计划</el-button>
      </div>
    </div>

    <el-alert
      v-if="focusHint"
      :title="focusHint"
      type="success"
      show-icon
      :closable="true"
      style="margin-bottom:12px"
      @close="focusHint=''"
    />

    <el-form :inline="true" size="small" class="filter-bar" @submit.native.prevent>
      <el-form-item label="周期">
        <el-select v-model="filterPeriod" clearable placeholder="全部" style="width:120px">
          <el-option label="日计划" value="day" />
          <el-option label="周计划" value="week" />
          <el-option label="月计划" value="month" />
          <el-option label="年计划" value="year" />
        </el-select>
      </el-form-item>
      <el-form-item label="缓急">
        <el-select v-model="filterLevel" clearable placeholder="全部" style="width:120px">
          <el-option label="紧急" value="urgent" />
          <el-option label="优先" value="priority" />
          <el-option label="日常" value="normal" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="keyword" clearable placeholder="任务内容" style="width:180px" />
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-refresh" @click="resetFilter">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="period-cards">
      <el-col :xs="24" :sm="12" :md="6" v-for="p in periodStats" :key="p.key">
        <el-card shadow="hover" :class="['period-card', { active: filterPeriod === p.key }]" @click.native="filterPeriod = p.key">
          <div class="period-name">{{ p.label }}</div>
          <div class="period-count">{{ p.pending }} <span class="unit">项进行中</span></div>
          <div class="period-done">已完成 {{ p.done }} / 共 {{ p.total }}</div>
          <div class="period-levels">
            <span class="lv urgent">急 {{ p.urgent }}</span>
            <span class="lv priority">优 {{ p.priority }}</span>
            <span class="lv normal">常 {{ p.normal }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table :data="filteredTasks" border size="small" style="width:100%;margin-top:16px" :row-class-name="rowClassName">
      <el-table-column label="周期" prop="periodLabel" width="90" />
      <el-table-column label="缓急" width="100">
        <template slot-scope="{ row }">
          <el-tag :type="levelTag(row.level)" size="mini">{{ levelLabel(row.level) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="任务内容" prop="title" min-width="220" show-overflow-tooltip />
      <el-table-column label="鱼池" prop="pondDetailNum" width="100" show-overflow-tooltip />
      <el-table-column label="来源" width="100">
        <template slot-scope="{ row }">{{ row.from || '—' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag :type="row.done ? 'success' : 'info'" size="mini">{{ row.done ? '已完成' : '进行中' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="setLevel(row, 'urgent')">紧急</el-button>
          <el-button type="text" size="mini" @click="setLevel(row, 'priority')">优先</el-button>
          <el-button type="text" size="mini" @click="setLevel(row, 'normal')">日常</el-button>
          <el-button type="text" size="mini" @click="toggleDone(row)">{{ row.done ? '重开' : '完成' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="新建计划" :visible.sync="createOpen" width="480px" append-to-body>
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="周期" required>
          <el-select v-model="form.period" style="width:100%">
            <el-option label="日计划" value="day" />
            <el-option label="周计划" value="week" />
            <el-option label="月计划" value="month" />
            <el-option label="年计划" value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="缓急" required>
          <el-radio-group v-model="form.level">
            <el-radio label="urgent">紧急</el-radio>
            <el-radio label="priority">优先</el-radio>
            <el-radio label="normal">日常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.title" type="textarea" :rows="3" maxlength="120" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="createOpen = false">取消</el-button>
        <el-button type="primary" size="small" @click="submitCreate">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { TASK_STORAGE_KEY, createInspectionPlanTask } from '@/utils/alarmTaskBridge'

const PERIOD_MAP = { day: '日计划', week: '周计划', month: '月计划', year: '年计划' }
const LEVEL_MAP = { urgent: '紧急', priority: '优先', normal: '日常' }

export default {
  name: 'TaskPlan',
  data() {
    return {
      filterPeriod: '',
      filterLevel: '',
      keyword: '',
      createOpen: false,
      form: { period: 'day', level: 'priority', title: '' },
      periodList: [
        { key: 'day', label: '日计划' },
        { key: 'week', label: '周计划' },
        { key: 'month', label: '月计划' },
        { key: 'year', label: '年计划' },
      ],
      tasks: [],
      focusId: '',
      focusHint: '',
    }
  },
  computed: {
    filteredTasks() {
      const kw = (this.keyword || '').trim()
      return this.tasks.filter(t => {
        if (this.filterPeriod && t.period !== this.filterPeriod) return false
        if (this.filterLevel && t.level !== this.filterLevel) return false
        if (kw && !(t.title || '').includes(kw)) return false
        return true
      })
    },
    // 依赖 tasks，完成/重开后卡片数字必重算（急/优/常只计进行中）
    periodStats() {
      return this.periodList.map(p => {
        const list = this.tasks.filter(t => t.period === p.key)
        const pendingList = list.filter(t => !t.done)
        return {
          key: p.key,
          label: p.label,
          total: list.length,
          pending: pendingList.length,
          done: list.length - pendingList.length,
          urgent: pendingList.filter(t => t.level === 'urgent').length,
          priority: pendingList.filter(t => t.level === 'priority').length,
          normal: pendingList.filter(t => t.level === 'normal').length,
        }
      })
    },
  },
  watch: {
    '$route.query': {
      deep: true,
      handler() {
        this.applyQuery()
      },
    },
  },
  created() {
    this.loadTasks()
    this.applyQuery()
  },
  methods: {
    defaultTasks() {
      return [
        { id: 't1', period: 'day', level: 'urgent', title: '1号车间溶氧异常处理', done: false },
        { id: 't2', period: 'day', level: 'priority', title: '完成水质检测报告', done: false },
        { id: 't3', period: 'day', level: 'normal', title: '巡检设备运行情况', done: false },
        { id: 't4', period: 'week', level: 'urgent', title: '设备维护计划执行', done: false },
        { id: 't5', period: 'week', level: 'priority', title: '投喂方案优化调整', done: false },
        { id: 't6', period: 'month', level: 'priority', title: '完成月度产能目标', done: false },
        { id: 't7', period: 'year', level: 'priority', title: '年度产能目标推进', done: false },
      ].map(t => ({ ...t, periodLabel: PERIOD_MAP[t.period] }))
    },
    loadTasks() {
      try {
        const raw = localStorage.getItem(TASK_STORAGE_KEY)
        if (raw) {
          const list = JSON.parse(raw)
          if (Array.isArray(list) && list.length) {
            this.tasks = list.map(t => ({ ...t, periodLabel: PERIOD_MAP[t.period] || t.period }))
            return
          }
        }
      } catch (e) { /* ignore */ }
      this.tasks = this.defaultTasks()
      this.persist()
    },
    persist() {
      localStorage.setItem(TASK_STORAGE_KEY, JSON.stringify(this.tasks))
    },
    applyQuery() {
      const q = (this.$route && this.$route.query) || {}
      this.focusId = q.focus || ''
      if (q.from) {
        this.focusHint = `来自 ${q.from} 的任务联动${q.pond ? '（鱼池 ' + q.pond + '）' : ''}`
      }
      // 若带 title：自动插入一条（与 bridge 共用去重逻辑）
      if (q.title) {
        const res = createInspectionPlanTask({
          title: q.title,
          level: q.level || 'priority',
          period: q.period || 'day',
          from: q.from || '',
          pondDetailNum: q.pond || '',
        })
        this.loadTasks()
        if (res.task) {
          this.focusId = res.task.id
          this.focusHint = '已根据跳转参数创建/定位任务'
        }
      } else {
        this.loadTasks()
      }
      if (q.period) this.filterPeriod = q.period
      if (q.level) this.filterLevel = q.level
      if (q.pond) this.keyword = q.pond
    },
    rowClassName({ row }) {
      return row.id === this.focusId ? 'is-focus-row' : ''
    },
    levelLabel(level) { return LEVEL_MAP[level] || level },
    levelTag(level) {
      if (level === 'urgent') return 'danger'
      if (level === 'priority') return 'warning'
      return 'success'
    },
    resetFilter() {
      this.filterPeriod = ''
      this.filterLevel = ''
      this.keyword = ''
    },
    openCreate() {
      const q = (this.$route && this.$route.query) || {}
      this.form = {
        period: this.filterPeriod || q.period || 'day',
        level: this.filterLevel || q.level || 'priority',
        title: q.title || '',
      }
      this.createOpen = true
    },
    submitCreate() {
      if (!this.form.title || !this.form.title.trim()) {
        this.$message.warning('请填写任务内容')
        return
      }
      const res = createInspectionPlanTask({
        title: this.form.title.trim(),
        period: this.form.period,
        level: this.form.level,
        from: 'manual',
      })
      this.loadTasks()
      this.createOpen = false
      if (res.ok) {
        this.focusId = res.task.id
        this.$message.success('已新建计划')
      }
    },
    setLevel(row, level) {
      row.level = level
      this.persist()
      this.$message.success('已调整为' + this.levelLabel(level))
    },
    toggleDone(row) {
      const idx = this.tasks.findIndex(t => String(t.id) === String(row.id))
      if (idx < 0) {
        this.$message.error('未找到该任务，请刷新页面')
        return
      }
      const cur = this.tasks[idx]
      const nextDone = !cur.done
      const next = Object.assign({}, cur, {
        done: nextDone,
        periodLabel: PERIOD_MAP[cur.period] || cur.periodLabel
      })
      // 整项替换，保证 periodStats 计算属性一定刷新
      this.$set(this.tasks, idx, next)
      this.persist()
      this.$message.success(nextDone ? '已完成：进行中/急优常已减少' : '已重开：进行中数量已恢复')
    },
  },
}
</script>

<style scoped>
.task-plan {
  color: #e8eef7;
}
.task-plan .page-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 12px;
}
.task-plan .head-actions { display: flex; flex-wrap: wrap; gap: 8px; }
.task-plan .title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.02em;
}
.task-plan .sub {
  margin: 6px 0 0;
  font-size: 13px;
  color: #a8b6c9;
  line-height: 1.5;
}
.period-card {
  cursor: pointer;
  margin-bottom: 12px;
}
.period-card.active {
  border-color: #409eff !important;
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.45);
}
.period-name {
  font-weight: 700;
  margin-bottom: 6px;
  color: #f2f6fc;
  font-size: 15px;
}
.period-count {
  font-size: 24px;
  margin-bottom: 4px;
  color: #ffffff;
  font-weight: 700;
}
.period-count .unit {
  font-size: 13px;
  font-weight: 500;
  color: #a8b6c9;
  margin-left: 2px;
}
.period-done {
  font-size: 12px;
  color: #85d96a;
  margin-bottom: 8px;
}
.period-levels {
  display: flex;
  gap: 10px;
  font-size: 13px;
  font-weight: 600;
}
.period-levels .urgent { color: #ff8e8e; }
.period-levels .priority { color: #f0c078; }
.period-levels .normal { color: #85d96a; }

/* 筛选区：标签与控件可读 */
.task-plan ::v-deep .filter-bar .el-form-item__label {
  color: #d5deeb !important;
}
.task-plan ::v-deep .el-input__inner,
.task-plan ::v-deep .el-textarea__inner {
  background: #152233 !important;
  border-color: rgba(255, 255, 255, 0.18) !important;
  color: #f0f4fa !important;
}
.task-plan ::v-deep .el-input__inner::placeholder,
.task-plan ::v-deep .el-textarea__inner::placeholder {
  color: #8a9bb0 !important;
}
.task-plan ::v-deep .el-select .el-input .el-select__caret {
  color: #c5d0e0;
}

/* 顶部按钮 */
.task-plan ::v-deep .head-actions .el-button--default {
  background: #1a2b3f !important;
  border-color: rgba(255, 255, 255, 0.22) !important;
  color: #eef3fa !important;
}
.task-plan ::v-deep .head-actions .el-button--default:hover {
  border-color: #409eff !important;
  color: #ffffff !important;
}
.task-plan ::v-deep .filter-bar .el-button {
  background: #1a2b3f !important;
  border-color: rgba(255, 255, 255, 0.22) !important;
  color: #eef3fa !important;
}

/* 周期卡片 */
.task-plan ::v-deep .period-card.el-card {
  background: #152233 !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #e8eef7;
}
.task-plan ::v-deep .period-card .el-card__body {
  background: transparent !important;
  color: #e8eef7;
}

/* 表格与操作文字 */
.task-plan ::v-deep .el-table {
  color: #f0f4fa;
}
.task-plan ::v-deep .el-table .el-button--text {
  color: #6eb6ff !important;
  font-weight: 600;
}
.task-plan ::v-deep .el-table .el-button--text:hover {
  color: #9fd0ff !important;
}
.task-plan ::v-deep .el-tag--info {
  background: rgba(144, 147, 153, 0.28) !important;
  border-color: rgba(200, 205, 215, 0.35) !important;
  color: #e8edf5 !important;
}
.task-plan ::v-deep .is-focus-row > td {
  background: rgba(64, 158, 255, 0.16) !important;
}
</style>
