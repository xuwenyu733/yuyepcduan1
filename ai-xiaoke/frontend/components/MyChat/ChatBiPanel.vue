<template>
  <div v-if="viz && (viz.table || viz.type === 'data_entry')" class="chat-bi-panel">
    <!-- 数据录入：可编辑表格 -->
    <div v-if="viz && viz.type === 'data_entry'" class="data-entry-panel">
      <div class="de-title">
        <div class="left">
          <span class="tag">数据录入</span>
          <span class="name">{{ viz.module_label || viz.module_id }}</span>
          <span v-if="isDataEntrySubmitted" class="submitted-tag">已提交</span>
        </div>
        <div class="right">
          <el-button
            v-if="!isDataEntrySubmitted"
            size="mini"
            type="primary"
            :loading="submitting"
            :disabled="submitting || isDataEntrySubmitted"
            @click="submitDataEntry"
          >提交</el-button>
        </div>
      </div>

      <el-table
        :data="dataEntryRows"
        border
        stripe
        size="mini"
        max-height="360"
        class="bi-table"
      >
        <el-table-column
          v-for="c in dataEntryDisplayColumns"
          :key="c.key"
          :prop="c.key"
          :label="c.label"
          :min-width="columnMinWidth(c)"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <div :class="cellClass(scope.row, c)">
              <div v-if="isReadonlyCell(c)" class="de-readonly-text">
                {{ displayCellValue(scope.row, c) }}
              </div>
              <el-select
                v-else-if="c.enum && c.enum.length"
                v-model="scope.row[c.key]"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :disabled="isReadonlyCell(c)"
                @change="onCellChange(scope.row, c.key, $event)"
              >
                <el-option v-for="opt in c.enum" :key="String(opt.value)" :label="opt.label" :value="opt.value" />
              </el-select>

              <!-- 分池记录：批次/原池/鱼苗种类下拉（对齐 PC） -->
              <el-select
                v-else-if="splitParentRemoteKind(c) === 'batch'"
                v-model="scope.row.batchNumber"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择批次"
                :disabled="isReadonlyCell(c)"
                @change="onSplitParentBatchChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in deBatchList"
                  :key="`spbatch_${it.relationOrderNo || ''}_${it.id || ''}`"
                  :label="it.info"
                  :value="it.relationOrderNo"
                />
              </el-select>

              <el-select
                v-else-if="splitParentRemoteKind(c) === 'originPond'"
                v-model="scope.row.originPondId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择原池"
                :disabled="isReadonlyCell(c)"
                @change="onSplitParentOriginPondChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in dePondList"
                  :key="`spopond_${it.id}`"
                  :label="`${it.pondDetailNum}(${it.pondName}:${it.pondScale}m³:${it.fishNum}条)`"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="splitParentRemoteKind(c) === 'product'"
                v-model="scope.row.productNo"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择鱼苗种类"
                :disabled="isReadonlyCell(c)"
                @change="onSplitParentProductChange(scope.row, $event)"
              >
                <el-option v-for="it in deProductList" :key="`spproduct_${it.productNo}`" :label="it.info" :value="it.productNo" />
              </el-select>

              <el-select
                v-else-if="reviseRemoteKind(c) === 'biz'"
                v-model="scope.row.busId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择对应记录"
                :disabled="isReadonlyCell(c)"
                @change="onReviseBizChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deReviseBizList"
                  :key="`revbiz_${it.id || ''}_${idx}`"
                  :label="reviseBizLabel(it, scope.row.reviseType)"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="stockingRemoteKind(c) === 'batch'"
                v-model="scope.row.batchNumber"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择批次"
                :disabled="isReadonlyCell(c)"
                @change="onStockingBatchChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in deBatchList"
                  :key="`batch_${it.relationOrderNo || ''}_${it.id || ''}`"
                  :label="it.info"
                  :value="it.relationOrderNo"
                />
              </el-select>

              <el-select
                v-else-if="stockingRemoteKind(c) === 'pond'"
                v-model="scope.row.pondId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择鱼池"
                :disabled="isReadonlyCell(c)"
                @change="onStockingPondChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in dePondList"
                  :key="`pond_${it.id}`"
                  :label="`${it.pondDetailNum}(${it.pondName}:${it.pondScale}m³:${it.fishNum}条)`"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="stockingRemoteKind(c) === 'product'"
                v-model="scope.row.productNo"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择鱼苗种类"
                :disabled="isReadonlyCell(c)"
                @change="onStockingProductChange(scope.row, $event)"
              >
                <el-option v-for="it in deProductList" :key="`product_${it.productNo}`" :label="it.info" :value="it.productNo" />
              </el-select>

              <el-select
                v-else-if="stockingRemoteKind(c) === 'user'"
                v-model="scope.row.userId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择投放人"
                :disabled="isReadonlyCell(c)"
                @change="onStockingUserChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deUserList"
                  :key="`user_${it.id}_${it.deptId || ''}_${it.tenantId || ''}_${idx}`"
                  :label="it.nickname"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="workshopRemoteKind(c) === 'user'"
                v-model="scope.row.userId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择负责人"
                :disabled="isReadonlyCell(c)"
                @change="onWorkshopUserChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deUserList"
                  :key="`wkuser_${it.id}_${it.deptId || ''}_${it.tenantId || ''}_${idx}`"
                  :label="it.nickname"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="workshopRemoteKind(c) === 'dept'"
                v-model="scope.row.deptId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择归属部门"
                :disabled="isReadonlyCell(c)"
                @change="onWorkshopDeptChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deDeptList"
                  :key="`wkdept_${it.id || ''}_${idx}`"
                  :label="it.name"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="pondSystemRemoteKind(c) === 'workshop'"
                v-model="scope.row.workshopId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择所在车间"
                :disabled="isReadonlyCell(c)"
                @change="onPondSystemWorkshopChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deWorkshopList"
                  :key="`pswk_${it.id || ''}_${idx}`"
                  :label="it.workshopName"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="pondSystemRemoteKind(c) === 'user'"
                v-model="scope.row.userId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择负责人"
                :disabled="isReadonlyCell(c)"
                @change="onPondSystemUserChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deUserList"
                  :key="`psuser_${it.id}_${it.deptId || ''}_${it.tenantId || ''}_${idx}`"
                  :label="it.nickname"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="pondSystemRemoteKind(c) === 'dept'"
                v-model="scope.row.deptId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择归属部门"
                :disabled="isReadonlyCell(c)"
                @change="onPondSystemDeptChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deDeptList"
                  :key="`psdept_${it.id || ''}_${idx}`"
                  :label="it.name"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="pondRemoteKind(c) === 'pondSystem'"
                v-model="scope.row.pondSystemId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择系统编号"
                :disabled="isReadonlyCell(c)"
                @change="onPondPondSystemChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in dePondSystemList"
                  :key="`pondsys_${it.id || ''}_${idx}`"
                  :label="`${it.workshopName}-${it.systemName}`"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="pondRemoteKind(c) === 'user'"
                v-model="scope.row.userId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择负责人"
                :disabled="isReadonlyCell(c)"
                @change="onPondUserChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deUserList"
                  :key="`ponduser_${it.id}_${it.deptId || ''}_${it.tenantId || ''}_${idx}`"
                  :label="it.nickname"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="pondRemoteKind(c) === 'dept'"
                v-model="scope.row.deptId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择归属部门"
                :disabled="isReadonlyCell(c)"
                @change="onPondDeptChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deDeptList"
                  :key="`ponddept_${it.id || ''}_${idx}`"
                  :label="it.name"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="feedingRemoteKind(c) === 'pond'"
                v-model="scope.row.pondId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择存塘"
                :disabled="isReadonlyCell(c)"
                @change="onFeedingPondChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in deFeedingPondList"
                  :key="`fpond_${it.id}`"
                  :label="`${it.pondDetailNum}(${it.pondName}:${it.pondScale}m³:${it.fishNum}条)`"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="feedingRemoteKind(c) === 'product'"
                v-model="scope.row.productNo"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择投放饲料"
                :disabled="isReadonlyCell(c)"
                @change="onFeedingProductChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in deFeedingProductList"
                  :key="`fproduct_${it.productNo}`"
                  :label="it.info"
                  :value="it.productNo"
                />
              </el-select>

              <el-select
                v-else-if="feedingRemoteKind(c) === 'warehouse'"
                v-model="scope.row.warehouseId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择饲料仓库"
                :disabled="isReadonlyCell(c)"
                @change="onFeedingWarehouseChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in deWarehouseList"
                  :key="`wh_${it.id}`"
                  :label="it.name || it.warehouseName || String(it.id)"
                  :value="it.id"
                />
              </el-select>

              <el-select
                v-else-if="feedingRemoteKind(c) === 'user'"
                v-model="scope.row.userId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                :loading="deOptionsLoading"
                placeholder="请选择执行人"
                :disabled="isReadonlyCell(c)"
                @change="onFeedingUserChange(scope.row, $event)"
              >
                <el-option
                  v-for="(it, idx) in deFeedingUserList"
                  :key="`fuser_${it.id}_${it.deptId || ''}_${it.tenantId || ''}_${idx}`"
                  :label="it.nickname"
                  :value="it.id"
                />
              </el-select>

              <el-input-number
                v-else-if="c.type === 'integer' || c.type === 'number'"
                :key="c.key === 'fishNum' ? `fishNum_${scope.row.__deForceFishNum || 0}` : c.key"
                v-model="scope.row[c.key]"
                :precision="c.type === 'integer' ? 0 : 2"
                :controls="false"
                size="mini"
                style="width: 100%;"
                :disabled="isReadonlyCell(c)"
                @input="(v) => onNumericCellInput(scope.row, c, v)"
                @change="(v) => onNumericCellChange(scope.row, c, v)"
              />

              <el-date-picker
                v-else-if="c.type === 'datetime'"
                v-model="scope.row[c.key]"
                type="datetime"
                value-format="timestamp"
                format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择时间"
                size="mini"
                style="width: 100%;"
                :disabled="isReadonlyCell(c)"
                @change="(v) => onCellChange(scope.row, c.key, v)"
              />

              <el-input
                v-else
                v-model="scope.row[c.key]"
                size="mini"
                clearable
                :disabled="isReadonlyCell(c)"
                @input="(v) => onMaybeTextCellInput(scope.row, c, v)"
                @change="(v) => onMaybeTextCellChange(scope.row, c, v)"
              />
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 用药记录：子表（用药明细） -->
      <div v-if="viz && viz.module_id === 'zk_use_medicine_add'" class="de-subtable">
        <div class="de-subtable-title">
          <span>用药明细</span>
          <el-button size="mini" type="primary" @click="addUseMedicineLogRow" :disabled="isDataEntrySubmitted">添加明细</el-button>
        </div>
        <el-table :data="useMedicineLogRows" border stripe size="mini" max-height="260" class="bi-table">
          <el-table-column label="所在存塘" min-width="200">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.pondIdArr"
                multiple
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                placeholder="请选择存塘"
                :disabled="isDataEntrySubmitted"
                @change="persistDataEntryDraftToLocal()"
              >
                <el-option
                  v-for="it in deFeedingPondList"
                  :key="`umpond_${it.id}`"
                  :label="`${it.pondDetailNum}(${it.pondName})`"
                  :value="it.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="对应用药" min-width="180">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.productNo"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                placeholder="请选择用药"
                :disabled="isDataEntrySubmitted"
                @change="onUseMedicineLogProductChange(scope.row, $event)"
              >
                <el-option v-for="it in deMedicineProductList" :key="`med_${it.productNo}`" :label="it.info" :value="it.productNo" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="用药用量" min-width="140">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.midicineDosage"
                size="mini"
                :disabled="isDataEntrySubmitted"
                placeholder="用药用量"
                @change="persistDataEntryDraftToLocal()"
              />
            </template>
          </el-table-column>
          <el-table-column label="用药结果" min-width="140">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.medicineAfter"
                size="mini"
                :disabled="isDataEntrySubmitted"
                placeholder="用药结果"
                @change="persistDataEntryDraftToLocal()"
              />
            </template>
          </el-table-column>
          <el-table-column label="用药时间" min-width="200">
            <template slot-scope="scope">
              <el-date-picker
                v-model="scope.row.feedTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                format="yyyy-MM-dd HH:mm:ss"
                size="mini"
                style="width: 100%;"
                :disabled="isDataEntrySubmitted"
                @change="persistDataEntryDraftToLocal()"
              />
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="140">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.remark"
                size="mini"
                :disabled="isDataEntrySubmitted"
                placeholder="备注"
                @change="persistDataEntryDraftToLocal()"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="removeUseMedicineLogRow(scope.$index)" :disabled="isDataEntrySubmitted">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分池记录：子表（分池明细） -->
      <div v-if="viz && viz.module_id === 'zk_pond_fish_parent_add'" class="de-subtable">
        <div class="de-subtable-title">
          <span>分池明细</span>
          <el-button size="mini" type="primary" @click="addSplitDetailRow" :disabled="isDataEntrySubmitted">添加分池目标</el-button>
        </div>
        <el-table :data="splitDetailRows" border stripe size="mini" max-height="260" class="bi-table">
          <el-table-column label="目标池子" min-width="220">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.targetPondId"
                filterable
                clearable
                size="mini"
                style="width: 100%;"
                placeholder="请选择目标池子"
                :disabled="isDataEntrySubmitted"
                @change="onSplitTargetPondChange(scope.row, $event)"
              >
                <el-option
                  v-for="it in dePondList"
                  :key="`spond_${it.id}`"
                  :label="`${it.pondDetailNum}(${it.pondName}:${it.pondScale}m³:${it.fishNum}条)`"
                  :value="it.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="目标规格(g/尾)" min-width="160">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.targetFishSpec"
                size="mini"
                :disabled="isDataEntrySubmitted"
                placeholder="规格"
                @change="onSplitDetailInputChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="分池重量(kg)" min-width="140">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.targetWeight"
                size="mini"
                :disabled="isDataEntrySubmitted"
                placeholder="重量"
                @change="onSplitDetailInputChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="分池数量(尾)" min-width="140">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.fishNum"
                size="mini"
                :disabled="isDataEntrySubmitted"
                placeholder="数量"
                @change="onSplitDetailFishNumChange()"
              />
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="140">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.remark"
                size="mini"
                :disabled="isDataEntrySubmitted"
                placeholder="备注"
                @change="persistDataEntryDraftToLocal()"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="removeSplitDetailRow(scope.$index)" :disabled="isDataEntrySubmitted">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="de-hint" v-if="deShowRequiredHint && missingRequiredLabels.length">
        必填未填：{{ missingRequiredLabels.join('、') }}
      </div>
    </div>

    <!-- 普通 BI 图表/表格：数据录入场景不展示 -->
    <template v-if="viz && viz.table">
      <div class="bi-toolbar" v-if="chartChoices.length">
        <el-radio-group v-model="chartType" size="mini" @change="renderChart">
          <el-radio-button v-for="c in chartChoices" :key="c" :label="c">{{ labelFor(c) }}</el-radio-button>
        </el-radio-group>
      </div>
      <div v-show="chartType !== 'table'" ref="chartEl" class="bi-chart"></div>
      <el-table
        v-show="chartType === 'table'"
        :data="tableRows"
        border
        stripe
        size="mini"
        max-height="280"
        class="bi-table"
      >
        <el-table-column
          v-for="col in tableColumns"
          :key="col"
          :prop="col"
          :label="fieldLabel(col)"
          min-width="90"
          show-overflow-tooltip
        />
      </el-table>
      <div v-if="viz.truncated" class="bi-trunc-hint">
        已展示 {{ viz.truncated.rows_shown }} / {{ viz.truncated.rows_total }} 行（后端已截断）
      </div>
    </template>
  </div>
</template>

<script>
import * as echarts from "echarts";
import { dataEntrySubmit } from "@/api/fish/aiAgent";
import { DICT_TYPE, getDictDatas } from "@/utils/dict";
import {
  loadBatchOptionsForStocking,
  loadPondOptionsForStocking,
  loadUserOptionsForStocking,
  loadProductOptionsForStocking,
  loadPondOptionsForFeeding,
  loadUserOptionsForFeeding,
  loadProductOptionsForFeeding,
  loadWarehouseOptionsForFeeding,
  loadProductOptionsForMedicine,
  loadDeptOptionsSimple,
  loadWorkshopOptions,
  applyWorkshopToRow,
  loadPondSystemOptions,
  applyPondSystemToPondRow,
  loadReviseBizOptions,
  computeReviseChangeRatio,
  applyReviseBizItemToRow,
  applyStockingBatchToRow,
  applyStockingProductToRow,
  applyStockingPondToRow,
  applyStockingUserToRow,
  applyFeedingPondToRow,
  applyFeedingProductToRow,
  applyFeedingUserToRow,
  applyMedicineProductToLogRow,
  ensureZkPondFishDetailAddDefaults,
  ensureZkPondFishTypeAddDefaults,
  ensureZkUseMedicineAddDefaults,
  ensureZkFeedingLogAddDefaults,
  getFeedingStockSnapshot,
  recomputeStockingDerivedFields,
  recomputeFeedingDerivedFields,
} from "@/utils/dataEntry/chatDataEntryModules";

export default {
  name: "ChatBiPanel",
  props: {
    viz: { type: Object, default: null },
    conversationId: { type: String, default: "" },
  },
  data() {
    return {
      chartType: "table",
      chartInstance: null,
      submitting: false,
      // 提交成功后置为 true：按钮不再显示（同一条 viz 草稿仅允许提交一次）
      dataEntrySubmitted: false,
      /** 投放记录下拉缓存（与 PC 页面同源接口） */
      deBatchList: [],
      dePondList: [],
      deUserList: [],
      deProductList: [],
      /** 投喂记录下拉缓存（与 PC 页面同源接口） */
      deFeedingPondList: [],
      deFeedingUserList: [],
      deFeedingProductList: [],
      deWarehouseList: [],
      /** 用药记录：用药下拉（ERP 商品 categoryId=2） */
      deMedicineProductList: [],
      /** 车间记录：部门下拉（system/dept/list-all-simple） */
      deDeptList: [],
      /** 系统记录：车间下拉（fish/zk-workshop/page） */
      deWorkshopList: [],
      /** 存塘记录：系统下拉（fish/zk-pond-system/page） */
      dePondSystemList: [],
      /** 修正记录：当前操作类型对应的业务记录下拉 */
      deReviseBizList: [],
      deOptionsLoading: false,
      deBootstrappedKey: "",
      // 数据录入草稿本地持久化（避免下次打开为空白）
      _dePersistTimer: null,
      // 防止 deep watch + $set 回填造成递归卡死：同一张表格只初始化一次
      _deHandledVizKey: "",
      // 仅在用户提交/编辑后才展示“必填未填”，避免表格刚出来就提示一堆必填
      deShowRequiredHint: false,
      // 数据录入初始化/回填期间：禁止弹校验提示（避免表格刚出现就连弹多次）
      deInitializing: false,
    };
  },
  computed: {
    tableColumns() {
      return (this.viz && this.viz.table && this.viz.table.columns) || [];
    },
    tableRows() {
      return (this.viz && this.viz.table && this.viz.table.rows) || [];
    },
    chartChoices() {
      const a = (this.viz && this.viz.allowed_charts) || ["table"];
      return a.filter((x) => x);
    },
    tableColumnLabels() {
      return (this.viz && this.viz.table && this.viz.table.column_labels) || {};
    },
    /** 数据录入：完整列（用于必填校验/提交），不等于展示列 */
    dataEntryAllColumns() {
      const cols = (this.viz && this.viz.ui && this.viz.ui.columns) || [];
      const kept = cols.filter((c) => {
        if (!c) return false;
        // fid 仍随 rows 提交，但不参与“必填未填”与展示
        if (c.key === "fid") return false;
        // 投放记录：隐藏“投放鱼池”回显列（仅 pondId 需要用户选择）
        if (this.viz && (this.viz.module_id === "zk_pond_fish_detail_add" || this.viz.module_id === "zk_pond_fish_detail_dead_add")) {
          const k = String(c.key || "");
          const lbl = String(c.label || "");
          if (k === "pondDetailNum" || k === "pondName") return false;
          if (lbl.includes("投放鱼池")) return false;
        }
        return true;
      });
      // 鱼苗种类：fishType 必须稳定为字典下拉（避免回显/重建列后 enum 丢失导致退化成输入框）
      if (this.viz && this.viz.module_id === "zk_pond_fish_type_add") {
        try {
          const fishTypeCol = kept.find((c) => c && String(c.key) === "fishType");
          if (fishTypeCol && (!fishTypeCol.enum || !fishTypeCol.enum.length)) {
            const opts = getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES) || [];
            const normalized = (opts || []).map((x) => ({ label: x.label, value: x.value }));
            this.$set(fishTypeCol, "enum", normalized);
          }
        } catch (e) {
          /* ignore */
        }
      }
      if (this.viz && this.viz.module_id === "zk_workshop_add") {
        try {
          const statusCol = kept.find((c) => c && String(c.key) === "status");
          if (statusCol && (!statusCol.enum || !statusCol.enum.length)) {
            const opts = getDictDatas(DICT_TYPE.ZK_WORKSHOP_STATUS) || [];
            const normalized = (opts || []).map((x) => ({ label: x.label, value: x.value }));
            this.$set(statusCol, "enum", normalized);
          }
        } catch (e) {
          /* ignore */
        }
      }
      if (this.viz && this.viz.module_id === "zk_data_revise_add") {
        try {
          const reviseTypeCol = kept.find((c) => c && String(c.key) === "reviseType");
          if (reviseTypeCol && (!reviseTypeCol.enum || !reviseTypeCol.enum.length)) {
            const opts = getDictDatas(DICT_TYPE.ZK_DATA_REVISE_REVISE_TYPE) || [];
            const normalized = (opts || []).map((x) => ({ label: x.label, value: x.value }));
            this.$set(reviseTypeCol, "enum", normalized);
          }
          const busCol = kept.find((c) => c && String(c.key) === "busId");
          if (busCol) this.$set(busCol, "label", "操作记录");
        } catch (e) {
          /* ignore */
        }
      }
      // 分池记录：投放类型（addType）必须稳定为字典下拉
      if (this.viz && this.viz.module_id === "zk_pond_fish_parent_add") {
        try {
          const addTypeCol = kept.find((c) => c && String(c.key) === "addType");
          if (addTypeCol && (!addTypeCol.enum || !addTypeCol.enum.length)) {
            const opts = getDictDatas(DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE) || [];
            const normalized = (opts || []).map((x) => ({ label: x.label, value: x.value }));
            this.$set(addTypeCol, "enum", normalized);
          }
        } catch (e) {
          /* ignore */
        }
      }
      return kept;
    },
    /** 数据录入：展示列（严格对齐 PC 弹窗，只展示需要用户填写/选择的字段） */
    dataEntryDisplayColumns() {
      const cols = this.dataEntryAllColumns || [];
      if (!this.viz) return cols;
      if (this.viz.module_id === "zk_pond_fish_detail_add") {
        // PC 弹窗（添加投放记录）实际让用户填写/选择的字段
        const allow = new Set([
          "batchNumber",
          "pondId",
          "productNo",
          "fishNum",
          "pondWeight",
          "currFishNum",
          "fishSpec",
          "createTime",
          "userId",
        ]);
        return cols.filter((c) => allow.has(String(c.key || "")));
      }
      if (this.viz.module_id === "zk_pond_fish_detail_dead_add") {
        // PC 弹窗（添加死亡记录）实际让用户填写/选择的字段
        const allow = new Set([
          "batchNumber",
          "pondId",
          "productNo",
          "fishNum",
          "currFishNum",
          "fishSpec",
          "createTime",
          "userId",
        ]);
        return cols.filter((c) => allow.has(String(c.key || "")));
      }
      if (this.viz.module_id === "zk_feeding_log_add") {
        const allow = new Set([
          "pondId",
          "productNo",
          "warehouseId",
          "feedCount",
          "feedTime",
          "personScore",
          "userId",
          "sumScore",
        ]);
        return cols.filter((c) => allow.has(String(c.key || "")));
      }
      if (this.viz.module_id === "zk_pond_fish_type_add") {
        // PC 弹窗（新增鱼苗种类）实际让用户填写/选择的字段
        const allow = new Set([
          "batchNumber",
          "pondId",
          "fishNum",
          "fishSpec",
          "staticYear",
          "staticMonth",
          "staticDay",
          "fishType",
        ]);
        return cols.filter((c) => allow.has(String(c.key || "")));
      }
      if (this.viz.module_id === "zk_workshop_add") {
        // PC 弹窗（添加车间）实际让用户填写/选择的字段
        const order = [
          "workshopName",
          "workshopPosition",
          "longitude",
          "dimension",
          "userId",
          "deptId",
          "fid", // 聊天侧会隐藏，但仍允许 schema 下发时被过滤掉；实际展示列不展示 fid
          "status",
        ];
        const allow = new Set(order.filter((k) => k !== "fid"));
        const byKey = new Map(cols.map((c) => [String(c.key || ""), c]));
        // 固定顺序 + 过滤 fid
        return order
          .filter((k) => allow.has(k))
          .map((k) => byKey.get(k))
          .filter(Boolean);
      }
      if (this.viz.module_id === "zk_pond_system_add") {
        // PC 弹窗（添加存塘系统）实际让用户填写/选择的字段
        const order = [
          "workshopId",
          "systemNum",
          "systemName",
          "userId",
          "deptId",
        ];
        const byKey = new Map(cols.map((c) => [String(c.key || ""), c]));
        return order.map((k) => byKey.get(k)).filter(Boolean);
      }
      if (this.viz.module_id === "zk_pond_add") {
        // PC 弹窗（添加存塘）实际让用户填写/选择的字段
        const order = [
          "pondSystemId",
          "pondDetailNum",
          "pondName",
          "pondScale",
          "userId",
          "deptId",
        ];
        const byKey = new Map(cols.map((c) => [String(c.key || ""), c]));
        return order.map((k) => byKey.get(k)).filter(Boolean);
      }
      if (this.viz.module_id === "zk_data_revise_add") {
        // PC 弹窗核心字段（操作类型联动 + 计算 changeRatio）
        const order = [
          "reviseType",
          "busId",
          "originData",
          "differenceData",
          "changeRatio",
          "reason",
          "technicalDirectorsOpinion",
          "directorsOpinion",
        ];
        const byKey = new Map(cols.map((c) => [String(c.key || ""), c]));
        return order.map((k) => byKey.get(k)).filter(Boolean);
      }
      if (this.viz.module_id === "zk_use_medicine_add") {
        // PC 弹窗（新增用药记录）核心可编辑字段（附件/明细表先不做）
        const order = [
          "pondId",
          "createTime",
          "controlContent",
          "medicineName",
          "expertSolution",
          "actualSolution",
          "stopMedicationTime",
          "numberOfLosses",
          "specifications",
          "medicineAfter",
          "userId",
        ];
        const byKey = new Map(cols.map((c) => [String(c.key || ""), c]));
        return order.map((k) => byKey.get(k)).filter(Boolean);
      }
      if (this.viz.module_id === "zk_pond_fish_parent_add") {
        // PC 弹窗（新增分池）主表字段
        const order = [
          "batchNumber",
          "originPondId",
          "productNo",
          "fishNum",
          "fishNumDiff",
          "addType",
          "remark",
        ];
        const byKey = new Map(cols.map((c) => [String(c.key || ""), c]));
        return order.map((k) => byKey.get(k)).filter(Boolean);
      }
      return cols;
    },
    dataEntryRows() {
      return (this.viz && this.viz.rows) || [];
    },
    useMedicineLogRows() {
      const r0 = (this.dataEntryRows && this.dataEntryRows[0]) || {};
      const list = r0 && Array.isArray(r0.useMedicineLogList) ? r0.useMedicineLogList : [];
      // 归一化：子表 feedTime 统一为 yyyy-MM-dd HH:mm:ss 字符串
      // ElementUI DatePicker 在 value-format=string 时会对值调用 .match()，number/Date 会直接报错
      try {
        if (list && list.length && this.viz && this.viz.module_id === "zk_use_medicine_add") {
          list.forEach((it) => {
            if (!it) return;
            const ft = it.feedTime;
            if (typeof ft === "number" && Number.isFinite(ft) && ft > 0) {
              it.feedTime = this.formatDateTimeStr(ft);
            } else if (ft instanceof Date) {
              it.feedTime = this.formatDateTimeStr(ft.getTime());
            } else if (typeof ft === "string") {
              if (!ft.trim()) it.feedTime = this.formatDateTimeStr(Date.now());
            } else if (ft == null) {
              it.feedTime = this.formatDateTimeStr(Date.now());
            }
          });
        }
      } catch (e) {
        /* ignore */
      }
      return list;
    },
    splitDetailRows() {
      const r0 = (this.dataEntryRows && this.dataEntryRows[0]) || {};
      const list = r0 && Array.isArray(r0.details) ? r0.details : [];
      return list;
    },
    missingRequiredLabels() {
      // 必填校验按“完整列”算，避免隐藏了联动字段导致提交时后端缺字段
      const cols = this.dataEntryAllColumns || [];
      const row = (this.dataEntryRows && this.dataEntryRows[0]) || {};
      const miss = [];
      for (const c of cols) {
        if (!c.required) continue;
        const v = row[c.key];
        if (v === null || v === undefined || String(v).trim() === "") {
          miss.push(c.label || c.key);
        }
      }
      return miss;
    },
    isDataEntrySubmitted() {
      return !!(this.dataEntrySubmitted || (this.viz && this.viz._dataEntrySubmitted));
    },
  },
  watch: {
    viz: {
      // 不能 deep：回填/联动会 $set rows，从而递归触发 watch 导致页面卡死
      deep: false,
      handler() {
        this.syncDefaultChart();
        this.$nextTick(() => this.renderChart());

        // 数据录入初始化：仅当“表格身份”变化时执行一次，避免回填触发递归
        const v = this.viz;
        if (v && v.type === "data_entry") {
          const row = (v.rows && v.rows[0]) || {};
          const fid = row && row.fid ? String(row.fid) : "";
          const key = `${v.module_id || ""}::${v.session_id || ""}::${fid}`;
          if (this._deHandledVizKey !== key) {
            this._deHandledVizKey = key;
            this.bootstrapDataEntryIfNeeded();
            this.restoreDataEntryDraftFromLocal();
          }
        } else {
          this.dataEntrySubmitted = false;
          this._deHandledVizKey = "";
        }
      },
    },
  },
  mounted() {
    this.syncDefaultChart();
    this.$nextTick(() => this.renderChart());
    this.bootstrapDataEntryIfNeeded();
    // mounted 时也走一次“按 key 初始化”，避免首次渲染没触发 watch
    try {
      const v = this.viz;
      if (v && v.type === "data_entry") {
        const row = (v.rows && v.rows[0]) || {};
        const fid = row && row.fid ? String(row.fid) : "";
        const key = `${v.module_id || ""}::${v.session_id || ""}::${fid}`;
        if (this._deHandledVizKey !== key) {
          this._deHandledVizKey = key;
          this.restoreDataEntryDraftFromLocal();
        }
      }
    } catch (e) {
      /* ignore */
    }
    this._onResize = () => {
      if (this.chartInstance) this.chartInstance.resize();
    };
    window.addEventListener("resize", this._onResize);
  },
  beforeDestroy() {
    if (this._dePersistTimer) {
      clearTimeout(this._dePersistTimer);
      this._dePersistTimer = null;
    }
    window.removeEventListener("resize", this._onResize);
    if (this.chartInstance) {
      this.chartInstance.dispose();
      this.chartInstance = null;
    }
  },
  methods: {
    formatDateTimeStr(ts = Date.now()) {
      const d = new Date(typeof ts === "number" ? ts : Date.now());
      const pad = (n) => String(n).padStart(2, "0");
      const yyyy = d.getFullYear();
      const MM = pad(d.getMonth() + 1);
      const dd = pad(d.getDate());
      const hh = pad(d.getHours());
      const mm = pad(d.getMinutes());
      const ss = pad(d.getSeconds());
      return `${yyyy}-${MM}-${dd} ${hh}:${mm}:${ss}`;
    },
    addUseMedicineLogRow() {
      try {
        const r0 = (this.viz && this.viz.rows && this.viz.rows[0]) || null;
        if (!r0) return;
        if (!Array.isArray(r0.useMedicineLogList)) this.$set(r0, "useMedicineLogList", []);
        r0.useMedicineLogList.push({
          pondIdArr: [],
          productNo: undefined,
          midicineDosage: undefined,
          medicineAfter: undefined,
          feedTime: this.formatDateTimeStr(Date.now()),
          remark: undefined,
        });
        this.persistDataEntryDraftToLocal(true);
      } catch (e) {
        /* ignore */
      }
    },
    removeUseMedicineLogRow(index) {
      try {
        const r0 = (this.viz && this.viz.rows && this.viz.rows[0]) || null;
        if (!r0 || !Array.isArray(r0.useMedicineLogList)) return;
        r0.useMedicineLogList.splice(index, 1);
        this.persistDataEntryDraftToLocal(true);
      } catch (e) {
        /* ignore */
      }
    },
    addSplitDetailRow() {
      try {
        const r0 = (this.viz && this.viz.rows && this.viz.rows[0]) || null;
        if (!r0) return;
        if (!Array.isArray(r0.details)) this.$set(r0, "details", []);
        const d = {
          targetPondId: undefined,
          targetPondName: undefined,
          targetPondNum: undefined,
          targetPondScale: undefined,
          targetHistoryFishNum: undefined,
          targetPondDetailNum: undefined,
          targetPondSystemName: undefined,
          targetFishSpec: (r0.fishSpec != null ? String(r0.fishSpec) : ""),
          targetWeight: undefined,
          fishNum: 0,
          remark: "",
          // 携带主表信息（对齐 PC handleAddDetail）
          batchNumber: r0.batchNumber,
          pondId: r0.originPondId,
          fishTypes: r0.fishTypes,
          productNo: r0.productNo,
          fishName: r0.fishName,
          addType: r0.addType || "-3",
          userId: r0.userId,
          deptId: r0.deptId,
        };
        r0.details.push(d);
        this.persistDataEntryDraftToLocal(true);
      } catch (e) {
        /* ignore */
      }
    },
    removeSplitDetailRow(index) {
      try {
        const r0 = (this.viz && this.viz.rows && this.viz.rows[0]) || null;
        if (!r0 || !Array.isArray(r0.details)) return;
        r0.details.splice(index, 1);
        this.onSplitDetailFishNumChange();
        this.persistDataEntryDraftToLocal(true);
      } catch (e) {
        /* ignore */
      }
    },
    onSplitTargetPondChange(detailRow, targetPondId) {
      try {
        const pond = (this.dePondList || []).find((p) => p && String(p.id) === String(targetPondId));
        if (pond) {
          this.$set(detailRow, "targetPondName", pond.pondName);
          this.$set(detailRow, "targetPondNum", pond.pondNum);
          this.$set(detailRow, "targetPondScale", pond.pondScale);
          this.$set(detailRow, "targetHistoryFishNum", pond.fishNum);
          this.$set(detailRow, "targetPondDetailNum", pond.pondDetailNum);
          this.$set(detailRow, "targetPondSystemName", pond.pondSystemName);
        }
      } catch (e) {
        /* ignore */
      }
      this.persistDataEntryDraftToLocal(true);
    },
    onSplitDetailInputChange(detailRow) {
      // 规格/重量变更：重量(kg)*1000/规格(g/尾)=数量(尾)（对齐 PC calculateDetailFishNum）
      try {
        const w = parseFloat(String(detailRow.targetWeight || "").trim());
        const m = String(detailRow.targetFishSpec || "").match(/-?\d+(\.\d+)?/);
        const spec = m ? parseFloat(m[0]) : NaN;
        if (Number.isFinite(w) && Number.isFinite(spec) && spec > 0) {
          const n = Math.round((w * 1000) / spec);
          this.$set(detailRow, "fishNum", n);
        }
      } catch (e) {
        /* ignore */
      }
      this.onSplitDetailFishNumChange();
      // 子表输入：用 change 保存，避免每键入 stringify 卡顿
      this.persistDataEntryDraftToLocal();
    },
    onSplitDetailFishNumChange() {
      // 父表 fishNumDiff = -fishNum + sum(details.fishNum)（对齐 PC calculateDiff）
      try {
        const r0 = (this.viz && this.viz.rows && this.viz.rows[0]) || null;
        if (!r0) return;
        const base = parseInt(String(r0.fishNum || "0"), 10) || 0;
        const list = Array.isArray(r0.details) ? r0.details : [];
        const total = list.reduce((sum, d) => sum + (parseInt(String((d && d.fishNum) || "0"), 10) || 0), 0);
        this.$set(r0, "fishNumDiff", -base + total);
      } catch (e) {
        /* ignore */
      }
      this.persistDataEntryDraftToLocal();
    },
    onUseMedicineLogProductChange(logRow, productNo) {
      try {
        const item = (this.deMedicineProductList || []).find((x) => x && String(x.productNo) === String(productNo));
        applyMedicineProductToLogRow(logRow, item);
      } catch (e) {
        /* ignore */
      }
      this.persistDataEntryDraftToLocal();
    },
    displayCellValue(row, col) {
      if (!row || !col) return "";
      // 投放鱼池展示兜底：有的 schema 用 pondName，但联动填充的是 pondDetailNum
      if (this.viz && this.viz.module_id === "zk_pond_fish_detail_add") {
        const k = String(col.key || "");
        const lbl = String(col.label || "");
        const isPondDisplay = k === "pondDetailNum" || k === "pondName" || lbl.indexOf("投放鱼池") >= 0;
        if (isPondDisplay) {
          const vd = row.pondDetailNum;
          if (vd !== null && vd !== undefined && String(vd).trim() !== "") return vd;
          const vn = row.pondName;
          if (vn !== null && vn !== undefined && String(vn).trim() !== "") return vn;
        }
      }

      const v = row[col.key];
      if (v === null || v === undefined) return "";
      // datetime：回显时将时间戳格式化为 yyyy-MM-dd HH:mm:ss
      if (col.type === "datetime") {
        const ts = typeof v === "number" ? v : parseInt(String(v), 10);
        if (!Number.isFinite(ts) || ts <= 0) return "";
        const d = new Date(ts);
        const pad = (n) => String(n).padStart(2, "0");
        const yyyy = d.getFullYear();
        const MM = pad(d.getMonth() + 1);
        const dd = pad(d.getDate());
        const hh = pad(d.getHours());
        const mm = pad(d.getMinutes());
        const ss = pad(d.getSeconds());
        return `${yyyy}-${MM}-${dd} ${hh}:${mm}:${ss}`;
      }
      return v;
    },
    onNumericCellInput(row, col, v) {
      const key = (col && col.key) || "";
      if (this.viz && this.viz.module_id === "zk_pond_fish_detail_add" && key === "fishNum") {
        // 输入中实时纠正，但不弹提示（避免刷屏）
        return this.onStockingFishNumChange(row, v, { showTip: false, forceTickWriteback: true });
      }
      return this.onCellChange(row, key, v);
    },
    onNumericCellChange(row, col, v) {
      const key = (col && col.key) || "";
      // 投放记录：fishNum 需要库存上限保护，且要把“修正后的值”写回到 v-model（否则只提示但不生效）
      if (this.viz && this.viz.module_id === "zk_pond_fish_detail_add" && key === "fishNum") {
        // change 阶段弹提示即可
        return this.onStockingFishNumChange(row, v, { showTip: true, forceTickWriteback: true });
      }
      return this.onCellChange(row, key, v);
    },
    onMaybeTextCellInput(row, col, v) {
      try {
        // 用药记录：避免每次键入都 stringify(含子表) 导致卡顿；改成失焦再处理/保存
        if (this.viz && this.viz.module_id === "zk_use_medicine_add") {
          // v-model 已经写入 row，无需这里重复 set / persist
          return;
        }
      } catch (e) {
        /* ignore */
      }
      return this.onCellChange(row, (col && col.key) || "", v);
    },
    onMaybeTextCellChange(row, col, v) {
      // 用药记录：在 change（失焦）时再落草稿 + required hint
      return this.onCellChange(row, (col && col.key) || "", v);
    },
    onStockingFishNumChange(row, v, opts = {}) {
      const base = Number(row && row._remainingInventoryBase);
      const n = Number(v);
      let nextVal = v;
      const shouldClamp = Number.isFinite(base) && Number.isFinite(n) && n > base;
      if (shouldClamp) {
        nextVal = base;
        if (opts.showTip) {
          this.$message && this.$message.warning(`当前批次剩余鱼苗数${base}小于${n}，已自动改为${base}`);
        }
      }
      // 用 $set 写回，确保 el-input-number 立即显示修正后的值
      this.$set(row, "fishNum", nextVal);
      try {
        if (this.viz && this.viz.rows && this.viz.rows[0]) {
          this.$set(this.viz.rows[0], "fishNum", nextVal);
        }
      } catch (e) {
        /* ignore */
      }
      // 触发强制重渲染：解决 el-input-number 内部值回写导致“提示了但不生效”
      if (shouldClamp) {
        const cur = Number(row.__deForceFishNum || 0);
        this.$set(row, "__deForceFishNum", cur + 1);
      }
      // 兜底：input-number 有时在同一 tick 内回写内部值，下一轮 tick 再写一次更稳定
      if (opts.forceTickWriteback) {
        this.$nextTick(() => {
          try {
            this.$set(row, "fishNum", nextVal);
            if (this.viz && this.viz.rows && this.viz.rows[0]) {
              this.$set(this.viz.rows[0], "fishNum", nextVal);
            }
            if (shouldClamp) {
              const cur2 = Number(row.__deForceFishNum || 0);
              this.$set(row, "__deForceFishNum", cur2 + 1);
            }
          } catch (e) {
            /* ignore */
          }
        });
      }
      recomputeStockingDerivedFields(row, "fishNum");
      this.persistDataEntryDraftToLocal();
    },
    dataEntryLocalKeyByFid(moduleId, fid) {
      const mid = moduleId || "no_module";
      const f = fid || "";
      if (!f) return "";
      return `CHAT_DATA_ENTRY_DRAFT::${mid}::FID::${f}`;
    },
    dataEntrySessionToFidKey(moduleId, sessionId) {
      const mid = moduleId || "no_module";
      const sid = sessionId || "";
      if (!sid) return "";
      return `CHAT_DATA_ENTRY_SESSION_FID::${mid}::${sid}`;
    },
    dataEntryLocalKey() {
      const v = this.viz || {};
      if (!v || v.type !== "data_entry") return "";
      const mid = v.module_id || "no_module";
      const row = (v.rows && v.rows[0]) || {};
      const fid = row && row.fid ? String(row.fid) : "";
      // 优先使用 fid：跨 session 重建也不丢；避免 lastKey 串表
      const k1 = this.dataEntryLocalKeyByFid(mid, fid);
      if (k1) return k1;
      // 兜底：没有 fid 时才用 session（理论上投放模块不会走到这里，因为会 ensure fid）
      const sid = v.session_id || "no_session";
      return `CHAT_DATA_ENTRY_DRAFT::${mid}::SID::${sid}`;
    },
    restoreDataEntryDraftFromLocal() {
      try {
        const v = this.viz;
        if (!v || v.type !== "data_entry") return;
        const row = (v.rows && v.rows[0]) || {};
        // 先确保投放模块 fid/时间存在，避免后续 key 构建拿不到 fid
        if (v.module_id === "zk_pond_fish_detail_add") {
          ensureZkPondFishDetailAddDefaults(row);
        }

        const mid = v.module_id || "no_module";
        const fid = row && row.fid ? String(row.fid) : "";
        // 1) 先按 fid 恢复（最稳）
        let key = this.dataEntryLocalKeyByFid(mid, fid);
        let raw = key ? localStorage.getItem(key) : "";
        // 2) 若 fid 仍为空或没命中，按 session_id -> fid 映射尝试恢复
        if (!raw) {
          const mapKey = this.dataEntrySessionToFidKey(mid, v.session_id || "");
          const mappedFid = mapKey ? localStorage.getItem(mapKey) : "";
          if (mappedFid) {
            const k2 = this.dataEntryLocalKeyByFid(mid, mappedFid);
            const raw2 = k2 ? localStorage.getItem(k2) : "";
            if (raw2) {
              key = k2;
              raw = raw2;
            }
          }
        }
        // 3) 最后兜底：SID key（仅用于 fid 缺失的极端情况）
        if (!raw) {
          const sid = v.session_id || "no_session";
          const k3 = `CHAT_DATA_ENTRY_DRAFT::${mid}::SID::${sid}`;
          const raw3 = localStorage.getItem(k3);
          if (raw3) {
            key = k3;
            raw = raw3;
          }
        }
        if (!raw) return;
        const parsed = JSON.parse(raw);
        const meta = (parsed && parsed.__meta) || {};
        const metaFid = meta && meta.fid ? String(meta.fid) : "";
        const rowFid = row && row.fid ? String(row.fid) : "";
        const usingFallback = !key.includes("::FID::"); // 非 fid key 都视为兜底
        const fidStrictMatch = !!metaFid && !!rowFid && metaFid === rowFid;
        if (usingFallback && !fidStrictMatch) {
          return;
        }
        const savedRow = (parsed && parsed.row) || null;
        if (savedRow && typeof savedRow === "object") {
          Object.keys(savedRow).forEach((k) => {
            if (k === "__meta") return;
            const sv = savedRow[k];
            // 不用“空值”覆盖当前草稿默认值/联动值，避免出现时间不显示、重量/投放后数量不联动
            if (sv === null || sv === undefined || String(sv).trim() === "") return;
            this.$set(row, k, sv);
          });
        }
        if (parsed && parsed.submitted) {
          // submitted 恢复：仅允许“同 session key”或“兜底且 fid 严格匹配”两种情况
          if (!usingFallback || fidStrictMatch) {
            this.dataEntrySubmitted = true;
            this.$set(v, "_dataEntrySubmitted", true);
          }
        }
        if (parsed && parsed.draft_version) {
          this.$set(v, "draft_version", parsed.draft_version);
        }

        // 回填后：对齐模块默认值与派生字段，确保“时间显示/重量联动/投放后鱼数联动”不被回填打断
        if (v.module_id === "zk_pond_fish_detail_add") {
          recomputeStockingDerivedFields(row, "fishNum");
        }
      } catch (e) {
        // ignore
      }
    },
    persistDataEntryDraftToLocal(immediate = false) {
      const doSave = () => {
        try {
          const v = this.viz;
          if (!v || v.type !== "data_entry") return;
          const row = (v.rows && v.rows[0]) || {};
          // 投放模块：确保 fid 在任何保存前就存在，保证 key 稳定
          if (v.module_id === "zk_pond_fish_detail_add") {
            ensureZkPondFishDetailAddDefaults(row);
          }
          const key = this.dataEntryLocalKey();
          if (!key) return;
          const payload = {
            submitted: !!(this.dataEntrySubmitted || v._dataEntrySubmitted),
            draft_version: v.draft_version || 1,
            __meta: {
              module_id: v.module_id || "",
              session_id: v.session_id || "",
              fid: row && row.fid ? String(row.fid) : "",
            },
            row: row,
          };
          localStorage.setItem(key, JSON.stringify(payload));
          // 记录 session_id -> fid 映射：session 重建/变化时仍能找回同一张业务表
          const mid = v.module_id || "no_module";
          const fid = row && row.fid ? String(row.fid) : "";
          if (fid && v.session_id) {
            const mapKey = this.dataEntrySessionToFidKey(mid, v.session_id);
            if (mapKey) localStorage.setItem(mapKey, fid);
          }
        } catch (e) {
          // ignore
        }
      };
      if (immediate) {
        doSave();
        return;
      }
      if (this._dePersistTimer) clearTimeout(this._dePersistTimer);
      this._dePersistTimer = setTimeout(doSave, 250);
    },
    columnMinWidth(col) {
      if (!col) return 140;
      if (col.minWidth) return col.minWidth;
      // 时间列默认需要完整展示到秒
      if (col.type === "datetime") return 200;
      // 下拉项文本通常较长（批次/鱼池）
      if (this.viz && (this.viz.module_id === "zk_pond_fish_detail_add" || this.viz.module_id === "zk_pond_fish_detail_dead_add")) {
        if (["batchNumber", "pondId", "productNo", "userId"].includes(col.key)) return 220;
      }
      return 140;
    },
    /** 投放记录：不可编辑字段（PC 里是选择/联动出来的快照） */
    stockingReadonlyKeys() {
      return [
        "fid",
        "supplier",
        "supplierId",
        "supplierAddress",
        "workshopName",
        "workshopId",
        "pondName",
        "pondNum",
        "pondScale",
        "pondDetailNum",
        "pondSystemName",
        "pondSystemId",
        "deptId",
        "tenantId",
        "userName",
        "historyFishNum",
        "currFishNum",
        "remainingInventory",
        "_remainingInventoryBase",
      ];
    },
    feedingReadonlyKeys() {
      return [
        "workshopId",
        "workshopName",
        "pondSystemId",
        "pondSystemName",
        "pondDetailNum",
        "pondName",
        "pondNum",
        "pondScale",
        "fishNum",
        "productId",
        "feedName",
        "feedBrand",
        "feedSpec",
        "feedingPrice",
        "feedingCost",
        "userName",
        "deptId",
        "tenantId",
      ];
    },
    isReadonlyCell(col) {
      if (!col) return false;
      // 已提交：整表只读
      if (this.isDataEntrySubmitted) return true;
      // 后端 schema 若有 readonly/disabled 优先尊重
      if (this.viz && (this.viz.module_id === "zk_pond_fish_detail_add" || this.viz.module_id === "zk_pond_fish_detail_dead_add")) {
        // 未提交：只锁“联动/派生字段”，其余允许编辑（避免后端 schema 误传 editable=false 造成整表不可编辑）
        return this.stockingReadonlyKeys().includes(col.key);
      }
      if (this.viz && this.viz.module_id === "zk_feeding_log_add") {
        return this.feedingReadonlyKeys().includes(col.key);
      }
      if (this.viz && this.viz.module_id === "zk_pond_fish_parent_add") {
        // 分池：原池编号/原池数量/分池差距均为联动/计算字段（对齐 PC）
        const ro = new Set(["originPondDetailNum", "fishNum", "fishNumDiff"]);
        if (ro.has(String(col.key || ""))) return true;
      }
      // 其它模块：仍尊重 schema
      if (col.readonly || col.disabled || col.editable === false) return true;
      return false;
    },
    /** 数据录入：加载下拉、默认时间/fid（仅投放记录模块已实现） */
    async bootstrapDataEntryIfNeeded() {
      const v = this.viz;
      if (!v || v.type !== "data_entry") return;
      this.deInitializing = true;
      // 关键：每次新表格初始化时先清空“已提交态”，避免历史状态误带导致首次就全只读
      this.dataEntrySubmitted = false;
      // 新表格首次渲染不提示必填，等用户提交/编辑再提示（对齐 PC 弹窗体验）
      this.deShowRequiredHint = false;
      if (v._dataEntrySubmitted) this.$set(v, "_dataEntrySubmitted", false);

      // A: 历史回显/已提交表格不加载下拉，避免对话多时请求风暴
      const editable = !!(v && v._editable);
      if (!editable || this.isDataEntrySubmitted) {
        this.deInitializing = false;
        return;
      }
      // 注意：下拉“加载”需要去重，但“联动补齐”必须每次都跑
      // 否则同一 session 里后端又回传新 row（draft_version 未变）会导致 deptId/userName/fishTypes 等再次丢失

      if (v.module_id === "zk_pond_fish_detail_add" || v.module_id === "zk_pond_fish_detail_dead_add") {
        const row = (v.rows && v.rows[0]) || {};
        ensureZkPondFishDetailAddDefaults(row);
        // 如果已经有缓存则不重复请求，但“回显后联动字段”需要重新 apply 一次
        const needLoad =
          !(this.deBatchList && this.deBatchList.length) ||
          !(this.dePondList && this.dePondList.length) ||
          !(this.deUserList && this.deUserList.length) ||
          !(this.deProductList && this.deProductList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
          this.deOptionsLoading = true;
          try {
            const [b, p, u, pr] = await Promise.all([
              loadBatchOptionsForStocking(),
              loadPondOptionsForStocking(),
              loadUserOptionsForStocking(),
              loadProductOptionsForStocking(),
            ]);
            this.deBatchList = b;
            this.dePondList = p;
            this.deUserList = u;
            this.deProductList = pr;
          } catch (e) {
            console.warn("数据录入下拉加载失败", e);
          } finally {
            this.deOptionsLoading = false;
          }
          }
        }

        // 关键：回显/恢复后，把 batch/pond 的联动字段重新跑一遍（死亡模块同样需要这些快照字段）
        try {
          if (row.batchNumber) {
            const bi = (this.deBatchList || []).find((x) => x && x.relationOrderNo === row.batchNumber);
            applyStockingBatchToRow(row, bi);
          }
          if (row.pondId) {
            const pi = (this.dePondList || []).find((x) => x && String(x.id) === String(row.pondId));
            applyStockingPondToRow(row, pi);
          }
          // userId 可能由后端默认推断（用户未点下拉也要补齐 deptId/userName/tenantId）
          if (row.userId) {
            const ui = (this.deUserList || []).find((x) => x && String(x.id) === String(row.userId));
            applyStockingUserToRow(row, ui);
          }
          // productNo 若由批次带出，也尽量补齐 fishName/fishTypes/fishSpec（对齐 PC updateProductId）
          if (row.productNo) {
            const pr = (this.deProductList || []).find((x) => x && String(x.productNo) === String(row.productNo));
            applyStockingProductToRow(row, pr);
          }
          // 强制重算：确保回显后“投放后/死亡后存塘鱼数”等派生字段都有值
          recomputeStockingDerivedFields(row, "fishNum");
        } catch (e) {
          /* ignore */
        }
      }

      if (v.module_id === "zk_pond_fish_type_add") {
        const row = (v.rows && v.rows[0]) || {};
        // 年月日默认值：必须用 $set 挂载到响应式对象（Vue2 直接赋值新增字段不更新 UI）
        try {
          const d = new Date();
          const yy = d.getFullYear();
          const mm = d.getMonth() + 1;
          const dd = d.getDate();
          if (!(("staticYear" in row) && row.staticYear)) this.$set(row, "staticYear", row.staticYear || yy);
          if (!(("staticMonth" in row) && row.staticMonth)) this.$set(row, "staticMonth", row.staticMonth || mm);
          if (!(("staticDay" in row) && row.staticDay)) this.$set(row, "staticDay", row.staticDay || dd);
        } catch (e) {
          /* ignore */
        }
        // 仍保留工具函数兜底（用于回填/非 UI 场景）
        ensureZkPondFishTypeAddDefaults(row);
        // fishType：对齐 PC，用系统字典下拉（DICT_TYPE.ZK_POND_FISH_TYPES）
        try {
          const cols = (v && v.ui && v.ui.columns) || [];
          const fishTypeCol = cols.find((c) => c && String(c.key) === "fishType");
          if (fishTypeCol && (!fishTypeCol.enum || !fishTypeCol.enum.length)) {
            const opts = getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES) || [];
            // 统一成 {label,value} 结构（与 ChatBiPanel 的 enum 渲染一致）
            const normalized = (opts || []).map((x) => ({ label: x.label, value: x.value }));
            this.$set(fishTypeCol, "enum", normalized);
          }
        } catch (e) {
          /* ignore */
        }
        const needLoad = !(this.deBatchList && this.deBatchList.length) || !(this.dePondList && this.dePondList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
            this.deOptionsLoading = true;
            try {
              const [b, p] = await Promise.all([loadBatchOptionsForStocking(), loadPondOptionsForStocking()]);
              this.deBatchList = b;
              this.dePondList = p;
            } catch (e) {
              console.warn("鱼苗种类下拉加载失败", e);
            } finally {
              this.deOptionsLoading = false;
            }
          }
        }
        // 回显：尽量补齐存塘快照字段（若已有 pondId）
        try {
          if (row.pondId) {
            const pi = (this.dePondList || []).find((x) => x && String(x.id) === String(row.pondId));
            applyStockingPondToRow(row, pi);
          }
        } catch (e) {
          /* ignore */
        }
      }

      if (v.module_id === "zk_workshop_add") {
        const row = (v.rows && v.rows[0]) || {};
        // 默认值：status=2（养殖中）；fid 若后端未给则前端兜底
        try {
          if (!("status" in row) || row.status == null || String(row.status).trim() === "") this.$set(row, "status", "2");
          if (!("fid" in row) || !row.fid) this.$set(row, "fid", `wk_${Date.now()}`);
        } catch (e) {
          /* ignore */
        }
        // 字典：状态下拉（DICT_TYPE.ZK_WORKSHOP_STATUS）
        try {
          const cols = (v && v.ui && v.ui.columns) || [];
          const statusCol = cols.find((c) => c && String(c.key) === "status");
          if (statusCol && (!statusCol.enum || !statusCol.enum.length)) {
            const opts = getDictDatas(DICT_TYPE.ZK_WORKSHOP_STATUS) || [];
            const normalized = (opts || []).map((x) => ({ label: x.label, value: x.value }));
            this.$set(statusCol, "enum", normalized);
          }
        } catch (e) {
          /* ignore */
        }
        const needLoad =
          !(this.deUserList && this.deUserList.length) ||
          !(this.deDeptList && this.deDeptList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
            this.deOptionsLoading = true;
            try {
              const [u, d] = await Promise.all([loadUserOptionsForStocking(), loadDeptOptionsSimple()]);
              this.deUserList = u;
              this.deDeptList = d;
            } catch (e) {
              console.warn("车间记录下拉加载失败", e);
            } finally {
              this.deOptionsLoading = false;
            }
          }
        }
        // 回显/恢复：补齐 userName/deptName 等快照字段
        try {
          if (row.userId) {
            const ui = (this.deUserList || []).find((x) => x && String(x.id) === String(row.userId));
            applyStockingUserToRow(row, ui);
          }
          if (row.deptId) {
            const di = (this.deDeptList || []).find((x) => x && String(x.id) === String(row.deptId));
            if (di && di.name) this.$set(row, "deptName", di.name);
          }
        } catch (e) {
          /* ignore */
        }
      }
      if (v.module_id === "zk_pond_system_add") {
        const row = (v.rows && v.rows[0]) || {};
        // fid：Java 必填，后端也会兜底；前端这里再兜底一次避免 UI 未加载完就提交
        try {
          if (!("fid" in row) || !row.fid) this.$set(row, "fid", `ps_${Date.now()}`);
        } catch (e) {
          /* ignore */
        }
        const needLoad =
          !(this.deWorkshopList && this.deWorkshopList.length) ||
          !(this.deUserList && this.deUserList.length) ||
          !(this.deDeptList && this.deDeptList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
            this.deOptionsLoading = true;
            try {
              const [wk, u, d] = await Promise.all([loadWorkshopOptions(), loadUserOptionsForStocking(), loadDeptOptionsSimple()]);
              this.deWorkshopList = wk;
              this.deUserList = u;
              this.deDeptList = d;
            } catch (e) {
              console.warn("系统记录下拉加载失败", e);
            } finally {
              this.deOptionsLoading = false;
            }
          }
        }
        // 回显/恢复：补齐 workshopName/userName/deptName 等快照字段
        try {
          if (row.workshopId && !row.workshopName) {
            const wi = (this.deWorkshopList || []).find((x) => x && String(x.id) === String(row.workshopId));
            applyWorkshopToRow(row, wi);
          }
          if (row.userId && (!row.userName || !row.deptId || !row.tenantId)) {
            const ui = (this.deUserList || []).find((x) => x && String(x.id) === String(row.userId));
            applyStockingUserToRow(row, ui);
          }
          if (row.deptId && !row.deptName) {
            const di = (this.deDeptList || []).find((x) => x && String(x.id) === String(row.deptId));
            if (di && di.name) this.$set(row, "deptName", di.name);
          }
        } catch (e) {
          /* ignore */
        }
      }
      if (v.module_id === "zk_pond_add") {
        const row = (v.rows && v.rows[0]) || {};
        // 默认值：对齐 PC reset()
        try {
          if (!("status" in row) || row.status == null || String(row.status).trim() === "") this.$set(row, "status", "1");
          if (!("pondScale" in row) || row.pondScale == null || String(row.pondScale).trim() === "") this.$set(row, "pondScale", 20);
          if (!("fishNum" in row) || row.fishNum == null || String(row.fishNum).trim() === "") this.$set(row, "fishNum", 0);
          if (!("fid" in row) || !row.fid) this.$set(row, "fid", `pond_${Date.now()}`);
        } catch (e) {
          /* ignore */
        }
        const needLoad =
          !(this.dePondSystemList && this.dePondSystemList.length) ||
          !(this.deUserList && this.deUserList.length) ||
          !(this.deDeptList && this.deDeptList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
            this.deOptionsLoading = true;
            try {
              const [ps, u, d] = await Promise.all([loadPondSystemOptions(), loadUserOptionsForStocking(), loadDeptOptionsSimple()]);
              this.dePondSystemList = ps;
              this.deUserList = u;
              this.deDeptList = d;
            } catch (e) {
              console.warn("存塘记录下拉加载失败", e);
            } finally {
              this.deOptionsLoading = false;
            }
          }
        }
        // 回显/恢复：补齐联动快照字段
        try {
          if (row.pondSystemId) {
            const psi = (this.dePondSystemList || []).find((x) => x && String(x.id) === String(row.pondSystemId));
            applyPondSystemToPondRow(row, psi);
          }
          if (row.userId && (!row.userName || !row.deptId || !row.tenantId)) {
            const ui = (this.deUserList || []).find((x) => x && String(x.id) === String(row.userId));
            applyStockingUserToRow(row, ui);
          }
          if (row.deptId && !row.deptName) {
            const di = (this.deDeptList || []).find((x) => x && String(x.id) === String(row.deptId));
            if (di && di.name) this.$set(row, "deptName", di.name);
          }
        } catch (e) {
          /* ignore */
        }
      }
      if (v.module_id === "zk_data_revise_add") {
        const row = (v.rows && v.rows[0]) || {};
        // 默认 taskParam：对齐 PC（processKey=zk_data_revise）
        try {
          if (!("taskParam" in row) || !row.taskParam) {
            this.$set(row, "taskParam", {
              processKey: "zk_data_revise",
              submitToPrev: "y",
              finishProcess: "n",
              variables: {},
            });
          } else if (!row.taskParam.processKey) {
            this.$set(row.taskParam, "processKey", "zk_data_revise");
          }
        } catch (e) {
          /* ignore */
        }
        // 初始化：如果已有 reviseType，拉对应业务记录 options
        try {
          const rt = row.reviseType != null ? String(row.reviseType) : "";
          if (rt) {
            this.deOptionsLoading = true;
            loadReviseBizOptions(rt)
              .then((list) => { this.deReviseBizList = list || []; })
              .catch(() => { this.deReviseBizList = []; })
              .finally(() => { this.deOptionsLoading = false; });
          }
        } catch (e) {
          /* ignore */
        }
        // 修正记录：也需要用户列表来补齐 deptId/tenantId/userName（避免落库 dept_id 为 null）
        try {
          if (!(this.deUserList && this.deUserList.length)) {
            this.deOptionsLoading = true;
            loadUserOptionsForStocking()
              .then((u) => { this.deUserList = u || []; })
              .catch(() => { /* ignore */ })
              .finally(() => { this.deOptionsLoading = false; });
          }
        } catch (e) {
          /* ignore */
        }
        // 回显：差值存在时补齐变化比例
        try {
          const cr = computeReviseChangeRatio(row.differenceData, row.originData);
          if (cr != null) this.$set(row, "changeRatio", cr);
        } catch (e) {
          /* ignore */
        }
      }
      if (v.module_id === "zk_pond_fish_parent_add") {
        const row = (v.rows && v.rows[0]) || {};
        // 默认值：addType=-3；details 数组必须提前挂载避免 Vue2 非响应
        try {
          if (!("addType" in row) || !row.addType) this.$set(row, "addType", "-3");
          if (!Array.isArray(row.details)) this.$set(row, "details", []);
          // fishNum/fishNumDiff 默认 0，避免必填校验误判
          if (!("fishNum" in row) || row.fishNum == null || String(row.fishNum).trim() === "") this.$set(row, "fishNum", 0);
          if (!("fishNumDiff" in row) || row.fishNumDiff == null || String(row.fishNumDiff).trim() === "") this.$set(row, "fishNumDiff", 0);
        } catch (e) {
          /* ignore */
        }
        // 下拉：批次/原池/鱼苗/负责人（用于 deptId/tenantId/userName 补齐）
        const needLoad =
          !(this.deBatchList && this.deBatchList.length) ||
          !(this.dePondList && this.dePondList.length) ||
          !(this.deProductList && this.deProductList.length) ||
          !(this.deUserList && this.deUserList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
            this.deOptionsLoading = true;
            try {
              const [b, p, pr, u] = await Promise.all([
                loadBatchOptionsForStocking(),
                loadPondOptionsForStocking(),
                loadProductOptionsForStocking(),
                loadUserOptionsForStocking(),
              ]);
              this.deBatchList = b;
              this.dePondList = p;
              this.deProductList = pr;
              this.deUserList = u;
            } catch (e) {
              console.warn("分池记录下拉加载失败", e);
            } finally {
              this.deOptionsLoading = false;
            }
          }
        }
        // 回显/恢复：补齐快照字段（批次/原池/鱼苗/操作人）
        try {
          if (row.batchNumber) {
            const bi = (this.deBatchList || []).find((x) => x && x.relationOrderNo === row.batchNumber);
            // 复用批次联动：补齐 productNo/fishSpec/fishName/fishTypes 等
            applyStockingBatchToRow(row, bi);
          }
          if (row.originPondId) {
            const pi = (this.dePondList || []).find((x) => x && String(x.id) === String(row.originPondId));
            if (pi) {
              this.$set(row, "originPondDetailNum", pi.pondDetailNum);
              // 恢复时也联动回填“原总数量”(fishNum)
              if (pi.fishNum != null) this.$set(row, "fishNum", parseInt(String(pi.fishNum), 10) || 0);
              // 重新计算 fishNumDiff，避免回显时不一致
              this.onSplitDetailFishNumChange();
            }
          }
          if (row.productNo) {
            const pr = (this.deProductList || []).find((x) => x && String(x.productNo) === String(row.productNo));
            applyStockingProductToRow(row, pr);
          }
          if (row.userId && (!row.userName || !row.deptId || !row.tenantId)) {
            const ui = (this.deUserList || []).find((x) => x && String(x.id) === String(row.userId));
            applyStockingUserToRow(row, ui);
          }
        } catch (e) {
          /* ignore */
        }
        // 子表：确保每行携带 batchNumber/pondId/userId/deptId
        try {
          if (Array.isArray(row.details)) {
            row.details.forEach((d) => {
              if (!d) return;
              if (d.batchNumber == null) d.batchNumber = row.batchNumber;
              if (d.pondId == null) d.pondId = row.originPondId;
              if (d.productNo == null) d.productNo = row.productNo;
              if (d.fishName == null) d.fishName = row.fishName;
              if (d.fishTypes == null) d.fishTypes = row.fishTypes;
              if (d.addType == null) d.addType = row.addType;
              if (d.userId == null) d.userId = row.userId;
              if (d.deptId == null) d.deptId = row.deptId;
            });
          }
        } catch (e) {
          /* ignore */
        }
      }

      if (v.module_id === "zk_feeding_log_add") {
        const row = (v.rows && v.rows[0]) || {};
        ensureZkFeedingLogAddDefaults(row);
        const needLoad =
          !(this.deFeedingPondList && this.deFeedingPondList.length) ||
          !(this.deFeedingUserList && this.deFeedingUserList.length) ||
          !(this.deFeedingProductList && this.deFeedingProductList.length) ||
          !(this.deWarehouseList && this.deWarehouseList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
            this.deOptionsLoading = true;
            try {
              const [p, u, pr, wh] = await Promise.all([
                loadPondOptionsForFeeding(),
                loadUserOptionsForFeeding(),
                loadProductOptionsForFeeding(),
                loadWarehouseOptionsForFeeding(),
              ]);
              this.deFeedingPondList = p;
              this.deFeedingUserList = u;
              this.deFeedingProductList = pr;
              this.deWarehouseList = wh;
            } catch (e) {
              console.warn("投喂数据录入下拉加载失败", e);
            } finally {
              this.deOptionsLoading = false;
            }
          }
        }

        // 回显/恢复后：重新 apply 一遍联动字段
        try {
          if (row.pondId) {
            const pi = (this.deFeedingPondList || []).find((x) => x && String(x.id) === String(row.pondId));
            applyFeedingPondToRow(row, pi);
          }
          if (row.productNo) {
            const pr = (this.deFeedingProductList || []).find((x) => x && String(x.productNo) === String(row.productNo));
            applyFeedingProductToRow(row, pr);
          }
          if (row.userId) {
            const ui = (this.deFeedingUserList || []).find((x) => x && String(x.id) === String(row.userId));
            applyFeedingUserToRow(row, ui);
          }
          recomputeFeedingDerivedFields(row, "feedCount");
        } catch (e) {
          /* ignore */
        }
      }

      if (v.module_id === "zk_use_medicine_add") {
        const row = (v.rows && v.rows[0]) || {};
        ensureZkUseMedicineAddDefaults(row);
        const needLoad =
          !(this.deFeedingPondList && this.deFeedingPondList.length) ||
          !(this.deFeedingUserList && this.deFeedingUserList.length) ||
          !(this.deMedicineProductList && this.deMedicineProductList.length);
        if (needLoad) {
          const loadKey = `${v.module_id || ""}_${v.session_id || ""}_${v.draft_version || ""}`;
          if (this.deBootstrappedKey !== loadKey) {
            this.deBootstrappedKey = loadKey;
            this.deOptionsLoading = true;
            try {
              const [p, u, pr] = await Promise.all([loadPondOptionsForFeeding(), loadUserOptionsForFeeding(), loadProductOptionsForMedicine()]);
              this.deFeedingPondList = p;
              this.deFeedingUserList = u;
              this.deMedicineProductList = pr;
            } catch (e) {
              console.warn("用药数据录入下拉加载失败", e);
            } finally {
              this.deOptionsLoading = false;
            }
          }
        }
        // 回显/恢复后：补齐存塘/执行人快照字段
        try {
          if (row.pondId) {
            const pi = (this.deFeedingPondList || []).find((x) => x && String(x.id) === String(row.pondId));
            applyFeedingPondToRow(row, pi);
          }
          if (row.userId) {
            const ui = (this.deFeedingUserList || []).find((x) => x && String(x.id) === String(row.userId));
            applyFeedingUserToRow(row, ui);
          }
        } catch (e) {
          /* ignore */
        }
      }
      this.deInitializing = false;
    },
    /** 投放记录：哪些列走远程下拉 */
    stockingRemoteKind(col) {
      if (
        !this.viz ||
        !(
          this.viz.module_id === "zk_pond_fish_detail_add" ||
          this.viz.module_id === "zk_pond_fish_detail_dead_add" ||
          this.viz.module_id === "zk_pond_fish_type_add"
        ) ||
        !col
      )
        return null;
      const m = {
        batchNumber: "batch",
        pondId: "pond",
        userId: "user",
        productNo: "product",
      };
      return m[col.key] || null;
    },
    feedingRemoteKind(col) {
      if (!this.viz || !(this.viz.module_id === "zk_feeding_log_add" || this.viz.module_id === "zk_use_medicine_add") || !col) return null;
      const m = {
        pondId: "pond",
        userId: "user",
      };
      if (this.viz.module_id === "zk_feeding_log_add") {
        m.productNo = "product";
        m.warehouseId = "warehouse";
      }
      return m[col.key] || null;
    },
    workshopRemoteKind(col) {
      if (!this.viz || this.viz.module_id !== "zk_workshop_add" || !col) return null;
      const m = {
        userId: "user",
        deptId: "dept",
      };
      return m[col.key] || null;
    },
    pondSystemRemoteKind(col) {
      if (!this.viz || this.viz.module_id !== "zk_pond_system_add" || !col) return null;
      const m = {
        workshopId: "workshop",
        userId: "user",
        deptId: "dept",
      };
      return m[col.key] || null;
    },
    pondRemoteKind(col) {
      if (!this.viz || this.viz.module_id !== "zk_pond_add" || !col) return null;
      const m = {
        pondSystemId: "pondSystem",
        userId: "user",
        deptId: "dept",
      };
      return m[col.key] || null;
    },
    reviseRemoteKind(col) {
      if (!this.viz || this.viz.module_id !== "zk_data_revise_add" || !col) return null;
      const m = {
        busId: "biz",
      };
      return m[col.key] || null;
    },
    splitParentRemoteKind(col) {
      if (!this.viz || this.viz.module_id !== "zk_pond_fish_parent_add" || !col) return null;
      const m = {
        batchNumber: "batch",
        originPondId: "originPond",
        productNo: "product",
      };
      return m[col.key] || null;
    },
    onStockingBatchChange(row, relationOrderNo) {
      const item = (this.deBatchList || []).find((x) => x.relationOrderNo === relationOrderNo);
      applyStockingBatchToRow(row, item);
      // 批次变更后，按当前投放数量刷新剩余/重量等派生字段
      recomputeStockingDerivedFields(row, "fishNum");
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onStockingPondChange(row, pondId) {
      const item = (this.dePondList || []).find((x) => String(x.id) === String(pondId));
      applyStockingPondToRow(row, item);
      // 鱼池变更后：historyFishNum 已落地，立即刷新 currFishNum
      recomputeStockingDerivedFields(row, "pondId");
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onStockingProductChange(row, productNo) {
      const item = (this.deProductList || []).find((x) => x && String(x.productNo) === String(productNo));
      applyStockingProductToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onStockingUserChange(row, userId) {
      const item = (this.deUserList || []).find((x) => String(x.id) === String(userId));
      applyStockingUserToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onSplitParentBatchChange(row, relationOrderNo) {
      const item = (this.deBatchList || []).find((x) => x && x.relationOrderNo === relationOrderNo);
      applyStockingBatchToRow(row, item);
      // 批次变更后：保持子表新行默认规格跟随主表 fishSpec
      try {
        if (Array.isArray(row.details)) {
          row.details.forEach((d) => {
            if (!d) return;
            if (d.batchNumber == null) d.batchNumber = relationOrderNo;
            if (!d.targetFishSpec && row.fishSpec != null) d.targetFishSpec = String(row.fishSpec);
          });
        }
      } catch (e) {
        /* ignore */
      }
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onSplitParentOriginPondChange(row, originPondId) {
      try {
        const item = (this.dePondList || []).find((x) => x && String(x.id) === String(originPondId));
        this.$set(row, "originPondId", originPondId);
        if (item && item.pondDetailNum) this.$set(row, "originPondDetailNum", item.pondDetailNum);
        // 原池联动：回填“原总数量”(fishNum)（对齐 PC：选原池后自动带出当前存塘数量）
        if (item && item.fishNum != null) {
          this.$set(row, "fishNum", parseInt(String(item.fishNum), 10) || 0);
        }
        // 子表：pondId = originPondId（Java 侧按 pondId 识别来源池）
        if (Array.isArray(row.details)) {
          row.details.forEach((d) => {
            if (!d) return;
            d.pondId = originPondId;
          });
        }
      } catch (e) {
        /* ignore */
      }
      // 原总数量变更后：同步重新计算 fishNumDiff
      this.onSplitDetailFishNumChange();
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onSplitParentProductChange(row, productNo) {
      const item = (this.deProductList || []).find((x) => x && String(x.productNo) === String(productNo));
      applyStockingProductToRow(row, item);
      // 子表：复制鱼苗快照字段，避免提交出现 null
      try {
        if (Array.isArray(row.details)) {
          row.details.forEach((d) => {
            if (!d) return;
            d.productNo = row.productNo;
            d.fishName = row.fishName;
            d.fishTypes = row.fishTypes;
          });
        }
      } catch (e) {
        /* ignore */
      }
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onFeedingPondChange(row, pondId) {
      const item = (this.deFeedingPondList || []).find((x) => String(x.id) === String(pondId));
      applyFeedingPondToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onFeedingProductChange(row, productNo) {
      const item = (this.deFeedingProductList || []).find((x) => String(x.productNo) === String(productNo));
      applyFeedingProductToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onFeedingWarehouseChange(row, warehouseId) {
      row.warehouseId = warehouseId;
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onFeedingUserChange(row, userId) {
      const item = (this.deFeedingUserList || []).find((x) => String(x.id) === String(userId));
      applyFeedingUserToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onWorkshopUserChange(row, userId) {
      const item = (this.deUserList || []).find((x) => String(x.id) === String(userId));
      // 复用 PC updateUserId：联动 tenantId/deptId/userName
      applyStockingUserToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onWorkshopDeptChange(row, deptId) {
      try {
        const item = (this.deDeptList || []).find((x) => x && String(x.id) === String(deptId));
        this.$set(row, "deptId", deptId);
        if (item && item.name) this.$set(row, "deptName", item.name);
      } catch (e) {
        /* ignore */
      }
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onPondSystemWorkshopChange(row, workshopId) {
      const item = (this.deWorkshopList || []).find((x) => x && String(x.id) === String(workshopId));
      applyWorkshopToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onPondSystemUserChange(row, userId) {
      const item = (this.deUserList || []).find((x) => String(x.id) === String(userId));
      applyStockingUserToRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onPondSystemDeptChange(row, deptId) {
      return this.onWorkshopDeptChange(row, deptId);
    },
    onPondPondSystemChange(row, pondSystemId) {
      const item = (this.dePondSystemList || []).find((x) => x && String(x.id) === String(pondSystemId));
      // 对齐 PC：选择系统后联动写 pondDetailNum 前缀、workshop/pondNum/pondSystemName 等
      applyPondSystemToPondRow(row, item);
      this.persistDataEntryDraftToLocal();
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    onPondUserChange(row, userId) {
      return this.onPondSystemUserChange(row, userId);
    },
    onPondDeptChange(row, deptId) {
      return this.onWorkshopDeptChange(row, deptId);
    },
    onReviseTypeChange(row, reviseType) {
      try {
        this.$set(row, "reviseType", reviseType);
        // 切换操作类型：清理业务选择与快照字段（避免残留）
        ["busId", "pondId", "originData", "differenceData", "changeRatio"].forEach((k) => {
          if (k in row) this.$set(row, k, null);
        });
      } catch (e) {
        /* ignore */
      }
      // 拉取对应业务记录下拉
      this.deOptionsLoading = true;
      loadReviseBizOptions(reviseType)
        .then((list) => { this.deReviseBizList = list || []; })
        .catch(() => { this.deReviseBizList = []; })
        .finally(() => { this.deOptionsLoading = false; });
      this.persistDataEntryDraftToLocal(true);
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    reviseBizLabel(it, reviseType) {
      const rt = reviseType != null ? String(reviseType) : "";
      if (!it) return "";
      try {
        if (rt === "1") return `${it.pondDetailNum}(投放${it.fishNum}条,规格:${it.fishSpec})`;
        if (rt === "2") return `${it.pondDetailNum}(死亡${it.fishNum}条,规格:${it.fishSpec})`;
        if (rt === "3") return `${it.pondDetailNum}->${it.targetPondDetailNum || ""}(分池${it.fishNum}条,规格:${it.fishSpec})`;
        if (rt === "4") return `${it.pondDetailNum}(投喂${it.feedCount || it.fishNum || ""},饲料:${it.feedName || ""})`;
        if (rt === "5") return `${it.pondDetailNum}(${it.pondName}:${it.pondScale}m³:${it.fishNum}条)`;
      } catch (e) {
        /* ignore */
      }
      return String(it.id || "");
    },
    onReviseBizChange(row, busId) {
      try {
        const rt = row && row.reviseType != null ? String(row.reviseType) : "";
        const item = (this.deReviseBizList || []).find((x) => x && String(x.id) === String(busId));
        applyReviseBizItemToRow(row, rt, item);
      } catch (e) {
        /* ignore */
      }
      this.persistDataEntryDraftToLocal(true);
      this.$emit("data-entry-change", { moduleId: this.viz.module_id, row });
    },
    cellClass(row, col) {
      if (!col || !col.required) return "";
      const v = row ? row[col.key] : undefined;
      if (v === null || v === undefined || String(v).trim() === "") {
        return "de-cell de-missing";
      }
      return "de-cell";
    },
    onCellChange(row, key, val) {
      // 保持 viz.rows 与当前编辑一致（viz 挂在 messageList 上，便于刷新后仍能看到编辑结果）
      try {
        if (this.viz && this.viz.rows && this.viz.rows[0]) {
          this.$set(this.viz.rows[0], key, val);
        }
      } catch (e) {
        /* ignore */
      }

      // 投放记录：实时联动（fishNum 无论走 input-number 还是 input，都统一走专用 handler）
      if (this.viz && this.viz.module_id === "zk_pond_fish_detail_add") {
        if (key === "fishNum") {
          return this.onStockingFishNumChange(row, val);
        }
        if (key === "pondWeight" || key === "fishSpec") {
          recomputeStockingDerivedFields(row, key);
        }
      }
      // 一旦用户开始编辑，就允许展示“必填未填”（避免刚出现就提示）
      if (!this.deInitializing && !this.deShowRequiredHint) this.deShowRequiredHint = true;
      // 投喂记录：实时联动（feedCount → feedingCost）
      if (this.viz && this.viz.module_id === "zk_feeding_log_add") {
        if (key === "feedCount") {
          if (!row.feedingPrice) {
            // 对齐 PC：允许先填投喂量，费用等字段在选择饲料（价格）后再计算
            this.persistDataEntryDraftToLocal();
            return;
          }
          recomputeFeedingDerivedFields(row, "feedCount");
        }
        if (key === "feedingPrice") {
          recomputeFeedingDerivedFields(row, "feedingPrice");
        }
      }
      // 修正记录：differenceData 改动时，联动 changeRatio = difference/origin（对齐 PC my_common.computer(type=3,fixed=2)）
      if (this.viz && this.viz.module_id === "zk_data_revise_add") {
        if (key === "reviseType") {
          // 用专用 handler，避免直接落草稿导致不拉下拉
          return this.onReviseTypeChange(row, val);
        }
        if (key === "differenceData" || key === "originData") {
          const cr = computeReviseChangeRatio(row.differenceData, row.originData);
          if (cr != null) this.$set(row, "changeRatio", cr);
        }
      }
      // 分池记录：主表联动（对齐 PC）
      if (this.viz && this.viz.module_id === "zk_pond_fish_parent_add") {
        // 操作类型变更：同步到子表每行（提交前也会兜底，但这里先保证界面一致）
        if (key === "addType") {
          try {
            if (Array.isArray(row.details)) {
              row.details.forEach((d) => {
                if (!d) return;
                d.addType = val;
              });
            }
          } catch (e) {
            /* ignore */
          }
        }
        // 原池数量变更（理论只读，但回显/兜底可能写入）：重新计算差距
        if (key === "fishNum") {
          try { this.onSplitDetailFishNumChange(); } catch (e) { /* ignore */ }
        }
      }
      this.persistDataEntryDraftToLocal();
    },
    async submitDataEntry() {
      if (!this.viz || this.viz.type !== "data_entry") return;
      if (this.isDataEntrySubmitted) {
        this.$message && this.$message.info("该表格已提交，无需重复提交");
        return;
      }
      // 用户点提交才触发必填提示（对齐 PC）
      this.deShowRequiredHint = true;
      if (this.missingRequiredLabels.length) {
        this.$message && this.$message.warning(`请先补齐必填：${this.missingRequiredLabels.join("、")}`);
        return;
      }
      if (this.submitting) return;
      this.submitting = true;
      try {
        // 投喂记录：提交前校验“仓库+饲料”库存存在且足够（对齐 Java judgeAddStock）
        if (this.viz && this.viz.module_id === "zk_feeding_log_add") {
          try {
            const r0 = (this.viz && this.viz.rows && this.viz.rows[0]) || {};
            const pid = r0.productId;
            const wid = r0.warehouseId;
            const need = r0.feedCount;
            if (pid && wid) {
              const stock = await getFeedingStockSnapshot(pid, wid);
              const cnt = stock && (stock.count != null ? Number(stock.count) : (stock.stockCount != null ? Number(stock.stockCount) : NaN));
              if (!stock) {
                this.$message && this.$message.error("当前仓库没有该饲料的库存记录，请更换仓库或饲料");
                return;
              }
              if (Number.isFinite(cnt) && need != null && Number(need) > cnt) {
                this.$message && this.$message.error(`仓库库存不足（有${cnt}，需要${Number(need)}）`);
                return;
              }
            }
          } catch (e) {
            // 校验接口失败不阻断编辑，但阻断提交，避免提交后端报错
            this.$message && this.$message.error("库存校验失败，请稍后重试或更换仓库/饲料");
            return;
          }
        }

        const buildPayload = () => {
          const p = {
            conversation_id: this.conversationId || "",
            session_id: this.viz.session_id,
            module_id: this.viz.module_id || "",
            rows: this.dataEntryRows || [],
            action: "create",
          };
          // Python /api/data-entry/submit 要求 draft_version 必填；校验 draft_checksum 仅在非空时才生效。
          // 由于前端允许用户编辑行数据，但不会同步更新 draft_checksum（否则需实现同算法 checksum），
          // 这里统一不传 draft_checksum，避免“本地改了值但 checksum 还是旧的”导致必然冲突。
          p.draft_version = this.viz.draft_version || 1;
          return p;
        };

        const trySubmitOnce = async () => {
          // 提交前兜底同步一次（避免任何恢复/联动导致的数据未写回 viz.rows）
          try {
            const r = (this.viz && this.viz.rows && this.viz.rows[0]) || null;
            if (r && this.viz && (this.viz.module_id === "zk_pond_fish_detail_add" || this.viz.module_id === "zk_pond_fish_detail_dead_add")) {
              // 强制补齐：对齐 PC 页面联动字段（避免因时序/去重导致 fishTypes/deptId/userName 等为空）
              try {
                if (r.batchNumber && (!r.supplier || !r.productNo || !r.supplierId)) {
                  const list = (this.deBatchList && this.deBatchList.length) ? this.deBatchList : await loadBatchOptionsForStocking();
                  if (list && list.length && (!this.deBatchList || !this.deBatchList.length)) this.deBatchList = list;
                  const bi = (list || []).find((x) => x && x.relationOrderNo === r.batchNumber);
                  applyStockingBatchToRow(r, bi);
                }
                if (r.pondId && !r.zkPondFishId) {
                  const list = (this.dePondList && this.dePondList.length) ? this.dePondList : await loadPondOptionsForStocking();
                  if (list && list.length && (!this.dePondList || !this.dePondList.length)) this.dePondList = list;
                  const pi = (list || []).find((x) => x && String(x.id) === String(r.pondId));
                  applyStockingPondToRow(r, pi);
                }
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list = (this.deUserList && this.deUserList.length) ? this.deUserList : await loadUserOptionsForStocking();
                  if (list && list.length && (!this.deUserList || !this.deUserList.length)) this.deUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyStockingUserToRow(r, ui);
                }
                if (r.productNo && (!r.fishTypes || !r.fishName || !r.fishSpec)) {
                  const list = (this.deProductList && this.deProductList.length) ? this.deProductList : await loadProductOptionsForStocking();
                  if (list && list.length && (!this.deProductList || !this.deProductList.length)) this.deProductList = list;
                  const pr = (list || []).find((x) => x && String(x.productNo) === String(r.productNo));
                  applyStockingProductToRow(r, pr);
                }
              } catch (e) {
                /* ignore */
              }
              recomputeStockingDerivedFields(r, "fishNum");
            }
            if (r && this.viz && this.viz.module_id === "zk_feeding_log_add") {
              // 强制补齐：对齐 PC 页面联动字段（避免 userName/deptId 为空落库）
              try {
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list =
                    (this.deFeedingUserList && this.deFeedingUserList.length)
                      ? this.deFeedingUserList
                      : await loadUserOptionsForFeeding();
                  if (list && list.length && (!this.deFeedingUserList || !this.deFeedingUserList.length)) this.deFeedingUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyFeedingUserToRow(r, ui);
                }
              } catch (e) {
                /* ignore */
              }
              recomputeFeedingDerivedFields(r, "feedCount");
            }
            if (r && this.viz && this.viz.module_id === "zk_use_medicine_add") {
              // 提交前强制补齐：存塘/执行人联动快照字段，避免落库为 NULL
              try {
                if (r.pondId) {
                  const list =
                    (this.deFeedingPondList && this.deFeedingPondList.length) ? this.deFeedingPondList : await loadPondOptionsForFeeding();
                  if (list && list.length && (!this.deFeedingPondList || !this.deFeedingPondList.length)) this.deFeedingPondList = list;
                  const pi = (list || []).find((x) => x && String(x.id) === String(r.pondId));
                  applyFeedingPondToRow(r, pi);
                }
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list =
                    (this.deFeedingUserList && this.deFeedingUserList.length) ? this.deFeedingUserList : await loadUserOptionsForFeeding();
                  if (list && list.length && (!this.deFeedingUserList || !this.deFeedingUserList.length)) this.deFeedingUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyFeedingUserToRow(r, ui);
                }
                // Java 侧会直接取 taskParam.processKey 发起 BPM；务必兜底
                if (!r.taskParam || typeof r.taskParam !== "object") {
                  this.$set(r, "taskParam", {
                    processKey: "apply_for_medication",
                    submitToPrev: "y",
                    finishProcess: "n",
                    variables: {},
                  });
                } else if (!r.taskParam.processKey) {
                  this.$set(r.taskParam, "processKey", "apply_for_medication");
                }
                // 子表：feedTime 统一为 yyyy-MM-dd HH:mm:ss（对齐 PC），避免后端解析异常
                if (Array.isArray(r.useMedicineLogList)) {
                  r.useMedicineLogList.forEach((it) => {
                    if (!it) return;
                    const ft = it.feedTime;
                    if (typeof ft === "number" && Number.isFinite(ft) && ft > 0) {
                      it.feedTime = this.formatDateTimeStr(ft);
                    } else if (typeof ft === "string") {
                      if (!ft.trim()) it.feedTime = this.formatDateTimeStr(Date.now());
                    } else if (ft == null) {
                      it.feedTime = this.formatDateTimeStr(Date.now());
                    }
                  });
                }
              } catch (e) {
                /* ignore */
              }
            }
            if (r && this.viz && this.viz.module_id === "zk_workshop_add") {
              // 提交前强制补齐：负责人联动快照字段（userName/deptId/tenantId），避免后端校验 userName 为空
              try {
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list =
                    (this.deUserList && this.deUserList.length)
                      ? this.deUserList
                      : await loadUserOptionsForStocking();
                  if (list && list.length && (!this.deUserList || !this.deUserList.length)) this.deUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyStockingUserToRow(r, ui);
                }
                if (r.deptId && !r.deptName) {
                  const dlist =
                    (this.deDeptList && this.deDeptList.length)
                      ? this.deDeptList
                      : await loadDeptOptionsSimple();
                  if (dlist && dlist.length && (!this.deDeptList || !this.deDeptList.length)) this.deDeptList = dlist;
                  const di = (dlist || []).find((x) => x && String(x.id) === String(r.deptId));
                  if (di && di.name) this.$set(r, "deptName", di.name);
                }
                if (!r.status) this.$set(r, "status", "2");
              } catch (e) {
                /* ignore */
              }
            }
            if (r && this.viz && this.viz.module_id === "zk_pond_system_add") {
              // 提交前强制补齐：workshopName / userName / deptName 等快照字段，避免后端校验或落库为 NULL
              try {
                if (r.workshopId && !r.workshopName) {
                  const wk =
                    (this.deWorkshopList && this.deWorkshopList.length)
                      ? this.deWorkshopList
                      : await loadWorkshopOptions();
                  if (wk && wk.length && (!this.deWorkshopList || !this.deWorkshopList.length)) this.deWorkshopList = wk;
                  const wi = (wk || []).find((x) => x && String(x.id) === String(r.workshopId));
                  applyWorkshopToRow(r, wi);
                }
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list =
                    (this.deUserList && this.deUserList.length)
                      ? this.deUserList
                      : await loadUserOptionsForStocking();
                  if (list && list.length && (!this.deUserList || !this.deUserList.length)) this.deUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyStockingUserToRow(r, ui);
                }
                if (r.deptId && !r.deptName) {
                  const dlist =
                    (this.deDeptList && this.deDeptList.length)
                      ? this.deDeptList
                      : await loadDeptOptionsSimple();
                  if (dlist && dlist.length && (!this.deDeptList || !this.deDeptList.length)) this.deDeptList = dlist;
                  const di = (dlist || []).find((x) => x && String(x.id) === String(r.deptId));
                  if (di && di.name) this.$set(r, "deptName", di.name);
                }
              } catch (e) {
                /* ignore */
              }
            }
            if (r && this.viz && this.viz.module_id === "zk_pond_add") {
              // 提交前强制补齐：系统联动字段（pondNum/workshop/pondDetailNum 前缀等）+ 负责人/部门快照字段
              try {
                if (r.pondSystemId) {
                  const ps =
                    (this.dePondSystemList && this.dePondSystemList.length)
                      ? this.dePondSystemList
                      : await loadPondSystemOptions();
                  if (ps && ps.length && (!this.dePondSystemList || !this.dePondSystemList.length)) this.dePondSystemList = ps;
                  const psi = (ps || []).find((x) => x && String(x.id) === String(r.pondSystemId));
                  applyPondSystemToPondRow(r, psi);
                }
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list =
                    (this.deUserList && this.deUserList.length)
                      ? this.deUserList
                      : await loadUserOptionsForStocking();
                  if (list && list.length && (!this.deUserList || !this.deUserList.length)) this.deUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyStockingUserToRow(r, ui);
                }
                if (r.deptId && !r.deptName) {
                  const dlist =
                    (this.deDeptList && this.deDeptList.length)
                      ? this.deDeptList
                      : await loadDeptOptionsSimple();
                  if (dlist && dlist.length && (!this.deDeptList || !this.deDeptList.length)) this.deDeptList = dlist;
                  const di = (dlist || []).find((x) => x && String(x.id) === String(r.deptId));
                  if (di && di.name) this.$set(r, "deptName", di.name);
                }
                if (!r.status) this.$set(r, "status", "1");
                if (r.pondScale == null || String(r.pondScale).trim() === "") this.$set(r, "pondScale", 20);
                if (r.fishNum == null || String(r.fishNum).trim() === "") this.$set(r, "fishNum", 0);
              } catch (e) {
                /* ignore */
              }
            }
            if (r && this.viz && this.viz.module_id === "zk_data_revise_add") {
              // 提交前强制补齐：修正记录也要带 deptId/tenantId/userName（后端落库字段不能为 null）
              try {
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list = (this.deUserList && this.deUserList.length) ? this.deUserList : await loadUserOptionsForStocking();
                  if (list && list.length && (!this.deUserList || !this.deUserList.length)) this.deUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyStockingUserToRow(r, ui);
                }
              } catch (e) {
                /* ignore */
              }
            }
            if (r && this.viz && this.viz.module_id === "zk_pond_fish_parent_add") {
              // 提交前强制补齐：分池父表 deptId/userName 以及子表 details 的 userId/deptId/batchNumber/pondId 等
              try {
                if (r.userId && (!r.userName || !r.deptId || !r.tenantId)) {
                  const list = (this.deUserList && this.deUserList.length) ? this.deUserList : await loadUserOptionsForStocking();
                  if (list && list.length && (!this.deUserList || !this.deUserList.length)) this.deUserList = list;
                  const ui = (list || []).find((x) => x && String(x.id) === String(r.userId));
                  applyStockingUserToRow(r, ui);
                }
                // 原池联动兜底：补齐 originPondDetailNum + 原池数量 fishNum
                if (r.originPondId && (!r.originPondDetailNum || r.fishNum == null || String(r.fishNum).trim() === "")) {
                  const p = (this.dePondList && this.dePondList.length) ? this.dePondList : await loadPondOptionsForStocking();
                  if (p && p.length && (!this.dePondList || !this.dePondList.length)) this.dePondList = p;
                  const pi = (p || []).find((x) => x && String(x.id) === String(r.originPondId));
                  if (pi) {
                    if (!r.originPondDetailNum) this.$set(r, "originPondDetailNum", pi.pondDetailNum);
                    if (r.fishNum == null || String(r.fishNum).trim() === "") this.$set(r, "fishNum", parseInt(String(pi.fishNum), 10) || 0);
                  }
                }
                // 批次/鱼苗联动兜底
                if (r.batchNumber && (!r.productNo || !r.fishSpec)) {
                  const b = (this.deBatchList && this.deBatchList.length) ? this.deBatchList : await loadBatchOptionsForStocking();
                  if (b && b.length && (!this.deBatchList || !this.deBatchList.length)) this.deBatchList = b;
                  const bi = (b || []).find((x) => x && x.relationOrderNo === r.batchNumber);
                  applyStockingBatchToRow(r, bi);
                }
                if (Array.isArray(r.details)) {
                  r.details.forEach((d) => {
                    if (!d) return;
                    if (d.batchNumber == null) d.batchNumber = r.batchNumber;
                    if (d.pondId == null) d.pondId = r.originPondId;
                    if (d.productNo == null) d.productNo = r.productNo;
                    if (d.fishName == null) d.fishName = r.fishName;
                    if (d.fishTypes == null) d.fishTypes = r.fishTypes;
                    if (d.addType == null) d.addType = r.addType || "-3";
                    if (d.userId == null) d.userId = r.userId;
                    if (d.deptId == null) d.deptId = r.deptId;
                  });
                }
                // 重新计算 fishNumDiff
                try { this.onSplitDetailFishNumChange(); } catch (e) { /* ignore */ }
              } catch (e) {
                /* ignore */
              }
            }
          } catch (e) {
            /* ignore */
          }
          const resp = await dataEntrySubmit(buildPayload());
          return (resp && resp.data) || {};
        };

        let data = await trySubmitOnce();
        // session 丢失：后端可能会重建 session 并返回新的 session_id/draft_version，自动对齐后再试一次
        if (data && data.error === "session_not_found" && data.session_id && data.session_id !== this.viz.session_id) {
          this.$set(this.viz, "session_id", data.session_id);
          if (data.draft_version) this.$set(this.viz, "draft_version", data.draft_version);
          data = await trySubmitOnce();
        }
        // 冲突：Python 端会返回当前 draft_version/draft_checksum（我们不传 checksum 但仍可记录），对齐后自动重试一次
        if (data && data.error === "conflict" && data.draft_version) {
          this.$set(this.viz, "draft_version", data.draft_version);
          if (data.draft_checksum) this.$set(this.viz, "draft_checksum", data.draft_checksum);
          data = await trySubmitOnce();
        }
        const ok = !!data.ok;
        if (data.draft_version) this.$set(this.viz, "draft_version", data.draft_version);
        if (data.draft_checksum) this.$set(this.viz, "draft_checksum", data.draft_checksum);

        if (ok) {
          this.dataEntrySubmitted = true;
          this.$set(this.viz, "_dataEntrySubmitted", true);
          this.$message && this.$message.success("提交成功");
          this.persistDataEntryDraftToLocal(true);
        } else if (data.error === "conflict") {
          this.$message && this.$message.warning("提交冲突：表格已被更新，请稍后重试");
        } else {
          // Java 业务失败时，优先展示 CommonResult.msg，避免只看到 java_biz_error
          const jmsg =
            (data && data.java_msg) ||
            (data && data.java_body && (data.java_body.msg || data.java_body.message)) ||
            "";
          const hint = jmsg ? `${data.error || "failed"}（${jmsg}）` : (data.error || "unknown");
          this.$message && this.$message.error(`提交失败：${hint}`);
        }
      } catch (e) {
        this.$message && this.$message.error(`提交异常：${e && e.message ? e.message : e}`);
      } finally {
        this.submitting = false;
      }
    },
    labelFor(c) {
      const m = { table: "表格", bar: "柱状", line: "折线", pie: "饼图" };
      return m[c] || c;
    },
    fieldLabel(field) {
      const f = String(field || "");
      if (this.tableColumnLabels && this.tableColumnLabels[f]) return this.tableColumnLabels[f];
      // 仅保留“schema 无法覆盖的语义别名/计算列”兜底翻译
      const aliasMap = {
        device_type: "设备类型",
        device_status: "设备状态",
        device_count: "设备数量",
        active_count: "启用数量",
        inactive_count: "停用数量",
        discarded_count: "废弃数量",
        metric_type: "指标类型",
        value: "数值",
        "指标": "指标",
        "数值": "数值",
      };
      if (aliasMap[f]) return aliasMap[f];
      return f;
    },
    syncDefaultChart() {
      const d = (this.viz && this.viz.default_chart) || "table";
      const allowed = this.chartChoices;
      this.chartType = allowed.indexOf(d) >= 0 ? d : allowed[0] || "table";
    },
    parseNum(v) {
      if (v === null || v === undefined) return NaN;
      if (typeof v === "number") return v;
      const s = String(v).replace(/,/g, "").trim();
      const n = parseFloat(s);
      return Number.isFinite(n) ? n : NaN;
    },
    buildOption() {
      const viz = this.viz;
      if (!viz || !viz.table) return null;
      const rows = this.tableRows;
      const keys = viz.keys || {};
      const catKey = keys.category || keys.time;
      const valKeys = keys.values || [];
      const isHoriz = keys.horizontal && this.chartType === "bar";

      if (this.chartType === "pie" && catKey && valKeys.length === 1) {
        const vk = valKeys[0];
        const data = rows
          .map((r) => ({
            name: String(r[catKey] != null ? r[catKey] : ""),
            value: this.parseNum(r[vk]),
          }))
          .filter((d) => Number.isFinite(d.value));
        return {
          tooltip: { trigger: "item" },
          series: [
            {
              type: "pie",
              radius: ["28%", "62%"],
              data,
              label: { color: "#c8e6d9" },
            },
          ],
        };
      }

      if (this.chartType === "line" && catKey && valKeys.length) {
        const x = rows.map((r) => String(r[catKey] != null ? r[catKey] : ""));
        const series = valKeys.map((vk) => ({
          name: this.fieldLabel(vk),
          type: "line",
          smooth: true,
          data: rows.map((r) => this.parseNum(r[vk])),
        }));
        return {
          tooltip: { trigger: "axis" },
          legend: { data: series.map((s) => s.name), textStyle: { color: "#a8c9b8" } },
          grid: { left: 48, right: 24, bottom: 40, top: 36 },
          xAxis: { type: "category", data: x, axisLabel: { color: "#8aa899", rotate: x.length > 8 ? 35 : 0 } },
          yAxis: { type: "value", axisLabel: { color: "#8aa899" }, splitLine: { lineStyle: { color: "rgba(255,255,255,0.06)" } } },
          series,
        };
      }

      if (this.chartType === "bar" && catKey && valKeys.length) {
        const cats = rows.map((r) => String(r[catKey] != null ? r[catKey] : ""));
        const series = valKeys.map((vk) => ({
          name: this.fieldLabel(vk),
          type: "bar",
          data: rows.map((r) => this.parseNum(r[vk])),
        }));
        const base = {
          tooltip: { trigger: "axis" },
          legend: { data: series.map((s) => s.name), textStyle: { color: "#a8c9b8" } },
          series,
        };
        if (isHoriz) {
          return {
            ...base,
            grid: { left: 100, right: 24, bottom: 28, top: 28 },
            xAxis: { type: "value", axisLabel: { color: "#8aa899" }, splitLine: { lineStyle: { color: "rgba(255,255,255,0.06)" } } },
            yAxis: { type: "category", data: cats, axisLabel: { color: "#8aa899" } },
          };
        }
        return {
          ...base,
          grid: { left: 48, right: 24, bottom: cats.length > 10 ? 80 : 40, top: 36 },
          xAxis: {
            type: "category",
            data: cats,
            axisLabel: { color: "#8aa899", rotate: cats.length > 6 ? 35 : 0 },
          },
          yAxis: { type: "value", axisLabel: { color: "#8aa899" }, splitLine: { lineStyle: { color: "rgba(255,255,255,0.06)" } } },
        };
      }

      return null;
    },
    renderChart() {
      if (this.chartType === "table") {
        if (this.chartInstance) {
          this.chartInstance.dispose();
          this.chartInstance = null;
        }
        return;
      }
      const opt = this.buildOption();
      if (!this.$refs.chartEl) return;
      if (!this.chartInstance) {
        this.chartInstance = echarts.init(this.$refs.chartEl, null, { renderer: "canvas" });
      }
      if (!opt) {
        this.chartInstance.clear();
        return;
      }
      this.chartInstance.setOption(opt, true);
      this.$nextTick(() => {
        if (this.chartInstance) this.chartInstance.resize();
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.chat-bi-panel {
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(0, 40, 30, 0.35);
  border: 1px solid rgba(76, 145, 101, 0.35);
}
.bi-toolbar {
  margin-bottom: 8px;
}
.bi-chart {
  width: 100%;
  height: 260px;
}
.bi-table {
  width: 100%;
  background: transparent;
}
.bi-trunc-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #7a9a8a;
}

.data-entry-panel .de-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.data-entry-panel .de-title .left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.data-entry-panel .de-title .tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  color: #c8e6d9;
  background: rgba(76, 145, 101, 0.18);
  border: 1px solid rgba(76, 145, 101, 0.35);
}
.data-entry-panel .de-title .name {
  font-weight: 600;
  color: #d8efe4;
}
.submitted-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  color: #d8efe4;
  background: rgba(64, 158, 255, 0.12);
  border: 1px solid rgba(64, 158, 255, 0.28);
}
.de-cell.de-missing {
  border: 1px solid rgba(245, 108, 108, 0.65);
  border-radius: 6px;
  padding: 2px;
}
.de-readonly-text {
  width: 100%;
  min-height: 28px;
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px dashed rgba(200, 230, 217, 0.25);
  color: #d8efe4;
  white-space: normal;
  word-break: break-all;
  line-height: 1.25;
}
.de-hint {
  margin-top: 8px;
  font-size: 12px;
  color: rgba(245, 108, 108, 0.9);
}
</style>
