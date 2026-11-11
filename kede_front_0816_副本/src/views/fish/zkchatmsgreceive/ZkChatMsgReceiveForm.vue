<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="聊天消息" prop="chatMsgId">
          <el-select style="width:100%;" v-model="formData.chatMsgId" placeholder="请选择聊天消息" filterable clearable>
            <el-option v-for="(item, index) in chatMsgList1" :key="item.id+'__'+index" :label="`(${item.userName}->${item.targetUserName}/${item.groupName})${my_common.strShow(item.msg, 19)}`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="消息处理类型" prop="handleType">
          <el-select v-model="formData.handleType" placeholder="请输入消息处理类型" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_CHAT_MSG_RECEIVE_HANDLE_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="消息所属人" prop="userId">
          <el-input v-model="formData.userId" placeholder="请输入消息所属人" />
        </el-form-item>
        <el-form-item label="所属人部门" prop="deptId">
          <el-input v-model="formData.deptId" placeholder="请输入所属部门" />
        </el-form-item> -->
        <el-form-item label="消息所属人" prop="userId">
          <el-select style="width:100%;" v-model="formData.userId" placeholder="请选择消息所属人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属人部门" prop="deptId">
          <treeselect v-model="formData.deptId" :options="deptOptions" :show-count="true" :clearable="false"
                      placeholder="请选择所属人部门" :normalizer="normalizer"/>
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
import * as ZkChatMsgReceiveApi from '@/api/fish/zkchatmsgreceive';
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import {listSimpleDepts} from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { UserInfoKey } from "@/utils/constants";
import * as ZkChatMsgApi from '@/api/fish/zkchatmsg';

export default {
  name: "ZkChatMsgReceiveForm",
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
        chatMsgId: undefined,
        userId: undefined,
        handleType: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        chatMsgId: [{ required: true, message: '聊天消息id不能为空', trigger: 'blur' }],
      },
      userList1: [],
      userInfo: {},
      // 部门树选项
      deptOptions: undefined,
      chatMsgList1: [],
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`userInfo================>${JSON.stringify(this.userInfo)}`);
    this.getUserList();
    this.getTreeselect();
    this.getChatMsgList();
  },
  methods: {
    getChatMsgList() {
      let params = {currPage:1, pageSize: 500};
      this.chatMsgList1 = []
      ZkChatMsgApi.getZkChatMsgPage(params).then(response => {
        this.chatMsgList1.push(...response.data.list);
        // this.chatMsgList1.forEach(obj => {
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
          const res = await ZkChatMsgReceiveApi.getZkChatMsgReceive(id);
          this.formData = {...res.data, handleType: `${res.data.handleType}`};
          // console.log(`==========>`, typeof this.formData.handleType);
          this.updateUserId(this.formData.userId);
          this.title = "修改聊天消息接受";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增聊天消息接受";
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
          await ZkChatMsgReceiveApi.updateZkChatMsgReceive(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await ZkChatMsgReceiveApi.createZkChatMsgReceive(data);
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
        chatMsgId: undefined,
        handleType: "0",
        userId: this.userInfo.userId,
        deptId: this.userInfo.deptId,
      };
      this.resetForm("formRef");
    }
  }
};
</script>