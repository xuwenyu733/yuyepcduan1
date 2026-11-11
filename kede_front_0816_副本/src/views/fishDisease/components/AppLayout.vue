<template>
  <div class="app-layout">
    <div
      class="nav-backdrop"
      :class="{ open: menuOpen }"
      @click="closeNav"
    />

    <aside class="sidebar" :class="{ open: menuOpen }">
      <div class="sidebar-brand">
        <div class="brand-text">
          <div class="brand-title">循环水养殖</div>
          <div class="brand-sub">病害诊断系统</div>
        </div>
        <button type="button" class="nav-close" aria-label="关闭导航菜单" @click="closeNav">
          <i class="el-icon-close" />
        </button>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menus"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
          @click.native="closeNav"
        >
          <NavIcon :name="item.icon" />
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- 侧栏底部用户信息 / 退出登录 已移除
      <div class="sidebar-user" @click.stop="userMenuOpen = !userMenuOpen">
        <div class="user-avatar">{{ avatarText }}</div>
        <div class="user-info">
          <div class="user-name">{{ displayName }}</div>
          <div class="user-email">{{ currentUser.email || '—' }}</div>
        </div>
        <i class="el-icon-arrow-up user-caret" :class="{ open: userMenuOpen }" />

        <div v-if="userMenuOpen" class="user-menu" @click.stop>
          <div class="user-menu-meta">
            <div class="user-menu-name">{{ currentUser.username }}</div>
            <div class="user-menu-role">{{ currentUser.roleLabel }}</div>
          </div>
          <button type="button" class="user-menu-item" @click="handleLogout">
            <i class="el-icon-switch-button" />
            退出登录
          </button>
        </div>
      </div>
      -->
    </aside>

    <div class="main-wrap">
      <header v-if="!isFullPage && !hideHeader" class="page-header">
        <div class="header-row">
          <MenuToggle />
          <div class="header-text">
            <div class="breadcrumb">
              <span v-for="(crumb, idx) in breadcrumbs" :key="idx">
                <span v-if="idx > 0" class="sep">/</span>
                <span :class="{ current: idx === breadcrumbs.length - 1 }">{{ crumb }}</span>
              </span>
            </div>
            <h1 class="page-title">{{ pageTitle }}</h1>
          </div>
        </div>
      </header>
      <main class="app-main" :class="{ 'is-full': isFullPage, 'is-module': hideHeader }">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import NavIcon from './NavIcon.vue'
import MenuToggle from './MenuToggle.vue'
import { logout as logoutApi } from '@/api/fishDisease'
import {
  clearAuth,
  getAvatarText,
  getDisplayName,
  getUser,
  hasRole,
  LOGIN_REGISTER_DISABLED,
  ensureGuestAuth
} from '@/views/fishDisease/utils/auth'

const routeMeta = {
  '/dashboard': { title: '首页', crumbs: ['主页', '首页'], full: false },
  '/monitor': { title: '病情监测', crumbs: ['主页', '病情监测'], full: false },
  '/diagnosis': { title: '协同诊断', crumbs: ['主页', '协同诊断'], full: false, hideHeader: true },
  '/history': { title: '诊断历史', crumbs: ['主页', '病情监测', '诊断历史'], full: false },
  '/knowledge': { title: '知识库', crumbs: ['主页', '知识库'], full: false },
  '/collect': { title: '数据采集', crumbs: ['主页', '数据采集'], full: false, hideHeader: true },
  '/config': { title: '基础配置', crumbs: ['主页', '基础配置'], full: false, hideHeader: true },
  '/archive': { title: '案例归档', crumbs: ['主页', '案例归档'], full: false, hideHeader: true },
  '/system': { title: '系统管理', crumbs: ['主页', '系统管理'], full: false, hideHeader: true }
}

export default {
  name: 'AppLayout',
  components: { NavIcon, MenuToggle },
  provide() {
    return {
      openNav: this.openNav,
      closeNav: this.closeNav,
      toggleNav: this.toggleNav,
      getCurrentUser: () => this.currentUser
    }
  },
  data() {
    return {
      menuOpen: false,
      userMenuOpen: false,
      currentUser: getUser() || {},
      allMenus: [
        { path: '/config', label: '基础配置', icon: 'config', roles: ['admin', 'expert'] },
        { path: '/collect', label: '数据采集', icon: 'collect', roles: ['admin', 'expert', 'worker'] },
        { path: '/diagnosis', label: '协同诊断', icon: 'diagnosis', roles: ['admin', 'expert', 'worker'] },
        { path: '/archive', label: '案例归档', icon: 'archive', roles: ['admin', 'expert', 'worker'] }
        // 系统管理入口已按需求移除
      ]
    }
  },
  computed: {
    meta() {
      return routeMeta[this.$route.path] || routeMeta['/config']
    },
    pageTitle() {
      return this.meta.title
    },
    breadcrumbs() {
      return this.meta.crumbs
    },
    isFullPage() {
      return this.meta.full
    },
    hideHeader() {
      return this.meta.hideHeader
    },
    displayName() {
      return getDisplayName(this.currentUser)
    },
    avatarText() {
      return getAvatarText(this.currentUser)
    },
    menus() {
      return this.allMenus.filter(item => {
        if (!item.roles || !item.roles.length) return true
        return hasRole(...item.roles)
      })
    }
  },
  watch: {
    $route() {
      this.closeNav()
      this.userMenuOpen = false
      this.currentUser = getUser() || {}
    }
  },
  mounted() {
    if (LOGIN_REGISTER_DISABLED) ensureGuestAuth()
    this.currentUser = getUser() || {}
    document.addEventListener('click', this.closeUserMenu)
  },
  beforeDestroy() {
    document.body.style.overflow = ''
    document.removeEventListener('click', this.closeUserMenu)
  },
  methods: {
    isActive(path) {
      return this.$route.path === path || this.$route.path.startsWith(`${path}/`)
    },
    openNav() {
      this.menuOpen = true
      document.body.style.overflow = 'hidden'
    },
    closeNav() {
      this.menuOpen = false
      document.body.style.overflow = ''
    },
    toggleNav() {
      if (this.menuOpen) this.closeNav()
      else this.openNav()
    },
    closeUserMenu() {
      this.userMenuOpen = false
    },
    async handleLogout() {
      this.userMenuOpen = false
      this.closeNav()
      // 注册与登录已关闭
      if (LOGIN_REGISTER_DISABLED) {
        this.$message.info('登录功能已关闭')
        return
      }
      try {
        await logoutApi()
      } catch (e) {
        // 即使接口失败也清理本地凭证
      }
      clearAuth()
      this.$message.success('已退出登录')
      this.$router.replace('/login')
    }
  }
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  background: #0a1520;
}

.nav-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.25s ease, visibility 0.25s ease;
  z-index: 200;
}

.nav-backdrop.open {
  opacity: 1;
  visibility: visible;
}

.sidebar {
  width: 200px;
  max-width: 84vw;
  background: #0a1520;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 210;
  transform: translateX(-100%);
  transition: transform 0.28s ease;
  box-shadow: none;
  border-right: 1px solid rgba(124, 143, 161, 0.18);
}

.sidebar.open {
  transform: translateX(0);
  box-shadow: 8px 0 24px rgba(0, 0, 0, 0.45);
}

.sidebar-brand {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 24px 16px 20px;
}

.brand-text {
  min-width: 0;
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
  line-height: 1.4;
}

.nav-close {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #7c8fa1;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.15s, color 0.15s;
}

.nav-close:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #ffffff;
}

.sidebar-nav {
  flex: 1;
  padding: 8px 8px 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow-y: auto;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  margin: 0;
  border-radius: 8px;
  border: 1px solid transparent;
  color: #7c8fa1;
  text-decoration: none;
  font-size: 14px;
  transition: background 0.2s, color 0.2s, border-color 0.2s;
}

.nav-label {
  line-height: 1.4;
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
  width: 100%;
  display: flex;
  flex-direction: column;
  background: #0a1520;
}

.page-header {
  background: #0f1a26;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.18);
  min-height: 86px;
}

.header-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.header-text {
  min-width: 0;
}

.breadcrumb {
  font-size: 14px;
  color: #7c8fa1;
  margin-bottom: 8px;
  line-height: 22px;
}

.breadcrumb .sep {
  margin: 0 8px;
  color: #5a6b7d;
}

.breadcrumb .current {
  color: #a8b8c8;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #e8edf2;
  line-height: 28px;
}

.app-main {
  flex: 1;
  padding: 24px;
  overflow: auto;
  background: #0a1520;
}

.app-main.is-full {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.app-main.is-module {
  padding: 0;
  background: #0a1520;
}
</style>
