<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="计划年份" prop="planYear">
          <el-input v-model="formData.planYear" placeholder="请输入计划年份 (如:2026)" />
        </el-form-item>
        <!-- <el-form-item label="关联的品种ID" prop="speciesId">
          <el-input v-model="formData.speciesId" placeholder="请输入关联的品种ID" />
        </el-form-item> -->
        <el-form-item label="品种名称" prop="speciesName">
          <el-input v-model="formData.speciesName" placeholder="请输入品种名称 (如:大西洋鲑鱼)" />
        </el-form-item>
        <el-form-item label="当前产能" prop="currentCapacity">
          <el-input v-model="formData.currentCapacity" placeholder="请输入当前产能/产量 (单位:吨)" />
        </el-form-item>
        <el-form-item label="目标产能" prop="targetCapacity">
          <el-input v-model="formData.targetCapacity" placeholder="请输入目标产能 (单位:吨)" />
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
import * as ZkAnnualCapacityPlanApi from '@/api/fish/zkannualcapacityplan';
import { UserInfoKey } from "@/utils/constants";
export default {
  name: "ZkAnnualCapacityPlanForm",
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
        planYear: undefined,
        speciesId: undefined,
        speciesName: undefined,
        currentCapacity: undefined,
        targetCapacity: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
      },
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`============>userInfo:${JSON.stringify(this.userInfo)}`);
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
          const res = await ZkAnnualCapacityPlanApi.getZkAnnualCapacityPlan(id);
          this.formData = res.data;
          this.title = "修改年度产能计划";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增年度产能计划";
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
          await ZkAnnualCapacityPlanApi.updateZkAnnualCapacityPlan(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkAnnualCapacityPlanApi.createZkAnnualCapacityPlan(data);
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
        planYear: undefined,
        speciesId: undefined,
        speciesName: undefined,
        currentCapacity: undefined,
        targetCapacity: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>