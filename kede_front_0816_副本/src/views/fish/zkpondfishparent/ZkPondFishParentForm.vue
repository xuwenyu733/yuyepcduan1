<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="80%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="批次编号" prop="batchNumber">
              <el-select style="width:100%;" v-model="formData.batchNumber" placeholder="请选择批次编号" filterable clearable @change="updateBatchNumerId">
                <el-option v-for="(item, index) in bachchNumberList" :key="item.id+'__'+index" :label="`${item.info}`"
                  :value="item.relationOrderNo"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原池编号" prop="originPondId">
              <el-select style="width:100%;" v-model="formData.originPondId" placeholder="请选择原池编号" filterable clearable
                @change="updatePondId">
                <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
                  :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="鱼苗种类" prop="productNo">
              <el-select v-model="formData.productNo" placeholder="请输入鱼苗种类" filterable clearable style="width: 100%;" @change="updateProductId">
                <el-option v-for="dict in productList" :key="dict.productNo"
                  :label="dict.info" :value="dict.productNo" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="原总数量" prop="fishNum">
              <el-input v-model="formData.fishNum" placeholder="请输入鱼苗数量" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="数量差距" prop="fishNumDiff">
              <el-input v-model="formData.fishNumDiff" placeholder="自动计算差距(正多负少)" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="投放类型" prop="addType">
              <el-select v-model="formData.addType" placeholder="请输入投放类型" filterable clearable style="width: 100%;"> 
                <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE)" :key="dict.value"
                  :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">分池明细</el-divider>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddDetail" style="margin-bottom: 10px;">添加分池目标</el-button>
        
        <el-table :data="formData.details" border style="width: 100%">
          <el-table-column label="目标池子" width="250">
            <template slot-scope="scope">
              <el-select v-model="scope.row.targetPondId" placeholder="请选择目标池子" filterable clearable @change="(val) => handleTargetPondChange(val, scope.row)">
                <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
                  :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
                  :value="item.id"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="目标规格(g/尾)">
            <template slot-scope="scope">
              <el-input v-model="scope.row.targetFishSpec" placeholder="规格" @input="calculateDetailFishNum(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="分池重量(kg)">
            <template slot-scope="scope">
              <el-input v-model="scope.row.targetWeight" placeholder="重量" @input="calculateDetailFishNum(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="分池数量(尾)">
            <template slot-scope="scope">
              <el-input v-model="scope.row.fishNum" placeholder="数量" @input="calculateDiff" />
            </template>
          </el-table-column>
          <el-table-column label="备注">
            <template slot-scope="scope">
              <el-input v-model="scope.row.remark" placeholder="备注" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="scope">
              <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="handleDeleteDetail(scope.$index)"></el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-form-item label="备注" prop="remark" style="margin-top: 20px;">
          <el-input type="textarea" v-model="formData.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading" :loading="operationLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkPondFishParentApi from '@/api/fish/zkpondfishparent';
import { DICT_TYPE } from "@/utils/dict";
import { getZkPondPage } from "@/api/fish/zkPond";
import { getProductPage } from "@/api/erp/product";
import { UserInfoKey } from "@/utils/constants";
import { listForBatch } from "@/api/erp/purchaseIn";
import { listSimpleUsersByRole } from "@/api/system/user";
import { KE_DE_ROLES } from "@/utils/constants";

export default {
  name: "ZkPondFishParentForm",
  data() {
    return {
      operationLoading: false,
      dialogTitle: "",
      dialogVisible: false,
      formLoading: false,
      formData: {
        id: undefined,
        originPondId: undefined,
        fishTypes: undefined,
        productNo: undefined,
        fishName: undefined,
        fishNum: 0,
        fishNumDiff: 0,
        addType: "-3",
        remark: undefined,
        deptId: undefined,
        batchNumber: undefined,
        details: []
      },
      userInfo: {},
      pondList: [],
      bachchNumberList: [],
      productList: [],
      userList1: [],
      formRules: {
        batchNumber: [{ required: true, message: '批次不能为空', trigger: 'change' }],
        originPondId: [{ required: true, message: '原池不能为空', trigger: 'change' }],
        fishNum: [{ required: true, message: '原总数量不能为空', trigger: 'blur' }],
        addType: [{ required: true, message: '投放类型不能为空', trigger: 'change' }],
      },
    };
  },
  watch: {
    'formData.fishNum': {
      handler() { this.calculateDiff(); }
    }
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.init();
  },
  methods: {
    init() {
      this.getProductList();
      this.getPondList();
      this.getBatchNumerList();
      this.getUserList();
    },
    getUserList() {
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1 = response.data.map(obj => ({
          ...obj,
          nickname: `${obj.deptName}-${obj.nickname}`
        }));
      });
    },
    handleAddDetail() {
      this.formData.details.push({
        targetPondId: undefined,
        targetFishSpec: this.formData.fishSpec || '',
        targetWeight: undefined,
        fishNum: 0,
        remark: '',
        // 携带主表信息
        batchNumber: this.formData.batchNumber,
        pondId: this.formData.originPondId,
        fishTypes: this.formData.fishTypes,
        productNo: this.formData.productNo,
        fishName: this.formData.fishName,
        addType: this.formData.addType,
        userId: this.userInfo.userId,
        deptId: this.userInfo.deptId
      });
    },
    handleDeleteDetail(index) {
      this.formData.details.splice(index, 1);
      this.calculateDiff();
    },
    handleTargetPondChange(val, row) {
      const pond = this.pondList.find(p => p.id === val);
      if (pond) {
        row.targetPondName = pond.pondName;
        row.targetPondNum = pond.pondNum;
        row.targetPondScale = pond.pondScale;
        row.targetHistoryFishNum = pond.fishNum;
        row.targetPondDetailNum = pond.pondDetailNum;
        row.targetPondSystemName = pond.pondSystemName;
      }
    },
    calculateDetailFishNum(row) {
      let tempSpec = this.my_common.selectNumberByStr(row.targetFishSpec);
      // console.log(`calculateDetailFishNum=======tempSpec:${tempSpec}==================>row:${JSON.stringify(row)}`);
      if (row.targetWeight && tempSpec && tempSpec > 0) {
        // 重量(kg) * 1000 / 规格(g/尾) = 数量(尾)
        row.fishNum = Math.round((parseFloat(row.targetWeight) * 1000) / parseFloat(tempSpec));
      }
      this.calculateDiff();
    },
    calculateDiff() {
      const totalSplit = this.formData.details.reduce((sum, d) => sum + (parseInt(d.fishNum) || 0), 0);
      this.formData.fishNumDiff = -(parseInt(this.formData.fishNum) || 0) + totalSplit;
    },
    updateProductId(currId){
      const item = this.productList.find(obj => obj.productNo === currId);
      if (item) {
        this.formData.fishName = item.productName;
        this.formData.fishTypes = item.fishTypes;
        this.formData.fishSpec = item.standard;
        this.formData.productNo = item.productNo;
      }
    },
    getProductList() {
      getProductPage({ pageNo: 1, pageSize: 500, categoryId: 7 }).then(response => {
        this.productList = response.data.list.map(obj => ({
          ...obj,
          info: `${obj.productName}` // -${obj.productBrand}
        }));
      });
    },
    updateBatchNumerId(currId){
      // console.log(`updateBatchNumerId============================>updateBatchNumerId:${currId}`);
      const item = this.bachchNumberList.find(obj => obj.relationOrderNo === currId);
      // console.log(`updateBatchNumerId============================>${JSON.stringify(item)}`);
      if (item) {
        this.formData.batchNumber = item.relationOrderNo;
        this.formData.productNo = item.productNo;
        this.formData.fishSpec = item.productSpec;
        this.updateProductId(this.formData.productNo);
      }
    },
    getBatchNumerList() {
      listForBatch({ pageNo: 1, pageSize: 50, categoryId: "7", oType: 1 }).then(response => {
        this.bachchNumberList = response.data.map(item => ({
          ...item,
          info: `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName})`
        }));
      });
    },
    updatePondId(currId){
      const item = this.pondList.find(obj => obj.id === currId);
      if (item) {
        this.formData.originPondId = item.id;
        this.formData.pondName = item.pondName;
        this.formData.pondNum = item.pondNum;
        this.formData.pondScale = item.pondScale;
        this.formData.fishNum = item.fishNum; // 默认原池总数
        this.formData.pondDetailNum = item.pondDetailNum;
        this.formData.pondSystemName = item.pondSystemName;
        this.formData.workshopId = item.workshopId;
        this.formData.workshopName = item.workshopName;
      }
    },
    getPondList() {
      return getZkPondPage({ pageNo: 1, pageSize: 500 }).then(response => {
        this.pondList = response.data.list;
      });
    },
    calculateSpec(targetWeight, targetFishNum, targetPondId) {
      // debugger
      const item = this.pondList.find(obj => obj.id === targetPondId);
      // 1. 计算 分过来的重量g
      let t1 = this.my_common.computer(targetWeight, 1000, 2, 2);
      // 2. 计算总鱼数
      let t2 = this.my_common.computer(targetFishNum, item.fishNum, 0, 2);
      // 3. 计算 目标池子的重量
      let t4 = this.my_common.computer(item.fishNum, item.fishSpec, 2, 2);
      // 4. 计算 目标池子+分过来的重量和
      let t5 = this.my_common.computer(t1, t4, 0, 2);
      // 5. 计算 avg fishSpec
      let t6 = this.my_common.computer(t5, t2, 3, 2);
      return t6;
    },
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      if (id) {
        this.dialogTitle = "修改分池";
        this.formLoading = true;
        try {
          const res = await ZkPondFishParentApi.getZkPondFishParent(id);
          this.formData = res.data;
          // 后端返回的 details 需要处理下，确保包含必要的上下文
          if (this.formData.details) {
            this.formData.details.forEach(d => {
              d.userId = d.userId || this.userInfo.userId;
            });
          }
        } finally {
          this.formLoading = false;
        }
      } else {
        this.dialogTitle = "新增分池";
      }
    },
    async submitForm() {
      await this.$refs["formRef"].validate();
      if (this.formData.details.length === 0) {
        this.$message.error("请至少添加一个分池明细");
        return;
      }
      let duplicates = this.formData.details.filter((item, index, self) => {
          return self.findIndex(i => i.targetPondId === item.targetPondId && i !== item) !== -1;
      });
      if(duplicates.length > 0) {
        this.$message.error(`${duplicates[0].targetPondDetailNum}重复添加`);
        return;
      }
      
      // 检查是否有分到自己的逻辑,将分到自己的情况排到最后
      const selfSplit = this.formData.details.find(d => d.targetPondId === this.formData.originPondId);
      if (selfSplit) {
        let arr = this.formData.details.filter(d => d.targetPondId != this.formData.originPondId)
        arr.push(selfSplit);
        this.formData.details = arr;
      }

      this.formLoading = true;
      if(this.operationLoading) {
        this.$modal.msgWarning("不能重复请求！");
        return;
      }
      this.operationLoading = true;
      try {
        let historyFishNum = this.formData.fishNum;
        this.formData.details.forEach(item => {
          item.historyFishNum = historyFishNum;
          historyFishNum -= item.fishNum;
          if(item.targetPondId != this.formData.originPondId) {
            // 存塘不是自己则需要计算avg规格 
            // 1. 将当前分池重量、分池数量和当前存塘id传入获取avg规格的方法
            item.splitAvgFishSpec = this.calculateSpec(item.targetWeight, item.fishNum, item.targetPondId);
          } else {
            // item.splitAvgFishSpec = item.targetFishSpec;
            item.splitAvgFishSpec = this.my_common.selectNumberByStr(item.targetFishSpec);
          }
        })
        const data = {
          ...this.formData,
          tenantId: this.userInfo.tenantId,
          deptId: this.formData.deptId || this.userInfo.deptId,
          // 确保明细中也有必要的字段
          details: this.formData.details.map(d => ({
            ...d,
            pondId: this.formData.originPondId,
            pondName: this.formData.pondName,
            pondNum: this.formData.pondNum,
            pondDetailNum: this.formData.pondDetailNum,
            pondSystemName: this.formData.pondSystemName,
            workshopId: this.formData.workshopId,
            workshopName: this.formData.workshopName,
            fishTypes: this.formData.fishTypes,
            productNo: this.formData.productNo,
            fishName: this.formData.fishName,
            fishSpec: this.formData.fishSpec,
            batchNumber: this.formData.batchNumber,
            addType: this.formData.addType,
            userId: d.userId || this.userInfo.userId,
            deptId: d.deptId || this.userInfo.deptId
          }))
        };

        // console.log(`submitForm==============>${JSON.stringify(data)}`);
        // return;
        if (data.id) {
          await ZkPondFishParentApi.updateZkPondFishParent(data);
          this.$modal.msgSuccess("修改成功");
        } else {
          await ZkPondFishParentApi.createZkPondFishParent(data);
          this.$modal.msgSuccess("新增成功");
        }
        this.dialogVisible = false;
        this.$emit('success');
      } finally {
        this.formLoading = false;
        setTimeout(()=>{
          this.operationLoading = false;
        }, 1000);
      }
    },
    reset() {
      this.formData = {
        id: undefined,
        originPondId: undefined,
        fishTypes: undefined,
        productNo: undefined,
        fishName: undefined,
        fishNum: 0,
        fishNumDiff: 0,
        addType: "-3",
        remark: undefined,
        deptId: undefined,
        batchNumber: undefined,
        details: []
      };
      if (this.$refs.formRef) this.$refs.formRef.resetFields();
    }
  }
};
</script>
