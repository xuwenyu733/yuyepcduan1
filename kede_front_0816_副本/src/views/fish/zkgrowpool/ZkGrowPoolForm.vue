<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="池号" prop="poolNumber">
          <el-input v-model="formData.poolNumber" placeholder="请输入池号" />
        </el-form-item>
        <el-form-item label="大小(m³)" prop="size">
          <el-input v-model="formData.size" placeholder="请输入大小(m³)" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-input v-model="formData.manager" placeholder="请输入负责人" />
        </el-form-item>
        <!-- (available:空闲, in_use:使用中, maintenance:维护中) -->
        <!-- <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_POOL_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="formData.notes" placeholder="请输入备注" />
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
import * as ZkGrowPoolApi from '@/api/fish/zkgrowpool';
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkGrowPoolForm",
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
        poolNumber: undefined,
        size: undefined,
        location: undefined,
        manager: undefined,
        status: undefined,
        notes: undefined,
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
          const res = await ZkGrowPoolApi.getZkGrowPool(id);
          this.formData = res.data;
          this.title = "修改生长模型鱼池";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增生长模型鱼池";
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
          await ZkGrowPoolApi.updateZkGrowPool(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkGrowPoolApi.createZkGrowPool(data);
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
        poolNumber: undefined,
        size: undefined,
        location: undefined,
        manager: undefined,
        status: undefined,
        notes: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>