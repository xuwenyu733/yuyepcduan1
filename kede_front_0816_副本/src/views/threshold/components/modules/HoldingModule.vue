<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-box head-icon" />
      <div>
        <h2>暂养损耗阈值配置</h2>
        <p>风味优化暂养死亡率 · 成品掉秤率</p>
      </div>
    </div>
    <div class="panel">
      <div class="setting-row" v-for="item in local.items" :key="item.id">
        <div>
          <div class="name">{{ item.name }}</div>
          <div class="hint" v-if="item.desc">{{ item.desc }}</div>
        </div>
        <div class="row-right">
          <number-stepper :tone="item.danger ? 'danger' : 'accent'" v-model="item.value" :step="0.1" @change="emitChange" />
          <span class="suffix">{{ item.unit }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HoldingModule',
  props: { value: { type: Object, required: true } },
  data() { return { local: JSON.parse(JSON.stringify(this.value)) } },
  watch: { value: { deep: true, handler(v) { this.local = JSON.parse(JSON.stringify(v)) } } },
  methods: { emitChange() { this.$emit('input', JSON.parse(JSON.stringify(this.local))) } }
}
</script>
