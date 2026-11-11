<template>
  <div class="evaluation-tab">
    <!-- 顶部标题和时间选择 -->
    <div class="evaluation-header">
      <div class="header-left">
        <h2>考核评分系统</h2>
        <p>多维度综合考核评价（计划25 / 考勤10 / 考试15 / 他人20 / 绩效30）</p>
        <p v-if="showKnowledgeNote" class="knowledge-note-inline">
          评分标准详见知识库 · OA 分类文档《已有模块-OA考核指标》。
        </p>
      </div>
      <div class="header-right">
        <el-button size="mini" type="primary" plain @click="goRelated('userRating')">他人评分</el-button>
        <el-button size="mini" type="success" plain @click="goRelated('workPerformance')">工作绩效</el-button>
        <el-button size="mini" type="warning" plain @click="goRelated('notify')">我的通知</el-button>
        <el-date-picker
          v-model="selectedPeriod"
          type="month"
          placeholder="选择月份"
          format="yyyy年MM月"
          value-format="yyyy-MM"
          @change="handlePeriodChange">
        </el-date-picker>
      </div>
    </div>

    <div class="standard-card">
      <h3>考核评分标准（100分）</h3>
      <div class="standard-grid">
        <div v-for="item in standardDims" :key="item.key" class="standard-item">
          <div class="std-name">{{ item.name }}</div>
          <div class="std-score">{{ item.max }}分</div>
          <div class="std-desc">{{ item.desc }}</div>
        </div>
      </div>
    </div>

    <!-- 综合考核得分卡片 -->
    <div class="score-card">
      <div class="score-title">综合考核得分</div>
      <div class="score-main">
        <div class="score-value">
          <span class="current-score">{{ personalData.totalScore || 0 }}</span>
          <span class="max-score">/ 100</span>
        </div>
        <div class="score-grade">
          <span class="grade-label">等级: {{ personalData.grade || '--' }}</span>
        </div>
        <div class="score-message">
          {{ getScoreMessage(personalData.totalScore) }}
        </div>
      </div>
    </div>

    <!-- 评分明细 -->
    <div class="detail-card">
      <h3>评分明细</h3>
      <p class="subtitle">各维度得分（满分合计 100）</p>
      <div class="dimension-list">
        <div 
          v-for="(dimension, index) in personalData.dimensions" 
          :key="index"
          class="dimension-item">
          <div class="dimension-header">
            <i :class="getDimensionIcon(dimension.type)" class="dimension-icon"></i>
            <div class="dimension-info">
              <div class="dimension-name">{{ dimension.name }}</div>
              <div class="dimension-weight">满分 {{ dimension.weight }} 分</div>
            </div>
          </div>
          <div class="dimension-details">
            <div class="dimension-desc">{{ dimension.details }}</div>
            <div class="dimension-scores">
              <span class="dimension-score">{{ dimension.score }}分</span>
              <span class="dimension-weighted">计入总分: {{ dimension.weightedScore }}分</span>
            </div>
          </div>
          <div class="dimension-progress">
            <el-progress 
              :percentage="dimPercent(dimension)" 
              :color="getProgressColor(dimPercent(dimension))"
              :show-text="false">
            </el-progress>
          </div>
        </div>
      </div>
    </div>

    <!-- Tab导航 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="evaluation-tabs">
      <el-tab-pane label="个人考核" name="personal">
        <div v-if="activeTab === 'personal'" class="personal-evaluation">
          <!-- 个人考核趋势 -->
          <div class="trend-card">
            <h3>个人考核趋势</h3>
            <p class="subtitle">近6个月考核得分变化</p>
            <div class="trend-list">
              <div 
                v-for="(item, index) in trendData" 
                :key="index"
                class="trend-item">
                <div class="trend-period">{{ item.period }}</div>
                <div class="trend-grade">
                  <el-tag :type="getGradeTagType(item.grade)" size="small" class="trend-grade-tag">
                    {{ item.grade }}
                  </el-tag>
                </div>
                <div class="trend-progress">
                  <el-progress 
                    :percentage="item.score" 
                    :color="getProgressColor(item.score)"
                    :show-text="false"
                    :stroke-width="6">
                  </el-progress>
                </div>
                <div class="trend-score">{{ item.score }}</div>
              </div>
            </div>
          </div>

          <!-- 统计摘要 -->
          <div class="summary-card">
            <h3>统计摘要</h3>
            <div class="summary-grid">
              <div class="summary-item">
                <div class="summary-label">年度平均分</div>
                <div class="summary-value">{{ summaryData.avgScore || '--' }}</div>
                <div class="summary-change positive">
                  <i class="el-icon-arrow-up"></i>
                  较去年+{{ summaryData.avgChange || 0 }}
                </div>
              </div>
              <div class="summary-item">
                <div class="summary-label">最高得分</div>
                <div class="summary-value">{{ summaryData.maxScore || '--' }}</div>
                <div class="summary-date">{{ summaryData.maxScorePeriod || '--' }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">部门排名</div>
                <div class="summary-value">{{ summaryData.ranking || '--' }}</div>
                <div class="summary-dept">{{ summaryData.department || '--' }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">获得等级A+</div>
                <div class="summary-value">{{ summaryData.gradeACount || 0 }}次</div>
                <div class="summary-period">本年度</div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="部门排名" name="ranking">
        <div v-if="activeTab === 'ranking'" class="ranking-section">
          <h3>部门排名</h3>
          <p class="subtitle">本月部门内考核排名</p>
          <el-table 
            :data="rankingData" 
            style="width: 100%"
            stripe
            class="ranking-table">
            <el-table-column prop="rank" label="排名" width="100" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.rank === 1" class="medal gold">🥇</span>
                <span v-else-if="scope.row.rank === 2" class="medal silver">🥈</span>
                <span v-else-if="scope.row.rank === 3" class="medal bronze">🥉</span>
                <span v-else>{{ scope.row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="姓名" min-width="120"></el-table-column>
            <el-table-column prop="department" label="部门" min-width="120"></el-table-column>
            <el-table-column prop="period" label="考核期" min-width="120" align="center"></el-table-column>
            <el-table-column prop="totalScore" label="总分" min-width="120" align="center">
              <template slot-scope="scope">
                <span :class="getScoreColorClass(scope.row.totalScore)">
                  {{ scope.row.totalScore }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="grade" label="等级" min-width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="getGradeTagType(scope.row.grade)" size="small">
                  {{ scope.row.grade }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="历史记录" name="history">
        <div v-if="activeTab === 'history'" class="history-section">
          <h3>历史考核记录</h3>
          <p class="subtitle">查看所有历史考核数据</p>
          <el-table 
            :data="historyData" 
            style="width: 100%"
            stripe
            class="history-table">
            <el-table-column prop="period" label="考核期" min-width="120" align="center"></el-table-column>
            <el-table-column prop="workPlan" label="工作计划" min-width="100" align="center"></el-table-column>
            <el-table-column prop="attendance" label="考勤" min-width="100" align="center"></el-table-column>
            <el-table-column prop="exam" label="考试" min-width="100" align="center"></el-table-column>
            <el-table-column prop="peerRating" label="他人评分" min-width="100" align="center"></el-table-column>
            <el-table-column prop="workPerformance" label="工作绩效" min-width="100" align="center"></el-table-column>
            <el-table-column prop="totalScore" label="总分" min-width="100" align="center">
              <template slot-scope="scope">
                <span style="color: #54907a; font-weight: 500;">{{ scope.row.totalScore }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="grade" label="等级" min-width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="getGradeTagType(scope.row.grade)" size="small">
                  {{ scope.row.grade }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getChartSeed } from '@/api/fish/daPing'
import { 
  getPersonalEvaluation,
  getPersonalEvaluationTrend,
  getPersonalEvaluationSummary,
  getDepartmentRanking,
  getEvaluationHistory
} from '@/api/fish/evaluation'

export default {
  name: 'EvaluationTab',
  props: {
    showKnowledgeNote: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      activeTab: 'personal',
      selectedPeriod: this.getCurrentPeriod(),
      personalData: {
        totalScore: 0,
        grade: '',
        dimensions: []
      },
      trendData: [],
      summaryData: {},
      rankingData: [],
      historyData: [],
      loading: false,
      standardDims: [
        { key: 'workPlan', name: '工作计划', max: 25, desc: '已审计划领导评价均分' },
        { key: 'attendance', name: '考勤', max: 10, desc: '出勤与迟到早退扣分' },
        { key: 'exam', name: '考试', max: 15, desc: '理论10 + 实操5' },
        { key: 'peerRating', name: '他人评分', max: 20, desc: '同事8 + 领导12' },
        { key: 'workPerformance', name: '工作绩效', max: 30, desc: '绩效表得分映射' }
      ]
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    dimPercent(dimension) {
      const max = Number(dimension && dimension.weight) || 0
      const score = Number(dimension && dimension.score) || 0
      if (max <= 0) return 0
      // score 可能是百分制旧数据，也可能是绝对分；优先按满分比例
      const ratio = score > max ? Math.min(100, score) : (score / max) * 100
      return Math.max(0, Math.min(100, Math.round(ratio)))
    },
    goRelated(key) {
      const map = {
        userRating: '/oaManager/userRating',
        workPerformance: '/oaManager/workPerformance',
        notify: '/user/notify-message'
      }
      const path = map[key]
      if (path) this.$router.push({ path }).catch(() => {})
    },
    getCurrentPeriod() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      return `${year}-${month}`
    },
    
    handlePeriodChange() {
      this.loadData()
    },
    
    handleTabClick(tab) {
      this.loadData()
    },
    
    async loadData() {
      this.loading = true
      try {
        if (this.activeTab === 'personal') {
          await Promise.all([
            this.loadPersonalData(),
            this.loadTrendData(),
            this.loadSummaryData()
          ])
        } else if (this.activeTab === 'ranking') {
          await this.loadRankingData()
        } else if (this.activeTab === 'history') {
          await this.loadHistoryData()
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadPersonalData() {
      try {
        const res = await getPersonalEvaluation(this.selectedPeriod)
        if (res.code === 200 || res.code === 0) {
          this.personalData = res.data || {
            totalScore: 0,
            grade: '',
            dimensions: []
          }
        } else {
          this.personalData = await this.getMockPersonalData()
        }
      } catch (error) {
        console.error('加载个人考核数据失败:', error)
        this.personalData = await this.getMockPersonalData()
      }
    },
    
    async loadTrendData() {
      try {
        const res = await getPersonalEvaluationTrend()
        if (res.code === 200 || res.code === 0) {
          this.trendData = res.data || []
        }
      } catch (error) {
        console.error('加载趋势数据失败:', error)
        this.trendData = await this.getMockTrendData()
      }
    },
    
    async loadSummaryData() {
      try {
        const res = await getPersonalEvaluationSummary()
        if (res.code === 200 || res.code === 0) {
          this.summaryData = res.data || {}
        }
      } catch (error) {
        console.error('加载统计摘要失败:', error)
        this.summaryData = await this.getMockSummaryData()
      }
    },
    
    async loadRankingData() {
      try {
        const res = await getDepartmentRanking(this.selectedPeriod)
        if (res.code === 200 || res.code === 0) {
          this.rankingData = res.data || []
        } else {
          this.rankingData = await this.getMockRankingData()
        }
      } catch (error) {
        console.error('加载部门排名失败:', error)
        this.rankingData = await this.getMockRankingData()
      }
    },
    
    async loadHistoryData() {
      try {
        const res = await getEvaluationHistory({})
        if (res.code === 200 || res.code === 0) {
          this.historyData = res.data || []
        }
      } catch (error) {
        console.error('加载历史记录失败:', error)
        this.historyData = await this.getMockHistoryData()
      }
    },
    
    // 原前端假数据已落库 zk_chart_seed；以下方法改读库，旧硬编码整段注释保留在 git/种子 SQL
    async getMockPersonalData() {
      /*
      return { totalScore: 86.5, grade: 'A', dimensions: [ ... ] }
      */
      try {
        const res = await getChartSeed('oa_eval_personal')
        return (res && res.data) || { totalScore: 0, grade: '', dimensions: [] }
      } catch (e) {
        return { totalScore: 0, grade: '', dimensions: [] }
      }
    },
    
    async getMockTrendData() {
      try {
        const res = await getChartSeed('oa_eval_trend')
        return Array.isArray(res && res.data) ? res.data : []
      } catch (e) {
        return []
      }
    },
    
    async getMockSummaryData() {
      try {
        const res = await getChartSeed('oa_eval_summary')
        return (res && res.data) || {}
      } catch (e) {
        return {}
      }
    },
    
    async getMockRankingData() {
      try {
        const res = await getChartSeed('oa_eval_ranking')
        return Array.isArray(res && res.data) ? res.data : []
      } catch (e) {
        return []
      }
    },
    
    async getMockHistoryData() {
      try {
        const res = await getChartSeed('oa_eval_history')
        return Array.isArray(res && res.data) ? res.data : []
      } catch (e) {
        return []
      }
    },
    
    getDimensionIcon(type) {
      const iconMap = {
        workPlan: 'el-icon-document',
        attendance: 'el-icon-check',
        exam: 'el-icon-user',
        peerRating: 'el-icon-user-solid',
        workPerformance: 'el-icon-briefcase'
      }
      return iconMap[type] || 'el-icon-document'
    },
    
    getGradeTagType(grade) {
      if (!grade) return 'info'
      if (grade.includes('A+')) return 'success'
      if (grade.includes('A')) return 'primary'
      if (grade.includes('B+')) return 'success'
      if (grade.includes('B')) return 'warning'
      return 'info'
    },
    
    getProgressColor(score) {
      if (score >= 90) return '#67C23A'
      if (score >= 80) return '#54907a'
      if (score >= 70) return '#E6A23C'
      return '#F56C6C'
    },
    
    getScoreMessage(score) {
      if (score >= 90) return '本期考核成绩优秀,继续保持!'
      if (score >= 80) return '本期考核成绩良好,继续努力!'
      if (score >= 70) return '本期考核成绩合格,还有提升空间!'
      return '本期考核成绩待提升,加油!'
    },
    
    getScoreColorClass(score) {
      if (score >= 90) return 'score-purple'
      if (score >= 85) return 'score-blue'
      return 'score-green'
    }
  }
}
</script>

<style lang="scss" scoped>
.evaluation-tab {
  padding: 20px;
  min-height: 600px;
  background: transparent;

  ::v-deep .el-progress-bar__outer {
    background: rgba(84, 144, 122, 0.12) !important;
  }

  .evaluation-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    padding: 20px;
    background: rgba(10, 21, 32, 0.5);
    border-radius: 8px;
    border: 1px solid rgba(84, 144, 122, 0.15);

    .header-left {
      h2 {
        color: #ffffff;
        font-size: 24px;
        margin: 0 0 8px 0;
        font-weight: 600;
      }

      p {
        color: rgba(255, 255, 255, 0.55);
        font-size: 14px;
        margin: 0;
      }

      .knowledge-note-inline {
        margin-top: 8px;
        color: rgba(255, 255, 255, 0.45);
        font-size: 13px;
      }
    }

    .header-right {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: flex-end;
      gap: 8px;

      ::v-deep .el-date-editor {
        .el-input__inner {
          background: rgba(10, 21, 32, 0.6);
          border-color: rgba(84, 144, 122, 0.3);
          color: #ffffff;
        }
      }
    }
  }

  .standard-card {
    background: rgba(10, 21, 32, 0.5);
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 24px;
    border: 1px solid rgba(84, 144, 122, 0.15);

    h3 {
      margin: 0 0 16px;
      font-size: 16px;
      color: #ffffff;
    }

    .standard-grid {
      display: grid;
      grid-template-columns: repeat(5, minmax(0, 1fr));
      gap: 12px;
    }

    .standard-item {
      padding: 12px;
      background: rgba(10, 21, 32, 0.7);
      border-radius: 6px;
      border: 1px solid rgba(84, 144, 122, 0.1);

      .std-name {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.7);
      }

      .std-score {
        margin: 6px 0;
        font-size: 22px;
        font-weight: 600;
        color: #54907a;
      }

      .std-desc {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.4);
        line-height: 1.4;
      }
    }

    @media (max-width: 900px) {
      .standard-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }
    }
  }

  .score-card {
    background: linear-gradient(135deg, rgba(84, 144, 122, 0.2) 0%, rgba(111, 181, 154, 0.1) 100%);
    border: 1px solid rgba(84, 144, 122, 0.3);
    border-radius: 12px;
    padding: 32px;
    margin-bottom: 24px;

    .score-title {
      color: rgba(255, 255, 255, 0.85);
      font-size: 16px;
      margin-bottom: 20px;
      font-weight: 500;
    }

    .score-main {
      text-align: center;
      color: #ffffff;

      .score-value {
        margin-bottom: 16px;

        .current-score {
          font-size: 56px;
          font-weight: bold;
          margin-right: 8px;
          color: #54907a;
          text-shadow: 0 0 20px rgba(84, 144, 122, 0.5);
        }

        .max-score {
          font-size: 32px;
          opacity: 0.6;
        }
      }

      .score-grade {
        margin-bottom: 12px;

        .grade-label {
          font-size: 18px;
          font-weight: 500;
          opacity: 0.85;
        }
      }

      .score-message {
        font-size: 16px;
        opacity: 0.7;
      }
    }
  }

  .detail-card {
    background: rgba(10, 21, 32, 0.5);
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    border: 1px solid rgba(84, 144, 122, 0.15);

    h3 {
      color: #ffffff;
      font-size: 20px;
      margin: 0 0 8px 0;
      font-weight: 600;
    }

    .subtitle {
      color: rgba(255, 255, 255, 0.5);
      font-size: 14px;
      margin: 0 0 20px 0;
    }

    .dimension-list {
      .dimension-item {
        padding: 20px;
        margin-bottom: 16px;
        background: rgba(10, 21, 32, 0.7);
        border-radius: 8px;
        border: 1px solid rgba(84, 144, 122, 0.12);

        .dimension-header {
          display: flex;
          align-items: center;
          margin-bottom: 12px;

          .dimension-icon {
            font-size: 24px;
            color: #54907a;
            margin-right: 12px;
          }

          .dimension-info {
            flex: 1;

            .dimension-name {
              color: #ffffff;
              font-size: 16px;
              font-weight: 500;
              margin-bottom: 4px;
            }

            .dimension-weight {
              color: rgba(255, 255, 255, 0.45);
              font-size: 14px;
            }
          }
        }

        .dimension-details {
          margin-bottom: 12px;

          .dimension-desc {
            color: rgba(255, 255, 255, 0.6);
            font-size: 14px;
            margin-bottom: 8px;
          }

          .dimension-scores {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .dimension-score {
              color: #54907a;
              font-size: 18px;
              font-weight: 600;
            }

            .dimension-weighted {
              color: rgba(255, 255, 255, 0.55);
              font-size: 14px;
            }
          }
        }

        .dimension-progress {
          ::v-deep .el-progress {
            .el-progress-bar__outer {
              background: rgba(84, 144, 122, 0.1);
            }
          }
        }
      }
    }
  }

  .evaluation-tabs {
    margin-top: 24px;

    ::v-deep .el-tabs__header {
      background: rgba(10, 21, 32, 0.6);
      border-radius: 8px 8px 0 0;
      padding: 0 20px;
      margin: 0;
      border: 1px solid rgba(84, 144, 122, 0.15);
      border-bottom: none;
    }

    ::v-deep .el-tabs__nav-wrap::after {
      background-color: rgba(84, 144, 122, 0.15);
    }

    ::v-deep .el-tabs__item {
      color: rgba(255, 255, 255, 0.55);
      border-bottom: 2px solid transparent;

      &:hover {
        color: #54907a;
      }

      &.is-active {
        color: #54907a;
        border-bottom-color: #54907a;
      }
    }

    ::v-deep .el-tabs__content {
      background: rgba(10, 21, 32, 0.5);
      border: 1px solid rgba(84, 144, 122, 0.15);
      border-top: none;
      border-radius: 0 0 8px 8px;
      padding: 24px;
      min-height: 400px;
    }
  }

  // 个人考核样式
  .personal-evaluation {
    .trend-card,
    .summary-card {
      background: rgba(10, 21, 32, 0.6);
      border-radius: 8px;
      padding: 24px;
      margin-bottom: 24px;
      border: 1px solid rgba(84, 144, 122, 0.12);

      h3 {
        color: #ffffff;
        font-size: 18px;
        margin: 0 0 8px 0;
        font-weight: 600;
      }

      .subtitle {
        color: rgba(255, 255, 255, 0.5);
        font-size: 14px;
        margin: 0 0 20px 0;
      }
    }

    .trend-card {
      .trend-list {
        .trend-item {
          display: grid;
          grid-template-columns: 80px 60px 1fr 60px;
          gap: 12px;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid rgba(84, 144, 122, 0.1);

          &:last-child {
            border-bottom: none;
          }

          .trend-period {
            color: rgba(255, 255, 255, 0.7);
            font-size: 14px;
          }

          .trend-grade {
            .trend-grade-tag {
              font-size: 12px;
            }
          }

          .trend-progress {
            ::v-deep .el-progress {
              .el-progress-bar__outer {
                background: rgba(84, 144, 122, 0.1);
              }
            }
          }

          .trend-score {
            color: #54907a;
            font-size: 16px;
            font-weight: 600;
            text-align: right;
          }
        }
      }
    }

    .summary-card {
      .summary-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;

        .summary-item {
          background: rgba(10, 21, 32, 0.7);
          border-radius: 8px;
          padding: 16px;
          border: 1px solid rgba(84, 144, 122, 0.1);
          text-align: center;

          .summary-label {
            color: rgba(255, 255, 255, 0.45);
            font-size: 14px;
            margin-bottom: 8px;
          }

          .summary-value {
            color: #ffffff;
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 8px;
          }

          .summary-change {
            font-size: 12px;
            margin-top: 4px;

            &.positive {
              color: #67C23A;
            }
          }

          .summary-date,
          .summary-dept,
          .summary-period {
            color: rgba(255, 255, 255, 0.4);
            font-size: 12px;
            margin-top: 4px;
          }
        }
      }
    }
  }

  // 部门排名样式
  .ranking-section,
  .history-section {
    h3 {
      color: #ffffff;
      font-size: 20px;
      margin: 0 0 8px 0;
      font-weight: 600;
    }

    .subtitle {
      color: rgba(255, 255, 255, 0.5);
      font-size: 14px;
      margin: 0 0 20px 0;
    }

    .ranking-table,
    .history-table {
      ::v-deep .el-table {
        background: transparent;

        &::before {
          background: rgba(84, 144, 122, 0.15);
        }

        .el-table__header {
          th {
            background: rgba(10, 21, 32, 0.8);
            color: rgba(255, 255, 255, 0.7);
            border-color: rgba(84, 144, 122, 0.15);
          }
        }

        .el-table__body {
          tr {
            background: transparent;

            &:hover {
              background: rgba(84, 144, 122, 0.08);
            }

            td {
              border-color: rgba(84, 144, 122, 0.1);
              color: rgba(255, 255, 255, 0.85);
            }
          }

          tr.el-table__row--striped {
            background: rgba(10, 21, 32, 0.3);
          }
        }
      }

      .medal {
        font-size: 24px;
      }

      .score-purple {
        color: #b37feb;
        font-weight: 600;
      }

      .score-blue {
        color: #54907a;
        font-weight: 600;
      }

      .score-green {
        color: #67C23A;
        font-weight: 600;
      }
    }
  }
}
</style>
