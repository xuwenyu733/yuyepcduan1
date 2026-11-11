import request from '@/utils/request'

// 创建业务字典明细
export function createPurifyDictData(data) {
  return request({
    url: '/fish/purify-dict-data/create',
    method: 'post',
    data: data
  })
}

// 更新业务字典明细
export function updatePurifyDictData(data) {
  return request({
    url: '/fish/purify-dict-data/update',
    method: 'put',
    data: data
  })
}

// 删除业务字典明细
export function deletePurifyDictData(id) {
  return request({
    url: '/fish/purify-dict-data/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除业务字典明细 */
export function deletePurifyDictDataList(ids) {
  return request({
    url: `/fish/purify-dict-data/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得业务字典明细
export function getPurifyDictData(id) {
  return request({
    url: '/fish/purify-dict-data/get?id=' + id,
    method: 'get'
  })
}

// 获得业务字典明细分页
export function getPurifyDictDataPage(params) {
  return request({
    url: '/fish/purify-dict-data/page',
    method: 'get',
    params
  })
}
// 导出业务字典明细 Excel
export function exportPurifyDictDataExcel(params) {
  return request({
    url: '/fish/purify-dict-data/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}