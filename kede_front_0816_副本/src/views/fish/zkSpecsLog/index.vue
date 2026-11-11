<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="系统" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable @change="querySearch($event, -2.3)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondSystemName}`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable @change="querySearch($event, -2.4)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName})`"
            :value="item.pondDetailNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="起始时间" end-placeholder="截至时间" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-if="false"
                   v-hasPermi="['pl:zk-specs-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-specs-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="车间id" align="center" prop="workshopId" /> -->
      <!-- <el-table-column label="车间名称" align="center" prop="workshopName" /> -->
      <el-table-column label="系统" align="center" prop="pondSystemName" />
      <!-- <el-table-column label="系统id" align="center" prop="pondSystemId" /> -->
      <!-- <el-table-column label="系统编号" align="center" prop="pondNum" /> -->
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" /> -->
      <!-- <el-table-column label="存塘id" align="center" prop="pondId" /> -->
      <el-table-column label="池号" align="center" prop="pondDetailNum" />
      <el-table-column label="初始规格(g/尾)" align="center" prop="initSpecs" />
      <!-- <el-table-column label="初始数量(尾)" align="center" prop="initNum" /> -->
      <el-table-column label="初始数量(尾)" align="center" prop="initNum">
        <template v-slot="scope">
          <span>{{ scope.row.initNum>0?scope.row.initNum:0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="投喂总量(Kg)" align="center" prop="feedCount" />
      <el-table-column label="当前存塘数量(尾)" align="center" prop="currNum" />
      <el-table-column label="预估饵料系数" align="center" prop="predictRadio" />
      <el-table-column label="预估规格(g/尾)" align="center" prop="predictSpecs" />
      <el-table-column label="打样规格(g/尾)" align="center" prop="acturalSpecs" />
      <el-table-column label="实际饵料系数" align="center" prop="acturalRadio" />
      <el-table-column label="打样时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="记录人" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-specs-log:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-specs-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="池号" prop="workshopId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择池号" filterable clearable
            @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
              :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="初始规格" prop="initSpecs">
          <el-input v-model="form.initSpecs" placeholder="请输入初始规格(g/尾)" />
        </el-form-item>
        <el-form-item label="初始数量" prop="initNum">
          <el-input v-model="form.initNum" placeholder="请输入初始数量" />
        </el-form-item>
        <el-form-item label="投喂总量" prop="feedCount">
          <el-input v-model="form.feedCount" placeholder="请输入投喂总量(Kg)" />
        </el-form-item>
        <el-form-item label="当前存塘数量" prop="currNum">
          <el-input v-model="form.currNum" placeholder="请输入当前存塘数量(尾)" />
        </el-form-item>
        <el-form-item label="预估饵料系数" prop="predictRadio">
          <el-input v-model="form.predictRadio" placeholder="请输入预估饵料系数" />
        </el-form-item>
        <el-form-item label="预估规格" prop="predictSpecs">
          <el-input v-model="form.predictSpecs" placeholder="请输入预估规格" />
        </el-form-item>
        <el-form-item label="实际打样规格" prop="acturalSpecs">
          <el-input v-model="form.acturalSpecs" placeholder="请输入实际打样规格" />
        </el-form-item>
        <el-form-item label="实际饵料系数" prop="acturalRadio">
          <el-input v-model="form.acturalRadio" placeholder="请输入实际饵料系数" />
        </el-form-item>
        <!-- <el-form-item label="记录人" prop="userId">
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
import { createZkSpecsLog, updateZkSpecsLog, deleteZkSpecsLog, getZkSpecsLog, getZkSpecsLogPage, exportZkSpecsLogExcel } from "@/api/fish/zkSpecsLog";
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
import { createSaleOut, updateSaleOut, deleteSaleOut, getSaleOut, getSaleOutPage, exportSaleOutExcel } from "@/api/erp/saleOut";

export default {
  name: "ZkSpecsLog",
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
      // 规格记录列表
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
        workshopId: null,
        workshopName: null,
        pondSystemName: null,
        pondSystemId: null,
        pondNum: null,
        pondName: null,
        pondId: null,
        pondDetailNum: null,
        initSpecs: null,
        initNum: null,
        feedCount: null,
        currNum: null,
        predictRadio: null,
        predictSpecs: null,
        acturalSpecs: null,
        acturalRadio: null,
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
    this.getPondList();
    this.getUserList();
    this.getBatchNumerList();
    this.getProductList();
  },
  watch: {
    'form.predictRadio': {
      handler(newV, oldV) {// 预估规格计算
        console.log("===>newV:{},oldV:{}", newV, oldV); // 
        if (newV && this.form.feedCount) {
          // 预估规格=（投喂总量÷预估饵料系数+初始规格×初始数量）÷当前存塘数量
          let temp1 = this.my_common.computer(this.form.feedCount, newV, 3, 2); 
          // console.log("===>newV:{},temp1:{}", newV, temp1); // 
          let temp2 = this.my_common.computer(this.form.currNum, this.form.initSpecs, 2, 2); 
          // console.log("===>newV:{},temp2:{}", newV, temp2); // 
          let temp3 = this.my_common.computer(temp1, temp2, 0, 2); 
          // console.log("===>newV:{},temp3:{}", newV, temp3); // 
          this.form.predictSpecs = this.my_common.computer(temp3, this.form.currNum, 3, 2); 
        }
        // console.log(`===>newV:${newV},oldV:${oldV},当前鱼数量：${this.form.currFishNum},pondId:${this.form.pondId}`); // 
      }
    },
    'form.acturalSpecs': { // 实际饵料系数计算
      handler(newV, oldV) {
        console.log("===>newV:{},oldV:{}", newV, oldV); // 
        if (newV && this.form.feedCount) {
          // 实际饵料系数=投喂总量÷（打样规格×当前存塘数量-初始重量）
          let temp1 = this.my_common.computer(this.form.currNum, newV, 2, 2); 
          let temp2 = this.my_common.computer(this.form.currNum, this.form.initSpecs, 2, 2); 
          let temp3 = this.my_common.computer(temp1, temp2, 1, 2); 
          let temp4 = this.my_common.computer(this.form.feedCount, temp3, 3, 2); 
          this.form.acturalRadio = temp4; 
        }
        // console.log(`===>newV:${newV},oldV:${oldV},当前鱼数量：${this.form.currFishNum},pondId:${this.form.pondId}`); // 
      }
    },
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
        }
        if(!systemTemp.includes(item.pondNum)) {
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
      getSaleOutPage(queryParams).then(response => {
        let list = response.data;
        if(this.my_common.listIsEmpty(list)) return;
        list.forEach(item => {
          // 供应商地址
          item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName}-剩余${item.remainingInventory}条)`;
        })
        this.bachchNumberList = list;
        // console.log(`==============>${JSON.stringify(this.bachchNumberList)}`);
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkSpecsLogPage(this.queryParams).then(response => {
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
        workshopId: undefined,
        workshopName: undefined,
        pondSystemName: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondName: undefined,
        pondId: undefined,
        pondDetailNum: undefined,
        initSpecs: undefined,
        initNum: undefined,
        feedCount: undefined,
        currNum: undefined,
        predictRadio: undefined,
        predictSpecs: undefined,
        acturalSpecs: undefined,
        acturalRadio: undefined,
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
      this.title = "添加规格记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkSpecsLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改规格记录";
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
          updateZkSpecsLog(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkSpecsLog(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          notifyProductionRecordChanged({ type: 'specs', pondId: this.form.pondId, source: 'zkSpecsLog' });
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除规格记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkSpecsLog(id);
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
      this.$modal.confirm('是否确认导出所有规格记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkSpecsLogExcel(params);
        }).then(response => {
          this.$download.excel(response, '规格记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
