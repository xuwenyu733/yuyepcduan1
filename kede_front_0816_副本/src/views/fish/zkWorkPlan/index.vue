<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">

      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <!-- <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="所属部门" prop="deptId">
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
      <!--  v-hasPermi="['pl:zk-work-plan:create']" -->
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-work-plan:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="日计划" align="center" prop="daylyPlan" />
      <el-table-column label="周计划" align="center" prop="weeklyPlan" />
      <el-table-column label="月度目标" align="center" prop="monthlyGoal" />
      <el-table-column label="季度目标" align="center" prop="quarterlyGoal" />
      <el-table-column label="年度目标" align="center" prop="annualGoal" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="负责人" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.userName">{{ scope.row.userName }}</span>
          <span v-else="scope.row.userId">
            <userName :user-id="scope.row.userId"></userName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="所属部门" align="center" prop="deptId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
          <span v-else="scope.row.deptId">
            <deptName :deptId="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
        <!--  v-hasPermi="['pl:zk-work-plan:update']" -->
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                    >修改</el-button>
                    <!--  v-hasPermi="['pl:zk-work-plan:delete']" -->
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                    >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="日计划" prop="daylyPlan">
          <el-input v-model="form.daylyPlan" placeholder="请输入日计划" />
        </el-form-item>
        <el-form-item label="周计划" prop="weeklyPlan">
          <el-input v-model="form.weeklyPlan" placeholder="请输入周计划" />
        </el-form-item>
        <el-form-item label="月度目标" prop="monthlyGoal">
          <el-input v-model="form.monthlyGoal" placeholder="请输入月度目标" />
        </el-form-item>
        <el-form-item label="季度目标" prop="quarterlyGoal">
          <el-input v-model="form.quarterlyGoal" placeholder="请输入季度目标" />
        </el-form-item>
        <el-form-item label="年度目标" prop="annualGoal">
          <el-input v-model="form.annualGoal" placeholder="请输入年度目标" />
        </el-form-item>
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择负责人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="负责人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入负责人id" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
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
import { createZkWorkPlan, updateZkWorkPlan, deleteZkWorkPlan, getZkWorkPlan, getZkWorkPlanPage, exportZkWorkPlanExcel } from "@/api/fish/zkWorkPlan";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { UserInfoKey } from "@/utils/constants";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";

export default {
  name: "ZkWorkPlan",
  components: {
      FileBusUpload,
  },
  data() {
    return {
      userInfo: {},
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 工作目标与计划列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      userList1: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        daylyPlan: null,
        weeklyPlan: null,
        monthlyGoal: null,
        quarterlyGoal: null,
        annualGoal: null,
        createTime: [],
        userId: null,
        deptId: null,
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
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getList();
    this.getUserList();
  },
  methods: {
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.userId = item.id;
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>"+JSON.stringify(item));
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          // let tempStr = `${obj.deptName}-${obj.nickname}`;
          let tempStr = `${obj.nickname}`;
          obj.nickname = tempStr;
        })
        this.updateUserId(this.userInfo.userId);
        // console.log("getUserList===========>"+JSON.stringify(this.userList1));
      })
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkWorkPlanPage(this.queryParams).then(response => {
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
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        daylyPlan: undefined,
        weeklyPlan: undefined,
        monthlyGoal: undefined,
        quarterlyGoal: undefined,
        annualGoal: undefined,
        userId: undefined,
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
      this.title = "添加工作目标与计划";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkWorkPlan(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改工作目标与计划";
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
          updateZkWorkPlan(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkWorkPlan(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除工作目标与计划编号为"' + id + '"的数据项?').then(function() {
          return deleteZkWorkPlan(id);
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
      this.$modal.confirm('是否确认导出所有工作目标与计划数据项?').then(() => {
          this.exportLoading = true;
          return exportZkWorkPlanExcel(params);
        }).then(response => {
          this.$download.excel(response, '工作目标与计划.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
