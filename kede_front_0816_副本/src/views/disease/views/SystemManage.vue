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
            <p class="section-desc">管理系统用户账号、角色和权限分配</p>
          </div>
          <button type="button" class="btn-add" @click="openUserDialog()">
            <i class="el-icon-user-solid" /> 添加用户
          </button>
        </div>

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
                  <button type="button" class="icon-btn" title="编辑" @click="openUserDialog(user)">
                    <i class="el-icon-edit-outline" />
                  </button>
                  <button type="button" class="icon-btn danger" title="删除" @click="handleDeleteUser(user)">
                    <i class="el-icon-delete" />
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
            <p class="section-desc">记录系统关键操作，便于审计和追溯</p>
          </div>
          <div class="head-actions">
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
            <button type="button" class="btn-outline" @click="exportLogs">
              <i class="el-icon-download" /> 导出日志
            </button>
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
  createSystemUser,
  updateSystemUser,
  deleteSystemUser,
  fetchOperationLogs,
  fetchSystemSettings,
  updateSystemSettings
} from '../api'

import MenuToggle from '../components/MenuToggle.vue'
import HeaderAuth from '../components/HeaderAuth.vue'

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
    async loadTabData() {
      this.loading = true
      try {
        if (this.activeTab === 'users') {
          const res = await fetchSystemUsers()
          this.users = res.data || []
        } else if (this.activeTab === 'logs') {
          await this.loadLogs()
        } else {
          const res = await fetchSystemSettings()
          this.settings = { ...this.settings, ...res.data }
        }
      } catch (e) {
        if (this.activeTab === 'users') this.users = []
        else if (this.activeTab === 'logs') this.logs = []
        this.$message.error('加载失败，请确认后端服务已启动（端口 3011）')
      } finally {
        this.loading = false
      }
    },
    async loadLogs() {
      const res = await fetchOperationLogs({ range: this.logRange })
      this.logs = res.data || []
    },
    openUserDialog(user) {
      if (user && user.id) {
        this.userForm = {
          id: user.id,
          username: user.username,
          password: '',
          role: user.role,
          department: user.department || '',
          email: user.email || '',
          status: user.status || 'active'
        }
      } else {
        this.userForm = this.emptyUserForm()
      }
      this.userVisible = true
      this.$nextTick(() => {
        if (this.$refs.userRef) this.$refs.userRef.clearValidate()
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
      this.$refs.userRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const payload = {
            username: this.userForm.username.trim(),
            role: this.userForm.role,
            department: this.userForm.department.trim(),
            email: this.userForm.email || `${this.userForm.username.trim()}@system.com`,
            status: this.userForm.status || 'active'
          }
          if (this.userForm.password) {
            payload.password = this.userForm.password
          }
          if (this.userForm.id) {
            await updateSystemUser(this.userForm.id, payload)
          } else {
            if (!payload.password) payload.password = '123456'
            await createSystemUser(payload)
          }
          this.$message.success(this.userForm.id ? '保存成功' : '用户已添加')
          this.userVisible = false
          const res = await fetchSystemUsers()
          this.users = res.data || []
        } finally {
          this.saving = false
        }
      })
    },
    handleDeleteUser(user) {
      this.$confirm(`确认删除用户「${user.username}」？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteSystemUser(user.id)
          this.$message.success('已删除')
          const res = await fetchSystemUsers()
          this.users = res.data || []
        })
        .catch(() => {})
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
    exportLogs() {
      const blob = new Blob([JSON.stringify(this.logs, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `操作日志_${Date.now()}.json`
      link.click()
      URL.revokeObjectURL(url)
      this.$message.success('日志已导出')
    }
  }
}
</script>

<style scoped>
.system-page {
  min-height: 100vh;
  background: #f7f8fa;
  position: relative;
}

.system-header {
  background: #fff;
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
  color: #1d2129;
  line-height: 1.25;
}

.header-date {
  font-size: 14px;
  color: #86909c;
  white-space: nowrap;
  padding-top: 6px;
}

.header-desc {
  margin: 10px 0 0;
  font-size: 14px;
  color: #86909c;
  line-height: 1.5;
}

.system-body {
  padding: 0 40px 48px;
}

.system-tabs {
  display: flex;
  width: 100%;
  background: #eceef2;
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
  color: #86909c;
  cursor: pointer;
  transition: all 0.15s;
}

.system-tab.active {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  color: #1d2129;
  font-weight: 500;
}

.content-card {
  background: #fff;
  border: 1px solid #eef0f3;
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
  color: #1d2129;
}

.section-desc {
  margin: 0;
  font-size: 14px;
  color: #86909c;
}

.btn-add {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  background: #1d2129;
  color: #fff;
  font-size: 14px;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  font-weight: 500;
}

.btn-add:hover {
  background: #4e5969;
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
  background: #f2f3f5;
  color: #1d2129;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}

.range-trigger:hover,
.range-trigger.open {
  background: #eef0f3;
}

.range-trigger .caret {
  font-size: 12px;
  color: #c9cdd4;
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
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
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
  color: #1d2129;
  cursor: pointer;
  text-align: left;
}

.range-option:hover,
.range-option.active {
  background: #f2f3f5;
}

.range-option .el-icon-check {
  color: #1d2129;
  font-weight: 700;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  border: 1px solid #e5e6eb;
  border-radius: 10px;
  background: #fff;
  color: #1d2129;
  font-size: 14px;
  cursor: pointer;
}

.btn-outline:hover {
  background: #f7f8fa;
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
  color: #86909c;
  border-bottom: 1px solid #eef0f3;
  background: #fafbfc;
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid #eef0f3;
  color: #1d2129;
}

.data-table tbody tr:last-child td {
  border-bottom: none;
}

.data-table tbody tr:hover td {
  background: #fafbfc;
}

.cell-name {
  font-weight: 500;
}

.cell-muted {
  color: #86909c;
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
  background: #e8f3ff;
  color: #1890ff;
}

.role-tag.worker {
  background: #f6ffed;
  color: #52c41a;
}

.role-tag.admin {
  background: #f9f0ff;
  color: #722ed1;
}

.status-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.result-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

.result-tag.success {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.result-tag.failed {
  background: #fff1f0;
  color: #f53f3f;
  border: 1px solid #ffccc7;
}

.icon-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #e5e6eb;
  background: #fff;
  border-radius: 8px;
  color: #1d2129;
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
  background: #f7f8fa;
}

.icon-btn.danger {
  color: #f53f3f;
}

.icon-btn.danger:hover {
  background: #fff1f0;
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
  background: #1d2129;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
}

.btn-save-config:hover:not(:disabled) {
  background: #4e5969;
}

.btn-save-config:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.config-card {
  background: #fff;
  border: 1px solid #eef0f3;
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
  color: #1d2129;
}

.config-card-desc {
  margin: 0;
  font-size: 13px;
  color: #86909c;
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
  background: #f2f3f5;
  color: #1d2129;
  font-size: 14px;
  cursor: pointer;
  text-align: left;
}

.notify-trigger:hover,
.notify-trigger.open {
  background: #eef0f3;
}

.notify-trigger .caret {
  font-size: 12px;
  color: #c9cdd4;
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
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
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
  color: #1d2129;
  cursor: pointer;
  text-align: left;
}

.notify-option:hover,
.notify-option.active {
  background: #f2f3f5;
}

.notify-option .el-icon-check {
  color: #1d2129;
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
  color: #1d2129;
  margin-bottom: 4px;
}

.field-hint {
  margin: 0 0 8px;
  font-size: 12px;
  color: #86909c;
  line-height: 1.4;
}

.switch-field .field-label {
  margin-bottom: 2px;
}

.switch-field .field-hint {
  margin-bottom: 0;
}

.config-card >>> .el-input__inner {
  background: #f2f3f5;
  border: none;
  border-radius: 8px;
  height: 40px;
}

.config-card >>> .el-input__inner:focus {
  background: #fff;
  border: 1px solid #c9cdd4;
}

.config-card >>> .el-input-number {
  width: 100%;
}

.config-card >>> .el-input-number .el-input__inner {
  text-align: left;
  padding-left: 14px;
}

.config-card >>> .el-select .el-input__inner {
  background: #f2f3f5;
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
  border-bottom: 1px solid #f2f3f5;
}

.info-row:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #86909c;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #1d2129;
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
  color: #1d2129;
  line-height: 1.3;
}

.user-dialog-title .dialog-sub-title {
  margin-top: 6px;
  font-size: 13px;
  color: #86909c;
  font-weight: 400;
}

.user-form .required-star {
  color: #f53f3f;
}

.user-form .el-form-item__label {
  padding-bottom: 8px;
  line-height: 1.4;
  color: #1d2129;
  font-weight: 500;
}

.user-form .el-input__inner,
.user-form .el-select .el-input__inner {
  height: 44px;
  line-height: 44px;
  background: #f2f3f5;
  border: none;
  border-radius: 10px;
  color: #1d2129;
}

.user-form .el-input__inner:focus,
.user-form .el-select .el-input.is-focus .el-input__inner {
  background: #eef0f3;
  box-shadow: none;
}

.user-form .el-select .el-input .el-select__caret {
  color: #c9cdd4;
}

.user-dialog .btn-save i {
  margin-right: 4px;
}
</style>
