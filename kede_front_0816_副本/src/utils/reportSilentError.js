/**
 * 业务报警/任务推送失败可见性：避免空 catch 吞错
 * 控制台必记；短时合并 toast，防止刷屏
 */
import { Message } from 'element-ui'

const recent = Object.create(null)
const TOAST_COOLDOWN_MS = 15000

/**
 * @param {string} scope 模块名，如「精准投喂」「设备运行」
 * @param {any} err
 * @param {{ toast?: boolean }} [opts]
 */
export function reportSilentError(scope, err, opts) {
  const msg = (err && (err.message || err.msg)) || String(err || '未知错误')
  const key = String(scope || 'biz')
  console.error('[' + key + ']', msg, err)

  const toast = !opts || opts.toast !== false
  if (!toast) return

  const now = Date.now()
  if (recent[key] && now - recent[key] < TOAST_COOLDOWN_MS) return
  recent[key] = now
  try {
    Message.warning({
      message: key + '：同步失败（' + msg.slice(0, 80) + '）',
      duration: 3500,
      showClose: true
    })
  } catch (e) { /* Message 不可用时仅依赖 console */ }
}

/** 供 Promise.catch 使用 */
export function silentCatch(scope, opts) {
  return function (err) {
    reportSilentError(scope, err, opts)
  }
}
