<template>
  <div class="knowledge-manage-page">
    <header class="page-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">知识库管理</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">
        管理员专用：维护鱼病知识库已有数据（修改 / 删除）。普通用户不可见本页。
      </p>
      <div class="header-actions">
        <button type="button" class="btn-ghost" @click="goBrowse">返回知识库浏览</button>
        <button type="button" class="btn-primary" @click="openCreate">新增知识</button>
      </div>
    </header>

    <div class="page-body">
      <section class="toolbar-card">
        <div class="search-box">
          <i class="el-icon-search" />
          <input v-model="keyword" type="text" placeholder="按名称、分类、症状筛选">
          <button v-if="keyword" type="button" class="clear-btn" @click="keyword = ''">
            <i class="el-icon-close" />
          </button>
        </div>
        <div class="toolbar-meta">共 {{ filteredList.length }} 条</div>
      </section>

      <div v-loading="loading" class="table-wrap">
        <el-table
          :data="filteredList"
          stripe
          height="100%"
          class="manage-table"
          empty-text="暂无知识数据"
        >
          <el-table-column prop="name" label="病害名称" min-width="140" show-overflow-tooltip />
          <el-table-column prop="category" label="分类" width="110" show-overflow-tooltip />
          <el-table-column label="症状" min-width="180" show-overflow-tooltip>
            <template slot-scope="{ row }">
              {{ (row.symptoms || []).join('、') || '—' }}
            </template>
          </el-table-column>
          <el-table-column prop="medicine" label="推荐用药" min-width="140" show-overflow-tooltip />
          <el-table-column label="风险" width="88" align="center">
            <template slot-scope="{ row }">
              <span class="risk-badge" :class="row.riskLevel">{{ riskLabel(row.riskLevel) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="168" fixed="right" align="center">
            <template slot-scope="{ row }">
              <el-button type="text" size="small" @click="openEdit(row)">修改</el-button>
              <el-button type="text" size="small" class="danger-text" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog
      :title="editMode ? '修改知识条目' : '新增知识条目'"
      :visible.sync="formVisible"
      width="520px"
      append-to-body
      custom-class="knowledge-create-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="病害名称" prop="name">
          <el-input v-model="form.name" placeholder="如：细菌性烂鳃" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="如：细菌病" />
        </el-form-item>
        <el-form-item label="症状（逗号分隔）">
          <el-input v-model="form.symptomsText" placeholder="烂鳃,浮头,摄食下降" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="简要描述病害特征与危害" />
        </el-form-item>
        <el-form-item label="推荐用药">
          <el-input v-model="form.medicine" placeholder="如：聚维酮碘、恩诺沙星" />
        </el-form-item>
        <el-form-item label="治疗方案">
          <el-input v-model="form.treatment" type="textarea" :rows="2" placeholder="填写用药剂量、疗程与操作要点" />
        </el-form-item>
        <el-form-item label="预防措施">
          <el-input v-model="form.prevention" type="textarea" :rows="2" placeholder="填写日常防控与管理建议" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="form.riskLevel" style="width:100%" placeholder="请选择风险等级">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
            <el-option label="紧急" value="critical" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchKnowledge,
  fetchMeta,
  createKnowledge,
  updateKnowledge,
  deleteKnowledge
} from '@/api/fishDisease'
import { hasRole, getUser } from '@/views/fishDisease/utils/auth'
import {
  buildKnowledgePayload,
  bumpKnowledgeRev,
  nameRules,
  categoryRules
} from '@/views/fishDisease/utils/knowledgeHelpers'
import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'

const ELEVATED_RE = /admin|expert|leader|管理员|专家|super|ke_admin|kede_group_leader/i

export default {
  name: 'KnowledgeManage',
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
      formVisible: false,
      saving: false,
      deleting: false,
      editMode: false,
      editingId: '',
      loadSeq: 0,
      form: this.emptyForm(),
      rules: {
        name: nameRules(),
        category: categoryRules()
      }
    }
  },
  computed: {
    canManage() {
      const roles = (this.$store && this.$store.getters && this.$store.getters.roles) || []
      if (roles.length) return roles.some(r => ELEVATED_RE.test(String(r)))
      const user = getUser() || {}
      if (user.role && ELEVATED_RE.test(String(user.role))) return true
      return hasRole('admin', 'expert', '管理员', '专家')
    },
    filteredList() {
      const kw = this.keyword.trim().toLowerCase()
      if (!kw) return this.list
      return this.list.filter(item =>
        (item.name || '').toLowerCase().includes(kw) ||
        (item.category || '').toLowerCase().includes(kw) ||
        (item.medicine || '').toLowerCase().includes(kw) ||
        (item.symptoms || []).some(s => String(s).toLowerCase().includes(kw))
      )
    }
  },
  created() {
    if (!this.canManage) {
      this.$message.warning('普通用户无权限访问知识库管理')
      this.goBrowse()
      return
    }
    this.init()
  },
  activated() {
    if (!this.canManage) {
      this.goBrowse()
      return
    }
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
    fillForm(item) {
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
    riskLabel(level) {
      return (this.meta.severityMap && this.meta.severityMap[level]) || level || '未知'
    },
    goBrowse() {
      if (typeof this.switchFishNav === 'function') {
        this.switchFishNav('knowledge')
      }
    },
    async init() {
      const seq = ++this.loadSeq
      this.loading = true
      try {
        const [listRes, metaRes] = await Promise.all([fetchKnowledge(), fetchMeta()])
        if (seq !== this.loadSeq) return
        this.list = Array.isArray(listRes && listRes.data) ? listRes.data : []
        this.meta = (metaRes && metaRes.data) || { severityMap: {} }
      } catch (e) {
        if (seq !== this.loadSeq) return
        this.list = []
        this.$message.warning('知识库加载失败，请稍后重试')
      } finally {
        if (seq === this.loadSeq) this.loading = false
      }
    },
    openCreate() {
      if (!this.canManage || this.saving) return
      this.editMode = false
      this.editingId = ''
      this.form = this.emptyForm()
      this.formVisible = true
      this.$nextTick(() => this.$refs.formRef && this.$refs.formRef.clearValidate())
    },
    openEdit(row) {
      if (!this.canManage || !row || !row.id || this.saving) return
      this.editMode = true
      this.editingId = row.id
      this.form = this.fillForm(row)
      this.formVisible = true
      this.$nextTick(() => this.$refs.formRef && this.$refs.formRef.clearValidate())
    },
    submitForm() {
      if (!this.canManage || this.saving) return
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        const built = buildKnowledgePayload(this.form, {
          checkDuplicate: true,
          list: this.list,
          excludeId: this.editMode ? this.editingId : ''
        })
        if (!built.ok) {
          this.$message.warning(built.message)
          return
        }
        this.saving = true
        try {
          if (this.editMode && this.editingId) {
            await updateKnowledge(this.editingId, built.payload)
            this.$message.success('已修改')
          } else {
            await createKnowledge(built.payload)
            this.$message.success('已新增，可在「知识库」浏览页看到')
          }
          bumpKnowledgeRev()
          this.formVisible = false
          await this.init()
        } catch (e) {
          // interceptor
        } finally {
          this.saving = false
        }
      })
    },
    handleDelete(row) {
      if (!this.canManage || !row || !row.id || this.deleting) return
      this.$confirm(`确认删除知识「${row.name || row.id}」？删除后不可恢复。`, '删除确认', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      })
        .then(async () => {
          this.deleting = true
          try {
            await deleteKnowledge(row.id)
            this.$message.success('已删除')
            bumpKnowledgeRev()
            if (this.editingId === row.id) {
              this.formVisible = false
              this.editingId = ''
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
.knowledge-manage-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  background: #0a1520;
  color: #e5eef7;
}

.page-header {
  flex-shrink: 0;
  background: #0f1a26;
  padding: 20px 24px 16px;
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

.header-actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.btn-primary,
.btn-ghost {
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 13px;
  cursor: pointer;
}

.btn-primary {
  background: #409eff;
  color: #fff;
}

.btn-primary:hover {
  background: #66b1ff;
}

.btn-ghost {
  background: transparent;
  color: #9eb0c2;
  border: 1px solid rgba(124, 143, 161, 0.35);
}

.btn-ghost:hover {
  color: #e5eef7;
  border-color: rgba(64, 158, 255, 0.5);
}

.page-body {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 16px 20px 20px;
  gap: 12px;
}

.toolbar-card {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 12px;
  padding: 10px 12px;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  background: #0a1520;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 8px;
  padding: 0 10px;
}

.search-box i {
  color: #7c8fa1;
}

.search-box input {
  flex: 1;
  height: 34px;
  border: none;
  outline: none;
  background: transparent;
  color: #e5eef7;
  font-size: 13px;
}

.clear-btn {
  border: none;
  background: transparent;
  color: #7c8fa1;
  cursor: pointer;
}

.toolbar-meta {
  font-size: 12px;
  color: #7c8fa1;
  white-space: nowrap;
}

.table-wrap {
  flex: 1;
  min-height: 0;
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 12px;
  overflow: hidden;
  padding: 4px;
}

.manage-table {
  background: transparent;
}

.manage-table ::v-deep .el-table,
.manage-table ::v-deep .el-table__expanded-cell {
  background: transparent;
  color: #e5eef7;
}

.manage-table ::v-deep th.el-table__cell {
  background: #132030 !important;
  color: #9eb0c2;
  border-bottom-color: rgba(124, 143, 161, 0.18);
}

.manage-table ::v-deep td.el-table__cell {
  background: transparent !important;
  border-bottom-color: rgba(124, 143, 161, 0.12);
}

.manage-table ::v-deep .el-table__row:hover > td.el-table__cell {
  background: rgba(64, 158, 255, 0.08) !important;
}

.manage-table ::v-deep .el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: rgba(255, 255, 255, 0.02) !important;
}

.manage-table ::v-deep .el-button--text {
  color: #409eff;
}

.danger-text {
  color: #f56c6c !important;
}

.risk-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  line-height: 18px;
}

.risk-badge.low { background: rgba(103, 194, 58, 0.15); color: #67c23a; }
.risk-badge.medium { background: rgba(230, 162, 60, 0.15); color: #e6a23c; }
.risk-badge.high { background: rgba(245, 108, 108, 0.15); color: #f56c6c; }
.risk-badge.critical { background: rgba(245, 108, 108, 0.28); color: #ff7875; }
</style>
