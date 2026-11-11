<template>
  <div class="app-container">
    <el-col v-if="form.fid">
      <el-form ref="form" :model="form" :rules="rules" :label-position="labelPosition" label-width="120px" style="padding-bottom: 10px;">
        <!-- <el-form-item label="用法用量" prop="midicineDosage">
          <el-input v-model="form.midicineDosage" placeholder="请输入用法用量" />
        </el-form-item> -->
        <el-form-item label="症状(文字)" prop="controlContent">
          <editor v-model="form.controlContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="症状(图像)" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_use_medicine_img" @sendCurrFiles="receiveFiles_img"></FileBusUpload>
        </el-form-item>
        <el-form-item label="症状(视频)" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_use_medicine_video" :fileType="fileType" @sendCurrFiles="receiveFiles_video"></FileBusUpload>
        </el-form-item>
        <el-form-item label="用药方案">
          <!-- <editor v-model="form.medicineName" :min-height="192"/> -->
          <el-input type="textarea" :autosize="autosizeObj" v-model="form.medicineName" placeholder="请输入专家方案" />
        </el-form-item>
        <el-form-item label="专家方案">
          <el-input type="textarea" :autosize="autosizeObj" v-model="form.expertSolution" placeholder="请输入专家方案" />
        </el-form-item>
        <el-form-item label="实际方案">
          <el-input type="textarea" :autosize="autosizeObj" v-model="form.actualSolution" placeholder="请输入实际方案" />
        </el-form-item>
        <!-- <el-form-item label="用药效果评估" prop="medicineAfter">
          <el-input v-model="form.medicineAfter" placeholder="请输入用药效果评估" />
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item> -->
        <el-form-item label="用药明细">
          <el-button type="primary" size="mini" @click="addMedicineLog">添加明细</el-button>
          <el-table :data="form.useMedicineLogList" style="width: 100%; margin-top: 10px;" border>
            <el-table-column label="所在存塘" width="200">
              <template v-slot="scope">
                <el-select v-model="scope.row.pondIdArr" multiple filterable clearable placeholder="请选择存塘">
                  <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName})`" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="对应用药" width="180">
              <template v-slot="scope">
                <el-select v-model="scope.row.productNo" filterable clearable placeholder="请选择用药" @change="updateLogProductId(scope.row)">
                  <el-option v-for="(item, index) in productList" :key="item.id+'__'+index" :label="`${item.info}`" :value="item.productNo"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="用药用量" width="150">
              <template v-slot="scope">
                <el-input v-model="scope.row.midicineDosage" placeholder="用药用量" @input="calcMedicineCost(scope.row)"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="用药结果" width="150">
              <template v-slot="scope">
                <el-input v-model="scope.row.medicineAfter" placeholder="用药结果"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="用药时间" width="200">
              <template v-slot="scope">
                <el-date-picker v-model="scope.row.feedTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择时间" style="width: 100%;"></el-date-picker>
              </template>
            </el-table-column>
            <el-table-column label="备注" width="150">
              <template v-slot="scope">
                <el-input v-model="scope.row.remark" placeholder="备注"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template v-slot="scope">
                <el-button size="mini" type="text" icon="el-icon-delete" @click="removeMedicineLog(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.USE_MEDICINE_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-col>
  </div>
</template>

<script>
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import {listSimpleSuppliers} from "@/api/erp/supplier";
import { getProductPage } from "@/api/erp/product";
import { getZkPondPage } from "@/api/fish/zkPond";
import BigNumber from "bignumber.js";
import { createZkUseMedicine, updateZkUseMedicine, deleteZkUseMedicine, getZkUseMedicine, getZkUseMedicinePage, exportZkUseMedicineExcel } from "@/api/fish/zkUseMedicine";
import Editor from '@/components/Editor';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { UPLOAD_VIDEO_TYPE } from "@/utils/constants";

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
      autosizeObj: {minRows:4},
      labelPosition: 'right',
      supplierOptions:[],
      storeHouseOptions: [],
      old_form:{},
      form:{},
      pondList: [],
      productList: [],
      fileType: UPLOAD_VIDEO_TYPE,
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
    // alert(this.$route.query)
    // console.log(this.taskId, this.businessKey)
    // alert(this.taskKey)
    if (!this.businessKey) {
      this.$message.error('未传递 id 参数，无法查看流程信息');
      return;
    }
    this.getForm();
    this.getSupplierList();
    this.getPondList();
    this.getProductList();
    // this.getStoreHouseList();
  },
  methods:{
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
      getZkUseMedicine(this.businessKey).then(response => {
        this.form = response.data;
        this.old_form = JSON.parse(JSON.stringify(response.data));
        console.log(`form:${JSON.stringify(this.form)}`);
        
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
        if (!this.form.useMedicineLogList) {
          this.$set(this.form, 'useMedicineLogList', []);
        }
        
        // Fetch logs
        import("@/api/fish/zkUseMedicineLog").then(api => {
          api.getZkUseMedicineLogPage({ zkUseMedicineId: this.businessKey, pageNo: 1, pageSize: 500 }).then(res => {
            if (res.data && res.data.list) {
              res.data.list.forEach(log => {
                if (log.pondIdArr && typeof log.pondIdArr === 'string') {
                  log.pondIdArr = log.pondIdArr.split(',').map(Number);
                }
              });
              this.$set(this.form, 'useMedicineLogList', res.data.list);
            }
          });
        });
      })
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
      })
    },
    getProductList() {
      this.productList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
        categoryId: 2,
      }
      getProductPage(params).then(response => {
        this.productList.push(...response.data.list)
        this.productList.forEach(obj => {
          let tempStr = `${obj.productName}-${obj.productBrand}`;
          obj.info = tempStr;
        })
      })
    },
    addMedicineLog() {
      if (!this.form.useMedicineLogList) {
        this.$set(this.form, 'useMedicineLogList', []);
      }
      let now = new Date();
      let year = now.getFullYear();
      let month = String(now.getMonth() + 1).padStart(2, '0');
      let day = String(now.getDate()).padStart(2, '0');
      let hours = String(now.getHours()).padStart(2, '0');
      let minutes = String(now.getMinutes()).padStart(2, '0');
      let seconds = String(now.getSeconds()).padStart(2, '0');
      let timeStr = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      this.form.useMedicineLogList.push({
        pondIdArr: [],
        productNo: undefined,
        midicineDosage: undefined,
        medicineAfter: undefined,
        feedTime: timeStr,
        remark: undefined,
      });
    },
    removeMedicineLog(index) {
      this.form.useMedicineLogList.splice(index, 1);
    },
    updateLogProductId(row) {
      let item = null;
      this.productList.forEach(obj => {
        if(obj.productNo == row.productNo) {
          item = obj;
          return;
        }
      })
      if (item) {
        row.medicineName = item.productName;
        row.medicineBrand = item.productBrand;
        row.medicineSpec = item.productSpec;
        row.medicinePrice = item.purchasePrice;
        this.calcMedicineCost(row);
      }
    },
    calcMedicineCost(row) {
      if (row.midicineDosage && row.medicinePrice) {
        row.medicineCost = this.my_common.computer(row.midicineDosage, row.medicinePrice, 2, 4);
      }
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
        
        let submitData = JSON.parse(JSON.stringify(this.form));
        
        updateZkUseMedicine(submitData).then(response => {
          // this.$modal.msgSuccess('审批成功')
          if(callback) callback();
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

</style>
