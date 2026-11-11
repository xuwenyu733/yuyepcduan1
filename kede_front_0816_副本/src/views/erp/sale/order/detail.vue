<template>
  <div v-if="dialogVisible">
    <el-dialog :title="title" :visible.sync="dialogVisible" width="90%" :close-on-click-modal="false"
      :destroy-on-close="true" class="order-dialog" :before-close="son_close">
      <!-- 订单基本信息区域 -->
      <el-form ref="orderForm" :model="orderForm" :rules="rules" label-width="100px" size="small">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="订单单号" prop="orderNo">
              <el-input v-model="orderForm.orderNo" placeholder="保存时自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="订单时间" prop="orderTime">
              <el-date-picker v-model="orderForm.orderTime" type="datetime" placeholder="选择订单时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="orderForm.customerId" placeholder="请选择客户" style="width: 100%">
                <el-option v-for="item in customers" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" v-if="false">
            <el-form-item label="附件">
              <el-upload class="upload-btn" :show-file-list="false" :before-upload="beforeUpload">
                <el-button type="primary" icon="el-icon-upload2">选取文件</el-button>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="orderForm.remark" placeholder="请输入备注" type="textarea" rows="2" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 订单产品清单区域 -->
        <div class="order-table-container">
          <h3 class="table-title">订单产品清单</h3>
          <el-table :data="orderProducts" border size="small" class="order-table">
            <!-- <el-table-column type="index" label="序号" width="60" align="center" /> -->
            <el-table-column prop="productId" label="产品名称">
              <template slot-scope="scope">
                <el-select v-model="scope.row.productId" placeholder="请选择产品" style="width: 100%"
                  @change="handleProductChange(scope.row, scope.$index)">
                  <el-option v-for="item in products" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </template>
            </el-table-column>
            <!-- <el-table-column label="规格" width="90" align="center" prop="standard" /> -->
            <el-table-column label="规格" width="110" align="center">
              <template slot-scope="scope">
                <el-input v-model="scope.row.standard" placeholder="请输入规格"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="库存" width="80" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.stockCount || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.count" :min="0.001" :step="1" :precision="3"
                  @change="calculateAmount(scope.$index)" />
              </template>
            </el-table-column>
            <el-table-column label="单位" width="80" align="center" prop="productUnitName" />
            <el-table-column prop="purchasePrice" label="产品单价">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.purchasePrice" :min="0" :step="0.01" :precision="2"
                  @change="calculateAmount(scope.$index)" />
              </template>
            </el-table-column>
            <el-table-column prop="totalProductPrice" label="金额" width="80" align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.totalProductPrice ? scope.row.totalProductPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="taxPercent" label="税率(%)">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.taxPercent" :min="0" :step="1" :precision="2"
                  @change="calculateTax(scope.$index)" />
              </template>
            </el-table-column>
            <el-table-column prop="taxPrice" width="80" label="税额" align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.taxPrice ? scope.row.taxPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" width="80" label="税额合计" align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.totalPrice ? scope.row.totalPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button type="text" icon="el-icon-delete" size="small" @click="removeProduct(scope.$index)"
                  :disabled="orderProducts.length <= 1" />
              </template>
            </el-table-column>
          </el-table>

          <!-- 添加产品按钮 -->
          <div class="add-product-btn">
            <el-button type="primary" icon="el-icon-plus" size="small" @click="addProduct">
              添加销售产品
            </el-button>
          </div>

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
                <span>数量：{{ scope.row.count ? scope.row.count : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="金额" align="right">
              <template slot-scope="scope">
                <span>金额：{{ scope.row.totalPrice ? scope.row.totalPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额" align="right">
              <template slot-scope="scope">
                <span>税额：{{ scope.row.taxPrice ? scope.row.taxPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="税额合计" align="right">
              <template slot-scope="scope">
                <span>税额合计：{{ scope.row.totalProductPrice ? scope.row.totalProductPrice : 0 }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 优惠和结算信息区域 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- <el-col :span="6">
            <el-form-item label="优惠率(%)" prop="discountRate">
              <el-input-number 
                v-model="orderForm.discountRate" 
                :min="0" 
                :max="100" 
                :step="0.01" 
                :precision="2"
                @change="calculateDiscount"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="付款优惠" prop="discountAmount">
              <el-input v-model="orderForm.discountAmount" placeholder="0.00" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优惠后金额" prop="totalPrice">
              <el-input v-model="orderForm.totalPrice" placeholder="0.00" disabled />
            </el-form-item>
          </el-col> -->
          <el-col :span="6">
            <el-form-item label="结算账户" prop="accountId">
              <el-select v-model="orderForm.accountId" placeholder="请选择结算账户" style="width: 100%">
                <el-option v-for="item in accounts" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" v-if="false">
            <el-form-item label="支付订金" prop="deposit">
              <el-input-number v-model="orderForm.deposit" :min="0" :step="0.01" :precision="2" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 底部操作按钮 -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="son_close">取消</el-button>
        <el-button type="primary" @click="submitOrder" :disabled="orderForm.status === 20">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createSupplier, updateSupplier, deleteSupplier, getSupplier, getSupplierPage, exportSupplierExcel } from "@/api/erp/supplier";
import { createProduct, updateProduct, deleteProduct, getProduct, getProductPage, exportProductExcel } from "@/api/erp/product";
import { createAccount, updateAccount, deleteAccount, getAccount, getAccountPage, exportAccountExcel } from "@/api/erp/account";
import { createStock, updateStock, deleteStock, getStock, getStockPage, exportStockExcel } from "@/api/erp/stock";
import { createSaleOrder, updateSaleOrder, deleteSaleOrder, getSaleOrder, getSaleOrderPage, exportSaleOrderExcel } from "@/api/erp/saleOrder";
import { createCustomer, updateCustomer, deleteCustomer, getCustomer, getCustomerPage, exportCustomerExcel } from "@/api/erp/customer";
import { status } from "nprogress";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: 'SaleOrderDetail',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      userInfo: {},
      title: '新增订单',
      dialogVisible: this.visible,
      // 订单表单数据
      orderForm: {
        customerId: '',
        accountId: '',
        saleUserId: '',
        orderTime: new Date().getTime(),
        discountPercent: 0,
        discountPrice: 0,
        totalPrice: 0,
        depositPrice: 0,

        orderNo: '',
        supplier: '',
        remark: '',
        discountRate: 0,
        discountAmount: 0,
        totalPrice: 0,
        deposit: 0,
      },
      // 订单产品列表
      orderProducts: [
        {
          productId: '',
          productUnitName: '',
          standard: '',
          productBarCode: '',
          purchasePrice: 0,
          productPrice: 0,
          stockCount: 1,
          count: 1,
          totalProductPrice: 0,
          taxPercent: 0,
          taxPrice: 0,
          totalPrice: 0,

          productName: '',
        }
      ],
      // 下拉选择数据源
      suppliers: [], // 供应商列表
      customers: [],
      products: [], // 产品列表
      accounts: [], // 结算账户列表
      // 表单验证规则
      rules: {
        orderTime: [
          { required: true, message: '请选择订单时间', trigger: 'change' }
        ],
        supplier: [
          { required: true, message: '请选择供应商', trigger: 'change' }
        ],
        accountId: [{ required: true, message: "结算账号不能为空", trigger: "blur" }],
        customerId: [{ required: true, message: "客户不能为空", trigger: "blur" }],
      }
    };
  },
  computed: {
    // 计算总数量
    count() {
      // return this.orderProducts.reduce((sum, item) => sum + Number(item.count), 0);
      return this.orderProducts.reduce((sum, item) => this.my_common.computer(sum, item.count, 0, 2), 0);
    },
    // 计算总金额
    totalProductPrice() {
      // return this.orderProducts.reduce((sum, item) => sum + Number(item.totalProductPrice), 0);
      return this.orderProducts.reduce((sum, item) => this.my_common.computer(sum, item.totalProductPrice, 0, 2), 0);
      // return this.orderProducts.reduce((sum, item) => sum + Number(item.productPrice)*Number(item.count), 0);
    },
    // 计算总税额
    taxPrice() {
      // return this.orderProducts.reduce((sum, item) => sum + Number(item.taxPrice), 0);
      return this.orderProducts.reduce((sum, item) => this.my_common.computer(sum, item.taxPrice, 0, 2), 0);
    },
    // 计算总税额合计
    totalPrice() {
      // return this.orderProducts.reduce((sum, item) => sum + Number(item.totalPrice), 0);
      return this.orderProducts.reduce((sum, item) => this.my_common.computer(sum, item.totalPrice, 0, 2), 0);
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
    },
    dialogVisible(val) {
      this.$emit('update:visible', val);
    }
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`=============================>detail.vue`);
    this.get_customers();
    this.get_products();
    this.get_accounts();
  },
  methods: {
    /** 修改按钮操作 */
    handleUpdate(row) {
      let that = this;
      const id = row.id;
      getSaleOrder(id).then(response => {
        this.orderForm = { ...this.orderForm, ...response.data };

        this.orderProducts = this.orderForm.items;
        this.orderForm.items.forEach((obj, index) => {
          obj.purchasePrice = obj.productPrice;
          obj.totalPrice = obj.count * obj.productPrice;
          this.calculateAmount(index);
          that.update_productId(obj.productId, obj);
        })
        // console.log(`getSaleOrder===================>form:${JSON.stringify(this.orderForm)}`);
        this.title = "修改ERP 销售订单";
        // if (!this.form.fid) {
        //   this.form.fid = getUUID().replaceAll("-", "");
        // }
      });
    },
    son_close() {
      this.$emit('son_close');
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
      // console.log("===========>" + JSON.stringify(this.form));
    },
    get_customers() {
      this.customers = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getCustomerPage(params).then(response => {
        this.customers.push(...response.data.list)
        // this.customers.forEach(obj => {
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
      this.form.accountName = item.accountName;
      this.form.accountId = item.id;
      console.log("===========>" + JSON.stringify(this.form));
    },
    get_accounts() {
      this.accounts = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getAccountPage(params).then(response => {
        this.accounts.push(...response.data.list);
        // this.accounts.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log("getAccountPage===========>"+JSON.stringify(this.accounts));
      })
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
      getProductPage(params).then(response => {
        this.products.push(...response.data.list);
        this.products.forEach(obj => {
          obj.taxPercent = 0;
          if (!obj.stockCount) obj.stockCount = 0;
        })
        // this.products.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log("getProductPage===========>"+JSON.stringify(this.products));
      })
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
      this.form.supplierName = item.supplierName;
      this.form.supplierId = item.id;
      console.log("===========>" + JSON.stringify(this.form));
    },
    get_suppliers() {
      this.suppliers = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getSupplierPage(params).then(response => {
        this.suppliers.push(...response.data.list);
        // this.suppliers.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log("getSupplierPage===========>"+JSON.stringify(this.suppliers));
      })
    },

    // 添加产品
    addProduct() {
      this.orderProducts.push({
        productId: '',
        productName: '',
        standard: '',
        count: 1,
        purchasePrice: 0,
        totalPrice: 0,
        taxPercent: 0,
        taxPrice: 0,
        totalTax: 0
      });
    },

    // 移除产品
    removeProduct(index) {
      this.orderProducts.splice(index, 1);
      this.calculateTotal();
    },

    // 产品选择变化
    handleProductChange(row, index) {
      const product = this.products.find(item => item.id === row.productId);
      if (product) {
        row.productName = product.name;
        row.purchasePrice = product.purchasePrice;
        row.productPrice = product.purchasePrice;
        row.totalProductPrice = product.purchasePrice;
        row.taxPercent = product.taxPercent;

        row.productUnitName = product.unitName;
        row.standard = product.standard;
        row.productBarCode = product.barCode;
        row.stockCount = product.taxPercent;
        // row.count = product.taxPercent;
        // row.totalProductPrice = product.taxPercent;
        row.taxPercent = 0;
        row.taxPrice = 0;
        row.totalPrice = 0;
        // row.taxPrice = product.taxPercent;
        // row.totalPrice = product.taxPercent;
        row.stockCount = product.stockCount;

        this.calculateAmount(index);
      }
    },

    // 计算金额
    calculateAmount(index) {
      const row = this.orderProducts[index];
      console.log(`calculateAmount index:${index}=========================>${JSON.stringify(row)}`);
      row.totalPrice = this.my_common.computer(row.count, row.purchasePrice, 2, 2);
      this.$set(this.orderProducts, index, row);
      this.calculateTax(index);
    },
    // 计算税额
    calculateTax(index) {
      const row = this.orderProducts[index];
      // row.taxPrice = row.totalProductPrice * (row.taxPercent / 100);
      let persent = this.my_common.computer(row.taxPercent, 100, 3, 2);
      row.taxPrice = this.my_common.computer(row.totalPrice, persent, 2, 2);
      // row.totalPrice = row.totalProductPrice + row.taxPrice;
      row.totalProductPrice = this.my_common.computer(row.totalPrice, row.taxPrice, 0, 2);
      this.$set(this.orderProducts, index, row);
      this.calculateDiscount();
    },
    // 计算优惠
    calculateDiscount() {
      let total = 0;
      this.orderProducts.forEach(item => {
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

    // 文件上传前校验
    beforeUpload(file) {
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('上传附件大小不能超过 2MB!');
      }
      return isLt2M;
    },

    // 提交订单
    submitOrder() {
      this.$refs.orderForm.validate(valid => {
        if (valid) {
          // 检查是否选择了产品
          const hasProduct = this.orderProducts.some(item => item.productId);
          if (!hasProduct) {
            this.$message.error('请至少选择一个产品');
            return;
          }

          // 构造提交数据
          if(!this.orderForm.tenantId) this.orderForm.tenantId = this.userInfo.tenantId;
          if(!this.orderForm.deptId) this.orderForm.deptId = this.userInfo.deptId;
          if(!this.orderForm.userId) this.orderForm.userId = this.userInfo.userId;
          this.orderProducts.forEach(item => {
            if(!item.tenantId) item.tenantId = this.userInfo.tenantId;
            if(!item.deptId) item.deptId = this.userInfo.deptId;
            if(!item.userId) item.userId = this.userInfo.userId;
          })
          const orderData = {
            ...this.orderForm,
            items: this.orderProducts,
            count: this.count,
            totalPrice: this.totalPrice,
            totalTaxPrice: this.totalTaxPrice,
            totalTaxSum: this.totalTaxSum,

            "discountPercent": this.orderForm.discountPercent,
            "discountPrice": this.orderForm.discountPrice,
            "depositPrice": this.orderForm.depositPrice,
          };
          // console.log(`orderForm=====================>${JSON.stringify(this.orderForm)}`);
          // console.log(`submitOrder=====================>${JSON.stringify(orderData)}`);
          // return

          // 提交表单数据
          this.$emit('submit', orderData);
        }
      });
    }
  }
};
</script>

<style scoped>
.order-dialog .el-dialog__body {
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
}

.order-table-container {
  margin-top: 20px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 15px;
}

.table-title {
  font-size: 14px;
  color: #1890ff;
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px solid #e6e6e6;
}

.add-product-btn {
  text-align: center;
  margin: 15px 0;
}

.total-table .el-table__cell {
  background-color: #f5f7fa !important;
  font-weight: bold;
}

.total-label {
  font-weight: bold;
}

.upload-btn {
  margin-left: 10px;
}
</style>
