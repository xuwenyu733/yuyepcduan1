<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="接收邮箱" prop="toMail">
        <el-input v-model="queryParams.toMail" placeholder="请输入接收邮箱" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="发送状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="请选择发送状态" clearable>
          <el-option label="成功" :value="true"/>
          <el-option label="失败" :value="false"/>
        </el-select>
      </el-form-item>
      <el-form-item label="发送时间" prop="sendTime">
        <el-date-picker v-model="queryParams.sendTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" width="80"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="170">
        <template v-slot="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="接收邮箱" align="center" prop="toMail" min-width="160" :show-overflow-tooltip="true"/>
      <el-table-column label="用户编号" align="center" prop="userId" width="100"/>
      <el-table-column label="模板编号" align="center" prop="templateId" width="100"/>
      <el-table-column label="模板编码" align="center" prop="templateCode" width="140"/>
      <el-table-column label="模板标题" align="center" prop="templateTitle" min-width="140" :show-overflow-tooltip="true"/>
      <el-table-column label="发送状态" align="center" prop="sendStatus" width="100">
        <template v-slot="scope">
          <el-tag v-if="scope.row.sendStatus === true || scope.row.sendStatus === 1" type="success">成功</el-tag>
          <el-tag v-else-if="scope.row.sendStatus === false || scope.row.sendStatus === 0" type="danger">失败</el-tag>
          <span v-else>{{ scope.row.sendStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" width="170">
        <template v-slot="scope"><span>{{ parseTime(scope.row.sendTime) }}</span></template>
      </el-table-column>
      <el-table-column label="发送异常" align="center" prop="sendException" min-width="160" :show-overflow-tooltip="true"/>
      <el-table-column label="操作" align="center" width="100">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <el-dialog title="邮件日志详情" :visible.sync="open" width="700px" append-to-body>
      <el-descriptions :column="1" border v-if="detail">
        <el-descriptions-item label="接收邮箱">{{ detail.toMail }}</el-descriptions-item>
        <el-descriptions-item label="模板标题">{{ detail.templateTitle }}</el-descriptions-item>
        <el-descriptions-item label="模板内容">
          <div v-html="detail.templateContent"></div>
        </el-descriptions-item>
        <el-descriptions-item label="发送异常">{{ detail.sendException || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getMailLog, getMailLogPage } from '@/api/system/mail/log'

export default {
  name: 'SystemMailLog',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      list: [],
      open: false,
      detail: null,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        toMail: undefined,
        sendStatus: undefined,
        sendTime: []
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getMailLogPage(this.queryParams).then(response => {
        this.list = (response.data && response.data.list) || []
        this.total = (response.data && response.data.total) || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNo = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleView(row) {
      getMailLog(row.id).then(response => {
        this.detail = response.data || row
        this.open = true
      }).catch(() => {
        this.detail = row
        this.open = true
      })
    }
  }
}
</script>
