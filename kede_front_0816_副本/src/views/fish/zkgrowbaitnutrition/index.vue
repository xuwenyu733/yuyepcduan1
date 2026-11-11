<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="饵料名称" prop="baitName">
        <el-input v-model="queryParams.baitName" placeholder="请输入饵料名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="颗粒大小(mm)" prop="pelletSize">
        <el-input v-model="queryParams.pelletSize" placeholder="请输入颗粒大小(mm)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="粗蛋白(%)" prop="crudeProtein">
        <el-input v-model="queryParams.crudeProtein" placeholder="请输入粗蛋白(%)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="粗脂肪(%)" prop="crudeFat">
        <el-input v-model="queryParams.crudeFat" placeholder="请输入粗脂肪(%)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="无氮浸出物(%)" prop="nitrogenFreeExtract">
        <el-input v-model="queryParams.nitrogenFreeExtract" placeholder="请输入无氮浸出物(%)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="粗纤维(%)" prop="crudeFiber">
        <el-input v-model="queryParams.crudeFiber" placeholder="请输入粗纤维(%)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="磷(%)" prop="phosphorus">
        <el-input v-model="queryParams.phosphorus" placeholder="请输入磷(%)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="总能(MJ)" prop="grossEnergy">
        <el-input v-model="queryParams.grossEnergy" placeholder="请输入总能(MJ)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="消化能(MJ)" prop="digestibleEnergy">
        <el-input v-model="queryParams.digestibleEnergy" placeholder="请输入消化能(MJ)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openForm(undefined)"
          v-hasPermi="['fish:zk-grow-bait-nutrition:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-grow-bait-nutrition:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-grow-bait-nutrition:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="饵料名称" align="center" prop="baitName" />
      <el-table-column label="厂家名称" align="center" prop="manufacturer" />
      <el-table-column label="颗粒大小(mm)" align="center" prop="pelletSize" />
      <el-table-column label="粗蛋白(%)" align="center" prop="crudeProtein" />
      <el-table-column label="粗脂肪(%)" align="center" prop="crudeFat" />
      <el-table-column label="无氮浸出物(%)" align="center" prop="nitrogenFreeExtract" />
      <el-table-column label="灰分(%)" align="center" prop="ashContent" />
      <el-table-column label="粗纤维(%)" align="center" prop="crudeFiber" />
      <el-table-column label="磷(%)" align="center" prop="phosphorus" />
      <el-table-column label="总能(MJ)" align="center" prop="grossEnergy" />
      <el-table-column label="消化能(MJ)" align="center" prop="digestibleEnergy" />
      <el-table-column label="水分(%)" align="center" prop="moisture" />
      <el-table-column label="钙(%)" align="center" prop="calcium" />
      <el-table-column label="赖氨酸(%)" align="center" prop="lysine" />
      <el-table-column label="蛋氨酸(%)" align="center" prop="methionine" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-grow-bait-nutrition:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-grow-bait-nutrition:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkGrowBaitNutritionForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkGrowBaitNutritionApi from '@/api/fish/zkgrowbaitnutrition';
import ZkGrowBaitNutritionForm from './ZkGrowBaitNutritionForm.vue';
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkGrowBaitNutrition",
  components: {
    ZkGrowBaitNutritionForm
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
      // 饵料营养成分列表
      list: [],
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 选中行
      currentRow: {},
      checkedIds: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        baitName: null,
        pelletSize: null,
        crudeProtein: null,
        crudeFat: null,
        nitrogenFreeExtract: null,
        ashContent: null,
        crudeFiber: null,
        phosphorus: null,
        grossEnergy: null,
        digestibleEnergy: null,
        createTime: [],
        deptId: null,
      },
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getList();
  },
  methods: {
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await ZkGrowBaitNutritionApi.getZkGrowBaitNutritionPage(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
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
      await this.$modal.confirm('是否确认删除饵料营养成分编号为"' + id + '"的数据项?')
      try {
        await ZkGrowBaitNutritionApi.deleteZkGrowBaitNutrition(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除饵料营养成分 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkGrowBaitNutritionApi.deleteZkGrowBaitNutritionList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有饵料营养成分数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkGrowBaitNutritionApi.exportZkGrowBaitNutritionExcel(this.queryParams);
        this.$download.excel(data, '饵料营养成分.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>