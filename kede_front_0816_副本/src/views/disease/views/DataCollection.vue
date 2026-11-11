<template>
  <div class="collect-page">
    <header class="collect-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">数据采集</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">全面收集病害诊断所需的视觉素材、基础信息、设备数据和解剖信息</p>
    </header>

    <div class="collect-body">
      <div class="collect-grid">
        <!-- 视觉素材上传 -->
        <section class="module-card">
        <h2 class="module-title">视觉素材上传</h2>
        <p class="module-desc">上传体表照片、解剖照片或异常行为视频</p>

        <div
          class="upload-zone"
          :class="{ 'is-dragover': dragOver }"
          @click="triggerFilePick"
          @dragover.prevent="dragOver = true"
          @dragleave.prevent="dragOver = false"
          @drop.prevent="onDrop"
        >
          <i class="el-icon-upload upload-icon" />
          <div class="upload-main">点击上传或拖拽文件至此</div>
          <div class="upload-hint">支持图片和视频格式</div>
          <input
            ref="fileInput"
            type="file"
            accept="image/*,video/*"
            multiple
            class="hidden-input"
            @change="onFileChange"
          >
        </div>

        <div v-if="mediaFiles.length" class="media-list">
          <div v-for="(file, idx) in mediaFiles" :key="file.id" class="media-item">
            <div class="media-thumb">
              <img v-if="file.preview && file.kind === 'image'" :src="file.preview" alt="">
              <i v-else class="el-icon-video-camera" />
            </div>
            <div class="media-meta">
              <div class="media-name">{{ file.name }}</div>
              <div class="media-size">{{ formatSize(file.size) }}</div>
            </div>
            <button type="button" class="media-remove" @click.stop="removeMedia(idx)">
              <i class="el-icon-close" />
            </button>
          </div>
        </div>

        <div class="action-row">
          <button type="button" class="action-btn" @click="triggerCapture('image')">
            <i class="el-icon-camera" /> 拍照
          </button>
          <button type="button" class="action-btn" @click="triggerCapture('video')">
            <i class="el-icon-video-camera" /> 录像
          </button>
        </div>
        <input
          ref="captureInput"
          type="file"
          class="hidden-input"
          :accept="captureAccept"
          :capture="captureMode"
          @change="onCaptureChange"
        >
        </section>

        <!-- 基础信息 -->
        <section class="module-card">
        <h2 class="module-title">基础信息</h2>
        <p class="module-desc">填写鱼品种、养殖池等基本信息</p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" hide-required-asterisk class="basic-form">
          <el-form-item prop="species">
            <template slot="label">鱼品种 <span class="required-star">*</span></template>
            <el-select
              v-model="form.species"
              placeholder="选择鱼品种"
              style="width:100%"
              popper-class="config-select-dropdown"
            >
              <el-option v-for="s in speciesList" :key="s.id" :label="s.name" :value="s.name" />
            </el-select>
          </el-form-item>
          <el-form-item prop="pondCode">
            <template slot="label">养殖池编号 <span class="required-star">*</span></template>
            <el-input v-model="form.pondCode" placeholder="例如：A-01" />
          </el-form-item>
          <el-form-item label="鱼苗来源">
            <el-input v-model="form.frySource" placeholder="例如：福建育苗场" />
          </el-form-item>
          <el-form-item label="发病数量">
            <el-input v-model="form.diseaseCount" placeholder="例如：50" />
          </el-form-item>
          <el-form-item label="投喂方式">
            <el-select
              v-model="form.feedingMethod"
              placeholder="选择投喂方式"
              clearable
              style="width:100%"
              popper-class="config-select-dropdown"
            >
              <el-option v-for="m in feedingMethods" :key="m" :label="m" :value="m" />
            </el-select>
          </el-form-item>
        </el-form>
        </section>

        <!-- 设备监测数据 -->
        <section class="module-card">
        <h2 class="module-title">设备监测数据</h2>
        <p class="module-desc">从自动化设备同步的水质和行为数据</p>

        <div v-loading="monitorLoading" class="metric-list">
          <div
            v-for="item in metrics"
            :key="item.key"
            class="metric-card"
            :class="{ abnormal: item.abnormal }"
          >
            <div class="metric-main">
              <div class="metric-label">{{ item.label }}</div>
              <div class="metric-value">
                <span class="num">{{ item.value }}</span>
                <span v-if="item.unit" class="unit">{{ item.unit }}</span>
              </div>
              <div class="metric-range">正常范围：{{ item.rangeText }}</div>
            </div>
            <span v-if="item.abnormal" class="abnormal-badge">
              <span class="badge-icon">!</span> 异常
            </span>
          </div>

          <div v-if="hasAbnormal" class="water-alert">
            <span class="alert-icon">!</span>
            <span>检测到异常水质指标，请及时关注</span>
          </div>
        </div>
        </section>

        <!-- 解剖信息 -->
        <section class="module-card module-card--anatomy">
        <h2 class="module-title">解剖信息</h2>
        <p class="module-desc">勾选异常部位并描述具体表现</p>

        <div class="field-block">
          <div class="field-label">异常部位</div>
          <el-checkbox-group v-model="form.abnormalParts" class="part-checks">
            <el-checkbox v-for="part in anatomyParts" :key="part" :label="part">{{ part }}</el-checkbox>
          </el-checkbox-group>
        </div>

        <div class="field-block">
          <div class="field-label">异常表现描述</div>
          <el-input
            v-model="form.abnormalDesc"
            type="textarea"
            :rows="4"
            placeholder="详细描述观察到的异常表现，如：鳃部发白、肝脏肿大等"
            class="desc-input"
          />
        </div>
        </section>
      </div>

      <div class="footer-bar">
        <button type="button" class="btn-export" @click="handleExport">
          <i class="el-icon-upload2" /> 导出数据
        </button>
        <button type="button" class="btn-submit" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '提交中…' : '提交诊断申请' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchFishSpecies, fetchMonitorData, submitCollection } from '../api'
import MenuToggle from '../components/MenuToggle.vue'
import HeaderAuth from '../components/HeaderAuth.vue'

let mediaSeq = 0

export default {
  name: 'DataCollection',
  components: { MenuToggle, HeaderAuth },
  data() {
    return {
      dragOver: false,
      monitorLoading: false,
      submitting: false,
      captureAccept: 'image/*',
      captureMode: 'environment',
      speciesList: [],
      mediaFiles: [],
      metrics: [],
      feedingMethods: ['人工投喂', '自动投喂', '混合投喂'],
      anatomyParts: ['鳃部', '肝脏', '肠道', '体表', '其他'],
      form: {
        species: '',
        pondCode: '',
        frySource: '',
        diseaseCount: '',
        feedingMethod: '',
        abnormalParts: [],
        abnormalDesc: ''
      },
      rules: {
        species: [{ required: true, message: '请选择鱼品种', trigger: 'change' }],
        pondCode: [{ required: true, message: '请填写养殖池编号', trigger: 'blur' }]
      }
    }
  },
  computed: {
    todayText() {
      const now = new Date()
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日${weekDays[now.getDay()]}`
    },
    hasAbnormal() {
      return this.metrics.some(m => m.abnormal)
    }
  },
  created() {
    this.init()
  },
  beforeDestroy() {
    this.mediaFiles.forEach(f => {
      if (f.preview) URL.revokeObjectURL(f.preview)
    })
  },
  methods: {
    async init() {
      try {
        const [speciesRes] = await Promise.all([
          fetchFishSpecies(),
          this.loadMonitor()
        ])
        this.speciesList = speciesRes.data || []
      } catch (e) {
        this.$message.error('加载数据失败，请确认后端服务已启动（端口 3011）')
      }
    },
    async loadMonitor() {
      this.monitorLoading = true
      try {
        const res = await fetchMonitorData()
        this.metrics = res.data || []
      } catch (e) {
        this.metrics = []
      } finally {
        this.monitorLoading = false
      }
    },
    triggerFilePick() {
      this.$refs.fileInput.click()
    },
    triggerCapture(kind) {
      this.captureAccept = kind === 'video' ? 'video/*' : 'image/*'
      this.captureMode = 'environment'
      this.$nextTick(() => {
        this.$refs.captureInput.click()
      })
    },
    onFileChange(e) {
      this.addFiles(Array.from(e.target.files || []))
      e.target.value = ''
    },
    onCaptureChange(e) {
      this.addFiles(Array.from(e.target.files || []))
      e.target.value = ''
    },
    onDrop(e) {
      this.dragOver = false
      this.addFiles(Array.from(e.dataTransfer.files || []))
    },
    addFiles(files) {
      files.forEach(file => {
        if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) return
        const kind = file.type.startsWith('video/') ? 'video' : 'image'
        const preview = kind === 'image' ? URL.createObjectURL(file) : ''
        this.mediaFiles.push({
          id: `m-${Date.now()}-${mediaSeq++}`,
          name: file.name,
          size: file.size,
          kind,
          preview,
          file
        })
      })
    },
    removeMedia(idx) {
      const item = this.mediaFiles[idx]
      if (item && item.preview) URL.revokeObjectURL(item.preview)
      this.mediaFiles.splice(idx, 1)
    },
    formatSize(bytes) {
      if (bytes < 1024) return `${bytes} B`
      if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
      return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
    },
    handleExport() {
      const payload = {
        ...this.form,
        mediaCount: this.mediaFiles.length,
        mediaNames: this.mediaFiles.map(f => f.name),
        metrics: this.metrics,
        exportedAt: new Date().toISOString()
      }
      const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `数据采集_${Date.now()}.json`
      link.click()
      URL.revokeObjectURL(url)
      this.$message.success('数据已导出')
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) {
          this.$message.warning('请完善必填的基础信息')
          return
        }
        this.submitting = true
        try {
          await submitCollection({
            ...this.form,
            mediaCount: this.mediaFiles.length,
            mediaNames: this.mediaFiles.map(f => f.name),
            metrics: this.metrics
          })
          this.$message.success('诊断申请已提交')
          this.resetForm()
        } catch (e) {
          // 错误提示由 request 拦截器处理
        } finally {
          this.submitting = false
        }
      })
    },
    resetForm() {
      this.mediaFiles.forEach(f => {
        if (f.preview) URL.revokeObjectURL(f.preview)
      })
      this.mediaFiles = []
      this.form = {
        species: '',
        pondCode: '',
        frySource: '',
        diseaseCount: '',
        feedingMethod: '',
        abnormalParts: [],
        abnormalDesc: ''
      }
      this.$nextTick(() => {
        if (this.$refs.formRef) this.$refs.formRef.clearValidate()
      })
    }
  }
}
</script>

<style scoped>
.collect-page {
  min-height: 100vh;
  background: #f7f8fa;
  position: relative;
}

.collect-header {
  background: #fff;
  padding: 28px 40px 20px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.header-title-row {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}

.header-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1.25;
}

.header-date {
  font-size: 14px;
  color: #86909c;
  white-space: nowrap;
  padding-top: 6px;
}

.header-desc {
  margin: 10px 0 0;
  font-size: 14px;
  color: #86909c;
  line-height: 1.5;
}

.collect-body {
  padding: 0 40px 100px;
}

.collect-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.module-card {
  background: #fff;
  border: 1px solid #eef0f3;
  border-radius: 16px;
  padding: 28px;
}

.module-card--anatomy {
  display: flex;
  flex-direction: column;
}

.module-title {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}

.module-desc {
  margin: 0 0 20px;
  font-size: 14px;
  color: #86909c;
  line-height: 1.5;
}

.upload-zone {
  border: 1.5px dashed #91caff;
  border-radius: 12px;
  background: #fafcff;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
}

.upload-zone:hover,
.upload-zone.is-dragover {
  border-color: #69b1ff;
  background: #f0f7ff;
}

.upload-icon {
  font-size: 36px;
  color: #c0c4cc;
  margin-bottom: 12px;
}

.upload-main {
  font-size: 14px;
  color: #4e5969;
  margin-bottom: 6px;
}

.upload-hint {
  font-size: 12px;
  color: #c0c4cc;
}

.hidden-input {
  display: none;
}

.media-list {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.media-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border: 1px solid #eef0f3;
  border-radius: 8px;
  background: #fafbfc;
}

.media-thumb {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  background: #f2f3f5;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #86909c;
  font-size: 18px;
  flex-shrink: 0;
}

.media-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.media-meta {
  flex: 1;
  min-width: 0;
}

.media-name {
  font-size: 13px;
  color: #1d2129;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.media-size {
  margin-top: 2px;
  font-size: 12px;
  color: #86909c;
}

.media-remove {
  border: none;
  background: transparent;
  color: #86909c;
  cursor: pointer;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.media-remove:hover {
  background: #f2f3f5;
  color: #f53f3f;
}

.action-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 16px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 44px;
  border: 1px solid #e5e6eb;
  background: #fff;
  border-radius: 10px;
  font-size: 14px;
  color: #1d2129;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}

.action-btn:hover {
  background: #f7f8fa;
  border-color: #d0d3d9;
}

.basic-form >>> .el-form-item.is-required .el-form-item__label::before {
  display: none;
}

.required-star {
  color: #f53f3f;
  margin-left: 2px;
}

.basic-form >>> .el-form-item__label {
  padding: 0 0 8px;
  line-height: 1.4;
  color: #1d2129;
  font-size: 14px;
  font-weight: 500;
}

.basic-form >>> .el-form-item {
  margin-bottom: 18px;
}

.basic-form >>> .el-form-item:last-child {
  margin-bottom: 0;
}

.basic-form >>> .el-input__inner,
.basic-form >>> .el-select .el-input__inner {
  background: #f2f3f5;
  border: none;
  height: 44px;
  line-height: 44px;
  border-radius: 10px;
  color: #1d2129;
  padding-left: 14px;
}

.basic-form >>> .el-input__inner::placeholder,
.basic-form >>> .el-select .el-input__inner::placeholder {
  color: #c9cdd4;
}

.basic-form >>> .el-input__inner:focus,
.basic-form >>> .el-select .el-input.is-focus .el-input__inner {
  background: #eef0f3;
  border: none;
  box-shadow: none;
}

.basic-form >>> .el-select .el-input .el-select__caret {
  color: #c9cdd4;
  font-weight: 600;
}

.metric-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 40px;
}

.metric-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 18px 20px;
  border: 1px solid #eef0f3;
  border-radius: 12px;
  background: #fafbfc;
}

.metric-card.abnormal {
  background: #fffbe6;
  border-color: #ffe58f;
}

.metric-label {
  font-size: 13px;
  color: #86909c;
  margin-bottom: 8px;
}

.metric-value {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 8px;
}

.metric-value .num {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  line-height: 1.1;
}

.metric-value .unit {
  font-size: 14px;
  color: #86909c;
}

.metric-range {
  font-size: 12px;
  color: #86909c;
}

.abnormal-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 5px 12px;
  border-radius: 999px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  color: #d48806;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.badge-icon {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 1.5px solid #d48806;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
}

.water-alert {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  background: #fffbe6;
  border: 1px solid #ffe58f;
  color: #874d00;
  font-size: 13px;
  line-height: 1.4;
}

.alert-icon {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 1.5px solid #d48806;
  color: #d48806;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
  line-height: 1;
}

.field-block {
  margin-bottom: 22px;
}

.field-block:last-child {
  margin-bottom: 0;
}

.field-label {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 12px;
}

.part-checks {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.part-checks >>> .el-checkbox {
  margin-right: 0;
  display: flex;
  align-items: center;
  height: 36px;
}

.part-checks >>> .el-checkbox__input .el-checkbox__inner {
  width: 18px;
  height: 18px;
  border-radius: 5px;
  border-color: #d0d3d9;
  background: #fff;
}

.part-checks >>> .el-checkbox__input.is-checked .el-checkbox__inner,
.part-checks >>> .el-checkbox__input.is-focus.is-checked .el-checkbox__inner {
  background: #86909c;
  border-color: #86909c;
}

.part-checks >>> .el-checkbox__input.is-checked .el-checkbox__inner::after {
  border-color: #fff;
}

.part-checks >>> .el-checkbox__input.is-focus .el-checkbox__inner {
  border-color: #86909c;
}

.part-checks >>> .el-checkbox__label {
  font-size: 14px;
  color: #1d2129;
  padding-left: 10px;
}

.desc-input >>> .el-textarea__inner {
  background: #f2f3f5;
  border: none;
  min-height: 120px;
  border-radius: 10px;
  padding: 14px 16px;
  resize: vertical;
  color: #1d2129;
  line-height: 1.6;
}

.desc-input >>> .el-textarea__inner::placeholder {
  color: #c9cdd4;
}

.desc-input >>> .el-textarea__inner:focus {
  background: #eef0f3;
  border: none;
  box-shadow: none;
}

.footer-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  margin-top: 24px;
  padding-top: 4px;
}

.btn-export {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 44px;
  padding: 0 22px;
  border: 1px solid #e5e6eb;
  border-radius: 10px;
  background: #fff;
  color: #1d2129;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}

.btn-export:hover {
  background: #f7f8fa;
  border-color: #d0d3d9;
}

.btn-export i {
  font-size: 16px;
}

.btn-submit {
  height: 44px;
  padding: 0 28px;
  border: none;
  border-radius: 10px;
  background: #1d2129;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
}

.btn-submit:hover:not(:disabled) {
  background: #4e5969;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .collect-header,
  .collect-body {
    padding-left: 16px;
    padding-right: 16px;
  }

  .header-title {
    font-size: 22px;
  }

  .module-card {
    padding: 20px 16px;
  }

  .footer-bar {
    flex-direction: column-reverse;
    align-items: stretch;
  }

  .btn-export,
  .btn-submit {
    width: 100%;
    justify-content: center;
  }
}
</style>
