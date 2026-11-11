<template>
  <div class="experiment-page">
    <div class="toolbar page-card">
      <el-select v-model="workshop" placeholder="全部车间" clearable class="filter-select" @change="applyFilters">
        <el-option label="全部车间" value="" />
        <el-option label="1号车间" value="1" />
        <el-option label="2号车间" value="2" />
        <el-option label="3号车间" value="3" />
      </el-select>
      <el-select v-model="system" placeholder="全部系统" clearable class="filter-select" @change="applyFilters">
        <el-option label="全部系统" value="" />
        <el-option label="1号系统" value="1" />
        <el-option label="2号系统" value="2" />
        <el-option label="3号系统" value="3" />
      </el-select>
      <el-input
        v-model="keyword"
        placeholder="搜索实验编号、标题、品种..."
        prefix-icon="el-icon-search"
        clearable
        class="search-input"
        @keyup.enter.native="applyFilters"
        @clear="applyFilters"
      />
      <el-select v-model="status" placeholder="全部状态" clearable class="status-select" @change="applyFilters">
        <el-option label="全部状态" value="" />
        <el-option
          v-for="(label, key) in statusMap"
          :key="key"
          :label="label"
          :value="key"
        />
      </el-select>
      <el-button type="primary" icon="el-icon-plus" @click="openCreate">新建实验</el-button>
    </div>

    <div v-loading="loading" class="content page-card">
      <template v-if="!loading && filteredList.length === 0">
        <div class="empty-state">
          <i class="el-icon-folder-opened empty-icon" />
          <p>暂无实验数据</p>
          <el-button type="text" @click="openCreate">创建第一个实验</el-button>
        </div>
      </template>

      <div v-else class="card-grid">
        <div v-for="item in filteredList" :key="item.id" class="experiment-card">
          <div class="card-header">
            <div>
              <h3 class="card-title">{{ item.name }}</h3>
              <div class="card-sub">{{ item.id }} · {{ item.species || '未填品种' }}</div>
            </div>
            <el-tag size="small" :type="statusTagType(item.status)" effect="plain">
              {{ statusMap[item.status] || item.status }}
            </el-tag>
          </div>
          <ul class="meta">
            <li v-if="item.workshop"><i class="el-icon-office-building" />车间：{{ formatWorkshop(item.workshop) }}</li>
            <li v-if="item.system"><i class="el-icon-cpu" />系统：{{ formatSystem(item.system) }}</li>
            <li><i class="el-icon-office-building" />池号：{{ item.poolNo || '-' }}</li>
            <li><i class="el-icon-user" />负责人：{{ item.manager }}</li>
            <li><i class="el-icon-date" />周期：{{ item.startDate }} ~ {{ item.endDate || '-' }}</li>
            <li v-if="item.pools && item.pools.length">
              <i class="el-icon-s-grid" />实验池：{{ item.pools.length }} 个
            </li>
            <li v-if="item.metric && item.metric.value" class="metric">
              <i class="el-icon-data-analysis" />{{ item.metric.label }}：{{ item.metric.value }}
            </li>
            <li v-if="item.conclusion || item.effectEvaluation" class="conclusion">
              <i class="el-icon-document-checked" />结论：{{ item.conclusion || item.effectEvaluation }}
            </li>
          </ul>
          <div class="actions">
            <el-button size="small" type="primary" plain @click="handleView(item)">查看</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      title="新建实验"
      :visible.sync="createVisible"
      width="760px"
      append-to-body
      :close-on-click-modal="false"
      @closed="resetCreateForm"
    >
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-position="top">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="实验编号" prop="id">
              <el-input v-model="createForm.id" placeholder="例如: EXP-2024-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实验标题" prop="name">
              <el-input v-model="createForm.name" placeholder="实验名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车间" prop="workshop">
              <el-select v-model="createForm.workshop" style="width: 100%">
                <el-option label="1号车间" value="1" />
                <el-option label="2号车间" value="2" />
                <el-option label="3号车间" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="系统" prop="system">
              <el-select v-model="createForm.system" style="width: 100%">
                <el-option label="1号系统" value="1" />
                <el-option label="2号系统" value="2" />
                <el-option label="3号系统" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="池号" prop="poolNo">
              <el-input v-model="createForm.poolNo" placeholder="例如: A1, B2" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品种" prop="species">
              <el-input v-model="createForm.species" placeholder="例如: 草鱼" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="苗种来源" prop="seedSource">
              <el-input v-model="createForm.seedSource" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="manager">
              <el-input v-model="createForm.manager" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="createForm.startDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="createForm.endDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="createForm.status" style="width: 100%">
                <el-option v-for="(label, key) in statusMap" :key="key" :label="label" :value="key" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实验目标" prop="goal">
              <el-input v-model="createForm.goal" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实验内容" prop="content">
              <el-input v-model="createForm.content" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实验方法" prop="method">
              <el-input v-model="createForm.method" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实验结论 / 效果评估">
              <el-input
                v-model="createForm.conclusion"
                type="textarea"
                :rows="3"
                placeholder="实验完成后填写结论与效果评估，进行中可留空"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer">
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="submitCreate">创建</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchExperimentMeta,
  fetchExperiments,
  createExperiment,
  deleteExperiment
} from '@/api/experiment'

function emptyCreateForm() {
  return {
    id: '',
    name: '',
    workshop: '1',
    system: '1',
    poolNo: '',
    species: '',
    seedSource: '',
    manager: '',
    startDate: '',
    endDate: '',
    status: 'planned',
    goal: '',
    content: '',
    method: '',
    conclusion: ''
  }
}

export default {
  name: 'ExperimentList',
  inject: {
    openExperimentCreate: { default: null }
  },
  data() {
    return {
      loading: false,
      creating: false,
      createVisible: false,
      keyword: '',
      status: '',
      workshop: '',
      system: '',
      statusMap: {},
      list: [],
      createForm: emptyCreateForm(),
      createRules: {
        id: [{ required: true, message: '请填写实验编号', trigger: 'blur' }],
        name: [{ required: true, message: '请填写实验标题', trigger: 'blur' }],
        workshop: [{ required: true, message: '请选择车间', trigger: 'change' }],
        system: [{ required: true, message: '请选择系统', trigger: 'change' }],
        poolNo: [{ required: true, message: '请填写池号', trigger: 'blur' }],
        species: [{ required: true, message: '请填写品种', trigger: 'blur' }],
        seedSource: [{ required: true, message: '请填写苗种来源', trigger: 'blur' }],
        manager: [{ required: true, message: '请填写负责人', trigger: 'blur' }],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }],
        goal: [{ required: true, message: '请填写实验目标', trigger: 'blur' }],
        content: [{ required: true, message: '请填写实验内容', trigger: 'blur' }],
        method: [{ required: true, message: '请填写实验方法', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filteredList() {
      return this.list.filter(item => {
        if (this.workshop && item.workshop !== this.workshop) return false
        if (this.system && item.system !== this.system) return false
        return true
      })
    }
  },
  created() {
    this.init()
  },
  activated() {
    if (this.statusMap && Object.keys(this.statusMap).length) {
      this.loadList()
    }
  },
  methods: {
    formatWorkshop(value) {
      return value ? `${value}号车间` : '-'
    },
    formatSystem(value) {
      return value ? `${value}号系统` : '-'
    },
    async init() {
      const metaRes = await fetchExperimentMeta()
      this.statusMap = metaRes.data.statusMap
      await this.loadList()
    },
    async loadList() {
      this.loading = true
      try {
        const res = await fetchExperiments({
          keyword: this.keyword,
          status: this.status
        })
        this.list = res.data
      } finally {
        this.loading = false
      }
    },
    applyFilters() {
      this.loadList()
    },
    openCreate() {
      if (typeof this.openExperimentCreate === 'function') {
        this.openExperimentCreate()
        return
      }
      this.createForm = emptyCreateForm()
      this.createVisible = true
      this.$nextTick(() => {
        this.$refs.createFormRef && this.$refs.createFormRef.clearValidate()
      })
    },
    resetCreateForm() {
      this.createForm = emptyCreateForm()
    },
    submitCreate() {
      this.$refs.createFormRef.validate(async valid => {
        if (!valid) return
        if (this.createForm.endDate < this.createForm.startDate) {
          this.$message.warning('结束日期不能早于开始日期')
          return
        }
        this.creating = true
        try {
          await createExperiment({
            ...this.createForm,
            effectEvaluation: this.createForm.conclusion,
            category: 'breeding',
            metric: { label: '关键指标', value: '-' }
          })
          this.$message.success('创建成功')
          this.createVisible = false
          await this.loadList()
        } finally {
          this.creating = false
        }
      })
    },
    handleView(item) {
      this.$alert(
        [
          `编号：${item.id}`,
          `车间/系统：${this.formatWorkshop(item.workshop)} / ${this.formatSystem(item.system)}`,
          `品种：${item.species || '-'}`,
          `池号：${item.poolNo || '-'}`,
          `苗种来源：${item.seedSource || '-'}`,
          `目标：${item.goal || '-'}`,
          `内容：${item.content || '-'}`,
          `方法：${item.method || '-'}`,
          `结论/效果评估：${item.conclusion || item.effectEvaluation || '-'}`,
          `多池：${(item.pools || []).map(p => `${p.name || p.poolNo}(${p.groupType === 'control' ? '对照' : '实验'})`).join('、') || '-'}`
        ].join('\n'),
        item.name,
        { confirmButtonText: '关闭' }
      )
    },
    handleDelete(item) {
      this.$confirm(`确定删除实验「${item.name}」吗？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteExperiment(item.id)
          this.$message.success('删除成功')
          this.loadList()
        })
        .catch(() => {})
    },
    statusTagType(status) {
      return {
        planned: 'info',
        in_progress: '',
        completed: 'success',
        stopped: 'danger'
      }[status] || 'info'
    }
  }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filter-select {
  width: 140px;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.status-select {
  width: 160px;
}

.content {
  min-height: 420px;
}

.empty-state {
  min-height: 360px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.empty-icon {
  font-size: 72px;
  margin-bottom: 12px;
  color: #c0c4cc;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.experiment-card {
  border: 1px solid #eef0f5;
  border-radius: 10px;
  padding: 16px;
  background: #fff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.card-title {
  margin: 0 0 4px;
  font-size: 16px;
}

.card-sub {
  font-size: 12px;
  color: #909399;
}

.meta {
  list-style: none;
  margin: 0 0 16px;
  padding: 0;
}

.meta li {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #606266;
}

.meta .metric,
.meta .conclusion {
  color: #67c23a;
}

.actions {
  display: flex;
  gap: 10px;
}

.actions .el-button {
  flex: 1;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-select,
  .status-select {
    width: 100%;
  }
}
</style>
