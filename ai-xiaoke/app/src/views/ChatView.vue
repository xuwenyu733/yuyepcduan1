<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { chatDirectStream, clearAuth, ensureLogin, getLlmStatus } from '../api'

const DIGITAL_HUMAN = import.meta.env.VITE_NUMBER_PERSON || ''
const CHAT_STORE_KEY = 'xiaoke_chat_v1'

const activeTab = ref('agent')
const routeHint = ref('query_analysis')
const input = ref('')
const messages = ref([])
const conversationId = ref('')
const thinking = ref(false)
const ready = ref(false)
const errorBanner = ref('')
const infoBanner = ref('')
const authUser = ref(null)
const listRef = ref(null)
const avatarFailed = ref(false)
const llmReady = ref(false)
const llmMeta = ref(null)
let abortCtrl = null
let persistTimer = null

function loadPersistedChat() {
  try {
    const raw = localStorage.getItem(CHAT_STORE_KEY)
    if (!raw) return
    const data = JSON.parse(raw)
    if (Array.isArray(data.messages) && data.messages.length) {
      messages.value = data.messages
    }
    if (data.conversationId) conversationId.value = String(data.conversationId)
  } catch {
    /* ignore corrupt cache */
  }
}

function persistChatSoon() {
  clearTimeout(persistTimer)
  persistTimer = setTimeout(() => {
    try {
      localStorage.setItem(
        CHAT_STORE_KEY,
        JSON.stringify({
          conversationId: conversationId.value || '',
          messages: messages.value.map((m) => ({
            role: m.role,
            text: m.text || '',
            sections: m.sections || null,
            rag: m.rag
              ? {
                  mode: m.rag.mode,
                  modeLabel: m.rag.modeLabel,
                  localHits: m.rag.localHits || [],
                  webHits: m.rag.webHits || [],
                }
              : null,
          })),
          savedAt: Date.now(),
        }),
      )
    } catch {
      /* quota / private mode */
    }
  }, 200)
}

watch([messages, conversationId], () => persistChatSoon(), { deep: true })

function messagePlain(m) {
  if (!m) return ''
  if (m.role === 'user') return String(m.text || '').trim()
  const combined = String(m.sections?.combined || '').trim()
  if (combined) return combined
  return String(m.text || '').trim()
}

/** 把正文里的 http(s) 链接变成可点击 <a> */
function linkifyHtml(text) {
  const s = String(text || '')
  if (!s) return ''
  const esc = s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  return esc.replace(
    /(https?:\/\/[^\s<>"'）】\]]+)/g,
    (url) =>
      `<a href="${url}" target="_blank" rel="noopener noreferrer" class="inline-link">${url}</a>`,
  )
}

function webHitsOf(m) {
  return (m?.rag?.webHits || []).filter((w) => w && (w.url || w.title))
}

const statusOk = computed(() => ready.value && llmReady.value && !errorBanner.value)
const showDigitalHuman = computed(() => DIGITAL_HUMAN && !avatarFailed.value)

const welcome = {
  title: '欢迎使用渔业科技智能助手！先读企业 Skill（MD），不足再联网补充。',
  items: ['水质监测数据分析', '养殖密度优化建议', '鱼类健康状态评估', '渔业设备管理指导'],
}

async function boot() {
  errorBanner.value = ''
  infoBanner.value = ''
  llmReady.value = false

  // Java 登录可选（有则显示昵称；没有也能直连大模型聊天）
  try {
    const auth = await ensureLogin()
    authUser.value = auth
  } catch {
    authUser.value = { nickname: '访客', username: 'guest' }
  }

  try {
    const st = await getLlmStatus()
    llmMeta.value = st
    ready.value = true
    // 无 Key 也可聊（本地 Skill 演示）；有 Key 走完整大模型
    llmReady.value = Boolean(st.chatReady || st.configured)
      const n = st.rag?.localRules ?? 0
      const web = st.rag?.webSearchEnabled ? '开（每次必搜）' : '关'
      if (st.configured) {
        errorBanner.value = ''
        infoBanner.value = `模型 ${st.providerHint}/${st.model}｜Skill ${n} 条｜联网 ${web}｜回答=Skill↑ 网络→ 综合↓｜对话会保留`
      } else {
        errorBanner.value = ''
        infoBanner.value = `演示模式｜Skill ${n} 条｜仍会尝试联网｜回答分三段｜对话会保留`
      }
  } catch (e) {
    ready.value = false
    errorBanner.value = `LLM 代理不可用：${e.message || e}（请确认 npm run dev 在运行）`
  }
}

function onAvatarError() {
  avatarFailed.value = true
}

function push(role, text, extra = {}) {
  messages.value.push({ role, text: text || '', ...extra })
  return messages.value.length - 1
}

async function scrollBottom() {
  await nextTick()
  const el = listRef.value
  if (el) el.scrollTop = el.scrollHeight
}

function clearChat() {
  messages.value = []
  conversationId.value = ''
  errorBanner.value = ''
  try {
    localStorage.removeItem(CHAT_STORE_KEY)
  } catch {
    /* ignore */
  }
}

async function send(text) {
  const content = String(text ?? input.value).trim()
  if (!content || thinking.value) return
  if (!llmReady.value) {
    await boot()
  }
  // 即使未配 Key，后端也支持演示模式，允许继续发送

  push('user', content)
  input.value = ''
  thinking.value = true
  await scrollBottom()

  // 发给模型的历史：用户原文 + AI 综合结论（不含刚推入的本轮）
  const history = messages.value
    .slice(0, -1)
    .map((m) => ({
      role: m.role === 'user' ? 'user' : 'assistant',
      content: messagePlain(m),
    }))
    .filter((h) => h.content)
    .slice(-16)

  const req = {
    conversation_id: conversationId.value || '',
    message: content,
    route_hint: routeHint.value,
    history,
  }

  let aiIndex = -1
  const ensureAi = (t = '') => {
    if (aiIndex >= 0) return aiIndex
    aiIndex = push('ai', t)
    return aiIndex
  }

  abortCtrl?.abort()
  abortCtrl = new AbortController()

  try {
    await chatDirectStream(req, {
      signal: abortCtrl.signal,
      onEvent: (evt) => {
        const e = evt || {}
        if (e.type === 'meta') {
          const cid = e.conversation_id || e.conversationId || ''
          if (cid) conversationId.value = cid
          const idx = ensureAi('')
          if (e.rag) messages.value[idx].rag = e.rag
          if (e.sections) messages.value[idx].sections = { ...e.sections }
        } else if (e.type === 'sections') {
          const idx = ensureAi('')
          messages.value[idx].sections = {
            ...(messages.value[idx].sections || {}),
            ...(e.sections || {}),
          }
        } else if (e.type === 'progress') {
          const idx = ensureAi('')
          const tip = String(e.content || '').trim()
          if (tip) messages.value[idx].progress = tip
        } else if (e.type === 'delta') {
          const idx = ensureAi('')
          // 流式内容写入综合结论
          if (!messages.value[idx].sections) {
            messages.value[idx].sections = { skill: '', web: '', combined: '' }
          }
          messages.value[idx].sections.combined =
            (messages.value[idx].sections.combined || '') + String(e.content || '')
          messages.value[idx].text = messages.value[idx].sections.combined
        } else if (e.type === 'final') {
          const idx = ensureAi('')
          if (e.sections) {
            messages.value[idx].sections = { ...e.sections }
            if (e.sections.combined) {
              messages.value[idx].text = e.sections.combined
            }
          }
          if (!messages.value[idx].text && !messages.value[idx].sections?.combined) {
            messages.value[idx].text = '暂时没有可展示的回复，请稍后重试。'
          }
        } else if (e.content) {
          const idx = ensureAi('')
          messages.value[idx].text = (messages.value[idx].text || '') + String(e.content)
        }
        scrollBottom()
      },
    })
  } catch (err) {
    if (err?.name === 'AbortError') return
    ensureAi('不好意思呢！服务器异常，请稍后再试。')
    errorBanner.value = `大模型调用失败：${err?.message || err}`
  } finally {
    thinking.value = false
    abortCtrl = null
    persistChatSoon()
    scrollBottom()
  }
}

function onKeydown(e) {
  if (e.key === 'Enter' && !e.ctrlKey && !e.shiftKey) {
    e.preventDefault()
    send()
  }
}

function quickAsk(text) {
  send(text)
}

function logoutLocal() {
  clearAuth()
  ready.value = false
  authUser.value = null
  boot()
}

onMounted(() => {
  loadPersistedChat()
  boot()
  nextTick(() => scrollBottom())
})
</script>

<template>
  <div class="chat-shell">
    <div class="chat-frame">
      <!-- 左侧 Tab -->
      <aside class="left-tabs">
        <button class="tab-item" :class="{ active: activeTab === 'agent' }" @click="activeTab = 'agent'">
          <span class="icon">✦</span>
          <span class="label">智能体</span>
        </button>
        <button class="tab-item" :class="{ active: activeTab === 'single' }" @click="activeTab = 'single'">
          <span class="icon">👤</span>
          <span class="label">单聊</span>
        </button>
        <button class="tab-item" :class="{ active: activeTab === 'group' }" @click="activeTab = 'group'">
          <span class="icon">👥</span>
          <span class="label">群聊</span>
        </button>
      </aside>

      <!-- 数字人区域 -->
      <section class="avatar-pane">
        <iframe
          v-if="showDigitalHuman"
          :src="DIGITAL_HUMAN"
          title="数字人"
          allow="autoplay; microphone"
          @error="onAvatarError"
        />
        <div v-else class="avatar-fallback">
          <div class="avatar-orb">
            <span>小科</span>
          </div>
          <p>智慧养殖 AI 助手</p>
          <small v-if="DIGITAL_HUMAN">数字人外链暂不可用，已显示本地形象</small>
          <small v-else>未配置数字人地址（VITE_NUMBER_PERSON）</small>
        </div>
      </section>

      <!-- 聊天主区 -->
      <section class="chat-main">
        <header class="chat-header">
          <h1>渔业科技智能助手</h1>
          <div class="header-actions">
            <span class="status-dot" :class="{ bad: !statusOk }" :title="ready ? '已登录' : '未就绪'" />
            <span style="font-size: 12px">{{ authUser?.nickname || authUser?.username || '未登录' }}</span>
            <button class="close-btn" title="重新登录" @click="logoutLocal">×</button>
          </div>
        </header>

        <div v-if="errorBanner" class="banner">{{ errorBanner }}</div>
        <div v-else-if="infoBanner" class="banner" :class="{ ok: llmReady }">
          {{ infoBanner }}
        </div>

        <div ref="listRef" class="chat-content">
          <div v-if="activeTab === 'agent'" class="message-group">
            <div class="message-bubble system">
              <div>{{ welcome.title }}</div>
              <ul>
                <li v-for="item in welcome.items" :key="item">
                  <a href="#" @click.prevent="quickAsk(item)">{{ item }}</a>
                </li>
              </ul>
            </div>
            <div class="message-bubble system">
              Hi~，我是小科。提问时会先读取项目里的原生 MD Skill；本地没有覆盖时，再结合网上资料回答。
            </div>

            <div
              v-for="(m, i) in messages"
              :key="i"
              class="message-bubble"
              :class="m.role === 'user' ? 'user' : 'system wide'"
            >
              <template v-if="m.role === 'user'">
                <div class="msg-text">{{ m.text }}</div>
              </template>
              <template v-else>
                <div v-if="m.rag" class="rag-bar">
                  <span class="rag-chip">{{ m.rag.modeLabel || m.rag.mode }}</span>
                  <span v-for="h in m.rag.localHits || []" :key="'L' + h.id" class="rag-chip local">
                    企业: {{ h.title }}
                  </span>
                  <span v-for="(w, wi) in m.rag.webHits || []" :key="'W' + wi" class="rag-chip web">
                    联网: {{ w.title || '资料' }}
                  </span>
                </div>
                <div v-if="m.progress && !(m.sections && (m.sections.skill || m.sections.combined))" class="progress-tip">
                  {{ m.progress }}
                </div>

                <div v-if="m.sections" class="answer-sections">
                  <section class="ans-card skill">
                    <h4>一、企业 Skill 依据</h4>
                    <div class="ans-body" v-html="linkifyHtml(m.sections.skill || '检索中…')" />
                  </section>
                  <section class="ans-card web">
                    <h4>二、网络检索资料</h4>
                    <ol v-if="webHitsOf(m).length" class="web-hit-list">
                      <li v-for="(w, wi) in webHitsOf(m)" :key="'web' + wi" class="web-hit">
                        <a
                          v-if="w.url"
                          class="web-hit-title"
                          :href="w.url"
                          target="_blank"
                          rel="noopener noreferrer"
                        >{{ w.title || '打开资料' }}</a>
                        <div v-else class="web-hit-title plain">{{ w.title || '未命名资料' }}</div>
                        <div v-if="w.snippet" class="web-hit-snip">{{ w.snippet }}</div>
                        <a
                          v-if="w.url"
                          class="web-hit-url"
                          :href="w.url"
                          target="_blank"
                          rel="noopener noreferrer"
                        >点击打开原文 →</a>
                      </li>
                    </ol>
                    <div
                      v-else
                      class="ans-body"
                      v-html="linkifyHtml(m.sections.web || '检索中…')"
                    />
                  </section>
                  <section class="ans-card combined">
                    <h4>三、综合结论（Skill + 网络）</h4>
                    <div
                      class="ans-body"
                      v-html="linkifyHtml(m.sections.combined || (thinking ? '生成中…' : '暂无'))"
                    />
                  </section>
                </div>
                <div v-else class="msg-text">{{ m.text }}</div>
              </template>
            </div>

            <div v-if="thinking" class="message-bubble system thinking">
              思考中
              <span class="thinking-dots"><span /><span /><span /></span>
            </div>
          </div>

          <div v-else class="message-bubble system">
            当前独立页仅实现「智能体」对话。单聊 / 群聊仍依赖原主站 IM。
          </div>
        </div>

        <footer class="chat-footer">
          <div class="route-hint-bar">
            <span class="route-hint-label">意图选择</span>
            <button
              type="button"
              class="route-hint-btn"
              :class="{ active: routeHint === 'query_analysis' }"
              @click="routeHint = 'query_analysis'"
            >
              查询分析
            </button>
            <button
              type="button"
              class="route-hint-btn"
              :class="{ active: routeHint === 'data_entry' }"
              @click="routeHint = 'data_entry'"
            >
              录入数据
            </button>
          </div>

          <div class="composer">
            <textarea
              v-model="input"
              rows="3"
              placeholder="请输入您的渔业相关问题(发送消息 enter, 换行 ctrl+enter)..."
              @keydown="onKeydown"
            />
            <div class="composer-actions">
              <button class="icon-btn" title="清空会话" @click="clearChat">↻</button>
              <button class="icon-btn send" :disabled="thinking || !input.trim()" title="发送" @click="send()">
                ➤
              </button>
            </div>
          </div>
        </footer>
      </section>
    </div>
  </div>
</template>

<style scoped>
a {
  color: #9fd0ff;
  text-decoration: none;
}
a:hover {
  text-decoration: underline;
}
.rag-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}
.rag-chip {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(74, 163, 255, 0.16);
  color: #9fd0ff;
  border: 1px solid rgba(74, 163, 255, 0.35);
}
.rag-chip.local {
  background: rgba(61, 214, 140, 0.14);
  color: #9be7c0;
  border-color: rgba(61, 214, 140, 0.35);
}
.rag-chip.web {
  background: rgba(255, 193, 77, 0.12);
  color: #ffd27a;
  border-color: rgba(255, 193, 77, 0.35);
}
.progress-tip {
  color: #8aa3c7;
  font-size: 12px;
  margin-bottom: 6px;
}
.msg-text {
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
  max-width: 100%;
}
.rag-bar {
  max-width: 100%;
  margin-bottom: 8px;
}
.answer-sections {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.ans-card {
  border-radius: 12px;
  padding: 10px 12px;
  border: 1px solid rgba(120, 170, 230, 0.22);
  background: rgba(0, 0, 0, 0.18);
}
.ans-card h4 {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.3px;
}
.ans-card.skill {
  border-color: rgba(61, 214, 140, 0.35);
}
.ans-card.skill h4 {
  color: #9be7c0;
}
.ans-card.web {
  border-color: rgba(255, 193, 77, 0.35);
}
.ans-card.web h4 {
  color: #ffd27a;
}
.ans-card.combined {
  border-color: rgba(74, 163, 255, 0.45);
  background: rgba(74, 163, 255, 0.08);
}
.ans-card.combined h4 {
  color: #7ec2ff;
}
.ans-body {
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
  font-size: 13px;
  line-height: 1.55;
  color: #e8f1ff;
}
.ans-body :deep(.inline-link),
.web-hit-title,
.web-hit-url {
  color: #ffd27a;
  text-decoration: underline;
  word-break: break-all;
}
.web-hit-title {
  font-weight: 600;
  font-size: 13px;
  line-height: 1.45;
  display: inline-block;
}
.web-hit-title.plain {
  color: #e8f1ff;
  text-decoration: none;
  font-weight: 600;
}
.web-hit-list {
  margin: 0;
  padding-left: 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.web-hit {
  color: #e8f1ff;
}
.web-hit-snip {
  margin-top: 4px;
  font-size: 12px;
  line-height: 1.5;
  color: #c5d7f0;
  word-break: break-word;
}
.web-hit-url {
  display: inline-block;
  margin-top: 4px;
  font-size: 12px;
}
</style>
