<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="父编号" prop="parentId">
        <el-input v-model="queryParams.parentId" placeholder="请输入父编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="节点标题" prop="nodeTitle">
        <el-input v-model="queryParams.nodeTitle" placeholder="请输入节点标题" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="节点tag" prop="nodeTag">
        <el-input v-model="queryParams.nodeTag" placeholder="请输入节点tag" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- (1:鱼;2:水;3:设备;4:人;) -->
      <!-- <el-form-item label="节点类型" prop="nodeType">
        <el-select v-model="queryParams.nodeType" placeholder="请选择节点类型(1:鱼;2:水;3:设备;4:人;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item> -->
      <el-form-item label="节点类型" prop="nodeType">
        <el-select v-model="queryParams.nodeType" placeholder="请输入节点类型" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_KONWLEDGE_TREE_NODE_TYPE)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="记录人" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入记录人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件url" prop="fileUrl">
        <el-input v-model="queryParams.fileUrl" placeholder="请输入文件url" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-konwledge-tree:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-konwledge-tree:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="refreshTable" v-loading="loading" :data="knowledgeList" row-key="id" :default-expand-all="isExpandAll"
              :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column prop="nodeTitle" label="节点名称" :show-overflow-tooltip="true" width="250"></el-table-column>
      <!-- <el-table-column prop="id" label="编号" width="100"></el-table-column> -->
      <el-table-column prop="nodeTag" label="节点tag" :show-overflow-tooltip="true" />
      <el-table-column prop="nodeDescribe" label="节点描述" :show-overflow-tooltip="true" />
      <el-table-column prop="illName" label="疾病名称" :show-overflow-tooltip="true" />
      <el-table-column prop="handleContent" label="解决思路" :show-overflow-tooltip="true" />
      <el-table-column label="节点类型" align="center" prop="nodeType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_KONWLEDGE_TREE_NODE_TYPE" :value="scope.row.nodeType" />
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="60"></el-table-column>
      <!-- <el-table-column prop="permission" label="权限标识" :show-overflow-tooltip="true" /> -->
      <!-- <el-table-column prop="componentName" label="组件名称" :show-overflow-tooltip="true" /> -->
      <!-- <el-table-column prop="appPath" label="app路径" :show-overflow-tooltip="true" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-konwledge-tree:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
                     v-hasPermi="['pl:zk-konwledge-tree:create']">新增</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-konwledge-tree:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="父编号" prop="parentId">
          <el-input v-model="form.parentId" placeholder="请输入父编号" />
        </el-form-item> -->
        <el-form-item label="上级节点">
          <treeselect v-model="form.parentId" :options="knowledgeOptions" :normalizer="normalizer" :show-count="true"
                      placeholder="选择上级节点"/>
        </el-form-item>
        <el-form-item label="节点内容" prop="nodeTitle">
          <el-input v-model="form.nodeTitle" placeholder="请输入节点内容" />
        </el-form-item>
        <el-form-item label="节点描述" prop="nodeDescribe">
          <el-input v-model="form.nodeDescribe" placeholder="请输入节点描述" />
        </el-form-item>
        <el-form-item label="节点tag" prop="nodeTag">
          <el-input v-model="form.nodeTag" placeholder="请输入节点tag" />
        </el-form-item>
        <el-form-item label="解决思路" prop="handleContent">
          <el-input v-model="form.handleContent" placeholder="请输入解决思路" />
        </el-form-item>
        <el-form-item label="疾病名称" prop="illName">
          <el-input v-model="form.illName" placeholder="请输入疾病名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
        </el-form-item>
        <!-- <el-form-item label="节点类型(1:鱼;2:水;3:设备;4:人;)" prop="nodeType">
          <el-select v-model="form.nodeType" placeholder="请选择节点类型(1:鱼;2:水;3:设备;4:人;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="节点类型" prop="nodeType">
          <el-select v-model="form.nodeType" placeholder="请输入节点类型" filterable clearable size="small">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_KONWLEDGE_TREE_NODE_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="记录人" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入记录人" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入所属部门" />
        </el-form-item> -->
        <el-form-item label="文件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_konwledge_tree" @uploadSuccess="confirmUrl"></FileBusUpload>
        </el-form-item>
        <!-- <el-form-item label="文件url" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入文件url" />
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
import { createZkKonwledgeTree, updateZkKonwledgeTree, deleteZkKonwledgeTree, getZkKonwledgeTree, getZkKonwledgeTreePage, exportZkKonwledgeTreeExcel, listKnowledge } from "@/api/fish/zkKonwledgeTree";
import FileBusUpload from "@/components/FileUpload/index3.vue";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkKonwledgeTree",
  components: {
      FileBusUpload, Treeselect, 
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
      // 知识树列表
      list: [],
      knowledgeList: [],
      // 菜单树选项
      knowledgeOptions: [],
      // 重新渲染表格状态
      refreshTable: true,
      // 是否展开，默认全部折叠
      isExpandAll: false,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 500,
        parentId: null,
        nodeTitle: null,
        nodeTag: null,
        nodeType: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
        fileUrl: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        nodeType: [{ required: true, message: "节点类型(1:鱼;2:水;3:设备;4:人;)不能为空", trigger: "change" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    confirmUrl(url='') {
      this.form.fileUrl = url;
      // console.log("confirmUrl=====>"+JSON.stringify(this.form));
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      listKnowledge(this.queryParams).then(response => {
        // console.log(`=========================>${JSON.stringify(response)}`);
        this.list = response.data;
        this.list.forEach(item => {
          item.name = item.nodeTitle;
        });
        this.knowledgeList = this.handleTree(this.list, "id");
        // this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 查询菜单下拉树结构 */
    getTreeselect() {
      listKnowledge(this.queryParams).then(response => {
        this.list = response.data;
        this.list.forEach(item => {
          item.name = item.nodeTitle;
        })
        this.knowledgeOptions = [];
        const item = { id: 0, name: '主类目', children: [] };
        item.children = this.handleTree(this.list, "id");
        this.knowledgeOptions.push(item);
      });
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        parentId: undefined,
        nodeTitle: undefined,
        nodeTag: undefined,
        nodeType: undefined,
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        fileUrl: undefined,
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
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.id) {
        this.form.parentId = row.id;
        this.form.nodeType = row.nodeType;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加知识树";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 转换知识数据结构 */
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
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      const id = row.id;
      getZkKonwledgeTree(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改知识树";
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
          updateZkKonwledgeTree(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkKonwledgeTree(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除知识树编号为"' + id + '"的数据项?').then(function() {
          return deleteZkKonwledgeTree(id);
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
      this.$modal.confirm('是否确认导出所有知识树数据项?').then(() => {
          this.exportLoading = true;
          return exportZkKonwledgeTreeExcel(params);
        }).then(response => {
          this.$download.excel(response, '知识树.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
