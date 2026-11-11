import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const RULES_DIR = path.join(__dirname, 'rules')
const SKILLS_DIR = path.join(__dirname, 'skills')

/** 命中某条经验至少几个关键词才引用（樊总要求） */
export const MIN_KEYWORD_HITS = 2

function parseFrontmatter(raw) {
  const m = raw.match(/^---\r?\n([\s\S]*?)\r?\n---\r?\n([\s\S]*)$/)
  if (!m) {
    return { meta: {}, body: raw }
  }
  const meta = {}
  for (const line of m[1].split(/\r?\n/)) {
    const i = line.indexOf(':')
    if (i < 0) continue
    const key = line.slice(0, i).trim()
    let val = line.slice(i + 1).trim()
    if (key === 'weight') val = Number(val) || 1
    if (key === 'tags') val = val.split(/[,，、\s]+/).filter(Boolean)
    meta[key] = val
  }
  return { meta, body: m[2].trim() }
}

function tokenize(text) {
  const s = String(text || '').toLowerCase()
  const cn = s.match(/[\u4e00-\u9fff]{2,}/g) || []
  const en = s.match(/[a-z0-9_]{2,}/g) || []
  const extras = []
  const keys = [
    '水质',
    '溶氧',
    '氨氮',
    '亚硝酸盐',
    '密度',
    '投喂',
    '饵料',
    '病害',
    '浮头',
    '死鱼',
    '用药',
    '增氧',
    '设备',
    '传感器',
    '报警',
    'pH',
    '温度',
    '分塘',
    'FCR',
  ]
  for (const k of keys) {
    if (s.includes(k.toLowerCase())) extras.push(k)
  }
  return [...new Set([...cn, ...en, ...extras])]
}

/** 用户问题文本是否包含某个关键词 */
function queryHasKeyword(queryLower, keyword) {
  const k = String(keyword || '').trim().toLowerCase()
  if (!k) return false
  return queryLower.includes(k)
}

/**
 * 统计「经验自身关键词」在用户问题中的命中数。
 * 关键词优先用 frontmatter tags；不足时用标题分词补齐，便于旧文档也能按双词命中。
 */
function keywordHitsForRule(query, rule) {
  const q = String(query || '').toLowerCase()
  const tags = Array.isArray(rule.tags) ? rule.tags.map((t) => String(t).trim()).filter(Boolean) : []
  let keywords = [...tags]
  if (keywords.length < MIN_KEYWORD_HITS) {
    const titleParts = tokenize(rule.title || '').filter((t) => t.length >= 2)
    for (const t of titleParts) {
      if (!keywords.some((k) => k.toLowerCase() === t.toLowerCase())) keywords.push(t)
    }
  }
  // 去重（忽略大小写）
  const seen = new Set()
  keywords = keywords.filter((k) => {
    const key = k.toLowerCase()
    if (seen.has(key)) return false
    seen.add(key)
    return true
  })

  const matched = []
  for (const kw of keywords) {
    if (queryHasKeyword(q, kw)) matched.push(kw)
  }
  return { keywords, matched, hitCount: matched.length }
}

function loadDir(dir, folder) {
  if (!fs.existsSync(dir)) return []
  return fs
    .readdirSync(dir)
    .filter((f) => f.endsWith('.md'))
    .map((file) => {
      const raw = fs.readFileSync(path.join(dir, file), 'utf8')
      const { meta, body } = parseFrontmatter(raw)
      const title = meta.title || file.replace(/\.md$/, '')
      const tags = Array.isArray(meta.tags) ? meta.tags : []
      const weight = Number(meta.weight) || 1
      const text = `${title}\n${tags.join(' ')}\n${body}`
      return {
        id: meta.id || `${folder}-${file}`,
        file,
        folder,
        title,
        tags,
        weight,
        body,
        tokens: tokenize(text),
      }
    })
}

let cache = null

/** 同时加载内置 rules + 用户录入 skills（原生 MD） */
export function loadRules(force = false) {
  if (cache && !force) return cache
  cache = [...loadDir(RULES_DIR, 'rules'), ...loadDir(SKILLS_DIR, 'skills')]
  return cache
}

/**
 * 本地经验检索。
 * 判定（樊总）：只要命中某条经验的至少两个关键词，就参考该经验。
 * @param {string} query
 * @param {{ topK?: number, minKeywordHits?: number }} [opts]
 *   minScore 仍接受但忽略（兼容旧调用）；以 minKeywordHits 为准。
 */
export function retrieveLocalRules(query, { topK = 3, minKeywordHits = MIN_KEYWORD_HITS, minScore } = {}) {
  const rules = loadRules()
  const need = Number(minKeywordHits) > 0 ? Number(minKeywordHits) : MIN_KEYWORD_HITS
  // 兼容旧参数：若显式传了很低的 minScore，不再据此放宽（双词门槛优先）
  void minScore

  if (!String(query || '').trim()) {
    return { hits: [], bestScore: 0, sufficient: false }
  }

  const scored = rules
    .map((rule) => {
      const { keywords, matched, hitCount } = keywordHitsForRule(query, rule)
      if (hitCount < need) {
        return { ...rule, score: 0, hit: hitCount, hitCount, matchedKeywords: matched, keywords }
      }
      // 通过门槛后按：命中词数 + 权重 + skills 目录加成 排序
      const folderBoost = rule.folder === 'skills' ? 1.08 : 1
      const score = hitCount * (1 + rule.weight / 10) * folderBoost
      return {
        ...rule,
        score,
        hit: hitCount,
        hitCount,
        matchedKeywords: matched,
        keywords,
      }
    })
    .filter((r) => r.hitCount >= need)
    .sort((a, b) => b.score - a.score || b.hitCount - a.hitCount)

  const hits = scored.slice(0, topK)
  const bestScore = hits[0]?.score || 0
  return {
    hits,
    bestScore,
    sufficient: hits.length > 0,
  }
}

export function formatLocalContext(hits) {
  if (!hits.length) return ''
  return hits
    .map((h, i) => {
      const kind = h.folder === 'skills' ? '企业 Skill' : '内置规则'
      const matched = Array.isArray(h.matchedKeywords) && h.matchedKeywords.length
        ? `｜命中词 ${h.matchedKeywords.join('、')}`
        : ''
      return `【${kind} ${i + 1}｜${h.title}｜权重 ${h.weight}｜相关分 ${h.score.toFixed(1)}${matched}】\n${h.body}`
    })
    .join('\n\n')
}
