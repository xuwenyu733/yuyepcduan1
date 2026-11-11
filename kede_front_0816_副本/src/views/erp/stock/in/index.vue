<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="入库单号" prop="no">
        <el-input v-model="queryParams.no" placeholder="请输入入库单号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="供应商编号" prop="supplierId">
        <el-input v-model="queryParams.supplierId" placeholder="请输入供应商编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="入库时间" prop="inTime">
        <el-date-picker v-model="queryParams.inTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="合计数量" prop="totalCount">
        <el-input v-model="queryParams.totalCount" placeholder="请输入合计数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="合计金额，单位：元" prop="totalPrice">
        <el-input v-model="queryParams.totalPrice" placeholder="请输入合计金额，单位：元" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="附件 URL" prop="fileUrl">
        <el-input v-model="queryParams.fileUrl" placeholder="请输入附件 URL" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['erp:stock-in:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['erp:stock-in:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="入库编号" align="center" prop="id" />
      <el-table-column label="入库单号" align="center" prop="no" />
      <el-table-column label="供应商编号" align="center" prop="supplierId" />
      <el-table-column label="入库时间" align="center" prop="inTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.inTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="合计数量" align="center" prop="totalCount" />
      <el-table-column label="合计金额，单位：元" align="center" prop="totalPrice" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="附件 URL" align="center" prop="fileUrl" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['erp:stock-in:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['erp:stock-in:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="入库单号" prop="no">
          <el-input v-model="form.no" placeholder="请输入入库单号" />
        </el-form-item>
        <el-form-item label="供应商编号" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="入库时间" prop="inTime">
          <el-date-picker clearable v-model="form.inTime" type="date" value-format="timestamp" placeholder="选择入库时间" />
        </el-form-item>
        <el-form-item label="合计数量" prop="totalCount">
          <el-input v-model="form.totalCount" placeholder="请输入合计数量" />
        </el-form-item>
        <el-form-item label="合计金额，单位：元" prop="totalPrice">
          <el-input v-model="form.totalPrice" placeholder="请输入合计金额，单位：元" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="附件 URL" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入附件 URL" />
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
import { createStockIn, updateStockIn, deleteStockIn, getStockIn, getStockInPage, exportStockInExcel } from "@/api/erp/stockIn";
import FileBusUpload from "@/components/FileBusUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "StockIn",
  components: {
      FileBusUpload,
  },
  data() {
    return {
      userInfo: {},
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // ERP 其它入库单列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        no: null,
        supplierId: null,
        inTime: [],
        totalCount: null,
        totalPrice: null,
        status: null,
        remark: null,
        fileUrl: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        no: [{ required: true, message: "入库单号不能为空", trigger: "blur" }],
        inTime: [{ required: true, message: "入库时间不能为空", trigger: "blur" }],
        totalCount: [{ required: true, message: "合计数量不能为空", trigger: "blur" }],
        totalPrice: [{ required: true, message: "合计金额，单位：元不能为空", trigger: "blur" }],
        status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getStockInPage(this.queryParams).then(response => {
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
        supplierId: undefined,
        inTime: undefined,
        totalCount: undefined,
        totalPrice: undefined,
        status: undefined,
        remark: undefined,
        fileUrl: undefined,
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
      this.title = "添加ERP 其它入库单";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getStockIn(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改ERP 其它入库单";
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
        if(!this.form.tenantId) this.form.tenantId = this.userInfo.tenantId;
        if(!this.form.deptId) this.form.deptId = this.userInfo.deptId;
        if(!this.form.userId) this.form.userId = this.userInfo.userId;
        if (this.form.id != null) {
          updateStockIn(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createStockIn(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除ERP 其它入库单编号为"' + id + '"的数据项?').then(function() {
          return deleteStockIn(id);
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
      this.$modal.confirm('是否确认导出所有ERP 其它入库单数据项?').then(() => {
          this.exportLoading = true;
          return exportStockInExcel(params);
        }).then(response => {
          this.$download.excel(response, 'ERP 其它入库单.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
