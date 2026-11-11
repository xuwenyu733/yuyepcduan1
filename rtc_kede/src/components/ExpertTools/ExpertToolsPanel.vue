<template>
  <div class="expert-tools-panel">
    <div class="expert-tools-panel__hint" v-if="meetingNum">
      会码 {{ meetingNum }} · 双击 AI 字幕记录可选中，点「上传」写入会议记录
    </div>

    <section class="card">
      <header class="card-hd">会议记录</header>
      <textarea
        v-model="meetingRecordContent"
        class="card-ta"
        placeholder="双击选中语音转文字后点「上传」，内容会出现在这里"
      />
      <button
        class="card-btn"
        :disabled="!isHost || summaryLoading"
        :title="isHost ? '' : '仅房主可获取纪要'"
        @click="getMeetSummary"
      >
        {{ summaryLoading ? '系统总结中…' : '获取纪要及待办' }}
      </button>
      <p v-if="!isHost" class="hint">仅房主可发起纪要生成与云端录制</p>
    </section>

    <section class="card">
      <header class="card-hd">会议纪要及待办</header>
      <textarea
        v-model="meetingPointAndTodo"
        class="card-ta"
        placeholder="纪要与待办"
      />
      <button
        class="card-btn accent"
        :disabled="!canSubmitKb"
        :title="canSubmitKb ? '' : '仅专业人员可提交知识库'"
        @click="addMeetRecord"
      >
        提交知识库
      </button>
      <p v-if="!canSubmitKb" class="hint">提交知识库需专业人员权限</p>
    </section>

    <section class="card">
      <header class="card-hd">结果反馈</header>
      <textarea
        v-model="toDoFeedback"
        class="card-ta"
        placeholder="上一场待办的落实反馈"
      />
      <button class="card-btn" @click="updateFeedback">上传提交</button>
    </section>

    <p v-if="toast" class="toast">{{ toast }}</p>
    <div class="expert-tools-panel__end" aria-hidden="true" />
  </div>
</template>

<script setup lang="ts">
import { useExpertMeetingState } from '@/composables/useExpertMeetingState'

const {
  meetingRecordContent,
  meetingPointAndTodo,
  toDoFeedback,
  summaryLoading,
  toast,
  meetingNum,
  isHost,
  canSubmitKb,
  getMeetSummary,
  addMeetRecord,
  updateFeedback,
} = useExpertMeetingState()
</script>

<style scoped lang="scss">
.expert-tools-panel {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
  min-height: 0;
  padding: 12px 14px 0;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
  color: #eef4fc;
  background: transparent;
}
.expert-tools-panel__hint {
  flex-shrink: 0;
  font-size: 12px;
  line-height: 1.45;
  color: rgba(238, 244, 252, 0.55);
}
.card {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  border-radius: 10px;
  background: rgba(12, 28, 36, 0.85);
  border: 1px solid rgba(76, 145, 101, 0.22);
}
.card-hd {
  font-size: 13px;
  font-weight: 600;
  color: #a8e6c1;
}
.card-ta {
  width: 100%;
  box-sizing: border-box;
  height: clamp(72px, 14vh, 140px);
  min-height: 72px;
  max-height: 240px;
  resize: vertical;
  border: 1px solid rgba(76, 145, 101, 0.28);
  border-radius: 8px;
  background: rgba(4, 12, 18, 0.9);
  color: #eef4fc;
  padding: 8px 10px;
  font-size: 12px;
  line-height: 1.5;
  outline: none;
}
.card-ta:focus {
  border-color: #4c9165;
}
.card-btn {
  align-self: flex-end;
  border: none;
  border-radius: 6px;
  padding: 6px 14px;
  font-size: 12px;
  cursor: pointer;
  color: #0a1520;
  background: #4c9165;
  font-weight: 600;
}
.card-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
.card-btn.accent {
  background: linear-gradient(90deg, #3d8b5c, #5cb87a);
}
.hint {
  margin: 0;
  font-size: 11px;
  color: rgba(238, 244, 252, 0.45);
}
.toast {
  margin: 0;
  padding: 8px 10px;
  border-radius: 8px;
  background: rgba(76, 145, 101, 0.2);
  border: 1px solid rgba(76, 145, 101, 0.45);
  font-size: 12px;
  color: #c8f0d6;
}
.expert-tools-panel__end {
  height: max(28px, env(safe-area-inset-bottom, 0px));
  flex-shrink: 0;
}
</style>
