<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="试卷ID" prop="paperId">
        <el-input v-model="queryParams.paperId" placeholder="请输入试卷ID" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="试题类型 1单选题 2多选题 3判断题 " prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择试题类型 1单选题 2多选题 3判断题 " clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item> -->
      <el-form-item label="试卷" prop="paperId">
        <el-select style="width:100%;" v-model="queryParams.paperId" placeholder="请选择试卷" filterable clearable  @change="handleQuery">
          <el-option v-for="(item, index) in paperList" :key="item.id+'__'+index" :label="item.name"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="试题类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请输入试题类型" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PAPER_QUESTION_TYPE)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="试题类型" prop="typeName">
        <el-input v-model="queryParams.typeName" placeholder="请输入试题类型" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="题干" prop="question">
        <el-input v-model="queryParams.question" placeholder="请输入题干" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="答案" prop="answer">
        <el-input v-model="queryParams.answer" placeholder="请输入答案" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="解析" prop="analysis">
        <el-input v-model="queryParams.analysis" placeholder="请输入解析" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="标签" prop="tag">
        <el-input v-model="queryParams.tag" placeholder="请输入标签" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="分数" prop="score">
        <el-input v-model="queryParams.score" placeholder="请输入分数" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="选项" prop="options">
        <el-input v-model="queryParams.options" placeholder="请输入选项" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-paper-question:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-paper-question:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <!-- <el-table-column label="试卷ID" align="center" prop="paperId" /> -->
      <!-- <el-table-column label="试题类型 1单选题 2多选题 3判断题 " align="center" prop="type" /> -->
      <el-table-column label="试题类型" align="center" prop="type" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_PAPER_QUESTION_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="试题类型" align="center" prop="typeName" /> -->
      <el-table-column label="题干" align="center" prop="question" show-overflow-tooltip />
      <el-table-column label="答案" align="center" prop="answer" />
      <el-table-column label="解析" align="center" prop="analysis" />
      <el-table-column label="标签" align="center" prop="tag" />
      <el-table-column label="分数" align="center" prop="score" />
      <el-table-column label="选项" align="center" prop="options" show-overflow-tooltip />
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
                     v-hasPermi="['pl:zk-paper-question:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-paper-question:delete']">删除</el-button>
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
        </el-form-item>
        <el-form-item label="试题类型 1单选题 2多选题 3判断题 " prop="type">
          <el-select v-model="form.type" placeholder="请选择试题类型 1单选题 2多选题 3判断题 ">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="试题类型" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入试题类型" />
        </el-form-item> -->
        <el-form-item label="试题类型" prop="type">
          <el-select v-model="form.type" placeholder="请输入试题类型" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PAPER_QUESTION_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="题干" prop="question">
          <el-input v-model="form.question" placeholder="请输入题干" />
        </el-form-item>
        <el-form-item label="答案" prop="answer">
          <el-input v-model="form.answer" placeholder="请输入答案" />
        </el-form-item>
        <el-form-item label="解析" prop="analysis">
          <el-input v-model="form.analysis" placeholder="请输入解析" />
        </el-form-item>
        <el-form-item label="标签" prop="tag">
          <el-input v-model="form.tag" placeholder="请输入标签" />
        </el-form-item>
        <el-form-item label="分数" prop="score">
          <el-input v-model="form.score" placeholder="请输入分数" />
        </el-form-item>
        <el-form-item label="选项" prop="options">
          <el-input v-model="form.options" placeholder="请输入选项" />
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
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择负责人" filterable clearable  @change="updateUserId">
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
import { createZkPaperQuestion, updateZkPaperQuestion, deleteZkPaperQuestion, getZkPaperQuestion, getZkPaperQuestionPage, exportZkPaperQuestionExcel } from "@/api/fish/zkPaperQuestion";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { createZkPaper, updateZkPaper, deleteZkPaper, getZkPaper, getZkPaperPage, exportZkPaperExcel } from "@/api/fish/zkPaper";

export default {
  name: "ZkPaperQuestion",
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
      // 试题列表
      list: [],
      workshopList: [], 
      userList1: [],
      paperList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        paperId: null,
        type: null,
        typeName: null,
        question: null,
        answer: null,
        analysis: null,
        tag: null,
        score: null,
        options: null,
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
    const paperId = this.$route.query && this.$route.query.paperId;
    // console.log(this.$route)
    // console.log(`paperId==========>${paperId}`);
    if(paperId) this.queryParams.paperId = parseInt(paperId);
    this.getList();
    this.getUserList();
    this.get_paperList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPaperQuestionPage(this.queryParams).then(response => {
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
    update_paperId(currId){
      let item = null;
      this.paperList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.paperName = item.paperName;
      this.form.paperId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    get_paperList() {
      this.paperList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkPaperPage(params).then(response => {
        this.paperList.push(...response.data.list)
        // this.paperList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
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
        type: undefined,
        typeName: undefined,
        question: undefined,
        answer: undefined,
        analysis: undefined,
        tag: undefined,
        score: undefined,
        options: undefined,
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
      this.title = "添加试题";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPaperQuestion(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改试题";
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
          updateZkPaperQuestion(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkPaperQuestion(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除试题编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPaperQuestion(id);
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
      this.$modal.confirm('是否确认导出所有试题数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPaperQuestionExcel(params);
        }).then(response => {
          this.$download.excel(response, '试题.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
