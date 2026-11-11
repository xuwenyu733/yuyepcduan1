<template>
  <div class="knowledge-page">
    <div class="page-card filter-bar">
      <el-input
        v-model="keyword"
        clearable
        prefix-icon="el-icon-search"
        placeholder="搜索病害名称或症状"
        size="small"
        style="max-width: 280px"
      />
      <div class="category-tabs">
        <button
          type="button"
          class="cat-tab"
          :class="{ active: '' === categoryFilter }"
          @click="categoryFilter = ''"
        >
          全部
        </button>
        <button
          v-for="cat in categories"
          :key="cat"
          type="button"
          class="cat-tab"
          :class="{ active: categoryFilter === cat }"
          @click="categoryFilter = cat"
        >
          {{ cat }}
        </button>
      </div>
    </div>

    <div v-loading="loading" class="knowledge-grid">
      <div
        v-for="item in filteredList"
        :key="item.id"
        class="page-card knowledge-card"
        @click="openDetail(item)"
      >
        <div class="card-head">
          <div>
            <div class="card-title">{{ item.name }}</div>
            <div class="card-tag">{{ item.category }}</div>
          </div>
          <el-tag size="small" :type="riskTagType(item.riskLevel)">
            {{ riskLabel(item.riskLevel) }}
          </el-tag>
        </div>
        <p class="desc">{{ item.description }}</p>
        <div class="symptom-row">
          <span
            v-for="s in item.symptoms.slice(0, 4)"
            :key="s"
            class="symptom-tag"
          >{{ s }}</span>
          <span
            v-if="item.symptoms.length > 4"
            class="symptom-tag more"
          >+{{ item.symptoms.length - 4 }}</span>
        </div>
        <div class="card-foot">
          点击查看详情 →
        </div>
      </div>

      <div v-if="!loading && filteredList.length === 0" class="empty-box page-card">
        未找到匹配的病害
      </div>
    </div>

    <el-drawer
      :visible="detailVisible"
      :title="detail.name"
      size="480px"
      custom-class="knowledge-drawer"
      @update:visible="detailVisible = $event"
    >
      <div v-if="detail.id" class="drawer-body">
        <el-tag size="small" :type="riskTagType(detail.riskLevel)">
          {{ riskLabel(detail.riskLevel) }}
        </el-tag>
        <el-tag style="margin-left: 6px" size="small" type="info">
          {{ detail.category }}
        </el-tag>
        <p class="drawer-desc">{{ detail.description }}</p>

        <div class="drawer-section">
          <strong>典型症状</strong>
          <div class="symptom-row">
            <span
              v-for="s in detail.symptoms"
              :key="s"
              class="symptom-tag"
            >{{ s }}</span>
          </div>
        </div>

        <div class="drawer-section">
          <strong>推荐用药</strong>
          <p>{{ detail.medicine }}</p>
        </div>

        <div class="drawer-section">
          <strong>治疗方案</strong>
          <p>{{ detail.treatment }}</p>
        </div>

        <div class="drawer-section">
          <strong>预防措施</strong>
          <p>{{ detail.prevention }}</p>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { fetchKnowledge, fetchKnowledgeDetail, fetchMeta } from '../api'

export default {
  name: 'KnowledgeBase',
  data() {
    return {
      loading: false,
      list: [],
      meta: { severityMap: {} },
      keyword: '',
      categoryFilter: '',
      detailVisible: false,
      detail: {}
    }
  },
  computed: {
    categories() {
      return [...new Set(this.list.map(item => item.category))]
    },
    filteredList() {
      let list = this.list
      if (this.categoryFilter) {
        list = list.filter(item => item.category === this.categoryFilter)
      }
      if (this.keyword.trim()) {
        const kw = this.keyword.trim().toLowerCase()
        list = list.filter(item =>
          item.name.toLowerCase().includes(kw) ||
          item.description.toLowerCase().includes(kw) ||
          item.symptoms.some(s => s.toLowerCase().includes(kw))
        )
      }
      return list
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      const [listRes, metaRes] = await Promise.all([
        fetchKnowledge(),
        fetchMeta()
      ])
      this.list = listRes.data
      this.meta = metaRes.data
      this.loading = false
    },
    async openDetail(item) {
      const res = await fetchKnowledgeDetail(item.id)
      this.detail = res.data
      this.detailVisible = true
    },
    riskLabel(level) {
      return (this.meta.severityMap && this.meta.severityMap[level]) || level
    },
    riskTagType(level) {
      return { critical: 'danger', high: 'warning', medium: '', low: 'info' }[level] || ''
    }
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  padding: 14px 16px;
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.cat-tab {
  border: 1px solid #e5e7eb;
  background: #f9fafb;
  color: #606266;
  border-radius: 8px;
  padding: 5px 12px;
  font-size: 12px;
  cursor: pointer;
}

.cat-tab.active {
  background: var(--primary-light);
  border-color: var(--primary-border);
  color: var(--primary);
  font-weight: 600;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 12px;
}

.knowledge-card {
  padding: 18px;
  cursor: pointer;
  transition: box-shadow 0.15s;
}

.knowledge-card:hover {
  box-shadow: 0 4px 16px rgba(31, 45, 61, 0.08);
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 10px;
}

.card-title {
  font-size: 16px;
  font-weight: 650;
  color: #111827;
}

.card-tag {
  margin-top: 4px;
  font-size: 12px;
  color: var(--primary);
}

.desc {
  margin: 0 0 12px;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.symptom-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.symptom-tag {
  background: var(--primary-light);
  color: var(--primary);
  border-radius: 8px;
  padding: 3px 10px;
  font-size: 12px;
}

.symptom-tag.more {
  background: #f3f4f6;
  color: #909399;
}

.card-foot {
  font-size: 12px;
  color: var(--primary);
}

.drawer-body {
  padding: 0 4px;
}

.drawer-desc {
  margin: 12px 0 16px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.drawer-section {
  margin-bottom: 16px;
}

.drawer-section strong {
  display: block;
  font-size: 13px;
  color: #374151;
  margin-bottom: 6px;
}

.drawer-section p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.55;
}
</style>
