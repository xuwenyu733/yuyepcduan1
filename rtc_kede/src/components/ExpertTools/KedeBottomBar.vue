<template>
  <Teleport to="body">
    <div class="kede-bottom-bar" role="toolbar" data-kede-bar="1">
      <button type="button" class="kede-bar-btn" :class="{ on: !micMuted }" @click="toggleMic">
        {{ micMuted ? '麦克风' : '麦克风开' }}
      </button>
      <button type="button" class="kede-bar-btn" :class="{ on: cameraOn }" @click="toggleCamera">
        {{ cameraOn ? '摄像头开' : '摄像头' }}
      </button>
      <button type="button" class="kede-bar-btn" @click="shareScreen">屏幕共享</button>
      <button
        v-if="isHost"
        type="button"
        class="kede-bar-btn"
        :class="{ on: isRecording }"
        @click="handleRecordingClick"
      >
        {{ isRecording ? '停止录制' : '录制' }}
      </button>
      <button type="button" class="kede-bar-btn" @click="toggleWidgetPanel(BuiltinWidget.MemberWidget)">
        成员
      </button>
      <button
        type="button"
        class="kede-bar-btn"
        :class="{ on: isSubtitlesVisible || hasStartedASR }"
        @click="toggleSubtitles"
      >
        AI字幕
      </button>
      <button
        v-if="isHost || hasStartedASR"
        type="button"
        class="kede-bar-btn"
        @click="openAsrRecords"
      >
        字幕记录
      </button>
      <button
        type="button"
        class="kede-bar-btn accent"
        :class="{ on: activeWidgetId === EXPERT_TOOLS_WIDGET_ID }"
        @click="toggleWidgetPanel(EXPERT_TOOLS_WIDGET_ID)"
      >
        专家工具
      </button>
      <button type="button" class="kede-bar-btn warn" @click="leaveMeeting">离开</button>
      <button v-if="isHost" type="button" class="kede-bar-btn danger" @click="endMeeting">结束</button>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import {
  DeviceStatus,
  RoomParticipantRole,
  useDeviceState,
  useLoginState,
  useRoomParticipantState,
  useRoomState,
} from 'tuikit-atomicx-vue3/room'
import { TUIToast } from '@tencentcloud/uikit-base-component-vue3'
import { BuiltinWidget } from '@/components/RoomKit/adapter/type'
import { useRoomSidePanel } from '@/components/RoomKit/hooks/useRoomSidePanel'
import { useASRToolsState } from '@/components/RoomKit/hooks/useASRToolsState'
import { useSubtitlesState } from '@/components/RoomKit/hooks/useSubtitlesState'
import { useCloudRecordingAction } from '@/components/RoomKit/components/CloudRecording/useCloudRecordingAction'
import { EXPERT_TOOLS_WIDGET_ID } from './constants'

const emit = defineEmits(['leave', 'end'])

const { loginUserInfo } = useLoginState()
const { currentRoom, leaveRoom, endRoom } = useRoomState()
const { localParticipant, muteMicrophone, unmuteMicrophone } = useRoomParticipantState()
const {
  microphoneStatus,
  cameraStatus,
  openLocalMicrophone,
  openLocalCamera,
  closeLocalCamera,
  startScreenShare,
} = useDeviceState()
const { activeWidgetId, toggleWidgetPanel } = useRoomSidePanel()
const { isSubtitlesVisible } = useSubtitlesState()
const { hasStartedASR, ensureASRStarted } = useASRToolsState()
const { isRecording, handleRecordingClick } = useCloudRecordingAction()

const isHost = computed(() => {
  const ownerId = currentRoom.value?.roomOwner?.userId
  const localId = loginUserInfo.value?.userId || localParticipant.value?.userId
  return !!(ownerId && localId && String(ownerId) === String(localId))
    || localParticipant.value?.role === RoomParticipantRole.Owner
})
const micMuted = computed(() => microphoneStatus.value !== DeviceStatus.On)
const cameraOn = computed(() => cameraStatus.value === DeviceStatus.On)

onMounted(() => {
  // 进房后自动开摄像头，避免只剩头像空画面
  openLocalCamera().catch(() => {})
})

async function toggleMic() {
  try {
    if (microphoneStatus.value === DeviceStatus.On) {
      await muteMicrophone()
    } else {
      await openLocalMicrophone()
      await unmuteMicrophone()
    }
  } catch (e: any) {
    TUIToast.error({ message: e?.message || '麦克风操作失败' })
  }
}

async function toggleCamera() {
  try {
    if (cameraStatus.value === DeviceStatus.On) {
      await closeLocalCamera()
    } else {
      await openLocalCamera()
    }
  } catch (e: any) {
    TUIToast.error({ message: e?.message || '摄像头操作失败' })
  }
}

async function shareScreen() {
  try {
    await startScreenShare({ screenAudio: true })
  } catch (e: any) {
    TUIToast.error({ message: e?.message || '屏幕共享失败' })
  }
}

async function toggleSubtitles() {
  if (isSubtitlesVisible.value) {
    isSubtitlesVisible.value = false
    return
  }
  if (!isHost.value && !hasStartedASR.value) {
    TUIToast.info({ message: '请等待房主先开启 AI 实时字幕' })
    return
  }
  if (isHost.value && !hasStartedASR.value) {
    try {
      await ensureASRStarted()
    } catch (e) {
      return
    }
  }
  isSubtitlesVisible.value = true
}

async function openAsrRecords() {
  if (!isHost.value && !hasStartedASR.value) {
    TUIToast.info({ message: '请等待房主先开启 AI 实时字幕' })
    return
  }
  if (isHost.value && !hasStartedASR.value) {
    try {
      await ensureASRStarted()
    } catch (e) {
      return
    }
  }
  toggleWidgetPanel(BuiltinWidget.AIToolsWidget)
}

async function leaveMeeting() {
  try {
    await leaveRoom()
  } catch (e: any) {
    TUIToast.error({ message: e?.message || '离开会议失败' })
    return
  }
  emit('leave')
}

async function endMeeting() {
  if (!window.confirm('确定结束会议？所有人将退出')) return
  try {
    await endRoom()
  } catch (e: any) {
    TUIToast.error({ message: e?.message || '结束会议失败' })
    return
  }
  emit('end')
}
</script>

<style>
.kede-bottom-bar {
  position: fixed !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  z-index: 2147483646 !important;
  box-sizing: border-box;
  display: flex !important;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100% !important;
  min-height: 64px;
  padding: 10px 16px calc(10px + env(safe-area-inset-bottom, 0px));
  background: #0c1c24 !important;
  border-top: 3px solid #4c9165 !important;
  box-shadow: 0 -8px 24px rgba(0, 0, 0, 0.45);
  pointer-events: auto !important;
  opacity: 1 !important;
  visibility: visible !important;
  transform: none !important;
}
.kede-bar-btn {
  height: 40px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid rgba(76, 145, 101, 0.5);
  background: #123028;
  color: #d8f3e4;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  pointer-events: auto;
}
.kede-bar-btn:hover {
  filter: brightness(1.12);
}
.kede-bar-btn.on {
  background: #4c9165;
  color: #06110b;
  border-color: #4c9165;
}
.kede-bar-btn.accent {
  background: linear-gradient(90deg, #3d8b5c, #5cb87a);
  color: #06110b;
  border-color: #5cb87a;
}
.kede-bar-btn.warn {
  border-color: rgba(230, 162, 60, 0.7);
  color: #f0d9a8;
}
.kede-bar-btn.danger {
  border-color: rgba(220, 80, 80, 0.7);
  color: #f0b4b4;
}
</style>
