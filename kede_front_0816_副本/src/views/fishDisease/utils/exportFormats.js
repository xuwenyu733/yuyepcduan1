/**
 * 鱼病模块多格式导出：xls / xlsx / csv / doc / docx / pdf
 * 纯前端实现，不新增依赖。
 */

export const EXPORT_FORMAT_OPTIONS = [
  { value: 'xls', label: 'Excel (.xls)' },
  { value: 'xlsx', label: 'Excel (.xlsx)' },
  { value: 'csv', label: 'CSV (.csv)' },
  { value: 'doc', label: 'Word (.doc)' },
  { value: 'docx', label: 'Word (.docx)' },
  { value: 'pdf', label: 'PDF (.pdf)' }
]

function escapeHtml(value) {
  return String(value == null ? '' : value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function escapeCsv(value) {
  const text = String(value == null ? '' : value)
  return `"${text.replace(/"/g, '""')}"`
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

function toMatrix(columns, rows) {
  const headers = columns.map(c => c.label)
  const body = rows.map(row => columns.map(c => {
    if (typeof c.value === 'function') return c.value(row)
    return row[c.key]
  }))
  return [headers, ...body]
}

function buildHtmlTable(title, matrix) {
  const thead = `<tr>${matrix[0].map(h => `<th>${escapeHtml(h)}</th>`).join('')}</tr>`
  const tbody = matrix.slice(1).map(r =>
    `<tr>${r.map(c => `<td>${escapeHtml(c)}</td>`).join('')}</tr>`
  ).join('')
  return `<!DOCTYPE html><html><head><meta charset="utf-8"><title>${escapeHtml(title)}</title>
<style>body{font-family:Microsoft YaHei,sans-serif;padding:16px}table{border-collapse:collapse;width:100%}
th,td{border:1px solid #999;padding:6px 8px;font-size:12px}th{background:#f0f0f0}</style></head>
<body><h2>${escapeHtml(title)}</h2><table>${thead}${tbody}</table></body></html>`
}

function buildCsv(matrix) {
  return '\ufeff' + matrix.map(row => row.map(escapeCsv).join(',')).join('\n')
}

function buildSpreadsheetXml(title, matrix) {
  const rows = matrix.map(row =>
    `<Row>${row.map(cell => `<Cell><Data ss:Type="String">${escapeHtml(cell)}</Data></Cell>`).join('')}</Row>`
  ).join('')
  return `<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
 <Worksheet ss:Name="${escapeHtml(title).slice(0, 31) || 'Sheet1'}">
  <Table>${rows}</Table>
 </Worksheet>
</Workbook>`
}

/** 简易 PDF（支持中文：以 Helvetica 无法显示时用十六进制 UTF-16BE 文本流较复杂，改用 HTML→打印；此处生成可打开的文本型 PDF 用 Latin 回退，同时提供中文 HTML 打印页） */
function buildSimplePdf(title, matrix) {
  const lines = [String(title)]
  matrix.forEach(row => lines.push(row.map(c => String(c == null ? '' : c)).join(' | ')))
  // 使用 UTF-16BE with BOM in a text stream via WinAnsi is insufficient for CJK.
  // Fallback: embed as PDF with Chinese via CID is heavy — emit HTML-print blob labeled pdf via print dialog helper.
  // For automated download, emit a minimal PDF with ASCII transliteration + note.
  const contentLines = lines.map((line, i) => {
    const safe = line.replace(/[^\x20-\x7E]/g, '?')
    return `BT /F1 10 Tf 40 ${780 - i * 14} Td (${safe.replace(/[\\()]/g, '\\$&')}) Tj ET`
  })
  const stream = contentLines.join('\n')
  const objs = []
  objs.push('1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj')
  objs.push('2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj')
  objs.push('3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources<< /Font<< /F1 5 0 R >> >> >>endobj')
  objs.push(`4 0 obj<< /Length ${stream.length} >>stream\n${stream}\nendstream endobj`)
  objs.push('5 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj')
  let pdf = '%PDF-1.4\n'
  const offsets = [0]
  objs.forEach(obj => {
    offsets.push(pdf.length)
    pdf += obj + '\n'
  })
  const xrefPos = pdf.length
  pdf += `xref\n0 ${objs.length + 1}\n0000000000 65535 f \n`
  for (let i = 1; i <= objs.length; i += 1) {
    pdf += `${String(offsets[i]).padStart(10, '0')} 00000 n \n`
  }
  pdf += `trailer<< /Size ${objs.length + 1} /Root 1 0 R >>\nstartxref\n${xrefPos}\n%%EOF`
  return pdf
}

/**
 * @param {object} options
 * @param {string} options.filenameBase
 * @param {string} [options.title]
 * @param {{key?:string,label:string,value?:Function}[]} options.columns
 * @param {object[]} options.rows
 * @param {string} options.format xls|xlsx|csv|doc|docx|pdf
 */
export function exportTable({ filenameBase, title, columns, rows, format }) {
  const name = filenameBase || 'export'
  const sheetTitle = title || name
  const matrix = toMatrix(columns, rows || [])
  const fmt = String(format || 'csv').toLowerCase()

  if (fmt === 'csv') {
    downloadBlob(new Blob([buildCsv(matrix)], { type: 'text/csv;charset=utf-8;' }), `${name}.csv`)
    return
  }

  if (fmt === 'xls' || fmt === 'xlsx') {
    const xml = buildSpreadsheetXml(sheetTitle, matrix)
    const mime = fmt === 'xlsx'
      ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      : 'application/vnd.ms-excel'
    downloadBlob(new Blob([xml], { type: mime }), `${name}.${fmt}`)
    return
  }

  if (fmt === 'doc' || fmt === 'docx') {
    const html = buildHtmlTable(sheetTitle, matrix)
    const mime = fmt === 'docx'
      ? 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      : 'application/msword'
    downloadBlob(new Blob(['\ufeff' + html], { type: mime }), `${name}.${fmt}`)
    return
  }

  if (fmt === 'pdf') {
    // 中文友好：打开打印页，用户可“另存为 PDF”
    const html = buildHtmlTable(sheetTitle, matrix)
    const w = window.open('', '_blank')
    if (w) {
      w.document.write(html + '<script>window.onload=function(){window.print()}<\/script>')
      w.document.close()
    } else {
      // 弹窗被拦时退回简易 PDF
      downloadBlob(new Blob([buildSimplePdf(sheetTitle, matrix)], { type: 'application/pdf' }), `${name}.pdf`)
    }
    return
  }

  downloadBlob(new Blob([buildCsv(matrix)], { type: 'text/csv;charset=utf-8;' }), `${name}.csv`)
}

/** 将任意对象/数组展平为表格行后导出 */
export function exportRecords(filenameBase, records, format, title) {
  const list = Array.isArray(records) ? records : [records]
  if (!list.length) {
    exportTable({
      filenameBase,
      title: title || filenameBase,
      columns: [{ key: 'msg', label: '说明' }],
      rows: [{ msg: '暂无数据' }],
      format
    })
    return
  }
  const keys = Array.from(list.reduce((set, row) => {
    Object.keys(row || {}).forEach(k => {
      if (k === undefined) return
      const v = row[k]
      if (v != null && typeof v === 'object') return
      set.add(k)
    })
    return set
  }, new Set()))
  const columns = keys.map(k => ({ key: k, label: k }))
  const rows = list.map(row => {
    const flat = {}
    keys.forEach(k => {
      const v = row[k]
      flat[k] = v == null ? '' : (typeof v === 'object' ? JSON.stringify(v) : v)
    })
    return flat
  })
  exportTable({ filenameBase, title: title || filenameBase, columns, rows, format })
}
