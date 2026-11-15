<template>
  <div class="stock-model4" v-loading="loading">
    <div class="back-button-container">
      <button type="button" class="back-button" @click="goBack">返回</button>
      <button type="button" class="refresh-button" @click="loadData">刷新</button>
    </div>

    <div class="page-hero">
      <h1>存量分布</h1>
    </div>


    <div v-if="!loading && !fishTreeList.length" class="empty-box">
      <el-empty description="暂无批次存量数据" />
    </div>

    <div
      class="tree-container"
      v-for="(item, index) in fishTreeList"
      :key="'batch-' + index + '-' + (item.root && item.root.batchNumber)"
      :ref="'treeBox' + index"
    >
      <!-- 批次主节点 -->
      <div
        class="main-node"
        :id="'sd-root-' + index"
        @click="openDetail(item.root, true)"
      >
        <div class="node-title">{{ (item.root && item.root.batchNumber) || '-' }}#批次鱼苗</div>
        <div class="node-data2" v-if="item.root && item.root.batchNumberInfo">
          <div class="data-item1">
            <span class="data-value1">{{ item.root.batchNumberInfo }}</span>
          </div>
        </div>
        <div class="node-data1">
          <div class="data-item" v-for="m in rootMetrics(item.root)" :key="m.label">
            <span class="data-label">{{ m.label }}</span>
            <span class="data-value">
              <svg v-if="m.ring" class="progress-ring" viewBox="0 0 24 24">
                <circle
                  class="progress-ring-circle"
                  :stroke-dasharray="m.ring + ', 100'"
                  stroke="#4CAF50"
                  r="8"
                  cx="12"
                  cy="12"
                />
              </svg>
              {{ m.value }}
              <span
                v-if="m.change"
                class="data-change"
                :class="m.trend === 'up' ? 'increase' : 'decrease'"
              >{{ m.change }}</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 分池层级（跳过 treeMap 第一层，与原版 model4 一致） -->
      <div
        v-for="(level, li) in childLevels(item)"
        :key="'lvl-' + index + '-' + level.key"
        class="sub-nodes"
      >
        <div
          v-for="(di, ii) in level.list"
          :id="'sd-node-' + index + '-' + level.key + '-' + ii"
          :key="'pond-' + index + '-' + level.key + '-' + ii"
          class="sub-node"
          :class="'node-' + ((ii % 3) + 1)"
          @click.stop="openDetail(di, false)"
        >
          <div class="node-title">{{ pondTitle(di) }}</div>
          <div class="node-data">
            <div class="data-item" v-for="m in pondMetrics(di)" :key="m.label">
              <span class="data-label">{{ m.label }}</span>
              <span class="data-value">
                <svg v-if="m.ring" class="progress-ring" viewBox="0 0 24 24">
                  <circle
                    class="progress-ring-circle"
                    :stroke-dasharray="m.ring + ', 100'"
                    stroke="#4CAF50"
                    r="8"
                    cx="12"
                    cy="12"
                  />
                </svg>
                {{ m.value }}
                <span
                  v-if="m.change"
                  class="data-change"
                  :class="m.trend === 'up' ? 'increase' : 'decrease'"
                >{{ m.change }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 节点详情（对齐原悬浮窗：转入 / 养殖统计 / 节点） -->
    <el-dialog
      :title="detailTitle"
      :visible.sync="detailOpen"
      width="720px"
      append-to-body
      custom-class="stock-model4-dialog"
    >
      <div v-if="detailNode">
        <div v-for="(sec, si) in detailSections" :key="'sec-' + si" class="detail-section">
          <h4>{{ sec.title }}</h4>
          <div class="detail-grid">
            <div v-for="row in sec.rows" :key="row.label" class="detail-row" :class="{ highlight: row.highlight }">
              <span>{{ row.label }}</span>
              <b>{{ row.value }}</b>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
        <el-button type="primary" plain @click="goPage('/keyshow/zkDeathSurvivalRate?activeTab=mortality')">死亡率</el-button>
        <el-button type="primary" plain @click="goPage('/water_quality')">水质情况</el-button>
          </div>
    </el-dialog>
  </div>
</template>

<script>
import { getFishBatch } from '@/api/fish/daPing'
import { createTreeConnector, clearTreeConnectors } from '@/utils/treeLineConnector'

export default {
  name: 'StockDistribution',
  data() {
    return {
      loading: false,
      fishTreeList: [],
      detailOpen: false,
      detailTitle: '',
      detailNode: null,
      detailIsRoot: false,
      _lineCleanups: [],
      _resizeTimer: null
    }
  },
  computed: {
    detailSections() {
      const node = this.detailNode
      if (!node) return []
      const detail = (node.fishNodeDetailList && node.fishNodeDetailList[0]) || {}
      const sections = []

      const transfers = detail.transferPoList || []
      if (transfers.length) {
        transfers.forEach((obj, i) => {
          const str = obj.splitPondDetailNum ? `${obj.splitPondDetailNum}分池转入` : '分池转入'
          sections.push({
            title: transfers.length > 1 ? `转入信息 #${i + 1}` : '转入信息',
            rows: [
              { label: '转入时间', value: obj.transferTime || '-' },
              { label: '转入数量', value: this.numFormat(obj.transferFishNum, '尾') },
              { label: '转入重量', value: this.numFormat(obj.transferFishWeight, 'Kg') },
              { label: '转入规格', value: this.numFormat(obj.transferFishSpec, 'g/尾') },
              { label: '转入饵料系数', value: obj.transferBaitCoefficient > 0 ? obj.transferBaitCoefficient : '1.1' },
              { label: `转入${obj.addType == '-3' ? '三日' : '七日'}死亡量`, value: obj.transferSevenDayMortalityRate != null ? obj.transferSevenDayMortalityRate : '-' },
              { label: `转入${obj.addType == '-3' ? '三日' : '七日'}死亡率`, value: this.percentFormat(obj.transferSevenDayMortalityRatePer) },
              { label: '转入类型', value: obj.addType == '-3' ? str : '投放' }
            ]
          })
        })
      } else if (detail.transferTime) {
        sections.push({
          title: '转入信息',
          rows: [
            { label: '转入时间', value: detail.transferTime || '-' },
            { label: '转入数量', value: this.numFormat(detail.transferFishNum, '尾') },
            { label: '转入规格', value: this.numFormat(detail.transferFishSpec, 'g/尾') }
          ]
        })
      }

      sections.push({
        title: '养殖统计',
        rows: [
          { label: '打样规格', value: this.numFormat(detail.avgSpecifications || node.fishSpec, 'g/尾') },
          { label: '累计投喂量', value: this.numFormat(detail.accumulatedFeedingAmount != null ? detail.accumulatedFeedingAmount : node.accumulatedFeedingAmount, 'Kg') },
          { label: '累计死亡量', value: this.numFormat(detail.accumulativeDeathToll != null ? detail.accumulativeDeathToll : node.accumulativeDeathToll, '尾') },
          { label: '饵料系数', value: (detail.feedCoefficient > 0 ? detail.feedCoefficient : (node.avgBaitCoefficient > 0 ? node.avgBaitCoefficient : '1.1')) },
          { label: '投入成本', value: this.numFormat(detail.inputCost, '元'), highlight: true },
          { label: '生物资产', value: this.numFormat(detail.sumBiologicalAssets != null ? detail.sumBiologicalAssets : node.sumBiologicalAssets, 'Kg'), highlight: true }
        ]
      })

      sections.push({
        title: '节点信息',
        rows: [
          { label: '实时存池', value: this.numFormat(node.realTimeStoragePool, '尾') },
          { label: '累计增重', value: this.numFormat(node.accumulatedWeightGain, 'Kg') },
          { label: '死亡率', value: this.percentFormat(node.mortalityRate) },
          { label: '成活率', value: this.percentFormat(node.survivalRate) },
          { label: '规格', value: node.fishSpec != null ? `${node.fishSpec}g/尾` : '-' },
          { label: '密度', value: node.fishDensity > 0 ? `${node.fishDensity}kg/m³` : '-' },
          { label: '今日投喂量', value: this.numFormat(node.feedCount, 'Kg'), highlight: true },
          { label: '今日死亡量', value: this.numFormat(node.deadCount, '尾'), highlight: true }
        ]
      })
      return sections
    }
  },
  created() {
    this.loadData()
  },
  mounted() {
    this._onResize = () => {
      clearTimeout(this._resizeTimer)
      this._resizeTimer = setTimeout(() => this.redrawTreeLines(), 120)
    }
    window.addEventListener('resize', this._onResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this._onResize)
    clearTimeout(this._resizeTimer)
    this.clearTreeLines()
  },
  methods: {
    goBack() {
      if (window.history.length > 1) this.$router.back()
      else this.$router.push('/index_daping3')
    },
    goPage(path) {
      this.detailOpen = false
      this.$router.push(path).catch(() => {})
    },
    clearTreeLines() {
      ;(this._lineCleanups || []).forEach((fn) => {
        try { if (typeof fn === 'function') fn() } catch (e) { /* ignore */ }
      })
      this._lineCleanups = []
      ;(this.fishTreeList || []).forEach((_, index) => {
        const box = this.resolveTreeBox(index)
        if (box) clearTreeConnectors(box)
      })
    },
    resolveTreeBox(index) {
      const ref = this.$refs['treeBox' + index]
      if (Array.isArray(ref)) return ref[0]
      return ref || null
    },
    /** 对齐原版 model4：elbow 直角折线 #33B3ED */
    redrawTreeLines() {
      this.clearTreeLines()
      this.$nextTick(() => {
        ;(this.fishTreeList || []).forEach((item, index) => {
          const box = this.resolveTreeBox(index)
          if (!box) return
          clearTreeConnectors(box)
          const levels = this.childLevels(item)
          levels.forEach((level, li) => {
            ;(level.list || []).forEach((di, ii) => {
              const endId = `sd-node-${index}-${level.key}-${ii}`
              const parents = []
              if (li === 0) {
                parents.push(`sd-root-${index}`)
              } else if (Array.isArray(di.parentNodeList) && di.parentNodeList.length) {
                const prevKey = levels[li - 1].key
                di.parentNodeList.forEach((p) => {
                  const pIdx = p && p.parentNodeIndex != null ? p.parentNodeIndex : 0
                  parents.push(`sd-node-${index}-${prevKey}-${pIdx}`)
                })
              } else {
                const prevKey = levels[li - 1].key
                const pIdx = di.parentNodeIndex != null ? di.parentNodeIndex : 0
                parents.push(`sd-node-${index}-${prevKey}-${pIdx}`)
              }
              parents.forEach((startId) => {
                const remove = createTreeConnector(startId, endId, {
                  color: '#33B3ED',
                  width: 1,
                  type: 'elbow',
                  offset: -3,
                  container: box
                })
                if (remove) this._lineCleanups.push(remove)
              })
            })
          })
        })
      })
    },
    percentFormat(item) {
      if (item == null || item === '') return '0%'
      const n = Number(item)
      if (Number.isNaN(n)) return '0%'
      return `${(n * 100).toFixed(2)}%`
    },
    numFormat(item, unit) {
      if (item == null || item === '') return `0${unit || ''}`
      const n = Number(item)
      if (Number.isNaN(n)) return `0${unit || ''}`
      const text = Number.isInteger(n) ? String(n) : n.toFixed(2)
      return `${text}${unit || ''}`
    },
    trendChange(val, trend, unit) {
      if (!val) return null
      const arrow = trend === 'up' ? ' ↑' : ' ↓'
      return `${this.numFormat(val, unit)}${arrow}`
    },
    survivalRing(rate) {
      const n = Number(rate)
      if (!(n > 0)) return 0
      return Math.max(0, Math.min(100, Math.round(n * 100)))
    },
    rootMetrics(root) {
      if (!root) return []
      const gain = root.accumulatedWeightGain > 0
        ? root.accumulatedWeightGain
        : (Number(root.accumulatedFeedingAmount) || 0) / (root.avgBaitCoefficient > 0 ? root.avgBaitCoefficient : 1.1)
      return [
        { label: '实时存池', value: this.numFormat(root.realTimeStoragePool, '尾') },
        { label: '投放七日死亡量', value: this.numFormat(root.sevenDayMortalityRate, '尾') },
        { label: '投放七日死亡率', value: this.percentFormat(root.sevenDayMortalityRatePer) },
        { label: '累计死亡量', value: this.numFormat(root.accumulativeDeathToll, '尾') },
        { label: '累计死亡率', value: this.percentFormat(root.mortalityRate) },
        { label: '成活率', value: this.percentFormat(root.survivalRate), ring: this.survivalRing(root.survivalRate) },
        { label: '累计投喂量', value: this.numFormat(root.accumulatedFeedingAmount, 'Kg') },
        { label: '累计增重', value: this.numFormat(gain, 'Kg') },
        { label: '销售数量', value: this.numFormat(root.saleFishNum, '尾') },
        { label: '销售重量', value: this.numFormat(root.saleWeight, 'Kg') },
        { label: '存塘生物资产', value: this.numFormat(root.biologicalAssets, 'Kg') },
        { label: '总生物资产', value: this.numFormat(root.sumBiologicalAssets, 'Kg') },
        { label: '平均饵料系数', value: root.avgBaitCoefficient > 0 ? root.avgBaitCoefficient : '1.1' },
        {
          label: '今日投喂量',
          value: this.numFormat(root.feedCount, 'Kg'),
          change: this.trendChange(root.subtractFeedCount, root.feedCountTrend, 'Kg'),
          trend: root.feedCountTrend
        },
        {
          label: '今日死亡量',
          value: root.deadCount ? `${root.deadCount}尾` : '0尾',
          change: this.trendChange(root.subtractDeadCount, root.deadCountTrend, '尾'),
          trend: root.deadCountTrend
        }
      ]
    },
    pondMetrics(di) {
      if (!di) return []
      return [
        { label: '实时存池', value: `${di.realTimeStoragePool != null ? di.realTimeStoragePool : 0}尾` },
        { label: '死亡率', value: this.percentFormat(di.mortalityRate) },
        {
          label: '成活率',
          value: di.survivalRate != null ? `${(Number(di.survivalRate) * 100).toFixed(2)}%` : '',
          ring: this.survivalRing(di.survivalRate)
        },
        { label: '规格', value: di.fishSpec != null ? `${di.fishSpec}g/尾` : '-' },
        { label: '密度', value: di.fishDensity ? `${di.fishDensity}kg/m³` : '' },
        {
          label: '今日投喂量',
          value: di.feedCount ? `${di.feedCount}kg` : '0Kg',
          change: di.subtractFeedCount
            ? `${di.subtractFeedCount}斤${di.feedCountTrend === 'up' ? ' ↑' : ' ↓'}`
            : null,
          trend: di.feedCountTrend
        },
        {
          label: '今日死亡量',
          value: di.deadCount ? `${di.deadCount}尾` : '0尾',
          change: di.subtractDeadCount
            ? `${di.subtractDeadCount}尾${di.deadCountTrend === 'up' ? ' ↑' : ' ↓'}`
            : null,
          trend: di.deadCountTrend
        }
      ]
    },
    pondTitle(di) {
      const parts = [di.workshopName, di.pondSystemName, di.pondName].filter(Boolean)
      return parts.length ? parts.join('#') : (di.batchNumber ? `${di.batchNumber}#节点` : '分池节点')
    },
    childLevels(item) {
      const map = (item && item.treeMap) || {}
      const keys = Object.keys(map).sort((a, b) => Number(a) - Number(b))
      // 与原版一致：跳过第一层（常为批次镜像节点）
      return keys.slice(1).map((key) => ({
        key,
        list: Array.isArray(map[key]) ? map[key] : []
      })).filter((lvl) => lvl.list.length)
    },
    openDetail(node, isRoot) {
      if (!node) return
      this.detailNode = node
      this.detailIsRoot = !!isRoot
      this.detailTitle = isRoot
        ? `${node.batchNumber || ''}#批次养殖数据`
        : `${this.pondTitle(node)} 养殖数据`
      this.detailOpen = true
    },
    async loadData() {
      this.loading = true
      try {
        const user = (this.$store && this.$store.getters && this.$store.getters.user) || {}
        const deptId = (this.$route.query && (this.$route.query.deptId || this.$route.query.workshopId)) ||
          user.deptId ||
          '1'
        const res = await getFishBatch(deptId ? { deptId: String(deptId) } : {})
        // request 拦截器成功时返回整个 CommonResult，或已解包 data
        let list = []
        if (Array.isArray(res)) {
          list = res
        } else if (res && Array.isArray(res.data)) {
          list = res.data
        } else if (res && res.code === 403 && /暂无数据/.test(String(res.msg || ''))) {
          // 兼容旧后端：部门权限过滤后曾返回 403「暂无数据」
          list = []
        } else if (res && res.code != null && Number(res.code) !== 0) {
          throw new Error(res.msg || '存量分布加载失败')
        }
        this.fishTreeList = list
        if (!this.fishTreeList.length) {
          this.$message && this.$message.info('暂无批次数据')
        } else {
          this.$nextTick(() => this.redrawTreeLines())
        }
      } catch (e) {
        console.warn('[stockDistribution] getFishBatch failed', e)
        this.fishTreeList = []
        this.clearTreeLines()
        const msg = (e && e.message) || (typeof e === 'string' ? e : '') || '存量分布加载失败'
        // 业务「暂无数据」不当成崩溃失败
        if (/暂无数据/.test(msg)) {
          this.$message && this.$message.info('暂无批次数据')
        } else {
          this.$message && this.$message.error('存量分布加载失败，请确认账号有权限或联系管理员')
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.stock-model4 {
  min-height: calc(100vh - 84px);
  padding: 24px 20px 48px;
  position: relative;
  overflow-x: hidden;
  background: linear-gradient(135deg, #E6F7FF 0%, #B3E0FF 120%);
}
.stock-model4::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="rgba(255,255,255,0.35)" d="M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,250.7C672,235,768,181,864,181.3C960,181,1056,235,1152,234.7C1248,235,1344,181,1392,154.7L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>');
  background-size: cover;
  background-position: bottom;
  opacity: 0.45;
  pointer-events: none;
  z-index: 0;
}
.back-button-container {
  position: sticky;
  top: 12px;
  z-index: 20;
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}
.back-button,
.refresh-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #00A0E9 0%, #00C1D4 100%);
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.25);
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 160, 233, 0.28);
}
.refresh-button {
  background: linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%);
}
.back-button::before {
  content: '←';
  font-weight: 700;
}
.page-hero {
  position: relative;
  z-index: 1;
  text-align: center;
  margin: 8px 0 28px;
}
.page-hero h1 {
  margin: 0;
  font-size: 28px;
  color: #0A2A55;
  letter-spacing: 0.04em;
}
.page-hero p {
  margin: 8px 0 0;
  color: #3b6b8c;
  font-size: 14px;
}
.empty-box {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 16px;
  padding: 40px 20px;
  max-width: 520px;
  margin: 0 auto;
}
.tree-container {
  width: min(1100px, 96%);
  margin: 0 auto 48px;
  position: relative;
  z-index: 1;
}
.main-node {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  width: 100%;
  margin: 0 auto 40px;
  box-shadow: 0 10px 30px rgba(0, 160, 233, 0.2);
  border: 2px solid #00A0E9;
  cursor: pointer;
  transition: box-shadow 0.25s ease;
  position: relative;
  z-index: 2;
}
.main-node:hover {
  box-shadow: 0 15px 35px rgba(0, 160, 233, 0.3);
}
.node-title {
  font-size: 18px;
  font-weight: 700;
  color: #0A2A55;
  text-align: center;
  border-bottom: 1px dashed #00A0E9;
  padding-bottom: 10px;
  margin-bottom: 8px;
}
.node-data2 {
  width: 100%;
  font-size: 12px;
}
.data-item1 {
  padding: 6px 0 10px;
  text-align: center;
}
.data-value1 {
  color: #3b6b8c;
  font-size: 13px;
}
.node-data1 {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px 14px;
}
.node-data {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}
.data-item {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.data-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 3px;
}
.data-value {
  font-size: 14px;
  font-weight: 700;
  color: #0A2A55;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 2px;
}
.data-change {
  font-size: 11px;
  margin-left: 4px;
}
.increase { color: #FF5A5F; }
.decrease { color: #4CAF50; }
.sub-nodes {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  gap: 16px;
  position: relative;
  padding-top: 8px;
  margin-bottom: 32px;
  z-index: 2;
}
.sub-node {
  background: #fff;
  border-radius: 15px;
  padding: 15px;
  box-shadow: 0 8px 25px rgba(0, 160, 233, 0.15);
  border: 2px solid #00C1D4;
  width: min(320px, 100%);
  flex: 1 1 280px;
  max-width: 360px;
  position: relative;
  z-index: 2;
  cursor: pointer;
}
.sub-node:hover {
  box-shadow: 0 12px 30px rgba(0, 160, 233, 0.25);
}
.tree-connector-container {
  z-index: 0 !important;
}
.progress-ring {
  width: 16px;
  height: 16px;
  margin-right: 4px;
  flex-shrink: 0;
}
.progress-ring-circle {
  stroke-width: 8;
  stroke-linecap: round;
  fill: none;
  transform: rotate(-90deg);
  transform-origin: 50% 50%;
}
.detail-section {
  margin-bottom: 16px;
}
.detail-section h4 {
  margin: 0 0 8px;
  color: #E8F7FF;
  font-size: 15px;
  font-weight: 600;
  border-left: 3px solid #38BDF8;
  padding-left: 8px;
  letter-spacing: 0.02em;
}
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 12px;
}
.detail-row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 10px;
  background: rgba(14, 116, 144, 0.22);
  border: 1px solid rgba(56, 189, 248, 0.28);
  border-radius: 8px;
  font-size: 13px;
}
.detail-row span { color: #A5D8FF; }
.detail-row b { color: #F0F9FF; font-weight: 600; }
.detail-row.highlight {
  background: rgba(14, 165, 233, 0.28);
  border: 1px solid rgba(125, 211, 252, 0.55);
}
@media (max-width: 1100px) {
  .node-data1 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
@media (max-width: 640px) {
  .node-data1,
  .node-data,
  .detail-grid {
    grid-template-columns: 1fr 1fr;
  }
}
</style>

<style>
.stock-model4-dialog .el-dialog {
  border-radius: 16px;
  overflow: hidden;
}
.stock-model4-dialog .el-dialog__title {
  color: #F0F9FF !important;
  font-weight: 700;
}
.stock-model4-dialog .el-dialog__body {
  padding-top: 10px;
}
.stock-model4-dialog .detail-section h4 {
  color: #E8F7FF !important;
  border-left-color: #38BDF8 !important;
}
</style>
