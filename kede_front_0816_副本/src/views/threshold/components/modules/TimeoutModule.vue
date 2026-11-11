<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-time head-icon" />
      <div>
        <h2>任务超时阈值配置</h2>
        <p>紧急 / 优先 / 日常任务逾期判定时间与报警处理时限</p>
      </div>
    </div>
    <div class="panel">
      <div class="panel-title">任务逾期时限</div>
      <div class="setting-row" v-for="item in local.overdue" :key="item.level">
        <div class="name" :class="'level-' + item.color">{{ item.level }}</div>
        <div class="row-right">
          <number-stepper
            :tone="item.color === 'red' ? 'danger' : (item.color === 'orange' ? 'warn' : 'accent')"
            v-model="item.hours"
            :step="0.25"
            :min="0"
            @change="emitChange"
          />
          <span class="suffix">小时</span>
        </div>
      </div>
    </div>
    <div class="panel">
      <div class="panel-title">报警生成任务处理时限</div>
      <div class="setting-row" style="border:none">
        <div>
          <div class="name">报警任务处理上限时间</div>
          <div class="hint">{{ local.alarmTask.desc }}</div>
        </div>
        <div class="row-right">
          <number-stepper tone="danger" v-model="local.alarmTask.value" :step="0.25" :min="0" @change="emitChange" />
          <span class="suffix">{{ local.alarmTask.unit }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TimeoutModule',
  props: { value: { type: Object, required: true } },
  data() { return { local: JSON.parse(JSON.stringify(this.value)) } },
  watch: { value: { deep: true, handler(v) { this.local = JSON.parse(JSON.stringify(v)) } } },
  methods: { emitChange() { this.$emit('input', JSON.parse(JSON.stringify(this.local))) } }
}
</script>
