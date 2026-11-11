<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="付款单号" prop="no">
        <el-input v-model="queryParams.no" placeholder="请输入付款单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="付款时间" prop="paymentTime">
        <el-date-picker v-model="queryParams.paymentTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="财务人员编号" prop="financeUserId">
        <el-input v-model="queryParams.financeUserId" placeholder="请输入财务人员编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="供应商编号" prop="supplierId">
        <el-input v-model="queryParams.supplierId" placeholder="请输入供应商编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="付款账户编号" prop="accountId">
        <el-input v-model="queryParams.accountId" placeholder="请输入付款账户编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合计价格" prop="totalPrice">
        <el-input v-model="queryParams.totalPrice" placeholder="请输入合计价格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="优惠金额" prop="discountPrice">
        <el-input v-model="queryParams.discountPrice" placeholder="请输入优惠金额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="实付金额" prop="paymentPrice">
        <el-input v-model="queryParams.paymentPrice" placeholder="请输入实付金额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['erp:finance-payment:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['erp:finance-payment:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="付款单号" align="center" prop="no" />
      <!-- <el-table-column label="状态" align="center" prop="status" /> -->
      <el-table-column label="付款时间" align="center" prop="paymentTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.paymentTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="财务人员编号" align="center" prop="financeUserId" /> -->
      <el-table-column label="供应商编号" align="center" prop="supplierId" />
      <el-table-column label="付款账户编号" align="center" prop="accountId" />
      <el-table-column label="合计价格" align="center" prop="totalPrice" />
      <!-- <el-table-column label="优惠金额" align="center" prop="discountPrice" /> -->
      <el-table-column label="实付金额" align="center" prop="paymentPrice" />
      <!-- <el-table-column label="备注" align="center" prop="remark" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" :disabled="scope.row.status === 20"
            v-hasPermi="['erp:finance-payment:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="exe_updatePurchaseOrderStatus(scope.row, 20)"
            v-if="scope.row.status == 10" v-hasPermi="['erp:purchase-order:update']">审批</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="exe_updatePurchaseOrderStatus(scope.row, 10)"
            v-if="scope.row.status == 20" v-hasPermi="['erp:purchase-order:update']">反审批</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['erp:finance-payment:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 对话框(添加 / 修改) -->
    <FinancePaymentDetail ref="FinancePaymentDetailRef" :visible="other_open" @submit="submitFormDetail" @son_close="parentClose">
    </FinancePaymentDetail>
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="付款单号" prop="no">
          <el-input v-model="form.no" placeholder="请输入付款单号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="付款时间" prop="paymentTime">
          <el-date-picker clearable v-model="form.paymentTime" type="date" value-format="timestamp"
            placeholder="选择付款时间" />
        </el-form-item>
        <el-form-item label="财务人员编号" prop="financeUserId">
          <el-input v-model="form.financeUserId" placeholder="请输入财务人员编号" />
        </el-form-item>
        <el-form-item label="供应商编号" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="付款账户编号" prop="accountId">
          <el-input v-model="form.accountId" placeholder="请输入付款账户编号" />
        </el-form-item>
        <el-form-item label="合计价格" prop="totalPrice">
          <el-input v-model="form.totalPrice" placeholder="请输入合计价格" />
        </el-form-item>
        <el-form-item label="优惠金额" prop="discountPrice">
          <el-input v-model="form.discountPrice" placeholder="请输入优惠金额" />
        </el-form-item>
        <el-form-item label="实付金额" prop="paymentPrice">
          <el-input v-model="form.paymentPrice" placeholder="请输入实付金额" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createFinancePayment, updateFinancePayment, deleteFinancePayment, getFinancePayment, getFinancePaymentPage, exportFinancePaymentExcel, updateFinancePayStatus } from "@/api/erp/financePayment";
import FileBusUpload from "@/components/FileBusUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import FinancePaymentDetail from './detail.vue';

export default {
  name: "FinancePayment",
  components: {
    FileBusUpload,
    FinancePaymentDetail,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // ERP 付款单列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      other_open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        no: null,
        status: null,
        paymentTime: [],
        financeUserId: null,
        supplierId: null,
        accountId: null,
        totalPrice: null,
        discountPrice: null,
        paymentPrice: null,
        remark: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        no: [{ required: true, message: "付款单号不能为空", trigger: "blur" }],
        status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
        paymentTime: [{ required: true, message: "付款时间不能为空", trigger: "blur" }],
        supplierId: [{ required: true, message: "供应商编号不能为空", trigger: "blur" }],
        accountId: [{ required: true, message: "付款账户编号不能为空", trigger: "blur" }],
        totalPrice: [{ required: true, message: "合计价格不能为空", trigger: "blur" }],
        discountPrice: [{ required: true, message: "优惠金额不能为空", trigger: "blur" }],
        paymentPrice: [{ required: true, message: "实付金额不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    exe_updatePurchaseOrderStatus(row, status) {
      console.log(`==========>row:${JSON.stringify(row)}`);
      try {
        this.$modal.confirm(`确定${status === 20 ? '审批' : '反审批'}该订单吗？`).then(function () {
          return updateFinancePayStatus({ id: row.id, status: status });
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess(`${status === 20 ? '审批' : '反审批'}成功`);
        }).catch(() => { });
      } catch { }
    },

    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getFinancePaymentPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        no: undefined,
        status: undefined,
        paymentTime: undefined,
        financeUserId: undefined,
        supplierId: undefined,
        accountId: undefined,
        totalPrice: undefined,
        discountPrice: undefined,
        paymentPrice: undefined,
        remark: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.other_open = true;
      // this.reset();
      // this.open = true;
      // this.title = "添加ERP 付款单";
      // this.form.fid = getUUID().replaceAll('-', '');
    },
    parentClose() {
      this.other_open = false;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.other_open = true;
      this.$refs.FinancePaymentDetailRef.handleUpdate(row);
      // this.reset();
      // const id = row.id;
      // getFinancePayment(id).then(response => {
      //   this.form = response.data;
      //   this.open = true;
      //   this.title = "修改ERP 付款单";
      //   if (!this.form.fid) {
      //     this.form.fid = getUUID().replaceAll("-", "");
      //   }
      // });
    },
    submitFormDetail(form) {
      this.form = JSON.parse(JSON.stringify(form));
      // console.log(`submitFormDetail==================>form:${JSON.stringify(this.form)}`)
      this.submitForm();
    },
    /** 提交按钮 */
    submitForm() {
      // 修改的提交
      if (this.form.id != null) {
        updateFinancePayment(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.other_open = false;
          this.getList();
        });
        return;
      }
      // 添加的提交
      createFinancePayment(this.form).then(response => {
        this.$modal.msgSuccess("新增成功");
        this.open = false;
        this.other_open = false;
        this.getList();
      });
      // this.$refs["form"].validate(valid => {
      //   if (!valid) {
      //     return;
      //   }
      // });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除ERP 付款单编号为"' + id + '"的数据项?').then(function () {
        return deleteFinancePayment(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = { ...this.queryParams };
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有ERP 付款单数据项?').then(() => {
        this.exportLoading = true;
        return exportFinancePaymentExcel(params);
      }).then(response => {
        this.$download.excel(response, 'ERP 付款单.xls');
        this.exportLoading = false;
      }).catch(() => { });
    }
  }
};
</script>
