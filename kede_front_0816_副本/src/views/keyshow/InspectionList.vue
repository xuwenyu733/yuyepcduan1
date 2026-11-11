<template>
  <div class="app-container">
    <div class="list-filter-panel">
      <!-- 搜索工作栏 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
        <el-form-item label="巡检类型" prop="checkName">
          <el-select style="width:100%;" v-model="queryParams.checkName" placeholder="请选择巡检类型" filterable clearable  @change="handleQuery">
            <el-option v-for="(item, index) in checkNameList" :key="item.id+'__'+index" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车间" prop="filterWorkshop">
          <el-select v-model="filterWorkshop" placeholder="全部车间" clearable filterable @change="applyClientFilter">
            <el-option v-for="w in workshopOptions" :key="w" :label="w" :value="w" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统" prop="filterSystem">
          <el-select v-model="filterSystem" placeholder="全部系统" clearable filterable @change="applyClientFilter">
            <el-option v-for="s in systemOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="池号" prop="filterPond">
          <el-select v-model="filterPond" placeholder="全部池号" clearable filterable @change="applyClientFilter">
            <el-option v-for="p in pondOptions" :key="p" :label="p" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务" prop="taskId">
          <el-input v-model="queryParams.taskId" placeholder="请输入任务" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                          range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
        </el-form-item>
        <el-form-item class="search-btns">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
        <right-toolbar class="list-filter-tools" :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-form>

      <!-- 操作工具栏 -->
      <div class="list-action-bar mb8">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:task-detail:create']">新增</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:task-detail:export']">导出</el-button>
        <el-button type="success" plain icon="el-icon-document" size="mini" @click="$router.push('/keyshow/inspectionreport')">智能分析报告</el-button>
        <el-button type="info" plain icon="el-icon-first-aid-kit" size="mini" @click="$router.push({ path: '/fish-disease', query: { from: '/keyshow/InspectionList' } })">鱼病诊断</el-button>
      </div>
    </div>

    <el-row :gutter="12" class="mb8 stat-row">
      <el-col :span="6"><el-card shadow="hover" class="stat-card stat-card--blue"><div class="stat-label">总记录</div><div class="stat-value">{{ displayList.length }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="stat-card stat-card--green"><div class="stat-label">车间筛选</div><div class="stat-value">{{ filterWorkshop || '全部' }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="stat-card stat-card--orange"><div class="stat-label">系统筛选</div><div class="stat-value">{{ filterSystem || '全部' }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="stat-card stat-card--purple"><div class="stat-label">池号筛选</div><div class="stat-value">{{ filterPond || '全部' }}</div></el-card></el-col>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="displayList">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="任务id" align="center" prop="taskId" /> -->
      <el-table-column label="巡检时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="检测名称" align="center" prop="checkName" />
      <!-- <el-table-column label="检测类型" align="center" prop="checkType" /> -->
      <el-table-column label="检测内容" align="center" min-width="500">
        <template v-slot="scope">
          <div v-if="isJson(scope.row.checkContent)">
            <el-table :data="filterJson(scope.row.checkContent)" size="small" :show-header="false" class="inner-table">
              <el-table-column prop="key" label="键" width="150" align="right" header-align="right">
                <template v-slot="{ row }">
                  <span class="json-key">{{ row.value.typeName }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="value" label="值" align="left" header-align="left">
                <template v-slot="{ row }">
                  <div>
                    <el-table :data="filterDetailList(row.value.detailList)" size="mini" :show-header="false" class="inner-table">
                      <el-table-column prop="key" label="键" width="120" align="right" header-align="right">
                        <template v-slot="{ row: detailRow }">
                          <span class="json-key">{{ detailRow.value.detailTypeName }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="value" label="值" align="left" header-align="left">
                        <template v-slot="{ row: detailRow }">
                          <div v-if="detailRow.value.detailTypeName == '打样'">
                            <el-table :data="threeLevel2List(detailRow.value)" size="mini" :show-header="false" class="inner-table">
                              <el-table-column prop="tableIndex" label="键" width="120" align="right" header-align="right">
                                <template v-slot="{ row: threeRow }">
                                  <span class="json-key">{{ threeRow.tableIndex }}</span>
                                </template>
                              </el-table-column>
                              <el-table-column prop="tableBodyLength" label="键" align="right" header-align="right">
                                <template v-slot="{ row: threeRow }">
                                  <span class="json-key">{{ threeRow.tableBodyLength }}</span>
                                </template>
                              </el-table-column>
                              <el-table-column prop="tableBodyWeight" label="键" align="right" header-align="right">
                                <template v-slot="{ row: threeRow }">
                                  <span class="json-key">{{ threeRow.tableBodyWeight }}</span>
                                </template>
                              </el-table-column>
                            </el-table>
                          </div>
                          <div v-if="detailRow.value.detailTypeName != '打样'">
                            <el-table :data="threeLevel2List(detailRow.value)" size="mini" :show-header="false" class="inner-table">
                              <el-table-column prop="key_ch" label="键" width="120" align="right" header-align="right">
                                <template v-slot="{ row: threeRow }">
                                  <span class="json-key">{{ threeRow.key_ch }}</span>
                                </template>
                              </el-table-column>
                              <el-table-column prop="key_value" label="键" align="right" header-align="right">
                                <template v-slot="{ row: threeRow }">
                                  <div>
                                    <el-table :data="threeRow.key_value" size="mini" :show-header="false" class="inner-table">
                                      <el-table-column prop="key" label="键" width="200" align="right" header-align="right">
                                        <template v-slot="{ row: propertyRow }">
                                          <span class="json-key">{{ propertyRow.key_ch }}:</span>
                                        </template>
                                      </el-table-column>
                                      <el-table-column prop="key" label="键" align="right" header-align="right">
                                        <template v-slot="{ row: propertyRow }">
                                          <div class="key_value_is_video" v-if="propertyRow.key_en.indexOf('_fileUrl')!=-1">
                                            <video 
                                              style="width: 50px;height: 50px;"
                                              :src="propertyRow.key_value" type="video/mp4" controls muted autoplay loop>
                                            </video>
                                          </div>
                                          <span class="json-key" v-else>{{ propertyRow.key_value }}</span>
                                        </template>
                                      </el-table-column>
                                    </el-table>
                                  </div>
                                </template>
                              </el-table-column>
                            </el-table>
                          </div>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-else>{{ scope.row.checkContent }}</div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="检测反馈" align="center" prop="checkRes" /> -->
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:task-detail:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:task-detail:delete']">删除</el-button>
        </template>
      </el-table-column> -->
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="任务id(zk_task)" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入任务id(zk_task)" />
        </el-form-item>
        <el-form-item label="检测名称" prop="checkName">
          <el-input v-model="form.checkName" placeholder="请输入检测名称" />
        </el-form-item>
        <el-form-item label="检测类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)" prop="checkType">
          <el-select v-model="form.checkType" placeholder="请选择检测类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="检测config">
          <editor v-model="form.checkContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="检测res" prop="checkRes">
          <el-input v-model="form.checkRes" placeholder="请输入检测res" />
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
import { createTaskDetail, updateTaskDetail, deleteTaskDetail, getTaskDetail, getTaskDetailPage, exportTaskDetailExcel } from "@/api/fish/taskDetail";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
// 导入任务JSON字典
import * as taskJsonDict from "@/utils/taskJsonDict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadSharedSiteOptions, pickWorkshopOptionLabels, pickSystemOptionLabels, pickPondOptionLabels, rowMatchesSiteFilters, workshopsEqualLoose, systemsEqualLoose } from '@/utils/siteOptions';

export default {
  name: "TaskDetail",
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
      // 任务明细列表
      list: [],
      rawList: [],
      filterWorkshop: '',
      filterSystem: '',
      filterPond: '',
      siteOptions: null,
      pondList: [],
      productList: [],
      bachchNumberList: [],
      userList1: [],
      dict_flushTypes: [], 
      checkNameList: [
        {label: '定期巡检', value: '定期'},
        {label: '日巡检', value: '日巡检'},
      ],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        taskId: null,
        checkName: null,
        checkType: null,
        checkContent: null,
        checkRes: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        taskId: [{ required: true, message: "任务id(zk_task)不能为空", trigger: "blur" }],
      }
    };
  },
  computed: {
    workshopOptions() {
      return pickWorkshopOptionLabels(this.siteOptions)
    },
    systemOptions() {
      return pickSystemOptionLabels(this.siteOptions, this.filterWorkshop)
    },
    pondOptions() {
      return pickPondOptionLabels(this.siteOptions, this.filterWorkshop, this.filterSystem)
    },
    displayList() {
      return this.list.filter(row => {
        const loc = this.extractLocation(row)
        return rowMatchesSiteFilters(
          {
            workshopName: loc.workshop,
            pondSystemName: loc.system,
            pondDetailNum: loc.pond,
            pondName: loc.pond
          },
          {
            workshop: this.filterWorkshop,
            system: this.filterSystem,
            pond: this.filterPond
          },
          this.siteOptions
        )
      })
    }
  },
  created() {
    this.getList();
    this.getPondList();
    this.loadSiteOptions();
  },
  methods: {
    async loadSiteOptions() {
      try {
        this.siteOptions = await loadSharedSiteOptions({ force: false })
      } catch (e) {
        console.warn('[InspectionList] site options', e)
      }
    },
    extractLocation(row) {
      const loc = { workshop: '', system: '', pond: '' }
      if (!row || !row.checkContent) return loc
      if (typeof row.checkContent === 'string') {
        try {
          const data = JSON.parse(row.checkContent)
          Object.values(data || {}).forEach(block => {
            if (block && typeof block === 'object') {
              if (block.workshopName) loc.workshop = block.workshopName
              if (block.pondSystemName) loc.system = block.pondSystemName
              if (block.pondDetailNum) loc.pond = block.pondDetailNum
              else if (block.pondName) loc.pond = block.pondName
            }
          })
        } catch (e) {
          const text = row.checkContent
          if (this.filterWorkshop && text.indexOf(this.filterWorkshop) >= 0) loc.workshop = this.filterWorkshop
          if (this.filterSystem && text.indexOf(this.filterSystem) >= 0) loc.system = this.filterSystem
          if (this.filterPond && text.indexOf(this.filterPond) >= 0) loc.pond = this.filterPond
        }
      }
      if (!loc.workshop || !loc.system || !loc.pond) {
        const pond = this.pondList.find(p => String(p.id) === String(row.taskId))
        if (pond) {
          if (!loc.workshop) loc.workshop = pond.workshopName || ''
          if (!loc.system) loc.system = pond.pondSystemName || ''
          if (!loc.pond) loc.pond = pond.pondDetailNum || pond.pondName || ''
        }
      }
      return loc
    },
    applyClientFilter() {
      if (this.filterSystem && !this.systemOptions.includes(this.filterSystem)) {
        this.filterSystem = ''
      }
      if (this.filterPond && !this.pondOptions.includes(this.filterPond)) {
        this.filterPond = ''
      }
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
      // this.form.userName = item.userName;
      // this.form.userId = item.userId;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;

      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      return getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
        // console.log(`========================>${JSON.stringify(this.pondList)}`);
      })
    },
    /** 巡检list 第三级，巡检项的指标，数据解析方便展示 */
    threeLevel2List(obj) {
      // console.log(`threeLevel2List=========>${JSON.stringify(obj)}`);
      let res = [];
      if(obj.detailTypeName == '打样') {
        res = this.threeLevel2List_type1(obj);
      } else {
        res = this.threeLevel2List_normal(obj);
      }
      // console.log(`threeLevel2List=========>${JSON.stringify(res)}`);
      return res;
    },
    threeLevel2List_type1(obj) { // 表格类型
      // console.log(`threeLevel2List=========>${JSON.stringify(obj)}`);
      let that = this;
      // let obj = JSON.parse(str);
      // { "tableIndex": 3, "tableBodyLength": "", "tableBodyWeight": "" }
      let head = {
        tableIndex: '序号',
        tableBodyLength: '体长cm',
        tableBodyWeight: '体重g'
      };
      let res = [head, ...obj.tableValue];
      res = res.filter(item => {
        if(item.tableBodyLength && item.tableBodyWeight) return true;
        return false
      })
      return res;
    },
    threeLevel2List_normal(obj) { // 正常
      let that = this;
      // let obj = JSON.parse(str);
      let res = [];
      // 一、 获取 检查项
      Object.keys(obj).forEach(key => {
        // 1. 白名单 跳过
        let isWhite = that.judgeIsShowThree(key);
        if(isWhite) return;
        // 2. 获取 key 名称
        let key_ch = taskJsonDict.DICT_TYPE[key];
        let temp = {key_en: "", key_ch: "", key_value: []};
        if(key_ch) {
          temp = {...temp, key_en: key, key_ch};
          res.push(temp);
        }
      })
      // 二、 检查项 填充列表
      res.forEach(item => {
        let currKey = item.key_en;
        let key_value = [];
        Object.keys(obj).forEach(key => {
          // 1. 白名单 跳过
          let isWhite = that.judgeIsShowThree(key);
          if(isWhite) return;
          // 2. 不是检查项 跳过
          if(key.indexOf(currKey) == -1) return;
          // 3. 加值
          if(key.indexOf("_score") != -1 && key == `${currKey}_score`) {
              let key_value_temp = {key_en: key, key_ch: "评分", key_value: obj[key]};
              key_value.push(key_value_temp);
          }
          if(key.indexOf("_content") != -1 && key == `${currKey}_content`) {
              let key_value_temp = {key_en: key, key_ch: "描述", key_value: obj[key]};
              key_value.push(key_value_temp);
          }
          if(key.indexOf("_fileUrl") != -1 && key == `${currKey}_fileUrl`) {
              let key_value_temp = {key_en: key, key_ch: "拍摄视频", key_value: obj[key]}; // , key_fid: obj[`${currKey}_fid`]
              key_value.push(key_value_temp);
          }
        })
        item.key_value = JSON.parse(JSON.stringify(key_value));
      })
      // console.log(`============>res:${JSON.stringify(res)}`);
      return res;
    },

    /** 巡检list 第三级，巡检项的指标，判断是否展示 */
    judgeIsShowThree(key) {
      let temp = taskJsonDict.DICT_TYPE_WHITE[key];
      return temp;
    },

    /** 根据key获取字典中的值，如果不存在则返回原始key */
    getDictValue(key) {
      return taskJsonDict.DICT_TYPE[key] || key;
    },

    /** 判断是否为JSON字符串 */
    isJson(str) {
      try {
        JSON.parse(str);
        return true;
      } catch (e) {
        return false;
      }
    },

    /** 过滤JSON数据，移除_fid结尾的键值对 */
    filterJson(jsonStr) {
      try {
        const data = JSON.parse(jsonStr);
        let res = Object.entries(data)
          .filter(([key]) => !key.endsWith('_fid'))
          .filter(([key, value]) => {
            if(value.typeValue == 199) { // 提示不展示
              return false;
            }
            // if(value.typeName == '其他') return false;
            // console.log(`=============>key:${key},value:${JSON.stringify(value)}`);
            return true;
          })
          .map(([key, value]) => ({ key, value }));
        // console.log(`========>${JSON.stringify(res)}`);
        return res;
      } catch (e) {
        return [];
      }
    },

    /** 过滤detailList数据，移除_fid结尾的键值对 */
    filterDetailList(detailList) {
      if (!detailList) {
        return [];
      }
      // 如果是字符串，尝试解析为JSON
      if (typeof detailList === 'string') {
        try {
          detailList = JSON.parse(detailList);
        } catch (e) {
          return [];
        }
      }
      // 确保是对象类型
      if (typeof detailList !== 'object' || detailList === null) {
        return [];
      }
      let res = Object.entries(detailList)
        .filter(([key]) => !key.endsWith('_fid'))
        .filter(([key, value]) => {
          // console.log(`=============>key:${key},value:${JSON.stringify(value)}`);
          return true;
        })
        .map(([key, value]) => ({ key, value }));
      // console.log(`level two ========>${JSON.stringify(res)}`);
      return res;
    },

    /** 递归过滤JSON数据，移除包含_fid的键值对 */
    filterNestedJson(data) {
      if (!data) {
        return [];
      }
      // 如果是字符串，尝试解析为JSON
      if (typeof data === 'string') {
        try {
          data = JSON.parse(data);
        } catch (e) {
          return [{ key: 'value', value: data }];
        }
      }
      let res = [];
      // 如果是对象类型
      if (typeof data === 'object' && data !== null) {
        // 如果是数组
        if (Array.isArray(data)) {
          return data.map((item, index) => ({
            key: index.toString(),
            value: this.isJson(JSON.stringify(item)) ? this.filterNestedJson(item) : item
          }));
        } else {
          // 如果是对象
          return Object.entries(data)
            .filter(([key]) => !key.includes('_fid'))
            .map(([key, value]) => ({
              key: key,
              value: this.isJson(JSON.stringify(value)) ? this.filterNestedJson(value) : value
            }));
        }
      }
      // let res = [{ key: 'value', value: data }];
      // console.log(`========>${JSON.stringify(res)}`);
      // 如果是基本类型
      return [];
    },

    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getTaskDetailPage(this.queryParams).then(response => {
        this.rawList = response.data.list || [];
        this.list = this.rawList;
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
        taskId: undefined,
        checkName: undefined,
        checkType: undefined,
        checkContent: undefined,
        checkRes: undefined,
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
      this.filterWorkshop = '';
      this.filterSystem = '';
      this.filterPond = '';
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加任务明细";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getTaskDetail(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改任务明细";
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
          updateTaskDetail(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createTaskDetail(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除任务明细编号为"' + id + '"的数据项?').then(function() {
          return deleteTaskDetail(id);
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
      this.$modal.confirm('是否确认导出所有任务明细数据项?').then(() => {
          this.exportLoading = true;
          return exportTaskDetailExcel(params);
        }).then(response => {
          this.$download.excel(response, '任务明细.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.stat-row {
  margin-bottom: 12px;
}
.stat-card {
  border-radius: 10px;
  border: 1px solid #fff;
  color: #fff;
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
}
.stat-card .stat-label {
  font-size: 13px;
  color: #fff;
  margin-bottom: 6px;
}
.stat-card .stat-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.2;
}
.stat-card--blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.stat-card--blue .stat-value {
  color: #ffd93d;
}
.stat-card--green {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}
.stat-card--green .stat-value {
  color: #ff6b6b;
}
.stat-card--orange {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}
.stat-card--orange .stat-value {
  color: #48dbfb;
}
.stat-card--purple {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}
.stat-card--purple .stat-value {
  color: #feca57;
}
.inner-table {
  border-radius: 4px;
  overflow: hidden;
  margin: 8px 0;
  border-top: 1px solid;
  border-left: 1px solid;
  cursor: pointer;
}
.inner-table:hover {
  background-color: #d9ebff !important;
}
.json-item {
  display: flex;
  margin-bottom: 4px;
  line-height: 1.5;
}
.json-key {
  font-weight: 600;
  margin-right: 8px;
  color: #409EFF;
}
.json-value {
  color: #606266;
}
</style>
