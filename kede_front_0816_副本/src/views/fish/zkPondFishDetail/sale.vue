<template>
  <!-- 3. 销售记录 -->
  <div class="app-container">
    <!-- 3. 销售记录 -->
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="存塘鱼苗表id" prop="zkPondFishId">
        <el-input v-model="queryParams.zkPondFishId" placeholder="请输入存塘鱼苗表id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="批次编号" prop="batchNumber">
        <el-select style="width:100%;" v-model="queryParams.batchNumber" placeholder="请选择批次编号" filterable clearable  @change="handleQuery">
          <el-option v-for="(item, index) in bachchNumberList" :key="item.id+'__'+index" :label="`${item.info}`"
            :value="item.relationOrderNo"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="起始时间" end-placeholder="截至时间" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="车间" prop="workshopId">
        <el-select style="width:100%;" v-model="queryParams.workshopId" placeholder="请选择车间" filterable clearable @change="querySearch($event, -2.2)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.workshopName}`"
            :value="item.workshopId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="系统" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable @change="querySearch($event, -2.3)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable @change="querySearch($event, -2.4)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName})`"
            :value="item.pondDetailNum"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="负责人" prop="userId">
        <el-select style="width:100%;" v-model="queryParams.userId" placeholder="请选择负责人" filterable clearable @change="handleQuery">
          <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="系统编号" prop="pondId">
        <el-select style="width:100%;" v-model="queryParams.pondId" placeholder="请选择存塘系统编号" filterable clearable
          @change="handleQuery">
          <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
            :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
            :value="item.id"></el-option>
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-select v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" filterable clearable size="small"
          style="width: 100%;" @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="投放类型" prop="addType">
        <el-select v-model="queryParams.addType" placeholder="请输入投放类型" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="鱼苗数量" prop="fishNum">
        <el-input v-model="queryParams.fishNum" placeholder="请输入鱼苗数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="投放类型(-2:损失;-1:卖出;1:投放;)" prop="addType">
        <el-select v-model="queryParams.addType" placeholder="请选择投放类型(-2:损失;-1:卖出;1:投放;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
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
      <el-col :span="1.5" v-if="false">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['pl:zk-pond-fish-detail:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          :loading="exportLoading" v-hasPermi="['pl:zk-pond-fish-detail:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="批次" align="center" prop="batchNumber" width="180" />
      <el-table-column label="品种" align="center" prop="fishTypes" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_POND_FISH_TYPES" :value="scope.row.fishTypes" />
        </template>
      </el-table-column>
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="系统" align="center" prop="pondSystemName" width="150" />
      <el-table-column label="池号" align="center" prop="pondDetailNum" />
      <el-table-column label="销售数量(尾)" align="center" prop="fishNum" width="150" />
      <el-table-column label="规格(g/尾)" align="center" prop="fishSpec" width="150" />
      <el-table-column label="总重量(Kg)" align="center" prop="saleWeight" width="150" />
      <el-table-column label="销售价格(元)" align="center" prop="salePrice" width="150" />
      <el-table-column label="销售收入(元)" align="center" prop="saleIncome" width="150" />
      <el-table-column label="存塘数量(尾)" align="center" prop="historyFishNum" width="150" />
      <el-table-column label="剩余数量(尾)" align="center" prop="currFishNum" width="150" />
      <el-table-column label="误差值(尾)" align="center" prop="errorValue" width="150" />
      <!-- <el-table-column label="执行人" align="center" prop="saleIncome" /> -->
      <el-table-column label="负责人" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <userName :user-id="scope.row.userId"></userName>
        </template>
      </el-table-column>
      <el-table-column label="销售方向(城市)" align="center" prop="saleDirection" width="150" />
      <el-table-column label="购买人" align="center" prop="purchaser" />
      <el-table-column label="联系方式" align="center" prop="purchaserPhone" />
      <!-- <el-table-column label="存塘鱼苗表id" align="center" prop="zkPondFishId" />
      <el-table-column label="所属车间id" align="center" prop="workshopId" /> -->
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" /> -->
      <!-- <el-table-column label="所属存塘id" align="center" prop="pondId" /> -->
      <!-- <el-table-column label="投放类型(-2:损失;-1:卖出;1:投放;)" align="center" prop="addType" /> -->
      <!-- <el-table-column label="操作类型" align="center" prop="addType" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE" :value="scope.row.addType" />
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="false"
            v-hasPermi="['pl:zk-pond-fish-detail:update']">修正</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['pl:zk-pond-fish-detail:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 对话框(添加 / 修正) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <!-- <el-form-item label="存塘鱼苗表id" prop="zkPondFishId">
          <el-input v-model="form.zkPondFishId" placeholder="请输入存塘鱼苗表id" />
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
        <el-form-item label="批次编号" prop="batchNumber">
          <el-select style="width:100%;" v-model="form.batchNumber" placeholder="请选择批次编号" filterable clearable  @change="updateBatchNumerId">
            <el-option v-for="(item, index) in bachchNumberList" :key="item.id+'__'+index" :label="`${item.info}`"
              :value="item.relationOrderNo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="池号" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择池号" filterable clearable
            @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
              :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
          <el-select v-model="form.fishTypes" placeholder="请输入鱼苗种类" filterable clearable size="small"
            style="width: 100%;" @change="updateFlushTypes">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
          <el-select v-model="form.productNo" placeholder="请输入鱼苗种类" filterable clearable size="small" style="width: 100%;" @change="updateProductId">
            <el-option v-for="dict in productList" :key="dict.productNo"
              :label="dict.info" :value="dict.productNo" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="鱼苗种类" prop="fishTypes">
          <el-input v-model="form.fishTypes" placeholder="请输入鱼苗种类" />
        </el-form-item> -->
        <el-form-item label="销售数量(尾)" prop="fishNum">
          <el-input v-model="form.fishNum" placeholder="请输入销售数量(尾)" />
        </el-form-item>
        <el-form-item label="销售后存塘鱼数(尾)" prop="currFishNum">
          <el-input v-model="form.currFishNum" placeholder="请输入销售后存塘鱼数(尾)" />
        </el-form-item>
        <el-form-item label="规格(g/尾)" prop="fishSpec">
          <el-input v-model="form.fishSpec" placeholder="请输入规格(g/尾)" />
        </el-form-item>
        <el-form-item label="销售价格(Kg/元)" prop="salePrice">
          <el-input v-model="form.salePrice" placeholder="请输入销售价格(元)"
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <el-form-item label="销售重量(Kg)" prop="saleWeight">
          <el-input v-model="form.saleWeight" placeholder="请输入销售重量(Kg)"
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <!-- <el-form-item label="销售成本" prop="saleCost">
          <el-input v-model="form.saleCost" placeholder="请输入销售成本"
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item> -->
        <el-form-item label="销售收入(元)" prop="saleIncome">
          <el-input v-model="form.saleIncome" placeholder="请输入销售收入(元)"
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <!-- <el-form-item label="投放类型(-2:损失;-1:卖出;1:投放;)" prop="addType">
          <el-select v-model="form.addType" placeholder="请选择投放类型(-2:损失;-1:卖出;1:投放;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="投放类型" prop="addType">
          <el-select v-model="form.addType" placeholder="请输入投放类型" filterable clearable size="small" style="width: 100%;" >
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择负责人" filterable clearable
            @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id + '__' + index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="误差值(尾)" prop="errorValue">
          <el-input v-model="form.errorValue" placeholder="请输入误差值(尾)"
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <el-form-item label="销售方向" prop="saleDirection">
          <el-input v-model="form.saleDirection" placeholder="请输入销售方向"  />
        </el-form-item>
        <el-form-item label="购买人" prop="purchaser">
          <el-input v-model="form.purchaser" placeholder="请输入购买人"  />
        </el-form-item>
        <el-form-item label="联系方式" prop="purchaserPhone">
          <el-input v-model="form.purchaserPhone" placeholder="请输入联系方式"  />
        </el-form-item>

        <!-- <el-form-item label="相关附件" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_ill_control"></FileBusUpload>
        </el-form-item> -->
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
        <el-button type="primary" @click="submitForm" :loading="operationLoading">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkPondFishDetail, updateZkPondFishDetail, deleteZkPondFishDetail, getZkPondFishDetail, getZkPondFishDetailPage, exportZkPondFishDetailExcel } from "@/api/fish/zkPondFishDetail";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadDeptScopedPondList } from "@/utils/siteOptions";
import { notifyProductionRecordChanged } from "@/utils/productionRecordBridge";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import {
  getProductPage
} from "@/api/erp/product";
import { listForBatch } from "@/api/erp/purchaseIn";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkPondFishDetailSale",
  components: {
    FileBusUpload,
  },
  data() {
    return {
      userInfo: {},
      // 遮罩层
      loading: true,
      operationLoading: false,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 存塘鱼苗明细列表
      list: [],
      pondList: [],
      userList1: [],
      dict_flushTypes: [],
      bachchNumberList: [],
      workshopList: [], 
      pondSystemList: [], 
      systemList: [], 
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        zkPondFishId: null,
        workshopId: null,
        pondName: null,
        pondId: null,
        fishTypes: null,
        fishName: null,
        fishNum: null,
        addType: "-1", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        zkPondFishId: [{ required: true, message: "存塘鱼苗表id不能为空", trigger: "blur" }],
        fishNum: [{ required: true, message: "鱼苗数量不能为空", trigger: "blur" }],
        // fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
        batchNumber: [{ required: true, message: "批次编号不能为空", trigger: "blur" }],
        pondId: [{ required: true, message: "池号不能为空", trigger: "blur" }],
        fishNum: [{ required: true, message: "销售数量不能为空", trigger: "blur" }],
        salePrice: [{ required: true, message: "销售价格不能为空", trigger: "blur" }],
        saleWeight: [{ required: true, message: "销售重量不能为空", trigger: "blur" }],
        saleIncome: [{ required: true, message: "销售收入不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    // console.log(`============>userInfo:${JSON.stringify(this.userInfo)}`);
    this.getList();
    this.getPondList();
    this.getUserList();
    this.getBatchNumerList();
    this.getProductList();
    this.dict_flushTypes = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES);
    console.log("created=======>" + JSON.stringify(this.dict_flushTypes));
  },
  watch: {
    'form.fishNum': {
      handler(newV, oldV) {
        console.log("===>newV:{},oldV:{}", newV, oldV); // 
        if (this.form.pondId) {
          let newNum = parseInt(this.form.historyFishNum) - parseInt(newV);
          if (newNum < 0) {
            this.$modal.msgWarning("存塘鱼数不能小于0！");
            this.form.fishNum = 0;
          } else {
            this.form.currFishNum = newNum;
          }
        } else {
          this.$modal.msgWarning("请先选择存塘！");
          this.form.fishNum = 0;
        }
        console.log(`===>newV:${newV},oldV:${oldV},当前鱼数量：${this.form.currFishNum},pondId:${this.form.pondId}`); // 
      }
    },
  },
  methods: {
    judgePondType(zkPondFish) {
      if(!zkPondFish) return '单一';
      if(zkPondFish.batchNumber && zkPondFish.batchNumber.split(",").length >1) return "混合";
      return '单一';
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
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.pondList.forEach((item, index) => {
        if(!shopTemp.includes(item.workshopId)) {
          shopTemp.push(item.workshopId);
          this.workshopList.push(item);
        }
        if(!systemTemp.includes(item.pondNum)) {
          systemTemp.push(item.pondNum);
          this.systemList.push(item);
        }
      })
      // console.log(`workshopList====================>${JSON.stringify(this.workshopList)}`);
      // console.log(`systemList====================>${JSON.stringify(this.systemList)}`);
    },
    updateProductId(currId) {
      let item = null;
      this.productList.forEach(obj => {
        if(obj.productNo == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.fishName = item.productName;
      this.form.fishTypes = item.fishTypes;
      this.form.fishSpec = item.standard;
      this.form.productNo = item.productNo;
      // this.form.userName = item.userName;
      // this.form.userId = item.userId;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getProductList() {
      this.productList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
				categoryId: 7,
      }
      getProductPage(params).then(response => {
        this.productList.push(...response.data.list)
        this.productList.forEach(obj => {
          let tempStr = `${obj.productName}-${obj.productBrand}`;
          obj.info = tempStr;
          // console.log("===========>"+JSON.stringify(obj));
        })
      })
    },
    updateBatchNumerId(currId) {
      let item = null;
      this.bachchNumberList.forEach(obj => {
        if(obj.relationOrderNo == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.supplier = item.supplierName;
      this.form.supplierAddress = item.supplierAddress;
      this.form.supplierId = item.supplierId;
      this.form.productNo = item.productNo;
      // this.form.fishTypes = item.productName;
      this.form.fishSpec = item.productSpec;
      this.updateProductId(this.form.productNo);
      console.log("===========>"+JSON.stringify(this.form));
    },
    getBatchNumerList() {
      let queryParams = {
        pageNo: 1,
        pageSize: 10,
        orderNo: null,
        outKind: '1',
        productName: null,
        userId: null,
        categoryId: "7",
        createTime: [],
        oType: 1, // 订单类型(0:出库单;1:入库单;)
      };
      // 执行查询
      listForBatch(queryParams).then(response => {
        let list = response.data;
        list.forEach(item => {
          // 供应商地址 -剩余${item.remainingInventory}条)
          item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName})`;
        })
        this.bachchNumberList = list;
        // console.log(`==============>${JSON.stringify(this.bachchNumberList)}`);
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPondFishDetailPage(this.queryParams).then(response => {
        this.list = response.data.list;
        if(this.list) {
          this.list.forEach(obj => {
            obj.batchNumber = obj.batchNumber?obj.batchNumber.replaceAll("BZ", "").replaceAll("CGRK", ""):'';
          })
        }
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateFlushTypes(currType) {
      // 1. 找到对应值
      let item = null;
      this.dict_flushTypes.forEach(obj => {
        if (obj.value == currType) {
          item = obj;
          return;
        }
      })
      // 2. 执行更新操作
      this.form.fishName = item.label;
      console.log(`=====>form:${JSON.stringify(this.form)}`)
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if (obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>" + JSON.stringify(this.form));
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
    updatePondId(currId) {
      let item = null;
      this.pondList.forEach(obj => {
        if (obj.id == currId) {
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
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      console.log("===========>" + JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      return loadDeptScopedPondList().then(list => {
        this.pondList = Array.isArray(list) ? list.slice() : []
        this.getWorkshopList();
      }).catch(() => {
        this.pondList = []
      })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        zkPondFishId: undefined,
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        fishTypes: undefined,
        fishNum: undefined,
        addType: "-1", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        batchNumber: undefined,

        pondScale: undefined, //存塘规格
        historyFishNum: undefined, //历史鱼数
        currFishNum: undefined, //现在鱼数
        fishSpec: '2000g/尾', //鱼苗规格
        salePrice: undefined, //销售价格
        saleWeight: undefined, //销售重量
        saleIncome: undefined, //销售收入
        saleCost: undefined, //销售成本
        targetPondNum: undefined, //目标存塘编号
        targetPondSpec: undefined, //目标存塘规格
        targetPondId: undefined, //目标存塘Id
        targetPondName: undefined, //目标存塘名称
        targetHistoryFishNum: undefined, //目标历史鱼数
        targetCurrFishNum: undefined, //目标现在鱼数
        remark: undefined, //备注
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
    async handleAdd() {
      await this.getPondList();
      this.reset();
      this.updateUserId(parseInt(this.userInfo.userId));
      this.open = true;
      this.title = "添加销售记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修正按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPondFishDetail(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修正销售记录";
        if(this.form.batchNumber) {
          this.updateBatchNumerId(this.form.batchNumber);
        }
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
        // 修正的提交
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        // console.log(`=======================>submitForm:${JSON.stringify(this.form)}`);
        // return;
        this.operationLoading = true;
        if (this.form.id != null) {
          updateZkPondFishDetail(this.form).then(response => {
            this.$modal.msgSuccess("修正成功");
            this.open = false;
            this.getList();
            setTimeout(()=>{
              this.operationLoading = false;
            }, 1000);
          });
          return;
        }
        // 添加的提交
        createZkPondFishDetail(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          notifyProductionRecordChanged({ type: 'sale', pondId: this.form.pondId, source: 'sale' });
          setTimeout(()=>{
            this.operationLoading = false;
          }, 1000);
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除存塘鱼苗明细编号为"' + id + '"的数据项?').then(function () {
        return deleteZkPondFishDetail(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = { ...this.queryParams };
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有存塘鱼苗明细数据项?').then(() => {
        this.exportLoading = true;
        return exportZkPondFishDetailExcel(params);
      }).then(response => {
        this.$download.excel(response, '存塘鱼苗明细.xls');
        this.exportLoading = false;
      }).catch(() => { });
    }
  }
};
</script>
