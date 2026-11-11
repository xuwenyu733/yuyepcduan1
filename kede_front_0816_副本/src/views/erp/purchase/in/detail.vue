<template>
  <div v-if="visible">
    <el-dialog :title="title" :visible.sync="visible" width="98%" :close-on-click-modal="false" :before-close="son_close">
      <el-form ref="form" :model="orderForm" :rules="rules" label-width="100px" size="small">
        <!-- 基础信息 -->
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="入库单号"><el-input disabled placeholder="保存时自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="入库时间" prop="inTime">
              <el-date-picker v-model="orderForm.inTime" type="datetime" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- <el-form-item label="关联订单">
              <el-input v-model="orderForm.order" placeholder="订单号" />
              <el-button type="text" @click="selectOrder">选择</el-button>
            </el-form-item> -->
            <el-form-item label="关联订单" prop="purchaseId">
              <el-select style="width:100%;" v-model="orderForm.purchaseId" placeholder="请选择关联订单" filterable clearable
                @change="updatePurchase">
                <el-option v-for="(item, index) in list" :key="item.id + '__' + index"
                  :label="`${item.no}${item.productNames ? `(${item.productNames})` : ''}`" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="orderForm.supplierId" placeholder="请选择供应商" clearable filterable style="width:100%">
                <el-option v-for="s in suppliers" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="orderForm.remark" type="textarea" rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="附件" v-if="false">
              <el-upload :show-file-list="false">
                <el-button type="primary" icon="el-icon-upload2">选取文件</el-button>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 产品清单 .toFixed(2) -->
        <div style="margin:20px 0">
          <h3 style="color:#1890ff">入库产品清单</h3>
          <el-table :data="items" border size="small">
            <!-- <el-table-column type="index" label="序号" width="60" /> -->
            <el-table-column label="仓库">
              <template slot-scope="scope">
                <el-select v-model="scope.row.warehouseId" style="width:100%">
                  <el-option v-for="w in warehouses" :key="w.id" :label="w.name" :value="w.id" />
                </el-select>
              </template>
            </el-table-column>
            <!-- <el-table-column label="产品" width="160">
              <el-select v-model="row.productId" @change="changeProduct($index)">
                <el-option
                  v-for="p in products" :key="p.id" :label="p.productName" :value="p.id" />
              </el-select>
            </el-table-column> -->
            <el-table-column label="商品" align="center" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.productName }}</span>
              </template>
            </el-table-column>
            <!-- <el-table-column label="规格" width="80" align="center" prop="standard" /> -->
            <el-table-column prop="standard" label="规格">
              <template slot-scope="scope">
                <el-input v-model="scope.row.standard" />
              </template>
            </el-table-column>
            <!-- <el-table-column label="库存" width="80" align="center">{{ row.stock || 0 }}</el-table-column> -->
            <el-table-column label="库存" width="80" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.stockCount || 0 }}</span>
              </template>
            </el-table-column>
            <!--  width="200" -->
            <el-table-column label="数量" align="center">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.count" :min="0.001" :precision="3" @change="calc(scope.row)" />
              </template>
            </el-table-column>
            <!-- <el-table-column label="单位" width="80" align="center">{{ scope.row.productUnitName || '暂无' }}</el-table-column> -->
            <el-table-column label="单位" width="80" align="center" prop="productUnitName" />
            <el-table-column label="单价" align="center">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.productPrice" :min="0.001" :precision="3" @change="calc(scope.row)" />
              </template>
            </el-table-column>
            <el-table-column label="金额" align="center" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.totalProductPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="giftNum" label="赠送数量" align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.giftNum ? scope.row.giftNum : 0 }}</span>
                <!-- <el-input-number v-model="scope.row.giftNum"  /> -->
              </template>
            </el-table-column>
            <el-table-column label="税率(%)" align="center" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.taxPercent }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额" align="center" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.taxPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额合计" align="center" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.totalPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60">
              <el-button type="text" icon="el-icon-delete" @click="delProduct($index)" :disabled="items.length <= 1" />
            </el-table-column>
          </el-table>
          <!-- <el-button type="primary" icon="el-icon-plus" size="small" @click="addProduct"
            style="margin-top:10px">添加产品</el-button> -->
          <!-- 合计行 -->
          <el-table :data="[{ count, totalProductPrice, taxPrice, totalPrice }]" border size="small" :show-header="false"
            class="total-table">
            <el-table-column label="合计" width="180" align="center">
              <template slot-scope="scope">
                <span class="total-label">合计</span>
              </template>
            </el-table-column>
            <el-table-column label="数量" align="right">
              <template slot-scope="scope">
                <span>数量：{{ scope.row.count }}</span>
              </template>
            </el-table-column>
            <el-table-column label="金额" align="right">
              <template slot-scope="scope">
                <span>金额：{{ scope.row.totalProductPrice ? scope.row.totalProductPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额" align="right">
              <template slot-scope="scope">
                <span>税额：{{ scope.row.taxPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额合计" align="right">
              <template slot-scope="scope">
                <span>税额合计：{{ scope.row.totalPrice }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 费用信息 -->
        <el-row :gutter="20">
          <!-- <el-col :span="6">
            <el-form-item label="优惠率(%)">
              <el-input-number v-model="orderForm.discount" :min="0" :max="100" :precision="2" @change="calcTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="付款优惠">
              <el-input v-model="orderForm.discountAmt"
                disabled />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优惠后金额">
              <el-input v-model="orderForm.finalAmt" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="其它费用">
              <el-input-number v-model="orderForm.otherFee" :min="0" :precision="2" @change="calcTotal" />
            </el-form-item>
          </el-col> -->
          <el-col :span="6">
            <el-form-item label="结算账户" prop="accountId">
              <el-select v-model="orderForm.accountId" placeholder="请选择结算账户" clearable filterable style="width:100%">
                <el-option v-for="a in accounts" :key="a.id" :label="a.name" :value="parseInt(a.id)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="应付金额" prop="totalPrice">
              <el-input v-model="orderForm.totalPrice" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="son_close">取消</el-button>
        <el-button type="primary" @click="submit" :disabled="orderForm.status === 20">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createPurchaseOrder, updatePurchaseOrder, deletePurchaseOrder, getPurchaseOrder, getPurchaseOrderPage, exportPurchaseOrderExcel, updatePurchaseOrderStatus } from "@/api/erp/purchaseOrder";
import { createWarehouse, updateWarehouse, deleteWarehouse, getWarehouse, getWarehousePage, exportWarehouseExcel } from "@/api/erp/warehouse";
import { createSupplier, updateSupplier, deleteSupplier, getSupplier, getSupplierPage, exportSupplierExcel } from "@/api/erp/supplier";
import { createProduct, updateProduct, deleteProduct, getProduct, getProductPage, exportProductExcel } from "@/api/erp/product";
import { createAccount, updateAccount, deleteAccount, getAccount, getAccountPage, exportAccountExcel } from "@/api/erp/account";
import { createPurchaseIn, updatePurchaseIn, deletePurchaseIn, getPurchaseIn, getPurchaseInPage, exportPurchaseInExcel, updatePurchaseInStatus } from "@/api/erp/purchaseIn";
import { UserInfoKey } from "@/utils/constants";

export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      userInfo: {},
      title: '新增入库单',
      row: {},
      list: [], // erp_purchase_order 可入库订单
      purchaseItem: {}, // erp_purchase_order 当前订单
      orderForm: {
        "supplierId": undefined,
        "accountId": undefined,
        "inTime": new Date().getTime(),
        "remark": "",
        "fileUrl": null,
        "discountPercent": 0,
        "discountPrice": 0,
        "totalPrice": 0,
        "otherPrice": 0,
        "items": [
        ],
        "orderId": undefined,
        "discountRate": 0,
        "orderNo": ""
      },
      // { warehouse: '', product: '', stock: 0, qty: 1, price: 0, amount: 0, tax: 0, total: 0 }
      items: [],
      suppliers: [],
      warehouses: [],
      products: [],
      accounts: [],
      rules: { 
          time: [{ required: true, message: '请选择入库时间' }], supplier: [{ required: true, message: '请选择供应商' }],
            purchaseId: [{ required: true, message: "关联订单不能为空", trigger: "blur" }],
            supplierId: [{ required: true, message: "供应商不能为空", trigger: "blur" }],
            saleUserId: [{ required: true, message: "销售不能为空", trigger: "blur" }],
            accountId: [{ required: true, message: "结算账户不能为空", trigger: "blur" }],
        }
    }
  },
  watch: {
    visible(val) {
      this.reset();
    }
  },
  computed: {
    // 计算总数量
    count() {
      // return this.items.reduce((sum, item) => sum + Number(item.count), 0);
      return this.items.reduce((sum, item) => this.my_common.computer(sum, item.count, 0, 2), 0);
    },
    // 计算总金额
    totalProductPrice() {
      // return this.items.reduce((sum, item) => sum + Number(item.totalProductPrice), 0);
      return this.items.reduce((sum, item) => this.my_common.computer(sum, item.totalProductPrice, 0, 2), 0);
      // return this.items.reduce((sum, item) => sum + Number(item.productPrice)*Number(item.count), 0);
    },
    // 计算总税额
    taxPrice() {
      // return this.items.reduce((sum, item) => sum + Number(item.taxPrice), 0);
      return this.items.reduce((sum, item) => this.my_common.computer(sum, item.taxPrice, 0, 2), 0);
    },
    // 计算总税额合计
    totalPrice() {
      // return this.items.reduce((sum, item) => sum + Number(item.totalPrice), 0);
      return this.items.reduce((sum, item) => this.my_common.computer(sum, item.totalPrice, 0, 2), 0);
    }
  },
  async mounted() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    await this.get_products();
    await this.get_suppliers();
    await this.get_accounts();
    await this.get_warehouseList();
    this.getEnableList();
  },
  methods: {
    reset() {
      this.orderForm = {
        "supplierId": undefined,
        "accountId": undefined,
        "inTime": new Date().getTime(),
        "remark": "",
        "fileUrl": null,
        "discountPercent": 0,
        "discountPrice": 0,
        "totalPrice": 0,
        "otherPrice": 0,
        "items": [
        ],
        "orderId": undefined,
        "discountRate": 0,
        "orderNo": ""
      };
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id;
      getPurchaseIn(id).then(response => {
        this.orderForm = response.data;
        this.list.push({
          id: this.orderForm.purchaseId,
          no: this.orderForm.orderNo
        })
        this.items = this.orderForm.items;
        this.orderForm.items.forEach((obj, index) => {
          obj.purchasePrice = obj.productPrice;
          this.calculateAmount(index);
          this.update_productId(obj.productId, obj);
        })

        console.log(`getPurchaseOrder============>form:${JSON.stringify(this.form)}`);
        this.title = "修改ERP 采购入库";
        // if (!this.orderForm.fid) {
        //   this.orderForm.fid = getUUID().replaceAll("-", "");
        // }
      });
    },
    son_close() {
      this.reset();
      this.$emit('son_close');
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
      this.orderForm.warehouseName = item.warehouseName;
      this.orderForm.warehouseId = item.id;
      console.log("===========>" + JSON.stringify(this.form));
    },
    get_warehouseList() {
      this.warehouses = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getWarehousePage(params).then(response => {
        this.warehouses.push(...response.data.list);
        // this.warehouses.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },

    update_accountId(currId) {
      let item = null;
      this.accounts.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.orderForm.accountName = item.accountName;
      this.orderForm.accountId = item.id;
      console.log("===========>" + JSON.stringify(this.form));
    },
    get_accounts() {
      this.accounts = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        getAccountPage(params).then(response => {
          this.accounts.push(...response.data.list);
          resolve(this.accounts);
          // this.accounts.forEach(obj => {
          //   let tempStr = `${obj.deptName}-${obj.nickname}`;
          //   obj.nickname = tempStr;
          //   console.log("===========>"+JSON.stringify(obj));
          // })
          // console.log("getAccountPage===========>"+JSON.stringify(this.accounts));
        })
      });
      return promise;
    },

    update_productId(currId, row){
      let item = null;
      this.products.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      console.log(`update_productId=======>row:${JSON.stringify(row)}`);
      row.productName = item.productName;
      row.productId = item.id;
      if(!row.standard) row.standard = item.standard;
      if(!row.productPrice) row.productPrice = item.purchasePrice;
      console.log("===========>"+JSON.stringify(row));
    },
    get_products() {
      this.products = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        getProductPage(params).then(response => {
          this.products.push(...response.data.list);
          this.products.forEach(obj => {
            obj.taxPercent = 0;
            if (!obj.stockCount) obj.stockCount = 0;
          })
          resolve(this.products);
        })
      });
      return promise;
    },

    update_supplierId(currId) {
      let item = null;
      this.suppliers.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.orderForm.supplierName = item.supplierName;
      this.orderForm.supplierId = item.id;
      console.log("===========>" + JSON.stringify(this.form));
    },
    get_suppliers() {
      this.suppliers = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        getSupplierPage(params).then(response => {
          this.suppliers.push(...response.data.list);
          resolve(this.suppliers);
          // this.suppliers.forEach(obj => {
          //   let tempStr = `${obj.deptName}-${obj.nickname}`;
          //   obj.nickname = tempStr;
          //   console.log("===========>"+JSON.stringify(obj));
          // })
          // console.log("getSupplierPage===========>"+JSON.stringify(this.suppliers));
        })
      });
      return promise;
    },

    addProduct() { this.products.push({ warehouse: '', product: '', stock: 0, qty: 1, price: 0, amount: 0, tax: 0, total: 0 }) },
    delProduct(i) { this.products.splice(i, 1); this.calcTotal() },
    changeProduct(i) {
      const p = this.productsData.find(item => item.id === this.products[i].product);
      console.log(`===>i:${i},p:${JSON.stringify(p)}`);
      if (p) { this.products[i].productPrice = p.productPrice; this.products[i].tax = p.tax; this.products[i].stock = p.stock; this.calc(i) }
    },
    
    // 计算金额
    calculateAmount(index) {
      const row = this.items[index];
      console.log(`calculateAmount index:${index}=========================>${JSON.stringify(row)}`);
      row.totalProductPrice = this.my_common.computer(row.count, row.purchasePrice, 2, 2);
      this.$set(this.items, index, row);
      this.calculateTax(index);
    },
    // 计算税额
    calculateTax(index) {
      const row = this.items[index];
      // row.taxPrice = row.totalProductPrice * (row.taxPercent / 100);
      let persent = this.my_common.computer(row.taxPercent, 100, 3, 2);
      row.taxPrice = this.my_common.computer(row.totalProductPrice, persent, 2, 2);
      // row.totalPrice = row.totalProductPrice + row.taxPrice;
      row.totalPrice = this.my_common.computer(row.totalProductPrice, row.taxPrice, 0, 2);
      this.$set(this.items, index, row);
      this.calculateDiscount();
    },
    // 计算优惠
    calculateDiscount() {
      let total = 0;
      this.items.forEach(item => {
        total = this.my_common.computer(total, item.totalPrice, 0, 2);
      });
      console.log(`updatePurchase products====333====>total:${JSON.stringify(total)}`);
      // this.orderForm.discountAmount = total * (this.orderForm.discountRate / 100);
      let discountRate = this.orderForm.discountRate;
      if(!discountRate) discountRate = 0;
      let persent = this.my_common.computer(discountRate, 100, 3, 2);
      this.orderForm.discountAmount = this.my_common.computer(persent, total, 2, 2);
      // this.orderForm.totalPrice = total - this.orderForm.discountAmount;
      this.orderForm.totalPrice = this.my_common.computer(total, this.orderForm.discountAmount, 1, 2);
      
    },

    calc(p) {
      // const p = this.items[i];
      p.totalPrice = p.count * p.productPrice;
      p.totalCount = p.count;
      p.totalProductPrice = p.totalPrice;
      // p.totalPrice = p.amount * (1 + p.tax / 100);
      this.calcTotal();
    },
    calcTotal() {
      const sum = this.items.reduce((t, p) => t + p.totalPrice, 0);
      this.orderForm.totalProductPrice = sum;
      // this.orderForm.discountAmt = sum * (this.orderForm.discount / 100);
      // this.orderForm.finalAmt = sum - this.orderForm.discountAmt;
      // this.orderForm.totalProductPrice = this.orderForm.finalAmt + this.orderForm.otherFee;
    },
    selectOrder() { this.$message.info('选择订单功能待实现') },
    submit() {
      // this.orderForm.items = this.items;
      // console.log(`=================>form:${JSON.stringify(this.form)}`);
      // return;
      this.$refs.form.validate(v => {
        if(!v) return; 
        this.orderForm.items = this.items;
        let msg = '';
        this.orderForm.items.forEach(ii => {
          if(!ii.warehouseId) {
            msg = `${ii.productName}的仓库不能为空！`;
            return;
          }
        })
        if(msg) {
          this.$modal.msgWarning(msg);
          return;
        }
        // console.log(`=================>form:${JSON.stringify(this.orderForm)}`);
        // return;
        if(!this.orderForm.tenantId) this.orderForm.tenantId = this.userInfo.tenantId;
        if(!this.orderForm.deptId) this.orderForm.deptId = this.userInfo.deptId;
        if(!this.orderForm.userId) this.orderForm.userId = this.userInfo.userId;
        this.orderForm.items.forEach(item => {
          if(!item.tenantId) item.tenantId = this.userInfo.tenantId;
          if(!item.deptId) item.deptId = this.userInfo.deptId;
          if(!item.userId) item.userId = this.userInfo.userId;
        })
        this.$emit('submit', this.orderForm);
        // this.visible = false
      }) 
      // this.$emit('submit', this.orderForm);
      // this.$refs.orderForm.validate(v => v && (this.$emit('submit', this.form), this.visible = false)) 
    },

    getEnableList() {
      const queryParams = {
        pageNo: 1,
        pageSize: 10,
        no: undefined,
        productId: undefined,
        orderTime: [],
        inEnable: true
      };
      // 执行查询
      getPurchaseOrderPage(queryParams).then(response => {
        this.list = response.data.list;
        // console.log(`==================>list:${JSON.stringify(this.list)}`);
      });
    },
    updatePurchase(id) {
      let purchaseItem = null;
      this.list.forEach(obj => {
        if (obj.id == id) {
          purchaseItem = obj;
          return;
        }
      })
      if (purchaseItem == null) return;
      this.purchaseItem = purchaseItem;
      purchaseItem.items.forEach(obj => {
        obj.purchasePrice = obj.productPrice;
      })
      this.items = purchaseItem.items;
      this.items.forEach((obj, index) => {
        obj.orderItemId = obj.id;
        this.calculateAmount(index);
        obj.id = null;
        this.update_productId(obj.productId, obj);
      })
      this.orderForm.orderId = purchaseItem.id;
      this.orderForm.supplierId = purchaseItem.supplierId;
      this.orderForm.orderNo = purchaseItem.orderNo;
      this.filterItems();
      // console.log(`updatePurchase products========>items:${JSON.stringify(this.items)}`);
      // console.log(`updatePurchase products========>form:${JSON.stringify(this.orderForm)}`);
    },
    filterItems() {
      this.items.forEach(ii => {
        let productItem = null;
        this.products.forEach(item => {
          if (ii.productId == item.id) {
            productItem = item;
            return;
          }
        })
        if (productItem != null) {
          ii.categoryId = productItem.categoryId;
        }
      })
    }
  }
}
</script>

<style scoped>
.el-form-item {
  margin-bottom: 15px;
}

.el-dialog__body {
  padding: 20px;
}
</style>
