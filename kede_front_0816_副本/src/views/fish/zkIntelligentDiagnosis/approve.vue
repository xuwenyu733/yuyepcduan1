<template>
  <div class="app-container" style="background-color: #fff !important;padding: 0;" v-if="form.fid">
    <el-col style="background-color: #0f1a26 !important;border-radius: 4px;padding: 20px 7px 7px 0;margin-bottom: 15px;">
      <el-form ref="form" :model="form" :rules="rules" :label-position="labelPosition" label-width="120px" style="padding-bottom: 10px;">
        <!-- <el-form-item label="用法用量" prop="midicineDosage">
          <el-input v-model="form.midicineDosage" placeholder="请输入用法用量" />
        </el-form-item> -->
        <el-form-item label="批次编号" prop="batchNumber">
          <el-input v-model="form.batchNumber" :min-height="192"/>
        </el-form-item>
        <el-form-item label="存塘编号" prop="pondDetailNum">
          <el-input v-model="form.pondDetailNum" :min-height="192"/>
        </el-form-item>
        <el-form-item label="鱼苗种类" prop="status">
          <el-select v-model="form.fishTypes" placeholder="请输入鱼苗种类" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_INTELLIGENT_DIAGNOSIS_ILL_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源" prop="supplierName">
          <el-input v-model="form.supplierName" :min-height="192"/>
        </el-form-item>
        <el-form-item label="投喂方式" prop="feedType">
          <el-input v-model="form.feedType" :min-height="192"/>
        </el-form-item>
        <el-form-item label="发病数量" prop="illNum">
          <el-input v-model="form.illNum" :min-height="192"/>
        </el-form-item>
        <el-form-item label="病害名称" prop="illName">
          <el-input v-model="form.illName" :min-height="192"/>
        </el-form-item>
        <el-form-item label="病害分类" prop="illType">
          <el-input v-model="form.illType" :min-height="192"/>
        </el-form-item>
        <el-form-item label="异常部位" prop="abnormalArea">
          <el-input v-model="form.abnormalArea" :min-height="192"/>
        </el-form-item>
        <el-form-item label="异常表现及描述" prop="abnormalContent">
          <el-input v-model="form.abnormalContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="溶氧" prop="rongYangNongDu">
          <el-input v-model="form.rongYangNongDu" :min-height="192"/>
        </el-form-item>
        <el-form-item label="酸碱度" prop="suanJianDu">
          <el-input v-model="form.suanJianDu" :min-height="192"/>
        </el-form-item>
        <el-form-item label="氨氮" prop="anDan">
          <el-input v-model="form.anDan" :min-height="192"/>
        </el-form-item>
        <el-form-item label="水温" prop="shuiWen">
          <el-input v-model="form.shuiWen" :min-height="192"/>
        </el-form-item>
        <el-form-item label="水位" prop="shuiWei">
          <el-input v-model="form.shuiWei" :min-height="192"/>
        </el-form-item>
        <el-form-item label="症状(图像)" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_intelligent_diagnosis_image" @sendCurrFiles="receiveFiles_img"></FileBusUpload>
        </el-form-item>
        <el-form-item label="症状(视频)" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_intelligent_diagnosis_video" @sendCurrFiles="receiveFiles_video"></FileBusUpload>
        </el-form-item>
        <el-form-item label="养殖负责人意见">
          <el-input v-model="form.humanReport" :min-height="192"/>
        </el-form-item>
        <el-form-item label="专家意见">
          <el-input v-model="form.differentialAnalysis" :min-height="192"/>
        </el-form-item>
      </el-form>
    </el-col>
  </div>
</template>

<script>
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import {listSimpleSuppliers} from "@/api/erp/supplier";
import BigNumber from "bignumber.js";
import { createZkIntelligentDiagnosis, updateZkIntelligentDiagnosis, deleteZkIntelligentDiagnosis, getZkIntelligentDiagnosis, getZkIntelligentDiagnosisPage, exportZkIntelligentDiagnosisExcel } from "@/api/fish/zkIntelligentDiagnosis";
import Editor from '@/components/Editor';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

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
      // 表单校验
      rules: {
      },
      fileObjects_img: [],
      fileObjects_video: [],
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
    // this.getStoreHouseList();
  },
  methods:{
    receiveFiles_img(param) {
      // console.log(`==============>param:${JSON.stringify(param)}`)
      this.fileObjects_img.push(param);
      this.form.imageUrls = this.fileObjects_img.map(o => o.fileUrl).join(",");
    },
    receiveFiles_video(param) {
      // console.log(`==============>param:${JSON.stringify(param)}`)
      this.fileObjects_video.push(param);
      this.form.videoUrls = this.fileObjects_video.map(o => o.fileUrl).join(",");
    },
    getForm(){
      getZkIntelligentDiagnosis(this.businessKey).then(response => {
        this.form = response.data;
        this.form.batchNumber = this.form.batchNumber.replace("CGRK", "");
        this.old_form = JSON.parse(JSON.stringify(response.data));
        // console.log(`form:${JSON.stringify(this.form)}`);
        
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      })
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
      debugger
      console.log(`====================>submitForm:${JSON.stringify(data)}`);
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // let new_taskParam = JSON.parse(JSON.stringify(data.taskParam));
        // let str = this.getUpdateInfo();
        // console.log(`str=======>${str}`);
        // new_taskParam.approveMessage = str?`${data.taskParam.approveMessage}，修改信息：${str}`:data.taskParam.approveMessage;
        // this.form.taskParam = new_taskParam;
        // this.form.action = this.taskKey;
        // console.log(`form:${JSON.stringify(this.form)}`);
        // return;
        updateZkIntelligentDiagnosis(this.form).then(response => {
          // this.$modal.msgSuccess('审批成功')
          callback();
        })
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
  }
}
</script>

<style scoped>
.el-tag {
  color: #ccc;
}
</style>
