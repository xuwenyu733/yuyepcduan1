<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="品种名称 (如:大西洋鲑鱼)" prop="speciesName">
          <el-input v-model="formData.speciesName" placeholder="请输入品种名称 (如:大西洋鲑鱼)" />
        </el-form-item>
        <el-form-item label="质量等级标签 (如:生食级、优质级)" prop="qualityGrade">
          <el-input v-model="formData.qualityGrade" placeholder="请输入质量等级标签 (如:生食级、优质级)" />
        </el-form-item>
        <el-form-item label="学名 (如:Salmo salar)" prop="scientificName">
          <el-input v-model="formData.scientificName" placeholder="请输入学名 (如:Salmo salar)" />
        </el-form-item>
        <el-form-item label="适宜水温 (如:10-16°C)" prop="suitableTemp">
          <el-input v-model="formData.suitableTemp" placeholder="请输入适宜水温 (如:10-16°C)" />
        </el-form-item>
        <el-form-item label="养殖周期 (如:18-24月)" prop="breedingCycle">
          <el-input v-model="formData.breedingCycle" placeholder="请输入养殖周期 (如:18-24月)" />
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
import * as ZkBreedSpeciesApi from '@/api/fish/zkbreedspecies';
import { UserInfoKey } from "@/utils/constants";
export default {
  name: "ZkBreedSpeciesForm",
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
        speciesName: undefined,
        qualityGrade: undefined,
        scientificName: undefined,
        suitableTemp: undefined,
        breedingCycle: undefined,
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
          const res = await ZkBreedSpeciesApi.getZkBreedSpecies(id);
          this.formData = res.data;
          this.title = "修改养殖品种";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增养殖品种";
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
          await ZkBreedSpeciesApi.updateZkBreedSpecies(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkBreedSpeciesApi.createZkBreedSpecies(data);
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
        speciesName: undefined,
        qualityGrade: undefined,
        scientificName: undefined,
        suitableTemp: undefined,
        breedingCycle: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>