<template>
  <div class="app-container bpm-process-detail">
    <!-- 审批信息 -->
    <el-card class="box-card" v-loading="processInstanceLoading" v-for="(item, index) in runningTasks" :key="index">
      <div slot="header" class="clearfix">
        <span class="el-icon-picture-outline">审批任务【{{ item.name }}】</span>
      </div>
      <el-col :span="16" :offset="6">
        <el-form :ref="'form' + index" :model="auditForms[index]" :rules="auditRule" label-width="100px">
          <el-form-item label="流程名" v-if="processInstance && processInstance.name">
            {{ processInstance.name }}
          </el-form-item>
          <el-form-item label="流程发起人" v-if="processInstance && processInstance.startUser">
            {{ processInstance.startUser.nickname }}
            <el-tag type="info" size="mini">{{ processInstance.startUser.deptName }}</el-tag>
          </el-form-item>
          <el-form-item label="审批建议" prop="reason">
            <el-input type="textarea" v-model="auditForms[index].reason" placeholder="请输入审批建议"/>
          </el-form-item>
        </el-form>
        <div style="margin-left: 10%; margin-bottom: 20px; font-size: 14px;">
          <el-button icon="el-icon-edit-outline" type="success" size="mini" @click="handleAudit(item, true)">通过
          </el-button>
          <el-button icon="el-icon-circle-close" type="danger" size="mini" @click="handleAudit(item, false)">不通过
          </el-button>
          <el-button icon="el-icon-edit-outline" type="primary" size="mini" @click="handleUpdateAssignee(item)">转办
          </el-button>
          <el-button icon="el-icon-edit-outline" type="primary" size="mini" @click="handleDelegate(item)">委派
          </el-button>
          <el-button icon="el-icon-refresh-left" type="warning" size="mini" @click="handleBackList(item)">退回
          </el-button>
        </div>
      </el-col>
    </el-card>
    <!-- 申请信息 -->
    <el-card class="box-card" v-loading="processInstanceLoading">
      <div slot="header" class="clearfix">
        <span class="el-icon-document">申请信息【{{ processInstance.name }}】</span>
      </div>
      <el-col v-if="this.processInstance.processDefinition && this.processInstance.processDefinition.formType === 10"
              :span="16" :offset="6">
        <div>
          <!-- <parser :key="new Date().getTime()" :form-conf="detailForm"/> -->
        </div>
      </el-col>
      <div v-if="this.processInstance.processDefinition && this.processInstance.processDefinition.formType === 20">
        <async-biz-form-component ref="businessRef" :id="this.processInstance.businessKey" :businessKey="this.processInstance.businessKey"></async-biz-form-component>
      </div>
    </el-card>

    <!-- 审批记录 -->
    <el-card class="box-card" v-loading="tasksLoad" v-if="true">
      <div slot="header" class="clearfix">
        <span class="el-icon-picture-outline">审批记录</span>
      </div>
      <el-col :span="16" :offset="4">
        <div class="block">
          <el-timeline>
            <el-timeline-item v-for="(item, index) in tasks" :key="index"
                              :icon="getTimelineItemIcon(item)" :type="getTimelineItemType(item)">
              <p style="font-weight: 700">任务：{{ item.name }}</p>
              <el-card :body-style="{ padding: '10px' }">
                <label v-if="item.assigneeUser" style="font-weight: normal; margin-right: 30px;">
                  审批人：{{ item.assigneeUser.nickname }}
                  <el-tag type="info" size="mini">{{ item.assigneeUser.deptName }}</el-tag>
                </label>
                <label style="font-weight: normal" v-if="item.createTime">创建时间：</label>
                <label style="color:#8a909c; font-weight: normal">{{ parseTime(item.createTime) }}</label>
                <label v-if="item.endTime" style="margin-left: 30px;font-weight: normal">审批时间：</label>
                <label v-if="item.endTime" style="color:#8a909c;font-weight: normal"> {{
                    parseTime(item.endTime)
                  }}</label>
                <label v-if="item.durationInMillis" style="margin-left: 30px;font-weight: normal">耗时：</label>
                <label v-if="item.durationInMillis" style="color:#8a909c;font-weight: normal">
                  {{ getDateStar(item.durationInMillis) }} </label>
                <p v-if="item.reason">
                  <el-tag :type="getTimelineItemType(item)">{{ item.reason }}</el-tag>
                </p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-col>
    </el-card>

    <!-- 高亮流程图 -->
    <el-card class="box-card" v-loading="processInstanceLoading" v-if="false">
      <div slot="header" class="clearfix">
        <span class="el-icon-picture-outline">流程图</span>
      </div>
      <my-process-viewer key="designer" v-model="bpmnXML" v-bind="bpmnControlForm" :activityData="activityList"
                         :processInstanceData="processInstance" :taskData="tasks"/>
    </el-card>

    <!-- 对话框(转办审批人) -->
    <el-dialog title="转办审批人" :visible.sync="updateAssignee.open" width="500px" append-to-body>
      <el-form ref="updateAssigneeForm" :model="updateAssignee.form" :rules="updateAssignee.rules" label-width="110px">
        <el-form-item label="新审批人" prop="assigneeUserId">
          <el-select v-model="updateAssignee.form.assigneeUserId" clearable style="width: 100%">
            <el-option v-for="item in userOptions" :key="parseInt(item.id)" :label="item.nickname"
                       :value="parseInt(item.id)"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpdateAssigneeForm">确 定</el-button>
        <el-button @click="cancelUpdateAssigneeForm">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 对话框(委派审批人) -->
    <el-dialog title="委派审批人" :visible.sync="updateDelegate.open" width="500px" append-to-body>
      <el-form ref="updateDelegateForm" :model="updateDelegate.form" :rules="updateDelegate.rules" label-width="110px">
        <el-form-item label="新审批人" prop="assigneeUserId">
          <el-select v-model="updateDelegate.form.delegateUserId" clearable style="width: 100%">
            <el-option v-for="item in userOptions" :key="parseInt(item.id)" :label="item.nickname"
                       :value="parseInt(item.id)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="委派理由" prop="reason">
          <el-input v-model="updateDelegate.form.reason" clearable placeholder="请输入委派理由"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpdateDelegateForm">确 定</el-button>
        <el-button @click="cancelUpdateDelegateForm">取 消</el-button>
      </div>
    </el-dialog>
    <!--退回流程-->
    <el-dialog title="退回流程" :visible.sync="returnOpen" width="40%" append-to-body>
      <el-form ref="formRef" v-loading="formLoading" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="退回节点" prop="targetDefinitionKey">
          <el-select v-model="formData.targetDefinitionKey" clearable style="width: 100%">
            <el-option
              v-for="item in returnList"
              :key="item.definitionKey"
              :label="item.name"
              :value="item.definitionKey"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="回退理由" prop="reason">
          <el-input v-model="formData.reason" clearable placeholder="请输入回退理由"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="returnOpen = false">取 消</el-button>
        <el-button :disabled="formLoading" type="primary" @click="submitReturn">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {getProcessDefinitionBpmnXML} from "@/api/bpm/definition";
import store from "@/store";
import {decodeFields} from "@/utils/formGenerator";
import Parser from '@/components/parser/Parser'
import {getProcessInstance} from "@/api/bpm/processInstance";
import {
  approveTask,
  delegateTask,
  getReturnList,
  getTaskListByProcessInstanceId,
  rejectTask,
  returnTask,
  updateTaskAssignee,
} from "@/api/bpm/task";
import {getDate} from "@/utils/dateUtils";
import {listSimpleUsers} from "@/api/system/user";
import {getActivityList} from "@/api/bpm/activity";
import Vue from "vue";
import { Loading } from 'element-ui';
import { isKnownBpmView, toBpmViewRequirePath } from '@/utils/bpmFormPath'

// 流程实例的详情页，可用于审批
export default {
  name: "ProcessInstanceDetail",
  components: {
    Parser
  },
  data() {
    return {
      // 遮罩层
      processInstanceLoading: true,
      // 流程实例
      id: undefined, // 流程实例的编号
      processInstance: {},
      formLoading: false,
      // 流程表单详情
      detailForm: {
        fields: []
      },
      //回退列表数据
      returnList: [],
      formData: {
        id: '',
        targetDefinitionKey: undefined,
        reason: ''
      },
      formRules: {
        targetDefinitionKey: [{required: true, message: '必须选择回退节点', trigger: 'change'}],
        reason: [{required: true, message: '回退理由不能为空', trigger: 'blur'}]
      },
      returnOpen: false,
      // BPMN 数据
      bpmnXML: null,
      bpmnControlForm: {
        prefix: "flowable"
      },
      activityList: [],

      // 审批记录
      tasksLoad: true,
      tasks: [],

      // 审批表单
      runningTasks: [],
      auditForms: [],
      auditRule: {
        reason: [{required: true, message: "审批建议不能为空", trigger: "blur"}],
      },
      // 转派审批人
      userOptions: [],
      updateAssignee: {
        open: false,
        form: {
          assigneeUserId: undefined,
        },
        rules: {
          assigneeUserId: [{required: true, message: "新审批人不能为空", trigger: "change"}],
        }
      },
      updateDelegate: {
        open: false,
        form: {
          delegateUserId: undefined,
          reason: ''
        },
        rules: {
          delegateUserId: [{required: true, message: "新审批人不能为空", trigger: "change"}],
          reason: [{required: true, message: '委派理由不能为空', trigger: 'blur'}]
        }
      }
    };
  },
  created() {
    this.id = this.$route.query.id;
    if (!this.id) {
      this.$message.error('未传递 id 参数，无法查看流程信息');
      return;
    }
    this.getDetail();

    // 获得用户列表
    this.userOptions = [];
    listSimpleUsers().then(response => {
      this.userOptions.push(...response.data);
    });
  },
  methods: {
    /** 获得流程实例 */
    getDetail() {
      // 获得流程实例相关
      this.processInstanceLoading = true;
      getProcessInstance(this.id).then(response => {
        if (!response.data) {
          this.$message.error('查询不到流程信息！');
          return;
        }
        // 设置流程信息
        this.processInstance = response.data;
        // this.processInstance.processDefinition.formType = 20;
        // console.log(`getProcessInstance==================>${JSON.stringify(this.processInstance)}`);

        // 自定义业务表单：仅 formType=20 且路径已知时再动态加载，避免 webpack 404
        const def = this.processInstance.processDefinition || {}
        const customPath = def.formCustomViewPath
        if (Number(def.formType) === 20 && customPath && isKnownBpmView(customPath)) {
          const viewPath = toBpmViewRequirePath(customPath)
          const self = this
          Vue.component("async-biz-form-component", function (resolve, reject) {
            const fail = function (err) {
              self.$message.error('自定义表单页面不存在（404）：' + viewPath)
              if (typeof reject === 'function') reject(err)
            }
            if (viewPath === '/bpm/oa/leave/approve') {
              require(['@/views/bpm/oa/leave/approve'], resolve, fail)
            } else if (viewPath === '/bpm/oa/leave/detail') {
              require(['@/views/bpm/oa/leave/detail'], resolve, fail)
            } else if (viewPath === '/bpm/oa/leave/create') {
              require(['@/views/bpm/oa/leave/create'], resolve, fail)
            } else if (viewPath === '/fish/zkDeviceAlarm/alarmHolder') {
              require(['@/views/fish/zkDeviceAlarm/alarmHolder'], resolve, fail)
            } else if (viewPath === '/fish/zkUseMedicine/approve') {
              require(['@/views/fish/zkUseMedicine/approve'], resolve, fail)
            } else if (viewPath === '/fish/zkDataRevise/approve') {
              require(['@/views/fish/zkDataRevise/approve'], resolve, fail)
            } else if (viewPath === '/fish/zkAskforLeave/approve') {
              require(['@/views/fish/zkAskforLeave/approve'], resolve, fail)
            } else if (viewPath === '/fish/zkIntelligentDiagnosis/approve') {
              require(['@/views/fish/zkIntelligentDiagnosis/approve'], resolve, fail)
            } else if (viewPath === '/erp/purchase/order/approve') {
              require(['@/views/erp/purchase/order/approve'], resolve, fail)
            } else {
              fail(new Error('unknown bpm view'))
            }
          });
        } else if (Number(def.formType) === 20 && customPath) {
          this.$message.error('该流程表单页面不存在（404）：' + customPath)
        }

        // 设置表单信息
        if (Number(this.processInstance.processDefinition.formType) === 10) {
          try {
            this.detailForm = {
              ...JSON.parse(this.processInstance.processDefinition.formConf || '{}'),
              disabled: true,
              formBtns: false,
              fields: decodeFields(this.processInstance.processDefinition.formFields)
            }
            this.detailForm.fields.forEach(item => {
              const val = this.processInstance.formVariables[item.__vModel__]
              if (val) {
                item.__config__.defaultValue = val
              }
            });
          } catch (e) {
            this.$message.error('流程表单配置损坏，无法打开')
          }
        }

        // 加载流程图
        // getProcessDefinitionBpmnXML(this.processInstance.processDefinition.id).then(response => {
        //   this.bpmnXML = response.data
        // });
        // 加载活动列表
        getActivityList({
          processInstanceId: this.processInstance.id
        }).then(response => {
          this.activityList = response.data;
        });

        // 取消加载中
        this.processInstanceLoading = false;
      });

      // 获得流程任务列表（审批记录）
      this.tasksLoad = true;
      this.runningTasks = [];
      this.auditForms = [];
      getTaskListByProcessInstanceId(this.id).then(response => {
        // 审批记录
        this.tasks = [];
        // 移除已取消的审批
        response.data.forEach(task => {
          if (task.result !== 4) {
            this.tasks.push(task);
          }
        });
        // console.log(`getTaskListByProcessInstanceId=================>${JSON.stringify(response)}`);
        // 排序，将未完成的排在前面，已完成的排在后面；
        this.tasks.sort((a, b) => {
          // 有已完成的情况，按照完成时间倒序
          if (a.endTime && b.endTime) {
            return b.endTime - a.endTime;
          } else if (a.endTime) {
            return 1;
          } else if (b.endTime) {
            return -1;
            // 都是未完成，按照创建时间倒序
          } else {
            return b.createTime - a.createTime;
          }
        });

        // 需要审核的记录
        const userId = store.getters.userId;
        this.tasks.forEach(task => {
          if (task.status !== 1 && task.status !== 6) { // 只有待处理才需要 task.result !== 1 && task.result !== 6
            return;
          }
          if (!task.assigneeUser || task.assigneeUser.id !== userId) { // 自己不是处理人
            return;
          }
          this.runningTasks.push({...task});
          // console.log(`runningTasks=================>${JSON.stringify(this.runningTasks)}`);
          this.auditForms.push({
            reason: ''
          })
        });

        // 取消加载中
        this.tasksLoad = false;
      });
    },
    getDateStar(ms) {
      return getDate(ms);
    },
    getTimelineItemIcon(item) {
      if (item.result === 1) {
        return 'el-icon-time';
      }
      if (item.result === 2) {
        return 'el-icon-check';
      }
      if (item.result === 3) {
        return 'el-icon-close';
      }
      if (item.result === 4) {
        return 'el-icon-remove-outline';
      }
      if (item.result === 5) {
        return 'el-icon-back'
      }
      return '';
    },
    getTimelineItemType(item) {
      if (item.result === 1) {
        return 'primary';
      }
      if (item.result === 2) {
        return 'success';
      }
      if (item.result === 3) {
        return 'danger';
      }
      if (item.result === 4) {
        return 'info';
      }
      if (item.result === 5) {
        return 'warning';
      }
      if (item.result === 6) {
        return 'default'
      }
      return '';
    },
    /** 处理审批通过和不通过的操作 */
    handleAudit(task, pass) {
      const that = this;
      const index = this.runningTasks.indexOf(task);
      this.$refs['form' + index][0].validate(valid => {
        if (!valid) {
          return;
        }
        const data = {
          id: task.id,
          reason: this.auditForms[index].reason
        }
        let loadingInstance = Loading.service({
            lock: true,
            text: '操作中……',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
        if (pass) {
          approveTask(data).then(response => {
            if(that.$refs['businessRef']) {
              that.$refs['businessRef'].submitForm(data, function (){
                //that.getDetail();
                that.$modal.msgSuccess("审批通过成功！");
                that.getDetail(); // 获得最新详情
              });
            } else {
              that.$modal.msgSuccess("审批通过成功！");
              that.getDetail(); // 获得最新详情
            }
            this.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭
                loadingInstance.close();
            });
          });
        } else {
          rejectTask(data).then(response => {
            this.$modal.msgSuccess("审批不通过成功！");
            this.getDetail(); // 获得最新详情
            this.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭
                loadingInstance.close();
            });
          });
        }
      });
    },
    /** 处理转办审批人 */
    handleUpdateAssignee(task) {
      // 设置表单
      this.resetUpdateAssigneeForm();
      this.updateAssignee.form.id = task.id;
      // 设置为打开
      this.updateAssignee.open = true;
    },
    /** 提交转办审批人 */
    submitUpdateAssigneeForm() {
      this.$refs['updateAssigneeForm'].validate(valid => {
        if (!valid) {
          return;
        }
        updateTaskAssignee(this.updateAssignee.form).then(response => {
          this.$modal.msgSuccess("转办任务成功！");
          this.updateAssignee.open = false;
          this.getDetail(); // 获得最新详情
        });
      });
    },
    /** 取消转办审批人 */
    cancelUpdateAssigneeForm() {
      this.updateAssignee.open = false;
      this.resetUpdateAssigneeForm();
    },
    /** 重置转办审批人 */
    resetUpdateAssigneeForm() {
      this.updateAssignee.form = {
        id: undefined,
        assigneeUserId: undefined,
      };
      this.resetForm("updateAssigneeForm");
    },
    /** 处理审批委派的操作 */
    handleDelegate(task) {
      //this.$modal.msgError("暂不支持【委派】功能，可以使用【转派】替代！");
      this.resetUpdateDelegateForm();
      this.updateDelegate.form.id = task.id;
      // 设置为打开
      this.updateDelegate.open = true;
    },
    /** 提交委派审批人 */
    submitUpdateDelegateForm() {
      this.$refs['updateDelegateForm'].validate(valid => {
        if (!valid) {
          return;
        }
        delegateTask(this.updateDelegate.form).then(response => {
          this.$modal.msgSuccess("委派任务成功！");
          this.updateDelegate.open = false;
          this.getDetail(); // 获得最新详情
        });
      });
    },
    /** 取消委派审批人 */
    cancelUpdateDelegateForm() {
      this.updateDelegate.open = false;
      this.resetUpdateDelegateForm();
    },
    /** 重置委派审批人 */
    resetUpdateDelegateForm() {
      this.updateDelegate.form = {
        id: undefined,
        delegateUserId: undefined,
      };
      this.resetForm("updateDelegateForm");
    },
    /** 处理审批退回的操作 */
    /** 返回退回节点列表 */
    handleBackList(task) {
      // 可参考 http://blog.wya1.com/article/636697030/details/7296
      getReturnList(task.id).then(response => {
        this.returnList = response.data;
        if (this.returnList.length == 0) {
          this.$modal.msgError("当前没有可回退的节点！");
          return;
        }
        this.formData.id = task.id;
        this.returnOpen = true;
      });

    },
    /** 提交退回任务 */
    submitReturn() {
      if (!this.formData.targetDefinitionKey) {
        this.$modal.msgError("请选择退回节点！");
      }
      this.$refs['formRef'].validate(valid => {
        if (!valid) {
          return;
        }
        returnTask(this.formData).then(res => {
          if (res.data) {
            this.$modal.msgSuccess("回退成功！");
            this.returnOpen = false;
            this.getDetail();
          }
        });
      });
    }
  }
};
</script>

<style lang="scss">
.my-process-designer {
  height: calc(100vh - 200px);
}

.box-card {
  width: 100%;
  margin-bottom: 20px;
}

.bpm-process-detail {
  color: #e8eef8;
  line-height: 1.35;
  .el-card__body {
    padding: 12px 16px;
  }
  .el-form-item {
    margin-bottom: 10px;
  }
  .el-form-item__label,
  .el-form-item__content,
  label {
    line-height: 1.3 !important;
    color: #dce7f5 !important;
    font-weight: 400;
  }
  .el-timeline-item {
    padding-bottom: 8px;
  }
  .el-timeline-item__content,
  .el-timeline-item__content p {
    line-height: 1.3;
    color: #e8eef8;
    margin: 2px 0;
  }
  .el-timeline-item__content label {
    color: #c5d4e8 !important;
  }
}
</style>
