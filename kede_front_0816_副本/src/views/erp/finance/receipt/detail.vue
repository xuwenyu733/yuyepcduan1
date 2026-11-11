<template>
  <div>
    <!-- 新增表单 -->
    <el-dialog :title="title" :visible.sync="visible" width="80%" @close="handleClose">
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <!-- 基础信息行 -->
        <el-row>
          <el-col :span="6">
            <el-form-item label="收款单号">
              <el-input v-model="form.receiptNo" disabled placeholder="保存时自动生成"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- value-format="yyyy-MM-dd HH:mm:ss" -->
            <el-form-item label="收款时间" prop="receiptTime">
              <el-date-picker
                v-model="form.receiptTime"
                type="datetime"
                placeholder="选择收款时间"
              ></el-date-picker>
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
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户">
                <el-option
                  v-for="item in customers"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
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
              <el-upload
                action="https://jsonplaceholder.typicode.com/posts/"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :file-list="fileList"
                :show-file-list="true"
                :limit="3"
                accept=".xlsx,.xls,.doc,.docx,.pdf"
              >
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
            <el-table-column prop="no" label="销售单据编号" width="180"></el-table-column>
            <el-table-column prop="remark" label="备注"></el-table-column>
            <el-table-column prop="totalPrice" label="应付金额" width="120"></el-table-column>
            <el-table-column prop="receiptedPrice" label="已付金额" width="120"></el-table-column>
            <el-table-column prop="receiptPrice" label="本次收款" width="120"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <!-- <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button> -->
                <el-button type="text" size="small" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 16px">
            <el-button type="primary" @click="addSalesOutbound">+ 添加销售出库单</el-button>
            <!-- <el-button type="primary" @click="addSalesReturn">+ 添加销售退货单</el-button> -->
          </div>
        </el-card>
        <el-row>
          <div style="width: 100%;height: 15px"></div>
        </el-row>
        <!-- 收款账户行 -->
        <el-row>
          <el-col :span="6">
            <el-form-item label="收款账户" prop="accountId">
              <el-select v-model="form.accountId" placeholder="请选择结算账户">
                <el-option
                  v-for="item in accounts"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="合计收款">
              <el-input v-model="form.totalReceipt" disabled></el-input>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="6">
            <el-form-item label="优惠金额">
              <el-input v-model="form.discountAmount"></el-input>
            </el-form-item>
          </el-col> -->
          <el-col :span="6">
            <el-form-item label="实际收款">
              <el-input v-model="form.actualReceipt"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 操作按钮 -->
        <div style="text-align: right; margin-top: 20px">
          <el-button type="primary" @click="handleSubmit">确定</el-button>
          <el-button @click="handleClose">取消</el-button>
        </div>
      </el-form>
    </el-dialog>

    <!-- 选择订单 -->
    <SaleOutReceiptEnableList :visible="other_open" @submit="submitFormDetail" @son_close="parentClose"></SaleOutReceiptEnableList>
  </div>
</template>

<script>
import { createCustomer, updateCustomer, deleteCustomer, getCustomer, getCustomerPage, exportCustomerExcel } from "@/api/erp/customer";
import { createAccount, updateAccount, deleteAccount, getAccount, getAccountPage, exportAccountExcel } from "@/api/erp/account";
import { createSaleOut, updateSaleOut, deleteSaleOut, getSaleOut, getSaleOutPage, exportSaleOutExcel, updateSaleOutStatus } from "@/api/erp/saleOut";
import SaleOutReceiptEnableList from '@/views/erp/sale/out/components/SaleOutReceiptEnableList.vue'
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { createFinanceReceipt, updateFinanceReceipt, deleteFinanceReceipt, getFinanceReceipt, getFinanceReceiptPage, exportFinanceReceiptExcel, updateFinanceReceiptStatus } from "@/api/erp/financeReceipt";
import { UserInfoKey } from "@/utils/constants";

export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  components: {
      SaleOutReceiptEnableList,
  },
  data() {
    return {
      userInfo: {},
      title: '新增',
      other_open: false,
      form: {
        receiptNo: '', // 收款单号（自动生成）
        receiptTime: new Date().getTime, // 收款时间
        customerId: '', // 客户ID
        remark: '', // 备注
        fileList: [], // 附件文件列表
        items: [], // 采购入库/退货单表格数据
        account: '', // 收款账户
        totalReceipt: 0, // 合计收款（自动生成）
        discountAmount: 0, // 优惠金额
        actualReceipt: 0, // 实际收款
        discountPrice: 0,
      },
      items: [],
      rules: {
        receiptTime: [{ required: true, message: '请选择收款时间', trigger: 'change' }],
        customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
        account: [{ required: true, message: '请选择账户', trigger: 'change' }],
      },
      customers: [],
      accounts: [],
      userList1: [],
    };
  },
  async mounted() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    await this.get_accounts();
    await this.get_customers();
    await this.getUserList();
  },
  methods: {
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id;
      getFinanceReceipt(id).then(response => {
        this.form = response.data;
        let sum = 0;
        this.form.items.forEach(obj => {
          obj.no = obj.bizNo;
          obj.id = obj.bizId;
          sum += obj.receiptPrice;
        })
        if(!this.form.receiptTime) this.form.receiptTime = new Date().getTime();
        this.form.totalReceipt = sum;
        this.form.actualReceipt = sum;
        
        console.log(`getFinanceReceipt==================>${JSON.stringify(this.form)}`);
        this.title = "修改ERP 收款单";
        // if (!this.form.fid) {
        //   this.form.fid = getUUID().replaceAll("-", "");
        // }
      });
    },
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.userId = item.id;
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>"+JSON.stringify(this.form));
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

    submitFormDetail(form) {
      this.items = form;
      let sum = 0;
      this.items.forEach(obj => {
        obj.receiptedPrice = obj.receiptPrice;
        obj.receiptPrice = obj.totalPrice - obj.receiptedPrice;
        sum += obj.receiptPrice;
      })
      this.form.items = this.items;
      if(!this.form.customerId) this.form.customerId = form[0].customerId;
      this.form.totalReceipt = sum;
      this.form.actualReceipt = sum;
      // console.log(`submitFormDetail============>${JSON.stringify(this.form)}`);
    },
    parentClose() {
      this.other_open = false;
    },

    update_customerId(currId){
      let item = null;
      this.customers.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.customerName = item.customerName;
      this.form.customerId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
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

    update_accountId(currId){
      let item = null;
      this.accounts.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.accountName = item.accountName;
      this.form.accountId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
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

    // 表单提交
    handleSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          // 调用后端接口提交数据
          // console.log('提交数据:', this.form);
          this.other_open = false;
          // 变换提交的参数
          let items = this.form.items.map(obj => {
            let temp = {
              "bizId": obj.id,
              "bizType": 21,
              "bizNo": obj.no,
              "totalPrice": obj.totalPrice,
              "receiptedPrice": obj.receiptedPrice,
              "receiptPrice": obj.receiptPrice
            };
            return temp;
          })

          let params = {
            "id": this.form.id,
            "customerId": this.form.customerId,
            "accountId": this.form.accountId,
            "financeUserId": this.form.financeUserId,
            "receiptTime": this.form.receiptTime,
            "totalPrice": this.form.totalReceipt,
            "discountPrice": this.form.discountPrice,
            "receiptPrice": this.form.actualReceipt,
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
    // 关闭对话框时重置表单
    handleClose() {
      this.other_open = false;
      this.$refs['form'].resetFields();
      this.$emit('son_close');
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
    // 添加销售出库单
    addSalesOutbound() {
      this.other_open = true;
      // this.items.push({
      //   index: this.items.length + 1,
      //   salesDocNo: 'SOD001',
      //   remark: '出库单备注',
      //   dueAmount: 1000,
      //   paidAmount: 500,
      //   currentReceipt: 300,
      // });
    },
    // 添加销售退货单
    addSalesReturn() {
      this.items.push({
        index: this.items.length + 1,
        salesDocNo: 'SOD002',
        remark: '退货单备注',
        dueAmount: 800,
        paidAmount: 200,
        currentReceipt: 200,
      });
    },
    // 编辑行
    handleEdit(row) {
      console.log('编辑行:', row);
    },
    // 删除行
    handleDelete(row) {
      const index = this.items.indexOf(row);
      if (index !== -1) {
        this.items.splice(index, 1);
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
