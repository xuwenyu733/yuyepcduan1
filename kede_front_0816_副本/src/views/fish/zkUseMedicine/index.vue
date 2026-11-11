<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
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
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}`"
            :value="item.pondDetailNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="起始时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="起始时间" end-placeholder="截至时间" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="用药效果评估" prop="medicineAfter">
        <el-input v-model="queryParams.medicineAfter" placeholder="请输入用药效果评估" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="用药方案" prop="medicineName">
        <el-input v-model="queryParams.medicineName" placeholder="请输入用药方案" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="用法用量" prop="midicineDosage">
        <el-input v-model="queryParams.midicineDosage" placeholder="请输入用法用量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="症状" prop="controlContent">
        <el-input v-model="queryParams.controlContent" placeholder="请输入症状" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <!-- <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.USE_MEDICINE_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="附件" prop="fids">
        <el-input v-model="queryParams.fids" placeholder="请输入附件" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="流程实例" prop="processInstanceId">
        <el-input v-model="queryParams.processInstanceId" placeholder="请输入流程实例" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="用药人" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用药人" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-use-medicine:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-use-medicine:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="车间" align="center" prop="workshopName" show-overflow-tooltip/>
      <el-table-column label="系统" align="center" prop="pondSystemName" show-overflow-tooltip/>
      <el-table-column label="池号" align="center" prop="pondName" show-overflow-tooltip/>
      <el-table-column label="起始时间(用药)" align="center" prop="createTime" width="150">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="停药时间" align="center" prop="stopMedicationTime" width="150">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.stopMedicationTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="症状(文字)" align="center" prop="controlContent" width="120"  show-overflow-tooltip/>
      <!-- <el-table-column label="症状(图像)" align="center" prop="symptomUrls"  show-overflow-tooltip/> -->
      <el-table-column label="症状(图像)" align="center" prop="picUrl" width="120">
        <template v-slot="scope">
          <image-preview v-if="scope.row.symptomUrls != null && scope.row.symptomUrls != ''" :src="scope.row.symptomUrls"
            width="60px" height="60px"></image-preview>
          <i v-else>
            暂无图像
          </i>
        </template>
      </el-table-column>
      <el-table-column label="症状(视频)" align="center" prop="picUrl" width="120">
        <template v-slot="scope">
          <div style="" class="videoShowDiv"
            v-if="scope.row.symptomVideos != null && scope.row.symptomVideos != ''" :src="scope.row.symptomVideos"
            @click.stop="openVideo(scope.row.symptomVideos)">视频</div>
          <i v-else>
            暂无内容
          </i>
        </template>
      </el-table-column>
      <!-- <el-table-column label="症状(视频)" align="center" prop="symptomVideos" width="120"  show-overflow-tooltip/> -->
      <el-table-column label="水质" align="center" prop="waterQuality" />
      <el-table-column label="系统状态" align="center" prop="systemState" />
      <el-table-column label="动保名称" align="center" prop="feedBrand" />
      <el-table-column label="前一天摄食量" align="center" prop="oneBeforeFeed" width="120" />
      <el-table-column label="前两天摄食量" align="center" prop="twoBeforeFeed" width="120" />
      <el-table-column label="前三天摄食量" align="center" prop="threeBeforeFeed" width="120" />
      <el-table-column label="用药方案" align="center" prop="status" width="120" show-overflow-tooltip>
        <template v-slot="scope">
          <span @click="toPage(scope.row)"
            style="cursor: pointer;color: #409EFF;">{{ scope.row.medicineName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="专家方案" align="center" prop="expertSolution" show-overflow-tooltip/>
      <el-table-column label="实际方案" align="center" prop="actualSolution" show-overflow-tooltip/>
      <el-table-column label="损失数量(尾)" align="center" prop="numberOfLosses" width="120" />
      <el-table-column label="规格(g/尾)" align="center" prop="specifications" width="120" />
      <el-table-column label="用药效果评估" align="center" prop="medicineAfter" width="120" />
      <el-table-column label="执行人" align="center" prop="userName" show-overflow-tooltip/>

      <!-- <el-table-column label="用药方案" align="center" prop="medicineName" @click="toPage" /> -->
      <!-- <el-table-column label="用法用量" align="center" prop="midicineDosage" /> -->
      <!-- <el-table-column label="症状" align="center" prop="controlTitle" /> -->
      <!-- <el-table-column label="状态" align="center" prop="status" /> -->
      <!-- <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.USE_MEDICINE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="附件" align="center" prop="fids" />
      <el-table-column label="流程实例" align="center" prop="processInstanceId" /> -->
      <!-- <el-table-column label="负责人" align="center" prop="userId">
        <template v-slot="scope">
          <userName :user-id="scope.row.userId"></userName>
        </template>
      </el-table-column> -->
      <!--
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleDetail(scope.row)">审批进度</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-use-medicine:update']">修正</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-use-medicine:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修正) -->
    <el-dialog :title="title" :visible.sync="open" width="1400px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <!-- <el-form-item label="用药方案" prop="medicineName">
          <el-input v-model="form.medicineName" placeholder="请输入用药方案" />
        </el-form-item> -->
        <el-form-item label="所在存塘" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择所在存塘" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName})`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!--  format="yyyy-MM-dd HH:mm:ss" -->
        <el-form-item label="起始时间(用药)" prop="createTime">
          <el-date-picker
            style="width:100%"
            v-model="form.createTime"
            type="datetime"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="症状(文字)" prop="controlContent">
          <!-- <editor v-model="form.controlContent" :min-height="192"/> -->
          <el-input type="textarea" :autosize="autosizeObj" v-model="form.controlContent" placeholder="请输入症状(文字)" />
        </el-form-item>
        <el-form-item label="症状(图像)" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_use_medicine_img" @sendCurrFiles="receiveFiles_img"></FileBusUpload>
        </el-form-item>
        <el-form-item label="症状(视频)" prop="fid">
          <FileBusUpload ref="uploadRef" :fid="form.fid" bus-type="zk_use_medicine_video" :fileType="fileType" @sendCurrFiles="receiveFiles_video"></FileBusUpload>
        </el-form-item>
        <el-form-item label="用药方案" prop="medicineName">
          <!-- <editor v-model="form.medicineName" :min-height="192"/> -->
          <el-input type="textarea" :autosize="autosizeObj" v-model="form.medicineName" placeholder="请输入用药方案" />
        </el-form-item>
        <el-form-item label="专家方案">
          <el-input type="textarea" :autosize="autosizeObj" v-model="form.expertSolution" placeholder="请输入专家方案" />
        </el-form-item>
        <el-form-item label="实际方案">
          <el-input type="textarea" :autosize="autosizeObj" v-model="form.actualSolution" placeholder="请输入实际方案" />
        </el-form-item>
        <!-- <el-form-item label="用法用量" prop="midicineDosage">
          <el-input v-model="form.midicineDosage" placeholder="请输入用法用量" />
        </el-form-item> -->
        <el-form-item label="停药时间" prop="stopMedicationTime">
          <el-date-picker
            style="width:100%"
            v-model="form.stopMedicationTime"
            type="datetime"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="损失数量(尾)" prop="numberOfLosses">
          <el-input v-model="form.numberOfLosses" placeholder="请输入损失数量(尾)" />
        </el-form-item>
        <el-form-item label="规格(g/尾)" prop="specifications">
          <el-input v-model="form.specifications" placeholder="请输入规格(g/尾)" />
        </el-form-item>
        <!-- <el-form-item label="发病原因分析">
          <editor v-model="form.controlContent" :min-height="192"/>
        </el-form-item> -->
        <el-form-item label="用药效果评估" prop="medicineAfter">
          <el-input v-model="form.medicineAfter" placeholder="请输入用药效果评估" />
        </el-form-item>
        <el-form-item label="用药明细">
          <el-button type="primary" size="mini" @click="addMedicineLog">添加明细</el-button>
          <el-table :data="form.useMedicineLogList" style="width: 100%; margin-top: 10px;" border>
            <el-table-column label="所在存塘" width="200">
              <template v-slot="scope">
                <el-select v-model="scope.row.pondIdArr" multiple filterable clearable placeholder="请选择存塘">
                  <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName})`" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="对应用药" width="180">
              <template v-slot="scope">
                <el-select v-model="scope.row.productNo" filterable clearable placeholder="请选择用药" @change="updateLogProductId(scope.row)">
                  <el-option v-for="(item, index) in productList" :key="item.id+'__'+index" :label="`${item.info}`" :value="item.productNo"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="用药用量" width="150">
              <template v-slot="scope">
                <el-input v-model="scope.row.midicineDosage" oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''"
                   placeholder="用药用量" @input="calcMedicineCost(scope.row)"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="用药结果" width="150">
              <template v-slot="scope">
                <el-input v-model="scope.row.medicineAfter" placeholder="用药结果"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="用药时间" width="200">
              <template v-slot="scope">
                <!--  value-format="yyyy-MM-dd HH:mm:ss" -->
                <el-date-picker v-model="scope.row.feedTime" type="datetime" value-format="timestamp" placeholder="选择时间" style="width: 100%;"></el-date-picker>
              </template>
            </el-table-column>
            <el-table-column label="备注" width="150">
              <template v-slot="scope">
                <el-input v-model="scope.row.remark" placeholder="备注"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template v-slot="scope">
                <el-button size="mini" type="text" icon="el-icon-delete" @click="removeMedicineLog(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="执行人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择执行人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.USE_MEDICINE_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="附件" prop="fids">
          <el-input v-model="form.fids" placeholder="请输入附件" />
        </el-form-item>
        <el-form-item label="流程实例" prop="processInstanceId">
          <el-input v-model="form.processInstanceId" placeholder="请输入流程实例" />
        </el-form-item>
        <el-form-item label="用药人" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用药人" />
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
    <el-dialog :title="title" :visible.sync="open_video" width="800px" v-dialogDrag append-to-body>
      <video
          style="width: 100%;height: 100%;"
          :src="open_url" controls muted autoplay>
          <p>你的浏览器不支持 HTML5 视频，请下载<a href="example.mp4">视频文件</a>。</p>
      </video>
    </el-dialog>
  </div>
</template>

<script>
import { createZkUseMedicine, updateZkUseMedicine, deleteZkUseMedicine, getZkUseMedicine, getZkUseMedicinePage, exportZkUseMedicineExcel } from "@/api/fish/zkUseMedicine";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import {getTaskListByProcessInstanceId} from "@/api/bpm/task";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadDeptScopedPondList } from "@/utils/siteOptions";
import { notifyProductionRecordChanged } from "@/utils/productionRecordBridge";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import ImagePreview from "@/components/ImagePreview";
import { UPLOAD_VIDEO_TYPE } from "@/utils/constants";
import { getProductPage } from "@/api/erp/product";
import { createZkWorkshop, updateZkWorkshop, deleteZkWorkshop, getZkWorkshop, getZkWorkshopPage, exportZkWorkshopExcel } from "@/api/fish/zkWorkshop";


export default {
  name: "ZkUseMedicine",
  components: {
    Editor, FileBusUpload, ImagePreview,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      autosizeObj: {minRows:4},
      operationLoading: false,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用药记录列表
      list: [],
      pondList: [],
      productList: [],
      userList1: [],
      workshopList: [],
      systemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      open_video: false,
      open_url: '',

      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        medicineName: null,
        midicineDosage: null,
        controlTitle: null,
        controlContent: null,
        medicineAfter: null,
        status: null,
        fids: null,
        processInstanceId: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      taskParam: {//流程参数
        approveUserId: '',
        processKey: 'apply_for_medication',
        approveNo: '',
        businessKey: '',
        taskId: '',
        processInstanceId: '',
        finishProcess: 'n',
        approveMessage: '',
        variables: {},
        submitToPrev: 'y'//退回后的流程再次提交直接到上一次的退回人那里
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        // fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
        pondId: [{ required: true, message: "存塘不能为空", trigger: "blur" }],
        createTime: [{ required: true, message: "起始时间不能为空", trigger: "blur" }],
        controlContent: [{ required: true, message: "症状不能为空", trigger: "blur" }],
        medicineName: [{ required: true, message: "用药方案不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "执行人不能为空", trigger: "blur" }],
        stopMedicationTime: [{ required: true, message: "停药时间不能为空", trigger: "blur" }],
      },
      fileObjects_img: [],
      fileObjects_video: [],
      fileType: UPLOAD_VIDEO_TYPE,
    };
  },
  created() {
    this.getList();
    this.flushChoose();
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
    openVideo(videoUrl) {
      this.open_url = videoUrl;
      this.open_video = true;
      // console.log(`=============>${videoUrl}`);
    },
    receiveFiles_img(param) {
      // console.log(`==============>param:${JSON.stringify(param)}`)
      this.fileObjects_img.push(param);
      this.form.symptomUrls = this.fileObjects_img.map(o => o.fileUrl).join(",");
    },
    receiveFiles_video(param) {
      // console.log(`==============>param:${JSON.stringify(param)}`)
      this.fileObjects_video.push(param);
      this.form.symptomVideos = this.fileObjects_video.map(o => o.fileUrl).join(",");
    },
    toPage(currItem) {
      console.log(`============>currItem:${JSON.stringify(currItem)}`);
      this.$router.push({
          path: '/jidi/zk-use-medicine-log',
          // path: '/fish/zk-paper-question',
          params: {},
          query: { zkUseMedicineId: currItem.id },
      });
    },
    flushChoose() {
      this.getPondList();
      this.getUserList();
      this.getProductList();
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
        })
      })
    },
    addMedicineLog() {
      if (!this.form.useMedicineLogList) {
        this.$set(this.form, 'useMedicineLogList', []);
      }
      let now = new Date();
      let year = now.getFullYear();
      let month = String(now.getMonth() + 1).padStart(2, '0');
      let day = String(now.getDate()).padStart(2, '0');
      let hours = String(now.getHours()).padStart(2, '0');
      let minutes = String(now.getMinutes()).padStart(2, '0');
      let seconds = String(now.getSeconds()).padStart(2, '0');
      let timeStr = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      this.form.useMedicineLogList.push({
        pondIdArr: [],
        productNo: undefined,
        midicineDosage: undefined,
        medicineAfter: undefined,
        feedTime: timeStr,
        remark: undefined,
      });
    },
    removeMedicineLog(index) {
      this.form.useMedicineLogList.splice(index, 1);
    },
    updateLogProductId(row) {
      let item = null;
      this.productList.forEach(obj => {
        if(obj.productNo == row.productNo) {
          item = obj;
          return;
        }
      })
      if (item) {
        row.medicineName = item.productName;
        row.medicineBrand = item.productBrand;
        row.medicineSpec = item.productSpec;
        row.medicinePrice = item.purchasePrice;
        this.calcMedicineCost(row);
      }
    },
    calcMedicineCost(row) {
      if (row.midicineDosage && row.medicinePrice) {
        row.medicineCost = this.my_common.computer(row.midicineDosage, row.medicinePrice, 2, 4);
      }
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkUseMedicinePage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
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
      this.form.deptId = item.deptId;
      this.form.tenantId = item.tenantId;
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
      loadDeptScopedPondList().then(list => {
        this.pondList = Array.isArray(list) ? list.slice() : []
        this.getWorkshopList();
      }).catch(() => {
        this.pondList = []
      })
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
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        medicineName: undefined,
        midicineDosage: undefined,
        controlTitle: undefined,
        controlContent: undefined,
        medicineAfter: undefined,
        status: "0", // 0:申请中;
        fids: undefined,
        processInstanceId: undefined,
        userId: undefined,
        deptId: undefined,
        fid: undefined,
        createTime: new Date().getTime(),
        stopMedicationTime: new Date().getTime(),
        useMedicineLogList: [],
      };
      this.resetForm("form");
    },
    handleDetail(row) {
      this.$router.push({ name: "BpmProcessInstanceDetail", query: { id: row.processInstanceId}});
      return;
      const processInstanceId = row.processInstanceId;
      getTaskListByProcessInstanceId(processInstanceId).then(response => {
        if(response.data && response.data.length > 0){
          // this.$router.push({ path: "/workflow/process-instance/detail", query: {task: encodeURIComponent(JSON.stringify(response.data[0]))}});
          this.$router.push({ path: "/workflow/process-instance/detail?taskId=" + response.data[0].taskId, query: {}});
        }
      });
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
      this.open = true;
      this.title = "添加用药记录";
      this.form.fid = getUUID().replaceAll('-', '');
      this.$set(this.form, 'useMedicineLogList', []);
    },
    /** 修正按钮操作 */
    handleUpdate(row) {
      this.flushChoose();
      this.reset();
      const id = row.id;
      getZkUseMedicine(id).then(response => {
        this.form = {...response.data, createTimeStr: new Date(), stopMedicationTimeStr: new Date()};
        if(this.form.createTime) this.form.createTimeStr = new Date(this.form.createTime);
        if(this.form.stopMedicationTime) this.form.stopMedicationTimeStr = new Date(this.form.stopMedicationTime);
        if (!this.form.useMedicineLogList) {
          this.$set(this.form, 'useMedicineLogList', []);
        }
        this.open = true;
        this.title = "修正用药记录";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }

        // Fetch logs
        import("@/api/fish/zkUseMedicineLog").then(api => {
          api.getZkUseMedicineLogPage({ zkUseMedicineId: id, pageNo: 1, pageSize: 500 }).then(res => {
            console.log(`getZkUseMedicineLogPage========================>`, res);
            if (res.data && res.data.list) {
              res.data.list.forEach(log => {
                if (log.pondIdArr && typeof log.pondIdArr === 'string') {
                  log.pondIdArr = log.pondIdArr.split(',').map(Number);
                }
              });
              this.$set(this.form, 'useMedicineLogList', res.data.list);
            }
          });
        });
      });
    },
    /** 提交按钮 */
    submitForm() {
      // console.log(`================>submitForm 1`);
      this.$refs["form"].validate(valid => {
        // console.log(`================>submitForm 2`);
        if (!valid) {
          return;
        }

        let submitData = JSON.parse(JSON.stringify(this.form));
        if(submitData.createTimeStr) {
          submitData.createTime = new Date(submitData.createTimeStr).getTime();
        }
        if(submitData.stopMedicationTimeStr) {
          submitData.stopMedicationTime = new Date(submitData.stopMedicationTimeStr).getTime();
        }
        submitData.taskParam = this.taskParam;

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
        // 修正的提交
        if (submitData.id != null) {
          updateZkUseMedicine(submitData).then(response => {
            this.$modal.msgSuccess("修正成功");
            this.open = false;
            this.getList();
            setTimeout(()=>{
              this.operationLoading = false;
            }, 1000);
            loading.close();
          }).catch(error => {
            // console.log(`=============>`, error);
            this.operationLoading = false;
            loading.close();
          });
          return;
        }
        // 添加的提交
        createZkUseMedicine(submitData).then(response => {
          // console.log(`================>submitForm 3`, response);
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
          notifyProductionRecordChanged({ type: 'medicine', pondId: submitData.pondId, source: 'zkUseMedicine' });
          setTimeout(()=>{
            this.operationLoading = false;
          }, 1000);
          loading.close();
        }).catch(error => {
          // console.log(`=============>`, error);
          this.operationLoading = false;
          loading.close();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除用药记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkUseMedicine(id);
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
      this.$modal.confirm('是否确认导出所有用药记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkUseMedicineExcel(params);
        }).then(response => {
          this.$download.excel(response, '用药记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
<style lang="scss" scoped>
.videoShowDiv {
  height: 60px;
  width: 60px;
  border: 1px solid #409EFF;
  color: #409EFF;
  border-radius: 7px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
</style>
