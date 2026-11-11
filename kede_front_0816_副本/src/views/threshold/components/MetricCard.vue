<template>
  <div class="metric-card" :class="{ disabled: !item.enabled }">
    <div class="card-head">
      <div>
        <div class="title">{{ item.name }}</div>
        <div class="unit">{{ item.unit || '—' }}</div>
        <div v-if="item.desc" class="desc">{{ item.desc }}</div>
      </div>
      <enable-btn :value="item.enabled" @input="onEnable" />
    </div>
    <div class="range-bar" aria-hidden="true">
      <span
        v-for="(seg, i) in segments"
        :key="i"
        class="seg"
        :class="seg.type"
        :style="{ width: seg.width + '%' }"
      />
    </div>
    <div class="fields">
      <div class="col">
        <div class="col-label warn"><i class="el-icon-warning" /> 警告</div>
        <div class="row">
          <span>低</span>
          <number-stepper class="in-warn" :value="item.warnLow" :step="step" :precision="precision" @input="set('warnLow', $event)" />
        </div>
        <div class="row">
          <span>高</span>
          <number-stepper class="in-warn" :value="item.warnHigh" :step="step" :precision="precision" @input="set('warnHigh', $event)" />
        </div>
      </div>
      <div class="col">
        <div class="col-label critical"><i class="el-icon-warning" /> 临界</div>
        <div class="row">
          <span>低</span>
          <number-stepper class="in-crit" :value="item.criticalLow" :step="step" :precision="precision" @input="set('criticalLow', $event)" />
        </div>
        <div class="row">
          <span>高</span>
          <number-stepper class="in-crit" :value="item.criticalHigh" :step="step" :precision="precision" @input="set('criticalHigh', $event)" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from './EnableBtn.vue'

export default {
  name: 'MetricCard',
  components: { EnableBtn },
  props: {
    item: { type: Object, required: true }
  },
  computed: {
    step() {
      const s = Number(this.item.step)
      return Number.isFinite(s) && s > 0 ? s : 1
    },
    precision() {
      const str = String(this.step)
      const i = str.indexOf('.')
      return i >= 0 ? str.length - i - 1 : 0
    },
    segments() {
      const cl = Number(this.item.criticalLow)
      const wl = Number(this.item.warnLow)
      const wh = Number(this.item.warnHigh)
      const ch = Number(this.item.criticalHigh)
      const nums = [cl, wl, wh, ch]
      if (nums.some(n => !Number.isFinite(n))) {
        return [
          { type: 'critical', width: 16 },
          { type: 'warn', width: 18 },
          { type: 'safe', width: 32 },
          { type: 'warn', width: 18 },
          { type: 'critical', width: 16 }
        ]
      }

      // expected: criticalLow ≤ warnLow ≤ warnHigh ≤ criticalHigh
      const inner = Math.max(ch - cl, wh - wl, Math.abs(this.step) || 1e-6)
      const pad = Math.max(inner * 1.5, Math.abs(this.step) * 4)
      let min = cl - pad
      let max = ch + pad
      // 多数水质指标不以负值展示
      if (cl >= 0 && wl >= 0 && min < 0) min = 0

      const total = Math.max(max - min, 1e-6)
      const edges = [min, cl, wl, wh, ch, max]
      const types = ['critical', 'warn', 'safe', 'warn', 'critical']
      return types.map((type, i) => {
        const w = Math.max(0, edges[i + 1] - edges[i])
        return { type, width: (w / total) * 100 }
      })
    }
  },
  methods: {
    set(key, val) {
      this.$emit('change', { ...this.item, [key]: val })
    },
    onEnable(v) {
      this.$emit('change', { ...this.item, enabled: v })
    }
  }
}
</script>

<style scoped>
.metric-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 12px;
  padding: 16px 16px 14px;
  transition: opacity 0.2s;
}
.metric-card.disabled {
  opacity: 0.42;
  filter: grayscale(0.15);
}
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 12px;
}
.title {
  font-size: 15px;
  font-weight: 650;
  color: #f3f7ff;
  letter-spacing: 0.2px;
}
.unit {
  margin-top: 5px;
  font-size: 12px;
  color: #7d8fab;
}
.desc {
  margin-top: 4px;
  font-size: 11px;
  color: #6b7c94;
  line-height: 1.35;
  max-width: 200px;
}
.range-bar {
  display: flex;
  height: 5px;
  border-radius: 999px;
  overflow: hidden;
  margin-bottom: 14px;
  background: #0a1220;
}
.seg {
  flex: 0 0 auto;
  height: 100%;
  min-width: 0;
  transition: width 0.2s ease;
}
.seg.safe { background: #18c9a8; }
.seg.warn { background: #f0b429; }
.seg.critical { background: #ef5b6b; }
.fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 18px;
}
.col-label {
  font-size: 12px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
}
.col-label.warn { color: #f0b429; }
.col-label.critical { color: #ef5b6b; }
.row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #8fa0b8;
  font-size: 12px;
}
.row >>> .num-step { flex: 1; width: auto; min-width: 0; }
.row >>> .in-warn { border-color: rgba(240, 180, 41, 0.55); }
.row >>> .in-crit { border-color: rgba(180, 90, 140, 0.65); }
</style>
