/**
 * 站内信轻重缓急：紧急 / 优先 / 日常
 * 对应《渔业页面分布》通知：分栏分类 + 不同提示音
 */

export const NOTIFY_URGENCY = {
  URGENT: 'urgent',     // 紧急
  PRIORITY: 'priority', // 优先
  NORMAL: 'normal',     // 日常
}

export const NOTIFY_URGENCY_OPTIONS = [
  { value: NOTIFY_URGENCY.URGENT, label: '紧急', type: 'danger' },
  { value: NOTIFY_URGENCY.PRIORITY, label: '优先', type: 'warning' },
  { value: NOTIFY_URGENCY.NORMAL, label: '日常', type: 'info' },
]

export function getUrgencyLabel(level) {
  const hit = NOTIFY_URGENCY_OPTIONS.find(o => o.value === level)
  return hit ? hit.label : '日常'
}

export function getUrgencyTagType(level) {
  const hit = NOTIFY_URGENCY_OPTIONS.find(o => o.value === level)
  return hit ? hit.type : 'info'
}

/**
 * 从消息字段推断轻重缓急
 * 优先读后端字段，其次模板类型，再次内容关键词
 */
export function resolveNotifyUrgency(row = {}) {
  const raw = row.urgency || row.priority || row.level || row.dangerRating || row.templateUrgency
  if (raw != null && raw !== '') {
    const s = String(raw).toLowerCase()
    if (['urgent', 'emergency', 'critical', '1', '紧急', '高'].includes(s)) return NOTIFY_URGENCY.URGENT
    if (['priority', 'high', 'warn', 'warning', '2', '优先', '中'].includes(s)) return NOTIFY_URGENCY.PRIORITY
    if (['normal', 'low', 'daily', '3', '日常', '普通', '低'].includes(s)) return NOTIFY_URGENCY.NORMAL
  }

  // 模板类型常见：1 通知 / 2 公告；也可按编码关键字
  const type = row.templateType != null ? String(row.templateType) : ''
  const code = String(row.templateCode || row.code || '').toLowerCase()
  const text = `${row.templateNickname || ''} ${row.templateContent || row.content || row.title || ''}`

  if (/紧急|告警|报警|超限|严重|critical|urgent|alarm/i.test(text) || /urgent|alarm|critical/.test(code)) {
    return NOTIFY_URGENCY.URGENT
  }
  if (/优先|预警|注意|逾期|超时|考核|待评|priority|warn/i.test(text) || /priority|warn|oa.?eval|evaluation/.test(code)) {
    return NOTIFY_URGENCY.PRIORITY
  }
  // 类型字典值若本身带语义
  if (type === '1' && /通知/.test(text) === false) {
    // 保持默认可
  }
  return NOTIFY_URGENCY.NORMAL
}

export function enrichNotifyList(list = []) {
  return (list || []).map(row => Object.assign({}, row, {
    urgencyLevel: resolveNotifyUrgency(row),
  }))
}

export function filterByUrgency(list, level) {
  if (!level || level === 'all') return list || []
  return (list || []).filter(row => (row.urgencyLevel || resolveNotifyUrgency(row)) === level)
}

export function countByUrgency(list = []) {
  const enriched = enrichNotifyList(list)
  return {
    all: enriched.length,
    urgent: enriched.filter(r => r.urgencyLevel === NOTIFY_URGENCY.URGENT).length,
    priority: enriched.filter(r => r.urgencyLevel === NOTIFY_URGENCY.PRIORITY).length,
    normal: enriched.filter(r => r.urgencyLevel === NOTIFY_URGENCY.NORMAL).length,
  }
}

let audioCtx = null
function getAudioCtx() {
  if (typeof window === 'undefined') return null
  const Ctx = window.AudioContext || window.webkitAudioContext
  if (!Ctx) return null
  if (!audioCtx) audioCtx = new Ctx()
  if (audioCtx.state === 'suspended') {
    audioCtx.resume().catch(() => {})
  }
  return audioCtx
}

function beep(ctx, { freq = 880, duration = 0.18, type = 'sine', gain = 0.08, startAt = 0 }) {
  const osc = ctx.createOscillator()
  const g = ctx.createGain()
  osc.type = type
  osc.frequency.value = freq
  g.gain.value = gain
  osc.connect(g)
  g.connect(ctx.destination)
  const t0 = ctx.currentTime + startAt
  g.gain.setValueAtTime(gain, t0)
  g.gain.exponentialRampToValueAtTime(0.001, t0 + duration)
  osc.start(t0)
  osc.stop(t0 + duration + 0.02)
}

/** 按轻重缓急播放不同提示音 */
export function playNotifySound(level = NOTIFY_URGENCY.NORMAL) {
  try {
    const ctx = getAudioCtx()
    if (!ctx) return
    if (level === NOTIFY_URGENCY.URGENT) {
      // 急促三声高音
      beep(ctx, { freq: 980, duration: 0.12, type: 'square', gain: 0.09, startAt: 0 })
      beep(ctx, { freq: 980, duration: 0.12, type: 'square', gain: 0.09, startAt: 0.16 })
      beep(ctx, { freq: 1200, duration: 0.18, type: 'square', gain: 0.1, startAt: 0.32 })
    } else if (level === NOTIFY_URGENCY.PRIORITY) {
      // 两声中音
      beep(ctx, { freq: 740, duration: 0.16, type: 'triangle', gain: 0.08, startAt: 0 })
      beep(ctx, { freq: 880, duration: 0.2, type: 'triangle', gain: 0.08, startAt: 0.22 })
    } else {
      // 轻柔一声
      beep(ctx, { freq: 620, duration: 0.22, type: 'sine', gain: 0.06, startAt: 0 })
    }
  } catch (e) {
    console.warn('playNotifySound failed', e)
  }
}

/** 取列表中最高紧急度 */
export function maxUrgency(list = []) {
  const levels = enrichNotifyList(list).map(r => r.urgencyLevel)
  if (levels.includes(NOTIFY_URGENCY.URGENT)) return NOTIFY_URGENCY.URGENT
  if (levels.includes(NOTIFY_URGENCY.PRIORITY)) return NOTIFY_URGENCY.PRIORITY
  return NOTIFY_URGENCY.NORMAL
}
