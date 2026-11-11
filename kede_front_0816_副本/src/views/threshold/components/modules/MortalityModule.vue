<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-warning-outline head-icon fish-icon" />
      <div>
        <h2>鱼类死亡阈值配置</h2>
        <p>按规格差异化设置死亡率 / 近5日趋势 / 单日数量三重触发阈值</p>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">
        <span>分规格死亡预警阈值</span>
        <span class="note">满足任一条件即触发告警</span>
      </div>
      <table class="dark-table mort-table">
        <thead>
          <tr>
            <th>鱼种规格</th>
            <th>死亡率阈值 (%)</th>
            <th>近5日均值升幅 (%)</th>
            <th>单日死亡数 (尾)</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in local.specs" :key="row.spec" :class="{ 'row-disabled': !row.enabled }">
            <td class="spec">{{ row.spec }}</td>
            <td><number-stepper tone="warn" v-model="row.rate" :step="0.01" :precision="2" @change="emitChange" /></td>
            <td><number-stepper tone="accent" v-model="row.trendRise" :step="1" @change="emitChange" /></td>
            <td><number-stepper tone="danger" v-model="row.dailyCount" :step="1" @change="emitChange" /></td>
            <td><enable-btn v-model="row.enabled" @input="emitChange" /></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="two-col">
      <div class="panel bottom-card" :class="{ 'row-disabled': !local.pond7day.enabled }">
        <div class="panel-title">
          <span>鱼池七日死亡率预警</span>
          <enable-btn v-model="local.pond7day.enabled" @input="emitChange" />
        </div>
        <p class="hint">{{ local.pond7day.desc }}</p>
        <div class="value-row">
          <number-stepper
            class="wide"
            size="lg"
            tone="danger"
            v-model="local.pond7day.value"
            :step="0.01"
            :precision="2"
            @change="emitChange"
          />
          <span class="suffix">%</span>
        </div>
      </div>
      <div class="panel bottom-card" :class="{ 'row-disabled': !local.factoryAvg.enabled }">
        <div class="panel-title">
          <span>全场整体平均死亡率</span>
          <enable-btn v-model="local.factoryAvg.enabled" @input="emitChange" />
        </div>
        <p class="hint">{{ local.factoryAvg.desc }}</p>
        <div class="value-row">
          <number-stepper
            class="wide"
            size="lg"
            tone="danger"
            v-model="local.factoryAvg.value"
            :step="0.01"
            :precision="2"
            @change="emitChange"
          />
          <span class="suffix">%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from '../EnableBtn.vue'
export default {
  name: 'MortalityModule',
  components: { EnableBtn },
  props: { value: { type: Object, required: true } },
  data() { return { local: JSON.parse(JSON.stringify(this.value)) } },
  watch: { value: { deep: true, handler(v) { this.local = JSON.parse(JSON.stringify(v)) } } },
  methods: { emitChange() { this.$emit('input', JSON.parse(JSON.stringify(this.local))) } }
}
</script>

<style scoped>
.fish-icon {
  color: #5ddea0 !important;
  background: rgba(93, 222, 160, 0.12) !important;
}
.panel-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.note {
  font-size: 12px;
  font-weight: 400;
  color: #7d8fab;
}
.spec {
  color: #c8d4e6;
  font-weight: 500;
  white-space: nowrap;
}
.mort-table th:last-child,
.mort-table td:last-child {
  text-align: right;
  width: 110px;
}
.bottom-card .hint {
  margin: 6px 0 18px;
  line-height: 1.5;
}
.value-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.value-row >>> .num-step.wide,
.value-row >>> .num-step.lg {
  width: 160px;
  height: 48px;
}
.value-row >>> .num-step.lg input {
  font-size: 22px;
  font-weight: 700;
}
.suffix {
  font-size: 16px;
  color: #8fa0b8;
  font-weight: 500;
}
.row-disabled {
  opacity: 0.42;
  filter: grayscale(0.15);
  transition: opacity 0.2s;
}
</style>
