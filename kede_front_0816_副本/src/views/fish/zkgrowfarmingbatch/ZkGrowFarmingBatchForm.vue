<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="鱼类品种" prop="speciesId">
          <el-select style="width:100%;" v-model="formData.speciesId" placeholder="请选择鱼类品种" filterable clearable  @change="updateGrowFishSpeciesId">
            <el-option v-for="(item, index) in growFishSpeciesList" :key="item.id+'__'+index" :label="item.scientificName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="批次编号" prop="batchNo">
          <el-input v-model="formData.batchNo" placeholder="请输入批次编号" />
        </el-form-item>
        <!-- <el-form-item label="鱼类品种ID" prop="speciesId">
          <el-input v-model="formData.speciesId" placeholder="请输入鱼类品种ID" />
        </el-form-item> -->
        <el-form-item label="投苗日期" prop="startDate">
          <el-date-picker clearable v-model="formData.startDate" type="date" value-format="timestamp" style="width:100%;"
            placeholder="选择投苗日期" />
        </el-form-item>
        <el-form-item label="初始投放数量" prop="initialCount">
          <el-input v-model="formData.initialCount" placeholder="请输入初始投放数量" />
        </el-form-item>
        <el-form-item label="当前存栏数量" prop="currentCount">
          <el-input v-model="formData.currentCount" placeholder="请输入当前存栏数量" />
        </el-form-item>
        <!-- <el-form-item label="状态 (1:进行中, 2:已收成)" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_GROW_FARMING_BATCH_STATUS)" :key="dict.value"
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
import * as ZkGrowFarmingBatchApi from '@/api/fish/zkgrowfarmingbatch';
import * as ZkGrowFishSpeciesApi from '@/api/fish/zkgrowfishspecies';
import { UserInfoKey } from "@/utils/constants";
export default {
  name: "ZkGrowFarmingBatchForm",
  components: {
  },
  data() {
    return {
      userInfo: {},
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        batchNo: undefined,
        speciesId: undefined,
        startDate: undefined,
        initialCount: undefined,
        currentCount: undefined,
        status: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        batchNo: [{ required: true, message: '批次编号不能为空', trigger: 'blur' }],
      },
      growFishSpeciesList: [], 
    };
  },
  created() {
    this.get_GrowFishSpeciesList();
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
  },
  methods: {
    updateGrowFishSpeciesId(currId){
      let item = null;
      this.growFishSpeciesList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.growFishSpeciesName = item.growFishSpeciesName;
      this.formData.growFishSpeciesId = item.id;
      // console.log("===========>"+JSON.stringify(this.form));
    },
    get_GrowFishSpeciesList() {
      this.growFishSpeciesList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      ZkGrowFishSpeciesApi.getZkGrowFishSpeciesPage(params).then(response => {
        this.growFishSpeciesList.push(...response.data.list)
        // this.growFishSpeciesList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
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
          const res = await ZkGrowFarmingBatchApi.getZkGrowFarmingBatch(id);
          this.formData = res.data;
          this.title = "修改养殖批次";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增养殖批次";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        if(!this.formData.tenantId) this.formData.tenantId = this.userInfo.tenantId;
        if(!this.formData.deptId) this.formData.deptId = this.userInfo.deptId;
        if(!this.formData.userId) this.formData.userId = this.userInfo.userId;
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await ZkGrowFarmingBatchApi.updateZkGrowFarmingBatch(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkGrowFarmingBatchApi.createZkGrowFarmingBatch(data);
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
        batchNo: undefined,
        speciesId: undefined,
        startDate: undefined,
        initialCount: undefined,
        currentCount: undefined,
        status: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>
