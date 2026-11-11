<template>
  <div
    id="roomPage"
    ref="roomPageRef"
    class="room-page kede-room-page"
  >
    <div class="room-shell">
    <!-- 用模板遮罩代替 v-tui-loading：指令往 #roomPage 插 DOM 会破坏 Teleport 锚点，触发 nextSibling/emitsOptions 空引用 -->
    <LoadingOverlay v-if="isJoiningRoom" class="kede-joining-overlay" />
    <div class="room-container" :class="{ 'has-side-panel': activeWidgetId }">
      <header :class="['header', { 'header-chrome-hidden': !showToolbar } ]">
        <div class="header-left">
          <CustomWidgetRenderer zone="top-left" platform="pc">
            <ThemeRegistrar v-if="conference.getWidgetVisible(BuiltinWidget.ThemeWidget)" />
            <LayoutRegistrar
              v-if="conference.getWidgetVisible(BuiltinWidget.LayoutWidget) && notWebinar"
              :layout="participantViewLayout"
              @update:layout="handleLayoutUpdate"
            />
            <LocalNetworkInfoRegistrar v-if="conference.getWidgetVisible(BuiltinWidget.LocalNetworkInfoWidget)" />
          </CustomWidgetRenderer>
        </div>
        <div class="header-center">
          <CustomWidgetRenderer zone="top-center" platform="pc">
            <CurrentRoomInfoRegistrar v-if="conference.getWidgetVisible(BuiltinWidget.CurrentRoomInfoWidget)" />
          </CustomWidgetRenderer>
        </div>
        <div class="header-right">
          <CustomWidgetRenderer zone="top-right" platform="pc" />
        </div>
      </header>

      <main class="room-main">
        <div
          v-if="conference.getWidgetVisible(BuiltinWidget.RecordingWidget)"
          class="recording-status-anchor"
          :class="{ 'toolbar-visible': showToolbar }"
        >
          <CloudRecordingStatus />
        </div>
        <TUIWatermark
          v-if="watermarkEnabled"
          :font="watermarkFont"
          :content="watermarkContent"
        >
          <RoomLayoutView
            :layout-template="participantViewLayout"
            @update:layout-template="handleLayoutUpdate"
          >
            <template v-if="$slots['participantViewUI']" #participantViewUI="slotProps">
              <slot name="participantViewUI" v-bind="slotProps" />
            </template>
          </RoomLayoutView>
        </TUIWatermark>
        <RoomLayoutView
          v-else
          :layout-template="participantViewLayout"
          @update:layout-template="handleLayoutUpdate"
        >
          <template v-if="$slots['participantViewUI']" #participantViewUI="slotProps">
            <slot name="participantViewUI" v-bind="slotProps" />
          </template>
        </RoomLayoutView>
      </main>

      <!-- 仅注册侧栏面板 -->
      <div class="kede-registrar-slot" aria-hidden="true">
        <MemberRegistrar v-if="conference.getWidgetVisible(BuiltinWidget.MemberWidget)" />
        <AIToolsRegistrar v-if="conference.getWidgetVisible(BuiltinWidget.AIToolsWidget) && notWebinar" />
        <ExpertToolsRegistrar />
      </div>

      <!-- 左右侧控制条：避开底部裁切，落在视频两侧黑边区域 -->
      <aside
        class="kede-side-rail kede-side-rail--left kede-control-bar"
        data-kede-bar="1"
        :class="{ 'kede-bar-hidden': !showToolbar }"
      >
        <MicButton v-if="!isWebinar || isParticipant || isOwner" />
        <CameraButton v-if="ownerOrNotWebinar" />
        <LeaveRoomButton
          @leave="handleLeaveRoom"
          @end="handleEndRoom"
        />
      </aside>
      <aside
        class="kede-side-rail kede-side-rail--right kede-control-bar"
        data-kede-bar="1"
        :class="{ 'kede-bar-hidden': !showToolbar }"
      >
        <ScreenShareButton v-if="ownerOrNotWebinar" />
        <CloudRecordingButton v-if="!isWebinar && (isOwner || isAdmin)" />
        <ParticipantButton :toggle-panel="() => toggleWidgetPanel(BuiltinWidget.MemberWidget)" />
        <AIToolsButton :toggle-panel="() => toggleWidgetPanel(BuiltinWidget.AIToolsWidget)" />
        <ExpertToolsButton :toggle-panel="() => toggleWidgetPanel(EXPERT_TOOLS_WIDGET_ID)" />
      </aside>

      <PasswordDialog
        v-model="roomPasswordVisible"
        :room-id="joiningRoomId"
        @cancel="handlePasswordCancel"
        @error="handleJoinRoomError"
      />
    </div>
    <RoomSidePanel
      class="side-panel"
      :class="{ 'side-panel-visible': activeWidgetId }"
      :title="sidePanelTitle"
      @close="closePanel"
    >
      <template v-for="widget in panelWidgets" :key="widget.id">
        <component
          :is="widget.panel!.component"
          v-if="widget.panel?.component && (widget.panel?.keepAlive || activeWidgetId === widget.id)"
          v-show="!widget.panel?.keepAlive || activeWidgetId === widget.id"
          v-bind="getPanelProps(widget)"
        />
      </template>
    </RoomSidePanel>
    <!-- 字幕挂在稳定的房间页上；用 v-show 避免反复挂载触发 Vue patch 异常 -->
    <div v-show="isSubtitlesVisible" class="ai-subtitle-pc">
      <Subtitle
        :target-language="targetLanguage"
        :display-mode="subtitleDisplayMode"
      >
        <template #actions>
          <IconSettings
            class="subtitle-settings-button"
            :size="16"
            @click="isSettingsDialogVisible = true"
          />
        </template>
      </Subtitle>
    </div>
    <AISettingsDialog
      v-if="isSettingsDialogVisible"
      :is-owner="isOwner"
      @close="isSettingsDialogVisible = false"
    />
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, computed } from 'vue';
import type { Ref } from 'vue';
import {
  useUIKit,
  TUIWatermark,
  IconSettings,
} from '@tencentcloud/uikit-base-component-vue3';
import {
  useLoginState,
  useRoomState,
  useRoomParticipantState,
  RoomLayoutTemplate,
  RoomEvent,
  RoomParticipantEvent,
  KickedOutOfRoomReason,
  RoomType,
  RoomParticipantRole,
} from 'tuikit-atomicx-vue3/room';
import { conference } from '../../adapter/conference';
import { RoomEvent as ConferenceRoomEvent, BuiltinWidget } from '../../adapter/type';
import {
  RoomLayoutView,
  PasswordDialog,
  RoomSidePanel,
  CustomWidgetRenderer,
  LoadingOverlay,
  // Registrar components (PC)
  CloudRecordingStatus,
  ThemeRegistrar,
  LayoutRegistrar,
  LocalNetworkInfoRegistrar,
  CurrentRoomInfoRegistrar,
  LanguageRegistrar,
  LoginUserInfoRegistrar,
  MicRegistrar,
  CameraRegistrar,
  ScreenShareRegistrar,
  InviteRegistrar,
  ChatRegistrar,
  MemberRegistrar,
  VirtualBackgroundRegistrar,
  BasicBeautyRegistrar,
  AIToolsRegistrar,
  SettingsRegistrar,
  LeaveRoomRegistrar,
  BarrageRegistrar,
  RaiseHandsRegistrar,
  RaiseHandsListRegistrar,
} from '../../components';
import MicButton from '../../components/MicButton/index.vue';
import CameraButton from '../../components/CameraButton/index.vue';
import LeaveRoomButton from '../../components/LeaveRoomButton/index.vue';
import ScreenShareButton from '../../components/ScreenShareButton/index.vue';
import CloudRecordingButton from '../../components/CloudRecording/CloudRecordingButton.vue';
import ParticipantButton from '../../components/ParticipantButton/index.vue';
import AIToolsButton from '../../components/AIToolsButton/index.vue';
import AISettingsDialog from '../../components/AIToolsButton/AISettingsDialog.vue';
import { Subtitle } from '../../components/ASRTools';
import ExpertToolsRegistrar from '@/components/ExpertTools/ExpertToolsRegistrar.vue';
import ExpertToolsButton from '@/components/ExpertTools/ExpertToolsButton.vue';
import { EXPERT_TOOLS_WIDGET_ID } from '@/components/ExpertTools/constants';
import useCustomizedAutoPlayDialog from '../../hooks/useCustomizedAutoPlayDialog';
import useRoomLifeCycle from '../../hooks/useRoomLifeCycle';
import { useRoomSidePanel } from '../../hooks/useRoomSidePanel';
import { useRoomTips } from '../../hooks/useRoomTips';
import { useRoomToolbar } from '../../hooks/useRoomToolbar';
import { useSubtitlesState } from '../../hooks/useSubtitlesState';
import { useASRToolsState } from '../../hooks/useASRToolsState';
import { eventCenter } from '../../utils/eventCenter';
import type { WidgetConfig } from '../../adapter/type';
import { notifyParentJoin, saveMeetingMeta } from '@/utils/kedeMeetingBridge';
import { useExpertMeetingState } from '@/composables/useExpertMeetingState';
import { useParentMeetingCommands } from '@/composables/useParentMeetingCommands';

const { t } = useUIKit();
const roomPageRef = ref<HTMLElement | null>(null);
const { showToolbar } = useRoomToolbar(roomPageRef);
const { activeWidgetId, sidePanelTitle, panelWidgets, closePanel, toggleWidgetPanel } = useRoomSidePanel();
const { isSubtitlesVisible, resetSubtitlesState } = useSubtitlesState();
const { targetLanguage, subtitleDisplayMode, hasStartedASR, stopASR } = useASRToolsState();
const isSettingsDialogVisible = ref(false);
// 提前绑定 ASR→会议记录 监听，面板关闭时仍可接收双击上传
useExpertMeetingState();
useParentMeetingCommands();

watch(
  () => activeWidgetId.value,
  (id) => {
    if (id) showToolbar.value = true;
  },
);

/** Resolve panel props (supports both static object and dynamic function) */
const getPanelProps = (widget: WidgetConfig) => {
  const rawProps = widget.panel?.props;
  return typeof rawProps === 'function' ? rawProps() : (rawProps ?? {});
};

useCustomizedAutoPlayDialog();
useRoomTips();

const { loginUserInfo } = useLoginState();
const {
  currentRoom,
  subscribeEvent: subscribeRoomEvent,
  unsubscribeEvent: unsubscribeRoomEvent,
} = useRoomState();
const {
  localParticipant,
  participantList,
  audienceListCursor,
  participantListCursor,
  getParticipantList,
  getAudienceList,
  subscribeEvent: subscribeRoomParticipantEvent,
  unsubscribeEvent: unsubscribeRoomParticipantEvent,
} = useRoomParticipantState();
const {
  isJoiningRoom,
  joiningRoomId,
  roomPasswordVisible,
  handleJoinRoomError,
} = useRoomLifeCycle();

const isWebinar = computed(() => currentRoom.value?.roomType === RoomType.Webinar);
const isOwner = computed(() => localParticipant.value?.role === RoomParticipantRole.Owner);
const isAdmin = computed(() => localParticipant.value?.role === RoomParticipantRole.Admin);
const isGeneralUser = computed(() => localParticipant.value?.role === RoomParticipantRole.GeneralUser);
const isParticipant = computed(() => participantList.value.some(participant => participant.userId === localParticipant.value?.userId));
const isAudience = computed(() => !participantList.value.some(participant => participant.userId === localParticipant.value?.userId));
const participantViewLayout: Ref<RoomLayoutTemplate | undefined> = ref(undefined);
function handleLayoutUpdate(layout: RoomLayoutTemplate) {
  participantViewLayout.value = layout;
}

// Cache roomInfo before leave/dismiss since SDK may clear currentRoom after the operation
let cachedRoomInfo: any = null;
watch(() => currentRoom.value, (newRoom) => {
  if (newRoom?.roomId) {
    cachedRoomInfo = { ...newRoom };
  }
}, { immediate: true, deep: true });

const handlePasswordCancel = () => {
  eventCenter.emit(ConferenceRoomEvent.ROOM_ERROR);
};
const cleanupMeetingUi = async () => {
  isSettingsDialogVisible.value = false;
  resetSubtitlesState();
  closePanel();
  if (hasStartedASR.value) {
    try {
      await stopASR({ suppressError: true, resetState: true });
    } catch {
      /* ignore */
    }
  }
};

const handleLeaveRoom = () => {
  void cleanupMeetingUi();
  eventCenter.emit(ConferenceRoomEvent.ROOM_LEAVE, { roomInfo: cachedRoomInfo || currentRoom.value });
};
const handleEndRoom = () => {
  void cleanupMeetingUi();
  eventCenter.emit(ConferenceRoomEvent.ROOM_DISMISS, { roomInfo: cachedRoomInfo || currentRoom.value });
};

const notWebinar = computed(() => !isWebinar.value);
const ownerOrNotWebinar = computed(() => !isWebinar.value || isOwner.value);
const participantOrNotWebinar = computed(() => !isWebinar.value || isParticipant.value);

const watermarkConfig = computed(() => conference.getFeatureConfig('watermark'));
const watermarkEnabled = computed(() => watermarkConfig.value?.enable !== false);
const watermarkContent = computed(() => {
  if (watermarkConfig.value?.content) return watermarkConfig.value.content;
  // 只用水印昵称，禁止把 userId 打到画面上（避免「研发管理员 1」）
  const nick = String(loginUserInfo.value?.userName || '').trim();
  if (nick && !/^\d+$/.test(nick)) return [nick];
  return ['科德专家会议'];
});
const watermarkFont = computed(() => watermarkConfig.value?.font ?? { fontSize: 16 });

watch(() => currentRoom.value?.roomType, (newRoomType, oldRoomType) => {
  if (oldRoomType !== undefined && oldRoomType === newRoomType) {
    return;
  }
  if (newRoomType === RoomType.Webinar) {
    participantViewLayout.value = undefined;
  } else {
    const configuredLayout = conference.getFeatureConfig('layoutTemplate');
    participantViewLayout.value = configuredLayout ?? RoomLayoutTemplate.GridLayout;
  }
}, { immediate: true });

watch(() => conference.getFeatureConfig('layoutTemplate'), (newVal) => {
  if (currentRoom.value?.roomType !== RoomType.Webinar) {
    participantViewLayout.value = newVal ?? RoomLayoutTemplate.GridLayout;
  }
});

watch(
  () => currentRoom.value?.roomId,
  async (roomId, oldRoomId) => {
    if (!oldRoomId && roomId) {
      await getParticipantList({ cursor: participantListCursor.value });
      await getAudienceList({ cursor: audienceListCursor.value });
      saveMeetingMeta({ meetingNum: roomId });
      notifyParentJoin(roomId);
    }
  },
  { immediate: true },
);

const onRoomEnded = () => {
  eventCenter.emit(ConferenceRoomEvent.ROOM_DISMISS, { roomInfo: cachedRoomInfo || currentRoom.value });
};

const onKickedFromRoom = (eventInfo: {
  reason: KickedOutOfRoomReason;
  message: string;
}) => {
  eventCenter.emit(ConferenceRoomEvent.KICKED_OUT, eventInfo);
};

onMounted(() => {
  subscribeRoomEvent(RoomEvent.onRoomEnded, onRoomEnded);
  subscribeRoomParticipantEvent(
    RoomParticipantEvent.onKickedFromRoom,
    onKickedFromRoom,
  );
  // 加入通知改由 roomId watch → notifyParentJoin，避免挂载时 roomId 为空触发父页报错
});

onUnmounted(() => {
  unsubscribeRoomEvent(RoomEvent.onRoomEnded, onRoomEnded);
  unsubscribeRoomParticipantEvent(
    RoomParticipantEvent.onKickedFromRoom,
    onKickedFromRoom,
  );
  void cleanupMeetingUi();
});
</script>

<style lang="scss" scoped>
.room-page {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: row;
  width: 100%;
  height: 100%;
  max-height: 100%;
  overflow: hidden;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.kede-room-page {
  position: absolute;
  inset: 0;
  height: 100%;
  max-height: 100%;
}

.room-shell {
  position: relative;
  display: flex;
  flex-direction: row;
  width: 100%;
  height: 100%;
  max-height: 100%;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
  background: #071018;
}

.kede-joining-overlay {
  z-index: 2147483002;
  background: var(--bg-color-topbar, #071018);
}

.room-container {
  position: absolute;
  inset: 0;
  display: block;
  min-width: 0;
  min-height: 0;
  width: auto;
  height: auto;
  overflow: hidden;

  &.has-side-panel {
    right: 0;
  }
}

.header {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 40;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 56px;
  padding: 16px 20px;
  background-color: var(--bg-color-operate);
  border-bottom: 1px solid var(--stroke-color-primary);
  box-shadow: 0 1px 0 var(--uikit-color-black-8);
  transition: opacity 0.2s ease, visibility 0.2s ease;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 56px;
  padding: 16px 20px;
  background-color: var(--bg-color-operate);
  border-bottom: 1px solid var(--stroke-color-primary);
  box-shadow: 0 1px 0 var(--uikit-color-black-8);
  transition: opacity 0.2s ease, visibility 0.2s ease;

  &-left {
    display: flex;
    flex: 1;
    gap: 12px;
    justify-content: flex-start;
    align-items: center;
  }

  &-center {
    flex: 1;
    height: 100%;
    text-align: center;
  }

  &-right {
    display: flex;
    flex: 1;
    gap: 16px;
    align-items: center;
    justify-content: flex-end;
  }
}

.header-chrome-hidden {
  visibility: hidden;
  pointer-events: none;
  opacity: 0;
}

.recording-status-anchor {
  position: absolute;
  top: 16px;
  left: 16px;
  transform: translateY(0);
  transition: transform 0.2s ease;

  &.toolbar-visible {
    transform: translateY(56px);
  }
  z-index: 2;
}

.room-main {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  width: 100%;
  height: auto;
  min-height: 0;
  overflow: hidden;
  background-color: var(--bg-color-topbar);
  user-select: none;

  /* 只拉满水印根节点，不要强改 participant 内部尺寸（会把画面挤没） */
  :deep(> .tui-watermark),
  :deep(> .tui-watermark > *) {
    width: 100% !important;
    height: 100% !important;
    min-height: 0 !important;
  }

  input,
  textarea,
  [contenteditable='true'] {
    user-select: text;
  }
}

.kede-registrar-slot {
  position: absolute !important;
  width: 0 !important;
  height: 0 !important;
  overflow: hidden !important;
  opacity: 0 !important;
  pointer-events: none !important;
}

.kede-side-rail {
  position: absolute;
  top: 50%;
  z-index: 2147483000;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 72px;
  padding: 12px 6px;
  border-radius: 14px;
  background: rgba(7, 16, 24, 0.92);
  border: 1px solid rgba(76, 145, 101, 0.4);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  pointer-events: auto;
  transform: translateY(-50%);
  transition: opacity 0.2s ease, visibility 0.2s ease, transform 0.2s ease;

  :deep(.icon-button-container),
  :deep(.audio-control-container),
  :deep(.video-control-container),
  :deep(.leave-room-icon-btn) {
    width: 100%;
    justify-content: center;
  }

  &--left {
    left: 16px;
  }

  &--right {
    right: 16px;
  }

  &.kede-bar-hidden {
    opacity: 0;
    visibility: hidden;
    pointer-events: none;
  }

  &--left.kede-bar-hidden {
    transform: translate(-12px, -50%);
  }

  &--right.kede-bar-hidden {
    transform: translate(12px, -50%);
  }
}

.ai-subtitle-pc {
  position: absolute;
  left: 50%;
  bottom: 80px;
  z-index: 50;
  display: flex;
  justify-content: center;
  width: min(720px, 50%);
  pointer-events: none;
  text-align: initial;
  transform: translateX(-50%);
}

.subtitle-settings-button {
  color: var(--text-color-button);
  cursor: pointer;
  pointer-events: auto;
}

/* 兼容旧类名选择器（toolbar safe layer） */
.control-bar {
  pointer-events: auto;
}

.side-panel {
  position: absolute;
  top: 0;
  right: 0;
  box-sizing: border-box;
  width: 400px;
  height: 100%;
  z-index: 2147483001;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(100%);

  &-visible {
    transform: translateX(0);
  }
}
</style>
