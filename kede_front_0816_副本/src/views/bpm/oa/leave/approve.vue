<template>
  <div class="app-container">
    <el-col >
      <el-form ref="form" :model="form" label-width="120px" style="padding-bottom: 10px;">
        <el-form-item label="开始时间：" prop="startTime"> {{parseTime(form.startTime, '{y}-{m}-{d}')}} </el-form-item>
        <el-form-item label="结束时间：" prop="endTime"> {{parseTime(form.endTime, '{y}-{m}-{d}')}} </el-form-item>
        <el-form-item label="请假类型：" prop="type">
          <dict-tag :type="DICT_TYPE.BPM_OA_LEAVE_TYPE" :value="form.type"/>
        </el-form-item>
        <el-form-item label="原因：" prop="reason"> {{ form.reason }}</el-form-item>
      </el-form>
    </el-col>
  </div>
</template>

<script>
import {listSimpleUsers, listSimpleUsers0} from "@/api/system/user";
import {listDept} from "@/api/system/dept";
import { getLeave}  from "@/api/bpm/leave"
import FileBusUpload from "@/components/FileUpload";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
export default {
  name: "approve",
  components: {FileBusUpload,Treeselect},
  props: {
    // 流程数据
    businessKey: {
      type: String,
    },
    id: {
      type: String,
    },
    taskKey: {
      type: String,
    },
  },
  data(){
    return {
      userOptions: [],
      deptOptions: [],
      form:{}
    }
  },
  created() {
    //alert(this.$route.query)
    // console.log(this.taskId, this.businessKey)
    if (!this.businessKey) {
      this.$message.error('未传递 id 参数，无法查看流程信息');
      return;
    }
    this.getForm();
    // this.getUserList();
    // this.getDeptList();
  },
  methods:{
    getForm(){
      getLeave(this.businessKey).then(response => {
        this.form = response.data;
        // console.log(`getLeave=============>${JSON.stringify(this.form)}`)
      })
    },
    getUserList() {
      // 获得用户列表
      this.userOptions = []
      listSimpleUsers0().then(response => {
        this.userOptions.push(...response.data)
      })
    },
    getDeptList(){
      listDept().then(response => {
        this.deptOptions = this.handleTree(response.data, "id");
      });
    },
    /** 转换部门数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.id,
        label: node.name,
        children: node.children
      };
    },
    submitForm(data, callback){
      this.form.taskParam = data.taskParam;
      this.form.action = this.taskKey;
      console.log(data);
      console.log(`====================>submitForm:${JSON.stringify(data)}`);
      // updateLeaveApply(this.form).then(response => {
      //   this.$modal.msgSuccess('审批成功')
        callback();
      // })
    },
  }
}
</script>

<style scoped>

</style>
