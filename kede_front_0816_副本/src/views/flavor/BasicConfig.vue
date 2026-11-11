<template>
  <div class="config-page">
    <div class="page-head">
      <div>
        <h2 class="section-title">基础档案</h2>
      </div>
      <el-button class="btn-dark save-btn" icon="el-icon-setting" :loading="saving" @click="saveAll">
        保存配置
      </el-button>
    </div>

    <div class="sub-tabs">
      <button type="button" class="sub-tab" :class="{ active: tab === 'dict' }" @click="tab = 'dict'">字典配置</button>
      <button type="button" class="sub-tab" :class="{ active: tab === 'unit' }" @click="tab = 'unit'">单位配置</button>
    </div>

    <!-- 字典配置 -->
    <template v-if="tab === 'dict'">
      <div class="cfg-card">
        <div class="cfg-title">可用鱼类管理</div>
        <p class="cfg-sub">
          配置风味优化系统可选鱼种；甲方可随时新增。采购/筛选下拉会自动包含下方列表，以及入池记录中已出现的鱼种。
        </p>
        <div class="add-row">
          <el-input
            v-model="drafts.category"
            placeholder="输入新鱼种名称，如：鲈鱼"
            class="soft-input"
            clearable
            @keyup.enter.native="addItem('categories', 'category')"
          />
          <el-button class="btn-dark add-btn" @click="addItem('categories', 'category')">+ 添加鱼种</el-button>
        </div>
        <div class="tag-list">
          <el-tag
            v-for="(item, idx) in dictionaries.categories"
            :key="'c-' + item + idx"
            closable
            class="cfg-tag"
            @close="removeItem('categories', idx)"
          >{{ item }}</el-tag>
          <span v-if="!dictionaries.categories.length" class="tag-empty">暂无可用鱼类，请添加或从入池记录纳入</span>
        </div>

        <div class="entry-fish-block">
          <div class="entry-fish-head">
            <div>
              <div class="entry-fish-title">入池记录中的鱼种</div>
              <p class="cfg-sub entry-fish-sub">
                扫描风味入池与管理端入池档案；未纳入可用列表的可一键加入，便于后续采购选用。
              </p>
            </div>
            <div class="entry-fish-actions">
              <el-button size="mini" icon="el-icon-refresh" :loading="entryHintsLoading" @click="loadEntryHints">刷新</el-button>
              <el-button
                size="mini"
                class="btn-dark"
                :disabled="!entryMissingCategories.length"
                @click="adoptAllEntryCategories"
              >全部纳入</el-button>
            </div>
          </div>
          <div v-if="entryHintsLoading" class="tag-empty">正在扫描入池记录…</div>
          <div v-else-if="!entryCategories.length" class="tag-empty">暂无入池记录鱼种</div>
          <div v-else class="tag-list">
            <el-tag
              v-for="item in entryCategories"
              :key="'e-' + item"
              :closable="false"
              class="cfg-tag"
              :class="{ 'cfg-tag-adopted': !entryMissingSet.has(item) }"
            >
              {{ item }}
              <el-button
                v-if="entryMissingSet.has(item)"
                type="text"
                class="adopt-link"
                @click="adoptEntryCategory(item)"
              >纳入</el-button>
              <span v-else class="adopted-mark">已可用</span>
            </el-tag>
          </div>
        </div>
      </div>

      <div class="cfg-card">
        <div class="cfg-title">来源管理</div>
        <p class="cfg-sub">配置鱼类来源（养殖场/供应商/捕捞区域等）</p>
        <div class="add-row">
          <el-input v-model="drafts.source" placeholder="输入新来源名称" class="soft-input" clearable @keyup.enter.native="addItem('sources', 'source')" />
          <el-button class="btn-dark add-btn" @click="addItem('sources', 'source')">+ 添加</el-button>
        </div>
        <div class="tag-list">
          <el-tag
            v-for="(item, idx) in dictionaries.sources"
            :key="'s-' + item + idx"
            closable
            class="cfg-tag"
            @close="removeItem('sources', idx)"
          >{{ item }}</el-tag>
        </div>
      </div>

      <div class="cfg-card">
        <div class="cfg-title">规格等级管理</div>
        <p class="cfg-sub">配置鱼类规格等级标准（以重量为单位）</p>
        <div class="add-row">
          <el-input
            v-model="drafts.grade"
            placeholder="输入规格等级（例如：0.5-0.8kg/尾）"
            class="soft-input"
            clearable
            @keyup.enter.native="addItem('grades', 'grade')"
          />
          <el-button class="btn-dark add-btn" @click="addItem('grades', 'grade')">+ 添加</el-button>
        </div>
        <div class="tag-list">
          <el-tag
            v-for="(item, idx) in dictionaries.grades"
            :key="'g-' + item + idx"
            closable
            class="cfg-tag"
            @close="removeItem('grades', idx)"
          >{{ item }}</el-tag>
        </div>
      </div>
    </template>

    <!-- 单位配置 -->
    <template v-else>
      <div class="cfg-card">
        <div class="cfg-title">计量单位配置</div>
        <p class="cfg-sub">设置系统默认的计量单位</p>

        <div class="unit-field">
          <div class="unit-label">重量单位</div>
          <el-select v-model="units.weight" class="unit-select" placeholder="请选择">
            <el-option label="千克 (kg)" value="kg" />
            <el-option label="斤" value="jin" />
          </el-select>
          <p class="unit-hint">用于重量相关的数据录入和展示</p>
        </div>

        <div class="unit-field">
          <div class="unit-label">长度单位</div>
          <el-select v-model="units.length" class="unit-select" placeholder="请选择">
            <el-option label="厘米 (cm)" value="cm" />
            <el-option label="毫米 (mm)" value="mm" />
          </el-select>
        </div>
      </div>
    </template>

    <!-- 系统信息 + 鱼池：通栏上下排布 -->
    <div class="cfg-card">
      <div class="cfg-title">系统信息</div>
      <p class="cfg-sub">当前系统配置概览</p>
      <div class="info-list">
        <div class="info-row"><span>可用鱼类数量</span><b>{{ liveOverview.categoryCount }} 个</b></div>
        <div class="info-row"><span>来源数量</span><b>{{ liveOverview.sourceCount }} 个</b></div>
        <div class="info-row"><span>规格等级数量</span><b>{{ liveOverview.gradeCount }} 个</b></div>
        <div class="info-row"><span>当前重量单位</span><b>{{ units.weight }}</b></div>
        <div class="info-row"><span>当前长度单位</span><b>{{ units.length }}</b></div>
      </div>
    </div>

    <div class="cfg-card">
      <div class="cfg-title">鱼池管理</div>
      <p class="cfg-sub">只读展示存塘记录（zk_pond），与全站编号一致；增删改请在「存塘管理」维护</p>
      <el-table :data="ponds" class="pond-table" empty-text="暂无存塘鱼池，请先在「存塘管理」维护">
        <el-table-column prop="code" label="鱼池编号" min-width="110" />
        <el-table-column label="来源" min-width="100" :formatter="() => '存塘'" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column label="车间/系统" min-width="160">
          <template slot-scope="{ row }">
            {{ [row.location || row.workshop, row.systemName].filter(Boolean).join(' · ') || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="statusLabel" label="状态" min-width="100" />
      </el-table>
    </div>
  </div>
</template>

<script>
import {
  fetchConfig,
  updateUnits,
  updateDictionaries
} from '@/api/flavor'
import { fetchFlavorMergedPonds, getPondSourceErrors } from '@/utils/pondBridge'
import {
  mergeFlavorMetaWithPurify,
  loadEntryCategoryHints,
  syncFlavorCategoriesToPurifyDict
} from '@/utils/flavorPurifyBridge'

export default {
  name: 'BasicConfig',
  data() {
    return {
      tab: 'dict',
      saving: false,
      overview: {},
      units: { weight: 'kg', length: 'cm' },
      dictionaries: {
        categories: [],
        sources: [],
        grades: []
      },
      drafts: {
        category: '',
        source: '',
        grade: ''
      },
      ponds: [],
      entryCategories: [],
      entryMissingCategories: [],
      entryHintsLoading: false
    }
  },
  created() {
    this.load()
  },
  activated() {
    this.load()
  },
  computed: {
    liveOverview() {
      return {
        categoryCount: (this.dictionaries.categories || []).length,
        sourceCount: (this.dictionaries.sources || []).length,
        gradeCount: (this.dictionaries.grades || []).length
      }
    },
    entryMissingSet() {
      return new Set(this.entryMissingCategories || [])
    }
  },
  methods: {
    async load() {
      const res = await fetchConfig()
      const { overview, meta } = res.data
      const synced = mergeFlavorMetaWithPurify(meta || {})
      this.overview = overview || {}
      this.units = { ...(synced.units || meta.units || { weight: 'kg', length: 'cm' }) }
      this.dictionaries = {
        categories: [...(synced.categories || [])],
        sources: [...(synced.sources || [])],
        grades: [...(synced.grades || [])]
      }
      await this.loadEntryHints()
      try {
        const merged = await fetchFlavorMergedPonds()
        this.ponds = Array.isArray(merged) ? merged : []
        const errs = getPondSourceErrors(merged)
        if (errs.length) {
          this.$message.warning('存塘鱼池加载失败：' + errs.map(e => e.message).join('；'))
        }
      } catch (e) {
        console.error('[flavor配置] 鱼池加载失败', e)
        this.ponds = []
        this.$message.error('鱼池列表加载失败，请确认后端存塘接口可用')
      }
    },
    async loadEntryHints() {
      this.entryHintsLoading = true
      try {
        const hints = await loadEntryCategoryHints(this.dictionaries.categories)
        this.entryCategories = hints.fromEntries || []
        this.entryMissingCategories = hints.missing || []
      } catch (e) {
        console.warn('[flavor配置] 入池鱼种扫描失败', e)
        this.entryCategories = []
        this.entryMissingCategories = []
      } finally {
        this.entryHintsLoading = false
      }
    },
    adoptEntryCategory(name) {
      const value = String(name || '').trim()
      if (!value) return
      if (this.dictionaries.categories.includes(value)) {
        this.$message.info('该鱼种已在可用列表中')
        return
      }
      this.dictionaries.categories.push(value)
      this.entryMissingCategories = this.entryMissingCategories.filter((c) => c !== value)
      this.$message.success('已纳入可用鱼类，请点击右上角「保存配置」生效')
    },
    adoptAllEntryCategories() {
      const missing = [...(this.entryMissingCategories || [])]
      if (!missing.length) {
        this.$message.info('入池记录中的鱼种均已可用')
        return
      }
      missing.forEach((name) => {
        if (!this.dictionaries.categories.includes(name)) {
          this.dictionaries.categories.push(name)
        }
      })
      this.entryMissingCategories = []
      this.$message.success(`已纳入 ${missing.length} 个鱼种，请保存配置生效`)
    },
    addItem(listKey, draftKey) {
      const value = String(this.drafts[draftKey] || '').trim()
      if (!value) {
        this.$message.warning(listKey === 'categories' ? '请输入鱼种名称' : '请输入名称')
        return
      }
      if (this.dictionaries[listKey].includes(value)) {
        this.$message.warning('该项已存在')
        return
      }
      this.dictionaries[listKey].push(value)
      this.drafts[draftKey] = ''
      if (listKey === 'categories') {
        this.entryMissingCategories = this.entryMissingCategories.filter((c) => c !== value)
      }
    },
    removeItem(listKey, idx) {
      this.dictionaries[listKey].splice(idx, 1)
      if (listKey === 'categories') {
        this.loadEntryHints()
      }
    },
    async saveAll() {
      this.saving = true
      try {
        await updateUnits(this.units)
        await updateDictionaries({
          categories: this.dictionaries.categories,
          sources: this.dictionaries.sources,
          grades: this.dictionaries.grades
        })
        try {
          await syncFlavorCategoriesToPurifyDict(this.dictionaries.categories)
          if (this.$store && this.$store.dispatch) {
            await this.$store.dispatch('dict/loadDictDatas')
          }
        } catch (e) {
          console.warn('[flavor配置] 同步系统品类字典失败', e)
          this.$message.warning('可用鱼类已写入风味配置，但同步系统字典失败，采购下拉可能需刷新后重试')
        }
        this.$message.success('配置已保存')
        await this.load()
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.config-page {
  width: 100%;
}

.page-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 12px;
}

.section-title {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 650;
  color: #e8edf2;
}

.section-desc {
  margin: 0;
  color: #b7c4d2;
  font-size: 13px;
  line-height: 1.4;
}

.save-btn {
  border-radius: 10px;
  padding: 10px 18px;
}

.sub-tabs {
  display: flex;
  align-items: stretch;
  width: 100%;
  box-sizing: border-box;
  gap: 0;
  margin-bottom: 14px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.18);
}

.sub-tab {
  position: relative;
  border: none;
  outline: none;
  background: transparent;
  padding: 11px 18px;
  cursor: pointer;
  color: #b7c4d2;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.2;
  text-align: center;
  transition: color 0.15s ease;
}

.sub-tab:hover:not(.active) {
  color: #c5d0dc;
}

.sub-tab.active {
  color: #54907a;
  font-weight: 600;
}

.sub-tab.active::after {
  content: '';
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: -1px;
  height: 2px;
  border-radius: 2px 2px 0 0;
  background: #54907a;
}

.cfg-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 8px;
  padding: 18px 20px;
  margin-bottom: 14px;
  width: 100%;
  box-sizing: border-box;
}

.cfg-title {
  font-size: 16px;
  font-weight: 700;
  color: #e8edf2;
}

.cfg-sub {
  margin: 4px 0 16px;
  font-size: 13px;
  color: #b7c4d2;
}

.add-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 14px;
}

.add-row .soft-input {
  flex: 1;
}

.add-btn,
.add-pond-btn {
  border-radius: 10px;
  padding: 10px 16px;
  flex-shrink: 0;
}

.add-pond-btn {
  margin-bottom: 14px;
}

.soft-input >>> .el-input__inner {
  background: #132030;
  border-color: rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  height: 40px;
  line-height: 40px;
  color: #e8edf2;
}

.soft-input >>> .el-input__inner:focus {
  background: #132030;
  border-color: #54907a;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.tag-empty {
  color: #8a9aab;
  font-size: 13px;
  line-height: 28px;
}

.entry-fish-block {
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid rgba(124, 143, 161, 0.16);
}

.entry-fish-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 10px;
}

.entry-fish-title {
  font-size: 14px;
  font-weight: 650;
  color: #e8edf2;
}

.entry-fish-sub {
  margin: 4px 0 0;
}

.entry-fish-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.cfg-tag-adopted {
  opacity: 0.85;
}

.adopt-link {
  margin-left: 4px !important;
  padding: 0 2px !important;
  color: #6fb59a !important;
  font-size: 12px !important;
}

.adopted-mark {
  margin-left: 6px;
  font-size: 11px;
  color: #8a9aab;
}

.cfg-tag {
  background: #132030 !important;
  border: none !important;
  color: #c5d0dc !important;
  border-radius: 8px !important;
  height: 30px;
  line-height: 30px;
  padding: 0 10px;
  font-size: 13px;
}

.cfg-tag >>> .el-tag__close {
  color: #b7c4d2;
}

.cfg-tag >>> .el-tag__close:hover {
  background: rgba(124, 143, 161, 0.4);
  color: #c5d0dc;
}

.unit-card {
  max-width: 100%;
}

.unit-field {
  margin-bottom: 20px;
}

.unit-field:last-child {
  margin-bottom: 0;
}

.unit-label {
  font-size: 14px;
  font-weight: 600;
  color: #e8edf2;
  margin-bottom: 8px;
}

.unit-select {
  width: 100%;
}

.unit-select >>> .el-input__inner {
  background: #0f1a26;
  border-color: rgba(124, 143, 161, 0.22);
  border-radius: 12px;
  height: 42px;
  line-height: 42px;
}

.unit-hint {
  margin: 8px 0 0;
  font-size: 12px;
  color: #b7c4d2;
}

.info-list {
  width: 100%;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 4px;
  border-bottom: 1px solid #132030;
  font-size: 14px;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row span {
  color: #b7c4d2;
}

.info-row b {
  color: #e8edf2;
  font-weight: 600;
}

.pond-table {
  width: 100%;
}

.pond-table >>> th,
.pond-table >>> td {
  text-align: left;
}

.pond-table >>> .el-table__empty-text {
  color: #b7c4d2;
}

.danger-text {
  color: #f56c6c;
}

.btn-dark {
  background: #54907a !important;
  border-color: #54907a !important;
  color: #fff !important;
}

.btn-dark:hover,
.btn-dark:focus {
  background: #6fb59a !important;
  border-color: #6fb59a !important;
  color: #fff !important;
}

.wave-ico {
  display: inline-block;
  width: 14px;
  height: 12px;
  margin-right: 6px;
  vertical-align: -1px;
  background:
    radial-gradient(circle at 2px 6px, transparent 1.5px, rgba(232,237,242,0.55) 1.6px 2.2px, transparent 2.3px) 0 0 / 5px 12px repeat-x,
    radial-gradient(circle at 2px 6px, transparent 1.5px, rgba(232,237,242,0.55) 1.6px 2.2px, transparent 2.3px) 2.5px 3px / 5px 12px repeat-x;
}
</style>

<style>
/* append-to-body 弹窗需非 scoped */
.pond-dialog {
  border-radius: 14px;
  overflow: hidden;
}

.pond-dialog .el-dialog__header {
  padding: 18px 20px 8px;
}

.pond-dialog .el-dialog__title {
  font-size: 16px;
  font-weight: 700;
  color: #e8edf2;
}

.pond-dialog .el-dialog__body {
  padding: 8px 20px 4px;
}

.pond-dialog .el-dialog__footer {
  padding: 8px 20px 18px;
}

.pond-dialog .pond-form .el-form-item {
  margin-bottom: 14px;
}

.pond-dialog .pond-form .el-form-item__label {
  padding: 0 0 6px;
  line-height: 1.2;
  color: #e8edf2;
  font-weight: 600;
  font-size: 13px;
}

.pond-dialog .soft-input .el-input__inner,
.pond-dialog .unit-select .el-input__inner {
  background: #132030;
  border-color: rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  height: 40px;
  line-height: 40px;
  color: #e8edf2;
}

.pond-dialog .soft-textarea .el-textarea__inner {
  background: #132030;
  border-color: rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  min-height: 84px;
  color: #e8edf2;
}

.pond-dialog .pond-footer {
  display: inline-flex;
  gap: 10px;
}

.pond-dialog .btn-dark {
  background: #54907a !important;
  border-color: #54907a !important;
  color: #fff !important;
  border-radius: 10px;
  padding: 10px 18px;
}

.pond-dialog .btn-cancel {
  border-radius: 10px;
  padding: 10px 18px;
  color: #e8edf2 !important;
  border-color: rgba(124, 143, 161, 0.35) !important;
  background: transparent !important;
}

.pond-dialog .btn-cancel:hover,
.pond-dialog .btn-cancel:focus {
  color: #54907a !important;
  border-color: rgba(84, 144, 122, 0.45) !important;
  background: rgba(84, 144, 122, 0.1) !important;
}

.pond-dialog .btn-dark:hover,
.pond-dialog .btn-dark:focus {
  background: #6fb59a !important;
  border-color: #6fb59a !important;
  color: #fff !important;
}

.pond-dialog .wave-ico {
  display: inline-block;
  width: 14px;
  height: 10px;
  margin-right: 6px;
  vertical-align: middle;
  border-top: 2px solid rgba(124, 143, 161, 0.25);
  border-bottom: 2px solid rgba(124, 143, 161, 0.25);
  box-shadow: inset 0 3px 0 0 rgba(255,255,255,0.35);
  border-radius: 40%;
  opacity: 0.95;
}
</style>
