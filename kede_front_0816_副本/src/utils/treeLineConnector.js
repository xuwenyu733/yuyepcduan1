/**
 * 存量分布树连线（对齐 jidi_daping/model4 TreeLine.js）
 * 默认 elbow 直角折线：父节点底边中心 → 中段水平 → 子节点顶边中心
 */

const SVG_NS = 'http://www.w3.org/2000/svg'

/**
 * @param {string} startId
 * @param {string} endId
 * @param {{
 *   color?: string,
 *   width?: number,
 *   type?: 'straight'|'bezier'|'elbow',
 *   container?: string|HTMLElement,
 *   offset?: number
 * }} [options]
 * @returns {null|function}
 */
export function createTreeConnector(startId, endId, options = {}) {
  const config = Object.assign(
    {
      color: '#33B3ED',
      width: 1,
      type: 'elbow',
      container: null,
      offset: -3
    },
    options || {}
  )

  const startEl = document.getElementById(startId)
  const endEl = document.getElementById(endId)
  if (!startEl || !endEl) return null

  let host = null
  if (typeof config.container === 'string') {
    host = document.querySelector(config.container)
  } else if (config.container && config.container.nodeType === 1) {
    host = config.container
  } else {
    host = startEl.closest('.tree-container') || document.body
  }
  if (!host) return null

  const hostStyle = window.getComputedStyle(host)
  if (hostStyle.position === 'static') {
    host.style.position = 'relative'
  }

  let svg = host.querySelector(':scope > .tree-connector-container')
  if (!svg) {
    svg = document.createElementNS(SVG_NS, 'svg')
    svg.classList.add('tree-connector-container')
    svg.setAttribute('aria-hidden', 'true')
    svg.style.position = 'absolute'
    svg.style.top = '0'
    svg.style.left = '0'
    svg.style.width = '100%'
    svg.style.height = '100%'
    svg.style.pointerEvents = 'none'
    svg.style.overflow = 'visible'
    svg.style.zIndex = '0'
    host.insertBefore(svg, host.firstChild)
  }

  const hostRect = host.getBoundingClientRect()
  const startRect = startEl.getBoundingClientRect()
  const endRect = endEl.getBoundingClientRect()

  const startX = startRect.left + startRect.width / 2 - hostRect.left
  const startY = startRect.bottom - hostRect.top - config.offset
  const endX = endRect.left + endRect.width / 2 - hostRect.left
  const endY = endRect.top - hostRect.top + config.offset

  let pathData
  if (config.type === 'bezier') {
    const controlX = (startX + endX) / 2
    const controlY = (startY + endY) / 2
    pathData = `M${startX},${startY} Q${controlX},${controlY} ${endX},${endY}`
  } else if (config.type === 'straight') {
    pathData = `M${startX},${startY} L${endX},${endY}`
  } else {
    // elbow：与原版一致的直角折线
    const midY = (startY + endY) / 2
    pathData = `M${startX},${startY} L${startX},${midY} L${endX},${midY} L${endX},${endY}`
  }

  const path = document.createElementNS(SVG_NS, 'path')
  path.setAttribute('fill', 'none')
  path.setAttribute('stroke', config.color)
  path.setAttribute('stroke-width', String(config.width))
  path.setAttribute('stroke-linecap', 'square')
  path.setAttribute('stroke-linejoin', 'miter')
  path.classList.add('tree-connector-line')
  path.setAttribute('d', pathData)
  svg.appendChild(path)

  return function removeConnector() {
    if (path.parentNode) path.parentNode.removeChild(path)
    if (svg && svg.children.length === 0 && svg.parentNode) {
      svg.parentNode.removeChild(svg)
    }
  }
}

/** 清空某容器内全部树连线 */
export function clearTreeConnectors(container) {
  let host = container
  if (typeof container === 'string') host = document.querySelector(container)
  if (!host) return
  const svg = host.querySelector(':scope > .tree-connector-container')
  if (svg && svg.parentNode) svg.parentNode.removeChild(svg)
}
