<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="空闲状态" prop="fishNumType">
        <el-select style="width:100%;" v-model="queryParams.fishNumType" placeholder="请选择空闲状态" filterable clearable @change="handleQuery">
          <el-option v-for="(item, index) in fishNumTypeList" :key="item.label+'_fishNumType_'+index" :label="`${item.label}`"
            :value="item.value"></el-option>
        </el-select>
      </el-form-item>
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
        <el-form-item label="存塘类型" prop="pondType">
          <el-select style="width:100%;" v-model="queryParams.pondType" placeholder="请选择存塘类型" filterable clearable  @change="handleQuery">
            <el-option v-for="(item, index) in pondTypeList" :key="item.value+'_pt_'+index" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      <!-- <el-form-item label="系统编号" prop="pondNum">
        <el-input v-model="queryParams.pondNum" placeholder="请输入系统编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="所在车间" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入所在车间" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="所在车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所在车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="经度" prop="longitude">
        <el-input v-model="queryParams.longitude" placeholder="请输入经度" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="纬度" prop="dimension">
        <el-input v-model="queryParams.dimension" placeholder="请输入纬度" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_WORKSHOP_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="创建时间" prop="createTime">
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
                   v-hasPermi="['pl:zk-pond:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-pond:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column v-if="false" label="编号" align="center" prop="id"></el-table-column> -->
      <el-table-column label="基地" align="center" prop="deptName" />
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="系统" align="center" prop="pondNum" />
      <el-table-column label="池号" align="center" prop="pondDetailNum" />
      <el-table-column label="类型" align="center" prop="pondType" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ judgePondType(scope.row) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="所在车间" align="center" prop="workshopName" /> -->
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" />
      <el-table-column label="存塘规模" align="center" prop="pondScale" /> -->
      <el-table-column label="投放记录" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <div @click="showFishLog(scope.row, 1)"
          style="width: 90px;height: 33px;border: 1px solid green;border-radius: 5px;line-height: 33px;text-align: center;cursor:pointer;margin: 0 auto;color: green;">
            查看
          </div>
        </template>
      </el-table-column>
      <el-table-column label="销售记录" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <div @click="showFishLog(scope.row, -1)"
          style="width: 90px;height: 33px;border: 1px solid green;border-radius: 5px;line-height: 33px;text-align: center;cursor:pointer;margin: 0 auto;color: green;">
            查看
          </div>
        </template>
      </el-table-column>
      <el-table-column label="死亡记录" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <div @click="showFishLog(scope.row, -2)"
          style="width: 90px;height: 33px;border: 1px solid green;border-radius: 5px;line-height: 33px;text-align: center;cursor:pointer;margin: 0 auto;color: green;">
            查看
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分池记录" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <div @click="showFishLog(scope.row, -3)"
          style="width: 90px;height: 33px;border: 1px solid green;border-radius: 5px;line-height: 33px;text-align: center;cursor:pointer;margin: 0 auto;color: green;">
            查看
          </div>
        </template>
      </el-table-column>
      <el-table-column label="存塘数量" align="center" prop="fishNum" />
      <el-table-column label="规格(g/尾)" align="center" prop="fishSpec" />
      <el-table-column label="总重量(Kg)" align="center" prop="totalWeight" />
      <!-- <el-table-column label="所在车间id" align="center" prop="workshopId" /> -->
      <!-- <el-table-column label="经度" align="center" prop="longitude" />
      <el-table-column label="纬度" align="center" prop="dimension" /> -->
      <!-- <el-table-column label="负责人" align="center" prop="userName" show-overflow-tooltip /> -->
      <!-- <el-table-column label="状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)" align="center" prop="status" /> -->
      <!-- <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_WORKSHOP_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-pond:update']">修正</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-pond:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修正) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <!-- <el-form-item label="所在车间" prop="workshopId">
          <el-select style="width:100%;" v-model="form.workshopId" placeholder="请选择所在车间" filterable clearable  @change="updateWorkshopId">
            <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="item.workshopName"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="系统编号" prop="pondNum">
          <el-select style="width:100%;" v-model="form.pondNum" placeholder="请选择系统编号" filterable clearable  @change="updatePondSystemId">
            <el-option v-for="(item, index) in pondSystemList" :key="item.id+'__'+index" :label="`${item.workshopName}-${item.systemName}`"
              :value="item.id"></el-option>
          </el-select>
          <div class="toPondParent">
            <div style="" class="toPondSystem" @click.stop="toPondSystem">添加系统</div>
          </div>
        </el-form-item>
        <!-- <el-form-item label="系统编号" prop="pondNum">
          <el-input v-model="form.pondNum" placeholder="请输入系统编号" />
        </el-form-item> -->
        <el-form-item label="存塘编号" prop="pondDetailNum">
          <el-input v-model="form.pondDetailNum" placeholder="请输入存塘编号" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="鱼池规格(m³)" prop="pondScale">
          <el-input v-model="form.pondScale" oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''"
           placeholder="请输入鱼池规格(m³)" />
        </el-form-item>
        <el-form-item label="存塘鱼数(尾)" prop="fishNum" v-if="editType == 2">
          <el-input v-model="form.fishNum" placeholder="请输入存塘鱼数(尾)" />
        </el-form-item>
        <el-form-item label="规格(g/尾)" prop="fishSpec" v-if="editType == 2">
          <el-input v-model="form.fishSpec"  placeholder="请输入规格(g/尾)" />
        </el-form-item>
        <el-form-item label="总重量(Kg)" prop="totalWeight" v-if="editType == 2">
          <el-input v-model="form.totalWeight" placeholder="请输入总重量(Kg)" />
        </el-form-item>
        <!-- <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度" />
        </el-form-item>
        <el-form-item label="纬度" prop="dimension">
          <el-input v-model="form.dimension" placeholder="请输入纬度" />
        </el-form-item> -->
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
        <!-- <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item> -->
        <el-form-item label="原数据" prop="originData" v-if="editType == 2">
          <el-input v-model="form.originData" placeholder="请输入原数据" />
        </el-form-item>
        <el-form-item label="差值" prop="differenceData" v-if="editType == 2">
          <el-input v-model="form.differenceData" placeholder="请输入差值" />
        </el-form-item>
        <el-form-item label="变化比例" prop="changeRatio" v-if="editType == 2">
          <el-input v-model="form.changeRatio" placeholder="请输入变化比例" />
        </el-form-item>
        <el-form-item label="原因" prop="reason" v-if="editType == 2">
          <el-input v-model="form.reason" placeholder="请输入原因" />
        </el-form-item>
        <!-- <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_WORKSHOP_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="所在车间" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入所在车间" />
        </el-form-item>
        <el-form-item label="所在车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所在车间id" />
        </el-form-item>
        <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)" prop="status">
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
    <!-- 查看鱼苗投放历史 -->
    <el-dialog
      :title="operationTitle"
      :visible.sync="showFishLogShow"
      width="50%">
      <el-row>
        <el-col :xs="24" :sm="24" :lg="24">
          {{ fishLogInfo.summary }}
        </el-col>
      </el-row>
      <div style="height: 13px;width: 100%"></div>
      <el-row>
        <el-col :xs="24" :sm="24" :lg="24">
          <template v-for="(item, index) in fishLogInfo.fishTypeNums">
            <div>{{ item }}</div>
          </template>
        </el-col>
      </el-row>
      <div style="height: 13px;width: 100%"></div>
      <el-row>
        <el-col :xs="24" :sm="24" :lg="24">
          <template v-for="(item, index) in fishLogInfo.fishLogs">
            <div>{{ item }}</div>
          </template>
        </el-col>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getFishLogById, getFishLogByQuery, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
// import FileBusUpload from "@/components/FileUpload";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkWorkshop, updateZkWorkshop, deleteZkWorkshop, getZkWorkshop, getZkWorkshopPage, exportZkWorkshopExcel } from "@/api/fish/zkWorkshop";
import { createZkPondSystem, updateZkPondSystem, deleteZkPondSystem, getZkPondSystem, getZkPondSystemPage, exportZkPondSystemExcel } from "@/api/fish/zkPondSystem";
import { add } from "lodash";
import { UserInfoKey } from "@/utils/constants";
import {listSimpleDepts} from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { notifySharedSiteOptionsChanged, resolveCurrentUserDeptId } from "@/utils/siteOptions";

export default {
  name: "ZkPond",
  components: {
    FileBusUpload, 
    Treeselect, 
  },
  data() {
    return {
      userInfo: {},
      pondTypeList: [
        {label:'单一', value: '1'},
        {label:'混合', value: '2'},
        {label:'全部', value: '3'}
      ],
      fishNumTypeList: [
        {label: '养殖中', value: '1'},
        {label: '空闲中', value: '2'},
        {label: '全部', value: '3'}
      ],
      // 遮罩层
      loading: true,
      showFishLogShow: false,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      operationTitle: '',
      // 存塘列表
      list: [],
      pondList: [],
      workshopList: [], 
      pondSystemList: [], 
      systemList: [], 
      userList1: [],
      fishLogInfo: [],
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
        pondName: null,
        fishNumType: "1", // 1:养殖中;2:空闲中;3:全部;
        workshopName: null,
        workshopId: null,
        longitude: null,
        dimension: null,
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
        workshopId: [{ required: true, message: "所在车间id不能为空", trigger: "blur" }],
        pondNum: [{ required: true, message: "系统编号不能为空", trigger: "blur" }],
        pondDetailNum: [{ required: true, message: "存塘编号不能为空", trigger: "blur" }],
        pondName: [{ required: true, message: "存塘名称不能为空", trigger: "blur" }],
        pondScale: [{ required: true, message: "存塘规模不能为空", trigger: "blur" }],
        fishNum: [{ required: true, message: "存塘鱼数不能为空", trigger: "blur" }],
        // fishSpec: [{ required: true, message: "规格不能为空", trigger: "blur" }],
        // reason: [{ required: true, message: "修正原因不能为空", trigger: "blur" }],
      },
      editType: 1, // 1:添加;2:修正;
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`============>userInfo:${JSON.stringify(this.userInfo)}`);
    const deptId = resolveCurrentUserDeptId();
    if (deptId != null) this.queryParams.deptId = deptId;
    this.getList();
    this.getUserList();
    this.getPondList();
    this.getPondSystemList();
    this.getTreeselect();
  },
  watch: {
    'form.fishSpec': {
      handler(newV, oldV) {
        console.log("===>newV:{},oldV:{}",newV, oldV);  // salesIntensity
        if (newV) {
          // this.form.procurementCost = newV*this.form.catalogPrice;
          // let totalWeight = this.my_common.computer(newV, this.form.fishNum, 2, 4);
          // this.form.totalWeight = this.my_common.computer(totalWeight, 1000, 3, 4);
        }
      }
    },
    'form.fishNum': {
      handler(newV, oldV) {
        console.log("fishNum ===>newV:{},oldV:{}",newV, oldV);  // salesIntensity
        if (newV && this.editType == 2) {
          this.form.differenceData = newV - this.form.originData; // 计算差值
          this.form.changeRatio = this.my_common.computer(this.form.differenceData, this.form.originData, 3, 2);
        }
      }
    }
  },
  methods: {
    judgePondType(zkPondFish) {
      if(!zkPondFish) return '单一';
      if(zkPondFish.batchNumber && zkPondFish.batchNumber.split(",").length >1) return "混合";
      return '单一';
    },
    /** 查询部门下拉树结构  */
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
    toPondSystem() {
      this.open = false;
      this.$router.push({
          path: '/jidi/zk-pond-system',
          params: {},
          query: {  },
      });
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
    showFishLog(currItem, addType) {
      if(addType == 1) {
        this.operationTitle = `投放历史`;
      } else if(addType == -1) {
        this.operationTitle = `销售历史`;
      } else if(addType == -2) {
        this.operationTitle = `死亡历史`;
      } else if(addType == -3) {
        this.operationTitle = `分池历史`;
      }
      // console.log(`addType:${addType} 当前存塘信息：${JSON.stringify(currItem)}`);
      getFishLogByQuery({id: currItem.id, addType}).then(res => {
        // console.log(`==========>getFishLogById:${JSON.stringify(res)}`);
        this.fishLogInfo = res.data;
        this.showFishLogShow = true;
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
      this.form.fishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      this.form.deptId = item.deptId;
      this.form.tenantId = item.tenantId;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      // console.log(`==============>getPondList 1 `);
      getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log(`getZkPondPage==================>${JSON.stringify(this.pondList)}`);
        this.getWorkshopList();
      })
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPondPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    getListAddDept(deptId) {
      this.queryParams = {...this.queryParams, deptId};
      this.getList();
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.showFishLogShow = false;
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
      this.form.userId = item.id;
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
    updatePondSystemId(currId){
      let item = null;
      this.pondSystemList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.pondDetailNum = `${item.systemNum}-`;
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondNum = item.systemNum;
      this.form.pondSystemId = item.id;
      this.form.pondSystemName = item.systemName;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondSystemList() {
      this.pondSystemList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkPondSystemPage(params).then(response => {
        this.pondSystemList.push(...response.data.list)
        // this.pondSystemList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
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
    getWorkshopList2() {
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
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        pondName: undefined,
        workshopName: "默认车间",
        workshopId: 1,
        pondDetailNum: undefined,
        longitude: undefined,
        dimension: undefined,
        userName: undefined,
        status: "1",
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        pondScale: 20,
        fishNum: 0,
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
      this.editType = 1;
      this.reset();
      this.updateUserId(parseInt(this.userInfo.userId));
      this.open = true;
      this.title = "添加存塘";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修正按钮操作 */
    handleUpdate(row) {
      this.editType = 2;
      this.reset();
      const id = row.id;
      getZkPond(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修正存塘";
        this.form.originData = this.form.fishNum; // 原数据
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
        if(this.editType == 2 && !this.form.reason && this.form.differenceData) {
          this.$modal.msgWarning(`修正原因不能为空`);
          return;
        }
        // 修正的提交
        if (this.form.id != null) {
          updateZkPond(this.form).then(response => {
            this.$modal.msgSuccess("修正成功");
            this.open = false;
            this.getList();
            notifySharedSiteOptionsChanged();
          });
          return;
        }
        // 添加的提交
        createZkPond(this.form).then(response => {
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
      if(row.fishNum > 0) {
        this.$modal.msgWarning(`当前存塘鱼苗数${row.fishNum}不为空，不能删除！`);
        return;
      }
      this.$modal.confirm('是否确认删除存塘编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPond(id);
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
      this.$modal.confirm('是否确认导出所有存塘数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPondExcel(params);
        }).then(response => {
          this.$download.excel(response, '存塘.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
<style lang="scss" scoped>
.toPondParent {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  .toPondSystem {
    width: 100px;
    // display: flex;
    // justify-content: flex-end;
    // padding-right: 20px;
    // padding-bottom: 0px;
    padding: 7px 0;
    cursor: pointer;
    // border: 1px solid green;
    // height: 15px;
  }
}
</style>