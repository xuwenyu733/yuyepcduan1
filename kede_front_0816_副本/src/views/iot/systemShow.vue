<template>
  <div class="system-show-container">
    <div class="system-content">
      <SystemHeader 
        :system-name="systemData.systemName"
        :last-update="systemData.lastUpdate"
        @refresh="loadSystemData(true)"
      />
      
      <SystemOverview :data="systemData" />
      
      <div class="system-main-view">
        <FishPond 
          v-for="(pond, idx) in systemData.fishPonds"
          :key="pond.id || idx"
          :data="pond" 
          :position="pondPositions[idx] || { x: 150 + idx * 300, y: 250 }" 
        />
        
        <MicroFilter 
          :data="systemData.microFilter" 
          :position="{ x: 1050, y: 250 }" 
        />
        
        <BioFilter 
          :data="systemData.bioFilter" 
          :position="{ x: 1350, y: 250 }" 
        />
        
        <AerationSystem 
          :data="systemData.aeration" 
          :position="{ x: 1650, y: 250 }" 
        />
        
        <PipeSystem />
      </div>
      <div style="width: 100%;height: 200px;"></div>
    </div>
  </div>
</template>

<script>
import SystemHeader from '@/components/systemItem/SystemHeader.vue'
import SystemOverview from '@/components/systemItem/SystemOverview.vue'
import FishPond from '@/components/systemItem/FishPond.vue'
import MicroFilter from '@/components/systemItem/MicroFilter.vue'
import BioFilter from '@/components/systemItem/BioFilter.vue'
import AerationSystem from '@/components/systemItem/AerationSystem.vue'
import WaterPump from '@/components/systemItem/WaterPump.vue'
import PipeSystem from '@/components/systemItem/PipeSystem.vue'
import { fishApi } from '@/api/modules'

function onOff(v, fallback = 'stopped') {
  if (v === true) return 'running'
  if (v === false) return 'stopped'
  return fallback
}

export default {
  name: "SystemShow",
  components: {
    SystemHeader,
    SystemOverview,
    FishPond,
    MicroFilter,
    BioFilter,
    AerationSystem,
    WaterPump,
    PipeSystem
  },
  data() {
    return {
      _pollTimer: null,
      _loading: false,
      pondPositions: [
        { x: 150, y: 250 },
        { x: 450, y: 250 },
        { x: 750, y: 250 }
      ],
      systemData: {
        systemName: "门头沟循环水系统",
        lastUpdate: new Date(),
        fishPonds: [
          { id: 'pond1', name: '门头沟1#', temperature: null, ph: null, dissolvedOxygen: null, doSaturation: null, waterLevel: null, fishCount: '--' },
          { id: 'pond2', name: '门头沟2#', temperature: null, ph: null, dissolvedOxygen: null, doSaturation: null, waterLevel: null, fishCount: '--' },
          { id: 'pond3', name: '门头沟3#', temperature: null, ph: null, dissolvedOxygen: null, doSaturation: null, waterLevel: null, fishCount: '--' }
        ],
        microFilter: {
          id: "microFilter1",
          status: "stopped",
          flowRate: 0,
          pressure: 0,
          efficiency: 0,
          circulationPumps: [
            { id: "pump1", status: "stopped", flowRate: 0, power: 0, pressure: 0 },
            { id: "pump2", status: "stopped", flowRate: 0, power: 0, pressure: 0 }
          ]
        },
        bioFilter: {
          id: "bioFilter1",
          status: "running",
          temperature: null,
          efficiency: 0,
          bacteriaActivity: 0,
          circulationPump: { id: "pump3", status: "stopped", flowRate: 0, power: 0, pressure: 0 }
        },
        aeration: {
          id: "aeration1",
          status: "stopped",
          oxygenLevel: null,
          airFlowRate: 0,
          power: 0,
          circulationPump: { id: "pump4", status: "stopped", flowRate: 0, power: 0, pressure: 0 }
        },
        circulationPumps: []
      }
    }
  },
  methods: {
    levelToPct(level) {
      const n = Number(level)
      if (!Number.isFinite(n) || n <= 0) return null
      return Math.round(Math.min(100, Math.max(0, n)))
    },
    applyMentougouLive(live) {
      if (!live) return
      const pools = Array.isArray(live.pools) ? live.pools.slice() : []
      pools.sort((a, b) => Number(a.idx || 0) - Number(b.idx || 0))
      const nextPonds = [1, 2, 3].map((idx) => {
        const p = pools.find((x) => Number(x.idx) === idx) || pools[idx - 1] || {}
        return {
          id: `pond${idx}`,
          name: p.lake_name || p.name || `门头沟${idx}#`,
          temperature: p.lake_temp != null ? Number(p.lake_temp) : null,
          ph: p.lake_ph != null ? Number(p.lake_ph) : null,
          dissolvedOxygen: p.lake_do != null ? Number(p.lake_do) : null,
          doSaturation: p.do_saturation != null ? Number(p.do_saturation) : null,
          // 仅 1# 有可靠水位探头；2#/3# 无独立水位时显示 --
          waterLevel: idx === 1 ? this.levelToPct(p.water_level) : null,
          fishCount: '--'
        }
      })
      this.systemData.fishPonds = nextPonds

      const extras = live.extras || {}
      const dev = extras.devices || {}
      const avgSat = Number(extras.doSaturation)
      const pump1On = !!dev.pump1
      const pump2On = !!dev.pump2
      const freq1 = Number(dev.pump1Freq)
      const freq2 = Number(dev.pump2Freq)

      this.systemData.microFilter = {
        ...this.systemData.microFilter,
        status: onOff(dev.microFilter, this.systemData.microFilter.status),
        circulationPumps: [
          {
            id: 'pump1',
            status: onOff(dev.pump1),
            flowRate: pump1On && Number.isFinite(freq1) ? Math.round(freq1 * 15) : 0,
            power: pump1On ? 2.1 : 0,
            pressure: pump1On ? 0.25 : 0
          },
          {
            id: 'pump2',
            status: onOff(dev.pump2),
            flowRate: pump2On && Number.isFinite(freq2) ? Math.round(freq2 * 15) : 0,
            power: pump2On ? 1.8 : 0,
            pressure: pump2On ? 0.22 : 0
          }
        ]
      }

      this.systemData.aeration = {
        ...this.systemData.aeration,
        status: onOff(dev.aerationFan, this.systemData.aeration.status),
        oxygenLevel: Number.isFinite(avgSat) ? avgSat : this.systemData.aeration.oxygenLevel,
        airFlowRate: dev.aerationFan ? 800 : 0,
        power: dev.aerationFan ? 4.5 : 0
      }

      if (extras.airTemp != null) {
        this.systemData.bioFilter = {
          ...this.systemData.bioFilter,
          temperature: Number(extras.airTemp)
        }
      }

      this.systemData.circulationPumps = this.systemData.microFilter.circulationPumps
      const asOf = live.waterAsOf ? new Date(String(live.waterAsOf).replace(/-/g, '/')) : new Date()
      this.systemData.lastUpdate = Number.isNaN(asOf.getTime()) ? new Date() : asOf
      this.systemData.systemName = '门头沟循环水系统'
    },
    async loadSystemData(force = false) {
      if (this._loading) return
      this._loading = true
      try {
        const res = await fishApi.getMentougouWaterLive(force ? { force: 1 } : undefined)
        if (res && Number(res.code) === 0 && res.data) {
          this.applyMentougouLive(res.data)
        }
      } catch (e) {
        console.warn('[systemShow] mentougou live', e && (e.message || e))
      } finally {
        this._loading = false
      }
    }
  },
  mounted() {
    this.loadSystemData(false)
    this._pollTimer = setInterval(() => {
      if (!this._isDestroyed) this.loadSystemData(false)
    }, 30000)
  },
  beforeDestroy() {
    this._isDestroyed = true
    if (this._pollTimer) clearInterval(this._pollTimer)
  }
}
</script>

<style scoped>
.system-show-container {
  width: 100%;
  height: calc(100vh - 84px);
  background: linear-gradient(135deg, #1a2a3a 0%, #0d1b2a 100%);
  color: #e2e8f0;
  overflow: auto;
  position: relative;
}

.system-content {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

.system-main-view {
  position: relative;
  width: 100%;
  height: calc(100% - 200px);
  background: rgba(30, 41, 59, 0.7);
  border: 1px solid #334155;
  border-radius: 12px;
  margin-top: 20px;
  overflow: auto;
}

.system-main-view::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(51, 65, 85, 0.3) 1px, transparent 1px),
    linear-gradient(90deg, rgba(51, 65, 85, 0.3) 1px, transparent 1px);
  background-size: 20px 20px;
  pointer-events: none;
  mask-image: linear-gradient(0deg, #1e293b, rgba(30, 41, 59, 0.6));
}
</style>
