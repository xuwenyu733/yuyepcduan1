<template>
  <div>
    <div class="page-head">
      <div>
        <h2 class="section-title">采购管理</h2>
      </div>
      <el-button class="btn-dark" icon="el-icon-plus" @click="openDialog()">新建采购</el-button>
    </div>

    <div class="page-card">
      <div class="card-title">采购记录</div>
      <p class="section-desc" style="margin-bottom: 16px">
        共 {{ list.length }} 条采购记录<span v-if="list.length">，总采购量 {{ totalWeight.toFixed(2) }} kg</span>
      </p>
      <div v-if="!loading && !list.length" class="empty-purchase">
        <i class="el-icon-shopping-cart-2" />
        <p>暂无采购记录</p>
        <p class="empty-tip">点击“新建采购”按钮创建第一条记录</p>
      </div>
      <el-table v-else v-loading="loading" :data="list" border size="small">
        <el-table-column prop="id" label="采购单号" min-width="140" />
        <el-table-column prop="category" label="品类" width="90" />
        <el-table-column prop="purchaseDate" label="采购日期" width="110" />
        <el-table-column prop="weight" label="采购数量(kg)" width="120" />
        <el-table-column prop="unitPrice" label="单价(元/kg)" width="110" />
        <el-table-column prop="unitWeight" label="单尾重(kg)" width="100" />
        <el-table-column prop="amount" label="金额(元)" width="100" />
        <el-table-column prop="source" label="来源" width="100" />
        <el-table-column prop="contact" label="联系人" width="90" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="transportMethod" label="运输方式" width="110" />
        <el-table-column prop="manager" label="采购负责人" width="100" />
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
      width="680px"
      append-to-body
      custom-class="purchase-dialog"
      :close-on-click-modal="false"
      top="6vh"
    >
      <div slot="title" class="dialog-title-wrap">
        <div class="dialog-title">{{ editingRow ? '编辑采购记录' : '新建采购记录' }}</div>
        <div class="dialog-subtitle">写入管理端采购记录，供入池与净化流程共用</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="purchase-form" v-loading="formLoading">
        <div class="form-section">
          <div class="form-section-title">
            <i class="el-icon-shopping-cart-2" />
            <span>采购基本信息</span>
          </div>
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12">
              <el-form-item label="品类" prop="category">
                <el-select v-model="form.category" style="width:100%" filterable allow-create default-first-option placeholder="选择或输入鱼种">
                  <el-option v-for="item in meta.categories" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="采购日期" prop="purchaseDate">
                <el-date-picker
                  v-model="form.purchaseDate"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="年 / 月 / 日"
                  style="width:100%"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="采购数量 (kg)" prop="weight">
                <el-input v-model.number="form.weight" placeholder="请输入采购数量" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="单价 (元/kg)" prop="unitPrice">
                <el-input-number
                  v-model="form.unitPrice"
                  :min="0"
                  :precision="2"
                  :controls="true"
                  controls-position="right"
                  placeholder="请输入单价"
                  style="width:100%"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="规格 - 单尾重 (kg)">
                <el-input v-model.number="form.unitWeight" placeholder="单尾重量" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="采购负责人" prop="manager">
                <el-input v-model="form.manager" placeholder="请输入负责人" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <div class="form-section-title">
            <i class="el-icon-location-outline" />
            <span>供应商信息</span>
          </div>
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12">
              <el-form-item label="来源 (城市/地区)" prop="source">
                <el-input v-model="form.source" placeholder="如：广东深圳" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="具体地址" prop="address">
                <el-input v-model="form.address" placeholder="详细地址" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="联系人" prop="contact">
                <el-input v-model="form.contact" placeholder="联系人姓名" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" placeholder="联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section form-section--last">
          <div class="form-section-title">
            <i class="el-icon-truck" />
            <span>运输信息</span>
          </div>
          <el-form-item label="运输方式" prop="transportMethod">
            <el-select v-model="form.transportMethod" style="width:100%" placeholder="选择运输方式">
              <el-option
                v-for="item in meta.transportMethods"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
          </el-form-item>
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
  loadMergedPurchases,
  syncPurchaseToPurify,
  loadPurifyPurchaseForm,
  updatePurifyPurchaseRecord,
  deletePurifyPurchaseRecord,
  fetchFlavorMetaSynced
} from '@/utils/flavorPurifyBridge'

export default {
  name: 'PurchaseManage',
  data() {
    return {
      loading: false,
      saving: false,
      formLoading: false,
      visible: false,
      editingRow: null,
      list: [],
      meta: { categories: [], transportMethods: [] },
      form: this.emptyForm(),
      rules: {
        category: [{ required: true, message: '请选择品类', trigger: 'change' }],
        purchaseDate: [{ required: true, message: '请选择采购日期', trigger: 'change' }],
        weight: [{ required: true, message: '请输入采购数量', trigger: 'blur' }],
        unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
        unitWeight: [
          { required: true, message: '请输入单尾重', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (Number(value) > 0) callback()
              else callback(new Error('单尾重须大于 0'))
            },
            trigger: 'blur'
          }
        ],
        manager: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
        source: [{ required: true, message: '请输入来源', trigger: 'blur' }],
        address: [{ required: true, message: '请输入具体地址', trigger: 'blur' }],
        contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        transportMethod: [{ required: true, message: '请选择运输方式', trigger: 'change' }]
      }
    }
  },
  computed: {
    totalWeight() {
      return this.list.reduce((s, i) => s + (Number(i.weight) || 0), 0)
    }
  },
  created() {
    this.init()
  },
  methods: {
    emptyForm() {
      return {
        category: '',
        purchaseDate: '',
        weight: undefined,
        unitPrice: undefined,
        unitWeight: '',
        manager: '',
        source: '',
        address: '',
        contact: '',
        phone: '',
        transportMethod: '',
        remark: ''
      }
    },
    async init() {
      const metaRes = await fetchFlavorMetaSynced()
      this.meta = metaRes.data
      await this.loadList()
    },
    async loadList() {
      this.loading = true
      try {
        this.list = await loadMergedPurchases()
      } finally {
        this.loading = false
      }
    },
    openDialog(row) {
      var self = this
      var editRow = row && typeof row === 'object' && row.id != null ? row : null
      this.editingRow = editRow
      if (editRow) {
        if (!editRow._purifyId) {
          this.$message.warning('该记录缺少管理端主键，请刷新后重试')
          return
        }
        this.formLoading = true
        loadPurifyPurchaseForm(editRow)
          .then(function (form) {
            self.form = Object.assign(self.emptyForm(), form)
            self.visible = true
            self.$nextTick(function () {
              self.$refs.formRef && self.$refs.formRef.clearValidate()
            })
          })
          .catch(function (e) {
            self.$message.error((e && e.message) || '加载采购记录失败')
          })
          .finally(function () {
            self.formLoading = false
          })
        return
      }
      this.form = this.emptyForm()
      this.visible = true
      this.$nextTick(function () {
        self.$refs.formRef && self.$refs.formRef.clearValidate()
      })
    },
    handleSave() {
      var self = this
      this.$refs.formRef.validate(async function (valid) {
        if (!valid) return
        self.saving = true
        try {
          if (self.editingRow) {
            if (!self.editingRow._purifyId) {
              self.$message.error('无法更新：缺少管理端主键')
              return
            }
            await updatePurifyPurchaseRecord(self.form, self.editingRow)
            self.$message.success('采购记录已更新')
          } else {
            const orderNo = 'FW-PUR-' + Date.now()
            const synced = await syncPurchaseToPurify({
              ...self.form,
              purchaseOrderNo: orderNo,
              id: orderNo
            })
            if (!synced) {
              self.$message.error('创建采购记录失败，请稍后重试')
              return
            }
            self.$message.success('采购记录已创建')
          }
          self.visible = false
          self.editingRow = null
          await self.loadList()
        } catch (e) {
          self.$message.error((e && e.message) || '保存失败')
        } finally {
          self.saving = false
        }
      })
    },
    handleDelete(row) {
      var self = this
      this.$confirm('确定删除采购记录「' + (row.id || '') + '」吗？', '提示', { type: 'warning' })
        .then(async function () {
          if (!row._purifyId) {
            self.$message.error('无法删除：缺少管理端主键')
            return
          }
          await deletePurifyPurchaseRecord(row)
          self.$message.success('删除成功')
          self.loadList()
        })
        .catch(function () {})
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

.empty-purchase {
  padding: 56px 16px 48px;
  text-align: center;
  color: #b7c4d2;
}

.empty-purchase i {
  font-size: 48px;
  color: #5a6b7d;
  margin-bottom: 12px;
}

.empty-purchase p {
  margin: 0;
  font-size: 14px;
}

.empty-tip {
  margin-top: 6px !important;
  font-size: 13px !important;
  color: #5a6b7d;
}

.dialog-title-wrap {
  padding-right: 24px;
}

.dialog-title {
  font-size: 18px;
  font-weight: 650;
  color: #e8edf2;
  line-height: 1.3;
}

.dialog-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: #b7c4d2;
  font-weight: 400;
}

.form-section {
  margin-bottom: 18px;
}

.form-section--last {
  margin-bottom: 0;
}

.form-section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 650;
  color: #e8edf2;
  margin-bottom: 14px;
}

.form-section-title i {
  font-size: 16px;
  color: #b7c4d2;
}

.purchase-form >>> .el-form-item {
  margin-bottom: 16px;
}

.purchase-form >>> .el-form-item__label {
  padding-bottom: 6px;
  line-height: 1.2;
  color: #b7c4d2;
}

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
</style>

<style>
.purchase-dialog {
  border-radius: 14px;
  overflow: hidden;
}

.purchase-dialog .el-dialog__header {
  padding: 20px 24px 12px;
  border-bottom: none;
}

.purchase-dialog .el-dialog__body {
  padding: 8px 24px 8px;
  max-height: 68vh;
  overflow-y: auto;
}

.purchase-dialog .el-dialog__footer {
  padding: 12px 24px 20px;
  border-top: 1px solid rgba(124, 143, 161, 0.22);
}

.purchase-dialog .el-input__inner,
.purchase-dialog .el-textarea__inner {
  background: #0a1520;
  border-color: rgba(124, 143, 161, 0.25);
}

.purchase-dialog .el-input__inner:focus,
.purchase-dialog .el-textarea__inner:focus {
  background: #0f1a26;
  border-color: #54907a;
}

.purchase-dialog .el-input-number {
  width: 100%;
}

.purchase-dialog .el-input-number .el-input__inner {
  text-align: left;
}
</style>
