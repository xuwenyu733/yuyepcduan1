<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联流程ID" prop="processId">
        <el-input v-model="queryParams.processId" placeholder="请输入关联流程ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="出池时间" prop="outPoolTime">
        <el-date-picker v-model="queryParams.outPoolTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="出池重量(kg)" prop="outPoolWeight">
        <el-input v-model="queryParams.outPoolWeight" placeholder="请输入出池重量(kg)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="出池数量" prop="outPoolCount">
        <el-input v-model="queryParams.outPoolCount" placeholder="请输入出池数量" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="规格等级" prop="specification">
        <el-input v-model="queryParams.specification" placeholder="请输入规格等级" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="客户名称" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="销售单价" prop="salesPrice">
        <el-input v-model="queryParams.salesPrice" placeholder="请输入销售单价" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="销售总额" prop="totalAmount">
        <el-input v-model="queryParams.totalAmount" placeholder="请输入销售总额" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="品质评价(优/良/中/差)" prop="fishQuality">
        <el-input v-model="queryParams.fishQuality" placeholder="请输入品质评价(优/良/中/差)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="检测人" prop="testOperator">
        <el-input v-model="queryParams.testOperator" placeholder="请输入检测人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openForm(undefined)"
          v-hasPermi="['fish:purify-out-pool:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:purify-out-pool:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:purify-out-pool:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="关联流程ID" align="center" prop="processId" /> -->
      <el-table-column label="关联流程" align="center" prop="recordNumber" />
      <el-table-column label="出池时间" align="center" prop="outPoolTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.outPoolTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="出池重量(kg)" align="center" prop="outPoolWeight" />
      <el-table-column label="出池数量" align="center" prop="outPoolCount" />
      <el-table-column label="规格等级" align="center" prop="specification" />
      <el-table-column label="客户名称" align="center" prop="customerName" />
      <el-table-column label="销售单价" align="center" prop="salesPrice" />
      <el-table-column label="销售总额" align="center" prop="totalAmount" />
      <!-- (优/良/中/差) -->
      <el-table-column label="品质评价" align="center" prop="fishQuality" />
      <el-table-column label="检测人" align="center" prop="testOperator" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="部门" align="center" prop="deptId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
          <span v-else="scope.row.deptId">
            <deptName :deptId="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:purify-out-pool:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-out-pool:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <PurifyOutPoolForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as PurifyOutPoolApi from '@/api/fish/zkpurifyoutpool';
import PurifyOutPoolForm from './PurifyOutPoolForm.vue';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
export default {
  name: "PurifyOutPool",
  components: {
    PurifyOutPoolForm
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
      // 出池登记列表
      list: [],
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 选中行
      currentRow: {},
      checkedIds: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        processId: null,
        outPoolTime: [],
        outPoolWeight: null,
        outPoolCount: null,
        specification: null,
        customerName: null,
        salesPrice: null,
        totalAmount: null,
        fishQuality: null,
        testOperator: null,
        createTime: [],
        deptId: null,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await PurifyOutPoolApi.getPurifyOutPoolPage(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
      }
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
    /** 添加/修改操作 */
    openForm(id) {
      this.$refs["formRef"].open(id);
    },
    /** 删除按钮操作 */
    async handleDelete(row) {
      const id = row.id;
      await this.$modal.confirm('是否确认删除出池登记编号为"' + id + '"的数据项?')
      try {
        await PurifyOutPoolApi.deletePurifyOutPool(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除出池登记 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await PurifyOutPoolApi.deletePurifyOutPoolList(this.checkedIds);
        this.checkedIds = [];
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    handleRowCheckboxChange(records) {
      this.checkedIds = records.map((item) => item.id);
    },
    /** 导出按钮操作 */
    async handleExport() {
      await this.$modal.confirm('是否确认导出所有出池登记数据项?');
      try {
        this.exportLoading = true;
        const data = await PurifyOutPoolApi.exportPurifyOutPoolExcel(this.queryParams);
        this.$download.excel(data, '出池登记.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>