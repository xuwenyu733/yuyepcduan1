<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="采购单号" prop="purchaseOrderNo">
        <el-input v-model="queryParams.purchaseOrderNo" placeholder="请输入采购单号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="品类" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入品类" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="来源（城市/地区）" prop="source">
        <el-input v-model="queryParams.source" placeholder="请输入来源（城市/地区）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="具体地址" prop="detailedAddress">
        <el-input v-model="queryParams.detailedAddress" placeholder="请输入具体地址" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="queryParams.contactPerson" placeholder="请输入联系人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="queryParams.contactPhone" placeholder="请输入联系电话" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="采购数量(kg)" prop="quantity">
        <el-input v-model="queryParams.quantity" placeholder="请输入采购数量(kg)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="单价(元/kg)" prop="unitPrice">
        <el-input v-model="queryParams.unitPrice" placeholder="请输入单价(元/kg)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="总金额(元)" prop="totalAmount">
        <el-input v-model="queryParams.totalAmount" placeholder="请输入总金额(元)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="运输方式" prop="transportMethod">
        <el-input v-model="queryParams.transportMethod" placeholder="请输入运输方式" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="采购日期" prop="purchaseDate">
        <el-date-picker v-model="queryParams.purchaseDate" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="采购负责人" prop="purchaseManager">
        <el-input v-model="queryParams.purchaseManager" placeholder="请输入采购负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="规格-单尾重(kg)" prop="avgWeight">
        <el-input v-model="queryParams.avgWeight" placeholder="请输入规格-单尾重(kg)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="notes">
        <el-input v-model="queryParams.notes" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
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
          v-hasPermi="['fish:purify-purchase:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:purify-purchase:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:purify-purchase:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="采购单号" align="center" prop="purchaseOrderNo" />
      <!-- <el-table-column label="品类" align="center" prop="category" /> -->
      <el-table-column label="品类" align="center" prop="category" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="source" />
      <el-table-column label="具体地址" align="center" prop="detailedAddress" />
      <el-table-column label="联系人" align="center" prop="contactPerson" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="采购数量(kg)" align="center" prop="quantity" />
      <el-table-column label="单价(元/kg)" align="center" prop="unitPrice" />
      <el-table-column label="总金额(元)" align="center" prop="totalAmount" />
      <el-table-column label="运输方式" align="center" prop="transportMethod" />
      <!-- <el-table-column label="采购日期" align="center" prop="purchaseDate" /> -->
      <el-table-column label="采购日期" align="center" prop="purchaseDate" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.purchaseDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采购负责人" align="center" prop="purchaseManager" />
      <el-table-column label="规格-单尾重(kg)" align="center" prop="avgWeight" />
      <el-table-column label="备注" align="center" prop="notes" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="所属部门" align="center" prop="deptId" show-overflow-tooltip>
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
            v-hasPermi="['fish:purify-purchase:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:purify-purchase:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <PurifyPurchaseForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as PurifyPurchaseApi from '@/api/fish/zkpurifypurchase';
import PurifyPurchaseForm from './PurifyPurchaseForm.vue';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
export default {
  name: "PurifyPurchase",
  components: {
    PurifyPurchaseForm
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
      // 采购记录列表
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
        purchaseOrderNo: null,
        category: null,
        source: null,
        detailedAddress: null,
        contactPerson: null,
        contactPhone: null,
        quantity: null,
        unitPrice: null,
        totalAmount: null,
        transportMethod: null,
        purchaseDate: [],
        purchaseManager: null,
        avgWeight: null,
        notes: null,
        createTime: [],
        deptId: null,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await PurifyPurchaseApi.getPurifyPurchasePage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除采购记录编号为"' + id + '"的数据项?')
      try {
        await PurifyPurchaseApi.deletePurifyPurchase(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除采购记录 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await PurifyPurchaseApi.deletePurifyPurchaseList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有采购记录数据项?');
      try {
        this.exportLoading = true;
        const data = await PurifyPurchaseApi.exportPurifyPurchaseExcel(this.queryParams);
        this.$download.excel(data, '采购记录.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>