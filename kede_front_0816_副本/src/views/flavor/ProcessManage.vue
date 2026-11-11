<template>
  <div class="process-page">
    <div class="page-head">
      <div>
        <h2 class="section-title">营养过程动态记录</h2>
      </div>
    </div>

    <div class="sub-tabs">
      <button
        type="button"
        class="sub-tab"
        :class="{ active: tab === 'opt' }"
        @click="switchTab('opt')"
      >
        优化记录
      </button>
      <button
        type="button"
        class="sub-tab"
        :class="{ active: tab === 'exit' }"
        @click="switchTab('exit')"
      >
        出池管理
      </button>
    </div>

    <div v-show="tab === 'opt'" class="page-card content-card" v-loading="loading && !optimizing.length">
      <div class="card-head">
        <div>
          <div class="card-title">风味优化中的记录</div>
        </div>
        <el-button
          class="btn-dark"
          icon="el-icon-plus"
          @click="openOptDialog()"
        >
          添加优化记录
        </el-button>
      </div>

      <div v-if="!optimizing.length" class="empty-state">
        暂无优化中的记录
      </div>

      <el-table v-else :data="optimizing" border size="small">
          <el-table-column prop="recordNo" label="记录单号" min-width="150" />
          <el-table-column prop="category" label="品类" width="90" />
          <el-table-column prop="pondCode" label="池号" width="80" />
          <el-table-column prop="entryTime" label="入池时间" min-width="150" />
          <el-table-column prop="totalWeight" label="入池重量" width="100" />
          <el-table-column prop="deathCount" label="累计死亡(尾)" width="110" />
          <el-table-column prop="statusLabel" label="状态" width="90" />
          <el-table-column label="操作" width="120" fixed="right">
            <template slot-scope="{ row }">
              <el-button type="text" @click="openOptDialog(row)">录优化</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="log-block">
          <div class="card-title">最近优化记录</div>
          <el-table :data="optLogs" border size="small" style="margin-top: 10px" empty-text="暂无优化记录">
            <el-table-column prop="recordNo" label="记录单号" min-width="140" />
            <el-table-column prop="measureDate" label="日期" width="110" />
            <el-table-column prop="deathCount" label="死亡(尾)" width="90" />
            <el-table-column prop="waterTemp" label="水温" width="80" />
            <el-table-column prop="dissolvedOxygen" label="溶氧" width="80" />
            <el-table-column prop="measure" label="处理措施" min-width="160" />
            <el-table-column prop="operator" label="操作人" width="90" />
          </el-table>
        </div>

      <div v-if="optimizing.length || alarms.length" class="log-block">
        <div class="card-head" style="padding:0;margin-bottom:10px">
          <div class="card-title">AI 报警</div>
          <el-button size="mini" :loading="alarmLoading" @click="loadAlarms">刷新</el-button>
        </div>
        <el-table v-loading="alarmLoading" :data="alarms" border size="small" empty-text="暂无 AI 报警">
          <el-table-column prop="alarmTitle" label="标题" min-width="160" />
          <el-table-column prop="poolCode" label="池号" width="80" />
          <el-table-column prop="priority" label="优先级" width="80" />
          <el-table-column prop="alarmReason" label="原因" min-width="200" show-overflow-tooltip />
          <el-table-column prop="aiSuggestion" label="AI建议" min-width="160" show-overflow-tooltip />
          <el-table-column prop="alarmTime" label="时间" width="150" />
          <el-table-column label="状态" width="90">
            <template slot-scope="{ row }">
              {{ row.alarmStatus === 'open' ? '未确认' : '已确认' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template slot-scope="{ row }">
              <el-button
                v-if="row.alarmStatus === 'open'"
                type="text"
                @click="ackAlarm(row)"
              >确认</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <div v-show="tab === 'exit'" class="page-card content-card" v-loading="loading && !readyExit.length">
      <div class="card-head">
        <div>
          <div class="card-title">出池管理</div>
        </div>
        <el-button
          class="btn-dark"
          icon="el-icon-plus"
          :disabled="!readyExit.length"
          @click="openExitDialog()"
        >
          出池登记
        </el-button>
      </div>

      <div v-if="!readyExit.length" class="empty-state">
        暂无可出池的记录
      </div>

      <el-table v-else :data="readyExit" border size="small">
        <el-table-column prop="recordNo" label="记录单号" min-width="150" />
        <el-table-column prop="category" label="品类" width="90" />
        <el-table-column prop="pondCode" label="池号" width="80" />
        <el-table-column prop="totalWeight" label="入池重量" width="100" />
        <el-table-column prop="deathCount" label="死亡(尾)" width="90" />
        <el-table-column prop="mortalityRate" label="死亡率%" width="90" />
        <el-table-column prop="statusLabel" label="状态" width="90" />
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" class="btn-dark" @click="openExitDialog(scope.row)">
              出池登记
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      :visible.sync="optVisible"
      width="640px"
      append-to-body
      custom-class="process-dialog"
      :close-on-click-modal="false"
    >
      <div slot="title" class="dialog-title-wrap">
        <div class="dialog-title">添加优化记录</div>
        <div class="dialog-subtitle">添加日常监测数据和处理措施</div>
      </div>
      <el-form ref="optFormRef" :model="optForm" :rules="optRules" label-position="top">
        <el-form-item label="关联记录" prop="entryId">
          <el-select v-model="optForm.entryId" style="width:100%" placeholder="请选择优化中的记录">
            <el-option
              v-for="item in optimizing"
              :key="item.id"
              :label="`${item.recordNo} / ${item.category} / ${item.pondCode}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="监测日期" prop="measureDate">
              <el-date-picker
                v-model="optForm.measureDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="年 / 月 / 日"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人">
              <el-input v-model="optForm.operator" placeholder="请输入操作人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="死亡数量 (尾)">
              <el-input-number v-model="optForm.deathCount" :min="0" :step="1" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="死亡重量 (kg)">
              <el-input-number
                v-model="optForm.deathWeight"
                :min="0"
                :step="0.01"
                :precision="2"
                controls-position="right"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="水温 (℃)">
              <el-input-number
                v-model="optForm.waterTemp"
                :step="0.1"
                :precision="1"
                controls-position="right"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="溶氧 (mg/L)">
              <el-input-number
                v-model="optForm.dissolvedOxygen"
                :step="0.1"
                :precision="1"
                controls-position="right"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="外观评估">
              <el-select v-model="optForm.appearance" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'a'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活力评估">
              <el-select v-model="optForm.vitality" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'v'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况">
              <el-select v-model="optForm.health" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'h'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格均匀度">
              <el-select v-model="optForm.uniformity" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'u'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理措施" prop="measure">
              <el-input v-model="optForm.measure" type="textarea" :rows="3" placeholder="填写处理措施" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="btn-cancel" @click="optVisible = false">取消</el-button>
        <el-button class="btn-create" :loading="saving" @click="saveOpt">创建</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :visible.sync="exitVisible"
      width="680px"
      append-to-body
      custom-class="process-dialog"
      :close-on-click-modal="false"
    >
      <div slot="title" class="dialog-title-wrap">
        <div class="dialog-title">出池登记</div>
        <div class="dialog-subtitle">完成风味优化流程，记录出池数据</div>
      </div>
      <el-form ref="exitFormRef" :model="exitForm" :rules="exitRules" label-position="top">
        <el-form-item label="出池记录" prop="entryId">
          <el-select v-model="exitForm.entryId" style="width:100%" placeholder="请选择可出池记录" @change="onExitRecordChange">
            <el-option
              v-for="item in readyExit"
              :key="item.id"
              :label="`${item.recordNo} / ${item.category} / ${item.pondCode} / 入池${item.totalWeight}kg`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出池重量 (kg)" prop="exitWeight">
              <el-input-number
                v-model="exitForm.exitWeight"
                :min="0"
                :step="0.01"
                :precision="2"
                controls-position="right"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售金额 (元)" prop="salesAmount">
              <el-input-number
                v-model="exitForm.salesAmount"
                :min="0"
                :step="0.01"
                :precision="2"
                controls-position="right"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="外观评估">
              <el-select v-model="exitForm.appearance" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'ea'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活力评估">
              <el-select v-model="exitForm.vitality" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'ev'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况">
              <el-select v-model="exitForm.health" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'eh'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格均匀度">
              <el-select v-model="exitForm.uniformity" style="width:100%" placeholder="请选择">
                <el-option v-for="item in evalOptions" :key="'eu'+item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人工成本">
              <el-input-number v-model="exitForm.laborCost" :min="0" :step="0.01" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电费">
              <el-input-number v-model="exitForm.electricityCost" :min="0" :step="0.01" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="饲料成本">
              <el-input-number v-model="exitForm.feedCost" :min="0" :step="0.01" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="动保成本">
              <el-input-number v-model="exitForm.protectCost" :min="0" :step="0.01" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="exitForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="btn-cancel" @click="exitVisible = false">取消</el-button>
        <el-button class="btn-create" :loading="saving" @click="saveExit">确认出池</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  createOptimization,
  exitEntry,
  fetchAiAlarms,
  ackAiAlarm
} from '@/api/flavor'
import {
  loadMergedOptimizing,
  loadMergedReadyExit,
  loadMergedOptimizations,
  syncOptimizationToPurify,
  syncExitToPurify,
  findEntryRow
} from '@/utils/flavorPurifyBridge'

export default {
  name: 'ProcessManage',
  props: {
    preferTab: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tab: 'opt',
      loading: false,
      saving: false,
      alarmLoading: false,
      optimizing: [],
      readyExit: [],
      optLogs: [],
      alarms: [],
      optVisible: false,
      exitVisible: false,
      evalOptions: ['优', '良', '中', '差'],
      optForm: this.emptyOptForm(),
      exitForm: {
        entryId: '',
        exitWeight: undefined,
        salesAmount: undefined,
        appearance: '',
        vitality: '',
        health: '',
        uniformity: '',
        laborCost: undefined,
        electricityCost: undefined,
        feedCost: undefined,
        protectCost: undefined,
        remark: ''
      },
      optRules: {
        entryId: [{ required: true, message: '请选择记录', trigger: 'change' }],
        measureDate: [{ required: true, message: '请选择监测日期', trigger: 'change' }],
        measure: [{ required: true, message: '请填写处理措施', trigger: 'blur' }]
      },
      exitRules: {
        entryId: [{ required: true, message: '请选择出池记录', trigger: 'change' }],
        exitWeight: [{ required: true, message: '请输入出池重量', trigger: 'blur' }]
      }
    }
  },
  created() {
    if (this.preferTab === 'exit') this.tab = 'exit'
    else if (this.preferTab === 'opt') this.tab = 'opt'
    this.load()
  },
  activated() {
    if (this.preferTab === 'exit') this.tab = 'exit'
    else if (this.preferTab === 'opt') this.tab = 'opt'
    this.load()
  },
  watch: {
    preferTab(val) {
      if (val === 'exit') this.tab = 'exit'
      else if (val === 'opt') this.tab = 'opt'
    }
  },
  methods: {
    switchTab(next) {
      if (this.tab === next) return
      this.tab = next
    },
    emptyOptForm() {
      return {
        entryId: '',
        measureDate: '',
        deathCount: 0,
        deathWeight: 0,
        waterTemp: undefined,
        dissolvedOxygen: undefined,
        appearance: '',
        vitality: '',
        health: '',
        uniformity: '',
        measure: '',
        operator: ''
      }
    },
    async load() {
      this.loading = true
      try {
        const [optRes, exitRes, logRes] = await Promise.all([
          loadMergedOptimizing(),
          loadMergedReadyExit(),
          loadMergedOptimizations()
        ])
        this.optimizing = optRes
        this.readyExit = exitRes
        this.optLogs = logRes
        await this.loadAlarms()
      } finally {
        this.loading = false
      }
    },
    async loadAlarms() {
      this.alarmLoading = true
      try {
        const res = await fetchAiAlarms({})
        this.alarms = res.data || []
      } catch (e) {
        this.alarms = []
      } finally {
        this.alarmLoading = false
      }
    },
    async ackAlarm(row) {
      await ackAiAlarm(row.id)
      this.$message.success('已确认报警')
      await this.loadAlarms()
    },
    openOptDialog(row) {
      if (!this.optimizing.length) {
        this.$message.warning('暂无优化中的入池记录，请先在「入池管理」新建入池')
        return
      }
      this.optForm = this.emptyOptForm()
      if (row && row.id != null) this.optForm.entryId = row.id
      this.optVisible = true
      this.$nextTick(() => {
        this.$refs.optFormRef && this.$refs.optFormRef.clearValidate()
      })
    },
    openExitDialog(row) {
      if (!this.readyExit.length) {
        this.$message.warning('暂无可出池记录')
        return
      }
      const target = row && row.id != null ? row : null
      this.exitForm = {
        entryId: target ? target.id : '',
        exitWeight: target ? Number(target.totalWeight) || undefined : undefined,
        salesAmount: undefined,
        appearance: '',
        vitality: '',
        health: '',
        uniformity: '',
        laborCost: target ? target.laborCost || 0 : 0,
        electricityCost: target ? target.electricityCost || 0 : 0,
        feedCost: target ? target.feedCost || 0 : 0,
        protectCost: target ? target.protectCost || 0 : 0,
        remark: ''
      }
      this.exitVisible = true
      this.$nextTick(() => {
        this.$refs.exitFormRef && this.$refs.exitFormRef.clearValidate()
      })
    },
    onExitRecordChange(id) {
      const item = findEntryRow(this.readyExit, id)
      if (!item) return
      this.exitForm.exitWeight = Number(item.totalWeight) || 0
      this.exitForm.laborCost = item.laborCost || 0
      this.exitForm.electricityCost = item.electricityCost || 0
      this.exitForm.feedCost = item.feedCost || 0
      this.exitForm.protectCost = item.protectCost || 0
    },
    saveOpt() {
      this.$refs.optFormRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const entryRow =
            findEntryRow(this.optimizing, this.optForm.entryId) ||
            findEntryRow(this.readyExit, this.optForm.entryId)
          if (!entryRow) {
            this.$message.error('未找到关联的入池记录，请重新选择')
            return
          }
          if (entryRow._purifyProcessId) {
            await syncOptimizationToPurify(this.optForm, entryRow)
          }
          // 始终写入风味优化日志，保证「最近优化记录」即时可见（管理端流程用快照）
          try {
            await createOptimization({
              ...this.optForm,
              entryId: entryRow.id,
              recordNo: entryRow.recordNo,
              pondCode: entryRow.pondCode,
              category: entryRow.category
            })
          } catch (e) {
            // 管理端已写入时，风味侧失败不阻断展示（随后 load 仍可读监测）
            if (!entryRow._purifyProcessId) throw e
          }
          this.$message.success('优化记录已添加')
          this.optVisible = false
          await this.load()
          await this.loadAlarms()
        } catch (e) {
          const msg = (e && (e.message || e.msg)) || '添加优化记录失败'
          if (!String(msg).includes('请求失败')) {
            this.$message.error(msg)
          }
        } finally {
          this.saving = false
        }
      })
    },
    saveExit() {
      this.$refs.exitFormRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const entryRow = findEntryRow(this.readyExit, this.exitForm.entryId)
          if (!entryRow) {
            this.$message.error('未找到可出池记录，请重新选择')
            return
          }
          if (entryRow._purifyProcessId) {
            await syncExitToPurify(this.exitForm, entryRow)
            if (String(entryRow.id).indexOf('ENT-') === 0) {
              try {
                await exitEntry({
                  ...this.exitForm,
                  entryId: entryRow.id
                })
              } catch (e) {
                /* 管理端已出池即可 */
              }
            }
          } else {
            await exitEntry(this.exitForm)
          }
          this.$message.success('出池成功')
          this.exitVisible = false
          await this.load()
        } catch (e) {
          const msg = (e && (e.message || e.msg)) || '出池失败'
          if (!String(msg).includes('请求失败')) {
            this.$message.error(msg)
          }
        } finally {
          this.saving = false
        }
      })
    }
  }
}
</script>

<style scoped>
.process-page {
  width: 100%;
}

.page-head {
  margin-bottom: 16px;
}

.sub-tabs {
  display: flex;
  width: 100%;
  gap: 0;
  margin-bottom: 14px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.18);
}

.sub-tab {
  position: relative;
  border: none;
  outline: none;
  box-shadow: none;
  background: transparent;
  padding: 10px 18px;
  cursor: pointer;
  color: #b7c4d2;
  font-size: 14px;
  transition: color 0.15s ease;
  -webkit-tap-highlight-color: transparent;
}

.sub-tab:hover,
.sub-tab:focus,
.sub-tab:focus-visible {
  color: #c5d0dc;
  outline: none;
  box-shadow: none;
}

.sub-tab.active,
.sub-tab.active:focus,
.sub-tab.active:focus-visible {
  color: #54907a;
  font-weight: 600;
  outline: none;
  box-shadow: none;
}

.sub-tab.active::after {
  content: '';
  position: absolute;
  left: 12px;
  right: 12px;
  bottom: -1px;
  height: 2px;
  border-radius: 2px 2px 0 0;
  background: #54907a;
}

.content-card {
  min-height: 280px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: 650;
  color: #e8edf2;
  margin-bottom: 4px;
}

.empty-state {
  min-height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #b7c4d2;
  font-size: 14px;
}

.log-block {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(124, 143, 161, 0.22);
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

.btn-dark.is-disabled,
.btn-dark.is-disabled:hover,
.btn-dark.is-disabled:focus,
.btn-dark[disabled],
.btn-dark[disabled]:hover {
  color: #b7c4d2 !important;
  background: #132030 !important;
  border-color: rgba(124, 143, 161, 0.22) !important;
  opacity: 1;
}
</style>

<style>
.process-dialog {
  border-radius: 14px;
  overflow: hidden;
}

.process-dialog .el-dialog__header {
  padding: 20px 24px 10px;
}

.process-dialog .el-dialog__body {
  padding: 4px 24px 8px;
  max-height: 68vh;
  overflow-y: auto;
}

.process-dialog .el-dialog__footer {
  padding: 12px 24px 20px;
  border-top: 1px solid rgba(124, 143, 161, 0.22);
}

.process-dialog .el-input__inner,
.process-dialog .el-textarea__inner {
  background: #0a1520;
  border-color: rgba(124, 143, 161, 0.25);
}

.process-dialog .el-input-number {
  width: 100%;
}

.process-dialog .el-input-number .el-input__inner {
  text-align: left;
}
</style>
