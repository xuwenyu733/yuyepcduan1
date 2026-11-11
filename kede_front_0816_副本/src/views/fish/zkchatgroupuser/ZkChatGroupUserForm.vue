<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="群组" prop="groupId">
          <el-select style="width:100%;" v-model="formData.groupId" placeholder="请选择群组" filterable clearable>
            <el-option v-for="(item, index) in chatGroupList1" :key="item.id+'__'+index" :label="item.groupName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="formData.userType" placeholder="请输入用户类型" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_CHAT_GROUP_USER_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="组员" prop="userId">
          <el-select style="width:100%;" v-model="formData.userId" placeholder="请选择组员" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="归属部门" prop="deptId">
          <treeselect v-model="formData.deptId" :options="deptOptions" :show-count="true" :clearable="false"
                      placeholder="请选择归属部门" :normalizer="normalizer"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkChatGroupUserApi from '@/api/fish/zkchatgroupuser';
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import {listSimpleDepts} from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { UserInfoKey } from "@/utils/constants";
import * as ZkChatGroupApi from '@/api/fish/zkchatgroup';

export default {
  name: "ZkChatGroupUserForm",
  components: {
    Treeselect,
  },
  data() {
    return {
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        groupId: undefined,
        remark: undefined,
        userName: undefined,
        userType: undefined,
        userId: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        groupId: [{ required: true, message: '群组id不能为空', trigger: 'blur' }],
      },
      userList1: [],
      userInfo: {},
      // 部门树选项
      deptOptions: undefined,
      chatGroupList1: [],
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`userInfo================>${JSON.stringify(this.userInfo)}`);
    this.getUserList();
    this.getTreeselect();
    this.getChatGroupList();
  },
  methods: {  
    getChatGroupList() {
      let params = {
        currPage:1 ,
        pageSize: 500
      }
      this.chatGroupList1 = []
      ZkChatGroupApi.getZkChatGroupPage(params).then(response => {
        this.chatGroupList1.push(...response.data.list);
        let chatGroupList1 = this.chatGroupList1;
        this.$emit('sonSendData', {chatGroupList1});
        console.log(`chatGroupList1=============>${JSON.stringify(this.chatGroupList1)}`);
        // this.chatGroupList1.forEach(obj => {
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },
    /** 查询部门下拉树结构  */
    getTreeselect() {
      listSimpleDepts().then(response => {
        // 处理 deptOptions 参数
        this.deptOptions = [];
        this.deptOptions.push(...this.handleTree(response.data, "id"));
      });
    },
    // 格式化部门的下拉框
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
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
      if(item) {
        this.formData.tenantId = item.tenantId;
        this.formData.userId = item.id;
        this.formData.deptId = item.deptId;
        this.formData.userName = item.nickname;
        console.log("formData===========>"+JSON.stringify(this.formData));
      }
      console.log(`====userList1:${JSON.stringify(this.userList1)}===userId:${userId}======>item:`+JSON.stringify(item));
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
    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await ZkChatGroupUserApi.getZkChatGroupUser(id);
          this.formData = res.data;
          this.title = "修改群聊成员";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增群聊成员";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await ZkChatGroupUserApi.updateZkChatGroupUser(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkChatGroupUserApi.createZkChatGroupUser(data);
        this.$modal.msgSuccess("新增成功");
        this.dialogVisible = false;
        this.$emit('success');
      } finally {
        this.formLoading = false;
      }
    },
    /** 表单重置 */
    reset() {
      this.formData = {
        id: undefined,
        groupId: undefined,
        remark: undefined,
        userName: undefined,
        userType: undefined,
        userId: this.userInfo.userId,
        deptId: this.userInfo.deptId,
        userName: this.userInfo.nickname,
      };
      this.resetForm("formRef");
    }
  }
};
</script>