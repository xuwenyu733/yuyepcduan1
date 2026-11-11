<template>
  <div class="app-container">
    <div class="page-head mb8">
      <h2 class="page-head-title">水质数据管理</h2>
      <p class="page-head-sub">维护监测记录（zk_device_record），供「水质情况」大屏读取。可在此新增水温、溶氧、pH、氨氮等指标。</p>
      <el-button size="mini" type="text" icon="el-icon-back" @click="$router.push('/water_quality')">返回水质情况</el-button>
    </div>

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="设备唯一标识" prop="deviceId">
        <el-input v-model="queryParams.deviceId" placeholder="请输入设备唯一标识" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="设备编码" prop="deviceSn">
        <el-input v-model="queryParams.deviceSn" placeholder="请输入设备编码" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="设备类型ID，关联iot_device_type表" prop="deviceTypeId">
        <el-input v-model="queryParams.deviceTypeId" placeholder="请输入设备类型ID，关联iot_device_type表" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="记录时间（精确到分钟，按5分钟间隔存储）" prop="recordTime">
        <el-date-picker v-model="queryParams.recordTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item> -->
      <el-form-item label="监测指标ID" prop="metricId">
        <el-input v-model="queryParams.metricId" placeholder="请输入监测指标ID，关联iot_monitor_metric表" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="指标值" prop="metricValue">
        <el-input v-model="queryParams.metricValue" placeholder="请输入指标值" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="数据状态（1：正常，2：异常，3：已处理）" prop="dataStatus">
        <el-select v-model="queryParams.dataStatus" placeholder="请选择数据状态（1：正常，2：异常，3：已处理）" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="原始数据（保留原始上传数据）" prop="rawData">
        <el-input v-model="queryParams.rawData" placeholder="请输入原始数据（保留原始上传数据）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="采集方式（auto：自动定时，manual：手动上传）" prop="collectionMethod">
        <el-input v-model="queryParams.collectionMethod" placeholder="请输入采集方式（auto：自动定时，manual：手动上传）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="queryParams.createTimeStart"
          style="width: 150px"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="开始日期"
          clearable
          :picker-options="createTimeStartOptions"
        />
        <span style="margin: 0 6px; color: #909399;">-</span>
        <el-date-picker
          v-model="queryParams.createTimeEnd"
          style="width: 150px"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="结束日期"
          clearable
          :picker-options="createTimeEndOptions"
        />
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
                   v-hasPermi="['pl:zk-device-record:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-device-record:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="记录ID" align="center" prop="id" />
      <el-table-column label="设备唯一标识" align="center" prop="deviceId" />
      <el-table-column label="设备类型ID" align="center" prop="deviceTypeId" />
      <el-table-column label="记录时间" align="center" prop="recordTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.recordTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监测指标ID" align="center" prop="metricId" />
      <el-table-column label="指标值" align="center" prop="metricValue" />
      <el-table-column label="数据状态" align="center" prop="dataStatus" />
      <!-- <el-table-column label="原始数据" align="center" prop="rawData" /> -->
      <el-table-column label="采集方式" align="center" prop="collectionMethod" />
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
                     v-hasPermi="['pl:zk-device-record:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-device-record:delete']">删除</el-button>
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
        <el-form-item label="采集方式（auto：自动定时，manual：手动上传）" prop="collectionMethod">
          <el-input v-model="form.collectionMethod" placeholder="请输入采集方式（auto：自动定时，manual：手动上传）" />
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
import { createZkDeviceRecord, updateZkDeviceRecord, deleteZkDeviceRecord, getZkDeviceRecord, getZkDeviceRecordPage, exportZkDeviceRecordExcel } from "@/api/fish/zkDeviceRecord";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkDeviceRecord",
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
      // 设备数据记录列表
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
        deviceSn: null,
        metricId: null,
        metricValue: null,
        dataStatus: null,
        rawData: null,
        collectionMethod: null,
        createTimeStart: null,
        createTimeEnd: null,
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deviceId: [{ required: true, message: "设备唯一标识不能为空", trigger: "blur" }],
        deviceTypeId: [{ required: true, message: "设备类型ID，关联iot_device_type表不能为空", trigger: "blur" }],
        recordTime: [{ required: true, message: "记录时间（精确到分钟，按5分钟间隔存储）不能为空", trigger: "blur" }],
        metricId: [{ required: true, message: "监测指标ID，关联iot_monitor_metric表不能为空", trigger: "blur" }],
        metricValue: [{ required: true, message: "指标值不能为空", trigger: "blur" }],
        dataStatus: [{ required: true, message: "数据状态（1：正常，2：异常，3：已处理）不能为空", trigger: "blur" }],
      }
    };
  },
  computed: {
    createTimeStartOptions() {
      const end = this.queryParams.createTimeEnd
      return {
        disabledDate: (time) => {
          if (!end) return false
          return time.getTime() > new Date(end + ' 23:59:59').getTime()
        }
      }
    },
    createTimeEndOptions() {
      const start = this.queryParams.createTimeStart
      return {
        disabledDate: (time) => {
          if (!start) return false
          return time.getTime() < new Date(start + ' 00:00:00').getTime()
        }
      }
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询参数里的开始/结束日期 → 后端 LocalDateTime[] */
    buildQueryParams() {
      const params = { ...this.queryParams }
      delete params.createTimeStart
      delete params.createTimeEnd
      const start = this.queryParams.createTimeStart
        ? String(this.queryParams.createTimeStart).slice(0, 10)
        : ''
      const end = this.queryParams.createTimeEnd
        ? String(this.queryParams.createTimeEnd).slice(0, 10)
        : ''
      if (start || end) {
        params.createTime = [
          `${start || end} 00:00:00`,
          `${end || start} 23:59:59`
        ]
      } else {
        params.createTime = undefined
      }
      return params
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkDeviceRecordPage(this.buildQueryParams()).then(response => {
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
        collectionMethod: undefined,
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
      this.queryParams.createTimeStart = null;
      this.queryParams.createTimeEnd = null;
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加设备数据记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkDeviceRecord(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备数据记录";
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
          updateZkDeviceRecord(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkDeviceRecord(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除设备数据记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkDeviceRecord(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = this.buildQueryParams();
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有设备数据记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkDeviceRecordExcel(params);
        }).then(response => {
          this.$download.excel(response, '设备数据记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.page-head {
  margin-bottom: 12px;
}
.page-head-title {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.page-head-sub {
  margin: 0 0 6px;
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
}
</style>
