<template>
  <ConferenceMainView />
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, watch } from 'vue';
import { conference, ConferenceMainView, RoomEvent as ConferenceRoomEvent, handleMediaCaptureError, InterceptorAction } from '../components/RoomKit/index.ts';
import {
  useUIKit,
  TUIMessageBox,
} from '@tencentcloud/uikit-base-component-vue3';
import {
  useLoginState,
  useRoomState,
  useDeviceState,
  VideoQuality,
  useRoomParticipantState,
  useRoomModal,
  RoomType,
} from 'tuikit-atomicx-vue3/room';
import { useRoute, useRouter } from 'vue-router';
import { useMediaPreference } from '../hooks/useMediaPreference';
import { waitForRoomChannelReady } from '../utils/waitForRoomChannel';
const route = useRoute();
const router = useRouter();
const { t } = useUIKit();
const { handleErrorWithModal } = useRoomModal();

const { loginUserInfo } = useLoginState();
const { currentRoom } = useRoomState();
const { localVideoQuality, openLocalCamera, updateVideoQuality, openLocalMicrophone } = useDeviceState();
const { muteMicrophone, unmuteMicrophone } = useRoomParticipantState();

const { getMicrophonePreference, getCameraPreference } = useMediaPreference();

const { roomId, password, roomType: roomTypeString, isCreate: isCreateQuery } = route.query as {
  roomId: string;
  password?: string;
  roomType: string;
  isCreate?: string;
};
const roomType = Number(roomTypeString) as RoomType;

if (!roomId) {
  router.replace('/home');
}

watch(() => loginUserInfo.value?.userId, async (userId, oldUserId) => {
  if (!oldUserId && userId) {
    await handleEnterRoom();
  }
}, { immediate: true });
watch(() => currentRoom.value?.roomId, async (currentRoomId, prevRoomId) => {
  if (!prevRoomId && currentRoomId) {
    if (currentRoom.value?.roomType === RoomType.Webinar && currentRoom.value?.roomOwner.userId !== loginUserInfo.value?.userId) {
      return;
    }
    handleOpenMicrophone();
    handleOpenCamera().catch(() => {});
  }
}, { immediate: true });

async function handleEnterRoom() {
  const isCreateKey = `room-${roomId}-isCreate`;
  const fromQuery = isCreateQuery === '1' || isCreateQuery === 'true';
  let isCreate = fromQuery || sessionStorage.getItem(isCreateKey) === 'true';
  try {
    const pending = JSON.parse(localStorage.getItem('kede-pending-room') || 'null');
    if (pending && String(pending.roomId) === String(roomId) && pending.isCreate != null) {
      isCreate = !!pending.isCreate || isCreate;
    }
  } catch (e) { /* ignore */ }
  sessionStorage.removeItem(isCreateKey);

  const isInitRace = (err: any) => {
    const msg = String((err && (err.message || err.msg || err.code)) || err || '');
    return /sdk not ready|not ready|not inited|SendPacket|not init|channel.*init|未初始化/i.test(msg);
  };

  // 进房前先等 SSO 通道，减少首包 SendPacket|not inited
  await waitForRoomChannelReady({ maxMs: 8000, intervalMs: 400 });

  const enterOnce = async () => {
    if (isCreate) await handleStartConference();
    else await handleJoinConference();
    try { localStorage.removeItem('kede-pending-room'); } catch (e2) { /* ignore */ }
  };

  try {
    await enterOnce();
    return;
  } catch (error: any) {
    if (isInitRace(error)) {
      const waits = [800, 1500, 2500, 4000];
      for (let i = 0; i < waits.length; i++) {
        await new Promise((r) => setTimeout(r, waits[i]));
        await waitForRoomChannelReady({ maxMs: 3000, intervalMs: 400 });
        try {
          await enterOnce();
          return;
        } catch (e4) {
          if (!isInitRace(e4) || i === waits.length - 1) {
            handleErrorWithModal(e4);
            console.error('[rtc] enter room failed after retries', e4);
            return;
          }
        }
      }
      return;
    }
    handleErrorWithModal(error);
    console.error('[rtc] enter room failed', error);
  }
}

async function handleStartConference() {
  const roomName = roomType === RoomType.Webinar
    ? `${loginUserInfo.value?.userName || loginUserInfo.value?.userId}${t('Room.Webinar')}`
    : `${loginUserInfo.value?.userName || loginUserInfo.value?.userId}${t('Room.TemporaryMeeting')}`;
  await conference.createAndJoinRoom({
    roomId,
    roomType,
    options: { roomName },
  });
}

async function handleJoinConference() {
  await conference.joinRoom({
    roomId,
    roomType,
    password,
  });
}

async function handleOpenCamera() {
  if (!localVideoQuality.value) {
    updateVideoQuality({ quality: VideoQuality.Quality720P });
  }
  // 专家会议进房默认开摄像头，避免只看到头像空画面
  await openLocalCamera();
}

async function handleOpenMicrophone() {
  await muteMicrophone();
  await openLocalMicrophone();
  if (getMicrophonePreference()) {
    await unmuteMicrophone();
  }
}

const handleBackHome = () => {
  try {
    const info = currentRoom.value;
    const meetingNum = info?.roomId || roomId || '';
    try {
      const raw = localStorage.getItem('kede-meeting-meta') || '{}';
      const meta = JSON.parse(raw);
      meta.meetingNum = meetingNum || meta.meetingNum;
      localStorage.setItem('kede-meeting-meta', JSON.stringify(meta));
      sessionStorage.setItem('kede-meeting-meta', JSON.stringify(meta));
    } catch (e0) { /* ignore */ }
    // 先进入总结页，再通知父页离开，避免父页提前关掉本窗口导致提交超时
    try {
      sessionStorage.setItem('kede-suppress-kick', '1');
    } catch {
      /* ignore */
    }
    const notifyLeave = () => {
      try {
        const w = (window.opener && !window.opener.closed)
          ? window.opener
          : (window.parent && window.parent !== window ? window.parent : null);
        if (!w) return;
        let asrText = '';
        let meetingPoint = '';
        let meetToDo = '';
        try {
          const asrRaw = localStorage.getItem('kede-selected-asr-text')
            || sessionStorage.getItem('kede-selected-asr-text')
            || '';
          if (asrRaw) {
            try {
              const parsed = JSON.parse(asrRaw);
              if (parsed && typeof parsed === 'object') {
                const num = String(parsed.meetingNum || '');
                if (!meetingNum || !num || num === String(meetingNum)) {
                  asrText = String(parsed.text || '').trim();
                }
              } else {
                asrText = String(asrRaw).trim();
              }
            } catch {
              asrText = String(asrRaw).trim();
            }
          }
          const meta = JSON.parse(localStorage.getItem('kede-meeting-meta') || sessionStorage.getItem('kede-meeting-meta') || '{}');
          meetingPoint = String(meta.meetingPoint || '').trim();
          meetToDo = String(meta.meetToDo || '').trim();
        } catch {
          /* ignore */
        }
        const summaryText = [meetingPoint, meetToDo, asrText].filter(Boolean).join('\n\n').trim();
        w.postMessage({
          type: 'son_send_to_parent_meeting',
          value_type: 'son_send_to_parent_meeting_leave_meet',
          meetingNum,
          asrText,
          meetingPoint,
          meetToDo,
          summaryText,
        }, '*');
        w.postMessage({ type: 'rtc_kede_left_room', meetingNum, asrText, summaryText }, '*');
      } catch {
        /* ignore */
      }
    };
    try {
      const num = meetingNum;
      router.replace({ path: '/summary', query: num ? { roomId: String(num) } : {} }).then(() => {
        window.setTimeout(notifyLeave, 80);
      }).catch(() => {
        notifyLeave();
      });
      return;
    } catch {
      notifyLeave();
    }
  } catch {
    /* ignore */
  }
  try {
    if (window.opener && !window.opener.closed) {
      window.close();
      return;
    }
  } catch {
    /* ignore */
  }
  router.replace('/home');
};

let removeScreenShareInterceptor: (() => void) | undefined;

onMounted(() => {
  conference.on(ConferenceRoomEvent.ROOM_DISMISS, handleBackHome);
  conference.on(ConferenceRoomEvent.ROOM_LEAVE, handleBackHome);
  conference.on(ConferenceRoomEvent.ROOM_ERROR, handleBackHome);
  conference.on(ConferenceRoomEvent.KICKED_OUT, handleBackHome);

  removeScreenShareInterceptor = conference.onWill(
    InterceptorAction.StartScreenShare,
    (_action, next, abort) => {
      TUIMessageBox.confirm({
        title: t('ScreenShare.StartSharing'),
        content: t('ScreenShare.StartSharingConfirm'),
        callback: async (action) => {
          if (action === 'confirm') {
            await next();
          } else {
            abort();
          }
        },
      });
    },
  );
});

onUnmounted(() => {
  conference.off(ConferenceRoomEvent.ROOM_DISMISS, handleBackHome);
  conference.off(ConferenceRoomEvent.ROOM_LEAVE, handleBackHome);
  conference.off(ConferenceRoomEvent.ROOM_ERROR, handleBackHome);
  conference.off(ConferenceRoomEvent.KICKED_OUT, handleBackHome);
  removeScreenShareInterceptor?.();
});

</script>

<style lang="scss" scoped>
</style>
