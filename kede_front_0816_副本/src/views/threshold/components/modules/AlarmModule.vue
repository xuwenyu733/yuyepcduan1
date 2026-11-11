<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-bell head-icon alarm-icon" />
      <div>
        <h2>AI 报警分级阈值</h2>
        <p>告警等级 · 逾期时限 · 密集触发 · 多指标联动 · 高发时段</p>
      </div>
    </div>

    <div class="panel">
      <div class="panel-title">报警等级与处理时限</div>
      <div class="level-grid">
        <div
          v-for="item in local.overdue"
          :key="item.level"
          class="level-card"
          :class="item.color"
        >
          <div class="level-name">{{ item.level }}</div>
          <div class="level-desc">{{ item.desc }}</div>
          <div class="lab">超时时限 (分钟)</div>
          <number-stepper
            size="lg"
            :tone="toneOf(item.color)"
            v-model="item.minutes"
            :step="5"
            :min="1"
            @change="emitChange"
          />
        </div>
      </div>
    </div>

    <div class="rule-grid">
      <div class="panel rule-card" :class="{ 'row-disabled': !local.dense.enabled }">
        <div class="panel-title">密集告警判定</div>
        <p class="hint">{{ local.dense.desc }}</p>
        <div class="field">
          <div class="lab">时间窗口 (分钟)</div>
          <number-stepper tone="accent" v-model="local.dense.window" :step="5" :min="1" @change="emitChange" />
        </div>
        <div class="field">
          <div class="lab">触发次数阈值</div>
          <number-stepper tone="accent" v-model="local.dense.count" :step="1" :min="1" @change="emitChange" />
        </div>
        <div class="rule-foot">
          <enable-btn v-model="local.dense.enabled" @input="emitChange" />
        </div>
      </div>

      <div class="panel rule-card" :class="{ 'row-disabled': !local.multi.enabled }">
        <div class="panel-title">多指标同步告警</div>
        <p class="hint">{{ local.multi.desc }}</p>
        <div class="field">
          <div class="lab">同步告警指标数</div>
          <number-stepper tone="danger" v-model="local.multi.value" :step="1" :min="1" @change="emitChange" />
        </div>
        <div class="info-box">
          达到 <b>{{ local.multi.value }}</b> 个指标告警时，自动升级至
          <span class="urgent">紧急</span> 等级
        </div>
        <div class="rule-foot">
          <enable-btn v-model="local.multi.enabled" @input="emitChange" />
        </div>
      </div>

      <div class="panel rule-card" :class="{ 'row-disabled': !local.peakHours.enabled }">
        <div class="panel-title">高发时段判定</div>
        <p class="hint">{{ local.peakHours.desc }}</p>
        <div class="time-row">
          <div class="field">
            <div class="lab">开始 (时)</div>
            <number-stepper tone="accent" v-model="local.peakHours.startHour" :step="1" :min="0" :max="23" @change="emitChange" />
          </div>
          <div class="field">
            <div class="lab">结束 (时)</div>
            <number-stepper tone="accent" v-model="local.peakHours.endHour" :step="1" :min="0" :max="23" @change="emitChange" />
          </div>
        </div>
        <div class="field">
          <div class="lab">时段内告警次数上限</div>
          <number-stepper tone="accent" v-model="local.peakHours.maxCount" :step="1" :min="1" @change="emitChange" />
        </div>
        <div class="rule-foot">
          <enable-btn v-model="local.peakHours.enabled" @input="emitChange" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EnableBtn from '../EnableBtn.vue'

export default {
  name: 'AlarmModule',
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
    toneOf(color) {
      if (color === 'red') return 'danger'
      if (color === 'orange') return 'warn'
      return 'accent'
    },
    normalize(v) {
      const data = JSON.parse(JSON.stringify(v || {}))
      if (!Array.isArray(data.overdue)) data.overdue = []
      data.overdue.forEach(item => {
        if (!item.desc) {
          if (item.level === '紧急') item.desc = '立即处理，超时弹窗提醒'
          else if (item.level === '优先') item.desc = '优先处理，超时升级告警'
          else item.desc = '计划处理，超时记录'
        }
      })
      if (!data.dense) data.dense = {}
      if (data.dense.enabled == null) data.dense.enabled = true
      if (!data.multi) data.multi = {}
      if (data.multi.enabled == null) data.multi.enabled = true
      if (!data.peakHours) data.peakHours = {}
      if (data.peakHours.enabled == null) data.peakHours.enabled = true
      if (data.peakHours.startHour == null) {
        const s = String(data.peakHours.start || '14:00')
        data.peakHours.startHour = Number(s.split(':')[0]) || 14
      }
      if (data.peakHours.endHour == null) {
        const e = String(data.peakHours.end || '16:00')
        data.peakHours.endHour = Number(e.split(':')[0]) || 16
      }
      if (data.peakHours.maxCount == null) data.peakHours.maxCount = 10
      return data
    },
    emitChange() {
      this.$emit('input', JSON.parse(JSON.stringify(this.local)))
    }
  }
}
</script>

<style scoped>
.alarm-icon {
  color: #ef5b6b !important;
  background: rgba(239, 91, 107, 0.14) !important;
}
.level-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}
.level-card {
  border-radius: 10px;
  padding: 14px 16px 16px;
  border: 1px solid;
  background: #0c1422;
}
.level-card.red {
  border-color: rgba(239, 91, 107, 0.45);
  background: rgba(239, 91, 107, 0.06);
}
.level-card.orange {
  border-color: rgba(240, 180, 41, 0.45);
  background: rgba(240, 180, 41, 0.06);
}
.level-card.blue {
  border-color: rgba(64, 158, 255, 0.4);
  background: rgba(64, 158, 255, 0.05);
}
.level-name {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 4px;
}
.level-card.red .level-name { color: #ef5b6b; }
.level-card.orange .level-name { color: #f0b429; }
.level-card.blue .level-name { color: #409eff; }
.level-desc {
  font-size: 12px;
  color: #7d8fab;
  margin-bottom: 14px;
  line-height: 1.4;
}
.lab {
  font-size: 12px;
  color: #7d8fab;
  margin-bottom: 8px;
}
.level-card >>> .num-step {
  width: 100%;
  height: 48px;
}
.level-card >>> .num-step input {
  font-size: 22px;
  font-weight: 700;
}

.rule-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}
.rule-card {
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  min-height: 280px;
}
.rule-card .hint {
  margin: 0 0 14px;
  line-height: 1.5;
  min-height: 40px;
}
.field {
  margin-bottom: 12px;
}
.field >>> .num-step { width: 100%; }
.time-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.info-box {
  margin: 4px 0 12px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(239, 91, 107, 0.1);
  border: 1px solid rgba(239, 91, 107, 0.28);
  font-size: 12px;
  color: #c8d4e6;
  line-height: 1.5;
}
.info-box b { color: #f3f7ff; }
.info-box .urgent {
  color: #ef5b6b;
  font-weight: 700;
}
.rule-foot {
  margin-top: auto;
  padding-top: 8px;
}
.row-disabled {
  opacity: 0.42;
  filter: grayscale(0.15);
  transition: opacity 0.2s;
}
@media (max-width: 1100px) {
  .level-grid,
  .rule-grid { grid-template-columns: 1fr; }
}
</style>
