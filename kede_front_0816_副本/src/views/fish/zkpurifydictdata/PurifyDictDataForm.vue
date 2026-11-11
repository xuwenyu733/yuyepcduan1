<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <!-- <el-form-item label="字典类型(category:品类, source:来源, spec:规格)" prop="dictType">
          <el-select v-model="formData.dictType" placeholder="请选择字典类型(category:品类, source:来源, spec:规格)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="formData.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="formData.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="formData.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="formData.sort" placeholder="请输入排序" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" placeholder="请输入备注" />
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
import * as PurifyDictDataApi from '@/api/fish/zkpurifydictdata';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "PurifyDictDataForm",
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
        dictType: undefined,
        dictLabel: undefined,
        dictValue: undefined,
        sort: undefined,
        remark: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        dictType: [{ required: true, message: '字典类型(category:品类, source:来源, spec:规格)不能为空', trigger: 'change' }],
        dictLabel: [{ required: true, message: '字典标签不能为空', trigger: 'blur' }],
        dictValue: [{ required: true, message: '字典键值不能为空', trigger: 'blur' }],
      },
    };
  },
  mounted() {
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
          const res = await PurifyDictDataApi.getPurifyDictData(id);
          this.formData = res.data;
          this.title = "修改业务字典明细";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增业务字典明细";
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
          await PurifyDictDataApi.updatePurifyDictData(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyDictDataApi.createPurifyDictData(data);
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
        dictType: undefined,
        dictLabel: undefined,
        dictValue: undefined,
        sort: undefined,
        remark: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>