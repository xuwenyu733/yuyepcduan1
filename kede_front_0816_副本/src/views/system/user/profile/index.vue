<template>
  <div class="profile-container">
    <!-- 顶部用户横幅 -->
    <div class="profile-banner">
      <div class="banner-bg"></div>
      <div class="banner-content">
        <div class="banner-avatar">
          <userAvatar :user="user" />
        </div>
        <div class="banner-info">
          <h2 class="banner-name">{{ user.nickname || user.username || '用户' }}</h2>
          <div class="banner-meta">
            <span class="meta-item" v-if="user.username">
              <i class="el-icon-user"></i>{{ user.username }}
            </span>
            <span class="meta-item" v-if="user.dept">
              <i class="el-icon-office-building"></i>{{ user.dept.name }}
            </span>
            <span class="meta-item" v-if="user.roles && user.roles.length">
              <i class="el-icon-medal"></i>{{ user.roles.map(role => role.name).join('、') }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <el-row :gutter="20" class="profile-row">
      <!-- 左侧:个人信息卡片 -->
      <el-col :span="8" :xs="24">
        <div class="info-card">
          <div class="card-header">
            <span class="header-title">
              <i class="el-icon-info"></i>个人信息
            </span>
          </div>
          <div class="card-body">
            <ul class="info-list">
              <li class="info-item">
                <span class="item-label">
                  <i class="el-icon-user"></i>用户名称
                </span>
                <span class="item-value">{{ user.username || '-' }}</span>
              </li>
              <li class="info-item">
                <span class="item-label">
                  <i class="el-icon-mobile-phone"></i>手机号码
                </span>
                <span class="item-value">{{ user.mobile || '-' }}</span>
              </li>
              <li class="info-item">
                <span class="item-label">
                  <i class="el-icon-message"></i>用户邮箱
                </span>
                <span class="item-value">{{ user.email || '-' }}</span>
              </li>
              <li class="info-item">
                <span class="item-label">
                  <i class="el-icon-share"></i>所属部门
                </span>
                <span class="item-value" v-if="user.dept">{{ user.dept.name }}</span>
                <span class="item-value" v-else>-</span>
              </li>
              <li class="info-item">
                <span class="item-label">
                  <i class="el-icon-postcard"></i>所属岗位
                </span>
                <span class="item-value" v-if="user.posts">{{ user.posts.map(post => post.name).join('、') }}</span>
                <span class="item-value" v-else>-</span>
              </li>
              <li class="info-item">
                <span class="item-label">
                  <i class="el-icon-trophy"></i>所属角色
                </span>
                <span class="item-value" v-if="user.roles">{{ user.roles.map(role => role.name).join('、') }}</span>
                <span class="item-value" v-else>-</span>
              </li>
              <li class="info-item">
                <span class="item-label">
                  <i class="el-icon-time"></i>创建日期
                </span>
                <span class="item-value">{{ parseTime(user.createTime) }}</span>
              </li>
            </ul>
          </div>
        </div>
      </el-col>

      <!-- 右侧:基本资料 -->
      <el-col :span="16" :xs="24">
        <div class="info-card">
          <div class="card-header">
            <span class="header-title">
              <i class="el-icon-edit-outline"></i>基本资料
            </span>
          </div>
          <div class="card-body">
            <el-tabs v-model="activeTab" class="profile-tabs">
              <el-tab-pane label="基本资料" name="userinfo">
                <userInfo :user="user" />
              </el-tab-pane>
              <el-tab-pane label="修改密码" name="resetPwd">
                <resetPwd :user="user" />
              </el-tab-pane>
              <el-tab-pane label="社交信息" name="userSocial">
                <userSocial :user="user" :getUser="getUser" :setActiveTab="setActiveTab" />
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userAvatar from "./userAvatar";
import userInfo from "./userInfo";
import resetPwd from "./resetPwd";
import userSocial from "./userSocial";
import { getUserProfile } from "@/api/system/user";

export default {
  name: "Profile",
  components: { userAvatar, userInfo, resetPwd, userSocial },
  data() {
    return {
      user: {},
      roleGroup: {},
      postGroup: {},
      activeTab: "userinfo"
    };
  },
  created() {
    this.getUser();
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data;
      });
    },
    setActiveTab(activeTab) {
      this.activeTab = activeTab
    }
  }
};
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px;
  min-height: calc(100vh - 84px);
}

/* 顶部用户横幅 */
.profile-banner {
  position: relative;
  height: 180px;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 20px;
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.banner-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(10, 21, 32, 0.95) 0%, rgba(20, 40, 60, 0.95) 50%, rgba(10, 30, 50, 0.95) 100%);
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle at 30% 50%, rgba(64, 158, 255, 0.15) 0%, transparent 40%),
                radial-gradient(circle at 70% 50%, rgba(99, 102, 241, 0.12) 0%, transparent 40%);
    animation: bannerGlow 8s ease-in-out infinite alternate;
  }
}

@keyframes bannerGlow {
  0% { transform: translate(0, 0); }
  100% { transform: translate(-10%, -5%); }
}

.banner-content {
  position: relative;
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 40px;
  z-index: 1;
}

.banner-avatar {
  margin-right: 30px;

  ::v-deep .user-info-head {
    height: 110px;

    img {
      width: 110px !important;
      height: 110px !important;
      border: 3px solid rgba(64, 158, 255, 0.5);
      box-shadow: 0 0 20px rgba(64, 158, 255, 0.4);
    }
  }
}

.banner-info {
  flex: 1;
}

.banner-name {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.banner-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;

  .meta-item {
    display: inline-flex;
    align-items: center;
    color: rgba(255, 255, 255, 0.75);
    font-size: 14px;

    i {
      margin-right: 6px;
      color: rgba(64, 158, 255, 0.9);
      font-size: 16px;
    }
  }
}

/* 通用卡片样式 */
.info-card {
  background: rgba(10, 21, 32, 0.6);
  border: 1px solid rgba(64, 158, 255, 0.15);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  height: 100%;

  &:hover {
    border-color: rgba(64, 158, 255, 0.3);
    box-shadow: 0 6px 28px rgba(64, 158, 255, 0.15);
  }
}

.card-header {
  padding: 16px 20px;
  background: rgba(10, 21, 32, 0.7);
  border-bottom: 1px solid rgba(64, 158, 255, 0.15);

  .header-title {
    display: inline-flex;
    align-items: center;
    color: #fff;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 0.5px;

    i {
      margin-right: 8px;
      color: rgba(64, 158, 255, 0.9);
      font-size: 17px;
    }
  }
}

.card-body {
  padding: 20px;
}

/* 左侧信息列表 */
.info-list {
  list-style: none;
  margin: 0;
  padding: 0;

  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 14px 12px;
    border-bottom: 1px dashed rgba(64, 158, 255, 0.12);
    transition: background 0.25s ease;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background: rgba(64, 158, 255, 0.05);
    }

    .item-label {
      display: inline-flex;
      align-items: center;
      color: #fff;
      font-size: 13px;

      i {
        margin-right: 8px;
        color: rgba(64, 158, 255, 0.9);
        font-size: 15px;
      }
    }

    .item-value {
      color: #fff;
      font-size: 13px;
      font-weight: 500;
      text-align: right;
      max-width: 55%;
      word-break: break-all;
    }
  }
}

/* 右侧 Tabs 样式 */
.profile-tabs {
  ::v-deep .el-tabs__header {
    margin: 0 0 20px 0;
    border-bottom: 1px solid rgba(64, 158, 255, 0.15);
  }

  ::v-deep .el-tabs__nav-wrap::after {
    background-color: transparent;
  }

  ::v-deep .el-tabs__item {
    color: #fff !important;
    font-size: 14px;
    font-weight: 500;
    border-bottom: 2px solid transparent;
    transition: all 0.3s ease;

    &:hover {
      color: rgba(64, 158, 255, 0.9) !important;
    }

    &.is-active {
      color: #fff !important;
      font-weight: 600;
    }
  }

  ::v-deep .el-tabs__active-bar {
    background-color: rgba(64, 158, 255, 0.9);
    height: 2px;
    box-shadow: 0 0 8px rgba(64, 158, 255, 0.6);
  }

  /* 表单样式适配 */
  ::v-deep .el-form-item__label {
    color: #fff;
    font-size: 13px;
  }

  ::v-deep .el-input__inner {
    background-color: rgba(10, 21, 32, 0.5);
    border: 1px solid rgba(64, 158, 255, 0.2);
    color: #fff;
    border-radius: 6px;

    &:hover {
      border-color: rgba(64, 158, 255, 0.4);
    }

    &:focus {
      border-color: rgba(64, 158, 255, 0.7);
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.15);
    }
  }

  ::v-deep .el-radio {
    color: #fff;

    .el-radio__label {
      color: #fff;
    }
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .profile-container {
    padding: 12px;
  }

  .profile-banner {
    height: auto;
    padding: 20px 0;
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
    padding: 20px;

    .banner-avatar {
      margin-right: 0;
      margin-bottom: 16px;
    }
  }

  .banner-meta {
    justify-content: center;
  }
}
</style>
