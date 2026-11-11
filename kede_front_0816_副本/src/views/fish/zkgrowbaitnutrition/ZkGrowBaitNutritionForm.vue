<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <el-form-item label="饵料名称" prop="baitName">
          <el-input v-model="formData.baitName" placeholder="请输入饵料名称" />
        </el-form-item>
        <el-form-item label="厂家名称" prop="manufacturer">
          <el-input v-model="formData.manufacturer" placeholder="请输入厂家名称" />
        </el-form-item>
        <el-form-item label="颗粒大小(mm)" prop="pelletSize">
          <el-input v-model="formData.pelletSize" placeholder="请输入颗粒大小(mm)" />
        </el-form-item>
        <el-form-item label="粗蛋白(%)" prop="crudeProtein">
          <el-input v-model="formData.crudeProtein" placeholder="请输入粗蛋白(%)" />
        </el-form-item>
        <el-form-item label="粗脂肪(%)" prop="crudeFat">
          <el-input v-model="formData.crudeFat" placeholder="请输入粗脂肪(%)" />
        </el-form-item>
        <el-form-item label="无氮浸出物(%)" prop="nitrogenFreeExtract">
          <el-input v-model="formData.nitrogenFreeExtract" placeholder="请输入无氮浸出物(%)" />
        </el-form-item>
        <el-form-item label="灰分(%)">
          <!-- <Editor v-model="formData.ashContent" :min-height="192" /> -->
          <el-input v-model="formData.ashContent" placeholder="请输入灰分(%)" />
        </el-form-item>
        <el-form-item label="粗纤维(%)" prop="crudeFiber">
          <el-input v-model="formData.crudeFiber" placeholder="请输入粗纤维(%)" />
        </el-form-item>
        <el-form-item label="磷(%)" prop="phosphorus">
          <el-input v-model="formData.phosphorus" placeholder="请输入磷(%)" />
        </el-form-item>
        <el-form-item label="总能(MJ)" prop="grossEnergy">
          <el-input v-model="formData.grossEnergy" placeholder="请输入总能(MJ)" />
        </el-form-item>
        <el-form-item label="消化能(MJ)" prop="digestibleEnergy">
          <el-input v-model="formData.digestibleEnergy" placeholder="请输入消化能(MJ)" />
        </el-form-item>
        <el-form-item label="水分(%)" prop="moisture">
          <el-input v-model="formData.moisture" placeholder="请输入水分(%)" />
        </el-form-item>
        <el-form-item label="钙(%)" prop="calcium">
          <el-input v-model="formData.calcium" placeholder="请输入钙(%)" />
        </el-form-item>
        <el-form-item label="赖氨酸(%)" prop="lysine">
          <el-input v-model="formData.lysine" placeholder="请输入赖氨酸(%)" />
        </el-form-item>
        <el-form-item label="蛋氨酸(%)" prop="methionine">
          <el-input v-model="formData.methionine" placeholder="请输入蛋氨酸(%)" />
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
import * as ZkGrowBaitNutritionApi from '@/api/fish/zkgrowbaitnutrition';
import Editor from '@/components/Editor';
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkGrowBaitNutritionForm",
  components: {
    Editor,
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
        baitName: undefined,
        manufacturer: undefined,
        pelletSize: undefined,
        crudeProtein: undefined,
        crudeFat: undefined,
        nitrogenFreeExtract: undefined,
        ashContent: undefined,
        crudeFiber: undefined,
        phosphorus: undefined,
        grossEnergy: undefined,
        digestibleEnergy: undefined,
        moisture: undefined,
        calcium: undefined,
        lysine: undefined,
        methionine: undefined,
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
          const res = await ZkGrowBaitNutritionApi.getZkGrowBaitNutrition(id);
          this.formData = res.data;
          this.title = "修改饵料营养成分";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增饵料营养成分";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        if(!this.formData.tenantId) this.formData.tenantId = this.userInfo.tenantId;
        if(!this.formData.userId) this.formData.userId = this.userInfo.userId;
        if(!this.formData.deptId) this.formData.deptId = this.userInfo.deptId;
        
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await ZkGrowBaitNutritionApi.updateZkGrowBaitNutrition(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkGrowBaitNutritionApi.createZkGrowBaitNutrition(data);
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
        baitName: undefined,
        pelletSize: undefined,
        crudeProtein: undefined,
        crudeFat: undefined,
        nitrogenFreeExtract: undefined,
        ashContent: undefined,
        crudeFiber: undefined,
        phosphorus: undefined,
        grossEnergy: undefined,
        digestibleEnergy: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>