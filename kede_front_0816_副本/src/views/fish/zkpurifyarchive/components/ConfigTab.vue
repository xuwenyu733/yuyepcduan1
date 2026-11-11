<template>
  <div class="config-container">
    <el-tabs v-model="subActiveTab" tab-position="left">
      <el-tab-pane label="字典配置" name="dict">
        <DictTab />
      </el-tab-pane>
      <el-tab-pane label="鱼池管理" name="pool">
        <PoolTab @open-form="openPoolForm" />
      </el-tab-pane>
      <el-tab-pane label="成本管理" name="cost">
        <CostTab />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import DictTab from './DictTab.vue';
import PoolTab from './PoolTab.vue';
import CostTab from './CostTab.vue';

export default {
  name: "ConfigTab",
  components: {
    DictTab,
    PoolTab,
    CostTab
  },
  data() {
    return {
      subActiveTab: 'dict'
    };
  },
  methods: {
    openPoolForm(id) {
      this.$emit('open-pool-form', id);
    },
    refreshPool() {
      // This is a bit tricky with nested components, but we can use refs
      // Or just rely on the components loading themselves on mount
    }
  }
};
</script>

<style scoped>
.config-container {
  padding: 10px;
}
::v-deep .el-tabs--left .el-tabs__header.is-left {
  margin-right: 20px;
  background: #122033;
  border-radius: 6px;
  border: 1px solid rgba(84, 144, 122, 0.28);
  padding: 8px 0;
}
::v-deep .el-tabs--left .el-tabs__item {
  color: #c5d0dc !important;
  font-weight: 500;
}
::v-deep .el-tabs--left .el-tabs__item:hover {
  color: #e8edf2 !important;
}
::v-deep .el-tabs--left .el-tabs__item.is-active {
  color: #8fd4b8 !important;
  background: rgba(84, 144, 122, 0.12);
}
</style>
