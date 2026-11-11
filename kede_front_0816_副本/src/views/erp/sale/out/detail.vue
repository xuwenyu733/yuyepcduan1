<template>
  <div v-if="visible">
    <el-dialog :title="title" :visible.sync="visible" width="90%" :close-on-click-modal="false"
      :before-close="son_close">
      <el-form ref="form" :model="orderForm" :rules="rules" label-width="100px" size="small">
        <!-- 基础信息 -->
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="出库单号"><el-input disabled placeholder="保存时自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="出库时间" prop="outTime">
              <el-date-picker v-model="orderForm.outTime" type="datetime" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联订单" prop="orderNo">
              <el-select style="width:100%;" v-model="orderForm.orderNo" placeholder="请选择关联订单" filterable clearable
                @change="updateSale">
                <el-option v-for="(item, index) in list" :key="item.id + '__' + index"
                  :label="`${item.no}${item.productNames ? `(${item.productNames})` : ''}`" :value="item.no"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="orderForm.customerId" style="width:100%">
                <el-option v-for="s in customers" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="负责人" prop="saleUserId">
              <el-select v-model="orderForm.saleUserId" style="width:100%">
                <el-option v-for="(s, index) in userList1" :key="`${s.id}__sale__${index}`" :label="s.nickname" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="orderForm.remark" type="textarea" rows="1" />
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
          <h3 style="color:#1890ff">出库产品清单</h3>
          <el-table :data="items" border size="small">
            <!-- <el-table-column type="index" label="序号" width="60" /> -->
            <el-table-column label="仓库">
              <template slot-scope="scope">
                <el-select v-model="scope.row.warehouseId" style="width:100%">
                  <el-option v-for="w in warehouses" :key="w.id" :label="w.name" :value="w.id" />
                </el-select>
                <!-- <el-form-item label="" prop="warehouseId" label-width="0">
                </el-form-item> -->
              </template>
            </el-table-column>
            <!-- <el-table-column label="产品" width="160">
              <el-select v-model="row.productId" @change="changeProduct($index)">
                <el-option
                  v-for="p in products" :key="p.id" :label="p.productName" :value="p.id" />
              </el-select>
            </el-table-column> -->
            <el-table-column label="商品" width="180" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.productName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="规格" width="90" align="center" prop="standard" />
            <el-table-column label="库存" width="80" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.stockCount || 0 }}</span>
              </template>
            </el-table-column>
            <!-- <el-table-column label="库存" width="80" align="center">{{ row.stock || 0 }}</el-table-column> -->
            <!-- <el-table-column label="商品名称" width="80" align="center">{{ row.productName || 0 }}</el-table-column> -->
            <el-table-column label="数量" width="280" align="center">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.count" :min="0.001" :precision="3"
                  @change="calculateAmount(scope.$index)" />
              </template>
            </el-table-column>
            <el-table-column label="单位" width="80" align="center" prop="productUnitName" />
            <el-table-column label="单价" align="center">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.productPrice" :min="0.001" :precision="3"
                  @change="calculateAmount(scope.$index)" />
              </template>
            </el-table-column>
            <el-table-column label="金额" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.totalProductPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税率(%)" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.taxPercent }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.taxPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额合计" align="center">
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
          <el-table :data="[{ count, totalProductPrice, taxPrice, totalPrice }]" border size="small"
            :show-header="false" class="total-table">
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
                <span>金额：{{ scope.row.totalPrice ? scope.row.totalPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额" align="right">
              <template slot-scope="scope">
                <span>税额：{{ scope.row.taxPrice?scope.row.taxPrice:0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额合计" align="right">
              <template slot-scope="scope">
                <span>税额合计：{{ scope.row.totalProductPrice?scope.row.totalProductPrice:0 }}</span>
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
              <el-select v-model="orderForm.accountId" style="width:100%">
                <el-option v-for="a in accounts" :key="a.id" :label="a.name" :value="a.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="应收金额">
              <el-input v-model="orderForm.totalProductPrice" disabled />
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
import { createSaleOrder, updateSaleOrder, deleteSaleOrder, getSaleOrder, getSaleOrderPage, exportSaleOrderExcel, updateSaleOrderStatus } from "@/api/erp/saleOrder";
import { createWarehouse, updateWarehouse, deleteWarehouse, getWarehouse, getWarehousePage, exportWarehouseExcel } from "@/api/erp/warehouse";
import { createSupplier, updateSupplier, deleteSupplier, getSupplier, getSupplierPage, exportSupplierExcel } from "@/api/erp/supplier";
import { createProduct, updateProduct, deleteProduct, getProduct, getProductPage, exportProductExcel } from "@/api/erp/product";
import { createAccount, updateAccount, deleteAccount, getAccount, getAccountPage, exportAccountExcel } from "@/api/erp/account";
import { createCustomer, updateCustomer, deleteCustomer, getCustomer, getCustomerPage, exportCustomerExcel } from "@/api/erp/customer";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { createSaleOut, updateSaleOut, deleteSaleOut, getSaleOut, getSaleOutPage, exportSaleOutExcel, updateSaleOutStatus } from "@/api/erp/saleOut";
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
      title: '新增出库单',
      row: {},
      list: [], // erp_purchase_order 可出库订单
      saleItem: {}, // erp_sale_order 当前订单
      orderForm: {
        customerId: undefined,
        accountId: undefined,
        saleUserId: undefined,
        "outTime": new Date().getTime(),
        "remark": "",
        "fileUrl": null,
        "discountPercent": 0,
        "discountPrice": 0,
        "totalPrice": 0,
        "otherPrice": 0,
        "items": [
        ],
        "orderId": 0,
        "orderNo": ""
      },
      // { warehouse: '', product: '', stock: 0, qty: 1, price: 0, amount: 0, tax: 0, total: 0 }
      items: [],
      suppliers: [],
      warehouses: [],
      userList1: [],
      products: [],
      customers: [],
      accounts: [],
      rules: {
        time: [{ required: true, message: '请选择出库时间' }],
        supplier: [{ required: true, message: '请选择销售' }],
        orderNo: [{ required: true, message: "关联订单不能为空", trigger: "blur" }],
        customerId: [{ required: true, message: "客户不能为空", trigger: "blur" }],
        saleUserId: [{ required: true, message: "销售不能为空", trigger: "blur" }],
        accountId: [{ required: true, message: "结算账户不能为空", trigger: "blur" }],
        // warehouseId: [{ required: true, message: "仓库不能为空", trigger: "blur" }],
      }
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
    await this.get_customers();
    await this.get_accounts();
    await this.getUserList();
    await this.get_warehouseList();
    console.log(`============>mounted`);
    this.getEnableList();
  },
  methods: {
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id;
      getSaleOut(id).then(response => {
        this.orderForm = { ...this.orderForm, ...response.data };
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
        this.title = "修改ERP 销售出库";
        // console.log(`getSaleOut============>form:${JSON.stringify(this.form)}`);
        // if (!this.orderForm.fid) {
        //   this.orderForm.fid = getUUID().replaceAll("-", "");
        // }
      });
    },
    son_close() {
      this.$emit('son_close');
    },
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if (obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.orderForm.userId = item.id;
      this.orderForm.saleUserId = item.id;
      this.orderForm.deptId = item.deptId;
      this.orderForm.userName = item.nickname;
      // console.log("===========>"+JSON.stringify(this.form));
    },
    getUserList() {
      this.userList1 = []
      let promise = new Promise((resolve, reject) => {
        listSimpleUsersByRole(KE_DE_ROLES).then(response => {
          this.userList1.push(...response.data)
          this.userList1.forEach(obj => {
            let tempStr = `${obj.deptName}-${obj.nickname}`;
            obj.nickname = tempStr;
            // console.log("===========>"+JSON.stringify(obj));
          })
          resolve(this.userList1);
        })
      });
      return promise;
    },

    update_customerId(currId) {
      let item = null;
      this.customers.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.orderForm.customerName = item.name;
      this.orderForm.customerId = item.id;
      // console.log("===========>"+JSON.stringify(this.form));
    },
    get_customers() {
      this.customers = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        getCustomerPage(params).then(response => {
          this.customers.push(...response.data.list)
          resolve(this.customers);
          // this.customers.forEach(obj => {
          //   let tempStr = `${obj.deptName}-${obj.nickname}`;
          //   obj.nickname = tempStr;
          //   console.log("===========>"+JSON.stringify(obj));
          // })
        })
      });
      return promise;
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
          // this.warehouses.forEach(obj => {
          //   let tempStr = `${obj.deptName}-${obj.nickname}`;
          //   obj.nickname = tempStr;
          //   console.log("===========>"+JSON.stringify(obj));
          // })
        })
      });
      return promise;
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

    update_productId(currId, row) {
      let item = null;
      this.products.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      row.productName = item.productName;
      row.productId = item.id;
      row.standard = item.standard;
      console.log("===========>" + JSON.stringify(row));
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
      row.totalPrice = this.my_common.computer(row.count, row.purchasePrice, 2, 2);
      this.$set(this.items, index, row);
      this.calculateTax(index);
    },
    // 计算税额
    calculateTax(index) {
      const row = this.items[index];
      // row.taxPrice = row.totalProductPrice * (row.taxPercent / 100);
      let persent = this.my_common.computer(row.taxPercent, 100, 3, 2);
      row.taxPrice = this.my_common.computer(row.totalPrice, persent, 2, 2);
      // row.totalPrice = row.totalProductPrice + row.taxPrice;
      row.totalProductPrice = this.my_common.computer(row.totalPrice, row.taxPrice, 0, 2);
      this.$set(this.items, index, row);
      this.calculateDiscount();
    },
    // 计算优惠
    calculateDiscount() {
      let total = 0;
      this.items.forEach(item => {
        total = this.my_common.computer(total, item.totalProductPrice, 0, 2);
      });
      // this.orderForm.discountAmount = total * (this.orderForm.discountRate / 100);
      let discountRate = this.orderForm.discountRate;
      if (!discountRate) discountRate = 0;
      let persent = this.my_common.computer(discountRate, 100, 3, 2);
      this.orderForm.discountAmount = this.my_common.computer(persent, total, 2, 2);
      // this.orderForm.totalPrice = total - this.orderForm.discountAmount;
      this.orderForm.totalProductPrice = this.my_common.computer(total, this.orderForm.discountAmount, 1, 2);
    },

    calc(p) {
      // const p = this.items[i];
      p.totalPrice = p.count * p.productPrice;
      p.totalCount = p.count;
      p.totalProductPrice = p.totalPrice;
      // console.log("calc===========>"+JSON.stringify(p));
      // p.totalPrice = p.amount * (1 + p.tax / 100);
      this.calcTotal();
    },
    calcTotal() {
      this.orderForm.discount = 0;
      this.orderForm.otherFee = 0;
      const sum = this.items.reduce((t, p) => t + p.totalProductPrice, 0);
      this.orderForm.discountAmt = sum * (this.orderForm.discount / 100);
      this.orderForm.finalAmt = sum - this.orderForm.discountAmt;
      this.orderForm.totalPrice = this.orderForm.finalAmt + this.orderForm.otherFee;
      console.log(`calcTotal============>items:${JSON.stringify(this.items)}`);
      // console.log(`calcTotal============>sum:${sum}`);
    },
    selectOrder() { this.$message.info('选择订单功能待实现') },
    submit() {
      this.$refs.form.validate(v => {
        if (!v) return;
        this.orderForm.items = this.items;
        let msg = '';
        this.orderForm.items.forEach(ii => {
          if (!ii.warehouseId) {
            msg = `${ii.productName}的仓库不能为空！`;
            return;
          }
        })
        if (msg) {
          this.$modal.msgWarning(msg);
          return;
        }
          if(!this.orderForm.tenantId) this.orderForm.tenantId = this.userInfo.tenantId;
          if(!this.orderForm.deptId) this.orderForm.deptId = this.userInfo.deptId;
          if(!this.orderForm.userId) this.orderForm.userId = this.userInfo.userId;
          this.orderForm.items.forEach(item => {
            if(!item.tenantId) item.tenantId = this.userInfo.tenantId;
            if(!item.deptId) item.deptId = this.userInfo.deptId;
            if(!item.userId) item.userId = this.userInfo.userId;
          })
        // console.log(`=================>form:${JSON.stringify(this.orderForm)}`);
        // return;
        this.$emit('submit', this.orderForm);
        // this.visible = false
      })
    },

    getEnableList() {
      // console.log(`============>getEnableList`);
      const queryParams = {
        pageNo: 1,
        pageSize: 10,
        no: undefined,
        productId: undefined,
        orderTime: [],
        outEnable: true
      };
      // 执行查询
      getSaleOrderPage(queryParams).then(response => {
        this.list = response.data.list;
        // console.log(`getSaleOrderPage==================>list:${JSON.stringify(this.list)}`);
      });
    },
    updateSale(no) {
      let saleItem = null;
      this.list.forEach(obj => {
        if (obj.no == no) {
          saleItem = obj;
          return;
        }
      })
      if (saleItem == null) return;
      this.saleItem = saleItem;
      saleItem.items.forEach(obj => {
        obj.purchasePrice = obj.productPrice;
      })
      this.orderForm.orderId = saleItem.id;
      this.orderForm.customerId = saleItem.customerId;
      this.orderForm.orderNo = saleItem.no;
      this.orderForm.saleUserId = saleItem.userId;
      this.items = saleItem.items;
      this.filterItems();
      console.log(`=============>filterItems:${JSON.stringify(saleItem.items)}`);
      this.items.forEach((obj, index) => {
        obj.orderItemId = obj.id;
        this.calculateAmount(index);
        this.update_productId(obj.productId, obj);
        obj.id = null;
      })
      // console.log(`updateSale products========>items:${JSON.stringify(this.items)}`);
      // console.log(`updateSale products========>form:${JSON.stringify(this.form)}`);
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
          // console.log(`filterItems productItem========>items:${JSON.stringify(productItem)}`);
          ii.categoryId = productItem.categoryId;
          if (!ii.productPrice) ii.productPrice = productItem.purchasePrice;
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
