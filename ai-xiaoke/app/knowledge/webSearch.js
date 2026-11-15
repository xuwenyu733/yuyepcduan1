/**
 * 联网检索（每次问答默认启用）
 * 优先级：
 * 1) Bing Web Search API（配置 BING_SEARCH_KEY）
 * 2) 百度 + 360 并行合并（国内可用，无需 Key）
 * 3) DuckDuckGo Instant Answer（海外备用）
 */

const UA =
  'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36'

function stripTags(html) {
  return String(html || '')
    .replace(/<script[\s\S]*?<\/script>/gi, ' ')
    .replace(/<style[\s\S]*?<\/style>/gi, ' ')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/gi, ' ')
    .replace(/&amp;/gi, '&')
    .replace(/&lt;/gi, '<')
    .replace(/&gt;/gi, '>')
    .replace(/&quot;/gi, '"')
    .replace(/&#39;/gi, "'")
    .replace(/&#?\w+;/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

function decodeHtml(s) {
  return String(s || '')
    .replace(/&amp;/g, '&')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
}

function isWeakSnippet(s) {
  const t = String(s || '').trim()
  if (!t) return true
  if (t.length < 12) return true
  if (/^\d+页$/.test(t)) return true
  if (/^\d+个回答$/.test(t)) return true
  if (/^时长\s*\d/.test(t)) return true
  if (/^(百度|360)检索命中/.test(t)) return true
  return false
}

function normalizeTitleKey(title) {
  return String(title || '')
    .replace(/\s+/g, '')
    .replace(/[-_|·•].*$/, '')
    .slice(0, 28)
    .toLowerCase()
}

function mergeItems(lists, limit = 6) {
  const out = []
  const seen = new Set()
  for (const list of lists) {
    for (const it of list || []) {
      const key = normalizeTitleKey(it.title)
      if (!key || seen.has(key)) continue
      seen.add(key)
      const snip = String(it.snippet || '').trim()
      out.push({
        ...it,
        snippet: isWeakSnippet(snip) ? '' : snip.slice(0, 240),
      })
      if (out.length >= limit) return out
    }
  }
  return out
}

async function searchBing(query, key, count = 5) {
  const endpoint =
    process.env.BING_SEARCH_ENDPOINT || 'https://api.bing.microsoft.com/v7.0/search'
  const url = `${endpoint}?q=${encodeURIComponent(query)}&count=${count}&mkt=zh-CN`
  const resp = await fetch(url, {
    headers: { 'Ocp-Apim-Subscription-Key': key },
    signal: AbortSignal.timeout(12000),
  })
  if (!resp.ok) {
    const text = await resp.text().catch(() => '')
    throw new Error(`Bing 搜索失败(${resp.status}): ${text}`)
  }
  const json = await resp.json()
  const pages = json.webPages?.value || []
  return pages.map((p) => ({
    title: p.name || '',
    snippet: p.snippet || '',
    url: p.url || '',
    source: 'bing',
  }))
}

function extractBaiduSnippet(block) {
  const patterns = [
    /data-content="([^"]*)"/,
    /class="[^"]*c-span-last[^"]*"[^>]*>[\s\S]*?<span[^>]*>([\s\S]*?)<\/span>/i,
    /class="[^"]*c-abstract[^"]*"[^>]*>([\s\S]*?)<\/(?:span|div|p)>/i,
    /class="[^"]*content-right_[^"]*"[^>]*>([\s\S]*?)<\/span>/i,
    /class="[^"]*cu-line-clamp-[^"]*"[^>]*>([\s\S]*?)<\/span>/i,
    /class="[^"]*c-color-text[^"]*"[^>]*>([\s\S]*?)<\/span>/i,
    /<span class="content-right_8Zs40"[^>]*>([\s\S]*?)<\/span>/i,
  ]
  for (const re of patterns) {
    const m = block.match(re)
    const snip = stripTags(m?.[1] || '')
    if (!isWeakSnippet(snip)) return snip
  }
  // 退而求其次：块内最长一段纯文本
  const plain = stripTags(block)
  const parts = plain.split(/[\n。！？]/).map((s) => s.trim()).filter((s) => s.length > 20)
  if (parts.length) return parts.sort((a, b) => b.length - a.length)[0].slice(0, 220)
  return ''
}

async function searchBaidu(query, limit = 8) {
  const url = `https://www.baidu.com/s?wd=${encodeURIComponent(query)}&rn=10`
  const resp = await fetch(url, {
    headers: {
      'User-Agent': UA,
      'Accept-Language': 'zh-CN,zh;q=0.9',
      Accept: 'text/html,application/xhtml+xml',
    },
    signal: AbortSignal.timeout(12000),
  })
  if (!resp.ok) throw new Error(`百度搜索失败(${resp.status})`)
  const html = await resp.text()
  const containers = [
    ...html.matchAll(
      /<div class="[^"]*c-container[^"]*"[\s\S]*?(?=<div class="[^"]*c-container|id="page"|<div id="content_right")/g,
    ),
  ]
  const items = []
  for (const m of containers) {
    const block = m[0]
    const titleMatch = block.match(
      /<h3[\s\S]*?<a[\s\S]*?href="([^"]+)"[\s\S]*?>([\s\S]*?)<\/a>/i,
    )
    if (!titleMatch) continue
    const title = stripTags(titleMatch[2])
    const href = decodeHtml(titleMatch[1])
    if (!title || title.length < 2) continue
    // 过滤纯广告/爱采购噪音过多时可保留，先都收录
    items.push({
      title,
      snippet: extractBaiduSnippet(block),
      url: href,
      source: 'baidu',
    })
    if (items.length >= limit) break
  }
  return items
}

async function searchSo360(query, limit = 8) {
  const url = `https://www.so.com/s?q=${encodeURIComponent(query)}`
  const resp = await fetch(url, {
    headers: {
      'User-Agent': UA,
      'Accept-Language': 'zh-CN,zh;q=0.9',
      Accept: 'text/html,application/xhtml+xml',
    },
    signal: AbortSignal.timeout(12000),
  })
  if (!resp.ok) throw new Error(`360搜索失败(${resp.status})`)
  const html = await resp.text()
  return parseSo360Results(html, limit)
}

export function parseSo360Results(html, limit = 8) {
  const blocks = [...html.matchAll(/<li class="res-list"[\s\S]*?<\/li>/g)]
  const items = []
  for (const m of blocks) {
    const block = m[0]
    const titleMatch = block.match(/<h3[\s\S]*?<a[^>]*href="([^"]+)"[^>]*>([\s\S]*?)<\/a>/i)
    if (!titleMatch) continue
    const title = stripTags(titleMatch[2])
    if (!title) continue
    const snipMatch =
      block.match(/<span[^>]*class=["'][^"']*\bres-list-summary\b[^"']*["'][^>]*>([\s\S]*?)<\/span>/i) ||
      block.match(/<p[^>]*class=["'][^"']*\bres-desc\b[^"']*["'][^>]*>([\s\S]*?)<\/p>/i)
    const directUrl = block.match(/<h3[\s\S]*?<a[^>]*data-mdurl="([^"]+)"/i)?.[1]
    items.push({
      title,
      snippet: stripTags(snipMatch?.[1] || ''),
      url: decodeHtml(/^https?:\/\//i.test(directUrl || '') ? directUrl : titleMatch[1]),
      source: '360',
    })
    if (items.length >= limit) break
  }
  return items
}

async function searchDuckDuckGo(query) {
  const url = `https://api.duckduckgo.com/?q=${encodeURIComponent(query)}&format=json&no_html=1&skip_disambig=1`
  const resp = await fetch(url, {
    headers: { 'User-Agent': 'XiaokeFishAssistant/1.0' },
    signal: AbortSignal.timeout(8000),
  })
  if (!resp.ok) return []
  const json = await resp.json()
  const items = []
  if (json.AbstractText) {
    items.push({
      title: json.Heading || '摘要',
      snippet: json.AbstractText,
      url: json.AbstractURL || '',
      source: 'duckduckgo',
    })
  }
  for (const t of json.RelatedTopics || []) {
    if (t.Text) {
      items.push({
        title: (t.Text || '').slice(0, 40),
        snippet: t.Text || '',
        url: t.FirstURL || '',
        source: 'duckduckgo',
      })
    } else if (Array.isArray(t.Topics)) {
      for (const sub of t.Topics.slice(0, 3)) {
        if (sub.Text) {
          items.push({
            title: (sub.Text || '').slice(0, 40),
            snippet: sub.Text || '',
            url: sub.FirstURL || '',
            source: 'duckduckgo',
          })
        }
      }
    }
  }
  return items.slice(0, 5)
}

function enrichQuery(q) {
  if (/鱼|养殖|水质|溶氧|饵料|池塘|增氧|渔业|水产/.test(q)) return q
  return `${q} 水产养殖`
}

export async function webSearch(query) {
  const q = String(query || '').trim()
  if (!q) return { items: [], provider: 'none' }

  const enriched = enrichQuery(q)
  const errors = []

  const bingKey = (process.env.BING_SEARCH_KEY || '').trim()
  if (bingKey) {
    try {
      const items = await searchBing(enriched, bingKey)
      if (items.length) return { items, provider: 'bing' }
    } catch (e) {
      errors.push(`bing: ${e.message}`)
      console.warn('[webSearch] Bing failed:', e.message)
    }
  }

  // 国内：百度 + 360 并行，合并去重，摘要更完整
  const [baiduRes, soRes] = await Promise.allSettled([
    searchBaidu(enriched),
    searchSo360(enriched),
  ])
  const baiduItems = baiduRes.status === 'fulfilled' ? baiduRes.value : []
  const soItems = soRes.status === 'fulfilled' ? soRes.value : []
  if (baiduRes.status === 'rejected') {
    errors.push(`baidu: ${baiduRes.reason?.message || baiduRes.reason}`)
    console.warn('[webSearch] Baidu failed:', baiduRes.reason?.message || baiduRes.reason)
  }
  if (soRes.status === 'rejected') {
    errors.push(`360: ${soRes.reason?.message || soRes.reason}`)
    console.warn('[webSearch] 360 failed:', soRes.reason?.message || soRes.reason)
  }

  const merged = mergeItems([baiduItems, soItems], 6)
  if (merged.length) {
    const provider =
      baiduItems.length && soItems.length
        ? 'baidu+360'
        : baiduItems.length
          ? 'baidu'
          : '360'
    return { items: merged, provider }
  }

  try {
    const items = await searchDuckDuckGo(enriched)
    if (items.length) return { items, provider: 'duckduckgo' }
  } catch (e) {
    errors.push(`ddg: ${e.message}`)
    console.warn('[webSearch] DDG failed:', e.message)
  }

  return {
    items: [],
    provider: 'none',
    error: errors.join('; ') || 'all providers empty',
  }
}

export function formatWebContext(items) {
  if (!items?.length) return ''
  return items
    .map(
      (it, i) =>
        `【联网资料 ${i + 1}｜${it.title}】\n${it.snippet}\n来源: ${it.url || '无链接'}`,
    )
    .join('\n\n')
}
