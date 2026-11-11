<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-s-finance head-icon energy-icon" />
      <div>
        <h2>能耗阈值配置</h2>
        <p>月度总能耗、单类设备占比、单设备环比波动预警配置</p>
      </div>
    </div>
    <div class="panel">
      <div class="panel-title">能耗预警阈值</div>
      <div class="setting-row" v-for="item in local.items" :key="item.id">
        <div>
          <div class="name">{{ item.name }}</div>
          <div class="hint">{{ item.desc }}</div>
        </div>
        <div class="row-right">
          <number-stepper tone="warn" v-model="item.value" :step="1" :min="0" @change="emitChange" />
          <span class="suffix">{{ item.unit }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EnergyModule',
  props: { value: { type: Object, required: true } },
  data() { return { local: JSON.parse(JSON.stringify(this.value)) } },
  watch: { value: { deep: true, handler(v) { this.local = JSON.parse(JSON.stringify(v)) } } },
  methods: { emitChange() { this.$emit('input', JSON.parse(JSON.stringify(this.local))) } }
}
</script>

<style scoped>
.energy-icon {
  color: #f0b429 !important;
  background: rgba(240, 180, 41, 0.14) !important;
  font-size: 18px;
  line-height: 1;
  font-style: normal;
}
.setting-row {
  padding: 16px 0;
}
.setting-row:first-of-type {
  border-top: none;
  padding-top: 4px;
}
.setting-row:last-child {
  padding-bottom: 4px;
}
.name {
  font-size: 14px;
  font-weight: 600;
  color: #e8f1ff;
}
.hint {
  margin-top: 6px;
}
.row-right >>> .num-step {
  width: 100px;
}
.suffix {
  font-size: 14px;
  color: #7d8fab;
  min-width: 16px;
}
</style>
