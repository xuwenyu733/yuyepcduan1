<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="原塘id" prop="originPondId">
        <el-input v-model="queryParams.originPondId" placeholder="请输入原塘id" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-input v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="鱼苗编号(pl_product)" prop="productNo">
        <el-input v-model="queryParams.productNo" placeholder="请输入鱼苗编号(pl_product)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="鱼苗名称" prop="fishName">
        <el-input v-model="queryParams.fishName" placeholder="请输入鱼苗名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="鱼苗数量" prop="fishNum">
        <el-input v-model="queryParams.fishNum" placeholder="请输入鱼苗数量" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="鱼苗数量差距(正多负少)" prop="fishNumDiff">
        <el-input v-model="queryParams.fishNumDiff" placeholder="请输入鱼苗数量差距(正多负少)" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="投放类型(-3:分池;-2:死亡;-1:销售;1:投放;)" prop="addType">
        <el-select v-model="queryParams.addType" placeholder="请选择投放类型(-3:分池;-2:死亡;-1:销售;1:投放;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5" v-if="showDialog">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openForm(undefined)"
          v-hasPermi="['fish:zk-pond-fish-parent:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-pond-fish-parent:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-pond-fish-parent:delete']">
          批量删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-s-grid" size="mini"
          @click="toPage(`/jidi/zk-pond-fish-detail-split`)" v-hasPermi="['fish:zk-pond-fish-parent:delete']">
          分池记录明细
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="原塘id" align="center" prop="originPondId" /> -->
      <!-- <el-table-column label="鱼苗种类" align="center" prop="fishTypes" /> -->
      <!-- (pl_product) -->
      <el-table-column label="鱼苗编号" align="center" prop="productNo" width="180" />
      <el-table-column label="鱼苗名称" align="center" prop="fishName" />
      <el-table-column label="原池编号" align="center" prop="originPondDetailNum" />
      <el-table-column label="原池数量" align="center" prop="fishNum" />
      <!-- (正多负少) -->
      <el-table-column label="分池后差距" align="center" prop="fishNumDiff" />
      <!-- (-3:分池;-2:死亡;-1:销售;1:投放;) -->
      <!-- <el-table-column label="投放类型" align="center" prop="addType" /> -->
      <el-table-column label="操作类型" align="center" prop="addType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE" :value="scope.row.addType" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="部门" align="center" prop="deptId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
          <span v-else="scope.row.deptId">
            <deptName :deptId="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-pond-fish-parent:update']">修正</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-pond-fish-parent:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <div v-if="showDialog">
      <ZkPondFishParentForm ref="formRef" @success="getList" />
    </div>
  </div>
</template>

<script>
import * as ZkPondFishParentApi from '@/api/fish/zkpondfishparent';
import ZkPondFishParentForm from './ZkPondFishParentForm.vue';
export default {
  name: "ZkPondFishParent",
  components: {
    ZkPondFishParentForm
  },
  data() {
    return {
      showDialog: true,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 鱼池操作父列表
      list: [],
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 选中行
      currentRow: {},
      checkedIds: [],
      pondSystemList: [], 
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        originPondId: null,
        fishTypes: null,
        productNo: null,
        fishName: null,
        fishNum: null,
        fishNumDiff: null,
        addType: null,
        remark: null,
        createTime: [],
        deptId: null,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    toPage(path) {
      this.open = false;
      this.$router.push({
          path: path,
          params: {},
          query: {  },
      });
    },
    /** 查询列表 */
    async getList() {
      try {
        this.showDialog = false;
        this.loading = true;
        const res = await ZkPondFishParentApi.getZkPondFishParentPage(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
        this.showDialog = true;
      }
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
    /** 添加/修改操作 */
    openForm(id) {
      this.$refs["formRef"].open(id);
    },
    /** 删除按钮操作 */
    async handleDelete(row) {
      const id = row.id;
      await this.$modal.confirm('是否确认删除鱼池操作父编号为"' + id + '"的数据项?')
      try {
        await ZkPondFishParentApi.deleteZkPondFishParent(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除鱼池操作父 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkPondFishParentApi.deleteZkPondFishParentList(this.checkedIds);
        this.checkedIds = [];
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    handleRowCheckboxChange(records) {
      this.checkedIds = records.map((item) => item.id);
    },
    /** 导出按钮操作 */
    async handleExport() {
      await this.$modal.confirm('是否确认导出所有鱼池操作父数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkPondFishParentApi.exportZkPondFishParentExcel(this.queryParams);
        this.$download.excel(data, '鱼池操作父.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>