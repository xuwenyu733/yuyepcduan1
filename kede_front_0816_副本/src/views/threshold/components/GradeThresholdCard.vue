<template>
  <div class="grade-card" :class="{ disabled: !enabled }">
    <div class="head">
      <div class="title">{{ title }}</div>
      <enable-btn :value="enabled" @input="$emit('update:enabled', $event)" />
    </div>

    <div class="row safe">
      <div class="left">
        <div class="tag">安全阈值</div>
        <div class="desc">≤ 此值安全</div>
      </div>
      <div class="right">
        <number-stepper tone="safe" :value="safeValue" :step="0.01" :precision="2" @input="onSafe" />
        <span class="unit">{{ unit }}</span>
      </div>
    </div>

    <div class="row warn">
      <div class="left">
        <div class="tag">警戒阈值</div>
        <div class="desc">> 安全值 ≤ 此值为警戒</div>
      </div>
      <div class="right">
        <number-stepper tone="warn" :value="warnValue" :step="0.01" :precision="2" @input="onWarn" />
        <span class="unit">{{ unit }}</span>
      </div>
    </div>

    <div class="row danger">
      <div class="left">
        <div class="tag">危险区域</div>
        <div class="desc">> {{ warnValue }} {{ unit }} 为危险</div>
      </div>
      <div class="right auto">
        <span>自动计算</span>
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from './EnableBtn.vue'

export default {
  name: 'GradeThresholdCard',
  components: { EnableBtn },
  props: {
    title: { type: String, required: true },
    unit: { type: String, default: 'mg/L' },
    safeValue: { type: Number, required: true },
    warnValue: { type: Number, required: true },
    enabled: { type: Boolean, default: true }
  },
  methods: {
    onSafe(val) {
      this.$emit('change', { safe: val, warn: this.warnValue })
    },
    onWarn(val) {
      this.$emit('change', { safe: this.safeValue, warn: val })
    }
  }
}
</script>

<style scoped>
.grade-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 12px;
  padding: 16px 18px;
}
.grade-card.disabled { opacity: 0.42; filter: grayscale(0.15); }
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.title {
  font-size: 16px;
  font-weight: 700;
  color: #409eff;
}
.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-top: 1px solid rgba(36, 50, 71, 0.75);
}
.row:first-of-type { border-top: 1px solid rgba(36, 50, 71, 0.55); }
.tag {
  font-size: 14px;
  font-weight: 650;
  margin-bottom: 4px;
}
.desc {
  font-size: 12px;
  color: #7d8fab;
}
.safe .tag { color: #3de0bf; }
.warn .tag { color: #f0b429; }
.danger .tag { color: #ff7a88; }
.right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.unit { color: #7d8fab; font-size: 12px; }
.auto {
  min-width: 96px;
  height: 34px;
  border-radius: 8px;
  border: 1px dashed rgba(239, 91, 107, 0.45);
  color: #ff7a88;
  font-size: 13px;
  justify-content: center;
  background: rgba(239, 91, 107, 0.08);
}
</style>
