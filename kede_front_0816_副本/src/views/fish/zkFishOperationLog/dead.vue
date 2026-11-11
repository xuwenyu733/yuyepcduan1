<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="批次" prop="batchNumber">
        <el-input v-model="queryParams.batchNumber" placeholder="请输入批次" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="批次">
        <el-select style="width:100%;" v-model="filterBatch" placeholder="请选择批次" filterable clearable @change="onHealthFilterChange">
          <el-option v-for="(item, index) in healthBatchOptionList" :key="'b_'+index+'_'+item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="车间" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="系统" prop="pondSystemName">
        <el-input v-model="queryParams.pondSystemName" placeholder="请输入系统" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="鱼池" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入鱼池" clearable @keyup.enter.native="handleQuery"/>
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
      <el-form-item label="品种">
        <el-select style="width:100%;" v-model="filterSpecies" placeholder="请选择品种" filterable clearable @change="onHealthFilterChange">
          <el-option v-for="(item, index) in healthSpeciesOptionList" :key="'sp_'+index+'_'+item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="起始时间" end-placeholder="截至时间" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="规格" prop="fishSpecStr">
        <el-select style="width:100%;" v-model="queryParams.fishSpecStr" placeholder="请选择规格" filterable clearable @change="handleQuery">
          <el-option v-for="(item, index) in fishSpecList" :key="item+'_spec_'+index" :label="`${item}`"
            :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 死亡工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="toAddDeathLog"
                   v-hasPermi="['pl:zk-pond-fish-detail:create']">添加死亡记录</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-fish-operation-log:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-document" size="mini" @click="toLog" :loading="exportLoading"
                   v-hasPermi="['pl:zk-fish-operation-log:export']">死亡日志</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="记录时间" align="center" prop="logDay" width="110" />
      <el-table-column label="规格(g/尾)" align="center" prop="displayFishSpec" width="110">
        <template v-slot="scope">
          <span>{{ scope.row.displayFishSpec != null && scope.row.displayFishSpec !== '' ? scope.row.displayFishSpec : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="批次" align="center" min-width="180" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthBatch(scope.row.batchNumber) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车间" align="center" min-width="120" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthWorkshop(scope.row, siteOptions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="系统" align="center" min-width="100" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthSystem(scope.row, siteOptions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="池号" align="center" min-width="140" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthPond(scope.row, siteOptions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="品种" align="center" min-width="100" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayHealthSpecies(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当日死亡量(尾)" align="center" prop="dayFishNum" width="110" />
      <el-table-column label="累计死亡量(尾)" align="center" prop="accumulateFishNum" width="110" />
      <el-table-column label="投放数量(尾)" align="center" prop="pushFishNum" width="110" />
      <el-table-column label="批次存量(尾)" align="center" prop="currFishNum" width="110" />
      <!-- <el-table-column label="投放记录1" align="center" prop="logType" /> -->
      <el-table-column label="投放记录" align="center" prop="pushFishNum" width="180">
        <template v-slot="scope">
          <a v-if="scope.row.logType=='-2.1'" @click.stop="toPushLog({'batchNumber': scope.row.batchNumber})" style="color: #409EFF;">批次:{{ scope.row.batchNumber }}</a>
          <a v-if="scope.row.logType=='-2.2'" @click.stop="toPushLog({'workshopId': scope.row.workshopId})" style="color: #409EFF;">车间:{{ scope.row.workshopName }}</a>
          <a v-if="scope.row.logType=='-2.3'" @click.stop="toPushLog({'pondNum': scope.row.pondNum})" style="color: #409EFF;">系统:{{ scope.row.pondSystemName }}</a>
          <a v-if="scope.row.logType=='-2.4'" @click.stop="toPushLog({'pondId': scope.row.pondId})" style="color: #409EFF;">存塘:{{ scope.row.pondName }}</a>
        </template>
      </el-table-column>
      <el-table-column label="当日死亡率" align="center" prop="dayRate" width="110" />
      <el-table-column label="累计死亡率" align="center" prop="accumulateRate" width="110" />
      <!-- <el-table-column label="成活率" align="center" prop="survivalRate" /> -->
      <el-table-column label="成活率" align="center" prop="survivalRate" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{scope.row.survivalRate*100}}%</span>
        </template>
      </el-table-column>
      <el-table-column label="数据修正" align="center" prop="newDayFishNum" />
      <el-table-column label="修正依据" align="center" prop="correctionBasis" />
      <el-table-column label="差值率" align="center" prop="differencesRate" />
      <el-table-column label="差异原因" align="center" prop="reasonsForDifferences" />
      <el-table-column label="具体描述" align="center" prop="detailedDescription" />
      <!-- <el-table-column label="危险评级" align="center" prop="dangerRating" /> -->
      <el-table-column label="危险评级" align="center" prop="dangerRating" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_FISH_OPERATION_LOG_DANGER_RATING" :value="scope.row.dangerRating" />
        </template>
      </el-table-column>
      <el-table-column label="评级人" align="center" prop="userName" />
      <el-table-column label="评级时间" align="center" prop="updateTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="false"
                     v-hasPermi="['pl:zk-fish-operation-log:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-fish-operation-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 添加死亡记录：复用死亡明细弹窗，本页内打开，不再跳转 -->
    <DeathRecordFormDialog ref="deathRecordDialog" @success="onDeathRecordSaved" />
  </div>
</template>

<script>
import { createZkFishOperationLog, updateZkFishOperationLog, deleteZkFishOperationLog, getZkFishOperationLog, getZkFishOperationLogPage, exportZkFishOperationLogExcel } from "@/api/fish/zkFishOperationLog";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { getZkBiologicalAssetPage } from "@/api/fish/zkBiologicalAsset";
import { getZkSpecsLogPage } from "@/api/fish/zkSpecsLog";
import { loadDeptScopedPondList, loadSharedSiteOptions } from "@/utils/siteOptions";
import { getPrecisionFeedingPonds } from "@/api/fish/zkBiologyCapital";
import { normalizeSpeciesName } from "@/utils/speciesTempBridge";
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
  name: "ZkFishOperationLogDead",
  components: {
    FileBusUpload,
    DeathRecordFormDialog,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      searchType: -2.4, // 与健康页一致，按池展示车间/系统/池号/品种/批次
      // 鱼苗统计列表
      list: [],
      pondList: [],
      workshopList: [], 
      pondSystemList: [], 
      systemList: [], 
      batchNumberList: [],
      siteOptions: null,
      healthPonds: [],
      filterWorkshop: '',
      filterSystem: '',
      filterPond: '',
      filterSpecies: '',
      filterBatch: '',
      /** 用户主动清空批次后，禁止再自动选中第一个批次 */
      batchFilterCleared: false,
      // 生物性资产规格 / 预估修正规格缓存
      assetSpecByBatch: {},
      assetSpecByPond: {},
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
        batchNumber: null,
        workshopId: null,
        pondSystemId: null,
        pondNum: null,
        pondId: null,
        workshopName: null,
        pondSystemName: null,
        pondName: null,
        logDay: null,
        productNo: null,
        fishTypes: null,
        fishSpec: null,
        fishSpecStr: '',
        dayFishNum: null,
        accumulateFishNum: null,
        pushFishNum: null,
        dayRate: null,
        accumulateRate: null,
        survivalRate: null,
        newDayFishNum: null,
        newAccumulateFishNum: null,
        newPushFishNum: null,
        newDayRate: null,
        newAccumulateRate: null,
        newSurvivalRate: null,
        correctionBasis: null,
        differencesRate: null,
        reasonsForDifferences: null,
        detailedDescription: null,
        dangerRating: null,
        // 与 searchType 对齐，避免首屏 logType 为空导致列表/分页错乱
        logType: '-2.4',
        createTime: [],
        userId: null,
        userName: null,
        deptId: null,
        fid: null,
      },
      fishSpecList: [
        "1-10g",
        "10-50g",
        "50-250g",
        "250-500g",
        "500-1000g",
        "1000-3000g",
        "3000-5000g",
        "5000g以上",
        "全部",
      ],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dayFishNum: [{ required: true, message: "死亡数量不能为空", trigger: "blur" }],
        accumulateFishNum: [{ required: true, message: "累计死亡数量不能为空", trigger: "blur" }],
        pushFishNum: [{ required: true, message: "投放数量不能为空", trigger: "blur" }],
        newDayFishNum: [{ required: true, message: "修正后死亡数量不能为空", trigger: "blur" }],
        newAccumulateFishNum: [{ required: true, message: "修正后累计死亡数量不能为空", trigger: "blur" }],
        newPushFishNum: [{ required: true, message: "修正后投放数量不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
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
    this.loadSpecSources().then(() => {
      this.getList();
    });
    this.getPondList();
    this.loadHealthMaster();
  },
  methods: {
    /** 加载生物性资产、规格日志，用于关联预估/修正规格 */
    loadSpecSources() {
      const assetReq = getZkBiologicalAssetPage({ pageNo: 1, pageSize: 500 }).then(res => {
        const mapBatch = {};
        const mapPond = {};
        (res.data && res.data.list || []).forEach(item => {
          const spec = item.fishSpecification;
          if (spec == null || spec === '') return;
          if (item.batchNumber) mapBatch[String(item.batchNumber)] = spec;
          if (item.pondId != null) mapPond[String(item.pondId)] = spec;
          if (item.pondDetailNum) mapPond[`detail_${item.pondDetailNum}`] = spec;
        });
        this.assetSpecByBatch = mapBatch;
        this.assetSpecByPond = mapPond;
      }).catch(() => {});
      const specsReq = getZkSpecsLogPage({ pageNo: 1, pageSize: 500 }).then(res => {
        const byPondId = {};
        const byDetail = {};
        const list = (res.data && res.data.list || []).slice().sort((a, b) => {
          return Number(b.createTime || 0) - Number(a.createTime || 0);
        });
        list.forEach(item => {
          // 优先打样/修正规格，其次预估规格
          const spec = (item.acturalSpecs != null && item.acturalSpecs !== '')
            ? item.acturalSpecs
            : item.predictSpecs;
          if (spec == null || spec === '') return;
          if (item.pondId != null && byPondId[String(item.pondId)] == null) {
            byPondId[String(item.pondId)] = spec;
          }
          if (item.pondDetailNum && byDetail[String(item.pondDetailNum)] == null) {
            byDetail[String(item.pondDetailNum)] = spec;
          }
        });
        this.specsByPondId = byPondId;
        this.specsByPondDetailNum = byDetail;
      }).catch(() => {});
      return Promise.all([assetReq, specsReq]);
    },
    /** 解析规格数值（g/尾） */
    parseSpecNum(val) {
      if (val == null || val === '') return null;
      if (typeof val === 'number' && !isNaN(val)) return val;
      const m = String(val).match(/(\d+(?:\.\d+)?)/);
      return m ? Number(m[1]) : null;
    },
    /** 按实际规格归入区间 */
    getSpecRange(num) {
      if (num == null || isNaN(num)) return '';
      if (num >= 1 && num < 10) return '1-10g';
      if (num >= 10 && num < 50) return '10-50g';
      if (num >= 50 && num < 250) return '50-250g';
      if (num >= 250 && num < 500) return '250-500g';
      if (num >= 500 && num < 1000) return '500-1000g';
      if (num >= 1000 && num < 3000) return '1000-3000g';
      if (num >= 3000 && num < 5000) return '3000-5000g';
      if (num >= 5000) return '5000g以上';
      return '';
    },
    /** 关联生物性资产 / 预估修正规格 */
    resolveFishSpec(row) {
      if (!row) return '';
      const pondId = row.pondId != null ? String(row.pondId) : '';
      const detail = row.pondDetailNum != null ? String(row.pondDetailNum) : '';
      const batch = row.batchNumber != null ? String(row.batchNumber) : '';
      const fromSpecs = (pondId && this.specsByPondId[pondId])
        || (detail && this.specsByPondDetailNum[detail]);
      const fromAsset = (batch && this.assetSpecByBatch[batch])
        || (pondId && this.assetSpecByPond[pondId])
        || (detail && this.assetSpecByPond[`detail_${detail}`]);
      const raw = fromSpecs != null && fromSpecs !== ''
        ? fromSpecs
        : (fromAsset != null && fromAsset !== '' ? fromAsset : row.fishSpec);
      if (raw == null || raw === '') return '';
      const num = this.parseSpecNum(raw);
      return num != null ? num : raw;
    },
    enrichListWithSpec(list) {
      return (list || []).map(row => {
        const displayFishSpec = this.resolveFishSpec(row);
        return Object.assign({}, row, {
          displayFishSpec,
          fishSpecRange: this.getSpecRange(this.parseSpecNum(displayFishSpec)),
        });
      });
    },
    matchSpecRange(row, rangeStr) {
      if (!rangeStr || rangeStr === '全部') return true;
      const range = row.fishSpecRange || this.getSpecRange(this.parseSpecNum(row.displayFishSpec));
      return range === rangeStr;
    },
    querySearch(item, searchType) {
      if (searchType == -2.1) this.filterBatch = item || '';
      this.onHealthFilterChange();
    },
    displayHealthWorkshop,
    displayHealthSystem,
    displayHealthPond,
    displayHealthSpecies,
    displayHealthBatch,
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
      this.searchType = -2.4;
      this.queryParams.logType = '-2.4';
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
      this.queryParams.logType = '-2.4';
    },
    resolveFishTypeQuery(species) {
      const name = String(species || '').trim();
      if (!name) return null;
      const dicts = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES) || [];
      const hit = dicts.find((d) => normalizeSpeciesName(d.label || d.value) === name || d.label === name);
      return hit ? hit.value : name;
    },
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.workshopList = [];
      this.systemList = [];
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
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.fishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      console.log("===========>"+JSON.stringify(this.form));
    },
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
    getPondList() {
      this.pondList = [];
      return Promise.all([
        loadDeptScopedPondList(),
        loadSharedSiteOptions({ includePonds: true }).catch(() => null),
      ]).then(([list, opts]) => {
        this.siteOptions = opts;
        const rows = Array.isArray(list) ? list.slice() : [];
        this.enrichPondsFromSiteOptions(rows, opts);
        this.pondList = rows;
        this.getWorkshopList();
        return rows;
      }).catch((err) => {
        console.warn('getPondList failed', err);
        this.pondList = [];
        return [];
      });
    },
    toPushLog(obj) {
      this.$router.push({
          path: '/jidi/zk-pond-fish-detail',
          // path: '/fish/zk-paper-question',
          params: {},
          query: obj,
      });
    },
    toLog() {
      this.open = false;
      this.$router.push({
          path: '/jidi/zk-pond-fish-detail-dead',
          params: {},
          query: {  },
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      this.applyHealthQuery();
      let params = JSON.parse(JSON.stringify(this.queryParams));
      const specFilter = params.fishSpecStr;
      // 规格区间按生物性资产/预估修正规格前端分类，不传给后端字符串匹配
      params.fishSpecStr = undefined;
      const needSpecFilter = specFilter && specFilter !== '全部';
      if (needSpecFilter) {
        params.pageNo = 1;
        params.pageSize = 500;
      }
      // 执行查询
      // 维度与后端约定为字符串 "-2.1" 等
      if (params.logType == null || params.logType === '') {
        params.logType = String(this.searchType);
      } else {
        params.logType = String(params.logType);
      }
      getZkFishOperationLogPage(params).then(response => {
        this.batchNumberList = [...(response.other && response.other.batchNumberList || [])];
        let list = response.data.list || [];
        // 默认不选批次，列表展示全部；下拉仅作可选项
        list = this.enrichListWithSpec(list);
        if (needSpecFilter) {
          list = list.filter(row => this.matchSpecRange(row, specFilter));
          this.total = list.length;
          const start = (this.queryParams.pageNo - 1) * this.queryParams.pageSize;
          this.list = list.slice(start, start + this.queryParams.pageSize);
        } else {
          this.list = list;
          this.total = response.data.total != null ? response.data.total : list.length;
        }
        this.loading = false;
      }).catch(() => {
        this.list = [];
        this.total = 0;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        batchNumber: undefined,
        workshopId: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondId: undefined,
        workshopName: undefined,
        pondSystemName: undefined,
        pondName: undefined,
        logDay: undefined,
        productNo: undefined,
        fishTypes: undefined,
        fishSpec: undefined,
        dayFishNum: undefined,
        accumulateFishNum: undefined,
        pushFishNum: undefined,
        dayRate: undefined,
        accumulateRate: undefined,
        survivalRate: undefined,
        newDayFishNum: undefined,
        newAccumulateFishNum: undefined,
        newPushFishNum: undefined,
        newDayRate: undefined,
        newAccumulateRate: undefined,
        newSurvivalRate: undefined,
        correctionBasis: undefined,
        differencesRate: undefined,
        reasonsForDifferences: undefined,
        detailedDescription: undefined,
        dangerRating: undefined,
        logType: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        fid: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮死亡 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮死亡 */
    resetQuery() {
      this.filterWorkshop = '';
      this.filterSystem = '';
      this.filterPond = '';
      this.filterSpecies = '';
      this.filterBatch = '';
      this.resetForm("queryForm");
      this.batchFilterCleared = false;
      this.searchType = -2.4;
      this.queryParams.logType = '-2.4';
      this.handleQuery();
    },
    /** 跳转到死亡日志页并打开新增 */
    /** 本页直接打开「添加死亡记录」弹窗（与死亡日志明细页同源，不再跳转） */
    toAddDeathLog() {
      if (this.$refs.deathRecordDialog) {
        this.$refs.deathRecordDialog.openAdd();
      }
    },
    /** 新增按钮兼容 */
    handleAdd() {
      this.toAddDeathLog();
    },
    onDeathRecordSaved() {
      this.getList();
    },
    /** 修改按钮死亡 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkFishOperationLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改鱼苗统计";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateZkFishOperationLog(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkFishOperationLog(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮死亡 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除鱼苗统计编号为"' + id + '"的数据项?').then(function() {
          return deleteZkFishOperationLog(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮死亡 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      if (!params.fishSpecStr || params.fishSpecStr === '全部') {
        params.fishSpecStr = undefined;
      }
      this.$modal.confirm('是否确认导出所有鱼苗统计数据项?').then(() => {
          this.exportLoading = true;
          return exportZkFishOperationLogExcel(params);
        }).then(response => {
          this.$download.excel(response, '鱼苗统计.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
