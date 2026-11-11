<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <el-form-item label="采购单号" prop="purchaseOrderNo">
          <el-input v-model="formData.purchaseOrderNo" placeholder="自动生成" :disabled="true" />
        </el-form-item>
        <!-- <el-form-item label="品类" prop="category">
          <el-input v-model="formData.category" placeholder="请输入品类" />
        </el-form-item> -->
        <el-form-item label="品类" prop="category">
          <el-select v-model="formData.category" placeholder="请输入品类" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源（城市/地区）" prop="source">
          <el-input v-model="formData.source" placeholder="请输入来源（城市/地区）" />
        </el-form-item>
        <el-form-item label="具体地址" prop="detailedAddress">
          <el-input v-model="formData.detailedAddress" placeholder="请输入具体地址" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="采购数量(kg)" prop="quantity">
          <el-input v-model="formData.quantity" placeholder="请输入采购数量(kg)" />
        </el-form-item>
        <el-form-item label="单价(元/kg)" prop="unitPrice">
          <el-input v-model="formData.unitPrice" placeholder="请输入单价(元/kg)" />
        </el-form-item>
        <el-form-item label="总金额(元)" prop="totalAmount">
          <el-input v-model="formData.totalAmount" placeholder="请输入总金额(元)" />
        </el-form-item>
        <!-- <el-form-item label="运输方式" prop="transportMethod">
          <el-input v-model="formData.transportMethod" placeholder="请输入运输方式" />
        </el-form-item> -->
        <el-form-item label="运输方式" prop="transportMethod">
          <el-select v-model="formData.transportMethod" placeholder="请输入运输方式" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_TRANSPORT)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购日期" prop="purchaseDate">
          <el-date-picker clearable v-model="formData.purchaseDate" type="date" value-format="timestamp"
            style="width: 100%;" placeholder="选择采购日期" />
        </el-form-item>
        <el-form-item label="采购负责人" prop="purchaseManager">
          <el-input v-model="formData.purchaseManager" placeholder="请输入采购负责人" />
        </el-form-item>
        <el-form-item label="规格-单尾重(kg)" prop="avgWeight">
          <el-input v-model="formData.avgWeight" placeholder="请输入规格-单尾重(kg)" />
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
import * as PurifyPurchaseApi from '@/api/fish/zkpurifypurchase';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
export default {
  name: "PurifyPurchaseForm",
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
        purchaseOrderNo: undefined,
        category: undefined,
        source: undefined,
        detailedAddress: undefined,
        contactPerson: undefined,
        contactPhone: undefined,
        quantity: undefined,
        unitPrice: undefined,
        totalAmount: undefined,
        transportMethod: undefined,
        purchaseDate: undefined,
        purchaseManager: undefined,
        avgWeight: undefined,
        notes: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        category: [{ required: true, message: "品类不能为空", trigger: "change" }],
        source: [{ required: true, message: "来源不能为空", trigger: "blur" }],
        detailedAddress: [{ required: true, message: "具体地址不能为空", trigger: "blur" }],
        contactPerson: [{ required: true, message: "联系人不能为空", trigger: "blur" }],
        contactPhone: [{ required: true, message: "联系电话不能为空", trigger: "blur" }],
        quantity: [{ required: true, message: "采购数量不能为空", trigger: "blur" }],
        unitPrice: [{ required: true, message: "单价不能为空", trigger: "blur" }],
        transportMethod: [{ required: true, message: "运输方式不能为空", trigger: "change" }],
        purchaseDate: [{ required: true, message: "采购日期不能为空", trigger: "change" }],
        purchaseManager: [{ required: true, message: "采购负责人不能为空", trigger: "blur" }],
      },
    };
  },
  watch: {
    'formData.quantity': function() { this.calculateTotalAmount(); },
    'formData.unitPrice': function() { this.calculateTotalAmount(); }
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
  },
  methods: {
    calculateTotalAmount() {
      if (this.formData.quantity && this.formData.unitPrice) {
        this.formData.totalAmount = (parseFloat(this.formData.quantity) * parseFloat(this.formData.unitPrice)).toFixed(2);
      }
    },
    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await PurifyPurchaseApi.getPurifyPurchase(id);
          this.formData = res.data;
          this.title = "修改采购记录";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增采购记录";
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
          await PurifyPurchaseApi.updatePurifyPurchase(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyPurchaseApi.createPurifyPurchase(data);
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
        purchaseOrderNo: undefined,
        category: undefined,
        source: undefined,
        detailedAddress: undefined,
        contactPerson: undefined,
        contactPhone: undefined,
        quantity: undefined,
        unitPrice: undefined,
        totalAmount: undefined,
        transportMethod: undefined,
        purchaseDate: undefined,
        purchaseManager: undefined,
        avgWeight: undefined,
        notes: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>