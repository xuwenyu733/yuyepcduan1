<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <!-- <el-form-item label="关联流程ID" prop="processId">
          <el-input v-model="formData.processId" placeholder="请输入关联流程ID" />
        </el-form-item> -->
        <!-- <el-form-item label="关联档案" prop="archiveId">
          <el-select style="width:100%;" v-model="formData.archiveId" placeholder="请选择关联档案" filterable clearable  @change="updatePurifyArchiveId">
            <el-option v-for="(item, index) in purifyArchiveList" :key="item.id+'__'+index" :label="`${item.purchaseOrderNo}-${item.source}-${item.inPoolWeight}Kg-${item.inPoolCount}尾`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="关联流程" prop="processId">
          <el-select style="width:100%;" v-model="formData.processId" placeholder="请选择关联流程" filterable clearable  @change="updatePurifyProcessId">
            <el-option v-for="(item, index) in purifyProcessList" :key="item.id+'__'+index" :label="`${item.recordNumber}`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="监测日期" prop="monitorDate">
          <el-date-picker clearable v-model="formData.monitorDate" type="datetime" value-format="timestamp" style="width: 100%;"
            placeholder="选择监测日期" />
        </el-form-item>
        <el-form-item label="溶氧(mg/L)" prop="oxygen">
          <el-input v-model="formData.oxygen" placeholder="请输入溶氧(mg/L)" />
        </el-form-item>
        <el-form-item label="PH值" prop="ph">
          <el-input v-model="formData.ph" placeholder="请输入PH值" />
        </el-form-item>
        <el-form-item label="水温(℃)" prop="temperature">
          <el-input v-model="formData.temperature" placeholder="请输入水温(℃)" />
        </el-form-item>
        <el-form-item label="投喂量(kg)" prop="feedAmount">
          <el-input v-model="formData.feedAmount" placeholder="请输入投喂量(kg)" />
        </el-form-item>
        <el-form-item label="投喂时间" prop="feedTime">
          <el-date-picker clearable v-model="formData.feedTime" type="datetime" value-format="timestamp" style="width: 100%;"
            placeholder="选择投喂时间" />
        </el-form-item>
        <!-- <el-form-item label="健康状况" prop="healthStatus">
          <el-radio-group v-model="formData.healthStatus">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="健康状况" prop="healthStatus">
          <el-select v-model="formData.healthStatus" placeholder="请输入健康状况" filterable clearable size="small" style="width: 100%;"> 
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_DAILY_MONITORING_HEALTH_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="当日死亡数(尾)" prop="deathCount">
          <el-input v-model="formData.deathCount" placeholder="请输入当日死亡数" />
        </el-form-item>
        <el-form-item label="当日死亡重量(Kg)" prop="deathWeight">
          <el-input v-model="formData.deathWeight" placeholder="请输入当日死亡重量" />
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
import * as PurifyDailyMonitoringApi from '@/api/fish/zkpurifydailymonitoring';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
export default {
  name: "PurifyDailyMonitoringForm",
  components: {
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
        processId: undefined,
        monitorDate: undefined,
        oxygen: undefined,
        ph: undefined,
        temperature: undefined,
        feedAmount: undefined,
        feedTime: undefined,
        healthStatus: undefined,
        deathCount: 0,
        deathWeight: 0,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        processId: [{ required: true, message: '关联流程ID不能为空', trigger: 'blur' }],
        monitorDate: [{ required: true, message: "监测日期不能为空", trigger: "change" }],
        feedAmount: [{ required: true, message: "投喂量不能为空", trigger: "blur" }],
        feedTime: [{ required: true, message: "投喂时间不能为空", trigger: "change" }],
        healthStatus: [{ required: true, message: "健康状况不能为空", trigger: "change" }],
      },
      purifyArchiveList: [],
      purifyProcessList: [], 
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getPurifyProcessList();
  },
  methods: {
    updatePurifyProcessId(currId){
      let item = null;
      this.purifyProcessList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.purifyProcessName = item.purifyProcessName;
      this.formData.purifyProcessId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPurifyProcessList() {
      this.purifyProcessList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      PurifyProcessApi.getPurifyProcessPage(params).then(response => {
        this.purifyProcessList.push(...response.data.list)
        // this.purifyProcessList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },
    updatePurifyArchiveId(currId) {
      let item = null;
      this.purifyArchiveList.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.purifyArchiveName = item.purifyArchiveName;
      this.formData.purifyArchiveId = item.id;
      // console.log("===========>" + JSON.stringify(this.form));
    },
    getPurifyArchiveList() {
      this.purifyArchiveList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      PurifyArchiveApi.getPurifyArchivePage(params).then(response => {
        this.purifyArchiveList.push(...response.data.list);
        // console.log(`purifyArchiveList==================>${JSON.stringify(this.purifyArchiveList)}`);
        // this.purifyArchiveList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },
    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await PurifyDailyMonitoringApi.getPurifyDailyMonitoring(id);
          this.formData = res.data;
          this.dialogTitle = "修改日常监测记录";
        } finally {
          this.formLoading = false;
        }
      } else {
        this.dialogTitle = "新增日常监测记录";
      }
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
          await PurifyDailyMonitoringApi.updatePurifyDailyMonitoring(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyDailyMonitoringApi.createPurifyDailyMonitoring(data);
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
        processId: undefined,
        monitorDate: undefined,
        oxygen: undefined,
        ph: undefined,
        temperature: undefined,
        feedAmount: undefined,
        feedTime: undefined,
        healthStatus: undefined,
        deathCount: undefined,
        deathWeight: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>