<template>
  <div class="tab-content">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="采购单号" prop="purchaseOrderNo">
        <el-input v-model="queryParams.purchaseOrderNo" placeholder="请输入采购单号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="品类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择品类" clearable size="small">
          <el-option v-for="dict in getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="$emit('open-form')"
          v-hasPermi="['fish:purify-purchase:create']">新增采购</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column label="采购日期" align="center" prop="purchaseDate" width="120">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.purchaseDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采购单号" align="center" prop="purchaseOrderNo" />
      <el-table-column label="品类" align="center" prop="category">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="source" />
      <el-table-column label="数量(kg)" align="center" prop="quantity" />
      <el-table-column label="单价(元/kg)" align="center" prop="unitPrice" />
      <el-table-column label="总金额(元)" align="center" prop="totalAmount">
        <template v-slot="scope">
          <span style="font-weight: bold; color: #409EFF">¥{{ scope.row.totalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="运输方式" align="center" prop="transportMethod">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_TRANSPORT" :value="scope.row.transportMethod" />
        </template>
      </el-table-column>
      <el-table-column label="负责人" align="center" prop="purchaseManager" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="$emit('open-form', scope.row.id)"
            v-hasPermi="['fish:purify-purchase:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-purchase:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo"
      :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import * as PurifyPurchaseApi from '@/api/fish/zkpurifypurchase';

export default {
  name: "PurchaseTab",
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        purchaseOrderNo: null,
        category: null
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
        const res = await PurifyPurchaseApi.getPurifyPurchasePage(this.queryParams);
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
        await PurifyPurchaseApi.deletePurifyPurchase(row.id);
        this.$modal.msgSuccess("删除成功");
        this.getList();
      } catch { }
    }
  }
};
</script>
