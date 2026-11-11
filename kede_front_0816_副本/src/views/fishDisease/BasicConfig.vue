<template>
  <div class="config-page">
    <header class="config-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">基础配置</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">管理支撑全流程的基础信息，减少输入成本，规范数据格式</p>
    </header>

    <div class="config-body">
      <div class="config-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          type="button"
          class="config-tab"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >{{ tab.label }}</button>
      </div>

      <div class="content-card">
        <!-- 养殖信息 -->
        <template v-if="activeTab === 'breeding'">
          <div class="section-head">
            <div>
              <h2 class="section-title">鱼品种管理</h2>
              <p class="section-desc">管理养殖鱼品种、养殖池编号、鱼苗来源等基础信息</p>
            </div>
            <button type="button" class="btn-add" @click="openSpeciesDialog()">
              + 添加品种
            </button>
          </div>
          <div v-loading="loading" class="item-list">
            <div v-for="item in species" :key="item.id" class="item-row">
              <div class="item-info">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-sub">{{ item.waterTypeLabel }}</div>
              </div>
              <div class="item-actions">
                <button type="button" class="icon-btn" title="编辑" @click="openSpeciesDialog(item)">
                  <i class="el-icon-edit-outline" />
                </button>
                <button type="button" class="icon-btn danger" title="删除" @click="handleDeleteSpecies(item)">
                  <i class="el-icon-delete" />
                </button>
              </div>
            </div>
            <div v-if="!loading && !species.length" class="list-empty">暂无品种数据</div>
          </div>

          <div class="section-head" style="margin-top: 28px">
            <div>
              <h2 class="section-title">鱼池管理</h2>
              <p class="section-desc">只读展示存塘记录（zk_pond），与全站编号一致；增删改请在「存塘管理」维护</p>
            </div>
          </div>
          <div v-loading="pondLoading" class="item-list">
            <div v-for="item in ponds" :key="item.id" class="item-row">
              <div class="item-info">
                <div class="item-name">
                  {{ item.code }}
                  <span class="src-tag">存塘</span>
                </div>
                <div class="item-sub">
                  {{ item.name && item.name !== item.code ? item.name + ' · ' : '' }}{{ item.location || item.workshop || '未填车间' }}{{ item.systemName ? ' · ' + item.systemName : '' }}{{ item.species ? ' · ' + item.species : '' }}
                </div>
              </div>
            </div>
            <div v-if="!pondLoading && !ponds.length" class="list-empty">暂无存塘鱼池，请先在「存塘管理」维护后再进行数据采集与协同诊断</div>
          </div>
        </template>

        <!-- 病害类型 -->
        <template v-else-if="activeTab === 'disease'">
          <div class="section-head">
            <div>
              <h2 class="section-title">病害类型预设</h2>
              <p class="section-desc">管理常见病害名称及分类</p>
            </div>
            <button type="button" class="btn-add" @click="openDiseaseDialog()">
              + 添加病害
            </button>
          </div>
          <div v-loading="loading" class="item-list">
            <div v-for="item in diseaseTypes" :key="item.id" class="item-row">
              <div class="item-info">
                <div class="item-name">{{ item.name }}</div>
                <div v-if="item.category" class="item-sub">{{ item.category }}</div>
              </div>
              <div class="item-actions">
                <button type="button" class="icon-btn" title="编辑" @click="openDiseaseDialog(item)">
                  <i class="el-icon-edit-outline" />
                </button>
                <button type="button" class="icon-btn danger" title="删除" @click="handleDeleteDisease(item)">
                  <i class="el-icon-delete" />
                </button>
              </div>
            </div>
            <div v-if="!loading && !diseaseTypes.length" class="list-empty">暂无病害类型</div>
          </div>
        </template>

        <!-- 设备配置 -->
        <template v-else>
          <div class="section-head">
            <div>
              <h2 class="section-title">设备关联配置</h2>
              <p class="section-desc">管理监测设备及数据采集频率</p>
            </div>
            <button type="button" class="btn-add" @click="openEquipmentDialog()">
              + 添加设备
            </button>
          </div>
          <div v-loading="loading" class="item-list">
            <div v-for="item in equipments" :key="item.id" class="item-row">
              <div class="item-info">
                <div class="item-name">{{ item.code }}</div>
                <div class="item-sub">{{ equipmentSubText(item) }}</div>
              </div>
              <div class="item-actions">
                <button type="button" class="icon-btn" title="编辑" @click="openEquipmentDialog(item)">
                  <i class="el-icon-edit-outline" />
                </button>
                <button type="button" class="icon-btn danger" title="删除" @click="handleDeleteEquipment(item)">
                  <i class="el-icon-delete" />
                </button>
              </div>
            </div>
            <div v-if="!loading && !equipments.length" class="list-empty">暂无设备配置</div>
          </div>
        </template>
      </div>
    </div>

    <el-dialog
      :title="speciesForm.id ? '编辑品种' : '添加品种'"
      :visible.sync="speciesVisible"
      width="420px"
      custom-class="disease-dialog"
    >
      <el-form ref="speciesRef" :model="speciesForm" :rules="speciesRules" label-position="top">
        <el-form-item label="品种名称" prop="name">
          <el-input v-model="speciesForm.name" placeholder="如 鲈鱼" />
        </el-form-item>
        <el-form-item label="水体类型" prop="waterType">
          <el-select v-model="speciesForm.waterType" style="width:100%">
            <el-option v-for="(label, key) in meta.waterTypeMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="btn-cancel" @click="speciesVisible = false">取消</el-button>
        <el-button class="btn-dark btn-save" :loading="saving" @click="saveSpecies">
          <svg class="save-icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M5 3h12l4 4v14a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
            <path d="M8 3v6h9V3" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
            <path d="M8 15h8v6H8z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
          </svg>
          保存
        </el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="diseaseForm.id ? '编辑病害' : '添加病害'"
      :visible.sync="diseaseVisible"
      width="420px"
      custom-class="disease-dialog"
    >
      <el-form ref="diseaseRef" :model="diseaseForm" :rules="diseaseRules" label-position="top">
        <el-form-item label="病害名称" prop="name">
          <el-input v-model="diseaseForm.name" placeholder="如 白点病" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="diseaseForm.category" placeholder="选择分类" style="width:100%">
            <el-option v-for="c in diseaseCategories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="btn-cancel" @click="diseaseVisible = false">取消</el-button>
        <el-button class="btn-dark btn-save" :loading="saving" @click="saveDisease">
          <svg class="save-icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M5 3h12l4 4v14a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
            <path d="M8 3v6h9V3" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
            <path d="M8 15h8v6H8z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
          </svg>
          保存
        </el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="equipmentForm.id ? '编辑设备' : '添加设备'"
      :visible.sync="equipmentVisible"
      width="420px"
      custom-class="disease-dialog config-select-dialog"
    >
      <el-form ref="equipmentRef" :model="equipmentForm" :rules="equipmentRules" label-position="top">
        <el-form-item label="设备编号" prop="code">
          <el-input v-model="equipmentForm.code" placeholder="如 DO-001" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select
            v-model="equipmentForm.type"
            placeholder="选择类型"
            style="width:100%"
            popper-class="config-select-dropdown"
          >
            <el-option
              v-for="item in equipmentTypes"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="采集频率" prop="frequency">
          <el-select
            v-model="equipmentForm.frequency"
            placeholder="选择频率"
            style="width:100%"
            popper-class="config-select-dropdown"
          >
            <el-option
              v-for="item in frequencyOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关联养殖池" prop="pondCodes">
          <el-select
            v-model="equipmentForm.pondCodes"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入池号，如 A1"
            style="width:100%"
            popper-class="config-select-dropdown"
          >
            <el-option v-for="code in pondCodeOptions" :key="code" :label="code" :value="code" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="btn-cancel" @click="equipmentVisible = false">取消</el-button>
        <el-button class="btn-dark btn-save" :loading="saving" @click="saveEquipment">
          <svg class="save-icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M5 3h12l4 4v14a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
            <path d="M8 3v6h9V3" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
            <path d="M8 15h8v6H8z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
          </svg>
          保存
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchMeta,
  fetchFishSpecies,
  createFishSpecies,
  updateFishSpecies,
  deleteFishSpecies,
  fetchDiseaseTypes,
  createDiseaseType,
  updateDiseaseType,
  deleteDiseaseType,
  fetchEquipments,
  createEquipment,
  updateEquipment,
  deleteEquipment
} from '@/api/fishDisease'
import { fetchMergedPonds, getPondSourceErrors } from '@/utils/pondBridge'
import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'

export default {
  name: 'BasicConfig',
  components: { MenuToggle, HeaderAuth },
  data() {
    return {
      loading: false,
      saving: false,
      activeTab: 'breeding',
      tabs: [
        { key: 'breeding', label: '养殖信息' },
        { key: 'disease', label: '病害类型' },
        { key: 'equipment', label: '设备配置' }
      ],
      species: [],
      ponds: [],
      pondLoading: false,
      diseaseTypes: [],
      equipments: [],
      pondCodeOptions: [],
      diseaseCategories: ['寄生虫', '细菌', '病毒', '霉菌', '营养', '环境'],
      equipmentTypes: ['溶氧监测', 'pH监测', '氨氮监测', '温度监测', '行为监测'],
      frequencyOptions: ['1分钟', '5分钟', '10分钟', '30分钟', '1小时'],
      meta: { waterTypeMap: {} },
      speciesVisible: false,
      diseaseVisible: false,
      equipmentVisible: false,
      speciesForm: this.emptySpeciesForm(),
      diseaseForm: this.emptyDiseaseForm(),
      equipmentForm: this.emptyEquipmentForm(),
      speciesRules: {
        name: [{ required: true, message: '请填写品种名称', trigger: 'blur' }],
        waterType: [{ required: true, message: '请选择水体类型', trigger: 'change' }]
      },
      diseaseRules: {
        name: [{ required: true, message: '请填写病害名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }]
      },
      equipmentRules: {
        code: [{ required: true, message: '请填写设备编号', trigger: 'blur' }],
        type: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
        frequency: [{ required: true, message: '请选择采集频率', trigger: 'change' }]
      }
    }
  },
  computed: {
    todayText() {
      const now = new Date()
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日${weekDays[now.getDay()]}`
    }
  },
  watch: {
    activeTab() {
      this.loadTabData()
    }
  },
  created() {
    this.init()
  },
  methods: {
    emptySpeciesForm() {
      return { id: '', name: '', waterType: 'marine' }
    },
    emptyDiseaseForm() {
      return { id: '', name: '', category: '', description: '' }
    },
    emptyEquipmentForm() {
      return { id: '', code: '', name: '', type: '', frequency: '', pondCodes: [] }
    },
    equipmentSubText(item) {
      const type = item.type || item.name || ''
      const freq = item.frequency || ''
      const ponds = Array.isArray(item.pondCodes) ? item.pondCodes.join('/') : (item.pondCode || '')
      const base = freq ? `${type} · 采集频率: ${freq}` : type
      return ponds ? `${base} · 池: ${ponds}` : base
    },
    async refreshPonds() {
      this.pondLoading = true
      try {
        const list = await fetchMergedPonds()
        this.ponds = Array.isArray(list) ? list : []
        this.pondCodeOptions = this.ponds.map(p => p.code || p.pondCode || p.poolCode).filter(Boolean)
        const errs = getPondSourceErrors(list)
        if (errs.length) {
          this.$message.warning('存塘鱼池加载失败：' + errs.map(e => e.message).join('；'))
        } else if (!this.ponds.length) {
          this.$message.warning('暂无存塘鱼池，请先在「存塘管理」维护')
        }
      } catch (e) {
        console.error('[鱼病配置] 鱼池加载失败', e)
        this.ponds = []
        this.pondCodeOptions = []
        this.$message.error('鱼池列表加载失败，请确认后端存塘接口可用')
      } finally {
        this.pondLoading = false
      }
    },
    async init() {
      try {
        const metaRes = await fetchMeta()
        this.meta = metaRes.data
        await this.refreshPonds()
        await this.loadTabData()
      } catch (e) {
        this.$message.error('加载配置失败，请确认统一 BFF 已启动（端口 3020）')
      }
    },
    async loadTabData() {
      this.loading = true
      try {
        if (this.activeTab === 'breeding') {
          const res = await fetchFishSpecies()
          this.species = res.data
          await this.refreshPonds()
        } else if (this.activeTab === 'disease') {
          const res = await fetchDiseaseTypes()
          this.diseaseTypes = res.data
        } else {
          const res = await fetchEquipments()
          this.equipments = res.data
        }
      } catch (e) {
        if (this.activeTab === 'breeding') this.species = []
        else if (this.activeTab === 'disease') this.diseaseTypes = []
        else this.equipments = []
      } finally {
        this.loading = false
      }
    },
    openSpeciesDialog(row) {
      this.speciesForm = row && row.id ? { ...row } : this.emptySpeciesForm()
      this.speciesVisible = true
    },
    saveSpecies() {
      this.$refs.speciesRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          if (this.speciesForm.id) {
            await updateFishSpecies(this.speciesForm.id, this.speciesForm)
          } else {
            await createFishSpecies(this.speciesForm)
          }
          this.$message.success('保存成功')
          this.speciesVisible = false
          const res = await fetchFishSpecies()
          this.species = res.data
        } finally {
          this.saving = false
        }
      })
    },
    handleDeleteSpecies(row) {
      this.$confirm(`确认删除品种「${row.name}」？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteFishSpecies(row.id)
          this.$message.success('已删除')
          const res = await fetchFishSpecies()
          this.species = res.data
        })
        .catch(() => {})
    },
    openDiseaseDialog(row) {
      this.diseaseForm = row && row.id ? { ...row } : this.emptyDiseaseForm()
      this.diseaseVisible = true
    },
    saveDisease() {
      this.$refs.diseaseRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          if (this.diseaseForm.id) {
            await updateDiseaseType(this.diseaseForm.id, this.diseaseForm)
          } else {
            await createDiseaseType(this.diseaseForm)
          }
          this.$message.success('保存成功')
          this.diseaseVisible = false
          const res = await fetchDiseaseTypes()
          this.diseaseTypes = res.data
        } finally {
          this.saving = false
        }
      })
    },
    handleDeleteDisease(row) {
      this.$confirm(`确认删除病害类型「${row.name}」？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteDiseaseType(row.id)
          this.$message.success('已删除')
          const res = await fetchDiseaseTypes()
          this.diseaseTypes = res.data
        })
        .catch(() => {})
    },
    openEquipmentDialog(row) {
      this.equipmentForm = row && row.id
        ? { ...this.emptyEquipmentForm(), ...row, pondCodes: Array.isArray(row.pondCodes) ? [...row.pondCodes] : (row.pondCode ? [row.pondCode] : []) }
        : this.emptyEquipmentForm()
      this.equipmentVisible = true
    },
    saveEquipment() {
      this.$refs.equipmentRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const payload = {
            ...this.equipmentForm,
            name: this.equipmentForm.type,
            pondCodes: Array.isArray(this.equipmentForm.pondCodes) ? this.equipmentForm.pondCodes : []
          }
          if (this.equipmentForm.id) {
            await updateEquipment(this.equipmentForm.id, payload)
          } else {
            await createEquipment(payload)
          }
          this.$message.success('保存成功')
          this.equipmentVisible = false
          const res = await fetchEquipments()
          this.equipments = res.data
        } finally {
          this.saving = false
        }
      })
    },
    handleDeleteEquipment(row) {
      this.$confirm(`确认删除设备「${row.name || row.code}」？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteEquipment(row.id)
          this.$message.success('已删除')
          const res = await fetchEquipments()
          this.equipments = res.data
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.config-page {
  min-height: 100vh;
  background: #0a1520;
  position: relative;
}

.config-header {
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

.config-body {
  padding: 0 40px 48px;
}

.config-tabs {
  display: flex;
  width: 100%;
  background: rgba(124, 143, 161, 0.25);
  border-radius: 999px;
  padding: 4px;
  margin-bottom: 16px;
}

.config-tab {
  flex: 1;
  border: none;
  background: transparent;
  border-radius: 999px;
  padding: 11px 24px;
  font-size: 14px;
  color: #7c8fa1;
  cursor: pointer;
  transition: all 0.15s;
  line-height: 1.4;
}

.config-tab.active {
  background: #0f1a26;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  color: #e8edf2;
  font-weight: 500;
}

.content-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 12px;
  padding: 28px 32px 32px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.section-title {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 600;
  color: #e8edf2;
}

.section-desc {
  margin: 0;
  font-size: 14px;
  color: #7c8fa1;
  line-height: 1.5;
}

.btn-add {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: #409eff;
  color: #fff;
  font-size: 14px;
  padding: 10px 22px;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  font-weight: 500;
  line-height: 1;
}

.btn-add:hover {
  background: #66b1ff;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  background: #0f1a26;
  transition: border-color 0.15s;
}

.item-row:hover {
  border-color: rgba(124, 143, 161, 0.25);
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: #e8edf2;
  line-height: 1.4;
}

.src-tag {
  margin-left: 8px;
  font-size: 11px;
  font-weight: 500;
  color: #7dd3fc;
  background: rgba(56, 189, 248, 0.12);
  border: 1px solid rgba(56, 189, 248, 0.28);
  border-radius: 4px;
  padding: 1px 6px;
  vertical-align: middle;
}

.item-sub {
  margin-top: 4px;
  font-size: 14px;
  color: #7c8fa1;
  font-weight: 400;
  line-height: 1.4;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.icon-btn {
  width: 38px;
  height: 38px;
  border: 1px solid rgba(124, 143, 161, 0.25);
  background: #0f1a26;
  border-radius: 8px;
  color: #e8edf2;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  transition: background 0.15s, border-color 0.15s;
}

.icon-btn:hover {
  background: #0a1520;
  border-color: rgba(124, 143, 161, 0.35);
}

.icon-btn.danger {
  color: #f53f3f;
  border-color: rgba(124, 143, 161, 0.25);
}

.icon-btn.danger:hover {
  background: rgba(245, 63, 63, 0.15);
  border-color: rgba(245, 63, 63, 0.35);
}

.list-empty {
  padding: 48px 16px;
  text-align: center;
  color: #7c8fa1;
  font-size: 13px;
}

@media (max-width: 768px) {
  .config-header,
  .config-body {
    padding-left: 16px;
    padding-right: 16px;
  }

  .header-title {
    font-size: 22px;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

<style>
.config-select-dialog .el-form-item__label {
  font-weight: 600;
  color: #e8edf2;
  padding-bottom: 8px;
  line-height: 1.4;
}

.config-select-dialog .el-input__inner,
.config-select-dialog .el-select .el-input__inner {
  height: 44px;
  line-height: 44px;
  background: rgba(64, 158, 255, 0.1);
  border: none;
  border-radius: 10px;
  color: #e8edf2;
  padding-left: 14px;
}

.config-select-dialog .el-select .el-input__inner:focus,
.config-select-dialog .el-input__inner:focus {
  background: rgba(124, 143, 161, 0.22);
}

.config-select-dialog .el-select .el-input .el-select__caret {
  color: #5a6b7d;
  font-weight: 600;
}
</style>
