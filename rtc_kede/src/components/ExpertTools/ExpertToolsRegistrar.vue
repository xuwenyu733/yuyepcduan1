<script setup lang="ts">
import { onUnmounted } from 'vue'
import { conference } from '@/components/RoomKit/adapter/conference'
import ExpertToolsButton from './ExpertToolsButton.vue'
import ExpertToolsPanel from './ExpertToolsPanel.vue'
import { EXPERT_TOOLS_WIDGET_ID } from './constants'

const unregister = conference.registerWidget({
  id: EXPERT_TOOLS_WIDGET_ID,
  zone: { pc: 'bottom-center' },
  order: 8,
  component: ExpertToolsButton,
  panel: {
    title: '专家工具',
    component: ExpertToolsPanel,
    keepAlive: true,
  },
})

onUnmounted(() => unregister())
</script>

<template>
  <!-- Registrar: registration-only -->
</template>
