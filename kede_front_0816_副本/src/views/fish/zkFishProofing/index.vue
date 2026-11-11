<template>
  <div class="app-container zk-fish-proofing-page">
    <PageLoadingMask
      :visible="pageLoading && !list.length"
      text="正在加载打样记录…"
    />

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="批次编号" prop="batchNumber">
        <el-input v-model="queryParams.batchNumber" placeholder="请输入批次编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="车间" prop="workshopId">
        <el-select style="width:100%;" v-model="queryParams.workshopId" placeholder="请选择车间" filterable clearable
                   :loading="pondLoading" @change="querySearch($event, -2.2)">
          <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="`${item.workshopName}`"
            :value="item.workshopId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="系统" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable
                   :loading="pondLoading" @change="querySearch($event, -2.3)">
          <el-option v-for="(item, index) in systemList" :key="item.id+'__'+index" :label="`${item.pondSystemName || item.pondNum}`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable
                   :loading="pondLoading" @change="querySearch($event, -2.4)">
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondName || item.pondDetailNum}`"
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
                   v-hasPermi="['pl:zk-fish-proofing:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-fish-proofing:export']">导出</el-button>
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
      <el-table-column label="批次" align="center" prop="batchNumber" />
      <!-- <el-table-column label="车间id" align="center" prop="workshopId" /> -->
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="系统" align="center" prop="pondSystemName" />
      <!-- <el-table-column label="系统id" align="center" prop="pondSystemId" /> -->
      <!-- <el-table-column label="系统编号" align="center" prop="pondNum" /> -->
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" /> -->
      <!-- <el-table-column label="存塘id" align="center" prop="pondId" /> -->
      <el-table-column label="池号" align="center" prop="pondDetailNum" />
      <!-- <el-table-column label="初始规格" align="center" prop="initSpecs" /> -->
      <el-table-column label="平均打样体长(cm)" align="center" prop="avgBodyLength" />
      <el-table-column label="平均打样规格(g)" align="center" prop="avgSpecs" />
      <el-table-column label="打样数量(尾)" align="center" prop="currNum" />
      <!-- <el-table-column label="任务明细id" align="center" prop="taskDetailId" /> -->
      <!-- <el-table-column label="记录人" align="center" prop="userId" /> -->
      <!-- <el-table-column label="所属部门" align="center" prop="deptId" /> -->
      <!-- <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="seeLog(scope.row)"
                     v-hasPermi="['pl:zk-fish-proofing:update']">打样记录</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-fish-proofing:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-fish-proofing:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="批次编号" prop="batchNumber">
          <el-select style="width:100%;" v-model="form.batchNumber" placeholder="请选择批次编号" filterable clearable
                     :loading="batchLoading" @change="updateBatchNumerId">
            <el-option v-for="(item, index) in bachchNumberList" :key="item.id+'__'+index" :label="`${item.info}`"
              :value="item.relationOrderNo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="存塘编号" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择存塘编号" filterable clearable
            :loading="pondLoading" :no-data-text="pondLoading ? '数据正在加载中…' : '无数据'"
            @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id + '__' + index"
              :label="pondOptionLabel(item)"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="初始规格" prop="initSpecs">
          <el-input v-model="form.initSpecs" placeholder="请输入初始规格" />
        </el-form-item> -->
        <el-form-item label="平均打样体长(cm)" prop="avgBodyLength">
          <el-input v-model="form.avgBodyLength" placeholder="请输入平均打样体长(cm)" />
        </el-form-item>
        <el-form-item label="平均打样规格(g)" prop="avgSpecs">
          <el-input v-model="form.avgSpecs" placeholder="请输入平均打样规格(g)" />
        </el-form-item>
        <el-form-item label="打样数量(尾)" prop="currNum">
          <el-input v-model="form.currNum" placeholder="请输入打样数量(尾)" />
        </el-form-item>
        <!-- <el-form-item label="任务明细id" prop="taskDetailId">
          <el-input v-model="form.taskDetailId" placeholder="请输入任务明细id" />
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
    <!-- 查看记录 -->
    <el-dialog
      title="查看打样记录"
      :visible.sync="itemOpen"
      width="70%">
      <div class="cotenent">
        <div v-if="!(form.taskDetailDO && form.taskDetailDO.proofingParamList && form.taskDetailDO.proofingParamList.length)">暂无记录</div>
        <div v-else>
          <el-table
            :data="form.taskDetailDO.proofingParamList"
            style="width: 100%">
            <el-table-column
              prop="tableIndex"
              label="序号"
              width="180">
            </el-table-column>
            <el-table-column
              prop="tableBodyLength"
              label="体长cm"
              width="180">
            </el-table-column>
            <el-table-column
              prop="tableBodyWeight"
              label="规格g">
            </el-table-column>
          </el-table>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { createZkFishProofing, updateZkFishProofing, deleteZkFishProofing, getZkFishProofing, getZkFishProofingPage, exportZkFishProofingExcel, getLastProofing } from "@/api/fish/zkFishProofing";
import FileBusUpload from "@/components/FileUpload";
import PageLoadingMask from '@/components/PageLoadingMask/index';
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { listForBatch } from "@/api/erp/purchaseIn";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getFishLogById, getFishLogByQuery, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadDeptScopedPondList } from "@/utils/siteOptions";
import { notifyProductionRecordChanged } from "@/utils/productionRecordBridge";

export default {
  name: "ZkFishProofing",
  components: {
      FileBusUpload,
      PageLoadingMask,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      pageLoading: true,
      pondLoading: false,
      batchLoading: false,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 打样记录列表
      list: [],
      pondList: [],
      workshopList: [], 
      pondSystemList: [], 
      systemList: [], 
      bachchNumberList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      itemOpen: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        batchNumber: null,
        workshopId: null,
        workshopName: null,
        pondSystemName: null,
        pondSystemId: null,
        pondNum: null,
        pondName: null,
        pondId: null,
        pondDetailNum: null,
        initSpecs: null,
        avgBodyLength: null,
        avgSpecs: null,
        currNum: null,
        taskDetailId: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        batchNumber: [{ required: true, message: '请选择批次编号', trigger: 'change' }],
        pondId: [{ required: true, message: '请选择存塘编号', trigger: 'change' }],
        avgBodyLength: [{ required: true, message: '请输入平均打样体长', trigger: 'blur' }],
        avgSpecs: [{ required: true, message: '请输入平均打样规格', trigger: 'blur' }],
        currNum: [{ required: true, message: '请输入打样数量', trigger: 'blur' }],
      }
    };
  },
  created() {
    this.bootstrapPage();
  },
  methods: {
    bootstrapPage() {
      this.pageLoading = true;
      Promise.all([
        this.getList(true),
        this.getPondList(false),
        this.getBatchNumerList(false)
      ]).finally(() => {
        this.pageLoading = false;
      });
    },
    pondOptionLabel(item) {
      if (!item) return '';
      const ws = item.workshopName || '';
      const sys = item.pondSystemName || item.pondNum || '';
      const pond = item.pondName || item.pondDetailNum || '';
      const scale = item.pondScale != null && item.pondScale !== '' ? item.pondScale + 'm³' : '-';
      const num = item.fishNum != null ? item.fishNum : '-';
      return `${ws}/${sys}/${pond}（${scale}，${num}尾）`;
    },
    seeLog(row) {
      this.reset();
      const id = row.id;
      getZkFishProofing(id).then(response => {
        this.form = response.data;
        this.itemOpen = true;
        this.title = "查看打样记录";
        // console.log(`======================>item:${JSON.stringify(this.form)}`);
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    updateBatchNumerId(currId){
      let item = null;
      this.bachchNumberList.forEach(obj => {
        if(obj.relationOrderNo == currId) {
          item = obj;
        }
      })
      if (!item) return;
      // 2. 执行 赋值
      this.form.supplier = item.supplierName;
      this.form.supplierAddress = item.supplierAddress;
      this.form.supplierId = item.supplierId;
      this.form.productNo = item.productNo;
      this.form.batchNumber = item.relationOrderNo;
      // 后端 initSpecs 为 BigDecimal，禁止提交「10g/尾」带单位字符串
      this.form.initSpecs = this.parseSpecNumber(item.productSpec || item.standard);
      this.form.fishSpec = this.parseSpecNumber(item.productSpec || item.standard);
    },
    /** 从「10g/尾」「10」等文本提取数值 */
    parseSpecNumber(val) {
      if (val == null || val === '') return undefined;
      if (typeof val === 'number' && !isNaN(val)) return val;
      const m = String(val).match(/-?\d+(\.\d+)?/);
      return m ? Number(m[0]) : undefined;
    },
    buildProofingPayload() {
      const f = Object.assign({}, this.form);
      f.initSpecs = this.parseSpecNumber(f.initSpecs);
      f.avgSpecs = this.parseSpecNumber(f.avgSpecs);
      f.avgBodyLength = this.parseSpecNumber(f.avgBodyLength);
      f.currNum = this.parseSpecNumber(f.currNum);
      // fishSpec 非打样表字段，勿随请求提交以免类型错误
      delete f.fishSpec;
      return f;
    },
    getBatchNumerList(force) {
      if (!force && this.bachchNumberList && this.bachchNumberList.length) {
        return Promise.resolve(this.bachchNumberList);
      }
      let queryParams = {
        pageNo: 1,
        pageSize: 500,
        orderNo: null,
        outKind: '1',
        productName: null,
        userId: null,
        categoryId: "7",
        createTime: [],
        oType: 1, // 订单类型(0:出库单;1:入库单;)
      };
      this.batchLoading = true;
      // 与存塘投放等页一致：采购入库批次，不是销售出库分页
      return listForBatch(queryParams).then(response => {
        let list = Array.isArray(response.data) ? response.data : ((response.data && response.data.list) || []);
        list.forEach(item => {
          if (item.remainingInventory === -1) {
            item.remainingInventory = item.productCount;
          }
          const remainRaw = item.remainingInventory;
          const remain = remainRaw == null || remainRaw === '' ? '-' : Number(remainRaw);
          const remainText = remain === '-' || Number.isNaN(remain) ? '-' : (Number.isInteger(remain) ? remain : Math.round(remain));
          item.info = `${item.relationOrderNo || ''}(${item.productName || ''}-${item.productBrand || ''}-${item.supplierName || ''}-剩余${remainText}条)`;
        })
        this.bachchNumberList = list;
        return list;
      }).catch(() => {
        if (!this.bachchNumberList.length) this.bachchNumberList = [];
        return [];
      }).finally(() => {
        this.batchLoading = false;
      });
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
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
        }
      })
      if (!item) return;
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
      this.form.pondSystemId = item.pondSystemId;
      this.form.userName = item.userName;
      if (item.userId != null) this.form.userId = item.userId;
      if (item.deptId != null) this.form.deptId = item.deptId;
    },
    getPondList(force) {
      if (!force && this.pondList && this.pondList.length) {
        return Promise.resolve(this.pondList);
      }
      this.pondLoading = true;
      return loadDeptScopedPondList().then(list => {
        const rows = Array.isArray(list) ? list.slice() : [];
        this.pondList = rows;
        this.getWorkshopList();
        return rows;
      }).catch(() => {
        if (!this.pondList.length) this.pondList = [];
        return [];
      }).finally(() => {
        this.pondLoading = false;
      })
    },
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.workshopList = [];
      this.systemList = [];
      this.pondList.forEach((item) => {
        if(!shopTemp.includes(item.workshopId)) {
          shopTemp.push(item.workshopId);
          this.workshopList.push(item);
        }
        if(!systemTemp.includes(item.pondNum)) {
          systemTemp.push(item.pondNum);
          this.systemList.push(item);
        }
      })
    },
    /** 查询列表 */
    getList(silent) {
      this.loading = true;
      return getZkFishProofingPage(this.queryParams).then(response => {
        this.list = (response.data && response.data.list) ? response.data.list : [];
        this.total = (response.data && response.data.total) != null ? response.data.total : 0;
        return this.list;
      }).catch(() => {
        if (!silent) this.list = [];
        return [];
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.itemOpen = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        batchNumber: undefined,
        workshopId: undefined,
        workshopName: undefined,
        pondSystemName: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondName: undefined,
        pondId: undefined,
        pondDetailNum: undefined,
        initSpecs: undefined,
        avgBodyLength: undefined,
        avgSpecs: undefined,
        currNum: undefined,
        taskDetailId: undefined,
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
      // 已有缓存不重复清空；缺数据时后台补拉，下拉显示「数据正在加载中…」
      if (!this.bachchNumberList.length) this.getBatchNumerList(true);
      if (!this.pondList.length) this.getPondList(true);
      this.open = true;
      this.title = "添加打样记录";
      this.form.fid = getUUID().replaceAll('-', '');
      try {
        const uid = this.$store && this.$store.getters && this.$store.getters.userId;
        const deptId = this.$store && this.$store.getters && this.$store.getters.deptId;
        if (uid != null) this.form.userId = uid;
        if (deptId != null) this.form.deptId = deptId;
      } catch (e) { /* ignore */ }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkFishProofing(id).then(response => {
        this.form = response.data || {};
        this.open = true;
        this.title = "修改打样记录";
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
        const payload = this.buildProofingPayload();
        if (payload.initSpecs == null && this.form.initSpecs) {
          this.$modal.msgWarning('初始规格格式不正确，请填写数字（如 10）');
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateZkFishProofing(payload).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          }).catch(() => {});
          return;
        }
        // 添加的提交
        createZkFishProofing(payload).then(() => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          notifyProductionRecordChanged({ type: 'proofing', pondId: payload.pondId, source: 'zkFishProofing' });
        }).catch(() => {});
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除打样记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkFishProofing(id);
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
      this.$modal.confirm('是否确认导出所有打样记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkFishProofingExcel(params);
        }).then(response => {
          this.$download.excel(response, '打样记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.zk-fish-proofing-page {
  position: relative;
  min-height: 420px;
}
</style>
