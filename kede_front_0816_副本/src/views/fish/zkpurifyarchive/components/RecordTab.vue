<template>
  <div class="tab-content">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="记录单号" prop="recordNumber">
        <el-input v-model="queryParams.recordNumber" placeholder="请输入记录单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="品类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择品类" clearable>
          <el-option v-for="dict in getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY)" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="poolNumber">
        <el-input v-model="queryParams.poolNumber" placeholder="请输入池号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in getDictDatas(DICT_TYPE.ZK_PURIFY_PROCESS_STATUS)" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      
      <template v-if="showAdvancedSearch">
        <el-form-item label="死亡率(%)">
          <el-input v-model="queryParams.minMortalityRate" placeholder="最小" style="width: 80px" />
          <span> - </span>
          <el-input v-model="queryParams.maxMortalityRate" placeholder="最大" style="width: 80px" />
        </el-form-item>
        <el-form-item label="掉秤率(%)">
          <el-input v-model="queryParams.minScaleDropRate" placeholder="最小" style="width: 80px" />
          <span> - </span>
          <el-input v-model="queryParams.maxScaleDropRate" placeholder="最大" style="width: 80px" />
        </el-form-item>
      </template>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="text" @click="showAdvancedSearch = !showAdvancedSearch">
          {{ showAdvancedSearch ? '收起高级搜索' : '高级搜索' }}
          <i :class="showAdvancedSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        </el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column label="记录单号" align="center" prop="recordNumber" width="180" />
      <el-table-column label="品类" align="center" prop="category">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_PROCESS_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="source" />
      <el-table-column label="池号" align="center" prop="poolNumber" />
      <el-table-column label="入池时间" align="center" prop="inPoolTime" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.inPoolTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="入池重量(kg)" align="center" prop="inPoolWeight" />
      <el-table-column label="出池重量(kg)" align="center" prop="totalOutWeight" />
      <el-table-column label="死亡率" align="center">
        <template v-slot="scope">
          <span :class="scope.row.deathRate > 5 ? 'text-danger' : ''">{{ scope.row.deathRate }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="掉秤率" align="center">
        <template v-slot="scope">
          <span :class="scope.row.weightLossRate > 10 ? 'text-warning' : ''">{{ scope.row.weightLossRate }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="$emit('view-detail', scope.row)">详情</el-button>
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
  name: "RecordTab",
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      showAdvancedSearch: false,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        recordNumber: null,
        category: null,
        poolNumber: null,
        status: null,
        minMortalityRate: null,
        maxMortalityRate: null,
        minScaleDropRate: null,
        maxScaleDropRate: null
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
        const res = await PurifyArchiveApi.getRecordQueryPage(this.queryParams);
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
      this.queryParams.minMortalityRate = null;
      this.queryParams.maxMortalityRate = null;
      this.queryParams.minScaleDropRate = null;
      this.queryParams.maxScaleDropRate = null;
      this.handleQuery();
    }
  }
};
</script>
