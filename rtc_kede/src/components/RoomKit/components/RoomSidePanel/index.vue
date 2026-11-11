<template>
  <div class="room-side-panel">
    <div class="header">
      <span class="title">{{ title }}</span>
      <button
        type="button"
        class="close"
        aria-label="关闭"
        title="关闭"
        @click="closeSidePanel"
      >
        <IconClose size="16" />
      </button>
    </div>
    <div class="content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { IconClose } from '@tencentcloud/uikit-base-component-vue3';

defineProps<{
  title: string;
}>();

const emit = defineEmits(['close']);

const closeSidePanel = () => {
  emit('close');
};
</script>

<style lang="scss" scoped>
.room-side-panel {
  background-color: var(--bg-color-operate);
  border-left: 1px solid var(--stroke-color-primary);
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .header {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
    padding: 16px 20px;
    border-bottom: 1px solid var(--stroke-color-primary);
    height: 56px;
    box-sizing: border-box;

    .title {
      font-size: 16px;
      font-weight: 500;
      color: var(--text-color-primary);
    }

    .close {
      position: absolute;
      top: 50%;
      right: 16px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 28px;
      height: 28px;
      padding: 0;
      border: none;
      border-radius: 6px;
      background: transparent;
      cursor: pointer;
      transform: translateY(-50%);
      color: var(--text-color-primary);

      &:hover {
        background: rgba(255, 255, 255, 0.08);
      }
    }
  }

  .content {
    flex: 1;
    min-height: 0;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
}
</style>
