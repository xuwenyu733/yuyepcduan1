<template>
  <div class="app-container">
    <div class="list-filter-panel">
      <!-- 搜索工作栏 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
        <el-form-item label="工作内容" prop="taskContent">
          <el-input v-model="queryParams.taskContent" placeholder="请输入工作内容" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="参加人" prop="userId">
          <el-select style="width:100%;" v-model="queryParams.userId" placeholder="请选择执行人" filterable clearable>
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="任务状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请输入任务状态" filterable clearable size="small">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_TASK_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-btns">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
        <right-toolbar class="list-filter-tools" :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-form>

      <!-- 操作工具栏 -->
      <div class="list-action-bar mb8">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-task:create']">新增</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-task:export']">导出</el-button>
      </div>
    </div>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="任务状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_TASK_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <div class="small" v-if="scope.row.taskType==4">
            <dict-tag :type="DICT_TYPE.ZK_SCHEDULING_TYPE" :value="scope.row.taskType" />
          </div>
          <div class="middle" v-if="scope.row.taskType==3">
            <dict-tag :type="DICT_TYPE.ZK_SCHEDULING_TYPE" :value="scope.row.taskType" />
          </div>
          <div class="big" v-if="scope.row.taskType==1">
            <dict-tag :type="DICT_TYPE.ZK_SCHEDULING_TYPE" :value="scope.row.taskType" />
          </div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="业务id" align="center" prop="busId" /> -->
      <el-table-column label="任务名称" align="center" prop="taskName" />
      <el-table-column label="工作内容" align="center" prop="taskContent" />
      <el-table-column label="起始时间" align="center" prop="startTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="截至时间" align="center" prop="endTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="任务反馈" align="center" prop="taskPoint" />
      <el-table-column label="任务结果" align="center" prop="taskSummary" /> -->
      <!-- <el-table-column label="记录人id" align="center" prop="userId" /> -->
      <el-table-column label="牵头人" align="center" prop="sellId" show-overflow-tooltip>
        <template v-slot="scope">
          <userName :user-id="scope.row.creator"></userName>
        </template>
      </el-table-column>
      <el-table-column label="参加人" align="center" prop="userName" />
      <!-- <el-table-column label="详情" align="center" prop="fileUrl" /> -->
      <el-table-column label="详情" align="center" prop="picUrl" width="100">
        <template v-slot="scope">
          <image-preview v-if="scope.row.fileUrl != null && scope.row.fileUrl != ''" :src="scope.row.fileUrl"
            width="60px" height="60px"></image-preview>
          <i v-else>
            暂无内容
          </i>
        </template>
      </el-table-column>
      <!-- <el-table-column label="记录人所属部门" align="center" prop="deptId" />
      <el-table-column label="状态(0:已下发;1:已通知;2:待完成;3:已完成;4:已评估;-1:已删除;)" align="center" prop="status" /> -->
      <!-- <el-table-column label="文件id" align="center" prop="fid" />
      <el-table-column label="流程实例" align="center" prop="processInstanceId" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-finished" @click="handleReceive(scope.row, 2)" v-if="scope.row.status == 1"
                     v-hasPermi="['pl:zk-task:update']">接收</el-button>
          <el-button size="mini" type="text" icon="el-icon-finished" @click="handleReceive(scope.row, 3)" v-if="scope.row.status == 2"
                     v-hasPermi="['pl:zk-task:update']">完成</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-task:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-task:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="业务id" prop="busId">
          <el-input v-model="form.busId" placeholder="请输入业务id" />
        </el-form-item> -->
        <el-form-item label="排班类别" prop="taskType">
          <el-select v-model="form.taskType" placeholder="请输入排班类别" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_SCHEDULING_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围" prop="tempTimeArr">
          <!-- <el-time-picker v-model="form.tempTimeArr" is-range format="yyyy-MM-dd HH:mm:ss" value-format="yyyy-MM-dd HH:mm:ss"
            :style="{width: '100%'}" start-placeholder="开始时间" end-placeholder="结束时间" range-separator="至"
            clearable></el-time-picker> -->
          <el-date-picker
            v-model="form.tempTimeArr"
            type="datetimerange"
            format="yyyy-MM-dd HH:mm:ss" value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="工作内容">
          <editor v-model="form.taskContent" :min-height="192" placeholder="请输入工作内容" />
        </el-form-item>
        <!-- <el-form-item label="任务反馈" prop="taskPoint">
          <el-input v-model="form.taskPoint" placeholder="请输入任务反馈" />
        </el-form-item>
        <el-form-item label="任务结果" prop="taskSummary">
          <el-input v-model="form.taskSummary" placeholder="请输入任务结果" />
        </el-form-item> -->
        <el-form-item label="执行人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择执行人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详情" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_task" @uploadSuccess="confirmUrl"></FileBusUpload>
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="editType==2">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_TASK_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
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
        <el-form-item label="状态(0:已下发;1:已通知;2:待完成;3:已完成;4:已评估;-1:已删除;)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item>
        <el-form-item label="流程实例" prop="processInstanceId">
          <el-input v-model="form.processInstanceId" placeholder="请输入流程实例" />
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
import { createZkTask, updateZkTask, deleteZkTask, getZkTask, getZkTaskPage, exportZkTaskExcel } from "@/api/fish/zkTask";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import ImagePreview from "@/components/ImagePreview";

export default {
  name: "ZkTask",
  components: {
      FileBusUpload,
    Editor,
    ImagePreview,
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
      // 任务列表
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
        busId: null,
        taskName: null,
        taskContent: null,
        taskPoint: null,
        taskSummary: null,
        userId: null,
        userName: null,
        deptId: null,
        status: null,
        createTime: [],
        tempTimeArr: [],
        fid: null,
        processInstanceId: null,
      },
      // 表单参数
      form: {},
      editType: 1, // 1:添加;2:修正;
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
    confirmUrl(url='') {
      this.form.fileUrl = url;
      // console.log("confirmUrl=====>"+JSON.stringify(this.form));
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkTaskPage(this.queryParams).then(response => {
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
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        busId: undefined,
        taskName: undefined,
        taskContent: undefined,
        taskPoint: undefined,
        taskSummary: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        status: undefined,
        fid: undefined,
        processInstanceId: undefined,
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
      this.editType = 1;
      this.reset();
      this.open = true;
      this.title = "添加任务";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    handleReceive(row, taskStatus) {
      let msg = taskStatus==2?'接受':'完成';
      this.$modal.confirm(`是否确认${msg}该任务?`).then(() => {
        this.sureReceive(row, taskStatus);
      }).catch(() => {});
    },
    sureReceive(row, taskStatus) {
      updateZkTask({id: row.id, status: taskStatus}).then(response => {
        this.$modal.msgSuccess("操作成功");
        this.getList();
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.editType = 2;
      this.reset();
      const id = row.id;
      getZkTask(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改任务";
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
          updateZkTask(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkTask(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除任务编号为"' + id + '"的数据项?').then(function() {
          return deleteZkTask(id);
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
      this.$modal.confirm('是否确认导出所有任务数据项?').then(() => {
          this.exportLoading = true;
          return exportZkTaskExcel(params);
        }).then(response => {
          this.$download.excel(response, '任务.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
<style lang="scss" scoped>
.small {
  border: 1px solid #4C9165;
  border-radius: 4px;
}
.middle {
  border: 1px solid #FBC21D;
  color: #FBC21D;
  border-radius: 4px;
  font-weight: bold;
}
.big {
  border: 1px solid #E63F31;
  color: #E63F31;
  border-radius: 4px;
  font-weight: bold;
}
</style>