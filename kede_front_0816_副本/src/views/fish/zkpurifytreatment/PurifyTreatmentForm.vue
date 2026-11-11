<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <!-- <el-form-item label="关联流程ID" prop="processId">
          <el-input v-model="formData.processId" placeholder="请输入关联流程ID" />
        </el-form-item> -->
        <el-form-item label="关联流程" prop="processId">
          <el-select style="width:100%;" v-model="formData.processId" placeholder="请选择关联流程" filterable clearable  @change="updatePurifyProcessId">
            <el-option v-for="(item, index) in purifyProcessList" :key="item.id+'__'+index" :label="`${item.recordNumber}`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理日期" prop="treatmentDate">
          <el-date-picker clearable v-model="formData.treatmentDate" type="datetime" value-format="timestamp" style="width: 100%;"
            placeholder="选择处理日期" />
        </el-form-item>
        <!-- <el-form-item label="措施类型(用药/水质调节等)" prop="type">
          <el-select v-model="formData.type" placeholder="请选择措施类型(用药/水质调节等)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="措施类型" prop="type">
          <el-select v-model="formData.type" placeholder="请输入措施类型" filterable clearable size="small" style="width: 100%;"> 
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_TREATMENT_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="措施内容">
          <Editor v-model="formData.content" :min-height="192" />
        </el-form-item>
        <el-form-item label="剂量" prop="dosage">
          <el-input v-model="formData.dosage" placeholder="请输入剂量" />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="formData.operator" placeholder="请输入操作人" />
        </el-form-item>
        <el-form-item label="备注说明" prop="notes">
          <el-input v-model="formData.notes" placeholder="请输入备注说明" />
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
import * as PurifyTreatmentApi from '@/api/fish/zkpurifytreatment';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import Editor from '@/components/Editor';
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
export default {
  name: "PurifyTreatmentForm",
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
        processId: undefined,
        treatmentDate: undefined,
        type: undefined,
        content: undefined,
        dosage: undefined,
        operator: undefined,
        notes: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        processId: [{ required: true, message: '关联流程ID不能为空', trigger: 'blur' }],
        treatmentDate: [{ required: true, message: "处理日期不能为空", trigger: "change" }],
        type: [{ required: true, message: "措施类型不能为空", trigger: "change" }],
        operator: [{ required: true, message: "操作人不能为空", trigger: "blur" }],
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
    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await PurifyTreatmentApi.getPurifyTreatment(id);
          this.formData = res.data;
          this.dialogTitle = "修改处理措施记录";
        } finally {
          this.formLoading = false;
        }
      } else {
        this.dialogTitle = "新增处理措施记录";
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
          await PurifyTreatmentApi.updatePurifyTreatment(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyTreatmentApi.createPurifyTreatment(data);
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
        treatmentDate: undefined,
        type: undefined,
        content: undefined,
        dosage: undefined,
        operator: undefined,
        notes: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>