<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="车间" prop="workshopId">
        <el-select style="width:100%;" v-model="queryParams.workshopId" placeholder="请选择车间" filterable clearable @change="querySearch($event, -2.2)">
          <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="`${item.workshopName}`"
            :value="item.workshopId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="系统" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable @change="querySearch($event, -2.3)">
          <el-option v-for="(item, index) in systemList" :key="item.id+'__'+index" :label="`${item.pondNum}`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable @change="querySearch($event, -2.4)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}`"
            :value="item.pondDetailNum"></el-option>
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
                   v-hasPermi="['pl:zk-biology-capital:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-biology-capital:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="系统" align="center" prop="pondSystemName" />
      <!-- <el-table-column label="系统id" align="center" prop="pondSystemId" /> -->
      <el-table-column label="池号" align="center" prop="pondNum" />
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" />
      <el-table-column label="存塘id" align="center" prop="pondId" />
      <el-table-column label="存塘编号" align="center" prop="pondDetailNum" /> -->
      <el-table-column label="品名" align="center" prop="productName" />
      <!-- <el-table-column label="品牌" align="center" prop="brand" /> -->
      <el-table-column label="规格" align="center" prop="specifications" />
      <!-- <el-table-column label="库存类别" align="center" prop="productClassification" />
      <el-table-column label="类别编号" align="center" prop="categoryNo" /> -->
      <el-table-column label="数量" align="center" prop="productNum" />
      <el-table-column label="总重量" align="center" prop="productWeight" />
      <el-table-column label="市场单价" align="center" prop="productPrice" />
      <el-table-column label="资产总计" align="center" prop="capitalTotal" />
      <el-table-column label="当前成本" align="center" prop="currCost" />
      <!-- <el-table-column label="饲料成本" align="center" prop="feedCost" />
      <el-table-column label="药品成本" align="center" prop="medicineCost" />
      <el-table-column label="设备成本" align="center" prop="deviceCost" />
      <el-table-column label="能源成本" align="center" prop="energyCost" />
      <el-table-column label="额外成本" align="center" prop="extraCost" />
      <el-table-column label="固定资产成本" align="center" prop="fixedCost" />
      <el-table-column label="生物资产成本" align="center" prop="biologyCost" />
      <el-table-column label="存塘个数" align="center" prop="pondCount" /> -->
      <el-table-column label="总体盈亏" align="center" prop="overallProfitAndLoss" />
      <el-table-column label="盈亏比例" align="center" prop="profitAndLossRatio" />
      <!-- <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="记录人" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-biology-capital:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-biology-capital:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="系统编号" prop="workshopId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择系统编号" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="品名" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入品名" />
        </el-form-item>
        <el-form-item label="规格" prop="specifications">
          <el-input v-model="form.specifications" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="数量" prop="productNum">
          <el-input v-model="form.productNum" placeholder="请输入数量" />
        </el-form-item>
        <el-form-item label="总重量" prop="productWeight">
          <el-input v-model="form.productWeight" placeholder="请输入总重量" />
        </el-form-item>
        <el-form-item label="市场单价" prop="productPrice">
          <el-input v-model="form.productPrice" placeholder="请输入市场单价" />
        </el-form-item>
        <el-form-item label="资产总计" prop="capitalTotal">
          <el-input v-model="form.capitalTotal" placeholder="请输入资产总计" />
        </el-form-item>
        <el-form-item label="当前成本" prop="currCost">
          <el-input v-model="form.currCost" placeholder="请输入当前成本" />
        </el-form-item>
        <el-form-item label="总体盈亏" prop="overallProfitAndLoss">
          <el-input v-model="form.overallProfitAndLoss" placeholder="请输入总体盈亏" />
        </el-form-item>
        <el-form-item label="盈亏比例" prop="profitAndLossRatio">
          <el-input v-model="form.profitAndLossRatio" placeholder="请输入盈亏比例" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkBiologyCapital, updateZkBiologyCapital, deleteZkBiologyCapital, getZkBiologyCapital, getZkBiologyCapitalPage, exportZkBiologyCapitalExcel } from "@/api/fish/zkBiologyCapital";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";

export default {
  name: "ZkBiologyCapital",
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
      // 生物性资产列表
      list: [],
      pondList: [],
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
        productName: null,
        brand: null,
        specifications: null,
        productClassification: null,
        categoryNo: null,
        productNum: null,
        productWeight: null,
        productPrice: null,
        capitalTotal: null,
        currCost: null,
        feedCost: null,
        medicineCost: null,
        deviceCost: null,
        energyCost: null,
        extraCost: null,
        fixedCost: null,
        biologyCost: null,
        pondCount: null,
        overallProfitAndLoss: null,
        profitAndLossRatio: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productName: [{ required: true, message: "品名不能为空", trigger: "blur" }],
        brand: [{ required: true, message: "品牌不能为空", trigger: "blur" }],
        specifications: [{ required: true, message: "规格不能为空", trigger: "blur" }],
        productClassification: [{ required: true, message: "库存类别不能为空", trigger: "blur" }],
        categoryNo: [{ required: true, message: "类别编号不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
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
      getZkBiologyCapitalPage(this.queryParams).then(response => {
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
        productName: undefined,
        brand: undefined,
        specifications: undefined,
        productClassification: undefined,
        categoryNo: undefined,
        productNum: undefined,
        productWeight: undefined,
        productPrice: undefined,
        capitalTotal: undefined,
        currCost: undefined,
        feedCost: undefined,
        medicineCost: undefined,
        deviceCost: undefined,
        energyCost: undefined,
        extraCost: undefined,
        fixedCost: undefined,
        biologyCost: undefined,
        pondCount: undefined,
        overallProfitAndLoss: undefined,
        profitAndLossRatio: undefined,
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
      this.title = "添加生物性资产";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkBiologyCapital(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改生物性资产";
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
          updateZkBiologyCapital(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkBiologyCapital(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除生物性资产编号为"' + id + '"的数据项?').then(function() {
          return deleteZkBiologyCapital(id);
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
      this.$modal.confirm('是否确认导出所有生物性资产数据项?').then(() => {
          this.exportLoading = true;
          return exportZkBiologyCapitalExcel(params);
        }).then(response => {
          this.$download.excel(response, '生物性资产.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
