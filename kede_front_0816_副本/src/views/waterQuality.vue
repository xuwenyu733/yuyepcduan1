<template>
  <div
    class="waterquality"
    :class="{
      'layout-compact': layoutCompact,
      'layout-tight': layoutTight
    }"
  >
    <PageLoadingMask
      :visible="dataLoading && !(realtime_data && realtime_data.length)"
      text="正在加载水质数据…"
    />

    <header class="header">
      <div class="header-bar">
        <div class="header-left">
          <button class="nav-btn btn-blue" @click="toPage('/index_daping1_son')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="7" x="3" y="3" rx="1"></rect><rect width="7" height="7" x="14" y="3" rx="1"></rect><rect width="7" height="7" x="14" y="14" rx="1"></rect><rect width="7" height="7" x="3" y="14" rx="1"></rect></svg>
            <span>数字孪生</span>
          </button>
          <button class="nav-btn btn-purple" @click="toPage('/index_daping2')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
            <span>视频监控</span>
          </button>
          <button class="nav-btn btn-cyan" @click="toPage('/index')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
            <span>数字化监控</span>
          </button>
        </div>

        <div class="header_bar">
          <div class="page-title">水质情况</div>
          <div class="time-text">{{ current_time }}</div>
          <div
            class="data-asof"
            :class="{ 'data-asof--stale': dataStale }"
            :title="dataStale ? '数据时间超过 3 分钟，可能延迟' : '设备上报时间'"
          >
            <template v-if="waterAsOf">数据 {{ waterAsOf }}<span v-if="dataStale"> · 延迟</span></template>
            <template v-else>数据 —</template>
          </div>
        </div>

        <div class="header-right">
          <div class="quick-btns">
            <button class="q-btn btn-red" @click="toPage('/alert_monitor?source=water&category=water')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
              <span>AI报警监控</span>
            </button>
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>存量分布</span>
            </button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
              <span>智能巡检</span>
            </button>
            <button class="q-btn btn-cyan-light" @click="toPage('/keyshow/inspectionreport')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
              <span>分析报告</span>
            </button>
            <button class="q-btn btn-pink" @click="goFishDisease()">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              <span>鱼病诊断</span>
            </button>
            <button class="q-btn btn-indigo" @click="toPage('/knowledge/zk-meeting-expert')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              <span>专家会议</span>
            </button>
            <!-- Excel：系统总览/水质综合目录去掉试验系统入口 -->
          </div>
        </div>
      </div>
    </header>

    <div class="filter-bar" :class="{ 'is-filter-loading': dataLoading && !siteOptionsReady }">
      <div class="filter-label">筛选</div>
      <div v-if="dataLoading && !siteOptionsReady" class="filter-loading-tip">
        <i class="wq-spinner"></i>
        <span>筛选项加载中…</span>
      </div>
      <div class="select-wrap">
        <label>车间</label>
        <select class="workshop" v-model="filter_workshop" @change="onFilterChange" :disabled="dataLoading && !siteOptionsReady">
          <option value="">{{ dataLoading && !siteOptionsReady ? '加载中…' : '全厂' }}</option>
          <option v-for="w in workshop_list" :key="w" :value="w">{{ w }}</option>
        </select>
      </div>
      <div class="select-wrap">
        <label>系统</label>
        <select class="system" v-model="filter_system" @change="onFilterChange" :disabled="dataLoading && !siteOptionsReady">
          <option value="">{{ dataLoading && !siteOptionsReady ? '加载中…' : '全部系统' }}</option>
          <option v-for="s in system_list" :key="s" :value="s">{{ s }}</option>
        </select>
      </div>
      <div class="select-wrap">
        <label>鱼池</label>
        <select class="pool" v-model="filter_pool" @change="onFilterChange" :disabled="dataLoading && !siteOptionsReady">
          <option value="">{{ dataLoading && !siteOptionsReady ? '加载中…' : '全部鱼池' }}</option>
          <option v-for="p in pool_list" :key="p" :value="p">{{ p }}</option>
        </select>
      </div>
      <div class="filter-hint" v-if="active_pool">
        当前选中：{{ displayLakeName(active_pool) }}
        <button type="button" class="filter-clear-btn" @click="clearPoolSelection">清除选中</button>
      </div>
    </div>

    <div class="content">
      <div class="card-row-top" :class="{ 'is-section-loading': dataLoading && !filteredRealtime.length }">
        <div
          v-for="m in metrics"
          :key="m.key"
          :class="['index-card', m.cardClass, metricStatus(m.key) !== '正常' ? 'card-alert' : '']"
          :title="'查看' + m.label + '历史趋势'"
          @click="goMetricTrend(m.key)"
        >
          <div class="card-top">
            <span class="material-symbols-sharp card-icon" aria-hidden="true">{{ m.icon }}</span>
            <span class="card-label">{{ m.label }}</span>
          </div>
          <div class="card-value">
            <template v-if="dataLoading && !cardPool[m.key]">
              <span class="inline-loading"><i class="wq-spinner"></i></span>
            </template>
            <template v-else>{{ cardPool[m.key] || '--' }}<small v-if="m.unit">{{ m.unit }}</small></template>
          </div>
          <div class="card-range">设定 {{ m.range }}</div>
        </div>
      </div>

      <div class="main-row">
        <div class="panel panel-table">
          <div class="panel-title">
            <span class="title-text">实时水质数据</span>
            <div class="title-meta">
              <span class="tag">{{ filteredRealtime.length }} 个监测点</span>
              <span v-if="demoMode" class="tag demo-tag">演示模拟</span>
            </div>
          </div>
          <div class="panel-content">
            <div class="panel-scroll">
              <table class="water-table">
                <thead>
                  <tr>
                    <th>鱼池</th>
                    <th>pH</th>
                    <th>DO</th>
                    <th>温℃</th>
                    <th>浊度</th>
                    <th>ORP</th>
                    <th>碱度</th>
                    <th>硬度</th>
                    <th>硝酸盐</th>
                    <th>氨氮</th>
                    <th>CO₂</th>
                    <th>TSS</th>
                    <th>总磷</th>
                    <th>总氮</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="row in filteredRealtime"
                    :key="row.Id"
                    class="clickable"
                    :class="{ 'row-active': row.lake_name === active_pool }"
                    @click="selectPool(row.lake_name)"
                  >
                    <td class="td-name">{{ displayLakeName(row.lake_name) }}</td>
                    <td :style="cellStyle(row, 'lake_ph')">{{ row.lake_ph }}</td>
                    <td :style="cellStyle(row, 'lake_do')">{{ row.lake_do }}</td>
                    <td :style="cellStyle(row, 'lake_temp')">{{ row.lake_temp }}</td>
                    <td :style="cellStyle(row, 'lake_tubid')">{{ row.lake_tubid }}</td>
                    <td :style="cellStyle(row, 'lake_orp')">{{ row.lake_orp }}</td>
                    <td :style="cellStyle(row, 'lake_alkalin')">{{ row.lake_alkalin }}</td>
                    <td :style="cellStyle(row, 'lake_hard')">{{ row.lake_hard }}</td>
                    <td :style="cellStyle(row, 'lake_nirate')">{{ row.lake_nirate }}</td>
                    <td :style="cellStyle(row, 'lake_an')">{{ row.lake_an }}</td>
                    <td :style="cellStyle(row, 'lake_co2')">{{ row.lake_co2 }}</td>
                    <td :style="cellStyle(row, 'lake_tss')">{{ row.lake_tss }}</td>
                    <td :style="cellStyle(row, 'lake_p')">{{ row.lake_p }}</td>
                    <td :style="cellStyle(row, 'lake_n')">{{ row.lake_n }}</td>
                    <td>
                      <span :class="['status-tag', statusClass(poolStatus(row))]">{{ poolStatus(row) }}</span>
                    </td>
                  </tr>
                  <tr v-if="dataLoading && filteredRealtime.length === 0">
                    <td colspan="15" class="td-empty">
                      <div class="wq-loading-block">
                        <i class="wq-spinner wq-spinner-lg"></i>
                        <span>正在加载全厂水质数据…</span>
                      </div>
                    </td>
                  </tr>
                  <tr v-else-if="filteredRealtime.length === 0">
                    <td colspan="15" class="td-empty">
                      <div class="empty-manage">
                        <span>暂无水质监测数据，请前往数据管理页录入</span>
                        <button type="button" class="empty-manage-btn" @click="goWaterDataManage">水质数据管理</button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="panel tank-panel">
          <div class="panel-title">
            <span class="title-text">鱼池3D实时监控</span>
            <span class="pool-chip">{{ displayLakeName(active_pool) || '未选择' }}</span>
          </div>
          <div class="panel-content tank-stage">
            <div class="tank-3d-box">
              <div class="tank-scene">
                <div :class="['tank-cube', tankVisualClass]">
                  <div class="tank-fill" :style="{ height: tankFillPct + '%' }">
                    <div class="tank-surface"></div>
                    <div class="tank-bubbles">
                      <span></span><span></span><span></span>
                    </div>
                  </div>
                  <div class="tank-gloss"></div>
                </div>
              </div>
              <div class="tank-footer" v-if="cardPool.lake_name">
                <span>pH {{ cardPool.lake_ph }}</span>
                <span>DO {{ cardPool.lake_do }}</span>
                <span>T {{ cardPool.lake_temp }}℃</span>
                <span :class="['tank-status', tankVisualClass || 'ok']">{{ poolStatus(cardPool) }}</span>
              </div>
              <div class="tank-empty" v-else-if="dataLoading">
                <div class="wq-loading-block compact">
                  <i class="wq-spinner"></i>
                  <span>加载中…</span>
                </div>
              </div>
              <div class="tank-empty" v-else>点击左侧表格选择鱼池</div>
            </div>
          </div>
        </div>

        <div class="panel warning-panel">
          <div class="panel-title warn-title">
            <span class="title-text">
              <span class="material-symbols-sharp">warning</span>
              AI报警监控
            </span>
            <button type="button" class="link-all" @click.stop="toPage('/alert_monitor?source=water&category=water')">全部 →</button>
          </div>
          <div class="panel-content">
            <div class="panel-scroll">
              <div v-if="dataLoading && warning_data.length === 0" class="wq-loading-block">
                <i class="wq-spinner wq-spinner-lg"></i>
                <span>正在加载报警数据…</span>
              </div>
              <template v-else>
                <div
                  v-for="a in warning_data"
                  :key="a.Id"
                  :class="['alarm-item', a.warning_status === '-1' ? 'alarm-done' : 'alarm-warn']"
                  @click="selectPool(a.warning_lake)"
                >
                  <div class="alarm-top">
                    <span class="alarm-main">
                      <span
                        class="material-symbols-sharp alarm-icon"
                        :class="a.warning_status === '-1' ? 'color-c' : 'color-w'"
                      >{{ a.warning_status === '-1' ? 'check_circle' : 'error' }}</span>
                      <span class="alarm-text">{{ a.warning_lake }} · {{ a.warning_data }}{{ a.warning_data_type === '1' ? '偏高' : '偏低' }}</span>
                    </span>
                    <span :class="['alarm-tag', alarmTagClass(a.warning_status)]">{{ alarmStatusText(a.warning_status) }}</span>
                  </div>
                  <div class="alarm-val">当前值 {{ a.warning_value }} {{ a.warning_unit }}</div>
                  <div class="alarm-time">{{ formatAlarmTime(a) }}</div>
                </div>
                <div v-if="warning_data.length === 0" class="empty-hint">暂无报警数据</div>
              </template>
            </div>
          </div>
        </div>
      </div>

      <div class="bottom-row">
        <div class="panel bottom-chart-panel">
          <div class="panel-title chart-head">
            <span class="title-text">历史数据及趋势</span>
            <div class="chart-controls">
              <div class="chart-tab-bar">
                <button
                  v-for="m in chartMetrics"
                  :key="m.key"
                  :class="{ active: activeChartMetrics.includes(m.key) }"
                  @click="toggleChartMetric(m.key)"
                >{{ m.label }}</button>
              </div>
              <div class="time-selector">
                <button
                  v-for="r in ['24h', '3d', '7d']"
                  :key="r"
                  :class="{ active: chartRange === r }"
                  @click="setChartRange(r)"
                >{{ r === '24h' ? '24小时' : r === '3d' ? '3天' : '7天' }}</button>
              </div>
            </div>
          </div>
          <div class="panel-content bottom-chart-content">
            <div
              v-if="dataLoading && !(history_data && history_data.length)"
              class="wq-loading-block chart-loading"
            >
              <i class="wq-spinner wq-spinner-lg"></i>
              <span>正在加载历史趋势…</span>
            </div>
            <div id="historyChart" class="bottom-chart-wrap" :class="{ 'is-dimmed': dataLoading && !(history_data && history_data.length) }"></div>
          </div>
        </div>

        <div class="panel report-panel">
          <div class="panel-title">
            <span class="title-text">
              <span class="material-symbols-sharp">vital_signs</span>
              AI 巡检报告
            </span>
            <button
              type="button"
              class="auto-generation"
              :disabled="inspectionReportLoading"
              @click="loadInspectionReportFromDb"
              title="从库表刷新最新巡检报告"
            >{{ inspectionReportLoading ? '加载中…' : '自动生成' }}</button>
          </div>
          <div class="panel-content">
            <div class="panel-scroll">
              <div v-if="inspectionReportLoading && !dbInspectionReport" class="wq-loading-block">
                <i class="wq-spinner wq-spinner-lg"></i>
                <span>正在加载巡检报告…</span>
              </div>
              <div v-else-if="!dbInspectionReport" class="report-empty">
                暂无库内巡检报告，请先在「智能巡检报告」中生成
                <button type="button" class="report-link-btn" @click="toPage('/keyshow/inspectionreport')">去查看</button>
              </div>
              <template v-else>
                <div class="report-score-row">
                  <div class="report-score">{{ reportScore }}</div>
                  <div class="report-level">{{ reportLevel }}</div>
                </div>
                <div class="report-summary">{{ reportSummary }}</div>
                <div class="report-section" v-if="reportFindings.length">
                  <h4>主要发现</h4>
                  <div
                    v-for="(item, i) in reportFindings"
                    :key="'f' + i"
                    :class="['report-item', item.type]"
                  >{{ item.text }}</div>
                </div>
                <div class="report-section report-suggest" v-if="reportSuggestions.length">
                  <h4>处理建议</h4>
                  <div
                    v-for="(item, i) in reportSuggestions"
                    :key="'s' + i"
                    class="report-item"
                  >{{ item }}</div>
                </div>
                <div class="report-footer">更新于 {{ reportUpdatedAt }}</div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>
    <MyChat />
  </div>
</template>

<script>
// import $ from "jquery";

import * as echarts from "echarts";
import MyChat from '@/components/MyChat/index'
import PageLoadingMask from '@/components/PageLoadingMask/index'
import { metricStatusByKey, metricCellStyle } from "@/utils/waterMetricStatus";
import {
  USE_WATER_QUALITY_DEMO,
  getSharedWaterQualityDemoState,
  getSharedWaterQualitySnapshot,
} from "@/utils/waterQualityDemoGenerator";
import { WATER_QUALITY_METRICS } from "@/utils/waterQualityMetrics";
import { syncModuleAiAlarmsToMission } from '@/utils/alarmTaskBridge';
import { formatWorkshop, formatSystem, formatPondCode, workshopsEqual, systemsEqual, normalizeLakeName, parseSiteString } from '@/utils/siteNaming';
import { loadSharedSiteOptions as fetchSiteOptions, pickWorkshopOptionLabels, pickSystemOptionLabels, pickPondOptionLabels, isPlatformUnscopedAdmin, SITE_OPTIONS_CHANGED_EVENT, clearSharedSiteOptionsCache } from '@/utils/siteOptions';
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope';
import { getWaterThresholdConfig, evaluateWaterStatus, getTempRange } from './index_daping3_waterThreshold';
import { evaluateAssociationAndPush } from '@/utils/associationBridge';
import { getZkInspectionReportPage } from '@/api/fish/zkInspectionReport';
import { fetchWaterQualityLiveData, fetchMentougouHistoryForChart } from '@/utils/waterQualityLiveBridge';
/** 甲方演示开关：USE_WATER_QUALITY_DEMO 在 waterQualityDemoGenerator.js，与总览共用。 */
// import { ref } from "vue";

export default {

  name: "WaterQuality",
  components: { MyChat, PageLoadingMask },
  data() {
    return {
      METRICS: WATER_QUALITY_METRICS,
      myChart: null,
      realtime_data: [],
      history_data: [],
      warning_data: [],
      active_pool: "",
      filter_workshop: "",
      filter_system: "",
      filter_pool: "",
      current_time: "",
      /** 设备上报时间（非本地时钟） */
      waterAsOf: "",
      dataStale: false,
      chartRange: "24h",
      activeChartMetrics: ["lake_ph", "lake_do", "lake_temp", "lake_an"],
      metrics: WATER_QUALITY_METRICS,
      chartMetrics: WATER_QUALITY_METRICS,
      refreshTimer: null,
      /** 演示发生器内部状态（仅 USE_WATER_QUALITY_DEMO 时使用） */
      _demoState: null,
      /** 125：与其他页共用的车间/系统/池选项 */
      sharedSiteOptions: { workshops: [], systems: [], ponds: [] },
      /** 站点白名单是否已按当前用户拉完（用于区分「未加载」与「空部门」） */
      siteOptionsReady: false,
      /** 首屏/强制刷新加载中，避免空表误导为无数据 */
      dataLoading: false,
      /** 81/98：水质阈值（阈值管理 BFF），驱动 poolStatus / 指标区间 */
      waterThresholdConfig: null,
      /** 低分辨率 / 高 DPI 时压缩底部图表，避免中间区域被挤占 */
      layoutCompact: false,
      /** 更矮视口：进一步压缩底部，保住中间 3D/报警 */
      layoutTight: false,
      /** 来自 zk_inspection_report 的最新一条 */
      dbInspectionReport: null,
      inspectionReportLoading: false,
    };
  },
  watch: {
    layoutCompact() {
      this.$nextTick(() => {
        if (this.myChart) {
          this.updateChart();
          this.myChart.resize();
        }
      });
    },
    layoutTight() {
      this.$nextTick(() => {
        if (this.myChart) {
          this.updateChart();
          this.myChart.resize();
        }
      });
    }
  },
  computed: {
    demoMode() {
      return USE_WATER_QUALITY_DEMO;
    },
    current_time_short() {
      return this.current_time.split(" ")[1] || "";
    },
    filteredRealtime() {
      return this.realtime_data.filter((row) => {
        if (!row.lake_name) return false;
        // 实时行来自部门范围内的 zk_pond，不再用站点白名单二次滤空（名称/系统字段不一致会导致首屏全空）
        // 仅应用顶部「全厂 / 车间 / 系统 / 鱼池」UI 筛选
        const parts = this.parsePoolName(row.lake_name);
        const workshop = row.workshopName || row.workshop || parts.workshop || '';
        const system = row.systemName || row.pondSystemName || parts.system || '';
        if (this.filter_workshop && !workshopsEqual(workshop, this.filter_workshop))
          return false;
        if (this.filter_system && !systemsEqual(system, this.filter_system))
          return false;
        if (this.filter_pool && normalizeLakeName(row.lake_name) !== normalizeLakeName(this.filter_pool))
          return false;
        return true;
      });
    },
    workshop_list() {
      return pickWorkshopOptionLabels(this.sharedSiteOptions);
    },
    system_list() {
      return pickSystemOptionLabels(this.sharedSiteOptions, this.filter_workshop);
    },
    pool_list() {
      return pickPondOptionLabels(this.sharedSiteOptions, this.filter_workshop, this.filter_system);
    },
    cardPool() {
      // 必须走 filteredRealtime，避免部门无站点时指标卡仍显示未过滤的 realtime_data
      const list = this.filteredRealtime || [];
      if (!list.length) return {};
      if (this.active_pool) {
        const hit = list.find((r) => r.lake_name === this.active_pool);
        if (hit) return hit;
      }
      return list[0] || {};
    },
    tankVisualClass() {
      const s = this.poolStatus(this.cardPool);
      if (s === "超标") return "danger";
      if (s === "预警") return "warn";
      return "";
    },
    /** 3D 鱼缸水位：按溶解氧相对正常区间映射，缺省约 62% */
    tankFillPct() {
      const pool = this.cardPool || {};
      const doVal = Number(pool.lake_do);
      if (!Number.isFinite(doVal) || doVal <= 0) return 62;
      const pct = Math.round(Math.min(88, Math.max(28, (doVal / 10) * 100)));
      return pct;
    },
    reportPayload() {
      return this.parseInspectionReportPayload(this.dbInspectionReport);
    },
    reportScore() {
      const p = this.reportPayload;
      if (p && p.score != null) return p.score;
      return '--';
    },
    reportLevel() {
      const p = this.reportPayload;
      if (p && p.level) return p.level;
      return '暂无评估';
    },
    reportSummary() {
      const p = this.reportPayload;
      if (p && p.summary) return p.summary;
      return '暂无库内巡检报告摘要';
    },
    reportFindings() {
      const p = this.reportPayload;
      return (p && p.findings) || [];
    },
    reportSuggestions() {
      const p = this.reportPayload;
      return (p && p.suggestions) || [];
    },
    reportUpdatedAt() {
      const row = this.dbInspectionReport;
      if (!row || row.createTime == null) return this.current_time_short || '--';
      const t = row.createTime;
      const d = new Date(typeof t === 'number' ? t : String(t).replace(/-/g, '/'));
      if (isNaN(d.getTime())) return this.current_time_short || '--';
      const pad = (n) => String(n).padStart(2, '0');
      return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },
  },
  mounted() {
    this.metrics = this.METRICS;
    this.tickClock();
    if (USE_WATER_QUALITY_DEMO) {
      this._demoState = getSharedWaterQualityDemoState();
    }
    this.loadWaterThresholds();
    this.loadInspectionReportFromDb();
    // 并行：筛选项与水质数据；首屏优先走缓存（force=false），切换部门时再强制刷新
    this.dataLoading = true;
    Promise.all([
      Promise.resolve(this.loadSharedSiteOptions(false)).catch(() => null),
      Promise.resolve(this.loadAllData(false)).catch((e) => {
        console.warn('[waterQuality] 首屏加载失败', e && (e.message || e));
      })
    ]).finally(() => {
      if (!this._isDestroyed) this.dataLoading = false;
    });
    // 演示：2s；接库 30s。后台标签页暂停轮询，避免切回时定时器堆积把页面卡死
    this._pollIntervalMs = USE_WATER_QUALITY_DEMO ? 2000 : 30000;
    this.startWaterPolling();
    this.clockTimer = setInterval(this.tickClock, 1000);
    this._onVisibilityChange = () => {
      if (this._isDestroyed) return;
      if (document.hidden) {
        this.stopWaterPolling();
        return;
      }
      this.startWaterPolling();
      // 回前台只补一次轻量刷新，禁止 force 风暴
      if (!this._loadInflight) {
        Promise.resolve(this.loadAllData(false)).catch(() => null);
      }
      this.$nextTick(() => {
        if (this.myChart) {
          try { this.myChart.resize(); } catch (e) { /* ignore */ }
        }
      });
    };
    document.addEventListener('visibilitychange', this._onVisibilityChange);
    this._onSiteOptionsChanged = () => {
      if (!this._isDestroyed) this.reloadSiteFilters();
    };
    window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    this._onUserScopeChanged = async () => {
      if (this._isDestroyed) return;
      this.realtime_data = [];
      this.history_data = [];
      this.warning_data = [];
      this.active_pool = '';
      this.filter_workshop = '';
      this.filter_system = '';
      this.filter_pool = '';
      this.siteOptionsReady = false;
      this.sharedSiteOptions = { workshops: [], systems: [], ponds: [], workshopOptions: [], systemOptions: [], pondOptions: [] };
      try { clearSharedSiteOptionsCache(); } catch (e) { /* ignore */ }
      this.dataLoading = true;
      try {
        await this.reloadSiteFilters();
        await this.loadAllData(true);
      } finally {
        if (!this._isDestroyed) this.dataLoading = false;
      }
    };
    window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    this.$nextTick(() => {
      if (this._isDestroyed) return;
      this.updateCompactLayout();
      const el = document.getElementById("historyChart");
      if (!el || !el.isConnected) return;
      try {
        this.myChart = echarts.init(el, "dark");
        this._onChartResize = () => {
          this.updateCompactLayout();
          this.$nextTick(() => this.myChart && this.myChart.resize());
        };
        window.addEventListener("resize", this._onChartResize);
        if (window.visualViewport) {
          window.visualViewport.addEventListener("resize", this._onChartResize);
        }
        this.updateChart();
      } catch (e) {
        console.warn("waterQuality chart init skipped", e);
      }
    });
  },
  activated() {
    // 从其他页返回：校正筛选项；若列表为空则补拉全厂数据
    this.reloadSiteFilters();
    if (!(this.realtime_data && this.realtime_data.length) && !this.dataLoading) {
      this.dataLoading = true;
      Promise.resolve(this.loadAllData(true))
        .catch(() => null)
        .finally(() => {
          if (!this._isDestroyed) this.dataLoading = false;
        });
    }
  },
  beforeDestroy() {
    this._isDestroyed = true;
    if (this._onVisibilityChange) {
      document.removeEventListener('visibilitychange', this._onVisibilityChange);
      this._onVisibilityChange = null;
    }
    if (this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    if (this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    }
    this.stopWaterPolling();
    if (this.clockTimer) clearInterval(this.clockTimer);
    if (this._onChartResize) {
      window.removeEventListener("resize", this._onChartResize);
      if (window.visualViewport) {
        window.visualViewport.removeEventListener("resize", this._onChartResize);
      }
    }
    if (this.myChart) {
      this.myChart.dispose();
      this.myChart = null;
    }
  },
  methods: {
    /** 视口偏矮或系统缩放偏高时，缩小底部图表高度，优先保住中间三栏 */
    updateCompactLayout() {
      const root = this.$el;
      // 优先用页面实际可用高度（含被布局壳裁切后的高度），避免只用 window 误判
      const avail =
        (root && root.clientHeight) ||
        (window.visualViewport && window.visualViewport.height) ||
        window.innerHeight ||
        0;
      const w = window.innerWidth || 0;
      const dpr = window.devicePixelRatio || 1;
      // Windows 125%/150% 缩放时 CSS 像素变少，同等逻辑高度更易挤爆中间区
      const scaled = avail / Math.max(1, dpr > 1.1 ? dpr * 0.85 : 1);
      const nextCompact =
        avail < 920 ||
        scaled < 780 ||
        (dpr >= 1.25 && avail < 1000) ||
        (dpr >= 1.5 && avail < 1100) ||
        (dpr >= 2 && avail < 1200) ||
        (w > 0 && avail / w < 0.55);
      const nextTight =
        avail < 780 ||
        scaled < 680 ||
        (dpr >= 1.5 && avail < 900) ||
        (dpr >= 2 && avail < 1000);
      if (this.layoutCompact !== nextCompact) this.layoutCompact = nextCompact;
      if (this.layoutTight !== nextTight) this.layoutTight = nextTight;
    },
    /** 解析 zk_inspection_report.ai_agent_report（JSON 或纯文本） */
    parseAiAgentJson(raw) {
      if (raw == null || raw === '') return {};
      if (typeof raw === 'object') return raw;
      const text = String(raw).trim();
      if (!text) return {};
      try {
        return JSON.parse(text);
      } catch (e) {
        return { comprehensiveEvaluation: text };
      }
    },
    /** 从报告正文提取百分制分数（库内常见「评分均为7分」→70） */
    extractReportScore(text) {
      const s = String(text || '');
      let m = s.match(/评分[^0-9]{0,8}(\d+(?:\.\d+)?)\s*分/);
      if (!m) m = s.match(/综合[评得]分[^0-9]{0,6}(\d+(?:\.\d+)?)/);
      if (!m) m = s.match(/健康度[^0-9]{0,6}(\d+(?:\.\d+)?)/);
      if (!m) return null;
      const v = parseFloat(m[1]);
      if (!Number.isFinite(v)) return null;
      if (v <= 10) return Math.round(v * 10);
      if (v <= 100) return Math.round(v);
      return null;
    },
    scoreToLevel(score) {
      if (score == null || score === '--') return '暂无评估';
      const n = Number(score);
      if (!Number.isFinite(n)) return '暂无评估';
      if (n >= 85) return '水质状况良好';
      if (n >= 70) return '水质需关注';
      return '水质存在风险';
    },
    splitReportLines(text) {
      if (!text) return [];
      return String(text)
        .split(/(?:\d+[\._、．]\s*|\n+|；|;)/)
        .map((x) => x.replace(/^_/, '').trim())
        .filter((x) => x && x.length > 1)
        .slice(0, 6);
    },
    parseInspectionReportPayload(row) {
      if (!row) return null;
      const parsed = this.parseAiAgentJson(row.aiAgentReport);
      const evalText =
        row.evaluation ||
        row.conclusion ||
        parsed.comprehensiveEvaluation ||
        '';
      const score =
        this.extractReportScore(evalText) ||
        this.extractReportScore(row.aiAgentReport) ||
        null;
      const site = [row.workshopName, row.pondSystemName || row.pondName]
        .filter(Boolean)
        .join(' · ');
      const summaryCore = String(evalText || '').replace(/\s+/g, ' ').trim();
      const summary = site
        ? `${site}：${summaryCore || '已生成巡检报告'}`
        : (summaryCore || '已生成巡检报告');
      const findingSrc = parsed.subItemSuggestions || row.differentialAnalysis || '';
      const findings = this.splitReportLines(findingSrc).map((text) => ({
        type: /异常|风险|关注|偏低|偏高|故障/.test(text) ? 'warn' : 'ok',
        text
      }));
      if (!findings.length && summaryCore) {
        findings.push({ type: 'ok', text: summaryCore.slice(0, 120) });
      }
      const suggestSrc = parsed.longTermOptimization || row.humanReport || '';
      const suggestions = this.splitReportLines(suggestSrc);
      if (!suggestions.length && parsed.longTermOptimization) {
        suggestions.push(String(parsed.longTermOptimization).slice(0, 160));
      }
      return {
        score: score != null ? score : '--',
        level: this.scoreToLevel(score),
        summary: summary.length > 160 ? summary.slice(0, 160) + '…' : summary,
        findings,
        suggestions
      };
    },
    /** 从 zk_inspection_report 拉取最新报告（可按车间/池筛选） */
    async loadInspectionReportFromDb() {
      this.inspectionReportLoading = true;
      try {
        const query = { pageNo: 1, pageSize: 1 };
        if (this.filter_workshop) query.workshopName = this.filter_workshop;
        if (this.filter_pool) {
          const name = normalizeLakeName(this.filter_pool) || this.filter_pool;
          query.pondName = name;
        } else if (this.filter_system) {
          query.pondSystemName = this.filter_system;
        }
        let res = await getZkInspectionReportPage(query);
        let list = (res && res.data && res.data.list) || (res && res.list) || [];
        // 精确筛选无结果时：仅真超管回退全厂最新一条；部门用户 / 空部门非超管保持空
        if (!list.length && (query.workshopName || query.pondName || query.pondSystemName) && isPlatformUnscopedAdmin()) {
          res = await getZkInspectionReportPage({ pageNo: 1, pageSize: 1 });
          list = (res && res.data && res.data.list) || (res && res.list) || [];
        }
        this.dbInspectionReport = list[0] || null;
      } catch (e) {
        console.warn('[waterQuality] 巡检报告加载失败', e && (e.message || e));
        this.dbInspectionReport = null;
      } finally {
        this.inspectionReportLoading = false;
      }
    },
    async loadSharedSiteOptions(force = false) {
      try {
        this.sharedSiteOptions = await fetchSiteOptions({
          includePonds: true,
          force: !!force
        });
      } catch (e) {
        console.warn('[waterQuality] shared site options', e);
        this.sharedSiteOptions = this.sharedSiteOptions || { workshops: [], systems: [], ponds: [] };
      } finally {
        this.siteOptionsReady = true;
      }
    },
    /** 重新拉取车间/系统/鱼池选项，并校正当前筛选是否仍有效 */
    async reloadSiteFilters() {
      await this.loadSharedSiteOptions(true);
      this.sanitizeSiteFilters();
      this.onFilterChange();
    },
    sanitizeSiteFilters() {
      const workshops = this.workshop_list || [];
      if (this.filter_workshop && !workshops.includes(this.filter_workshop)) {
        this.filter_workshop = '';
      }
      const systems = this.system_list || [];
      if (this.filter_system && !systems.includes(this.filter_system)) {
        this.filter_system = '';
      }
      const pools = this.pool_list || [];
      if (this.filter_pool && !pools.some((p) => normalizeLakeName(p) === normalizeLakeName(this.filter_pool))) {
        this.filter_pool = '';
        this.active_pool = '';
      }
    },
    displayLakeName(name) {
      return normalizeLakeName(name);
    },
    /** 81/98：拉取阈值管理中的水质分区，同步到本页判定与指标 range */
    async loadWaterThresholds() {
      try {
        const cfg = await getWaterThresholdConfig();
        this.waterThresholdConfig = cfg;
        this.applyWaterThresholdRanges(cfg);
      } catch (e) {
        console.warn('[waterQuality] water thresholds', e);
      }
    },
    applyWaterThresholdRanges(cfg) {
      if (!cfg) return;
      const findM = (id) => (cfg.metrics || []).find((m) => m && m.id === id);
      const nh3Safe = (cfg.nh3 && cfg.nh3.levels && cfg.nh3.levels[0]) ? cfg.nh3.levels[0].value : 0.5;
      const doNormal = (cfg.doLevels && cfg.doLevels.normal != null) ? cfg.doLevels.normal : 6;
      const no3 = findM('no3');
      const ph = findM('ph');
      const orp = findM('orp');
      const alk = findM('alk');
      const hard = findM('hard');
      const co2 = findM('co2');
      const tss = findM('tss');
      const tp = findM('tp');
      const tn = findM('tn');
      const temp = getTempRange(cfg);
      const patch = {
        lake_an: `0-${nh3Safe}`,
        lake_do: `${doNormal}-20`,
        lake_nirate: no3 ? `0-${no3.warnHigh}` : undefined,
        lake_ph: ph ? `${ph.warnLow}-${ph.warnHigh}` : undefined,
        lake_orp: orp ? `${orp.warnLow}-${orp.warnHigh}` : undefined,
        lake_alkalin: alk ? `${alk.warnLow}-${alk.warnHigh}` : undefined,
        lake_hard: hard ? `${hard.warnLow}-${hard.warnHigh}` : undefined,
        lake_co2: co2 ? `0-${co2.warnHigh}` : undefined,
        lake_tss: tss ? `0-${tss.warnHigh}` : undefined,
        lake_p: tp ? `0-${tp.warnHigh}` : undefined,
        lake_n: tn ? `0-${tn.warnHigh}` : undefined,
        lake_temp: temp ? `${temp.warnLow}-${temp.warnHigh}` : undefined
      };
      const apply = (list) => (list || []).map((m) => {
        const range = patch[m.key];
        return range != null ? { ...m, range: String(range) } : m;
      });
      this.METRICS = apply(this.METRICS);
      this.metrics = apply(this.metrics);
      this.chartMetrics = apply(this.chartMetrics);
    },
    toPage(path) {
      if (!path) return;
      if (String(path).indexOf('?') >= 0) {
        const [p, q] = String(path).split('?');
        const query = {};
        String(q || '').split('&').forEach((pair) => {
          const [k, v] = pair.split('=');
          if (k) query[decodeURIComponent(k)] = decodeURIComponent(v || '');
        });
        this.$router.push({ path: p, query }).catch(() => {});
        return;
      }
      this.$router.push(path).catch(() => {});
    },
    goFishDisease() {
      this.$router.push({ path: '/fish-disease', query: { from: '/water_quality' } }).catch(() => {});
    },
    goWaterDataManage() {
      this.$router.push({ path: '/keyshow/waterQualityManage', query: { from: '/water_quality' } }).catch(() => {});
    },
    tickClock() {
      const d = new Date();
      const p = (n) => String(n).padStart(2, "0");
      this.current_time = `${p(d.getMonth() + 1)}/${p(d.getDate())} ${p(
        d.getHours()
      )}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
    },
    parsePoolName(name) {
      const site = parseSiteString(name);
      return {
        workshop: site.workshop || '',
        system: site.system || '',
        pool: site.label || normalizeLakeName(name)
      };
    },
    workshopsEqual,
    systemsEqual,
    async querySql(sql) {
      // 遗留直连 SQL（已不再用于水质页展示；现数据来自 Java API / zk_device_record）
      const res = await request.post(
        "mysql/getDataListBySql.jsp",
        { sqlstring: [sql] },
        { noVerify: true, noAuth: true }
      );
      return res.data && res.data[0] ? res.data[0] : [];
    },
    startWaterPolling() {
      if (this.refreshTimer || this._isDestroyed || document.hidden) return;
      const ms = this._pollIntervalMs || 30000;
      this.refreshTimer = setInterval(() => {
        if (this._isDestroyed || document.hidden || this._loadInflight) return;
        // 前台轮询用 force，保证实时；有互斥不会叠请求
        this.loadAllData(!USE_WATER_QUALITY_DEMO);
      }, ms);
    },
    stopWaterPolling() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer);
        this.refreshTimer = null;
      }
    },
    async loadAllData(force = false) {
      if (this._isDestroyed) return;
      // 离开浏览器再回来时定时器会连发：合并为单次进行中的请求
      if (this._loadInflight) {
        this._loadQueuedForce = this._loadQueuedForce || !!force;
        return this._loadInflight;
      }
      this._loadInflight = this._loadAllDataInner(!!force).finally(() => {
        this._loadInflight = null;
        if (this._isDestroyed) return;
        if (this._loadQueuedForce) {
          this._loadQueuedForce = false;
          Promise.resolve(this.loadAllData(true)).catch(() => null);
        }
      });
      return this._loadInflight;
    },
    async _loadAllDataInner(force = false) {
      if (USE_WATER_QUALITY_DEMO) {
        if (!this._demoState) this._demoState = getSharedWaterQualityDemoState();
        const snap = getSharedWaterQualitySnapshot({ tickIfStaleMs: 1800 });
        this.realtime_data = snap.realtime;
        this.history_data = snap.history;
        this.warning_data = snap.warnings;
        if (this.realtime_data.length && !this.active_pool) {
          this.active_pool = this.realtime_data[0].lake_name;
        }
        this.updateChart();
        var now = Date.now();
        if (!this._lastMissionSyncAt || now - this._lastMissionSyncAt > 120000) {
          this._lastMissionSyncAt = now;
          this.syncWarningsToMission();
          this.pushWaterAssociationIfNeeded();
        }
        return;
      }

      try {
        const data = await fetchWaterQualityLiveData(!!force);
        this.realtime_data = data.realtime || [];
        this.history_data = data.history || [];
        this.warning_data = data.warnings || [];
        this.waterAsOf = data.waterAsOf || '';
        this.dataStale = !!data.dataStale;
        if (!this.realtime_data.length || data.emptyDeptSites) {
          this.active_pool = '';
          this.history_data = [];
          this.warning_data = [];
        } else if (!this.active_pool) {
          this.active_pool = this.realtime_data[0].lake_name;
        } else if (!this.realtime_data.some((r) => r.lake_name === this.active_pool)) {
          this.active_pool = this.realtime_data[0].lake_name;
        }
        // 部门无站点时 filteredRealtime 为空：同步清空选中池，避免指标卡/3D 仍显示
        this.$nextTick(() => {
          if (this._isDestroyed) return;
          const visible = this.filteredRealtime || [];
          if (!visible.length) {
            this.active_pool = '';
          } else if (!visible.some((r) => r.lake_name === this.active_pool)) {
            this.active_pool = visible[0].lake_name;
          }
          this.updateChart();
        });
        this.updateChart();
        // 门头沟：仅在本地还没有足够历史点时补拉，避免每 30s / 回前台重复打上游
        const histLen = (this.history_data || []).length;
        const poolCount = Math.max(1, (this.realtime_data || []).length);
        if (
          data.source === 'mentougou-hflrwl' &&
          data.historyPending &&
          !(data.emptyDeptSites) &&
          histLen <= poolCount * 2
        ) {
          this.fillMentougouHistoryChart();
        }
        // 偶发：指标接口慢导致全是「--」时自动补拉一次（不循环）
        const hasMetricVal = (this.realtime_data || []).some((r) =>
          r && ['lake_ph', 'lake_do', 'lake_temp', 'lake_orp', 'lake_an'].some((k) =>
            r[k] != null && r[k] !== '' && !Number.isNaN(Number(r[k]))
          )
        );
        if (data.metricsIncomplete || ((this.realtime_data || []).length && !hasMetricVal)) {
          if (!this._wqEmptyRetried) {
            this._wqEmptyRetried = true;
            setTimeout(() => {
              if (this._isDestroyed) return;
              Promise.resolve(this.loadAllData(true)).finally(() => {
                this._wqEmptyRetried = false;
              });
            }, 1200);
          }
        } else {
          this._wqEmptyRetried = false;
        }
        var nowDb = Date.now();
        if (!this._lastMissionSyncAt || nowDb - this._lastMissionSyncAt > 120000) {
          this._lastMissionSyncAt = nowDb;
          // 任务同步不阻塞首屏，放到下一拍
          setTimeout(() => {
            if (this._isDestroyed) return;
            this.syncWarningsToMission();
            this.pushWaterAssociationIfNeeded();
          }, 0);
        }
      } catch (e) {
        const msg = (e && (e.message || e.msg || e)) || 'unknown';
        console.error('[waterQuality] 库表数据加载失败:', msg, e);
      }
    },
    /** 遗留：旧 SQL 实时水质拉取（水质页已改走 fetchWaterQualityLiveData） */
    async getRealTimeData() {
      // 按 Id 取每池最新一条
      const sql =
        "SELECT * FROM test_table_test_realtime_water_data WHERE isDel=0 AND lake_name!='' ORDER BY Id DESC";
      const rows = await this.querySql(sql);
      const latest = {};
      rows.forEach((r) => {
        if (!latest[r.lake_name] || r.Id > latest[r.lake_name].Id)
          latest[r.lake_name] = r;
      });
      this.realtime_data = Object.values(latest).sort((a, b) =>
        a.lake_name.localeCompare(b.lake_name)
      );
      if (this.realtime_data.length && !this.active_pool)
        this.active_pool = this.realtime_data[0].lake_name;
    },
    /** 遗留：历史水质 SQL */
    async getHistoryData() {
      const sql =
        "SELECT * FROM test_table_test_history_water_data WHERE isDel=0 AND lake_name!='' ORDER BY created_time ASC";
      this.history_data = await this.querySql(sql);
    },
    /** 遗留：报警 SQL */
    async getWarningData() {
      const sql =
        "SELECT * FROM test_table_test_warning_data WHERE isDel=0 ORDER BY Id DESC";
      this.warning_data = await this.querySql(sql);
      this.syncWarningsToMission();
    },
    /** AI 水质报警 → 任务与计划 */
    syncWarningsToMission() {
      const list = (this.warning_data || [])
        .filter(w => String(w.warning_status) !== '-1')
        .map((w, idx) => ({
          id: w.Id || w.id || ('WQ-' + idx + '-' + (w.warning_lake || '') + '-' + (w.warning_data || '')),
          title: (w.warning_lake || '鱼池') + ' ' + (w.warning_data || '水质') +
            (w.warning_data_type === '1' ? '偏高' : '偏低'),
          message: '当前值 ' + (w.warning_value || '') + (w.warning_unit || ''),
          priority: String(w.warning_status) === '1' ? 'urgent' : 'priority',
          category: 'water',
          poolCode: w.warning_lake || '',
          currentValue: w.warning_value || '',
          unit: w.warning_unit || '',
          alarmStatus: 'open'
        }));
      if (!list.length) return;
      syncModuleAiAlarmsToMission(list, {
        prefix: 'WQ-AI-',
        source: 'water_quality',
        category: 'water'
      }).catch(e => console.warn('[水质] 报警同步任务', e));
    },
    onFilterChange() {
      // 车间变更后，系统/鱼池下拉需落在合法子集内
      const systems = this.system_list || [];
      if (this.filter_system && !systems.includes(this.filter_system)) {
        this.filter_system = '';
      }
      const pools = this.pool_list || [];
      if (this.filter_pool && !pools.some((p) => normalizeLakeName(p) === normalizeLakeName(this.filter_pool))) {
        this.filter_pool = '';
      }
      if (this.filter_pool) {
        const hit = (this.realtime_data || []).find(
          (r) => normalizeLakeName(r.lake_name) === normalizeLakeName(this.filter_pool)
        );
        this.active_pool = hit ? hit.lake_name : this.filter_pool;
      } else {
        // 全厂 / 全部鱼池：默认展示第一行，保证指标卡有值
        const visible = this.filteredRealtime || [];
        if (visible.length) {
          if (!this.active_pool || !visible.some((r) => r.lake_name === this.active_pool)) {
            this.active_pool = visible[0].lake_name;
          }
        } else if (!(this.realtime_data && this.realtime_data.length) && !this.dataLoading) {
          // 列表仍空时主动补拉，避免“不筛就没数据”
          this.dataLoading = true;
          Promise.resolve(this.loadAllData(true))
            .catch(() => null)
            .finally(() => {
              if (!this._isDestroyed) this.dataLoading = false;
            });
        }
      }
      this.updateChart();
      this.loadInspectionReportFromDb();
    },
    selectPool(name) {
      if (this.active_pool === name) {
        this.clearPoolSelection();
        return;
      }
      this.active_pool = name;
      this.updateChart();
    },
    clearPoolSelection() {
      this.active_pool = '';
      this.filter_pool = '';
      this.updateChart();
    },
    poolStatus(row) {
      if (!row || !row.lake_name) return "正常";
      const cfg = this.waterThresholdConfig;
      if (cfg) {
        const checks = [
          evaluateWaterStatus(cfg, 'nh3', parseFloat(row.lake_an)),
          evaluateWaterStatus(cfg, 'doOxygen', parseFloat(row.lake_do)),
          evaluateWaterStatus(cfg, 'no3', parseFloat(row.lake_nirate))
        ];
        if (checks.includes('critical')) return "超标";
        if (checks.includes('warn')) return "预警";
        return "正常";
      }
      const an = parseFloat(row.lake_an) || 0;
      const do_ = parseFloat(row.lake_do) || 99;
      const nitrate = parseFloat(row.lake_nirate) || 0;
      if (an > 0.5 || do_ < 4 || nitrate > 35) return "超标";
      if (an > 0.3 || do_ < 5.5 || nitrate > 30) return "预警";
      return "正常";
    },
    /** 超标池触发全局联动（阈值 association） */
    async pushWaterAssociationIfNeeded() {
      try {
        const bad = (this.filteredRealtime || []).filter((r) => this.poolStatus(r) === "超标");
        if (!bad.length) return;
        const sample = bad[0];
        const pondNo = sample.lake_name || '';
        await evaluateAssociationAndPush({
          category: 'water',
          pondDetailNum: pondNo,
          pondCode: pondNo,
          pondName: pondNo,
          nh3: parseFloat(sample.lake_an),
          doValue: parseFloat(sample.lake_do),
          title: pondNo + ' 水质超标（氨氮/溶氧/硝酸盐）'
        });
      } catch (e) {
        console.warn('[waterQuality] association push', e);
      }
    },
    /** 按单指标 range 判断是否异常（卡片与表格共用） */
    metricStatus(key) {
      return metricStatusByKey(this.METRICS, this.cardPool, key);
    },
    /** 点击关键指标卡片 → 定位到底部历史趋势并聚焦该指标 */
    goMetricTrend(key) {
      this.activeChartMetrics = [key];
      this.$nextTick(() => {
        this.updateChart();
        const el = this.$el && this.$el.querySelector(".bottom-chart-panel");
        if (el && typeof el.scrollIntoView === "function") {
          el.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      });
    },
    statusClass(status) {
      if (status === "预警") return "status-warn";
      if (status === "超标") return "status-danger";
      return "status-normal";
    },
    cellStyle(row, key) {
      return metricCellStyle(this.METRICS, row, key);
    },
    alarmStatusText(s) {
      if (s === "1") return "严重";
      if (s === "0") return "预警";
      if (s === "-1") return "已处理";
      return "预警";
    },
    alarmTagClass(s) {
      if (s === "-1") return "tag-done";
      if (s === "1") return "tag-warn";
      return "tag-warn";
    },
    formatAlarmTime(a) {
      if (a.createdTime && a.createdTime.hours !== undefined) {
        const p = (n) => String(n).padStart(2, "0");
        return `${p(a.createdTime.hours)}:${p(a.createdTime.minutes)}`;
      }
      return "";
    },
    toggleChartMetric(key) {
      const idx = this.activeChartMetrics.indexOf(key);
      if (idx >= 0) {
        if (this.activeChartMetrics.length > 1)
          this.activeChartMetrics.splice(idx, 1);
      } else {
        this.activeChartMetrics.push(key);
      }
      this.updateChart();
    },
    setChartRange(r) {
      this.chartRange = r;
      this.updateChart();
    },
    async fillMentougouHistoryChart() {
      if (this._histFillInflight) return;
      this._histFillInflight = true;
      try {
        const rows = await fetchMentougouHistoryForChart(168);
        if (this._isDestroyed) return;
        if (rows && rows.length > (this.history_data || []).length) {
          this.history_data = rows;
          this.updateChart();
        }
      } catch (e) {
        console.warn('[waterQuality] mentougou history fill', e && (e.message || e));
      } finally {
        this._histFillInflight = false;
      }
    },
    updateChart() {
      if (!this.myChart) return;
      const pool =
        this.active_pool ||
        (this.filteredRealtime[0] && this.filteredRealtime[0].lake_name);
      let rows = (this.history_data || []).filter((r) => r.lake_name === pool);
      // 池名对不上时退回该池名列表中第一条有历史的点，避免整图空白
      if (!rows.length && (this.history_data || []).length) {
        const firstName = this.history_data[0].lake_name;
        rows = this.history_data.filter((r) => r.lake_name === firstName);
      }
      const now = Date.now();
      const hours =
        this.chartRange === "24h" ? 24 : this.chartRange === "3d" ? 72 : 168;
      const cutoff = now - hours * 3600000;
      rows = rows.filter((r) => {
        const raw = String(r.created_time || '').trim()
        if (/^\d{1,2}:\d{2}(:\d{2})?$/.test(raw)) return true
        const t = new Date(raw.replace(/-/g, '/')).getTime()
        if (isNaN(t)) return true
        return t >= cutoff
      });
      const labels = rows.map((r) => {
        const raw = String(r.created_time || '').trim()
        if (/^\d{1,2}:\d{2}/.test(raw)) return raw.slice(0, 5)
        const d = new Date(raw.replace(/-/g, '/'));
        if (isNaN(d.getTime())) return raw
        if (this.chartRange === '24h') {
          return `${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
        }
        return `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:00`;
      });
      const series = this.activeChartMetrics.map((key) => {
        const m = this.METRICS.find((x) => x.key === key);
        return {
          name: m.label,
          type: "line",
          smooth: true,
          color: m.color,
          data: rows.map((r) => {
            const v = parseFloat(r[key]);
            return Number.isFinite(v) ? v : null;
          }),
        };
      });
      const tightChart = this.layoutTight || this.layoutCompact;
      this.myChart.setOption(
        {
          // 白色悬浮框：挂到 body，整体顶到图表上方空隙（中间 3D 下方红圈那块）
          tooltip: {
            trigger: "axis",
            appendToBody: true,
            confine: false,
            renderMode: "html",
            backgroundColor: "rgba(255, 255, 255, 0.96)",
            borderColor: "#e5e7eb",
            borderWidth: 1,
            textStyle: { color: "#334155", fontSize: 12 },
            extraCssText:
              "box-shadow:0 4px 14px rgba(0,0,0,0.12);border-radius:6px;pointer-events:none;z-index:999999;",
            position: (point, _params, dom, _rect, size) => {
              const tw =
                (size && size.contentSize && size.contentSize[0]) ||
                (dom && dom.offsetWidth) ||
                150;
              const th =
                (size && size.contentSize && size.contentSize[1]) ||
                (dom && dom.offsetHeight) ||
                180;
              const vw = (size && size.viewSize && size.viewSize[0]) || 0;
              let x = point[0] + 16;
              if (vw && x + tw > vw - 4) x = Math.max(4, point[0] - tw - 16);
              if (x < 4) x = 4;
              // 负坐标：浮到图表面板上沿以上的空隙（用户圈出的区域）
              const y = -th + 20;
              return [x, y];
            }
          },
          // 指标切换用上方 tab，图内 legend 容易顶到按钮，仍关闭
          legend: { show: false },
          grid: tightChart
            ? { left: 36, right: 10, top: 8, bottom: 16 }
            : { left: 48, right: 16, top: 16, bottom: 28 },
          backgroundColor: "transparent",
          xAxis: {
            type: "category",
            data: labels,
            axisLabel: { fontSize: 10, color: "#8bb4cc" },
            axisLine: { lineStyle: { color: "#2a5a80" } },
            splitLine: { show: false },
          },
          yAxis: {
            type: "value",
            axisLabel: { fontSize: 10, color: "#8bb4cc" },
            axisLine: { show: false },
            splitLine: { lineStyle: { color: "rgba(50, 90, 120, 0.35)" } },
            scale: true,
          },
          series,
        },
        true
      );
    },
  },
};
</script>

<style lang="scss" scoped>
$material-symbols-font-path: "~material-symbols/";
@import "material-symbols/sharp";

.header {
  background: linear-gradient(130deg, #091a30 0%, #09293e 100%);
  border-bottom: 1px solid rgba(59, 130, 246, 0.3);
  padding: 12px 16px;
  height: auto;
  min-height: 72px;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;

  .header-bar {
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    align-items: center;
    gap: 1rem;
    min-width: 0;
    width: 100%;
  }

  .header_bar {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    .page-title {
      font-size: 22px;
      font-weight: 700;
      color: #fff;
      letter-spacing: 0.04em;
    }
    .time-text {
      font-size: 12px;
      color: #7eb8d8;
      font-variant-numeric: tabular-nums;
    }
    .data-asof {
      font-size: 11px;
      color: #5a9bb8;
      font-variant-numeric: tabular-nums;
      letter-spacing: 0.02em;
    }
    .data-asof--stale {
      color: #e8a45a;
      opacity: 0.95;
    }
  }

  .header-left {
    justify-self: start;
    display: flex;
    align-items: center;
    gap: 0.6rem;
    min-width: 0;

    .nav-btn {
      display: flex;
      align-items: center;
      gap: 0.45rem;
      padding: 0 0.9rem;
      height: 2.5rem;
      border-radius: 0.5rem;
      color: white;
      font-weight: 600;
      font-size: 0.94rem;
      cursor: pointer;
      border: none;
      transition: all 0.3s;
      white-space: nowrap;
      flex-shrink: 0;
      .icon { width: 0.9rem; height: 0.9rem; }
      &.btn-blue {
        background: linear-gradient(to right, #2563eb, #3b82f6);
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
        &:hover { filter: brightness(1.08); transform: translateY(-1px); }
      }
      &.btn-purple {
        background: linear-gradient(to right, #9333ea, #a855f7);
        box-shadow: 0 4px 12px rgba(168, 85, 247, 0.35);
        &:hover { filter: brightness(1.08); transform: translateY(-1px); }
      }
      &.btn-cyan {
        background: linear-gradient(to right, #0891b2, #06b6d4);
        box-shadow: 0 4px 12px rgba(6, 182, 212, 0.35);
        &:hover { filter: brightness(1.08); transform: translateY(-1px); }
      }
    }
  }

  .header-right {
    justify-self: end;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    min-width: 0;
    .quick-btns {
      display: flex;
      flex-wrap: wrap;
      justify-content: flex-end;
      gap: 0.6rem;
      .q-btn {
        height: 2.5rem;
        padding: 0 0.9rem;
        border-radius: 0.5rem;
        display: flex;
        align-items: center;
        gap: 0.45rem;
        color: white;
        font-size: 0.94rem;
        font-weight: 600;
        border: none;
        cursor: pointer;
        transition: all 0.3s;
        white-space: nowrap;
        flex-shrink: 0;
        .icon { width: 0.9rem; height: 0.9rem; }
        &.btn-red { background: linear-gradient(to right, #dc2626, #ef4444); box-shadow: 0 4px 10px rgba(239, 68, 68, 0.28); }
        &.btn-emerald { background: linear-gradient(to right, #059669, #10b981); box-shadow: 0 4px 10px rgba(16, 185, 129, 0.25); }
        &.btn-orange { background: linear-gradient(to right, #ea580c, #f97316); box-shadow: 0 4px 10px rgba(249, 115, 22, 0.25); }
        &.btn-cyan-light { background: linear-gradient(to right, #0891b2, #06b6d4); box-shadow: 0 4px 10px rgba(6, 182, 212, 0.25); }
        &.btn-pink { background: linear-gradient(to right, #db2777, #ec4899); box-shadow: 0 4px 10px rgba(236, 72, 153, 0.25); }
        &.btn-indigo { background: linear-gradient(to right, #4f46e5, #6366f1); box-shadow: 0 4px 10px rgba(99, 102, 241, 0.25); }
        &:hover { filter: brightness(1.08); transform: translateY(-1px); }
      }
    }
  }
}

.waterquality {
  position: relative;
  color: #e6f7ff;
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  max-height: 100%;
  min-height: 0;
  overflow: hidden;
  background:
    radial-gradient(ellipse 80% 50% at 20% -10%, rgba(14, 90, 130, 0.35), transparent 55%),
    radial-gradient(ellipse 60% 40% at 90% 10%, rgba(8, 70, 90, 0.28), transparent 50%),
    linear-gradient(165deg, #07131f 0%, #0a1c30 45%, #081624 100%);
  box-sizing: border-box;
  * { box-sizing: border-box; }
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 18px;
  flex-shrink: 0;
  border-bottom: 1px solid rgba(40, 90, 130, 0.45);
  background: rgba(8, 24, 40, 0.55);
  .filter-label {
    font-size: 12px;
    color: #7aa8c4;
    letter-spacing: 0.08em;
  }
  .filter-loading-tip {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #7dd3fc;
  }
  select:disabled {
    opacity: 0.65;
    cursor: wait;
  }
  .select-wrap {
    display: flex;
    align-items: center;
    gap: 8px;
    label { font-size: 13px; color: #9ec8e0; }
    select {
      appearance: none;
      -webkit-appearance: none;
      background: rgba(10, 36, 56, 0.9) url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath fill='%237eb8d8' d='M6 8L0 0h12z'/%3E%3C/svg%3E") no-repeat right 10px center;
      border: 1px solid #2a6088;
      color: #e8f6ff;
      padding: 7px 28px 7px 12px;
      border-radius: 6px;
      font-size: 13px;
      min-width: 120px;
      cursor: pointer;
      &:focus { outline: none; border-color: #48d1ff; }
    }
    .workshop { border-color: #3a9ec8; }
    .system { border-color: #3ab88a; }
    .pool { border-color: #c9a43a; }
  }
  .filter-hint {
    margin-left: auto;
    font-size: 12px;
    color: #7dd3fc;
    background: rgba(14, 80, 120, 0.35);
    padding: 5px 10px;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }
  .filter-clear-btn {
    border: 1px solid rgba(125, 211, 252, 0.45);
    background: rgba(8, 40, 68, 0.55);
    color: #e0f2fe;
    border-radius: 999px;
    font-size: 11px;
    padding: 2px 8px;
    cursor: pointer;
  }
  .filter-clear-btn:hover {
    border-color: #7dd3fc;
    color: #fff;
  }
}

.content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px 16px 16px;
  min-height: 0;
  flex: 1;
  overflow: hidden;
}

.card-row-top {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 8px;
  flex-shrink: 0;
}
@media (max-width: 1280px) {
  .card-row-top { grid-template-columns: repeat(5, minmax(0, 1fr)); }
}
.index-card {
  --color: #48d1ff;
  background: linear-gradient(160deg, rgba(12, 32, 52, 0.95) 0%, rgba(16, 40, 62, 0.85) 100%);
  border: 1px solid rgba(60, 120, 160, 0.35);
  border-top: 2px solid var(--color);
  border-radius: 10px;
  padding: 10px 12px;
  cursor: pointer;
  transition: transform 0.15s ease, border-color 0.15s ease, box-shadow 0.15s ease;
  &:hover {
    transform: translateY(-1px);
    border-color: var(--color);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
  }
}
.card-ph { --color: #36e088; }
.card-do { --color: #36b8ff; }
.card-temp { --color: #ffd240; }
.card-turb { --color: #8cf0b8; }
.card-orp { --color: #63d2ff; }
.card-alk { --color: #ff9f40; }
.card-hard { --color: #ff6b6b; }
.card-nitrate { --color: #ff88cc; }
.card-ammonia { --color: #ff7a7a; }
.card-co2 { --color: #c39eff; }
.card-tss { --color: #b0c4de; }
.card-tp { --color: #40e0d0; }
.card-tn { --color: #d8dde6; }
.card-top { display: flex; align-items: center; gap: 6px; margin-bottom: 6px; }
.card-icon { font-size: 16px !important; color: var(--color); }
.card-label { font-size: 12px; color: #9bb8cc; }
.card-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--color);
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
  small { font-size: 11px; font-weight: 500; opacity: 0.75; margin-left: 2px; }
}
.card-range { margin-top: 4px; font-size: 10px; color: #6f8fa8; }
.index-card.card-alert {
  border-color: #ff4d4f !important;
  box-shadow: 0 0 0 1px rgba(255, 77, 79, 0.35), 0 0 16px rgba(255, 59, 59, 0.25);
  animation: card-alert-pulse 1.8s ease-in-out infinite;
}
@keyframes card-alert-pulse {
  0%, 100% { box-shadow: 0 0 0 1px rgba(255, 59, 59, 0.35), 0 0 10px rgba(255, 59, 59, 0.2); }
  50% { box-shadow: 0 0 0 2px rgba(255, 80, 80, 0.55), 0 0 18px rgba(255, 59, 59, 0.4); }
}

.main-row {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(220px, 0.55fr) minmax(260px, 0.7fr);
  gap: 12px;
  /* 中间三栏优先吃高度，底部图表用 max-height 让位 */
  flex: 1 1 0;
  min-height: 240px;
  min-width: 0;
}

$panel-padding: 12px;
.panel {
  background: rgba(10, 28, 48, 0.72);
  border: 1px solid rgba(40, 100, 140, 0.4);
  border-radius: 12px;
  padding: $panel-padding;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  backdrop-filter: blur(6px);
  min-height: 0;
  /* 防止表格内容把网格列撑开、挤没中间 3D（样式外观不变） */
  min-width: 0;
}
.panel-table { min-height: 0; min-width: 0; }
.warning-panel {
  min-height: 0;
  border-color: rgba(245, 158, 11, 0.35);
}
.tank-panel { min-height: 0; min-width: 0; }
.panel-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  margin: (-$panel-padding) (-$panel-padding) 10px;
  padding: 10px 12px;
  border-bottom: 1px solid rgba(40, 100, 140, 0.35);
  background: linear-gradient(90deg, rgba(12, 40, 62, 0.95), rgba(10, 28, 48, 0.4));
  .title-text {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 700;
    color: #7dd3fc;
    .material-symbols-sharp { font-size: 16px; }
  }
  &.warn-title .title-text { color: #fbbf24; }
  .title-meta { display: flex; gap: 6px; align-items: center; }
}
.link-all {
  margin-left: auto;
  background: transparent;
  border: 0;
  color: #5ecbff;
  cursor: pointer;
  font-size: 12px;
  padding: 0;
  flex-shrink: 0;
  &:hover { color: #a5e4ff; text-decoration: underline; }
}
.tag {
  font-size: 11px;
  padding: 3px 8px;
  color: #6ee7b7;
  border-radius: 999px;
  font-weight: 600;
  background: rgba(6, 78, 59, 0.55);
}
.demo-tag { color: #fde68a; background: rgba(120, 80, 10, 0.55); }
.pool-chip {
  font-size: 12px;
  color: #bae6fd;
  background: rgba(14, 80, 120, 0.4);
  padding: 3px 10px;
  border-radius: 999px;
  max-width: 55%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.panel-content {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.panel-scroll {
  flex: 1;
  overflow: auto;
  scrollbar-width: thin;
  scrollbar-color: #2a6a90 transparent;
  min-width: 0;
}

.water-table {
  /* 列多时横向滚动，而不是把网格第一列撑开盖住 3D */
  width: max-content;
  min-width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 12px;
  th {
    color: #8ecae6;
    padding: 8px 6px;
    text-align: center;
    font-weight: 600;
    border-bottom: 1px solid rgba(50, 110, 150, 0.45);
    position: sticky;
    top: 0;
    z-index: 1;
    background: rgba(8, 28, 46, 0.96);
    white-space: nowrap;
  }
  td {
    padding: 7px 6px;
    text-align: center;
    border-bottom: 1px solid rgba(30, 60, 90, 0.55);
    color: #d7ebf7;
    font-variant-numeric: tabular-nums;
    white-space: nowrap;
  }
  .td-name { text-align: left; padding-left: 10px; color: #eaf6ff; white-space: nowrap; }
  .td-empty { opacity: 0.85; padding: 28px 12px !important; text-align: center; white-space: normal; }
  .empty-manage {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
  }
  .empty-manage-btn {
    border: 1px solid rgba(56, 189, 248, 0.55);
    background: rgba(14, 165, 233, 0.18);
    color: #7dd3fc;
    border-radius: 6px;
    padding: 6px 14px;
    cursor: pointer;
    font-size: 13px;
  }
  .empty-manage-btn:hover {
    background: rgba(14, 165, 233, 0.32);
    color: #e0f2fe;
  }
  tr.clickable { cursor: pointer; transition: background 0.12s; }
  tr.clickable:hover td { background: rgba(56, 140, 190, 0.12); }
  tr.row-active td { background: rgba(14, 120, 180, 0.18); }
}
.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
}
.status-normal { background: rgba(6, 95, 70, 0.7); color: #a7f3d0; }
.status-warn { background: rgba(120, 80, 0, 0.7); color: #fde68a; }
.status-danger { background: rgba(120, 30, 30, 0.75); color: #fecaca; }

.tank-stage {
  align-items: stretch;
  justify-content: center;
  min-height: 0;
  padding: 4px 0 0;
}
.tank-3d-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  min-height: 0;
}
.tank-scene {
  perspective: 600px;
  width: 100%;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tank-cube {
  position: relative;
  width: 168px;
  height: 168px;
  background: linear-gradient(145deg, rgba(0, 150, 220, 0.28), rgba(0, 70, 130, 0.45));
  border: 2px solid rgba(34, 184, 255, 0.7);
  border-radius: 6px;
  transform: rotateX(22deg) rotateY(32deg);
  box-shadow:
    18px 14px 0 -2px rgba(10, 50, 90, 0.55),
    18px 14px 0 0 rgba(40, 140, 200, 0.35),
    0 18px 36px rgba(0, 0, 0, 0.4),
    inset 0 0 40px rgba(80, 200, 255, 0.12);
  overflow: hidden;
  animation: tank-idle 5.5s ease-in-out infinite;
  &.warn {
    border-color: #ffaa22;
    background: linear-gradient(130deg, #ffaa2233, #aa660044);
    box-shadow:
      18px 14px 0 -2px rgba(90, 55, 10, 0.55),
      18px 14px 0 0 rgba(255, 170, 34, 0.35),
      0 18px 36px rgba(0, 0, 0, 0.4);
  }
  &.danger {
    border-color: #ff4444;
    background: linear-gradient(130deg, #ff444433, #aa222244);
    animation: tank-idle 5.5s ease-in-out infinite, blink 1.5s infinite;
    box-shadow:
      18px 14px 0 -2px rgba(90, 20, 20, 0.55),
      18px 14px 0 0 rgba(255, 68, 68, 0.4),
      0 18px 36px rgba(0, 0, 0, 0.4);
  }
}
@keyframes tank-idle {
  0%, 100% { transform: rotateX(22deg) rotateY(32deg) translateY(0); }
  50% { transform: rotateX(20deg) rotateY(28deg) translateY(-5px); }
}
.tank-fill {
  position: absolute;
  left: 0; right: 0; bottom: 0;
  background: linear-gradient(180deg, rgba(56, 189, 248, 0.55), rgba(12, 90, 150, 0.88));
  transition: height 0.55s ease;
}
.tank-surface {
  position: absolute;
  top: -2px; left: -10%;
  width: 120%;
  height: 10px;
  border-radius: 50%;
  background: rgba(200, 240, 255, 0.45);
  animation: wave 2.8s ease-in-out infinite;
}
@keyframes wave {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(6%); }
}
.tank-bubbles {
  position: absolute;
  inset: 0;
  span {
    position: absolute;
    bottom: 10%;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: rgba(220, 245, 255, 0.6);
    animation: bubble-up 2.6s ease-in infinite;
    &:nth-child(1) { left: 24%; }
    &:nth-child(2) { left: 50%; width: 4px; height: 4px; animation-delay: 0.6s; }
    &:nth-child(3) { left: 70%; animation-delay: 1.2s; }
  }
}
@keyframes bubble-up {
  0% { transform: translateY(0); opacity: 0.75; }
  100% { transform: translateY(-100px); opacity: 0; }
}
.tank-gloss {
  position: absolute;
  inset: 0;
  background: linear-gradient(115deg, rgba(255,255,255,0.18), transparent 38%, transparent 62%, rgba(255,255,255,0.06));
  pointer-events: none;
}
.tank-footer {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  font-size: 12px;
  span {
    padding: 4px 9px;
    background: rgba(12, 40, 62, 0.9);
    border: 1px solid rgba(50, 110, 150, 0.45);
    border-radius: 6px;
    color: #d7eefc;
  }
  .tank-status {
    font-weight: 700;
    &.ok { color: #6ee7b7; border-color: rgba(16, 185, 129, 0.45); }
    &.warn { color: #fbbf24; border-color: rgba(245, 158, 11, 0.5); }
    &.danger { color: #fca5a5; border-color: rgba(239, 68, 68, 0.5); }
  }
}
.tank-empty, .empty-hint {
  font-size: 12px;
  color: #7a9bb0;
  padding: 16px;
  text-align: center;
}
.wq-spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2px solid rgba(56, 189, 248, 0.25);
  border-top-color: #38bdf8;
  border-radius: 50%;
  animation: wq-spin 0.75s linear infinite;
  vertical-align: middle;
}
.wq-spinner-lg {
  width: 28px;
  height: 28px;
  border-width: 3px;
}
@keyframes wq-spin {
  to { transform: rotate(360deg); }
}
.wq-loading-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  min-height: 96px;
  padding: 20px 12px;
  color: #9ec8e0;
  font-size: 13px;
}
.wq-loading-block.compact {
  min-height: 64px;
  padding: 12px;
}
.inline-loading {
  display: inline-flex;
  align-items: center;
  height: 1.2em;
}
.wq-loading-block.chart-loading {
  position: absolute;
  inset: 0;
  z-index: 2;
  background: rgba(6, 22, 38, 0.72);
  border-radius: 8px;
}
.bottom-chart-wrap.is-dimmed {
  opacity: 0.2;
  pointer-events: none;
}
@keyframes blink { 50% { opacity: 0.72; } }

.alarm-item {
  padding: 10px;
  border-bottom: 1px solid rgba(40, 80, 110, 0.45);
  font-size: 12px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 4px;
  &:hover { filter: brightness(1.08); }
}
.alarm-warn { border-left: 3px solid #f59e0b; background: rgba(120, 70, 0, 0.18); }
.alarm-done { border-left: 3px solid #10b981; background: rgba(0, 90, 60, 0.14); }
.alarm-top { display: flex; justify-content: space-between; gap: 8px; margin-bottom: 4px; align-items: flex-start; }
.alarm-main { display: flex; align-items: flex-start; gap: 4px; min-width: 0; }
.alarm-text { color: #e8f2fa; line-height: 1.35; }
.alarm-icon { font-size: 16px !important; margin-top: 1px; }
.color-w { color: #f59e0b; }
.color-c { color: #34d399; }
.alarm-val { color: #b7d4e8; margin-bottom: 2px; }
.alarm-time { opacity: 0.55; font-size: 10px; }
.alarm-tag { font-size: 10px; padding: 2px 6px; border-radius: 999px; flex-shrink: 0; }
.tag-warn { background: #704c00; color: #ffdd99; }
.tag-done { background: #085932; color: #8ef1c2; }

.bottom-row {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(280px, 0.72fr);
  gap: 12px;
  flex: 0 0 auto;
  /* 给图表区留足可视高度，避免被中间区 / 高 DPI 压缩到「看不见」 */
  min-height: 200px;
  height: clamp(200px, 26vh, 280px);
  max-height: min(280px, 34vh);
}
.bottom-chart-panel,
.report-panel { min-height: 0; height: 100%; overflow: hidden; }
.chart-head {
  flex-wrap: nowrap;
  row-gap: 0;
  align-items: center;
  position: relative;
  z-index: 3;
}
.chart-controls {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 3;
}
.chart-tab-bar {
  display: flex;
  flex-wrap: nowrap;
  gap: 4px;
  min-width: 0;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: thin;
  scrollbar-color: #2a6a90 transparent;
  -webkit-overflow-scrolling: touch;
  padding-bottom: 2px;
  position: relative;
  z-index: 3;
  button {
    background: rgba(18, 48, 78, 0.9);
    border: 1px solid #2a5a80;
    color: #b8e0ff;
    padding: 3px 8px;
    border-radius: 999px;
    font-size: 11px;
    cursor: pointer;
    flex-shrink: 0;
    white-space: nowrap;
    &.active { background: rgba(14, 90, 140, 0.95); border-color: #38bdf8; color: #fff; }
  }
}
.time-selector {
  display: inline-flex;
  border: 1px solid #2a5a80;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  button {
    padding: 4px 10px;
    font-size: 12px;
    color: #cfe8f8;
    background: transparent;
    border: none;
    border-left: 1px solid #2a5a80;
    cursor: pointer;
    white-space: nowrap;
    &:first-child { border-left: none; }
    &.active { background: #0b5f78; color: #fff; }
  }
}
.bottom-chart-content { position: relative; z-index: 1; min-height: 0; flex: 1; overflow: hidden; }
.bottom-chart-wrap { width: 100%; height: 100%; min-height: 120px; position: relative; z-index: 1; }

.auto-generation {
  font-size: 11px;
  color: #7dd3fc;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(10, 63, 83, 0.7);
  border: 1px solid rgba(56, 189, 248, 0.35);
  cursor: pointer;
  &:hover { filter: brightness(1.1); color: #fff; }
  &:disabled { opacity: 0.6; cursor: wait; }
}
.report-empty {
  font-size: 12px;
  color: #8bb4cc;
  line-height: 1.5;
  padding: 12px 4px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}
.report-link-btn {
  border: 1px solid rgba(56, 189, 248, 0.55);
  background: rgba(14, 165, 233, 0.18);
  color: #7dd3fc;
  border-radius: 6px;
  padding: 4px 10px;
  cursor: pointer;
  font-size: 12px;
  &:hover { background: rgba(14, 165, 233, 0.32); color: #e0f2fe; }
}
.report-score-row { display: flex; align-items: baseline; gap: 10px; margin: 4px 0 8px; }
.report-score { font-size: 32px; color: #34d399; font-weight: 800; line-height: 1; }
.report-level { font-size: 13px; color: #a7f3d0; }
.report-summary { font-size: 12px; color: #9bb8cc; margin-bottom: 10px; line-height: 1.45; }
.report-section {
  margin: 10px 0;
  font-size: 12px;
  h4 { font-size: 12px; margin-bottom: 6px; color: #8ecae6; font-weight: 700; }
}
.report-item {
  margin: 5px 0;
  padding-left: 14px;
  position: relative;
  color: #d5e8f4;
  line-height: 1.4;
  &.warn::before { content: "▲"; color: #fbbf24; position: absolute; left: 0; font-size: 10px; }
  &.ok::before { content: "✓"; color: #34d399; position: absolute; left: 0; }
}
.report-suggest { margin-top: 8px; padding-top: 8px; border-top: 1px dashed rgba(50, 90, 120, 0.6); }
.report-footer { margin-top: 10px; font-size: 10px; opacity: 0.5; text-align: right; }

/* 矮屏 / 高 DPI：略压底部，但仍保证图表 / 报告可读 */
.waterquality.layout-compact {
  .content { gap: 8px; padding: 8px 12px 10px; }
  .card-row-top { gap: 6px; }
  .index-card { padding: 6px 8px; }
  .card-value { font-size: 15px; }
  .main-row { min-height: 220px; }
  .bottom-row {
    height: clamp(180px, 22vh, 220px);
    max-height: min(220px, 28vh);
    min-height: 180px;
  }
  .bottom-chart-wrap { min-height: 100px; }
  .chart-head { padding-top: 6px; padding-bottom: 6px; }
  .chart-tab-bar button { padding: 2px 6px; font-size: 10px; }
  .time-selector button { padding: 2px 8px; font-size: 11px; }
  .report-score { font-size: 22px; }
  .report-summary { margin-bottom: 6px; font-size: 11px; }
}

/* 更极端：仍保住底部图表最低可视高度 */
.waterquality.layout-tight {
  .content { gap: 6px; padding: 6px 10px 8px; }
  .main-row { min-height: 200px; gap: 8px; }
  .bottom-row {
    height: clamp(160px, 20vh, 200px);
    max-height: min(200px, 26vh);
    min-height: 160px;
  }
  .bottom-chart-wrap { min-height: 88px; }
  .panel-title { margin-bottom: 6px; padding: 6px 10px; }
  .report-score { font-size: 20px; }
  .report-section { display: none; }
  .report-suggest { display: none; }
  .report-footer { display: none; }
}

@media (max-height: 900px) {
  .bottom-row {
    height: clamp(180px, 24vh, 220px);
    max-height: min(220px, 28vh);
    min-height: 180px;
  }
}

@media (max-height: 760px) {
  .main-row { min-height: 200px; }
  .bottom-row {
    height: clamp(160px, 22vh, 200px);
    max-height: min(200px, 26vh);
    min-height: 160px;
  }
  .bottom-chart-wrap { min-height: 88px; }
}

@media (max-width: 1100px) {
  .main-row,
  .bottom-row { grid-template-columns: 1fr; height: auto; max-height: none; }
  .bottom-row { min-height: 220px; height: auto; }
  .warning-panel { min-height: 200px; }
  .bottom-chart-wrap { min-height: 160px; }
}
</style>
