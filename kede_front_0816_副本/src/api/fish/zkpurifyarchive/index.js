import request from '@/utils/request'

// 创建入池档案
export function createPurifyArchive(data, config) {
  return request({
    url: '/fish/purify-archive/create',
    method: 'post',
    data: data,
    ...(config || {})
  })
}

// 更新入池档案
export function updatePurifyArchive(data) {
  return request({
    url: '/fish/purify-archive/update',
    method: 'put',
    data: data
  })
}

// 删除入池档案
export function deletePurifyArchive(id) {
  return request({
    url: '/fish/purify-archive/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除入池档案 */
export function deletePurifyArchiveList(ids) {
  return request({
    url: `/fish/purify-archive/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得入池档案
export function getPurifyArchive(id) {
  return request({
    url: '/fish/purify-archive/get?id=' + id,
    method: 'get'
  })
}

// 获得入池档案分页
export function getPurifyArchivePage(params) {
  return request({
    url: '/fish/purify-archive/page',
    method: 'get',
    params
  })
}
// 导出入池档案 Excel
export function exportPurifyArchiveExcel(params) {
  return request({
    url: '/fish/purify-archive/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获得风味优化统计数据
export function getPurifyStats(params) {
  return request({
    url: '/fish/purify-archive/get-stats',
    method: 'get',
    params
  })
}

// 获得记录查询分页
export function getRecordQueryPage(params) {
  return request({
    url: '/fish/purify-archive/record-query-page',
    method: 'get',
    params
  })
}