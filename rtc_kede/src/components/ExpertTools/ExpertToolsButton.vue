<template>
  <IconButton
    title="专家工具"
    :is-active="isActive"
    @click-icon="props.togglePanel?.()"
  >
    <IconAITranscription size="24" />
  </IconButton>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { IconAITranscription } from '@tencentcloud/uikit-base-component-vue3'
import IconButton from '@/components/RoomKit/components/base/IconButton.vue'
import { useRoomSidePanel } from '@/components/RoomKit/hooks/useRoomSidePanel'
import { EXPERT_TOOLS_WIDGET_ID } from './constants'

interface Props {
  togglePanel?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  togglePanel: undefined,
})

const { activeWidgetId } = useRoomSidePanel()
const isActive = computed(() => activeWidgetId.value === EXPERT_TOOLS_WIDGET_ID)
</script>
