<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import ChatView from './views/ChatView.vue'
import SkillsView from './views/SkillsView.vue'

function routeFromHash() {
  const h = (location.hash || '#/chat').replace(/^#\/?/, '')
  if (h.startsWith('skills') || h.startsWith('knowledge')) return 'skills'
  return 'chat'
}

const page = ref(routeFromHash())

const pageTitle = computed(() =>
  page.value === 'skills' ? '渔业知识录入 · Skills' : '渔业科技智能助手 · 小科',
)

function go(name) {
  page.value = name
  location.hash = name === 'skills' ? '#/skills' : '#/chat'
}

function onHash() {
  page.value = routeFromHash()
}

onMounted(() => {
  if (!location.hash) location.hash = '#/chat'
  window.addEventListener('hashchange', onHash)
  document.title = pageTitle.value
})

onUnmounted(() => {
  window.removeEventListener('hashchange', onHash)
})
</script>

<template>
  <div class="page-root">
    <nav class="top-nav">
      <div class="brand">AI 小科</div>
      <div class="nav-links">
        <button type="button" class="nav-btn" :class="{ active: page === 'chat' }" @click="go('chat')">
          智能问答
        </button>
        <button type="button" class="nav-btn" :class="{ active: page === 'skills' }" @click="go('skills')">
          知识录入
        </button>
      </div>
      <div class="nav-hint">不懂技术也能写规定 → 自动变 MD → 问答优先读取</div>
    </nav>

    <main class="page-main" :class="page">
      <ChatView v-if="page === 'chat'" />
      <SkillsView v-else />
    </main>
  </div>
</template>

<style scoped>
.page-root {
  height: 100vh;
  max-height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.page-main {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
/* 知识录入页：允许整页上下滚动 */
.page-main.skills {
  overflow-x: hidden;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
.top-nav {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 20px;
  border-bottom: 1px solid rgba(120, 170, 230, 0.18);
  background: rgba(6, 20, 40, 0.92);
  z-index: 20;
}
.brand {
  font-weight: 700;
  color: #7ec2ff;
  letter-spacing: 1px;
}
.nav-links {
  display: flex;
  gap: 8px;
}
.nav-btn {
  border: 1px solid transparent;
  background: transparent;
  color: #8aa3c7;
  border-radius: 999px;
  padding: 7px 14px;
  cursor: pointer;
}
.nav-btn.active {
  background: rgba(74, 163, 255, 0.16);
  border-color: rgba(74, 163, 255, 0.45);
  color: #9fd0ff;
}
.nav-hint {
  margin-left: auto;
  color: #6f87a8;
  font-size: 12px;
}
@media (max-width: 800px) {
  .nav-hint {
    display: none;
  }
}
</style>
