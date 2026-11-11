<template>
  <div class="tab-content">
    <el-alert title="从入池到出池的全流程操作记录" type="info" :closable="false" show-icon style="margin-bottom: 15px" />

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column label="记录单号" align="center" prop="recordNumber" width="180" />
      <el-table-column label="品类" align="center" prop="category" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="池号" align="center" prop="poolNumber" />
      <el-table-column label="入池时间" align="center" prop="startTime" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_PROCESS_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="存塘量(尾)" align="center" prop="currentStockCount">
        <template v-slot="scope">
          <el-tag type="success" effect="dark">{{ scope.row.currentStockCount }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="累计死亡" align="center">
        <template v-slot="scope">
          <span style="color: #F56C6C">{{ scope.row.totalDeathCount }} 尾 / {{ scope.row.totalDeathWeight }} kg</span>
        </template>
      </el-table-column>
      <el-table-column label="累计出池" align="center">
        <template v-slot="scope">
          <span style="color: #409EFF">{{ scope.row.totalOutCount }} 尾 / {{ scope.row.totalOutWeight }} kg</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="300">
        <template v-slot="scope">
          <el-button size="mini" type="primary" icon="el-icon-plus" @click="$emit('open-monitoring', scope.row)"
            v-if="scope.row.status === 'farming'">监测</el-button>
          <el-button size="mini" type="warning" icon="el-icon-first-aid-kit" @click="$emit('open-treatment', scope.row)"
            v-if="scope.row.status === 'farming'">处理</el-button>
          <el-button size="mini" type="success" icon="el-icon-sell" @click="$emit('open-outpool', scope.row)"
            v-if="scope.row.status === 'farming'">出池</el-button>
          <el-button size="mini" type="info" icon="el-icon-view"
            @click="$emit('view-detail', scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo"
      :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';

export default {
  name: "ProcessTab",
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        status: null
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
        const res = await PurifyProcessApi.getPurifyProcessPage(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
