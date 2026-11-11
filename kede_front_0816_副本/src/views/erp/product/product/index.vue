<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-card class="box-card mb8" shadow="never">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px" style="margin-bottom: -18px;">
        <el-form-item label="产品分类" prop="categoryId">
          <el-select v-model="queryParams.categoryId" placeholder="请选择产品分类" clearable @change="handleQuery" size="small">
            <el-option v-for="(item, index) in categoryList" :key="item.id+'_c_'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="产品" prop="productId">
          <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable @change="handleQuery" size="small" style="width: 100%">
            <el-option v-for="(item, index) in products" :key="item.id+'_product_'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作工具栏 -->
    <el-card class="box-card mb8" shadow="never">
      <div slot="header" class="clearfix">
        <span style="font-weight: bold; font-size: 16px;">库存记录</span>
      </div>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                     v-hasPermi="['erp:product:create']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                     v-hasPermi="['erp:product:export']">导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 列表 -->
      <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="产品编号" align="center" prop="id" /> -->
      <el-table-column label="产品分类" align="center" prop="categoryName" />
      <el-table-column label="标准名" align="center" prop="name" />
      <el-table-column label="产品条码" align="center" prop="barCode" width="180" />
      <!-- <el-table-column label="产品分类编号" align="center" prop="categoryId" /> -->
      <!-- <el-table-column label="单位编号" align="center" prop="unitId" /> -->
      <!-- <el-table-column label="产品状态" align="center" prop="status" /> -->
      <!-- <el-table-column label="价格" align="center" prop="purchasePrice" /> -->
      <el-table-column label="价格" align="center" prop="purchasePrice" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ `${scope.row.purchasePrice}元/${scope.row.unitName}` }}</span>
        </template>
      </el-table-column>
      <el-table-column label="规格" align="center" prop="standard" />
      <el-table-column label="单位" align="center" prop="unitName" />
      <el-table-column label="库存数量" align="center" prop="stockCount" />
      <el-table-column label="预警阈值" align="center" prop="warningThreshold" />
      <!-- <el-table-column label="产品备注" align="center" prop="remark" /> -->
      <!-- <el-table-column label="保质期天数" align="center" prop="expiryDay" /> -->
      <!-- <el-table-column label="基础重量（kg）" align="center" prop="weight" /> -->
      <!-- <el-table-column label="销售价格" align="center" prop="salePrice" /> -->
      <!-- <el-table-column label="最低价格" align="center" prop="minPrice" /> -->
      <el-table-column label="创建人" align="center" prop="creator" show-overflow-tooltip>
        <template v-slot="scope">
          <userName :user-id="scope.row.creator"></userName>
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
                     v-hasPermi="['erp:product:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['erp:product:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                  @pagination="getList"/>
    </el-card>

    <!-- 零星库存列表 -->
    <el-card class="box-card mb8" shadow="never">
      <div slot="header" class="clearfix">
        <span style="font-weight: bold; font-size: 16px;">零星库存</span>
      </div>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openScatteredForm(undefined)"
            v-hasPermi="['fish:zk-scattered-inventory:create']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleScatteredExport"
            :loading="scatteredExportLoading" v-hasPermi="['fish:zk-scattered-inventory:export']">导出</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="scatteredCheckedIds.length === 0"
            @click="handleScatteredDeleteBatch" v-hasPermi="['fish:zk-scattered-inventory:delete']">
            批量删除
          </el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getScatteredList"></right-toolbar>
      </el-row>

      <el-table v-loading="scatteredLoading" :data="scatteredList" :stripe="true" :show-overflow-tooltip="true"
        @selection-change="handleScatteredRowCheckboxChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="商品" align="center" prop="productName" />
        <el-table-column label="仓库" align="center" prop="warehouseName" />
        <el-table-column label="品类" align="center" prop="categoryName" />
        <el-table-column label="剩余数量" align="center" prop="remainingQuantity" />
        <el-table-column label="单位" align="center" prop="unitName" />
        <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template v-slot="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="部门" align="center" prop="deptId" show-overflow-tooltip>
          <template v-slot="scope">
            <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
            <span v-else>
              <deptName :deptId="scope.row.deptId"></deptName>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template v-slot="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="openScatteredForm(scope.row.id)"
              v-hasPermi="['fish:zk-scattered-inventory:update']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleScatteredDelete(scope.row)"
              v-hasPermi="['fish:zk-scattered-inventory:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 零星库存分页 -->
      <pagination v-show="scatteredTotal > 0" :total="scatteredTotal" :page.sync="scatteredQueryParams.pageNo" :limit.sync="scatteredQueryParams.pageSize"
        @pagination="getScatteredList" />
    </el-card>

    <!-- 对话框(添加 / 修改) ERP产品 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="产品条码" prop="barCode" class="barcode-input">
          <el-input v-model="form.barCode" placeholder="产品条码自动生成" disabled>
            <el-button slot="append" icon="el-icon-refresh" @click="regenBarCode">重新生成</el-button>
          </el-input>
        </el-form-item>
        <!-- <el-form-item label="产品分类编号" prop="categoryId">
          <el-input v-model="form.categoryId" placeholder="请输入产品分类编号" />
        </el-form-item> -->
        <!-- <el-form-item label="单位编号" prop="unitId">
          <el-input v-model="form.unitId" placeholder="请输入单位编号" />
        </el-form-item> -->
        <!-- <el-form-item label="产品状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="产品分类" prop="categoryId">
          <el-select style="width:100%;" v-model="form.categoryId" placeholder="请选择产品分类" filterable clearable  @change="update_categoryId">
            <el-option v-for="(item, index) in categoryList" :key="item.id+'__'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生物类别" prop="fishTypes" v-if="form.categoryId == 7">
          <el-select v-model="form.fishTypes" placeholder="请输入生物类别" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="单位" prop="unitId">
          <el-select style="width:100%;" v-model="form.unitId" placeholder="请选择单位" filterable clearable  @change="update_unitId">
            <el-option v-for="(item, index) in unitList" :key="item.id+'__'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="价格(元)" prop="purchasePrice">
          <el-input v-model="form.purchasePrice" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="规格" prop="standard">
          <el-input v-model="form.standard" placeholder="请输入产品规格" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in getDictDatas(DICT_TYPE.COMMON_STATUS)" :key="parseInt(dict.value)" :label="parseInt(dict.value)">
              {{dict.label}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="预警阈值" prop="warningThreshold">
          <el-input v-model="form.warningThreshold" placeholder="请输入预警阈值" />
        </el-form-item>
        <el-form-item label="产品备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入产品备注" />
        </el-form-item>
        <el-form-item label="保质期天数" prop="expiryDay">
          <el-input v-model="form.expiryDay" placeholder="请输入保质期天数" />
        </el-form-item>
        <el-form-item label="重量（kg）" prop="weight">
          <el-input v-model="form.weight" placeholder="请输入基础重量（kg）" />
        </el-form-item>
        <!-- <el-form-item label="销售价格" prop="salePrice">
          <el-input v-model="form.salePrice" placeholder="请输入销售价格" />
        </el-form-item>
        <el-form-item label="最低价格" prop="minPrice">
          <el-input v-model="form.minPrice" placeholder="请输入最低价格" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 零星库存对话框(添加 / 修改) -->
    <ZkScatteredInventoryForm ref="scatteredFormRef" @success="getScatteredList" />
  </div>
</template>

<script>
import { createProduct, updateProduct, deleteProduct, getProduct, getProductPage, exportProductExcel } from "@/api/erp/product";
import * as ZkScatteredInventoryApi from '@/api/fish/zkscatteredinventory';
import ZkScatteredInventoryForm from '@/views/fish/zkscatteredinventory/ZkScatteredInventoryForm.vue';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createProductUnit, updateProductUnit, deleteProductUnit, getProductUnit, getProductUnitPage, exportProductUnitExcel } from "@/api/erp/productUnit";
import { createProductCategory, updateProductCategory, deleteProductCategory, getProductCategory, getProductCategoryPage, exportProductCategoryExcel } from "@/api/erp/productCategory";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "Product",
  components: {
      FileBusUpload,
      ZkScatteredInventoryForm
  },
  props: {
    defaultCategoryId: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      products: [],
      userInfo: {},
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // ERP 产品列表
      list: [],
      unitList: [], 
      categoryList: [], 
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        name: null,
        productName: null,
        barCode: null,
        categoryId: null,
        unitId: null,
        status: null,
        standard: null,
        remark: null,
        expiryDay: null,
        weight: null,
        purchasePrice: null,
        salePrice: null,
        minPrice: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [{ required: true, message: "产品名称不能为空", trigger: "blur" }],
        categoryId: [{ required: true, message: "产品分类编号不能为空", trigger: "blur" }],
        unitId: [{ required: true, message: "单位编号不能为空", trigger: "blur" }],
        status: [{ required: true, message: "产品状态不能为空", trigger: "blur" }],
        purchasePrice: [{ required: true, message: "产品价格不能为空", trigger: "blur" }],
        standard: [{ required: true, message: "产品规格不能为空", trigger: "blur" }],
        fishTypes: [{ required: true, message: "生物资产生物类别不能为空", trigger: "blur" }],
      },
      // 零星库存状态
      scatteredLoading: true,
      scatteredExportLoading: false,
      scatteredTotal: 0,
      scatteredList: [],
      scatteredCheckedIds: [],
      scatteredQueryParams: {
        pageNo: 1,
        pageSize: 10,
        productId: null,
        productName: null,
        categoryId: null
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    if (this.defaultCategoryId) {
      this.queryParams.categoryId = this.defaultCategoryId;
    }
    this.getList();
    this.getScatteredList();
    this.get_unitList();
    this.get_categoryList();
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
    getList() {
      this.loading = true;
      // 执行查询
      getProductPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 查询零星库存列表 */
    async getScatteredList() {
      try {
        this.scatteredLoading = true;
        const res = await ZkScatteredInventoryApi.getZkScatteredInventoryPage(this.scatteredQueryParams);
        this.scatteredList = res.data.list;
        this.scatteredTotal = res.data.total;
      } finally {
        this.scatteredLoading = false;
      }
    },
    /** 零星库存操作方法 */
    openScatteredForm(id) {
      this.$refs["scatteredFormRef"].open(id);
    },
    async handleScatteredDelete(row) {
      const id = row.id;
      await this.$modal.confirm('是否确认删除零星库存编号为"' + id + '"的数据项?');
      try {
        await ZkScatteredInventoryApi.deleteZkScatteredInventory(id);
        await this.getScatteredList();
        this.$modal.msgSuccess("删除成功");
      } catch (e) {}
    },
    async handleScatteredDeleteBatch() {
      await this.$modal.confirm('是否确认删除?');
      try {
        await ZkScatteredInventoryApi.deleteZkScatteredInventoryList(this.scatteredCheckedIds);
        this.scatteredCheckedIds = [];
        await this.getScatteredList();
        this.$modal.msgSuccess("删除成功");
      } catch (e) {}
    },
    handleScatteredRowCheckboxChange(records) {
      this.scatteredCheckedIds = records.map((item) => item.id);
    },
    async handleScatteredExport() {
      await this.$modal.confirm('是否确认导出所有零星库存数据项?');
      try {
        this.scatteredExportLoading = true;
        const data = await ZkScatteredInventoryApi.exportZkScatteredInventoryExcel(this.scatteredQueryParams);
        this.$download.excel(data, '零星库存.xls');
      } catch (e) {
      } finally {
        this.scatteredExportLoading = false;
      }
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
        name: undefined,
        barCode: undefined,
        categoryId: this.defaultCategoryId || undefined,
        unitId: undefined,
        status: 0,
        standard: undefined,
        remark: undefined,
        expiryDay: undefined,
        weight: undefined,
        purchasePrice: undefined,
        salePrice: undefined,
        minPrice: undefined,
        productNo: undefined,
      };
      this.resetForm("form");
    },
    update_categoryId(currId){
      let item = null;
      this.categoryList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.categoryName = item.name;
      this.form.categoryId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    get_categoryList() {
      this.categoryList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getProductCategoryPage(params).then(response => {
        this.categoryList.push(...response.data)
        // this.categoryList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        console.log(`getProductCategoryPage================>${JSON.stringify(this.categoryList)}`);
      })
    },
    update_unitId(currId){
      let item = null;
      this.unitList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.unitName = item.name;
      this.form.unitId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    get_unitList() {
      this.unitList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getProductUnitPage(params).then(response => {
        this.unitList.push(...response.data.list)
        // this.unitList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log(`getProductUnitPage================>${JSON.stringify(this.unitList)}`);
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.scatteredQueryParams.pageNo = 1;
      // 同步搜索条件
      this.scatteredQueryParams.productId = this.queryParams.productId;
      this.scatteredQueryParams.productName = this.queryParams.productName;
      this.scatteredQueryParams.categoryId = this.queryParams.categoryId;
      this.getList();
      this.getScatteredList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.scatteredQueryParams.productId = null;
      this.scatteredQueryParams.productName = null;
      this.scatteredQueryParams.categoryId = null;
      this.handleQuery();
    },
    /** 重新生成条码 */
    regenBarCode() {
      this.form.barCode = 'P' + new Date().getTime();
      this.$forceUpdate(); // 强制刷新，确保输入框显示更新
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加ERP 产品";
      this.form.fid = getUUID().replaceAll('-', '');
      this.form.barCode = 'P' + new Date().getTime(); // 自动生成条码
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getProduct(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改ERP 产品";
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
        this.form.productNo = this.form.barCode;
        if(!this.form.tenantId) this.form.tenantId = this.userInfo.tenantId;
        if(!this.form.deptId) this.form.deptId = this.userInfo.deptId;
        if(!this.form.userId) this.form.userId = this.userInfo.userId;
        
        if (this.form.id != null) {
          updateProduct(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          }).catch(err => {
            // 如果条码重复，重新生成并提示
            if (err.code === 1030500002) {
              this.form.barCode = 'P' + new Date().getTime();
              this.$modal.msgError("产品条码重复，已自动重新生成，请重新提交");
            }
          });
          return;
        }
        // 添加的提交
        createProduct(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        }).catch(err => {
          // 如果条码重复，重新生成并提示
          if (err.code === 1030500002) {
            this.form.barCode = 'P' + new Date().getTime();
            this.$modal.msgError("产品条码重复，已自动重新生成，请重新提交");
          }
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除ERP 产品编号为"' + id + '"的数据项?').then(function() {
          return deleteProduct(id);
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
      this.$modal.confirm('是否确认导出所有ERP 产品数据项?').then(() => {
          this.exportLoading = true;
          return exportProductExcel(params);
        }).then(response => {
          this.$download.excel(response, 'ERP 产品.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.barcode-input ::v-deep .el-input-group__append {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  border-left: 0 !important;
}
.barcode-input ::v-deep .el-input-group__append .el-button {
  color: #606266 !important;
}
.barcode-input ::v-deep .el-input-group__append .el-button:hover {
  color: #409EFF !important;
}
</style>
