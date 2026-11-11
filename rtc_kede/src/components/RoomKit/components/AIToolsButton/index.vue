<template>
  <div
    v-click-outside="closeMenu"
    class="ai-tools-button-wrapper"
  >
    <IconButton
      :title="t('AITools.Title')"
      :is-active="showMenu || isSubtitlesVisible || activeWidgetId === BuiltinWidget.AIToolsWidget"
      @click-icon="toggleMenu"
    >
      <IconAIIcon :size="24" />
    </IconButton>
    <transition name="menu-fade">
      <div
        v-show="showMenu"
        class="dropdown-menu"
        @click.stop
      >
        <div
          class="dropdown-item"
          :class="{ disabled: !canToggleSubtitles }"
          :title="subtitleHint"
          @click="handleToggleSubtitles"
        >
          <IconAISubtitles :size="16" />
          <span class="dropdown-item-text">
            {{ isSubtitlesVisible ? t('AITools.SubtitlesClose') : 'AI实时字幕' }}
          </span>
        </div>
        <div
          class="dropdown-item"
          :class="{ disabled: !canOpenAsrRecords }"
          :title="asrListHint"
          @click="handleToggleRealtimeMessageList"
        >
          <IconAITranscription :size="16" />
          <span class="dropdown-item-text">
            {{ activeWidgetId === BuiltinWidget.AIToolsWidget ? '关闭字幕记录' : 'AI字幕记录' }}
          </span>
        </div>
      </div>
    </transition>
  </div>
  <!-- 字幕层已上移到 ConferenceMainView，避免 Teleport 到祖先 #roomPage
       在按钮重挂载时触发 nextSibling/emitsOptions 空引用，拖垮侧栏 -->
</template>

<script lang="ts" setup>
import { computed, nextTick, ref, watch } from 'vue';
import {
  IconAIIcon,
  IconAISubtitles,
  IconAITranscription,
  TUIToast,
  useUIKit,
} from '@tencentcloud/uikit-base-component-vue3';
import {
  RoomParticipantRole,
  useRoomParticipantState,
  useRoomState,
} from 'tuikit-atomicx-vue3/room';
import { BuiltinWidget } from '../../adapter/type';
import { useASRToolsState } from '../../hooks/useASRToolsState';
import { useRoomSidePanel } from '../../hooks/useRoomSidePanel';
import { useSubtitlesState } from '../../hooks/useSubtitlesState';
import IconButton from '../base/IconButton.vue';
import vClickOutside from '../base/vClickOutside';

interface Props {
  togglePanel?: () => void;
}

const props = withDefaults(defineProps<Props>(), {
  togglePanel: undefined,
});

const { t } = useUIKit();
const { activeWidgetId } = useRoomSidePanel();
const { isSubtitlesVisible } = useSubtitlesState();
const { currentRoom } = useRoomState();
const { localParticipant } = useRoomParticipantState();
const showMenu = ref(false);
const isOwner = computed(() => currentRoom.value?.roomOwner?.userId === localParticipant.value?.userId);
const {
  hasStartedASR,
  ensureASRStarted,
} = useASRToolsState();
/** 房主可随时开启；成员仅在房主已开启后可开关本地字幕显示 */
const canToggleSubtitles = computed(() => isOwner.value || hasStartedASR.value || isSubtitlesVisible.value);
/** 房主开启后可看记录；成员同样需房主已开启转写 */
const canOpenAsrRecords = computed(() => isOwner.value || hasStartedASR.value);

const subtitleHint = computed(() => {
  if (isOwner.value) return '房主开启后，会议内其他成员也可打开字幕';
  if (hasStartedASR.value) return '房主已开启，可打开/关闭本地字幕';
  return '请等待房主先开启 AI 实时字幕';
});

const asrListHint = computed(() => {
  if (isOwner.value) return '查看转写记录；双击选中后点上传，写入专家工具-会议记录'
  if (hasStartedASR.value) return '查看转写记录'
  return '请等待房主先开启 AI 实时字幕'
});

function toggleMenu() {
  showMenu.value = !showMenu.value;
}

function closeMenu() {
  showMenu.value = false;
}

async function handleToggleSubtitles() {
  closeMenu();
  await nextTick();
  if (isSubtitlesVisible.value) {
    isSubtitlesVisible.value = false;
    return;
  }
  if (!isOwner.value && !hasStartedASR.value) {
    TUIToast.info({ message: '请等待房主先开启 AI 实时字幕' });
    return;
  }
  if (isOwner.value && !hasStartedASR.value) {
    try {
      await ensureASRStarted();
    } catch (e) {
      return;
    }
  }
  // 等菜单 leave 过渡结束再挂字幕，避免与 Transition 同帧 patch
  await nextTick();
  isSubtitlesVisible.value = true;
}

async function handleToggleRealtimeMessageList() {
  closeMenu();
  await nextTick();
  const opening = activeWidgetId.value !== BuiltinWidget.AIToolsWidget;
  if (opening) {
    if (!isOwner.value && !hasStartedASR.value) {
      TUIToast.info({ message: '请等待房主先开启 AI 实时字幕' });
      return;
    }
    if (isOwner.value && !hasStartedASR.value) {
      try {
        await ensureASRStarted();
      } catch (e) {
        return;
      }
    }
  }
  try {
    props.togglePanel?.();
  } catch (e) {
    console.warn('[AIToolsButton] togglePanel failed', e);
  }
}

function closeASRRelatedUI() {
  isSubtitlesVisible.value = false;
  if (activeWidgetId.value === BuiltinWidget.AIToolsWidget) {
    try {
      props.togglePanel?.();
    } catch (e) {
      /* ignore */
    }
  }
}

watch(
  hasStartedASR,
  (started, previousStarted) => {
    if (previousStarted && !started) {
      closeASRRelatedUI();
    }
  },
);

// 失去房主身份时关闭字幕相关 UI（原先误写成「成为房主」且未导入枚举）
watch(() => localParticipant.value?.role, (role, previousRole) => {
  if (previousRole === RoomParticipantRole.Owner && role !== RoomParticipantRole.Owner) {
    closeASRRelatedUI();
  }
});
</script>

<style lang="scss" scoped>
.ai-tools-button-wrapper {
  position: relative;
  flex-shrink: 0;
}

.dropdown-menu {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  z-index: 1000;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
  white-space: nowrap;
  background: var(--bg-color-operate);
  border: 1px solid var(--stroke-color-primary);
  border-radius: 8px;
  box-shadow: 0 4px 12px var(--uikit-color-black-16);
  transform: translateX(-50%);
  text-align: initial;

  .dropdown-item {
    display: flex;
    flex-direction: row;
    gap: 6px;
    align-items: center;
    padding: 6px 8px;
    color: var(--text-color-primary);
    cursor: pointer;
    border-radius: 6px;

    &:hover:not(.disabled) {
      background: var(--button-color-secondary-hover);
    }

    &.disabled {
      opacity: 0.45;
      cursor: not-allowed;
    }

    .dropdown-item-text {
      font-size: 13px;
      line-height: 20px;
    }
  }
}

.menu-fade-enter-active,
.menu-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.menu-fade-enter-from {
  opacity: 0;
  transform: translateX(-50%) translateY(10px);
}

.menu-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(10px);
}

.menu-fade-enter-to,
.menu-fade-leave-from {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
}
</style>
