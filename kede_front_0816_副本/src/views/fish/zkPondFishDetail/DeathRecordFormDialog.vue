<template>
  <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body @closed="onClosed">
    <el-form ref="form" :model="form" :rules="rules" label-width="150px">
      <el-form-item label="存塘编号" prop="pondId">
        <el-select
          style="width:100%;"
          v-model="form.pondId"
          placeholder="请选择存塘编号"
          filterable
          clearable
          :loading="pondLoading"
          :no-data-text="pondLoading ? '数据正在加载中…' : '无数据'"
          @change="updatePondId"
        >
          <el-option
            v-for="(item, index) in pondList3"
            :key="item.id + '_pondId_' + index"
            :label="pondOptionLabel(item)"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="批次编号" prop="batchNumber">
        <el-select
          style="width:100%;"
          v-model="form.batchNumber"
          placeholder="选择存塘后自动带出，也可手动选择"
          filterable
          clearable
          @change="onBatchNumberChange"
        >
          <el-option
            v-for="(item, index) in bachchNumberList"
            :key="(item.id || item.relationOrderNo) + '_batch_' + index"
            :label="item.info || item.relationOrderNo"
            :value="item.relationOrderNo"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-select
          v-model="form.productNo"
          placeholder="请输入鱼苗种类"
          filterable
          clearable
          size="small"
          style="width: 100%;"
          @change="updateProductId"
        >
          <el-option
            v-for="dict in productList"
            :key="`${dict.productNo}_pro_${dict.id}`"
            :label="dict.info"
            :value="dict.productNo"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="死亡数量(尾)" prop="fishNum">
        <el-input v-model="form.fishNum" placeholder="请输入死亡尾数" />
      </el-form-item>
      <el-form-item label="鱼苗规格(g/尾)" prop="fishSpec">
        <el-input v-model="form.fishSpec" placeholder="选择存塘后按最近一次打样自动带出" disabled />
      </el-form-item>
      <el-form-item label="时间" prop="createTime">
        <el-date-picker
          clearable
          v-model="form.createTime"
          style="width: 100%;"
          type="datetime"
          value-format="timestamp"
          placeholder="选择时间"
        />
      </el-form-item>
      <el-form-item label="统计人" prop="userId">
        <el-select
          style="width:100%;"
          v-model="form.userId"
          placeholder="请选择统计人"
          filterable
          clearable
          @change="updateUserId"
        >
          <el-option
            v-for="(item, index) in userList1"
            :key="item.id + '_userId_' + index"
            :label="item.nickname"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="原数据" prop="originData" v-if="editType == 2">
        <el-input v-model="form.originData" placeholder="请输入原数据" />
      </el-form-item>
      <el-form-item label="差值" prop="differenceData" v-if="editType == 2">
        <el-input v-model="form.differenceData" placeholder="请输入差值" />
      </el-form-item>
      <el-form-item label="变化比例" prop="changeRatio" v-if="editType == 2">
        <el-input v-model="form.changeRatio" placeholder="请输入变化比例" />
      </el-form-item>
      <el-form-item label="原因" prop="reason" v-if="editType == 2">
        <el-input v-model="form.reason" placeholder="请输入原因" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm" :loading="operationLoading">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import {
  createZkPondFishDetail,
  updateZkPondFishDetail,
  getZkPondFishDetail,
  getZkPondFishDetailPage
} from '@/api/fish/zkPondFishDetail'
import { getLastProofing } from '@/api/fish/zkFishProofing'
import { getUUID } from '@/utils'
import { loadDeptScopedPondList } from '@/utils/siteOptions'
import { buildSiteDisplayLabel } from '@/utils/siteDisplay'
import { notifyProductionRecordChanged } from '@/utils/productionRecordBridge'
import { KE_DE_ROLES, UserInfoKey } from '@/utils/constants'
import { listSimpleUsersByRole } from '@/api/system/user'
import { listForBatch } from '@/api/erp/purchaseIn'
import { getProductPage } from '@/api/erp/product'
import { getZkPondFishPage } from '@/api/fish/zkPondFish'
import { getZkSpecsLogPage } from '@/api/fish/zkSpecsLog'

export default {
  name: 'DeathRecordFormDialog',
  data() {
    return {
      open: false,
      title: '',
      editType: 1,
      operationLoading: false,
      pondLoading: false,
      userInfo: {},
      pondList: [],
      pondList3: [],
      userList1: [],
      productList: [],
      bachchNumberList: [],
      specsByPondId: {},
      form: {},
      rules: {
        fishNum: [{ required: true, message: '死亡数量不能为空', trigger: 'blur' }],
        createTime: [{ required: true, message: '时间不能为空', trigger: 'change' }],
        batchNumber: [{
          validator: (rule, value, callback) => {
            if (value === null || value === undefined || String(value).trim() === '') {
              callback(new Error('批次编号不能为空'))
            } else {
              callback()
            }
          },
          trigger: ['change', 'blur']
        }],
        pondId: [{ required: true, message: '存塘不能为空', trigger: 'change' }]
      },
      optionsReady: false
    }
  },
  watch: {
    'form.fishNum'(newV) {
      if (!newV) return
      if (this.form.pondId) {
        const newNum = parseInt(this.form.historyFishNum, 10) - parseInt(newV, 10)
        if (newNum < 0) {
          this.$modal.msgWarning('存塘鱼数不能小于0！')
          this.form.fishNum = 0
        } else {
          this.form.currFishNum = newNum
          if (this.editType == 2) {
            this.form.differenceData = newV - this.form.originData
            if (this.my_common && typeof this.my_common.computer === 'function') {
              this.form.changeRatio = this.my_common.computer(this.form.differenceData, this.form.originData, 3, 2)
            }
          }
        }
      } else {
        this.$modal.msgWarning('请先选择存塘！')
        this.form.fishNum = 0
      }
    }
  },
  created() {
    try {
      this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey) || '{}') || {}
    } catch (e) {
      this.userInfo = {}
    }
  },
  methods: {
    /** 与死亡量存活率筛选一致：车间-系统-池名 */
    pondOptionLabel(item) {
      if (!item) return ''
      return buildSiteDisplayLabel(item) || item.pondName || item.pondDetailNum || ''
    },
    ensureOptions() {
      if (this.optionsReady) return Promise.resolve()
      this.optionsReady = true
      return Promise.all([
        this.getPondList(false),
        this.getUserList(),
        this.getBatchNumerList(),
        this.getProductList(),
        this.loadCurrentSpecs()
      ]).catch(() => {})
    },
    /** 对外：打开新增 */
    openAdd() {
      this.editType = 1
      this.reset()
      this.open = true
      this.title = '添加死亡记录'
      this.ensureOptions().then(() => {
        try {
          const uid = (this.userInfo && this.userInfo.userId) ||
            (this.$store && this.$store.getters && this.$store.getters.userId)
          if (uid != null && uid !== '') this.updateUserId(parseInt(uid, 10))
        } catch (e) { /* ignore */ }
      })
      try {
        const uuid = typeof getUUID === 'function' ? getUUID() : String(Date.now())
        this.form.fid = String(uuid).replace(/-/g, '')
      } catch (e) {
        this.form.fid = String(Date.now())
      }
      if (!(this.pondList3 && this.pondList3.length)) {
        this.getPondList(false).catch(() => {})
      }
    },
    /** 对外：打开修正 */
    openEdit(row) {
      if (!row || row.id == null) return
      this.editType = 2
      this.reset()
      this.ensureOptions()
      getZkPondFishDetail(row.id).then(response => {
        this.form = response.data || {}
        this.form.originData = this.form.fishNum
        this.open = true
        this.title = '修正死亡记录'
        if (!this.form.fid) {
          this.form.fid = String(getUUID()).replace(/-/g, '')
        }
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    onClosed() {
      this.reset()
    },
    reset() {
      this.form = {
        id: undefined,
        zkPondFishId: undefined,
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        batchNumber: undefined,
        productNo: undefined,
        fishTypes: undefined,
        fishName: undefined,
        fishNum: undefined,
        addType: '-2',
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        createTime: new Date().getTime(),
        pondScale: undefined,
        historyFishNum: undefined,
        currFishNum: undefined,
        fishSpec: '',
        supplier: undefined,
        supplierAddress: undefined,
        supplierId: undefined,
        pondDetailNum: undefined,
        pondSystemName: undefined,
        pondNum: undefined,
        workshopName: undefined,
        userName: undefined,
        originData: undefined,
        differenceData: undefined,
        changeRatio: undefined,
        reason: undefined
      }
      this.$nextTick(() => {
        if (this.$refs.form && this.$refs.form.clearValidate) this.$refs.form.clearValidate()
      })
    },
    updateBatchNumerId(currId) {
      const id = currId == null ? '' : String(currId).trim()
      const item = (this.bachchNumberList || []).find(obj => String(obj.relationOrderNo) === id)
      if (!item) return
      this.form.supplier = item.supplierName
      this.form.supplierAddress = item.supplierAddress
      this.form.supplierId = item.supplierId
      if (item.productNo) {
        this.form.productNo = item.productNo
        this.updateProductId(this.form.productNo)
      }
    },
    onBatchNumberChange(currId) {
      this.$set(this.form, 'batchNumber', currId == null ? '' : String(currId).trim())
      this.updateBatchNumerId(this.form.batchNumber)
      this.$nextTick(() => {
        if (this.$refs.form) this.$refs.form.clearValidate('batchNumber')
      })
    },
    normalizeBatch(val) {
      return String(val == null ? '' : val).replace(/BZ/g, '').replace(/CGRK/g, '').trim()
    },
    pickFirstBatch(raw) {
      return String(raw || '').split(/[,，]/).map(s => s.trim()).filter(Boolean)[0] || ''
    },
    matchBatchOption(raw) {
      const first = this.pickFirstBatch(raw)
      if (!first) return ''
      const n = this.normalizeBatch(first)
      const hit = (this.bachchNumberList || []).find(b =>
        this.normalizeBatch(b.relationOrderNo) === n || String(b.relationOrderNo) === String(first)
      )
      return hit ? hit.relationOrderNo : first
    },
    ensureBatchOption(batchNo) {
      const no = String(batchNo || '').trim()
      if (!no) return
      const exists = (this.bachchNumberList || []).some(b => String(b.relationOrderNo) === no)
      if (!exists) {
        this.bachchNumberList = [{ id: 'tmp_' + no, relationOrderNo: no, info: no }].concat(this.bachchNumberList || [])
      }
    },
    setFormBatchNumber(batchNo) {
      const no = String(batchNo || '').trim()
      if (!no) return
      this.ensureBatchOption(no)
      this.$set(this.form, 'batchNumber', no)
      this.updateBatchNumerId(no)
      this.$nextTick(() => {
        if (this.$refs.form) this.$refs.form.clearValidate('batchNumber')
      })
    },
    getBatchNumerList() {
      return listForBatch({
        pageNo: 1,
        pageSize: 500,
        orderNo: null,
        outKind: '1',
        productName: null,
        userId: null,
        categoryId: '7',
        createTime: [],
        oType: 1
      }).then(response => {
        let list = response.data || []
        if (!Array.isArray(list)) list = []
        list.forEach(item => {
          item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName})`
        })
        this.bachchNumberList = list
      }).catch(() => {
        this.bachchNumberList = []
      })
    },
    updateProductId(currId) {
      if (currId == null || currId === '') return
      const item = (this.productList || []).find(obj => obj.productNo == currId)
      if (!item) return
      this.form.fishName = item.productName
      this.form.fishTypes = item.fishTypes
      this.form.productNo = item.productNo
      if (!this.form.fishSpec) this.form.fishSpec = item.standard
    },
    getProductList() {
      this.productList = []
      return getProductPage({ pageNo: 1, pageSize: 500, categoryId: 7 }).then(response => {
        const list = (response.data && response.data.list) || []
        this.productList.push(...list)
        this.productList.forEach(obj => {
          obj.info = `${obj.productName}-${obj.productBrand}`
        })
      }).catch(() => {})
    },
    updateUserId(userId) {
      const item = (this.userList1 || []).find(obj => obj.id == userId)
      if (!item) return
      this.form.userId = item.id
      this.form.deptId = item.deptId
      this.form.userName = item.nickname
    },
    getUserList() {
      this.userList1 = []
      return listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...(response.data || []))
        this.userList1.forEach(obj => {
          obj.nickname = `${obj.deptName}-${obj.nickname}`
        })
      }).catch(() => {})
    },
    loadCurrentSpecs() {
      return getZkSpecsLogPage({ pageNo: 1, pageSize: 500 }).then(res => {
        const byPondId = {}
        const list = ((res.data && res.data.list) || []).slice()
          .sort((a, b) => Number(b.createTime || 0) - Number(a.createTime || 0))
        list.forEach(item => {
          const spec = (item.acturalSpecs != null && item.acturalSpecs !== '') ? item.acturalSpecs : item.predictSpecs
          if (spec == null || spec === '') return
          if (item.pondId != null && byPondId[String(item.pondId)] == null) byPondId[String(item.pondId)] = spec
        })
        this.specsByPondId = byPondId
      }).catch(() => {})
    },
    updateLastFishSpec(pondId) {
      const fromLog = pondId != null ? this.specsByPondId[String(pondId)] : ''
      if (fromLog != null && fromLog !== '') this.form.fishSpec = fromLog
      getLastProofing(pondId).then(response => {
        if (response.data && response.data.avgSpecs) this.form.fishSpec = response.data.avgSpecs
      }).catch(() => {})
    },
    async fillBatchFromPond(item) {
      if (!item) return
      let batchRaw = item.batchNumber
      if (batchRaw) {
        const matched = this.matchBatchOption(batchRaw)
        if (matched) this.setFormBatchNumber(matched)
        getZkPondFishPage({ pageNo: 1, pageSize: 5, pondId: item.id }).then(fishRes => {
          const fishList = (fishRes.data && fishRes.data.list) || []
          if (fishList.length && fishList[0].id != null) this.form.zkPondFishId = fishList[0].id
          if (fishList.length && fishList[0].batchNumber) {
            const m2 = this.matchBatchOption(fishList[0].batchNumber)
            if (m2) this.setFormBatchNumber(m2)
          }
        }).catch(() => {})
        return
      }
      try {
        const fishRes = await getZkPondFishPage({ pageNo: 1, pageSize: 20, pondId: item.id })
        const fishList = (fishRes.data && fishRes.data.list) || []
        if (fishList.length && fishList[0].batchNumber) batchRaw = fishList[0].batchNumber
        if (fishList.length && fishList[0].id != null) this.form.zkPondFishId = fishList[0].id
      } catch (e) { /* ignore */ }
      if (!batchRaw) {
        try {
          const stockRes = await getZkPondFishDetailPage({ pageNo: 1, pageSize: 20, pondId: item.id, addType: '1' })
          const stocks = ((stockRes.data && stockRes.data.list) || []).slice()
            .sort((a, b) => Number(b.createTime || 0) - Number(a.createTime || 0))
          if (stocks.length && stocks[0].batchNumber) batchRaw = stocks[0].batchNumber
          if (stocks.length && stocks[0].productNo && !this.form.productNo) {
            this.form.productNo = stocks[0].productNo
            this.updateProductId(this.form.productNo)
          }
        } catch (e) { /* ignore */ }
      }
      const matched = this.matchBatchOption(batchRaw)
      if (matched) this.setFormBatchNumber(matched)
    },
    updatePondId(currId) {
      const pools = (this.pondList3 && this.pondList3.length) ? this.pondList3 : this.pondList
      const item = (pools || []).find(obj => obj.id == currId)
      if (!item) return
      this.form.workshopName = item.workshopName
      this.form.workshopId = item.workshopId
      this.form.pondName = item.pondName
      this.form.pondNum = item.pondNum
      this.form.pondScale = item.pondScale
      this.form.historyFishNum = item.fishNum
      this.form.pondId = item.id
      this.form.userName = item.userName
      this.form.userId = item.userId
      this.form.pondDetailNum = item.pondDetailNum
      this.form.pondSystemName = item.pondSystemName
      this.updateLastFishSpec(item.id)
      this.fillBatchFromPond(item)
    },
    getPondList(force) {
      if (!force && this.pondList3 && this.pondList3.length) {
        return Promise.resolve(this.pondList3)
      }
      if (!(this.pondList3 && this.pondList3.length)) this.pondLoading = true
      return loadDeptScopedPondList({ force: !!force }).then(list => {
        const rows = Array.isArray(list) ? list.slice() : []
        this.pondList = rows
        this.pondList3 = rows.slice().sort((a, b) => (Number(b.fishNum) || 0) - (Number(a.fishNum) || 0))
        return this.pondList3
      }).catch(() => {
        if (!(this.pondList3 && this.pondList3.length)) {
          this.pondList = []
          this.pondList3 = []
        }
        return this.pondList3 || []
      }).finally(() => {
        this.pondLoading = false
      })
    },
    submitForm() {
      const runValidateAndSave = () => {
        if (this.form.batchNumber != null && String(this.form.batchNumber).trim() !== '') {
          this.$set(this.form, 'batchNumber', String(this.form.batchNumber).trim())
        }
        this.$refs.form.validate(valid => {
          if (!valid) return
          if (!this.form.batchNumber || String(this.form.batchNumber).trim() === '') {
            this.$modal.msgWarning('批次编号不能为空，请选择存塘或手动选择批次')
            return
          }
          if (this.editType == 2 && !this.form.reason && this.form.differenceData) {
            this.$modal.msgWarning('修正原因不能为空')
            return
          }
          if (this.operationLoading) {
            this.$modal.msgWarning('不能重复请求！')
            return
          }
          this.operationLoading = true
          const done = () => {
            setTimeout(() => { this.operationLoading = false }, 1000)
          }
          if (this.form.id != null) {
            updateZkPondFishDetail(this.form).then(() => {
              this.$modal.msgSuccess('修正成功')
              this.open = false
              notifyProductionRecordChanged({ type: 'death', pondId: this.form.pondId, source: 'dead-dialog' })
              this.$emit('success', { mode: 'update', form: this.form })
              done()
            }).catch(() => { this.operationLoading = false })
            return
          }
          createZkPondFishDetail(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            notifyProductionRecordChanged({ type: 'death', pondId: this.form.pondId, source: 'dead-dialog' })
            this.$emit('success', { mode: 'create', form: this.form })
            done()
          }).catch(e => {
            this.operationLoading = false
            this.$modal.msgError((e && e.message) || '新增失败，请检查必填项与权限')
          })
        })
      }
      if (this.form.pondId && (!this.form.batchNumber || String(this.form.batchNumber).trim() === '')) {
        const pools = (this.pondList3 && this.pondList3.length) ? this.pondList3 : this.pondList
        const item = (pools || []).find(p => p.id == this.form.pondId)
        if (item) {
          this.fillBatchFromPond(item).then(() => runValidateAndSave())
          return
        }
      }
      runValidateAndSave()
    }
  }
}
</script>
