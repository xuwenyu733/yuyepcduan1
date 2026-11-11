import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { loadRules } from './retriever.js'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
export const SKILLS_DIR = path.join(__dirname, 'skills')
export const RULES_DIR = path.join(__dirname, 'rules')

function ensureSkillsDir() {
  if (!fs.existsSync(SKILLS_DIR)) {
    fs.mkdirSync(SKILLS_DIR, { recursive: true })
  }
}

export function safeFileName(title) {
  const base = String(title || '未命名技能')
    .replace(/[\\/:*?"<>|]/g, '')
    .replace(/\s+/g, '_')
    .slice(0, 40)
  return `${base || '未命名技能'}.md`
}

export function makeId(title) {
  const slug = String(title || 'skill')
    .toLowerCase()
    .replace(/[^\u4e00-\u9fffa-z0-9]+/g, '-')
    .replace(/^-|-$/g, '')
    .slice(0, 48)
  return `skill-${slug || 'item'}-${Date.now().toString(36)}`
}

/**
 * 把普通人写的纯文本，尽量整理成可读 Markdown
 */
export function plainTextToMarkdown(title, content) {
  const lines = String(content || '')
    .replace(/\r\n/g, '\n')
    .trim()
    .split('\n')
  const out = [`# ${title || '未命名技能'}`, '']
  for (const line of lines) {
    const t = line.trim()
    if (!t) {
      out.push('')
      continue
    }
    // 已是标题/列表则保留
    if (/^(#{1,6}\s|[-*+]\s|\d+\.\s)/.test(t)) {
      out.push(t)
      continue
    }
    // 「一、xxx」「1、xxx」「标题：」→ 二级标题
    if (/^[一二三四五六七八九十]+[、.．]/.test(t) || /^\d+[、.．]/.test(t) || /[:：]$/.test(t)) {
      out.push(`## ${t.replace(/[:：]$/, '')}`)
      continue
    }
    out.push(t)
  }
  return out.join('\n').replace(/\n{3,}/g, '\n\n').trim() + '\n'
}

export function buildSkillMarkdown({ id, title, weight, tags, body }) {
  const tagStr = Array.isArray(tags) ? tags.join(', ') : String(tags || '')
  const w = Number(weight) || 8
  return [
    '---',
    `id: ${id}`,
    `title: ${title}`,
    `weight: ${w}`,
    `tags: ${tagStr}`,
    'source: skills-editor',
    '---',
    '',
    body.trim(),
    '',
  ].join('\n')
}

function resolvePath(file, folder) {
  const dir = folder === 'rules' ? RULES_DIR : SKILLS_DIR
  const name = path.basename(String(file || ''))
  if (!name.endsWith('.md')) throw new Error('仅支持 .md 文件')
  return path.join(dir, name)
}

export function listSkillsDetailed() {
  const rules = loadRules(true)
  return rules.map((r) => ({
    id: r.id,
    title: r.title,
    weight: r.weight,
    tags: r.tags,
    file: r.file,
    folder: r.folder,
    editable: r.folder === 'skills',
    preview: String(r.body || '').slice(0, 120),
  }))
}

export function getSkill(file, folder = 'skills') {
  const full = resolvePath(file, folder)
  if (!fs.existsSync(full)) throw new Error('文件不存在')
  const raw = fs.readFileSync(full, 'utf8')
  return { file: path.basename(full), folder, raw }
}

export function saveSkill({
  title,
  content,
  weight = 8,
  tags = [],
  file,
  convertPlain = true,
}) {
  ensureSkillsDir()
  const t = String(title || '').trim()
  if (!t) throw new Error('请填写标题')
  const plain = String(content || '').trim()
  if (!plain) throw new Error('请填写内容')

  const body = convertPlain ? plainTextToMarkdown(t, plain) : plain
  const existingName = file ? path.basename(file) : ''
  const targetName = existingName && existingName.endsWith('.md') ? existingName : safeFileName(t)
  const full = path.join(SKILLS_DIR, targetName)

  let id = makeId(t)
  if (existingName && fs.existsSync(full)) {
    const old = fs.readFileSync(full, 'utf8')
    const m = old.match(/^id:\s*(.+)$/m)
    if (m) id = m[1].trim()
  }

  const tagList = Array.isArray(tags)
    ? tags
    : String(tags || '')
        .split(/[,，、\s]+/)
        .filter(Boolean)

  const md = buildSkillMarkdown({
    id,
    title: t,
    weight,
    tags: tagList,
    body,
  })
  fs.writeFileSync(full, md, 'utf8')
  loadRules(true)
  return {
    id,
    title: t,
    weight: Number(weight) || 8,
    tags: tagList,
    file: targetName,
    folder: 'skills',
    editable: true,
  }
}

export function deleteSkill(file) {
  const full = resolvePath(file, 'skills')
  if (!fs.existsSync(full)) throw new Error('只能删除「知识录入」生成的技能文件')
  fs.unlinkSync(full)
  loadRules(true)
  return { ok: true, file: path.basename(full) }
}
