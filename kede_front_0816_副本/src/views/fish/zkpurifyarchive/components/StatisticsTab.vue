<template>
  <div class="tab-content" v-loading="loading">
    <!-- 筛选栏 -->
    <el-form :inline="true" size="small" style="margin-bottom: 20px">
      <el-form-item label="品类">
        <el-select v-model="queryParams.category" placeholder="全部品类" clearable @change="getStatistics">
          <el-option v-for="dict in getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-select v-model="queryParams.timeRange" placeholder="全部时间" clearable @change="getStatistics">
          <el-option label="近7天" value="7" />
          <el-option label="近30天" value="30" />
          <el-option label="近90天" value="90" />
        </el-select>
      </el-form-item>
      <el-form-item label="记录单号">
        <el-select v-model="queryParams.recordNumber" placeholder="全部记录单号" clearable filterable @change="getStatistics">
          <el-option v-for="item in processList" :key="item.id" :label="item.recordNumber" :value="item.recordNumber" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 顶部指标卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div slot="header" class="flex-between">
            <span>重量变化</span>
            <i :class="netWeightChange >= 0 ? 'el-icon-top text-success' : 'el-icon-bottom text-danger'"></i>
          </div>
          <div class="stat-value" :class="netWeightChange >= 0 ? 'up' : 'down'">
            {{ netWeightChange >= 0 ? '+' : '' }}{{ formatNumber(netWeightChange) }} kg
          </div>
          <div class="stat-sub">入: {{ formatNumber(stats.totalInWeight) }} / 出: {{ formatNumber(stats.totalOutWeight) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div slot="header" class="flex-between">
            <span>死亡率</span>
            <i class="el-icon-warning-outline text-warning"></i>
          </div>
          <div class="stat-value warn">{{ formatNumber(stats.deathRate) }}%</div>
          <div class="stat-sub">死亡: {{ stats.totalDeathCount || 0 }} 尾 / {{ formatNumber(stats.totalDeathWeight) }} kg
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div slot="header" class="flex-between">
            <span>掉秤率</span>
            <i class="el-icon-scale-to-original text-primary"></i>
          </div>
          <div class="stat-value primary">{{ formatNumber(stats.weightLossRate) }}%</div>
          <div class="stat-sub">累计掉秤: {{ formatNumber(stats.totalWeightLoss) }} kg</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div slot="header" class="flex-between">
            <span>销售金额</span>
            <i class="el-icon-money text-success"></i>
          </div>
          <div class="stat-value price">¥{{ formatNumber(stats.totalSalesAmount, 0) }}</div>
          <div class="stat-sub">平均单价: ¥{{ formatNumber(stats.averagePrice) }}/kg</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 品类与规格分布 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card header="品类分布">
          <div ref="categoryChart" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="规格变化统计 (入池 vs 出池)">
          <div ref="specChart" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成本与收益分析 -->
    <el-card header="成本与收益分析" style="margin-top: 20px">
      <div class="analysis-grid">
        <div class="analysis-item">
          <p class="label">总投喂量</p>
          <p class="value">{{ formatNumber(stats.totalFeedAmount) }} kg</p>
        </div>
        <div class="analysis-item">
          <p class="label">销售总金额</p>
          <p class="value text-success">¥{{ formatNumber(stats.totalSalesAmount) }}</p>
        </div>
        <div class="analysis-item">
          <p class="label">平均售价</p>
          <p class="value text-primary">¥{{ formatNumber(stats.averagePrice) }}/kg</p>
        </div>
      </div>
    </el-card>

    <!-- 成本核算 -->
    <el-card header="成本核算" style="margin-top: 20px">
      <div class="cost-detail-grid">
        <div class="cost-box bg-blue">
          <p class="label">采购成本</p>
          <p class="value">¥{{ formatNumber(stats.totalPurchaseCost) }}</p>
        </div>
        <div class="cost-box bg-purple">
          <p class="label">人工成本</p>
          <p class="value">¥{{ formatNumber(stats.totalLaborCost) }}</p>
        </div>
        <div class="cost-box bg-yellow">
          <p class="label">电费</p>
          <p class="value">¥{{ formatNumber(stats.totalElectricityCost) }}</p>
        </div>
        <div class="cost-box bg-green">
          <p class="label">饲料成本</p>
          <p class="value">¥{{ formatNumber(stats.totalFeedCost) }}</p>
        </div>
        <div class="cost-box bg-pink">
          <p class="label">动保成本</p>
          <p class="value">¥{{ formatNumber(stats.totalHealthProductCost) }}</p>
        </div>
      </div>
      <div class="total-profit-grid" style="margin-top: 20px">
        <div class="total-box bg-gray">
          <p class="label">总成本</p>
          <p class="value">¥{{ formatNumber(stats.totalCost) }}</p>
        </div>
        <div class="total-box bg-light-green">
          <p class="label">总利润</p>
          <p class="value" :class="stats.totalProfit >= 0 ? 'text-success' : 'text-danger'">¥{{
            formatNumber(stats.totalProfit) }}</p>
        </div>
        <div class="total-box bg-light-blue">
          <p class="label">利润率</p>
          <p class="value" :class="stats.profitMargin >= 0 ? 'text-primary' : 'text-danger'">{{
            formatNumber(stats.profitMargin) }}%</p>
        </div>
      </div>
      <div class="formula-box" style="margin-top: 15px">
        <p><strong>成本计算公式：</strong></p>
        <p>• 总成本 = 采购成本 + 人工成本 + 电费 + 饲料成本 + 动保成本</p>
        <p>• 总利润 = 销售总额 - 总成本</p>
        <p>• 利润率 = (总利润 / 销售总额) × 100%</p>
      </div>
    </el-card>

    <!-- 损耗分析详情 -->
    <el-card header="损耗分析详情" style="margin-top: 20px">
      <div class="loss-grid">
        <div class="loss-box bg-red">
          <p class="label">死亡数量</p>
          <p class="value">{{ stats.totalDeathCount || 0 }} 尾</p>
        </div>
        <div class="loss-box bg-red">
          <p class="label">死亡率</p>
          <p class="value text-danger font-bold">{{ formatNumber(stats.deathRate) }}%</p>
        </div>
        <div class="loss-box bg-orange">
          <p class="label">掉秤重量</p>
          <p class="value">{{ formatNumber(stats.totalWeightLoss) }} kg</p>
        </div>
        <div class="loss-box bg-orange">
          <p class="label">掉秤率</p>
          <p class="value text-warning font-bold">{{ formatNumber(stats.weightLossRate) }}%</p>
        </div>
      </div>
      <div class="formula-box" style="margin-top: 10px">
        <p>* 死亡率 = 死亡数量 / 入池数量 × 100%</p>
        <p>* 掉秤率 = 掉秤重量 / 入池重量 × 100%</p>
      </div>
    </el-card>

    <!-- 阶段性死亡率分析 -->
    <el-card header="阶段性死亡率分析" style="margin-top: 20px">
      <div ref="trendChart" style="height: 400px"></div>
    </el-card>

    <!-- 各批次详细数据 -->
    <el-card header="各批次详细数据" style="margin-top: 20px">
      <el-table :data="stats.stageMortalityAnalysis" stripe border size="mini">
        <el-table-column label="记录单号" prop="recordNumber" width="150" />
        <el-table-column label="品类" prop="category" width="100">
          <template v-slot="scope">
            <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="池号" prop="poolNumber" width="80" />
        <el-table-column label="入池时间" prop="inPoolTime" width="140">
          <template v-slot="scope">{{ parseTime(scope.row.inPoolTime, '{y}-{m}-{d}') }}</template>
        </el-table-column>
        <el-table-column label="入池数量(尾)" prop="inPoolCount" width="100" />
        <el-table-column label="3天" align="center">
          <el-table-column label="死亡量" prop="day3Deaths" width="70" />
          <el-table-column label="死亡率" width="80">
            <template v-slot="scope">
              <el-tag :type="scope.row.day3Rate > 5 ? 'danger' : 'info'" size="mini">{{ formatNumber(scope.row.day3Rate)
                }}%</el-tag>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="5天" align="center">
          <el-table-column label="死亡量" prop="day5Deaths" width="70" />
          <el-table-column label="死亡率" width="80">
            <template v-slot="scope">
              <el-tag :type="scope.row.day5Rate > 8 ? 'danger' : 'info'" size="mini">{{ formatNumber(scope.row.day5Rate)
                }}%</el-tag>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="7天" align="center">
          <el-table-column label="死亡量" prop="day7Deaths" width="70" />
          <el-table-column label="死亡率" width="80">
            <template v-slot="scope">
              <el-tag :type="scope.row.day7Rate > 10 ? 'danger' : 'info'" size="mini">{{
                formatNumber(scope.row.day7Rate) }}%</el-tag>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="累计" align="center">
          <el-table-column label="死亡量" prop="totalDeaths" width="70" />
          <el-table-column label="死亡率" width="80">
            <template v-slot="scope">
              <el-tag :type="scope.row.totalRate > 15 ? 'danger' : 'info'" size="mini" effect="dark">{{
                formatNumber(scope.row.totalRate) }}%</el-tag>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 批次散点图 -->
    <el-card header="批次散点图：掉秤率 vs 死亡率" style="margin-top: 20px">
      <div ref="scatterChart" style="height: 500px"></div>
      <div class="formula-box" style="margin-top: 15px">
        <p><strong>图表说明：</strong></p>
        <p>• 横坐标：掉秤率 (%) - 养殖过程中的重量损失比例</p>
        <p>• 纵坐标：死亡率 (%) - 养殖过程中的死亡比例</p>
        <p>• 点的大小：入池重量 (kg) - 点越大表示入池重量越大</p>
        <p>• 不同颜色：代表不同的品类</p>
        <p>• 理想批次：应位于左下角（低掉秤率、低死亡率）</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import * as echarts from 'echarts';

export default {
  name: "StatisticsTab",
  data() {
    return {
      loading: false,
      processList: [],
      stats: {
        totalInWeight: 0,
        totalOutWeight: 0,
        deathRate: 0,
        totalDeathCount: 0,
        totalDeathWeight: 0,
        totalWeightLoss: 0,
        weightLossRate: 0,
        totalSalesAmount: 0,
        averagePrice: 0,
        totalCost: 0,
        totalProfit: 0,
        profitMargin: 0,
        totalFeedAmount: 0,
        totalPurchaseCost: 0,
        totalLaborCost: 0,
        totalElectricityCost: 0,
        totalFeedCost: 0,
        totalHealthProductCost: 0,
        stageMortalityAnalysis: [],
        batchScatterData: []
      },
      queryParams: {
        category: null,
        timeRange: null,
        recordNumber: null
      },
      charts: {
        category: null,
        spec: null,
        scatter: null,
        trend: null
      }
    };
  },
  computed: {
    netWeightChange() {
      return (this.stats.totalOutWeight || 0) - (this.stats.totalInWeight || 0);
    }
  },
  mounted() {
    this.getProcessList();
    this.getStatistics();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    async getProcessList() {
      const res = await PurifyProcessApi.getPurifyProcessPage({ pageSize: 500 });
      this.processList = res.data.list;
      // console.log(`processList======================>${JSON.stringify(this.processList)}`);
    },
    async getStatistics() {
      this.loading = true;
      try {
        const res = await PurifyArchiveApi.getPurifyStats(this.queryParams);
        // console.log(`getStatistics================>${JSON.stringify(res)}`);
        // 打印信息，方便回显报表
        console.log(`   `)
        console.log(`   `)
        if (res.data) {
          this.stats = { ...this.stats, ...res.data };
          this.$nextTick(() => {
            this.renderCharts(res.data);
          });
        }
      } catch (error) {
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    resetQuery() {
      this.queryParams = {
        category: null,
        timeRange: null,
        recordNumber: null
      };
      this.getStatistics();
    },
    renderCharts(data) {
      this.$nextTick(() => {
        // Category Chart
        if (this.$refs.categoryChart && this.$refs.categoryChart.clientWidth > 0) {
          const catData = Object.entries(data.categoryDistribution || {}).map(([name, value]) => {
            const label = getDictDataLabel(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY, name) || name;
            return { name: label, value };
          });
          const categoryOption = {
            title: { text: '品类占比', left: 'center', textStyle: { color: '#fff' } },
            tooltip: { trigger: 'item' },
            series: [{
              type: 'pie',
              radius: ['40%', '70%'],
              data: catData,
              emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
            }]
          };
          if (!this.charts.category) this.charts.category = echarts.init(this.$refs.categoryChart);
          this.charts.category.setOption(categoryOption);
        }

        // Spec Chart
        if (this.$refs.specChart && this.$refs.specChart.clientWidth > 0) {
          const specLabels = Object.keys(data.specComparison || {});
          const specIn = specLabels.map(l => data.specComparison[l][0]);
          const specOut = specLabels.map(l => data.specComparison[l][1]);
          const specOption = {
            title: { text: '规格统计', left: 'center', textStyle: { color: '#fff' } },
            tooltip: { trigger: 'axis' },
            legend: { data: ['入池', '出池'], bottom: 0, textStyle: { color: '#ccc' } },
            xAxis: { type: 'category', data: specLabels, axisLabel: { color: '#ccc' } },
            yAxis: { type: 'value', axisLabel: { color: '#ccc' } },
            series: [
              { name: '入池', type: 'bar', data: specIn, itemStyle: { color: '#409EFF' } },
              { name: '出池', type: 'bar', data: specOut, itemStyle: { color: '#67C23A' } }
            ]
          };
          if (!this.charts.spec) this.charts.spec = echarts.init(this.$refs.specChart);
          this.charts.spec.setOption(specOption);
        }

        // Trend Chart
        if (this.$refs.trendChart && this.$refs.trendChart.clientWidth > 0) {
          const trendData = data.stageMortalityAnalysis || [];
          const trendOption = {
            title: { text: '阶段性死亡率分析', left: 'center', textStyle: { color: '#fff' } },
            tooltip: { trigger: 'axis' },
            legend: { data: ['3天死亡率', '5天死亡率', '7天死亡率'], bottom: 0, textStyle: { color: '#ccc' } },
            xAxis: { type: 'category', data: trendData.map(d => d.recordNumber), axisLabel: { color: '#ccc' } },
            yAxis: { type: 'value', axisLabel: { formatter: '{value}%', color: '#ccc' } },
            series: [
              { name: '3天死亡率', type: 'line', data: trendData.map(d => d.day3Rate), smooth: true },
              { name: '5天死亡率', type: 'line', data: trendData.map(d => d.day5Rate), smooth: true },
              { name: '7天死亡率', type: 'line', data: trendData.map(d => d.day7Rate), smooth: true }
            ]
          };
          if (!this.charts.trend) this.charts.trend = echarts.init(this.$refs.trendChart);
          this.charts.trend.setOption(trendOption);
        }

        // Scatter Chart
        if (this.$refs.scatterChart && this.$refs.scatterChart.clientWidth > 0) {
          const scatterData = (data.batchScatterData || []).map(d => [
            d.weightLossRate,
            d.mortalityRate,
            d.inPoolWeight,
            d.recordNumber,
            d.category
          ]);
          const scatterOption = {
            title: { text: '损耗分布 (掉秤率 vs 死亡率)', left: 'center', textStyle: { color: '#fff' } },
            tooltip: {
              formatter: (p) => {
                const d = p.data;
                return `${d[3]}<br/>品类: ${d[4]}<br/>掉秤率: ${d[0]}%<br/>死亡率: ${d[1]}%<br/>入池重量: ${d[2]}kg`;
              }
            },
            xAxis: { name: '掉秤率(%)', nameTextStyle: { color: '#ccc' }, axisLabel: { color: '#ccc' }, splitLine: { lineStyle: { type: 'dashed' } } },
            yAxis: { name: '死亡率(%)', nameTextStyle: { color: '#ccc' }, axisLabel: { color: '#ccc' }, splitLine: { lineStyle: { type: 'dashed' } } },
            series: [{
              type: 'scatter',
              symbolSize: (val) => Math.sqrt(val[2]) * 2,
              data: scatterData,
              itemStyle: { color: '#F56C6C' }
            }]
          };
          if (!this.charts.scatter) this.charts.scatter = echarts.init(this.$refs.scatterChart);
          this.charts.scatter.setOption(scatterOption);
        }
      });
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => chart && chart.resize());
    },
    formatNumber(val, precision = 2) {
      if (val === null || val === undefined || isNaN(val)) {
        return (0).toFixed(precision);
      }
      return parseFloat(val).toFixed(precision);
    }
  }
};
</script>

<style scoped>
.tab-content {
  padding: 10px 0;
}

.stat-card {
  text-align: center;
  margin-bottom: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(84, 144, 122, 0.28);
  color: #fff;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.stat-value.up {
  color: #67C23A;
}

.stat-value.down {
  color: #F56C6C;
}

.stat-value.warn {
  color: #E6A23C;
}

.stat-value.price {
  color: #409EFF;
}

.stat-value.primary {
  color: #409EFF;
}

.stat-sub {
  font-size: 12px;
  color: #b7c4d2;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-success {
  color: #67C23A;
}

.text-danger {
  color: #F56C6C;
}

.text-warning {
  color: #E6A23C;
}

.text-primary {
  color: #409EFF;
}

.font-bold {
  font-weight: bold;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  text-align: center;
}

.analysis-item .label {
  font-size: 14px;
  color: #b7c4d2;
  margin-bottom: 8px;
}

.analysis-item .value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.cost-detail-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

.cost-box {
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  background-color: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(84, 144, 122, 0.28);
}

.cost-box .label {
  font-size: 12px;
  color: #b7c4d2;
  margin-bottom: 5px;
}

.cost-box .value {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.bg-blue {
  border-left: 4px solid #409EFF;
}

.bg-purple {
  border-left: 4px solid #B173F6;
}

.bg-yellow {
  border-left: 4px solid #E6A23C;
}

.bg-green {
  border-left: 4px solid #67C23A;
}

.bg-pink {
  border-left: 4px solid #F56C6C;
}

.total-profit-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.total-box {
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  background-color: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(84, 144, 122, 0.28);
}

.total-box .label {
  font-size: 14px;
  color: #b7c4d2;
  margin-bottom: 10px;
}

.total-box .value {
  font-size: 28px;
  font-weight: bold;
}

.loss-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.loss-box {
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  background-color: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(84, 144, 122, 0.28);
}

.loss-box .label {
  font-size: 12px;
  color: #b7c4d2;
  margin-bottom: 5px;
}

.loss-box .value {
  font-size: 20px;
  color: #fff;
}

.formula-box {
  font-size: 12px;
  color: #b7c4d2;
  background: rgba(255, 255, 255, 0.03);
  padding: 15px;
  border-radius: 4px;
  border: 1px dashed rgba(84, 144, 122, 0.35);
  line-height: 1.8;
}

.formula-box strong {
  color: #E6A23C;
  margin-bottom: 5px;
  display: inline-block;
}

/* 覆盖 Element UI 卡片在深色模式下的样式 */
::v-deep .el-card {
  background-color: #1d1e1f !important;
  border: 1px solid rgba(84, 144, 122, 0.28) !important;
  color: #fff !important;
}

::v-deep .el-card__header {
  border-bottom: 1px solid rgba(84, 144, 122, 0.28) !important;
  color: #fff !important;
}

::v-deep .el-table {
  background-color: transparent !important;
}

::v-deep .el-table th,
::v-deep .el-table tr {
  background-color: transparent !important;
}
</style>
