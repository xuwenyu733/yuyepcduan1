<template>
  <div class="flavor-native">
    <div class="app-layout embedded">
      <header class="app-header">
        <nav class="nav-tabs" aria-label="风味模块导航">
          <button
            v-for="item in menus"
            :key="item.key"
            type="button"
            class="nav-item"
            :class="{ active: active === item.key }"
            @click="active = item.key"
          >
            <i :class="item.icon" />
            <span>{{ item.label }}</span>
          </button>
        </nav>
      </header>
      <main class="app-main">
          <keep-alive>
            <component
              :is="activeComp"
              :key="active"
              :prefer-tab="active === 'exit' ? 'exit' : (active === 'process' ? 'opt' : '')"
            />
          </keep-alive>
      </main>
    </div>
  </div>
</template>

<script>
import PurchaseManage from './PurchaseManage.vue'
import EntryManage from './EntryManage.vue'
import ProcessManage from './ProcessManage.vue'
import DataStats from './DataStats.vue'
import RecordQuery from './RecordQuery.vue'
import BasicConfig from './BasicConfig.vue'
import './flavor-theme.css'

const COMP_MAP = {
  purchase: 'PurchaseManage',
  entry: 'EntryManage',
  process: 'ProcessManage',
  exit: 'ProcessManage',
  stats: 'DataStats',
  records: 'RecordQuery',
  config: 'BasicConfig'
}

export default {
  name: 'FlavorShell',
  components: {
    PurchaseManage,
    EntryManage,
    ProcessManage,
    DataStats,
    RecordQuery,
    BasicConfig
  },
  data() {
    return {
      // 导航顺序：入池管理 → 营养过程动态记录 → … → 基础档案置末
      active: 'entry',
      menus: [
        { key: 'entry', label: '入池管理', icon: 'el-icon-coin' },
        { key: 'process', label: '营养过程动态记录', icon: 'el-icon-data-line' },
        { key: 'exit', label: '优化出池', icon: 'el-icon-sold-out' },
        { key: 'purchase', label: '采购管理', icon: 'el-icon-shopping-cart-2' },
        { key: 'stats', label: '数据统计', icon: 'el-icon-s-data' },
        { key: 'records', label: '记录查询', icon: 'el-icon-document' },
        { key: 'config', label: '基础档案', icon: 'el-icon-folder-opened' }
      ]
    }
  },
  computed: {
    activeComp() {
      return COMP_MAP[this.active] || 'PurchaseManage'
    }
  },
  created() {
    if (this.$route && this.$route.meta) {
      this.$set(this.$route.meta, 'appMainOverflow', 'hidden')
    }
  },
  beforeDestroy() {
    if (this.$route && this.$route.meta) {
      this.$delete(this.$route.meta, 'appMainOverflow')
    }
  }
}
</script>

<style scoped>
.flavor-native {
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
  padding: 10px 20px 0;
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
  padding: 11px 16px;
  color: #c5d0dc;
  background: transparent;
  border: none;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  cursor: pointer;
  transition: color 0.15s ease;
}

.nav-item:hover {
  color: #e8edf2;
}

.nav-item.active {
  color: #8fd4b8;
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
  background: #54907a;
}

.app-main {
  flex: 1;
  min-height: 0;
  padding: 16px 20px 20px;
  overflow: auto;
  background: #0a1520;
}
</style>
