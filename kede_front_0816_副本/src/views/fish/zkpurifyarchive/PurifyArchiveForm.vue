<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <!-- <el-form-item label="关联采购记录ID" prop="purchaseRecordId">
          <el-input v-model="formData.purchaseRecordId" placeholder="请输入关联采购记录ID" />
        </el-form-item> -->
        <!-- <el-form-item label="关联采购单号" prop="purchaseOrderNo">
          <el-input v-model="formData.purchaseOrderNo" placeholder="请输入关联采购单号" />
        </el-form-item> -->
        <el-form-item label="关联采购单号" prop="purchaseOrderNo">
          <el-select style="width:100%;" v-model="formData.purchaseOrderNo" placeholder="请选择关联采购单号" filterable clearable  @change="updatePurifyPurchaseId">
            <el-option v-for="(item, index) in purifyPurchaseList" :key="item.id+'__'+index" :label="`${item.purchaseOrderNo}-${item.categoryName}-${item.quantity}Kg`"
              :value="item.purchaseOrderNo"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="品类" prop="category">
          <el-input v-model="formData.category" placeholder="请输入品类" />
        </el-form-item> -->
        <el-form-item label="品类" prop="category">
          <el-select v-model="formData.category" placeholder="请输入品类" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="formData.source" placeholder="请输入来源" />
        </el-form-item>
        <el-form-item label="入池时间" prop="inPoolTime">
          <el-date-picker clearable v-model="formData.inPoolTime" type="date" value-format="timestamp" style="width: 100%;"
            placeholder="选择入池时间" />
        </el-form-item>
        <el-form-item label="入池总重量(kg)" prop="inPoolWeight">
          <el-input v-model="formData.inPoolWeight" placeholder="请输入入池总重量(kg)" />
        </el-form-item>
        <el-form-item label="规格-单尾重(kg)" prop="avgWeight">
          <el-input v-model="formData.avgWeight" placeholder="请输入规格-单尾重(kg)" />
        </el-form-item>
        <el-form-item label="入池尾数" prop="inPoolCount">
          <el-input v-model="formData.inPoolCount" placeholder="请输入入池尾数" />
        </el-form-item>
        <!-- <el-form-item label="池号" prop="poolNumber">
          <el-input v-model="formData.poolNumber" placeholder="请输入池号" />
        </el-form-item> -->
        <el-form-item label="池号" prop="poolNumber">
          <el-select style="width:100%;" v-model="formData.poolNumber" placeholder="请选择池号" filterable clearable  @change="updatePurifyPoolId">
            <el-option v-for="(item, index) in purifyPoolList" :key="item.id+'__'+index" :label="item.poolNumber"
              :value="item.poolNumber"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="初始密度(kg/m³)" prop="density">
          <el-input v-model="formData.density" placeholder="请输入初始密度(kg/m³)" />
        </el-form-item>
        <!-- <el-form-item label="优化等级(premium/wagyu/tuna)" prop="optimizationLevel">
          <el-input v-model="formData.optimizationLevel" placeholder="请输入优化等级(premium/wagyu/tuna)" />
        </el-form-item> -->
        <el-form-item label="优化等级" prop="optimizationLevel">
          <el-select v-model="formData.optimizationLevel" placeholder="请输入优化等级" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_OPTIMIZATION_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="检验报告文件名/URL" prop="inspectionReport">
          <el-input v-model="formData.inspectionReport" placeholder="请输入检验报告文件名/URL" />
        </el-form-item> -->
        <el-form-item label="检验报告" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="formData.fid" bus-type="zk_purify_archive" @uploadSuccess="confirmUrl"></FileBusUpload>
        </el-form-item>
        <el-form-item label="初始溶氧(mg/L)" prop="initialOxygen">
          <el-input v-model="formData.initialOxygen" placeholder="请输入初始溶氧(mg/L)" />
        </el-form-item>
        <el-form-item label="初始PH值" prop="initialPh">
          <el-input v-model="formData.initialPh" placeholder="请输入初始PH值" />
        </el-form-item>
        <el-form-item label="初始水温(℃)" prop="initialTemperature">
          <el-input v-model="formData.initialTemperature" placeholder="请输入初始水温(℃)" />
        </el-form-item>
        <!-- <el-form-item label="外观评估" prop="appearanceScore">
          <el-input v-model="formData.appearanceScore" placeholder="请输入外观评估" />
        </el-form-item> -->
        <el-form-item label="外观评估" prop="appearanceScore">
          <el-select v-model="formData.appearanceScore" placeholder="请输入外观评估" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_SHOW_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="活力评估" prop="vitalityScore">
          <el-input v-model="formData.vitalityScore" placeholder="请输入活力评估" />
        </el-form-item> -->
        <el-form-item label="活力评估" prop="vitalityScore">
          <el-select v-model="formData.vitalityScore" placeholder="请输入活力评估" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_SHOW_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="健康状况" prop="healthStatus">
          <el-radio-group v-model="formData.healthStatus">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="健康状况" prop="healthStatus">
          <el-select v-model="formData.healthStatus" placeholder="请输入健康状况" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_SHOW_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格均匀度" prop="specUniformity">
          <el-select v-model="formData.specUniformity" placeholder="请输入规格均匀度" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_SHOW_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="应激程度" prop="stressLevel">
          <el-select v-model="formData.stressLevel" placeholder="请输入应激程度" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_HIGH_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预期存活率(%)" prop="expectedSurvivalRate">
          <el-input v-model="formData.expectedSurvivalRate" placeholder="请输入预期存活率(%)" />
        </el-form-item>
        <!-- <el-form-item label="风险等级" prop="riskLevel">
          <el-input v-model="formData.riskLevel" placeholder="请输入风险等级" />
        </el-form-item> -->
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="formData.riskLevel" placeholder="请输入风险等级" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_HIGH_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="评估人" prop="assessor">
          <el-input v-model="formData.assessor" placeholder="请输入评估人" />
        </el-form-item>
        <!-- <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="formData.deptId" placeholder="请输入所属部门" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import * as PurifyPoolApi from '@/api/fish/zkpurifypool';
import * as PurifyPurchaseApi from '@/api/fish/zkpurifypurchase';
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
export default {
  name: "PurifyArchiveForm",
  components: {
    FileBusUpload,
  },
  data() {
    return {
      userInfo: {},
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        purchaseRecordId: undefined,
        purchaseOrderNo: undefined,
        category: undefined,
        source: undefined,
        inPoolTime: undefined,
        inPoolWeight: undefined,
        avgWeight: undefined,
        inPoolCount: undefined,
        poolNumber: undefined,
        density: undefined,
        optimizationLevel: undefined,
        inspectionReport: undefined,
        initialOxygen: undefined,
        initialPh: undefined,
        initialTemperature: undefined,
        appearanceScore: undefined,
        vitalityScore: undefined,
        healthStatus: undefined,
        expectedSurvivalRate: undefined,
        riskLevel: undefined,
        assessor: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        purchaseOrderNo: [{ required: true, message: "关联采购单号不能为空", trigger: "change" }],
        category: [{ required: true, message: "品类不能为空", trigger: "change" }],
        source: [{ required: true, message: "来源不能为空", trigger: "blur" }],
        inPoolTime: [{ required: true, message: "入池时间不能为空", trigger: "change" }],
        inPoolWeight: [{ required: true, message: "入池总重量不能为空", trigger: "blur" }],
        avgWeight: [{ required: true, message: "规格单尾重不能为空", trigger: "blur" }],
        poolNumber: [{ required: true, message: "池号不能为空", trigger: "change" }],
        optimizationLevel: [{ required: true, message: "优化等级不能为空", trigger: "change" }],
        appearanceScore: [{ required: true, message: "外观评估不能为空", trigger: "change" }],
        vitalityScore: [{ required: true, message: "活力评估不能为空", trigger: "change" }],
        healthStatus: [{ required: true, message: "健康状况不能为空", trigger: "change" }],
        specUniformity: [{ required: true, message: "规格均匀度不能为空", trigger: "change" }],
        stressLevel: [{ required: true, message: "应激程度不能为空", trigger: "change" }],
        expectedSurvivalRate: [{ required: true, message: "预期存活率不能为空", trigger: "blur" }],
        riskLevel: [{ required: true, message: "风险等级不能为空", trigger: "change" }],
        assessor: [{ required: true, message: "评估人不能为空", trigger: "blur" }],
      },
      purifyPoolList: [], 
      purifyPurchaseList: [], 
      categoryList: [], 
      fileUrls: [],
    };
  },
  watch: {
    'formData.inPoolWeight': function() { this.calculateCountAndDensity(); },
    'formData.avgWeight': function() { this.calculateCountAndDensity(); },
    'formData.poolNumber': function() { this.calculateCountAndDensity(); }
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // this.getPurifyPurchaseList();
    // this.get_PurifyPoolList();
    this.init();
    this.categoryList = this.getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY);
    // console.log(`categoryList=============>${JSON.stringify(this.categoryList)}`);
  },
  methods: {
    async init() {
      await this.getPurifyPurchaseList();
      await this.get_PurifyPoolList();
    },
    confirmUrl(obj='') {
      let name = obj.name;
      let url = obj.url;
      // console.log(`confirmUrl=============>${JSON.stringify(obj)}`);
      if(!obj) return;
      this.fileUrls.push(url);
      // this.fileNames.push(name);
      this.formData.inspectionReport = this.fileUrls.join(",");
      // this.formData.fileNames = this.fileNames.join(",");
      // console.log("confirmUrl=====>"+JSON.stringify(this.formData));
    },
    calculateCountAndDensity() {
      if (this.formData.inPoolWeight && this.formData.avgWeight) {
        this.formData.inPoolCount = Math.round(parseFloat(this.formData.inPoolWeight) / parseFloat(this.formData.avgWeight));
      }
      if (this.formData.inPoolWeight && this.formData.poolNumber) {
        const pool = this.purifyPoolList.find(p => p.poolNumber === this.formData.poolNumber);
        const poolSize = pool ? pool.size : 300;
        this.formData.density = (parseFloat(this.formData.inPoolWeight) / poolSize).toFixed(2);
      }
    },
    updatePurifyPurchaseId(currId){
      let item = null;
      this.purifyPurchaseList.forEach(obj => {
        if(obj.purchaseOrderNo == currId) {
          item = obj;
          return;
        }
      })
      if (item) {
        this.formData.purchaseRecordId = item.id;
        this.formData.category = item.category;
        this.formData.source = item.source;
        this.formData.avgWeight = item.avgWeight;
        this.formData.inPoolWeight = item.quantity;
        this.$modal.msgSuccess("已根据采购单自动填充数据");
      }
    },
    getPurifyPurchaseList() {
      this.purifyPurchaseList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        PurifyPurchaseApi.getPurifyPurchasePage(params).then(response => {
          this.purifyPurchaseList.push(...response.data.list)
          this.my_common.list2listHandle(this.categoryList, "value", this.purifyPurchaseList, "category", (o, t) => {
            t["categoryName"] = o["label"];
          });
          resolve(this.purifyPurchaseList);
          // console.log("getPurifyPurchaseList===========>"+JSON.stringify(this.purifyPurchaseList));
        })
      });
      return promise;
    },
    updatePurifyPoolId(currId){
      let item = null;
      this.purifyPoolList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      // this.form.purifyPoolName = item.purifyPoolName;
      // this.formData.purifyPoolId = item.id;
      // console.log("===========>"+JSON.stringify(this.formData));
    },
    get_PurifyPoolList() {
      this.purifyPoolList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        PurifyPoolApi.getPurifyPoolPage(params).then(response => {
          this.purifyPoolList.push(...response.data.list)
          resolve(this.purifyPoolList);
          // this.purifyPoolList.forEach(obj => {
          //   let tempStr = `${obj.deptName}-${obj.nickname}`;
          //   obj.nickname = tempStr;
          //   console.log("===========>"+JSON.stringify(obj));
          // })
        })
      });
      return promise;
    },
    /** 打开弹窗 */
    async open(id) {
      await this.init();
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await PurifyArchiveApi.getPurifyArchive(id);
          this.formData = res.data;
          if (!this.formData.fid) {
            this.formData.fid = getUUID().replaceAll("-", "");
          }
          this.title = "修改入池档案";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增入池档案";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        if(!this.formData.tenantId) this.formData.tenantId = this.userInfo.tenantId;
        if(!this.formData.deptId) this.formData.deptId = this.userInfo.deptId;
        if(!this.formData.userId) this.formData.userId = this.userInfo.userId;
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await PurifyArchiveApi.updatePurifyArchive(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyArchiveApi.createPurifyArchive(data);
        this.$modal.msgSuccess("新增成功");
        this.dialogVisible = false;
        this.$emit('success');
      } finally {
        this.formLoading = false;
      }
    },
    /** 表单重置 */
    reset() {
      this.formData = {
        id: undefined,
        purchaseRecordId: undefined,
        purchaseOrderNo: undefined,
        category: undefined,
        source: undefined,
        inPoolTime: undefined,
        inPoolWeight: undefined,
        avgWeight: undefined,
        inPoolCount: undefined,
        poolNumber: undefined,
        density: undefined,
        optimizationLevel: undefined,
        inspectionReport: undefined,
        initialOxygen: undefined,
        initialPh: undefined,
        initialTemperature: undefined,
        appearanceScore: undefined,
        vitalityScore: undefined,
        healthStatus: undefined,
        expectedSurvivalRate: undefined,
        riskLevel: undefined,
        assessor: undefined,
        deptId: undefined,
        fid: getUUID().replaceAll("-", ""),
      };
      this.resetForm("formRef");
    }
  }
};
</script>