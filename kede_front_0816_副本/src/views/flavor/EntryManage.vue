<template>
  <div>
    <div class="page-head">
      <div>
        <h2 class="section-title">入池管理</h2>
      </div>
      <el-button class="btn-dark" icon="el-icon-plus" @click="openDialog()">新建入池</el-button>
    </div>

    <div class="page-card">
      <div class="card-title">入池记录列表</div>
      <div class="list-filters">
        <el-select v-model="filters.category" clearable placeholder="全部品类" style="width: 140px">
          <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-select v-model="filters.source" clearable placeholder="全部来源" style="width: 140px">
          <el-option v-for="item in sourceOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-select v-model="filters.status" clearable placeholder="全部状态" style="width: 140px">
          <el-option label="已入池" value="entered" />
          <el-option label="优化中" value="optimizing" />
          <el-option label="已出池" value="exited" />
        </el-select>
        <el-select v-model="filters.pondCode" clearable filterable placeholder="全部池号" style="width: 140px">
          <el-option v-for="p in pondOptions" :key="p" :label="p" :value="p" />
        </el-select>
      </div>
      <p class="section-desc" style="margin-bottom: 16px">共 {{ filteredList.length }} 条入池记录</p>
      <el-empty v-if="!loading && !filteredList.length" description="暂无入池数据，点击“新建入池”开始录入" />
      <el-table v-else v-loading="loading" :data="filteredList" border size="small">
        <el-table-column prop="recordNo" label="记录单号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="category" label="品类" width="90" />
        <el-table-column prop="source" label="来源" width="110" />
        <el-table-column label="数据来源" width="100">
          <template slot-scope="{ row }">
            <span :style="{ color: row.fromPurify ? '#67c23a' : '#909399', fontSize: '12px' }">
              {{ row.fromPurify ? '管理端' : '风味' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="entryTime" label="入池时间" min-width="150" />
        <el-table-column prop="totalWeight" label="总重(kg)" width="90" />
        <el-table-column prop="unitWeight" label="单尾重(kg)" width="100" />
        <el-table-column prop="quantity" label="数量(尾)" width="90" />
        <el-table-column prop="density" label="密度(kg/m³)" width="110" />
        <el-table-column prop="unitPrice" label="单价(元/kg)" width="110" />
        <el-table-column prop="manager" label="负责人" width="90" />
        <el-table-column prop="pondCode" label="池号" width="80" />
        <el-table-column prop="optLevel" label="优化等级" width="100" />
        <el-table-column prop="statusLabel" label="状态" width="90" />
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" @click="openDialog(row)">编辑</el-button>
            <el-button type="text" style="color:#f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      :visible.sync="visible"
      width="760px"
      append-to-body
      custom-class="entry-dialog"
      :close-on-click-modal="false"
      top="4vh"
    >
      <div slot="title" class="dialog-title-wrap">
        <div class="dialog-title">{{ editingRow ? '编辑入池记录' : '新建入池记录' }}</div>
        <div class="dialog-subtitle">{{ editingRow && editingRow.fromPurify ? '管理端入池档案（旧系统）' : (editingRow ? '风味模块入池记录' : '填写鱼类入池的相关信息和采购数据') }}</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="entry-form" v-loading="formLoading">
        <el-form-item label="关联采购记录" prop="purchaseId">
          <el-select
            v-model="form.purchaseId"
            style="width:100%"
            placeholder="请选择采购记录"
            filterable
            @change="onPurchaseChange"
          >
            <el-option
              v-for="item in purchases"
              :key="item.id"
              :label="`${item.id} · ${item.category} · ${item.weight}kg · ¥${item.unitPrice}/kg`"
              :value="item.id"
            />
          </el-select>
          <div class="field-hint">选择采购记录后将自动填充品类、来源、单价等信息</div>
        </el-form-item>

        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="品类" prop="category">
              <el-input v-model="form.category" disabled placeholder="自动填充" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="来源" prop="source">
              <el-input v-model="form.source" disabled placeholder="自动填充" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="入池时间" prop="entryTime">
              <el-date-picker
                v-model="form.entryTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择入池时间"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="采购负责人" prop="manager">
              <el-input v-model="form.manager" disabled placeholder="自动填充" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="入池总重量 (kg)" prop="totalWeight">
              <el-input-number
                v-model="form.totalWeight"
                :min="0"
                :step="0.01"
                :precision="2"
                controls-position="right"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="规格 - 单尾重 (kg)" prop="unitWeight">
              <el-input-number
                v-model="form.unitWeight"
                :min="0.001"
                :step="0.01"
                :precision="3"
                controls-position="right"
                style="width:100%"
                placeholder="可从采购带入，也可手动填写"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="自动计算尾数">
              <el-input :value="quantityText" disabled />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="单价 (元/kg)" prop="unitPrice">
              <el-input v-model="form.unitPrice" disabled placeholder="自动填充" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="优化池编号" prop="pondCode">
              <el-select v-model="form.pondCode" style="width:100%" placeholder="选择优化池">
                <el-option
                  v-for="item in ponds"
                  :key="item.id"
                  :label="`${item.code}（${item.statusLabel}）`"
                  :value="item.code"
                />
              </el-select>
              <div class="field-hint">可选择任何鱼池，使用中的鱼池可以继续入池</div>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="池容量">
              <el-input value="300 m³ (固定)" disabled />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="密度">
              <el-input :value="densityText" disabled />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="预估掉秤率">
              <el-input value="5% (固定)" disabled />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="收货人员" prop="receiver">
              <el-input v-model="form.receiver" placeholder="请输入收货人员" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="优化等级" prop="optLevel">
              <el-select v-model="form.optLevel" style="width:100%" placeholder="请选择优化等级">
                <el-option v-for="item in optLevels" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="检验报告 (药残)">
              <div class="upload-row">
                <el-input :value="reportName || '未上传'" disabled />
                <el-button icon="el-icon-upload2" @click="pickReport">上传</el-button>
                <input ref="reportInput" type="file" accept=".pdf,.jpg,.jpeg,.png" style="display:none" @change="onReportChange" />
              </div>
              <div class="field-hint">支持 PDF、JPG、PNG 格式，最大 10MB</div>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="info-box">
          <i class="el-icon-info" />
          <div>
            <div class="info-title">水质数据将自动获取</div>
            <div class="info-text">
              初始水质指标（溶氧、PH值、水温）将在创建记录时自动从对应池号的传感器设备读取，无需手动填写。
            </div>
          </div>
        </div>

        <div class="eval-box">
          <div class="eval-title">入池前评估</div>
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12">
              <el-form-item label="外观评估" prop="appearance">
                <el-select v-model="form.appearance" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in evalOptions" :key="'a'+item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="活力评估" prop="vitality">
                <el-select v-model="form.vitality" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in evalOptions" :key="'v'+item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="健康状况" prop="health">
                <el-select v-model="form.health" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in evalOptions" :key="'h'+item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="规格均匀度" prop="uniformity">
                <el-select v-model="form.uniformity" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in evalOptions" :key="'u'+item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="应激程度" prop="stress">
                <el-select v-model="form.stress" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in stressOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="预期存活率 (%)" prop="survivalRate">
                <el-input v-model.number="form.survivalRate" placeholder="例如: 95.0" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="风险等级" prop="riskLevel">
                <el-select v-model="form.riskLevel" style="width:100%" placeholder="请选择">
                  <el-option v-for="item in riskOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="评估人" prop="assessor">
                <el-input v-model="form.assessor" placeholder="输入评估人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="评估备注">
                <el-input
                  v-model="form.evalRemark"
                  type="textarea"
                  :rows="3"
                  placeholder="其他观察到的情况或特殊说明"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="summary-box">
          <div class="summary-item">
            <div class="summary-label">采购总额</div>
            <div class="summary-value green">¥{{ purchaseAmount.toFixed(2) }}</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">预估掉秤量</div>
            <div class="summary-value orange">{{ estimatedLoss.toFixed(2) }} kg</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">预估净重</div>
            <div class="summary-value blue">{{ estimatedNet.toFixed(2) }} kg</div>
          </div>
        </div>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button class="btn-cancel" @click="visible = false">取消</el-button>
        <el-button class="btn-create" :loading="saving" @click="handleSave">{{ editingRow ? '保存' : '创建' }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  createEntry,
  updateEntry,
  deleteEntry
} from '@/api/flavor'
import { fetchFlavorMergedPonds, getPondSourceErrors } from '@/utils/pondBridge'
import { loadMergedEntries, loadMergedPurchases, syncEntryToPurify, loadPurifyArchiveForm, updatePurifyArchiveRecord, deletePurifyArchiveRecord, fetchFlavorMetaSynced } from '@/utils/flavorPurifyBridge'

const POND_CAPACITY = 300
const LOSS_RATE = 0.05

export default {
  name: 'EntryManage',
  data() {
    return {
      loading: false,
      saving: false,
      formLoading: false,
      visible: false,
      editingRow: null,
      list: [],
      filters: {
        category: '',
        source: '',
        status: '',
        pondCode: ''
      },
      ponds: [],
      purchases: [],
      reportName: '',
      meta: { categories: [], sources: [] },
      optLevels: ['精品级 - 净化10天', '和牛级 - 净化20天', '金枪鱼级 - 净化30天'],
      evalOptions: ['优', '良', '中', '差'],
      stressOptions: ['低', '中', '高'],
      riskOptions: ['低', '中', '高'],
      form: this.emptyForm(),
      rules: {
        purchaseId: [{ required: true, message: '请选择采购记录', trigger: 'change' }],
        category: [{ required: true, message: '请选择采购记录以填充品类', trigger: 'change' }],
        source: [{ required: true, message: '请选择采购记录以填充来源', trigger: 'change' }],
        entryTime: [{ required: true, message: '请选择入池时间', trigger: 'change' }],
        manager: [{ required: true, message: '请选择采购记录以填充负责人', trigger: 'change' }],
        totalWeight: [{ required: true, message: '请输入入池总重量', trigger: 'blur' }],
        unitWeight: [
          { required: true, message: '请填写单尾重', trigger: 'change' },
          {
            validator: (rule, value, callback) => {
              if (Number(value) > 0) callback()
              else callback(new Error('单尾重须大于 0（可手动补录）'))
            },
            trigger: 'blur'
          }
        ],
        unitPrice: [{ required: true, message: '请选择采购记录以填充单价', trigger: 'change' }],
        pondCode: [{ required: true, message: '请选择优化池', trigger: 'change' }],
        receiver: [{ required: true, message: '请输入收货人员', trigger: 'blur' }],
        optLevel: [{ required: true, message: '请选择优化等级', trigger: 'change' }],
        appearance: [{ required: true, message: '请选择外观评估', trigger: 'change' }],
        vitality: [{ required: true, message: '请选择活力评估', trigger: 'change' }],
        health: [{ required: true, message: '请选择健康状况', trigger: 'change' }],
        uniformity: [{ required: true, message: '请选择规格均匀度', trigger: 'change' }],
        stress: [{ required: true, message: '请选择应激程度', trigger: 'change' }],
        survivalRate: [{ required: true, message: '请输入预期存活率', trigger: 'blur' }],
        riskLevel: [{ required: true, message: '请选择风险等级', trigger: 'change' }],
        assessor: [{ required: true, message: '请输入评估人', trigger: 'blur' }]
      }
    }
  },
  computed: {
    categoryOptions() {
      const set = new Set(this.meta.categories || [])
      this.list.forEach(item => {
        if (item.category) set.add(item.category)
      })
      return Array.from(set)
    },
    sourceOptions() {
      const set = new Set(this.meta.sources || [])
      this.list.forEach(item => {
        if (item.source) set.add(item.source)
      })
      return Array.from(set)
    },
    pondOptions() {
      const set = new Set()
      this.list.forEach(item => {
        if (item.pondCode) set.add(item.pondCode)
      })
      ;(this.ponds || []).forEach(p => {
        const code = p.code || p.pondCode || p.pondDetailNum || p.name
        if (code) set.add(code)
      })
      return Array.from(set)
    },
    filteredList() {
      return this.list.filter(item => {
        if (this.filters.category && item.category !== this.filters.category) return false
        if (this.filters.source && item.source !== this.filters.source) return false
        if (this.filters.status && item.status !== this.filters.status) return false
        if (this.filters.pondCode && item.pondCode !== this.filters.pondCode) return false
        return true
      })
    },
    quantity() {
      const w = Number(this.form.totalWeight)
      const u = Number(this.form.unitWeight)
      if (!w || !u) return 0
      return Math.round(w / u)
    },
    quantityText() {
      if (!this.form.totalWeight || !this.form.unitWeight) return '请先输入总重和单尾重'
      return `${this.quantity} 尾`
    },
    density() {
      const w = Number(this.form.totalWeight) || 0
      return Number((w / POND_CAPACITY).toFixed(2))
    },
    densityText() {
      if (!this.form.totalWeight) return '待计算'
      return `${this.density} kg/m³`
    },
    purchaseAmount() {
      return (Number(this.form.totalWeight) || 0) * (Number(this.form.unitPrice) || 0)
    },
    estimatedLoss() {
      return (Number(this.form.totalWeight) || 0) * LOSS_RATE
    },
    estimatedNet() {
      return (Number(this.form.totalWeight) || 0) * (1 - LOSS_RATE)
    }
  },
  created() {
    this.init()
  },
  activated() {
    this.loadList()
  },
  methods: {
    emptyForm() {
      return {
        purchaseId: '',
        purchaseRecordId: undefined,
        category: '',
        source: '',
        entryTime: '',
        manager: '',
        totalWeight: undefined,
        unitWeight: undefined,
        unitPrice: '',
        pondCode: '',
        receiver: '',
        optLevel: '',
        reportFile: '',
        appearance: '',
        vitality: '',
        health: '',
        uniformity: '',
        stress: '',
        survivalRate: '',
        riskLevel: '',
        assessor: '',
        evalRemark: ''
      }
    },
    async loadPonds() {
      try {
        this.ponds = await fetchFlavorMergedPonds()
        const errs = getPondSourceErrors(this.ponds)
        if (errs.length) {
          this.$message.warning('存塘鱼池加载失败：' + errs.map(e => e.message).join('；'))
        }
      } catch (e) {
        console.error('[flavor入池] 鱼池加载失败', e)
        this.ponds = []
        this.$message.error('鱼池列表加载失败，请确认后端存塘接口可用')
      }
    },
    async init() {
      const [metaRes] = await Promise.all([fetchFlavorMetaSynced(), this.loadPonds(), this.ensurePurchases(false)])
      this.meta = metaRes.data
      await this.loadList()
    },
    async loadList() {
      this.loading = true
      try {
        // 88：入池管理对齐管理端「入池档案」(zk_purify_archive)
        this.list = await loadMergedEntries()
      } finally {
        this.loading = false
      }
    },
    async ensurePurchases(force) {
      try {
        this.purchases = (await loadMergedPurchases({ force: !!force })) || []
      } catch (e) {
        console.warn('[flavor入池] 采购列表加载失败', e && e.message)
        if (!this.purchases.length) this.purchases = []
      }
    },
    /** 列表行 → 表单（同步，用于弹窗秒开） */
    rowToForm(row) {
      if (!row) return this.emptyForm()
      return Object.assign(this.emptyForm(), {
        purchaseId: row.purchaseId || '',
        purchaseRecordId: row.purchaseRecordId,
        category: row.category || '',
        source: row.source || '',
        entryTime: row.entryTime || '',
        manager: row.manager || '',
        totalWeight: row.totalWeight,
        unitWeight: row.unitWeight,
        unitPrice: row.unitPrice != null && row.unitPrice !== '' ? row.unitPrice : '0',
        pondCode: row.pondCode || '',
        receiver: row.receiver || row.manager || '',
        optLevel: row.optLevel || '',
        appearance: row.appearance || '',
        vitality: row.vitality || '',
        health: row.health || '',
        uniformity: row.uniformity || '',
        stress: row.stress || '',
        survivalRate: row.survivalRate || '',
        riskLevel: row.riskLevel || '',
        assessor: row.assessor || row.manager || '',
        evalRemark: row.evalRemark || '',
        reportFile: row.reportFile || ''
      })
    },
    async openDialog(row) {
      // 避免 @click="openDialog" 把 MouseEvent 当成编辑行（会误走 update →「入池记录不存在」）
      const editRow = row && typeof row === 'object' && row.id != null ? row : null
      this.editingRow = editRow
      this.formLoading = false

      // 先填表并立刻打开弹窗，不再阻塞等鱼池/采购接口
      if (editRow) {
        this.form = this.rowToForm(editRow)
        this.reportName = editRow.reportFile || ''
      } else {
        this.form = this.emptyForm()
        this.reportName = ''
      }
      this.visible = true
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })

      // 后台刷新下拉数据（有缓存时几乎瞬时）
      const optionTasks = Promise.all([
        this.ponds.length ? Promise.resolve() : this.loadPonds(),
        this.ensurePurchases(false)
      ])

      // 管理端档案：用列表数据秒开后，再静默拉详情补全
      if (editRow && editRow.fromPurify && editRow._purifyArchiveId) {
        this.formLoading = true
        try {
          const form = await loadPurifyArchiveForm(editRow)
          if (this.visible && this.editingRow && this.editingRow.id === editRow.id) {
            this.form = Object.assign(this.emptyForm(), form)
            this.reportName = form.reportFile || ''
            if (this.form.purchaseId) this.onPurchaseChange(this.form.purchaseId)
            if (!this.form.unitPrice) this.form.unitPrice = '0'
          }
        } catch (e) {
          // 列表数据已可编辑，详情失败不关弹窗
          console.warn('[flavor入池] 档案详情补全失败', e && e.message)
        } finally {
          this.formLoading = false
        }
      }

      await optionTasks
      // 采购到齐后，若表单已有 purchaseId 再同步一次联动字段
      if (this.visible && this.form.purchaseId) {
        this.onPurchaseChange(this.form.purchaseId)
      }
    },
    onPurchaseChange(id) {
      const item = this.purchases.find(p => p.id === id)
      if (!item) return
      this.form.category = item.category || ''
      this.form.source = item.source || ''
      this.form.manager = item.manager || ''
      this.form.unitWeight = Number(item.unitWeight) > 0 ? Number(item.unitWeight) : undefined
      this.form.unitPrice = item.unitPrice || ''
      this.form.purchaseRecordId =
        item._purifyId != null && item._purifyId !== '' ? item._purifyId : undefined
      if (!this.form.totalWeight && item.weight) {
        this.form.totalWeight = Number(item.weight)
      }
      if (!(Number(item.unitWeight) > 0)) {
        this.$message.warning('该采购未填单尾重，请在入池时手动补录')
      }
    },
    pickReport() {
      this.$refs.reportInput && this.$refs.reportInput.click()
    },
    onReportChange(e) {
      const file = e.target.files && e.target.files[0]
      if (!file) return
      if (file.size > 10 * 1024 * 1024) {
        this.$message.warning('文件不能超过 10MB')
        e.target.value = ''
        return
      }
      this.reportName = file.name
      this.form.reportFile = file.name
      e.target.value = ''
    },
    handleSave() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        if (!(Number(this.form.unitWeight) > 0)) {
          this.$message.warning('单尾重须大于 0')
          return
        }
        this.saving = true
        try {
          if (this.editingRow && this.editingRow.fromPurify) {
            await updatePurifyArchiveRecord(this.form, {
              quantity: this.quantity,
              density: this.density
            }, this.editingRow)
            this.$message.success('管理端入池档案已更新')
            this.visible = false
            this.editingRow = null
            this.loadList()
          } else if (this.editingRow) {
            const payload = {
              ...this.form,
              quantity: this.quantity,
              density: this.density,
              purchaseAmount: Number(this.purchaseAmount.toFixed(2)),
              estimatedLoss: Number(this.estimatedLoss.toFixed(2)),
              estimatedNet: Number(this.estimatedNet.toFixed(2))
            }
            await updateEntry(this.editingRow.id, payload)
            this.$message.success('入池记录已更新')
            this.visible = false
            this.editingRow = null
            this.loadList()
          } else {
            const recordNo = `FW-${Date.now()}`
            const payload = {
              ...this.form,
              recordNo,
              quantity: this.quantity,
              density: this.density,
              estimatedLossRate: 5,
              pondCapacity: POND_CAPACITY,
              purchaseAmount: Number(this.purchaseAmount.toFixed(2)),
              estimatedLoss: Number(this.estimatedLoss.toFixed(2)),
              estimatedNet: Number(this.estimatedNet.toFixed(2))
            }
            const createdRes = await createEntry(payload)
            const created =
              createdRes && createdRes.data != null ? createdRes.data : createdRes
            if (!created || !created.id) {
              this.$message.error('入池写入失败，请稍后重试')
              return
            }
            const finalRecordNo = created.recordNo || recordNo
            const synced = await syncEntryToPurify(
              {
                ...this.form,
                recordNo: finalRecordNo,
                purchaseRecordId: this.form.purchaseRecordId
              },
              {
                quantity: this.quantity,
                density: this.density,
                recordNo: finalRecordNo
              }
            )
            // 清空筛选，避免新记录被筛掉
            this.filters = { category: '', source: '', status: '', pondCode: '' }
            this.$message.success(
              synced ? '入池成功并已同步管理端档案' : '入池成功（管理端同步失败时可到旧系统补录）'
            )
            this.visible = false
            await this.loadList()
            // 兜底：刷新后仍无该单号则插入本地行，保证立刻可见
            const hit = (this.list || []).some(
              r =>
                (created.id && String(r.id) === String(created.id)) ||
                (finalRecordNo && String(r.recordNo) === String(finalRecordNo))
            )
            if (!hit) {
              this.list = [
                {
                  ...created,
                  recordNo: finalRecordNo,
                  status: created.status || 'entered',
                  statusLabel: created.statusLabel || '已入池',
                  fromPurify: false,
                  sourceLabel: '风味模块'
                },
                ...(this.list || [])
              ]
            }
          }
        } catch (e) {
          if (e && e !== 'cancel') {
            console.error('[flavor入池] 保存失败', e)
            const msg = (e && (e.message || e.msg)) || '保存失败'
            if (!String(msg).includes('请求失败') && !String(msg).includes('模块接口')) {
              this.$message.error(msg)
            }
          }
        } finally {
          this.saving = false
        }
      })
    },
    handleDelete(row) {
      const label = row.fromPurify ? '管理端入池档案' : '入池记录'
      this.$confirm(`确定删除${label}「${row.recordNo || row.id}」吗？`, '提示', { type: 'warning' })
        .then(async () => {
          if (row.fromPurify) {
            await deletePurifyArchiveRecord(row)
          } else {
            await deleteEntry(row.id)
          }
          this.$message.success('删除成功')
          this.loadList()
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #e8edf2;
  margin-bottom: 4px;
}

.list-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 12px 0;
}

.dialog-title-wrap {
  padding-right: 24px;
}

.dialog-title {
  font-size: 18px;
  font-weight: 650;
  color: #e8edf2;
}

.dialog-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: #b7c4d2;
  font-weight: 400;
}

.field-hint {
  margin-top: 4px;
  font-size: 12px;
  color: #b7c4d2;
  line-height: 1.4;
}

.upload-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
}

.info-box {
  display: flex;
  gap: 10px;
  padding: 12px 14px;
  margin: 4px 0 16px;
  background: rgba(84, 144, 122, 0.15);
  border: 1px solid rgba(84, 144, 122, 0.35);
  border-radius: 10px;
  color: #54907a;
}

.info-box i {
  margin-top: 2px;
  font-size: 16px;
}

.info-title {
  font-size: 13px;
  font-weight: 600;
  color: #e8edf2;
  margin-bottom: 4px;
}

.info-text {
  font-size: 12px;
  color: #b7c4d2;
  line-height: 1.5;
}

.eval-box {
  margin-bottom: 16px;
  padding: 14px 14px 4px;
  background: rgba(250, 173, 20, 0.12);
  border: 1px solid rgba(250, 173, 20, 0.35);
  border-radius: 10px;
}

.eval-title {
  font-size: 14px;
  font-weight: 650;
  color: #e8edf2;
  margin-bottom: 10px;
}

.summary-box {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 14px;
  background: rgba(82, 196, 26, 0.12);
  border: 1px solid rgba(82, 196, 26, 0.35);
  border-radius: 10px;
}

.summary-item {
  text-align: center;
}

.summary-label {
  font-size: 12px;
  color: #b7c4d2;
  margin-bottom: 6px;
}

.summary-value {
  font-size: 18px;
  font-weight: 700;
}

.summary-value.green { color: #52c41a; }
.summary-value.orange { color: #faad14; }
.summary-value.blue { color: #54907a; }

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel {
  min-width: 88px;
  color: #e8edf2;
  border-color: rgba(124, 143, 161, 0.35);
  background: transparent;
}

.btn-cancel:hover,
.btn-cancel:focus {
  color: #54907a;
  border-color: rgba(84, 144, 122, 0.45);
  background: rgba(84, 144, 122, 0.1);
}

.btn-create {
  min-width: 88px;
  color: #fff;
  background: #54907a;
  border-color: #54907a;
}

.btn-create:hover,
.btn-create:focus {
  color: #fff;
  background: #6fb59a;
  border-color: #6fb59a;
}

@media (max-width: 640px) {
  .summary-box {
    grid-template-columns: 1fr;
  }
}
</style>

<style>
.entry-dialog {
  border-radius: 14px;
  overflow: hidden;
}

.entry-dialog .el-dialog__header {
  padding: 20px 24px 10px;
}

.entry-dialog .el-dialog__body {
  padding: 4px 24px 8px;
  max-height: 70vh;
  overflow-y: auto;
}

.entry-dialog .el-dialog__footer {
  padding: 12px 24px 20px;
  border-top: 1px solid rgba(124, 143, 161, 0.22);
}

.entry-dialog .el-input__inner,
.entry-dialog .el-textarea__inner {
  background: #0a1520;
  border-color: rgba(124, 143, 161, 0.25);
}

.entry-dialog .el-input.is-disabled .el-input__inner {
  color: #b7c4d2;
  background: #0a1520;
}

.entry-dialog .el-input-number {
  width: 100%;
}

.entry-dialog .el-input-number .el-input__inner {
  text-align: left;
}
</style>
