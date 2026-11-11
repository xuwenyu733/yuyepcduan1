<template>
  <UIKitProvider :theme="{ themeStyle: 'dark'}" :language="initialLanguage">
    <div id="app">
      <router-view />
    </div>
  </UIKitProvider>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { conference, useRoomInvitation, useRoomInvitationH5, RoomEvent } from './components/RoomKit/index.ts';
import { TUIMessageBox, UIKitProvider, useUIKit } from '@tencentcloud/uikit-base-component-vue3';
import { useRoute, useRouter } from 'vue-router';
import { isPC } from './utils/utils';
const { theme, setTheme } = useUIKit();
setTheme({ themeStyle: 'dark', primaryColor: '#4C9165', textColor: '#eef4fc', backgroundColor: '#0a1520' });
// 先保证底栏功能可见；自动隐藏待底栏稳定后再开
conference.setFeatureConfig({
  toolbar: { alwaysShow: false, autoHideDelay: 5000 },
});
const { t } = useUIKit();
const initialTheme = ref(localStorage.getItem('tuiRoom-theme') || 'light');
const initialLanguage = ref(localStorage.getItem('tuiRoom-language') || '');

const router = useRouter();
const route = useRoute();

if (!isPC) {
  useRoomInvitationH5({
    onAcceptCall: (params) => {
      router.push({
        path: '/room',
        query: params,
      });
    },
  });
} else {
  useRoomInvitation({
    onAcceptCall: (params) => {
      router.push({
        path: '/room',
        query: params,
      });
    },
  });
}

const onLoginExpired = () => {
  TUIMessageBox.alert({
    title: t('Login.Expired'),
    content: t('Login.ExpiredDescription'),
  });
  router.replace({ path: '/login' });
};
const onKickedOffline = () => {
  // 专家会议 iframe 大厅：房间窗用同账号登录时会踢掉这里，属预期，勿弹窗并跳登录页
  try {
    if (window.self !== window.top) {
      sessionStorage.removeItem('tuiRoom-userInfo');
      return;
    }
  } catch (e) { /* ignore */ }
  // 离开会议后的总结页 / 主动重登大厅引起的互踢：静默处理
  try {
    const hash = String(window.location.hash || '');
    const suppress = sessionStorage.getItem('kede-suppress-kick') === '1';
    if (suppress || hash.includes('/summary') || hash.includes('/end')) {
      sessionStorage.removeItem('tuiRoom-userInfo');
      sessionStorage.removeItem('kede-suppress-kick');
      return;
    }
  } catch (e) { /* ignore */ }
  TUIMessageBox.alert({
    title: t('Login.KickedOffline'),
    content: t('Login.KickedOfflineDescription'),
  });
  router.replace({ path: '/login' });
};
const bindEvent = () => {
  conference.on(RoomEvent.USER_SIG_EXPIRED, onLoginExpired);
  conference.on(RoomEvent.KICKED_OFFLINE, onKickedOffline);
};
const unbindEvent = () => {
  conference.off(RoomEvent.USER_SIG_EXPIRED, onLoginExpired);
  conference.off(RoomEvent.KICKED_OFFLINE, onKickedOffline);
};

onMounted(async () => {
  conference.setFeatureConfig({
    toolbar: { alwaysShow: false, autoHideDelay: 5000 },
  });
  bindEvent();
  await router.isReady();
  if (route.path === '/login') {
    return;
  }
  const storedData = sessionStorage.getItem('tuiRoom-userInfo') || '{}';
  const userInfo = JSON.parse(storedData);
  try {
    await conference.login({
      userId: userInfo.userID,
      userSig: userInfo.userSig,
      sdkAppId: userInfo.SDKAppID,
    });
  } catch (error: any) {
    console.error('Login failed:', error);
    sessionStorage.removeItem('tuiRoom-userInfo');
    router.replace({ path: '/login', query: { redirect: route.fullPath } });
  }
});

onUnmounted(() => {
  unbindEvent();
});

</script>

<style lang="scss">
@use './styles/base.scss';
html, body, #app {
  width: 100% !important;
  height: 100% !important;
  max-height: 100% !important;
  overflow: hidden !important;
}
#app > * {
  width: 100% !important;
  height: 100% !important;
  max-height: 100% !important;
}
#roomPage {
  position: absolute !important;
  inset: 0 !important;
  width: 100% !important;
  height: 100% !important;
}
*{
  font-family: "DIN Alternate", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
}
.header-left{
  .icon-button-container{
    .icon-content{
      display: none !important;
    }
  }
}
.title{
  .logo{
    display: none !important;
  }
}
.subtitle-text--secondary{
  display: none !important;
}
.side-panel{
  transition: none !important;
}
.tui-message-box-default{
  border-radius: 8px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(76, 145, 101, 0.35);
}
.tui-button{
  border-radius: 6px !important;
}
.login-container{
  border-radius: 12px !important;
  background: linear-gradient(160deg, #0c1a24, #0a1520) !important;
  border: 1px solid rgba(76, 145, 101, 0.3);
}
.login-input {
  transition: none !important;
}
.participant-view{
  border-radius: 8px !important;
}
.vsdk-integration-wrapper {
  display: none !important;
}
/* 大厅卡片：抵御全局 button/布局污染 */
.kede-lobby .choose-card {
  display: flex !important;
  min-height: 168px !important;
  height: auto !important;
  background: #0c1c24 !important;
  opacity: 1 !important;
  visibility: visible !important;
  color: #eef4fc !important;
}
.header {
  background: rgba(10, 21, 32, 0.96) !important;
  border-bottom: 1px solid rgba(76, 145, 101, 0.22) !important;
  z-index: 40 !important;
}
</style>
