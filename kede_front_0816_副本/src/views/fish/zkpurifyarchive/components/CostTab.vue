<template>
  <div class="tab-content">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="关联流程" prop="processId">
        <el-select v-model="queryParams.processId" placeholder="请选择关联流程" clearable filterable>
          <el-option v-for="item in processList" :key="item.id" :label="item.recordNumber" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="成本类型" prop="costType">
        <el-select v-model="queryParams.costType" placeholder="请选择成本类型" clearable>
          <el-option v-for="dict in getDictDatas(DICT_TYPE.ZK_PURIFY_COST_DETAIL_COST_TYPE)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
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
    </el-row>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column label="关联流程" align="center" prop="recordNumber" />
      <el-table-column label="成本类型" align="center" prop="costType">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_COST_DETAIL_COST_TYPE" :value="scope.row.costType" />
        </template>
      </el-table-column>
      <el-table-column label="金额" align="center" prop="amount">
        <template v-slot="scope">
          <span style="font-weight: bold; color: #F56C6C">¥{{ scope.row.amount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发生日期" align="center" prop="occurrenceDate" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.occurrenceDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注说明" align="center" prop="notes" show-overflow-tooltip />
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
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
import PurifyCostDetailForm from '../../zkpurifycostdetail/PurifyCostDetailForm.vue';
import { DICT_TYPE } from "@/utils/dict";

export default {
  name: "CostTab",
  components: {
    PurifyCostDetailForm
  },
  data() {
    return {
      loading: true,
      total: 0,
      list: [],
      processList: [],
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        processId: null,
        costType: null
      },
    };
  },
  created() {
    this.getProcessList();
    this.getList();
  },
  methods: {
    async getProcessList() {
      const res = await PurifyProcessApi.getPurifyProcessPage({ pageSize: 500 });
      this.processList = res.data.list;
    },
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
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    openForm(id) {
      this.$refs["formRef"].open(id);
    },
    async handleDelete(row) {
      await this.$modal.confirm('是否确认删除该成本记录?');
      try {
        await PurifyCostDetailApi.deletePurifyCostDetail(row.id);
        this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    }
  }
};
</script>
