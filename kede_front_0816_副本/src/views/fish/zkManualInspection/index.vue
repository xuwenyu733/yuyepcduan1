<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="系统编号" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统编号" filterable clearable>
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName})`"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="车间" prop="workshopId">
        <el-select style="width:100%;" v-model="queryParams.workshopId" placeholder="请选择车间" filterable clearable @change="querySearch($event, -2.2)">
          <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="`${item.workshopName}`"
            :value="item.workshopId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="系统" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable @change="querySearch($event, -2.3)">
          <el-option v-for="(item, index) in systemList" :key="item.id+'__'+index" :label="`${item.pondSystemName}`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable @change="querySearch($event, -2.4)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondName}`"
            :value="item.pondDetailNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="巡检名称" prop="inspectionName">
        <el-input v-model="queryParams.inspectionName" placeholder="请输入巡检名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)" clearable size="small">
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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small" @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_MANUAL_INSPECTION_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-manual-inspection:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-manual-inspection:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="所属车间id" align="center" prop="workshopId" /> -->
      <!-- <el-table-column label="车间名称" align="center" prop="workshopName" /> -->
      <el-table-column label="系统编号" align="center" prop="pondNum" />
      <el-table-column label="巡检名称" align="center" prop="inspectionName" />
      <el-table-column label="巡检描述" align="center" prop="inspectionContent"  show-overflow-tooltip/>
      <!-- <el-table-column label="巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)" align="center" prop="status" /> -->
      <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_MANUAL_INSPECTION_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
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
                     v-hasPermi="['pl:zk-manual-inspection:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-manual-inspection:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="所属车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所属车间id" />
        </el-form-item>
        <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入车间名称" />
        </el-form-item> -->
        <!-- <el-form-item label="所在车间" prop="workshopId">
          <el-select style="width:100%;" v-model="form.workshopId" placeholder="请选择所在车间" filterable clearable  @change="updateWorkshopId">
            <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="item.workshopName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="所在存塘" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择所在存塘" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="巡检名称" prop="inspectionName">
          <el-input v-model="form.inspectionName" placeholder="请输入巡检名称" />
        </el-form-item>
        <el-form-item label="巡检描述">
          <editor v-model="form.inspectionContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_MANUAL_INSPECTION_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)" prop="status">
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkManualInspection, updateZkManualInspection, deleteZkManualInspection, getZkManualInspection, getZkManualInspectionPage, exportZkManualInspectionExcel } from "@/api/fish/zkManualInspection";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkWorkshop, updateZkWorkshop, deleteZkWorkshop, getZkWorkshop, getZkWorkshopPage, exportZkWorkshopExcel } from "@/api/fish/zkWorkshop";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";

export default {
  name: "ZkManualInspection",
  components: {
      FileBusUpload,
    Editor,
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
      // 人工巡检列表
      list: [],
      pondList: [],
      workshopList: [],
      systemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        workshopId: null,
        workshopName: null,
        inspectionName: null,
        inspectionContent: null,
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
    this.getList();
    // this.getWorkshopList();
    this.getPondList();
    
  },
  methods: {
    querySearch(item, searchType) {
      this.searchType = searchType;
      this.queryParams.batchNumber = "";
      this.queryParams.workshopId = "";
      this.queryParams.pondNum = "";
      this.queryParams.pondDetailNum = "";
      this.queryParams.logType = searchType;
      // console.log(`================>${JSON.stringify(this.pondList)}`);
      if(this.searchType == -2.1) {
        this.queryParams.batchNumber = item;
      } else if(this.searchType == -2.2) {
        this.queryParams.workshopId = item;
      } else if(this.searchType == -2.3) {
        // 系统
        let currPond = this.getPondInfo(this.pondList, null, item, null);
        this.queryParams.workshopId = currPond.workshopId;
        this.queryParams.pondNum = item;
      } else if(this.searchType == -2.4) {
        // 池号
        let currPond = this.getPondInfo(this.pondList, null, null, item);
        this.queryParams.workshopId = currPond.workshopId;
        this.queryParams.pondNum = currPond.pondNum;
        this.queryParams.pondDetailNum = item;
      }
      this.getList();
    },
    getPondInfo(list, workshopId, pondNum, pondDetailNum) {
      if(!(list && list.length)) return;
      let item = null;
      list.forEach((curr, index) => {
        if(workshopId == curr.workshopId) {
          item = curr;
          return;
        }
        if(pondNum == curr.pondNum) {
          item = curr;
          return;
        }
        if(pondDetailNum == curr.pondDetailNum) {
          item = curr;
          return;
        }
      })
      return item;
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkManualInspectionPage(this.queryParams).then(response => {
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
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondId = item.id;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        this.getWorkshopList();
      })
    },
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.pondList.forEach((item, index) => {
        if(!shopTemp.includes(item.workshopId)) {
          this.workshopList.push(item);
        }
        if(!systemTemp.includes(item.pondNum)) {
          this.systemList.push(item);
        }
      })
      // console.log(`workshopList====================>${JSON.stringify(this.workshopList)}`);
      // console.log(`systemList====================>${JSON.stringify(this.systemList)}`);
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        workshopId: undefined,
        workshopName: undefined,
        inspectionName: undefined,
        inspectionContent: undefined,
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
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加人工巡检";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkManualInspection(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改人工巡检";
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
          updateZkManualInspection(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkManualInspection(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除人工巡检编号为"' + id + '"的数据项?').then(function() {
          return deleteZkManualInspection(id);
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
      this.$modal.confirm('是否确认导出所有人工巡检数据项?').then(() => {
          this.exportLoading = true;
          return exportZkManualInspectionExcel(params);
        }).then(response => {
          this.$download.excel(response, '人工巡检.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
