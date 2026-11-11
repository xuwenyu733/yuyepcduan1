<template>
  <div class="app-container">
    <el-col >
      <el-form ref="form" :model="form" :rules="rules" :label-position="labelPosition" label-width="120px" style="padding-bottom: 10px;">
        <el-form-item label="请假名称" prop="leaveName">
          <el-input v-model="form.leaveName" placeholder="请输入请假名称" />
        </el-form-item>
        <el-form-item label="请假备注">
          <editor v-model="form.leaveContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable v-model="form.startTime" type="date" value-format="timestamp" placeholder="选择开始时间"
            format="yyyy-MM-dd HH:mm:ss"
           />
        </el-form-item>
        <el-form-item label="开始时间" prop="endTime">
           <el-date-picker clearable v-model="form.endTime" type="date" value-format="timestamp" placeholder="选择结束时间"
            format="yyyy-MM-dd HH:mm:ss"
           />
        </el-form-item>
        <el-form-item label="请假人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入请假人" />
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_ASKFOR_LEAVE_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>

      </el-form>
    </el-col>
  </div>
</template>

<script>
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import {listSimpleSuppliers} from "@/api/erp/supplier";
import BigNumber from "bignumber.js";
import { createZkUseMedicine, updateZkUseMedicine, deleteZkUseMedicine, getZkUseMedicine, getZkUseMedicinePage, exportZkUseMedicineExcel } from "@/api/fish/zkUseMedicine";
import Editor from '@/components/Editor';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkAskforLeave, updateZkAskforLeave, deleteZkAskforLeave, getZkAskforLeave, getZkAskforLeavePage, exportZkAskforLeaveExcel } from "@/api/fish/zkAskforLeave";

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
      form:{},
      // 表单校验
      rules: {
      }
    }
  },
  created() {
    let result = window.matchMedia("(min-width: 450px)").matches;
    if(result){
      this.labelPosition = 'right';
    }else{
      this.labelPosition = 'top';
    }
    //alert(this.$route.query)
    // console.log(this.taskId, this.businessKey)
    // alert(this.taskKey)
    if (!this.businessKey) {
      this.$message.error('未传递 id 参数，无法查看流程信息');
      return;
    }
    this.getForm();
    this.getSupplierList();
    this.getStoreHouseList();
  },
  methods:{
    getForm(){
      getZkAskforLeave(this.businessKey).then(response => {
        this.form = response.data
        console.log(`form:${JSON.stringify(this.form)}`);
        
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      })
    },
    submitForm(data, callback){
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        this.form.taskParam = data.taskParam;
        this.form.action = this.taskKey;
        console.log(data);
        updateZkAskforLeave(this.form).then(response => {
          this.$modal.msgSuccess('审批成功')
          callback();
        })
      });
    },
    getSupplierList() {
      // 获得用户列表
      this.supplierOptions = [];
      listSimpleSuppliers().then(response => {
        this.supplierOptions.push(...response.data);
      });
    },
    // getStoreHouseList() {
    //   // 获得用户列表
    //   this.storeHouseOptions = [];
    //   listSimpleStoreHouses().then(response => {
    //     this.storeHouseOptions.push(...response.data);
    //   });
    // },
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
  }
}
</script>

<style scoped>

</style>
