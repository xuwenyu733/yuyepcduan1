<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="群组" prop="groupId">
        <el-input v-model="queryParams.groupId" placeholder="请输入群组" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="群组" prop="groupId">
          <el-select style="width:100%;" v-model="queryParams.groupId" placeholder="请选择群组" filterable clearable @keyup.enter.native="handleQuery">
            <el-option v-for="(item, index) in chatGroupList1" :key="item.id+'__'+index" :label="item.groupName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      <!-- <el-form-item label="群组id(1对1时有效)" prop="targetUserId">
        <el-input v-model="queryParams.targetUserId" placeholder="请输入群组id(1对1时有效)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="消息(文件消息时是url)" prop="msg">
        <el-input v-model="queryParams.msg" placeholder="请输入消息(文件消息时是url)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="消息类型(0:文本;1:图片;2:视频;3:文档;4:其他;)" prop="msgType">
        <el-select v-model="queryParams.msgType" placeholder="请选择消息类型(0:文本;1:图片;2:视频;3:文档;4:其他;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="发送人" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入发送人" clearable @keyup.enter.native="handleQuery"/>
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
          v-hasPermi="['fish:zk-chat-msg:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-chat-msg:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-chat-msg:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="编号" align="center" prop="id" />
      <!-- <el-table-column label="群组id(群聊消息时有效)" align="center" prop="groupId" /> -->
      <!-- <el-table-column label="群组id(1对1时有效)" align="center" prop="targetUserId" /> -->
      <el-table-column label="接受群组" align="center" prop="groupName" />
      <el-table-column label="接受人" align="center" prop="targetUserId" />
      <el-table-column label="接受人" align="center" prop="targetUserId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.targetUserName">{{ scope.row.targetUserName }}</span>
          <span v-else="scope.row.targetUserId">
            <userName :user-id="scope.row.userId"></userName>
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="消息" align="center" prop="msg" /> -->
      <el-table-column label="消息" align="left" prop="msg" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.msg"
            v-clipboard:success="clipboardSuccess">
            {{ scope.row.msg }}
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="文件id" align="center" prop="fid" /> -->
      <!-- <el-table-column label="消息类型" align="center" prop="msgType" /> -->
      <el-table-column label="消息类型" align="center" prop="msgType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_CHAT_MSG_TYPE" :value="scope.row.msgType" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="发送人" align="center" prop="userId" />
      <el-table-column label="发送人部门" align="center" prop="deptId" /> -->
      <el-table-column label="发送人" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.userName">{{ scope.row.userName }}</span>
          <span v-else="scope.row.userId">
            <userName :user-id="scope.row.userId"></userName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发送人部门" align="center" prop="deptId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
          <span v-else="scope.row.deptId">
            <deptName :dept-id="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-chat-msg:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-chat-msg:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkChatMsgForm ref="formRef" @success="getList" @sonSendData="parentReceiveData" />
  </div>
</template>

<script>
import * as ZkChatMsgApi from '@/api/fish/zkchatmsg';
import ZkChatMsgForm from './ZkChatMsgForm.vue';
export default {
  name: "ZkChatMsg",
  components: {
    ZkChatMsgForm
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
      // 聊天消息列表
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
        groupId: null,
        targetUserId: null,
        msg: null,
        fid: null,
        msgType: null,
        createTime: [],
        userId: null,
        deptId: null,
      },
      chatGroupList1: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    parentReceiveData(params) {
      if(params.chatGroupList1) {
        this.chatGroupList1 = params.chatGroupList1;
      }
    },
    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功");
    },
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await ZkChatMsgApi.getZkChatMsgPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除聊天消息编号为"' + id + '"的数据项?')
      try {
        await ZkChatMsgApi.deleteZkChatMsg(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除聊天消息 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkChatMsgApi.deleteZkChatMsgList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有聊天消息数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkChatMsgApi.exportZkChatMsgExcel(this.queryParams);
        this.$download.excel(data, '聊天消息.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>