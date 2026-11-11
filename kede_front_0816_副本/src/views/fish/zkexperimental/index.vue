<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="实验编号" prop="experimentNumber">
        <el-input v-model="queryParams.experimentNumber" placeholder="请输入实验编号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="实验标题" prop="experimentTitle">
        <el-input v-model="queryParams.experimentTitle" placeholder="请输入实验标题" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-input v-model="queryParams.pondDetailNum" placeholder="请输入池号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="品种" prop="variety">
        <el-input v-model="queryParams.variety" placeholder="请输入品种" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="personInCharge">
        <el-input v-model="queryParams.personInCharge" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人id" prop="personInChargeId">
        <el-input v-model="queryParams.personInChargeId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker v-model="queryParams.startTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker v-model="queryParams.endTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="实验状态(0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;)" prop="experimentalStatus">
        <el-select v-model="queryParams.experimentalStatus" placeholder="请选择实验状态(0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="实验目标" prop="experimentalObjective">
        <el-input v-model="queryParams.experimentalObjective" placeholder="请输入实验目标" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="实验方法" prop="experimentalMethod">
        <el-input v-model="queryParams.experimentalMethod" placeholder="请输入实验方法" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item> -->
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openForm(undefined)"
          v-hasPermi="['fish:zk-experimental:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-experimental:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-experimental:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="实验编号" align="center" prop="experimentNumber" />
      <el-table-column label="实验标题" align="center" prop="experimentTitle" />
      <el-table-column label="池号" align="center" prop="poolNumbers">
        <template v-slot="scope">
          <el-tag v-for="num in scope.row.poolNumbers" :key="num" size="mini" style="margin-right: 5px;">{{ num }}</el-tag>
        </template>
      </el-table-column>
      <!-- <el-table-column label="品种" align="center" prop="variety" /> -->
      <el-table-column label="负责人" align="center" prop="personInCharge" />
      <!-- <el-table-column label="负责人id" align="center" prop="personInChargeId" /> -->
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <!-- (0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;) -->
      <el-table-column label="实验状态" align="center" prop="experimentalStatus" />
      <el-table-column label="实验目标" align="center" prop="experimentalObjective" />
      <el-table-column label="实验内容" align="center" prop="experimentalContent" />
      <el-table-column label="实验方法" align="center" prop="experimentalMethod" />
      <el-table-column label="主要结果" align="center" prop="mainResults" />
      <el-table-column label="效果评估" align="center" prop="effectEvaluation" />
      <el-table-column label="完成人" align="center" prop="completedBy" />
      <el-table-column label="完成时间" align="center" prop="completionTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.completionTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="部门" align="center" prop="deptId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
          <span v-if="scope.row.deptId">
            <deptName :deptId="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-experimental:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-experimental:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkExperimentalForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkExperimentalApi from '@/api/fish/zkexperimental';
import ZkExperimentalForm from './ZkExperimentalForm.vue';
export default {
  name: "ZkExperimental",
  components: {
    ZkExperimentalForm
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
      // 实验列表
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
        experimentNumber: null,
        experimentTitle: null,
        pondDetailNum: null,
        variety: null,
        personInCharge: null,
        personInChargeId: null,
        startTime: [],
        endTime: [],
        experimentalStatus: null,
        experimentalObjective: null,
        experimentalContent: null,
        experimentalMethod: null,
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
        const res = await ZkExperimentalApi.getZkExperimentalPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除实验编号为"' + id + '"的数据项?')
      try {
        await ZkExperimentalApi.deleteZkExperimental(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除实验 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkExperimentalApi.deleteZkExperimentalList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有实验数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkExperimentalApi.exportZkExperimentalExcel(this.queryParams);
        this.$download.excel(data, '实验.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>