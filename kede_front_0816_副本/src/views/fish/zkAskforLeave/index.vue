<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="请假名称" prop="leaveName">
        <el-input v-model="queryParams.leaveName" placeholder="请输入请假名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="请假天数" prop="leaveDay">
        <el-input v-model="queryParams.leaveDay" placeholder="请输入请假天数" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="开始时间" prop="startTime">
        <el-date-picker v-model="queryParams.startTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker v-model="queryParams.endTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item> -->
      <!-- <el-form-item label="状态(0:待审批;1:已审批;2:已销假;-1:待申请;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:待审批;1:已审批;2:已销假;-1:待申请;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item> -->
      <el-form-item label="发起人" prop="userId">
        <el-select style="width:100%;" v-model="queryParams.userId" placeholder="请选择发起人" filterable clearable  @change="updateUserId">
          <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_ASKFOR_LEAVE_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="请假人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入请假人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="请假人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入请假人所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
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
                   v-hasPermi="['pl:zk-askfor-leave:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-askfor-leave:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="请假类型" align="center" prop="leaveName" />
      <el-table-column label="请假说明" align="center" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ plainLeaveContent(scope.row.leaveContent) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="请假天数" align="center" prop="leaveDay" /> -->
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="状态(0:待审批;1:已审批;2:已销假;-1:待申请;)" align="center" prop="status" /> -->
      <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_ASKFOR_LEAVE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="请假人id" align="center" prop="userId" /> -->
      <!-- <el-table-column label="发起人" align="center" prop="userName" /> -->
      <el-table-column label="发起人" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.userName">{{ scope.row.userName }}</span>
          <span v-else="scope.row.userId">
            <userName :user-id="scope.row.userId"></userName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发起人部门" align="center" prop="deptId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
          <span v-else="scope.row.deptId">
            <deptName :deptId="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="请假人" align="center" prop="deptId" /> -->
      <!-- <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleDetail(scope.row)">审批进度</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-askfor-leave:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-askfor-leave:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改)  :rules="rules"  -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="请假类型" prop="leaveName">
          <el-select v-model="form.leaveName" placeholder="请输入请假类型" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in askforTypeList" :key="dict.value"
              :label="dict.label" :value="dict.label" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="请假名称" prop="leaveName">
          <el-input v-model="form.leaveName" placeholder="请输入请假名称" />
        </el-form-item> -->
        <el-form-item label="请假说明">
          <editor v-model="form.leaveContent" :min-height="192"/>
        </el-form-item>
        <!-- <el-form-item label="请假天数" prop="leaveDay">
          <el-input v-model="form.leaveDay" placeholder="请输入请假天数" :readonly="true" />
        </el-form-item> -->
        <!-- <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable v-model="form.startTime" type="date" value-format="timestamp" placeholder="选择开始时间"
            format="yyyy-MM-dd HH:mm:ss"
           />
           <el-date-picker clearable type="datetimerange" value-format="timestamp" placeholder="选择结束时间"
            format="yyyy-MM-dd HH:mm:ss"
           />
        </el-form-item> -->
        <!-- format="yyyy-MM-dd HH:mm:ss" value-format="yyyy-MM-dd HH:mm:ss" -->
        <el-form-item label="请假时间" prop="value1">
           <el-date-picker
            v-model="value1"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            @change="value1Change"
            end-placeholder="结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发起人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择发起人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审批人" prop="approveUserId" v-if="false">
          <el-select style="width:100%;" v-model="form.approveUserId" placeholder="请选择审批人" filterable clearable>
            <el-option v-for="(item, index) in userList1" :key="item.id+'_a_'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="抄送人" prop="noticeUserId">
          <el-select style="width:100%;" v-model="form.noticeUserId" placeholder="请选择抄送人" filterable clearable>
            <el-option v-for="(item, index) in userList1" :key="item.id+'_n_'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="editType == 2">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_ASKFOR_LEAVE_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_askfor_leave"></FileBusUpload>
        </el-form-item>
        <!-- <el-form-item label="状态(0:待审批;1:已审批;2:已销假;-1:待申请;)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="请假人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入请假人id" />
        </el-form-item>
        <el-form-item label="请假人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入请假人所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :loading="operationLoading">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkAskforLeave, updateZkAskforLeave, deleteZkAskforLeave, getZkAskforLeave, getZkAskforLeavePage, exportZkAskforLeaveExcel } from "@/api/fish/zkAskforLeave";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID, html2Text } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { UserInfoKey } from "@/utils/constants";
import { removeToken } from "@/utils/auth";
import { listSimpleUsersByRole } from "@/api/system/user";
import {getTaskListByProcessInstanceId} from "@/api/bpm/task";


export default {
  name: "ZkAskforLeave",
  components: {
      FileBusUpload,
    Editor,
  },
  data() {
    return {
      userInfo: {},
      value1: "",
      // 遮罩层
      loading: true,
      operationLoading: false,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 请假列表
      list: [],
      askforTypeList: [],
      userList1: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      taskParam: {//流程参数
        approveUserId: '',
        processKey: 'apply_for_leave',
        approveNo: '',
        businessKey: '',
        taskId: '',
        processInstanceId: '',
        finishProcess: 'n',
        approveMessage: '',
        variables: {},
        submitToPrev: 'y'//退回后的流程再次提交直接到上一次的退回人那里
      },
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        leaveName: null,
        leaveContent: null,
        leaveDay: null,
        startTime: [],
        endTime: [],
        status: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      editType: 1, // 1:添加;2:修正;
      // 表单校验
      rules: {
        // startTime: [{ required: true, message: "开始时间不能为空", trigger: "blur" }],
        // endTime: [{ required: true, message: "结束时间不能为空", trigger: "blur" }],
        value1: [{ required: true, message: "请假时间不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    console.log(`===============>userInfo:${JSON.stringify(this.userInfo)}`);
    if(!(this.userInfo && this.userInfo.deptDO && this.userInfo.deptDO.leaderUserId)) {
      removeToken();
      this.$router.push({path: "/login"})
      return;
    }
    this.form.approveUserId = this.userInfo.deptDO.leaderUserId;
    // console.log(`查看当前页面(请假)名称：`, this.$router.currentRoute.name);
    this.getList();
    this.getUserList();
    this.judgeIsNeedAdd();
    this.askforTypeList = this.getDictDatas(DICT_TYPE.ZK_ASKFOR_LEAVE_ASKFOR_TYPE);
  },
  methods: {
    /** 117：列表请假说明去 HTML，避免富文本标签直出 */
    plainLeaveContent(val) {
      if (val == null || val === '') return '';
      return html2Text(String(val)).replace(/\s+/g, ' ').trim();
    },
    updateZkAskforType(askforType) {
      let curr = null;
      this.askforTypeList.forEach(item => {
        if(askforType == item.value) {
          curr = item;
          return;
        }
      })
      if(curr != null) {
        this.form.leaveName = curr.label;
      }
      console.log(`==============>a:${JSON.stringify(this.form)}`);
    },
    // 1. 当前页面休眠中： 由其他页面跳转当前页面，弹出添加数据弹窗
    judgeIsNeedAdd() {
      const socketParam = this.$route.params && this.$route.params.socketParam && JSON.parse(this.$route.params.socketParam);
      console.log(`socketParam==========>`, socketParam);
      if(socketParam && socketParam.operateKey && socketParam.operateKey == "ADD_DATA" && socketParam.content) {
        let temp = JSON.parse(socketParam.content);
        // if(temp.supplierId) temp.supplierId = parseInt(temp.supplierId)
        this.handleAdd(temp);
      }
    },
    // 2. 当前页面活着： 接受消息
    socketParentReceive(msgParam){
      if(!(msgParam && msgParam.includes("{"))) return;
      let socketParam = JSON.parse(msgParam);
      console.log(`views/fish/zkAskforLeave/index.vue====location.href：${location.href}==>收到后台发送过来的消息：`, socketParam);
      if(socketParam && socketParam.operateKey == "SURE_ADD") {
        this.submitForm(() => {
          this.$router.push({
          path: '/workmng/zk-askfor-leave',
              query: { },
          });
        });
        console.log(`执行了点击操作`)
      } else if(socketParam && socketParam.operateKey && this.my_common.judgeOperateUrl(socketParam.targetPath)
        && socketParam.operateKey == "ADD_DATA" && socketParam.content) {
        let temp = JSON.parse(socketParam.content);
        if(!this.open) this.handleAdd(temp);
        else {
          this.form = {...this.form, ...temp};
        }
      }
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkAskforLeavePage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    value1Change(a,b,c) {
      this.value1 = a;
      // /(9*60*60*1000)
      let hour = (a[1].getTime()-a[0].getTime())/(24*60*60*1000);
      console.log(`=============>hour:${hour}`);
      let currHour = hour.toFixed(2);
      // console.log(`b:${a[0].getTime()-a[1].getTime()},c:${c}`);
      this.form.leaveDay = currHour;
      this.form.startTime = a[0].getTime();
      this.form.endTime = a[1].getTime();
      console.log(`form===>${JSON.stringify(this.form)}`)
    },
    /** 表单重置 */
    reset() {
      this.value1 = undefined;
      this.form = {
        id: undefined,
        leaveName: undefined,
        leaveContent: undefined,
        leaveDay: undefined,
        startTime: undefined,
        endTime: undefined,
        status: 0,
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        approveUserId: undefined,
      };
      this.form.userId = this.userInfo.userId;
      this.form.deptId = this.userInfo.deptId;
      this.form.tenantId = this.userInfo.tenantId;
      this.form.userName = this.userInfo.nickname;
      this.form.approveUserId = this.userInfo.deptDO.leaderUserId;
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
      this.form.tenantId = this.userInfo.tenantId;
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
    /** 新增按钮操作 */
    handleAdd() {
      this.editType = 1;
      this.reset();
      this.open = true;
      this.title = "添加请假";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.editType = 2;
      this.reset();
      const id = row.id;
      getZkAskforLeave(id).then(response => {
        this.form = response.data;
        this.value1 = [];
        if(this.form.startTime) {
          let startStr = this.parseTime(this.form.startTime);
          this.value1.push(startStr);
        }
        if(this.form.endTime) {
          let endStr = this.parseTime(this.form.endTime);
          this.value1.push(endStr);
        }
        console.log(`=================>${JSON.stringify(this.form)}`);
        this.open = true;
        this.title = "修改请假";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 修改按钮操作 */
    handleDetail(row) {
      this.$router.push({ name: "BpmProcessInstanceDetail", query: { id: row.processInstanceId}});
      return;
      const processInstanceId = row.processInstanceId;
      getTaskListByProcessInstanceId(processInstanceId).then(response => {
        if(response.data && response.data.length > 0){
          // this.$router.push({ path: "/workflow/process-instance/detail", query: {task: encodeURIComponent(JSON.stringify(response.data[0]))}});
          this.$router.push({ path: "/workflow/process-instance/detail?taskId=" + response.data[0].taskId, query: {}});
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        this.form.taskParam = this.taskParam;
        let value1 = [new Date(this.form.startTime), new Date(this.form.endTime)];
        this.value1Change(value1);
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        this.operationLoading = true;
        const loading = this.$loading({
          lock: true,
          text: 'Loading',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        // console.log(`==============>form:${JSON.stringify(this.form)}`);
        // return;
        // 修改的提交
        if (this.form.id != null) {
          updateZkAskforLeave(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
            this.operationLoading = false;
            loading.close();
          });
          return;
        }
        // 添加的提交
        createZkAskforLeave(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          this.operationLoading = false;
          loading.close();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除请假编号为"' + id + '"的数据项?').then(function() {
          return deleteZkAskforLeave(id);
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
      this.$modal.confirm('是否确认导出所有请假数据项?').then(() => {
          this.exportLoading = true;
          return exportZkAskforLeaveExcel(params);
        }).then(response => {
          this.$download.excel(response, '请假.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
