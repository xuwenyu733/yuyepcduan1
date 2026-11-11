<template>
  <div class="fish-disease-native">
    <div class="app-layout">
      <aside class="sidebar">
        <div class="sidebar-brand">
          <div class="brand-text">
            <div class="brand-title">循环水养殖</div>
            <div class="brand-sub">病害诊断系统</div>
          </div>
          <button
            type="button"
            class="exit-btn"
            title="返回系统总览"
            @click="exitToOverview"
          >
            <i class="el-icon-switch-button" />
            <span>退出</span>
          </button>
        </div>
        <nav class="sidebar-nav">
          <button
            v-for="item in menus"
            :key="item.key"
            type="button"
            class="nav-item"
            :class="{ active: active === item.key }"
            @click="switchNav(item.key)"
          >
            <nav-icon :name="item.icon" />
            <span class="nav-label">{{ item.label }}</span>
          </button>
        </nav>
      </aside>

      <div class="main-wrap">
        <main class="app-main is-module">
          <keep-alive>
            <component :is="activeComp" />
          </keep-alive>
        </main>
      </div>
    </div>
  </div>
</template>

<script>
import NavIcon from './components/NavIcon.vue'
import Dashboard from './Dashboard.vue'
import BasicConfig from './BasicConfig.vue'
import DataCollection from './DataCollection.vue'
import DiagnosisChat from './DiagnosisChat.vue'
import CaseArchive from './CaseArchive.vue'
import SystemManage from './SystemManage.vue'
import Monitor from './Monitor.vue'
import KnowledgeBase from './KnowledgeBase.vue'
import KnowledgeManage from './KnowledgeManage.vue'
import KnowledgeTree from './KnowledgeTree.vue'
import { ensureGuestAuth, LOGIN_REGISTER_DISABLED, hasRole, getUser } from './utils/auth'
import { takePendingMedia as takeStoredPendingMedia } from './utils/handoff'
import './fish-theme.css'

const COMP_MAP = {
  dashboard: 'Dashboard',
  config: 'BasicConfig',
  collect: 'DataCollection',
  diagnosis: 'DiagnosisChat',
  archive: 'CaseArchive',
  monitor: 'Monitor',
  knowledge: 'KnowledgeBase',
  knowledgeManage: 'KnowledgeManage',
  knowledgeTree: 'KnowledgeTree',
  system: 'SystemManage'
}

const PLATFORM_ADMIN_RE = /^(admin|super_admin|super_administrator|tenant_admin|administrator|expert|ke_admin|kede_group_leader)$/i

const DEFAULT_EXIT = '/index'

export default {
  name: 'FishDiseaseShell',
  components: {
    NavIcon,
    Dashboard,
    BasicConfig,
    DataCollection,
    DiagnosisChat,
    CaseArchive,
    Monitor,
    KnowledgeBase,
    KnowledgeManage,
    KnowledgeTree,
    SystemManage
  },
  provide() {
    return {
      openNav: () => {},
      closeNav: () => {},
      toggleNav: () => {},
      getCurrentUser: () => this.currentUser,
      switchFishNav: (key, opts) => this.switchNav(key, opts),
      takePendingDiagnosisCaseId: () => {
        const id = this.pendingDiagnosisCaseId
        this.pendingDiagnosisCaseId = null
        return id
      },
      takePendingDiagnosisTab: () => {
        const tab = this.pendingDiagnosisTab
        this.pendingDiagnosisTab = null
        return tab
      },
      takePendingPondCode: () => {
        const code = this.pendingPondCode
        this.pendingPondCode = null
        return code
      },
      peekPendingPondCode: () => this.pendingPondCode,
      takePendingMedia: () => takeStoredPendingMedia()
    }
  },
  data() {
    return {
      active: 'dashboard',
      currentUser: {},
      pendingDiagnosisCaseId: null,
      pendingDiagnosisTab: null,
      pendingPondCode: null,
      exitFrom: DEFAULT_EXIT,
      allMenus: [
        { key: 'dashboard', label: '总览', icon: 'collect', roles: ['admin', 'expert', 'worker'] },
        { key: 'config', label: '基础配置', icon: 'config', roles: ['admin', 'expert'] },
        { key: 'collect', label: '数据采集', icon: 'collect', roles: ['admin', 'expert', 'worker'] },
        { key: 'diagnosis', label: '协同诊断', icon: 'diagnosis', roles: ['admin', 'expert', 'worker'] },
        { key: 'archive', label: '案例归档', icon: 'archive', roles: ['admin', 'expert', 'worker'] },
        { key: 'monitor', label: 'AI报警监控', icon: 'collect', roles: ['admin', 'expert', 'worker'] },
        { key: 'knowledge', label: '知识库', icon: 'archive', roles: ['admin', 'expert', 'worker'] },
        // 知识库管理页：仅管理员/专家，方便改删已有数据；不对普通用户开放
        { key: 'knowledgeManage', label: '知识库管理', icon: 'config', roles: ['admin', 'expert'], adminOnly: true },
        // 123：知识树 CRUD，仅管理员/专家；不对普通用户开放
        // 平台路由 /knowledge/zk-konwledge-tree ；侧栏 /fish-disease?tab=knowledgeTree
        { key: 'knowledgeTree', label: '知识树', icon: 'archive', roles: ['admin', 'expert'], adminOnly: true }
        // 系统管理入口已按需求移除
      ]
    }
  },
  computed: {
    isPlatformAdmin() {
      const roles = (this.$store && this.$store.getters && this.$store.getters.roles) || []
      if (roles.length) return roles.some(r => PLATFORM_ADMIN_RE.test(String(r)))
      return hasRole('admin', 'expert')
    },
    menus() {
      return this.allMenus.filter(item => {
        if (item.adminOnly && !this.isPlatformAdmin) return false
        if (!item.roles || !item.roles.length) return true
        if (this.isPlatformAdmin) return true
        return hasRole(...item.roles)
      })
    },
    activeComp() {
      return COMP_MAP[this.active] || 'BasicConfig'
    }
  },
  watch: {
    '$route.query': {
      deep: true,
      handler() {
        this.applyRouteQuery()
      }
    }
  },
  created() {
    if (LOGIN_REGISTER_DISABLED) ensureGuestAuth()
    this.currentUser = getUser() || {}
    if (this.$route && this.$route.meta) {
      this.$set(this.$route.meta, 'appMainOverflow', 'hidden')
    }
    this.applyRouteQuery()
  },
  beforeDestroy() {
    if (this.$route && this.$route.meta) {
      this.$delete(this.$route.meta, 'appMainOverflow')
    }
  },
  methods: {
    applyRouteQuery() {
      const q = (this.$route && this.$route.query) || {}
      if (q.from) {
        const from = String(q.from)
        this.exitFrom = from.startsWith('/') ? from : `/${from}`
      }
      if (q.pondCode) {
        this.pendingPondCode = String(q.pondCode)
      }
      const tab = q.tab ? String(q.tab) : null
      const opts = {}
      if (q.caseId) opts.caseId = String(q.caseId)
      if (q.pondCode) opts.pondCode = String(q.pondCode)
      if (tab && COMP_MAP[tab]) {
        this.switchNav(tab, opts)
      } else if (opts.caseId || opts.pondCode) {
        // 仅有池号/病例时默认进诊断
        if (opts.caseId) this.switchNav('diagnosis', opts)
      }
    },
    switchNav(key, opts) {
      if (key === 'system') {
        this.$message && this.$message.info('系统管理入口已关闭')
        return
      }
      if (!COMP_MAP[key]) return
      const allowed = this.menus.some(item => item.key === key)
      if (!allowed && key !== 'dashboard') {
        this.$message && this.$message.warning('暂无权限访问该页面')
        return
      }
      if (opts && opts.caseId) {
        this.pendingDiagnosisCaseId = opts.caseId
      }
      if (opts && opts.tab) {
        this.pendingDiagnosisTab = String(opts.tab)
      }
      if (opts && opts.pondCode) {
        this.pendingPondCode = String(opts.pondCode)
      }
      this.active = key
    },
    /** 返回平台首页（固定 /index，不再回到大屏） */
    exitToOverview() {
      this.$router.push(DEFAULT_EXIT)
    }
  }
}
</script>

<style scoped>
.fish-disease-native {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background: #0a1520;
}

.app-layout {
  display: flex;
  height: 100%;
  background: #0a1520;
}

.sidebar {
  width: 200px;
  flex-shrink: 0;
  height: 100%;
  background: #0a1520;
  border-right: 1px solid rgba(124, 143, 161, 0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-brand {
  padding: 16px 14px 14px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.14);
}

.brand-text {
  margin-bottom: 12px;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.3;
}

.brand-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #7c8fa1;
}

.exit-btn {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid rgba(124, 143, 161, 0.22);
  background: rgba(255, 255, 255, 0.03);
  color: #7c8fa1;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.15s ease, border-color 0.15s ease, background 0.15s ease;
}

.exit-btn:hover {
  color: #fff;
  border-color: rgba(64, 158, 255, 0.45);
  background: rgba(64, 158, 255, 0.12);
}

.sidebar-nav {
  flex: 1;
  padding: 8px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 8px;
  border: 1px solid transparent;
  color: #7c8fa1;
  background: transparent;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, border-color 0.2s;
}

.nav-item:hover {
  color: #ffffff;
  background: rgba(64, 158, 255, 0.1);
}

.nav-item.active {
  color: #ffffff;
  background: rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.5);
  font-weight: 500;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background-color: #409eff;
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(64, 158, 255, 0.6);
}

.main-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #0a1520;
}

.app-main {
  flex: 1;
  min-height: 0;
  overflow: auto;
  background: #0a1520;
}

.app-main.is-module {
  padding: 0;
}
</style>
