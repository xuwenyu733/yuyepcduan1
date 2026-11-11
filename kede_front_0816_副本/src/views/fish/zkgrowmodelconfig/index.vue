<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="所属品种ID" prop="speciesId">
        <el-input v-model="queryParams.speciesId" placeholder="请输入所属品种ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="模型名称" prop="modelName">
        <el-input v-model="queryParams.modelName" placeholder="请输入模型名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="极限体长 (cm)" prop="lInf">
        <el-input v-model="queryParams.lInf" placeholder="请输入极限体长 (cm)" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="极限体重 (g)" prop="wInf">
        <el-input v-model="queryParams.wInf" placeholder="请输入极限体重 (g)" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="体长生长系数" prop="kLength">
        <el-input v-model="queryParams.kLength" placeholder="请输入体长生长系数" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="体重生长系数" prop="kWeight">
        <el-input v-model="queryParams.kWeight" placeholder="请输入体重生长系数" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="初始体重 (g)" prop="initialWeight">
        <el-input v-model="queryParams.initialWeight" placeholder="请输入初始体重 (g)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="初始体长 (cm)" prop="initialLength">
        <el-input v-model="queryParams.initialLength" placeholder="请输入初始体长 (cm)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="最适水温最小值" prop="optimalTempMin">
        <el-input v-model="queryParams.optimalTempMin" placeholder="请输入最适水温最小值" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="最适水温最大值" prop="optimalTempMax">
        <el-input v-model="queryParams.optimalTempMax" placeholder="请输入最适水温最大值" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="最适投饵率 (%)" prop="optimalFeedRate">
        <el-input v-model="queryParams.optimalFeedRate" placeholder="请输入最适投饵率 (%)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
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
          v-hasPermi="['fish:zk-grow-model-config:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-grow-model-config:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-grow-model-config:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="所属品种ID" align="center" prop="speciesId" />
      <el-table-column label="模型名称" align="center" prop="modelName" />
      <el-table-column label="极限体长 (cm)" align="center" prop="lInf" />
      <el-table-column label="极限体重 (g)" align="center" prop="wInf" />
      <el-table-column label="体长生长系数" align="center" prop="kLength" />
      <el-table-column label="体重生长系数" align="center" prop="kWeight" />
      <el-table-column label="初始体重 (g)" align="center" prop="initialWeight" />
      <el-table-column label="初始体长 (cm)" align="center" prop="initialLength" />
      <el-table-column label="最适水温最小值" align="center" prop="optimalTempMin" />
      <el-table-column label="最适水温最大值" align="center" prop="optimalTempMax" />
      <el-table-column label="最适投饵率 (%)" align="center" prop="optimalFeedRate" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="所属部门" align="center" prop="deptId" show-overflow-tooltip>
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
            v-hasPermi="['fish:zk-grow-model-config:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-grow-model-config:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkGrowModelConfigForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkGrowModelConfigApi from '@/api/fish/zkgrowmodelconfig';
import ZkGrowModelConfigForm from './ZkGrowModelConfigForm.vue';
export default {
  name: "ZkGrowModelConfig",
  components: {
    ZkGrowModelConfigForm,
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
      // 生长模型配置列表
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
        speciesId: null,
        modelName: null,
        lInf: null,
        wInf: null,
        kLength: null,
        kWeight: null,
        initialWeight: null,
        initialLength: null,
        optimalTempMin: null,
        optimalTempMax: null,
        optimalFeedRate: null,
        createTime: [],
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
        const res = await ZkGrowModelConfigApi.getZkGrowModelConfigPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除生长模型配置编号为"' + id + '"的数据项?')
      try {
        await ZkGrowModelConfigApi.deleteZkGrowModelConfig(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除生长模型配置 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkGrowModelConfigApi.deleteZkGrowModelConfigList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有生长模型配置数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkGrowModelConfigApi.exportZkGrowModelConfigExcel(this.queryParams);
        this.$download.excel(data, '生长模型配置.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>
