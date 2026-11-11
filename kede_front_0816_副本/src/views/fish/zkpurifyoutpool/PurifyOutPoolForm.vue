<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <!-- <el-form-item label="关联流程ID" prop="processId">
          <el-input v-model="formData.processId" placeholder="请输入关联流程ID" />
        </el-form-item> -->
        <el-form-item label="关联流程" prop="processId">
          <el-select style="width:100%;" v-model="formData.processId" placeholder="请选择关联流程" filterable clearable  @change="updatePurifyProcessId">
            <el-option v-for="(item, index) in purifyProcessList" :key="item.id+'__'+index" :label="`${item.recordNumber}`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出池时间" prop="outPoolTime">
          <el-date-picker clearable v-model="formData.outPoolTime" type="datetime" value-format="timestamp" style="width: 100%;"
            placeholder="选择出池时间" />
        </el-form-item>
        <el-form-item label="出池重量(kg)" prop="outPoolWeight">
          <el-input v-model="formData.outPoolWeight" placeholder="请输入出池重量(kg)" />
        </el-form-item>
        <el-form-item label="出池数量" prop="outPoolCount">
          <el-input v-model="formData.outPoolCount" placeholder="请输入出池数量" />
        </el-form-item>
        <!-- <el-form-item label="规格等级" prop="specification">
          <el-input v-model="formData.specification" placeholder="请输入规格等级" />
        </el-form-item> -->
        <el-form-item label="规格等级" prop="specification">
          <el-select v-model="formData.specification" placeholder="请输入规格等级" filterable clearable size="small" style="width: 100%;"> 
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_SPEC)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="formData.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="销售单价" prop="salesPrice">
          <el-input v-model="formData.salesPrice" placeholder="请输入销售单价" />
        </el-form-item>
        <el-form-item label="销售总额" prop="totalAmount">
          <el-input v-model="formData.totalAmount" placeholder="请输入销售总额" />
        </el-form-item>
        <!-- <el-form-item label="品质评价(优/良/中/差)" prop="fishQuality">
          <el-input v-model="formData.fishQuality" placeholder="请输入品质评价(优/良/中/差)" />
        </el-form-item> -->
        <el-form-item label="品质评价" prop="fishQuality">
          <el-select v-model="formData.fishQuality" placeholder="请输入品质评价" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_ARCHIVE_SHOW_LEVEL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="检测人" prop="testOperator">
          <el-input v-model="formData.testOperator" placeholder="请输入检测人" />
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
import * as PurifyOutPoolApi from '@/api/fish/zkpurifyoutpool';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
export default {
  name: "PurifyOutPoolForm",
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
        outPoolTime: undefined,
        outPoolWeight: undefined,
        outPoolCount: undefined,
        specification: undefined,
        customerName: undefined,
        salesPrice: undefined,
        totalAmount: undefined,
        fishQuality: undefined,
        testOperator: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        processId: [{ required: true, message: '关联流程ID不能为空', trigger: 'blur' }],
        outPoolTime: [{ required: true, message: "出池时间不能为空", trigger: "change" }],
        outPoolWeight: [{ required: true, message: "出池重量不能为空", trigger: "blur" }],
        outPoolCount: [{ required: true, message: "出池数量不能为空", trigger: "blur" }],
        specification: [{ required: true, message: "规格等级不能为空", trigger: "change" }],
      },
      purifyArchiveList: [],
      purifyProcessList: [], 
    };
  },
  watch: {
    'formData.outPoolWeight': function() { this.calculateTotalAmount(); },
    'formData.salesPrice': function() { this.calculateTotalAmount(); }
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getPurifyProcessList();
  },
  methods: {
    calculateTotalAmount() {
      if (this.formData.outPoolWeight && this.formData.salesPrice) {
        this.formData.totalAmount = (parseFloat(this.formData.outPoolWeight) * parseFloat(this.formData.salesPrice)).toFixed(2);
      }
    },
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
          const res = await PurifyOutPoolApi.getPurifyOutPool(id);
          this.formData = res.data;
          this.dialogTitle = "修改出池登记";
        } finally {
          this.formLoading = false;
        }
      } else {
        this.dialogTitle = "新增出池登记";
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
          await PurifyOutPoolApi.updatePurifyOutPool(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyOutPoolApi.createPurifyOutPool(data);
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
        outPoolTime: undefined,
        outPoolWeight: undefined,
        outPoolCount: undefined,
        specification: undefined,
        customerName: undefined,
        salesPrice: undefined,
        totalAmount: undefined,
        fishQuality: undefined,
        testOperator: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>