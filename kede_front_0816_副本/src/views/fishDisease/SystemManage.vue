<template>
  <div class="system-page">
    <header class="system-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">系统管理</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">保障系统安全稳定运行，管理用户权限和系统配置</p>
    </header>

    <div class="system-body">
      <div class="system-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          type="button"
          class="system-tab"
          :class="{ active: activeTab === tab.key }"
          @click="switchTab(tab.key)"
        >{{ tab.label }}</button>
      </div>

      <!-- 用户管理 -->
      <div v-if="activeTab === 'users'" class="content-card">
        <div class="section-head">
          <div>
            <h2 class="section-title">用户与权限管理</h2>
            <p class="section-desc">对接平台系统用户数据；账号维护请在系统用户管理中操作</p>
          </div>
          <div class="head-actions">
            <button type="button" class="btn-outline" @click="openPlatformUsers">
              <i class="el-icon-link" /> 打开系统用户管理
            </button>
            <button type="button" class="btn-add" @click="loadTabData">
              <i class="el-icon-refresh" /> 刷新
            </button>
          </div>
        </div>
        <p v-if="dataSourceTip" class="data-source-tip">{{ dataSourceTip }}</p>

        <div v-loading="loading" class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>用户名</th>
                <th>角色</th>
                <th>部门</th>
                <th>状态</th>
                <th class="col-actions">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td class="cell-name">{{ user.username }}</td>
                <td>
                  <span class="role-tag" :class="user.role">{{ user.roleLabel }}</span>
                </td>
                <td class="cell-muted">{{ user.department }}</td>
                <td>
                  <span class="status-tag">{{ user.statusLabel }}</span>
                </td>
                <td class="col-actions">
                  <button type="button" class="icon-btn" title="查看" @click="viewUser(user)">
                    <i class="el-icon-view" />
                  </button>
                  <button type="button" class="icon-btn" title="在系统中管理" @click="openPlatformUsers">
                    <i class="el-icon-setting" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 操作日志 -->
      <div v-else-if="activeTab === 'logs'" class="content-card">
        <div class="section-head">
          <div>
            <h2 class="section-title">操作日志</h2>
            <p class="section-desc">对接平台系统操作日志，便于审计和追溯</p>
          </div>
          <div class="head-actions">
            <button type="button" class="btn-outline" @click="openPlatformLogs">
              <i class="el-icon-link" /> 打开系统操作日志
            </button>
            <div class="range-dropdown" @click.stop>
              <button
                type="button"
                class="range-trigger"
                :class="{ open: rangeMenuOpen }"
                @click="rangeMenuOpen = !rangeMenuOpen"
              >
                <span>{{ logRangeLabel }}</span>
                <i class="el-icon-arrow-down caret" />
              </button>
              <div v-if="rangeMenuOpen" class="range-menu">
                <button
                  v-for="opt in logRangeOptions"
                  :key="opt.value"
                  type="button"
                  class="range-option"
                  :class="{ active: logRange === opt.value }"
                  @click="selectLogRange(opt.value)"
                >
                  <span>{{ opt.label }}</span>
                  <i v-if="logRange === opt.value" class="el-icon-check" />
                </button>
              </div>
            </div>
            <el-dropdown trigger="click" @command="exportLogs">
              <button type="button" class="btn-outline">
                <i class="el-icon-download" /> 导出日志 <i class="el-icon-arrow-down" />
              </button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="f in exportFormats" :key="f.value" :command="f.value">{{ f.label }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>

        <div v-loading="loading" class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>操作人</th>
                <th>操作内容</th>
                <th>操作时间</th>
                <th>IP地址</th>
                <th>结果</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in logs" :key="log.id">
                <td class="cell-name">{{ log.operator }}</td>
                <td>{{ log.action }}</td>
                <td class="cell-muted">{{ log.time }}</td>
                <td class="cell-muted">{{ log.ip }}</td>
                <td>
                  <span class="result-tag" :class="log.result">{{ log.resultLabel }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 系统配置 -->
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
              <el-input-number v-model="settings.dataRetentionDays" :min="30" :max="3650" controls-position="right" />
            </div>
            <div class="form-field">
              <label class="field-label">素材上传限制（MB）</label>
              <p class="field-hint">单次上传文件大小上限</p>
              <el-input-number v-model="settings.uploadLimitMb" :min="1" :max="500" controls-position="right" />
            </div>
            <div class="form-field switch-field">
              <div>
                <label class="field-label">自动备份</label>
                <p class="field-hint">每日凌晨自动备份系统数据</p>
              </div>
              <el-switch v-model="settings.autoBackup" active-color="#1d2129" />
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
                <button
                  type="button"
                  class="notify-trigger"
                  :class="{ open: notifyMenuOpen }"
                  @click="notifyMenuOpen = !notifyMenuOpen"
                >
                  <span>{{ notifyMethodLabel }}</span>
                  <i class="el-icon-arrow-down caret" />
                </button>
                <div v-if="notifyMenuOpen" class="notify-menu">
                  <button
                    v-for="opt in notifyMethodOptions"
                    :key="opt.value"
                    type="button"
                    class="notify-option"
                    :class="{ active: settings.notifyMethod === opt.value }"
                    @click="selectNotifyMethod(opt.value)"
                  >
                    <span>{{ opt.label }}</span>
                    <i v-if="settings.notifyMethod === opt.value" class="el-icon-check" />
                  </button>
                </div>
              </div>
            </div>
            <div class="form-field switch-field">
              <label class="field-label">新任务通知</label>
              <el-switch v-model="settings.notifyNewTask" active-color="#1d2129" />
            </div>
            <div class="form-field switch-field">
              <label class="field-label">诊断完成提醒</label>
              <el-switch v-model="settings.notifyDiagnosisDone" active-color="#1d2129" />
            </div>
            <div class="form-field switch-field">
              <label class="field-label">异常数据告警</label>
              <el-switch v-model="settings.notifyAbnormalData" active-color="#1d2129" />
            </div>
          </section>

          <section class="config-card">
            <div class="config-card-head">
              <h3 class="config-card-title">设备对接参数</h3>
              <p class="config-card-desc">配置外部设备接口参数</p>
            </div>
            <div class="form-field">
              <label class="field-label">API 接口地址</label>
              <el-input v-model="settings.deviceApiUrl" placeholder="https://api.aquaculture.com/v1" />
            </div>
            <div class="form-field">
              <label class="field-label">API 密钥</label>
              <el-input v-model="settings.deviceApiKey" type="password" show-password />
            </div>
            <div class="form-field">
              <label class="field-label">数据同步间隔（秒）</label>
              <el-input-number v-model="settings.syncIntervalSeconds" :min="10" :max="3600" controls-position="right" />
            </div>
          </section>

          <section class="config-card">
            <div class="config-card-head">
              <h3 class="config-card-title">系统信息</h3>
              <p class="config-card-desc">当前系统运行状态</p>
            </div>
            <div class="info-list">
              <div class="info-row">
                <span class="info-label">系统版本</span>
                <span class="info-value">{{ settings.version }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">数据库大小</span>
                <span class="info-value">{{ settings.databaseSize }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">活跃用户数</span>
                <span class="info-value">{{ settings.activeUsers }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">案例总数</span>
                <span class="info-value">{{ settings.totalCases }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">最后备份时间</span>
                <span class="info-value">{{ settings.lastBackupTime }}</span>
              </div>
            </div>
          </section>
        </div>

        <div class="settings-footer">
          <button type="button" class="btn-save-config" :disabled="savingSettings" @click="saveSettings">
            <i class="el-icon-setting" /> {{ savingSettings ? '保存中…' : '保存配置' }}
          </button>
        </div>
      </div>
    </div>


    <el-dialog
      :visible.sync="userVisible"
      width="440px"
      custom-class="disease-dialog user-dialog"
      :show-close="true"
    >
      <div slot="title" class="user-dialog-title">
        <div class="dialog-main-title">{{ userForm.id ? '编辑用户' : '添加新用户' }}</div>
        <div class="dialog-sub-title">填写用户信息并分配角色权限</div>
      </div>
      <el-form
        ref="userRef"
        :model="userForm"
        :rules="userRules"
        label-position="top"
        hide-required-asterisk
        class="user-form"
        @submit.native.prevent
      >
        <el-form-item prop="username">
          <template slot="label">用户名 <span class="required-star">*</span></template>
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!userForm.id" prop="password">
          <template slot="label">密码 <span class="required-star">*</span></template>
          <el-input v-model="userForm.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item v-else prop="password">
          <template slot="label">密码</template>
          <el-input v-model="userForm.password" type="password" show-password placeholder="不修改请留空" />
        </el-form-item>
        <el-form-item prop="role">
          <template slot="label">角色 <span class="required-star">*</span></template>
          <el-select
            v-model="userForm.role"
            placeholder="选择角色"
            style="width:100%"
            popper-class="config-select-dropdown"
          >
            <el-option label="专家" value="expert" />
            <el-option label="工人" value="worker" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="userForm.department" placeholder="例如：养殖一组" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="btn-cancel" @click="userVisible = false">取消</el-button>
        <el-button class="btn-dark btn-save" :loading="saving" @click="saveUser">
          <i class="el-icon-user-solid" />
          {{ userForm.id ? '保存' : '添加用户' }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchSystemUsers,
  fetchOperationLogs,
  fetchSystemSettings,
  updateSystemSettings
} from '@/api/fishDisease'
import { listUser } from '@/api/system/user'
import { listOperateLog } from '@/api/system/operatelog'

import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'
import { EXPORT_FORMAT_OPTIONS, exportRecords } from '@/views/fishDisease/utils/exportFormats'

export default {
  name: 'SystemManage',
  components: { MenuToggle, HeaderAuth },
  data() {
    return {
      loading: false,
      saving: false,
      savingSettings: false,
      activeTab: 'users',
      logRange: 'today',
      rangeMenuOpen: false,
      notifyMenuOpen: false,
      exportFormats: EXPORT_FORMAT_OPTIONS,
      logRangeOptions: [
        { label: '今天', value: 'today' },
        { label: '最近7天', value: 'week' },
        { label: '最近30天', value: 'month' },
        { label: '全部', value: 'all' }
      ],
      notifyMethodOptions: [
        { label: '邮件', value: 'email' },
        { label: '短信', value: 'sms' },
        { label: '微信', value: 'wechat' },
        { label: '全部', value: 'all' }
      ],
      tabs: [
        { key: 'users', label: '用户管理' },
        { key: 'logs', label: '操作日志' },
        { key: 'settings', label: '系统配置' }
      ],
      users: [],
      logs: [],
      dataSourceTip: '',
      settings: {
        dataRetentionDays: 365,
        uploadLimitMb: 50,
        autoBackup: true,
        notifyMethod: 'email',
        notifyNewTask: true,
        notifyDiagnosisDone: true,
        notifyAbnormalData: true,
        deviceApiUrl: '',
        deviceApiKey: '',
        syncIntervalSeconds: 60,
        version: 'v1.0.0',
        databaseSize: '2.3 GB',
        activeUsers: 12,
        totalCases: 156,
        lastBackupTime: '2024-12-22 02:00'
      },
      userVisible: false,
      userForm: this.emptyUserForm(),
      userRules: {
        username: [{ required: true, message: '请填写用户名', trigger: 'blur' }],
        password: [
          {
            validator: (rule, value, callback) => {
              if (!this.userForm.id && !value) {
                callback(new Error('请填写密码'))
              } else if (value && value.length < 6) {
                callback(new Error('密码至少 6 位'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    }
  },
  computed: {
    todayText() {
      const now = new Date()
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日${weekDays[now.getDay()]}`
    },
    logRangeLabel() {
      const found = this.logRangeOptions.find(o => o.value === this.logRange)
      return found ? found.label : '今天'
    },
    notifyMethodLabel() {
      const found = this.notifyMethodOptions.find(o => o.value === this.settings.notifyMethod)
      return found ? found.label : '邮件'
    }
  },
  created() {
    this.init()
  },
  mounted() {
    document.addEventListener('click', this.closeDropdownMenus)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.closeDropdownMenus)
  },
  methods: {
    closeDropdownMenus() {
      this.rangeMenuOpen = false
      this.notifyMenuOpen = false
    },
    selectLogRange(value) {
      this.logRange = value
      this.rangeMenuOpen = false
      this.loadLogs()
    },
    selectNotifyMethod(value) {
      this.settings.notifyMethod = value
      this.notifyMenuOpen = false
    },
    emptyUserForm() {
      return { id: '', username: '', password: '', role: '', department: '', email: '', status: 'active' }
    },
    async init() {
      await this.loadTabData()
    },
    switchTab(tab) {
      this.activeTab = tab
      this.loadTabData()
    },
    mapPlatformRole(user) {
      const roles = user.roleNames || user.roles || []
      const text = Array.isArray(roles)
        ? roles.map(r => (typeof r === 'string' ? r : (r.name || r.code || ''))).join(',')
        : String(roles || '')
      if (/admin|管理/i.test(text)) return { role: 'admin', roleLabel: '管理员' }
      if (/expert|专家|兽医/i.test(text)) return { role: 'expert', roleLabel: '专家' }
      if (/worker|工人|技工|技术/i.test(text)) return { role: 'worker', roleLabel: '工人' }
      return { role: 'worker', roleLabel: text || '系统用户' }
    },
    mapPlatformUser(u) {
      const mapped = this.mapPlatformRole(u)
      const active = u.status === 0 || u.status === '0' || u.status === 'active'
      return {
        id: u.id,
        username: u.nickname || u.username || String(u.id),
        role: mapped.role,
        roleLabel: mapped.roleLabel,
        department: u.deptName || (u.dept && u.dept.name) || '',
        email: u.email || u.mobile || '',
        status: active ? 'active' : 'disabled',
        statusLabel: active ? '正常' : '停用',
        _platform: true
      }
    },
    mapPlatformLog(row) {
      return {
        id: row.id,
        operator: row.userNickname || row.userName || '',
        action: [row.type, row.subType, row.action].filter(Boolean).join(' / '),
        module: row.type || '',
        content: row.action || '',
        time: row.createTime || '',
        createdAt: row.createTime || '',
        ip: row.userIp || '',
        result: 'success',
        resultLabel: '成功'
      }
    },
    logRangeToTime() {
      const end = new Date()
      const start = new Date()
      if (this.logRange === 'today') {
        start.setHours(0, 0, 0, 0)
      } else if (this.logRange === 'week') {
        start.setDate(start.getDate() - 7)
      } else if (this.logRange === 'month') {
        start.setDate(start.getDate() - 30)
      } else {
        return []
      }
      const fmt = d => {
        const p = n => String(n).padStart(2, '0')
        return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
      }
      return [fmt(start), fmt(end)]
    },
    async loadTabData() {
      this.loading = true
      this.dataSourceTip = ''
      try {
        if (this.activeTab === 'users') {
          await this.loadPlatformUsers()
        } else if (this.activeTab === 'logs') {
          await this.loadLogs()
        } else {
          const res = await fetchSystemSettings()
          this.settings = { ...this.settings, ...res.data }
        }
      } catch (e) {
        if (this.activeTab === 'users') this.users = []
        else if (this.activeTab === 'logs') this.logs = []
        this.$message.error((e && e.message) || '加载失败')
      } finally {
        this.loading = false
      }
    },
    async loadPlatformUsers() {
      try {
        const res = await listUser({ pageNo: 1, pageSize: 100 })
        const list = (res && res.data && res.data.list) || (res && res.data) || []
        if (Array.isArray(list) && list.length) {
          this.users = list.map(u => this.mapPlatformUser(u))
          this.dataSourceTip = '数据来源：平台系统用户（/system/user/page）'
          return
        }
      } catch (e) {
        console.warn('[system-manage] platform users failed', e)
      }
      // 回退鱼病 BFF，并提示
      try {
        const res = await fetchSystemUsers()
        this.users = res.data || []
        this.dataSourceTip = '平台用户接口不可用，已回退鱼病模块用户数据'
      } catch (e2) {
        this.users = []
        this.dataSourceTip = '用户数据加载失败，请确认已登录平台且后端可用'
        throw e2
      }
    },
    async loadLogs() {
      try {
        const createTime = this.logRangeToTime()
        const res = await listOperateLog({
          pageNo: 1,
          pageSize: 100,
          createTime: createTime.length ? createTime : undefined
        })
        const list = (res && res.data && res.data.list) || []
        if (Array.isArray(list)) {
          this.logs = list.map(row => this.mapPlatformLog(row))
          this.dataSourceTip = '数据来源：平台操作日志（/system/operate-log/page）'
          return
        }
      } catch (e) {
        console.warn('[system-manage] platform logs failed', e)
      }
      const res = await fetchOperationLogs({ range: this.logRange })
      this.logs = res.data || []
      this.dataSourceTip = '平台操作日志不可用，已回退鱼病模块日志'
    },
    openPlatformUsers() {
      this.$router.push('/system/user').catch(() => {
        this.$message.info('请从左侧菜单进入「系统管理 → 用户管理」')
      })
    },
    openPlatformLogs() {
      this.$router.push('/system/operate-log').catch(() => {
        this.$router.push('/system/operatelog').catch(() => {
          this.$message.info('请从左侧菜单进入「系统管理 → 操作日志」')
        })
      })
    },
    viewUser(user) {
      this.$alert(
        `用户名：${user.username}\n角色：${user.roleLabel}\n部门：${user.department || '—'}\n邮箱：${user.email || '—'}`,
        '用户详情',
        { confirmButtonText: '关闭' }
      )
    },
    saveUser() {
      this.$message.info('请在平台「系统用户管理」中维护账号')
      this.userVisible = false
      this.openPlatformUsers()
    },
    async saveSettings() {
      this.savingSettings = true
      try {
        const res = await updateSystemSettings(this.settings)
        this.settings = { ...this.settings, ...res.data }
        this.$message.success('配置已保存')
      } finally {
        this.savingSettings = false
      }
    },
    exportLogs(format) {
      const rows = (this.logs || []).map(l => ({
        操作人: l.operator || l.userName || '',
        操作内容: l.content || l.action || '',
        模块: l.module || '',
        时间: l.createdAt || l.time || ''
      }))
      exportRecords(`操作日志_${Date.now()}`, rows, format || 'csv', '操作日志')
      this.$message.success(`已导出 ${String(format || 'csv').toUpperCase()} 格式`)
    }
  }
}
</script>

<style scoped>
.system-page {
  min-height: 100vh;
  background: #0a1520;
  position: relative;
}

.data-source-tip {
  margin: 0 0 12px;
  padding: 8px 12px;
  border-radius: 8px;
  background: rgba(56, 189, 248, 0.08);
  border: 1px solid rgba(56, 189, 248, 0.22);
  color: #8ecae6;
  font-size: 12px;
}

.system-header {
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

.system-body {
  padding: 0 40px 48px;
}

.system-tabs {
  display: flex;
  width: 100%;
  background: rgba(124, 143, 161, 0.25);
  border-radius: 999px;
  padding: 4px;
  margin-bottom: 16px;
}

.system-tab {
  flex: 1;
  border: none;
  background: transparent;
  border-radius: 999px;
  padding: 11px 20px;
  font-size: 14px;
  color: #7c8fa1;
  cursor: pointer;
  transition: all 0.15s;
}

.system-tab.active {
  background: #0f1a26;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  color: #e8edf2;
  font-weight: 500;
}

.content-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 12px;
  padding: 28px 32px;
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
}

.btn-add {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  background: #409eff;
  color: #fff;
  font-size: 14px;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  font-weight: 500;
}

.btn-add:hover {
  background: #66b1ff;
}

.head-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.range-dropdown {
  position: relative;
}

.range-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 36px;
  padding: 0 14px;
  border: none;
  border-radius: 10px;
  background: rgba(64, 158, 255, 0.1);
  color: #e8edf2;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}

.range-trigger:hover,
.range-trigger.open {
  background: rgba(124, 143, 161, 0.22);
}

.range-trigger .caret {
  font-size: 12px;
  color: #5a6b7d;
  transition: transform 0.2s;
}

.range-trigger.open .caret {
  transform: rotate(180deg);
}

.range-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 140px;
  background: #0f1a26;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.45);
  padding: 6px;
  z-index: 30;
}

.range-option {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: none;
  background: transparent;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #e8edf2;
  cursor: pointer;
  text-align: left;
}

.range-option:hover,
.range-option.active {
  background: rgba(64, 158, 255, 0.1);
}

.range-option .el-icon-check {
  color: #e8edf2;
  font-weight: 700;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  border: 1px solid rgba(124, 143, 161, 0.25);
  border-radius: 10px;
  background: #0f1a26;
  color: #e8edf2;
  font-size: 14px;
  cursor: pointer;
}

.btn-outline:hover {
  background: #0a1520;
}

.table-wrap {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th {
  text-align: left;
  padding: 12px 16px;
  font-weight: 500;
  color: #7c8fa1;
  border-bottom: 1px solid rgba(124, 143, 161, 0.22);
  background: #132030;
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.22);
  color: #e8edf2;
}

.data-table tbody tr:last-child td {
  border-bottom: none;
}

.data-table tbody tr:hover td {
  background: #132030;
}

.cell-name {
  font-weight: 500;
}

.cell-muted {
  color: #7c8fa1;
}

.col-actions {
  width: 140px;
  text-align: right;
  white-space: nowrap;
}

.role-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

.role-tag.expert {
  background: rgba(64, 158, 255, 0.18);
  color: #409eff;
}

.role-tag.worker {
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
}

.role-tag.admin {
  background: rgba(162, 112, 255, 0.18);
  color: #722ed1;
}

.status-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
  border: 1px solid rgba(82, 196, 26, 0.35);
}

.result-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

.result-tag.success {
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
  border: 1px solid rgba(82, 196, 26, 0.35);
}

.result-tag.failed {
  background: rgba(245, 63, 63, 0.15);
  color: #f53f3f;
  border: 1px solid rgba(245, 63, 63, 0.35);
}

.icon-btn {
  width: 32px;
  height: 32px;
  border: 1px solid rgba(124, 143, 161, 0.25);
  background: #0f1a26;
  border-radius: 8px;
  color: #e8edf2;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  margin-left: 6px;
}

.icon-btn:first-child {
  margin-left: 0;
}

.icon-btn:hover {
  background: #0a1520;
}

.icon-btn.danger {
  color: #f53f3f;
}

.icon-btn.danger:hover {
  background: rgba(245, 63, 63, 0.15);
}

.config-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.settings-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.settings-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  padding-top: 4px;
}

.btn-save-config {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: none;
  background: #409eff;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
}

.btn-save-config:hover:not(:disabled) {
  background: #66b1ff;
}

.btn-save-config:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.config-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 12px;
  padding: 24px 28px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.config-card-head {
  margin-bottom: 20px;
}

.config-card-title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  color: #e8edf2;
}

.config-card-desc {
  margin: 0;
  font-size: 13px;
  color: #7c8fa1;
  line-height: 1.4;
}

.form-field {
  margin-bottom: 20px;
}

.form-field:last-child {
  margin-bottom: 0;
}

.notify-dropdown {
  position: relative;
  width: 100%;
}

.notify-trigger {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 40px;
  padding: 0 14px;
  border: none;
  border-radius: 8px;
  background: rgba(64, 158, 255, 0.1);
  color: #e8edf2;
  font-size: 14px;
  cursor: pointer;
  text-align: left;
}

.notify-trigger:hover,
.notify-trigger.open {
  background: rgba(124, 143, 161, 0.22);
}

.notify-trigger .caret {
  font-size: 12px;
  color: #5a6b7d;
  transition: transform 0.2s;
}

.notify-trigger.open .caret {
  transform: rotate(180deg);
}

.notify-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: #0f1a26;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.45);
  padding: 6px;
  z-index: 30;
}

.notify-option {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: none;
  background: transparent;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #e8edf2;
  cursor: pointer;
  text-align: left;
}

.notify-option:hover,
.notify-option.active {
  background: rgba(64, 158, 255, 0.1);
}

.notify-option .el-icon-check {
  color: #e8edf2;
  font-weight: 700;
}

.switch-field {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.field-label {
  display: block;
  font-size: 14px;
  color: #e8edf2;
  margin-bottom: 4px;
}

.field-hint {
  margin: 0 0 8px;
  font-size: 12px;
  color: #7c8fa1;
  line-height: 1.4;
}

.switch-field .field-label {
  margin-bottom: 2px;
}

.switch-field .field-hint {
  margin-bottom: 0;
}

.config-card >>> .el-input__inner {
  background: rgba(64, 158, 255, 0.1);
  border: none;
  border-radius: 8px;
  height: 40px;
}

.config-card >>> .el-input__inner:focus {
  background: #0f1a26;
  border: 1px solid #5a6b7d;
}

.config-card >>> .el-input-number {
  width: 100%;
}

.config-card >>> .el-input-number .el-input__inner {
  text-align: left;
  padding-left: 14px;
}

.config-card >>> .el-select .el-input__inner {
  background: rgba(64, 158, 255, 0.1);
  border: none;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.1);
}

.info-row:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #7c8fa1;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #e8edf2;
}

@media (max-width: 768px) {
  .system-header,
  .system-body {
    padding-left: 16px;
    padding-right: 16px;
  }

  .system-tabs {
    max-width: none;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .head-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>

<style>
.user-dialog .el-dialog__header {
  padding: 24px 24px 8px;
  border-bottom: none;
}

.user-dialog .el-dialog__body {
  padding: 8px 24px 8px;
}

.user-dialog-title .dialog-main-title {
  font-size: 20px;
  font-weight: 600;
  color: #e8edf2;
  line-height: 1.3;
}

.user-dialog-title .dialog-sub-title {
  margin-top: 6px;
  font-size: 13px;
  color: #7c8fa1;
  font-weight: 400;
}

.user-form .required-star {
  color: #f53f3f;
}

.user-form .el-form-item__label {
  padding-bottom: 8px;
  line-height: 1.4;
  color: #e8edf2;
  font-weight: 500;
}

.user-form .el-input__inner,
.user-form .el-select .el-input__inner {
  height: 44px;
  line-height: 44px;
  background: rgba(64, 158, 255, 0.1);
  border: none;
  border-radius: 10px;
  color: #e8edf2;
}

.user-form .el-input__inner:focus,
.user-form .el-select .el-input.is-focus .el-input__inner {
  background: rgba(124, 143, 161, 0.22);
  box-shadow: none;
}

.user-form .el-select .el-input .el-select__caret {
  color: #5a6b7d;
}

.user-dialog .btn-save i {
  margin-right: 4px;
}
</style>
