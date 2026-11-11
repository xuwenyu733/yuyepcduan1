<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-s-opportunity head-icon heart-icon" />
      <div>
        <h2>鱼类健康评分阈值</h2>
        <p>
          总分100分三层加权：外部环境{{ weightEnv }}分 + 外观行为{{ weightApp }}分 + 内部肌体{{ weightInt }}分
          <span v-if="weightSum !== 100" class="weight-warn">（当前合计 {{ weightSum }}，需等于 100）</span>
        </p>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">综合健康评分等级划分</div>
      <div class="spectrum">
        <div class="seg red">
          <span>0 ~ 84（高风险）</span>
        </div>
        <div class="seg yellow">
          <span>85 ~ 94</span>
        </div>
        <div class="seg green">
          <span>95+</span>
        </div>
      </div>
      <div class="grade-row">
        <div v-for="g in local.grades" :key="g.label" class="grade-card" :class="g.color">
          <div class="g-top">
            <i class="dot" />
            <span class="g-label">{{ g.label }}</span>
          </div>
          <div class="g-range">{{ g.range }}</div>
          <div class="g-action">{{ g.action }}</div>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">分层评分权重与预警阈值</div>
      <div
        v-for="(layer, idx) in local.layers"
        :key="layer.id"
        class="layer-card"
        :class="{ 'row-disabled': !layer.enabled }"
      >
        <div class="layer-head">
          <div>
            <div class="name">{{ layer.name }}</div>
            <div class="hint">{{ layer.desc }}（满分{{ layer.full }}分）</div>
          </div>
          <enable-btn v-model="layer.enabled" @input="emitChange" />
        </div>
        <div class="layer-fields">
          <div class="field">
            <div class="lab">满分 (分)</div>
            <number-stepper v-model="layer.full" :step="1" :min="1" @change="onFull(idx)" />
          </div>
          <div class="field">
            <div class="lab">预警阈值 (分)</div>
            <number-stepper v-model="layer.warn" :step="1" :min="0" :max="layer.full" @change="emitChange" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from '../EnableBtn.vue'

export default {
  name: 'HealthModule',
  components: { EnableBtn },
  props: { value: { type: Object, required: true }},
  data() {
    return { local: JSON.parse(JSON.stringify(this.value)) }
  },
  computed: {
    weightEnv() {
      const l = this.local.layers.find(x => x.id === 'env')
      return l ? l.full : 20
    },
    weightApp() {
      const l = this.local.layers.find(x => x.id === 'appearance')
      return l ? l.full : 60
    },
    weightInt() {
      const l = this.local.layers.find(x => x.id === 'internal')
      return l ? l.full : 20
    },
    weightSum() {
      return this.weightEnv + this.weightApp + this.weightInt
    }
  },
  watch: {
    value: {
      deep: true,
      handler(v) { this.local = JSON.parse(JSON.stringify(v)) }
    }
  },
  methods: {
    onFull(idx) {
      const layer = this.local.layers[idx]
      if (layer.warn > layer.full) layer.warn = layer.full
      this.emitChange()
    },
    emitChange() {
      this.$emit('input', JSON.parse(JSON.stringify(this.local)))
    }
  }
}
</script>

<style scoped>
.heart-icon {
  color: #5ddea0 !important;
  background: rgba(93, 222, 160, 0.12) !important;
}
.weight-warn {
  color: #ff7a88;
  margin-left: 4px;
}
.spectrum {
  display: flex;
  height: 28px;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 14px;
  font-size: 11px;
  font-weight: 600;
}
.spectrum .seg {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  letter-spacing: 0.2px;
}
.spectrum .red { flex: 8.4; background: #c94a5a; }
.spectrum .yellow { flex: 1; background: #d4a017; }
.spectrum .green { flex: 0.6; background: #1dbf8a; min-width: 48px; }

.grade-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.grade-card {
  border-radius: 10px;
  padding: 14px 16px;
  border: 1px solid;
}
.grade-card.green {
  border-color: rgba(30, 200, 120, 0.45);
  background: rgba(30, 200, 120, 0.07);
}
.grade-card.yellow {
  border-color: rgba(240, 180, 41, 0.45);
  background: rgba(240, 180, 41, 0.07);
}
.grade-card.red {
  border-color: rgba(232, 93, 106, 0.5);
  background: rgba(232, 93, 106, 0.08);
}
.g-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}
.grade-card.green .dot { background: #1dbf8a; box-shadow: 0 0 0 3px rgba(29, 191, 138, 0.2); }
.grade-card.yellow .dot { background: #f0b429; box-shadow: 0 0 0 3px rgba(240, 180, 41, 0.2); }
.grade-card.red .dot { background: #ef5b6b; box-shadow: 0 0 0 3px rgba(239, 91, 107, 0.2); }
.g-label { font-size: 15px; font-weight: 650; color: #f2f7ff; }
.g-range { font-size: 13px; color: #9aadc6; margin-bottom: 6px; }
.g-action { font-size: 12px; color: #7d8fab; }

.layer-card {
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  padding: 14px 16px 12px;
  background: #0c1422;
  margin-bottom: 12px;
  transition: opacity 0.2s;
}
.layer-card:last-child { margin-bottom: 0; }
.layer-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}
.layer-fields {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 12px;
}
.lab {
  font-size: 12px;
  color: #7d8fab;
  margin-bottom: 8px;
}
.field >>> .num-step {
  width: 100%;
}
.row-disabled {
  opacity: 0.42;
  filter: grayscale(0.15);
}
@media (max-width: 900px) {
  .grade-row,
  .layer-fields { grid-template-columns: 1fr; }
}
</style>
