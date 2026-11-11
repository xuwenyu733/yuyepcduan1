<template>
  <div class="templates-page">
    <div class="page-card">
      <h2 class="section-title">创建专项实验模板</h2>
      <div class="type-grid">
        <button
          v-for="item in types"
          :key="item.key"
          type="button"
          class="type-card"
          :class="{ active: selectedType === item.key }"
          @click="handleCreateByType(item)"
        >
          <span class="type-icon">{{ item.icon }}</span>
          <span class="type-label">{{ item.label }}</span>
        </button>
      </div>
    </div>

    <div class="page-card list-card">
      <h2 class="section-title">我的实验模板（{{ templates.length }}）</h2>
      <el-empty v-if="!loading && templates.length === 0" description="暂无模板，请先创建" />
      <div v-loading="loading" class="template-list">
        <div v-for="item in templates" :key="item.id" class="template-item">
          <div class="tpl-header">
            <div class="tpl-info">
              <i class="el-icon-document tpl-doc" />
              <div>
                <div class="tpl-name">{{ item.name }}</div>
                <div class="tpl-meta">
                  {{ item.typeLabel }} · {{ item.fields.length }} 个字段 · {{ item.updatedAt }}
                </div>
              </div>
            </div>
            <div class="tpl-actions">
              <el-button size="mini" type="primary" plain @click="handleView(item)">查看</el-button>
              <el-button size="mini" icon="el-icon-document-copy" circle @click="handleCopy(item)" />
              <el-button size="mini" type="danger" icon="el-icon-delete" circle @click="handleDelete(item)" />
            </div>
          </div>
          <div class="field-grid">
            <div
              v-for="field in item.fields.slice(0, 8)"
              :key="field.key"
              class="field-chip"
            >
              <span v-if="field.required" class="required">*</span>
              {{ field.label }}
            </div>
            <div v-if="item.fields.length > 8" class="field-more">
              +{{ item.fields.length - 8 }} 更多...
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      :visible.sync="viewVisible"
      width="640px"
      custom-class="template-detail-dialog"
      :show-close="true"
    >
      <div v-if="viewItem" slot="title" class="detail-title-wrap">
        <div class="detail-title">{{ viewItem.name }}</div>
        <div class="detail-created">创建时间: {{ viewItem.createdAt || viewItem.updatedAt }}</div>
      </div>

      <div v-if="viewItem" class="detail-body">
        <div v-if="viewItem.description" class="detail-desc">{{ viewItem.description }}</div>
        <div class="detail-section-title">字段定义 ({{ viewItem.fields.length }})</div>
        <div class="detail-field-list">
          <div
            v-for="field in viewItem.fields"
            :key="field.key"
            class="detail-field-card"
          >
            <div class="detail-field-main">
              <div class="detail-field-name">{{ field.label }}</div>
              <div v-if="field.unit" class="detail-field-unit">单位: {{ field.unit }}</div>
              <div v-if="field.options && field.options.length" class="detail-field-unit">
                选项: {{ field.options.join('，') }}
              </div>
            </div>
            <div class="detail-field-tags">
              <span v-if="field.required" class="tag-required">必填</span>
              <span class="tag-type">{{ typeLabel(field.type) }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchTemplateTypes,
  fetchTemplates,
  createTemplate,
  copyTemplate,
  deleteTemplate
} from '@/api/experiment'

export default {
  name: 'ExperimentTemplates',
  data() {
    return {
      loading: false,
      selectedType: '',
      types: [],
      templates: [],
      viewVisible: false,
      viewItem: null
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const typeRes = await fetchTemplateTypes()
      this.types = typeRes.data
      await this.loadList()
    },
    async loadList() {
      this.loading = true
      try {
        const res = await fetchTemplates()
        this.templates = res.data
      } finally {
        this.loading = false
      }
    },
    handleCreateByType(item) {
      this.selectedType = item.key
      this.$confirm(`确认为「${item.label}」创建默认模板吗？`, '创建模板', { type: 'info' })
        .then(async () => {
          await createTemplate({ type: item.key })
          this.$message.success('模板已添加到下方列表')
          await this.loadList()
        })
        .catch(() => {})
    },
    handleView(item) {
      this.viewItem = item
      this.viewVisible = true
    },
    typeLabel(type) {
      return {
        text: '文本',
        textarea: '多行文本',
        number: '数字',
        date: '日期',
        select: '选择'
      }[type] || type
    },
    handleCopy(item) {
      copyTemplate(item.id).then(async () => {
        this.$message.success('已在下方创建副本')
        await this.loadList()
      })
    },
    handleDelete(item) {
      this.$confirm(`确定删除模板「${item.name}」吗？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteTemplate(item.id)
          this.$message.success('删除成功')
          this.loadList()
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.templates-page {
  color: rgba(255, 255, 255, 0.88);
}

.page-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: none;
}

.section-title {
  margin: 0 0 14px;
  font-size: 15px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.type-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-height: 104px;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.03);
  cursor: pointer;
  transition: border-color 0.15s ease, background 0.15s ease;
}

.type-card:hover,
.type-card.active {
  border-color: rgba(64, 158, 255, 0.45);
  background: rgba(64, 158, 255, 0.1);
}

.type-icon {
  font-size: 26px;
}

.type-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

.list-card {
  margin-top: 14px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.template-item {
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 10px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.025);
}

.tpl-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.tpl-info {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.tpl-doc {
  color: #409eff;
  font-size: 18px;
  margin-top: 2px;
}

.tpl-name {
  font-size: 15px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.92);
}

.tpl-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #7c8fa1;
}

.tpl-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.field-chip,
.field-more {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(124, 143, 161, 0.14);
  border-radius: 6px;
  padding: 8px 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
}

.required {
  color: #f56c6c;
  margin-right: 2px;
}

.detail-title-wrap {
  padding-right: 24px;
}

.detail-title {
  font-size: 17px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.92);
}

.detail-created {
  margin-top: 6px;
  font-size: 13px;
  color: #7c8fa1;
}

.detail-desc {
  margin-bottom: 12px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(124, 143, 161, 0.14);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 13px;
  line-height: 1.5;
}

.detail-section-title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.88);
}

.detail-field-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 520px;
  overflow: auto;
}

.detail-field-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.03);
}

.detail-field-name {
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.88);
}

.detail-field-unit {
  margin-top: 4px;
  font-size: 12px;
  color: #7c8fa1;
}

.detail-field-tags {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.tag-required,
.tag-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 20px;
}

.tag-required {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.12);
  border: 1px solid rgba(245, 108, 108, 0.28);
}

.tag-type {
  color: #409eff;
  background: rgba(64, 158, 255, 0.12);
  border: 1px solid rgba(64, 158, 255, 0.28);
}

@media (max-width: 900px) {
  .type-grid,
  .field-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .type-grid,
  .field-grid {
    grid-template-columns: 1fr;
  }

  .tpl-header {
    flex-direction: column;
  }
}
</style>
