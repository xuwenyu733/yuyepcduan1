<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import {
  deleteSkillApi,
  getSkillDetail,
  listSkills,
  previewSkillMarkdown,
  saveSkillApi,
} from '../api'

const list = ref([])
const loading = ref(false)
const saving = ref(false)
const tip = ref('')
const tipOk = ref(true)

const editingFile = ref('')
const title = ref('')
const tags = ref('水质, 养殖')
const weight = ref(9)
const content = ref('')
const mdPreview = ref('')
const showPreview = ref(true)

const editableList = computed(() => list.value.filter((x) => x.editable))
const builtinList = computed(() => list.value.filter((x) => !x.editable))

function flash(msg, ok = true) {
  tip.value = msg
  tipOk.value = ok
}

async function refresh() {
  loading.value = true
  try {
    const data = await listSkills()
    list.value = data.skills || data.rules || []
  } catch (e) {
    flash(e.message || String(e), false)
  } finally {
    loading.value = false
  }
}

function resetForm() {
  editingFile.value = ''
  title.value = ''
  tags.value = '水质, 养殖'
  weight.value = 9
  content.value = ''
  mdPreview.value = ''
}

async function refreshPreview() {
  if (!title.value.trim() && !content.value.trim()) {
    mdPreview.value = ''
    return
  }
  try {
    mdPreview.value = await previewSkillMarkdown(title.value || '未命名', content.value || '')
  } catch {
    mdPreview.value = content.value
  }
}

let previewTimer = null
watch([title, content], () => {
  clearTimeout(previewTimer)
  previewTimer = setTimeout(refreshPreview, 350)
})

async function save() {
  saving.value = true
  try {
    const skill = await saveSkillApi({
      title: title.value,
      content: content.value,
      weight: weight.value,
      tags: tags.value,
      file: editingFile.value || undefined,
      convertPlain: true,
    })
    flash(`已保存为 Markdown：knowledge/skills/${skill.file}`)
    editingFile.value = skill.file
    await refresh()
    await refreshPreview()
  } catch (e) {
    flash(e.message || String(e), false)
  } finally {
    saving.value = false
  }
}

async function editItem(item) {
  if (!item.editable) {
    flash('内置规则只读，可新建一条覆盖或补充', false)
    return
  }
  try {
    const detail = await getSkillDetail(item.file, 'skills')
    editingFile.value = item.file
    title.value = item.title
    tags.value = (item.tags || []).join(', ')
    weight.value = item.weight || 8
    // 展示正文：去掉 frontmatter 后的纯内容给普通人编辑
    const raw = detail.raw || ''
    const m = raw.match(/^---[\s\S]*?---\r?\n([\s\S]*)$/)
    content.value = (m ? m[1] : raw).trim()
    await refreshPreview()
    flash(`正在编辑：${item.file}`)
  } catch (e) {
    flash(e.message || String(e), false)
  }
}

async function removeItem(item) {
  if (!item.editable) return
  if (!confirm(`确定删除「${item.title}」？删除后小科将不再读取该 Skill。`)) return
  try {
    await deleteSkillApi(item.file)
    flash(`已删除 ${item.file}`)
    if (editingFile.value === item.file) resetForm()
    await refresh()
  } catch (e) {
    flash(e.message || String(e), false)
  }
}

function fillExample() {
  title.value = '草鱼夏季高温投喂注意点'
  tags.value = '草鱼, 投喂, 高温, 溶氧'
  weight.value = 10
  content.value = `一、适用条件
适用于水温持续高于 30℃ 的草鱼主养池塘。

二、投喂调整
1、上午投喂量减少约 20%，下午视溶氧再定。
2、发现浮头风险立即停料并开启增氧。

三、巡塘要求
早晚各巡塘一次，重点看游动与摄食。
连续两天摄食明显下降要检测水质并上报。`
  refreshPreview()
}

onMounted(refresh)
</script>

<template>
  <div class="skills-page">
    <header class="skills-hero">
      <div>
        <h1>渔业知识录入</h1>
        <p>不懂技术也能用：写清楚标题和内容 → 自动变成 Markdown Skill → 小科回答时优先读取。</p>
      </div>
      <div class="hero-actions">
        <button type="button" class="btn ghost" @click="fillExample">填入示例</button>
        <button type="button" class="btn ghost" @click="resetForm">新建空白</button>
      </div>
    </header>

    <div v-if="tip" class="banner" :class="{ ok: tipOk }">{{ tip }}</div>
    <p class="path-hint">文件保存在：<code>app/knowledge/skills/</code>（系统内置在 <code>app/knowledge/rules/</code>）</p>

    <div class="skills-grid">
      <section class="panel editor-panel">
        <h2>{{ editingFile ? '编辑 Skill' : '新建 Skill' }}</h2>
        <label>
          <span>标题（必填）</span>
          <input v-model="title" type="text" placeholder="例如：草鱼夏季高温投喂注意点" />
        </label>
        <div class="row2">
          <label>
            <span>关键词标签</span>
            <input v-model="tags" type="text" placeholder="用逗号分隔，如：水质, 溶氧" />
          </label>
          <label>
            <span>重要程度（1-10）</span>
            <input v-model.number="weight" type="number" min="1" max="10" />
          </label>
        </div>
        <label>
          <span>正文（可直接写大白话）</span>
          <textarea
            v-model="content"
            rows="14"
            placeholder="把你们的渔业规定、技术要点写在这里……&#10;可用「一、」「二、」或「1、」分段，保存时会自动转成 Markdown。"
          />
        </label>
        <div class="editor-actions">
          <label class="check">
            <input v-model="showPreview" type="checkbox" />
            显示 Markdown 预览
          </label>
          <button type="button" class="btn primary" :disabled="saving" @click="save">
            {{ saving ? '保存中…' : '保存为 MD Skill' }}
          </button>
        </div>
        <div v-if="showPreview" class="md-box">
          <div class="md-title">将生成的 Markdown 预览</div>
          <pre>{{ mdPreview || '（输入标题和正文后自动预览）' }}</pre>
        </div>
      </section>

      <aside class="panel list-panel">
        <div class="list-head">
          <h2>已有 Skill / 规则</h2>
          <button type="button" class="btn ghost sm" :disabled="loading" @click="refresh">刷新</button>
        </div>

        <h3>我录入的（可编辑） · {{ editableList.length }}</h3>
        <div v-if="!editableList.length" class="empty">还没有录入，右侧写完点「保存」即可。</div>
        <ul class="skill-list">
          <li v-for="item in editableList" :key="item.file">
            <div class="item-main" @click="editItem(item)">
              <strong>{{ item.title }}</strong>
              <small>{{ item.file }} · 权重 {{ item.weight }}</small>
              <p>{{ item.preview }}</p>
            </div>
            <button type="button" class="btn danger sm" @click="removeItem(item)">删除</button>
          </li>
        </ul>

        <h3>系统内置示例（只读） · {{ builtinList.length }}</h3>
        <ul class="skill-list readonly">
          <li v-for="item in builtinList" :key="item.file">
            <div class="item-main">
              <strong>{{ item.title }}</strong>
              <small>{{ item.file }} · 权重 {{ item.weight }}</small>
            </div>
          </li>
        </ul>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.skills-page {
  width: min(1200px, 100%);
  margin: 0 auto;
  padding: 12px 16px 48px;
  flex: none;
  min-height: min-content;
}
.skills-hero {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 14px;
}
.skills-hero h1 {
  margin: 0 0 8px;
  font-size: 26px;
  color: #e8f1ff;
}
.skills-hero p {
  margin: 0;
  color: #8aa3c7;
  line-height: 1.55;
  max-width: 720px;
}
.hero-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
.skills-grid {
  display: grid;
  grid-template-columns: 1.35fr 0.9fr;
  gap: 14px;
}
.panel {
  background: linear-gradient(180deg, #0d2748, #081a30);
  border: 1px solid rgba(120, 170, 230, 0.22);
  border-radius: 16px;
  padding: 16px 18px 18px;
}
.panel h2 {
  margin: 0 0 14px;
  font-size: 18px;
  color: #7ec2ff;
}
.panel h3 {
  margin: 16px 0 8px;
  font-size: 13px;
  color: #8aa3c7;
  font-weight: 600;
}
label {
  display: block;
  margin-bottom: 12px;
}
label > span {
  display: block;
  margin-bottom: 6px;
  color: #8aa3c7;
  font-size: 13px;
}
input,
textarea {
  width: 100%;
  border: 1px solid rgba(120, 170, 230, 0.22);
  background: #102848;
  color: #e8f1ff;
  border-radius: 10px;
  padding: 10px 12px;
  outline: none;
}
textarea {
  resize: vertical;
  line-height: 1.55;
  font-family: inherit;
}
.row2 {
  display: grid;
  grid-template-columns: 1fr 160px;
  gap: 12px;
}
.editor-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin: 4px 0 12px;
}
.check {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #8aa3c7;
  font-size: 13px;
  margin: 0;
}
.check input {
  width: auto;
}
.btn {
  border: 1px solid rgba(120, 170, 230, 0.28);
  background: transparent;
  color: #9fd0ff;
  border-radius: 10px;
  padding: 9px 14px;
  cursor: pointer;
}
.btn.primary {
  background: #1e6fd9;
  border-color: #1e6fd9;
  color: #fff;
}
.btn.ghost {
  background: rgba(74, 163, 255, 0.08);
}
.btn.danger {
  color: #ffb4b4;
  border-color: rgba(232, 93, 93, 0.35);
}
.btn.sm {
  padding: 6px 10px;
  font-size: 12px;
}
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.md-box {
  border: 1px dashed rgba(120, 170, 230, 0.28);
  border-radius: 12px;
  padding: 10px 12px;
  background: rgba(0, 0, 0, 0.18);
}
.md-title {
  color: #8aa3c7;
  font-size: 12px;
  margin-bottom: 8px;
}
.md-box pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  color: #d7e7ff;
  font-size: 12px;
  line-height: 1.5;
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
}
.list-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.skill-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.skill-list li {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  border: 1px solid rgba(120, 170, 230, 0.18);
  border-radius: 12px;
  padding: 10px;
  background: rgba(16, 40, 72, 0.65);
}
.item-main {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}
.item-main strong {
  display: block;
  color: #e8f1ff;
  margin-bottom: 2px;
}
.item-main small {
  color: #6f87a8;
  font-size: 11px;
}
.item-main p {
  margin: 6px 0 0;
  color: #8aa3c7;
  font-size: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.skill-list.readonly .item-main {
  cursor: default;
}
.empty {
  color: #6f87a8;
  font-size: 13px;
  padding: 8px 0;
}
@media (max-width: 960px) {
  .skills-grid {
    grid-template-columns: 1fr;
  }
  .skills-hero {
    flex-direction: column;
  }
}
</style>
