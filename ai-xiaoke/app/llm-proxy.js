/**
 * 开发态本地代理：
 * 每次问答都检索企业 Skill + 网络资料，回答固定三段：
 * 一、企业 Skill 依据
 * 二、网络检索资料
 * 三、综合结论（Skill + 网络结合）
 */

import { formatLocalContext, loadRules, retrieveLocalRules } from './knowledge/retriever.js'
import {
  deleteSkill,
  getSkill,
  listSkillsDetailed,
  plainTextToMarkdown,
  saveSkill,
} from './knowledge/skillsStore.js'
import { formatWebContext, webSearch } from './knowledge/webSearch.js'
import { handleDatabaseResearch } from './knowledge/databaseResearch.js'

const COMBINE_SYSTEM = `你是「小科」，智慧养殖 AI 助手。
你将收到：多轮对话历史、当前用户问题、企业 Skill 摘要、网络检索摘要。
请只输出「综合结论」正文（不要再写一二三标题），要求：
1. 必须结合对话历史理解指代（如“刚才那个”“继续上面”），保持上下文连续。
2. 冲突时以企业 Skill 为准，并点明差异。
3. 给出可执行建议，分点说明。
4. 关键结论用括号标注来源，如（来源：企业Skill《xxx》）或（来源：网络《标题》）。
5. 若两边都不足，如实说明不确定。
6. 用中文，简洁专业。`

function getConfig() {
  const apiKey = (process.env.LLM_API_KEY || '').trim()
  let baseUrl = (process.env.LLM_BASE_URL || 'https://api.deepseek.com').trim().replace(/\/+$/, '')
  if (baseUrl.endsWith('/chat/completions')) {
    baseUrl = baseUrl.replace(/\/chat\/completions$/, '')
  }
  if (!baseUrl.endsWith('/v1') && !baseUrl.includes('/compatible-mode')) {
    if (baseUrl.includes('deepseek.com') || baseUrl.includes('openai.com')) {
      baseUrl = `${baseUrl}/v1`
    }
  }
  const model = (process.env.LLM_MODEL || 'deepseek-chat').trim()
  // 旧 LOCAL_RULE_MIN_SCORE 已废弃；现按「至少命中 N 个关键词」判定（默认 2）
  const localMinKeywordHits = Number(process.env.LOCAL_MIN_KEYWORD_HITS || 2)
  const enableWeb = String(process.env.ENABLE_WEB_SEARCH || 'true').toLowerCase() !== 'false'
  return {
    apiKey,
    baseUrl,
    model,
    configured: Boolean(apiKey),
    localMinKeywordHits,
    localMinScore: localMinKeywordHits, // 兼容旧字段名
    enableWeb,
  }
}

function readJson(req) {
  return new Promise((resolve, reject) => {
    const chunks = []
    req.on('data', (c) => chunks.push(c))
    req.on('end', () => {
      try {
        const raw = Buffer.concat(chunks).toString('utf8')
        resolve(raw ? JSON.parse(raw) : {})
      } catch (e) {
        reject(e)
      }
    })
    req.on('error', reject)
  })
}

function routeHintLabel(hint) {
  if (hint === 'data_entry') return '录入数据（帮助用户整理要录入的业务字段与步骤）'
  return '查询分析（只读分析与建议，不要求用户填表）'
}

function modeLabel(mode) {
  if (mode === 'local_plus_web') return '企业Skill + 网络检索（三段式）'
  if (mode === 'local_only') return '仅企业Skill（网络暂无结果）'
  if (mode === 'web_only') return '仅网络检索（本地未命中）'
  if (mode === 'local_demo') return '演示模式三段式'
  return '资料不足'
}

function cleanMd(text) {
  return String(text || '')
    .replace(/^#{1,6}\s*/gm, '')
    .replace(/\*\*(.*?)\*\*/g, '$1')
    .trim()
}

function buildSkillSection(hits) {
  if (!hits?.length) {
    return '本地 Skill 暂无直接覆盖本问题。\n（可在「知识录入」补充相关规定）'
  }
  return hits
    .map((h, i) => {
      const kind = h.folder === 'skills' ? '用户录入' : '系统内置'
      const body = cleanMd(h.body)
      return [
        `${i + 1}. 《${h.title}》（${kind}｜权重 ${h.weight}｜相关分 ${Number(h.score).toFixed(2)}｜命中词 ${(h.matchedKeywords || []).join('、') || '-'}｜文件 ${h.file || '-'}）`,
        body,
      ].join('\n')
    })
    .join('\n\n')
}

function buildWebSection(items, provider) {
  if (!items?.length) {
    return `本次网络检索暂无可用资料${provider && provider !== 'none' ? `（渠道：${provider}）` : ''}。\n已尝试百度/360 等公开检索，可稍后重试。`
  }
  const channel = provider ? `（检索渠道：${provider}）` : ''
  return [
    `以下为本次联网检索结果${channel}：`,
    '',
    ...items.map((it, i) => {
      const link = it.url ? `\n   链接：${it.url}` : ''
      const src = it.source ? `｜${it.source}` : ''
      return `${i + 1}. ${it.title || '未命名资料'}${src}\n   ${it.snippet || ''}${link}`
    }),
  ].join('\n')
}

function normalizeHistory(raw) {
  if (!Array.isArray(raw)) return []
  return raw
    .map((h) => {
      const role = h?.role === 'assistant' || h?.role === 'ai' ? 'assistant' : h?.role === 'user' ? 'user' : ''
      const content = String(h?.content || h?.text || '').trim()
      if (!role || !content) return null
      return { role, content: content.slice(0, 3500) }
    })
    .filter(Boolean)
    .slice(-16)
}

function buildCombinedFallback(message, hits, webItems) {
  const lines = [`针对「${message}」，综合如下：`, '']
  if (hits?.length) {
    lines.push(`优先依据企业 Skill：${hits.map((h) => `《${h.title}》`).join('、')}。`)
  } else {
    lines.push('本地 Skill 未覆盖，结论主要参考网络资料（请谨慎核实）。')
  }
  if (webItems?.length) {
    lines.push(`网络资料参考：${webItems.map((w) => w.title || '资料').join('、')}。`)
  } else {
    lines.push('网络侧暂无可用补充。')
  }
  lines.push('')
  lines.push('建议：先按企业 Skill 执行现场操作；网络信息仅作补充对照。配置大模型 Key 后可获得更完整的综合分析。')
  return lines.join('\n')
}

function toHitMeta(hits) {
  return (hits || []).map((h) => ({
    id: h.id,
    title: h.title,
    weight: h.weight,
    score: Number(h.score.toFixed(2)),
    hitCount: h.hitCount || 0,
    matchedKeywords: h.matchedKeywords || [],
    file: h.file,
    folder: h.folder,
  }))
}

async function gatherEvidence(message, cfg) {
  // 樊总：命中某条经验至少 2 个关键词才引用；不再用低分回退放宽
  const local = retrieveLocalRules(message, {
    topK: 3,
    minKeywordHits: cfg.localMinKeywordHits || 2,
  })

  let web = { items: [], provider: 'none' }
  if (cfg.enableWeb) {
    try {
      web = await webSearch(message)
    } catch (e) {
      web = { items: [], provider: 'error', error: e.message }
    }
  }

  let mode = 'none'
  if (local.hits.length && web.items.length) mode = 'local_plus_web'
  else if (local.hits.length) mode = 'local_only'
  else if (web.items.length) mode = 'web_only'

  const skillText = buildSkillSection(local.hits)
  const webText = buildWebSection(web.items, web.provider)

  return {
    mode,
    modeLabel: modeLabel(mode),
    localHits: toHitMeta(local.hits),
    webHits: (web.items || []).map((it) => ({
      title: it.title,
      url: it.url,
      source: it.source,
      snippet: it.snippet,
    })),
    webProvider: web.provider,
    bestLocalScore: local.bestScore,
    skillText,
    webText,
    // 给模型用的压缩上下文
    modelContext: [
      formatLocalContext(local.hits) || '（无企业 Skill）',
      formatWebContext(web.items) || '（无网络资料）',
    ].join('\n\n'),
    rawHits: local.hits,
    rawWeb: web.items,
  }
}

function beginSse(res, { conversationId, rag, sections, progress }) {
  res.statusCode = 200
  res.setHeader('Content-Type', 'text/event-stream;charset=utf-8')
  res.setHeader('Cache-Control', 'no-cache')
  res.setHeader('Connection', 'keep-alive')
  res.write(
    `data: ${JSON.stringify({
      type: 'meta',
      conversation_id: conversationId,
      rag,
      sections,
    })}\n\n`,
  )
  if (progress) {
    res.write(`data: ${JSON.stringify({ type: 'progress', content: progress })}\n\n`)
  }
}

function streamText(res, text) {
  const s = String(text || '')
  const chunkSize = 48
  for (let i = 0; i < s.length; i += chunkSize) {
    res.write(`data: ${JSON.stringify({ type: 'delta', content: s.slice(i, i + chunkSize) })}\n\n`)
  }
}

function endSse(res, { sections, combined }) {
  const finalSections = { ...sections, combined }
  // 兼容旧前端：text 仍给出完整三段
  const full = [
    '一、企业 Skill 依据',
    sections.skill,
    '',
    '二、网络检索资料',
    sections.web,
    '',
    '三、综合结论',
    combined,
  ].join('\n')
  res.write(
    `data: ${JSON.stringify({
      type: 'final',
      content: full,
      sections: finalSections,
    })}\n\n`,
  )
  res.end()
}

export function llmProxyPlugin() {
  return {
    name: 'xiaoke-llm-proxy',
    configureServer(server) {
      server.middlewares.use(async (req, res, next) => {
        const url = req.url || ''

        if (url === '/llm/database/chat/stream' && req.method === 'POST') {
          await handleDatabaseResearch(req, res, getConfig())
          return
        }

        if (url === '/llm/status' && req.method === 'GET') {
          const cfg = getConfig()
          const rules = loadRules()
          res.setHeader('Content-Type', 'application/json;charset=utf-8')
          res.end(
            JSON.stringify({
              configured: cfg.configured,
              chatReady: true,
              demoMode: !cfg.configured,
              model: cfg.model,
              baseUrl: cfg.baseUrl,
              providerHint: cfg.configured
                ? cfg.baseUrl.includes('deepseek')
                  ? 'DeepSeek'
                  : cfg.baseUrl.includes('dashscope')
                    ? '通义千问'
                    : cfg.baseUrl.includes('openai.com')
                      ? 'OpenAI'
                      : 'OpenAI-Compatible'
                : '本地Skill演示',
              rag: {
                localRules: rules.length,
                webSearchEnabled: cfg.enableWeb,
                alwaysWebSearch: cfg.enableWeb,
                answerFormat: 'skill_then_web_then_combined',
                bingConfigured: Boolean((process.env.BING_SEARCH_KEY || '').trim()),
                localMinScore: cfg.localMinScore,
              },
            }),
          )
          return
        }

        if ((url === '/llm/rules' || url === '/llm/skills') && req.method === 'GET') {
          const rules = listSkillsDetailed()
          res.setHeader('Content-Type', 'application/json;charset=utf-8')
          res.end(JSON.stringify({ count: rules.length, rules, skills: rules }))
          return
        }

        if (url.startsWith('/llm/skills/detail') && req.method === 'GET') {
          try {
            const u = new URL(url, 'http://127.0.0.1')
            const file = u.searchParams.get('file')
            const folder = u.searchParams.get('folder') || 'skills'
            const data = getSkill(file, folder)
            res.setHeader('Content-Type', 'application/json;charset=utf-8')
            res.end(JSON.stringify(data))
          } catch (e) {
            res.statusCode = 404
            res.end(JSON.stringify({ error: e.message }))
          }
          return
        }

        if (url === '/llm/skills/preview' && req.method === 'POST') {
          try {
            const body = await readJson(req)
            const md = plainTextToMarkdown(body.title || '未命名', body.content || '')
            res.setHeader('Content-Type', 'application/json;charset=utf-8')
            res.end(JSON.stringify({ markdown: md }))
          } catch (e) {
            res.statusCode = 400
            res.end(JSON.stringify({ error: e.message }))
          }
          return
        }

        if (url === '/llm/skills' && req.method === 'POST') {
          try {
            const body = await readJson(req)
            const saved = saveSkill(body)
            res.setHeader('Content-Type', 'application/json;charset=utf-8')
            res.end(JSON.stringify({ ok: true, skill: saved }))
          } catch (e) {
            res.statusCode = 400
            res.end(JSON.stringify({ error: e.message }))
          }
          return
        }

        if (url.startsWith('/llm/skills') && req.method === 'DELETE') {
          try {
            const u = new URL(url, 'http://127.0.0.1')
            const file = u.searchParams.get('file')
            const result = deleteSkill(file)
            res.setHeader('Content-Type', 'application/json;charset=utf-8')
            res.end(JSON.stringify(result))
          } catch (e) {
            res.statusCode = 400
            res.end(JSON.stringify({ error: e.message }))
          }
          return
        }

        if (url !== '/llm/chat' || req.method !== 'POST') {
          return next()
        }

        const cfg = getConfig()

        try {
          const body = await readJson(req)
          const message = String(body.message || '').trim()
          const routeHint = body.route_hint || 'query_analysis'
          if (!message) {
            res.statusCode = 400
            res.end(JSON.stringify({ error: 'message 不能为空' }))
            return
          }

          const conversationId = body.conversation_id || `local-${Date.now()}`
          const history = normalizeHistory(body.history)
          const evidence = await gatherEvidence(message, cfg)

          const rag = {
            mode: evidence.mode,
            modeLabel: evidence.modeLabel,
            localHits: evidence.localHits,
            webHits: evidence.webHits,
            webProvider: evidence.webProvider,
            bestLocalScore: evidence.bestLocalScore,
            historyTurns: history.length,
          }

          const sections = {
            skill: evidence.skillText,
            web: evidence.webText,
            combined: '',
          }

          beginSse(res, {
            conversationId,
            rag,
            sections,
            progress: `已检索：企业Skill ${evidence.localHits.length} 条｜网络 ${evidence.webHits.length} 条｜历史 ${history.length} 轮，正在生成综合结论…`,
          })

          // 先把一二两部分推给前端（通过特殊事件）
          res.write(
            `data: ${JSON.stringify({
              type: 'sections',
              sections: { skill: sections.skill, web: sections.web, combined: '' },
            })}\n\n`,
          )

          let combined = ''

          if (!cfg.configured) {
            const histHint = history.length
              ? `\n（已记住本会话最近 ${history.length} 轮对话）`
              : ''
            combined = `${buildCombinedFallback(message, evidence.rawHits, evidence.rawWeb)}${histHint}`
            streamText(res, combined)
            endSse(res, { sections, combined })
            return
          }

          const userPrompt = [
            `用户意图：${routeHintLabel(routeHint)}`,
            `当前用户问题：${message}`,
            '',
            '【企业 Skill 依据】',
            evidence.skillText,
            '',
            '【网络检索资料】',
            evidence.webText,
            '',
            '请结合上方资料与对话历史，输出综合结论：',
          ].join('\n')

          const upstream = await fetch(`${cfg.baseUrl}/chat/completions`, {
            method: 'POST',
            headers: {
              Authorization: `Bearer ${cfg.apiKey}`,
              'Content-Type': 'application/json',
              Accept: 'text/event-stream',
            },
            body: JSON.stringify({
              model: cfg.model,
              messages: [
                { role: 'system', content: COMBINE_SYSTEM },
                ...history,
                { role: 'user', content: userPrompt },
              ],
              stream: true,
              temperature: 0.4,
            }),
          })

          if (!upstream.ok || !upstream.body) {
            const text = await upstream.text().catch(() => '')
            combined = `${buildCombinedFallback(message, evidence.rawHits, evidence.rawWeb)}\n\n（大模型调用失败：${upstream.status} ${text.slice(0, 200)}）`
            streamText(res, combined)
            endSse(res, { sections, combined })
            return
          }

          const reader = upstream.body.getReader()
          const decoder = new TextDecoder('utf-8')
          let buffer = ''

          while (true) {
            const { value, done } = await reader.read()
            if (done) break
            buffer += decoder.decode(value, { stream: true })
            const lines = buffer.split(/\r?\n/)
            buffer = lines.pop() || ''
            for (const line of lines) {
              const s = line.trim()
              if (!s.startsWith('data:')) continue
              const payload = s.slice(5).trim()
              if (!payload || payload === '[DONE]') continue
              try {
                const json = JSON.parse(payload)
                const delta = json.choices?.[0]?.delta?.content || ''
                if (delta) {
                  combined += delta
                  res.write(`data: ${JSON.stringify({ type: 'delta', content: delta })}\n\n`)
                }
              } catch {
                /* skip */
              }
            }
          }

          if (!combined.trim()) {
            combined = buildCombinedFallback(message, evidence.rawHits, evidence.rawWeb)
            streamText(res, combined)
          }

          endSse(res, { sections, combined })
        } catch (e) {
          res.statusCode = 500
          res.setHeader('Content-Type', 'application/json;charset=utf-8')
          res.end(JSON.stringify({ error: e.message || String(e) }))
        }
      })
    },
  }
}
