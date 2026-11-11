/**
 * 科德专家会议：会议记录 / 纪要 / 反馈 共享状态
 * 供底栏侧栏面板与 ASR 双击上传共用，面板关闭后数据不丢。
 */
import { ref, computed, watch } from 'vue'
import {
  useLoginState,
  useRoomState,
} from 'tuikit-atomicx-vue3/room'
import {
  callParentApi,
  isProfessionalUser,
  loadKedeUser,
  loadMeetingMeta,
  loadSelectedAsrText,
  resetMeetingSession,
  saveMeetingMeta,
  type KedeUserInfo,
} from '@/utils/kedeMeetingBridge'

const meetingRecordContent = ref('')
const meetingPointAndTodo = ref('')
const toDoFeedback = ref('')
const meetingPoint = ref('')
const meetToDo = ref('')
const meetingId = ref<string | number | null>(null)
const summaryLoading = ref(false)
const toast = ref('')
const kedeUser = ref<KedeUserInfo>(loadKedeUser())
let toastTimer: ReturnType<typeof setTimeout> | null = null
let listenersBound = false

function showToast(msg: string) {
  toast.value = msg
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value = '' }, 2800)
}

function appendMeetingRecord(text: string) {
  const t = String(text || '').trim()
  if (!t) return
  meetingRecordContent.value = meetingRecordContent.value
    ? `${meetingRecordContent.value}\n${t}`
    : t
}

function onAsrSelected(e: Event) {
  const detail = (e as CustomEvent).detail || {}
  appendMeetingRecord(detail.text || detail.all || '')
}

function onUserReady(e: Event) {
  kedeUser.value = (e as CustomEvent).detail || loadKedeUser()
}

function onParentChatMirror(e: Event) {
  const detail = (e as CustomEvent).detail || {}
  if (detail.content) appendMeetingRecord(detail.content)
  if (detail.meetingId) meetingId.value = detail.meetingId
  if (detail.toDoFeedback) toDoFeedback.value = detail.toDoFeedback
}

function onMeetingReset() {
  meetingRecordContent.value = ''
  meetingPointAndTodo.value = ''
  toDoFeedback.value = ''
  meetingPoint.value = ''
  meetToDo.value = ''
  meetingId.value = null
}

function ensureListeners() {
  if (listenersBound || typeof window === 'undefined') return
  listenersBound = true
  window.addEventListener('kede:asr-selected', onAsrSelected as EventListener)
  window.addEventListener('kede:user-ready', onUserReady as EventListener)
  window.addEventListener('kede:parent-mirror', onParentChatMirror as EventListener)
  window.addEventListener('kede:meeting-reset', onMeetingReset as EventListener)
  kedeUser.value = loadKedeUser()
  const meta = loadMeetingMeta()
  if (meta.meetingId) meetingId.value = meta.meetingId
  if (meta.meetingPoint || meta.meetToDo) {
    meetingPoint.value = meta.meetingPoint || ''
    meetToDo.value = meta.meetToDo || ''
    meetingPointAndTodo.value = `${meetingPoint.value}\n\n${meetToDo.value}`.trim()
  }
}

ensureListeners()

export function useExpertMeetingState() {
  const { loginUserInfo } = useLoginState()
  const { currentRoom } = useRoomState()

  const meetingNum = computed(() => currentRoom.value?.roomId || loadMeetingMeta().meetingNum || '')

  const isHost = computed(() => {
    const ownerId = currentRoom.value?.roomOwner?.userId
    const localId = loginUserInfo.value?.userId
    return !!(ownerId && localId && String(ownerId) === String(localId))
  })

  const canSubmitKb = computed(() => isProfessionalUser(kedeUser.value))

  async function applyAsrFallback(reason: string) {
    const asr = loadSelectedAsrText().trim() || String(meetingRecordContent.value || '').trim()
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
      await applyAsrFallback(e?.message || '系统异常')
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

  ensureListeners()
  watch(
    () => currentRoom.value?.roomId,
    (roomId, previousRoomId) => {
      const next = String(roomId || '').trim()
      const prev = String(previousRoomId || '').trim()
      if (next && next !== prev) {
        resetMeetingSession(next)
        meetingRecordContent.value = ''
        meetingPointAndTodo.value = ''
        toDoFeedback.value = ''
        meetingPoint.value = ''
        meetToDo.value = ''
        meetingId.value = null
        saveMeetingMeta({ meetingNum: next })
      } else if (next) {
        saveMeetingMeta({ meetingNum: next })
      }
    },
    { immediate: true },
  )

  return {
    meetingRecordContent,
    meetingPointAndTodo,
    toDoFeedback,
    meetingPoint,
    meetToDo,
    meetingId,
    summaryLoading,
    toast,
    meetingNum,
    isHost,
    canSubmitKb,
    showToast,
    appendMeetingRecord,
    getMeetSummary,
    addMeetRecord,
    updateFeedback,
  }
}

export { appendMeetingRecord }
