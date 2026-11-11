<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="具体位置" prop="workshopPosition">
        <el-input v-model="queryParams.workshopPosition" placeholder="请输入具体位置" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
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
      <!-- <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="告警对象" prop="warnName">
        <el-input v-model="queryParams.warnName" placeholder="请输入告警对象" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small" @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_WARN_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="告警类型" prop="warnType">
        <el-select v-model="queryParams.warnType" placeholder="请输入告警类型" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_WARN_TYPE)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="状态(0:环境告警;1:事故告警;-1:病虫告警;)" prop="warnType">
        <el-select v-model="queryParams.warnType" placeholder="请选择状态(0:环境告警;1:事故告警;-1:病虫告警;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态(0:待处理;1:已处理;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:待处理;1:已处理;)" clearable size="small">
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-pond-warn:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-pond-warn:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="车间名称" align="center" prop="workshopName" />
      <!-- <el-table-column label="车间" align="center" prop="pondNum" /> -->
      <el-table-column label="系统" align="center" prop="pondNum" />
      <!-- <el-table-column label="池号" align="center" prop="pondDetailNum" /> -->
      <!-- <el-table-column label="具体位置" align="center" prop="workshopPosition" /> -->
      <!-- <el-table-column label="所属车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="存塘名称" align="center" prop="pondName" />
      <!-- <el-table-column label="所属存塘id" align="center" prop="pondId" /> -->
      <el-table-column label="告警对象" align="center" prop="warnName" />
      <!-- <el-table-column label="告警描述(0:环境告警;1:事故告警;-1:病虫告警;)" align="center" prop="warnType" /> -->
      <el-table-column label="告警类型" align="center" prop="warnType" show-overflow-tooltip sortable>
        <template slot-scope="scope">
          <dict-tag :type="DICT_TYPE.ZK_POND_WARN_TYPE" :value="scope.row.warnType" :class="getTagClass(scope.row.warnType)"></dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="告警描述" align="center" prop="warnContent" />
      <el-table-column label="负责人" align="center" prop="userName" show-overflow-tooltip/>
      <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_POND_WARN_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="状态(0:待处理;1:已处理;)" align="center" prop="status" /> -->
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
                     v-hasPermi="['pl:zk-pond-warn:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-pond-warn:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="系统编号" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择系统编号" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="告警对象" prop="warnDeviceId">
          <el-select style="width:100%;" v-model="form.warnDeviceId" placeholder="请选择告警设备" filterable clearable  @change="updateDeviceId">
            <el-option v-for="(item, index) in deviceList" :key="item.id+'__'+index" :label="`${item.deviceName}(${item.pondNum})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入车间名称" />
        </el-form-item>
        <el-form-item label="具体位置" prop="workshopPosition">
          <el-input v-model="form.workshopPosition" placeholder="请输入具体位置" />
        </el-form-item>
        <el-form-item label="所属车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所属车间id" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="所属存塘id" prop="pondId">
          <el-input v-model="form.pondId" placeholder="请输入所属存塘id" />
        </el-form-item>
        <el-form-item label="告警对象" prop="warnName">
          <el-input v-model="form.warnName" placeholder="请输入告警对象" />
        </el-form-item> -->
        <el-form-item label="告警类型" prop="warnType">
          <el-select v-model="form.warnType" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_WARN_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="告警类型(0:环境告警;1:事故告警;-1:病虫告警;)" prop="warnType">
          <el-select v-model="form.warnType" placeholder="请选择状态(0:环境告警;1:事故告警;-1:病虫告警;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="告警描述" prop="warnContent">
          <!-- <editor v-model="form.warnContent" :min-height="192"/> -->
          <el-input v-model="form.warnContent" placeholder="请输入告警描述" />
        </el-form-item>
        <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :value="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_WARN_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="状态(0:待处理;1:已处理;)" prop="status">
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

    <!-- websocket模块，能够暴露api方便智能体调用 -->
    <!-- <WebSocket @socketSonSend="socketParentReceive" isNav="false" parentTellMePage="/yangzhi/zk-pond-warn"></WebSocket> -->
  </div>
</template>

<script>
import { createZkPondWarn, updateZkPondWarn, deleteZkPondWarn, getZkPondWarn, getZkPondWarnPage, exportZkPondWarnExcel } from "@/api/fish/zkPondWarn";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
// import WebSocket from '@/components/WebSocket/index.vue';
import { UserInfoKey } from "@/utils/constants";
import { createZkDevice, updateZkDevice, deleteZkDevice, getZkDevice, getZkDevicePage, exportZkDeviceExcel } from "@/api/fish/zkDevice";

export default {
  name: "ZkPondWarn",
  components: {
    Editor, FileBusUpload
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
      // 告警列表
      list: [],
      pondList: [],
      deviceList: [],
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
        workshopName: null,
        workshopPosition: null,
        workshopId: null,
        pondName: null,
        pondId: null,
        warnDeviceId: null,
        warnName: null,
        warnType: null,
        warnContent: null,
        userName: null,
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
        // fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
        pondId: [{ required: true, message: "系统编号不能为空", trigger: "blur" }],
        warnDeviceId: [{ required: true, message: "告警对象不能为空", trigger: "blur" }],
        warnType: [{ required: true, message: "告警类型不能为空", trigger: "blur" }],
        warnContent: [{ required: true, message: "告警描述不能为空", trigger: "blur" }],
      },
      socketParam: {}, // 后台websocket发送给前台内容
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    console.log(`查看当前页面名称：`, this.$router.currentRoute.name);
    this.getList();
    this.getPondList();
    this.getDeviceList();
    this.judgeIsNeedAdd();
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
    /** 处理排序变化 */
    handleSortChange({ column, prop, order }) {
      this.queryParams.sortField = prop;
      this.queryParams.sortOrder = order === 'ascending' ? 'asc' : 'desc';
      this.getList();
    },
    /** 获取告警类型标签样式 */
    getTagClass(warnType) {
      // 将warnType统一转换为字符串进行比较
      const type = String(warnType);
      switch(type) {
        case '0': return 'red-tag';
        case '1': return 'yellow-tag';
        case '-1': return 'blue-tag';
        default: return '';
      }
    },     
    judgeIsNeedAdd() {
      const socketParam = this.$route.params && this.$route.params.socketParam && JSON.parse(this.$route.params.socketParam);
      this.socketParam = socketParam;
      console.log(`socketParam==========>`, socketParam);
      if(socketParam && socketParam.operateKey && socketParam.operateKey == "ADD_DATA" && socketParam.content) {
        let temp = JSON.parse(socketParam.content);
        if(temp.supplierId) temp.supplierId = parseInt(temp.supplierId)
        this.handleAdd(temp);
      }
    },
    socketParentReceive(msgParam){
      if(!(msgParam && msgParam.includes("{"))) return;
      let socketParam = JSON.parse(msgParam);
      this.socketParam = socketParam;
      console.log(`views/pl/zPondWarn/index.vue====location.href：${location.href}==>收到后台发送过来的消息：`, socketParam);
      if(socketParam && socketParam.operateKey == "SURE_ADD" && this.my_common.judgeOperateUrl(socketParam.targetPath, "/zk-pond-warn")) {
        if(!this.open) {
          // 如果弹窗都没有开启，就执行了第二步，肯定需要给他一个提示
          let replyParam = {"uid": this.userInfo.userId, "receiveMsg": "作为助手你还没有调用工具'add2'。", "isSyncMsg": true, "version": this.socketParam.version};
          this.$socketPublic.dispatch("sendMsg", JSON.stringify(replyParam), "zkPondWarn.vue submitForm");
          return;
        }
        this.submitForm(() => {
          this.$router.push({
          path: '/yangzhi/zk-pond-warn',
              query: { },
          });
        });
        console.log(`执行了点击操作`)
      } else if(socketParam && socketParam.operateKey && this.my_common.judgeOperateUrl(socketParam.targetPath, "/zk-pond-warn")
        && socketParam.operateKey == "ADD_DATA" && socketParam.content) {
        let temp = JSON.parse(socketParam.content);
        if(temp.supplierId) temp.supplierId = parseInt(temp.supplierId)
        if(!this.open) this.handleAdd(temp);
        else {
          this.form = {...this.form, ...temp};
          console.log(`this.form 2 ====>${JSON.stringify(this.form)}`);
          if(this.form.pondId) this.updatePondId(this.form.pondId);
          if(this.form.warnDeviceId) this.updateDeviceId(this.form.warnDeviceId);
        }
      }
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
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPondWarnPage(this.queryParams).then(response => {
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
    updateDeviceId(currId){
      let item = null;
      this.deviceList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      if(item) this.form.warnName = item.deviceName;
      console.log(`updateDeviceId item:${JSON.stringify(item)}===========>`+JSON.stringify(this.form));
    },
    getDeviceList() {
      this.deviceList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      if(this.form.pondId) params.pondId = this.form.pondId;
      getZkDevicePage(params).then(response => {
        this.deviceList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
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
      if(item && item.workshopName) {
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.historyFishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.pondSystemId = item.pondSystemId;
      }
      console.log("===========>"+JSON.stringify(this.form));
      // this.getDeviceList();
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
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        workshopName: undefined,
        workshopPosition: undefined,
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        warnName: undefined,
        warnType: undefined,
        warnContent: undefined,
        userName: undefined,
        status: "0",
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
    handleAdd(defaultParam) {
      this.reset();
      this.open = true;
      this.title = "添加告警";
      this.form.fid = getUUID().replaceAll('-', '');
      if(defaultParam) this.form = {...this.form, ...defaultParam};
      if(this.form.pondId) this.updatePondId(this.form.pondId);
      if(this.form.warnDeviceId) this.updateDeviceId(this.form.warnDeviceId);
      console.log(`this.form 2 handleAdd====>${JSON.stringify(this.form)}`);
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPondWarn(id).then(response => {
        this.form = response.data;
        console.log(`================>form:${JSON.stringify(this.form)}`);
        this.open = true;
        this.title = "修改告警";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid, invalidFields) => {
        console.log(`参数校验结果：${JSON.stringify(invalidFields)},添加故障告警的参数：${JSON.stringify(this.form)}`);
        // return;
        if (!valid) {
          let l1 = [];
          for(let key in invalidFields) {
            let to1 = invalidFields[key][0];
            l1.push(`${to1.field}:${to1.message}`)
          }
          let t1 = l1.join("、");
          console.log(`t1===>${t1}`);
          let replyParam = {"uid": this.userInfo.userId, "receiveMsg": t1, "isSyncMsg": true, "version": this.socketParam.version};
          this.$socketPublic.dispatch("sendMsg", JSON.stringify(replyParam), "zkPondWarn.vue submitForm");
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateZkPondWarn(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkPondWarn(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          let replyParam = {"uid": this.userInfo.userId, "receiveMsg": "数据入库成功！", "isSyncMsg": true, "version": this.socketParam.version};
          this.$socketPublic.dispatch("sendMsg", JSON.stringify(replyParam), "zkPondWarn.vue submitForm");
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除告警编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPondWarn(id);
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
      this.$modal.confirm('是否确认导出所有告警数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPondWarnExcel(params);
        }).then(response => {
          this.$download.excel(response, '告警.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style lang="scss" scope>
::v-deep .el-table-column__cell .dict-tag.red-tag {
  background-color: #ff4d4f !important;
  color: white !important;
}
::v-deep .el-table-column__cell .dict-tag.yellow-tag {
  background-color: #faad14 !important;
  color: white !important;
}
::v-deep .el-table-column__cell .dict-tag.blue-tag {
  background-color: #1890ff !important;
  color: white !important;
}


</style>
