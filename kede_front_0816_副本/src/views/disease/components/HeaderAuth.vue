<template>
  <div class="header-auth">
    <div class="header-date">{{ todayText }}</div>
    <div class="auth-actions">
      <template v-if="loginDisabled">
        <span class="auth-guest">访客模式</span>
      </template>
      <template v-else-if="loggedIn">
        <button type="button" class="auth-user-btn" @click.stop="menuOpen = !menuOpen">
          <span class="auth-avatar">{{ avatarText }}</span>
          <span class="auth-name">{{ displayName }}</span>
          <i class="el-icon-arrow-down" :class="{ open: menuOpen }" />
        </button>
        <div v-if="menuOpen" class="auth-menu" @click.stop>
          <div class="auth-menu-meta">
            <div class="auth-menu-name">{{ currentUser.username }}</div>
            <div class="auth-menu-email">{{ currentUser.email }}</div>
          </div>
          <!-- 注册与登录已关闭
          <button type="button" class="auth-menu-item" @click="goLogin">
            <i class="el-icon-refresh" /> 切换账号
          </button>
          <button type="button" class="auth-menu-item" @click="goRegister">
            <i class="el-icon-plus" /> 注册新账号
          </button>
          -->
          <button type="button" class="auth-menu-item danger" @click="handleLogout">
            <i class="el-icon-switch-button" /> 退出登录
          </button>
        </div>
      </template>
      <!-- 注册与登录入口已关闭
      <template v-else>
        <router-link class="auth-link" :to="{ path: '/login', query: { mode: 'login' } }">登录</router-link>
        <span class="auth-sep">|</span>
        <router-link class="auth-link primary" :to="{ path: '/login', query: { mode: 'register' } }">注册</router-link>
      </template>
      -->
    </div>
  </div>
</template>

<script>
import { logout as logoutApi } from '../api'
import {
  clearAuth,
  getAvatarText,
  getDisplayName,
  getUser,
  isLoggedIn,
  LOGIN_REGISTER_DISABLED,
  ensureGuestAuth
} from '@/utils/disease_auth'

export default {
  name: 'HeaderAuth',
  data() {
    return {
      menuOpen: false,
      currentUser: getUser() || {}
    }
  },
  computed: {
    loginDisabled() {
      return LOGIN_REGISTER_DISABLED
    },
    loggedIn() {
      return isLoggedIn()
    },
    displayName() {
      return getDisplayName(this.currentUser)
    },
    avatarText() {
      return getAvatarText(this.currentUser)
    },
    todayText() {
      const now = new Date()
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日${weekDays[now.getDay()]}`
    }
  },
  watch: {
    $route() {
      this.currentUser = getUser() || {}
      this.menuOpen = false
    }
  },
  mounted() {
    if (LOGIN_REGISTER_DISABLED) ensureGuestAuth()
    this.currentUser = getUser() || {}
    document.addEventListener('click', this.closeMenu)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.closeMenu)
  },
  methods: {
    closeMenu() {
      this.menuOpen = false
    },
    /* 注册与登录已关闭
    goLogin() {
      this.menuOpen = false
      clearAuth()
      this.$router.push({ path: '/login', query: { mode: 'login' } })
    },
    goRegister() {
      this.menuOpen = false
      clearAuth()
      this.$router.push({ path: '/login', query: { mode: 'register' } })
    },
    */
    async handleLogout() {
      this.menuOpen = false
      if (LOGIN_REGISTER_DISABLED) {
        this.$message.info('登录功能已关闭')
        return
      }
      try {
        await logoutApi()
      } catch (e) {
        // ignore
      }
      clearAuth()
      this.$message.success('已退出登录')
      this.$router.replace({ path: '/login', query: { mode: 'login' } })
    }
  }
}
</script>

<style scoped>
.header-auth {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.header-date {
  font-size: 14px;
  color: #86909c;
  white-space: nowrap;
  line-height: 1.4;
}

.auth-actions {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
}

.auth-guest {
  font-size: 13px;
  color: #86909c;
}

.auth-link {
  font-size: 13px;
  color: #4e5969;
  text-decoration: none;
  line-height: 1;
}

.auth-link:hover {
  color: #1890ff;
}

.auth-link.primary {
  color: #1890ff;
  font-weight: 500;
}

.auth-sep {
  color: #d0d3d9;
  font-size: 12px;
}

.auth-user-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #e5e6eb;
  background: #fff;
  border-radius: 999px;
  padding: 4px 10px 4px 4px;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
}

.auth-user-btn:hover {
  border-color: #c9cdd4;
  background: #f7f8fa;
}

.auth-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #1890ff;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.auth-name {
  font-size: 13px;
  color: #1d2129;
  font-weight: 500;
}

.auth-user-btn .el-icon-arrow-down {
  font-size: 12px;
  color: #c9cdd4;
  transition: transform 0.2s;
}

.auth-user-btn .el-icon-arrow-down.open {
  transform: rotate(180deg);
}

.auth-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 200px;
  background: #fff;
  border: 1px solid #eef0f3;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  padding: 8px;
  z-index: 30;
}

.auth-menu-meta {
  padding: 10px 12px;
  border-bottom: 1px solid #eef0f3;
  margin-bottom: 6px;
}

.auth-menu-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}

.auth-menu-email {
  margin-top: 2px;
  font-size: 12px;
  color: #86909c;
  word-break: break-all;
}

.auth-menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
  border: none;
  background: transparent;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
  color: #1d2129;
  cursor: pointer;
  text-align: left;
}

.auth-menu-item:hover {
  background: #f2f3f5;
}

.auth-menu-item.danger {
  color: #f53f3f;
}

.auth-menu-item i {
  font-size: 15px;
  color: inherit;
  opacity: 0.75;
}
</style>
