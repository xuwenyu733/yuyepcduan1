import request from '@/utils/request'

// 创建优化流程记录
export function createPurifyProcess(data) {
  return request({
    url: '/fish/purify-process/create',
    method: 'post',
    data: data
  })
}

// 更新优化流程记录
export function updatePurifyProcess(data, config = {}) {
  return request({
    url: '/fish/purify-process/update',
    method: 'put',
    data: data,
    ...config
  })
}

// 删除优化流程记录
export function deletePurifyProcess(id) {
  return request({
    url: '/fish/purify-process/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除优化流程记录 */
export function deletePurifyProcessList(ids) {
  return request({
    url: `/fish/purify-process/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得优化流程记录
export function getPurifyProcess(id) {
  return request({
    url: '/fish/purify-process/get?id=' + id,
    method: 'get'
  })
}

// 获得优化流程记录分页
export function getPurifyProcessPage(params) {
  return request({
    url: '/fish/purify-process/page',
    method: 'get',
    params
  })
}
// 导出优化流程记录 Excel
export function exportPurifyProcessExcel(params) {
  return request({
    url: '/fish/purify-process/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}