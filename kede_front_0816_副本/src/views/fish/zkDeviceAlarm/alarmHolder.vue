<template>
  <div class="app-container">
    <el-col>
      <el-form ref="form" :model="form" label-width="120px" style="padding-bottom: 10px;">
        <el-form-item label="系统" prop="pondSystemName">
          <el-input v-model="form.pondSystemName" :disabled="true" placeholder="系统名称" />
        </el-form-item>
        <el-form-item label="存塘" prop="pondName">
          <el-input v-model="form.pondName" :disabled="true" placeholder="存塘名称" />
        </el-form-item>
        <el-form-item label="告警设备" prop="warnDevice">
          <el-input v-model="form.warnDevice" :disabled="true" placeholder="告警设备名" />
        </el-form-item>
        <el-form-item label="告警对象" prop="warnName">
          <el-input v-model="form.warnName" :disabled="true" placeholder="告警对象" />
        </el-form-item>
        <el-form-item label="告警类型" prop="warnType">
          <dict-tag :type="DICT_TYPE.ZK_DEVICE_ALARM_WARN_TYPE" :value="form.warnType" />
        </el-form-item>
        <el-form-item label="告警描述" prop="warnContent">
          <el-input v-model="form.warnContent" :disabled="true" type="textarea" rows="4" placeholder="告警描述" />
        </el-form-item>
        <el-form-item label="发起人" prop="userName">
          <el-input v-model="form.userName" :disabled="true" placeholder="发起人" />
        </el-form-item>
        <el-form-item label="审批人" prop="approveUserId">
          <el-select v-model="form.approveUserId" placeholder="请选择审批人" style="width: 100%;">
            <el-option v-for="user in userList" :key="user.userId" :label="user.nickName" :value="user.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理描述" prop="handleInfo">
          <editor v-model="form.handleInfo" :min-height="200" placeholder="请输入处理描述"/>
        </el-form-item>
        <el-form-item label="抄送人" prop="ccIds">
          <el-input v-model="form.ccIds" placeholder="请输入抄送人ID，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zkDeviceAlarmFile"></FileBusUpload>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleSubmit">提交处理</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </el-col>
  </div>
</template>

<script>
import { getZkDeviceAlarm, updateZkDeviceAlarm } from "@/api/fish/zkDeviceAlarm";
import FileBusUpload from "@/components/FileBusUpload";
import Editor from '@/components/Editor';
import DictTag from "@/components/DictTag/index.vue";
import { DICT_TYPE } from "@/utils/dict";
import { getUUID } from "@/utils";
// import { getDictDataLabel } from '@/utils/dict';
// import { getTaskListByProcessInstanceId } from "@/api/bpm/task";
import { listSimpleUsers } from "@/api/system/user";

export default {
  name: "zkDeviceAlarmApprove",
  components: {
    FileBusUpload,
    Editor,
    DictTag
  },
  props: {
    // 流程数据
    businessKey: {
      type: String,
      default: null
    },
    // 流程变量
    variables: {
      type: Object,
      default: () => {}
    }
  },
  data(){
    return {
      form: {
        id: undefined,
        workshopId: undefined,
        workshopName: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondSystemName: undefined,
        pondId: undefined,
        pondDetailNum: undefined,
        pondName: undefined,
        batchNumber: undefined,
        warnDeviceId: undefined,
        warnDevice: undefined,
        warnDeviceType: undefined,
        warnName: undefined,
        warnType: undefined,
        warnContent: undefined,
        userName: undefined,
        status: undefined,
        handleInfo: undefined,
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        processInstanceId: undefined,
        approveUserId: undefined,
        approveUserName: undefined,
        noticeUserId: undefined,
        noticeUserName: undefined,
        ccIds: ''
      },
      // 表单校验
      rules: {
        handleInfo: [{ required: true, message: "处理描述不能为空", trigger: "blur" }],
        approveUserId: [{ required: true, message: "请选择审批人", trigger: "change" }]
      },
      // 流程参数
      taskParam: {
        processKey: "device_alarm_process",
        businessKey: null,
        processInstanceId: '',
        taskId: '',
        variables: {},
        comment: '',
        approveUserId: '',
        approveUserName: '',
        ccIds: []
      },
      // 用户列表
      userList: []
    }
  },
  created() {
    // 初始化字典类型
    if (!DICT_TYPE.ZK_DEVICE_ALARM_WARN_TYPE) {
      DICT_TYPE.ZK_DEVICE_ALARM_WARN_TYPE = 'zk_device_alarm_warn_type';
    }
    
    // 获取用户列表
    this.getUserList();
    
    // 优先使用props传入的businessKey
    if (this.businessKey) {
      this.getFormById(this.businessKey);
    } else {
      // 然后尝试从URL参数获取ID
      const idFromUrl = this.$route.query.id;
      if (idFromUrl) {
        this.getFormById(idFromUrl);
      } else if (Object.keys(this.variables).length > 0) {
        // 从流程变量中初始化表单数据
        this.initFormFromVariables();
      } else {
        this.$message.error('未传递告警信息，无法查看');
        return;
      }
    }
  },
  methods: {
    // 获取用户列表
    getUserList() {
      listSimpleUsers().then(response => {
        this.userList = response.data;
      }).catch(error => {
        this.$message.error('获取用户列表失败');
        console.error('获取用户列表失败:', error);
      });
    },
    // 从API获取告警信息
    getForm() {
      getZkDeviceAlarm(this.businessKey).then(response => {
        this.form = response.data;
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      }).catch(error => {
        this.$message.error('获取告警信息失败');
        console.error('获取告警信息失败:', error);
      })
    },
    
    // 从URL参数获取告警信息
    getFormById(id) {
      getZkDeviceAlarm(id).then(response => {
        this.form = response.data;
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      }).catch(error => {
        this.$message.error('获取告警信息失败');
        console.error('获取告警信息失败:', error);
      })
    },
    
    // 从流程变量初始化表单数据
    initFormFromVariables() {
      this.form = {
        id: this.variables.id || '',
        workshopId: this.variables.workshopId || '',
        workshopName: this.variables.workshopName || '',
        pondSystemId: this.variables.pondSystemId || '',
        pondNum: this.variables.pondNum || '',
        pondSystemName: this.variables.pondSystemName || '',
        pondId: this.variables.pondId || '',
        pondDetailNum: this.variables.pondDetailNum || '',
        pondName: this.variables.pondName || '',
        batchNumber: this.variables.batchNumber || '',
        warnDeviceId: this.variables.warnDeviceId || '',
        warnDevice: this.variables.warnDevice || '',
        warnDeviceType: this.variables.warnDeviceType || '',
        warnName: this.variables.warnName || '',
        warnType: this.variables.warnType || '',
        warnContent: this.variables.warnContent || '',
        userName: this.variables.userName || '',
        status: this.variables.status || '',
        handleInfo: this.variables.handleInfo || '',
        userId: this.variables.userId || '',
        deptId: this.variables.deptId || '',
        fid: this.variables.fid || getUUID().replaceAll("-", ""),
        processInstanceId: this.variables.processInstanceId || '',
        ccIds: this.variables.ccIds || ''
      };
      
      // 如果有id，尝试获取完整的告警信息
      if (this.form.id) {
        this.getForm();
      }
    },
    
    // 页面直接提交处理
    handleSubmit() {
      this.submitForm();
    },
    
    // 提交表单，集成工作流
    submitForm(data, callback) {

      this.$refs["form"].validate(valid => {
        if (!valid) {
          if (callback) {
            callback(false);
          } else {
            return;
          }
        }
        
        // 确保有告警ID
        if (!this.form.id) {
          this.$message.error('未找到告警信息，无法提交');
          if (callback) {
            callback(false);
          }
          return;
        }
        
        // 设置状态为已处理
        this.form.status = 1; // 1表示已处理
        
        // 准备流程参数，参考index页面的实现方式
        const approveTaskParam = {
          processKey: "device_alarm_process",
          businessKey: this.form.id,
          processInstanceId: this.form.processInstanceId || '',
          taskId: '',
          variables: {
            ...this.form,
            comment: this.form.handleInfo
          },
          comment: this.form.handleInfo,
          approveUserId: this.form.approveUserId,
          approveUserName: '',
          ccIds: this.form.ccIds ? this.form.ccIds.split(',') : []
        };
        
        // 将流程参数添加到表单中
        this.form.taskParam = approveTaskParam;
        
        // 调用API更新告警信息
        updateZkDeviceAlarm(this.form).then(() => {
          this.$modal.msgSuccess('告警处理申请提交成功');
          if (callback) {
            callback(true);
          } else {
            // 组件模式下，通知父组件处理完成
            this.$emit('handled');
            // 页面模式下返回上一页
            if (!this.businessKey) {
              this.$router.back();
            }
          }
        }).catch(error => {
          this.$modal.msgError('提交失败，请重试');
          console.error('提交失败:', error);
          if (callback) {
            callback(false);
          }
        });
      });
    },
    
    // 工作流会调用这个方法获取表单数据
    getFormData() {
      return this.form;
    }
  }
}
</script>

<style scoped>
.editor-wrapper {
  margin-top: 10px;
}

.el-input__inner:disabled {
  background-color: #f5f7fa;
}
</style>