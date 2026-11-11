<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="所在车间" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入所在车间" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="系统编号" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统编号" filterable clearable>
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName})`"
            :value="item.pondNum"></el-option>
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
          <el-option v-for="(item, index) in systemList" :key="item.id+'__'+index" :label="`${item.workshopName} ${item.pondNum}`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable @change="querySearch($event, -2.4)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.workshopName} ${item.pondNum}(${item.pondName} ${item.pondDetailNum})`"
            :value="item.pondDetailNum"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="设备编码" prop="deviceSn">
        <el-input v-model="queryParams.deviceSn" placeholder="请输入设备编码" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="监测指标ID" prop="deviceMetricId">
        <el-input v-model="queryParams.deviceMetricId" placeholder="请输入监测指标ID" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small"  @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DEVICE_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="所在车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所在车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态(0:关闭中;1:开启中;-1:已废弃;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:关闭中;1:开启中;-1:已废弃;)" clearable size="small">
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
        <!--  v-hasPermi="['pl:zk-device:create']" -->
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                  >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-device:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-menu" size="mini" @click="showDeviceCard">
          卡片展示
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-monitor" size="mini" @click="showSystemDisplay">
          系统展示
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="所在车间" align="center" prop="workshopName" /> -->
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="系统" align="center" prop="pondNum" />
      <el-table-column label="池号" align="center" prop="pondDetailNum" />
      <el-table-column label="设备名称" align="center" prop="deviceName" show-overflow-tooltip>
        <template v-slot="scope">
          <span style="cursor: pointer;" @click.stop="toDetail(scope.row)">{{ scope.row.deviceName }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="设备名称" align="center" prop="deviceName" /> -->
      <el-table-column label="设备编码" align="center" prop="deviceSn" show-overflow-tooltip/>
      <el-table-column label="设备单元" align="center" prop="deviceMetricId" show-overflow-tooltip/>
      <!-- <el-table-column label="所在车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="负责人" align="center" prop="userName" show-overflow-tooltip/>
      <!-- <el-table-column label="状态(0:关闭中;1:开启中;-1:已废弃;)" align="center" prop="status" /> -->
      <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_DEVICE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="设备类型" align="center" prop="deviceType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_DEVICE_TYPE" :value="scope.row.deviceType" />
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
        <!--  v-hasPermi="['pl:zk-device:update']" -->
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                    >修改</el-button>
        <!--  v-hasPermi="['pl:zk-device:delete']" -->
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                    >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!--  @change="updateSystemId" -->
        <!-- <el-form-item label="所在车间" prop="workshopId">
          <el-select style="width:100%;" v-model="form.workshopId" placeholder="请选择所在车间" filterable clearable  @change="updateWorkshopId">
            <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="item.workshopName"
              :value="item.workshopId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="系统" prop="systemId">
          <el-select style="width:100%;" v-model="form.systemId" placeholder="请选择系统" filterable clearable >
            <el-option v-for="(item, index) in systemList" :key="item.id+'__'+index" :label="`${item.workshopName}-${item.pondNum}`"
              :value="item.pondSystemId"></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="所在存塘" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择所在存塘" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.workshopName} ${item.pondNum}(${item.pondName} ${item.pondDetailNum})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备编码" prop="deviceSn">
          <el-input v-model="form.deviceSn" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item label="设备单元" prop="deviceMetricId">
          <el-input v-model="form.deviceMetricId" placeholder="请输入设备单元" />
        </el-form-item>

        <!-- <el-form-item label="最小值" prop="standardStart">
          <el-input v-model="form.standardStart" placeholder="请输入最小值" />
        </el-form-item>
        <el-form-item label="最小值符号" prop="startComputer">
          <el-select v-model="form.startComputer" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.COMPUTER_SYMBOL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大值" prop="standardEnd">
          <el-input v-model="form.standardEnd" placeholder="请输入最大值" />
        </el-form-item>
        <el-form-item label="最大值符号" prop="endComputer">
          <el-select v-model="form.endComputer" placeholder="请输入最大值符号" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.COMPUTER_SYMBOL)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="设备值单位" prop="deviceUnit">
          <el-input v-model="form.deviceUnit" placeholder="请输入设备值单位" />
        </el-form-item>
        <!-- <el-form-item label="当前值" prop="currValue">
          <el-input v-model="form.currValue" placeholder="请输入当前值" />
        </el-form-item> -->
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DEVICE_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="form.deviceType" placeholder="请输入设备类型" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DEVICE_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择负责人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="相关附件" prop="fid" v-if="false">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item> -->
        <!-- <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DEVICE_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="所在车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所在车间id" />
        </el-form-item>
        <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="状态(0:关闭中;1:开启中;-1:已废弃;)" prop="status">
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
import { createZkDevice, updateZkDevice, deleteZkDevice, getZkDevice, getZkDevicePage, exportZkDeviceExcel } from "@/api/fish/zkDevice";
import { KE_DE_ROLES } from "@/utils/constants";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { listSimpleUsersByRole } from "@/api/system/user";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { UserInfoKey } from "@/utils/constants";
import { createZkWorkshop, updateZkWorkshop, deleteZkWorkshop, getZkWorkshop, getZkWorkshopPage, exportZkWorkshopExcel } from "@/api/fish/zkWorkshop";

export default {
  name: "ZkDevice",
  components: {
    FileBusUpload, 
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
      // 设备列表
      list: [],
      workshopList: [], 
      systemList: [],
      userList1: [],
      pondList: [], // 存塘列表
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        deviceName: null,
        deviceSn: null,
        deviceMetricId: null,
        pondNum: null,
        pondSystemId: null,
        workshopName: null,
        workshopId: null,
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
        pondId: [{ required: true, message: "所在存塘不能为空", trigger: "blur" }],
        deviceName: [{ required: true, message: "设备名称不能为空", trigger: "blur" }],
        deviceSn: [{ required: true, message: "设备编码不能为空", trigger: "blur" }],
        deviceMetricId: [{ required: true, message: "设备单元不能为空", trigger: "blur" }],
        // deviceUnit: [{ required: true, message: "设备值单位不能为空", trigger: "blur" }],
        status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
        deviceType: [{ required: true, message: "设备类型不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "负责人不能为空", trigger: "blur" }],
        // fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    const q = (this.$route && this.$route.query) || {};
    if (q.pondSystemId != null && q.pondSystemId !== '') {
      const sid = Number(q.pondSystemId);
      this.queryParams.pondSystemId = Number.isFinite(sid) ? sid : q.pondSystemId;
    }
    if (q.pondNum) {
      this.queryParams.pondNum = q.pondNum;
    }
    if (q.workshopId != null && q.workshopId !== '') {
      const wid = Number(q.workshopId);
      this.queryParams.workshopId = Number.isFinite(wid) ? wid : q.workshopId;
    }
    this.getList();
    this.getUserList();
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
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.pondList.forEach((item, index) => {
        if(!shopTemp.includes(item.workshopId)) {
          this.workshopList.push(item);
          shopTemp.push(item.workshopId);
        }
        const systemKey = item.pondSystemId != null ? ('s:' + item.pondSystemId) : ('n:' + item.pondNum);
        if(!systemTemp.includes(systemKey)) {
          this.systemList.push(item);
          systemTemp.push(systemKey);
        }
      })
      // console.log(`workshopList====================>${JSON.stringify(this.workshopList)}`);
      // console.log(`systemList====================>${JSON.stringify(this.systemList)}`);
    },
    toDetail(currItem) {
      console.log(`currItem=============>${JSON.stringify(currItem)}`);
      if(currItem.deviceType == 1) {
        // 视频设备
        this.$router.push({
            path: '/device/zk-device-video',
            // path: '/fish/zk-paper-question',
            params: {},
            query: { deviceId: currItem.id },
        });
      } else if(currItem.deviceType == 0) {
        // 数值设备
        this.$router.push({
            path: '/yangzhi/zk-device-detail1',
            // path: '/fish/zk-paper-question',
            params: {},
            query: { deviceId: currItem.id },
        });
      }
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkDevicePage(this.queryParams).then(response => {
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
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.pondSystemId = item.pondSystemId;
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
        // this.pondList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        this.getWorkshopList();
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
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        deviceName: undefined,
        workshopName: "默认车间",
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        pondNum: undefined,
        userName: undefined,
        status: "1",
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
      this.title = "添加设备";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkDevice(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备";
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
        if(!this.form.tenantId) this.form.tenantId = this.userInfo.tenantId;
        if(!this.form.userId) this.form.userId = this.userInfo.userId;
        if(!this.form.deptId) this.form.deptId = this.userInfo.deptId;
        if (this.form.id != null) {
          updateZkDevice(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkDevice(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除设备编号为"' + id + '"的数据项?').then(function() {
          return deleteZkDevice(id);
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
      this.$modal.confirm('是否确认导出所有设备数据项?').then(() => {
          this.exportLoading = true;
          return exportZkDeviceExcel(params);
        }).then(response => {
          this.$download.excel(response, '设备.xls');
          this.exportLoading = false;
        }).catch(() => {});
    },
    
    /** 卡片展示按钮操作 */
    showDeviceCard() {
      // 传递当前的查询参数到卡片展示页面
      this.$router.push({
        path: '/iot/deviceCardShow',
        query: {
          queryParams: JSON.stringify(this.queryParams)
        }
      });
    },

    /** 系统展示按钮操作 */
    showSystemDisplay() {
      this.$router.push('/iot/systemShow');
    }
  }
};
</script>