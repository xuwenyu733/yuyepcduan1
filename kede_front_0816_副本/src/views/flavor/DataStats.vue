<template>
  <div class="page stats-page">
    <div class="page-head">
      <div>
        <h2>数据统计分析</h2>
      </div>
      <div class="filters">
        <el-select v-model="filters.category" clearable placeholder="全部品类" @change="load">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select v-model="filters.timeRange" clearable placeholder="全部时间" @change="load">
          <el-option label="近7天" value="7d" />
          <el-option label="近30天" value="30d" />
          <el-option label="近90天" value="90d" />
        </el-select>
        <el-select v-model="filters.recordNo" clearable placeholder="全部记录单号" @change="load">
          <el-option v-for="r in recordOptions" :key="r" :label="r" :value="r" />
        </el-select>
      </div>
    </div>

    <!-- KPI -->
    <div class="kpi-grid">
      <div class="kpi-card">
        <div class="kpi-top">
          <span>重量变化</span>
          <i class="el-icon-data-line kpi-icon green" />
        </div>
        <div class="kpi-val" :class="stats.weightChange >= 0 ? 'up' : 'down'">
          {{ stats.weightChange >= 0 ? '+' : '' }}{{ fmt(stats.weightChange) }}
        </div>
        <div class="kpi-foot">入池: {{ fmt(stats.entryWeight) }} kg / 出池: {{ fmt(stats.exitWeight) }} kg</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-top">
          <span>死亡率</span>
          <i class="el-icon-warning-outline kpi-icon orange" />
        </div>
        <div class="kpi-val">{{ fmt(stats.mortalityRate) }}%</div>
        <div class="kpi-foot">死亡: {{ stats.deathCount || 0 }} 尾 / {{ fmt(stats.deathWeight) }} kg</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-top">
          <span>掉秤率</span>
          <i class="el-icon-odometer kpi-icon blue" />
        </div>
        <div class="kpi-val">{{ fmt(stats.weightLossRate) }}%</div>
        <div class="kpi-foot">累计掉秤: {{ fmt(stats.weightLoss) }} kg</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-top">
          <span>销售金额</span>
          <i class="el-icon-coin kpi-icon teal" />
        </div>
        <div class="kpi-val money">¥{{ Math.round(stats.salesAmount || 0) }}</div>
        <div class="kpi-foot">平均单价: ¥{{ fmt(stats.avgPrice) }}/kg</div>
      </div>
    </div>

    <!-- 品类分布 -->
    <div class="panel">
      <div class="panel-title">品类分布</div>
      <div class="panel-sub">各品类优化记录数量占比</div>
      <div v-if="!categoryDist.length" class="empty">暂无数据</div>
      <div v-else class="cat-bars">
        <div v-for="(item, idx) in categoryDist" :key="item.name" class="cat-row">
          <div class="cat-label">
            <span class="dot" :style="{ background: catColor(idx) }" />
            {{ item.name }}
            <em>{{ item.count }} 批 · {{ item.percent }}%</em>
          </div>
          <div class="bar-track">
            <div class="bar-fill" :style="{ width: item.percent + '%', background: catColor(idx) }" />
          </div>
        </div>
      </div>
    </div>

    <!-- 规格变化 -->
    <div class="panel">
      <div class="panel-title">规格变化统计</div>
      <div class="panel-sub">入池与出池规格对比分析</div>
      <div v-if="!hasSpec" class="empty">暂无数据</div>
      <div v-else class="spec-compare">
        <div class="spec-col">
          <div class="spec-label">入池规格</div>
          <div class="spec-val">{{ fmt(stats.specChange.entrySpec, 3) }} <small>kg/尾</small></div>
          <div class="spec-bar in" :style="{ height: specBar(stats.specChange.entrySpec) }" />
        </div>
        <div class="spec-arrow"><i class="el-icon-right" /></div>
        <div class="spec-col">
          <div class="spec-label">出池规格</div>
          <div class="spec-val">{{ fmt(stats.specChange.exitSpec, 3) }} <small>kg/尾</small></div>
          <div class="spec-bar out" :style="{ height: specBar(stats.specChange.exitSpec) }" />
        </div>
        <div class="spec-delta">
          <div class="spec-label">规格变化</div>
          <div class="spec-val" :class="specDelta >= 0 ? 'up' : 'down'">
            {{ specDelta >= 0 ? '+' : '' }}{{ fmt(specDelta, 3) }} kg/尾
          </div>
        </div>
      </div>
    </div>

    <!-- 成本与收益 -->
    <div class="panel">
      <div class="panel-title">成本与收益分析</div>
      <div class="panel-sub">投喂成本与销售收益概览</div>
      <div class="benefit-row">
        <div class="benefit-item">
          <span>总投喂量</span>
          <strong>{{ fmt(stats.feedAmount) }} kg</strong>
        </div>
        <div class="benefit-item">
          <span>销售总金额</span>
          <strong class="green">¥{{ fmt(stats.salesAmount) }}</strong>
        </div>
        <div class="benefit-item">
          <span>平均售价</span>
          <strong class="blue">¥{{ fmt(stats.avgPrice) }}/kg</strong>
        </div>
      </div>
    </div>

    <!-- 成本核算 -->
    <div class="panel">
      <div class="panel-title">成本核算</div>
      <div class="panel-sub">综合成本分析与利润率计算</div>
      <div class="cost-grid">
        <div class="cost-cell c-blue"><span>采购成本</span><b>¥{{ fmt(costs.purchaseCost) }}</b></div>
        <div class="cost-cell c-purple"><span>人工成本</span><b>¥{{ fmt(costs.laborCost) }}</b></div>
        <div class="cost-cell c-yellow"><span>电费</span><b>¥{{ fmt(costs.electricityCost) }}</b></div>
        <div class="cost-cell c-green"><span>饲料成本</span><b>¥{{ fmt(costs.feedCost) }}</b></div>
        <div class="cost-cell c-pink"><span>动保成本</span><b>¥{{ fmt(costs.protectCost) }}</b></div>
      </div>
      <div class="summary-grid">
        <div class="sum-cell s-gray"><span>总成本</span><b>¥{{ fmt(costs.totalCost) }}</b></div>
        <div class="sum-cell s-green"><span>总利润</span><b class="green">¥{{ fmt(costs.profit) }}</b></div>
        <div class="sum-cell s-blue"><span>利润率</span><b class="blue">{{ fmt(costs.profitRate) }}%</b></div>
      </div>
      <div class="formula-box">
        <div class="formula-title">成本计算公式：</div>
        <ul>
          <li>总成本 = 采购成本 + 人工成本 + 电费 + 饲料成本 + 动保成本</li>
          <li>总利润 = 销售总额 - 总成本</li>
          <li>利润率 = (总利润 / 销售总额) × 100%</li>
        </ul>
      </div>
    </div>

    <!-- 损耗分析 -->
    <div class="panel">
      <div class="panel-title">损耗分析详情</div>
      <div class="panel-sub">死亡率与掉秤率综合分析</div>
      <div class="loss-grid">
        <div class="loss-cell l-pink"><span>死亡数量</span><b>{{ stats.deathCount || 0 }} 尾</b></div>
        <div class="loss-cell l-pink"><span>死亡率</span><b class="red">{{ fmt(stats.mortalityRate) }}%</b></div>
        <div class="loss-cell l-peach"><span>掉秤重量</span><b>{{ fmt(stats.weightLoss) }} kg</b></div>
        <div class="loss-cell l-peach"><span>掉秤率</span><b class="orange">{{ fmt(stats.weightLossRate) }}%</b></div>
      </div>
      <div class="loss-notes">
        <div>* 死亡率 = 死亡数量 / 入池数量 × 100%</div>
        <div>* 掉秤率 = 掉秤重量 / 入池重量 × 100%</div>
      </div>
    </div>

    <!-- 阶段性死亡率 -->
    <div class="panel">
      <div class="panel-title">阶段性死亡率分析</div>
      <div class="panel-sub">3天、5天、7天死亡率趋势图</div>
      <div v-if="!batchDetails.length" class="empty">暂无数据</div>
      <div v-else class="trend-chart">
        <div v-for="item in stageBars" :key="item.label" class="trend-col">
          <div class="trend-val">{{ item.value }}%</div>
          <div class="trend-bar" :style="{ height: Math.max(8, item.value * 4) + 'px' }" />
          <div class="trend-label">{{ item.label }}</div>
        </div>
      </div>
    </div>

    <!-- 各批次详细数据 -->
    <div class="panel">
      <div class="panel-title">各批次详细数据</div>
      <div class="panel-sub">每个批次的阶段性死亡量和死亡率明细</div>
      <div v-if="!batchDetails.length" class="empty">暂无数据</div>
      <el-table v-else :data="batchDetails" stripe border size="small">
        <el-table-column prop="recordNo" label="记录单号" min-width="140" />
        <el-table-column prop="category" label="品类" width="100" />
        <el-table-column prop="pondCode" label="鱼池" width="90" />
        <el-table-column prop="deathCount" label="死亡数量" width="90" />
        <el-table-column prop="mortalityRate" label="死亡率%" width="90" />
        <el-table-column prop="day3Rate" label="3天死亡率%" width="110" />
        <el-table-column prop="day5Rate" label="5天死亡率%" width="110" />
        <el-table-column prop="day7Rate" label="7天死亡率%" width="110" />
        <el-table-column prop="weightLossRate" label="掉秤率%" width="90" />
      </el-table>
    </div>

    <!-- 散点图 -->
    <div class="panel">
      <div class="panel-title">批次散点图：掉秤率 vs 死亡率</div>
      <div class="panel-sub">每个点代表一个批次，横坐标为掉秤率，纵坐标为死亡率，点的大小代表入池重量</div>
      <div v-if="!scatterData.length" class="empty">暂无数据</div>
      <div v-else class="scatter-wrap">
        <div class="scatter-y">死亡率 %</div>
        <div class="scatter-area">
          <div
            v-for="p in scatterPoints"
            :key="p.recordNo"
            class="scatter-dot"
            :style="p.style"
            :title="`${p.recordNo}\n掉秤率 ${p.x}% / 死亡率 ${p.y}%\n入池 ${p.size} kg`"
          />
          <div class="axis-x-label">掉秤率 %</div>
        </div>
      </div>
      <div v-if="scatterData.length" class="scatter-legend">
        <span v-for="p in scatterData" :key="'l-' + p.recordNo" class="legend-item">
          {{ p.recordNo }}（{{ p.category }}）
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { loadMergedStats, fetchFlavorMetaSynced } from '@/utils/flavorPurifyBridge'

const CAT_COLORS = ['#54907a', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899']

export default {
  name: 'DataStats',
  data() {
    return {
      filters: { category: '', timeRange: '', recordNo: '' },
      categories: [],
      recordOptions: [],
      stats: {
        weightChange: 0,
        entryWeight: 0,
        exitWeight: 0,
        mortalityRate: 0,
        deathCount: 0,
        deathWeight: 0,
        weightLossRate: 0,
        weightLoss: 0,
        salesAmount: 0,
        avgPrice: 0,
        feedAmount: 0,
        costs: {},
        categoryDist: [],
        specChange: { entrySpec: 0, exitSpec: 0 },
        stageTrend: { day3: 0, day5: 0, day7: 0 },
        batchDetails: [],
        scatterData: []
      }
    }
  },
  computed: {
    costs() {
      return this.stats.costs || {}
    },
    categoryDist() {
      return this.stats.categoryDist || []
    },
    batchDetails() {
      return this.stats.batchDetails || []
    },
    scatterData() {
      return this.stats.scatterData || []
    },
    hasSpec() {
      const s = this.stats.specChange || {}
      return (s.entrySpec || 0) > 0 || (s.exitSpec || 0) > 0
    },
    specDelta() {
      const s = this.stats.specChange || {}
      return Number(((s.exitSpec || 0) - (s.entrySpec || 0)).toFixed(3))
    },
    stageBars() {
      const t = this.stats.stageTrend || {}
      return [
        { label: '3天', value: t.day3 || 0 },
        { label: '5天', value: t.day5 || 0 },
        { label: '7天', value: t.day7 || 0 }
      ]
    },
    scatterPoints() {
      const data = this.scatterData
      if (!data.length) return []
      const maxX = Math.max(...data.map(d => d.x), 1)
      const maxY = Math.max(...data.map(d => d.y), 1)
      const maxS = Math.max(...data.map(d => d.size), 1)
      return data.map((d, i) => {
        const left = Math.min(92, Math.max(4, (d.x / maxX) * 88 + 4))
        const bottom = Math.min(88, Math.max(6, (d.y / maxY) * 80 + 6))
        const size = 10 + (d.size / maxS) * 28
        return {
          ...d,
          style: {
            left: left + '%',
            bottom: bottom + '%',
            width: size + 'px',
            height: size + 'px',
            background: CAT_COLORS[i % CAT_COLORS.length]
          }
        }
      })
    }
  },
  created() {
    this.init()
  },
  activated() {
    this.load()
  },
  methods: {
    fmt(n, digits = 2) {
      return Number(n || 0).toFixed(digits)
    },
    catColor(i) {
      return CAT_COLORS[i % CAT_COLORS.length]
    },
    specBar(v) {
      const max = Math.max(this.stats.specChange.entrySpec || 0, this.stats.specChange.exitSpec || 0, 0.001)
      return Math.max(24, (v / max) * 100) + 'px'
    },
    async init() {
      try {
        const meta = await fetchFlavorMetaSynced()
        this.categories = (meta.data && meta.data.categories) || []
      } catch (e) {
        this.categories = []
      }
      await this.load()
    },
    async load() {
      const params = {
        category: this.filters.category || undefined,
        recordNo: this.filters.recordNo || undefined,
        range: this.filters.timeRange || undefined
      }
      const data = await loadMergedStats(params)
      this.stats = {
        ...this.stats,
        ...data,
        costs: data.costs || {},
        specChange: data.specChange || { entrySpec: 0, exitSpec: 0 },
        stageTrend: data.stageTrend || { day3: 0, day5: 0, day7: 0 },
        categoryDist: data.categoryDist || [],
        batchDetails: data.batchDetails || [],
        scatterData: data.scatterData || []
      }
      this.recordOptions = data.recordOptions || []
    }
  }
}
</script>

<style scoped>
.stats-page { max-width: 1200px; }
.page-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}
.page-head h2 { margin: 0 0 4px; font-size: 22px; color: #e8edf2; }
.page-head p { margin: 0; color: #b7c4d2; font-size: 13px; }
.filters { display: flex; gap: 10px; flex-wrap: wrap; }
.filters .el-select { width: 160px; }

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 16px;
}
.kpi-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 8px;
  padding: 16px 18px;
}
.kpi-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #b7c4d2;
  font-size: 13px;
}
.kpi-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}
.kpi-icon.green { background: rgba(82, 196, 26, 0.15); color: #52c41a; }
.kpi-icon.orange { background: rgba(234, 88, 12, 0.15); color: #faad14; }
.kpi-icon.blue { background: rgba(84, 144, 122, 0.15); color: #54907a; }
.kpi-icon.teal { background: rgba(5, 150, 105, 0.15); color: #34d399; }
.kpi-val {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  color: #e8edf2;
  line-height: 1.2;
}
.kpi-val.up { color: #52c41a; }
.kpi-val.down { color: #dc2626; }
.kpi-val.money { color: #059669; }
.kpi-foot { margin-top: 8px; font-size: 12px; color: #b7c4d2; }

.panel {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 8px;
  padding: 18px 20px;
  margin-bottom: 16px;
}
.panel-title { font-size: 16px; font-weight: 700; color: #e8edf2; }
.panel-sub { margin-top: 4px; font-size: 13px; color: #b7c4d2; margin-bottom: 16px; }
.empty {
  text-align: center;
  color: #b7c4d2;
  padding: 48px 0;
  font-size: 14px;
}

.cat-bars { display: flex; flex-direction: column; gap: 14px; }
.cat-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #c5d0dc;
  margin-bottom: 6px;
}
.cat-label em { margin-left: auto; font-style: normal; color: #b7c4d2; font-size: 12px; }
.dot { width: 8px; height: 8px; border-radius: 50%; }
.bar-track { height: 10px; background: #132030; border-radius: 999px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 999px; transition: width .3s; }

.spec-compare {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 28px;
  padding: 20px 0 8px;
  flex-wrap: wrap;
}
.spec-col { text-align: center; min-width: 100px; }
.spec-label { font-size: 13px; color: #b7c4d2; margin-bottom: 8px; }
.spec-val { font-size: 22px; font-weight: 700; color: #e8edf2; margin-bottom: 12px; }
.spec-val small { font-size: 12px; font-weight: 500; color: #b7c4d2; }
.spec-val.up { color: #52c41a; }
.spec-val.down { color: #dc2626; }
.spec-bar {
  width: 56px;
  margin: 0 auto;
  border-radius: 8px 8px 4px 4px;
}
.spec-bar.in { background: linear-gradient(180deg, #8fd4b8, #54907a); }
.spec-bar.out { background: linear-gradient(180deg, #6ee7b7, #10b981); }
.spec-arrow { color: #b7c4d2; font-size: 22px; padding-bottom: 40px; }
.spec-delta { text-align: center; padding-bottom: 40px; }

.benefit-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}
.benefit-item {
  background: #132030;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 12px;
  padding: 18px 16px;
  text-align: center;
}
.benefit-item span { display: block; font-size: 13px; color: #b7c4d2; margin-bottom: 8px; }
.benefit-item strong { font-size: 22px; color: #e8edf2; }
.benefit-item strong.green { color: #52c41a; }
.benefit-item strong.blue { color: #54907a; }

.cost-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin-bottom: 12px;
}
.cost-cell {
  border-radius: 12px;
  padding: 14px 10px;
  text-align: center;
}
.cost-cell span { display: block; font-size: 12px; color: #b7c4d2; margin-bottom: 6px; }
.cost-cell b { font-size: 16px; color: #e8edf2; }
.c-blue { background: rgba(84, 144, 122, 0.12); }
.c-purple { background: rgba(139, 92, 246, 0.12); }
.c-yellow { background: rgba(250, 173, 20, 0.12); }
.c-green { background: rgba(16, 185, 129, 0.12); }
.c-pink { background: rgba(236, 72, 153, 0.12); }

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 14px;
}
.sum-cell {
  border-radius: 12px;
  padding: 18px 14px;
  text-align: center;
}
.sum-cell span { display: block; font-size: 13px; color: #b7c4d2; margin-bottom: 8px; }
.sum-cell b { font-size: 26px; color: #e8edf2; }
.sum-cell b.green { color: #52c41a; }
.sum-cell b.blue { color: #54907a; }
.s-gray { background: #132030; }
.s-green { background: rgba(82, 196, 26, 0.12); }
.s-blue { background: rgba(84, 144, 122, 0.12); }

.formula-box {
  background: #132030;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 10px;
  padding: 14px 16px;
  font-size: 13px;
  color: #a8b8c8;
}
.formula-title { font-weight: 600; margin-bottom: 8px; color: #c5d0dc; }
.formula-box ul { margin: 0; padding-left: 18px; line-height: 1.8; }

.loss-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 12px;
}
.loss-cell {
  border-radius: 12px;
  padding: 16px 12px;
  text-align: center;
}
.loss-cell span { display: block; font-size: 12px; color: #b7c4d2; margin-bottom: 8px; }
.loss-cell b { font-size: 20px; color: #e8edf2; }
.loss-cell b.red { color: #dc2626; }
.loss-cell b.orange { color: #ea580c; }
.l-pink { background: rgba(220, 38, 38, 0.12); }
.l-peach { background: rgba(234, 88, 12, 0.12); }
.loss-notes { font-size: 12px; color: #b7c4d2; line-height: 1.7; }

.trend-chart {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 48px;
  min-height: 180px;
  padding: 20px 0 8px;
}
.trend-col { text-align: center; }
.trend-val { font-size: 13px; font-weight: 600; color: #c5d0dc; margin-bottom: 8px; }
.trend-bar {
  width: 48px;
  margin: 0 auto;
  border-radius: 8px 8px 4px 4px;
  background: linear-gradient(180deg, #fb923c, #ef4444);
}
.trend-label { margin-top: 10px; font-size: 13px; color: #b7c4d2; }

.scatter-wrap {
  display: flex;
  gap: 8px;
  align-items: stretch;
  min-height: 280px;
}
.scatter-y {
  writing-mode: vertical-rl;
  transform: rotate(180deg);
  font-size: 12px;
  color: #b7c4d2;
  text-align: center;
}
.scatter-area {
  position: relative;
  flex: 1;
  background: #132030;
  border: 1px solid rgba(124, 143, 161, 0.25);
  border-radius: 12px;
  min-height: 260px;
}
.scatter-dot {
  position: absolute;
  border-radius: 50%;
  opacity: 0.85;
  transform: translate(-50%, 50%);
  box-shadow: 0 0 0 2px rgba(84, 144, 122, 0.35);
  cursor: default;
}
.axis-x-label {
  position: absolute;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: #b7c4d2;
}
.scatter-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  margin-top: 12px;
  font-size: 12px;
  color: #b7c4d2;
}

@media (max-width: 1100px) {
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
  .cost-grid { grid-template-columns: repeat(3, 1fr); }
  .loss-grid { grid-template-columns: repeat(2, 1fr); }
  .benefit-row { grid-template-columns: 1fr; }
}
</style>
