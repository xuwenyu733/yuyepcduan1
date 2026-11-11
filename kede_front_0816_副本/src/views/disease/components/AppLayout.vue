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
import { logout as logoutApi } from '../api'
import {
  clearAuth,
  getAvatarText,
  getDisplayName,
  getUser,
  hasRole,
  LOGIN_REGISTER_DISABLED,
  ensureGuestAuth
} from '@/utils/disease_auth'

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
  background: #f7f8fa;
}

.nav-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
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
  width: 260px;
  max-width: 84vw;
  background: #fff;
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
}

.sidebar.open {
  transform: translateX(0);
  box-shadow: 8px 0 24px rgba(0, 0, 0, 0.12);
}

.sidebar-brand {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 28px 20px 24px 24px;
}

.brand-text {
  min-width: 0;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
  color: #1890ff;
  line-height: 1.3;
}

.brand-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #86909c;
  line-height: 1.4;
}

.nav-close {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #86909c;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.15s, color 0.15s;
}

.nav-close:hover {
  background: #f2f3f5;
  color: #1d2129;
}

.sidebar-nav {
  flex: 1;
  padding: 8px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: background 0.15s, color 0.15s;
}

.nav-label {
  line-height: 1.4;
}

.nav-item:hover {
  color: #409eff;
  background: #f5f7fa;
}

.nav-item.active {
  color: #409eff;
  background: #ecf5ff;
  font-weight: 500;
}

.sidebar-user {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #eef0f3;
  cursor: pointer;
  transition: background 0.15s;
}

.sidebar-user:hover {
  background: #f7f8fa;
}

.user-caret {
  margin-left: auto;
  color: #c9cdd4;
  font-size: 12px;
  transition: transform 0.2s;
}

.user-caret.open {
  transform: rotate(180deg);
  color: #86909c;
}

.user-menu {
  position: absolute;
  left: 12px;
  right: 12px;
  bottom: calc(100% + 8px);
  background: #fff;
  border: 1px solid #eef0f3;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  padding: 8px;
  z-index: 20;
}

.user-menu-meta {
  padding: 10px 12px 8px;
  border-bottom: 1px solid #eef0f3;
  margin-bottom: 6px;
}

.user-menu-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}

.user-menu-role {
  margin-top: 2px;
  font-size: 12px;
  color: #86909c;
}

.user-menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
  border: none;
  background: transparent;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #1d2129;
  cursor: pointer;
  text-align: left;
}

.user-menu-item:hover {
  background: #f2f3f5;
}

.user-menu-item i {
  font-size: 16px;
  color: #86909c;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #1890ff;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-info {
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1d2129;
  line-height: 1.3;
}

.user-email {
  margin-top: 2px;
  font-size: 12px;
  color: #86909c;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.main-wrap {
  flex: 1;
  min-width: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #eef0f3;
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
  color: #86909c;
  margin-bottom: 8px;
  line-height: 22px;
}

.breadcrumb .sep {
  margin: 0 8px;
  color: #c9cdd4;
}

.breadcrumb .current {
  color: #4e5969;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1d2129;
  line-height: 28px;
}

.app-main {
  flex: 1;
  padding: 24px;
  overflow: auto;
}

.app-main.is-full {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.app-main.is-module {
  padding: 0;
  background: #f7f8fa;
}
</style>
