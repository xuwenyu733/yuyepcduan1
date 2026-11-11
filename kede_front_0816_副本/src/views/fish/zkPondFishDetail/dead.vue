<template>
  <!-- 2. 死亡记录 -->
  <div class="app-container">
    <!-- 2. 死亡记录 -->
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="存塘鱼苗表id" prop="zkPondFishId">
        <el-input v-model="queryParams.zkPondFishId" placeholder="请输入存塘鱼苗表id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="批次编号">
        <el-select style="width:100%;" v-model="filterBatch" placeholder="请选择批次" filterable clearable @change="onHealthFilterChange">
          <el-option v-for="(item, index) in healthBatchOptionList" :key="'b_'+index+'_'+item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="系统编号" prop="pondId">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择存塘系统编号" filterable clearable @change="handleQuery">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
            :value="item.pondDetailNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="存塘编号" prop="pondId">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择存塘编号" filterable clearable
          @change="handleQuery">
          <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
            :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="车间">
        <el-select style="width:100%;" v-model="filterWorkshop" placeholder="请选择车间" filterable clearable @change="onHealthWorkshopChange">
          <el-option v-for="(item, index) in healthWorkshopOptionList" :key="'w_'+index+'_'+item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="系统">
        <el-select style="width:100%;" v-model="filterSystem" placeholder="请选择系统" filterable clearable @change="onHealthSystemChange">
          <el-option v-for="(item, index) in healthSystemOptionList" :key="'s_'+index+'_'+item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号">
        <el-select style="width:100%;" v-model="filterPond" placeholder="请选择池号" filterable clearable @change="onHealthFilterChange">
          <el-option v-for="(item, index) in healthPondOptionList" :key="'p_'+index+'_'+item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="鱼苗种类">
        <el-select v-model="filterSpecies" placeholder="请选择品种" filterable clearable size="small" style="width: 100%;" @change="onHealthFilterChange">
          <el-option v-for="(item, index) in healthSpeciesOptionList" :key="'sp_'+index+'_'+item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-pond-fish-detail:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="存塘鱼苗表id" align="center" prop="zkPondFishId" />
      <el-table-column label="所属车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="批次编号" align="center" prop="batchNumber" min-width="220" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthBatch(scope.row.batchNumber) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车间" align="center" prop="workshopName" min-width="120" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthWorkshop(scope.row, siteOptions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="系统" align="center" prop="pondSystemName" min-width="100" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthSystem(scope.row, siteOptions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="池号" align="center" min-width="140" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthPond(scope.row, siteOptions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="pondType" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ judgePondType(scope.row.zkPondFishDO) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="鱼苗种类" align="center" prop="fishTypes" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthSpecies(scope.row, fishTypeDictLabel(scope.row.fishTypes)) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="死苗规格" align="center" prop="fishSpec">
        <template v-slot="scope">
          <span>{{ displayDeathSpec(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="历史鱼数" align="center" prop="historyFishNum" />
      <el-table-column label="死亡数量" align="center" prop="fishNum" />
      <el-table-column label="当前鱼数" align="center" prop="currFishNum" />
      <!-- <el-table-column label="投放类型(-2:损失;-1:卖出;1:投放;)" align="center" prop="addType" /> -->
      <!-- <el-table-column label="操作类型" align="center" prop="addType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE" :value="scope.row.addType" />
        </template>
      </el-table-column> -->
      <el-table-column label="时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
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

    <!-- 添加 / 修正死亡记录（共用弹窗） -->
    <DeathRecordFormDialog ref="deathRecordDialog" @success="onDeathRecordSaved" />
  </div>
</template>

<script>
import { createZkPondFishDetail, updateZkPondFishDetail, deleteZkPondFishDetail, getZkPondFishDetail, getZkPondFishDetailPage, exportZkPondFishDetailExcel } from "@/api/fish/zkPondFishDetail";
import { createZkFishProofing, updateZkFishProofing, deleteZkFishProofing, getZkFishProofing, getZkFishProofingPage, exportZkFishProofingExcel, getLastProofing } from "@/api/fish/zkFishProofing";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadDeptScopedPondList, loadSharedSiteOptions } from "@/utils/siteOptions";
import { notifyProductionRecordChanged } from "@/utils/productionRecordBridge";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { listForBatch } from "@/api/erp/purchaseIn";
import {
  getProductPage
} from "@/api/erp/product";
import { UserInfoKey } from "@/utils/constants";
import { getZkPondFishPage } from "@/api/fish/zkPondFish";
import { getZkSpecsLogPage } from "@/api/fish/zkSpecsLog";
import { normalizeSpeciesName } from "@/utils/speciesTempBridge";
import { getPrecisionFeedingPonds } from "@/api/fish/zkBiologyCapital";
import {
  healthWorkshopOptions,
  healthSystemOptions,
  healthPondOptions,
  healthSpeciesOptions,
  healthBatchOptions,
  mapHealthPondMaster,
  mapHealthFiltersToQuery,
  displayHealthWorkshop,
  displayHealthSystem,
  displayHealthPond,
  displayHealthSpecies,
  displayHealthBatch
} from "@/utils/healthFilterOptions";
import DeathRecordFormDialog from "@/views/fish/zkPondFishDetail/DeathRecordFormDialog.vue";

export default {
  name: "ZkPondFishDetail",
  components: {
    FileBusUpload, 
    DeathRecordFormDialog,
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
      pondList3: [],
      pondLoading: false,
      userList1: [],
      productList: [],
      workshopList: [],
      systemList: [],
      bachchNumberList: [],
      siteOptions: null,
      healthPonds: [],
      filterWorkshop: '',
      filterSystem: '',
      filterPond: '',
      filterSpecies: '',
      filterBatch: '',
      dict_flushTypes: [],
      specsByPondId: {},
      specsByPondDetailNum: {}, 
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
        batchNumber: null,
        pondNum: null,
        pondDetailNum: null,
        pondName: null,
        pondId: null,
        fishTypes: null,
        fishName: null,
        fishNum: null,
        addType: "-2", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fishNum: [{ required: true, message: "死亡数量不能为空", trigger: "blur" }],
        createTime: [{ required: true, message: "时间不能为空", trigger: "change" }],
        batchNumber: [{
          validator: (rule, value, callback) => {
            if (value === null || value === undefined || String(value).trim() === '') {
              callback(new Error('批次编号不能为空'));
            } else {
              callback();
            }
          },
          trigger: ['change', 'blur']
        }],
        pondId: [{ required: true, message: "存塘不能为空", trigger: "change" }],
      },
      editType: 1, // 1:添加;2:修正;
    };
  },
  computed: {
    healthWorkshopOptionList() {
      return healthWorkshopOptions(this.siteOptions);
    },
    healthSystemOptionList() {
      return healthSystemOptions(this.siteOptions, this.filterWorkshop);
    },
    healthPondOptionList() {
      return healthPondOptions(this.siteOptions, this.filterWorkshop, this.filterSystem);
    },
    healthSpeciesOptionList() {
      return healthSpeciesOptions(this.healthPonds);
    },
    healthBatchOptionList() {
      return healthBatchOptions(this.healthPonds);
    },
  },
  created() {
    try {
      this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey) || '{}') || {};
    } catch (e) {
      this.userInfo = {};
    }
    this.getList();
    this.getPondList();
    this.loadHealthMaster();
    this.getUserList();
    this.getBatchNumerList();
    this.getProductList();
    this.loadCurrentSpecs();
    this.dict_flushTypes = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES);
    if (String(this.$route.query.openAdd || '') === '1') {
      this.$nextTick(() => {
        this.handleAdd();
        try {
          const q = Object.assign({}, this.$route.query);
          delete q.openAdd;
          this.$router.replace({ path: this.$route.path, query: q });
        } catch (e) { /* ignore */ }
      });
    }
  },
  watch: {},
  methods: {
    judgePondType(zkPondFish) {
      if(!zkPondFish) return '单一';
      if(zkPondFish.batchNumber && zkPondFish.batchNumber.split(",").length >1) return "混合";
      return '单一';
    },
    displayHealthWorkshop,
    displayHealthSystem,
    displayHealthPond,
    displayHealthSpecies,
    displayHealthBatch,
    fishTypeDictLabel(value) {
      return getDictDataLabel(DICT_TYPE.ZK_POND_FISH_TYPES, value);
    },
    loadHealthMaster() {
      return getPrecisionFeedingPonds().then((res) => {
        const list = (res && res.data) || [];
        this.healthPonds = mapHealthPondMaster(Array.isArray(list) ? list : []);
      }).catch(() => {
        this.healthPonds = [];
      });
    },
    onHealthWorkshopChange(val) {
      this.filterWorkshop = val || '';
      if (!val) {
        this.filterSystem = '';
        this.filterPond = '';
      } else if (this.filterSystem && this.healthSystemOptionList.length && !this.healthSystemOptionList.includes(this.filterSystem)) {
        this.filterSystem = '';
        this.filterPond = '';
      }
      this.onHealthFilterChange();
    },
    onHealthSystemChange(val) {
      this.filterSystem = val || '';
      if (!val || (this.filterPond && this.healthPondOptionList.length && !this.healthPondOptionList.includes(this.filterPond))) {
        this.filterPond = '';
      }
      this.onHealthFilterChange();
    },
    onHealthFilterChange() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    applyHealthQuery() {
      const mapped = mapHealthFiltersToQuery(this.siteOptions, this.healthPonds.length ? this.healthPonds : this.pondList, {
        workshop: this.filterWorkshop,
        system: this.filterSystem,
        pond: this.filterPond,
      });
      this.queryParams.workshopId = mapped.workshopId;
      this.queryParams.pondSystemId = mapped.pondSystemId;
      this.queryParams.pondNum = mapped.pondNum;
      this.queryParams.pondDetailNum = mapped.pondDetailNum;
      this.queryParams.pondId = mapped.pondId;
      this.queryParams.batchNumber = this.filterBatch || null;
      this.queryParams.fishTypes = this.resolveFishTypeQuery(this.filterSpecies);
    },
    resolveFishTypeQuery(species) {
      const name = String(species || '').trim();
      if (!name) return null;
      const dicts = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES) || [];
      const hit = dicts.find((d) => normalizeSpeciesName(d.label || d.value) === name || d.label === name);
      return hit ? hit.value : name;
    },
    querySearch(item, searchType) {
      if (searchType == -2.1) this.filterBatch = item || '';
      this.onHealthFilterChange();
    },
    getPondInfo(list, workshopId, pondNum, pondDetailNum) {
      if(!(list && list.length)) return;
      let item = null;
      list.forEach((curr) => {
        if(workshopId != null && workshopId !== '' && String(workshopId) === String(curr.workshopId)) {
          item = curr;
          return;
        }
        if(pondNum != null && pondNum !== '' && String(pondNum) === String(curr.pondNum)) {
          item = curr;
          return;
        }
        if(pondDetailNum != null && pondDetailNum !== '' && String(pondDetailNum) === String(curr.pondDetailNum)) {
          item = curr;
          return;
        }
      })
      return item;
    },
    formatBatchDisplay(raw) {
      if (raw == null || raw === '') return '';
      const matched = this.matchBatchOption(raw);
      const hit = (this.bachchNumberList || []).find(
        (b) => String(b.relationOrderNo) === String(matched)
      );
      return (hit && hit.info) || matched || String(raw);
    },
    formatFishTypeDisplay(row) {
      if (!row) return '';
      const fromDict = getDictDataLabel(DICT_TYPE.ZK_POND_FISH_TYPES, row.fishTypes);
      const label = fromDict || row.fishName || row.fishTypes;
      return normalizeSpeciesName(label) || label || '-';
    },
    resolveWorkshopName(row) {
      if (!row) return '-';
      if (row.workshopName) return row.workshopName;
      const wid = row.workshopId;
      if (wid == null) return '-';
      const hit = (this.workshopList || []).find((w) => String(w.workshopId) === String(wid));
      return (hit && hit.workshopName) || '-';
    },
    resolveSystemName(row) {
      if (!row) return '-';
      if (row.pondSystemName) return row.pondSystemName;
      const pondNum = row.pondNum;
      if (pondNum != null && pondNum !== '') {
        const hit = (this.systemList || []).find((s) => String(s.pondNum) === String(pondNum));
        if (hit && (hit.pondSystemName || hit.pondNum)) return hit.pondSystemName || hit.pondNum;
      }
      const pond = this.getPondInfo(this.pondList, row.workshopId, row.pondNum, row.pondDetailNum);
      return (pond && (pond.pondSystemName || pond.pondNum)) || '-';
    },
    updateBatchNumerId(currId){
      let item = null;
      const id = currId == null ? '' : String(currId).trim();
      (this.bachchNumberList || []).forEach(obj => {
        if (String(obj.relationOrderNo) === id) {
          item = obj;
        }
      });
      if (!item) return;
      this.form.supplier = item.supplierName;
      this.form.supplierAddress = item.supplierAddress;
      this.form.supplierId = item.supplierId;
      if (item.productNo) {
        this.form.productNo = item.productNo;
        this.updateProductId(this.form.productNo);
      }
    },
    onBatchNumberChange(currId) {
      this.$set(this.form, 'batchNumber', currId == null ? '' : String(currId).trim());
      this.updateBatchNumerId(this.form.batchNumber);
      this.$nextTick(() => {
        if (this.$refs.form) this.$refs.form.clearValidate('batchNumber');
      });
    },
    normalizeBatch(val) {
      return String(val == null ? '' : val).replace(/BZ/g, '').replace(/CGRK/g, '').trim();
    },
    pickFirstBatch(raw) {
      return String(raw || '').split(/[,，]/).map(s => s.trim()).filter(Boolean)[0] || '';
    },
    matchBatchOption(raw) {
      const first = this.pickFirstBatch(raw);
      if (!first) return '';
      const n = this.normalizeBatch(first);
      const hit = (this.bachchNumberList || []).find(b =>
        this.normalizeBatch(b.relationOrderNo) === n || String(b.relationOrderNo) === String(first)
      );
      // 命中返回原 relationOrderNo；未命中保留原始批次号（勿去掉 CGRK 前缀）
      return hit ? hit.relationOrderNo : first;
    },
    ensureBatchOption(batchNo) {
      const no = String(batchNo || '').trim();
      if (!no) return;
      const exists = (this.bachchNumberList || []).some(b => String(b.relationOrderNo) === no);
      if (!exists) {
        this.bachchNumberList = [{ id: 'tmp_' + no, relationOrderNo: no, info: no }].concat(this.bachchNumberList || []);
      }
    },
    setFormBatchNumber(batchNo) {
      const no = String(batchNo || '').trim();
      if (!no) return;
      this.ensureBatchOption(no);
      this.$set(this.form, 'batchNumber', no);
      this.updateBatchNumerId(no);
      this.$nextTick(() => {
        if (this.$refs.form) this.$refs.form.clearValidate('batchNumber');
      });
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
      // 执行查询
      listForBatch(queryParams).then(response => {
        let list = response.data || [];
        if (!Array.isArray(list)) list = [];
        list.forEach(item => {
          item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName})`;
        })
        this.bachchNumberList = list;
      }).catch(() => {
        this.bachchNumberList = [];
      });
    },
    updateProductId(currId){
      if (currId == null || currId === '') return;
      let item = null;
      (this.productList || []).forEach(obj => {
        if(obj.productNo == currId) {
          item = obj;
        }
      })
      if (!item) return;
      this.form.fishName = item.productName;
      this.form.fishTypes = item.fishTypes;
      this.form.productNo = item.productNo;
      if (!this.form.fishSpec) this.form.fishSpec = item.standard;
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
          // console.log("===========>"+JSON.stringify(obj));
        })
      })
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      this.applyHealthQuery();
      // 执行查询
      getZkPondFishDetailPage(this.queryParams).then(response => {
        this.list = response.data.list;
        if(this.list) {
          this.list.forEach(obj => {
            // 保留完整批次号（含 CGRK 等前缀），展示时用 formatBatchDisplay 对齐入库单文案
            obj.displayFishSpec = this.resolveCurrentSpec(obj);
          })
        }
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateFlushTypes(currType) {
      // 1. 找到对应值
      let item = null;
      this.dict_flushTypes.forEach(obj => {
        if(obj.value == currType) {
          item = obj;
          return;
        }
      })
      // 2. 执行更新操作
      this.form.fishName = item.label;
      console.log(`=====>form:${JSON.stringify(this.form)}`)
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
      // 2. 执行 deptId、userId赋值
      this.form.userId = item.id;
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>"+JSON.stringify(this.form));
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
    loadCurrentSpecs() {
      getZkSpecsLogPage({ pageNo: 1, pageSize: 500 }).then(res => {
        const byPondId = {};
        const byDetail = {};
        const list = ((res.data && res.data.list) || []).slice().sort((a, b) => Number(b.createTime || 0) - Number(a.createTime || 0));
        list.forEach(item => {
          const spec = (item.acturalSpecs != null && item.acturalSpecs !== '') ? item.acturalSpecs : item.predictSpecs;
          if (spec == null || spec === '') return;
          if (item.pondId != null && byPondId[String(item.pondId)] == null) byPondId[String(item.pondId)] = spec;
          if (item.pondDetailNum && byDetail[String(item.pondDetailNum)] == null) byDetail[String(item.pondDetailNum)] = spec;
        });
        this.specsByPondId = byPondId;
        this.specsByPondDetailNum = byDetail;
        if (this.list && this.list.length) {
          this.list.forEach(obj => { obj.displayFishSpec = this.resolveCurrentSpec(obj); });
        }
      }).catch(() => {});
    },
    resolveCurrentSpec(row) {
      if (!row) return '';
      const pondId = row.pondId != null ? String(row.pondId) : '';
      const detail = row.pondDetailNum != null ? String(row.pondDetailNum) : '';
      const fromSpecs = (pondId && this.specsByPondId[pondId]) || (detail && this.specsByPondDetailNum[detail]);
      return fromSpecs != null && fromSpecs !== '' ? fromSpecs : (row.fishSpec || '');
    },
    displayDeathSpec(row) {
      if (!row) return '-';
      if (row.displayFishSpec != null && row.displayFishSpec !== '') return row.displayFishSpec;
      return this.resolveCurrentSpec(row) || '-';
    },
    updateLastFishSpec(pondId) {
      const fromLog = pondId != null ? this.specsByPondId[String(pondId)] : '';
      if (fromLog != null && fromLog !== '') {
        this.form.fishSpec = fromLog;
      }
      getLastProofing(pondId).then(response => {
        if(response.data && response.data.avgSpecs) {
          this.form.fishSpec = response.data.avgSpecs;
        }
      }).catch(() => {});
    },
    async fillBatchFromPond(item) {
      if (!item) return;
      let batchRaw = item.batchNumber;
      // 存塘行已带批次时优先用，避免每次选择都串行打两遍接口
      if (batchRaw) {
        const matched = this.matchBatchOption(batchRaw);
        if (matched) this.setFormBatchNumber(matched);
        getZkPondFishPage({ pageNo: 1, pageSize: 5, pondId: item.id }).then((fishRes) => {
          const fishList = (fishRes.data && fishRes.data.list) || [];
          if (fishList.length && fishList[0].id != null) {
            this.form.zkPondFishId = fishList[0].id;
          }
          if (fishList.length && fishList[0].batchNumber) {
            const m2 = this.matchBatchOption(fishList[0].batchNumber);
            if (m2) this.setFormBatchNumber(m2);
          }
        }).catch(() => {});
        return;
      }
      try {
        const fishRes = await getZkPondFishPage({ pageNo: 1, pageSize: 20, pondId: item.id });
        const fishList = (fishRes.data && fishRes.data.list) || [];
        if (fishList.length && fishList[0].batchNumber) batchRaw = fishList[0].batchNumber;
        if (fishList.length && fishList[0].id != null) {
          this.form.zkPondFishId = fishList[0].id;
        }
      } catch (e) { /* ignore */ }
      if (!batchRaw) {
        try {
          const stockRes = await getZkPondFishDetailPage({ pageNo: 1, pageSize: 20, pondId: item.id, addType: '1' });
          const stocks = ((stockRes.data && stockRes.data.list) || []).slice().sort((a, b) => Number(b.createTime || 0) - Number(a.createTime || 0));
          if (stocks.length && stocks[0].batchNumber) batchRaw = stocks[0].batchNumber;
          if (stocks.length && stocks[0].productNo && !this.form.productNo) {
            this.form.productNo = stocks[0].productNo;
            this.updateProductId(this.form.productNo);
          }
        } catch (e) { /* ignore */ }
      }
      const matched = this.matchBatchOption(batchRaw);
      if (matched) {
        this.setFormBatchNumber(matched);
      }
    },
    updatePondId(currId){
      let item = null;
      const pools = (this.pondList3 && this.pondList3.length) ? this.pondList3 : this.pondList;
      (pools || []).forEach(obj => {
        if(obj.id == currId) {
          item = obj;
        }
      })
      if (!item) return
      // 2. 执行 赋值
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.historyFishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.updateLastFishSpec(item.id);
      this.fillBatchFromPond(item);
    },
    getPondList(force) {
      if (!force && this.pondList3 && this.pondList3.length) {
        return Promise.resolve(this.pondList3);
      }
      // 已有数据时后台静默刷新，不先清空下拉（避免弹窗打开后选项空白等待）
      if (!(this.pondList3 && this.pondList3.length)) {
        this.pondLoading = true;
      }
      return Promise.all([
        loadDeptScopedPondList({ force: !!force }),
        loadSharedSiteOptions({ includePonds: true, force: !!force }).catch(() => null),
      ]).then(([list, opts]) => {
        this.siteOptions = opts;
        const rows = Array.isArray(list) ? list.slice() : [];
        this.enrichPondsFromSiteOptions(rows, opts);
        this.pondList = rows;
        // 与账户可见鱼池一致：全部纳入可选，存塘>0 的排前面便于选择
        this.pondList3 = rows.slice().sort((a, b) => (Number(b.fishNum) || 0) - (Number(a.fishNum) || 0));
        this.getWorkshopList();
        return this.pondList3;
      }).catch(err => {
        console.warn('getPondList failed', err);
        if (!(this.pondList3 && this.pondList3.length)) {
        this.pondList = [];
        this.pondList3 = [];
        }
        return this.pondList3 || [];
      }).finally(() => {
        this.pondLoading = false;
      })
    },
    /** 用生产登记表（与其它页同源）回填车间名 / 系统名，避免存塘冗余字段过期 */
    enrichPondsFromSiteOptions(rows, opts) {
      if (!opts || !rows || !rows.length) return;
      const wsById = new Map();
      (opts.workshopOptions || []).forEach((o) => {
        if (o == null) return;
        const id = o.value != null ? o.value : o.id;
        if (id != null) wsById.set(String(id), o.label || o.name || '');
      });
      const sysById = new Map();
      (opts.systemOptions || []).forEach((o) => {
        if (o == null) return;
        const id = o.value != null ? o.value : o.id;
        if (id != null) sysById.set(String(id), o.label || o.name || '');
      });
      const byPondId = new Map();
      (opts.rows || []).forEach((r) => {
        if (r && r.id != null) byPondId.set(String(r.id), r);
      });
      rows.forEach((p) => {
        if (!p) return;
        if (p.workshopId != null && wsById.has(String(p.workshopId))) {
          p.workshopName = wsById.get(String(p.workshopId)) || p.workshopName;
        }
        if (p.pondSystemId != null && sysById.has(String(p.pondSystemId))) {
          p.pondSystemName = sysById.get(String(p.pondSystemId)) || p.pondSystemName;
        }
        const hit = byPondId.get(String(p.id));
        if (hit) {
          if (hit.workshopName) p.workshopName = hit.workshopName;
          if (hit.pondSystemName) p.pondSystemName = hit.pondSystemName;
          if (hit.pondNum != null && (p.pondNum == null || p.pondNum === '')) p.pondNum = hit.pondNum;
        }
      });
    },
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.workshopList = [];
      this.systemList = [];
      // 车间：优先生产登记表（与死亡量存活率 / 精准投喂一致）
      if (this.siteOptions && (this.siteOptions.workshopOptions || []).length) {
        (this.siteOptions.workshopOptions || []).forEach((o) => {
          const wid = o.id != null ? o.id : o.value;
          if (wid == null) return;
          this.workshopList.push({
            id: wid,
            workshopId: wid,
            workshopName: o.label || o.name || String(wid),
          });
        });
      } else {
        (this.pondList || []).forEach((item) => {
          if (!shopTemp.includes(item.workshopId)) {
            shopTemp.push(item.workshopId);
            this.workshopList.push(item);
          }
        });
      }
      // 系统：按 pondNum 去重（后端按 pondNum 筛选），展示名用系统登记名
      (this.pondList || []).forEach((item) => {
        if (item.pondNum == null || item.pondNum === '') return;
        if (!systemTemp.includes(item.pondNum)) {
          systemTemp.push(item.pondNum);
          this.systemList.push({
            id: item.id,
            pondNum: item.pondNum,
            pondSystemName: item.pondSystemName || item.pondNum,
            pondSystemId: item.pondSystemId,
            workshopId: item.workshopId,
            workshopName: item.workshopName,
          });
        }
      });
    },
    /** 表单重置 */
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
        addType: "-2", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        createTime: new Date().getTime(),

        pondScale: undefined, //存塘规格
        historyFishNum: undefined, //历史鱼数
        currFishNum: undefined, //现在鱼数
        fishSpec: '', //鱼苗规格
        salePrice: undefined, //销售价格
        saleWeight: undefined, //销售重量
        saleIncome: undefined, //销售收入
        saleCost: undefined, //销售成本
        targetPondNum: undefined, //目标存塘编号
        targetPondSpec: undefined, //目标存塘规格
        targetPondId: undefined, //目标存塘Id
        targetPondName: undefined, //目标存塘名称
        targetHistoryFishNum: undefined, //目标历史鱼数
        targetCurrFishNum: undefined, //目标现在鱼数
        remark: undefined, //备注
        supplier: undefined,
        supplierAddress: undefined,
        supplierId: undefined,
        pondDetailNum: undefined,
        pondSystemName: undefined,
        pondNum: undefined,
        workshopName: undefined,
        userName: undefined,
      };
      this.$nextTick(() => {
        this.resetForm("form");
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.filterWorkshop = '';
      this.filterSystem = '';
      this.filterPond = '';
      this.filterSpecies = '';
      this.filterBatch = '';
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增：打开共用弹窗 */
    handleAdd() {
      if (this.$refs.deathRecordDialog) this.$refs.deathRecordDialog.openAdd();
    },
    /** 修正：打开共用弹窗 */
    handleUpdate(row) {
      if (this.$refs.deathRecordDialog) this.$refs.deathRecordDialog.openEdit(row);
    },
    onDeathRecordSaved() {
            this.queryParams.pageNo = 1;
            this.getList();
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
      // 处理查询参数
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
