<template>
  <div class="app-container">
    <doc-alert v-if="false" title="站内信配置" url="https://doc.iocoder.cn/notify/" />

    <!-- 轻重缓急分栏 -->
    <el-tabs v-model="urgencyTab" class="notify-urgency-tabs" @tab-click="handleUrgencyTab">
      <el-tab-pane name="all">
        <span slot="label">全部</span>
      </el-tab-pane>
      <el-tab-pane name="urgent">
        <span slot="label"><i class="urgency-dot urgent" />紧急</span>
      </el-tab-pane>
      <el-tab-pane name="priority">
        <span slot="label"><i class="urgency-dot priority" />优先</span>
      </el-tab-pane>
      <el-tab-pane name="normal">
        <span slot="label"><i class="urgency-dot normal" />日常</span>
      </el-tab-pane>
    </el-tabs>

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="是否已读" prop="readStatus">
        <el-select v-model="queryParams.readStatus" placeholder="请选择状态" clearable @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                     :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="发送时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="warning" plain size="mini" icon="el-icon-bell" @click="previewSound">试听提示音</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleUpdateList">标记已读</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleUpdateAll">全部已读</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" ref="tables" :data="displayList" :row-class-name="tableRowClassName">
      <el-table-column type="selection" width="55" />
      <el-table-column label="缓急" align="center" width="80">
        <template v-slot="scope">
          <el-tag size="mini" :type="getUrgencyTagType(scope.row.urgencyLevel)" effect="plain">
            {{ getUrgencyLabel(scope.row.urgencyLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送人" align="center" prop="templateNickname" width="120" />
      <el-table-column label="发送时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="templateType" width="80">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_NOTIFY_TEMPLATE_TYPE" :value="scope.row.templateType" />
        </template>
      </el-table-column>
      <el-table-column label="内容" align="center" prop="templateContent" show-overflow-tooltip />
      <el-table-column label="是否已读" align="center" prop="readStatus" width="80">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.readStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template v-slot="scope">
          <el-button v-show="!scope.row.readStatus" size="mini" type="text" icon="el-icon-check" @click="handleUpdateSingle(scope.row)">已读</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

  </div>
</template>

<script>
import {getMyNotifyMessagePage, updateAllNotifyMessageRead, updateNotifyMessageRead} from "@/api/system/notify/message";
import {
  enrichNotifyList,
  filterByUrgency,
  playNotifySound,
  getUrgencyLabel,
  getUrgencyTagType,
  NOTIFY_URGENCY,
} from "@/utils/notifyUrgency";

export default {
  name: "SystemMyNotify",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      list: [],
      urgencyTab: 'all',
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        readStatus: 'false',
        createTime: []
      },
    };
  },
  computed: {
    displayList() {
      return filterByUrgency(this.list, this.urgencyTab)
    },
  },
  created() {
    this.getList();
  },
  methods: {
    getUrgencyLabel,
    getUrgencyTagType,
    getList() {
      this.loading = true;
      getMyNotifyMessagePage(this.queryParams).then(response => {
        this.list = enrichNotifyList(response.data.list || []);
        this.total = response.data.total;
        this.loading = false;
      }).catch(() => { this.loading = false });
    },
    handleUrgencyTab() {
      // 前端分栏过滤，不改分页请求
    },
    previewSound() {
      const level = this.urgencyTab === 'all' ? NOTIFY_URGENCY.URGENT : this.urgencyTab
      playNotifySound(level)
      this.$modal.msgSuccess(`已播放「${getUrgencyLabel(level)}」提示音`)
    },
    tableRowClassName({ row }) {
      if (row.urgencyLevel === NOTIFY_URGENCY.URGENT) return 'notify-row-urgent'
      if (row.urgencyLevel === NOTIFY_URGENCY.PRIORITY) return 'notify-row-priority'
      return ''
    },
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.urgencyTab = 'all'
      this.handleQuery();
    },
    handleUpdateList() {
      let list = this.$refs["tables"].selection;
      if (list.length === 0) {
        return;
      }
      this.handleUpdate(list.map(v => v.id))
    },
    handleUpdateSingle(row) {
      this.handleUpdate([row.id])
    },
    handleUpdate(ids) {
      updateNotifyMessageRead(ids).then(() => {
        this.$modal.msgSuccess("标记已读成功！");
        this.getList();
      });
    },
    handleUpdateAll(){
      updateAllNotifyMessageRead().then(() => {
        this.$modal.msgSuccess("全部已读成功！");
        this.getList();
      });
    }
  }
}
</script>

<style scoped>
.notify-urgency-tabs {
  margin-bottom: 8px;
}
.urgency-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 4px;
  vertical-align: middle;
}
.urgency-dot.urgent { background: #f56c6c; }
.urgency-dot.priority { background: #e6a23c; }
.urgency-dot.normal { background: #909399; }
</style>

<style>
.notify-row-urgent > td {
  background: rgba(245, 108, 108, 0.08) !important;
}
.notify-row-priority > td {
  background: rgba(230, 162, 60, 0.08) !important;
}
</style>
