<template>
  <div :class="{'has-logo':showLogo}" :style="{ backgroundColor: settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <div v-if="showLogo" class="sidebar-divider"></div>
    <el-scrollbar :class="settings.sideTheme" wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
        :text-color="settings.sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true"
        :active-text-color="settings.theme"
        :collapse-transition="false"
        mode="vertical"
      >
        <!-- 根据 sidebarRouters 路由，生成菜单 -->
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path  + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
      <div style="height: 50px; width: 100%;"></div>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import variables from "@/assets/styles/variables.scss";

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapState(["settings"]),
    ...mapGetters(["sidebarRouters", "sidebar"]),
    activeMenu() {
      const route = this.$route;
      const { meta, path } = route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo;
    },
    variables() {
      return variables;
    },
    isCollapse() {
      return !this.sidebar.opened;
    }
  }
};
</script>

<style lang="scss" scoped>
/* 深色主题菜单样式 */
::v-deep .el-menu {
  border-right: none;
  
  /* 菜单项基础样式 */
  .el-menu-item {
    color: #7C8FA1;
    background-color: transparent;
    border-radius: 8px;
    margin: 4px 8px;
    height: 44px;
    line-height: 44px;
    position: relative;
    transition: all 0.3s;
    
    &:hover {
      background-color: rgba(64, 158, 255, 0.1);
      color: #ffffff;
    }
    
    /* 选中状态样式 */
    &.is-active {
      background-color: rgba(64, 158, 255, 0.15);
      color: #ffffff;
      border: 1px solid rgba(64, 158, 255, 0.5);
      
      /* 右侧圆点指示器 */
      &::after {
        content: '';
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 6px;
        height: 6px;
        background-color: #409EFF;
        border-radius: 50%;
        box-shadow: 0 0 8px rgba(64, 158, 255, 0.6);
      }
    }
    
    /* 图标样式 */
    i {
      color: inherit;
      margin-right: 8px;
    }
  }
  
  /* 子菜单样式 */
  .el-submenu {
    .el-submenu__title {
      color: #7C8FA1;
      border-radius: 8px;
      margin: 4px 8px;
      height: 44px;
      line-height: 44px;
      transition: all 0.3s;
      
      &:hover {
        background-color: rgba(64, 158, 255, 0.1);
        color: #ffffff;
      }
      
      i {
        color: inherit;
      }
    }
    
    /* 子菜单项样式 */
    .el-menu-item {
      background-color: transparent;
      padding-left: 50px !important;
      
      &.is-active {
        background-color: rgba(64, 158, 255, 0.15);
        color: #ffffff;
        border: 1px solid rgba(64, 158, 255, 0.5);
        
        &::after {
          right: 12px;
        }
      }
    }
  }
}

/* Logo和菜单之间的分隔线 */
.sidebar-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0 12px;
}

/* 滚动条样式 */
::v-deep .scrollbar-wrapper {
  background-color: #0a1520;
  
  .el-scrollbar__bar {
    &.is-vertical {
      right: 0px;
      width: 6px;
      
      .el-scrollbar__thumb {
        background-color: rgba(255, 255, 255, 0.2);
        border-radius: 3px;
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.3);
        }
      }
    }
  }
}
</style>