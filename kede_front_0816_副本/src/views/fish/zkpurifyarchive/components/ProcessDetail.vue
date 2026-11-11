<template>
  <el-dialog title="流程详情" :visible.sync="visible" width="70%" append-to-body>
    <div v-if="currentProcess" class="process-detail">
      <el-descriptions title="基本信息" border :column="3">
        <el-descriptions-item label="记录单号">{{ currentProcess.recordNumber }}</el-descriptions-item>
        <el-descriptions-item label="池号">{{ currentProcess.poolNumber }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ currentProcess.operator }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_PROCESS_STATUS" :value="currentProcess.status" />
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ parseTime(currentProcess.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="存塘量">{{ currentProcess.currentStockCount }} 尾</el-descriptions-item>
      </el-descriptions>

      <h4 class="detail-title">日常监测记录</h4>
      <el-table :data="details.monitoring" stripe size="mini" border>
        <el-table-column label="日期" prop="monitorDate" width="160">
          <template v-slot="scope"><span>{{ parseTime(scope.row.monitorDate) }}</span></template>
        </el-table-column>
        <el-table-column label="溶氧" prop="oxygen" />
        <el-table-column label="PH" prop="ph" />
        <el-table-column label="水温" prop="temperature" />
        <el-table-column label="投喂(kg)" prop="feedAmount" />
        <el-table-column label="健康状况" prop="healthStatus">
          <template v-slot="scope"><dict-tag :type="DICT_TYPE.ZK_PURIFY_DAILY_MONITORING_HEALTH_STATUS"
              :value="scope.row.healthStatus" /></template>
        </el-table-column>
        <el-table-column label="死亡(尾/kg)">
          <template v-slot="scope">{{ scope.row.deathCount }} / {{ scope.row.deathWeight }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120">
          <template v-slot="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="$emit('edit', 'monitoring', scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete('monitoring', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <h4 class="detail-title">处理措施</h4>
      <el-table :data="details.treatment" stripe size="mini" border>
        <el-table-column label="日期" prop="treatmentDate" width="160">
          <template v-slot="scope"><span>{{ parseTime(scope.row.treatmentDate) }}</span></template>
        </el-table-column>
        <el-table-column label="类型" prop="type">
          <template v-slot="scope"><dict-tag :type="DICT_TYPE.ZK_PURIFY_TREATMENT_TYPE"
              :value="scope.row.type" /></template>
        </el-table-column>
        <el-table-column label="内容" prop="content" min-width="160" show-overflow-tooltip>
          <template v-slot="scope">
            <span>{{ formatContent(scope.row.content) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="剂量" prop="dosage" />
        <el-table-column label="操作人" prop="operator" />
        <el-table-column label="操作" align="center" width="120">
          <template v-slot="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="$emit('edit', 'treatment', scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete('treatment', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <h4 class="detail-title">出池记录</h4>
      <el-table :data="details.outPool" stripe size="mini" border>
        <el-table-column label="时间" prop="outPoolTime" width="160">
          <template v-slot="scope"><span>{{ parseTime(scope.row.outPoolTime) }}</span></template>
        </el-table-column>
        <el-table-column label="重量(kg)" prop="outPoolWeight" />
        <el-table-column label="数量(尾)" prop="outPoolCount" />
        <el-table-column label="客户" prop="customerName" />
        <el-table-column label="金额" prop="totalAmount" />
        <el-table-column label="品质" prop="fishQuality">
          <template v-slot="scope"><dict-tag :type="DICT_TYPE.ZK_PURIFY_ARCHIVE_SHOW_LEVEL"
              :value="scope.row.fishQuality" /></template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120">
          <template v-slot="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="$emit('edit', 'outPool', scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete('outPool', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-dialog>
</template>

<script>
import * as PurifyDailyMonitoringApi from '@/api/fish/zkpurifydailymonitoring';
import * as PurifyTreatmentApi from '@/api/fish/zkpurifytreatment';
import * as PurifyOutPoolApi from '@/api/fish/zkpurifyoutpool';
import { DICT_TYPE } from "@/utils/dict";
import { html2Text } from '@/utils/index';

export default {
  name: "ProcessDetail",
  data() {
    return {
      visible: false,
      currentProcess: null,
      details: {
        monitoring: [],
        treatment: [],
        outPool: []
      }
    };
  },
  methods: {
    formatContent(content) {
      if (!content) return '';
      return html2Text(content).replace(/\s+/g, ' ').trim();
    },
    async open(row) {
      this.currentProcess = row;
      this.visible = true;
      this.refresh();
    },
    async refresh() {
      if (!this.currentProcess) return;
      const [mRes, tRes, oRes] = await Promise.all([
        PurifyDailyMonitoringApi.getPurifyDailyMonitoringPage({ processId: this.currentProcess.id, pageSize: 100 }),
        PurifyTreatmentApi.getPurifyTreatmentPage({ processId: this.currentProcess.id, pageSize: 100 }),
        PurifyOutPoolApi.getPurifyOutPoolPage({ processId: this.currentProcess.id, pageSize: 100 })
      ]);
      this.details.monitoring = mRes.data.list;
      this.details.treatment = tRes.data.list;
      this.details.outPool = oRes.data.list;
    },
    async handleDelete(type, row) {
      await this.$modal.confirm('确认删除该记录吗？');
      try {
        if (type === 'monitoring') await PurifyDailyMonitoringApi.deletePurifyDailyMonitoring(row.id);
        else if (type === 'treatment') await PurifyTreatmentApi.deletePurifyTreatment(row.id);
        else if (type === 'outPool') await PurifyOutPoolApi.deletePurifyOutPool(row.id);
        this.$modal.msgSuccess("删除成功");
        this.refresh();
        this.$emit('success');
      } catch { }
    }
  }
};
</script>

<style scoped>
.detail-title {
  margin: 20px 0 10px;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
  font-size: 16px;
  color: #fff;
}
.process-detail {
  padding: 0 10px;
}
</style>
