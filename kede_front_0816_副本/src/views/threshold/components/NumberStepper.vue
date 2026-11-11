<template>
  <div class="num-step" :class="[tone, { lg: size === 'lg', sm: size === 'sm' }]">
    <input
      ref="input"
      type="number"
      :step="step"
      :min="min"
      :max="max"
      :value="displayValue"
      @input="onInput"
      @blur="onBlur"
      @keydown.up.prevent="bump(1)"
      @keydown.down.prevent="bump(-1)"
    />
    <div class="spinners" aria-hidden="true">
      <button type="button" class="spin up" tabindex="-1" @mousedown.prevent="bump(1)">
        <span class="tri" />
      </button>
      <button type="button" class="spin down" tabindex="-1" @mousedown.prevent="bump(-1)">
        <span class="tri" />
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NumberStepper',
  props: {
    value: { type: [Number, String], default: 0 },
    step: { type: Number, default: 1 },
    min: { type: Number, default: undefined },
    max: { type: Number, default: undefined },
    precision: { type: Number, default: undefined },
    tone: { type: String, default: '' }, // accent | danger | safe | warn
    size: { type: String, default: 'md' } // sm | md | lg
  },
  computed: {
    displayValue() {
      if (this.value === null || this.value === undefined || this.value === '') return ''
      return this.value
    },
    resolvedStep() {
      const s = Number(this.step)
      return Number.isFinite(s) && s > 0 ? s : 1
    },
    resolvedPrecision() {
      if (this.precision !== undefined && this.precision !== null) return this.precision
      const step = this.resolvedStep
      const str = String(step)
      const i = str.indexOf('.')
      return i >= 0 ? str.length - i - 1 : 0
    }
  },
  methods: {
    normalize(n) {
      if (!Number.isFinite(n)) return 0
      let v = n
      if (this.min !== undefined && v < this.min) v = this.min
      if (this.max !== undefined && v > this.max) v = this.max
      const p = Math.pow(10, this.resolvedPrecision)
      v = Math.round(v * p) / p
      return v
    },
    emit(v) {
      this.$emit('input', v)
      this.$emit('change', v)
    },
    onInput(e) {
      const raw = e.target.value
      if (raw === '' || raw === '-') return
      const n = Number(raw)
      if (!Number.isFinite(n)) return
      this.emit(this.normalize(n))
    },
    onBlur(e) {
      const raw = e.target.value
      if (raw === '' || raw === '-') {
        this.emit(this.normalize(0))
        return
      }
      this.emit(this.normalize(Number(raw)))
    },
    bump(dir) {
      const cur = Number(this.value)
      const base = Number.isFinite(cur) ? cur : 0
      this.emit(this.normalize(base + dir * this.resolvedStep))
    }
  }
}
</script>

<style scoped>
.num-step {
  position: relative;
  display: inline-flex;
  align-items: stretch;
  width: 96px;
  height: 34px;
  background: #0a1220;
  border: 1px solid #3a3836;
  border-radius: 6px;
  overflow: hidden;
  vertical-align: middle;
}
.num-step.sm { width: 84px; height: 30px; }
.num-step.lg { width: 120px; height: 48px; }
.num-step input {
  flex: 1;
  min-width: 0;
  width: 100%;
  height: 100%;
  border: 0;
  outline: none;
  background: transparent;
  color: #f3f7ff;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  padding: 0 22px 0 8px;
  -moz-appearance: textfield;
}
.num-step.lg input { font-size: 26px; font-weight: 700; padding-right: 28px; }
.num-step input::-webkit-outer-spin-button,
.num-step input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.spinners {
  position: absolute;
  top: 1px;
  right: 1px;
  bottom: 1px;
  width: 18px;
  display: flex;
  flex-direction: column;
  background: #454545;
  border-radius: 0 5px 5px 0;
  overflow: hidden;
}
.spin {
  flex: 1;
  border: 0;
  padding: 0;
  margin: 0;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.spin:hover { background: rgba(255, 255, 255, 0.08); }
.spin:active { background: rgba(255, 255, 255, 0.14); }
.tri {
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
}
.spin.up .tri { border-bottom: 5px solid #fff; }
.spin.down .tri { border-top: 5px solid #fff; }

.num-step.accent { border-color: rgba(64, 158, 255, 0.55); }
.num-step.accent input { color: #409eff; }
.num-step.safe { border-color: rgba(24, 201, 168, 0.55); }
.num-step.safe input { color: #3de0bf; }
.num-step.warn { border-color: rgba(240, 180, 41, 0.65); }
.num-step.warn input { color: #f0b429; }
.num-step.danger { border-color: rgba(239, 91, 107, 0.65); }
.num-step.danger input { color: #ff6b7a; }
.num-step:focus-within { border-color: #409eff; }
</style>
