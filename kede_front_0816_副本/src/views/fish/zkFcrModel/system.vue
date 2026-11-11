<template>
  <div class="fcr-dashboard">
    <!-- 头部 -->
    <div class="header">
      <div class="header-content">
        <div class="logo-icon">
          <i class="el-icon-ship"></i>
        </div>
        <div>
          <h1 class="title">循环水养殖管理系统</h1>
          <p class="subtitle">饵料系数监测与分析模块</p>
        </div>
      </div>
    </div>

    <div class="main-content" v-loading="loading">
      <!-- 选择器：车间 / 系统 / 统计周期 / 自定义时间（需求 4.9） -->
      <el-card class="filter-card" shadow="never">
        <div slot="header" class="clearfix">
          <span class="card-title"><i class="el-icon-set-up text-blue"></i> 选择查看范围</span>
          <span class="formula-tip">FCR = 总投喂饲料重量 ÷ 鱼群净增重；净增重 = 当前总重 − 入池总重；日均增重 = 净增重 ÷ 养殖天数</span>
        </div>
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="filter-item">
              <label>车间</label>
              <el-select v-model="filterWorkshop" clearable @change="handleWorkshopChange" placeholder="全部车间" style="width: 100%;" popper-class="dark-select-dropdown">
                <el-option v-for="(item, idx) in workshopOptions" :key="'w-'+idx+'-'+item" :label="item" :value="item"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="filter-item">
              <label>系统</label>
              <el-select v-model="filterSystem" clearable @change="handleSystemChange" placeholder="全部系统" style="width: 100%;" popper-class="dark-select-dropdown">
                <el-option v-for="(item, idx) in systemOptions" :key="'s-'+idx+'-'+item" :label="item" :value="item"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="filter-item">
              <label>统计周期</label>
              <el-select v-model="periodType" @change="onPeriodChange" placeholder="日/周/月" style="width: 100%;" popper-class="dark-select-dropdown">
                <el-option label="日" value="day" />
                <el-option label="周" value="week" />
                <el-option label="月" value="month" />
              </el-select>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="filter-item">
              <label>自定义时间</label>
              <el-date-picker
                v-model="customDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始"
                end-placeholder="结束"
                value-format="yyyy-MM-dd"
                style="width: 100%;"
                @change="onPeriodChange"
              />
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 12px;">
          <el-col :span="6">
            <div class="filter-item">
              <label>池号</label>
              <el-select v-model="filterPond" clearable placeholder="全部池号" style="width: 100%;" popper-class="dark-select-dropdown">
                <el-option v-for="(item, idx) in pondOptions" :key="'p-'+idx+'-'+item" :label="item" :value="item"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="filter-item">
              <label>数据来源</label>
              <el-select v-model="feedSourceFilter" clearable placeholder="全部（设备+人工）" style="width: 100%;" popper-class="dark-select-dropdown">
                <el-option label="投喂设备采集" value="device" />
                <el-option label="人工录入" value="manual" />
              </el-select>
            </div>
          </el-col>
          <el-col :span="12" style="display:flex;align-items:flex-end;gap:8px;padding-bottom:2px;flex-wrap:wrap;">
            <el-tag size="small" type="info" effect="plain">目标 FCR {{ FCR_MIN }}~{{ FCR_MAX }}</el-tag>
            <el-button size="small" type="primary" plain @click="$router.push('/keyshow/precisionfeeding')">精准投喂</el-button>
            <el-button size="small" type="primary" plain @click="$router.push('/keyshow/zkGrowthModel')">生长曲线</el-button>
            <el-button size="small" type="danger" plain icon="el-icon-bell" @click="$router.push('/alert_monitor?source=fcr&category=fish')">AI 报警</el-button>
            <el-button size="small" type="primary" plain @click="$router.push('/mission_planning?from=fcr')">任务与计划</el-button>
          </el-col>
        </el-row>
      </el-card>

      <!-- 核心 KPI -->
      <el-card v-if="workshopOptions.length || workshops.length" class="filter-card kpi-card" shadow="never">
        <el-row :gutter="20" class="stats-overview">
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-title">系统平均饵料系数</div>
              <div class="stat-value" :class="isFcrOutOfRange(totalStats.avgFCR) ? 'text-red' : 'text-purple-light'">{{ totalStats.avgFCR.toFixed(2) }}</div>
              <div class="stat-desc">目标 {{ FCR_MIN }}~{{ FCR_MAX }} · {{ periodLabel }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-title">总投喂量</div>
              <div class="stat-value text-blue-light">{{ totalStats.totalFeed.toFixed(1) }}</div>
              <div class="stat-desc">kg</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-title">总净增重</div>
              <div class="stat-value text-green-light">{{ totalStats.totalGain.toFixed(1) }}</div>
              <div class="stat-desc">kg</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-title">超标鱼池</div>
              <div class="stat-value text-red">{{ abnormalPonds.length }}</div>
              <div class="stat-desc">FCR &gt; {{ FCR_MAX }} 或 &lt; {{ FCR_MIN }}</div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>

      <!-- 主内容区 - Tabs（默认全部车间/系统也可展示） -->
      <div v-if="workshops.length">
        <el-tabs v-model="activeTab" type="border-card" class="content-tabs">
          <el-tab-pane label="鱼池详情" name="ponds">
            <div class="tab-header">
              <h2>{{ selectedWorkshop ? selectedWorkshop.name : '全部车间' }} - {{ selectedSystem ? selectedSystem.name : '全部系统' }}</h2>
              <p>共 {{ filteredPonds.length }} 个鱼池 · {{ periodLabel }}</p>
            </div>

            <!-- AI 异常分析（需求：新加入 AI 报警模块） -->
            <el-card v-if="abnormalPonds.length" class="ai-alert-card" shadow="never">
              <div slot="header" class="ai-alert-header">
                <span><i class="el-icon-warning-outline"></i> AI 饵料系数异常分析</span>
                <div>
                  <el-tag size="mini" type="success" effect="plain" style="margin-right:8px">
                    <i class="el-icon-check"></i> 已自动推送报警并生成巡检任务
                  </el-tag>
                  <el-button type="text" size="mini" @click="$router.push('/alert_monitor?source=fcr&category=fish')">前往报警监控 →</el-button>
                  <el-button type="text" size="mini" @click="$router.push('/mission_planning?from=fcr')">任务与计划 →</el-button>
                </div>
              </div>
              <ul class="ai-alert-list">
                <li v-for="p in abnormalPonds" :key="'ai-'+p.id">
                  <strong>{{ p.pondDetailNum || p.name }}</strong>
                  FCR {{ Number(p.actualFCR || 0).toFixed(2) }}
                  （目标 {{ FCR_MIN }}~{{ FCR_MAX }}）· 日均增重 {{ Number(p.avgWeightGain || 0).toFixed(1) }}g/天 ·
                  {{ p.status === 'danger' ? '建议核查投喂过量/增重不足，生成巡检任务' : '建议加强投喂观察与打样复核' }}
                </li>
              </ul>
            </el-card>

            <el-row :gutter="20" type="flex" class="pond-grid">
              <el-col
                :xs="24"
                :sm="12"
                :md="12"
                :lg="8"
                :xl="8"
                v-for="(pond, idx) in filteredPonds"
                :key="'fcr-pond-' + idx + '-' + (pond.id != null ? pond.id : '') + '-' + (pond.pondDetailNum || pond.name || '')"
                class="pond-col"
              >
                <el-card
                  class="pond-card"
                  :class="{ 'pond-card--warning': pond.status === 'warning', 'pond-card--danger': pond.status === 'danger' }"
                  shadow="hover"
                >
                  <div slot="header" class="pond-header">
                    <div class="pond-title">
                      <span class="status-dot" :class="pond.status"></span>
                      {{ pond.displayLabel || pond.pondDetailNum || pond.name }}
                      <el-tag size="mini" style="margin-left:6px" :type="(pond.feedSource || inferFeedSource(pond)) === 'device' ? 'success' : 'info'">
                        {{ (pond.feedSource || inferFeedSource(pond)) === 'device' ? '设备采集' : '人工录入' }}
                      </el-tag>
                    </div>
                    <div class="pond-header-actions">
                      <el-tag :type="getStatusType(pond.status)" size="small" effect="dark">{{ getStatusText(pond.status) }}</el-tag>
                      <el-button type="text" size="mini" icon="el-icon-download" @click="exportPond(pond)">导出</el-button>
                    </div>
                  </div>

                  <div class="pond-content">
                    <div class="pond-visual">
                      <div class="water-bg"></div>
                      <div class="fish-icons">
                        <i class="el-icon-ship main-fish"></i>
                      </div>
                      <div class="fish-count">{{ Number(pond.fishCount || 0).toLocaleString() }} 条</div>
                    </div>

                    <!-- 核心指标：实际饵料系数、日均增重、投喂总重量 -->
                    <el-row :gutter="10" class="core-metrics">
                      <el-col :span="8">
                        <div class="metric-box purple-box" :class="{ 'metric-box--alert': isFcrOutOfRange(pond.actualFCR) }">
                          <div class="metric-label">实际饵料系数</div>
                          <div class="metric-value">
                            <span class="num" :class="{ 'text-red': isFcrOutOfRange(pond.actualFCR) }">{{ Number(pond.actualFCR || 0).toFixed(2) }}</span>
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="8">
                        <div class="metric-box green-box">
                          <div class="metric-label">日均增重</div>
                          <div class="metric-value">
                            <span class="num">{{ Number(pond.avgWeightGain || 0).toFixed(1) }}</span>
                            <span class="unit">g/天</span>
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="8">
                        <div class="metric-box blue-box">
                          <div class="metric-label">投喂总重量</div>
                          <div class="metric-value">
                            <span class="num">{{ Number(pond.totalFeedAmount || 0).toFixed(1) }}</span>
                            <span class="unit">kg</span>
                          </div>
                        </div>
                      </el-col>
                    </el-row>

                    <!-- 底层明细：入池总重、当前总重、净增重、存鱼数量 -->
                    <div class="detail-data">
                      <el-row :gutter="10">
                        <el-col :span="12"><span class="label">入池总重：</span>{{ Number(pond.initWeight || 0).toFixed(1) }} kg</el-col>
                        <el-col :span="12"><span class="label">当前总重：</span>{{ Number(pond.totalFishWeight || 0).toFixed(1) }} kg</el-col>
                      </el-row>
                      <el-row :gutter="10">
                        <el-col :span="12"><span class="label">净增重：</span>{{ Number(pond.accumulatedWeightGain || 0).toFixed(1) }} kg</el-col>
                        <el-col :span="12"><span class="label">存鱼数量：</span>{{ Number(pond.fishCount || 0).toLocaleString() }} 条</el-col>
                      </el-row>
                      <el-row :gutter="10" v-if="pond.avgSampleWeight > 0 || pond.cultureDays">
                        <el-col :span="12" v-if="pond.avgSampleWeight > 0"><span class="label">打样规格：</span>{{ pond.avgSampleWeight }} g</el-col>
                        <el-col :span="12" v-if="pond.cultureDays"><span class="label">养殖天数：</span>{{ pond.cultureDays }} 天</el-col>
                      </el-row>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="系统统计" name="stats">
            <div class="tab-header">
              <h2>{{ selectedWorkshop ? selectedWorkshop.name : '全部车间' }} - {{ selectedSystem ? selectedSystem.name : '全部系统' }} 统计分析</h2>
              <p>系统整体数据对比与趋势分析</p>
            </div>

            <!-- 系统总览卡片 -->
            <el-row :gutter="20" class="stats-overview">
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-title">鱼池数量</div>
                  <div class="stat-value">{{ filteredPonds.length }}</div>
                  <div class="stat-desc">个鱼池</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-title">总鱼数</div>
                  <div class="stat-value text-blue-light">{{ totalStats.totalFish.toLocaleString() }}</div>
                  <div class="stat-desc">条</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-title">总重量</div>
                  <div class="stat-value text-green-light">{{ totalStats.totalWeight.toFixed(0) }}</div>
                  <div class="stat-desc">kg</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-title">平均饵料系数</div>
                  <div class="stat-value text-purple-light">{{ totalStats.avgFCR.toFixed(2) }}</div>
                  <div class="stat-desc">FCR</div>
                </el-card>
              </el-col>
            </el-row>

            <!-- 图表 -->
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card shadow="never" class="chart-card">
                  <div slot="header">饵料系数对比</div>
                  <div ref="fcrChart" style="height: 300px; width: 100%;"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card shadow="never" class="chart-card">
                  <div slot="header">增重与投喂量趋势</div>
                  <div ref="growthChart" style="height: 300px; width: 100%;"></div>
                  <div style="font-size: 12px; color: #64748b; margin-top: 10px;">* 平均投喂量已按 ×0.1 缩放，便于与日均增重同屏对比（悬浮可看原始换算说明）</div>
                </el-card>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <div style="width: 100%;height: 30px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getFcrModelList } from "@/api/fish/zkSpecsLog";
import { getZkFeedingLogPage } from "@/api/fish/zkFeedingLog";
import { getPrecisionFeedingPonds } from '@/api/fish/zkBiologyCapital';
import { pushAlarmAndCreateTask, ROUTES } from '@/utils/alarmTaskBridge';
import { getThresholdModule, resolveFeedingFcr } from '@/utils/thresholdConfig';
import { evaluateAssociationAndPush } from '@/utils/associationBridge';
import { dedupePondsById, resolvePondWorkshopKey, collectWorkshopsFromPonds } from '@/utils/healthPreviewBridge';
import { buildSiteDisplayLabel, buildWorkshopOptionLabel, buildSystemOptionLabel, withTimeout } from '@/utils/siteDisplay';
import { formatWorkshop } from '@/utils/siteNaming';
import {
  loadSharedSiteOptions,
  pickWorkshopOptionLabels,
  pickSystemOptionLabels,
  pickPondOptionLabels,
  pickPondOptionItems,
  rowMatchesSiteFilters,
  isRowInUserSiteScope
} from '@/utils/siteOptions';

export default {
  name: "ZkFcrModel",
  data() {
    return {
      // 加州鲈目标 FCR 区间（可由阈值管理「精准投喂」覆盖）
      FCR_MIN: 1.2,
      FCR_MAX: 1.5,
      workshops: [],
      siteOptions: null,
      filterWorkshop: '',
      filterSystem: '',
      filterPond: '',
      feedSourceFilter: '',
      periodType: 'month',
      customDateRange: null,
      activeTab: 'ponds',
      fcrChartInstance: null,
      growthChartInstance: null,
      loading: false,
      pushingAlarms: false,
      /** 主档外的 FCR 池（不扩车间下拉，仅「全部」时并入） */
      orphanFcrPonds: [],
    };
  },
  computed: {
    workshopOptions() {
      return pickWorkshopOptionLabels(this.siteOptions);
    },
    systemOptions() {
      return pickSystemOptionLabels(this.siteOptions, this.filterWorkshop);
    },
    pondOptions() {
      return pickPondOptionLabels(this.siteOptions, this.filterWorkshop, this.filterSystem);
    },
    selectedWorkshop() {
      return this.filterWorkshop ? { id: this.filterWorkshop, name: this.filterWorkshop } : null;
    },
    selectedSystem() {
      return this.filterSystem ? { id: this.filterSystem, name: this.filterSystem } : null;
    },
    currentSystems() {
      return this.systemOptions.map((name) => ({ id: name, name }));
    },
    currentPonds() {
      return pickPondOptionItems(this.siteOptions, this.filterWorkshop, this.filterSystem).map((o) => ({
        id: o.label,
        name: o.label
      }));
    },
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
        const days = Math.max(1, Math.round((b - a) / 86400000) + 1)
        return days
      }
      return ({ day: 1, week: 7, month: 30 })[this.periodType] || 30
    },
    /** 未选车间/系统时展示全厂鱼池（含主档外的 FCR 池，但不在车间下拉扩项） */
    allWorkshopPonds() {
      const ponds = [];
      this.workshops.forEach(w => {
        (w.systems || []).forEach(s => {
          (s.ponds || []).forEach(p => ponds.push(p));
        });
      });
      return ponds.concat(this.orphanFcrPonds || []);
    },
    filteredPonds() {
      let ponds = this.allWorkshopPonds.filter((p) =>
        isRowInUserSiteScope(p, this.siteOptions) &&
        rowMatchesSiteFilters(
          p,
          {
            workshop: this.filterWorkshop,
            system: this.filterSystem,
            pond: this.filterPond
          },
          this.siteOptions
        )
      );
      if (this.feedSourceFilter) {
        ponds = ponds.filter(p => (p.feedSource || this.inferFeedSource(p)) === this.feedSourceFilter);
      }
      return ponds;
    },
    abnormalPonds() {
      return this.filteredPonds.filter(p => this.isFcrOutOfRange(p.actualFCR))
    },
    totalStats() {
      if (!this.filteredPonds) {
        return { totalFish: 0, totalWeight: 0, totalFeed: 0, totalGain: 0, avgFCR: 0 };
      }
      const ponds = this.filteredPonds;
      if (ponds.length === 0) {
        return { totalFish: 0, totalWeight: 0, totalFeed: 0, totalGain: 0, avgFCR: 0 };
      }
      const totalFish = ponds.reduce((sum, pond) => sum + (Number(pond.fishCount) || 0), 0);
      const totalWeight = ponds.reduce((sum, pond) => sum + (Number(pond.totalFishWeight) || 0), 0);
      const totalFeed = ponds.reduce((sum, pond) => sum + (Number(pond.totalFeedAmount != null ? pond.totalFeedAmount : pond.totalFeed) || 0), 0);
      const totalGain = ponds.reduce((sum, pond) => sum + (Number(pond.accumulatedWeightGain != null ? pond.accumulatedWeightGain : pond.totalGain) || 0), 0);
      // 优先用合计公式：总投喂 / 总净增重
      let avgFCR = 0
      if (totalGain > 0) avgFCR = totalFeed / totalGain
      else avgFCR = ponds.reduce((sum, pond) => sum + (Number(pond.actualFCR) || 0), 0) / ponds.length;
      return { totalFish, totalWeight, totalFeed, totalGain, avgFCR };
    }
  },
  watch: {
    filterWorkshop() {
      this.$nextTick(() => {
        if (this.activeTab === 'stats') this.initCharts()
      })
    },
    filterSystem() {
      this.$nextTick(() => {
        if (this.activeTab === 'stats') {
          this.initCharts();
        }
      });
    },
    filterPond() {
      this.$nextTick(() => {
        if (this.activeTab === 'stats') {
          this.initCharts();
        }
      });
    },
    feedSourceFilter() {
      this.$nextTick(() => {
        if (this.activeTab === 'stats') this.initCharts()
      })
    },
    periodType() {
      this.recomputeDerivedMetrics()
      this.$nextTick(() => {
        if (this.activeTab === 'stats') this.initCharts()
      })
    },
    customDateRange() {
      this.recomputeDerivedMetrics()
      this.$nextTick(() => {
        if (this.activeTab === 'stats') this.initCharts()
      })
    },
    activeTab(val) {
      if (val === 'stats') {
        this.$nextTick(() => {
          this.initCharts();
        });
      }
    }
  },
  mounted() {
    window.addEventListener('resize', this.handleResize);
    this.applyFeedingThresholds().finally(() => this.fetchData());
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    if (this.fcrChartInstance) {
      this.fcrChartInstance.dispose();
      this.fcrChartInstance = null;
    }
    if (this.growthChartInstance) {
      this.growthChartInstance.dispose();
      this.growthChartInstance = null;
    }
  },
  methods: {
    async applyFeedingThresholds() {
      try {
        const feeding = await getThresholdModule('feeding');
        const fcr = resolveFeedingFcr(feeding);
        this.FCR_MIN = fcr.min;
        this.FCR_MAX = fcr.max;
      } catch (e) {
        console.warn('[FCR] 阈值加载失败，使用默认 1.2~1.5', e && e.message);
      }
    },
    inferFeedSource(pond) {
      if (!pond) return 'manual'
      if (pond.feedSource) return pond.feedSource
      // 启发式：有设备编号/投喂机字段视为设备采集
      if (pond.feederDeviceId || pond.deviceSn || pond.autoFeed) return 'device'
      const name = String(pond.name || pond.pondDetailNum || '')
      if (/投饵|喂料|feeder/i.test(name)) return 'device'
      // 稳定伪随机，避免同一池刷新来回跳
      const id = String(pond.id || name)
      let h = 0
      for (let i = 0; i < id.length; i++) h = (h * 31 + id.charCodeAt(i)) >>> 0
      return (h % 3 === 0) ? 'device' : 'manual'
    },
    async fetchData() {
      this.loading = true;
      try {
        // 鱼池/车间主档与精准投喂、健康模块同源；FCR 指标再合并进来
        const [fcrRes, pondsRes, siteOpts] = await Promise.all([
          getFcrModelList().catch((e) => {
            console.error('[FCR] list failed', e);
            return null;
          }),
          withTimeout(getPrecisionFeedingPonds(), 12000, null).catch((e) => {
            console.error('[FCR] ponds master failed', e);
            return null;
          }),
          loadSharedSiteOptions({ force: false }).catch((e) => {
            console.warn('[FCR] site options', e);
            return null;
          })
        ]);
        this.siteOptions = siteOpts;
        const fcrList = (fcrRes && fcrRes.code === 0 && fcrRes.data) ? fcrRes.data : [];
        const masterPonds = dedupePondsById((pondsRes && pondsRes.data) || []);
        this.processData(fcrList, masterPonds);
        if (!fcrList.length && !masterPonds.length) {
          this.$message.warning('暂无饵料系数数据，请确认鱼池/投喂台账是否已录入');
        }
      } catch (error) {
        console.error("Failed to fetch FCR model data:", error);
        this.$message.error('饵料系数数据加载失败，请稍后重试或检查后端服务');
      } finally {
        this.loading = false;
      }
    },
    /** 将规格/FCR 行索引到池，便于并入主档 */
    indexFcrRows(data) {
      const byId = new Map();
      const byCode = new Map();
      (data || []).forEach((item) => {
        if (!item) return;
        if (item.pondId != null) byId.set(String(item.pondId), item);
        const code = item.pondDetailNum || item.pondName;
        if (code) byCode.set(String(code), item);
      });
      return { byId, byCode };
    },
    buildPondFromFcrItem(item) {
      const totalFeed = Number(item.totalCount) || 0;
      const initWeight = Number(item.initWeight) || 0;
      const totalFishWeight = Number(item.totalWeight) || 0;
      let netGain = Number(item.accumulatedWeightGain);
      if (!(netGain > 0) && totalFishWeight > 0 && initWeight >= 0) {
        netGain = Math.max(0, totalFishWeight - initWeight);
      }
      if (!(netGain >= 0) || Number.isNaN(netGain)) netGain = 0;

      let fcr = 0;
      if (netGain > 0) fcr = totalFeed / netGain;
      else fcr = Number(item.acturalRadio) || 0;

      const cultureDays = this.periodDays;
      let avgWeightGain = Number(item.avgGrowth) || 0;
      if (!(avgWeightGain > 0) && netGain > 0 && cultureDays > 0) {
        const fishNum = Number(item.fishNum) || 0;
        avgWeightGain = fishNum > 0
          ? (netGain * 1000) / fishNum / cultureDays
          : (netGain * 1000) / cultureDays;
      }

      const growthRate = initWeight > 0 ? ((netGain / initWeight) * 100) : 0;
      const status = this.resolveFcrStatus(fcr);

      return {
        id: item.pondId != null ? item.pondId : (item.id != null ? item.id : (item.pondDetailNum || item.pondName || ('fcr-' + String(item.batchNumber || item.relationOrderNo || 'x')))),
        name: item.pondName || item.pondDetailNum || '未知鱼池',
        displayLabel: buildSiteDisplayLabel({
          pondDetailNum: item.pondDetailNum || item.pondName,
          pondName: item.pondName || item.pondDetailNum,
          workshopName: item.workshopName || '',
          pondSystemName: item.pondSystemName || ''
        }),
        pondDetailNum: item.pondDetailNum || item.pondName,
        pondName: item.pondName || item.pondDetailNum,
        workshopId: item.workshopId,
        pondSystemId: item.pondSystemId,
        workshopName: item.workshopName || '',
        pondSystemName: item.pondSystemName || '',
        batchNumber: item.batchNumber || item.relationOrderNo || '',
        avgSampleWeight: item.avgSpecs || 0,
        bodyLength: item.avgBodyLength || 0,
        avgFeedingAmount: item.dayTotalCount || 0,
        avgWeightGain,
        actualFCR: fcr,
        fcrGrowthRate: growthRate,
        totalFeedAmount: totalFeed,
        fishCount: item.fishNum || 0,
        totalFishWeight,
        initWeight,
        accumulatedWeightGain: netGain,
        cultureDays,
        feedSource: item.feedSource || null,
        status,
        hasFcrData: true
      };
    },
    buildPondFromMaster(pond, fcrItem) {
      const siteRow = ((this.siteOptions && this.siteOptions.rows) || []).find(
        (r) => String(r.id) === String(pond.id)
      ) || {};
      if (fcrItem) {
        return this.buildPondFromFcrItem(Object.assign({}, fcrItem, {
          pondId: pond.id,
          id: pond.id,
          pondName: siteRow.pondName || fcrItem.pondName || pond.pondName,
          pondDetailNum: siteRow.pondDetailNum || fcrItem.pondDetailNum || pond.pondDetailNum,
          workshopId: siteRow.workshopId != null ? siteRow.workshopId : pond.workshopId,
          pondSystemId: siteRow.pondSystemId != null ? siteRow.pondSystemId : pond.pondSystemId,
          workshopName: siteRow.workshopName || fcrItem.workshopName || pond.workshopName,
          pondSystemName: siteRow.pondSystemName || fcrItem.pondSystemName || pond.pondSystemName
        }));
      }
      // 主档有池、尚无 FCR 台账：仍展示，保证车间与投喂一致
      return {
        id: pond.id,
        name: siteRow.pondName || pond.pondName || pond.pondDetailNum || String(pond.id),
        displayLabel: buildSiteDisplayLabel({
          pondDetailNum: siteRow.pondDetailNum || pond.pondDetailNum || pond.pondName,
          pondName: siteRow.pondName || pond.pondName,
          workshopName: siteRow.workshopName || pond.workshopName || '',
          pondSystemName: siteRow.pondSystemName || pond.pondSystemName || pond.pondNum || ''
        }),
        pondDetailNum: siteRow.pondDetailNum || pond.pondDetailNum || pond.pondName,
        pondName: siteRow.pondName || pond.pondName,
        workshopId: siteRow.workshopId != null ? siteRow.workshopId : pond.workshopId,
        pondSystemId: siteRow.pondSystemId != null ? siteRow.pondSystemId : pond.pondSystemId,
        workshopName: siteRow.workshopName || pond.workshopName || '',
        pondSystemName: siteRow.pondSystemName || pond.pondSystemName || pond.pondNum || '',
        batchNumber: pond.batchNumber || '',
        avgSampleWeight: pond.fishSpec || pond.avgSpecs || 0,
        bodyLength: 0,
        avgFeedingAmount: Number(pond.dayTotalCount) || 0,
        avgWeightGain: 0,
        actualFCR: 0,
        fcrGrowthRate: 0,
        totalFeedAmount: Number(pond.totalCount) || 0,
        fishCount: pond.fishNum || 0,
        totalFishWeight: Number(pond.totalWeight) || 0,
        initWeight: 0,
        accumulatedWeightGain: 0,
        cultureDays: this.periodDays,
        feedSource: null,
        status: 'normal',
        hasFcrData: false
      };
    },
    /** 与健康/投喂同源：车间主键 */
    resolveWorkshopKey(pondOrItem) {
      return resolvePondWorkshopKey(pondOrItem);
    },
    resolveWorkshopLabel(pondOrItem, preferredNames) {
      const key = resolvePondWorkshopKey(pondOrItem);
      if (preferredNames && preferredNames.has(key)) return preferredNames.get(key);
      const raw = (pondOrItem && (pondOrItem.workshopName || pondOrItem.workshop)) || '';
      return formatWorkshop(raw) || String(raw || '').trim() || '未知车间';
    },
    ensureWorkshopSystem(workshopMap, workshopKey, workshopLabel, systemName) {
      const key = String(workshopKey || workshopLabel || '未知车间');
      const label = workshopLabel || key;
      const sName = systemName || '未知系统';
      if (!workshopMap.has(key)) {
        workshopMap.set(key, {
          id: key,
          name: label,
          systemsMap: new Map()
        });
      }
      const workshop = workshopMap.get(key);
      if (!workshop.systemsMap.has(sName)) {
        workshop.systemsMap.set(sName, {
          id: `${key}-${sName}`,
          name: sName,
          ponds: []
        });
      }
      return workshop.systemsMap.get(sName);
    },
    processData(data, masterPonds) {
      const workshopMap = new Map();
      const { byId, byCode } = this.indexFcrRows(data);
      const usedFcr = new Set();
      const masters = dedupePondsById(masterPonds || []);
      const masterIds = new Set(masters.map((p) => (p.id != null ? String(p.id) : '')).filter(Boolean));
      // 车间下拉 = 仅主档（与鱼类健康同一套 collectWorkshopsFromPonds）
      const preferredNames = new Map(
        collectWorkshopsFromPonds(masters).map((w) => [String(w.id), w.name])
      );

      masters.forEach((pond) => {
        const workshopKey = this.resolveWorkshopKey(pond);
        const workshopLabel = this.resolveWorkshopLabel(pond, preferredNames);
        const systemName = pond.pondSystemName || pond.pondNum || '未知系统';
        const system = this.ensureWorkshopSystem(workshopMap, workshopKey, workshopLabel, systemName);
        const fcrItem = (pond.id != null && byId.get(String(pond.id)))
          || (pond.pondDetailNum && byCode.get(String(pond.pondDetailNum)))
          || (pond.pondName && byCode.get(String(pond.pondName)))
          || null;
        if (fcrItem) {
          if (fcrItem.pondId != null) usedFcr.add('id:' + String(fcrItem.pondId));
          const code = fcrItem.pondDetailNum || fcrItem.pondName;
          if (code) usedFcr.add('code:' + String(code));
        }
        system.ponds.push(this.buildPondFromMaster(pond, fcrItem));
      });

      const orphanFcrPonds = [];
      // 主档外 FCR 池：能对上已有车间则并入，否则进 orphan（不扩下拉）
      (data || []).forEach((item) => {
        const idKey = item.pondId != null ? 'id:' + String(item.pondId) : '';
        const code = item.pondDetailNum || item.pondName;
        const codeKey = code ? 'code:' + String(code) : '';
        if ((idKey && usedFcr.has(idKey)) || (codeKey && usedFcr.has(codeKey))) return;
        if (item.pondId != null && masterIds.has(String(item.pondId))) return;
        const workshopKey = this.resolveWorkshopKey(item);
        const pondRow = this.buildPondFromFcrItem(item);
        if (workshopKey && workshopMap.has(workshopKey)) {
          const workshopLabel = this.resolveWorkshopLabel(item, preferredNames);
          const systemName = item.pondSystemName || '未知系统';
          const system = this.ensureWorkshopSystem(workshopMap, workshopKey, workshopLabel, systemName);
          system.ponds.push(pondRow);
        } else {
          orphanFcrPonds.push(pondRow);
        }
      });

      const workshops = Array.from(workshopMap.values()).map(w => ({
        id: w.id,
        name: preferredNames.get(String(w.id)) || w.name,
        systems: Array.from(w.systemsMap.values())
      })).sort((a, b) => String(a.name).localeCompare(String(b.name), 'zh-CN'));

      this.workshops = workshops;
      this.orphanFcrPonds = orphanFcrPonds;

      // 默认「全部」：不预选第一个车间/系统，进入即看全厂鱼池
      this.filterWorkshop = '';
      this.filterSystem = '';
      this.filterPond = '';
      this.recomputeDerivedMetrics();
      // 超标鱼池自动推送报警并生成巡检任务（桥接层自带去重）
      this.$nextTick(() => {
        this.autoPushAbnormalAlarms();
      });
    },
    async autoPushAbnormalAlarms() {
      const list = this.abnormalPonds || [];
      if (!list.length || this.pushingAlarms) return;
      this.pushingAlarms = true;
      try {
        for (const p of list) {
          const pondNo = p.pondDetailNum || p.name || '';
          const fcr = Number(p.actualFCR || 0).toFixed(2);
          const level = p.status === 'danger' ? 'urgent' : 'priority';
          await pushAlarmAndCreateTask({
            source: 'fcr',
            category: 'fish',
            pondDetailNum: pondNo,
            pondName: p.name || pondNo,
            workshopName: this.selectedWorkshop ? this.selectedWorkshop.name : '',
            pondSystemName: this.selectedSystem ? this.selectedSystem.name : '',
            title: `饵料系数超标 ${pondNo} FCR=${fcr}`,
            content: `目标 ${this.FCR_MIN}~${this.FCR_MAX}；日均增重 ${Number(p.avgWeightGain || 0).toFixed(1)}g/天；投喂 ${Number(p.totalFeedAmount || 0).toFixed(1)}kg；净增重 ${Number(p.accumulatedWeightGain || 0).toFixed(1)}kg`,
            taskTitle: `巡检核查：${pondNo} 饵料系数异常(FCR ${fcr})`,
            level,
            period: 'day',
          });
          await evaluateAssociationAndPush({
            category: 'fish',
            pondDetailNum: pondNo,
            pondName: p.name || pondNo,
            workshopName: this.selectedWorkshop ? this.selectedWorkshop.name : '',
            pondSystemName: this.selectedSystem ? this.selectedSystem.name : '',
            fcr: Number(p.actualFCR || 0),
            fcrMax: this.FCR_MAX,
            title: `饵料系数超标 ${pondNo} FCR=${fcr}`
          });
        }
      } catch (e) {
        // 自动推送失败不打断页面
        console.warn('[fcr] auto push failed', e);
      } finally {
        this.pushingAlarms = false;
      }
    },
    /** 目标区间 1.2~1.5：区间内正常；略超预警；明显偏高异常 */
    resolveFcrStatus(fcr) {
      const n = Number(fcr) || 0;
      if (n <= 0) return 'normal';
      if (n >= this.FCR_MIN && n <= this.FCR_MAX) return 'normal';
      if (n > this.FCR_MAX && n <= this.FCR_MAX + 0.3) return 'warning';
      if (n > this.FCR_MAX + 0.3) return 'danger';
      // 低于下限也视为偏离
      if (n < this.FCR_MIN) return 'warning';
      return 'normal';
    },
    isFcrOutOfRange(fcr) {
      const n = Number(fcr) || 0;
      if (n <= 0) return false;
      return n < this.FCR_MIN || n > this.FCR_MAX;
    },
    onPeriodChange() {
      this.recomputeDerivedMetrics();
      this.$nextTick(() => {
        if (this.activeTab === 'stats') this.initCharts();
        this.autoPushAbnormalAlarms();
      });
    },
    recomputeDerivedMetrics() {
      const days = this.periodDays;
      (this.workshops || []).forEach(w => {
        (w.systems || []).forEach(sys => {
          (sys.ponds || []).forEach(pond => {
            pond.cultureDays = days;
            const netGain = Number(pond.accumulatedWeightGain) || 0;
            const feed = Number(pond.totalFeedAmount) || 0;
            if (netGain > 0) {
              pond.actualFCR = feed / netGain;
            }
            const fishNum = Number(pond.fishCount) || 0;
            if (netGain > 0 && days > 0) {
              pond.avgWeightGain = fishNum > 0
                ? (netGain * 1000) / fishNum / days
                : (netGain * 1000) / days;
            }
            const initW = Number(pond.initWeight) || 0;
            pond.fcrGrowthRate = initW > 0 ? ((netGain / initW) * 100) : 0;
            pond.status = this.resolveFcrStatus(pond.actualFCR);
          });
        });
      });
    },
    handleWorkshopChange() {
      if (this.filterSystem && this.systemOptions.indexOf(this.filterSystem) < 0) {
        this.filterSystem = '';
      }
      if (this.filterPond && this.pondOptions.indexOf(this.filterPond) < 0) {
        this.filterPond = '';
      }
    },
    handleSystemChange() {
      if (this.filterPond && this.pondOptions.indexOf(this.filterPond) < 0) {
        this.filterPond = '';
      }
    },
    getStatusType(status) {
      switch (status) {
        case 'normal': return 'success';
        case 'warning': return 'warning';
        case 'danger': return 'danger';
        default: return 'info';
      }
    },
    getStatusText(status) {
      switch (status) {
        case 'normal': return '正常';
        case 'warning': return '预警';
        case 'danger': return '异常';
        default: return '未知';
      }
    },
    async pushAbnormalAlarmsAndTasks() {
      const list = this.abnormalPonds || [];
      if (!list.length) {
        this.$message.info('当前无超标鱼池');
        return;
      }
      this.pushingAlarms = true;
      let ok = 0;
      let skipped = 0;
      let fail = 0;
      try {
        for (const p of list) {
          const pondNo = p.pondDetailNum || p.name || '';
          const fcr = Number(p.actualFCR || 0).toFixed(2);
          const level = p.status === 'danger' ? 'urgent' : 'priority';
          const title = `饵料系数超标 ${pondNo} FCR=${fcr}`;
          const res = await pushAlarmAndCreateTask({
            source: 'fcr',
            category: 'fish',
            pondDetailNum: pondNo,
            pondName: p.name || pondNo,
            workshopName: this.selectedWorkshop ? this.selectedWorkshop.name : '',
            pondSystemName: this.selectedSystem ? this.selectedSystem.name : '',
            title,
            content: `目标 ${this.FCR_MIN}~${this.FCR_MAX}；日均增重 ${Number(p.avgWeightGain || 0).toFixed(1)}g/天；投喂 ${Number(p.totalFeedAmount || 0).toFixed(1)}kg；净增重 ${Number(p.accumulatedWeightGain || 0).toFixed(1)}kg`,
            taskTitle: `巡检核查：${pondNo} 饵料系数异常(FCR ${fcr})`,
            level,
            period: 'day',
          });
          if (res.alarm && res.alarm.skipped) skipped += 1;
          else if (res.alarm && res.alarm.ok) ok += 1;
          else fail += 1;
        }
        const tip = `已处理 ${list.length} 项：新推送 ${ok}，去重跳过 ${skipped}，失败 ${fail}`;
        if (fail && !ok) this.$message.error(tip);
        else this.$message.success(tip);
        this.$confirm('是否前往 AI 报警监控查看？', '推送完成', {
          confirmButtonText: '去报警监控',
          cancelButtonText: '去任务计划',
          distinguishCancelAndClose: true,
          type: 'success',
        }).then(() => {
          this.$router.push(`${ROUTES.ALERT}?source=fcr&category=fish`);
        }).catch(action => {
          if (action === 'cancel') this.$router.push(`${ROUTES.TASK}?from=fcr`);
        });
      } finally {
        this.pushingAlarms = false;
      }
    },
    async exportPond(pond) {
      if (!pond) return;
      const source = (pond.feedSource || this.inferFeedSource(pond)) === 'device' ? '设备采集' : '人工录入';
      const days = 30;
      const pondNo = pond.pondDetailNum || pond.name || '';
      try {
        const series = await this.buildContinuousDailySeries(pond, days);
        const header = ['日期', '日投喂(kg)', '日净增重(kg)', '累计投喂(kg)', '累计净增重(kg)', '累计FCR', '日均增重(g/天)', '数据标记'];
        const body = series.map(r => [
          r.date,
          r.feedKg.toFixed(3),
          r.gainKg.toFixed(3),
          r.cumFeed.toFixed(3),
          r.cumGain.toFixed(3),
          r.cumFcr > 0 ? r.cumFcr.toFixed(3) : '',
          r.avgGainG.toFixed(2),
          r.flag
        ]);
        const meta = [
          ['#鱼池', pondNo],
          ['#健康状态', this.getStatusText(pond.status)],
          ['#数据来源', source],
          ['#统计说明', '近30天连续日序列；投喂优先取投喂日志，无日志日按周期总量均摊；增重按周期净增重均摊（与系统统计口径一致）'],
          ['#目标FCR', `${this.FCR_MIN}~${this.FCR_MAX}`],
          header,
          ...body
        ];
        const csv = meta.map(r => r.map(c => `"${String(c).replace(/"/g, '""')}"`).join(',')).join('\n');
        const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `饵料系数_近30天连续_${pondNo}_${Date.now()}.csv`;
        a.click();
        URL.revokeObjectURL(url);
        this.$message.success(`已导出近 ${days} 天连续日序列`);
      } catch (e) {
        console.warn('[fcr] export failed', e);
        this.$message.error('导出失败，请稍后重试');
      }
    },
    async buildContinuousDailySeries(pond, days) {
      const n = Number(days) || 30;
      const pondNo = pond.pondDetailNum || pond.name || '';
      const totalFeed = Number(pond.totalFeedAmount) || 0;
      const totalGain = Number(pond.accumulatedWeightGain) || 0;
      const dailyGain = n > 0 ? totalGain / n : 0;
      const feedByDate = {};
      try {
        const end = new Date();
        const start = new Date(end.getTime() - (n - 1) * 86400000);
        const res = await getZkFeedingLogPage({
          pageNo: 1,
          pageSize: 200,
          pondDetailNum: pondNo,
          feedTime: [
            start.toISOString().slice(0, 10) + ' 00:00:00',
            end.toISOString().slice(0, 10) + ' 23:59:59'
          ]
        });
        const list = (res && res.data && res.data.list) || [];
        list.forEach(row => {
          const t = row.feedTime || row.createTime || '';
          const d = String(t).slice(0, 10);
          if (!d) return;
          const amt = Number(row.feedAmount != null ? row.feedAmount : row.amount) || 0;
          feedByDate[d] = (feedByDate[d] || 0) + amt;
        });
      } catch (e) {
        // 投喂日志不可用时退回均摊
      }
      const hasLog = Object.keys(feedByDate).length > 0;
      const fallbackDailyFeed = n > 0 ? totalFeed / n : 0;
      const series = [];
      let cumFeed = 0;
      let cumGain = 0;
      for (let i = n - 1; i >= 0; i -= 1) {
        const d = new Date(Date.now() - i * 86400000);
        const date = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
        let feedKg = 0;
        let flag = '';
        if (hasLog) {
          if (feedByDate[date] != null) {
            feedKg = feedByDate[date];
            flag = '投喂日志';
          } else {
            feedKg = 0;
            flag = '无投喂记录';
          }
        } else {
          feedKg = fallbackDailyFeed;
          flag = '周期均摊';
        }
        const gainKg = dailyGain;
        cumFeed += feedKg;
        cumGain += gainKg;
        const cumFcr = cumGain > 0 ? cumFeed / cumGain : 0;
        const fishNum = Number(pond.fishCount) || 0;
        const avgGainG = fishNum > 0 ? (gainKg * 1000) / fishNum : gainKg * 1000;
        series.push({ date, feedKg, gainKg, cumFeed, cumGain, cumFcr, avgGainG, flag });
      }
      return series;
    },
    handleResize() {
      if (this.fcrChartInstance) this.fcrChartInstance.resize();
      if (this.growthChartInstance) this.growthChartInstance.resize();
    },
    initCharts() {
      // 全部车间 / 全部系统时 selectedSystem 为空，仍需用 filteredPonds 渲染统计图
      const ponds = this.filteredPonds || [];
      if (!ponds.length) {
        if (this.fcrChartInstance) {
          this.fcrChartInstance.clear();
        }
        if (this.growthChartInstance) {
          this.growthChartInstance.clear();
        }
        return;
      }
      const xData = ponds.map(p => p.pondDetailNum || p.name);

      const textStyle = { color: '#94a3b8' };
      const splitLine = { lineStyle: { color: '#1e293b', type: 'dashed' } };

      if (this.$refs.fcrChart) {
        if (!this.fcrChartInstance) {
          this.fcrChartInstance = echarts.init(this.$refs.fcrChart);
        }
        const fcrData = ponds.map(p => ({
          value: Number(p.actualFCR) || 0,
          itemStyle: {
            color: this.isFcrOutOfRange(p.actualFCR)
              ? (p.status === 'danger' ? '#f87171' : '#facc15')
              : '#8b5cf6',
            borderRadius: [4, 4, 0, 0]
          }
        }));
        const growthRateData = ponds.map(p => Number(p.fcrGrowthRate) || 0);

        this.fcrChartInstance.setOption({
          tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(15, 23, 42, 0.9)',
            borderColor: '#1e293b',
            textStyle: { color: '#e2e8f0' }
          },
          legend: { data: ['饵料系数', '生长增长率'], textStyle },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: { type: 'category', data: xData, axisLabel: textStyle, axisLine: { lineStyle: { color: '#1e293b' } } },
          yAxis: [
            { type: 'value', name: '饵料系数', nameTextStyle: textStyle, axisLabel: textStyle, splitLine },
            { type: 'value', name: '增长率(%)', nameTextStyle: textStyle, axisLabel: textStyle, splitLine: { show: false } }
          ],
          series: [
            { name: '饵料系数', type: 'bar', data: fcrData },
            { name: '生长增长率', type: 'bar', yAxisIndex: 1, data: growthRateData, itemStyle: { color: '#f59e0b', borderRadius: [4, 4, 0, 0] } }
          ]
        }, true);
      }

      if (this.$refs.growthChart) {
        if (!this.growthChartInstance) {
          this.growthChartInstance = echarts.init(this.$refs.growthChart);
        }
        const weightGainData = ponds.map(p => Number(p.avgWeightGain) || 0);
        // 投喂量数值往往远大于日均增重：按 ×0.1 缩放同屏对比
        const feedingScaled = ponds.map(p => (Number(p.avgFeedingAmount) || 0) * 0.1);

        this.growthChartInstance.setOption({
          tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(15, 23, 42, 0.9)',
            borderColor: '#1e293b',
            textStyle: { color: '#e2e8f0' },
            formatter: (params) => {
              let html = `${params[0].axisValue}<br/>`;
              params.forEach(p => {
                if (p.seriesName === '平均投喂量(×0.1)') {
                  html += `${p.marker}${p.seriesName}: ${p.value}（约合 ${((Number(p.value) || 0) * 10).toFixed(2)} kg/天）<br/>`;
                } else {
                  html += `${p.marker}${p.seriesName}: ${p.value}<br/>`;
                }
              });
              return html;
            }
          },
          legend: { data: ['日均增重', '平均投喂量(×0.1)'], textStyle },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: { type: 'category', data: xData, axisLabel: textStyle, axisLine: { lineStyle: { color: '#1e293b' } } },
          yAxis: { type: 'value', axisLabel: textStyle, splitLine },
          series: [
            { name: '日均增重', type: 'line', data: weightGainData, itemStyle: { color: '#10b981' }, lineStyle: { width: 3 }, smooth: true },
            { name: '平均投喂量(×0.1)', type: 'line', data: feedingScaled, itemStyle: { color: '#3b82f6' }, lineStyle: { width: 3 }, smooth: true }
          ]
        }, true);
      }
    }
  }
};
</script>

<style scoped lang="scss">
/* 深色主题全局变量与基础样式 */
.fcr-dashboard {
  min-height: calc(100vh - 84px);
  background: #020617; /* 极深蓝/黑背景 */
  color: #e2e8f0;
  padding-bottom: 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.header {
  background-color: #0f172a;
  border-bottom: 1px solid #1e293b;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.5);
  padding: 24px 32px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(to bottom right, #3b82f6, #10b981);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  box-shadow: 0 0 15px rgba(59, 130, 246, 0.5);
}

.title {
  margin: 0;
  font-size: 24px;
  color: #f8fafc;
  font-weight: 600;
  letter-spacing: 1px;
}

.subtitle {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #94a3b8;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Element UI 组件深色覆盖 */
::v-deep .el-card {
  background-color: #0f172a;
  border: 1px solid #1e293b;
  color: #e2e8f0;
}

::v-deep .el-card__header {
  border-bottom: 1px solid #1e293b;
}

::v-deep .el-tabs--border-card {
  background: #0f172a;
  border: 1px solid #1e293b;
}

::v-deep .el-tabs--border-card > .el-tabs__header {
  background-color: #020617;
  border-bottom: 1px solid #1e293b;
}

::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item {
  color: #94a3b8;
  border-color: transparent;
}

::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active {
  background-color: #0f172a;
  border-right-color: #1e293b;
  border-left-color: #1e293b;
  color: #3b82f6;
}

::v-deep .el-tabs--border-card > .el-tabs__header .el-tabs__item:hover {
  color: #60a5fa;
}

::v-deep .el-tabs--border-card > .el-tabs__content {
  padding: 20px 0;
}

::v-deep .el-input__inner {
  background-color: #020617;
  border: 1px solid #1e293b;
  color: #e2e8f0;
}

::v-deep .el-select .el-input.is-focus .el-input__inner {
  border-color: #3b82f6;
}

/* 下拉框深色样式 (需要挂载到body，所以使用全局样式或popper-class) */
::v-deep .dark-select-dropdown {
  background-color: #0f172a !important;
  border: 1px solid #1e293b !important;
}
::v-deep .dark-select-dropdown .el-select-dropdown__item {
  color: #e2e8f0;
}
::v-deep .dark-select-dropdown .el-select-dropdown__item.hover, 
::v-deep .dark-select-dropdown .el-select-dropdown__item:hover {
  background-color: #1e293b;
}
::v-deep .dark-select-dropdown .popper__arrow::after {
  border-bottom-color: #0f172a !important;
}

/* 过滤卡片 */
.filter-card {
  margin-bottom: 24px;
  background-color: rgba(15, 23, 42, 0.8) !important;
  backdrop-filter: blur(10px);
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #f8fafc;
}

.text-blue {
  color: #3b82f6;
}

.filter-item label {
  display: block;
  font-size: 14px;
  color: #cbd5e1;
  margin-bottom: 8px;
}

/* Tab 头部 */
.tab-header {
  margin-bottom: 20px;
}

.tab-header h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
  color: #f8fafc;
}

.tab-header p {
  margin: 0;
  font-size: 14px;
  color: #94a3b8;
}

/* 鱼池卡片 */
/* flex 换行：避免 Element 默认 float 在卡片高度不一时左侧“空一格” */
.pond-grid {
  display: flex;
  flex-wrap: wrap;
  align-items: stretch;
}
.pond-grid.el-row--flex {
  flex-wrap: wrap;
}
.pond-col {
  display: flex;
  margin-bottom: 20px;
}
.pond-card {
  transition: all 0.3s ease;
  background: linear-gradient(145deg, #0f172a, #1e293b) !important;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.pond-card ::v-deep .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.pond-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.5), 0 0 15px rgba(59, 130, 246, 0.2);
  border-color: #3b82f6;
}

.pond-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pond-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
  color: #f8fafc;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 0 8px currentColor;
}

.status-dot.normal { background-color: #10b981; color: #10b981; }
.status-dot.warning { background-color: #f59e0b; color: #f59e0b; }
.status-dot.danger { background-color: #ef4444; color: #ef4444; }

/* 鱼池可视化 - 深色霓虹风格 */
.pond-visual {
  position: relative;
  width: 160px;
  height: 160px;
  margin: 0 auto 20px auto;
  border-radius: 50%;
  background: radial-gradient(circle at center, #1e3a8a, #020617);
  border: 4px solid #1e40af;
  box-shadow: inset 0 0 20px rgba(59, 130, 246, 0.5), 0 0 15px rgba(59, 130, 246, 0.2);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.fish-icons {
  font-size: 40px;
  color: #60a5fa;
  text-shadow: 0 0 10px #3b82f6;
  animation: pulse 2s infinite;
}

.fish-count {
  position: absolute;
  bottom: 12px;
  background-color: rgba(15, 23, 42, 0.8);
  border: 1px solid #1e3a8a;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #93c5fd;
  box-shadow: 0 0 10px rgba(0,0,0,0.5);
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: .7; transform: scale(1.05); }
}

/* 核心指标框 - 深色半透明 */
.core-metrics {
  margin-bottom: 16px;
}

.metric-box {
  padding: 12px;
  border-radius: 8px;
  backdrop-filter: blur(4px);
}

.purple-box {
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
}

.green-box {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.metric-label {
  font-size: 12px;
  margin-bottom: 4px;
}

.purple-box .metric-label { color: #c4b5fd; }
.green-box .metric-label { color: #6ee7b7; }

.metric-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.purple-box .metric-value .num { font-size: 24px; color: #ddd6fe; font-weight: bold; }
.green-box .metric-value .num { font-size: 24px; color: #a7f3d0; font-weight: bold; }

.metric-value .rate { font-size: 12px; }
.metric-value .unit { font-size: 14px; color: #94a3b8; }

.text-red { color: #fca5a5; }
.text-green { color: #86efac; }

/* 详细数据列表 */
.detail-data {
  border-top: 1px dashed #1e293b;
  padding-top: 12px;
  font-size: 13px;
  color: #e2e8f0;
}

.detail-data .el-row {
  margin-bottom: 8px;
}

.detail-data .label {
  color: #94a3b8;
}

/* 统计总览卡片 */
.stats-overview .stat-card {
  text-align: center;
  background: linear-gradient(180deg, #0f172a, #020617) !important;
}

.stat-title {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 30px;
  font-weight: bold;
  color: #f8fafc;
  text-shadow: 0 0 10px rgba(255,255,255,0.1);
}

.text-blue-light { color: #60a5fa; text-shadow: 0 0 10px rgba(96, 165, 250, 0.3); }
.text-green-light { color: #34d399; text-shadow: 0 0 10px rgba(52, 211, 153, 0.3); }
.text-purple-light { color: #a78bfa; text-shadow: 0 0 10px rgba(167, 139, 250, 0.3); }

.stat-desc {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

.kpi-card {
  margin-bottom: 24px;
}

.link-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  height: 100%;
  padding-top: 8px;
}

/* 图表卡片 */
.chart-card {
  background: #0f172a !important;
}

.formula-tip {
  float: right;
  font-size: 12px;
  color: #64748b;
  font-weight: normal;
  max-width: 62%;
  text-align: right;
  line-height: 1.4;
}

.pond-header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pond-card--warning {
  border-color: rgba(250, 204, 21, 0.65) !important;
  box-shadow: 0 0 0 1px rgba(250, 204, 21, 0.25), 0 8px 24px rgba(250, 204, 21, 0.08);
}

.pond-card--danger {
  border-color: rgba(248, 113, 113, 0.75) !important;
  box-shadow: 0 0 0 1px rgba(248, 113, 113, 0.3), 0 8px 24px rgba(248, 113, 113, 0.12);
}

.blue-box {
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
}
.blue-box .metric-label { color: #93c5fd; }
.blue-box .metric-value .num { font-size: 20px; color: #bfdbfe; font-weight: bold; }

.metric-box--alert {
  border-color: rgba(248, 113, 113, 0.55) !important;
  background: rgba(248, 113, 113, 0.12) !important;
}

.ai-alert-card {
  margin-bottom: 16px;
  border-color: rgba(248, 113, 113, 0.35) !important;
}
.ai-alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fca5a5;
  font-weight: 600;
}
.ai-alert-list {
  margin: 0;
  padding-left: 18px;
  color: #e2e8f0;
  font-size: 13px;
  line-height: 1.7;
}
.ai-alert-list li + li {
  margin-top: 4px;
}
</style>
