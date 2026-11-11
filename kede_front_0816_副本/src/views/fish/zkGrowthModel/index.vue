<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="所属系统id" prop="pondSystemId">
        <el-input v-model="queryParams.pondSystemId" placeholder="请输入所属系统id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘系统编号" prop="pondNum">
        <el-input v-model="queryParams.pondNum" placeholder="请输入存塘系统编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="系统名称" prop="pondSystemName">
        <el-input v-model="queryParams.pondSystemName" placeholder="请输入存塘系统名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="存塘编号" prop="pondDetailNum">
        <el-input v-model="queryParams.pondDetailNum" placeholder="请输入存塘编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="批次编号" prop="batchNumber">
        <el-input v-model="queryParams.batchNumber" placeholder="请输入批次编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="平均长度" prop="averageLength">
        <el-input v-model="queryParams.averageLength" placeholder="请输入平均长度" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="平均体重" prop="averageWeight">
        <el-input v-model="queryParams.averageWeight" placeholder="请输入平均体重" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="增长速度" prop="growthRate">
        <el-input v-model="queryParams.growthRate" placeholder="请输入增长速度" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="规格" prop="specification">
        <el-input v-model="queryParams.specification" placeholder="请输入规格" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-growth-model:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-growth-model:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="所属车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="车间名称" align="center" prop="workshopName" />
      <!-- <el-table-column label="所属系统id" align="center" prop="pondSystemId" />
      <el-table-column label="存塘系统编号" align="center" prop="pondNum" /> -->
      <el-table-column label="系统名称" align="center" prop="pondSystemName" />
      <!-- <el-table-column label="所属存塘id" align="center" prop="pondId" />
      <el-table-column label="存塘编号" align="center" prop="pondDetailNum" /> -->
      <el-table-column label="存塘名称" align="center" prop="pondName" />
      <el-table-column label="批次编号" align="center" prop="batchNumber" />
      <el-table-column label="平均长度" align="center" prop="averageLength" />
      <el-table-column label="平均体重" align="center" prop="averageWeight" />
      <el-table-column label="增长速度" align="center" prop="growthRate" />
      <el-table-column label="规格" align="center" prop="specification" />
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
                     v-hasPermi="['pl:zk-growth-model:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-growth-model:delete']">删除</el-button>
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
        <el-form-item label="平均长度" prop="averageLength">
          <el-input v-model="form.averageLength" placeholder="请输入平均长度" />
        </el-form-item>
        <el-form-item label="平均体重" prop="averageWeight">
          <el-input v-model="form.averageWeight" placeholder="请输入平均体重" />
        </el-form-item>
        <el-form-item label="增长速度" prop="growthRate">
          <el-input v-model="form.growthRate" placeholder="请输入增长速度" />
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格" />
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
import { createZkGrowthModel, updateZkGrowthModel, deleteZkGrowthModel, getZkGrowthModel, getZkGrowthModelPage, exportZkGrowthModelExcel } from "@/api/fish/zkGrowthModel";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkGrowthModel",
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
      // 生长模型列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
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
        averageLength: null,
        averageWeight: null,
        growthRate: null,
        specification: null,
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
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkGrowthModelPage(this.queryParams).then(response => {
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
        averageLength: undefined,
        averageWeight: undefined,
        growthRate: undefined,
        specification: undefined,
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
      this.title = "添加生长模型";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkGrowthModel(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改生长模型";
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
          updateZkGrowthModel(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkGrowthModel(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除生长模型编号为"' + id + '"的数据项?').then(function() {
          return deleteZkGrowthModel(id);
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
      this.$modal.confirm('是否确认导出所有生长模型数据项?').then(() => {
          this.exportLoading = true;
          return exportZkGrowthModelExcel(params);
        }).then(response => {
          this.$download.excel(response, '生长模型.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
