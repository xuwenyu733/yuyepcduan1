<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="检测名称" prop="checkName">
        <el-input v-model="queryParams.checkName" placeholder="请输入检测名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- (1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;) -->
      <el-form-item label="类型" prop="status">
        <el-select v-model="queryParams.checkType" placeholder="请选择类型" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_SCHEDULING_TYPE_CHECK_TYPE)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
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
                   v-hasPermi="['pl:zk-scheduling-type:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-scheduling-type:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="检测名称" align="center" prop="checkName" />
      <el-table-column label="检测内容" align="center" prop="picUrl" width="100">
        <template v-slot="scope">
          <image-preview v-if="scope.row.fileUrl != null && scope.row.fileUrl != ''" :src="scope.row.fileUrl"
            width="60px" height="60px"></image-preview>
          <i v-else>
            无检测内容图片
          </i>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="checkType" />
      <el-table-column label="具体配置" align="center" prop="checkContent" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-scheduling-type:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-scheduling-type:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型" prop="checkType">
          <el-select v-model="form.checkType" placeholder="请输入类型" filterable clearable size="small" @change="change_checkType">
            <el-option v-for="dict in checkTypeList" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="检测名称" prop="checkName">
          <el-input v-model="form.checkName" placeholder="请输入检测名称" />
        </el-form-item>
        <el-form-item label="备注">
          <editor v-model="form.checkContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_scheduling_type" @uploadSuccess="confirmUrl"></FileBusUpload>
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
import { createZkSchedulingType, updateZkSchedulingType, deleteZkSchedulingType, getZkSchedulingType, getZkSchedulingTypePage, exportZkSchedulingTypeExcel } from "@/api/fish/zkSchedulingType";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import ImagePreview from "@/components/ImagePreview";

export default {
  name: "ZkSchedulingType",
  components: {
      FileBusUpload,
    Editor, ImagePreview, 
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
      // 检测配置列表
      list: [],
      checkTypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        checkName: null,
        checkType: null,
        checkContent: null,
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
    this.checkTypeList = this.getDictDatas(DICT_TYPE.ZK_SCHEDULING_TYPE_CHECK_TYPE);
    // console.log(`checkTypeList============>${JSON.stringify(this.checkTypeList)}`);
  },
  methods: {
    change_checkType(item) {
      // console.log(`item======>${item}`);
      let curr = null;
      this.checkTypeList.forEach((dd, di) => {
        if(dd.value == item) {
          curr = dd;
          return;
        }
      });
      this.form.checkName = curr.label;
      // console.log(`item======>${item}==>${JSON.stringify(this.form)}`);
    },
    confirmUrl(obj={}) {
      this.form.fileUrl = obj.url;
      // console.log("confirmUrl=====>"+JSON.stringify(this.form));
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkSchedulingTypePage(this.queryParams).then(response => {
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
        checkName: undefined,
        checkType: undefined,
        checkContent: undefined,
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
      this.title = "添加检测配置";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkSchedulingType(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改检测配置";
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
          this.form.checkContent = null;
          updateZkSchedulingType(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkSchedulingType(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除检测配置编号为"' + id + '"的数据项?').then(function() {
          return deleteZkSchedulingType(id);
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
      this.$modal.confirm('是否确认导出所有检测配置数据项?').then(() => {
          this.exportLoading = true;
          return exportZkSchedulingTypeExcel(params);
        }).then(response => {
          this.$download.excel(response, '检测配置.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
