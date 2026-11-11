<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <el-form-item label="车间id" prop="workshopId">
          <el-input v-model="formData.workshopId" placeholder="请输入车间id" />
        </el-form-item>
        <el-form-item label="系统id" prop="systemId">
          <el-input v-model="formData.systemId" placeholder="请输入系统id" />
        </el-form-item>
        <el-form-item label="存塘id" prop="pondId">
          <el-input v-model="formData.pondId" placeholder="请输入存塘id" />
        </el-form-item>
        <!-- <el-form-item label="批次编号" prop="batchNumber">
          <el-input v-model="formData.batchNumber" placeholder="请输入批次编号" />
        </el-form-item> -->
        <el-form-item label="统计日期" prop="staticDay">
          <el-input v-model="formData.staticDay" placeholder="请输入统计日期" />
        </el-form-item>
        <!-- <el-form-item label="鱼苗数量" prop="fishNum">
          <el-input v-model="formData.fishNum" placeholder="请输入鱼苗数量" />
        </el-form-item>
        <el-form-item label="鱼苗规格" prop="fishSpec">
          <el-input v-model="formData.fishSpec" placeholder="请输入鱼苗规格" />
        </el-form-item> -->
        <el-form-item label="今日鱼增加重量" prop="fishGainCount">
          <el-input v-model="formData.fishGainCount" placeholder="请输入今日鱼增加重量" />
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
import * as ZkPondGainLogApi from '@/api/fish/zkpondgainlog';
export default {
  name: "ZkPondGainLogForm",
  components: {
  },
  data() {
    return {
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        workshopId: undefined,
        systemId: undefined,
        pondId: undefined,
        batchNumber: undefined,
        staticDay: undefined,
        fishNum: undefined,
        fishSpec: undefined,
        fishGainCount: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
      },
    };
  },
  methods: {
    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await ZkPondGainLogApi.getZkPondGainLog(id);
          this.formData = res.data;
          this.title = "修改鱼生长";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增鱼生长";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await ZkPondGainLogApi.updateZkPondGainLog(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkPondGainLogApi.createZkPondGainLog(data);
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
        workshopId: undefined,
        systemId: undefined,
        pondId: undefined,
        batchNumber: undefined,
        staticDay: undefined,
        fishNum: undefined,
        fishSpec: undefined,
        fishGainCount: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>