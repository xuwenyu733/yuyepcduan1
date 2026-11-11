<template>
  <div class="app-container">
    <el-row>
      <el-col :span="24">
        <div class="grid-content bg-purple-dark">
            <template v-for="item in list">
              <el-card class="box-card" :key="item.id">
                <div slot="header" class="clearfix title">
                  <span>{{item.name}}</span>
                  <span></span>
                  <span></span>
                  <span></span>
                  <!-- <el-button style="float: right; padding: 3px 0;color: #ccc;" type="text" @click.prevent="exeExam(item)">开始考试</el-button> -->
                  <span class="btn" @click.prevent="exeExam(item)">开始考试</span>
                </div>
                <div  class="text item">
                  考试说明： {{item.content}}
                </div>
                <div  class="text item">
                  开始时间： {{ parseTime(item.startTime) }}
                </div>
                <div  class="text item">
                  结束时间： {{ parseTime(item.endTime) }}
                </div>
              </el-card>
              <div style="height: 15px;"></div>
            </template>
        </div>
        </el-col>
    </el-row>
  </div>
</template>

<script>
import { createZkPaperQuestion, updateZkPaperQuestion, deleteZkPaperQuestion, getZkPaperQuestion, getZkPaperQuestionPage, exportZkPaperQuestionExcel } from "@/api/fish/zkPaperQuestion";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { listForNeedExam } from "@/api/fish/zkPaper";

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
    this.getList();
    // this.getUserList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      listForNeedExam(this.queryParams).then(response => {
        this.list = response.data;

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
    exeExam(item) {
      // console.log("exeExam======================"+JSON.stringify(item))
      this.$router.push({
          path: '/fish/zkPaperQuestion/exe-exam',
          // path: '/fish/zk-paper-question',
          params: {},
          query: { paperId: item.id },
      });
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
<style lang="scss" scoped>
.app-container {
  .title {
    font-size: 14px;
  }
  .text {
    font-size: 14px;
  }
  // .box-card button span{
  //   color: #ccc;
  // }
  .box-card  {
    .title {
      display: flex;
      justify-content: space-between;
      // align-items: center;
      .btn {
        padding: 7px 7px;
        border: 1px solid green;
        color: green;
        border-radius: 3px;
        cursor: pointer;
      }
    }
  }
}
</style>