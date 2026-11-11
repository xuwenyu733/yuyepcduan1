<template>
  <div>
    <!-- 新增表单 -->
    <el-dialog :title="title" :visible.sync="visible" width="80%" @close="handleClose">
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <!-- 基础信息行 -->
        <el-row>
          <el-col :span="6">
            <el-form-item label="付款单号">
              <el-input v-model="form.paymentNo" disabled placeholder="保存时自动生成"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="付款时间" prop="paymentTime">
              <el-date-picker v-model="form.paymentTime" type="datetime" placeholder="选择付款时间" ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="财务" prop="financeUserId">
              <el-select v-model="form.financeUserId" style="width:100%">
                <el-option v-for="s in userList1" :key="s.id" :label="s.nickname" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商">
                <el-option v-for="item in suppliers" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 附件行 -->
        <el-row>
          <el-col :span="6" v-if="false">
            <el-form-item label="附件">
              <el-upload action="https://jsonplaceholder.typicode.com/posts/" :on-success="handleUploadSuccess"
                :on-error="handleUploadError" :file-list="fileList" :show-file-list="true" :limit="3"
                accept=".xlsx,.xls,.doc,.docx,.pdf">
                <el-button type="primary">选取文件</el-button>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 采购入库/退货单表格 -->
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>采购入库、退货单</span>
            </div>
          </template>
          <el-table :data="form.items" border style="width: 100%">
            <!-- <el-table-column prop="index" label="序号" width="80"></el-table-column> -->
            <el-table-column prop="no" label="采购单据编号" width="180"></el-table-column>
            <el-table-column prop="remark" label="备注"></el-table-column>
            <!-- <el-table-column prop="productNames" label="商品信息"></el-table-column> -->
            <el-table-column prop="totalPrice" label="应付金额" width="120"></el-table-column>
            <el-table-column prop="paidPrice" label="已付金额" width="120"></el-table-column>
            <el-table-column prop="paymentPrice" label="本次付款" width="120"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <!-- <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button> -->
                <el-button type="text" size="small" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 16px">
            <el-button type="primary" @click="addPurchaseInbound">+ 添加采购入库单</el-button>
            <!-- <el-button type="primary" @click="addPurchaseReturn">+ 添加采购退货单</el-button> -->
          </div>
        </el-card>
        <el-row>
          <div style="width: 100%;height: 15px"></div>
        </el-row>

        <!-- 付款账户行 -->
        <el-row>
          <el-col :span="6">
            <el-form-item label="付款账户" prop="accountId">
              <el-select v-model="form.accountId" placeholder="请选择结算账户">
                <el-option v-for="item in accounts" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="合计付款">
              <el-input v-model="form.totalPrice" disabled></el-input>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="6">
            <el-form-item label="优惠金额">
              <el-input v-model="form.discountPrice"></el-input>
            </el-form-item>
          </el-col> -->
          <el-col :span="6">
            <el-form-item label="实际付款">
              <el-input v-model="form.paymentPrice"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 操作按钮 -->
        <div style="text-align: right; margin-top: 20px">
          <el-button type="primary" @click="handleSubmit">确定</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </div>
      </el-form>
    </el-dialog>

    <!-- 选择订单 -->
    <PurchaseInPaymentEnableList :visible="other_open" @submit="submitFormDetail" @son_close="parentClose">
    </PurchaseInPaymentEnableList>
  </div>
</template>

<script>
import PurchaseInPaymentEnableList from '@/views/erp/purchase/in/components/PurchaseInPaymentEnableList.vue'
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { createAccount, updateAccount, deleteAccount, getAccount, getAccountPage, exportAccountExcel } from "@/api/erp/account";
import { createSupplier, updateSupplier, deleteSupplier, getSupplier, getSupplierPage, exportSupplierExcel } from "@/api/erp/supplier";
import { createFinancePayment, updateFinancePayment, deleteFinancePayment, getFinancePayment, getFinancePaymentPage, exportFinancePaymentExcel, updateFinancePayStatus } from "@/api/erp/financePayment";
import { UserInfoKey } from "@/utils/constants";

export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  components: {
    PurchaseInPaymentEnableList,
  },
  data() {
    return {
      userInfo: {},
      title: '新增',
      other_open: false,
      form: {
        supplierId: '', // 供应商ID
        accountId: '', // 账户
        paymentTime: new Date().getTime(), // 付款时间
        totalPrice: '', // 应付
        discountPrice: '', // 折扣
        paymentPrice: '', // 
        items: [], // 采购入库/退货单表格数据
      },
      tableData: [],
      rules: {
        paymentTime: [{ required: true, message: '请选择付款时间', trigger: 'change' }],
        supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
        account: [{ required: true, message: '请选择账户', trigger: 'change' }],
      },
      userList1: [],
      accounts: [],
      suppliers: [],
    };
  },
  async mounted() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    await this.get_accounts();
    await this.get_suppliers();
    await this.getUserList();
  },
  methods: {
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id;
      getFinancePayment(id).then(response => {
        this.form = response.data;
        this.form.items.forEach(obj => {
          obj.no = obj.bizNo;
          obj.id = obj.bizId;
        })
        console.log(`getFinancePayment==================>${JSON.stringify(this.form)}`);
        this.title = "修改ERP 付款单";
        // if (!this.form.fid) {
        //   this.form.fid = getUUID().replaceAll("-", "");
        // }
      });
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
      this.form.userId = item.id;
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>" + JSON.stringify(this.form));
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

    submitFormDetail(form) {
      this.tableData = form;
      let sum = 0;
      this.tableData.forEach(obj => {
        obj.paidPrice = obj.paymentPrice;
        obj.paymentPrice = obj.totalPrice - obj.paidPrice;
        sum += obj.paymentPrice;
        obj.bizId = obj.id;
        obj.bizType = 11;
        obj.bizNo = obj.no;
      })
      this.form.items = this.tableData;
      this.form.supplierId = form[0].supplierId;
      this.form.totalPrice = sum;
      this.form.discountPrice = 0;
      this.form.paymentPrice = sum;
      console.log(`submitFormDetail============>${JSON.stringify(form)}`);
      // console.log(`submitFormDetail============>${JSON.stringify(this.form)}`);
    },
    parentClose() {
      this.other_open = false;
    },

    // 表单提交
    handleSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          // 调用后端接口提交数据
          // console.log('提交数据:', this.form);
          this.other_open = false;
          // this.visible = false;
          // 变换提交的参数
          let items = this.form.items.map(obj => {
            let temp = {
              "bizId": obj.id,
              "bizType": 11,
              "bizNo": obj.no,
              "totalPrice": obj.totalPrice,
              "paidPrice": obj.paidPrice,
              "paymentPrice": obj.paymentPrice
            };
            return temp;
          })

          let params = {
            "id": this.form.id,
            "supplierId": this.form.supplierId,
            "accountId": this.form.accountId,
            "financeUserId": this.form.financeUserId,
            "paymentTime": this.form.paymentTime,
            "totalPrice": this.form.totalPrice,
            "discountPrice": this.form.discountPrice,
            "paymentPrice": this.form.paymentPrice,
            items: items
          };
          if(!params.tenantId) params.tenantId = this.userInfo.tenantId;
          if(!params.deptId) params.deptId = this.userInfo.deptId;
          if(!params.userId) params.userId = this.userInfo.userId;
          params.items.forEach(item => {
            if(!item.tenantId) item.tenantId = this.userInfo.tenantId;
            if(!item.deptId) item.deptId = this.userInfo.deptId;
            if(!item.userId) item.userId = this.userInfo.userId;
          })

          this.$emit('submit', params);
        } else {
          console.log('表单验证失败');
          return false;
        }
      });
    },
    // 取消操作
    handleCancel() {
      this.visible = false;
    },
    // 关闭对话框时重置表单
    handleClose() {
      this.$refs['form'].resetFields();
    },
    // 附件上传成功
    handleUploadSuccess(response, file, fileList) {
      console.log('上传成功:', response, file, fileList);
      this.fileList = fileList;
    },
    // 附件上传失败
    handleUploadError(err, file, fileList) {
      console.error('上传失败:', err, file, fileList);
    },
    // 添加采购入库单
    addPurchaseInbound() {
      this.other_open = true;
      // this.tableData.push({
      //   index: this.tableData.length + 1,
      //   purchaseDocNo: 'PID001',
      //   remark: '入库单备注',
      //   dueAmount: 1000,
      //   paidAmount: 500,
      //   currentPayment: 300,
      // });
    },
    // 添加采购退货单
    addPurchaseReturn() {
      this.tableData.push({
        index: this.tableData.length + 1,
        purchaseDocNo: 'PID002',
        remark: '退货单备注',
        dueAmount: 800,
        paidAmount: 200,
        currentPayment: 200,
      });
    },
    // 编辑行
    handleEdit(row) {
      console.log('编辑行:', row);
    },
    // 删除行
    handleDelete(row) {
      const index = this.tableData.indexOf(row);
      if (index !== -1) {
        this.tableData.splice(index, 1);
      }
    },
  },
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
