<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="采购管理" name="purchase">
        <PurchaseTab @open-form="openPurchaseForm" />
      </el-tab-pane>
      <el-tab-pane label="入池管理" name="archive">
        <ArchiveTab @open-form="openArchiveForm" />
      </el-tab-pane>
      <el-tab-pane label="优化出池" name="process">
        <ProcessTab 
          ref="processTab"
          @open-monitoring="openMonitoringForm"
          @open-treatment="openTreatmentForm"
          @open-outpool="openOutPoolForm"
          @view-detail="viewProcessDetail"
        />
      </el-tab-pane>
      <el-tab-pane label="数据统计" name="statistics">
        <StatisticsTab v-if="activeTab === 'statistics'" />
      </el-tab-pane>
      <el-tab-pane label="记录查询" name="record">
        <RecordTab @view-detail="viewProcessDetail" />
      </el-tab-pane>
      <el-tab-pane label="基础配置" name="config">
        <ConfigTab @open-pool-form="openPoolForm" />
      </el-tab-pane>
    </el-tabs>

    <!-- 弹窗组件 -->
    <PurifyPurchaseForm ref="purchaseFormRef" @success="refreshTab('purchase')" />
    <PurifyArchiveForm ref="archiveFormRef" @success="refreshTab('archive')" />
    <PurifyPoolForm ref="poolFormRef" @success="refreshTab('pool')" />
    <PurifyDailyMonitoringForm ref="monitoringFormRef" @success="onDetailSuccess" />
    <PurifyTreatmentForm ref="treatmentFormRef" @success="onDetailSuccess" />
    <PurifyOutPoolForm ref="outPoolFormRef" @success="onOutPoolSuccess" />
    
    <ProcessDetail ref="processDetailRef" @edit="handleEditDetail" @success="onDetailSuccess" />
  </div>
</template>

<script>
import PurchaseTab from './components/PurchaseTab.vue';
import ArchiveTab from './components/ArchiveTab.vue';
import ProcessTab from './components/ProcessTab.vue';
import StatisticsTab from './components/StatisticsTab.vue';
import RecordTab from './components/RecordTab.vue';
import ConfigTab from './components/ConfigTab.vue';
import ProcessDetail from './components/ProcessDetail.vue';

import PurifyPurchaseForm from '../zkpurifypurchase/PurifyPurchaseForm.vue';
import PurifyArchiveForm from './PurifyArchiveForm.vue';
import PurifyPoolForm from '../zkpurifypool/PurifyPoolForm.vue';
import PurifyDailyMonitoringForm from '../zkpurifydailymonitoring/PurifyDailyMonitoringForm.vue';
import PurifyTreatmentForm from '../zkpurifytreatment/PurifyTreatmentForm.vue';
import PurifyOutPoolForm from '../zkpurifyoutpool/PurifyOutPoolForm.vue';

export default {
  name: "FlavorOptimizationSystem",
  components: {
    PurchaseTab, ArchiveTab, ProcessTab, StatisticsTab, RecordTab, ConfigTab, ProcessDetail,
    PurifyPurchaseForm, PurifyArchiveForm, PurifyPoolForm, PurifyDailyMonitoringForm, PurifyTreatmentForm, PurifyOutPoolForm
  },
  data() {
    return {
      activeTab: 'purchase'
    };
  },
  methods: {
    refreshTab(name) {
      const tabMap = {
        purchase: 'PurchaseTab',
        archive: 'ArchiveTab',
        pool: 'PoolTab',
        process: 'ProcessTab'
      };
      // Find the component instance and call getList
      const findAndRefresh = (children) => {
        children.forEach(child => {
          if (child.$options.name === tabMap[name]) {
            child.getList();
          } else if (child.$children && child.$children.length > 0) {
            findAndRefresh(child.$children);
          }
        });
      };
      findAndRefresh(this.$children);
    },
    openPurchaseForm(id) { this.$refs.purchaseFormRef.open(id); },
    openArchiveForm(id) { this.$refs.archiveFormRef.open(id); },
    openPoolForm(id) { this.$refs.poolFormRef.open(id); },
    openMonitoringForm(row) {
      this.$refs.monitoringFormRef.open();
      this.$nextTick(() => { this.$refs.monitoringFormRef.formData.processId = row.id; });
    },
    openTreatmentForm(row) {
      this.$refs.treatmentFormRef.open();
      this.$nextTick(() => { this.$refs.treatmentFormRef.formData.processId = row.id; });
    },
    openOutPoolForm(row) {
      this.$refs.outPoolFormRef.open();
      this.$nextTick(() => {
        this.$refs.outPoolFormRef.formData.processId = row.id;
        this.$refs.outPoolFormRef.formData.outPoolCount = row.currentStockCount;
      });
    },
    viewProcessDetail(row) {
      this.$refs.processDetailRef.open(row);
    },
    handleEditDetail(type, row) {
      if (type === 'monitoring') this.$refs.monitoringFormRef.open(row.id);
      else if (type === 'treatment') this.$refs.treatmentFormRef.open(row.id);
      else if (type === 'outPool') this.$refs.outPoolFormRef.open(row.id);
    },
    onDetailSuccess() {
      if (this.$refs.processTab) this.$refs.processTab.getList();
      if (this.$refs.processDetailRef) this.$refs.processDetailRef.refresh();
    },
    onOutPoolSuccess() {
      this.onDetailSuccess();
      this.refreshTab('pool');
    }
  }
};
</script>

<style scoped>
.app-container { background: transparent; }

/* Tab：提高对比度，避免深灰字贴深色底看不清 */
::v-deep .el-tabs--border-card {
  background: transparent;
  border: 1px solid rgba(84, 144, 122, 0.28);
  box-shadow: none;
}
::v-deep .el-tabs--border-card > .el-tabs__header {
  background-color: #122033;
  border-bottom: 1px solid rgba(84, 144, 122, 0.28);
}
::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item {
  color: #c5d0dc !important;
  font-weight: 500;
  border-color: transparent !important;
}
::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item:hover {
  color: #e8edf2 !important;
}
::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active {
  background-color: rgba(84, 144, 122, 0.12) !important;
  color: #8fd4b8 !important;
  font-weight: 600;
  border-right-color: rgba(84, 144, 122, 0.28) !important;
  border-left-color: rgba(84, 144, 122, 0.28) !important;
}
::v-deep .el-tabs--border-card > .el-tabs__content {
  background-color: transparent;
  color: #e8edf2;
}

/* 筛选区：标签 / 输入框 / 占位符 */
::v-deep .el-form-item__label {
  color: #c5d0dc !important;
}
::v-deep .el-input__inner,
::v-deep .el-textarea__inner {
  background: #0f1a26 !important;
  border-color: rgba(84, 144, 122, 0.4) !important;
  color: #e8edf2 !important;
}
::v-deep .el-input__inner::placeholder,
::v-deep .el-textarea__inner::placeholder {
  color: #9aa8b6 !important;
  opacity: 1;
}
::v-deep .el-input__inner::-webkit-input-placeholder {
  color: #9aa8b6 !important;
}
::v-deep .el-input__inner::-moz-placeholder {
  color: #9aa8b6 !important;
}
::v-deep .el-input__inner:hover,
::v-deep .el-input__inner:focus {
  border-color: #54907a !important;
}
::v-deep .el-select .el-input__inner {
  color: #e8edf2 !important;
}
::v-deep .el-button--default {
  background: rgba(255, 255, 255, 0.08) !important;
  border-color: rgba(197, 208, 220, 0.45) !important;
  color: #e8edf2 !important;
}
::v-deep .el-button--default:hover,
::v-deep .el-button--default:focus {
  background: rgba(84, 144, 122, 0.18) !important;
  border-color: #54907a !important;
  color: #8fd4b8 !important;
}
::v-deep .el-button--primary {
  background: #2f6b5a !important;
  border-color: #54907a !important;
  color: #fff !important;
}
::v-deep .el-button--primary.is-plain {
  background: rgba(84, 144, 122, 0.15) !important;
  border-color: #54907a !important;
  color: #8fd4b8 !important;
}

::v-deep .el-table,
::v-deep .el-table__expanded-cell {
  background-color: transparent !important;
  color: #e8edf2;
}
::v-deep .el-table th,
::v-deep .el-table tr {
  background-color: transparent !important;
}
::v-deep .el-table th > .cell,
::v-deep .el-table td > .cell {
  color: #e8edf2;
}
::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background-color: rgba(84, 144, 122, 0.1) !important;
}
::v-deep .el-pagination {
  background-color: transparent;
  padding: 20px 0;
  color: #c5d0dc;
}
::v-deep .el-pagination button:disabled,
::v-deep .el-pagination .btn-next,
::v-deep .el-pagination .btn-prev,
::v-deep .el-pager li {
  background-color: transparent;
  color: #c5d0dc !important;
}
::v-deep .el-pager li.active {
  color: #8fd4b8 !important;
}

/* 提示条 / 文字按钮 / 左右子 Tab */
::v-deep .el-alert {
  background: rgba(84, 144, 122, 0.12) !important;
  border: 1px solid rgba(84, 144, 122, 0.35) !important;
  color: #e8edf2 !important;
}
::v-deep .el-alert__title,
::v-deep .el-alert__description {
  color: #e8edf2 !important;
}
::v-deep .el-button--text {
  color: #8fd4b8 !important;
}
::v-deep .el-button--text:hover {
  color: #a8e0c8 !important;
}
::v-deep .el-tabs--left .el-tabs__item {
  color: #c5d0dc !important;
}
::v-deep .el-tabs--left .el-tabs__item:hover {
  color: #e8edf2 !important;
}
::v-deep .el-tabs--left .el-tabs__item.is-active {
  color: #8fd4b8 !important;
}
::v-deep .el-tabs--left .el-tabs__active-bar,
::v-deep .el-tabs--left .el-tabs__nav-wrap::after {
  background-color: rgba(84, 144, 122, 0.45) !important;
}
::v-deep .el-range-separator {
  color: #c5d0dc !important;
}
::v-deep .el-input__prefix,
::v-deep .el-input__suffix,
::v-deep .el-select .el-input .el-select__caret {
  color: #9aa8b6 !important;
}
::v-deep .el-table .el-button--text {
  color: #8fd4b8 !important;
}
::v-deep .el-table .el-button--text[style*="color:#f56c6c"],
::v-deep .el-table .el-button--text.el-button--danger {
  color: #f87171 !important;
}
</style>
