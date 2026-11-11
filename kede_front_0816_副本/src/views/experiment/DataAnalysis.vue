<template>
  <div class="analysis-page">
    <div class="page-card filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="车间">
          <el-select v-model="filters.workshop" clearable placeholder="全部车间" style="width: 140px" @change="loadSummary">
            <el-option label="1号车间" value="1" />
            <el-option label="2号车间" value="2" />
            <el-option label="3号车间" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统">
          <el-select v-model="filters.system" clearable placeholder="全部系统" style="width: 140px" @change="loadSummary">
            <el-option label="1号系统" value="1" />
            <el-option label="2号系统" value="2" />
            <el-option label="3号系统" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择实验">
          <el-select v-model="filters.experimentId" placeholder="全部实验" clearable style="width: 220px" @change="loadSummary">
            <el-option label="全部实验" value="" />
            <el-option
              v-for="item in filteredExperiments"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="filters.startDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="年/月/日"
            @change="loadSummary"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="filters.endDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="年/月/日"
            @change="loadSummary"
          />
        </el-form-item>
      </el-form>
    </div>

    <div class="kpi-grid">
      <div v-for="item in kpis" :key="item.key" class="kpi-card page-card">
        <div>
          <div class="kpi-label">{{ item.label }}</div>
          <div class="kpi-value">{{ item.value }}</div>
        </div>
        <i :class="item.icon" :style="{ color: item.color }" class="kpi-icon" />
      </div>
    </div>

    <div class="export-row">
      <el-button type="primary" icon="el-icon-download" @click="handleExport">导出分析报告</el-button>
    </div>

    <div class="page-card chart-card">
      <div v-if="summary.trend && summary.trend.length" class="trend-list">
        <div v-for="item in summary.trend" :key="item.date" class="trend-item">
          <div class="trend-date">{{ item.date }}</div>
          <div class="trend-bar-wrap">
            <div class="trend-bar" :style="{ width: `${item.growthRate}%` }" />
          </div>
          <div class="trend-value">增重率 {{ item.growthRate }}%</div>
        </div>
      </div>
      <div v-else class="chart-empty">
        <i class="el-icon-date" />
        <p>暂无分析数据，请先录入实验数据</p>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchExperiments } from '@/api/experiment'
import { fetchAnalysisSummary } from '@/api/experiment'

export default {
  name: 'DataAnalysis',
  data() {
    return {
      experiments: [],
      filters: {
        workshop: '',
        system: '',
        experimentId: '',
        startDate: '',
        endDate: ''
      },
      summary: {
        avgGrowthRate: 0,
        avgSurvivalRate: 0,
        avgFcr: 0,
        totalMortality: 0,
        trend: []
      }
    }
  },
  computed: {
    filteredExperiments() {
      return this.experiments.filter(item => {
        if (this.filters.workshop && item.workshop !== this.filters.workshop) return false
        if (this.filters.system && item.system !== this.filters.system) return false
        return true
      })
    },
    kpis() {
      return [
        {
          key: 'growth',
          label: '平均增重率',
          value: `${Number(this.summary.avgGrowthRate || 0).toFixed(2)}%`,
          icon: 'el-icon-top',
          color: '#67c23a'
        },
        {
          key: 'survival',
          label: '平均成活率',
          value: `${Number(this.summary.avgSurvivalRate || 0).toFixed(2)}%`,
          icon: 'el-icon-data-line',
          color: '#2d5cf6'
        },
        {
          key: 'fcr',
          label: '平均料肉比',
          value: Number(this.summary.avgFcr || 0).toFixed(2),
          icon: 'el-icon-s-data',
          color: '#9b6dff'
        },
        {
          key: 'mortality',
          label: '累计死亡量',
          value: `${this.summary.totalMortality || 0} 尾`,
          icon: 'el-icon-warning-outline',
          color: '#f56c6c'
        }
      ]
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const expRes = await fetchExperiments({})
      this.experiments = expRes.data
      await this.loadSummary()
    },
    async loadSummary() {
      const res = await fetchAnalysisSummary(this.filters)
      this.summary = res.data
    },
    handleExport() {
      const lines = [
        '指标,数值',
        `平均增重率,${this.summary.avgGrowthRate}%`,
        `平均成活率,${this.summary.avgSurvivalRate}%`,
        `平均料肉比,${this.summary.avgFcr}`,
        `累计死亡量,${this.summary.totalMortality}`
      ]
      const blob = new Blob([lines.join('\n')], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = '分析报告.csv'
      a.click()
      URL.revokeObjectURL(url)
      this.$message.success('报告已导出')
    }
  }
}
</script>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  margin-bottom: 14px;
}

.kpi-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 100px;
}

.kpi-label {
  color: #909399;
  font-size: 13px;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 28px;
  font-weight: 600;
  color: #1f2d3d;
}

.kpi-icon {
  font-size: 28px;
}

.export-row {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 14px;
}

.chart-card {
  min-height: 280px;
}

.chart-empty {
  min-height: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.chart-empty i {
  font-size: 64px;
  margin-bottom: 10px;
}

.trend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trend-item {
  display: grid;
  grid-template-columns: 110px 1fr 120px;
  gap: 12px;
  align-items: center;
}

.trend-date,
.trend-value {
  font-size: 13px;
  color: #606266;
}

.trend-bar-wrap {
  height: 10px;
  background: #f0f2f5;
  border-radius: 999px;
  overflow: hidden;
}

.trend-bar {
  height: 100%;
  background: linear-gradient(90deg, #6b8cff, var(--primary));
  border-radius: 999px;
}

@media (max-width: 768px) {
  .kpi-grid {
    grid-template-columns: 1fr;
  }

  .trend-item {
    grid-template-columns: 1fr;
  }
}
</style>
