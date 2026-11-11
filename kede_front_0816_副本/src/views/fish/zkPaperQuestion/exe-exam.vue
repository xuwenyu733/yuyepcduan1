<template>
  <div class="app-container exe-exam">
    <el-row>
      <el-col :span="24">
        <div class="grid-content bg-purple-dark">
            <el-button type="primary" class="subBtn" @click="returnExeExam">返回</el-button>
            <div class="grid-content bg-purple-dark">
              <!-- <el-form>
                <el-form-item label="" v-for="(t,i) in list" :key="t.id">
                  <P style="margin-top: 10px;margin-bottom: 0px;line-height: 2">{{i + 1}}.{{t.question}}</P>
                <el-radio-group v-model="currItem.resource[t.id]">
                  <el-radio label="A">A.{{testObj.A}}</el-radio><br>
                  <el-radio label="B">B.{{}}</el-radio><br>
                  <el-radio label="C">C.{{}}</el-radio><br>
                  <el-radio label="D">D.{{}}</el-radio>
                </el-radio-group>
                </el-form-item>
                <el-button type="primary" class="subBtn" @click="present">提交试卷</el-button>
              </el-form> -->

              <el-form>
                <template v-for="(item, index) in list">
                  <div :key="item.id">
                    <div v-if="item.type == 1">
                      <el-form-item>
                        <!-- <P style="margin-top: 10px;margin-bottom: 0px;line-height: 2">{{item.question}}</P> 
                         :label="index+1+''"
                        -->
                          <div class="exam-title" style="margin-top: 5px;margin-bottom: 0px;line-height: 2">{{ index+1}}、 {{item.question}}</div>
                            <el-radio-group v-model="item.myAnswer">
                              <template v-for="(value, key, index) in JSON.parse(item.options)">
                                <div class="exam-choose">
                                  <el-radio :key="value+key+index" :label="key+''" >{{key+"."+value}}</el-radio><br :key="key+value+index" />
                                </div>
                              </template>
                            </el-radio-group>
                      </el-form-item>
                    </div>
                    <div v-if="item.type == 2">
                      <!-- 多选 -->
                      <el-form-item>
                        <!-- 
                           :label="index+1+''"
                         -->
                        <!-- <P style="margin-top: 5px;margin-bottom: 0px;line-height: 2">{{ index+1}}、 {{item.question}}</P> -->
                        <div class="exam-title" style="margin-top: 5px;margin-bottom: 0px;line-height: 2">{{ index+1}}、 {{item.question}}</div>
                          <el-checkbox-group v-model="item.myAnswer2">
                            <template v-for="(value, key, index) in JSON.parse(item.options)">
                              <div class="exam-choose">
                                  <el-checkbox :key="value+key+index" :label="key+'.'+value" :name="key" ></el-checkbox><br :key="key+value+index">
                              </div>
                            </template>
                          </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </div>
                </template>
                <el-button type="primary" class="subBtn" @click="present">提交试卷</el-button>
              </el-form>
            </div>
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
import { UserInfoKey } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { createZkPaperAnswer, updateZkPaperAnswer, deleteZkPaperAnswer, getZkPaperAnswer, getZkPaperAnswerPage, exportZkPaperAnswerExcel } from "@/api/fish/zkPaperAnswer";

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
        pageSize: 500,
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
    // console.log(`paperId===>${paperId}`)
    if(paperId) this.queryParams.paperId = parseInt(paperId);
    this.getList();
    // this.getUserList();
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
    },
    present() {
      var answer = []
      this.list.forEach(obj => {
        var temp = ''
        if(obj.type == 1) {
          temp = obj.myAnswer?obj.myAnswer:''
        } else if(obj.type == 2) {
          if(obj.myAnswer2 && obj.myAnswer2.length > 0) {
            temp = obj.myAnswer2.sort().map(iii => iii.split(".")[0]).join("|");
          }
        }
        answer.push({
          questionId: obj.id,
          answer: temp
        })
        // console.log("present================"+JSON.stringify(obj))
      })
      let userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
      var currItem = {
              paperId: this.$route.query.paperId,
              deptId: userInfo.deptId,
              userId: userInfo.userId,
              submitTime: new Date().getTime(),
              answer: JSON.stringify(answer)
      }
      
      // console.log("考完了，提交======================"+JSON.stringify(currItem))

      createZkPaperAnswer(currItem).then(res => {
        console.log("present==============================>"+JSON.stringify(res))
        if(res.code == 0) {
          this.$message({
            message: '恭喜你，考试结束，您的分数是：'+res.other.zkPaperAnswer.score,
            type: 'success'
          });
        }
        this.returnPaperAnswer();
      })
    },
    returnExeExam() {
      this.$router.push({
          path: '/knowledge/zk-paper-start'
      });
    },
    returnPaperAnswer() {
      this.$router.push({
          path: '/knowledge/zk-paper-answer'
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.exe-exam {
  .exam-title {
    font-size: 16px !important;
  }
  .exam-choose {
    padding-left: 15px;
  }
}
</style>