<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="160px">
        <!-- <el-form-item label="所属品种ID" prop="speciesId">
          <el-input v-model="formData.speciesId" placeholder="请输入所属品种ID" />
        </el-form-item> -->
        <el-form-item label="鱼类品种" prop="speciesId">
          <el-select style="width:100%;" v-model="formData.speciesId" placeholder="请选择鱼类品种" filterable clearable  @change="updateGrowFishSpeciesId">
            <el-option v-for="(item, index) in growFishSpeciesList" :key="item.id+'__'+index" :label="item.scientificName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="formData.modelName" placeholder="请输入模型名称" />
        </el-form-item>
        <el-form-item label="极限体长 (cm)" prop="lInf">
          <el-input v-model="formData.lInf" placeholder="请输入极限体长 (cm)" />
        </el-form-item>
        <el-form-item label="极限体重 (g)" prop="wInf">
          <el-input v-model="formData.wInf" placeholder="请输入极限体重 (g)" />
        </el-form-item>
        <el-form-item label="体长生长系数" prop="kLength">
          <el-input v-model="formData.kLength" placeholder="请输入体长生长系数" />
        </el-form-item>
        <el-form-item label="体重生长系数" prop="kWeight">
          <el-input v-model="formData.kWeight" placeholder="请输入体重生长系数" />
        </el-form-item>
        <el-form-item label="初始体重 (g)" prop="initialWeight">
          <el-input v-model="formData.initialWeight" placeholder="请输入初始体重 (g)" />
        </el-form-item>
        <el-form-item label="初始体长 (cm)" prop="initialLength">
          <el-input v-model="formData.initialLength" placeholder="请输入初始体长 (cm)" />
        </el-form-item>
        <el-form-item label="最适水温最小值" prop="optimalTempMin">
          <el-input v-model="formData.optimalTempMin" placeholder="请输入最适水温最小值" />
        </el-form-item>
        <el-form-item label="最适水温最大值" prop="optimalTempMax">
          <el-input v-model="formData.optimalTempMax" placeholder="请输入最适水温最大值" />
        </el-form-item>
        <el-form-item label="最适投饵率 (%)" prop="optimalFeedRate">
          <el-input v-model="formData.optimalFeedRate" placeholder="请输入最适投饵率 (%)" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkGrowModelConfigApi from '@/api/fish/zkgrowmodelconfig';
import * as ZkGrowFishSpeciesApi from '@/api/fish/zkgrowfishspecies';
import { UserInfoKey } from "@/utils/constants";
export default {
  name: "ZkGrowModelConfigForm",
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
        speciesId: undefined,
        modelName: undefined,
        lInf: undefined,
        wInf: undefined,
        kLength: undefined,
        kWeight: undefined,
        initialWeight: undefined,
        initialLength: undefined,
        optimalTempMin: undefined,
        optimalTempMax: undefined,
        optimalFeedRate: undefined,
      },
      // 表单校验
      formRules: {
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
          const res = await ZkGrowModelConfigApi.getZkGrowModelConfig(id);
          this.formData = res.data;
          this.title = "修改生长模型配置";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增生长模型配置";
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
          await ZkGrowModelConfigApi.updateZkGrowModelConfig(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkGrowModelConfigApi.createZkGrowModelConfig(data);
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
        speciesId: undefined,
        modelName: undefined,
        lInf: undefined,
        wInf: undefined,
        kLength: undefined,
        kWeight: undefined,
        initialWeight: undefined,
        initialLength: undefined,
        optimalTempMin: undefined,
        optimalTempMax: undefined,
        optimalFeedRate: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>
