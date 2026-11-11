<template>
  <div class="threshold-native">
    <app-layout
      :nav="nav"
      :active="active"
      :meta="meta"
      :saving="saving"
      @change="switchModule"
      @save="saveCurrent"
      @save-all="saveEverything"
      @reset="handleReset"
    >
      <div v-loading="loading" class="page-body">
        <water-module v-if="active === 'water' && config.water" v-model="config.water" @input="markDirty" />
        <device-module v-else-if="active === 'device' && config.device" v-model="config.device" @input="markDirty" />
        <mortality-module v-else-if="active === 'mortality' && config.mortality" v-model="config.mortality" @input="markDirty" />
        <health-module v-else-if="active === 'health' && config.health" v-model="config.health" @input="markDirty" />
        <feeding-module v-else-if="active === 'feeding' && config.feeding" v-model="config.feeding" @input="markDirty" />
        <alarm-module v-else-if="active === 'alarm' && config.alarm" v-model="config.alarm" @input="markDirty" />
        <energy-module v-else-if="active === 'energy' && config.energy" v-model="config.energy" @input="markDirty" />
        <experiment-module v-else-if="active === 'experiment' && config.experiment" v-model="config.experiment" @input="markDirty" />
        <security-module v-else-if="active === 'security' && config.security" v-model="config.security" @input="markDirty" />
        <holding-module v-else-if="active === 'holding' && config.holding" v-model="config.holding" @input="markDirty" />
        <timeout-module v-else-if="active === 'timeout' && config.timeout" v-model="config.timeout" @input="markDirty" />
        <association-module v-else-if="active === 'association' && config.association" v-model="config.association" @input="markDirty" />
      </div>
    </app-layout>
  </div>
</template>

<script>
import AppLayout from './AppLayout.vue'
import WaterModule from './components/modules/WaterModule.vue'
import DeviceModule from './components/modules/DeviceModule.vue'
import MortalityModule from './components/modules/MortalityModule.vue'
import HealthModule from './components/modules/HealthModule.vue'
import FeedingModule from './components/modules/FeedingModule.vue'
import AlarmModule from './components/modules/AlarmModule.vue'
import EnergyModule from './components/modules/EnergyModule.vue'
import ExperimentModule from './components/modules/ExperimentModule.vue'
import SecurityModule from './components/modules/SecurityModule.vue'
import HoldingModule from './components/modules/HoldingModule.vue'
import TimeoutModule from './components/modules/TimeoutModule.vue'
import AssociationModule from './components/modules/AssociationModule.vue'
import NumberStepper from './components/NumberStepper.vue'
import { fetchNav, fetchAll, saveModule, saveAll, resetAll, fetchAiAlarms } from '@/api/threshold'
import { syncModuleAiAlarmsToMission } from '@/utils/alarmTaskBridge'
import { invalidateThresholdCache } from '@/utils/thresholdConfig'
import {
  fetchHealthFilterSpecies,
  syncSpeciesTempsWithHealthCatalog
} from '@/utils/speciesTempBridge'
import { clearWaterThresholdCache } from '@/views/index_daping3_waterThreshold'
import { clearHealthPreviewCaches } from '@/utils/healthPreviewBridge'
import Vue from 'vue'
import './threshold-theme.css'

if (!Vue.options.components.NumberStepper) {
  Vue.component('NumberStepper', NumberStepper)
}

export default {
  name: 'ThresholdSettings',
  components: {
    AppLayout,
    WaterModule,
    DeviceModule,
    MortalityModule,
    HealthModule,
    FeedingModule,
    AlarmModule,
    EnergyModule,
    ExperimentModule,
    SecurityModule,
    HoldingModule,
    TimeoutModule,
    AssociationModule
  },
  data() {
    return {
      loading: false,
      saving: false,
      dirty: false,
      active: 'water',
      nav: [],
      meta: { version: 'v3.3.0', lastSaved: '-', moduleCount: 12 },
      config: {}
    }
  },
  created() {
    if (this.$route && this.$route.meta) {
      this.$set(this.$route.meta, 'appMainOverflow', 'hidden')
    }
    this.bootstrap()
  },
  mounted() {
    this._onBeforeUnload = (e) => {
      if (!this.dirty) return
      e.preventDefault()
      e.returnValue = ''
    }
    window.addEventListener('beforeunload', this._onBeforeUnload)
  },
  beforeDestroy() {
    this._leaveConfirming = false
    try {
      // 销毁时关掉残留确认框，避免遮罩残留
      if (this.$msgbox && typeof this.$msgbox.close === 'function') {
        this.$msgbox.close()
      }
    } catch (e) { /* ignore */ }
    if (this._onBeforeUnload) {
      window.removeEventListener('beforeunload', this._onBeforeUnload)
      this._onBeforeUnload = null
    }
    if (this.$route && this.$route.meta) {
      this.$delete(this.$route.meta, 'appMainOverflow')
    }
  },
  beforeRouteLeave(to, from, next) {
    // 无脏数据：直接放行
    if (!this.dirty) {
      next()
      return
    }
    // 正在确认中：挡住重复导航（避免二次 $confirm 把弹窗立刻关掉）
    if (this._leaveConfirming) {
      next(false)
      return
    }
    // 关键：先 next(false) 取消本次导航，再异步弹窗。
    // 若在侧栏 click 同栈里直接 $confirm，弹窗会被同一次点击关掉，
    // 随后 catch → next(false)，页面就卡在阈值管理出不去。
    next(false)
    this._leaveConfirming = true
    const target = to && (to.fullPath || to.path)
    // 必须用宏任务：侧栏 click 同栈里 $nextTick 仍会被残留点击关掉 MessageBox
    window.setTimeout(() => {
      this.$confirm(
        '当前有未保存的阈值设置，离开后将丢失本次修改。确定离开吗？',
        '未保存提示',
        {
          type: 'warning',
          confirmButtonText: '确认离开',
          cancelButtonText: '继续编辑',
          distinguishCancelAndClose: true,
          closeOnClickModal: false,
          closeOnPressEscape: false,
          showClose: false
        }
      )
        .then(() => {
          this.dirty = false
          if (target) this.$router.push(target)
        })
        .catch(() => {})
        .then(() => {
          this._leaveConfirming = false
        })
    }, 80)
  },
  methods: {
    markDirty() {
      this.dirty = true
    },
    normalizeNavLabels(nav) {
      const labelMap = {
        water: '水质指标',
        device: '设备运行',
        mortality: '鱼类死亡/死亡率',
        health: '健康评分',
        feeding: '精准投喂'
      }
      const groupMap = {
        experiment: '实验'
      }
      return (nav || []).map(item => ({
        ...item,
        name: labelMap[item.id] || item.name,
        group: groupMap[item.id] || item.group
      }))
    },
    async bootstrap() {
      this.loading = true
      try {
        const [navRes, allRes] = await Promise.all([fetchNav(), fetchAll()])
        this.nav = this.normalizeNavLabels(navRes.data || [])
        this.applyConfig(allRes.data || {})
        await this.syncWaterSpeciesWithHealth({ persist: true })
        const qModule = this.$route && this.$route.query && this.$route.query.module
        if (qModule && this.config[qModule]) this.active = qModule
        // 阈值 AI 报警补同步到任务与计划
        fetchAiAlarms({ status: 'open' })
          .then(res => syncModuleAiAlarmsToMission((res && res.data) || [], {
            prefix: 'TH-AI-',
            source: 'threshold',
            category: 'water'
          }))
          .catch(err => console.warn('threshold → mission', err))
      } catch (e) {
        // BFF 不可用时仍保留左侧菜单，避免页面空白不可操作
        this.nav = this.normalizeNavLabels([
          { id: 'water', name: '水质阈值', group: '养殖' },
          { id: 'device', name: '设备阈值', group: '养殖' },
          { id: 'mortality', name: '死亡阈值', group: '养殖' },
          { id: 'health', name: '健康评分', group: '养殖' },
          { id: 'feeding', name: '投喂阈值', group: '养殖' },
          { id: 'alarm', name: '报警阈值', group: '报警' },
          { id: 'energy', name: '能耗阈值', group: '运维' },
          { id: 'experiment', name: '实验阈值', group: '运维' },
          { id: 'security', name: '安防阈值', group: '运维' },
          { id: 'holding', name: '存塘阈值', group: '运维' },
          { id: 'timeout', name: '超时阈值', group: '运维' },
          { id: 'association', name: '联动规则', group: '运维' }
        ])
        this.config = {
          water: { enabled: true, items: [] },
          device: { enabled: true, items: [] },
          mortality: { enabled: true, items: [] },
          health: { enabled: true, layers: [
            { id: 'env', name: '环境层', full: 20, warn: 10 },
            { id: 'appearance', name: '外观行为层', full: 60, warn: 30 },
            { id: 'internal', name: '内部肌体层', full: 20, warn: 10 }
          ] },
          feeding: { enabled: true, items: [] },
          alarm: { enabled: true, items: [] },
          energy: { enabled: true, items: [] },
          experiment: { enabled: true, items: [] },
          security: { enabled: true, items: [] },
          holding: { enabled: true, items: [] },
          timeout: { enabled: true, items: [] },
          association: { enabled: true, items: [] }
        }
        this.$message.warning('阈值服务暂不可用，已加载本地默认菜单，可切换查看')
      } finally {
        this.loading = false
      }
    },
    /** 水质品种与鱼类健康可筛品种对齐，并可选持久化 */
    async syncWaterSpeciesWithHealth({ persist = false } = {}) {
      if (!this.config.water || typeof this.config.water !== 'object') return false
      try {
        const catalog = await fetchHealthFilterSpecies()
        const prev = this.config.water.speciesTemps || []
        const next = syncSpeciesTempsWithHealthCatalog(prev, catalog)
        const changed = JSON.stringify(next) !== JSON.stringify(prev)
        if (!changed) return false
        this.$set(this.config.water, 'speciesTemps', next)
        if (persist) {
          await saveModule('water', this.config.water)
          this.bumpThresholdCaches()
          this.dirty = false
        } else {
          this.dirty = true
        }
        return true
      } catch (e) {
        console.warn('[阈值] 同步健康品种失败', e)
        return false
      }
    },
    bumpThresholdCaches() {
      invalidateThresholdCache()
      clearWaterThresholdCache()
      clearHealthPreviewCaches()
    },
    applyConfig(data) {
      const { meta, ...rest } = data
      this.meta = meta || this.meta
      this.config = rest
      this.dirty = false
    },
    async switchModule(id) {
      if (id === this.active) return
      this.active = id
    },
    async reload() {
      const { data } = await fetchAll()
      this.applyConfig(data || {})
    },
    async saveCurrent() {
      this.saving = true
      try {
        const payload = this.config[this.active]
        const res = await saveModule(this.active, payload)
        // 以接口返回为准写回当前模块，避免 reload 失败或读到旧默认值
        if (res && res.data) {
          this.$set(this.config, this.active, res.data)
          this.dirty = false
        }
        invalidateThresholdCache()
        clearWaterThresholdCache()
        clearHealthPreviewCaches()
        this.$message.success(res.message || '已保存')
        try {
          await this.reload()
        } catch (e) {
          // 已用返回值更新本地，reload 失败不回退
        }
      } finally {
        this.saving = false
      }
    },
    async saveEverything() {
      this.saving = true
      try {
        const res = await saveAll(this.config)
        invalidateThresholdCache()
        clearWaterThresholdCache()
        clearHealthPreviewCaches()
        this.$message.success(res.message || '已保存所有配置')
        this.applyConfig(res.data || {})
      } finally {
        this.saving = false
      }
    },
    async handleReset() {
      try {
        await this.$confirm('确定恢复全部默认阈值？当前自定义配置将被覆盖。', '提示', { type: 'warning' })
      } catch (e) {
        return
      }
      this.saving = true
      try {
        const res = await resetAll()
        invalidateThresholdCache()
        clearWaterThresholdCache()
        clearHealthPreviewCaches()
        this.$message.success(res.message || '已恢复默认')
        this.applyConfig(res.data || {})
        await this.syncWaterSpeciesWithHealth({ persist: true })
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.threshold-native {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background: #0a1520;
}
.threshold-native >>> .app-shell {
  height: 100%;
}
.page-body {
  min-height: 200px;
}
</style>
