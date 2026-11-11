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
      <p class="header-desc">工人与专家协同诊断，实时沟通并输出诊断结论与治疗方案</p>
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
            <div class="task-meta">{{ item.reporter || '—' }} · {{ formatTime(item) }}</div>
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
              <button
                v-if="current.status !== 'resolved'"
                type="button"
                class="btn-conclusion"
                @click="openConclusionDialog"
              >填写诊断结论</button>
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
                        v-else-if="item.data.msgType === 'voice' && item.data.fileUrl"
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
              <div v-if="recording" class="recording-bar">
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
} from '../api'
import MenuToggle from '../components/MenuToggle.vue'
import HeaderAuth from '../components/HeaderAuth.vue'
import ChatSocket from '@/utils/chatSocket'
import { getUser } from '@/utils/disease_auth'

export default {
  name: 'DiagnosisChat',
  components: { MenuToggle, HeaderAuth },
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
      chatSocket: null,
      offSocket: null
    }
  },
  computed: {
    me() {
      return getUser() || {}
    },
    activeTabLabel() {
      const tab = this.listTabs.find(t => t.key === this.listTab)
      return tab ? tab.label : ''
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
    const caseId = this.$route.query.caseId
    if (caseId) this.pendingCaseId = caseId
    this.setupSocket()
    this.init()
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
    async init() {
      try {
        await Promise.all([this.loadCases(), this.loadContacts()])
        if (this.pendingCaseId) {
          const found = this.cases.find(c => c.id === this.pendingCaseId)
          if (found) {
            this.selectCase(found)
            return
          }
        }
        if (this.cases.length) this.selectCase(this.cases[0])
      } catch (e) {
        this.$message.error('加载诊断任务失败，请确认后端服务已启动（端口 3011）')
      }
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
      this.ensureChatTarget()
      if (this.chatSocket) {
        this.chatSocket.join(item.id, this.currentSender())
      }
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
      this.conclusionForm = {
        suspectedDisease: this.current.suspectedDisease || '',
        treatment: this.current.treatment || '',
        conclusion: this.current.conclusion || ''
      }
      this.conclusionVisible = true
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
  background: #f7f8fa;
  display: flex;
  flex-direction: column;
  position: relative;
}

.diagnosis-header {
  background: #fff;
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
  background: #fff;
  border: 1px solid #eef0f3;
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
  color: #1d2129;
}

.panel-desc {
  margin: 0;
  font-size: 14px;
  color: #86909c;
}

.filter-tabs {
  display: flex;
  margin: 16px 20px 12px;
  background: #eceef2;
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
  color: #86909c;
  cursor: pointer;
  transition: all 0.15s;
}

.filter-tab.active {
  background: #fff;
  color: #1d2129;
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
  border: 1px solid #eef0f3;
  border-radius: 10px;
  cursor: pointer;
  margin-bottom: 10px;
  transition: border-color 0.15s, background 0.15s;
}

.task-card:hover {
  border-color: #d0d3d9;
}

.task-card.active {
  background: #f0f7ff;
  border-color: #91caff;
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
  color: #1d2129;
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
  background: #f2f3f5;
  color: #86909c;
}

.task-status.diagnosing,
.task-status.treating {
  background: #e8f3ff;
  color: #1890ff;
}

.task-status.resolved {
  background: #f6ffed;
  color: #52c41a;
}

.task-detail {
  font-size: 15px;
  font-weight: 500;
  color: #1d2129;
  margin-bottom: 6px;
}

.task-meta {
  font-size: 13px;
  color: #86909c;
}

.list-empty {
  text-align: center;
  padding: 48px 16px;
  color: #86909c;
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
  background: #c9cdd4;
}

.contact-pane {
  width: 260px;
  flex-shrink: 0;
  border-right: 1px solid #eef0f3;
  background: #f7f8fa;
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
  color: #1d2129;
}

.contact-pane-sub {
  padding: 0 16px 10px;
  font-size: 12px;
  color: #86909c;
}

.contact-search {
  position: relative;
  display: flex;
  align-items: center;
  margin: 0 12px 10px;
  padding: 0 10px;
  height: 34px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #e5e6eb;
}

.contact-search .el-icon-search {
  color: #c9cdd4;
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
  color: #1d2129;
}

.contact-search-input::placeholder {
  color: #c9cdd4;
}

.contact-search-clear {
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: #c9cdd4;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  flex-shrink: 0;
}

.contact-search-clear:hover {
  color: #86909c;
  background: #f2f3f5;
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
  background: #eef0f3;
}

.contact-item.active {
  background: #e8f3ff;
}

.contact-avatar {
  width: 42px;
  height: 42px;
  border-radius: 6px;
  background: #1890ff;
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
  color: #1d2129;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-time {
  font-size: 11px;
  color: #c9cdd4;
  flex-shrink: 0;
}

.contact-preview {
  display: block;
  font-size: 12px;
  color: #86909c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-empty {
  padding: 32px 12px;
  text-align: center;
  font-size: 13px;
  color: #c9cdd4;
}

.session-pane {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.session-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid #eef0f3;
  min-height: 64px;
}

.session-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
}

.session-sub {
  margin: 4px 0 0;
  font-size: 12px;
  color: #86909c;
}

.btn-conclusion {
  border: none;
  background: #1d2129;
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
  background: #4e5969;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background: #ededed;
}

.chat-thread-empty {
  text-align: center;
  padding: 64px 16px;
  color: #86909c;
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
  background: #c9cdd4;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.msg-avatar.mine {
  background: #1890ff;
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
  background: #fff;
  color: #1d2129;
}

.is-mine .msg-bubble {
  background: #95ec69;
  color: #1d2129;
}

.chat-composer {
  border-top: 1px solid #e5e6eb;
  background: #f7f8fa;
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
  color: #4e5969;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
}

.tool-btn:hover:not(:disabled) {
  background: #eef0f3;
}

.tool-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.tool-btn.active {
  background: #e8f3ff;
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
  background: #fff1f0;
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
  color: #4e5969;
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
  color: #86909c;
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
  color: #1d2129;
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
  color: #c9cdd4;
}

.btn-send {
  min-width: 72px;
  height: 32px;
  border: none;
  border-radius: 4px;
  background: #1890ff;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  padding: 0 16px;
}

.btn-send:hover:not(:disabled) {
  background: #40a9ff;
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
  color: #86909c;
}

.empty-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #f2f3f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #c9cdd4;
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
    border-bottom: 1px solid #eef0f3;
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
