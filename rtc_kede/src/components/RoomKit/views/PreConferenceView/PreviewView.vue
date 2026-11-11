<template>
  <div class="kede-lobby">
    <header v-if="mergedUiOptions.showHeader" class="kede-lobby__top">
      <div class="kede-lobby__brand">
        <span class="kede-lobby__mark">科德</span>
        <span class="kede-lobby__title">专家会议</span>
      </div>
      <div class="kede-lobby__top-actions">
        <LoginUserInfo @logout="handleLogout" />
      </div>
    </header>

    <!-- 第一步：选择创建或加入（用 div，避开全局 button 样式把卡片压成 56px 透明） -->
    <main v-if="step === 'choose'" class="kede-lobby__choose">
      <h2 class="choose-title">请选择会议方式</h2>
      <p class="choose-sub">创建新会议立即发起，或输入会议号加入已有会议</p>
      <div class="choose-grid">
        <div
          role="button"
          tabindex="0"
          class="choose-card"
          @click="goCreate"
          @keyup.enter="goCreate"
        >
          <div class="choose-card__icon">＋</div>
          <div class="choose-card__name">创建会议</div>
          <div class="choose-card__desc">生成会议号，立即作为房主发起</div>
        </div>
        <div
          role="button"
          tabindex="0"
          class="choose-card"
          @click="goJoin"
          @keyup.enter="goJoin"
        >
          <div class="choose-card__icon">⇢</div>
          <div class="choose-card__name">加入会议</div>
          <div class="choose-card__desc">输入对方分享的会议号进入</div>
        </div>
      </div>
    </main>

    <!-- 创建会议 -->
    <main v-else-if="step === 'create'" class="kede-lobby__main">
      <section class="lobby-card">
        <div class="step-bar">
          <button type="button" class="back-btn" @click="step = 'choose'">← 返回</button>
          <span class="step-bar__label">创建会议</span>
        </div>
        <div class="lobby-card__preview">
          <div id="room-preview-video" class="lobby-card__video" />
          <div class="lobby-card__overlay">
            <span v-if="!isCameraTesting && !isCameraTestLoading" class="lobby-card__hint">摄像头未开启</span>
            <IconLoading v-if="isCameraTestLoading" size="36" class="lobby-card__loading" />
          </div>
          <div class="lobby-card__media">
            <MicButton />
            <CameraButton cameraTestContainer="room-preview-video" />
          </div>
        </div>
        <div class="lobby-card__actions">
          <div class="meet-code">
            <div class="meet-code__label">会议号</div>
            <div class="meet-code__row">
              <input
                v-model="createMeetingCode"
                class="meet-code__input"
                maxlength="32"
                placeholder="可手动输入，留空则自动生成"
                @keyup.enter="handleCreateMeeting"
              />
              <button
                type="button"
                class="meet-code__gen"
                :disabled="entering"
                @click="fillRandomMeetingCode"
              >
                随机生成
              </button>
            </div>
            <p class="meet-code__tip">可先调试麦克风与摄像头；会议号可手动指定，也可点「随机生成」</p>
          </div>
          <button
            type="button"
            class="primary-btn"
            :disabled="entering"
            @click="handleCreateMeeting"
          >
            {{ entering ? '创建中…' : '创建并进入会议' }}
          </button>
        </div>
      </section>
    </main>

    <!-- 加入会议 -->
    <main v-else-if="step === 'join'" class="kede-lobby__main">
      <section class="lobby-card">
        <div class="step-bar">
          <button type="button" class="back-btn" @click="step = 'choose'">← 返回</button>
          <span class="step-bar__label">加入会议</span>
        </div>
        <div class="lobby-card__preview">
          <div id="room-preview-video-join" class="lobby-card__video" />
          <div class="lobby-card__overlay">
            <span v-if="!isCameraTesting && !isCameraTestLoading" class="lobby-card__hint">摄像头未开启</span>
            <IconLoading v-if="isCameraTestLoading" size="36" class="lobby-card__loading" />
          </div>
          <div class="lobby-card__media">
              <MicButton />
            <CameraButton cameraTestContainer="room-preview-video-join" />
          </div>
            </div>
        <div class="lobby-card__actions">
          <div class="meet-code">
            <div class="meet-code__label">会议号</div>
            <div class="meet-code__row">
                <input
                  v-model="manualMeetingCode"
                class="meet-code__input"
                  maxlength="32"
                placeholder="请输入会议号"
                @keyup.enter="handleJoinOnly"
              />
              <button
                type="button"
                class="meet-code__enter"
                :disabled="entering"
                @click="handleJoinOnly"
              >
                {{ entering ? '加入中…' : '加入会议' }}
              </button>
            </div>
            <p class="meet-code__tip">请输入房主分享的会议号；房间不存在时会提示，不会自动创建</p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  onMounted,
  onBeforeUnmount,
  watch,
  ref,
} from 'vue';
import { TUIErrorCode } from '@tencentcloud/tuiroom-engine-js';
import {
  IconLoading,
  TUIToast,
} from '@tencentcloud/uikit-base-component-vue3';
import {
  RoomType,
  useDeviceState,
  useRoomState,
} from 'tuikit-atomicx-vue3/room';
import CameraButton from '../../components/CameraButton/index.vue';
import LoginUserInfo from '../../components/LoginUserInfo/index.vue';
import MicButton from '../../components/MicButton/index.vue';

type LobbyStep = 'choose' | 'create' | 'join';

interface Emits {
  (e: 'create-room', roomId: string, roomType: RoomType): void;
  (e: 'join-room', roomId: string, roomType: RoomType): void;
  (e: 'camera-preference-change', isOpen: boolean): void;
  (e: 'microphone-preference-change', isOpen: boolean): void;
  (e: 'logout'): void;
}

interface Props {
  uiOptions?: {
    showHeader?: boolean;
    showLogo?: boolean;
  };
}

const props = withDefaults(defineProps<Props>(), {
  uiOptions: () => ({}),
});

const emit = defineEmits<Emits>();
const mergedUiOptions = computed(() => ({
  showHeader: true,
  showLogo: false,
  ...props.uiOptions,
}));

const step = ref<LobbyStep>('choose');
const createMeetingCode = ref('');
const manualMeetingCode = ref('');
const entering = ref(false);

const { getRoomInfo } = useRoomState();
const {
  isMicrophoneTesting,
  isCameraTesting,
  isCameraTestLoading,
  stopCameraTest,
  stopMicrophoneTest,
} = useDeviceState();

watch(isMicrophoneTesting, () => {
  emit('microphone-preference-change', isMicrophoneTesting.value);
});

watch(isCameraTesting, () => {
  emit('camera-preference-change', isCameraTesting.value);
});

const checkRoomExist = async (roomId: string) => {
  try {
    await getRoomInfo({ roomId });
  } catch (error: any) {
    if (error.code === TUIErrorCode.ERR_ROOM_ID_NOT_EXIST) {
      return false;
    }
  }
  return true;
};

const MAX_ROOM_ID_RETRIES = 5;

const generateRoomId = async (roomType: RoomType, retriesLeft = MAX_ROOM_ID_RETRIES): Promise<string> => {
  const roomId = String(Math.floor(Math.random() * 900000) + 100000);
  if (!(await checkRoomExist(roomId))) {
    return roomType === RoomType.Standard ? roomId : `webinar_${roomId}`;
  }
  if (retriesLeft <= 1) {
    const fallbackId = `${Math.floor(Math.random() * 900000) + 100000}_${Date.now()}`;
    return roomType === RoomType.Standard ? fallbackId : `webinar_${fallbackId}`;
  }
  return generateRoomId(roomType, retriesLeft - 1);
};

function goCreate() {
  step.value = 'create';
  if (!createMeetingCode.value) {
    fillRandomMeetingCode();
  }
}

function goJoin() {
  step.value = 'join';
}

function fillRandomMeetingCode() {
  createMeetingCode.value = String(Math.floor(Math.random() * 900000) + 100000);
}

const handleCreateMeeting = async () => {
  entering.value = true;
  try {
    let roomId = String(createMeetingCode.value || '').trim();
    if (!roomId) {
      roomId = await generateRoomId(RoomType.Standard);
      createMeetingCode.value = roomId;
    } else {
  const exist = await checkRoomExist(roomId);
  if (exist) {
        TUIToast.error({ message: '该会议号已存在，请换一个或点「随机生成」' });
        return;
      }
    }
    sessionStorage.setItem(`room-${roomId}-isCreate`, 'true');
    sessionStorage.setItem('manualRoomId', roomId);
    emit('create-room', roomId, RoomType.Standard);
  } finally {
    entering.value = false;
  }
};

/** 仅加入：房间不存在则提示，不自动创建 */
const handleJoinOnly = async () => {
  const code = String(manualMeetingCode.value || '').trim();
  if (!code) {
    TUIToast.error({ message: '请输入会议号' });
    return;
  }
  entering.value = true;
  try {
    const exist = await checkRoomExist(code);
    if (!exist) {
      TUIToast.error({ message: '会议不存在，请确认会议号，或返回选择「创建会议」' });
      return;
    }
    const isWebinar = code.startsWith('webinar_');
    const type = isWebinar ? RoomType.Webinar : RoomType.Standard;
    emit('join-room', code, type);
  } finally {
    entering.value = false;
  }
};

function handleLogout() {
  emit('logout');
}

onMounted(() => {
  const fromQuery = new URLSearchParams(window.location.hash.split('?')[1] || '').get('meetingCode')
    || new URLSearchParams(window.location.search).get('meetingCode');
  if (fromQuery) {
    manualMeetingCode.value = fromQuery;
    step.value = 'join';
  }
});

onBeforeUnmount(() => {
  stopCameraTest();
  stopMicrophoneTest();
});
</script>

<style lang="scss" scoped>
.kede-lobby {
  --lobby-green: #4c9165;
  --lobby-green-soft: #7dcea0;
  --lobby-line: rgba(76, 145, 101, 0.28);
  --lobby-text: #eef4fc;
  --lobby-muted: rgba(238, 244, 252, 0.62);

  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  min-height: 0;
  padding: 10px 20px 16px;
  color: var(--lobby-text);
  background:
    radial-gradient(ellipse 80% 50% at 20% 0%, rgba(76, 145, 101, 0.14), transparent 55%),
    linear-gradient(165deg, #0c1a24 0%, #071018 55%, #050c12 100%);
}

.kede-lobby__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  margin-bottom: 8px;
}
.kede-lobby__brand {
  display: flex;
  align-items: baseline;
  gap: 10px;
}
.kede-lobby__mark {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--lobby-green-soft);
}
.kede-lobby__title {
  font-size: 15px;
  color: var(--lobby-muted);
  letter-spacing: 0.12em;
}

.kede-lobby__choose {
  flex: 1 1 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  min-height: 0;
  padding-top: 28px;
  overflow: auto;
}
.choose-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #eef4fc;
  flex-shrink: 0;
}
.choose-sub {
  margin: 0 0 8px;
  font-size: 14px;
  color: var(--lobby-muted);
  flex-shrink: 0;
}
.choose-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  width: 100%;
  max-width: 760px;
  justify-content: center;
  align-content: start;
  padding: 8px 0 24px;
  box-sizing: border-box;
}
.choose-card {
  display: flex !important;
  flex-direction: column !important;
  align-items: flex-start !important;
  gap: 10px !important;
  box-sizing: border-box;
  width: min(320px, calc(50% - 12px));
  min-width: 220px;
  min-height: 168px !important;
  height: auto !important;
  max-height: none !important;
  padding: 28px 24px !important;
  border-radius: 16px !important;
  border: 1px solid var(--lobby-line) !important;
  background: #0c1c24 !important;
  color: #eef4fc !important;
  text-align: left !important;
  cursor: pointer;
  overflow: visible !important;
  opacity: 1 !important;
  visibility: visible !important;
  white-space: normal !important;
  transform: none !important;
  transition: border-color 0.15s ease, transform 0.15s ease, background 0.15s ease;
}
.choose-card:hover {
  border-color: var(--lobby-green);
  background: rgba(12, 32, 28, 0.95);
  transform: translateY(-2px);
}
.choose-card__icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(76, 145, 101, 0.2);
  color: var(--lobby-green-soft);
  font-size: 24px;
  font-weight: 700;
}
.choose-card__name {
  font-size: 20px;
  font-weight: 700;
  color: #eef4fc;
}
.choose-card__desc {
  font-size: 13px;
  line-height: 1.5;
  color: var(--lobby-muted);
}

.kede-lobby__main {
  flex: 1;
  min-height: 0;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 8px;
  overflow: auto;
}

.lobby-card {
  display: flex;
  flex-direction: column;
  width: min(720px, 100%);
  max-height: 100%;
  min-height: 0;
  flex-shrink: 1;
  border-radius: 16px;
  border: 1px solid var(--lobby-line);
  background: rgba(10, 21, 32, 0.88);
  overflow: hidden;
}

.step-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  padding: 10px 16px;
  border-bottom: 1px solid var(--lobby-line);
  background: rgba(7, 16, 24, 0.9);
}
.back-btn {
  border: none;
  background: transparent;
  color: var(--lobby-green-soft);
  font-size: 13px;
  cursor: pointer;
}
.step-bar__label {
  font-size: 14px;
  font-weight: 600;
  color: #eef4fc;
}

.lobby-card__preview {
      position: relative;
  flex: 1 1 auto;
  height: min(320px, 42vh);
  min-height: 140px;
  max-height: min(320px, 42vh);
  background: #040a10;
}
.lobby-card__video {
        width: 100%;
        height: 100%;
      }
.lobby-card__overlay {
        position: absolute;
  inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
  pointer-events: none;
}
.lobby-card__hint {
  font-size: 15px;
  color: rgba(238, 244, 252, 0.45);
}
.lobby-card__loading {
  animation: lobby-spin 1.2s linear infinite;
}
.lobby-card__media {
  position: absolute;
  left: 16px;
  bottom: 16px;
  z-index: 2;
      display: flex;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 12px;
  background: rgba(7, 16, 24, 0.78);
  border: 1px solid rgba(76, 145, 101, 0.25);
}

.lobby-card__actions {
        display: flex;
        flex-direction: column;
        gap: 12px;
  flex-shrink: 0;
  padding: 16px 20px 18px;
  border-top: 1px solid var(--lobby-line);
  background: rgba(7, 16, 24, 0.95);
}
.action-tip {
  margin: 0;
            font-size: 13px;
  color: var(--lobby-muted);
}
.primary-btn {
  height: 46px;
            border: none;
  border-radius: 10px;
  background: var(--lobby-green);
  color: #06110b;
  font-size: 15px;
            font-weight: 700;
            cursor: pointer;
          }
.primary-btn:hover:not(:disabled) {
  filter: brightness(1.08);
}
.primary-btn:disabled {
  opacity: 0.65;
  cursor: wait;
}

.meet-code__label {
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  color: var(--lobby-green-soft);
}
.meet-code__row {
  display: flex;
  gap: 10px;
}
.meet-code__input {
  flex: 1;
  height: 44px;
  padding: 0 14px;
  border-radius: 10px;
  border: 1px solid rgba(76, 145, 101, 0.4);
  background: #050c12;
  color: var(--lobby-text);
  font-size: 15px;
  outline: none;
}
.meet-code__enter,
.meet-code__gen {
  min-width: 112px;
  height: 44px;
  border: none;
  border-radius: 10px;
  background: var(--lobby-green);
  color: #06110b;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}
.meet-code__gen {
  min-width: 96px;
  background: rgba(76, 145, 101, 0.22);
  color: var(--lobby-green-soft);
  border: 1px solid rgba(76, 145, 101, 0.45);
}
.meet-code__gen:disabled,
.meet-code__enter:disabled {
  opacity: 0.65;
  cursor: wait;
}
.meet-code__tip {
  margin: 8px 0 0;
  font-size: 12px;
  color: var(--lobby-muted);
}

@keyframes lobby-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 720px) {
  .choose-grid {
    flex-direction: column;
    align-items: center;
    max-width: 360px;
  }
  .choose-card {
    width: 100% !important;
    min-width: 0 !important;
  }
}

/* 矮屏 / 高 DPI：压缩预览区，保证底部「进入会议」等操作完整可见 */
@media (max-height: 720px), (max-height: 900px) and (min-resolution: 1.25dppx) {
  .kede-lobby {
    padding: 8px 14px 12px;
  }
  .lobby-card__preview {
    height: min(220px, 32vh);
    min-height: 100px;
    max-height: min(220px, 32vh);
  }
  .lobby-card__actions {
    padding: 12px 16px 14px;
    gap: 8px;
  }
  .primary-btn {
    height: 40px;
    font-size: 14px;
  }
}

@media (max-height: 560px) {
  .lobby-card__preview {
    height: min(160px, 26vh);
    min-height: 80px;
    max-height: min(160px, 26vh);
  }
  .choose-card {
    padding: 18px 16px;
  }
}
</style>
