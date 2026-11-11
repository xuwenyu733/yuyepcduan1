<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属系统id" prop="pondSystemId">
        <el-input v-model="queryParams.pondSystemId" placeholder="请输入所属系统id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="存塘系统编号" prop="pondNum">
        <el-input v-model="queryParams.pondNum" placeholder="请输入存塘系统编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="存塘系统名称" prop="pondSystemName">
        <el-input v-model="queryParams.pondSystemName" placeholder="请输入存塘系统名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="存塘编号" prop="pondDetailNum">
        <el-input v-model="queryParams.pondDetailNum" placeholder="请输入存塘编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="批次编号" prop="batchNumber">
        <el-input v-model="queryParams.batchNumber" placeholder="请输入批次编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="报警设备id" prop="warnDeviceId">
        <el-input v-model="queryParams.warnDeviceId" placeholder="请输入报警设备id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="告警设备名" prop="warnDevice">
        <el-input v-model="queryParams.warnDevice" placeholder="请输入告警设备名" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="告警设备类型(0:系统设备;1:池塘传感器;)", prop="warnDeviceType">
        <el-select v-model="queryParams.warnDeviceType" placeholder="请选择告警设备类型(0:系统设备;1:池塘传感器;)" clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DEVICE_TYPE)" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="告警对象" prop="warnName">
        <el-input v-model="queryParams.warnName" placeholder="请输入告警对象" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="告警类型(0:环境告警;1:事故告警;-2病虫告警;)" prop="warnType">
        <el-select v-model="queryParams.warnType" placeholder="请选择告警类型(0:环境告警;1:事故告警;-2病虫告警;)" clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_WARN_TYPE)" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态(0:待处理;1:已处理;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:待处理;1:已处理;)" clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DEVICE_ALARM_STATUS)" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理描述" prop="handleInfo">
        <el-input v-model="queryParams.handleInfo" placeholder="请输入处理描述" clearable @keyup.enter.native="handleQuery"/>
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
      <el-col :span="1.5" v-if="false">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-device-alarm:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-device-alarm:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="所属车间id" align="center" prop="workshopId" />
      <el-table-column label="所属系统id" align="center" prop="pondSystemId" />
      <el-table-column label="存塘系统编号" align="center" prop="pondNum" /> -->
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="系统" align="center" prop="pondSystemName" />
      <!-- <el-table-column label="所属存塘id" align="center" prop="pondId" />
      <el-table-column label="存塘编号" align="center" prop="pondDetailNum" /> -->
      <el-table-column label="存塘" align="center" prop="pondName" show-overflow-tooltip />
      <!-- <el-table-column label="批次编号" align="center" prop="batchNumber" /> -->
      <el-table-column label="报警设备id" align="center" prop="warnDeviceId" />
      <el-table-column label="告警设备名" align="center" prop="warnDevice" show-overflow-tooltip />
      <el-table-column label="设备类型" align="center" prop="warnDeviceType" show-overflow-tooltip>
        <template v-slot="scope">
          <div :class="{'device-type-system': scope.row.warnDeviceType == 0, 'device-type-sensor': scope.row.warnDeviceType == 1}">
            <dict-tag :type="DICT_TYPE.ZK_DEVICE_ALARM_WARN_DEVICE_TYPE" :value="scope.row.warnDeviceType" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="告警对象" align="center" prop="warnName" show-overflow-tooltip />
      <el-table-column label="告警类型" align="center" prop="warnType" show-overflow-tooltip>
        <template v-slot="scope">
          <div :class="{'alarm-type-accident': scope.row.warnType == 0, 'alarm-type-environment': scope.row.warnType == 1, 'alarm-type-disease': scope.row.warnType == 2}">
            <dict-tag :type="DICT_TYPE.ZK_DEVICE_ALARM_WARN_TYPE" :value="scope.row.warnType" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="告警描述" align="center" prop="warnContent" show-overflow-tooltip />
      <el-table-column label="负责人" align="center" prop="userName" show-overflow-tooltip />
      <el-table-column label="处理状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <div 
            :class="{'status-pending': scope.row.status == 0, 'status-processed': scope.row.status == 1, 'clickable-status': scope.row.status == 0}"
            @click="handleStatusClick(scope.row)"
            :style="scope.row.status == 0 ? { cursor: 'pointer' } : {}"
          >
            <dict-tag :type="DICT_TYPE.ZK_DEVICE_ALARM_STATUS" :value="scope.row.status" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="处理描述" align="center">
        <template v-slot="scope">
          <span v-if="scope.row.handleInfo" v-html="scope.row.handleInfo.replace(/<[^>]*>/g, '')" />      
          <span v-else></span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="负责人id" align="center" prop="userId" /> -->
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="false"
                     v-hasPermi="['pl:zk-device-alarm:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-device-alarm:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所属车间id" />
        </el-form-item>
        <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入车间名称" />
        </el-form-item>
        <el-form-item label="所属系统id" prop="pondSystemId">
          <el-input v-model="form.pondSystemId" placeholder="请输入所属系统id" />
        </el-form-item>
        <el-form-item label="存塘系统编号" prop="pondNum">
          <el-input v-model="form.pondNum" placeholder="请输入存塘系统编号" />
        </el-form-item>
        <el-form-item label="存塘系统名称" prop="pondSystemName">
          <el-input v-model="form.pondSystemName" placeholder="请输入存塘系统名称" />
        </el-form-item>
        <el-form-item label="所属存塘id" prop="pondId">
          <el-input v-model="form.pondId" placeholder="请输入所属存塘id" />
        </el-form-item>
        <el-form-item label="存塘编号" prop="pondDetailNum">
          <el-input v-model="form.pondDetailNum" placeholder="请输入存塘编号" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="批次编号" prop="batchNumber">
          <el-input v-model="form.batchNumber" placeholder="请输入批次编号" />
        </el-form-item>
        <el-form-item label="报警设备id" prop="warnDeviceId">
          <el-input v-model="form.warnDeviceId" placeholder="请输入报警设备id" />
        </el-form-item>
        <el-form-item label="告警设备名" prop="warnDevice">
          <el-input v-model="form.warnDevice" placeholder="请输入告警设备名" />
        </el-form-item>
        <el-form-item label="告警设备类型(0:系统设备;1:池塘传感器;)" prop="warnDeviceType">
          <el-select v-model="form.warnDeviceType" placeholder="请选择告警设备类型(0:系统设备;1:池塘传感器;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警对象" prop="warnName">
          <el-input v-model="form.warnName" placeholder="请输入告警对象" />
        </el-form-item>
        <el-form-item label="告警类型(0:环境告警;1:事故告警;-2病虫告警;)" prop="warnType">
          <el-select v-model="form.warnType" placeholder="请选择告警类型(0:环境告警;1:事故告警;-2病虫告警;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警描述">
          <editor v-model="form.warnContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="状态(0:待处理;1:已处理;2:处理中)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理描述" prop="handleInfo">
          <el-input v-model="form.handleInfo" placeholder="请输入处理描述" />
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
    
    <!-- 告警处理弹框 -->
    <el-dialog 
      title="告警处理" 
      :visible.sync="alarmDialogVisible" 
      width="60%" 
      v-dialogDrag 
      append-to-body
    >
      <alarm-holder 
        :business-key="currentAlarmId"
        ref="alarmHolderRef"
        @handled="onAlarmHandled"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="alarmDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkDeviceAlarm, updateZkDeviceAlarm, deleteZkDeviceAlarm, getZkDeviceAlarm, getZkDeviceAlarmPage, exportZkDeviceAlarmExcel } from "@/api/fish/zkDeviceAlarm";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import DictTag from "@/components/DictTag/index.vue";
import AlarmHolder from './alarmHolder.vue';

export default {
  name: "ZkDeviceAlarm",
  components: {
    FileBusUpload,
    Editor,
    DictTag,
    AlarmHolder
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
      // 设备报警列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示告警处理弹框
      alarmDialogVisible: false,
      // 当前告警ID
      currentAlarmId: null,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        workshopId: null,
        workshopName: null,
        pondSystemId: null,
        pondNum: null,
        pondSystemName: null,
        pondId: null,
        pondDetailNum: null,
        pondName: null,
        batchNumber: null,
        warnDeviceId: null,
        warnDevice: null,
        warnDeviceType: null,
        warnName: null,
        warnType: null,
        warnContent: null,
        userName: null,
        status: null,
        handleInfo: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
        // 添加排序字段，确保未处理状态(status=0)的记录排在前面
        orderByColumn: 'status',
        isAsc: 'desc'
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
    // 添加自定义字典类型，如果系统中没有定义
    if (!DICT_TYPE.ZK_DEVICE_ALARM_STATUS) {
      DICT_TYPE.ZK_DEVICE_ALARM_STATUS = 'zk_device_alarm_status';
    }
    if (!DICT_TYPE.ZK_DEVICE_ALARM_WARN_TYPE) {
      DICT_TYPE.ZK_DEVICE_ALARM_WARN_TYPE = 'zk_device_alarm_warn_type';
    }
    if (!DICT_TYPE.ZK_DEVICE_ALARM_WARN_DEVICE_TYPE) {
      DICT_TYPE.ZK_DEVICE_ALARM_WARN_DEVICE_TYPE = 'zk_device_alarm_warn_device_type';
    }
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkDeviceAlarmPage(this.queryParams).then(response => {
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
        workshopId: undefined,
        workshopName: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondSystemName: undefined,
        pondId: undefined,
        pondDetailNum: undefined,
        pondName: undefined,
        batchNumber: undefined,
        warnDeviceId: undefined,
        warnDevice: undefined,
        warnDeviceType: undefined,
        warnName: undefined,
        warnType: undefined,
        warnContent: undefined,
        userName: undefined,
        status: undefined,
        handleInfo: undefined,
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
      this.title = "添加设备报警";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkDeviceAlarm(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备报警";
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
          updateZkDeviceAlarm(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkDeviceAlarm(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除设备报警编号为"' + id + '"的数据项?').then(function() {
          return deleteZkDeviceAlarm(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 处理状态点击打开弹框 */
    handleStatusClick(row) {
      // 只有待处理状态可以点击处理
      if (row.status === 0) {
        this.currentAlarmId = row.id;
        this.alarmDialogVisible = true;
      }
    },
    /** 告警处理完成后的回调 */
    onAlarmHandled() {
      this.alarmDialogVisible = false;
      this.getList(); // 重新加载列表，更新状态
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有设备报警数据项?').then(() => {
          this.exportLoading = true;
          return exportZkDeviceAlarmExcel(params);
        }).then(response => {
          this.$download.excel(response, '设备报警.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.device-type-system {
  border: 1px solid #4096FF;
  border-radius: 4px;
  color: #4096FF;
}

.clickable-status {
  transition: all 0.3s ease;
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
}

.device-type-sensor {
  border: 1px solid #67C23A;
  border-radius: 4px;
  color: #67C23A;
}

.alarm-type-environment {
  border: 1px solid #E6A23C;
  border-radius: 4px;
  color: #E6A23C;
  // background-color: #FFEAA9;

}

.alarm-type-accident {
  border: 1px solid #F56C6C;
  border-radius: 4px;
  color: #F56C6C;
  // background-color: #FEE2E2;
}

.alarm-type-disease {
  border: 1px solid #4096FF;
  border-radius: 4px;
  color: #4096FF;
  // background-color: #E6F7FF;

}

.status-pending {
  border: 1px solid #4096FF;
  border-radius: 4px;
  color: #4096FF;
  // background-color: #E6F7FF;
}

.status-processed {
  border: 1px solid #67C23A;
  border-radius: 4px;
  color: #67C23A;
  // background-color: #F0F9EB;
}
</style>
