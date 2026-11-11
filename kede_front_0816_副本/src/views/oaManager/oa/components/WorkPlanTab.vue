<template>
  <div class="workplan-tab">
    <!-- 头部区域 -->
    <div class="workplan-header">
      <div class="header-title">
        <h2><i class="el-icon-notebook-2"></i> 工作计划管理</h2>
        <p>创建、提交和管理您的工作计划</p>
      </div>
      <el-button type="primary" class="create-plan-btn" icon="el-icon-plus" @click="showCreateDialog">
        创建新计划
      </el-button>
    </div>

    <!-- 工作计划列表 -->
    <div class="workplan-list-container">
      <div class="list-header">
        <h3><i class="el-icon-s-order"></i> 工作计划列表</h3>
      </div>
      <div v-loading="loading">
        <el-table 
          :data="workPlanList" 
          style="width: 100%"
          stripe>
          <el-table-column prop="title" label="计划标题" min-width="200">
            <template slot-scope="scope">
              <i class="el-icon-document" style="margin-right: 8px; color: #54907a;"></i>
              <span>{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" min-width="120" align="center">
            <template slot-scope="scope">
              <el-tag size="small" effect="plain" type="info">{{ getTypeLabel(scope.row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="period" label="周期" min-width="160" align="center">
            <template slot-scope="scope">
              {{ formatPeriod(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="创建人" min-width="100" align="center">
            <template slot-scope="scope">
              {{ scope.row.userName || '--' }}
            </template>
          </el-table-column>
          <el-table-column prop="submitDate" label="提交日期" min-width="120" align="center">
            <template slot-scope="scope">
              {{ formatDate(scope.row.submitDate || scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="附件" min-width="100" align="center">
            <template slot-scope="scope">
              <span v-if="getAttachmentsList(scope.row.attachments).length > 0" style="color: #54907a;">
                <i class="el-icon-paperclip"></i> {{ getAttachmentsList(scope.row.attachments).length }}个
              </span>
              <span v-else style="color: rgba(255, 255, 255, 0.5);">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="completionRate" label="完成度" min-width="100" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.completionRate !== null && scope.row.completionRate !== undefined"
                    style="color: #67C23A; font-weight: 500;">
                {{ scope.row.completionRate }}%
              </span>
              <span v-else style="color: rgba(255, 255, 255, 0.5);">--</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="280" align="center" fixed="right">
            <template slot-scope="scope">
              <div class="action-btns">
                <el-button
                  class="act-btn act-view"
                  size="mini"
                  icon="el-icon-view"
                  @click="handleViewPlan(scope.row)">
                  查看
                </el-button>
                <el-button
                  v-if="canEditPlan(scope.row)"
                  class="act-btn act-edit"
                  size="mini"
                  icon="el-icon-edit"
                  @click="handleEditPlan(scope.row)">
                  修改
                </el-button>
                <el-button
                  v-if="canDeletePlan(scope.row)"
                  class="act-btn act-delete"
                  size="mini"
                  icon="el-icon-delete"
                  @click="handleDeletePlan(scope.row)">
                  删除
                </el-button>
                <el-button
                  v-if="canReviewPlan(scope.row)"
                  class="act-btn act-review"
                  size="mini"
                  icon="el-icon-check"
                  @click="handleReviewPlan(scope.row)">
                  审核
                </el-button>
                <el-button
                  v-if="canCompletePlan(scope.row)"
                  class="act-btn act-complete"
                  size="mini"
                  icon="el-icon-circle-check"
                  @click="handleCompletePlan(scope.row)">
                  完成情况
                </el-button>
                <el-button
                  v-if="canEvaluatePlan(scope.row)"
                  class="act-btn act-evaluate"
                  size="mini"
                  icon="el-icon-star-on"
                  @click="handleEvaluatePlan(scope.row)">
                  评价打分
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container" style="margin-top: 20px; text-align: right;">
          <el-pagination
            @size-change="handlePageSizeChange"
            @current-change="handlePageChange"
            :current-page="queryParams.pageNo"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="queryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="workplan-statistics-section">
      <div class="stat-card">
        <div class="stat-icon" style="background: #54907a;">
          <i class="el-icon-calendar"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.thisMonthCount || 0 }}个</div>
          <div class="stat-label">已完成: {{ statistics.thisMonthCompleted || 0 }}个</div>
        </div>
        <div class="stat-title">本月计划</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #67C23A;">
          <i class="el-icon-success"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value" style="color: #67C23A;">{{ statistics.avgCompletionRate || 0 }}%</div>
          <div class="stat-label">本月统计</div>
        </div>
        <div class="stat-title">平均完成度</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #E6A23C;">
          <i class="el-icon-star-on"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.avgQualityScore || 0 }}</div>
          <div class="stat-label">满分5分</div>
        </div>
        <div class="stat-title">平均质量评分</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #F56C6C;">
          <i class="el-icon-warning"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.pendingReview || 0 }}个</div>
          <div class="stat-label">需及时关注</div>
        </div>
        <div class="stat-title">待审核计划</div>
      </div>
    </div>

    <!-- 创建/编辑工作计划对话框 -->
    <el-dialog 
      :title="isEditMode ? '编辑工作计划' : '创建工作计划'" 
      :visible.sync="createDialogVisible"
      width="800px"
      :close-on-click-modal="false"
      @close="resetCreateForm">
      <div class="dialog-subtitle">填写工作计划详情并提交审核</div>
      
      <el-form 
        ref="createForm" 
        :model="createForm" 
        :rules="createRules" 
        label-width="120px"
        label-position="left">
        <el-form-item label="计划类型" prop="type">
          <el-select 
            v-model="createForm.type" 
            placeholder="选择计划类型"
            style="width: 100%;"
            @change="handleTypeChange">
            <el-option value="weekly" label="周计划">
              <span><i class="el-icon-date" style="margin-right:6px"></i>周计划</span>
            </el-option>
            <el-option value="monthly" label="月计划">
              <span><i class="el-icon-s-grid" style="margin-right:6px"></i>月计划</span>
            </el-option>
            <el-option value="quarterly" label="季度计划">
              <span><i class="el-icon-s-data" style="margin-right:6px"></i>季度计划</span>
            </el-option>
            <el-option value="annual" label="年度计划">
              <span><i class="el-icon-trophy" style="margin-right:6px"></i>年度计划</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="计划标题" prop="title">
          <el-input 
            v-model="createForm.title" 
            placeholder="请输入计划标题"
            maxlength="100"
            show-word-limit>
          </el-input>
        </el-form-item>

        <el-form-item label="计划周期" prop="period">
          <el-date-picker
            v-model="periodRange"
            type="daterange"
            value-format="yyyy-MM-dd"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
            @change="handlePeriodRangeChange">
          </el-date-picker>
        </el-form-item>

        <!-- 周计划：每日工作内容 -->
        <template v-if="createForm.type === 'weekly'">
          <el-form-item 
            v-for="day in weekDays" 
            :key="day.value"
            :label="day.label" 
            :prop="`dailyContent.${day.value}`">
            <el-input 
              v-model="createForm.dailyContent[day.value]"
              type="textarea" 
              :rows="3"
              :placeholder="`请输入${day.label}的工作内容`">
            </el-input>
          </el-form-item>
        </template>

        <!-- 月计划：每月工作内容 -->
        <el-form-item v-if="createForm.type === 'monthly'" label="工作内容" prop="content">
          <el-input 
            v-model="createForm.content"
            type="textarea" 
            :rows="6"
            placeholder="请输入本月的工作内容">
          </el-input>
        </el-form-item>

        <!-- 季度计划：每季度工作内容 -->
        <el-form-item v-if="createForm.type === 'quarterly'" label="工作内容" prop="content">
          <el-input 
            v-model="createForm.content"
            type="textarea" 
            :rows="6"
            placeholder="请输入本季度的工作内容">
          </el-input>
        </el-form-item>

        <!-- 年度计划：每年工作内容 -->
        <el-form-item v-if="createForm.type === 'annual'" label="工作内容" prop="content">
          <el-input 
            v-model="createForm.content"
            type="textarea" 
            :rows="6"
            placeholder="请输入本年度的工作内容">
          </el-input>
        </el-form-item>

        <!-- 附件上传 -->
        <el-form-item label="附件上传">
          <el-upload
            ref="upload"
            class="workplan-uploader"
            drag
            :action="uploadAction"
            :headers="uploadHeaders"
            :data="uploadData"
            :before-upload="beforeUpload"
            :on-change="handleFileChange"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :file-list="uploadFileList"
            :limit="5"
            :auto-upload="false"
            :on-remove="handleFileRemove">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">点击或拖拽文件到此处上传</div>
            <div class="el-upload__tip" slot="tip">
              支持 DOC, DOCX, PDF, XLS, XLSX 格式, 最大 50MB
            </div>
          </el-upload>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button 
          v-if="!isEditMode"
          type="primary" 
          :loading="submitting"
          @click="handleSubmitReview">
          保存
        </el-button>
        <el-button 
          v-if="isEditMode"
          type="primary" 
          :loading="submitting"
          @click="handleUpdatePlan">
          保存修改
        </el-button>
      </div>
    </el-dialog>

    <!-- 查看工作计划对话框 -->
    <el-dialog 
      title="查看工作计划" 
      :visible.sync="viewDialogVisible"
      width="800px"
      :close-on-click-modal="false">
      <div v-if="currentPlan" class="plan-detail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <div class="section-header">
            <i class="el-icon-info"></i>
            <span>基本信息</span>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">计划标题</div>
              <div class="info-value">{{ currentPlan.title }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">计划类型</div>
              <div class="info-value">
                <el-tag :type="getTypeTagType(currentPlan.type)" size="small">
                  {{ getTypeLabel(currentPlan.type) }}
                </el-tag>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">计划周期</div>
              <div class="info-value">{{ formatPeriod(currentPlan) }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">状态</div>
              <div class="info-value">
                <el-tag :type="getStatusTagType(currentPlan.status)" size="small">
                  {{ getStatusLabel(currentPlan.status) }}
                </el-tag>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">创建人</div>
              <div class="info-value">{{ currentPlan.userName || '--' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">提交日期</div>
              <div class="info-value">{{ formatDate(currentPlan.submitDate || currentPlan.createTime) }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">完成度</div>
              <div class="info-value">
                <span v-if="currentPlan.completionRate !== null && currentPlan.completionRate !== undefined" 
                      style="color: #67C23A; font-weight: 600;">
                  {{ currentPlan.completionRate }}%
                </span>
                <span v-else style="color: rgba(255, 255, 255, 0.4);">--</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 审核信息 -->
        <div v-if="currentPlan.reviewerName" class="detail-section">
          <div class="section-header">
            <i class="el-icon-circle-check"></i>
            <span>审核信息</span>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">审核结果</div>
              <div class="info-value">
                <el-tag :type="currentPlan.reviewComment && currentPlan.reviewComment.includes('拒绝') ? 'danger' : 'success'" size="small">
                  {{ currentPlan.reviewComment && currentPlan.reviewComment.includes('拒绝') ? '已拒绝' : '已通过' }}
                </el-tag>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">审核人</div>
              <div class="info-value">{{ currentPlan.reviewerName }}</div>
            </div>
            <div class="info-item info-item-full">
              <div class="info-label">审核时间</div>
              <div class="info-value">{{ formatDate(currentPlan.reviewTime) }}</div>
            </div>
            <div class="info-item info-item-full">
              <div class="info-label">审核意见</div>
              <div class="info-value info-value-text">{{ currentPlan.reviewComment || '--' }}</div>
            </div>
          </div>
        </div>
        
        <!-- 完成情况信息 -->
        <div v-if="currentPlan.completionStatus" class="detail-section">
          <div class="section-header">
            <i class="el-icon-finished"></i>
            <span>完成情况</span>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">完成情况</div>
              <div class="info-value">
                <el-tag :type="currentPlan.completionStatus === 'completed' ? 'success' : 'warning'" size="small">
                  {{ currentPlan.completionStatus === 'completed' ? '已完成' : '未完成' }}
                </el-tag>
              </div>
            </div>
            <div class="info-item info-item-full">
              <div class="info-label">完成说明</div>
              <div class="info-value info-value-text">{{ currentPlan.completionComment || '--' }}</div>
            </div>
          </div>
        </div>
        
        <!-- 评价信息 -->
        <div v-if="currentPlan.evaluatorName" class="detail-section">
          <div class="section-header">
            <i class="el-icon-star-off"></i>
            <span>评价信息</span>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">评价人</div>
              <div class="info-value">{{ currentPlan.evaluatorName }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">评价时间</div>
              <div class="info-value">{{ formatDate(currentPlan.evaluationTime) }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">评价打分</div>
              <div class="info-value">
                <span style="color: #E6A23C; font-weight: 600; font-size: 16px;">
                  {{ currentPlan.evaluationScore !== null && currentPlan.evaluationScore !== undefined ? currentPlan.evaluationScore + '分' : '--' }}
                </span>
              </div>
            </div>
            <div class="info-item info-item-full">
              <div class="info-label">完成质量评价</div>
              <div class="info-value info-value-text">{{ currentPlan.qualityEvaluation || '--' }}</div>
            </div>
            <div class="info-item info-item-full">
              <div class="info-label">成本与效率评价</div>
              <div class="info-value info-value-text">{{ currentPlan.costEfficiencyEvaluation || '--' }}</div>
            </div>
          </div>
        </div>
        
        <!-- 工作内容 -->
        <div class="detail-section">
          <div class="section-header">
            <i class="el-icon-document"></i>
            <span>工作内容</span>
          </div>
          <div class="content-text" v-html="formatPlanContent(currentPlan)"></div>
        </div>
        
        <!-- 附件列表 -->
        <div class="detail-section">
          <div class="section-header">
            <i class="el-icon-paperclip"></i>
            <span>附件</span>
          </div>
          <div v-if="getAttachmentsList(currentPlan.attachments).length > 0" class="attachment-list">
            <div v-for="(file, index) in getAttachmentsList(currentPlan.attachments)" :key="index" class="attachment-item">
              <el-link :href="file.url" target="_blank" type="primary" icon="el-icon-document">
                {{ file.name || '未知文件' }}
              </el-link>
            </div>
          </div>
          <div v-else class="empty-text">暂无附件</div>
        </div>
      </div>
    </el-dialog>

    <!-- 审核工作计划对话框 -->
    <el-dialog 
      title="审核工作计划" 
      custom-class="review-work-plan-dialog"
      :visible.sync="reviewDialogVisible"
      width="640px"
      :close-on-click-modal="false"
      @close="reviewForm = { reviewResult: '', reviewComment: '' }">
      <div v-if="currentPlan" class="review-dialog">
        <!-- 计划概要 -->
        <div class="detail-section">
          <div class="section-header">
            <i class="el-icon-document"></i>
            <span>计划概要</span>
          </div>
          <div class="info-grid">
            <div class="info-item info-item-full">
              <div class="info-label">计划标题</div>
              <div class="info-value">{{ currentPlan.title }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">计划类型</div>
              <div class="info-value">
                <el-tag :type="getTypeTagType(currentPlan.type)" size="small">
                  {{ getTypeLabel(currentPlan.type) }}
                </el-tag>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">计划周期</div>
              <div class="info-value">{{ formatPeriod(currentPlan) }}</div>
            </div>
            <div class="info-item info-item-full">
              <div class="info-label">提交日期</div>
              <div class="info-value">{{ formatDate(currentPlan.submitDate || currentPlan.createTime) }}</div>
            </div>
            <div class="info-item info-item-full">
              <div class="info-label">工作内容</div>
              <div class="info-value info-value-text review-plan-content" v-html="formatPlanContent(currentPlan)"></div>
            </div>
          </div>
        </div>
        
        <!-- 审核表单 -->
        <div class="detail-section">
          <div class="section-header">
            <i class="el-icon-edit-outline"></i>
            <span>审核操作</span>
          </div>
          <div class="review-form-wrap">
            <el-form :model="reviewForm" label-width="100px">
              <el-form-item label="审核结果" required>
                <el-radio-group v-model="reviewForm.reviewResult">
                  <el-radio label="approved">
                    <span style="color: #67C23A;">通过</span>
                  </el-radio>
                  <el-radio label="rejected">
                    <span style="color: #F56C6C;">拒绝</span>
                  </el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="审核意见">
                <el-input 
                  v-model="reviewForm.reviewComment"
                  type="textarea" 
                  :rows="4"
                  placeholder="请输入审核意见（可选）">
                </el-input>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :disabled="!reviewForm.reviewResult"
          @click="handleSubmitReviewDialog">
          提交审核
        </el-button>
      </div>
    </el-dialog>

    <!-- 完成情况对话框 -->
    <el-dialog 
      title="完成情况" 
      :visible.sync="completeDialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="completeForm = { completionStatus: '', completionComment: '' }">
      <div v-if="currentPlan" class="complete-dialog">
        <el-descriptions :column="1" border style="margin-bottom: 20px;">
          <el-descriptions-item label="计划标题">{{ currentPlan.title }}</el-descriptions-item>
          <el-descriptions-item label="计划类型">
            <el-tag :type="getTypeTagType(currentPlan.type)" size="small">
              {{ getTypeLabel(currentPlan.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="计划周期">{{ formatPeriod(currentPlan) }}</el-descriptions-item>
        </el-descriptions>
        
        <el-form :model="completeForm" :rules="completeRules" ref="completeForm" label-width="120px">
          <el-form-item label="完成情况" prop="completionStatus" required>
            <el-radio-group v-model="completeForm.completionStatus">
              <el-radio label="completed">已完成</el-radio>
              <el-radio label="uncompleted">未完成</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="完成说明" prop="completionComment">
            <el-input 
              v-model="completeForm.completionComment"
              type="textarea" 
              :rows="4"
              placeholder="请输入完成说明（可选）"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="submittingComplete"
          :disabled="!completeForm.completionStatus"
          @click="handleSubmitComplete">
          保存
        </el-button>
      </div>
    </el-dialog>

    <!-- 评价打分对话框 -->
    <el-dialog 
      title="评价打分" 
      :visible.sync="evaluationDialogVisible"
      width="700px"
      :close-on-click-modal="false"
      @close="evaluationForm = { id: null, qualityEvaluation: '', costEfficiencyEvaluation: '', evaluationScore: null }">
      <div v-if="currentPlan" class="evaluation-dialog">
        <el-descriptions :column="1" border style="margin-bottom: 20px;">
          <el-descriptions-item label="计划标题">{{ currentPlan.title }}</el-descriptions-item>
          <el-descriptions-item label="计划类型">
            <el-tag :type="getTypeTagType(currentPlan.type)" size="small">
              {{ getTypeLabel(currentPlan.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="计划周期">{{ formatPeriod(currentPlan) }}</el-descriptions-item>
          <el-descriptions-item label="完成情况">
            <el-tag :type="currentPlan.completionStatus === 'completed' ? 'success' : 'warning'" size="small">
              {{ currentPlan.completionStatus === 'completed' ? '已完成' : '未完成' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="完成说明">
            <div style="white-space: pre-wrap;">{{ currentPlan.completionComment || '--' }}</div>
          </el-descriptions-item>
        </el-descriptions>
        
        <el-form :model="evaluationForm" :rules="evaluationRules" ref="evaluationForm" label-width="140px">
          <el-form-item label="完成质量评价" prop="qualityEvaluation">
            <el-input 
              v-model="evaluationForm.qualityEvaluation"
              type="textarea" 
              :rows="4"
              placeholder="请输入完成质量评价（可选）"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
          <el-form-item label="成本与效率评价" prop="costEfficiencyEvaluation">
            <el-input 
              v-model="evaluationForm.costEfficiencyEvaluation"
              type="textarea" 
              :rows="4"
              placeholder="请输入成本与效率评价（可选）"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
          <el-form-item label="评价打分" prop="evaluationScore" required>
            <el-input-number 
              v-model="evaluationForm.evaluationScore"
              :min="0"
              :max="25"
              :precision="0"
              placeholder="请输入0-25分的评价"
              style="width: 100%;">
            </el-input-number>
            <div style="color: rgba(255, 255, 255, 0.6); font-size: 12px; margin-top: 5px;">
              评分范围：0-25分
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="evaluationDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="submittingEvaluation"
          :disabled="evaluationForm.evaluationScore === null || evaluationForm.evaluationScore === undefined"
          @click="handleSubmitEvaluation">
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getOaWorkPlanPage, 
  createOaWorkPlan, 
  updateOaWorkPlan,
  deleteOaWorkPlan,
  getOaWorkPlan,
  reviewOaWorkPlan,
  getOaWorkPlanStatistics,
  completeOaWorkPlan,
  evaluateOaWorkPlan
} from '@/api/fish/zkWorkPlan'
import { getAccessToken } from '@/utils/auth'
import { getInfo } from '@/api/login'
import { getDept } from '@/api/system/dept'
import { workPlanTypeIcon } from '@/utils/workPlanTypeMeta'

export default {
  name: 'WorkPlanTab',
  data() {
    return {
      loading: false,
      workPlanList: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10
      },
      statistics: {
        thisMonthCount: 0,
        thisMonthCompleted: 0,
        avgCompletionRate: 0,
        avgQualityScore: 0,
        pendingReview: 0
      },
      // 创建对话框
      createDialogVisible: false,
      submitting: false,
      createForm: {
        type: 'weekly',
        title: '',
        period: '',
        content: '',
        dailyContent: {
          monday: '',
          tuesday: '',
          wednesday: '',
          thursday: '',
          friday: '',
          saturday: '',
          sunday: ''
        },
        attachments: []
      },
      periodRange: null,
      createRules: {
        type: [
          { required: true, message: '请选择计划类型', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请输入计划标题', trigger: 'blur' }
        ],
        period: [
          { required: true, message: '请选择计划周期', trigger: 'change' }
        ]
      },
      uploadFileList: [],
      /** 附件在校验阶段被拒（格式/大小），阻止误保存不含合规附件的计划 */
      attachmentUploadRejected: false,
      uploadAction: process.env.VUE_APP_BASE_API + '/admin-api/infra/file/upload',
      uploadData: {},
      // 查看对话框
      viewDialogVisible: false,
      currentPlan: null,
      // 编辑模式
      isEditMode: false,
      // 审核对话框
      reviewDialogVisible: false,
      reviewForm: {
        reviewResult: '',
        reviewComment: ''
      },
      // 完成情况对话框
      completeDialogVisible: false,
      submittingComplete: false,
      completeForm: {
        id: null,
        completionStatus: '',
        completionComment: ''
      },
      completeRules: {
        completionStatus: [
          { required: true, message: '请选择完成情况', trigger: 'change' }
        ]
      },
      // 评价对话框
      evaluationDialogVisible: false,
      submittingEvaluation: false,
      evaluationForm: {
        id: null,
        qualityEvaluation: '',
        costEfficiencyEvaluation: '',
        evaluationScore: null
      },
      evaluationRules: {
        evaluationScore: [
          { required: true, message: '请输入评价打分', trigger: 'blur' },
          { type: 'number', min: 0, max: 25, message: '评价打分必须在0-25分之间', trigger: 'blur' }
        ]
      },
      // 用户角色信息
      userRoles: [],
      userDeptId: null,
      isDeptHead: false,
      /** 与后端分页「超管看全量」一致，避免列表仍带 userId 导致与统计卡片不一致 */
      isSuperAdmin: false,
      currentUserId: null,
      // 周计划的天数
      weekDays: [
        { label: '星期一', value: 'monday' },
        { label: '星期二', value: 'tuesday' },
        { label: '星期三', value: 'wednesday' },
        { label: '星期四', value: 'thursday' },
        { label: '星期五', value: 'friday' },
        { label: '星期六', value: 'saturday' },
        { label: '星期日', value: 'sunday' }
      ]
    }
  },
  computed: {
    uploadHeaders() {
      const token = getAccessToken()
      return {
        Authorization: token ? 'Bearer ' + token : ''
      }
    }
  },
  async mounted() {
    // 先加载用户信息，再获取工作计划列表
    await this.loadUserInfo()
    this.getWorkPlanList()
    this.getStatistics()
  },
  methods: {
    // 获取工作计划列表
    async getWorkPlanList() {
      this.loading = true
      try {
        // 构建查询参数
        const params = { ...this.queryParams }
        
        // 获取当前用户ID
        const currentUserId = this.currentUserId || this.$store.getters.userId || this.$store.getters.userInfo?.id

        // 超级管理员：不传 userId/deptId，与 getOaWorkPlanStatistics 全量口径一致（后端亦会清空误传参数）
        if (this.isSuperAdmin) {
          delete params.userId
          delete params.deptId
        } else if (!this.isDeptHead) {
          params.userId = currentUserId
        } else if (this.userDeptId) {
          params.deptId = this.userDeptId
        }
        
        const res = await getOaWorkPlanPage(params)
        if (res.code === 200 || res.code === 0) {
          this.workPlanList = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg || '获取工作计划列表失败')
        }
      } catch (error) {
        this.$message.error('获取工作计划列表失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },
    
    // 获取统计数据
    async getStatistics() {
      try {
        const res = await getOaWorkPlanStatistics()
        if (res.code === 200 || res.code === 0) {
          this.statistics = res.data || {
            thisMonthCount: 0,
            thisMonthCompleted: 0,
            avgCompletionRate: 0,
            avgQualityScore: 0,
            pendingReview: 0
          }
        }
      } catch (error) {
        // 静默处理
      }
    },
    
    // 显示创建对话框
    showCreateDialog() {
      this.resetCreateForm()
      this.isEditMode = false
      this.createDialogVisible = true
    },

    /** 当前登录用户 ID（统一为数字，避免 string/number 比较失败） */
    resolveCurrentUserId() {
      const raw =
        this.currentUserId ||
        this.$store.getters.userId ||
        (this.$store.getters.userInfo && this.$store.getters.userInfo.id)
      const n = Number(raw)
      return Number.isFinite(n) && n > 0 ? n : null
    },

    isPlanOwner(plan) {
      const uid = this.resolveCurrentUserId()
      if (uid == null || plan == null || plan.userId == null) return false
      return Number(plan.userId) === uid
    },
    
    // 重置创建表单
    resetCreateForm() {
      this.$refs.createForm && this.$refs.createForm.resetFields()
      this.createForm = {
        id: null,
        type: 'weekly',
        title: '',
        period: '',
        content: '',
        dailyContent: {
          monday: '',
          tuesday: '',
          wednesday: '',
          thursday: '',
          friday: '',
          saturday: '',
          sunday: ''
        },
        attachments: []
      }
      this.periodRange = null
      this.uploadFileList = []
      this.attachmentUploadRejected = false
      this.submitting = false
      this.isEditMode = false
    },
    
    // 计划类型改变
    handleTypeChange() {
      this.periodRange = null
      this.createForm.period = ''
    },

    handlePeriodRangeChange(val) {
      if (Array.isArray(val) && val.length === 2 && val[0] && val[1]) {
        this.createForm.period = `${val[0]} 至 ${val[1]}`
      } else {
        this.createForm.period = ''
      }
    },

    parsePeriodToRange(period) {
      if (!period || typeof period !== 'string') return null
      const m = period.match(/(\d{4}-\d{2}-\d{2})\s*(?:至|-|~|—)\s*(\d{4}-\d{2}-\d{2})/)
      if (m) return [m[1], m[2]]
      return null
    },
    
    // 上传前验证
    beforeUpload(file) {
      const isValidType = ['doc', 'docx', 'pdf', 'xls', 'xlsx'].includes(
        file.name.split('.').pop().toLowerCase()
      )
      const isLt50M = file.size / 1024 / 1024 < 50

      if (!isValidType) {
        this.attachmentUploadRejected = true
        this.$message.error('不支持的文件格式！')
        return false
      }
      if (!isLt50M) {
        this.attachmentUploadRejected = true
        this.$message.error('文件大小不能超过 50MB！')
        return false
      }
      this.attachmentUploadRejected = false
      return true
    },
    
    // 文件列表变化
    handleFileChange(file, fileList) {
      this.uploadFileList = fileList
    },
    
    // 上传成功
    handleUploadSuccess(response, file, fileList) {
      // 处理不同的响应格式
      let fileUrl = ''
      let fileId = null
      
      // 尝试多种方式获取文件URL
      if (response) {
        if (response.code === 200 || response.code === 0) {
          if (response.data) {
            if (typeof response.data === 'string') {
              fileUrl = response.data
            } else if (response.data.url) {
              fileUrl = response.data.url
              fileId = response.data.id || response.data.fileId
            } else if (response.data.path) {
              fileUrl = response.data.path
              fileId = response.data.id || response.data.fileId
            } else if (response.data.id) {
              fileId = response.data.id
            }
          }
        } else if (response.url) {
          fileUrl = response.url
          fileId = response.id
        } else if (response.path) {
          fileUrl = response.path
          fileId = response.id
        } else if (typeof response === 'string') {
          fileUrl = response
        }
      }
      
      // 如果还是没有URL，尝试从file对象中获取
      if (!fileUrl && file && file.response) {
        const fileResponse = file.response
        if (fileResponse.code === 200 || fileResponse.code === 0) {
          if (fileResponse.data) {
            if (typeof fileResponse.data === 'string') {
              fileUrl = fileResponse.data
            } else {
              fileUrl = fileResponse.data.url || fileResponse.data.path
              fileId = fileResponse.data.id || fileResponse.data.fileId
            }
          }
        } else if (fileResponse.url) {
          fileUrl = fileResponse.url
          fileId = fileResponse.id
        }
      }
      
      if (fileUrl) {
        if (!this.createForm.attachments) {
          this.createForm.attachments = []
        }
        // 检查是否已存在，避免重复添加
        const exists = this.createForm.attachments.some(att => att.url === fileUrl)
        if (!exists) {
          this.createForm.attachments.push({
            url: fileUrl,
            id: fileId,
            name: file.name
          })
        }
        // 更新文件列表
        this.uploadFileList = fileList
        this.$message.success('文件上传成功')
      } else {
        this.$message.warning('文件上传成功，但无法获取文件URL')
      }
    },
    
    // 文件移除
    handleFileRemove(file, fileList) {
      this.uploadFileList = fileList
      // 从附件列表中移除
      if (file.response) {
        const response = file.response
        let fileUrl = ''
        if (response.code === 200 || response.code === 0) {
          if (response.data) {
            fileUrl = typeof response.data === 'string' ? response.data : (response.data.url || response.data.path)
          }
        } else if (response.url) {
          fileUrl = response.url
        }
        if (fileUrl && this.createForm.attachments) {
          this.createForm.attachments = this.createForm.attachments.filter(att => att.url !== fileUrl)
        }
      }
    },
    
    // 上传失败
    handleUploadError(err) {
      this.$message.error('文件上传失败，请重试')
    },
    
    // 提交审核（创建）
    async handleSubmitReview() {
      this.$refs.createForm.validate(async (valid) => {
        if (!valid) {
          return false
        }

        if (this.attachmentUploadRejected) {
          this.$message.warning('附件格式或大小不符合要求，请重新选择符合要求的附件后再保存')
          return false
        }
        
        // 如果uploadFileList为空，尝试从组件实例获取文件列表
        if (this.uploadFileList.length === 0 && this.$refs.upload) {
          const uploadFiles = this.$refs.upload.fileList || []
          if (uploadFiles.length > 0) {
            this.uploadFileList = uploadFiles
          }
        }
        
        // 确保文件已上传并提取附件信息
        if (this.uploadFileList.length > 0) {
          // 先检查是否有未上传的文件，如果有则先上传
          const unUploadedFiles = this.uploadFileList.filter(f => !f.response && f.status !== 'success' && f.status !== 'fail')
          if (unUploadedFiles.length > 0) {
            // 手动触发上传
            try {
              this.$refs.upload.submit()
              // 等待上传完成
              let waitCount = 0
              while (unUploadedFiles.some(f => !f.response && f.status !== 'success' && f.status !== 'fail') && waitCount < 50) {
                await new Promise(resolve => setTimeout(resolve, 200))
                waitCount++
              }
            } catch (error) {
              console.error('触发文件上传失败:', error)
            }
          }
          
          // 从文件列表中提取已上传的文件
          const attachments = []
          this.uploadFileList.forEach(file => {
            let fileUrl = ''
            let fileId = null
            const response = file.response || file
            
            if (response) {
              if (response.code === 200 || response.code === 0) {
                if (response.data) {
                  if (typeof response.data === 'string') {
                    fileUrl = response.data
                  } else {
                    fileUrl = response.data.url || response.data.path || response.data
                    fileId = response.data.id || response.data.fileId
                  }
                }
              } else if (response.url) {
                fileUrl = response.url
                fileId = response.id
              } else if (response.path) {
                fileUrl = response.path
                fileId = response.id
              } else if (typeof response === 'string') {
                fileUrl = response
              }
            }
            
            if (fileUrl) {
              attachments.push({
                url: fileUrl,
                id: fileId,
                name: file.name || '未知文件'
              })
            }
          })
          
          this.createForm.attachments = attachments
        }
        
        this.submitting = true
        try {
          const planData = {
            type: this.createForm.type,
            title: this.createForm.title,
            period: this.createForm.period,
            content: this.createForm.type === 'weekly' 
              ? JSON.stringify(this.createForm.dailyContent)
              : this.createForm.content,
            status: 'pending', // 待审核
            attachments: this.createForm.attachments && this.createForm.attachments.length > 0 ? JSON.stringify(this.createForm.attachments) : null
          }
          
          const res = await createOaWorkPlan(planData)
          if (res.code === 200 || res.code === 0) {
            this.$message.success('工作计划保存成功')
            this.createDialogVisible = false
            this.resetCreateForm()
            this.getWorkPlanList()
            this.getStatistics()
          } else {
            this.$message.error(res.msg || '保存失败')
          }
        } catch (error) {
          this.$message.error('保存失败，请稍后重试')
        } finally {
          this.submitting = false
        }
      })
    },
    
    // 更新计划
    async handleUpdatePlan() {
      this.$refs.createForm.validate(async (valid) => {
        if (!valid) {
          return false
        }

        if (this.attachmentUploadRejected) {
          this.$message.warning('附件格式或大小不符合要求，请重新选择符合要求的附件后再保存')
          return false
        }
        
        // 如果uploadFileList为空，尝试从组件实例获取文件列表
        if (this.uploadFileList.length === 0 && this.$refs.upload) {
          const uploadFiles = this.$refs.upload.fileList || []
          if (uploadFiles.length > 0) {
            this.uploadFileList = uploadFiles
          }
        }
        
        // 确保文件已上传并提取附件信息
        if (this.uploadFileList.length > 0) {
          // 先检查是否有未上传的文件，如果有则先上传
          const unUploadedFiles = this.uploadFileList.filter(f => !f.response && f.status !== 'success' && f.status !== 'fail')
          if (unUploadedFiles.length > 0) {
            // 手动触发上传
            try {
              this.$refs.upload.submit()
              // 等待上传完成
              let waitCount = 0
              while (unUploadedFiles.some(f => !f.response && f.status !== 'success' && f.status !== 'fail') && waitCount < 50) {
                await new Promise(resolve => setTimeout(resolve, 200))
                waitCount++
              }
            } catch (error) {
              console.error('触发文件上传失败:', error)
            }
          }
          
          // 从文件列表中提取已上传的文件
          const attachments = []
          this.uploadFileList.forEach(file => {
            let fileUrl = ''
            let fileId = null
            const response = file.response || file
            
            if (response) {
              if (response.code === 200 || response.code === 0) {
                if (response.data) {
                  if (typeof response.data === 'string') {
                    fileUrl = response.data
                  } else {
                    fileUrl = response.data.url || response.data.path || response.data
                    fileId = response.data.id || response.data.fileId
                  }
                }
              } else if (response.url) {
                fileUrl = response.url
                fileId = response.id
              } else if (response.path) {
                fileUrl = response.path
                fileId = response.id
              } else if (typeof response === 'string') {
                fileUrl = response
              }
            }
            
            if (fileUrl) {
              attachments.push({
                url: fileUrl,
                id: fileId,
                name: file.name || '未知文件'
              })
            }
          })
          
          this.createForm.attachments = attachments
        }
        
        this.submitting = true
        try {
          const planData = {
            id: this.createForm.id,
            type: this.createForm.type,
            title: this.createForm.title,
            period: this.createForm.period,
            content: this.createForm.type === 'weekly' 
              ? JSON.stringify(this.createForm.dailyContent)
              : this.createForm.content,
            attachments: this.createForm.attachments && this.createForm.attachments.length > 0 ? JSON.stringify(this.createForm.attachments) : null
          }
          
          const res = await updateOaWorkPlan(planData)
          if (res.code === 200 || res.code === 0) {
            this.$message.success('工作计划更新成功')
            this.createDialogVisible = false
            this.resetCreateForm()
            this.getWorkPlanList()
            this.getStatistics()
          } else {
            this.$message.error(res.msg || '更新失败')
          }
        } catch (error) {
          this.$message.error('更新失败，请稍后重试')
        } finally {
          this.submitting = false
        }
      })
    },
    
    // 查看计划
    async handleViewPlan(plan) {
      try {
        const res = await getOaWorkPlan(plan.id)
        if (res.code === 200 || res.code === 0) {
          this.currentPlan = res.data || plan
          this.viewDialogVisible = true
        } else {
          // 如果获取详情失败，使用列表中的数据
          this.currentPlan = plan
          this.viewDialogVisible = true
        }
      } catch (error) {
        // 如果获取详情失败，使用列表中的数据
        this.currentPlan = plan
        this.viewDialogVisible = true
      }
    },
    
    // 分页大小改变
    handlePageSizeChange(size) {
      this.queryParams.pageSize = size
      this.queryParams.pageNo = 1
      this.getWorkPlanList()
    },
    
    // 分页页码改变
    handlePageChange(page) {
      this.queryParams.pageNo = page
      this.getWorkPlanList()
    },
    
    // 格式化周期
    formatPeriod(plan) {
      if (plan.period) {
        return plan.period
      }
      // 如果没有周期字段，根据类型和日期生成
      const type = plan.type
      const date = new Date(plan.createTime || plan.submitDate)
      
      if (type === 'weekly' || type === '周计划') {
        // 计算周的开始和结束日期
        const day = date.getDay()
        const diff = date.getDate() - day + (day === 0 ? -6 : 1)
        const startDate = new Date(date.setDate(diff))
        const endDate = new Date(startDate)
        endDate.setDate(startDate.getDate() + 6)
        return `${this.formatDate(startDate)} 至 ${this.formatDate(endDate)}`
      } else if (type === 'monthly' || type === '月计划') {
        return `${date.getFullYear()}年${date.getMonth() + 1}月`
      } else if (type === 'quarterly' || type === '季度计划') {
        const quarter = Math.floor(date.getMonth() / 3) + 1
        return `${date.getFullYear()}年第${quarter}季度`
      } else if (type === 'annual' || type === '年度计划') {
        return `${date.getFullYear()}年`
      }
      return '--'
    },
    
    // 格式化计划内容
    formatPlanContent(plan) {
      if (!plan.content) return '暂无内容'
      
      try {
        // 如果是周计划，内容可能是 JSON 格式
        if (plan.type === 'weekly' || plan.type === '周计划') {
          const dailyContent = JSON.parse(plan.content)
          let html = ''
          const weekDays = [
            { label: '星期一', value: 'monday' },
            { label: '星期二', value: 'tuesday' },
            { label: '星期三', value: 'wednesday' },
            { label: '星期四', value: 'thursday' },
            { label: '星期五', value: 'friday' },
            { label: '星期六', value: 'saturday' },
            { label: '星期日', value: 'sunday' }
          ]
          weekDays.forEach(day => {
            if (dailyContent[day.value]) {
              html += `<div style="margin-bottom: 12px;"><strong>${day.label}：</strong>${dailyContent[day.value]}</div>`
            }
          })
          return html || '暂无内容'
        } else {
          // 其他类型直接显示内容
          return plan.content.replace(/\n/g, '<br>')
        }
      } catch (e) {
        // 如果解析失败，直接显示
        return plan.content.replace(/\n/g, '<br>')
      }
    },
    
    // 获取类型标签类型
    getTypeTagType(type) {
      const typeMap = {
        'weekly': 'info',
        '周计划': 'info',
        'monthly': 'success',
        '月计划': 'success',
        'quarterly': 'warning',
        '季度计划': 'warning',
        'annual': 'danger',
        '年度计划': 'danger'
      }
      return typeMap[type] || ''
    },
    
    // 获取类型标签文本
    getTypeLabel(type) {
      const typeMap = {
        'weekly': '周计划',
        'monthly': '月计划',
        'quarterly': '季度计划',
        'annual': '年度计划'
      }
      return typeMap[type] || type || '未知'
    },
    workPlanTypeIcon,
    
    // 获取状态标签类型
    getStatusTagType(status) {
      const statusMap = {
        'pending': 'warning',
        '待审核': 'warning',
        'pending_review': 'warning',
        'approved': 'success',
        '已批准': 'success',
        '已审核': 'success',
        'completed': 'success',
        '已完成': 'success',
        'draft': '',
        '草稿': '',
        'rejected': 'danger',
        '已拒绝': 'danger'
      }
      return statusMap[status] || ''
    },
    
    // 获取状态标签文本
    getStatusLabel(status) {
      const statusMap = {
        'pending': '待审核',
        'pending_review': '待审核',
        'approved': '已审核',
        '已批准': '已审核',
        'completed': '已完成',
        'rejected': '已拒绝'
      }
      return statusMap[status] || status || '未知'
    },
    
    // 获取附件列表
    getAttachmentsList(attachments) {
      if (!attachments) return []
      try {
        if (typeof attachments === 'string') {
          return JSON.parse(attachments)
        }
        return Array.isArray(attachments) ? attachments : []
      } catch (e) {
        return []
      }
    },
    
    // 格式化日期
    formatDate(date) {
      if (!date) return '--'
      
      if (typeof date === 'number') {
        let timestamp = date
        if (date < 10000000000) {
          timestamp = date * 1000
        }
        const dateObj = new Date(timestamp)
        if (!isNaN(dateObj.getTime())) {
          const year = dateObj.getFullYear()
          const month = String(dateObj.getMonth() + 1).padStart(2, '0')
          const day = String(dateObj.getDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }
      }
      
      if (Array.isArray(date) && date.length >= 3) {
        const year = String(date[0]).padStart(4, '0')
        const month = String(date[1]).padStart(2, '0')
        const day = String(date[2]).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      if (typeof date === 'string') {
        const datePart = date.split('T')[0]
        return datePart
      }
      
      if (date instanceof Date) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      return String(date)
    },
    
    // 判断是否可以编辑（创建者或超管；未完结即可改）
    canEditPlan(plan) {
      if (!plan) return false
      const status = String(plan.status || '').toLowerCase()
      const locked = ['completed', 'evaluated', '已完成', '已评价'].includes(status)
      if (locked) return false
      if (this.isSuperAdmin) return true
      return this.isPlanOwner(plan)
    },
    
    // 判断是否可以删除（创建者或超管）
    canDeletePlan(plan) {
      if (!plan) return false
      if (this.isSuperAdmin) return true
      return this.isPlanOwner(plan)
    },
    
    // 加载用户信息
    async loadUserInfo() {
      try {
        const res = await getInfo()
        if (res && res.data) {
          this.userRoles = res.data.roles || []
          this.syncSuperAdminFlag()
          this.userDeptId = res.data.user?.deptId || this.$store.getters.deptId
          this.currentUserId = res.data.user?.id || this.$store.getters.userId || this.$store.getters.userInfo?.id
          
          // 判断是否是部门负责人
          if (this.userDeptId) {
            try {
              const deptRes = await getDept(this.userDeptId)
              if (deptRes && deptRes.data) {
                const dept = deptRes.data
                // 检查当前用户是否是部门负责人
                this.isDeptHead = dept.leaderUserId != null && dept.leaderUserId === this.currentUserId
              } else {
                this.isDeptHead = false
              }
            } catch (error) {
              console.error('获取部门信息失败:', error)
              this.isDeptHead = false
            }
          } else {
            this.isDeptHead = false
          }
        } else {
          this.isDeptHead = false
          this.isSuperAdmin = false
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        this.isDeptHead = false
        this.isSuperAdmin = false
      }
    },

    /** 与后端 isSuperAdmin（roleCodes 含 admin/super_admin 等）对齐 */
    syncSuperAdminFlag() {
      const roleCodes = (this.userRoles || [])
        .map((r) => {
          if (typeof r === 'string') return r.toLowerCase()
          if (typeof r === 'object' && r !== null) {
            return (r.code || r.roleCode || r.name || '').toLowerCase()
          }
          return ''
        })
        .join(',')
      this.isSuperAdmin =
        roleCodes.includes('admin') ||
        roleCodes.includes('super_admin') ||
        roleCodes.includes('super_administrator')
    },
    
    // 判断是否可以审核（同步方法）
    canReviewPlan(plan) {
      // 只有待审核状态的计划才能审核
      if (plan.status !== 'pending' && plan.status !== 'pending_review' && plan.status !== '待审核') {
        return false
      }
      
      // 不能审核自己的计划
      const currentUserId = this.resolveCurrentUserId()
      if (plan.userId != null && currentUserId != null && Number(plan.userId) === currentUserId) {
        return false
      }
      
      // 获取角色代码
      const roleCodes = this.userRoles.map(r => {
        if (typeof r === 'string') return r.toLowerCase()
        if (typeof r === 'object' && r !== null) {
          return (r.code || r.roleCode || r.name || '').toLowerCase()
        }
        return ''
      }).join(',')
      
      // 判断是否是超级管理员
      const isSuperAdmin = roleCodes.includes('admin') || 
                          roleCodes.includes('super_admin') || 
                          roleCodes.includes('super_administrator')
      
      // 超级管理员可以审核所有计划（除了自己的）
      if (isSuperAdmin) {
        return true
      }
      
      // 如果是部门负责人，可以审核本部门成员的计划（除了自己的）
      if (this.isDeptHead) {
        const currentDeptId = this.userDeptId || this.$store.getters.deptId
        // 必须是本部门的计划，且不是自己的计划
        return plan.deptId === currentDeptId && plan.userId !== currentUserId
      }
      
      return false
    },
    
    // 编辑计划
    async handleEditPlan(plan) {
      try {
        const res = await getOaWorkPlan(plan.id)
        if (res.code === 200 || res.code === 0) {
          const planData = res.data || plan
          
          // 填充编辑表单
          this.createForm = {
            id: planData.id,
            type: planData.type || 'weekly',
            title: planData.title || '',
            period: planData.period || '',
            content: planData.content || '',
            dailyContent: {},
            attachments: planData.attachments ? JSON.parse(planData.attachments) : []
          }
          this.periodRange = this.parsePeriodToRange(planData.period)
          
          // 如果是周计划，解析每日内容
          if (planData.type === 'weekly' && planData.content) {
            try {
              this.createForm.dailyContent = JSON.parse(planData.content)
            } catch (e) {
              this.createForm.dailyContent = {
                monday: '',
                tuesday: '',
                wednesday: '',
                thursday: '',
                friday: '',
                saturday: '',
                sunday: ''
              }
            }
          }
          
          // 设置文件列表回显
          this.uploadFileList = []
          if (planData.attachments) {
            try {
              const attachments = typeof planData.attachments === 'string' 
                ? JSON.parse(planData.attachments) 
                : planData.attachments
              if (Array.isArray(attachments) && attachments.length > 0) {
                this.uploadFileList = attachments.map((file, index) => ({
                  name: file.name || '未知文件',
                  url: file.url,
                  uid: file.id || file.uid || Date.now() + index,
                  status: 'success',
                  response: { code: 200, data: file.url }
                }))
                // 同时更新createForm.attachments
                this.createForm.attachments = attachments
              }
            } catch (e) {
              console.error('解析附件失败:', e)
              this.uploadFileList = []
            }
          }
          
          this.createDialogVisible = true
          this.isEditMode = true
        }
      } catch (error) {
        this.$message.error('获取计划详情失败')
      }
    },
    
    // 删除计划
    async handleDeletePlan(plan) {
      try {
        await this.$confirm(`确定要删除计划"${plan.title}"吗？`, '提示', {
          type: 'warning'
        })
        
        const res = await deleteOaWorkPlan(plan.id)
        if (res.code === 200 || res.code === 0) {
          this.$message.success('删除成功')
          this.getWorkPlanList()
          this.getStatistics()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败: ' + (error.message || '未知错误'))
        }
      }
    },
    
    // 审核计划
    handleReviewPlan(plan) {
      this.currentPlan = plan
      this.reviewDialogVisible = true
    },
    
    // 提交审核（审核对话框）
    async handleSubmitReviewDialog() {
      if (!this.reviewForm.reviewResult) {
        this.$message.warning('请选择审核结果')
        return
      }
      
      try {
        const res = await reviewOaWorkPlan({
          id: this.currentPlan.id,
          reviewResult: this.reviewForm.reviewResult,
          reviewComment: this.reviewForm.reviewComment
        })
        
        if (res.code === 200 || res.code === 0) {
          this.$message.success('审核成功')
          this.reviewDialogVisible = false
          this.reviewForm = {
            reviewResult: '',
            reviewComment: ''
          }
          this.getWorkPlanList()
          this.getStatistics()
        } else {
          this.$message.error(res.msg || '审核失败')
        }
      } catch (error) {
        this.$message.error('审核失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 判断是否可以标记为已完成
    canCompletePlan(plan) {
      const currentUserId = this.currentUserId || this.$store.getters.userId || this.$store.getters.userInfo?.id
      // 只有创建者可以更新完成情况，且状态为已审核
      return plan.userId === currentUserId && 
             (plan.status === 'approved' || plan.status === '已批准' || plan.status === '已审核')
    },
    
    // 打开完成情况对话框
    handleCompletePlan(plan) {
      this.currentPlan = plan
      // 初始化表单，如果有已保存的完成情况则回显
      this.completeForm = {
        id: plan.id,
        completionStatus: plan.completionStatus || '',
        completionComment: plan.completionComment || ''
      }
      this.completeDialogVisible = true
    },
    
    // 提交完成情况
    async handleSubmitComplete() {
      this.$refs.completeForm.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        this.submittingComplete = true
        try {
          const res = await completeOaWorkPlan({
            id: this.completeForm.id,
            completionStatus: this.completeForm.completionStatus,
            completionComment: this.completeForm.completionComment || ''
          })
          
          if (res.code === 200 || res.code === 0) {
            this.$message.success('保存成功')
            this.completeDialogVisible = false
            this.getWorkPlanList()
            this.getStatistics()
          } else {
            this.$message.error(res.msg || '保存失败')
          }
        } catch (error) {
          this.$message.error('保存失败: ' + (error.message || '未知错误'))
        } finally {
          this.submittingComplete = false
        }
      })
    },
    
    // 判断是否可以评价
    canEvaluatePlan(plan) {
      // 必须已保存完成情况
      if (!plan.completionStatus || plan.completionStatus === '') {
        return false
      }
      
      // 不能评价自己的计划
      const currentUserId = this.currentUserId || this.$store.getters.userId || this.$store.getters.userInfo?.id
      if (plan.userId === currentUserId) {
        return false
      }
      
      // 只有部门负责人或超级管理员可以评价
      const roleCodes = this.userRoles.map(r => {
        if (typeof r === 'string') return r.toLowerCase()
        if (typeof r === 'object' && r !== null) {
          return (r.code || r.roleCode || r.name || '').toLowerCase()
        }
        return ''
      }).join(',')
      const isSuperAdmin = roleCodes.includes('admin') || 
                           roleCodes.includes('super_admin') || 
                           roleCodes.includes('super_administrator')
      
      if (isSuperAdmin) {
        return true
      }
      
      // 如果是部门负责人，可以评价本部门的计划
      if (this.isDeptHead && plan.deptId === this.userDeptId) {
        return true
      }
      
      return false
    },
    
    // 打开评价对话框
    handleEvaluatePlan(plan) {
      this.currentPlan = plan
      // 初始化表单，如果有已保存的评价则回显
      this.evaluationForm = {
        id: plan.id,
        qualityEvaluation: plan.qualityEvaluation || '',
        costEfficiencyEvaluation: plan.costEfficiencyEvaluation || '',
        evaluationScore: plan.evaluationScore || null
      }
      this.evaluationDialogVisible = true
    },
    
    // 提交评价
    async handleSubmitEvaluation() {
      this.$refs.evaluationForm.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        this.submittingEvaluation = true
        try {
          const res = await evaluateOaWorkPlan({
            id: this.evaluationForm.id,
            qualityEvaluation: this.evaluationForm.qualityEvaluation || '',
            costEfficiencyEvaluation: this.evaluationForm.costEfficiencyEvaluation || '',
            evaluationScore: this.evaluationForm.evaluationScore
          })
          
          if (res.code === 200 || res.code === 0) {
            this.$message.success('评价成功')
            this.evaluationDialogVisible = false
            this.getWorkPlanList()
            this.getStatistics()
          } else {
            this.$message.error(res.msg || '评价失败')
          }
        } catch (error) {
          this.$message.error('评价失败: ' + (error.message || '未知错误'))
        } finally {
          this.submittingEvaluation = false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.workplan-tab {
  padding: 20px;

  .workplan-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    padding: 20px;
    background: linear-gradient(135deg, rgba(15, 26, 38, 0.95) 0%, rgba(20, 35, 50, 0.95) 100%);
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(84, 144, 122, 0.3);
    border: 1px solid rgba(84, 144, 122, 0.2);

    .header-title {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #ffffff;
        font-weight: 600;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.7);
      }
    }
  }

  .workplan-list-container {
    background: linear-gradient(135deg, rgba(15, 26, 38, 0.95) 0%, rgba(20, 35, 50, 0.95) 100%);
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(84, 144, 122, 0.3);
    border: 1px solid rgba(84, 144, 122, 0.2);
    margin-bottom: 20px;

    .list-header {
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #ffffff;
        font-weight: 600;
      }
    }

    ::v-deep .el-table {
      background: transparent;
      color: #ffffff;

      &::before {
        background-color: rgba(84, 144, 122, 0.2);
      }

      th {
        background: rgba(10, 21, 32, 0.8);
        color: rgba(255, 255, 255, 0.9);
        border-bottom: 1px solid rgba(84, 144, 122, 0.3);
      }

      td {
        background: transparent;
        color: rgba(255, 255, 255, 0.8);
        border-bottom: 1px solid rgba(84, 144, 122, 0.1);
      }

      tr {
        background: transparent;

        &:hover {
          background: rgba(84, 144, 122, 0.1);
        }
      }

      &.el-table--striped {
        .el-table__body {
          tr.el-table__row--striped {
            background: rgba(10, 21, 32, 0.3);

            &:hover {
              background: rgba(84, 144, 122, 0.15);
            }
          }
        }
      }

      /* 状态标签自定义颜色 */
      .el-tag--success {
        background: rgba(103, 194, 58, 0.25) !important;
        border-color: #85ce61 !important;
        color: #95d475 !important;
        font-weight: 600;
      }

      .el-tag--warning {
        background: rgba(230, 162, 60, 0.25) !important;
        border-color: #ebb563 !important;
        color: #f0c78a !important;
        font-weight: 600;
      }

      .el-tag--danger {
        background: rgba(245, 108, 108, 0.25) !important;
        border-color: #f78989 !important;
        color: #f89898 !important;
        font-weight: 600;
      }

      /* 固定列样式修复 */
      .el-table__fixed-right {
        border-left: 1px solid rgba(84, 144, 122, 0.2);

        &::before {
          display: none;
        }

        td {
          background: rgba(15, 26, 38, 0.95);
        }

        tr:hover td {
          background: rgba(84, 144, 122, 0.1);
        }
      }

      .el-table__fixed-right-patch {
        background: rgba(10, 21, 32, 0.8);
        border-bottom: 1px solid rgba(84, 144, 122, 0.3);
      }

      .el-table__fixed-right::before,
      .el-table__fixed::before {
        display: none;
      }
    }

    /* 操作按钮布局 — 对齐正式版绿/蓝/红色块 */
    .action-btns {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-wrap: wrap;
      gap: 6px;

      ::v-deep .el-button {
        margin: 0 !important;
      }

      ::v-deep .act-btn {
        border-radius: 4px !important;
        padding: 5px 10px !important;
        font-size: 12px !important;
        border: 1px solid transparent !important;
        color: #ffffff !important;
        background: transparent !important;
      }

      ::v-deep .act-view {
        background: rgba(34, 197, 94, 0.22) !important;
        border-color: rgba(34, 197, 94, 0.55) !important;
        color: #4ade80 !important;
        &:hover { background: rgba(34, 197, 94, 0.35) !important; color: #86efac !important; }
      }

      ::v-deep .act-edit {
        background: rgba(59, 130, 246, 0.22) !important;
        border-color: rgba(59, 130, 246, 0.55) !important;
        color: #60a5fa !important;
        &:hover { background: rgba(59, 130, 246, 0.35) !important; color: #93c5fd !important; }
      }

      ::v-deep .act-delete {
        background: rgba(239, 68, 68, 0.2) !important;
        border-color: rgba(239, 68, 68, 0.55) !important;
        color: #f87171 !important;
        &:hover { background: rgba(239, 68, 68, 0.32) !important; color: #fca5a5 !important; }
      }

      ::v-deep .act-review {
        background: rgba(103, 194, 58, 0.15) !important;
        border-color: rgba(103, 194, 58, 0.55) !important;
        color: #67c23a !important;
      }

      ::v-deep .act-complete {
        background: rgba(84, 144, 122, 0.15) !important;
        border-color: rgba(84, 144, 122, 0.5) !important;
        color: #79bbff !important;
      }

      ::v-deep .act-evaluate {
        background: rgba(230, 162, 60, 0.15) !important;
        border-color: rgba(230, 162, 60, 0.5) !important;
        color: #e6a23c !important;
      }
    }
  }

  .create-plan-btn {
    font-weight: 600 !important;
    background: #2f6b5a !important;
    border-color: #54907a !important;
    color: #fff !important;
    &:hover, &:focus {
      background: #3a8570 !important;
      border-color: #6fb59a !important;
    }
  }

  .pagination-container {
    background: transparent !important;
    padding: 0 !important;
    border: none !important;

    ::v-deep .el-pagination {
      padding: 0;
      background: transparent !important;
      height: auto;
      line-height: normal;

      .el-pagination__total,
      .el-pagination__jump {
        color: rgba(255, 255, 255, 0.8);
        background: transparent !important;
      }

      .btn-prev,
      .btn-next,
      .number {
        background: #162e46 !important;
        color: rgba(255, 255, 255, 0.85) !important;
        border: 1px solid rgba(84, 144, 122, 0.28) !important;
        border-radius: 4px;
      }

      .number.active,
      .el-pager li.active {
        background: #54907a !important;
        color: #fff !important;
        border-color: #54907a !important;
      }

      .btn-prev:hover,
      .btn-next:hover,
      .number:hover {
        color: #8fd4b8 !important;
        border-color: #54907a !important;
      }

      .el-select .el-input__inner {
        background: #162e46;
        color: rgba(255, 255, 255, 0.8);
        border: 1px solid rgba(84, 144, 122, 0.3);
      }

      span:not(.number):not(.btn-prev):not(.btn-next) {
        background: transparent !important;
      }
    }
  }

  .workplan-statistics-section {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }

    .stat-card {
      background: linear-gradient(135deg, rgba(15, 26, 38, 0.95) 0%, rgba(20, 35, 50, 0.95) 100%);
      border-radius: 8px;
      padding: 24px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(84, 144, 122, 0.3);
      border: 1px solid rgba(84, 144, 122, 0.2);
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      position: relative;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 6px 30px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(84, 144, 122, 0.5);
        transform: translateY(-2px);
      }

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
        margin-bottom: 15px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);

        i {
          font-size: 28px;
          color: white;
        }
      }

      .stat-content {
        margin-bottom: 10px;

        .stat-value {
          font-size: 32px;
          font-weight: bold;
          color: #ffffff;
          margin-bottom: 5px;
          text-shadow: 0 0 10px rgba(84, 144, 122, 0.5);
        }

        .stat-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.7);
        }
      }

      .stat-title {
        font-size: 16px;
        color: rgba(255, 255, 255, 0.8);
        font-weight: 500;
      }
    }
  }

  .plan-detail {
    .detail-section {
      margin-bottom: 20px;
      background: rgba(10, 21, 32, 0.5);
      border-radius: 8px;
      border: 1px solid rgba(84, 144, 122, 0.15);
      overflow: hidden;

      .section-header {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 16px;
        background: rgba(84, 144, 122, 0.08);
        border-bottom: 1px solid rgba(84, 144, 122, 0.2);

        i {
          font-size: 16px;
          color: #54907a;
        }

        span {
          font-size: 15px;
          font-weight: 600;
          color: #ffffff;
        }
      }

      .info-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1px;
        padding: 16px;
        background: rgba(84, 144, 122, 0.08);

        .info-item {
          background: rgba(10, 21, 32, 0.7);
          padding: 12px 14px;
          display: flex;
          flex-direction: column;
          gap: 6px;

          &.info-item-full {
            grid-column: 1 / -1;
          }
        }

        .info-label {
          font-size: 12px;
          color: rgba(255, 255, 255, 0.45);
          letter-spacing: 0.5px;
        }

        .info-value {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.95);
          font-weight: 500;

          &.info-value-text {
            white-space: pre-wrap;
            word-break: break-word;
            line-height: 1.7;
            font-weight: 400;
          }
        }
      }

      .content-text {
        color: rgba(255, 255, 255, 0.9);
        line-height: 1.8;
        padding: 14px 16px;
        background: rgba(10, 21, 32, 0.7);
        border-radius: 6px;
        margin: 16px;
      }

      .attachment-list {
        padding: 12px 16px;

        .attachment-item {
          margin-bottom: 8px;
          padding: 8px 12px;
          background: rgba(10, 21, 32, 0.7);
          border-radius: 4px;
          border: 1px solid rgba(84, 144, 122, 0.1);
          transition: all 0.3s;

          &:hover {
            background: rgba(84, 144, 122, 0.1);
            border-color: rgba(84, 144, 122, 0.3);
          }
        }
      }

      .empty-text {
        padding: 16px;
        text-align: center;
        color: rgba(255, 255, 255, 0.4);
        font-size: 14px;
      }
    }

    .el-link {
      color: #54907a !important;
    }
  }
}

// 对话框样式
.dialog-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(84, 144, 122, 0.2);
}

::v-deep .el-dialog {
  background: linear-gradient(135deg, rgba(15, 26, 38, 0.98) 0%, rgba(20, 35, 50, 0.98) 100%);
  border: 1px solid rgba(84, 144, 122, 0.3);

  .el-dialog__header {
    border-bottom: 1px solid rgba(84, 144, 122, 0.2);
    padding: 20px 20px 16px;

    .el-dialog__title {
      color: #ffffff;
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__close {
    color: rgba(255, 255, 255, 0.7);

      &:hover {
        color: #54907a;
      }
    }
  }

  .el-dialog__body {
    padding: 20px;
    color: rgba(255, 255, 255, 0.9);
    max-height: 70vh;
    overflow-y: auto;
  }

  .el-dialog__footer {
    border-top: 1px solid rgba(84, 144, 122, 0.2);
    padding: 16px 20px;
  }

  .el-form-item__label {
    color: rgba(255, 255, 255, 0.9);
  }

  .el-input__inner,
  .el-textarea__inner {
    background: rgba(10, 21, 32, 0.6);
    border-color: rgba(84, 144, 122, 0.3);
    color: #ffffff;

    &::placeholder {
      color: rgba(255, 255, 255, 0.5);
    }

    &:focus {
      border-color: #54907a;
    }
  }

  .el-select .el-input__inner {
    background: rgba(10, 21, 32, 0.6);
    border-color: rgba(84, 144, 122, 0.3);
    color: #ffffff;
  }

  .el-descriptions {
    ::v-deep .el-descriptions__header {
      .el-descriptions__title {
        color: #ffffff !important;
      }
    }

    ::v-deep .el-descriptions__label {
      color: rgba(255, 255, 255, 0.9) !important;
      font-weight: 500;
      background: rgba(10, 21, 32, 0.8) !important;
    }

    ::v-deep .el-descriptions__content {
      color: rgba(255, 255, 255, 0.9) !important;
      background: rgba(10, 21, 32, 0.6) !important;
    }

    ::v-deep .el-descriptions__table {
      border-color: rgba(84, 144, 122, 0.2) !important;
      background: rgba(10, 21, 32, 0.4) !important;
      
      th, td {
        border-color: rgba(84, 144, 122, 0.2) !important;
      }
    }

    ::v-deep .el-descriptions__table th {
      border-color: rgba(84, 144, 122, 0.2) !important;
      background: rgba(10, 21, 32, 0.8) !important;
      background-color: rgba(10, 21, 32, 0.8) !important;
      color: rgba(255, 255, 255, 0.9) !important;
    }

    ::v-deep .el-descriptions__table td {
      border-color: rgba(84, 144, 122, 0.2) !important;
      background: rgba(10, 21, 32, 0.6) !important;
      background-color: rgba(10, 21, 32, 0.6) !important;
      color: rgba(255, 255, 255, 0.9) !important;
    }

    ::v-deep .el-descriptions__table tr {
      background: transparent !important;
    }

    ::v-deep .el-descriptions__table tbody tr {
      background: transparent !important;
    }

    ::v-deep .el-descriptions__table tbody tr td {
      background: rgba(10, 21, 32, 0.6) !important;
      background-color: rgba(10, 21, 32, 0.6) !important;
      color: rgba(255, 255, 255, 0.9) !important;
    }

    ::v-deep .el-descriptions__table thead tr th {
      background: rgba(10, 21, 32, 0.8) !important;
      background-color: rgba(10, 21, 32, 0.8) !important;
      color: rgba(255, 255, 255, 0.9) !important;
    }

    ::v-deep .el-tag {
      background: transparent !important;
      border-color: rgba(84, 144, 122, 0.3) !important;
      color: rgba(255, 255, 255, 0.9) !important;
    }
  }

  // 查看对话框分区样式
  .plan-detail {
    .detail-section {
      margin-bottom: 20px;
      background: rgba(10, 21, 32, 0.4);
      border-radius: 8px;
      border: 1px solid rgba(84, 144, 122, 0.15);
      overflow: hidden;

      .section-header {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 16px;
        background: rgba(84, 144, 122, 0.1);
        border-bottom: 1px solid rgba(84, 144, 122, 0.2);

        i {
          font-size: 16px;
          color: #54907a;
        }

        span {
          font-size: 15px;
          font-weight: 600;
          color: #ffffff;
        }
      }

      .el-descriptions {
        padding: 12px 16px;

        .el-descriptions__table {
          background: transparent !important;
          background-color: transparent !important;

          th, td {
            background: transparent !important;
            background-color: transparent !important;
            color: rgba(255, 255, 255, 0.9) !important;
            border-color: rgba(84, 144, 122, 0.15) !important;
          }

          th {
            background: rgba(10, 21, 32, 0.5) !important;
            background-color: rgba(10, 21, 32, 0.5) !important;
            color: rgba(255, 255, 255, 0.7) !important;
            font-weight: 500;
          }
        }
      }

      .content-text {
        color: rgba(255, 255, 255, 0.9);
        line-height: 1.8;
        padding: 14px 16px;
        background: rgba(10, 21, 32, 0.5);
        border-radius: 6px;
        border: 1px solid rgba(84, 144, 122, 0.15);
        margin: 12px 16px;
      }

      .attachment-list {
        padding: 12px 16px;

        .attachment-item {
          margin-bottom: 8px;
          padding: 8px 12px;
          background: rgba(10, 21, 32, 0.5);
          border-radius: 4px;
          border: 1px solid rgba(84, 144, 122, 0.1);
          transition: all 0.3s;

          &:hover {
            background: rgba(84, 144, 122, 0.1);
            border-color: rgba(84, 144, 122, 0.3);
          }
        }
      }

      .empty-text {
        padding: 16px;
        text-align: center;
        color: rgba(255, 255, 255, 0.4);
        font-size: 14px;
      }
    }

    .el-link {
      color: #54907a !important;
    }
  }
}

.workplan-uploader {
  ::v-deep .el-upload {
    width: 100%;
  }

  ::v-deep .el-upload-dragger {
    width: 100%;
    background: rgba(10, 21, 32, 0.6);
    border-color: rgba(84, 144, 122, 0.3);
    border-style: dashed;

    &:hover {
      border-color: #54907a;
      background: rgba(10, 21, 32, 0.8);
    }

    .el-icon-upload {
      font-size: 67px;
      color: rgba(84, 144, 122, 0.6);
      margin: 40px 0 16px;
    }

    .el-upload__text {
      color: rgba(255, 255, 255, 0.9);
      font-size: 14px;
    }

    .el-upload__tip {
      color: rgba(255, 255, 255, 0.6);
      font-size: 12px;
      margin-top: 8px;
    }
  }
}
</style>

<!-- 审核弹窗默认 append-to-body，scoped 无法覆盖内部表格白底；用 custom-class 单独统一深色 -->
<style lang="scss">
.review-work-plan-dialog {
  background: linear-gradient(135deg, rgba(15, 26, 38, 0.98) 0%, rgba(20, 35, 50, 0.98) 100%);
  border: 1px solid rgba(84, 144, 122, 0.25);

  .el-dialog__header {
    border-bottom: 1px solid rgba(84, 144, 122, 0.2);
    padding: 20px 20px 16px;

    .el-dialog__title {
      color: #ffffff;
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__close {
      color: rgba(255, 255, 255, 0.7);

      &:hover {
        color: #54907a;
      }
    }
  }

  .el-dialog__body {
    padding: 20px;
    color: rgba(255, 255, 255, 0.9);
    background: transparent;
    max-height: 70vh;
    overflow-y: auto;
  }

  .el-dialog__footer {
    border-top: 1px solid rgba(84, 144, 122, 0.2);
    padding: 16px 20px;
  }

  /* 分区样式 */
  .detail-section {
    margin-bottom: 20px;
    background: rgba(10, 21, 32, 0.4);
    border-radius: 8px;
    border: 1px solid rgba(84, 144, 122, 0.15);
    overflow: hidden;

    .section-header {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 16px;
      background: rgba(84, 144, 122, 0.1);
      border-bottom: 1px solid rgba(84, 144, 122, 0.2);

      i {
        font-size: 16px;
        color: #54907a;
      }

      span {
        font-size: 15px;
        font-weight: 600;
        color: #ffffff;
      }
    }

    .info-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1px;
      padding: 16px;
      background: rgba(84, 144, 122, 0.08);

      .info-item {
        background: rgba(10, 21, 32, 0.7);
        padding: 12px 14px;
        display: flex;
        flex-direction: column;
        gap: 6px;

        &.info-item-full {
          grid-column: 1 / -1;
        }
      }

      .info-label {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.45);
        letter-spacing: 0.5px;
      }

      .info-value {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.95);
        font-weight: 500;

        &.info-value-text {
          white-space: pre-wrap;
          word-break: break-word;
          line-height: 1.7;
          font-weight: 400;
        }
      }
    }

    .review-plan-content {
      max-height: 240px;
      overflow-y: auto;

      strong {
        color: rgba(255, 255, 255, 0.95);
      }
    }

    .review-form-wrap {
      padding: 16px;
    }

    .el-form {
      .el-form-item__label {
        color: rgba(255, 255, 255, 0.9);
      }

      .el-input__inner,
      .el-textarea__inner {
        background: rgba(10, 21, 32, 0.6);
        border-color: rgba(84, 144, 122, 0.3);
        color: #ffffff;

        &::placeholder {
          color: rgba(255, 255, 255, 0.5);
        }

        &:focus {
          border-color: #54907a;
        }
      }

      .el-radio__label {
        color: rgba(255, 255, 255, 0.9);
      }

      .el-radio-group {
        padding-left: 20px;

        .el-radio {
          margin-right: 30px;
        }
      }
    }
  }

  .el-tag {
    background: transparent !important;
    border-color: rgba(84, 144, 122, 0.35) !important;
    color: rgba(255, 255, 255, 0.9) !important;
  }
}
</style>
