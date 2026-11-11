<script setup lang="ts">
import {ref, onMounted, onUnmounted, watch, computed} from 'vue';
import { TUIRoomEvents } from '@tencentcloud/tuiroom-engine-js';
import {
  RoomParticipantRole,
  useRoomParticipantState,
  useRoomState,
} from 'tuikit-atomicx-vue3/room';
import {BuiltinWidget, TUIRoomEngine} from "../components/RoomKit/index.ts";
import {useASRToolsState} from "@/components/RoomKit/hooks/useASRToolsState";
import {useSubtitlesState} from "@/components/RoomKit/hooks/useSubtitlesState";
import {useRoomSidePanel} from "@/components/RoomKit/hooks/useRoomSidePanel";
import {MicButton} from "@/components/RoomKit/components";
import {IconSettings, useUIKit} from "@tencentcloud/uikit-base-component-vue3";
import {Subtitle} from "@/components/RoomKit/components/ASRTools";
let isSharing = false;
let isCam = false;
let isMic = false;
interface Props {
  togglePanel?: () => void;
}
// const { t } = useUIKit();
// const isOwner = computed(() => currentRoom.value?.roomOwner?.userId === localParticipant.value?.userId);
const { activeWidgetId } = useRoomSidePanel();
const { isSubtitlesVisible } = useSubtitlesState();
const { currentRoom } = useRoomState();
const { localParticipant } = useRoomParticipantState();
const isSettingsDialogVisible = ref(false);
const showMenu = ref(false);
const {
  targetLanguage,
  subtitleDisplayMode,
  hasStartedASR,
  ensureASRStarted,
} = useASRToolsState();
const roomEngine = TUIRoomEngine.getInstance();
const props = withDefaults(defineProps<Props>(), {
  togglePanel: undefined,
});
const onStart = () => isSharing = true;
const onStop = () => isSharing = false;
//
// onMounted(() => {
//   roomEngine.on(TUIRoomEvents.onScreenCaptureStarted, onStart);
//   roomEngine.on(TUIRoomEvents.onScreenCaptureStopped, onStop);
// });
//
// onUnmounted(() => {
//   roomEngine.off(TUIRoomEvents.onScreenCaptureStarted, onStart);
//   roomEngine.off(TUIRoomEvents.onScreenCaptureStopped, onStop);
// });
async function handleToggleRealtimeMessageList() {
  if (activeWidgetId.value !== BuiltinWidget.AIToolsWidget) {
    await ensureASRStarted();
  }

  props.togglePanel?.();
}
async function toggleA() {
  // console.log(isSubtitlesVisible.value);
  if (!isSubtitlesVisible.value) {
    await ensureASRStarted();
  }

  isSubtitlesVisible.value = !isSubtitlesVisible.value;
}
const toggleCamera = async () => {
  if (isCam) {
    try {
      await roomEngine.closeLocalCamera();
      isCam = false;
    }
    catch (error) {
      isCam = true;
    }

  } else {
    try{
      await roomEngine.openLocalCamera();
      isCam = true;
    }
    catch (error) {
      isCam = false;
    }
    // await roomEngine.startScreenSharing();
  }
}
const toggleMic = async () => {
  if (isMic) {
    try {
      await roomEngine.closeLocalCamera();
      isMic = false;
    }
    catch (error) {
      isMic = true;
    }

  } else {
    try{
      await roomEngine.openLocalMicrophone();
      isMic = true;
    }
    catch (error) {
      isMic = false;
    }
    // await roomEngine.startScreenSharing();
  }
}
const toggleShare = async () => {
  if (isSharing) {
    try {
      await roomEngine.stopScreenSharing();
      onStop();
    }
    catch (error) {
      onStart()
    }

  } else {
    try{
      await roomEngine.startScreenSharing();
      onStart();
    }
    catch (error) {
      onStop();
    }
    // await roomEngine.startScreenSharing();
  }
};
function closeASRRelatedUI() {
  isSubtitlesVisible.value = false;

  if (activeWidgetId.value === BuiltinWidget.AIToolsWidget) {
    props.togglePanel?.();
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
onMounted(async () => {
  await toggleCamera();
})
// watch(() => localParticipant.value?.role, (role, previousRole) => {
//   if (role === RoomParticipantRole.Owner && previousRole !== role) {
//     closeASRRelatedUI();
//   }
// });
</script>

<template>
<!--  <button @click="toggleCamera()">摄像头</button>-->
  <MicButton></MicButton>
<!--  <button @click="toggleMic()">麦克风</button>-->
<!--  <button @click="toggleShare()">屏幕共享</button>-->
</template>

<style scoped lang="scss">
.ai-subtitle-pc {
  position: fixed;
  left: 50%;
  bottom: 80px;
  transform: translateX(-50%);
  width: 50%;
  display: flex;
  justify-content: center;
  pointer-events: none;
  text-align: initial;
}
</style>
