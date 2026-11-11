<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="修正人" prop="userId">
        <el-select style="width:100%;" v-model="queryParams.userId" placeholder="请选择修正人" filterable clearable>
          <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="修正时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="修正种类" prop="reviseType">
        <el-select v-model="queryParams.reviseType" placeholder="请输入修正种类" filterable clearable @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DATA_REVISE_REVISE_TYPE)" :key="dict.value"
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
                   v-hasPermi="['pl:zk-data-revise:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-data-revise:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="系统" align="center" prop="pondSystemName" />
      <!-- <el-table-column label="系统id" align="center" prop="pondSystemId" /> -->
      <el-table-column label="池号" align="center" prop="pondNum" />
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" />
      <el-table-column label="存塘id" align="center" prop="pondId" />
      <el-table-column label="存塘编号" align="center" prop="pondDetailNum" /> -->
      <!-- <el-table-column label="操作类型" align="center" prop="reviseType" /> -->
      <el-table-column label="操作类型" align="center" prop="reviseType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_DATA_REVISE_REVISE_TYPE" :value="scope.row.reviseType" />
        </template>
      </el-table-column>
      <el-table-column label="修正日期" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人(组长)" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <userName :user-id="scope.row.userId"></userName>
        </template>
      </el-table-column>
      <el-table-column label="原数据" align="center" prop="originData" />
      <el-table-column label="差值" align="center" prop="differenceData" />
      <el-table-column label="变化比例" align="center" prop="changeRatio" />
      <el-table-column label="原因" align="center" prop="reason" />
      <el-table-column label="技术总监(意见及批复)" align="center" prop="technicalDirectorsOpinion" />
      <el-table-column label="厂长(意见及批复)" align="center" prop="directorsOpinion" />
      <!-- <el-table-column label="业务id" align="center" prop="busId" />
      <el-table-column label="记录人" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleDetail(scope.row)" v-if="false">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-data-revise:update']">修正</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-data-revise:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修正) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="操作类型" prop="reviseType">
          <el-select v-model="form.reviseType" placeholder="请输入操作类型" filterable clearable @change="update_reviseType">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DATA_REVISE_REVISE_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="投放记录" prop="workshopId" v-if="form.reviseType == 1">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择投放记录" filterable clearable  @change="update_push">
            <el-option v-for="(item, index) in realList_push" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(于${parseTime(item.createTime)}投放${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="死亡记录" prop="workshopId" v-if="form.reviseType == 2">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择死亡记录" filterable clearable  @change="update_dead">
            <el-option v-for="(item, index) in realList_dead" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(于${parseTime(item.createTime)}死亡${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分池记录" prop="workshopId" v-if="form.reviseType == 3">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择分池记录" filterable clearable  @change="update_split">
            <el-option v-for="(item, index) in realList_split" :key="item.id+'__'+index" :label="`${item.pondDetailNum}->${item.targetPondDetailNum}(于${parseTime(item.createTime)}分池${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投喂记录" prop="workshopId" v-if="form.reviseType == 4">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择投喂记录" filterable clearable  @change="update_feed">
            <el-option v-for="(item, index) in realList_feed" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(于${parseTime(item.createTime)}投喂${item.fishNum}条,规格:${item.fishSpec})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="存塘记录" prop="workshopId" v-if="form.reviseType == 5">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择存塘记录" filterable clearable  @change="update_pond">
            <el-option v-for="(item, index) in realList_pond" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="原数据" prop="originData">
          <el-input v-model="form.originData" placeholder="请输入原数据" />
        </el-form-item>
        <el-form-item label="差值" prop="differenceData">
          <el-input v-model="form.differenceData" placeholder="请输入差值" />
        </el-form-item>
        <el-form-item label="变化比例" prop="changeRatio">
          <el-input v-model="form.changeRatio" placeholder="请输入变化比例" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" placeholder="请输入原因" />
        </el-form-item>
        <el-form-item label="技术总监意见" prop="technicalDirectorsOpinion">
          <el-input v-model="form.technicalDirectorsOpinion" placeholder="请输入技术总监意见" />
        </el-form-item>
        <el-form-item label="厂长意见" prop="directorsOpinion">
          <el-input v-model="form.directorsOpinion" placeholder="请输入厂长意见" />
        </el-form-item>
        <!-- <el-form-item label="业务id" prop="busId">
          <el-input v-model="form.busId" placeholder="请输入业务id" />
        </el-form-item>
        <el-form-item label="记录人" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入记录人" />
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
import { createZkDataRevise, updateZkDataRevise, deleteZkDataRevise, getZkDataRevise, getZkDataRevisePage, exportZkDataReviseExcel } from "@/api/fish/zkDataRevise";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import {getTaskListByProcessInstanceId} from "@/api/bpm/task";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadDeptScopedPondList } from "@/utils/siteOptions";
import { notifyProductionRecordChanged } from "@/utils/productionRecordBridge";
import { createZkFeedingLog, updateZkFeedingLog, deleteZkFeedingLog, getZkFeedingLog, getZkFeedingLogPage, exportZkFeedingLogExcel } from "@/api/fish/zkFeedingLog";
import { createZkPondFishDetail, updateZkPondFishDetail, deleteZkPondFishDetail, getZkPondFishDetail, getZkPondFishDetailPage, exportZkPondFishDetailExcel } from "@/api/fish/zkPondFishDetail";

export default {
  name: "ZkDataRevise",
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
      // 数据修正记录列表
      list: [],
      // 弹出层标题
      title: "",
      pondList: [],
      workshopList: [], 
      pondSystemList: [], 
      systemList: [], 
      userList1: [],
      realList_push: [], // 投放真实数据list
      realList_dead: [], // 死亡真实数据list
      realList_split: [], // 分池真实数据list
      realList_feed: [], // 投喂真实数据list
      realList_pond: [], // 存塘真实数据list
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        workshopId: null,
        workshopName: null,
        pondSystemName: null,
        pondSystemId: null,
        pondNum: null,
        pondName: null,
        pondId: null,
        pondDetailNum: null,
        reviseType: null,
        originData: null,
        differenceData: null,
        changeRatio: null,
        reason: null,
        technicalDirectorsOpinion: null,
        directorsOpinion: null,
        busId: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      taskParam: {//流程参数
        approveUserId: '',
        processKey: 'zk_data_revise',
        approveNo: '',
        businessKey: '',
        taskId: '',
        processInstanceId: '',
        finishProcess: 'n',
        approveMessage: '',
        variables: {},
        submitToPrev: 'y'//退回后的流程再次提交直接到上一次的退回人那里
      },
      // 表单校验
      rules: {
        reviseType: [{ required: true, message: "操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)不能为空", trigger: "change" }],
        originData: [{ required: true, message: "原数据不能为空", trigger: "blur" }],
        differenceData: [{ required: true, message: "差值不能为空", trigger: "blur" }],
        changeRatio: [{ required: true, message: "变化比例不能为空", trigger: "blur" }],
        // reason: [{ required: true, message: "原因不能为空", trigger: "blur" }],
        // technicalDirectorsOpinion: [{ required: true, message: "技术总监意见不能为空", trigger: "blur" }],
        // directorsOpinion: [{ required: true, message: "厂长意见不能为空", trigger: "blur" }],
        // fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
    this.getUserList();
    this.getPondList();
  },
  watch: {
    'form.differenceData': {
      handler(newV, oldV) {
        console.log("===>newV:{},oldV:{}",newV, oldV)  // salesIntensity
        if (newV) {
          this.form.changeRatio = this.my_common.computer(newV, this.form.originData, 3, 2);
          // this.form.procurementCost = newV*this.form.catalogPrice;
          // this.form.salesPrice = this.my_common.computer(newV, this.form.catalogPrice, 2, 2);
        }
      }
    },
  },
  methods: {
    // 操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)
    update_reviseType(reviseType) {
      // console.log(`=========>a:${reviseType}`);
      let queryParams = {
        pageNo: 1,
        pageSize: 500,
        addType: "1", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
      };
      if(reviseType == 1) { // 投放
        queryParams.addType = "1";
        this.getFishDetailList(reviseType, queryParams);
      } else if(reviseType == 2) { // 死亡
        queryParams.addType = "-2";
        this.getFishDetailList(reviseType, queryParams);
      } else if(reviseType == 3) { // 分池
        queryParams.addType = "-3";
        this.getFishDetailList(reviseType, queryParams);
      } else if(reviseType == 4) { // 投喂
        queryParams.addType = undefined;
        this.getFeedList(queryParams);
      } else if(reviseType == 5) { // 存塘
        // 开始的时候就已经获取
      }
    },
    handleDetail(row) {
      const processInstanceId = row.processInstanceId;
      getTaskListByProcessInstanceId(processInstanceId).then(response => {
        if(response.data && response.data.length > 0){
          // this.$router.push({ path: "/workflow/process-instance/detail", query: {task: encodeURIComponent(JSON.stringify(response.data[0]))}});
          this.$router.push({ path: "/workflow/process-instance/detail?taskId=" + response.data[0].taskId, query: {}});
        }
      });
    },
    update_push(busId) {
      let currItem = null;
      this.realList_push.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.fishPush = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_dead(busId) {
      let currItem = null;
      this.realList_dead.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.fishDead = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_split(busId) {
      let currItem = null;
      this.realList_split.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.fishSplit = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_feed(busId) {
      let currItem = null;
      this.realList_feed.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.feedCount;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.feedingLog = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    update_pond(busId) {
      let currItem = null;
      this.realList_pond.forEach((item, index) => {
        if(busId == item.id) {
          currItem = item;
          return;
        }
      })
      if(currItem) {
        this.form.originData = currItem.fishNum;
        this.form.busId = busId;
        this.updateOther(currItem);
        this.form.pondData = currItem;
      }
      console.log(`update_push=========>a:${busId},form:${JSON.stringify(this.form)}`);
    },
    updateOther(item) {
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.fishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
    },
    getFeedList(queryParams) {
      // 执行查询
      getZkFeedingLogPage(queryParams).then(response => {
        this.realList_feed = response.data.list;
      });
    },
    getFishDetailList(reviseType, queryParams) {
      // 执行查询
      getZkPondFishDetailPage(queryParams).then(response => {
        if(reviseType == 1) { // 投放
          this.realList_push = response.data.list;
        } else if(reviseType == 2) { // 死亡
          this.realList_dead = response.data.list;
        } else if(reviseType == 3) { // 分池
          this.realList_split = response.data.list;
        }
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkDataRevisePage(this.queryParams).then(response => {
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
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        workshopId: undefined,
        workshopName: undefined,
        pondSystemName: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondName: undefined,
        pondId: undefined,
        pondDetailNum: undefined,
        reviseType: undefined,
        originData: undefined,
        differenceData: undefined,
        changeRatio: undefined,
        reason: undefined,
        technicalDirectorsOpinion: undefined,
        directorsOpinion: undefined,
        busId: undefined,
        userId: undefined,
        deptId: undefined,
        fid: undefined,
      };
      this.resetForm("form");
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
      this.form.fishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      loadDeptScopedPondList().then(list => {
        const rows = Array.isArray(list) ? list.slice() : []
        this.realList_pond = rows
        this.pondList = rows.slice()
        this.getWorkshopList();
      }).catch(() => {
        this.pondList = []
        this.realList_pond = []
      })
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
      this.title = "添加数据修正记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修正按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkDataRevise(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修正数据修正记录";
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
        this.form.taskParam = this.taskParam;
        // console.log(`============>`, this.form.createTimeStr);
        // return;
        // 修正的提交
        if (this.form.id != null) {
          updateZkDataRevise(this.form).then(response => {
            this.$modal.msgSuccess("修正成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkDataRevise(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除数据修正记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkDataRevise(id);
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
      this.$modal.confirm('是否确认导出所有数据修正记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkDataReviseExcel(params);
        }).then(response => {
          this.$download.excel(response, '数据修正记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    },
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        // this.userList1.forEach(obj => {
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },
  }
};
</script>
