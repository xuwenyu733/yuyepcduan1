<template>
  <div class="dashboard-wrapper">
    <header class="header">
      <div class="header-top">
        <div class="header-left">
          <button class="nav-btn home" @click="toPage('/index_daping1')" title="返回首页">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
          </button>
          <div class="divider"></div>
          <button class="nav-btn btn-blue is-active" type="button">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
              <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
              <line x1="12" y1="22.08" x2="12" y2="12" />
            </svg>
            <span>数字孪生</span>
          </button>
          <button class="nav-btn btn-purple" @click="toPage('/index_daping2')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="m22 8-6 4 6 4V8Z" />
              <rect width="14" height="12" x="2" y="6" rx="2" ry="2" />
            </svg>
            <span>视频监控</span>
          </button>
          <button class="nav-btn btn-cyan" @click="toPage('/index')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
            </svg>
            <span>数字化监控</span>
          </button>
        </div>

        <div class="header-center">
          <div class="title-wrap">
            <h1>数字孪生</h1>
            <p>{{ viewMode === '3d' ? '3D数字孪生演示' : '厂区平面示意' }}</p>
          </div>
        </div>

        <div class="header-right">
          <button
            class="mode-btn"
            :class="{ active: viewMode === '3d' }"
            type="button"
            @mouseenter="onPrefetch"
            @click="viewMode = '3d'"
          >3D孪生</button>
          <button
            class="mode-btn"
            :class="{ active: viewMode === 'map' }"
            type="button"
            @click="viewMode = 'map'"
          >平面示意</button>
          <button
            v-if="viewMode === '3d'"
            class="mode-btn ghost"
            type="button"
            title="全屏打开（更低延迟）"
            @mouseenter="onPrefetch"
            @click="openFullTwin"
          >全屏</button>
        </div>
      </div>
    </header>

    <main class="page-main" :class="{ 'no-pad': viewMode === '3d' }">
      <!-- 与原有数字孪生页连接：默认嵌入新 3D，保留平面示意便于点车间 -->
      <iframe
        v-show="viewMode === '3d'"
        class="twin-frame"
        :src="twinSrc"
        title="3D数字孪生"
        allow="fullscreen"
      />
      <IndexDaping1Son v-show="viewMode === 'map'" />
    </main>
  </div>
</template>

<script>
import IndexDaping1Son from './index_daping1_son.vue';
import { digitalTwinBase, prefetchDigitalTwin } from '@/utils/digitalTwin';

export default {
  name: 'IndexDaping1SonPage',
  components: {
    IndexDaping1Son,
  },
  data() {
    return {
      viewMode: '3d',
      twinSrc: digitalTwinBase(),
    };
  },
  created() {
    prefetchDigitalTwin();
  },
  methods: {
    onPrefetch() {
      prefetchDigitalTwin();
    },
    openFullTwin() {
      prefetchDigitalTwin();
      window.location.assign(digitalTwinBase());
    },
    toPage(path) {
      this.$router.push({ path }).catch(() => {});
    },
  },
};
</script>

<style lang="scss" scoped>
.dashboard-wrapper {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom right, #020617, #172554, #0f172a);
  color: white;
  overflow: hidden;

  * {
    box-sizing: border-box;
  }

  .header {
    background: linear-gradient(to bottom, rgba(15, 23, 42, 0.95), rgba(15, 23, 42, 0.6));
    border-bottom: 1px solid rgba(6, 182, 212, 0.3);
    box-shadow: 0 4px 20px rgba(6, 182, 212, 0.1);
    padding: 0.4rem 1rem;
    flex-shrink: 0;
    z-index: 100;
    backdrop-filter: blur(8px);

    .header-top {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 0.75rem;
    }

    .header-left {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      flex: 1;
      min-width: 0;

      .nav-btn {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        padding: 0.4rem 0.75rem;
        border-radius: 0.5rem;
        border: none;
        cursor: pointer;
        transition: all 0.3s;
        color: white;
        font-weight: 600;
        font-size: 0.875rem;
        height: 2.25rem;

        &.home {
          background: linear-gradient(to bottom right, #2563eb, #06b6d4);
          justify-content: center;
          padding: 0;
          width: 2.25rem;
          box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
        }

        &.btn-blue {
          background: #3b82f6;
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        }

        &.btn-purple {
          background: #a855f7;
          box-shadow: 0 4px 12px rgba(168, 85, 247, 0.3);
        }

        &.btn-cyan {
          background: #06b6d4;
          box-shadow: 0 4px 12px rgba(6, 182, 212, 0.3);
        }

        &.is-active {
          outline: 2px solid rgba(255, 255, 255, 0.35);
        }

        &:hover {
          filter: brightness(1.1);
          transform: translateY(-1px);
        }

        .icon {
          width: 0.875rem;
          height: 0.875rem;
          flex-shrink: 0;
        }
      }

      .nav-btn.home .icon {
        width: 1rem;
        height: 1rem;
      }

      .divider {
        width: 1px;
        height: 1.5rem;
        background: rgba(6, 182, 212, 0.3);
      }
    }

    .header-center {
      flex: 1;
      display: flex;
      justify-content: center;

      .title-wrap {
        text-align: center;

        h1 {
          font-size: 1.25rem;
          font-weight: 700;
          margin: 0;
          background: linear-gradient(to right, #22d3ee, #3b82f6);
          -webkit-background-clip: text;
          background-clip: text;
          color: transparent;
        }

        p {
          font-size: 0.75rem;
          color: #94a3b8;
          margin: 0.15rem 0 0;
        }
      }
    }

    .header-right {
      flex: 1;
      display: flex;
      justify-content: flex-end;
      align-items: center;
      gap: 0.4rem;
      min-width: 0;
    }

    .mode-btn {
      height: 2rem;
      padding: 0 0.65rem;
      border-radius: 0.4rem;
      border: 1px solid rgba(148, 163, 184, 0.35);
      background: rgba(30, 41, 59, 0.7);
      color: #e2e8f0;
      font-size: 0.75rem;
      font-weight: 600;
      cursor: pointer;

      &.active {
        background: #2563eb;
        border-color: #3b82f6;
        color: #fff;
      }

      &.ghost {
        background: transparent;
      }

      &:hover {
        filter: brightness(1.08);
      }
    }
  }

  .page-main {
    flex: 1;
    min-height: 0;
    padding: 0.75rem 1rem 1rem;
    display: flex;
    flex-direction: column;

    &.no-pad {
      padding: 0;
    }

    .twin-frame {
      flex: 1;
      width: 100%;
      min-height: 0;
      border: 0;
      background: #020617;
    }

    :deep(.model-card) {
      flex: 1;
      min-height: 0;
    }
  }
}
</style>
