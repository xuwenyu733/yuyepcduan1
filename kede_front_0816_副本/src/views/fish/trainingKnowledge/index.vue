<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="知识名称" prop="knowledgeName">
        <el-input v-model="queryParams.knowledgeName" placeholder="请输入知识名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="知识备注" prop="knowledgeRemark">
        <el-input v-model="queryParams.knowledgeRemark" placeholder="请输入知识备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_TRAINING_KNOWLEDGE_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="状态(0:待入库;1:已入库;-1:已删除;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:待入库;1:已入库;-1:已删除;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:training-knowledge:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:training-knowledge:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="知识名称" align="center" prop="knowledgeName" />
      <el-table-column label="知识详情" align="center" prop="knowledgeContent" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ plainContent(scope.row.knowledgeContent) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="知识备注" align="center" prop="knowledgeRemark" />
      <!-- <el-table-column label="状态(0:待入库;1:已入库;-1:已删除;)" align="center" prop="status" /> -->
      <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_TRAINING_KNOWLEDGE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:training-knowledge:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:training-knowledge:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="知识名称" prop="knowledgeName">
          <el-input v-model="form.knowledgeName" placeholder="请输入知识名称" />
        </el-form-item>
        <el-form-item label="知识详情">
          <editor v-model="form.knowledgeContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="知识备注" prop="knowledgeRemark">
          <el-input v-model="form.knowledgeRemark" placeholder="请输入知识备注" />
        </el-form-item>
        <!-- <el-form-item label="状态(0:待入库;1:已入库;-1:已删除;)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_TRAINING_KNOWLEDGE_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="负责人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入负责人id" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- websocket模块，能够暴露api方便智能体调用 -->
    <WebSocket @socketSonSend="socketParentReceive" isNav="false" parentTellMePage="/peixun_zhishimng/peixun_zhishi"></WebSocket>
  </div>
</template>

<script>
import { createTrainingKnowledge, updateTrainingKnowledge, deleteTrainingKnowledge, getTrainingKnowledge, getTrainingKnowledgePage, exportTrainingKnowledgeExcel } from "@/api/fish/trainingKnowledge";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID, html2Text } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import WebSocket from '@/components/WebSocket/index.vue';
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "TrainingKnowledge",
  components: {
      FileBusUpload,
    Editor, WebSocket,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      userInfo: {},
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 培训知识列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        knowledgeName: null,
        knowledgeContent: null,
        knowledgeRemark: null,
        status: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      },
      socketParam: {}, // 后台websocket发送给前台内容
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    console.log(`培训知识-页面名称：`, this.$router.currentRoute.name);
    this.getList();
    this.judgeIsNeedAdd();
  },
  methods: {
    /** 列表不展示 HTML 源码（语段/知识详情） */
    plainContent(val) {
      if (val == null || val === '') return '';
      const text = html2Text(String(val));
      return (text || String(val)).replace(/\s+/g, ' ').trim();
    },
    // 1. 当前页面休眠中： 由其他页面跳转当前页面，弹出添加数据弹窗
    judgeIsNeedAdd() {
      const socketParam = this.$route.params && this.$route.params.socketParam && JSON.parse(this.$route.params.socketParam);
      this.socketParam = socketParam;
      // console.log(`socketParam==========>`, socketParam);
      if(socketParam && socketParam.operateKey && socketParam.operateKey == "ADD_DATA" && socketParam.content) {
        let temp = JSON.parse(socketParam.content);
        if(temp.supplierId) temp.supplierId = parseInt(temp.supplierId)
        this.handleAdd(temp);
      }
    },
    // 2. 当前页面活着： 接受消息
    socketParentReceive(msgParam){
      if(!(msgParam && msgParam.includes("{"))) return;
      let socketParam = JSON.parse(msgParam);
      this.socketParam = socketParam;
      // console.log(`views/pl/trainingKnowledge/index.vue====location.href：${location.href}==>收到后台发送过来的消息：`, socketParam);
      if(socketParam && socketParam.operateKey == "SURE_ADD" && this.my_common.judgeOperateUrl(socketParam.targetPath, "/training-knowledge")) {
        // 数据入库第二步 且 当前页面 活着，则当前页面处理点击事件
        if(!this.open) {
          // 如果弹窗都没有开启，就执行了第二步，肯定需要给他一个提示
          let replyParam = {"uid": this.userInfo.userId, "receiveMsg": "作为助手你还没有调用工具'add2'。", "isSyncMsg": true, "version": this.socketParam.version};
          this.$socketPublic.dispatch("sendMsg", JSON.stringify(replyParam), "trainingKnowledge.vue submitForm");
          return;
        }
        this.submitForm(() => {
          this.$router.push({
          path: socketParam.targetPath,
              query: { },
          });
        });
        console.log(`执行了点击操作`);
      } else if(socketParam && socketParam.operateKey && this.my_common.judgeOperateUrl(socketParam.targetPath, "/training-knowledge")
        && socketParam.operateKey == "ADD_DATA" && socketParam.content) {
        // 数据入库第一步 且 当前页面 活着，则当前页面处理跳出弹窗事件
        let temp = JSON.parse(socketParam.content);
        if(!this.open) this.handleAdd(temp);
        else {
          this.form = {...this.form, ...temp};
        }
      }
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getTrainingKnowledgePage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        knowledgeName: undefined,
        knowledgeContent: undefined,
        knowledgeRemark: undefined,
        status: '0',
        userId: undefined,
        deptId: undefined,
        fid: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(defaultParam) {
      this.reset();
      this.open = true;
      this.title = "添加培训知识";
      this.form.fid = getUUID().replaceAll('-', '');
      if(defaultParam) this.form = {...this.form, ...defaultParam};
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getTrainingKnowledge(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改培训知识";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // console.log(`form============>${JSON.stringify(this.form)}`);
        // return;
        // 修改的提交
        if (this.form.id != null) {
          updateTrainingKnowledge(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createTrainingKnowledge(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          let replyParam = {"uid": this.userInfo.userId, "receiveMsg": "数据成功入库。", "isSyncMsg": true, "version": this.socketParam.version};
          this.$socketPublic.dispatch("sendMsg", JSON.stringify(replyParam), "trainingKnowledge.vue submitForm");
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除培训知识编号为"' + id + '"的数据项?').then(function() {
          return deleteTrainingKnowledge(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有培训知识数据项?').then(() => {
          this.exportLoading = true;
          return exportTrainingKnowledgeExcel(params);
        }).then(response => {
          this.$download.excel(response, '培训知识.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
