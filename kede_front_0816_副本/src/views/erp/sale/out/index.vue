<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="销售出库编号" prop="no">
        <el-input v-model="queryParams.no" placeholder="请输入销售出库编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="出库状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择出库状态" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="客户编号" prop="customerId">
        <el-input v-model="queryParams.customerId" placeholder="请输入客户编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="结算账户编号" prop="accountId">
        <el-input v-model="queryParams.accountId" placeholder="请输入结算账户编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="销售用户编号" prop="saleUserId">
        <el-input v-model="queryParams.saleUserId" placeholder="请输入销售用户编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="出库时间" prop="outTime">
        <el-date-picker v-model="queryParams.outTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="销售订单编号" prop="orderId">
        <el-input v-model="queryParams.orderId" placeholder="请输入销售订单编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="销售订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入销售订单号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合计数量" prop="totalCount">
        <el-input v-model="queryParams.totalCount" placeholder="请输入合计数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合计价格" prop="totalPrice">
        <el-input v-model="queryParams.totalPrice" placeholder="请输入合计价格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="已收款金额" prop="receiptPrice">
        <el-input v-model="queryParams.receiptPrice" placeholder="请输入已收款金额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合计产品价格" prop="totalProductPrice">
        <el-input v-model="queryParams.totalProductPrice" placeholder="请输入合计产品价格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合计税额" prop="totalTaxPrice">
        <el-input v-model="queryParams.totalTaxPrice" placeholder="请输入合计税额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="优惠率，百分比" prop="discountPercent">
        <el-input v-model="queryParams.discountPercent" placeholder="请输入优惠率，百分比" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="优惠金额" prop="discountPrice">
        <el-input v-model="queryParams.discountPrice" placeholder="请输入优惠金额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="其它金额" prop="otherPrice">
        <el-input v-model="queryParams.otherPrice" placeholder="请输入其它金额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="附件地址" prop="fileUrl">
        <el-input v-model="queryParams.fileUrl" placeholder="请输入附件地址" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
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
                   v-hasPermi="['erp:sale-out:create']">新增其他</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddFish"
                   v-hasPermi="['erp:sale-out:create']">新增卖鱼</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['erp:sale-out:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="销售出库编号" align="center" prop="no" width="180" />
      <!-- <el-table-column label="出库状态" align="center" prop="status" /> -->
      <!-- <el-table-column label="客户编号" align="center" prop="customerId" /> -->
      <!-- <el-table-column label="结算账户编号" align="center" prop="accountId" /> -->
      <!-- <el-table-column label="销售用户编号" align="center" prop="saleUserId" /> -->
      <el-table-column label="出库时间" align="center" prop="outTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.outTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="销售订单编号" align="center" prop="orderId" /> -->
      <el-table-column label="销售订单号" align="center" prop="orderNo" width="180" />
      <el-table-column label="合计数量" align="center" prop="totalCount" />
      <!-- <el-table-column label="合计价格" align="center" prop="totalPrice" /> -->
      <!-- <el-table-column label="已收款金额" align="center" prop="receiptPrice" /> -->
      <el-table-column label="合计产品价格" align="center" prop="totalProductPrice" />
      <el-table-column label="合计税额" align="center" prop="totalTaxPrice" />
      <el-table-column label="合计价格" align="center" prop="totalPrice" />
      <!-- <el-table-column label="优惠率，百分比" align="center" prop="discountPercent" /> -->
      <!-- <el-table-column label="优惠金额" align="center" prop="discountPrice" /> -->
      <!-- <el-table-column label="其它金额" align="center" prop="otherPrice" /> -->
      <!-- <el-table-column label="附件地址" align="center" prop="fileUrl" /> -->
      <!-- <el-table-column label="备注" align="center" prop="remark" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <!-- :disabled="scope.row.status === 20" -->
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status != 20"
                     v-hasPermi="['erp:sale-out:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-else
                     v-hasPermi="['erp:sale-out:update']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="exe_updatePurchaseOrderStatus(scope.row, 20)" v-if="scope.row.status == 10"
                     v-hasPermi="['erp:purchase-order:update']">审批</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="exe_updatePurchaseOrderStatus(scope.row, 10)" v-if="scope.row.status == 20"
                     v-hasPermi="['erp:purchase-order:update']">反审批</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['erp:sale-out:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <SaleOutDetail ref="SaleOutDetailRef" :visible="other_open" @submit="submitFormDetail" @son_close="parentClose"></SaleOutDetail>
    <SaleFishOutDetail ref="SaleFishOutDetailRef" :visible="other_open_fish" @submit="submitFormDetail" @son_close="parentClose"></SaleFishOutDetail>
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="销售出库编号" prop="no">
          <el-input v-model="form.no" placeholder="请输入销售出库编号" />
        </el-form-item>
        <el-form-item label="出库状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="客户编号" prop="customerId">
          <el-input v-model="form.customerId" placeholder="请输入客户编号" />
        </el-form-item>
        <el-form-item label="结算账户编号" prop="accountId">
          <el-input v-model="form.accountId" placeholder="请输入结算账户编号" />
        </el-form-item>
        <el-form-item label="销售用户编号" prop="saleUserId">
          <el-input v-model="form.saleUserId" placeholder="请输入销售用户编号" />
        </el-form-item>
        <el-form-item label="出库时间" prop="outTime">
          <el-date-picker clearable v-model="form.outTime" type="date" value-format="timestamp" placeholder="选择出库时间" />
        </el-form-item>
        <el-form-item label="销售订单编号" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入销售订单编号" />
        </el-form-item>
        <el-form-item label="销售订单号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入销售订单号" />
        </el-form-item>
        <el-form-item label="合计数量" prop="totalCount">
          <el-input v-model="form.totalCount" placeholder="请输入合计数量" />
        </el-form-item>
        <el-form-item label="合计价格" prop="totalPrice">
          <el-input v-model="form.totalPrice" placeholder="请输入合计价格" />
        </el-form-item>
        <el-form-item label="已收款金额" prop="receiptPrice">
          <el-input v-model="form.receiptPrice" placeholder="请输入已收款金额" />
        </el-form-item>
        <el-form-item label="合计产品价格" prop="totalProductPrice">
          <el-input v-model="form.totalProductPrice" placeholder="请输入合计产品价格" />
        </el-form-item>
        <el-form-item label="合计税额" prop="totalTaxPrice">
          <el-input v-model="form.totalTaxPrice" placeholder="请输入合计税额" />
        </el-form-item>
        <el-form-item label="优惠率，百分比" prop="discountPercent">
          <el-input v-model="form.discountPercent" placeholder="请输入优惠率，百分比" />
        </el-form-item>
        <el-form-item label="优惠金额" prop="discountPrice">
          <el-input v-model="form.discountPrice" placeholder="请输入优惠金额" />
        </el-form-item>
        <el-form-item label="其它金额" prop="otherPrice">
          <el-input v-model="form.otherPrice" placeholder="请输入其它金额" />
        </el-form-item>
        <el-form-item label="附件地址" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入附件地址" />
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
import { createSaleOut, updateSaleOut, deleteSaleOut, getSaleOut, getSaleOutPage, exportSaleOutExcel, updateSaleOutStatus } from "@/api/erp/saleOut";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import SaleOutDetail from './detail.vue';
import SaleFishOutDetail from './detail-fish.vue';

export default {
  name: "SaleOut",
  components: {
      FileBusUpload,
      SaleOutDetail,
      SaleFishOutDetail,
  },
  data() {
    return {
      operationLoading: false,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // ERP 销售出库列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      other_open: false,
      other_open_fish: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        no: null,
        status: null,
        customerId: null,
        accountId: null,
        saleUserId: null,
        outTime: [],
        orderId: null,
        orderNo: null,
        totalCount: null,
        totalPrice: null,
        receiptPrice: null,
        totalProductPrice: null,
        totalTaxPrice: null,
        discountPercent: null,
        discountPrice: null,
        otherPrice: null,
        fileUrl: null,
        remark: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        no: [{ required: true, message: "销售出库编号不能为空", trigger: "blur" }],
        status: [{ required: true, message: "出库状态不能为空", trigger: "blur" }],
        customerId: [{ required: true, message: "客户编号不能为空", trigger: "blur" }],
        accountId: [{ required: true, message: "结算账户编号不能为空", trigger: "blur" }],
        outTime: [{ required: true, message: "出库时间不能为空", trigger: "blur" }],
        orderId: [{ required: true, message: "销售订单编号不能为空", trigger: "blur" }],
        orderNo: [{ required: true, message: "销售订单号不能为空", trigger: "blur" }],
        totalCount: [{ required: true, message: "合计数量不能为空", trigger: "blur" }],
        totalPrice: [{ required: true, message: "合计价格不能为空", trigger: "blur" }],
        receiptPrice: [{ required: true, message: "已收款金额不能为空", trigger: "blur" }],
        totalProductPrice: [{ required: true, message: "合计产品价格不能为空", trigger: "blur" }],
        totalTaxPrice: [{ required: true, message: "合计税额不能为空", trigger: "blur" }],
        discountPercent: [{ required: true, message: "优惠率，百分比不能为空", trigger: "blur" }],
        discountPrice: [{ required: true, message: "优惠金额不能为空", trigger: "blur" }],
        otherPrice: [{ required: true, message: "其它金额不能为空", trigger: "blur" }],
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
          return updateSaleOutStatus({id: row.id, status: status});
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess(`${status === 20 ? '审批' : '反审批'}成功`);
        }).catch(() => { });
      } catch {}
    },

    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getSaleOutPage(this.queryParams).then(response => {
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
        customerId: undefined,
        accountId: undefined,
        saleUserId: undefined,
        outTime: undefined,
        orderId: undefined,
        orderNo: undefined,
        totalCount: undefined,
        totalPrice: undefined,
        receiptPrice: undefined,
        totalProductPrice: undefined,
        totalTaxPrice: undefined,
        discountPercent: undefined,
        discountPrice: undefined,
        otherPrice: undefined,
        fileUrl: undefined,
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
      // this.title = "添加ERP 销售出库";
      // this.form.fid = getUUID().replaceAll('-', '');
    },
    handleAddFish() {
      this.other_open_fish = true;
    },
    parentClose() {
      this.other_open = false;
      this.other_open_fish = false;
    },
    /** 修改按钮操作  */
    handleUpdate(row) {
      let flag = false;
      if(row && !this.my_common.listIsEmpty(row.items)) {
        row.items.forEach(item => {
          if(item.busSaleInfo) {
            flag = true;
            item.busSaleInfoObj = JSON.parse(item.busSaleInfo);
          }
        });
      }
      
      if(flag) {
        this.other_open_fish = true;
        this.$refs.SaleFishOutDetailRef.handleUpdate(row);
      } else {
        this.other_open = true;
        this.$refs.SaleOutDetailRef.handleUpdate(row);
      }
      // this.reset();
      // const id = row.id;
      // getSaleOut(id).then(response => {
      //   this.form = response.data;
      //   this.open = true;
      //   this.title = "修改ERP 销售出库";
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
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        this.operationLoading = true;
        const loading = this.$loading({
          lock: true,
          text: 'Loading',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
      // 修改的提交
      if (this.form.id != null) {
        updateSaleOut(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.other_open = false;
          this.other_open_fish = false;
          this.getList();
          this.operationLoading = false;
          loading.close();
        });
        return;
      }
      // 添加的提交
      createSaleOut(this.form).then(response => {
        this.$modal.msgSuccess("新增成功");
        this.open = false;
        this.other_open = false;
        this.other_open_fish = false;
        this.getList();
        this.operationLoading = false;
        loading.close();
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
      this.$modal.confirm('是否确认删除ERP 销售出库编号为"' + id + '"的数据项?').then(function() {
          return deleteSaleOut(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有ERP 销售出库数据项?').then(() => {
          this.exportLoading = true;
          return exportSaleOutExcel(params);
        }).then(response => {
          this.$download.excel(response, 'ERP 销售出库.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
