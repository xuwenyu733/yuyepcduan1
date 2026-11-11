<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="实验编号" prop="experimentNumber">
          <el-input v-model="formData.experimentNumber" placeholder="请输入实验编号" />
        </el-form-item>
        <el-form-item label="实验标题" prop="experimentTitle">
          <el-input v-model="formData.experimentTitle" placeholder="请输入实验标题" />
        </el-form-item>
        <el-form-item label="池号" prop="poolIds">
          <el-select style="width:100%;" v-model="formData.poolIds" placeholder="请选择池号" filterable clearable multiple>
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.poolNumber}(${item.location})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="formData.personInChargeId" placeholder="请选择负责人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="品种" prop="variety">
                      <el-input v-model="formData.variety" placeholder="请输入品种" />
                    </el-form-item> -->
        <!-- <el-form-item label="负责人" prop="personInCharge">
                      <el-input v-model="formData.personInCharge" placeholder="请输入负责人" />
                    </el-form-item>
                    <el-form-item label="负责人id" prop="personInChargeId">
                      <el-input v-model="formData.personInChargeId" placeholder="请输入负责人id" />
                    </el-form-item> -->
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable v-model="formData.startTime" type="date" value-format="timestamp"
            placeholder="选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable v-model="formData.endTime" type="date" value-format="timestamp"
            placeholder="选择结束时间" />
        </el-form-item>
        <!-- (0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;) -->
        <!-- <el-form-item label="实验状态" prop="experimentalStatus">
          <el-radio-group v-model="formData.experimentalStatus">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="实验状态" prop="experimentalStatus">
          <el-select v-model="formData.experimentalStatus" placeholder="请输入实验状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_EXPERIMENTAL_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="实验目标" prop="experimentalObjective">
          <Editor v-model="formData.experimentalObjective" :min-height="192" />
          <!-- <el-input v-model="formData.experimentalObjective" placeholder="请输入实验目标" /> -->
        </el-form-item>
        <el-form-item label="实验内容">
          <Editor v-model="formData.experimentalContent" :min-height="192" />
        </el-form-item>
        <el-form-item label="实验方法" prop="experimentalMethod">
          <!-- <el-input v-model="formData.experimentalMethod" placeholder="请输入实验方法" /> -->
          <Editor v-model="formData.experimentalMethod" :min-height="192" />
        </el-form-item>
        <el-form-item label="主要结果" prop="mainResults">
          <Editor v-model="formData.mainResults" :min-height="192" />
        </el-form-item>
        <el-form-item label="效果评估" prop="effectEvaluation">
          <Editor v-model="formData.effectEvaluation" :min-height="192" />
        </el-form-item>
        <el-form-item label="完成人" prop="completedBy">
          <el-input v-model="formData.completedBy" placeholder="请输入完成人" />
        </el-form-item>
        <el-form-item label="完成时间" prop="completionTime">
          <el-date-picker clearable v-model="formData.completionTime" type="datetime" value-format="timestamp"
            placeholder="选择完成时间" />
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
import * as ZkExperimentalApi from '@/api/fish/zkexperimental';
import { createZkExperimentalPool, updateZkExperimentalPool, deleteZkExperimentalPool, getZkExperimentalPool, getZkExperimentalPoolPage } from "@/api/fish/zkexperimentalpool";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { UserInfoKey } from "@/utils/constants";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import Editor from '@/components/Editor';
export default {
  name: "ZkExperimentalForm",
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
        experimentNumber: undefined,
        experimentTitle: undefined,
        poolIds: [],
        variety: undefined,
        personInCharge: undefined,
        personInChargeId: undefined,
        startTime: undefined,
        endTime: undefined,
        experimentalStatus: undefined,
        experimentalObjective: undefined,
        experimentalContent: undefined,
        experimentalMethod: undefined,
        mainResults: undefined,
        effectEvaluation: undefined,
        completedBy: undefined,
        completionTime: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        startTime: [{ required: true, message: '开始时间不能为空', trigger: 'blur' }],
        endTime: [{ required: true, message: '结束时间不能为空', trigger: 'blur' }],
      },
      pondList: [],
      userList1: [],
      userInfo: {},
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`============>userInfo:${JSON.stringify(this.userInfo)}`);
    this.getPondList();
    this.getUserList();
    // this.dict_flushTypes = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES);
    // console.log("created=======>"+JSON.stringify(this.dict_flushTypes));
  },
  methods: {
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
      this.formData.personInCharge = item.nickname;
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
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.pondDetailNum == currId) {
          item = obj;
          return;
        }
      })
      console.log(`updatePondId===========>currId:${currId},item:${JSON.stringify(item)}`);
      // 2. 执行 赋值
      // this.formData.workshopName = item.workshopName;
      // this.formData.workshopId = item.workshopId;
      // this.formData.pondName = item.pondName;
      // this.formData.pondNum = item.pondNum;
      // this.formData.pondScale = item.pondScale;
      // this.formData.historyFishNum = item.fishNum;
      // this.formData.pondId = item.id;
      // this.formData.userName = item.userName;
      // this.formData.userId = item.userId;
      this.formData.pondDetailNum = item.pondDetailNum;
      // this.formData.pondSystemName = item.pondSystemName;

      console.log("===========>"+JSON.stringify(this.formData));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      return getZkExperimentalPoolPage(params).then(response => {
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
          const res = await ZkExperimentalApi.getZkExperimental(id);
          this.formData = res.data;
          this.title = "修改实验";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增实验";
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
          await ZkExperimentalApi.updateZkExperimental(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkExperimentalApi.createZkExperimental(data);
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
        experimentNumber: undefined,
        experimentTitle: undefined,
        poolIds: [],
        variety: undefined,
        personInCharge: undefined,
        personInChargeId: undefined,
        startTime: undefined,
        endTime: undefined,
        experimentalStatus: undefined,
        experimentalObjective: undefined,
        experimentalContent: undefined,
        experimentalMethod: undefined,
        mainResults: undefined,
        effectEvaluation: undefined,
        completedBy: undefined,
        completionTime: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>