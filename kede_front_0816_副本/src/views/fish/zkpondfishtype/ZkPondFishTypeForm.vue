<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <!-- <el-form-item label="存塘id" prop="pondId">
          <el-input v-model="formData.pondId" placeholder="请输入存塘id" />
        </el-form-item> -->
        <el-form-item label="批次编号" prop="batchNumber">
          <el-select style="width:100%;" v-model="formData.batchNumber" placeholder="请选择批次编号" filterable clearable  @change="updateBatchNumerId">
            <el-option v-for="(item, index) in bachchNumberList" :key="item.id+'__'+index" :label="`${item.info}`"
              :value="item.relationOrderNo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="存塘" prop="pondId">
          <el-select style="width:100%;" v-model="formData.pondId" placeholder="请选择存塘" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="批次编号" prop="batchNumber">
          <el-input v-model="formData.batchNumber" placeholder="请输入批次编号" />
        </el-form-item>
        <el-form-item label="鱼苗数量" prop="fishNum">
          <el-input v-model="formData.fishNum" placeholder="请输入鱼苗数量" />
        </el-form-item>
        <el-form-item label="鱼苗规格" prop="fishSpec">
          <el-input v-model="formData.fishSpec" placeholder="请输入鱼苗规格" />
        </el-form-item>
        <el-form-item label="统计年" prop="staticYear">
          <el-input v-model="formData.staticYear" placeholder="请输入统计年" />
        </el-form-item>
        <el-form-item label="统计月份" prop="staticMonth">
          <el-input v-model="formData.staticMonth" placeholder="请输入统计月份" />
        </el-form-item>
        <el-form-item label="统计日期" prop="staticDay">
          <el-input v-model="formData.staticDay" placeholder="请输入统计日期" />
        </el-form-item>
        <!-- <el-form-item label="鱼苗种类" prop="fishType">
          <el-select v-model="formData.fishType" placeholder="请选择鱼苗种类">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="鱼苗种类" prop="fishType">
          <el-select v-model="formData.fishType" placeholder="请输入鱼苗种类" filterable clearable size="small" style="width: 100%;"> 
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="formData.deptId" placeholder="请输入所属部门" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkPondFishTypeApi from '@/api/fish/zkpondfishtype';
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { listForBatch } from "@/api/erp/purchaseIn";

export default {
  name: "ZkPondFishTypeForm",
  components: {
  },
  data() {
    return {
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        pondId: undefined,
        batchNumber: undefined,
        fishNum: undefined,
        fishSpec: undefined,
        staticYear: undefined,
        staticMonth: undefined,
        staticDay: undefined,
        fishType: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
      },
      pondList: [],
      bachchNumberList: [],
    };
  },
  created() {
    this.getPondList();
    this.getBatchNumerList();
  },
  methods: {
    updateBatchNumerId(currId){
      let item = null;
      this.bachchNumberList.forEach(obj => {
        if(obj.relationOrderNo == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.supplier = item.supplierName;
      this.formData.supplierAddress = item.supplierAddress;
      this.formData.supplierId = item.supplierId;
      this.formData.productNo = item.productNo;
      // this.formData.fishTypes = item.productName;
      this.formData.fishSpec = item.productSpec; // 后面updateProductId 会重新更新该字段
      this.formData.pondDetailNum = item.pondDetailNum;
      this.formData.pondScale = item.pondScale;
      this.updateProductId(this.formData.productNo);
      this.formData.remainingInventory = item.remainingInventory==-1?item.productCount:item.remainingInventory;
      // console.log("===========>"+JSON.stringify(this.form));
      console.log("updateBatchNumerId obj ===========>"+JSON.stringify(item));
    },
    getBatchNumerList() {
      let queryParams = {
        pageNo: 1,
        pageSize: 10,
        orderNo: null,
        outKind: '1',
        productName: null,
        userId: null,
        categoryId: "7",
        createTime: [],
        oType: 1, // 订单类型(0:出库单;1:入库单;)
      };
      // 执行查询
      listForBatch(queryParams).then(response => {
        let list = response.data;
        list.forEach(item => {
          // 供应商地址
          // console.log(`getBatchNumerList====================>item:${JSON.stringify(item)}`);
          if(item.remainingInventory == -1) {
            item.remainingInventory = item.productCount;
          }
          item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName}-待投放${item.remainingInventory}条)`;
        })
        this.bachchNumberList = list.filter(obj => obj.remainingInventory>0);
        // console.log(`==============>${JSON.stringify(this.bachchNumberList)}`);
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
      this.formData.workshopName = item.workshopName;
      this.formData.workshopId = item.workshopId;
      this.formData.pondName = item.pondName;
      this.formData.pondNum = item.pondNum;
      this.formData.pondScale = item.pondScale;
      this.formData.historyFishNum = item.fishNum;
      this.formData.pondId = item.id;
      // this.formData.userName = item.userName;
      // this.formData.userId = item.userId;
      this.formData.pondDetailNum = item.pondDetailNum;
      this.formData.pondSystemName = item.pondSystemName;

      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      return getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
      })
    },
    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await ZkPondFishTypeApi.getZkPondFishType(id);
          this.formData = res.data;
          this.title = "修改鱼苗种类";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增鱼苗种类";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await ZkPondFishTypeApi.updateZkPondFishType(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkPondFishTypeApi.createZkPondFishType(data);
        this.$modal.msgSuccess("新增成功");
        this.dialogVisible = false;
        this.$emit('success');
      } finally {
        this.formLoading = false;
      }
    },
    /** 表单重置 */
    reset() {
      this.formData = {
        id: undefined,
        pondId: undefined,
        batchNumber: undefined,
        fishNum: undefined,
        fishSpec: undefined,
        staticYear: undefined,
        staticMonth: undefined,
        staticDay: undefined,
        fishType: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>