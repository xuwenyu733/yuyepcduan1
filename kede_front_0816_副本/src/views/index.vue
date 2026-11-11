<template>
  <div class="dashboard-editor-container" :class="{ 'is-daping-home': showMainHome }">
    <sellPanel v-if="showSell"></sellPanel>
    <backPanel v-if="showBack"></backPanel>
    <oprPanel v-if="showOpr"></oprPanel>
    <!-- 现在的登陆进去是首页屏，点击数字化监控才是当前页面，且当前页面已经升级 -->
    <IndexDaping3 v-if="showMainHome" embedded />


<!--    <panel-group @handleSetLineChartData="handleSetLineChartData" />-->

<!--    <el-row style="background:#fff;padding:8px 8px 0;margin-bottom:16px;">-->
<!--      <line-chart :chart-data="lineChartData" />-->
<!--    </el-row>-->

<!--    <el-row :gutter="32">-->
<!--      <el-col :xs="24" :sm="24" :lg="8">-->
<!--        <div class="chart-wrapper">-->
<!--          <raddar-chart />-->
<!--        </div>-->
<!--      </el-col>-->
<!--      <el-col :xs="24" :sm="24" :lg="8">-->
<!--        <div class="chart-wrapper">-->
<!--          <pie-chart />-->
<!--        </div>-->
<!--      </el-col>-->
<!--      <el-col :xs="24" :sm="24" :lg="8">-->
<!--        <div class="chart-wrapper">-->
<!--          <bar-chart />-->
<!--        </div>-->
<!--      </el-col>-->
<!--    </el-row>-->
  </div>
</template>

<script>
import PanelGroup from './dashboard/PanelGroup'
import LineChart from './dashboard/LineChart'
import RaddarChart from './dashboard/RaddarChart'
import PieChart from './dashboard/PieChart'
import BarChart from './dashboard/BarChart'
import {mapGetters} from "vuex";
import {checkRole} from "@/utils/permission";
import sellPanel from "@/views/dashboard/sellPanel";
import backPanel from "@/views/dashboard/backPanel";
import oprPanel from "@/views/dashboard/oprPanel";
import mainhome from "@/views/keyshow/mainhome.vue";
import IndexDaping3 from "@/views/index_daping3.vue";
import { HT_MANYS, XS_MANYS, KEDE_MANYS } from "@/utils/constants";
import { createZkFishOperationLog, updateZkFishOperationLog, deleteZkFishOperationLog, getZkFishOperationLog, getZkFishOperationLogPage, exportZkFishOperationLogExcel } from "@/api/fish/zkFishOperationLog";

const lineChartData = {
  newVisitis: {
    expectedData: [100, 120, 161, 134, 105, 160, 165],
    actualData: [120, 82, 91, 154, 162, 140, 145]
  },
  messages: {
    expectedData: [200, 192, 120, 144, 160, 130, 140],
    actualData: [180, 160, 151, 106, 145, 150, 130]
  },
  purchases: {
    expectedData: [80, 100, 121, 104, 105, 90, 100],
    actualData: [120, 90, 100, 138, 142, 130, 130]
  },
  shoppings: {
    expectedData: [130, 140, 141, 142, 145, 150, 160],
    actualData: [120, 82, 91, 154, 162, 140, 130]
  }
}

export default {
  name: 'Index',
  components: {
    sellPanel,
    backPanel,
    oprPanel,
    mainhome,
    PanelGroup,
    LineChart,
    RaddarChart,
    PieChart, 
    IndexDaping3, 
    BarChart
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'userId',
      'avatar',
      'nickname',
      'device'
    ]),
  },
  data() {
    return {
      showSell: 0,
      showBack: 0,
      showOpr: 0,
      showMainHome:0,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
      },
      lineChartData: lineChartData.newVisitis
    }
  },
  created() {
    console.log(`查看当前页面名称index.vue：`, this.$router.currentRoute.name);
    // this.showOpr = true;
    this.showMainHome = true;
    this.getList();
    this
    return;
    // console.log(`==========>nodeEnv:${process.env.IS_DEV},this.isDev:${this.isDev}`);
    if(checkRole(XS_MANYS)){
      this.showSell = true;
    }else{
      if(checkRole(HT_MANYS)){
        this.showBack = true;
      }else {
        this.showOpr = true;
      }
    }
    // if(checkRole(['yy'])){
    // }
  },
  methods: {
    handleSetLineChartData(type) {
      this.lineChartData = lineChartData[type]
    },
    getList() {
      this.loading = true;
      let params = JSON.parse(JSON.stringify(this.queryParams));
      if(params.fishSpecStr == '全部') {
        params.fishSpecStr = undefined;
      }
      // 执行查询
      getZkFishOperationLogPage(params).then(response => {
        console.log(`getZkFishOperationLogPage==================>response:`, response);
      });
    },
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  min-height: 800px;
  padding: 8px;
  background-color: rgb(240, 242, 245);
  position: relative;

  &.is-daping-home {
    height: 100%;
    min-height: 0;
    padding: 0;
    background: transparent;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    ::v-deep .dashboard-wrapper {
      flex: 1;
      width: 100%;
      height: 100%;
      min-height: 0;
    }
  }

  .chart-wrapper {
    background: #fff;
    padding: 8px 8px 0;
    margin-bottom: 16px;
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
