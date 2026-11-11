import { getPath } from '@/utils/ruoyi'

/** 退出后强制整页进入登录页，避免 Layout 残留样式把登录页打乱（刷新才恢复）。 */
export function goLoginPage() {
  const base = getPath('/')
  const originPath = String(base || '/').replace(/\/?$/, '/')
  window.location.replace(originPath + '?from=logout&t=' + Date.now() + '#/login')
}

export function resetLoginPageChrome() {
  if (typeof document === 'undefined') return
  const body = document.body
  if (!body) return
  body.classList.remove('hideSidebar', 'openSidebar', 'mobile', 'withoutAnimation', 'sidebarHide')
  body.style.overflow = ''
  body.style.background = ''
  const html = document.documentElement
  if (html) {
    html.style.overflow = ''
    html.style.fontSize = ''
  }
}
