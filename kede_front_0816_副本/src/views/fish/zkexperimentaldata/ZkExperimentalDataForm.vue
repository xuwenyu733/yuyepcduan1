<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <!-- <el-form-item label="实验id" prop="experimentId">
          <el-input v-model="formData.experimentId" placeholder="请输入实验id" />
        </el-form-item> -->
        <el-form-item label="实验" prop="experimentalId">
          <el-select style="width:100%;" v-model="formData.experimentId" placeholder="请选择实验" filterable clearable  @change="update_ExperimentalId">
            <el-option v-for="(item, index) in experimentalList" :key="item.id+'__'+index" :label="item.experimentTitle"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="池号" prop="poolId">
          <el-select style="width:100%;" v-model="formData.poolId" placeholder="请选择池号" filterable clearable @change="update_PoolId">
            <el-option v-for="(item, index) in currentPoolList" :key="item.id+'__'+index" :label="item.poolNumber"
              :value="item.poolId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="采集类型" prop="collectionType">
          <el-select v-model="formData.collectionType" placeholder="请选择采集类型">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="采集类型" prop="collectionType">
          <el-select v-model="formData.collectionType" placeholder="请输入采集类型" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="(dict, index) in collectionTypeList" :key="dict.value+'_33_'+index"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="采集数据" prop="collectData">
          <div v-if="collectSummary" class="collect-summary">{{ collectSummary }}</div>
          <el-input v-model="formData.collectData" type="textarea" :rows="6" placeholder="结构化数据由采集页写入；此处仅在必要时维护" />
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
import * as ZkExperimentalDataApi from '@/api/fish/zkexperimentaldata';
import * as ZkExperimentalApi from '@/api/fish/zkexperimental';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import Editor from '@/components/Editor';
export default {
  name: "ZkExperimentalDataForm",
  components: {
    Editor,
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
        experimentId: undefined,
        experimentNumber: undefined,
        poolId: undefined,
        poolNumber: undefined,
        collectionType: undefined,
        collectData: undefined,
        deptId: undefined,
        tenantId: undefined,
      },
      // 表单校验
      formRules: {
      },
      experimentalList: [], 
      collectionTypeList: [], 
      currentPoolList: [],
    };
  },
  created() {
    this.collectionTypeList = this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_TAB_JSON);
    this.get_ExperimentalList();
    // console.log(`collectionTypeList:=================>${JSON.stringify(this.collectionTypeList)}`)
  },
  computed: {
    collectSummary() {
      const dataStr = this.formData && this.formData.collectData
      if (dataStr == null || dataStr === '') return ''
      try {
        const data = typeof dataStr === 'string' ? JSON.parse(dataStr) : dataStr
        if (Array.isArray(data)) {
          return data.map(item => {
            const key = item.keyCh || item.key || item.name || ''
            const val = item.val != null ? item.val : (item.value != null ? item.value : '')
            return key ? `${key}: ${val}` : String(val)
          }).filter(Boolean).join('；')
        }
        if (data && typeof data === 'object') {
          return Object.keys(data).map(k => `${k}: ${data[k]}`).join('；')
        }
        return String(data)
      } catch {
        return ''
      }
    }
  },
  methods: {
    update_ExperimentalId(currId){
      let item = null;
      this.experimentalList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.experimentTitle = item.experimentTitle;
      this.formData.experimentalId = item.id;
      this.formData.tenantId = item.tenantId;
      this.formData.deptId = item.deptId;
      this.formData.experimentNumber = item.experimentNumber;
      // 设置当前实验关联的池子列表
      this.currentPoolList = [];
      if (item.poolIds && item.poolIds.length > 0) {
        for (let i = 0; i < item.poolIds.length; i++) {
          this.currentPoolList.push({
            poolId: item.poolIds[i],
            poolNumber: item.poolNumbers[i]
          });
        }
      }
      console.log("===========>"+JSON.stringify(this.form));
    },
    update_PoolId(poolId) {
      let item = this.currentPoolList.find(obj => obj.poolId == poolId);
      if (item) {
        this.formData.poolNumber = item.poolNumber;
      }
    },
    get_ExperimentalList() {
      this.experimentalList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      ZkExperimentalApi.getZkExperimentalPage(params).then(response => {
        this.experimentalList.push(...response.data.list)
        // this.experimentalList.forEach(obj => {
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
          const res = await ZkExperimentalDataApi.getZkExperimentalData(id);
          this.formData = res.data;
          this.title = "修改实验数据采集";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增实验数据采集";
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
          await ZkExperimentalDataApi.updateZkExperimentalData(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkExperimentalDataApi.createZkExperimentalData(data);
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
        experimentId: undefined,
        experimentNumber: undefined,
        poolId: undefined,
        poolNumber: undefined,
        collectionType: undefined,
        collectData: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>