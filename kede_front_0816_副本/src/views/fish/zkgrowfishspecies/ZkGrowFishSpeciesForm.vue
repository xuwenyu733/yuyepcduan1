<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="品种名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入品种名称" />
        </el-form-item>
        <el-form-item label="俗名" prop="commonName">
          <el-input v-model="formData.commonName" placeholder="请输入俗名" />
        </el-form-item>
        <el-form-item label="学名" prop="scientificName">
          <el-input v-model="formData.scientificName" placeholder="请输入学名" />
        </el-form-item>
        <el-form-item label="品种描述">
          <Editor v-model="formData.description" :min-height="192" />
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
import * as ZkGrowFishSpeciesApi from '@/api/fish/zkgrowfishspecies';
import Editor from '@/components/Editor';
import { UserInfoKey } from "@/utils/constants";
export default {
  name: "ZkGrowFishSpeciesForm",
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
        name: undefined,
        commonName: undefined,
        scientificName: undefined,
        description: undefined,
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
          const res = await ZkGrowFishSpeciesApi.getZkGrowFishSpecies(id);
          this.formData = res.data;
          this.title = "修改鱼类品种";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增鱼类品种";
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
          await ZkGrowFishSpeciesApi.updateZkGrowFishSpecies(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkGrowFishSpeciesApi.createZkGrowFishSpecies(data);
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
        name: undefined,
        commonName: undefined,
        scientificName: undefined,
        description: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>
