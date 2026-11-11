<template>
  <div class="app-container">
    <el-col>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="small">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="订单单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="保存时自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="订单时间" prop="orderTime">
              <el-date-picker v-model="form.orderTime" type="datetime" value-format="timestamp"
                placeholder="选择订单时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%">
                <el-option v-for="item in suppliers" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入备注" type="textarea" rows="2" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 订单产品清单区域 -->
        <div class="order-table-container">
          <h3 class="table-title">订单产品清单</h3>
          <el-table :data="orderProducts" border size="small" class="order-table">
            <!-- <el-table-column type="index" label="序号" width="60" align="center" /> -->
            <el-table-column prop="productId" label="产品名称" width="180">
              <template slot-scope="scope">
              <!-- @change="handleProductChange(scope.row, scope.$index)" -->
                <el-select v-model="scope.row.productId" placeholder="请选择产品" style="width: 100%"
                  >
                  <el-option v-for="item in products" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
                <!-- <div>{{ scope.row.productId }}</div> -->
              </template>
            </el-table-column>
            <el-table-column label="规格" width="80" align="center" prop="standard" />
            <el-table-column label="库存" width="80" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.stockCount || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量">
              <template slot-scope="scope">
              <!-- @change="calculateAmount(scope.$index)" -->
                <el-input-number v-model="scope.row.count" :min="0.01" :step="1" :precision="3"
                   />
              </template>
            </el-table-column>
            <el-table-column label="单位" width="80" align="center" prop="productUnitName" />
            <el-table-column prop="purchasePrice" label="产品单价">
              <template slot-scope="scope">
              <!-- @change="calculateAmount(scope.$index)" -->
                <el-input-number v-model="scope.row.purchasePrice" :min="0" :step="0.01" :precision="2"
                   />
              </template>
            </el-table-column>
            <el-table-column prop="totalProductPrice" label="金额" width="80" align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.totalProductPrice ? scope.row.totalProductPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="taxPercent" label="税率(%)">
              <!-- @change="calculateTax(scope.$index)" -->
              <template slot-scope="scope">
              <!-- @change="calculateAmount(scope.$index)" -->
                <el-input-number v-model="scope.row.taxPercent" :min="0" :step="1" :precision="2"
                   />
              </template>
            </el-table-column>
            <el-table-column prop="taxPrice" label="税额" align="right" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.taxPrice ? scope.row.taxPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="税额合计" align="right" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.totalPrice ? scope.row.totalPrice : 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center" v-if="false">
              <template slot-scope="scope">
                <el-button type="text" icon="el-icon-delete" size="small" @click="removeProduct(scope.$index)"
                  :disabled="orderProducts.length <= 1" />
              </template>
            </el-table-column>
          </el-table>

          <!-- 添加产品按钮 -->
          <!-- <div class="add-product-btn">
            <el-button type="primary" icon="el-icon-plus" size="small" @click="addProduct">
              添加采购产品
            </el-button>
          </div> -->

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

        <!-- 优惠和结算信息区域 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- <el-col :span="6">
            <el-form-item label="优惠率(%)" prop="discountRate">
              <el-input-number 
                v-model="form.discountRate" 
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
              <el-input v-model="form.discountAmount" placeholder="0.00" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优惠后金额" prop="totalPrice">
              <el-input v-model="form.totalPrice" placeholder="0.00" disabled />
            </el-form-item>
          </el-col> -->
          <el-col :span="6">
            <el-form-item label="结算账户" prop="accountId">
              <el-select v-model="form.accountId" placeholder="请选择结算账户" style="width: 100%">
                <el-option v-for="item in accounts" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" v-if="false">
            <el-form-item label="支付订金" prop="depositPrice">
              <el-input-number v-model="form.depositPrice" :min="0" :step="0.01" :precision="2" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-col>
  </div>
</template>

<script>
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import {listSimpleSuppliers} from "@/api/erp/supplier";
import BigNumber from "bignumber.js";
import { createZkUseMedicine, updateZkUseMedicine, deleteZkUseMedicine, getZkUseMedicine, getZkUseMedicinePage, exportZkUseMedicineExcel } from "@/api/fish/zkUseMedicine";
import Editor from '@/components/Editor';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { UPLOAD_VIDEO_TYPE } from "@/utils/constants";
import { createSupplier, updateSupplier, deleteSupplier, getSupplier, getSupplierPage, exportSupplierExcel } from "@/api/erp/supplier";
import { createProduct, updateProduct, deleteProduct, getProduct, getProductPage, exportProductExcel } from "@/api/erp/product";
import { createAccount, updateAccount, deleteAccount, getAccount, getAccountPage, exportAccountExcel } from "@/api/erp/account";
import { createStock, updateStock, deleteStock, getStock, getStockPage, exportStockExcel } from "@/api/erp/stock";
import { createPurchaseOrder, updatePurchaseOrder, deletePurchaseOrder, getPurchaseOrder, getPurchaseOrderPage, updatePurchaseOrderStatus } from "@/api/erp/purchaseOrder";

export default {
  name: "approve",
  components: {FileBusUpload, Editor, },
  props: {
    // 流程数据
    businessKey: {
      type: String,
    },
    taskKey: {
      type: String,
    },
  },
  data(){
    return {
      labelPosition: 'right',
      supplierOptions:[],
      storeHouseOptions: [],
      old_form:{},
      form:{},
      fileType: UPLOAD_VIDEO_TYPE,
      // 表单校验
      rules: {
      },
      fileObjects_img: [],
      fileObjects_video: [],
      suppliers: [], // 供应商列表
      products: [], // 产品列表
      accounts: [], // 结算账户列表
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
    }
  },
  async created() {
    let result = window.matchMedia("(min-width: 450px)").matches;
    if(result){
      this.labelPosition = 'right';
    }else{
      this.labelPosition = 'top';
    }
    // alert(this.$route.query)
    // console.log(this.taskId, this.businessKey)
    // alert(this.taskKey)
    if (!this.businessKey) {
      this.$message.error('未传递 id 参数，无法查看流程信息');
      return;
    }
    await this.getSupplierList();
    await this.get_accounts();
    await this.get_products();
    this.getForm();
    // this.getStoreHouseList();
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
  methods:{
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
      // console.log("===========>" + JSON.stringify(this.form));
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
    receiveFiles_img(param) {
      // console.log(`==============>param:${JSON.stringify(param)}`)
      this.fileObjects_img.push(param);
      this.form.symptomUrls = this.fileObjects_img.map(o => o.fileUrl).join(",");
    },
    receiveFiles_video(param) {
      // console.log(`==============>param:${JSON.stringify(param)}`)
      this.fileObjects_video.push(param);
      this.form.symptomVideos = this.fileObjects_video.map(o => o.fileUrl).join(",");
    },
    getForm(){
      let that = this;
      getPurchaseOrder(this.businessKey).then(response => {
        // console.log(`getPurchaseOrder============>suppliers:${JSON.stringify(this.suppliers)}`);
        // console.log(`getPurchaseOrder============>products:${JSON.stringify(this.products)}`);

        this.form = response.data;
        this.form.items.forEach(obj => {
          obj.purchasePrice = obj.productPrice;
          that.update_productId(obj.productId, obj);
        })
        this.orderProducts = this.form.items;
        this.orderProducts.forEach((item, index) => {
          this.calculateAmount(index);
        })
        console.log(`getPurchaseOrder============>form:${JSON.stringify(this.form)}`);
        this.title = "修改ERP 采购订单";
        // if (!this.form.fid) {
        //   this.form.fid = getUUID().replaceAll("-", "");
        // }
      });
    },
    getUpdateInfo() {
      let arr = [];
      if(this.form.controlContent != this.old_form.controlContent) {
        arr.push(`症状(文字)由${this.old_form.controlContent}改成${this.form.controlContent}`);
      }
      if(this.form.medicineName != this.old_form.medicineName) {
        arr.push(`用药方案由${this.old_form.medicineName}改成${this.form.medicineName}`);
      }
      if(arr.length > 0) {
        return arr.join(",");
      }
      return "";
    },
    submitForm(data, callback){
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // this.$modal.msgSuccess('审批成功')
        if(callback) callback();
        // let new_taskParam = JSON.parse(JSON.stringify(data.taskParam));
        // let str = this.getUpdateInfo();
        // console.log(`str=======>${str}`);
        // new_taskParam.approveMessage = str?`${data.taskParam.approveMessage}，修改信息：${str}`:data.taskParam.approveMessage;
        // this.form.taskParam = new_taskParam;
        // this.form.action = this.taskKey;
        // console.log(`form:${JSON.stringify(this.form)}`);
        // return;
        // updateZkUseMedicine(this.form).then(response => {
        //   // this.$modal.msgSuccess('审批成功')
        //   if(callback) callback();
        // })
      });
    },
    getSupplierList() {
      // 获得用户列表
      this.supplierOptions = [];
      listSimpleSuppliers().then(response => {
        this.supplierOptions.push(...response.data.list);
      });
    },
    getSummaries(param) {
      // console.log(param)
      const { columns, data } = param;
      const sums = {};
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计';
          return;
        }
        const values = data.map(item => Number(item[column.property]));
        if (column.label == "价格" || column.label == "数量" || column.label == "总价" || column.label == "成本" || column.label == "总成本") {
          if (!values.every(value => isNaN(value))) {
            sums[index] = values.reduce((prev, curr) => {
              const value = Number(curr);
              if (!isNaN(value)) {
                return BigNumber(prev).plus(BigNumber(curr)).toFixed(2);
              } else {
                return BigNumber(prev).toFixed(2);
              }
            }, 0);
            // sums[index] += ' kg';
          } else {
            // sums[index] = 'N/A';
          }
        }
      });
      return sums;
    },
    // 计算金额
    calculateAmount(index) {
      const row = this.orderProducts[index];
      // console.log(`calculateAmount index:${index}=========================>${JSON.stringify(row)}`);
      row.totalProductPrice = this.my_common.computer(row.count, row.purchasePrice, 2, 2);
      this.$set(this.orderProducts, index, row);
      this.calculateTax(index);
    },
    // 计算税额
    calculateTax(index) {
      const row = this.orderProducts[index];
      // row.taxPrice = row.totalProductPrice * (row.taxPercent / 100);
      let persent = this.my_common.computer(row.taxPercent, 100, 3, 2);
      row.taxPrice = this.my_common.computer(row.totalProductPrice, persent, 2, 2);
      // row.totalPrice = row.totalProductPrice + row.taxPrice;
      row.totalPrice = this.my_common.computer(row.totalProductPrice, row.taxPrice, 0, 2);
      this.$set(this.orderProducts, index, row);
      this.calculateDiscount();
    },
    // 计算优惠
    calculateDiscount() {
      let total = 0;
      this.orderProducts.forEach(item => {
        total = this.my_common.computer(total, item.totalPrice, 0, 2);
      });
      // this.form.discountAmount = total * (this.form.discountRate / 100);
      let discountRate = this.form.discountRate;
      if (!discountRate) discountRate = 0;
      let persent = this.my_common.computer(discountRate, 100, 3, 2);
      this.form.discountAmount = this.my_common.computer(persent, total, 2, 2);
      // this.form.totalPrice = total - this.form.discountAmount;
      this.form.totalPrice = this.my_common.computer(total, this.form.discountAmount, 1, 2);
    },
    // 计算总计
    calculateTotal() {

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
      // console.log("===========>" + JSON.stringify(row));
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
          // this.products.forEach(obj => {
          //   let tempStr = `${obj.deptName}-${obj.nickname}`;
          //   obj.nickname = tempStr;
          //   console.log("===========>"+JSON.stringify(obj));
          // })
          // console.log("getProductPage===========>"+JSON.stringify(this.products));
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
      this.form.accountName = item.accountName;
      this.form.accountId = item.id;
      // console.log("===========>" + JSON.stringify(this.form));
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
  }
}
</script>

<style scoped>

</style>
