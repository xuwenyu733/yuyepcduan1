import { ref, watch, onMounted, onUnmounted, computed, nextTick } from 'vue';
import type { Ref } from 'vue';
import { conference } from '../adapter/conference';

const DEFAULT_AUTO_HIDE_DELAY = 5000;
const TOOLBAR_SAFE_LAYER_SELECTOR = '[class*="emoji-picker__list"], .kede-control-bar, .kede-side-rail, .kede-bottom-bar, .toolbar-hotzone, .side-panel, .dropdown-menu, .ai-subtitle-pc';

export function useRoomToolbar(
  containerRef: Ref<HTMLElement | null>,
  mouseMoveThrottle = 200,
) {
  const toolbarConfig = computed(() => conference.getFeatureConfig('toolbar'));
  const alwaysShow = computed(() => toolbarConfig.value?.alwaysShow === true);
  const autoHideDelay = computed(() => toolbarConfig.value?.autoHideDelay ?? DEFAULT_AUTO_HIDE_DELAY);

  const showToolbar = ref(true);
  const hideToolTimer = ref<number | null>(null);
  const throttleTimer = ref<number | null>(null);
  const isThrottled = ref(false);

  const clearHideTimer = () => {
    if (hideToolTimer.value !== null) {
      clearTimeout(hideToolTimer.value);
      hideToolTimer.value = null;
    }
  };

  const clearThrottleTimer = () => {
    if (throttleTimer.value !== null) {
      clearTimeout(throttleTimer.value);
      throttleTimer.value = null;
    }
  };

  const isToolbarSafeLayerElement = (target: EventTarget | null) => {
    if (!(target instanceof Element)) {
      return false;
    }
    return Boolean(target.closest(TOOLBAR_SAFE_LAYER_SELECTOR));
  };

  const setHideTimer = () => {
    if (alwaysShow.value) {
      return;
    }
    clearHideTimer();
    hideToolTimer.value = window.setTimeout(() => {
      showToolbar.value = false;
    }, autoHideDelay.value);
  };

  const revealToolbar = () => {
    showToolbar.value = true;
    setHideTimer();
  };

  const handleMouseEnter = () => {
    revealToolbar();
  };

  const handleMouseMoveThrottled = (event?: MouseEvent) => {
    // 靠近左右两侧或底部时始终唤出控制条
    if (event && typeof window !== 'undefined') {
      const fromBottom = window.innerHeight - event.clientY;
      const fromLeft = event.clientX;
      const fromRight = window.innerWidth - event.clientX;
      if (fromBottom <= 96 || fromLeft <= 88 || fromRight <= 88) {
        revealToolbar();
        return;
      }
    }
    if (isThrottled.value) {
      return;
    }
    revealToolbar();
    isThrottled.value = true;
    throttleTimer.value = window.setTimeout(() => {
      isThrottled.value = false;
    }, mouseMoveThrottle);
  };

  const handleClick = () => {
    if (alwaysShow.value) {
      return;
    }
    revealToolbar();
  };

  const handleMouseLeave = (event: MouseEvent) => {
    if (alwaysShow.value) {
      return;
    }
    // iframe 内 relatedTarget 常为 null，勿立刻隐藏，改走延时
    if (isToolbarSafeLayerElement(event.relatedTarget)) {
      return;
    }
    setHideTimer();
  };

  const handleWindowMouseMove = (event: MouseEvent) => {
    handleMouseMoveThrottled(event);
  };

  const addEventListeners = () => {
    const container = containerRef.value;
    if (container) {
      container.addEventListener('mouseenter', handleMouseEnter);
      container.addEventListener('mousemove', handleMouseMoveThrottled as EventListener);
      container.addEventListener('mouseleave', handleMouseLeave);
      container.addEventListener('click', handleClick);
    }
    // 嵌入渔业 iframe 时，用 window 级移动保证能唤出底栏
    window.addEventListener('mousemove', handleWindowMouseMove, { passive: true });
    window.addEventListener('mousedown', handleClick);
  };

  const removeEventListeners = () => {
    const container = containerRef.value;
    if (container) {
      container.removeEventListener('mouseenter', handleMouseEnter);
      container.removeEventListener('mousemove', handleMouseMoveThrottled as EventListener);
      container.removeEventListener('mouseleave', handleMouseLeave);
      container.removeEventListener('click', handleClick);
    }
    window.removeEventListener('mousemove', handleWindowMouseMove);
    window.removeEventListener('mousedown', handleClick);
  };

  watch(alwaysShow, (newVal) => {
    if (newVal) {
      clearHideTimer();
      clearThrottleTimer();
      showToolbar.value = true;
    } else {
      setHideTimer();
    }
  });

  onMounted(() => {
    nextTick(() => {
      addEventListeners();
      // 进房先展示底栏数秒，避免用户以为「功能没了」
      showToolbar.value = true;
      if (!alwaysShow.value) {
        setHideTimer();
      }
    });
  });

  onUnmounted(() => {
    clearHideTimer();
    clearThrottleTimer();
    removeEventListeners();
  });

  return {
    showToolbar,
    revealToolbar,
  };
}
