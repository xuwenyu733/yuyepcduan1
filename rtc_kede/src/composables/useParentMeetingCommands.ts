/**
 * 父页底栏 → 会议页指令
 * 父页在 iframe 外渲染底栏，避免被 RoomKit / 视频层裁掉。
 */
import { onMounted, onUnmounted } from 'vue'
import {
  DeviceStatus,
  useDeviceState,
  useRoomParticipantState,
  useRoomState,
} from 'tuikit-atomicx-vue3/room'
import { TUIToast } from '@tencentcloud/uikit-base-component-vue3'
import { BuiltinWidget } from '@/components/RoomKit/adapter/type'
import { useRoomSidePanel } from '@/components/RoomKit/hooks/useRoomSidePanel'
import { useASRToolsState } from '@/components/RoomKit/hooks/useASRToolsState'
import { useSubtitlesState } from '@/components/RoomKit/hooks/useSubtitlesState'
import { useCloudRecordingAction } from '@/components/RoomKit/components/CloudRecording/useCloudRecordingAction'
import { EXPERT_TOOLS_WIDGET_ID } from '@/components/ExpertTools/constants'
import { eventCenter } from '@/components/RoomKit/utils/eventCenter'
import { RoomEvent as ConferenceRoomEvent } from '@/components/RoomKit/adapter/type'

export function useParentMeetingCommands() {
  const { leaveRoom, endRoom, currentRoom } = useRoomState()
  const { muteMicrophone, unmuteMicrophone } = useRoomParticipantState()
  const {
    microphoneStatus,
    cameraStatus,
    openLocalMicrophone,
    openLocalCamera,
    closeLocalCamera,
    startScreenShare,
  } = useDeviceState()
  const { toggleWidgetPanel } = useRoomSidePanel()
  const { isSubtitlesVisible } = useSubtitlesState()
  const { hasStartedASR, ensureASRStarted } = useASRToolsState()
  const { handleRecordingClick } = useCloudRecordingAction()

  async function runCmd(cmd: string) {
    try {
      switch (cmd) {
        case 'toggle_mic':
          if (microphoneStatus.value === DeviceStatus.On) {
            await muteMicrophone()
          } else {
            await openLocalMicrophone()
            await unmuteMicrophone()
          }
          break
        case 'toggle_camera':
          if (cameraStatus.value === DeviceStatus.On) {
            await closeLocalCamera()
          } else {
            await openLocalCamera()
          }
          break
        case 'share_screen':
          await startScreenShare({ screenAudio: true })
          break
        case 'record':
          handleRecordingClick()
          break
        case 'members':
          toggleWidgetPanel(BuiltinWidget.MemberWidget)
          break
        case 'ai_subtitle': {
          if (isSubtitlesVisible.value) {
            isSubtitlesVisible.value = false
            break
          }
          if (!hasStartedASR.value) {
            await ensureASRStarted()
          }
          isSubtitlesVisible.value = true
          break
        }
        case 'asr_records':
          if (!hasStartedASR.value) {
            await ensureASRStarted()
          }
          toggleWidgetPanel(BuiltinWidget.AIToolsWidget)
          break
        case 'expert_tools':
          toggleWidgetPanel(EXPERT_TOOLS_WIDGET_ID)
          break
        case 'leave':
          await leaveRoom()
          eventCenter.emit(ConferenceRoomEvent.ROOM_LEAVE, { roomInfo: currentRoom.value })
          break
        case 'end':
          if (!window.confirm('确定结束会议？所有人将退出')) return
          await endRoom()
          eventCenter.emit(ConferenceRoomEvent.ROOM_DISMISS, { roomInfo: currentRoom.value })
          break
        default:
          break
      }
    } catch (e: any) {
      TUIToast.error({ message: e?.message || `操作失败: ${cmd}` })
    }
  }

  function onMessage(ev: MessageEvent) {
    const data = ev.data || {}
    if (data.type !== 'parent_kede_cmd' || !data.cmd) return
    runCmd(String(data.cmd))
  }

  onMounted(() => {
    window.addEventListener('message', onMessage)
    const notifyParent = () => {
      try {
        if (window.parent && window.parent !== window) {
          window.parent.postMessage({ type: 'rtc_kede_room_ui' }, '*')
        }
      } catch {
        /* ignore */
      }
    }
    notifyParent()
    window.setTimeout(notifyParent, 500)
  })

  onUnmounted(() => {
    window.removeEventListener('message', onMessage)
  })
}
