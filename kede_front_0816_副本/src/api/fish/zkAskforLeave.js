import request from '@/utils/request_jidi'

// 创建请假
export function createZkAskforLeave(data) {
  return request({
    url: '/fish/zk-askfor-leave/create',
    method: 'post',
    data: data
  })
}

// 更新请假
export function updateZkAskforLeave(data) {
  return request({
    url: '/fish/zk-askfor-leave/update',
    method: 'put',
    data: data
  })
}

// 删除请假
export function deleteZkAskforLeave(id) {
  return request({
    url: '/fish/zk-askfor-leave/delete?id=' + id,
    method: 'delete'
  })
}

// 获得请假
export function getZkAskforLeave(id) {
  return request({
    url: '/fish/zk-askfor-leave/get?id=' + id,
    method: 'get'
  })
}

// 获得请假分页
export function getZkAskforLeavePage(query) {
  return request({
    url: '/fish/zk-askfor-leave/page',
    method: 'get',
    params: query
  })
}

// 导出请假 Excel
export function exportZkAskforLeaveExcel(query) {
  return request({
    url: '/fish/zk-askfor-leave/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
