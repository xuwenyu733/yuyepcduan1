<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-s-goods head-icon" />
      <div>
        <h2>投喂与生长阈值配置</h2>
        <p>基础投喂率 · 环境修正 · 投喂偏差 · 摄食行为 · FCR · 生长模型</p>
      </div>
    </div>

    <div class="feed-layout">
      <div class="panel rates-panel">
        <div class="panel-title">基础投喂率（加州鲈·按规格）</div>
        <div class="rates-head">
          <span>规格</span>
          <span class="accent">基础投喂率 (%)</span>
          <span>启用</span>
        </div>
        <div class="rates-scroll">
          <div
            v-for="row in local.baseRates"
            :key="row.spec"
            class="rate-row"
            :class="{ 'row-disabled': !row.enabled }"
          >
            <div class="spec">
              <span class="spec-name">{{ row.spec }}</span>
              <span class="hint">({{ row.approx }})</span>
            </div>
            <number-stepper tone="accent" v-model="row.rate" :step="0.1" @change="emitChange" />
            <enable-btn v-model="row.enabled" @input="emitChange" />
          </div>
        </div>
      </div>

      <div class="right-col">
        <div class="panel">
          <div class="panel-title">水温修正系数</div>
          <div class="setting-row" v-for="t in local.tempFactors" :key="t.range">
            <div class="name">{{ t.range }} <span class="hint">({{ t.label }})</span></div>
            <div class="row-right">
              <span class="suffix">×</span>
              <number-stepper tone="accent" v-model="t.factor" :step="0.05" :precision="2" @change="emitChange" />
            </div>
          </div>
        </div>

        <div class="panel meal-panel">
          <div class="panel-title">日投喂频次（按规格）</div>
          <div class="meal-scroll">
            <div
              v-for="m in local.mealFreq"
              :key="m.range"
              class="meal-card"
              :class="{ 'row-disabled': !m.enabled }"
            >
              <div class="meal-head">
                <span class="name">{{ m.range }}</span>
                <enable-btn v-model="m.enabled" @input="emitChange" />
              </div>
              <div class="meal-inputs">
                <div class="meal-field">
                  <span class="lab">最少 (餐)</span>
                  <number-stepper tone="accent" v-model="m.min" :step="1" :min="1" @change="emitChange" />
                </div>
                <div class="meal-field">
                  <span class="lab">最多 (餐)</span>
                  <number-stepper tone="accent" v-model="m.max" :step="1" :min="1" @change="emitChange" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-grid">
      <div class="panel bottom-card">
        <div class="panel-title">饵料系数 FCR 目标区间</div>
        <p class="hint">{{ local.fcr.desc }}</p>
        <div class="stack-fields">
          <div class="field">
            <div class="lab">目标值</div>
            <number-stepper size="lg" tone="accent" v-model="local.fcr.target" :step="0.05" @change="emitChange" />
          </div>
          <div class="field">
            <div class="lab">区间下限</div>
            <number-stepper size="lg" tone="safe" v-model="local.fcr.min" :step="0.1" @change="emitChange" />
          </div>
          <div class="field">
            <div class="lab">区间上限（超出预警）</div>
            <number-stepper size="lg" tone="danger" v-model="local.fcr.max" :step="0.1" @change="emitChange" />
          </div>
        </div>
      </div>

      <div class="panel bottom-card">
        <div class="panel-title">生长模型偏差阈值</div>
        <p class="hint">{{ local.growth.desc }}</p>
        <div class="stack-fields">
          <div class="field">
            <div class="lab">日增重最低值 (g/天)</div>
            <number-stepper size="lg" tone="accent" v-model="local.growth.dailyGainMin" :step="0.1" @change="emitChange" />
          </div>
          <div class="field">
            <div class="lab">SGR特定生长率下限 (%/天)</div>
            <number-stepper size="lg" tone="accent" v-model="local.growth.sgrMin" :step="0.1" @change="emitChange" />
          </div>
          <div class="field">
            <div class="lab">模型预测偏差上限 (%)</div>
            <number-stepper size="lg" tone="accent" v-model="local.growth.modelDevMax" :step="1" @change="emitChange" />
          </div>
        </div>
      </div>

      <div class="panel bottom-card" :class="{ 'row-disabled': !local.feedDev.enabled }">
        <div class="panel-title">
          <span>投喂偏差预警</span>
          <enable-btn v-model="local.feedDev.enabled" @input="emitChange" />
        </div>
        <p class="hint">{{ local.feedDev.desc }}</p>
        <div class="field">
          <div class="lab">偏差阈值 (%)</div>
          <number-stepper size="lg" tone="warn" v-model="local.feedDev.value" :step="5" @change="emitChange" />
        </div>
        <div class="behavior-title">摄食行为四段判定</div>
        <ul class="behavior-list">
          <li v-for="b in local.behavior" :key="b.label">
            <i class="dot" :class="b.color" />
            <span :class="'txt-' + b.color">{{ b.label }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from '../EnableBtn.vue'

export default {
  name: 'FeedingModule',
  components: { EnableBtn },
  props: { value: { type: Object, required: true } },
  data() {
    return { local: this.normalize(this.value) }
  },
  watch: {
    value: {
      deep: true,
      handler(v) { this.local = this.normalize(v) }
    }
  },
  methods: {
    normalize(v) {
      const data = JSON.parse(JSON.stringify(v || {}))
      if (!data.fcr) data.fcr = { target: 1.35, min: 1.2, max: 1.5, desc: '' }
      if (data.fcr.target == null) data.fcr.target = 1.35
      if (!data.growth) {
        data.growth = {
          dailyGainMin: 0.5,
          sgrMin: (data.sgr && data.sgr.value) || 0.8,
          modelDevMax: (data.growthDev && data.growthDev.value) || 15,
          desc: '实测与AI预测曲线偏差过大触发长势预警'
        }
      }
      if (data.feedDev && data.feedDev.enabled == null) data.feedDev.enabled = true
      if (!Array.isArray(data.behavior) || !data.behavior[0] || !data.behavior[0].color) {
        data.behavior = [
          { label: '快速摄食 · 5-10 分钟吃完', color: 'green' },
          { label: '缓慢摄食 · 10-15 分钟吃完', color: 'yellow' },
          { label: '残饵较多 · 15 分钟后仍有残饵', color: 'orange' },
          { label: '完全不摄食 · 投喂后无摄食行为', color: 'red' }
        ]
      }
      return data
    },
    emitChange() {
      this.$emit('input', JSON.parse(JSON.stringify(this.local)))
    }
  }
}
</script>

<style scoped>
.feed-layout {
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  gap: 14px;
  margin-bottom: 14px;
  align-items: stretch;
}
.right-col {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.rates-panel {
  display: flex;
  flex-direction: column;
  min-height: 0;
  margin-bottom: 0;
}
.rates-head {
  display: grid;
  grid-template-columns: minmax(110px, 1.1fr) 110px 72px;
  align-items: center;
  gap: 12px;
  padding: 0 4px 10px;
  border-bottom: 1px solid #243247;
  font-size: 12px;
  color: #7d8fab;
}
.rates-head .accent {
  color: #409eff;
  text-align: center;
  font-weight: 600;
}
.rates-head span:last-child { text-align: center; }
.rates-scroll {
  max-height: 420px;
  overflow-y: auto;
  padding-right: 4px;
}
.rates-scroll::-webkit-scrollbar { width: 6px; }
.rates-scroll::-webkit-scrollbar-thumb {
  background: rgba(124, 143, 161, 0.3);
  border-radius: 3px;
}
.rates-scroll::-webkit-scrollbar-track { background: transparent; }
.rate-row {
  display: grid;
  grid-template-columns: minmax(110px, 1.1fr) 110px 72px;
  align-items: center;
  justify-items: start;
  gap: 12px;
  padding: 10px 4px;
  border-bottom: 1px solid rgba(36, 50, 71, 0.75);
}
.rate-row:last-child { border-bottom: none; }
.rate-row >>> .num-step { justify-self: center; }
.rate-row >>> .enable-btn { justify-self: center; }
.spec { min-width: 0; }
.spec-name { color: #e8f1ff; font-weight: 500; }

.meal-panel { margin-bottom: 0; flex: 1; min-height: 0; }
.meal-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}
.meal-scroll::-webkit-scrollbar { height: 6px; }
.meal-scroll::-webkit-scrollbar-thumb {
  background: rgba(124, 143, 161, 0.3);
  border-radius: 3px;
}
.meal-card {
  flex: 0 0 240px;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  padding: 12px 14px;
  background: #0c1422;
}
.meal-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.meal-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  align-items: end;
}
.meal-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}
.meal-field >>> .num-step { width: 100%; }
.lab {
  font-size: 12px;
  color: #7d8fab;
  white-space: nowrap;
}

.bottom-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  align-items: stretch;
}
.bottom-card {
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
}
.bottom-card .hint {
  margin: 0 0 14px;
  line-height: 1.5;
  min-height: 36px;
}
.stack-fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: auto;
}
.field { min-width: 0; }
.field .lab { margin-bottom: 8px; display: block; }
.field >>> .num-step,
.stack-fields >>> .num-step {
  width: 100%;
}
.bottom-card >>> .num-step.lg {
  width: 100%;
  height: 44px;
}
.bottom-card >>> .num-step.lg input {
  font-size: 20px;
}

.behavior-title {
  margin-top: 16px;
  margin-bottom: 10px;
  font-size: 13px;
  font-weight: 600;
  color: #e8f1ff;
}
.behavior-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.behavior-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot.green { background: #1dbf8a; }
.dot.yellow { background: #f0b429; }
.dot.orange { background: #f0783c; }
.dot.red { background: #ef5b6b; }
.txt-green { color: #1dbf8a; }
.txt-yellow { color: #f0b429; }
.txt-orange { color: #f0783c; }
.txt-red { color: #ef5b6b; }

.row-disabled {
  opacity: 0.42;
  filter: grayscale(0.15);
  transition: opacity 0.2s;
}

@media (max-width: 1200px) {
  .feed-layout,
  .bottom-grid { grid-template-columns: 1fr; }
}
</style>
