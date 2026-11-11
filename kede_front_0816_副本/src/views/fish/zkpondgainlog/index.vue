<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="系统id" prop="systemId">
        <el-input v-model="queryParams.systemId" placeholder="请输入系统id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="批次编号" prop="batchNumber">
        <el-input v-model="queryParams.batchNumber" placeholder="请输入批次编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="统计日期" prop="staticDay">
        <el-input v-model="queryParams.staticDay" placeholder="请输入统计日期" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="鱼苗数量" prop="fishNum">
        <el-input v-model="queryParams.fishNum" placeholder="请输入鱼苗数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="鱼苗规格" prop="fishSpec">
        <el-input v-model="queryParams.fishSpec" placeholder="请输入鱼苗规格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="今日鱼增加重量" prop="fishGainCount">
        <el-input v-model="queryParams.fishGainCount" placeholder="请输入今日鱼增加重量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <!-- <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
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
          v-hasPermi="['fish:zk-pond-gain-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-pond-gain-log:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-pond-gain-log:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="车间id" align="center" prop="workshopId" />
      <el-table-column label="系统id" align="center" prop="systemId" />
      <el-table-column label="存塘id" align="center" prop="pondId" />
      <!-- <el-table-column label="批次编号" align="center" prop="batchNumber" /> -->
      <el-table-column label="统计日期" align="center" prop="staticDay" />
      <!-- <el-table-column label="鱼苗数量" align="center" prop="fishNum" /> -->
      <!-- <el-table-column label="鱼苗规格" align="center" prop="fishSpec" /> -->
      <el-table-column label="今日鱼增加重量" align="center" prop="fishGainCount" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-pond-gain-log:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-pond-gain-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkPondGainLogForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkPondGainLogApi from '@/api/fish/zkpondgainlog';
import ZkPondGainLogForm from './ZkPondGainLogForm.vue';
export default {
  name: "ZkPondGainLog",
  components: {
    ZkPondGainLogForm
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
      // 鱼生长列表
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
        workshopId: null,
        systemId: null,
        pondId: null,
        batchNumber: null,
        staticDay: null,
        fishNum: null,
        fishSpec: null,
        fishGainCount: null,
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
        const res = await ZkPondGainLogApi.getZkPondGainLogPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除鱼生长编号为"' + id + '"的数据项?')
      try {
        await ZkPondGainLogApi.deleteZkPondGainLog(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除鱼生长 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkPondGainLogApi.deleteZkPondGainLogList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有鱼生长数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkPondGainLogApi.exportZkPondGainLogExcel(this.queryParams);
        this.$download.excel(data, '鱼生长.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>