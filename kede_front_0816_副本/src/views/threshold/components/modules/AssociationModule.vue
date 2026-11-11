<template>
  <div class="module">
    <div class="module-head">
      <i class="el-icon-share head-icon" />
      <div>
        <h2>数据关联逻辑配置</h2>
        <p>跨维度参数联动与自动响应规则，支持条件触发、延迟执行</p>
      </div>
    </div>
    <div class="panel">
      <div class="panel-title">
        <span>{{ enabledCount }}/{{ local.rules.length }} 条规则已启用</span>
        <el-button type="text" class="add-btn" icon="el-icon-plus" @click="addRule">添加规则</el-button>
      </div>

      <div
        v-for="(rule, idx) in local.rules"
        :key="rule.id"
        class="rule-row"
        :class="{ disabled: !rule.enabled }"
      >
        <div class="field">
          <label>触发条件</label>
          <el-select v-model="rule.trigger" filterable placeholder="选择触发条件" @change="emitChange">
            <el-option v-for="t in local.triggers" :key="t" :label="t" :value="t" />
          </el-select>
        </div>
        <div class="arrow">→</div>
        <div class="field action">
          <label>执行动作</label>
          <el-select v-model="rule.action" filterable placeholder="选择执行动作" @change="emitChange">
            <el-option v-for="a in local.actions" :key="a" :label="a" :value="a" />
          </el-select>
        </div>
        <div class="field delay">
          <label>延迟(MIN)</label>
          <number-stepper tone="accent" v-model="rule.delay" :step="1" :min="0" @change="emitChange" />
        </div>
        <div class="ops">
          <enable-btn v-model="rule.enabled" @input="emitChange" />
          <button type="button" class="del" @click="removeRule(idx)"><i class="el-icon-delete" /></button>
        </div>
      </div>
      <div class="hint" style="margin-top:10px">延迟为 0 表示立即执行</div>
    </div>
  </div>
</template>

<script>
import EnableBtn from '../EnableBtn.vue'
export default {
  name: 'AssociationModule',
  components: { EnableBtn },
  props: { value: { type: Object, required: true } },
  data() { return { local: JSON.parse(JSON.stringify(this.value)) } },
  computed: {
    enabledCount() { return this.local.rules.filter(r => r.enabled).length }
  },
  watch: { value: { deep: true, handler(v) { this.local = JSON.parse(JSON.stringify(v)) } } },
  methods: {
    emitChange() { this.$emit('input', JSON.parse(JSON.stringify(this.local))) },
    addRule() {
      this.local.rules.push({
        id: 'r' + Date.now(),
        trigger: this.local.triggers[0],
        action: this.local.actions[0],
        delay: 0,
        enabled: true
      })
      this.emitChange()
    },
    removeRule(idx) {
      this.local.rules.splice(idx, 1)
      this.emitChange()
    }
  }
}
</script>

<style scoped>
.add-btn { color: var(--accent) !important; font-weight: 600; }
.rule-row {
  display: grid;
  grid-template-columns: 1.2fr auto 1.4fr 120px auto;
  gap: 10px;
  align-items: end;
  padding: 12px;
  margin-bottom: 10px;
  border: 1px solid var(--border);
  border-radius: 10px;
  background: #132030;
}
.rule-row.disabled { opacity: 0.42; filter: grayscale(0.15); }
.field label {
  display: block;
  font-size: 12px;
  color: #7f91ab;
  margin-bottom: 6px;
}
.arrow {
  align-self: center;
  color: var(--accent);
  padding-bottom: 4px;
  font-size: 16px;
}
.ops {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  padding-bottom: 2px;
}
.del {
  border: none;
  background: transparent;
  color: #7f91ab;
  cursor: pointer;
  font-size: 16px;
}
.del:hover { color: #e85d6a; }
.rule-row >>> .el-select {
  width: 100%;
}
@media (max-width: 1100px) {
  .rule-row {
    grid-template-columns: 1fr;
  }
  .arrow { display: none; }
}
</style>
