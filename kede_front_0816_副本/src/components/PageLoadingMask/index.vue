<template>
  <div class="page-loading-mask" v-if="visible" :style="{ zIndex: zIndex }">
    <div class="spin"></div>
    <span>{{ text }}</span>
  </div>
</template>

<script>
/**
 * 与任务与计划中心一致的全页「正在加载…」遮罩。
 * 父容器需 position: relative（或 absolute/fixed）。
 */
export default {
  name: 'PageLoadingMask',
  props: {
    visible: { type: Boolean, default: false },
    text: { type: String, default: '数据正在加载中…' },
    zIndex: { type: [Number, String], default: 80 }
  }
}
</script>

<style scoped>
.page-loading-mask {
  position: absolute;
  inset: 0;
  z-index: 80;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(6, 18, 36, 0.62);
  color: #d7e8ff;
  font-size: 14px;
  letter-spacing: 0.04em;
  pointer-events: all;
}
.page-loading-mask .spin {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(94, 200, 255, 0.25);
  border-top-color: #5ec8ff;
  border-radius: 50%;
  animation: pageLoadSpin 0.8s linear infinite;
  margin-bottom: 10px;
}
@keyframes pageLoadSpin {
  to { transform: rotate(360deg); }
}
</style>
