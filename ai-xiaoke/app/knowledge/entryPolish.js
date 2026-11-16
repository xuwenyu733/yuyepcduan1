/**
 * 图文录入：一键规范标题与文字说明（可选调用大模型；无 Key 时本地规则润色）
 */

function clampTitle(title) {
  return String(title || '')
    .replace(/\s+/g, ' ')
    .trim()
    .slice(0, 160)
}

function clampContent(content) {
  return String(content || '')
    .replace(/\r\n/g, '\n')
    .trim()
    .slice(0, 20000)
}

/** 无模型时的本地规范：去噪、分段、补基础结构，不编造事实 */
export function polishEntryLocal(title, content) {
  let t = clampTitle(title)
  let c = clampContent(content)

  c = c
    .replace(/[ \t]+\n/g, '\n')
    .replace(/\n{3,}/g, '\n\n')
    .replace(/[，,]{2,}/g, '，')
    .replace(/[。.]{2,}/g, '。')
    .replace(/[！!]{2,}/g, '！')
    .replace(/[？?]{2,}/g, '？')

  // 口语连接词轻度规范化
  c = c
    .replace(/嗯+|额+|啊+|那个+/g, '')
    .replace(/\s{2,}/g, ' ')
    .trim()

  if (!t && c) {
    const first = c.split(/[。！？\n]/)[0] || c
    t = clampTitle(first.slice(0, 40) || '现场记录')
  }

  const looksStructured = /^(基本信息|现场情况|关键数据|处理措施|待办|备注)[：:]/m.test(c)
  if (c && !looksStructured) {
    const lines = c.split(/\n+/).map((x) => x.trim()).filter(Boolean)
    if (lines.length === 1) {
      c = [
        '【基本信息】',
        `标题参考：${t || '（未命名）'}`,
        '',
        '【现场情况】',
        lines[0],
        '',
        '【关键数据】',
        '（原文未单独列出，请核对后补充）',
        '',
        '【待办/备注】',
        '（如有后续处理请补充）',
      ].join('\n')
    } else {
      c = [
        '【基本信息】',
        `标题参考：${t || '（未命名）'}`,
        '',
        '【现场情况】',
        ...lines.map((line) => (line.startsWith('-') || line.startsWith('•') ? line : `- ${line}`)),
      ].join('\n')
    }
  }

  return {
    title: t,
    content: clampContent(c),
    mode: 'local',
    notice: '已按本地规则整理（未调用大模型）。请核对事实后保存；配置 LLM_API_KEY 后可获得更自然的优化。',
  }
}

function extractJsonObject(text) {
  const raw = String(text || '').trim()
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    /* continue */
  }
  const fence = raw.match(/```(?:json)?\s*([\s\S]*?)```/i)
  if (fence) {
    try {
      return JSON.parse(fence[1].trim())
    } catch {
      /* continue */
    }
  }
  const start = raw.indexOf('{')
  const end = raw.lastIndexOf('}')
  if (start >= 0 && end > start) {
    try {
      return JSON.parse(raw.slice(start, end + 1))
    } catch {
      return null
    }
  }
  return null
}

export async function polishEntryWithModel(title, content, cfg, fetcher = fetch) {
  const system = `你是渔业现场记录润色助手。用户会给出可能口语化、不规范的标题和文字说明。
请在不编造事实的前提下，整理成便于入库与检索的规范中文。
硬性要求：
1. 不得新增原文没有的数值、鱼池编号、药品名、时间、结论；不确定处用「待补充」标注。
2. 标题简洁（不超过 40 字更佳，最长 160 字），含池号/主题更佳。
3. 正文建议结构（可按原文取舍）：【基本信息】【现场情况】【关键数据】【处理措施】【待办/备注】。
4. 修正错别字与标点，口语改书面，保留原意。
5. 只输出 JSON：{"title":"...","content":"..."}，不要其它说明。`

  const model = await fetcher(`${cfg.baseUrl}/chat/completions`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${cfg.apiKey}`,
      'Content-Type': 'application/json',
    },
    signal: AbortSignal.timeout(45000),
    body: JSON.stringify({
      model: cfg.model,
      stream: false,
      temperature: 0.2,
      messages: [
        { role: 'system', content: system },
        {
          role: 'user',
          content: JSON.stringify({
            title: String(title || ''),
            content: String(content || ''),
          }),
        },
      ],
    }),
  })
  if (!model.ok) {
    throw new Error(model.status === 401 ? '模型认证失败，请检查 API Key' : `模型服务暂时不可用（${model.status}）`)
  }
  const result = await model.json()
  const text = result.choices?.[0]?.message?.content?.trim()
  const parsed = extractJsonObject(text)
  if (!parsed || typeof parsed !== 'object') {
    throw new Error('模型未返回可用的优化结果')
  }
  const nextTitle = clampTitle(parsed.title != null ? parsed.title : title)
  const nextContent = clampContent(parsed.content != null ? parsed.content : content)
  if (!nextTitle && !nextContent) {
    throw new Error('优化结果为空')
  }
  return {
    title: nextTitle,
    content: nextContent,
    mode: 'model',
    notice: '已用 AI 规范标题与文字说明。请核对数值与事实后再保存。',
  }
}

export async function handleEntryPolish(req, res, cfg) {
  try {
    const chunks = []
    let size = 0
    await new Promise((resolve, reject) => {
      req.on('data', (c) => {
        size += c.length
        if (size > 120000) {
          reject(new Error('请求内容过长'))
          return
        }
        chunks.push(c)
      })
      req.on('end', resolve)
      req.on('error', reject)
    })
    const body = JSON.parse(Buffer.concat(chunks).toString('utf8') || '{}')
    const title = String(body.title || '')
    const content = String(body.content || '')
    if (!title.trim() && !content.trim()) {
      res.statusCode = 400
      res.setHeader('Content-Type', 'application/json;charset=utf-8')
      res.end(JSON.stringify({ error: '请先填写标题或文字说明' }))
      return
    }

    let result
    if (cfg.configured) {
      try {
        result = await polishEntryWithModel(title, content, cfg)
      } catch (e) {
        const local = polishEntryLocal(title, content)
        result = {
          ...local,
          notice: `${e.message || '模型调用失败'}；已改用本地规则整理，请核对后保存。`,
          mode: 'local_fallback',
        }
      }
    } else {
      result = polishEntryLocal(title, content)
    }

    res.statusCode = 200
    res.setHeader('Content-Type', 'application/json;charset=utf-8')
    res.end(JSON.stringify({ ok: true, ...result }))
  } catch (e) {
    res.statusCode = 400
    res.setHeader('Content-Type', 'application/json;charset=utf-8')
    res.end(JSON.stringify({ error: e.message || '优化失败' }))
  }
}
