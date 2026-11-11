<template>
  <div class="knowledge-page">
    <header class="page-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">知识库</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">
        检索常见病害症状、用药与防治方案。管理员请使用「知识库管理」统一改删数据。
      </p>
      <div v-if="canEditKnowledge" class="header-actions">
        <button type="button" class="btn-ghost" @click="goManage">知识库管理</button>
        <button type="button" class="btn-primary" @click="openCreate">新增知识</button>
      </div>
    </header>

    <div class="page-body">
      <section class="toolbar-card">
        <div class="search-box">
          <i class="el-icon-search" />
          <input
            v-model="keyword"
            type="text"
            placeholder="搜索病害名称或症状"
          >
          <button
            v-if="keyword"
            type="button"
            class="clear-btn"
            @click="keyword = ''"
          >
            <i class="el-icon-close" />
          </button>
        </div>
        <div class="category-tabs">
          <button
            type="button"
            class="cat-tab"
            :class="{ active: categoryFilter === '' }"
            @click="categoryFilter = ''"
          >全部</button>
          <button
            v-for="cat in categories"
            :key="cat"
            type="button"
            class="cat-tab"
            :class="{ active: categoryFilter === cat }"
            @click="categoryFilter = cat"
          >{{ cat }}</button>
        </div>
      </section>

      <div v-loading="loading" class="knowledge-grid">
        <article
          v-for="item in filteredList"
          :key="item.id"
          class="knowledge-card"
          @click="openDetail(item)"
        >
          <div class="card-head">
            <div class="card-titles">
              <h3 class="card-title">{{ item.name }}</h3>
              <span class="card-tag">{{ item.category }}</span>
            </div>
            <span class="risk-badge" :class="item.riskLevel">
              {{ riskLabel(item.riskLevel) }}
            </span>
          </div>
          <p class="desc">{{ item.description }}</p>
          <div class="symptom-row">
            <span
              v-for="(s, idx) in (item.symptoms || []).slice(0, 4)"
              :key="item.id + '-s-' + idx"
              class="symptom-tag"
            >{{ s }}</span>
            <span
              v-if="(item.symptoms || []).length > 4"
              class="symptom-tag more"
            >+{{ item.symptoms.length - 4 }}</span>
          </div>
          <div class="card-foot">
            <span>查看详情 <i class="el-icon-arrow-right" /></span>
            <span v-if="canEditKnowledge" class="card-admin-actions" @click.stop>
              <button type="button" class="link-btn" @click="openEdit(item)">修改</button>
              <button type="button" class="link-btn danger" @click="handleDelete(item)">删除</button>
            </span>
          </div>
        </article>

        <div v-if="!loading && filteredList.length === 0" class="empty-box">
          未找到匹配的病害
        </div>
      </div>
    </div>

    <el-drawer
      :visible.sync="detailVisible"
      :title="detail.name || '病害详情'"
      size="480px"
      custom-class="knowledge-drawer"
      append-to-body
    >
      <div v-if="detail.id" class="drawer-body">
        <div class="drawer-tags">
          <span class="risk-badge" :class="detail.riskLevel">
            {{ riskLabel(detail.riskLevel) }}
          </span>
          <span class="card-tag">{{ detail.category }}</span>
        </div>
        <p class="drawer-desc">{{ detail.description }}</p>

        <div class="drawer-section">
          <strong>典型症状</strong>
          <div class="symptom-row">
            <span
              v-for="(s, idx) in detail.symptoms || []"
              :key="'d-s-' + idx"
              class="symptom-tag"
            >{{ s }}</span>
          </div>
        </div>

        <div class="drawer-section">
          <strong>推荐用药</strong>
          <p>{{ detail.medicine || '—' }}</p>
        </div>

        <div class="drawer-section">
          <strong>治疗方案</strong>
          <p>{{ detail.treatment || '—' }}</p>
        </div>

        <div class="drawer-section">
          <strong>预防措施</strong>
          <p>{{ detail.prevention || '—' }}</p>
        </div>

        <div v-if="canEditKnowledge" class="drawer-actions">
          <el-button type="primary" size="small" @click="openEdit(detail)">修改</el-button>
          <el-button type="danger" size="small" plain @click="handleDelete(detail)">删除</el-button>
        </div>
      </div>
    </el-drawer>

    <el-dialog
      :title="editMode ? '修改知识条目' : '新增知识条目'"
      :visible.sync="createVisible"
      width="520px"
      append-to-body
      custom-class="knowledge-create-dialog"
    >
      <el-form ref="createRef" :model="createForm" :rules="createRules" label-position="top">
        <el-form-item label="病害名称" prop="name">
          <el-input v-model="createForm.name" placeholder="如：细菌性烂鳃" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="createForm.category" placeholder="如：细菌病" />
        </el-form-item>
        <el-form-item label="症状（逗号分隔）">
          <el-input v-model="createForm.symptomsText" placeholder="烂鳃,浮头,摄食下降" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" :rows="2" placeholder="简要描述病害特征与危害" />
        </el-form-item>
        <el-form-item label="推荐用药">
          <el-input v-model="createForm.medicine" placeholder="如：聚维酮碘、恩诺沙星" />
        </el-form-item>
        <el-form-item label="治疗方案">
          <el-input v-model="createForm.treatment" type="textarea" :rows="2" placeholder="填写用药剂量、疗程与操作要点" />
        </el-form-item>
        <el-form-item label="预防措施">
          <el-input v-model="createForm.prevention" type="textarea" :rows="2" placeholder="填写日常防控与管理建议" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="createForm.riskLevel" style="width:100%" placeholder="请选择风险等级">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
            <el-option label="紧急" value="critical" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="submitForm">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchKnowledge,
  fetchKnowledgeDetail,
  fetchMeta,
  createKnowledge,
  updateKnowledge,
  deleteKnowledge
} from '@/api/fishDisease'
import { hasRole, getUser } from '@/views/fishDisease/utils/auth'
import {
  buildKnowledgePayload,
  bumpKnowledgeRev,
  readKnowledgeRev,
  nameRules,
  categoryRules
} from '@/views/fishDisease/utils/knowledgeHelpers'
import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'

export default {
  name: 'KnowledgeBase',
  components: { MenuToggle, HeaderAuth },
  inject: {
    switchFishNav: { default: null }
  },
  data() {
    return {
      loading: false,
      list: [],
      meta: { severityMap: {} },
      keyword: '',
      categoryFilter: '',
      detailVisible: false,
      detail: {},
      createVisible: false,
      creating: false,
      deleting: false,
      editMode: false,
      editingId: '',
      _knowledgeRev: '',
      loadSeq: 0,
      createForm: {
        name: '',
        category: '',
        symptomsText: '',
        description: '',
        medicine: '',
        treatment: '',
        prevention: '',
        riskLevel: 'medium'
      },
      createRules: {
        name: nameRules(),
        category: categoryRules()
      }
    }
  },
  computed: {
    canEditKnowledge() {
      const roles = (this.$store && this.$store.getters && this.$store.getters.roles) || []
      if (roles.length) {
        return roles.some(r =>
          /admin|expert|leader|管理员|专家|super|ke_admin|kede_group_leader/i.test(String(r))
        )
      }
      const user = getUser() || {}
      if (user.role && /admin|expert|leader|管理员|专家|super/i.test(String(user.role))) return true
      return hasRole('admin', 'expert', '管理员', '专家')
    },
    categories() {
      return [...new Set(this.list.map(item => item.category).filter(Boolean))]
    },
    filteredList() {
      let list = this.list
      if (this.categoryFilter) {
        list = list.filter(item => item.category === this.categoryFilter)
      }
      if (this.keyword.trim()) {
        const kw = this.keyword.trim().toLowerCase()
        list = list.filter(item =>
          (item.name || '').toLowerCase().includes(kw) ||
          (item.description || '').toLowerCase().includes(kw) ||
          (item.symptoms || []).some(s => String(s).toLowerCase().includes(kw))
        )
      }
      return list
    }
  },
  watch: {
    categories(list) {
      if (this.categoryFilter && !list.includes(this.categoryFilter)) {
        this.categoryFilter = ''
      }
    }
  },
  created() {
    this.init()
  },
  activated() {
    this._knowledgeRev = readKnowledgeRev()
    this.init()
  },
  methods: {
    emptyForm() {
      return {
        name: '',
        category: '',
        symptomsText: '',
        description: '',
        medicine: '',
        treatment: '',
        prevention: '',
        riskLevel: 'medium'
      }
    },
    fillFormFromItem(item) {
      return {
        name: item.name || '',
        category: item.category || '',
        symptomsText: Array.isArray(item.symptoms) ? item.symptoms.join('、') : String(item.symptoms || ''),
        description: item.description || '',
        medicine: item.medicine || '',
        treatment: item.treatment || '',
        prevention: item.prevention || '',
        riskLevel: item.riskLevel || 'medium'
      }
    },
    async init() {
      const seq = ++this.loadSeq
      this.loading = true
      try {
        const [listRes, metaRes] = await Promise.all([
          fetchKnowledge(),
          fetchMeta()
        ])
        if (seq !== this.loadSeq) return
        this.list = Array.isArray(listRes && listRes.data) ? listRes.data : []
        this.meta = (metaRes && metaRes.data) || { severityMap: {} }
        if (this.detail && this.detail.id && !this.list.some(i => i.id === this.detail.id)) {
          this.detailVisible = false
          this.detail = {}
        }
      } catch (e) {
        if (seq !== this.loadSeq) return
        this.list = []
        this.$message.warning('知识库加载失败，请稍后重试')
      } finally {
        if (seq === this.loadSeq) this.loading = false
      }
    },
    async openDetail(item) {
      if (!item || !item.id) return
      try {
        const res = await fetchKnowledgeDetail(item.id)
        this.detail = (res && res.data) || item
        this.detailVisible = true
      } catch (e) {
        this.$message.error((e && e.message) || '该知识不存在或已被删除')
        this.init()
      }
    },
    riskLabel(level) {
      return (this.meta.severityMap && this.meta.severityMap[level]) || level || '未知'
    },
    goManage() {
      if (!this.canEditKnowledge) {
        this.$message.warning('普通用户无权限访问知识库管理')
        return
      }
      if (typeof this.switchFishNav === 'function') {
        this.switchFishNav('knowledgeManage')
      }
    },
    openCreate() {
      if (!this.canEditKnowledge) {
        this.$message.warning('普通用户无权限管理知识库')
        return
      }
      this.editMode = false
      this.editingId = ''
      this.createForm = this.emptyForm()
      this.createVisible = true
      this.$nextTick(() => {
        if (this.$refs.createRef) this.$refs.createRef.clearValidate()
      })
    },
    openEdit(item) {
      if (!this.canEditKnowledge) {
        this.$message.warning('普通用户无权限修改知识库')
        return
      }
      if (!item || !item.id) return
      this.editMode = true
      this.editingId = item.id
      this.createForm = this.fillFormFromItem(item)
      this.createVisible = true
      this.$nextTick(() => {
        if (this.$refs.createRef) this.$refs.createRef.clearValidate()
      })
    },
    submitForm() {
      if (!this.canEditKnowledge) {
        this.$message.warning('普通用户无权限管理知识库')
        return
      }
      if (this.creating) return
      this.$refs.createRef.validate(async valid => {
        if (!valid) return
        const built = buildKnowledgePayload(this.createForm, {
          checkDuplicate: true,
          list: this.list,
          excludeId: this.editMode ? this.editingId : ''
        })
        if (!built.ok) {
          this.$message.warning(built.message)
          return
        }
        this.creating = true
        try {
          if (this.editMode && this.editingId) {
            const res = await updateKnowledge(this.editingId, built.payload)
            this.$message.success('知识条目已更新')
            if (this.detail && this.detail.id === this.editingId) {
              this.detail = (res && res.data) || { ...this.detail, ...built.payload, id: this.editingId }
            }
          } else {
            await createKnowledge(built.payload)
            this.$message.success('知识条目已添加')
          }
          bumpKnowledgeRev()
          this.createVisible = false
          await this.init()
        } catch (e) {
          // interceptor
        } finally {
          this.creating = false
        }
      })
    },
    handleDelete(item) {
      if (!this.canEditKnowledge) {
        this.$message.warning('普通用户无权限删除知识库')
        return
      }
      if (!item || !item.id || this.deleting) return
      this.$confirm(`确认删除知识「${item.name || item.id}」？删除后不可恢复。`, '删除确认', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      })
        .then(async () => {
          this.deleting = true
          try {
            await deleteKnowledge(item.id)
            this.$message.success('已删除')
            bumpKnowledgeRev()
            if (this.detail && this.detail.id === item.id) {
              this.detailVisible = false
              this.detail = {}
            }
            await this.init()
          } catch (e) {
            await this.init()
          } finally {
            this.deleting = false
          }
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.knowledge-page {
  min-height: 100vh;
  background: #0a1520;
  position: relative;
}

.page-header {
  background: #0f1a26;
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
  color: #e8edf2;
  line-height: 1.25;
}

.header-desc {
  margin: 10px 0 0;
  font-size: 14px;
  color: #7c8fa1;
  line-height: 1.5;
}
.header-actions {
  margin-top: 12px;
}
.btn-primary {
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  background: #3b82f6;
  color: #fff;
  cursor: pointer;
  font-size: 13px;
}
.btn-primary:hover {
  background: #2563eb;
}
.btn-ghost {
  border: 1px solid rgba(124, 143, 161, 0.35);
  border-radius: 8px;
  padding: 8px 16px;
  background: transparent;
  color: #9eb0c2;
  cursor: pointer;
  font-size: 13px;
}
.btn-ghost:hover {
  color: #e5eef7;
  border-color: rgba(64, 158, 255, 0.5);
}

.page-body {
  padding: 0 40px 48px;
}

.toolbar-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 16px;
  padding: 16px 20px;
  margin-bottom: 16px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 14px;
}

.search-box {
  flex: 1;
  min-width: 220px;
  max-width: 360px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(64, 158, 255, 0.1);
  border-radius: 10px;
  padding: 0 14px;
  height: 40px;
}

.search-box i {
  color: #7c8fa1;
  font-size: 15px;
  flex-shrink: 0;
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  color: #e8edf2;
  min-width: 0;
}

.search-box input::placeholder {
  color: #5a6b7d;
}

.clear-btn {
  border: none;
  background: transparent;
  color: #5a6b7d;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.clear-btn:hover {
  color: #e8edf2;
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cat-tab {
  border: 1px solid rgba(124, 143, 161, 0.28);
  background: transparent;
  color: #7c8fa1;
  border-radius: 10px;
  padding: 7px 14px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}

.cat-tab:hover {
  background: rgba(64, 158, 255, 0.08);
  color: #e8edf2;
}

.cat-tab.active {
  background: rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.45);
  color: #409eff;
  font-weight: 600;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  min-height: 120px;
}

.knowledge-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 16px;
  padding: 22px 24px;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s, transform 0.15s;
}

.knowledge-card:hover {
  border-color: rgba(64, 158, 255, 0.45);
  background: #132030;
  transform: translateY(-1px);
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.card-titles {
  min-width: 0;
}

.card-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #e8edf2;
  line-height: 1.35;
}

.card-tag {
  display: inline-block;
  margin-top: 6px;
  font-size: 12px;
  color: #409eff;
  background: rgba(64, 158, 255, 0.12);
  border-radius: 6px;
  padding: 2px 8px;
}

.risk-badge {
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 600;
  border-radius: 8px;
  padding: 4px 10px;
  line-height: 1.2;
  background: rgba(124, 143, 161, 0.15);
  color: #7c8fa1;
}

.risk-badge.critical,
.risk-badge.high {
  background: rgba(255, 77, 79, 0.15);
  color: #ff7875;
}

.risk-badge.medium {
  background: rgba(250, 173, 20, 0.15);
  color: #faad14;
}

.risk-badge.low {
  background: rgba(82, 196, 26, 0.15);
  color: #73d13d;
}

.desc {
  margin: 0 0 14px;
  font-size: 13px;
  color: #7c8fa1;
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
  margin-bottom: 12px;
}

.symptom-tag {
  background: rgba(64, 158, 255, 0.12);
  color: #69b1ff;
  border-radius: 8px;
  padding: 3px 10px;
  font-size: 12px;
}

.symptom-tag.more {
  background: #132030;
  color: #5a6b7d;
}

.card-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  font-size: 13px;
  color: #409eff;
}

.card-admin-actions {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.link-btn {
  border: none;
  background: transparent;
  padding: 0;
  font-size: 12px;
  color: #7c8fa1;
  cursor: pointer;
}

.link-btn:hover {
  color: #409eff;
}

.link-btn.danger:hover {
  color: #f56c6c;
}

.drawer-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(124, 143, 161, 0.18);
}

.empty-box {
  grid-column: 1 / -1;
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 16px;
  padding: 48px 16px;
  text-align: center;
  color: #7c8fa1;
}

.drawer-body {
  padding: 0 4px 16px;
}

.drawer-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.drawer-desc {
  margin: 12px 0 18px;
  font-size: 14px;
  color: #7c8fa1;
  line-height: 1.65;
}

.drawer-section {
  margin-bottom: 18px;
}

.drawer-section strong {
  display: block;
  font-size: 13px;
  color: #e8edf2;
  margin-bottom: 8px;
}

.drawer-section p {
  margin: 0;
  font-size: 13px;
  color: #7c8fa1;
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .page-header,
  .page-body { padding-left: 20px; padding-right: 20px; }
}

@media (max-width: 640px) {
  .header-title { font-size: 22px; }
  .search-box { max-width: none; }
}
</style>

<style>
/* drawer teleport 到 body，需非 scoped */
.knowledge-drawer {
  background: #0f1a26 !important;
}

.knowledge-drawer .el-drawer__header {
  margin-bottom: 16px;
  padding: 20px 20px 12px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.22);
  color: #e8edf2 !important;
}

.knowledge-drawer .el-drawer__title {
  color: #e8edf2 !important;
  font-weight: 600;
}

.knowledge-drawer .el-drawer__close-btn {
  color: #7c8fa1 !important;
}

.knowledge-drawer .el-drawer__body {
  background: #0f1a26;
  color: #7c8fa1;
  padding: 8px 20px 24px;
}
</style>
