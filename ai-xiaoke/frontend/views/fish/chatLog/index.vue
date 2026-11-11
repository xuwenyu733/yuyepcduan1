<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="消息发送人" prop="roleName">
        <el-input v-model="queryParams.roleName" placeholder="请输入消息发送人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="对话所属人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入对话所属人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>  @change="updateQueryUserId" -->
      <el-form-item label="对话所属人" prop="userId">
        <el-select style="width:100%;" v-model="queryParams.userId" placeholder="请选择对话所属人" filterable clearable >
          <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="对话所属人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入对话所属人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="对话所属人部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入对话所属人部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <!-- <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:chat-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:chat-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="消息发送人" align="center" prop="roleName" />
      <el-table-column label="AI回复评分" align="center" prop="askScore" />
      <!-- <el-table-column label="聊天内容" align="center" prop="chatContent" show-overflow-tooltip /> -->
      <el-table-column label="聊天内容" align="center" prop="chatContent" min-width="150px" >
        <template v-slot="scope">
          <el-row>
            <el-link @click="orderNoClick(scope.row)" :underline="false" :show-overflow-tooltip="true" :title="scope.row.chatContent">
            {{ scope.row.chatContent&&scope.row.chatContent.length>10?scope.row.chatContent.substr(0, 9)+'...':scope.row.chatContent }}&nbsp;
            </el-link>
            <el-link :underline="false" v-clipboard:copy="scope.row.chatContent"
              v-clipboard:success="clipboardSuccess" icon="el-icon-document-copy" type="primary" />
          </el-row>
        </template>
      </el-table-column>
      <!-- <el-table-column label="对话所属人id" align="center" prop="userId" /> -->
      <el-table-column label="对话所属人" align="center" prop="userName" />
      <!-- <el-table-column label="对话所属人部门" align="center" prop="deptId" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:chat-log:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:chat-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="消息发送人" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入消息发送人" />
        </el-form-item>
        <el-form-item label="聊天内容">
          <editor v-model="form.chatContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="对话所属人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择对话所属人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item>
        <!-- <el-form-item label="对话所属人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入对话所属人id" />
        </el-form-item>
        <el-form-item label="对话所属人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入对话所属人" />
        </el-form-item>
        <el-form-item label="对话所属人部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入对话所属人部门" />
        </el-form-item> -->
        <!-- <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createChatLog, updateChatLog, deleteChatLog, getChatLog, getChatLogPage, exportChatLogExcel } from "@/api/fish/chatLog";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";

export default {
  name: "ChatLog",
  components: {
      FileBusUpload,
    Editor,
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
      // 聊天日志列表
      list: [],
      userList1: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        roleName: null,
        chatContent: null,
        userId: null,
        userName: null,
        deptId: null,
        createTime: [],
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
    this.getUserList();
  },
  methods: {
    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功");
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getChatLogPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>"+JSON.stringify(this.form));
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          let tempStr = `${obj.deptName}-${obj.nickname}`;
          obj.nickname = tempStr;
          // console.log("===========>"+JSON.stringify(obj));
        })
      })
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        roleName: undefined,
        chatContent: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        fid: undefined,
      };
      this.resetForm("form");
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加聊天日志";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getChatLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改聊天日志";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateChatLog(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createChatLog(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除聊天日志编号为"' + id + '"的数据项?').then(function() {
          return deleteChatLog(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有聊天日志数据项?').then(() => {
          this.exportLoading = true;
          return exportChatLogExcel(params);
        }).then(response => {
          this.$download.excel(response, '聊天日志.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
