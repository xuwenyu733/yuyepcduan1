/** 知识库表单/列表边界处理（浏览页与管理页共用） */

const RISK_LEVELS = ['low', 'medium', 'high', 'critical']
const MAX = {
  name: 64,
  category: 32,
  medicine: 120,
  description: 500,
  treatment: 800,
  prevention: 800,
  symptomsText: 300,
  symptomItem: 40,
  symptomCount: 20
}

export const KNOWLEDGE_REV_KEY = 'fish_knowledge_rev'

export function bumpKnowledgeRev() {
  try {
    localStorage.setItem(KNOWLEDGE_REV_KEY, String(Date.now()))
  } catch (e) {}
}

export function readKnowledgeRev() {
  try {
    return localStorage.getItem(KNOWLEDGE_REV_KEY) || ''
  } catch (e) {
    return ''
  }
}

export function normalizeSymptomsInput(input) {
  const raw = Array.isArray(input)
    ? input
    : String(input || '').split(/[,，、;；\n]/)
  const seen = new Set()
  const out = []
  for (const part of raw) {
    const s = String(part || '').trim().slice(0, MAX.symptomItem)
    if (!s) continue
    const key = s.toLowerCase()
    if (seen.has(key)) continue
    seen.add(key)
    out.push(s)
    if (out.length >= MAX.symptomCount) break
  }
  return out
}

export function clip(str, max) {
  return String(str == null ? '' : str).trim().slice(0, max)
}

export function normalizeRiskLevel(level) {
  const v = String(level || '').trim().toLowerCase()
  return RISK_LEVELS.includes(v) ? v : 'medium'
}

/**
 * 规范化并校验提交载荷
 * @returns {{ ok: true, payload: object } | { ok: false, message: string }}
 */
export function buildKnowledgePayload(form = {}, opts = {}) {
  const name = clip(form.name, MAX.name)
  const category = clip(form.category, MAX.category)
  if (!name) return { ok: false, message: '请填写病害名称' }
  if (!category) return { ok: false, message: '请填写分类' }

  const symptoms = normalizeSymptomsInput(form.symptoms != null ? form.symptoms : form.symptomsText)
  const payload = {
    name,
    category,
    symptoms,
    description: clip(form.description, MAX.description),
    medicine: clip(form.medicine, MAX.medicine),
    treatment: clip(form.treatment, MAX.treatment),
    prevention: clip(form.prevention, MAX.prevention),
    riskLevel: normalizeRiskLevel(form.riskLevel)
  }

  if (opts.checkDuplicate && Array.isArray(opts.list)) {
    const selfId = opts.excludeId || ''
    const dup = opts.list.find(
      item =>
        item &&
        item.id !== selfId &&
        String(item.name || '').trim().toLowerCase() === name.toLowerCase()
    )
    if (dup) return { ok: false, message: `已存在同名知识「${dup.name}」` }
  }

  return { ok: true, payload }
}

export function nameRules() {
  return [
    { required: true, message: '请输入病害名称', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        const v = String(value || '').trim()
        if (!v) return cb(new Error('请输入病害名称'))
        if (v.length > MAX.name) return cb(new Error(`名称不超过 ${MAX.name} 字`))
        cb()
      },
      trigger: 'blur'
    }
  ]
}

export function categoryRules() {
  return [
    { required: true, message: '请输入分类', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        const v = String(value || '').trim()
        if (!v) return cb(new Error('请输入分类'))
        if (v.length > MAX.category) return cb(new Error(`分类不超过 ${MAX.category} 字`))
        cb()
      },
      trigger: 'blur'
    }
  ]
}

export { MAX, RISK_LEVELS }
