<template>
  <div class="experiment-native">
    <div class="app-layout embedded">
      <header class="app-header">
        <nav class="nav-tabs" aria-label="实验模块导航">
          <button
            v-for="item in menus"
            :key="item.key"
            type="button"
            class="nav-item"
            :class="{ active: active === item.key }"
            @click="switchMenu(item.key)"
          >
            <i :class="item.icon" />
            <span>{{ item.label }}</span>
          </button>
        </nav>
      </header>
      <main class="shell-main">
        <!-- 旧库能力：实验管理 -->
        <div v-show="active === 'experiments'" class="tab-pane">
          <legacy-experiment-list
            ref="legacyListRef"
            @edit="handleEditExperiment"
            @refresh="loadExperiments"
          />
        </div>

        <!-- 旧库能力：数据采集 -->
        <div v-show="active === 'collection'" class="tab-pane">
          <legacy-data-collection
            v-if="active === 'collection'"
            :experiments="experiments"
            @refresh="loadExperiments"
          />
        </div>

        <!-- 旧库能力：多媒体 -->
        <div v-show="active === 'media'" class="tab-pane">
          <legacy-media-management
            v-if="active === 'media'"
            :experiments="experiments"
            @refresh="loadExperiments"
          />
        </div>

        <!-- 旧库能力：数据分析 -->
        <div v-show="active === 'analysis'" class="tab-pane">
          <legacy-data-analytics
            v-if="active === 'analysis'"
            :experiments="experiments"
            @edit="handleEditExperiment"
            @refresh="loadExperiments"
          />
        </div>

        <!-- 旧库能力：存塘管理 -->
        <div v-show="active === 'pool'" class="tab-pane">
          <zk-experimental-pool v-if="active === 'pool'" />
        </div>

        <!-- 新功能（BFF mock）：实验模板 / 权限管理 — 先不动 -->
        <div v-show="active === 'templates'" class="tab-pane">
          <experiment-templates v-if="active === 'templates'" />
        </div>
        <div v-show="active === 'permissions'" class="tab-pane">
          <permission-manage v-if="active === 'permissions'" />
        </div>
      </main>
    </div>

    <el-dialog
      v-if="experimentFormVisible"
      :title="experimentFormTitle"
      :visible.sync="experimentFormVisible"
      width="800px"
      append-to-body
      custom-class="experiment-form-dialog"
      @close="handleCloseExperimentForm"
    >
      <legacy-experiment-form
        :experiment-id="currentExperimentId"
        @success="handleExperimentFormSuccess"
        @cancel="handleExperimentFormCancel"
      />
    </el-dialog>
  </div>
</template>

<script>
/**
 * 养殖实验数据管理系统壳：
 * - 路由沿用旧菜单 /experimental/zk-experimental-system
 * - 实验/采集/多媒体/分析/存塘：对接原有 Java（zk-experimental*）库
 * - 模板/权限：桌面新系统功能，仍走 modules BFF，暂不改
 */
import * as ZkExperimentalApi from '@/api/fish/zkexperimental'
import LegacyExperimentList from '@/views/fish/zkexperimental/components/ExperimentList.vue'
import LegacyExperimentForm from '@/views/fish/zkexperimental/components/ExperimentForm.vue'
import LegacyDataCollection from '@/views/fish/zkexperimental/components/DataCollection.vue'
import LegacyMediaManagement from '@/views/fish/zkexperimental/components/MediaManagement.vue'
import LegacyDataAnalytics from '@/views/fish/zkexperimental/components/DataAnalytics.vue'
import ZkExperimentalPool from '@/views/fish/zkexperimentalpool/index.vue'
import ExperimentTemplates from './ExperimentTemplates.vue'
import PermissionManage from './PermissionManage.vue'
import './experiment-shell-theme.css'

const ADMIN_ROLE_RE = /^(admin|super_admin|super_administrator|tenant_admin|administrator)$/i

export default {
  name: 'ExperimentShell',
  components: {
    LegacyExperimentList,
    LegacyExperimentForm,
    LegacyDataCollection,
    LegacyMediaManagement,
    LegacyDataAnalytics,
    ZkExperimentalPool,
    ExperimentTemplates,
    PermissionManage
  },
  data() {
    return {
      active: 'experiments',
      experiments: [],
      experimentFormVisible: false,
      experimentFormTitle: '新建实验',
      currentExperimentId: null,
      allMenus: [
        { key: 'experiments', label: '实验管理', icon: 'el-icon-s-opportunity', adminOnly: false },
        { key: 'collection', label: '数据采集', icon: 'el-icon-coin', adminOnly: false },
        { key: 'media', label: '多媒体', icon: 'el-icon-picture-outline', adminOnly: false },
        { key: 'analysis', label: '数据分析', icon: 'el-icon-data-line', adminOnly: false },
        { key: 'pool', label: '存塘管理', icon: 'el-icon-s-home', adminOnly: false },
        { key: 'templates', label: '实验模板', icon: 'el-icon-document', adminOnly: true },
        { key: 'permissions', label: '权限管理', icon: 'el-icon-user', adminOnly: true }
      ]
    }
  },
  computed: {
    isExperimentAdmin() {
      const roles = (this.$store && this.$store.getters && this.$store.getters.roles) || []
      if (!roles.length) return false
      return roles.some(r => ADMIN_ROLE_RE.test(String(r)))
    },
    menus() {
      return this.allMenus.filter(m => !m.adminOnly || this.isExperimentAdmin)
    }
  },
  created() {
    if (this.$route && this.$route.meta) {
      this.$set(this.$route.meta, 'appMainOverflow', 'hidden')
    }
    this.loadExperiments()
  },
  beforeDestroy() {
    if (this.$route && this.$route.meta) {
      this.$delete(this.$route.meta, 'appMainOverflow')
    }
  },
  methods: {
    switchMenu(key) {
      if ((key === 'templates' || key === 'permissions') && !this.isExperimentAdmin) {
        this.$message && this.$message.warning('普通用户无权访问实验模板与权限管理')
        return
      }
      this.active = key
      if (key === 'experiments') {
        this.$nextTick(() => {
          const list = this.$refs.legacyListRef
          if (list && typeof list.getList === 'function') list.getList()
        })
      }
    },
    async loadExperiments() {
      try {
        const res = await ZkExperimentalApi.getZkExperimentalPage({
          pageNo: 1,
          pageSize: 100
        })
        this.experiments = (res.data && res.data.list) || []
      } catch (e) {
        console.error('加载实验列表失败:', e)
      }
    },
    handleEditExperiment(id) {
      this.currentExperimentId = id
      this.experimentFormTitle = id ? '编辑实验' : '新建实验'
      this.experimentFormVisible = true
    },
    handleCloseExperimentForm() {
      this.currentExperimentId = null
      this.experimentFormTitle = '新建实验'
    },
    handleExperimentFormSuccess() {
      this.experimentFormVisible = false
      this.loadExperiments()
      const list = this.$refs.legacyListRef
      if (list && typeof list.getList === 'function') list.getList()
    },
    handleExperimentFormCancel() {
      this.experimentFormVisible = false
    }
  }
}
</script>

<style scoped>
.experiment-native {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background: #0a1520;
}

.app-layout {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #0a1520;
}

.app-header {
  flex-shrink: 0;
  background: #0a1520;
  border-bottom: 1px solid rgba(124, 143, 161, 0.18);
  padding: 8px 20px 0;
}

.nav-tabs {
  display: flex;
  flex-wrap: nowrap;
  gap: 0;
  overflow-x: auto;
  scrollbar-width: none;
}

.nav-tabs::-webkit-scrollbar {
  display: none;
}

.nav-item {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  border: none;
  color: #7c8fa1;
  font-size: 14px;
  background: transparent;
  cursor: pointer;
  white-space: nowrap;
  transition: color 0.15s ease;
}

.nav-item i {
  font-size: 14px;
  opacity: 0.9;
}

.nav-item:hover {
  color: #c5d0dc;
}

.nav-item.active {
  color: #409eff;
  font-weight: 600;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  left: 12px;
  right: 12px;
  bottom: 0;
  height: 2px;
  border-radius: 2px 2px 0 0;
  background: #409eff;
}

.shell-main {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 16px 20px 24px;
  background: #0a1520;
}

.tab-pane {
  max-width: 1320px;
  margin: 0 auto;
  animation: exp-fade-in 0.18s ease;
}

@keyframes exp-fade-in {
  from {
    opacity: 0;
    transform: translateY(4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
