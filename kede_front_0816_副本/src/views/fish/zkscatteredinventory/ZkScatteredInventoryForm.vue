<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <!-- <el-form-item label="商品id" prop="productId">
          <el-input v-model="formData.productId" placeholder="请输入商品id" />
        </el-form-item> -->
        <el-form-item label="商品" prop="productId">
          <el-select v-model="formData.productId" placeholder="请选择商品" clearable size="small" style="width: 100%">
            <el-option v-for="(item, index) in products" :key="item.id+'_p_'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="仓库id" prop="warehouseId">
          <el-input v-model="formData.warehouseId" placeholder="请输入仓库id" />
        </el-form-item> -->
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="formData.warehouseId" placeholder="请选择仓库" clearable size="small" style="width: 100%">
            <el-option v-for="(item, index) in warehouses" :key="item.id+'_w_'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="单位id" prop="unitId">
          <el-input v-model="formData.unitId" placeholder="请输入单位id" />
        </el-form-item> -->
        <el-form-item label="单位" prop="unitId">
          <el-select v-model="formData.unitId" placeholder="请选择单位" clearable size="small" style="width: 100%">
            <el-option v-for="(item, index) in unitList" :key="item.id+'_u_'+index" :label="item.name"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="剩余数量" prop="remainingQuantity">
          <el-input v-model="formData.remainingQuantity" placeholder="请输入剩余数量" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" placeholder="请输入备注" />
        </el-form-item>
        <!-- <el-form-item label="零星库存类型(1:手动出库;2:手动入库零星;)" prop="busType">
          <el-select v-model="formData.busType" placeholder="请选择零星库存类型(1:手动出库;2:手动入库零星;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="formData.deptId" placeholder="请输入所属部门" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkScatteredInventoryApi from '@/api/fish/zkscatteredinventory';
import {
  getProductPage
} from "@/api/erp/product";
import { createWarehouse, updateWarehouse, deleteWarehouse, getWarehouse, getWarehousePage, exportWarehouseExcel } from "@/api/erp/warehouse";
import { createProductUnit, updateProductUnit, deleteProductUnit, getProductUnit, getProductUnitPage, exportProductUnitExcel } from "@/api/erp/productUnit";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkScatteredInventoryForm",
  components: {
  },
  data() {
    return {
      userInfo: {},
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        productId: undefined,
        warehouseId: undefined,
        unitId: undefined,
        remainingQuantity: undefined,
        remark: undefined,
        busType: "2", // 零星库存类型(1:手动出库;2:手动入库零星;)
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        productId: [{ required: true, message: "商品不能为空", trigger: "blur" }],
        warehouseId: [{ required: true, message: "仓库不能为空", trigger: "blur" }],
        unitId: [{ required: true, message: "单位不能为空", trigger: "blur" }],
        remainingQuantity: [{ required: true, message: "剩余数量不能为空", trigger: "blur" }],
      },
      products: [],
      unitList: [], 
      warehouses: [],
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.get_products();
    this.get_unitList();
    this.get_warehouseList();
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
      })
    },

    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await ZkScatteredInventoryApi.getZkScatteredInventory(id);
          this.formData = res.data;
          this.title = "修改零星库存";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增零星库存";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        if(!this.formData.tenantId) this.formData.tenantId = this.userInfo.tenantId;
        if(!this.formData.userId) this.formData.userId = this.userInfo.userId;
        if(!this.formData.deptId) this.formData.deptId = this.userInfo.deptId;
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await ZkScatteredInventoryApi.updateZkScatteredInventory(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkScatteredInventoryApi.createZkScatteredInventory(data);
        this.$modal.msgSuccess("新增成功");
        this.dialogVisible = false;
        this.$emit('success');
      } finally {
        this.formLoading = false;
      }
    },
    /** 表单重置 */
    reset() {
      this.formData = {
        id: undefined,
        productId: undefined,
        warehouseId: undefined,
        unitId: undefined,
        remainingQuantity: undefined,
        remark: undefined,
        busType: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>