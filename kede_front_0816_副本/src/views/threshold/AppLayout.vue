<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="logo">◈</span>
        <span>智慧循环水养殖平台</span>
      </div>
      <nav class="nav">
        <div v-for="group in groups" :key="group.name" class="nav-group">
          <div class="group-title">{{ group.name }}</div>
          <button
            v-for="item in group.items"
            :key="item.id"
            type="button"
            class="nav-item"
            :class="{ active: active === item.id }"
            @click="$emit('change', item.id)"
          >
            <i :class="item.icon" />
            <span>{{ item.name }}</span>
          </button>
        </div>
      </nav>
    </aside>

    <div class="main-wrap">
      <header class="topbar">
        <div class="crumb">
          <span class="brand-mini">智慧循环水养殖平台</span>
          <span class="sep">/</span>
          <i class="el-icon-s-tools" />
          <span>阈值参数全局配置</span>
        </div>
        <div class="actions">
          <el-button class="btn-exit" icon="el-icon-back" @click="handleExit">返回</el-button>
          <el-button class="btn-save" type="primary" icon="el-icon-document" :loading="saving" @click="$emit('save')">
            保存配置
          </el-button>
          <el-button class="btn-reset" icon="el-icon-refresh-left" @click="$emit('reset')">恢复默认</el-button>
        </div>
      </header>
      <main class="content">
        <slot />
      </main>
      <footer class="footer">
        <div class="meta">
          配置版本 {{ meta.version }} · 最后保存 {{ meta.lastSaved }} · {{ meta.moduleCount }} 个配置模块
        </div>
        <div class="footer-right">
          <span v-if="active === 'association'" class="engine"><i class="dot" />规则引擎运行中</span>
          <el-button class="btn-save" type="primary" icon="el-icon-document" :loading="saving" @click="$emit('save-all')">
            保存所有配置
          </el-button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AppLayout',
  props: {
    nav: { type: Array, default: () => [] },
    active: { type: String, default: 'water' },
    meta: { type: Object, default: () => ({ version: 'v3.3.0', lastSaved: '-', moduleCount: 12 }) },
    saving: { type: Boolean, default: false }
  },
  computed: {
    groups() {
      const map = {}
      this.nav.forEach(item => {
        if (!map[item.group]) map[item.group] = []
        map[item.group].push(item)
      })
      return Object.keys(map).map(name => ({ name, items: map[name] }))
    }
  },
  methods: {
    handleExit() {
      const from = this.$route && this.$route.query && this.$route.query.from
      if (from) {
        this.$router.push(from).catch(() => {})
        return
      }
      if (window.history.length > 1) {
        this.$router.back()
        return
      }
      this.$router.push('/index_daping3').catch(() => {})
    }
  }
}
</script>

<style scoped>
.app-shell {
  display: flex;
  height: 100%;
  min-height: 100%;
  background: var(--bg, #0a1520);
  color: #d7e3f4;
}
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #0a1520;
  border-right: 1px solid var(--border, rgba(124, 143, 161, 0.22));
  padding: 16px 12px;
  height: 100%;
  overflow: auto;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 650;
  color: #f2f7ff;
  padding: 8px 8px 18px;
}
.logo { color: var(--accent); font-size: 18px; }
.group-title {
  font-size: 11px;
  color: #5f7190;
  padding: 10px 10px 6px;
  letter-spacing: 0.5px;
}
.nav-item {
  width: 100%;
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid transparent;
  background: transparent;
  color: #9aadc6;
  padding: 9px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  margin-bottom: 2px;
  text-align: left;
}
.nav-item:hover {
  color: #dff7f7;
  background: rgba(64, 158, 255, 0.06);
}
.nav-item.active {
  color: var(--accent);
  border-color: rgba(64, 158, 255, 0.45);
  background: rgba(64, 158, 255, 0.1);
}
.nav-item.active::before {
  content: '';
  position: absolute;
  left: -1px;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 2px;
  background: var(--accent);
  box-shadow: 0 0 8px rgba(64, 158, 255, 0.55);
}
.brand-mini {
  color: #9aadc6;
  font-weight: 500;
  font-size: 13px;
}
.sep {
  color: #4a5d78;
  margin: 0 2px;
}
.main-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.topbar {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 14px 22px;
  background: rgba(10, 21, 32, 0.92);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--border);
}
.crumb {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #e8f1ff;
  font-size: 15px;
  font-weight: 600;
}
.actions { display: flex; gap: 8px; align-items: center; }
.btn-exit {
  background: transparent !important;
  border-color: rgba(148, 163, 184, 0.45) !important;
  color: #d7e3f4 !important;
}
.content {
  flex: 1;
  padding: 18px 22px 80px;
  overflow: auto;
}
.footer {
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 12px 22px;
  background: rgba(10, 21, 32, 0.95);
  border-top: 1px solid var(--border);
}
.meta { color: #6d809c; font-size: 12px; }
.footer-right { display: flex; align-items: center; gap: 14px; }
.engine {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #7ddea8;
  font-size: 12px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3ad47a;
  box-shadow: 0 0 8px rgba(58, 212, 122, 0.8);
  display: inline-block;
}
</style>
