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
            <el-select
              v-model="form.pondCode"
              filterable
              allow-create
              default-first-option
              clearable
              placeholder="选择或输入存塘编号"
              style="width:100%"
              popper-class="config-select-dropdown"
            >
              <el-option
                v-for="p in pondOptions"
                :key="p.value"
                :label="p.label"
                :value="p.value"
              />
            </el-select>
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
        <p class="module-desc">按当前养殖池已绑定设备展示可用监测指标</p>

        <div v-loading="monitorLoading" class="metric-list">
          <div v-if="!form.pondCode" class="monitor-empty">请先选择养殖池，以加载该池可用设备数据</div>
          <div v-else-if="!metrics.length && !monitorLoading" class="monitor-empty">该池暂无绑定监测设备</div>
          <div
            v-for="item in metrics"
            :key="item.key"
            class="metric-card"
            :class="{ abnormal: item.abnormal }"
          >
            <div class="metric-main">
              <div class="metric-label">{{ item.label }}<span v-if="item.deviceType" class="metric-device"> · {{ item.deviceType }}</span></div>
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
        <el-dropdown trigger="click" @command="handleExport">
          <button type="button" class="btn-export">
            <i class="el-icon-upload2" /> 导出数据 <i class="el-icon-arrow-down" />
          </button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-for="f in exportFormats" :key="f.value" :command="f.value">{{ f.label }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <button
          v-if="pondOptions.length && form.pondCode"
          type="button"
          class="btn-submit"
          :disabled="submitting"
          @click="handleSubmit"
        >
          {{ submitting ? '提交中…' : '提交诊断申请' }}
        </button>
        <span v-else class="submit-hint">请先选择已配置的鱼池后再提交</span>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchFishSpecies, fetchMonitorData, submitCollection, createDiagnosisRecord } from '@/api/fishDisease'
import { fetchMergedPonds } from '@/utils/pondBridge'
import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'
import { EXPORT_FORMAT_OPTIONS, exportRecords } from '@/views/fishDisease/utils/exportFormats'

let mediaSeq = 0

export default {
  name: 'DataCollection',
  components: { MenuToggle, HeaderAuth },
  inject: {
    takePendingPondCode: { default: null },
    peekPendingPondCode: { default: null },
    switchFishNav: { default: null }
  },
  data() {
    return {
      dragOver: false,
      monitorLoading: false,
      submitting: false,
      captureAccept: 'image/*',
      captureMode: 'environment',
      speciesList: [],
      pondOptions: [],
      mediaFiles: [],
      metrics: [],
      feedingMethods: ['人工投喂', '自动投喂', '混合投喂'],
      anatomyParts: ['鳃部', '肝脏', '肠道', '体表', '其他'],
      exportFormats: EXPORT_FORMAT_OPTIONS,
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
        pondCode: [{ required: true, message: '请选择或填写养殖池编号', trigger: 'change' }]
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
  watch: {
    'form.pondCode'(val) {
      this.loadMonitor(val)
    }
  },
  activated() {
    this.applyPendingPond()
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
    applyPendingPond() {
      let code = null
      if (typeof this.takePendingPondCode === 'function') {
        code = this.takePendingPondCode()
      } else if (typeof this.peekPendingPondCode === 'function') {
        code = this.peekPendingPondCode()
      }
      if (code) this.form.pondCode = String(code)
    },
    normalizePondOptions(list) {
      const arr = Array.isArray(list) ? list : []
      return arr.map(item => {
        const value = item.poolCode || item.pool_code || item.pondCode || item.pondDetailNum || item.code || item.id
        const name = item.poolName || item.pool_name || item.pondName || item.name || value
        return {
          value: value != null ? String(value) : '',
          label: name && String(name) !== String(value) ? `${name}（${value}）` : String(value || '')
        }
      }).filter(o => o.value)
    },
    async init() {
      // 分块加载：监测失败不影响品种下拉
      const speciesP = fetchFishSpecies()
        .then(res => { this.speciesList = (res && res.data) || [] })
        .catch(() => {
          this.speciesList = []
          this.$message.warning('品种列表加载失败，可手动填写或刷新重试')
        })
      const pondsP = fetchMergedPonds()
        .then(list => {
          this.pondOptions = this.normalizePondOptions(list || [])
        })
        .catch(() => {
          this.pondOptions = []
        })
      await Promise.all([speciesP, pondsP])
      this.applyPendingPond()
      await this.loadMonitor(this.form.pondCode)
    },
    async loadMonitor(pondCode) {
      const code = pondCode || this.form.pondCode || ''
      if (!code) {
        this.metrics = []
        return
      }
      this.monitorLoading = true
      try {
        const res = await fetchMonitorData({ pondCode: code })
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
    handleExport(format) {
      const fmt = format || 'csv'
      const metricText = (this.metrics || []).map(m => `${m.label}:${m.value}${m.unit || ''}`).join('；')
      exportRecords(
        `数据采集_${this.form.pondCode || '未选池'}_${Date.now()}`,
        [{
          品种: this.form.species,
          养殖池: this.form.pondCode,
          苗种来源: this.form.frySource,
          发病数量: this.form.diseaseCount,
          投喂方式: this.form.feedingMethod,
          异常部位: (this.form.abnormalParts || []).join('、'),
          异常描述: this.form.abnormalDesc,
          媒体数量: this.mediaFiles.length,
          监测数据: metricText,
          导出时间: new Date().toLocaleString()
        }],
        fmt,
        '鱼病数据采集'
      )
      this.$message.success(`已导出 ${String(fmt).toUpperCase()} 格式`)
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) {
          this.$message.warning('请完善必填的基础信息')
          return
        }
        this.submitting = true
        let diagnosisId = ''
        try {
          await submitCollection({
            ...this.form,
            mediaCount: this.mediaFiles.length,
            mediaNames: this.mediaFiles.map(f => f.name),
            metrics: this.metrics
          })
          // 同步进入协同诊断「待诊断」
          try {
            const diagRes = await createDiagnosisRecord({
              pondCode: this.form.pondCode,
              category: this.form.species || '',
              status: 'pending',
              physicalSymptoms: this.form.abnormalParts || [],
              behavioralSymptoms: this.form.abnormalDesc ? [this.form.abnormalDesc] : [],
              suspectedDisease: '',
              reporter: '现场采集',
              metrics: this.metrics,
              source: 'collection'
            })
            diagnosisId = (diagRes && diagRes.data && (diagRes.data.id || diagRes.data.Id)) || ''
          } catch (diagErr) {
            this.$message.error('已保存采集，但写入协同诊断「待诊断」失败，请检查模块服务')
            console.warn('[collect] create diagnosis failed', diagErr)
          }
          this.$message.success('诊断申请已提交，可在协同诊断「待诊断」中查看')
          this.resetForm()
          if (typeof this.switchFishNav === 'function') {
            this.switchFishNav('diagnosis', diagnosisId ? { caseId: diagnosisId, tab: 'pending' } : { tab: 'pending' })
          }
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
  background: #0a1520;
  position: relative;
}

.collect-header {
  background: #0f1a26;
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
  color: #e8edf2;
  line-height: 1.25;
}

.header-date {
  font-size: 14px;
  color: #7c8fa1;
  white-space: nowrap;
  padding-top: 6px;
}

.header-desc {
  margin: 10px 0 0;
  font-size: 14px;
  color: #7c8fa1;
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
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
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
  color: #e8edf2;
}

.module-desc {
  margin: 0 0 20px;
  font-size: 14px;
  color: #7c8fa1;
  line-height: 1.5;
}

.upload-zone {
  border: 1.5px dashed rgba(64, 158, 255, 0.4);
  border-radius: 12px;
  background: #132030;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
}

.upload-zone:hover,
.upload-zone.is-dragover {
  border-color: #69b1ff;
  background: rgba(64, 158, 255, 0.12);
}

.upload-icon {
  font-size: 36px;
  color: #c0c4cc;
  margin-bottom: 12px;
}

.upload-main {
  font-size: 14px;
  color: #7c8fa1;
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
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 8px;
  background: #132030;
}

.media-thumb {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  background: rgba(64, 158, 255, 0.1);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7c8fa1;
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
  color: #e8edf2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.media-size {
  margin-top: 2px;
  font-size: 12px;
  color: #7c8fa1;
}

.media-remove {
  border: none;
  background: transparent;
  color: #7c8fa1;
  cursor: pointer;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.media-remove:hover {
  background: rgba(64, 158, 255, 0.1);
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
  border: 1px solid rgba(124, 143, 161, 0.25);
  background: #0f1a26;
  border-radius: 10px;
  font-size: 14px;
  color: #e8edf2;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}

.action-btn:hover {
  background: #0a1520;
  border-color: rgba(124, 143, 161, 0.35);
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
  color: #e8edf2;
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
  background: rgba(64, 158, 255, 0.1);
  border: none;
  height: 44px;
  line-height: 44px;
  border-radius: 10px;
  color: #e8edf2;
  padding-left: 14px;
}

.basic-form >>> .el-input__inner::placeholder,
.basic-form >>> .el-select .el-input__inner::placeholder {
  color: #5a6b7d;
}

.basic-form >>> .el-input__inner:focus,
.basic-form >>> .el-select .el-input.is-focus .el-input__inner {
  background: rgba(124, 143, 161, 0.22);
  border: none;
  box-shadow: none;
}

.basic-form >>> .el-select .el-input .el-select__caret {
  color: #5a6b7d;
  font-weight: 600;
}

.metric-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 40px;
}

.monitor-empty {
  padding: 18px 16px;
  border: 1px dashed rgba(124, 143, 161, 0.35);
  border-radius: 12px;
  color: #8ea0b5;
  font-size: 13px;
  text-align: center;
}

.metric-device {
  color: #8ea0b5;
  font-size: 12px;
  font-weight: 400;
}

.metric-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 18px 20px;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 12px;
  background: #132030;
}

.metric-card.abnormal {
  background: rgba(250, 219, 20, 0.15);
  border-color: rgba(250, 219, 20, 0.35);
}

.metric-label {
  font-size: 13px;
  color: #7c8fa1;
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
  color: #e8edf2;
  line-height: 1.1;
}

.metric-value .unit {
  font-size: 14px;
  color: #7c8fa1;
}

.metric-range {
  font-size: 12px;
  color: #7c8fa1;
}

.abnormal-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(250, 173, 20, 0.15);
  border: 1px solid rgba(250, 173, 20, 0.4);
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
  background: rgba(250, 219, 20, 0.15);
  border: 1px solid rgba(250, 219, 20, 0.35);
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
  color: #e8edf2;
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
  border-color: rgba(124, 143, 161, 0.35);
  background: #0f1a26;
}

.part-checks >>> .el-checkbox__input.is-checked .el-checkbox__inner,
.part-checks >>> .el-checkbox__input.is-focus.is-checked .el-checkbox__inner {
  background: #409eff;
  border-color: #409eff;
}

.part-checks >>> .el-checkbox__input.is-checked .el-checkbox__inner::after {
  border-color: rgba(124, 143, 161, 0.25);
}

.part-checks >>> .el-checkbox__input.is-focus .el-checkbox__inner {
  border-color: #7c8fa1;
}

.part-checks >>> .el-checkbox__label {
  font-size: 14px;
  color: #e8edf2;
  padding-left: 10px;
}

.desc-input >>> .el-textarea__inner {
  background: rgba(64, 158, 255, 0.1);
  border: none;
  min-height: 120px;
  border-radius: 10px;
  padding: 14px 16px;
  resize: vertical;
  color: #e8edf2;
  line-height: 1.6;
}

.desc-input >>> .el-textarea__inner::placeholder {
  color: #5a6b7d;
}

.desc-input >>> .el-textarea__inner:focus {
  background: rgba(124, 143, 161, 0.22);
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
  border: 1px solid rgba(124, 143, 161, 0.25);
  border-radius: 10px;
  background: #0f1a26;
  color: #e8edf2;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}

.btn-export:hover {
  background: #0a1520;
  border-color: rgba(124, 143, 161, 0.35);
}

.btn-export i {
  font-size: 16px;
}

.btn-submit {
  height: 44px;
  padding: 0 28px;
  border: none;
  border-radius: 10px;
  background: #409eff;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
}

.btn-submit:hover:not(:disabled) {
  background: #66b1ff;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.submit-hint {
  color: #7c8fa1;
  font-size: 13px;
  align-self: center;
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
