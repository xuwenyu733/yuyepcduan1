<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <transition name="sidebarLogoFade">
      <div v-if="collapse" key="collapse" class="sidebar-logo-link">
        <div class="logo-icon-collapse">
          <svg viewBox="0 0 64 64" class="fish-logo">
            <!-- 鱼身体 -->
            <ellipse cx="32" cy="32" rx="18" ry="12" fill="#409EFF" opacity="0.9"/>
            <!-- 鱼头 -->
            <ellipse cx="20" cy="32" rx="8" ry="10" fill="#409EFF" opacity="0.9"/>
            <!-- 鱼眼睛 -->
            <circle cx="18" cy="30" r="2.5" fill="#ffffff"/>
            <circle cx="18" cy="30" r="1.5" fill="#409EFF"/>
            <!-- 鱼尾 -->
            <path d="M 50 32 L 58 24 L 58 28 L 54 32 L 58 36 L 58 40 Z" fill="#409EFF" opacity="0.9"/>
            <!-- 鱼鳍（上） -->
            <ellipse cx="32" cy="22" rx="6" ry="4" fill="#409EFF" opacity="0.8" transform="rotate(-20 32 22)"/>
            <!-- 鱼鳍（下） -->
            <ellipse cx="32" cy="42" rx="6" ry="4" fill="#409EFF" opacity="0.8" transform="rotate(20 32 42)"/>
          </svg>
        </div>
      </div>
      <div v-else key="expand" class="sidebar-logo-link">
        <div class="logo-section">
          <div class="logo-icon">
            <svg viewBox="0 0 64 64" class="fish-logo">
              <!-- 鱼身体 -->
              <ellipse cx="32" cy="32" rx="18" ry="12" fill="#409EFF" opacity="0.9"/>
              <!-- 鱼头 -->
              <ellipse cx="20" cy="32" rx="8" ry="10" fill="#409EFF" opacity="0.9"/>
              <!-- 鱼眼睛 -->
              <circle cx="18" cy="30" r="2.5" fill="#ffffff"/>
              <circle cx="18" cy="30" r="1.5" fill="#409EFF"/>
              <!-- 鱼尾 -->
              <path d="M 50 32 L 58 24 L 58 28 L 54 32 L 58 36 L 58 40 Z" fill="#409EFF" opacity="0.9"/>
              <!-- 鱼鳍（上） -->
              <ellipse cx="32" cy="22" rx="6" ry="4" fill="#409EFF" opacity="0.8" transform="rotate(-20 32 22)"/>
              <!-- 鱼鳍（下） -->
              <ellipse cx="32" cy="42" rx="6" ry="4" fill="#409EFF" opacity="0.8" transform="rotate(20 32 42)"/>
            </svg>
          </div>
          <div class="logo-text">
            <h1 class="platform-title">智慧渔业</h1>
            <p class="platform-subtitle">Smart Aquaculture Platform</p>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.png'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables;
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme
    }
  },
  data() {
    return {
      title: '养殖智慧平台',
      logo: logoImg
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  min-height: 60px;
  background: #0a1520;
  overflow: hidden;
  padding: 15px 20px;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    text-decoration: none;
    cursor: default;
  }

  .logo-section {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;
  }

  .logo-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.2), rgba(64, 158, 255, 0.1));
    border: 2px solid rgba(64, 158, 255, 0.25);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .fish-logo {
      width: 28px;
      height: 28px;
    }
  }

  .logo-icon-collapse {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    .fish-logo {
      width: 32px;
      height: 32px;
    }
  }

  .logo-text {
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex: 1;
    min-width: 0;
  }

  .platform-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #ffffff;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .platform-subtitle {
    margin: 2px 0 0 0;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &.collapse {
    padding: 15px 10px;
    
    .logo-icon-collapse {
      .fish-logo {
        width: 32px;
        height: 32px;
      }
    }
  }
}
</style>
