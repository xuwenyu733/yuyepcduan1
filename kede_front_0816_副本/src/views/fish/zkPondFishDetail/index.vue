<template>
  <!-- 1. 投放记录 -->
  <div class="app-container">
    <!-- 1. 投放记录 -->
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="存塘鱼苗表id" prop="zkPondFishId">
        <el-input v-model="queryParams.zkPondFishId" placeholder="请输入存塘鱼苗表id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="批次编号" prop="batchNumber">
        <el-select style="width:100%;" v-model="queryParams.batchNumber" placeholder="请选择批次编号" filterable clearable  @change="handleQuery">
          <el-option v-for="(item, index) in bachchNumberList" :key="item.id+'__'+index" :label="`${item.info}`"
            :value="item.relationOrderNo"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="系统编号" prop="pondId">
        <el-select style="width:100%;" v-model="queryParams.pondId" placeholder="请选择存塘系统编号" filterable clearable @change="handleQuery">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="供应商" prop="supplier">
        <el-input v-model="queryParams.supplier" placeholder="请输入供应商" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
        <el-form-item label="存塘类型" prop="pondType">
          <el-select style="width:100%;" v-model="queryParams.pondType" placeholder="请选择存塘类型" filterable clearable  @change="handleQuery">
            <el-option v-for="(item, index) in pondTypeList" :key="item.value+'_pt_'+index" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-select v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" filterable clearable size="small" style="width: 100%;" @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="投放类型" prop="addType">
        <el-select v-model="queryParams.addType" placeholder="请输入投放类型" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="投放人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入投放人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="鱼苗数量" prop="fishNum">
        <el-input v-model="queryParams.fishNum" placeholder="请输入鱼苗数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="投放类型(-2:损失;-1:卖出;1:投放;)" prop="addType">
        <el-select v-model="queryParams.addType" placeholder="请选择投放类型(-2:损失;-1:卖出;1:投放;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="起始时间" end-placeholder="截至时间" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-pond-fish-detail:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-pond-fish-detail:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="批次编号" align="center" prop="batchNumber" width="180" />
      <el-table-column label="供应商" align="center" prop="supplier" min-width="120" show-overflow-tooltip />
      <el-table-column label="供应商地址" align="center" prop="supplierAddress" min-width="140" show-overflow-tooltip />
      <el-table-column label="投放鱼池" align="center" min-width="160" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ formatStockPondLabel(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="pondType" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ judgePondType(scope.row.zkPondFishDO) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="投放品种" align="center" prop="fishTypes" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_POND_FISH_TYPES" :value="scope.row.fishTypes" />
        </template>
      </el-table-column>
      <el-table-column label="投放规格(g/尾)" align="center" prop="fishSpec" />
      <el-table-column label="投放数量" align="center" prop="fishNum" />
      <el-table-column label="投放重量(kg)" align="center" prop="pondWeight" />
      <el-table-column label="投放后7日死亡量" align="center" prop="sevenDayMortalityRate" width="130" />
      <el-table-column label="投放时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-pond-fish-detail:update']">修正</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-pond-fish-detail:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修正) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="批次编号" prop="batchNumber">
          <el-select style="width:100%;" v-model="form.batchNumber" placeholder="请选择苗种采购批次(自动带入供应商/品种/规格)" filterable clearable
            @change="updateBatchNumerId">
            <el-option v-for="(item, index) in bachchNumberList" :key="item.id+'__'+index" :label="`${item.info}`"
              :value="item.relationOrderNo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <div class="manual-row">
            <el-input v-model="form.supplier" placeholder="关联苗种采购自动带入，可人工修正" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='supplier'"
              @click="startVoiceField('supplier', false)">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="供应商地址" prop="supplierAddress">
          <div class="manual-row">
            <el-input v-model="form.supplierAddress" placeholder="关联苗种采购自动带入，可人工修正" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='supplierAddress'"
              @click="startVoiceField('supplierAddress', false)">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="投放方式" prop="stockMode">
          <el-radio-group v-model="stockMode" @change="onStockModeChange">
            <el-radio label="all">整批同池(采购数量自动带入)</el-radio>
            <el-radio label="split">分池投放(人工修正重量/数量)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="投放鱼池" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择投放鱼池(人工选择)" filterable clearable
            @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index"
              :label="`${formatStockPondLabel(item)}（${item.pondScale != null ? item.pondScale + 'm³' : '-'}，${item.fishNum || 0}条）`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投放品种" prop="productNo">
          <el-select v-model="form.productNo" placeholder="关联苗种采购自动带入" filterable clearable size="small"
            style="width: 100%;" @change="updateProductId">
            <el-option v-for="dict in productList" :key="dict.productNo"
              :label="dict.info" :value="dict.productNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="投放规格(g/尾)" prop="fishSpec">
          <div class="manual-row">
            <el-input v-model="form.fishSpec" placeholder="关联苗种采购自动带入(g/尾)" @change="recalcBySpec" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='fishSpec'"
              @click="startVoiceField('fishSpec', true)">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="投放数量" prop="fishNum">
          <div class="manual-row">
            <el-input type="number" v-model="form.fishNum"
              placeholder="可按库存修正投放数量" @input="onFishNumInput" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='fishNum'"
              @click="startVoiceField('fishNum', true)">语音</el-button>
          </div>
          <div class="tip-text" v-if="form.remainingInventory != null">待投放库存：{{ form.remainingInventory }} 尾</div>
        </el-form-item>
        <el-form-item label="投放重量(kg)" prop="pondWeight">
          <div class="manual-row">
            <el-input type="number" v-model="form.pondWeight"
              :placeholder="stockMode==='split' ? '分池投放请录入重量，数量=重量/规格' : '数量×规格自动计算'"
              @input="onPondWeightInput" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='pondWeight'"
              @click="startVoiceField('pondWeight', true)">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="投放后存塘鱼数" prop="currFishNum">
          <el-input v-model="form.currFishNum" placeholder="投放后存塘鱼数(自动)" disabled />
        </el-form-item>
        <el-form-item label="投放后7日死亡量" prop="sevenDayMortalityRate">
          <el-input v-model="form.sevenDayMortalityRate" placeholder="关联死亡记录，按投放时间起7日内自动汇总" disabled />
        </el-form-item>
        <el-form-item label="投放时间" prop="createTime">
          <el-date-picker clearable v-model="form.createTime" style="width: 100%;" type="datetime"
            value-format="timestamp" placeholder="默认取苗种收货/入库时间" @change="scheduleCalcSevenDay" />
        </el-form-item>
        <el-form-item label="投放人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择投放人" filterable clearable
            @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单据附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_pond_fish_stock"></FileBusUpload>
          <div class="tip-text">可上传收货单据照片辅助录入</div>
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
  </div>
</template>

<script>
import { createZkPondFishDetail, updateZkPondFishDetail, deleteZkPondFishDetail, getZkPondFishDetail, getZkPondFishDetailPage, exportZkPondFishDetailExcel } from "@/api/fish/zkPondFishDetail";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadDeptScopedPondList } from "@/utils/siteOptions";
import { notifyProductionRecordChanged } from "@/utils/productionRecordBridge";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import {
  getProductPage
} from "@/api/erp/product";
import { listForBatch } from "@/api/erp/purchaseIn";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkPondFishDetail",
  components: {
    FileBusUpload,
  },
  data() {
    return {
      operationLoading: false,
      userInfo: {},
      pondTypeList: [
        {label:'单一', value: '1'},
        {label:'混合', value: '2'},
        {label:'全部', value: '3'}
      ],
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 存塘鱼苗明细列表
      list: [],
      pondList: [],
      productList: [],
      bachchNumberList: [],
      bachchNumberListAll: [],
      userList1: [],
      dict_flushTypes: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        zkPondFishId: null,
        workshopId: null,
        pondName: null,
        pondId: null,
        pondType: null,
        fishTypes: null,
        fishName: null,
        fishNum: null,
        addType: "1", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
        batchNumber: null,
        supplier: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        zkPondFishId: [{ required: true, message: "存塘鱼苗表id不能为空", trigger: "blur" }],
        fishNum: [{ required: true, message: "鱼苗数量不能为空", trigger: "blur" }],
        batchNumber: [{ required: true, message: "批次编号不能为空", trigger: "blur" }],
        pondId: [{ required: true, message: "鱼池编号不能为空", trigger: "blur" }],
        productNo: [{ required: true, message: "鱼苗种类不能为空", trigger: "blur" }],
        fishNum: [{ required: true, message: "鱼苗数量不能为空", trigger: "blur" }],
        pondWeight: [{ required: true, message: "投放重量不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "用户不能为空", trigger: "blur" }],
        currFishNum: [{ required: true, message: "投放后存塘鱼数不能为空", trigger: "blur" }],
        fishSpec: [{ required: true, message: "投放鱼苗规格不能为空", trigger: "blur" }],
        createTime: [{ required: true, message: "时间不能为空", trigger: "blur" }],
        // reason: [{ required: true, message: "修正原因不能为空", trigger: "blur" }],
      },
      editType: 1, // 1:添加;2:修正;
      // all:整批同池; split:分池投放
      stockMode: 'all',
      syncingQtyWeight: false,
      voiceField: '',
      speechRecognition: null,
      calcSevenTimer: null,
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    const batchNumber = this.$route.query && this.$route.query.batchNumber;
    const workshopId = this.$route.query && this.$route.query.workshopId;
    const pondNum = this.$route.query && this.$route.query.pondNum;
    const pondId = this.$route.query && this.$route.query.pondId;
    if(batchNumber) this.queryParams.batchNumber = batchNumber;
    if(workshopId) this.queryParams.workshopId = workshopId;
    if(pondNum) this.queryParams.pondNum = pondNum;
    if(pondId) this.queryParams.pondId = pondId;
    this.getList();
    this.getPondList();
    this.getUserList();
    this.getProductList();
    this.getBatchNumerList();
    this.dict_flushTypes = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES);
  },
  beforeDestroy() {
    this.stopVoice()
    if (this.calcSevenTimer) clearTimeout(this.calcSevenTimer)
  },
  watch: {
    'form.fishNum': {
      handler(newV) {
        if (this.syncingQtyWeight) return
        if (!newV && newV !== 0) return
        if (this.form.pondId) {
          const hist = parseInt(this.form.historyFishNum) || 0
          this.form.currFishNum = hist + (parseInt(newV) || 0)
          if (this.editType == 2) {
            this.form.differenceData = newV - this.form.originData
            this.form.changeRatio = this.my_common.computer(this.form.differenceData, this.form.originData, 3, 3)
          }
        } else if (this.editType != 2 && newV) {
          this.$modal.msgWarning("请先选择投放鱼池！")
          this.syncingQtyWeight = true
          this.form.fishNum = 0
          this.$nextTick(() => { this.syncingQtyWeight = false })
        }
      }
    },
  },
  methods: {
    pad2(n) { return n < 10 ? '0' + n : '' + n },
    fmtDateTime(ts) {
      const d = new Date(Number(ts))
      return `${d.getFullYear()}-${this.pad2(d.getMonth()+1)}-${this.pad2(d.getDate())} ${this.pad2(d.getHours())}:${this.pad2(d.getMinutes())}:${this.pad2(d.getSeconds())}`
    },
    parseSpecNum(val) {
      if (val == null || val === '') return null
      if (typeof val === 'number' && !isNaN(val)) return val
      const m = String(val).match(/(\d+(?:\.\d+)?)/)
      return m ? Number(m[1]) : null
    },
    /** 数量 → 重量(kg) = 数量 × 规格(g) / 1000 */
    calcWeightFromNum() {
      const num = Number(this.form.fishNum)
      const spec = this.parseSpecNum(this.form.fishSpec)
      if (!Number.isFinite(num) || !spec) return
      this.syncingQtyWeight = true
      this.form.pondWeight = Number(((num * spec) / 1000).toFixed(3))
      this.$nextTick(() => { this.syncingQtyWeight = false })
    },
    /** 重量 → 数量 = 重量(kg)×1000 / 规格(g) */
    calcNumFromWeight() {
      const weight = Number(this.form.pondWeight)
      const spec = this.parseSpecNum(this.form.fishSpec)
      if (!Number.isFinite(weight) || !spec || spec <= 0) return
      this.syncingQtyWeight = true
      this.form.fishNum = Math.round((weight * 1000) / spec)
      this.$nextTick(() => { this.syncingQtyWeight = false })
      if (this.form.pondId) {
        const hist = parseInt(this.form.historyFishNum) || 0
        this.form.currFishNum = hist + (parseInt(this.form.fishNum) || 0)
      }
    },
    onFishNumInput() {
      if (this.syncingQtyWeight) return
      this.calcWeightFromNum()
    },
    onPondWeightInput() {
      if (this.syncingQtyWeight) return
      // 分池/修正/改重量：以重量反推数量；整批同池改数量则重量由数量算出
      this.calcNumFromWeight()
    },
    recalcBySpec() {
      if (this.stockMode === 'split' && this.form.pondWeight) {
        this.calcNumFromWeight()
      } else if (this.form.fishNum) {
        this.calcWeightFromNum()
      }
    },
    onStockModeChange() {
      if (this.stockMode === 'all' && this.form.remainingInventory != null) {
        this.syncingQtyWeight = true
        this.form.fishNum = this.form.remainingInventory
        this.$nextTick(() => {
          this.syncingQtyWeight = false
          this.calcWeightFromNum()
        })
      }
    },
    applyBatchPurchaseDefaults(item, opts = {}) {
      if (!item) return
      const preserve = !!opts.preserveExisting
      if (!preserve || !this.form.supplier) this.form.supplier = item.supplierName
      if (!preserve || !this.form.supplierAddress) this.form.supplierAddress = item.supplierAddress
      this.form.supplierId = item.supplierId
      if (!preserve || !this.form.productNo) this.form.productNo = item.productNo
      const purchaseSpec = item.productSpec || item.standard
      if (!preserve || !this.form.fishSpec) {
        this.form.fishSpec = purchaseSpec || this.form.fishSpec
      }
      this.updateProductId(this.form.productNo)
      if (purchaseSpec && !preserve) this.form.fishSpec = purchaseSpec
      const remain = item.remainingInventory == -1 ? item.productCount : item.remainingInventory
      this.form.remainingInventory = remain
      // 投放时间：苗种收货/入库时间（新增时自动带入）
      const receiveTs = item.inTime || item.receiveTime || item.createTime
      if (receiveTs && (!preserve || !this.form.createTime)) {
        this.form.createTime = Number(receiveTs)
      }
      if (!preserve && this.stockMode === 'all' && remain != null) {
        this.syncingQtyWeight = true
        this.form.fishNum = remain
        this.$nextTick(() => {
          this.syncingQtyWeight = false
          this.calcWeightFromNum()
        })
      }
      this.scheduleCalcSevenDay()
    },
    updateBatchNumerId(currId, opts){
      let item = null;
      const pools = (this.bachchNumberListAll && this.bachchNumberListAll.length)
        ? this.bachchNumberListAll
        : this.bachchNumberList;
      pools.forEach(obj => {
        if(obj.relationOrderNo === currId) {
          item = obj;
        }
      })
      // 列表展示可能去掉了 CGRK/BZ 前缀，修正时兼容匹配
      if (!item && currId) {
        const raw = String(currId)
        pools.forEach(obj => {
          const no = String(obj.relationOrderNo || '')
          const stripped = no.replaceAll('BZ', '').replaceAll('CGRK', '')
          if (no === raw || stripped === raw || no.endsWith(raw)) item = obj
        })
      }
      if(!item) return;
      this.applyBatchPurchaseDefaults(item, opts || {});
    },
    scheduleCalcSevenDay() {
      if (this.calcSevenTimer) clearTimeout(this.calcSevenTimer)
      this.calcSevenTimer = setTimeout(() => this.calcSevenDayMortality(), 400)
    },
    /** 投放后7日死亡量：同池、投放时间起7日内死亡合计 */
    async calcSevenDayMortality(row) {
      const target = row || this.form
      const pondId = target.pondId
      const startTs = Number(target.createTime)
      if (!pondId || !Number.isFinite(startTs) || startTs <= 0) {
        if (!row) this.$set(this.form, 'sevenDayMortalityRate', 0)
        return 0
      }
      const endTs = startTs + 7 * 24 * 60 * 60 * 1000
      try {
        const res = await getZkPondFishDetailPage({
          pageNo: 1,
          pageSize: 200,
          pondId,
          addType: '-2',
          createTime: [this.fmtDateTime(startTs), this.fmtDateTime(endTs)]
        })
        let list = (res.data && res.data.list) || []
        list = list.filter(r => {
          const t = Number(r.createTime)
          return Number.isFinite(t) && t >= startTs && t <= endTs
        })
        const sum = list.reduce((s, r) => s + (Number(r.fishNum) || 0), 0)
        if (row) {
          this.$set(row, 'sevenDayMortalityRate', sum)
        } else {
          this.$set(this.form, 'sevenDayMortalityRate', sum)
        }
        return sum
      } catch (e) {
        console.warn('calcSevenDayMortality failed', e)
        return 0
      }
    },
    async enrichListSevenDay(list) {
      if (!list || !list.length) return
      await Promise.all(list.map(row => this.calcSevenDayMortality(row)))
    },
    startVoiceField(field, numeric) {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
      if (!SpeechRecognition) {
        this.$modal.msgWarning('当前浏览器不支持语音录入')
        return
      }
      this.stopVoice()
      const recognition = new SpeechRecognition()
      recognition.lang = 'zh-CN'
      recognition.interimResults = false
      recognition.maxAlternatives = 1
      this.speechRecognition = recognition
      this.voiceField = field
      recognition.onresult = (event) => {
        const transcript = (event.results[0] && event.results[0][0] && event.results[0][0].transcript) || ''
        if (numeric) {
          const match = String(transcript).match(/(\d+(\.\d+)?)/)
          if (match) {
            this.$set(this.form, field, match[1])
            if (field === 'fishNum') this.onFishNumInput()
            if (field === 'pondWeight') this.onPondWeightInput()
            if (field === 'fishSpec') this.recalcBySpec()
            this.$modal.msgSuccess(`语音识别：${match[1]}`)
          } else {
            this.$modal.msgWarning(`未识别到数字，原文：${transcript || '空'}`)
          }
        } else {
          this.$set(this.form, field, transcript)
          this.$modal.msgSuccess(`语音识别：${transcript}`)
        }
      }
      recognition.onerror = () => { this.voiceField = '' }
      recognition.onend = () => { this.voiceField = '' }
      recognition.start()
    },
    stopVoice() {
      if (this.speechRecognition) {
        try { this.speechRecognition.stop() } catch (e) {}
        this.speechRecognition = null
      }
      this.voiceField = ''
    },
    /** 鱼池命名：车间-系统-池号 */
    formatStockPondLabel(row) {
      if (!row) return '-';
      let workshop = row.workshopName || '';
      let system = row.pondSystemName || row.pondNum || '';
      let pond = row.pondDetailNum || row.pondName || '';
      // 列表字段不全时，用存塘缓存补全
      if ((!workshop || !system) && row.pondId != null && this.pondList && this.pondList.length) {
        const hit = this.pondList.find(p => String(p.id) === String(row.pondId));
        if (hit) {
          workshop = workshop || hit.workshopName || '';
          system = system || hit.pondSystemName || hit.pondNum || '';
          pond = pond || hit.pondDetailNum || hit.pondName || '';
        }
      }
      const parts = [workshop, system, pond].map(s => String(s || '').trim()).filter(Boolean);
      return parts.length ? parts.join('-') : '-';
    },
    judgePondType(zkPondFish) {
      if(!zkPondFish) return '单一';
      if(zkPondFish.batchNumber && zkPondFish.batchNumber.split(",").length >1) return "混合";
      return '单一';
    },
    getBatchNumerList() {
      let queryParams = {
        pageNo: 1,
        pageSize: 500,
        orderNo: null,
        outKind: '1',
        productName: null,
        userId: null,
        categoryId: "7",
        createTime: [],
        oType: 1, // 订单类型(0:出库单;1:入库单;)
      };
      listForBatch(queryParams).then(response => {
        let list = response.data || [];
        list.forEach(item => {
          if(item.remainingInventory === -1) {
            item.remainingInventory = item.productCount;
          }
          // 兼容收货时间字段
          if (!item.inTime && item.receiveTime) item.inTime = item.receiveTime
          item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName}-待投放${item.remainingInventory}条)`;
        })
        // 新增只展示仍有待投放库存的批次；修正时按完整列表匹配
        this.bachchNumberListAll = list;
        this.bachchNumberList = list.filter(obj => obj.remainingInventory > 0);
      });
    },
    updateProductId(currId){
      let item = null;
      this.productList.forEach(obj => {
        if(obj.productNo === currId) {
          item = obj;
        }
      })
      if(!item) return;
      this.form.fishName = item.productName;
      this.form.fishTypes = item.fishTypes;
      // 若采购规格已带入则不覆盖
      if (!this.form.fishSpec) {
        this.form.fishSpec = item.standard || item.productSpec;
      }
      this.form.productNo = item.productNo;
    },
    getProductList() {
      this.productList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
				categoryId: 7,
      }
      getProductPage(params).then(response => {
        this.productList.push(...response.data.list)
        this.productList.forEach(obj => {
          let tempStr = `${obj.productName}-${obj.productBrand}`;
          obj.info = tempStr;
        })
      })
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      getZkPondFishDetailPage(this.queryParams).then(async response => {
        this.list = response.data.list || [];
        if(this.list) {
          this.list.forEach(obj => {
            obj.batchNumber = obj.batchNumber?obj.batchNumber.replaceAll("BZ", "").replaceAll("CGRK", ""):'';
          })
        }
        this.total = response.data.total;
        this.loading = false;
        // 关联死亡记录刷新7日死亡量
        this.enrichListSevenDay(this.list)
      }).catch(() => { this.loading = false });
    },
    updateFlushTypes(currType) {
      let item = null;
      this.dict_flushTypes.forEach(obj => {
        if(obj.value == currType) {
          item = obj;
          return;
        }
      })
      this.form.fishName = item.label;
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      if (!item) return
      this.form.tenantId = item.tenantId;
      this.form.userId = item.id;
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
    },
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          let tempStr = `${obj.nickname}`;
          obj.nickname = tempStr;
        })
      })
    },
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      if (!item) return
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.historyFishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      const hist = parseInt(this.form.historyFishNum) || 0
      this.form.currFishNum = hist + (parseInt(this.form.fishNum) || 0)
      this.scheduleCalcSevenDay()
    },
    getPondList() {
      this.pondList = []
      return loadDeptScopedPondList().then(list => {
        this.pondList = Array.isArray(list) ? list.slice() : []
      }).catch(() => {
        this.pondList = []
      })
    },
    /** 表单重置 */
    reset() {
      this.stockMode = 'all'
      this.form = {
        id: undefined,
        zkPondFishId: undefined,
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        fishTypes: undefined,
        fishNum: undefined,
        addType: "1",
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        createTime: new Date().getTime(),
        pondScale: undefined,
        historyFishNum: undefined,
        currFishNum: undefined,
        fishSpec: '',
        salePrice: undefined,
        pondWeight: undefined,
        saleIncome: undefined,
        saleCost: undefined,
        targetPondNum: undefined,
        targetPondSpec: undefined,
        targetPondId: undefined,
        targetPondName: undefined,
        targetHistoryFishNum: undefined,
        targetCurrFishNum: undefined,
        remark: undefined,
        supplier: undefined,
        supplierAddress: undefined,
        supplierId: undefined,
        remainingInventory: undefined,
        sevenDayMortalityRate: 0,
        productNo: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作：先开弹窗再异步拉鱼池，避免等全量鱼池列表拖慢打开 */
    handleAdd() {
      this.editType = 1;
      this.stockMode = 'all'
      this.reset();
      this.updateUserId(parseInt(this.userInfo.userId));
      this.open = true;
      this.title = "添加投放记录";
      this.form.fid = getUUID().replaceAll('-', '');
      this.getPondList().catch(() => {});
    },
    /** 修正按钮操作 */
    handleUpdate(row) {
      this.editType = 2;
      this.stockMode = 'split'
      this.reset();
      const id = row.id;
      getZkPondFishDetail(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修正投放记录";
        this.form.originData = this.form.fishNum;
        if(this.form.batchNumber) {
          this.updateBatchNumerId(this.form.batchNumber, { preserveExisting: true });
        }
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
        this.scheduleCalcSevenDay()
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        if(this.editType == 2 && !this.form.reason && this.form.differenceData) {
          this.$modal.msgWarning(`修正原因不能为空`);
          return;
        }
        if(this.editType == 1 && this.form.remainingInventory < this.form.fishNum) {
          this.$modal.msgWarning(`当前批次剩余鱼苗数${this.form.remainingInventory}小于${this.form.fishNum}`);
          this.form.fishNum = this.form.remainingInventory;
          return;
        }
        if(this.editType == 2 && this.form.differenceData > 0) {
          if(!this.form.remainingInventory) {
            this.$modal.msgWarning(`当前批次剩余鱼苗数0小于新增加的鱼苗数${this.form.differenceData}`);
            return;
          }
          if(this.form.remainingInventory && this.form.remainingInventory < this.form.differenceData) {
            this.$modal.msgWarning(`当前批次剩余鱼苗数${this.form.remainingInventory}小于${this.form.differenceData}`);
            return;
          }
        }
        if(this.editType == 1) {
          this.form.remainingInventory = this.form.remainingInventory - this.form.fishNum;
        } else if(this.editType == 2) {
          if(!this.form.remainingInventory) this.form.remainingInventory = 0;
          this.form.remainingInventory = this.form.remainingInventory - this.form.differenceData;
        }
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        this.operationLoading = true;
        if (this.form.id != null) {
          updateZkPondFishDetail(this.form).then(response => {
            this.$modal.msgSuccess("修正成功");
            this.open = false;
            this.getList();
            this.getBatchNumerList();
            notifyProductionRecordChanged({ type: 'stocking', pondId: this.form.pondId, source: 'zkPondFishDetail' });
            setTimeout(()=>{
              this.operationLoading = false;
            }, 1000);
          }).catch(() => { this.operationLoading = false });
          return;
        }
        createZkPondFishDetail(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          this.getBatchNumerList();
          notifyProductionRecordChanged({ type: 'stocking', pondId: this.form.pondId, source: 'zkPondFishDetail' });
          setTimeout(()=>{
            this.operationLoading = false;
          }, 1000);
        }).catch(() => { this.operationLoading = false });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除存塘鱼苗明细编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPondFishDetail(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有存塘鱼苗明细数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPondFishDetailExcel(params);
        }).then(response => {
          this.$download.excel(response, '存塘鱼苗明细.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.manual-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.manual-row .el-input {
  flex: 1;
}
.tip-text {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
}
</style>
