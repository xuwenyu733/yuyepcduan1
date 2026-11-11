<template>
  <aside class="expert-panel">
    <div class="brand-bar">
      <span class="brand">科德 · 专家会议</span>
      <span class="room-tag" v-if="meetingNum">会码 {{ meetingNum }}</span>
    </div>

    <div class="media-dock">
      <button type="button" class="dock-btn" :class="{ on: !micMuted }" @click="toggleMic">
        {{ micMuted ? '打开麦克风' : '关闭麦克风' }}
      </button>
      <button type="button" class="dock-btn" :class="{ on: cameraOn }" @click="toggleCamera">
        {{ cameraOn ? '关闭视频' : '打开视频' }}
      </button>
      <button type="button" class="dock-btn warn" @click="leaveMeeting">离开会议</button>
      <button type="button" class="dock-btn danger" @click="endMeeting">结束会议</button>
    </div>

    <div class="expert-panel__scroll">
      <section class="card">
        <header class="card-hd">会议记录</header>
        <textarea
          v-model="meetingRecordContent"
          class="card-ta"
          placeholder="双击选中语音转文字后点「上传」，内容会出现在这里"
        />
        <button
          class="card-btn"
          :disabled="!isHost || summaryLoading"
          :title="isHost ? '' : '仅房主可获取纪要'"
          @click="getMeetSummary"
        >
          {{ summaryLoading ? '系统总结中…' : '获取纪要及待办' }}
        </button>
        <p v-if="!isHost" class="hint">仅房主可发起纪要生成与云端录制</p>
      </section>

      <section class="card">
        <header class="card-hd">会议纪要及待办</header>
        <textarea
          v-model="meetingPointAndTodo"
          class="card-ta"
          placeholder="纪要与待办"
        />
        <button
          class="card-btn accent"
          :disabled="!canSubmitKb"
          :title="canSubmitKb ? '' : '仅专业人员可提交知识库'"
          @click="addMeetRecord"
        >
          提交知识库
        </button>
        <p v-if="!canSubmitKb" class="hint">提交知识库需专业人员权限</p>
      </section>

      <section class="card">
        <header class="card-hd">结果反馈</header>
        <textarea
          v-model="toDoFeedback"
          class="card-ta"
          placeholder="上一场待办的落实反馈"
        />
        <button class="card-btn" @click="updateFeedback">上传提交</button>
      </section>

      <p v-if="toast" class="toast">{{ toast }}</p>
      <div class="expert-panel__end" aria-hidden="true" />
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import {
  useLoginState,
  useRoomState,
  useDeviceState,
  useRoomParticipantState,
  DeviceStatus,
} from 'tuikit-atomicx-vue3/room'
import {
  callParentApi,
  isProfessionalUser,
  loadKedeUser,
  loadMeetingMeta,
  loadSelectedAsrText,
  saveMeetingMeta,
  type KedeUserInfo,
} from '@/utils/kedeMeetingBridge'
import { RoomEvent as ConferenceRoomEvent } from '@/components/RoomKit/adapter/type'
import { eventCenter } from '@/components/RoomKit/utils/eventCenter'

const { loginUserInfo } = useLoginState()
const { currentRoom, leaveRoom, endRoom } = useRoomState()
const {
  microphoneStatus,
  cameraStatus,
  openLocalMicrophone,
  openLocalCamera,
  closeLocalCamera,
} = useDeviceState()
const { muteMicrophone, unmuteMicrophone } = useRoomParticipantState()

const meetingRecordContent = ref('')
const meetingPointAndTodo = ref('')
const toDoFeedback = ref('')
const meetingPoint = ref('')
const meetToDo = ref('')
const meetingId = ref<string | number | null>(null)
const summaryLoading = ref(false)
const toast = ref('')
const kedeUser = ref<KedeUserInfo>(loadKedeUser())
const mediaBusy = ref(false)

const meetingNum = computed(() => currentRoom.value?.roomId || loadMeetingMeta().meetingNum || '')

const isHost = computed(() => {
  const ownerId = currentRoom.value?.roomOwner?.userId
  const localId = loginUserInfo.value?.userId
  return !!(ownerId && localId && String(ownerId) === String(localId))
})

const canSubmitKb = computed(() => isProfessionalUser(kedeUser.value))
const micMuted = computed(() => microphoneStatus.value !== DeviceStatus.On)
const cameraOn = computed(() => cameraStatus.value === DeviceStatus.On)

let toastTimer: any
function showToast(msg: string) {
  toast.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value = '' }, 2800)
}

async function toggleMic() {
  if (mediaBusy.value) return
  mediaBusy.value = true
  try {
    if (microphoneStatus.value === DeviceStatus.On) {
      await muteMicrophone()
      showToast('麦克风已关闭')
    } else {
      await openLocalMicrophone()
      await unmuteMicrophone()
      showToast('麦克风已打开')
    }
  } catch (e: any) {
    showToast(e?.message || '麦克风操作失败')
  } finally {
    mediaBusy.value = false
  }
}

async function toggleCamera() {
  if (mediaBusy.value) return
  mediaBusy.value = true
  try {
    if (cameraStatus.value === DeviceStatus.On) {
      await closeLocalCamera()
      showToast('视频已关闭')
    } else {
      await openLocalCamera()
      showToast('视频已打开')
    }
  } catch (e: any) {
    showToast(e?.message || '摄像头操作失败')
  } finally {
    mediaBusy.value = false
  }
}

async function leaveMeeting() {
  if (!currentRoom.value?.roomId) {
    showToast('当前不在会议中')
    return
  }
  try {
    const info = currentRoom.value
    await leaveRoom()
    window.alert('您已离开当前会议')
    eventCenter.emit(ConferenceRoomEvent.ROOM_LEAVE, { roomInfo: info })
  } catch (e: any) {
    showToast(e?.message || '离开会议失败')
  }
}

async function endMeeting() {
  if (!isHost.value) {
    showToast('仅房主可结束会议')
    return
  }
  if (!currentRoom.value?.roomId) {
    showToast('当前不在会议中')
    return
  }
  if (!window.confirm('确定结束会议？所有人将退出')) return
  try {
    const info = currentRoom.value
    await endRoom()
    window.alert('会议结束！')
    eventCenter.emit(ConferenceRoomEvent.ROOM_DISMISS, { roomInfo: info })
  } catch (e: any) {
    showToast(e?.message || '结束会议失败')
  }
}

function onAsrSelected(e: Event) {
  const detail = (e as CustomEvent).detail || {}
  const text = String(detail.text || '').trim()
  if (!text) return
  meetingRecordContent.value = meetingRecordContent.value
    ? `${meetingRecordContent.value}\n${text}`
    : text
}

function onUserReady(e: Event) {
  kedeUser.value = (e as CustomEvent).detail || loadKedeUser()
}

function onParentChatMirror(e: Event) {
  const detail = (e as CustomEvent).detail || {}
  if (detail.content) {
    meetingRecordContent.value += String(detail.content) + '\n'
  }
  if (detail.meetingId) meetingId.value = detail.meetingId
  if (detail.toDoFeedback) toDoFeedback.value = detail.toDoFeedback
}

watch(
  () => currentRoom.value?.roomId,
  (id) => {
    if (id) saveMeetingMeta({ meetingNum: id })
  },
  { immediate: true },
)

async function applyAsrFallback(reason: string) {
  const asr = loadSelectedAsrText().trim()
  if (!asr) {
    showToast(reason || '获取纪要失败')
    return false
  }
  meetingPoint.value = asr
  meetToDo.value = ''
  meetingPointAndTodo.value = asr
  saveMeetingMeta({
    meetingId: meetingId.value || undefined,
    meetingPoint: meetingPoint.value,
    meetToDo: meetToDo.value,
  })
  showToast('小科异常，已用语音转文字作为会议纪要')
  return true
}

async function getMeetSummary() {
  if (!isHost.value) {
    showToast('仅房主可获取纪要及待办')
    return
  }
  if (!meetingNum.value) {
    showToast('还没有开始会议')
    return
  }
  summaryLoading.value = true
  try {
    const data: any = await callParentApi('summaryPoint', {
      meetingNum: meetingNum.value,
      meetingId: meetingId.value,
    })
    meetingPoint.value = data?.meetingPoint || ''
    meetToDo.value = data?.meetToDo || ''
    meetingPointAndTodo.value = `${meetingPoint.value}\n\n${meetToDo.value}`.trim()
    saveMeetingMeta({
      meetingId: meetingId.value || undefined,
      meetingPoint: meetingPoint.value,
      meetToDo: meetToDo.value,
    })
    showToast('纪要已生成')
  } catch (e: any) {
    applyAsrFallback(e?.message || '系统异常')
  } finally {
    summaryLoading.value = false
  }
}

async function addMeetRecord() {
  if (!canSubmitKb.value) {
    showToast('仅专业人员可提交知识库')
    return
  }
  if (!meetingPointAndTodo.value.trim()) {
    showToast('请先获取或填写会议纪要')
    return
  }
  try {
    await callParentApi('createRecord', {
      meetingId: meetingId.value,
      meetingName: meetingNum.value,
      meetingNum: meetingNum.value,
      meetingPoint: meetingPoint.value || meetingPointAndTodo.value,
      meetToDo: meetToDo.value || '',
    })
    showToast('已提交知识库')
  } catch (e: any) {
    try {
      const list = JSON.parse(localStorage.getItem('kede-kb-drafts') || '[]')
      list.unshift({
        at: Date.now(),
        meetingNum: meetingNum.value,
        content: meetingPointAndTodo.value,
      })
      localStorage.setItem('kede-kb-drafts', JSON.stringify(list.slice(0, 20)))
    } catch (err) { /* ignore */ }
    showToast(e?.message === 'standalone_no_parent' ? '已本地暂存（无渔业父页）' : (e?.message || '提交失败'))
  }
}

async function updateFeedback() {
  try {
    await callParentApi('updateFeedback', {
      meetingNum: meetingNum.value,
      meetingId: meetingId.value,
      toDoFeedback: toDoFeedback.value,
    })
    showToast('反馈已提交')
  } catch (e: any) {
    showToast(e?.message === 'standalone_no_parent' ? '已本地记录反馈' : (e?.message || '提交失败'))
    try {
      sessionStorage.setItem('kede-last-feedback', toDoFeedback.value)
    } catch (err) { /* ignore */ }
  }
}

onMounted(() => {
  window.addEventListener('kede:asr-selected', onAsrSelected as EventListener)
  window.addEventListener('kede:user-ready', onUserReady as EventListener)
  window.addEventListener('kede:parent-mirror', onParentChatMirror as EventListener)
  kedeUser.value = loadKedeUser()
  const meta = loadMeetingMeta()
  if (meta.meetingId) meetingId.value = meta.meetingId
  if (meta.meetingPoint || meta.meetToDo) {
    meetingPoint.value = meta.meetingPoint || ''
    meetToDo.value = meta.meetToDo || ''
    meetingPointAndTodo.value = `${meetingPoint.value}\n\n${meetToDo.value}`.trim()
  }
  try {
    window.parent.postMessage({ type: 'rtc_kede_ready' }, '*')
  } catch (e) { /* ignore */ }
})

onUnmounted(() => {
  window.removeEventListener('kede:asr-selected', onAsrSelected as EventListener)
  window.removeEventListener('kede:user-ready', onUserReady as EventListener)
  window.removeEventListener('kede:parent-mirror', onParentChatMirror as EventListener)
  clearTimeout(toastTimer)
})

defineExpose({
  meetingRecordContent,
  meetingPointAndTodo,
  toDoFeedback,
})
</script>

<style scoped lang="scss">
.expert-panel {
  --panel-w: 320px;
  width: var(--panel-w);
  min-width: 260px;
  max-width: 360px;
  /* flex 子项必须允许收缩，否则矮屏/高 DPI 下被内容撑破，底部被父级 overflow:hidden 裁掉且内部无法滚动 */
  height: 100%;
  max-height: 100%;
  min-height: 0;
  align-self: stretch;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  background: linear-gradient(180deg, #0c1a24 0%, #0a1520 40%, #071018 100%);
  border-left: 1px solid rgba(76, 145, 101, 0.35);
  color: #eef4fc;
  overflow: hidden;
  position: relative;
  z-index: 100000;
}

.brand-bar {
  flex-shrink: 0;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
  padding: 12px 12px 8px;
  border-bottom: 1px solid rgba(76, 145, 101, 0.25);
  background: rgba(7, 16, 24, 0.92);
}
.brand {
  font-weight: 700;
  letter-spacing: 0.04em;
  color: #7dcea0;
  font-size: 14px;
}
.room-tag {
  font-size: 11px;
  color: rgba(238, 244, 252, 0.65);
}

.media-dock {
  flex-shrink: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  padding: 10px 12px;
  border-bottom: 1px solid rgba(76, 145, 101, 0.25);
  background: rgba(6, 14, 20, 0.96);
}
.dock-btn {
  height: 34px;
  border-radius: 8px;
  border: 1px solid rgba(76, 145, 101, 0.45);
  background: rgba(12, 28, 36, 0.95);
  color: #c8f0d6;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}
.dock-btn.on {
  background: #4c9165;
  color: #06110b;
  border-color: #4c9165;
}
.dock-btn.warn {
  border-color: rgba(230, 162, 60, 0.55);
  color: #f0d9a8;
}
.dock-btn.danger {
  border-color: rgba(220, 80, 80, 0.55);
  color: #f0b4b4;
}
.dock-btn:hover {
  filter: brightness(1.08);
}

.expert-panel__scroll {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  overscroll-behavior: contain;
  -webkit-overflow-scrolling: touch;
  padding: 12px 12px 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  scrollbar-gutter: stable;
}
.expert-panel__scroll::-webkit-scrollbar {
  width: 8px;
}
.expert-panel__scroll::-webkit-scrollbar-thumb {
  border-radius: 4px;
  background: rgba(76, 145, 101, 0.55);
}
.expert-panel__scroll::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.25);
}

.card {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  border-radius: 10px;
  background: rgba(12, 28, 36, 0.85);
  border: 1px solid rgba(76, 145, 101, 0.22);
}
.card-hd {
  font-size: 13px;
  font-weight: 600;
  color: #a8e6c1;
}
.card-ta {
  width: 100%;
  box-sizing: border-box;
  /* 矮视口压缩文本框，保证「结果反馈」等底部卡片能滚到可见 */
  height: clamp(56px, 11vh, 110px);
  min-height: 56px;
  max-height: 220px;
  resize: vertical;
  border: 1px solid rgba(76, 145, 101, 0.28);
  border-radius: 8px;
  background: rgba(4, 12, 18, 0.9);
  color: #eef4fc;
  padding: 8px 10px;
  font-size: 12px;
  line-height: 1.5;
  outline: none;
}
.card-ta:focus {
  border-color: #4c9165;
}
.card-btn {
  align-self: flex-end;
  border: none;
  border-radius: 6px;
  padding: 6px 14px;
  font-size: 12px;
  cursor: pointer;
  color: #0a1520;
  background: #4c9165;
  font-weight: 600;
}
.card-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
.card-btn.accent {
  background: linear-gradient(90deg, #3d8b5c, #5cb87a);
}
.hint {
  margin: 0;
  font-size: 11px;
  color: rgba(238, 244, 252, 0.45);
}
.toast {
  margin: 0;
  padding: 8px 10px;
  border-radius: 8px;
  background: rgba(76, 145, 101, 0.2);
  border: 1px solid rgba(76, 145, 101, 0.45);
  font-size: 12px;
  color: #c8f0d6;
}
.expert-panel__end {
  height: max(40px, env(safe-area-inset-bottom, 0px));
  flex-shrink: 0;
}

/* 矮屏 / 高 DPI 缩放：压缩顶栏与按钮区，把空间留给可滚动内容 */
@media (max-height: 720px), (max-height: 900px) and (min-resolution: 1.25dppx) {
  .brand-bar {
    padding: 8px 10px 6px;
  }
  .brand {
    font-size: 13px;
  }
  .media-dock {
    gap: 6px;
    padding: 8px 10px;
  }
  .dock-btn {
    height: 30px;
    font-size: 11px;
  }
  .expert-panel__scroll {
    gap: 8px;
    padding: 8px 10px 0;
  }
  .card {
    padding: 8px 10px;
    gap: 6px;
  }
  .card-ta {
    height: clamp(48px, 9vh, 88px);
    min-height: 48px;
  }
}

@media (max-height: 560px) {
  .media-dock {
    grid-template-columns: 1fr 1fr;
    gap: 4px;
    padding: 6px 8px;
  }
  .dock-btn {
    height: 28px;
    font-size: 10px;
    border-radius: 6px;
  }
  .card-ta {
    height: clamp(40px, 8vh, 72px);
    min-height: 40px;
  }
  .expert-panel__end {
    height: 48px;
  }
}
</style>
