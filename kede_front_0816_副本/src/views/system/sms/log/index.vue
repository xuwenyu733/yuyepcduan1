<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="发送状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="请选择发送状态" clearable>
          <el-option v-for="dict in getDictDatas(DICT_TYPE.SYSTEM_SMS_SEND_STATUS)"
                     :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"/>
        </el-select>
      </el-form-item>
      <el-form-item label="接收状态" prop="receiveStatus">
        <el-select v-model="queryParams.receiveStatus" placeholder="请选择接收状态" clearable>
          <el-option v-for="dict in getDictDatas(DICT_TYPE.SYSTEM_SMS_RECEIVE_STATUS)"
                     :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"/>
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
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['system:sms-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" width="80"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="170">
        <template v-slot="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="手机号" align="center" prop="mobile" width="120"/>
      <el-table-column label="短信内容" align="center" prop="templateContent" :show-overflow-tooltip="true" min-width="200"/>
      <el-table-column label="发送状态" align="center" prop="sendStatus" width="100">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_SMS_SEND_STATUS" :value="scope.row.sendStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" width="170">
        <template v-slot="scope"><span>{{ parseTime(scope.row.sendTime) }}</span></template>
      </el-table-column>
      <el-table-column label="短信渠道" align="center" prop="channelCode" width="120">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_SMS_CHANNEL_CODE" :value="scope.row.channelCode"/>
        </template>
      </el-table-column>
      <el-table-column label="模板编码" align="center" prop="templateCode" width="140"/>
      <el-table-column label="短信类型" align="center" prop="templateType" width="100">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_SMS_TEMPLATE_TYPE" :value="scope.row.templateType"/>
        </template>
      </el-table-column>
      <el-table-column label="接收状态" align="center" prop="receiveStatus" width="100">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_SMS_RECEIVE_STATUS" :value="scope.row.receiveStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="接收时间" align="center" prop="receiveTime" width="170">
        <template v-slot="scope"><span>{{ parseTime(scope.row.receiveTime) }}</span></template>
      </el-table-column>
      <el-table-column label="API 发送编码" align="center" prop="apiSendCode" width="120" :show-overflow-tooltip="true"/>
      <el-table-column label="API 发送消息" align="center" prop="apiSendMsg" width="140" :show-overflow-tooltip="true"/>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>
  </div>
</template>

<script>
import { getSmsLogPage, exportSmsLogExcel } from '@/api/system/sms/smsLog'
import { DICT_TYPE, getDictDatas } from '@/utils/dict'

export default {
  name: 'SystemSmsLog',
  data() {
    return {
      DICT_TYPE,
      loading: true,
      showSearch: true,
      exportLoading: false,
      total: 0,
      list: [],
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        channelId: undefined,
        templateId: undefined,
        mobile: undefined,
        sendStatus: undefined,
        sendTime: [],
        receiveStatus: undefined,
        receiveTime: []
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getDictDatas,
    getList() {
      this.loading = true
      getSmsLogPage(this.queryParams).then(response => {
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
    handleExport() {
      this.$modal.confirm('是否确认导出所有短信日志数据项?').then(() => {
        this.exportLoading = true
        return exportSmsLogExcel(this.queryParams)
      }).then(response => {
        this.$download.excel(response, '短信日志.xls')
        this.exportLoading = false
      }).catch(() => {
        this.exportLoading = false
      })
    }
  }
}
</script>
