<script>
import Vue from 'vue'
var iconPaths = {
  edit: '<path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 013 3L7 19l-4 1 1-4 12.5-12.5z"/>',
  delete: '<path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6M14 11v6"/>',
  view: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-3-3"/>',
  close: '<path d="M18 6L6 18M6 6l12 12"/>',
  upload: '<path d="M12 16V4M12 4l-4 4M12 4l4 4"/><path d="M4 20h16"/>',
  'cloud-upload': '<path d="M7 18a4 4 0 010-8 5 5 0 019.9-1A4.5 4.5 0 0117.5 14H18a3 3 0 010 6H6"/><path d="M12 8v8"/><path d="M8 12l4-4 4 4"/>',
  upload2: '<path d="M12 3v12"/><path d="M8 7l4-4 4 4"/><path d="M4 21h16"/>',
  camera: '<path d="M4 7h3l2-2h6l2 2h3a2 2 0 012 2v9a2 2 0 01-2 2H4a2 2 0 01-2-2V9a2 2 0 012-2z"/><circle cx="12" cy="13" r="3"/>',
  video: '<rect x="3" y="6" width="13" height="12" rx="2"/><path d="M16 10l5-3v10l-5-3"/>',
  download: '<path d="M12 3v12"/><path d="M8 11l4 4 4-4"/><path d="M4 21h16"/>',
  picture: '<rect x="3" y="5" width="18" height="14" rx="2"/><circle cx="9" cy="10" r="2"/><path d="M21 17l-5-5-4 4-2-2-5 5"/>',
  folder: '<path d="M3 7a2 2 0 012-2h5l2 2h9a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>',
  microphone: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0014 0"/><path d="M12 18v3"/>',
  tag: '<path d="M4 12V4h8l8 8-8 8-8-8z"/><circle cx="9" cy="9" r="1.5"/>',
  chart: '<path d="M3 20h18"/><path d="M7 16l3-4 3 3 5-8"/>',
  filter: '<path d="M4 6h16M7 12h10M10 18h4"/>',
  user: '<path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/>',
  'user-solid': '<path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/>',
  logout: '<path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><path d="M16 17l5-5-5-5"/><path d="M21 12H9"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  'check-circle': '<circle cx="12" cy="12" r="9"/><path d="M9 12l2 2 4-4"/>'
};
Vue.component('ui-icon', {
  props: ['name'],
  render: function (h) {
    var strokeW = this.name === 'cloud-upload' ? '1.6' : '1.8';
    return h('svg', {
      class: 'ui-icon-svg',
      attrs: {
        viewBox: '0 0 24 24',
        fill: 'none',
        stroke: 'currentColor',
        'stroke-width': strokeW,
        'stroke-linecap': 'round',
        'stroke-linejoin': 'round'
      },
      domProps: { innerHTML: iconPaths[this.name] || '' }
    });
  }
});

Vue.component('header-auth', {
  props: ['user'],
  template:
    '<div class="header-auth">' +
    '<div class="header-date">{{ todayText }}</div>' +
    '<div class="auth-actions">' +
    '<button type="button" class="auth-user-btn" @click.stop="menuOpen = !menuOpen">' +
    '<span class="auth-avatar">{{ avatarText }}</span>' +
    '<span class="auth-name">{{ user.username }}</span>' +
    '<span class="auth-caret" :class="{ open: menuOpen }">▾</span>' +
    '</button>' +
    '<div v-if="menuOpen" class="auth-menu" @click.stop>' +
    '<div class="auth-menu-meta">' +
    '<div class="auth-menu-name">{{ user.username }}</div>' +
    '<div class="auth-menu-email">{{ user.email || "—" }}</div>' +
    '</div>' +
    '<button type="button" class="auth-menu-item" @click="$emit(\'switch-account\')"><ui-icon name="user"></ui-icon> 切换账号</button>' +
    '<button type="button" class="auth-menu-item danger" @click="$emit(\'logout\')"><ui-icon name="logout"></ui-icon> 退出登录</button>' +
    '</div>' +
    '</div>' +
    '</div>',
  data: function () { return { menuOpen: false }; },
  computed: {
    todayText: function () {
      var now = new Date();
      var weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
      return now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate() + '日' + weekDays[now.getDay()];
    },
    avatarText: function () {
      return (this.user && this.user.username) ? this.user.username.slice(0, 1) : '?';
    }
  },
  mounted: function () {
    var self = this;
    this._closeMenu = function () { self.menuOpen = false; };
    document.addEventListener('click', this._closeMenu);
  },
  beforeDestroy: function () {
    document.removeEventListener('click', this._closeMenu);
  }
});

Vue.component('nav-icon', {
  props: ['name'],
  template: '<svg class="nav-icon-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">' +
    '<template v-if="name === \'config\'"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/></template>' +
    '<template v-else-if="name === \'collect\'"><ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M3 5v14c0 1.66 4.03 3 9 3s9-1.34 9-3V5"/><path d="M3 12c0 1.66 4.03 3 9 3s9-1.34 9-3"/></template>' +
    '<template v-else-if="name === \'diagnosis\'"><path d="M4.8 10.3a6 6 0 108.4 1.4"/><path d="M8 4v2.5"/><path d="M16 4v2.5"/><circle cx="20" cy="16" r="2.5"/><path d="M20 13.5v-1a3 3 0 00-6 0V16"/></template>' +
    '<template v-else-if="name === \'archive\'"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 9h18"/><path d="M10 6.5h4"/></template>' +
    '<template v-else-if="name === \'system\'"><path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></template>' +
    '</svg>'
});

var pageMeta = {
  config: { title: '基础配置', crumbs: ['主页', '基础配置'], module: true },
  collect: { title: '数据采集', crumbs: ['主页', '数据采集'], module: true },
  diagnosis: { title: '协同诊断', crumbs: ['主页', '协同诊断'], module: true },
  archive: { title: '案例归档', crumbs: ['主页', '案例归档'], module: true },
  system: { title: '系统管理', crumbs: ['主页', '系统管理'], module: true }
};
export default {
  data: function () {
    return {
      loggedIn: FishAuth.isLoggedIn(),
      authMode: 'login',
      authLoading: false,
      authError: '',
      loginForm: { account: 'admin@system.com', password: '123456' },
      registerForm: { username: '', email: '', role: 'worker', password: '', confirmPassword: '' },
      page: 'config',
      menuOpen: false,
      userMenuOpen: false,
      toast: '',
      currentUser: FishAuth.getUser() || FishStore.currentUser,
      chatContacts: FishStore.chatContacts.slice(),
      chatTarget: null,
      contactKeyword: '',
      menus: [
        { path: 'config', label: '基础配置', icon: 'config' },
        { path: 'collect', label: '数据采集', icon: 'collect' },
        { path: 'diagnosis', label: '协同诊断', icon: 'diagnosis' },
        { path: 'archive', label: '案例归档', icon: 'archive' }
        // 系统管理入口已按需求移除
      ],
      meta: {},
      summary: {},
      diagnosisList: [],
      diagTab: 'active',
      diagTabs: [
        { key: 'pending', label: '待诊断' },
        { key: 'active', label: '进行中' },
        { key: 'done', label: '已完成' }
      ],
      currentCase: null,
      chatInput: '',
      metrics: [],
      mediaFiles: [],
      uploadDragOver: false,
      collectForm: { species: '', pondCode: '', frySource: '', diseaseCount: '', feedingMethod: '', abnormalParts: [], abnormalDesc: '' },
      speciesList: [],
      diseaseTypes: [],
      equipments: [],
      configTab: 'breeding',
      configTabs: [
        { key: 'breeding', label: '养殖信息' },
        { key: 'disease', label: '病害类型' },
        { key: 'equipment', label: '设备配置' }
      ],
      configVisible: false,
      configForm: {},
      equipmentTypes: ['溶氧监测', 'pH监测', '氨氮监测', '温度监测', '行为监测'],
      frequencyOptions: ['1分钟', '5分钟', '10分钟', '30分钟', '1小时'],
      diseaseCategories: ['寄生虫', '细菌', '病毒', '霉菌', '营养', '环境'],
      archiveSummary: { total: 0, highMatch: 0, estimatedOpen: '' },
      archiveCategories: [],
      archiveKeyword: '',
      archiveCategory: 'all',
      archiveTab: 'all',
      categoryMenuOpen: false,
      archiveDetailVisible: false,
      archiveDetail: {},
      conclusionVisible: false,
      conclusionForm: { suspectedDisease: '', treatment: '', conclusion: '' },
      systemTab: 'users',
      systemTabs: [
        { key: 'users', label: '用户管理' },
        { key: 'logs', label: '操作日志' },
        { key: 'settings', label: '系统配置' }
      ],
      systemUsers: [],
      operationLogs: [],
      systemSettings: {},
      logRange: 'today',
      logRangeMenuOpen: false,
      notifyMenuOpen: false,
      notifyMethodOptions: [
        { value: 'email', label: '邮件' },
        { value: 'sms', label: '短信' },
        { value: 'wechat', label: '微信' },
        { value: 'all', label: '全部' }
      ],
      logRangeOptions: [
        { value: 'today', label: '今天' },
        { value: 'week', label: '最近7天' },
        { value: 'month', label: '最近30天' },
        { value: 'all', label: '全部' }
      ],
      userVisible: false,
      userForm: { id: '', username: '', password: '', role: '', department: '' },
      savingUser: false
    };
  },
  computed: {
    isModulePage: function () {
      var m = pageMeta[this.page];
      return m && m.module;
    },
    pageTitle: function () {
      return (pageMeta[this.page] && pageMeta[this.page].title) || '';
    },
    breadcrumbs: function () {
      return (pageMeta[this.page] && pageMeta[this.page].crumbs) || ['主页'];
    },
    filteredCases: function () {
      return FishAPI.listDiagnosis({ tab: this.diagTab });
    },
    activeDiagTabLabel: function () {
      var tab = this.diagTabs.find(function (t) { return t.key === this.diagTab; }.bind(this));
      return tab ? tab.label : '';
    },
    caseChatContacts: function () {
      if (!this.currentCase) return [];
      var list = this.chatContacts.slice();
      var me = this.currentUser;
      var ensure = function (username, role, roleLabel) {
        if (!username || username === me.username) return;
        if (list.some(function (u) { return u.username === username; })) return;
        list.unshift({ id: 'local-' + username, username: username, role: role, roleLabel: roleLabel });
      };
      ensure(this.currentCase.reporter, 'worker', '工人');
      ensure(this.currentCase.veterinarian, 'expert', '专家');
      return list;
    },
    filteredContacts: function () {
      var kw = this.contactKeyword.trim().toLowerCase();
      if (!kw) return this.caseChatContacts;
      return this.caseChatContacts.filter(function (p) {
        var name = (p.username || '').toLowerCase();
        var role = (p.roleLabel || p.role || '').toLowerCase();
        return name.indexOf(kw) !== -1 || role.indexOf(kw) !== -1;
      });
    },
    visibleMessages: function () {
      if (!this.currentCase || !this.chatTarget) return [];
      return this.messagesWithPerson(this.chatTarget);
    },
    displayMessages: function () {
      var list = [];
      var lastTime = '';
      this.visibleMessages.forEach(function (msg) {
        var t = msg.time || '';
        if (t && t !== lastTime) {
          list.push({ type: 'time', text: t });
          lastTime = t;
        }
        list.push({ type: 'msg', data: msg });
      });
      return list;
    },
    filteredArchive: function () {
      return FishAPI.listArchivedCases({
        tab: this.archiveTab === 'all' ? '' : this.archiveTab,
        category: this.archiveCategory,
        keyword: this.archiveKeyword
      });
    },
    archiveCategoryLabel: function () {
      if (this.archiveCategory === 'all') return '全部分类';
      return this.archiveCategory;
    },
    logRangeLabel: function () {
      var opt = this.logRangeOptions.find(function (o) { return o.value === this.logRange; }.bind(this));
      return opt ? opt.label : '今天';
    },
    notifyMethodLabel: function () {
      var opt = this.notifyMethodOptions.find(function (o) { return o.value === this.systemSettings.notifyMethod; }.bind(this));
      return opt ? opt.label : '邮件';
    },
    configDialogTitle: function () {
      if (this.configTab === 'equipment') return (this.configForm.id ? '编辑设备' : '添加设备');
      return (this.configForm.id ? '编辑' : '添加') + this.configTabLabel;
    },
    hasAbnormalMetric: function () {
      return this.metrics.some(function (m) { return m.abnormal; });
    },
    configTabTitle: function () {
      return { breeding: '鱼品种管理', disease: '病害类型预设', equipment: '设备关联配置' }[this.configTab];
    },
    configTabDesc: function () {
      return {
        breeding: '管理养殖鱼品种、养殖池编号、鱼苗来源等基础信息',
        disease: '管理常见病害名称及分类',
        equipment: '管理监测设备及数据采集频率'
      }[this.configTab];
    },
    configTabLabel: function () {
      return { breeding: '品种', disease: '病害', equipment: '设备' }[this.configTab];
    },
    configListEmpty: function () {
      if (this.configTab === 'breeding') return !this.speciesList.length;
      if (this.configTab === 'disease') return !this.diseaseTypes.length;
      return !this.equipments.length;
    }
  },
  created: function () {
    if (this.loggedIn) {
      this.currentUser = FishAuth.getUser() || this.currentUser;
      this.page = this.resolveHomePage(this.currentUser);
      this.refreshAll();
      if (this.page === 'diagnosis' && this.filteredCases.length) {
        this.selectCase(this.filteredCases[0]);
      }
    }
  },
  mounted: function () {
    var self = this;
    this._closeDropdowns = function () {
      self.categoryMenuOpen = false;
      self.logRangeMenuOpen = false;
      self.notifyMenuOpen = false;
      self.userMenuOpen = false;
    };
    document.addEventListener('click', this._closeDropdowns);
  },
  beforeDestroy: function () {
    document.body.style.overflow = '';
    document.removeEventListener('click', this._closeDropdowns);
  },
  methods: {
    openNav: function () {
      this.menuOpen = true;
      document.body.style.overflow = 'hidden';
    },
    closeNav: function () {
      this.menuOpen = false;
      document.body.style.overflow = '';
    },
    showToast: function (msg) {
      var self = this;
      this.toast = msg;
      setTimeout(function () { self.toast = ''; }, 2200);
    },
    switchAuthMode: function (mode) {
      this.authMode = mode;
      this.authError = '';
    },
    applyAuth: function (data) {
      FishAuth.setAuth({
        accessToken: data.accessToken,
        user: data.user
      });
      this.currentUser = data.user;
      this.loggedIn = true;
      this.authError = '';
      this.page = this.resolveHomePage(data.user);
      this.refreshAll();
      if (this.page === 'diagnosis' && this.filteredCases.length) {
        this.selectCase(this.filteredCases[0]);
      }
    },
    resolveHomePage: function (user) {
      if (user && user.role === 'worker') return 'diagnosis';
      if (user && user.role === 'admin') return 'config';
      return 'diagnosis';
    },
    handleLogin: function () {
      if (!this.loginForm.account.trim() || !this.loginForm.password) {
        this.authError = '请输入账号和密码';
        return;
      }
      this.authLoading = true;
      this.authError = '';
      var res = FishAPI.login({
        account: this.loginForm.account.trim(),
        password: this.loginForm.password
      });
      this.authLoading = false;
      if (!res.ok) {
        this.authError = res.message || '登录失败';
        return;
      }
      this.applyAuth(res.data);
      this.showToast('登录成功');
    },
    handleRegister: function () {
      if (!this.registerForm.username.trim()) {
        this.authError = '请填写用户名';
        return;
      }
      if (!this.registerForm.email.trim()) {
        this.authError = '请填写邮箱';
        return;
      }
      if (this.registerForm.password.length < 6) {
        this.authError = '密码至少 6 位';
        return;
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.authError = '两次输入的密码不一致';
        return;
      }
      this.authLoading = true;
      this.authError = '';
      var res = FishAPI.register({
        username: this.registerForm.username.trim(),
        email: this.registerForm.email.trim(),
        password: this.registerForm.password,
        role: this.registerForm.role
      });
      this.authLoading = false;
      if (!res.ok) {
        this.authError = res.message || '注册失败';
        return;
      }
      this.applyAuth(res.data);
      this.showToast('注册成功，已自动登录');
    },
    handleLogout: function () {
      FishAuth.clearAuth();
      this.loggedIn = false;
      this.userMenuOpen = false;
      this.authMode = 'login';
      this.authError = '';
      this.showToast('已退出登录');
    },
    switchAccount: function () {
      this.authMode = 'login';
      this.handleLogout();
    },
    switchSystemTab: function (tab) {
      this.systemTab = tab;
      if (tab === 'logs') this.loadLogs();
      else if (tab === 'users') this.systemUsers = FishAPI.listSystemUsers();
      else if (tab === 'settings') this.systemSettings = FishAPI.getSystemSettings();
    },
    loadLogs: function () {
      this.operationLogs = FishAPI.listOperationLogs({ range: this.logRange });
    },
    selectLogRange: function (value) {
      this.logRange = value;
      this.logRangeMenuOpen = false;
      this.loadLogs();
    },
    selectNotifyMethod: function (value) {
      this.systemSettings.notifyMethod = value;
      this.notifyMenuOpen = false;
    },
    emptyUserForm: function () {
      return { id: '', username: '', password: '', role: '', department: '' };
    },
    openUserDialog: function (user) {
      if (user && user.id) {
        this.userForm = {
          id: user.id,
          username: user.username,
          password: '',
          role: user.role,
          department: user.department || ''
        };
      } else {
        this.userForm = this.emptyUserForm();
      }
      this.userVisible = true;
    },
    viewUser: function (user) {
      alert('用户名：' + user.username + '\n角色：' + user.roleLabel + '\n部门：' + (user.department || '—') + '\n邮箱：' + (user.email || '—'));
    },
    saveUser: function () {
      if (!this.userForm.username.trim()) {
        this.showToast('请填写用户名');
        return;
      }
      if (!this.userForm.role) {
        this.showToast('请选择角色');
        return;
      }
      if (!this.userForm.id && (!this.userForm.password || this.userForm.password.length < 6)) {
        this.showToast('密码至少 6 位');
        return;
      }
      this.savingUser = true;
      var payload = {
        username: this.userForm.username.trim(),
        role: this.userForm.role,
        department: this.userForm.department.trim(),
        email: this.userForm.username.trim() + '@system.com'
      };
      if (this.userForm.password) payload.password = this.userForm.password;
      if (this.userForm.id) FishAPI.updateSystemUser(this.userForm.id, payload);
      else FishAPI.createSystemUser(payload);
      this.systemUsers = FishAPI.listSystemUsers();
      this.userVisible = false;
      this.savingUser = false;
      this.showToast('用户已保存');
    },
    handleDeleteUser: function (user) {
      if (!confirm('确认删除用户「' + user.username + '」？')) return;
      FishAPI.deleteSystemUser(user.id);
      this.systemUsers = FishAPI.listSystemUsers();
      this.showToast('已删除');
    },
    switchPage: function (path) {
      this.page = path;
      this.closeNav();
      if (path === 'diagnosis' && !this.currentCase && this.filteredCases.length) {
        this.selectCase(this.filteredCases[0]);
      }
    },
    refreshAll: function () {
      this.meta = FishAPI.getMeta();
      this.summary = FishAPI.getSummary();
      this.diagnosisList = FishAPI.listDiagnosis({});
      this.metrics = FishAPI.getMonitorData();
      this.speciesList = FishAPI.listFishSpecies();
      this.diseaseTypes = FishAPI.listDiseaseTypes();
      this.equipments = FishAPI.listEquipments();
      this.archiveSummary = FishAPI.getArchiveSummary();
      this.archiveCategories = FishAPI.getArchiveCategories();
      this.systemUsers = FishAPI.listSystemUsers();
      this.loadLogs();
      this.systemSettings = FishAPI.getSystemSettings();
    },
    equipmentSubText: function (item) {
      var type = item.type || item.name || '';
      var freq = item.frequency || '';
      return freq ? type + ' · 采集频率: ' + freq : type;
    },
    selectArchiveCategory: function (value) {
      this.archiveCategory = value;
      this.categoryMenuOpen = false;
    },
    saveSystemSettings: function () {
      FishAPI.updateSystemSettings(this.systemSettings);
      this.showToast('配置已保存');
    },
    goDiagnosis: function (row) {
      this.switchPage('diagnosis');
      this.selectCase(row);
    },
    switchDiagTab: function (key) {
      this.diagTab = key;
      var list = this.filteredCases;
      if (list.length) this.selectCase(list[0]);
      else {
        this.currentCase = null;
        this.chatTarget = null;
      }
    },
    formatCaseTime: function (item) {
      var t = item.createdAt || item.updatedAt || '';
      return t.length > 16 ? t.slice(0, 16) : t;
    },
    statusIconName: function (status) {
      return status === 'resolved' ? 'check-circle' : 'clock';
    },
    messagesWithPerson: function (person) {
      if (!this.currentCase || !person) return [];
      var meName = this.currentUser.username;
      var targetName = person.username;
      return (this.currentCase.messages || []).filter(function (m) {
        if (m.toName) {
          return (m.senderName === meName && m.toName === targetName) ||
            (m.senderName === targetName && m.toName === meName);
        }
        return m.senderName === meName || m.senderName === targetName;
      });
    },
    contactPreview: function (person) {
      var list = this.messagesWithPerson(person);
      if (!list.length) return person.roleLabel || '暂无消息';
      var last = list[list.length - 1];
      var prefix = last.senderName === this.currentUser.username ? '我: ' : '';
      var text = prefix + last.content;
      return text.length > 22 ? text.slice(0, 22) + '…' : text;
    },
    contactLastTime: function (person) {
      var list = this.messagesWithPerson(person);
      if (!list.length) return '';
      return list[list.length - 1].time || '';
    },
    ensureChatTarget: function () {
      var list = this.caseChatContacts;
      if (!list.length) {
        this.chatTarget = null;
        return;
      }
      if (this.chatTarget && list.some(function (u) {
        return u.id === this.chatTarget.id || u.username === this.chatTarget.username;
      }.bind(this))) {
        this.chatTarget = list.find(function (u) {
          return u.id === this.chatTarget.id || u.username === this.chatTarget.username;
        }.bind(this));
        return;
      }
      var preferred = null;
      if (this.currentUser.role === 'worker') {
        preferred = list.find(function (u) { return u.username === this.currentCase.veterinarian; }.bind(this)) ||
          list.find(function (u) { return u.role === 'expert'; });
      } else {
        preferred = list.find(function (u) { return u.username === this.currentCase.reporter; }.bind(this)) ||
          list.find(function (u) { return u.role === 'worker'; });
      }
      this.chatTarget = preferred || list[0];
    },
    selectChatTarget: function (person) {
      this.chatTarget = person;
      this.scrollChat();
    },
    selectCase: function (item) {
      this.currentCase = FishAPI.getDiagnosis(item.id);
      this.chatInput = '';
      this.contactKeyword = '';
      this.ensureChatTarget();
      this.scrollChat();
    },
    scrollChat: function () {
      var self = this;
      this.$nextTick(function () {
        var el = self.$refs.chatBody;
        if (el) el.scrollTop = el.scrollHeight;
      });
    },
    msgIsMine: function (msg) {
      if (!msg) return false;
      if (msg.senderId && this.currentUser.id) return msg.senderId === this.currentUser.id;
      return msg.senderName === this.currentUser.username;
    },
    sendMessage: function () {
      if (!this.chatInput.trim() || !this.currentCase || !this.chatTarget) return;
      var role = this.currentUser.role === 'worker' ? 'worker' : 'expert';
      this.currentCase = FishAPI.appendMessage(this.currentCase.id, {
        role: role,
        senderName: this.currentUser.username,
        content: this.chatInput.trim()
      });
      this.chatInput = '';
      this.diagnosisList = FishAPI.listDiagnosis({});
      this.scrollChat();
    },
    openConclusion: function () {
      this.conclusionForm = {
        suspectedDisease: this.currentCase.suspectedDisease || '',
        treatment: this.currentCase.treatment || '',
        conclusion: this.currentCase.conclusion || ''
      };
      this.conclusionVisible = true;
    },
    saveConclusion: function () {
      if (!this.conclusionForm.suspectedDisease.trim()) {
        this.showToast('请填写诊断结论');
        return;
      }
      this.currentCase = FishAPI.updateDiagnosis(this.currentCase.id, {
        suspectedDisease: this.conclusionForm.suspectedDisease,
        treatment: this.conclusionForm.treatment,
        conclusion: this.conclusionForm.conclusion || this.conclusionForm.suspectedDisease,
        status: 'resolved', veterinarian: this.currentUser.username
      });
      FishAPI.appendMessage(this.currentCase.id, {
        role: 'expert', senderName: this.currentUser.username,
        content: '诊断结论：' + this.conclusionForm.suspectedDisease + '。' + (this.conclusionForm.treatment || '')
      });
      this.currentCase = FishAPI.getDiagnosis(this.currentCase.id);
      this.conclusionVisible = false;
      this.refreshAll();
      this.showToast('诊断结论已保存');
    },
    mockUpload: function () {
      this.mediaFiles.push({ name: 'demo-photo-' + (this.mediaFiles.length + 1) + '.jpg', size: '1.2 MB' });
      this.showToast('演示环境：已模拟上传文件');
    },
    onUploadDrop: function () {
      this.uploadDragOver = false;
      this.mockUpload();
    },
    submitCollect: function () {
      if (!this.collectForm.species || !this.collectForm.pondCode) {
        this.showToast('请填写鱼品种和养殖池编号');
        return;
      }
      FishAPI.createCollection(Object.assign({}, this.collectForm, { mediaCount: this.mediaFiles.length }));
      var diag = FishAPI.createDiagnosis({
        pondCode: this.collectForm.pondCode,
        category: this.collectForm.species,
        reporter: '张工',
        physicalSymptoms: this.collectForm.abnormalParts.length ? ['体表充血'] : [],
        behavioralSymptoms: this.collectForm.abnormalDesc ? ['摄食下降'] : []
      });
      this.collectForm = { species: '', pondCode: '', frySource: '', diseaseCount: '', feedingMethod: '', abnormalParts: [], abnormalDesc: '' };
      this.mediaFiles = [];
      this.refreshAll();
      this.showToast('诊断申请已提交：' + diag.caseNo);
      this.switchPage('diagnosis');
      this.selectCase(diag);
    },
    openConfigDialog: function (row) {
      if (this.configTab === 'breeding') this.configForm = row && row.id ? Object.assign({}, row) : { name: '', waterType: 'marine' };
      else if (this.configTab === 'disease') this.configForm = row && row.id ? Object.assign({}, row) : { name: '', category: '细菌' };
      else this.configForm = row && row.id ? Object.assign({}, row) : { code: '', name: '', type: '', frequency: '' };
      this.configVisible = true;
    },
    saveConfig: function () {
      if (this.configTab === 'breeding') {
        if (!this.configForm.name) { this.showToast('请输入品种名称'); return; }
        if (this.configForm.id) FishAPI.updateFishSpecies(this.configForm.id, this.configForm);
        else FishAPI.createFishSpecies(this.configForm);
        this.speciesList = FishAPI.listFishSpecies();
      } else if (this.configTab === 'disease') {
        if (!this.configForm.name) { this.showToast('请输入病害名称'); return; }
        if (this.configForm.id) FishAPI.updateDiseaseType(this.configForm.id, this.configForm);
        else FishAPI.createDiseaseType(this.configForm);
        this.diseaseTypes = FishAPI.listDiseaseTypes();
      } else {
        if (!this.configForm.code) { this.showToast('请输入设备编号'); return; }
        if (!this.configForm.type) { this.showToast('请选择设备类型'); return; }
        if (!this.configForm.frequency) { this.showToast('请选择采集频率'); return; }
        var payload = Object.assign({}, this.configForm, { name: this.configForm.type || this.configForm.name });
        if (this.configForm.id) FishAPI.updateEquipment(this.configForm.id, payload);
        else FishAPI.createEquipment(payload);
        this.equipments = FishAPI.listEquipments();
      }
      this.configVisible = false;
      this.showToast('保存成功');
    },
    deleteSpecies: function (row) {
      if (!confirm('确认删除品种「' + row.name + '」？')) return;
      FishAPI.deleteFishSpecies(row.id);
      this.speciesList = FishAPI.listFishSpecies();
      this.showToast('已删除');
    },
    deleteDiseaseType: function (row) {
      if (!confirm('确认删除病害「' + row.name + '」？')) return;
      FishAPI.deleteDiseaseType(row.id);
      this.diseaseTypes = FishAPI.listDiseaseTypes();
      this.showToast('已删除');
    },
    deleteEquipment: function (row) {
      if (!confirm('确认删除设备「' + row.code + '」？')) return;
      FishAPI.deleteEquipment(row.id);
      this.equipments = FishAPI.listEquipments();
      this.showToast('已删除');
    },
    openArchiveDetail: function (item) {
      this.archiveDetail = FishAPI.getArchivedCase(item.id);
      this.archiveDetailVisible = true;
    }
  }
}
</script>

<template>
  <div id="app">
    <div v-if="!loggedIn" class="login-page">
      <div class="login-card">
        <div class="login-brand">
          <div class="brand-title">循环水养殖</div>
          <div class="brand-sub">病害诊断系统</div>
        </div>
        <div class="mode-tabs">
          <button type="button" class="mode-tab" :class="{ active: authMode === 'login' }" @click="switchAuthMode('login')">登录</button>
          <button type="button" class="mode-tab" :class="{ active: authMode === 'register' }" @click="switchAuthMode('register')">注册</button>
        </div>
        <p class="login-desc">{{ authMode === 'login' ? '请使用系统账号登录后继续操作' : '注册新账号，加入协同诊断系统' }}</p>
        <div v-if="authError" class="login-error">{{ authError }}</div>
        <form v-if="authMode === 'login'" class="login-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <input v-model="loginForm.account" class="form-control" placeholder="用户名或邮箱" />
          </div>
          <div class="form-group">
            <input v-model="loginForm.password" type="password" class="form-control" placeholder="密码" />
          </div>
          <button type="submit" class="login-btn" :disabled="authLoading">{{ authLoading ? '登录中…' : '登录' }}</button>
        </form>
        <form v-else class="login-form" @submit.prevent="handleRegister">
          <div class="form-group">
            <input v-model="registerForm.username" class="form-control" placeholder="用户名" />
          </div>
          <div class="form-group">
            <input v-model="registerForm.email" class="form-control" placeholder="邮箱" />
          </div>
          <div class="form-group">
            <select v-model="registerForm.role" class="form-control">
              <option value="worker">工人</option>
              <option value="expert">专家</option>
            </select>
          </div>
          <div class="form-group">
            <input v-model="registerForm.password" type="password" class="form-control" placeholder="密码（至少 6 位）" />
          </div>
          <div class="form-group">
            <input v-model="registerForm.confirmPassword" type="password" class="form-control" placeholder="确认密码" />
          </div>
          <button type="submit" class="login-btn" :disabled="authLoading">{{ authLoading ? '注册中…' : '注册并登录' }}</button>
        </form>
        <div v-if="authMode === 'login'" class="login-hint">
          <div>演示账号（密码均为 123456）</div>
          <div>admin@system.com · 陈专家 · 张工</div>
        </div>
      </div>
    </div>

    <div v-else class="app-layout">
      <div class="nav-backdrop" :class="{ open: menuOpen }" @click="closeNav"></div>

      <aside class="sidebar" :class="{ open: menuOpen }">
        <div class="sidebar-brand">
          <div>
            <div class="brand-title">循环水养殖</div>
            <div class="brand-sub">病害诊断系统</div>
          </div>
          <button type="button" class="nav-close" aria-label="关闭导航" @click="closeNav">×</button>
        </div>
        <nav class="sidebar-nav">
          <button v-for="item in menus" :key="item.path" type="button" class="nav-item"
                  :class="{ active: page === item.path }" @click="switchPage(item.path)">
            <nav-icon :name="item.icon"></nav-icon>
            <span>{{ item.label }}</span>
          </button>
        </nav>
        <div class="sidebar-user" @click.stop="userMenuOpen = !userMenuOpen">
          <div class="user-avatar">{{ currentUser.username.slice(0, 1) }}</div>
          <div style="min-width:0">
            <div class="user-name">{{ currentUser.username }}</div>
            <div class="user-email">{{ currentUser.email || '—' }}</div>
          </div>
          <span class="user-caret" :class="{ open: userMenuOpen }">▾</span>
          <div v-if="userMenuOpen" class="user-menu" @click.stop>
            <div class="user-menu-meta">
              <div class="user-menu-name">{{ currentUser.username }}</div>
              <div class="user-menu-role">{{ currentUser.roleLabel }}</div>
            </div>
            <button type="button" class="user-menu-item" @click="switchAuthMode('login'); handleLogout()">
              <ui-icon name="user"></ui-icon> 切换账号
            </button>
            <button type="button" class="user-menu-item danger" @click="handleLogout">
              <ui-icon name="logout"></ui-icon> 退出登录
            </button>
          </div>
        </div>
      </aside>

      <div class="main-wrap">
        <header v-if="!isModulePage" class="page-header">
          <div class="header-row">
            <button type="button" class="menu-toggle" aria-label="打开导航" @click="openNav">
              <span class="menu-toggle-bar"></span>
              <span class="menu-toggle-bar"></span>
              <span class="menu-toggle-bar"></span>
            </button>
            <div>
              <div class="breadcrumb">
              <span v-for="(crumb, idx) in breadcrumbs" :key="idx">
                <span v-if="idx > 0" class="sep">/</span>
                <span :class="{ current: idx === breadcrumbs.length - 1 }">{{ crumb }}</span>
              </span>
              </div>
              <h1 class="page-title">{{ pageTitle }}</h1>
            </div>
          </div>
        </header>

        <main class="app-main" :class="{ 'is-module': isModulePage }">
          <!-- 基础配置 -->
          <section v-show="page === 'config'" class="config-page">
            <header class="config-header">
              <div class="header-top">
                <div class="header-title-row">
                  <button type="button" class="menu-toggle" @click="openNav"><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span></button>
                  <h1 class="header-title">基础配置</h1>
                </div>
                <header-auth :user="currentUser" @logout="handleLogout" @switch-account="switchAccount"></header-auth>
              </div>
              <p class="header-desc">管理支撑全流程的基础信息，减少输入成本，规范数据格式</p>
            </header>
            <div class="config-body">
              <div class="config-tabs">
                <button v-for="tab in configTabs" :key="tab.key" type="button" class="config-tab"
                        :class="{ active: configTab === tab.key }" @click="configTab = tab.key">{{ tab.label }}</button>
              </div>
              <div class="content-card">
                <div class="section-head">
                  <div>
                    <h2 class="section-title">{{ configTabTitle }}</h2>
                    <p class="section-desc">{{ configTabDesc }}</p>
                  </div>
                  <button type="button" class="btn-add" @click="openConfigDialog()">+ 添加{{ configTabLabel }}</button>
                </div>
                <div class="item-list">
                  <template v-if="configTab === 'breeding'">
                    <div v-for="item in speciesList" :key="item.id" class="item-row">
                      <div><div class="item-name">{{ item.name }}</div><div class="item-sub">{{ item.waterTypeLabel }}</div></div>
                      <div class="item-actions">
                        <button type="button" class="icon-btn" title="编辑" @click="openConfigDialog(item)"><ui-icon name="edit"></ui-icon></button>
                        <button type="button" class="icon-btn danger" title="删除" @click="deleteSpecies(item)"><ui-icon name="delete"></ui-icon></button>
                      </div>
                    </div>
                  </template>
                  <template v-else-if="configTab === 'disease'">
                    <div v-for="item in diseaseTypes" :key="item.id" class="item-row">
                      <div><div class="item-name">{{ item.name }}</div><div class="item-sub">{{ item.category }}</div></div>
                      <div class="item-actions">
                        <button type="button" class="icon-btn" title="编辑" @click="openConfigDialog(item)"><ui-icon name="edit"></ui-icon></button>
                        <button type="button" class="icon-btn danger" title="删除" @click="deleteDiseaseType(item)"><ui-icon name="delete"></ui-icon></button>
                      </div>
                    </div>
                  </template>
                  <template v-else>
                    <div v-for="item in equipments" :key="item.id" class="item-row">
                      <div><div class="item-name">{{ item.code }}</div><div class="item-sub">{{ equipmentSubText(item) }}</div></div>
                      <div class="item-actions">
                        <button type="button" class="icon-btn" title="编辑" @click="openConfigDialog(item)"><ui-icon name="edit"></ui-icon></button>
                        <button type="button" class="icon-btn danger" title="删除" @click="deleteEquipment(item)"><ui-icon name="delete"></ui-icon></button>
                      </div>
                    </div>
                  </template>
                  <div v-if="configListEmpty" class="list-empty">暂无数据</div>
                </div>
              </div>
            </div>
          </section>

          <!-- 数据采集 -->
          <section v-show="page === 'collect'" class="collect-page">
            <header class="collect-header">
              <div class="header-top">
                <div class="header-title-row">
                  <button type="button" class="menu-toggle" @click="openNav"><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span></button>
                  <h1 class="header-title">数据采集</h1>
                </div>
                <header-auth :user="currentUser" @logout="handleLogout" @switch-account="switchAccount"></header-auth>
              </div>
              <p class="header-desc">全面收集病害诊断所需的视觉素材、基础信息、设备数据和解剖信息</p>
            </header>
            <div class="collect-body">
              <div class="collect-grid">
                <div class="module-card">
                  <h2 class="module-title">视觉素材上传</h2>
                  <p class="module-desc">上传体表照片、解剖照片或异常行为视频</p>
                  <div
                    class="upload-zone"
                    :class="{ 'is-dragover': uploadDragOver }"
                    @click="mockUpload"
                    @dragover.prevent="uploadDragOver = true"
                    @dragleave.prevent="uploadDragOver = false"
                    @drop.prevent="onUploadDrop"
                  >
                    <ui-icon name="cloud-upload" class="upload-icon-svg"></ui-icon>
                    <div class="upload-main">点击上传或拖拽文件至此</div>
                    <div class="upload-hint">支持图片和视频格式</div>
                  </div>
                  <div v-if="mediaFiles.length" class="media-list">
                    <div v-for="(file, idx) in mediaFiles" :key="idx" class="media-item">
                      <div class="media-meta">
                        <div class="media-name">{{ file.name }}</div>
                        <div class="media-size">{{ file.size || '演示文件' }}</div>
                      </div>
                      <button type="button" class="media-remove" @click.stop="mediaFiles.splice(idx, 1)"><ui-icon name="close"></ui-icon></button>
                    </div>
                  </div>
                  <div class="action-row">
                    <button type="button" class="action-btn" @click="mockUpload"><ui-icon name="camera"></ui-icon><span>拍照</span></button>
                    <button type="button" class="action-btn" @click="mockUpload"><ui-icon name="video"></ui-icon><span>录像</span></button>
                  </div>
                </div>
                <div class="module-card">
                  <h2 class="module-title">基础信息</h2>
                  <p class="module-desc">填写鱼品种、养殖池等基本信息</p>
                  <div class="form-group">
                    <label class="form-label">鱼品种 <span class="required-star">*</span></label>
                    <select v-model="collectForm.species" class="form-control">
                      <option value="">选择鱼品种</option>
                      <option v-for="s in speciesList" :key="s.id" :value="s.name">{{ s.name }}</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label class="form-label">养殖池编号 <span class="required-star">*</span></label>
                    <input v-model="collectForm.pondCode" class="form-control" placeholder="例如：A-01" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">鱼苗来源</label>
                    <input v-model="collectForm.frySource" class="form-control" placeholder="例如：福建育苗场" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">发病数量</label>
                    <input v-model="collectForm.diseaseCount" class="form-control" placeholder="例如：50" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">投喂方式</label>
                    <select v-model="collectForm.feedingMethod" class="form-control">
                      <option value="">选择投喂方式</option>
                      <option v-for="m in meta.feedingMethods" :key="m" :value="m">{{ m }}</option>
                    </select>
                  </div>
                </div>
                <div class="module-card">
                  <h2 class="module-title">设备监测数据</h2>
                  <p class="module-desc">从自动化设备同步的水质和行为数据</p>
                  <div class="metric-list">
                    <div v-for="item in metrics" :key="item.key" class="metric-card" :class="{ abnormal: item.abnormal }">
                      <div class="metric-main">
                        <div class="metric-label">{{ item.label }}</div>
                        <div class="metric-value"><span class="num">{{ item.value }}</span><span v-if="item.unit" class="unit">{{ item.unit }}</span></div>
                        <div class="metric-range">正常范围：{{ item.rangeText }}</div>
                      </div>
                      <span v-if="item.abnormal" class="abnormal-badge"><span class="badge-icon">!</span> 异常</span>
                    </div>
                    <div v-if="hasAbnormalMetric" class="water-alert"><span class="alert-icon">!</span> 检测到异常水质指标，请及时关注</div>
                  </div>
                </div>
                <div class="module-card module-card--anatomy">
                  <h2 class="module-title">解剖信息</h2>
                  <p class="module-desc">勾选异常部位并描述具体表现</p>
                  <div class="field-block">
                    <div class="field-label">异常部位</div>
                    <div class="part-checks">
                      <label v-for="part in meta.anatomyParts" :key="part" class="part-check">
                        <input type="checkbox" :value="part" v-model="collectForm.abnormalParts" /> {{ part }}
                      </label>
                    </div>
                  </div>
                  <div class="field-block">
                    <div class="field-label">异常表现描述</div>
                    <textarea v-model="collectForm.abnormalDesc" class="form-control" placeholder="详细描述观察到的异常表现，如：鳃部发白、肝脏肿大等"></textarea>
                  </div>
                </div>
              </div>
              <div class="footer-bar">
                <button type="button" class="btn-export" @click="showToast('演示环境：导出功能暂未开放')"><ui-icon name="upload2"></ui-icon> 导出数据</button>
                <button type="button" class="btn-submit" @click="submitCollect">提交诊断申请</button>
              </div>
            </div>
          </section>

          <!-- 协同诊断 -->
          <section v-show="page === 'diagnosis'" class="diagnosis-page">
            <header class="diagnosis-header">
              <div class="header-top">
                <div class="header-title-row">
                  <button type="button" class="menu-toggle" @click="openNav"><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span></button>
                  <h1 class="header-title">协同诊断</h1>
                </div>
                <header-auth :user="currentUser" @logout="handleLogout" @switch-account="switchAccount"></header-auth>
              </div>
              <p class="header-desc">工人与专家协同诊断，实时沟通并输出诊断结论与治疗方案</p>
            </header>
            <div class="diagnosis-body">
              <aside class="task-panel">
                <div class="panel-head">
                  <h2 class="panel-title">诊断任务</h2>
                  <p class="panel-desc">选择任务进行诊断</p>
                </div>
                <div class="filter-tabs">
                  <button v-for="tab in diagTabs" :key="tab.key" type="button" class="filter-tab"
                          :class="{ active: diagTab === tab.key }" @click="switchDiagTab(tab.key)">{{ tab.label }}</button>
                </div>
                <div class="task-list">
                  <div v-for="item in filteredCases" :key="item.id" class="task-card"
                       :class="{ active: currentCase && currentCase.id === item.id }" @click="selectCase(item)">
                    <div class="task-card-top">
                      <span class="task-id">{{ item.caseNo }}</span>
                      <span class="task-status" :class="item.status">
                      <ui-icon :name="statusIconName(item.status)"></ui-icon>
                      {{ item.statusLabel }}
                    </span>
                    </div>
                    <div class="task-detail">{{ item.pondCode }} · {{ item.category || '未指定品种' }}</div>
                    <div class="task-meta">{{ item.reporter || '—' }} · {{ formatCaseTime(item) }}</div>
                  </div>
                  <div v-if="!filteredCases.length" class="list-empty">暂无{{ activeDiagTabLabel }}任务</div>
                </div>
              </aside>
              <section class="chat-panel">
                <template v-if="currentCase">
                  <aside class="contact-pane">
                    <div class="contact-pane-head"><span>沟通窗口</span><span class="ws-dot online"></span></div>
                    <div class="contact-pane-sub">{{ currentCase.caseNo }} · {{ currentCase.pondCode }}</div>
                    <div class="contact-search">
                      <ui-icon name="search"></ui-icon>
                      <input v-model="contactKeyword" type="text" class="contact-search-input" placeholder="搜索联系人" />
                      <button v-if="contactKeyword" type="button" class="contact-search-clear" @click="contactKeyword = ''"><ui-icon name="close"></ui-icon></button>
                    </div>
                    <div class="contact-list">
                      <button v-for="person in filteredContacts" :key="person.id" type="button" class="contact-item"
                              :class="{ active: chatTarget && chatTarget.id === person.id }" @click="selectChatTarget(person)">
                        <span class="contact-avatar">{{ person.username.slice(0, 1) }}</span>
                        <span class="contact-body">
                        <span class="contact-top">
                          <span class="contact-name">{{ person.username }}</span>
                          <span class="contact-time">{{ contactLastTime(person) }}</span>
                        </span>
                        <span class="contact-preview">{{ contactPreview(person) }}</span>
                      </span>
                      </button>
                      <div v-if="!filteredContacts.length" class="contact-empty">{{ contactKeyword ? '未找到匹配联系人' : '暂无可选联系人' }}</div>
                    </div>
                  </aside>
                  <div class="session-pane">
                    <div class="session-head">
                      <div class="session-head-info">
                        <h2 class="session-title">{{ chatTarget ? chatTarget.username : '请选择聊天对象' }}</h2>
                        <p v-if="chatTarget" class="session-sub">{{ chatTarget.roleLabel }} · {{ currentCase.caseNo }}</p>
                      </div>
                      <button v-if="currentCase.status !== 'resolved'" type="button" class="btn-conclusion" @click="openConclusion">填写诊断结论</button>
                    </div>
                    <div ref="chatBody" class="chat-body">
                      <template v-if="chatTarget">
                        <template v-for="(item, idx) in displayMessages">
                          <div v-if="item.type === 'time'" :key="'t-' + idx" class="msg-time-sep">{{ item.text }}</div>
                          <div v-else :key="item.data.id" class="msg-row"
                               :class="msgIsMine(item.data) ? 'is-mine' : 'is-peer'">
                            <span class="msg-avatar" :class="{ mine: msgIsMine(item.data) }">{{ item.data.senderName.slice(0, 1) }}</span>
                            <div class="msg-bubble">{{ item.data.content }}</div>
                          </div>
                        </template>
                        <div v-if="!visibleMessages.length" class="chat-thread-empty">还没有与 {{ chatTarget.username }} 的消息，发送第一条吧</div>
                      </template>
                      <div v-else class="chat-thread-empty">请从左侧选择聊天对象</div>
                    </div>
                    <div class="chat-composer">
                      <div class="composer-tools">
                        <button type="button" class="tool-btn" title="上传图片" :disabled="!chatTarget" @click="showToast('演示环境：图片上传暂未开放')"><ui-icon name="picture"></ui-icon></button>
                        <button type="button" class="tool-btn" title="文件" :disabled="!chatTarget" @click="showToast('演示环境：文件上传暂未开放')"><ui-icon name="folder"></ui-icon></button>
                        <button type="button" class="tool-btn" title="语音" :disabled="!chatTarget" @click="showToast('演示环境：语音暂未开放')"><ui-icon name="microphone"></ui-icon></button>
                      </div>
                      <textarea v-model="chatInput" class="chat-input" rows="3"
                                :placeholder="chatTarget ? '输入消息...' : '请先选择聊天对象'"
                                :disabled="!chatTarget"
                                @keydown.enter.exact.prevent="sendMessage"></textarea>
                      <div class="composer-footer">
                        <span class="composer-tip">Enter 发送</span>
                        <button type="button" class="btn-send" :disabled="!chatInput.trim() || !chatTarget" @click="sendMessage">发送</button>
                      </div>
                    </div>
                  </div>
                </template>
                <div v-else class="chat-empty">
                  <div class="empty-icon">💬</div>
                  <div>请选择诊断任务</div>
                </div>
              </section>
            </div>
          </section>

          <!-- 案例归档 -->
          <section v-show="page === 'archive'" class="archive-page">
            <header class="archive-header">
              <div class="header-top">
                <div class="header-title-row">
                  <button type="button" class="menu-toggle" @click="openNav"><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span></button>
                  <h1 class="header-title">案例归档</h1>
                </div>
                <header-auth :user="currentUser" @logout="handleLogout" @switch-account="switchAccount"></header-auth>
              </div>
              <p class="header-desc">归档历史案例，构建经验库，支持智能检索和自动初判</p>
            </header>
            <div class="archive-body">
              <section class="ai-banner">
                <div class="ai-banner-head">
                  <ui-icon name="chart" class="ai-icon"></ui-icon>
                  <div>
                    <h2 class="ai-title">智能初判系统</h2>
                    <p class="ai-desc">基于历史案例库的 AI 辅助诊断功能（后续迭代开放）</p>
                  </div>
                </div>
                <div class="ai-info-card">
                  <p>当匹配度 ≥ 80% 时，系统将自动显示初步诊断结果，供专家参考</p>
                  <p class="ai-stats">当前案例库数量：{{ archiveSummary.total }} 例 · 预计开放时间：{{ archiveSummary.estimatedOpen }}</p>
                </div>
              </section>
              <section class="toolbar-card">
                <div class="search-row">
                  <div class="search-box">
                    <ui-icon name="search"></ui-icon>
                    <input v-model="archiveKeyword" type="text" placeholder="搜索案例编号、病害类型、标签..." />
                  </div>
                  <div class="toolbar-actions">
                    <div class="category-dropdown" @click.stop>
                      <button type="button" class="category-trigger" :class="{ open: categoryMenuOpen }" @click="categoryMenuOpen = !categoryMenuOpen">
                        <ui-icon name="filter"></ui-icon>
                        <span>{{ archiveCategoryLabel }}</span>
                        <span class="caret">▾</span>
                      </button>
                      <div v-if="categoryMenuOpen" class="category-menu">
                        <button type="button" class="category-option" :class="{ active: archiveCategory === 'all' }" @click="selectArchiveCategory('all')">
                          <span>全部分类</span><span v-if="archiveCategory === 'all'">✓</span>
                        </button>
                        <button v-for="c in archiveCategories" :key="c" type="button" class="category-option" :class="{ active: archiveCategory === c }" @click="selectArchiveCategory(c)">
                          <span>{{ c }}</span><span v-if="archiveCategory === c">✓</span>
                        </button>
                      </div>
                    </div>
                    <button type="button" class="btn-export" @click="showToast('演示环境：导出功能暂未开放')"><ui-icon name="download"></ui-icon> 导出</button>
                  </div>
                </div>
              </section>
              <div class="list-section">
                <div class="list-tabs">
                  <button type="button" class="list-tab" :class="{ active: archiveTab === 'all' }" @click="archiveTab = 'all'">全部案例 ({{ archiveSummary.total }})</button>
                  <button type="button" class="list-tab" :class="{ active: archiveTab === 'highMatch' }" @click="archiveTab = 'highMatch'">高匹配度案例 ({{ archiveSummary.highMatch }})</button>
                </div>
                <div class="case-list">
                  <article v-for="item in filteredArchive" :key="item.id" class="case-card">
                    <div class="case-head">
                      <span class="case-id">{{ item.archiveNo }}</span>
                      <span class="category-tag">{{ item.category }}</span>
                      <span v-if="item.isHighMatch" class="match-tag">匹配度 {{ item.matchRate }}%</span>
                    </div>
                    <div class="case-meta-row">
                      <p class="case-summary">{{ item.pondCode }} · {{ item.species }} · {{ item.diseaseName }} · {{ item.archiveDate }} · {{ item.expert }}</p>
                      <div class="case-meta-actions">
                        <span class="recovery-tag" :class="item.recoveryStatus">{{ item.recoveryLabel }}</span>
                        <button type="button" class="btn-detail" @click="openArchiveDetail(item)"><ui-icon name="view"></ui-icon> 查看详情</button>
                      </div>
                    </div>
                    <div v-if="item.tags && item.tags.length" class="case-tags">
                      <span v-for="tag in item.tags" :key="tag" class="case-tag"><ui-icon name="tag"></ui-icon> {{ tag }}</span>
                    </div>
                  </article>
                  <div v-if="!filteredArchive.length" class="list-empty">暂无归档案例</div>
                </div>
              </div>
            </div>
          </section>

          <!-- 系统管理 -->
          <section v-show="page === 'system'" class="system-page">
            <header class="system-header">
              <div class="header-top">
                <div class="header-title-row">
                  <button type="button" class="menu-toggle" @click="openNav"><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span><span class="menu-toggle-bar"></span></button>
                  <h1 class="header-title">系统管理</h1>
                </div>
                <header-auth :user="currentUser" @logout="handleLogout" @switch-account="switchAccount"></header-auth>
              </div>
              <p class="header-desc">保障系统安全稳定运行，管理用户权限和系统配置</p>
            </header>
            <div class="system-body">
              <div class="system-tabs">
                <button v-for="tab in systemTabs" :key="tab.key" type="button" class="system-tab"
                        :class="{ active: systemTab === tab.key }" @click="switchSystemTab(tab.key)">{{ tab.label }}</button>
              </div>
              <div v-if="systemTab === 'users'" class="content-card">
                <div class="section-head">
                  <div>
                    <h2 class="section-title">用户与权限管理</h2>
                    <p class="section-desc">管理系统用户账号、角色和权限分配</p>
                  </div>
                  <button type="button" class="btn-add" @click="openUserDialog()"><ui-icon name="user-solid"></ui-icon><span>添加用户</span></button>
                </div>
                <div class="table-wrap">
                  <table class="data-table">
                    <thead>
                    <tr><th>用户名</th><th>角色</th><th>部门</th><th>状态</th><th class="col-actions">操作</th></tr>
                    </thead>
                    <tbody>
                    <tr v-for="user in systemUsers" :key="user.id">
                      <td class="cell-name">{{ user.username }}</td>
                      <td><span class="role-tag" :class="user.role">{{ user.roleLabel }}</span></td>
                      <td class="cell-muted">{{ user.department }}</td>
                      <td><span class="status-tag">{{ user.statusLabel }}</span></td>
                      <td class="col-actions">
                        <div class="action-btns">
                          <button type="button" class="icon-btn" title="查看" @click="viewUser(user)"><ui-icon name="view"></ui-icon></button>
                          <button type="button" class="icon-btn" title="编辑" @click="openUserDialog(user)"><ui-icon name="edit"></ui-icon></button>
                          <button type="button" class="icon-btn danger" title="删除" @click="handleDeleteUser(user)"><ui-icon name="delete"></ui-icon></button>
                        </div>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <div v-else-if="systemTab === 'logs'" class="content-card">
                <div class="section-head">
                  <div>
                    <h2 class="section-title">操作日志</h2>
                    <p class="section-desc">记录系统关键操作，便于审计和追溯</p>
                  </div>
                  <div class="head-actions">
                    <div class="range-dropdown" @click.stop>
                      <button type="button" class="range-trigger" :class="{ open: logRangeMenuOpen }" @click="logRangeMenuOpen = !logRangeMenuOpen">
                        <span>{{ logRangeLabel }}</span><span class="caret">▾</span>
                      </button>
                      <div v-if="logRangeMenuOpen" class="range-menu">
                        <button v-for="opt in logRangeOptions" :key="opt.value" type="button" class="range-option" :class="{ active: logRange === opt.value }" @click="selectLogRange(opt.value)">
                          <span>{{ opt.label }}</span><span v-if="logRange === opt.value">✓</span>
                        </button>
                      </div>
                    </div>
                    <button type="button" class="btn-outline" @click="showToast('演示环境：日志已导出')"><ui-icon name="download"></ui-icon> 导出日志</button>
                  </div>
                </div>
                <div class="table-wrap">
                  <table class="data-table">
                    <thead>
                    <tr><th>操作人</th><th>操作内容</th><th>操作时间</th><th>IP地址</th><th>结果</th></tr>
                    </thead>
                    <tbody>
                    <tr v-for="log in operationLogs" :key="log.id">
                      <td class="cell-name">{{ log.operator }}</td>
                      <td>{{ log.action }}</td>
                      <td class="cell-muted">{{ log.time }}</td>
                      <td class="cell-muted">{{ log.ip }}</td>
                      <td><span class="result-tag" :class="log.result">{{ log.resultLabel }}</span></td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <div v-else class="settings-panel">
                <div class="config-grid">
                  <section class="config-card">
                    <div class="config-card-head">
                      <h3 class="config-card-title">数据管理配置</h3>
                      <p class="config-card-desc">配置数据存储、备份与上传策略</p>
                    </div>
                    <div class="form-field">
                      <label class="field-label">数据存储周期（天）</label>
                      <p class="field-hint">超过此期限的数据将自动归档</p>
                      <input v-model.number="systemSettings.dataRetentionDays" type="number" class="number-input" />
                    </div>
                    <div class="form-field">
                      <label class="field-label">素材上传限制（MB）</label>
                      <p class="field-hint">单次上传文件大小上限</p>
                      <input v-model.number="systemSettings.uploadLimitMb" type="number" class="number-input" />
                    </div>
                    <div class="form-field switch-field">
                      <div>
                        <label class="field-label">自动备份</label>
                        <p class="field-hint">每日凌晨自动备份系统数据</p>
                      </div>
                      <button type="button" class="switch-toggle" :class="{ on: systemSettings.autoBackup }" @click="systemSettings.autoBackup = !systemSettings.autoBackup"></button>
                    </div>
                  </section>
                  <section class="config-card">
                    <div class="config-card-head">
                      <h3 class="config-card-title">通知与提醒配置</h3>
                      <p class="config-card-desc">配置系统消息推送与告警方式</p>
                    </div>
                    <div class="form-field">
                      <label class="field-label">消息提醒方式</label>
                      <div class="notify-dropdown" @click.stop>
                        <button type="button" class="notify-trigger" :class="{ open: notifyMenuOpen }" @click="notifyMenuOpen = !notifyMenuOpen">
                          <span>{{ notifyMethodLabel }}</span><span class="caret">▾</span>
                        </button>
                        <div v-if="notifyMenuOpen" class="notify-menu">
                          <button v-for="opt in notifyMethodOptions" :key="opt.value" type="button" class="notify-option" :class="{ active: systemSettings.notifyMethod === opt.value }" @click="selectNotifyMethod(opt.value)">
                            <span>{{ opt.label }}</span><span v-if="systemSettings.notifyMethod === opt.value">✓</span>
                          </button>
                        </div>
                      </div>
                    </div>
                    <div class="form-field switch-field">
                      <label class="field-label">新任务通知</label>
                      <button type="button" class="switch-toggle" :class="{ on: systemSettings.notifyNewTask }" @click="systemSettings.notifyNewTask = !systemSettings.notifyNewTask"></button>
                    </div>
                    <div class="form-field switch-field">
                      <label class="field-label">诊断完成提醒</label>
                      <button type="button" class="switch-toggle" :class="{ on: systemSettings.notifyDiagnosisDone }" @click="systemSettings.notifyDiagnosisDone = !systemSettings.notifyDiagnosisDone"></button>
                    </div>
                    <div class="form-field switch-field">
                      <label class="field-label">异常数据告警</label>
                      <button type="button" class="switch-toggle" :class="{ on: systemSettings.notifyAbnormalData }" @click="systemSettings.notifyAbnormalData = !systemSettings.notifyAbnormalData"></button>
                    </div>
                  </section>
                  <section class="config-card">
                    <div class="config-card-head">
                      <h3 class="config-card-title">设备对接参数</h3>
                      <p class="config-card-desc">配置外部设备接口参数</p>
                    </div>
                    <div class="form-field">
                      <label class="field-label">API 接口地址</label>
                      <input v-model="systemSettings.deviceApiUrl" type="text" class="number-input" placeholder="https://api.aquaculture.com/v1" />
                    </div>
                    <div class="form-field">
                      <label class="field-label">API 密钥</label>
                      <input v-model="systemSettings.deviceApiKey" type="password" class="number-input" />
                    </div>
                    <div class="form-field">
                      <label class="field-label">数据同步间隔（秒）</label>
                      <input v-model.number="systemSettings.syncIntervalSeconds" type="number" class="number-input" />
                    </div>
                  </section>
                  <section class="config-card">
                    <div class="config-card-head">
                      <h3 class="config-card-title">系统信息</h3>
                      <p class="config-card-desc">当前系统运行状态</p>
                    </div>
                    <div class="info-list">
                      <div class="info-row"><span class="info-label">系统版本</span><span class="info-value">{{ systemSettings.version }}</span></div>
                      <div class="info-row"><span class="info-label">数据库大小</span><span class="info-value">{{ systemSettings.databaseSize }}</span></div>
                      <div class="info-row"><span class="info-label">活跃用户数</span><span class="info-value">{{ systemSettings.activeUsers }}</span></div>
                      <div class="info-row"><span class="info-label">案例总数</span><span class="info-value">{{ systemSettings.totalCases }}</span></div>
                      <div class="info-row"><span class="info-label">最后备份时间</span><span class="info-value">{{ systemSettings.lastBackupTime }}</span></div>
                    </div>
                  </section>
                </div>
                <div class="settings-footer">
                  <button type="button" class="btn-save-config" @click="saveSystemSettings">保存配置</button>
                </div>
              </div>
            </div>
          </section>
        </main>
      </div>

      <!-- 弹窗 -->
      <div v-if="archiveDetailVisible" class="modal-mask" @click.self="archiveDetailVisible = false">
        <div class="modal-box wide">
          <div class="modal-head">
            <div>
              <div class="modal-title">案例详情 - {{ archiveDetail.archiveNo }}</div>
              <p style="font-size:14px;color:#86909c;margin-top:6px">完整的诊断数据和治疗方案</p>
            </div>
            <button type="button" class="modal-close" @click="archiveDetailVisible = false">×</button>
          </div>
          <div class="modal-body">
            <div class="detail-section">
              <div class="detail-section-title">基本信息</div>
              <div class="detail-info-grid">
                <div class="detail-info-col">
                  <div class="detail-info-row"><span class="detail-info-label">养殖池：</span><span class="detail-info-value">{{ archiveDetail.pondCode }}</span></div>
                  <div class="detail-info-row"><span class="detail-info-label">病害类型：</span><span class="detail-info-value">{{ archiveDetail.diseaseName }}</span></div>
                  <div class="detail-info-row"><span class="detail-info-label">诊断专家：</span><span class="detail-info-value">{{ archiveDetail.expert }}</span></div>
                </div>
                <div class="detail-info-col">
                  <div class="detail-info-row"><span class="detail-info-label">鱼品种：</span><span class="detail-info-value">{{ archiveDetail.species }}</span></div>
                  <div class="detail-info-row"><span class="detail-info-label">诊断日期：</span><span class="detail-info-value">{{ archiveDetail.archiveDate }}</span></div>
                  <div class="detail-info-row"><span class="detail-info-label">恢复情况：</span><span class="recovery-tag" :class="archiveDetail.recoveryStatus">{{ archiveDetail.recoveryLabel }}</span></div>
                </div>
              </div>
            </div>
            <div v-if="archiveDetail.tags && archiveDetail.tags.length" class="detail-section">
              <div class="detail-section-title">案例标签</div>
              <div class="detail-tag-list">
                <span v-for="tag in archiveDetail.tags" :key="tag" class="case-tag"><ui-icon name="tag"></ui-icon> {{ tag }}</span>
              </div>
            </div>
            <div class="detail-section">
              <div class="detail-section-title">诊断结论</div>
              <div class="diagnosis-box">
                <p class="diagnosis-line"><span class="diagnosis-label">确诊依据：</span>{{ archiveDetail.diagnosisBasis || archiveDetail.summary }}</p>
                <p class="diagnosis-line"><span class="diagnosis-label">病因分析：</span>{{ archiveDetail.causeAnalysis || '—' }}</p>
              </div>
            </div>
            <div class="detail-section">
              <div class="detail-section-title">治疗方案</div>
              <div v-for="step in archiveDetail.treatmentSteps" :key="step.title" class="treatment-card">
                <div class="treatment-card-title">{{ step.title }}</div>
                <div class="treatment-card-desc">{{ step.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="conclusionVisible" class="modal-mask" @click.self="conclusionVisible = false">
        <div class="modal-box">
          <div class="modal-head"><div class="modal-title">填写诊断结论</div><button type="button" class="modal-close" @click="conclusionVisible = false">×</button></div>
          <div class="modal-body">
            <div class="form-group"><label class="form-label">诊断结论 <span class="req">*</span></label><input v-model="conclusionForm.suspectedDisease" class="form-control" placeholder="如：细菌性烂鳃" /></div>
            <div class="form-group"><label class="form-label">治疗方案</label><textarea v-model="conclusionForm.treatment" class="form-control" placeholder="填写具体治疗方案"></textarea></div>
            <div class="form-group"><label class="form-label">备注</label><textarea v-model="conclusionForm.conclusion" class="form-control" placeholder="补充说明"></textarea></div>
          </div>
          <div class="modal-foot"><button type="button" class="btn-cancel" @click="conclusionVisible = false">取消</button><button type="button" class="btn-dark" @click="saveConclusion">保存并完成</button></div>
        </div>
      </div>

      <div v-if="configVisible" class="modal-mask" @click.self="configVisible = false">
        <div class="modal-box">
          <div class="modal-head"><div class="modal-title">{{ configDialogTitle }}</div><button type="button" class="modal-close" @click="configVisible = false">×</button></div>
          <div class="modal-body">
            <template v-if="configTab === 'breeding'">
              <div class="form-group"><label class="form-label">品种名称</label><input v-model="configForm.name" class="form-control" placeholder="如 鲈鱼" /></div>
              <div class="form-group"><label class="form-label">水体类型</label><select v-model="configForm.waterType" class="form-control"><option value="marine">海水鱼</option><option value="freshwater">淡水鱼</option><option value="brackish">咸淡水鱼</option></select></div>
            </template>
            <template v-else-if="configTab === 'disease'">
              <div class="form-group"><label class="form-label">病害名称</label><input v-model="configForm.name" class="form-control" placeholder="如 白点病" /></div>
              <div class="form-group"><label class="form-label">分类</label><select v-model="configForm.category" class="form-control"><option v-for="cat in diseaseCategories" :key="cat" :value="cat">{{ cat }}</option></select></div>
            </template>
            <template v-else>
              <div class="form-group"><label class="form-label">设备编号</label><input v-model="configForm.code" class="form-control" placeholder="如 DO-001" /></div>
              <div class="form-group"><label class="form-label">设备类型</label><select v-model="configForm.type" class="form-control"><option value="">选择设备类型</option><option v-for="t in equipmentTypes" :key="t" :value="t">{{ t }}</option></select></div>
              <div class="form-group"><label class="form-label">采集频率</label><select v-model="configForm.frequency" class="form-control"><option value="">选择采集频率</option><option v-for="f in frequencyOptions" :key="f" :value="f">{{ f }}</option></select></div>
            </template>
          </div>
          <div class="modal-foot"><button type="button" class="btn-cancel" @click="configVisible = false">取消</button><button type="button" class="btn-dark" @click="saveConfig">保存</button></div>
        </div>
      </div>

      <div v-if="userVisible" class="modal-mask" @click.self="userVisible = false">
        <div class="modal-box">
          <div class="modal-head">
            <div class="user-dialog-title">
              <div class="dialog-main-title">{{ userForm.id ? '编辑用户' : '添加新用户' }}</div>
              <div class="dialog-sub-title">填写用户信息并分配角色权限</div>
            </div>
            <button type="button" class="modal-close" @click="userVisible = false">×</button>
          </div>
          <div class="modal-body">
            <div class="form-group"><label class="form-label">用户名 <span class="required-star">*</span></label><input v-model="userForm.username" class="form-control" placeholder="请输入用户名" /></div>
            <div class="form-group"><label class="form-label">{{ userForm.id ? '密码' : '密码 *' }}</label><input v-model="userForm.password" type="password" class="form-control" :placeholder="userForm.id ? '不修改请留空' : '请输入密码'" /></div>
            <div class="form-group"><label class="form-label">角色 <span class="required-star">*</span></label><select v-model="userForm.role" class="form-control"><option value="">选择角色</option><option value="expert">专家</option><option value="worker">工人</option><option value="admin">管理员</option></select></div>
            <div class="form-group"><label class="form-label">所属部门</label><input v-model="userForm.department" class="form-control" placeholder="例如：养殖一组" /></div>
          </div>
          <div class="modal-foot"><button type="button" class="btn-cancel" @click="userVisible = false">取消</button><button type="button" class="btn-dark" @click="saveUser">{{ savingUser ? '保存中…' : '保存' }}</button></div>
        </div>
      </div>

    </div>

    <div v-if="toast" class="toast">{{ toast }}</div>
  </div>
</template>

<style scoped lang="scss">

</style>
