<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="排班名称" prop="schedulingName">
        <el-input v-model="queryParams.schedulingName" placeholder="请输入排班名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_SCHEDULING_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="排班人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入排班人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="排班人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入排班人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="排班人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入排班人所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="排班人id" prop="userIdStr">
        <el-input v-model="queryParams.userIdStr" placeholder="请输入排班人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <!-- <el-form-item label="文件id" prop="fid">
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
                   v-hasPermi="['pl:zk-scheduling:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-scheduling:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="排班名称" align="center" prop="schedulingName" />
      <el-table-column label="排班描述" align="center" prop="schedulingContent" />
      <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_SCHEDULING_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="排班人" align="center" prop="userName" />
      <el-table-column label="任务时间" align="center" prop="schedulingTime" />
      <!-- <el-table-column label="排班人id" align="center" prop="userId" />
      <el-table-column label="排班人所属部门" align="center" prop="deptId" />
      <el-table-column label="状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)" align="center" prop="status" />
      <el-table-column label="排班人id" align="center" prop="userIdStr" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-scheduling:update']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-scheduling:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="排班名称" prop="schedulingName">
          <el-input v-model="form.schedulingName" placeholder="请输入排班名称" />
        </el-form-item>
        <el-form-item label="工作内容" prop="schedulingContent">
          <!-- <editor v-model="form.schedulingContent" :min-height="192"/> -->
          <el-input v-model="form.schedulingContent" placeholder="请输入工作内容" />
        </el-form-item>
        <el-form-item label="排班人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择排班人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="执行人员" prop="userIds">
          <el-select style="width:100%;" v-model="form.userIds" placeholder="请选择执行人员" filterable multiple clearable  @change="updateUserIds">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="巡检内容" prop="taskConfigIdArr">
          <el-select style="width:100%;" v-model="form.taskConfigIdArr" placeholder="请选择巡检内容(非巡检请勿选择)" filterable multiple clearable  @change="upateTaskConfigIds">
            <el-option v-for="(item, index) in schedulingConfiglist" :key="item.id+'_tconfig_'+index" :label="item.checkName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排班类别" prop="schedulingType">
          <el-select v-model="form.schedulingType" placeholder="请输入排班类别" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_SCHEDULING_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="鱼池编号" prop="pondIdArr" v-if="form.taskConfigIdArr && form.taskConfigIdArr.length>0">
          <el-select style="width:100%;" v-model="form.pondIdArr" placeholder="请选择鱼池编号" filterable multiple clearable >
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围" prop="tempTimeArr">
          <el-time-picker v-model="form.tempTimeArr" is-range format="HH:mm:ss" value-format="HH:mm:ss"
            :style="{width: '100%'}" start-placeholder="开始时间" end-placeholder="结束时间" range-separator="至"
            clearable></el-time-picker>
        </el-form-item>
        <el-form-item label="日期类型">
          <el-radio-group v-model="form.selectDayType" size="medium">
            <el-radio v-for="(item, index) in selectDayTypeList" :key="index" :label="item.value" :disabled="item.disabled">{{item.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排班日期">
          <div class="paiban_date">
            <div class="select_data">
              <el-checkbox-group v-model="form.selectDays">
                <!-- :disabled="item.disabled"   size="medium" -->
                <el-checkbox v-for="(item, index) in useDayList" :key="index" :label="item.info" :value="item.simpleFormat" border name="type" :checked="item.isSelected">
                  {{item.info}}
                </el-checkbox>
              </el-checkbox-group>
            </div>
            <div class="check_month">
              <div class="lastMonth1" @click.stop="update_plusMonth(-1)"><<上<<</div>
              <div style="height: 34px;width: 100%"></div>
              <div class="nextMonth2" @click.stop="update_plusMonth(1)">>>下>></div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_scheduling"></FileBusUpload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_SCHEDULING_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="排班人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入排班人id" />
        </el-form-item>
        <el-form-item label="排班人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入排班人" />
        </el-form-item>
        <el-form-item label="排班人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入排班人所属部门" />
        </el-form-item>
        <el-form-item label="状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排班人id" prop="userIdStr">
          <el-input v-model="form.userIdStr" placeholder="请输入排班人id" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <!--  v-if="!isUpdate" -->
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkScheduling, updateZkScheduling, deleteZkScheduling, getZkScheduling, getZkSchedulingPage, exportZkSchedulingExcel } from "@/api/fish/zkScheduling";
import { createZkDayRecord, updateZkDayRecord, deleteZkDayRecord, getZkDayRecord, getZkDayRecordPage, exportZkDayRecordExcel } from "@/api/fish/zkDayRecord";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { UserInfoKey } from "@/utils/constants";
import { createZkSchedulingType, updateZkSchedulingType, deleteZkSchedulingType, getZkSchedulingType, getZkSchedulingTypePage, exportZkSchedulingTypeExcel } from "@/api/fish/zkSchedulingType";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";

export default {
  name: "ZkScheduling",
  components: {
      FileBusUpload,
    Editor,
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
      // 排班列表
      list: [],
      dayList: [],
      pondList: [],
      useDayList: [],
      userList1: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      isUpdate: false, // 是否是更新
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        schedulingName: null,
        schedulingContent: null,
        userId: null,
        userName: null,
        deptId: null,
        status: null,
        userIdStr: null,
        checkTypes: null,
        createTime: [],
        fid: null,
        taskConfigIdArr: [],
      },
      schedulingConfiglist: [],
      // 表单参数
      form: {
        selectDays: [],
      },
      plusMonth: 0, // 当前月份
      selectDayTypeList: [
          {
            "label": "每日",
            "value": 1
          }, 
          {
            "label": "周末休息",
            "value": 2
          }, 
          {
            "label": "周末及假日休息",
            "value": 3
          }, 
          {
            "label": "自定义",
            "value": 4
          }
      ],
      // 表单校验
      rules: {
        schedulingName: [{ required: true, message: "排班名称不能为空", trigger: "blur" }],
        schedulingContent: [{ required: true, message: "工作内容不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "排班人不能为空", trigger: "blur" }],
        userIds: [{ required: true, message: "执行人员不能为空", trigger: "blur" }],
        taskConfigIdArr: [{ required: true, message: "巡检内容不能为空", trigger: "blur" }],
        schedulingType: [{ required: true, message: "排班类别不能为空", trigger: "blur" }],
        // pondIdArr: [{ required: true, message: "存塘编号不能为空", trigger: "blur" }],
      },
      schedulingTypeList: [],
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`===========>userInfo:${JSON.stringify(this.userInfo)}`);
    this.getList();
    this.getUserList();
    this.getDayList();
    this.getSchedulingConfigs();
    this.getPondList();
    this.schedulingTypeList = this.getDictDatas(DICT_TYPE.ZK_SCHEDULING_TYPE);
    // console.log(`=============>schedulingTypeList:${JSON.stringify(this.schedulingTypeList)}`);
  },
  watch: {
    'form.selectDayType': {
      handler(newV, oldV) {
        let tempList = this.useDayList;
        this.form.selectDays = [];
        // this.form.selectDays.push(this.useDayList[0].value);
        console.log("===>newV:{},oldV:{},selectDays:{}",newV, oldV)
        if (newV == 1) {
          tempList.forEach(item => {
            item.isSelected = true;
          })
        } else if(newV == 2){
          tempList.forEach(item => {
            if(item.dateType == 1) {item.isSelected = false;return;}
            item.isSelected = true;
          })
        } else if(newV == 3){
          tempList.forEach(item => {
            if(item.dateType == 1) {item.isSelected = false;return;}
            if(item.dateType == 2) {item.isSelected = false;return;}
            item.isSelected = true;
          })
        } else if(newV == 4){
          tempList.forEach(item => {
            item.isSelected = false;
          })
        }
        this.flushUserDayList(tempList);
      }
    },
    'form.selectDays': {
      handler(newV, oldV) {
        // console.log("===>newV:{},oldV:{}",newV, oldV);
      }
    },
    'form.schedulingName': {
      handler(newV, oldV) {
        this.form.schedulingContent = newV;
        // console.log("===>newV:{},oldV:{}",newV, oldV);
      }
    },
  },
  methods: {
    upateTaskConfigIds(taskConfigIds) {
      // console.log(`============>taskConfigIds:${JSON.stringify(this.form)}`);
      if(!(taskConfigIds && taskConfigIds.length>0)) return;
      this.form.taskConfigIds = taskConfigIds.join(",");
      let checkTypeArr = [];
      this.schedulingConfiglist.forEach(di => {
        if(taskConfigIds.includes(di.id)) {
          checkTypeArr.push(di.checkType);
        }
      })
      this.form.checkTypes = checkTypeArr.join(",");
    },
    getSchedulingConfigs() {
      let queryParams = {
        pageNo: 1,
        pageSize: 500
      };
      this.loading = true;
      // 执行查询
      getZkSchedulingTypePage(queryParams).then(response => {
        this.schedulingConfiglist = response.data.list;
        // console.log(`============>schedulingConfiglist:${JSON.stringify(this.schedulingConfiglist)}`);
        this.loading = false;
      });
    },
    update_plusMonth(type) {
      // console.log(`=======>type:${type}`);
      // if(this.plusMonth == 0 && type < 0) {
      //   this.$modal.msgError(`已经过去的时间不能够再进行排班了!`);
      //   return;
      // }
      this.plusMonth = this.plusMonth + type;
      this.getDayList();
    },
    flushUserDayList(tempList) {
      this.useDayList = [];
      this.$nextTick(() => {
        this.useDayList = tempList;
      })
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkSchedulingPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 查询列表 */
    getDayList(type = 0) {
      let that = this;
      let params = {
        pageNo: 1,
        pageSize: 500,
        plusMonth: that.plusMonth
      }
      // 执行查询
      getZkDayRecordPage(params).then(response => {
        this.dayList = response.data.list;
        this.useDayList = [];
        this.dayList.forEach(item => {
          let info = `${item.holidayName?item.holidayName:item.weekName}:${item.simpleFormat}`
          let temp = {
            ...item, isSelected: false, info, value:item.simpleFormat
          }
          this.useDayList.push(temp)
        })
        // console.log(`=======>获取日历：${JSON.stringify(this.useDayList)}` );
      });
    },
    updateUserIds(userIds) {
      // let item = null;
      // this.userList1.forEach(obj => {
      //   if(obj.id == userId) {
      //     item = obj;
      //     return;
      //   }
      // })
      // // 2. 执行 deptId、userId赋值
      // this.form.deptId = item.deptId;
      // this.form.userName = item.nickname;
      // console.log(`==>a:${userIds}`);
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
        this.pondList.push(...response.data.list);
        // console.log("pondList===========>"+JSON.stringify(this.pondList));
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
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
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.isUpdate = false;
      this.form = {
        id: undefined,
        schedulingName: undefined,
        schedulingContent: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        status: undefined,
        userIdStr: undefined,
        fid: undefined,
        schedulingType: "4",
        checkTypes: undefined,
        status: '1',
        selectDays: [],
        taskConfigIdArr: [],
        pondIdArr: [],
        tempTimeArr: ["09:00:00", "18:00:00"],
        selectDayType: 4, // 默认选择自定义
      };
      this.form.userId = this.userInfo.userId;
      this.form.deptId = this.userInfo.deptId;
      this.form.tenantId = this.userInfo.tenantId;
      this.form.userName = this.userInfo.nickname;
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
      this.title = "添加排班";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkScheduling(id).then(response => {
        this.form = {...this.form, ...response.data};
        this.open = true;
        this.isUpdate = true;
        this.title = "修改排班";
        if(this.form.taskConfigIds) {
          this.form.taskConfigIdArr = this.form.taskConfigIds.split(",").map(o => parseInt(o));
        }
        if(this.form.pondIds) {
          this.form.pondIdArr = [...this.form.pondIds.split(",").map(obj => parseInt(obj))];
          // this.form.pondIdArr = [];
        }
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
        if(this.form.selectDays) {
          let tempList = this.useDayList;
          tempList.forEach(item => {
            this.form.selectDays.forEach(iii => {
              if(iii == item.simpleFormat) {
                // console.log(`=========================>修改排班:${item.simpleFormat}`);
                item.isSelected = true;
              }
            })
          })
          this.flushUserDayList(tempList);
        }
        if(this.form.schedulingTime) {
          this.form.tempTimeArr = this.form.schedulingTime.split(",");
        }
        this.form.selectDayType = 4;
        console.log(`================>form:${JSON.stringify(this.form)}`);
      });
    },
    flushUsers(param) {
      if(param.tempTimeArr) {
        param.schedulingTime = param.tempTimeArr.join(",");
      }
      if(param.userIds) {
        let userList = [];
        this.userList1.forEach(item => {
          if(param.userIds.includes(item.id)) {
            userList.push(item);
          }
        })
        param.userList = userList;
      }
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        let param = JSON.parse(JSON.stringify(this.form));
        if(param.selectDays) {
          let arr = [];
          param.selectDays.filter(obj => obj!=null).forEach(item => arr.push(item.split(":")[1]));
          param.selectDays = arr.filter(obj => obj!=null);
        }
        if(this.my_common.listIsEmpty(param.selectDays)) {
          this.$modal.msgWarning("排班日期不能为空！");
          return;
        }
        if(param.pondIdArr) {
          param.pondIds = param.pondIdArr.join(",");
        }
        this.flushUsers(param);
        // console.log(`提交===============>${JSON.stringify(param)}`);
        // return;
        // 修改的提交
        if (param.id != null) {
          updateZkScheduling(param).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkScheduling(param).then(response => {
          this.$modal.msgSuccess(`新增成功${response.data}条排班明细数据。`);
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除排班编号为"' + id + '"的数据项?').then(function() {
          return deleteZkScheduling(id);
        }).then(res => {
          this.getList();
          this.$modal.msgSuccess(`成功删除${res.data}条排班明细数据。`);
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有排班数据项?').then(() => {
          this.exportLoading = true;
          return exportZkSchedulingExcel(params);
        }).then(response => {
          this.$download.excel(response, '排班.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
<style lang="scss" scope>
.paiban_date {
  // background-color: red;
  .el-button+.el-button, .el-checkbox.is-bordered+.el-checkbox.is-bordered {
    margin-left: 0;
  }
  .el-checkbox.is-bordered.el-checkbox--medium {
    width: 173px;
  }
  position: relative;
  display: flex;
  flex-direction: row;
  .select_data {
    width: 80%;
  }
  .check_month {
    width: 20%;
    height: auto;
    .lastMonth1, .nextMonth2 {
      cursor: pointer;
    }
  }
}
.lastMonth {
  position: absolute;
  top: 0;
  right: 10px;
  cursor: pointer;
}
.nextMonth {
  position: absolute;
  bottom: 60px;
  right: 10px;
  cursor: pointer;
}
</style>
