<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联流程ID" prop="processId">
        <el-input v-model="queryParams.processId" placeholder="请输入关联流程ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="处理日期" prop="treatmentDate">
        <el-date-picker v-model="queryParams.treatmentDate" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="措施类型(用药/水质调节等)" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择措施类型(用药/水质调节等)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="剂量" prop="dosage">
        <el-input v-model="queryParams.dosage" placeholder="请输入剂量" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作人" prop="operator">
        <el-input v-model="queryParams.operator" placeholder="请输入操作人" clearable @keyup.enter.native="handleQuery" />
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
          v-hasPermi="['fish:purify-treatment:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:purify-treatment:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:purify-treatment:delete']">
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
      <el-table-column label="处理日期" align="center" prop="treatmentDate" width="170">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.treatmentDate) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="措施类型(用药/水质调节等)" align="center" prop="type" /> -->
      <el-table-column label="措施类型" align="center" prop="type" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_TREATMENT_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="措施内容" align="center" prop="content" min-width="160" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ formatContent(scope.row.content) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剂量" align="center" prop="dosage" />
      <el-table-column label="操作人" align="center" prop="operator" />
      <el-table-column label="备注说明" align="center" prop="notes" min-width="120" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ formatContent(scope.row.notes) }}</span>
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
          <span v-else>
            <deptName :deptId="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:purify-treatment:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-treatment:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <PurifyTreatmentForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as PurifyTreatmentApi from '@/api/fish/zkpurifytreatment';
import PurifyTreatmentForm from './PurifyTreatmentForm.vue';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { html2Text } from '@/utils/index';
export default {
  name: "PurifyTreatment",
  components: {
    PurifyTreatmentForm
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
      // 处理措施记录列表
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
        treatmentDate: [],
        type: null,
        content: null,
        dosage: null,
        operator: null,
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
    /** 富文本措施内容：列表去标签展示，避免直接露出 <p> 等 HTML */
    formatContent(content) {
      if (!content) return '';
      return html2Text(content).replace(/\s+/g, ' ').trim();
    },
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await PurifyTreatmentApi.getPurifyTreatmentPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除处理措施记录编号为"' + id + '"的数据项?')
      try {
        await PurifyTreatmentApi.deletePurifyTreatment(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除处理措施记录 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await PurifyTreatmentApi.deletePurifyTreatmentList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有处理措施记录数据项?');
      try {
        this.exportLoading = true;
        const data = await PurifyTreatmentApi.exportPurifyTreatmentExcel(this.queryParams);
        this.$download.excel(data, '处理措施记录.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>