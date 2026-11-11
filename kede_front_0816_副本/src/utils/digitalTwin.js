/** 静态 3D 孪生入口（public/digital-twin），便于演示预取 */

export function digitalTwinBase() {
  const base = process.env.BASE_URL || '/'
  return base.endsWith('/') ? `${base}digital-twin/` : `${base}/digital-twin/`
}

/** 预取入口 HTML 及其 JS/CSS，缩短首次打开延迟 */
export function prefetchDigitalTwin() {
  if (typeof document === 'undefined') return
  if (window.__twinPrefetchStarted) return
  window.__twinPrefetchStarted = true
  const base = digitalTwinBase()
  const addPrefetch = (href, asType) => {
    if (!href || document.querySelector(`link[rel="prefetch"][href="${href}"]`)) return
    const link = document.createElement('link')
    link.rel = 'prefetch'
    if (asType) link.as = asType
    link.href = href
    document.head.appendChild(link)
  }
  fetch(base, { credentials: 'same-origin' })
    .then((r) => (r.ok ? r.text() : Promise.reject(r.status)))
    .then((html) => {
      const doc = new DOMParser().parseFromString(html, 'text/html')
      doc.querySelectorAll('script[src]').forEach((el) => {
        const src = el.getAttribute('src')
        if (!src) return
        addPrefetch(src.startsWith('/') || src.startsWith('http') ? src : base + src, 'script')
      })
      doc.querySelectorAll('link[rel="stylesheet"]').forEach((el) => {
        const href = el.getAttribute('href')
        if (!href) return
        addPrefetch(href.startsWith('/') || href.startsWith('http') ? href : base + href, 'style')
      })
    })
    .catch(() => {
      window.__twinPrefetchStarted = false
    })
}
