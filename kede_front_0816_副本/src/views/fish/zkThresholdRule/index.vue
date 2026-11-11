<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="设备类型ID" prop="deviceTypeId">
        <el-input v-model="queryParams.deviceTypeId" placeholder="请输入设备类型ID，关联iot_device_type表" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="监测指标ID" prop="metricId">
        <el-input v-model="queryParams.metricId" placeholder="请输入监测指标ID，关联iot_monitor_metric表" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="比较运算符（如：>、<、>=、<=、==、!=、between）" prop="comparisonOperator">
        <el-input v-model="queryParams.comparisonOperator" placeholder="请输入比较运算符（如：>、<、>=、<=、==、!=、between）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="阈值1（用于单值比较）" prop="thresholdValue1">
        <el-input v-model="queryParams.thresholdValue1" placeholder="请输入阈值1（用于单值比较）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="阈值2（用于范围比较，如between）" prop="thresholdValue2">
        <el-input v-model="queryParams.thresholdValue2" placeholder="请输入阈值2（用于范围比较，如between）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="是否启用" prop="isEnabled">
        <el-input v-model="queryParams.isEnabled" placeholder="请输入是否启用（1：启用，0：禁用）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="触发级别（1：提示，2：警告，3：严重，4：紧急）" prop="triggerLevel">
        <el-input v-model="queryParams.triggerLevel" placeholder="请输入触发级别（1：提示，2：警告，3：严重，4：紧急）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="触发动作（如：发送通知、启动设备、关闭设备）" prop="triggerAction">
        <el-input v-model="queryParams.triggerAction" placeholder="请输入触发动作（如：发送通知、启动设备、关闭设备）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <!-- <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
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
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-threshold-rule:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-threshold-rule:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="规则ID" align="center" prop="id" />
      <el-table-column label="规则名称" align="center" prop="ruleName" />
      <el-table-column label="设备类型ID" align="center" prop="deviceTypeId" />
      <el-table-column label="监测指标ID" align="center" prop="metricId" />
      <el-table-column label="比较运算符" align="center" prop="comparisonOperator" />
      <el-table-column label="阈值1" align="center" prop="thresholdValue1" />
      <el-table-column label="阈值2" align="center" prop="thresholdValue2" />
      <el-table-column label="是否启用" align="center" prop="isEnabled" />
      <el-table-column label="触发级别" align="center" prop="triggerLevel" />
      <el-table-column label="触发动作" align="center" prop="triggerAction" />
      <el-table-column label="规则描述" align="center" prop="description" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-threshold-rule:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-threshold-rule:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="设备类型ID，关联iot_device_type表" prop="deviceTypeId">
          <el-input v-model="form.deviceTypeId" placeholder="请输入设备类型ID，关联iot_device_type表" />
        </el-form-item>
        <el-form-item label="监测指标ID，关联iot_monitor_metric表" prop="metricId">
          <el-input v-model="form.metricId" placeholder="请输入监测指标ID，关联iot_monitor_metric表" />
        </el-form-item>
        <el-form-item label="比较运算符（如：>、<、>=、<=、==、!=、between）" prop="comparisonOperator">
          <el-input v-model="form.comparisonOperator" placeholder="请输入比较运算符（如：>、<、>=、<=、==、!=、between）" />
        </el-form-item>
        <el-form-item label="阈值1（用于单值比较）" prop="thresholdValue1">
          <el-input v-model="form.thresholdValue1" placeholder="请输入阈值1（用于单值比较）" />
        </el-form-item>
        <el-form-item label="阈值2（用于范围比较，如between）" prop="thresholdValue2">
          <el-input v-model="form.thresholdValue2" placeholder="请输入阈值2（用于范围比较，如between）" />
        </el-form-item>
        <el-form-item label="是否启用（1：启用，0：禁用）" prop="isEnabled">
          <el-input v-model="form.isEnabled" placeholder="请输入是否启用（1：启用，0：禁用）" />
        </el-form-item>
        <el-form-item label="触发级别（1：提示，2：警告，3：严重，4：紧急）" prop="triggerLevel">
          <el-input v-model="form.triggerLevel" placeholder="请输入触发级别（1：提示，2：警告，3：严重，4：紧急）" />
        </el-form-item>
        <el-form-item label="触发动作（如：发送通知、启动设备、关闭设备）" prop="triggerAction">
          <el-input v-model="form.triggerAction" placeholder="请输入触发动作（如：发送通知、启动设备、关闭设备）" />
        </el-form-item>
        <el-form-item label="规则描述">
          <editor v-model="form.description" :min-height="192"/>
        </el-form-item>
        <el-form-item label="负责人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入负责人id" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
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
import { createZkThresholdRule, updateZkThresholdRule, deleteZkThresholdRule, getZkThresholdRule, getZkThresholdRulePage, exportZkThresholdRuleExcel } from "@/api/fish/zkThresholdRule";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkThresholdRule",
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
      // 阈值规则列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        ruleName: null,
        deviceTypeId: null,
        metricId: null,
        comparisonOperator: null,
        thresholdValue1: null,
        thresholdValue2: null,
        isEnabled: null,
        triggerLevel: null,
        triggerAction: null,
        description: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ruleName: [{ required: true, message: "规则名称不能为空", trigger: "blur" }],
        deviceTypeId: [{ required: true, message: "设备类型ID，关联iot_device_type表不能为空", trigger: "blur" }],
        metricId: [{ required: true, message: "监测指标ID，关联iot_monitor_metric表不能为空", trigger: "blur" }],
        comparisonOperator: [{ required: true, message: "比较运算符（如：>、<、>=、<=、==、!=、between）不能为空", trigger: "blur" }],
        isEnabled: [{ required: true, message: "是否启用（1：启用，0：禁用）不能为空", trigger: "blur" }],
        triggerLevel: [{ required: true, message: "触发级别（1：提示，2：警告，3：严重，4：紧急）不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkThresholdRulePage(this.queryParams).then(response => {
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
        ruleName: undefined,
        deviceTypeId: undefined,
        metricId: undefined,
        comparisonOperator: undefined,
        thresholdValue1: undefined,
        thresholdValue2: undefined,
        isEnabled: undefined,
        triggerLevel: undefined,
        triggerAction: undefined,
        description: undefined,
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
      this.title = "添加阈值规则";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkThresholdRule(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改阈值规则";
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
          updateZkThresholdRule(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkThresholdRule(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除阈值规则编号为"' + id + '"的数据项?').then(function() {
          return deleteZkThresholdRule(id);
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
      this.$modal.confirm('是否确认导出所有阈值规则数据项?').then(() => {
          this.exportLoading = true;
          return exportZkThresholdRuleExcel(params);
        }).then(response => {
          this.$download.excel(response, '阈值规则.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
