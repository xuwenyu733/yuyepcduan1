import { TUIMessageBox, useUIKit } from '@tencentcloud/uikit-base-component-vue3';
import { useRoomState } from 'tuikit-atomicx-vue3/room';
import { createRouter, createWebHashHistory } from 'vue-router';
import Login from '@/views/newLogIn.vue';

const routes = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/login',
    component: Login,
  },
  {
    path: '/end',
    component: () => import('@/views/meetingSummary.vue'),
  },
  {
    path: '/summary',
    component: () => import('@/views/meetingSummary.vue'),
  },
  {
    path: '/home',
    component: () => import('@/views/home.vue'),
  },
  {
    path: '/room',
    component: () => import('@/views/room.vue'),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next();
    return;
  }
  const userInfo = sessionStorage.getItem('tuiRoom-userInfo');
  if (!userInfo) {
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }

  if ((from.path === '/room') && (to.path !== '/room') && (to.path !== '/end') && (to.path !== '/summary')) {
    const { currentRoom, leaveRoom } = useRoomState();
    const { t } = useUIKit();
    if (!currentRoom.value?.roomId) {
      next();
      return;
    }

    TUIMessageBox.confirm({
      type: 'warning',
      title: t('Room.LeaveRoomTitle'),
      content: t('Room.ConfirmLeavePage'),
      callback: async (action?: string) => {
        if (action === 'confirm') {
          await leaveRoom();
          next();
        } else {
          next(false);
        }
      },
    });
    return;
  }
  next();
});

export default router;
