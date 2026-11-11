<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="存塘编号" prop="pondDetailNum">
        <el-input v-model="queryParams.pondDetailNum" placeholder="请输入存塘编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="日志标题" prop="logTitle">
        <el-input v-model="queryParams.logTitle" placeholder="请输入日志标题" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="日志类型(0:fish;1:其他;)" prop="logType">
        <el-select v-model="queryParams.logType" placeholder="请选择日志类型(0:fish;1:其他;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="原始数据" prop="originData">
        <el-input v-model="queryParams.originData" placeholder="请输入原始数据" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="目标数据" prop="targetData">
        <el-input v-model="queryParams.targetData" placeholder="请输入目标数据" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="原始结果数据" prop="originRes">
        <el-input v-model="queryParams.originRes" placeholder="请输入原始结果数据" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="目标结果数据" prop="targetRes">
        <el-input v-model="queryParams.targetRes" placeholder="请输入目标结果数据" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="记录人" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入记录人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
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
      <el-col :span="1.5" v-if="false">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['zk:fish-system-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['zk:fish-system-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="存塘编号" align="center" prop="pondDetailNum" />
      <el-table-column label="日志标题" align="center" prop="logTitle" show-overflow-tooltip />
      <!-- <el-table-column label="日志类型" align="center" prop="logType" /> -->
      <el-table-column label="备注" align="center" prop="remark" />
      <!-- <el-table-column label="原始数据" align="center" prop="originData" show-overflow-tooltip /> -->
      <el-table-column label="原始数据" align="left" prop="originData" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.originData"
            v-clipboard:success="clipboardSuccess">
            {{ scope.row.originData }}
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="目标数据" align="center" prop="targetData" show-overflow-tooltip /> -->
      <el-table-column label="目标数据" align="left" prop="targetData" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.targetData"
            v-clipboard:success="clipboardSuccess">
            {{ scope.row.targetData }}
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="原始结果数据" align="center" prop="originRes" show-overflow-tooltip /> -->
      <el-table-column label="原始结果数据" align="left" prop="originRes" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.originRes"
            v-clipboard:success="clipboardSuccess">
            {{ scope.row.originRes }}
          </span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="目标结果数据" align="center" prop="targetRes" show-overflow-tooltip /> -->
      <el-table-column label="目标结果数据" align="left" prop="targetRes" min-width="100" show-overflow-tooltip >
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.targetRes"
            v-clipboard:success="clipboardSuccess">
            {{ scope.row.targetRes }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人id" align="center" prop="creator" />
      <el-table-column label="操作人" align="center" prop="creator" show-overflow-tooltip>
        <template v-slot="scope">
          <userName :user-id="scope.row.creator"></userName>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <!-- <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['zk:fish-system-log:update']">查看</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['zk:fish-system-log:delete']">删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize" page-sizes="100"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="存塘编号" prop="pondDetailNum">
          <el-input v-model="form.pondDetailNum" placeholder="请输入存塘编号" />
        </el-form-item>
        <el-form-item label="日志标题" prop="logTitle">
          <el-input v-model="form.logTitle" placeholder="请输入日志标题" />
        </el-form-item>
        <!-- <el-form-item label="日志类型(0:fish;1:其他;)" prop="logType">
          <el-select v-model="form.logType" placeholder="请选择日志类型(0:fish;1:其他;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="原始数据" prop="originData">
          <el-input v-model="form.originData" placeholder="请输入原始数据" />
        </el-form-item>
        <el-form-item label="目标数据" prop="targetData">
          <el-input v-model="form.targetData" placeholder="请输入目标数据" />
        </el-form-item>
        <el-form-item label="原始结果数据" prop="originRes">
          <el-input v-model="form.originRes" placeholder="请输入原始结果数据" />
        </el-form-item>
        <el-form-item label="目标结果数据" prop="targetRes">
          <el-input v-model="form.targetRes" placeholder="请输入目标结果数据" />
        </el-form-item>
        <!-- <el-form-item label="记录人" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入记录人" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button type="primary" @click="submitForm">确 定</el-button> -->
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createFishSystemLog, updateFishSystemLog, deleteFishSystemLog, getFishSystemLog, getFishSystemLogPage, exportFishSystemLogExcel } from "@/api/fish/fishSystemLog";
import FileBusUpload from "@/components/FileBusUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "FishSystemLog",
  components: {
      FileBusUpload,
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
      // fish系统日志列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 500,
        pondDetailNum: null,
        logTitle: null,
        logType: null,
        remark: null,
        originData: null,
        targetData: null,
        originRes: null,
        targetRes: null,
        createTime: [],
        userId: null,
        deptId: null,
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
  },
  methods: {
    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功");
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getFishSystemLogPage(this.queryParams).then(response => {
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
        pondDetailNum: undefined,
        logTitle: undefined,
        logType: undefined,
        remark: undefined,
        originData: undefined,
        targetData: undefined,
        originRes: undefined,
        targetRes: undefined,
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
      this.title = "添加fish系统日志";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getFishSystemLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改fish系统日志";
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
          updateFishSystemLog(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createFishSystemLog(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除fish系统日志编号为"' + id + '"的数据项?').then(function() {
          return deleteFishSystemLog(id);
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
      this.$modal.confirm('是否确认导出所有fish系统日志数据项?').then(() => {
          this.exportLoading = true;
          return exportFishSystemLogExcel(params);
        }).then(response => {
          this.$download.excel(response, 'fish系统日志.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
