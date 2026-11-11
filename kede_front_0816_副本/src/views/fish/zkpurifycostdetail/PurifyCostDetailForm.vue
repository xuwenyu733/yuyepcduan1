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
        <!-- <el-form-item label="成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)" prop="costType">
          <el-select v-model="formData.costType" placeholder="请选择成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="成本类型" prop="costType">
          <el-select v-model="formData.costType" placeholder="请输入成本类型" filterable clearable size="small" style="width: 100%;"> 
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_COST_DETAIL_COST_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model="formData.amount" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="发生日期" prop="occurrenceDate">
          <el-date-picker clearable v-model="formData.occurrenceDate" type="date" value-format="timestamp" style="width: 100%;"
            placeholder="选择发生日期" />
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
import * as PurifyCostDetailApi from '@/api/fish/zkpurifycostdetail';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
export default {
  name: "PurifyCostDetailForm",
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
        processId: undefined,
        costType: undefined,
        amount: undefined,
        occurrenceDate: undefined,
        notes: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        processId: [{ required: true, message: '关联流程ID不能为空', trigger: 'blur' }],
        costType: [{ required: true, message: '成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)不能为空', trigger: 'change' }],
        amount: [{ required: true, message: '金额不能为空', trigger: 'blur' }],
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
          const res = await PurifyCostDetailApi.getPurifyCostDetail(id);
          this.formData = res.data;
          this.title = "修改成本明细记录";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增成本明细记录";
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
          await PurifyCostDetailApi.updatePurifyCostDetail(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyCostDetailApi.createPurifyCostDetail(data);
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
        costType: undefined,
        amount: undefined,
        occurrenceDate: undefined,
        notes: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>