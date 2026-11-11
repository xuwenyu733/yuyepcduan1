<template>
  <div class="summary-page">
    <header class="hd">
      <div>
        <h1>科德专家会议 · 会议总结</h1>
        <p class="sub">会议码 {{ meetingNum || '—' }} · 所有人可填写并提交</p>
      </div>
      <button class="ghost" @click="goHome">返回首页</button>
    </header>

    <section class="card">
      <h2>会议纪要（可编辑）</h2>
      <textarea v-model="minutes" rows="10" placeholder="可粘贴语音转文字或小科生成内容" />
    </section>

    <section class="card">
      <h2>个人总结</h2>
      <textarea v-model="personalSummary" rows="6" placeholder="请写下本次会议的个人总结…" />
    </section>

    <div class="actions">
      <button class="primary" :disabled="submitting" @click="submitSummary">
        {{ submitting ? '提交中…' : '提交总结' }}
      </button>
      <button class="ghost" @click="goHome">稍后填写</button>
    </div>
    <p v-if="toast" class="toast">{{ toast }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  callParentApi,
  loadKedeUser,
  loadMeetingMeta,
  loadSelectedAsrText,
  resetMeetingSession,
} from '@/utils/kedeMeetingBridge'

const router = useRouter()
const route = useRoute()
const meetingNum = ref('')
const minutes = ref('')
const personalSummary = ref('')
const toast = ref('')
const submitting = ref(false)

onMounted(() => {
  const meta = loadMeetingMeta()
  const q = String(route.query.roomId || route.query.meetingNum || '').trim()
  meetingNum.value = q || meta.meetingNum || ''
  const sameMeeting = !meta.meetingNum || !meetingNum.value
    || String(meta.meetingNum) === String(meetingNum.value)
  const point = sameMeeting
    ? [meta.meetingPoint, meta.meetToDo].filter(Boolean).join('\n\n')
    : ''
  const asr = sameMeeting ? loadSelectedAsrText(meetingNum.value) : ''
  minutes.value = point || asr || ''
})

function showToast(msg: string) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 3200)
}

function authHeaders() {
  const user = loadKedeUser()
  const token = String(user.accessToken || localStorage.getItem('ACCESS_TOKEN') || '').replace(/^Bearer\s+/i, '')
  const tenantId = String(user.tenantId || localStorage.getItem('TENANT_ID') || '1')
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'tenant-id': tenantId,
  }
  if (token) headers.Authorization = `Bearer ${token}`
  return { headers, user }
}

async function postAdminApi(path: string, body: Record<string, any>) {
  const { headers } = authHeaders()
  const urls = [
    `/proxy-api/admin-api${path}`,
    `/admin-api${path}`,
  ]
  let lastError: Error | null = null
  for (const url of urls) {
    try {
      const res = await fetch(url, {
        method: 'POST',
        headers,
        body: JSON.stringify(body),
      })
      const data = await res.json().catch(() => ({}))
      if (res.ok && (data.code === 0 || data.code === 200)) return data
      lastError = new Error(data.msg || `接口失败(${res.status})`)
    } catch (e: any) {
      lastError = e instanceof Error ? e : new Error(String(e))
    }
  }
  throw lastError || new Error('接口失败')
}

async function submitDirectly(content: string) {
  const { user } = authHeaders()
  const num = meetingNum.value
  if (!num) throw new Error('会议号缺失')
  let meetingId = loadMeetingMeta().meetingId
  if (!meetingId) {
    try {
      const join = await postAdminApi('/fish/zk-meeting/receiveMsg', {
        receiveType: 'son_send_to_parent_meeting_jion_meet',
        meetingNum: num,
        userId: user.userId,
        deptId: user.deptId,
        tenantId: user.tenantId,
        userName: user.nickname || user.userName,
        version: Date.now(),
      })
      meetingId = join?.data?.id
    } catch {
      /* meetingId 可空，仍尝试创建记录 */
    }
  }
  await postAdminApi('/fish/zk-meeting-record/create', {
    meetingId: meetingId || undefined,
    meetingName: num,
    meetingNum: num,
    meetingPoint: minutes.value,
    meetToDo: personalSummary.value,
    meetingContent: content,
    meetingRecordContent: content,
    meetingRecordName: `会议总结-${num}`,
    userId: user.userId,
    deptId: user.deptId,
    tenantId: user.tenantId,
    userName: user.nickname || user.userName,
    status: '0',
  })
}

async function submitSummary() {
  const content = `${minutes.value}\n\n【个人总结】\n${personalSummary.value}`.trim()
  if (!content) {
    showToast('请填写内容后再提交')
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    try {
      await callParentApi('createRecord', {
        meetingNum: meetingNum.value,
        meetingName: meetingNum.value,
        meetingPoint: minutes.value,
        meetToDo: personalSummary.value,
        meetingContent: content,
      }, 8000)
    } catch {
      await submitDirectly(content)
    }
    showToast('总结已提交')
    try {
      resetMeetingSession()
    } catch {
      /* ignore */
    }
  } catch (e: any) {
    try {
      const list = JSON.parse(localStorage.getItem('kede-meeting-summaries') || '[]')
      list.unshift({ at: Date.now(), meetingNum: meetingNum.value, content })
      localStorage.setItem('kede-meeting-summaries', JSON.stringify(list.slice(0, 30)))
    } catch {
      /* ignore */
    }
    showToast(e?.message || '提交失败，已尝试本地保存')
  } finally {
    submitting.value = false
  }
}

function goHome() {
  router.replace('/home')
}
</script>

<style scoped lang="scss">
.summary-page {
  min-height: 100%;
  box-sizing: border-box;
  padding: 28px 32px 48px;
  background:
    radial-gradient(ellipse at top right, rgba(76, 145, 101, 0.18), transparent 45%),
    linear-gradient(160deg, #0a1520, #071018 60%, #050d12);
  color: #eef4fc;
}
.hd {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  h1 {
    margin: 0 0 6px;
    font-size: 22px;
    color: #7dcea0;
  }
  .sub {
    margin: 0;
    color: rgba(238, 244, 252, 0.55);
    font-size: 13px;
  }
}
.card {
  margin-bottom: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: rgba(10, 24, 32, 0.85);
  border: 1px solid rgba(76, 145, 101, 0.28);
  h2 {
    margin: 0 0 10px;
    font-size: 14px;
    color: #a8e6c1;
  }
  textarea {
    width: 100%;
    box-sizing: border-box;
    border-radius: 8px;
    border: 1px solid rgba(76, 145, 101, 0.3);
    background: #050d12;
    color: #eef4fc;
    padding: 10px 12px;
    font-size: 13px;
    line-height: 1.55;
    resize: vertical;
  }
}
.actions {
  display: flex;
  gap: 10px;
}
.primary, .ghost {
  border-radius: 8px;
  padding: 8px 18px;
  font-weight: 600;
  cursor: pointer;
}
.primary {
  border: none;
  background: #4c9165;
  color: #0a1520;
}
.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.ghost {
  border: 1px solid rgba(76, 145, 101, 0.45);
  background: transparent;
  color: #a8e6c1;
}
.toast {
  margin-top: 14px;
  color: #c8f0d6;
}
</style>
