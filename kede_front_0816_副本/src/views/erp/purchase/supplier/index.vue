<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="供应商名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入供应商名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="联系人" prop="contact">
        <el-input v-model="queryParams.contact" placeholder="请输入联系人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="手机号码" prop="mobile">
        <el-input v-model="queryParams.mobile" placeholder="请输入手机号码" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="联系电话" prop="telephone">
        <el-input v-model="queryParams.telephone" placeholder="请输入联系电话" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="电子邮箱" prop="email">
        <el-input v-model="queryParams.email" placeholder="请输入电子邮箱" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="传真" prop="fax">
        <el-input v-model="queryParams.fax" placeholder="请输入传真" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="开启状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择开启状态" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input v-model="queryParams.sort" placeholder="请输入排序" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="纳税人识别号" prop="taxNo">
        <el-input v-model="queryParams.taxNo" placeholder="请输入纳税人识别号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="税率" prop="taxPercent">
        <el-input v-model="queryParams.taxPercent" placeholder="请输入税率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="开户行" prop="bankName">
        <el-input v-model="queryParams.bankName" placeholder="请输入开户行" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="开户账号" prop="bankAccount">
        <el-input v-model="queryParams.bankAccount" placeholder="请输入开户账号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="开户地址" prop="bankAddress">
        <el-input v-model="queryParams.bankAddress" placeholder="请输入开户地址" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['erp:supplier:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['erp:supplier:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="供应商编号" align="center" prop="id" /> -->
      <el-table-column label="供应商名称" align="center" prop="name" />
      <el-table-column label="联系人" align="center" prop="contact" />
      <el-table-column label="手机号码" align="center" prop="mobile" />
      <!-- <el-table-column label="联系电话" align="center" prop="telephone" /> -->
      <!-- <el-table-column label="电子邮箱" align="center" prop="email" /> -->
      <!-- <el-table-column label="传真" align="center" prop="fax" /> -->
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="开启状态" align="center" prop="status">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <!-- <el-table-column label="开启状态" align="center" prop="status" /> -->
      <!-- <el-table-column label="排序" align="center" prop="sort" /> -->
      <!-- <el-table-column label="纳税人识别号" align="center" prop="taxNo" /> -->
      <!-- <el-table-column label="税率" align="center" prop="taxPercent" /> -->
      <!-- <el-table-column label="开户行" align="center" prop="bankName" /> -->
      <!-- <el-table-column label="开户账号" align="center" prop="bankAccount" /> -->
      <!-- <el-table-column label="开户地址" align="center" prop="bankAddress" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['erp:supplier:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['erp:supplier:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="供应商名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="手机号码" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in getDictDatas(DICT_TYPE.COMMON_STATUS)" :key="parseInt(dict.value)" :label="parseInt(dict.value)">
              {{dict.label}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- <el-form-item label="联系电话" prop="telephone">
          <el-input v-model="form.telephone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="传真" prop="fax">
          <el-input v-model="form.fax" placeholder="请输入传真" />
        </el-form-item>
        <el-form-item label="开启状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
        </el-form-item>
        <el-form-item label="纳税人识别号" prop="taxNo">
          <el-input v-model="form.taxNo" placeholder="请输入纳税人识别号" />
        </el-form-item>
        <el-form-item label="税率" prop="taxPercent">
          <el-input v-model="form.taxPercent" placeholder="请输入税率" />
        </el-form-item>
        <el-form-item label="开户行" prop="bankName">
          <el-input v-model="form.bankName" placeholder="请输入开户行" />
        </el-form-item>
        <el-form-item label="开户账号" prop="bankAccount">
          <el-input v-model="form.bankAccount" placeholder="请输入开户账号" />
        </el-form-item>
        <el-form-item label="开户地址" prop="bankAddress">
          <el-input v-model="form.bankAddress" placeholder="请输入开户地址" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createSupplier, updateSupplier, deleteSupplier, getSupplier, getSupplierPage, exportSupplierExcel } from "@/api/erp/supplier";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "Supplier",
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
      // ERP 供应商列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        name: null,
        contact: null,
        mobile: null,
        telephone: null,
        email: null,
        fax: null,
        remark: null,
        status: null,
        sort: null,
        taxNo: null,
        taxPercent: null,
        bankName: null,
        bankAccount: null,
        bankAddress: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [{ required: true, message: "供应商名称不能为空", trigger: "blur" }],
        status: [{ required: true, message: "开启状态不能为空", trigger: "blur" }],
        sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
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
      getSupplierPage(this.queryParams).then(response => {
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
        name: undefined,
        contact: undefined,
        mobile: undefined,
        telephone: undefined,
        email: undefined,
        fax: undefined,
        remark: undefined,
        status: 1,
        sort: 1,
        taxNo: undefined,
        taxPercent: undefined,
        bankName: undefined,
        bankAccount: undefined,
        bankAddress: undefined,
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
      this.title = "添加ERP 供应商";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getSupplier(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改ERP 供应商";
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
          updateSupplier(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createSupplier(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除ERP 供应商编号为"' + id + '"的数据项?').then(function() {
          return deleteSupplier(id);
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
      this.$modal.confirm('是否确认导出所有ERP 供应商数据项?').then(() => {
          this.exportLoading = true;
          return exportSupplierExcel(params);
        }).then(response => {
          this.$download.excel(response, 'ERP 供应商.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
