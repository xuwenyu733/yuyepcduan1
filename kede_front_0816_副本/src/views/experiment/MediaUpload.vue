<template>
  <div class="media-page">
    <div class="page-card filter-bar">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="车间">
          <el-select v-model="filters.workshop" clearable placeholder="全部车间" style="width: 140px" @change="loadList">
            <el-option label="1号车间" value="1" />
            <el-option label="2号车间" value="2" />
            <el-option label="3号车间" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统">
          <el-select v-model="filters.system" clearable placeholder="全部系统" style="width: 140px" @change="loadList">
            <el-option label="1号系统" value="1" />
            <el-option label="2号系统" value="2" />
            <el-option label="3号系统" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="实验/文件名" style="width: 180px" @keyup.enter.native="loadList" @clear="loadList" />
        </el-form-item>
      </el-form>
    </div>

    <div class="page-card">
      <h2 class="section-title">多媒体上传</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="media-form">
        <el-form-item label="选择实验" prop="experimentId">
          <el-select v-model="form.experimentId" placeholder="请选择实验" filterable style="width: 100%">
            <el-option
              v-for="item in filteredExperiments"
              :key="item.id"
              :label="`${item.name}（${item.id}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="文件类型" prop="fileType">
              <el-select v-model="form.fileType" style="width: 100%">
                <el-option
                  v-for="item in meta.fileTypes"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="类别" prop="category">
              <el-select v-model="form.category" style="width: 100%" @change="form.subCategory = ''">
                <el-option
                  v-for="item in categories"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="子类别" prop="subCategory">
              <el-select v-model="form.subCategory" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="item in subCategories"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="池号" prop="poolNo">
              <el-input v-model="form.poolNo" placeholder="例如: A1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="添加备注信息..." />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            icon="el-icon-upload2"
            class="upload-btn"
            native-type="button"
            @click="openUploadDialog"
          >
            选择{{ form.fileType }}上传
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 自定义弹窗，避免 el-dialog 被遮挡/不显示 -->
    <div
      v-if="uploadDialogVisible"
      class="upload-modal-mask"
      @click.self="closeUploadDialog"
    >
      <div class="upload-modal" role="dialog" aria-modal="true">
        <div class="upload-modal__header">
          <h3 class="upload-modal__title">{{ filePickLabel }}</h3>
          <button type="button" class="upload-modal__close" @click="closeUploadDialog">
            <i class="el-icon-close" />
          </button>
        </div>
        <div class="upload-modal__body">
          <div class="file-picker">
            <input
              ref="fileInput"
              type="file"
              :accept="fileAccept"
              class="file-picker__input"
              @change="onFileSelect"
            />
            <template v-if="!selectedFile">
              <i class="el-icon-upload2 file-picker__icon" />
              <p class="file-picker__title">点击选择本地{{ form.fileType }}</p>
              <p class="file-picker__hint">{{ filePickHint }}</p>
            </template>
            <template v-else>
              <i :class="[selectedFileIcon, 'file-picker__icon is-selected']" />
              <p class="file-picker__title">{{ selectedFile.name }}</p>
              <p class="file-picker__hint">{{ formatSize(selectedFile.size) }} · 点击可重新选择</p>
            </template>
          </div>
        </div>
        <div class="upload-modal__footer">
          <el-button @click="closeUploadDialog">取消</el-button>
          <el-button
            type="primary"
            icon="el-icon-upload2"
            :loading="uploading"
            :disabled="!selectedFile"
            @click="handleUpload"
          >
            确认上传
          </el-button>
        </div>
      </div>
    </div>

    <div class="page-card list-card">
      <div class="list-header">
        <h3 class="section-title" style="margin: 0">媒体文件清单（{{ list.length }}）</h3>
        <el-select v-model="filterCategory" placeholder="全部类别" clearable style="width: 160px" @change="loadList">
          <el-option label="全部类别" value="" />
          <el-option v-for="item in allCategories" :key="item" :label="item" :value="item" />
        </el-select>
      </div>
      <el-empty v-if="!loading && list.length === 0" description="暂无媒体文件" />
      <el-table v-else v-loading="loading" :data="list" size="small" border>
        <el-table-column prop="fileName" label="文件名" min-width="160" />
        <el-table-column prop="experimentName" label="实验" min-width="140" />
        <el-table-column prop="fileType" label="类型" width="90" />
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column prop="subCategory" label="子类别" width="100" />
        <el-table-column prop="poolNo" label="池号" width="80" />
        <el-table-column prop="createdAt" label="上传时间" width="170" />
        <el-table-column label="操作" width="90" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { fetchExperiments } from '@/api/experiment'
import { fetchMediaMeta, fetchMediaList, uploadMedia, deleteMedia } from '@/api/experiment'

export default {
  name: 'MediaUpload',
  data() {
    return {
      loading: false,
      uploading: false,
      uploadDialogVisible: false,
      experiments: [],
      meta: { fileTypes: [], categories: {} },
      list: [],
      allList: [],
      filterCategory: '',
      filters: {
        workshop: '',
        system: '',
        keyword: ''
      },
      selectedFile: null,
      form: {
        experimentId: '',
        fileType: '图片',
        category: '体表',
        subCategory: '',
        poolNo: '',
        remark: ''
      },
      rules: {
        experimentId: [{ required: true, message: '请选择实验', trigger: 'change' }],
        fileType: [{ required: true, message: '请选择文件类型', trigger: 'change' }],
        category: [{ required: true, message: '请选择类别', trigger: 'change' }]
      }
    }
  },
  computed: {
    filteredExperiments() {
      const keyword = (this.filters.keyword || '').trim().toLowerCase()
      return this.experiments.filter(item => {
        if (this.filters.workshop && item.workshop !== this.filters.workshop) return false
        if (this.filters.system && item.system !== this.filters.system) return false
        if (keyword) {
          const text = `${item.name || ''} ${item.id || ''}`.toLowerCase()
          if (!text.includes(keyword)) return false
        }
        return true
      })
    },
    categories() {
      return Object.keys(this.meta.categories || {})
    },
    subCategories() {
      return (this.meta.categories && this.meta.categories[this.form.category]) || []
    },
    allCategories() {
      return this.categories
    },
    fileAccept() {
      return this.form.fileType === '视频' ? 'video/*' : 'image/*'
    },
    filePickLabel() {
      return this.form.fileType === '视频' ? '选择本地视频' : '选择本地图片'
    },
    filePickHint() {
      return this.form.fileType === '视频'
        ? '支持 mp4/mov/avi/mkv/webm 等常见视频，大小不超过 500MB'
        : '支持 jpg、png、gif、webp 等常见图片，大小不超过 20MB'
    },
    uploadMaxSizeMb() {
      return this.form.fileType === '视频' ? 500 : 20
    },
    selectedFileIcon() {
      return this.form.fileType === '视频' ? 'el-icon-video-camera' : 'el-icon-picture-outline'
    }
  },
  watch: {
    'form.fileType'() {
      this.clearSelectedFile()
    }
  },
  created() {
    this.init()
  },
  beforeDestroy() {
    document.body.style.overflow = ''
  },
  methods: {
    async init() {
      const [expRes, metaRes] = await Promise.all([
        fetchExperiments({}),
        fetchMediaMeta()
      ])
      this.experiments = expRes.data
      this.meta = metaRes.data
      if (this.categories.length) {
        this.form.category = this.categories[0]
      }
      await this.loadList()
    },
    async loadList() {
      this.loading = true
      try {
        const res = await fetchMediaList({ category: this.filterCategory })
        this.allList = res.data
        this.applyListFilters()
      } finally {
        this.loading = false
      }
    },
    applyListFilters() {
      const keyword = (this.filters.keyword || '').trim().toLowerCase()
      const expMap = {}
      this.experiments.forEach(item => {
        expMap[item.id] = item
      })
      this.list = this.allList.filter(row => {
        const exp = expMap[row.experimentId]
        if (this.filters.workshop && (!exp || exp.workshop !== this.filters.workshop)) return false
        if (this.filters.system && (!exp || exp.system !== this.filters.system)) return false
        if (keyword) {
          const text = `${row.experimentName || ''} ${row.fileName || ''}`.toLowerCase()
          if (!text.includes(keyword)) return false
        }
        return true
      })
    },
    openUploadDialog() {
      this.selectedFile = null
      this.uploadDialogVisible = true
      document.body.style.overflow = 'hidden'
      this.$nextTick(() => {
        if (this.$refs.fileInput) {
          this.$refs.fileInput.value = ''
        }
      })
    },
    closeUploadDialog() {
      this.uploadDialogVisible = false
      document.body.style.overflow = ''
      this.clearSelectedFile()
    },
    clearSelectedFile() {
      this.selectedFile = null
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = ''
      }
    },
    onFileSelect(e) {
      const file = e.target.files && e.target.files[0]
      if (!file) return
      const ext = (file.name.split('.').pop() || '').toLowerCase()
      const videoExts = ['mp4', 'mov', 'avi', 'mkv', 'wmv', 'flv', 'webm', 'm4v', 'mpeg', 'mpg', '3gp', 'ts']
      const imageExts = ['png', 'jpg', 'jpeg', 'gif', 'bmp', 'webp']
      const isVideo = file.type.startsWith('video/') || videoExts.includes(ext)
      const isImage = file.type.startsWith('image/') || imageExts.includes(ext)
      if (this.form.fileType === '视频' && !isVideo) {
        this.$message.warning('请选择常见视频格式（mp4/mov/avi/mkv/webm 等）')
        this.clearSelectedFile()
        return
      }
      if (this.form.fileType === '图片' && !isImage) {
        this.$message.warning('请选择图片文件')
        this.clearSelectedFile()
        return
      }
      const maxMb = this.uploadMaxSizeMb
      if (file.size / 1024 / 1024 > maxMb) {
        this.$message.warning(`文件大小不能超过 ${maxMb}MB`)
        this.clearSelectedFile()
        return
      }
      this.selectedFile = file
    },
    formatSize(size) {
      if (size < 1024) return `${size} B`
      if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
      return `${(size / 1024 / 1024).toFixed(1)} MB`
    },
    handleUpload() {
      if (!this.selectedFile) {
        this.$message.warning(`请先选择本地${this.form.fileType}`)
        return
      }
      this.$refs.formRef.validate(async valid => {
        if (!valid) {
          this.$message.warning('请先完善表单必填项（如选择实验）')
          this.closeUploadDialog()
          return
        }
        this.uploading = true
        try {
          const exp = this.experiments.find(item => item.id === this.form.experimentId)
          await uploadMedia({
            ...this.form,
            fileName: this.selectedFile.name,
            experimentName: exp ? exp.name : '',
            size: this.selectedFile.size
          })
          this.$message.success('上传成功')
          this.closeUploadDialog()
          this.loadList()
        } finally {
          this.uploading = false
        }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定删除「${row.fileName}」吗？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteMedia(row.id)
          this.$message.success('删除成功')
          this.loadList()
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.filter-bar {
  margin-bottom: 16px;
}

.filter-form {
  margin: 0;
}

.media-form {
  width: 100%;
}

.upload-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
}

.upload-modal-mask {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(0, 0, 0, 0.45);
}

.upload-modal {
  width: 100%;
  max-width: 520px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
  overflow: hidden;
}

.upload-modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.upload-modal__title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
}

.upload-modal__close {
  border: none;
  background: transparent;
  color: #909399;
  font-size: 18px;
  cursor: pointer;
  line-height: 1;
  padding: 4px;
}

.upload-modal__close:hover {
  color: #606266;
}

.upload-modal__body {
  padding: 20px;
}

.upload-modal__footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px 20px;
}

.file-picker {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 168px;
  padding: 24px 16px;
  border: 1px dashed #c0c4cc;
  border-radius: 8px;
  background: #fafbfc;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}

.file-picker:hover {
  border-color: var(--primary, #2d5cf6);
  background: #f5f8ff;
}

.file-picker__input {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.file-picker__icon {
  font-size: 40px;
  color: #909399;
  margin-bottom: 12px;
}

.file-picker__icon.is-selected {
  color: var(--primary, #2d5cf6);
}

.file-picker__title {
  margin: 0;
  font-size: 15px;
  color: #303133;
  word-break: break-all;
  text-align: center;
}

.file-picker__hint {
  margin: 8px 0 0;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

.list-card {
  margin-top: 16px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  gap: 12px;
}

@media (max-width: 768px) {
  .list-header {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
