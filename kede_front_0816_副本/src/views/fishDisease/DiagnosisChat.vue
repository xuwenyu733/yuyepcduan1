<template>
  <div class="diagnosis-page">
    <header class="diagnosis-header">
      <div class="header-top">
        <div class="header-title-row">
          <MenuToggle />
          <h1 class="header-title">协同诊断</h1>
        </div>
        <HeaderAuth />
      </div>
      <p class="header-desc">
        {{ diagnosisRoleHint }}
      </p>
    </header>

    <div class="diagnosis-body">
      <!-- 左侧：诊断任务 -->
      <aside class="task-panel">
        <div class="panel-head">
          <h2 class="panel-title">诊断任务</h2>
          <p class="panel-desc">选择任务进行诊断</p>
        </div>

        <div class="filter-tabs">
          <button
            v-for="tab in listTabs"
            :key="tab.key"
            type="button"
            class="filter-tab"
            :class="{ active: listTab === tab.key }"
            @click="switchListTab(tab.key)"
          >{{ tab.label }}</button>
        </div>

        <div v-loading="loading" class="task-list">
          <div
            v-for="item in cases"
            :key="item.id"
            class="task-card"
            :class="{ active: current && current.id === item.id }"
            @click="selectCase(item)"
          >
            <div class="task-card-top">
              <span class="task-id">{{ item.caseNo || item.id }}</span>
              <span class="task-status" :class="item.status">
                <i :class="statusIcon(item.status)" />
                {{ item.statusLabel }}
              </span>
            </div>
            <div class="task-detail">{{ item.pondCode }} · {{ item.category || '未指定品种' }}</div>
            <div class="task-meta">
              {{ item.reporter || '—' }}
              <span v-if="item.assigneeName"> · 技工 {{ item.assigneeName }}</span>
              · {{ formatTime(item) }}
            </div>
          </div>
          <div v-if="!loading && !cases.length" class="list-empty">暂无{{ activeTabLabel }}任务</div>
        </div>
      </aside>

      <!-- 沟通窗口：左侧联系人 + 右侧会话 -->
      <section class="chat-panel">
        <template v-if="current">
          <aside class="contact-pane">
            <div class="contact-pane-head">
              <span>沟通窗口</span>
              <span class="ws-dot" :class="wsConnected ? 'online' : 'offline'" :title="wsConnected ? '实时连接中' : '连接断开'" />
            </div>
            <div class="contact-pane-sub">{{ current.caseNo || current.id }} · {{ current.pondCode }}</div>
            <div class="contact-search">
              <i class="el-icon-search" />
              <input
                v-model.trim="contactKeyword"
                type="text"
                class="contact-search-input"
                placeholder="搜索联系人"
              >
              <button
                v-if="contactKeyword"
                type="button"
                class="contact-search-clear"
                title="清除"
                @click="contactKeyword = ''"
              >
                <i class="el-icon-close" />
              </button>
            </div>
            <div class="contact-list">
              <button
                v-for="person in filteredChatContacts"
                :key="person.id"
                type="button"
                class="contact-item"
                :class="{ active: chatTarget && chatTarget.id === person.id }"
                @click="selectChatTarget(person)"
              >
                <span class="contact-avatar">{{ person.username.slice(0, 1) }}</span>
                <span class="contact-body">
                  <span class="contact-top">
                    <span class="contact-name">{{ person.username }}</span>
                    <span class="contact-time">{{ contactLastTime(person) }}</span>
                  </span>
                  <span class="contact-preview">{{ contactPreview(person) }}</span>
                </span>
              </button>
              <div v-if="!filteredChatContacts.length" class="contact-empty">
                {{ contactKeyword ? '未找到匹配联系人' : '暂无可选联系人' }}
              </div>
            </div>
          </aside>

          <div class="session-pane">
            <div class="session-head">
              <div class="session-head-info">
                <h2 class="session-title">{{ chatTarget ? chatTarget.username : '请选择聊天对象' }}</h2>
                <p v-if="chatTarget" class="session-sub">{{ chatTarget.roleLabel }} · {{ current.caseNo || current.id }}</p>
              </div>
              <div class="session-head-actions">
                <button
                  v-if="current.status === 'pending' && canAcceptDiagnosis"
                  type="button"
                  class="btn-open-collect"
                  @click="acceptDiagnosis"
                >受理任务</button>
                <button
                  v-if="contextPondCode && canOpenCollect"
                  type="button"
                  class="btn-open-collect"
                  @click="openCollectForPond"
                >在采集中打开此池</button>
                <button
                  v-if="canSubmitConclusion"
                  type="button"
                  class="btn-conclusion"
                  @click="openConclusionDialog"
                >提交诊断结论</button>
                <span v-if="isDiagnosisWorker && current.status === 'pending'" class="role-cap-tip">工人可沟通，受理由专家操作</span>
              </div>
            </div>

            <div v-if="contextPondCode" class="pond-context-card">
              <div class="pond-context-head">
                <span class="pond-context-title">池塘上下文</span>
                <span class="pond-context-code">{{ contextPondCode }}</span>
                <span v-if="pondMetaText" class="pond-context-meta">{{ pondMetaText }}</span>
                <span v-if="pondMissing" class="pond-context-missing">鱼池未配置</span>
                <button type="button" class="pond-context-refresh" :disabled="contextLoading" @click="loadPondContext">
                  <i class="el-icon-refresh" :class="{ spinning: contextLoading }" />
                </button>
              </div>
              <div v-if="pondMissing" class="pond-context-warn">
                当前池号不在基础配置的鱼池列表中，请先到「基础配置 → 养殖信息 → 鱼池管理」添加该鱼池。
              </div>
              <div class="pond-context-grid">
                <div class="pond-context-item">
                  <div class="pci-label">投喂</div>
                  <div class="pci-body">{{ feedContextText }}</div>
                </div>
                <div class="pond-context-item">
                  <div class="pci-label">生长</div>
                  <div class="pci-body">{{ growContextText }}</div>
                </div>
                <div class="pond-context-item">
                  <div class="pci-label">死亡/存塘</div>
                  <div class="pci-body">{{ deathContextText }}</div>
                </div>
              </div>
            </div>

            <div ref="chatBody" class="chat-body">
              <template v-if="chatTarget">
                <template v-for="(item, idx) in displayMessages">
                  <div v-if="item.type === 'time'" :key="'t-' + idx" class="msg-time-sep">{{ item.text }}</div>
                  <div
                    v-else
                    :key="item.data.id"
                    class="msg-row"
                    :class="isMine(item.data) ? 'is-mine' : 'is-peer'"
                  >
                    <div class="msg-avatar" :class="{ mine: isMine(item.data) }">{{ avatarText(item.data) }}</div>
                    <div class="msg-bubble" :class="'type-' + (item.data.msgType || 'text')">
                      <img
                        v-if="item.data.msgType === 'image' && item.data.fileUrl"
                        :src="item.data.fileUrl"
                        :alt="item.data.fileName || '图片'"
                        class="msg-image"
                        @click="previewImage(item.data.fileUrl)"
                      >
                      <a
                        v-else-if="item.data.msgType === 'file' && item.data.fileUrl"
                        class="msg-file"
                        :href="item.data.fileUrl"
                        :download="item.data.fileName || '附件'"
                      >
                        <i class="el-icon-document" />
                        <span class="msg-file-meta">
                          <span class="msg-file-name">{{ item.data.fileName || '附件' }}</span>
                          <span v-if="item.data.fileSize" class="msg-file-size">{{ formatFileSize(item.data.fileSize) }}</span>
                        </span>
                      </a>
                      <button
                        v-else-if="false && item.data.msgType === 'voice' && item.data.fileUrl"
                        type="button"
                        class="msg-voice"
                        @click="playVoice(item.data)"
                      >
                        <i class="el-icon-microphone" />
                        <span>{{ item.data.duration || 1 }}″</span>
                      </button>
                      <template v-else>{{ item.data.content }}</template>
                    </div>
                  </div>
                </template>
                <div v-if="!visibleMessages.length" class="chat-thread-empty">
                  还没有与 {{ chatTarget.username }} 的消息，发送第一条吧
                </div>
              </template>
              <div v-else class="chat-thread-empty">请从左侧选择聊天对象</div>
            </div>

            <div class="chat-composer">
              <div class="composer-tools">
                <button
                  type="button"
                  class="tool-btn"
                  title="上传图片"
                  :disabled="!chatTarget"
                  @click="pickImage"
                >
                  <i class="el-icon-picture-outline" />
                </button>
                <button
                  type="button"
                  class="tool-btn"
                  title="文件"
                  :disabled="!chatTarget"
                  @click="pickFile"
                >
                  <i class="el-icon-folder" />
                </button>
                <button
                  v-if="false"
                  type="button"
                  class="tool-btn"
                  :class="{ active: recording }"
                  :title="recording ? '点击结束录音' : '语音'"
                  :disabled="!chatTarget"
                  @click="toggleVoice"
                >
                  <i class="el-icon-microphone" />
                </button>
                <input
                  ref="imageInput"
                  type="file"
                  accept="image/*"
                  class="hidden-file"
                  @change="onImageSelected"
                >
                <input
                  ref="fileInput"
                  type="file"
                  class="hidden-file"
                  @change="onFileSelected"
                >
              </div>
              <div v-if="false && recording" class="recording-bar">
                <span class="recording-dot" />
                正在录音 {{ recordSeconds }}″，再次点击麦克风结束并发送
              </div>
              <el-input
                v-model="inputText"
                type="textarea"
                :autosize="{ minRows: 3, maxRows: 6 }"
                :placeholder="chatTarget ? '输入消息...' : '请先选择聊天对象'"
                :disabled="!chatTarget"
                resize="none"
                class="composer-input"
                @keydown.enter.native.prevent="handleSend"
              />
              <div class="composer-footer">
                <span class="composer-tip">Enter 发送</span>
                <button type="button" class="btn-send" :disabled="sending || !chatTarget" @click="handleSend">
                  {{ sending ? '发送中' : '发送' }}
                </button>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="chat-empty">
          <div class="empty-icon"><i class="el-icon-chat-dot-round" /></div>
          <p>请选择诊断任务</p>
        </div>
      </section>
    </div>

    <el-dialog
      title="受理任务并分配技工"
      :visible.sync="acceptVisible"
      width="480px"
      custom-class="disease-dialog"
    >
      <el-form ref="acceptRef" :model="acceptForm" :rules="acceptRules" label-position="top">
        <el-form-item label="负责技工" prop="assigneeName">
          <el-select
            v-model="acceptForm.assigneeName"
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入技工"
            style="width:100%"
            @change="onAssigneePicked"
          >
            <el-option
              v-for="w in workerOptions"
              :key="w.id || w.username"
              :label="w.username + (w.roleLabel ? `（${w.roleLabel}）` : '')"
              :value="w.username"
            />
          </el-select>
        </el-form-item>
        <p class="accept-hint">受理后任务进入「进行中」，并自动归档到案例库（供后续 AI 分析）。未受理则保持「待诊断」。</p>
      </el-form>
      <span slot="footer">
        <el-button @click="acceptVisible = false">取消</el-button>
        <el-button class="btn-dark" :loading="saving" @click="confirmAcceptDiagnosis">确认受理</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="填写诊断结论"
      :visible.sync="conclusionVisible"
      width="520px"
      custom-class="disease-dialog"
    >
      <el-form ref="conclusionRef" :model="conclusionForm" :rules="conclusionRules" label-position="top">
        <el-form-item label="诊断结论" prop="suspectedDisease">
          <el-input v-model="conclusionForm.suspectedDisease" placeholder="如：细菌性烂鳃" />
        </el-form-item>
        <el-form-item label="治疗方案" prop="treatment">
          <el-input
            v-model="conclusionForm.treatment"
            type="textarea"
            :rows="4"
            placeholder="填写具体治疗方案"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="conclusionVisible = false">取消</el-button>
        <el-button class="btn-dark" :loading="saving" @click="saveConclusion">提交结论</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchDiagnosisRecords,
  updateDiagnosisRecord,
  chatDiagnosis,
  fetchChatContacts
} from '@/api/fishDisease'
import { fetchMergedPonds } from '@/utils/pondBridge'
import { canSubmitDiagnosisConclusion } from '@/utils/diagnosisStatus'
import MenuToggle from '@/views/fishDisease/components/MenuToggle.vue'
import HeaderAuth from '@/views/fishDisease/components/HeaderAuth.vue'
import ChatSocket from '@/views/fishDisease/utils/chatSocket'
import { getUser, hasRole } from '@/views/fishDisease/utils/auth'
import { readPendingMedia, clearPendingMedia } from '@/views/fishDisease/utils/handoff'
import { getPrecisionFeedRecommend } from '@/api/fish/zkPrecisionFeeding'
import { getDashboardData } from '@/api/fish/zkgrowdailyrecord'
import { getZkFishOperationLogPage } from '@/api/fish/zkFishOperationLog'

export default {
  name: 'DiagnosisChat',
  components: { MenuToggle, HeaderAuth },
  inject: {
    takePendingDiagnosisCaseId: { default: null },
    takePendingDiagnosisTab: { default: null },
    takePendingPondCode: { default: null },
    peekPendingPondCode: { default: null },
    takePendingMedia: { default: null },
    switchFishNav: { default: null }
  },
  data() {
    return {
      loading: false,
      saving: false,
      sending: false,
      wsConnected: false,
      listTab: 'active',
      listTabs: [
        { key: 'pending', label: '待诊断' },
        { key: 'active', label: '进行中' },
        { key: 'done', label: '已完成' }
      ],
      cases: [],
      current: null,
      contacts: [],
      chatTarget: null,
      contactKeyword: '',
      inputText: '',
      recording: false,
      recordSeconds: 0,
      mediaRecorder: null,
      recordChunks: [],
      recordTimer: null,
      audioPlayer: null,
      acceptVisible: false,
      acceptForm: {
        assigneeId: '',
        assigneeName: ''
      },
      acceptRules: {
        assigneeName: [{ required: true, message: '请选择负责技工', trigger: 'change' }]
      },
      conclusionVisible: false,
      conclusionForm: {
        suspectedDisease: '',
        treatment: '',
        conclusion: ''
      },
      conclusionRules: {
        suspectedDisease: [{ required: true, message: '请填写诊断结论', trigger: 'blur' }],
        treatment: [{ required: true, message: '请填写治疗方案', trigger: 'blur' }]
      },
      pendingCaseId: null,
      contextPondCode: '',
      contextPondInfo: null,
      pondMissing: false,
      contextLoading: false,
      feedContext: null,
      growContext: null,
      deathContext: null,
      pendingMediaConsumed: false,
      chatSocket: null,
      offSocket: null
    }
  },
  computed: {
    me() {
      return getUser() || {}
    },
    /** 128：按平台角色/模块角色映射诊断身份 */
    diagnosisRole() {
      const roles = (this.$store && this.$store.getters && this.$store.getters.roles) || []
      const text = roles.map(r => String(r)).join(',')
      if (text && /admin|super_admin|管理员/i.test(text)) return 'admin'
      if (text && /expert|专家|兽医|leader|主管/i.test(text)) return 'expert'
      if (text && /worker|工人|技工|技术员|common|普通/i.test(text)) return 'worker'
      const local = String((this.me && this.me.role) || '')
      if (/admin/i.test(local)) return 'admin'
      if (/expert/i.test(local)) return 'expert'
      if (/worker/i.test(local)) return 'worker'
      // 有平台角色但未命中专家/管理 → 按普通工人能力
      if (roles.length) return 'worker'
      return hasRole('admin') ? 'admin' : (hasRole('expert') ? 'expert' : 'worker')
    },
    isDiagnosisExpert() {
      return this.diagnosisRole === 'admin' || this.diagnosisRole === 'expert'
    },
    isDiagnosisWorker() {
      return this.diagnosisRole === 'worker'
    },
    diagnosisRoleHint() {
      if (this.isDiagnosisExpert) {
        return '专家/管理员：可受理任务、提交诊断结论与治疗方案；工人侧重上报与沟通'
      }
      return '工人：可查看任务、实时沟通与打开采集；受理与提交结论仅专家/管理员可用'
    },
    canAcceptDiagnosis() {
      return this.isDiagnosisExpert
    },
    canSubmitConclusion() {
      // 128：结论提交仅专家/管理员；状态仍须为进行中
      return this.isDiagnosisExpert && !!(this.current && canSubmitDiagnosisConclusion(this.current.status))
    },
    canOpenCollect() {
      // 工人与专家均可跳转采集
      return this.isDiagnosisExpert || this.isDiagnosisWorker
    },
    workerOptions() {
      const list = (this.contacts || []).filter(u => {
        const role = String(u.role || u.roleLabel || '')
        return /worker|工人|技工|技术/i.test(role)
      })
      const names = new Set(list.map(u => u.username))
      if (this.current && this.current.reporter && !names.has(this.current.reporter)) {
        list.unshift({
          id: 'reporter',
          username: this.current.reporter,
          role: 'worker',
          roleLabel: '上报技工'
        })
      }
      if (!list.length) {
        list.push(
          { id: 'U-002', username: '张工', role: 'worker', roleLabel: '工人' },
          { id: 'U-003', username: '李工', role: 'worker', roleLabel: '工人' }
        )
      }
      return list
    },
    activeTabLabel() {
      const tab = this.listTabs.find(t => t.key === this.listTab)
      return tab ? tab.label : ''
    },
    feedContextText() {
      const d = this.feedContext
      if (!d) return '暂无数据'
      const parts = []
      if (d.recommendAmountKg != null || d.recommendAmount != null || d.totalAmountKg != null) {
        parts.push(`推荐量 ${d.recommendAmountKg ?? d.recommendAmount ?? d.totalAmountKg} kg`)
      }
      if (d.healthLevel || d.healthStatus) parts.push(`健康 ${d.healthLevel || d.healthStatus}`)
      if (d.dissolvedOxygen != null || d.doValue != null) parts.push(`溶氧 ${d.dissolvedOxygen ?? d.doValue}`)
      if (d.feedType) parts.push(String(d.feedType))
      return parts.length ? parts.join(' · ') : '暂无数据'
    },
    growContextText() {
      const d = this.growContext
      if (!d) return '暂无数据'
      const parts = []
      if (d.recommendFeedRate != null || d.feedRate != null) {
        parts.push(`推荐投饵率 ${d.recommendFeedRate ?? d.feedRate}`)
      }
      if (d.growthStatus || d.growStatus || d.statusText) {
        parts.push(d.growthStatus || d.growStatus || d.statusText)
      }
      if (d.breedingDays != null) parts.push(`养殖 ${d.breedingDays} 天`)
      if (d.avgWeight != null) parts.push(`均重 ${d.avgWeight}`)
      return parts.length ? parts.join(' · ') : '暂无数据'
    },
    deathContextText() {
      const d = this.deathContext
      if (!d) return '暂无数据'
      if (typeof d === 'string') return d
      const parts = []
      if (d.recentDead != null) parts.push(`近期死亡 ${d.recentDead}`)
      if (d.currFishNum != null || d.stockNum != null) parts.push(`存塘 ${d.currFishNum ?? d.stockNum}`)
      if (d.summary) parts.push(d.summary)
      return parts.length ? parts.join(' · ') : '暂无数据'
    },
    pondMetaText() {
      const p = this.contextPondInfo
      if (!p) return ''
      const parts = []
      if (p.size) parts.push(p.size)
      if (p.location) parts.push(p.location)
      if (p.manager) parts.push(p.manager)
      return parts.join(' · ')
    },
    chatContacts() {
      if (!this.current) return []
      const list = [...this.contacts]
      const ensure = (username, role, roleLabel) => {
        if (!username) return
        if (username === this.me.username) return
        if (list.some(u => u.username === username)) return
        list.unshift({
          id: `local-${username}`,
          username,
          role,
          roleLabel,
          email: ''
        })
      }
      ensure(this.current.reporter, 'worker', '工人')
      ensure(this.current.assigneeName, 'worker', '技工')
      ensure(this.current.veterinarian, 'expert', '专家')
      return list
    },
    filteredChatContacts() {
      const keyword = (this.contactKeyword || '').toLowerCase()
      if (!keyword) return this.chatContacts
      return this.chatContacts.filter(person => {
        const name = (person.username || '').toLowerCase()
        const role = (person.roleLabel || person.role || '').toLowerCase()
        const email = (person.email || '').toLowerCase()
        return name.includes(keyword) || role.includes(keyword) || email.includes(keyword)
      })
    },
    visibleMessages() {
      const messages = (this.current && this.current.messages) || []
      if (!this.chatTarget) return []
      const meName = this.me.username
      const targetName = this.chatTarget.username
      return messages.filter(m => {
        if (m.toName) {
          return (
            (m.senderName === meName && m.toName === targetName) ||
            (m.senderName === targetName && m.toName === meName)
          )
        }
        // 兼容历史消息：展示双方发送的记录
        return m.senderName === meName || m.senderName === targetName
      })
    },
    displayMessages() {
      const list = []
      let lastTime = ''
      this.visibleMessages.forEach(msg => {
        const t = msg.time || ''
        if (t && t !== lastTime) {
          list.push({ type: 'time', text: t })
          lastTime = t
        }
        list.push({ type: 'msg', data: msg })
      })
      return list
    }
  },
  created() {
    this.applyPendingCaseId()
    this.applyPendingPondCode()
    this.setupSocket()
    this.init()
  },
  activated() {
    // keep-alive：从监测页「查看」再次进入时消费 shell 传入的 caseId
    this.applyPendingCaseId()
    this.applyPendingPondCode()
    if (this.pendingCaseId && this.cases && this.cases.length) {
      this.openPendingCase()
    }
    this.$nextTick(() => {
      this.consumePendingMedia()
      this.loadPondContext()
    })
  },
  beforeDestroy() {
    this.stopRecording(false)
    if (this.audioPlayer) {
      this.audioPlayer.pause()
      this.audioPlayer = null
    }
    if (this.offSocket) this.offSocket()
    if (this.chatSocket) this.chatSocket.close()
  },
  methods: {
    applyPendingPondCode() {
      let code = null
      if (typeof this.peekPendingPondCode === 'function') {
        code = this.peekPendingPondCode()
      }
      if (!code && typeof this.takePendingPondCode === 'function') {
        // peek 不可用时再 take（采集页会单独 take）
        code = this.takePendingPondCode()
      }
      if (!code && this.$route && this.$route.query && this.$route.query.pondCode) {
        code = this.$route.query.pondCode
      }
      if (code) this.contextPondCode = String(code)
    },
    openCollectForPond() {
      const pondCode = this.contextPondCode || (this.current && this.current.pondCode) || ''
      if (typeof this.switchFishNav === 'function') {
        this.switchFishNav('collect', pondCode ? { pondCode } : {})
        return
      }
      this.$router.push({
        path: '/fish-disease',
        query: { tab: 'collect', ...(pondCode ? { pondCode } : {}) }
      })
    },
    async loadPondContext() {
      const pondCode = this.contextPondCode || (this.current && this.current.pondCode) || ''
      if (!pondCode) {
        this.feedContext = null
        this.growContext = null
        this.deathContext = null
        this.contextPondInfo = null
        this.pondMissing = false
        return
      }
      this.contextPondCode = String(pondCode)
      this.contextLoading = true
      this.feedContext = null
      this.growContext = null
      this.deathContext = null
      this.contextPondInfo = null
      this.pondMissing = false
      const normalizePondKey = (v) => String(v || '')
        .trim()
        .toUpperCase()
        .replace(/[\s_]+/g, '')
        // A-01 / A-1 → A1；保留 A1-1 这类多段编号
        .replace(/([A-Z])-0*(\d+)/g, '$1$2')
        .replace(/-0+(\d+)/g, '-$1')
      const targetKey = normalizePondKey(pondCode)
      const matchPond = (p) => {
        if (!p) return false
        const keys = [p.code, p.pondCode, p.poolCode, p.pondDetailNum, p.id, p.name]
        return keys.some((k) => k != null && k !== '' && normalizePondKey(k) === targetKey)
      }
      try {
        // 校验鱼病+精准投喂/生长曲线合并后的鱼池；池号格式差异（A1 / A-01）做归一匹配
        try {
          const list = await fetchMergedPonds()
          const found = (Array.isArray(list) ? list : []).find(matchPond)
          if (found) {
            this.contextPondInfo = found
            this.pondMissing = false
          } else if (!list.length) {
            this.pondMissing = true
            this.contextPondInfo = null
          } else {
            this.pondMissing = true
            this.contextPondInfo = null
          }
        } catch (e) {
          // 模块服务不可用时不阻断协同诊断主流程
          this.pondMissing = false
        }

        const silent = { silent: true, hideErrorMessage: true }
        const settled = await Promise.allSettled([
          getPrecisionFeedRecommend(pondCode, silent),
          getDashboardData({ searchType: 'pool', searchId: pondCode }, silent),
          getZkFishOperationLogPage({
            pageNo: 1,
            pageSize: 5,
            pondDetailNum: pondCode,
            pondCode
          }, silent)
        ])
        const feedRes = settled[0].status === 'fulfilled' ? settled[0].value : null
        const growRes = settled[1].status === 'fulfilled' ? settled[1].value : null
        const deathRes = settled[2].status === 'fulfilled' ? settled[2].value : null
        this.feedContext = (feedRes && feedRes.data) || null
        this.growContext = (growRes && growRes.data) || null
        const list = (deathRes && deathRes.data && (deathRes.data.list || deathRes.data.records)) || []
        if (list.length) {
          const recent = list[0]
          const deadSum = list.reduce((s, row) => s + Number(row.deadNum || row.deadCount || row.splitAfterDead || 0), 0)
          this.deathContext = {
            recentDead: deadSum || recent.deadNum || recent.deadCount || 0,
            currFishNum: recent.currFishNum || recent.stockNum || recent.fishNum,
            summary: recent.remark || recent.operateType || recent.operationType || ''
          }
        } else {
          this.deathContext = null
        }
      } catch (e) {
        this.feedContext = null
        this.growContext = null
        this.deathContext = null
      } finally {
        this.contextLoading = false
      }
    },
    async consumePendingMedia() {
      if (this.pendingMediaConsumed) return
      const media = readPendingMedia()
      if (!media || !media.dataUrl) return
      if (media.pondCode && !this.contextPondCode) {
        this.contextPondCode = String(media.pondCode)
      }
      if (!this.current) return
      clearPendingMedia()
      this.pendingMediaConsumed = true
      this.ensureChatTarget()
      const fileName = media.fileName || `video-snap-${Date.now()}.jpg`
      let fileUrl = media.dataUrl
      try {
        fileUrl = await this.ensureImageUnder2MB(fileUrl)
      } catch (e) { /* keep original */ }
      const approxSize = Math.ceil((fileUrl.length * 3) / 4)
      await this.sendRichMessage({
        msgType: 'image',
        content: `[图片] ${fileName}`,
        fileName,
        fileUrl,
        fileSize: approxSize
      })
    },
    ensureImageUnder2MB(dataUrl) {
      return new Promise((resolve) => {
        if (!dataUrl || dataUrl.length < 2.5 * 1024 * 1024) {
          resolve(dataUrl)
          return
        }
        const img = new Image()
        img.onload = () => {
          const canvas = document.createElement('canvas')
          const maxW = 1280
          const scale = Math.min(1, maxW / img.width)
          canvas.width = Math.max(1, Math.round(img.width * scale))
          canvas.height = Math.max(1, Math.round(img.height * scale))
          const ctx = canvas.getContext('2d')
          ctx.drawImage(img, 0, 0, canvas.width, canvas.height)
          resolve(canvas.toDataURL('image/jpeg', 0.72))
        }
        img.onerror = () => resolve(dataUrl)
        img.src = dataUrl
      })
    },
    pickImage() {
      if (!this.ensureCanAttach()) return
      this.$refs.imageInput && this.$refs.imageInput.click()
    },
    pickFile() {
      if (!this.ensureCanAttach()) return
      this.$refs.fileInput && this.$refs.fileInput.click()
    },
    ensureCanAttach() {
      if (!this.current) {
        this.$message.warning('请先选择诊断任务')
        return false
      }
      if (!this.chatTarget) {
        this.$message.warning('请先选择聊天对象')
        return false
      }
      return true
    },
    onImageSelected(e) {
      const file = e.target.files && e.target.files[0]
      e.target.value = ''
      if (!file) return
      if (!file.type.startsWith('image/')) {
        this.$message.warning('请选择图片文件')
        return
      }
      if (file.size > 2 * 1024 * 1024) {
        this.$message.warning('图片请小于 2MB')
        return
      }
      this.readFileAsDataUrl(file).then(url => {
        this.sendRichMessage({
          msgType: 'image',
          content: `[图片] ${file.name}`,
          fileName: file.name,
          fileUrl: url,
          fileSize: file.size
        })
      })
    },
    onFileSelected(e) {
      const file = e.target.files && e.target.files[0]
      e.target.value = ''
      if (!file) return
      if (file.size > 2 * 1024 * 1024) {
        this.$message.warning('文件请小于 2MB')
        return
      }
      this.readFileAsDataUrl(file).then(url => {
        this.sendRichMessage({
          msgType: 'file',
          content: `[文件] ${file.name}`,
          fileName: file.name,
          fileUrl: url,
          fileSize: file.size
        })
      })
    },
    readFileAsDataUrl(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => resolve(reader.result)
        reader.onerror = () => {
          this.$message.error('文件读取失败')
          reject(new Error('read failed'))
        }
        reader.readAsDataURL(file)
      })
    },
    async toggleVoice() {
      if (!this.ensureCanAttach()) return
      if (this.recording) {
        this.stopRecording(true)
        return
      }
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        this.$message.warning('当前浏览器不支持录音')
        return
      }
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
        const recorder = new MediaRecorder(stream)
        this.recordChunks = []
        this.mediaRecorder = recorder
        this.recording = true
        this.recordSeconds = 0
        this.recordTimer = setInterval(() => {
          this.recordSeconds += 1
          if (this.recordSeconds >= 60) this.stopRecording(true)
        }, 1000)
        recorder.ondataavailable = ev => {
          if (ev.data && ev.data.size) this.recordChunks.push(ev.data)
        }
        recorder.onstop = () => {
          stream.getTracks().forEach(t => t.stop())
        }
        recorder.start()
      } catch (err) {
        this.$message.error('无法使用麦克风，请检查权限')
      }
    },
    stopRecording(shouldSend) {
      if (this.recordTimer) {
        clearInterval(this.recordTimer)
        this.recordTimer = null
      }
      const recorder = this.mediaRecorder
      const seconds = this.recordSeconds
      this.recording = false
      this.mediaRecorder = null
      if (!recorder) return
      const finish = () => {
        if (!shouldSend || !this.recordChunks.length || seconds < 1) {
          this.recordChunks = []
          if (shouldSend && seconds < 1) this.$message.warning('录音时间太短')
          return
        }
        const blob = new Blob(this.recordChunks, { type: recorder.mimeType || 'audio/webm' })
        this.recordChunks = []
        if (blob.size > 2 * 1024 * 1024) {
          this.$message.warning('语音过大，请缩短录音')
          return
        }
        const reader = new FileReader()
        reader.onload = () => {
          this.sendRichMessage({
            msgType: 'voice',
            content: `[语音] ${seconds}″`,
            fileName: `voice-${Date.now()}.webm`,
            fileUrl: reader.result,
            fileSize: blob.size,
            duration: seconds
          })
        }
        reader.readAsDataURL(blob)
      }
      if (recorder.state !== 'inactive') {
        recorder.addEventListener('stop', finish, { once: true })
        recorder.stop()
      } else {
        finish()
      }
    },
    playVoice(msg) {
      if (!msg.fileUrl) return
      if (this.audioPlayer) {
        this.audioPlayer.pause()
        this.audioPlayer = null
      }
      const audio = new Audio(msg.fileUrl)
      this.audioPlayer = audio
      audio.play().catch(() => {
        this.$message.error('语音播放失败')
      })
    },
    previewImage(url) {
      window.open(url, '_blank')
    },
    formatFileSize(size) {
      if (!size) return ''
      if (size < 1024) return `${size} B`
      if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
      return `${(size / (1024 * 1024)).toFixed(1)} MB`
    },
    messagePreviewText(msg) {
      if (!msg) return ''
      if (msg.msgType === 'image') return '[图片]'
      if (msg.msgType === 'file') return `[文件] ${msg.fileName || ''}`.trim()
      if (msg.msgType === 'voice') return `[语音] ${msg.duration || 1}″`
      return msg.content || ''
    },
    setupSocket() {
      this.chatSocket = new ChatSocket()
      this.offSocket = this.chatSocket.on(this.onSocketEvent)
      this.chatSocket.connect()
    },
    onSocketEvent(data) {
      if (data.type === 'socket_open' || data.type === 'ready') {
        this.wsConnected = true
        return
      }
      if (data.type === 'socket_close' || data.type === 'socket_error') {
        this.wsConnected = false
        return
      }
      if (data.type === 'joined' && data.record) {
        if (this.current && this.current.id === data.caseId) {
          this.current = {
            ...this.current,
            ...data.record,
            messages: [...(data.record.messages || [])]
          }
          this.$nextTick(this.scrollChat)
        }
        return
      }
      if (data.type === 'message' && data.message) {
        this.applyIncomingMessage(data)
        return
      }
      if (data.type === 'error') {
        this.sending = false
        this.$message.error(data.message || '实时通信失败')
      }
    },
    applyIncomingMessage(data) {
      const { caseId, message, record } = data
      if (!message) return

      // 更新当前会话消息
      if (this.current && this.current.id === caseId) {
        const exists = (this.current.messages || []).some(m => m.id === message.id)
        const messages = exists
          ? this.current.messages
          : [...(this.current.messages || []), message]
        this.current = {
          ...this.current,
          ...(record || {}),
          messages
        }
        this.sending = false
        this.$nextTick(this.scrollChat)
      }

      // 同步任务列表中的对应项
      const idx = this.cases.findIndex(c => c.id === caseId)
      if (idx !== -1) {
        const item = this.cases[idx]
        const exists = (item.messages || []).some(m => m.id === message.id)
        const next = {
          ...item,
          ...(record || {}),
          messages: exists ? item.messages : [...(item.messages || []), message]
        }
        this.$set(this.cases, idx, next)
      }
    },
    formatTime(item) {
      const t = item.createdAt || item.updatedAt || ''
      return t.length > 16 ? t.slice(0, 16) : t
    },
    statusIcon(status) {
      if (status === 'resolved') return 'el-icon-circle-check'
      return 'el-icon-time'
    },
    isMine(msg) {
      if (!msg) return false
      if (msg.senderId && this.me.id) return msg.senderId === this.me.id
      return msg.senderName === this.me.username
    },
    avatarText(msg) {
      const name = msg.senderName || '用'
      return name.slice(0, 1)
    },
    applyPendingCaseId() {
      let caseId = null
      if (typeof this.takePendingDiagnosisCaseId === 'function') {
        caseId = this.takePendingDiagnosisCaseId()
      }
      if (!caseId && this.$route && this.$route.query) {
        caseId = this.$route.query.caseId
      }
      if (caseId) this.pendingCaseId = caseId
      let tab = null
      if (typeof this.takePendingDiagnosisTab === 'function') {
        tab = this.takePendingDiagnosisTab()
      }
      if (!tab && this.$route && this.$route.query) tab = this.$route.query.tab
      if (tab === 'pending' || tab === 'active' || tab === 'resolved') {
        this.listTab = tab
      } else if (caseId) {
        this.listTab = 'pending'
      }
    },
    openPendingCase() {
      if (!this.pendingCaseId) return false
      const found = this.cases.find(c => c.id === this.pendingCaseId)
      if (!found) return false
      this.listTab = 'pending'
      this.selectCase(found)
      this.pendingCaseId = null
      return true
    },
    async init() {
      try {
        // 从数据采集跳转过来时优先展示「待诊断」
        if (this.pendingCaseId || (this.$route && this.$route.query && this.$route.query.tab === 'pending')) {
          this.listTab = 'pending'
        }
        await Promise.all([this.loadCases(), this.loadContacts()])
        if (this.openPendingCase()) {
          await this.afterCaseReady()
          return
        }
        if (this.contextPondCode) {
          const matched = this.cases.find(c => String(c.pondCode || '') === String(this.contextPondCode))
          if (matched) {
            this.selectCase(matched)
            await this.afterCaseReady()
            return
          }
        }
        if (this.cases.length) this.selectCase(this.cases[0])
        await this.afterCaseReady()
      } catch (e) {
        this.$message.error('加载诊断任务失败，请确认统一 BFF 已启动（端口 3020）')
        await this.loadPondContext()
      }
    },
    async afterCaseReady() {
      if (this.current && this.current.pondCode && !this.contextPondCode) {
        this.contextPondCode = String(this.current.pondCode)
      }
      await this.loadPondContext()
      await this.$nextTick()
      await this.consumePendingMedia()
    },
    async loadContacts() {
      try {
        const res = await fetchChatContacts()
        this.contacts = res.data || []
      } catch (e) {
        this.contacts = []
      }
    },
    switchListTab(tab) {
      if (this.listTab === tab) return
      this.listTab = tab
      // 仅刷新上方任务列表，不影响下方正在进行的聊天
      this.loadCases({ preserveChat: true })
    },
    async loadCases({ preserveChat = false } = {}) {
      this.loading = true
      try {
        const res = await fetchDiagnosisRecords({ tab: this.listTab })
        this.cases = res.data || []

        if (preserveChat) {
          if (this.current) {
            const found = this.cases.find(c => c.id === this.current.id)
            if (found) {
              // 同步列表中的最新消息，但不切换会话、不重置聊天对象
              const keepTarget = this.chatTarget
              this.current = {
                ...this.current,
                ...found,
                messages: [...(found.messages || this.current.messages || [])]
              }
              this.chatTarget = keepTarget
            }
          }
          return
        }

        if (this.current) {
          const found = this.cases.find(c => c.id === this.current.id)
          if (found) {
            this.current = { ...found, messages: [...(found.messages || [])] }
            this.ensureChatTarget()
          } else {
            this.current = this.cases[0]
              ? { ...this.cases[0], messages: [...(this.cases[0].messages || [])] }
              : null
            if (this.current) this.ensureChatTarget()
            else this.chatTarget = null
          }
        } else if (this.cases.length) {
          this.current = { ...this.cases[0], messages: [...(this.cases[0].messages || [])] }
          this.ensureChatTarget()
        }
      } finally {
        this.loading = false
      }
    },
    currentSender() {
      const user = this.me
      const role = user.role === 'worker' ? 'worker' : user.role === 'admin' ? 'expert' : 'expert'
      return {
        role,
        senderId: user.id || '',
        senderName: user.username || (role === 'expert' ? '陈专家' : '工人')
      }
    },
    selectChatTarget(person) {
      this.chatTarget = person
      this.$nextTick(this.scrollChat)
    },
    messagesWithPerson(person) {
      if (!this.current || !person) return []
      const meName = this.me.username
      const targetName = person.username
      return (this.current.messages || []).filter(m => {
        if (m.toName) {
          return (
            (m.senderName === meName && m.toName === targetName) ||
            (m.senderName === targetName && m.toName === meName)
          )
        }
        return m.senderName === meName || m.senderName === targetName
      })
    },
    contactPreview(person) {
      const list = this.messagesWithPerson(person)
      if (!list.length) return person.roleLabel || '暂无消息'
      const last = list[list.length - 1]
      const prefix = last.senderName === this.me.username ? '我: ' : ''
      const text = `${prefix}${this.messagePreviewText(last)}`
      return text.length > 22 ? `${text.slice(0, 22)}…` : text
    },
    contactLastTime(person) {
      const list = this.messagesWithPerson(person)
      if (!list.length) return ''
      return list[list.length - 1].time || ''
    },
    ensureChatTarget() {
      const list = this.chatContacts
      if (!list.length) {
        this.chatTarget = null
        return
      }
      if (this.chatTarget && list.some(u => u.id === this.chatTarget.id || u.username === this.chatTarget.username)) {
        const matched = list.find(u => u.id === this.chatTarget.id || u.username === this.chatTarget.username)
        this.chatTarget = matched
        return
      }
      // 默认：专家/管理员优先找上报工人；工人优先找负责专家
      let preferred = null
      if (this.me.role === 'worker') {
        preferred = list.find(u => u.username === this.current.veterinarian) || list.find(u => u.role === 'expert')
      } else {
        preferred = list.find(u => u.username === this.current.reporter) || list.find(u => u.role === 'worker')
      }
      this.chatTarget = preferred || list[0]
    },
    selectCase(item) {
      this.current = { ...item, messages: [...(item.messages || [])] }
      this.contactKeyword = ''
      if (item && item.pondCode) this.contextPondCode = String(item.pondCode)
      this.ensureChatTarget()
      if (this.chatSocket) {
        this.chatSocket.join(item.id, this.currentSender())
      }
      this.loadPondContext()
      this.$nextTick(this.scrollChat)
    },
    async handleSend() {
      const text = this.inputText.trim()
      if (!text) {
        this.$message.warning('请输入消息')
        return
      }
      await this.sendRichMessage({
        msgType: 'text',
        content: text
      })
    },
    async sendRichMessage(extra = {}) {
      if (!this.current) return
      if (!this.chatTarget) {
        this.$message.warning('请先选择聊天对象')
        return
      }
      const content = (extra.content || '').trim()
      if (!content) {
        this.$message.warning('请输入消息')
        return
      }

      this.sending = true
      const payload = {
        message: content,
        ...this.currentSender(),
        toUserId: this.chatTarget.id.startsWith('local-') ? '' : this.chatTarget.id,
        toName: this.chatTarget.username,
        msgType: extra.msgType || 'text',
        fileName: extra.fileName || '',
        fileUrl: extra.fileUrl || '',
        fileSize: extra.fileSize || 0,
        duration: extra.duration || 0
      }

      if (this.chatSocket && this.chatSocket.connected) {
        const ok = this.chatSocket.chat(content, {
          caseId: this.current.id,
          ...payload
        })
        if (ok) {
          if (extra.msgType === 'text' || !extra.msgType) this.inputText = ''
          setTimeout(() => {
            if (this.sending) this.sending = false
          }, 3000)
          return
        }
      }

      try {
        const res = await chatDiagnosis(this.current.id, payload)
        this.current = {
          ...res.data,
          messages: [...(res.data.messages || [])]
        }
        if (extra.msgType === 'text' || !extra.msgType) this.inputText = ''
        await this.loadCases({ preserveChat: true })
        this.$nextTick(this.scrollChat)
      } catch (e) {
        this.$message.error('消息发送失败')
      } finally {
        this.sending = false
      }
    },
    openConclusionDialog() {
      if (!this.canSubmitConclusion) {
        this.$message.warning('仅专家/管理员可提交诊断结论')
        return
      }
      this.conclusionForm = {
        suspectedDisease: this.current.suspectedDisease || '',
        treatment: this.current.treatment || '',
        conclusion: this.current.conclusion || ''
      }
      this.conclusionVisible = true
    },
    acceptDiagnosis() {
      if (!this.canAcceptDiagnosis) {
        this.$message.warning('仅专家/管理员可受理诊断任务')
        return
      }
      if (!this.current || this.current.status !== 'pending') return
      const defaultWorker = (this.workerOptions[0] && this.workerOptions[0].username) || this.current.reporter || ''
      this.acceptForm = {
        assigneeId: (this.workerOptions[0] && this.workerOptions[0].id) || '',
        assigneeName: defaultWorker
      }
      this.acceptVisible = true
    },
    onAssigneePicked(name) {
      const hit = this.workerOptions.find(w => w.username === name)
      this.acceptForm.assigneeId = hit ? hit.id : ''
      this.acceptForm.assigneeName = name
    },
    confirmAcceptDiagnosis() {
      if (!this.canAcceptDiagnosis) {
        this.$message.warning('仅专家/管理员可受理诊断任务')
        return
      }
      this.$refs.acceptRef.validate(async valid => {
        if (!valid) return
        if (!this.current || this.current.status !== 'pending') return
        this.saving = true
        try {
          const res = await updateDiagnosisRecord(this.current.id, {
            status: 'diagnosing',
            veterinarian: this.me.username || this.me.roleLabel || '专家',
            assigneeId: this.acceptForm.assigneeId || '',
            assigneeName: this.acceptForm.assigneeName,
            acceptedAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
          })
          this.current = { ...this.current, ...(res.data || {}), messages: [...((res.data && res.data.messages) || this.current.messages || [])] }
          this.acceptVisible = false
          this.$message.success(`已受理并分配给技工「${this.acceptForm.assigneeName}」，任务进入进行中`)
          this.listTab = 'active'
          await this.loadCases()
          // 优先选中技工为沟通对象
          const assignee = this.chatContacts.find(u => u.username === this.acceptForm.assigneeName)
          if (assignee) this.chatTarget = assignee
        } catch (e) {
          this.$message.error('受理失败，请稍后重试')
        } finally {
          this.saving = false
        }
      })
    },
    saveConclusion() {
      this.$refs.conclusionRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const vetName = this.me.role === 'expert' || this.me.role === 'admin'
            ? this.me.username
            : this.current.veterinarian || '陈专家'
          const res = await updateDiagnosisRecord(this.current.id, {
            suspectedDisease: this.conclusionForm.suspectedDisease,
            treatment: this.conclusionForm.treatment,
            conclusion: this.conclusionForm.conclusion || this.conclusionForm.suspectedDisease,
            status: 'resolved',
            veterinarian: vetName
          })
          this.current = { ...this.current, ...res.data, messages: [...(res.data.messages || this.current.messages || [])] }
          this.conclusionVisible = false
          await this.loadCases()
          this.$message.success('诊断结论已提交')
        } finally {
          this.saving = false
        }
      })
    },
    scrollChat() {
      const el = this.$refs.chatBody
      if (el) el.scrollTop = el.scrollHeight
    }
  }
}
</script>

<style scoped>
.diagnosis-page {
  min-height: 100vh;
  background: #0a1520;
  display: flex;
  flex-direction: column;
  position: relative;
}

.diagnosis-header {
  background: #0f1a26;
  padding: 28px 40px 20px;
  flex-shrink: 0;
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

.diagnosis-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 0 40px 40px;
  min-height: 0;
}

.task-panel,
.chat-panel {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 16px;
  overflow: hidden;
}

.task-panel {
  display: flex;
  flex-direction: column;
  min-height: auto;
  max-height: 420px;
}

.chat-panel {
  display: flex;
  flex-direction: row;
  min-height: 620px;
  height: calc(100vh - 280px);
}

.panel-head {
  padding: 24px 24px 0;
}

.panel-title {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 600;
  color: #e8edf2;
}

.panel-desc {
  margin: 0;
  font-size: 14px;
  color: #7c8fa1;
}

.filter-tabs {
  display: flex;
  margin: 16px 20px 12px;
  background: rgba(124, 143, 161, 0.25);
  border-radius: 999px;
  padding: 4px;
}

.filter-tab {
  flex: 1;
  border: none;
  background: transparent;
  border-radius: 999px;
  padding: 9px 0;
  font-size: 13px;
  color: #7c8fa1;
  cursor: pointer;
  transition: all 0.15s;
}

.filter-tab.active {
  background: #0f1a26;
  color: #e8edf2;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.task-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px 20px;
}

.task-card {
  padding: 16px 18px;
  border: 1px solid rgba(124, 143, 161, 0.22);
  border-radius: 10px;
  cursor: pointer;
  margin-bottom: 10px;
  transition: border-color 0.15s, background 0.15s;
}

.task-card:hover {
  border-color: rgba(124, 143, 161, 0.35);
}

.task-card.active {
  background: rgba(64, 158, 255, 0.12);
  border-color: rgba(64, 158, 255, 0.4);
}

.task-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.task-id {
  font-size: 14px;
  font-weight: 600;
  color: #e8edf2;
}

.task-status {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.task-status.pending {
  background: rgba(64, 158, 255, 0.1);
  color: #7c8fa1;
}

.task-status.diagnosing,
.task-status.treating {
  background: rgba(64, 158, 255, 0.18);
  color: #409eff;
}

.task-status.resolved {
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
}

.task-detail {
  font-size: 15px;
  font-weight: 500;
  color: #e8edf2;
  margin-bottom: 6px;
}

.task-meta {
  font-size: 13px;
  color: #7c8fa1;
}

.list-empty {
  text-align: center;
  padding: 48px 16px;
  color: #7c8fa1;
  font-size: 13px;
}

.ws-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.ws-dot.online {
  background: #52c41a;
  box-shadow: 0 0 0 3px rgba(82, 196, 26, 0.18);
}

.ws-dot.offline {
  background: #5a6b7d;
}

.contact-pane {
  width: 260px;
  flex-shrink: 0;
  border-right: 1px solid rgba(124, 143, 161, 0.22);
  background: #0a1520;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.contact-pane-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 18px 16px 6px;
  font-size: 16px;
  font-weight: 600;
  color: #e8edf2;
}

.contact-pane-sub {
  padding: 0 16px 10px;
  font-size: 12px;
  color: #7c8fa1;
}

.contact-search {
  position: relative;
  display: flex;
  align-items: center;
  margin: 0 12px 10px;
  padding: 0 10px;
  height: 34px;
  border-radius: 8px;
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.25);
}

.contact-search .el-icon-search {
  color: #5a6b7d;
  font-size: 14px;
  flex-shrink: 0;
}

.contact-search-input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  background: transparent;
  height: 100%;
  padding: 0 8px;
  font-size: 13px;
  color: #e8edf2;
}

.contact-search-input::placeholder {
  color: #5a6b7d;
}

.contact-search-clear {
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: #5a6b7d;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  flex-shrink: 0;
}

.contact-search-clear:hover {
  color: #7c8fa1;
  background: rgba(64, 158, 255, 0.1);
}

.contact-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px 12px;
}

.contact-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  border: none;
  background: transparent;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
  text-align: left;
  transition: background 0.15s;
}

.contact-item:hover {
  background: rgba(124, 143, 161, 0.22);
}

.contact-item.active {
  background: rgba(64, 158, 255, 0.18);
}

.contact-avatar {
  width: 42px;
  height: 42px;
  border-radius: 6px;
  background: #409eff;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.contact-body {
  flex: 1;
  min-width: 0;
}

.contact-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.contact-name {
  font-size: 14px;
  font-weight: 500;
  color: #e8edf2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-time {
  font-size: 11px;
  color: #5a6b7d;
  flex-shrink: 0;
}

.contact-preview {
  display: block;
  font-size: 12px;
  color: #7c8fa1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-empty {
  padding: 32px 12px;
  text-align: center;
  font-size: 13px;
  color: #5a6b7d;
}

.session-pane {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #0f1a26;
}

.session-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.22);
  min-height: 64px;
}

.accept-hint {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: #8ea0b5;
}

.session-head-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  flex-wrap: wrap;
}

.role-cap-tip {
  font-size: 12px;
  color: #8ea0b5;
  line-height: 1.4;
}

.btn-open-collect {
  border: 1px solid rgba(64, 158, 255, 0.45);
  background: rgba(64, 158, 255, 0.12);
  color: #9ecbff;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 13px;
  cursor: pointer;
}

.btn-open-collect:hover {
  background: rgba(64, 158, 255, 0.22);
  color: #fff;
}

.pond-context-card {
  margin: 0 16px 0;
  padding: 12px 14px;
  border-bottom: 1px solid rgba(124, 143, 161, 0.18);
  background: rgba(15, 30, 48, 0.65);
}

.pond-context-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.pond-context-title {
  font-size: 13px;
  font-weight: 600;
  color: #e8edf2;
}

.pond-context-code {
  font-size: 12px;
  color: #7c8fa1;
}

.pond-context-meta {
  font-size: 12px;
  color: #9aa8b6;
}

.pond-context-missing {
  font-size: 11px;
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.12);
  border: 1px solid rgba(255, 107, 107, 0.35);
  border-radius: 4px;
  padding: 1px 6px;
}

.pond-context-warn {
  margin: 0 0 10px;
  padding: 8px 10px;
  font-size: 12px;
  line-height: 1.5;
  color: #ffb4b4;
  background: rgba(255, 107, 107, 0.08);
  border: 1px solid rgba(255, 107, 107, 0.25);
  border-radius: 6px;
}

.pond-context-refresh {
  margin-left: auto;
  border: none;
  background: transparent;
  color: #7c8fa1;
  cursor: pointer;
  padding: 4px;
}

.pond-context-refresh:hover {
  color: #409eff;
}

.pond-context-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.pond-context-item {
  min-width: 0;
  padding: 8px 10px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(124, 143, 161, 0.14);
}

.pci-label {
  font-size: 11px;
  color: #7c8fa1;
  margin-bottom: 4px;
}

.pci-body {
  font-size: 12px;
  color: #d5dee8;
  line-height: 1.45;
  word-break: break-word;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
.spinning {
  animation: spin 0.8s linear infinite;
}

.session-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #e8edf2;
}

.session-sub {
  margin: 4px 0 0;
  font-size: 12px;
  color: #7c8fa1;
}

.btn-conclusion {
  border: none;
  background: #409eff;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
}

.btn-conclusion:hover {
  background: #66b1ff;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background: #1a2a3a;
}

.chat-thread-empty {
  text-align: center;
  padding: 64px 16px;
  color: #7c8fa1;
  font-size: 13px;
}

.msg-time-sep {
  text-align: center;
  margin: 12px 0 16px;
  font-size: 12px;
  color: #b0b4bb;
}

.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 16px;
}

.msg-row.is-mine {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  background: #5a6b7d;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.msg-avatar.mine {
  background: #409eff;
}

.msg-bubble {
  max-width: 62%;
  padding: 10px 12px;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
  position: relative;
}

.is-peer .msg-bubble {
  background: #0f1a26;
  color: #e8edf2;
}

.is-mine .msg-bubble {
  background: rgba(64, 158, 255, 0.35);
  color: #e8edf2;
}

.chat-composer {
  border-top: 1px solid rgba(124, 143, 161, 0.25);
  background: #0a1520;
  padding: 0 12px 12px;
}

.composer-tools {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 6px 4px;
}

.tool-btn {
  width: 34px;
  height: 34px;
  border: none;
  background: transparent;
  color: #7c8fa1;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
}

.tool-btn:hover:not(:disabled) {
  background: rgba(124, 143, 161, 0.22);
}

.tool-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.tool-btn.active {
  background: rgba(64, 158, 255, 0.18);
  color: #165dff;
}

.hidden-file {
  display: none;
}

.recording-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 8px 6px;
  padding: 8px 12px;
  border-radius: 8px;
  background: rgba(245, 63, 63, 0.15);
  color: #f53f3f;
  font-size: 12px;
}

.recording-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f53f3f;
  animation: rec-blink 1s infinite;
}

@keyframes rec-blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.msg-bubble.type-image {
  padding: 6px;
  overflow: hidden;
}

.msg-image {
  display: block;
  max-width: 220px;
  max-height: 220px;
  border-radius: 6px;
  cursor: zoom-in;
}

.msg-file {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 160px;
  color: inherit;
  text-decoration: none;
}

.msg-file i {
  font-size: 28px;
  color: #7c8fa1;
}

.msg-file-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.msg-file-name {
  font-size: 13px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 180px;
}

.msg-file-size {
  font-size: 12px;
  color: #7c8fa1;
}

.msg-voice {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: none;
  background: transparent;
  color: inherit;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  min-width: 72px;
}

.msg-voice i {
  font-size: 16px;
}

.composer-input >>> .el-textarea__inner {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 4px 8px 8px;
  min-height: 88px !important;
  color: #e8edf2;
  font-size: 14px;
  line-height: 1.6;
}

.composer-input >>> .el-textarea__inner:focus {
  background: transparent;
  border: none;
  box-shadow: none;
}

.composer-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  padding: 0 4px;
}

.composer-tip {
  font-size: 12px;
  color: #5a6b7d;
}

.btn-send {
  min-width: 72px;
  height: 32px;
  border: none;
  border-radius: 4px;
  background: #409eff;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  padding: 0 16px;
}

.btn-send:hover:not(:disabled) {
  background: #66b1ff;
}

.btn-send:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #7c8fa1;
}

.empty-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #5a6b7d;
  margin-bottom: 12px;
}

@media (max-width: 900px) {
  .chat-panel {
    flex-direction: column;
    height: auto;
    min-height: 640px;
  }

  .contact-pane {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid rgba(124, 143, 161, 0.22);
    max-height: 220px;
  }
}

@media (max-width: 768px) {
  .diagnosis-body {
    padding: 0 16px 24px;
  }

  .task-panel {
    max-height: 360px;
  }

  .diagnosis-header {
    padding: 20px 16px 16px;
  }

  .header-title {
    font-size: 22px;
  }
}
</style>
