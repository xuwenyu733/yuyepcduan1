<template>
  <button
    type="button"
    class="enable-btn"
    :class="{ on: value, off: !value }"
    @click="$emit('input', !value)"
  >
    <i class="el-icon-switch-button" />
    {{ value ? '启用' : '禁用' }}
  </button>
</template>

<script>
export default {
  name: 'EnableBtn',
  props: {
    value: { type: Boolean, default: true }
  }
}
</script>

<style scoped>
.enable-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 26px;
  padding: 0 10px;
  border-radius: 13px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  line-height: 1;
}
.enable-btn.on {
  color: #409eff;
  border: 1px solid #409eff;
  background: transparent;
  font-weight: 600;
}
.enable-btn.off {
  color: #8fa0b8;
  border: 1px solid #4a5a72;
  background: rgba(58, 74, 99, 0.25);
}
.metric-card.disabled .enable-btn,
.grade-card.disabled .enable-btn,
.row-disabled .enable-btn {
  opacity: 1 !important;
  filter: none !important;
}
.enable-btn:hover {
  filter: brightness(1.08);
}
</style>
