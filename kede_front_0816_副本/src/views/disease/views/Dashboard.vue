<template>
  <div class="dashboard-page">
    <div class="welcome-card page-card">
      <div class="welcome-text">
        <h2>欢迎使用鱼病诊断系统</h2>
        <p>智能水产病害诊断与处置方案管理平台</p>
      </div>
      <el-button type="primary" @click="$router.push('/diagnosis')">开始辅助诊断</el-button>
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
        <div class="shortcut-item" @click="$router.push('/disease/monitor')">
          <i class="el-icon-data-line" />
          <span>病情监测</span>
        </div>
        <div class="shortcut-item" @click="$router.push('/disease/diagnosis')">
          <i class="el-icon-chat-dot-round" />
          <span>辅助诊断</span>
        </div>
        <div class="shortcut-item" @click="$router.push('/disease/knowledge')">
          <i class="el-icon-notebook-2" />
          <span>知识库</span>
        </div>
        <div class="shortcut-item" @click="$router.push('/disease/config')">
          <i class="el-icon-user" />
          <span>基础配置</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchSummary } from '../api'

export default {
  name: 'Dashboard',
  data() {
    return { summary: {} }
  },
  created() {
    fetchSummary().then(res => { this.summary = res.data })
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
  background: linear-gradient(135deg, #e6f7ff 0%, #fff 60%);
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
