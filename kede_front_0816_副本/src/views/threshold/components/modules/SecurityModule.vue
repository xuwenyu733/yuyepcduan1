<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-user head-icon shield-icon" />
      <div>
        <h2>人员安防阈值配置</h2>
        <p>巡检频次 · 到岗率 · 工时上限 · 非工时入侵预警</p>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">人员管理阈值</div>
      <div class="setting-row" v-for="item in local.items" :key="item.id">
        <div>
          <div class="name">{{ item.name }}</div>
          <div v-if="item.desc" class="hint">{{ item.desc }}</div>
        </div>
        <div class="row-right">
          <number-stepper tone="accent" v-model="item.value" :step="stepOf(item)" :min="0" @change="emitChange" />
          <span class="suffix">{{ item.unit }}</span>
        </div>
      </div>
    </div>

    <div class="panel intrusion-panel" :class="{ 'row-disabled': !local.intrusion.enabled }">
      <div class="setting-row intrusion-row">
        <div>
          <div class="name">非工作时段人员入侵告警</div>
          <div class="hint">{{ local.intrusion.desc }}</div>
        </div>
        <enable-btn v-model="local.intrusion.enabled" @input="emitChange" />
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from '../EnableBtn.vue'

export default {
  name: 'SecurityModule',
  components: { EnableBtn },
  props: { value: { type: Object, required: true } },
  data() { return { local: JSON.parse(JSON.stringify(this.value)) } },
  watch: { value: { deep: true, handler(v) { this.local = JSON.parse(JSON.stringify(v)) } } },
  methods: {
    stepOf(item) {
      if (item.id === 'response') return 5
      if (item.id === 'workHours') return 0.5
      return 1
    },
    emitChange() { this.$emit('input', JSON.parse(JSON.stringify(this.local))) }
  }
}
</script>

<style scoped>
.shield-icon {
  color: #8b7cf6 !important;
  background: rgba(139, 124, 246, 0.14) !important;
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
  font-size: 13px;
  color: #7d8fab;
  min-width: 36px;
}
.intrusion-panel {
  margin-bottom: 0;
}
.intrusion-row {
  border-top: none !important;
  padding: 4px 0 !important;
  align-items: center;
}
.row-disabled {
  opacity: 0.42;
  filter: grayscale(0.15);
  transition: opacity 0.2s;
}
</style>
