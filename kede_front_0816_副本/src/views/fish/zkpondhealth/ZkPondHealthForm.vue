<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <!-- <el-form-item label="存塘id" prop="pondId">
          <el-input v-model="formData.pondId" placeholder="请输入存塘id" />
        </el-form-item> -->
        <el-form-item label="鱼池编号" prop="pondId">
          <el-select style="width:100%;" v-model="formData.pondId" placeholder="请选择鱼池编号" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="环境层评分" prop="environmentalLayerRating">
          <el-input v-model="formData.environmentalLayerRating" placeholder="请输入环境层评分" />
        </el-form-item>
        <el-form-item label="行为层评分" prop="behavioralLayerRating">
          <el-input v-model="formData.behavioralLayerRating" placeholder="请输入行为层评分" />
        </el-form-item>
        <el-form-item label="机体层评分" prop="bodyLayerRating">
          <el-input v-model="formData.bodyLayerRating" placeholder="请输入机体层评分" />
        </el-form-item>
        <el-form-item label="综合评分" prop="overallRating">
          <el-input v-model="formData.overallRating" placeholder="请输入综合评分" />
        </el-form-item>
        <el-form-item label="环境层信息" prop="environmentalLayerInformation">
          <el-input v-model="formData.environmentalLayerInformation" placeholder="请输入环境层信息" />
        </el-form-item>
        <el-form-item label="行为层信息" prop="behavioralLayerInformation">
          <el-input v-model="formData.behavioralLayerInformation" placeholder="请输入行为层信息" />
        </el-form-item>
        <el-form-item label="机体层信息" prop="bodyLayerInformation">
          <el-input v-model="formData.bodyLayerInformation" placeholder="请输入机体层信息" />
        </el-form-item>
        <el-form-item label="异常信息" prop="exceptionMessage">
          <el-input v-model="formData.exceptionMessage" placeholder="请输入异常信息" />
        </el-form-item>
        <el-form-item label="异常个数" prop="numberOfExceptions">
          <el-input v-model="formData.numberOfExceptions" placeholder="请输入异常个数" />
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
import * as ZkPondHealthApi from '@/api/fish/zkpondhealth/index.js';
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { UserInfoKey } from "@/utils/constants";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";

export default {
  name: "ZkPondHealthForm",
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
        pondId: undefined,
        environmentalLayerRating: undefined,
        behavioralLayerRating: undefined,
        bodyLayerRating: undefined,
        overallRating: undefined,
        environmentalLayerInformation: undefined,
        behavioralLayerInformation: undefined,
        bodyLayerInformation: undefined,
        exceptionMessage: undefined,
        numberOfExceptions: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
      },
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getUserList();
    this.getPondList();
  },
  methods: {
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
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log(`========================>${JSON.stringify(this.pondList)}`);
      })
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
      this.formData.userId = item.id;
      this.formData.deptId = item.deptId;
      this.formData.userName = item.nickname;
      console.log("===========>"+JSON.stringify(item));
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          // let tempStr = `${obj.deptName}-${obj.nickname}`;
          let tempStr = `${obj.nickname}`;
          obj.nickname = tempStr;
        })
        // console.log("getUserList===========>"+JSON.stringify(this.userList1));
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
          const res = await ZkPondHealthApi.getZkPondHealth(id);
          this.formData = res.data;
          this.title = "修改鱼池健康";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增鱼池健康";
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
          await ZkPondHealthApi.updateZkPondHealth(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkPondHealthApi.createZkPondHealth(data);
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
        environmentalLayerRating: undefined,
        behavioralLayerRating: undefined,
        bodyLayerRating: undefined,
        overallRating: undefined,
        environmentalLayerInformation: undefined,
        behavioralLayerInformation: undefined,
        bodyLayerInformation: undefined,
        exceptionMessage: undefined,
        numberOfExceptions: undefined,
        deptId: this.userInfo.deptId,
        tenantId: this.userInfo.tenantId,
      };
      this.resetForm("formRef");
    }
  }
};
</script>