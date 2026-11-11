<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="采购单编号" prop="no">
        <el-input v-model="queryParams.no" placeholder="请输入采购单编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="采购状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择采购状态" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="供应商编号" prop="supplierId">
        <el-input v-model="queryParams.supplierId" placeholder="请输入供应商编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="结算账户编号" prop="accountId">
        <el-input v-model="queryParams.accountId" placeholder="请输入结算账户编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="采购时间" prop="orderTime">
        <el-date-picker v-model="queryParams.orderTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="合计数量" prop="totalCount">
        <el-input v-model="queryParams.totalCount" placeholder="请输入合计数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合计价格" prop="totalPrice">
        <el-input v-model="queryParams.totalPrice" placeholder="请输入合计价格" clearable @keyup.enter.native="handleQuery"/>
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
      <el-form-item label="定金金额" prop="depositPrice">
        <el-input v-model="queryParams.depositPrice" placeholder="请输入定金金额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="附件地址" prop="fileUrl">
        <el-input v-model="queryParams.fileUrl" placeholder="请输入附件地址" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="采购入库数量" prop="inCount">
        <el-input v-model="queryParams.inCount" placeholder="请输入采购入库数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="采购退货数量" prop="returnCount">
        <el-input v-model="queryParams.returnCount" placeholder="请输入采购退货数量" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['erp:purchase-order:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['erp:purchase-order:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="采购单编号" align="center" prop="no" width="180" />
      <!-- <el-table-column label="采购状态" align="center" prop="status" /> -->
      <el-table-column label="产品信息" align="center" prop="productNames" show-overflow-tooltip />
      <!-- <el-table-column label="供应商编号" align="center" prop="supplierId" /> -->
      <el-table-column label="供应商" align="center" prop="supplierName" />
      <!-- <el-table-column label="结算账户编号" align="center" prop="accountId" /> -->
      <el-table-column label="采购时间" align="center" prop="orderTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.orderTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否审批" align="center" prop="createTime">
        <template v-slot="scope">
          <span>{{ scope.row.status==20?'已审批':'未审批' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="合计数量" align="center" prop="totalCount" />
      <el-table-column label="赠送数量" align="center" prop="giftNum" />
      <el-table-column label="产品价格" align="center" prop="totalProductPrice" />
      <el-table-column label="合计税额" align="center" prop="totalTaxPrice" />
      <el-table-column label="合计价格" align="center" prop="totalPrice" />
      <!-- <el-table-column label="优惠率，百分比" align="center" prop="discountPercent" /> -->
      <!-- <el-table-column label="优惠金额" align="center" prop="discountPrice" /> -->
      <!-- <el-table-column label="定金金额" align="center" prop="depositPrice" /> -->
      <!-- <el-table-column label="附件地址" align="center" prop="fileUrl" /> -->
      <!-- <el-table-column label="备注" align="center" prop="remark" /> -->
      <el-table-column label="操作人" align="center" prop="creatorName" show-overflow-tooltip />
      <el-table-column label="采购入库数量" align="center" prop="inCount" />
      <!-- <el-table-column label="采购退货数量" align="center" prop="returnCount" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="scope.row.status != 20"
                     v-hasPermi="['erp:purchase-order:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-else
                     v-hasPermi="['erp:purchase-order:update']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleDetail(scope.row)">审批进度</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-edit" @click="exe_updatePurchaseOrderStatus(scope.row, 20)" v-if="scope.row.status == 10"
                     v-hasPermi="['erp:purchase-order:update']">审批</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="exe_updatePurchaseOrderStatus(scope.row, 10)" v-if="scope.row.status == 20"
                     v-hasPermi="['erp:purchase-order:update']">反审批</el-button> -->
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['erp:purchase-order:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <PurchaseOrderDetail ref="PurchaseOrderDetailRef" :visible="other_open" @submit="submitFormDetail" @son_close="parentClose"></PurchaseOrderDetail>
    <!-- <div v-if="other_open">
    </div> -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="采购单编号" prop="no">
          <el-input v-model="form.no" placeholder="请输入采购单编号" />
        </el-form-item>
        <el-form-item label="采购状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="供应商编号" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="结算账户编号" prop="accountId">
          <el-input v-model="form.accountId" placeholder="请输入结算账户编号" />
        </el-form-item>
        <el-form-item label="采购时间" prop="orderTime">
          <el-date-picker clearable v-model="form.orderTime" type="date" placeholder="选择采购时间" />
        </el-form-item>
        <el-form-item label="合计数量" prop="totalCount">
          <el-input v-model="form.totalCount" placeholder="请输入合计数量" />
        </el-form-item>
        <el-form-item label="合计产品价格" prop="totalProductPrice">
          <el-input v-model="form.totalProductPrice" placeholder="请输入合计产品价格" />
        </el-form-item>
        <el-form-item label="合计价格" prop="totalPrice">
          <el-input v-model="form.totalPrice" placeholder="请输入合计价格" />
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
import { createPurchaseOrder, updatePurchaseOrder, deletePurchaseOrder, getPurchaseOrder, getPurchaseOrderPage, exportPurchaseOrderExcel, updatePurchaseOrderStatus } from "@/api/erp/purchaseOrder";
import FileBusUpload from "@/components/FileBusUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import PurchaseOrderDetail from './detail.vue';

export default {
  name: "PurchaseOrder",
  components: {
      FileBusUpload,
      PurchaseOrderDetail,
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
      // ERP 采购订单列表
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
        supplierId: null,
        accountId: null,
        orderTime: [],
        totalCount: null,
        totalPrice: null,
        totalProductPrice: null,
        totalTaxPrice: null,
        discountPercent: null,
        discountPrice: null,
        depositPrice: null,
        fileUrl: null,
        remark: null,
        inCount: null,
        returnCount: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        no: [{ required: true, message: "采购单编号不能为空", trigger: "blur" }],
        status: [{ required: true, message: "采购状态不能为空", trigger: "blur" }],
        supplierId: [{ required: true, message: "供应商编号不能为空", trigger: "blur" }],
        orderTime: [{ required: true, message: "采购时间不能为空", trigger: "blur" }],
        totalCount: [{ required: true, message: "合计数量不能为空", trigger: "blur" }],
        totalPrice: [{ required: true, message: "合计价格不能为空", trigger: "blur" }],
        totalProductPrice: [{ required: true, message: "合计产品价格不能为空", trigger: "blur" }],
        totalTaxPrice: [{ required: true, message: "合计税额不能为空", trigger: "blur" }],
        discountPercent: [{ required: true, message: "优惠率，百分比不能为空", trigger: "blur" }],
        discountPrice: [{ required: true, message: "优惠金额不能为空", trigger: "blur" }],
        depositPrice: [{ required: true, message: "定金金额不能为空", trigger: "blur" }],
        inCount: [{ required: true, message: "采购入库数量不能为空", trigger: "blur" }],
        returnCount: [{ required: true, message: "采购退货数量不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleDetail(row) {
      this.$router.push({ name: "BpmProcessInstanceDetail", query: { id: row.processInstanceId}});
    },
    exe_updatePurchaseOrderStatus(row, status) {
      // console.log(`==========>row:${JSON.stringify(row)}`);
      try {
        this.$modal.confirm(`确定${status === 20 ? '审批' : '反审批'}该订单吗？`).then(function () {
          return updatePurchaseOrderStatus({id: row.id, status: status});
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
      getPurchaseOrderPage(this.queryParams).then(response => {
        let list = response.data.list;
        list.forEach(i => {
          if(this.my_common.listIsEmpty(i.items)) return;
          let sum = 0;
          i.items.forEach(ii => {
            sum += ii.giftNum||0;
          })
          i.giftNum = sum;
        });
        this.list = list;
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
        supplierId: undefined,
        accountId: undefined,
        orderTime: undefined,
        totalCount: undefined,
        totalPrice: undefined,
        totalProductPrice: undefined,
        totalTaxPrice: undefined,
        discountPercent: undefined,
        discountPrice: undefined,
        depositPrice: undefined,
        fileUrl: undefined,
        remark: undefined,
        inCount: undefined,
        returnCount: undefined,
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
      // this.$router.push({ name: 'PurchaseOrder', query: { id: 1 }})
      // this.reset();
      // this.open = true;
      // this.title = "添加ERP 采购订单";
      // this.form.fid = getUUID().replaceAll('-', '');
    },
    parentClose() {
      this.other_open = false;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.other_open = true;
      this.$refs.PurchaseOrderDetailRef.handleUpdate(row);
      // this.reset();
      // const id = row.id;
      // getPurchaseOrder(id).then(response => {
      //   this.form = response.data;
      //   this.other_open = true;
      //   this.title = "修改ERP 采购订单";
      //   if (!this.form.fid) {
      //     this.form.fid = getUUID().replaceAll("-", "");
      //   }
      // });
    },
    submitFormDetail(form) {
      const payload = form ? { ...form } : {}
      // 避免 Date 经 JSON.stringify 变成 UTC ISO，与列表 parseTime 产生 8 小时时差
      if (payload.orderTime != null && payload.orderTime !== '') {
        payload.orderTime = new Date(payload.orderTime).getTime()
      }
      this.form = payload
      this.submitForm();
    },
    /** 提交按钮 */
    submitForm() {
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        // 避免 Date 经 JSON.stringify 变成 UTC ISO，与列表 parseTime 产生 8 小时时差
        if (this.form.orderTime != null && this.form.orderTime !== '') {
          this.form.orderTime = new Date(this.form.orderTime).getTime()
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
        updatePurchaseOrder(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.other_open = false;
          this.operationLoading = false;
          loading.close();
          this.getList();
        });
        return;
      }
      // 添加的提交
      createPurchaseOrder(this.form).then(response => {
        this.$modal.msgSuccess("新增成功");
        this.open = false;
        this.other_open = false;
        this.operationLoading = false;
        loading.close();
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
      this.$modal.confirm('是否确认删除ERP 采购订单编号为"' + id + '"的数据项?').then(function() {
          return deletePurchaseOrder(id);
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
      this.$modal.confirm('是否确认导出所有ERP 采购订单数据项?').then(() => {
          this.exportLoading = true;
          return exportPurchaseOrderExcel(params);
        }).then(response => {
          this.$download.excel(response, 'ERP 采购订单.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
