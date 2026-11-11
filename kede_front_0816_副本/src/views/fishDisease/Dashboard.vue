<template>
  <div class="dashboard-page">
    <div class="welcome-card page-card">
      <div class="welcome-text">
        <h2>欢迎使用鱼病诊断系统</h2>
        <p>智能水产病害诊断与处置方案管理平台</p>
      </div>
      <el-button type="primary" @click="goNav('collect')">开始辅助诊断</el-button>
    </div>

    <div class="kpi-grid">
      <div class="kpi-card page-card">
        <div class="kpi-label">活跃病例</div>
        <div class="kpi-val">{{ summary.active || 0 }}</div>
      </div>
      <div class="kpi-card page-card">
        <div class="kpi-label">紧急病例</div>
        <div class="kpi-val danger">{{ summary.critical || 0 }}</div>
      </div>
      <div class="kpi-card page-card">
        <div class="kpi-label">治疗中</div>
        <div class="kpi-val">{{ summary.treating || 0 }}</div>
      </div>
      <div class="kpi-card page-card">
        <div class="kpi-label">已治愈</div>
        <div class="kpi-val success">{{ summary.resolved || 0 }}</div>
      </div>
    </div>

    <div class="page-card shortcut-card">
      <div class="card-title">快捷入口</div>
      <div class="shortcut-grid">
        <div class="shortcut-item" @click="goNav('monitor')">
          <i class="el-icon-data-line" />
          <span>病情监测</span>
        </div>
        <div class="shortcut-item" @click="goNav('diagnosis')">
          <i class="el-icon-chat-dot-round" />
          <span>辅助诊断</span>
        </div>
        <div class="shortcut-item" @click="goNav('knowledge')">
          <i class="el-icon-notebook-2" />
          <span>知识库</span>
        </div>
        <div class="shortcut-item" @click="goNav('config')">
          <i class="el-icon-setting" />
          <span>基础配置</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchSummary } from '@/api/fishDisease'

export default {
  name: 'Dashboard',
  inject: {
    switchFishNav: { default: null }
  },
  data() {
    return { summary: {} }
  },
  created() {
    fetchSummary().then(res => { this.summary = res.data || {} }).catch(e => {
      console.error('[鱼病首页] 汇总加载失败', e)
      this.$message && this.$message.warning('病例汇总加载失败，请确认 modules-api 可用')
    })
  },
  methods: {
    goNav(key) {
      if (typeof this.switchFishNav === 'function') {
        this.switchFishNav(key)
        return
      }
      // 兜底：独立路由场景
      const map = {
        monitor: '/monitor',
        diagnosis: '/diagnosis',
        knowledge: '/knowledge',
        config: '/config',
        collect: '/collect'
      }
      if (map[key]) this.$router.push(map[key])
    }
  }
}
</script>

<style scoped>
.welcome-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2) 0%, #0f1a26 70%);
}

.welcome-text h2 {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
  color: var(--ink);
}

.welcome-text p {
  margin: 0;
  color: var(--text-secondary);
}

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
}

.kpi-val.danger { color: #ff4d4f; }
.kpi-val.success { color: #52c41a; }

.shortcut-card {
  padding: 24px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
  margin-bottom: 16px;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.15s;
}

.shortcut-item:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-light);
}

.shortcut-item i {
  font-size: 24px;
}

@media (max-width: 900px) {
  .kpi-grid, .shortcut-grid { grid-template-columns: repeat(2, 1fr); }
  .welcome-card { flex-direction: column; gap: 16px; text-align: center; }
}
</style>
