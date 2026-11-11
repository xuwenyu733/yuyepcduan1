<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="用药方案" prop="medicineName">
        <el-input v-model="zkUseMedicineItem.medicineName" placeholder="请输入用药方案" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <div style="display: flex;flex-direction: column;">
        <div style="font-weight: bold;font-size: 14px;">用药方案：</div>
        <!-- background-color: #fff; -->
        <div style="margin-left: 13px;font-size: 14px;">{{ plainMedicineName }}</div>
      </div>
      <!-- 
      <el-form-item label="用药方案id" prop="zkUseMedicineId">
        <el-input v-model="queryParams.zkUseMedicineId" placeholder="请输入用药方案id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="系统编号(车间序号_存塘序号)" prop="pondNum">
        <el-input v-model="queryParams.pondNum" placeholder="请输入系统编号(车间序号_存塘序号)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘规模" prop="pondScale">
        <el-input v-model="queryParams.pondScale" placeholder="请输入存塘规模" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘鱼数" prop="fishNum">
        <el-input v-model="queryParams.fishNum" placeholder="请输入存塘鱼数" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="商品编号" prop="productNo">
        <el-input v-model="queryParams.productNo" placeholder="请输入商品编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="用药名称" prop="medicineName">
        <el-input v-model="queryParams.medicineName" placeholder="请输入用药名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="用药用量" prop="midicineDosage">
        <el-input v-model="queryParams.midicineDosage" placeholder="请输入用药用量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="诊断标题" prop="controlTitle">
        <el-input v-model="queryParams.controlTitle" placeholder="请输入诊断标题" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="用药结果" prop="medicineAfter">
        <el-input v-model="queryParams.medicineAfter" placeholder="请输入用药结果" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="品牌" prop="medicineBrand">
        <el-input v-model="queryParams.medicineBrand" placeholder="请输入品牌" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="规格" prop="medicineSpec">
        <el-input v-model="queryParams.medicineSpec" placeholder="请输入规格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="用药时间" prop="feedTime">
        <el-date-picker v-model="queryParams.feedTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="类型(1:集群用药;2:隔离用药;)" prop="feedType">
        <el-select v-model="queryParams.feedType" placeholder="请选择类型(1:集群用药;2:隔离用药;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入负责人所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item> -->
      <!-- <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item> -->
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-use-medicine-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-use-medicine-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="用药方案id(zk_use_medicine.id)" align="center" prop="zkUseMedicineId" /> -->
      <!-- <el-table-column label="系统编号" align="center" prop="pondNum" /> -->
      <el-table-column label="存塘编号" align="center" prop="pondDetailNumStr" />
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" />
      <el-table-column label="存塘id" align="center" prop="pondId" /> -->
      <el-table-column label="存塘规模" align="center" prop="pondScale" />
      <el-table-column label="存塘鱼数" align="center" prop="fishNum" />
      <el-table-column label="商品编号" align="center" prop="productNo" show-overflow-tooltip/>
      <el-table-column label="用药名称" align="center" prop="medicineName" />
      <el-table-column label="用药用量" align="center" prop="midicineDosage" />
      <!-- <el-table-column label="诊断标题" align="center" prop="controlTitle" /> -->
      <el-table-column label="用药结果" align="center" prop="medicineAfter" />
      <el-table-column label="品牌" align="center" prop="medicineBrand" />
      <el-table-column label="规格" align="center" prop="medicineSpec" />
      <el-table-column label="用药时间" align="center" prop="feedTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.feedTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <!-- <el-table-column label="类型(1:集群用药;2:隔离用药;)" align="center" prop="feedType" /> -->
      <!-- <el-table-column label="负责人id" align="center" prop="userId" /> -->
      <el-table-column label="负责人" align="center" prop="userName" show-overflow-tooltip/>
      <!-- <el-table-column label="负责人所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-use-medicine-log:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-use-medicine-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="用药方案id(zk_use_medicine.id)" prop="zkUseMedicineId">
          <el-input v-model="form.zkUseMedicineId" placeholder="请输入用药方案id(zk_use_medicine.id)" />
        </el-form-item>
        <el-form-item label="系统编号(车间序号_存塘序号)" prop="pondNum">
          <el-input v-model="form.pondNum" placeholder="请输入系统编号(车间序号_存塘序号)" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="存塘id" prop="pondId">
          <el-input v-model="form.pondId" placeholder="请输入存塘id" />
        </el-form-item>
        <el-form-item label="存塘规模" prop="pondScale">
          <el-input v-model="form.pondScale" placeholder="请输入存塘规模" />
        </el-form-item>
        <el-form-item label="存塘鱼数" prop="fishNum">
          <el-input v-model="form.fishNum" placeholder="请输入存塘鱼数" />
        </el-form-item> -->
        <!-- <el-form-item label="商品编号" prop="productNo">
          <el-input v-model="form.productNo" placeholder="请输入商品编号" />
        </el-form-item> -->
        <!-- <el-form-item label="用药名称" prop="medicineName">
          <el-input v-model="form.medicineName" placeholder="请输入用药名称" />
        </el-form-item> -->
        <el-form-item label="所在存塘" prop="pondIdArr">
          <el-select style="width:100%;" v-model="form.pondIdArr" placeholder="请选择所在存塘" multiple filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="对应用药" prop="productNo">
          <el-select style="width:100%;" v-model="form.productNo" placeholder="请选择对应用药" filterable clearable  @change="updateProductId">
            <el-option v-for="(item, index) in productList" :key="item.id+'__'+index" :label="`${item.info}`"
              :value="item.productNo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用药用量" prop="midicineDosage">
          <!-- 药规格的几分之几 -->
          <el-input v-model="form.midicineDosage" placeholder="请输入所有存塘的汇总用药用量(请注明单位,eg:ml、瓶)" />
        </el-form-item>
        <!-- <el-form-item label="诊断标题" prop="controlTitle">
          <el-input v-model="form.controlTitle" placeholder="请输入诊断标题" />
        </el-form-item> -->
        <el-form-item label="用药结果" prop="medicineAfter">
          <el-input v-model="form.medicineAfter" placeholder="请输入用药结果" />
        </el-form-item>
        <!-- <el-form-item label="品牌" prop="medicineBrand">
          <el-input v-model="form.medicineBrand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="规格" prop="medicineSpec">
          <el-input v-model="form.medicineSpec" placeholder="请输入规格" />
        </el-form-item> -->
        <el-form-item label="用药时间" prop="feedTime">
          <el-date-picker clearable style="width: 100%;" v-model="form.feedTime" type="datetime" value-format="timestamp" placeholder="选择用药时间" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <!-- <el-form-item label="类型" prop="feedType">
          <el-select v-model="form.feedType" placeholder="请选择类型(1:集群用药;2:隔离用药;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="负责人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入负责人id" />
        </el-form-item>
        <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="负责人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入负责人所属部门" />
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
import { createZkUseMedicineLog, updateZkUseMedicineLog, deleteZkUseMedicineLog, getZkUseMedicineLog, getZkUseMedicineLogPage, exportZkUseMedicineLogExcel } from "@/api/fish/zkUseMedicineLog";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkUseMedicine, updateZkUseMedicine, deleteZkUseMedicine, getZkUseMedicine, getZkUseMedicinePage, exportZkUseMedicineExcel } from "@/api/fish/zkUseMedicine";
import {
  getProductPage
} from "@/api/erp/product";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";

export default {
  name: "ZkUseMedicineLog",
  components: {
      FileBusUpload,
  },
  data() {
    return {
      operationLoading: false,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用药明细列表
      list: [],
      zkUseMedicineItem: {}, // 当前用药方案
      productList: [],
      pondList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        zkUseMedicineId: null,
        pondNum: null,
        pondName: null,
        pondId: null,
        pondScale: null,
        fishNum: null,
        productNo: null,
        medicineName: null,
        midicineDosage: null,
        controlTitle: null,
        medicineAfter: null,
        medicineBrand: null,
        medicineSpec: null,
        feedTime: [],
        remark: null,
        feedType: null,
        userId: null,
        userName: null,
        deptId: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      editType: 1, // 1:添加;2:编辑;
      // 表单校验
      rules: {
        zkUseMedicineId: [{ required: true, message: "用药方案id(zk_use_medicine.id)不能为空", trigger: "blur" }],
        pondId: [{ required: true, message: "存塘id不能为空", trigger: "blur" }],
        feedTime: [{ required: true, message: "用药时间不能为空", trigger: "blur" }],
        productNo: [{ required: true, message: "用药不能为空", trigger: "blur" }],
        midicineDosage: [{ required: true, message: "用药用量不能为空", trigger: "blur" }],
        pondIdArr: [{ required: true, message: "存塘不能为空", trigger: "blur" }],
      }
    };
  },
  computed: {
    /** 列表/页头不以 HTML 展示用药方案名 */
    plainMedicineName() {
      const raw = (this.zkUseMedicineItem && this.zkUseMedicineItem.medicineName) || '';
      return String(raw).replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim() || '—';
    }
  },
  created() {
    const zkUseMedicineId = this.$route.query && this.$route.query.zkUseMedicineId;
    console.log(`==========>zkUseMedicineId:${zkUseMedicineId}`);
    if(zkUseMedicineId) {
      this.getZkUseMedicineById(zkUseMedicineId);
    }
    this.getPondList();
    this.getList();
    this.flushChoose();
  },
  watch: {
    'form.midicineDosage': {
      handler(newV, oldV) {
        if(!this.form.medicinePrice && this.editType == 1 ) {
          this.$modal.msgWarning("请先选择用药！");
          this.form.midicineDosage = 0;
          return;
        }
        if (newV && this.form.medicinePrice) {
          // this.form.procurementCost = newV*this.form.catalogPrice;
          this.form.medicineCost = this.my_common.computer(newV, this.form.medicinePrice, 2, 4);
        }
        console.log("===>newV:{},oldV:{},medicineCost:{}",newV, oldV, this.form.medicineCost)  // salesIntensity
      }
    },
  },
  methods: {
    updatePondId(currId){
      console.log(`updatePondId===================>`, currId);
      return;
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
      getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list);
      })
    },
    flushChoose() {
      // this.getPondList();
      // this.getUserList();
      this.getProductList();
    },
    getZkUseMedicineById(zkUseMedicineId) {
      getZkUseMedicine(zkUseMedicineId).then(response => {
        console.log(`getZkUseMedicine=====>response:${JSON.stringify(response)}`);
        this.zkUseMedicineItem = response.data;
      });
    },
    updateProductId(currId){
      let item = null;
      this.productList.forEach(obj => {
        if(obj.productNo == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.medicineName = item.productName;
      this.form.medicineBrand = item.productBrand;
      this.form.medicineSpec = item.productSpec;
      this.form.productNo = item.productNo;
      this.form.medicinePrice = item.purchasePrice;
      // this.form.userName = item.userName;
      // this.form.userId = item.userId;
      // console.log("===========>"+JSON.stringify(this.form));
      console.log("===========>"+JSON.stringify(item));
    },
    getProductList() {
      this.productList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
				categoryId: 2,
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
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkUseMedicineLogPage(this.queryParams).then(response => {
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
        zkUseMedicineId: undefined,
        pondNum: undefined,
        pondName: undefined,
        pondId: undefined,
        pondScale: undefined,
        fishNum: undefined,
        productNo: undefined,
        medicineName: undefined,
        midicineDosage: undefined,
        controlTitle: undefined,
        medicineAfter: undefined,
        medicineBrand: undefined,
        medicineSpec: undefined,
        feedTime: new Date().getTime(),
        remark: undefined,
        feedType: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        pondIdArr: undefined,
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
      this.flushChoose();
      this.reset();
      this.editType = 1;
      this.open = true;
      this.title = "添加用药明细";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.flushChoose();
      this.reset();
      const id = row.id;
      this.editType = 2;
      getZkUseMedicineLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改用药明细";
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
        if(this.zkUseMedicineItem) {
          this.form.zkUseMedicineId = this.zkUseMedicineItem.id;
          this.form.pondNum = this.zkUseMedicineItem.pondNum;
          this.form.pondDetailNum = this.zkUseMedicineItem.pondDetailNum;
          this.form.pondId = this.zkUseMedicineItem.pondId;
          this.form.pondScale = this.zkUseMedicineItem.pondName;
          this.form.pondScale = this.zkUseMedicineItem.pondScale;
          this.form.fishNum = this.zkUseMedicineItem.fishNum;
          this.form.userId = this.zkUseMedicineItem.userId;
          this.form.userName = this.zkUseMedicineItem.userName;
          this.form.deptId = this.zkUseMedicineItem.deptId;
        }
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        this.operationLoading = true;
        const loading = this.$loading({
          lock: true,
          text: 'Loading',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        // console.log(`当前提交的参数: ${JSON.stringify(this.form)}`);
        // return;
        // 修改的提交
        if (this.form.id != null) {
          updateZkUseMedicineLog(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
            setTimeout(()=>{
              this.operationLoading = false;
              loading.close();
            }, 1000);
          });
          return;
        }
        // 添加的提交
        createZkUseMedicineLog(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          setTimeout(()=>{
            this.operationLoading = false;
            loading.close();
          }, 1000);
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除用药明细编号为"' + id + '"的数据项?').then(function() {
          return deleteZkUseMedicineLog(id);
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
      this.$modal.confirm('是否确认导出所有用药明细数据项?').then(() => {
          this.exportLoading = true;
          return exportZkUseMedicineLogExcel(params);
        }).then(response => {
          this.$download.excel(response, '用药明细.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
