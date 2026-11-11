<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="会议id" prop="meetingId">
        <el-input v-model="queryParams.meetingId" placeholder="请输入会议id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="记录名称" prop="meetingRecordName">
        <el-input v-model="queryParams.meetingRecordName" placeholder="请输入记录名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="记录概要" prop="meetingPoint">
        <el-input v-model="queryParams.meetingPoint" placeholder="请输入记录概要" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="记录总结" prop="meetingSummary">
        <el-input v-model="queryParams.meetingSummary" placeholder="请输入记录总结" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="记录人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入记录人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="记录人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入记录人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="记录人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入记录人所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态(0:已记录;1:待入库;2:已入库;3:待评价;3:已评价;-1:已删除;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:已记录;1:待入库;2:已入库;3:待评价;3:已评价;-1:已删除;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
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
                   v-hasPermi="['pl:zk-meeting-record:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-meeting-record:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="会议id" align="center" prop="meetingId" /> -->
      <!-- <el-table-column label="会议名称" align="center" prop="meetingName" /> -->
      <el-table-column label="会议号" align="center" prop="meetingNum" />
      <!-- <el-table-column label="记录名称" align="center" prop="meetingRecordName" /> -->
      <!-- <el-table-column label="记录描述" align="center" prop="meetingRecordContent" /> -->
      <!-- <el-table-column label="记录概要" align="center" prop="meetingPoint" /> -->
      <el-table-column label="会议记录" align="left" prop="meetingRecordContent" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.meetingRecordContent"
            v-clipboard:success="clipboardSuccess">
            {{ scope.row.meetingRecordContent }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="记录概要" align="left" prop="meetingPoint" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.meetingPoint"
            v-clipboard:success="clipboardSuccess">
            {{ scope.row.meetingPoint }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="记录总结" align="center" prop="meetingSummary" />
      <el-table-column label="会议录屏" align="left" prop="localVideoUrls" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <div class="video_show" v-if="scope.row.localVideoUrls != ''">
            <template v-for="item in scope.row.localVideoUrls.split(',')">
              <div class="video_show_item">
                <video :width="'100px'" controls>
                  <source :src="item"/>
                </video>
              </div>
            </template>
          </div>
          <div class="video_show" v-else>
            暂无记录
          </div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="记录人id" align="center" prop="userId" />
      <el-table-column label="记录人" align="center" prop="userName" />
      <el-table-column label="记录人所属部门" align="center" prop="deptId" />
      <el-table-column label="状态(0:已记录;1:待入库;2:已入库;3:待评价;3:已评价;-1:已删除;)" align="center" prop="status" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-meeting-record:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-meeting-record:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="会议id" prop="meetingId">
          <el-input v-model="form.meetingId" placeholder="请输入会议id" />
        </el-form-item> -->
        <!-- <el-form-item label="会议名称" prop="meetingName">
          <el-input v-model="form.meetingRecordName" placeholder="请输入会议名称" />
        </el-form-item> -->
        <el-form-item label="记录名称" prop="meetingRecordName">
          <el-input v-model="form.meetingRecordName" placeholder="请输入记录名称" />
        </el-form-item>
        <el-form-item label="记录描述">
          <editor v-model="form.meetingRecordContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="记录概要" prop="meetingPoint">
          <el-input v-model="form.meetingPoint" placeholder="请输入记录概要" />
        </el-form-item>
        <el-form-item label="记录总结" prop="meetingSummary">
          <el-input v-model="form.meetingSummary" placeholder="请输入记录总结" />
        </el-form-item>
        <!-- <el-form-item label="记录人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入记录人id" />
        </el-form-item>
        <el-form-item label="记录人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入记录人" />
        </el-form-item>
        <el-form-item label="记录人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入记录人所属部门" />
        </el-form-item>
        <el-form-item label="状态(0:已记录;1:待入库;2:已入库;3:待评价;3:已评价;-1:已删除;)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_meeting_record"></FileBusUpload>
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
import { createZkMeetingRecord, updateZkMeetingRecord, deleteZkMeetingRecord, getZkMeetingRecord, getZkMeetingRecordPage, exportZkMeetingRecordExcel } from "@/api/fish/zkMeetingRecord";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkMeeting, updateZkMeeting, deleteZkMeeting, getZkMeeting, getZkMeetingPage, exportZkMeetingExcel } from "@/api/fish/zkMeeting";

export default {
  name: "ZkMeetingRecord",
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
      // 会议记录列表
      list: [],
      meetingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        meetingId: null,
        meetingRecordName: null,
        meetingRecordContent: null,
        meetingPoint: null,
        meetingSummary: null,
        userId: null,
        userName: null,
        deptId: null,
        status: null,
        createTime: [],
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
    this.getMeetingList();
  },
  methods: {
    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功");
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkMeetingRecordPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateMeetingId(itemId) {
      let item = null;
      this.meetingList.forEach(obj => {
        if(obj.id == itemId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.meetingName = item.meetingName;
      // console.log("===========>"+JSON.stringify(this.form));
    }, 
    getMeetingList() {
      this.meetingList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkMeetingPage(params).then(response => {
        this.meetingList.push(...response.data.list);
        // this.userList1.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   // console.log("===========>"+JSON.stringify(obj));
        // })
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
        meetingId: undefined,
        meetingRecordName: undefined,
        meetingRecordContent: undefined,
        meetingPoint: undefined,
        meetingSummary: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        status: undefined,
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
      this.title = "添加会议记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkMeetingRecord(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改会议记录";
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
          updateZkMeetingRecord(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkMeetingRecord(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除会议记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkMeetingRecord(id);
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
      this.$modal.confirm('是否确认导出所有会议记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkMeetingRecordExcel(params);
        }).then(response => {
          this.$download.excel(response, '会议记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
<style lang="scss" scoped>
.video_show {
  display: flex;
}
</style>