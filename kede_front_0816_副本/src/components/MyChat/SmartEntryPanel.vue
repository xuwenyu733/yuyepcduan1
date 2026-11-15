<template>
  <section class="smart-entry-panel" aria-label="图文资料录入">
    <div class="entry-tabs" role="tablist" aria-label="录入与已存资料">
      <button id="smart-entry-edit-tab" type="button" role="tab" :aria-selected="tab === 'entry'" aria-controls="smart-entry-edit"
        :class="{ active: tab === 'entry' }" @click="selectTab('entry')"><i class="el-icon-edit-outline" /> 图文录入</button>
      <button id="smart-entry-records-tab" type="button" role="tab" :aria-selected="tab === 'records'" aria-controls="smart-entry-records"
        :class="{ active: tab === 'records' }" @click="selectTab('records')"><i class="el-icon-folder-opened" /> 已存资料</button>
    </div>
    <div ref="scroll" class="entry-scroll">
      <div v-show="tab === 'entry'" id="smart-entry-edit" role="tabpanel" aria-labelledby="smart-entry-edit-tab">
        <div class="entry-intro"><i class="el-icon-document-add" /><div><strong>把现场信息，存成可查询的资料</strong><p>在这里填写文字、添加图片，保存后就能在查询分析中找到。</p></div></div>
        <div class="entry-form" @paste="pasteImages">
          <label class="field-label" for="smart-entry-title">资料标题 <span>必填</span></label>
          <el-input id="smart-entry-title" v-model="title" @input="draftChanged" placeholder="例如：1号池今日水质与投喂记录" :maxlength="160" show-word-limit :disabled="saving" />
          <div class="entry-columns">
          <div class="entry-text-column">
          <label class="field-label" for="smart-entry-content">文字说明 <span>可与图片一起保存</span></label>
          <el-input id="smart-entry-content" v-model="content" @input="draftChanged" type="textarea" :rows="6" :maxlength="20000" show-word-limit
            placeholder="填写日期、鱼池、现场情况或需要保存的说明。可同时上传图片。" :disabled="saving" />
          </div>
          <div class="entry-image-column">
          <label class="field-label">现场图片 <span>{{ images.length }} / 6 张</span></label>
          <label class="image-drop" :class="{ disabled: saving }" @dragover.prevent @drop.prevent="dropImages">
            <input ref="files" type="file" accept="image/png,image/jpeg,image/gif,image/webp" multiple :disabled="saving" @change="chooseImages" aria-label="选择录入图片" />
            <i class="el-icon-picture-outline" /><strong>点击选择、拖入，或粘贴图片</strong><span>PNG / JPEG / GIF / WebP</span><span>单张 5 MB，合计 20 MB · 保留原图</span>
          </label>
          <div v-if="images.length" class="selected-images">
            <div v-for="(image, index) in images" :key="image.key" class="selected-image">
              <img :src="image.url" :alt="image.file.name" /><span>{{ image.file.name }}</span>
              <button type="button" :disabled="saving" @click="removeImage(index)" :aria-label="`移除 ${image.file.name}`">×</button>
            </div>
          </div>
          </div>
          </div>
          <p class="entry-help">图片可单独保存。添加文字说明后，更容易通过关键词查询到这条资料。</p>
          <p v-if="saveError" class="entry-error" role="alert">{{ saveError }}</p>
        </div>
      </div>
      <div v-show="tab === 'records'" id="smart-entry-records" role="tabpanel" aria-labelledby="smart-entry-records-tab">
        <div class="records-toolbar"><span>本组织共 {{ total }} 条图文资料</span><el-button size="small" icon="el-icon-refresh" @click="loadRecords">刷新</el-button></div>
        <div v-if="savedId" class="save-success" role="status"><i class="el-icon-success" /> 已保存，记录编号 #{{ savedId }}。文字和图片均已入库。</div>
        <div v-loading="loading" class="records-list">
          <p v-if="listError" class="entry-error" role="alert">{{ listError }}</p>
          <div v-else-if="!loading && !records.length" class="entry-empty"><i class="el-icon-folder-opened" /><p>还没有录入资料</p><span>保存的文字和图片会出现在这里。</span></div>
          <article v-for="record in records" :key="record.id" class="saved-record" :data-record-id="record.id">
            <div class="record-heading"><h3>{{ record.title }}</h3><el-button type="text" @click="queryRecord(record)">查询这条资料</el-button></div>
            <small>#{{ record.id }} · {{ (record.createdAt || '').replace('T', ' ') }} · {{ record.images.length }} 张图片</small>
            <p class="record-content">{{ record.content || '此记录仅包含图片' }}</p>
            <DatabaseImages :images="record.images" :auto-open="record.id === savedId" />
          </article>
        </div>
        <el-pagination v-if="total > 10" :current-page.sync="pageNo" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadRecords" />
      </div>
    </div>
    <div class="entry-actions">
      <span class="draft-note">切换查询可保留当前草稿</span>
      <div><el-button @click="$emit('back')">返回查询</el-button><el-button v-if="tab === 'entry'" type="primary" :loading="saving" @click="save">保存到数据库</el-button><el-button v-else type="primary" @click="selectTab('entry')">继续录入</el-button></div>
    </div>
  </section>
</template>
<script>
import { saveSmartEntry, listSmartEntries } from '@/api/fish/smartEntry'
import DatabaseImages from './DatabaseImages.vue'
function draftId() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const n = crypto.getRandomValues(new Uint8Array(1))[0] & 15
    return (c === 'x' ? n : (n & 3) | 8).toString(16)
  })
}
export default {
  name: 'SmartEntryPanel', components: { DatabaseImages },
  data: () => ({ tab: 'entry', title: '', content: '', images: [], requestId: draftId(), saving: false,
    loading: false, records: [], total: 0, pageNo: 1, savedId: null, saveError: '', listError: '' }),
  beforeDestroy() { this.clearImages() },
  methods: {
    open(text = '') {
      if (!this.content && text) { this.content = text; this.draftChanged() }
      this.tab = 'entry'
    },
    // Keep the same ID for an unchanged retry, but never discard edits after a lost save response.
    draftChanged() { this.requestId = draftId() },
    clearImages() { this.images.forEach(i => URL.revokeObjectURL(i.url)); this.images = [] },
    removeImage(index) { URL.revokeObjectURL(this.images[index].url); this.images.splice(index, 1); this.draftChanged() },
    addImages(files) {
      if (this.saving) return
      for (const file of Array.from(files || [])) {
        if (!['image/png', 'image/jpeg', 'image/gif', 'image/webp'].includes(file.type)) { this.$message.warning('请选择 PNG、JPEG、GIF 或 WebP 图片'); continue }
        if (this.images.length >= 6) { this.$message.warning('一次最多上传 6 张图片'); break }
        if (!file.size || file.size > 5 * 1024 * 1024) { this.$message.warning('单张图片不能超过 5 MB'); continue }
        if (this.images.reduce((sum, i) => sum + i.file.size, file.size) > 20 * 1024 * 1024) { this.$message.warning('图片合计不能超过 20 MB'); break }
        this.images.push({ file, url: URL.createObjectURL(file), key: draftId() })
        this.draftChanged()
      }
    },
    chooseImages(event) { this.addImages(event.target.files); event.target.value = '' },
    dropImages(event) { this.addImages(event.dataTransfer.files) },
    pasteImages(event) {
      const files = event.clipboardData && event.clipboardData.files
      if (files && files.length) { event.preventDefault(); this.addImages(files) }
    },
    async save() {
      if (this.saving) return
      if (!this.title.trim()) { this.$message.warning('请填写资料标题'); return }
      if (!this.content.trim() && !this.images.length) { this.$message.warning('请填写文字或选择图片'); return }
      const form = new FormData(); form.append('title', this.title.trim()); form.append('content', this.content.trim()); form.append('requestId', this.requestId)
      this.images.forEach(image => form.append('images', image.file))
      this.saving = true; this.saveError = ''
      try {
        const result = await saveSmartEntry(form)
        if (result.code !== 0 || !result.data || !result.data.id) throw new Error(result.msg || '未收到入库确认，请重试')
        this.savedId = result.data.id; this.$emit('saved', result.data)
        this.title = ''; this.content = ''; this.clearImages(); this.requestId = draftId()
        this.pageNo = 1; this.selectTab('records')
      } catch (e) { this.saveError = e.message || '保存失败，文字和图片仍保留在窗口中，可重试。' }
      finally { this.saving = false }
    },
    selectTab(tab) {
      this.tab = tab
      this.$nextTick(() => { if (this.$refs.scroll) this.$refs.scroll.scrollTop = 0 })
      if (tab === 'records') this.loadRecords()
    },
    async loadRecords() {
      this.loading = true; this.listError = ''
      try { const result = await listSmartEntries(this.pageNo); this.records = result.data.list; this.total = result.data.total }
      catch (e) { this.listError = '读取已存资料失败，请点击刷新重试。' }
      finally { this.loading = false }
    },
    queryRecord(record) { this.$emit('query', record.title) }
  }
}
</script>
<style>
.smart-entry-panel { flex: 1 1 0; min-height: 0; min-width: 0; display: flex; flex-direction: column; color: #e6f1ff; font-size: 14px; }
.smart-entry-panel .entry-tabs { display: flex; gap: 10px; padding: 16px 20px 0; flex-shrink: 0; }
.smart-entry-panel .entry-tabs button { padding: 9px 16px; border: 1px solid rgba(64,158,255,.35); border-radius: 8px; background: rgba(30,70,120,.35); color: #b8d4f0; font: inherit; cursor: pointer; }
.smart-entry-panel .entry-tabs button.active { background: linear-gradient(135deg,rgba(64,158,255,.45),rgba(100,168,255,.25)); border-color: #64a8ff; color: #fff; box-shadow: 0 0 12px rgba(64,158,255,.15); }
.smart-entry-panel .entry-scroll { flex: 1; min-height: 0; overflow-y: auto; padding: 16px 20px 20px; scrollbar-width: thin; scrollbar-color: rgba(64,158,255,.5) transparent; }
.smart-entry-panel .entry-intro { display: flex; align-items: center; gap: 14px; width: fit-content; max-width: 100%; background: rgba(30,70,120,.5); border: 1px solid rgba(64,158,255,.3); padding: 16px 20px; border-radius: 6px 16px 16px; margin-bottom: 18px; }
.smart-entry-panel .entry-intro > i { font-size: 28px; color: #4bcbf1; }
.smart-entry-panel .entry-intro strong { font-size: 16px; }
.smart-entry-panel .entry-intro p { margin: 7px 0 0; color: #b8d4f0; line-height: 1.6; }
.smart-entry-panel .entry-columns { display: grid; grid-template-columns: minmax(0,1.25fr) minmax(260px,1fr); gap: 20px; }
.smart-entry-panel .field-label { display: block; margin: 16px 0 9px; font-weight: 600; color: #dfecf6; line-height: 1.6; }
.smart-entry-panel .entry-form > .field-label:first-child { margin-top: 0; }
.smart-entry-panel .field-label span { color: #9ec5ef; font-weight: normal; font-size: 12px; margin-left: 8px; }
.smart-entry-panel .el-input__inner, .smart-entry-panel .el-textarea__inner { background: rgba(30,70,120,.5) !important; border: 1px solid rgba(64,158,255,.35) !important; border-radius: 9px; color: #e6f1ff !important; font-family: inherit; }
.smart-entry-panel .el-input__inner:focus, .smart-entry-panel .el-textarea__inner:focus { border-color: #64a8ff !important; box-shadow: 0 0 10px rgba(100,168,255,.2); }
.smart-entry-panel .el-textarea__inner { line-height: 1.7; padding: 12px 15px 26px; resize: vertical; }
.smart-entry-panel .el-input__inner::placeholder, .smart-entry-panel .el-textarea__inner::placeholder { color: #91b1d3; }
.smart-entry-panel .el-input__count, .smart-entry-panel .el-input__count-inner { color: #9ec5ef !important; background: #193d66 !important; }
.smart-entry-panel .image-drop { display: flex; position: relative; flex-direction: column; justify-content: center; align-items: center; text-align: center; gap: 7px; min-height: 176px; background: rgba(30,70,120,.25); border: 1px dashed rgba(100,168,255,.65); border-radius: 10px; padding: 16px; cursor: pointer; }
.smart-entry-panel .image-drop:hover { background: rgba(30,70,120,.5); border-color: #64a8ff; }
.smart-entry-panel .image-drop input { position: absolute; inset: 0; opacity: 0; cursor: pointer; width: 100%; height: 100%; }
.smart-entry-panel .image-drop i { font-size: 30px; color: #64caff; }
.smart-entry-panel .image-drop strong { color: #d3e9ff; font-weight: 500; }
.smart-entry-panel .image-drop span, .smart-entry-panel .entry-help, .smart-entry-panel .draft-note { color: #9ec5ef; font-size: 12px; line-height: 1.7; }
.smart-entry-panel .entry-help { margin: 14px 0 0; }
.smart-entry-panel button:focus-visible, .smart-entry-panel .image-drop:focus-within { outline: 2px solid #64caff; outline-offset: 3px; }
.smart-entry-panel .image-drop.disabled { opacity: .5; pointer-events: none; }
.smart-entry-panel .selected-images { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 12px; }
.smart-entry-panel .selected-image { width: 100px; position: relative; }
.smart-entry-panel .selected-image img { width: 100px; height: 72px; object-fit: cover; border: 1px solid rgba(64,158,255,.35); border-radius: 7px; }
.smart-entry-panel .selected-image span { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 12px; color: #b8d4f0; }
.smart-entry-panel .selected-image button { position: absolute; right: 0; top: 0; width: 23px; height: 23px; color: #fff; background: #98445c; border: 0; border-radius: 0 7px 0 7px; cursor: pointer; }
.smart-entry-panel .entry-actions, .smart-entry-panel .records-toolbar, .smart-entry-panel .record-heading { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.smart-entry-panel .entry-actions { flex-shrink: 0; padding: 12px 20px; border-top: 1px solid rgba(64,158,255,.15); }
.smart-entry-panel .entry-actions > div { display: flex; flex-shrink: 0; }
.smart-entry-panel .el-button { background: rgba(30,70,120,.4) !important; border-color: rgba(64,158,255,.4) !important; color: #c8e2ff !important; border-radius: 8px; }
.smart-entry-panel .el-button:hover { border-color: #64a8ff !important; color: #fff !important; }
.smart-entry-panel .el-button--primary { background: linear-gradient(135deg,#327dc8,#24588c) !important; border-color: #64a8ff !important; color: #fff !important; box-shadow: 0 0 12px rgba(64,158,255,.15); }
.smart-entry-panel .el-button--text { background: transparent !important; border-color: transparent !important; color: #7ccfff !important; }
.smart-entry-panel .save-success { margin: 14px 0; padding: 12px 16px; background: rgba(39,132,91,.16); border: 1px solid rgba(77,199,156,.35); color: #87e2c2; border-radius: 9px; line-height: 1.7; }
.smart-entry-panel .saved-record { background: rgba(30,70,120,.4); border: 1px solid rgba(64,158,255,.3); border-radius: 6px 16px 16px; padding: 14px 18px; margin-top: 12px; }
.smart-entry-panel .record-heading h3 { margin: 0; color: #d3edff; font-size: 16px; overflow-wrap: anywhere; }
.smart-entry-panel .record-heading .el-button { flex-shrink: 0; }
.smart-entry-panel .saved-record small, .smart-entry-panel .stored-image span { color: #9ec5ef; }
.smart-entry-panel .record-content { white-space: pre-wrap; overflow-wrap: anywhere; max-height: 180px; overflow-y: auto; line-height: 1.7; }
.smart-entry-panel .entry-error { color: #ffa9a1; }
.smart-entry-panel .records-list { min-height: 100px; }
.smart-entry-panel .entry-empty { padding: 50px 20px; text-align: center; color: #9ec5ef; }
.smart-entry-panel .entry-empty i { font-size: 38px; color: #64a8ff; }
.smart-entry-panel .entry-empty p { color: #d3e9ff; }
.smart-entry-panel .entry-empty span { font-size: 12px; }
.smart-entry-panel .el-loading-mask { background: rgba(16,42,67,.8); }
.smart-entry-panel .image-toggle { color: #b8ddff; border-color: rgba(64,158,255,.4); background: rgba(30,70,120,.5); }
.smart-entry-panel .el-pagination { margin-top: 18px; }
.smart-entry-panel .el-pagination button, .smart-entry-panel .el-pager li { background: transparent !important; color: #b8d4f0 !important; }
.smart-entry-panel .el-pager li.active { color: #64caff !important; }
@media (max-width: 1280px) { .smart-entry-panel .entry-columns { grid-template-columns: minmax(0,1fr); gap: 0; } }
@media (max-width: 760px) { .smart-entry-panel .entry-tabs { padding: 12px 12px 0; } .smart-entry-panel .entry-scroll { padding: 12px; } .smart-entry-panel .entry-actions { padding: 12px; flex-wrap: wrap; } .smart-entry-panel .entry-intro { padding: 12px; } .smart-entry-panel .record-heading { align-items: flex-start; flex-wrap: wrap; } }
</style>
