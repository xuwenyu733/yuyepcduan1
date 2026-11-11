<template>
  <div class="app-container iframe_parent">
    <div v-if="loading">
      <iframe class="iframe" align="center" height="700px" width="100%" :src="iframe_url" frameborder="no" border="0"
        marginwidth="0" marginheight="0"></iframe>
    </div>
  </div>
</template>

<script>
import { createZkNetBox, updateZkNetBox, deleteZkNetBox, getZkNetBox, getZkNetBoxPage, exportZkNetBoxExcel } from "@/api/fish/zkNetBox";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkNetBox",
  components: {
      FileBusUpload,
  },
  data() {
    return {
      iframe_url: '',
      iframe_server: process.env.VUE_APP_NET_BOX_SERVER,
      token: '',
      zfile_username: '',
      zfile_password: '',
      currItem: {},
      // 遮罩层
      loading: false,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 网盘资源列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        fileType: 1,
        fileTypeName: null,
        prefixPath: null,
        remark: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fileType: [{ required: true, message: "资源类型(1:入职培训;2:运营手册;3:规章制度;4:工作任务及流程;)不能为空", trigger: "change" }],
        fileTypeName: [{ required: true, message: "资源类型名称不能为空", trigger: "blur" }],
        prefixPath: [{ required: true, message: "资源前缀不能为空", trigger: "blur" }],
        remark: [{ required: true, message: "备份不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  async created() {
    await this.getList();
    this.loading = false;
    
  },
  methods: {
    getToken() {
      let that = this;
      this.jq.ajax({
        type : "POST",
        url : `${this.iframe_server}/user/login`,
        data : JSON.stringify({"username": that.zfile_username,"password": that.zfile_password}),
        headers: {
          "Content-Type": "application/json;charset:utf-8;"
        },
        contentType : "application/json",
        success : function(data){
          that.token = data.data.token;
          // console.log(`zkNetBox============>data:${JSON.stringify(data.data.token)}`);
          // this.iframe_url = `${process.env.NET_BOX_PAGE}?queryToken=${this.token}`;
          // this.iframe_url = `http://192.168.10.131:5173/login?queryToken=${this.token}`;
          that.iframe_url = `${that.iframe_server}/login?queryToken=${that.token}&queryPath=${that.currItem.prefixPath}`;
          // console.log(`zkNetBox============>iframe_url:${that.iframe_url}`);
          that.loading = true;
        }
      });
    },
    /** 查询列表 */
    getList() {
      let that = this;
      this.loading = true;
      // 执行查询
      return getZkNetBoxPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.currItem = this.list[0];
        let remarkStr = that.currItem.remark;
        if(remarkStr) {
          let remarkJson = JSON.parse(remarkStr);
          that.zfile_username = remarkJson.zfile_username;
          that.zfile_password = remarkJson.zfile_password;
          that.getToken();
        } else {
          this.$modal.msgWarning("当前网盘配置信息有误！");
        }
        console.log(`==================>${JSON.stringify(this.currItem.prefixPath)}`);

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
        fileType: undefined,
        fileTypeName: undefined,
        prefixPath: undefined,
        remark: undefined,
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
      this.title = "添加网盘资源";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkNetBox(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改网盘资源";
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
          updateZkNetBox(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkNetBox(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除网盘资源编号为"' + id + '"的数据项?').then(function() {
          return deleteZkNetBox(id);
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
      this.$modal.confirm('是否确认导出所有网盘资源数据项?').then(() => {
          this.exportLoading = true;
          return exportZkNetBoxExcel(params);
        }).then(response => {
          this.$download.excel(response, '网盘资源.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
<style lang="scss" scoped>
.iframe_parent {
  width: 100%;
  min-height: 700px;
}
</style>