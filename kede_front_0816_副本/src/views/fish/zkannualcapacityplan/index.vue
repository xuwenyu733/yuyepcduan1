<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="计划年份 (如:2026)" prop="planYear">
        <el-input v-model="queryParams.planYear" placeholder="请输入计划年份 (如:2026)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="关联的品种ID" prop="speciesId">
        <el-input v-model="queryParams.speciesId" placeholder="请输入关联的品种ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="品种名称" prop="speciesName">
        <el-input v-model="queryParams.speciesName" placeholder="请输入品种名称 (如:大西洋鲑鱼)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="当前产能/产量 (单位:吨)" prop="currentCapacity">
        <el-input v-model="queryParams.currentCapacity" placeholder="请输入当前产能/产量 (单位:吨)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="目标产能 (单位:吨)" prop="targetCapacity">
        <el-input v-model="queryParams.targetCapacity" placeholder="请输入目标产能 (单位:吨)" clearable
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
          v-hasPermi="['fish:zk-annual-capacity-plan:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-annual-capacity-plan:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-annual-capacity-plan:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="计划年份" align="center" prop="planYear" />
      <!-- <el-table-column label="关联的品种ID" align="center" prop="speciesId" /> -->
      <el-table-column label="品种名称" align="center" prop="speciesName" />
      <el-table-column label="当前产能(吨)" align="center" prop="currentCapacity" />
      <el-table-column label="目标产能(吨)" align="center" prop="targetCapacity" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-annual-capacity-plan:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-annual-capacity-plan:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkAnnualCapacityPlanForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkAnnualCapacityPlanApi from '@/api/fish/zkannualcapacityplan';
import ZkAnnualCapacityPlanForm from './ZkAnnualCapacityPlanForm.vue';
export default {
  name: "ZkAnnualCapacityPlan",
  components: {
    ZkAnnualCapacityPlanForm
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
      // 年度产能计划列表
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
        planYear: null,
        speciesId: null,
        speciesName: null,
        currentCapacity: null,
        targetCapacity: null,
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
        const res = await ZkAnnualCapacityPlanApi.getZkAnnualCapacityPlanPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除年度产能计划编号为"' + id + '"的数据项?')
      try {
        await ZkAnnualCapacityPlanApi.deleteZkAnnualCapacityPlan(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除年度产能计划 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkAnnualCapacityPlanApi.deleteZkAnnualCapacityPlanList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有年度产能计划数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkAnnualCapacityPlanApi.exportZkAnnualCapacityPlanExcel(this.queryParams);
        this.$download.excel(data, '年度产能计划.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>