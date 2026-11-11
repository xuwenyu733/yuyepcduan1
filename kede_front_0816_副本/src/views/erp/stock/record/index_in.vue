<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- 
      <el-form-item label="产品编号" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入产品编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="仓库编号" prop="warehouseId">
        <el-input v-model="queryParams.warehouseId" placeholder="请输入仓库编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="出入库数量" prop="count">
        <el-input v-model="queryParams.count" placeholder="请输入出入库数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="总库存量" prop="totalCount">
        <el-input v-model="queryParams.totalCount" placeholder="请输入总库存量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="业务类型" prop="bizType">
        <el-select v-model="queryParams.bizType" placeholder="请选择业务类型" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务编号" prop="bizId">
        <el-input v-model="queryParams.bizId" placeholder="请输入业务编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="业务项编号" prop="bizItemId">
        <el-input v-model="queryParams.bizItemId" placeholder="请输入业务项编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="业务单号" prop="bizNo">
        <el-input v-model="queryParams.bizNo" placeholder="请输入业务单号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      -->
      <el-form-item label="业务单号" prop="bizNo">
        <el-input v-model="queryParams.bizNo" placeholder="请输入业务单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <!--  v-hasPermi="['erp:stock-record:create']" -->
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['erp:stock-record:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="产品编号" align="center" prop="productId" /> -->
      <el-table-column label="分类名称" align="center" prop="categoryName" show-overflow-tooltip />
      <el-table-column label="产品名称" align="center" prop="productName" show-overflow-tooltip />
      <el-table-column label="单位" align="center" prop="unitName" />
      <!-- <el-table-column label="仓库编号" align="center" prop="warehouseId" /> -->
      <el-table-column label="仓库" align="center" prop="warehouseName" show-overflow-tooltip />
      <el-table-column label="入库数量" align="center" prop="count" />
      <!-- 总库存量 -->
      <el-table-column label="当前库存" align="center" prop="totalCount" />
      <!-- <el-table-column label="业务类型" align="center" prop="bizType" /> -->
      <el-table-column label="业务类型" align="center" prop="custStatus" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ERP_STOCK_RECORD_BIZ_TYPE" :value="scope.row.bizType" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="业务编号" align="center" prop="bizId" /> -->
      <!-- <el-table-column label="业务项编号" align="center" prop="bizItemId" /> -->
      <!-- <el-table-column label="业务单号" align="center" prop="bizNo" /> -->
      <el-table-column label="业务单号" align="left" prop="bizNo" min-width="100" show-overflow-tooltip>
        <template v-slot="scope">
          <span style="cursor: pointer;" v-clipboard:copy="scope.row.bizNo" v-clipboard:success="clipboardSuccess">
            {{ scope.row.bizNo }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" v-if="false">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['erp:stock-record:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['erp:stock-record:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">

        <el-form-item label="产品编号" prop="productId">
          <!-- <el-input v-model="form.productId" placeholder="请输入产品编号" /> -->
          <el-select v-model="form.productId" placeholder="请选择产品" style="width: 100%" @change="update_productId">
            <el-option v-for="item in products" :key="item.id"
              :label="`${item.name}-${item.unitName}(${item.stockInfo ? item.stockInfo : ''})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库编号" prop="warehouseId">
          <!-- <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" /> -->
          <el-select v-model="form.warehouseId" style="width:100%">
            <el-option v-for="w in warehouses" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="count">
          <el-input v-model="form.count" placeholder="请输入出入库数量" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>

        <!-- <el-form-item label="产品编号" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入产品编号" />
        </el-form-item>
        <el-form-item label="仓库编号" prop="warehouseId">
          <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
        </el-form-item>
        <el-form-item label="入库数量" prop="count">
          <el-input v-model="form.count" placeholder="请输入出入库数量" />
        </el-form-item>
        <el-form-item label="总库存量" prop="totalCount">
          <el-input v-model="form.totalCount" placeholder="请输入总库存量" />
        </el-form-item>
        <el-form-item label="业务类型" prop="bizType">
          <el-select v-model="form.bizType" placeholder="请选择业务类型">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务编号" prop="bizId">
          <el-input v-model="form.bizId" placeholder="请输入业务编号" />
        </el-form-item>
        <el-form-item label="业务项编号" prop="bizItemId">
          <el-input v-model="form.bizItemId" placeholder="请输入业务项编号" />
        </el-form-item>
        <el-form-item label="业务单号" prop="bizNo">
          <el-input v-model="form.bizNo" placeholder="请输入业务单号" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createStockRecord, updateStockRecord, deleteStockRecord, getStockRecord, getStockRecordPage, exportStockRecordExcel } from "@/api/erp/stockRecord";
import FileBusUpload from "@/components/FileBusUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createProduct, updateProduct, deleteProduct, getProduct, getProductPage, exportProductExcel } from "@/api/erp/product";
import { createWarehouse, updateWarehouse, deleteWarehouse, getWarehouse, getWarehousePage, exportWarehouseExcel } from "@/api/erp/warehouse";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "StockRecord",
  components: {
    FileBusUpload,
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
      // ERP 产品库存明细列表
      list: [],
      warehouses: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        inType: 1, // 0:默认;1:入库;2:出库;
        productId: null,
        warehouseId: null,
        count: null,
        totalCount: null,
        bizType: null,
        bizId: null,
        bizItemId: null,
        bizNo: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productId: [{ required: true, message: "产品编号不能为空", trigger: "blur" }],
        warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
        count: [{ required: true, message: "出入库数量不能为空", trigger: "blur" }],
        totalCount: [{ required: true, message: "总库存量不能为空", trigger: "blur" }],
        bizType: [{ required: true, message: "业务类型不能为空", trigger: "change" }],
        bizId: [{ required: true, message: "业务编号不能为空", trigger: "blur" }],
        bizItemId: [{ required: true, message: "业务项编号不能为空", trigger: "blur" }],
        bizNo: [{ required: true, message: "业务单号不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.init();
  },
  methods: {
    init() {
      this.getList();
      this.get_products();
      this.get_warehouseList();
    },

    update_warehouseId(currId) {
      let item = null;
      this.warehouses.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.warehouseName = item.warehouseName;
      this.form.warehouseId = item.id;
      // console.log("===========>"+JSON.stringify(this.form));
    },
    get_warehouseList() {
      this.warehouses = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        getWarehousePage(params).then(response => {
          this.warehouses.push(...response.data.list);
          resolve(this.warehouses);
        })
      });
      return promise;
    },

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

    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功");
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getStockRecordPage(this.queryParams).then(response => {
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
        productId: undefined,
        warehouseId: undefined,
        count: undefined,
        totalCount: undefined,
        bizType: "10",
        bizId: 0,
        bizItemId: 0,
        bizNo: 0,
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
      this.init();
      this.reset();
      this.open = true;
      this.title = "添加入库记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.init();
      this.reset();
      const id = row.id;
      getStockRecord(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改入库记录";
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
        if (!this.form.tenantId) this.form.tenantId = this.userInfo.tenantId;
        if (!this.form.deptId) this.form.deptId = this.userInfo.deptId;
        if (!this.form.userId) this.form.userId = this.userInfo.userId;
        if (!this.form.bizNo) this.form.bizNo = "手动入库";
        let param = JSON.parse(JSON.stringify(this.form));
        // 修改的提交
        if (this.form.id != null) {
          updateStockRecord(param).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createStockRecord(param).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除ERP 产品库存明细编号为"' + id + '"的数据项?').then(function () {
        return deleteStockRecord(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = { ...this.queryParams };
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有ERP 产品库存明细数据项?').then(() => {
        this.exportLoading = true;
        return exportStockRecordExcel(params);
      }).then(response => {
        this.$download.excel(response, 'ERP 产品库存明细.xls');
        this.exportLoading = false;
      }).catch(() => { });
    }
  }
};
</script>
