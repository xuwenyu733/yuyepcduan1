<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <!-- <el-form-item label="养殖批次ID" prop="batchId">
          <el-input v-model="formData.batchId" placeholder="请输入养殖批次ID" />
        </el-form-item> -->
        <el-form-item label="养殖批次" prop="batchId">
          <el-select style="width:100%;" v-model="formData.batchId" placeholder="请选择养殖批次" filterable clearable  @change="update_GrowFarmingBatchId">
            <el-option v-for="(item, index) in growFarmingBatchList" :key="item.id+'__'+index" :label="item.batchNo"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="养殖天数 (第X天)" prop="dayIndex">
          <el-input v-model="formData.dayIndex" placeholder="请输入养殖天数 (第X天)" />
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker clearable v-model="formData.recordDate" type="date" value-format="timestamp" style="width:100%;"
            placeholder="选择记录日期" />
        </el-form-item>
        <el-form-item label="当日平均水温" prop="waterTemp">
          <el-input v-model="formData.waterTemp" placeholder="请输入当日平均水温" />
        </el-form-item>
        <el-form-item label="当日投饵率 (%)" prop="feedRate">
          <el-input v-model="formData.feedRate" placeholder="请输入当日投饵率 (%)" />
        </el-form-item>
        <el-form-item label="推荐投饵率 (%)" prop="recommendedFeedRate">
          <el-input v-model="formData.recommendedFeedRate" placeholder="请输入推荐投饵率 (%)" />
        </el-form-item>
        <el-form-item label="模型预测体重 (g)" prop="predictedWeight">
          <el-input v-model="formData.predictedWeight" placeholder="请输入模型预测体重 (g)" />
        </el-form-item>
        <el-form-item label="模型预测体长 (cm)" prop="predictedLength">
          <el-input v-model="formData.predictedLength" placeholder="请输入模型预测体长 (cm)" />
        </el-form-item>
        <el-form-item label="视觉识别体重 (g)" prop="visionWeight">
          <el-input v-model="formData.visionWeight" placeholder="请输入视觉识别体重 (g)" />
        </el-form-item>
        <el-form-item label="视觉识别体长 (cm)" prop="visionLength">
          <el-input v-model="formData.visionLength" placeholder="请输入视觉识别体长 (cm)" />
        </el-form-item>
        <el-form-item label="手动测量体重 (g)" prop="manualWeight">
          <el-input v-model="formData.manualWeight" placeholder="请输入手动测量体重 (g)" />
        </el-form-item>
        <el-form-item label="手动测量体长 (cm)" prop="manualLength">
          <el-input v-model="formData.manualLength" placeholder="请输入手动测量体长 (cm)" />
        </el-form-item>
        <el-form-item label="特定生长率 (SGR)" prop="sgr">
          <el-input v-model="formData.sgr" placeholder="请输入特定生长率 (SGR)" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkGrowDailyRecordApi from '@/api/fish/zkgrowdailyrecord';
import * as ZkGrowFarmingBatchApi from '@/api/fish/zkgrowfarmingbatch';
import { UserInfoKey } from "@/utils/constants";
export default {
  name: "ZkGrowDailyRecordForm",
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
        batchId: undefined,
        dayIndex: undefined,
        recordDate: undefined,
        waterTemp: undefined,
        feedRate: undefined,
        predictedWeight: undefined,
        predictedLength: undefined,
        visionWeight: undefined,
        visionLength: undefined,
        manualWeight: undefined,
        manualLength: undefined,
        sgr: undefined,
        recommendedFeedRate: undefined,
      },
      // 表单校验
      formRules: {
        batchId: [{ required: true, message: '养殖批次ID不能为空', trigger: 'blur' }],
        dayIndex: [{ required: true, message: '养殖天数 (第X天)不能为空', trigger: 'blur' }],
      },
      growFarmingBatchList: [], 
    };
  },
  created() {
    this.get_GrowFarmingBatchList();
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
  },
  methods: {
    update_GrowFarmingBatchId(currId){
      let item = null;
      this.growFarmingBatchList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.growFarmingBatchName = item.growFarmingBatchName;
      this.formData.growFarmingBatchId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    get_GrowFarmingBatchList() {
      this.growFarmingBatchList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      ZkGrowFarmingBatchApi.getZkGrowFarmingBatchPage(params).then(response => {
        this.growFarmingBatchList.push(...response.data.list)
        // this.growFarmingBatchList.forEach(obj => {
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
          const res = await ZkGrowDailyRecordApi.getZkGrowDailyRecord(id);
          this.formData = res.data;
          this.title = "修改每日生长与环境记录";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增每日生长与环境记录";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        if (!this.formData.tenantId) this.formData.tenantId = this.userInfo.tenantId;
        if (!this.formData.deptId) this.formData.deptId = this.userInfo.deptId;
        if (!this.formData.userId) this.formData.userId = this.userInfo.userId;
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await ZkGrowDailyRecordApi.updateZkGrowDailyRecord(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkGrowDailyRecordApi.createZkGrowDailyRecord(data);
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
        batchId: undefined,
        dayIndex: undefined,
        recordDate: undefined,
        waterTemp: undefined,
        feedRate: undefined,
        predictedWeight: undefined,
        predictedLength: undefined,
        visionWeight: undefined,
        visionLength: undefined,
        manualWeight: undefined,
        manualLength: undefined,
        sgr: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>
