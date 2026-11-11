<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="试卷名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入试卷名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="开始考试时间" prop="startTime">
        <el-date-picker v-model="queryParams.startTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="结束考试时间" prop="endTime">
        <el-date-picker v-model="queryParams.endTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="时长（分钟）" prop="duration">
        <el-input v-model="queryParams.duration" placeholder="请输入时长（分钟）" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="是否允许查看答案 1是 0否" prop="lookAnswer">
        <el-input v-model="queryParams.lookAnswer" placeholder="请输入是否允许查看答案 1是 0否" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="是否打乱试题 1是 0否" prop="upset">
        <el-input v-model="queryParams.upset" placeholder="请输入是否打乱试题 1是 0否" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="及格分数" prop="passScore">
        <el-input v-model="queryParams.passScore" placeholder="请输入及格分数" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="试卷总分" prop="totalScore">
        <el-input v-model="queryParams.totalScore" placeholder="请输入试卷总分" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态 1正常 0删除" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态 1正常 0删除" clearable size="small">
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
                   v-hasPermi="['pl:zk-paper:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="downExcel_handleAdd" :loading="exportLoading"
                   v-hasPermi="['pl:zk-paper:export']">下载试题模版文件</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="试卷名称" align="center" :show-overflow-tooltip="true">
        <template v-slot="scope">
          <router-link :to="'/fish/zk-paper-question?paperId=' + scope.row.id" class="link-type">
            <span>{{ scope.row.name }}</span>
          </router-link>
        </template>
      </el-table-column>
      <!-- <el-table-column label="试卷名称" align="center" prop="name" /> -->
      <el-table-column label="考试说明" align="center" prop="content" show-overflow-tooltip/>
      <el-table-column label="开始考试时间" align="center" prop="startTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束考试时间" align="center" prop="endTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="时长（分钟）" align="center" prop="duration" />
      <!-- <el-table-column label="是否允许查看答案 1是 0否" align="center" prop="lookAnswer" />
      <el-table-column label="是否打乱试题 1是 0否" align="center" prop="upset" /> -->
      <el-table-column label="及格分数" align="center" prop="passScore" />
      <el-table-column label="试卷总分" align="center" prop="totalScore" />
      <!-- <el-table-column label="状态 1正常 0删除" align="center" prop="status" /> -->
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
                     v-hasPermi="['pl:zk-paper:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-paper:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

      <!-- 对话框  下载导入试题Excel文件  -->
    <el-dialog :title="downExcel_title" :visible.sync="downExcel_open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="下载模版" prop="fid">
          <FileBusUpload ref="uploadRef" fid="exam_excel_template_fid" bus-type="zk_paper" ></FileBusUpload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="downExcel_cancelM">确 定</el-button>
        <el-button @click="downExcel_cancelM">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="试卷名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="考试说明">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="开始考试时间" prop="startTime" style="width: 100%;">
          <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="timestamp" placeholder="选择开始考试时间" />
        </el-form-item>
        <el-form-item label="结束考试时间" prop="endTime">
          <el-date-picker clearable v-model="form.endTime" type="datetime" value-format="timestamp" placeholder="选择结束考试时间" />
        </el-form-item>
        <el-form-item label="时长（分钟）" prop="duration">
          <el-input v-model="form.duration" placeholder="请输入时长（分钟）" />
        </el-form-item>
        <!-- <el-form-item label="是否允许查看答案 1是 0否" prop="lookAnswer">
          <el-input v-model="form.lookAnswer" placeholder="请输入是否允许查看答案 1是 0否" />
        </el-form-item>
        <el-form-item label="是否打乱试题 1是 0否" prop="upset">
          <el-input v-model="form.upset" placeholder="请输入是否打乱试题 1是 0否" />
        </el-form-item> -->
        <el-form-item label="及格分数" prop="passScore">
          <el-input v-model="form.passScore" placeholder="请输入及格分数" />
        </el-form-item>
        <el-form-item label="试卷总分" prop="totalScore">
          <el-input v-model="form.totalScore" placeholder="请输入试卷总分" />
        </el-form-item>
        <!-- <el-form-item label="状态 1正常 0删除" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="负责人id" prop="userId">
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
        <el-form-item label="导入试题" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_paper" @uploadSuccess="confirmUrl"></FileBusUpload>
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
import { createZkPaper, updateZkPaper, deleteZkPaper, getZkPaper, getZkPaperPage, exportZkPaperExcel } from "@/api/fish/zkPaper";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";

export default {
  name: "ZkPaper",
  components: {
      FileBusUpload,
    Editor,
  },
  data() {
    return {
      downExcel_title: "",
      downExcel_cancel: false,
      downExcel_open: false,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 试卷列表
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
        name: null,
        content: null,
        startTime: [],
        endTime: [],
        duration: null,
        lookAnswer: null,
        upset: null,
        passScore: null,
        totalScore: null,
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
      }
    };
  },
  created() {
    const paperName = this.$route.query && this.$route.query.paperName;
    // console.log(this.$route)
    console.log(`paperName==========>${paperName}`);
    if(paperName) this.queryParams.name = paperName;

    this.getList();
    this.getUserList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPaperPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    confirmUrl(url='') {
      this.form.examUrl = url;
      console.log("confirmProcurementInformationUrlFill=====>"+JSON.stringify(this.form));
    },
    /** 取消按钮 downExcel_cancel */
    cancel() {
      this.open = false;
      this.reset();
    },
    downExcel_cancelM() {
      this.downExcel_open = false;
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
        name: undefined,
        content: undefined,
        startTime: undefined,
        endTime: undefined,
        duration: undefined,
        lookAnswer: undefined,
        upset: undefined,
        passScore: undefined,
        totalScore: undefined,
        status: undefined,
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
    downExcel_handleAdd() {
      this.reset();
      this.downExcel_open = true;
      this.downExcel_title = "下载试题Excel模版文件";
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加试卷";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPaper(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改试卷";
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
          updateZkPaper(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkPaper(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除试卷编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPaper(id);
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
      this.$modal.confirm('是否确认导出所有试卷数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPaperExcel(params);
        }).then(response => {
          this.$download.excel(response, '试卷.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
