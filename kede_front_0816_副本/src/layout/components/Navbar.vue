<template>
  <div class="navbar-wrapper">
    <div class="navbar-top">
      <div class="navbar-left">
        <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
        <!-- 搜索框 -->
        <div class="search-container">
          <search id="header-search" class="header-search-wrapper" />
        </div>
      </div>
      
      <div class="navbar-right">
        <template v-if="device!=='mobile'">
          <!-- 日期时间显示 -->
          <div class="datetime-display">
            <span class="status-dot"></span>
            <span class="datetime-text">{{ currentDateTime }}</span>
          </div>

          <!-- 刷新按钮 -->
          <el-tooltip content="刷新" effect="dark" placement="bottom">
            <div class="right-menu-item hover-effect refresh-btn" @click="handleRefresh">
              <i class="el-icon-refresh"></i>
            </div>
          </el-tooltip>

          <!-- 站内信 -->
          <notify-message class="right-menu-item hover-effect" />

          <!-- 设置按钮 -->
          <el-tooltip content="设置" effect="dark" placement="bottom">
            <div class="right-menu-item hover-effect settings-btn" @click="setting = true">
              <i class="el-icon-setting"></i>
            </div>
          </el-tooltip>

          <!-- 租户下拉框 -->
          <tenant-visit v-if="tenantEnable" v-hasPermi="['system:tenant:visit']" class="right-menu-item tenant-visit-wrapper" />

          <screenfull id="screenfull" class="right-menu-item hover-effect" />

          <el-tooltip content="布局大小" effect="dark" placement="bottom" v-if="false">
            <size-select id="size-select" class="right-menu-item hover-effect" />
          </el-tooltip>
        </template>

        <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click" popper-class="user-dropdown-popper">
          <div class="avatar-wrapper">
            <img :src="avatar" class="user-avatar">
            <span v-if="nickname" class="user-nickname">{{ nickname }}</span>
            <i class="el-icon-caret-bottom" />
          </div>
          <el-dropdown-menu slot="dropdown" style="background-color: rgba(10, 21, 32, 0.95) !important; border: 1px solid rgba(64, 158, 255, 0.3) !important; border-radius: 8px !important; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6) !important; padding: 6px 0 !important;">
            <router-link to="/user/profile">
              <el-dropdown-item style="background-color: rgba(10, 21, 32, 0.95) !important; color: #fff !important; font-size: 14px !important; padding: 10px 20px !important; line-height: 1.5 !important;">
                <i class="el-icon-user" style="margin-right: 8px; color: rgba(64, 158, 255, 0.9); font-size: 15px;"></i> 个人中心
              </el-dropdown-item>
            </router-link>
            <el-dropdown-item @click.native="setting = true" style="background-color: rgba(10, 21, 32, 0.95) !important; color: #fff !important; font-size: 14px !important; padding: 10px 20px !important; line-height: 1.5 !important;">
              <i class="el-icon-setting" style="margin-right: 8px; color: rgba(64, 158, 255, 0.9); font-size: 15px;"></i> 布局设置
            </el-dropdown-item>
            <el-dropdown-item @click.native="logout" style="background-color: rgba(10, 21, 32, 0.95) !important; color: #fff !important; font-size: 14px !important; padding: 10px 20px !important; line-height: 1.5 !important; border-top: 1px solid rgba(64, 158, 255, 0.2) !important; margin-top: 4px !important; padding-top: 12px !important;">
              <i class="el-icon-switch-button" style="margin-right: 8px; color: rgba(64, 158, 255, 0.9); font-size: 15px;"></i> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    
    <!-- 分隔线 + 面包屑：开启 Tab 时与标签页重复，不再展示 -->
    <template v-if="showSecondNavRow">
      <div class="navbar-divider"></div>
      <div class="navbar-breadcrumb">
        <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav"/>
        <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav"/>
      </div>
    </template>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import RuoYiGit from '@/components/RuoYi/Git'
import RuoYiDoc from '@/components/RuoYi/Doc'
import NotifyMessage from '@/layout/components/Message'
import TenantVisit from '@/components/TenantVisit'
import {getTenantEnable} from "@/utils/ruoyi";
import { goLoginPage } from '@/utils/goLogin'

export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc,
    NotifyMessage,
    TenantVisit
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'nickname',
      'device'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    },
    tagsView() {
      return this.$store.state.settings.tagsView
    },
    /** 顶部第二行：面包屑 / 顶栏菜单。Tab 已展示当前路径时隐藏，避免重复 */
    showSecondNavRow() {
      if (this.topNav) return true
      return !this.tagsView
    },
    tenantEnable() {
      return getTenantEnable()
    }
  },
  data() {
    return {
      currentDateTime: ''
    }
  },
  mounted() {
    this.updateDateTime()
    this.timer = setInterval(() => {
      this.updateDateTime()
    }, 1000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    updateDateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      this.currentDateTime = `${year}/${month}/${day} ${hours}:${minutes}`
    },
    handleRefresh() {
      this.$router.go(0)
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      this.$modal.confirm('确定注销并退出系统吗？', '提示').then(() => {
        this.$store.dispatch('LogOut').finally(() => {
          goLoginPage()
        })
      }).catch(() => {});
    },
    checkPermi(permissions) {
      return this.$auth.hasPermi(permissions)
      return true;
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar-wrapper {
  background: #0a1520;
  box-shadow: 0 1px 4px rgba(0,0,0,.3);
}

.navbar-top {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: relative;
}

.navbar-left {
  display: flex;
  align-items: center;
  flex: 1;
  gap: 12px;
  
  .hamburger-container {
    line-height: 50px;
    height: 100%;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;
    padding: 0 10px;
    flex-shrink: 0;
    position: relative;
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      background: rgba(64, 158, 255, 0.1);
    }
    
    ::v-deep .hamburger {
      width: 20px;
      height: 20px;
      fill: #ffffff;
    }
  }
  
  .search-container {
    flex: 1;
    max-width: 400px;
    min-width: 200px;
    position: relative;
    z-index: 1;
    
    .header-search-wrapper {
      width: 100%;
    }
  }
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .datetime-display {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 6px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    
    .status-dot {
      width: 8px;
      height: 8px;
      background-color: #67C23A;
      border-radius: 50%;
      box-shadow: 0 0 6px rgba(103, 194, 58, 0.6);
    }
    
    .datetime-text {
      color: #ffffff;
      font-size: 14px;
      font-weight: 500;
    }
  }
  
  .refresh-btn,
  .settings-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    transition: all 0.3s;
    
    i {
      font-size: 18px;
      color: #ffffff;
    }
    
    &:hover {
      background: rgba(64, 158, 255, 0.15);
    }
  }
}

.navbar-divider {
  height: 1px;
  background: rgba(64, 158, 255, 0.3);
  margin: 0;
}

.navbar-breadcrumb {
  padding: 0 20px;
  height: 40px;
  display: flex;
  align-items: center;
  
  .breadcrumb-container {
    float: none;
  }

  .topmenu-container {
    position: static;
  }
}

.right-menu {
  display: flex;
  align-items: center;
  height: 100%;

  &:focus {
    outline: none;
  }

  .right-menu-item {
    display: inline-flex;
    align-items: center;
    padding: 0 8px;
    height: 36px;
    font-size: 18px;
    color: #ffffff;
    vertical-align: middle;

    &.hover-effect {
      cursor: pointer;
      transition: background .3s;
      border-radius: 6px;

      &:hover {
        background: rgba(64, 158, 255, 0.15);
      }
    }
  }

  .avatar-container {
    margin-left: 8px;

    ::v-deep .el-dropdown-menu {
      background: #000000 !important;
      border: 1px solid rgba(64, 158, 255, 0.3) !important;
      border-radius: 8px !important;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6) !important;
      padding: 6px 0 !important;
    }

    ::v-deep .el-dropdown-menu__item {
      color: rgba(255, 255, 255, 0.85) !important;
      font-size: 14px;
      padding: 10px 20px !important;
      line-height: 1.5 !important;
      transition: all 0.2s;

      i {
        margin-right: 8px;
        font-size: 15px;
        color: rgba(64, 158, 255, 0.8);
      }

      &:not(.is-disabled):hover {
        background: rgba(64, 158, 255, 0.12) !important;
        color: #409EFF !important;

        i {
          color: #409EFF;
        }
      }
    }

    ::v-deep .el-dropdown-menu__item.is-divided {
      border-top: 1px solid rgba(64, 158, 255, 0.15);
      margin-top: 4px;
      padding-top: 12px !important;

      &:hover {
        color: #F56C6C !important;

        i {
          color: #F56C6C;
        }
      }
    }

    ::v-deep .popper__arrow {
      border-bottom-color: rgba(64, 158, 255, 0.3) !important;

      &::after {
        border-bottom-color: #0a1520 !important;
      }
    }

    .avatar-wrapper {
      display: flex;
      justify-content: center;
      align-items: center;
      position: relative;
      height: 36px;
      padding: 0 8px;
      border-radius: 6px;

      img.user-avatar,
      .user-avatar {
        cursor: pointer;
        width: 22px !important;
        height: 22px !important;
        max-width: 22px !important;
        max-height: 22px !important;
        min-width: 22px !important;
        min-height: 22px !important;
        border-radius: 50%;
        object-fit: cover;
        flex-shrink: 0;
        display: block;
      }
      
      ::v-deep img.user-avatar {
        width: 22px !important;
        height: 22px !important;
        max-width: 22px !important;
        max-height: 22px !important;
      }
      .user-nickname{
        margin-left: 8px;
        font-size: 14px;
        color: #ffffff;
      }

      .el-icon-caret-bottom {
        cursor: pointer;
        margin-left: 4px;
        font-size: 12px;
        color: #ffffff;
      }
    }
  }
  
  .tenant-visit-wrapper {
    ::v-deep .el-select {
      .el-input__inner {
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 6px;
        color: #ffffff;
        height: 36px;
        line-height: 36px;
        
        &::placeholder {
          color: rgba(255, 255, 255, 0.5);
        }
      }
    }
  }
}
</style>

<style lang="scss">
.user-dropdown-popper {
  background: #0a1520 !important;
  border: 1px solid rgba(64, 158, 255, 0.3) !important;
  border-radius: 8px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6) !important;
  padding: 6px 0 !important;
  min-width: 160px;

  .popper__arrow {
    border-bottom-color: rgba(64, 158, 255, 0.3) !important;

    &::after {
      border-bottom-color: #0a1520 !important;
    }
  }

  .el-dropdown-menu {
    background: transparent !important;
    border: none !important;
    padding: 0 !important;
  }

  .el-dropdown-menu__item {
    color: rgba(255, 255, 255, 0.85) !important;
    font-size: 14px;
    padding: 10px 20px !important;
    line-height: 1.5 !important;
    transition: all 0.2s;

    i {
      margin-right: 8px;
      font-size: 15px;
      color: rgba(64, 158, 255, 0.8);
    }

    &:not(.is-disabled):hover {
      background: rgba(64, 158, 255, 0.12) !important;
      color: #409EFF !important;

      i {
        color: #409EFF;
      }
    }

    &.is-divided {
      border-top: 1px solid rgba(64, 158, 255, 0.15);
      margin-top: 4px;
      padding-top: 12px !important;

      &:hover {
        color: #F56C6C !important;

        i {
          color: #F56C6C;
        }
      }
    }
  }

  a {
    text-decoration: none;

    .el-dropdown-menu__item {
      color: rgba(255, 255, 255, 0.85) !important;
    }
  }
}
</style>
