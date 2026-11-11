<template>
  <!-- 4. 分池记录 -->
  <div class="app-container">
    <!-- 4. 分池记录 -->
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
      <el-form-item label="原池号" prop="pondId">
        <el-select style="width:100%;" v-model="queryParams.pondId" placeholder="请选择原池号" filterable clearable
          @change="handleQuery">
          <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
            :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="分池号" prop="targetPondId">
        <el-select style="width:100%;" v-model="queryParams.targetPondId" placeholder="请选择分池号" filterable clearable
          @change="handleQuery">
          <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
            :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-select v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" filterable clearable size="small"
          style="width: 100%;" @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="投放人" prop="userName">
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
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
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
      <el-table-column label="原池编号" align="center" prop="pondDetailNum" />
      <el-table-column label="类型" align="center" prop="pondType" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ judgePondType(scope.row.zkPondFishDO) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成活率" align="center" prop="survivalRate" width="100">
        <template v-slot="scope">
          <span>{{ formatSurvivalRate(scope.row.survivalRate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="原池规格" align="center" prop="fishSpec" />
      <el-table-column label="原池数量(尾)" align="center" prop="historyFishNum" />
      <el-table-column label="原池重量(kg)" align="center" prop="currPondWeight" />
      <el-table-column label="分池编号" align="center" prop="targetPondDetailNum" />
      <el-table-column label="分池规格(g/尾)" align="center" prop="targetFishSpec" />
      <el-table-column label="分池数量(尾)" align="center" prop="fishNum" />
      <el-table-column label="分池重量(kg)" align="center" prop="targetWeight" />
      <el-table-column label="分池后3日死亡量" align="center" prop="splitAfterDead" width="130" />
      <el-table-column label="分池时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="true"
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
      <el-form ref="form" :model="form" :rules="rules" label-width="160px">
        <el-form-item label="原池编号" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择有存苗的原池(关联投放记录自动带出批次)" filterable clearable
            @change="updatePondId">
            <el-option v-for="(item, index) in sourcePondList" :key="item.id + '__' + index"
              :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="批次编号" prop="batchNumber">
          <el-input v-model="form.batchNumber" placeholder="选择原池后自动导入投放批次" />
        </el-form-item>
        <el-form-item label="成活率" prop="survivalRate">
          <el-input :value="formatSurvivalRate(form.survivalRate)" placeholder="(投放-死亡)/投放，自动计算" disabled />
        </el-form-item>
        <el-form-item label="原池规格" prop="fishSpec">
          <div class="manual-row">
            <el-input v-model="form.fishSpec" placeholder="关联存塘/投放规格，可修正(g/尾)" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='fishSpec'"
              @click="startVoiceField('fishSpec')">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="原池重量(kg)" prop="currPondWeight">
          <div class="manual-row">
            <el-input v-model="form.currPondWeight" placeholder="人工录入原池重量(kg)" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='currPondWeight'"
              @click="startVoiceField('currPondWeight')">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="原池数量(尾)" prop="historyFishNum">
          <el-input v-model="form.historyFishNum" placeholder="原池重量/原池规格 自动计算" disabled />
        </el-form-item>
        <el-form-item label="分池编号" prop="targetPondId">
          <el-select style="width:100%;" v-model="form.targetPondId" placeholder="请选择分池编号(人工选择)" filterable clearable
            @change="updateNewPondId">
            <el-option v-for="(item, index) in pondList" :key="'t_'+item.id + '__' + index"
              :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="鱼苗种类" prop="productNo">
          <el-select v-model="form.productNo" placeholder="请输入鱼苗种类" filterable clearable size="small" style="width: 100%;" @change="updateProductId">
            <el-option v-for="dict in productList" :key="dict.productNo"
              :label="dict.info" :value="dict.productNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="分池规格(g/尾)" prop="targetFishSpec">
          <div class="manual-row">
            <el-input v-model="form.targetFishSpec" placeholder="人工录入分池规格(g/尾)" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='targetFishSpec'"
              @click="startVoiceField('targetFishSpec')">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="分池重量(kg)" prop="targetWeight">
          <div class="manual-row">
            <el-input v-model="form.targetWeight" placeholder="人工录入分池重量(kg)" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceField==='targetWeight'"
              @click="startVoiceField('targetWeight')">语音</el-button>
          </div>
        </el-form-item>
        <el-form-item label="分池数量(尾)" prop="fishNum">
          <el-input v-model="form.fishNum" placeholder="分池重量/分池规格 自动计算" disabled />
        </el-form-item>
        <el-form-item label="分池后3日死亡量" prop="splitAfterDead">
          <el-input v-model="form.splitAfterDead" placeholder="关联死亡记录，分池后3日内自动汇总" disabled />
        </el-form-item>
        <el-form-item label="分池时间" prop="createTime">
          <el-date-picker clearable v-model="form.createTime" style="width: 100%;" type="datetime" value-format="timestamp"
            placeholder="分池时间(默认当前，可按单据时间调整)" />
        </el-form-item>
        <el-form-item label="单据附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_pond_fish_split"></FileBusUpload>
          <div class="tip-text">支持拍照单据上传；上传后可人工核对分池时间</div>
        </el-form-item>
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择负责人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
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
import { createSaleOut, updateSaleOut, deleteSaleOut, getSaleOut, getSaleOutPage, exportSaleOutExcel } from "@/api/erp/saleOut";
import {
  getProductPage
} from "@/api/erp/product";
import { UserInfoKey } from "@/utils/constants";
import { listForBatch } from "@/api/erp/purchaseIn";

export default {
  name: "ZkPondFishDetail",
  components: {
    FileBusUpload, 
  },
  data() {
    return {
      operationLoading: false,
      userInfo: {},
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
      bachchNumberList: [],
      productList: [],
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
        fishTypes: null,
        fishName: null,
        fishNum: null,
        addType: "-3", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fishNum: [{ required: true, message: "分池数量不能为空", trigger: "blur" }],
        batchNumber: [{ required: true, message: "批次编号不能为空", trigger: "blur" }],
        pondId: [{ required: true, message: "原池编号不能为空", trigger: "change" }],
        targetPondId: [{ required: true, message: "分池编号不能为空", trigger: "change" }],
        productNo: [{ required: true, message: "鱼苗种类不能为空", trigger: "change" }],
        targetWeight: [{ required: true, message: "分池重量不能为空", trigger: "blur" }],
        currPondWeight: [{ required: true, message: "原池重量不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "用户不能为空", trigger: "change" }],
        fishSpec: [{ required: true, message: "原池规格不能为空", trigger: "blur" }],
        targetFishSpec: [{ required: true, message: "分池规格不能为空", trigger: "blur" }],
        createTime: [{ required: true, message: "分池时间不能为空", trigger: "blur" }],
      },
      editType: 1, // 1:添加;2:修正;
      voiceField: '',
      speechRecognition: null,
      calcDeadTimer: null,
    };
  },
  computed: {
    // 原池：仅展示有存苗的池
    sourcePondList() {
      return (this.pondList || []).filter(p => Number(p.fishNum) > 0)
    },
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getList();
    this.getPondList();
    this.getUserList();
    this.getProductList();
    this.getBatchNumerList();
    this.dict_flushTypes = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES);
  },
  beforeDestroy() {
    this.stopVoice()
    if (this.calcDeadTimer) clearTimeout(this.calcDeadTimer)
  },
  watch: {
    'form.targetFishSpec'() {
      if (this.form.targetFishSpec && this.form.targetWeight) this.calculateFishNum()
    },
    'form.targetWeight'() {
      if (this.form.targetFishSpec && this.form.targetWeight) this.calculateFishNum()
    },
    'form.fishSpec'() {
      if (this.form.fishSpec && this.form.currPondWeight) this.calculateHistoryFishNum()
    },
    'form.currPondWeight'() {
      if (this.form.fishSpec && this.form.currPondWeight) this.calculateHistoryFishNum()
    },
    'form.createTime'() {
      this.scheduleCalcSplitAfterDead()
    },
    'form.targetPondId'() {
      this.scheduleCalcSplitAfterDead()
    },
  },
  methods: {
    formatSurvivalRate(val) {
      if (val === null || val === undefined || val === '') return '-'
      const num = Number(val)
      if (!Number.isFinite(num)) return String(val)
      return (num * 100).toFixed(2) + '%'
    },
    judgePondType(zkPondFish) {
      if(!zkPondFish) return '单一';
      if(zkPondFish.batchNumber && zkPondFish.batchNumber.split(",").length >1) return "混合";
      return '单一';
    },
    /** 分池数量 = 分池重量(kg) / 分池规格(g/尾) */
    calculateFishNum() {
      const spec = this.parseSpecNumber(this.form.targetFishSpec)
      const weight = Number(this.form.targetWeight)
      if (!spec || !Number.isFinite(weight) || weight <= 0) return
      let temp1 = this.my_common.computer(weight, 1000, 2, 2);
      this.form.fishNum = this.my_common.computer(temp1, spec, 3, 0);
    },
    /** 原池数量 = 原池重量(kg) / 原池规格(g/尾) */
    calculateHistoryFishNum() {
      const spec = this.parseSpecNumber(this.form.fishSpec)
      const weight = Number(this.form.currPondWeight)
      if (!spec || !Number.isFinite(weight) || weight <= 0) return
      let temp1 = this.my_common.computer(weight, 1000, 2, 2);
      this.form.historyFishNum = this.my_common.computer(temp1, spec, 3, 0);
    },
    parseSpecNumber(spec) {
      if (spec == null || spec === '') return 0
      if (typeof spec === 'number') return spec
      const m = String(spec).match(/(\d+(\.\d+)?)/)
      return m ? Number(m[1]) : 0
    },
    pad2(n) { return n < 10 ? '0' + n : '' + n },
    fmtDateTime(ts) {
      const d = new Date(Number(ts))
      return `${d.getFullYear()}-${this.pad2(d.getMonth()+1)}-${this.pad2(d.getDate())} ${this.pad2(d.getHours())}:${this.pad2(d.getMinutes())}:${this.pad2(d.getSeconds())}`
    },
    /** 选原池后：带出投放批次/规格，并算成活率 */
    async loadPondStockingDerived(pondId) {
      if (!pondId) return
      try {
        const [stockRes, deadRes] = await Promise.all([
          getZkPondFishDetailPage({ pageNo: 1, pageSize: 200, pondId, addType: '1' }),
          getZkPondFishDetailPage({ pageNo: 1, pageSize: 200, pondId, addType: '-2' })
        ])
        const stocks = (stockRes.data && stockRes.data.list) || []
        const deads = (deadRes.data && deadRes.data.list) || []
        // 最近一条投放记录 → 批次 / 规格 / 品种
        if (stocks.length) {
          const latest = stocks.slice().sort((a, b) => Number(b.createTime || 0) - Number(a.createTime || 0))[0]
          if (latest.batchNumber) {
            this.form.batchNumber = String(latest.batchNumber).replaceAll('BZ', '').replaceAll('CGRK', '')
          }
          if (latest.fishSpec) this.form.fishSpec = latest.fishSpec
          if (latest.productNo) {
            this.form.productNo = latest.productNo
            this.updateProductId(latest.productNo)
            // 规格以投放记录为准（可再人工修正）
            if (latest.fishSpec) this.form.fishSpec = latest.fishSpec
          }
          if (latest.fishName) this.form.fishName = latest.fishName
        }
        const stockNum = stocks.reduce((s, r) => s + (Number(r.fishNum) || 0), 0)
        const deadNum = deads.reduce((s, r) => s + (Number(r.fishNum) || 0), 0)
        if (stockNum > 0) {
          this.$set(this.form, 'survivalRate', Number(((stockNum - deadNum) / stockNum).toFixed(4)))
        } else {
          this.$set(this.form, 'survivalRate', undefined)
        }
      } catch (e) {
        console.warn('loadPondStockingDerived failed', e)
      }
    },
    scheduleCalcSplitAfterDead() {
      if (this.calcDeadTimer) clearTimeout(this.calcDeadTimer)
      this.calcDeadTimer = setTimeout(() => this.calcSplitAfterDead(), 400)
    },
    /** 分池后3日死亡量：目标池在分池时间起3日内死亡记录合计 */
    async calcSplitAfterDead() {
      const pondId = this.form.targetPondId
      const startTs = Number(this.form.createTime)
      if (!pondId || !Number.isFinite(startTs) || startTs <= 0) return
      const endTs = startTs + 3 * 24 * 60 * 60 * 1000
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
        this.$set(this.form, 'splitAfterDead', sum)
      } catch (e) {
        console.warn('calcSplitAfterDead failed', e)
      }
    },
    startVoiceField(field) {
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
        const match = String(transcript).match(/(\d+(\.\d+)?)/)
        if (match) {
          this.$set(this.form, field, match[1])
          this.$modal.msgSuccess(`语音识别：${match[1]}`)
        } else {
          this.$modal.msgWarning(`未识别到数字，原文：${transcript || '空'}`)
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
    updateBatchNumerId(currId){
      let item = null;
      this.bachchNumberList.forEach(obj => {
        if(obj.relationOrderNo == currId) {
          item = obj;
          return;
        }
      })
      if (!item) return
      this.form.supplier = item.supplierName;
      this.form.supplierAddress = item.supplierAddress;
      this.form.supplierId = item.supplierId;
      this.form.productNo = item.productNo;
      this.form.fishSpec = item.productSpec;
      this.updateProductId(this.form.productNo);
      this.form.remainingInventory = item.remainingInventory==-1?item.productCount:item.remainingInventory;
    },
    getBatchNumerList() {
      let queryParams = {
        pageNo: 1,
        pageSize: 10,
        orderNo: null,
        outKind: '1',
        productName: null,
        userId: null,
        categoryId: "7",
        createTime: [],
        oType: 1,
      };
      listForBatch(queryParams).then(response => {
        let list = response.data;
        list.forEach(item => {
          item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName})`;
        })
        this.bachchNumberList = list;
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      getZkPondFishDetailPage(this.queryParams).then(response => {
        this.list = response.data.list;
        if(this.list) {
          this.list.forEach(obj => {
            obj.batchNumber = obj.batchNumber?obj.batchNumber.replaceAll("BZ", "").replaceAll("CGRK", ""):'';
          })
        }
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateProductId(currId){
      let item = null;
      this.productList.forEach(obj => {
        if(obj.productNo == currId) {
          item = obj;
          return;
        }
      })
      if (!item) return
      this.form.fishName = item.productName;
      this.form.fishTypes = item.fishTypes;
      // 若尚未有原池规格，则用产品标准规格
      if (!this.form.fishSpec) this.form.fishSpec = item.standard;
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
    updateFlushTypes(currType) {
      let item = null;
      this.dict_flushTypes.forEach(obj => {
        if(obj.value == currType) {
          item = obj;
          return;
        }
      })
      if (!item) return
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
      this.form.userId = item.id;
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          let tempStr = `${obj.deptName}-${obj.nickname}`;
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
      // 关联投放记录：批次自动导入 + 成活率
      this.loadPondStockingDerived(item.id)
    },
    updateNewPondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      if (!item) return
      this.form.targetPondName = item.pondName;
      this.form.targetPondNum = item.pondNum;
      this.form.targetPondScale = item.pondScale;
      this.form.targetHistoryFishNum = item.fishNum;
      this.form.targetPondId = item.id;
      this.form.targetPondDetailNum = item.pondDetailNum;
      this.form.targetPondSystemName = item.pondSystemName;
      this.scheduleCalcSplitAfterDead()
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
      this.form = {
        id: undefined,
        zkPondFishId: undefined,
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        fishTypes: undefined,
        fishNum: undefined,
        addType: "-3",
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        createTime: new Date().getTime(),
        pondScale: undefined,
        historyFishNum: undefined,
        currFishNum: undefined,
        fishSpec: '',
        currPondWeight: undefined,
        survivalRate: undefined,
        splitAfterDead: undefined,
        salePrice: undefined,
        saleWeight: undefined,
        saleIncome: undefined,
        saleCost: undefined,
        targetPondNum: undefined,
        targetPondSpec: undefined,
        targetPondId: undefined,
        targetPondName: undefined,
        targetHistoryFishNum: undefined,
        targetCurrFishNum: undefined,
        targetFishSpec: undefined,
        targetWeight: undefined,
        batchNumber: undefined,
        productNo: undefined,
        remark: undefined,
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
      this.queryParams.addType = "-3";
      this.handleQuery();
    },
    /** 新增按钮操作 */
    async handleAdd() {
      this.editType = 1;
      await this.getPondList();
      this.reset();
      this.updateUserId(parseInt(this.userInfo.userId));
      this.open = true;
      this.title = "添加分池记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修正按钮操作 */
    handleUpdate(row) {
      this.editType = 2;
      this.reset();
      const id = row.id;
      getZkPondFishDetail(id).then(response => {
        this.form = response.data;
        this.form.originData = this.form.fishNum;
        this.open = true;
        this.title = "修正分池记录";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
        if(!this.form.userId) {
          this.updateUserId(parseInt(this.userInfo.userId));
        }
        this.scheduleCalcSplitAfterDead()
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
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        this.operationLoading = true;
        const loading = this.$loading({
          lock: true,
          text: 'Loading',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        if (this.form.id != null) {
          updateZkPondFishDetail(this.form).then(response => {
            this.$modal.msgSuccess("修正成功");
            this.open = false;
            this.getList();
            setTimeout(()=>{
              this.operationLoading = false;
              loading.close();
            }, 1000);
          }).catch(() => { this.operationLoading = false; loading.close(); });
          return;
        }
        createZkPondFishDetail(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          notifyProductionRecordChanged({ type: 'split', pondId: this.form.pondId, source: 'split' });
          setTimeout(()=>{
            this.operationLoading = false;
            loading.close();
          }, 1000);
        }).catch(() => { this.operationLoading = false; loading.close(); });
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
