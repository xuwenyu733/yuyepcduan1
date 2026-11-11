<template>
  <div class="data-analytics">
    <!-- 统计卡片 / 数据分区 -->
    <el-row :gutter="16" class="stats-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-icon--blue">
              <i class="el-icon-s-promotion"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.ongoingExperiments }}</div>
              <div class="stat-label">进行中的实验</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-icon--green">
              <i class="el-icon-s-marketing"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.avgWeightGainRate }}%</div>
              <div class="stat-label">平均增重率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-icon--orange">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.avgSurvivalRate }}%</div>
              <div class="stat-label">平均成活率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon stat-icon--red">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.warningCount }}</div>
              <div class="stat-label">异常数据预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card class="panel-card" shadow="never">
          <div slot="header" class="panel-header">
            <span class="panel-title">生长趋势分析</span>
          </div>
          <div class="chart-wrap">
            <div ref="growthChart" class="chart-box"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="panel-card" shadow="never">
          <div slot="header" class="panel-header">
            <span class="panel-title">成活率对比</span>
          </div>
          <div class="chart-wrap">
            <div ref="survivalChart" class="chart-box"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近实验 -->
    <el-card class="panel-card recent-card" shadow="never">
      <div slot="header" class="panel-header">
        <span class="panel-title">最近实验</span>
      </div>
      <el-table :data="recentExperiments" class="recent-table">
        <el-table-column label="实验编号" prop="experimentNumber" width="150" />
        <el-table-column label="实验标题" prop="experimentTitle" show-overflow-tooltip />
        <el-table-column label="状态" prop="experimentalStatus" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.experimentalStatus)" size="small">
              {{ getStatusLabel(scope.row.experimentalStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建人" align="center" prop="creator" show-overflow-tooltip>
          <template v-slot="scope">
            <span v-if="scope.row.userName">{{ scope.row.userName }}</span>
            <span v-else-if="scope.row.creator">
              <userName :user-id="scope.row.creator"></userName>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleViewDetail(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import * as ZkExperimentalApi from '@/api/fish/zkexperimental'
import * as ZkExperimentalDataApi from '@/api/fish/zkexperimentaldata'
import * as echarts from 'echarts'
import { getThresholdModule, resolveExperimentThresholds } from '@/utils/thresholdConfig'
import { pushAlarmAndCreateTask } from '@/utils/alarmTaskBridge'
import { silentCatch } from '@/utils/reportSilentError'

export default {
  name: 'DataAnalytics',
  props: {
    experiments: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      stats: {
        ongoingExperiments: 0,
        avgWeightGainRate: 0,
        avgSurvivalRate: 0,
        warningCount: 0
      },
      experimentThreshold: {
        growthMax: 20,
        surviveMin: 80,
        controlMax: 15,
        enabled: true
      },
      recentExperiments: [],
      growthChart: null,
      survivalChart: null
    }
  },
  created() {
    this.loadExperimentThresholds().finally(() => this.loadAnalytics())
  },
  mounted() {
    // 延迟初始化图表，确保 DOM 完全渲染
    this.$nextTick(() => {
      setTimeout(() => {
        this.initCharts()
      }, 100)
    })
  },
  beforeDestroy() {
    // 移除窗口大小监听
    if (this._resizeHandler) {
      window.removeEventListener('resize', this._resizeHandler)
      this._resizeHandler = null
    }
    
    // if (this.growthChart) {
    //   this.growthChart.dispose()
    //   this.growthChart = null
    // }
    // if (this.survivalChart) {
    //   this.survivalChart.dispose()
    //   this.survivalChart = null
    // }
  },
  methods: {
    async loadExperimentThresholds() {
      try {
        const mod = await getThresholdModule('experiment', true)
        this.experimentThreshold = resolveExperimentThresholds(mod)
      } catch (e) {
        console.warn('[实验系统] 阈值加载失败，将使用内置默认值', e && e.message)
        this.thresholdFallback = true
        this.$message && this.$message.warning('实验阈值未加载，已使用默认成活率/增重阈值')
      }
    },
    /** 加载分析数据 */
    async loadAnalytics() {
      try {
        this.loading = true
        await Promise.all([this.loadStats(), this.loadRecentExperiments()])
        // loadChartData 在图表初始化后调用
      } catch (error) {
        this.$modal.msgError('加载分析数据失败')
      } finally {
        this.loading = false
      }
    },

    /**
     * collectData 入库格式为字段数组 [{keyEn,keyCh,val}]，
     * 历史数据也可能是扁平对象；统一解析为规范字段。
     */
    parseCollectMetrics(collectData) {
      if (collectData == null || collectData === '') return {}
      let raw = collectData
      if (typeof raw === 'string') {
        try {
          raw = JSON.parse(raw)
        } catch (e) {
          return {}
        }
      }
      const pickNum = (v) => {
        if (v === '' || v === null || v === undefined) return undefined
        const n = Number(v)
        return Number.isFinite(n) ? n : undefined
      }
      const map = {}
      const applyAliases = (key, num, ch = '') => {
        if (num === undefined) return
        if (key) map[key] = num
        const k = String(key || '')
        const label = String(ch || '')
        if (
          k === 'survivalRate' || k === 'survival' ||
          label.includes('成活率') || label.includes('存活率')
        ) map.survivalRate = num
        if (
          k === 'weightGainRate' || k === 'gainRate' || k === 'weightGain' ||
          label.includes('增重率')
        ) map.weightGainRate = num
        if (
          k === 'startingWeight' || k === 'startWeight' || k === 'initialWeight' ||
          label.includes('起始重量') || label.includes('初始重量') || label.includes('开始重量')
        ) map.startingWeight = num
        if (
          k === 'endWeight' || k === 'finalWeight' ||
          label.includes('结束重量') || label.includes('末重') || label.includes('终末重量')
        ) map.endWeight = num
        if (k === 'mortality' || label.includes('死亡量')) map.mortality = num
      }
      if (Array.isArray(raw)) {
        raw.forEach((f) => {
          if (!f || typeof f !== 'object') return
          applyAliases(f.keyEn, pickNum(f.val), f.keyCh || '')
        })
        return map
      }
      if (raw && typeof raw === 'object') {
        Object.keys(raw).forEach((k) => applyAliases(k, pickNum(raw[k]), ''))
        return map
      }
      return {}
    },

    isGrowthCollection(type) {
      return type && (String(type).includes('生长') || String(type).includes('增重'))
    },
    isSurvivalCollection(type) {
      return type && (String(type).includes('存活') || String(type).includes('成活'))
    },

    /** 百分数：0~1 的小数比率转为 %（0 保持为 0） */
    toPercent(rate) {
      if (rate == null || !Number.isFinite(Number(rate))) return null
      let r = Number(rate)
      if (r > 0 && r <= 1) r = r * 100
      return r
    },

    /**
     * 有效成活率：排除「未计算」占位（死亡量=0 且成活率仍为默认 0）。
     */
    resolveSurvivalRate(metrics) {
      const rate = this.toPercent(metrics.survivalRate)
      if (rate == null) return null
      const mortality = metrics.mortality
      if (rate === 0 && (mortality == null || mortality === 0)) return null
      return rate
    },

    /** 单条生长记录的增重（结束重量 - 起始重量） */
    calcWeightGain(metrics) {
      const end = metrics.endWeight
      const start = metrics.startingWeight
      if (end == null || start == null) return null
      return end - start
    },

    /** 单条生长记录的增重率（百分数）；无字段时用 (结束-起始)/起始*100 */
    calcWeightGainRate(metrics) {
      let rate = null
      if (metrics.weightGainRate != null && Number.isFinite(Number(metrics.weightGainRate))) {
        rate = Number(metrics.weightGainRate)
      } else {
        const end = metrics.endWeight
        const start = metrics.startingWeight
        if (end == null || start == null || !start) return null
        rate = (end - start) / start
      }
      return this.toPercent(rate)
    },

    /** 加载统计数据 */
    async loadStats() {
      const ongoing = this.experiments.filter(e => e.experimentalStatus === 2).length
      this.stats.ongoingExperiments = ongoing

      // 加载所有实验数据来计算平均值
      try {
        const res = await ZkExperimentalDataApi.getZkExperimentalDataPage({
          pageNo: 1,
          pageSize: 500
        })
        const dataList = (res.data && res.data.list) || []

        // 平均增重率：所有生长记录增重率的算术平均
        let totalWeightGain = 0
        let weightGainCount = 0
        // 成活率：先按实验求均值，再对实验均值求平均
        const survivalByExp = {}
        let warningCount = 0
        const th = this.experimentThreshold || {}

        dataList.forEach(item => {
          try {
            const metrics = this.parseCollectMetrics(item.collectData)
            if (this.isGrowthCollection(item.collectionType)) {
              const rate = this.calcWeightGainRate(metrics)
              if (rate != null) {
                totalWeightGain += rate
                weightGainCount++
                if (th.enabled && Math.abs(rate) > Number(th.growthMax || 20)) warningCount++
              }
            }
            if (this.isSurvivalCollection(item.collectionType)) {
              const rate = this.resolveSurvivalRate(metrics)
              if (rate != null) {
                const expKey = item.experimentNumber || item.experimentId || 'unknown'
                if (!survivalByExp[expKey]) survivalByExp[expKey] = []
                survivalByExp[expKey].push(rate)
                if (th.enabled && rate < Number(th.surviveMin || 80)) {
                  warningCount++
                  pushAlarmAndCreateTask({
                    source: 'experiment',
                    category: 'fish',
                    pondDetailNum: item.experimentNumber || '',
                    pondName: item.experimentTitle || item.experimentNumber || '实验',
                    title: `实验成活率偏低 ${item.experimentNumber || ''} =${rate}%`,
                    content: `成活率 ${rate}% 低于阈值 ${th.surviveMin}%`,
                    taskTitle: `巡检：实验 ${item.experimentNumber || ''} 成活率异常`,
                    level: 'priority',
                    period: 'day'
                  }).catch(silentCatch('实验系统-成活率报警'))
                }
              }
            }
          } catch (e) {
            console.warn('[实验分析] 统计行解析失败', e && e.message)
          }
        })

        const expAvgs = Object.keys(survivalByExp).map(k => {
          const arr = survivalByExp[k]
          return arr.reduce((a, b) => a + b, 0) / arr.length
        })

        this.stats.avgWeightGainRate = weightGainCount > 0
          ? parseFloat((totalWeightGain / weightGainCount).toFixed(2))
          : 0
        this.stats.avgSurvivalRate = expAvgs.length > 0
          ? parseFloat((expAvgs.reduce((a, b) => a + b, 0) / expAvgs.length).toFixed(2))
          : 0
        this.stats.warningCount = warningCount
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },

    /** 加载最近实验 */
    loadRecentExperiments() {
      this.recentExperiments = this.experiments
        .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        .slice(0, 10)
    },

    /** 加载图表数据 */
    async loadChartData() {
      // 确保图表实例已初始化
      if (!this.growthChart || !this.survivalChart) {
        console.warn('图表实例未初始化，延迟加载数据')
        setTimeout(() => {
          this.loadChartData()
        }, 200)
        return
      }

      let dataList = []
      try {
        const res = await ZkExperimentalDataApi.getZkExperimentalDataPage({
          pageNo: 1,
          pageSize: 500
        })
        dataList = (res.data && res.data.list) || []
      } catch (error) {
        console.error('加载实验采集数据失败:', error)
        this.$message && this.$message.warning('图表数据加载失败，已显示空图')
        this.updateGrowthChart([])
        this.updateSurvivalChart([])
        return
      }

      // 生长趋势：按日期，取每次记录 (结束重量 - 起始重量) 的平均值
      try {
        const growthDataMap = {}
        dataList.forEach(item => {
          try {
            if (!this.isGrowthCollection(item.collectionType)) return
            const metrics = this.parseCollectMetrics(item.collectData)
            const gain = this.calcWeightGain(metrics)
            if (gain == null) return
            const date = this.parseTime(item.createTime, '{y}-{m}-{d}')
            if (!date) return
            if (!growthDataMap[date]) growthDataMap[date] = []
            growthDataMap[date].push(gain)
          } catch (e) {
            console.warn('[实验分析] 生长数据行解析失败', e && e.message)
          }
        })

        const growthChartData = Object.keys(growthDataMap)
          .sort()
          .slice(-6)
          .map(date => ({
            date,
            value: parseFloat((growthDataMap[date].reduce((a, b) => a + b, 0) / growthDataMap[date].length).toFixed(2))
          }))

        if (growthChartData.length === 0) {
          console.warn('[实验分析] 暂无生长趋势真实数据')
        }
        this.updateGrowthChart(growthChartData)
      } catch (error) {
        console.error('加载生长数据失败:', error)
        this.$message && this.$message.warning('生长趋势加载失败，已显示空图')
        this.updateGrowthChart([])
      }

      // 成活率对比：每个柱子 = 该实验全部成活率记录的平均值
      try {
        const survivalDataMap = {}
        dataList.forEach(item => {
          try {
            if (!this.isSurvivalCollection(item.collectionType)) return
            const metrics = this.parseCollectMetrics(item.collectData)
            const rate = this.resolveSurvivalRate(metrics)
            if (rate == null) return
            const expNum = item.experimentNumber || String(item.experimentId || '未知')
            if (!survivalDataMap[expNum]) survivalDataMap[expNum] = []
            survivalDataMap[expNum].push(rate)
          } catch (e) {
            console.warn('[实验分析] 成活率行解析失败', e && e.message)
          }
        })

        const survivalChartData = Object.keys(survivalDataMap)
          .slice(0, 8)
          .map(expNum => ({
            name: expNum,
            value: parseFloat((survivalDataMap[expNum].reduce((a, b) => a + b, 0) / survivalDataMap[expNum].length).toFixed(2))
          }))

        if (survivalChartData.length === 0) {
          console.warn('[实验分析] 暂无成活率真实数据')
        }
        this.updateSurvivalChart(survivalChartData)
      } catch (error) {
        console.error('加载成活率数据失败:', error)
        this.$message && this.$message.warning('成活率图表加载失败，已显示空图')
        this.updateSurvivalChart([])
      }
    },

    /** 初始化图表 */
    initCharts() {
      if (!this.$refs.growthChart || !this.$refs.survivalChart) {
        // 如果 DOM 还没准备好，延迟重试
        setTimeout(() => {
          this.initCharts()
        }, 100)
        return
      }

      try {
        // 初始化生长趋势图（空图占位，随后由 loadChartData 填真实数据）
        if (this.$refs.growthChart && !this.growthChart) {
          this.growthChart = echarts.init(this.$refs.growthChart)
          this.updateGrowthChart([])
        }

        // 初始化成活率图
        if (this.$refs.survivalChart && !this.survivalChart) {
          this.survivalChart = echarts.init(this.$refs.survivalChart)
          this.updateSurvivalChart([])
        }

        // 监听窗口大小变化，自动调整图表大小
        if (!this._resizeHandler) {
          this._resizeHandler = this.handleResize
          window.addEventListener('resize', this._resizeHandler)
        }

        // 延迟加载真实数据，如果有数据会替换示例数据
        setTimeout(() => {
          this.loadChartData()
        }, 500)
      } catch (error) {
        console.error('初始化图表失败:', error)
        // 即使出错也保持空图（样例已落库 zk_chart_seed，由 getSample* 异步拉取）
        if (this.$refs.growthChart && !this.growthChart) {
          try {
            this.growthChart = echarts.init(this.$refs.growthChart)
            this.updateGrowthChart([])
          } catch (e) {
            console.error('初始化生长趋势图失败:', e)
          }
        }
        if (this.$refs.survivalChart && !this.survivalChart) {
          try {
            this.survivalChart = echarts.init(this.$refs.survivalChart)
            this.updateSurvivalChart([])
          } catch (e) {
            console.error('初始化成活率图失败:', e)
          }
        }
      }
    },

    /** 处理窗口大小变化 */
    handleResize() {
      if (this.growthChart) {
        this.growthChart.resize()
      }
      if (this.survivalChart) {
        this.survivalChart.resize()
      }
    },

    /** 更新生长趋势图 */
    updateGrowthChart(data) {
      if (!this.growthChart) {
        console.warn('生长趋势图表实例不存在，尝试重新初始化')
        if (this.$refs.growthChart) {
          this.growthChart = echarts.init(this.$refs.growthChart)
        } else {
          return
        }
      }

      // 无真实业务数据时显示空图（不再回填种子，避免覆盖真实数据 / 假 0）
      if (!data || !Array.isArray(data)) data = []

      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(50, 50, 50, 0.9)',
          borderColor: 'rgba(255, 255, 255, 0.2)',
          textStyle: {
            color: '#fff'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: data.map(d => d.date || ''),
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.3)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.8)'
          }
        },
        yAxis: {
          type: 'value',
          name: '增重(g)',
          nameTextStyle: { color: 'rgba(255, 255, 255, 0.55)' },
          min: 0,
          scale: true,
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.3)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.8)'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.1)'
            }
          }
        },
        series: [
          {
            name: '平均增重',
            type: 'line',
            data: data.map(d => {
              const val = parseFloat(d.value)
              return Number.isFinite(val) ? parseFloat(val.toFixed(2)) : null
            }),
            smooth: true,
            itemStyle: {
              color: '#3db88c'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(61, 184, 140, 0.28)' },
                  { offset: 1, color: 'rgba(61, 184, 140, 0.03)' }
                ]
              }
            }
          }
        ]
      }

      try {
        this.growthChart.setOption(option, true) // true 表示不合并，完全替换
        console.log('生长趋势图更新成功，数据点数量:', data.length)
      } catch (error) {
        console.error('更新生长趋势图失败:', error)
      }
    },

    /** 更新成活率图 */
    updateSurvivalChart(data) {
      if (!this.survivalChart) {
        console.warn('成活率图表实例不存在，尝试重新初始化')
        if (this.$refs.survivalChart) {
          this.survivalChart = echarts.init(this.$refs.survivalChart)
        } else {
          return
        }
      }

      if (!data || !Array.isArray(data)) data = []

      const names = data.map(d => d.name || '')
      const values = data.map(d => {
        const val = parseFloat(d.value)
        return Number.isFinite(val) ? parseFloat(val.toFixed(2)) : 0
      })

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          backgroundColor: 'rgba(50, 50, 50, 0.9)',
          borderColor: 'rgba(255, 255, 255, 0.2)',
          textStyle: {
            color: '#fff'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.3)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.8)'
          }
        },
        yAxis: {
          type: 'value',
          name: '成活率(%)',
          nameTextStyle: { color: 'rgba(255, 255, 255, 0.55)' },
          // 必须从 0 起，否则低于 90% 的柱子会被裁切看起来像「始终为 0」
          min: 0,
          max: 100,
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.3)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.8)'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.1)'
            }
          }
        },
        series: [
          {
            name: '成活率',
            type: 'bar',
            data: values,
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: '#faad14' },
                  { offset: 1, color: '#d48806' }
                ]
              },
              borderRadius: [4, 4, 0, 0]
            },
            barWidth: '48%'
          }
        ]
      }

      try {
        this.survivalChart.setOption(option, true) // true 表示不合并，完全替换
        console.log('成活率图更新成功，数据点数量:', data.length)
      } catch (error) {
        console.error('更新成活率图失败:', error)
      }
    },

    /** 生长趋势：已改为仅展示真实采集数据，种子方法保留兼容旧调用 */
    async getSampleGrowthChartData() {
      return []
    },

    /** 成活率：已改为仅展示真实采集数据 */
    async getSampleSurvivalChartData() {
      return []
    },

    /** 计算进度 */
    calculateProgress(experiment) {
      if (!experiment.startTime || !experiment.endTime) return 0
      const now = new Date().getTime()
      const start = new Date(experiment.startTime).getTime()
      const end = new Date(experiment.endTime).getTime()
      if (now < start) return 0
      if (now > end) return 100
      return Math.round(((now - start) / (end - start)) * 100)
    },

    /** 获取进度状态 */
    getProgressStatus(experiment) {
      const progress = this.calculateProgress(experiment)
      if (progress === 100) return 'success'
      if (progress > 50) return ''
      return 'exception'
    },

    /** 获取状态类型 */
    getStatusType(status) {
      const statusMap = {
        0: 'info',
        1: 'warning',
        2: 'success',
        3: '',
        4: 'info'
      }
      return statusMap[status] || 'info'
    },

    /** 获取状态标签 */
    getStatusLabel(status) {
      const statusMap = {
        0: '待开始',
        1: '计划中',
        2: '进行中',
        3: '已完成',
        4: '已总结'
      }
      return statusMap[status] || '未知'
    },

    /** 查看详情 */
    handleViewDetail(row) {
      // 可以跳转到详情页或打开详情对话框
      // this.$modal.msgInfo(`查看实验: ${row.experimentTitle}`)
      this.$emit('edit', row.id)
    }
  }
}
</script>

<style scoped lang="scss">
.data-analytics {
  ::v-deep .el-card {
    background-color: #12202d !important;
    border: 1px solid rgba(255, 255, 255, 0.08) !important;
    border-radius: 10px !important;
    color: #ffffff !important;
    box-shadow: none !important;

    .el-card__header {
      background-color: transparent !important;
      border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
      color: #ffffff !important;
      padding: 14px 18px !important;
    }

    .el-card__body {
      background-color: transparent !important;
      color: #ffffff !important;
      padding: 16px 18px !important;
    }
  }

  .panel-header {
    display: flex;
    align-items: center;
  }

  .panel-title {
    font-size: 15px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.9);
    letter-spacing: 0.2px;
  }

  .chart-row {
    margin-top: 16px;
  }

  .chart-wrap {
    width: 100%;
  }

  .chart-box {
    width: 100%;
    height: 300px;
  }

  .recent-card {
    margin-top: 16px;
  }

  .stats-cards {
    margin-bottom: 4px;

    .stat-card {
      background: #12202d !important;
      border: 1px solid rgba(255, 255, 255, 0.08) !important;
      border-radius: 10px !important;
      box-shadow: none !important;

      ::v-deep .el-card__body {
        padding: 18px 20px !important;
      }

      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;
        min-height: 56px;

        .stat-icon {
          flex-shrink: 0;
          width: 48px;
          height: 48px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 22px;

          &--blue {
            background: #e6f7ff !important;
            color: #1890ff !important;
          }

          &--green {
            background: #f6ffed !important;
            color: #52c41a !important;
          }

          &--orange {
            background: #fff7e6 !important;
            color: #fa8c16 !important;
          }

          &--red {
            background: #fff1f0 !important;
            color: #f5222d !important;
          }
        }

        .stat-info {
          flex: 1;
          min-width: 0;

          .stat-value {
            font-size: 26px;
            font-weight: 700;
            line-height: 1.15;
            color: #ffffff;
            margin-bottom: 6px;
            letter-spacing: 0.02em;
          }

          .stat-label {
            font-size: 13px;
            line-height: 1.3;
            color: #8b9aab;
            white-space: nowrap;
          }
        }
      }
    }
  }

  // 表格样式
  ::v-deep .el-table {
    background-color: transparent !important;
    color: #ffffff !important;

    &::before {
      background-color: transparent !important;
    }

    .el-table__header-wrapper {
      th {
        background-color: rgba(255, 255, 255, 0.03) !important;
        color: #8b9aab !important;
        border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
        font-weight: 500 !important;
      }
    }

    .el-table__body-wrapper {
      tr {
        background-color: transparent !important;

        &:hover > td {
          background-color: rgba(255, 255, 255, 0.04) !important;
        }

        td {
          border-bottom: 1px solid rgba(255, 255, 255, 0.06) !important;
          color: rgba(255, 255, 255, 0.85) !important;
        }
      }
    }
  }

  ::v-deep .el-progress {
    .el-progress__text {
      color: rgba(255, 255, 255, 0.8) !important;
    }
  }
}
</style>