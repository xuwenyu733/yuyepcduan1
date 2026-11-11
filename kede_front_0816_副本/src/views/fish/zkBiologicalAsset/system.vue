<template>
  <div class="app-container">
    <PageLoadingMask
      :visible="pageLoading && !(pondProductivity && pondProductivity.length) && !(fishBySpecies && fishBySpecies.length)"
      text="正在加载生物资产数据…"
    />
    <header class="header">
      <div class="header-content">
        <h1 class="title">循环水养殖场生物资产评估系统</h1>
        <p class="subtitle">Recirculating Aquaculture System - Asset Management</p>
      </div>
    </header>

    <nav class="nav">
      <div class="nav-content">
        <el-menu :default-active="currentView" mode="horizontal" @select="handleSelect">
          <el-menu-item index="dashboard">总体概览</el-menu-item>
          <!-- <el-menu-item index="ponds">鱼池管理</el-menu-item>
          <el-menu-item index="feed">饲料库存</el-menu-item>
          <el-menu-item index="equipment">设备管理</el-menu-item> -->
        </el-menu>
      </div>
    </nav>

    <main class="main-content">
      <!-- Dashboard View -->
      <div v-if="currentView === 'dashboard'" class="dashboard-view">
        <!-- 全局：时间回溯 + 导出；各图自带车间/系统/池号/品种/批次 -->
        <el-form :inline="true" size="small" class="asset-filter-bar" @submit.native.prevent>
          <el-form-item label="统计周期">
            <el-select v-model="periodType" placeholder="日/周/月" style="width: 100px;" @change="onPeriodChange">
              <el-option label="日" value="day" />
              <el-option label="周" value="week" />
              <el-option label="月" value="month" />
            </el-select>
          </el-form-item>
          <el-form-item label="自定义时间">
            <el-date-picker
              v-model="customDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始"
              end-placeholder="结束"
              value-format="yyyy-MM-dd"
              style="width: 240px;"
              @change="onPeriodChange"
            />
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-refresh" @click="resetAllChartFilters">重置筛选</el-button>
            <el-button type="primary" plain icon="el-icon-download" :loading="exporting" @click="exportAssetExcel">导出 Excel</el-button>
            <el-button type="text" icon="el-icon-setting" @click="openFishPriceDialog">鱼类价格</el-button>
          </el-form-item>
          <el-form-item v-if="snapshotDate">
            <el-tag size="small" type="info" effect="plain">存档日 {{ snapshotDate }} · {{ periodLabel }}</el-tag>
          </el-form-item>
        </el-form>

        <el-row :gutter="20" class="summary-cards">
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card">
              <div class="card-content">
                <div>
                  <div class="card-title">生物性资产总值</div>
                  <div class="card-value">¥{{ formatWan(summaryTotals.value) }}万</div>
                </div>
                <i class="el-icon-s-data icon-blue"></i>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card">
              <div class="card-content">
                <div>
                  <div class="card-title">总尾数</div>
                  <div class="card-value">{{ formatInt(summaryTotals.count) }} <span class="card-unit">尾</span></div>
                </div>
                <i class="el-icon-s-help icon-green"></i>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card">
              <div class="card-content">
                <div>
                  <div class="card-title">总重量</div>
                  <div class="card-value">{{ formatTon(summaryTotals.weightKg) }} <span class="card-unit">吨</span></div>
                </div>
                <i class="el-icon-s-order icon-amber"></i>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row">
          <el-col :span="12">
            <el-card shadow="hover" class="chart-panel">
              <div slot="header" class="card-header-flex">
                <span>品种分布（价值）</span>
              </div>
              <div class="chart-filter-bar">
                <el-select v-model="chartFilters.pie.workshop" placeholder="车间" clearable filterable size="mini" style="width: 100px;" @change="onChartWorkshopChange('pie')">
                  <el-option v-for="(item, idx) in workshopOptions" :key="'pie-w-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.pie.system" placeholder="系统" clearable filterable size="mini" style="width: 100px;" @change="onChartSystemChange('pie')">
                  <el-option v-for="(item, idx) in chartSystemOptions('pie')" :key="'pie-s-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.pie.pond" placeholder="池号" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('pie')">
                  <el-option v-for="(item, idx) in chartPondOptions('pie')" :key="'pie-p-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.pie.species" placeholder="品种" clearable filterable size="mini" style="width: 100px;" @change="applyChartFilter('pie')">
                  <el-option v-for="item in speciesOptions" :key="'pie-sp-'+item" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.pie.batch" placeholder="批次" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('pie')">
                  <el-option v-for="item in batchOptions" :key="'pie-b-'+item" :label="item" :value="item" />
                </el-select>
              </div>
              <div ref="pieChart" class="chart-box"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="chart-panel">
              <div slot="header" class="card-header-flex">
                <span>各规格价值分布</span>
              </div>
              <div class="chart-filter-bar">
                <el-select v-model="chartFilters.value.workshop" placeholder="车间" clearable filterable size="mini" style="width: 100px;" @change="onChartWorkshopChange('value')">
                  <el-option v-for="(item, idx) in workshopOptions" :key="'val-w-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.value.system" placeholder="系统" clearable filterable size="mini" style="width: 100px;" @change="onChartSystemChange('value')">
                  <el-option v-for="(item, idx) in chartSystemOptions('value')" :key="'val-s-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.value.pond" placeholder="池号" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('value')">
                  <el-option v-for="(item, idx) in chartPondOptions('value')" :key="'val-p-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.value.species" placeholder="品种" clearable filterable size="mini" style="width: 100px;" @change="applyChartFilter('value')">
                  <el-option v-for="item in speciesOptions" :key="'val-sp-'+item" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.value.batch" placeholder="批次" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('value')">
                  <el-option v-for="item in batchOptions" :key="'val-b-'+item" :label="item" :value="item" />
                </el-select>
              </div>
              <div ref="valueSpecChart" class="chart-box"></div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card shadow="hover" class="chart-panel">
              <div slot="header" class="card-header-flex">
                <span>各规格数量分布</span>
              </div>
              <div class="chart-filter-bar">
                <el-select v-model="chartFilters.qty.workshop" placeholder="车间" clearable filterable size="mini" style="width: 100px;" @change="onChartWorkshopChange('qty')">
                  <el-option v-for="(item, idx) in workshopOptions" :key="'qty-w-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.qty.system" placeholder="系统" clearable filterable size="mini" style="width: 100px;" @change="onChartSystemChange('qty')">
                  <el-option v-for="(item, idx) in chartSystemOptions('qty')" :key="'qty-s-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.qty.pond" placeholder="池号" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('qty')">
                  <el-option v-for="(item, idx) in chartPondOptions('qty')" :key="'qty-p-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.qty.species" placeholder="品种" clearable filterable size="mini" style="width: 100px;" @change="applyChartFilter('qty')">
                  <el-option v-for="item in speciesOptions" :key="'qty-sp-'+item" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.qty.batch" placeholder="批次" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('qty')">
                  <el-option v-for="item in batchOptions" :key="'qty-b-'+item" :label="item" :value="item" />
                </el-select>
              </div>
              <div ref="qtySpecChart" class="chart-box"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="chart-panel">
              <div slot="header" class="card-header-flex">
                <span>各规格重量分布</span>
              </div>
              <div class="chart-filter-bar">
                <el-select v-model="chartFilters.weight.workshop" placeholder="车间" clearable filterable size="mini" style="width: 100px;" @change="onChartWorkshopChange('weight')">
                  <el-option v-for="(item, idx) in workshopOptions" :key="'wt-w-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.weight.system" placeholder="系统" clearable filterable size="mini" style="width: 100px;" @change="onChartSystemChange('weight')">
                  <el-option v-for="(item, idx) in chartSystemOptions('weight')" :key="'wt-s-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.weight.pond" placeholder="池号" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('weight')">
                  <el-option v-for="(item, idx) in chartPondOptions('weight')" :key="'wt-p-'+idx" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.weight.species" placeholder="品种" clearable filterable size="mini" style="width: 100px;" @change="applyChartFilter('weight')">
                  <el-option v-for="item in speciesOptions" :key="'wt-sp-'+item" :label="item" :value="item" />
                </el-select>
                <el-select v-model="chartFilters.weight.batch" placeholder="批次" clearable filterable size="mini" style="width: 120px;" @change="applyChartFilter('weight')">
                  <el-option v-for="item in batchOptions" :key="'wt-b-'+item" :label="item" :value="item" />
                </el-select>
              </div>
              <div ref="weightSpecChart" class="chart-box"></div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- Other Views Placeholders -->
      <div v-else-if="currentView === 'ponds'" class="other-view" style="padding: 0; overflow: hidden;width: 100%;">
        <zk-pond />
      </div>
      <div v-else-if="currentView === 'feed'" class="other-view" style="padding: 0; overflow: hidden;">
        <product :default-category-id="1" />
      </div>
      <div v-else-if="currentView === 'equipment'" class="other-view" style="padding: 0; overflow: hidden;">
        <!-- <el-empty description="设备管理内容"></el-empty> -->
        <zk-device />
      </div>
    </main>

    <!-- 鱼类资产价格列表对话框 -->
    <el-dialog title="鱼类资产价格设置" :visible.sync="fishPriceDialogVisible" width="800px" append-to-body>
      <div class="mb8" style="margin-bottom: 10px;">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddFishPrice">新增</el-button>
      </div>

      <el-table v-loading="fishPriceLoading" :data="fishPriceList">
        <el-table-column label="鱼类别" align="center" prop="label" />
        <!-- <el-table-column label="字典键值" align="center" prop="value" /> -->
        <el-table-column label="鱼对应价格(元/Kg)" align="center" prop="remark" />
        <!-- <el-table-column label="显示排序" align="center" prop="sort" /> -->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template v-slot="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditFishPrice(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加或修改鱼类价格对话框 -->
      <el-dialog :title="fishPriceFormTitle" :visible.sync="fishPriceFormVisible" width="500px" append-to-body>
        <el-form ref="fishPriceForm" :model="fishPriceForm" :rules="fishPriceRules" label-width="150px">
          <el-form-item label="鱼类别" prop="label">
            <el-input v-model="fishPriceForm.label" placeholder="请输入鱼类别" />
          </el-form-item>
          <!-- <el-form-item label="字典键值" prop="value">
            <el-input v-model="fishPriceForm.value" placeholder="请输入数据键值" />
          </el-form-item> -->
          <el-form-item label="对应价格(元/Kg)" prop="remark">
            <el-input v-model="fishPriceForm.remark" placeholder="请输入鱼对应价格(元/Kg)" />
          </el-form-item>
          <!-- <el-form-item label="显示排序" prop="sort">
            <el-input-number v-model="fishPriceForm.sort" controls-position="right" :min="0" />
          </el-form-item> -->
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitFishPriceForm">确 定</el-button>
          <el-button @click="cancelFishPriceForm">取 消</el-button>
        </div>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import ZkPond from '../zkPond/index.vue';
import ZkDevice from '../zkDevice/index.vue';
import Product from '../../erp/product/product/index.vue';
import { getZkBiologicalAssetDashboard } from '@/api/fish/zkBiologicalAsset';
import { listData, getData, addData, updateData } from "@/api/system/dict/data";
import {
  loadSharedSiteOptions,
  pickWorkshopOptionLabels,
  pickSystemOptionLabels,
  pickPondOptionLabels,
  rowMatchesSiteFilters
} from '@/utils/siteOptions';
import { collectBatchFilterOptions, rowMatchesBatchFilter } from '@/utils/batchTokens';
import PageLoadingMask from '@/components/PageLoadingMask/index';
import { DICT_TYPE, getDictDataLabel } from '@/utils/dict';
import {
  normalizeSpeciesName,
  speciesNamesEqual,
  collectSpeciesFilterOptions
} from '@/utils/speciesTempBridge';

/** 规格区间：与需求/效果图一致 */
const SPEC_BUCKETS = [
  { key: '0-1', label: '0-1g', min: 0, max: 1 },
  { key: '1-5', label: '1-5g', min: 1, max: 5 },
  { key: '5-25', label: '5-25g', min: 5, max: 25 },
  { key: '25-250', label: '25-250g', min: 25, max: 250 },
  { key: '250-1000', label: '250-1000g', min: 250, max: 1000 },
  { key: '1000+', label: '1000g以上', min: 1000, max: Infinity }
]
const SPECIES_COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#c084fc', '#ef4444', '#06b6d4', '#f472b6']

function emptyChartFilter() {
  return { workshop: '', system: '', pond: '', species: '', batch: '' }
}

export default {
  name: 'BiologicalAssetSystem',
  components: {
    ZkPond,
    ZkDevice,
    Product,
    PageLoadingMask
  },
  data() {
    return {
      currentView: 'dashboard',
      pageLoading: false,
      totalAssets: {
        fish: 0,
        feed: 0,
        equipment: 0,
        total: 0,
        fishCount: 0,
        feedCount: 0,
        equipmentCount: 0
      },
      fishBySpecies: [],
      pondProductivity: [],
      feedConsumption: [],
      exporting: false,
      periodType: 'day',
      customDateRange: null,
      snapshotDate: '',
      asOfDate: '',
      // 各图独立筛选（车间、系统、池号、品种、批次）
      chartFilters: {
        pie: emptyChartFilter(),
        value: emptyChartFilter(),
        qty: emptyChartFilter(),
        weight: emptyChartFilter()
      },
      siteOptions: null,
      speciesOptions: [],
      batchOptions: [],
      kpi: {
        avgLive: 0,
        feedConversionRatio: 0,
        waterRecyclingRate: 98.5,
        dailyWeightGain: 0,
        estimatedHarvestTime: '45天'
      },
      charts: {},
      // 鱼类价格列表相关
      fishPriceDialogVisible: false,
      fishPriceLoading: false,
      fishPriceList: [],
      fishPriceFormVisible: false,
      fishPriceFormTitle: '',
      fishPriceForm: {},
      fishPriceRules: {
        label: [{ required: true, message: "鱼类别不能为空", trigger: "blur" }],
        value: [{ required: true, message: "数据键值不能为空", trigger: "blur" }],
        remark: [{ required: true, message: "对应价格不能为空", trigger: "blur" }],
        sort: [{ required: true, message: "显示排序不能为空", trigger: "blur" }]
      }
    };
  },
  computed: {
    workshopOptions() {
      return pickWorkshopOptionLabels(this.siteOptions)
    },
    /** 与饵料系数同款：自定义区间优先，否则日/周/月 */
    periodLabel() {
      if (this.customDateRange && this.customDateRange.length === 2) {
        return `${this.customDateRange[0]} ~ ${this.customDateRange[1]}`
      }
      return ({ day: '日周期', week: '周周期', month: '月周期' })[this.periodType] || '周期'
    },
    periodDays() {
      if (this.customDateRange && this.customDateRange.length === 2) {
        const a = new Date(this.customDateRange[0]).getTime()
        const b = new Date(this.customDateRange[1]).getTime()
        return Math.max(1, Math.round((b - a) / 86400000) + 1)
      }
      return ({ day: 1, week: 7, month: 30 })[this.periodType] || 1
    },
    /** 传给仪表盘：起止日；存档取 ≤ endDate 的最近日快照 */
    archiveDateRange() {
      const pad = (n) => String(n).padStart(2, '0')
      const fmt = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
      if (this.customDateRange && this.customDateRange.length === 2) {
        return { startDate: this.customDateRange[0], endDate: this.customDateRange[1] }
      }
      const end = new Date()
      const start = new Date(end.getTime() - (this.periodDays - 1) * 86400000)
      return { startDate: fmt(start), endDate: fmt(end) }
    },
    /** 顶部三卡：全量存塘汇总（不受各图筛选影响） */
    summaryTotals() {
      const ponds = this.pondProductivity || []
      let value = 0
      let count = 0
      let weightKg = 0
      ponds.forEach((p) => {
        const m = this.pondMetric(p)
        value += m.value
        count += m.count
        weightKg += m.weightKg
      })
      if (!ponds.length) {
        value = Number(this.totalAssets.fish) || 0
        count = Number(this.totalAssets.fishCount) || 0
      }
      return { value, count, weightKg }
    },
    /** 导出仍用全量池数据 */
    filteredPondProductivity() {
      return this.pondProductivity || []
    },
  },
  mounted() {
    this.loadSiteOptions();
    this.fetchDashboardData();
    window.addEventListener('resize', this.handleResize);
  },
  activated() {
    // 118：keep-alive 再次进入时若无数据则重拉，避免偶发空白
    if (!this.siteOptions) this.loadSiteOptions();
    if (!this.pondProductivity || !this.pondProductivity.length) {
      this.fetchDashboardData();
    } else {
      this.$nextTick(() => this.initCharts && this.initCharts());
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    Object.values(this.charts).forEach(chart => chart && chart.dispose());
  },
  methods: {
    async loadSiteOptions() {
      try {
        this.siteOptions = await loadSharedSiteOptions({ force: false });
      } catch (e) {
        console.warn('[bioAsset] site options', e);
        this.siteOptions = this.siteOptions || null;
      }
    },
    fetchDashboardData() {
      this.pageLoading = !(this.pondProductivity && this.pondProductivity.length) && !(this.fishBySpecies && this.fishBySpecies.length);
      const range = this.archiveDateRange || {};
      const query = {
        startDate: range.startDate,
        endDate: range.endDate
      };
      const apply = (data) => {
        if (!data) return false;
        this.totalAssets = data.totalAssets || this.totalAssets;
        if (this.totalAssets) {
          this.totalAssets.total = Number(this.totalAssets.fish) || 0;
        }
        this.fishBySpecies = data.fishBySpecies || [];
        this.pondProductivity = data.pondProductivity || [];
        this.feedConsumption = data.feedConsumption || [];
        this.kpi = data.kpi || this.kpi;
        this.snapshotDate = data.snapshotDate || range.endDate || '';
        this.asOfDate = data.asOfDate || range.endDate || '';
        // 品种名：字典 value(1/3/4) → 标签；饼图无数据时从存塘产量汇总
        this.fishBySpecies = this.normalizeFishBySpecies(this.fishBySpecies, this.pondProductivity);
        this.speciesOptions = this.buildSpeciesOptions(this.fishBySpecies, this.pondProductivity);
        this.batchOptions = collectBatchFilterOptions(this.pondProductivity);
        // 车间/系统/池只来自生产记录三表，不再用仪表盘行反推
        if (!this.siteOptions) this.loadSiteOptions();
        this.$nextTick(() => {
          this.initCharts();
        });
        return (this.pondProductivity && this.pondProductivity.length) || (this.fishBySpecies && this.fishBySpecies.length);
      };
      const done = () => { this.pageLoading = false; };
      getZkBiologicalAssetDashboard(query).then(response => {
        const ok = apply(response && response.data);
        if (!ok) {
          setTimeout(() => {
            getZkBiologicalAssetDashboard(query).then(res2 => {
              if (!apply(res2 && res2.data)) {
                this.$message && this.$message.warning('生物资产数据暂时为空，请稍后刷新');
              }
            }).catch(() => {}).finally(done);
          }, 600);
        } else {
          done();
        }
      }).catch(e => {
        console.error('[bioAsset] dashboard', e);
        this.$message && this.$message.warning('生物资产评估加载失败，正在重试…');
        setTimeout(() => {
          getZkBiologicalAssetDashboard(query).then(res => apply(res && res.data)).catch(() => {
            this.$message && this.$message.error('生物资产评估仍无法加载');
          }).finally(done);
        }, 800);
      });
    },
    /** 字典码 → 统一展示名（三文鱼/鲑鱼→大西洋鲑，鲈鱼苗→加州鲈） */
    resolveSpeciesLabel(raw) {
      if (raw == null || raw === '') return '';
      const s = String(raw).trim();
      if (!s || s === '未知') return '';
      // 先走全站归一（含字典码 /「null,4」脏串）
      const canon = normalizeSpeciesName(s);
      if (canon) return canon;
      try {
        const label = getDictDataLabel(DICT_TYPE.ZK_POND_FISH_TYPES, s);
        if (label && String(label).trim()) {
          return normalizeSpeciesName(label) || String(label).trim();
        }
      } catch (e) { /* ignore */ }
      if (/^\d+$/.test(s)) return '';
      return '';
    },
    /** 从存塘产量行汇总品种分布（数量/价值）；同归一名合并 */
    rebuildSpeciesFromPonds(ponds) {
      const map = {};
      (ponds || []).forEach((row) => {
        const name = this.resolveSpeciesLabel(row && (row.fishName || row.fishTypes));
        if (!name) return;
        const qty = Number(row.fishNum != null ? row.fishNum : row.fish) || 0;
        const val = Number(row.capacity != null ? row.capacity : 0) || 0;
        if (!map[name]) map[name] = { name, quantity: 0, value: 0 };
        map[name].quantity += qty;
        map[name].value += val;
      });
      return Object.keys(map).map((k) => map[k]);
    },
    normalizeFishBySpecies(apiList, ponds) {
      const map = {};
      (apiList || []).forEach((item) => {
        const name = this.resolveSpeciesLabel(item && item.name) || this.resolveSpeciesLabel(item && item.fishTypes);
        if (!name) return;
        const quantity = Number(item && item.quantity) || 0;
        const value = Number(item && item.value) || 0;
        if (!map[name]) map[name] = { name, quantity: 0, value: 0 };
        map[name].quantity += quantity;
        map[name].value += value;
      });
      const mapped = Object.keys(map).map((k) => map[k]);
      const hasQty = mapped.some((i) => i.quantity > 0);
      if (mapped.length && hasQty) return mapped;
      return this.rebuildSpeciesFromPonds(ponds);
    },
    buildSpeciesOptions(speciesList, ponds) {
      return collectSpeciesFilterOptions([
        ...(speciesList || []),
        ...(ponds || [])
      ]);
    },
    rowMatchesSpecies(item, filterSpecies) {
      if (!filterSpecies || !item) return true
      if (!String(filterSpecies).trim()) return true
      let dictLabel = ''
      try {
        dictLabel = getDictDataLabel(DICT_TYPE.ZK_POND_FISH_TYPES, item.fishTypes) || ''
      } catch (e) {
        dictLabel = ''
      }
      const resolved = this.resolveSpeciesLabel(item.fishName || item.fishTypes || item.name)
      const cands = [item.fishName, item.species, item.fishTypes, item.name, dictLabel, resolved]
      return cands.some((c) => c != null && c !== '' && speciesNamesEqual(c, filterSpecies))
    },
    applyDashboardFilter() {
      this.$nextTick(() => this.initCharts())
    },
    emptyChartFilter() {
      return emptyChartFilter()
    },
    chartSystemOptions(chartKey) {
      const f = (this.chartFilters && this.chartFilters[chartKey]) || {}
      return pickSystemOptionLabels(this.siteOptions, f.workshop || '')
    },
    chartPondOptions(chartKey) {
      const f = (this.chartFilters && this.chartFilters[chartKey]) || {}
      return pickPondOptionLabels(this.siteOptions, f.workshop || '', f.system || '')
    },
    onChartWorkshopChange(chartKey) {
      const f = this.chartFilters[chartKey]
      if (!f) return
      const systems = this.chartSystemOptions(chartKey)
      if (f.system && systems.indexOf(f.system) < 0) f.system = ''
      const ponds = this.chartPondOptions(chartKey)
      if (f.pond && ponds.indexOf(f.pond) < 0) f.pond = ''
      this.applyChartFilter(chartKey)
    },
    onChartSystemChange(chartKey) {
      const f = this.chartFilters[chartKey]
      if (!f) return
      const ponds = this.chartPondOptions(chartKey)
      if (f.pond && ponds.indexOf(f.pond) < 0) f.pond = ''
      this.applyChartFilter(chartKey)
    },
    applyChartFilter(chartKey) {
      this.$nextTick(() => {
        if (chartKey === 'pie') this.initPieChart()
        else if (chartKey === 'value') this.initSpecMetricChart('value')
        else if (chartKey === 'qty') this.initSpecMetricChart('qty')
        else if (chartKey === 'weight') this.initSpecMetricChart('weight')
        else this.initCharts()
      })
    },
    resetAllChartFilters() {
      this.chartFilters = {
        pie: emptyChartFilter(),
        value: emptyChartFilter(),
        qty: emptyChartFilter(),
        weight: emptyChartFilter()
      }
      this.periodType = 'day'
      this.customDateRange = null
      this.fetchDashboardData()
    },
    resetDashboardFilter() {
      this.resetAllChartFilters()
    },
    onPeriodChange() {
      this.fetchDashboardData()
    },
    formatWan(value) {
      const n = Number(value) || 0
      return (n / 10000).toFixed(1)
    },
    formatInt(value) {
      const n = Math.round(Number(value) || 0)
      return n.toLocaleString('zh-CN')
    },
    formatTon(weightKg) {
      const n = Number(weightKg) || 0
      return (n / 1000).toFixed(1)
    },
    /** 解析规格为克数：支持 300 / 300.000 / 10g/尾 / 300g */
    parseSpecGrams(raw) {
      if (raw == null || raw === '') return null
      const s = String(raw).trim()
      if (!s || s === '未知' || s === '-') return null
      const m = s.replace(/,/g, '').match(/(\d+(?:\.\d+)?)/)
      if (!m) return null
      const n = Number(m[1])
      return Number.isFinite(n) ? n : null
    },
    formatActualSpec(grams) {
      if (grams == null || !Number.isFinite(grams)) return ''
      const n = Math.round(grams * 1000) / 1000
      return `${n}g`
    },
    /** 同名品种跨饼图/柱图使用固定色，避免筛选后颜色跳动 */
    speciesColor(name, fallbackIndex) {
      const list = this.speciesOptions && this.speciesOptions.length
        ? this.speciesOptions
        : []
      const idx = list.indexOf(name)
      const i = idx >= 0 ? idx : (fallbackIndex || 0)
      return SPECIES_COLORS[i % SPECIES_COLORS.length]
    },
    resolveSpecBucket(grams) {
      if (grams == null || !Number.isFinite(grams) || grams < 0) return null
      for (let i = 0; i < SPEC_BUCKETS.length; i++) {
        const b = SPEC_BUCKETS[i]
        if (grams >= b.min && grams < b.max) return b
        if (b.max === Infinity && grams >= b.min) return b
      }
      return null
    },
    enrichPondRow(item) {
      if (!item) return item
      const siteRow = ((this.siteOptions && this.siteOptions.rows) || []).find((r) =>
        String(r.id) === String(item.pondId || item.id) ||
        (item.pondDetailNum && String(r.pondDetailNum || r.code) === String(item.pondDetailNum)) ||
        (item.name && String(r.pondDetailNum || r.code) === String(item.name))
      )
      return siteRow
        ? Object.assign({}, item, {
            workshopId: siteRow.workshopId != null ? siteRow.workshopId : item.workshopId,
            pondSystemId: siteRow.pondSystemId != null ? siteRow.pondSystemId : item.pondSystemId,
            workshopName: siteRow.workshopName || item.workshopName || item.workshop,
            pondSystemName: siteRow.pondSystemName || item.pondSystemName || item.system,
            pondDetailNum: siteRow.pondDetailNum || item.pondDetailNum || item.name,
            pondName: siteRow.pondName || item.pondName || item.name
          })
        : item
    },
    filterPondsByChart(chartKey) {
      const f = (this.chartFilters && this.chartFilters[chartKey]) || emptyChartFilter()
      return (this.pondProductivity || []).filter((item) => {
        const row = this.enrichPondRow(item)
        if (!rowMatchesSiteFilters(row, {
          workshop: f.workshop,
          system: f.system,
          pond: f.pond
        }, this.siteOptions)) {
          return false
        }
        if (f.batch && !rowMatchesBatchFilter(item, f.batch)) return false
        if (f.species && !this.rowMatchesSpecies(item, f.species)) return false
        return true
      })
    },
    pondMetric(row) {
      const grams = this.parseSpecGrams(row && row.fishSpec)
      const species = this.resolveSpeciesLabel(row && (row.fishName || row.fishTypes)) || '未标注'
      const count = Number(row && (row.fishNum != null ? row.fishNum : row.quantity)) || 0
      const value = Number(row && (row.fish != null ? row.fish : row.value)) || 0
      let weightKg = Number(row && row.capacity) || 0
      if (!weightKg && grams != null && count > 0) {
        weightKg = (count * grams) / 1000
      }
      return { grams, species, count, value, weightKg, actualSpec: this.formatActualSpec(grams) }
    },
    /** 按规格区间 + 品种堆叠 */
    buildSpecSeries(ponds, metricKey) {
      const speciesSet = new Set()
      const bucketMaps = SPEC_BUCKETS.map(() => ({}))
      const actualByBucketSpecies = SPEC_BUCKETS.map(() => ({}))

      ;(ponds || []).forEach((row) => {
        const m = this.pondMetric(row)
        const bucket = this.resolveSpecBucket(m.grams)
        if (!bucket) return
        const idx = SPEC_BUCKETS.findIndex((b) => b.key === bucket.key)
        if (idx < 0) return
        speciesSet.add(m.species)
        const map = bucketMaps[idx]
        const metricVal = metricKey === 'value' ? m.value : metricKey === 'qty' ? m.count : m.weightKg
        map[m.species] = (map[m.species] || 0) + metricVal
        if (!actualByBucketSpecies[idx][m.species]) actualByBucketSpecies[idx][m.species] = new Set()
        if (m.actualSpec) actualByBucketSpecies[idx][m.species].add(m.actualSpec)
      })

      const speciesList = [...speciesSet].sort((a, b) => a.localeCompare(b, 'zh-CN'))
      const categories = SPEC_BUCKETS.map((b) => b.label)
      const series = speciesList.map((sp, si) => ({
        name: sp,
        type: 'bar',
        stack: 'total',
        emphasis: { focus: 'series' },
        itemStyle: { color: this.speciesColor(sp, si) },
        data: bucketMaps.map((map, bi) => {
          const v = map[sp] || 0
          const actuals = [...(actualByBucketSpecies[bi][sp] || [])].sort((a, b) => parseFloat(a) - parseFloat(b))
          return {
            value: Math.round(v * 100) / 100,
            actualSpecs: actuals,
            bucketLabel: SPEC_BUCKETS[bi].label,
            species: sp
          }
        })
      }))
      return { categories, series, speciesList }
    },
    handleSelect(key) {
      this.currentView = key;
      if (key === 'dashboard') {
        this.$nextTick(() => {
          this.initCharts();
        });
      }
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => chart && chart.resize());
    },
    initCharts() {
      if (this.currentView !== 'dashboard') return;
      this.initPieChart();
      this.initSpecMetricChart('value');
      this.initSpecMetricChart('qty');
      this.initSpecMetricChart('weight');
    },
    initPieChart() {
      if (!this.$refs.pieChart) return;
      if (this.charts.pie) {
        this.charts.pie.dispose()
      }
      const chart = echarts.init(this.$refs.pieChart);
      const ponds = this.filterPondsByChart('pie')
      const map = {}
      ponds.forEach((row) => {
        const m = this.pondMetric(row)
        if (!m.species || m.species === '未标注') {
          if (!(m.value > 0 || m.count > 0)) return
        }
        if (!map[m.species]) map[m.species] = { name: m.species, value: 0, quantity: 0 }
        map[m.species].value += m.value
        map[m.species].quantity += m.count
      })
      const filteredData = Object.keys(map)
        .map((k) => map[k])
        .filter((i) => i.value > 0 || i.quantity > 0)
        .sort((a, b) => b.value - a.value)
      const pieData = filteredData.length
        ? filteredData.map((item, index) => ({
            value: Number(item.value) > 0 ? Number(item.value) : Number(item.quantity) || 0,
            name: item.name,
            quantity: item.quantity,
            itemStyle: { color: this.speciesColor(item.name, index) }
          }))
        : [{ value: 1, name: '暂无品种分布', itemStyle: { color: '#475569' } }];

      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: (p) => {
            if (!filteredData.length) return p.name
            const wan = (Number(p.value) / 10000).toFixed(2)
            const qty = (p.data && p.data.quantity) != null ? p.data.quantity : ''
            return `${p.name}<br/>价值：¥${wan}万（${p.percent}%）` + (qty !== '' ? `<br/>数量：${qty}尾` : '')
          },
          backgroundColor: '#1e293b',
          borderColor: '#334155',
          textStyle: { color: '#e2e8f0' }
        },
        legend: {
          bottom: 0,
          left: 'center',
          textStyle: { color: '#94a3b8' }
        },
        series: [
          {
            type: 'pie',
            radius: ['32%', '58%'],
            center: ['50%', '46%'],
            data: pieData,
            label: { color: '#e2e8f0', formatter: '{b}\n{d}%' },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      });
      this.charts.pie = chart;
    },
    /**
     * metric: value | qty | weight
     * 柱状图悬停展示实际规格（非仅区间）
     */
    initSpecMetricChart(metric) {
      const refMap = { value: 'valueSpecChart', qty: 'qtySpecChart', weight: 'weightSpecChart' }
      const chartKey = metric
      const refName = refMap[metric]
      if (!refName || !this.$refs[refName]) return
      if (this.charts[metric]) this.charts[metric].dispose()

      const ponds = this.filterPondsByChart(chartKey)
      const { categories, series } = this.buildSpecSeries(ponds, metric)
      const chart = echarts.init(this.$refs[refName])

      const yName = metric === 'value' ? '价值(万元)' : metric === 'qty' ? '数量(尾)' : '重量(吨)'
      const scale = metric === 'value' ? 10000 : metric === 'weight' ? 1000 : 1
      const scaledSeries = series.map((s) => ({
        ...s,
        data: (s.data || []).map((d) => ({
          ...d,
          value: Math.round((Number(d.value) / scale) * 100) / 100
        }))
      }))

      chart.setOption({
        tooltip: {
          trigger: 'item',
          backgroundColor: '#1e293b',
          borderColor: '#334155',
          textStyle: { color: '#e2e8f0' },
          formatter: (p) => {
            const d = p.data || {}
            const actual = (d.actualSpecs && d.actualSpecs.length)
              ? d.actualSpecs.join('、')
              : '无'
            const unit = metric === 'value' ? '万元' : metric === 'qty' ? '尾' : '吨'
            return [
              `规格区间：${d.bucketLabel || p.name}`,
              `品种：${d.species || p.seriesName}`,
              `${yName.replace(/\(.*\)/, '')}：${p.value}${unit}`,
              `实际规格：${actual}`
            ].join('<br/>')
          }
        },
        legend: {
          bottom: 0,
          textStyle: { color: '#94a3b8' }
        },
        grid: { left: '3%', right: '4%', bottom: '14%', top: '12%', containLabel: true },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: { color: '#94a3b8', interval: 0, rotate: categories.length > 4 ? 20 : 0 },
          axisLine: { lineStyle: { color: '#334155' } }
        },
        yAxis: {
          type: 'value',
          name: yName,
          nameTextStyle: { color: '#94a3b8' },
          axisLabel: { color: '#94a3b8' },
          splitLine: { lineStyle: { color: '#334155' } }
        },
        series: scaledSeries.length
          ? scaledSeries
          : [{
              name: '暂无数据',
              type: 'bar',
              data: categories.map(() => 0),
              itemStyle: { color: '#475569' }
            }]
      })
      this.charts[metric] = chart
    },
    inventoryPonds() {
      return this.pondProductivity || []
    },
    blankPlaceholder(value) {
      const s = value == null ? '' : String(value).trim()
      if (!s || s === '未知' || s === '-' || s.toLowerCase() === 'null') return ''
      return s
    },
    pondSpeciesLabel(row) {
      return this.resolveSpeciesLabel(row && (row.fishName || row.fishTypes)) || ''
    },
    exportAssetExcel() {
      const ponds = this.inventoryPonds()
      if (!ponds.length) {
        this.$message && this.$message.warning('当前没有可导出的资产数据')
        return
      }
      this.exporting = true
      try {
        const xml = this.buildAssetWorkbookXml(ponds)
        const blob = new Blob([xml], { type: 'application/vnd.ms-excel;charset=utf-8' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        const now = new Date()
        const pad = (n) => String(n).padStart(2, '0')
        a.href = url
        a.download = `生物资产评估_${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}.xls`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
        this.$message && this.$message.success('已导出资产统计')
      } catch (e) {
        console.warn('[bioAsset] export excel', e)
        this.$message && this.$message.error('导出失败，请稍后重试')
      } finally {
        this.exporting = false
      }
    },
    buildAssetWorkbookXml(ponds) {
      const now = new Date()
      const pad = (n) => String(n).padStart(2, '0')
      const exportedAt = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`
      const totals = this.summaryTotals || {}
      const totalValue = Number(totals.value) || 0
      const totalCount = Number(totals.count) || 0
      const bySpecies = {}
      ponds.forEach((row) => {
        const name = this.pondSpeciesLabel(row) || '未标注'
        if (!bySpecies[name]) bySpecies[name] = { name, quantity: 0, weight: 0, value: 0 }
        const m = this.pondMetric(row)
        bySpecies[name].quantity += m.count
        bySpecies[name].weight += m.weightKg
        bySpecies[name].value += m.value
      })
      const speciesRows = Object.keys(bySpecies)
        .map((k) => bySpecies[k])
        .sort((a, b) => b.value - a.value || a.name.localeCompare(b.name, 'zh-CN'))
      const summary = [
        ['生物资产评估盘点', ''],
        ['导出时间', exportedAt],
        ['统计周期', this.periodLabel],
        ['存档日', this.snapshotDate || this.asOfDate || ''],
        ['总资产价值(元)', this.roundMoney(totalValue)],
        ['存塘数量(尾)', Math.round(totalCount)],
        ['总重量(kg)', this.roundMoney(totals.weightKg)],
        ['鱼池数', ponds.length]
      ]
      const speciesSheet = [['品种', '数量(尾)', '重量(kg)', '资产价值(元)', '价值占比(%)']].concat(
        speciesRows.map((item) => [
          item.name,
          Math.round(item.quantity),
          this.roundMoney(item.weight),
          this.roundMoney(item.value),
          totalValue > 0 ? this.roundMoney((item.value / totalValue) * 100) : 0
        ])
      )
      const pondSheet = [['车间', '系统', '池号', '品种', '批次', '规格', '存塘数量(尾)', '重量(kg)', '资产价值(元)']].concat(
        ponds.map((row) => {
          const m = this.pondMetric(row)
          return [
            this.blankPlaceholder(row.workshopName || row.workshop),
            this.blankPlaceholder(row.pondSystemName || row.system),
            this.blankPlaceholder(row.pondDetailNum || row.pondName || row.name),
            m.species,
            this.blankPlaceholder(row.batchNumber),
            this.blankPlaceholder(row.fishSpec),
            Math.round(m.count),
            this.roundMoney(m.weightKg),
            this.roundMoney(m.value)
          ]
        })
      )
      const bucketSheet = [['规格区间', '品种', '数量(尾)', '重量(kg)', '资产价值(元)', '实际规格']].concat(
        (() => {
          const rows = []
          const { series } = this.buildSpecSeries(ponds, 'value')
          const qtyBuilt = this.buildSpecSeries(ponds, 'qty')
          const wtBuilt = this.buildSpecSeries(ponds, 'weight')
          SPEC_BUCKETS.forEach((b, bi) => {
            const speciesNames = [...new Set([
              ...series.map((s) => s.name),
              ...qtyBuilt.series.map((s) => s.name),
              ...wtBuilt.series.map((s) => s.name)
            ])]
            speciesNames.forEach((sp) => {
              const valSeries = series.find((s) => s.name === sp)
              const qtySeries = qtyBuilt.series.find((s) => s.name === sp)
              const wtSeries = wtBuilt.series.find((s) => s.name === sp)
              const valCell = valSeries && valSeries.data[bi]
              const qtyCell = qtySeries && qtySeries.data[bi]
              const wtCell = wtSeries && wtSeries.data[bi]
              const qty = qtyCell ? Number(qtyCell.value) || 0 : 0
              const wt = wtCell ? Number(wtCell.value) || 0 : 0
              const val = valCell ? Number(valCell.value) || 0 : 0
              if (!qty && !wt && !val) return
              rows.push([
                b.label,
                sp,
                Math.round(qty),
                this.roundMoney(wt),
                this.roundMoney(val),
                ((valCell && valCell.actualSpecs) || []).join('、')
              ])
            })
          })
          return rows
        })()
      )
      return this.toExcelXml([
        ['资产汇总', summary],
        ['品种资产分布', speciesSheet],
        ['各鱼池资产价值', pondSheet],
        ['规格区间分布', bucketSheet]
      ])
    },
    roundMoney(value) {
      const n = Number(value)
      if (!Number.isFinite(n)) return 0
      return Math.round(n * 100) / 100
    },
    excelEscape(value) {
      return String(value == null ? '' : value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
    },
    toExcelXml(sheets) {
      const worksheetXml = (sheets || []).map(([name, rows]) => {
        const body = (rows || []).map((row) => {
          const cells = (row || []).map((value) => {
            const numeric = typeof value === 'number' && Number.isFinite(value)
            const type = numeric ? 'Number' : 'String'
            const text = numeric ? String(value) : this.excelEscape(value)
            return `<Cell><Data ss:Type="${type}">${text}</Data></Cell>`
          }).join('')
          return `<Row>${cells}</Row>`
        }).join('')
        return `<Worksheet ss:Name="${this.excelEscape(name)}"><Table>${body}</Table></Worksheet>`
      }).join('')
      return `<?xml version="1.0" encoding="UTF-8"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet" xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
${worksheetXml}
</Workbook>`
    },
    // 鱼类价格列表相关方法
    openFishPriceDialog() {
      this.fishPriceDialogVisible = true;
      this.getFishPriceList();
    },
    getFishPriceList() {
      this.fishPriceLoading = true;
      listData({
        pageNo: 1,
        pageSize: 100,
        dictType: 'zk_pond_fish_types'
      }).then(response => {
        this.fishPriceList = response.data.list;
        this.fishPriceLoading = false;
      });
    },
    resetFishPriceForm() {
      this.fishPriceForm = {
        id: undefined,
        dictType: 'zk_pond_fish_types',
        label: undefined,
        value: undefined,
        sort: 0,
        status: 0, // 默认启用
        colorType: 'default',
        cssClass: '',
        remark: undefined
      };
      if (this.$refs["fishPriceForm"]) {
        this.$refs["fishPriceForm"].resetFields();
      }
    },
    handleAddFishPrice() {
      this.resetFishPriceForm();
      this.fishPriceFormTitle = "添加鱼类价格";
      this.fishPriceFormVisible = true;
    },
    handleEditFishPrice(row) {
      this.resetFishPriceForm();
      const id = row.id;
      getData(id).then(response => {
        this.fishPriceForm = response.data;
        this.fishPriceFormTitle = "修改鱼类价格";
        this.fishPriceFormVisible = true;
      });
    },
    submitFishPriceForm() {
      this.$refs["fishPriceForm"].validate(valid => {
        if (valid) {
          if (this.fishPriceForm.id !== undefined) {
            updateData(this.fishPriceForm).then(response => {
              this.$message.success("修改成功");
              this.fishPriceFormVisible = false;
              this.getFishPriceList();
            });
          } else {
            addData(this.fishPriceForm).then(response => {
              this.$message.success("新增成功");
              this.fishPriceFormVisible = false;
              this.getFishPriceList();
            });
          }
        }
      });
    },
    cancelFishPriceForm() {
      this.fishPriceFormVisible = false;
      this.resetFishPriceForm();
    }
  }
};
</script>

<style scoped>
.app-container {
  position: relative;
  min-height: 100vh;
  background-color: #0b1120; /* 深色背景 */
  color: #e2e8f0;
}

.header {
  background-color: #1e293b; /* 深色头部 */
  color: #f8fafc;
  padding: 1rem 0;
  border-bottom: 1px solid #334155;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.title {
  font-size: 1.5rem;
  margin: 0;
  color: #f8fafc;
}

.subtitle {
  color: #94a3b8;
  font-size: 0.875rem;
  margin: 0.25rem 0 0 0;
}

.nav {
  background-color: #1e293b;
  border-bottom: 1px solid #334155;
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

/* 覆盖 Element UI 菜单样式 */
::v-deep .el-menu {
  background-color: transparent;
  border-bottom: none;
}

/* 覆盖 Element UI Select 样式以适应深色背景 */
::v-deep .el-input__inner {
  background-color: #334155;
  border-color: #475569;
  color: #f8fafc;
}

::v-deep .el-select .el-input .el-select__caret {
  color: #94a3b8;
}

::v-deep .el-select-dropdown {
  background-color: #1e293b;
  border: 1px solid #334155;
}

::v-deep .el-select-dropdown__item {
  color: #94a3b8;
}

::v-deep .el-select-dropdown__item.hover, 
::v-deep .el-select-dropdown__item:hover {
  background-color: #334155;
  color: #f8fafc;
}

::v-deep .el-select-dropdown__item.selected {
  color: #38bdf8;
  font-weight: bold;
}

::v-deep .el-menu-item {
  color: #94a3b8;
}

::v-deep .el-menu-item:hover,
::v-deep .el-menu-item:focus {
  background-color: #334155 !important;
  color: #f8fafc !important;
}

::v-deep .el-menu-item.is-active {
  color: #38bdf8 !important;
  border-bottom-color: #38bdf8 !important;
}

.main-content {
  /** 
  max-width: 1200px;
  */
  margin: 1.5rem auto;
  padding: 0 1rem;
}

.summary-cards {
  margin-bottom: 1.5rem;
}

.summary-card {
  height: 120px;
  border: none;
}

/* 覆盖 Element UI 卡片样式 */
::v-deep .el-card {
  background-color: #1e293b;
  border: 1px solid #334155;
  color: #e2e8f0;
}

::v-deep .el-card__header {
  border-bottom: 1px solid #334155;
  color: #f8fafc;
  font-weight: 500;
}

.card-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cursor-pointer {
  cursor: pointer;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  color: #94a3b8;
  font-size: 0.875rem;
}

.card-unit {
  font-size: 0.95rem;
  color: #94a3b8;
  font-weight: 500;
}

.chart-panel ::v-deep .el-card__body {
  padding-top: 12px;
}

.chart-filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.chart-box {
  height: 320px;
}

.card-value {
  font-size: 1.875rem;
  margin-top: 0.5rem;
  color: #f8fafc;
}

.card-sub-value {
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

/* 调整图标背景为半透明，适应深色模式 */
.icon-blue { color: #38bdf8; font-size: 2rem; background: rgba(56, 189, 248, 0.15); padding: 0.5rem; border-radius: 50%; }
.icon-green { color: #4ade80; font-size: 2rem; background: rgba(74, 222, 128, 0.15); padding: 0.5rem; border-radius: 50%; }
.icon-amber { color: #fbbf24; font-size: 2rem; background: rgba(251, 191, 36, 0.15); padding: 0.5rem; border-radius: 50%; }
.icon-purple { color: #c084fc; font-size: 2rem; background: rgba(192, 132, 252, 0.15); padding: 0.5rem; border-radius: 50%; }

.text-green { color: #4ade80; }
.text-amber { color: #fbbf24; }
.text-purple { color: #c084fc; }
.text-blue { color: #38bdf8; }
.text-teal { color: #2dd4bf; }
.text-orange { color: #fb923c; }

.asset-filter-bar {
  margin-bottom: 16px;
  padding: 12px 16px 0;
  background: #1e293b;
  border: 1px solid #334155;
  border-radius: 0.5rem;
}
.asset-filter-bar ::v-deep .el-form-item__label {
  color: #94a3b8;
}

.kpi-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.kpi-list-row {
  flex-direction: row;
  flex-wrap: wrap;
  gap: 1.5rem;
}
.kpi-list-row .kpi-item {
  flex: 1;
  min-width: 160px;
  border-bottom: none;
  padding-bottom: 0;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.5rem;
}

.kpi-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #334155;
}

.kpi-item:last-child {
  border-bottom: none;
}

.kpi-label {
  color: #94a3b8;
}

.kpi-value {
  font-size: 1.25rem;
  font-weight: 500;
}

.other-view {
  background: #1e293b;
  padding: 2rem;
  border-radius: 0.5rem;
  border: 1px solid #334155;
}

/* 覆盖 Element UI 空状态样式 */
::v-deep .el-empty__description p {
  color: #94a3b8;
}
</style>
