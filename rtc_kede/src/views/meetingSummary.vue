<template>
  <div class="summary-page">
    <header class="hd">
      <div>
        <h1>科德专家会议 · 会议总结</h1>
        <p class="sub">会议码 {{ meetingNum || '未记录' }} · 所有人可填写并提交</p>
      </div>
      <button class="ghost" @click="returnToExpertMeeting">返回专家会议</button>
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
      <button class="primary" @click="submitSummary">提交总结</button>
      <button class="ghost" @click="returnToExpertMeeting">稍后填写</button>
    </div>
    <p v-if="toast" class="toast">{{ toast }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  callParentApi,
  loadMeetingMeta,
  loadSelectedAsrText,
  saveMeetingMeta,
} from '@/utils/kedeMeetingBridge'

const route = useRoute()
const router = useRouter()
const meetingNum = ref('')
const minutes = ref('')
const personalSummary = ref('')
const toast = ref('')

onMounted(() => {
  const meta = loadMeetingMeta()
  const fromQuery = String(route.query.meetingNum || route.query.roomId || '').trim()
  meetingNum.value = fromQuery || meta.meetingNum || ''
  if (meetingNum.value) {
    saveMeetingMeta({ meetingNum: meetingNum.value })
  }
  const point = [meta.meetingPoint, meta.meetToDo].filter(Boolean).join('\n\n')
  minutes.value = point || loadSelectedAsrText() || ''
})

function showToast(msg: string) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2600)
}

/** 通知养殖智慧平台并关闭会议窗，回到「科德专家会议」页 */
function returnToExpertMeeting() {
  try {
    const w = (window.opener && !window.opener.closed)
      ? window.opener
      : (window.parent && window.parent !== window ? window.parent : null)
    if (w) {
      w.postMessage({
        type: 'rtc_kede_summary_done',
        meetingNum: meetingNum.value,
      }, '*')
      try { w.focus() } catch (e0) { /* ignore */ }
    }
  } catch (e) { /* ignore */ }

  // 独立会议窗：关闭后用户回到渔业平台标签
  try {
    if (window.opener && !window.opener.closed) {
      window.close()
      return
    }
  } catch (e1) { /* ignore */ }

  // 仍在 iframe 内：留在父页专家会议，子页只回大厅
  router.replace('/home')
}

async function submitSummary() {
  const content = `${minutes.value}\n\n【个人总结】\n${personalSummary.value}`.trim()
  if (!content) {
    showToast('请填写内容后再提交')
    return
  }
  try {
    await callParentApi('createRecord', {
      meetingNum: meetingNum.value,
      meetingName: meetingNum.value,
      meetingPoint: minutes.value,
      meetToDo: personalSummary.value,
      meetingContent: content,
    })
    showToast('总结已提交')
    window.setTimeout(() => returnToExpertMeeting(), 400)
  } catch (e: any) {
    try {
      const list = JSON.parse(localStorage.getItem('kede-meeting-summaries') || '[]')
      list.unshift({ at: Date.now(), meetingNum: meetingNum.value, content })
      localStorage.setItem('kede-meeting-summaries', JSON.stringify(list.slice(0, 30)))
    } catch (err) { /* ignore */ }
    showToast(e?.message === 'standalone_no_parent' ? '已本地保存总结' : (e?.message || '提交失败，已尝试本地保存'))
    // 本地保存也视为完成，同样返回专家会议
    if (e?.message === 'standalone_no_parent') {
      window.setTimeout(() => returnToExpertMeeting(), 600)
    }
  }
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
