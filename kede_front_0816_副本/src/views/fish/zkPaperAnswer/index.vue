<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="试卷名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入试卷名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
        <el-form-item label="考试人" prop="userId">
          <el-select style="width:100%;" v-model="queryParams.userId" placeholder="请选择考试人" filterable clearable>
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      <!-- <el-form-item label="试卷ID" prop="paperId">
        <el-input v-model="queryParams.paperId" placeholder="请输入试卷ID" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="答案JSON数组 [{questionId:1,answer:'A'}]" prop="answer">
        <el-input v-model="queryParams.answer" placeholder="请输入答案JSON数组 [{questionId:1,answer:'A'}]" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="交卷时间" prop="submitTime">
        <el-date-picker v-model="queryParams.submitTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="总得分" prop="score">
        <el-input v-model="queryParams.score" placeholder="请输入总得分" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="是否及格 1是 0否" prop="passed">
        <el-input v-model="queryParams.passed" placeholder="请输入是否及格 1是 0否" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="考试人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入考试人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="考试人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入考试人所属部门" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-paper-answer:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-paper-answer:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <!-- <el-table-column label="试卷ID" align="center" prop="paperId" /> -->
      <el-table-column label="试卷名称" align="center" :show-overflow-tooltip="true">
        <template v-slot="scope">
          <router-link :to="'/fish/zk-paper?paperName=' + scope.row.paperName" class="link-type">
            <span>{{ scope.row.paperName }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="考试人" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <userName :user-id="scope.row.userId"></userName>
        </template>
      </el-table-column>
      <el-table-column label="答题记录" align="center" prop="answer" show-overflow-tooltip/>
      <el-table-column label="交卷时间" align="center" prop="submitTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.submitTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总得分" align="center" prop="score" />
      <!-- <el-table-column label="是否及格 1是 0否" align="center" prop="passed" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="考试人id" align="center" prop="userId" />
      <el-table-column label="考试人所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-paper-answer:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-paper-answer:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="试卷ID" prop="paperId">
          <el-input v-model="form.paperId" placeholder="请输入试卷ID" />
        </el-form-item> -->
        <el-form-item label="答题记录" prop="answer">
          <el-input v-model="form.answer" placeholder="请输入答题记录" />
        </el-form-item>
        <el-form-item label="交卷时间" prop="submitTime">
          <el-date-picker clearable v-model="form.submitTime" type="date" value-format="timestamp" placeholder="选择交卷时间" />
        </el-form-item>
        <el-form-item label="总得分" prop="score">
          <el-input v-model="form.score" placeholder="请输入总得分" />
        </el-form-item>
        <!-- <el-form-item label="是否及格 1是 0否" prop="passed">
          <el-input v-model="form.passed" placeholder="请输入是否及格 1是 0否" />
        </el-form-item>
        <el-form-item label="考试人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入考试人id" />
        </el-form-item>
        <el-form-item label="考试人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入考试人所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
        <el-form-item label="考试人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择考试人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkPaperAnswer, updateZkPaperAnswer, deleteZkPaperAnswer, getZkPaperAnswer, getZkPaperAnswerPage, exportZkPaperAnswerExcel } from "@/api/fish/zkPaperAnswer";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";

export default {
  name: "ZkPaperAnswer",
  components: {
      FileBusUpload,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 答题记录列表
      list: [],
      userList1: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        paperId: null,
        answer: null,
        submitTime: [],
        score: null,
        passed: null,
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
      }
    };
  },
  created() {
    this.getList();
    this.getUserList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPaperAnswerPage(this.queryParams).then(response => {
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
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>"+JSON.stringify(this.form));
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          let tempStr = `${obj.deptName}-${obj.nickname}`;
          obj.nickname = tempStr;
          // console.log("===========>"+JSON.stringify(obj));
        })
      })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        paperId: undefined,
        answer: undefined,
        submitTime: undefined,
        score: undefined,
        passed: undefined,
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
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加答题记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPaperAnswer(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改答题记录";
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
        // 修改的提交
        if (this.form.id != null) {
          updateZkPaperAnswer(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkPaperAnswer(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除答题记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPaperAnswer(id);
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
      this.$modal.confirm('是否确认导出所有答题记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPaperAnswerExcel(params);
        }).then(response => {
          this.$download.excel(response, '答题记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
