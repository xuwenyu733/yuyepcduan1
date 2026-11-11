<template>
  <div class="app-container">
    <el-card shadow="never">
      <div slot="header" class="clearfix">
        <span>各鱼苗期成本</span>
      </div>
      
      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column label="首次投放时间" align="center" prop="first_day" width="160">
          <template v-slot="scope">
            <span>{{ parseTime(scope.row.first_day) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="车间名称" align="center" prop="workshop_name" />
        <el-table-column label="系统编号" align="center" prop="pond_num" />
        <el-table-column label="存塘编号" align="center" prop="pond_detail_num" />
        <!-- <el-table-column label="存塘ID" align="center" prop="pond_id" /> -->
        <!-- <el-table-column label="鱼苗规格" align="center" prop="fish_spec" /> -->
        <el-table-column label="鱼苗期成本(0-30天)" align="center" prop="seedling_stage_cost" />
        <el-table-column label="幼鱼期成本(31-90天)" align="center" prop="juvenile_stage_cost" />
        <el-table-column label="成鱼期前期成本(91-180天)" align="center" prop="early_adult_stage_cost" />
        <el-table-column label="成鱼期后期成本(181-365天)" align="center" prop="late_adult_stage_cost" />
        <el-table-column label="总饲料成本(元)" align="center" prop="total_feeding_cost" />
        <el-table-column label="每公斤鱼成本(元)" align="center" prop="cost_per_kg" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export function getVariousFryStagesCost() {
  return request({
    url: '/fish/zk-feeding-log/various-fry-stages-cost',
    method: 'get'
  })
}

export default {
  name: "VariousFryStages",
  data() {
    return {
      loading: true,
      list: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      getVariousFryStagesCost().then(response => {
        this.list = response.data;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    }
  }
};
</script>

<style scoped>
</style>