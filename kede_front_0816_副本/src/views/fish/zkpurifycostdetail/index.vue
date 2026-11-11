<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联流程ID" prop="processId">
        <el-input v-model="queryParams.processId" placeholder="请输入关联流程ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)" prop="costType">
        <el-select v-model="queryParams.costType" placeholder="请选择成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)"
          clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="金额" prop="amount">
        <el-input v-model="queryParams.amount" placeholder="请输入金额" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发生日期" prop="occurrenceDate">
        <el-date-picker v-model="queryParams.occurrenceDate" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="备注说明" prop="notes">
        <el-input v-model="queryParams.notes" placeholder="请输入备注说明" clearable @keyup.enter.native="handleQuery" />
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
          v-hasPermi="['fish:purify-cost-detail:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:purify-cost-detail:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:purify-cost-detail:delete']">
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
      <!-- (labor:人工, electricity:电费, feed:饲料, health:动保) -->
      <!-- <el-table-column label="成本类型" align="center" prop="costType" /> -->
      <el-table-column label="成本类型" align="center" prop="costType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_COST_DETAIL_COST_TYPE" :value="scope.row.costType" />
        </template>
      </el-table-column>
      <el-table-column label="金额" align="center" prop="amount" />
      <!-- <el-table-column label="发生日期" align="center" prop="occurrenceDate" /> -->
      <el-table-column label="发生日期" align="center" prop="occurrenceDate" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.occurrenceDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注说明" align="center" prop="notes" />
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
            v-hasPermi="['fish:purify-cost-detail:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-cost-detail:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <PurifyCostDetailForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as PurifyCostDetailApi from '@/api/fish/zkpurifycostdetail';
import PurifyCostDetailForm from './PurifyCostDetailForm.vue';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
export default {
  name: "PurifyCostDetail",
  components: {
    PurifyCostDetailForm
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
      // 成本明细记录列表
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
        costType: null,
        amount: null,
        occurrenceDate: [],
        notes: null,
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
        const res = await PurifyCostDetailApi.getPurifyCostDetailPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除成本明细记录编号为"' + id + '"的数据项?')
      try {
        await PurifyCostDetailApi.deletePurifyCostDetail(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除成本明细记录 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await PurifyCostDetailApi.deletePurifyCostDetailList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有成本明细记录数据项?');
      try {
        this.exportLoading = true;
        const data = await PurifyCostDetailApi.exportPurifyCostDetailExcel(this.queryParams);
        this.$download.excel(data, '成本明细记录.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>