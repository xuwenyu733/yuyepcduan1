<template>
  <div class="knowledge-tree-page">
    <header class="page-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">知识树</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">
        维护知识树节点（仅管理员/专家/养殖负责人可访问，普通养殖人员不可见）。
        平台 CRUD 路由：<code>/knowledge/zk-konwledge-tree</code>
        ；鱼病侧栏入口：<code>/fish-disease?tab=knowledgeTree</code>
      </p>
    </header>
    <div class="page-body">
      <zk-konwledge-tree />
    </div>
  </div>
</template>

<script>
import ZkKonwledgeTree from '@/views/fish/zkKonwledgeTree/index.vue'
import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'

export default {
  name: 'FishDiseaseKnowledgeTree',
  components: { ZkKonwledgeTree, MenuToggle, HeaderAuth }
}
</script>

<style scoped>
.knowledge-tree-page {
  min-height: 100%;
  background: #0a1520;
  color: #e5eef7;
}
.page-header {
  padding: 16px 20px 8px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.14);
}
.header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.header-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}
.header-desc {
  margin: 8px 0 0;
  font-size: 12px;
  color: #7c8fa1;
}
.page-body {
  padding: 8px 12px 20px;
}
.page-body ::v-deep .app-container {
  background: transparent;
  padding: 8px;
}
</style>
