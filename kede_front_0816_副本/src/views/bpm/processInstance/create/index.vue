<template>
  <div class="app-container">
    <doc-alert v-if="false" title="流程发起、取消、重新发起" url="https://doc.iocoder.cn/bpm/process-instance/" />

    <!-- 第一步，通过流程定义的列表，选择对应的流程 -->
    <div v-if="!selectProcessInstance">
      <el-table v-loading="loading" :data="list">
        <el-table-column label="流程名称" align="center" prop="name" width="200">
          <template v-slot="scope">
            <el-button type="text" @click="handleBpmnDetail(scope.row)">
              <span>{{ scope.row.name }}</span>
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="流程分类" align="center" prop="category" width="100">
          <template v-slot="scope">
            <dict-tag :type="DICT_TYPE.BPM_MODEL_CATEGORY" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="流程版本" align="center" prop="processDefinition.version" width="80">
          <template v-slot="scope">
            <el-tag size="medium" v-if="scope.row">v{{ scope.row.version }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="流程描述" align="center" prop="description" width="300" show-overflow-tooltip />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template v-slot="scope">
            <el-button type="text" size="small" icon="el-icon-plus" @click="handleSelect(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 第二步，填写表单，进行流程的提交 -->
    <div v-else>
      <el-card class="box-card" >
        <div slot="header" class="clearfix">
          <span class="el-icon-document">申请信息【{{ selectProcessInstance.name }}】</span>
          <el-button style="float: right;" type="primary" @click="selectProcessInstance = undefined">选择其它流程</el-button>
        </div>
        <el-col :span="16" :offset="6">
          <div>
            <parser :key="new Date().getTime()" :form-conf="detailForm" @submit="submitForm" />
          </div>
        </el-col>
      </el-card>
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span class="el-icon-picture-outline">流程图</span>
        </div>
        <my-process-viewer key="designer" v-model="bpmnXML" v-bind="bpmnControlForm" />
      </el-card>
    </div>

    <el-dialog title="流程图" :visible.sync="showBpmnOpen" width="80%" append-to-body>
      <my-process-viewer v-if="showBpmnOpen" v-model="bpmnXML" v-bind="bpmnControlForm" />
    </el-dialog>
  </div>
</template>

<script>
import {getProcessDefinitionBpmnXML, getProcessDefinitionList} from "@/api/bpm/definition";
import {DICT_TYPE, getDictDatas} from "@/utils/dict";
import {decodeFields} from "@/utils/formGenerator";
import Parser from '@/components/parser/Parser'
import {createProcessInstance} from "@/api/bpm/processInstance";
import { openBpmCustomForm } from '@/utils/bpmFormPath'

// 流程实例的发起
export default {
  name: "ProcessInstanceCreate",
  components: {
    Parser
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 表格数据
      list: [],

      // 流程表单详情
      detailForm: {
        fields: []
      },

      // BPMN 数据
      bpmnXML: null,
      bpmnControlForm: {
        prefix: "flowable"
      },

      // 流程表单
      selectProcessInstance: undefined, // 选择的流程实例
      showBpmnOpen: false,

      // 数据字典
      categoryDictDatas: getDictDatas(DICT_TYPE.BPM_MODEL_CATEGORY),
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询流程定义列表 */
    getList() {
      this.loading = true;
      getProcessDefinitionList({
        suspensionState: 1
      }).then(response => {
          this.list = response.data
          this.loading = false
        }
      );
    },
    /** 处理选择流程的按钮操作 **/
    handleSelect(row) {
      if (!row) return
      // 业务表单优先：部分流程同时带了残留 formId，不能当成表单设计器打开
      if (Number(row.formType) === 20 && row.formCustomCreatePath) {
        openBpmCustomForm(this, row.formCustomCreatePath)
        return
      }
      if (row.formId || Number(row.formType) === 10) {
        try {
          this.selectProcessInstance = row
          this.detailForm = {
            ...JSON.parse(row.formConf || '{}'),
            fields: decodeFields(row.formFields)
          }
          getProcessDefinitionBpmnXML(row.id).then(response => {
            this.bpmnXML = response.data
          }).catch(() => {
            this.$message.error('流程图加载失败')
          })
        } catch (e) {
          this.selectProcessInstance = undefined
          this.$message.error('流程表单配置损坏，无法打开')
        }
        return
      }
      if (row.formCustomCreatePath) {
        openBpmCustomForm(this, row.formCustomCreatePath)
        return
      }
      this.$message.warning('该流程未配置表单，无法发起')
    },
    handleBpmnDetail(row) {
      if (!row || !row.id) {
        this.$message.error('缺少流程定义 ID')
        return
      }
      getProcessDefinitionBpmnXML(row.id).then(response => {
        this.bpmnXML = response.data
        this.showBpmnOpen = true
      }).catch(() => {
        this.$message.error('流程图加载失败（404）')
      })
    },
    /** 提交按钮 */
    submitForm(params) {
      if (!params) {
        return;
      }
      // 设置表单禁用
      const conf = params.conf;
      conf.disabled = true; // 表单禁用
      conf.formBtns = false; // 按钮隐藏

      // 提交表单，创建流程
      const variables = params.values;
      createProcessInstance({
        processDefinitionId: this.selectProcessInstance.id,
        variables: variables
      }).then(response => {
        this.$modal.msgSuccess("发起流程成功");
        // 关闭当前窗口
        this.$tab.closeOpenPage();
        this.$router.go(-1);
      }).catch(() => {
        conf.disabled = false; // 表单开启
        conf.formBtns = true; // 按钮展示
      })
    },
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
</style>
