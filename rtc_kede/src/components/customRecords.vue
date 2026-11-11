<!-- CustomAISummary.vue -->
<template>
  <div class="custom-ai-summary">

    <div class="ai-summary-container" @dblclick="handleDblClickSummary">
      <div
        v-for="item in summaryList"
        :key="`${item.startTime}-${item.speakerId}`"
        class="summary-item"
        :data-starttime="item.startTime"
      >
        <span class="speaker">{{ item.speakerName }}：</span>
        <span v-html="renderHighlightText(item.text, item.startTime)" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue';
import { TUIRoomEngine} from '../components/RoomKit/index.ts';
import {TUIRoomEvents} from "@tencentcloud/tuiroom-engine-js";

type AiSummaryItem = {
  speakerId: string;
  speakerName: string;
  text: string;
  startTime: number;
  endTime: number;
};

// 高亮存储：key=startTime，数组保存文本偏移区间
const highlightStore = ref<Record<string, Array<{ start: number; end: number }>>>({});
const summaryList = ref<AiSummaryItem[]>([]);

const roomEngine = TUIRoomEngine.getInstance();

// ✅ 去掉 export，普通内部函数
async function loadHistorySummary(roomId: string) {
  const res = await roomEngine.getAIMeetingSummary(roomId);
  summaryList.value = res.summaryItems || [];
}

// 监听实时会议增量纪要
roomEngine.on(TUIRoomEvents.onAISummaryReceived, (payload) => {
  if (payload?.summaryItems) {
    summaryList.value.push(...payload.summaryItems);
  }
});

// 双击处理
const handleDblClickSummary = () => {
  const selection = window.getSelection();
  if (!selection || selection.isCollapsed) return;

  const range = selection.getRangeAt(0);
  const textNode = range.startContainer;
  if (textNode.nodeType !== Node.TEXT_NODE) return;

  const startOffset = range.startOffset;
  const endOffset = range.endOffset;

  const itemDom = range.commonAncestorContainer.parentElement?.closest('.summary-item');
  if (!itemDom) return;
  const timeKey = itemDom.dataset.starttime;
  if (!timeKey) return;

  if (!highlightStore.value[timeKey]) {
    highlightStore.value[timeKey] = [];
  }
  highlightStore.value[timeKey].push({ start: startOffset, end: endOffset });

  // 清除浏览器选中
  selection.removeAllRanges();
};

// 根据偏移渲染高亮HTML
const renderHighlightText = (text: string, time: number) => {
  const marks = highlightStore.value[String(time)] || [];
  if (!marks.length) return text;

  let html = '';
  let lastPos = 0;
  const sorted = [...marks].sort((a, b) => a.start - b.start);

  sorted.forEach((mark) => {
    html += text.slice(lastPos, mark.start);
    html += `<span style="background:#ffec3d;">${text.slice(mark.start, mark.end)}</span>`;
    lastPos = mark.end;
  });
  html += text.slice(lastPos);
  return html;
};

// ✅ 向外暴露方法，父组件用 ref 调用
defineExpose({
  loadHistorySummary,
  highlightStore,
  summaryList
});

onUnmounted(() => {
  roomEngine.off(TUIRoomEvents.onAISummaryReceived);
  summaryList.value = [];
  highlightStore.value = {};
});
</script>

<style scoped>
.custom-ai-summary {
  height: 100%;
  overflow-y: auto;
  padding: 12px;
}
.summary-item {
  margin: 8px 0;
  line-height: 1.6;
}
.speaker {
  font-weight: 600;
  margin-right: 4px;
}
.ai-summary-container {
  user-select: text;
}
</style>
