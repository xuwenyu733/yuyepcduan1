<template>
  <div class="document-collaboration">
    <!-- 头部区域 -->
    <div class="document-header">
      <div class="header-title">
        <h2>文档协同管理</h2>
        <p>共享文档编辑、分类管理与查询</p>
      </div>
      <el-button type="primary" class="upload-doc-btn" @click="showUploadDialog">
        <i class="el-icon-plus"></i> 上传文档
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter-bar">
      <el-input 
        v-model="documentSearchKeyword"
        placeholder="搜索文档标题或作者..."
        prefix-icon="el-icon-search"
        clearable
        @input="handleDocumentSearch"
        style="flex: 1; max-width: 400px;">
      </el-input>
      <el-select 
        v-model="documentFilterType" 
        placeholder="全部类型"
        clearable
        style="width: 150px; margin-left: 12px;"
        @change="handleDocumentFilter">
        <el-option label="全部类型" value=""></el-option>
        <el-option label="申报文档" value="申报文档"></el-option>
        <el-option label="设计文档" value="设计文档"></el-option>
        <el-option label="实验文档" value="实验文档"></el-option>
        <el-option label="工作计划" value="工作计划"></el-option>
        <el-option label="报告文档" value="报告文档"></el-option>
        <el-option label="其他" value="其他"></el-option>
      </el-select>
      <el-select 
        v-model="documentFilterAuthor" 
        placeholder="全部作者"
        clearable
        filterable
        style="width: 150px; margin-left: 12px;"
        @change="handleDocumentFilter">
        <el-option label="全部作者" value=""></el-option>
        <el-option 
          v-for="author in authorList" 
          :key="author.id"
          :label="author.name" 
          :value="author.id">
        </el-option>
      </el-select>
    </div>

    <!-- 分类标签 -->
    <el-tabs v-model="documentViewMode" @tab-click="handleDocumentViewChange" class="document-tabs">
      <el-tab-pane label="全部文档" name="all"></el-tab-pane>
      <el-tab-pane label="按人员分类" name="personnel"></el-tab-pane>
      <el-tab-pane label="按业务分类" name="business"></el-tab-pane>
    </el-tabs>

    <!-- 文档列表 -->
    <div class="document-list-container">
      <div class="document-list-header">
        <h3>文档列表</h3>
        <span class="document-count">共{{ documentTotal }}个文档</span>
      </div>

      <!-- 全部文档视图（表格形式） -->
      <div v-if="documentViewMode === 'all'" v-loading="documentLoading">
        <el-table 
          :data="documentList" 
          style="width: 100%"
          stripe>
          <el-table-column prop="title" label="文档标题" min-width="250">
            <template slot-scope="scope">
              <i class="el-icon-document" style="margin-right: 8px; color: #54907a;"></i>
              <span>{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" min-width="120" align="center">
            <template slot-scope="scope">
              <el-tag 
                :type="getDocumentTypeTagType(scope.row.type)" 
                size="small">
                {{ scope.row.type }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="author" label="作者" min-width="100" align="center">
            <template slot-scope="scope">
              {{ scope.row.author }}
            </template>
          </el-table-column>
          <el-table-column prop="department" label="部门" min-width="120" align="center">
            <template slot-scope="scope">
              {{ scope.row.department }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="上传日期" min-width="120" align="center">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="大小" min-width="100" align="center">
            <template slot-scope="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" min-width="100" align="center">
            <template slot-scope="scope">
              <el-tag 
                v-if="scope.row.status === 'shared'"
                type="success" 
                size="small"
                class="status-tag">
                <i class="el-icon-share"></i>
                共享
              </el-tag>
              <el-tag 
                v-else
                type="info" 
                size="small"
                class="status-tag">
                <i class="el-icon-lock"></i>
                私有
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="200" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button 
                type="text" 
                size="small" 
                icon="el-icon-view"
                @click="handleViewDocument(scope.row)">
                查看
              </el-button>
              <el-button 
                v-if="canEditDocument(scope.row)"
                type="text" 
                size="small" 
                icon="el-icon-edit"
                @click="handleEditDocument(scope.row)">
                编辑
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                icon="el-icon-download"
                @click="handleDownloadDocument(scope.row)">
                下载
              </el-button>
              <el-button 
                v-if="canDeleteDocument(scope.row)"
                type="text" 
                size="small" 
                icon="el-icon-delete"
                style="color: #F56C6C;"
                @click="handleDeleteDocument(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="pagination-container" style="margin-top: 20px; text-align: right;">
          <el-pagination
            @size-change="handleDocumentPageSizeChange"
            @current-change="handleDocumentPageChange"
            :current-page="documentQueryParams.pageNo"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="documentQueryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="documentTotal">
          </el-pagination>
        </div>
      </div>

      <!-- 按人员分类视图 -->
      <div v-else-if="documentViewMode === 'personnel'" v-loading="documentLoading">
        <div v-if="groupedByPersonnel.length === 0" class="empty-state">
          <i class="el-icon-document" style="font-size: 48px; color: #C0C4CC;"></i>
          <p>暂无数据</p>
        </div>
        <div v-else>
          <div 
            v-for="group in groupedByPersonnel" 
            :key="group.authorId || group.author"
            class="personnel-group">
            <div class="group-header">
              <span class="group-title">{{ group.author }}</span>
              <span class="group-count">{{ group.count }}个文档</span>
            </div>
            <div class="group-content">
              <div 
                v-for="doc in group.documents" 
                :key="doc.id"
                class="document-item">
                <div class="document-info">
                  <i class="el-icon-document" style="color: #54907a; margin-right: 8px;"></i>
                  <div class="document-details">
                    <div class="document-title">{{ doc.title }}</div>
                    <div class="document-meta">
                      <span>{{ doc.department }}</span>
                      <span class="separator">·</span>
                      <span>{{ formatDate(doc.createTime) }}</span>
                      <span class="separator">·</span>
                      <span>{{ formatFileSize(doc.fileSize) }}</span>
                    </div>
                  </div>
                </div>
                <div class="document-actions">
                  <el-tag 
                    :type="getDocumentTypeTagType(doc.type)" 
                    size="small"
                    style="margin-right: 8px;">
                    {{ doc.type }}
                  </el-tag>
                  <el-button 
                    v-if="doc.status === 'shared'"
                    type="text" 
                    size="small" 
                    icon="el-icon-share"
                    @click="handleShareDocument(doc)">
                    共享
                  </el-button>
                  <el-button 
                    type="text" 
                    size="small" 
                    icon="el-icon-download"
                    @click="handleDownloadDocument(doc)">
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 分页 -->
        <div class="pagination-container" style="margin-top: 20px; text-align: right;">
          <el-pagination
            @size-change="handleDocumentPageSizeChange"
            @current-change="handleDocumentPageChange"
            :current-page="documentQueryParams.pageNo"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="documentQueryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="documentTotal">
          </el-pagination>
        </div>
      </div>

      <!-- 按业务分类视图 -->
      <div v-else-if="documentViewMode === 'business'" v-loading="documentLoading">
        <div v-if="groupedByBusiness.length === 0" class="empty-state">
          <i class="el-icon-document" style="font-size: 48px; color: #C0C4CC;"></i>
          <p>暂无数据</p>
        </div>
        <div v-else>
          <div 
            v-for="group in groupedByBusiness" 
            :key="group.type"
            class="business-group">
            <div class="group-header">
              <span class="group-title">{{ group.type }}</span>
              <span class="group-count">{{ group.count }}个文档</span>
            </div>
            <div class="group-content">
              <div 
                v-for="doc in group.documents" 
                :key="doc.id"
                class="document-item">
                <div class="document-info">
                  <i class="el-icon-document" style="color: #54907a; margin-right: 8px;"></i>
                  <div class="document-details">
                    <div class="document-title">{{ doc.title }}</div>
                    <div class="document-meta">
                      <span>{{ doc.author }}</span>
                      <span class="separator">·</span>
                      <span>{{ doc.department }}</span>
                      <span class="separator">·</span>
                      <span>{{ formatDate(doc.createTime) }}</span>
                      <span class="separator">·</span>
                      <span>{{ formatFileSize(doc.fileSize) }}</span>
                    </div>
                  </div>
                </div>
                <div class="document-actions">
                  <el-button 
                    v-if="doc.status === 'shared'"
                    type="text" 
                    size="small" 
                    icon="el-icon-share"
                    @click="handleShareDocument(doc)">
                    共享
                  </el-button>
                  <el-button 
                    v-else
                    type="text" 
                    size="small" 
                    icon="el-icon-download"
                    @click="handleDownloadDocument(doc)">
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 分页 -->
        <div class="pagination-container" style="margin-top: 20px; text-align: right;">
          <el-pagination
            @size-change="handleDocumentPageSizeChange"
            @current-change="handleDocumentPageChange"
            :current-page="documentQueryParams.pageNo"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="documentQueryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="documentTotal">
          </el-pagination>
        </div>
      </div>
    </div>

    <!-- 文档统计卡片 -->
    <div class="document-statistics-section">
      <div class="stat-card">
        <div class="stat-icon" style="background: #54907a;">
          <i class="el-icon-document"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ documentStatistics.total || 0 }}</div>
          <div class="stat-label">全部文档</div>
        </div>
        <div class="stat-title">文档总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #67C23A;">
          <i class="el-icon-share"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ documentStatistics.shared || 0 }}</div>
          <div class="stat-label">可协作编辑</div>
        </div>
        <div class="stat-title">共享文档</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #E6A23C;">
          <i class="el-icon-user"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ documentStatistics.myDocuments || 0 }}</div>
          <div class="stat-label">我创建的</div>
        </div>
        <div class="stat-title">我的文档</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #F56C6C;">
          <i class="el-icon-folder-add"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ documentStatistics.thisMonth || 0 }}</div>
          <div class="stat-label">12月上传</div>
        </div>
        <div class="stat-title">本月新增</div>
      </div>
    </div>

    <!-- 上传文档对话框 -->
    <el-dialog 
      title="上传新文档" 
      :visible.sync="uploadDialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="resetUploadForm">
      <div class="upload-dialog-subtitle">上传文档并设置分类和共享权限</div>
      
      <el-form 
        ref="uploadForm" 
        :model="uploadForm" 
        :rules="uploadRules" 
        label-width="100px"
        label-position="left">
        <el-form-item label="文档标题" prop="title">
          <el-input 
            v-model="uploadForm.title" 
            placeholder="请输入文档标题"
            maxlength="100"
            show-word-limit>
          </el-input>
        </el-form-item>

        <el-form-item label="文档类型" prop="type">
          <el-select 
            v-model="uploadForm.type" 
            placeholder="选择文档类型"
            style="width: 100%;">
            <el-option label="申报文档" value="申报文档"></el-option>
            <el-option label="设计文档" value="设计文档"></el-option>
            <el-option label="实验文档" value="实验文档"></el-option>
            <el-option label="工作计划" value="工作计划"></el-option>
            <el-option label="报告文档" value="报告文档"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="文档描述" prop="description">
          <el-input 
            v-model="uploadForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="简要描述文档内容"
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>

        <el-form-item label="文件上传">
          <el-upload
            ref="upload"
            class="document-uploader"
            drag
            :action="uploadAction"
            :headers="uploadHeaders"
            :data="uploadData"
            :before-upload="beforeUpload"
            :on-change="handleFileChange"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-progress="handleUploadProgress"
            :file-list="uploadFileList"
            :limit="1"
            :auto-upload="false">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">点击或拖拽文件到此处上传</div>
            <div class="el-upload__tip" slot="tip">
              支持 DOC, DOCX, PDF, XLS, XLSX, PPT, PPTX 等格式, 最大 50MB
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="共享设置">
          <el-checkbox v-model="uploadForm.isShared">设为共享文档</el-checkbox>
          <div class="form-item-tip">勾选后，所有用户都可以查看和编辑此文档</div>
        </el-form-item>

        <el-form-item label="协作者 (可选)">
          <el-input 
            v-model="uploadForm.collaborators" 
            placeholder="输入协作者姓名,用逗号分隔">
          </el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="uploading"
          @click="handleSubmitUpload">
          上传文档
        </el-button>
      </div>
    </el-dialog>

    <!-- WPS文档编辑器对话框 -->
    <el-dialog 
      :title="wpsDialogTitle"
      :visible.sync="wpsDialogVisible"
      width="95%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="true"
      class="wps-dialog"
      @close="handleWPSDialogClose">
      <div class="wps-dialog-content">
        <WPSEditor
          v-if="wpsDialogVisible && currentDocument"
          :document-id="currentDocument.id"
          :file-url="currentDocument.fileUrl"
          :mode="wpsMode"
          :app-id="wpsAppId"
          @loaded="handleWPSEditorLoaded"
          @save="handleWPSEditorSave"
          @close="handleWPSEditorClose"
          @error="handleWPSEditorError"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getDocumentPage, 
  uploadDocument, 
  uploadDocumentFile,
  deleteDocument, 
  getDocumentStatistics,
  getWPSEditLink,
  getWPSViewLink
} from '@/api/oa/document'
import { listUser, listSimpleUsers, getUserProfile } from '@/api/system/user'
import { getInfo } from '@/api/login'
import { getAccessToken } from '@/utils/auth'
import WPSEditor from '@/components/WPSEditor/index.vue'

export default {
  name: 'DocumentTab',
  components: {
    WPSEditor
  },
  data() {
    return {
      // 文档列表
      documentList: [],
      documentLoading: false,
      documentTotal: 0,
      documentSearchKeyword: '',
      documentFilterType: '',
      documentFilterAuthor: '',
      documentViewMode: 'all',
      documentQueryParams: {
        pageNo: 1,
        pageSize: 10,
        keyword: '',
        type: '',
        authorId: '',
        viewMode: 'all'
      },
      documentStatistics: {
        total: 0,
        shared: 0,
        myDocuments: 0,
        thisMonth: 0
      },
      authorList: [],
      // 上传对话框
      uploadDialogVisible: false,
      uploading: false,
      uploadForm: {
        title: '',
        type: '',
        description: '',
        file: null,
        fileUrl: '',
        fileId: null,
        fileSize: 0,
        isShared: false,
        collaborators: ''
      },
      uploadRules: {
        title: [
          { required: true, message: '请输入文档标题', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择文档类型', trigger: 'change' }
        ]
      },
      uploadFileList: [],
      uploadAction: process.env.VUE_APP_BASE_API + '/admin-api/infra/file/upload',
      uploadData: {},
      // WPS编辑器对话框
      wpsDialogVisible: false,
      currentDocument: null,
      wpsMode: 'view', // 'view' 或 'edit'
      wpsAppId: process.env.VUE_APP_WPS_APP_ID || '',
    }
  },
  computed: {
    uploadHeaders() {
      const token = getAccessToken()
      return {
        Authorization: token ? 'Bearer ' + token : ''
      }
    },
    wpsDialogTitle() {
      if (!this.currentDocument) return '文档预览'
      const modeText = this.wpsMode === 'edit' ? '编辑' : '查看'
      return `${modeText} - ${this.currentDocument.title}`
    },
    // 按人员分组的文档列表
    groupedByPersonnel() {
      if (this.documentViewMode !== 'personnel' || !this.documentList || this.documentList.length === 0) {
        return []
      }
      
      const groups = new Map()
      this.documentList.forEach(doc => {
        const key = doc.authorId || doc.author || '未知'
        const authorName = doc.author || '未知'
        
        if (!groups.has(key)) {
          groups.set(key, {
            authorId: doc.authorId,
            author: authorName,
            count: 0,
            documents: []
          })
        }
        
        const group = groups.get(key)
        group.count++
        group.documents.push(doc)
      })
      
      // 转换为数组并按作者名称排序
      return Array.from(groups.values()).sort((a, b) => {
        return a.author.localeCompare(b.author, 'zh-CN')
      })
    },
    // 按业务类型分组的文档列表
    groupedByBusiness() {
      if (this.documentViewMode !== 'business' || !this.documentList || this.documentList.length === 0) {
        return []
      }
      
      const groups = new Map()
      this.documentList.forEach(doc => {
        const type = doc.type || '其他'
        
        if (!groups.has(type)) {
          groups.set(type, {
            type: type,
            count: 0,
            documents: []
          })
        }
        
        const group = groups.get(type)
        group.count++
        group.documents.push(doc)
      })
      
      // 转换为数组并按类型排序
      return Array.from(groups.values()).sort((a, b) => {
        return a.type.localeCompare(b.type, 'zh-CN')
      })
    }
  },
  mounted() {
    this.initData()
  },
  activated() {
    // 如果使用了 keep-alive，在组件激活时重新加载数据
    this.initData()
  },
  methods: {
    // 初始化数据
    async initData() {
      this.documentViewMode = 'all'
      this.documentQueryParams.pageNo = 1
      this.documentQueryParams.viewMode = 'all'
      await Promise.all([
        this.getDocumentList(),
        this.getDocumentStatistics()
      ])
      this.$nextTick(() => {
        this.getAuthorList()
      })
    },
    // 显示上传对话框
    showUploadDialog() {
      this.uploadDialogVisible = true
    },
    // 重置上传表单
    resetUploadForm() {
      this.$refs.uploadForm && this.$refs.uploadForm.resetFields()
      this.uploadForm = {
        title: '',
        type: '',
        description: '',
        file: null,
        fileUrl: '',
        fileId: null,
        fileSize: 0,
        isShared: false,
        collaborators: ''
      }
      this.uploadFileList = []
      this.uploading = false
    },
    // 上传前验证
    beforeUpload(file) {
      const isValidType = ['doc', 'docx', 'pdf', 'xls', 'xlsx', 'ppt', 'pptx', 'jpg', 'jpeg', 'png', 'gif'].includes(
        file.name.split('.').pop().toLowerCase()
      )
      const isLt50M = file.size / 1024 / 1024 < 50

      if (!isValidType) {
        this.$message.error('不支持的文件格式！')
        return false
      }
      if (!isLt50M) {
        this.$message.error('文件大小不能超过 50MB！')
        return false
      }
      this.uploadForm.file = file
      return true
    },
    // 上传进度
    handleUploadProgress(event, file, fileList) {
      // 可以在这里显示上传进度
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
      
      // 更新文件列表
      if (fileList && fileList.length > 0) {
        this.uploadFileList = fileList
        const currentFile = fileList.find(f => f.uid === file.uid) || file
        if (currentFile && !currentFile.response) {
          currentFile.response = response
        }
      }
      
      if (fileUrl) {
        this.uploadForm.fileUrl = fileUrl
        this.uploadForm.fileId = fileId
        this.uploadForm.fileSize = file.size || 0
        this.uploading = false
        this.$message.success('文件上传成功')
      } else {
        this.uploading = false
        this.$message.error(response?.msg || '文件上传失败，无法获取文件地址')
      }
    },
    // 上传失败
    handleUploadError(err, file, fileList) {
      this.uploading = false
      let errorMsg = '文件上传失败，请重试'
      
      if (err && err.response) {
        const errorData = err.response.data || err.response
        if (errorData && errorData.msg) {
          errorMsg = errorData.msg
        } else if (errorData && errorData.message) {
          errorMsg = errorData.message
        }
      } else if (err && err.message) {
        errorMsg = err.message
      } else if (typeof err === 'string') {
        errorMsg = err
      }
      
      this.$message.error(errorMsg)
    },
    
    // 获取文档列表
    async getDocumentList() {
      this.documentLoading = true
      try {
        const params = {
          pageNo: this.documentQueryParams.pageNo,
          pageSize: this.documentQueryParams.pageSize,
          keyword: this.documentSearchKeyword || '',
          type: this.documentFilterType || '',
          authorId: this.documentFilterAuthor || '',
          viewMode: this.documentViewMode || 'all'
        }
        
        const res = await getDocumentPage(params)
        
        if (res.code === 200 || res.code === 0) {
          this.documentList = res.data?.list || []
          this.documentTotal = res.data?.total || 0
        } else {
          this.$message.error(res.msg || '获取文档列表失败')
        }
      } catch (error) {
        this.$message.error('获取文档列表失败: ' + (error.message || '未知错误'))
      } finally {
        this.documentLoading = false
      }
    },
    
    // 获取文档统计
    async getDocumentStatistics() {
      try {
        const res = await getDocumentStatistics()
        if (res.code === 200 || res.code === 0) {
          this.documentStatistics = res.data || {
            total: 0,
            shared: 0,
            myDocuments: 0,
            thisMonth: 0
          }
        }
      } catch (error) {
        // 静默处理
      }
    },
    
    // 获取作者列表
    async getAuthorList() {
      try {
        let roles = []
        let userId = this.$store.getters.userId
        let isAdmin = false
        
        try {
          const permissionRes = await getInfo()
          
          if (permissionRes && permissionRes.data) {
            const data = permissionRes.data
            roles = data.roles || []
            userId = data.user?.id || userId
            
            if (Array.isArray(roles) && roles.length > 0) {
              isAdmin = roles.some(role => {
                if (typeof role === 'string') {
                  const roleCode = role.toLowerCase()
                  return roleCode === 'admin' || 
                         roleCode === 'super_admin' || 
                         roleCode === 'super_administrator' ||
                         roleCode.includes('admin')
                }
                if (typeof role === 'object' && role !== null) {
                  const roleCode = (role.code || role.roleCode || role.name || '').toLowerCase()
                  return roleCode === 'admin' || 
                         roleCode === 'super_admin' || 
                         roleCode === 'super_administrator' ||
                         roleCode.includes('admin')
                }
                return false
              })
            }
          } else {
            roles = this.$store.getters.roles || []
            if (Array.isArray(roles) && roles.length > 0) {
              isAdmin = roles.some(role => {
                if (typeof role === 'string') {
                  return role.toLowerCase().includes('admin')
                }
                if (typeof role === 'object' && role !== null) {
                  const roleCode = (role.code || role.roleCode || role.name || '').toLowerCase()
                  return roleCode.includes('admin')
                }
                return false
              })
            }
          }
        } catch (error) {
          roles = this.$store.getters.roles || []
          if (Array.isArray(roles) && roles.length > 0) {
            isAdmin = roles.some(role => {
              if (typeof role === 'string') {
                return role.toLowerCase().includes('admin')
              }
              if (typeof role === 'object' && role !== null) {
                const roleCode = (role.code || role.roleCode || role.name || '').toLowerCase()
                return roleCode.includes('admin')
              }
              return false
            })
          }
        }
        
        let users = []
        
        if (isAdmin) {
          const res = await listSimpleUsers()
          if (res.code === 200 || res.code === 0) {
            users = res.data || []
          }
        } else {
          try {
            const userInfoRes = await getUserProfile()
            if (userInfoRes.code === 200 || userInfoRes.code === 0) {
              const userInfo = userInfoRes.data || {}
              const deptId = userInfo.deptId
              
              if (deptId) {
                const res = await listUser({
                  deptId: deptId,
                  status: 0,
                  pageNo: 1,
                  pageSize: 1000
                })
                if (res.code === 200 || res.code === 0) {
                  users = res.data?.list || []
                }
              }
            }
          } catch (profileError) {
            // 静默处理
          }
          
          if (users.length === 0) {
            const authors = new Map()
            this.documentList.forEach(doc => {
              if (doc.author && doc.authorId) {
                authors.set(doc.authorId, { id: doc.authorId, name: doc.author })
              }
            })
            users = Array.from(authors.values())
          }
        }
        
        this.authorList = users.map(user => ({
          id: user.id,
          name: user.nickname || user.username || user.name || '未知用户'
        }))
        
        const uniqueAuthors = new Map()
        this.authorList.forEach(author => {
          if (author.id && !uniqueAuthors.has(author.id)) {
            uniqueAuthors.set(author.id, author)
          }
        })
        this.authorList = Array.from(uniqueAuthors.values())
        
        this.authorList.sort((a, b) => {
          return (a.name || '').localeCompare(b.name || '', 'zh-CN')
        })
      } catch (error) {
        const authors = new Map()
        this.documentList.forEach(doc => {
          if (doc.author && doc.authorId) {
            authors.set(doc.authorId, { id: doc.authorId, name: doc.author })
          }
        })
        this.authorList = Array.from(authors.values())
        this.$message.warning('获取用户列表失败，已从文档列表中提取作者')
      }
    },
    
    // 文档搜索
    handleDocumentSearch() {
      this.documentQueryParams.pageNo = 1
      this.getDocumentList()
    },
    
    // 文档筛选
    handleDocumentFilter() {
      this.documentQueryParams.pageNo = 1
      this.getDocumentList()
    },
    
    // 文档视图切换
    handleDocumentViewChange(tab) {
      this.documentViewMode = tab.name
      this.documentQueryParams.pageNo = 1
      this.getDocumentList()
    },
    
    // 文档分页大小改变
    handleDocumentPageSizeChange(size) {
      this.documentQueryParams.pageSize = size
      this.documentQueryParams.pageNo = 1
      this.getDocumentList()
    },
    
    // 文档分页页码改变
    handleDocumentPageChange(page) {
      this.documentQueryParams.pageNo = page
      this.getDocumentList()
    },
    
    // 共享文档
    handleShareDocument(doc) {
      // 如果已经是共享状态，可以显示共享信息
      if (doc.status === 'shared') {
        this.$message.info('该文档已经是共享状态')
      } else {
        // 可以在这里实现共享功能
        this.$message.info('共享功能待实现')
      }
    },
    // 查看文档 - 在新窗口打开
    async handleViewDocument(doc) {
      if (!doc.fileUrl) {
        this.$message.warning('文档文件不存在')
        return
      }

      // 检查文件类型是否支持预览
      const fileExt = doc.fileUrl.split('.').pop().toLowerCase()
      const supportedTypes = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'pdf', 'txt', 'csv']
      
      if (!supportedTypes.includes(fileExt)) {
        this.$message.warning('该文件类型不支持在线预览，请下载后使用本地软件打开')
        return
      }

      // 使用WebOffice SDK在对话框中打开
      this.currentDocument = doc
      this.wpsMode = 'view'
      this.wpsDialogVisible = true
    },
    // 编辑文档 - 使用WebOffice SDK
    async handleEditDocument(doc) {
      // 检查权限
      if (!this.canEditDocument(doc)) {
        this.$message.warning('您没有权限编辑此文档')
        return
      }

      if (!doc.fileUrl) {
        this.$message.warning('文档文件不存在')
        return
      }

      // 检查文件类型是否支持编辑
      const fileExt = doc.fileUrl.split('.').pop().toLowerCase()
      const editableTypes = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx']
      
      if (!editableTypes.includes(fileExt)) {
        this.$message.warning('该文件类型不支持在线编辑，请下载后使用本地软件编辑')
        return
      }

      // 使用WebOffice SDK在对话框中打开
      this.currentDocument = doc
      this.wpsMode = 'edit'
      this.wpsDialogVisible = true
    },
    // 下载文档
    handleDownloadDocument(doc) {
      const link = document.createElement('a')
      link.href = doc.fileUrl
      link.download = doc.title
      link.click()
    },
    // 删除文档
    async handleDeleteDocument(doc) {
      this.$confirm('确定要删除该文档吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteDocument(doc.id)
          if (res.code === 200 || res.code === 0) {
            this.$message.success('删除成功')
            this.getDocumentList()
            this.getDocumentStatistics()
          } else {
            this.$message.error(res.msg || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败，请稍后重试')
        }
      }).catch(() => {})
    },
    // 判断是否可以编辑
    canEditDocument(doc) {
      // 如果是共享文档，所有人都可以编辑
      if (doc.status === 'shared') {
        return true
      }
      // 如果是私有文档，只有创建者可以编辑
      const currentUserId = this.$store.getters.userId || this.$store.getters.userInfo?.id
      return doc.authorId === currentUserId
    },
    // 判断是否可以删除
    canDeleteDocument(doc) {
      // 只有创建者可以删除
      const currentUserId = this.$store.getters.userId || this.$store.getters.userInfo?.id
      return doc.authorId === currentUserId
    },
    
    // 文件列表变化
    handleFileChange(file, fileList) {
      this.uploadFileList = fileList
      // 如果文件被移除，清空fileUrl
      if (fileList.length === 0) {
        this.uploadForm.fileUrl = ''
        this.uploadForm.fileId = null
        this.uploadForm.fileSize = 0
      }
    },
    
    // 提交上传
    async handleSubmitUpload() {
      // 先验证表单字段（标题、类型等）
      this.$refs.uploadForm.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        // 如果uploadFileList为空，尝试从组件实例获取文件列表
        if (this.uploadFileList.length === 0 && this.$refs.upload) {
          const uploadFiles = this.$refs.upload.fileList || []
          if (uploadFiles.length > 0) {
            this.uploadFileList = uploadFiles
          }
        }
        
        // 如果fileUrl为空，尝试从uploadFileList中提取
        if (!this.uploadForm.fileUrl && this.uploadFileList.length > 0) {
          let successFile = this.uploadFileList.find(f => f.status === 'success' && f.response)
          if (!successFile) {
            successFile = this.uploadFileList.find(f => f.response)
          }
          if (!successFile && this.uploadFileList.length > 0) {
            successFile = this.uploadFileList[0]
          }
          
          if (successFile) {
            let fileUrl = ''
            let fileId = null
            const response = successFile.response || successFile
            
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
              this.uploadForm.fileUrl = fileUrl
              this.uploadForm.fileId = fileId
              this.uploadForm.fileSize = successFile.size || 0
            }
          }
        }
        
        // 最终检查：如果还是没有fileUrl
        if (!this.uploadForm.fileUrl) {
          if (this.uploadFileList.length === 0) {
            this.$message.warning('请先上传文件')
            return false
          }
          
          const hasUploadingFile = this.uploadFileList.some(f => f.status === 'uploading')
          if (hasUploadingFile) {
            this.$message.warning('文件正在上传中，请稍候...')
            return false
          }
          
          const hasFailedFile = this.uploadFileList.some(f => f.status === 'fail')
          if (hasFailedFile) {
            this.$message.warning('文件上传失败，请重新上传')
            return false
          }
          
          // 尝试手动触发上传
          this.uploading = true
          try {
            this.$refs.upload.submit()
            let waitCount = 0
            while (!this.uploadForm.fileUrl && waitCount < 100) {
              await new Promise(resolve => setTimeout(resolve, 100))
              waitCount++
            }
          } catch (error) {
            // 即使上传失败，也尝试提交
          } finally {
            this.uploading = false
          }
        }

        // 提交文档信息
        let finalFileUrl = this.uploadForm.fileUrl
        let finalFileId = this.uploadForm.fileId
        let finalFileSize = this.uploadForm.fileSize
        
        if (!finalFileUrl && this.uploadFileList.length > 0) {
          const file = this.uploadFileList[0]
          finalFileSize = file.size || 0
        }
        
        if (!finalFileUrl && this.uploadFileList.length === 0) {
          this.$message.warning('请先上传文件')
          return false
        }
        
        if (!finalFileUrl) {
          this.$message.warning('文件尚未上传成功，请等待上传完成')
          return false
        }
        
        this.uploading = true
        try {
          const res = await uploadDocument({
            title: this.uploadForm.title,
            type: this.uploadForm.type,
            description: this.uploadForm.description,
            fileUrl: finalFileUrl,
            fileId: finalFileId,
            fileSize: finalFileSize,
            status: this.uploadForm.isShared ? 'shared' : 'private',
            collaborators: this.uploadForm.collaborators
          })

          if (res.code === 200 || res.code === 0) {
            this.$message.success('文档上传成功')
            this.uploadDialogVisible = false
            this.resetUploadForm()
            this.getDocumentList()
            this.getDocumentStatistics()
          } else {
            this.$message.error(res.msg || '文档上传失败')
          }
        } catch (error) {
          if (error.response && error.response.status === 404) {
            this.$message.warning('文档上传接口尚未实现，请联系后端开发人员实现 /fish/zk-document/upload 接口')
          } else if (error.response && error.response.data && error.response.data.msg) {
            this.$message.error(error.response.data.msg)
          } else {
            this.$message.error('文档上传失败，请稍后重试')
          }
        } finally {
          this.uploading = false
        }
      })
    },
    
    // WPS编辑器相关方法
    handleWPSEditorLoaded() {
      this.$message.success('文档加载成功')
    },
    handleWPSEditorSave(data) {
      this.$message.success('文档已保存')
      // 可以在这里刷新文档列表
      this.getDocumentList()
    },
    handleWPSEditorClose() {
      this.wpsDialogVisible = false
      this.currentDocument = null
    },
    handleWPSEditorError(error) {
      this.$message.error(error.message || '编辑器加载失败')
    },
    handleWPSDialogClose() {
      this.currentDocument = null
    },
    
    // 格式化文件大小
    formatFileSize(bytes) {
      if (bytes === null || bytes === undefined || bytes === '') return '0 B'
      
      // 转换为数字
      const size = typeof bytes === 'string' ? parseFloat(bytes) : Number(bytes)
      
      if (isNaN(size) || size < 0) return '0 B'
      if (size === 0) return '0 B'
      
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
      const i = Math.floor(Math.log(size) / Math.log(k))
      const value = Math.round(size / Math.pow(k, i) * 100) / 100
      
      return value + ' ' + sizes[Math.min(i, sizes.length - 1)]
    },
    
    // 格式化日期（只显示日期部分 yyyy-MM-dd）
    formatDate(date) {
      if (!date) return '--'
      
      // 处理时间戳（毫秒或秒）
      if (typeof date === 'number') {
        // 判断是毫秒还是秒（毫秒通常是13位，秒是10位）
        let timestamp = date
        if (date < 10000000000) {
          // 如果是秒，转换为毫秒
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
      
      // 处理数组格式 [year, month, day]（LocalDate 的 JSON 序列化格式）
      if (Array.isArray(date) && date.length >= 3) {
        const year = String(date[0]).padStart(4, '0')
        const month = String(date[1]).padStart(2, '0')
        const day = String(date[2]).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      if (typeof date === 'string') {
        // 如果是 ISO 格式字符串，提取日期部分
        const datePart = date.split('T')[0]
        return datePart
      }
      
      // 如果是 Date 对象
      if (date instanceof Date) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      return String(date)
    },
    
    // 获取文档类型标签类型
    getDocumentTypeTagType(type) {
      const typeMap = {
        '申报文档': 'primary',
        '设计文档': 'success',
        '实验文档': 'warning',
        '工作计划': 'danger',
        '报告文档': 'info',
        '其他': ''
      }
      return typeMap[type] || ''
    }
  }
}
</script>

<style lang="scss" scoped>
// ========== 文档协同样式 ==========
.document-collaboration {
  width: 100%;

  .document-header {
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

    .upload-doc-btn {
      flex-shrink: 0;
      font-weight: 600 !important;
      background: #2f6b5a !important;
      border-color: #54907a !important;
      color: #fff !important;
      &:hover, &:focus {
        background: #3a8570 !important;
        border-color: #6fb59a !important;
      }
    }
  }

  .search-filter-bar {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: linear-gradient(135deg, rgba(15, 26, 38, 0.95) 0%, rgba(20, 35, 50, 0.95) 100%);
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(84, 144, 122, 0.3);
    border: 1px solid rgba(84, 144, 122, 0.2);
  }

  .document-tabs {
    margin-bottom: 20px;

    ::v-deep .el-tabs__header {
      margin: 0;
    }

    ::v-deep .el-tabs__nav-wrap {
      background: linear-gradient(135deg, rgba(15, 26, 38, 0.95) 0%, rgba(20, 35, 50, 0.95) 100%);
      padding: 0 20px;
      border-radius: 8px 8px 0 0;
      border: 1px solid rgba(84, 144, 122, 0.2);
      border-bottom: none;
    }

    ::v-deep .el-tabs__item {
      color: rgba(255, 255, 255, 0.7);
      font-size: 14px;

      &.is-active {
        color: #54907a;
      }

      &:hover {
        color: #54907a;
      }
    }

    ::v-deep .el-tabs__active-bar {
      background-color: #54907a;
    }
  }

  .document-list-container {
    background: linear-gradient(135deg, rgba(15, 26, 38, 0.95) 0%, rgba(20, 35, 50, 0.95) 100%);
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(84, 144, 122, 0.3);
    border: 1px solid rgba(84, 144, 122, 0.2);
    margin-bottom: 20px;

    .document-list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #ffffff;
        font-weight: 600;
      }

      .document-count {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.7);
      }
    }

    ::v-deep .el-table {
      background: transparent;
      color: #ffffff;

      &::before {
        background-color: rgba(84, 144, 122, 0.2);
      }

      .el-table__fixed-right {
        border-left: 1px solid rgba(84, 144, 122, 0.2);

        &::before {
          display: none;
        }

        th {
          background: rgba(10, 21, 32, 0.9);
        }

        td {
          background: rgba(10, 21, 32, 0.6);
        }

        tr:hover td {
          background: rgba(84, 144, 122, 0.1);
        }
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

    /* 状态标签统一样式 */
    .status-tag {
      i + span, i {
        margin-right: 2px;
      }
      ::v-deep & {
        padding: 2px 8px !important;
        font-size: 12px !important;
        height: 24px !important;
        line-height: 20px !important;
        i {
          margin-right: 2px;
          font-size: 12px;
        }
      }
    }

    /* 下载按钮样式 */
    ::v-deep .download-btn {
      border: 1px solid rgba(84, 144, 122, 0.5) !important;
      border-radius: 4px !important;
      padding: 4px 10px !important;
      color: #54907a !important;
      background: rgba(84, 144, 122, 0.1) !important;
      transition: all 0.3s;
      font-size: 12px !important;
      height: auto !important;
      line-height: 1 !important;

      &:hover {
        background: rgba(84, 144, 122, 0.25) !important;
        border-color: #54907a !important;
        box-shadow: 0 0 10px rgba(84, 144, 122, 0.4);
      }

      i {
        margin-right: 2px;
      }
    }
  }

  // 按人员分类和按业务分类的样式
  .personnel-group,
  .business-group {
    margin-bottom: 24px;
    background: rgba(10, 21, 32, 0.6);
    border-radius: 8px;
    border: 1px solid rgba(84, 144, 122, 0.2);
    overflow: hidden;

    .group-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      background: rgba(84, 144, 122, 0.1);
      border-bottom: 1px solid rgba(84, 144, 122, 0.2);

      .group-title {
        font-size: 16px;
        font-weight: 600;
        color: #ffffff;
      }

      .group-count {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.7);
      }
    }

    .group-content {
      padding: 12px 20px;
    }

    .document-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid rgba(84, 144, 122, 0.1);

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: rgba(84, 144, 122, 0.05);
        border-radius: 4px;
        padding-left: 8px;
        padding-right: 8px;
      }

      .document-info {
        display: flex;
        align-items: center;
        flex: 1;

        .document-details {
          flex: 1;

          .document-title {
            font-size: 15px;
            color: #ffffff;
            margin-bottom: 6px;
            cursor: pointer;

            &:hover {
              color: #54907a;
            }
          }

          .document-meta {
            font-size: 13px;
            color: rgba(255, 255, 255, 0.6);

            .separator {
              margin: 0 8px;
              color: rgba(255, 255, 255, 0.4);
            }
          }
        }
      }

      .document-actions {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: rgba(255, 255, 255, 0.5);

    p {
      margin-top: 16px;
      font-size: 14px;
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
        background: rgba(10, 21, 32, 0.6) !important;
        color: rgba(255, 255, 255, 0.8);
        border: 1px solid rgba(84, 144, 122, 0.3);

        &:hover {
          color: #54907a;
          border-color: #54907a;
        }
      }

      .number.active {
        background: #54907a !important;
        color: #ffffff;
        border-color: #54907a;
      }

      .el-select .el-input__inner {
        background: rgba(10, 21, 32, 0.6);
        color: rgba(255, 255, 255, 0.8);
        border: 1px solid rgba(84, 144, 122, 0.3);
      }

      span:not(.number):not(.btn-prev):not(.btn-next) {
        background: transparent !important;
      }
    }
  }

  .document-statistics-section {
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
}

// 上传对话框样式
.upload-dialog-subtitle {
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

  .el-checkbox__label {
    color: rgba(255, 255, 255, 0.9);
  }

  .form-item-tip {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    margin-top: 4px;
  }
}

.document-uploader {
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

// WPS编辑器对话框样式
.wps-dialog {
  ::v-deep .el-dialog {
    background: rgba(15, 26, 38, 0.95);
    border: 1px solid rgba(84, 144, 122, 0.3);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
    border-radius: 8px;
    height: 90vh;
    display: flex;
    flex-direction: column;
  }

  ::v-deep .el-dialog__header {
    background: rgba(10, 21, 32, 0.6);
    padding: 16px 20px;
    border-bottom: 1px solid rgba(84, 144, 122, 0.2);
    border-radius: 8px 8px 0 0;
  }

  ::v-deep .el-dialog__title {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
  }

  ::v-deep .el-dialog__close {
    color: rgba(255, 255, 255, 0.7);
    font-size: 20px;

    &:hover {
      color: #54907a;
    }
  }

  ::v-deep .el-dialog__body {
    padding: 0;
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .wps-dialog-content {
    width: 100%;
    height: 100%;
    min-height: 600px;
    display: flex;
    flex-direction: column;
  }
}
</style>

