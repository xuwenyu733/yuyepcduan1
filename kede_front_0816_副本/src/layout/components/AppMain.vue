<template>
  <section
    class="app-main"
    :class="{
      'app-main--no-scroll': appMainNoScroll || isExpertMeetingPath,
      'app-main--expert': isExpertMeetingPath
    }"
  >
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view v-if="!$route.meta.link && !isExpertMeetingPath" :key="key" />
      </keep-alive>
    </transition>
    <!-- 专家会议：每次切入都重建 iframe，展示科德大厅 -->
    <div v-show="isExpertMeetingPath" class="remote-meeting-container">
      <ZkMeetingExpert v-if="isRemoteMeetingOpened" :key="'zk-meet-' + remoteMeetingSession" />
    </div>
    <iframe-toggle />
  </section>
</template>

<script>
import iframeToggle from "./IframeToggle/index"
import ZkMeetingExpert from "@/views/fish/zkMeeting/remote_meeting"

function isExpertMeetingRoute(route) {
  if (!route) return false
  const path = String(route.path || '')
  const fullPath = String(route.fullPath || '')
  const title = String((route.meta && route.meta.title) || '')
  const name = String(route.name || '')
  if (title.indexOf('专家会议') !== -1) return true
  if (name.indexOf('Meeting') !== -1 && name.indexOf('Expert') !== -1) return true
  if (/zk-meeting-expert|remote_meeting|zkMeeting\/remote|zk-meeting\/remote/i.test(path)) return true
  if (/zk-meeting-expert|remote_meeting/i.test(fullPath)) return true
  if (path.indexOf('meeting') !== -1 && (path.indexOf('expert') !== -1 || title.indexOf('专家') !== -1)) return true
  const matched = route.matched || []
  for (let i = 0; i < matched.length; i++) {
    const m = matched[i]
    const metaTitle = String((m.meta && m.meta.title) || '')
    if (metaTitle.indexOf('专家会议') !== -1) return true
    if (/zk-meeting-expert|remote_meeting/i.test(String(m.path || ''))) return true
  }
  return false
}

export default {
  name: 'AppMain',
  components: { iframeToggle, ZkMeetingExpert },
  data() {
    return {
      isRemoteMeetingOpened: false,
      remoteMeetingSession: 0
    }
  },
  watch: {
    isExpertMeetingPath: {
      handler(val) {
        if (!val) return
        this.isRemoteMeetingOpened = true
        // 每次进入专家会议都重建，确保加载最新 rtc_kede（大厅：创建/加入）
        this.remoteMeetingSession = Date.now()
      },
      immediate: true
    }
  },
  computed: {
    isExpertMeetingPath() {
      return isExpertMeetingRoute(this.$route)
    },
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    },
    appMainNoScroll() {
      return !!(this.$route.meta && this.$route.meta.appMainOverflow === 'hidden')
    }
  }
}
</script>

<style lang="scss" scoped>
.remote-meeting-container {
  position: absolute;
  z-index: 2;
  /* 默认贴顶：顶栏非 fixed 时已在文档流占位，再写 84px 会多出一整块空白 */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  box-sizing: border-box;
  width: 100%;
  height: auto;
  min-height: 480px;
  overflow: hidden;
  background: #0a1520;
}

/* 仅 fixed 顶栏遮罩 app-main 时，才需要下移避开 navbar + tags */
.fixed-header + .app-main .remote-meeting-container {
  top: 84px;
}

.app-main {
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
  background-color: #0a1520;
}

.app-main.app-main--expert {
  box-sizing: border-box;
  height: 100vh;
  min-height: 100vh;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
  overflow: hidden;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    height: calc(100vh - 84px);
    min-height: calc(100vh - 84px);
    overflow: scroll;
    padding-bottom: 30px;
  }

  .app-main.app-main--no-scroll {
    overflow: hidden;
    padding-bottom: 0;
  }

  .app-main.app-main--expert {
    height: 100vh;
    min-height: 100vh;
    overflow: hidden;
    padding-top: 0 !important;
    padding-bottom: 0;
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }

  .fixed-header + .app-main.app-main--expert {
    padding-top: 0 !important;
  }
}
</style>

<style lang="scss">
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 17px;
  }
}

.hasTagsView .app-main.app-main--no-scroll .dashboard-editor-container.is-daping-home,
.hasTagsView .app-main.app-main--no-scroll .workshop-cnc-page {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>
