<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="环境层评分" prop="environmentalLayerRating">
        <el-input v-model="queryParams.environmentalLayerRating" placeholder="请输入环境层评分" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="行为层评分" prop="behavioralLayerRating">
        <el-input v-model="queryParams.behavioralLayerRating" placeholder="请输入行为层评分" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="机体层评分" prop="bodyLayerRating">
        <el-input v-model="queryParams.bodyLayerRating" placeholder="请输入机体层评分" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="综合评分" prop="overallRating">
        <el-input v-model="queryParams.overallRating" placeholder="请输入综合评分" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="环境层信息" prop="environmentalLayerInformation">
        <el-input v-model="queryParams.environmentalLayerInformation" placeholder="请输入环境层信息" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="行为层信息" prop="behavioralLayerInformation">
        <el-input v-model="queryParams.behavioralLayerInformation" placeholder="请输入行为层信息" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="机体层信息" prop="bodyLayerInformation">
        <el-input v-model="queryParams.bodyLayerInformation" placeholder="请输入机体层信息" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="异常信息" prop="exceptionMessage">
        <el-input v-model="queryParams.exceptionMessage" placeholder="请输入异常信息" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="异常个数" prop="numberOfExceptions">
        <el-input v-model="queryParams.numberOfExceptions" placeholder="请输入异常个数" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openForm(undefined)"
          v-hasPermi="['fish:zk-pond-health:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['fish:zk-pond-health:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch" v-hasPermi="['fish:zk-pond-health:delete']">
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="存塘id" align="center" prop="pondId" />
      <el-table-column label="环境层评分" align="center" prop="environmentalLayerRating" />
      <el-table-column label="行为层评分" align="center" prop="behavioralLayerRating" />
      <el-table-column label="机体层评分" align="center" prop="bodyLayerRating" />
      <el-table-column label="综合评分" align="center" prop="overallRating" />
      <el-table-column label="环境层信息" align="center" prop="environmentalLayerInformation" min-width="220" show-overflow-tooltip>
        <template v-slot="scope">{{ formatEnvironmentalSummary(scope.row.environmentalLayerInformation) }}</template>
      </el-table-column>
      <el-table-column label="行为层信息" align="center" prop="behavioralLayerInformation" min-width="200" show-overflow-tooltip>
        <template v-slot="scope">{{ formatBehavioralSummary(scope.row.behavioralLayerInformation) }}</template>
      </el-table-column>
      <el-table-column label="机体层信息" align="center" prop="bodyLayerInformation" min-width="200" show-overflow-tooltip>
        <template v-slot="scope">{{ formatBodySummary(scope.row.bodyLayerInformation) }}</template>
      </el-table-column>
      <el-table-column label="异常信息" align="center" prop="exceptionMessage" min-width="120" show-overflow-tooltip>
        <template v-slot="scope">{{ plainText(scope.row.exceptionMessage) }}</template>
      </el-table-column>
      <el-table-column label="异常个数" align="center" prop="numberOfExceptions" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="部门" align="center" prop="deptId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
          <span v-else>
            <deptName :deptId="scope.row.deptId"></deptName>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row.id)"
            v-hasPermi="['fish:zk-pond-health:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['fish:zk-pond-health:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 对话框(添加 / 修改) -->
    <ZkPondHealthForm ref="formRef" @success="getList" />
  </div>
</template>

<script>
import * as ZkPondHealthApi from '@/api/fish/zkpondhealth/index.js';
import ZkPondHealthForm from './ZkPondHealthForm.vue';
import { UserInfoKey } from "@/utils/constants";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { html2Text } from '@/utils/index';
import { formatEnvironmentalSummary, formatBehavioralSummary, formatBodySummary } from '@/utils/healthLayerDisplay';

export default {
  name: "ZkPondHealth",
  components: {
    ZkPondHealthForm
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
      // 鱼池健康列表
      list: [],
      pondList: [],
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 选中行
      currentRow: {},
      checkedIds: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        pondId: null,
        environmentalLayerRating: null,
        behavioralLayerRating: null,
        bodyLayerRating: null,
        overallRating: null,
        environmentalLayerInformation: null,
        behavioralLayerInformation: null,
        bodyLayerInformation: null,
        exceptionMessage: null,
        numberOfExceptions: null,
        createTime: [],
        deptId: null,
      },
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getUserList();
    this.getPondList();
    this.getList();
  },
  methods: {
    plainText(val) {
      if (val == null || val === '') return '';
      return html2Text(String(val)).replace(/\s+/g, ' ').trim();
    },
    formatEnvironmentalSummary,
    formatBehavioralSummary,
    formatBodySummary,
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
      this.form.pondScale = item.pondScale;
      this.form.historyFishNum = item.fishNum;
      this.form.pondId = item.id;
      // this.form.userName = item.userName;
      // this.form.userId = item.userId;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;

      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      return getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log(`========================>${JSON.stringify(this.pondList)}`);
      })
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
      this.form.userId = item.id;
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>"+JSON.stringify(item));
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          // let tempStr = `${obj.deptName}-${obj.nickname}`;
          let tempStr = `${obj.nickname}`;
          obj.nickname = tempStr;
        })
        // console.log("getUserList===========>"+JSON.stringify(this.userList1));
      })
    },
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true;
        const res = await ZkPondHealthApi.getZkPondHealthPage(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
      }
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
    /** 添加/修改操作 */
    openForm(id) {
      this.$refs["formRef"].open(id);
    },
    /** 删除按钮操作 */
    async handleDelete(row) {
      const id = row.id;
      await this.$modal.confirm('是否确认删除鱼池健康编号为"' + id + '"的数据项?')
      try {
        await ZkPondHealthApi.deleteZkPondHealth(id);
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    /** 批量删除鱼池健康 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认删除?')
      try {
        await ZkPondHealthApi.deleteZkPondHealthList(this.checkedIds);
        this.checkedIds = [];
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    handleRowCheckboxChange(records) {
      this.checkedIds = records.map((item) => item.id);
    },
    /** 导出按钮操作 */
    async handleExport() {
      await this.$modal.confirm('是否确认导出所有鱼池健康数据项?');
      try {
        this.exportLoading = true;
        const data = await ZkPondHealthApi.exportZkPondHealthExcel(this.queryParams);
        this.$download.excel(data, '鱼池健康.xls');
      } catch {
      } finally {
        this.exportLoading = false;
      }
    },
  }
};
</script>