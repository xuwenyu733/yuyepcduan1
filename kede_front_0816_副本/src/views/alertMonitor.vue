<script>
import $ from 'jquery'
import '@/utils/platformAlarmData'
import { createInspectionPlanTask, ROUTES, syncOpenSystemAlarmsToMission, pushToMissionPlanning, getWiiduApiBase, markAlarmUrgentLocal, applyAlarmUrgentOverride } from '@/utils/alarmTaskBridge'
import { getThresholdModule, resolveAlarmRules } from '@/utils/thresholdConfig'
import { resolveOperatorUser } from '@/utils/backendUserId'
import { normalizeSiteRecord, workshopsEqual, systemsEqual, pondsEqual } from '@/utils/siteNaming'
import {
  loadSharedSiteOptions as fetchSiteOptions,
  peekSharedSiteOptions,
  pickWorkshopOptionLabels,
  pickSystemOptionLabels,
  pickPondOptionLabels,
  isAlarmInUserSiteScope,
  isUserDeptScopeDenied,
  isPlatformUnscopedAdmin,
  SITE_OPTIONS_CHANGED_EVENT
} from '@/utils/siteOptions'
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope'
import { clearZkAlarmCache } from '@/utils/zkAlarmShared'
import MyChat from '@/components/MyChat/index'
import PageLoadingMask from '@/components/PageLoadingMask/index'
import * as echarts from "echarts";
var API_BASE_URL = getWiiduApiBase();

function baseRequest(url, method, data, options) {
  options = options || {};
  return new Promise(function (resolve, reject) {
    $.ajax({
      url: API_BASE_URL + String(url).replace(/^\/+/, ''),
      type: (method || 'GET').toUpperCase(),
      contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
      dataType: 'json',
      timeout: options.timeout === 0 ? 0 : (options.timeout || 8000),
      data: JSON.stringify(data || {}),
      success: function (raw) {
        if (options.noVerify) {
          resolve(raw);
          return;
        }
        var body = raw && raw.data !== undefined ? raw.data : raw;
        if (!body) {
          reject(new Error('后端未返回数据'));
          return;
        }
        if (body.status === undefined || Number(body.status) === 200) resolve(body);
        else reject(new Error(body.message || '后端请求失败'));
      },
      error: function (xhr, textStatus) {
        var statusText = xhr.status ? 'HTTP ' + xhr.status : '未收到 HTTP 响应';
        var message = '请求失败：' + (textStatus || '网络错误') + '（' + statusText + '）';
        try {
          var body = JSON.parse(xhr.responseText);
          message = body.message || body.error || message;
          if (body.detail) message += '：' + body.detail;
        } catch (ignore) {}
        reject(new Error(message));
      }
    });
  });
}

var request = {};
['get', 'post', 'put', 'delete'].forEach(function (method) {
  request[method] = function (api, data, options) {
    return baseRequest(api, method, data, options);
  };
});

global.API_BASE_URL = API_BASE_URL;
global.request = request;
function loadEchartsWithFallback() {
  // loadNext();
  window.__echartsReady = true;
}
loadEchartsWithFallback();
var PAGE_CONFIG = (function () {
  var icons = {
    warn: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13" stroke-linecap="round"/><line x1="12" y1="17" x2="12.01" y2="17" stroke-linecap="round"/></svg>',
    info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
    check: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M9 12l2 2 4-4"/></svg>',
    fish: '<path d="M12 4c-2.5 2-7 3.5-9 6 2 2.5 6.5 4 9 6 2.5-2 7-3.5 9-6-2-2.5-6.5-4-9-6zm0 4.5a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3z"/>',
    water: '<path d="M12 2.7C12 2.7 5 10.1 5 14.5A7 7 0 0 0 12 21a7 7 0 0 0 7-6.5C19 10.1 12 2.7 12 2.7z"/>',
    gear: '<path d="M19.14 12.94c.04-.31.06-.63.06-.94s-.02-.63-.06-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.49.49 0 0 0-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54a.48.48 0 0 0-.48-.41h-3.84a.48.48 0 0 0-.48.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96a.49.49 0 0 0-.59.22L2.74 8.87a.48.48 0 0 0 .12.61l2.03 1.58c-.04.31-.06.63-.06.94s.02.63.06.94l-2.03 1.58a.49.49 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.48-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32a.49.49 0 0 0-.12-.61l-2.03-1.58zM12 15.6A3.6 3.6 0 1 1 12 8.4a3.6 3.6 0 0 1 0 7.2z"/>',
    person: '<path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8V22h19.2v-2.8c0-3.2-6.4-4.8-9.6-4.8z"/>'
  };
  return {
    icons: icons,
    aiIcons: {
      warn: '<svg viewBox="0 0 24 24"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>',
      wave: '<svg viewBox="0 0 24 24"><path d="M2 12c2-2 4-2 6 0s4 2 6 0 4-2 6 0v2c-2-2-4-2-6 0s-4 2-6 0-4-2-6 0v-2zm0 5c2-2 4-2 6 0s4 2 6 0 4-2 6 0v2c-2-2-4-2-6 0s-4 2-6 0-4-2-6 0v-2z"/></svg>',
      chart: '<svg viewBox="0 0 24 24"><path d="M3 3v18h18v-2H5V3H3zm14 10h2v6h-2v-6zm-4-4h2v10h-2V9zm-4 2h2v8h-2v-8zm-4 3h2v5H5v-5z"/></svg>',
      star: '<svg viewBox="0 0 24 24"><path d="M12 2l2.4 7.4H22l-6 4.6 2.3 7-6.3-4.6L5.7 21l2.3-7-6-4.6h7.6z"/></svg>',
      eye: '<svg viewBox="0 0 24 24"><path d="M12 5C7 5 2.7 8.1 1 12c1.7 3.9 6 7 11 7s9.3-3.1 11-7c-1.7-3.9-6-7-11-7zm0 12a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-8a3 3 0 1 0 0 6 3 3 0 0 0 0-6z"/></svg>'
    },
    metaSvg: {
      location: '<svg viewBox="0 0 24 24"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5z"/></svg>',
      owner: '<svg viewBox="0 0 24 24"><path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8V22h19.2v-2.8c0-3.2-6.4-4.8-9.6-4.8z"/></svg>',
      time: '<svg viewBox="0 0 24 24"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm1 11H7v-2h4V6h2v7z"/></svg>'
    },
    statusMap: {
      pending: '未接收',
      done: '已完成',
      exec: '执行中',
      overdue: '已逾期'
    },
    levelMap: {
      urgent: '紧急',
      priority: '优先',
      daily: '日常'
    },
    statusOptions: [
      { value: 'pending', label: '未接收', activeCls: 'active-pending' },
      { value: 'exec', label: '执行中', activeCls: 'active-exec' },
      { value: 'done', label: '已完成', activeCls: 'active-done' },
      { value: 'overdue', label: '已逾期', activeCls: 'active-overdue' }
    ],
    categoryMeta: {
      fish: { name: '鱼类', color: '#5ec8ff', icon: icons.fish },
      water: { name: '水质', color: '#36cfc9', icon: icons.water },
      equip: { name: '设备', color: '#e6a23c', icon: icons.gear },
      person: { name: '人员', color: '#c678dd', icon: icons.person }
    },
    defaultFilters: { workshop: '全部', system: '全部', pool: '全部', time: '全部' }
  };
})();
export default {
  components: { MyChat, PageLoadingMask },
  data() {
    return {
      config: PAGE_CONFIG,
      lastRefresh: '--',
      dataMode: 'loading',
      dataError: '',
      loading: false,
      savingAlarmCode: '',
      toastText: '',
      filterLevel: 'all',
      filterStatus: 'all',
      trendRange: '24h',
      customStart: '',
      customEnd: '',
      selectedPeriodIndex: null,
      selectedTimeRange: null,
      openDd: null,
      chart: null,
      operator: { id: null, name: '\u5f53\u524d\u64cd\u4f5c\u5458' },
      locations: [],
      tasks: [],
      thresholds: [],
      statusOptions: PAGE_CONFIG.statusOptions.slice(),
      stats: [
        { key: 'pending', label: '未接收', count: 0, pct: 0, tone: 'gray' },
        { key: 'done', label: '已完成', count: 0, pct: 0, tone: 'green' },
        { key: 'exec', label: '执行中', count: 0, pct: 0, tone: 'yellow' },
        { key: 'overdue', label: '已逾期', count: 0, pct: 0, tone: 'red' }
      ],
      levelFilters: [
        { value: 'all', label: '全部', count: 0, pct: 100, cls: '' },
        { value: 'urgent', label: '紧急', count: 0, pct: 0, cls: 'lv-urgent' },
        { value: 'priority', label: '优先', count: 0, pct: 0, cls: 'lv-priority' },
        { value: 'daily', label: '日常', count: 0, pct: 0, cls: 'lv-daily' }
      ],
      statusFilters: [
        { value: 'all', label: '全部', count: 0 },
        { value: 'pending', label: '未接收', count: 0 },
        { value: 'done', label: '已完成', count: 0 },
        { value: 'exec', label: '执行中', count: 0 },
        { value: 'overdue', label: '已逾期', count: 0 }
      ],
      catFilters: [
        { key: 'workshop', label: '车间' },
        { key: 'system', label: '系统' },
        { key: 'pool', label: '鱼池' },
        { key: 'time', label: '时间' }
      ],
      trendRanges: [
        { value: '6h', label: '近6小时' },
        { value: '12h', label: '近12小时' },
        { value: '24h', label: '近24小时' },
        { value: 'custom', label: '自定义' }
      ],
      categories: [],
      aiInsights: [],
      displayMetrics: [],
      alarmRules: {
        overdue: { urgent: 15, priority: 60, routine: 240 },
        dense: { count: 5, window: 30, enabled: true },
        multi: { value: 3, enabled: true }
      },
      sharedSiteOptions: peekSharedSiteOptions() || { workshops: [], systems: [], ponds: [], workshopOptions: [], systemOptions: [], pondOptions: [] },
    };
  },

  computed: {
    trendPack: function () {
      return this.buildTrendPack();
    },
    trendAutoExpanded: function () {
      return !!(this.trendPack && this.trendPack.autoExpanded);
    },
    displayMetricGroups: function () {
      return this.displayMetrics || [];
    },
    trendLegend: function () {
      var pack = this.trendPack;
      return {
        urgent: pack.urgent.reduce(function (sum, value) { return sum + value; }, 0),
        priority: pack.priority.reduce(function (sum, value) { return sum + value; }, 0),
        daily: pack.daily.reduce(function (sum, value) { return sum + value; }, 0)
      };
    },
    overdueUrgentCount: function () {
      return this.scopedAlerts().filter(function (item) {
        return item.level === 'urgent' && item.status === 'overdue';
      }).length;
    },
    selectedPeriod: function () {
      var self = this;
      if (this.selectedPeriodIndex === null) return null;
      var bucket = this.trendPack.buckets[this.selectedPeriodIndex];
      if (!bucket) return null;
      return {
        index: this.selectedPeriodIndex,
        value: bucket.alerts.filter(function (item) { return item.level === 'urgent'; }).length,
        label: bucket.label,
        sev: 'urgent',
        alerts: bucket.alerts.filter(function (item) { return item.level === 'urgent'; }).map(function (item) {
          var meta = self.config.categoryMeta[item.category] || { name: item.category, color: '#5ec8ff', icon: '' };
          return Object.assign({}, item, {
            catName: meta.name,
            color: meta.color,
            icon: meta.icon
          });
        })
      };
    }
  },

  created: function () {
    this.customStart = this.defaultDate(-1);
    this.customEnd = this.defaultDate(0);
    this.operator = this.currentOperator();
    this.categories = this.buildCategories();
  },

  mounted: function () {
    var self = this;
    this.applyRouteQuery();
    this.prepareDemoData();
    this.recalcStats();
    this.rebuildAiInsights();
    this.tickRefreshTime();
  this.loadSharedSiteOptions(false);
    this.loadAlarmThresholds();
    this.loadData(false);
    this.$nextTick(function () { self.waitEchartsThenRender(); });
    document.addEventListener('click', this.closeDd);
    window.addEventListener('resize', this.onResize);
    this._onSiteOptionsChanged = function () {
      self.loadSharedSiteOptions(true).finally(function () {
        // 筛选项晚到时补刷，避免首屏被空白名单滤成「暂无匹配告警」
        self.loadData(false);
      });
    };
    window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    this._onUserScopeChanged = function () {
      clearZkAlarmCache();
      self.categories = self.emptyCategories();
      self.recalcStats();
      self.loadSharedSiteOptions(true).finally(function () {
        self.loadData(true);
      });
    };
    window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    this._refreshTimer = setInterval(function () { self.loadData(false); }, 15000);
  },

  activated: function () {
    this.loadSharedSiteOptions(true);
  },

  watch: {
    '$route.query': {
      deep: true,
      handler: function () {
        this.applyRouteQuery();
      }
    }
  },

  beforeDestroy: function () {
    document.removeEventListener('click', this.closeDd);
    window.removeEventListener('resize', this.onResize);
    if (this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    if (this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    }
    if (this._refreshTimer) clearInterval(this._refreshTimer);
    if (this._echartsTimer) clearTimeout(this._echartsTimer);
    if (this._rz) clearTimeout(this._rz);
    if (this._toastTimer) clearTimeout(this._toastTimer);
    if (this._chartRo) {
      try { this._chartRo.disconnect(); } catch (ignore) {}
    }
    if (this.chart) this.chart.dispose();
  },

  methods: {
    loadAlarmThresholds: async function () {
      try {
        var mod = await getThresholdModule('alarm', true);
        this.alarmRules = resolveAlarmRules(mod);
      } catch (e) {
        console.warn('[AI报警] 阈值加载失败，使用默认时限', e && e.message);
      }
    },
    applyAlarmOverdueByThreshold: function (alarm) {
      if (!alarm || alarm.status === 'done') return alarm;
      var overdueMap = (this.alarmRules && this.alarmRules.overdue) || {};
      var minutes = overdueMap[alarm.level] != null
        ? overdueMap[alarm.level]
        : (alarm.level === 'urgent' ? 15 : alarm.level === 'priority' ? 60 : 240);
      var ts = alarm.alarmTime || alarm.time || alarm.createdAt;
      var d = this.parseDate(ts);
      if (!d || !Number.isFinite(minutes)) return alarm;
      if (Date.now() - d.getTime() > minutes * 60 * 1000) {
        if (alarm.status === 'pending' || alarm.status === 'exec') {
          alarm.status = 'overdue';
          alarm.backendStatus = alarm.backendStatus || 'overdue';
        }
      }
      return alarm;
    },
    toPage(path) {
      this.$router.push({ path: path }).catch(() => {});
    },
    goFishDiagnosis(item) {
      var pondCode = '';
      if (item) {
        pondCode = item.poolCode || item.pool_code || '';
        if (!pondCode && item.location) {
          pondCode = String(item.location).replace(/鱼?池/g, '').trim();
        }
      }
      var query = { tab: 'diagnosis', from: '/alert_monitor' };
      if (pondCode) query.pondCode = pondCode;
      this.$router.push({ path: '/fish-disease', query: query }).catch(function () {});
    },
    applyRouteQuery: function () {
      // 业务报警列表已移除；路由 query（source/category/pond）仅作跳转上下文保留
    },
    goTaskPlan: function () {
      this.toPage(ROUTES.TASK);
    },
    goInspectList: function () {
      this.toPage(ROUTES.INSPECT);
    },
    pad2: function (n) {
      n = String(n);
      return n.length < 2 ? '0' + n : n;
    },

    formatSlashDate: function (iso) {
      if (!iso) return '--/--/--';
      return String(iso).replace(/-/g, '/');
    },

    pointColor: function (v) {
      if (v >= 6) return '#ff4d4f';
      if (v >= 4) return '#faad14';
      return '#52c41a';
    },

    createAlert: function (id, title, level, status, location, owner, time, reason, aiAdvice, solution) {
      return {
        id: id,
        title: title,
        level: level,
        status: status,
        location: location,
        owner: owner,
        time: time,
        reason: reason,
        detail: reason,
        aiAdvice: aiAdvice,
        solution: solution || '',
        _open: false,
        _editing: false,
        _draft: ''
      };
    },

    buildCategories: function () {
      var emptyFilters = Object.assign({}, this.config.defaultFilters);
      return [
        {
          key: 'fish',
          title: '鱼类监控',
          icon: this.config.icons.fish,
          badgeRed: 0,
          badgeYellow: 0,
          badgeGreen: 0,
          filters: Object.assign({}, emptyFilters),
          alerts: []
        },
        {
          key: 'water',
          title: '水质监控',
          icon: this.config.icons.water,
          badgeRed: 0,
          badgeYellow: 0,
          badgeGreen: 0,
          filters: Object.assign({}, emptyFilters),
          alerts: []
        },
        {
          key: 'equip',
          title: '设备监控',
          icon: this.config.icons.gear,
          badgeRed: 0,
          badgeYellow: 0,
          badgeGreen: 0,
          filters: Object.assign({}, emptyFilters),
          alerts: []
        },
        {
          key: 'person',
          title: '人员监控',
          icon: this.config.icons.person,
          badgeRed: 0,
          badgeYellow: 0,
          badgeGreen: 0,
          filters: Object.assign({}, emptyFilters),
          alerts: []
        }
      ];
    },

    defaultDate: function (offsetDays) {
      var d = new Date(Date.now() + (offsetDays || 0) * 86400000);
      return d.getFullYear() + '-' + this.pad2(d.getMonth() + 1) + '-' + this.pad2(d.getDate());
    },

    parseDate: function (value) {
      if (value === null || value === undefined || value === '') return null;
      if (value instanceof Date) return isNaN(value.getTime()) ? null : value;
      // 数字时间戳：秒 / 毫秒
      if (typeof value === 'number' && isFinite(value)) {
        var ms = value < 1e12 ? value * 1000 : value;
        var fromNum = new Date(ms);
        return isNaN(fromNum.getTime()) ? null : fromNum;
      }
      // Java LocalDateTime 序列化为数组 [y, m, d, h, mi, s, nano]
      if (Object.prototype.toString.call(value) === '[object Array]' && value.length >= 3) {
        var fromArr = new Date(
          Number(value[0]),
          Number(value[1]) - 1,
          Number(value[2]),
          Number(value[3] || 0),
          Number(value[4] || 0),
          Number(value[5] || 0)
        );
        return isNaN(fromArr.getTime()) ? null : fromArr;
      }
      var raw = String(value).trim();
      if (!raw) return null;
      if (/^\d+$/.test(raw)) {
        return this.parseDate(Number(raw));
      }
      // 兼容 ISO / "yyyy-MM-dd HH:mm:ss" / 带 T
      var normalized = raw
        .replace('T', ' ')
        .replace(/\.\d+Z?$/, '')
        .replace(/-/g, '/');
      var date = new Date(normalized);
      if (isNaN(date.getTime())) date = new Date(raw);
      return isNaN(date.getTime()) ? null : date;
    },

    htmlEscape: function (value) {
      return String(value == null ? '' : value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
    },

    relativeTime: function (value) {
      var date = this.parseDate(value);
      if (!date) return value || '--';
      var diff = Math.max(0, Date.now() - date.getTime());
      var minutes = Math.floor(diff / 60000);
      if (minutes < 1) return '刚刚';
      if (minutes < 60) return minutes + '分钟前';
      if (minutes < 1440) return Math.floor(minutes / 60) + '小时前';
      if (minutes < 2880) return '昨天 ' + this.pad2(date.getHours()) + ':' + this.pad2(date.getMinutes());
      return date.getFullYear() + '-' + this.pad2(date.getMonth() + 1) + '-' + this.pad2(date.getDate()) +
        ' ' + this.pad2(date.getHours()) + ':' + this.pad2(date.getMinutes());
    },

    backendLevel: function (value) {
      if (value === 'urgent') return 'urgent';
      if (value === 'priority' || value === 'warning') return 'priority';
      return 'daily';
    },

    backendStatus: function (value) {
      if (value === 'executing') return 'exec';
      if (value === 'completed' || value === 'handled') return 'done';
      if (value === 'overdue') return 'overdue';
      return 'pending';
    },

    databaseStatus: function (value) {
      if (value === 'exec') return 'executing';
      if (value === 'done') return 'completed';
      if (value === 'overdue') return 'overdue';
      return 'unreceived';
    },

    fallbackAdvice: function (category) {
      var map = {
        fish: '复核鱼群行为、生长及摄食数据，并安排现场检查。',
        water: '复核传感器读数并按水质处置规程逐步调节。',
        device: '检查设备供电、运行参数和故障码，必要时切换备用设备。',
        person: '核实人员身份与现场操作记录，按安全流程处置。'
      };
      return map[category] || '请按现场标准作业流程核实并完成处置。';
    },

    emptyCategories: function () {
      var defaultFilters = this.config.defaultFilters;
      return this.buildCategories().map(function (category) {
        return Object.assign({}, category, {
          filters: Object.assign({ pool: '全部' }, defaultFilters),
          alerts: [],
          badgeRed: 0,
          badgeYellow: 0,
          badgeGreen: 0
        });
      });
    },

    currentOperator: function () {
      return resolveOperatorUser(this, '当前操作员');
    },

    prepareDemoData: function () {
      // 演示数据仅限平台级超管可见；部门/空白范围账号不得预置「他部门样例报警」，
      // 避免后端返回前闪现不属于其所属部门的报警。
      if (!isPlatformUnscopedAdmin()) return;
      var self = this;
      this.categories = this.categories.map(function (category) {
        var alerts = category.alerts.map(function (item, index) {
          return normalizeSiteRecord(Object.assign({}, item, {
            category: category.key,
            alarmCode: item.id,
            alarm_code: item.id,
            backendStatus: self.databaseStatus(item.status),
            alarm_status: self.databaseStatus(item.status),
            alarmTime: new Date(Date.now() - (index + 1) * 45 * 60000).toISOString(),
            sourceTaskCode: ''
          }));
        });
        return Object.assign({}, category, { alerts: alerts });
      });
    },

    showToast: function (text) {
      var self = this;
      this.toastText = text;
      if (this._toastTimer) clearTimeout(this._toastTimer);
      this._toastTimer = setTimeout(function () { self.toastText = ''; }, 2600);
    },

    tickRefreshTime: function (serverTime) {
      var d = this.parseDate(serverTime) || new Date();
      this.lastRefresh = d.getFullYear() + '-' + this.pad2(d.getMonth() + 1) + '-' + this.pad2(d.getDate()) +
        ' ' + this.pad2(d.getHours()) + ':' + this.pad2(d.getMinutes());
    },

    loadData: async function (manual) {
      if (this.loading) return;
      if (!manual && this.hasEditingAlarm()) return;
      var self = this;
      this.loading = true;
      this.dataError = '';
      try {
        if (!window.PlatformAlarmData) throw new Error('报警数据服务未加载');
        var payload = await Promise.race([
          window.PlatformAlarmData.loadAll(!!manual),
          new Promise(function (_, reject) {
            setTimeout(function () { reject(new Error('报警加载超时，请刷新重试')); }, 8000);
          })
        ]);
        this.applyBackendData(payload);
        this.dataMode = 'database';
        this.dataError = (payload._errors || []).join('；');
        this.tickRefreshTime(payload.serverTime);
        // 后端创建的报警（精准投喂/生长模型等）补写入任务与计划，节流避免 15s 轮询反复打库
        var now = Date.now();
        if (!this._lastMissionSyncAt || now - this._lastMissionSyncAt > 120000) {
          this._lastMissionSyncAt = now;
          syncOpenSystemAlarmsToMission().catch(function (err) {
            console.warn('syncOpenSystemAlarmsToMission', err);
          });
        }
        if (manual) this.showToast('数据库数据已刷新');
      } catch (error) {
        this.dataError = error && error.message ? error.message : String(error);
        if (this.dataMode !== 'database') this.dataMode = 'demo';
        if (manual) this.showToast('后端连接失败，已保留当前数据');
      } finally {
        this.loading = false;
        this.recalcStats();
        this.rebuildAiInsights();
        this.$nextTick(function () {
          self.renderTrend();
          setTimeout(function () { self.renderTrend(); }, 120);
        });
      }
    },

    applyBackendData: function (payload) {
      var self = this;
      var prev = {};
      (this.categories || []).forEach(function (cat) {
        (cat.alerts || []).forEach(function (a) {
          var k = String(a.alarm_code || a.alarmCode || a.Id || a.id || '');
          if (!k) return;
          prev[k] = { _open: !!a._open, _editing: !!a._editing, _draft: a._draft || '' };
        });
      });
      var categories = this.emptyCategories();
      var byKey = {};
      categories.forEach(function (category) { byKey[category.key] = category; });
      var taskByAlarm = {};
      (payload.tasks || []).forEach(function (task) {
        if (task.source_alarm_code) taskByAlarm[task.source_alarm_code] = task;
      });
      (payload.alarms || []).forEach(function (row) {
        // 统一只读 zk_device_alarm（PlatformAlarmData），部门范围由接口 deptId 保证
        var backendCategory = row.category || 'device';
        if (backendCategory === 'human') backendCategory = 'person';
        var categoryKey = backendCategory === 'device' ? 'equip' : backendCategory;
        if (!byKey[categoryKey]) categoryKey = 'equip';
        var category = byKey[categoryKey];
        var alarm = normalizeSiteRecord({
          Id: row.Id,
          id: row.id,
          alarmCode: row.alarm_code,
          alarm_code: row.alarm_code,
          title: row.alarm_title || '未命名报警',
          alarm_title: row.alarm_title,
          category: categoryKey,
          backendCategory: backendCategory,
          level: self.backendLevel(row.priority),
          priority: row.priority,
          status: self.backendStatus(row.alarm_status),
          backendStatus: row.alarm_status,
          alarm_status: row.alarm_status,
          workshop: row.workshop || '',
          systemName: row.system_name || '',
          system_name: row.system_name || '',
          poolCode: row.pool_code || '',
          pool_code: row.pool_code || '',
          poolName: row.pool_name || '',
          location: row.pool_name || row.pool_code || row.workshop || '未指定位置',
          owner: row.assignee_name || '未分配',
          assignee_id: row.assignee_id || '',
          assignee_name: row.assignee_name || '',
          alarmTime: (function () {
            var parsed = self.parseDate(row.alarm_time);
            return parsed ? parsed.toISOString() : (row.alarm_time || '');
          })(),
          time: self.relativeTime(row.alarm_time),
          reason: row.alarm_reason || '后端未填写报警原因',
          alarm_reason: row.alarm_reason || '',
          aiAdvice: row.ai_advice || self.fallbackAdvice(backendCategory),
          ai_advice: row.ai_advice || '',
          solution: self.stripHtml(row.resolution || ''),
          sourceTaskCode: row.source_task_code || '',
          deptId: row.deptId != null ? row.deptId : row.dept_id,
          dept_id: row.deptId != null ? row.deptId : row.dept_id,
          task: taskByAlarm[row.alarm_code] || null,
          _raw: row._raw || null,
          _readonlyExt: !!row._readonlyExt,
          _open: false,
          _editing: false,
          _draft: ''
        });
        var keep = prev[String(alarm.alarm_code || alarm.Id || '')] || {};
        alarm._open = !!keep._open;
        alarm._editing = !!keep._editing;
        alarm._draft = keep._draft || '';
        if (alarm._editing) alarm._open = true;
        if (alarm.task && !alarm.sourceTaskCode) alarm.sourceTaskCode = alarm.task.task_code;
        applyAlarmUrgentOverride(alarm);
        self.applyAlarmOverdueByThreshold(alarm);
        category.alerts.push(alarm);
      });
      this.categories = categories;
      this.locations = payload.locations || [];
      this.tasks = payload.tasks || [];
      this.thresholds = payload.thresholds || [];
    },

    doRefresh: function () {
      this.loadData(true);
    },

    waitEchartsThenRender: function () {
      var self = this;
      if (typeof echarts !== 'undefined') {
        this.renderTrend();
        return;
      }
      var tries = 0;
      function tick() {
        if (typeof echarts !== 'undefined') {
          self.renderTrend();
          return;
        }
        tries += 1;
        if (tries < 40) self._echartsTimer = setTimeout(tick, 150);
      }
      tick();
    },

    onResize: function () {
      var self = this;
      if (this._rz) clearTimeout(this._rz);
      this._rz = setTimeout(function () { self.safeChartResize(); }, 100);
    },

    ensureChartBoxSize: function (el) {
      if (!el) return false;
      el.style.height = '200px';
      el.style.minHeight = '200px';
      el.style.maxHeight = '200px';
      return el.clientWidth >= 40 && el.clientHeight >= 80;
    },

    safeChartResize: function () {
      var el = document.getElementById('chartTrend');
      if (!el || !this.chart || !this.ensureChartBoxSize(el)) return;
      this.chart.resize({ width: el.clientWidth, height: el.clientHeight });
    },

    scheduleChartResize: function () {
      var self = this;
      this.$nextTick(function () {
        self.safeChartResize();
        setTimeout(function () { self.safeChartResize(); }, 100);
      });
    },

    loadSharedSiteOptions: function (force) {
      var self = this;
      return fetchSiteOptions({ includePonds: true, force: !!force }).then(function (opts) {
        self.sharedSiteOptions = opts || self.sharedSiteOptions;
        self.sanitizeSiteFilters();
      }).catch(function (e) {
        console.warn('[alertMonitor] shared site options', e);
      });
    },

    sanitizeSiteFilters: function () {
      var self = this;
      (this.categories || []).forEach(function (cat) {
        if (!cat || !cat.filters) return;
        var wsOpts = self.optionsForFilter(cat, { key: 'workshop' });
        if (cat.filters.workshop !== '全部' && wsOpts.indexOf(cat.filters.workshop) < 0) {
          cat.filters.workshop = '全部';
          cat.filters.system = '全部';
          cat.filters.pool = '全部';
          return;
        }
        var sysOpts = self.optionsForFilter(cat, { key: 'system' });
        if (cat.filters.system !== '全部' && sysOpts.indexOf(cat.filters.system) < 0) {
          cat.filters.system = '全部';
          cat.filters.pool = '全部';
          return;
        }
        var poolOpts = self.optionsForFilter(cat, { key: 'pool' });
        if (cat.filters.pool !== '全部' && poolOpts.indexOf(cat.filters.pool) < 0) {
          cat.filters.pool = '全部';
        }
      });
    },

    optionsForFilter: function (cat, filter) {
      if (filter.key === 'time') return ['全部', '近1小时', '近6小时', '近12小时', '近24小时', '自定义'];
      var ws = cat.filters && cat.filters.workshop !== '全部' ? cat.filters.workshop : '';
      var sys = cat.filters && cat.filters.system !== '全部' ? cat.filters.system : '';
      var labels = [];
      if (filter.key === 'workshop') {
        labels = pickWorkshopOptionLabels(this.sharedSiteOptions);
      } else if (filter.key === 'system') {
        labels = pickSystemOptionLabels(this.sharedSiteOptions, ws);
      } else if (filter.key === 'pool') {
        labels = pickPondOptionLabels(this.sharedSiteOptions, ws, sys);
      }
      // 与任务/健康等页同源，不再用告警行拼车间（否则会混入一号养殖车间等旧名）
      labels = Array.from(new Set((labels || []).filter(function (v) {
        if (!v) return false;
        var s = String(v).trim();
        if (!s || s === '全场' || s === '所有车间' || s === '所有系统' || s === '所有鱼池') return false;
        if (/^全部(车间|系统|鱼池)?$/.test(s)) return false;
        return true;
      })));
      return ['全部'].concat(labels);
    },

    alarmInTimeFilter: function (item, filterValue) {
      if (!filterValue || filterValue === '全部') return true;
      var date = this.parseDate(item.alarmTime);
      if (!date) return true;
      var end = new Date();
      var start = null;
      if (filterValue === '近1小时') start = new Date(end.getTime() - 3600000);
      if (filterValue === '近6小时') start = new Date(end.getTime() - 6 * 3600000);
      if (filterValue === '近12小时') start = new Date(end.getTime() - 12 * 3600000);
      if (filterValue === '近24小时') start = new Date(end.getTime() - 24 * 3600000);
      if (filterValue === '自定义') {
        start = this.parseDate(this.customStart);
        end = this.parseDate(this.customEnd);
        if (end) end = new Date(end.getTime() + 86400000 - 1);
      }
      return (!start || date >= start) && (!end || date <= end);
    },

    filteredAlerts: function (catKey) {
      var self = this;
      var cat = this.categories.find(function (category) { return category.key === catKey; });
      if (!cat) return [];
      if (isUserDeptScopeDenied()) return [];
      var opts = this.sharedSiteOptions || peekSharedSiteOptions();
      var levelRank = { urgent: 0, priority: 1, daily: 2 };
      return cat.alerts.filter(function (item) {
        if (!isAlarmInUserSiteScope(item, opts)) return false;
        if (self.filterLevel !== 'all' && item.level !== self.filterLevel) return false;
        if (self.filterStatus !== 'all' && item.status !== self.filterStatus) return false;
        if (cat.filters.workshop !== '全部' && !workshopsEqual(item.workshop, cat.filters.workshop)) return false;
        if (cat.filters.system !== '全部' && !systemsEqual(item.systemName, cat.filters.system)) return false;
        if (cat.filters.pool !== '全部' && !pondsEqual(item.poolCode, cat.filters.pool)) return false;
        if (!self.alarmInTimeFilter(item, cat.filters.time)) return false;
        if (self.selectedTimeRange) {
          var date = self.parseDate(item.alarmTime);
          if (date && (date < self.selectedTimeRange.start || date >= self.selectedTimeRange.end)) return false;
        }
        return true;
      }).sort(function (a, b) {
        var ra = levelRank[a.level] != null ? levelRank[a.level] : 9;
        var rb = levelRank[b.level] != null ? levelRank[b.level] : 9;
        if (ra !== rb) return ra - rb;
        var ta = self.parseDate(a.alarmTime);
        var tb = self.parseDate(b.alarmTime);
        return (tb ? tb.getTime() : 0) - (ta ? ta.getTime() : 0);
      });
    },

    toggleDd: function (id) { this.openDd = this.openDd === id ? null : id; },
    closeDd: function () { this.openDd = null; },
    pickDd: function (cat, key, option) {
      this.$set(cat.filters, key, option);
      this.openDd = null;
    },
    levelText: function (value) { return this.config.levelMap[value] || '日常'; },
    statusText: function (value) { return this.config.statusMap[value] || value; },
    statusTagClass: function (value) { return value; },
    alertIcon: function (level) {
      return level === 'daily' ? this.config.icons.check : level === 'priority' ? this.config.icons.info : this.config.icons.warn;
    },
    aiIcon: function (name) { return this.config.aiIcons[name] || this.config.aiIcons.chart; },
    metaSvg: function (key) { return this.config.metaSvg[key] || ''; },
    allAlerts: function () {
      return this.categories.reduce(function (list, category) { return list.concat(category.alerts); }, []);
    },
    /** 与首页同源：zk_device_alarm；前端再按站点范围裁剪，防止空部门串看 */
    scopedAlerts: function () {
      var self = this
      var opts = this.sharedSiteOptions || peekSharedSiteOptions()
      if (isUserDeptScopeDenied()) return []
      return this.allAlerts().filter(function (a) {
        return isAlarmInUserSiteScope(a, opts)
      })
    },

    recalcStats: function () {
      var self = this;
      var opts = this.sharedSiteOptions || peekSharedSiteOptions();
      var all = this.scopedAlerts();
      var total = all.length || 1;
      var statusCounts = { pending: 0, done: 0, exec: 0, overdue: 0 };
      var levelCounts = { urgent: 0, priority: 0, daily: 0 };
      all.forEach(function (item) {
        if (statusCounts[item.status] !== undefined) statusCounts[item.status] += 1;
        if (levelCounts[item.level] !== undefined) levelCounts[item.level] += 1;
      });
      this.stats.forEach(function (item) {
        item.count = statusCounts[item.key] || 0;
        item.pct = Math.round(item.count / total * 100);
      });
      this.statusFilters.forEach(function (item) {
        item.count = item.value === 'all' ? all.length : statusCounts[item.value] || 0;
      });
      this.levelFilters.forEach(function (item) {
        item.count = item.value === 'all' ? all.length : levelCounts[item.value] || 0;
        item.pct = item.value === 'all' ? 100 : Math.round(item.count / total * 100);
      });
      this.categories.forEach(function (category) {
        var scoped = (category.alerts || []).filter(function (item) {
          return isAlarmInUserSiteScope(item, opts);
        });
        category.badgeRed = scoped.filter(function (item) { return item.level === 'urgent'; }).length;
        category.badgeYellow = scoped.filter(function (item) { return item.level === 'priority'; }).length;
        category.badgeGreen = scoped.filter(function (item) { return item.level === 'daily'; }).length;
      });
      this.rebuildDisplayMetrics();
    },

    /** 多类型展示数据：鱼/水/设备/人员维度指标条 */
    rebuildDisplayMetrics: function () {
      var self = this;
      var all = this.scopedAlerts();
      var byCat = { fish: [], water: [], equip: [], person: [] };
      all.forEach(function (item) {
        var key = item.category === 'device' ? 'equip' : item.category;
        if (!byCat[key]) byCat[key] = [];
        byCat[key].push(item);
      });
      function countLevel(list, level) {
        return list.filter(function (i) { return i.level === level; }).length;
      }
      function openCount(list) {
        return list.filter(function (i) { return i.status !== 'done'; }).length;
      }
      function poolCount(list) {
        var set = {};
        list.forEach(function (i) {
          var p = i.poolCode || i.poolName || i.location || '';
          if (p) set[p] = true;
        });
        return Object.keys(set).length;
      }
      this.displayMetrics = [
        {
          key: 'fish',
          title: '鱼类',
          tone: 'fish',
          items: [
            { label: '活跃报警', value: openCount(byCat.fish), unit: '条' },
            { label: '紧急', value: countLevel(byCat.fish, 'urgent'), unit: '条' },
            { label: '涉及鱼池', value: poolCount(byCat.fish), unit: '个' }
          ]
        },
        {
          key: 'water',
          title: '水质',
          tone: 'water',
          items: [
            { label: '活跃报警', value: openCount(byCat.water), unit: '条' },
            { label: '紧急', value: countLevel(byCat.water, 'urgent'), unit: '条' },
            { label: '优先跟进', value: countLevel(byCat.water, 'priority'), unit: '条' }
          ]
        },
        {
          key: 'equip',
          title: '设备',
          tone: 'equip',
          items: [
            { label: '活跃报警', value: openCount(byCat.equip), unit: '条' },
            { label: '故障紧急', value: countLevel(byCat.equip, 'urgent'), unit: '条' },
            { label: '已完成', value: byCat.equip.filter(function (i) { return i.status === 'done'; }).length, unit: '条' }
          ]
        },
        {
          key: 'person',
          title: '人员',
          tone: 'person',
          items: [
            { label: '活跃报警', value: openCount(byCat.person), unit: '条' },
            { label: '逾期', value: byCat.person.filter(function (i) { return i.status === 'overdue'; }).length, unit: '条' },
            { label: '执行中', value: byCat.person.filter(function (i) { return i.status === 'exec'; }).length, unit: '条' }
          ]
        }
      ];
      // 补充趋势窗内总量，便于一眼看到多类型对比
      var trendTotal = 0;
      try {
        var pack = self.trendPack;
        trendTotal = (pack.urgent || []).reduce(function (a, b) { return a + b; }, 0)
          + (pack.priority || []).reduce(function (a, b) { return a + b; }, 0)
          + (pack.daily || []).reduce(function (a, b) { return a + b; }, 0);
      } catch (e) { trendTotal = all.length; }
      this.displayMetrics.push({
        key: 'trend',
        title: '趋势窗',
        tone: 'trend',
        items: [
          { label: '窗内合计', value: trendTotal, unit: '条' },
          { label: '全部报警', value: all.length, unit: '条' },
          { label: '紧急占比', value: all.length ? Math.round(countLevel(all, 'urgent') / all.length * 100) : 0, unit: '%' }
        ]
      });
    },

    hasEditingAlarm: function () {
      var cats = this.categories || [];
      for (var i = 0; i < cats.length; i++) {
        var alerts = cats[i].alerts || [];
        for (var j = 0; j < alerts.length; j++) {
          if (alerts[j] && alerts[j]._editing) return true;
        }
      }
      return false;
    },
    stripHtml: function (val) {
      if (val == null || val === '') return '';
      var s = String(val);
      if (typeof document !== 'undefined') {
        var div = document.createElement('div');
        div.innerHTML = s;
        s = div.textContent || div.innerText || '';
      } else {
        s = s.replace(/<[^>]+>/g, ' ');
      }
      return s.replace(/\s+/g, ' ').trim();
    },
    toggleAlert: function (item) { this.$set(item, '_open', !item._open); },

    setStatus: async function (item, status) {
      if (this.savingAlarmCode) return;
      // 完成时若尚无处置方案，先打开编辑；已有方案则直接落库
      if (status === 'done' && !(item.solution && String(item.solution).trim())) {
        this.startEdit(item);
        return;
      }
      var previous = item.status;
      this.savingAlarmCode = item.alarmCode || item.id;
      try {
        if (this.dataMode === 'database' && item.Id && window.PlatformAlarmData) {
          if (status === 'done') {
            await window.PlatformAlarmData.completeAlarm(item, item.solution || '已完成处置', this.operator);
          } else if (status === 'exec' && (previous === 'pending' || previous === 'overdue')) {
            await window.PlatformAlarmData.acceptAlarm(item, this.operator);
          } else {
            await window.PlatformAlarmData.updateStatus(item, this.databaseStatus(status), this.operator);
          }
        }
        this.$set(item, 'status', status);
        this.$set(item, 'backendStatus', this.databaseStatus(status));
        this.$set(item, 'alarm_status', this.databaseStatus(status));
        this.recalcStats();
        this.rebuildAiInsights();
        this.showToast(status === 'exec' ? '报警已接收' : (status === 'done' ? '报警已完成' : '报警状态已更新'));
      } catch (error) {
        this.showToast(error.message || '状态更新失败');
      } finally {
        this.savingAlarmCode = '';
      }
    },

    startEdit: function (item) {
      this.$set(item, '_editing', true);
      this.$set(item, '_draft', this.stripHtml(item.solution || ''));
      this.$set(item, '_open', true);
    },

    confirmPlan: async function (item) {
      var resolution = this.stripHtml(item._draft || '').trim();
      if (!resolution) {
        this.showToast('请填写实际处置方案');
        return;
      }
      this.savingAlarmCode = item.alarmCode || item.id;
      try {
        if (this.dataMode === 'database' && item.Id && window.PlatformAlarmData) {
          await window.PlatformAlarmData.completeAlarm(item, resolution, this.operator);
        }
        this.$set(item, 'solution', resolution);
        this.$set(item, 'status', 'done');
        this.$set(item, 'backendStatus', 'completed');
        this.$set(item, 'alarm_status', 'completed');
        this.$set(item, '_editing', false);
        this.$set(item, '_draft', '');
        this.recalcStats();
        this.rebuildAiInsights();
        this.showToast('处置方案已保存，报警已完成');
      } catch (error) {
        this.showToast(error.message || '处置方案保存失败');
      } finally {
        this.savingAlarmCode = '';
      }
    },

    cancelEdit: function (item) {
      this.$set(item, '_editing', false);
      this.$set(item, '_draft', '');
    },

    transferTask: async function (item) {
      if (item.sourceTaskCode || this.savingAlarmCode) return;
      var taskTitle = '【报警转任务】' + (item.title || item.alarm_title || '报警处置');
      var taskRes = createInspectionPlanTask({
        title: taskTitle,
        level: 'urgent',
        period: 'day',
        from: 'alert_monitor',
        pondDetailNum: item.poolCode || item.pool_code || item.pondDetailNum || ''
      });
      if (!taskRes.ok) {
        this.showToast(taskRes.error || '转任务失败');
        return;
      }
      var code = (taskRes.task && taskRes.task.id) || 'TASK';
      this.$set(item, 'level', 'urgent');
      this.$set(item, 'priority', 'urgent');
      this.$set(item, 'sourceTaskCode', code);
      markAlarmUrgentLocal(item, code);
      this.recalcStats();
      this.rebuildAiInsights();
      this.showToast('已生成紧急任务 ' + code);

      var self = this;
      (async function () {
        try {
          var realCode = '';
          if (self.dataMode === 'database' && item.Id && window.PlatformAlarmData) {
            realCode = await window.PlatformAlarmData.transferToTask(
              item,
              self.operator,
              self.urgentTimeoutMinutes(),
              { skipLocal: true, taskCode: code }
            );
          } else {
            var missionRes = await pushToMissionPlanning({
              alarmCode: item.alarm_code || item.alarmCode || ('ZK-TR-' + (item.id || Date.now())),
              title: taskTitle,
              taskTitle: taskTitle,
              content: item.reason || item.alarm_reason || taskTitle,
              level: 'urgent',
              forceUrgent: true,
              skipDedupe: true,
              category: item.category || 'fish',
              source: 'alert_monitor',
              pondDetailNum: item.poolCode || item.pool_code || '',
              pondName: item.poolName || item.pool_name || '',
              workshopName: item.workshop || '',
              pondSystemName: item.systemName || item.system_name || ''
            });
            if (missionRes && missionRes.taskCode) realCode = missionRes.taskCode;
          }
          if (realCode && realCode !== code) {
            self.$set(item, 'sourceTaskCode', realCode);
          }
        } catch (error) {
          console.warn('transferTask background sync', error);
        }
      })();
    },

    urgentTimeoutMinutes: function () {
      var item = this.thresholds.find(function (row) {
        return row.metric_code === 'urgent_timeout' || row.threshold_code === 'TASK-URGENT';
      });
      return item ? Number(item.normal_max || item.warning_max || 60) : 60;
    },

    setTrendRange: function (value) {
      this.trendRange = value;
      this.selectedPeriodIndex = null;
      this.selectedTimeRange = null;
      this.renderTrend();
      this.scheduleChartResize();
    },
    onCustomDateChange: function () {
      if (this.customStart && this.customEnd && this.customStart > this.customEnd) {
        var temp = this.customStart;
        this.customStart = this.customEnd;
        this.customEnd = temp;
      }
      this.selectedPeriodIndex = null;
      this.selectedTimeRange = null;
      this.renderTrend();
    },

    buildTrendPack: function () {
      var self = this;
      var end = new Date();
      var hours = this.trendRange === '6h' ? 6 : this.trendRange === '12h' ? 12 : 24;
      var start = new Date(end.getTime() - hours * 3600000);
      var autoExpanded = false;
      if (this.trendRange === 'custom') {
        start = this.parseDate(this.customStart) || start;
        var customEnd = this.parseDate(this.customEnd);
        if (customEnd) end = new Date(customEnd.getTime() + 86400000);
      }
      if (end <= start) end = new Date(start.getTime() + 3600000);

      // 收集可解析时间的报警
      var dated = [];
      this.scopedAlerts().forEach(function (item) {
        var date = self.parseDate(item.alarmTime);
        if (date) dated.push({ item: item, date: date });
      });

      // 预设时间窗内无点时，自动扩展到全部已加载报警的实际时间跨度，避免空图
      var inPreset = dated.filter(function (x) {
        return x.date >= start && x.date < end;
      });
      // 预设窗内点过少时扩展到全部已加载报警的时间跨度，避免近6小时空图
      if (inPreset.length < 3 && dated.length && this.trendRange !== 'custom') {
        dated.sort(function (a, b) { return a.date.getTime() - b.date.getTime(); });
        start = new Date(dated[0].date.getTime());
        end = new Date(dated[dated.length - 1].date.getTime());
        var span = end.getTime() - start.getTime();
        if (span < 3600000) {
          end = new Date(start.getTime() + 3600000);
        } else {
          var pad = Math.max(1800000, Math.floor(span * 0.05));
          start = new Date(start.getTime() - pad);
          end = new Date(end.getTime() + pad);
        }
        autoExpanded = true;
      }

      var durationHours = Math.max(1, (end - start) / 3600000);
      var count = Math.min(12, Math.max(6, Math.ceil(durationHours <= 24 ? durationHours : 8)));
      var width = (end - start) / count;
      var pack = {
        labels: [],
        urgent: [],
        priority: [],
        daily: [],
        buckets: [],
        autoExpanded: autoExpanded
      };
      for (var i = 0; i < count; i += 1) {
        var bucketStart = new Date(start.getTime() + width * i);
        var bucketEnd = new Date(start.getTime() + width * (i + 1));
        var label = durationHours > 48
          ? this.pad2(bucketStart.getMonth() + 1) + '/' + this.pad2(bucketStart.getDate())
          : this.pad2(bucketStart.getHours()) + ':' + this.pad2(bucketStart.getMinutes());
        pack.labels.push(label);
        pack.buckets.push({ start: bucketStart, end: bucketEnd, label: label, alerts: [] });
        pack.urgent.push(0);
        pack.priority.push(0);
        pack.daily.push(0);
      }
      dated.forEach(function (entry) {
        var date = entry.date;
        if (date < start || date >= end) return;
        var index = Math.min(count - 1, Math.max(0, Math.floor((date - start) / width)));
        pack.buckets[index].alerts.push(entry.item);
        var level = entry.item.level;
        if (level === 'urgent' || level === 'priority' || level === 'daily') {
          pack[level][index] += 1;
        } else {
          pack.daily[index] += 1;
        }
      });
      return pack;
    },

    selectPeriod: function (index) {
      var bucket = this.trendPack.buckets[index];
      if (!bucket) return;
      this.selectedPeriodIndex = index;
      this.selectedTimeRange = { start: bucket.start, end: bucket.end };
      this.renderTrend();
      this.scheduleChartResize();
    },

    selectTrendRange: function (startIndex, endIndex) {
      var buckets = this.trendPack.buckets;
      if (!buckets.length) return;
      startIndex = Math.max(0, Math.min(buckets.length - 1, startIndex));
      endIndex = Math.max(startIndex, Math.min(buckets.length - 1, endIndex));
      this.selectedPeriodIndex = null;
      this.selectedTimeRange = { start: buckets[startIndex].start, end: buckets[endIndex].end };
    },

    clearPeriodSelection: function () {
      this.selectedPeriodIndex = null;
      this.selectedTimeRange = null;
      if (this.chart) this.chart.dispatchAction({ type: 'dataZoom', start: 0, end: 100 });
      this.renderTrend();
    },

    rebuildAiInsights: function () {
      var escapeHtml = this.htmlEscape;
      var all = this.scopedAlerts();
      var active = all.filter(function (item) { return item.status !== 'done'; });
      var insights = [];
      var overdueUrgent = active.filter(function (item) {
        return item.level === 'urgent' && item.status === 'overdue';
      });
      if (overdueUrgent.length) {
        insights.push({ sev: 'urgent', icon: 'dot', text: '<strong>' + overdueUrgent.length +
            ' 条紧急报警已逾期</strong>，请立即升级通知负责人并优先清零。' });
      }
      var poolGroups = {};
      active.forEach(function (item) {
        if (!item.poolCode) return;
        if (!poolGroups[item.poolCode]) poolGroups[item.poolCode] = [];
        poolGroups[item.poolCode].push(item);
      });
      Object.keys(poolGroups).some(function (pool) {
        var categories = Array.from(new Set(poolGroups[pool].map(function (item) { return item.category; })));
        if (categories.length < 2) return false;
        insights.push({ sev: 'urgent', icon: 'dot', text: '<strong>' + escapeHtml(pool) +
            ' 存在多维度同步报警</strong>，涉及 ' + categories.length + ' 个业务分类，建议联合排查水质与设备。' });
        return true;
      });
      var categoryCounts = {};
      active.forEach(function (item) {
        var key = (item.workshop || '未分区') + '|' + item.category;
        categoryCounts[key] = (categoryCounts[key] || 0) + 1;
      });
      var denseKey = Object.keys(categoryCounts).sort(function (a, b) { return categoryCounts[b] - categoryCounts[a]; })[0];
      if (denseKey && categoryCounts[denseKey] >= 3) {
        insights.push({ sev: 'priority', icon: 'dot', text: escapeHtml(denseKey.split('|')[0]) +
            ' 同类报警较密集（' + categoryCounts[denseKey] + ' 条），建议排查系统性隐患。' });
      }
      var completed = all.filter(function (item) { return item.status === 'done'; }).length;
      var rate = all.length ? Math.round(completed / all.length * 100) : 100;
      insights.push({ sev: rate >= 80 ? 'daily' : 'warn', icon: rate >= 80 ? 'dot' : 'warn',
        text: '当前报警完成率为 <strong>' + rate + '%</strong>，' +
          (rate >= 80 ? '整体处置效率良好。' : '建议优先处理积压和逾期报警。') });
      if (!active.length) {
        insights.push({ sev: 'daily', icon: 'dot', text: '当前无待处置报警，全场运行状态正常。' });
      }
      this.aiInsights = insights;
    },

    renderTrend: function () {
      if (typeof echarts === 'undefined') {
        this.waitEchartsThenRender();
        return;
      }
      var el = document.getElementById('chartTrend');
      if (!el) return;
      var self = this;
      if (!this.ensureChartBoxSize(el)) {
        if (!this._trendRetry) this._trendRetry = 0;
        if (this._trendRetry < 8) {
          this._trendRetry += 1;
          setTimeout(function () { self.renderTrend(); }, 150);
        }
        return;
      }
      this._trendRetry = 0;
      if (!this.chart) {
        this.chart = echarts.init(el, null, { renderer: 'canvas' });
        this.chart.on('click', function (params) {
          if (params.dataIndex != null) self.selectPeriod(params.dataIndex);
        });
        this.chart.on('datazoom', function (params) {
          var zoom = params.batch && params.batch[0] ? params.batch[0] : params;
          var size = self.trendPack.labels.length;
          if (!size) return;
          var from = Math.floor((Number(zoom.start) || 0) / 100 * size);
          var to = Math.max(from, Math.ceil((Number(zoom.end) || 100) / 100 * size) - 1);
          if (from > 0 || to < size - 1) self.selectTrendRange(from, to);
        });
        if (typeof ResizeObserver !== 'undefined') {
          this._chartRo = new ResizeObserver(function () { self.safeChartResize(); });
          this._chartRo.observe(el);
        }
      }
      var pack = this.trendPack;
      var colors = { urgent: '#ff4d4f', priority: '#faad14', daily: '#52c41a' };
      var names = { urgent: '紧急', priority: '优先', daily: '日常' };
      var selected = this.selectedPeriodIndex;
      function series(level, asBar) {
        return {
          name: names[level],
          type: asBar ? 'bar' : 'line',
          barMaxWidth: 10,
          barGap: '20%',
          smooth: true,
          symbol: 'circle',
          data: pack[level].map(function (value, index) {
            return {
              value: value,
              symbolSize: selected === index ? 13 : 7,
              itemStyle: {
                color: colors[level],
                borderColor: selected === index ? '#fff' : colors[level],
                borderWidth: selected === index ? 2 : 1
              }
            };
          }),
          lineStyle: { color: colors[level], width: level === 'urgent' ? 2.5 : 2 },
          itemStyle: { color: colors[level] },
          areaStyle: !asBar && level === 'urgent' ? { color: 'rgba(255,77,79,.12)' } : undefined,
          markLine: selected !== null && level === 'urgent' && !asBar ? {
            symbol: 'none', label: { show: false },
            lineStyle: { color: '#e8eef5', type: 'dashed', opacity: .7 },
            data: [{ xAxis: pack.labels[selected] }]
          } : undefined
        };
      }
      this.chart.setOption({
        animationDuration: 300,
        backgroundColor: 'transparent',
        grid: { left: 36, right: 18, top: 26, bottom: 30 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(10,16,28,.96)',
          borderColor: '#00e5ff',
          textStyle: { color: '#e8eef5' },
          formatter: function (params) {
            var index = params[0] ? params[0].dataIndex : 0;
            var bucket = pack.buckets[index];
            return (bucket ? bucket.label : '') + '<br>' + params.map(function (param) {
              var value = param.data && param.data.value !== undefined ? param.data.value : param.value;
              return param.marker + param.seriesName + '：' + value + ' 条';
            }).join('<br>') + '<br><span style="color:#8b949e">点击查看该时段紧急报警</span>';
          }
        },
        toolbox: {
          right: 8,
          top: -4,
          iconStyle: { borderColor: '#5ec8ff' },
          feature: { dataZoom: { yAxisIndex: 'none', title: { zoom: '框选时段', back: '还原' } } }
        },
        xAxis: {
          type: 'category', data: pack.labels, boundaryGap: true,
          axisLabel: { color: '#9aa8b8', fontSize: 11 },
          axisLine: { lineStyle: { color: 'rgba(255,255,255,.18)' } },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value', minInterval: 1, min: 0,
          axisLabel: { color: '#9aa8b8', fontSize: 11 },
          splitLine: { lineStyle: { color: 'rgba(255,255,255,.08)', type: 'dashed' } }
        },
        dataZoom: [{ type: 'inside', xAxisIndex: 0, filterMode: 'none' }],
        series: [series('urgent', true), series('priority', true), series('daily', true)]
      }, true);
      this.chart.resize();
      this.safeChartResize();
    }
  }
}
</script>

<template>
  <div id="app" v-cloak>
    <PageLoadingMask
      :visible="loading && !allAlerts().length"
      text="正在加载报警数据…"
    />
    <div class="toast" v-if="toastText">{{ toastText }}</div>
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

        <div class="page-header">
          <div class="header-left">
            <span class="breadcrumb-link">AI智能报警监控</span>
            <span class="data-source" :class="dataMode">{{ dataMode === 'database' ? '数据库实时数据' : dataMode === 'loading' ? '正在连接' : '演示数据' }}</span>
          </div>
          <div class="header-right">
            <span>最后刷新: {{ lastRefresh }}</span>
            <button type="button" class="btn-refresh" title="刷新" :disabled="loading" @click="doRefresh">
              <svg viewBox="0 0 24 24"><path d="M17.65 6.35A7.95 7.95 0 0 0 12 4V1L7 6l5 5V7c2.76 0 5 2.24 5 5a5 5 0 0 1-8.9 3.1L6.7 16.5A7.96 7.96 0 0 0 20 12c0-2.21-.9-4.21-2.35-5.65zM6 12c0-1.34.53-2.55 1.39-3.45L6 7.16A7.96 7.96 0 0 0 4 12c0 3.73 2.55 6.86 6 7.74v-2.1A6.01 6.01 0 0 1 6 12z"/></svg>
            </button>
          </div>
        </div>

        <div class="header-right">
          <div class="quick-btns">
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>存量分布</span>
            </button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
              <span>智能巡检</span>
            </button>
            <button class="q-btn btn-orange" style="border-color:#f59e0b;color:#fbbf24" @click="goTaskPlan">
              <span>任务与计划</span>
            </button>
            <button class="q-btn btn-cyan-light" @click="toPage('/keyshow/inspectionreport')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
              <span>分析报告</span>
            </button>
            <!-- 新建诊断暂不可用，隐藏入口（需求：AI报警监控） -->
            <button class="q-btn btn-indigo" @click="toPage('/knowledge/zk-meeting-expert')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              <span>专家会议</span>
            </button>
            <!-- Excel：综合目录去掉试验系统入口 -->
          </div>
        </div>
      </div>
    </header>


    <div class="data-warning" v-if="dataError">部分后端数据不可用：{{ dataError }}</div>

    <!-- 系统业务报警面板已按需求移除 -->

    <button class="overdue-reminder" v-if="overdueUrgentCount" @click="filterLevel = 'urgent'; filterStatus = 'overdue'">紧急提醒：{{ overdueUrgentCount }} 条紧急报警已逾期，点击立即处理</button>

    <section class="display-metrics" v-if="displayMetricGroups.length">
      <div
        class="metric-group"
        v-for="g in displayMetricGroups"
        :key="g.key"
        :class="'tone-' + g.tone"
        @click="g.key !== 'trend' && (filterLevel = 'all')"
      >
        <div class="metric-group-title">{{ g.title }}</div>
        <div class="metric-group-items">
          <div class="metric-item" v-for="m in g.items" :key="g.key + '-' + m.label">
            <div class="metric-item-label">{{ m.label }}</div>
            <div class="metric-item-value">{{ m.value }}<span class="metric-unit">{{ m.unit }}</span></div>
          </div>
        </div>
      </div>
    </section>

    <section class="stats-row">
      <div
        class="stat-card"
        v-for="s in stats"
        :key="s.key"
        :class="'tone-' + s.tone"
        @click="filterStatus = s.key"
      >
        <div class="stat-top">
          <div>
            <div class="stat-label">{{ s.label }}</div>
            <div class="stat-num">{{ s.count }}</div>
          </div>
          <div class="stat-pct">{{ s.pct }}%</div>
        </div>
        <div class="stat-bar"><i :style="{ width: s.pct + '%' }"></i></div>
      </div>
    </section>

    <section class="filter-section">
      <div class="filter-row">
        <span class="flabel">报警级别:</span>
        <div class="filter-btns">
          <button
            type="button"
            class="fbtn"
            v-for="lv in levelFilters"
            :key="lv.value"
            :class="[lv.cls, { active: filterLevel === lv.value }]"
            @click="filterLevel = lv.value"
          >{{ lv.label }} {{ lv.count }}{{ lv.pct ? ' (' + lv.pct + '%)' : '' }}</button>
        </div>
      </div>
      <div class="filter-row">
        <span class="flabel">任务状态:</span>
        <div class="filter-btns">
          <button
            type="button"
            class="fbtn"
            v-for="st in statusFilters"
            :key="st.value"
            :class="{ active: filterStatus === st.value }"
            @click="filterStatus = st.value"
          >{{ st.label }} {{ st.count }}</button>
        </div>
      </div>
    </section>

    <section class="monitor-grid">
      <div
        class="mon-col"
        v-for="cat in categories"
        :key="cat.key"
        :class="'cat-' + cat.key"
      >
        <div class="mon-hd">
          <div class="mon-title">
            <svg class="ico" viewBox="0 0 24 24" v-html="cat.icon"></svg>
            {{ cat.title }}
          </div>
          <div class="mon-badges">
            <span class="count-badge red" v-if="cat.badgeRed">{{ cat.badgeRed }}</span>
            <span class="count-badge yellow" v-if="cat.badgeYellow">{{ cat.badgeYellow }}</span>
            <span class="count-badge green" v-if="cat.badgeGreen">{{ cat.badgeGreen }}</span>
          </div>
        </div>

        <div class="mon-filters">
          <div class="mini-dd" v-for="f in catFilters" :key="cat.key + '-' + f.key">
            <span>{{ f.label }}:</span>
            <button
              type="button"
              class="dd-trigger"
              :class="{ open: openDd === cat.key + '-' + f.key }"
              @click.stop="toggleDd(cat.key + '-' + f.key)"
            >{{ cat.filters[f.key] || '全部' }}</button>
            <div class="dd-menu scroll-y" v-if="openDd === cat.key + '-' + f.key" @click.stop>
              <div
                class="dd-item"
                v-for="opt in optionsForFilter(cat, f)"
                :key="opt"
                :class="{ selected: (cat.filters[f.key] || '全部') === opt }"
                @click="pickDd(cat, f.key, opt)"
              >{{ opt }}</div>
            </div>
          </div>
        </div>

        <div class="alert-list scroll-y">
          <div
            class="alert-card"
            v-for="(item, idx) in filteredAlerts(cat.key)"
            :key="item.id || (cat.key + '-' + idx)"
            :class="['lv-' + item.level, { expanded: item._open }]"
          >
            <div class="alert-hd" @click="toggleAlert(item)">
              <div class="alert-ico" v-html="alertIcon(item.level)"></div>
              <div class="alert-body">
                <div class="alert-name-row">
                  <span class="alert-name">{{ item.title }}</span>
                  <span class="tag" :class="'tag-' + item.level">{{ levelText(item.level) }}</span>
                  <span class="tag tag-status" :class="statusTagClass(item.status)">{{ statusText(item.status) }}</span>
                </div>
                <div class="alert-meta">
                  <span><span v-html="metaSvg('location')"></span> {{ item.location }}</span>
                  <span><span v-html="metaSvg('owner')"></span> {{ item.owner }}</span>
                  <span><span v-html="metaSvg('time')"></span> {{ item.time }}</span>
                </div>
              </div>
              <span class="alert-chevron">▼</span>
            </div>

            <div class="alert-panel" v-if="item._open" @click.stop>
              <div class="info-box">
                <span class="box-label">报警原因</span>
                <div class="box-text">{{ stripHtml(item.reason || item.detail) }}</div>
              </div>
              <div class="info-box ai-box">
              <span class="box-label">
                <svg viewBox="0 0 24 24"><path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a3 3 0 0 1 3 3v1h1a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1h-1v1a3 3 0 0 1-3 3h-1v.27c.6.34 1 .99 1 1.73a2 2 0 1 1-4 0c0-.74.4-1.39 1-1.73V14H9a3 3 0 0 1-3-3v-1H5a1 1 0 0 1-1-1V8a1 1 0 0 1 1-1h1V6a3 3 0 0 1 3-3h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"/></svg>
                AI建议
              </span>
                <div class="box-text">{{ stripHtml(item.aiAdvice) }}</div>
              </div>

              <div class="info-box sol-box" v-if="item.status === 'done' && item.solution && !item._editing">
              <span class="box-label">
                <svg viewBox="0 0 24 24"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm-1.5 14.5L6 12l1.4-1.4 3.1 3.1 6.6-6.6L18.5 8.5l-8 8z"/></svg>
                实际处置方案
              </span>
                <div class="box-text">{{ stripHtml(item.solution) }}</div>
              </div>

              <div class="info-box edit-box" v-if="item._editing">
              <span class="box-label">
                <svg viewBox="0 0 24 24"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm-1.5 14.5L6 12l1.4-1.4 3.1 3.1 6.6-6.6L18.5 8.5l-8 8z"/></svg>
                填写实际处置方案（完成后自动保存）
              </span>
                <textarea
                  class="plan-input"
                  v-model="item._draft"
                  placeholder="请输入实际采取的处置措施..."
                  @click.stop
                ></textarea>
                <div class="plan-actions">
                  <button type="button" class="btn-confirm" @click.stop="confirmPlan(item)">确认完成</button>
                  <button type="button" class="btn-cancel" @click.stop="cancelEdit(item)">取消</button>
                </div>
              </div>

              <div class="status-row">
                <span class="st-label">状态:</span>
                <button
                  type="button"
                  class="sbtn"
                  v-for="opt in statusOptions"
                  :key="opt.value"
                  :class="item.status === opt.value ? opt.activeCls : ''"
                  @click.stop="setStatus(item, opt.value)"
                >{{ opt.label }}</button>
              </div>

              <div class="alarm-actions">
                <button
                  type="button"
                  class="btn-transfer"
                  v-if="!item.sourceTaskCode && item.status !== 'done' && !(item.status === 'overdue' && item.level === 'urgent')"
                  :disabled="!!savingAlarmCode"
                  @click.stop="transferTask(item)"
                >转为紧急任务</button>
                <span class="task-linked" v-else-if="item.sourceTaskCode">已关联 {{ item.sourceTaskCode }}</span>
                <button
                  type="button"
                  class="btn-edit"
                  v-if="item.status === 'done' && !item._editing"
                  @click.stop="startEdit(item)"
                >编辑处置方案</button>
              </div>
            </div>
          </div>
          <div v-if="!filteredAlerts(cat.key).length" class="empty-hint">暂无匹配告警</div>
        </div>
      </div>
    </section>

    <section class="bottom-row">
      <div class="panel trend-panel" :class="{ 'has-period': selectedPeriod }">
        <div class="panel-hd">
          <div class="panel-title">
            <svg class="ico" viewBox="0 0 24 24"><path d="M3.5 18.5l4.2-5.4 3.3 2.6L16 9.2l4.5 5.3v1.5H3.5v-1.5zm1.5 0h14V15l-3.3-3.9-4.8 6.4-3.2-2.5L5 18.5zM3 4h18v2H3V4z"/></svg>
            报警趋势
            <span class="unit">(条)</span>
            <span v-if="trendAutoExpanded" class="trend-hint">已按实际报警时间跨度展示</span>
          </div>
          <div class="range-btns">
            <button
              type="button"
              class="rbtn"
              v-for="r in trendRanges"
              :key="r.value"
              :class="{ active: trendRange === r.value }"
              @click="setTrendRange(r.value)"
            >{{ r.label }}</button>
          </div>
        </div>

        <div class="custom-range" v-if="trendRange === 'custom'">
          <label class="cr-item">
            <span>起:</span>
            <span class="date-field">
            <span class="date-text">{{ formatSlashDate(customStart) }}</span>
            <svg class="cal-ico" viewBox="0 0 24 24"><path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/></svg>
            <input class="date-native" type="date" v-model="customStart" @change="onCustomDateChange" />
          </span>
          </label>
          <label class="cr-item">
            <span>止:</span>
            <span class="date-field">
            <span class="date-text">{{ formatSlashDate(customEnd) }}</span>
            <svg class="cal-ico" viewBox="0 0 24 24"><path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/></svg>
            <input class="date-native" type="date" v-model="customEnd" @change="onCustomDateChange" />
          </span>
          </label>
        </div>

        <div class="chart-wrap">
          <div v-if="loading && !allAlerts().length" class="chart-loading">
            <i class="wq-spinner"></i>
            <span>趋势加载中…</span>
          </div>
          <div id="chartTrend" class="chart-box"></div>
        </div>

        <div class="chart-footer">
          <div class="chart-legend">
            <span class="lg-item"><i class="lg-dot urgent"></i>紧急 <span class="lg-count">x{{ trendLegend.urgent }}</span></span>
            <span class="lg-item"><i class="lg-dot priority"></i>优先 <span class="lg-count">x{{ trendLegend.priority }}</span></span>
            <span class="lg-item"><i class="lg-dot daily"></i>日常 <span class="lg-count">x{{ trendLegend.daily }}</span></span>
          </div>
          <button
            type="button"
            class="btn-clear-sel"
            v-if="selectedPeriodIndex !== null || selectedTimeRange"
            @click="clearPeriodSelection"
          >清除选择</button>
        </div>

        <div
          class="period-panel"
          v-if="selectedPeriod"
          :class="'sev-' + selectedPeriod.sev"
        >
          <div class="period-hd">
            <svg class="warn-ico" viewBox="0 0 24 24"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>
            第 {{ selectedPeriod.index + 1 }} 个时段 时段紧急预警 共 {{ selectedPeriod.value }} 条
          </div>
          <div class="period-list scroll-y">
            <div class="period-row" v-for="(row, ri) in selectedPeriod.alerts" :key="ri">
              <svg class="p-cat-ico" viewBox="0 0 24 24" :style="{ fill: row.color }" v-html="row.icon"></svg>
              <span class="p-cat" :style="{ color: row.color }">{{ row.catName }}</span>
              <span class="p-title">{{ row.title }}</span>
              <span class="p-meta">{{ row.location }} 负责人: {{ row.owner }}</span>
              <span class="p-tag" :class="statusTagClass(row.status)">{{ statusText(row.status) }}</span>
              <span class="p-time">{{ row.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="panel ai-panel">
        <div class="panel-hd">
          <div class="panel-title ai-title">
            <svg class="ico" viewBox="0 0 24 24"><path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h2a4 4 0 0 1 4 4v1h1a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1h-1v1a4 4 0 0 1-4 4h-1v1.27c.6.34 1 .99 1 1.73a2 2 0 1 1-4 0c0-.74.4-1.39 1-1.73V18H9a4 4 0 0 1-4-4v-1H4a1 1 0 0 1-1-1v-2a1 1 0 0 1 1-1h1v-1a4 4 0 0 1 4-4h2V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2zm-1 9v4h2v-4h-2zm4 0v4h1v-1a2 2 0 0 0-1-1.73V11h0zm-6 0v1.27A2 2 0 0 0 8 14v1h1v-4H9z"/></svg>
            AI综合分析
          </div>
          <span class="ai-live">实时分析</span>
        </div>
        <div class="ai-list scroll-y">
          <div class="ai-item" v-for="(a, i) in aiInsights" :key="i" :class="'sev-' + a.sev">
            <span class="ai-dot" v-if="a.icon === 'dot'">&#8203;</span>
            <span class="ai-ico" v-else v-html="aiIcon(a.icon)"></span>
            <div class="ai-text" v-html="a.text"></div>
          </div>
        </div>
      </div>
    </section>
    <MyChat />
  </div>
</template>

<style scoped lang="scss">
.header {
  border-bottom: 1px solid rgba(59, 130, 246, 0.3);
  padding: 0.65rem 1.25rem;
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

        &:hover {
          filter: brightness(1.08);
          transform: translateY(-1px);
        }
      }
    }
  }
}
/* ===== Reset ===== */
*, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

/* ===== Tokens ===== */
//:root {
//
//}

#app {
  --bg: #0a0e14;
  --panel: #121820;
  --panel-2: #161b22;
  --border: rgba(255, 255, 255, 0.08);
  --text: #e8eef5;
  --text-2: #8b949e;
  --text-3: #606a78;
  --cyan: #00e5ff;
  --cyan-soft: #5ec8ff;
  --blue: #409eff;
  --green: #52c41a;
  --yellow: #faad14;
  --orange: #e6a23c;
  --red: #ff4d4f;
  --gap: 12px;
  --r: 8px;
  --safe-top: env(safe-area-inset-top, 0px);
  --safe-right: env(safe-area-inset-right, 0px);
  --safe-bottom: env(safe-area-inset-bottom, 0px);
  --safe-left: env(safe-area-inset-left, 0px);
  //width: 100%;
  //min-height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Hiragino Sans GB",
  "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif;
  //background: var(--bg);
  color: var(--text) !important;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
  color-scheme: dark;
}

#app {
  position: relative;
  //min-height: 100vh;
  //min-height: 100dvh;
  padding: calc(14px + var(--safe-top)) calc(16px + var(--safe-right)) calc(18px + var(--safe-bottom)) calc(16px + var(--safe-left));
  //background:
  //  radial-gradient(ellipse at 50% -10%, #142038 0%, transparent 50%),
  //  var(--bg);
}

button, input, textarea, select {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  font-family: inherit;
  color: inherit;
}

button {
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}

textarea { -webkit-overflow-scrolling: touch; }

/* ===== Layout ===== */
#app {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  min-height: calc(100vh - 32px);
  min-height: calc(100dvh - 32px);
  height: auto;
  overflow: visible;
}

.scroll-y {
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: #2a4058 transparent;
}

.scroll-y::-webkit-scrollbar { width: 4px; }
.scroll-y::-webkit-scrollbar-thumb { background: #2a4058; border-radius: 2px; }

.empty-hint {
  color: var(--text-3);
  font-size: 12px;
  padding: 12px 0;
  text-align: center;
}

/* ===== Header ===== */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  padding-bottom: 6px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.breadcrumb-link {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  font-size: 14px;
  font-weight: 500;
  color: var(--cyan);
  border: 1px solid var(--cyan);
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.15s, box-shadow 0.15s;
}

.breadcrumb-link:hover { background: rgba(0, 229, 255, 0.08); }

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-2);
}

.btn-refresh {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: 1px solid var(--border);
  border-radius: 4px;
  background: transparent;
  color: var(--cyan-soft);
  transition: border-color 0.15s, box-shadow 0.15s;
}

.btn-refresh:hover {
  border-color: var(--cyan);
  box-shadow: 0 0 8px rgba(0, 229, 255, 0.25);
}

.btn-refresh svg { width: 14px; height: 14px; fill: currentColor; }

/* ===== Stats ===== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--gap);
  flex-shrink: 0;
}

.stat-card {
  background: var(--panel-2);
  border: 1px solid var(--border);
  border-radius: var(--r);
  padding: 14px 18px 12px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.15s;
}

.stat-card:hover { border-color: rgba(255, 255, 255, 0.16); }

.stat-top {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-2);
  margin-bottom: 4px;
}

.stat-num {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  color: #fff;
  font-variant-numeric: tabular-nums;
}

.stat-pct {
  font-size: 14px;
  font-weight: 600;
  color: var(--tone);
  margin-bottom: 4px;
}

.stat-bar {
  height: 3px;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.stat-bar > i {
  display: block;
  height: 100%;
  border-radius: 2px;
  background: var(--tone);
  box-shadow: 0 0 8px var(--tone);
}

.stat-card.tone-gray { --tone: #c0c8d4; }
.stat-card.tone-green { --tone: var(--green); }
.stat-card.tone-yellow { --tone: var(--yellow); }
.stat-card.tone-red { --tone: var(--red); }

.stat-card.tone-green .stat-num { color: var(--green); }
.stat-card.tone-yellow .stat-num { color: var(--yellow); }
.stat-card.tone-red .stat-num { color: var(--red); }

/* ===== Filters ===== */
.filter-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-row .flabel {
  font-size: 13px;
  color: var(--text-2);
  white-space: nowrap;
  min-width: 70px;
}

.filter-btns {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.fbtn {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: transparent;
  color: var(--text-2);
  transition: all 0.15s;
  white-space: nowrap;
}

.fbtn:hover { border-color: rgba(255, 255, 255, 0.28); color: #fff; }

.fbtn.active {
  color: #fff;
  border-color: var(--cyan);
  box-shadow: 0 0 8px rgba(0, 229, 255, 0.35);
  background: rgba(0, 229, 255, 0.08);
}

.fbtn.lv-urgent.active {
  border-color: var(--red);
  color: var(--red);
  background: rgba(255, 77, 79, 0.12);
  box-shadow: 0 0 8px rgba(255, 77, 79, 0.35);
}

.fbtn.lv-priority.active {
  border-color: var(--yellow);
  color: var(--yellow);
  background: rgba(250, 173, 20, 0.12);
  box-shadow: 0 0 8px rgba(250, 173, 20, 0.35);
}

.fbtn.lv-daily.active {
  border-color: var(--green);
  color: var(--green);
  background: rgba(82, 196, 26, 0.12);
  box-shadow: 0 0 8px rgba(82, 196, 26, 0.35);
}

/* ===== Monitor ===== */
.monitor-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--gap);
  flex: 0 0 auto;
  min-height: 460px;
  height: 480px;
  overflow: hidden;
}

.mon-col {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: var(--r);
  padding: 12px;
  display: flex;
  flex-direction: column;
  flex-shrink: 1;
  min-height: 0;
  overflow: hidden;
}

.mon-hd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.mon-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
}

.mon-title .ico {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.mon-col.cat-fish .mon-title { color: #5ec8ff; }
.mon-col.cat-fish .mon-title .ico { fill: #5ec8ff; }
.mon-col.cat-water .mon-title { color: #13c2c2; }
.mon-col.cat-water .mon-title .ico { fill: #13c2c2; }
.mon-col.cat-equip .mon-title { color: var(--orange); }
.mon-col.cat-equip .mon-title .ico { fill: var(--orange); }
.mon-col.cat-person .mon-title { color: #c678dd; }
.mon-col.cat-person .mon-title .ico { fill: #c678dd; }

.mon-badges { display: flex; gap: 4px; }

.count-badge {
  min-width: 20px;
  height: 20px;
  padding: 0 5px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.count-badge.red { background: rgba(255, 77, 79, 0.75); }
.count-badge.green { background: rgba(82, 196, 26, 0.65); }
.count-badge.yellow { background: rgba(250, 173, 20, 0.7); }

.mon-filters {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 6px 6px;
  margin-bottom: 10px;
  flex-shrink: 0;
  position: relative;
  z-index: 5;
  overflow: visible;
}

.mini-dd {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: var(--text-2);
  position: relative;
  flex: 1 1 0;
  min-width: 0;
}

.mini-dd > span { white-space: nowrap; flex-shrink: 0; }

.dd-trigger {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  min-width: 0;
  flex: 1 1 auto;
  padding: 3px 6px;
  border: 1px solid rgba(94, 200, 255, 0.45);
  border-radius: 4px;
  background: rgba(6, 16, 28, 0.9);
  color: var(--cyan-soft);
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  justify-content: space-between;
}

.dd-trigger::after {
  content: "";
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid var(--cyan-soft);
  margin-left: 2px;
  opacity: 0.8;
}

.dd-trigger.open {
  border-color: var(--cyan);
  box-shadow: 0 0 6px rgba(0, 229, 255, 0.3);
}

.dd-menu {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  min-width: 108px;
  max-height: 200px;
  background: rgba(14, 22, 36, 0.98);
  border: 1px solid rgba(94, 200, 255, 0.35);
  border-radius: 12px;
  z-index: 50;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.55);
  padding: 6px 0;
}

.dd-item {
  padding: 7px 14px;
  font-size: 12px;
  color: var(--cyan-soft);
  cursor: pointer;
  white-space: nowrap;
}

.dd-item:hover { background: rgba(0, 229, 255, 0.1); color: #fff; }

.dd-item.selected {
  background: #a0cfff;
  color: #1a1f28;
  font-weight: 600;
}

.alert-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 0;
}

/* ===== Alert cards ===== */
.alert-card {
  display: flex;
  flex-direction: column;
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid transparent;
  background: rgba(255, 255, 255, 0.03);
  transition: background 0.15s, box-shadow 0.15s;
  position: relative;
}

.alert-card:hover { background: rgba(255, 255, 255, 0.05); }

.alert-card.lv-urgent {
  border-color: rgba(255, 77, 79, 0.6);
  box-shadow: 0 0 12px rgba(255, 77, 79, 0.18), inset 0 0 16px rgba(255, 77, 79, 0.07);
  background: rgba(255, 77, 79, 0.07);
}

.alert-card.lv-priority {
  border-color: rgba(250, 173, 20, 0.55);
  box-shadow: 0 0 12px rgba(250, 173, 20, 0.14), inset 0 0 16px rgba(250, 173, 20, 0.06);
  background: rgba(250, 173, 20, 0.06);
}

.alert-card.lv-daily {
  border-color: rgba(82, 196, 26, 0.5);
  box-shadow: 0 0 12px rgba(82, 196, 26, 0.14), inset 0 0 16px rgba(82, 196, 26, 0.06);
  background: rgba(82, 196, 26, 0.06);
}

.alert-hd {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
  -webkit-user-select: none;
  user-select: none;
}

.alert-ico {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
  margin-top: 1px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.alert-ico svg { width: 18px; height: 18px; }

.alert-card.lv-urgent .alert-ico { color: var(--red); }
.alert-card.lv-priority .alert-ico { color: var(--yellow); }
.alert-card.lv-daily .alert-ico { color: var(--green); }

.alert-body { flex: 1; min-width: 0; }

.alert-name-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 6px;
}

.alert-name {
  font-size: 13px;
  font-weight: 600;
  color: #fff;
}

.tag {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 500;
  line-height: 1.5;
}

.tag-urgent { background: rgba(255, 77, 79, 0.9); color: #fff; }
.tag-priority { background: rgba(250, 173, 20, 0.9); color: #1a1a1a; }
.tag-daily { background: rgba(82, 196, 26, 0.85); color: #fff; }

.tag-status {
  background: rgba(255, 255, 255, 0.08);
  color: var(--text-2);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.tag-status.exec {
  background: transparent;
  color: var(--yellow);
  border-color: rgba(250, 173, 20, 0.55);
}

.tag-status.overdue {
  background: rgba(255, 77, 79, 0.2);
  color: var(--red);
  border-color: rgba(255, 77, 79, 0.5);
}

.tag-status.done {
  background: rgba(82, 196, 26, 0.15);
  color: var(--green);
  border-color: rgba(82, 196, 26, 0.4);
}

.alert-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 14px;
  font-size: 11px;
  color: var(--text-3);
}

.alert-meta span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.alert-meta svg {
  width: 11px;
  height: 11px;
  fill: currentColor;
  opacity: 0.7;
}

.alert-chevron {
  color: var(--text-3);
  font-size: 11px;
  flex-shrink: 0;
  margin-top: 4px;
  transition: transform 0.2s;
  opacity: 0.7;
}

.alert-card.expanded .alert-chevron { transform: rotate(180deg); }

.alert-panel {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.alert-card.lv-urgent .alert-panel { border-top-color: rgba(255, 77, 79, 0.25); }
.alert-card.lv-priority .alert-panel { border-top-color: rgba(250, 173, 20, 0.3); }
.alert-card.lv-daily .alert-panel { border-top-color: rgba(82, 196, 26, 0.3); }

.info-box {
  border-radius: 5px;
  padding: 8px 10px;
  font-size: 12px;
  line-height: 1.5;
  background: rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.info-box .box-label {
  display: block;
  font-size: 11px;
  color: var(--text-3);
  margin-bottom: 4px;
}

.info-box .box-text { color: #d0d8e0; }

.info-box.ai-box {
  border-color: rgba(19, 194, 194, 0.45);
  background: rgba(19, 194, 194, 0.06);
}

.info-box.ai-box .box-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #13c2c2;
  margin-bottom: 4px;
}

.info-box.ai-box .box-label svg {
  width: 13px;
  height: 13px;
  fill: currentColor;
}

.info-box.ai-box .box-text { color: #9ed9e8; }

.info-box.sol-box {
  border-color: rgba(82, 196, 26, 0.55);
  background: rgba(82, 196, 26, 0.06);
}

.info-box.sol-box .box-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--green);
}

.info-box.sol-box .box-label svg {
  width: 13px;
  height: 13px;
  fill: currentColor;
}

.info-box.edit-box {
  border-color: rgba(82, 196, 26, 0.45);
  background: rgba(82, 196, 26, 0.05);
}

.info-box.edit-box .box-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--green);
  margin-bottom: 6px;
}

.info-box.edit-box .box-label svg {
  width: 13px;
  height: 13px;
  fill: currentColor;
}

.plan-input {
  width: 100%;
  min-height: 64px;
  resize: vertical;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(0, 0, 0, 0.35);
  color: #e8eef5;
  padding: 8px 10px;
  font-size: 12px;
  line-height: 1.5;
  outline: none;
}

.plan-input:focus {
  border-color: rgba(82, 196, 26, 0.55);
  box-shadow: 0 0 0 1px rgba(82, 196, 26, 0.2);
}

.plan-input::placeholder { color: var(--text-3); }

.plan-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.btn-confirm,
.btn-cancel,
.btn-edit {
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 4px;
  transition: all 0.15s;
}

.btn-confirm {
  border: 1px solid rgba(82, 196, 26, 0.7);
  background: rgba(82, 196, 26, 0.85);
  color: #fff;
}

.btn-confirm:hover { background: var(--green); box-shadow: 0 0 8px rgba(82, 196, 26, 0.35); }

.btn-cancel {
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.06);
  color: var(--text-2);
}

.btn-cancel:hover { color: #fff; border-color: rgba(255, 255, 255, 0.3); }

.btn-edit {
  border: 1px solid rgba(82, 196, 26, 0.55);
  background: transparent;
  color: var(--green);
  width: 100%;
}

.btn-edit:hover {
  background: rgba(82, 196, 26, 0.1);
  box-shadow: 0 0 8px rgba(82, 196, 26, 0.2);
}

.status-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 2px;
}

.status-row .st-label {
  font-size: 12px;
  color: var(--text-3);
  margin-right: 2px;
}

.sbtn {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  background: transparent;
  color: var(--text-3);
  transition: all 0.15s;
}

.sbtn:hover { color: #fff; border-color: rgba(255, 255, 255, 0.35); }

.sbtn.active-pending {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.65);
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.15);
}

.sbtn.active-exec {
  color: var(--yellow);
  border-color: rgba(250, 173, 20, 0.75);
  background: rgba(250, 173, 20, 0.12);
  box-shadow: 0 0 8px rgba(250, 173, 20, 0.25);
}

.sbtn.active-done {
  color: var(--green);
  border-color: rgba(82, 196, 26, 0.75);
  background: rgba(82, 196, 26, 0.12);
  box-shadow: 0 0 8px rgba(82, 196, 26, 0.25);
}

.sbtn.active-overdue {
  color: var(--red);
  border-color: rgba(255, 77, 79, 0.8);
  background: rgba(255, 77, 79, 0.12);
  box-shadow: 0 0 8px rgba(255, 77, 79, 0.3);
}

/* ===== Bottom ===== */
.bottom-row {
  display: grid;
  grid-template-columns: 1.55fr 1fr;
  gap: var(--gap);
  flex: 0 0 auto;
  min-height: 0;
  overflow: visible;
  align-items: start;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: var(--r);
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  min-height: 0;
  height: auto;
  overflow: hidden;
}

.panel-hd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  flex-shrink: 0;
  gap: 8px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: var(--cyan-soft);
}

.panel-title .unit {
  font-size: 12px;
  font-weight: 400;
  color: var(--text-3);
}

.panel-title .trend-hint {
  font-size: 11px;
  font-weight: 400;
  color: var(--text-3);
  margin-left: 4px;
}

.display-metrics {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  margin: 0 0 12px;
}
.metric-group {
  border: 1px solid rgba(94, 234, 212, 0.18);
  border-radius: 10px;
  padding: 10px 12px;
  background: linear-gradient(180deg, rgba(15, 35, 48, 0.9), rgba(10, 24, 34, 0.95));
}
.metric-group-title {
  font-size: 12px;
  color: var(--cyan-soft);
  margin-bottom: 8px;
  font-weight: 600;
}
.metric-group-items {
  display: flex;
  gap: 8px;
  justify-content: space-between;
}
.metric-item-label {
  font-size: 11px;
  color: var(--text-3);
}
.metric-item-value {
  font-size: 16px;
  font-weight: 700;
  color: #e8f7f4;
  margin-top: 2px;
}
.metric-unit {
  font-size: 11px;
  font-weight: 400;
  margin-left: 2px;
  color: var(--text-3);
}
.metric-group.tone-fish { border-color: rgba(248, 113, 113, 0.35); }
.metric-group.tone-water { border-color: rgba(56, 189, 248, 0.35); }
.metric-group.tone-equip { border-color: rgba(251, 191, 36, 0.35); }
.metric-group.tone-person { border-color: rgba(167, 139, 250, 0.35); }
.metric-group.tone-trend { border-color: rgba(94, 234, 212, 0.35); }
@media (max-width: 1200px) {
  .display-metrics { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

.panel-title .ico {
  width: 16px;
  height: 16px;
  fill: var(--cyan);
}

.panel-title.ai-title { color: var(--cyan); }
.panel-title.ai-title .ico { fill: var(--cyan); }

.ai-live {
  font-size: 12px;
  color: var(--text-3);
}

.range-btns {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.rbtn {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  border: 1px solid transparent;
  background: transparent;
  color: var(--text-3);
  transition: all 0.15s;
}

.rbtn:hover { color: var(--text-2); background: rgba(255, 255, 255, 0.04); }

.rbtn.active {
  color: var(--cyan);
  border-color: rgba(0, 229, 255, 0.65);
  box-shadow: 0 0 10px rgba(0, 229, 255, 0.28);
  background: rgba(0, 229, 255, 0.1);
}

/* ===== Chart ===== */
.chart-wrap { position: relative; }
.chart-loading {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #9ec8e0;
  font-size: 13px;
  background: rgba(6, 16, 32, 0.55);
}
.wq-spinner {
  display: inline-block;
  width: 22px;
  height: 22px;
  border: 2px solid rgba(56, 189, 248, 0.25);
  border-top-color: #38bdf8;
  border-radius: 50%;
  animation: wq-spin 0.75s linear infinite;
}
@keyframes wq-spin { to { transform: rotate(360deg); } }
.chart-box {
  flex: none !important;
  flex-grow: 0 !important;
  flex-shrink: 0 !important;
  flex-basis: 200px !important;
  width: 100%;
  height: 200px !important;
  min-height: 200px !important;
  max-height: 200px !important;
  position: relative;
  overflow: hidden;
}

/* 只约束图表主容器，勿命中 ECharts tooltip（同级 div） */
.chart-box > div:first-child,
.chart-box canvas {
  width: 100% !important;
  height: 100% !important;
}

.custom-range {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px 28px;
  margin: 4px 0 12px;
  flex-shrink: 0;
}

.custom-range .cr-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #888d94;
  user-select: none;
}

.date-field {
  position: relative;
  display: inline-flex;
  align-items: center;
  min-width: 142px;
  height: 32px;
  padding: 0 32px 0 12px;
  border: 1px solid #1a4d5e;
  border-radius: 4px;
  background: #0a0e14;
  cursor: pointer;
  box-shadow: none;
}

.date-field:hover { border-color: #2a6a7a; }

.date-field .date-text {
  font-size: 13px;
  color: #d1d7dd;
  letter-spacing: 0.3px;
  line-height: 1;
  pointer-events: none;
  font-variant-numeric: tabular-nums;
}

.date-field .date-native {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  border: 0;
  background: transparent;
  z-index: 2;
  color-scheme: dark;
}

.date-field .cal-ico {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  fill: #8b949e;
  pointer-events: none;
  z-index: 1;
}

.chart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  flex-shrink: 0;
  margin-top: 6px;
}

.chart-legend {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 12px;
  color: var(--text-2);
}

.chart-legend .lg-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.chart-legend .lg-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 0 6px currentColor;
}

.chart-legend .lg-dot.urgent { background: var(--red); color: var(--red); }
.chart-legend .lg-dot.priority { background: var(--yellow); color: var(--yellow); }
.chart-legend .lg-dot.daily { background: var(--green); color: var(--green); }

.chart-legend .lg-count {
  color: var(--text-3);
  font-variant-numeric: tabular-nums;
}

.btn-clear-sel {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: transparent;
  color: var(--text-3);
  white-space: nowrap;
}

.btn-clear-sel:hover {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.35);
}

.period-panel {
  margin-top: 12px;
  flex: 0 0 auto;
  min-height: 0;
  height: auto;
  max-height: none;
  display: flex;
  flex-direction: column;
  border: 1px solid #5a2125;
  border-radius: 6px;
  background: #0c1018;
  box-shadow: none;
  overflow: hidden;
}

.period-hd {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  font-size: 13px;
  font-weight: 600;
  color: #ff4d4f;
  border-bottom: 1px solid rgba(255, 77, 79, 0.16);
  flex-shrink: 0;
  background: transparent;
}

.period-hd .warn-ico {
  width: 15px;
  height: 15px;
  fill: #ff4d4f;
  flex-shrink: 0;
}

.period-list {
  flex: 1;
  min-height: 0;
  max-height: 360px;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.period-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 13px;
  background: transparent;
  line-height: 1.35;
}

.period-row:last-child { border-bottom: none; }

.period-row .p-cat-ico {
  width: 15px;
  height: 15px;
  flex-shrink: 0;
}

.period-row .p-cat {
  min-width: 28px;
  flex-shrink: 0;
  font-size: 13px;
}

.period-row .p-title {
  color: #ff4d4f;
  font-weight: 700;
  flex-shrink: 0;
  white-space: nowrap;
}

.period-row .p-meta {
  color: #6b7380;
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.period-row .p-tag {
  flex-shrink: 0;
  padding: 2px 9px;
  border-radius: 3px;
  font-size: 12px;
  line-height: 1.4;
  border: 1px solid transparent;
}

.period-row .p-tag.pending {
  color: #9aa3af;
  border-color: rgba(154, 163, 175, 0.4);
  background: rgba(40, 46, 56, 0.95);
}

.period-row .p-tag.exec {
  color: #1a1a1a;
  border-color: #e6a23c;
  background: #e6a23c;
  font-weight: 600;
}

.period-row .p-tag.overdue {
  color: #ff6b6d;
  border-color: rgba(255, 77, 79, 0.65);
  background: rgba(80, 20, 24, 0.85);
}

.period-row .p-tag.done {
  color: var(--green);
  border-color: rgba(82, 196, 26, 0.45);
  background: rgba(82, 196, 26, 0.12);
}

.period-row .p-time {
  color: #6b7380;
  flex-shrink: 0;
  margin-left: 4px;
  min-width: 68px;
  text-align: right;
}

.panel.trend-panel {
  overflow: visible;
  min-height: 360px;
  height: auto;
  flex: none;
}

.panel.trend-panel.has-period {
  min-height: 720px;
}

.panel.ai-panel {
  min-height: 360px;
}

/* ===== AI ===== */
.ai-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0;
  min-height: 0;
  padding-right: 4px;
}

.ai-item {
  display: flex;
  gap: 10px;
  font-size: 13px;
  line-height: 1.55;
  color: var(--text-2);
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.ai-item:last-child { border-bottom: none; }

.ai-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 6px;
  box-shadow: 0 0 6px currentColor;
}

.ai-ico {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  margin-top: 3px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.ai-ico svg { width: 14px; height: 14px; fill: currentColor; }

.ai-item.sev-urgent .ai-dot { background: var(--red); color: var(--red); }
.ai-item.sev-priority .ai-dot { background: var(--yellow); color: var(--yellow); }
.ai-item.sev-daily .ai-dot { background: var(--green); color: var(--green); }
.ai-item.sev-info .ai-ico { color: var(--blue); }
.ai-item.sev-warn .ai-ico { color: var(--orange); }
.ai-item.sev-wave .ai-ico { color: var(--cyan); }
.ai-item.sev-star .ai-ico { color: #c678dd; }

.ai-text strong { color: #d0d8e0; font-weight: 500; }

/* ===== Responsive ===== */
@media (max-width: 1200px) {
  .monitor-grid {
    grid-template-columns: repeat(2, 1fr);
    height: auto;
    min-height: 480px;
    overflow: visible;
  }
  .mon-col { min-height: 240px; }
  .bottom-row { grid-template-columns: 1fr; min-height: 0; }
  .panel { min-height: 0; }
  .panel.trend-panel { min-height: 360px; }
  .panel.trend-panel.has-period { min-height: 680px; }
  .stats-row { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 700px) {
  .monitor-grid { grid-template-columns: 1fr; min-height: 0; height: auto; }
  .stats-row { grid-template-columns: 1fr 1fr; }
  .breadcrumb-link { font-size: 13px; padding: 3px 10px; }
  .chart-box {
    height: 180px !important;
    min-height: 180px !important;
    max-height: 180px !important;
    flex-basis: 180px !important;
  }
  .period-list { max-height: 260px; }
}

/* ===== Backend integration ===== */
[v-cloak] { display: none !important; }

.header-left { flex-wrap: wrap; }

.data-source {
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 9px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 999px;
  color: var(--text-2);
  font-size: 11px;
  background: rgba(255, 255, 255, 0.03);
}

.data-source.database {
  color: var(--green);
  border-color: rgba(82, 196, 26, 0.35);
  background: rgba(82, 196, 26, 0.08);
}

.data-source.demo {
  color: var(--yellow);
  border-color: rgba(250, 173, 20, 0.35);
  background: rgba(250, 173, 20, 0.08);
}

.data-source.loading { color: var(--cyan-soft); }

.data-warning {
  padding: 8px 12px;
  border: 1px solid rgba(250, 173, 20, 0.28);
  border-radius: 6px;
  color: #ffd666;
  background: rgba(250, 173, 20, 0.08);
  font-size: 12px;
}

.toast {
  position: fixed;
  z-index: 9999;
  top: calc(18px + var(--safe-top));
  left: 50%;
  transform: translateX(-50%);
  max-width: min(520px, calc(100vw - 32px));
  padding: 9px 16px;
  border: 1px solid rgba(0, 229, 255, 0.4);
  border-radius: 6px;
  color: #fff;
  background: rgba(8, 18, 30, 0.96);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35), 0 0 15px rgba(0, 229, 255, 0.12);
  font-size: 13px;
}

.btn-refresh:disabled,
.btn-transfer:disabled,
.sbtn:disabled {
  cursor: wait;
  opacity: 0.55;
}

.alarm-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 9px;
}

.btn-transfer {
  padding: 5px 12px;
  border: 1px solid rgba(0, 229, 255, 0.35);
  border-radius: 4px;
  color: var(--cyan);
  background: rgba(0, 229, 255, 0.08);
  font-size: 12px;
}

.btn-transfer:hover {
  border-color: var(--cyan);
  background: rgba(0, 229, 255, 0.14);
}

.task-linked {
  padding: 4px 9px;
  border-radius: 4px;
  color: var(--green);
  background: rgba(82, 196, 26, 0.08);
  font-size: 11px;
}

@media (max-width: 700px) {
  .page-header { align-items: flex-start; gap: 8px; }
  .header-right { flex-shrink: 0; }
  .data-source { order: 3; }
}



.overdue-reminder {
  width: 100%;
  padding: 9px 14px;
  border: 1px solid rgba(255, 77, 79, 0.55);
  border-radius: 6px;
  color: #fff;
  background: linear-gradient(90deg, rgba(255, 77, 79, 0.2), rgba(255, 77, 79, 0.08));
  box-shadow: 0 0 14px rgba(255, 77, 79, 0.12);
  text-align: left;
  font-size: 12px;
}

.overdue-reminder:hover { border-color: var(--red); }

/* ===== PC single-screen layout =====
   Keep every primary section in the viewport. Long alarm/analysis content
   scrolls inside its own panel instead of making the whole page scroll. */
@media (min-width: 901px) {
  html,
  body {
    height: 100%;
    min-height: 0;
    overflow: hidden;
  }

  body {
    height: 100vh;
    height: 100dvh;
    min-height: 0;
    padding: calc(10px + var(--safe-top))
    calc(12px + var(--safe-right))
    calc(10px + var(--safe-bottom))
    calc(12px + var(--safe-left));
  }

  #app {
    display: grid;
    grid-template-rows:
      auto
      auto
      auto
      minmax(215px, 1.3fr)
      minmax(185px, 0.9fr);
    gap: 8px;
    height: calc(100vh - 20px);
    height: calc(100dvh - 20px);
    min-height: 0;
    overflow: hidden;
  }

  .page-header {
    min-height: 28px;
    padding-bottom: 0;
  }

  .stats-row {
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 8px;
  }

  .stat-card {
    padding: 8px 14px 7px;
  }

  .stat-top { margin-bottom: 6px; }
  .stat-label { margin-bottom: 2px; }
  .stat-num { font-size: clamp(24px, 2.2vw, 30px); }
  .filter-section { gap: 5px; }
  .filter-row { gap: 8px; }
  .fbtn { padding: 3px 10px; }

  .monitor-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 8px;
    flex: none;
    width: 100%;
    height: 100%;
    min-height: 0;
    overflow: hidden;
  }

  .mon-col {
    height: 100%;
    min-height: 0;
    padding: 9px;
  }

  .mon-hd,
  .mon-filters { margin-bottom: 7px; }
  .alert-list { overflow-y: auto; }
  .alert-card { padding: 8px 9px; }
  .alert-name-row { margin-bottom: 4px; }

  .bottom-row {
    grid-template-columns: minmax(0, 1.55fr) minmax(0, 1fr);
    gap: 8px;
    align-items: stretch;
    width: 100%;
    height: 100%;
    min-height: 0;
    overflow: hidden;
  }

  .panel,
  .panel.trend-panel,
  .panel.trend-panel.has-period,
  .panel.ai-panel {
    height: 100%;
    min-height: 0;
    overflow: hidden;
  }

  .panel { padding: 9px 12px; }
  .panel-hd { margin-bottom: 4px; }

  .chart-box {
    flex: 1 1 120px !important;
    height: auto !important;
    min-height: 105px !important;
    max-height: 200px !important;
  }

  .trend-panel.has-period .chart-box {
    flex-basis: 85px !important;
    min-height: 72px !important;
  }

  .chart-footer { margin-top: 2px; }

  .period-panel {
    flex: 1 1 72px;
    margin-top: 5px;
    min-height: 0;
    max-height: none;
  }

  .period-hd { padding: 6px 10px; }

  .period-list,
  .ai-list {
    min-height: 0;
    overflow-y: auto;
  }

  .ai-item {
    padding: 5px 0;
    font-size: 12px;
    line-height: 1.4;
  }
}

@media (min-width: 901px) and (max-height: 740px) {
  #app {
    grid-template-rows:
      auto
      auto
      auto
      minmax(205px, 1.25fr)
      minmax(175px, 0.9fr);
    gap: 6px;
  }

  .page-header { min-height: 24px; }

  .stat-card {
    padding-top: 6px;
    padding-bottom: 6px;
  }

  .stat-top { margin-bottom: 4px; }
  .stat-num { font-size: 24px; }
  .filter-section { gap: 3px; }

  .fbtn {
    padding-top: 2px;
    padding-bottom: 2px;
  }

  .chart-box { min-height: 90px !important; }
}

/* Variable banners (for example, overdue reminders) stay compact while the
   monitor and analysis areas share all remaining viewport height. */
@media (min-width: 901px) {
  #app {
    display: flex;
    flex-direction: column;
  }

  .monitor-grid {
    flex: 1.3 1 0;
  }

  .bottom-row {
    flex: 0.9 1 0;
  }
}

</style>

// kede-patch-build-touch
