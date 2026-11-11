<template>
  <div class="workplan-tab">
    <!-- 头部区域 -->
    <div class="workplan-header">
      <div class="header-title">
        <h2><i class="el-icon-notebook-2"></i> 工作计划管理</h2>
        <p>创建、提交和管理您的工作计划</p>
      </div>
      <el-button
        type="primary"
        size="small"
        class="create-plan-btn"
        icon="el-icon-plus"
        @click="showCreateDialog">
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
              <el-tag
                :type="getTypeTagType(scope.row.type)"
                size="small"
                effect="plain">
                {{ getTypeLabel(scope.row.type) }}
              </el-tag>
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
          <el-table-column label="操作" min-width="260" align="center" fixed="right">
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
                  class="act-btn act-edit"
                  size="mini"
                  icon="el-icon-edit"
                  @click="handleEditPlan(scope.row)">
                  修改
                </el-button>
                <el-button
                  class="act-btn act-delete"
                  size="mini"
                  icon="el-icon-delete"
                  @click="handleDeletePlan(scope.row)">
                  删除
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

    <!-- 创建/编辑工作计划对话框 -->
    <el-dialog
      :title="isEditMode ? '编辑工作计划' : '创建工作计划'"
      :visible.sync="createDialogVisible"
      width="800px"
      :close-on-click-modal="false"
      @close="resetCreateForm">
      <div class="dialog-subtitle">填写工作计划详情并提交</div>

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

        <!-- 周计划：整体工作内容（不需要每天设置） -->
        <el-form-item v-if="createForm.type === 'weekly'" label="工作内容" prop="content">
          <el-input
            v-model="createForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入本周的工作内容">
          </el-input>
        </el-form-item>

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
        <el-descriptions :column="2" border>
          <el-descriptions-item label="计划标题">{{ currentPlan.title || '--' }}</el-descriptions-item>
          <el-descriptions-item label="计划类型">
            <el-tag :type="getTypeTagType(currentPlan.type)" size="small">
              {{ getTypeLabel(currentPlan.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="计划周期">{{ formatPeriod(currentPlan) }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ currentPlan.userName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="提交日期">{{ formatDate(currentPlan.submitDate || currentPlan.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="完成度">
            <span v-if="currentPlan.completionRate !== null && currentPlan.completionRate !== undefined"
                  style="color: #67C23A; font-weight: 500;">
              {{ currentPlan.completionRate }}%
            </span>
            <span v-else>--</span>
          </el-descriptions-item>
        </el-descriptions>

        <div class="plan-content" style="margin-top: 20px;">
          <h4>工作内容</h4>
          <div class="content-text" v-html="formatPlanContent(currentPlan)"></div>
        </div>

        <!-- 附件列表 -->
        <div style="margin-top: 20px;">
          <h4 style="color: #ffffff; margin-bottom: 12px; font-size: 16px;">附件</h4>
          <div v-if="getAttachmentsList(currentPlan.attachments).length > 0">
            <div v-for="(file, index) in getAttachmentsList(currentPlan.attachments)" :key="index" style="margin-bottom: 8px;">
              <el-link :href="file.url" target="_blank" type="primary" icon="el-icon-document" style="color: #54907a;">
                {{ file.name || '未知文件' }}
              </el-link>
            </div>
          </div>
          <div v-else style="color: rgba(255, 255, 255, 0.5);">暂无附件</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getDeptWorkPlanPage,
  createDeptWorkPlan,
  updateDeptWorkPlan,
  deleteDeptWorkPlan,
  getDeptWorkPlan
} from '@/api/fish/deptWorkPlan'
import { getAccessToken } from '@/utils/auth'
import { getInfo } from '@/api/login'
import { getDept } from '@/api/system/dept'
import { workPlanTypeIcon } from '@/utils/workPlanTypeMeta'

export default {
  name: 'DeptWorkPlan',
  data() {
    return {
      loading: false,
      workPlanList: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10
      },
      // 创建对话框
      createDialogVisible: false,
      submitting: false,
      createForm: {
        type: 'weekly',
        title: '',
        period: '',
        content: '',
        attachments: []
      },
      /** 计划周期日期范围（选择器用） */
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
        ],
        content: [
          { required: true, message: '请输入工作内容', trigger: 'blur' }
        ]
      },
      uploadFileList: [],
      /** 附件在校验阶段被拒（格式/大小），阻止在无合格附件时误保存 */
      attachmentUploadRejected: false,
      uploadAction: process.env.VUE_APP_BASE_API + '/admin-api/infra/file/upload',
      uploadData: {},
      // 查看对话框
      viewDialogVisible: false,
      currentPlan: null,
      // 编辑模式
      isEditMode: false,
      // 用户角色信息
      userRoles: [],
      userDeptId: null,
      isDeptHead: false,
      isSuperAdmin: false,
      currentUserId: null
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
    await this.loadUserInfo()
    this.getWorkPlanList()
  },
  methods: {
    // 获取工作计划列表
    async getWorkPlanList() {
      this.loading = true
      try {
        const res = await getDeptWorkPlanPage(this.queryParams)
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

    // 显示创建对话框
    showCreateDialog() {
      this.resetCreateForm()
      this.isEditMode = false
      this.createDialogVisible = true
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

    // 日期范围 → 周期字符串
    handlePeriodRangeChange(val) {
      if (Array.isArray(val) && val.length === 2 && val[0] && val[1]) {
        this.createForm.period = `${val[0]} 至 ${val[1]}`
      } else {
        this.createForm.period = ''
      }
    },

    // 周期字符串 → 日期范围（编辑回显）
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
          // 检查是否有正在上传的文件
          const uploadingFiles = this.uploadFileList.filter(f => f.status === 'uploading')
          if (uploadingFiles.length > 0) {
            this.$message.warning('文件正在上传中，请稍候...')
            return
          }

          // 检查是否有上传失败的文件
          const failedFiles = this.uploadFileList.filter(f => f.status === 'fail')
          if (failedFiles.length > 0) {
            this.$message.warning('有文件上传失败，请重新上传或移除失败的文件')
            return
          }

          // 检查是否有未上传的文件，如果有则先触发上传
          const unUploadedFiles = this.uploadFileList.filter(f => !f.response && f.status !== 'success' && f.status !== 'fail' && f.status !== 'uploading')
          if (unUploadedFiles.length > 0) {
            // 手动触发上传
            try {
              this.$refs.upload.submit()
              // 等待上传完成
              let waitCount = 0
              const maxWait = 30 // 最多等待6秒（30 * 200ms）
              while (unUploadedFiles.some(f => !f.response && f.status !== 'success' && f.status !== 'fail') && waitCount < maxWait) {
                await new Promise(resolve => setTimeout(resolve, 200))
                waitCount++
              }

              // 再次检查是否还有未上传的文件
              const stillUnUploaded = this.uploadFileList.filter(f => !f.response && f.status !== 'success' && f.status !== 'fail')
              if (stillUnUploaded.length > 0) {
                this.$message.warning('文件上传超时，请稍后重试')
                return
              }
            } catch (error) {
              this.$message.error('触发文件上传失败，请重试')
              return
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
        } else {
          // 如果没有文件，清空附件
          this.createForm.attachments = []
        }

        this.submitting = true
        try {
          const planData = {
            type: this.createForm.type,
            title: this.createForm.title,
            period: this.createForm.period,
            content: this.createForm.content, // 周计划直接使用content，不需要JSON.stringify
            attachments: this.createForm.attachments && this.createForm.attachments.length > 0 ? JSON.stringify(this.createForm.attachments) : null
          }

          const res = await createDeptWorkPlan(planData)
          if (res.code === 200 || res.code === 0) {
            this.$message.success('工作计划保存成功')
            this.createDialogVisible = false
            this.resetCreateForm()
            this.getWorkPlanList()
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
          // 检查是否有正在上传的文件
          const uploadingFiles = this.uploadFileList.filter(f => f.status === 'uploading')
          if (uploadingFiles.length > 0) {
            this.$message.warning('文件正在上传中，请稍候...')
            return
          }

          // 检查是否有上传失败的文件
          const failedFiles = this.uploadFileList.filter(f => f.status === 'fail')
          if (failedFiles.length > 0) {
            this.$message.warning('有文件上传失败，请重新上传或移除失败的文件')
            return
          }

          // 检查是否有未上传的文件，如果有则先触发上传
          const unUploadedFiles = this.uploadFileList.filter(f => !f.response && f.status !== 'success' && f.status !== 'fail' && f.status !== 'uploading')
          if (unUploadedFiles.length > 0) {
            // 手动触发上传
            try {
              this.$refs.upload.submit()
              // 等待上传完成
              let waitCount = 0
              const maxWait = 30 // 最多等待6秒（30 * 200ms）
              while (unUploadedFiles.some(f => !f.response && f.status !== 'success' && f.status !== 'fail') && waitCount < maxWait) {
                await new Promise(resolve => setTimeout(resolve, 200))
                waitCount++
              }

              // 再次检查是否还有未上传的文件
              const stillUnUploaded = this.uploadFileList.filter(f => !f.response && f.status !== 'success' && f.status !== 'fail')
              if (stillUnUploaded.length > 0) {
                this.$message.warning('文件上传超时，请稍后重试')
                return
              }
            } catch (error) {
              this.$message.error('触发文件上传失败，请重试')
              return
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
        } else {
          // 如果没有文件，清空附件
          this.createForm.attachments = []
        }

        this.submitting = true
        try {
          const planData = {
            id: this.createForm.id,
            type: this.createForm.type,
            title: this.createForm.title,
            period: this.createForm.period,
            content: this.createForm.content, // 周计划直接使用content，不需要JSON.stringify
            attachments: this.createForm.attachments && this.createForm.attachments.length > 0 ? JSON.stringify(this.createForm.attachments) : null
          }

          const res = await updateDeptWorkPlan(planData)
          if (res.code === 200 || res.code === 0) {
            this.$message.success('工作计划更新成功')
            this.createDialogVisible = false
            this.resetCreateForm()
            this.getWorkPlanList()
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
        const res = await getDeptWorkPlan(plan.id)
        if (res.code === 200 || res.code === 0) {
          // 确保使用后端返回的完整数据，如果后端数据不完整，则合并列表数据
          // 后端数据优先，列表数据作为补充
          this.currentPlan = {
            ...plan,
            ...(res.data || {})
          }
          // 确保 type 字段存在
          if (!this.currentPlan.type && plan.type) {
            this.currentPlan.type = plan.type
          }
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

      // 周计划直接显示文本，不需要解析JSON
      return plan.content.replace(/\n/g, '<br>')
    },

    // 获取类型标签类型
    getTypeTagType(type) {
      if (!type) return 'info'
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
      // 去除可能的空格
      const trimmedType = String(type).trim()
      return typeMap[trimmedType] || 'info'
    },

    // 获取类型标签文本
    getTypeLabel(type) {
      if (!type) return '未知'
      const typeMap = {
        'weekly': '周计划',
        'monthly': '月计划',
        'quarterly': '季度计划',
        'annual': '年度计划'
      }
      // 去除可能的空格
      const trimmedType = String(type).trim()
      return typeMap[trimmedType] || type || '未知'
    },
    workPlanTypeIcon,

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

    // 加载用户信息
    async loadUserInfo() {
      try {
        const res = await getInfo()
        if (res && res.data) {
          this.userRoles = res.data.roles || []
          this.userDeptId = res.data.user?.deptId || this.$store.getters.deptId
          const rawId = this.$store.getters.userId || res.data.user?.id
          const n = Number(rawId)
          this.currentUserId = Number.isFinite(n) && n > 0 ? n : null

          const roleCodes = (this.userRoles || [])
            .map((r) => {
              if (typeof r === 'string') return r.toLowerCase()
              if (typeof r === 'object' && r !== null) {
                return (r.code || r.roleCode || r.name || '').toLowerCase()
              }
              return ''
            })
            .join(',')
          const nick = (res.data.user?.nickname || res.data.user?.userName || '').toLowerCase()
          this.isSuperAdmin =
            roleCodes.includes('admin') ||
            roleCodes.includes('super_admin') ||
            roleCodes.includes('super_administrator') ||
            roleCodes.includes('tenant_admin') ||
            nick.includes('管理员') ||
            nick.includes('admin')

          if (this.userDeptId && this.currentUserId) {
            try {
              const deptRes = await getDept(this.userDeptId)
              if (deptRes && deptRes.data) {
                const dept = deptRes.data
                this.isDeptHead =
                  dept.leaderUserId != null &&
                  Number(dept.leaderUserId) === this.currentUserId
              } else {
                this.isDeptHead = false
              }
            } catch (deptError) {
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
        this.isDeptHead = false
        this.isSuperAdmin = false
      }
    },

    resolveCurrentUserId() {
      const raw =
        this.currentUserId ||
        this.$store.getters.userId ||
        (this.$store.getters.userInfo && this.$store.getters.userInfo.id)
      const n = Number(raw)
      return Number.isFinite(n) && n > 0 ? n : null
    },

    // 创建者 / 部门负责人 / 超管 可编辑
    canEditPlan(plan) {
      if (!plan) return false
      if (this.isSuperAdmin || this.isDeptHead) return true
      const uid = this.resolveCurrentUserId()
      return uid != null && plan.userId != null && Number(plan.userId) === uid
    },

    // 创建者 / 部门负责人 / 超管 可删除
    canDeletePlan(plan) {
      if (!plan) return false
      if (this.isSuperAdmin || this.isDeptHead) return true
      const uid = this.resolveCurrentUserId()
      return uid != null && plan.userId != null && Number(plan.userId) === uid
    },

    // 编辑计划
    async handleEditPlan(plan) {
      try {
        const res = await getDeptWorkPlan(plan.id)
        if (res.code === 200 || res.code === 0) {
          const planData = res.data || plan

          // 填充编辑表单
          this.createForm = {
            id: planData.id,
            type: planData.type || 'weekly',
            title: planData.title || '',
            period: planData.period || '',
            content: planData.content || '', // 周计划直接使用content，不需要解析JSON
            attachments: planData.attachments ? JSON.parse(planData.attachments) : []
          }
          this.periodRange = this.parsePeriodToRange(planData.period)

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

        const res = await deleteDeptWorkPlan(plan.id)
        if (res.code === 200 || res.code === 0) {
          this.$message.success('删除成功')
          this.getWorkPlanList()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败: ' + (error.message || '未知错误'))
        }
      }
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

    .create-plan-btn {
      flex-shrink: 0;
      font-weight: 600 !important;
      padding: 10px 18px !important;
      background: #2f6b5a !important;
      border-color: #3d8a74 !important;
      color: #fff !important;
      &:hover {
        background: #3a8570 !important;
        border-color: #4a9d86 !important;
      }
    }
  }

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
    }
  }

  .pagination-container {
    ::v-deep .el-pagination {
      .el-pagination__total,
      .el-pagination__jump {
        color: rgba(255, 255, 255, 0.8);
      }

      .btn-prev,
      .btn-next,
      .number {
        background: rgba(10, 21, 32, 0.6);
        color: rgba(255, 255, 255, 0.8);
        border: 1px solid rgba(84, 144, 122, 0.3);

        &:hover {
          color: #54907a;
          border-color: #54907a;
        }
      }

      .number.active {
        background: #54907a;
        color: #ffffff;
        border-color: #54907a;
      }

      .el-select .el-input__inner {
        background: rgba(10, 21, 32, 0.6);
        color: rgba(255, 255, 255, 0.8);
        border: 1px solid rgba(84, 144, 122, 0.3);
      }
    }
  }

  .plan-detail {
    .plan-content {
      h4 {
        color: #ffffff;
        margin-bottom: 12px;
        font-size: 16px;
      }

      .content-text {
        color: rgba(255, 255, 255, 0.8);
        line-height: 1.8;
        padding: 16px;
        background: rgba(10, 21, 32, 0.6);
        border-radius: 6px;
        border: 1px solid rgba(84, 144, 122, 0.2);
      }
    }

    // el-descriptions 深色主题样式
    ::v-deep .el-descriptions {
      background: transparent !important;

      .el-descriptions__header {
        .el-descriptions__title {
          color: rgba(255, 255, 255, 0.9) !important;
        }
      }

      .el-descriptions__label {
        color: rgba(255, 255, 255, 0.9) !important;
        background: rgba(10, 21, 32, 0.8) !important;
        font-weight: 500;
      }

      .el-descriptions__content {
        color: rgba(255, 255, 255, 0.8) !important;
        background: rgba(10, 21, 32, 0.6) !important;
      }

      .el-descriptions__table {
        background: transparent !important;
        border-color: rgba(84, 144, 122, 0.2) !important;
      }

      .el-descriptions__table th {
        background: rgba(10, 21, 32, 0.8) !important;
        color: rgba(255, 255, 255, 0.9) !important;
        border-color: rgba(84, 144, 122, 0.2) !important;
      }

      .el-descriptions__table td {
        background: rgba(10, 21, 32, 0.6) !important;
        color: rgba(255, 255, 255, 0.8) !important;
        border-color: rgba(84, 144, 122, 0.2) !important;
      }

      .el-descriptions__table tr {
        background: transparent !important;
      }

      .el-descriptions__table.is-bordered th,
      .el-descriptions__table.is-bordered td {
        border-color: rgba(84, 144, 122, 0.2) !important;
      }
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
}

// 全局深色主题样式（针对对话框内的 el-descriptions）
::v-deep .el-dialog .el-descriptions,
::v-deep .el-dialog .plan-detail .el-descriptions {
  background: transparent !important;

  .el-descriptions__header {
    .el-descriptions__title {
      color: rgba(255, 255, 255, 0.9) !important;
    }
  }

  .el-descriptions__label {
    color: rgba(255, 255, 255, 0.9) !important;
    background: rgba(10, 21, 32, 0.8) !important;
    font-weight: 500;
  }

  .el-descriptions__content {
    color: rgba(255, 255, 255, 0.8) !important;
    background: rgba(10, 21, 32, 0.6) !important;
  }

  .el-descriptions__table {
    background: transparent !important;
    border-color: rgba(84, 144, 122, 0.2) !important;
  }

  .el-descriptions__table th {
    background: rgba(10, 21, 32, 0.8) !important;
    color: rgba(255, 255, 255, 0.9) !important;
    border-color: rgba(84, 144, 122, 0.2) !important;
  }

  .el-descriptions__table td {
    background: rgba(10, 21, 32, 0.6) !important;
    color: rgba(255, 255, 255, 0.8) !important;
    border-color: rgba(84, 144, 122, 0.2) !important;
  }

  .el-descriptions__table tr {
    background: transparent !important;
  }

  .el-descriptions__table.is-bordered th,
  .el-descriptions__table.is-bordered td {
    border-color: rgba(84, 144, 122, 0.2) !important;
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

