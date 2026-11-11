<template>
  <div class="oa-score-page app-container">
    <div class="page-head">
      <div class="page-actions">
        <el-button type="primary" size="mini" @click="$router.push('/oaManager/userRating')">他人评分</el-button>
        <el-button type="success" size="mini" @click="$router.push('/oaManager/workPerformance')">工作绩效</el-button>
        <el-button type="warning" size="mini" plain @click="$router.push('/user/notify-message')">我的通知</el-button>
        <el-button size="mini" @click="$router.push('/oaManager/oa')">OA协同办公</el-button>
      </div>
      <h2 class="page-title">知识库 · 评分（OA考核标准）</h2>
      <p class="page-desc">
        对齐《已有模块-OA考核指标》：计划25 / 考勤10 / 考试15（理论10+实操5） / 他人20（同事8+领导12） / 绩效30。
        考核结果与实操录入会发【优先】站内信。
      </p>
    </div>

    <el-alert
      class="knowledge-note"
      type="info"
      :closable="false"
      show-icon
      title="评分标准文档已归档至知识库 OA 分类，可与下方五维满分对照查阅。"
    />

    <el-tabs v-model="activeTab" class="score-tabs">
      <el-tab-pane label="考核评分标准" name="standard">
        <EvaluationTab v-if="activeTab === 'standard'" show-knowledge-note />
      </el-tab-pane>
      <el-tab-pane label="他人评分" name="rating">
        <div class="rating-link-panel">
          <p>对部门成员进行月度评分（同事满分8、领导满分12），请前往 OA 他人打分页面。</p>
          <el-button type="primary" icon="el-icon-right" @click="goUserRating">
            打开他人评分
          </el-button>
        </div>
      </el-tab-pane>
      <el-tab-pane label="工作绩效" name="performance">
        <div class="rating-link-panel">
          <p>工作绩效满分 30 分，录入后计入月度综合考核。</p>
          <el-button type="success" icon="el-icon-right" @click="goWorkPerformance">
            打开工作绩效
          </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import EvaluationTab from '@/views/oaManager/oa/components/EvaluationTab.vue'

export default {
  name: 'OaScore',
  components: {
    EvaluationTab
  },
  data() {
    return {
      activeTab: 'standard'
    }
  },
  methods: {
    goUserRating() {
      this.$router.push({ path: '/oaManager/userRating' }).catch(() => {})
    },
    goWorkPerformance() {
      this.$router.push({ path: '/oaManager/workPerformance' }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.page-head {
  margin-bottom: 12px;
}

.page-actions {
  float: right;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.page-title {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.page-desc {
  margin: 0;
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
  max-width: 720px;
}

.knowledge-note {
  margin-bottom: 16px;
  clear: both;
}

.rating-link-panel {
  padding: 24px 0;
  color: #606266;
}

.rating-link-panel p {
  margin: 0 0 16px;
}
</style>
