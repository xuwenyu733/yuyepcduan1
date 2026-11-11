<template>
  <div class="oa-attendance-container">
    <!-- 头部 -->
    <div class="header-section">
      <div class="header-title">
        <h1>OA协同办公系统</h1>
        <p class="subtitle">考勤·工作计划·考核·文档协同</p>
      </div>
      <div class="header-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'attendance' }"
          @click="activeTab = 'attendance'">
          <i class="el-icon-time"></i>
          <span>考勤管理</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'workplan' }"
          @click="activeTab = 'workplan'">
          <i class="el-icon-date"></i>
          <span>工作计划</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'evaluation' }"
          @click="activeTab = 'evaluation'">
          <i class="el-icon-medal"></i>
          <span>考核评分</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'document' }"
          @click="handleDocumentTabClick">
          <i class="el-icon-folder-opened"></i>
          <span>文档协同</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 考勤管理 -->
      <AttendanceTab v-if="activeTab === 'attendance'" />
      
      <!-- 工作计划 -->
      <WorkPlanTab v-if="activeTab === 'workplan'" />
      
      <!-- 考核评分 -->
      <EvaluationTab v-if="activeTab === 'evaluation'" />
      
      <!-- 文档协同 -->
      <DocumentTab v-if="activeTab === 'document'" />
    </div>
  </div>
</template>

<script>
import AttendanceTab from './components/AttendanceTab.vue'
import WorkPlanTab from './components/WorkPlanTab.vue'
import EvaluationTab from './components/EvaluationTab.vue'
import DocumentTab from './components/DocumentTab.vue'

export default {
  name: 'OAAttendance',
  components: {
    AttendanceTab,
    WorkPlanTab,
    EvaluationTab,
    DocumentTab
  },
  data() {
    return {
      activeTab: 'attendance'
    }
  },
  methods: {
    // 点击文档协同tab
    handleDocumentTabClick() {
      this.activeTab = 'document'
      // 由于使用了 v-if，组件会被重新创建，mounted 钩子会自动调用 initData
      // 但为了确保数据加载，我们可以在 $nextTick 中触发
      this.$nextTick(() => {
        // DocumentTab 组件会在 mounted 时自动加载数据
      })
    }
  }
}
</script>

<style lang="scss" scoped>
$teal: #54907a;
$teal-border: rgba(84, 144, 122, 0.35);

.oa-attendance-container {
  min-height: 100vh;
  background: #0a1520;
  padding: 20px;

  .header-section {
    background: linear-gradient(180deg, #122033 0%, #0f1a26 100%);
    padding: 18px 24px;
    border-radius: 8px;
    margin-bottom: 16px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);
    border: 1px solid $teal-border;

    .header-title {
      margin-bottom: 16px;

      h1 {
        margin: 0;
        font-size: 22px;
        color: #ffffff;
        font-weight: 600;
      }

      .subtitle {
        margin: 6px 0 0 0;
        font-size: 13px;
        color: rgba(255, 255, 255, 0.62);
      }
    }

    .header-tabs {
      display: flex;
      gap: 10px;
      background: rgba(10, 21, 32, 0.65);
      padding: 8px;
      border-radius: 8px;
      border: 1px solid rgba(84, 144, 122, 0.25);

      .tab-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 6px;
        padding: 14px 20px;
        cursor: pointer;
        color: rgba(255, 255, 255, 0.65);
        font-size: 14px;
        border-radius: 8px;
        transition: all 0.25s ease;
        background: transparent;
        flex: 1;
        min-width: 0;
        border: 1px solid transparent;

        i {
          font-size: 22px;
          color: rgba(255, 255, 255, 0.65);
          line-height: 1;
        }

        span {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.65);
          white-space: nowrap;
          line-height: 1;
          font-weight: 400;
        }

        &.active {
          background: rgba(84, 144, 122, 0.18);
          border-color: rgba(84, 144, 122, 0.5);
          box-shadow: inset 0 0 0 1px rgba(84, 144, 122, 0.15);

          i {
            color: #8fd4b8;
          }

          span {
            color: #ffffff;
            font-weight: 500;
          }
        }

        &:hover:not(.active) {
          background: rgba(84, 144, 122, 0.1);
          border-color: rgba(84, 144, 122, 0.28);
        }
      }
    }
  }

  .main-content {
    width: 100%;
  }
}
</style>
