<template>
  <div class="tab-content">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="单号" prop="purchaseOrderNo">
        <el-input v-model="queryParams.purchaseOrderNo" placeholder="请输入单号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="池号" prop="poolNumber">
        <el-input v-model="queryParams.poolNumber" placeholder="请输入池号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="$emit('open-form')"
          v-hasPermi="['fish:purify-archive:create']">新建入池</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column label="品类" align="center" prop="category">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="source" />
      <el-table-column label="入池时间" align="center" prop="inPoolTime" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.inPoolTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总重(kg)" align="center" prop="inPoolWeight" />
      <el-table-column label="单尾重(kg)" align="center" prop="avgWeight" />
      <el-table-column label="数量(尾)" align="center" prop="inPoolCount" />
      <el-table-column label="密度(kg/m³)" align="center" prop="density" />
      <el-table-column label="池号" align="center" prop="poolNumber" />
      <el-table-column label="优化等级" align="center" prop="optimizationLevel">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_ARCHIVE_OPTIMIZATION_LEVEL" :value="scope.row.optimizationLevel" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="$emit('open-form', scope.row.id)"
            v-hasPermi="['fish:purify-archive:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-archive:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo"
      :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';

export default {
  name: "ArchiveTab",
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        purchaseOrderNo: null,
        poolNumber: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    async getList() {
      this.loading = true;
      try {
        const res = await PurifyArchiveApi.getPurifyArchivePage(this.queryParams);
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
    async handleDelete(row) {
      await this.$modal.confirm('确认删除吗？');
      try {
        await PurifyArchiveApi.deletePurifyArchive(row.id);
        this.$modal.msgSuccess("删除成功");
        this.getList();
      } catch { }
    }
  }
};
</script>
