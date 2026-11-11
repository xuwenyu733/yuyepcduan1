<template>
  <div class="create-page">
    <div class="page-card">
      <div class="page-head">
        <h2 class="section-title" style="margin: 0">新建实验</h2>
        <el-button type="text" icon="el-icon-arrow-left" @click="goBack">返回列表</el-button>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="create-form"
      >
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="实验编号" prop="id">
              <el-input v-model="form.id" placeholder="例如: EXP-2024-001" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="实验标题" prop="name">
              <el-input v-model="form.name" placeholder="例如: 饲料配方优化实验" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="池号" prop="poolNo">
              <el-input v-model="form.poolNo" placeholder="例如: A1, B2" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="品种" prop="species">
              <el-input v-model="form.species" placeholder="例如: 草鱼、鲤鱼" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="苗种来源" prop="seedSource">
              <el-input v-model="form.seedSource" placeholder="例如: 本地育苗场" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="负责人" prop="manager">
              <el-input v-model="form.manager" placeholder="负责人姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="年 / 月 / 日"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="form.endDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="年 / 月 / 日"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option
              v-for="(label, key) in statusMap"
              :key="key"
              :label="label"
              :value="key"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="实验目标" prop="goal">
          <el-input
            v-model="form.goal"
            type="textarea"
            :rows="3"
            placeholder="描述实验的主要目标和预期成果..."
          />
        </el-form-item>

        <el-form-item label="实验内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="详细描述实验的具体内容..."
          />
        </el-form-item>

        <el-form-item label="实验方法" prop="method">
          <el-input
            v-model="form.method"
            type="textarea"
            :rows="3"
            placeholder="描述实验的具体方法和步骤..."
          />
        </el-form-item>

        <el-form-item label="实验结论 / 效果评估">
          <el-input
            v-model="form.conclusion"
            type="textarea"
            :rows="3"
            placeholder="实验完成后填写结论与效果评估"
          />
        </el-form-item>

        <div class="pool-section">
          <div class="pool-header">
            <div>
              <h3 class="pool-title">多池管理</h3>
              <p class="pool-desc">
                配置多个实验池，支持设置实验组和对照组，便于数据采集时独立记录和对比分析
              </p>
            </div>
            <el-button type="primary" size="small" :icon="poolExpanded ? '' : 'el-icon-plus'" @click="poolExpanded = !poolExpanded">
              {{ poolExpanded ? '收起' : '管理池' }}
            </el-button>
          </div>

          <div v-show="poolExpanded" class="pool-body">
            <div
              v-for="(pool, index) in form.pools"
              :key="pool._key"
              class="pool-card"
            >
              <el-row :gutter="16">
                <el-col :xs="24" :sm="12">
                  <el-form-item
                    label="池名称"
                    :prop="`pools.${index}.name`"
                    :rules="[{ required: true, message: '请填写此字段', trigger: 'blur' }]"
                  >
                    <el-input v-model="pool.name" placeholder="例如: 1号池" />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="12">
                  <el-form-item
                    label="池类型"
                    :prop="`pools.${index}.groupType`"
                    :rules="[{ required: true, message: '请选择池类型', trigger: 'change' }]"
                  >
                    <el-select v-model="pool.groupType" style="width: 100%">
                      <el-option label="实验组" value="experiment" />
                      <el-option label="对照组" value="control" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="16">
                <el-col :xs="24" :sm="12">
                  <el-form-item label="初始放养数量（尾）">
                    <el-input v-model="pool.initialCount" placeholder="例如: 1000" />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="12">
                  <el-form-item label="初始平均重量（g）">
                    <el-input v-model="pool.initialWeight" placeholder="例如: 50.5" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="描述">
                <el-input
                  v-model="pool.description"
                  type="textarea"
                  :rows="3"
                  placeholder="描述池的详细信息，如处理方式、特殊条件等..."
                />
              </el-form-item>

              <div class="pool-card-footer">
                <el-button type="text" class="delete-pool-btn" icon="el-icon-delete" @click="removePool(index)">
                  删除此池
                </el-button>
              </div>
            </div>

            <button type="button" class="add-pool-btn" @click="addPool">
              + 添加池
            </button>
          </div>
        </div>

        <div class="form-actions">
          <el-button type="primary" icon="el-icon-document-checked" :loading="submitting" @click="submitForm">
            创建实验
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { fetchExperimentMeta, createExperiment } from '@/api/experiment'

let poolKeySeed = 1

function createPool(overrides = {}) {
  return {
    _key: `pool-${poolKeySeed++}`,
    name: '',
    groupType: 'experiment',
    initialCount: '',
    initialWeight: '',
    description: '',
    ...overrides
  }
}

function emptyForm() {
  return {
    id: '',
    name: '',
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
    conclusion: '',
    pools: [createPool({ name: '1号池' })]
  }
}

export default {
  name: 'ExperimentCreate',
  inject: {
    backToExperimentList: { default: null }
  },
  data() {
    return {
      submitting: false,
      poolExpanded: false,
      statusMap: {},
      form: emptyForm(),
      rules: {
        id: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        name: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        poolNo: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        species: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        seedSource: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        manager: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        startDate: [{ required: true, message: '请填写此字段', trigger: 'change' }],
        endDate: [{ required: true, message: '请填写此字段', trigger: 'change' }],
        status: [{ required: true, message: '请填写此字段', trigger: 'change' }],
        goal: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        content: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        method: [{ required: true, message: '请填写此字段', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const metaRes = await fetchExperimentMeta()
      this.statusMap = metaRes.data.statusMap
      if (!this.form.status && this.statusMap.planned) {
        this.form.status = 'planned'
      }
    },
    goBack() {
      if (typeof this.backToExperimentList === 'function') {
        this.backToExperimentList()
      }
    },
    addPool() {
      const nextIndex = this.form.pools.length + 1
      this.form.pools.push(createPool({ name: `${nextIndex}号池` }))
      this.poolExpanded = true
    },
    removePool(index) {
      if (this.form.pools.length <= 1) {
        this.$message.warning('至少保留一个实验池')
        return
      }
      this.form.pools.splice(index, 1)
    },
    calcDuration() {
      if (!this.form.startDate || !this.form.endDate) return 0
      const start = new Date(this.form.startDate)
      const end = new Date(this.form.endDate)
      const days = Math.round((end - start) / (24 * 3600 * 1000))
      return days > 0 ? days : 0
    },
    syncPoolNo() {
      return this.form.pools.map(item => item.name).filter(Boolean).join(', ')
    },
    submitForm() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        if (this.form.endDate < this.form.startDate) {
          this.$message.warning('结束日期不能早于开始日期')
          return
        }
        this.submitting = true
        try {
          const pools = this.form.pools.map(pool => ({
            name: pool.name,
            groupType: pool.groupType,
            initialCount: pool.initialCount,
            initialWeight: pool.initialWeight,
            description: pool.description
          }))
          await createExperiment({
            ...this.form,
            effectEvaluation: this.form.conclusion,
            poolNo: this.form.poolNo || this.syncPoolNo(),
            pools,
            duration: this.calcDuration(),
            category: 'breeding',
            metric: { label: '关键指标', value: '-' }
          })
          this.$message.success('创建成功')
          this.goBack()
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>

<style scoped>
.page-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.create-form {
  width: 100%;
}

.pool-section {
  margin: 8px 0 24px;
  padding: 16px;
  border: 1px solid #eef0f5;
  border-radius: 10px;
  background: #fff;
}

.pool-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 12px;
}

.pool-title {
  margin: 0 0 6px;
  font-size: 15px;
  color: #1f2d3d;
}

.pool-desc {
  margin: 0;
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
}

.pool-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.pool-card {
  border: 1px solid #e6eaf2;
  border-radius: 10px;
  padding: 16px 16px 8px;
  background: #fafbfc;
}

.pool-card-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: -4px;
  margin-bottom: 4px;
}

.delete-pool-btn {
  color: #f56c6c !important;
}

.add-pool-btn {
  width: 100%;
  min-height: 48px;
  border: 1px dashed #c0c4cc;
  border-radius: 8px;
  background: #fff;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.add-pool-btn:hover {
  color: var(--primary);
  border-color: var(--primary);
  background: var(--primary-light);
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

@media (max-width: 768px) {
  .pool-header {
    flex-direction: column;
  }
}
</style>
