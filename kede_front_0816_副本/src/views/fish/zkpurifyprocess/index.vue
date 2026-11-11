<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联档案ID" prop="archiveId">
        <el-input v-model="queryParams.archiveId" placeholder="请输入关联档案ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="流程单号" prop="recordNumber">
        <el-input v-model="queryParams.recordNumber" placeholder="请输入流程单号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态(farming:优化中, out_pool:已完成)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(farming:优化中, out_pool:已完成)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始确认时间" prop="startTime">
        <el-date-picker v-model="queryParams.startTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="负责人" prop="operator">
        <el-input v-model="queryParams.operator" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="累计死亡数量" prop="totalDeathCount">
        <el-input v-model="queryParams.totalDeathCount" placeholder="请输入累计死亡数量" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="累计死亡重量" prop="totalDeathWeight">
        <el-input v-model="queryParams.totalDeathWeight" placeholder="请输入累计死亡重量" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="累计出池数量" prop="totalOutCount">
        <el-input v-model="queryParams.totalOutCount" placeholder="请输入累计出池数量" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="累计出池重量" prop="totalOutWeight">
        <el-input v-model="queryParams.totalOutWeight" placeholder="请输入累计出池重量" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="当前存塘数量" prop="currentStockCount">
        <el-input v-model="queryParams.currentStockCount" placeholder="请输入当前存塘数量" clearable
          @keyup.enter.native="handleQuery" />
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
          v-hasPermi="['fish:purify-process:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:purify-process:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:purify-process:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="关联档案ID" align="center" prop="archiveId" /> -->
      <el-table-column label="流程单号" align="center" prop="recordNumber" />
      <!-- (farming:优化中, out_pool:已完成) -->
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="开始确认时间" align="center" prop="startTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="负责人" align="center" prop="operator" />
      <el-table-column label="累计死亡数量" align="center" prop="totalDeathCount" />
      <el-table-column label="累计死亡重量" align="center" prop="totalDeathWeight" />
      <el-table-column label="累计出池数量" align="center" prop="totalOutCount" />
      <el-table-column label="累计出池重量" align="center" prop="totalOutWeight" />
      <el-table-column label="当前存塘数量" align="center" prop="currentStockCount" />
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
            v-hasPermi="['fish:purify-process:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-process:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <PurifyProcessForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
import PurifyProcessForm from './PurifyProcessForm.vue';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
export default {
  name: "PurifyProcess",
  components: {
    PurifyProcessForm
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
      // 优化流程记录列表
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
        archiveId: null,
        recordNumber: null,
        status: null,
        startTime: [],
        operator: null,
        totalDeathCount: null,
        totalDeathWeight: null,
        totalOutCount: null,
        totalOutWeight: null,
        currentStockCount: null,
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
        const res = await PurifyProcessApi.getPurifyProcessPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除优化流程记录编号为"' + id + '"的数据项?')
      try {
        await PurifyProcessApi.deletePurifyProcess(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除优化流程记录 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await PurifyProcessApi.deletePurifyProcessList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有优化流程记录数据项?');
      try {
        this.exportLoading = true;
        const data = await PurifyProcessApi.exportPurifyProcessExcel(this.queryParams);
        this.$download.excel(data, '优化流程记录.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>