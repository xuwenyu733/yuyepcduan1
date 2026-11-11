<template>
  <div class="home-view">
    <PreConferenceView
      @logout="handleLogout"
      @create-room="handleCreateRoom"
      @join-room="handleJoinRoom"
      @camera-preference-change="handleCameraPreferenceChange"
      @microphone-preference-change="handleMicrophonePreferenceChange"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, nextTick } from 'vue';
import { PreConferenceView } from '../components/RoomKit/index.ts';
import { useRouter } from 'vue-router';
import { useMediaPreference } from '../hooks/useMediaPreference';
import { useLoginState, useDeviceState } from 'tuikit-atomicx-vue3/room';
import type { TUIRoomType } from 'tuikit-atomicx-vue3/room';

const router = useRouter();
const { logout } = useLoginState();
const { stopCameraTest, stopMicrophoneTest } = useDeviceState();

const { setMicrophonePreference, setCameraPreference } = useMediaPreference();

onMounted(() => {
  // 大厅就绪后再让父页揭开遮罩，避免闪登录页（支持 iframe / window.open）
  try {
    const w = (window.opener && !window.opener.closed)
      ? window.opener
      : (window.parent && window.parent !== window ? window.parent : null);
    if (w) w.postMessage({ type: 'rtc_kede_lobby_ready' }, '*');
  } catch (e) { /* ignore */ }
});

const handleCameraPreferenceChange = (isOpen: boolean) => {
  setCameraPreference(isOpen);
};

const handleMicrophonePreferenceChange = (isOpen: boolean) => {
  setMicrophonePreference(isOpen);
};

const handleLogout = () => {
  router.push('/login');
};

/** 是否嵌在渔业专家会议 iframe 中（需由父页开独立会议窗） */
function inFisheryIframe() {
  try {
    return window.self !== window.top;
  } catch (e) {
    return true;
  }
}

function savePendingRoom(roomId: string, roomType: TUIRoomType, isCreate: boolean) {
  try {
    localStorage.setItem('kede-pending-room', JSON.stringify({
      roomId: String(roomId || '').trim(),
      roomType,
      isCreate: !!isCreate,
      at: Date.now(),
    }));
  } catch (e) { /* ignore */ }
}

/** 开房前释放大厅 SDK 登录，避免同账号在新窗口互踢导致又回到创建/加入页 */
async function releaseLobbySdk() {
  try {
    stopCameraTest();
    stopMicrophoneTest();
  } catch (e0) { /* ignore */ }
  await nextTick();
  try {
    await logout();
  } catch (e) { /* ignore */ }
  try {
    sessionStorage.removeItem('tuiRoom-userInfo');
    // 标记大厅暂停自动登录，直到会议窗关闭
    sessionStorage.setItem('kede-lobby-hold', '1');
  } catch (e2) { /* ignore */ }
  await nextTick();
}

function requestParentOpenRoom(roomId: string, roomType: TUIRoomType, isCreate: boolean) {
  const target = (window.parent && window.parent !== window) ? window.parent : null;
  if (!target) return false;
  target.postMessage({
    type: 'rtc_kede_open_room',
    roomId: String(roomId || '').trim(),
    roomType,
    isCreate: !!isCreate,
  }, '*');
  return true;
}

const handleCreateRoom = async (roomId: string, roomType: TUIRoomType) => {
  const targetRoomId = sessionStorage.getItem('manualRoomId') || roomId;
  sessionStorage.setItem(`room-${targetRoomId}-isCreate`, 'true');
  savePendingRoom(String(targetRoomId), roomType, true);
  // 嵌入渔业页：大厅保留创建/加入页；房间另开窗直进，不再二次选创建/加入
  if (inFisheryIframe()) {
    await releaseLobbySdk();
    if (requestParentOpenRoom(String(targetRoomId), roomType, true)) {
      return;
    }
  }
  router.push({
    path: '/room',
    query: { roomId: targetRoomId, roomType: String(roomType), isCreate: '1' },
  });
};

const handleJoinRoom = async (roomId: string, roomType: TUIRoomType) => {
  sessionStorage.setItem(`room-${roomId}-isCreate`, 'false');
  savePendingRoom(String(roomId), roomType, false);
  if (inFisheryIframe()) {
    await releaseLobbySdk();
    if (requestParentOpenRoom(String(roomId), roomType, false)) {
      return;
    }
  }
  router.push({
    path: '/room',
    query: { roomId, roomType: String(roomType), isCreate: '0' },
  });
};
</script>

<style lang="scss" scoped>
.home-view {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 100%;
  overflow: hidden;
}
</style>
