<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="系统编号" prop="systemNum">
        <el-input v-model="queryParams.systemNum" placeholder="请输入系统编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="系统名称" prop="systemName">
        <el-input v-model="queryParams.systemName" placeholder="请输入系统名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所在车间" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入所在车间" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="所在车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所在车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-pond-system:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-pond-system:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="id" align="center" prop="id" /> -->
      <el-table-column label="基地" align="center" prop="deptName" />
      <el-table-column label="系统编号" align="center" prop="systemNum" />
      <el-table-column label="系统名称" align="center" prop="systemName" />
      <el-table-column label="所在车间" align="center" prop="workshopName" />
      <!-- <el-table-column label="所在车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="负责人" align="center" prop="userName" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="负责人id" align="center" prop="userId" /> -->
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <!-- <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-pond-system:update']">修改</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-pond-system:delete']">删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所在车间" prop="workshopId">
          <el-select style="width:100%;" v-model="form.workshopId" placeholder="请选择所在车间" filterable clearable  @change="updateWorkshopId">
            <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="item.workshopName"
              :value="item.id"></el-option>
          </el-select>
          <div style="" class="toPondSystem" @click.stop="toPondSystem">添加车间</div>
        </el-form-item>
        <el-form-item label="系统编号" prop="systemNum">
          <el-input v-model="form.systemNum" placeholder="请输入系统编号" />
        </el-form-item>
        <el-form-item label="系统名称" prop="systemName">
          <el-input v-model="form.systemName" placeholder="请输入系统名称" />
        </el-form-item>
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择负责人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="归属部门" prop="deptId">
          <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" :clearable="false"
                      placeholder="请选择归属部门" :normalizer="normalizer"/>
        </el-form-item>
        <!-- <el-form-item label="所在车间" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入所在车间" />
        </el-form-item>
        <el-form-item label="所在车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所在车间id" />
        </el-form-item> -->
        <!-- <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkPondSystem, updateZkPondSystem, deleteZkPondSystem, getZkPondSystem, getZkPondSystemPage, exportZkPondSystemExcel } from "@/api/fish/zkPondSystem";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkWorkshop, updateZkWorkshop, deleteZkWorkshop, getZkWorkshop, getZkWorkshopPage, exportZkWorkshopExcel } from "@/api/fish/zkWorkshop";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import {listSimpleDepts} from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { UserInfoKey } from "@/utils/constants";
import { notifySharedSiteOptionsChanged, resolveCurrentUserDeptId } from "@/utils/siteOptions";

export default {
  name: "ZkPondSystem",
  components: {
      FileBusUpload,
    Treeselect, 
  },
  data() {
    return {
      userInfo: {},
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 存塘系统列表
      list: [],
      workshopList: [], 
      userList1: [],
      // 弹出层标题
      title: "",
      // 部门树选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        systemNum: null,
        systemName: null,
        workshopName: null,
        workshopId: null,
        userName: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        workshopId: [{ required: true, message: "所在车间id不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    const deptId = resolveCurrentUserDeptId();
    if (deptId != null) this.queryParams.deptId = deptId;
    this.getList();
    this.getUserList();
    this.getWorkshopList();
    this.getTreeselect();
  },
  methods: {
    /** 查询部门下拉树结构 + 岗位下拉 */
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
    toPondSystem() {
      this.open = false;
      this.$router.push({
          path: '/jidi/zk-workshop',
          params: {},
          query: {  },
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPondSystemPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    getListAddDept(deptId) {
      this.queryParams = {...this.queryParams, deptId};
      this.getList();
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
      this.form.userId = userId;
      this.form.tenantId = item.tenantId;
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
    updateWorkshopId(currId){
      let item = null;
      this.workshopList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getWorkshopList() {
      this.workshopList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkWorkshopPage(params).then(response => {
        this.workshopList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
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
        systemNum: undefined,
        systemName: undefined,
        workshopName: undefined,
        workshopId: undefined,
        userName: undefined,
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
      this.title = "添加存塘系统";
      this.form.fid = getUUID().replaceAll('-', '');
      this.updateUserId(this.userInfo.userId);
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPondSystem(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改存塘系统";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
        if(!this.form.userId) {
          this.updateUserId(this.userInfo.userId);
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
          updateZkPondSystem(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
            notifySharedSiteOptionsChanged();
          });
          return;
        }
        // 添加的提交
        createZkPondSystem(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          notifySharedSiteOptionsChanged();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除存塘系统编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPondSystem(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
          notifySharedSiteOptionsChanged();
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有存塘系统数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPondSystemExcel(params);
        }).then(response => {
          this.$download.excel(response, '存塘系统.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.toPondSystem {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  padding-right: 20px;
  padding-bottom: 0px;
  cursor: pointer;
  height: 15px;
}
</style>