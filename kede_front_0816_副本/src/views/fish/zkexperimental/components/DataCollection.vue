<template>
  <div class="data-collection">
    <el-card>
      <div slot="header" class="card-header">
        <span>实验数据采集</span>
        <div v-if="false">
          <el-button size="mini" icon="el-icon-download" @click="handleExportTemplate">
            下载模板
          </el-button>
          <el-button size="mini" type="primary" icon="el-icon-upload2" @click="handleBatchUpload">
            批量上传
          </el-button>
        </div>
      </div>

      <!-- 选择实验 -->
      <el-form :model="formData" label-width="100px" size="small">
        <el-form-item label="选择实验" required>
          <el-select v-model="selectedExperimentId" placeholder="请选择实验" filterable style="width: 100%"
            @change="handleExperimentChange">
            <el-option v-for="exp in availableExperiments" :key="exp.id"
              :label="`${exp.experimentNumber} - ${exp.experimentTitle}`" :value="exp.id" />
          </el-select>
        </el-form-item>

        <!-- 选择池子 -->
        <el-form-item label="选择池" v-if="selectedExperimentId && currentPoolList.length > 0" required>
          <div class="pool-selection-cards">
            <div
              v-for="pool in currentPoolList"
              :key="pool.poolId"
              class="pool-select-card"
              :class="{ active: formData.poolId === pool.poolId }"
              @click="handlePoolSelect(pool)"
            >
              <div class="pool-name">{{ pool.poolNumber }}</div>
              <div class="pool-type">{{ pool.poolType === '1' ? '实验组' : '对照组' }}</div>
              <div class="pool-info">数量: {{ pool.initFishNum || 0 }} 尾</div>
            </div>
          </div>
        </el-form-item>

        <!-- 数据类型选择 -->
        <el-form-item label="数据类型" v-if="selectedExperimentId && zk_experimental_tab_json.length > 0">
          <el-radio-group v-model="activeDataType" @change="handleDataTypeChange">
            <el-radio-button
              v-for="tab in zk_experimental_tab_json"
              :key="tab.value"
              :label="tab.value"
            >
              {{ tab.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <!-- 存活数据状态展示窗口 -->
        <div v-if="activeDataTypeLabel === '存活数据' && formData.poolId" class="survival-status-window">
          <div class="status-title">
            <i class="el-icon-info"></i>
            <span>{{ formData.poolNumber }} 当前状态</span>
          </div>
          <div class="status-content">
            <div class="status-item">
              <span class="label">初始数量:</span>
              <span class="value">{{ currentPoolInfo.initFishNum || 0 }} 尾</span>
            </div>
            <div class="status-item">
              <span class="label">累计死亡:</span>
              <span class="value danger">{{ cumulativeMortality }} 尾</span>
            </div>
            <div class="status-item">
              <span class="label">当前数量:</span>
              <span class="value success">{{ currentFishCount }} 尾</span>
            </div>
            <div class="status-item">
              <span class="label">当前成活率:</span>
              <span class="value primary">{{ currentSurvivalRate }}%</span>
            </div>
          </div>
        </div>

        <!-- 数据录入表单 -->
        <el-form v-if="selectedExperimentId && currentFields.length > 0" ref="dataFormRef" :model="formData" label-width="120px"
          @submit.native.prevent>
          <el-form-item label="采集时间" required>
            <!--  value-format="yyyy-MM-dd HH:mm:ss" -->
            <el-date-picker v-model="formData.collectionDate" type="datetime" placeholder="选择采集时间" value-format="timestamp"
              style="width: 100%" />
          </el-form-item>

          <!-- 动态表单组件 -->
          <dynamic-data-form
            ref="dynamicForm"
            :fields="currentFields"
            :active-tab-label="activeDataTypeLabel"
            :pool-info="currentPoolInfo"
            :cumulative-mortality="cumulativeMortality"
          />

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              添加数据
            </el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-form>
    </el-card>

    <!-- 已录入数据列表 -->
    <el-card v-if="selectedExperimentId && dataList.length > 0" style="margin-top: 20px">
      <div slot="header">
        <span>已录入数据 ({{ dataList.length }} 条)</span>
      </div>
      <el-table :data="dataList" border>
        <el-table-column label="日期" prop="createTime" width="180">
          <template slot-scope="scope">
            {{ parseTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="池" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.poolNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column label="采集类型" prop="collectionType" width="120" />
        <!-- <el-table-column label="采集数据" prop="collectData" show-overflow-tooltip>
          <template slot-scope="scope">
            <pre style="margin: 0; white-space: pre-wrap;">{{ formatData(scope.row.collectData) }}</pre>
          </template>
        </el-table-column> -->
        <el-table-column label="采集数据" align="left" prop="collectData" min-width="180" show-overflow-tooltip >
          <template v-slot="scope">
            <span style="cursor: pointer;" v-clipboard:copy="formatCollectSummary(scope.row.collectData)"
              v-clipboard:success="clipboardSuccess">
              {{ formatCollectSummary(scope.row.collectData) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" @click="handleDeleteData(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import * as ZkExperimentalDataApi from '@/api/fish/zkexperimentaldata'
import * as ZkExperimentalApi from '@/api/fish/zkexperimental'
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import DynamicDataForm from './DynamicDataForm.vue'

export default {
  name: 'DataCollection',
  components: {
    DynamicDataForm
  },
  props: {
    experiments: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      selectedExperimentId: null,
      activeDataType: '',
      submitting: false,
      dataList: [],
      formData: {
        collectionDate: new Date().toISOString().split('T')[0],
        poolId: undefined,
        poolNumber: ''
      },
      zk_experimental_tab_json: [], // 数据采集数据类型动态json配置，填写数据参考 JSON.parse(json[index].remark)
      currentFields: [], // 当前选中数据类型的字段配置
      currExperiment: {}, // 当前选中的实验
      currentPoolList: [], // 当前实验关联的池子列表
    }
  },
  computed: {
    availableExperiments() {
      return this.experiments.filter(e => e.experimentalStatus !== 3 && e.experimentalStatus !== 4)
    },
    activeDataTypeLabel() {
      const tab = this.zk_experimental_tab_json.find(t => t.value === this.activeDataType)
      return tab ? tab.label : ''
    },
    currentPoolInfo() {
      if (!this.formData.poolId || !this.currentPoolList) return {}
      return this.currentPoolList.find(p => p.poolId === this.formData.poolId) || {}
    },
    cumulativeMortality() {
      if (!this.formData.poolId || !this.dataList) return 0
      // 过滤出当前池子的存活数据记录
      const survivalRecords = this.dataList.filter(d => 
        d.poolId === this.formData.poolId && d.collectionType === '存活数据'
      )
      
      let total = 0
      survivalRecords.forEach(record => {
        try {
          const data = JSON.parse(record.collectData)
          // 查找死亡量字段，兼容不同 keyEn
          const mortalityField = data.find(f => f.keyCh.includes('死亡量') || f.keyEn === 'mortality')
          if (mortalityField && mortalityField.val) {
            total += parseFloat(mortalityField.val) || 0
          }
        } catch (e) {
          console.error('解析存活数据失败', e)
        }
      })
      return total
    },
    currentFishCount() {
      const init = this.currentPoolInfo.initFishNum || 0
      return Math.max(0, init - this.cumulativeMortality)
    },
    currentSurvivalRate() {
      const init = this.currentPoolInfo.initFishNum || 0
      if (init === 0) return 0
      return ((this.currentFishCount / init) * 100).toFixed(2)
    }
  },
  watch: {
    activeDataType(newVal) {
      this.initFieldsForDataType(newVal)
    }
  },
  created() {
    this.zk_experimental_tab_json = this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_TAB_JSON);
    // console.log("zk_experimental_tab_json=======>"+JSON.stringify(this.zk_experimental_tab_json));
    // console.log("experiments=======>"+JSON.stringify(this.experiments));
  },
  mounted() {
    // 设置默认选中的数据类型
    this.$nextTick(() => {
      if (this.zk_experimental_tab_json.length > 0) {
        this.activeDataType = this.zk_experimental_tab_json[0].value
        this.initFieldsForDataType(this.activeDataType)
      }
    })
  },
  methods: {
    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功");
    },
    /** 初始化字段配置 */
    initFieldsForDataType(dataType) {
      if (!dataType) {
        this.currentFields = []
        return
      }
      const tabConfig = this.zk_experimental_tab_json.find(tab => tab.value === dataType)
      if (tabConfig && tabConfig.remark) {
        try {
          const realRemark = JSON.parse(tabConfig.remark)
          // 深拷贝字段配置，避免直接修改原始数据，并初始化 val 值
          this.currentFields = realRemark.map(field => {
            const newField = {
              ...field,
              val: field.val !== undefined && field.val !== null ? field.val : ''
            }
            // 成活率由计算填充，勿默认成 0（否则分析图会把「未计算」当成 0%）
            const isSurvivalRate = (field.keyCh && (field.keyCh.includes('成活率') || field.keyCh.includes('存活率')))
              || field.keyEn === 'survivalRate'
            const isNumeric = this.isNumericField(field)
            if (isNumeric && !isSurvivalRate && (newField.val === '' || newField.val === null || newField.val === undefined)) {
              newField.val = 0
            }
            return newField
          })
          // 初始化后触发一次计算
          this.$nextTick(() => {
            if (this.$refs.dynamicForm) {
              this.$refs.dynamicForm.calculateFields()
            }
          })
        } catch (error) {
          console.error('解析字段配置失败:', error, tabConfig)
          this.currentFields = []
        }
      } else {
        this.currentFields = []
      }
    },

    /** 判断是否为数字字段
     * 
     * 
     */
    isNumericField(field) {
      const numericKeywords = ['重量', '率', '量', '比', '温度', 'pH', '氧', '氮', '尾', 'kg', 'g', '%', 'mg/L', '℃', 'Kg']
      return numericKeywords.some(keyword => field.keyCh.includes(keyword))
    },

    /** 实验变更：拉取完整实验（含 poolDetails/initFishNum），供成活率自动计算 */
    async handleExperimentChange() {
      this.currExperiment = this.availableExperiments.find(d => d.id == this.selectedExperimentId) || {}
      this.currentPoolList = []
      this.formData.poolId = undefined
      this.formData.poolNumber = ''
      if (this.selectedExperimentId) {
        try {
          const res = await ZkExperimentalApi.getZkExperimental(this.selectedExperimentId)
          const detail = (res && res.data) || {}
          this.currExperiment = { ...this.currExperiment, ...detail }
          this.currentPoolList = detail.poolDetails || this.currExperiment.poolDetails || []
        } catch (e) {
          console.warn('[数据采集] 加载实验池明细失败', e && e.message)
          this.currentPoolList = this.currExperiment.poolDetails || []
        }
      }
      if (this.currentPoolList.length > 0) {
        this.handlePoolSelect(this.currentPoolList[0])
      }
      this.loadData()
      this.$nextTick(() => {
        if (this.$refs.dynamicForm) this.$refs.dynamicForm.calculateFields()
      })
    },

    /** 池子选择 */
    handlePoolSelect(pool) {
      this.formData.poolId = pool.poolId;
      this.formData.poolNumber = pool.poolNumber;
      this.$nextTick(() => {
        if (this.$refs.dynamicForm) this.$refs.dynamicForm.calculateFields()
      })
    },

    /** 数据类型变更 */
    handleDataTypeChange() {
      this.handleReset()
    },

    /** 提交数据 */
    async handleSubmit() {
      if (!this.selectedExperimentId) {
        this.$modal.msgWarning('请选择实验')
        return
      }
      if (!this.formData.collectionDate) {
        this.$modal.msgWarning('请选择采集日期')
        return
      }
      if (!this.activeDataType) {
        this.$modal.msgWarning('请选择数据类型')
        return
      }

      try {
        this.submitting = true
        const experiment = this.experiments.find(e => e.id === this.selectedExperimentId)
        
        // 提交前强制重算（成活率依赖池初始数量）
        if (this.$refs.dynamicForm) {
          this.$refs.dynamicForm.calculateFields()
        }

        // 从动态表单组件获取数据
        let collectData = {}
        if (this.$refs.dynamicForm) {
          collectData = this.$refs.dynamicForm.getFormData()
        }

        // 存活数据：若成活率仍为默认 0，用状态窗当前成活率兜底
        if (this.activeDataTypeLabel === '存活数据' && Array.isArray(collectData)) {
          const survivalField = collectData.find(f =>
            (f.keyCh && f.keyCh.includes('成活率')) || f.keyEn === 'survivalRate'
          )
          const rateNum = survivalField != null ? parseFloat(survivalField.val) : NaN
          if (survivalField && (!Number.isFinite(rateNum) || rateNum === 0)) {
            const fallback = parseFloat(this.currentSurvivalRate)
            if (Number.isFinite(fallback) && fallback > 0) {
              survivalField.val = fallback
            }
          }
        }

        const tabConfig = this.zk_experimental_tab_json.find(tab => tab.value === this.activeDataType)
        const collectionTypeLabel = tabConfig ? tabConfig.label : '未知类型'

        const data = {
          experimentId: this.selectedExperimentId,
          experimentNumber: experiment.experimentNumber,
          poolId: this.formData.poolId,
          poolNumber: this.formData.poolNumber,
          collectionType: collectionTypeLabel,
          collectData: JSON.stringify(collectData),
          tenantId: this.currExperiment.tenantId,
          deptId: this.currExperiment.deptId,
        }

        await ZkExperimentalDataApi.createZkExperimentalData(data)
        this.$modal.msgSuccess('数据添加成功')
        this.handleReset()
        this.loadData()
        this.$emit('refresh')
      } catch (error) {
        // 错误已在拦截器中处理
      } finally {
        this.submitting = false
      }
    },

    /** 重置表单 */
    handleReset() {
      const savedPoolId = this.formData.poolId;
      const savedPoolNumber = this.formData.poolNumber;
      this.formData = {
        collectionDate: new Date().toISOString().split('T')[0],
        poolId: savedPoolId,
        poolNumber: savedPoolNumber
      }
      // 重置动态表单
      if (this.$refs.dynamicForm) {
        this.$refs.dynamicForm.resetForm()
      }
      // 重新初始化字段
      this.initFieldsForDataType(this.activeDataType)
    },

    /** 加载数据 */
    async loadData() {
      if (!this.selectedExperimentId) {
        this.dataList = []
        return
      }
      this.currExperiment = this.availableExperiments.find(d => d.id == this.selectedExperimentId);
      // console.log(`currExperiment===================>${JSON.stringify(this.currExperiment)}`)
      try {
        const res = await ZkExperimentalDataApi.getZkExperimentalDataPage({
          pageNo: 1,
          pageSize: 10,
          experimentId: this.selectedExperimentId
        })
        this.dataList = res.data.list || []
      } catch (error) {
        this.$modal.msgError('加载数据失败')
      }
    },

    /** 删除数据 */
    async handleDeleteData(row) {
      await this.$modal.confirm('确定要删除这条数据吗？')
      try {
        await ZkExperimentalDataApi.deleteZkExperimentalData(row.id)
        this.$modal.msgSuccess('删除成功')
        this.loadData()
        this.$emit('refresh')
      } catch (error) {
        // 错误已在拦截器中处理
      }
    },


    /** 格式化数据（调试/复制用） */
    formatData(dataStr) {
      try {
        const data = JSON.parse(dataStr)
        return JSON.stringify(data, null, 2)
      } catch {
        return dataStr
      }
    },

    /** 采集数据摘要展示，避免直接显示 JSON */
    formatCollectSummary(dataStr) {
      if (dataStr == null || dataStr === '') return '—'
      try {
        const data = typeof dataStr === 'string' ? JSON.parse(dataStr) : dataStr
        if (Array.isArray(data)) {
          return data.map(item => {
            const key = item.keyCh || item.key || item.name || ''
            const val = item.val != null ? item.val : (item.value != null ? item.value : '')
            return key ? `${key}: ${val}` : String(val)
          }).filter(Boolean).join('；') || '—'
        }
        if (data && typeof data === 'object') {
          return Object.keys(data).map(k => `${k}: ${data[k]}`).join('；')
        }
        return String(data)
      } catch {
        return String(dataStr)
      }
    },

    /** 导出模板 */
    handleExportTemplate() {
      this.$modal.msgInfo('模板下载功能开发中')
    },

    /** 批量上传 */
    handleBatchUpload() {
      this.$modal.msgInfo('批量上传功能开发中')
    }
  }
}
</script>

<style scoped lang="scss">
.data-collection {
  .pool-selection-cards {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
    margin-bottom: 10px;

    .pool-select-card {
      width: 180px;
      padding: 15px;
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      position: relative;

      &:hover {
        border-color: rgba(84, 144, 122, 0.5);
        background: rgba(84, 144, 122, 0.08);
      }

      &.active {
        border-color: #54907a;
        background: rgba(84, 144, 122, 0.14);
        box-shadow: none;

        &::after {
          content: '✓';
          position: absolute;
          top: -8px;
          right: -8px;
          width: 20px;
          height: 20px;
          background: #54907a;
          color: white;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
        }
      }

      .pool-name {
        font-size: 16px;
        font-weight: 500;
        color: #ffffff;
        margin-bottom: 5px;
      }

      .pool-type {
        font-size: 12px;
        color: #6fba96;
        margin-bottom: 5px;
      }

      .pool-info {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.5);
      }
    }
  }

  ::v-deep .el-card {
    background-color: #12202d !important;
    border: 1px solid rgba(255, 255, 255, 0.08) !important;
    border-radius: 10px !important;
    color: #ffffff !important;
    box-shadow: none !important;

    .el-card__header {
      background-color: transparent !important;
      border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
      color: #ffffff !important;
      padding: 14px 18px !important;
      font-weight: 600;
    }

    .el-card__body {
      background-color: transparent !important;
      color: #ffffff !important;
      padding: 16px 18px !important;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: rgba(255, 255, 255, 0.9) !important;
  }

  .survival-status-window {
    background: rgba(84, 144, 122, 0.08);
    border: 1px solid rgba(84, 144, 122, 0.28);
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;

    .status-title {
      font-size: 14px;
      font-weight: 500;
      color: #6fba96;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .status-content {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;

      .status-item {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 13px;

        .label {
          color: rgba(255, 255, 255, 0.6);
        }

        .value {
          font-weight: 500;
          color: #ffffff;

          &.danger { color: #f56c6c; }
          &.success { color: #67c23a; }
          &.primary { color: #6fba96; }
        }
      }
    }
  }

  // 表单样式
  ::v-deep .el-form {
    .el-form-item__label {
      color: rgba(255, 255, 255, 0.8) !important;
    }

    .el-input__inner,
    .el-textarea__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;

      &::placeholder {
        color: rgba(255, 255, 255, 0.4) !important;
      }
    }

    .el-select .el-input__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;
    }

    .el-select .el-input__suffix {
      color: rgba(255, 255, 255, 0.6) !important;
    }

    .el-date-editor .el-input__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;
    }

    .el-input-number .el-input__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;
    }

    .el-radio-group {
      .el-radio-button__inner {
        background-color: rgba(255, 255, 255, 0.05) !important;
        border-color: rgba(255, 255, 255, 0.1) !important;
        color: rgba(255, 255, 255, 0.8) !important;
      }

      .el-radio-button__orig-radio:checked+.el-radio-button__inner {
        background-color: #54907a !important;
        border-color: #54907a !important;
        color: #ffffff !important;
      }
    }
  }

  // 表格样式
  ::v-deep .el-table {
    background-color: transparent !important;
    color: #ffffff !important;

    &::before {
      background-color: transparent !important;
    }

    .el-table__header-wrapper {
      th {
        background-color: rgba(255, 255, 255, 0.03) !important;
        color: #8b9aab !important;
        border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
      }
    }

    .el-table__body-wrapper {
      tr {
        background-color: transparent !important;

        &:hover > td {
          background-color: rgba(255, 255, 255, 0.04) !important;
        }

        td {
          border-bottom: 1px solid rgba(255, 255, 255, 0.06) !important;
          color: rgba(255, 255, 255, 0.85) !important;
        }
      }
    }
  }
}
</style>