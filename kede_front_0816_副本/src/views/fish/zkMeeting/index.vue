<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="会议名称" prop="meetingName">
        <el-input v-model="queryParams.meetingName" placeholder="请输入会议名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="会议编号" prop="meetingNum">
        <el-input v-model="queryParams.meetingNum" placeholder="请输入会议编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker v-model="queryParams.startTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker v-model="queryParams.endTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_MEETING_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="主持人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入主持人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="主持人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入主持人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="主持人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入主持人所属部门" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-meeting:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-meeting:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="序号" align="center" prop="id" />
      <el-table-column label="会议名称" align="center" prop="meetingName" />
      <el-table-column label="会议备注" align="center" prop="meetingContent" show-overflow-tooltip/>
      <el-table-column label="会议编号" align="center" prop="meetingNum" show-overflow-tooltip/>
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
      <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_MEETING_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="状态(0:已预定;1:待开始;2:会议中;3:已结束;-1:已撤销;)" align="center" prop="status" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="主持人id" align="center" prop="userId" /> -->
      <el-table-column label="主持人" align="center" prop="userName" />
      <el-table-column label="参会人" align="center" prop="userNameStr" show-overflow-tooltip />
      <!-- <el-table-column label="主持人所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-meeting:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-meeting:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="会议名称" prop="meetingName">
          <el-input v-model="form.meetingName" placeholder="请输入会议名称" />
        </el-form-item>
        <el-form-item label="会议备注">
          <editor v-model="form.meetingContent" :min-height="192"/>
        </el-form-item>
        <!-- <el-form-item label="会议编号" prop="meetingNum">
          <el-input v-model="form.meetingNum" placeholder="请输入会议编号" />
        </el-form-item> -->
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="timestamp" placeholder="选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable v-model="form.endTime" type="datetime" value-format="timestamp" placeholder="选择结束时间" />
        </el-form-item>
        <el-form-item label="主持人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择主持人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="参会人" prop="userId">
          <el-select style="width:100%;" v-model="form.userIds" placeholder="请选择参会人" filterable multiple clearable  @change="updateUserIds">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="状态(0:已预定;1:待开始;2:会议中;3:已结束;-1:已撤销;)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="主持人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入主持人id" />
        </el-form-item>
        <el-form-item label="主持人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入主持人" />
        </el-form-item>
        <el-form-item label="主持人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入主持人所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_meeting"></FileBusUpload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_MEETING_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkMeeting, updateZkMeeting, deleteZkMeeting, getZkMeeting, getZkMeetingPage, exportZkMeetingExcel } from "@/api/fish/zkMeeting";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";

export default {
  name: "ZkMeeting",
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
      // 会议列表
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
        meetingName: null,
        meetingContent: null,
        meetingNum: null,
        startTime: [],
        endTime: [],
        status: null,
        createTime: [],
        userId: null,
        userName: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        startTime: [{ required: true, message: "开始时间不能为空", trigger: "blur" }],
        endTime: [{ required: true, message: "结束时间不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
    this.getUserList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkMeetingPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateUserId(userId) {
      this.pushUserIds(userId);

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
      // console.log("===========>"+JSON.stringify(this.form));
    }, 
    pushUserIds(userId) {
      if(!this.form.userIds) {
        this.form.userIds = userId;
      } else if(!this.form.userIds.includes(userId)) {
        let arr = this.form.userIds.push(userId);
      }
    },
    updateUserIds(userIds) {
      // let item = null;
      // this.userList1.forEach(obj => {
      //   if(obj.id == userId) {
      //     item = obj;
      //     return;
      //   }
      // })
      // // 2. 执行 deptId、userId赋值
      // this.form.deptId = item.deptId;
      // this.form.userName = item.nickname;
      console.log(`==>a:${userIds}`);
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
        meetingName: undefined,
        meetingContent: undefined,
        meetingNum: undefined,
        startTime: undefined,
        endTime: undefined,
        status: undefined,
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
      this.title = "添加会议";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkMeeting(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改会议";
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
        let params = {...this.form};
        
        // 修改的提交
        if (params.id != null) {
          updateZkMeeting(params).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkMeeting(params).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除会议编号为"' + id + '"的数据项?').then(function() {
          return deleteZkMeeting(id);
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
      this.$modal.confirm('是否确认导出所有会议数据项?').then(() => {
          this.exportLoading = true;
          return exportZkMeetingExcel(params);
        }).then(response => {
          this.$download.excel(response, '会议.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
