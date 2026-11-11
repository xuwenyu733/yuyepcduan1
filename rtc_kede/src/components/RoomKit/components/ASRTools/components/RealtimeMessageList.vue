<template>
  <div
    ref="conversationContainerRef"
    class="conversation"
    @scroll="handleScroll"
  >
    <div class="multichoose_bar">
      已选择 {{chosenCount}} 条纪要
      <button @click="postChosenMessage">上传</button>
    </div>
    <div
      v-for="group in transcribedMessageList"
      :key="`${group.sender}-${group.startMsTs}`"
      class="conversation-group"
    >
      <div class="title">
        <span class="speaker">{{ getDisplayName(group.sender) }}</span>
        <span class="timestamp">
          {{ formatTimestampToTime(group.startMsTs) }}
        </span>
      </div>
      <div
        v-for="(message, messageIndex) in group.messages"
        :key="message.segmentId || messageIndex"
        class="content"
        @dblclick="infoDbClick(message)"
        @touchend="infoDbClick(message)"
        :class="message.dbc? 'dbc' : ''"
      >
        <div
          v-for="(line, lineIndex) in getDisplayLines(message)"
          :key="`${message.segmentId || messageIndex}-${lineIndex}`"
          class="content-line"
          :class="{ secondary: lineIndex > 0 }"
        >
          {{ line }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick, watch } from 'vue';
import {useAITranscriberState, useRoomState} from 'tuikit-atomicx-vue3/room';
import { getDisplayName, getMessageDisplayLines, hasDisplayableText } from '../utils/display';
import { formatTimestampToTime } from '../utils/formatTimestampToTime';
import type { SubtitleDisplayMode, TranscriberMessage } from 'tuikit-atomicx-vue3/room';
import { saveSelectedAsrText, notifyParentChat } from '@/utils/kedeMeetingBridge';
const {
  currentRoom
} = useRoomState();
let chosenCount = 0
interface MessageGroup {
  sender: string;
  startMsTs: number;
  messages: TranscriberMessage[];
}

const conversationContainerRef = ref<HTMLElement>();
const isUserScrolling = ref(false);
const timeInterval = 60 * 1000; // 60 seconds

const props = defineProps<{
  targetLanguage?: string;
  displayMode?: SubtitleDisplayMode;
}>();

const { realtimeMessageList } = useAITranscriberState();
const effectiveTargetLanguage = computed(() => props.targetLanguage || '');
const effectiveDisplayMode = computed(() => props.displayMode || 'translation');

const isAtBottom = (): boolean => {
  if (!conversationContainerRef.value) {
    return false;
  }
  const { scrollTop, scrollHeight, clientHeight } = conversationContainerRef.value;

  return Math.ceil(scrollTop + clientHeight) >= scrollHeight - 50;
};

const scrollToBottom = () => {
  if (!conversationContainerRef.value || isUserScrolling.value) {
    return;
  }
  requestAnimationFrame(() => {
    if (conversationContainerRef.value) {
      conversationContainerRef.value.scrollTop = conversationContainerRef.value.scrollHeight;
    }
  });
};

onMounted(() => {
  scrollToBottom();
});

const handleScroll = () => {
  if (conversationContainerRef.value) {
    isUserScrolling.value = !isAtBottom();
  }
};

const transcribedMessageList = computed((): MessageGroup[] => {
  const completedMessages = [...realtimeMessageList.value
    .filter(msg => hasDisplayableText(msg.sourceText) && typeof msg.timestamp === 'number' && Number.isFinite(msg.timestamp)),
  ].sort((a, b) => (a.timestamp ?? 0) - (b.timestamp ?? 0));

  if (completedMessages.length === 0) {
    return [];
  }

  // Group messages by speaker and time interval
  const aggregatedMessageList: MessageGroup[] = [];
  let currentGroup: MessageGroup | null = null;

  completedMessages.forEach((message) => {
    const speakerId = message.speakerUserId || 'unknown';
    const msgTs = message.timestamp ?? 0;

    if (
      !currentGroup
      || speakerId !== currentGroup.sender
      || msgTs - currentGroup.startMsTs > timeInterval
    ) {
      // Create a new group
      currentGroup = {
        sender: speakerId,
        startMsTs: msgTs,
        messages: [message],
      };
      aggregatedMessageList.push(currentGroup);
    } else {
      // Add to current group
      currentGroup.messages.push(message);
    }
  });

  return aggregatedMessageList;
});

const getDisplayLines = (message: TranscriberMessage) => getMessageDisplayLines(
  message,
  effectiveTargetLanguage.value,
  effectiveDisplayMode.value,
);
function postChosenMessage() {
  const fullList = transcribedMessageList.value || [];
  let messagestr = '';
  fullList.forEach((group) => {
    if (!group.messages.some((v) => v.dbc === true)) return;
    const name = getDisplayName(group.sender);
    messagestr += `${name}：\n`;
    group.messages.forEach((v) => {
      if (v.dbc !== true) return;
      getDisplayLines(v).forEach((line, lineIndex) => {
        if (lineIndex % 2 === 0) messagestr += `${line}\n`;
      });
    });
  });
  const text = messagestr.trim();
  if (!text) return;
  saveSelectedAsrText(text);
  notifyParentChat(currentRoom.value?.roomId || '', text);
}

/** 双击：选中该条，并立刻作为会议记录传递（同时支持多选后点上传） */
function infoDbClick(item: TranscriberMessage & { dbc?: boolean }) {
  if (!item.dbc) {
    item.dbc = true;
    chosenCount++;
    const lines = getDisplayLines(item).filter((_, i) => i % 2 === 0);
    const text = lines.join('\n').trim();
    if (text) {
      const name = getDisplayName(item.speakerUserId || '');
      const payload = name ? `${name}：\n${text}` : text;
      saveSelectedAsrText(payload);
      notifyParentChat(currentRoom.value?.roomId || '', payload);
    }
  } else {
    item.dbc = false;
    chosenCount--;
  }
}
// let lastTap = 0;
// const handleDoubleTap = (event, item) => {
//   const currentTime = new Date().getTime();
//   const tapDelay = currentTime - lastTap;
//
//   // A standard double-tap window is typically between 0 and 300ms
//   if (tapDelay < 300 && tapDelay > 0) {
//     event.preventDefault(); // Stop native zoom behaviors
//     infoDbClick(item)
//   }
//
//   lastTap = currentTime;
// };
watch(() => realtimeMessageList.value.length, () => {
  nextTick(() => {
    if (!isUserScrolling.value) {
      scrollToBottom();
    }
  });
});

</script>

<style scoped lang="scss">
.dbc{
  background-color: darksalmon !important;
}
.conversation {
  height: 100%;
  padding: 20px;
  overflow-y: auto;
  text-align: initial;

  :deep(::-webkit-scrollbar-track) {
    background: transparent;
    margin: 16px 0;
  }

  :deep(::-webkit-scrollbar) {
    width: 6px;
  }

  :deep(::-webkit-scrollbar-thumb) {
    border-radius: 3px;
    background-color: var(--stroke-color-secondary);
  }
}

.conversation-group {
  margin-bottom: 20px;
}

.title {
  display: flex;
  gap: 10px;
  align-items: center;
  font-size: 14px;
  font-weight: 400;
  line-height: 22px;
  text-align: left;
  color: var(--text-color-secondary);

  .speaker {
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.content {
  padding: 8px;
  margin-top: 5px;
  font-size: 14px;
  font-weight: 400;
  line-height: 22px;
  text-align: left;
  border-radius: 8px;
  background-color: var(--bg-color-bubble-reciprocal);
  color: var(--text-color-primary);
  cursor: pointer;
}

.content-line {
  white-space: pre-wrap;
  word-break: break-word;
  color: var(--text-color-primary);

  &.secondary {
    margin-top: 4px;
    color: var(--text-color-secondary);
    display: none;
  }
}
</style>
