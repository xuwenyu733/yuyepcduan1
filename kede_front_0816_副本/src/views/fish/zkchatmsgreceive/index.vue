<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="聊天消息" prop="chatMsgId">
        <el-input v-model="queryParams.chatMsgId" placeholder="请输入聊天消息id" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="消息所属人" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入消息所属人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="消息处理类型(0:待通知;1:已通知;2:已确认;3:已查看;4:已回复;)" prop="handleType">
        <el-select v-model="queryParams.handleType" placeholder="请选择消息处理类型(0:待通知;1:已通知;2:已确认;3:已查看;4:已回复;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openForm(undefined)"
          v-hasPermi="['fish:zk-chat-msg-receive:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-chat-msg-receive:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-chat-msg-receive:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="编号" align="center" prop="id" />
      <!-- <el-table-column label="聊天消息id" align="center" prop="chatMsgId" /> zkChatMsgDO -->
      <!-- <el-table-column label="聊天消息" align="center" prop="chatMsgId" /> -->
      <el-table-column label="消息类型" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <span >
          {{ scope.row.zkChatMsgDO.msgInfo }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="聊天消息" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <span >
          {{ scope.row.zkChatMsgDO.msg }}
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="消息所属人" align="center" prop="userId" /> -->
      <el-table-column label="消息所属人" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.userName">{{ scope.row.userName }}</span>
          <span v-if="scope.row.userId">
            <userName :user-id="scope.row.userId"></userName>
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="消息处理类型" align="center" prop="handleType" /> -->
      <el-table-column label="是否接受" align="center" prop="handleType" show-overflow-tooltip>
        <template v-slot="scope">
          <!-- <dict-tag :type="DICT_TYPE.ZK_CHAT_MSG_RECEIVE_HANDLE_TYPE" :value="scope.row.handleType" /> -->
          <span v-if="scope.row.handleType == 1" style="color: green;">已通知</span>
          <span v-if="scope.row.handleType == 0" style="color: orange;">未通知</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-chat-msg-receive:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-chat-msg-receive:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkChatMsgReceiveForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkChatMsgReceiveApi from '@/api/fish/zkchatmsgreceive';
import ZkChatMsgReceiveForm from './ZkChatMsgReceiveForm.vue';
export default {
  name: "ZkChatMsgReceive",
  components: {
    ZkChatMsgReceiveForm
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 聊天消息接受列表
      list: [],
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 选中行
      currentRow: {},
      checkedIds: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        chatMsgId: null,
        userId: null,
        handleType: null,
        createTime: [],
        deptId: null,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await ZkChatMsgReceiveApi.getZkChatMsgReceivePage(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 添加/修改操作 */
    openForm(id) {
      this.$refs["formRef"].open(id);
    },
    /** 删除按钮操作 */
    async handleDelete(row) {
      const id = row.id;
      await this.$modal.confirm('是否确认删除聊天消息接受编号为"' + id + '"的数据项?')
      try {
        await ZkChatMsgReceiveApi.deleteZkChatMsgReceive(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除聊天消息接受 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkChatMsgReceiveApi.deleteZkChatMsgReceiveList(this.checkedIds);
        this.checkedIds = [];
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    handleRowCheckboxChange(records) {
      this.checkedIds = records.map((item) => item.id);
    },
    /** 导出按钮操作 */
    async handleExport() {
      await this.$modal.confirm('是否确认导出所有聊天消息接受数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkChatMsgReceiveApi.exportZkChatMsgReceiveExcel(this.queryParams);
        this.$download.excel(data, '聊天消息接受.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>