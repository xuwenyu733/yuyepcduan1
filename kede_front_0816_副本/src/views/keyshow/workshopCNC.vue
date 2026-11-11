<template>
  <div class="workshop-cnc-page">
    <div v-if="!boardReady" class="cnc-boot">
      <div class="cnc-boot-card">车间数控加载中…</div>
    </div>
    <IndexDaping4 v-else embedded />
  </div>
</template>

<script>
export default {
  // 与路由 name 一致，便于 keep-alive 二次进入不重载
  name: '车间数控',
  components: {
    // 异步分包：切页先出壳，再拉大屏 chunk，避免同步解析卡死主线程
    IndexDaping4: () => import(/* webpackChunkName: "index-daping4" */ '@/views/index_daping4.vue')
  },
  data() {
    return {
      boardReady: false
    }
  },
  beforeCreate() {
    // 尽早收起侧栏，避免与大屏同帧抢布局
    try {
      this._prevSidebarOpened = this.$store.state.app.sidebar.opened
      if (this._prevSidebarOpened) {
        this.$store.dispatch('app/closeSideBar', { withoutAnimation: true })
      }
    } catch (e) { /* ignore */ }
  },
  mounted() {
    // 等侧栏收起后的两帧再挂载重组件，减少「卡一下」的闪缩
    const go = () => {
      if (this._isDestroyed) return
      this.boardReady = true
    }
    if (typeof requestAnimationFrame === 'function') {
      requestAnimationFrame(() => requestAnimationFrame(go))
    } else {
      setTimeout(go, 32)
    }
  },
  beforeDestroy() {
    this._isDestroyed = true
    if (this._prevSidebarOpened && !this.$store.state.app.sidebar.opened) {
      this.$store.dispatch('app/toggleSideBar')
    }
  }
}
</script>

<style lang="scss" scoped>
.workshop-cnc-page {
  height: 100%;
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #010409;

  .cnc-boot {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 0;
  }

  .cnc-boot-card {
    color: #8fa8c4;
    font-size: 14px;
    letter-spacing: 0.04em;
    padding: 12px 18px;
    border: 1px solid rgba(64, 130, 200, 0.25);
    border-radius: 8px;
    background: rgba(10, 22, 40, 0.9);
  }

  ::v-deep .dashboard-wrapper {
    flex: 1;
    width: 100%;
    height: 100% !important;
    max-height: 100%;
    min-height: 0;
  }

  ::v-deep .right-panel.side-panel,
  ::v-deep .right-panel {
    display: grid !important;
    grid-template-rows: minmax(0, 28%) minmax(0, 1fr) minmax(140px, 28%) !important;
    height: 100%;
    min-height: 0;
    overflow: hidden;
  }

  ::v-deep .task-card .card-body,
  ::v-deep .support-card .support-list {
    overflow-y: auto !important;
    min-height: 0;
  }
}
</style>
