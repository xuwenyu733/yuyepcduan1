<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="app版本" prop="appVersion">
        <el-input v-model="queryParams.appVersion" placeholder="请输入app版本" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="app地址" prop="fileUrl">
        <el-input v-model="queryParams.fileUrl" placeholder="请输入app地址" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-app-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-app-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="app版本" align="center" prop="appVersion" />
      <!-- <el-table-column label="基础地址" align="center" prop="baseWebUrl" show-overflow-tooltip /> -->
      <el-table-column label="基础地址" align="center" prop="baseWebUrl" show-overflow-tooltip>
        <template v-slot="scope">
          <span @click.stop="copyMsg">{{ scope.row.baseWebUrl }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="App地址" align="center" prop="fileUrl" show-overflow-tooltip /> -->
      <el-table-column label="App地址" align="center" prop="fileUrl" show-overflow-tooltip>
        <template v-slot="scope">
          <span @click.stop="copyMsg">{{ scope.row.fileUrl }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新描述" align="center" prop="remark" show-overflow-tooltip />
      <!-- <el-table-column label="测试设备" align="center" prop="deviceMac" show-overflow-tooltip /> -->
      <el-table-column label="测试设备" align="center" prop="deviceMac" show-overflow-tooltip>
        <template v-slot="scope">
          <span @click.stop="copyMsg">{{ scope.row.deviceMac }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-app-log:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-app-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="app版本" prop="appVersion">
          <el-input v-model="form.appVersion" placeholder="请输入app版本" />
        </el-form-item>
        <el-form-item label="基础地址" prop="baseWebUrl">
          <el-input v-model="form.baseWebUrl" placeholder="请输入测试设备" />
        </el-form-item>
        <!-- <el-form-item label="app地址" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入app地址" />
        </el-form-item> -->
        <el-form-item label="更新描述" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入更新描述" />
        </el-form-item>
        <el-form-item label="测试设备" prop="deviceMac">
          <el-input v-model="form.deviceMac" placeholder="请输入测试设备" />
        </el-form-item>
        <el-form-item label="App地址" prop="fileUrl" @click.stop="copyMsg(form.fileUrl)">
          <!-- readonly -->
          <el-input v-model="form.fileUrl" placeholder="请输入测试设备"   />
        </el-form-item>
        <el-form-item label="相关附件" prop="fileId">
          <FileBusUpload ref="uploadRef" :fid="form.fileId" bus-type="zk_app_log" @sendCurrFiles="receiveFileUpload"></FileBusUpload>
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
import { createZkAppLog, updateZkAppLog, deleteZkAppLog, getZkAppLog, getZkAppLogPage, exportZkAppLogExcel } from "@/api/fish/zkAppLog";
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkAppLog",
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
      // app记录列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        appVersion: null,
        fileUrl: null,
        remark: null,
        createTime: [],
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
  },
  methods: {
    receiveFileUpload(res) {
      console.log(`res:${JSON.stringify(res)}`);
      this.form.fileUrl = res.fileUrl;
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkAppLogPage(this.queryParams).then(response => {
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
        appVersion: undefined,
        fileUrl: undefined,
        baseWebUrl: "https://t1.kedeyuye.online",
        remark: undefined,
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
      this.title = "添加app记录";
      this.form.fileId = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkAppLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改app记录";
        if (!this.form.fileId) {
          this.form.fileId = getUUID().replaceAll("-", "");
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
          updateZkAppLog(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkAppLog(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },    
    async copyMsg(currDoc) {
      console.log(`=========>${currDoc}`);
      try {
          // 获取要复制的HTML内容
          const content = this.my_common.decode(currDoc.target.innerHTML);
          // 使用 navigator.clipboard.writeText() 复制内容到剪贴板
          await navigator.clipboard.writeText(content);
          // console.log('内容已复制到剪贴板！', content);
          this.$modal.msgSuccess('内容已复制到剪贴板！');
      } catch (err) {
          console.error('复制失败:', err);
          this.$modal.msgError(`复制失败!`,err);
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除app记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkAppLog(id);
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
      this.$modal.confirm('是否确认导出所有app记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkAppLogExcel(params);
        }).then(response => {
          this.$download.excel(response, 'app记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
