<template>
  <div class="app-container">
    <div class="list-action-bar mb8" style="margin-bottom:12px">
      <el-button type="primary" size="small" icon="el-icon-back" @click="$router.push('/keyshow/InspectionList')">巡检列表</el-button>
      <el-button type="success" size="small" icon="el-icon-first-aid-kit" @click="$router.push('/fish-disease')">鱼病诊断</el-button>
      <el-button type="info" size="small" icon="el-icon-view" @click="$router.push('/index_daping2')">视频监控</el-button>
    </div>

    <div class="list-filter-panel">
      <!-- 搜索工作栏 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
        <el-form-item label="车间名称" prop="workshopName">
          <el-input v-model="queryParams.workshopName" placeholder="请输入车间名称" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="存塘系统名称" prop="pondSystemName">
          <el-input v-model="queryParams.pondSystemName" placeholder="请输入存塘系统名称" clearable @keyup.enter.native="handleQuery"/>
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
                   v-hasPermi="['pl:zk-inspection-report:create']">新增</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-inspection-report:export']">导出</el-button>
      </div>
    </div>
    
    <!-- 卡片式展示 -->
    <div v-if="loading" class="loading-container">
      <i class="el-icon-loading loading-icon"></i>
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
              <div v-if="false" class="report-item-box">
                <div class="report-item-header">
                  <h3 class="report-label">智能体报告</h3>
                  <div class="right" v-if="item.pdfUrl && item.pdfUrl!='0'">
                    <span @click.stop="openPdf(`https://www.kdaqua.cn/server/admin-api/infra/preview?fileUrl=${item.pdfUrl}`)">预览</span>
                    <span @click.stop="openPdf(`${item.pdfUrl}`)">下载</span>
                  </div>
                </div>
                <!-- <p class="report-content">{{ item.aiAgentReport || '暂无数据' }}</p> -->
                <div class="report-content rich-text-content" v-html="formatRichText(item.aiAgentReport) || '暂无数据'"></div>
              </div>
              <div class="report-item-box">
                <h3 class="report-label">人工反馈</h3>
                <p class="report-content">{{ item.humanReport || '暂无反馈' }}</p>
              </div>
              <div v-if="false" class="report-item-box">
                <h3 class="report-label">差异分析</h3>
                <p class="report-content">{{ item.differentialAnalysis || '暂无分析' }}</p>
              </div>
            </div>
            <div class="report-footer">
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
      <div class="no-data-text">暂无巡检报告数据</div>
      <el-button type="primary" size="small" @click="handleAdd">立即创建</el-button>
    </div>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="toPage" :page-sizes="[3, 6, 9, 12]" class="report-pagination" />

    <!-- 列表 -->
    <!-- <el-table v-loading="loading" :data="list"> -->
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="所属车间id" align="center" prop="workshopId" /> -->
      <!-- <el-table-column label="车间名称" align="center" prop="workshopName" /> -->
      <!-- <el-table-column label="所属系统id" align="center" prop="pondSystemId" /> -->
      <!-- <el-table-column label="存塘系统编号" align="center" prop="pondNum" /> -->
      <!-- <el-table-column label="存塘系统名称" align="center" prop="pondSystemName" /> -->
      <!-- <el-table-column label="所属存塘id" align="center" prop="pondId" />
      <el-table-column label="存塘编号" align="center" prop="pondDetailNum" /> -->
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" />
      <el-table-column label="智能体报告" align="center" prop="aiAgentReport" />
      <el-table-column label="人工报告" align="center" prop="humanReport" />
      <el-table-column label="对比分析" align="center" prop="differentialAnalysis" /> -->
      <!-- <el-table-column label="整体结论" align="center" prop="conclusion" />
      <el-table-column label="整体评估" align="center" prop="evaluation" /> -->
      <!-- <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="所属部门" align="center" prop="deptId" />
      <el-table-column label="文件id" align="center" prop="fid" /> -->
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-inspection-report:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-inspection-report:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table> -->
    <!-- 分页组件
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/> -->

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
        <el-form-item v-if="false" label="智能体报告" prop="aiAgentReport">
          <el-input v-model="form.aiAgentReport" placeholder="请输入智能体报告" />
        </el-form-item>
        <el-form-item label="人工报告是否同意不同意写原因分析" prop="humanReport">
          <el-input v-model="form.humanReport" placeholder="请输入人工报告是否同意不同意写原因分析" />
        </el-form-item>
        <el-form-item v-if="false" label="对比分析人工和智能体报告" prop="differentialAnalysis">
          <el-input v-model="form.differentialAnalysis" placeholder="请输入对比分析人工和智能体报告" />
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkInspectionReport, updateZkInspectionReport, deleteZkInspectionReport, getZkInspectionReport, getZkInspectionReportPage, exportZkInspectionReportExcel } from "@/api/fish/zkInspectionReport";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";

export default {
  name: "ZkInspectionReport",
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
      // 巡检智能分析报告列表
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
    console.log('组件已创建，开始加载数据');
  },
  methods: {
    openPdf(pdfUrl) {
      window.open(pdfUrl, '_blank');
    },
    toPage(e) {
      this.queryParams.pageNo = e.page;
      this.queryParams.pageSize = e.limit;
      this.getList();
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      console.log('开始请求数据', this.queryParams);
      // 执行查询
      getZkInspectionReportPage(this.queryParams).then(response => {
        console.log('完整响应数据:', response);
        this.list = response.data?.list || response.list || [];
        this.total = response.data?.total || response.total || 0;
        console.log(`getZkInspectionReportPage total==============>${this.total}`);
        this.loading = false;
      }).catch(error => {
        console.error('数据请求失败', error);
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
      this.title = "添加巡检智能分析报告";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkInspectionReport(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改巡检智能分析报告";
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
          updateZkInspectionReport(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkInspectionReport(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除巡检智能分析报告编号为"' + id + '"的数据项?').then(function() {
          return deleteZkInspectionReport(id);
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
      this.$modal.confirm('是否确认导出所有巡检智能分析报告数据项?').then(() => {
          this.exportLoading = true;
          return exportZkInspectionReportExcel(params);
        }).then(response => {
          this.$download.excel(response, '巡检智能分析报告.xls');
          this.exportLoading = false;
        }).catch(() => {});
    },
    formatRichText(content) {
      if (!content) return '';
      let formattedContent = content;
      // 转换类似 markdown 的粗体语法为 HTML
      // let formattedContent = content.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
      
      // 转换 markdown 链接为 HTML 链接
      // formattedContent = formattedContent.replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" target="_blank">$1</a>');
      
      // 保留换行符
      // formattedContent = formattedContent.replace(/\n/g, '<br>');
      // formattedContent = formattedContent.replace(/\n/g, '');

      let arr = formattedContent.split("{");
      if(arr && arr.length>1) {
        formattedContent = "{"+arr[1]
        // console.log(`formattedContent============>`+formattedContent);
        let obj = JSON.parse(formattedContent);
        let values = [];
        Object.keys(obj).forEach(key => {
          values.push(obj[key]);
				})
        return values.join("   <br><br><br>");
      }
      
      return formattedContent;
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
.report-item-box {
  width: 100%;
  border: 1px solid rgba(64, 158, 255, 0.25);
  border-radius: 6px;
  padding: 15px;
  padding-top: 1px;
  background: linear-gradient(135deg, 
    rgba(10, 21, 32, 0.8) 0%, 
    rgba(15, 26, 38, 0.85) 100%);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(64, 158, 255, 0.1) inset;
  display: flex;
  flex-direction: column;
  .report-item-header {
    width: 100%;
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid rgba(64, 158, 255, 0.2);
    padding: 10px 0;
    h3 {
      border-bottom: none;
      font-weight: 600;
      color: #ffffff;
    }
    .right {
      display: flex;
      margin-top: -1px;
      span {
        display: flex;
        align-items: center;
        margin-right: 15px;
        font-size: 14px;
        cursor: pointer;
        transition: all 0.3s;
        &:hover {
          opacity: 0.8;
          transform: translateY(-1px);
        }
      }
      span:nth-child(1) {
        color: #64C8FF;
        &:hover {
          color: #90D5FF;
        }
      }
      span:nth-child(2) {
        color: #67C23A;
        &:hover {
          color: #85CE61;
        }
      }
    }
  }
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
  min-height: 100px;
}
.loading-icon {
  font-size: 32px;
  color: #409eff;
  animation: loading-rotate 1.5s linear infinite;
}
@keyframes loading-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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
.rich-text-content {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.6;
  flex-grow: 1;
  word-break: break-word;
  max-height: 150px;
  overflow: auto;
  white-space: normal;
  
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

.rich-text-content :deep(strong) {
  font-weight: bold;
  color: #ffffff;
}

.rich-text-content :deep(a) {
  color: #64C8FF;
  text-decoration: underline;
  transition: color 0.3s;
  &:hover {
    color: #90D5FF;
  }
}

.rich-text-content :deep(a:hover) {
  color: #90D5FF;
}

.rich-text-content :deep(br) {
  line-height: 1.2;
}

/* 筛选区样式统一走全局 list-filter.scss（正式版青绿/金铜） */
/* 分页样式统一走全局 element-ui.scss（正式版薄荷绿选中） */
</style>
