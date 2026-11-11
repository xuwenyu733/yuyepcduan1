<template>
  <div class="archive-page">
    <header class="archive-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">案例归档</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">归档历史案例，构建经验库，支持智能检索和自动初判</p>
    </header>

    <div class="archive-body">
      <!-- 智能初判系统 -->
      <section class="ai-banner">
        <div class="ai-banner-head">
          <i class="el-icon-data-line ai-icon" />
          <div>
            <h2 class="ai-title">智能初判系统</h2>
            <p class="ai-desc">基于历史案例库的 AI 辅助诊断功能（后续迭代开放）</p>
          </div>
        </div>
        <div class="ai-info-card">
          <p>当匹配度 ≥ 80% 时，系统将自动显示初步诊断结果，供专家参考</p>
          <p class="ai-stats">当前案例库数量：{{ summary.total }} 例 · 预计开放时间：{{ summary.estimatedOpen }}</p>
        </div>
      </section>

      <!-- 搜索与筛选 -->
      <section class="toolbar-card">
        <div class="search-row">
          <div class="search-box">
            <i class="el-icon-search" />
            <input
              v-model="keyword"
              type="text"
              placeholder="搜索案例编号、病害类型、标签..."
              @input="onSearch"
            >
          </div>
          <div class="toolbar-actions">
            <div class="category-dropdown" @click.stop>
              <button
                type="button"
                class="category-trigger"
                :class="{ open: categoryMenuOpen }"
                @click="categoryMenuOpen = !categoryMenuOpen"
              >
                <i class="el-icon-s-operation" />
                <span>{{ categoryLabel }}</span>
                <i class="el-icon-arrow-down caret" />
              </button>
              <div v-if="categoryMenuOpen" class="category-menu">
                <button
                  v-for="opt in categoryOptions"
                  :key="opt.value"
                  type="button"
                  class="category-option"
                  :class="{ active: category === opt.value }"
                  @click="selectCategory(opt.value)"
                >
                  <span>{{ opt.label }}</span>
                  <i v-if="category === opt.value" class="el-icon-check" />
                </button>
              </div>
            </div>
            <button type="button" class="btn-export" @click.stop="handleExport">
              <i class="el-icon-download" /> 导出
            </button>
          </div>
        </div>
      </section>

      <!-- Tab + 列表 -->
      <div class="list-section">
        <div class="list-tabs">
          <button
            type="button"
            class="list-tab"
            :class="{ active: listTab === 'all' }"
            @click="switchTab('all')"
          >全部案例 ({{ summary.total }})</button>
          <button
            type="button"
            class="list-tab"
            :class="{ active: listTab === 'highMatch' }"
            @click="switchTab('highMatch')"
          >高匹配度案例 ({{ summary.highMatch }})</button>
        </div>

        <div v-loading="loading" class="case-list">
          <article v-for="item in cases" :key="item.id" class="case-card">
            <div class="case-head">
              <span class="case-id">{{ item.archiveNo }}</span>
              <span class="category-tag">{{ item.category }}</span>
              <span v-if="item.isHighMatch" class="match-tag">匹配度 {{ item.matchRate }}%</span>
            </div>

            <div class="case-meta-row">
              <p class="case-summary">
                {{ item.pondCode }} · {{ item.species }} · {{ item.diseaseName }} · {{ item.archiveDate }} · {{ item.expert }}
              </p>
              <div class="case-meta-actions">
                <span class="recovery-tag" :class="item.recoveryStatus">{{ item.recoveryLabel }}</span>
                <button type="button" class="btn-detail" @click="openDetail(item)">
                  <i class="el-icon-view" /> 查看详情
                </button>
              </div>
            </div>

            <div v-if="item.tags && item.tags.length" class="case-tags">
              <span v-for="tag in item.tags" :key="tag" class="case-tag">
                <i class="el-icon-price-tag" /> {{ tag }}
              </span>
            </div>
          </article>
          <div v-if="!loading && !cases.length" class="list-empty">暂无归档案例</div>
        </div>
      </div>
    </div>


    <el-dialog
      :visible.sync="detailVisible"
      width="640px"
      custom-class="case-detail-dialog"
      :show-close="true"
    >
      <template slot="title">
        <div v-if="detail" class="detail-dialog-head">
          <h3 class="detail-dialog-title">案例详情 - {{ detail.archiveNo }}</h3>
          <p class="detail-dialog-sub">完整的诊断数据和治疗方案</p>
        </div>
      </template>

      <div v-if="detail" class="detail-dialog-body">
        <section class="detail-section">
          <h4 class="detail-section-title">基本信息</h4>
          <div class="detail-info-grid">
            <div class="detail-info-col">
              <div class="detail-info-row">
                <span class="detail-info-label">养殖池：</span>
                <span class="detail-info-value">{{ detail.pondCode }}</span>
              </div>
              <div class="detail-info-row">
                <span class="detail-info-label">病害类型：</span>
                <span class="detail-info-value">{{ detail.diseaseName }}</span>
              </div>
              <div class="detail-info-row">
                <span class="detail-info-label">诊断专家：</span>
                <span class="detail-info-value">{{ detail.expert }}</span>
              </div>
            </div>
            <div class="detail-info-col">
              <div class="detail-info-row">
                <span class="detail-info-label">鱼品种：</span>
                <span class="detail-info-value">{{ detail.species }}</span>
              </div>
              <div class="detail-info-row">
                <span class="detail-info-label">诊断日期：</span>
                <span class="detail-info-value">{{ detail.archiveDate }}</span>
              </div>
              <div class="detail-info-row">
                <span class="detail-info-label">恢复情况：</span>
                <span class="recovery-tag" :class="detail.recoveryStatus">{{ detail.recoveryLabel }}</span>
              </div>
            </div>
          </div>
        </section>

        <section v-if="detail.tags && detail.tags.length" class="detail-section">
          <h4 class="detail-section-title">案例标签</h4>
          <div class="detail-tag-list">
            <span v-for="tag in detail.tags" :key="tag" class="case-tag">
              <i class="el-icon-price-tag" /> {{ tag }}
            </span>
          </div>
        </section>

        <section class="detail-section">
          <h4 class="detail-section-title">诊断结论</h4>
          <div class="diagnosis-box">
            <p class="diagnosis-line">
              <span class="diagnosis-label">确诊依据：</span>{{ detail.diagnosisBasis || detail.summary }}
            </p>
            <p class="diagnosis-line">
              <span class="diagnosis-label">病因分析：</span>{{ detail.causeAnalysis || '—' }}
            </p>
          </div>
        </section>

        <section class="detail-section">
          <h4 class="detail-section-title">治疗方案</h4>
          <div class="treatment-list">
            <div
              v-for="step in detail.treatmentSteps || fallbackTreatmentSteps(detail)"
              :key="step.title"
              class="treatment-card"
            >
              <div class="treatment-card-title">{{ step.title }}</div>
              <div class="treatment-card-desc">{{ step.content }}</div>
            </div>
          </div>
        </section>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchArchiveSummary,
  fetchArchiveCategories,
  fetchArchivedCases
} from '../api'
import MenuToggle from '../components/MenuToggle.vue'
import HeaderAuth from '../components/HeaderAuth.vue'

export default {
  name: 'CaseArchive',
  components: { MenuToggle, HeaderAuth },
  data() {
    return {
      loading: false,
      keyword: '',
      category: 'all',
      categoryMenuOpen: false,
      listTab: 'all',
      cases: [],
      categories: [],
      categoryOptions: [
        { label: '全部分类', value: 'all' },
        { label: '细菌', value: '细菌' },
        { label: '寄生虫', value: '寄生虫' },
        { label: '病毒', value: '病毒' },
        { label: '霉菌', value: '霉菌' },
        { label: '营养', value: '营养' },
        { label: '环境', value: '环境' }
      ],
      summary: { total: 0, highMatch: 0, estimatedOpen: '2025年Q1' },
      detailVisible: false,
      detail: null,
      searchTimer: null
    }
  },
  computed: {
    todayText() {
      const now = new Date()
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日${weekDays[now.getDay()]}`
    },
    categoryLabel() {
      const found = this.categoryOptions.find(o => o.value === this.category)
      return found ? found.label : '全部分类'
    }
  },
  created() {
    this.init()
  },
  mounted() {
    document.addEventListener('click', this.closeCategoryMenu)
  },
  beforeDestroy() {
    if (this.searchTimer) clearTimeout(this.searchTimer)
    document.removeEventListener('click', this.closeCategoryMenu)
  },
  methods: {
    closeCategoryMenu() {
      this.categoryMenuOpen = false
    },
    selectCategory(value) {
      this.category = value
      this.categoryMenuOpen = false
      this.loadCases()
    },
    async init() {
      try {
        const [summaryRes, catRes] = await Promise.all([
          fetchArchiveSummary(),
          fetchArchiveCategories()
        ])
        this.summary = summaryRes.data || this.summary
        const remote = catRes.data || []
        // 合并接口返回的分类，保留设计稿固定选项
        const fixed = this.categoryOptions.map(o => o.value)
        remote.forEach(c => {
          if (c && !fixed.includes(c)) {
            this.categoryOptions.push({ label: c, value: c })
          }
        })
        this.categories = remote
        await this.loadCases()
      } catch (e) {
        this.$message.error('加载案例归档失败，请确认后端服务已启动（端口 3011）')
      }
    },
    switchTab(tab) {
      this.listTab = tab
      this.loadCases()
    },
    onSearch() {
      if (this.searchTimer) clearTimeout(this.searchTimer)
      this.searchTimer = setTimeout(() => this.loadCases(), 300)
    },
    async loadCases() {
      this.loading = true
      try {
        const res = await fetchArchivedCases({
          tab: this.listTab === 'all' ? undefined : this.listTab,
          category: this.category,
          keyword: this.keyword.trim() || undefined
        })
        this.cases = res.data || []
      } catch (e) {
        this.cases = []
      } finally {
        this.loading = false
      }
    },
    openDetail(item) {
      this.detail = item
      this.detailVisible = true
    },
    fallbackTreatmentSteps(detail) {
      if (!detail || !detail.treatment) return []
      return [{ title: '治疗措施', content: detail.treatment }]
    },
    handleExport() {
      const payload = {
        exportedAt: new Date().toISOString(),
        cases: this.cases
      }
      const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `案例归档_${Date.now()}.json`
      link.click()
      URL.revokeObjectURL(url)
      this.$message.success('案例数据已导出')
    }
  }
}
</script>

<style scoped>
.archive-page {
  min-height: 100vh;
  background: #f7f8fa;
  position: relative;
}

.archive-header {
  background: #fff;
  padding: 28px 40px 20px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.header-title-row {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}

.header-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1.25;
}

.header-date {
  font-size: 14px;
  color: #86909c;
  white-space: nowrap;
  padding-top: 6px;
}

.header-desc {
  margin: 10px 0 0;
  font-size: 14px;
  color: #86909c;
  line-height: 1.5;
}

.archive-body {
  padding: 0 40px 48px;
}

.ai-banner {
  background: linear-gradient(135deg, #e8f3ff 0%, #f0f7ff 100%);
  border: 1px solid #bae0ff;
  border-radius: 12px;
  padding: 24px 28px;
  margin-bottom: 16px;
}

.ai-banner-head {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 16px;
}

.ai-icon {
  font-size: 28px;
  color: #1890ff;
  margin-top: 2px;
}

.ai-title {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}

.ai-desc {
  margin: 0;
  font-size: 14px;
  color: #4e5969;
}

.ai-info-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  border: 1px solid #eef0f3;
}

.ai-info-card p {
  margin: 0;
  font-size: 14px;
  color: #4e5969;
  line-height: 1.6;
}

.ai-stats {
  margin-top: 8px !important;
  font-size: 13px !important;
  color: #86909c !important;
}

.toolbar-card {
  background: #fff;
  border: 1px solid #eef0f3;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 16px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f2f3f5;
  border-radius: 8px;
  padding: 0 14px;
  height: 40px;
  min-width: 0;
}

.search-box i {
  color: #86909c;
  font-size: 15px;
  flex-shrink: 0;
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  color: #1d2129;
  min-width: 0;
}

.search-box input::placeholder {
  color: #c9cdd4;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.filter-select-wrap {
  position: relative;
  display: flex;
  align-items: center;
}

.category-dropdown {
  position: relative;
}

.category-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  padding: 0 14px;
  border: none;
  border-radius: 10px;
  background: #f2f3f5;
  color: #1d2129;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s;
}

.category-trigger:hover,
.category-trigger.open {
  background: #eef0f3;
}

.category-trigger .caret {
  font-size: 12px;
  color: #c9cdd4;
  transition: transform 0.2s;
}

.category-trigger.open .caret {
  transform: rotate(180deg);
}

.category-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  min-width: 160px;
  background: #fff;
  border: none;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  padding: 6px;
  z-index: 20;
}

.category-option {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: none;
  background: transparent;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #1d2129;
  cursor: pointer;
  text-align: left;
}

.category-option:hover {
  background: #f2f3f5;
}

.category-option.active {
  font-weight: 500;
}

.category-option .el-icon-check {
  color: #1d2129;
  font-weight: 700;
}

.category-select {
  width: 140px;
}

.category-select >>> .el-input__inner {
  background: #f2f3f5;
  border: none;
  border-radius: 8px;
  height: 40px;
  line-height: 40px;
  padding-left: 34px;
  padding-right: 28px;
  color: #1d2129;
  font-size: 14px;
}

.category-select >>> .el-input__suffix {
  right: 8px;
}

.btn-export {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 40px;
  padding: 0 16px;
  border: 1px solid #e5e6eb;
  border-radius: 10px;
  background: #fff;
  color: #1d2129;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s, border-color 0.15s;
}

.btn-export i {
  font-size: 15px;
}

.btn-export:hover {
  background: #f7f8fa;
  border-color: #d0d3d9;
}

.list-section {
  margin-top: 4px;
}

.list-tabs {
  display: inline-flex;
  gap: 4px;
  margin-bottom: 14px;
}

.list-tab {
  border: none;
  background: transparent;
  padding: 7px 16px;
  border-radius: 8px;
  font-size: 14px;
  color: #86909c;
  cursor: pointer;
  transition: all 0.15s;
  line-height: 1.4;
}

.list-tab.active {
  background: #eceef2;
  color: #1d2129;
  font-weight: 500;
}

.case-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 120px;
}

.case-card {
  background: #fff;
  border: 1px solid #eef0f3;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.case-head {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.case-id {
  font-size: 15px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1.3;
}

.category-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 8px;
  background: #f2f3f5;
  color: #4e5969;
  font-size: 12px;
  line-height: 1.6;
}

.match-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 8px;
  background: #f9f0ff;
  border: 1px solid #efdbff;
  color: #722ed1;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.6;
}

.case-meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 14px;
}

.case-summary {
  margin: 0;
  flex: 1;
  min-width: 0;
  font-size: 14px;
  color: #86909c;
  line-height: 1.5;
}

.case-meta-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.recovery-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.recovery-tag.good {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.recovery-tag.fair {
  background: #e8f3ff;
  color: #1890ff;
  border: 1px solid #91caff;
}

.recovery-tag.poor {
  background: #fff1f0;
  color: #f53f3f;
  border: 1px solid #ffccc7;
}

.btn-detail {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid #e5e6eb;
  background: #fff;
  color: #1d2129;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  padding: 7px 14px;
  border-radius: 8px;
  transition: background 0.15s;
}

.btn-detail:hover {
  background: #f7f8fa;
}

.btn-detail i {
  font-size: 14px;
  color: #4e5969;
}

.case-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.case-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 8px;
  background: #f7f8fa;
  border: 1px solid #eef0f3;
  color: #4e5969;
  font-size: 12px;
  line-height: 1.4;
}

.case-tag i {
  font-size: 12px;
  color: #c9cdd4;
}

.list-empty {
  padding: 64px 16px;
  text-align: center;
  color: #86909c;
  font-size: 14px;
  background: #fff;
  border: 1px solid #eef0f3;
  border-radius: 12px;
}

@media (max-width: 768px) {
  .archive-header,
  .archive-body {
    padding-left: 16px;
    padding-right: 16px;
  }

  .header-title {
    font-size: 22px;
  }

  .search-row {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-actions {
    justify-content: space-between;
  }

  .category-select {
    flex: 1;
  }

  .case-meta-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .case-meta-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>

<style>
.case-detail-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.case-detail-dialog .el-dialog__header {
  padding: 24px 28px 16px;
  border-bottom: none;
}

.case-detail-dialog .el-dialog__headerbtn {
  top: 22px;
  right: 22px;
  width: 32px;
  height: 32px;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
}

.case-detail-dialog .el-dialog__headerbtn .el-dialog__close {
  color: #86909c;
  font-size: 14px;
}

.case-detail-dialog .el-dialog__body {
  padding: 0 28px 28px;
  max-height: 70vh;
  overflow-y: auto;
}

.detail-dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1.4;
}

.detail-dialog-sub {
  margin: 6px 0 0;
  font-size: 14px;
  color: #86909c;
  line-height: 1.5;
}

.detail-dialog-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section-title {
  margin: 0 0 14px;
  font-size: 15px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1.4;
}

.detail-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 32px;
}

.detail-info-col {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-info-row {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  line-height: 1.5;
}

.detail-info-label {
  color: #86909c;
  flex-shrink: 0;
}

.detail-info-value {
  color: #1d2129;
}

.detail-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.case-detail-dialog .case-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 8px;
  background: #f7f8fa;
  border: 1px solid #eef0f3;
  color: #4e5969;
  font-size: 12px;
  line-height: 1.4;
}

.case-detail-dialog .case-tag i {
  font-size: 12px;
  color: #c9cdd4;
}

.case-detail-dialog .recovery-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.case-detail-dialog .recovery-tag.good {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.case-detail-dialog .recovery-tag.fair {
  background: #e8f3ff;
  color: #1890ff;
  border: 1px solid #91caff;
}

.case-detail-dialog .recovery-tag.poor {
  background: #fff1f0;
  color: #f53f3f;
  border: 1px solid #ffccc7;
}

.diagnosis-box {
  background: #f7f8fa;
  border-radius: 10px;
  padding: 16px 18px;
}

.diagnosis-line {
  margin: 0;
  font-size: 14px;
  color: #4e5969;
  line-height: 1.7;
}

.diagnosis-line + .diagnosis-line {
  margin-top: 12px;
}

.diagnosis-label {
  font-weight: 600;
  color: #1d2129;
}

.treatment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.treatment-card {
  border: 1px solid #eef0f3;
  border-radius: 10px;
  padding: 14px 16px;
  background: #fff;
}

.treatment-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 6px;
  line-height: 1.4;
}

.treatment-card-desc {
  font-size: 14px;
  color: #4e5969;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .detail-info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
