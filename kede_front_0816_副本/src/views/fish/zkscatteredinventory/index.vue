<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="商品id" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入商品id" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
        <el-form-item label="商品" prop="productId">
          <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable @change="handleQuery"
             size="small" style="width: 100%">
            <el-option v-for="(item, index) in products" :key="item.id+'_p_'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      <!-- <el-form-item label="仓库id" prop="warehouseId">
        <el-input v-model="queryParams.warehouseId" placeholder="请输入仓库id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="单位id" prop="unitId">
        <el-input v-model="queryParams.unitId" placeholder="请输入单位id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="剩余数量" prop="remainingQuantity">
        <el-input v-model="queryParams.remainingQuantity" placeholder="请输入剩余数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="零星库存类型(1:手动出库;2:手动入库零星;)" prop="busType">
        <el-select v-model="queryParams.busType" placeholder="请选择零星库存类型(1:手动出库;2:手动入库零星;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
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
          v-hasPermi="['fish:zk-scattered-inventory:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-scattered-inventory:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-scattered-inventory:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="商品id" align="center" prop="productId" />
      <el-table-column label="仓库id" align="center" prop="warehouseId" />
      <el-table-column label="单位id" align="center" prop="unitId" /> -->
      <el-table-column label="商品" align="center" prop="productName" />
      <el-table-column label="仓库" align="center" prop="warehouseName" />
      <el-table-column label="品类" align="center" prop="categoryName" />
      <el-table-column label="剩余数量" align="center" prop="remainingQuantity" />
      <el-table-column label="单位" align="center" prop="unitName" />
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <!-- <el-table-column label="零星库存类型(1:手动出库;2:手动入库零星;)" align="center" prop="busType" /> -->
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
            v-hasPermi="['fish:zk-scattered-inventory:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-scattered-inventory:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkScatteredInventoryForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkScatteredInventoryApi from '@/api/fish/zkscatteredinventory';
import ZkScatteredInventoryForm from './ZkScatteredInventoryForm.vue';
import {
  getProductPage
} from "@/api/erp/product";

export default {
  name: "ZkScatteredInventory",
  components: {
    ZkScatteredInventoryForm
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
      // 零星库存列表
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
        productId: null,
        warehouseId: null,
        unitId: null,
        remainingQuantity: null,
        remark: null,
        busType: null,
        createTime: [],
        deptId: null,
      },
      products: [],
    };
  },
  created() {
    this.getList();
    this.get_products();
  },
  methods: {
    update_productId(currId) {
      let item = null;
      this.products.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.productName = item.productName;
      this.form.productId = item.id;
      this.form.standard = item.standard;
      if (item.warehouseIds) {
        this.form.warehouseId = parseInt(item.warehouseIds.split(",")[0]);
      }
      console.log("===========>" + JSON.stringify(this.form));
    },
    get_products() {
      this.products = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getProductPage(params).then(response => {
        this.products.push(...response.data.list);
        this.products.forEach(obj => {
          obj.taxPercent = 0;
          if (!obj.stockCount) obj.stockCount = 0;
        })
      })
    },
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await ZkScatteredInventoryApi.getZkScatteredInventoryPage(this.queryParams);
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
      await this.$modal.confirm('是否确认删除零星库存编号为"' + id + '"的数据项?')
      try {
        await ZkScatteredInventoryApi.deleteZkScatteredInventory(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除零星库存 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkScatteredInventoryApi.deleteZkScatteredInventoryList(this.checkedIds);
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
      await this.$modal.confirm('是否确认导出所有零星库存数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkScatteredInventoryApi.exportZkScatteredInventoryExcel(this.queryParams);
        this.$download.excel(data, '零星库存.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>