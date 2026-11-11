<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-s-data head-icon" />
      <div>
        <h2>水质阈值配置</h2>
        <div class="tags">
          <span>品种水温</span>
          <span>DO 四级</span>
          <span>氨氮 / 亚硝酸盐</span>
          <span>ORP / 碱度 / 硬度</span>
          <span>CO₂ / 总磷 / 总氮</span>
          <span>GSM / MIB</span>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">
        <span>品种水温区间（差异化配置）</span>
        <span class="unit">单位: °C · 与鱼类健康状态可筛品种同步</span>
      </div>
      <p class="sync-hint">
        品种列表自动对齐「鱼类健康状态」筛选源（投喂主档鱼种）；此处区间会作用于对应品种的健康环境层水温评分。
        <el-button type="text" size="mini" :loading="syncing" @click="resyncSpecies">重新同步品种</el-button>
      </p>
      <table class="dark-table temp-table">
        <thead>
          <tr>
            <th>鱼种</th>
            <th class="th-warn">警告低值 (°C)</th>
            <th class="th-warn">警告高值 (°C)</th>
            <th class="th-crit">临界低值 (°C)</th>
            <th class="th-crit">临界高值 (°C)</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in local.speciesTemps" :key="row.species" :class="{ 'row-disabled': !row.enabled }">
            <td class="species">{{ row.species }}</td>
            <td><number-stepper v-model="row.warnLow" :step="0.5" @change="emitChange" /></td>
            <td><number-stepper v-model="row.warnHigh" :step="0.5" @change="emitChange" /></td>
            <td><number-stepper v-model="row.criticalLow" :step="0.5" @change="emitChange" /></td>
            <td><number-stepper v-model="row.criticalHigh" :step="0.5" @change="emitChange" /></td>
            <td><enable-btn v-model="row.enabled" @input="emitChange" /></td>
          </tr>
          <tr v-if="!(local.speciesTemps && local.speciesTemps.length)">
            <td colspan="6" class="empty-species">暂无可管理品种，请确认投喂主档已配置鱼种后点击「重新同步品种」</td>
          </tr>
        </tbody>
      </table>

      <div class="inline-setting" :class="{ 'row-disabled': !local.tempDiff.enabled }">
        <div>
          <div class="name">全厂鱼池温差预警</div>
          <div class="hint">{{ local.tempDiff.desc }}</div>
        </div>
        <div class="inline-right">
          <number-stepper tone="accent" v-model="local.tempDiff.value" :step="0.1" :precision="1" @change="emitChange" />
          <span class="suffix">℃</span>
          <enable-btn v-model="local.tempDiff.enabled" @input="emitChange" />
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">溶解氧 DO 四级分级阈值 ({{ local.doLevels.unit }})</div>
      <level-cards :levels="doLevelList" :unit="local.doLevels.unit" @change="onDoChange" />
      <div class="gradient-note">
        <div class="bar" />
        <span>浓度从低到高，风险递减</span>
      </div>
    </div>

    <div class="two-col">
      <grade-threshold-card
        title="氨氮 NH₃-N"
        :unit="local.nh3.unit"
        :safe-value="local.nh3.levels[0].value"
        :warn-value="local.nh3.levels[1].value"
        :enabled="local.nh3.enabled !== false"
        @update:enabled="v => { $set(local.nh3, 'enabled', v); emitChange() }"
        @change="onNh3Change"
      />
      <grade-threshold-card
        title="亚硝酸盐 NO₂⁻-N"
        :unit="local.no2.unit"
        :safe-value="local.no2.levels[0].value"
        :warn-value="local.no2.levels[1].value"
        :enabled="local.no2.enabled !== false"
        @update:enabled="v => { $set(local.no2, 'enabled', v); emitChange() }"
        @change="onNo2Change"
      />
    </div>

    <div class="panel">
      <div class="panel-title">
        <span>其他水质指标</span>
        <span class="unit">警告 / 临界双阈值</span>
      </div>
      <div class="card-grid">
        <metric-card
          v-for="(m, idx) in local.metrics"
          :key="m.id"
          :item="m"
          @change="v => onMetric(idx, v)"
        />
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from '../EnableBtn.vue'
import LevelCards from '../LevelCards.vue'
import MetricCard from '../MetricCard.vue'
import GradeThresholdCard from '../GradeThresholdCard.vue'
import {
  fetchHealthFilterSpecies,
  syncSpeciesTempsWithHealthCatalog
} from '@/utils/speciesTempBridge'

export default {
  name: 'WaterModule',
  components: { EnableBtn, LevelCards, MetricCard, GradeThresholdCard },
  props: { value: { type: Object, required: true } },
  data() {
    return {
      local: JSON.parse(JSON.stringify(this.value)),
      syncing: false,
      _skipValueWatch: false
    }
  },
  watch: {
    value: {
      deep: true,
      handler(v) {
        if (this._skipValueWatch) return
        this.local = JSON.parse(JSON.stringify(v))
      }
    }
  },
  computed: {
    doLevelList() {
      const d = this.local.doLevels
      return [
        { label: '正常', desc: '≥ 此值为安全', value: d.normal, color: 'green' },
        { label: '预警', desc: '预警值 ~ 正常值', value: d.warn, color: 'yellow' },
        { label: '高危', desc: '高危值 ~ 预警值', value: d.highRisk, color: 'orange' },
        { label: '紧急停食', desc: '< 此值立即停食', value: d.emergency, color: 'red' }
      ]
    }
  },
  mounted() {
    this.resyncSpecies({ silent: true })
  },
  methods: {
    emitChange() {
      this._skipValueWatch = true
      this.$emit('input', JSON.parse(JSON.stringify(this.local)))
      this.$nextTick(() => { this._skipValueWatch = false })
    },
    speciesTempsSignature(list) {
      return JSON.stringify((list || []).map((r) => ({
        species: r && r.species,
        warnLow: r && r.warnLow,
        warnHigh: r && r.warnHigh,
        criticalLow: r && r.criticalLow,
        criticalHigh: r && r.criticalHigh,
        enabled: r && r.enabled !== false
      })))
    },
    async resyncSpecies(opts) {
      if (this.syncing) return
      this.syncing = true
      try {
        const catalog = await fetchHealthFilterSpecies()
        if (!this.local.speciesTemps) this.$set(this.local, 'speciesTemps', [])
        const next = syncSpeciesTempsWithHealthCatalog(this.local.speciesTemps, catalog)
        const changed = this.speciesTempsSignature(next) !== this.speciesTempsSignature(this.local.speciesTemps)
        if (changed) {
          this.$set(this.local, 'speciesTemps', next)
          this.emitChange()
          if (!(opts && opts.silent)) {
            this.$message && this.$message.success(`已同步 ${next.length} 个品种（与鱼类健康状态一致）`)
          }
        } else if (!(opts && opts.silent)) {
          this.$message && this.$message.info('品种列表已与健康状态一致，无需更新')
        }
      } catch (e) {
        console.warn('[WaterModule] 同步品种失败', e)
        if (!(opts && opts.silent)) {
          this.$message && this.$message.warning('同步品种失败，请稍后重试')
        }
      } finally {
        this.syncing = false
      }
    },
    onDoChange(list) {
      this.local.doLevels.normal = list[0].value
      this.local.doLevels.warn = list[1].value
      this.local.doLevels.highRisk = list[2].value
      this.local.doLevels.emergency = list[3].value
      this.emitChange()
    },
    onNh3Change({ safe, warn }) {
      this.local.nh3.levels[0].value = safe
      this.local.nh3.levels[1].value = warn
      this.local.nh3.levels[2].value = warn
      this.emitChange()
    },
    onNo2Change({ safe, warn }) {
      this.local.no2.levels[0].value = safe
      this.local.no2.levels[1].value = warn
      this.local.no2.levels[2].value = warn
      this.emitChange()
    },
    onMetric(idx, v) {
      this.$set(this.local.metrics, idx, v)
      this.emitChange()
    }
  }
}
</script>

<style scoped>
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 2px;
}
.tags span {
  font-size: 12px;
  color: #7d8fab;
  padding: 2px 0;
}
.tags span:not(:last-child)::after {
  content: '·';
  margin-left: 8px;
  color: #4a5d78;
}
.temp-table .th-warn { color: #f0b429 !important; }
.temp-table .th-crit { color: #ff7a88 !important; }
.sync-hint {
  margin: 0 0 12px;
  font-size: 12px;
  color: #8fa0b8;
  line-height: 1.5;
}
.sync-hint .el-button {
  padding: 0 4px;
  color: #5eb8ff;
}
.temp-table .species {
  font-weight: 600;
  color: #eef4ff;
  min-width: 84px;
}
.temp-table .empty-species {
  text-align: center;
  color: #8fa0b8;
  padding: 16px 8px;
}
.row-disabled {
  opacity: 0.42;
  filter: grayscale(0.15);
  transition: opacity 0.2s;
}
.row-disabled >>> .enable-btn {
  opacity: 1;
  filter: none;
}
</style>
