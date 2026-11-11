<template>
  <div class="app-container">
    <el-col >
      <el-form ref="form" :model="form" :rules="rules" :label-position="labelPosition" label-width="120px" style="padding-bottom: 10px;">
        <el-form-item label="操作类型" prop="reviseType">
          <el-select v-model="form.reviseType" placeholder="请输入操作类型" filterable clearable @change="update_reviseType">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DATA_REVISE_REVISE_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="投放记录" prop="workshopId" v-if="form.reviseType == 1">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择投放记录" filterable clearable  @change="update_push">
            <el-option v-for="(item, index) in realList_push" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(于${parseTime(item.createTime)}投放${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="死亡记录" prop="workshopId" v-if="form.reviseType == 2">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择死亡记录" filterable clearable  @change="update_dead">
            <el-option v-for="(item, index) in realList_dead" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(于${parseTime(item.createTime)}死亡${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分池记录" prop="workshopId" v-if="form.reviseType == 3">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择分池记录" filterable clearable  @change="update_split">
            <el-option v-for="(item, index) in realList_split" :key="item.id+'__'+index" :label="`${item.pondDetailNum}->${item.targetPondDetailNum}(于${parseTime(item.createTime)}分池${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投喂记录" prop="workshopId" v-if="form.reviseType == 4">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择投喂记录" filterable clearable  @change="update_feed">
            <el-option v-for="(item, index) in realList_feed" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(于${parseTime(item.createTime)}投喂${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="存塘记录" prop="workshopId" v-if="form.reviseType == 5">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择存塘记录" filterable clearable  @change="update_pond">
            <el-option v-for="(item, index) in realList_pond" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="原数据" prop="originData">
          <el-input v-model="form.originData" placeholder="请输入原数据" />
        </el-form-item>
        <el-form-item label="差值" prop="differenceData">
          <el-input v-model="form.differenceData" placeholder="请输入差值" />
        </el-form-item>
        <el-form-item label="变化比例" prop="changeRatio">
          <el-input v-model="form.changeRatio" placeholder="请输入变化比例" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" placeholder="请输入原因" />
        </el-form-item>
        <el-form-item label="技术总监意见" prop="technicalDirectorsOpinion">
          <el-input v-model="form.technicalDirectorsOpinion" placeholder="请输入技术总监意见" />
        </el-form-item>
        <el-form-item label="厂长意见" prop="directorsOpinion">
          <el-input v-model="form.directorsOpinion" placeholder="请输入厂长意见" />
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
import { createZkDataRevise, updateZkDataRevise, deleteZkDataRevise, getZkDataRevise, getZkDataRevisePage, exportZkDataReviseExcel } from "@/api/fish/zkDataRevise";
import Editor from '@/components/Editor';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { createZkFeedingLog, updateZkFeedingLog, deleteZkFeedingLog, getZkFeedingLog, getZkFeedingLogPage, exportZkFeedingLogExcel } from "@/api/fish/zkFeedingLog";
import { createZkPondFishDetail, updateZkPondFishDetail, deleteZkPondFishDetail, getZkPondFishDetail, getZkPondFishDetailPage, exportZkPondFishDetailExcel } from "@/api/fish/zkPondFishDetail";

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
      pondList: [],
      workshopList: [], 
      pondSystemList: [], 
      systemList: [], 
      userList1: [],
      realList_push: [], // 投放真实数据list
      realList_dead: [], // 死亡真实数据list
      realList_split: [], // 分池真实数据list
      realList_feed: [], // 投喂真实数据list
      realList_pond: [], // 存塘真实数据list
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
  },
  methods:{
    // 操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)
    update_reviseType(reviseType) {
      // console.log(`=========>a:${reviseType}`);
      let queryParams = {
        pageNo: 1,
        pageSize: 500,
        addType: "1", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
      };
      if(reviseType == 1) { // 投放
        queryParams.addType = "1";
        this.getFishDetailList(reviseType, queryParams);
      } else if(reviseType == 2) { // 死亡
        queryParams.addType = "-2";
        this.getFishDetailList(reviseType, queryParams);
      } else if(reviseType == 3) { // 分池
        queryParams.addType = "-3";
        this.getFishDetailList(reviseType, queryParams);
      } else if(reviseType == 4) { // 投喂
        queryParams.addType = undefined;
        this.getFeedList(queryParams);
      } else if(reviseType == 5) { // 存塘
        // 开始的时候就已经获取
      }
    },
    getForm(){
      getZkDataRevise(this.businessKey).then(response => {
        this.form = response.data
        this.update_reviseType(this.form.reviseType);
        console.log(`form:${JSON.stringify(this.form)}`);
        
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      })
    },
    update_push(busId) {
      let currItem = null;
      this.realList_push.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.fishPush = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_dead(busId) {
      let currItem = null;
      this.realList_dead.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.fishDead = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_split(busId) {
      let currItem = null;
      this.realList_split.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.fishSplit = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_feed(busId) {
      let currItem = null;
      this.realList_feed.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.feedCount;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.feedingLog = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_pond(busId) {
      let currItem = null;
      this.realList_pond.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.pondData = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    updateOther(item) {
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.fishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
    },
    getFeedList(queryParams) {
      // 执行查询
      getZkFeedingLogPage(queryParams).then(response => {
        this.realList_feed = response.data.list;
      });
    },
    getFishDetailList(reviseType, queryParams) {
      // 执行查询
      getZkPondFishDetailPage(queryParams).then(response => {
        if(reviseType == 1) { // 投放
          this.realList_push = response.data.list;
        } else if(reviseType == 2) { // 死亡
          this.realList_dead = response.data.list;
        } else if(reviseType == 3) { // 分池
          this.realList_split = response.data.list;
        }
      });
    },
    submitForm(data, callback){
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        this.form.taskParam = data.taskParam;
        this.form.action = this.taskKey;
        if(this.form.action == 'Activity_10rgkz5'){ // 技术总监 意见
          if(!this.form.technicalDirectorsOpinion){
            this.form.technicalDirectorsOpinion = data.taskParam.approveMessage;
          }
        }
        if(this.form.action == 'Activity_1b5hb88'){ // 厂长意见
          if(!this.form.directorsOpinion){
            this.form.directorsOpinion = data.taskParam.approveMessage;
          }
        }
        console.log(data);
        // console.log(`form=============>${JSON.stringify(this.form)}`);
        // return;
        
        updateZkDataRevise(this.form).then(response => {
          this.$modal.msgSuccess('审批成功')
          // console.log(`========>callback:${callback}`);
          callback();
        })
      });
    },
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.fishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      // console.log(`==============>getPondList 1 `);
      getZkPondPage(params).then(response => {
        this.realList_pond = response.data.list;
        this.pondList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log(`getZkPondPage==================>${JSON.stringify(this.pondList)}`);
        this.getWorkshopList();
      })
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
