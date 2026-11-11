<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="关联采购记录ID" prop="purchaseRecordId">
        <el-input v-model="queryParams.purchaseRecordId" placeholder="请输入关联采购记录ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="关联采购单号" prop="purchaseOrderNo">
        <el-input v-model="queryParams.purchaseOrderNo" placeholder="请输入关联采购单号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="品类" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入品类" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="来源" prop="source">
        <el-input v-model="queryParams.source" placeholder="请输入来源" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="入池时间" prop="inPoolTime">
        <el-date-picker v-model="queryParams.inPoolTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="入池总重量(kg)" prop="inPoolWeight">
        <el-input v-model="queryParams.inPoolWeight" placeholder="请输入入池总重量(kg)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="规格-单尾重(kg)" prop="avgWeight">
        <el-input v-model="queryParams.avgWeight" placeholder="请输入规格-单尾重(kg)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="入池尾数" prop="inPoolCount">
        <el-input v-model="queryParams.inPoolCount" placeholder="请输入入池尾数" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="池号" prop="poolNumber">
        <el-input v-model="queryParams.poolNumber" placeholder="请输入池号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="初始密度(kg/m³)" prop="density">
        <el-input v-model="queryParams.density" placeholder="请输入初始密度(kg/m³)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="优化等级(premium/wagyu/tuna)" prop="optimizationLevel">
        <el-input v-model="queryParams.optimizationLevel" placeholder="请输入优化等级(premium/wagyu/tuna)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="检验报告文件名/URL" prop="inspectionReport">
        <el-input v-model="queryParams.inspectionReport" placeholder="请输入检验报告文件名/URL" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="初始溶氧(mg/L)" prop="initialOxygen">
        <el-input v-model="queryParams.initialOxygen" placeholder="请输入初始溶氧(mg/L)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="初始PH值" prop="initialPh">
        <el-input v-model="queryParams.initialPh" placeholder="请输入初始PH值" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="初始水温(℃)" prop="initialTemperature">
        <el-input v-model="queryParams.initialTemperature" placeholder="请输入初始水温(℃)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="外观评估" prop="appearanceScore">
        <el-input v-model="queryParams.appearanceScore" placeholder="请输入外观评估" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="活力评估" prop="vitalityScore">
        <el-input v-model="queryParams.vitalityScore" placeholder="请输入活力评估" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="健康状况" prop="healthStatus">
        <el-select v-model="queryParams.healthStatus" placeholder="请选择健康状况" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="预期存活率(%)" prop="expectedSurvivalRate">
        <el-input v-model="queryParams.expectedSurvivalRate" placeholder="请输入预期存活率(%)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-input v-model="queryParams.riskLevel" placeholder="请输入风险等级" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="评估人" prop="assessor">
        <el-input v-model="queryParams.assessor" placeholder="请输入评估人" clearable @keyup.enter.native="handleQuery" />
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
          v-hasPermi="['fish:purify-archive:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:purify-archive:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:purify-archive:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="关联采购记录ID" align="center" prop="purchaseRecordId" /> -->
      <el-table-column label="关联采购单号" align="center" prop="purchaseOrderNo" />
      <!-- <el-table-column label="品类" align="center" prop="category" /> -->
      <el-table-column label="品类" align="center" prop="category" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="source" />
      <el-table-column label="入池时间" align="center" prop="inPoolTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.inPoolTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="入池总重量(kg)" align="center" prop="inPoolWeight" />
      <el-table-column label="规格-单尾重(kg)" align="center" prop="avgWeight" />
      <el-table-column label="入池尾数" align="center" prop="inPoolCount" />
      <el-table-column label="池号" align="center" prop="poolNumber" />
      <el-table-column label="初始密度(kg/m³)" align="center" prop="density" />
      <!-- <el-table-column label="优化等级(premium/wagyu/tuna)" align="center" prop="optimizationLevel" /> -->
      <el-table-column label="优化等级" align="center" prop="optimizationLevel" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_ARCHIVE_OPTIMIZATION_LEVEL" :value="scope.row.optimizationLevel" />
        </template>
      </el-table-column>
      <el-table-column label="检验报告文件名/URL" align="center" prop="inspectionReport" />
      <el-table-column label="初始溶氧(mg/L)" align="center" prop="initialOxygen" />
      <el-table-column label="初始PH值" align="center" prop="initialPh" />
      <el-table-column label="初始水温(℃)" align="center" prop="initialTemperature" />
      <el-table-column label="外观评估" align="center" prop="appearanceScore" />
      <el-table-column label="活力评估" align="center" prop="vitalityScore" />
      <el-table-column label="健康状况" align="center" prop="healthStatus" />
      <el-table-column label="预期存活率(%)" align="center" prop="expectedSurvivalRate" />
      <el-table-column label="风险等级" align="center" prop="riskLevel" />
      <el-table-column label="评估人" align="center" prop="assessor" />
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
            v-hasPermi="['fish:purify-archive:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-archive:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <PurifyArchiveForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
import PurifyArchiveForm from './PurifyArchiveForm.vue';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
export default {
  name: "PurifyArchive",
  components: {
    PurifyArchiveForm
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
      // 入池档案列表
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
        purchaseRecordId: null,
        purchaseOrderNo: null,
        category: null,
        source: null,
        inPoolTime: [],
        inPoolWeight: null,
        avgWeight: null,
        inPoolCount: null,
        poolNumber: null,
        density: null,
        optimizationLevel: null,
        inspectionReport: null,
        initialOxygen: null,
        initialPh: null,
        initialTemperature: null,
        appearanceScore: null,
        vitalityScore: null,
        healthStatus: null,
        expectedSurvivalRate: null,
        riskLevel: null,
        assessor: null,
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
        const res = await PurifyArchiveApi.getPurifyArchivePage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除入池档案编号为"' + id + '"的数据项?')
      try {
        await PurifyArchiveApi.deletePurifyArchive(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除入池档案 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await PurifyArchiveApi.deletePurifyArchiveList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有入池档案数据项?');
      try {
        this.exportLoading = true;
        const data = await PurifyArchiveApi.exportPurifyArchiveExcel(this.queryParams);
        this.$download.excel(data, '入池档案.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>