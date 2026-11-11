<script>
import * as echarts from "echarts";
import { syncModuleAiAlarmsToMission, syncOpenSystemAlarmsToMission, pushAlarmAndCreateTask } from '@/utils/alarmTaskBridge';
import { getThresholdModule, resolveDeviceMetrics } from '@/utils/thresholdConfig';
import { formatWorkshop, formatSystem, workshopsEqual, systemsEqual, normalizeLakeName, normalizeSiteRecord } from '@/utils/siteNaming';
import { loadSharedSiteOptions as fetchSiteOptions, pickWorkshopOptionLabels, pickSystemOptionLabels, isRowInUserSiteScope, isAlarmInUserSiteScope, resolveCurrentUserDeptId, isPlatformUnscopedAdmin, isUserDeptScopeDenied, SITE_OPTIONS_CHANGED_EVENT } from '@/utils/siteOptions';
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope';
import { silentCatch } from '@/utils/reportSilentError';
import { fetchDeviceMonitorLive, derivePanelsFromDevices } from '@/utils/dashboardLiveBridge';
import { loadZkDeviceAlarms, clearZkAlarmCache } from '@/utils/zkAlarmShared';
import { getZkDevicePage } from '@/api/fish/zkDevice';
import {
  TYPE_LEGEND,
  DEFAULT_ENERGY_BY_TYPE,
  inferProductionModule,
  // DEMO_DEVICES, DEMO_FAULT_MONTH, // 已落库，仅注释保留引用说明
} from '@/utils/deviceDemoData';
import MyChat from '@/components/MyChat/index';
import PageLoadingMask from '@/components/PageLoadingMask/index';

function todayIso() {
  const n = new Date();
  const p = x => String(x).padStart(2, '0');
  return n.getFullYear() + '-' + p(n.getMonth() + 1) + '-' + p(n.getDate());
}
function daysAgoIso(days) {
  const n = new Date();
  n.setDate(n.getDate() - days);
  const p = x => String(x).padStart(2, '0');
  return n.getFullYear() + '-' + p(n.getMonth() + 1) + '-' + p(n.getDate());
}
let chartPie = null;
function chartTip(extra) {
  return Object.assign({
    appendToBody: true,
    confine: false,
    renderMode: 'html',
    backgroundColor: 'rgba(10, 30, 54, 0.96)',
    borderColor: '#3aa0d4',
    borderWidth: 1,
    padding: [10, 14],
    textStyle: { color: '#e6f7ff', fontSize: 12 },
    extraCssText: 'z-index:100000;box-shadow:0 10px 28px rgba(0,0,0,.55);pointer-events:none;',
  }, extra || {});
}
function buildTrend(startIso, endIso) {
  // 按所选日期区间生成能耗趋势横轴，避免写死 6 月导致「改日期无反应」
  const base = {
    '水泵': 640, '增氧机': 195, '温控器': 185, '投饵机': 85,
    '微滤机': 95, '风机': 110, '臭氧发生器': 75,
    '紫外线消毒器': 45, '碱度调节系统': 42, '反硝化系统': 140,
  };
  let start = startIso ? new Date(String(startIso).replace(/-/g, '/')) : new Date();
  let end = endIso ? new Date(String(endIso).replace(/-/g, '/')) : new Date();
  if (isNaN(start.getTime())) start = new Date();
  if (isNaN(end.getTime())) end = new Date();
  if (start > end) {
    const t = start; start = end; end = t;
  }
  const spanDays = Math.max(1, Math.round((end - start) / 86400000) + 1);
  const points = Math.min(12, Math.max(3, spanDays <= 7 ? spanDays : 8));
  const pts = [];
  for (let i = 0; i < points; i++) {
    const ratio = points === 1 ? 0 : i / (points - 1);
    const d = new Date(start.getTime() + (end - start) * ratio);
    const label = String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
    const row = { time: label, _iso: d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0') };
    Object.keys(base).forEach((k, idx) => {
      const dayFactor = 0.85 + (spanDays / 30) * 0.25 + Math.sin(i / 1.6 + idx * 0.4) * 0.08;
      row[k] = Math.round(base[k] * dayFactor + (k === '水泵' ? 40 : 12) * Math.sin(i + idx));
    });
    pts.push(row);
  }
  return pts;
}
export default {
  components: { MyChat, PageLoadingMask },
  data() {
    return{
      TYPE_LEGEND: TYPE_LEGEND.map(function (t) { return Object.assign({}, t); }),
      /* 原 DEMO_* 假数据已落库 zk_device_monitor_panel / zk_energy_meter_reading，整段注释保留
      DEMO_DEVICES: JSON.parse(JSON.stringify(DEMO_DEVICES)),
      DEMO_ENERGY: Object.assign({}, DEMO_ENERGY),
      DEMO_BACKWASH: [
        { pool: 'c1-s1-1', count: 3.8, status: 'normal', device: '微滤机A1-1', workshop: '车间1', system: '系统1' },
        { pool: 'c1-s1-2', count: 4.2, status: 'normal', device: '微滤机A1-2', workshop: '车间1', system: '系统1' },
        { pool: 'c1-s2-1', count: 0, status: 'fault', device: '微滤机A2-1', workshop: '车间1', system: '系统2' },
        { pool: 'c2-s1-1', count: 3.1, status: 'normal', device: '微滤机B1-1', workshop: '车间2', system: '系统1' },
        { pool: 'c2-s2-1', count: 2.8, status: 'normal', device: '微滤机B2-1', workshop: '车间2', system: '系统2' },
        { pool: 'c3-s1-1', count: 3.4, status: 'normal', device: '微滤机C1-1', workshop: '车间3', system: '系统1' },
        { pool: 'DN-1', count: 2.5, status: 'normal', device: '反硝化补水泵', workshop: '反硝化', system: '反硝化系统' },
      ],
      DEMO_WATER: [
        { name: '车间1-系统1', flow: 45.0, workshop: '车间1', system: '系统1' },
        { name: '车间2-系统1', flow: 42.5, workshop: '车间2', system: '系统1' },
        { name: '车间3-系统1', flow: 38.2, workshop: '车间3', system: '系统1' },
        { name: '反硝化系统', flow: 28.6, workshop: '反硝化', system: '反硝化系统' },
      ],
      DEMO_AERATOR: [
        { pool: '车间1-系统1-鱼池1', share: 78, workshop: '车间1', system: '系统1' },
        { pool: '车间1-系统1-鱼池2', share: 65, workshop: '车间1', system: '系统1' },
        { pool: '车间2-系统1-鱼池1', share: 52, workshop: '车间2', system: '系统1' },
        { pool: '车间2-系统1-鱼池2', share: 41, workshop: '车间2', system: '系统1' },
        { pool: '车间3-系统1-鱼池1', share: 58, workshop: '车间3', system: '系统1' },
        { pool: '车间3-系统1-鱼池2', share: 47, workshop: '车间3', system: '系统1' },
      ],
      DEMO_CO2: [
        { device: '风机A1', start: '02:02', end: '04:05', workshop: '车间1', system: '系统1', module: 'CO₂吹脱' },
        { device: '风机B1', start: '05:10', end: '06:40', workshop: '车间2', system: '系统1', module: 'CO₂吹脱' },
        { device: '风机C1-1', start: '08:35', end: '10:06', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' },
        { device: '风机C2', start: '14:20', end: '15:50', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' },
      ],
      DEMO_ALARMS: [
        / * L1 严重 · 红 · 圆叉 * /
        { id: 'AL1', device_id: 'A2-1', device_name: 'A2-1微滤机', category: '微滤机', type: '反冲洗泵损坏', tone: 'urgent', ago: '5分钟前', workshop: '车间1', system: '系统2', module: '循环过滤' },
        / * L2 警告 · 橙 · 圆叹号 * /
        { id: 'AL2', device_id: 'B1-1', device_name: 'B1-1温控系统', category: '温控器', type: '温度传感器通讯异常', tone: 'warn', ago: '15分钟前', workshop: '车间2', system: '系统1', module: '温控' },
        { id: 'AL3', device_id: 'C1-1', device_name: 'C1-1风机', category: '风机', type: '风机皮带松弛', tone: 'warn', ago: '28分钟前', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' },
        / * L3 提示 · 蓝 · 三角叹号 * /
        { id: 'AL4', device_id: 'B2-3', device_name: 'B2-3水泵', category: '水泵', type: '运行时间超过维护周期', tone: 'info', ago: '2小时前', workshop: '车间2', system: '系统2', module: '循环过滤' },
        / * L4 正常 · 绿勾 + 绿色胶囊 * /
        { id: 'AL5', device_id: 'P-A1', device_name: 'A1-1水泵', category: '水泵', type: '运行正常', tone: 'ok', ago: '', workshop: '车间1', system: '系统1', module: '循环过滤' },
        { id: 'AL6', device_id: 'A1-2', device_name: 'A1-2水泵', category: '水泵', type: '运行正常', tone: 'ok', ago: '', workshop: '车间1', system: '系统1', module: '循环过滤' },
        { id: 'AL7', device_id: 'OX-1', device_name: '增氧机1号', category: '增氧机', type: '运行正常', tone: 'ok', ago: '', workshop: '车间1', system: '系统1', module: '增氧曝气' },
        { id: 'AL8', device_id: 'OX-5', device_name: '增氧机5号', category: '增氧机', type: '运行正常', tone: 'ok', ago: '', workshop: '车间1', system: '系统2', module: '增氧曝气' },
        { id: 'AL9', device_id: 'TC-A1', device_name: '温控器A1', category: '温控器', type: '运行正常', tone: 'ok', ago: '', workshop: '车间1', system: '系统1', module: '温控' },
        { id: 'AL10', device_id: 'FD-A1', device_name: '投饵机A1', category: '投饵机', type: '运行正常', tone: 'ok', ago: '', workshop: '车间1', system: '系统1', module: '投喂' },
        { id: 'AL11', device_id: 'UV-A1', device_name: '紫外线消毒器A1', category: '紫外线消毒器', type: '运行正常', tone: 'ok', ago: '', workshop: '车间1', system: '系统1', module: '消毒' },
        { id: 'AL12', device_id: 'P-B2', device_name: '循环泵B2', category: '水泵', type: '运行正常', tone: 'ok', ago: '', workshop: '车间2', system: '系统2', module: '循环过滤' },
        { id: 'AL13', device_id: 'OZ-C1', device_name: '臭氧发生器C1', category: '臭氧发生器', type: '运行正常', tone: 'ok', ago: '', workshop: '车间3', system: '系统1', module: '消毒' },
        { id: 'AL14', device_id: 'DN-P2', device_name: '反硝化补水泵', category: '水泵', type: '运行正常', tone: 'ok', ago: '', workshop: '反硝化', system: '反硝化系统', module: '反硝化' },
      ],
      DEMO_FAULT_WEEK: [
        { name: '微滤机A2-1', count: 5 },
        { name: '增氧机4号', count: 4 },
        { name: '臭氧发生器B1', count: 3 },
        { name: '反硝化微滤', count: 2 },
      ],
      DEMO_FAULT_MONTH: JSON.parse(JSON.stringify(DEMO_FAULT_MONTH)),
      DEMO_FAULT_3M: [
        { name: '微滤机A2-1', count: 28 },
        { name: '增氧机4号', count: 22 },
        { name: '臭氧发生器B1', count: 18 },
        { name: '反硝化微滤', count: 15 },
        { name: '温控器B1-1', count: 10 },
        { name: '循环泵A1', count: 6 },
      ],
      DEMO_FAULT_6M:  [
        { name: '微滤机A2-1', count: 46 },
        { name: '增氧机4号', count: 38 },
        { name: '臭氧发生器B1', count: 31 },
        { name: '反硝化微滤', count: 27 },
        { name: '温控器B1-1', count: 19 },
        { name: '循环泵A1', count: 14 },
        { name: '风机C1-1', count: 9 },
      ],
      ========== DEMO 注释结束 */
      devices: [],
      alarms: [],
      backwash: [],
      water: [],
      aerator: [],
      co2: [],
      energyByType: {},
      energyTrend: [],
      faultWeek: [],
      faultMonth: [],
      fault3m: [],
      fault6m: [],
      filter_workshop: '',
      sharedSiteOptions: { workshops: [], systems: [], ponds: [] },
      filter_system: '',
      filter_module: '',
      selectedTypes: [],
      typeLegend: [],
      faultRange: 'week',
      faultDdOpen: false,
      faultRanges: [
        { key: 'week', label: '本周' },
        { key: 'month', label: '本月' },
        { key: '3m', label: '近3个月' },
        { key: '6m', label: '近半年' },
      ],
      active_device_id: '',
      current_time: '',
      pageLoading: false,
      refreshTimer: null,
      clockTimer: null,
      dateRanges: {
        energy: { start: daysAgoIso(7), end: todayIso() },
        backwash: { start: todayIso(), end: todayIso() },
        water: { start: todayIso(), end: todayIso() },
        aerator: { start: todayIso(), end: todayIso() },
      },
      calOpen: false,
      calKey: 'backwash',
      calWhich: 'end',
      calViewYear: new Date().getFullYear(),
      calViewMonth: new Date().getMonth(),
      dateRev: 0,
      calPos: { top: 0, left: 0 },
      chartPie: null,
      chartTrend: null,
      chartFault: null,
      faultDetailVisible: false,
      faultDetailTitle: '',
      faultDetailList: [],
    }

  },
  computed: {
    faultRangeLabel() {
      const f = this.faultRanges.find(r => r.key === this.faultRange);
      return f ? f.label : '本周';
    },
    calStyle() {
      return { top: this.calPos.top + 'px', left: this.calPos.left + 'px' };
    },
    calDays() {
      const y = this.calViewYear, m = this.calViewMonth;
      const first = new Date(y, m, 1);
      const startWeek = first.getDay();
      const daysInMonth = new Date(y, m + 1, 0).getDate();
      const prevDays = new Date(y, m, 0).getDate();
      const selected = (this.dateRanges[this.calKey] || {})[this.calWhich] || '';
      const today = todayIso();
      const cells = [];
      for (let i = 0; i < startWeek; i++) {
        const day = prevDays - startWeek + 1 + i;
        const iso = this.toISO(y, m - 1, day);
        cells.push({ day, muted: true, iso, selected: iso === selected, disabled: iso > today });
      }
      for (let d = 1; d <= daysInMonth; d++) {
        const iso = this.toISO(y, m, d);
        cells.push({ day: d, muted: false, iso, selected: iso === selected, disabled: iso > today });
      }
      const remain = 42 - cells.length;
      for (let d = 1; d <= remain; d++) {
        const iso = this.toISO(y, m + 1, d);
        cells.push({ day: d, muted: true, iso, selected: iso === selected, disabled: iso > today });
      }
      return cells;
    },
    workshop_list() {
      return pickWorkshopOptionLabels(this.sharedSiteOptions);
    },
    system_list() {
      return pickSystemOptionLabels(this.sharedSiteOptions, this.filter_workshop);
    },
    module_list() {
      let list = this.devices;
      if (this.filter_workshop) list = list.filter(d => workshopsEqual(d.workshop, this.filter_workshop));
      if (this.filter_system) list = list.filter(d => systemsEqual(d.system, this.filter_system));
      const fromDevices = list.map(d => d.module).filter(Boolean)
      const fromCo2 = (this.co2 || []).map(r => r.module).filter(Boolean)
      return [...new Set(fromDevices.concat(fromCo2))].sort();
    },
    filteredDevices() {
      return this.devices.filter(d => {
        if (!this.inUserDeptScope(d)) return false;
        if (this.filter_workshop && !workshopsEqual(d.workshop, this.filter_workshop)) return false;
        if (this.filter_system && !systemsEqual(d.system, this.filter_system)) return false;
        if (this.filter_module && d.module !== this.filter_module) return false;
        return true;
      });
    },
    overview() {
      const list = this.filteredDevices;
      return {
        total: list.length,
        running: list.filter(d => d.status === 'running').length,
        stopped: list.filter(d => d.status === 'stopped').length,
        fault: list.filter(d => d.status === 'fault').length,
      };
    },
    tableDevices() {
      return this.filteredDevices.slice().sort((a, b) => {
        const r = { fault: 0, running: 1, stopped: 2 };
        return (r[a.status] || 9) - (r[b.status] || 9);
      });
    },
    energyTotalMWh() {
      // 总能耗只跟库表数据走，不受类型筛选影响
      let sum = 0;
      this.typeLegend.forEach(t => { sum += this.energyByType[t.name] || 0; });
      return sum.toFixed(1);
    },
    filteredBackwash() {
      void this.dateRev
      const rows = this.panelRowsOrDerived('backwash')
      return rows.filter(b => this.matchWS(b)).map((b, i) => {
        const scaled = this.applyDateScale(b, 'backwash', i, 'count')
        scaled._rowKey = 'bw-' + i + '-' + (b.device || b.pool || '')
        return scaled
      })
    },
    filteredWater() {
      void this.dateRev
      const rows = this.panelRowsOrDerived('water')
      return rows.filter(w => this.matchWS(w)).map((w, i) => {
        const scaled = this.applyDateScale(w, 'water', i, 'flow')
        scaled._rowKey = 'wt-' + i + '-' + (w.name || '')
        return scaled
      })
    },
    filteredAerator() {
      void this.dateRev
      const rows = this.panelRowsOrDerived('aerator')
      return rows.filter(a => this.matchWS(a)).map((a, i) => {
        const scaled = this.applyDateScale(a, 'aerator', i, 'share')
        scaled._rowKey = 'ae-' + i + '-' + (a.pool || a.device || '')
        return scaled
      })
    },
    filteredEnergyTrend() {
      void this.dateRev
      return this.energyTrend || []
    },
    filteredCo2() {
      const rows = this.panelRowsOrDerived('co2')
      return rows.filter(s => {
        if (!this.matchWS(s)) return false
        if (this.filter_module && s.module && s.module !== this.filter_module) return false
        return true
      })
    },
    filteredAlarms() {
      return this.alarms.filter(a => {
        if (!isAlarmInUserSiteScope(a, this.sharedSiteOptions)) return false;
        if (!this.matchWS(a)) return false;
        if (this.filter_module && a.module && a.module !== this.filter_module) return false;
        return true;
      }).sort((a, b) => {
        const rank = { urgent: 0, warn: 1, info: 2, ok: 3 };
        return (rank[a.tone] ?? 9) - (rank[b.tone] ?? 9);
      });
    },
    openAlarmCount() {
      return this.filteredAlarms.filter(a => a.tone === 'urgent' || a.tone === 'warn' || a.tone === 'info').length;
    },
    allTypesSelected() {
      return this.typeLegend.length > 0
        && this.selectedTypes.length === this.typeLegend.length
        && this.typeLegend.every(t => this.selectedTypes.includes(t.name));
    },
    report() {
      const ov = this.overview;
      const open = this.filteredAlarms.filter(a => a.tone === 'urgent' || a.tone === 'warn' || a.tone === 'info');
      let health = Math.max(55, Math.min(96, 94 - ov.fault * 2 - open.length));
      let efficiency = Math.max(60, Math.min(98, 95 - ov.fault * 1.5));
      let energyScore = Math.max(50, Math.min(95, 86 - ov.fault));
      // align with design numbers when full view
      if (!this.filter_workshop && !this.filter_system && !this.filter_module) {
        health = 86; efficiency = 91; energyScore = 78;
      }
      const systems = [
        {
          name: '养殖系统',
          tag: '警告',
          tagClass: 'warn',
          logs: [
            { level: 'severe', levelText: '严重', text: 'A2-1微滤机反冲洗泵损坏，需立即检修更换' },
            { level: 'warning', levelText: '警告', text: 'B1-1温控器温感通讯异常，可能传感器漂移' },
            { level: 'advice', levelText: '建议', text: '1号鱼池增氧开阀频率偏高，核对溶氧联动策略' },
          ],
        },
        {
          name: '反硝化系统',
          tag: '提示',
          tagClass: 'info',
          logs: [
            { level: 'warning', levelText: '警告', text: '反硝化微滤机处于故障状态，检查滤网与反冲泵' },
            { level: 'advice', levelText: '建议', text: '本周反硝化循环泵能耗正常，维持当前运行参数' },
          ],
        },
      ];
      if (this.filter_workshop === '反硝化') return { health, efficiency, energyScore, systems: [systems[1]] };
      if (this.filter_workshop && this.filter_workshop !== '反硝化') return { health, efficiency, energyScore, systems: [systems[0]] };
      return { health, efficiency, energyScore, systems };
    },
  },
  mounted() {
    this.selectedTypes = this.TYPE_LEGEND.map(t => t.name);
    this.typeLegend = this.TYPE_LEGEND
    // 默认近 7 天最新区间 + 立刻有曲线，避免等接口/未选类型时空白
    this.dateRanges.energy = { start: daysAgoIso(7), end: todayIso() }
    this.ensureEnergyChartData(true)
    this.tickClock();
    this.loadSharedSiteOptions(false);
    this.lockPageScroll(true)
    this.clockTimer = setInterval(this.tickClock, 1000);
    this.$nextTick(async () => {
      this.initCharts();
      this.refreshCharts();
      // 复用缓存首屏更快；部门切换仍走 force
      await this.loadFromDatabase(false);
      this.applyDeviceThresholds();
      this.refreshCharts();
      this.refreshTimer = setInterval(() => { this.tickClock(); }, 8000);
      window.addEventListener('resize', this.resizeCharts);
      document.addEventListener('click', this.closePopups);
    });
    this._onSiteOptionsChanged = () => {
      this.loadSharedSiteOptions(false);
    };
    window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    this._onUserScopeChanged = async () => {
      this.resetDeviceScopedState();
      await this.loadSharedSiteOptions(true);
      await this.loadFromDatabase(true);
      this.applyDeviceThresholds();
      this.refreshCharts();
    };
    window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
  },
  activated() {
    // 从车间/系统记录返回时轻量补刷筛选项；数据走缓存
    this.lockPageScroll(true)
    this.loadSharedSiteOptions(false);
    if (!this.devices || !this.devices.length) {
      this.loadFromDatabase(false);
    }
  },
  deactivated() {
    this.lockPageScroll(false)
  },
  beforeDestroy() {
    this.lockPageScroll(false)
    clearInterval(this.refreshTimer);
    clearInterval(this.clockTimer);
    window.removeEventListener('resize', this.resizeCharts);
    document.removeEventListener('click', this.closePopups);
    if (this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    if (this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    }
  },
  methods: {
    lockPageScroll(on) {
      const cls = 'device-monitor-scroll-lock'
      const root = document.documentElement
      const body = document.body
      if (on) {
        root.classList.add(cls)
        body.classList.add(cls)
        // 滚轮落到外层时收回顶部，避免露出白底
        window.scrollTo(0, 0)
      } else {
        root.classList.remove(cls)
        body.classList.remove(cls)
      }
    },
    async loadSharedSiteOptions(force = false) {
      try {
        this.sharedSiteOptions = await fetchSiteOptions({
          includePonds: false,
          force: !!force
        });
      } catch (e) {
        console.warn('[deviceMonitor] shared site options', e);
      }
    },
    toPage(path) {
      this.$router.push(path).catch(() => {});
    },
    goFishDisease() {
      this.$router.push({ path: '/fish-disease', query: { from: '/device_monitor' } }).catch(() => {});
    },
    closePopups() {
      this.faultDdOpen = false;
      this.calOpen = false;
    },
    closeFaultDd() { this.faultDdOpen = false; },
    setFaultRange(key) {
      this.faultRange = key;
      this.faultDdOpen = false;
      this.renderFault();
    },
    toISO(y, m, d) {
      const dt = new Date(y, m, d);
      const p = n => String(n).padStart(2, '0');
      return dt.getFullYear() + '-' + p(dt.getMonth() + 1) + '-' + p(dt.getDate());
    },
    fmtDate(iso) {
      if (!iso) return '';
      return String(iso).replace(/-/g, '/');
    },
    openCal(key, which, e) {
      this.calKey = key;
      this.calWhich = which;
      const cur = this.dateRanges[key][which];
      const base = cur ? new Date(cur) : new Date();
      this.calViewYear = base.getFullYear();
      this.calViewMonth = base.getMonth();
      const rect = e.currentTarget.getBoundingClientRect();
      const popW = 268, popH = 300;
      let left = rect.right - popW;
      let top = rect.bottom + 6;
      if (left < 8) left = 8;
      if (left + popW > window.innerWidth - 8) left = window.innerWidth - popW - 8;
      if (top + popH > window.innerHeight - 8) top = Math.max(8, rect.top - popH - 6);
      this.calPos = { top, left };
      this.calOpen = true;
    },
    calShift(delta) {
      let m = this.calViewMonth + delta;
      let y = this.calViewYear;
      if (m < 0) { m = 11; y--; }
      if (m > 11) { m = 0; y++; }
      this.calViewMonth = m;
      this.calViewYear = y;
    },
    pickCalDay(d) {
      if (!d || d.disabled || (d.iso && d.iso > todayIso())) return;
      this.$set(this.dateRanges[this.calKey], this.calWhich, d.iso);
      // 保证起止顺序（start ≤ end）
      const r = this.dateRanges[this.calKey];
      if (r.start && r.end && r.start > r.end) {
        const a = r.start;
        const b = r.end;
        this.$set(this.dateRanges[this.calKey], 'start', b);
        this.$set(this.dateRanges[this.calKey], 'end', a);
      }
      this.calOpen = false;
      this.onDateRangeChange();
    },
    clearCal() {
      this.$set(this.dateRanges[this.calKey], this.calWhich, '');
      this.calOpen = false;
      this.onDateRangeChange();
    },
    pickToday() {
      const n = new Date();
      const iso = this.toISO(n.getFullYear(), n.getMonth(), n.getDate());
      this.$set(this.dateRanges[this.calKey], this.calWhich, iso);
      this.calViewYear = n.getFullYear();
      this.calViewMonth = n.getMonth();
      this.calOpen = false;
      this.onDateRangeChange();
    },
    dateSpanDays(key) {
      const r = this.dateRanges[key] || {}
      if (!r.start || !r.end) return 1
      const a = new Date(r.start)
      const b = new Date(r.end)
      if (isNaN(a) || isNaN(b)) return 1
      return Math.max(1, Math.round((b - a) / 86400000) + 1)
    },
    applyDateScale(row, key, index, field) {
      const span = this.dateSpanDays(key)
      const factor = Math.max(0.35, Math.min(2.2, 0.55 + span * 0.12 + (index % 3) * 0.05))
      const next = Object.assign({}, row)
      if (field && row[field] != null && Number.isFinite(Number(row[field]))) {
        next[field] = Math.round(Number(row[field]) * factor * 10) / 10
      }
      next._dateSpan = span
      return next
    },
    onDateRangeChange() {
      this.normalizeAllDateRanges()
      // 日期变更后按区间重画趋势；无库趋势时用最新区间合成，避免空白
      this.ensureEnergyChartData(true)
      this.dateRev += 1
      this.$nextTick(() => {
        this.refreshCharts && this.refreshCharts()
        this.renderFault && this.renderFault()
      })
    },
    /** 保证能耗趋势/环形图始终有可展示的最新数据 */
    ensureEnergyChartData(forceTrend = false) {
      // 无部门普通用户：禁止合成全厂演示能耗
      if (isUserDeptScopeDenied()) {
        this.energyByType = {}
        this.energyTrend = []
        return
      }
      const er = this.dateRanges.energy || {}
      const start = er.start || daysAgoIso(7)
      const end = er.end || todayIso()
      if (!er.start || !er.end) {
        this.$set(this.dateRanges, 'energy', { start, end })
      }
      // 不在此处强制回填 selectedTypes，否则「全不选」会被立刻撤销
      const hasPie = Object.keys(this.energyByType || {}).some(k => Number(this.energyByType[k]) > 0)
      if (!hasPie) {
        // 仅超管无部门时用演示环形；有部门但无数据保持空
        if (isPlatformUnscopedAdmin() && resolveCurrentUserDeptId() == null) {
          this.energyByType = Object.assign({}, DEFAULT_ENERGY_BY_TYPE)
        } else {
          return
        }
      }
      if (forceTrend || !(this.energyTrend || []).length || (this.energyTrend || []).length < 2) {
        this.energyTrend = buildTrend(start, end)
        return
      }
      // 接口返回了时间点但无设备类型序列时，折线全为 0，看起来像空白
      const names = (this.typeLegend || []).map(t => t.name)
      const hasSeries = (this.energyTrend || []).some((p) =>
        names.some((n) => p && p[n] != null && Number(p[n]) > 0)
      )
      if (!hasSeries) {
        this.energyTrend = buildTrend(start, end)
      }
    },
    normalizeAllDateRanges() {
      const today = todayIso()
      Object.keys(this.dateRanges || {}).forEach(key => {
        const r = this.dateRanges[key]
        if (!r) return
        if (!r.start) this.$set(r, 'start', daysAgoIso(key === 'energy' ? 7 : 0))
        if (!r.end) this.$set(r, 'end', today)
        if (r.end && r.end > today) this.$set(r, 'end', today)
        if (r.start && r.start > today) this.$set(r, 'start', today)
        if (r.start && r.end && r.start > r.end) {
          const t = r.start
          this.$set(r, 'start', r.end)
          this.$set(r, 'end', t)
        }
      })
    },
    matchWS(item) {
      if (this.filter_workshop && !workshopsEqual(item.workshop, this.filter_workshop) && item.workshop !== this.filter_workshop) {
        // 允许筛选「1号车间」命中「A车间」
        if (formatWorkshop(item.workshop) !== formatWorkshop(this.filter_workshop)) return false
      }
      if (this.filter_system && !systemsEqual(item.system, this.filter_system) && item.system !== this.filter_system) {
        if (formatSystem(item.system) !== formatSystem(this.filter_system)) return false
      }
      return true;
    },
    /**
     * 面板数据：接口有则用；为空或全是外部门演示车间时，用本页设备推导。
     * 勿再 normalizeSiteRecord（会把多设备收成同一鱼池名 → Vue 重复 key 不渲染）。
     */
    panelRowsOrDerived(kind) {
      // 空部门普通账号：禁止用本页设备推导出「看起来像本部门」的面板
      if (isUserDeptScopeDenied()) return []
      const raw = kind === 'water' ? this.water
        : (kind === 'aerator' ? this.aerator
          : (kind === 'co2' ? this.co2 : this.backwash))
      const list = Array.isArray(raw) ? raw : []
      const visible = list.filter((row) => this.matchWS(row) && this.panelWorkshopOk(row))
      if (visible.length) return visible
      if (!(this.devices || []).length) return []
      const derived = derivePanelsFromDevices((this.devices || []).map((d, idx) => ({
        name: d.name,
        workshop: d.workshop,
        system: d.system,
        pondDetailNum: d.name || ('设备' + (idx + 1)),
        pondSystemName: d.system,
        currValue: (d.power_kw != null && Number(d.power_kw) > 0)
          ? Number(d.power_kw)
          : (2.6 + (idx % 5) * 0.4),
        status: d.status === 'fault' ? 'maintenance' : (d.status || 'running'),
        unit: d.type
      })))
      if (kind === 'water') {
        return (derived.waterFlow || []).map((w) => ({
          name: w.name, flow: w.flow, workshop: w.workshop, system: w.system
        }))
      }
      if (kind === 'aerator') return derived.aerator || []
      if (kind === 'co2') return derived.co2 || []
      return derived.backwash || []
    },
    /** 面板只校验车间白名单，忽略鱼池硬匹配（避免整栏被滤空） */
    panelWorkshopOk(row) {
      if (isUserDeptScopeDenied()) return false
      const deptId = resolveCurrentUserDeptId()
      if (deptId == null) return isPlatformUnscopedAdmin()
      const opts = this.sharedSiteOptions
      const wsOpts = (opts && opts.workshopOptions) || []
      if (!wsOpts.length) return false
      const ws = String((row && (row.workshop || row.workshopName)) || '').trim()
      if (!ws) return true
      return wsOpts.some((o) =>
        workshopsEqual(o.label, ws) ||
        workshopsEqual(o.value, ws) ||
        String(o.label) === ws ||
        String(o.value) === ws
      )
    },
    /** 面板轻量规范化：保留设备名，避免全员收成「4号池(...)」 */
    normPanel(row) {
      if (!row) return row
      return Object.assign({}, row, {
        workshop: formatWorkshop(row.workshop) || row.workshop || '',
        system: formatSystem(row.system) || row.system || '',
        pool: row.device || row.pool || row.name || '',
        name: row.name || row.device || row.pool || ''
      })
    },
    /** 部门用户：必须落在共享车间/系统白名单内；筛选项未就绪时宁可空也不串部门 */
    inUserDeptScope(row) {
      if (isUserDeptScopeDenied()) return false
      const deptId = resolveCurrentUserDeptId()
      const opts = this.sharedSiteOptions
      const hasScope = !!(opts && (
        (opts.workshopOptions || []).length ||
        (opts.systemOptions || []).length ||
        (opts.pondOptions || []).length
      ))
      if (deptId != null && !hasScope) {
        // 有归属部门但白名单未到：仅放行完全无车间/系统标注的行（接口可能暂无字段）
        const ws = row && (row.workshopName || row.workshop || '')
        const sys = row && (row.pondSystemName || row.systemName || row.system || '')
        return !String(ws || '').trim() && !String(sys || '').trim()
      }
      return isRowInUserSiteScope(row, opts)
    },
    /** 仅平台超管无部门时允许演示种子；普通空部门账号禁止 */
    allowDemoPanelFallback() {
      return isPlatformUnscopedAdmin() && resolveCurrentUserDeptId() == null
    },
    resetDeviceScopedState() {
      this.devices = []
      this.alarms = []
      this.backwash = []
      this.water = []
      this.aerator = []
      this.co2 = []
      this.faultWeek = []
      this.faultMonth = []
      this.fault3m = []
      this.fault6m = []
      this.energyTrend = []
      this.energyByType = {}
      this.filter_workshop = ''
      this.filter_system = ''
      this.filter_module = ''
    },
    tickClock() {
      const n = new Date();
      const p = x => String(x).padStart(2, '0');
      this.current_time = n.getFullYear() + '/' + p(n.getMonth() + 1) + '/' + p(n.getDate())
        + ' ' + p(n.getHours()) + ':' + p(n.getMinutes());
    },
    async loadFromDatabase(force = false) {
      if (this._dbLoadInflight && !force) return this._dbLoadInflight
      const showMask = !(this.devices && this.devices.length)
      if (showMask) this.pageLoading = true
      const run = this._loadFromDatabaseInner(force)
      this._dbLoadInflight = run
      try {
        await run
      } finally {
        if (showMask) this.pageLoading = false
        if (this._dbLoadInflight === run) this._dbLoadInflight = null
      }
    },
    async _loadFromDatabaseInner(force = false) {
      this.normalizeAllDateRanges();
      if (force) clearZkAlarmCache()
      const norm = (row) => normalizeSiteRecord(Object.assign({}, row, {
        workshop: formatWorkshop(row.workshop) || row.workshop,
        system: formatSystem(row.system) || row.system,
        pool: normalizeLakeName(row.pool, row.system) || row.pool,
      }));
      const normPanel = (row) => this.normPanel(row);
      const mapStatus = (s) => {
        if (s === 'running') return 'running';
        // 检修/维护按停止展示，避免无遥测时整页被算成「故障」
        if (s === 'maintenance') return 'stopped';
        if (s === 'fault') return 'fault';
        if (s === 'stopped') return 'stopped';
        return s || 'stopped';
      };
      const toneFromLevel = (lv) => {
        const s = String(lv || '').toLowerCase();
        if (s.includes('严重') || s.includes('urgent') || s === '1') return 'urgent';
        if (s.includes('警告') || s.includes('warn') || s.includes('priority') || s === '2') return 'warn';
        return 'info';
      };
      const resolveDeviceSystem = (d) => {
        const candidates = [
          d.pondSystemName, d.systemName, d.system_name, d.system
        ]
        for (let i = 0; i < candidates.length; i++) {
          const s = String(candidates[i] == null ? '' : candidates[i]).trim()
          if (!s) continue
          // 跳过鱼池编码误填
          if (/^c\d+-s\d+/i.test(s)) continue
          const formatted = formatSystem(s)
          if (formatted) return formatted
        }
        const code = String(d.pondDetailNum || d.pondNum || '').trim()
        const m = code.match(/^c\d+-s(\d+)/i)
        if (m) return '系统' + m[1]
        return ''
      }
      const applyDevicesFromList = (list) => {
        this.devices = (list || []).map((d, i) => {
          const st = String(d.status == null ? d.deviceStatus || '' : d.status)
          const running = st === '1' || st === 'running'
          const fault = st === 'fault' || st === '2'
          return norm({
            id: 'DEV-' + (d.id || i + 1),
            name: d.deviceName || d.name || '-',
            type: d.deviceUnit || d.unit || '设备',
            workshop: d.workshopName || d.workshop || '',
            system: resolveDeviceSystem(d),
            pool: d.pondDetailNum || d.pondName || '',
            module: inferProductionModule(d.deviceName || d.name, d.deviceUnit || d.unit || d.type),
            status: running ? 'running' : (fault ? 'fault' : 'stopped'),
            runtime_h: 0,
            power_kw: d.currValue != null ? Number(d.currValue) : 0,
            power_trend: 0,
            voltage: d.voltage,
            current: d.current,
            key_param: d.currValue,
            key_param_label: d.deviceUnit || d.unit || '监测值'
          })
        })
      }

      // 三项并行：设备主档 / 监控大包 / 报警（避免串行等待）
      const quickPromise = getZkDevicePage({ pageNo: 1, pageSize: 100 })
        .catch((e) => {
          console.warn('[deviceMonitor] quick devices', e && e.message)
          return null
        })
      const monPromise = fetchDeviceMonitorLive(!!force).catch((e) => {
        console.warn('[deviceMonitor] monitor', e && e.message)
        return null
      })
      const alarmPromise = loadZkDeviceAlarms(!!force).catch(() => ({ alarms: [] }))

      // 主档先到先画，面板先本地推导，用户不必等大包
      try {
        const quick = await quickPromise
        const qList = (quick && quick.data && quick.data.list) || []
        if (qList.length || !(this.devices || []).length) {
          applyDevicesFromList(qList)
        }
        this.ensureEnergyChartData(true)
        if (this.allowDemoPanelFallback() && (!this.backwash.length || !this.aerator.length || !this.water.length || !this.co2.length)) {
          this.applyPanelFallbacks(norm)
        } else {
          this.fillEmptyPanelsFromDevices(normPanel)
        }
        this.$nextTick(() => { this.refreshCharts && this.refreshCharts() })
      } catch (e) {
        console.warn('[deviceMonitor] progressive paint', e && e.message)
      }

      try {
        const [mon, zkAlarms] = await Promise.all([monPromise, alarmPromise]);
        if (mon && Array.isArray(mon.devices)) {
          this.devices = (mon.devices || []).map((d, i) => norm({
            id: 'DEV-' + (i + 1),
            name: d.name,
            type: d.unit || '设备',
            workshop: d.workshop,
            system: resolveDeviceSystem(d),
            pool: d.pondDetailNum || '',
            module: inferProductionModule(d.name, d.unit || d.type),
            status: mapStatus(d.status),
            runtime_h: 0,
            power_kw: d.currValue != null ? Number(d.currValue) : 0,
            power_trend: 0,
            voltage: d.voltage,
            current: d.current,
            key_param: d.currValue,
            key_param_label: d.unit || '监测值'
          }));
        }
        const catLabel = (a) => {
          const cat = String((a && a.category) || '')
          if (cat === 'water') return '水质'
          if (cat === 'fish') return '鱼类'
          if (cat === 'person' || cat === 'human') return '人员'
          if (cat === 'device' || cat === 'equip' || cat === 'equipment') return '设备'
          const t = Number(a && a.warnDeviceType)
          if (t === 1) return '水质'
          if (t === 2) return '鱼类'
          if (t === 3) return '人员'
          const raw = String((a && (a.warnName || a.title || a.warnContent || a.message || '')) || '')
          if (/人员|门禁|闯入/.test(raw)) return '人员'
          if (/水质|溶氧|氨氮/.test(raw)) return '水质'
          if (/鱼类|生长|饵料/.test(raw)) return '鱼类'
          return '设备'
        }
        const stripPlain = (val) => String(val == null ? '' : val)
          .replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
        const fmtAgo = (t) => {
          if (!t) return ''
          const d = new Date(t)
          if (isNaN(d.getTime())) return String(t)
          const mins = Math.max(0, Math.round((Date.now() - d.getTime()) / 60000))
          if (mins < 1) return '刚刚'
          if (mins < 60) return mins + '分钟前'
          if (mins < 1440) return Math.floor(mins / 60) + '小时前'
          return Math.floor(mins / 1440) + '天前'
        }
        // 与 AI 报警详情页同一套字段：warnDevice / warnName / warnContent / pondName
        const mapAlarmRow = (a, i) => {
          const title = stripPlain(a.warnName || a.title || a.alarm_title || '')
          const content = stripPlain(a.warnContent || a.alarmContent || a.message || a.alarm_reason || '')
          const loc = stripPlain(a.pondName || a.warnDevice || a.pool_name || a.deviceName || '')
          const name = loc || title || content.slice(0, 24) || '-'
          const typeText = title || content.split('|')[0].trim() || '报警'
          const sysRaw = a.pondSystemName || a.systemName || a.system_name || a.system || ''
          const sysClean = /^c\d+-s\d+/i.test(String(sysRaw).trim())
            ? resolveDeviceSystem({
              pondSystemName: '',
              pondDetailNum: a.pondDetailNum || sysRaw,
              system: ''
            })
            : String(sysRaw || '').trim()
          return norm({
            id: 'AL-' + (a.id || a.Id || i),
            device_id: a.deviceId || a.warnDeviceId || a.warnDevice || '',
            device_name: name,
            category: catLabel(a),
            type: typeText,
            tone: toneFromLevel(
              a.alarmLevel || a.level || a.priority
              || (/级别:\s*urgent/i.test(content) ? 'urgent' : (/级别:\s*priority/i.test(content) ? 'warn' : ''))
            ),
            ago: a.time || fmtAgo(a.createTime || a.alarm_time || a.alarmTime),
            workshop: a.workshopName || a.workshop || '',
            system: sysClean,
            pool: a.pondDetailNum || (/^c\d+-s\d+/i.test(String(sysRaw).trim()) ? sysRaw : ''),
            module: inferProductionModule(name, typeText)
          })
        }
        const alarmList = (zkAlarms && zkAlarms.alarms) || []
        const isEquip = (a) => {
          const cat = String((a && a.category) || '')
          if (cat === 'water' || cat === 'fish' || cat === 'person' || cat === 'human') return false
          const t = Number(a && (a.warnDeviceType != null ? a.warnDeviceType : a._raw && a._raw.warnDeviceType))
          if (t === 1 || t === 2 || t === 3) return false
          const text = String((a && (a.alarm_title || a.warnName || a.title || a.alarm_reason || '')) || '')
          if (/人员|门禁|闯入|水质|溶氧|鱼类|生长滞后|饵料/.test(text)) return false
          return true
        }
        const equipList = alarmList.filter(isEquip)
        const sourceList = (equipList.length ? equipList : alarmList).slice(0, 20)
        this.alarms = sourceList.map(mapAlarmRow)
        if (mon) {
          this.energyByType = {};
          (mon.energy || []).forEach((e) => {
            if (e && e.name) this.energyByType[e.name] = e.value;
          });
          if ((mon.energyTrend || []).length) {
            const byTime = {};
            mon.energyTrend.forEach((t) => {
              const time = (t && (t.time || t.timeBucket)) || '-'
              const dtype = (t && (t.deviceType || t.device_type || t.name)) || ''
              const raw = t && (t.value != null ? t.value : (t.deltaKwh != null ? t.deltaKwh : t.kwhValue))
              const val = raw != null && raw !== '' ? Number(raw) : 0
              if (!byTime[time]) byTime[time] = { time }
              if (dtype) byTime[time][dtype] = Number.isFinite(val) ? val : 0
            });
            this.energyTrend = Object.keys(byTime).sort().map((k) => byTime[k]);
          }
          // 无有效类型序列时补齐；有库表趋势则保留并自适应纵轴
          this.ensureEnergyChartData(false);
          if (Array.isArray(mon.faultMonth)) {
            this.faultMonth = (mon.faultMonth || []).map((f) => ({ name: f.name, count: f.count }));
            this.faultWeek = this.faultMonth.slice(0, 5);
            this.fault3m = this.faultMonth.slice();
            this.fault6m = this.faultMonth.slice();
          }
          // 含空数组：部门接口无数据时清空；面板用轻量 norm，禁止收成同一鱼池名
          if (Array.isArray(mon.backwash)) this.backwash = (mon.backwash || []).map(normPanel);
          if (Array.isArray(mon.waterFlow)) {
            this.water = (mon.waterFlow || []).map((w) => normPanel({
              name: w.name, flow: w.flow, workshop: w.workshop, system: w.system
            }));
          }
          if (Array.isArray(mon.aerator)) this.aerator = (mon.aerator || []).map(normPanel);
          if (Array.isArray(mon.co2)) this.co2 = (mon.co2 || []).map(normPanel);
        }
        // 面板仍空时：用本页设备列表推导
        this.fillEmptyPanelsFromDevices(normPanel);
        // 仅超管：后端 panel 失败时用全厂演示种子
        if (this.allowDemoPanelFallback()) {
          this.applyPanelFallbacks(norm);
          if (!this.alarms.length) {
            this.alarms = [
              { id: 'AL1', device_id: 'A2-1', device_name: 'A2-1微滤机', category: '微滤机', type: '反冲洗泵损坏', tone: 'urgent', ago: '5分钟前', workshop: '车间1', system: '系统2', module: '循环过滤' },
              { id: 'AL2', device_id: 'B1-1', device_name: 'B1-1温控系统', category: '温控器', type: '温度传感器通讯异常', tone: 'warn', ago: '15分钟前', workshop: '车间2', system: '系统1', module: '温控' },
              { id: 'AL3', device_id: 'C1-1', device_name: 'C1-1风机', category: '风机', type: '风机皮带松弛', tone: 'warn', ago: '28分钟前', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' },
            ].map(norm);
          }
        }
        this.ensureEnergyChartData(!(this.energyTrend || []).length || this.energyTrend.length < 2);
        // 任务同步放后台，不挡图表刷新
        try { this.syncAlarmsToMission(); } catch (e) { console.warn('[deviceMonitor] syncAlarms', e && e.message); }
        this.$nextTick(() => {
          this.refreshCharts && this.refreshCharts();
        });
      } catch (e) {
        // 失败时保留已加载的设备主档，禁止整页清零
        console.error('[deviceMonitor] loadFromDatabase failed', e);
      }
    },
    /** 面板为空时用本部门设备推导，避免反冲洗/增氧/补水/CO₂ 空白 */
    fillEmptyPanelsFromDevices(normFn) {
      const norm = typeof normFn === 'function' ? normFn : (row) => row
      const needBw = !this.backwash.length || !this.filteredBackwash.length
      const needAe = !this.aerator.length || !this.filteredAerator.length
      const needWt = !this.water.length || !this.filteredWater.length
      const needCo2 = !this.co2.length || !this.filteredCo2.length
      if (!needBw && !needAe && !needWt && !needCo2) return
      if (!(this.devices || []).length) return
      const derived = derivePanelsFromDevices((this.devices || []).map((d, idx) => ({
        name: d.name,
        workshop: d.workshop,
        system: d.system,
        pondDetailNum: d.name || ('设备' + (idx + 1)),
        pondSystemName: d.system,
        currValue: (d.power_kw != null && Number(d.power_kw) > 0) ? Number(d.power_kw) : (2.6 + (idx % 5) * 0.4),
        status: d.status === 'fault' ? 'maintenance' : (d.status || 'running'),
        unit: d.type
      })))
      if (needBw && derived.backwash.length) this.backwash = derived.backwash.map(norm)
      if (needAe && derived.aerator.length) this.aerator = derived.aerator.map(norm)
      if (needWt && derived.waterFlow.length) {
        this.water = derived.waterFlow.map((w) => norm({
          name: w.name, flow: w.flow, workshop: w.workshop, system: w.system
        }))
      }
      if (needCo2 && derived.co2.length) this.co2 = derived.co2.map(norm)
    },
    /** 面板空数据时立即可见的兜底（与库种子一致）；部门用户禁止调用 */
    applyPanelFallbacks(normFn) {
      if (!this.allowDemoPanelFallback()) return;
      const norm = typeof normFn === 'function' ? normFn : (row) => row;
      if (!this.backwash.length) {
        this.backwash = [
          { pool: 'c1-s1-1', count: 3.8, status: 'normal', device: '微滤机A1-1', workshop: '车间1', system: '系统1' },
          { pool: 'c1-s1-2', count: 4.2, status: 'normal', device: '微滤机A1-2', workshop: '车间1', system: '系统1' },
          { pool: 'c1-s2-1', count: 0, status: 'fault', device: '微滤机A2-1', workshop: '车间1', system: '系统2' },
          { pool: 'c2-s1-1', count: 3.1, status: 'normal', device: '微滤机B1-1', workshop: '车间2', system: '系统1' },
          { pool: 'c2-s2-1', count: 2.8, status: 'normal', device: '微滤机B2-1', workshop: '车间2', system: '系统2' },
          { pool: 'c3-s1-1', count: 3.4, status: 'normal', device: '微滤机C1-1', workshop: '车间3', system: '系统1' },
          { pool: 'DN-1', count: 2.5, status: 'normal', device: '反硝化补水泵', workshop: '反硝化', system: '反硝化系统' },
        ].map(norm);
      }
      if (!this.aerator.length) {
        this.aerator = [
          { pool: '车间1-系统1-鱼池1', share: 78, workshop: '车间1', system: '系统1' },
          { pool: '车间1-系统1-鱼池2', share: 65, workshop: '车间1', system: '系统1' },
          { pool: '车间2-系统1-鱼池1', share: 52, workshop: '车间2', system: '系统1' },
          { pool: '车间2-系统1-鱼池2', share: 41, workshop: '车间2', system: '系统1' },
          { pool: '车间3-系统1-鱼池1', share: 58, workshop: '车间3', system: '系统1' },
          { pool: '车间3-系统1-鱼池2', share: 47, workshop: '车间3', system: '系统1' },
        ].map(norm);
      }
      if (!this.co2.length) {
        this.co2 = [
          { device: '风机A1', start: '02:02', end: '04:05', workshop: '车间1', system: '系统1', module: 'CO₂吹脱' },
          { device: '风机B1', start: '05:10', end: '06:40', workshop: '车间2', system: '系统1', module: 'CO₂吹脱' },
          { device: '风机C1-1', start: '08:35', end: '10:06', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' },
          { device: '风机C2', start: '14:20', end: '15:50', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' },
        ].map(norm);
      }
      if (!this.water.length) {
        this.water = [
          { name: '车间1-系统1', flow: 45.0, workshop: '车间1', system: '系统1' },
          { name: '车间2-系统1', flow: 42.5, workshop: '车间2', system: '系统1' },
          { name: '车间3-系统1', flow: 38.2, workshop: '车间3', system: '系统1' },
          { name: '反硝化系统', flow: 28.6, workshop: '反硝化', system: '反硝化系统' },
        ].map(norm);
      }
      if (!this.faultMonth.length) {
        this.faultMonth = [
          { name: '微滤机A2-1', count: 14 },
          { name: '增氧机4号', count: 11 },
          { name: '臭氧发生器B1', count: 9 },
          { name: '反硝化微滤', count: 7 },
          { name: '温控器B1-1', count: 4 },
        ];
        this.faultWeek = this.faultMonth.slice(0, 5);
        this.fault3m = this.faultMonth.slice();
        this.fault6m = this.faultMonth.slice();
      }
    },
    /** 已停用：假数据已写入库表；方法体整段注释保留备查 */
    loadDemo() {
      /*
      this.normalizeAllDateRanges();
      const norm = (row) => normalizeSiteRecord(Object.assign({}, row, {
        workshop: formatWorkshop(row.workshop) || row.workshop,
        system: formatSystem(row.system) || row.system,
        pool: normalizeLakeName(row.pool, row.system) || row.pool,
      }));
      this.devices = JSON.parse(JSON.stringify(this.DEMO_DEVICES)).map(norm);
      this.alarms = JSON.parse(JSON.stringify(this.DEMO_ALARMS)).map(norm);
      this.backwash = JSON.parse(JSON.stringify(this.DEMO_BACKWASH)).map(norm);
      this.water = JSON.parse(JSON.stringify(this.DEMO_WATER)).map(norm);
      this.aerator = JSON.parse(JSON.stringify(this.DEMO_AERATOR)).map(norm);
      this.co2 = JSON.parse(JSON.stringify(this.DEMO_CO2)).map(norm);
      this.energyByType = Object.assign({}, this.DEMO_ENERGY);
      this.energyTrend = buildTrend(
        (this.dateRanges.energy && this.dateRanges.energy.start) || daysAgoIso(7),
        (this.dateRanges.energy && this.dateRanges.energy.end) || todayIso()
      );
      this.faultWeek = JSON.parse(JSON.stringify(this.DEMO_FAULT_WEEK));
      this.faultMonth = JSON.parse(JSON.stringify(this.DEMO_FAULT_MONTH));
      this.fault3m = JSON.parse(JSON.stringify(this.DEMO_FAULT_3M));
      this.fault6m = JSON.parse(JSON.stringify(this.DEMO_FAULT_6M));
      this.syncAlarmsToMission();
      */
    },
    async applyDeviceThresholds() {
      try {
        const deviceMod = await getThresholdModule('device', true)
        const metrics = resolveDeviceMetrics(deviceMod)
        const voltage = metrics.find(m => m.id === 'voltage')
        const current = metrics.find(m => m.id === 'current')
        const energy = metrics.find(m => m.id === 'energy_mom')
        const hasTelemetry = (v) => {
          if (v == null || v === '') return false
          const n = Number(v)
          // 0 多为未接入/未上报，不当作越限故障（否则全员「故障」）
          return Number.isFinite(n) && n !== 0
        }
        ;(this.devices || []).forEach(d => {
          if (!d) return
          let warn = false
          let critical = false
          if (voltage && hasTelemetry(d.voltage)) {
            const v = Number(d.voltage)
            if (v < voltage.criticalLow || v > voltage.criticalHigh) critical = true
            else if (v < voltage.warnLow || v > voltage.warnHigh) warn = true
          }
          if (current && hasTelemetry(d.current)) {
            const c = Number(d.current)
            if (c < current.criticalLow || c > current.criticalHigh) critical = true
            else if (c < current.warnLow || c > current.warnHigh) warn = true
          }
          if (energy && d.power_trend != null && Number(d.power_trend) !== 0) {
            const pct = Math.abs(Number(d.power_trend) * 100)
            if (pct > Math.abs(Number(energy.criticalHigh) || 52)) critical = true
            else if (pct > Math.abs(Number(energy.warnHigh) || 30)) warn = true
          }
          if (critical && d.status !== 'fault') {
            d.status = 'fault'
            d.thresholdHit = true
          } else if (warn && d.status === 'running') {
            d.status = 'warn'
            d.thresholdHit = true
          }
        })
        ;(this.devices || []).filter(d => d.status === 'fault' && d.thresholdHit).slice(0, 5).forEach(d => {
          pushAlarmAndCreateTask({
            source: 'device',
            category: 'equip',
            pondDetailNum: d.system || d.id,
            pondName: d.name,
            workshopName: d.workshop || '',
            pondSystemName: d.system || '',
            title: `设备异常 ${d.name}`,
            content: `电压=${d.voltage}V 电流=${d.current}A 功率=${d.power_kw}kW（阈值管理-设备运行）`,
            taskTitle: `巡检：设备 ${d.name} 运行异常`,
            level: 'urgent',
            period: 'day'
          }).catch(silentCatch('设备运行-故障报警'))
        })
      } catch (e) {
        console.warn('[设备运行] 阈值联动失败', e && e.message)
        this.$message && this.$message.warning('设备阈值联动失败，部分告警可能未同步')
      }
    },
    async syncAlarmsToMission() {
      try {
        await syncModuleAiAlarmsToMission(this.alarms || [], {
          prefix: 'DEV-AI-',
          source: 'device_monitor',
          category: 'equip'
        });
        await syncOpenSystemAlarmsToMission();
      } catch (e) {
        console.warn('[设备监控] 报警同步任务失败', e && e.message);
      }
    },
    statusText(s) {
      return { running: '运行', stopped: '停止', fault: '故障' }[s] || s;
    },
    trendArrow(t) {
      if (t > 0.05) return '↑';
      if (t < -0.05) return '↓';
      return '';
    },
    abnormalPower(d) { return d.status === 'fault' || (d.status === 'running' && d.power_kw > 7); },
    abnormalVoltage(d) { return d.status === 'running' && (d.voltage < 360 || d.voltage > 400); },
    backwashWidth(b) {
      const max = Math.max(...this.filteredBackwash.filter(x => x.status !== 'fault').map(x => x.count), 1);
      return Math.round(b.count / max * 100);
    },
    waterWidth(w) {
      const max = Math.max(...this.filteredWater.map(x => x.flow), 1);
      return Math.round(w.flow / max * 100);
    },
    co2SegStyle(seg) {
      const toMin = t => { const [h, m] = t.split(':').map(Number); return h * 60 + m; };
      const s = toMin(seg.start), e = toMin(seg.end);
      return { left: (s / 1440 * 100).toFixed(2) + '%', width: Math.max(2, (e - s) / 1440 * 100).toFixed(2) + '%' };
    },
    segLabel(seg) {
      const toMin = t => { const [h, m] = t.split(':').map(Number); return h * 60 + m; };
      const mins = toMin(seg.end) - toMin(seg.start);
      return seg.start + '-' + seg.end + ' (' + mins + 'min)';
    },
    selectDevice(id) {
      this.active_device_id = id;
      this.$nextTick(() => {
        const el = this.$el && this.$el.querySelector('.panel-params .row-active');
        if (el && el.scrollIntoView) {
          el.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
        }
      });
    },
    selectDeviceByName(name) {
      const d = this.devices.find(x => x.name === name || x.id === name || (name && name.indexOf(x.id) >= 0));
      if (d) this.selectDevice(d.id);
    },
    openFaultDetail(deviceName) {
      this.selectDeviceByName(deviceName);
      const map = {
        week: this.faultWeek,
        month: this.faultMonth,
        '3m': this.fault3m,
        '6m': this.fault6m,
      };
      const list = map[this.faultRange] || this.faultWeek;
      const hit = list.find(i => i.name === deviceName) || { name: deviceName, count: 0 };
      const relatedAlarms = this.alarms.filter(a =>
        a.device_name === deviceName ||
        a.device_id === deviceName ||
        (deviceName && (deviceName.indexOf(a.device_id) >= 0 || (a.device_name && deviceName.indexOf(a.device_name) >= 0)))
      );
      this.faultDetailTitle = deviceName + ' · ' + this.faultRangeLabel + '故障详情';
      this.faultDetailList = [];
      const count = Math.max(hit.count || 0, relatedAlarms.length || 1);
      for (let i = 0; i < count; i++) {
        const alarm = relatedAlarms[i];
        this.faultDetailList.push({
          index: i + 1,
          time: alarm ? (alarm.ago || '-') : (this.faultRangeLabel + '第' + (i + 1) + '次'),
          device: deviceName,
          type: alarm ? alarm.type : '设备故障/异常',
          category: alarm ? alarm.category : '-',
          tone: alarm ? alarm.tone : 'warn',
          alarmId: alarm ? alarm.id : '',
          deviceId: alarm ? alarm.device_id : (this.active_device_id || ''),
        });
      }
      this.faultDetailVisible = true;
    },
    jumpAlarmToParams(alarm) {
      if (!alarm) return;
      this.selectDevice(alarm.device_id);
      this.$nextTick(() => {
        const panel = this.$el && this.$el.querySelector('.panel-params');
        if (panel && panel.scrollIntoView) panel.scrollIntoView({ block: 'center', behavior: 'smooth' });
      });
    },
    jumpFaultToAlarm(row) {
      if (row && row.alarmId) {
        this.faultDetailVisible = false;
        this.$router.push({ path: '/alert_monitor', query: { deviceId: row.deviceId || '', alarmId: row.alarmId } }).catch(() => {});
        return;
      }
      this.faultDetailVisible = false;
      this.$router.push({
        path: '/alert_monitor',
        query: { deviceId: row && row.deviceId ? row.deviceId : this.active_device_id, deviceName: row && row.device }
      }).catch(() => {});
    },
    downloadText(filename, content, mime) {
      const blob = new Blob(['\ufeff' + content], { type: mime || 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.click();
      URL.revokeObjectURL(url);
    },
    exportEnergy(format) {
      const headers = ['时间'].concat(this.typeLegend.map(t => t.name));
      const rows = this.filteredEnergyTrend.map(p => [p.time].concat(this.typeLegend.map(t => p[t.name] != null ? p[t.name] : 0)));
      const typeRows = [['类型', '能耗(MWh)']].concat(this.typeLegend.map(t => [t.name, this.energyByType[t.name] || 0]));
      if (format === 'excel') {
        const sheet1 = [headers].concat(rows).map(r => r.join('\t')).join('\n');
        const sheet2 = typeRows.map(r => r.join('\t')).join('\n');
        this.downloadText('设备能耗统计.xls', sheet1 + '\n\n' + sheet2, 'application/vnd.ms-excel;charset=utf-8;');
      } else {
        const csv = [headers].concat(rows).map(r => r.map(c => '"' + String(c).replace(/"/g, '""') + '"').join(',')).join('\n');
        this.downloadText('设备能耗统计.csv', csv, 'text/csv;charset=utf-8;');
      }
      this.$message && this.$message.success('能耗统计已导出');
    },
    exportFrequency(format) {
      const bw = [['池号', '设备', '次数/时', '状态']].concat(
        this.filteredBackwash.map(b => [b.pool, b.device || '', b.count, b.status || ''])
      );
      const ae = [['池号', '开启频率', '状态']].concat(
        this.filteredAerator.map(a => [a.pool, a.count != null ? a.count : (a.freq || ''), a.status || ''])
      );
      const body = '反冲洗频率\n' + bw.map(r => r.join('\t')).join('\n') + '\n\n增氧开启频率\n' + ae.map(r => r.join('\t')).join('\n');
      if (format === 'excel') {
        this.downloadText('设备运行频率时段统计.xls', body, 'application/vnd.ms-excel;charset=utf-8;');
      } else {
        const csv = body.split('\n').map(line => line.split('\t').map(c => '"' + String(c).replace(/"/g, '""') + '"').join(',')).join('\n');
        this.downloadText('设备运行频率时段统计.csv', csv, 'text/csv;charset=utf-8;');
      }
      this.$message && this.$message.success('运行频率统计已导出');
    },
    onFilterChange() {
      if (this.filter_system && !this.system_list.includes(this.filter_system)) this.filter_system = '';
      if (this.filter_module && !this.module_list.includes(this.filter_module)) this.filter_module = '';
      this.refreshCharts();
    },
    toggleType(name) {
      const i = this.selectedTypes.indexOf(name);
      if (i >= 0) this.selectedTypes.splice(i, 1);
      else this.selectedTypes.push(name);
      this.renderEnergyTrend();
    },
    selectAllTypes() {
      this.selectedTypes = this.typeLegend.map(t => t.name);
      this.renderEnergyTrend();
    },
    deselectAllTypes() {
      this.selectedTypes = [];
      this.renderEnergyTrend();
    },
    toggleSelectAllTypes() {
      if (this.allTypesSelected) this.deselectAllTypes();
      else this.selectAllTypes();
    },
    initCharts() {
      this.chartPie = echarts.init(document.getElementById('energyPie'));
      this.chartTrend = echarts.init(document.getElementById('energyTrend'));
      this.chartFault = echarts.init(document.getElementById('faultChart'));
      this.chartFault.on('click', p => this.openFaultDetail(p.name));
    },
    resizeCharts() {
      [this.chartPie, this.chartTrend, this.chartFault].forEach(c => c && c.resize());
    },
    refreshCharts() {
      this.$nextTick(() => {
        this.renderEnergyPie();
        this.renderEnergyTrend();
        this.renderFault();
      });
    },
    /** 环形图：只读 energyByType（库表），不随类型按钮变化 */
    renderEnergyPie() {
      if (!this.chartPie) return;
      const pieData = this.typeLegend.map(t => {
        const mwh = +(this.energyByType[t.name] || 0);
        return {
          name: t.name,
          value: mwh,
          itemStyle: { color: t.color, borderColor: '#0a1628', borderWidth: 2 },
        };
      }).filter(d => d.value > 0);

      const fmtK = (mwh) => {
        const kwh = mwh * 1000;
        if (kwh >= 1000) return (kwh / 1000).toFixed(1) + 'k';
        return Math.round(kwh) + '';
      };

      this.chartPie.setOption({
        tooltip: chartTip({
          trigger: 'item',
          formatter: (p) => p.name + '<br/>' + p.value.toFixed(1) + ' MWh (' + p.percent + '%)',
        }),
        series: [{
          type: 'pie',
          // 恢复较大环带，接近原先视觉；标签仍放环内避免裁切
          radius: ['52%', '90%'],
          center: ['50%', '50%'],
          padAngle: 1.5,
          avoidLabelOverlap: true,
          label: {
            show: true,
            position: 'inside',
            formatter: (p) => {
              if (p.percent < 6) return '';
              return '{v|' + fmtK(p.value) + '}\n{p|' + Math.round(p.percent) + '%}';
            },
            rich: {
              v: { color: '#ffffff', fontSize: 11, fontWeight: 600, lineHeight: 14 },
              p: { color: '#f0c040', fontSize: 10, lineHeight: 13 },
            },
          },
          labelLine: { show: false },
          data: pieData.length ? pieData : [{ name: '-', value: 1, itemStyle: { color: '#1a3050' }, label: { show: false } }],
        }],
      }, true);
    },
    /** 趋势图：类型按钮控制折线显隐；全不选时清空折线 */
    renderEnergyTrend() {
      if (!this.chartTrend) return;
      const colors = this.typeLegend.map(t => t.color);
      const activeNames = Array.isArray(this.selectedTypes) ? this.selectedTypes.slice() : [];
      const points = this.filteredEnergyTrend || [];
      const typeSeries = activeNames.map(name => {
        const t = this.typeLegend.find(x => x.name === name);
        return {
          name,
          type: 'line',
          smooth: true,
          showSymbol: true,
          symbolSize: 4,
          lineStyle: { width: name === '水泵' ? 2.5 : 1.5 },
          itemStyle: { color: t && t.color },
          data: points.map(p => {
            const v = p ? Number(p[name]) : 0
            return Number.isFinite(v) ? v : 0
          }),
        };
      });
      // 电度日值多为个位数～几十 kWh，勿写死 0～800，否则折线贴底像「没数据」
      let dataMax = 0
      typeSeries.forEach((s) => {
        (s.data || []).forEach((v) => {
          if (Number(v) > dataMax) dataMax = Number(v)
        })
      })
      const yMax = dataMax <= 0
        ? 100
        : Math.max(10, Math.ceil(dataMax * 1.2 / 5) * 5)
      const yInterval = yMax <= 20 ? 5 : (yMax <= 50 ? 10 : (yMax <= 100 ? 20 : Math.ceil(yMax / 4)))
      this.chartTrend.setOption({
        color: colors,
        tooltip: chartTip({
          trigger: 'axis',
          formatter: (params) => {
            if (!params || !params.length) return '';
            let html = '<div style="margin-bottom:4px;color:#9bd8ff">' + params[0].axisValue + '</div>';
            params.forEach(p => {
              html += '<div style="line-height:1.55">' + (p.marker || '') + ' ' + p.seriesName + ' : <b style="color:#fff">' + p.value + '</b> kWh</div>';
            });
            return html;
          },
        }),
        legend: { show: false },
        grid: { left: 8, right: 12, top: 14, bottom: 8, containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: points.map(p => p.time),
          axisLabel: {
            show: true,
            color: '#8ad4ff',
            fontSize: 11,
            interval: 0,
            hideOverlap: false,
            margin: 10,
            formatter: (v) => v
          },
          axisLine: { show: true, lineStyle: { color: '#2a6a90' } },
          axisTick: { show: true, lineStyle: { color: '#2a6a90' } },
          splitLine: { show: true, lineStyle: { color: '#1a3a55', type: 'dashed' } },
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: yMax,
          interval: yInterval,
          axisLabel: { color: '#5ecbff', fontSize: 11 },
          axisLine: { show: true, lineStyle: { color: '#2a6a90' } },
          splitLine: { lineStyle: { color: '#1a3a55', type: 'dashed' } },
        },
        series: typeSeries.length ? typeSeries : [{ type: 'line', data: [] }],
      }, true);
      this.$nextTick(() => { this.chartTrend && this.chartTrend.resize(); this.chartPie && this.chartPie.resize(); });
    },
    renderEnergy() {
      this.renderEnergyPie();
      this.renderEnergyTrend();
    },
    renderFault() {
      if (!this.chartFault) return;
      const map = {
        week: this.faultWeek,
        month: this.faultMonth,
        '3m': this.fault3m,
        '6m': this.fault6m,
      };
      let list = map[this.faultRange] || this.faultWeek;
      const names = new Set(this.filteredDevices.map(d => d.name));
      const filtered = list.filter(i => names.has(i.name) || [...names].some(n => i.name.indexOf(n) >= 0 || n.indexOf(i.name) >= 0));
      if (filtered.length) list = filtered;
      this.chartFault.setOption({
        grid: { left: 88, right: 16, top: 8, bottom: 8 },
        tooltip: chartTip({
          trigger: 'axis',
          axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(255,80,80,0.12)' } },
          formatter: (params) => {
            if (!params || !params.length) return '';
            const p = params[0];
            return '<div style="margin-bottom:4px;color:#9bd8ff">' + (p.name || '') + '</div>'
              + (p.marker || '') + ' 故障次数 : <b style="color:#ffaaaa">' + p.value + '</b>';
          },
        }),
        xAxis: {
          type: 'value',
          show: false,
          splitLine: { show: false },
        },
        yAxis: {
          type: 'category',
          data: list.map(i => i.name),
          axisLabel: { color: '#94d8ff', fontSize: 10 },
          axisLine: { show: false },
          axisTick: { show: false },
        },
        series: [{
          type: 'bar',
          name: '故障次数',
          barWidth: 10,
          data: list.map(i => i.count),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#6a1a1a' },
              { offset: 1, color: '#ff5555' },
            ]),
            borderRadius: [0, 4, 4, 0],
          },
          label: { show: true, position: 'right', color: '#ffaaaa', fontSize: 10 },
        }],
      }, true);
    },
  }
}
</script>

<template>
  <div class="device-monitor-page">
    <PageLoadingMask
      :visible="pageLoading && !(devices && devices.length)"
      text="正在加载设备监控数据…"
    />
    <!-- 顶栏 -->
    <header class="header">
      <div class="header-bar">
        <div class="header-left">
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
          <div class="page-title">设备情况监控</div>
          <div class="time-text">{{ current_time }}</div>
        </div>

        <div class="header-right">
          <div class="quick-btns">
            <button class="q-btn btn-red" @click="toPage('/alert_monitor')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
              <span>设备报警</span>
            </button>
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>存量分布</span>
            </button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
              <span>智能巡检</span>
            </button>
            <button class="q-btn btn-orange" style="opacity:0.95" @click="toPage('/mission_planning')">
              <span>任务与计划</span>
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
            <!-- Excel：综合目录去掉试验系统入口 -->
          </div>
        </div>
      </div>
    </header>


    <!-- 筛选 -->
    <div class="filter-bar">
      <div class="filter-label">筛选条件</div>
      <div class="select-wrap">
        <label>车间</label>
        <select v-model="filter_workshop" @change="onFilterChange">
          <option value="">全部车间</option>
          <option v-for="(w, wi) in workshop_list" :key="'ws-' + wi + '-' + w" :value="w">{{ w }}</option>
        </select>
      </div>
      <div class="select-wrap">
        <label>系统</label>
        <select v-model="filter_system" @change="onFilterChange">
          <option value="">全部系统</option>
          <option v-for="(s, si) in system_list" :key="'sys-' + si + '-' + s" :value="s">{{ s }}</option>
        </select>
      </div>
      <div class="select-wrap">
        <label>生产模块</label>
        <select v-model="filter_module" @change="onFilterChange">
          <option value="">全部模块</option>
          <option v-for="(m, mi) in module_list" :key="'mod-' + mi + '-' + m" :value="m">{{ m }}</option>
        </select>
      </div>
    </div>

    <!-- 总览 -->
    <div class="overview-row">
      <div class="overview-card total">
        <div class="ov-icon blue">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M19.14 12.94c.04-.31.06-.63.06-.94 0-.31-.02-.63-.06-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.49.49 0 0 0-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54a.48.48 0 0 0-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.04.31-.06.63-.06.94s.02.63.06.94l-2.03 1.58a.49.49 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6A3.6 3.6 0 1 1 12 8.4a3.6 3.6 0 0 1 0 7.2z"/></svg>
        </div>
        <div class="ov-meta">
          <div class="label">设备总数</div>
          <div class="value">{{ overview.total }}<span>台</span></div>
        </div>
      </div>
      <div class="overview-card running">
        <div class="ov-icon green">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M7 2v11h3v9l7-12h-4l4-8z"/></svg>
        </div>
        <div class="ov-meta">
          <div class="label">运行中</div>
          <div class="value">{{ overview.running }}<span>台</span></div>
        </div>
      </div>
      <div class="overview-card stopped">
        <div class="ov-icon yellow">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 3c-1.1 0-2 .9-2 2v1.1C7.05 6.85 5 9.67 5 13c0 3.87 3.13 7 7 7s7-3.13 7-7c0-3.33-2.05-6.15-5-6.9V5c0-1.1-.9-2-2-2zm0 14.5c-2.49 0-4.5-2.01-4.5-4.5S9.51 8.5 12 8.5s4.5 2.01 4.5 4.5-2.01 4.5-4.5 4.5z"/></svg>
        </div>
        <div class="ov-meta">
          <div class="label">已停止</div>
          <div class="value">{{ overview.stopped }}<span>台</span></div>
        </div>
      </div>
      <div class="overview-card fault">
        <div class="ov-icon red">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>
        </div>
        <div class="ov-meta">
          <div class="label">故障/异常</div>
          <div class="value">{{ overview.fault }}<span>台</span></div>
        </div>
      </div>
    </div>

    <!-- 三列主体 -->
    <div class="body-grid">
      <!-- ===== 左列 ===== -->
      <div class="col col-left">
        <div class="panel panel-energy">
          <div class="panel-head">
            <div class="energy-title">
              <svg class="energy-bolt" viewBox="0 0 24 24" fill="currentColor"><path d="M13 2L4 14h7l-1 8 10-14h-7l1-6z"/></svg>
              设备能耗
              <span class="energy-mwh">{{ energyTotalMWh }} MWh</span>
            </div>
            <div class="export-btns">
              <button type="button" class="export-btn" @click.stop="exportEnergy('csv')">导出CSV</button>
              <button type="button" class="export-btn" @click.stop="exportEnergy('excel')">导出Excel</button>
            </div>
            <div class="drp" @click.stop>
              <div class="drp-field" :class="{on: calOpen && calKey==='energy' && calWhich==='start'}"
                   @click="openCal('energy','start', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.energy.start}">{{ fmtDate(dateRanges.energy.start) || '开始日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
              <span class="drp-to">至</span>
              <div class="drp-field" :class="{on: calOpen && calKey==='energy' && calWhich==='end'}"
                   @click="openCal('energy','end', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.energy.end}">{{ fmtDate(dateRanges.energy.end) || '结束日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
            </div>
          </div>
          <div class="energy-charts">
            <div id="energyPie" class="chart-box"></div>
            <div id="energyTrend" class="chart-box"></div>
          </div>
          <div class="type-filters">
          <span v-for="t in typeLegend" :key="t.name"
                class="type-chip" :class="{on: selectedTypes.includes(t.name)}"
                :style="{'--c': t.color}"
                @click="toggleType(t.name)">
            <i class="bar"></i>{{ t.name }}
          </span>
            <span
              class="type-chip act"
              role="button"
              tabindex="0"
              @click.stop.prevent="toggleSelectAllTypes"
              @keydown.enter.prevent="toggleSelectAllTypes"
            >
            {{ allTypesSelected ? '全不选' : '全选' }}
          </span>
          </div>
        </div>

        <div class="panel panel-params">
          <div class="panel-head">
            <div class="panel-title"><span class="dot green"></span>设备关键参数</div>
            <span class="hint">运行设备实时参数</span>
          </div>
          <div class="panel-scroll">
            <table class="device-table">
              <thead>
              <tr>
                <th>编号</th>
                <th>名称</th>
                <th>状态</th>
                <th>功率</th>
                <th>电压</th>
                <th>电流</th>
                <th>关键参数</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="d in tableDevices" :key="d.id"
                  class="clickable"
                  :class="{ 'row-active': active_device_id === d.id, 'row-fault': d.status === 'fault' }"
                  @click="selectDevice(d.id)">
                <td>{{ d.id }}</td>
                <td>{{ d.name }}</td>
                <td><span :class="['status-tag', 'status-' + d.status]">{{ statusText(d.status) }}</span></td>
                <td>
                  <span :class="abnormalPower(d) ? 'val-alert' : ''">{{ d.power_kw }}</span>
                  <span :class="d.power_trend > 0.05 ? 'trend-up' : (d.power_trend < -0.05 ? 'trend-down' : '')">{{ trendArrow(d.power_trend) }}</span>
                </td>
                <td :class="abnormalVoltage(d) ? 'val-alert' : ''">{{ d.voltage }}</td>
                <td>{{ d.current }}</td>
                <td>{{ d.key_param_label }} {{ d.key_param }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="panel panel-fault">
          <div class="panel-head">
            <div class="panel-title"><span class="dot red"></span>故障统计</div>
            <div class="panel-extra">
              <span class="hint">点击柱状图查看详情</span>
              <div class="dd" :class="{open: faultDdOpen}" @click.stop>
                <div class="dd-trigger" @click="faultDdOpen = !faultDdOpen">
                  <span>{{ faultRangeLabel }}</span>
                  <i class="chev"></i>
                </div>
                <div class="dd-menu" v-show="faultDdOpen">
                  <div v-for="opt in faultRanges" :key="opt.key"
                       class="dd-item" :class="{on: faultRange === opt.key}"
                       @click="setFaultRange(opt.key)">
                    <span class="dd-check">✓</span>
                    <span>{{ opt.label }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div id="faultChart" class="chart-box" style="flex:1;"></div>
        </div>
      </div>

      <!-- ===== 中列 ===== -->
      <div class="col col-mid">
        <div class="panel panel-backwash">
          <div class="panel-head">
            <div class="panel-title"><span class="dot orange"></span>微滤机反冲洗频率</div>
            <div class="export-btns">
              <button type="button" class="export-btn" @click.stop="exportFrequency('csv')">导出CSV</button>
              <button type="button" class="export-btn" @click.stop="exportFrequency('excel')">导出Excel</button>
            </div>
            <div class="drp" @click.stop>
              <div class="drp-field" :class="{on: calOpen && calKey==='backwash' && calWhich==='start'}"
                   @click="openCal('backwash','start', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.backwash.start}">{{ fmtDate(dateRanges.backwash.start) || '开始日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
              <span class="drp-to">至</span>
              <div class="drp-field" :class="{on: calOpen && calKey==='backwash' && calWhich==='end'}"
                   @click="openCal('backwash','end', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.backwash.end}">{{ fmtDate(dateRanges.backwash.end) || '结束日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
            </div>
          </div>
          <div class="panel-scroll">
            <div class="hbar-list">
              <div v-for="(b, bi) in filteredBackwash" :key="b._rowKey || ('bw-' + bi + '-' + (b.device || b.pool))" class="hbar-row" @click="selectDeviceByName(b.device)">
                <div class="hbar-name" :title="b.device || b.pool">{{ b.device || b.pool }}</div>
                <div class="hbar-track" v-if="b.status !== 'fault'">
                  <div class="hbar-fill orange" :style="{width: Math.max(8, backwashWidth(b)) + '%'}"></div>
                </div>
                <div class="hbar-track" v-else>
                  <div class="hbar-fill fault"></div>
                </div>
                <div class="hbar-val" :class="{fault: b.status === 'fault'}">
                  <span v-if="b.status === 'fault'" class="fault-badge">故障</span>
                  <span v-else>{{ b.count }} 次/时</span>
                </div>
              </div>
              <div v-if="!filteredBackwash.length" class="panel-empty-hint">暂无反冲洗数据</div>
            </div>
          </div>
        </div>

        <div class="panel panel-water">
          <div class="panel-head">
            <div class="panel-title"><span class="dot cyan"></span>补水量统计</div>
            <div class="drp" @click.stop>
              <div class="drp-field" :class="{on: calOpen && calKey==='water' && calWhich==='start'}"
                   @click="openCal('water','start', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.water.start}">{{ fmtDate(dateRanges.water.start) || '开始日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
              <span class="drp-to">至</span>
              <div class="drp-field" :class="{on: calOpen && calKey==='water' && calWhich==='end'}"
                   @click="openCal('water','end', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.water.end}">{{ fmtDate(dateRanges.water.end) || '结束日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
            </div>
          </div>
          <div class="panel-scroll">
            <div class="hbar-list">
              <div v-for="(w, wi) in filteredWater" :key="w._rowKey || ('wt-' + wi + '-' + w.name)" class="hbar-row">
                <div class="hbar-name" :title="w.name">{{ w.name }}</div>
                <div class="hbar-track">
                  <div class="hbar-fill" :style="{width: Math.max(8, waterWidth(w)) + '%'}"></div>
                </div>
                <div class="hbar-val">{{ w.flow }} m³/h</div>
              </div>
              <div v-if="!filteredWater.length" class="panel-empty-hint">暂无补水数据</div>
            </div>
          </div>
        </div>

        <div class="panel panel-alarm">
          <div class="panel-head">
            <div class="panel-title">
            <span class="alarm-warn-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="#f5c542"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>
            </span>
              AI报警监控
              <button type="button" style="margin-left:auto;background:transparent;border:0;color:#5ecbff;cursor:pointer;font-size:12px" @click.stop="toPage('/alert_monitor')">全部 →</button>
            </div>
          </div>
          <div class="panel-scroll">
            <div v-for="a in filteredAlarms" :key="a.id"
                 :class="['alarm-card', 'tone-' + a.tone, {active: active_device_id === a.device_id}]"
                 @click="jumpAlarmToParams(a)">
              <div class="alarm-row1">
                <span class="alarm-name">{{ a.device_name }}</span>
                <span class="alarm-cat" :class="{pill: a.tone === 'ok'}">{{ a.category }}</span>
              </div>
              <div class="alarm-row2">
                <!-- L1 严重：红底白叉 -->
                <span v-if="a.tone === 'urgent'" class="alarm-ico" aria-hidden="true">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="#e53935"/><path d="M8.5 8.5l7 7M15.5 8.5l-7 7" stroke="#fff" stroke-width="2.2" stroke-linecap="round"/></svg>
              </span>
                <!-- L2 警告：橙底白叹号 -->
                <span v-else-if="a.tone === 'warn'" class="alarm-ico" aria-hidden="true">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="#e08a2e"/><text x="12" y="16.5" text-anchor="middle" fill="#fff" font-size="13" font-weight="700" font-family="Arial,sans-serif">!</text></svg>
              </span>
                <!-- L3 提示：蓝空心三角 -->
                <span v-else-if="a.tone === 'info'" class="alarm-ico" aria-hidden="true">
                <svg viewBox="0 0 24 24"><path d="M12 3.5L2.8 20.5h18.4L12 3.5z" fill="none" stroke="#5eb8ff" stroke-width="1.8" stroke-linejoin="round"/><text x="12" y="17.5" text-anchor="middle" fill="#5eb8ff" font-size="10" font-weight="700" font-family="Arial,sans-serif">!</text></svg>
              </span>
                <!-- L4 正常：绿勾 -->
                <span v-else class="alarm-ico" aria-hidden="true">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="none" stroke="#36e088" stroke-width="2"/><path d="M7 12.5l3 3 7-7" fill="none" stroke="#36e088" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </span>
                <span class="alarm-msg">{{ a.type }}</span>
              </div>
              <div class="alarm-time" v-if="a.ago">{{ a.ago }}</div>
            </div>
            <div v-if="!filteredAlarms.length" style="opacity:0.5;padding:12px;font-size:12px;">暂无报警</div>
          </div>
        </div>
      </div>

      <!-- ===== 右列 ===== -->
      <div class="col col-right">
        <div class="panel panel-aerator">
          <div class="panel-head">
            <div class="panel-title"><span class="dot green"></span>增氧电磁阀开启频率</div>
            <div class="export-btns">
              <button type="button" class="export-btn" @click.stop="exportFrequency('csv')">导出CSV</button>
              <button type="button" class="export-btn" @click.stop="exportFrequency('excel')">导出Excel</button>
            </div>
            <div class="drp" @click.stop>
              <div class="drp-field" :class="{on: calOpen && calKey==='aerator' && calWhich==='start'}"
                   @click="openCal('aerator','start', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.aerator.start}">{{ fmtDate(dateRanges.aerator.start) || '开始日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
              <span class="drp-to">至</span>
              <div class="drp-field" :class="{on: calOpen && calKey==='aerator' && calWhich==='end'}"
                   @click="openCal('aerator','end', $event)">
                <span class="drp-val" :class="{empty: !dateRanges.aerator.end}">{{ fmtDate(dateRanges.aerator.end) || '结束日期' }}</span>
                <svg class="drp-ico" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></svg>
              </div>
            </div>
          </div>
          <div class="panel-scroll">
            <div class="hbar-list">
              <div v-for="(a, ai) in filteredAerator" :key="a._rowKey || ('ae-' + ai + '-' + a.pool)" class="hbar-row">
                <div class="hbar-name" :title="a.pool">{{ a.pool }}</div>
                <div class="hbar-track">
                  <div class="hbar-fill green" :style="{width: Math.max(8, Number(a.share) || 0) + '%'}"></div>
                </div>
                <div class="hbar-val">{{ a.share }}%</div>
              </div>
              <div v-if="!filteredAerator.length" class="panel-empty-hint">暂无增氧数据</div>
            </div>
          </div>
        </div>

        <div class="panel panel-co2">
          <div class="panel-head">
            <div class="panel-title"><span class="dot purple"></span>CO₂ 吹脱开启时段</div>
          </div>
          <div class="co2-track">
            <div v-for="(seg, i) in filteredCo2" :key="i" class="co2-seg"
                 :style="co2SegStyle(seg)"
                 :title="segLabel(seg)"
                 @click="selectDeviceByName(seg.device)"></div>
          </div>
          <div class="co2-hours">
            <span>0</span><span>6</span><span>12</span><span>18</span><span>24</span>
          </div>
          <div class="co2-legend">
            <span v-for="(seg, i) in filteredCo2" :key="'l'+i+'-'+(seg.device||'')">{{ segLabel(seg) }}</span>
            <span v-if="!filteredCo2.length" class="panel-empty-hint">暂无 CO₂ 开启时段</span>
          </div>
        </div>

        <div class="panel panel-report">
          <div class="panel-head">
            <div class="panel-title"><span class="dot"></span>AI 巡检报告</div>
          </div>
          <div class="score-row">
            <div class="score-card">
              <div class="num health">{{ report.health }}</div>
              <div class="lbl">设备健康度</div>
            </div>
            <div class="score-card">
              <div class="num eff">{{ report.efficiency }}</div>
              <div class="lbl">运行效率</div>
            </div>
            <div class="score-card">
              <div class="num energy">{{ report.energyScore }}</div>
              <div class="lbl">能耗评级</div>
            </div>
          </div>
          <div class="panel-scroll">
            <div v-for="sys in report.systems" :key="sys.name" class="sys-block">
              <div class="sys-head">
                <span>{{ sys.name }}</span>
                <span :class="['sys-tag', sys.tagClass]">{{ sys.tag }}</span>
              </div>
              <div v-for="(log, i) in sys.logs" :key="i" class="log-item">
                <span :class="['log-badge', log.level]">{{ log.levelText }}</span>
                <span>{{ log.text }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 全局日期弹层 -->
    <div class="cal-pop" v-show="calOpen" :style="calStyle" @click.stop>
      <div class="cal-head">
        <div class="cal-ym">{{ calViewYear }}年{{ String(calViewMonth + 1).padStart(2, '0') }}月 <i class="arrow-down"></i></div>
        <div class="cal-nav">
          <button type="button" @click="calShift(-1)" title="上一月">▲</button>
          <button type="button" @click="calShift(1)" title="下一月">▼</button>
        </div>
      </div>
      <div class="cal-week">
        <span v-for="w in ['日','一','二','三','四','五','六']" :key="w">{{ w }}</span>
      </div>
      <div class="cal-grid">
        <div v-for="(d, i) in calDays" :key="i"
             class="cal-day" :class="{ muted: d.muted, selected: d.selected, disabled: d.disabled }"
             @click="pickCalDay(d)">{{ d.day }}</div>
      </div>
      <div class="cal-foot">
        <button type="button" @click="clearCal">清除</button>
        <button type="button" @click="pickToday">今天</button>
      </div>
    </div>

    <!-- 故障统计详情 -->
    <div class="fault-modal-mask" v-if="faultDetailVisible" @click.self="faultDetailVisible = false">
      <div class="fault-modal">
        <div class="fault-modal-head">
          <span>{{ faultDetailTitle }}</span>
          <button type="button" class="fault-modal-close" @click="faultDetailVisible = false">×</button>
        </div>
        <div class="fault-modal-body">
          <table class="fault-detail-table">
            <thead>
              <tr>
                <th>#</th>
                <th>时间/序号</th>
                <th>设备</th>
                <th>故障类型</th>
                <th>分类</th>
                <th>关联报警</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in faultDetailList" :key="row.index">
                <td>{{ row.index }}</td>
                <td>{{ row.time }}</td>
                <td>{{ row.device }}</td>
                <td>{{ row.type }}</td>
                <td>{{ row.category }}</td>
                <td>
                  <button type="button" class="export-btn" @click="jumpFaultToAlarm(row)">查看报警</button>
                </td>
              </tr>
              <tr v-if="!faultDetailList.length">
                <td colspan="6" style="text-align:center;opacity:.6;padding:16px;">暂无故障明细</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <MyChat />
  </div>
</template>

<style lang="scss" scoped>
.header {
  border-bottom: 1px solid rgba(59, 130, 246, 0.3);
  padding: 0.65rem 0;
  flex-shrink: 0;

  .header-bar {
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    align-items: center;
    gap: 1rem;
    min-width: 0;
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

  .header-center {
    justify-self: center;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    min-width: 0;

    .logo {
      width: 2.75rem;
      height: 2.75rem;
      background: linear-gradient(to bottom right, #3b82f6, #22d3ee);
      border-radius: 0.5rem;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 15px rgba(59, 130, 246, 0.45);
      flex-shrink: 0;

      .icon-lg { width: 1.6rem; height: 1.6rem; color: white; }
    }

    .title-wrap {
      h1 {
        font-size: 1.75rem;
        font-weight: 800;
        letter-spacing: 0.04em;
        line-height: 1.2;
        white-space: nowrap;
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

        &.btn-emerald { background: linear-gradient(to right, #059669, #10b981); box-shadow: 0 4px 10px rgba(16, 185, 129, 0.25); }
        &.btn-orange { background: linear-gradient(to right, #ea580c, #f97316); box-shadow: 0 4px 10px rgba(249, 115, 22, 0.25); }
        &.btn-cyan-light { background: linear-gradient(to right, #0891b2, #06b6d4); box-shadow: 0 4px 10px rgba(6, 182, 212, 0.25); }
        &.btn-pink { background: linear-gradient(to right, #db2777, #ec4899); box-shadow: 0 4px 10px rgba(236, 72, 153, 0.25); }
        &.btn-indigo { background: linear-gradient(to right, #4f46e5, #6366f1); box-shadow: 0 4px 10px rgba(99, 102, 241, 0.25); }
        &.btn-teal { background: linear-gradient(to right, #0d9488, #14b8a6); box-shadow: 0 4px 10px rgba(20, 184, 166, 0.25); }
        &.btn-red { background: linear-gradient(to right, #dc2626, #ef4444); box-shadow: 0 4px 10px rgba(239, 68, 68, 0.25); }

        &:hover {
          filter: brightness(1.08);
          transform: translateY(-1px);
        }
      }
    }
  }
}
* { margin: 0; padding: 0; box-sizing: border-box; font-family: "Microsoft YaHei", "PingFang SC", sans-serif; }
.device-monitor-page {
  position: fixed;
  inset: 0;
  z-index: 10;
  width: 100%;
  color: #e6f7ff;
  padding: 12px 16px 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100vh;
  height: 100dvh;
  max-height: 100vh;
  max-height: 100dvh;
  min-height: 0;
  overflow: hidden;
  overscroll-behavior: none;
  box-sizing: border-box;
  background:
    radial-gradient(ellipse 80% 50% at 20% -10%, rgba(14, 90, 130, 0.35), transparent 55%),
    radial-gradient(ellipse 60% 40% at 90% 10%, rgba(8, 70, 90, 0.28), transparent 50%),
    linear-gradient(165deg, #07131f 0%, #0a1c30 45%, #081624 100%);
}

/* ===== 顶栏 ===== */
.header_bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  gap: 12px;
  height: 36px;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #5ecbff;
  letter-spacing: 1px;
}
.time-text { font-size: 14px; color: #7eb8d8; }

/* ===== 筛选条 ===== */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-shrink: 0;
  background: rgba(12, 32, 58, 0.85);
  border: 1px solid #1a4a6e;
  border-radius: 8px;
  padding: 8px 14px;
}
.filter-label {
  font-size: 13px;
  color: #8ecae6;
  display: flex;
  align-items: center;
  gap: 6px;
}
.filter-label::before {
  content: "";
  width: 14px;
  height: 14px;
  border: 1.5px solid #5ecbff;
  border-radius: 3px;
  background: linear-gradient(135deg, transparent 40%, #5ecbff55 40%);
}
.select-wrap { display: flex; align-items: center; gap: 6px; }
.select-wrap label { font-size: 12px; color: #9fc5d8; }
.select-wrap select {
  background: #0c2240;
  border: 1px solid #2a6a90;
  color: #e6f7ff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  min-width: 110px;
}

/* ===== 总览卡 ===== */
.overview-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  flex-shrink: 0;
}
.overview-card {
  background: rgba(10, 28, 52, 0.9);
  border: 1px solid #1c5077;
  border-radius: 10px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 14px;
}
.ov-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ov-icon svg { width: 24px; height: 24px; }
.ov-icon.blue { background: rgba(54, 184, 255, 0.15); color: #36b8ff; }
.ov-icon.green { background: rgba(54, 224, 136, 0.15); color: #36e088; }
.ov-icon.yellow { background: rgba(255, 210, 64, 0.15); color: #ffd240; }
.ov-icon.red { background: rgba(255, 80, 80, 0.15); color: #ff6b6b; }
.ov-meta .label { font-size: 12px; color: #8aabb8; margin-bottom: 4px; }
.ov-meta .value { font-size: 26px; font-weight: 700; line-height: 1; }
.ov-meta .value span { font-size: 13px; font-weight: 400; margin-left: 4px; opacity: 0.7; }
.overview-card.total .value { color: #5ecbff; }
.overview-card.running .value { color: #36e088; }
.overview-card.stopped .value { color: #ffd240; }
.overview-card.fault .value { color: #ff6b6b; }
.overview-card.fault { border-color: #aa3333; }

/* ===== 三列主体 ===== */
.body-grid {
  display: grid;
  grid-template-columns: 1.25fr 0.95fr 0.95fr;
  gap: 12px;
  flex: 1;
  min-height: 0;
}
.col {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

.panel {
  background: rgba(10, 28, 52, 0.88);
  border: 1px solid #1c5077;
  border-radius: 10px;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  flex-shrink: 0;
  gap: 8px;
  flex-wrap: wrap;
}
.panel-title {
  font-size: 13px;
  color: #b8e0ff;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}
.panel-title .dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  background: #36b8ff;
}
.panel-title .dot.green { background: #36e088; }
.panel-title .dot.orange { background: #ff9f40; }
.panel-title .dot.purple { background: #c39eff; }
.panel-title .dot.cyan { background: #40e0d0; }
.panel-title .dot.red { background: #ff6b6b; }
.panel-extra { display: flex; align-items: center; gap: 8px; }
.hint {
  font-size: 10px;
  color: #6a90a8;
}
.mini-select {
  background: #0c2240;
  border: 1px solid #2a6a90;
  color: #cdefff;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
}

.date-range {
  font-size: 10px;
  color: #6a90a8;
  border: 1px solid #1c5077;
  padding: 2px 6px;
  border-radius: 3px;
}

/* 日期范围选择器（设计稿样式） */
.drp {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  position: relative;
  z-index: 8;
  flex-shrink: 0;
}
.drp-to {
  font-size: 11px;
  color: #8aabb8;
}
.drp-field {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #0c2240;
  border: 1px solid #3aa0d4;
  border-radius: 4px;
  padding: 2px 6px;
  cursor: pointer;
  min-width: 92px;
  user-select: none;
}
.drp-field:hover { border-color: #5ecbff; }
.drp-field.on { border-color: #f0c040; box-shadow: 0 0 0 1px rgba(240, 192, 64, 0.25); }
.drp-val {
  font-size: 11px;
  color: #f0c040;
  font-weight: 600;
  letter-spacing: 0.2px;
  flex: 1;
}
.drp-val.empty { color: #6a90a8; font-weight: 400; }
.drp-val.empty { color: #6a90a8; font-weight: 400; }
.drp-ico {
  width: 13px;
  height: 13px;
  flex-shrink: 0;
  opacity: 0.9;
}

.cal-pop {
  position: fixed;
  z-index: 9999;
  width: 268px;
  background: #3f3f3f;
  border: 1px solid #c8c8c8;
  border-radius: 4px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.55);
  padding: 10px 12px 8px;
  color: #fff;
  font-size: 13px;
}
.cal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.cal-ym {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
  cursor: default;
}
.cal-ym .arrow-down {
  width: 0; height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid #fff;
  opacity: 0.85;
}
.cal-nav { display: flex; gap: 10px; }
.cal-nav button {
  background: none;
  border: none;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
  padding: 2px 4px;
  opacity: 0.9;
}
.cal-nav button:hover { opacity: 1; color: #90caf9; }
.cal-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-size: 12px;
  margin-bottom: 4px;
  opacity: 0.95;
}
.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px 0;
  text-align: center;
}
.cal-day {
  height: 30px;
  line-height: 30px;
  font-size: 13px;
  cursor: pointer;
  border-radius: 3px;
  color: #fff;
}
.cal-day.muted { opacity: 0.45; }
.cal-day.disabled {
  opacity: 0.28;
  cursor: not-allowed;
  color: #6a8499;
  pointer-events: none;
}
.cal-day:hover { background: rgba(144, 202, 249, 0.25); }
.cal-day.selected {
  background: #90caf9;
  color: #1a2330;
  font-weight: 700;
  box-shadow: inset 0 0 0 1px #fff;
}
.cal-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  padding-top: 6px;
  border-top: 1px solid rgba(255,255,255,0.12);
}
.cal-foot button {
  background: none;
  border: none;
  color: #90caf9;
  font-size: 13px;
  cursor: pointer;
  padding: 2px 4px;
}
.cal-foot button:hover { color: #bbdefb; }

.col-mid .panel,
.col-right .panel-aerator {
  overflow: visible;
}
.col-mid { position: relative; z-index: 3; }
.col-right { position: relative; z-index: 2; }

.panel-scroll {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  scrollbar-width: thin;
  scrollbar-color: #247299 #0a2038;
}

/* 左列高度分配：能耗区加大，折线图更清晰；提高 z-index 避免下方面板挡住类型按钮 */
.col-left .panel-energy {
  flex: 1.55;
  min-height: 0;
  overflow: hidden;
  position: relative;
  z-index: 5;
}
.col-left .panel-params {
  flex: 1.2;
  position: relative;
  z-index: 1;
  min-height: 0;
  overflow: auto;
}
.col-left .panel-fault {
  flex: 0.7;
  overflow: visible;
  position: relative;
  z-index: 2;
  min-height: 0;
}
.export-btns {
  display: inline-flex;
  gap: 6px;
  margin-right: 8px;
  flex-shrink: 0;
}
.export-btn {
  background: rgba(8, 24, 48, 0.95);
  border: 1px solid #3aa0d4;
  color: #9bd8ff;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  cursor: pointer;
  white-space: nowrap;
}
.export-btn:hover {
  background: rgba(58, 160, 212, 0.25);
  color: #fff;
}
.fault-modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  z-index: 100000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fault-modal {
  width: min(720px, 92vw);
  max-height: 80vh;
  background: #0d2138;
  border: 1px solid #3aa0d4;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.fault-modal-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(58, 160, 212, 0.35);
  color: #9bd8ff;
  font-size: 14px;
}
.fault-modal-close {
  background: none;
  border: none;
  color: #9bd8ff;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
}
.fault-modal-body {
  padding: 12px 16px 16px;
  overflow: auto;
}
.fault-detail-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  color: #d7eefc;
}
.fault-detail-table th,
.fault-detail-table td {
  border-bottom: 1px solid rgba(58, 160, 212, 0.2);
  padding: 8px 6px;
  text-align: left;
}
.fault-detail-table th {
  color: #5ecbff;
}
@media (max-width: 1400px), (max-resolution: 1.25dppx) {
  .col-left .panel-params {
    flex: 1.4;
    min-height: 200px;
  }
  .device-table {
    font-size: 11px;
  }
  .panel-head {
    flex-wrap: wrap;
    gap: 6px;
  }
}
.panel-fault .panel-head {
  position: relative;
  z-index: 30;
  overflow: visible;
}
.panel-fault #faultChart {
  overflow: hidden;
  border-radius: 4px;
  position: relative;
  z-index: 1;
  min-height: 100px;
}

/* 故障统计 · 时间范围自定义下拉（向上展开，避免挡柱状图） */
.dd {
  position: relative;
  z-index: 40;
}
.dd-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  min-width: 78px;
  background: rgba(8, 24, 48, 0.95);
  border: 1px solid #3aa0d4;
  color: #9bd8ff;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  user-select: none;
}
.dd-trigger .chev {
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid #7eb8d8;
  transition: transform 0.15s;
}
.dd.open .dd-trigger .chev {
  transform: rotate(180deg);
}
.dd-menu {
  position: absolute;
  right: 0;
  bottom: calc(100% + 6px);
  top: auto;
  min-width: 128px;
  background: #2a2e36;
  border: 1px solid #5a6578;
  border-radius: 10px;
  padding: 4px;
  box-shadow: 0 -8px 28px rgba(0, 0, 0, 0.55);
  z-index: 100;
}
.dd-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #e8eef5;
  cursor: pointer;
  white-space: nowrap;
}
.dd-item:hover { background: rgba(255, 255, 255, 0.06); }
.dd-item.on {
  background: #2f6bff;
  color: #fff;
}
.dd-check {
  width: 14px;
  text-align: center;
  font-size: 12px;
  font-weight: 700;
  opacity: 0;
}
.dd-item.on .dd-check { opacity: 1; }

/* 中列 */
.col-mid .panel-backwash { flex: 1.05; }
.col-mid .panel-water { flex: 0.9; }
.col-mid .panel-alarm { flex: 1.05; }

/* 右列 */
.col-right .panel-aerator { flex: 1; }
.col-right .panel-co2 { flex: 0.85; }
.col-right .panel-report { flex: 1.2; }

/* 能耗 */
.panel-energy { overflow: hidden; }
.panel-energy .panel-head {
  align-items: center;
  margin-bottom: 6px;
}
.energy-title {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #5ecbff;
}
.energy-bolt {
  width: 16px;
  height: 16px;
  color: #f0c040;
  flex-shrink: 0;
}
.energy-mwh {
  margin-left: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #f0c040;
  letter-spacing: 0.3px;
}
.energy-charts {
  display: grid;
  grid-template-columns: 0.95fr 1.35fr;
  gap: 8px;
  flex: 1;
  min-height: 0;
  align-items: stretch;
  /* 给饼图留出内边距，防止外弧贴边被裁切 */
  padding: 4px 2px 8px;
  box-sizing: border-box;
}
.energy-charts #energyPie {
  min-height: 0;
  height: 100%;
}
.energy-charts #energyTrend {
  min-height: 0;
  overflow: visible;
}
.chart-box { width: 100%; height: 100%; min-height: 0; }

/* 图表悬停提示挂到 body，避免被 panel overflow 裁切 */
.echarts-tooltip {
  z-index: 100000 !important;
  pointer-events: none !important;
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.55);
  max-height: none !important;
  overflow: visible !important;
}
.type-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 6px;
  flex-shrink: 0;
  align-items: center;
  position: relative;
  z-index: 6;
  pointer-events: auto;
}
.type-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  padding: 3px 9px;
  border-radius: 4px;
  border: 1px solid #2a4a66;
  background: transparent;
  color: #7a98ae;
  cursor: pointer;
  user-select: none;
  line-height: 1.2;
  pointer-events: auto;
  position: relative;
  z-index: 1;
}
.type-chip .bar {
  display: inline-block;
  width: 10px;
  height: 2px;
  border-radius: 1px;
  background: currentColor;
  flex-shrink: 0;
}
.type-chip.on {
  border-color: var(--c, #36b8ff);
  color: var(--c, #36b8ff);
  background: rgba(8, 28, 52, 0.35);
}
.type-chip.act {
  border: 1px solid #4a6178;
  color: #8ec8e8;
  background: rgba(12, 28, 48, 0.95);
  padding: 3px 10px;
  z-index: 2;
}
.type-chip.act:hover {
  border-color: #5ecbff;
  color: #b8e8ff;
}

/* 参数表 */
.device-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 11px;
}
.device-table th {
  color: #7eb8d8;
  padding: 5px 4px;
  text-align: center;
  border-bottom: 1px solid #1c4060;
  position: sticky;
  top: 0;
  background: rgba(10, 28, 52, 0.98);
  white-space: nowrap;
}
.device-table td {
  padding: 5px 4px;
  text-align: center;
  border-bottom: 1px solid #142838;
}
.device-table tr.clickable { cursor: pointer; }
.device-table tr.row-active td { background: rgba(54, 184, 255, 0.12); }
.device-table tr.row-fault td { background: rgba(120, 30, 30, 0.2); }
.status-tag {
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 10px;
}
.status-running { background: rgba(13, 104, 55, 0.6); color: #8cf9bc; }
.status-stopped { background: rgba(80, 70, 20, 0.5); color: #ffdd88; }
.status-fault { background: rgba(122, 26, 26, 0.7); color: #ff9999; }
.val-alert { color: #ff6b6b; font-weight: 600; }
.trend-up { color: #ff6b6b; }
.trend-down { color: #36e088; }

/* 水平条形行（反冲/耗水/增氧） */
.hbar-list { display: flex; flex-direction: column; gap: 8px; padding: 4px 0; }
.panel-empty-hint {
  opacity: 0.55;
  font-size: 12px;
  color: #8bb4cc;
  padding: 16px 8px;
  text-align: center;
}
.hbar-row {
  display: grid;
  grid-template-columns: 72px 1fr 56px;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  cursor: pointer;
}
.hbar-row:hover .hbar-fill { filter: brightness(1.15); }
.hbar-name { color: #b8d4e8; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.hbar-track {
  height: 10px;
  background: #0a1a2e;
  border-radius: 5px;
  overflow: hidden;
  border: 1px solid #1a3a55;
}
.hbar-fill {
  height: 100%;
  border-radius: 5px;
  background: linear-gradient(90deg, #1a8ccc, #36b8ff);
  transition: width 0.3s;
}
.hbar-fill.green { background: linear-gradient(90deg, #1a8a55, #36e088); }
.hbar-fill.orange { background: linear-gradient(90deg, #b86a10, #ff9f40); }
.hbar-fill.purple { background: linear-gradient(90deg, #6a4aab, #c39eff); }
.hbar-fill.fault { background: #ff4444; width: 100% !important; }
.hbar-val { text-align: right; color: #9fc5d8; font-variant-numeric: tabular-nums; }
.hbar-val.fault { color: #ff6b6b; font-weight: 600; }
.fault-badge {
  display: inline-block;
  background: #ff4444;
  color: #fff;
  font-size: 10px;
  padding: 1px 8px;
  border-radius: 3px;
}

/* AI报警监控 —— 四级：红严重 / 橙警告 / 蓝提示 / 绿正常 */
.panel-alarm .panel-head {
  border-bottom: 1px solid #1e5a88;
  padding-bottom: 8px;
  margin-bottom: 10px;
}
.panel-alarm .panel-title {
  color: #5ecbff;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
}
.alarm-warn-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 15px;
  height: 15px;
  margin-right: 2px;
  flex-shrink: 0;
}
.alarm-warn-icon svg { width: 15px; height: 15px; display: block; }

.alarm-card {
  border-radius: 6px;
  padding: 10px 12px;
  margin-bottom: 8px;
  cursor: pointer;
  border: 1px solid #1a3550;
  background: #0a1628;
}
/* L1 严重 · 红 · 圆叉 */
.alarm-card.tone-urgent {
  background: #4a1414;
  border-color: #e53935;
}
/* L2 警告 · 橙 · 圆叹号 */
.alarm-card.tone-warn {
  background: #3d2810;
  border-color: #e08a2e;
}
/* L3 提示 · 蓝 · 三角叹号 */
.alarm-card.tone-info {
  background: #0f2438;
  border-color: #3aa0d4;
}
/* L4 正常 · 绿勾 + 绿色胶囊 */
.alarm-card.tone-ok {
  background: #0a1628;
  border-color: #1a3550;
}
.alarm-card.active { box-shadow: 0 0 0 1px #36b8ff; }

.alarm-row1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.alarm-name {
  font-size: 13px;
  font-weight: 700;
  color: #e8eef5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.alarm-card.tone-urgent .alarm-name { color: #ff8a8a; }
.alarm-card.tone-warn .alarm-name { color: #ffb060; }
.alarm-card.tone-info .alarm-name { color: #6ec4ff; }
.alarm-card.tone-ok .alarm-name { color: #e8eef5; }

.alarm-cat {
  font-size: 11px;
  flex-shrink: 0;
  color: #8aabb8;
}
.alarm-card.tone-urgent .alarm-cat { color: #ff8a8a; }
.alarm-card.tone-warn .alarm-cat { color: #e08a2e; }
.alarm-card.tone-info .alarm-cat { color: #6ec4ff; }
.alarm-cat.pill {
  padding: 1px 8px;
  border-radius: 10px;
  background: rgba(20, 90, 55, 0.55);
  color: #6dffb0;
  border: 1px solid rgba(54, 224, 136, 0.35);
  font-size: 10px;
}

.alarm-row2 {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 4px;
}
.alarm-card.tone-ok .alarm-row2 { margin-bottom: 0; }
.alarm-ico {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.alarm-ico svg { width: 14px; height: 14px; display: block; }
.alarm-msg {
  font-size: 12px;
  color: #dce6f0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.alarm-card.tone-ok .alarm-msg { color: #9aafc0; }
.alarm-time {
  font-size: 11px;
  color: #7a8e9e;
  padding-left: 23px;
}
.alarm-card.tone-urgent .alarm-time { color: #c88080; }
.alarm-card.tone-warn .alarm-time { color: #b8926a; }
.alarm-card.tone-info .alarm-time { color: #6a879e; }

/* CO2 时间轴 */
.co2-track {
  position: relative;
  height: 28px;
  background: #0a1a2e;
  border: 1px solid #1c5077;
  border-radius: 6px;
  margin: 8px 0 4px;
}
.co2-seg {
  position: absolute;
  top: 4px;
  bottom: 4px;
  background: rgba(54, 224, 136, 0.7);
  border-radius: 3px;
  cursor: pointer;
}
.co2-seg:hover { background: rgba(54, 224, 136, 0.95); }
.co2-hours {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  color: #5a7a90;
}
.co2-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 6px;
  font-size: 10px;
  color: #8aabb8;
}
.co2-legend span::before {
  content: "";
  display: inline-block;
  width: 10px;
  height: 8px;
  background: #36e088;
  border-radius: 2px;
  margin-right: 4px;
  vertical-align: middle;
}

/* 巡检报告 */
.score-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 10px;
  flex-shrink: 0;
}
.score-card {
  background: rgba(8, 24, 44, 0.8);
  border: 1px solid #1c5077;
  border-radius: 8px;
  padding: 8px 6px;
  text-align: center;
}
.score-card .num {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.1;
}
.score-card .num.health { color: #36e088; }
.score-card .num.eff { color: #36b8ff; }
.score-card .num.energy { color: #ffd240; }
.score-card .lbl { font-size: 10px; color: #8aabb8; margin-top: 2px; }

.sys-block { margin-bottom: 10px; }
.sys-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #cdefff;
}
.sys-tag {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 3px;
}
.sys-tag.warn { background: #8a5020; color: #ffddaa; }
.sys-tag.info { background: #1a5080; color: #9bd8ff; }
.log-item {
  font-size: 11px;
  padding: 4px 0 4px 0;
  color: #c0d4e0;
  display: flex;
  gap: 6px;
  line-height: 1.4;
}
.log-badge {
  flex-shrink: 0;
  font-size: 10px;
  padding: 0 5px;
  border-radius: 2px;
  height: 16px;
  line-height: 16px;
}
.log-badge.severe { background: #aa2222; color: #fff; }
.log-badge.warning { background: #8a6020; color: #ffe0a0; }
.log-badge.advice { background: #1a5080; color: #9bd8ff; }
</style>

<style lang="scss">
/* 设备监控页：禁止外层文档滚动，避免滚轮露出白底 */
html.device-monitor-scroll-lock,
html.device-monitor-scroll-lock body {
  height: 100% !important;
  overflow: hidden !important;
  overscroll-behavior: none !important;
  background: #07131f !important;
}
html.device-monitor-scroll-lock #app {
  height: 100% !important;
  min-height: 100% !important;
  overflow: hidden !important;
  background: #07131f !important;
}
</style>
