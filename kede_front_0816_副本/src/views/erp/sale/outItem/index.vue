<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="销售出库编号" prop="outId">
        <el-input v-model="queryParams.outId" placeholder="请输入销售出库编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="销售订单项编号" prop="orderItemId">
        <el-input v-model="queryParams.orderItemId" placeholder="请输入销售订单项编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="仓库编号" prop="warehouseId">
        <el-input v-model="queryParams.warehouseId" placeholder="请输入仓库编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="产品编号" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入产品编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="产品单位单位" prop="productUnitId">
        <el-input v-model="queryParams.productUnitId" placeholder="请输入产品单位单位" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="产品单价" prop="productPrice">
        <el-input v-model="queryParams.productPrice" placeholder="请输入产品单价" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="数量" prop="count">
        <el-input v-model="queryParams.count" placeholder="请输入数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="总价" prop="totalPrice">
        <el-input v-model="queryParams.totalPrice" placeholder="请输入总价" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="税率，百分比" prop="taxPercent">
        <el-input v-model="queryParams.taxPercent" placeholder="请输入税率，百分比" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="税额" prop="taxPrice">
        <el-input v-model="queryParams.taxPrice" placeholder="请输入税额" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
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
                   v-hasPermi="['erp:sale-out-items:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['erp:sale-out-items:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="销售出库编号" align="center" prop="outId" />
      <el-table-column label="销售订单项编号" align="center" prop="orderItemId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="产品编号" align="center" prop="productId" />
      <el-table-column label="产品单位单位" align="center" prop="productUnitId" />
      <el-table-column label="产品单价" align="center" prop="productPrice" />
      <el-table-column label="数量" align="center" prop="count" />
      <el-table-column label="总价" align="center" prop="totalPrice" />
      <el-table-column label="税率，百分比" align="center" prop="taxPercent" />
      <el-table-column label="税额" align="center" prop="taxPrice" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['erp:sale-out-items:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['erp:sale-out-items:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="销售出库编号" prop="outId">
          <el-input v-model="form.outId" placeholder="请输入销售出库编号" />
        </el-form-item>
        <el-form-item label="销售订单项编号" prop="orderItemId">
          <el-input v-model="form.orderItemId" placeholder="请输入销售订单项编号" />
        </el-form-item>
        <el-form-item label="仓库编号" prop="warehouseId">
          <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
        </el-form-item>
        <el-form-item label="产品编号" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入产品编号" />
        </el-form-item>
        <el-form-item label="产品单位单位" prop="productUnitId">
          <el-input v-model="form.productUnitId" placeholder="请输入产品单位单位" />
        </el-form-item>
        <el-form-item label="产品单价" prop="productPrice">
          <el-input v-model="form.productPrice" placeholder="请输入产品单价" />
        </el-form-item>
        <el-form-item label="数量" prop="count">
          <el-input v-model="form.count" placeholder="请输入数量" />
        </el-form-item>
        <el-form-item label="总价" prop="totalPrice">
          <el-input v-model="form.totalPrice" placeholder="请输入总价" />
        </el-form-item>
        <el-form-item label="税率，百分比" prop="taxPercent">
          <el-input v-model="form.taxPercent" placeholder="请输入税率，百分比" />
        </el-form-item>
        <el-form-item label="税额" prop="taxPrice">
          <el-input v-model="form.taxPrice" placeholder="请输入税额" />
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
import { createSaleOut, updateSaleOut, deleteSaleOut, getSaleOut, getSaleOutPage, exportSaleOutExcel } from "@/api/erp/saleOut";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "SaleOutItems",
  components: {
      FileBusUpload,
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
      // ERP 销售出库项列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        outId: null,
        orderItemId: null,
        warehouseId: null,
        productId: null,
        productUnitId: null,
        productPrice: null,
        count: null,
        totalPrice: null,
        taxPercent: null,
        taxPrice: null,
        remark: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        outId: [{ required: true, message: "销售出库编号不能为空", trigger: "blur" }],
        orderItemId: [{ required: true, message: "销售订单项编号不能为空", trigger: "blur" }],
        warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
        productId: [{ required: true, message: "产品编号不能为空", trigger: "blur" }],
        productUnitId: [{ required: true, message: "产品单位单位不能为空", trigger: "blur" }],
        productPrice: [{ required: true, message: "产品单价不能为空", trigger: "blur" }],
        count: [{ required: true, message: "数量不能为空", trigger: "blur" }],
        totalPrice: [{ required: true, message: "总价不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
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
        outId: undefined,
        orderItemId: undefined,
        warehouseId: undefined,
        productId: undefined,
        productUnitId: undefined,
        productPrice: undefined,
        count: undefined,
        totalPrice: undefined,
        taxPercent: undefined,
        taxPrice: undefined,
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
      this.reset();
      this.open = true;
      this.title = "添加ERP 销售出库项";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getSaleOutItems(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改ERP 销售出库项";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateSaleOutItems(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createSaleOutItems(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除ERP 销售出库项编号为"' + id + '"的数据项?').then(function() {
          return deleteSaleOutItems(id);
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
      this.$modal.confirm('是否确认导出所有ERP 销售出库项数据项?').then(() => {
          this.exportLoading = true;
          return exportSaleOutItemsExcel(params);
        }).then(response => {
          this.$download.excel(response, 'ERP 销售出库项.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
