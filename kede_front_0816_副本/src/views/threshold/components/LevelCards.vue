<template>
  <div class="level-grid" :style="{ gridTemplateColumns: `repeat(${levels.length}, minmax(0, 1fr))` }">
    <div
      v-for="(lv, i) in levels"
      :key="i"
      class="level-card"
      :class="lv.color"
    >
      <div class="lv-label">{{ lv.label }}</div>
      <div class="lv-desc">{{ lv.desc }}</div>
      <div class="lv-input-wrap">
        <number-stepper
          size="lg"
          :tone="toneOf(lv.color)"
          :value="lv.value"
          :step="0.1"
          :precision="1"
          @input="onInput(i, $event)"
        />
        <span v-if="unit" class="lv-unit">{{ unit }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LevelCards',
  props: {
    levels: { type: Array, required: true },
    unit: { type: String, default: '' }
  },
  methods: {
    toneOf(color) {
      if (color === 'green') return 'safe'
      if (color === 'yellow' || color === 'orange') return 'warn'
      if (color === 'red') return 'danger'
      return 'accent'
    },
    onInput(i, val) {
      const next = this.levels.map((lv, idx) =>
        idx === i ? { ...lv, value: val } : { ...lv }
      )
      this.$emit('change', next)
    }
  }
}
</script>

<style scoped>
.level-grid {
  display: grid;
  gap: 12px;
}
.level-card {
  border-radius: 12px;
  padding: 14px 14px 16px;
  border: 1px solid;
  background: #132030;
  min-height: 148px;
}
.level-card.green {
  border-color: rgba(24, 201, 168, 0.55);
  box-shadow: inset 0 0 0 1px rgba(24, 201, 168, 0.08);
}
.level-card.orange,
.level-card.yellow {
  border-color: rgba(240, 180, 41, 0.55);
}
.level-card.red {
  border-color: rgba(239, 91, 107, 0.65);
  background: linear-gradient(180deg, rgba(90, 24, 36, 0.35), #132030 70%);
}
.lv-label {
  font-weight: 700;
  color: #f3f7ff;
  margin-bottom: 6px;
  font-size: 15px;
}
.level-card.green .lv-label { color: #3de0bf; }
.level-card.orange .lv-label,
.level-card.yellow .lv-label { color: #f0b429; }
.level-card.red .lv-label { color: #ff7a88; }
.lv-desc {
  font-size: 12px;
  color: #8fa0b8;
  margin-bottom: 14px;
  min-height: 34px;
  line-height: 1.4;
}
.lv-input-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}
.lv-input-wrap >>> .num-step {
  flex: 1;
  width: auto;
}
.lv-unit {
  color: #7d8fab;
  font-size: 12px;
  white-space: nowrap;
}
</style>
