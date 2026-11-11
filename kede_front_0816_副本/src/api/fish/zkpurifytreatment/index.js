import request from '@/utils/request'

// 创建处理措施记录
export function createPurifyTreatment(data) {
  return request({
    url: '/fish/purify-treatment/create',
    method: 'post',
    data: data
  })
}

// 更新处理措施记录
export function updatePurifyTreatment(data) {
  return request({
    url: '/fish/purify-treatment/update',
    method: 'put',
    data: data
  })
}

// 删除处理措施记录
export function deletePurifyTreatment(id) {
  return request({
    url: '/fish/purify-treatment/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除处理措施记录 */
export function deletePurifyTreatmentList(ids) {
  return request({
    url: `/fish/purify-treatment/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得处理措施记录
export function getPurifyTreatment(id) {
  return request({
    url: '/fish/purify-treatment/get?id=' + id,
    method: 'get'
  })
}

// 获得处理措施记录分页
export function getPurifyTreatmentPage(params) {
  return request({
    url: '/fish/purify-treatment/page',
    method: 'get',
    params
  })
}
// 导出处理措施记录 Excel
export function exportPurifyTreatmentExcel(params) {
  return request({
    url: '/fish/purify-treatment/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}