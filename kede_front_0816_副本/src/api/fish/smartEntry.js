import request, { getBaseHeader } from '@/utils/request_jidi'

export function saveSmartEntry(form) {
  return request({ url: '/fish/smart-entry/records', method: 'post', data: form,
    headers: { 'Content-Type': 'multipart/form-data' }, timeout: 60000 })
}
export function listSmartEntries(pageNo = 1) {
  return request({ url: '/fish/smart-entry/records', method: 'get', params: { pageNo, pageSize: 10 } })
}
export async function loadSmartImage(id, signal) {
  const base = (process.env.VUE_APP_BASE_API || '').replace(/\/+$/, '')
  const response = await fetch(`${base}/admin-api/fish/smart-entry/images/${encodeURIComponent(id)}`, {
    headers: getBaseHeader(), signal
  })
  if (!response.ok || !/^image\//i.test(response.headers.get('content-type') || '')) {
    throw new Error('图片读取失败，请确认登录状态后重试')
  }
  return response.blob()
}
