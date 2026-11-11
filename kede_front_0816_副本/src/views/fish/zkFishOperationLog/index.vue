<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="鱼苗批次" prop="batchNumber">
        <el-input v-model="queryParams.batchNumber" placeholder="请输入鱼苗批次" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属系统id" prop="pondSystemId">
        <el-input v-model="queryParams.pondSystemId" placeholder="请输入所属系统id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘系统编号" prop="pondNum">
        <el-input v-model="queryParams.pondNum" placeholder="请输入存塘系统编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘系统名称" prop="pondSystemName">
        <el-input v-model="queryParams.pondSystemName" placeholder="请输入存塘系统名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="统计时间(yyyy-MM-dd)" prop="logDay">
        <el-input v-model="queryParams.logDay" placeholder="请输入统计时间(yyyy-MM-dd)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="鱼苗编码(pl_product.product_no)" prop="productNo">
        <el-input v-model="queryParams.productNo" placeholder="请输入鱼苗编码(pl_product.product_no)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="鱼苗种类" prop="fishTypes">
        <el-input v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="鱼苗规格" prop="fishSpec">
        <el-input v-model="queryParams.fishSpec" placeholder="请输入鱼苗规格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="操作数量" prop="dayFishNum">
        <el-input v-model="queryParams.dayFishNum" placeholder="请输入操作数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="累计操作数量" prop="accumulateFishNum">
        <el-input v-model="queryParams.accumulateFishNum" placeholder="请输入累计操作数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="投放数量" prop="pushFishNum">
        <el-input v-model="queryParams.pushFishNum" placeholder="请输入投放数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="当日操作率" prop="dayRate">
        <el-input v-model="queryParams.dayRate" placeholder="请输入当日操作率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="累计操作率" prop="accumulateRate">
        <el-input v-model="queryParams.accumulateRate" placeholder="请输入累计操作率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="成活率" prop="survivalRate">
        <el-input v-model="queryParams.survivalRate" placeholder="请输入成活率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="修正后操作数量" prop="newDayFishNum">
        <el-input v-model="queryParams.newDayFishNum" placeholder="请输入修正后操作数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="修正后累计操作数量" prop="newAccumulateFishNum">
        <el-input v-model="queryParams.newAccumulateFishNum" placeholder="请输入修正后累计操作数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="修正后投放数量" prop="newPushFishNum">
        <el-input v-model="queryParams.newPushFishNum" placeholder="请输入修正后投放数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="修正后当日操作率" prop="newDayRate">
        <el-input v-model="queryParams.newDayRate" placeholder="请输入修正后当日操作率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="修正后累计操作率" prop="newAccumulateRate">
        <el-input v-model="queryParams.newAccumulateRate" placeholder="请输入修正后累计操作率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="修正后成活率" prop="newSurvivalRate">
        <el-input v-model="queryParams.newSurvivalRate" placeholder="请输入修正后成活率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="修正依据" prop="correctionBasis">
        <el-input v-model="queryParams.correctionBasis" placeholder="请输入修正依据" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="差值率" prop="differencesRate">
        <el-input v-model="queryParams.differencesRate" placeholder="请输入差值率" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="差异原因" prop="reasonsForDifferences">
        <el-input v-model="queryParams.reasonsForDifferences" placeholder="请输入差异原因" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="危险评级(1:正常;2:持续关注;3:立刻介入;)" prop="dangerRating">
        <el-input v-model="queryParams.dangerRating" placeholder="请输入危险评级(1:正常;2:持续关注;3:立刻介入;)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)" prop="logType">
        <el-select v-model="queryParams.logType" placeholder="请选择统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="评级人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入评级人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="评级人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入评级人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-fish-operation-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-fish-operation-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="鱼苗批次" align="center" prop="batchNumber" />
      <el-table-column label="所属车间id" align="center" prop="workshopId" />
      <el-table-column label="所属系统id" align="center" prop="pondSystemId" />
      <el-table-column label="存塘系统编号" align="center" prop="pondNum" />
      <el-table-column label="所属存塘id" align="center" prop="pondId" />
      <el-table-column label="车间名称" align="center" prop="workshopName" />
      <el-table-column label="存塘系统名称" align="center" prop="pondSystemName" />
      <el-table-column label="存塘名称" align="center" prop="pondName" />
      <el-table-column label="统计时间(yyyy-MM-dd)" align="center" prop="logDay" />
      <el-table-column label="鱼苗编码(pl_product.product_no)" align="center" prop="productNo" />
      <el-table-column label="鱼苗种类" align="center" prop="fishTypes" />
      <el-table-column label="鱼苗规格" align="center" prop="fishSpec" />
      <el-table-column label="操作数量" align="center" prop="dayFishNum" />
      <el-table-column label="累计操作数量" align="center" prop="accumulateFishNum" />
      <el-table-column label="投放数量" align="center" prop="pushFishNum" />
      <el-table-column label="当日操作率" align="center" prop="dayRate" />
      <el-table-column label="累计操作率" align="center" prop="accumulateRate" />
      <el-table-column label="成活率" align="center" prop="survivalRate" />
      <el-table-column label="修正后操作数量" align="center" prop="newDayFishNum" />
      <el-table-column label="修正后累计操作数量" align="center" prop="newAccumulateFishNum" />
      <el-table-column label="修正后投放数量" align="center" prop="newPushFishNum" />
      <el-table-column label="修正后当日操作率" align="center" prop="newDayRate" />
      <el-table-column label="修正后累计操作率" align="center" prop="newAccumulateRate" />
      <el-table-column label="修正后成活率" align="center" prop="newSurvivalRate" />
      <el-table-column label="修正依据" align="center" prop="correctionBasis" />
      <el-table-column label="差值率" align="center" prop="differencesRate" />
      <el-table-column label="差异原因" align="center" prop="reasonsForDifferences" />
      <el-table-column label="具体描述" align="center" prop="detailedDescription" />
      <el-table-column label="危险评级(1:正常;2:持续关注;3:立刻介入;)" align="center" prop="dangerRating" />
      <el-table-column label="统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)" align="center" prop="logType" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="评级人id" align="center" prop="userId" />
      <el-table-column label="评级人" align="center" prop="userName" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-fish-operation-log:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-fish-operation-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="鱼苗批次" prop="batchNumber">
          <el-input v-model="form.batchNumber" placeholder="请输入鱼苗批次" />
        </el-form-item>
        <el-form-item label="所属车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所属车间id" />
        </el-form-item>
        <el-form-item label="所属系统id" prop="pondSystemId">
          <el-input v-model="form.pondSystemId" placeholder="请输入所属系统id" />
        </el-form-item>
        <el-form-item label="存塘系统编号" prop="pondNum">
          <el-input v-model="form.pondNum" placeholder="请输入存塘系统编号" />
        </el-form-item>
        <el-form-item label="所属存塘id" prop="pondId">
          <el-input v-model="form.pondId" placeholder="请输入所属存塘id" />
        </el-form-item>
        <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入车间名称" />
        </el-form-item>
        <el-form-item label="存塘系统名称" prop="pondSystemName">
          <el-input v-model="form.pondSystemName" placeholder="请输入存塘系统名称" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="统计时间(yyyy-MM-dd)" prop="logDay">
          <el-input v-model="form.logDay" placeholder="请输入统计时间(yyyy-MM-dd)" />
        </el-form-item>
        <el-form-item label="鱼苗编码(pl_product.product_no)" prop="productNo">
          <el-input v-model="form.productNo" placeholder="请输入鱼苗编码(pl_product.product_no)" />
        </el-form-item>
        <el-form-item label="鱼苗种类" prop="fishTypes">
          <el-input v-model="form.fishTypes" placeholder="请输入鱼苗种类" />
        </el-form-item>
        <el-form-item label="鱼苗规格" prop="fishSpec">
          <el-input v-model="form.fishSpec" placeholder="请输入鱼苗规格" />
        </el-form-item>
        <el-form-item label="操作数量" prop="dayFishNum">
          <el-input v-model="form.dayFishNum" placeholder="请输入操作数量" />
        </el-form-item>
        <el-form-item label="累计操作数量" prop="accumulateFishNum">
          <el-input v-model="form.accumulateFishNum" placeholder="请输入累计操作数量" />
        </el-form-item>
        <el-form-item label="投放数量" prop="pushFishNum">
          <el-input v-model="form.pushFishNum" placeholder="请输入投放数量" />
        </el-form-item>
        <el-form-item label="当日操作率" prop="dayRate">
          <el-input v-model="form.dayRate" placeholder="请输入当日操作率" />
        </el-form-item>
        <el-form-item label="累计操作率" prop="accumulateRate">
          <el-input v-model="form.accumulateRate" placeholder="请输入累计操作率" />
        </el-form-item>
        <el-form-item label="成活率" prop="survivalRate">
          <el-input v-model="form.survivalRate" placeholder="请输入成活率" />
        </el-form-item>
        <el-form-item label="修正后操作数量" prop="newDayFishNum">
          <el-input v-model="form.newDayFishNum" placeholder="请输入修正后操作数量" />
        </el-form-item>
        <el-form-item label="修正后累计操作数量" prop="newAccumulateFishNum">
          <el-input v-model="form.newAccumulateFishNum" placeholder="请输入修正后累计操作数量" />
        </el-form-item>
        <el-form-item label="修正后投放数量" prop="newPushFishNum">
          <el-input v-model="form.newPushFishNum" placeholder="请输入修正后投放数量" />
        </el-form-item>
        <el-form-item label="修正后当日操作率" prop="newDayRate">
          <el-input v-model="form.newDayRate" placeholder="请输入修正后当日操作率" />
        </el-form-item>
        <el-form-item label="修正后累计操作率" prop="newAccumulateRate">
          <el-input v-model="form.newAccumulateRate" placeholder="请输入修正后累计操作率" />
        </el-form-item>
        <el-form-item label="修正后成活率" prop="newSurvivalRate">
          <el-input v-model="form.newSurvivalRate" placeholder="请输入修正后成活率" />
        </el-form-item>
        <el-form-item label="修正依据" prop="correctionBasis">
          <el-input v-model="form.correctionBasis" placeholder="请输入修正依据" />
        </el-form-item>
        <el-form-item label="差值率" prop="differencesRate">
          <el-input v-model="form.differencesRate" placeholder="请输入差值率" />
        </el-form-item>
        <el-form-item label="差异原因" prop="reasonsForDifferences">
          <el-input v-model="form.reasonsForDifferences" placeholder="请输入差异原因" />
        </el-form-item>
        <el-form-item label="具体描述">
          <editor v-model="form.detailedDescription" :min-height="192"/>
        </el-form-item>
        <el-form-item label="危险评级(1:正常;2:持续关注;3:立刻介入;)" prop="dangerRating">
          <el-input v-model="form.dangerRating" placeholder="请输入危险评级(1:正常;2:持续关注;3:立刻介入;)" />
        </el-form-item>
        <el-form-item label="统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)" prop="logType">
          <el-select v-model="form.logType" placeholder="请选择统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="评级人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入评级人id" />
        </el-form-item>
        <el-form-item label="评级人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入评级人" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
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
import { createZkFishOperationLog, updateZkFishOperationLog, deleteZkFishOperationLog, getZkFishOperationLog, getZkFishOperationLogPage, exportZkFishOperationLogExcel } from "@/api/fish/zkFishOperationLog";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkFishOperationLog",
  components: {
      FileBusUpload,
    Editor,
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
      // 鱼苗统计列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        batchNumber: null,
        workshopId: null,
        pondSystemId: null,
        pondNum: null,
        pondId: null,
        workshopName: null,
        pondSystemName: null,
        pondName: null,
        logDay: null,
        productNo: null,
        fishTypes: null,
        fishSpec: null,
        dayFishNum: null,
        accumulateFishNum: null,
        pushFishNum: null,
        dayRate: null,
        accumulateRate: null,
        survivalRate: null,
        newDayFishNum: null,
        newAccumulateFishNum: null,
        newPushFishNum: null,
        newDayRate: null,
        newAccumulateRate: null,
        newSurvivalRate: null,
        correctionBasis: null,
        differencesRate: null,
        reasonsForDifferences: null,
        detailedDescription: null,
        dangerRating: null,
        logType: null,
        createTime: [],
        userId: null,
        userName: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dayFishNum: [{ required: true, message: "操作数量不能为空", trigger: "blur" }],
        accumulateFishNum: [{ required: true, message: "累计操作数量不能为空", trigger: "blur" }],
        pushFishNum: [{ required: true, message: "投放数量不能为空", trigger: "blur" }],
        newDayFishNum: [{ required: true, message: "修正后操作数量不能为空", trigger: "blur" }],
        newAccumulateFishNum: [{ required: true, message: "修正后累计操作数量不能为空", trigger: "blur" }],
        newPushFishNum: [{ required: true, message: "修正后投放数量不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkFishOperationLogPage(this.queryParams).then(response => {
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
        batchNumber: undefined,
        workshopId: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondId: undefined,
        workshopName: undefined,
        pondSystemName: undefined,
        pondName: undefined,
        logDay: undefined,
        productNo: undefined,
        fishTypes: undefined,
        fishSpec: undefined,
        dayFishNum: undefined,
        accumulateFishNum: undefined,
        pushFishNum: undefined,
        dayRate: undefined,
        accumulateRate: undefined,
        survivalRate: undefined,
        newDayFishNum: undefined,
        newAccumulateFishNum: undefined,
        newPushFishNum: undefined,
        newDayRate: undefined,
        newAccumulateRate: undefined,
        newSurvivalRate: undefined,
        correctionBasis: undefined,
        differencesRate: undefined,
        reasonsForDifferences: undefined,
        detailedDescription: undefined,
        dangerRating: undefined,
        logType: undefined,
        userId: undefined,
        userName: undefined,
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
      this.title = "添加鱼苗统计";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkFishOperationLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改鱼苗统计";
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
          updateZkFishOperationLog(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkFishOperationLog(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除鱼苗统计编号为"' + id + '"的数据项?').then(function() {
          return deleteZkFishOperationLog(id);
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
      this.$modal.confirm('是否确认导出所有鱼苗统计数据项?').then(() => {
          this.exportLoading = true;
          return exportZkFishOperationLogExcel(params);
        }).then(response => {
          this.$download.excel(response, '鱼苗统计.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
