<template>
  <div class="collection-page">
    <div class="page-card filter-bar">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="车间">
          <el-select v-model="filters.workshop" clearable placeholder="全部车间" style="width: 140px">
            <el-option label="1号车间" value="1" />
            <el-option label="2号车间" value="2" />
            <el-option label="3号车间" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统">
          <el-select v-model="filters.system" clearable placeholder="全部系统" style="width: 140px">
            <el-option label="1号系统" value="1" />
            <el-option label="2号系统" value="2" />
            <el-option label="3号系统" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="实验名称/编号" style="width: 200px" />
        </el-form-item>
      </el-form>
    </div>

    <div class="page-card">
      <div class="header-row">
        <h2 class="section-title" style="margin: 0">实验数据采集</h2>
        <div class="header-actions">
          <el-button icon="el-icon-download" @click="handleDownload">下载模板</el-button>
          <el-button type="primary" icon="el-icon-upload2" @click="handleBatchUpload">批量上传</el-button>
        </div>
      </div>

      <el-form label-position="top" class="collect-form">
        <el-form-item label="选择实验" required>
          <el-select
            v-model="experimentId"
            placeholder="请选择实验"
            filterable
            style="width: 100%"
            @change="onExperimentChange"
          >
            <el-option
              v-for="item in filteredExperiments"
              :key="item.id"
              :label="`${item.name}（${item.id}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template v-if="selectedExperiment && fields.length">
        <el-divider content-position="left">按模板录入数据</el-divider>
        <el-form ref="dataForm" :model="formData" label-width="140px">
          <el-form-item
            v-for="field in fields"
            :key="field.key"
            :label="fieldLabel(field)"
            :required="field.required"
            :prop="field.key"
            :rules="field.required ? [{ required: true, message: `请填写${field.label}`, trigger: 'change' }] : []"
          >
            <el-select
              v-if="field.type === 'select'"
              v-model="formData[field.key]"
              placeholder="请选择"
              style="width: 100%"
            >
              <el-option
                v-for="opt in (field.options || [])"
                :key="opt"
                :label="opt"
                :value="opt"
              />
            </el-select>
            <el-input
              v-else-if="field.type === 'text' || field.type === 'number'"
              v-model="formData[field.key]"
              :placeholder="`请输入${field.label}`"
            />
            <el-date-picker
              v-else-if="field.type === 'date'"
              v-model="formData[field.key]"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
              style="width: 100%"
            />
            <el-input
              v-else
              v-model="formData[field.key]"
              type="textarea"
              :rows="2"
              :placeholder="`请输入${field.label}`"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submitData">提交数据</el-button>
          </el-form-item>
        </el-form>
      </template>

      <el-alert
        v-else
        title="请先选择实验。若实验关联了模板，将自动展示对应录入字段。"
        type="info"
        :closable="false"
        show-icon
      />
    </div>

    <div v-if="records.length" class="page-card" style="margin-top: 16px">
      <h3 class="section-title">最近录入记录（{{ records.length }}）</h3>
      <el-table :data="records" size="small" border>
        <el-table-column prop="experimentName" label="实验" min-width="160" />
        <el-table-column prop="createdAt" label="录入时间" width="180" />
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column label="摘要" min-width="200">
          <template slot-scope="{ row }">
            {{ summarize(row.data) }}
          </template>
        </el-table-column>
      </el-table>
    </div>

    <input ref="fileInput" type="file" accept=".xlsx,.xls,.csv" style="display: none" @change="onFileChange" />
  </div>
</template>

<script>
import { fetchExperiments } from '@/api/experiment'
import { fetchTemplates } from '@/api/experiment'
import { fetchCollectionRecords, submitCollection } from '@/api/experiment'

export default {
  name: 'DataCollection',
  data() {
    return {
      experiments: [],
      templates: [],
      filters: {
        workshop: '',
        system: '',
        keyword: ''
      },
      experimentId: '',
      fields: [],
      formData: {},
      records: [],
      submitting: false
    }
  },
  computed: {
    filteredExperiments() {
      const keyword = (this.filters.keyword || '').trim().toLowerCase()
      return this.experiments.filter(item => {
        if (this.filters.workshop && item.workshop !== this.filters.workshop) return false
        if (this.filters.system && item.system !== this.filters.system) return false
        if (keyword) {
          const text = `${item.name || ''} ${item.id || ''}`.toLowerCase()
          if (!text.includes(keyword)) return false
        }
        return true
      })
    },
    selectedExperiment() {
      return this.experiments.find(item => item.id === this.experimentId)
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const [expRes, tplRes, recRes] = await Promise.all([
        fetchExperiments({}),
        fetchTemplates(),
        fetchCollectionRecords({})
      ])
      this.experiments = expRes.data
      this.templates = tplRes.data
      this.records = recRes.data
    },
    onExperimentChange() {
      const exp = this.selectedExperiment
      const tpl = this.templates.find(item => item.id === (exp && exp.templateId))
      this.fields = tpl ? tpl.fields : []
      this.formData = {}
      this.fields.forEach(field => {
        this.$set(this.formData, field.key, '')
      })
      if (exp && !tpl) {
        this.$message.warning('该实验未关联模板，可先在「实验模板」创建并在新建实验时选择')
      }
    },
    fieldLabel(field) {
      return field.unit ? `${field.label}（${field.unit}）` : field.label
    },
    submitData() {
      if (!this.experimentId) {
        this.$message.warning('请先选择实验')
        return
      }
      this.$refs.dataForm.validate(async valid => {
        if (!valid) return
        this.submitting = true
        try {
          await submitCollection({
            experimentId: this.experimentId,
            data: { ...this.formData }
          })
          this.$message.success('录入成功')
          const recRes = await fetchCollectionRecords({})
          this.records = recRes.data
          this.onExperimentChange()
        } finally {
          this.submitting = false
        }
      })
    },
    handleDownload() {
      if (!this.experimentId) {
        this.$message.warning('请先选择实验')
        return
      }
      const headers = this.fields.map(f => f.label).join(',')
      const blob = new Blob([`${headers}\n`], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${this.experimentId}-采集模板.csv`
      a.click()
      URL.revokeObjectURL(url)
      this.$message.success('模板已下载')
    },
    handleBatchUpload() {
      if (!this.experimentId) {
        this.$message.warning('请先选择实验')
        return
      }
      this.$refs.fileInput.click()
    },
    onFileChange(e) {
      const file = e.target.files[0]
      if (!file) return
      this.$message.success(`已选择文件「${file.name}」，批量解析功能可后续接入真实 Excel 解析`)
      e.target.value = ''
    },
    summarize(data) {
      const keys = Object.keys(data || {})
      if (!keys.length) return '-'
      return keys.slice(0, 3).map(k => `${k}:${data[k]}`).join('；')
    }
  }
}
</script>

<style scoped>
.filter-bar {
  margin-bottom: 16px;
}

.filter-form {
  margin: 0;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.collect-form {
  max-width: 720px;
}

@media (max-width: 768px) {
  .header-row {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
