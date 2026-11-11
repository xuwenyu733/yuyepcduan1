<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-select v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-input v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="鱼苗数量" prop="fishNum">
        <el-input v-model="queryParams.fishNum" placeholder="请输入鱼苗数量" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-pond-fish:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-pond-fish:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="车间名称" align="center" prop="workshopName" />
      <!-- <el-table-column label="所属车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="存塘名称" align="center" prop="pondName" />
      <!-- <el-table-column label="所属存塘id" align="center" prop="pondId" /> -->
      <!-- <el-table-column label="鱼苗种类" align="center" prop="fishTypes" /> -->
      <el-table-column label="鱼苗种类" align="center" prop="flushInfo" />
      <el-table-column label="鱼苗数量" align="center" prop="fishNum" />
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
                     v-hasPermi="['pl:zk-pond-fish:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-pond-fish:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所在存塘" prop="workshopId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择所在存塘" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="item.workshopName+'__'+item.pondName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入车间名称" />
        </el-form-item>
        <el-form-item label="所属车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所属车间id" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="所属存塘id" prop="pondId">
          <el-input v-model="form.pondId" placeholder="请输入所属存塘id" />
        </el-form-item> -->
        <el-form-item label="鱼苗种类" prop="fishTypes">
          <el-input v-model="form.fishTypes" placeholder="请输入鱼苗种类" />
        </el-form-item>
        <el-form-item label="鱼苗数量" prop="fishNum">
          <el-input v-model="form.fishNum" placeholder="请输入鱼苗数量" />
        </el-form-item>
        <!-- <el-form-item label="负责人id" prop="userId">
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
import { createZkPondFish, updateZkPondFish, deleteZkPondFish, getZkPondFish, getZkPondFishPage, exportZkPondFishExcel } from "@/api/fish/zkPondFish";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";


export default {
  name: "ZkPondFish",
  components: {
    FileBusUpload,
  },
  data() {
    return {
      operationLoading: false,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 存塘鱼苗列表
      list: [],
      pondList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        workshopName: null,
        workshopId: null,
        pondName: null,
        pondId: null,
        fishTypes: null,
        fishNum: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fishNum: [{ required: true, message: "鱼苗数量不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
    this.getPondList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPondFishPage(this.queryParams).then(response => {
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
      })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        workshopName: undefined,
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        fishTypes: undefined,
        fishNum: undefined,
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
      this.title = "添加存塘鱼苗";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPondFish(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改存塘鱼苗";
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
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        this.operationLoading = true;
        const loading = this.$loading({
          lock: true,
          text: 'Loading',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        // 修改的提交
        if (this.form.id != null) {
          updateZkPondFish(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
            setTimeout(()=>{
              this.operationLoading = false;
              loading.close();
            }, 1000);
          });
          return;
        }
        // 添加的提交
        createZkPondFish(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          setTimeout(()=>{
            this.operationLoading = false;
            loading.close();
          }, 1000);
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除存塘鱼苗编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPondFish(id);
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
      this.$modal.confirm('是否确认导出所有存塘鱼苗数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPondFishExcel(params);
        }).then(response => {
          this.$download.excel(response, '存塘鱼苗.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
