<template>
  <div class="app-container">
    <!-- Excel：首页「鱼病诊断」取代智能诊断入口；本页保留历史记录并引导新系统 -->
    <el-alert
      type="warning"
      :closable="false"
      show-icon
      class="mb8"
      title="智能诊断已迁移至「鱼病诊断」系统"
      description="请使用病害诊断（基础配置 / 数据采集 / 协同诊断 / 案例归档）。下方为历史智能诊断记录，仅供查阅。"
    />
    <div style="margin-bottom:12px">
      <el-button type="primary" size="small" icon="el-icon-first-aid-kit" @click="$router.push({ path: '/fish-disease', query: { from: '/fish/zkIntelligentDiagnosis' } })">进入鱼病诊断</el-button>
    </div>

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="车间" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所属车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="车间名称" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="系统" prop="pondSystemId">
        <el-input v-model="queryParams.pondSystemId" placeholder="请输入所属系统id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="存塘系统编号" prop="pondNum">
        <el-input v-model="queryParams.pondNum" placeholder="请输入存塘系统编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘系统名称" prop="pondSystemName">
        <el-input v-model="queryParams.pondSystemName" placeholder="请输入存塘系统名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="存塘" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入所属存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="存塘编号" prop="pondDetailNum">
        <el-input v-model="queryParams.pondDetailNum" placeholder="请输入存塘编号" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="智能体分析报告" prop="aiAgentReport">
        <el-input v-model="queryParams.aiAgentReport" placeholder="请输入智能体分析报告" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="人工判断是否同意不同意写病因分析" prop="humanReport">
        <el-input v-model="queryParams.humanReport" placeholder="请输入人工判断是否同意不同意写病因分析" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="咨询专家后方案" prop="differentialAnalysis">
        <el-input v-model="queryParams.differentialAnalysis" placeholder="请输入咨询专家后方案" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="整体结论" prop="conclusion">
        <el-input v-model="queryParams.conclusion" placeholder="请输入整体结论" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="整体评估" prop="evaluation">
        <el-input v-model="queryParams.evaluation" placeholder="请输入整体评估" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <!-- <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="分析人" prop="analyst">
        <el-input v-model="queryParams.analyst" placeholder="请输入分析人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="专家" prop="expert">
        <el-input v-model="queryParams.expert" placeholder="请输入专家" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="上传的图片" prop="uploadedImages">
        <el-input v-model="queryParams.uploadedImages" placeholder="请输入上传的图片" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-intelligent-diagnosis:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-intelligent-diagnosis:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
   
    <!-- 卡片式展示 -->
    <div v-if="loading" class="loading-container">
      <!-- <el-loading text="加载中..."></el-loading> -->
    </div>
    <el-row v-else-if="list.length" :gutter="20">
      <el-col v-for="(item, index) in list" :key="index" :span="8" class="card-box mb20">
          <el-card class="report-card" shadow="hover">
            <div slot="header" class="card-header">
              <span class="card-title">{{ item.workshopName || '未知车间' }} - {{ item.pondSystemName || '未知系统' }}</span>
              <span class="create-time">{{ parseTime(item.createTime) }}</span>
            </div>
            <div class="card-body">
              <!-- <div class="report-item">
                <h3 class="report-label">负责人</h3>
                <p class="report-content">{{ item.userId || '未知' }}</p>
              </div> -->
              <div class="report-items-container">
                <div class="report-item-box" id="diagnosis_info">
                  <h3 class="report-label">诊断信息概览</h3>
                  <!-- 基础信息 -->
                  <div class="report-meta">
                    <div class="meta-row">
                      <span class="meta-label">批次编号：</span>
                      <span class="meta-value">{{ item.batchNumber || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">鱼苗种类：</span>
                      <span class="meta-value">{{ item.fishTypes || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">来源：</span>
                      <span class="meta-value">{{ item.supplierName || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">投喂方式：</span>
                      <span class="meta-value">{{ item.feedType || '暂无' }}</span>
                    </div>
                  </div>

                  <!-- 病害信息 -->
                  <div class="report-meta">
                    <div class="meta-row">
                      <span class="meta-label">发病数量：</span>
                      <span class="meta-value">{{ item.illNum != null ? item.illNum : '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">病害名称：</span>
                      <span class="meta-value">{{ item.illName || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">病害分类：</span>
                      <span class="meta-value">{{ item.illType || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">异常部位：</span>
                      <span class="meta-value">{{ item.abnormalArea || '暂无' }}</span>
                    </div>
                  </div>

                  <div class="report-content">
                    <div class="meta-row">
                      <span class="meta-label">异常表现与描述：</span>
                    </div>
                    <p class="meta-value">
                      {{ item.abnormalContent || '暂无' }}
                    </p>
                  </div>

                  <!-- 水质指标 -->
                  <div class="report-meta">
                    <div class="meta-row">
                      <span class="meta-label">溶氧：</span>
                      <span class="meta-value">{{ item.rongYangNongDu + '℃' || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">酸碱度：</span>
                      <span class="meta-value">{{ item.suanJianDu + 'mg/L' || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">氨氮：</span>
                      <span class="meta-value">{{ item.anDan + 'pH' || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">水温：</span>
                      <span class="meta-value">{{ item.shuiWen + 'mg/L' || '暂无' }}</span>
                    </div>
                    <div class="meta-row">
                      <span class="meta-label">水位：</span>
                      <span class="meta-value">{{ item.shuiWei + 'm' || '暂无' }}</span>
                    </div>
                  </div>
                </div>
                <div class="report-item-box">
                  <h3 class="report-label">养殖负责人诊断</h3>
                  <p class="report-content">{{ item.humanReport || '暂无反馈' }}</p>
                </div>
                <div class="report-item-box">
                  <h3 class="report-label">专家诊断</h3>
                  <p class="report-content">{{ item.differentialAnalysis || '暂无分析' }}</p>
                </div>
              </div>
              <div class="report-footer">
                <el-button size="mini" type="text" icon="el-icon-edit" style="padding:7px;" @click="handleDetail(item)">进度</el-button>
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(item)" v-hasPermi="['pl:zk-inspection-report:update']">编辑</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(item)" v-hasPermi="['pl:zk-inspection-report:delete']">删除</el-button>
              </div>
            </div>
          </el-card>
      </el-col>
    </el-row>
    <!-- 无数据提示 -->
    <div v-else class="no-data">
      <div class="no-data-icon"><i class="el-icon-empty"></i></div>
      <div class="no-data-text">暂无诊断数据</div>
      <el-button type="primary" size="small" @click="handleAdd">立即创建</el-button>
    </div>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 列表 -->
    <!-- <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="所属车间id" align="center" prop="workshopId" />
      <el-table-column label="车间" align="center" prop="workshopName" />
      <el-table-column label="所属系统id" align="center" prop="pondSystemId" />
      <el-table-column label="存塘系统编号" align="center" prop="pondNum" />
      <el-table-column label="系统" align="center" prop="pondSystemName" />
      <el-table-column label="所属存塘id" align="center" prop="pondId" />
      <el-table-column label="存塘编号" align="center" prop="pondDetailNum" />
      <el-table-column label="存塘" align="center" prop="pondName" />
      <el-table-column label="智能体分析报告" align="center" prop="aiAgentReport" />
      <el-table-column label="人工判断是否同意不同意写病因分析" align="center" prop="humanReport" />
      <el-table-column label="咨询专家后方案" align="center" prop="differentialAnalysis" />
      <el-table-column label="整体结论" align="center" prop="conclusion" />
      <el-table-column label="整体评估" align="center" prop="evaluation" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" />
      <el-table-column label="分析人" align="center" prop="analyst" />
      <el-table-column label="专家" align="center" prop="expert" />
      <el-table-column label="上传的图片" align="center" prop="uploadedImages" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-intelligent-diagnosis:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-intelligent-diagnosis:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属车间id" prop="workshopId">
          <el-input v-model="form.workshopId" placeholder="请输入所属车间id" />
        </el-form-item>
        <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="form.workshopName" placeholder="请输入车间名称" />
        </el-form-item>
        <el-form-item label="所属系统id" prop="pondSystemId">
          <el-input v-model="form.pondSystemId" placeholder="请输入所属系统id" />
        </el-form-item>
        <el-form-item label="存塘系统编号" prop="pondNum">
          <el-input v-model="form.pondNum" placeholder="请输入存塘系统编号" />
        </el-form-item>
        <el-form-item label="存塘系统名称" prop="pondSystemName">
          <el-input v-model="form.pondSystemName" placeholder="请输入存塘系统名称" />
        </el-form-item>
        <el-form-item label="所属存塘id" prop="pondId">
          <el-input v-model="form.pondId" placeholder="请输入所属存塘id" />
        </el-form-item>
        <el-form-item label="存塘编号" prop="pondDetailNum">
          <el-input v-model="form.pondDetailNum" placeholder="请输入存塘编号" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="批次编号" prop="batchNumber">
          <el-input v-model="form.batchNumber" placeholder="请输入批次编号" />
        </el-form-item>
        <el-form-item label="鱼苗种类" prop="fishTypes">
          <el-input v-model="form.fishTypes" placeholder="请输入鱼苗种类" />
        </el-form-item>
        <el-form-item label="来源" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入来源" />
        </el-form-item>
        <el-form-item label="投喂方式" prop="feedType">
          <el-input v-model="form.feedType" placeholder="请输入投喂方式" />
        </el-form-item>
        <el-form-item label="发病数量" prop="illNum">
          <el-input v-model="form.illNum" placeholder="请输入发病数量" type="number" />
        </el-form-item>
        <el-form-item label="病害名称" prop="illName">
          <el-input v-model="form.illName" placeholder="请输入病害名称" />
        </el-form-item>
        <el-form-item label="病害分类" prop="illType">
          <el-input v-model="form.illType" placeholder="请输入病害分类" />
        </el-form-item>
        <el-form-item label="异常部位" prop="abnormalArea">
          <el-input v-model="form.abnormalArea" placeholder="请输入异常部位" />
        </el-form-item>
        <el-form-item label="异常表现及描述" prop="abnormalContent">
          <el-input
            v-model="form.abnormalContent"
            type="textarea"
            :rows="3"
            placeholder="请输入异常表现及描述"
          />
        </el-form-item>
        <el-form-item label="溶氧" prop="rongYangNongDu">
          <el-input v-model="form.rongYangNongDu" placeholder="请输入溶氧" />
        </el-form-item>
        <el-form-item label="酸碱度" prop="suanJianDu">
          <el-input v-model="form.suanJianDu" placeholder="请输入酸碱度" />
        </el-form-item>
        <el-form-item label="氨氮" prop="anDan">
          <el-input v-model="form.anDan" placeholder="请输入氨氮" />
        </el-form-item>
        <el-form-item label="水温" prop="shuiWen">
          <el-input v-model="form.shuiWen" placeholder="请输入水温" />
        </el-form-item>
        <el-form-item label="水位" prop="shuiWei">
          <el-input v-model="form.shuiWei" placeholder="请输入水位" />
        </el-form-item>
        <el-form-item label="图片地址" prop="imageUrls">
          <el-input
            v-model="form.imageUrls"
            type="textarea"
            :rows="2"
            placeholder="请输入图片地址，多个用英文逗号分隔"
          />
        </el-form-item>
        <el-form-item label="视频地址" prop="videoUrls">
          <el-input
            v-model="form.videoUrls"
            type="textarea"
            :rows="2"
            placeholder="请输入视频地址，多个用英文逗号分隔"
          />
        </el-form-item>
        <el-form-item v-if="false" label="智能体分析报告" prop="aiAgentReport">
          <el-input v-model="form.aiAgentReport" type="textarea" :rows="3" placeholder="请输入智能体分析报告" />
        </el-form-item>
        <el-form-item label="人工判断是否同意不同意写病因分析" prop="humanReport">
          <el-input v-model="form.humanReport" type="textarea" :rows="3" placeholder="请输入人工判断是否同意不同意写病因分析" />
        </el-form-item>
        <el-form-item label="咨询专家后方案" prop="differentialAnalysis">
          <el-input v-model="form.differentialAnalysis" type="textarea" :rows="3" placeholder="请输入咨询专家后方案" />
        </el-form-item>
        <el-form-item label="整体结论" prop="conclusion">
          <el-input v-model="form.conclusion" placeholder="请输入整体结论" />
        </el-form-item>
        <el-form-item label="整体评估" prop="evaluation">
          <el-input v-model="form.evaluation" placeholder="请输入整体评估" />
        </el-form-item>
        <el-form-item label="负责人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入负责人id" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="文件id" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文件id" />
        </el-form-item>
        <el-form-item label="分析人" prop="analyst">
          <el-input v-model="form.analyst" placeholder="请输入分析人" />
        </el-form-item>
        <el-form-item label="专家" prop="expert">
          <el-input v-model="form.expert" placeholder="请输入专家" />
        </el-form-item>
        <el-form-item label="上传的图片" prop="uploadedImages">
          <el-input v-model="form.uploadedImages" placeholder="请输入上传的图片" />
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
import { createZkIntelligentDiagnosis, updateZkIntelligentDiagnosis, deleteZkIntelligentDiagnosis, getZkIntelligentDiagnosis, getZkIntelligentDiagnosisPage, exportZkIntelligentDiagnosisExcel } from "@/api/fish/zkIntelligentDiagnosis";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkIntelligentDiagnosis",
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
      // 智能诊断列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 3,
        workshopId: null,
        workshopName: null,
        pondSystemId: null,
        pondNum: null,
        pondSystemName: null,
        pondId: null,
        pondDetailNum: null,
        pondName: null,
        aiAgentReport: null,
        humanReport: null,
        differentialAnalysis: null,
        conclusion: null,
        evaluation: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
        analyst: null,
        expert: null,
        uploadedImages: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleDetail(row) {
      console.log(`==========>row:${JSON.stringify(row)}`);
      this.$router.push({ name: "BpmProcessInstanceDetail", query: { id: row.processInstanceId}});
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkIntelligentDiagnosisPage(this.queryParams).then(response => {
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
        workshopId: undefined,
        workshopName: undefined,
        pondSystemId: undefined,
        pondNum: undefined,
        pondSystemName: undefined,
        pondId: undefined,
        pondDetailNum: undefined,
        pondName: undefined,
        aiAgentReport: undefined,
        humanReport: undefined,
        differentialAnalysis: undefined,
        conclusion: undefined,
        evaluation: undefined,
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        analyst: undefined,
        expert: undefined,
        uploadedImages: undefined,
        batchNumber: undefined,
        fishTypes: undefined,
        supplierName: undefined,
        feedType: undefined,
        illNum: undefined,
        illName: undefined,
        illType: undefined,
        abnormalArea: undefined,
        abnormalContent: undefined,
        rongYangNongDu: undefined,
        suanJianDu: undefined,
        anDan: undefined,
        shuiWen: undefined,
        shuiWei: undefined,
        imageUrls: undefined,
        videoUrls: undefined,
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
      this.title = "添加智能诊断";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkIntelligentDiagnosis(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改智能诊断";
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
          updateZkIntelligentDiagnosis(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkIntelligentDiagnosis(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除智能诊断编号为"' + id + '"的数据项?').then(function() {
          return deleteZkIntelligentDiagnosis(id);
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
      this.$modal.confirm('是否确认导出所有智能诊断数据项?').then(() => {
          this.exportLoading = true;
          return exportZkIntelligentDiagnosisExcel(params);
        }).then(response => {
          this.$download.excel(response, '智能诊断.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.card-box {
  margin-bottom: 20px;
}
.report-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, 
    rgba(10, 21, 32, 0.95) 0%, 
    rgba(15, 26, 38, 0.98) 50%,
    rgba(20, 35, 50, 0.95) 100%) !important;
  border: 2px solid rgba(64, 158, 255, 0.25) !important;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.6),
    0 0 0 1px rgba(64, 158, 255, 0.15) inset,
    0 2px 8px rgba(64, 158, 255, 0.1) !important;
  border-radius: 8px !important;
  
  ::v-deep .el-card__body {
    background: transparent !important;
    color: #ffffff !important;
  }
  
  ::v-deep .el-card__header {
    background: transparent !important;
    border-bottom: 1px solid rgba(64, 158, 255, 0.3) !important;
  }
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: transparent;
  border-bottom: none;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
}
.create-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}
.report-item {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.2);
}
.report-items-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
}
#diagnosis_info {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  grid-auto-rows: auto;
  column-gap: 16px;
  row-gap: 10px;
}
#diagnosis_info .report-label {
  grid-column: 1 / 3;
}
#diagnosis_info .report-content {
  grid-column: 1 / 3;
}
.report-item-box {
  width: 100%;
  border: 1px solid rgba(64, 158, 255, 0.25);
  border-radius: 6px;
  padding: 15px;
  background: linear-gradient(135deg, 
    rgba(10, 21, 32, 0.8) 0%, 
    rgba(15, 26, 38, 0.85) 100%);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(64, 158, 255, 0.1) inset;
  display: flex;
  flex-direction: column;
}
.report-label {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #ffffff;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.2);
}
.report-content {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.6;
  flex-grow: 1;
  word-break: break-word;
  max-height: 80px;
  overflow: auto;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.2);
    border-radius: 3px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(64, 158, 255, 0.5);
    border-radius: 3px;
    &:hover {
      background: rgba(64, 158, 255, 0.7);
    }
  }
}
.report-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(64, 158, 255, 0.2);
  
  ::v-deep .el-button--text {
    color: rgba(255, 255, 255, 0.8) !important;
    &:hover {
      color: #409EFF !important;
    }
  }
}
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}
.no-data {
  text-align: center;
  padding: 40px 0;
  color: rgba(255, 255, 255, 0.6);
  background: linear-gradient(135deg, 
    rgba(10, 21, 32, 0.8) 0%, 
    rgba(15, 26, 38, 0.85) 100%);
  border: 1px solid rgba(64, 158, 255, 0.25);
  border-radius: 8px;
}
.no-data-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.3);
}
.no-data-text {
  margin-bottom: 24px;
  color: rgba(255, 255, 255, 0.6);
}
</style>
