<template>
  <!-- 嵌入自动登录：不展示表单，避免闪登录页 -->
  <div v-if="autoMode" class="kede-login kede-login--auto">
    <div class="kede-login__spin" />
    <div class="kede-login__sub">{{ statusText }}</div>
  </div>

  <div v-else class="kede-login">
    <div class="kede-login__card">
      <div class="kede-login__brand">科德专家会议</div>
      <div class="kede-login__sub">{{ statusText }}</div>

      <template v-if="loginUserInfo">
        <div class="kede-login__user">已登录：{{ loginUserInfo.userName || loginUserInfo.userId }}</div>
        <TUIButton type="primary" @click="handleLogout">退出登录</TUIButton>
      </template>

      <template v-else>
        <TUIInput v-model="userId" class="kede-login__input" placeholder="请输入 userId" />
        <TUIButton type="primary" :disabled="loggingIn" @click="handleLogin">
          {{ loggingIn ? '登录中…' : '进入会议' }}
        </TUIButton>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { TUIInput, TUIButton } from '@tencentcloud/uikit-base-component-vue3';
import { useLoginState } from 'tuikit-atomicx-vue3';
import { useRoute, useRouter } from 'vue-router';
import { SDKAPPID, genTestUserSig } from '../config/basic-info-config';
import { saveKedeUser, loadKedeUser } from '../utils/kedeMeetingBridge';

const { loginUserInfo, login, logout, setSelfInfo } = useLoginState();
const userId = ref('');
const displayName = ref('');
const loggingIn = ref(false);
const loginFailed = ref(false);
const statusText = ref('正在准备…');
const route = useRoute();
const router = useRouter();

const embedded = typeof window !== 'undefined' && window.self !== window.top;
const hasQueryUser = computed(() => Boolean(route.query.userid || route.query.userId));

/** 自动登录中：不露表单 */
const autoMode = computed(() => {
  if (loginFailed.value) return false;
  if (loginUserInfo.value) return true;
  if (loggingIn.value) return true;
  if (hasQueryUser.value) return true;
  if (sessionStorage.getItem('tuiRoom-userInfo')) return true;
  if (embedded && loadKedeUser()?.userId) return true;
  // 嵌入等待父页同步用户时也不露表单
  if (embedded && !loginFailed.value) return true;
  return false;
});

function notifyParent(type: string) {
  try {
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type }, '*');
    }
  } catch (e) { /* ignore */ }
}

/** 解析可读昵称：忽略空串、纯数字（与 userId 混淆） */
function pickDisplayName(...cands: Array<string | number | null | undefined>) {
  for (const c of cands) {
    const s = String(c == null ? '' : c).trim();
    if (!s) continue;
    if (/^\d+$/.test(s)) continue;
    return s;
  }
  return '';
}

async function applySelfDisplayName(nick?: string) {
  const name = pickDisplayName(
    nick,
    displayName.value,
    loadKedeUser()?.nickname,
    loadKedeUser()?.userName,
    route.query.username as string,
    route.query.nickname as string,
  );
  if (!name) return;
  displayName.value = name;
  try {
    saveKedeUser({
      ...loadKedeUser(),
      userId: userId.value || loadKedeUser()?.userId,
      nickname: name,
      userName: name,
    });
  } catch (e) { /* ignore */ }
  try {
    if (typeof setSelfInfo === 'function') {
      await setSelfInfo({ userName: name, avatarUrl: loginUserInfo.value?.avatarUrl || '' });
    }
  } catch (e) {
    console.warn('[rtc] setSelfInfo failed', e);
  }
}

async function handleLogin() {
  if (!userId.value || loggingIn.value) return;
  loggingIn.value = true;
  loginFailed.value = false;
  statusText.value = '正在连接会议服务…';
  try {
    const sig = genTestUserSig(String(userId.value));
    await login({
      sdkAppId: SDKAPPID,
      userId: String(userId.value),
      userSig: sig,
    });
    const userInfo = { SDKAppID: Number(SDKAPPID), userID: String(userId.value), userSig: sig };
    sessionStorage.setItem('tuiRoom-userInfo', JSON.stringify(userInfo));
    await applySelfDisplayName();
    const redirect = (route.query.redirect as string) || '/home';
    statusText.value = '正在进入…';
    await router.replace(redirect);
  } catch (e: any) {
    console.error('[rtc] login failed', e);
    loginFailed.value = true;
    statusText.value = `登录失败：${e?.message || e?.code || '请重试'}`;
    notifyParent('rtc_kede_login_failed');
  } finally {
    loggingIn.value = false;
  }
}

async function handleLogout() {
  await logout();
  sessionStorage.removeItem('tuiRoom-userInfo');
  loginFailed.value = true;
  statusText.value = '已退出，请重新登录';
}

function tryLoginFromParent(detail: any) {
  // 大厅为独立会议窗让出账号：暂停自动登录，避免把会议窗踢下线
  try {
    if (embedded && sessionStorage.getItem('kede-lobby-hold') === '1') {
      statusText.value = '会议进行中，大厅暂不登录';
      loginFailed.value = false;
      return;
    }
  } catch (e) { /* ignore */ }
  const uid = detail?.userId || detail?.id;
  if (!uid) return;
  const nick = pickDisplayName(detail?.nickname, detail?.userName, detail?.name);
  if (nick) {
    displayName.value = nick;
    saveKedeUser({ ...loadKedeUser(), ...detail, nickname: nick, userName: nick });
  } else if (detail) {
    saveKedeUser({ ...loadKedeUser(), ...detail });
  }
  if (loginUserInfo.value?.userId) {
    // 已登录：只补昵称，避免重复 login
    applySelfDisplayName(nick);
    return;
  }
  if (loggingIn.value) return;
  userId.value = String(uid);
  handleLogin();
}

function onKedeUserReady(ev: Event) {
  tryLoginFromParent((ev as CustomEvent).detail);
}

function clearLobbyHold() {
  try { sessionStorage.removeItem('kede-lobby-hold'); } catch (e) { /* ignore */ }
}

/** 解除 hold 后继续自动登录进大厅 */
function resumeLobbyLogin() {
  clearLobbyHold();
  statusText.value = '正在重新进入大厅…';
  loginFailed.value = false;
  const id = route.query.userid || route.query.userId || loadKedeUser()?.userId;
  if (id) {
    userId.value = String(id);
    handleLogin();
    return;
  }
  const cached = loadKedeUser();
  if (cached?.userId) {
    tryLoginFromParent(cached);
    return;
  }
  notifyParent('rtc_kede_ready');
}

function onParentCmd(ev: MessageEvent) {
  const data = ev.data || {};
  if (data.type !== 'parent_kede_cmd') return;
  if (data.cmd === 'lobby_hold') {
    try { sessionStorage.setItem('kede-lobby-hold', '1'); } catch (e) { /* ignore */ }
    statusText.value = '会议进行中，大厅暂不登录';
    loginFailed.value = false;
    return;
  }
  if (data.cmd === 'lobby_resume') {
    resumeLobbyLogin();
  }
}

onMounted(() => {
  window.addEventListener('kede:user-ready', onKedeUserReady as EventListener);
  window.addEventListener('message', onParentCmd);

  // 父页结束会议后会带 kede_resume=1 重载 iframe；优先清 hold，避免卡在「会议进行中」
  const resumeFlag = String(route.query.kede_resume || '').trim();
  let searchResume = '';
  try {
    searchResume = new URLSearchParams(window.location.search).get('kede_resume') || '';
  } catch (e) { /* ignore */ }
  if (resumeFlag === '1' || searchResume === '1') {
    clearLobbyHold();
  }

  // 仅通知父页可推用户信息，不要当作大厅已就绪
  notifyParent('rtc_kede_ready');

  // 会议窗开会中：大厅暂缓登录；并向父页确认是否仍在开会（结束会议后 hold 常残留）
  try {
    if (embedded && sessionStorage.getItem('kede-lobby-hold') === '1') {
      statusText.value = '会议进行中，大厅暂不登录';
      loginFailed.value = false;
      try {
        window.parent.postMessage({ type: 'rtc_kede_hold_check' }, '*');
      } catch (e0) { /* ignore */ }
      // 父页若已不在开会，会回 lobby_resume；再问两次防消息丢失
      [500, 1200, 2500].forEach((ms) => {
        window.setTimeout(() => {
          try {
            if (sessionStorage.getItem('kede-lobby-hold') !== '1') return;
            if (loginUserInfo.value?.userId || loggingIn.value) return;
            window.parent.postMessage({ type: 'rtc_kede_hold_check' }, '*');
          } catch (e1) { /* ignore */ }
        }, ms);
      });
      // 兜底：父页无响应时强制解除 hold，避免永久卡在「会议进行中 / 正在进入」
      window.setTimeout(() => {
        try {
          if (sessionStorage.getItem('kede-lobby-hold') !== '1') return;
          if (loginUserInfo.value?.userId || loggingIn.value) return;
          resumeLobbyLogin();
        } catch (e2) { /* ignore */ }
      }, 5000);
      return;
    }
  } catch (e) { /* ignore */ }

  const id = route.query.userid || route.query.userId;
  const nickFromQuery = pickDisplayName(
    route.query.username as string,
    route.query.nickname as string,
    route.query.userName as string,
  );
  if (nickFromQuery) {
    displayName.value = nickFromQuery;
    saveKedeUser({
      ...loadKedeUser(),
      userId: id ? String(id) : loadKedeUser()?.userId,
      nickname: nickFromQuery,
      userName: nickFromQuery,
    });
  }
  if (id) {
    userId.value = String(id);
    statusText.value = '正在进入专家会议…';
    handleLogin();
    return;
  }

  if (sessionStorage.getItem('tuiRoom-userInfo')) {
    statusText.value = '正在进入专家会议…';
    applySelfDisplayName().finally(() => {
      const redirect = (route.query.redirect as string) || '/home';
      router.replace(redirect);
    });
    return;
  }

  const cached = loadKedeUser();
  if (cached?.userId) {
    statusText.value = '正在进入专家会议…';
    tryLoginFromParent(cached);
    return;
  }

  if (embedded) {
    statusText.value = '正在同步用户信息…';
    // 父页超时未推用户时，再露出手动登录
    setTimeout(() => {
      if (!loginUserInfo.value && !loggingIn.value && !userId.value) {
        loginFailed.value = true;
        statusText.value = '未能同步用户，请手动输入 userId';
        notifyParent('rtc_kede_login_failed');
      }
    }, 8000);
  } else {
    loginFailed.value = true;
    statusText.value = '请输入 userId 登录';
  }
});

onUnmounted(() => {
  window.removeEventListener('kede:user-ready', onKedeUserReady as EventListener);
  window.removeEventListener('message', onParentCmd);
});
</script>

<style scoped>
.kede-login {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(ellipse 80% 50% at 20% 0%, rgba(76, 145, 101, 0.14), transparent 55%),
    linear-gradient(165deg, #0c1a24 0%, #071018 55%, #050c12 100%);
  color: #eef4fc;
}
.kede-login--auto {
  gap: 14px;
}
.kede-login__spin {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid rgba(125, 206, 160, 0.25);
  border-top-color: #7dcea0;
  animation: kede-spin 0.8s linear infinite;
}
.kede-login__card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: min(420px, 92vw);
  padding: 28px 24px;
  border-radius: 12px;
  border: 1px solid rgba(76, 145, 101, 0.35);
  background: rgba(10, 21, 32, 0.92);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
}
.kede-login__brand {
  font-size: 22px;
  font-weight: 700;
  color: #7dcea0;
  letter-spacing: 0.02em;
}
.kede-login__sub {
  font-size: 13px;
  color: rgba(238, 244, 252, 0.7);
  line-height: 1.5;
  min-height: 1.5em;
  text-align: center;
}
.kede-login__user {
  font-size: 14px;
}
.kede-login__input {
  width: 100%;
}
@keyframes kede-spin {
  to { transform: rotate(360deg); }
}
</style>
