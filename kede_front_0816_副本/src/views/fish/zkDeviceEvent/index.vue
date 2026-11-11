<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="设备id" prop="deviceId">
        <el-input v-model="queryParams.deviceId" placeholder="请输入设备唯一标识" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="设备类型" prop="deviceTypeId">
        <el-input v-model="queryParams.deviceTypeId" placeholder="请输入设备类型ID，关联iot_device_type表" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="记录时间" prop="recordTime">
        <el-date-picker v-model="queryParams.recordTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <!-- <el-form-item label="监测指标ID，关联iot_monitor_metric表" prop="metricId">
        <el-input v-model="queryParams.metricId" placeholder="请输入监测指标ID，关联iot_monitor_metric表" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="指标值" prop="metricValue">
        <el-input v-model="queryParams.metricValue" placeholder="请输入指标值" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="数据状态（1：正常，2：异常，3：已处理）" prop="dataStatus">
        <el-select v-model="queryParams.dataStatus" placeholder="请选择数据状态（1：正常，2：异常，3：已处理）" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="原始数据（保留原始上传数据）" prop="rawData">
        <el-input v-model="queryParams.rawData" placeholder="请输入原始数据（保留原始上传数据）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="设备事件JSON数据" prop="deviceEvent">
        <el-input v-model="queryParams.deviceEvent" placeholder="请输入设备事件JSON数据" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item> -->
      <!-- <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-device-event:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-device-event:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="记录ID" align="center" prop="id" />
      <el-table-column label="设备唯一标识" align="center" prop="deviceId" />
      <!-- <el-table-column label="设备类型ID，关联iot_device_type表" align="center" prop="deviceTypeId" /> -->
      <el-table-column label="记录时间" align="center" prop="recordTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.recordTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="监测指标ID，关联iot_monitor_metric表" align="center" prop="metricId" />
      <el-table-column label="指标值" align="center" prop="metricValue" />
      <el-table-column label="数据状态（1：正常，2：异常，3：已处理）" align="center" prop="dataStatus" /> -->
      <el-table-column label="原始数据（保留原始上传数据）" align="center" prop="rawData" />
      <!-- <el-table-column label="设备事件" align="center" min-width="400">
          <template v-slot="scope">
            <div v-if="isJson(scope.row.deviceEvent)">
              <el-table :data="formatDeviceEvent(scope.row.deviceEvent)" border size="mini" style="width: 100%">
                <el-table-column label="事件类型" align="center" prop="type" width="120" />
                <el-table-column label="故障描述" align="center" prop="description" width="200" />
                <el-table-column label="消息ID" align="center" prop="messageId" width="180" />
                <el-table-column label="时间戳" align="center" prop="timestamp" width="180">
                  <template v-slot="innerScope">
                    <span>{{ parseTime(innerScope.row.timestamp) }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <span v-else>{{ scope.row.deviceEvent }}</span>
          </template>
        </el-table-column> -->
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
                     v-hasPermi="['pl:zk-device-event:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-device-event:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备唯一标识" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备唯一标识" />
        </el-form-item>
        <el-form-item label="设备类型ID，关联iot_device_type表" prop="deviceTypeId">
          <el-input v-model="form.deviceTypeId" placeholder="请输入设备类型ID，关联iot_device_type表" />
        </el-form-item>
        <el-form-item label="记录时间（精确到分钟，按5分钟间隔存储）" prop="recordTime">
          <el-date-picker clearable v-model="form.recordTime" type="date" value-format="timestamp" placeholder="选择记录时间（精确到分钟，按5分钟间隔存储）" />
        </el-form-item>
        <el-form-item label="监测指标ID，关联iot_monitor_metric表" prop="metricId">
          <el-input v-model="form.metricId" placeholder="请输入监测指标ID，关联iot_monitor_metric表" />
        </el-form-item>
        <el-form-item label="指标值" prop="metricValue">
          <el-input v-model="form.metricValue" placeholder="请输入指标值" />
        </el-form-item>
        <el-form-item label="数据状态（1：正常，2：异常，3：已处理）" prop="dataStatus">
          <el-radio-group v-model="form.dataStatus">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="原始数据（保留原始上传数据）" prop="rawData">
          <el-input v-model="form.rawData" placeholder="请输入原始数据（保留原始上传数据）" />
        </el-form-item>
        <el-form-item label="设备事件JSON数据" prop="deviceEvent">
          <el-input v-model="form.deviceEvent" placeholder="请输入设备事件JSON数据" />
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
import { createZkDeviceEvent, updateZkDeviceEvent, deleteZkDeviceEvent, getZkDeviceEvent, getZkDeviceEventPage, exportZkDeviceEventExcel } from "@/api/fish/zkDeviceEvent";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkDeviceEvent",
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
      // 设备事件记录列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        deviceId: null,
        deviceTypeId: null,
        recordTime: [],
        metricId: null,
        metricValue: null,
        dataStatus: null,
        rawData: null,
        deviceEvent: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deviceId: [{ required: true, message: "设备唯一标识不能为空", trigger: "blur" }],
        recordTime: [{ required: true, message: "记录时间（精确到分钟，按5分钟间隔存储）不能为空", trigger: "blur" }],
        deviceEvent: [{ required: true, message: "设备事件JSON数据不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
      /** 检查字符串是否为JSON格式 */
      isJson(str) {
        try {
          JSON.parse(str);
          return true;
        } catch (e) {
          return false;
        }
      },
      
      /** 格式化设备事件JSON数据 */
      formatDeviceEvent(deviceEventStr) {
        try {
          const deviceEvent = JSON.parse(deviceEventStr);
          const events = [];
          
          // 定义故障类型映射
          const faultMap = {
            0: '溶氧传感器通讯故障',
            1: '溶氧故障',
            2: '温度故障'
          };
          
          // 处理data中的测试字段
          if (deviceEvent.data) {
            Object.entries(deviceEvent.data).forEach(([key, value]) => {
              if (key.startsWith('test')) {
                events.push({
                  type: key,
                  description: faultMap[value] || `未知故障类型(${value})`,
                  messageId: deviceEvent.messageId,
                  timestamp: deviceEvent.timestamp
                });
              }
            });
          }
          
          // 如果没有找到test字段，返回原始数据结构
          if (events.length === 0) {
            return [{
              type: '未知类型',
              description: '无详细故障信息',
              messageId: deviceEvent.messageId || '无',
              timestamp: deviceEvent.timestamp || Date.now()
            }];
          }
          
          return events;
        } catch (e) {
          console.error('格式化设备事件数据失败:', e);
          return [{
            type: '解析错误',
            description: '数据格式无效',
            messageId: '无',
            timestamp: Date.now()
          }];
        }
      },
    /** 查询列表 */
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkDeviceEventPage(this.queryParams).then(response => {
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
        deviceId: undefined,
        deviceTypeId: undefined,
        recordTime: undefined,
        metricId: undefined,
        metricValue: undefined,
        dataStatus: undefined,
        rawData: undefined,
        deviceEvent: undefined,
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
      this.title = "添加设备事件记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkDeviceEvent(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备事件记录";
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
          updateZkDeviceEvent(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkDeviceEvent(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除设备事件记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkDeviceEvent(id);
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
      this.$modal.confirm('是否确认导出所有设备事件记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkDeviceEventExcel(params);
        }).then(response => {
          this.$download.excel(response, '设备事件记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
/* 嵌套表格样式 */
.el-table .el-table {
  font-size: 12px;
  margin-top: 5px;
}

/* 表格单元格样式 */
.el-table .el-table td,
.el-table .el-table th {
  padding: 6px 0;
}

/* 表格标题样式 */
.el-table .el-table th {
  background-color: #f5f7fa;
  font-weight: bold;
}

/* 故障描述样式 */
.fault-description {
  color: #f56c6c;
}

/* 正常状态样式 */
.nostatusrmal- {
  color: #67c23a;
}
</style>
