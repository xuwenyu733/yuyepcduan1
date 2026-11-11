<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-cpu head-icon" style="color:#f0b429;background:rgba(240,180,41,.12)" />
      <div>
        <h2>设备运行阈值配置</h2>
        <p>压力 / 液位 / 电压电流 / 运行频次 / 能耗波动 / 停机时长</p>
      </div>
    </div>

    <div class="sec" v-for="sec in sections" :key="sec.key">
      <div class="sec-title">
        <i class="bar" />
        <span>{{ sec.title }}</span>
      </div>
      <div class="card-grid" v-if="(local[sec.key] || []).length">
        <metric-card
          v-for="(m, idx) in (local[sec.key] || [])"
          :key="m.id || (sec.key + '-' + idx)"
          :item="m"
          @change="v => { $set(local[sec.key], idx, v); emitChange() }"
        />
      </div>
      <div v-else class="empty-sec">暂无指标，请点击「恢复默认」或重新保存本模块</div>
    </div>
  </div>
</template>

<script>
import MetricCard from '../MetricCard.vue'

const SECTION_KEYS = ['power', 'pipeline', 'electric', 'levels', 'maintain']

function normalizeDevice(value) {
  const src = value && typeof value === 'object' ? value : {}
  const next = { enabled: src.enabled !== false }
  SECTION_KEYS.forEach(k => {
    next[k] = Array.isArray(src[k]) ? JSON.parse(JSON.stringify(src[k])) : []
  })
  return next
}

export default {
  name: 'DeviceModule',
  components: { MetricCard },
  props: { value: { type: Object, required: true } },
  data() {
    return {
      local: normalizeDevice(this.value),
      sections: [
        { key: 'power', title: '动力设备' },
        { key: 'pipeline', title: '管路与过滤' },
        { key: 'electric', title: '电气参数' },
        { key: 'levels', title: '液位传感' },
        { key: 'maintain', title: '运维预警' }
      ]
    }
  },
  watch: {
    value: {
      deep: true,
      handler(v) { this.local = normalizeDevice(v) }
    }
  },
  methods: {
    emitChange() { this.$emit('input', JSON.parse(JSON.stringify(this.local))) }
  }
}
</script>

<style scoped>
.sec { margin-bottom: 18px; }
.sec-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 650;
  color: #e8f1ff;
}
.sec-title .bar {
  width: 3px;
  height: 14px;
  border-radius: 2px;
  background: #f0b429;
  display: inline-block;
}
.empty-sec {
  padding: 12px 14px;
  margin-bottom: 8px;
  color: #9bb0c8;
  font-size: 13px;
  border: 1px dashed rgba(155, 176, 200, 0.35);
  border-radius: 8px;
}
</style>
