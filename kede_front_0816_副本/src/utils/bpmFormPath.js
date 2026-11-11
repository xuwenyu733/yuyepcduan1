/**
 * BPM 自定义表单路由 / 视图路径规范化，避免无效 path 打开成 404。
 */
const KEBAB_TO_VIEW = [
  ['/bpm/process-instance/', '/bpm/processInstance/'],
  ['/bpm/task-assign-rule/', '/bpm/taskAssignRule/'],
  ['/bpm/process-listener/', '/bpm/processListener/'],
  ['/bpm/process-expression/', '/bpm/processExpression/']
]

/** 已知存在的自定义业务表单（webpack 字面量 require） */
export const KNOWN_BPM_VIEW_PATHS = [
  '/bpm/oa/leave/detail',
  '/bpm/oa/leave/create',
  '/bpm/oa/leave/approve',
  '/fish/zkDeviceAlarm/alarmHolder',
  '/fish/zkUseMedicine/approve',
  '/fish/zkDataRevise/approve',
  '/fish/zkAskforLeave/approve',
  '/fish/zkIntelligentDiagnosis/approve',
  '/erp/purchase/order/approve'
]

/** 审批页不能当发起页：映射到已注册的发起路由 */
const BPM_CREATE_PATH_ALIAS = {
  '/bpm/oa/leave/approve': '/bpm/oa/leave/create',
  '/bpm/oa/leave/detail': '/bpm/oa/leave/create'
}

export function normalizeBpmRoutePath(path) {
  if (path == null) return ''
  let p = String(path).trim()
  if (!p) return ''
  p = p.replace(/^#\/?/, '/')
  if (/^https?:\/\//i.test(p)) {
    try {
      p = new URL(p).pathname || ''
    } catch (e) {
      return ''
    }
  }
  if (p.charAt(0) !== '/') p = '/' + p
  p = p.replace(/\/index(?:\.vue)?$/i, '')
  p = p.replace(/\.vue$/i, '')
  if (p.length > 1) p = p.replace(/\/+$/, '')
  return p
}

export function toBpmViewRequirePath(path) {
  let p = normalizeBpmRoutePath(path)
  p = p.replace(/^\/views/, '')
  p = p.replace(/^@\/views/, '')
  for (let i = 0; i < KEBAB_TO_VIEW.length; i++) {
    const pair = KEBAB_TO_VIEW[i]
    if (p.indexOf(pair[0]) === 0) p = pair[1] + p.slice(pair[0].length)
  }
  return p
}

export function isKnownBpmView(path) {
  const p = toBpmViewRequirePath(path)
  return KNOWN_BPM_VIEW_PATHS.indexOf(p) >= 0
}

export function resolveBpmCreatePath(path) {
  const p = normalizeBpmRoutePath(path)
  return BPM_CREATE_PATH_ALIAS[p] || p
}

export function isBpmRouteAvailable(router, path) {
  const p = resolveBpmCreatePath(path)
  if (!p || p === '/') return false
  if (!router || typeof router.resolve !== 'function') return isKnownBpmView(p)
  try {
    const resolved = router.resolve({ path: p })
    const matched = (resolved.route && resolved.route.matched) || []
    if (!matched.length) return false
    return !matched.some(function (m) {
      return m.path === '*' || m.name === 'Page404' || /\/404$/.test(String(m.path || ''))
    })
  } catch (e) {
    return false
  }
}

/** 发起/查看自定义表单：有路由才跳，审批页提示从业务菜单进，避免空白 404 */
export function openBpmCustomForm(vm, rawPath) {
  const p = resolveBpmCreatePath(rawPath)
  if (!p) {
    vm.$message.warning('该流程未配置表单，无法打开')
    return false
  }
  if (!isBpmRouteAvailable(vm.$router, p)) {
    if (isKnownBpmView(rawPath)) {
      vm.$message.warning('该流程需从对应业务菜单发起，当前页无法打开表单')
    } else {
      vm.$message.error('该流程表单页面不存在（404）：' + (rawPath || ''))
    }
    return false
  }
  vm.$router.push({ path: p }).catch(function () {
    vm.$message.error('该流程表单页面不存在（404）：' + p)
  })
  return true
}
