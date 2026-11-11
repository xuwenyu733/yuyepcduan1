<template>
  <div class="experiment-form">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" v-loading="formLoading">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="实验编号" prop="experimentNumber">
            <el-input v-model="formData.experimentNumber" placeholder="例如: EXP-2024-001" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="实验标题" prop="experimentTitle">
            <el-input v-model="formData.experimentTitle" placeholder="例如: 饲料配方优化实验" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <!-- <el-form-item label="池号" prop="pondDetailNum">
            <el-input v-model="formData.pondDetailNum" placeholder="例如: A1, B2" />
          </el-form-item> -->
          <!-- 移除旧的多选池号逻辑，改为下方的多池详情管理 -->
        </el-col>
        <el-col :span="12">
          <el-form-item label="品种" prop="variety">
            <el-input v-model="formData.variety" placeholder="例如: 草鱼、鲤鱼" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <!-- <el-form-item label="负责人" prop="personInCharge">
            <el-input v-model="formData.personInCharge" placeholder="负责人姓名" />
          </el-form-item> -->
          <el-form-item label="负责人" prop="personInChargeId">
            <el-select style="width:100%;" v-model="formData.personInChargeId" placeholder="请选择负责人" filterable clearable
              @change="updateUserId">
              <el-option v-for="(item, index) in userList1" :key="item.id + '__' + index" :label="item.nickname"
                :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="实验状态" prop="experimentalStatus">
            <el-select v-model="formData.experimentalStatus" placeholder="请选择状态" style="width: 100%">
              <el-option label="待开始" :value="0" />
              <el-option label="计划中" :value="1" />
              <el-option label="进行中" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已总结" :value="4" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开始日期" prop="startTime">
            <el-date-picker v-model="formData.startTime" type="date" placeholder="选择开始日期" value-format="timestamp"
              style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束日期" prop="endTime">
            <el-date-picker v-model="formData.endTime" type="date" placeholder="选择结束日期" value-format="timestamp"
              style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="实验目标" prop="experimentalObjective">
        <el-input v-model="formData.experimentalObjective" type="textarea" :rows="3" placeholder="描述实验的主要目标和预期成果..." />
      </el-form-item>

      <el-form-item label="实验内容" prop="experimentalContent">
        <el-input v-model="formData.experimentalContent" type="textarea" :rows="4" placeholder="详细描述实验的具体内容..." />
      </el-form-item>

      <el-form-item label="实验方法" prop="experimentalMethod">
        <el-input v-model="formData.experimentalMethod" type="textarea" :rows="4" placeholder="描述实验的具体方法和步骤..." />
      </el-form-item>

      <el-form-item label="主要结果" prop="mainResults">
        <!-- <Editor v-model="formData.mainResults" :min-height="192" /> -->
        <el-input v-model="formData.mainResults" type="textarea" :rows="4" placeholder="描述实验的主要结果..." />
      </el-form-item>
      <el-form-item label="实验结论/效果评估" prop="effectEvaluation">
        <!-- <Editor v-model="formData.effectEvaluation" :min-height="192" /> -->
        <el-input v-model="formData.effectEvaluation" type="textarea" :rows="4" placeholder="填写实验结论或效果评估..." />
      </el-form-item>
      <el-form-item label="完成人" prop="completedBy">
        <el-input v-model="formData.completedBy" placeholder="请输入完成人" />
      </el-form-item>
      <el-form-item label="完成时间" prop="completionTime">
        <el-date-picker clearable v-model="formData.completionTime" type="datetime" value-format="timestamp"
          placeholder="选择完成时间" />
      </el-form-item>

      <!-- 多池管理 -->
      <div class="pool-management-section">
        <div class="section-header">
          <div class="header-left">
            <span class="title">多池管理</span>
            <span class="subtitle">配置多个实验池，支持设置实验组和对照组，便于数据采集时独立记录和对比分析</span>
          </div>
          <el-button type="primary" size="mini" @click="addPoolItem">
            <i class="el-icon-plus"></i> 添加池
          </el-button>
        </div>

        <div v-for="(item, index) in formData.poolDetails" :key="index" class="pool-card">
          <div class="pool-card-header">
            <span class="pool-index">池 {{ index + 1 }}</span>
            <el-button type="text" class="delete-btn" @click="removePoolItem(index)">
              <i class="el-icon-delete"></i> 删除此池
            </el-button>
          </div>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="池名称" :prop="'poolDetails.' + index + '.poolId'" :rules="{ required: true, message: '请选择池子', trigger: 'change' }">
                <el-select v-model="item.poolId" placeholder="请选择池子" filterable style="width: 100%" @change="(val) => handlePoolChange(val, index)">
                  <el-option v-for="pool in pondList" :key="pool.id" :label="pool.poolNumber" :value="pool.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="池类型" :prop="'poolDetails.' + index + '.poolType'" :rules="{ required: true, message: '请选择类型', trigger: 'change' }">
                <el-select v-model="item.poolType" placeholder="请选择类型" style="width: 100%">
                  <el-option label="实验组" value="1" />
                  <el-option label="对照组" value="2" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="初始放养数量" :prop="'poolDetails.' + index + '.initFishNum'">
                <el-input-number v-model="item.initFishNum" :min="0" placeholder="尾" style="width: 100%" controls-position="right" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="初始平均重量" :prop="'poolDetails.' + index + '.initAvgWeight'">
                <el-input-number v-model="item.initAvgWeight" :min="0" :precision="2" placeholder="g" style="width: 100%" controls-position="right" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="描述" :prop="'poolDetails.' + index + '.description'">
            <el-input v-model="item.description" type="textarea" :rows="2" placeholder="描述池的详细信息，如处理方式、特殊条件等..." />
          </el-form-item>
        </div>
      </div>

      <div class="dialog-footer">
        <el-button @click="submitCancel">取消</el-button>
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确定</el-button>
      </div>
    </el-form>

  </div>
</template>

<script>
import * as ZkExperimentalApi from '@/api/fish/zkexperimental'
import { UserInfoKey } from "@/utils/constants";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import * as ZkExperimentalPoolApi from '@/api/fish/zkexperimentalpool';
import Editor from '@/components/Editor';

export default {
  name: 'ExperimentForm',
  components: {
    Editor,
  },
  props: {
    experimentId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    return {
      formLoading: false,
      formData: {
        experimentNumber: '',
        experimentTitle: '',
        poolIds: [],
        poolDetails: [],
        variety: '',
        personInCharge: '',
        personInChargeId: null,
        startTime: null,
        endTime: null,
        experimentalStatus: 1,
        experimentalObjective: '',
        experimentalContent: '',
        experimentalMethod: '',
        mainResults: '',
        effectEvaluation: '',
        completedBy: '',
        completionTime: null
      },
      formRules: {
        experimentNumber: [{ required: true, message: '请输入实验编号', trigger: 'blur' }],
        experimentTitle: [{ required: true, message: '请输入实验标题', trigger: 'blur' }],
        variety: [{ required: true, message: '请输入品种', trigger: 'blur' }],
        personInChargeId: [{ required: true, message: '请选择负责人', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
        experimentalStatus: [{ required: true, message: '请选择实验状态', trigger: 'change' }],
        experimentalObjective: [{ required: true, message: '请输入实验目标', trigger: 'blur' }],
        experimentalContent: [{ required: true, message: '请输入实验内容', trigger: 'blur' }],
        experimentalMethod: [{ required: true, message: '请输入实验方法', trigger: 'blur' }]
      },
      userInfo: {},
      pondList: [],
      userList1: [],
      editType: 'add', // add edit
    }
  },
  watch: {
    experimentId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.loadData()
        } else {
          this.resetForm()
        }
      }
    }
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getPondList();
    this.getUserList();
    // console.log(`created=================>userInfo:${JSON.stringify(this.userInfo)}`);
  },
  methods: {
    updateUserId(userId) {
      // console.log(`updateUserId===================>${userId}`);
      let item = null;
      this.userList1.forEach(obj => {
        if (obj.id == userId) {
          item = obj;
          return;
        }
      })
      // console.log(`updateUserId===================>${JSON.stringify(item)}`);
      // 2. 执行 deptId、userId赋值
      this.formData.userId = item.id;
      this.formData.deptId = item.deptId;
      this.formData.personInCharge = item.nickname;
      this.formData.personInChargeId = item.id;
      // console.log("===========>" + JSON.stringify(item));
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
        this.updateUserId(this.userInfo.userId);
        // console.log("getUserList===========>"+JSON.stringify(this.userList1));
      })
    },
    updatePondId(currId) {
      let item = null;
      this.pondList.forEach(obj => {
        if (obj.pondDetailNum == currId) {
          item = obj;
          return;
        }
      })
      // console.log(`updatePondId===========>currId:${currId},item:${JSON.stringify(item)}`);
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
      // this.formData.pondDetailNum = item.pondDetailNum;
      // this.formData.pondSystemName = item.pondSystemName;

      // console.log("===========>" + JSON.stringify(this.formData));
    },
    async getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      const res = await ZkExperimentalPoolApi.getZkExperimentalPoolPage(params);
      this.pondList = res.data.list;
      console.log(`getPondList=============>${JSON.stringify(this.pondList)}`);
      // return getZkPondPage(params).then(response => {
      //   this.pondList.push(...response.data.list)
      // })
    },
    /** 加载数据 */
    async loadData() {
      if (!this.experimentId) {
        this.resetForm()
        return
      }
      try {
        this.formLoading = true
        const res = await ZkExperimentalApi.getZkExperimental(this.experimentId)
        if (res.data) {
          this.formData = {
            ...res.data,
            startTime: res.data.startTime ? new Date(res.data.startTime).getTime() : null,
            endTime: res.data.endTime ? new Date(res.data.endTime).getTime() : null,
            poolDetails: res.data.poolDetails || []
          }
        }
      } catch (error) {
        this.$modal.msgError('加载实验数据失败')
      } finally {
        this.formLoading = false
      }
    },

    /** 提交表单 */
    async submitForm() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return

        try {
          this.formLoading = true
          const data = {
            ...this.formData,
            startTime: this.formData.startTime ? new Date(this.formData.startTime).getTime() : null,
            endTime: this.formData.endTime ? new Date(this.formData.endTime).getTime() : null
          }

          if (this.experimentId) {
            data.id = this.experimentId
            await ZkExperimentalApi.updateZkExperimental(data)
            this.$modal.msgSuccess('修改成功')
          } else {
            await ZkExperimentalApi.createZkExperimental(data)
            this.$modal.msgSuccess('创建成功')
          }
          this.$emit('success')
        } catch (error) {
          // 错误已在拦截器中处理
        } finally {
          this.formLoading = false
        }
      })
    },

    submitCancel() {
      this.$emit('cancel')
    },

    /** 重置表单 */
    resetForm() {
      this.formData = {
        "userId": undefined,
        "deptId": undefined,
        "tenantId": undefined,
        experimentNumber: '',
        experimentTitle: '',
        poolIds: [],
        poolDetails: [],
        variety: '',
        personInCharge: '',
        personInChargeId: null,
        startTime: null,
        endTime: null,
        experimentalStatus: 1,
        experimentalObjective: '',
        experimentalContent: '',
        experimentalMethod: '',
        mainResults: '',
        effectEvaluation: '',
        completedBy: '',
        completionTime: null
      }
      
      this.formData.personInCharge = this.userInfo.nickname;
      this.formData.tenantId = this.userInfo.tenantId;
      this.formData.deptId = this.userInfo.deptId;
      this.formData.personInChargeId = this.userInfo.userId;
      if (this.$refs.formRef) {
        this.$refs.formRef.clearValidate()
      }
    },
    addPoolItem() {
      this.formData.poolDetails.push({
        poolId: undefined,
        poolNumber: '',
        poolType: '1',
        initFishNum: 0,
        initAvgWeight: 0,
        description: ''
      });
    },
    removePoolItem(index) {
      this.formData.poolDetails.splice(index, 1);
    },
    handlePoolChange(val, index) {
      const pool = this.pondList.find(p => p.id === val);
      if (pool) {
        this.formData.poolDetails[index].poolNumber = pool.poolNumber;
      }
    }
  }
}
</script>

<style scoped lang="scss">
.experiment-form {
  .pool-management-section {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;

      .header-left {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .title {
          font-size: 16px;
          font-weight: 500;
          color: #ffffff;
        }

        .subtitle {
          font-size: 12px;
          color: rgba(255, 255, 255, 0.5);
        }
      }
    }

    .pool-card {
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 4px;
      padding: 20px;
      margin-bottom: 20px;
      position: relative;

      .pool-card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.05);

        .pool-index {
          font-size: 14px;
          font-weight: 500;
          color: #409eff;
        }

        .delete-btn {
          color: #f56c6c;
          font-size: 12px;
          padding: 0;

          &:hover {
            color: #ff8989;
          }
        }
      }
    }
  }

  // 表单样式
  ::v-deep .el-form {
    .el-form-item__label {
      color: rgba(255, 255, 255, 0.8) !important;
    }

    .el-input__inner,
    .el-textarea__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;

      &::placeholder {
        color: rgba(255, 255, 255, 0.4) !important;
      }
    }

    .el-select .el-input__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;
    }

    .el-select .el-input__suffix {
      color: rgba(255, 255, 255, 0.6) !important;
    }

    .el-date-editor .el-input__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;
    }
  }

  .dialog-footer {
    text-align: right;
    margin-top: 20px;
  }
}
</style>