<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="生长阶段" prop="growthStage">
          <el-input v-model="formData.growthStage" placeholder="请输入生长阶段" />
        </el-form-item>
        <el-form-item label="日龄" prop="dayold">
          <el-input v-model="formData.dayold" placeholder="请输入日龄" />
        </el-form-item>
        <el-form-item label="体重范围" prop="weightRange">
          <el-input v-model="formData.weightRange" placeholder="请输入体重范围" />
        </el-form-item>
        <el-form-item label="粗蛋白" prop="crudeProtein">
          <el-input v-model="formData.crudeProtein" placeholder="请输入粗蛋白" />
        </el-form-item>
        <el-form-item label="粗脂肪" prop="crudeFat">
          <el-input v-model="formData.crudeFat" placeholder="请输入粗脂肪" />
        </el-form-item>
        <el-form-item label="碳水化合物" prop="carbohydrate">
          <el-input v-model="formData.carbohydrate" placeholder="请输入碳水化合物" />
        </el-form-item>
        <el-form-item label="粗纤维" prop="crudeFiber">
          <el-input v-model="formData.crudeFiber" placeholder="请输入粗纤维" />
        </el-form-item>
        <el-form-item label="钙" prop="calcium">
          <el-input v-model="formData.calcium" placeholder="请输入钙" />
        </el-form-item>
        <el-form-item label="磷" prop="phosphorus">
          <el-input v-model="formData.phosphorus" placeholder="请输入磷" />
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
import * as ZkGrowNutritionalRequirementsApi from '@/api/fish/zkgrownutritionalrequirements';
import { UserInfoKey } from "@/utils/constants";
export default {
  name: "ZkGrowNutritionalRequirementsForm",
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
        growthStage: undefined,
        dayold: undefined,
        weightRange: undefined,
        crudeProtein: undefined,
        crudeFat: undefined,
        carbohydrate: undefined,
        crudeFiber: undefined,
        calcium: undefined,
        phosphorus: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
      },
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
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
          const res = await ZkGrowNutritionalRequirementsApi.getZkGrowNutritionalRequirements(id);
          this.formData = res.data;
          this.title = "修改各阶段营养需求";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增各阶段营养需求";
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
          await ZkGrowNutritionalRequirementsApi.updateZkGrowNutritionalRequirements(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkGrowNutritionalRequirementsApi.createZkGrowNutritionalRequirements(data);
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
        growthStage: undefined,
        dayold: undefined,
        weightRange: undefined,
        crudeProtein: undefined,
        crudeFat: undefined,
        carbohydrate: undefined,
        crudeFiber: undefined,
        calcium: undefined,
        phosphorus: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>
