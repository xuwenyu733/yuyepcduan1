<template>
  <div class="tab-content">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="$emit('open-form')"
          v-hasPermi="['fish:purify-pool:create']">添加鱼池</el-button>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column label="池号" align="center" prop="poolNumber" />
      <el-table-column label="大小(m³)" align="center" prop="size" />
      <el-table-column label="位置" align="center" prop="location" />
      <el-table-column label="负责人" align="center" prop="manager" />
      <el-table-column label="状态" align="center" prop="status">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_POOL_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="$emit('open-form', scope.row.id)"
            v-hasPermi="['fish:purify-pool:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-pool:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo"
      :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import * as PurifyPoolApi from '@/api/fish/zkpurifypool';

export default {
  name: "PoolTab",
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10
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
        const res = await PurifyPoolApi.getPurifyPoolPage(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
      }
    },
    async handleDelete(row) {
      await this.$modal.confirm('确认删除吗？');
      try {
        await PurifyPoolApi.deletePurifyPool(row.id);
        this.$modal.msgSuccess("删除成功");
        this.getList();
      } catch { }
    }
  }
};
</script>
